
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.bat;

import com.ailk.biz.impexp.ImportTaskExecutor;
import com.ailk.cache.memcache.util.SharedCache;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.cache.CacheKey;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;

public class BatDataImportByFileForBboss extends ImportTaskExecutor
{
    /**
     * @description BBOSS一点支付业务需要将excel附件转化为XML格式
     * @author xunyl
     * @date 2015-10-27
     */
    public IDataset executeImport(IData inparam,IDataset dataList)throws Exception{    	
    	//1- 定义返回值
    	IDataset returnInfoList = new DatasetList();   
    	
    	//2- 根据传入的属性编号放入对应的模板编号
    	String batchOperCode = "";
    	String attrCode = inparam.getString("ATTR_CODE");
    	if(StringUtils.equals(attrCode,"999033717")){
    		batchOperCode = "BATADDYDZFMEM";
    	}else if(StringUtils.equals(attrCode,"999033734")){
    		batchOperCode = "BATCONFIRMYDZFMEM";
    	}else if(StringUtils.equals(attrCode,"999033735")){    		
    		batchOperCode = "BATOPENYDZFMEM";
    	}
    	inparam.put("BATCH_OPER_CODE", batchOperCode);
    	IData tempData = new DataMap();
    	tempData.put("BBOSS", "BBOSS");
    	inparam.put("CODING_STR",tempData.toString());//没有实际作用，为了校验通过
    	inparam.put("BATCH_TASK_NAME","BBOSS批量特殊处理");
    	inparam.put("BATCH_OPER_NAME","BBOSS批量特殊处理");
    	
    	//3- 导入数据，利用已有的批量处理
    	BatDataImportByFile commDealClass = new BatDataImportByFile();
    	inparam.put("BATCH_OPER_TYPE", inparam.getString("BATCH_OPER_CODE"));
    	String batchTaskId = inparam.getString("BATCH_TASK_ID");
    	inparam.put("BATCH_ID",batchTaskId);
    	inparam.put("ACCEPT_MONTH", batchTaskId.substring(4, 6));
    	inparam.put(Route.USER_EPARCHY_CODE, getTradeEparchyCode()); 
    	returnInfoList = commDealClass.executeImport(inparam,dataList);
    	
    	//4- 自动启动批量    	    	   
    	BatDealBean batDealBean = new BatDealBean();
    	IDataset fileNameList =batDealBean.startBBossYDZFBatDeals(inparam);
        String fileName = fileNameList.get(0).toString();             
        
    	//5- 保存批量名称到缓存中
        String key = CacheKey.getBossBatchInfoKey(batchTaskId);
        SharedCache.set(key, fileName, 1200);
        
        //6- 返回值
        return returnInfoList;
    }
}
