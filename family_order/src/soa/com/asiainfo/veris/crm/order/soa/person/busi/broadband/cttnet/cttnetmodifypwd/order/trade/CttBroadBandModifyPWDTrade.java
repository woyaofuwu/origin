
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetmodifypwd.order.trade;

import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.exception.BroadBandException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.WideNetActTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.WideNetTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.broadband.WidenetInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetcreateuser.CttConstants;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetmodifypwd.order.requestdata.CttBroadBandModifyPWDReqData;

public class CttBroadBandModifyPWDTrade extends BaseTrade implements ITrade
{

    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        // reGeneTradeOrder(btd); //重写客户订单台帐表部分字段
        reGeneTradeMain(btd); // 重写用户订单台帐表部分字段

        geneTradeWidenet(btd); // 宽带账号台帐子表

        geneTradeWidenetAct(btd);

    }

    /**
     * 宽带台帐子表
     * 
     * @param pd
     * @param td
     * @throws Exception
     */
    protected void geneTradeWidenet(BusiTradeData btd) throws Exception
    {

        CttBroadBandModifyPWDReqData reqData = (CttBroadBandModifyPWDReqData) btd.getRD();
        String userId = reqData.getUca().getUserId();
        IDataset acctDataList = WidenetInfoQry.getUserWidenetInfo(userId);
        if (IDataUtil.isEmpty(acctDataList))
        {
            CSAppException.apperr(BroadBandException.CRM_BROADBAND_4);
        }

        // String key = userId.substring(userId.length() - 9, userId.length());
        //
        // String acctPwd = (String) Encryptor.fnEncrypt(reqData.getNewPasswd(), key);
        WideNetTradeData widenetTradeData = new WideNetTradeData(acctDataList.getData(0));
        widenetTradeData.setAcctPasswd(reqData.getNewPasswd());
        widenetTradeData.setModifyTag(BofConst.MODIFY_TAG_UPD);
        widenetTradeData.setStartDate(widenetTradeData.getStartDate().substring(0, 19));
        widenetTradeData.setEndDate(widenetTradeData.getEndDate().substring(0, 19));

        btd.add(reqData.getUca().getSerialNumber(), widenetTradeData);
    }

    /**
     * 宽带账号台帐子表
     * 
     * @param pd
     * @param td
     * @throws Exception
     */
    protected void geneTradeWidenetAct(BusiTradeData btd) throws Exception
    {
        CttBroadBandModifyPWDReqData reqData = (CttBroadBandModifyPWDReqData) btd.getRD();
        String userId = reqData.getUca().getUserId();
        IDataset acctDataList = WidenetInfoQry.getUserWidenetActInfosByUserid(userId);
        if (IDataUtil.isEmpty(acctDataList))
        {
            CSAppException.apperr(BroadBandException.CRM_BROADBAND_4);
        }

        // String key = userId.substring(userId.length() - 9, userId.length());
        //
        // String acctPwd = (String) Encryptor.fnEncrypt(reqData.getNewPasswd(), key);
        WideNetActTradeData actTradeData = new WideNetActTradeData(acctDataList.getData(0));
        actTradeData.setAcctPasswd(reqData.getNewPasswd());
        actTradeData.setModifyTag(BofConst.MODIFY_TAG_UPD);

        btd.add(reqData.getUca().getSerialNumber(), actTradeData);
    }

    private void reGeneTradeMain(BusiTradeData btd) throws Exception
    {
        // TODO Auto-generated method stub
        CttBroadBandModifyPWDReqData reqData = (CttBroadBandModifyPWDReqData) btd.getRD();
        MainTradeData mainTradeData = btd.getMainTradeData();
        mainTradeData.setSubscribeType(CttConstants.SUBSCRIBE_TYPE);
        mainTradeData.setPfType(CttConstants.PF_TYPE);
        mainTradeData.setOlcomTag(CttConstants.OLCOM_TAG);
        mainTradeData.setRsrvStr1(reqData.getNewPasswd());
        mainTradeData.setRsrvStr2("3");
        // mainTradeData.setProcessTagSet(processTagSet)("3");
    }

}
