
package com.asiainfo.veris.crm.order.soa.person.busi.score.scoreexchange.order.action.undo;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;

/**
 * REQ201607220020 关于2016预存话费送VOLTE手机营销活动的开发需求
 * chenxy3 2016-08-26
 * 返销.
 * 1、返销已发送红包 RSRV_VALUE=1
 * 2、冲正已完工红包扣款 RSRV_VALUE=3
 * */
public class UndoRedPackTradeAction implements ITradeFinishAction
{

    @Override
    public void executeAction(IData mainTrade) throws Exception
    {
    	boolean undoSucc=false;
        String tradeId = mainTrade.getString("TRADE_ID");
        String userId = mainTrade.getString("USER_ID"); 
        IData data = new DataMap();
        data.put("USER_ID", userId);
        data.put("CANCEL_TAG", "1");//代表返销， 1=返销
        data.put("RSRV_VALUE", "1"); //撤销发送的红包 
        data.put("TRADE_ID", tradeId); 
        IDataset selResult0= Dao.qryByCode("TF_B_TRADE", "SEL_TRADE_IF_RED_PAK_PRODUCT", data,Route.getJourDb());
        
        if(selResult0!=null &&selResult0.size()>0){ 
	        IDataset selResult1= Dao.qryByCode("TF_F_USER_OTHER", "SEL_OTHER_BY_VALUE_CODE_RSRV4", data);
	        if(selResult1!=null &&selResult1.size()>0){ 
	        	/**
	        	 * 只针对红包发送返销
	        	 * 获取红包有效期，如果红包已过期，则不再调用接口，否则报错无法返销。0=未过期 1=过期
	        	 * */
	        	String redPakLimtDate=selResult1.getData(0).getString("RED_PAK_LIMIT_FLAG","");
	        	if("0".equals(redPakLimtDate)){
		        	IDataset callResults1=CSAppCall.call("CS.SaleActiveQuerySVC.backRedPack", data); 
		        	if(IDataUtil.isNotEmpty(callResults1)){
		        		String x_resultcode=callResults1.getData(0).getString("X_RESULTCODE","");
		        		if("1".equals(x_resultcode)){
		        			undoSucc=true;
		        		} 
		        	}else{
		        		CSAppException.apperr(CrmCommException.CRM_COMM_103, "和包平台红包发放撤销失败！失败信息：接口backRedPack调用返回为空！");
		        	}
	        	}else{//红包超过24小时（根据参数6898，可能是48小时），则不调接口了，直接更新为返销。
	        		IData updData=new DataMap();
	        		updData.put("USER_ID", userId);
	        		updData.put("RSRV_VALUE", "6");
	        		updData.put("RSRV_STR17", "1"); //返销
	        		updData.put("RSRV_STR19", CSBizBean.getVisit().getStaffId()); 
	        		updData.put("RSRV_STR20", CSBizBean.getVisit().getDepartId());
	        		updData.put("RSRV_STR30", "红包超时，不调用接口，直接返销。查看RSRV_STR18返销时间。");
	        		updData.put("END_DATE", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
	        		updData.put("RSRV_VALUE_COND", "1");  //条件，状态是已发红包的
	                Dao.executeUpdateByCodeCode("TF_F_USER_OTHER", "UPD_OTHER_CANCEL_RED_PAK", updData);
	        	}
	        }else{
		        data.put("RSRV_VALUE", "3"); //撤销已完工的红包扣款
		        IDataset selResult2= Dao.qryByCode("TF_F_USER_OTHER", "SEL_OTHER_BY_VALUE_CODE_RSRV4", data);
		        if(selResult2!=null &&selResult2.size()>0){ 
		        	IDataset callResults2=CSAppCall.call("CS.SaleActiveQuerySVC.returnRedPackPay", data);
		        	if(IDataUtil.isNotEmpty(callResults2)){
		        		String x_resultcode=callResults2.getData(0).getString("X_RESULTCODE","");
		        		if("1".equals(x_resultcode)){
		        			undoSucc=true;
		        		} 
		        	}else{
		        		CSAppException.apperr(CrmCommException.CRM_COMM_103, "和包平台扣款撤销失败！失败信息：接口returnRedPackPay调用返回为空！");
		        	} 
		        }else{
		        	data.put("RSRV_VALUE", "2"); //查看是否未完工的返销，是2的则不允许返销。
			        IDataset selResult3= Dao.qryByCode("TF_F_USER_OTHER", "SEL_OTHER_BY_VALUE_CODE_RSRV4", data);
			        if(selResult3!=null &&selResult3.size()>0){ 
			        	//1、冲正不能传CANCEL_TAG
			        	IData data2=new DataMap();
			        	data2.put("USER_ID", userId);
			        	data2.put("RSRV_VALUE", "2");
			        	IDataset callResults3=CSAppCall.call("CS.SaleActiveQuerySVC.returnRedPackPay", data2);
			        	if(IDataUtil.isNotEmpty(callResults3)){
			        		String x_resultcode=callResults3.getData(0).getString("X_RESULTCODE","");
			        		if("1".equals(x_resultcode)){
			        			//2、红包撤销接口
			        			data.put("RSRV_VALUE", "1");
			        			callResults3=CSAppCall.call("CS.SaleActiveQuerySVC.backRedPack", data);
			        		} 
			        	} 
			        }
		        }
	        }
        }
    } 
}
