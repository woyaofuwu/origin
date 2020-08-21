
package com.asiainfo.veris.crm.order.soa.person.busi.valuecard;

import org.apache.log4j.Logger;

import com.ailk.biz.impexp.ExportTaskExecutor;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;

/**
 * 有价卡赠送清单导出
 * @author zyz
 *
 */
public class GiveValueCardDetailedTask extends ExportTaskExecutor
{
	static  Logger logger=Logger.getLogger(GiveValueCardDetailedTask.class);
	
    @Override
    public IDataset executeExport(IData param, Pagination pagination) throws Exception
    {
    	try {
    		//获取查询条件
        	IData input = param.subData("cond", true); 
        	IDataset  result = CSAppCall.call("SS.GiveValueCardDetailedSVC.queryValueCardDetailedInfo",input);
        	return result;
		} catch (Exception e) {
			// TODO: handle exception
			if(logger.isInfoEnabled()) logger.info(e);
			throw e;
		}
    } 
}
