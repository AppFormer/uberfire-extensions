package org.uberfire.ext.wires.client.navbar;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

import org.uberfire.client.workbench.Header;
import org.uberfire.client.workbench.widgets.menu.WorkbenchMenuBarPresenter;

import static java.lang.Integer.*;

@ApplicationScoped
public class AppNavBar extends Composite implements Header {

    @Inject
    private WorkbenchMenuBarPresenter menuBarPresenter;

    @Override
    public Widget asWidget() {
        return menuBarPresenter.getView().asWidget();
    }

    @Override
    public int getOrder() {
        return MAX_VALUE;
    }

    @Override
    public String getId() {
        return getClass().getName();
    }

    
}
