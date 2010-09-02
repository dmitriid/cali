package com.dmitriid.cali;

/**
 * Created by IntelliJ IDEA.
 * User: dmitriid
 * Date: Jul 20, 2010
 * Time: 8:46:23 PM
 * To change this template use File | Settings | File Templates.
 */

import com.dmitriid.ji.ConversionManager;
import com.dmitriid.cali.converters.ElementObject;
import com.ericsson.otp.erlang.*;
import jargs.gnu.CmdLineParser;
import org.neo4j.graphdb.*;
import org.neo4j.helpers.Pair;

import java.io.IOException;
import java.util.ArrayList;


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
        CmdLineParser.Option d = parser.addStringOption('d', "db_path");

        try {
            parser.parse(args);
        } catch(CmdLineParser.OptionException e) {
            System.err.println(e.getMessage());
            System.exit(2);
        }

        String name_value = (String) parser.getOptionValue(n);
        String mbox_value = (String) parser.getOptionValue(m);
        String cookie_value = (String) parser.getOptionValue(c);
        String db_path = (String) parser.getOptionValue(d);


        Neo4J db = new Neo4J(db_path);



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
        ElementObject elementObject = new ElementObject(null);

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
                        OtpErlangObject out = null;
                        try{
                            Object e = db.exec(data);
                            out = db.fromJava(e);
                        } catch(Exception e){
                            System.out.println(e);
                            out = db.fromJava(new Pair("error", e.getMessage()));
                        }
                        mbox.send(from, out);;
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
