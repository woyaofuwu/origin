package com.asiainfo.veris.crm.order.soa.person.busi.highvalueuser;

import com.ailk.biz.impexp.ExportTaskExecutor;
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UDepartInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UStaffInfoQry;

public class HighValueUserExportTask extends ExportTaskExecutor
{

    @Override
    public IDataset executeExport(IData paramIData, Pagination arg1) throws Exception
    {
        IData inparam = paramIData.subData("cond", true);
        System.out.print("wuhao"+inparam);
        inparam.put("START_DATE", inparam.getString("START_DATE") + SysDateMgr.START_DATE_FOREVER);
        inparam.put("FINISH_DATE", inparam.getString("FINISH_DATE") + SysDateMgr.END_DATE);  
        IDataset highUserDs = CSAppCall.call("SS.HighValueUserEntrySVC.queryHighValueUser", inparam);
        for (int i = 0, size = highUserDs.size(); i < size; i++)
        {
            String TRADE_DEPART_ID = highUserDs.getData(i).getString("TRADE_DEPART_ID", "");
            String TRADE_CITY_CODE = highUserDs.getData(i).getString("TRADE_CITY_CODE", "");
            if (!"".equals(TRADE_DEPART_ID))
            {
            	highUserDs.getData(i).put("TRADE_DEPART_ID", UDepartInfoQry.getDepartNameByDepartId(TRADE_DEPART_ID));
            }
            if (!"".equals(TRADE_CITY_CODE))
            {
            	highUserDs.getData(i).put("TRADE_CITY_CODE", StaticUtil.getStaticValue(getVisit(), "TD_M_AREA", "AREA_CODE", "AREA_NAME", TRADE_CITY_CODE));
            }
        }
        return highUserDs;
    }
}
