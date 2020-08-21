
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.svc;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.BusinessAbilityCall;


/**
 * 业务能力运营中心 调用 公共服务
 * @Author zhengkai5
 *
 * */
public class BusinessAbilitySVC extends CSBizService
{

    public IData callBusinessCenterCommon(IData input) throws Exception
    {
        String busiCode = input.getString("BUSI_ABILITY_CODE");
        return BusinessAbilityCall.callBusinessCenterCommon(busiCode,input);
    }

}
