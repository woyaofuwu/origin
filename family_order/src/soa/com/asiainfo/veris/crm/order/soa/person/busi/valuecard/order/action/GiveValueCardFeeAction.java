/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.valuecard.order.action;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DeviceTradeData;

/**
 * @CREATED by gongp@2014-8-1 修改历史 Revision 2014-8-1 上午09:55:57
 */
public class GiveValueCardFeeAction implements ITradeAction
{

    /*
     * (non-Javadoc)
     */
    @Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
        List<DeviceTradeData> list = btd.getTradeDatas(TradeTableEnum.TRADE_FEEDEVICE);

        String tradeTypecode = btd.getTradeTypeCode();
        String acceptTime = btd.getRD().getAcceptTime();
        String remark = "";

        if ("418".equals(tradeTypecode))
        {
            for (int i = 0, size = list.size(); i < size; i++)
            {
                DeviceTradeData data = list.get(i);

                long tempFee = Integer.parseInt(data.getDeviceNum()) * Integer.parseInt(data.getDevicePrice());
                remark = data.getRemark().replace("拆分", "");

                IData param = new DataMap();

                param.put("VFEE", tempFee);
                param.put("VSTAFF_ID", remark);
                param.put("VDEVICE_NO_S", data.getDeviceNoS());
                param.put("VDEVICE_NO_E", data.getDeviceNoE());
                param.put("VUPDATE_TIME", acceptTime);
                param.put("VRSRV_STR1", "");
                param.put("VRSRV_STR2", "");
                param.put("VRSRV_STR3", "");
                param.put("VRSRV_STR4", "");
                param.put("VREMARK", "");

                Dao.executeUpdateByCodeCode("TD_B_VALUECARD_AUDIT", "INS_VALUECARD_AUDIT", param);
            }
        }/*
          * else if("424".equals(tradeTypecode)){//返销的时候不插 for(int i=0,size = list.size();i<size;i++){ DeviceTradeData
          * data = list.get(i); long tempFee =
          * Integer.parseInt(data.getDeviceNum())*Integer.parseInt(data.getDevicePrice()); remark =
          * data.getRemark().replace("拆分", ""); IData param = new DataMap(); param.put("VFEE", -tempFee);
          * param.put("VSTAFF_ID", remark); param.put("VDEVICE_NO_S", data.getDeviceNoS()); param.put("VDEVICE_NO_E",
          * data.getDeviceNoE()); param.put("VUPDATE_TIME", acceptTime); param.put("VRSRV_STR1", "");
          * param.put("VRSRV_STR2", ""); param.put("VRSRV_STR3", ""); param.put("VRSRV_STR4", ""); param.put("VREMARK",
          * ""); Dao.executeUpdateByCodeCode("TD_B_VALUECARD_AUDIT", "INS_VALUECARD_AUDIT", param); } }
          */

    }

}
