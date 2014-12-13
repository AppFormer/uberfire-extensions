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

package org.uberfire.ext.editor.commons.client.menu;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.commons.data.Pair;
import org.uberfire.ext.editor.commons.client.file.CommandWithFileNameAndCommitMessage;
import org.uberfire.ext.editor.commons.client.file.CopyPopup;
import org.uberfire.ext.editor.commons.client.file.DeletePopup;
import org.uberfire.ext.editor.commons.client.file.FileNameAndCommitMessage;
import org.uberfire.ext.editor.commons.client.file.RenamePopup;
import org.uberfire.ext.editor.commons.client.resources.i18n.CommonConstants;
import org.uberfire.ext.editor.commons.client.validation.Validator;
import org.uberfire.ext.editor.commons.service.support.SupportsCopy;
import org.uberfire.ext.editor.commons.service.support.SupportsDelete;
import org.uberfire.ext.editor.commons.service.support.SupportsRename;
import org.uberfire.ext.widgets.common.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.Menus;

import static org.uberfire.workbench.model.menu.MenuFactory.*;

public class BasicFileMenuBuilderImpl implements BasicFileMenuBuilder {

    @Inject
    private RestoreVersionCommandProvider restoreVersionCommandProvider;

    @Inject
    private Event<NotificationEvent> notification;

    @Inject
    private PlaceManager placeManager;

    @Inject
    private BusyIndicatorView busyIndicatorView;

    private Command saveCommand = null;
    private MenuItem saveMenuItem;
    private Command deleteCommand = null;
    private Command renameCommand = null;
    private Command copyCommand = null;
    private Command validateCommand = null;
    private Command restoreCommand = null;
    private List<Pair<String, Command>> otherCommands = new ArrayList<Pair<String, Command>>();
    private List<MenuItem> topLevelMenus = new ArrayList<MenuItem>();

    @Override
    public BasicFileMenuBuilder addSave( final MenuItem menuItem ) {
        saveMenuItem = menuItem;
        return this;
    }

    @Override
    public BasicFileMenuBuilder addSave( final Command command ) {
        this.saveCommand = command;
        return this;
    }

    @Override
    public BasicFileMenuBuilder addDelete( final Path path,
                                           final Caller<? extends SupportsDelete> deleteCaller ) {
        return addDelete( new Command() {
            @Override
            public void execute() {
                final DeletePopup popup = new DeletePopup( new ParameterizedCommand<String>() {
                    @Override
                    public void execute( final String comment ) {
                        busyIndicatorView.showBusyIndicator( CommonConstants.INSTANCE.Deleting() );
                        deleteCaller.call( getDeleteSuccessCallback(),
                                           new HasBusyIndicatorDefaultErrorCallback( busyIndicatorView ) ).delete( path,
                                                                                                                   comment );
                    }
                } );

                popup.show();
            }
        } );
    }

    private RemoteCallback<Void> getDeleteSuccessCallback() {
        return new RemoteCallback<Void>() {

            @Override
            public void callback( final Void response ) {
                busyIndicatorView.hideBusyIndicator();
                notification.fire( new NotificationEvent( CommonConstants.INSTANCE.ItemDeletedSuccessfully() ) );
            }
        };
    }

    @Override
    public BasicFileMenuBuilder addDelete( final Command command ) {
        this.deleteCommand = command;
        return this;
    }

    @Override
    public BasicFileMenuBuilder addRename( final Command command ) {
        this.renameCommand = command;
        return this;
    }

    @Override
    public BasicFileMenuBuilder addRename( final Path path,
                                           final Caller<? extends SupportsRename> renameCaller ) {
        return addRename( new Command() {
            @Override
            public void execute() {
                final RenamePopup popup = new RenamePopup( path,
                                                           new CommandWithFileNameAndCommitMessage() {
                                                               @Override
                                                               public void execute( final FileNameAndCommitMessage details ) {
                                                                   busyIndicatorView.showBusyIndicator( CommonConstants.INSTANCE.Renaming() );
                                                                   renameCaller.call( getRenameSuccessCallback(),
                                                                                      new HasBusyIndicatorDefaultErrorCallback( busyIndicatorView ) ).rename( path,
                                                                                                                                                              details.getNewFileName(),
                                                                                                                                                              details.getCommitMessage() );
                                                               }
                                                           } );

                popup.show();
            }
        } );
    }

    private RemoteCallback<Path> getRenameSuccessCallback() {
        return new RemoteCallback<Path>() {

            @Override
            public void callback( final Path path ) {
                busyIndicatorView.hideBusyIndicator();
                notification.fire( new NotificationEvent( CommonConstants.INSTANCE.ItemRenamedSuccessfully() ) );
            }
        };
    }

    @Override
    public BasicFileMenuBuilder addRename( final Path path,
                                           final Validator validator,
                                           final Caller<? extends SupportsRename> renameCaller ) {
        return addRename( new Command() {
            @Override
            public void execute() {
                final RenamePopup popup = new RenamePopup( path,
                                                           validator,
                                                           new CommandWithFileNameAndCommitMessage() {
                                                               @Override
                                                               public void execute( final FileNameAndCommitMessage details ) {
                                                                   busyIndicatorView.showBusyIndicator( CommonConstants.INSTANCE.Renaming() );
                                                                   renameCaller.call( getRenameSuccessCallback(),
                                                                                      new HasBusyIndicatorDefaultErrorCallback( busyIndicatorView ) ).rename( path,
                                                                                                                                                              details.getNewFileName(),
                                                                                                                                                              details.getCommitMessage() );
                                                               }
                                                           } );

                popup.show();
            }
        } );
    }

    @Override
    public BasicFileMenuBuilder addCopy( final Command command ) {
        this.copyCommand = command;
        return this;
    }

    @Override
    public BasicFileMenuBuilder addCopy( final Path path,
                                         final Caller<? extends SupportsCopy> copyCaller ) {
        return addCopy( new Command() {
            @Override
            public void execute() {
                final CopyPopup popup = new CopyPopup( path,
                                                       new CommandWithFileNameAndCommitMessage() {
                                                           @Override
                                                           public void execute( final FileNameAndCommitMessage details ) {
                                                               busyIndicatorView.showBusyIndicator( CommonConstants.INSTANCE.Copying() );
                                                               copyCaller.call( getCopySuccessCallback(),
                                                                                new HasBusyIndicatorDefaultErrorCallback( busyIndicatorView ) ).copy( path,
                                                                                                                                                      details.getNewFileName(),
                                                                                                                                                      details.getCommitMessage() );
                                                           }
                                                       } );
                popup.show();
            }
        } );
    }

    @Override
    public BasicFileMenuBuilder addCopy( final Path path,
                                         final Validator validator,
                                         final Caller<? extends SupportsCopy> copyCaller ) {
        return addCopy( new Command() {
            @Override
            public void execute() {
                final CopyPopup popup = new CopyPopup( path,
                                                       validator,
                                                       new CommandWithFileNameAndCommitMessage() {
                                                           @Override
                                                           public void execute( final FileNameAndCommitMessage details ) {
                                                               busyIndicatorView.showBusyIndicator( CommonConstants.INSTANCE.Copying() );
                                                               copyCaller.call( getCopySuccessCallback(),
                                                                                new HasBusyIndicatorDefaultErrorCallback( busyIndicatorView ) ).copy( path,
                                                                                                                                                      details.getNewFileName(),
                                                                                                                                                      details.getCommitMessage() );
                                                           }
                                                       } );
                popup.show();
            }
        } );
    }

    private RemoteCallback<Path> getCopySuccessCallback() {
        return new RemoteCallback<Path>() {

            @Override
            public void callback( final Path path ) {
                busyIndicatorView.hideBusyIndicator();
                notification.fire( new NotificationEvent( CommonConstants.INSTANCE.ItemCopiedSuccessfully() ) );
            }
        };
    }

    @Override
    public BasicFileMenuBuilder addValidate( final Command validateCommand ) {
        this.validateCommand = validateCommand;
        return this;
    }

    @Override
    public BasicFileMenuBuilder addRestoreVersion( final Path path ) {
        this.restoreCommand = restoreVersionCommandProvider.getCommand( path );
        return this;
    }

    @Override
    public BasicFileMenuBuilder addCommand( final String caption,
                                            final Command command ) {
        this.otherCommands.add( new Pair<String, Command>( caption,
                                                           command ) );
        return this;
    }

    @Override
    public Menus build() {
        final Map<Object, MenuItem> menuItems = new LinkedHashMap<Object, MenuItem>();
        if ( saveCommand != null ) {
            menuItems.put( MenuItems.SAVE, MenuFactory.newTopLevelMenu( CommonConstants.INSTANCE.Save() )
                    .respondsWith( saveCommand )
                    .endMenu()
                    .build().getItems().get( 0 ) );
        } else if ( saveMenuItem != null ) {
            menuItems.put( MenuItems.SAVE, saveMenuItem );
        }

        if ( deleteCommand != null ) {
            menuItems.put( MenuItems.DELETE, MenuFactory.newTopLevelMenu( CommonConstants.INSTANCE.Delete() )
                    .respondsWith( deleteCommand )
                    .endMenu()
                    .build().getItems().get( 0 ) );
        }

        if ( renameCommand != null ) {
            menuItems.put( MenuItems.RENAME, MenuFactory.newTopLevelMenu( CommonConstants.INSTANCE.Rename() )
                    .respondsWith( renameCommand )
                    .endMenu().build().getItems().get( 0 ) );
        }

        if ( copyCommand != null ) {
            menuItems.put( MenuItems.COPY, MenuFactory.newTopLevelMenu( CommonConstants.INSTANCE.Copy() )
                    .respondsWith( copyCommand )
                    .endMenu()
                    .build().getItems().get( 0 ) );
        }

        if ( validateCommand != null ) {
            menuItems.put( MenuItems.VALIDATE, MenuFactory.newTopLevelMenu( CommonConstants.INSTANCE.Validate() )
                    .respondsWith( validateCommand )
                    .endMenu()
                    .build().getItems().get( 0 ) );
        }

        if ( restoreCommand != null ) {
            menuItems.put( MenuItems.RESTORE, MenuFactory.newTopLevelMenu( CommonConstants.INSTANCE.Restore() )
                    .respondsWith( restoreCommand )
                    .endMenu()
                    .build().getItems().get( 0 ) );
        }

        if ( !( otherCommands == null || otherCommands.isEmpty() ) ) {
            final List<MenuItem> otherMenuItems = new ArrayList<MenuItem>();
            for ( Pair<String, Command> other : otherCommands ) {
                otherMenuItems.add( newSimpleItem( other.getK1() )
                                            .respondsWith( other.getK2() )
                                            .endMenu().build().getItems().get( 0 ) );
            }
            final MenuItem item = MenuFactory.newTopLevelMenu( CommonConstants.INSTANCE.Other() )
                    .withItems( otherMenuItems )
                    .endMenu()
                    .build().getItems().get( 0 );
            menuItems.put( item, item );
        }

        for ( MenuItem menuItem : topLevelMenus ) {
            menuItems.put( menuItem, menuItem );
        }

        return new Menus() {

            @Override
            public List<MenuItem> getItems() {
                return new ArrayList<MenuItem>() {{
                    for ( final MenuItem menuItem : menuItems.values() ) {
                        add( menuItem );
                    }
                }};
            }

            @Override
            public Map<Object, MenuItem> getItemsMap() {
                return menuItems;
            }
        };
    }

    @Override
    public BasicFileMenuBuilder addNewTopLevelMenu( MenuItem menu ) {
        topLevelMenus.add( menu );
        return this;
    }
}
