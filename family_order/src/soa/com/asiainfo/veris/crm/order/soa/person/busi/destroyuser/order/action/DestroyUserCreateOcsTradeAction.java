
package com.asiainfo.veris.crm.order.soa.person.busi.destroyuser.order.action;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OcsTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: DestroyUserCreateOcsTradeAction.java
 * @Description: 销户删除other表数据
 * @version: v1.0.0
 * @author: xiaozb
 * @date: 2014-6-5 下午9:07:22
 */
public class DestroyUserCreateOcsTradeAction implements ITradeAction
{
    public void executeAction(BusiTradeData btd) throws Exception
    {
        UcaData ucaData = btd.getRD().getUca();
        String serialNumber = ucaData.getSerialNumber();
        String userId = ucaData.getUserId();
        String productId = ucaData.getProductId();
        IDataset userOcsDataset = UserInfoQry.getOCSInfo(userId, btd.getRD().getUca().getUserEparchyCode());
        if (IDataUtil.isNotEmpty(userOcsDataset))
        {
            IData userOcsData = userOcsDataset.getData(0);
            String accepteTime = btd.getRD().getAcceptTime();
            OcsTradeData otherTradeData = new OcsTradeData(userOcsData);
            otherTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL);
            otherTradeData.setEndDate(accepteTime);
            btd.add(serialNumber, otherTradeData);
        }
    }

}
