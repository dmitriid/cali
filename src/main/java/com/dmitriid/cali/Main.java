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
import jargs.gnu.CmdLineParser;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.helpers.Pair;

import java.io.IOException;


public class Main {

    private static final String GREMLIN = "gremlin";
    private static final String ROOT_VARIABLE = "$_";
    private static final String GRAPH_VARIABLE = "$_g";
    private static final String WILDCARD = "*";
    private static final String ROOT = "root";
    private static final String SCRIPT = "script";
    private static final String RESULTS = "results";
    private static final String RETURN_KEYS = "return_keys";
    
    public enum MyRelationshipTypes implements RelationshipType {
        KNOWS
    }

    public static void main(String[] args) {

        CmdLineParser parser = new CmdLineParser();

        CmdLineParser.Option n = parser.addStringOption('n', "name");
        CmdLineParser.Option m = parser.addStringOption('m', "mbox");
        CmdLineParser.Option c = parser.addStringOption('c', "cookie");
        CmdLineParser.Option cl = parser.addStringOption("connector");

        try {
            parser.parse(args);
        } catch(CmdLineParser.OptionException e) {
            System.err.println(e.getMessage());
            System.exit(2);
        }

        String name_value = (String) parser.getOptionValue(n);
        String mbox_value = (String) parser.getOptionValue(m);
        String cookie_value = (String) parser.getOptionValue(c);
        String className = (String) parser.getOptionValue(cl);

        if(className == null) className = "com.dmitriid.cali.db.Neo4JConnector";

        DBConnector db = DBConnectorFactory.getConnector(className, args);


        OtpNode self = null;
        OtpMbox mbox = null;
        
        try{
            self = new OtpNode(name_value != null ? name_value : "cali@localhost");
            mbox = self.createMbox(mbox_value != null ? mbox_value : "mbox");
        } catch(IOException e){
            System.err.println(e.getMessage());
            System.exit(2);
        }

        if(cookie_value != null) {
            self.setCookie(cookie_value);
        }

        OtpErlangObject o;
        OtpErlangTuple msg;
        OtpErlangPid from;

        while(true) {
            try {
                o = mbox.receive();
                System.out.println("Received something.");

                if(o instanceof OtpErlangTuple) {
                    msg = (OtpErlangTuple) o;
                    from = (OtpErlangPid) (msg.elementAt(0));


                    System.out.println("Echoing back...");


                    OtpErlangObject data = msg.elementAt(1);
                    if(data instanceof OtpErlangList) {
                        OtpErlangObject out  ;
                        try{
                            Object e = db.exec(data);
                            out = db.fromJava(e);
                        } catch(Exception e){
                            System.out.println(e);
                            out = db.fromJava(new Pair("error", e.getMessage()));
                        }
                        mbox.send(from, out);
                    }else{
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
