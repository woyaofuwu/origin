
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.queryterminalsellinfo;

import org.apache.log4j.Logger;

import com.ailk.biz.util.StaticUtil;
//import com.ailk.biz.impexp.ExportTaskExecutor;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSExportTaskExecutor;

public class ExportQueryTerminalSellInfo extends CSExportTaskExecutor
{
    private static transient final Logger logger = Logger.getLogger(ExportQueryTerminalSellInfo.class);
   
    public IDataset executeExport(IData data, Pagination page) throws Exception
    {    	
    	//IData param = data.subData("cond", false);
        IDataset result = new DatasetList();
        
		String salecampntype = data.getString("SALE_CAMPN_TYPE", "");
		
		if(!"".equals(salecampntype)&&salecampntype!=null)//根据活动类型查询
		{
			 result = CSAppCall.call("SS.QueryTerminalSellInfoSVC.queryPriceInfoByCamp", data);
		}
		else
		{
			 result = CSAppCall.call("SS.QueryTerminalSellInfoSVC.queryPriceInfoByPID", data);
		}
		
		return result;
    }      
}
    