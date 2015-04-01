package org.uberfire.ext.services.shared.preferences;

import java.util.HashMap;
import java.util.Map;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class UserWorkbenchPreferences extends UserPreference {

    private String language;
    private Map<String, String> perspectiveViewMode = new HashMap<String, String>();

    public UserWorkbenchPreferences() {
    }

    public UserWorkbenchPreferences( String language ) {
        super();
        super.type = UserPreferencesType.WORKBENCHSETTINGS;
        super.preferenceKey = "settings";
        this.language = language;

    }

    public Map<String, String> getPerspectiveViewMode() {
        return perspectiveViewMode;
    }

    public void setPerspectiveViewMode( Map<String, String> perspectiveViewMode ) {
        this.perspectiveViewMode = perspectiveViewMode;
    }

    public String getLanguage() {
        return language;
    }

    public String getViewMode( String perspective ) {
        return perspectiveViewMode.get( perspective );
    }

    public void setViewMode( String perspective , String ViewMode ) {
        perspectiveViewMode.put( perspective , ViewMode );
    }
}
