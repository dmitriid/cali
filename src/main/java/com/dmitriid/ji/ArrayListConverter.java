//
// @author Dmitrii Dimandt <dmitrii@dmitriid.com>
// @copyright 2010 Dmitrii Dimandt
//
//
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

import com.ericsson.otp.erlang.OtpErlangList;
import com.ericsson.otp.erlang.OtpErlangObject;

import java.util.ArrayList;

public class ArrayListConverter extends AbstractErlangJavaConverter<ArrayList, OtpErlangList> {
    private ConversionManager _cm;

    public ArrayListConverter() {
        super(ArrayList.class, OtpErlangList.class);
        _cm = new ConversionManager();
    }

    public ArrayListConverter(ConversionManager cm) {
        super(ArrayList.class, OtpErlangList.class);
        _cm = cm;
    }

    @Override
    protected OtpErlangList fromJava(ArrayList in) {
        ArrayList<OtpErlangObject> arr = new ArrayList<OtpErlangObject>();

        if(null == in) return new OtpErlangList(arr.toArray(new OtpErlangObject[]{}));

        for(Object o : in){
            arr.add((OtpErlangObject)_cm.convert(o));
        }

        return new OtpErlangList(arr.toArray(new OtpErlangObject[]{}));
    }

    @Override
    protected ArrayList fromErlang(OtpErlangList in) {
        OtpErlangObject[] arr = in.elements();
        ArrayList l = new ArrayList();
        for(OtpErlangObject o : arr){
            l.add(_cm.convert(o));
        }
        return l;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
