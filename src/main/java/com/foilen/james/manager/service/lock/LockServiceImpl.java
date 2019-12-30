/*
    Email Server
    https://github.com/foilen/foilen-email-server
    Copyright (c) 2019-2019 Foilen (http://foilen.com)

    The MIT License
    http://opensource.org/licenses/MIT

 */
package com.foilen.james.manager.service.lock;

import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import com.foilen.smalltools.tools.AbstractBasics;
import com.foilen.smalltools.tools.DateTools;
import com.foilen.smalltools.tools.HeartbeatTools;
import com.foilen.smalltools.tools.ResourceTools;
import com.foilen.smalltools.tools.SecureRandomTools;
import com.foilen.smalltools.tools.StringTools;
import com.foilen.smalltools.tools.ThreadTools;

public class LockServiceImpl extends AbstractBasics implements LockService {

    private static final Logger logger = LoggerFactory.getLogger(LockServiceImpl.class);

    private static final String LOCKER_ID = SecureRandomTools.randomHexString(100);
    private static final LockRowMapper ROW_MAPPER = new LockRowMapper();

    private static final int lockForSeconds = 60;
    private static final int pingLockEverySeconds = 10;

    static {
        logger.info("Current locker id: {}", LOCKER_ID);
    }

    private JdbcTemplate jdbcTemplate;

    public LockServiceImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void executeIfGotLock(String lockName, Runnable runnable) {
        if (tryGettingLock(lockName)) {
            HeartbeatTools.execute(pingLockEverySeconds * 1000L, () -> {
                ping(lockName);
            }, () -> {
                try {
                    runnable.run();
                } finally {
                    releaseLock(lockName);
                }
            });
        }
    }

    private Lock getLock(String lockName) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM FOILEN_LOCK WHERE name=?", new Object[] { lockName }, ROW_MAPPER);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public void init() {
        jdbcTemplate.execute(ResourceTools.getResourceAsString("lock.sql", getClass()));
    }

    private void ping(String lockName) {
        jdbcTemplate.update("UPDATE FOILEN_LOCK SET until=? WHERE name=? AND requestorId=?", //
                DateTools.addDate(Calendar.SECOND, lockForSeconds), //
                lockName, LOCKER_ID);
    }

    private void releaseLock(String lockName) {
        jdbcTemplate.update("DELETE FROM FOILEN_LOCK WHERE name=? AND requestorId=?", lockName, LOCKER_ID);
    }

    private boolean tryGettingLock(String lockName) {

        // Check if lock is present and already used by someone else
        logger.debug("Getting lock {}", lockName);
        Lock lock = getLock(lockName);
        if (lock == null) {
            logger.debug("Lock {} does not exist. Creating it", lockName);

            jdbcTemplate.update("INSERT INTO FOILEN_LOCK(name,requestorId,until) VALUES (?,?,?)", //
                    lockName, //
                    LOCKER_ID, //
                    DateTools.addDate(Calendar.SECOND, lockForSeconds));

        } else {
            if (lock.getUntil().getTime() < System.currentTimeMillis() + 10000) {
                logger.debug("Lock {} is expired on {}", lockName, DateTools.formatFull(lock.getUntil()));

                jdbcTemplate.update("UPDATE FOILEN_LOCK SET until=?, requestorId=? WHERE name=?", //
                        DateTools.addDate(Calendar.SECOND, lockForSeconds), LOCKER_ID, //
                        lockName);

            } else if (StringTools.safeEquals(LOCKER_ID, lock.getRequestorId())) {
                logger.debug("Lock {} is valid for us until {}", lockName, DateTools.formatFull(lock.getUntil()));
                ping(lockName);
                return true;
            } else {
                logger.debug("Lock {} valid until {} and is holded by {}", lockName, DateTools.formatFull(lock.getUntil()), lock.getRequestorId());
                return false;
            }
        }

        // Wait 5 seconds
        ThreadTools.sleep(5000);

        // Get it and check
        lock = getLock(lockName);
        if (StringTools.safeEquals(LOCKER_ID, lock.getRequestorId())) {
            logger.debug("Lock {} is valid for us until {}", lockName, DateTools.formatFull(lock.getUntil()));
            return true;
        } else {
            logger.debug("Lock {} valid until {} and is holded by {}", lockName, DateTools.formatFull(lock.getUntil()), lock.getRequestorId());
            return false;
        }

    }

}
