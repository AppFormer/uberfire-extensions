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
package org.uberfire.ext.wires.bpmn.beliefs.graph.impl;

import org.uberfire.ext.wires.bpmn.beliefs.graph.Edge;
import org.uberfire.ext.wires.bpmn.beliefs.graph.GraphNode;

public class EdgeImpl implements Edge {

    private GraphNode inGraphNode;

    private GraphNode outGraphNode;

    @Override
    public GraphNode getInGraphNode() {
        return inGraphNode;
    }

    @Override
    public GraphNode getOutGraphNode() {
        return outGraphNode;
    }

    @Override
    public String toString() {
        return "EdgeImpl{" +
                "inGraphNode=" + inGraphNode +
                ", outGraphNode=" + outGraphNode +
                '}';
    }

}
