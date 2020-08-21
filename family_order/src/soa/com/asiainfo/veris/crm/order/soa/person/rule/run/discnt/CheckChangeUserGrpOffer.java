package com.asiainfo.veris.crm.order.soa.person.rule.run.discnt;

import org.apache.log4j.Logger;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.group.grouprule.ErrorMgrUtil;

public class CheckChangeUserGrpOffer extends BreBase implements IBREScript{
	 private static Logger logger = Logger.getLogger(CheckChangeUserGrpOffer.class);
	@Override
	public boolean run(IData databus, BreRuleParam ruleParam) throws Exception {
		if (logger.isDebugEnabled())
        {
        	logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 CheckChangeUserGrpOffer >>>>>>>>>>>>>>>>>>");
        }
		
		String err = "";        
        String errCode = ErrorMgrUtil.getErrorCode(databus);
        String userElementsStr = databus.getString("ALL_SELECTED_ELEMENTS"); // 所有选择的元素
        String meb_userId = databus.getString("USER_ID_B", "-1");//用户ID
        String grp_userId = databus.getString("USER_ID", "-1");//集团用户ID
        
        if (StringUtils.isBlank(userElementsStr))
        {
            return true;
        }
        IDataset userElements = new DatasetList(userElementsStr);
        
        
        //根据USER_ID在TF_F_USER_DISCNT表中检索数据
	    IData param = new DataMap();
        param.put("USER_ID",meb_userId);
        
        IDataset userDiscnts = Dao.qryByCode("TF_F_USER_DISCNT", "SEL_BY_USERID", param, BizRoute.getRouteId());
        
        logger.debug("userDiscnts:"+userDiscnts.toString());
        
        
        for(int i = 0; i < userElements.size(); i++)
        {
            IData discnt = userElements.getData(i);
            String element_id = discnt.getString("ELEMENT_ID", "");
            if(StringUtils.isBlank(element_id))
            {
                return true;
            }
            
            String modify_tag = discnt.getString("MODIFY_TAG","");
            String eleTypeCode = discnt.getString("ELEMENT_TYPE_CODE","");
            if(!"0".equals(modify_tag)&&!"2".equals(modify_tag))
            {
                continue;
            }
            
            
            if(StringUtils.isNotBlank(element_id) && "0".equals(modify_tag))
            {
            	if(userDiscnts!=null&&userDiscnts.size()>0){
     		    	for(int j=0, userDiscntsSize=userDiscnts.size(); j<userDiscntsSize; j++){
     		    		    IData userDiscnt = userDiscnts.getData(j);
     		    		   logger.debug("userDiscnts["+j+"]:"+userDiscnt.toString());
     		    		    param = new DataMap();
     		    	        param.put("SUBSYS_CODE", "CSM");
     		    	        param.put("PARAM_ATTR", "7019");
     		    	        param.put("PARA_CODE1", "0123");
     		    	        param.put("PARAM_CODE", userDiscnt.get("DISCNT_CODE"));
     		    	        param.put("EPARCHY_CODE", "0898");
     		    	        IDataset comparas = Dao.qryByCode("TD_S_COMMPARA", "SEL3_PK_TD_S_COMMPARA", param, null, Route.CONN_CRM_CEN);
     		    	       
     		    	        if(comparas!=null&&comparas.size()>0){
     		    	        	logger.debug("comparas:"+comparas.toString());
     		    	        	//用户为一起一码一策套餐
     		    	        	//存在市场折扣优惠，则继续判断是否存在一企一码一策套餐，如存在则不能办理市场折扣套餐
		    						BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 150001, "你是一企一码一策套餐用户，请先取消一企一码一策套餐，在办理其他手机折扣套餐！");
		    						return false;
     		    	        }
     		    	}
     			 }
            }  
        }
		return true;
    }
}
