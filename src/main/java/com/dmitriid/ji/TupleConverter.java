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

import com.ericsson.otp.erlang.OtpErlangObject;
import com.ericsson.otp.erlang.OtpErlangTuple;

import java.util.ArrayList;

public class TupleConverter extends AbstractErlangJavaConverter<ArrayList, OtpErlangTuple>{
    private ConversionManager _cm;

    public TupleConverter() {
        this(new ConversionManager());
    }

    public TupleConverter(ConversionManager cm){
        super(ArrayList.class, OtpErlangTuple.class);
        _cm = cm;
    }

    @Override
    protected OtpErlangTuple fromJava(ArrayList in) {
        return null;
    }

    @Override
    protected ArrayList fromErlang(OtpErlangTuple in) {
        ArrayList arr = new ArrayList();
        for(OtpErlangObject o : in.elements()){
            arr.add(_cm.convert(o));
        }
        return arr;
    }
}
