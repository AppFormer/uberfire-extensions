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

package org.uberfire.ext.security.management.wildfly.properties;

import org.uberfire.commons.config.ConfigProperties;

/**
 * <p>Base class for JBoss WildFly security management when using realms based on properties files.</p>
 * <p>Based on JBoss WildFly controller client API & Util classes.</p>
 */
public abstract class BaseWildflyPropertiesManager {

    public static final String DEFAULT_REALM = "ApplicationRealm";

    protected String realm = DEFAULT_REALM;

    protected void loadConfig( final ConfigProperties config ) {
        final ConfigProperties.ConfigProperty realm = config.get("org.uberfire.ext.security.management.wildfly.properties.realm", DEFAULT_REALM);
        this.realm = realm.getValue();
    }

    protected abstract String generateHashPassword(final String username, final String realm, final String password);

    protected static boolean isConfigPropertySet(ConfigProperties.ConfigProperty property) {
        if (property == null) {
            return false;
        }
        String value = property.getValue();
        return !isEmpty(value);
    }

    protected static boolean isEmpty(String s) {
        return s == null || s.trim().length() == 0;
    }

}
