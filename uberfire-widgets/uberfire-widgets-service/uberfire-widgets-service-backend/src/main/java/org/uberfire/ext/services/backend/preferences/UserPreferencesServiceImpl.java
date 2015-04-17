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
import org.uberfire.java.nio.file.Path;
import com.thoughtworks.xstream.XStream;

@Service
public class UserPreferencesServiceImpl implements UserPreferencesService {

    @Inject
    private UserServicesBackendImpl userServicesBackend;

    @Inject
    private User identity;

    @Inject
    @Named("configIO")
    private IOService ioServiceConfig;

    private XStream xs = new XStream();

    @Override
    public void saveUserPreferences( UserPreference preferences ) {
        final Path preferencesPath = userServicesBackend.buildPath( identity.getIdentifier(), preferences.getType().getExt(), preferences.getPreferenceKey() );
        saveUserPreferences( preferences, preferencesPath );
    }

    @Override
    public UserPreference loadUserPreferences( String key, UserPreferencesType type ) {
        final Path preferencesPath = userServicesBackend.buildPath( identity.getIdentifier(), type.getExt(), key );
        return loadUserPreferences( preferencesPath );
    }

    private void saveUserPreferences( UserPreference preferences, Path path ) {

        try {
            ioServiceConfig.startBatch( path.getFileSystem() );
            ioServiceConfig.write( path, xs.toXML( preferences ) );
        } catch ( final Exception e ) {
            throw new RuntimeException( e );
        } finally {
            ioServiceConfig.endBatch();
        }
    }

    private UserPreference loadUserPreferences( Path path ) {
        try {
            if ( ioServiceConfig.exists( path ) ) {
                final String xml = ioServiceConfig.readAllString( path );
                return (UserPreference) xs.fromXML( xml );
            }
        } catch ( final Exception e ) {
            throw new RuntimeException( e );
        }
        return null;
    }

    @Override
    public UserPreference loadUserPreferences( UserPreference preferences ) {
        final Path preferencesPath = userServicesBackend.buildPath( identity.getIdentifier(), preferences.getType().getExt(), preferences.getPreferenceKey() );
        return loadUserPreferences( preferencesPath );
    }
}
