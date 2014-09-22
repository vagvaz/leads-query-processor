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
package com.l2fprod.common.propertysheet;

import com.l2fprod.common.beans.ExtendedPropertyDescriptor;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

/**
 * PropertyDescriptorAdapter.<br>
 *
 */
@SuppressWarnings({"unchecked", "serial"})
class PropertyDescriptorAdapter extends AbstractProperty {

  private PropertyDescriptor descriptor;
  
  public PropertyDescriptorAdapter() {
    super();
  }
  
  public PropertyDescriptorAdapter(PropertyDescriptor descriptor) {
    this();
    setDescriptor(descriptor);
  }

  public void setDescriptor(PropertyDescriptor descriptor) {
    this.descriptor = descriptor;
  }
  
  public PropertyDescriptor getDescriptor() {
    return descriptor;
  }
  
  public String getName() {
    return descriptor.getName();
  }
  
  public String getDisplayName() {
    return descriptor.getDisplayName();
  }
  
  public String getShortDescription() {
    return descriptor.getShortDescription();
  }

  public Class getType() {
    return descriptor.getPropertyType();
  }

  public Object clone() {
    PropertyDescriptorAdapter clone = new PropertyDescriptorAdapter(descriptor);
    clone.setValue(getValue());
    return clone;
  }
  
  public void readFromObject(Object object) {
    try {
      Method method = descriptor.getReadMethod();
      if (method != null) {
        setValue(method.invoke(object, (Object[])null));
      }
    } catch (Exception e) {
      String message = "Got exception when reading property " + getName();
      if (object == null) {
        message += ", object was 'null'";
      } else {
        message += ", object was " + String.valueOf(object);
      }
      throw new RuntimeException(message, e);
    }
  }
  
  public void writeToObject(Object object) {
    try {
      Method method = descriptor.getWriteMethod();
      if (method != null) {
        method.invoke(object, new Object[]{getValue()});
      }
    } catch (Exception e) {
      String message = "Got exception when writing property " + getName();
      if (object == null) {
        message += ", object was 'null'";
      } else {
        message += ", object was " + String.valueOf(object);
      }
      throw new RuntimeException(message, e);
    }
  }
  
  public boolean isEditable() {
    return descriptor.getWriteMethod() != null;
  }

  public String getCategory() {
    if (descriptor instanceof ExtendedPropertyDescriptor) {
      return ((ExtendedPropertyDescriptor)descriptor).getCategory();
    } else {
      return null;
    }
  }
  
}
