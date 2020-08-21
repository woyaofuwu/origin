
package com.asiainfo.veris.crm.order.soa.person.busi.plat;

import com.ailk.biz.impexp.ExportTaskExecutor;
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.tm.SpInfoCSQry;

public class SpInfoCSInfoExportTask extends ExportTaskExecutor
{
	public void chkDataByStaticValue(IData data, String colName, String typeId) throws Exception
    {
        String value = data.getString(colName);
        if (StringUtils.isBlank(value))
        {
            return;
        }

        String dataName = StaticUtil.getStaticValue(typeId, value);
        data.put(colName, dataName);
    }
    @Override
    public IDataset executeExport(IData paramIData, Pagination paramPagination) throws Exception
    {
    	String provinceCode = "898";
        String bizCode = paramIData.getString("cond_BIZ_CODE");
        String bizName = paramIData.getString("cond_BIZ_NAME");
        String spCode = paramIData.getString("cond_SP_CODE");
        String spName = paramIData.getString("cond_SP_NAME");
        String spType = paramIData.getString("cond_SP_TYPE");
        String spAttr = paramIData.getString("cond_SP_ATTR");
        String recordStatus = paramIData.getString("cond_RECORD_STATUS");
        IDataset result = SpInfoCSQry.querySpInfoCS(provinceCode, bizCode, bizName, spCode, spName, spType, spAttr, recordStatus, paramPagination);
        if (IDataUtil.isNotEmpty(result))
        {
            for (int i = 0, size = result.size(); i < size; i++)
            {
                IData temp = result.getData(i);
                chkDataByStaticValue(temp, "SP_TYPE", "SPINFO_CS_SP_TYPE");
                chkDataByStaticValue(temp, "SP_ATTR", "SPINFO_CS_SP_ATTR");
                chkDataByStaticValue(temp, "SP_STATUS", "SPINFO_CS_SP_STATUS");
                chkDataByStaticValue(temp, "PROVINCE_NO", "SYMTHESIS_PROVINCE_CODE");
                chkDataByStaticValue(temp, "RECORD_STATUS", "SPINFO_CS_RECORD_STATUS");

                chkDataByStaticValue(temp, "OPE_CODE", "SP_OPE_CODE");
                chkDataByStaticValue(temp, "TYPE", "SP_TYPE");
                chkDataByStaticValue(temp, "TRADE_TYPE", "SP_TRADE_TYPE");
                chkDataByStaticValue(temp, "CLIENT_GRADE", "SP_CLIENT_GRADE");

                chkDataByStaticValue(temp, "CLIENT_ATTR", "SP_CLIENT_ATTR");
                chkDataByStaticValue(temp, "BUSI_SCOPE", "SP_BUSI_SCOPE");
                chkDataByStaticValue(temp, "BIZ_STATUS", "SP_SPBIZ_STATUS");
                chkDataByStaticValue(temp, "SEND_TYPE", "SP_SEND_TYPE");
            }
        }
        return result;

    }

}
