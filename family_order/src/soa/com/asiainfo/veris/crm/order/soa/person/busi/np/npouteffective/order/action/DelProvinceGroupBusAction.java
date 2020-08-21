
package com.asiainfo.veris.crm.order.soa.person.busi.np.npouteffective.order.action;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: DestroyUserDelOtherTradeAction.java
 * @Description: 终止跨省集团业务注销帐务
 * @version: v1.0.0
 * @author: xiaozb
 * @date: 2014-6-5 下午9:07:22
 */
public class DelProvinceGroupBusAction implements ITradeAction
{
    public void executeAction(BusiTradeData btd) throws Exception
    {
        UcaData ucaData = btd.getRD().getUca();
        String serialNumber = ucaData.getSerialNumber();
        String userId = ucaData.getUserId();
        String productId = ucaData.getProductId();
        IDataset otherInfo = UserOtherInfoQry.getUserOverProvinceInfo(userId, "PGRP", productId, null);
        if (IDataUtil.isNotEmpty(otherInfo))
        {
            IData otherInfoData = otherInfo.getData(0);
            String accepteTime = btd.getRD().getAcceptTime();
            OtherTradeData otherTradeData = new OtherTradeData(otherInfoData);
            String endTime = SysDateMgr.getDateForYYYYMMDD(accepteTime) + SysDateMgr.getFirstTime00000();
            otherTradeData.setEndDate(endTime);
            otherTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL);
            btd.add(serialNumber, otherTradeData);

        }
    }

}
