
package com.asiainfo.veris.crm.order.web.frame.csview.group.createvpmngroupmember;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.attrbiz.AttrBizInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.product.productcompinfo.ProductCompInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;

public abstract class ProductInfo extends GroupBasePage
{
    public void initial(IRequestCycle cycle) throws Exception
    {

        IData inparam = getData();
        // 集团产品用户标识USER_ID
        String grpuserId = inparam.getString("GRP_USER_ID", "");
        String mebuserId = inparam.getString("MEB_USER_ID", "");
        String mebcustId = inparam.getString("MEB_CUST_ID", "");
        productId = inparam.getString("GRP_PRODUCT_ID", "");

        // 获取产品控制信息
        IData productCtrlInfo = AttrBizInfoIntfViewUtil.qryCrtMbProductCtrlInfoByProductId(this, productId);
        setProductCtrlInfo(productCtrlInfo);

        // 产品参数页面的条件参数值
        String mebeparchycode = getData().getString("MEB_EPARCHY_CODE", "");
        String grpid = inparam.getString("GROUP_ID", "");

        IData dynparam = new DataMap();
        dynparam.put("PRODUCT_ID", productId);
        dynparam.put("GRP_USER_ID", grpuserId);
        dynparam.put("MEB_USER_ID", mebuserId);
        dynparam.put("MEB_EPARCHY_CODE", mebeparchycode);
        dynparam.put("MEB_SERIAL_NUMBER", getData().getString("MEB_SERIAL_NUMBER", ""));
        dynparam.put("CUST_ID", getData().getString("CUST_ID", ""));
        dynparam.put("GROUP_ID", grpid);
        dynparam.put("MEB_CUST_ID", mebcustId); // 成员cust_id

        setDynParam(dynparam);

        // 产品组件页面已选区参数信息
        IData cond = new DataMap();

        cond.put("PRODUCT_ID", productId);
        cond.put("EPARCHY_CODE", mebeparchycode);
        cond.put(Route.ROUTE_EPARCHY_CODE, mebeparchycode); // 初始服务的路由
        cond.put("TRADE_TYPE_CODE", productCtrlInfo.getData("TradeTypeCode").getString("ATTR_VALUE"));// 传递业务类型后才做产品互斥规则验证
        cond.put("EFFECT_NOW", true);// 新增是默认为立即生效
        cond.put("USER_ID", mebuserId); // 规则验证时需要使用用户ID

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
        setCond(cond);

        // 产品组件页面包列表参数信息
        IData pkgParam = new DataMap();

        pkgParam.put("PRODUCT_ID", productId);
        pkgParam.put("EPARCHY_CODE", mebeparchycode);
        pkgParam.put(Route.USER_EPARCHY_CODE, mebeparchycode); // 初始服务的路由
        pkgParam.put("GRP_USER_ID", grpuserId); // 定制产品时需要查询集团给成员定制的元素
        setPkgParam(pkgParam);
        initResList(cycle);
        initRoleList(cycle);

    }

    public void initResList(IRequestCycle cycle) throws Exception
    {

        IData idata = new DataMap();
        IDataset dataList = new DatasetList();
        idata.put("USER_ID", getData().getString("MEB_USER_ID", ""));
        idata.put("USER_ID_A", getData().getString("GRP_USER_ID", ""));
        idata.put(Route.ROUTE_EPARCHY_CODE, getData().getString("MEB_EPARCHY_CODE", ""));
        // String eparchyCode = getTradeData().getMemUserInfo().getString("EPARCHY_CODE", "");
        IDataset resList = CSViewCall.call(this, "CS.UserResInfoQrySVC.getUserResByUserIdA", idata);
        orgainizeResData(resList, dataList);
        setResList(dataList);
    }

    public void initRoleList(IRequestCycle cycle) throws Exception
    {
        String relationTypeCode = ProductCompInfoIntfViewUtil.qryRelationTypeCodeStrByProductId(this, productId);
        // IDataset roleList = StaticUtil.getStaticList("TD_S_RELATION_ROLE_1_" + relationTypeCode);
        IData param = new DataMap();
        param.put("RELATION_TYPE_CODE", relationTypeCode);
        IDataset roleList = CSViewCall.call(this, "CS.StaticInfoQrySVC.getRoleCodeList", param);

        if (IDataUtil.isNotEmpty(roleList))
        {
            // filterList = DataHelper.filter(roleList, "ROLE_CODE_A=0");
            if ("6100".equals(productId))
            {
                IDataset filterList = DataHelper.filter(roleList, "ROLE_CODE_B=1");
                filterList.addAll(DataHelper.filter(roleList, "ROLE_CODE_B=8"));
                roleList.clear();
                roleList.addAll(filterList);
            }
            else if ("9048".equals(productId))
            {
                roleList.clear();
                IData roleData = new DataMap();
                roleData.put("ROLE_CODE_B", "2");
                roleData.put("ROLE_B", "商户管家终端");
                roleList.add(roleData);
            }

        }
        setRoleList(roleList);
    }

    public void orgainizeResData(IDataset resList, IDataset dataList) throws Exception
    {

        if (resList.size() > 0)
        {
            for (int i = 0; i < resList.size(); i++)
            {
                IData param = new DataMap();
                param.put("RES_TYPE_CODE", resList.get(i, "RES_TYPE_CODE"));
                param.put("RES_CODE", resList.get(i, "RES_CODE"));
                param.put("STATE", "EXIST");
                param.put("CHECKED", "true");
                param.put("DISABLED", "true");
                dataList.add(param);
            }
        }
    }

    public abstract void setCond(IData info);

    public abstract void setDynParam(IData dynparam);

    public abstract void setInfo(IData info);

    public abstract void setPkgParam(IData pkgparam);

    public abstract void setProductCtrlInfo(IData productCtrlInfo);

    public abstract void setResList(IDataset idata);

    public abstract void setRoleList(IDataset roleList);

}
