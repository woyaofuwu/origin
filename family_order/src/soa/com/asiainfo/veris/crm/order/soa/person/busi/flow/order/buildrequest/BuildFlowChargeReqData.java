
package com.asiainfo.veris.crm.order.soa.person.busi.flow.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.flow.order.requestdata.FlowChargeReqData;



public class BuildFlowChargeReqData extends BaseBuilder implements IBuilder
{
    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        // TODO Auto-generated method stub

    	FlowChargeReqData reqData = (FlowChargeReqData) brd;
    	String strAmount = param.getString("AMOUNT");
        String strTransfee = param.getString("TRANS_FEE");
        String strCommid = param.getString("COMM_ID");
        String strChannelid = CSBizBean.getVisit().getInModeCode();
        String strPeerbusinessid = brd.getTradeId();
        String strRemark = param.getString("REMARK");
        String strEffectivedate = param.getString("EFFECTIVE_DATE");
        String strExpiredate = param.getString("EXPIRE_DATE");
        String strTransNeeded = param.getString("TRANS_NEEDED");
        String strCommnum = param.getString("COMM_NUM");
        
        //long nAmount = Long.parseLong(strAmount) * 1024;
        Long nTransfee = Long.parseLong(strTransfee) * 100;
        
        //strAmount = String.valueOf(nAmount);
        strTransfee = String.valueOf(nTransfee);
        
        reqData.setAmount(strAmount);
        reqData.setTransfee(strTransfee);
        reqData.setCommid(strCommid);
        reqData.setChannelid(strChannelid);
        reqData.setPeerbusinessid(strPeerbusinessid);
        reqData.setRemark (strRemark);
        reqData.setEffectivedate(strEffectivedate);
        reqData.setExpiredate(strExpiredate);	
        reqData.setTransneeded(strTransNeeded);
        reqData.setCommnum(strCommnum);
    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        return new FlowChargeReqData();
    }

}
