package com.asiainfo.veris.crm.order.soa.person.busi.beforecheck;
  
import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset; 
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry; 

/** 
 * REQ201602290007 关于入网业务人证一致性核验提醒的需求
 * @chenxy3@2016-3-7 修改 
 */
public class BeforeCheckSVC extends CSBizService
{
	private static final Logger log = Logger.getLogger(BeforeCheckSVC.class);
    /**
     * 查询是否开启业务前校验
     * 先取1210是否为开启校验状态
     * 1、开启，要再取1119是否存在开启检查的项，如果一个项都没有，那么等于不开启校验
     * 2、不开启，就不开启咯
     */
    public IDataset checkNeedBeforeCheck(IData inparams) throws Exception
    {
    	IDataset rtnSet=new DatasetList();
    	IDataset checkFlag= CommparaInfoQry.getCommPkInfo("CSM", "1210", "BEFORECHECK", "0898"); 
    	IDataset checkList=getBeforeCheckList(new DataMap());
    	if(checkList!=null && checkList.size()>0){
    		rtnSet=checkFlag;
    	}else{
    		IData rtnMap=new DataMap();
    		rtnMap.put("PARA_CODE1", "0");
    		rtnSet.add(rtnMap);
    	}
    	return rtnSet;
    }
    
    /**
     * 获取业务前校验内容列表
     */
    public IDataset getBeforeCheckList(IData inparams) throws Exception
    {
    	IDataset rtnSet=new DatasetList();
    	//log.info("("********<CXY>********getBeforeCheckList.inparams"+inparams);
    	IDataset checklist= CommparaInfoQry.getCommByParaAttr("CSM", "1119",  "0898");
    	if(checklist!=null && checklist.size()>0){
    		for(int k=0;k<checklist.size();k++){
    			IData rtnData=new DataMap();
	    		String ruleCode=checklist.getData(k).getString("PARAM_CODE");
	    		String checkTag=checklist.getData(k).getString("PARA_CODE1");
	    		String tradeType=checklist.getData(k).getString("PARA_CODE3");
	    		if("1".equals(checkTag)){
	    			if("-1".equals(tradeType) || inparams.getString("TRADE_TYPE_CODE","").equals(tradeType)){
	    				rtnSet.add(checklist.getData(k));
	    			} 
	    		}
	    	}
    	}
    	return rtnSet;
    }
}