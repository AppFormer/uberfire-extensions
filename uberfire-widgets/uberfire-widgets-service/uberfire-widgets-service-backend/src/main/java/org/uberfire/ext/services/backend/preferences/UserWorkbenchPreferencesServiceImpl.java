package org.uberfire.ext.services.backend.preferences;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.errai.bus.server.annotations.Service;
import org.uberfire.backend.server.UserServicesBackendImpl;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.Path;
import com.thoughtworks.xstream.XStream;
import org.uberfire.ext.services.shared.preferences.UserWorkbenchPreferencesService;
import org.uberfire.ext.services.shared.preferences.UserWorkbenchPreferences;

@Service
@ApplicationScoped
public class UserWorkbenchPreferencesServiceImpl implements
        UserWorkbenchPreferencesService {
    private static final String workbenchSettings = "workbench-settings";
    private static final String userPreferences = "user-preferences";
    @Inject
    private UserServicesBackendImpl userServicesBackend;

    @Inject
    @Named("configIO")
    private IOService ioServiceConfig;

    private XStream xs = new XStream();

    @Override
    public void saveWorkbenchPreferences(UserWorkbenchPreferences preferences,
            String identity) {
        final Path preferencesPath = userServicesBackend.buildPath(identity,
                userPreferences, workbenchSettings);
        try {
            ioServiceConfig.startBatch(preferencesPath.getFileSystem());
            ioServiceConfig.write(preferencesPath, xs.toXML(preferences));
        } finally {
            ioServiceConfig.endBatch();
        }

    }

    @Override
    public UserWorkbenchPreferences loadWorkbenchPreferences(String identity) {
        Path preferencesPath = userServicesBackend.buildPath(identity,
                userPreferences, workbenchSettings);
        try {
            if (ioServiceConfig.exists(preferencesPath)) {
                final String xml = ioServiceConfig
                        .readAllString(preferencesPath);
                return (UserWorkbenchPreferences) xs.fromXML(xml);
            }
        } catch (final Exception e) {
        }
        return null;
    }

}
