
package com.asiainfo.veris.crm.order.web.person.multisnonecard;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class RemedyCard extends PersonBasePage
{
    /**
     * 页面初始化，加载页面需要的信息
     *
     * @param cycle
     * @throws Exception
     */
    public void checkNumber(IRequestCycle cycle) throws Exception
    {
        IData pagedata = getData();
        String serialNumber = pagedata.getString("NEW_SERIAL_NUMBER");
        IData params = new DataMap();
        params.put("SERIAL_NUMBER", serialNumber);

        IDataset dataset = CSViewCall.call(this, "SS.RemedyCardSVC.getOtherSNInfo", params);

        setOuser_info(dataset.getData(0).getData("OUSERINFO"));
        setOcust_info(dataset.getData(0).getData("OUSERINFO"));
    }

    /**
     *获取页面初始化时页面需要加载的数据
     *
     * @param userInfo
     * @return
     * @throws Exception
     */
    public IData getCommInfo(IData userInfo, String serialNumber) throws Exception
    {
        IData params = new DataMap();
        IData result = new DataMap();
        params.put("USER_ID", userInfo.getString("USER_ID"));
        params.put("RSRV_VALUE_CODE", "SIMM");
        params.put("RSRV_STR4", "01");
        IDataset otherInfos = CSViewCall.call(this, "SS.RemedyCardSVC.getCheck", params);
        result.put("DEPUTY_INFOS", otherInfos);

        // 读取操作类型
        params.put("SUBSYS_CODE", "CSM");
        params.put("PARAM_ATTR", "952");
        params.put("PARAM_CODE", "A3");
        IDataset operType = CSViewCall.call(this, "SS.RemedyCardSVC.getOperType", params);
        result.put("OPERTYPE_INFOS", operType);

        // 读取IMIS卡号
        IDataset dataset = CSViewCall.call(this, "SS.RemedyCardSVC.getImsi", params);
        if (IDataUtil.isNotEmpty(dataset)){
	        result.put("IMSI_CODE_OLD", dataset.getData(0).getString("RSRV_STR6", ""));
	        result.put("IMSI_CODE", dataset.getData(0).getString("RSRV_STR7", ""));
        }
        else{
        	CSViewException.apperr(CrmUserException.CRM_USER_1207);
        }

        setCommInfo(result);
        setInfo(result);

        return result;
    }

    /**
     * 页面初始化，加载页面需要的信息
     *
     * @param cycle
     * @throws Exception
     */
    public void loadChildInfo(IRequestCycle cycle) throws Exception
    {

        IData pagedata = getData();
        String serialNumber = pagedata.getString("AUTH_SERIAL_NUMBER");
        IData userInfo = new DataMap(pagedata.getString("USER_INFO"));
        IData commInfo = getCommInfo(userInfo, serialNumber);
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
        String serialNumber = data.getString("AUTH_SERIAL_NUMBER");
        String serialNumberO = data.getString("SERIAL_NUMBER_O", "");
        String operType = data.getString("OPERATION_TYPE", "");

        IData params = new DataMap();
        params.put("SERIAL_NUMBER", serialNumber);
        params.put("OPERATION_TYPE", operType);
        if ("02".equals(operType))
        {
            params.put("SERIAL_NUMBER_O", serialNumberO);
        }
        IDataset dataset = CSViewCall.call(this, "SS.RemedyRegSVC.tradeReg", params);
        setAjax(dataset);
    }

    public abstract void setCommInfo(IData editInfo);

    public abstract void setEditInfo(IData editInfo);

    public abstract void setInfo(IData list);

    public abstract void setOcust_info(IData list);

    public abstract void setOuser_info(IData list);

}
