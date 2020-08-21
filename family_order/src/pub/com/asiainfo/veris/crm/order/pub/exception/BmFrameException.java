/**
 * 
 */

package com.asiainfo.veris.crm.order.pub.exception;

import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.IBusiException;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: BmFrameException.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: chengxf2
 * @date: 2014-3-13 下午03:13:24 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-3-13 chengxf2 v1.0.0 修改原因
 */

public enum BmFrameException implements IBusiException
{

    CRM_BM_1("业务功能点【%s】没有定义!");

    private final String value;

    private BmFrameException(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
