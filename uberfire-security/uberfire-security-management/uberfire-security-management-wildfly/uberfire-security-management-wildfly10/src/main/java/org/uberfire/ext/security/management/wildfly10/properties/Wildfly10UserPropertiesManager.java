/*b
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

import org.jboss.as.domain.management.security.UserPropertiesFileLoader;
import org.jboss.sasl.util.UsernamePasswordHashUtil;
import org.uberfire.commons.config.ConfigProperties;
import org.uberfire.ext.security.management.api.ContextualManager;
import org.uberfire.ext.security.management.api.UserManager;
import org.uberfire.ext.security.management.wildfly.properties.BaseWildflyUserPropertiesManager;
import org.uberfire.ext.security.management.wildfly.properties.WildflyPropertiesFileLoader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>Users manager service provider implementation for JBoss Wildfly, when using default realm based on properties files.</p>
 */
public class Wildfly10UserPropertiesManager extends BaseWildflyUserPropertiesManager implements UserManager, ContextualManager {

    public Wildfly10UserPropertiesManager() {
        this( new ConfigProperties( System.getProperties() ) );
    }

    public Wildfly10UserPropertiesManager(final Map<String, String> gitPrefs) {
        this( new ConfigProperties( gitPrefs ) );
    }

    public Wildfly10UserPropertiesManager(final ConfigProperties gitPrefs) {
        super(gitPrefs);
    }

    protected WildflyPropertiesFileLoader buildFileLoader(String usersFilePath) throws Exception {
        File usersFile = new File(usersFilePath);
        if (!usersFile.exists()) throw new RuntimeException("Properties file for users not found at '" + usersFilePath + "'.");


        final UserPropertiesFileLoader usersFileLoader = new UserPropertiesFileLoader(usersFile.getAbsolutePath(), null) {
            public final Pattern PROPERTY_PATTERN_NO_EMPTY_VALUE = Pattern.compile("#?+((?:[,.\\-@/a-zA-Z0-9]++|(?:\\\\[=\\\\])++)++)=(.*)");

            // TODO Remove this when fixed in WF. Bug: Deleted properties are still persisted to properties file
            // as the line still present in the original property file is copied during persistProperties.
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
            usersFileLoader.start(null);
        } catch (Exception e) {
            throw new IOException(e);
        }
        return new WildflyPropertiesFileLoader() {
            @Override
            public Properties getProperties() throws IOException {
                return usersFileLoader.getProperties();
            }

            @Override
            public void persistProperties() throws IOException {
                usersFileLoader.persistProperties();
            }

            @Override
            public void stop() {
                usersFileLoader.stop(null);
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
