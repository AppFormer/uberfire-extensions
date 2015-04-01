package org.uberfire.ext.services.backend.preferences;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

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
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

@Service
public class UserPreferencesServiceImpl implements UserPreferencesService {

    @Inject
    private UserServicesBackendImpl userServicesBackend;

    @Inject
    private User identity;

    @Inject
    @Named("configIO")
    private IOService ioServiceConfig;

    @Override
    public void saveUserPreferences( UserPreference preferences ) {
        final Path preferencesPath = userServicesBackend.buildPath( identity.getIdentifier() , preferences.getType().getExt() , preferences.getPreferenceKey() );
        saveUserPreferences( preferences , preferencesPath );
    }

    @Override
    public UserPreference loadUserPreferences( String key , UserPreferencesType type ) {
        final Path preferencesPath = userServicesBackend.buildPath( identity.getIdentifier() , type.getExt() , key );
        return loadUserPreferences( preferencesPath );
    }

    private void saveUserPreferences( UserPreference preferences , Path path ) {
        XStream xs = new XStream();
        xs.registerConverter( new MapConverter() );

        try {
            ioServiceConfig.startBatch( path.getFileSystem() );
            ioServiceConfig.write( path , xs.toXML( preferences ) );
        } catch (final Exception e) {
            throw new RuntimeException( e );
        } finally {
            ioServiceConfig.endBatch();
        }
    }

    private UserPreference loadUserPreferences( Path path ) {
        try { 
            if (ioServiceConfig.exists( path )) {
                XStream xs = new XStream();
                xs.registerConverter( new MapConverter() );
                final String xml = ioServiceConfig.readAllString( path );
                return (UserPreference) xs.fromXML( xml );
            }
        } catch (final Exception e) {
            throw new RuntimeException( e );
        }
        return null;
    }

    @Override
    public UserPreference loadUserPreferences( UserPreference preferences ) {
        final Path preferencesPath = userServicesBackend.buildPath( identity.getIdentifier() , preferences.getType().getExt() , preferences.getPreferenceKey() );
        return loadUserPreferences( preferencesPath );
    }

    private class MapConverter implements Converter {

        public boolean canConvert( Class clazz ) {
            return AbstractMap.class.isAssignableFrom( clazz );
        }

        public void marshal( Object value , HierarchicalStreamWriter writer , MarshallingContext context ) {
            AbstractMap<String, String> map = (AbstractMap<String, String>) value;
            for (Map.Entry<String, String> entry : map.entrySet()) {
                writer.startNode( "property" );
                writer.addAttribute( "name" , entry.getKey().toString() );
                writer.addAttribute( "value" , entry.getValue().toString() );
                writer.endNode();
            }
        }

        public Object unmarshal( HierarchicalStreamReader reader , UnmarshallingContext context ) {
            Map<String, String> map = new HashMap<String, String>();
            while (reader.hasMoreChildren()) {
                reader.moveDown();
                map.put( reader.getAttribute( "name" ) , reader.getAttribute( "value" ) );
                reader.moveUp();
            }
            return map;
        }
    }
}
