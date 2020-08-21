
package com.asiainfo.veris.crm.order.web.person.internetofthings;

import org.apache.tapestry.IRequestCycle;

import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.parser.ExcelConfig;
import com.ailk.common.util.parser.ImpExpUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * 物联网服务状态批量查询
 * 
 * @author fengwp
 */
public abstract class StateBatchQueryService extends PersonBasePage
{
	
	
	
    /**
     * 批量查询信息导入
     * @param cycle
     * @throws Exception
     * @author weipeng.feng
     * @date 2018-1-12
     */
    public void  importQueryDataInfo(IRequestCycle cycle) throws Exception{
    	
    	    IData param = this.getData();
    	    param.put("SERIAL_NUMBER", "1");
            IDataset result = CSViewCall.call(this, "SS.IOTQuerySVC.batchQryUserServiceState", param);
            this.setInfos(result);
                        
 
    }
	
    public abstract void setInfos(IDataset infos);

}
