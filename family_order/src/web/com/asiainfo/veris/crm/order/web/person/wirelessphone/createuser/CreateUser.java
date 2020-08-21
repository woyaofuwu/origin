/**
 * 
 */

package com.asiainfo.veris.crm.order.web.person.wirelessphone.createuser;

import java.util.HashMap;
import java.util.Map;

import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.search.SearchResponse;
import com.ailk.search.client.SearchClient;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.ElementPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.ProductPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: CreateUser.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: chengxf2
 * @date: 2014-7-7 下午09:19:42 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-7-7 chengxf2 v1.0.0 修改原因
 */

public abstract class CreateUser extends PersonBasePage
{

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-7-8 上午10:21:50 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-7-8 chengxf2 v1.0.0 修改原因
     */
    public void checkSerialNumber(IRequestCycle clcle) throws Exception
    {
        IData data = getData();
        IDataset dataset = CSViewCall.call(this, "SS.CreateUserSVC.checkSerialNumber", data);
        setAjax(dataset);
    }

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-7-8 下午05:54:04 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-7-8 chengxf2 v1.0.0 修改原因
     */
    public void getProductFeeInfo(IRequestCycle clcle) throws Exception
    {
        IData data = getData();
        data.put(Route.ROUTE_EPARCHY_CODE, data.getString("EPARCHY_CODE"));
        IDataset dataset = CSViewCall.call(this, "SS.CreateUserSVC.getProductFeeInfo", data);
        setAjax(dataset);
    }

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-7-8 下午05:21:28 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-7-8 chengxf2 v1.0.0 修改原因
     */
    public void onInitTrade(IRequestCycle clcle) throws Exception
    {
        IData data = getData();
        IDataset dataset = CSViewCall.call(this, "SS.CreateUserSVC.onInitTrade", data);
        IDataset productTypeList = dataset.first().getDataset("PRODUCT_TYPE_LIST");
        this.setProductTypeList(productTypeList);
        IData editInfo = new DataMap();
        editInfo.put("EPARCHY_CODE", this.getTradeEparchyCode());
        String execAction = SysDateMgr.getSysTime();
        String departKindCode = StaticUtil.getStaticValue(getVisit(), "TD_M_DEPART", "DEPART_ID", "DEPART_KIND_CODE", this.getVisit().getDepartId());
        editInfo.put("DEPART_KIND_CODE", departKindCode);
        editInfo.put("EXEC_ACTION", execAction);
        editInfo.put("CHECK_USER_PSPTID", "CREATEUSERSW");
        this.setEditInfo(editInfo);
    }

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-7-9 上午10:53:47 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-7-9 chengxf2 v1.0.0 修改原因
     */
    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IDataset result = CSViewCall.call(this, "SS.CreateUserRegSVC.tradeReg", data);
        setAjax(result);
    }

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-7-8 下午05:21:23 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-7-8 chengxf2 v1.0.0 修改原因
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

    public abstract void setAcctInfoDays(IDataset acctInfoDays);

    public abstract void setBankCodeList(IDataset bankCodeList);

    public abstract void setCityCodeList(IDataset cityCodeList);

    public abstract void setEditInfo(IData editInfo);

    public abstract void setInitInfo(IData initInfo);

    public abstract void setPackageInfos(IDataset packageinfos);

    public abstract void setProductTypeList(IDataset productTypeList);

    public abstract void setReturnInfo(IData returnInfo);
}
