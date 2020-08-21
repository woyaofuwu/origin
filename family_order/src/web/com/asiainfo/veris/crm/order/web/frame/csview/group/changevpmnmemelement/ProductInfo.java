
package com.asiainfo.veris.crm.order.web.frame.csview.group.changevpmnmemelement;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.attrbiz.AttrBizInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.util.GroupProductUtilView;

public abstract class ProductInfo extends GroupBasePage
{
    /**
     * 作用：初始化页面
     * 
     * @author luojh 2009-07-29
     * @param cycle
     * @throws Exception
     */
    public void initial(IRequestCycle cycle) throws Exception
    {

        String grpUserId = getData().getString("GRP_USER_ID", "");
        String grpSn = getData().getString("GRP_USER_ID", "");
        String grpId = getData().getString("GROUP_ID", "");
        String mebuserId = getData().getString("MEB_USER_ID", "");
        String mebsn = getData().getString("MEB_SERIAL_NUMBER", "");
        String mebeparchycode = getData().getString("MEB_EPARCHY_CODE", "");
        productId = getData().getString("GRP_PRODUCT_ID", "");

        // 获取产品控制信息
        IData productCtrlInfo = AttrBizInfoIntfViewUtil.qryChgMbProductCtrlInfoByProductId(this, productId);
        setProductCtrlInfo(productCtrlInfo);

        // 获取成员账期信息
        IData mebUserAcctDayInfo = UCAInfoIntfViewUtil.qryMebUserAcctDayInfoUserId(this, mebuserId, mebeparchycode);

        // 获取产品组件的已选区初始参数
        IData cond = new DataMap();
        cond.put("PRODUCT_ID", productId);
        cond.put("USER_ID", mebuserId);
        cond.put("EPARCHY_CODE", mebeparchycode);
        cond.put("TRADE_TYPE_CODE", productCtrlInfo.getData("TradeTypeCode").getString("ATTR_VALUE"));
        cond.put(Route.ROUTE_EPARCHY_CODE, mebeparchycode);

        String acctDay = mebUserAcctDayInfo.getString("USER_ACCTDAY_ACCT_DAY", "");
        String firstDate = mebUserAcctDayInfo.getString("USER_ACCTDAY_FIRST_DATE", "");
        String nextAcctDay = mebUserAcctDayInfo.getString("USER_ACCTDAY_NEXT_ACCT_DAY", "");
        String nextFirstDate = mebUserAcctDayInfo.getString("USER_ACCTDAY_NEXT_FIRST_DATE", "");
        cond.put("ACCT_DAY", acctDay);
        cond.put("FIRST_DATE", firstDate);
        cond.put("NEXT_ACCT_DAY", nextAcctDay);
        cond.put("NEXT_FIRST_DATE", nextFirstDate);
        setCond(cond);

        // 获取产品组件的包列表区初始参数
        IData pkgParam = new DataMap();
        pkgParam.put("PRODUCT_ID", productId);
        pkgParam.put("GRP_USER_ID", grpUserId);
        pkgParam.put("EPARCHY_CODE", mebeparchycode);
        pkgParam.put(Route.USER_EPARCHY_CODE, mebeparchycode);
        setPkgParam(pkgParam);

        // 获取产品参数页面的的初始参数
        IData dynParam = new DataMap();
        dynParam.put("PRODUCT_ID", productId);
        dynParam.put("GRP_USER_ID", grpUserId);
        dynParam.put("GRP_SN", grpSn);
        dynParam.put("MEB_USER_ID", mebuserId);
        dynParam.put("MEB_EPARCHY_CODE", mebeparchycode);
        dynParam.put("EPARCHY_CODE", mebeparchycode);
        dynParam.put("MEB_SERIAL_NUMBER", mebsn);
        dynParam.put("GROUP_ID", grpId);
        dynParam.put(Route.ROUTE_EPARCHY_CODE, mebeparchycode);
        setDynParam(dynParam);

        setProductId(productId);
        setResList(GroupProductUtilView.initResList(this, getData().getString("MEB_USER_ID", ""), getData().getString("GRP_USER_ID", ""), getData().getString("MEB_EPARCHY_CODE", "")));

    }

    public abstract void setCond(IData info);

    public abstract void setDynParam(IData dynParam);

    public abstract void setInfo(IData info);

    public abstract void setPkgParam(IData pkgParam);

    public abstract void setProductCtrlInfo(IData productCtrlInfo);

    public abstract void setResList(IDataset idata);

}
