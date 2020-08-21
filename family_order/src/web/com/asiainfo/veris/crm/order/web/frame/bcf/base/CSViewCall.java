
package com.asiainfo.veris.crm.order.web.frame.bcf.base;

import org.apache.log4j.Logger;

import com.ailk.biz.BizVisit;
import com.ailk.biz.view.IBizCommon;
import com.ailk.common.Constants;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataInput;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.IVisit;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.client.ServiceFactory;
import com.ailk.service.client.http.HttpHelper;
import com.ailk.web.view.VisitManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.BizException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;

/**
 * 基础业务界面框架类，负责提供基本的响应方法，调度流程
 * 
 * @param cycle
 * @throws Exception
 */
public final class CSViewCall
{
    private final static Logger log = Logger.getLogger(CSViewCall.class);

    public final static IDataset call(IBizCommon bc, String svcName, IData data) throws Exception
    {
        IDataOutput output = svcFatCall(bc, svcName, data, null);

        IDataset result = output.getData();

        return result;
    }

    public final static IDataset call(IBizCommon bc, String svcName, IData data, Pagination page) throws Exception
    {
        IDataOutput output = svcFatCall(bc, svcName, data, null);

        IDataset result = output.getData();

        return result;
    }

    public final static IDataset call(IBizCommon bc, String url, String svcName, IData iData, Pagination page) throws Exception
    {
        IDataInput input = getDataInput(bc, iData, page);

        IDataOutput output = ServiceFactory.call(url, svcName, input, page, false, true);

        return output.getData();
    }

    public final static IDataOutput call(IBizCommon bc, String url, String svcName, IDataInput input, Pagination page) throws Exception
    {
        return ServiceFactory.call(url, svcName, input, page, false, true);
    }

    public final static IData callHttp(IBizCommon bc, String svcName, IData data) throws Exception
    {
        IDataInput input = getDataInput(bc, data, null);

        IDataOutput output = HttpHelper.callHttpSvc(svcName, input);

        IDataset result = output.getData();

        return result.getData(0);
    }

    public final static IData callone(IBizCommon bc, String svcName, IData data) throws Exception
    {
        IDataset result = call(bc, svcName, data);

        if (IDataUtil.isEmpty(result))
        {
            return null;
        }

        return result.getData(0);
    }

    public final static IData callone(IBizCommon bc, String url, String svcName, IData data) throws Exception
    {
        IDataInput input = getDataInput(bc, data, null);

        IDataOutput output = call(bc, url, svcName, input, null);

        IDataset result = output.getData();

        if (IDataUtil.isEmpty(result))
        {
            return null;
        }

        return result.getData(0);
    }

    public final static IDataOutput callPage(IBizCommon bc, String svcName, IData data, Pagination page) throws Exception
    {
        IDataOutput output = svcFatCall(bc, svcName, data, page);

        return output;
    }

    private final static IDataInput getDataInput(IBizCommon bc, IData data, Pagination page) throws Exception
    {
        IVisit visit = VisitManager.getVisit();

        BizVisit bv = (BizVisit) visit;
        String staffId = bv.getStaffId();

        String ngBossStaffId = data.getString("NGBOSS_STAFF_ID", "");

        if (StringUtils.isNotBlank(ngBossStaffId))
        {
            // 不相等
            if (!ngBossStaffId.equals(staffId))
            {
                StringBuilder sb = new StringBuilder("ngboss工号和visit工号不一致，请立即将此信息反馈给系统管理员。bv=[").append(bv.toString()).append("]").append("] data=[").append(data.toString()).append("]");

                log.error(sb.toString());

                CSViewException.apperr(BizException.CRM_BIZ_5, sb);
            }
        }

        String pageName = bc.getPageName();
        String menuId = bc.getMenuId();

        // visit.set(Constants.X_PAGENAME, pageName);
        // visit.set(Constants.X_MENU_ID, menuId);

        IDataInput input = DataHelper.createDataInput(visit, data, page, new String[]
        { Constants.X_PAGENAME, Constants.X_MENU_ID }, new String[]
        { pageName, menuId });

        return input;
    }

    private final static IDataOutput svcFatCall(IBizCommon bc, String svcName, IData data, Pagination page) throws Exception
    {
        IDataInput input = getDataInput(bc, data, page);

        IDataOutput output = ServiceFactory.call(svcName, input, page);

        return output;
    }
}
