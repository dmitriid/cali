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
import com.ericsson.otp.erlang.OtpErlangObject;
import com.ericsson.otp.erlang.OtpErlangTuple;
import com.tinkerpop.blueprints.pgm.Element;
import org.neo4j.helpers.Pair;

import java.util.ArrayList;


public class PairConverter extends AbstractErlangJavaConverter<Pair, OtpErlangTuple>{
    private ConversionManager _cm;

    public PairConverter() {
        this(new ConversionManager());
    }

    public PairConverter(ConversionManager cm){
        super(Pair.class, OtpErlangTuple.class);
        _cm = cm;
    }

    @Override
    protected OtpErlangTuple fromJava(Pair in) {
        if(null == in) return new OtpErlangTuple(new OtpErlangObject[]{});
        if(in.first() instanceof Element){
            return _cm.convert(new ElementObject(in.first(), (ArrayList)in.other()));
        }
        return new OtpErlangTuple(new OtpErlangObject[]{
                (OtpErlangObject)_cm.convert(in.first()),
                (OtpErlangObject)_cm.convert(in.other()) });
    }

    @Override
    protected Pair fromErlang(OtpErlangTuple in) {
        if(in.arity() == 2){
            return new Pair(_cm.convert(in.elementAt(0)), _cm.convert(in.elementAt(1)));    
        }
        return null;
    }
}
