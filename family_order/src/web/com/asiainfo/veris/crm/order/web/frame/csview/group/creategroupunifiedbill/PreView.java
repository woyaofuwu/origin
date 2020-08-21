
package com.asiainfo.veris.crm.order.web.frame.csview.group.creategroupunifiedbill;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.attrbiz.AttrBizInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBaseView;

public abstract class PreView extends GroupBasePage
{
    private String grpAcctId;

    private String grpUserId;

    private String mebEparchyCode;

    private String memAcctId;

    private String memUserId;

    public String getGrpAcctId() throws Exception
    {
        if (StringUtils.isBlank(grpAcctId))
            grpAcctId = getData().getString("GRP_ACCT_ID", "");
        return grpAcctId;
    }

    public String getGrpProductId() throws Exception
    {
        if (StringUtils.isBlank(productId))
            productId = getData().getString("PRODUCT_ID", "");
        return productId;
    }

    public String getGrpUserId() throws Exception
    {
        if (StringUtils.isBlank(grpUserId))
            grpUserId = getData().getString("GRP_USER_ID", "");
        return grpUserId;
    }

    public String getMebEparchyCode() throws Exception
    {
        if (StringUtils.isBlank(mebEparchyCode))
            mebEparchyCode = getData().getString("MEB_EPARCHY_CODE", "");
        return mebEparchyCode;
    }

    public String getMemAcctId() throws Exception
    {
        if (StringUtils.isBlank(memAcctId))
            memAcctId = getData().getString("MEM_ACCT_ID", "");
        return memAcctId;
    }

    public String getMemUserId() throws Exception
    {
        if (StringUtils.isBlank(memUserId))
            memUserId = getData().getString("MEM_USER_ID", "");
        return memUserId;
    }

    public abstract String getPamRemark();

    public void initial(IRequestCycle cycle) throws Exception
    {
        // 获取客户信息
        IData indata = getData();
        IData grpcustinfo = UCAInfoIntfViewUtil.qryGrpCustInfoByGrpCustId(this, indata.getString("CUST_ID", ""));
        setGrpCustInfo(grpcustinfo);

        // 获取成员用户信息
        IData mebuserinfo = UCAInfoIntfViewUtil.qryMebUserInfoByUserIdAndRoute(this, indata.getString("MEB_USER_ID", ""), indata.getString("MEB_EPARCHY_CODE"));
        setMebUserInfo(mebuserinfo);

        // 设置产品控制信息
        String productCtrlInfostr = getData().getString("PRODUCT_CTRL_INFO");
        IData productCtrlInfo = new DataMap();
        if (StringUtils.isEmpty(productCtrlInfostr))
        {
            productCtrlInfo = AttrBizInfoIntfViewUtil.qryNormalProductCtrlInfoByGrpProductIdAndBusiType(this, getGrpProductId(), BizCtrlType.CreateUnifiedBill);
        }
        else
        {
            productCtrlInfo = new DataMap(productCtrlInfostr);
        }
        // 费用列表未处理
        // IDataset feeList = TradeFeeMgr.getOperFee(pd, new DataMap());
        // getTradeData().setFeeList(feeList);
        // 分散修改
        IData cond = new DataMap();
        String acctDay = getData().getString("USER_ACCTDAY_ACCT_DAY", "");
        String firstDate = getData().getString("USER_ACCTDAY_FIRST_DATE", "");
        String nextAcctDay = getData().getString("USER_ACCTDAY_NEXT_ACCT_DAY", "");
        String nextFirstDate = getData().getString("USER_ACCTDAY_NEXT_FIRST_DATE", "");
        cond.put("ACCT_DAY", acctDay);
        cond.put("FIRST_DATE", firstDate);
        cond.put("NEXT_ACCT_DAY", nextAcctDay);
        cond.put("NEXT_FIRST_DATE", nextFirstDate);

        String bookingDate = getData().getString("bookingDate", "");
        cond.put("PRODUCT_PRE_DATE", bookingDate);
        setCondition(cond);
        // 分散修改 end
        // 产品包、元素信息
        IData packElement = new DataMap();
        packElement.put("ELEMENT_TYPE_CODE", "D");
        packElement.put("MODIFY_TAG", "0");
        packElement.put("ELEMENT_ID", "50009032");
        packElement.put("ELEMENT_NAME", "无线商话.商信通减免优惠");
        packElement.put("PACKAGE_ID", "-1");
        packElement.put("PRODUCT_ID", "-1");
        packElement.put("PRODUCT_MODE", "");

        IDataset packElements = new DatasetList();
        packElements.add(packElement);
        this.setInfo(packElements);

        IData memberPackElements = new DataMap();// 成员产品元素
        GroupBaseView.processProductElements(this, packElements, memberPackElements);

        setMemberProduct(memberPackElements);

        // 费用信息
        String tradeTypeCode = productCtrlInfo.getData("TradeTypeCode").getString("ATTR_VALUE");
        IDataset feeList = super.initElementDefaultFee(productId, tradeTypeCode, packElements);

        setGrpFeeList(feeList.toString());
    }

    public abstract void setCondition(IData condition);

    public abstract void setGrpCustInfo(IData grpCustInfo);

    public abstract void setGrpFeeList(String feeList);

    public abstract void setInfo(IDataset info);

    public abstract void setMebUserInfo(IData mebUserInfo);

    // 成员附加产品
    public abstract void setMemberProduct(IData memberProduct);

}
