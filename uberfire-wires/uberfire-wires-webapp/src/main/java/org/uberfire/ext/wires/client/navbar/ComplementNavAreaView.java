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

package org.uberfire.ext.wires.client.navbar;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Panel;
import org.uberfire.client.workbench.widgets.menu.PerspectiveContextMenusPresenter;
import org.uberfire.ext.wires.core.client.resources.AppResource;

/**
 * A stand-alone (i.e. devoid of Workbench dependencies) View
 */
public class ComplementNavAreaView
        extends Composite
        implements ComplementNavAreaPresenter.View {

    interface ViewBinder
            extends
            UiBinder<Panel, ComplementNavAreaView> {

    }

    private static ViewBinder uiBinder = GWT.create( ViewBinder.class );

    @UiField(provided = true)
    public Image logo;

    @UiField
    public FlowPanel contextMenuArea;

    @Inject
    private PerspectiveContextMenusPresenter contextMenu;

    @PostConstruct
    public void init() {
        logo = new Image( AppResource.INSTANCE.images().wiresUserLogo() );
        initWidget( uiBinder.createAndBindUi( this ) );
        contextMenuArea.add( contextMenu.getView() );
    }

}