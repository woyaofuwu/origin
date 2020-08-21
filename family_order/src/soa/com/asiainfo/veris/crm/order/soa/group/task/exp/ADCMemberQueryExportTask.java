
package com.asiainfo.veris.crm.order.soa.group.task.exp;

import com.ailk.biz.impexp.ExportTaskExecutor;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;

public class ADCMemberQueryExportTask extends ExportTaskExecutor
{

    @Override
    public IDataset executeExport(IData inParam, Pagination pg) throws Exception
    {

        String sn = inParam.getString("cond_SERIAL_NUMBER");
        IData param = new DataMap();
        param.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
        param.put("SERIAL_NUMBER", sn);
        param.put("REMOVE_TAG", "0");

        IDataset userInfo = CSAppCall.call("CS.UserInfoQrySVC.getUserInfoBySnNoProduct", param);
        if (IDataUtil.isEmpty(userInfo))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_573, sn);
        }

        param = new DataMap();
        param.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
        param.put("USER_ID", userInfo.getData(0).getString("USER_ID"));

        IDataset dataset = CSAppCall.call("SS.GroupInfoQuerySVC.qryADCMembersbyuser", param);
        return dataset;

    }
}
