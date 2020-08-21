
package com.asiainfo.veris.crm.order.web.frame.csview.group.destroygroupunifiedbill;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.attrbiz.AttrBizInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBaseView;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.util.GroupProductUtilView;

public abstract class PreView extends GroupBasePage
{

    public abstract String getPamRemark();

    /**
     * 用于组织集团成员业务退定预览界面数据
     * 
     * @author zouli
     * @throws Throwable
     */
    public void initial(IRequestCycle cycle) throws Throwable
    {
        IData baseinfo = getData();
        String eparchycode = baseinfo.getString("MEB_EPARCHY_CODE");
        String productId = baseinfo.getString("PRODUCT_ID");

        // 获取产品控制信息
        AttrBizInfoIntfViewUtil.qryNormalProductCtrlInfoByGrpProductIdAndBusiType(this, productId, BizCtrlType.DestroyUnifiedBill);
        // 查询集团客户信息
        setGrpCustInfo(UCAInfoIntfViewUtil.qryGrpCustInfoByGrpCustId(this, baseinfo.getString("CUST_ID", "")));
        // 查询成员用户信息
        setMebUserInfo(UCAInfoIntfViewUtil.qryMebUserInfoByUserIdAndRoute(this, baseinfo.getString("MEB_USER_ID", ""), eparchycode));

        IDataset userdestroyelments = new DatasetList();
        IData packElement = new DataMap();
        packElement.put("ELEMENT_TYPE_CODE", "D");
        packElement.put("ELEMENT_TYPE_NAME", "优惠");
        packElement.put("MODIFY_TAG", "1");
        packElement.put("ELEMENT_ID", "50009032");
        packElement.put("ELEMENT_NAME", "无线商话.商信通减免优惠");
        userdestroyelments.add(packElement);

        IData memberPackElements = new DataMap();// 成员产品元素
        GroupBaseView.processProductElements(this, userdestroyelments, memberPackElements);

        setUseElementinfo(memberPackElements);
        // 判断集团用户是否处于升级状态
        String userId = baseinfo.getString("GRP_USER_ID");
        GroupProductUtilView.judgeGroupUserCutState(this, userId);
    }

    public abstract void setGrpCustInfo(IData info);

    public abstract void setInfo(IData info);

    public abstract void setMebUserInfo(IData info);

    public abstract void setUseElementinfo(IData useElementinfo);

    public abstract void setUseElementinfos(IDataset useElementinfos);
}
