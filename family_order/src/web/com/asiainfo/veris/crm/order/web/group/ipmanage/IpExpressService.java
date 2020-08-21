
package com.asiainfo.veris.crm.order.web.group.ipmanage;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;

public abstract class IpExpressService extends GroupBasePage
{
    public abstract void setCondition(IData condition);

    public abstract void setInfos(IDataset infos);

    public abstract void setInfo(IData info);

    /**
     * 页面初始化
     * 
     * @throws Exception
     */
    public void initm(IRequestCycle cycle) throws Exception
    {
        String productId = getParameter("PRODUCT_ID");
        String serial_number = getParameter("SERIAL_NUMBER");
        IData inparam = new DataMap();
        inparam.put("SERIAL_NUMBER", serial_number);
        IData result = UCAInfoIntfViewUtil.qryGrpUCAInfoByGrpSn(this, serial_number);
        if (null == productId || "".equals(productId))
        {
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "请先选择IP直通车产品！！");
        }
        IData userInfo = result.getData("GRP_USER_INFO");
        IData indata = new DataMap();
        IData serviceTemp = new DataMap();
        indata.put("USER_ID", userInfo.getString("USER_ID"));

        IDataset userSvcInfos = CSViewCall.call(this, "CS.GrpUserPkgInfoQrySVC.getGrpCustomizeServByUserId", indata);

        if (IDataUtil.isEmpty(userSvcInfos))
        {
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "获取产品服务无数据！");
        }

        String serviceStr = getParameter("IPService", "");
        serviceTemp.put("SERVICE_STR", serviceStr);
        setCondition(serviceTemp);
        String[] strParam = serviceStr.split("@");
        IData params = new DataMap();
        for (int i = 0; i < strParam.length; i++)
        {
            String string = strParam[i];
            params.put(string, string); // IData型的会将前个string覆盖。name==id;
        }
        IDataset svc = new DatasetList();
        for (int i = 0; i < userSvcInfos.size(); i++)
        {
            IData service = userSvcInfos.getData(i);
            String forceTag = service.getString("FORCE_TAG", "");
            if ("1".equals(forceTag))
            {
                service.put("X_TAG", "true");
            }
            else
            {
                service.put("X_TAG", "false");
            }
            svc.add(service);
        }
        setInfos(svc);

    }

}
