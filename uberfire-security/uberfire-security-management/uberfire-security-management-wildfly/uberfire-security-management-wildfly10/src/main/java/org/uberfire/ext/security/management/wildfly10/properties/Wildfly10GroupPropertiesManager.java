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

package org.uberfire.ext.security.management.wildfly10.properties;

import org.jboss.as.domain.management.security.PropertiesFileLoader;
import org.jboss.errai.security.shared.api.Group;
import org.jboss.sasl.util.UsernamePasswordHashUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.commons.config.ConfigProperties;
import org.uberfire.ext.security.management.api.*;
import org.uberfire.ext.security.management.api.exception.SecurityManagementException;
import org.uberfire.ext.security.management.search.GroupsIdentifierRuntimeSearchEngine;
import org.uberfire.ext.security.management.search.IdentifierRuntimeSearchEngine;
import org.uberfire.ext.security.management.wildfly.properties.BaseWildflyGroupPropertiesManager;
import org.uberfire.ext.security.management.wildfly.properties.WildflyPropertiesFileLoader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>Groups manager service provider implementation for Wildfly, when using default realm based on properties files.</p>
 */
public class Wildfly10GroupPropertiesManager extends BaseWildflyGroupPropertiesManager implements GroupManager, ContextualManager {

    private static final Logger LOG = LoggerFactory.getLogger(Wildfly10GroupPropertiesManager.class);

    public Wildfly10GroupPropertiesManager() {
         super();
    }

    public Wildfly10GroupPropertiesManager(final Map<String, String> gitPrefs) {
        super( gitPrefs );
    }

    public Wildfly10GroupPropertiesManager(final ConfigProperties gitPrefs) {
        super( gitPrefs );
    }

    @Override
    protected WildflyPropertiesFileLoader getFileLoader(String filePath) {
        final File propertiesFile = new File(filePath);
        if (!propertiesFile.exists()) throw new RuntimeException("Cannot load roles/groups properties file from '" + filePath + "'.");
        String path = null;
        try {
            path = propertiesFile.getCanonicalPath();
        } catch(Exception e) {
            throw new RuntimeException(e);
        }


        final PropertiesFileLoader propertiesFileLoader = new PropertiesFileLoader(path, null) {
            public final Pattern PROPERTY_PATTERN_NO_EMPTY_VALUE = Pattern.compile("#?+((?:[,.\\-@/a-zA-Z0-9]++|(?:\\\\[=\\\\])++)++)=(.*)");

            @Override
            public synchronized void persistProperties() throws IOException {
                beginPersistence();
                List<String> content = readFile(propertiesFile);
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(propertiesFile), Charset.forName("UTF-8")));
                try {
                    for (String line : content) {
                        String trimmed = line.trim();
                        if (trimmed.length() == 0) {
                            bw.newLine();
                        } else {
                            Matcher matcher = PROPERTY_PATTERN_NO_EMPTY_VALUE.matcher(trimmed);
                            if (!matcher.matches()) {
                                write(bw, line, true);
                            }
                        }
                    }
                    endPersistence(bw);
                } finally {
                    safeClose(bw);
                }
            }
        };
        try {
            propertiesFileLoader.start(null);
        } catch (Exception e) {
            LOG.error("Error getting properties file.", e);
            throw new SecurityManagementException(e);
        }
        return new WildflyPropertiesFileLoader() {

            @Override
            public Properties getProperties() throws IOException {
                return propertiesFileLoader.getProperties();
            }

            @Override
            public void persistProperties() throws IOException {
                propertiesFileLoader.persistProperties();
            }

            @Override
            public void stop() {
                propertiesFileLoader.stop(null);
            }
        };
    }

    @Override
    protected String generateHashPassword(String username, String realm, String password) {
        String result = null;
        try {
            result = new UsernamePasswordHashUtil().generateHashedHexURP(
                    username,
                    realm,
                    password.toCharArray());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return result;
    }

}
