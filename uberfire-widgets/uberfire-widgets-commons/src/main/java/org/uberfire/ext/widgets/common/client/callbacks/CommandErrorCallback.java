/*
 * Copyright 2015 JBoss, by Red Hat, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.uberfire.ext.widgets.common.client.callbacks;

import org.jboss.errai.bus.client.api.messaging.Message;
import org.uberfire.mvp.Command;

import static org.uberfire.commons.validation.PortablePreconditions.checkNotNull;

/**
 * Default Error handler for all views that support HasBusyIndicator. Now with Command support.
 */
public class CommandErrorCallback
        extends DefaultErrorCallback {

    private final Command command;

    public CommandErrorCallback(final Command command) {
        this.command = checkNotNull("command", command);
    }

    @Override
    public boolean error(final Message message,
                         final Throwable throwable) {
        command.execute();
        return super.error(message, throwable);
    }
}
