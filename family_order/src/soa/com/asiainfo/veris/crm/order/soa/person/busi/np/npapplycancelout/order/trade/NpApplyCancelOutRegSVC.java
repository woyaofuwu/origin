
package com.asiainfo.veris.crm.order.soa.person.busi.np.npapplycancelout.order.trade;

import com.ailk.common.data.IData;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: NpApplyCancelOutRegSVC.java
 * @Description: 携出申请取消
 * @version: v1.0.0
 * @author: lijm3
 * @date: 2014-5-8 上午9:49:40 Modification History: Date Author Version Description
 *        ---------------------------------------------------------* 2014-5-8 lijm3 v1.0.0 修改原因
 */
public class NpApplyCancelOutRegSVC extends OrderService
{

    @Override
    public String getOrderTypeCode() throws Exception
    {
        return "1504";
    }

    @Override
    public String getTradeTypeCode() throws Exception
    {
        return "1504";
    }

    public void setTrans(IData input)
    {
        String serial_number = input.getString("SERIAL_NUMBER");
        String npcode = input.getString("NPCODE");
        if (StringUtils.isBlank(serial_number) && StringUtils.isNotBlank(serial_number))
        {
            input.put("SERIAL_NUMBER", npcode);
        }
    }

}
