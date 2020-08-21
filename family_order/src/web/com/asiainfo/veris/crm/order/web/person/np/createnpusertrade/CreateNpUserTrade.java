
package com.asiainfo.veris.crm.order.web.person.np.createnpusertrade;

import java.util.HashMap;
import java.util.Map;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.search.SearchResponse;
import com.ailk.search.client.SearchClient;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserNpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.ElementPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.ProductPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class CreateNpUserTrade extends PersonBasePage
{

    /**
     * 开户数限制校验
     * 
     * @author chenzm
     * @param clcle
     * @throws Exception
     */
    public void checkRealNameLimitByPspt(IRequestCycle clcle) throws Exception
    {
        IData data = getData();
        data.put(Route.ROUTE_EPARCHY_CODE, data.getString("EPARCHY_CODE"));
        IDataset dataset = CSViewCall.call(this, "SS.CreateNpUserTradeSVC.checkRealNameLimitByPspt", data);
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
        data.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
        IDataset ids = CSViewCall.call(this, "SS.CreateNpUserTradeSVC.checkAndInitProduct", data);

        setEditInfo(ids.getData(0).getData("checkSerialNumber"));
        setProductTypeList(ids.getData(0).getData("getProductInfos").getDataset("PRODUCT_TYPE_LIST"));
        setAjax(ids);
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
        IDataset dataset = CSViewCall.call(this, "SS.CreateNpUserTradeSVC.checkSimCardNo", data);
        setAjax(dataset);
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
        IDataset dataset = CSViewCall.call(this, "SS.CreateNpUserTradeSVC.getProductFeeInfo", data);
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
     * @Function: onInitTrade
     * @Description:暂时不用
     * @param clcle
     * @throws Exception
     * @version: v1.0.0
     * @author: lijm3
     * @date: 2014-5-26 上午9:09:39
     */
    public void onInitTrade(IRequestCycle clcle) throws Exception
    {
      String strDate1 =   SysDateMgr.getLastDateThisMonth4WEB();
      String strDate2  = SysDateMgr.getSysTime();
      
      int day = SysDateMgr.dayInterval(strDate1, strDate2);
      
      if(day<=1){
         CSViewException.apperr(CrmUserNpException.CRM_USER_NP_9527, "携转业务月末两天不充许办理！");
      }
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
        data.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
        IDataset result = CSViewCall.call(this, "SS.CreateNpUserTradeRegSVC.tradeReg", data);
        setAjax(result);
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
    
    /**
     * REQ201602290007 关于入网业务人证一致性核验提醒的需求
     * 获取是否需要弹出框，chenxy3
     * 2016-03-08 chenxy3
     */
    public void checkNeedBeforeCheck(IRequestCycle clcle) throws Exception
    {
        IData data = getData();
        IDataset dataset = CSViewCall.call(this, "SS.BeforeCheckSVC.checkNeedBeforeCheck", data);
        setAjax(dataset.getData(0));
    }
    

    public abstract void setBankCodeList(IDataset bankCodeList);

    public abstract void setCityCodeList(IDataset cityCodeList);

    public abstract void setEditInfo(IData editInfo);

    public abstract void setInitInfo(IData initInfo);

    public abstract void setNoteTypeList(IDataset noteTypeList);

    public abstract void setProductTypeList(IDataset productTypeList);

    public abstract void setReturnInfo(IData returnInfo);
}
