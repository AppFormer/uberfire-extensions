package org.uberfire.ext.widgets.common.client.tables;

import com.github.gwtbootstrap.client.ui.DataGrid;
import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.base.TextNode;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.uberfire.ext.services.shared.preferences.GridGlobalPreferences;

import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
@WithClassesToStub({Image.class, Label.class, TextNode.class})
public class SimpleTableTest {

    private SimpleTable simpleTable;

    @GwtMock
    DataGrid dataGrid;

    @Test
    public void testRedrawFlush() throws Exception {
        this.simpleTable = new SimpleTable(){
            @Override protected DataGrid makeDataGrid() {
                return SimpleTableTest.this.dataGrid;
            }

            @Override protected DataGrid makeDataGrid( ProvidesKey providesKey ) {
                return SimpleTableTest.this.dataGrid;
            }
        };;

        simpleTable.redraw();
        verify(dataGrid).redraw();
        verify(dataGrid).flush();
    }


}