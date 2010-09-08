//------------------------------------------------------------------------------
// Copyright (c) 2010. Dmitrii Dimandt <dmitrii@dmitriid.com>
//
//    Licensed under the Apache License, Version 2.0 (the "License");
//    you may not use this file except in compliance with the License.
//    You may obtain a copy of the License at
//
//        http://www.apache.org/licenses/LICENSE-2.0
//
//    Unless required by applicable law or agreed to in writing, software
//    distributed under the License is distributed on an "AS IS" BASIS,
//    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//    See the License for the specific language governing permissions and
//    limitations under the License.
//------------------------------------------------------------------------------

package com.dmitriid.cali.db;

import com.tinkerpop.blueprints.pgm.Vertex;
import com.tinkerpop.blueprints.pgm.impls.tg.TinkerGraph;
import com.tinkerpop.gremlin.compiler.context.GremlinScriptContext;

public class TinkerGraphConnector extends DBConnector{

    public TinkerGraphConnector(String[] args) {

        super();

        _graphDb = new TinkerGraph();

        Vertex root = _graphDb.getVertex(0);

        _engine.getBindings(GremlinScriptContext.ENGINE_SCOPE).put("$name", "gremlin");
        _engine.getBindings(GremlinScriptContext.ENGINE_SCOPE).put(ROOT_VARIABLE, root);
        _engine.getBindings(GremlinScriptContext.ENGINE_SCOPE).put(GRAPH_VARIABLE, _graphDb);
    }
}
