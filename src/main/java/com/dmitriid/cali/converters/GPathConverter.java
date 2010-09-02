package com.dmitriid.cali.converters;

import com.dmitriid.ji.AbstractErlangJavaConverter;
import com.dmitriid.ji.ConversionManager;
import com.ericsson.otp.erlang.OtpErlangList;
import com.ericsson.otp.erlang.OtpErlangObject;
import com.tinkerpop.gremlin.compiler.types.GPath;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Author: dmitriid
 * Date: Aug 26, 2010
 * Time: 10:58:16 AM
 */
public class GPathConverter extends AbstractErlangJavaConverter <GPath, OtpErlangList>{
    ConversionManager _cm = null;
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
