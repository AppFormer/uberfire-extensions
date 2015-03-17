/*
 * Copyright 2010 JBoss Inc
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

package org.uberfire.ext.widgets.common.client.tables;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.CheckBox;
import com.github.gwtbootstrap.client.ui.DataGrid;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.github.gwtbootstrap.client.ui.resources.ButtonSize;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.uberfire.ext.services.shared.preferences.GridColumnPreference;
import org.uberfire.ext.services.shared.preferences.GridPreferencesStore;
import org.uberfire.ext.widgets.common.client.resources.i18n.CommonConstants;

public class ColumnPicker<T> {

    private final DataGrid<T> dataGrid;
    private final List<ColumnMeta<T>> columnMetaList = new ArrayList<ColumnMeta<T>>();
    private final PopupPanel popup = new PopupPanel(true);

    private GridPreferencesStore gridPreferences;
    private List<ColumnChangedHandler> columnChangedHandler = new ArrayList<ColumnChangedHandler>();

    public ColumnPicker(DataGrid<T> dataGrid, GridPreferencesStore gridPreferences) {
        this.dataGrid = dataGrid;
        this.gridPreferences = gridPreferences;
    }


    public ColumnPicker(DataGrid<T> dataGrid) {
        this.dataGrid = dataGrid;
    }

    public void addColumnChangedHandler(ColumnChangedHandler handler) {
        columnChangedHandler.add(handler);
    }

    public void addColumns(List<ColumnMeta<T>> columnMetas) {
        columnMetaList.addAll(columnMetas);
        sortAndAddColumns(columnMetas);
        adjustColumnWidths();
    }

    protected void sortAndAddColumns(List<ColumnMeta<T>> columnMetas) {
        // Check for column preferences and orders
        for (ColumnMeta meta : columnMetas) {
            checkColumnMeta(meta);
        }
        // Sort based on preferences applied
        Collections.sort(columnMetas);
        //Add the columns based on the preferences
        for (ColumnMeta meta : columnMetas) {
            addColumn(meta);
        }
    }

    protected void checkColumnMeta(ColumnMeta<T> columnMeta) {
        if (gridPreferences != null) {
            List<GridColumnPreference> columnPreferences = gridPreferences.getColumnPreferences();
            if (!columnPreferences.isEmpty()) {
                boolean found = false;
                for (int i = 0; i < gridPreferences.getColumnPreferences().size() && !found; i++) {
                    GridColumnPreference gcp = gridPreferences.getColumnPreferences().get(i);
                    if (gcp.getName().equals(columnMeta.getHeader().getValue())) {
                        columnMeta.setVisible(true);
                        if (gcp.getWidth() != null) {
                            dataGrid.setColumnWidth(columnMeta.getColumn(), gcp.getWidth());
                        } else {
                            dataGrid.setColumnWidth(columnMeta.getColumn(), 100, Style.Unit.PCT);
                        }
                        columnMeta.setPosition(gcp.getPosition());
                        found = true;
                    }
                }
                if (!found) {
                    columnMeta.setPosition(-1);
                    columnMeta.setVisible(false);
                }
            } else if (gridPreferences.getGlobalPreferences() != null) {
                int position = gridPreferences.getGlobalPreferences().getInitialColumns().indexOf(columnMeta.getHeader().getValue());
                if (position != -1) {
                    columnMeta.setVisible(true);
                    columnMeta.setPosition(position);
                } else {
                    columnMeta.setPosition(-1);
                    columnMeta.setVisible(false);
                }
            }
        }
    }

    public void addColumn(ColumnMeta<T> columnMeta) {
        if (columnMeta == null) return;
        if (!columnMetaList.contains(columnMeta)) columnMetaList.add(columnMeta);
        if (columnMeta.isVisible()) dataGrid.addColumn(columnMeta.getColumn(), columnMeta.getHeader());
    }

    public void setGridPreferencesStore(GridPreferencesStore gridPreferences) {
        this.gridPreferences = gridPreferences;
    }

    public Button createToggleButton() {
        final Button button = new Button();
        button.setToggle(true);
        button.setIcon(IconType.LIST_UL);
        button.setTitle( CommonConstants.INSTANCE.ColumnPickerButtonTooltip() );

        popup.getElement().getStyle().setZIndex(Integer.MAX_VALUE);
        popup.addAutoHidePartner(button.getElement());
        popup.addCloseHandler(new CloseHandler<PopupPanel>() {
            public void onClose(CloseEvent<PopupPanel> popupPanelCloseEvent) {
                if (popupPanelCloseEvent.isAutoClosed()) {
                    button.setActive(false);
                }
            }
        });

        button.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                if (!button.isActive()) {
                    showColumnPickerPopup(button.getAbsoluteLeft() + button.getOffsetWidth(),
                            button.getAbsoluteTop() + button.getOffsetHeight());
                } else {
                    popup.hide(false);
                }
            }
        });
        return button;
    }

    private void showColumnPickerPopup(final int left,
                                       final int top) {
        VerticalPanel popupContent = new VerticalPanel();

        for (final ColumnMeta<T> columnMeta : columnMetaList) {

            final CheckBox checkBox = new CheckBox(columnMeta.getHeader().getValue());

            checkBox.setValue(columnMeta.isVisible());
            checkBox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
                public void onValueChange(ValueChangeEvent<Boolean> booleanValueChangeEvent) {
                    boolean visible = booleanValueChangeEvent.getValue();
                    if (visible) {
                        dataGrid.insertColumn(getVisibleColumnIndex(columnMeta),
                                columnMeta.getColumn(),
                                columnMeta.getHeader());
                    } else {
                        dataGrid.removeColumn(columnMeta.getColumn());
                    }
                    columnMeta.setVisible(visible);
                    adjustColumnWidths();
                }
            });

            if (gridPreferences == null || !gridPreferences.getGlobalPreferences()
                    .getBannedColumns().contains(columnMeta.getHeader().getValue()))
                popupContent.add(checkBox);
        }

        if (gridPreferences != null) {
            Button resetButton = new Button("Reset");
            resetButton.setSize(ButtonSize.MINI);
            resetButton.addClickHandler(new ClickHandler() {

                @Override
                public void onClick(ClickEvent event) {
                    resetTableColumns(left, top);
                }
            });

            popupContent.add(resetButton);
        }
        popup.setWidget(popupContent);
        popup.show();
        int finalLeft = left - popup.getOffsetWidth();
        popup.setPopupPosition(finalLeft, top);
    }

    protected void resetTableColumns(int left, int top) {
        gridPreferences.resetGridColumnPreferences();
        int count = dataGrid.getColumnCount();
        for (int i = 0; i < count; i++) {
            dataGrid.removeColumn(0);
        }

        for (final ColumnMeta<T> columnMeta : columnMetaList) {
            int position = gridPreferences.getGlobalPreferences().getInitialColumns().indexOf(columnMeta.getHeader().getValue());
            columnMeta.setPosition(position);
            columnMeta.setVisible(position > -1);
        }

        sortAndAddColumns(new ArrayList<ColumnMeta<T>>(columnMetaList));

        adjustColumnWidths();

        showColumnPickerPopup(left, top);
    }

    public List<GridColumnPreference> getColumnsState() {
        List<GridColumnPreference> state = new ArrayList<GridColumnPreference>();
        for (final ColumnMeta<T> cm : columnMetaList) {
            if (cm.isVisible()) {
                state.add(new GridColumnPreference(cm.getHeader().getValue(),
                        dataGrid.getColumnIndex(cm.getColumn()),
                        dataGrid.getColumnWidth(cm.getColumn())));
            }
        }
        return state;
    }


    private int getVisibleColumnIndex(final ColumnMeta<T> columnMeta) {
        int index = 0;
        for (final ColumnMeta<T> cm : columnMetaList) {
            if (cm.equals(columnMeta)) {
                return index;
            }
            if (cm.isVisible()) {
                index++;
            }
        }
        return index;
    }

    public void adjustColumnWidths() {
        for (ColumnChangedHandler handler : columnChangedHandler) {
            handler.afterColumnChanged();
        }

        List<GridColumnPreference> preferences = getColumnsState();

        if ( preferences.isEmpty() ) return;
        if ( preferences.size() == 1 ) {
            dataGrid.setColumnWidth(dataGrid.getColumn( 0 ),
                    100,
                    Style.Unit.PCT);
            return;
        }

        int fixedColumnsWidth = 0;
        Map<String, String> fixedWidths = new HashMap<String, String>(  );
        List<String> columnsToCalculate = new ArrayList<String>(  );

        for ( GridColumnPreference preference : preferences ) {
            if ( preference.getWidth() != null && preference.getWidth().endsWith( Style.Unit.PX.getType() ) ) {
                fixedWidths.put( preference.getName(), preference.getWidth() );
                fixedColumnsWidth += Integer.decode( preference.getWidth().substring( 0, preference.getWidth().indexOf( Style.Unit.PX.getType() ) ) );
            } else {
                columnsToCalculate.add( preference.getName() );
            }
        }

        if ( columnsToCalculate.size() > 0 ) {

            double columnPCT = 100 / columnsToCalculate.size();

            if (dataGrid.getOffsetWidth() != 0) {
                int availabelColumnSpace = dataGrid.getOffsetWidth() - fixedColumnsWidth;
                double availablePCT = availabelColumnSpace * 100 / dataGrid.getOffsetWidth();
                columnPCT = columnPCT * availablePCT / 100;
            }

            for ( ColumnMeta<T> cm : columnMetaList ) {
                if (cm.isVisible()) {
                    if ( columnsToCalculate.contains( cm.getHeader().getValue() ) ) {
                        dataGrid.setColumnWidth(cm.getColumn(),
                                columnPCT,
                                Style.Unit.PCT);
                    } else {
                        dataGrid.setColumnWidth( cm.getColumn(), fixedWidths.get( cm.getHeader().getValue() ) );
                    }
                }
            }
        }
    }

    protected void columnMoved(final int visibleFromIndex,
                               final int visibleBeforeIndex) {
        int visibleColumnFromIndex = 0;
        ColumnMeta<T> columnMetaToMove = null;
        for (int i = 0; i < columnMetaList.size(); i++) {
            final ColumnMeta<T> columnMeta = columnMetaList.get(i);
            if (columnMeta.isVisible()) {
                if (visibleFromIndex == visibleColumnFromIndex) {
                    columnMetaToMove = columnMeta;
                    break;
                }
                visibleColumnFromIndex++;
            }
        }
        if (columnMetaToMove == null) {
            return;
        }

        columnMetaList.remove(columnMetaToMove);

        boolean columnInserted = false;
        int visibleColumnBeforeIndex = 0;
        for (int i = 0; i < columnMetaList.size(); i++) {
            final ColumnMeta<T> columnMeta = columnMetaList.get(i);
            if (columnMeta.isVisible()) {
                if (visibleBeforeIndex == visibleColumnBeforeIndex) {
                    columnMetaList.add(i,
                            columnMetaToMove);
                    columnInserted = true;
                    break;
                }
                visibleColumnBeforeIndex++;
            }
        }
        if (!columnInserted) {
            columnMetaList.add(columnMetaToMove);
        }
    }
}
