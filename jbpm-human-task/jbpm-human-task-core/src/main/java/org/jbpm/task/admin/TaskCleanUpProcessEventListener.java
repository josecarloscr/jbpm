/*
 * Copyright 2012 JBoss by Red Hat.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.task.admin;

import java.util.List;
import org.drools.event.DefaultProcessEventListener;
import org.kie.event.process.ProcessCompletedEvent;
import org.jbpm.task.query.TaskSummary;

/**
 *
 * @author salaboy
 */
public class TaskCleanUpProcessEventListener extends DefaultProcessEventListener {

    private TasksAdmin admin;

    public TaskCleanUpProcessEventListener(TasksAdmin admin) {
        this.admin = admin;
    }
    

    @Override
    public void afterProcessCompleted(ProcessCompletedEvent event) {
        
        List<TaskSummary> completedTasksByProcessId = admin.getCompletedTasksByProcessId(event.getProcessInstance().getId());
        admin.archiveTasks(completedTasksByProcessId);
        admin.removeTasks(completedTasksByProcessId);
        
    }
}
