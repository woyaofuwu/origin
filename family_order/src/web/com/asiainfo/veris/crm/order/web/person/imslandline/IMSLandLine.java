package com.asiainfo.veris.crm.order.web.person.imslandline;

import java.util.HashMap;
import java.util.Map;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.search.SearchResponse;
import com.ailk.search.client.SearchClient;
import com.asiainfo.veris.crm.order.pub.consts.PersonConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.ElementPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.ProductPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.upc.UpcViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * IMS固话电话开户
 * <p>Title: IMSLandLine</p>
 * <p>Description: </p>
 * <p>Company: AsiaInfo</p>
 * @author Administrator
 * @date 2017-10-25 上午10:03:33
 */
public abstract class IMSLandLine extends PersonBasePage
{

	/**
	 * 输入号码后的查询
	 * */
    public void setPageInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        
        IData custInfo = new DataMap(data.getString("CUST_INFO", ""));
        this.setCustInfo(custInfo);//客户名称
        
        IData userInfo = new DataMap(data.getString("USER_INFO", ""));
        
        userInfo.put("PRODUCT_NAME", UpcViewCall.queryOfferNameByOfferId(this, PersonConst.ELEMENT_TYPE_CODE_PRODUCT, userInfo.getString("TD_B_PRODUCT")));
        
        this.setUserInfo(userInfo);//产品名称、开户日期
        
        IDataset dataset = CSViewCall.call(this, "SS.IMSLandLineSVC.checkAuthSerialNum", data);
        IData retData = dataset.first();
        this.setUserOldInfo(retData);//宽带信息
        setAjax(retData);

        IDataset productList = CSViewCall.call(this, "SS.IMSLandLineSVC.onInitTrade", data);
        this.setProductList(productList);
        
    }
    
    public void onInitTrade(IRequestCycle clcle) throws Exception
    {
        IData info = new DataMap();
        info.put("EPARCHY_CODE", this.getTradeEparchyCode());
        this.setInfo(info);
    }
	
    /**
     * 业务提交
     */
    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        if (StringUtils.isEmpty(data.getString("SERIAL_NUMBER", "")))
        {
            data.put("SERIAL_NUMBER", data.getString("AUTH_SERIAL_NUMBER"));
        }
        data.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
        data.put("WIDE_SERIAL_NUMBER","0898" + data.getString("FIX_NUMBER"));
        
        IDataset dataset = CSViewCall.call(this, "SS.IMSLandLineRegSVC.tradeReg", data);
        setAjax(dataset);
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
                SearchResponse resp = SearchClient.search("PM_OFFER_OPEN_USER", searchText, 0, 10);
                IDataset datas = resp.getDatas();
                ProductPrivUtil.filterProductListByPriv(this.getVisit().getStaffId(), datas);
                this.setAjax(datas);
            }
            else if ("2".equals(searchType))
            {
                // 元素搜索
                Map<String, String> searchData = new HashMap<String, String>();
                searchData.put("PRODUCT_ID", productId);
                SearchResponse resp = SearchClient.search("PM_OFFER_ELEMENT", searchText, searchData, 0, 10);
                IDataset datas = resp.getDatas();
                ElementPrivUtil.filterElementListByPriv(this.getVisit().getStaffId(), datas);
                this.setAjax(datas);
            }
        }
    }
    
    /**
     * 功能说明：校验固话号 
     */
    public void checkFixPhoneNum(IRequestCycle cycle) throws Exception
    {
    	IData userData = getData(); 
        String fixNum = userData.getString("FIX_NUMBER", ""); 
        userData.put("FIX_NUMBER", "0898"+fixNum);
        IDataset results = CSViewCall.call(this, "SS.IMSLandLineSVC.checkFixPhoneNum", userData);
        setAjax(results.first());
    }
    
    public void queryDiscntPackagesByPID(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IDataset dataset = CSViewCall.call(this, "SS.IMSLandLineSVC.queryDiscntPackagesByPID", data);
        IData retData = dataset.first();
        IDataset topSetBoxSaleActiveList = retData.getDataset("TOP_SET_BOX_SALE_ACTIVE_LIST");
        this.setTopSetBoxSaleActiveList(topSetBoxSaleActiveList);
        setAjax(retData);
    }
    
    public void checkSaleActive(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IData result = CSViewCall.callone(this, "SS.IMSLandLineSVC.checkSaleActive", data);
        this.setAjax(result);
    }
    
    public void queryCheckSaleActiveFee(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IData result = CSViewCall.callone(this, "SS.IMSLandLineSVC.queryCheckSaleActiveFee", data);
        this.setAjax(result);
    }
    
    /**
     * 提交前费用校验
     * @param cycle
     * @throws Exception
     * @author zhangyc5
     */
    public void checkFeeBeforeSubmit(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IData result = CSViewCall.callone(this, "SS.IMSLandLineSVC.checkFeeBeforeSubmit", data);
        this.setAjax(result);
    }

    public abstract void setBasePackages(IDataset basePackages);

    public abstract void setCustInfo(IData custInfo);
    
    public abstract void setInfo(IData info);
    
    public abstract void setProductList(IDataset datas);

    public abstract void setOptionPackages(IDataset optionPackages);

    public abstract void setProducts(IDataset products);

    public abstract void setUserInfo(IData userInfo);

    public abstract void setUserOldInfo(IData userOldInfo);
    
    public abstract void setTopSetBoxSaleActiveList(IDataset datas);

}
