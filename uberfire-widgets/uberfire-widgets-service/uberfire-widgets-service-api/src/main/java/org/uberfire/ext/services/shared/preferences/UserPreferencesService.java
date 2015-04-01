package org.uberfire.ext.services.shared.preferences;

import org.jboss.errai.bus.server.annotations.Remote;

@Remote
public interface UserPreferencesService {

    void saveUserPreferences( UserPreference preferences );

    UserPreference loadUserPreferences( String key, UserPreferencesType type );

    UserPreference loadUserPreferences( UserPreference preferences );
}
