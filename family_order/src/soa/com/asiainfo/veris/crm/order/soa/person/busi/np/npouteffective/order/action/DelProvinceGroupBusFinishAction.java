
package com.asiainfo.veris.crm.order.soa.person.busi.np.npouteffective.order.action;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;

public class DelProvinceGroupBusFinishAction implements ITradeFinishAction
{

    @Override
    public void executeAction(IData mainTrade) throws Exception
    {
        String userId = mainTrade.getString("USER_ID");
        String acctId = mainTrade.getString("ACCT_ID");
        String productId = mainTrade.getString("PRODUCT_ID");
        String accepteTime = mainTrade.getString("ACCEPT_DATE");
        IDataset otherInfo = UserOtherInfoQry.getUserOverProvinceInfo(userId, "PGRP", productId, null);
        if (IDataUtil.isNotEmpty(otherInfo))
        {

            String endTime = SysDateMgr.getDateForYYYYMMDD(accepteTime) + SysDateMgr.getFirstTime00000();

            if (StringUtils.equals("1", otherInfo.getData(0).getString("RSRV_STR3")))
            {
                IData param = new DataMap();
                param.put("UNIFY_PAY_CODE", otherInfo.getData(0).getString("RSRV_STR5"));
                param.put("ACCT_ID", acctId);
                param.put("END_DATE", endTime);
                Dao.executeBatchByCodeCode("TF_F_ACCT_UNIFYPAY", "UPD_BY_USERID", IDataUtil.idToIds(param));
            }
        }
    }

}
