
package com.asiainfo.veris.crm.order.web.group.modifymemdata;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.userpayplan.UserPayPlanInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBaseView;

public abstract class PreView extends GroupBasePage
{

    public abstract IData getInfo();

    public abstract String getPamRemark();

    /**
     * 作用：页面的初始化
     * 
     * @author luoy 2009-07-29
     * @param cycle
     * @throws Exception
     */
    public void initial(IRequestCycle cycle) throws Exception
    {
        IData preViewData = getData();
        String grpUserId = preViewData.getString("GRP_USER_ID");
        // 查询集团客户信息
        setGrpCustInfo(UCAInfoIntfViewUtil.qryGrpCustInfoByGrpCustId(this, preViewData.getString("CUST_ID")));
        // 查询集团三户信息
        IData grpUCA = UCAInfoIntfViewUtil.qryGrpUCAInfoByUserId(this, grpUserId);
        if (IDataUtil.isNotEmpty(grpUCA))
        {
            setGrpAcctInfo(grpUCA.getData("GRP_ACCT_INFO"));
        }
        // 查询成员三户信息
        IData mebUCAInfo = UCAInfoIntfViewUtil.qryMebUCAInfoByUserIdAndRoute(this, getData().getString("MEB_USER_ID", ""), getData().getString("MEB_EPARCHY_CODE"));

        IData mebUserInfo = mebUCAInfo.getData("MEB_USER_INFO");
        IData mebCustInfo = mebUCAInfo.getData("MEB_CUST_INFO");

        if (IDataUtil.isNotEmpty(mebCustInfo))
        {
            mebUserInfo.putAll(mebCustInfo);
        }
        setGrpMemberInfo(mebUserInfo);
        // 付费计划
        IDataset payPlanInfos = new DatasetList();
        String payPlanStr = preViewData.getString("grpPayRels"); // j2ee PayPlanSel组件有问题，暂时不这样用
        // ,以PAY_PLAN_SEL_PLAN_TYPE替代
        // payPlanInfos = new DatasetList(payPlanStr);
        // [{"PLAN_ID":"14070303293402","PAY_TYPE":"个人付费","PAY_TYPE_CODE":"P","PLAN_TYPE":"P","MODIFY_TAG":"0"},{"PLAN_ID":"14070303293702","PAY_TYPE":"集团付费","PAY_TYPE_CODE":"G","PLAN_TYPE":"G","MODIFY_TAG":"1"}]
        //
        String payTypeCode = preViewData.getString("PAY_PLAN_SEL_PLAN_TYPE");
        String mebUserId = mebUserInfo.getString("USER_ID");
        String mebEparchyCode = mebUserInfo.getString("EPARCHY_CODE");
        IDataset planInfos = UserPayPlanInfoIntfViewUtil.getGrpMemPayPlanByUserId(this, mebUserId, grpUserId, mebEparchyCode);
        IData payPlanInfo = new DataMap();
        if (IDataUtil.isNotEmpty(planInfos))
        {
            IData planInfo = planInfos.getData(0);
            String oldPlanType = planInfo.getString("PLAN_TYPE_CODE");
            if (oldPlanType.equals(payTypeCode))
            {
                // "没改你提交个毛啊！"
            }
            else
            {
                if (StringUtils.isBlank(payPlanStr))
                    payPlanStr = "[]";
                payPlanInfos = new DatasetList(payPlanStr);
            }
        }
        IData payPlanData = new DataMap();
        GroupBaseView.processMebPayPlanInfos(getVisit(), payPlanInfos, payPlanData);
        setMemberPayPlan(payPlanData);
        // 产品包、元素信息
        // IDataset selectedElementList = new DatasetList();
        // String selectedElementStr = getData().getString("SELECTED_ELEMENTS", "[]");
        //
        // if (StringUtils.isBlank(selectedElementStr) || "{}".equals(selectedElementStr))
        // {
        // selectedElementList = new DatasetList();
        // }
        // else
        // {
        // selectedElementList = new DatasetList(selectedElementStr);
        // }
        //
        // IData memberPkgElements = new DataMap();// 成员产品元素
        // GroupBaseView.processProductElements(getVisit(), selectedElementList, memberPkgElements);
        // setMemberProduct(memberPkgElements);

        // 产品控制信息
        // IData productCtrlInfo = new DataMap();
        // String productId = preViewData.getString("PRODUCT_ID");
        // productCtrlInfo = GroupProductCtrlUtilView.queryChgMbProductCtrlInfoByProductIdUserId(this, productId,
        // grpUserId);
        //
        // String tradeTypeCode = productCtrlInfo.getData("TradeTypeCode").getString("ATTR_VALUE");
        //
        // // 费用信息
        // IDataset feeList = super.initDefaultFee(productId, tradeTypeCode, selectedElementList);
        //
        // setGrpFeeList(feeList.toString());
    }

    public abstract void setCondition(IData condition);

    public abstract void setGrpAcctInfo(IData grpAcctInfo);

    public abstract void setGrpCustInfo(IData grpCustInfo);

    public abstract void setGrpMemberInfo(IData grpMemberInfo);

    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);

    public abstract void setMemberPayPlan(IData memberPayPlan);// 成员付费计划

}
