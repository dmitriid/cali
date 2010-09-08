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

import com.dmitriid.cali.converters.*;
import com.dmitriid.ji.ConversionManager;
import com.ericsson.otp.erlang.*;
import com.tinkerpop.blueprints.pgm.Element;
import com.tinkerpop.blueprints.pgm.Graph;
import com.tinkerpop.blueprints.pgm.Vertex;
import com.tinkerpop.gremlin.GremlinScriptEngine;
import com.tinkerpop.gremlin.GremlinScriptEngineFactory;
import org.neo4j.helpers.Pair;

import java.util.ArrayList;
import java.util.Stack;

public class DBConnector{

    protected static final String GREMLIN = "gremlin";
    protected static final String ROOT_VARIABLE = "$_";
    protected static final String GRAPH_VARIABLE = "$_g";
    protected static final String WILDCARD = "*";
    protected static final String ROOT = "root";
    protected static final String SCRIPT = "script";
    protected static final String RESULTS = "results";
    protected static final String RETURN_KEYS = "return_keys";

    Graph _graphDb = null;
    ConversionManager _cm = null;
    GremlinScriptEngine _engine = null;

    public DBConnector() {
        GremlinScriptEngineFactory f = new GremlinScriptEngineFactory();
        _engine = (GremlinScriptEngine) f.getScriptEngine();

        _cm = new ConversionManager();
        _cm.registerBasic();
        _cm.register(
                new ElementConverter(_cm),
                new PairConverter(_cm),
                new GraphConverter(),
                new ElementObjectConverter(_cm),
                new GPathConverter(_cm)
        );
    }

    public void shutdown() {
        if(null != _graphDb) _graphDb.shutdown();
        _graphDb = null;
    }

    public Object exec(final OtpErlangObject data) {
        Object request_data = _cm.convert(data);
        if(request_data instanceof ArrayList) {
            return exec((ArrayList) request_data);
        }

        return null;
    }

    public Object exec(final ArrayList data) {
        /*
            ElementList = [Element]
            ElementPropList = [ElementWithPropList]
            Element = ElementProper | ElementWithProplist
            ElementProper = {ElementType, Id}
            ElementWithProplist = {ElementType, Id, Proplist}

            ElementType = edge | vertex
            Id = integer()
            Proplist = proplist()

            EdgeDefinitions = [EdgeDefinition]
            EdgeDefinition = {Direction, Label}
            Direction = '<-' | '->' | '<->'
            Label  = string()

            Query = {q, Script}
            Script = string()

            CommandList = [Command]
            Command = SetCommand | GetCommand | DeleteCommand

            GetCommand = {get, Element} | {get, ElementList} | {get, Query}
            SetCommand = {set, ElementWithPropList} | {set, ElementPropList} | {set, EdgeDefinitions}
            DeleteCommand = ElementDeleteCommand | PropertyDeleteCommand
            ElementDeleteCommand = {delete, vertex, Id} | {delete, edge, Id}
            PropertyDeleteCommand = {delete_prop, vertex, id, [PropertyName]} | {delete_prop, edge, id, [PropertyName]}
            PropertyName = string()


            Example:

            {get, {vertex, 10}}
            {set, {vertex, 10, [{prop, "val"}]}}

            [
              {get, {vertex, 10}},
              {set, {'<-', "label1"}},
              {get, {vertex, 11}}
            ]


            [
              {get, [{vertex, 10}, {vertex, 12}]},
              {set, {'<->', "label2"}},
              {get, {vertex, 11}}
            ]

            [
              {get, {q, "$_/inE/outV"}},
              {set, {'->', "label3"}},
              {get, {q, "$_"}}
            ]
         */
        Stack stack = new Stack();

        boolean do_edge = false;

        for(Object request : data) {
            if(!(request instanceof ArrayList)) continue;
            if(((ArrayList) request).size() < 2) continue;

            ArrayList arr = (ArrayList) request;
            String op = (String) arr.get(0);

            if(op.equals("get")) {
                Object o = getElements(arr.get(1));
                if(null == o) continue;
                stack.push(o);
                if(do_edge) {
                    updateEdges(stack);
                    do_edge = false;
                }
            } else if(op.equals("set")) {
                Object o = setElements(arr.get(1));
                if(null == o) continue;

                if(o instanceof Pair) {
                    Pair p = (Pair) o;
                    if(p.first().equals("edge")) {
                        do_edge = true;
                        stack.push(p.other());
                    }
                } else {
                    stack.push(o);
                    if(do_edge) {
                        updateEdges(stack);
                        do_edge = false;
                    }
                }
            }
        }


        return stack.size() != 0 ? new ElementObject(stack.pop()) : null;
    }

    public Object getElements(Object request) {
        if(!(request instanceof ArrayList)) return null;
        if(((ArrayList) request).size() == 0) return null;

        ArrayList arr = (ArrayList) request;
        Object result = null;

        if(arr.get(0) instanceof ArrayList) {
            // [{edge, 10}, {vertex, 10, [name]}] etc.
            ArrayList result_arr = new ArrayList();
            for(Object o : arr) {
                ArrayList object = (ArrayList) o;
                Object local_result = null;

                String op = (String) object.get(0);
                if(op.equals("q")) {
                    local_result = query((String) object.get(1));
                } else if(op.equals("vertex") || op.equals("edge")) {
                    if(object.size() == 2) local_result = getElement(op, object.get(1));
                    else if(object.size() == 3) local_result = getElement(op, object.get(1), (ArrayList) object.get(2));
                }

                if(local_result instanceof ArrayList) {
                    for(Object arr_o : (ArrayList) local_result) {
                        result_arr.add(arr_o);
                    }
                } else if(null != local_result) result_arr.add(local_result);
            }
            result = result_arr;
        } else {
            // {edge, 10}
            // {vertex, 10, [name]} etc
            String op = (String) arr.get(0);
            if(op.equals("q")) {
                result = query((String) arr.get(1));
            } else if(op.equals("vertex") || op.equals("edge")) {
                if(arr.size() == 2) result = getElement(op, arr.get(1));
                else if(arr.size() == 3) result = getElement(op, arr.get(1), (ArrayList) arr.get(2));
            }

        }
        return result;
    }

    public Object setElements(Object request) {
        if(!(request instanceof ArrayList)) return null;
        if(((ArrayList) request).size() == 0) return null;

        ArrayList arr = (ArrayList) request;
        Object result = null;
        if(arr.get(0) instanceof ArrayList) {
            // [{'<-', "label"}, {'->', label2}]
            // [{vertex, [{prop, "val"}]}, {vertex, [{prop2, "label2"}]}] etc.
            String op = (String) ((ArrayList) arr.get(0)).get(0);
            if(op.equals("vertex")) {
                ArrayList local_result = new ArrayList();
                for(Object o : arr) {
                    Vertex v = addVertex((ArrayList) ((ArrayList) o).get(1));
                    if(null != v) local_result.add(v);
                }
                result = local_result;
            } else {
                result = new Pair<String, Object>("edge", arr);
            }
        } else {
            // {'<-', "label"}
            // {vertex, [{prop, "val"}]} etc.
            String op = (String) arr.get(0);
            if(op.equals("q")) {
                result = query((String) arr.get(1));
            } else if(op.equals("vertex")) {
                result = addVertex((ArrayList) arr.get(1));
            } else if(op.equals("<-") || op.equals("->") || op.equals("<->")) {
                result = new Pair<String, Object>("edge", arr);
            }
        }
        return result;
    }

    public Vertex addVertex(ArrayList props) {
        Vertex v = _graphDb.addVertex(null);
        for(Object p : props) {
            ArrayList prop = (ArrayList) p;
            v.setProperty((String) prop.get(0), prop.get(1));
        }

        return v;
    }

    public Object getElement(String op, Object id) {
        return getElement(op, id, null);
    }

    public Object getElement(String op, Object id, ArrayList proplist) {
        Element el;
        if(op.equals("vertex")) {
            el = _graphDb.getVertex(id);
        } else {
            el = _graphDb.getEdge(id);
        }

        if(null == el) return null;

        return proplist == null ? el : new Pair<Element, ArrayList>(el, proplist);
    }

    public Object query(String query) {
        try {
            return _engine.eval(query);
        } catch(Exception ignored) {

        }

        return null;
    }

    public void setProperties(Element el, OtpErlangList list) {
        for(OtpErlangObject object : list.elements()) {
            OtpErlangTuple tuple = (OtpErlangTuple) object;
            OtpErlangObject eo = tuple.elementAt(1);
            el.setProperty((String) _cm.convert(tuple.elementAt(0)), _cm.convert(eo));
        }
    }

    public String toString(OtpErlangAtom a) {
        return a.toString();
    }

    public String toString(OtpErlangString s) {
        return s.stringValue();
    }

    public String toString(OtpErlangObject o) {
        if(o instanceof OtpErlangString) return toString((OtpErlangString) o);
        if(o instanceof OtpErlangAtom) return toString((OtpErlangAtom) o);

        return o.toString();
    }

    private void updateEdges(Stack stack) {
        Object last_v = stack.empty() ? null : stack.pop();
        Object edge = stack.empty() ? null : stack.pop();
        Object previous_v = stack.empty() ? null : stack.pop();

        if(null == last_v || null == edge || null == previous_v) {
            if(null != previous_v) stack.push(previous_v);
            if(null != edge) stack.push(edge);
            if(null != last_v) stack.push(last_v);
            return;
        }

        if(!(previous_v instanceof Vertex) && !(previous_v instanceof ArrayList)) return;

        if(!(edge instanceof ArrayList)) {
            return;
        }

        Object e = null;

        if(last_v instanceof ArrayList) {
            for(Object o : (ArrayList) last_v) {
                if(o instanceof Vertex) {
                    e = addEdge((ArrayList) edge, (Vertex) o, previous_v);
                }
            }
        } else if(last_v instanceof Vertex) {
            e = addEdge((ArrayList) edge, (Vertex) last_v, previous_v);
        }

        stack.push(previous_v);
        //if(null != edge) stack.push(edge);
        if(null != e) stack.push(e);
        stack.push(last_v);

        //return e;
    }

    private Object addEdge(ArrayList edge, Vertex current_vertex, Object previous_vertex) {
        if(edge.get(0) instanceof ArrayList) {
            // [{'<-', "label"}, {'->', label2}]
            ArrayList result = new ArrayList();
            for(Object lo : edge) {
                ArrayList local_edge = (ArrayList) lo;

                String direction = (String) local_edge.get(0);
                String label = (String) local_edge.get(1);
                Object e = null;

                if(previous_vertex instanceof Vertex) {
                    e = addEdge(direction, current_vertex, (Vertex) previous_vertex, label);
                } else if(previous_vertex instanceof ArrayList) {
                    for(Object o : (ArrayList) previous_vertex) {
                        if(!(o instanceof Vertex)) continue;

                        e = addEdge(direction, current_vertex, (Vertex) o, label);
                    }
                }
                if(null != e) result.add(e);
            }
            return result;
        } else {

            // {'<-', "label"}

            String direction = (String) edge.get(0);
            String label = (String) edge.get(1);
            Object e = null;

            if(previous_vertex instanceof Vertex) {
                e = addEdge(direction, current_vertex, (Vertex) previous_vertex, label);
            } else if(previous_vertex instanceof ArrayList) {
                for(Object o : (ArrayList) previous_vertex) {
                    if(!(o instanceof Vertex)) continue;

                    e = addEdge(direction, current_vertex, (Vertex) o, label);
                }
            }

            return e;
        }
    }

    private Object addEdge(String direction, Vertex current_vertex, Vertex previous_vertex, String label) {
        Object e;
        if(current_vertex.equals(previous_vertex)) return null;
        if(direction.equals("<-")) {
            e = _graphDb.addEdge(null, current_vertex, previous_vertex, label);
        } else if(direction.equals("->")) {
            e = _graphDb.addEdge(null, previous_vertex, current_vertex, label);
        } else {
            ArrayList<Object> arr = new ArrayList();
            Element edge;

            edge = _graphDb.addEdge(null, previous_vertex, current_vertex, label);
            arr.add(edge);
            edge = _graphDb.addEdge(null, current_vertex, previous_vertex, label);
            arr.add(edge);

            e = arr;
        }

        return e;
    }

    public OtpErlangObject fromJava(Object o) {
        return _cm.convert(o);
    }
}
