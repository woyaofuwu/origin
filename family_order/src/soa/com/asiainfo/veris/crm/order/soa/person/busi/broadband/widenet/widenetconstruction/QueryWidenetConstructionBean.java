package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.widenetconstruction;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.person.common.query.broadband.widenetconstruction.WidenetConstructionQry;

public class QueryWidenetConstructionBean extends CSBizBean
{

	private static final long serialVersionUID = 1L;

	/**
	 * 
	 * @param params
	 * @return
	 * @throws Exception
	 */
    public IDataset queryWidenetConstsInfo(IData inparams, Pagination pagination) throws Exception
    {
    	return WidenetConstructionQry.queryWidenetConstsInfo(inparams, pagination);
    }
    

    /**
     * 稽核通过
     * @param input
     * @throws Exception
     */
    public void updateConstsPass(IData input) throws Exception
    {
    	String remark = input.getString("REMARK");
    	String staffId = getVisit().getStaffId();
    	if(IDataUtil.isNotEmpty(input)){
    		IDataset numberList = input.getDataset("NUMBER_LIST");
    		if(IDataUtil.isNotEmpty(numberList)){
    			for(int i=0; i < numberList.size(); i++){
    				IData numberData = numberList.getData(i);
    				String tradeId = numberData.getString("TRADE_ID","");
    				if(tradeId != null && !"".equals(tradeId)){
    					
    					IData inputData = new DataMap(); 
    					inputData.put("TRADE_ID", tradeId);
    					inputData.put("DEAL_TAG", "1");//稽核通过
    					inputData.put("DEAL_DATE", SysDateMgr.getSysTime());//稽核时间
    					inputData.put("DEAL_STAFF_ID", staffId);//稽核人员
    					if(StringUtils.isNotBlank(remark)){
    						inputData.put("REMARK", remark);
    					}
    					Dao.executeUpdateByCodeCode("TF_B_CONSTRUCTION_ADDR", "UPDATE_CONTRS_BY_TRADEID", inputData);
    				}
    			}
    		}
    	}
    }
    
    
    /**
     * 稽核不通过
     * @param input
     * @throws Exception
     */
    public void updateConstsNoPass(IData input) throws Exception
    {
    	String remark = input.getString("REMARK");
    	String staffId = getVisit().getStaffId();
    	
    	if(IDataUtil.isNotEmpty(input)){
    		IDataset numberList = input.getDataset("NUMBER_LIST");
    		if(IDataUtil.isNotEmpty(numberList)){
    			for(int i=0; i < numberList.size(); i++){
    				IData numberData = numberList.getData(i);
    				String tradeId = numberData.getString("TRADE_ID","");
    				if(tradeId != null && !"".equals(tradeId)){
    					
    					IData inputData = new DataMap(); 
    					inputData.put("TRADE_ID", tradeId);
    					inputData.put("DEAL_TAG", "2");//稽核不通过
    					inputData.put("DEAL_DATE", SysDateMgr.getSysTime());//稽核时间
    					inputData.put("DEAL_STAFF_ID", staffId);//稽核人员
    					inputData.put("RSRV_STR1", "2");//2标识还没有下发短号通知施工人员，
    					if(StringUtils.isNotBlank(remark)){
    						inputData.put("REMARK", remark);
    					}
    					int resultInt = Dao.executeUpdateByCodeCode("TF_B_CONSTRUCTION_ADDR", "UPDATE_CONTRS_BY_TRADEID", inputData);
    				}
    			}
    		}
    	}
    }
}
