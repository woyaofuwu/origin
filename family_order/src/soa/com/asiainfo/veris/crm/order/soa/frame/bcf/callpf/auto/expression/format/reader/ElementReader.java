
package com.asiainfo.veris.crm.order.soa.frame.bcf.callpf.auto.expression.format.reader;

import java.io.IOException;

import com.asiainfo.veris.crm.order.soa.frame.bcf.callpf.auto.expression.format.Element;
import com.asiainfo.veris.crm.order.soa.frame.bcf.callpf.auto.expression.format.ExpressionReader;
import com.asiainfo.veris.crm.order.soa.frame.bcf.callpf.auto.expression.format.FormatException;

/**
 */
public interface ElementReader
{
    Element read(ExpressionReader sr) throws FormatException, IOException;
}
