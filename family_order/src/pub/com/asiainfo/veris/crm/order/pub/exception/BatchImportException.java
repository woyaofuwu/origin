
package com.asiainfo.veris.crm.order.pub.exception;

import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.IBusiException;

public enum BatchImportException implements IBusiException 
{
	ESOP_BATCH_0("[%s]"),
    ESOP_BATCH_1("导入文件必填项不能为空!"), 
    ESOP_BATCH_2("IP地址类型为[%s]时，客户申请公网IP地址数只能为【0】!"),
	ESOP_BATCH_3("IP地址类型为[%s]时，申请公网IPV4、IPV6地址数只能为【0】!"),
//	ESOP_BATCH_4("IP地址类型为[%s]时，申请公网IPV6地址数只能为【0】!"),
	ESOP_BATCH_5("导入的标准地址[%s]和省份、区县、街道/乡镇、门牌/村组不统一!"),
	ESOP_BATCH_6("导入的标准地址[%s]不为快速开通地址!"),
	ESOP_BATCH_7("导入的标准地址[%s]不为厚覆盖，不可选为快速开通地址!"),
	ESOP_BATCH_8("业务范围为[%s],A、Z端地市要一致!"),
	ESOP_BATCH_9("业务范围为[%s],A、Z端地市不能一致!"),;
	
    private final String value;

    private BatchImportException(String value)
    {

        this.value = value;
    }

    public String getValue()
    {

        return value;
    }
}
