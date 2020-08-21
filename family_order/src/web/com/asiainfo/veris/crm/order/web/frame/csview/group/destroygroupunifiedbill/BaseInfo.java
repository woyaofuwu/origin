
package com.asiainfo.veris.crm.order.web.frame.csview.group.destroygroupunifiedbill;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.attrbiz.AttrBizInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;

public abstract class BaseInfo extends GroupBasePage
{
    public abstract IData getCompProductInfo();

    /**
     * 作用：根据group_USER_ID查询集团基本信息 默认传入为cond_GROUP_ID
     * 
     * @author hud
     * @param cycle
     * @throws Throwable
     */
    public boolean getGroupByUserId(IRequestCycle cycle) throws Throwable
    {
        String grpUserId = getData().getString("GRP_USER_ID");
        IData data = UCAInfoIntfViewUtil.qryGrpUCAInfoByUserId(this, grpUserId);
        if (IDataUtil.isEmpty(data))
        {
            return false;
        }
        IData userinfo = data.getData("GRP_USER_INFO");
        IData groupinfo = data.getData("GRP_CUST_INFO");

        setGroupInfo(groupinfo);
        setUserInfo(userinfo);
        if (IDataUtil.isNotEmpty(userinfo))
        {
            IData ctrlData = AttrBizInfoIntfViewUtil.qryNormalProductCtrlInfoByGrpProductIdAndBusiType(this, userinfo.getString("PRODUCT_ID"), BizCtrlType.DestroyUnifiedBill);
            setProductCtrlInfo(ctrlData);
        }
        return true;

    }

    /**
     * 集团信息查询
     * 
     * @author tengg
     * @param cycle
     * @throws Throwable
     */
    public void getGroupInfo(IRequestCycle cycle) throws Throwable
    {
        getGroupByUserId(cycle);
    }

    public abstract IData getProductInfo();

    /**
     * 查询个人客户信息
     * 
     * @author zouli
     * @throws Throwable
     * @throws Throwable
     */
    public void queryMemberInfo(IRequestCycle cycle) throws Throwable
    {
        // 查询成员用户信息
        String strMebSn = getData().getString("cond_SERIAL_NUMBER");
        IData mebinfo = UCAInfoIntfViewUtil.qryMebUCAInfoBySn(this, strMebSn);

        if (IDataUtil.isEmpty(mebinfo))
            return;

        IData userinfo = mebinfo.getData("MEB_USER_INFO");// 成员信息列表
        IData custinfo = mebinfo.getData("MEB_CUST_INFO");// 成员客户信息列表
        if (IDataUtil.isEmpty(userinfo))
            userinfo = new DataMap();
        if (IDataUtil.isEmpty(custinfo))
            custinfo = new DataMap();

        String user_id = userinfo.getString("USER_ID");
        String eparchy_code = userinfo.getString("EPARCHY_CODE");

        IData data = new DataMap();
        data.put("USER_ID", user_id);
        data.put("EPARCHY_CODE", eparchy_code);
        data.put(Route.ROUTE_EPARCHY_CODE, eparchy_code);
        data.put("RELATION_TYPE_CODE", "UB");// 查询融合用户集团信息列表
        IDataset groupinfo = new DatasetList();
        String relationType = getData().getString("RELATION_CODE", "1");
        if (relationType.equals("2"))
        {
            groupinfo = CSViewCall.call(this, "CS.GrpInfoQrySVC.queryUBRelaBbGroupInfo", data);
        }
        else
        {
            groupinfo = CSViewCall.call(this, "CS.GrpInfoQrySVC.queryUBRelaGroupInfo", data);
        }
        if (IDataUtil.isEmpty(groupinfo))
        {
            CSViewException.apperr(GrpException.CRM_GRP_269, getParameter("cond_SERIAL_NUMBER"));
        }
        if (groupinfo != null && groupinfo.size() > 0)
        {
            IDataset BBOSS = new DatasetList();
            for (int i = 0; i < groupinfo.size(); i++)
            {
                String brandCode = groupinfo.getData(i).getString("BRAND_CODE", "");
                if ("BOSG".equals(brandCode))
                {
                    BBOSS.add(groupinfo.getData(i));
                }
            }
            if (BBOSS != null && BBOSS.size() > 0)
            {
                // 商品
                IData temp = new DataMap();
                temp.put("PRODUCT_TYPE_CODE", "BBYY");
                IDataset products = CSViewCall.call(this, "CS.ProductInfoQrySVC.getProductsByTypeForGroup", temp);
                for (int x = 0; x < BBOSS.size(); x++)
                {
                    boolean tag = false;
                    for (int y = 0; y < products.size(); y++)
                    {
                        if (products.getData(y).getString("PRODUCT_ID", "").equals(BBOSS.getData(x).getString("PRODUCT_ID")))
                        {
                            tag = true;
                        }
                    }
                    if (!tag)
                    {
                        groupinfo.remove(BBOSS.getData(x));
                    }
                }
            }
        }

        if (IDataUtil.isEmpty(groupinfo))
        {
            CSViewException.apperr(GrpException.CRM_GRP_269, getParameter("cond_SERIAL_NUMBER"));
        }
        data.put("GROUPINFO_NUM", groupinfo.size());
        if (groupinfo.size() == 1)
        {
            getData().put("GRP_USER_ID", groupinfo.getData(0).getString("USER_ID", ""));
            getData().put("PRODUCT_ID", groupinfo.getData(0).getString("PRODUCT_ID", ""));
            if (!getGroupByUserId(cycle))
                return;

        }
        else
        {
            setInfos(groupinfo);
            setHidden("false");
        }
        setMebCustInfo(custinfo);
        setMebUserInfo(userinfo);
        setAjax(data);
    }

    public abstract void setCondition(IData condition);

    public abstract void setGroupInfo(IData groupUserInfo);

    public abstract void setHidden(String hidden);

    public abstract void setInfos(IDataset infos);

    public abstract void setMebCustInfo(IData info);

    public abstract void setMebUserInfo(IData info);

    public abstract void setProductCtrlInfo(IData productCtrlInfo);

    public abstract void setProductInfo(IData productInfo);

    public abstract void setUserInfo(IData userInfo);// 用户信息
}
