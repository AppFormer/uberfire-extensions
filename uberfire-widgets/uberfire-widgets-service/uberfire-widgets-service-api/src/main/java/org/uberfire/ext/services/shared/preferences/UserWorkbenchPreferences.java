package org.uberfire.ext.services.shared.preferences;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class UserWorkbenchPreferences {
    private String language;
    private String viewMode;

    public UserWorkbenchPreferences() {

    }

    public UserWorkbenchPreferences(String language, String viewMode) {
        super();
        this.language = language;
        this.viewMode = viewMode;
    }

    public String getLanguage() {
        return language;
    }

    public String getViewMode() {
        return viewMode;
    }

}
