/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.userdiscntspecdeal.order;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @ClassName: UserDiscntSpecDealRegSVC.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: maoke
 * @date: May 27, 2014 7:58:19 PM Modification History: Date Author Version Description
 *        -------------------------------------------------------* May 27, 2014 maoke v1.0.0 修改原因
 */
public class UserDiscntSpecDealRegSVC extends OrderService
{
    @Override
    public String getOrderTypeCode() throws Exception
    {
        return "152";
    }

    @Override
    public String getTradeTypeCode() throws Exception
    {
        return "152";
    }

    public void otherTradeDeal(IData input, BusiTradeData btd) throws Exception
    {
        if (StringUtils.isNotBlank(input.getString("OTHER_DISCNT_LIST")))
        {
            IDataset otherSpecList = new DatasetList(input.getString("OTHER_DISCNT_LIST"));

            if (IDataUtil.isNotEmpty(otherSpecList))
            {
                String serialNumber = btd.getRD().getUca().getSerialNumber();

                for (int i = 0, size = otherSpecList.size(); i < size; i++)
                {
                    IData data = otherSpecList.getData(i);
                    data.put("SERIAL_NUMBER", serialNumber);

                    IDataset result = CSAppCall.call("SS.UserDiscntSpecDealRegSVC.tradeReg", data);
                }
            }
        }
    }
    
    /**
     * 重写，不做业务校验
     */
    public void checkAfterRule(IData tableData, BusiTradeData btd) throws Exception
	{
    	
	}
}
