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

import java.util.Set;

public interface Converter {
    Set<Class<?>> getSupportedClasses();

    Class<?> getJavaClass();
    Class<?> getErlangClass();

    <I, O> O convert(I in) throws IllegalArgumentException;
}