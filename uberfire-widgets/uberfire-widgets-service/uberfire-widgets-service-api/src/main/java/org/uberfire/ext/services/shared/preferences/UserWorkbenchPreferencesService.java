package org.uberfire.ext.services.shared.preferences;

import org.jboss.errai.bus.server.annotations.Remote;
import java.util.Map;

@Remote
public interface UserWorkbenchPreferencesService {
    
    void saveWorkbenchPreferences( Map<String, String> preferences );

    Map<String, String> loadWorkbenchPreferences( String key );
}
