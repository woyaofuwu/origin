
package com.asiainfo.veris.crm.order.soa.person.busi.flow.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.flow.order.requestdata.FlowExchangeReqData;



public class BuildFlowExchangeReqData extends BaseBuilder implements IBuilder
{
    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        // TODO Auto-generated method stub

    	FlowExchangeReqData reqData = (FlowExchangeReqData) brd;
    	String strFmBalanceid = param.getString("FM_BALANCE_ID");
    	String strFmAcctID = param.getString("FM_ACCT_ID");
    	//long nBalance = param.getLong("BALANCE", 0);
    	String strBlance = param.getString("BALANCE");
        String strCommid = param.getString("COMM_ID");
        String strChannelid = CSBizBean.getVisit().getInModeCode();
        String strfmassettypeid = param.getString("FM_ASSET_TYPE_ID");
        //long nInitFlow = param.getLong("INIT_FLOW", 0);
        String strInitFlow = param.getString("INIT_FLOW");
        Long nInitFee = param.getLong("INIT_FEE", 0);
        String strRemark = param.getString("REMARK");
        String strEffectivedate = param.getString("EFFECTIVE_DATE");
        String strExpiredate = param.getString("EXPIRE_DATE");
        //long nTransValue = param.getLong("TRANS_VALUE", 0);
        String strTransValue = param.getString("TRANS_VALUE");
        
		//nBalance = nBalance * 1024;
		//String strBlance = String.valueOf(nBalance);
		
		//nInitFlow = nInitFlow * 1024;
		//String strInitFlow = String.valueOf(nInitFlow);
		
		nInitFee = nInitFee * 100;
		String strInitFee = String.valueOf(nInitFee);
		
		//nTransValue = nTransValue * 1024;
		//String strTransValue = String.valueOf(nTransValue);
        
        reqData.setFmbalanceid(strFmBalanceid);
        reqData.setFmacctid(strFmAcctID);
        reqData.setBalance(strBlance);
        reqData.setCommid(strCommid);
        reqData.setChannelid(strChannelid);
        reqData.setFmassettypeid(strfmassettypeid);
        reqData.setInitflow(strInitFlow);
        reqData.setInitfee(strInitFee);
        reqData.setRemark (strRemark);
        reqData.setEffectivedate(strEffectivedate);
        reqData.setExpiredate(strExpiredate);
        reqData.setTransvalue(strTransValue);	
    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        return new FlowExchangeReqData();
    }

}
