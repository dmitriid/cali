package com.dmitriid.cali.converters;

import com.dmitriid.ji.*;
import com.ericsson.otp.erlang.OtpErlangObject;
import com.ericsson.otp.erlang.OtpErlangTuple;
import com.tinkerpop.blueprints.pgm.Element;
import com.tinkerpop.blueprints.pgm.Vertex;

/**
 * Author: dmitriid
 * Date: Aug 23, 2010
 * Time: 3:20:58 PM
 */
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