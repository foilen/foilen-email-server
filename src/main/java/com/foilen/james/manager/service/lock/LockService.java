package com.foilen.james.manager.service.lock;

public interface LockService {

    void executeIfGotLock(String lockName, Runnable runnable);

    void init();

}
