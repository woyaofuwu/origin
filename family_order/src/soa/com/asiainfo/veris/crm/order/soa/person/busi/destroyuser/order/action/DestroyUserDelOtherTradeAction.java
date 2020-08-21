
package com.asiainfo.veris.crm.order.soa.person.busi.destroyuser.order.action;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
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
 * @Description: 销户删除other表数据
 * @version: v1.0.0
 * @author: xiaozb
 * @date: 2014-6-5 下午9:07:22
 */
public class DestroyUserDelOtherTradeAction implements ITradeAction
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
            if (StringUtils.equals("1", otherInfoData.getString("RSRV_STR3")))
            {
                IData param = new DataMap();
                param.put("UNIFY_PAY_CODE", otherInfoData.getString("RSRV_STR5"));
                param.put("ACCT_ID", ucaData.getAcctId());
                param.put("END_DATE", endTime);
                Dao.executeBatchByCodeCode("TF_F_ACCT_UNIFYPAY", "UPD_BY_USERID", IDataUtil.idToIds(param));
            }
        }
    }

}
