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

package org.uberfire.ext.security.management.wildfly8.properties;

import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.ext.security.management.wildfly.properties.BaseWildflyGroupPropertiesManager;
import org.uberfire.ext.security.management.wildfly.properties.BaseWildflyGroupsPropertiesManagerTest;
import org.uberfire.ext.security.management.wildfly10.properties.Wildfly10GroupPropertiesManager;

@RunWith(MockitoJUnitRunner.class)
public class Wildfly10GroupsPropertiesManagerTest extends BaseWildflyGroupsPropertiesManagerTest {

    @Override
    protected String getUsersFileClasspathLocation() {
        return "org/uberfire/ext/security/management/wildfly10/application-roles.properties";
    }

    @Override
    protected BaseWildflyGroupPropertiesManager getGroupPropertiesManager() {
        return new Wildfly10GroupPropertiesManager();
    }

}
