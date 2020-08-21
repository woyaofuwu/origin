
package com.asiainfo.veris.crm.order.soa.person.busi.dandelionplans;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class FreezeUserManageSVC extends CSBizService
{

    /**
     * @Fields serialVersionUID :
     */
    private static final long serialVersionUID = -1756769134328846009L;

    public IDataset createPhone(IData param) throws Exception
    {
        IDataUtil.chkParam(param, "SERIAL_NUMBER");
        IDataUtil.chkParam(param, "NUMBER_TYPE");
        IDataUtil.chkParam(param, "START_DATE");
        IDataUtil.chkParam(param, "END_DATE");
        FreezeUserManageBean bean = (FreezeUserManageBean) BeanManager.createBean(FreezeUserManageBean.class);
        int numStatus = bean.createPhone(param);
        IData data = new DataMap();
        data.put("STATUS", numStatus);
        IDataset results = new DatasetList();
        results.add(data);
        return results;
    }

    public IDataset queryUserFreeze(IData data) throws Exception
    {
        IDataUtil.chkParam(data, "SERIAL_NUMBER"); // 查询号码
        IDataUtil.chkParam(data, "NUMBER_TYPE"); // 号码类型
        data.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
        FreezeUserManageBean bean = (FreezeUserManageBean) BeanManager.createBean(FreezeUserManageBean.class);
        IDataset results = bean.queryUserFreeze(data, getPagination());
        return results;
    }

}
