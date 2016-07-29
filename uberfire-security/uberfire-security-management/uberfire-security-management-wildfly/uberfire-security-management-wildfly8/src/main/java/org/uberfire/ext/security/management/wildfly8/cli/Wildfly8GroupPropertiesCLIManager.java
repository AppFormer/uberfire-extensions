/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.security.management.wildfly8.cli;

import org.jboss.as.controller.client.ModelControllerClient;
import org.uberfire.ext.security.management.api.ContextualManager;
import org.uberfire.ext.security.management.api.GroupManager;
import org.uberfire.ext.security.management.wildfly.cli.BaseWildflyGroupPropertiesCLIManager;
import org.uberfire.ext.security.management.wildfly.properties.BaseWildflyGroupPropertiesManager;
import org.uberfire.ext.security.management.wildfly8.properties.Wildfly8GroupPropertiesManager;

import java.util.Map;

/**
 * <p>Groups manager service provider implementation for JBoss Wildfly.</p>
 * <p>It wraps the Wildfly groups manager based on properties file, but instead of the need to specify the path for the properties files, its absolute path discovery is automatically handled by using to the administration API for the server.</p>
 */
public class Wildfly8GroupPropertiesCLIManager extends BaseWildflyGroupPropertiesCLIManager implements GroupManager, ContextualManager {
    @Override
    protected BaseWildflyGroupPropertiesManager createWildflyGroupPropertiesManager(Map<String, String> arguments) {
        return new Wildfly8GroupPropertiesManager(arguments);
    }

    @Override
    protected String getPropertiesFilePath(String context) throws Exception {
        final ModelControllerClient client = Wildfly8ModelUtil.getClient(host, port, adminUser, adminPassword);
        return Wildfly8ModelUtil.getPropertiesFilePath(context, realm, client);
    }

}
