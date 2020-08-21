
package com.asiainfo.veris.crm.order.soa.frame.bcf.callpf.auto.expression.op;

import com.asiainfo.veris.crm.order.soa.frame.bcf.callpf.auto.expression.IllegalExpressionException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.callpf.auto.expression.datameta.BaseDataMeta;
import com.asiainfo.veris.crm.order.soa.frame.bcf.callpf.auto.expression.datameta.Constant;

/**
 * 操作符执行接口
 */
public interface IOperatorExecution
{
    /**
     * 执行操作符运算
     * 
     * @param args
     *            注意args中的参数由于是从栈中按LIFO顺序弹出的，所以必须从尾部倒着取数
     * @return Constant 常量型的执行结果
     * @throws IllegalExpressionException
     */
    public Constant execute(Constant[] args) throws IllegalExpressionException;

    /**
     * 验证操作符参数是否合法
     * 
     * @param opPositin
     * @param args
     *            注意args中的参数由于是从栈中按LIFO顺序弹出的，所以必须从尾部倒着取数
     * @return Constant 常量型的执行结果
     * @throws IllegalExpressionException
     */
    public Constant verify(int opPositin, BaseDataMeta[] args) throws IllegalExpressionException;
}
