package com.revolut.task.controller;

import com.revolut.task.connection_pool.TransactionalBlockingConnectionPool;

public class BaseEndpoint {

    protected static final TransactionalBlockingConnectionPool connectionPool = new TransactionalBlockingConnectionPool();

}