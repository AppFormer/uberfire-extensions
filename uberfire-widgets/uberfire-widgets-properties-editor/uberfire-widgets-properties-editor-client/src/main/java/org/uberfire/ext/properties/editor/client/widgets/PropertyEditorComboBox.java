package org.uberfire.ext.properties.editor.client.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.commons.data.Pair;

public class PropertyEditorComboBox extends AbstractPropertyEditorWidget {

    interface MyUiBinder extends UiBinder<Widget, PropertyEditorComboBox> {

    }

    private static MyUiBinder uiBinder = GWT.create( MyUiBinder.class );

    @UiField
    ListBox listBox;

    public PropertyEditorComboBox() {
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    public void addItem( final String value ) {
        listBox.addItem( value );
    }

    public void addItem( final Pair<String, String> pair ) {
        listBox.addItem( pair.getK1(),
                         pair.getK2() );
    }

    public Pair<String, String> getSelectedPair( final int selected ) {
        return Pair.newPair( listBox.getItemText( selected ),
                             listBox.getValue( selected ) );

    }

    public void setSelectedIndex( final int selected ) {
        listBox.setSelectedIndex( selected );
    }

    public int getSelectedIndex() {
        return listBox.getSelectedIndex();
    }

    public void setSelectItemByText( final String text ) {
        for ( int i = 0; i < listBox.getItemCount(); i++ ) {
            String candidate = listBox.getItemText( i );
            if ( candidate.equalsIgnoreCase( text ) ) {
                setSelectedIndex( i );
            }
        }
    }

    public void clear() {
        listBox.clear();
    }

    public String getItemText( final int selectedIndex ) {
        return listBox.getItemText( selectedIndex );
    }

    public void addChangeHandler( final ChangeHandler changeHandler ) {
        listBox.addChangeHandler( changeHandler );
    }

}