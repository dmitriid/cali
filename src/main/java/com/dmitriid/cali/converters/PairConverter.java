package com.dmitriid.cali.converters;

import com.dmitriid.ji.AbstractErlangJavaConverter;
import com.dmitriid.ji.ConversionManager;
import com.ericsson.otp.erlang.OtpErlangObject;
import com.ericsson.otp.erlang.OtpErlangTuple;
import com.tinkerpop.blueprints.pgm.Element;
import org.neo4j.helpers.Pair;

import java.util.ArrayList;

/**
 * Author: dmitriid
 * Date: Aug 23, 2010
 * Time: 3:36:01 PM
 */
public class PairConverter extends AbstractErlangJavaConverter<Pair, OtpErlangTuple>{
    private ConversionManager _cm;

    public PairConverter() {
        this(new ConversionManager());
    }

    public PairConverter(ConversionManager cm){
        super(Pair.class, OtpErlangTuple.class);
        _cm = cm;
    }

    @Override
    protected OtpErlangTuple fromJava(Pair in) {
        if(null == in) return new OtpErlangTuple(new OtpErlangObject[]{});
        if(in.first() instanceof Element){
            return _cm.convert(new ElementObject((Element)in.first(), (ArrayList)in.other()));
        }
        return new OtpErlangTuple(new OtpErlangObject[]{
                (OtpErlangObject)_cm.convert(in.first()),
                (OtpErlangObject)_cm.convert(in.other()) });
    }

    @Override
    protected Pair fromErlang(OtpErlangTuple in) {
        if(in.arity() == 2){
            return new Pair(_cm.convert(in.elementAt(0)), _cm.convert(in.elementAt(1)));    
        }
        return null;
    }
}
