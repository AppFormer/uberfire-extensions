package org.uberfire.ext.plugin.client.type;

import javax.enterprise.context.ApplicationScoped;

import com.google.gwt.user.client.ui.IsWidget;
import org.gwtbootstrap3.client.ui.Icon;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.uberfire.client.workbench.type.ClientResourceType;
import org.uberfire.ext.plugin.type.DynamicMenuResourceTypeDefinition;

@ApplicationScoped
public class DynamicMenuResourceType
        extends DynamicMenuResourceTypeDefinition
        implements ClientResourceType {

    @Override
    public IsWidget getIcon() {
        return new Icon( IconType.SHARE );
    }
}
