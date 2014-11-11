package org.uberfire.ext.plugin.type;

import javax.enterprise.context.ApplicationScoped;

import org.uberfire.ext.plugin.model.PluginType;
import org.uberfire.workbench.type.ResourceTypeDefinition;

@ApplicationScoped
public class EditorPluginResourceTypeDefinition extends BasePluginResourceTypeDefinition implements ResourceTypeDefinition {

    @Override
    public String getShortName() {
        return "editor plugin";
    }

    @Override
    public String getDescription() {
        return "Editor plugin";
    }

    @Override
    public String getSuffix() {
        return "/" + PluginType.EDITOR.toString().toLowerCase() + ".plugin";
    }

}
