/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.plugin.client.widget.navigator;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Named;

import com.github.gwtbootstrap.client.ui.Collapse;
import com.github.gwtbootstrap.client.ui.CollapseTrigger;
import com.github.gwtbootstrap.client.ui.Divider;
import com.github.gwtbootstrap.client.ui.NavLink;
import com.github.gwtbootstrap.client.ui.NavList;
import com.github.gwtbootstrap.client.ui.WellNavList;
import com.github.gwtbootstrap.client.ui.base.ListItem;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ioc.client.container.IOCBeanDef;
import org.uberfire.client.editor.JSEditorActivity;
import org.uberfire.client.mvp.ActivityManager;
import org.uberfire.client.mvp.PerspectiveActivity;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.SplashScreenActivity;
import org.uberfire.client.mvp.WorkbenchEditorActivity;
import org.uberfire.client.mvp.WorkbenchScreenActivity;
import org.uberfire.client.perspective.JSWorkbenchPerspectiveActivity;
import org.uberfire.client.screen.JSWorkbenchScreenActivity;
import org.uberfire.client.splash.JSSplashScreenActivity;
import org.uberfire.client.workbench.type.ClientResourceType;
import org.uberfire.client.workbench.type.ClientTypeRegistry;
import org.uberfire.ext.plugin.client.type.DynamicMenuResourceType;
import org.uberfire.ext.plugin.client.type.EditorPluginResourceType;
import org.uberfire.ext.plugin.client.type.PerspectiveLayoutPluginResourceType;
import org.uberfire.ext.plugin.client.type.ScreenPluginResourceType;
import org.uberfire.ext.plugin.client.type.SplashPluginResourceType;
import org.uberfire.ext.plugin.event.BaseNewPlugin;
import org.uberfire.ext.plugin.event.PluginAdded;
import org.uberfire.ext.plugin.event.PluginDeleted;
import org.uberfire.ext.plugin.event.PluginRenamed;
import org.uberfire.ext.plugin.model.Activity;
import org.uberfire.ext.plugin.model.Plugin;
import org.uberfire.ext.plugin.model.PluginType;
import org.uberfire.mvp.impl.PathPlaceRequest;

import static org.uberfire.ext.plugin.type.TypeConverterUtil.*;

@Dependent
public class PluginNavList extends Composite {

    interface ViewBinder
            extends
            UiBinder<Widget, PluginNavList> {

    }

    private static ViewBinder uiBinder = GWT.create( ViewBinder.class );

    private static final Comparator<String> PLUGIN_NAME_COMPARATOR = new Comparator<String>() {
        @Override
        public int compare( final String o1,
                            final String o2 ) {
            return o1.compareToIgnoreCase( o2 );
        }
    };

    @UiField
    WellNavList pluginsList;

    @Inject
    private ClientTypeRegistry clientTypeRegistry;

    @Inject
    private EditorPluginResourceType editorPluginResourceType;

    @Inject
    private PerspectiveLayoutPluginResourceType perspectiveLayoutPluginResourceType;

    @Inject
    private ScreenPluginResourceType screenPluginResourceType;

    @Inject
    private SplashPluginResourceType splashPluginResourceType;

    @Inject
    private DynamicMenuResourceType dynamicMenuResourceType;

    @Inject
    private ActivityManager activityManager;

    @Inject
    private PlaceManager placeManager;

    private Map<String, Widget> pluginRef = new HashMap<String, Widget>();

    private final Map<PluginType, NavList> navLists = new HashMap<PluginType, NavList>();

    @PostConstruct
    public void init() {
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    public void setup( final Collection<Plugin> plugins ) {

        final Map<ClientResourceType, Set<Activity>> classified = new LinkedHashMap<ClientResourceType, Set<Activity>>();

        classified.put( perspectiveLayoutPluginResourceType, new HashSet<Activity>() );
        classified.put( screenPluginResourceType, new HashSet<Activity>() );
        classified.put( editorPluginResourceType, new HashSet<Activity>() );
        classified.put( splashPluginResourceType, new HashSet<Activity>() );
        classified.put( dynamicMenuResourceType, new HashSet<Activity>() );

        final Collection<IOCBeanDef<WorkbenchScreenActivity>> screens = IOC.getBeanManager().lookupBeans( WorkbenchScreenActivity.class );
        for ( final IOCBeanDef<WorkbenchScreenActivity> beanDef : screens ) {
            classified.get( screenPluginResourceType ).add( new Activity( getName( beanDef ), PluginType.SCREEN ) );
        }

        final Collection<IOCBeanDef<WorkbenchEditorActivity>> editors = IOC.getBeanManager().lookupBeans( WorkbenchEditorActivity.class );
        for ( final IOCBeanDef<WorkbenchEditorActivity> beanDef : editors ) {
            classified.get( editorPluginResourceType ).add( new Activity( getName( beanDef ), PluginType.EDITOR ) );
        }

        final Collection<IOCBeanDef<SplashScreenActivity>> splashes = IOC.getBeanManager().lookupBeans( SplashScreenActivity.class );
        for ( final IOCBeanDef<SplashScreenActivity> beanDef : splashes ) {
            classified.get( splashPluginResourceType ).add( new Activity( getName( beanDef ), PluginType.SPLASH ) );
        }

        for ( final Plugin plugin : plugins ) {
            final ClientResourceType type = clientTypeRegistry.resolve( plugin.getPath() );
            if ( type != null ) {
                classified.get( type ).add( plugin );
            }
        }

        final Collection<IOCBeanDef<JSWorkbenchScreenActivity>> jsscreens = IOC.getBeanManager().lookupBeans( JSWorkbenchScreenActivity.class );
        for ( final IOCBeanDef<JSWorkbenchScreenActivity> beanDef : jsscreens ) {
            classified.get( screenPluginResourceType ).add( new Activity( beanDef.getName(), PluginType.SCREEN ) );
        }

        final Collection<IOCBeanDef<JSEditorActivity>> jseditors = IOC.getBeanManager().lookupBeans( JSEditorActivity.class );
        for ( final IOCBeanDef<JSEditorActivity> beanDef : jseditors ) {
            classified.get( editorPluginResourceType ).add( new Activity( beanDef.getName(), PluginType.EDITOR ) );
        }

        final Collection<IOCBeanDef<JSSplashScreenActivity>> jssplashes = IOC.getBeanManager().lookupBeans( JSSplashScreenActivity.class );
        for ( final IOCBeanDef<JSSplashScreenActivity> beanDef : jssplashes ) {
            classified.get( splashPluginResourceType ).add( new Activity( beanDef.getName(), PluginType.SPLASH ) );
        }

        pluginsList.clear();

        final Iterator<Map.Entry<ClientResourceType, Set<Activity>>> itr = classified.entrySet().iterator();
        while ( itr.hasNext() ) {
            final Map.Entry<ClientResourceType, Set<Activity>> e = itr.next();
            final PluginType type = fromResourceType( e.getKey() );

            final CollapseTrigger collapseTrigger = makeTriggerWidget( e.getKey(), type );

            final Collapse collapse = new Collapse();

            collapse.setExistTrigger( true );
            collapse.setId( type.toString() );

            final NavList itemsNavList = new NavList();
            collapse.add( itemsNavList );

            navLists.put( type,
                          itemsNavList );

            //Sort Activities by Name. A TreeMap supports sorting on insertion by natural ordering of its keys
            final Map<String, Activity> activities = new TreeMap<String, Activity>( PLUGIN_NAME_COMPARATOR );
            for ( final Activity item : e.getValue() ) {
                activities.put( item.getName(),
                                item );
            }
            for ( final Activity item : activities.values() ) {
                itemsNavList.add( makeItemNavLink( item ) );
            }
            collapse.setDefaultOpen( false );

            pluginsList.add( collapseTrigger );
            pluginsList.add( collapse );
            if ( itr.hasNext() ) {
                pluginsList.add( new Divider() );
            }
        }
    }

    private String getName( final IOCBeanDef<?> beanDef ) {
        for ( final Annotation annotation : beanDef.getQualifiers() ) {
            if ( annotation instanceof Named ) {
                return ( (Named) annotation ).value();
            }
        }
        return "";
    }

    private CollapseTrigger makeTriggerWidget( final ClientResourceType resourceType,
                                               final PluginType type ) {
        return new CollapseTrigger( "#" + type.toString() ) {{
            if ( resourceType.getIcon() == null ) {
                setWidget( new TriggerWidget( resourceType.getDescription() ) );
            } else {
                setWidget( new TriggerWidget( resourceType.getIcon(), resourceType.getDescription() ) );
            }
        }};
    }

    private Widget makeItemNavLink( final Activity activity ) {

        final Widget nav;
        if ( activity instanceof Plugin ) {
            nav = new NavLink( activity.getName() ) {{
                addClickHandler( new ClickHandler() {
                    @Override
                    public void onClick( final ClickEvent event ) {
                        placeManager.goTo( new PathPlaceRequest( ( (Plugin) activity ).getPath() ).addParameter( "name", activity.getName() ) );
                    }
                } );
            }};
        } else {
            nav = new ListItem( new InlineLabel( activity.getName() ) );
        }
        pluginRef.put( activity.getName(), nav );

        return pluginRef.get( activity.getName() );
    }

    public void onPlugInAdded( @Observes final PluginAdded pluginAdded ) {
        addNewPlugin( pluginAdded );
    }

    public void addNewPlugin( final BaseNewPlugin newPlugin ) {
        //Sort Widgets by Plugin Name. A TreeMap supports sorting on insertion by natural ordering of its keys
        final Map<String, Widget> sortedNavList = new TreeMap<String, Widget>( PLUGIN_NAME_COMPARATOR );
        final NavList navList = navLists.get( newPlugin.getPlugin().getType() );
        for ( int i = 0; i < navList.getWidgetCount(); i++ ) {
            final Widget w = navList.getWidget( i );
            for ( Map.Entry<String, Widget> e : pluginRef.entrySet() ) {
                if ( e.getValue().equals( w ) ) {
                    sortedNavList.put( e.getKey(),
                                       e.getValue() );
                }
            }
        }
        sortedNavList.put( newPlugin.getPlugin().getName(),
                           makeItemNavLink( newPlugin.getPlugin() ) );

        navList.clear();
        for ( Widget w : sortedNavList.values() ) {
            navList.add( w );
        }
    }

    public void onPlugInRenamed( @Observes final PluginRenamed pluginRenamed ) {
        final Widget nav = pluginRef.get( pluginRenamed.getOldPluginName() );
        if ( nav != null ) {
            nav.removeFromParent();
        }
        addNewPlugin( pluginRenamed );
    }

    public void onPlugInDeleted( @Observes final PluginDeleted pluginDeleted ) {
        final Widget nav = pluginRef.get( pluginDeleted.getPluginName() );
        if ( nav != null ) {
            nav.removeFromParent();
        }
    }

}