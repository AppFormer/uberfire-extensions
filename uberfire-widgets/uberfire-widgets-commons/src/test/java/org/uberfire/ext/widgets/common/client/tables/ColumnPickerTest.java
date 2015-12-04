/*
 * Copyright 2014 JBoss Inc
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

package org.uberfire.ext.widgets.common.client.tables;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.github.gwtbootstrap.client.ui.DataGrid;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.cellview.client.TextHeader;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.uberfire.ext.services.shared.preferences.GridColumnPreference;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
public class ColumnPickerTest {

    @InjectMocks
    ColumnPicker columnPicker;

    @GwtMock
    DataGrid dataGrid;

    @Before
    public void setUp() throws Exception {
        final List<Column> columns = new ArrayList<Column>();
        doAnswer( new Answer<Void>() {
            @Override
            public Void answer( InvocationOnMock invocationOnMock ) throws Throwable {
                columns.add( (Column) invocationOnMock.getArguments()[0] );
                return null;
            }
        } ).when( dataGrid ).addColumn( any( Column.class ), any( Header.class ) );

        doAnswer( new Answer<Integer>() {
            @Override
            public Integer answer( InvocationOnMock invocationOnMock ) throws Throwable {
                return columns.size();
            }
        } ).when( dataGrid ).getColumnCount();

        doAnswer( new Answer<Void>() {
            @Override
            public Void answer( InvocationOnMock invocationOnMock ) throws Throwable {
                columns.remove( 0 );
                return null;
            }
        } ).when( dataGrid ).removeColumn( 0 );
    }

    @Test
    public void testAddRemoveColumn() {
        final Column column = mock( Column.class );
        when( column.getDataStoreName() ).thenReturn( "id" );
        final ColumnMeta meta = new ColumnMeta( column, "caption" );
        meta.setHeader( new TextHeader( "header" ) );
        columnPicker.addColumn( meta );
        assertTrue( columnPicker.getColumnMetaList().contains( meta ) );
        verify( dataGrid ).addColumn( column, meta.getHeader() );
        assertEquals( 1, dataGrid.getColumnCount() );
        columnPicker.removeColumn( meta );
        assertFalse( columnPicker.getColumnMetaList().contains( meta ) );
        verify( dataGrid ).removeColumn( 0 );
        assertEquals( 0, dataGrid.getColumnCount() );
    }

    @Test
    public void testAddColumns() {
        final Column column1 = mock( Column.class );
        final ColumnMeta meta1 = new ColumnMeta( column1, "caption1", true, 1 );
        meta1.setHeader( new TextHeader( "header1" ) );
        final Column column0 = mock( Column.class );
        final ColumnMeta meta0 = new ColumnMeta( column0, "caption0", true, 0 );
        meta0.setHeader( new TextHeader( "header0" ) );
        columnPicker.addColumns( Arrays.asList( meta1, meta0 ) );
        assertEquals( 2, columnPicker.getColumnMetaList().size() );
        verify( dataGrid ).addColumn( column0, meta0.getHeader() );
        verify( dataGrid ).addColumn( column1, meta1.getHeader() );
        assertEquals( 2, dataGrid.getColumnCount() );
    }

    @Test
    public void testColumnPreference() {
        final Column column = mock( Column.class );
        when( column.getDataStoreName() ).thenReturn( "id" );
        final ColumnMeta meta = new ColumnMeta( column, "caption" );
        meta.setHeader( new TextHeader( "header" ) );
        columnPicker.addColumn( meta );
        final List<GridColumnPreference> columnsState = columnPicker.getColumnsState();
        assertEquals( 1, columnsState.size() );
        final GridColumnPreference preference = columnsState.get( 0 );
        assertEquals( preference.getName(), column.getDataStoreName() );
        assertEquals( 0, preference.getPosition().intValue() );
    }

}
