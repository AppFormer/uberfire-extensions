package org.kie.uberfire.plugin.client.type;

import javax.enterprise.context.ApplicationScoped;

import com.github.gwtbootstrap.client.ui.Icon;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.user.client.ui.IsWidget;
import org.kie.uberfire.plugin.type.DynamicMenuResourceTypeDefinition;
import org.uberfire.client.workbench.type.ClientResourceType;

@ApplicationScoped
public class DynamicMenuResourceType
        extends DynamicMenuResourceTypeDefinition
        implements ClientResourceType {

    @Override
    public IsWidget getIcon() {
        return new Icon( IconType.SHARE );
    }
}
