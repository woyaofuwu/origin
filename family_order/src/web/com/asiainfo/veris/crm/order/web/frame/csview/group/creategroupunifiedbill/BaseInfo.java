
package com.asiainfo.veris.crm.order.web.frame.csview.group.creategroupunifiedbill;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.ProductException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.attrbiz.AttrBizInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.util.GroupProductUtilView;

public abstract class BaseInfo extends GroupBasePage
{
    /**
     * 作用：根据group_sn查询集团基本信息 默认传入为cond_GROUP_ID
     * 
     * @author hud
     * @param cycle
     * @throws Throwable
     */
    public void getGroupBySN(IRequestCycle cycle) throws Throwable
    {

        String grpSN = getData().getString("cond_GROUP_SERIAL_NUMBER");
        IData data = UCAInfoIntfViewUtil.qryGrpUCAInfoByGrpSn(this, grpSN);
        if (IDataUtil.isEmpty(data))
        {
            return;
        }

        IData groupinfo = data.getData("GRP_CUST_INFO");
        setInfo(groupinfo);
        IData userinfo = data.getData("GRP_USER_INFO");
        userinfo.put("GRP_USERPAY_TAG", GroupProductUtilView.getCompixAccountTag(this, userinfo.getString("USER_ID")));
        setUserInfo(userinfo);

        // 获取产品信息
        String productId = userinfo.getString("PRODUCT_ID", "");
        setProductInfo(GroupProductUtilView.getProductExplainInfo(this, productId));

        setProductCtrlInfo(AttrBizInfoIntfViewUtil.qryCrtMbProductCtrlInfoByProductId(this, productId));
        IDataset userinfos = new DatasetList();
        userinfo.put("CHECKED", "true");
        userinfos.add(userinfo);
        setUseInfos(userinfos);
    }

    public abstract IData getInfo();

    /**
     * 作用：页面初始化
     * 
     * @author zhujm 2009-03-06
     * @param cycle请求参数
     * @throws Throwable
     */
    public void initial(IRequestCycle cycle) throws Exception
    {

    }

    /**
     * 查询集团订购的产品信息
     * 
     * @param cycle
     * @throws Throwable
     */
    public void queryGrpUserInfos(IRequestCycle cycle) throws Throwable
    {

        String custid = getData().getString("CUST_ID", "");
        IData para = new DataMap();
        para.put("CUST_ID", custid);
        para.put("PRODUCT_ID", getData().getString("PRODUCT_ID"));

        IDataset userinfos = UCAInfoIntfViewUtil.qryGrpUserInfoByCustIdAndProId(this, custid, getData().getString("PRODUCT_ID"), false);
        setUseInfos(userinfos);
        // 查询产品控制信息
        IData ctrlTypeData = AttrBizInfoIntfViewUtil.qryNormalProductCtrlInfoByGrpProductIdAndBusiType(this, getData().getString("PRODUCT_ID"), BizCtrlType.CreateUnifiedBill);
        if (IDataUtil.isEmpty(ctrlTypeData))
        {
            CSViewException.apperr(ProductException.CRM_PRODUCT_52);
        }
        setProductCtrlInfo(ctrlTypeData);
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
        queryGrpUserInfos(cycle);
    }

    public void refreshProductCtrlInfo(IRequestCycle cycle) throws Throwable
    {
        String productId = getData().getString("PRODUCT_ID");

        if (StringUtils.isBlank(productId))
            return;
        String userId = getData().getString("USER_ID");
        if (StringUtils.isBlank(userId))
            return;
        // 查询产品控制信息
        IData ctrlTypeData = AttrBizInfoIntfViewUtil.qryNormalProductCtrlInfoByGrpProductIdAndBusiType(this, getData().getString("PRODUCT_ID"), BizCtrlType.CreateUnifiedBill);

        setProductCtrlInfo(ctrlTypeData);
        setUserInfo(UCAInfoIntfViewUtil.qryGrpUserInfoByUserId(this, userId));
    }

    public abstract void setCompProductInfo(IData compProductInfo);// 组合产品信息

    public abstract void setCondition(IData condition);

    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);

    public abstract void setMemUserInfo(IData memUserInfo);

    public abstract void setProductCtrlInfo(IData productCtrlInfo);

    public abstract void setProductInfo(IData productInfo);// 产品信息

    public abstract void setUseInfos(IDataset useInfos);

    public abstract void setUserInfo(IData userInfo);// 用户信息
}
