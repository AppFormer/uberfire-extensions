/*
 * Copyright 2014 JBoss by Red Hat.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.uberfire.ext.services.backend.preferences;

import javax.inject.Inject;
import javax.inject.Named;

import com.thoughtworks.xstream.XStream;
import org.jboss.errai.bus.server.annotations.Service;
import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.backend.server.UserServicesBackendImpl;
import org.uberfire.ext.services.shared.preferences.GridPreferencesStore;
import org.uberfire.ext.services.shared.preferences.UserDataGridPreferencesService;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.Path;

@Service
public class UserDataGridPreferencesServiceImpl implements UserDataGridPreferencesService {

    @Inject
    private UserServicesBackendImpl userServicesBackend;

    @Inject
    private User identity;

    @Inject
    @Named("configIO")
    private IOService ioServiceConfig;

    private XStream xs = new XStream();

    @Override
    public synchronized void saveGridPreferences( final GridPreferencesStore preferences ) {
        final Path preferencesPath = userServicesBackend.buildPath( identity.getIdentifier(), "datagrid-preferences", preferences.getGlobalPreferences().getKey() );
        try {
            ioServiceConfig.startBatch( preferencesPath.getFileSystem() );
            ioServiceConfig.write( preferencesPath, xs.toXML( preferences ) );
        } finally {
            ioServiceConfig.endBatch();
        }
    }

    @Override
    public GridPreferencesStore loadGridPreferences( String key ) {
        Path preferencesPath = userServicesBackend.buildPath( identity.getIdentifier(), "datagrid-preferences", key );
        try {
            if ( ioServiceConfig.exists( preferencesPath ) ) {
                final String xml = ioServiceConfig.readAllString( preferencesPath );
                return (GridPreferencesStore) xs.fromXML( xml );
            }
        } catch ( final Exception e ) {
        }
        return null;
    }

}
