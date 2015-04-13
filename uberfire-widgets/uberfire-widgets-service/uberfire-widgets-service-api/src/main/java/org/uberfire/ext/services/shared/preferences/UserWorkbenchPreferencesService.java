package org.uberfire.ext.services.shared.preferences;

import org.jboss.errai.bus.server.annotations.Remote;

@Remote
public interface UserWorkbenchPreferencesService {
    void saveWorkbenchPreferences(UserWorkbenchPreferences preferences,
            String identity);

    UserWorkbenchPreferences loadWorkbenchPreferences(String identity);
}
