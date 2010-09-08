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

public class ElementObjectConverter extends AbstractErlangJavaConverter<ElementObject, OtpErlangObject> {
    private ConversionManager _cm;

    public ElementObjectConverter(ConversionManager cm) {
        super(ElementObject.class, OtpErlangObject.class);
        _cm = cm;
    }

    public ElementObjectConverter() {
        super(ElementObject.class, OtpErlangObject.class);
        _cm = new ConversionManager();
    }

    @Override
    protected OtpErlangObject fromJava(ElementObject in) {
        return _cm.convert(in.get());
    }

    @Override
    protected ElementObject fromErlang(OtpErlangObject in) {
        return null; 
    }
}