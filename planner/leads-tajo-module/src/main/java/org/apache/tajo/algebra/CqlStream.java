/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.tajo.algebra;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CqlStream extends UnaryOperator implements JsonSerializable {
    @Expose
    @SerializedName("CqlStream")
    private Expr child;
    @Expose
    @SerializedName("exprStr")
    private String exprStr;


    public CqlStream(String str, Expr child) {
        super(OpType.DataType);//super(OpType.CqlStream);
        this.exprStr = str;
        this.child = child;
    }

    public Expr getChild() {
        return child;
    }

    public void setChild(Expr child) {
        this.child = child;
    }

    public String getExprStr() {
        return exprStr + child.toString();
    }

    @Override
    public String toJson() {
        return JsonHelper.toJson(this);
    }

    public int hashCode() {
        return exprStr.hashCode();
    }

    @Override boolean equalsTo(Expr expr) {
        if (expr instanceof CqlStream) {
            CqlStream another = (CqlStream) expr;
            return exprStr.equals(another.exprStr);
        }
        return false;
    }
}
