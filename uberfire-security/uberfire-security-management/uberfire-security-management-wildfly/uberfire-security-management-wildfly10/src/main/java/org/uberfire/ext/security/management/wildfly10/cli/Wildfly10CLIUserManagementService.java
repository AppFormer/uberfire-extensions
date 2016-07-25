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

import org.uberfire.ext.security.management.api.GroupManager;
import org.uberfire.ext.security.management.api.UserManager;
import org.uberfire.ext.security.management.service.AbstractUserManagementService;
import org.uberfire.ext.security.management.wildfly.WildflyRoleManager;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

/**
 * <p>The Wildfly10/EAP7 management service beans for the CLI approach.</p>
 */
@Dependent
@Named(value = "WildflyCLIUserManagementService")
public class Wildfly10CLIUserManagementService extends AbstractUserManagementService {

    private final Wildfly10UserPropertiesCLIManager userManager;
    private final Wildfly10GroupPropertiesCLIManager groupManager;

    @Inject
    public Wildfly10CLIUserManagementService(final Wildfly10UserPropertiesCLIManager userManager,
                                             final Wildfly10GroupPropertiesCLIManager groupManager,
                                             final WildflyRoleManager roleManager) {
        super(roleManager);
        this.userManager = userManager;
        this.groupManager = groupManager;
    }

    @Override
    public UserManager users() {
        return userManager;
    }

    @Override
    public GroupManager groups() {
        return groupManager;
    }

}
