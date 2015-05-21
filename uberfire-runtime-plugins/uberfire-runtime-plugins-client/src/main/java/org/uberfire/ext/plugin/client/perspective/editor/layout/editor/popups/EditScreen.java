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
package org.uberfire.ext.plugin.client.perspective.editor.layout.editor.popups;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.gwtbootstrap.client.ui.AccordionGroup;
import com.github.gwtbootstrap.client.ui.ControlGroup;
import com.github.gwtbootstrap.client.ui.HelpInline;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.constants.ControlGroupType;
import com.github.gwtbootstrap.client.ui.event.HiddenEvent;
import com.github.gwtbootstrap.client.ui.event.HiddenHandler;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.ext.layout.editor.client.LayoutEditor;
import org.uberfire.ext.layout.editor.client.components.ModalConfigurationContext;
import org.uberfire.ext.layout.editor.client.structure.EditorWidget;
import org.uberfire.ext.plugin.client.perspective.editor.layout.editor.ScreenLayoutDragComponent;
import org.uberfire.ext.plugin.client.resources.i18n.CommonConstants;
import org.uberfire.ext.plugin.client.validation.NameValidator;
import org.uberfire.ext.properties.editor.client.PropertyEditorWidget;
import org.uberfire.ext.properties.editor.model.PropertyEditorCategory;
import org.uberfire.ext.properties.editor.model.PropertyEditorEvent;
import org.uberfire.ext.properties.editor.model.PropertyEditorFieldInfo;
import org.uberfire.ext.properties.editor.model.PropertyEditorType;
import org.uberfire.ext.properties.editor.model.validators.PropertyFieldValidator;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.footers.ModalFooterOKCancelButtons;

public class EditScreen
        extends BaseModal {

    public static String PROPERTY_EDITOR_KEY = "LayoutEditor";

    private final ModalConfigurationContext configContext;

    @UiField
    TextBox key;

    @UiField
    AccordionGroup paramAccordion;

    @UiField
    ControlGroup paramKeyControlGroup;

    @UiField
    HelpInline paramKeyInline;

    @UiField
    TextBox value;

    @UiField
    PropertyEditorWidget propertyEditor;

    private Boolean revertChanges = Boolean.TRUE;

    private Map<String, String> lastParametersSaved = new HashMap<String, String>();

    interface Binder
            extends
            UiBinder<Widget, EditScreen> {

    }

    private static Binder uiBinder = GWT.create( Binder.class );

    public EditScreen(ModalConfigurationContext configContext) {
        clearModal();
        this.configContext = configContext;
        setTitle( CommonConstants.INSTANCE.EditComponent() );
        setMaxHeigth("350px");
        add(uiBinder.createAndBindUi(this));
        propertyEditor.setLastOpenAccordionGroupTitle( "Screen Editors" );
        propertyEditor.handle( generateEvent( defaultScreenProperties() ) );
        saveOriginalState();
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
                     }
             )
           );
        paramAccordion.addHiddenHandler( new HiddenHandler() {
            @Override
            public void onHidden( HiddenEvent hiddenEvent ) {
                hiddenEvent.stopPropagation();
            }
        } );
        addHiddlenHandler();
    }

    private void clearModal() {

    }

    private void saveOriginalState() {
        lastParametersSaved = new HashMap<String, String>();
        Map<String, String> layoutComponentProperties = configContext.getComponentProperties();
        for ( String key : layoutComponentProperties.keySet() ) {
            lastParametersSaved.put( key, layoutComponentProperties.get( key ) );
        }
    }

    private void addHiddlenHandler() {
        addHiddenHandler( new HiddenHandler() {
            @Override
            public void onHidden( HiddenEvent hiddenEvent ) {
                if ( userPressCloseOrCancel() ) {
                    revertChanges();
                }
            }
        } );
    }

    private void revertChanges() {
        configContext.resetComponentProperties();
        for ( String key : lastParametersSaved.keySet() ) {
            configContext.setComponentProperty(key, lastParametersSaved.get(key));
        }
    }

    private boolean userPressCloseOrCancel() {
        return revertChanges;
    }

    public void show() {
        super.show();
    }

    void okButton() {
        super.hide();
        revertChanges = Boolean.FALSE;
        configContext.configurationFinished();
    }

    void cancelButton() {
        super.hide();
        configContext.configurationCancelled();
    }

    @Override
    public void hide() {
        super.hide();
    }

    @UiHandler("add")
    void add( final ClickEvent event ) {
        final PropertyEditorCategory property = addProperty();
        if ( property == null ) {
            return;
        }
        propertyEditor.setLastOpenAccordionGroupTitle( "Screen Editors" );
        propertyEditor.handle( generateEvent( property ) );
        key.setText( "" );
        value.setText( "" );
    }

    private PropertyEditorCategory addProperty() {
        paramKeyInline.setText( "" );
        paramKeyControlGroup.setType( ControlGroupType.NONE );

        //Check the Key is valid
        final NameValidator validator = NameValidator.parameterNameValidator();
        if ( !validator.isValid( key.getText() ) ) {
            paramKeyControlGroup.setType( ControlGroupType.ERROR );
            paramKeyInline.setText( validator.getValidationError() );
            return null;
        }

        //Check the Key is unique
        Map<String, String> properties = configContext.getComponentProperties();
        for ( String parameterKey : properties.keySet() ) {
            if ( key.getText().equals( parameterKey ) ) {
                paramKeyControlGroup.setType( ControlGroupType.ERROR );
                paramKeyInline.setText( CommonConstants.INSTANCE.DuplicateParameterName() );
                return null;
            }
        }

        configContext.setComponentProperty(key.getText(), value.getText());
        return defaultScreenProperties();
    }

    private PropertyEditorCategory defaultScreenProperties() {

        //Override getFields() so we can remove Parameter from ScreenEditor when collection is modified by PropertiesWidget
        PropertyEditorCategory category = new PropertyEditorCategory( "Screen Editors" ) {

            @Override
            public List<PropertyEditorFieldInfo> getFields() {
                return new ArrayList<PropertyEditorFieldInfo>( super.getFields() ) {

                    @Override
                    public boolean remove( Object o ) {
                        if ( o instanceof PropertyEditorFieldInfo ) {
                            final PropertyEditorFieldInfo info = (PropertyEditorFieldInfo) o;
                            configContext.removeComponentProperty(info.getLabel());
                        }
                        return super.remove( o );
                    }
                };
            }
        };

        boolean alreadyHasScreenNameParameter = false;
        final Map<String, String> parameters = configContext.getComponentProperties();
        for ( final String key : parameters.keySet() ) {
            if ( key.equals( ScreenLayoutDragComponent.PLACE_NAME_PARAMETER ) ) {
                alreadyHasScreenNameParameter = true;
            }
            category.withField( new PropertyEditorFieldInfo( key,
                                                             parameters.get( key ),
                                                             PropertyEditorType.TEXT )
                                        .withKey( configContext.hashCode() + key )
                                        .withRemovalSupported( !key.equals( ScreenLayoutDragComponent.PLACE_NAME_PARAMETER ) )
                                        .withValidators(new PropertyFieldValidator() {
                                            @Override
                                            public boolean validate(Object value) {
                                                return true;
                                            }

                                            @Override
                                            public String getValidatorErrorMessage() {
                                                return "";
                                            }
                                        }) );
        }

        if ( !alreadyHasScreenNameParameter ) {
            category.withField( new PropertyEditorFieldInfo( ScreenLayoutDragComponent.PLACE_NAME_PARAMETER,
                                                             "",
                                                             PropertyEditorType.TEXT )
                                        .withKey( configContext.hashCode() +  ScreenLayoutDragComponent.PLACE_NAME_PARAMETER)
                                        .withValidators(new PropertyFieldValidator() {
                                            @Override
                                            public boolean validate(Object value) {
                                                return true;
                                            }

                                            @Override
                                            public String getValidatorErrorMessage() {
                                                return "";
                                            }
                                        }) );
        }

        return category;
    }

    private PropertyEditorEvent generateEvent( PropertyEditorCategory category ) {
        PropertyEditorEvent event = new PropertyEditorEvent( PROPERTY_EDITOR_KEY, category );
        return event;
    }

}
