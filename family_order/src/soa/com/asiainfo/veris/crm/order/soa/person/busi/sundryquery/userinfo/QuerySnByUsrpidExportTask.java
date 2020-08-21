
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.userinfo;

import com.ailk.biz.impexp.ExportTaskExecutor;
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UDepartInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UStaffInfoQry;

public class QuerySnByUsrpidExportTask extends ExportTaskExecutor
{

    @Override
    public IDataset executeExport(IData paramIData, Pagination arg1) throws Exception
    {
        IData inparam = paramIData.subData("cond", true);
        inparam.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());
        IDataset ibplatDs = CSAppCall.call("SS.QuerySnByUsrpidSVC.querySnByUsrpid", inparam);
        for (int i = 0, size = ibplatDs.size(); i < size; i++)
        {
            String sex = ibplatDs.getData(i).getString("SEX", "");
            String LOCAL_NATIVE_CODE = ibplatDs.getData(i).getString("LOCAL_NATIVE_CODE", "");
            String PSPT_TYPE_CODE = ibplatDs.getData(i).getString("PSPT_TYPE_CODE", "");
            String REMOVE_TAG = ibplatDs.getData(i).getString("REMOVE_TAG", "");
            String EPARCHY_CODE = ibplatDs.getData(i).getString("EPARCHY_CODE", "");
            String CITY_CODE = ibplatDs.getData(i).getString("CITY_CODE", "");
            String IN_STAFF_ID = ibplatDs.getData(i).getString("IN_STAFF_ID", "");
            String IN_DEPART_ID = ibplatDs.getData(i).getString("IN_DEPART_ID", "");
            String OPEN_STAFF_ID = ibplatDs.getData(i).getString("OPEN_STAFF_ID", "");
            String OPEN_DEPART_ID = ibplatDs.getData(i).getString("OPEN_DEPART_ID", "");

            if (!"".equals(sex))
            {
                ibplatDs.getData(i).put("SEX", StaticUtil.getStaticValue("SEX", sex));
            }
            if (!"".equals(LOCAL_NATIVE_CODE))
            {
                ibplatDs.getData(i).put("LOCAL_NATIVE_CODE", StaticUtil.getStaticValue("TD_S_LOCAL_NATIVE", LOCAL_NATIVE_CODE));
            }
            if (!"".equals(PSPT_TYPE_CODE))
            {
                ibplatDs.getData(i).put("PSPT_TYPE_CODE", StaticUtil.getStaticValue("TD_S_PASSPORTTYPE", PSPT_TYPE_CODE));
            }
            if (!"".equals(REMOVE_TAG))
            {
                ibplatDs.getData(i).put("REMOVE_TAG", StaticUtil.getStaticValue("REMOVE_TAG", REMOVE_TAG));
            }
            if (!"".equals(EPARCHY_CODE))
            {
                ibplatDs.getData(i).put("EPARCHY_CODE", StaticUtil.getStaticValue(getVisit(), "TD_M_AREA", "AREA_CODE", "AREA_NAME", EPARCHY_CODE));
            }
            if (!"".equals(CITY_CODE))
            {
                ibplatDs.getData(i).put("CITY_CODE", StaticUtil.getStaticValue(getVisit(), "TD_M_AREA", "AREA_CODE", "AREA_NAME", CITY_CODE));
            }
            if (!"".equals(IN_STAFF_ID))
            {
                ibplatDs.getData(i).put("IN_STAFF_ID", UDepartInfoQry.getDepartNameByDepartId(IN_STAFF_ID));
            }
            if (!"".equals(IN_DEPART_ID))
            {
                ibplatDs.getData(i).put("IN_DEPART_ID", UStaffInfoQry.getStaffNameByStaffId(IN_DEPART_ID));
            }
            if (!"".equals(OPEN_STAFF_ID))
            {
                ibplatDs.getData(i).put("OPEN_STAFF_ID", UStaffInfoQry.getStaffNameByStaffId(OPEN_STAFF_ID));
            }
            if (!"".equals(OPEN_DEPART_ID))
            {
                ibplatDs.getData(i).put("OPEN_DEPART_ID", UDepartInfoQry.getDepartNameByDepartId(IN_DEPART_ID));
            }

        }

        return ibplatDs;

    }

}
