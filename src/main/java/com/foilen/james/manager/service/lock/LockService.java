/*
    Email Server
    https://github.com/foilen/foilen-email-server
    Copyright (c) 2019-2023 Foilen (https://foilen.com)

    The MIT License
    http://opensource.org/licenses/MIT

 */
package com.foilen.james.manager.service.lock;

public interface LockService {

    void executeIfGotLock(String lockName, Runnable runnable);

    void init();

}
