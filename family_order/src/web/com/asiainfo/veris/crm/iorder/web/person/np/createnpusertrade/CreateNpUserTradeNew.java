
package com.asiainfo.veris.crm.iorder.web.person.np.createnpusertrade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.search.SearchResponse;
import com.ailk.search.client.SearchClient;
import com.asiainfo.veris.crm.iorder.pub.consts.IUpcConst;
import com.asiainfo.veris.crm.iorder.web.igroup.util.IUpcViewCall;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserNpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.ElementPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.ProductPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.util.DataSetUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.upc.UpcViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;
import org.apache.tapestry.IRequestCycle;

import java.util.HashMap;
import java.util.Map;

public abstract class CreateNpUserTradeNew extends PersonBasePage
{

    /**
     * 开户数限制校验
     * 
     * @author chenzm
     * @param cycle
     * @throws Exception
     */
    public void checkRealNameLimitByPspt(IRequestCycle cycle) throws Exception
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
     * @param cycle
     * @throws Exception
     */
    public void checkSerialNumber(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        data.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
        IDataset ids = CSViewCall.call(this, "SS.CreateNpUserTradeSVC.checkAndInitProduct", data);
        IDataset productTypeList = ids.getData(0).getData("getProductInfos").getDataset("PRODUCT_TYPE_LIST");
        StringBuilder productTypeStr = new StringBuilder();
        for (Object obj : productTypeList) {
            IData productType = (IData) obj;
            productTypeStr.append(productType.getString("PRODUCT_TYPE_CODE")).append(",");
        }

        setEditInfo(ids.getData(0).getData("checkSerialNumber"));
        setProductTypeList(productTypeStr.toString());

        IDataset packageInfos = ids.first().getData("checkSerialNumber").getDataset("PACKAGES_SALE");
        setPackageInfos(packageInfos);

        String privTag = "0";
        if (StaffPrivUtil.isFuncDataPriv(this.getVisit().getStaffId(), "SYSCHANGPACKAGE")) {
            privTag = "1";
        }
        IData dataRight = getData();
        dataRight.put("SYSCHANGPACKAGE", privTag);
        setInitInfo(dataRight);
        
        setAjax(ids);

        // 查询"热门产品"和"推荐产品"
        IDataset hotRecommSales = new DatasetList();
        IData param = new DataMap();
        param.put("POPUTRADECODE", "3"); // 仅查询产品套餐类
        param.put("STAFF_ID", getVisit().getStaffId());
        IData saleInfos = CSViewCall.callone(this, "CS.WelcomeSVC.getHotAndRecInfo", param);
        if (IDataUtil.isNotEmpty(saleInfos)) {
            IDataset hotSales = saleInfos.getData("HOTINFOS").getDataset("PRODUCTS");
            IDataset recommSales = saleInfos.getData("RECINFOS").getDataset("PRODUCTS");
            if (IDataUtil.isNotEmpty(hotSales))
                hotRecommSales.addAll(hotSales);
            if (IDataUtil.isNotEmpty(recommSales))
                hotRecommSales.addAll(recommSales);
            for (Object obj : hotRecommSales) {
                IData sale = (IData) obj;
                IData offerInfo = UpcViewCall.queryOfferByOfferId(this, IUpcConst.ELEMENT_TYPE_CODE_PRODUCT, sale.getString("OFFER_CODE"));
                String brandCode = IUpcViewCall.queryBrandCodeByOfferId(offerInfo.getString("OFFER_ID"));
                sale.put("BRAND_CODE", brandCode);
            }
        }
        setHotRecomms(hotRecommSales);
    }

    /**
     * 输入SIM卡后的校验，获取卡信息
     * 
     * @author chenzm
     * @param cycle
     * @throws Exception
     */
    public void checkSimCardNo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IDataset dataset = CSViewCall.call(this, "SS.CreateNpUserTradeSVC.checkSimCardNo", data);
        setAjax(dataset);
    }

    /**
     * 获取产品费用
     * 
     * @author chenzm
     * @param cycle
     * @throws Exception
     */
    public void getProductFeeInfo(IRequestCycle cycle) throws Exception
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
     * @param cycle
     * @throws Exception
     * @version: v1.0.0
     * @author: lijm3
     * @date: 2014-5-26 上午9:09:39
     */
    public void onInitTrade(IRequestCycle cycle) throws Exception
    {
    	
    	/*
    	 * REQ201908210003	携号转网改造点补充20190815 （月末和春节假期正常受理业务）
    	 */
//      String strDate1 =   SysDateMgr.getLastDateThisMonth4WEB();
//      String strDate2  = SysDateMgr.getSysTime();
//      
//      int day = SysDateMgr.dayInterval(strDate1, strDate2);
//      
//      if(day<=1){
//          CSViewException.apperr(CrmUserNpException.CRM_USER_NP_9527, "携转业务月末两天不充许办理！");
//       }
    }

    /**
     * 业务提交
     * 
     * @author chenzm
     * @param cycle
     * @throws Exception
     */
    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        data.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
        IDataset result = CSViewCall.call(this, "SS.CreateNpUserTradeRegSVC.tradeReg", data);
        
        //REQ201904190005全网用户数据查询平台分册V2.2.0改造需求
        IData bizCode = new DataMap();
		bizCode.put("SUBSYS_CODE", "CSM");
		bizCode.put("PARAM_ATTR", "9124");
		bizCode.put("PARAM_CODE", "SAVE_PIC_TAG");
		IDataset commparas = CSViewCall.call(this, "CS.CommparaInfoQrySVC.getCommparaInfoByBizCode", bizCode);
        if (IDataUtil.isNotEmpty(commparas))
        {
        	if("1".equals(commparas.getData(0).getString("PARA_CODE1", "0")))
        	{
		        if(DataSetUtils.isBlank(result)) {
					CSViewException.apperr(CrmCommException.CRM_COMM_595);
				}
				String orderId = result.first().getString("ORDER_ID","");
		        String tradeId = result.first().getString("TRADE_ID","");
		        String sid=SysDateMgr.getSysDateYYYYMMDDHHMMSS();
		        orderId=orderId.substring(0, orderId.length()-4);
		        data.put("MOBILENUM", data.getString("SERIAL_NUMBER",""));
		        data.put("IDCARDNUM", data.getString("PSPT_ID",""));  
		        data.put("REMOTE_ORDER_ID", "COP898" + sid + orderId);
		        data.put("TRADE_ID", tradeId);
		        //System.out.println("-----------zhangxing3XieRu------------params:"+data);
		        CSViewCall.call(this, "SS.RemoteDestroyUserSVC.insPsptFrontBack", data);
        	}
        }
        //REQ201904190005全网用户数据查询平台分册V2.2.0改造需求
        
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
    public void checkNeedBeforeCheck(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IDataset dataset = CSViewCall.call(this, "SS.BeforeCheckSVC.checkNeedBeforeCheck", data);
        setAjax(dataset.getData(0));
    }
    
    /**
     * 查验流程处理
     * 校验是否已查验
     * 2019-03-13 dengyi5
     */
    public void checkBeforeNpUser(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        data.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
        IDataset dataset = CSViewCall.call(this, "SS.CreateNpUserTradeSVC.checkBeforeNpUser", data);
        setAjax(dataset.getData(0));
    }

    /**
     * @Description: 选择产品时业务提示信息
     * @param:参数描述
     * @return：返回结果描述
     * @version: v1.0.0
     * @author: liwei29
     * @date: 2020-07-13
     */
    public void createPersonUserTipsInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();

        IData infos = CSViewCall.callone(this, "SS.CreatePersonUserSVC.createPersonUserTipsInfo", data);

        setAjax(infos);
    }
    

    public abstract void setBankCodeList(IDataset bankCodeList);

    public abstract void setCityCodeList(IDataset cityCodeList);

    public abstract void setEditInfo(IData editInfo);

    public abstract void setInitInfo(IData initInfo);

    public abstract void setHotRecomms(IDataset hotRecomms);

    public abstract void setPackageInfos(IDataset packageinfos);

    public abstract void setProductTypeList(String productTypeList);

    public abstract void setReturnInfo(IData returnInfo);
}
