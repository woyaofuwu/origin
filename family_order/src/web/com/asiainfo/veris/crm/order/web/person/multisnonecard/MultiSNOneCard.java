
package com.asiainfo.veris.crm.order.web.person.multisnonecard;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class MultiSNOneCard extends PersonBasePage
{
    /**
     *获取页面初始化时页面需要加载的数据
     * 
     * @param userInfo
     * @return
     * @throws Exception
     * @author
     */
    public IData getCommInfo(IData userInfo, String serialNumber) throws Exception
    {
        IData params = new DataMap();
        IData result = new DataMap();
        params.put("SERIAL_NUMBER", serialNumber);
        params.put("USER_ID", userInfo.getString("USER_ID"));
        // 读取合作方地区
        params.put("SUBSYS_CODE", "CSM");
        params.put("PARAM_ATTR", "948");
        params.put("PARAM_CODE", "A3");
        params.put("EPARCHY_CODE", getVisit().getStaffEparchyCode());
        IDataset area_infos = CSViewCall.call(this, "SS.MultiSNOneCardSVC.getCommonParam1", params);
        result.put("AREA_INFOS", area_infos);
        // 读取合作网络
        params.put("SUBSYS_CODE", "CSM");
        params.put("PARAM_ATTR", "947");
        params.put("PARAM_CODE", "82");
        IDataset net_infos = CSViewCall.call(this, "SS.MultiSNOneCardSVC.getCommonParam1", params);
        result.put("NET_INFOS", net_infos);
        // 读取存在的信息（显示于页面本地业务tab下的数据）
        params.put("USER_ID", userInfo.getString("USER_ID"));
        params.put("RSRV_VALUE_CODE", "SIMM");
        IDataset dataset = CSViewCall.call(this, "SS.MultiSNOneCardSVC.getUserOther", params);
        result.put("DEPUTY_COUNT", "0");
        if (!dataset.isEmpty())
        {
            result.put("DEPUTY_COUNT", "1");
            result.put("CUR_STATE", dataset.getData(0).getString("RSRV_STR4"));
            result.put("SERVICE_TYPE", dataset.getData(0).getString("RSRV_STR8"));
            result.put("COOPER_AREA", dataset.getData(0).getString("RSRV_STR2"));
            result.put("COOPER_NET", dataset.getData(0).getString("RSRV_STR3"));
            result.put("VAILD_DATE", dataset.getData(0).getString("START_DATE"));
            result.put("INVAILD_DATE", dataset.getData(0).getString("END_DATE"));
            result.put("MAX_FEE", dataset.getData(0).getString("RSRV_STR5"));
            result.put("SUM_FEE", dataset.getData(0).getString("RSRV_STR6"));
            String card = dataset.getData(0).getString("RSRV_STR1", "");
            if ("".equals(card))
            {
                dataset.getData(0).put("RSRV_STR1", "系统控制");
            }
        }
        result.put("DEPUTY_INFOS", dataset);
        // 读取服务类型
        params.put("SUBSYS_CODE", "CSM");
        params.put("PARAM_ATTR", "949");
        params.put("PARAM_CODE", "A3");
        IDataset svctype_infos = CSViewCall.call(this, "SS.MultiSNOneCardSVC.getCommonParam", params);
        result.put("SVCTYPE_INFOS", svctype_infos);
        // 读取操作类型
        params.put("SUBSYS_CODE", "CSM");
        params.put("PARAM_ATTR", "951");
        params.put("PARAM_CODE", "A3");

        IDataset opertype_infos = CSViewCall.call(this, "SS.MultiSNOneCardSVC.getCommonParam", params);
        result.put("OPERTYPE_INFOS", opertype_infos);
        // 读取产品预付费标识
        params.put("PRODUCT_ID", userInfo.getString("PRODUCT_ID"));
        IDataset productDataset = CSViewCall.call(this, "SS.MultiSNOneCardSVC.getProductInfo", params);
        result.put("PREPAY_TAG", productDataset.getData(0).getString("PREPAY_TAG"));
        // 一卡多号平台查询 （显示于页面平台业务tab下的数据）
        params.put("SERIAL_NUMBER", userInfo.getString("SERIAL_NUMBER"));
        IDataset plat_deputy_infos = CSViewCall.call(this, "SS.MultiSNOneCardSVC.getMultiPlatInfo", params);
        result.put("PLAT_DEPUTY_INFOS", plat_deputy_infos);

        setCommInfo(result);
        setInfo(result);
        return result;
    }

    /**
     * 页面初始化，加载页面需要的信息
     * 
     * @param cycle
     * @throws Exception
     * @author
     */
    public void loadChildInfo(IRequestCycle cycle) throws Exception
    {
        IData pagedata = getData();
        String serialNumber = pagedata.getString("AUTH_SERIAL_NUMBER");
        IData data = new DataMap();

        IData userInfo = new DataMap(pagedata.getString("USER_INFO"));

        IDataset outinfos = CSViewCall.call(this, "SS.MultiSNOneCardSVC.getCheck", userInfo);

        IData commInfo = getCommInfo(userInfo, serialNumber);
        commInfo.put("USER_EPARCHY_CODE", userInfo.getString("EPARCHY_CODE"));
        setCommInfo(commInfo);// 公共信息保存到界面

        data.put("COOPER_AREA", "00852"); // 设置初始值
        setInfo(data);
    }

    /**
     * 业务提交
     * 
     * @param cycle
     * @throws Exception
     * @author
     */
    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        if ("".equals(data.getString("SERIAL_NUMBER", "")))
        {
            data.put("SERIAL_NUMBER", data.getString("AUTH_SERIAL_NUMBER"));
        }
        IDataset dataset = CSViewCall.call(this, "SS.MultiSNOneCardRegSVC.tradeReg", data);
        setAjax(dataset);
    }

    public abstract void setCommInfo(IData editInfo);

    public abstract void setEditInfo(IData editInfo);

    public abstract void setInfo(IData list);
}
