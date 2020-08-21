
package com.asiainfo.veris.crm.order.web.frame.csview.group.destroyvpmngroupmember;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.custinfo.custgroupmember.CustGroupMemberIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.multitableinfo.UserProductElementInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.userplatsvcinfo.UserPlatSvcInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;

public abstract class PreView extends GroupBasePage
{

    // 修改了一下原有的逻辑，如果成员资料不归属任何集团则不在页面上显示是否退出集团
    public IData getGrpMemberExisInfo(String productId, String serialNumber, String mebEparchCode) throws Exception
    {

        IData info = new DataMap();
        if (!productId.equals("8000") && !productId.equals("6200"))
        {
            info.put("IFJOIN", "false");
            return info;
        }
        // 8000 和 6200的产品
        IDataset grpMemberInfo = CustGroupMemberIntfViewUtil.qryGrpMebsBySN(this, serialNumber, mebEparchCode);
        if (IDataUtil.isNotEmpty(grpMemberInfo))
        {
            info.put("IFJOIN", "true");
            info.put("JOINVALUE", "1");
            info.put("IFDISABLED", "false");
            info.put("TITLENAME", "退出相应集团");
            // 8000的需要增加权限判断,不允许选择退订
            if (productId.equals("8000") && !StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "GROUPMENBER_VPMN_PRV"))
            {
                info.put("IFDISABLED", "true");
                info.put("TITLENAME", "退出相应集团（工号无成员退出集团资料的权限GROUPMENBER_VPMN_PRV）");
            }

        }
        else
        {
            info.put("IFJOIN", "false");
        }
        return info;
    }

    /*
     * @descripiton 获取成员在该集团下的所有元素信息
     * @author xunyl
     * @date 2013-08-07
     */
    protected void getMebElements(IDataset userdestroyelments, String memUserId, String grpUserId, String productId, String eparchycode) throws Throwable
    {
        IDataset useproductelmemets = UserProductElementInfoIntfViewUtil.qryUserElementInfosByUserIdAndUserIdA(this, memUserId, grpUserId, eparchycode);

        IDataset spElements = UserPlatSvcInfoIntfViewUtil.qryMebPlatSvcInfosByUserIdAndGrpProductId(this, memUserId, productId, eparchycode);
        if (IDataUtil.isNotEmpty(spElements))
        {
            for (int j = 0; j < spElements.size(); j++)
            {
                IData map = spElements.getData(j);
                map.put("ELEMENT_TYPE_NAME", "SP服务");
                map.put("STATE", "DEL");
            }
        }
        userdestroyelments.addAll(useproductelmemets);
        userdestroyelments.addAll(spElements);
    }

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
        // 查询集团客户信息
        setGrpCustInfo(UCAInfoIntfViewUtil.qryGrpCustInfoByGrpCustId(this, baseinfo.getString("CUST_ID")));
        // 查询成员用户信息
        setMebUserInfo(UCAInfoIntfViewUtil.qryMebUserInfoByUserIdAndRoute(this, baseinfo.getString("MEB_USER_ID", ""), eparchycode));

        String memUserId = baseinfo.getString("MEB_USER_ID");
        String grpUserId = baseinfo.getString("GRP_USER_ID");
        String productId = baseinfo.getString("GRP_PRODUCT_ID");
        String mebSn = baseinfo.getString("cond_SERIAL_NUMBER");
        IDataset userdestroyelments = new DatasetList();
        // 根据产品编号获取产品的品牌信息
        getMebElements(userdestroyelments, memUserId, grpUserId, productId, eparchycode);

        setUseElementinfos(userdestroyelments);

        // 是否可以退出集团资料
        setInfo(getGrpMemberExisInfo(productId, mebSn, eparchycode));
        // 生成支付组件需要的数据
        IDataset dataset = new DatasetList();
        IData data = new DataMap();
        data.put("PAY_MONEY_CODE", "0");
        data.put("MONEY", getFeeTotal());
        dataset.add(data);
    }

    public abstract void setGrpCustInfo(IData info);

    public abstract void setInfo(IData info);

    public abstract void setMebUserInfo(IData info);

    public abstract void setUseElementinfos(IDataset useElementinfos);
}
