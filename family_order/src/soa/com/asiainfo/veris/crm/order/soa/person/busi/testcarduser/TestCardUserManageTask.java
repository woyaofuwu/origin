
package com.asiainfo.veris.crm.order.soa.person.busi.testcarduser;

import org.apache.log4j.Logger;

import com.ailk.biz.impexp.ExportTaskExecutor;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;

/**
 * REQ201609060001_2016年下半年测试卡功能优化（二）
 * <br/>
 * 测试卡类型导出
 * @author zhuoyingzhi
 * 20160930
 *
 */
public class TestCardUserManageTask extends ExportTaskExecutor
{
	static  Logger logger=Logger.getLogger(TestCardUserManageTask.class);
	
    @Override
    public IDataset executeExport(IData param, Pagination pagination) throws Exception
    {
    	try {
    		//获取查询条件
        	IData input = param.subData("cond", true); 
        	IDataset  result = CSAppCall.call("SS.TestCardUserManageSVC.queryTestCardUserinfo",input);
        	return result;
		} catch (Exception e) {
			// TODO: handle exception
			if(logger.isInfoEnabled()) logger.info(e);
			throw e;
		}
    } 
}
