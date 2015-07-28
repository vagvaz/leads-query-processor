package leads.tajo.catalog; /**
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


import org.apache.tajo.catalog.CatalogUtil;
import org.apache.tajo.catalog.Column;
import org.apache.tajo.common.TajoDataTypes.DataType;
import org.apache.tajo.common.TajoDataTypes.Type;
import org.apache.tajo.datum.Datum;
import org.apache.tajo.datum.DatumFactory;
import org.apache.tajo.engine.function.annotation.Description;
import org.apache.tajo.engine.function.annotation.ParamTypes;
import org.apache.tajo.plan.function.AggFunction;
import org.apache.tajo.plan.function.FunctionContext;
import org.apache.tajo.storage.Tuple;

/**
 * Function definition
 *
 * INT4 sum(value INT4)
 */
@Description(
        functionName = "sum",
        description = "the sum of a set of numbers",
        example = "> SELECT sum(expr);",
        returnType = Type.INT8,
        paramTypes = {@ParamTypes(paramTypes = {Type.INT4})}
)
public class SumInt2 extends AggFunction<Datum> {

    public SumInt2() {
        super(new Column[] {
                new Column("expr", Type.INT4)
        });
    }

    @Override
    public SumIntContext newContext() {
        return new SumIntContext();
    }

    @Override
    public void eval(FunctionContext ctx, Tuple params) {
        SumIntContext sumCtx = (SumIntContext) ctx;
        sumCtx.sum += params.get(0).asInt8();
    }

    @Override
    public Datum getPartialResult(FunctionContext ctx) {
        return DatumFactory.createInt8(((SumIntContext) ctx).sum);
    }

    @Override
    public DataType getPartialResultType() {
        return CatalogUtil.newSimpleDataType(Type.INT8);
    }

    @Override
    public Datum terminate(FunctionContext ctx) {
        return DatumFactory.createInt8(((SumIntContext) ctx).sum);
    }

    private class SumIntContext implements FunctionContext {
        long sum;
    }
}
