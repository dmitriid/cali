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

import com.ericsson.otp.erlang.OtpErlangInt;
import com.ericsson.otp.erlang.OtpErlangRangeException;

public class IntegerConverter extends AbstractErlangJavaConverter<Integer, OtpErlangInt> {

    public IntegerConverter() {
        super(Integer.class, OtpErlangInt.class);
    }

    @Override
    protected OtpErlangInt fromJava(Integer in) {
        if(null == in) in = 0;
        return new OtpErlangInt(in);
    }

    @Override
    protected Integer fromErlang(OtpErlangInt in) {
        try {
            return in.intValue();
        } catch(OtpErlangRangeException e) {
            //e.printStackTrace();

            return null;
        }
    }
}
