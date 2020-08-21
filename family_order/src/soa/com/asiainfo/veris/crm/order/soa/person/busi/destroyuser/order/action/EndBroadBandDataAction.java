
package com.asiainfo.veris.crm.order.soa.person.busi.destroyuser.order.action;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AccessAcctTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AddrTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.RateTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.BroadBandInfoQry;

public class EndBroadBandDataAction implements ITradeAction
{

    private void endAccessAcctInfo(String userId, BusiTradeData btd, UcaData uca) throws Exception
    {
        IDataset acessAcctList = BroadBandInfoQry.getBroadBandAccessAcctByUserId(userId);

        for (int i = 0; i < acessAcctList.size(); i++)
        {
            AccessAcctTradeData accessAcctTrade = new AccessAcctTradeData();
            IData accessAcct = acessAcctList.getData(i);
            accessAcctTrade.setAccessAcct(accessAcct.getString("ACCESS_ACCT", ""));
            accessAcctTrade.setAccessPwd(accessAcct.getString("ACCESS_PWD", ""));
            accessAcctTrade.setAccessType(accessAcct.getString("ACCESS_TYPE", ""));
            accessAcctTrade.setEndDate(btd.getRD().getAcceptTime());
            accessAcctTrade.setInstId(accessAcct.getString("INST_ID", ""));
            accessAcctTrade.setModifyTag(BofConst.MODIFY_TAG_DEL);
            accessAcctTrade.setRemark(accessAcct.getString("REMARK", ""));
            accessAcctTrade.setStartDate(accessAcct.getString("START_DATE", ""));
            accessAcctTrade.setUserId(userId);
            btd.add(uca.getSerialNumber(), accessAcctTrade);
        }
    }

    private void endAddressInfo(String userId, BusiTradeData btd, UcaData uca) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        IDataset addressList = BroadBandInfoQry.queryBroadBandAddressInfo(param);

        for (int i = 0; i < addressList.size(); i++)
        {
            IData addressInfo = addressList.getData(i);
            AddrTradeData addressData = new AddrTradeData();
            addressData.setUserId(addressInfo.getString("USER_ID", ""));
            addressData.setInstId(addressInfo.getString("INST_ID", ""));
            addressData.setAddrName(addressInfo.getString("ADDR_NAME", ""));
            addressData.setAddrDesc(addressInfo.getString("ADDR_DESC", ""));
            addressData.setMofficeId(addressInfo.getString("MOFFICE_ID", ""));
            addressData.setAddrId(addressInfo.getString("ADDR_ID", ""));
            addressData.setStartDate(addressInfo.getString("START_DATE", ""));
            addressData.setEndDate(btd.getRD().getAcceptTime());
            addressData.setRemark(addressInfo.getString("REMARK", ""));
            addressData.setModifyTag(BofConst.MODIFY_TAG_DEL);
            btd.add(uca.getSerialNumber(), addressData);
        }

    }

    private void endRateInfo(String userId, BusiTradeData btd, UcaData uca) throws Exception
    {
        IDataset rateList = BroadBandInfoQry.queryUserRateByUserId(userId);

        for (int i = 0; i < rateList.size(); i++)
        {
            IData rateInfo = rateList.getData(i);
            RateTradeData rateData = new RateTradeData();
            rateData.setEndDate(btd.getRD().getAcceptTime());
            rateData.setInstId(rateInfo.getString("INST_ID", ""));
            rateData.setModifyTag(BofConst.MODIFY_TAG_DEL);
            rateData.setRate(rateInfo.getString("RATE", ""));
            rateData.setRemark(rateInfo.getString("REMARK", ""));
            rateData.setRsrvStr1(rateInfo.getString("RSRV_STR1", ""));
            rateData.setRsrvStr2(rateInfo.getString("RSRV_STR2", ""));
            rateData.setStartDate(rateInfo.getString("START_DATE", ""));
            rateData.setUserId(userId);
            btd.add(uca.getSerialNumber(), rateData);
        }
    }

    @Override
    public void executeAction(BusiTradeData btd) throws Exception
    {

        UcaData uca = btd.getRD().getUca();
        String netTypeCode = uca.getUser().getNetTypeCode();
        String userId = uca.getUserId();
        if ("04".equals(netTypeCode))
        {
            endAccessAcctInfo(userId, btd, uca);
            endAddressInfo(userId, btd, uca);
            endRateInfo(userId, btd, uca);
        }
    }
}
