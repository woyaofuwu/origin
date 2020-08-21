
package com.asiainfo.veris.crm.order.web.frame.csview.group.changevpmnmemelement;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
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

        // 查询集团客户信息
        setGrpCustInfo(UCAInfoIntfViewUtil.qryGrpCustInfoByGrpCustId(this, preViewData.getString("CUST_ID")));

        // 查询成员三户信息
        IData mebUCAInfo = UCAInfoIntfViewUtil.qryMebUCAInfoByUserIdAndRoute(this, getData().getString("MEB_USER_ID", ""), getData().getString("MEB_EPARCHY_CODE"));

        IData mebUserInfo = mebUCAInfo.getData("MEB_USER_INFO");
        IData mebCustInfo = mebUCAInfo.getData("MEB_CUST_INFO");

        if (IDataUtil.isNotEmpty(mebCustInfo))
        {
            mebUserInfo.putAll(mebCustInfo);
        }
        setGrpMemberInfo(mebUserInfo);

        // 产品包、元素信息
        IDataset selectedElementList = new DatasetList();
        String selectedElementStr = getData().getString("SELECTED_ELEMENTS", "[]");

        if (StringUtils.isBlank(selectedElementStr) || "{}".equals(selectedElementStr))
        {
            selectedElementList = new DatasetList();
        }
        else
        {
            selectedElementList = new DatasetList(selectedElementStr);
        }

        IData memberPkgElements = new DataMap();// 成员产品元素
        GroupBaseView.processProductElements(this, selectedElementList, memberPkgElements);
        setMemberProduct(memberPkgElements);

        // 产品控制信息
        /*
         * IData productCtrlInfo = new DataMap(); String productId = preViewData.getString("PRODUCT_ID"); String
         * grpUserId = preViewData.getString("GRP_USER_ID"); productCtrlInfo =
         * AttrBizInfoIntfViewUtil.qryChgMbProductCtrlInfoByProductId(this, productId); String tradeTypeCode =
         * productCtrlInfo.getData("TradeTypeCode").getString("ATTR_VALUE");
         */

        // 费用信息
        IDataset feeList = new DatasetList(); // super.initDefaultFee(productId, tradeTypeCode, selectedElementList);
        // //集团成员变更：不产生费用信息

        setGrpFeeList(feeList.toString());
    }

    public abstract void setCondition(IData condition);

    public abstract void setGrpCustInfo(IData grpCustInfo);

    public abstract void setGrpFeeList(String feeList);

    public abstract void setGrpMemberInfo(IData grpMemberInfo);

    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);

    public abstract void setMemberProduct(IData memberProduct);// 成员附加产品

}
