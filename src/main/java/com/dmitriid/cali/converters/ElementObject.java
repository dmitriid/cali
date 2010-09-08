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

// Adapted from Rexter,
// see http://github.com/tinkerpop/rexster/blob/master/src/main/java/com/tinkerpop/rexster/traversals/ElementJSONObject.java

package com.dmitriid.cali.converters;

import com.dmitriid.cali.db.Tokens;
import com.tinkerpop.blueprints.pgm.Edge;
import com.tinkerpop.blueprints.pgm.Element;
import com.tinkerpop.blueprints.pgm.Vertex;
import org.neo4j.helpers.Pair;

import java.util.ArrayList;
import java.util.Set;

public class ElementObject implements Element {

    private ArrayList _properties;
    private Object _o;


    public ElementObject(Object o) {
        this(o, null);
    }

    public ElementObject(Object o, ArrayList properties) {
        _o = o;
        _properties = properties;
        if(o instanceof Pair){
            _o = ((Pair) o).first();
            _properties = (ArrayList)((Pair) o).other();
        }
    }

    public Object get(){
        if(_o instanceof Element){
            return get_properties();
        }

        return _o;
    }

    Object get_properties(){
        ArrayList properties = new ArrayList();
        if(null == _properties) {
            if(_o instanceof Edge){
                Edge edge = (Edge) _o;
                properties.add(new Pair("label", edge.getLabel()));
                properties.add(new Pair("inE", edge.getInVertex().getId()));
                properties.add(new Pair("outE", edge.getOutVertex().getId()));

                return new Pair("edge", properties);
            } else {
                Vertex vertex = (Vertex) _o;
                properties.add(new Pair("id", vertex.getId()));
                for(String key : vertex.getPropertyKeys()) {
                    properties.add(new Pair(key, vertex.getProperty(key)));
                }
            }
        } else {
            Element element = (Element) _o;
            for(Object k : _properties) {
                String key = (String) k;
                if(key.equals(Tokens._ID)) {
                    properties.add(new Pair("id", element.getId()));
                } else if(element instanceof Edge && (key.equals(Tokens._LABEL) ||key.equals("label"))) {
                    Edge edge = (Edge) element;
                    properties.add(new Pair("label", edge.getLabel()));
                } else if(element instanceof Edge && key.equals(Tokens._IN_V)) {
                    Edge edge = (Edge) element;
                    properties.add(new Pair("inV", edge.getInVertex().getId()));
                } else if(element instanceof Edge && key.equals(Tokens._OUT_V)) {
                    Edge edge = (Edge) element;
                    properties.add(new Pair("outE", edge.getOutVertex().getId()));
                } else {
                    Object temp = element.getProperty(key);
                    if(null != temp) {
                        properties.add(new Pair(key, temp));
                    }
                }
            }
        }
        if(_o instanceof Edge) {
            return new Pair("edge", properties);
        } else {
            return new Pair("vertex", properties);
        }
    }

    @Override
    public Object getProperty(String s) {
        return ((Element) _o).getProperty(s);
    }

    @Override
    public Set<String> getPropertyKeys() {
        return ((Element) _o).getPropertyKeys();
    }

    @Override
    public void setProperty(String s, Object o) {
        ((Element) _o).setProperty(s, o);
    }

    @Override
    public Object removeProperty(String s) {
        return ((Element) _o).removeProperty(s);
    }

    @Override
    public Object getId() {
        return ((Element) _o).getId();
    }
}