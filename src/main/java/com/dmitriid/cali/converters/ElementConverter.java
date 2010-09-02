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
