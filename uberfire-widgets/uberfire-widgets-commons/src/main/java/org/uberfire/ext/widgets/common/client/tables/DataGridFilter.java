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

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.Header;

public class DataGridFilter<T> implements Comparable {
    private String key;
    private String filterName;
    private ClickHandler filterClickHandler;
    private boolean visible = true;
    private boolean selected = false;

    public DataGridFilter( String key,
                           String filterName,
                           ClickHandler filterClickHandler ) {
        this.key=key;
        this.filterName = filterName;
        this.filterClickHandler = filterClickHandler;
    }

    public DataGridFilter( String key,
                           String filterName,
                           ClickHandler filterClickHandler,
                           boolean visible ) {
        this( key, filterName, filterClickHandler );
        this.visible = visible;
    }

    public String getKey() {
        return key;
    }

    public void setKey( String key ) {
        this.key = key;
    }

    public String getFilterName() {
        return filterName;
    }

    public void setFilterName( String filterName ) {
        this.filterName = filterName;
    }

    public ClickHandler getFilterClickHandler() {
        return filterClickHandler;
    }

    public void setFilterClickHandler( ClickHandler filterClickHandler ) {
        this.filterClickHandler = filterClickHandler;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected( boolean selected ) {
        this.selected = selected;
    }

    @Override
    public int compareTo(Object o) {
        if (!(o instanceof DataGridFilter )) {
            return 0;
        }
        DataGridFilter otherFilter = (DataGridFilter ) o;
        if( filterName!=null && filterName.trim().equals( otherFilter.getFilterName() ) )
            return 0;
        else
            return -1;

    }
}
