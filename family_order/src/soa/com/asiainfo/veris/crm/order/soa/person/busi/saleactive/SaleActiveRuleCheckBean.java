 
package com.asiainfo.veris.crm.order.soa.person.busi.saleactive;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;



public class SaleActiveRuleCheckBean extends CSBizBean
{
	/**
     * 查询TD_S_MODFILE表取菜单地址
     */
    public static IDataset qryModfileInfo(IData inparams) throws Exception
    { 
    	SQLParser parser = new SQLParser(inparams); 
    	parser.addSQL(" select T1.MENU_TITLE, t.* "); 
    	parser.addSQL(" from ucr_sys.TD_S_MODFILE t, TD_B_SYSTEMGUIMENU t1 "); 
    	parser.addSQL(" where t.mod_code = t1.menu_id "); 
    	parser.addSQL(" and t.mod_code = :MOD_CODE "); 
    	return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
    }
    
    /**
     * REQ201606020021  补充保底消费送宽带活动参数的开发需求 
     * chenxy3 2016-06-15
     * 获取所有约定消费送宽带相关的套餐编码
     * */
    public static IDataset queryGiveWilenActiveInfo(IData input) throws Exception
    {
//    	IData param = new DataMap(); 
//    	param.put("PRODUCT_ID", input.getString("PRODUCT_ID"));
//    	IDataset results = Dao.qryByCode("TD_B_PACKAGE_ELEMENT", "SEL_DISCNT_WILEN_BY_PRODID", param, Route.CONN_CRM_CEN);
       //modify by　　duhj 2017/5/25
       IDataset forceDiscntList = UpcCall.qryAllOffersByOfferIdWithForceTagDefaultTagFilter(BofConst.ELEMENT_TYPE_CODE_PACKAGE, input.getString("PRODUCT_ID"),"","");       
        if(IDataUtil.isNotEmpty(forceDiscntList)){
        	for(int i=0;i<forceDiscntList.size();i++){
        		IData ss= forceDiscntList.getData(i);
        		String offerType=ss.getString("OFFER_TYPE");
        		
        		if(!StringUtils.equals(offerType, "D")){
        			forceDiscntList.remove(i);
        			i--;
        		}else{
        			ss.put("DISCNT_ID", ss.getString("OFFER_CODE"));
        			ss.put("ELEMENT_TYPE_CODE", ss.getString("OFFER_TYPE"));
        			ss.put("PRODUCT_ID", input.getString("PRODUCT_ID"));

        		}
        	
        	}
        }
    	
    	return forceDiscntList ;
    }
}