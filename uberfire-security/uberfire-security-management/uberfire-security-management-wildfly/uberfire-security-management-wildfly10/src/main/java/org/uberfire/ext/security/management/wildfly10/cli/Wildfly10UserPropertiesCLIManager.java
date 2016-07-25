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

package org.uberfire.ext.security.management.wildfly10.cli;

import org.jboss.as.controller.client.ModelControllerClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.ext.security.management.api.ContextualManager;
import org.uberfire.ext.security.management.api.UserManager;
import org.uberfire.ext.security.management.api.UserSystemManager;
import org.uberfire.ext.security.management.wildfly.cli.BaseWildflyGroupPropertiesCLIManager;
import org.uberfire.ext.security.management.wildfly.cli.BaseWildflyUserPropertiesCLIManager;
import org.uberfire.ext.security.management.wildfly.properties.BaseWildflyGroupPropertiesManager;
import org.uberfire.ext.security.management.wildfly.properties.BaseWildflyUserPropertiesManager;
import org.uberfire.ext.security.management.wildfly10.properties.Wildfly10UserPropertiesManager;

import java.util.Map;

/**
 * <p>Users manager service provider implementation for JBoss Wildfly.</p>
 * <p>It wraps the Wildfly users manager based on properties file, but instead of the need to specify the path for the properties files, its absolute path discovery is automatically handled by using to the administration API for the server.</p>
 */
public class Wildfly10UserPropertiesCLIManager extends BaseWildflyUserPropertiesCLIManager implements UserManager, ContextualManager {

    @Override
    protected BaseWildflyUserPropertiesManager getWildflyUserPropertiesManager(Map<String, String> arguments, final UserSystemManager usManager) {
        return new Wildfly10UserPropertiesManager(arguments) {
                @Override
                public synchronized BaseWildflyGroupPropertiesManager getGroupsPropertiesManager() {
                    try {
                        return ((BaseWildflyGroupPropertiesCLIManager) usManager.groups()).getGroupsPropertiesManager();
                    } catch (ClassCastException e) {
                        return super.getGroupsPropertiesManager();
                    }
                }
            };
    }

    @Override
    protected String getPropertiesFilePath(String context) throws Exception {
        final ModelControllerClient client = Wildfly10ModelUtil.getClient(host, port, adminUser, adminPassword);
        return Wildfly10ModelUtil.getPropertiesFilePath(context, realm, client);
    }

}
