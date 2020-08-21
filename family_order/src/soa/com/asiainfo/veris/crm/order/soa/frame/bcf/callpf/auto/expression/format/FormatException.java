
package com.asiainfo.veris.crm.order.soa.frame.bcf.callpf.auto.expression.format;

/**
 * 解析ExpressionToken出错时抛出 Sep 21, 2008
 */
public class FormatException extends Exception
{

    /**
	 * 
	 */
    private static final long serialVersionUID = 2156367068320450613L;

    public FormatException()
    {
        super();
    }

    public FormatException(String message)
    {
        super(message);
    }

    public FormatException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public FormatException(Throwable cause)
    {
        super(cause);
    }
}
