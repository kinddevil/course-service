package com.microservices.support.workers.tasks;

import com.microservices.support.workers.AsyncTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class UserActionLogTask extends AsyncTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserActionLogTask.class);

    public UserActionLogTask() {
        // TODO Init action with user info
    }

    @Override
    public String getTaskName() {
        return "LogUserAction";
    }

    @Override
    protected void runTask() throws Exception {
        // TODO save to user_action table
        LOGGER.info("this is log");
    }

}
