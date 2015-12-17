/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.security.management.api.exception;

import org.jboss.errai.common.client.api.annotations.Portable;

/**
 * <p>Exception for user system management when the entity to create already exists.</p>
 *
 * @since 0.8.0
 */
@SuppressWarnings("serial")
@Portable
public class EntityAlreadyExistsException extends SecurityManagementException {

    private String identifier;

    public EntityAlreadyExistsException() {
    }

    public EntityAlreadyExistsException(String message, String identifier) {
        super(message);
        this.identifier = identifier;
    }

    public EntityAlreadyExistsException(String identifier) {
        this.identifier = identifier;
    }

    @Override
    public String getMessage() {
        String m = super.getMessage();
        if (identifier != null) {
            return m + ": " + identifier;
        }
        return m;
    }
}
