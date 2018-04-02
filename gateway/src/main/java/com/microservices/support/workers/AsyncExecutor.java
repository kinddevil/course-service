package com.microservices.support.workers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AsyncExecutor {
    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncExecutor.class);

    private static volatile AsyncExecutor instance;

    private ExecutorService executorService;

    public AsyncExecutor() {
        executorService = Executors.newCachedThreadPool();
    }

//    public static AsyncExecutor getInstance() {
//        if (instance == null) {
//            synchronized (AsyncExecutor.class) {
//                if (instance == null) {
//                    instance = new AsyncExecutor();
//                }
//            }
//        }
//        return instance;
//    }

    public void submit(AsyncTask task) {
        executorService.submit(task);
    }

}
