
package com.asiainfo.veris.crm.order.soa.person.busi.plat.order.filter.voip;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterOut;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;

public class VoipIbossOutFilter implements IFilterOut
{

    public IData transfterDataOut(IData input, BusiTradeData btd) throws Exception
    {
        UcaData uca = btd.getRD().getUca();
        IData result = new DataMap();
        result.put("ID_TYPE", "01");
        result.put("ID_VALUE", uca.getSerialNumber());
        result.put("OPR_NUMB", input.getString("OPR_NUMB"));
        String imsi = "";
        IDataset simCard = UserResInfoQry.queryUserSimInfo(uca.getUserId(), "1");
        if (!IDataUtil.isEmpty(simCard))
        {
            imsi = simCard.getData(0).getString("IMSI");
        }
        result.put("IMSI", imsi);
        return result;

    }

}
