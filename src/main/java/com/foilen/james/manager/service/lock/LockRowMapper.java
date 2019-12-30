/*
    Email Server
    https://github.com/foilen/foilen-email-server
    Copyright (c) 2019-2019 Foilen (http://foilen.com)

    The MIT License
    http://opensource.org/licenses/MIT

 */
package com.foilen.james.manager.service.lock;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.foilen.smalltools.tools.AbstractBasics;

public class LockRowMapper extends AbstractBasics implements RowMapper<Lock> {

    @Override
    public Lock mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Lock(rs.getString("name"), //
                rs.getString("requestorId"), //
                rs.getDate("until"));
    }

}
