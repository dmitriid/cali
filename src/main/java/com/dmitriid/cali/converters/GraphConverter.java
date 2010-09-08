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
import com.ericsson.otp.erlang.OtpErlangAtom;
import com.ericsson.otp.erlang.OtpErlangObject;
import com.ericsson.otp.erlang.OtpErlangString;
import com.ericsson.otp.erlang.OtpErlangTuple;
import com.tinkerpop.blueprints.pgm.Graph;


public class GraphConverter extends AbstractErlangJavaConverter<Graph, OtpErlangTuple> {
    public GraphConverter() {
        super(Graph.class, OtpErlangTuple.class);
    }

    @Override
    protected OtpErlangTuple fromJava(Graph in) {
        return new OtpErlangTuple(new OtpErlangObject[]{
                new OtpErlangAtom("graph"),
                new OtpErlangString(in.toString())
        });
    }

    @Override
    protected Graph fromErlang(OtpErlangTuple in) {
        return null;
    }
}
