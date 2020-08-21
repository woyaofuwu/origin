
package com.asiainfo.veris.crm.order.web.group.modifyuserdata;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBaseView;

public abstract class PreView extends GroupBasePage
{

    /**
     * 作用：页面的初始化
     * 
     * @author luojh 2009-07-29
     * @param cycle
     * @throws Throwable
     */
    public void initial(IRequestCycle cycle) throws Throwable
    {
        IData preViewData = getData();

        String productId = preViewData.getString("PRODUCT_ID");// 集团产品ID
        String custId = preViewData.getString("CUST_ID");// 集团客户ID
        String uesrId = preViewData.getString("GRP_USER_ID");// 集团用户ID
        String tradeTypeCode = preViewData.getString("TRADE_TYPE_CODE");// 业务类型
        String userEparchyCode = preViewData.getString("GRP_USER_EPARCHYCODE");

        // 查询集团客户信息
        IData grpCustInfo = UCAInfoIntfViewUtil.qryGrpCustInfoByGrpCustId(this, custId);
        setGrpCustInfo(grpCustInfo);
        IDataset payPlanInfos = new DatasetList();
        String payPlanStr = preViewData.getString("PAYPLAN_INFOS");// 集团付费计划列表
        payPlanInfos = new DatasetList(payPlanStr);
        IData userPayPlanData = new DataMap(); // 集团用户付费计划
        GroupBaseView.processPayPlanInfos(getVisit(), payPlanInfos, userPayPlanData);
        setUserProduct(userPayPlanData);
    }

    public abstract void setGrpCustInfo(IData grpCustInfo);// 集团客户信息

    public abstract void setInfo(IData info);

    public abstract void setUserProduct(IData userProduct); // 用户产品

}
