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

package com.dmitriid.cali;

import com.dmitriid.cali.db.DBConnector;
import com.dmitriid.cali.db.DBConnectorFactory;
import com.ericsson.otp.erlang.*;
import org.neo4j.helpers.Pair;

import java.io.IOException;


public class Main {

    private String _name = "cali@localhost";
    private String _mbox_name = "mbox";
    private String _cookie = null;
    private String _connector = "com.dmitriid.cali.db.Neo4JConnector";

    public static void main(String[] args) {
        new Main().doMain(args);
    }

    private void doMain(String[] args){

        CommandLine line = new CommandLine(args);

        String name = line.optionValue("n", "name");
        String mbox_name = line.optionValue("m", "mbox");
        String cookie = line.optionValue("c", "cookie");
        String connector = line.optionValue("connector");

        _name      = name      != null ? name      : _name;
        _mbox_name = mbox_name != null ? mbox_name : _mbox_name;
        _cookie    = cookie    != null ? cookie    : _cookie;
        _connector = connector != null ? connector : _connector;

        DBConnector db = null;

        try{
            db = DBConnectorFactory.getConnector(_connector, args);
        }catch(Exception e){
            System.out.println(e.getMessage());
            System.exit(2);
        }

        OtpNode self = null;
        OtpMbox mbox = null;

        try {
            self = new OtpNode(_name);
            mbox = self.createMbox(_mbox_name);
        } catch(IOException e) {
            System.err.println(e.getMessage());
            System.exit(2);
        }

        if(_cookie != null) {
            self.setCookie(_cookie);
        }

        OtpErlangObject o;
        OtpErlangTuple msg;
        OtpErlangPid from;

        while(true) {
            try {
                o = mbox.receive();

                if(o instanceof OtpErlangTuple) {
                    msg = (OtpErlangTuple) o;
                    from = (OtpErlangPid) (msg.elementAt(0));

                    OtpErlangObject data = msg.elementAt(1);
                    if(data instanceof OtpErlangList) {
                        OtpErlangObject out;
                        try {
                            Object e = db.exec(data);
                            out = db.fromJava(e);
                        } catch(Exception e) {
                            System.out.println(e);
                            out = db.fromJava(new Pair("error", e.getMessage()));
                        }
                        mbox.send(from, out);
                    } else {
                        mbox.send(from, msg.elementAt(1));
                    }

                    if(msg.elementAt(1).toString().equals("exit")) break;
                }
            } catch(Exception e) {
                System.out.println("" + e);
            }
        }

        db.shutdown();
    }
}
