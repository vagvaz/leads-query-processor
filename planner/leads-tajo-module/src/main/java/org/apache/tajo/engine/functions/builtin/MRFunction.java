package org.apache.tajo.engine.functions.builtin;

import org.apache.tajo.catalog.proto.CatalogProtos;
import org.apache.tajo.common.TajoDataTypes;
import org.apache.tajo.datum.Datum;
import org.apache.tajo.datum.DatumFactory;
import org.apache.tajo.engine.function.GeneralFunction;
import org.apache.tajo.engine.function.annotation.Description;
import org.apache.tajo.engine.function.annotation.ParamTypes;
import org.apache.tajo.storage.Tuple;

/**
 * Created by tr on 18/11/2014.
 */


    /**
     * Function definition
     *
     * INT4 sum(value INT4)
     */
    @Description(
            functionName = "mr_function",
            description = "bourdes bourdes",
            example = "> SELECT MR_FUNCTION(expr);",
            returnType = TajoDataTypes.Type.INT4,
            paramTypes = {@ParamTypes(paramTypes = {})}
    )
    public class MRFunction extends GeneralFunction {

        public MRFunction() {
            super(NoArgs);
        }

        @Override
        public Datum eval(Tuple params) {
//            System.out.print("Parameter 0: " + params.get(0));
//            System.out.print("Parameter 1: " + params.get(1));
           // System.out.println("mr_function");
           // return null;
            return DatumFactory.createInt4(0);
        }
        @Override
        public CatalogProtos.FunctionType getFunctionType(){return CatalogProtos.FunctionType.UDF;}
    }


