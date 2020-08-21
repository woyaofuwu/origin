
package com.asiainfo.veris.crm.order.soa.person.busi.flow.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.flow.order.requestdata.FlowExchangeCancelReqData;



public class BuildFlowExchangeCancelReqData extends BaseBuilder implements IBuilder
{
    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
    	FlowExchangeCancelReqData reqData = (FlowExchangeCancelReqData) brd;
    	
    	String discntname = param.getString("DISCNT_NAME");   
    	String discntcode = param.getString("DISCNT_CODE");  
		String itemtype = param.getString("ITEM_TYPE");
		String itemvalue = param.getString("ITEM_VALUE");
		String balance = param.getString("BALANCE");
		String startdate = param.getString("START_DATE");
		String enddate = param.getString("END_DATE");
		String carryovertag = param.getString("CARRY_OVER_TAG");
		String detailitem = param.getString("DETAIL_ITEM");
		String userbegindate = param.getString("USER_BEGIN_DATE");
		String userenddate = param.getString("USER_END_DATE");
		String resinsid = param.getString("RES_INS_ID");
		String remark = param.getString("REMARK");
		String returnvalue = param.getString("RETURN_VALUE");
    	 
        
        reqData.setDiscntname(discntname);
        reqData.setDiscntcode(discntcode);
        reqData.setItemtype(itemtype);
        reqData.setItemvalue(itemvalue);
        reqData.setBalance(balance);	
        reqData.setStartdate(startdate);
        reqData.setEnddate(enddate);
        reqData.setCarryovertag(carryovertag);
        reqData.setDetailitem(detailitem);
        reqData.setUserbegindate(userbegindate);
        reqData.setUserenddate(userenddate);
        reqData.setResinsid(resinsid);
        reqData.setRemark(remark);
        reqData.setReturnvalue(returnvalue);
        
    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        return new FlowExchangeCancelReqData();
    }

}
