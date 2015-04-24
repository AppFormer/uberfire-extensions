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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Base implementation of a grid row to avoid boiler-plate for more specific implementations.
 * @param <V> The generic type of cells in the row
 */
public abstract class BaseGridRow<V extends IGridCell<?>> implements IGridRow<V> {

    protected double height = 20.0;
    protected Map<Integer, V> cells = new HashMap<Integer, V>();

    @Override
    public Map<Integer, V> getCells() {
        return Collections.unmodifiableMap( cells );
    }

    @Override
    public double getHeight() {
        return this.height;
    }

    @Override
    public void setHeight( final double height ) {
        this.height = height;
    }

}
