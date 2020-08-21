
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetcreateuser.order.action;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.BroadBandInfoQry;

/**
 * 开户时modem方式选择的是租用，则给绑定modem优惠
 */
public class UpdateAccountIp implements ITradeFinishAction
{

    public void executeAction(IData mainTrade) throws Exception
    {
        String serialNumber = mainTrade.getString("SERIAL_NUMBER");
        int num = BroadBandInfoQry.checkAccountIdInAccountIp("2", serialNumber);
        if (num > 0)
        {
            BroadBandInfoQry.updateAccountIp(serialNumber, "1");// 预占状态.1为正式占用
        }

    }

}
