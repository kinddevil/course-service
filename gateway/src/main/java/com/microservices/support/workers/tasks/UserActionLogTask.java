package com.microservices.support.workers.tasks;

import com.microservices.support.domain.UserAction;
import com.microservices.support.repository.UserActionRepository;
import com.microservices.support.workers.AsyncTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Scope("prototype")
public class UserActionLogTask extends AsyncTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserActionLogTask.class);

    private UserAction userAction;

    @Autowired
    private UserActionRepository userActionRepository;

    public UserActionLogTask(UserAction action) {
        this.userAction = action;
    }

    public UserActionLogTask() {
    }

    public UserActionLogTask BuildUser(UserAction action) {
        this.userAction = action;
        return this;
    }

    @Override
    public String getTaskName() {
        return "LogUserAction";
    }

    @Override
    protected void runTask() throws Exception {
        LOGGER.info("save user action");
        userActionRepository.save(this.userAction);
    }

}
