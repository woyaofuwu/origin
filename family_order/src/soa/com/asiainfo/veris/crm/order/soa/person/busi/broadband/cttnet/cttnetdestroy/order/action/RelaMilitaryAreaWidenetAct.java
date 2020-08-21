
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetdestroy.order.action;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.BroadBandInfoQry;

public class RelaMilitaryAreaWidenetAct implements ITradeFinishAction
{

    public void executeAction(IData mainTrade) throws Exception
    {
        String serialNumber = mainTrade.getString("SERIAL_NUMBER");

        IDataset broabandMilitary = BroadBandInfoQry.qryisBroadbandMilitary(serialNumber, "1");
        if (IDataUtil.isNotEmpty(broabandMilitary))
        {
            BroadBandInfoQry.UpdateBroadbandMilitary(serialNumber, "0");
        }

    }

}
