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

package com.dmitriid.cali.db;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class DBConnectorFactory {
    public static DBConnector getConnector(String className, String[] args) {
        try {

            Class[] intArgsClass = new Class[]{args.getClass()};

            Object arglist[] = new Object[1];
            arglist[0] = args;

            Constructor intArgsConstructor;

            intArgsConstructor = Class.forName(className).getConstructor(intArgsClass);

            return (DBConnector) createObject(intArgsConstructor, arglist);
        } catch(ClassNotFoundException e) {
            System.out.println(e);
        } catch(NoSuchMethodException e) {
            System.out.println(e);
        }
        return null;
    }

    private static Object createObject(Constructor constructor, Object[] arguments) {

        System.out.println("Constructor: " + constructor.toString());
        Object object = null;

        try {
            object = constructor.newInstance(arguments);
            System.out.println("Object: " + object.toString());
            return object;
        } catch(InstantiationException e) {
            System.out.println(e);
        } catch(IllegalAccessException e) {
            System.out.println(e);
        } catch(IllegalArgumentException e) {
            System.out.println(e);
        } catch(InvocationTargetException e) {
            System.out.println(e);
            System.out.println(e.getMessage());
            e.printStackTrace();
            System.out.println(e.getCause());
        }
        return object;
    }
}
