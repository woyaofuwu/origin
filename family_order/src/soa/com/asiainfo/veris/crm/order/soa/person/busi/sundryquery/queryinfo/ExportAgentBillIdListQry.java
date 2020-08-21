
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.queryinfo;

import org.apache.log4j.Logger;

import com.ailk.biz.util.StaticUtil;
//import com.ailk.biz.impexp.ExportTaskExecutor;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSExportTaskExecutor;

public class ExportAgentBillIdListQry extends CSExportTaskExecutor
{
    private static transient final Logger logger = Logger.getLogger(ExportAgentBillIdListQry.class);
   
    public IDataset executeExport(IData data, Pagination page) throws Exception
    {    	
    	//IData param = data.subData("cond", false);
        IDataset result = CSAppCall.call("SS.QueryInfoSVC.qryAgentUserListByCond", data);
        return result;
    }      
}
    