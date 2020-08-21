
package com.asiainfo.veris.crm.order.soa.person.busi.saleactive.order.action.finish.after;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.component.saleactive.SaleActiveCheckSnBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeSaleActive;

public class OldCust4GActiveAction implements ITradeFinishAction
{ 
    public void executeAction(IData mainTrade) throws Exception
    {
    	String userId=mainTrade.getString("USER_ID");
    	String tradeTypeCode="240";
    	String toDay=SysDateMgr.getSysDateYYYYMMDD();
    	String userFinalProd=mainTrade.getString("RSRV_STR1");
    	String userFinalPack=mainTrade.getString("RSRV_STR2","");
    	IData input = new DataMap();
    	input.put("USER_ID", userId);
    	input.put("TRADE_TYPE_CODE", tradeTypeCode);
    	input.put("TO_DAY", toDay);
    	input.put("RSRV_STR2", userFinalProd);
    	
    	SaleActiveCheckSnBean checkBean = BeanManager.createBean(SaleActiveCheckSnBean.class);
    	//查询是否存在临时表数据
    	IDataset oldCustInfos=checkBean.qryTempInfoByUserId(input);
    	if(oldCustInfos == null || oldCustInfos.size() <= 0){
    		input.put("RSRV_STR2", userFinalProd+"|"+userFinalPack);
    		oldCustInfos=checkBean.qryTempInfoByUserId(input);
    		if(oldCustInfos != null && oldCustInfos.size() > 0){
    			IData infos=oldCustInfos.getData(0);
        		String userProd=infos.getString("RSRV_STR2","");
        		String oldCustSn=infos.getString("RSRV_STR1","");
        		String oldCustNeedProd=infos.getString("RSRV_STR3","");
        		String oldCustUserId=infos.getString("RSRV_STR4","");
        		
    			//更新老用户的信息TF_F_USER_SALE_ACTIVE表该产品的RSRV_TAG2字段，用于标识该老用户已被校验过
    			IData updData=new DataMap();
    			updData.put("USER_ID", oldCustUserId);
    			updData.put("PRODUCT_ID", oldCustNeedProd.substring(0, 8));
    			updData.put("PACKAGE_ID", oldCustNeedProd.substring(9, 17));
    			checkBean.updCheckSnTagByPack(updData);
    			
    			IData delData=new DataMap();
    			delData.put("TRADE_TYPE_CODE", "240");
    			delData.put("USER_ID", userId);
    			delData.put("TO_DAY", toDay);
    			delData.put("USER_PRODUCT", userProd);
    			delData.put("CHECK_SN", oldCustSn);  
    			checkBean.delTemplate(delData); 
    		}else{
    			input.put("RSRV_STR2", "Y");
        		oldCustInfos=checkBean.qryTempInfoByUserId(input);
        		if(oldCustInfos != null && oldCustInfos.size() > 0){
        			IData infos=oldCustInfos.getData(0);
            		String userProd=infos.getString("RSRV_STR2","");
            		String oldCustSn=infos.getString("RSRV_STR1","");
            		String oldCustNeedProd=infos.getString("RSRV_STR3","");
            		String oldCustUserId=infos.getString("RSRV_STR4","");
            		
            		IData otherData = new DataMap();  
			        otherData.put("USER_ID", userId);
			        otherData.put("RSRV_VALUE_CODE", "4GGSALEACTIVE");
			        otherData.put("RSRV_VALUE", "1");
			        otherData.put("RSRV_STR1", oldCustSn);
			        otherData.put("RSRV_STR2", userProd);
			        otherData.put("RSRV_STR3", oldCustNeedProd);
			        otherData.put("RSRV_STR4", oldCustUserId);
			        otherData.put("RSRV_STR5", "");
			        otherData.put("RSRV_STR6", "");
			        otherData.put("RSRV_STR7", "");
			        otherData.put("RSRV_STR8", "");
			        otherData.put("RSRV_STR9", "");

			        otherData.put("RSRV_STR10", "");
			        otherData.put("RSRV_STR11", "");
			        otherData.put("RSRV_STR12", "");
			        otherData.put("RSRV_STR13", "");
			        otherData.put("RSRV_STR14", "");
			        otherData.put("RSRV_STR15", "");
			        otherData.put("RSRV_STR16", "");

			        otherData.put("RSRV_STR17", "");
			        otherData.put("RSRV_STR18", "");
			        otherData.put("RSRV_STR19", "");
			        otherData.put("RSRV_STR20", "");
			        otherData.put("RSRV_STR21", "");
			        otherData.put("RSRV_STR22", "");
			        otherData.put("RSRV_DATE1", null);
			        otherData.put("RSRV_DATE2", null);
			        otherData.put("RSRV_DATE3", null);
			        
			        otherData.put("START_DATE", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
			        otherData.put("END_DATE", SysDateMgr.addMonths(SysDateMgr.getSysDate(), 1));
			        otherData.put("TRADE_ID", mainTrade.getString("TRADE_ID",""));
			        otherData.put("STAFF_ID", mainTrade.getString("TRADE_STAFF_ID",""));
			        otherData.put("DEPART_ID", mainTrade.getString("TRADE_DEPART_ID",""));
			        otherData.put("UPDATE_STAFF_ID", mainTrade.getString("TRADE_STAFF_ID",""));
			        otherData.put("UPDATE_DEPART_ID", mainTrade.getString("TRADE_DEPART_ID",""));
			        otherData.put("UPDATE_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
			        otherData.put("REMARK", "新春有礼乡镇4G+终端普及活动"); 

			        Dao.executeUpdateByCodeCode("TF_F_USER_OTHER", "INS_USER_OTHER", otherData); 
        			
        			IData delData=new DataMap();
        			delData.put("TRADE_TYPE_CODE", "240");
        			delData.put("USER_ID", userId);
        			delData.put("TO_DAY", toDay);
        			delData.put("USER_PRODUCT", userProd);
        			delData.put("CHECK_SN", oldCustSn);  
        			checkBean.delTemplate(delData); 
        		}
    		}
    		
    		
    	}
    	else{
    		IData infos=oldCustInfos.getData(0);
    		String userProd=infos.getString("RSRV_STR2","");
    		String oldCustSn=infos.getString("RSRV_STR1","");
    		String oldCustNeedProd=infos.getString("RSRV_STR3","");
    		String oldCustUserId=infos.getString("RSRV_STR4","");
    		
			//更新老用户的信息TF_F_USER_SALE_ACTIVE表该产品的RSRV_TAG2字段，用于标识该老用户已被校验过
			IData updData=new DataMap();
			updData.put("USER_ID", oldCustUserId);
			updData.put("PRODUCT_ID", oldCustNeedProd);
			checkBean.updCheckSnTag(updData);
			
			IData delData=new DataMap();
			delData.put("TRADE_TYPE_CODE", "240");
			delData.put("USER_ID", userId);
			delData.put("TO_DAY", toDay);
			delData.put("USER_PRODUCT", userProd);
			delData.put("CHECK_SN", oldCustSn);  
			checkBean.delTemplate(delData);    		
    	}
    }
}