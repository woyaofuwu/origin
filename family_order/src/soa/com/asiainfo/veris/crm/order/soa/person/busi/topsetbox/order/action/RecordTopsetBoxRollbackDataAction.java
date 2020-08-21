package com.asiainfo.veris.crm.order.soa.person.busi.topsetbox.order.action;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ResTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;

public class RecordTopsetBoxRollbackDataAction implements ITradeAction{

	public void executeAction(BusiTradeData btd) throws Exception{
		
		List<ResTradeData> resDatas=btd.get("TF_B_TRADE_RES");
		
		if(resDatas!=null&&resDatas.size()>0){
			ResTradeData data=resDatas.get(0);
			
			IData param=new DataMap();
			param.put("TRADE_ID",btd.getRD().getTradeId());
			param.put("USER_ID",data.getUserId());
			param.put("USER_ID_A",data.getUserIdA());
			param.put("RES_TYPE_CODE",data.getResTypeCode());
			param.put("RES_CODE",data.getResCode());
			param.put("IMSI",data.getImsi());
			param.put("KI",data.getKi());
			param.put("ARTIFICAL_SERICES_TAG",data.getRsrvNum1());
			param.put("RES_FEE",data.getRsrvNum5());
			param.put("DEVICE_COST",data.getRsrvNum4());
			param.put("PRODUCT_ID",data.getRsrvStr1());
			param.put("BASE_PACKAGE",data.getRsrvStr2());
			param.put("OPTIONAL_PACKAGE",data.getRsrvStr3());
			param.put("RES_BRAND_INFO",data.getRsrvStr4());
			param.put("WIDE_NET_ADDRESS",data.getRsrvStr5());
			param.put("INST_ID",data.getInstId());
			param.put("START_DATE",data.getStartDate());
			param.put("END_DATE",data.getEndDate());
			param.put("UPDATE_TIME",btd.getRD().getAcceptTime());
			param.put("UPDATE_STAFF_ID",CSBizBean.getVisit().getStaffId());
			param.put("UPDATE_DEPART_ID",CSBizBean.getVisit().getDepartId());
			param.put("REMARK","魔百和开户开户记录用于进行退库的验证数据");
//			param.put("RSRV_NUM1","");
			param.put("RSRV_NUM2",data.getRsrvNum2());
//			param.put("RSRV_NUM3","");
//			param.put("RSRV_NUM4","");
//			param.put("RSRV_NUM5","");
//			param.put("RSRV_STR1","");
//			param.put("RSRV_STR2","");
//			param.put("RSRV_STR3","");
//			param.put("RSRV_STR4","");
//			param.put("RSRV_STR5","");
//			param.put("RSRV_DATE1","");
//			param.put("RSRV_DATE2","");
//			param.put("RSRV_DATE3","");
//			param.put("RSRV_TAG1","");
//			param.put("RSRV_TAG2","");
//			param.put("RSRV_TAG3","");
			
			UserResInfoQry.saveRollBackUserRes(param);
			
			
		}
		
	}
	
}
