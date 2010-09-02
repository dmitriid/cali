package com.dmitriid.cali;

import com.dmitriid.ji.ConversionManager;
import com.ericsson.otp.erlang.*;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: dmitriid
 * Date: Aug 20, 2010
 * Time: 5:27:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class ErlangObject {

    protected Object _o = null;
    protected ConversionManager _cm = null;

    public ErlangObject(Object o) {
        this(o, new ConversionManager());
    }

    public ErlangObject(Object o, ConversionManager cm) {
        _o = o;
        _cm = cm;
        _cm.registerBasic();
    }

    public OtpErlangObject get(){
        return encode(_o);
    }

    public OtpErlangObject encode(Object o){
        if(null == o) return null;
        
        return _cm.convert(o);
    }
}
