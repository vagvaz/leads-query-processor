/*
_______________________
Apatar Open Source Data Integration
Copyright (C) 2005-2007, Apatar, Inc.
info@apatar.com
195 Meadow St., 2nd Floor
Chicopee, MA 01013

### This program is free software; you can redistribute it and/or modify
### it under the terms of the GNU General Public License as published by
### the Free Software Foundation; either version 2 of the License, or
### (at your option) any later version.

### This program is distributed in the hope that it will be useful,
### but WITHOUT ANY WARRANTY; without even the implied warranty of
### MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.# See the
### GNU General Public License for more details.

### You should have received a copy of the GNU General Public License along
### with this program; if not, write to the Free Software Foundation, Inc.,
### 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
________________________

*/

/**
 * $ $ License.
 *
 * Copyright $ L2FProd.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.l2fprod.common.util.converter;

/**
 * Converts a boolean to string and vice-versa.
 */
public class BooleanConverter implements Converter {

  public void register(ConverterRegistry registry) {
    registry.addConverter(String.class, boolean.class, this);
    registry.addConverter(String.class, Boolean.class, this);
    registry.addConverter(Boolean.class, String.class, this);
    registry.addConverter(boolean.class, String.class, this);
  }
  
  public Object convert(Class type, Object value) {
    if (String.class.equals(type) && Boolean.class.equals(value.getClass())) {
      return String.valueOf(value);
    } else if (boolean.class.equals(type) || Boolean.class.equals(type)) {
      return Boolean.valueOf(String.valueOf(value));
    } else {
      throw new IllegalArgumentException("Can't convert " + value + " to "
        + type.getName());
    }
  }
  
}
