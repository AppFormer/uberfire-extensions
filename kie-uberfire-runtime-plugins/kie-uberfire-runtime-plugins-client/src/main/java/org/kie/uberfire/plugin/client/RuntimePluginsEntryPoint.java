package org.kie.uberfire.plugin.client;

import java.util.Collection;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.google.gwt.core.client.ScriptInjector;
import com.google.gwt.dom.client.StyleInjector;
import org.jboss.errai.bus.client.api.ClientMessageBus;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.jboss.errai.ioc.client.api.EntryPoint;
import org.kie.uberfire.plugin.client.resources.WebAppResource;
import org.kie.uberfire.plugin.model.DynamicMenu;
import org.kie.uberfire.plugin.model.DynamicMenuItem;
import org.kie.uberfire.plugin.model.RuntimePlugin;
import org.kie.uberfire.plugin.service.PluginServices;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.Workbench;
import org.uberfire.client.workbench.widgets.menu.WorkbenchMenuBar;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuPosition;

import static com.google.gwt.core.client.ScriptInjector.*;

@EntryPoint
public class RuntimePluginsEntryPoint {

    @Inject
    private Workbench workbench;

    @Inject
    private Caller<PluginServices> pluginServices;

    @Inject
    private ClientMessageBus bus;

    @Inject
    private WorkbenchMenuBar menubar;

    @Inject
    private PlaceManager placeManager;

    @PostConstruct
    public void init() {
        WebAppResource.INSTANCE.CSS().ensureInjected();
        workbench.addStartupBlocker( RuntimePluginsEntryPoint.class );
    }

    @AfterInitialization
    public void setup() {
        pluginServices.call( new RemoteCallback<Collection<RuntimePlugin>>() {
            @Override
            public void callback( Collection<RuntimePlugin> response ) {
                for ( final RuntimePlugin plugin : response ) {
                    ScriptInjector.fromString( plugin.getScript() ).setWindow( TOP_WINDOW ).inject();
                    StyleInjector.inject( plugin.getStyle(), true );
                }
                pluginServices.call( new RemoteCallback<Collection<DynamicMenu>>() {
                    @Override
                    public void callback( Collection<DynamicMenu> response ) {
                        for ( final DynamicMenu menu : response ) {
                            if ( !menu.getMenuItems().isEmpty() ) {
                                MenuFactory.SubMenusBuilder<MenuFactory.SubMenuBuilder<MenuFactory.TopLevelMenusBuilder<MenuFactory.MenuBuilder>>> dynamicMenu = MenuFactory.newTopLevelMenu( menu.getName() ).position( MenuPosition.CENTER ).menus();
                                for ( final DynamicMenuItem dynamicMenuItem : menu.getMenuItems() ) {
                                    dynamicMenu.menu( dynamicMenuItem.getMenuLabel() )
                                            .respondsWith( new Command() {
                                                @Override
                                                public void execute() {
                                                    placeManager.goTo( dynamicMenuItem.getActivityId() );
                                                }
                                            } )
                                            .endMenu();
                                }
                                menubar.addMenus( dynamicMenu.endMenus().endMenu().build() );
                            }
                        }
                        workbench.removeStartupBlocker( RuntimePluginsEntryPoint.class );
                    }
                } ).listDynamicMenus();
            }
        } ).listRuntimePlugins();
    }
}
