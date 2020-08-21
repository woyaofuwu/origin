
package com.asiainfo.veris.crm.order.soa.frame.bcf.callpf.auto.expression.op.define;

import com.asiainfo.veris.crm.order.soa.frame.bcf.callpf.auto.expression.IllegalExpressionException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.callpf.auto.expression.datameta.BaseDataMeta;
import com.asiainfo.veris.crm.order.soa.frame.bcf.callpf.auto.expression.datameta.Constant;
import com.asiainfo.veris.crm.order.soa.frame.bcf.callpf.auto.expression.datameta.Reference;
import com.asiainfo.veris.crm.order.soa.frame.bcf.callpf.auto.expression.op.IOperatorExecution;
import com.asiainfo.veris.crm.order.soa.frame.bcf.callpf.auto.expression.op.Operator;

/**
 * 逻辑非操作
 */
public class Op_NOT implements IOperatorExecution
{

    public static final Operator THIS_OPERATOR = Operator.NOT;

    public Constant execute(Constant[] args) throws IllegalExpressionException
    {

        if (args == null || args.length != 1)
        {
            throw new IllegalArgumentException("操作符\"" + THIS_OPERATOR.getToken() + "参数个数不匹配");
        }

        Constant first = args[0];
        if (null == first || null == first.getDataValue())
        {
            // 抛NULL异常
            throw new NullPointerException("操作符\"" + THIS_OPERATOR.getToken() + "\"参数为空");
        }

        // 如果第一参数为引用，则执行引用
        if (first.isReference())
        {
            Reference firstRef = (Reference) first.getDataValue();
            first = firstRef.execute();
        }

        if (BaseDataMeta.DataType.DATATYPE_BOOLEAN == first.getDataType())
        {
            Boolean result = !first.getBooleanValue();
            return new Constant(BaseDataMeta.DataType.DATATYPE_BOOLEAN, result);

        }
        else
        {
            // 抛异常
            throw new IllegalArgumentException("操作符\"" + THIS_OPERATOR.getToken() + "\"参数类型错误");

        }

    }

    public Constant verify(int opPositin, BaseDataMeta[] args) throws IllegalExpressionException
    {

        if (args == null)
        {
            throw new IllegalArgumentException("运算操作符参数为空");
        }
        if (args.length != 1)
        {
            // 抛异常
            throw new IllegalExpressionException("操作符\"" + THIS_OPERATOR.getToken() + "\"参数个数不匹配", THIS_OPERATOR.getToken(), opPositin);
        }

        BaseDataMeta first = args[0];
        if (first == null)
        {
            throw new NullPointerException("操作符\"" + THIS_OPERATOR.getToken() + "\"参数为空");
        }

        if (BaseDataMeta.DataType.DATATYPE_BOOLEAN == first.getDataType())
        {
            return new Constant(BaseDataMeta.DataType.DATATYPE_BOOLEAN, Boolean.FALSE);

        }
        else
        {
            // 抛异常
            throw new IllegalExpressionException("操作符\"" + THIS_OPERATOR.getToken() + "\"参数类型错误", THIS_OPERATOR.getToken(), opPositin);

        }
    }
}
