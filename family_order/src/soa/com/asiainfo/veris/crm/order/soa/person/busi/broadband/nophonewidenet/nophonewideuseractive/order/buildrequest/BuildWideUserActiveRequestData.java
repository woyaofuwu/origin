package com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.nophonewideuseractive.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.nophonewideuseractive.order.requestdata.NoPhoneWideUserActiveRequestData;

public class BuildWideUserActiveRequestData extends BaseBuilder implements IBuilder {

	public void buildBusiRequestData(IData param, BaseReqData brd)
			throws Exception {
	    
	    NoPhoneWideUserActiveRequestData request = (NoPhoneWideUserActiveRequestData) brd;
		
	    request.setCreateUserTradeId(param.getString("CREATE_USER_TRADE_ID",""));
        
        request.setFinishDate(param.getString("FINISH_DATE"));
        
        request.setDeviceId(param.getString("DEVICE_ID",""));
        
        request.setPortId(param.getString("PORT_ID",""));
        
        request.setStandAddress(param.getString("STAND_ADDRESS",""));
        
        request.setConstructionAddr(param.getString("CONSTRUCTION_ADDR",""));
        
        request.setConstPhone(param.getString("CONST_STAFF_ID",""));
        
        request.setConstPhone(param.getString("CONST_PHONE",""));
        
        request.setRsrvTag3(param.getString("RSRV_TAG3",""));
	}
	

	public BaseReqData getBlankRequestDataInstance() {
		// TODO Auto-generated method stub
		return new NoPhoneWideUserActiveRequestData();
	}

}
