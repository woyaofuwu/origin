
package com.asiainfo.veris.crm.order.soa.person.busi.changecustinfo.order.action.finish;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.ResException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.IPrintFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustPersonInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustVipInfoQry;

/**
 * 客户资料TF_F_CUST_PERSON_OTHER，afterAction 
 * 过户+客户资料变更
 * @author fangwz
 */
public class ModifyCustPersonOtherAction implements IPrintFinishAction
{
	private static transient final Logger logger = Logger.getLogger(ModifyCustPersonOtherAction.class);
	@Override
	public void executeAction(IData mainTrade) throws Exception {
		// TODO Auto-generated method stub
		String tradeTypeCode = mainTrade.getString("TRADE_TYPE_CODE");
		String strCustId = mainTrade.getString("CUST_ID");
		IData inparam = new DataMap(mainTrade.getData("RSRV_STR6"));
		String tag = mainTrade.getString("RSRV_STR7");
       if("OtherAction".equals(tag)&&IDataUtil.isNotEmpty(inparam)){
		IDataset list = CustPersonInfoQry.qryCustPersonOtherByCustId(strCustId);
		if( IDataUtil.isNotEmpty(list) ){
			IData custPersonOtherData = list.first();
        	custPersonOtherData.put("UPDATE_TIME", SysDateMgr.getSysTime());
        	custPersonOtherData.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        	custPersonOtherData.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        	custPersonOtherData.put("USE_NAME", inparam.getString("USE_NAME",custPersonOtherData.getString("USE_NAME")));
        	custPersonOtherData.put("USE_PSPT_TYPE_CODE", inparam.getString("USE_PSPT_TYPE_CODE",custPersonOtherData.getString("USE_PSPT_TYPE_CODE")));
        	custPersonOtherData.put("USE_PSPT_ID", inparam.getString("USE_PSPT_ID",custPersonOtherData.getString("USE_PSPT_ID")));
        	custPersonOtherData.put("USE_PSPT_ADDR", inparam.getString("USE_PSPT_ADDR",custPersonOtherData.getString("USE_PSPT_ADDR")));
        	custPersonOtherData.put("RSRV_STR1", inparam.getString("RSRV_STR1",custPersonOtherData.getString("RSRV_STR1")));
        	custPersonOtherData.put("RSRV_STR2", inparam.getString("RSRV_STR2",custPersonOtherData.getString("RSRV_STR2")));
        	custPersonOtherData.put("RSRV_STR3", inparam.getString("RSRV_STR3",custPersonOtherData.getString("RSRV_STR3")));
        	custPersonOtherData.put("RSRV_STR4", inparam.getString("RSRV_STR4",custPersonOtherData.getString("RSRV_STR4")));
        	custPersonOtherData.put("RSRV_STR5", inparam.getString("RSRV_STR5",custPersonOtherData.getString("RSRV_STR5")));
			Dao.update("TF_F_CUST_PERSON_OTHER", custPersonOtherData, new String[] { "PARTITION_ID", "CUST_ID" });
		}else{
			IData custPersonOtherData = new DataMap();
        	custPersonOtherData.put("PARTITION_ID", inparam.getString("PARTITION_ID"));
        	custPersonOtherData.put("CUST_ID", strCustId);
        	custPersonOtherData.put("USE_NAME", inparam.getString("USE_NAME",""));
        	custPersonOtherData.put("USE_PSPT_TYPE_CODE", inparam.getString("USE_NAME",""));
        	custPersonOtherData.put("USE_PSPT_ID", inparam.getString("USE_NAME",""));
        	custPersonOtherData.put("USE_PSPT_ADDR", inparam.getString("USE_NAME",""));
        	custPersonOtherData.put("CREATE_TIME", SysDateMgr.getSysTime());
        	custPersonOtherData.put("UPDATE_TIME", SysDateMgr.getSysTime());
        	custPersonOtherData.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        	custPersonOtherData.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        	custPersonOtherData.put("REMARK", inparam.getString("REMARK",""));
        	custPersonOtherData.put("RSRV_STR1", inparam.getString("RSRV_STR1",""));
        	custPersonOtherData.put("RSRV_STR2", inparam.getString("RSRV_STR2",""));
        	custPersonOtherData.put("RSRV_STR3", inparam.getString("RSRV_STR3",""));
        	custPersonOtherData.put("RSRV_STR4", inparam.getString("RSRV_STR4",""));
        	custPersonOtherData.put("RSRV_STR5", inparam.getString("RSRV_STR5",""));
			Dao.insert("TF_F_CUST_PERSON_OTHER", custPersonOtherData);
		}
       }
	}
}
