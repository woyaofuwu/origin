package com.asiainfo.veris.crm.order.pub.exception;

import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.IBusiException;

public enum CommonTransServiceException implements IBusiException
{
    COMMON_TRANS_SERVICE_1("OFFER_CODE不能为空!"),
    COMMON_TRANS_SERVICE_2("OFFER_TYPE不能为空!"),
    COMMON_TRANS_SERVICE_3("COMMON_TRANS_TAG不能为空!"),
    COMMON_TRANS_SERVICE_4("未获取到catalogId，请检查产商品接口!"),
    COMMON_TRANS_SERVICE_5("未查询到配置服务，请检查TD_S_SVC_COLLE_CONFIG表中配置!"),
    COMMON_TRANS_SERVICE_6("未配置SVC_NAME，请检查TD_S_SVC_COLLE_CONFIG表中配置!"),
	COMMON_TRANS_SERVICE_7("配置错误，请检查TD_S_SVC_COLLE_CONFIG表中配置!");
	
    private final String value;

    private CommonTransServiceException(String value)
    {

        this.value = value;
    }

    @Override
    public String getValue()
    {

        return value;
    }
}
