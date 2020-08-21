
package com.asiainfo.veris.crm.order.web.frame.csview.group.opengroupmember;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.attrbiz.AttrBizInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.util.GroupProductUtilView;

public abstract class BaseInfo extends GroupBasePage
{
    /**
     * 查询集团成员开户信息
     */
    public void getGroupBaseInfo(IRequestCycle cycle) throws Throwable
    {
        IData groupinfo = queryGroupCustInfo(cycle);
        groupinfo.put("default", "");
        groupinfo.put("PSPT_TYPE_CODE", "".equals(groupinfo.getString("PSPT_TYPE_CODE", "")) ? "Z" : groupinfo.getString("PSPT_TYPE_CODE"));
        groupinfo.put("USER_TYPE_CODE", "".equals(groupinfo.getString("USER_TYPE_CODE", "")) ? "0" : groupinfo.getString("USER_TYPE_CODE"));
        setGroupInfo(groupinfo);
    }

    /**
     * 根据group_sn查询集团基本信息 默认传入为cond_GROUP_ID
     * 
     * @param cycle
     * @throws Throwable
     */
    public void getGroupBySN(IRequestCycle cycle) throws Throwable
    {
        String grpSn = getData().getString("cond_GROUP_SERIAL_NUMBER");

        IData data = UCAInfoIntfViewUtil.qryGrpUCAInfoByGrpSn(this, grpSn);
        if (IDataUtil.isEmpty(data))
            return;

        IData userinfo = data.getData("GRP_USER_INFO");
        IData groupinfo = data.getData("GRP_CUST_INFO");
        setGroupInfo(groupinfo);
        setUserInfo(userinfo);
    }

    /**
     * 初始化
     * 
     * @param cycle
     * @throws Throwable
     */
    public void initial(IRequestCycle cycle) throws Throwable
    {

        IData condition = new DataMap();
        String grpUserEparchyCode = getTradeEparchyCode();
        condition.put("GRP_USER_EPARCHYCODE", grpUserEparchyCode);

        setCondition(condition);

    }

    /**
     * 作用：查询集团产品相关信息
     * 
     * @author xiajj
     * @throws Throwable
     */
    public void queryProductInfo(IRequestCycle cycle) throws Throwable
    {
        String productId = getData().getString("PRODUCT_ID");
        setProductInfo(GroupProductUtilView.getProductExplainInfo(this, productId));

        IData productCtrlInfo = AttrBizInfoIntfViewUtil.qryOpnMbProductCtrlInfoByProductId(this, productId);
        setProductCtrlInfo(productCtrlInfo);

    }
    
    /**
     * REQ201801150022_新增IMS号码开户人像比对功能
     * <br/>
     * 判断是否有免人像比对权限
     * @param clcle
     * @throws Exception
     * @author zhuoyingzhi
     * @date 20180321
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
    
    
    public abstract void setCondition(IData condition);

    public abstract void setCustInfo(IData custInfo);

    public abstract void setGroupInfo(IData groupInfo);

    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);

    public abstract void setProductCtrlInfo(IData productCtrlInfo);

    public abstract void setProductInfo(IData productInfo);// 产品信息

    public abstract void setUserInfo(IData userInfo);

}
