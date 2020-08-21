
package com.asiainfo.veris.crm.order.soa.frame.bcf.callpf.auto.expression.format.reader;

import java.io.IOException;

import com.asiainfo.veris.crm.order.soa.frame.bcf.callpf.auto.expression.format.Element;
import com.asiainfo.veris.crm.order.soa.frame.bcf.callpf.auto.expression.format.ExpressionReader;
import com.asiainfo.veris.crm.order.soa.frame.bcf.callpf.auto.expression.format.FormatException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.callpf.auto.expression.format.Element.ElementType;

/**
 * 读取函数类型
 */
public class FunctionTypeReader implements ElementReader
{
    public static final char START_MARK = '$';// 函数开始

    public static final char END_MARK = '(';// 函数结束

    /**
     * 从流中读取函数类型的ExpressionToken
     * 
     * @param sr
     * @return
     * @throws FormatException
     * @throws IOException
     */
    public Element read(ExpressionReader sr) throws FormatException, IOException
    {
        int index = sr.getCruuentIndex();
        StringBuilder sb = new StringBuilder();
        int b = sr.read();
        if (b == -1 || b != FunctionTypeReader.START_MARK)
        {
            throw new FormatException("不是有效的函数开始");
        }
        boolean readStart = true;
        while ((b = sr.read()) != -1)
        {
            char c = (char) b;
            if (c == FunctionTypeReader.END_MARK)
            {
                sr.reset();
                return new Element(sb.toString(), index, ElementType.FUNCTION);
            }
            if (!Character.isJavaIdentifierPart(c))
            {
                throw new FormatException("名称不能为非法字符：" + c);
            }
            if (readStart)
            {
                if (!Character.isJavaIdentifierStart(c))
                {
                    throw new FormatException("名称开头不能为字符：" + c);
                }
                readStart = false;
            }
            sb.append(c);
            sr.mark(0);
        }
        throw new FormatException("不是有效的函数结束");
    }
}
