
package com.asiainfo.veris.crm.order.web.person.createtduser;

import java.util.HashMap;
import java.util.Map;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.search.SearchResponse;
import com.ailk.search.client.SearchClient;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.ElementPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.ProductPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: CreateTDPersonUser.java
 * @Description:
 * @version: v1.0.0
 * @author: yxd
 * @date: 2014-8-7 下午8:20:28 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-8-7 yxd v1.0.0 修改原因
 */
public abstract class CreateTDPersonUser extends PersonBasePage
{
    /**
     * @Function: checkSerialNumber()
     * @Description: 号码校验
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: yxd
     * @date: 2014-8-7 下午9:07:34 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-8-7 yxd v1.0.0 修改原因
     */
    public void checkSerialNumber(IRequestCycle clcle) throws Exception
    {
        IData data = getData();
        IDataset dataset = CSViewCall.call(this, "SS.CreateTDPersonUserSVC.checkSerialNumber", data);
        this.setEditInfo(dataset.first());
        setAjax(dataset);
    }

    /**
     * @Function: checkSimCardNo()
     * @Description: SIM卡校验
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: yxd
     * @date: 2014-8-7 下午9:08:00 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-8-7 yxd v1.0.0 修改原因
     */
    public void checkSimCardNo(IRequestCycle clcle) throws Exception
    {
        IData data = getData();
        IDataset dataset = CSViewCall.call(this, "SS.CreateTDPersonUserSVC.checkSimCardNo", data);
        this.setEditInfo(dataset.first());
        setAjax(dataset);
    }

    /**
     * @Function: getProductFeeInfo()
     * @Description: 获取产品费用
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: yxd
     * @date: 2014-8-7 下午9:01:56 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-8-7 yxd v1.0.0 修改原因
     */
    public void getProductFeeInfo(IRequestCycle clcle) throws Exception
    {
        IData data = getData();
        data.put(Route.ROUTE_EPARCHY_CODE, data.getString("EPARCHY_CODE"));
        IDataset dataset = CSViewCall.call(this, "SS.CreateTDPersonUserSVC.getProductFeeInfo", data);
        setAjax(dataset);
    }

    /**
     * @Function: onInitTrade()
     * @Description:
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: yxd
     * @date: 2014-8-7 下午9:07:34 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-8-7 yxd v1.0.0 修改原因
     */
    public void onInitTrade(IRequestCycle clcle) throws Exception
    {
        IData data = getData();
        IDataset dataset = CSViewCall.call(this, "SS.CreateTDPersonUserSVC.onInitTrade", data);
        this.setProductTypeList(dataset);
        IData editInfo = new DataMap();
        editInfo.put("EPARCHY_CODE", this.getTradeEparchyCode());
        this.setEditInfo(editInfo);
    }

    /**
     * @Function: onTradeSubmit()
     * @Description: 业务提交
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: yxd
     * @date: 2014-8-7 下午9:06:44 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-8-7 yxd v1.0.0 修改原因
     */
    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IDataset result = CSViewCall.call(this, "SS.CreateTDPersonUserRegSVC.tradeReg", data);
        setAjax(result);
    }

    /**
     * @Function: search()
     * @Description: 搜索
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: yxd
     * @date: 2014-8-7 下午9:06:26 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-8-7 yxd v1.0.0 修改原因
     */
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
     * 人像信息比对员工信息
     * 
     * @param clcle
     * @throws Exception
     */
    public void isCmpPic(IRequestCycle clcle) throws Exception
    {
    	IData data = getData();
    	IData param = new DataMap();
    	
    	param.putAll(data);
    	param.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
    	
    	IDataset dataset = CSViewCall.call(this, "SS.CreatePersonUserSVC.isCmpPic", param);
    	setAjax(dataset.getData(0));
    }
    

    public abstract void setEditInfo(IData editInfo);

    public abstract void setProductTypeList(IDataset productTypeList);

}
