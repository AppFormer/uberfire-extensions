/*
 * Copyright 2015 JBoss Inc
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
package org.uberfire.ext.wires.core.grids.client.model;

import java.util.Map;

/**
 * Interface defining a row within the grid
 * @param <V> The generic type of cells in the row
 */
public interface IGridRow<V extends IGridCell<?>> {

    /**
     * Get the height of the row
     * @return
     */
    double getHeight();

    /**
     * Set the height of the row
     * @param height
     */
    void setHeight( final double height );

    /**
     * Get the cells within the row. This is an sparse map of column index to value.
     * Empty cells do not have an entry within the map. Empty cells should be considered
     * as "null" values; rather than empty Strings however the strict interpretation is up
     * to the implementations.
     * @return
     */
    Map<Integer, V> getCells();

}
