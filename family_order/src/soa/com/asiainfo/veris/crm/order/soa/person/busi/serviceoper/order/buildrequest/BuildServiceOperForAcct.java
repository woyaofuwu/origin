
package com.asiainfo.veris.crm.order.soa.person.busi.serviceoper.order.buildrequest;

import com.ailk.common.data.IData;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.ParamException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;

public class BuildServiceOperForAcct extends BuildServiceOper implements IBuilder
{
    @Override
    public UcaData buildUcaData(IData param) throws Exception
    {
        // 设置三户资料对象
        String userId = param.getString("USER_ID");
        if (StringUtils.isBlank(userId))
        {
            CSAppException.apperr(ParamException.CRM_PARAM_246);
        }
        UcaData uca = UcaDataFactory.getUcaByUserId(userId);
        return uca;
    }
}
