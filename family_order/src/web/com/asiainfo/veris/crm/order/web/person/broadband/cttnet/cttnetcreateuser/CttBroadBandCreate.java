
package com.asiainfo.veris.crm.order.web.person.broadband.cttnet.cttnetcreateuser;

import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: CttBroadBandCreate.java
 * @Description: 铁通宽带开户
 * @version: v1.0.0
 * @author: yxd
 * @date: 2014-3-3 上午11:05:43 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-3-3 yxd v1.0.0 修改原因
 */
public abstract class CttBroadBandCreate extends PersonBasePage
{
    public void checkAcctId(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        String routeId = getVisit().getStaffEparchyCode();
        if (StringUtils.isBlank(routeId))
        {
            routeId = "0898";
        }
        data.put(Route.ROUTE_EPARCHY_CODE, routeId);
        IDataset dataset = CSViewCall.call(this, "SS.CttBroadbandSVC.checkAcctId", data);
        this.setAccountInfo(dataset.getData(0));
    }

    public void checkPsptLimit(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        String routeId = getVisit().getStaffEparchyCode();
        data.put(Route.ROUTE_EPARCHY_CODE, routeId);
        IDataset dataset = CSViewCall.call(this, "SS.CttBroadbandSVC.checkPsptLimit", data);
        IData result = dataset.getData(0);
        IDataset custInfos = result.getDataset("CUST_INFO"); // 客户信息
        String message = result.getString("MESSAGE"); // 错误提示信息
        String hasOwefeeUser = result.getString("HAS_OWEFEE_USER"); // 欠费标志，1为有欠费用户
        IData custInfo = new DataMap();
        if (IDataUtil.isNotEmpty(custInfos))
        {
            custInfo = custInfos.getData(0);
        }
        else
        {
            custInfo.putAll(data);
        }
        IData errorMesg = new DataMap();
        errorMesg.put("MESSAGE", message);
        errorMesg.put("HAS_OWEFEE_USER", hasOwefeeUser);
        this.setAjax(errorMesg);
        this.setInfo(custInfo);
    }

    /**
     * 根据配置的账号规则生成默认账号 PRODUCT_SPEC,PRODUCT_ID
     * 
     * @return
     * @throws Exception
     */
    public void getAcctList(IRequestCycle cycle) throws Exception
    {
        IData param = getData();

        IDataset accountInfos = CSViewCall.call(this, "SS.CttBroadbandSVC.getAcctList", param);
        IData data = accountInfos.size() > 0 ? (IData) accountInfos.get(0) : null;
        this.setAccountInfo(data);
    }

    /**
     * getBankBySuperBank 获得银行名称
     * 
     * @param cycle
     * @throws Exception
     */
    public void getBankBySuperBank(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        String routeId = getVisit().getStaffEparchyCode();
        if (StringUtils.isBlank(routeId))
        {
            routeId = "0898";
        }
        data.put(Route.ROUTE_EPARCHY_CODE, routeId);
        IDataset dataset = CSViewCall.call(this, "SS.CttBroadbandSVC.getBankBySuperBank", data);
        this.setBankCodeList(dataset);
    }

    /**
     * @Function: getProductBySpec()
     * @Description: 根据规格获取产品
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: yxd
     * @date: 2014-3-19 上午9:42:13 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-3-19 yxd v1.0.0 修改原因
     */
    public void getProductBySpec(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IDataset productList = CSViewCall.call(this, "SS.CttBroadbandSVC.getProductBySpec", data);
        String eparchyCode = getVisit().getStaffEparchyCode();
        IData param = new DataMap();
        param.put("USER_EPARCHY_CODE", eparchyCode);
        this.setInfo(param);
        this.setProductList(productList);
    }

    public void initData(IRequestCycle cycle) throws Exception
    {
        IData info = getData();
        // info.put("STANDE_ADDRESS", "长沙市银华大厦");
        // info.put("STANDE_ADDRESS_CODE", "07310001");
        // info.put("RATE", "2");
        // info.put("MODEM_NUMERIC", "1");
        // info.put("SALEPRICE", "200");
        // info.put("MODEM_CODE", "MC01");
        // info.put("MOFFICE_ID", "8901");
        String execAction = SysDateMgr.getSysTime();
        String departKindCode = StaticUtil.getStaticValue(getVisit(), "TD_M_DEPART", "DEPART_ID", "DEPART_KIND_CODE", this.getVisit().getDepartId());
        info.put("DEPART_KIND_CODE", departKindCode);
        info.put("EXEC_ACTION", execAction);
        setInfo(info);
    }

    /**
     * @Function: onTradeSubmit()
     * @Description: 业务受理
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: yxd
     * @date: 2014-3-13 下午4:33:30 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-3-13 yxd v1.0.0 修改原因
     */
    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        String routeId = getVisit().getStaffEparchyCode();
        if (StringUtils.isBlank(routeId))
        {
            routeId = "0898";
        }
        data.put(Route.ROUTE_EPARCHY_CODE, routeId);
        IDataset dataset = CSViewCall.call(this, "SS.CttBroadBandCreateRegSVC.tradeReg", data);
        setAjax(dataset);
    }

    /**
     * 根据固话号码查询未完工或已完工的固话开户信息
     */
    public void queryCttPhone(IRequestCycle cycle) throws Exception
    {
        IData data = getData();

        String serialNumber = data.getString("CTT_PHONE");

        data.put("SERIAL_NUMBER", serialNumber);
        String routeId = getVisit().getStaffEparchyCode();
        if (StringUtils.isBlank(routeId))
        {
            routeId = "0898";
        }
        data.put(Route.ROUTE_EPARCHY_CODE, routeId);
        IDataset tradetelinfos = CSViewCall.call(this, "SS.CttBroadbandSVC.checkCttPhone", data);
        IData telinfo = IDataUtil.isNotEmpty(tradetelinfos) ? (IData) tradetelinfos.get(0) : null;
        if (telinfo != null)
        {
            telinfo.put("cond_CTT_PHONE", telinfo.getString("SERIAL_NUMBER"));
            setInfos(tradetelinfos);
        }
    }

    public abstract void setAccountInfo(IData data);

    public abstract void setBankCodeList(IDataset bankCodeList);

    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset info);

    public abstract void setProductList(IDataset productList);
}
