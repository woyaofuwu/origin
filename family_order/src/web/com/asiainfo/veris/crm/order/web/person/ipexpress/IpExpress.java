
package com.asiainfo.veris.crm.order.web.person.ipexpress;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.IpExpressException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class IpExpress extends PersonBasePage
{
    /**
     * 回到JS页面需要的数据集 号码校验
     * 
     * @data 2013-9-14
     * @param cycle
     * @throws Exception
     */
    public void checkPhone(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IDataset dataset = CSViewCall.call(this, "SS.IpExpressSVC.CheckPhone", data);
        setAjax(dataset);
    }

    /**
     * IP直通车选择服务时 页面初始化
     * 
     * @data 2013-9-13
     * @param cycle
     * @throws Exception
     */
    public void initService(IRequestCycle cycle) throws Exception
    {

        IData data = getData();
        IData indata = new DataMap();
        IData serviceTemp = new DataMap();
        String serviceStr = data.getString("IPService", "");
        String productId = data.getString("PRODUCT_ID");
        if (productId == null || productId.equals(""))
        {
            CSViewException.apperr(IpExpressException.CRM_IPEXPRESS_7);
        }

        indata.put("PRODUCT_ID", productId);

        IDataset infos = CSViewCall.call(this, "SS.IpExpressSVC.GetSvcByProductId", indata);
        if (infos == null || infos.isEmpty())
            CSViewException.apperr(IpExpressException.CRM_IPEXPRESS_8);

        serviceTemp.put("SERVICE_STR", serviceStr);
        setServiceCondition(serviceTemp);
        String[] strParam = serviceStr.split("@");
        IData params = new DataMap();
        for (int i = 0; i < strParam.length; i++)
        {
            String string = strParam[i];
            params.put(string, string); // IData型的会将前个string覆盖。name==id;
        }

        for (int i = 0; i < infos.size(); i++)
        {
            IData service = (IData) infos.get(i);
            String serviceId = service.getString("SERVICE_ID", "");
            String forceTag = service.getString("FORCE_TAG", "");
            if ("".equals(serviceId))
            {
                service.put("X_TAG", "false");
                continue;
            }
            if (params.containsKey(serviceId))
            {
                service.put("X_TAG", "true");
            }
            else
            {
                service.put("X_TAG", "false");
            }
            if ("1".equals(forceTag))
            {
                service.put("X_TAG", "true");
            }
        }
        setServiceInfos(infos);
    }

    public void loadChildInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IData userInfo = new DataMap(data.getString("USER_INFO", ""));

        IDataset returnInfos = CSViewCall.call(this, "SS.IpExpressSVC.GetIpExpressInfo", userInfo);
        IData returnInfo = returnInfos.getData(0);

        IDataset ipSnTypes = (IDataset) returnInfo.get("IP_SN_TYPE");
        for (int i = 0, size = ipSnTypes.size(); i < size; i++)
        {
            IData ipSnType = ipSnTypes.getData(i);
            String showName = ipSnType.getString("PARAM_NAME") + "|" + ipSnType.getString("PARAM_CODE");
            ipSnType.put("PARAM_NAME", showName);
        }

        setIpSnType(ipSnTypes);
        setIpProduct((IDataset) returnInfo.get("IP_PRODUCT"));
        setBindIpPhoneInfos((IDataset) returnInfo.get("BIND_IPPHONE"));

        IData commInfo = (IData) returnInfo.get("COMM_INFO");
        setCommInfo(commInfo);
    }

    /**
     * 页面初始化 赋值页面
     * 
     * @data 2013-9-12
     * @param cycle
     * @throws Exception
     */
    public void onInitTrade(IRequestCycle cycle) throws Exception
    {

        IData data = new DataMap();
        IData commInfo = new DataMap();

        data.put("EPARCHY_CODE", getVisit().getStaffEparchyCode());
        data.put("TAG_CODE", "CS_INF_DEFALTIPPASSWD");
        data.put("SUBSYS_CODE", "CSM");
        data.put("USE_TAG", "0");

        // 获取是否显示输入密码按钮
        IDataset dataset = CSViewCall.call(this, "CS.TagInfoQrySVC.getTagInfosByTagCode", data);
        String setPWD = dataset.getData(0).getString("TAG_CHAR", "");
        boolean isIPPasswd = setPWD.equals("1") ? false : true;
        commInfo.put("ISDEFALTIPPASSWD", isIPPasswd);
        commInfo.put("IP_OPEN_DATE", SysDateMgr.getSysDate());

        setCommInfo(commInfo);
    }

    /**
     * 业务提交
     * 
     * @param cycle
     * @throws Exception
     */
    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData data = getData();

        if ("".equals(data.getString("SERIAL_NUMBER", "")))
        {
            data.put("SERIAL_NUMBER", data.getString("AUTH_SERIAL_NUMBER"));
        }

        IDataset dataset = CSViewCall.call(this, "SS.IpExpressRegSVC.tradeReg", data);
        setAjax(dataset);
    }

    public abstract void setBindIpPhoneInfo(IData bindIpPhoneInfo);

    public abstract void setBindIpPhoneInfos(IDataset bindIpPhoneInfos);

    public abstract void setCommInfo(IData commInfo);

    public abstract void setIpProduct(IDataset ipProduct);

    public abstract void setIpSnType(IDataset ipSnType);

    public abstract void setServiceCondition(IData serviceCondition);

    public abstract void setServiceInfo(IData serviceInfo);

    public abstract void setServiceInfos(IDataset serviceInfos);
}
