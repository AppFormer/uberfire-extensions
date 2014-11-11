package org.uberfire.ext.plugin.client.type;

import javax.enterprise.context.ApplicationScoped;

import com.github.gwtbootstrap.client.ui.Icon;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.user.client.ui.IsWidget;
import org.uberfire.ext.plugin.type.ScreenPluginResourceTypeDefinition;
import org.uberfire.client.workbench.type.ClientResourceType;

@ApplicationScoped
public class ScreenPluginResourceType
        extends ScreenPluginResourceTypeDefinition
        implements ClientResourceType {

    @Override
    public IsWidget getIcon() {
        return new Icon( IconType.DESKTOP );
    }
}
