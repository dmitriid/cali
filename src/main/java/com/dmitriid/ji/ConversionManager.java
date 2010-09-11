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

//
// Original code by Denis Zhdanov, see http://rsdn.ru/forum/java/3929756.1.aspx
//

package com.dmitriid.ji;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ConversionManager {

    private final Map<Class<?>, AbstractErlangJavaConverter> converters = new HashMap<Class<?>, AbstractErlangJavaConverter>();

    @SuppressWarnings("unchecked")
    public <I, O> O convert(I in) throws IllegalArgumentException {
        if(null == in){
            return (O)do_conversion(new NullObject());
        }

        return (O)do_conversion(in);
    }

    public <I, O> O do_conversion(I in) throws IllegalArgumentException {
        // Try exact match.
        Converter converter = converters.get(in.getClass());
        if(converter != null) {
            return converter.<I, O>convert(in);
        }

        // Try IS-A match.
        for(Map.Entry<Class<?>, AbstractErlangJavaConverter> entry : converters.entrySet()) {
            if(entry.getKey().isAssignableFrom(in.getClass())) {
                return (O) entry.getValue().convert(in);
            }
        }

        throw new IllegalArgumentException(/* describe supported arguments and given one */);
    }

        //@Autowired

    public void register(AbstractErlangJavaConverter... converters) {
        for(AbstractErlangJavaConverter converter : converters) {
            Set<Class<?>> clazzes = converter.getSupportedClasses();
            for(Class<?> clazz : clazzes) {
                if(this.converters.containsKey(clazz)) {
                    this.converters.get(clazz).add(converter);
                } else
                    this.converters.put(clazz, converter);
            }
        }
    }

    public void registerBasic(){
        register(
                new IntegerConverter(),
                new LongConverter(),
                new FloatConverter(),
                new DoubleConverter(),

                new StringConverter(),
                new BinaryConverter(),
                new AtomConverter(),
                new ByteArrayConverter(),

                new ArrayListConverter(this),
                new TupleConverter(this),

                new NullObjectConverter()
        );
    }
}