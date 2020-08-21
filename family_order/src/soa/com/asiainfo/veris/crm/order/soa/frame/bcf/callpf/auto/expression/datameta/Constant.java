
package com.asiainfo.veris.crm.order.soa.frame.bcf.callpf.auto.expression.datameta;

import java.util.ArrayList;

/**
 * 常量数据描述
 */
public class Constant extends BaseDataMeta
{

    public Constant(DataType dataType, Object value)
    {
        super(dataType, value);

        if (dataType == null)
        {
            throw new IllegalArgumentException("非法参数：数据类型为空");
        }
        // 如果为集合类型，生成集合容器
        if (DataType.DATATYPE_LIST == dataType)
        {
            if (dataValue == null)
            {
                dataValue = new ArrayList<Object>(0);
            }
        }

    }

    public Constant(Reference ref)
    {
        super(null, ref);
        this.setReference(true);
    }
}
