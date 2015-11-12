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
public class PagedTableTest {

    private PagedTable pagedTable;

    @GwtMock
    DataGrid dataGrid;

    @Test
    public void testConstructorWithSize() throws Exception {
        this.pagedTable = new PagedTable( 5 ) {
            @Override protected DataGrid makeDataGrid() {
                return PagedTableTest.this.dataGrid;
            }

            @Override protected DataGrid makeDataGrid( ProvidesKey providesKey ) {
                return PagedTableTest.this.dataGrid;
            }
        };

        verify( dataGrid ).setPageSize( 5 );
        verify( pagedTable.pager ).setPageSize( 5 );
    }

    @Test
    public void testConstructorWithAllParameters() throws Exception {
        this.pagedTable = new PagedTable( 10,
                                          mock( ProvidesKey.class ),
                                          new GridGlobalPreferences(),
                                          false ) {
            @Override protected DataGrid makeDataGrid() {
                return PagedTableTest.this.dataGrid;
            }

            @Override protected DataGrid makeDataGrid( ProvidesKey providesKey ) {
                return PagedTableTest.this.dataGrid;
            }
        };

        verify( dataGrid ).setPageSize( 10 );
        verify( pagedTable.pager ).setPageSize( 10 );
    }
}