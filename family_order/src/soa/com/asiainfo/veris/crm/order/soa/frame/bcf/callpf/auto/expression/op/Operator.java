
package com.asiainfo.veris.crm.order.soa.frame.bcf.callpf.auto.expression.op;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import com.asiainfo.veris.crm.order.soa.frame.bcf.callpf.auto.expression.IllegalExpressionException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.callpf.auto.expression.datameta.BaseDataMeta;
import com.asiainfo.veris.crm.order.soa.frame.bcf.callpf.auto.expression.datameta.Constant;
import com.asiainfo.veris.crm.order.soa.frame.bcf.callpf.auto.expression.op.define.Op_AND;
import com.asiainfo.veris.crm.order.soa.frame.bcf.callpf.auto.expression.op.define.Op_APPEND;
import com.asiainfo.veris.crm.order.soa.frame.bcf.callpf.auto.expression.op.define.Op_COLON;
import com.asiainfo.veris.crm.order.soa.frame.bcf.callpf.auto.expression.op.define.Op_DIV;
import com.asiainfo.veris.crm.order.soa.frame.bcf.callpf.auto.expression.op.define.Op_EQ;
import com.asiainfo.veris.crm.order.soa.frame.bcf.callpf.auto.expression.op.define.Op_GE;
import com.asiainfo.veris.crm.order.soa.frame.bcf.callpf.auto.expression.op.define.Op_GT;
import com.asiainfo.veris.crm.order.soa.frame.bcf.callpf.auto.expression.op.define.Op_LE;
import com.asiainfo.veris.crm.order.soa.frame.bcf.callpf.auto.expression.op.define.Op_LT;
import com.asiainfo.veris.crm.order.soa.frame.bcf.callpf.auto.expression.op.define.Op_MINUS;
import com.asiainfo.veris.crm.order.soa.frame.bcf.callpf.auto.expression.op.define.Op_MOD;
import com.asiainfo.veris.crm.order.soa.frame.bcf.callpf.auto.expression.op.define.Op_MUTI;
import com.asiainfo.veris.crm.order.soa.frame.bcf.callpf.auto.expression.op.define.Op_NEQ;
import com.asiainfo.veris.crm.order.soa.frame.bcf.callpf.auto.expression.op.define.Op_NG;
import com.asiainfo.veris.crm.order.soa.frame.bcf.callpf.auto.expression.op.define.Op_NOT;
import com.asiainfo.veris.crm.order.soa.frame.bcf.callpf.auto.expression.op.define.Op_OR;
import com.asiainfo.veris.crm.order.soa.frame.bcf.callpf.auto.expression.op.define.Op_PLUS;
import com.asiainfo.veris.crm.order.soa.frame.bcf.callpf.auto.expression.op.define.Op_QUES;
import com.asiainfo.veris.crm.order.soa.frame.bcf.callpf.auto.expression.op.define.Op_SELECT;

/**
 * 表达式操作符接口 操作符优先级数值越大，优先级越高
 */
public enum Operator
{

    // 逻辑否
    NOT("!", 80, 1),
    // 取负
    NG("-", 80, 1),

    // 算术乘
    MUTI("*", 70, 2),
    // 算术除
    DIV("/", 70, 2),
    // 算术除
    MOD("%", 70, 2),

    // 算术加
    PLUS("+", 60, 2),
    // 算术减
    MINUS("-", 60, 2),

    // 逻辑小于
    LT("<", 50, 2),
    // 逻辑小等于
    LE("<=", 50, 2),
    // 逻辑大于
    GT(">", 50, 2),
    // 逻辑大等于
    GE(">=", 50, 2),

    // 逻辑等
    EQ("==", 40, 2),
    // 逻辑不等
    NEQ("!=", 40, 2),

    // 逻辑与
    AND("&&", 30, 2),

    // 逻辑或
    OR("||", 20, 2),

    // 集合添加
    APPEND("#", 10, 2),

    // 三元选择
    QUES("?", 0, 0), COLON(":", 0, 0), SELECT("?:", 0, 3);

    private static final Set<String> OP_RESERVE_WORD = new HashSet<String>();

    static
    {

        OP_RESERVE_WORD.add(NOT.getToken());
        OP_RESERVE_WORD.add(NG.getToken());

        OP_RESERVE_WORD.add(MUTI.getToken());
        OP_RESERVE_WORD.add(DIV.getToken());
        OP_RESERVE_WORD.add(MOD.getToken());

        OP_RESERVE_WORD.add(PLUS.getToken());
        OP_RESERVE_WORD.add(MINUS.getToken());

        OP_RESERVE_WORD.add(LT.getToken());
        OP_RESERVE_WORD.add(LE.getToken());
        OP_RESERVE_WORD.add(GT.getToken());
        OP_RESERVE_WORD.add(GE.getToken());

        OP_RESERVE_WORD.add(EQ.getToken());
        OP_RESERVE_WORD.add(NEQ.getToken());

        OP_RESERVE_WORD.add(AND.getToken());

        OP_RESERVE_WORD.add(OR.getToken());

        OP_RESERVE_WORD.add(APPEND.getToken());

        OP_RESERVE_WORD.add(SELECT.getToken());
        OP_RESERVE_WORD.add(QUES.getToken());
        OP_RESERVE_WORD.add(COLON.getToken());
    }

    private static final HashMap<Operator, IOperatorExecution> OP_EXEC_MAP = new HashMap<Operator, IOperatorExecution>();

    static
    {

        OP_EXEC_MAP.put(NOT, new Op_NOT());
        OP_EXEC_MAP.put(NG, new Op_NG());

        OP_EXEC_MAP.put(MUTI, new Op_MUTI());
        OP_EXEC_MAP.put(DIV, new Op_DIV());
        OP_EXEC_MAP.put(MOD, new Op_MOD());

        OP_EXEC_MAP.put(PLUS, new Op_PLUS());
        OP_EXEC_MAP.put(MINUS, new Op_MINUS());

        OP_EXEC_MAP.put(LT, new Op_LT());
        OP_EXEC_MAP.put(LE, new Op_LE());
        OP_EXEC_MAP.put(GT, new Op_GT());
        OP_EXEC_MAP.put(GE, new Op_GE());

        OP_EXEC_MAP.put(EQ, new Op_EQ());
        OP_EXEC_MAP.put(NEQ, new Op_NEQ());

        OP_EXEC_MAP.put(AND, new Op_AND());

        OP_EXEC_MAP.put(OR, new Op_OR());

        OP_EXEC_MAP.put(APPEND, new Op_APPEND());

        OP_EXEC_MAP.put(SELECT, new Op_SELECT());
        OP_EXEC_MAP.put(QUES, new Op_QUES());
        OP_EXEC_MAP.put(COLON, new Op_COLON());

    }

    /**
     * 判断字符串是否是合法的操作符
     * 
     * @param tokenText
     * @return
     */
    public static boolean isLegalOperatorToken(String tokenText)
    {
        return OP_RESERVE_WORD.contains(tokenText);
    }

    private String token;

    private int priority;

    private int opType;

    Operator(String token, int priority, int opType)
    {
        this.token = token;
        this.priority = priority;
        this.opType = opType;
    }

    /**
     * 执行操作，并返回结果Token
     * 
     * @param args
     *            注意args中的参数由于是从栈中按LIFO顺序弹出的，所以必须从尾部倒着取数
     * @return Constant 常量型的执行结果
     */
    public Constant execute(Constant[] args) throws IllegalExpressionException
    {

        IOperatorExecution opExec = OP_EXEC_MAP.get(this);
        if (opExec == null)
        {
            throw new IllegalStateException("系统内部错误：找不到操作符对应的执行定义");
        }
        return opExec.execute(args);
    }

    /**
     * 操作符类型 一元 ！ 二元 && >=
     * 
     * @return int 操作符类型（几元操作）
     */
    public int getOpType()
    {
        return this.opType;
    }

    /**
     * 获取操作符的优先级
     * 
     * @return int 操作符优先级
     */
    public int getPiority()
    {
        return this.priority;
    }

    /**
     * 获取操作符的字符表示 如：+ - equals && ==
     * 
     * @return String 操作符的字符形态
     */
    public String getToken()
    {
        return this.token;
    }

    /**
     * 检查操作符和参数是否合法，是可执行的 如果合法，则返回含有执行结果类型的Token 如果不合法，则返回null
     * 
     * @param opPositin
     *            操作符位置
     * @param args
     *            注意args中的参数由于是从栈中按LIFO顺序弹出的，所以必须从尾部倒着取数
     * @return Constant 常量型的执行结果
     */
    public Constant verify(int opPositin, BaseDataMeta[] args) throws IllegalExpressionException
    {

        IOperatorExecution opExec = OP_EXEC_MAP.get(this);
        if (opExec == null)
        {
            throw new IllegalStateException("系统内部错误：找不到操作符对应的执行定义");
        }
        return opExec.verify(opPositin, args);
    }

}
