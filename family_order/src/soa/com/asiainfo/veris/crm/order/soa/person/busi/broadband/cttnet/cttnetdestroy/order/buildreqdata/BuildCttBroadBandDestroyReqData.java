
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetdestroy.order.buildreqdata;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetdestroy.order.requestdata.CttBroadBandDestroyReqData;

public class BuildCttBroadBandDestroyReqData extends BaseBuilder implements IBuilder
{

    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        CttBroadBandDestroyReqData reqData = (CttBroadBandDestroyReqData) brd;

        reqData.setSerialNumber(param.getString("SERIAL_NUMBER"));
        reqData.setBookDestroyDate(param.getString("BOOK_DESTROY_DATE"));
        reqData.setRemark(param.getString("REMARK"));
    }

    // public UcaData buildUcaData(IData param) throws Exception
    // {
    // param.put("USER_ID", SeqMgr.getUserId());
    // param.put("CUST_ID", SeqMgr.getCustId());
    // param.put("ACCT_ID", SeqMgr.getAcctId());
    // param.put("NET_TYPE_CODE", "04");//04-移动自建宽带用户
    // String accessAcct = SeqMgr.getBroadBandId();
    // param.put("SERIAL_NUMBER", accessAcct);// 宽带账号
    // param.put("ACCESS_ACCT", accessAcct);// 宽带账号
    // param.put("ACCESS_PWD", CrmEncrypt.EncryptPasswd(param.getString("PASS_WORD")));// 加密宽带用户密码
    //
    // UcaData ucaData = new UcaData();
    // ucaData.setUser(new UserTradeData(param));
    // ucaData.setCustomer(new CustomerTradeData(param));
    // ucaData.setCustPerson(new CustPersonTradeData(param));
    // ucaData.setAccount(new AccountTradeData(param));
    // ucaData.setLastOweFee("0");
    // ucaData.setRealFee("0");
    // ucaData.setAcctBlance("0");
    //
    // DataBusManager.getDataBus().setUca(ucaData);
    // return ucaData;
    // }

    // @Override
    // protected void checkBefore(IData input, BaseReqData reqData) throws Exception
    // {
    // // 新建用户不走checkBefore。
    // }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        return new CttBroadBandDestroyReqData();
    }
}
