
package com.asiainfo.veris.crm.order.soa.person.busi.vipsimbak.order.action;

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
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustVipInfoQry;

/**
 * 大客户备卡，afterAction 
 * 
 * @author ray
 */
public class DealVipSimBakAction implements IPrintFinishAction
{
	private static transient final Logger logger = Logger.getLogger(DealVipSimBakAction.class);
	 /**
     * 生成大客户备卡表数据
     * 
     * @author songzy
     * @param pd
     * @param td
     * @throws Exception
     */
	@Override
	public void executeAction(IData mainTrade) throws Exception {
		// TODO Auto-generated method stub
		String tradeTypeCode = mainTrade.getString("TRADE_TYPE_CODE");
		
		String newSimBakNo = mainTrade.getString("RSRV_STR10","");
		
		if("146".equals(tradeTypeCode) && mainTrade.getString("RSRV_STR9") != null){
			IData simBakInfo = new DataMap(mainTrade.getString("RSRV_STR9"));
			IData inparam = new DataMap();
	        inparam.put("SIM_CARD_NO", newSimBakNo);
	        // TODO TF_F_CUST_VIPSIMBAK-SEL_BY_SIM
	        IDataset dataset = CustVipInfoQry.getVipSimbakInfos(newSimBakNo);
	        if (IDataUtil.isNotEmpty(dataset))
	        {
	            CSAppException.apperr(ResException.CRM_RES_21, newSimBakNo);
	        }

	        inparam.put("SIM_TYPE_CODE", simBakInfo.getString("SIM_TYPE_CODE",""));
	        inparam.put("IMSI", simBakInfo.getString("IMSI",""));
	        inparam.put("SEND_DATE", simBakInfo.getString("SEND_DATE",""));
	        inparam.put("START_DATE", "");
	        inparam.put("ACT_TAG", "0");
	        inparam.put("VIP_ID", simBakInfo.getString("VIP_ID",""));
	        inparam.put("CLIENT_INFO2", simBakInfo.getString("CLIENT_INFO2",""));
	        inparam.put("CLIENT_INFO3", simBakInfo.getString("CLIENT_INFO3",""));
	        inparam.put("CLIENT_INFO4", "0");
	        inparam.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
	        inparam.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
	        inparam.put("REMARK", "");

	        Dao.insert("TF_F_CUST_VIPSIMBAK", inparam);
		}else if("144".equals(tradeTypeCode)){
			
			IData inparam = new DataMap();

	        IDataset dataset = CustVipInfoQry.getVipSimbakInfos(newSimBakNo);
	        if (IDataUtil.isNotEmpty(dataset))
	        {

	            inparam.put("SIM_CARD_NO", newSimBakNo);
	            inparam.put("ACT_TAG", "0");
	            inparam.put("START_DATE", SysDateMgr.getSysTime());
	            inparam.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
	            inparam.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
	            inparam.put("REMARK", "备卡激活");

	            Dao.executeUpdateByCodeCode("TF_F_CUST_VIPSIMBAK", "UPD_SIMBAKACT", inparam);
	        }
	        else
	        {
	            CSAppException.apperr(CrmUserException.CRM_USER_292);
	        }
			
		}else if("147".equals(tradeTypeCode)){
			
			IData inparam = new DataMap();

	        IDataset dataset = CustVipInfoQry.getVipSimbakInfos(newSimBakNo);

	        if (IDataUtil.isNotEmpty(dataset))
	        {

	            inparam.put("SIM_CARD_NO", newSimBakNo);
	            inparam.put("ACT_TAG", "0");

	            Dao.executeUpdateByCodeCode("TF_F_CUST_VIPSIMBAK", "DEL_SIMCARDAPP", inparam);

	        }
	        else
	        {
	            CSAppException.apperr(CrmUserException.CRM_USER_292);
	        }
		}
		
	}
}
