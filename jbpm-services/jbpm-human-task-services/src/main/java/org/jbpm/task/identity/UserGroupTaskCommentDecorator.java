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
package org.jbpm.task.identity;

import java.util.List;
import javax.decorator.Decorator;
import javax.decorator.Delegate;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import org.drools.core.util.StringUtils;
import org.jbpm.task.Comment;
import org.jbpm.task.User;
import org.jbpm.task.api.TaskAttachmentService;
import org.jbpm.task.api.TaskCommentService;

/**
 *
 */
@Decorator
public class UserGroupTaskCommentDecorator implements TaskCommentService {

    @Inject
    @Delegate
    private TaskCommentService commentService;
    @Inject
    private UserGroupCallback userGroupCallback;
    @Inject
    private EntityManager em;

    public long addComment(long taskId, Comment comment) {
        doCallbackOperationForComment(comment);
        long commentId = commentService.addComment(taskId, comment);
        return commentId;
    }

    public void deleteComment(long taskId, long commentId) {
        commentService.deleteComment(taskId, commentId);
    }

    public List<Comment> getAllCommentsByTaskId(long taskId) {
        return commentService.getAllCommentsByTaskId(taskId);
    }

    public Comment getCommentById(long commentId) {
        return commentService.getCommentById(commentId);
    }

    private void doCallbackOperationForComment(Comment comment) {
        if (comment != null) {
            if (comment.getAddedBy() != null) {
                doCallbackUserOperation(comment.getAddedBy().getId());
            }
        }
    }

    private boolean doCallbackUserOperation(String userId) {

        if (userId != null && userGroupCallback.existsUser(userId)) {
            addUserFromCallbackOperation(userId);
            return true;
        }
        return false;

    }

    private void addUserFromCallbackOperation(String userId) {
        try {
            boolean userExists = em.find(User.class, userId) != null;
            if (!StringUtils.isEmpty(userId) && !userExists) {
                User user = new User(userId);
                em.persist(user);
            }
        } catch (Throwable t) {
            //logger.log(Level.SEVERE, "Unable to add user " + userId);
        }
    }
}
