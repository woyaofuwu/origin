
package com.asiainfo.veris.crm.order.soa.person.busi.rentmobile.order.action;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;

public class ModifyRentBalanceFinishAction implements ITradeFinishAction
{

    @Override
    public void executeAction(IData mainTrade) throws Exception
    {
        String userId = mainTrade.getString("USER_ID");

        IData param = new DataMap();
        param.put("USER_ID", userId);
        Dao.executeUpdateByCodeCode("TF_F_USER_RENT", "UPD_CAL_DATE_RENT", param);
    }

}
