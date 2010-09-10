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

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandLine {
    private HashMap<String, String> _options = new HashMap<String, String>();

    private Pattern _optionMatch = Pattern.compile("^[-]{1,2}(\\w+)");  // handle -o and --opt and -opt

    public CommandLine(String[] args) {
        String optionName = null;
        Matcher matcher;

        for(String o : args) {
            matcher = _optionMatch.matcher(o);
            if(matcher.find()) {
                if(optionName != null) { // in case there was an option without a parameter before this one
                    _options.put(matcher.group(1), null);
                    optionName = null;
                }
                optionName = matcher.group(1);
            } else {
                if(optionName != null) {
                    _options.put(optionName, o);
                }

                optionName = null;
            }
        }
    }

    public boolean hasOption(String... keys) {
        for(String key : keys) {
            if(_options.containsKey(key)) {
                return true;
            }
        }
        return false;
    }

    public String optionValue(String... options){  
        for(String option : options){
            if(_options.containsKey(option)){
                return _options.get(option);
            }
        }

        return null;
    }
}
