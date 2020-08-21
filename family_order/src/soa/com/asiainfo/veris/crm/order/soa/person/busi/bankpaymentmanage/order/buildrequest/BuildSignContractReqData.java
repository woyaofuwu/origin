/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.bankpaymentmanage.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.bankpaymentmanage.order.requestdata.BaseSignContractReqData;

/**
 * @CREATED by gongp@2014-6-20 修改历史 Revision 2014-6-20 下午04:13:56
 */
public class BuildSignContractReqData extends BaseBuilder implements IBuilder
{

    /*
     * (non-Javadoc)
     */
    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        // TODO Auto-generated method stub
        BaseSignContractReqData reqData = (BaseSignContractReqData) brd;

        reqData.setBankId(param.getString("BANK_ID"));
        reqData.setBankAcctType(param.getString("BANK_ACCT_TYPE"));
        reqData.setBankAcctid(param.getString("BANK_ACCT_ID"));

        reqData.setPayType(param.getString("PAY_TYPE"));
        reqData.setRechThreshold(param.getString("RECH_THRESHOLD"));
        reqData.setRechAmount(param.getString("RECH_AMOUNT"));
        reqData.setRemark(param.getString("REMARK"));

        reqData.setChnlType(param.getString("CHNL_TYPE", "01"));
        reqData.setSignId(param.getString("SIGN_ID"));
        reqData.setSubNumber(param.getString("SUB_NUMBER"));

        reqData.setCancelDataStr(param.getString("CANCEL_DATAS", ""));
        reqData.setOperFlowId(param.getString("TRANSIDO"));
        reqData.setPreTradeId(param.getString("PRE_TRADE_ID"));

    }

    /*
     * (non-Javadoc)
     */
    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        // TODO Auto-generated method stub
        return new BaseSignContractReqData();
    }

}
