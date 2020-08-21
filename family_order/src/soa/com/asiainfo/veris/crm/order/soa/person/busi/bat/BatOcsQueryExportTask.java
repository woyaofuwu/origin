
package com.asiainfo.veris.crm.order.soa.person.busi.bat;

import com.ailk.biz.BizVisit;
import com.ailk.biz.impexp.ExportTaskExecutor;
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UDepartInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UStaffInfoQry;

/**
 * Copyright: Copyright (c) 2013 Asiainfo-Linkage
 * 
 * @ClassName: BatQueryResultImportTask.java
 * @Description: 批量任務結果查詢導出後台TASK类
 * @version: v1.0.0
 * @author: xiangyc
 * @date: 2013-9-24 下午7:38:22 Modification History: Date Author Version Description
 *        ---------------------------------------------------------* 2013-9-24 xiangyc v1.0.0 修改原因
 */

public class BatOcsQueryExportTask extends ExportTaskExecutor
{
    @Override
    public IDataset executeExport(IData paramIData, Pagination paramPagination) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", paramIData.getString("SERIAL_NUMBER"));
        param.put("BATCH_ID", paramIData.getString("BATCH_ID"));
        param.put("START_DATE", paramIData.getString("START_DATE"));
        param.put("END_DATE", paramIData.getString("END_DATE"));
        BizVisit visit = CSBizBean.getVisit();
        visit.setCityCode(paramIData.getString("TRADE_CITY_CODE"));
        visit.setDepartCode(paramIData.getString("TRADE_DEPART_ID"));
        visit.setDepartId(paramIData.getString("TRADE_DEPART_ID"));
        visit.setStaffId(paramIData.getString("TRADE_STAFF_ID"));
        visit.setStaffEparchyCode(paramIData.getString("TRADE_EPARCHY_CODE"));
        visit.setSerialNumber(paramIData.getString("TRADE_SERIAL_NUMBER"));
        param.put("EPARCHY_CODE", paramIData.getString("TRADE_EPARCHY_CODE"));

        IDataset ret = CSAppCall.call("CS.BatDealSVC.queryOcsDealInfo", param);
        // BatDealInfoQry.queryOcsDealInfo(param, null);
        IDataset returnInfos = new DatasetList();
        if (IDataUtil.isNotEmpty(ret))
        {
            for (int i = 0; i < ret.size(); i++)
            {
                IData returnInfo = ret.getData(i);

                String staffId = returnInfo.getString("ACCEPT_STAFF_ID");
                String staffName = UStaffInfoQry.getStaffNameByStaffId(staffId);
                returnInfo.put("ACCEPT_STAFF_NAME", staffName);

                String departId = returnInfo.getString("ACCEPT_DEPART_ID");
                String departName = UDepartInfoQry.getDepartNameByDepartId(departId);
                returnInfo.put("ACCEPT_DEPART_NAME", departName);

                String dealState = returnInfo.getString("DEAL_STATE");
                String enableTag = returnInfo.getString("ENABLE_TAG");
                String writeType = returnInfo.getString("WRITE_TYPE");

                returnInfo.put("DEAL_STATE_NAME", StaticUtil.getStaticValue(getVisit(), "TD_S_STATIC", new java.lang.String[]
                { "TYPE_ID", "DATA_ID" }, "DATA_NAME", new java.lang.String[]
                { "OCSBATDEAL_DEALSTATE", dealState }));
                returnInfo.put("ENABLE_TAG_NAME", StaticUtil.getStaticValue(getVisit(), "TD_S_STATIC", new java.lang.String[]
                { "TYPE_ID", "DATA_ID" }, "DATA_NAME", new java.lang.String[]
                { "OCSBATDEAL_ENABLETAG", enableTag }));
                returnInfo.put("WRITE_TYPE_NAME", StaticUtil.getStaticValue(getVisit(), "TD_S_STATIC", new java.lang.String[]
                { "TYPE_ID", "DATA_ID" }, "DATA_NAME", new java.lang.String[]
                { "OCSBATDEAL_WRITETYPE", writeType }));
                returnInfos.add(returnInfo);
            }
        }
        return returnInfos;
    }

}
