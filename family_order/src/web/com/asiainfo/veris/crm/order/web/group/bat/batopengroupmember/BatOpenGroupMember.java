
package com.asiainfo.veris.crm.order.web.group.bat.batopengroupmember;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;

public abstract class BatOpenGroupMember extends GroupBasePage
{

    /**
     * 根据group_id查询集团基本信息 默认传入为cond_GROUP_ID
     * 
     * @author zhujm 2009-03-06
     * @param cycle
     * @throws Throwable
     */
    public void getGroupBaseInfo(IRequestCycle cycle) throws Throwable
    {
        IData groupInfo = queryGroupCustInfo(cycle);

        groupInfo.put("PSPT_TYPE_CODE", "".equals(groupInfo.getString("PSPT_TYPE_CODE", "")) ? "Z" : groupInfo.getString("PSPT_TYPE_CODE"));
        groupInfo.put("USER_TYPE_CODE", "".equals(groupInfo.getString("USER_TYPE_CODE", "")) ? "0" : groupInfo.getString("USER_TYPE_CODE"));

        setGroupInfo(groupInfo);
    }

    /**
     * 初始化批量弹出窗口页面
     * 
     * @param cycle
     * @throws Throwable
     */
    public void initial(IRequestCycle cycle) throws Throwable
    {
        getData().put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
        getData().put(Route.USER_EPARCHY_CODE, getTradeEparchyCode());
        getData().put("EPARCHY_CODE", getTradeEparchyCode());
        String esopTag = getData().getString("ESOP_TAG");
        if ("ESOP".equals(esopTag))
        {
            getData().put("ESOP_TAG", esopTag);
        }

        setCondition(getData());

        if ("ESOP".equals(esopTag))
        {
            // queryEsopInit(cycle);
        }
    }

    public void queryProductTypeList(IRequestCycle cycle) throws Throwable
    {
        IData param = new DataMap();
        param.put("PRODUCT_MODE", "20");

        IDataset productTypeList = CSViewCall.call(this, "CS.ProductInfoQrySVC.qryProductsByProductMode", param);

        setProductTypeList(productTypeList);

    }

    /**
     * 作用：初始化产品
     * 
     * @author luojh 2009-09-04 17:25
     * @param cycle
     * @throws Exception
     */
    public void refreshProduct(IRequestCycle cycle) throws Exception
    {

        String eparchyCode = getTradeEparchyCode();

        IData condition = getData();
        condition.put("EPARCHY_CODE", eparchyCode);
        condition.put(Route.ROUTE_EPARCHY_CODE, eparchyCode);
        condition.put(Route.USER_EPARCHY_CODE, eparchyCode);
        condition.put("GROUP_ID", getData().getString("GROUP_ID", ""));

        productId = getData().getString("PRODUCT_ID", "");

        this.setCondition(condition);

        this.setProductId(productId);
    }
   
    /**
     * REQ201801150022_新增IMS号码开户人像比对功能
     * <br/>
     * 判断是否有免人像比对权限
     * @param clcle
     * @throws Exception
     * @author zhuoyingzhi
     * @date 20180326
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
    
    /**
     * REQ201904260020新增物联网批量开户界面权限控制需求
     * 免人像比对权限判断
     * @author mengqx
     * @date 20190515
     * @param clcle
     * @throws Exception
     */
    public void isBatCmpPic(IRequestCycle clcle) throws Exception
    {
    	IData data = getData();
    	IData param = new DataMap();
    	
    	param.putAll(data);
    	param.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
    	
    	IDataset dataset = CSViewCall.call(this, "SS.CreatePersonUserSVC.isBatCmpPic", param);
    	setAjax(dataset.getData(0));
    }

    public abstract void setCondition(IData condition);

    public abstract void setElements(IDataset elementsList);

    public abstract void setExtendElements(IDataset extendElements);

    public abstract void setGroupInfo(IData groupInfo);

    public abstract void setInfo(IData info);

    public abstract void setPlusProducts(IDataset plusProducts);

    public abstract void setProductTypeList(IDataset productTypeList);

    public abstract void setUserInfo(IData userInfo);
}
