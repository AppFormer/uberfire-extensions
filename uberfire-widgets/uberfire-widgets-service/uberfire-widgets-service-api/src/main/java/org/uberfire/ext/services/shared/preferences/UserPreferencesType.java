package org.uberfire.ext.services.shared.preferences;


public enum UserPreferencesType {
    
    GRIDPREFERENCES( "datagrid-preferences" ),
    WORKBENCHSETTINGS( "workbench-settings" );
    private String ext;

    public String getExt() {
        return this.ext;
    }

    private UserPreferencesType( String ext ) {
        this.ext = ext;
    }
}
