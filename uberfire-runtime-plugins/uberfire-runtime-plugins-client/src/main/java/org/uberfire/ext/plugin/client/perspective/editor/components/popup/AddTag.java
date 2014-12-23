/*
* Copyright 2013 JBoss Inc
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
package org.uberfire.ext.plugin.client.perspective.editor.components.popup;

import java.util.ArrayList;
import java.util.List;

import com.github.gwtbootstrap.client.ui.ControlGroup;
import com.github.gwtbootstrap.client.ui.HelpInline;
import com.github.gwtbootstrap.client.ui.Icon;
import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.constants.ControlGroupType;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.ext.plugin.client.perspective.editor.PerspectiveEditorPresenter;
import org.uberfire.ext.plugin.client.resources.i18n.CommonConstants;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.footers.ModalFooterOKCancelButtons;

public class AddTag
        extends BaseModal {

    private final PerspectiveEditorPresenter perspectivePresenter;

    @UiField
    ControlGroup tagControlGroup;

    @UiField
    TextBox tag;

    @UiField
    HelpInline tagInline;

    @UiField
    HorizontalPanel tags;

    private List<String> tagsList = new ArrayList<String>();

    private List<String> originalList = new ArrayList<String>();

    interface Binder
            extends
            UiBinder<Widget, AddTag> {

    }

    private static Binder uiBinder = GWT.create( Binder.class );

    public AddTag( PerspectiveEditorPresenter perspectivePresenter ) {
        this.perspectivePresenter = perspectivePresenter;
        setTitle( CommonConstants.INSTANCE.AddTag() );
        add( uiBinder.createAndBindUi( this ) );
        add( new ModalFooterOKCancelButtons(
                     new Command() {
                         @Override
                         public void execute() {
                             okButton();
                         }
                     },
                     new Command() {
                         @Override
                         public void execute() {
                             cancelButton();
                         }
                     } )
           );
    }

    private void cancelButton() {
        this.tagsList.clear();
        this.tagsList.addAll( originalList );
        closePopup();
    }

    private void closePopup() {
        hide();
        super.hide();
    }

    public void show() {
        loadTags();
        super.show();
    }

    private void loadTags() {
        tagsList = perspectivePresenter.getTags();
        originalList.addAll( perspectivePresenter.getTags() );
        generateTags();
    }

    private void generateTags() {
        tags.clear();
        for ( String tag : tagsList ) {
            tags.add( new Label( tag ) );
            tags.add( generateRemoveIcon( tag ) );
        }
    }

    private void okButton() {
        hide();
    }

    @UiHandler("addTag")
    void addTag( final ClickEvent event ) {
        NameValidator validator = NameValidator.tagNameValidator();
        if ( validator.isValid( tag.getText() ) ) {
            tagsList.add( tag.getText() );
            HorizontalPanel panel = new HorizontalPanel();
            panel.add( new Label( tag.getText() ) );
            createRemoveTagIcon( panel );
            tags.add( panel );
            tag.setText( "" );
            tagControlGroup.setType( ControlGroupType.NONE );
            tagInline.setText( "" );

        } else {
            tagControlGroup.setType( ControlGroupType.ERROR );
            tagInline.setText( validator.getValidationError() );
        }

    }

    private void createRemoveTagIcon( final HorizontalPanel panel ) {
        final FlowPanel iconPanel = new FlowPanel();
        iconPanel.setWidth( "10px" );
        iconPanel.setHeight( "10px" );
        final Icon icon = generateRemoveIcon( tag.getText() );
        panel.addDomHandler( new MouseOverHandler() {
            @Override
            public void onMouseOver( MouseOverEvent event ) {
                iconPanel.add( icon );
            }
        }, MouseOverEvent.getType() );
        panel.addDomHandler( new MouseOutHandler() {
            @Override
            public void onMouseOut( MouseOutEvent event ) {
                iconPanel.remove( icon );
            }
        }, MouseOutEvent.getType() );
        panel.add( iconPanel );
    }

    private Icon generateRemoveIcon( final String value ) {
        final Icon icon = new Icon( IconType.REMOVE );
        icon.addDomHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                tagsList.remove( value );
                generateTags();
            }
        }, ClickEvent.getType() );
        icon.getElement().getStyle().setColor( "rgb(153, 153, 153)" );
        return icon;
    }

}
