
package com.asiainfo.veris.crm.order.web.group.destroyvpnmember;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.VpmnUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.custinfo.custgroupmember.CustGroupMemberIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.multitableinfo.UserProductElementInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.relationuuinfo.RelationUUInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.userplatsvcinfo.UserPlatSvcInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;

public abstract class DestroyVpnMember extends GroupBasePage
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
    public void initPreview(IRequestCycle cycle) throws Throwable
    {
        IData baseinfo = getData();
        String eparchycode = baseinfo.getString("MEB_EPARCHY_CODE");
        // // 查询集团客户信息
        // setGrpCustInfo(UCAInfoIntfViewUtil.qryGrpCustInfoByGrpCustId(this, baseinfo.getString("CUST_ID")));
        // // 查询成员用户信息
        // setMebUserInfo(UCAInfoIntfViewUtil.qryMebUserInfoByUserIdAndRoute(this, baseinfo.getString("MEB_USER_ID",
        // ""), eparchycode));

        String memUserId = baseinfo.getString("MEB_USER_ID");
        String grpUserId = baseinfo.getString("GRP_USER_ID");
        String productId = baseinfo.getString("GRP_PRODUCT_ID");
        String mebSn = baseinfo.getString("cond_SERIAL_NUMBER");
        IDataset userdestroyelments = new DatasetList();
        this.getMebElements(userdestroyelments, memUserId, grpUserId, productId, eparchycode);

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

    public void onInitTrade(IRequestCycle cycle) throws Exception
    {
        // if (pd.getContext().getStaffId() != null && pd.getContext().getStaffId().startsWith("HNYD"))
        // {
        // getTradeData().setInModeCode("1");
        // }
        // String ip = getIpAddr(pd.getRequest());
        //
        // if (ip != null && ip.startsWith("10"))
        // {
        // getTradeData().put("IF_GROUPINFOVIEW", "false");
        // }
        // else
        // {
        // getTradeData().put("IF_GROUPINFOVIEW", "true");
        // }
        // pd.setTransfer("tradeData", getTradeData().toString());
    }

    /**
     * submit
     * 
     * @param cycle
     * @throws Exception
     */
    public void onSubmitBaseTrade(IRequestCycle cycle) throws Exception
    {
        IData data = getData();

        IData input = new DataMap();
        input.put("USER_ID", data.getString("GRP_USER_ID"));
        input.put("REMARK", data.getString("parm_REMARK"));
        input.put("SERIAL_NUMBER", data.getString("cond_SERIAL_NUMBER"));

        // 业务是否预约 true 预约到账期末执行 false 非预约工单
        String ifBooking = data.getString("ifBooking");
        if (StringUtils.isNotBlank(ifBooking))
            input.put("IF_BOOKING", ifBooking);

        IDataset result = CSViewCall.call(this, "CS.DestroyGroupMemberSvc.destroyGroupMember", input);
        setAjax(result);
    }

    /**
     * 成员用户客户信息和集团用户信息查询
     * 
     * @throws Exception
     */
    public void queryMemberInfo(IRequestCycle cycle) throws Throwable
    {
        IData resultInfo = new DataMap();

        // 查询成员用户信息
        // String strMebSn = getData().getString("cond_SERIAL_NUMBER");
        // if (StringUtils.isEmpty(strMebSn))
        // return;
        // resultInfo = UCAInfoIntfViewUtil.qryMebOrderedGroupInfosBySnAndRela(this, strMebSn, "20");
        String serial_number = getData().getString("cond_SERIAL_NUMBER");
        // 获取用户信息
        IData parem = new DataMap();
        parem.put("SERIAL_NUMBER", serial_number);
        parem.put("REMOVE_TAG", "0");
        parem.put("NET_TYPE_CODE", "00");
        IData userinfo = UCAInfoIntfViewUtil.qryMebUserInfoBySn(this, serial_number, true);

        // 获取用户与用户关系信息
        IDataset userrelations = RelationUUInfoIntfViewUtil.qryRelaUUInfosByUserIdBAndRelationTypeCode(this, userinfo.getString("USER_ID"), "20", userinfo.getString("EPARCHY_CODE"), false);

        if (IDataUtil.isEmpty(userrelations))
        {
            CSViewException.apperr(VpmnUserException.VPMN_USER_29);
        }
        // 20的不止8000的vpmn产品，所以需要再判断 start
        // userrelations = resultInfo.getDataset("ORDERED_GROUPINFOS");

        IDataset ds8000 = getGrpUserInfosByProductId(userrelations, "8000");
        if (IDataUtil.isEmpty(ds8000))
        {
            CSViewException.apperr(VpmnUserException.VPMN_USER_29);
        }
        IData userrelation = (IData) ds8000.getData(0);
        // 20的不止8000的vpmn产品，所以需要再判断 end
        String grpsn = userrelation.getString("SERIAL_NUMBER_A"); // resultInfo.getDataset("ORDERED_GROUPINFOS").getData(0).getString("SERIAL_NUMBER");
        IData result = UCAInfoIntfViewUtil.qryGrpUCAInfoByGrpSn(this, grpsn);
        IData grpUserInfo = result.getData("GRP_USER_INFO");

        resultInfo.put("GRP_USER_INFO", grpUserInfo);
        resultInfo.put("MEB_USER_INFO", userinfo);
        this.setAjax(resultInfo);

    }

    /**
     * 从一个集团用户信息集里查出指定productId的集合
     * 
     * @param dataset
     * @param productId
     * @return
     * @throws Exception
     */
    public IDataset getGrpUserInfosByProductId(IDataset source, String productId) throws Exception
    {
        IDataset ds = new DatasetList();
        for (int i = 0, cout = source.size(); i < cout; i++)
        {
            IData map = source.getData(i);
            String grpUserId = map.getString("USER_ID_A");
            IData userInfo = UCAInfoIntfViewUtil.qryGrpUserInfoByUserId(this, grpUserId);
            if (IDataUtil.isNotEmpty(userInfo))
            {
                if (productId.equals(userInfo.getString("PRODUCT_ID")))
                {
                    ds.add(map);
                }
            }
        }
        return ds;
    }

    public abstract void setGrpCustInfo(IData info);

    public abstract void setInfo(IData info);

    public abstract void setMebUserInfo(IData info);

    public abstract void setUseElementinfos(IDataset useElementinfos);
}
