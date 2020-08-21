
package com.asiainfo.veris.crm.order.soa.frame.bcf.callpf.auto.expression;

/**
 * 非法的表达式异常
 */
public class IllegalExpressionException extends Exception
{

    /**
	 * 
	 */
    private static final long serialVersionUID = -382075370364295450L;

    /**
     * 错误标识
     */
    private String errorTokenText;

    /**
     * 错误位置
     */
    private int errorPosition = -1;

    public IllegalExpressionException()
    {
        super();
    }

    public IllegalExpressionException(String msg)
    {
        super(msg);
    }

    public IllegalExpressionException(String msg, String errorTokenText)
    {
        super(msg);
        this.errorTokenText = errorTokenText;
    }

    public IllegalExpressionException(String msg, String errorTokenText, int errorPosition)
    {
        super(msg);
        this.errorPosition = errorPosition;
        this.errorTokenText = errorTokenText;
    }

    public int getErrorPosition()
    {
        return errorPosition;
    }

    public String getErrorTokenText()
    {
        return errorTokenText;
    }

    public void setErrorPosition(int errorPosition)
    {
        this.errorPosition = errorPosition;
    }

    public void setErrorTokenText(String errorTokenText)
    {
        this.errorTokenText = errorTokenText;
    }

    public String toString()
    {
        StringBuilder sb = new StringBuilder(this.getMessage());
        sb.append("\r\n处理对象：").append(errorTokenText);
        sb.append("\r\n处理位置：").append(errorPosition == -1 ? " unknow " : errorPosition);
        return sb.toString();
    }
}
