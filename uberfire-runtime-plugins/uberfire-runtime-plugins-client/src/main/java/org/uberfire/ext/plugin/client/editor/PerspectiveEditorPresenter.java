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

package org.uberfire.ext.plugin.client.editor;

import com.google.gwt.user.client.ui.*;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.client.annotations.*;
import org.uberfire.client.mvp.UberView;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.ext.editor.commons.client.BaseEditor;
import org.uberfire.ext.editor.commons.service.support.SupportsCopy;
import org.uberfire.ext.editor.commons.service.support.SupportsDelete;
import org.uberfire.ext.editor.commons.service.support.SupportsRename;
import org.uberfire.ext.plugin.client.type.PerspectivePluginResourceType;
import org.uberfire.ext.plugin.event.PluginRenamed;
import org.uberfire.ext.plugin.model.*;
import org.uberfire.ext.plugin.service.PluginServices;

import org.uberfire.lifecycle.OnMayClose;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.model.menu.Menus;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import static org.uberfire.ext.editor.commons.client.menu.MenuItems.*;

@Dependent
@WorkbenchEditor(identifier = "Perspective PlugIn Editor", supportedTypes = { PerspectivePluginResourceType.class }, priority = Integer.MAX_VALUE)
public class PerspectiveEditorPresenter
        extends BaseEditor {

    @Inject
    private PerspectivePluginResourceType resourceType;

    @Inject
    private Caller<PluginServices> pluginServices;

    @Inject
    private Event<NotificationEvent> notification;

    private Plugin plugin;


    @Inject
    public PerspectiveEditorPresenter( final PerspectiveEditorView baseView ) {
        super( baseView );
    }

    @OnStartup
    public void onStartup( final ObservablePath path,
                           final PlaceRequest place ) {
        init( path, place, resourceType, true, false, SAVE, COPY, RENAME, DELETE );
        this.plugin = new Plugin( place.getParameter( "name", "" ), PluginType.PERSPECTIVE, path );
        this.place = place;
    }

    @WorkbenchPartTitleDecoration
    public IsWidget getTitle() {
        return super.getTitle();
    }

    @WorkbenchPartTitle
    public String getTitleText() {
        return "Perspective PlugIn Editor [" + plugin.getName() + "]";
    }

    @WorkbenchMenu
    public Menus getMenus() {
        return menus;
    }

    protected void onPlugInRenamed( @Observes final PluginRenamed pluginRenamed ) {
        if ( pluginRenamed.getOldPluginName().equals( plugin.getName() ) &&
                pluginRenamed.getPlugin().getType().equals( plugin.getType() ) ) {
            this.plugin = new Plugin( pluginRenamed.getPlugin().getName(), PluginType.PERSPECTIVE, pluginRenamed.getPlugin().getPath() );
            changeTitleNotification.fire( new ChangeTitleWidgetEvent( place, "Perspective PlugIn Editor [" + pluginRenamed.getPlugin().getName() + "]", getTitle() ) );
        }
    }

    @Override
    protected void loadContent() {
        pluginServices.call( new RemoteCallback<PluginContent>() {
            @Override
            public void callback( final PluginContent response ) {
                ( ( PerspectiveEditorView ) baseView ).setupContent( response );
                baseView.hideBusyIndicator();
            }

        } ).getPluginContent( versionRecordManager.getCurrentPath() );
    }

    protected void save() {
        pluginServices.call( getSaveSuccessCallback( getContent().hashCode() ) ).save( getContent() );
    }

    @WorkbenchPartView
    public UberView<PerspectiveEditorPresenter> getWidget() {
        return ( UberView<PerspectiveEditorPresenter> ) super.baseView;
    }

    @OnMayClose
    public boolean onMayClose() {
        return super.mayClose( getContent().hashCode() );
    }

    public PluginSimpleContent getContent() {
        return ( ( PerspectiveEditorView ) baseView ).getContent();
    }


    protected Caller<? extends SupportsDelete> getDeleteServiceCaller() {
        return pluginServices;
    }

    protected Caller<? extends SupportsRename> getRenameServiceCaller() {
        return pluginServices;
    }

    protected Caller<? extends SupportsCopy> getCopyServiceCaller() {
        return pluginServices;
    }


}