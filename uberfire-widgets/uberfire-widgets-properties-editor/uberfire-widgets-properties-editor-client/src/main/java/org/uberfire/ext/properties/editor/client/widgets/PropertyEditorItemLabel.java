package org.uberfire.ext.properties.editor.client.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.client.views.pfly.widgets.FormLabelHelp;

public class PropertyEditorItemLabel extends Composite {

    @UiField
    FormLabelHelp label;

    public PropertyEditorItemLabel() {
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    public void setText(String text){
        label.setText( text );
    }

    public void setFor(String forValue){
        label.setFor( forValue );
    }

    public void setHelpTitle( final String title ){
        label.setHelpTitle( title );
    }

    public void setHelpContent( final String content ){
        label.setHelpContent( content );
    }

    interface MyUiBinder extends UiBinder<Widget, PropertyEditorItemLabel> {

    }

    private static MyUiBinder uiBinder = GWT.create( MyUiBinder.class );

}