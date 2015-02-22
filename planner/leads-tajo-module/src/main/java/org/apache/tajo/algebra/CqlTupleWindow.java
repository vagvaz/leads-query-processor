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



public class CqlTupleWindow extends Expr {
    @Expose @SerializedName("CqlTupleWindow")
    private String exprStr;
    @Expose @SerializedName("rows")
    private int rows;

    public CqlTupleWindow(String str, int rows) {
        super(OpType.DataType);//super(OpType.CqlTupleWindow);
        this.exprStr = str;
        this.rows = rows;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public String getExprStr() {
        return exprStr + rows;
    }

    public int hashCode() {
        return exprStr.hashCode();
    }

    @Override boolean equalsTo(Expr expr) {
        if (expr instanceof CqlTupleWindow) {
            CqlTupleWindow another = (CqlTupleWindow) expr;
            return exprStr.equals(another.exprStr);
        }
        return false;
    }
}
