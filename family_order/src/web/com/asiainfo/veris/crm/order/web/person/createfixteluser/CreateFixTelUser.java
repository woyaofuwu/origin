
package com.asiainfo.veris.crm.order.web.person.createfixteluser;

import java.util.HashMap;
import java.util.Map;

import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.search.SearchResponse;
import com.ailk.search.client.SearchClient;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.ElementPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.ProductPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.person.simcardmgr.SimCardBasePage;

public abstract class CreateFixTelUser extends SimCardBasePage
{
    /**
     * 证件号码校验
     * 
     * @author chenzm
     * @param clcle
     * @throws Exception
     */
    public void checkPsptId(IRequestCycle clcle) throws Exception
    {
        IData data = getData();
        data.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
        IDataset dataset = CSViewCall.call(this, "SS.CreateFixTelUserSVC.checkPsptId", data);
        setAjax(dataset);
    }

    /**
     * 输入新开户号码后的校验，获取开户信息
     * 
     * @author chenzm
     * @param clcle
     * @throws Exception
     */
    public void checkSerialNumber(IRequestCycle clcle) throws Exception
    {
        IData data = getData();
        IDataset dataset = CSViewCall.call(this, "SS.CreateFixTelUserSVC.checkSerialNumber", data);
        setEditInfo(dataset.first());
        IDataset productTypeList = dataset.first().getDataset("PRODUCT_TYPE_LIST");
        IDataset acctInfoDays = dataset.first().getDataset("ACCT_INFO_DAYS");
        IDataset noteTypeList = dataset.first().getDataset("NOTE_TYPE_LIST");
        IDataset cityCodeList = dataset.first().getDataset("CITY_CODE_LIST");
        setProductTypeList(productTypeList);
        setAcctInfoDays(acctInfoDays);
        setNoteTypeList(noteTypeList);
        setCityCodeList(cityCodeList);
        setAjax(dataset);
    }

    /**
     * 输入SIM卡后的校验，获取卡信息
     * 
     * @author chenzm
     * @param clcle
     * @throws Exception
     */
    public void checkSimCardNo(IRequestCycle clcle) throws Exception
    {
        IData data = getData();
        IDataset dataset = CSViewCall.call(this, "SS.CreateFixTelUserSVC.checkSimCardNo", data);
        setEditInfo(dataset.first());
        setAjax(dataset);
    }

    /**
     * 简单密码校验
     * 
     * @author chenzm
     * @param clcle
     * @throws Exception
     */
    public void checkSimplePassword(IRequestCycle clcle) throws Exception
    {
        IData data = getData();
        IDataset dataset = CSViewCall.call(this, "SS.CreateFixTelUserSVC.checkSimplePassword", data);

        setAjax(dataset);
    }

    /**
     * 根据上级银行获取下级银行列表
     * 
     * @author chenzm
     * @param clcle
     * @throws Exception
     */
    public void getBankBySuperBank(IRequestCycle clcle) throws Exception
    {
        IData data = getData();
        IDataset dataset = new DatasetList();
        // 上级银行编码不为空，才调用服务刷新下级银行
        if (!StringUtils.isBlank(data.getString("SUPER_BANK_CODE")))
        {
            dataset = CSViewCall.call(this, "CS.BankInfoQrySVC.getBankBySuperBank3", data);
        }
        setBankCodeList(dataset);
    }

    /**
     * 获取产品费用
     * 
     * @author chenzm
     * @param clcle
     * @throws Exception
     */
    public void getProductFeeInfo(IRequestCycle clcle) throws Exception
    {
        IData data = getData();
        data.put(Route.ROUTE_EPARCHY_CODE, data.getString("EPARCHY_CODE"));
        IDataset dataset = CSViewCall.call(this, "SS.CreateFixTelUserSVC.getProductFeeInfo", data);
        setAjax(dataset);
    }

    /**
     * 根据type_id, eparchy_code获取静态参数，由于框架不支持，所以自己写 的服务
     * 
     * @author chenzm
     * @param typeId
     * @throws Exception
     */
    public IDataset getStaticListByTypeIdEparchy(String typeId) throws Exception
    {
        IData data = new DataMap();

        data.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());

        data.put("TYPE_ID", typeId);

        IDataset dataset = CSViewCall.call(this, "CS.StaticInfoQrySVC.getStaticListByTypeIdEparchy", data);

        return dataset;
    }

    /**
     * 页面加载时初始化产品类型列表
     * 
     * @author dengyong3
     * @param clcle
     * @throws Exception
     */
    public void initProductType(IData data) throws Exception
    {
        IDataset productTypeList = CSViewCall.call(this, "SS.CreateFixTelUserSVC.initProductType", data);
        setProductTypeList(productTypeList);
    }

    /**
     * 开户数限制校验
     * 
     * @author chenzm
     * @param clcle
     * @throws Exception
     */
    public void judgeOpenLimit(IRequestCycle clcle) throws Exception
    {
        IData data = getData();
        data.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
        IDataset dataset = CSViewCall.call(this, "SS.CreateFixTelUserSVC.judgeOpenLimit", data);
        setAjax(dataset);
    }

    /**
     * 费用重算
     * 
     * @author chenzm
     * @param clcle
     * @throws Exception
     */
    public void mputeFee(IRequestCycle clcle) throws Exception
    {
        IData data = getData();
        IDataset dataset = CSViewCall.call(this, "SS.CreateFixTelUserSVC.mputeFee", data);
        setAjax(dataset);
    }

    /**
     * 初始化方法
     * 
     * @author chenzm
     * @param clcle
     * @throws Exception
     */
    public void onInitTrade(IRequestCycle clcle) throws Exception
    {
        IData data = getData();
        // 记录页面初始化时间点，存入台帐表EXEC_ACTION字段
        String execAction = SysDateMgr.getSysTime();
        data.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
        IDataset dataset = CSViewCall.call(this, "SS.CreateFixTelUserSVC.onInitTrade", data);
        String departKindCode = StaticUtil.getStaticValue(getVisit(), "TD_M_DEPART", "DEPART_ID", "DEPART_KIND_CODE", this.getVisit().getDepartId());
        dataset.first().put("DEPART_KIND_CODE", departKindCode);
        dataset.first().put("EXEC_ACTION", execAction);
        setInitInfo(dataset.first());
        initProductType(data);
    }

    /**
     * 前台更换设备ID,后台查询该设备ID对应的使用方式
     * 
     * @author dengyong3
     * @param clcle
     * @throws Exception
     */
    public void onSelDeviceKindChange(IRequestCycle cycle) throws Exception
    {
        IData param = getData();
        String deviceKind = param.getString("DEVICE_KIND", "");
        if (deviceKind.equals(""))
            return;

        IData data = new DataMap();
        data.put("TYPE_ID", deviceKind.equals("01") ? "FIXED_PHONEDEVICEUSETYPE" : "FIXED_METERDEVICEUSETYPE");
        IDataset result = CSViewCall.call(this, "CS.StaticInfoQrySVC.getStaticValueByTypeId", data);
        setDeviceUseTypeList(result);
    }

    /**
     * 业务提交
     * 
     * @author chenzm
     * @param clcle
     * @throws Exception
     */
    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IDataset result = CSViewCall.call(this, "SS.CreateFixTelUserSVC.tradeReg", data);
        setAjax(result);
    }

    /**
     * 前台更换固话号码后,释放该号码资源
     * 
     * @author dengyong3
     * @param clcle
     * @throws Exception
     */
    public void releaseSelectedSNRes(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        String serialNumber = data.getString("RES_NO", "");
        if (serialNumber.equals(""))
            return;

        CSViewCall.call(this, "SS.CreateFixTelUserSVC.releaseSelectedSNRes", data);
    }

    public void search(IRequestCycle cycle) throws Exception
    {
        IData param = this.getData();
        String searchText = param.getString("SEARCH_NAME");
        String eparchyCode = param.getString("EPARCHY_CODE");
        String productId = param.getString("PRODUCT_ID");
        String searchType = param.getString("SEARCH_TYPE");
        if (StringUtils.isNotBlank(searchText) && searchText.length() >= 2 && StringUtils.isNotBlank(eparchyCode))
        {
            if ("1".equals(searchType))
            {
                // 产品搜索
                Map<String, String> searchData = new HashMap<String, String>();
                searchData.put("RELEASE_EPARCHY_CODE", eparchyCode);
                SearchResponse resp = SearchClient.search("TD_B_PRODUCT", searchText, searchData, 0, 10);
                IDataset datas = resp.getDatas();

                searchData.put("RELEASE_EPARCHY_CODE", "ZZZZ");
                resp = SearchClient.search("TD_B_PRODUCT", searchText, searchData, 0, 10);
                datas.addAll(resp.getDatas());
                ProductPrivUtil.filterProductListByPriv(this.getVisit().getStaffId(), datas);
                this.setAjax(datas);
            }
            else if ("2".equals(searchType))
            {
                // 元素搜索
                Map<String, String> searchData = new HashMap<String, String>();
                searchData.put("EPARCHY_CODE", eparchyCode);
                searchData.put("PRODUCT_ID", productId);
                SearchResponse resp = SearchClient.search("TD_B_PACKAGE_ELEMENT", searchText, searchData, 0, 10);
                IDataset datas = resp.getDatas();

                searchData.put("EPARCHY_CODE", "ZZZZ");
                searchData.put("PRODUCT_ID", productId);
                resp = SearchClient.search("TD_B_PACKAGE_ELEMENT", searchText, searchData, 0, 10);
                datas.addAll(resp.getDatas());
                ElementPrivUtil.filterElementListByPriv(this.getVisit().getStaffId(), datas);
                this.setAjax(datas);
            }
        }
    }

    public abstract void setAcctInfoDays(IDataset acctInfoDays);

    public abstract void setBankCodeList(IDataset bankCodeList);

    public abstract void setCityCodeList(IDataset cityCodeList);

    public abstract void setDeviceUseTypeList(IDataset set);

    public abstract void setEditInfo(IData editInfo);

    public abstract void setInitInfo(IData initInfo);

    public abstract void setNoteTypeList(IDataset noteTypeList);

    public abstract void setProductTypeList(IDataset productTypeList);

    public abstract void setReturnInfo(IData returnInfo);
}
