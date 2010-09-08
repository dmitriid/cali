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



package com.dmitriid.ji;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public abstract class AbstractErlangJavaConverter<J, E> implements Converter {

    private final Set<Class<?>> classes = new HashSet<Class<?>>();
    private final Class<J> javaClass;
    private final Class<E> erlangClass;
    private ArrayList<AbstractErlangJavaConverter> converter_chain;

    protected AbstractErlangJavaConverter(Class<J> javaClass, Class<E> erlangClass) {
        this.classes.add(javaClass);
        this.classes.add(erlangClass);
        this.javaClass = javaClass;
        this.erlangClass = erlangClass;
        this.converter_chain = new ArrayList<AbstractErlangJavaConverter>();
        this.converter_chain.add(this);
    }

    @Override
    public Set<Class<?>> getSupportedClasses() {
        return classes;
    }

    @Override
    public Class<?> getJavaClass() {
        return javaClass;
    }

    @Override
    public Class<?> getErlangClass() {
        return erlangClass;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <I, O> O convert(I in) throws IllegalArgumentException {
        Class<?> argClass = in.getClass();

        for(AbstractErlangJavaConverter c : converter_chain){
            try{
                // Exact match.
                if(c.getJavaClass().equals(argClass)) {
                    return (O) c.fromJava((J) in);
                } else if(c.getErlangClass().equals(argClass)) {
                    return (O) c.fromErlang((E) in);
                }

                // IS-A match.
                if(c.getJavaClass().isAssignableFrom(argClass)) {
                    return (O) fromJava((J) in);
                } else if(c.getErlangClass().isAssignableFrom(argClass)) {
                    return (O) fromErlang((E) in);
                }
            } catch(IllegalArgumentException ignored){
                
            }
        }
        throw new IllegalArgumentException(/* describe supported arguments and given one */);
    }

    public void add(AbstractErlangJavaConverter converter){
        converter_chain.add(converter);
    }

    protected abstract E fromJava(J in);

    protected abstract J fromErlang(E in);
}