
package com.asiainfo.veris.crm.order.web.frame.csview.group.removegroupmember;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.exception.ProductException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.group.common.GroupBaseConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.relationuuinfo.RelationUUInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;

public abstract class RemoveGroupMember extends GroupBasePage
{

    public void initial(IRequestCycle cycle) throws Throwable
    {
        setHintInfo("集团成员用户销户,请注意成员的费用！");
    }

    public void initialEsop(IRequestCycle cycle) throws Throwable
    {
        IData cond = new DataMap();

        String serialNumber = getData().getString("SERIAL_NUMBER", "");
        String ibsysid = getData().getString("IBSYSID", "");
        String productId = getData().getString("PRODUCT_ID", "");

        cond.put("IBSYSID", ibsysid);
        cond.put("PRODUCT_ID", productId);
        cond.put("SERIAL_NUMBER", serialNumber);
        cond.put("ESOP_TAG", "ESOP_MEM");

        setCondition(cond);

        queryMemberInfo(cycle);
        initial(cycle);
    }

    /**
     * 集团成员用户 查询
     */
    public void queryMemberInfo(IRequestCycle cycle) throws Exception
    {

        IData param = new DataMap();

        // 查询集团成员用户信息
        String serialNumber = getData().getString("SERIAL_NUMBER");

        // 根据成员号码查询三户信息
        IData mebUCAInfoData = UCAInfoIntfViewUtil.qryMebUCAInfoBySn(this, serialNumber);

        IData mebUserInfoData = mebUCAInfoData.getData("MEB_USER_INFO");
        IData mebCustInfoData = mebUCAInfoData.getData("MEB_CUST_INFO");
        IData mebAcctInfoData = mebUCAInfoData.getData("MEB_ACCT_INFO");

        setUserInfo(mebUserInfoData);
        setAcctInfo(mebAcctInfoData);
        setCustInfo(mebCustInfoData);

        // 非集团成员用户开户的信息不能在此界面注销
        String productMode = mebUserInfoData.getString("PRODUCT_MODE");
        String productName = mebUserInfoData.getString("PRODUCT_NAME");

        if (!productMode.equals(GroupBaseConst.PRODUCT_MODE.MEM_BASE_PRODUCT.getValue()) && !productMode.equals(GroupBaseConst.PRODUCT_MODE.MEM_MAIN_PLUS_PRODUCT.getValue()))
        {
            CSViewException.apperr(ProductException.CRM_PRODUCT_16, serialNumber, productMode, productName);
        }

        // IMS号码校验是否存在有效的CNTRX业务订购关系
        if ("IMSG".equals(mebUserInfoData.getString("BRAND_CODE")))
        {
            IDataset uuInfo = RelationUUInfoIntfViewUtil.qryRelaUUInfosByUserIdB(this, mebUserInfoData.getString("USER_ID"), mebUserInfoData.getString("EPARCHY_CODE"));
            if (IDataUtil.isNotEmpty(uuInfo))
            {
                CSViewException.apperr(GrpException.CRM_GRP_503, serialNumber);
            }
        }

        // 查询用户的费用情况
        param.clear();
        param.put("USER_ID", mebUserInfoData.getString("USER_ID"));
        param.put(Route.ROUTE_EPARCHY_CODE, mebUserInfoData.getString("EPARCHY_CODE"));

        // 调帐务接口 欠费信息: LAST_OWE_FEE:往月欠费 REAL_FEE:实时欠费 ACCT_BALANCE:实时结余
        IData oweFee = CSViewCall.callone(this, "CS.UserOwenInfoQrySVC.getOweFeeByUserId", param);
        setOweInfo(oweFee);

        // 查找是否有未完工的工单
        param.clear();
        param.put("USER_ID", mebUserInfoData.getString("USER_ID"));
        param.put("TRADE_TYPE_CODE", "3009");
        param.put(Route.ROUTE_EPARCHY_CODE, Route.getJourDb(mebUserInfoData.getString("EPARCHY_CODE")));
        IDataset finishData = CSViewCall.call(this, "CS.TradeInfoQrySVC.getMainTradeByRG", param);
        if (finishData.size() > 0)
        {
            CSViewException.apperr(GrpException.CRM_GRP_319, serialNumber);
        }

    }

    public abstract void setAcctInfo(IData acctinfo);

    public abstract void setBasecomminfo(IData basecomminfo);

    public abstract void setCondition(IData condition);

    public abstract void setCustInfo(IData custinfo);

    public abstract void setHintInfo(String hintInfo);

    public abstract void setInfo(IData info);

    public abstract void setOweInfo(IData oweinfo);

    public abstract void setUserInfo(IData userinfo);

    public void submit(IRequestCycle cycle) throws Exception
    {

        IData param = new DataMap();
        param.put("SERIAL_NUMBER", getData().getString("SERIAL_NUMBER"));
        param.put("REMOVE_REASON", getData().getString("REMOVE_REASON"));
        param.put("REMOVE_REASON_CODE", getData().getString("REMOVE_REASON_CODE"));
        param.put("REMARK", getData().getString("REMARK"));
        param.put(Route.ROUTE_EPARCHY_CODE, getData().getString("USER_EPARCHY_CODE"));

        IDataset result = CSViewCall.call(this, "CS.RemoveGroupMemberSVC.removeMember", param);

        setAjax(result);

    }

}
