/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.uberfire.ext.security.management.wildfly8.cli;

import org.jboss.as.controller.client.ModelControllerClient;
import org.jboss.dmr.ModelNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.sasl.RealmCallback;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;

public class Wildfly8ModelUtil {

    private static final Logger LOG = LoggerFactory.getLogger(Wildfly8ModelUtil.class);

    public static ModelControllerClient getClient(final String host, final int port, final String adminUser, final String adminPassword) throws Exception {
        return ModelControllerClient.Factory.create(
                InetAddress.getByName(host), port,
                new CallbackHandler() {
                    public void handle(Callback[] callbacks)
                            throws IOException, UnsupportedCallbackException {
                        for (Callback current : callbacks) {
                            if (current instanceof NameCallback) {
                                NameCallback ncb = (NameCallback) current;
                                ncb.setName(adminUser);
                            } else if (current instanceof PasswordCallback) {
                                PasswordCallback pcb = (PasswordCallback) current;
                                pcb.setPassword(adminPassword.toCharArray());
                            } else if (current instanceof RealmCallback) {
                                RealmCallback rcb = (RealmCallback) current;
                                rcb.setText(rcb.getDefaultText());
                            } else {
                                throw new UnsupportedCallbackException(current);
                            }
                        }
                    }
                });
    }


    public static String getPropertiesFilePath(String context, String realm, ModelControllerClient client) throws Exception {
        String result = null;
        if (client != null) {
            ModelNode operation = new ModelNode();
            operation.get("operation").set("read-resource");
            ModelNode address = operation.get("address");
            address.add("core-service", "management");
            address.add("security-realm", realm);
            address.add(context, "properties");
            try {
                ModelNode returnVal = client.execute(operation);
                if ("success".equalsIgnoreCase(returnVal.get("outcome").asString())) {
                    ModelNode resultNode = returnVal.get("result");
                    if (resultNode != null) {
                        String path = resultNode.get("path").asString();
                        String relativeTo = resultNode.get("relative-to").asString();
                        String relativeToPath = System.getProperty(relativeTo);
                        return new File(relativeToPath, path).getAbsolutePath();
                    }
                }
            } catch (Exception e) {
                LOG.error("Error reading realm using CLI commands.", e);
            } finally {
                client.close();
            }
        }
        return result;
    }
}
