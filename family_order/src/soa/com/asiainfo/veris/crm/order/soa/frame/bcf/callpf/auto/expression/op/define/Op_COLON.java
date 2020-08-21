
package com.asiainfo.veris.crm.order.soa.frame.bcf.callpf.auto.expression.op.define;

import com.asiainfo.veris.crm.order.soa.frame.bcf.callpf.auto.expression.IllegalExpressionException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.callpf.auto.expression.datameta.BaseDataMeta;
import com.asiainfo.veris.crm.order.soa.frame.bcf.callpf.auto.expression.datameta.Constant;
import com.asiainfo.veris.crm.order.soa.frame.bcf.callpf.auto.expression.op.IOperatorExecution;
import com.asiainfo.veris.crm.order.soa.frame.bcf.callpf.auto.expression.op.Operator;

/**

 */
public class Op_COLON implements IOperatorExecution
{

    public static final Operator THIS_OPERATOR = Operator.COLON;

    /*
     * (non-Javadoc)
     * @see
     * com.ailk.csservice.common.callpf.auto.expression.op.IOperatorExecution#execute(com.ailk.csservice.common.callpf
     * .auto.expression.datameta.Constant[])
     */
    public Constant execute(Constant[] args)
    {
        throw new UnsupportedOperationException("操作符\"" + THIS_OPERATOR.getToken() + "不支持该方法");
    }

    /*
     * (non-Javadoc)
     * @see com.ailk.csservice.common.callpf.auto.expression.op.IOperatorExecution#verify(int,
     * com.ailk.csservice.common.callpf.auto.expression.datameta.BaseDataMeta[])
     */
    public Constant verify(int opPositin, BaseDataMeta[] args) throws IllegalExpressionException
    {
        throw new UnsupportedOperationException("操作符\"" + THIS_OPERATOR.getToken() + "不支持该方法");
    }

}
