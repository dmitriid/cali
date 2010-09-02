package com.dmitriid.cali.converters;

import com.dmitriid.cali.Tokens;
import com.tinkerpop.blueprints.pgm.Edge;
import com.tinkerpop.blueprints.pgm.Element;
import com.tinkerpop.blueprints.pgm.Vertex;
import org.neo4j.helpers.Pair;

import java.util.ArrayList;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: dmitriid
 * Date: Aug 9, 2010
 * Time: 8:21:06 PM
 * To change this template use File | Settings | File Templates.
 */
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
            Object o = get_properties();
            return o;
        }

        return _o;
    }

    public Object get_properties(){
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