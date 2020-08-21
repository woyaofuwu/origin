
package com.asiainfo.veris.crm.order.soa.person.busi.plat.order.filter.wlan;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterIn;

/**
 * WLAN封顶暂停 恢复
 * 
 * @author xiekl
 */
public class WlanCapSuspendAndResumeFilter implements IFilterIn
{

    public void transferDataInput(IData input) throws Exception
    {

        String userId = input.getString("USER_ID");
        IData userInfo = UcaInfoQry.qryUserInfoByUserId(userId);
        if (userInfo != null)
        {
            input.put("SERIAL_NUMBER", userInfo.getString("SERIAL_NUMBER"));
        }

    }

}
