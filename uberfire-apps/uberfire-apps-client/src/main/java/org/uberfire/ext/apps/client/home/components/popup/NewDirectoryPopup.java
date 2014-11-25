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
package org.uberfire.ext.apps.client.home.components.popup;

import com.github.gwtbootstrap.client.ui.ControlGroup;
import com.github.gwtbootstrap.client.ui.HelpInline;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.constants.ControlGroupType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.ext.apps.api.Directory;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.mvp.ParameterizedCommand;

public class NewDirectoryPopup
        extends PopupPanel {

    private ParameterizedCommand clickCommand;

    interface Binder
            extends
            UiBinder<Widget, NewDirectoryPopup> {

    }

    @UiField
    BaseModal popup;

    @UiField
    ControlGroup directoryNameControlGroup;

    @UiField
    TextBox directoryName;

    @UiField
    HelpInline directoryNameInline;

    private DirectoryNameValidator directoryNameValidator;

    private static Binder uiBinder = GWT.create( Binder.class );

    public NewDirectoryPopup( Directory currentDirectory ) {
        setWidget( uiBinder.createAndBindUi( this ) );
        directoryNameValidator = new DirectoryNameValidator( currentDirectory );
    }

    public void show( ParameterizedCommand clickCommand ) {
        this.clickCommand = clickCommand;
        popup.show();
    }

    @UiHandler("cancelButton")
    void cancelButton( final ClickEvent event ) {
        closePopup();
    }

    @UiHandler("okButton")
    void okButton( final ClickEvent event ) {
        if ( directoryNameValidator.isValid( directoryName.getText() ) ) {
            this.clickCommand.execute( directoryName.getText() );
            closePopup();
        } else {
            directoryNameControlGroup.setType( ControlGroupType.ERROR );
            directoryNameInline.setText( directoryNameValidator.getValidationError() );
        }
    }

    private void closePopup() {
        this.directoryName.setText( "" );
        popup.hide();
        super.hide();
    }

}
