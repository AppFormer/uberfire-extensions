/*
 * Copyright 2015 JBoss Inc
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

import org.jboss.errai.bus.server.annotations.Service;
import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.backend.server.UserServicesBackendImpl;
import org.uberfire.ext.services.shared.preferences.UserPreference;
import org.uberfire.ext.services.shared.preferences.UserPreferencesService;
import org.uberfire.ext.services.shared.preferences.UserPreferencesType;
import org.uberfire.io.IOService;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.preferences.PreferenceStore;

@Service
public class UserPreferencesServiceImpl implements UserPreferencesService {

    @Inject
    private UserServicesBackendImpl userServicesBackend;

    @Inject
    private User identity;

    @Inject
    @Named("configIO")
    private IOService ioServiceConfig;

    @Inject
    private PreferenceStore preferenceStore;

    @Override
    public void saveUserPreferences( final UserPreference preferences ) {
        preferenceStore.put( buildKey( preferences ), preferences );
    }

    @Override
    public UserPreference loadUserPreferences( final String key,
                                               final UserPreferencesType type ) {
        // We're just ignoring type now - deprecation should follow
        final UserPreference[] result = new UserPreference[ 1 ];
        preferenceStore.get( key, new ParameterizedCommand<UserPreference>() {
            @Override
            public void execute( final UserPreference value ) {
                result[ 0 ] = value;
            }
        } );

        return result[ 0 ];
    }

    @Override
    public UserPreference loadUserPreferences( final UserPreference preferences ) {
        return loadUserPreferences( buildKey( preferences ), preferences.getType() );
    }

    private String buildKey( final UserPreference preference ) {
        return preference.getPreferenceKey() + "." + preference.getType().getExt();
    }

}
