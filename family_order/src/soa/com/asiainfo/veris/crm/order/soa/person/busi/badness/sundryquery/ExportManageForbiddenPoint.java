
package com.asiainfo.veris.crm.order.soa.person.busi.badness.sundryquery;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSExportTaskExecutor;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.badness.ForbiddenPointInfoQry;


public class ExportManageForbiddenPoint extends CSExportTaskExecutor
{
	
	public void chkDataByStaticValue(IData data, String colName, String typeId) throws Exception
    {
        String value = data.getString(colName).trim();
        if (StringUtils.isBlank(value))
        {
            return;
        }

        String dataName = StaticUtil.getStaticValue(typeId, value);
        data.put(colName, dataName);
    }
	
	public IDataset executeExport(IData data, Pagination page) throws Exception
    {
		IData param = data.subData("cond", true);
        data.putAll(param);
        IDataset result = CSAppCall.call("SS.ManageForbiddenPointSVC.queryForbiddenList", data);
        if (IDataUtil.isNotEmpty(result))
        {
            for (int i = 0, size = result.size(); i < size; i++)
            {
                IData temp = result.getData(i);
                chkDataByStaticValue(temp, "FORB_OPERATE_TYPE", "FORBIDDEN_STATES");
            }
        }
        return result;
    }
}
