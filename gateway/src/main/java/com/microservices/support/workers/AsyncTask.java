package com.microservices.support.workers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Map;

public abstract class AsyncTask implements Runnable{

    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncTask.class);

    @Override
    public void run() {
        try {
            long timeBeforeTask = System.currentTimeMillis();
            runTask();
            long timeAfterTask = System.currentTimeMillis();
            LOGGER.info("Async." + this.getTaskName() + (timeAfterTask - timeBeforeTask));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public String getTaskName() {
        return "Unknown";
    }

    protected abstract void runTask() throws Exception;

}

