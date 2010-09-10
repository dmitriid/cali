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

package com.dmitriid.cali.converters;

import com.dmitriid.ji.AbstractErlangJavaConverter;
import com.dmitriid.ji.ConversionManager;
import com.ericsson.otp.erlang.OtpErlangList;
import com.ericsson.otp.erlang.OtpErlangObject;
import com.tinkerpop.gremlin.compiler.types.GPath;

import java.util.ArrayList;


public class GPathConverter extends AbstractErlangJavaConverter <GPath, OtpErlangList>{
    private ConversionManager _cm = null;
    public GPathConverter() {
        this(new ConversionManager());
    }

    public GPathConverter(ConversionManager cm){
        super(GPath.class, OtpErlangList.class);
        _cm = cm;
    }

    @Override
    protected OtpErlangList fromJava(GPath in) {
        ArrayList<OtpErlangObject> arr = new ArrayList();

        for(Object o : (Iterable) in.getValue()){
            arr.add((OtpErlangObject) _cm.convert(o));
        }

        return new OtpErlangList(arr.toArray(new OtpErlangObject[]{}));
    }

    @Override
    protected GPath fromErlang(OtpErlangList in) {
        return null;  
    }
}
