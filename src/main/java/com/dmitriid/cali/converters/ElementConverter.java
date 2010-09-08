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

import com.dmitriid.ji.*;
import com.ericsson.otp.erlang.OtpErlangObject;
import com.ericsson.otp.erlang.OtpErlangTuple;
import com.tinkerpop.blueprints.pgm.Element;
import com.tinkerpop.blueprints.pgm.Vertex;


public class ElementConverter extends AbstractErlangJavaConverter<Element, OtpErlangTuple> {
    private ConversionManager _cm;

    public ElementConverter() {
        super(Element.class, OtpErlangTuple.class);
        _cm = new ConversionManager();
    }

    public ElementConverter(ConversionManager cm) {
        super(Element.class, OtpErlangTuple.class);
        _cm = cm;
    }

    @Override
    public OtpErlangTuple fromJava(Element in) {
        if(null == in) return new OtpErlangTuple(new OtpErlangObject[]{});
        return (OtpErlangTuple)_cm.convert(new ElementObject(in).get());
    }

    @Override
    public Vertex fromErlang(OtpErlangTuple in) {
        return null;
    }
}
