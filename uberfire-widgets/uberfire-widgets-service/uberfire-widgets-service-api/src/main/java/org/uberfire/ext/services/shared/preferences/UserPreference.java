package org.uberfire.ext.services.shared.preferences;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class UserPreference {
    UserPreferencesType type;
    
    String preferenceKey ;
    
    public UserPreferencesType getType() {
        return type;
    }
    
    public void setType(UserPreferencesType type) {
        this.type = type;
    }
    
    public void setPreferenceKey(String preferenceKey){
        this.preferenceKey = preferenceKey;
    }
    
    public String getPreferenceKey(){
        return this.preferenceKey;
    }
}
