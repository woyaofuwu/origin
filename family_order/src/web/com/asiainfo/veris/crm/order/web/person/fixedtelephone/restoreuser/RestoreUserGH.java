
package com.asiainfo.veris.crm.order.web.person.fixedtelephone.restoreuser;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class RestoreUserGH extends PersonBasePage
{
    /**
     * 功能说明：资源校验 修改时间
     * 
     * @param cycle
     * @throws Exception
     */
    public void checkNewResource(IRequestCycle cycle) throws Exception
    {
        IData pageData = getData();
        String resTypeCode = pageData.getString("RES_TYPE_CODE", "");
        IData userData = new DataMap(pageData.getString("USER_INFO"));
        String serialNumber = userData.getString("SERIAL_NUMBER", "");
        pageData.put("SERIAL_NUMBER", serialNumber);
        pageData.put(Route.ROUTE_EPARCHY_CODE, userData.getString("EPARCHY_CODE", ""));
        IData resData = new DataMap();
        resData.put("RESULT_CODE", "N");
        if (StringUtils.equals("1", resTypeCode) || StringUtils.equals("0", resTypeCode))
        {
            IDataset resDataset = CSViewCall.call(this, "SS.RestoreUserGHSVC.checkRestoreNewRes", pageData);
            if (IDataUtil.isNotEmpty(resDataset))
            {
                IData tempData = resDataset.getData(0);
                if (IDataUtil.isNotEmpty(tempData))
                {
                    resData.putAll(tempData);
                    resData.put("RESULT_CODE", "Y");
                    resData.put("START_DATE", SysDateMgr.getSysTime());
                    resData.put("END_DATE", SysDateMgr.END_DATE_FOREVER);
                }
            }
        }
        setAjax(resData);
    }

    /**
     * 认证结束之后回调的方法
     * 
     * @param cycle
     * @throws Exception
     */
    public void loadChildInfo(IRequestCycle cycle) throws Exception
    {
        IData pagedata = getData();
        pagedata.put(Route.ROUTE_EPARCHY_CODE, pagedata.getString("EPARCHY_CODE", ""));
        IDataset returnDataset = CSViewCall.call(this, "SS.RestoreUserGHSVC.getRestoreUserInfo", pagedata);
        IData returnData = returnDataset.getData(0);
        IData resData = returnData.getData("RES_INFO");
        setResInfos(resData.getDataset("RES_INFOS"));// 原始资源信息
        setOldSimCardNo(resData.getString("OLD_SIM_CARD_NO", ""));// 原sim卡
        setOldPhoneNo(resData.getString("OLD_PHONE_NO", ""));// 原号码
        setEditInfo(returnData.getData("EDIT_INFO"));
        // setAjax(returnData.getData("EDIT_INFO"));
        setProductTypeList(returnData.getDataset("PRODUCT_TYPE_LIST"));
        setTelephoneInfo(returnData.getData("TELEPHONE"));
    }

    /**
     * 菜单点击执行的事件
     * 
     * @param cycle
     * @throws Exception
     */
    public void onInitTrade(IRequestCycle cycle) throws Exception
    {
        IData editInfo = new DataMap();
        editInfo.put("IS_TD_USER", "0");// 默认不是3G用户
        editInfo.put("SIM_CHECK_TAG", "first");
        setEditInfo(editInfo);
    }

    /**
     * 业务提交,组件默认提交action方法
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
        data.put(Route.ROUTE_EPARCHY_CODE, data.getString("EPARCHY_CODE", ""));
        IDataset dataset = CSViewCall.call(this, "SS.RestoreUserGHRegSVC.tradeReg", data);
        setAjax(dataset);
    }

    /**
     * 功能: 资源信息列表每行鼠标点击
     * 
     * @return
     * @throws Exception
     */
    public IDataset resClickTag() throws Exception
    {
        DataMap inparams = new DataMap();
        inparams.put("USE_TAG", "0");
        inparams.put("SUBSYS_CODE", "CSM");
        inparams.put("TAG_CODE", "CS_VIEWCON_RESTORE");
        inparams.put("EPARCHY_CODE", getVisit().getStaffEparchyCode());
        // 查询复机界面控制参数
        IDataset dataset = CSViewCall.call(this, "CS.TagInfoQrySVC.getTagInfoBySubSys", inparams);
        if (null == dataset || dataset.size() < 1)
        {
            dataset = new DatasetList();
            IData data = new DataMap();
            data.put("TAG_NUMBER", "2");
            dataset.add(data);
        }
        return dataset;
    }

    /**
     * 功能说明：资源信息列表每行鼠标点击校验
     * 
     * @param cycle
     * @throws Exception
     */
    public void resTableRowClick(IRequestCycle cycle) throws Exception
    {
        IData pageData = getData();
        IDataset outlist = new DatasetList();
        outlist.add(pageData);
        IDataset dataset = this.resClickTag();
        if (dataset.size() > 0)
        {
            outlist.add(dataset.get(0));
        }
        setAjax(outlist);
    }

    public abstract void setEditInfo(IData commInfo);

    public abstract void setOldPhoneNo(String oldphoneno);

    public abstract void setOldSimCardNo(String oldsimno);

    public abstract void setProductTypeList(IDataset productTypeList);

    public abstract void setResInfos(IDataset relaList);

    public abstract void setTelephoneInfo(IData telephoneInfo);
}
