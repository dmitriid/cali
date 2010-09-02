package com.dmitriid.cali.converters;

import com.dmitriid.ji.AbstractErlangJavaConverter;
import com.ericsson.otp.erlang.OtpErlangAtom;
import com.ericsson.otp.erlang.OtpErlangObject;
import com.ericsson.otp.erlang.OtpErlangString;
import com.ericsson.otp.erlang.OtpErlangTuple;
import com.tinkerpop.blueprints.pgm.Graph;

/**
 * Author: dmitriid
 * Date: Aug 24, 2010
 * Time: 10:29:57 PM
 */
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
