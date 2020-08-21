
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.changecustinfo.order.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.WideNetTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.broadband.WidenetInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.changecustinfo.order.requestdata.WidenetModifyCustInfoRequestData;

public class WidenetModifyCustInfoTrade extends BaseTrade implements ITrade
{

    /**
     * 修改主台帐字段
     * 
     * @author chenzm
     * @param btd
     * @throws Exception
     */
    private void appendTradeMainData(BusiTradeData<BaseTradeData> btd) throws Exception
    {

        btd.getMainTradeData().setSubscribeType("300");
    }

    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        WidenetModifyCustInfoRequestData reqData = (WidenetModifyCustInfoRequestData) btd.getRD();
        createTradeWidenet(btd, reqData);
        appendTradeMainData(btd);
    }

    /**
     * 用户宽带台帐拼串
     * 
     * @param btd
     * @param reqData
     * @throws Exception
     * @author chenzm
     */
    private void createTradeWidenet(BusiTradeData<BaseTradeData> btd, WidenetModifyCustInfoRequestData reqData) throws Exception
    {
        IDataset widenetInfos = WidenetInfoQry.getUserWidenetInfo(reqData.getUca().getUserId());
        IData wideInfo = widenetInfos.getData(0);
        WideNetTradeData wtd = new WideNetTradeData(wideInfo);
        if ("609".equals(btd.getTradeTypeCode()))
        {
            wtd.setDetailAddress(reqData.getNewDetailAddress());
            wtd.setModifyTag(BofConst.MODIFY_TAG_UPD);
            wtd.setRemark("客户宽带资料修改");
        }
        else
        {
            wtd.setOldDetailAddress(wideInfo.getString("DETAIL_ADDRESS"));
            wtd.setOldStandAddress(wideInfo.getString("STAND_ADDRESS"));
            wtd.setOldStandAddressCode(wideInfo.getString("STAND_ADDRESS_CODE"));
            wtd.setDetailAddress(reqData.getNewDetailAddress());
            wtd.setModifyTag(BofConst.MODIFY_TAG_UPD);
            wtd.setContact(reqData.getNewContact());
            wtd.setContactPhone(reqData.getNewContactPhone());
            wtd.setRsrvStr5(reqData.getNewPsptId());
            wtd.setRemark("校园宽带资料修改");
        }
        btd.add(btd.getRD().getUca().getSerialNumber(), wtd);

    }

}
