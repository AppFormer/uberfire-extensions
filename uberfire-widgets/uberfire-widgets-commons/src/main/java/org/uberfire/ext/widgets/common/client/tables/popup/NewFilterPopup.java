/*
 * Copyright 2014 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.widgets.common.client.tables.popup;

import com.github.gwtbootstrap.client.ui.*;
import com.github.gwtbootstrap.client.ui.constants.ButtonType;
import com.github.gwtbootstrap.client.ui.constants.ControlGroupType;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.footers.GenericModalFooter;
import org.uberfire.ext.widgets.common.client.resources.i18n.CommonConstants;

import org.uberfire.mvp.Command;
import org.uberfire.workbench.events.NotificationEvent;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.util.*;

@Dependent
public class NewFilterPopup extends BaseModal {
    interface Binder
            extends
            UiBinder<Widget, NewFilterPopup> {

    }

    public static String FILTER_NAME_PARAM="filterName";

    @UiField
    public Form horizontalForm;

    @UiField
    public HelpBlock errorMessages;

    @UiField
    public ControlGroup errorMessagesGroup;

    @Inject
    private Event<NotificationEvent> notification;

    HashMap formValues = new HashMap();

    private final List<ControlGroup> filterControlGroups = new ArrayList<ControlGroup>();

    Command addfilterCommand;


    private static Binder uiBinder = GWT.create( Binder.class );

    public NewFilterPopup( ) {
        setTitle( CommonConstants.INSTANCE.Add_New_Filter() );

        add( uiBinder.createAndBindUi( this ) );
        init();
        final GenericModalFooter footer = new GenericModalFooter();
        footer.addButton( CommonConstants.INSTANCE.OK(),
                new Command() {
                    @Override
                    public void execute() {
                        okButton();
                    }
                }, IconType.PLUS_SIGN,
                ButtonType.PRIMARY );
        footer.addButton( CommonConstants.INSTANCE.Cancel(),
                new Command() {
                    @Override
                    public void execute() {
                        closePopup();
                    }
                }, null,
                ButtonType.PRIMARY );

        add( footer );
    }

    public void show( Command addfilterCommand) {
        this.addfilterCommand = addfilterCommand;
        super.show();
    }

    private void okButton() {
        getFormValues( filterControlGroups );
        if ( validateForm() ) {
            addfilterCommand.execute();
            closePopup();
        }
    }

    public void init() {
        horizontalForm.clear();
        filterControlGroups.clear();

        ControlGroup controlGroup = new ControlGroup();

        ControlLabel controlLabel = new ControlLabel();
        controlLabel.setTitle( CommonConstants.INSTANCE.Filter_Name() );
        HTML lab = new HTML( "<span style=\"color:red\"> * </span>"+"<span style=\"margin-right:10px\">"+CommonConstants.INSTANCE.Filter_Name()+"</span>" );
        controlLabel.add( lab );

        TextBox fieldTextBox = new TextBox();
        fieldTextBox.setName( FILTER_NAME_PARAM );

        controlGroup.add( controlLabel );
        controlGroup.add( fieldTextBox );

        filterControlGroups.add(controlGroup);
        horizontalForm.add( controlGroup );

    }

    public void cleanForm() {

    }

    public void closePopup() {
        cleanForm();
        hide();
        super.hide();
    }

    private boolean validateForm() {
        boolean valid = true;
        clearErrorMessages();
        String filterName =(String)formValues.get( FILTER_NAME_PARAM );
        if(!(filterName!=null && filterName.trim().length()>0)){
            errorMessages.setText( CommonConstants.INSTANCE.Filter_Must_Have_A_Name() );
            errorMessagesGroup.setType( ControlGroupType.ERROR );
            valid=false;
        }
        return valid;
    }

    public void getFormValues( List<ControlGroup> controlGroups ) {
        formValues =new HashMap(  );

        for ( ControlGroup groupControl : controlGroups ) {
            if(groupControl.getWidget( 1 ) instanceof  TextBox) {
                formValues.put( ( ( TextBox ) groupControl.getWidget( 1 ) ).getName(),
                        ( ( TextBox ) groupControl.getWidget( 1 ) ).getValue() );
            } else if(groupControl.getWidget( 1 ) instanceof  ListBox) {
                ListBox listBox =(ListBox)groupControl.getWidget( 1 );

                List<String> selectedValues = new ArrayList<String>(  );
                for(int i =0;i< listBox.getItemCount();i++){
                    if(listBox.isItemSelected( i )){
                        selectedValues.add( listBox.getValue( i ) );
                    }
                }

                formValues.put( listBox.getName(), selectedValues );
            }
        }
    }

    private void clearErrorMessages() {
        errorMessages.setText( "" );
    }

    public HashMap getFormValues(){
        return formValues;
    }

    public void  addListBoxToFilter(String label, String fieldName, boolean multiselect, HashMap<String,String> listBoxInfo){
        ControlGroup controlGroup = new ControlGroup();

        ControlLabel controlLabel = new ControlLabel();
        controlLabel.setTitle( label );
        HTML lab = new HTML( "<span style=\"margin-right:10px\">"+label +"</span>" );
        controlLabel.add( lab );

        ListBox listBox = new ListBox( multiselect );
        if (listBoxInfo!=null) {
            Set listBoxKeys = listBoxInfo.keySet();
            Iterator it = listBoxKeys.iterator();
            String key;
            while ( it.hasNext() ) {
                key = ( String ) it.next();
                listBox.addItem( listBoxInfo.get( key ), key );
            }
        }
        listBox.setName( fieldName );

        controlGroup.add( controlLabel );
        controlGroup.add( listBox );

        filterControlGroups.add(controlGroup);
        horizontalForm.add( controlGroup );
    }

    public void  addTextBoxToFilter(String label, String fieldName){
        ControlGroup controlGroup = new ControlGroup();

        ControlLabel controlLabel = new ControlLabel();
        controlLabel.setTitle( label );
        HTML lab = new HTML( "<span style=\"margin-right:10px\">"+label +"</span>" );
        controlLabel.add( lab );

        TextBox textBox = new TextBox( );
        textBox.setName( fieldName );

        controlGroup.add( controlLabel );
        controlGroup.add( textBox );

        filterControlGroups.add(controlGroup);
        horizontalForm.add( controlGroup );
    }


}
