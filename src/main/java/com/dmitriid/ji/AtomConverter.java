package com.dmitriid.ji;

import com.ericsson.otp.erlang.OtpErlangAtom;

public class AtomConverter extends AbstractErlangJavaConverter<String, OtpErlangAtom>{

    public AtomConverter() {
        super(String.class, OtpErlangAtom.class);
    }

    @Override
    protected OtpErlangAtom fromJava(String in) {
        return new OtpErlangAtom(in);
    }

    @Override
    protected String fromErlang(OtpErlangAtom in) {
        return in.atomValue();
    }
}
