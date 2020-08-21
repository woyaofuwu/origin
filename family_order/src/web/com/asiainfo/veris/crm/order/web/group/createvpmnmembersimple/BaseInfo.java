
package com.asiainfo.veris.crm.order.web.group.createvpmnmembersimple;

import java.util.Iterator;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.VpmnUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.attrbiz.AttrBizInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.staff.staffbbossinfo.StaffGrpRightInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.util.GroupProductUtilView;

public abstract class BaseInfo extends GroupBasePage
{
    private String grpUserId;

    private String shortCode;

    private IDataset mebPlusProducts;

    private IDataset packages;

    /**
     * 根据集团产品编码查询集团用户信息; 集团产品编码输入框回车触发
     * 
     * @param cycle
     * @throws Throwable
     */
    public void getGroupInfoByGPId(IRequestCycle cycle) throws Throwable
    {
        String grpSn = getData().getString("cond_GROUP_SERIAL_NUMBER", ""); // 集团服务号

        IData result = UCAInfoIntfViewUtil.qryGrpUCAInfoByGrpSn(this, grpSn, true);

        IData custInfo = result.getData("GRP_CUST_INFO");
        IData userInfo = result.getData("GRP_USER_INFO");
        grpUserId = userInfo.getString("USER_ID");
        String productId = userInfo.getString("PRODUCT_ID");
        // 检查工号是否有操作改VPMN的权限 qiand
        String staffId = getVisit().getStaffId();
        String rightCode = "CREATE_VPN_MEMBER";
        // IData params = new DataMap();
        // params.put("USER_PRODUCT_CODE", grpSn);
        // params.put("RIGHT_CODE", rightCode);
        // IDataset idataset = CSViewCall.call(this, "CS.StaffInfoQrySVC.queryGrpRightByIdCode", params);
        IDataset idataset = StaffGrpRightInfoIntfViewUtil.qryGrpRightInfosByStaffIdRightCodeUserProductCode(this, null, rightCode, grpSn);
        if (IDataUtil.isNotEmpty(idataset))
        {
            IDataset codeRight = DataHelper.filter(idataset, "STAFF_ID=" + staffId);
            if (IDataUtil.isEmpty(codeRight))
            {
                // j2ee "您无权办理集团"+ grpsn + "的业务！"
            }
        }
        vpnNumdeal(cycle);
        // j2ee 判断产品类型存在 ，貌似没必要
        // j2ee IData inparam = new DataMap();
        // j2ee inparam.put("PRODUCT_ID", userInfo.getString("PRODUCT_ID"));
        // j2ee IDataset productRelaInfo = ProductInfoQry.getProductTypeRelaByID(pd, inparam);
        // j2ee if (productRelaInfo == null || productRelaInfo.size() == 0)
        // j2ee common.error("该产品对应的产品类型找不到，请确认！");

        // pd.setTransfer("PRODUCT_ID", userInfo.getString("PRODUCT_ID"));

        // 获取产品控制信息
        IData productCtrlInfo = AttrBizInfoIntfViewUtil.qryCrtMbProductCtrlInfoByProductId(this, productId);
        setProductCtrlInfo(productCtrlInfo);

        IData data = getData();
        data.put("cond_GROUP_PRODUCT", grpSn);
        // start add by wangyf6 at 20120703
        String tempGroupProduct = getData().getString("temp_GROUP_PRODUCT", "");
        if (StringUtils.isBlank(tempGroupProduct))
        {
            tempGroupProduct = "OK";
        }
        data.put("temp_GROUP_PRODUCT", tempGroupProduct);
        // end add by wangyf6 at 20120703
        setCond(data);

        if (staffId != null && staffId.startsWith("HNYD"))
        {
            // j2ee getTradeData().setInModeCode("1");
        }
        String ip = getVisit().getRemoteAddr();

        if (ip != null && ip.startsWith("10"))
        {
            // j2ee getTradeData().put("IF_GROUPINFOVIEW", "false");
        }
        else
        {
            // j2ee getTradeData().put("IF_GROUPINFOVIEW", "true");
        }
    }

    /**
     * 获取产品ID
     * 
     * @return
     * @throws Exception
     */
    public String getGrpProductId() throws Exception
    {

        return this.getData().getString("GRP_PRODUCT_ID", "");

    }

    public void initial(IRequestCycle cycle) throws Throwable
    {
        // j2ee PageData pd = getPageData();
        // j2ee setSelectedElements(null);

        if (getVisit().getStaffId() != null && getVisit().getStaffId().startsWith("HNYD"))
        {
            // j2ee getTradeData().setInModeCode("1");
        }
        // j2ee String ip = getIpAddr(pd.getRequest());

        // j2ee if (ip != null && ip.startsWith("10"))
        {
            // j2ee getTradeData().put("IF_GROUPINFOVIEW", "false");
        }
        // j2ee else
        {
            // j2ee getTradeData().put("IF_GROUPINFOVIEW", "true");
        }
        // j2ee pd.setTransfer("tradeData", getTradeData().toString());
        // j2ee pd.setParameter("SELECTED_ELEMENTS", "");
    }

    public void initProductInfo(IRequestCycle cycle) throws Throwable
    {
        IData inparam = getData();
        // 集团产品用户标识USER_ID
        String grpuserId = inparam.getString("GRP_USER_ID", "");
        String mebuserId = inparam.getString("MEB_USER_ID", "");
        String mebcustId = inparam.getString("MEB_CUST_ID", "");
        productId = "8000";

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

        // setDynParam(dynparam);

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

        String bookingDate = getData().getString("BOOKING_DATE");

        if (StringUtils.isNotBlank(bookingDate) && !"{}".equals(bookingDate))
        {
            cond.put("PRODUCT_PRE_DATE", bookingDate);
        }

        setCond(cond);

        // 产品组件页面包列表参数信息
        IData pkgParam = new DataMap();

        pkgParam.put("PRODUCT_ID", productId);
        pkgParam.put("EPARCHY_CODE", mebeparchycode);
        pkgParam.put(Route.USER_EPARCHY_CODE, mebeparchycode); // 初始服务的路由
        pkgParam.put("GRP_USER_ID", grpuserId); // 定制产品时需要查询集团给成员定制的元素
        setPkgParam(pkgParam);

        setRoleList(GroupProductUtilView.qryRoleListByProductId(this, productId));
        IData resultInfo = new DataMap();
        resultInfo.put("DYN_PARAM", dynparam); // 动态产品参数
        resultInfo.put("COND", cond); // 已选产品元素
        resultInfo.put("PKG_PARAM", pkgParam); // 待选区 产品包
        setAjax(resultInfo);
    }

    public void onInitTrade(IRequestCycle cycle) throws Exception
    {
        // j2ee PageData pd = getPageData();
        // j2ee setSelectedElements(null);

        // j2ee if (pd.getContext().getStaffId() != null && pd.getContext().getStaffId().startsWith("HNYD"))
        {
            // j2ee getTradeData().setInModeCode("1");
        }
        // j2ee String ip = getIpAddr(pd.getRequest());

        // j2ee if (ip != null && ip.startsWith("10"))
        {
            // j2ee getTradeData().put("IF_GROUPINFOVIEW", "false");
        }
        // j2ee else
        {
            // j2ee getTradeData().put("IF_GROUPINFOVIEW", "true");
        }
        // j2ee pd.setTransfer("tradeData", getTradeData().toString());
        // j2ee pd.setParameter("SELECTED_ELEMENTS", "");
    }

    /**
     * 提交方法
     * 
     * @param cycle
     * @throws Exception
     */
    public void onSubmitBaseTrade(IRequestCycle cycle) throws Exception
    {
        IData inparam = new DataMap();

        inparam.put("USER_ID", getData().getString("GRP_USER_ID", ""));
        inparam.put("SERIAL_NUMBER", getData().getString("MEB_SERIAL_NUMBER", ""));
        inparam.put("MEM_ROLE_B", getData().getString("ROLE_CODE_B", ""));
        inparam.put("REMARK", getData().getString("parm_REMARK"));

        String bookingDate = getData().getString("BOOKING_DATE"); // 业务预约时间
        if (StringUtils.isNotEmpty(bookingDate) && !"{}".equals(bookingDate))
        {
            inparam.put("PRODUCT_PRE_DATE", bookingDate);
        }

        String checkModeString = getData().getString("cond_CHECK_MODE");
        if (StringUtils.isNotEmpty(checkModeString))
        {
            inparam.put("CHECK_MODE", checkModeString);
        }

        String effectNow = getData().getString("EFFECT_NOW");// 产品资费立即生效标志 true立即生效
        if (StringUtils.isNotEmpty(effectNow))
        {
            inparam.put("EFFECT_NOW", "true".equals(effectNow) ? "true" : "false");
        }

        IDataset productElements = saveProductElemensFrontData();
        shortCode = null;
        IDataset productParam = saveProductParamInfoFrontData();
        IDataset resinfos = saveResInfoFrontData();
        inparam.put("ELEMENT_INFO", productElements);
        if (IDataUtil.isNotEmpty(resinfos))
            inparam.put("RES_INFO", resinfos);

        if (IDataUtil.isNotEmpty(productParam))
        {
            inparam.put("PRODUCT_PARAM_INFO", productParam);

        }
        if (!StringUtils.isEmpty(shortCode))
        {
            inparam.put("SHORT_CODE", shortCode);
        }
        inparam.put("PLAN_TYPE_CODE", savePayPlanFrontData());

        // 费用信息
        inparam.put("X_TRADE_FEESUB", getData().getString("X_TRADE_FEESUB"));
        inparam.put("X_TRADE_PAYMONEY", getData().getString("X_TRADE_PAYMONEY"));

        // 发展人信息
        inparam.put("DEVELOP_STAFF_ID", getData().getString("DEVELOP_STAFF_ID"));

        IDataset result = CSViewCall.call(this, "CS.CreateGroupMemberSvc.createGroupMember", inparam);
        setAjax(result);
    }

    public void qryGrpInfoByMebSn(IRequestCycle cycle) throws Throwable
    {
        IData result = new DataMap();
        boolean flag = false;
        // j2ee super.queryMemberInfo(cycle);
        IData grpInfo = UCAInfoIntfViewUtil.qryMebOrderedGroupInfosBySnAndRela(this, getData().getString("cond_MEM_SERIAL_NUMBER", ""), "20", false);

        if (IDataUtil.isEmpty(grpInfo))
        {
            // modify by lixiuyu@20110825 用户要求把"该号码不属于任何集团"修改成"该集团V网限制办理，请和集团客户经理联系！"
            CSViewException.apperr(VpmnUserException.VPMN_USER_33);

        }

        IDataset orderInfos = new DatasetList(grpInfo.getString("ORDERED_GROUPINFOS", "[]"));
        IData orderInfo = orderInfos.getData(0);
        String grpSn = orderInfo.getString("SERIAL_NUMBER");
        String ip = this.getVisit().getRemoteAddr();// getIpAddr(pd.getRequest());

        if (ip != null && ip.startsWith("10"))
        {
            result.put("IF_GROUPINFOVIEW", "false");
        }
        else
        {
            result.put("IF_GROUPINFOVIEW", "true");
        }
        flag = true;
        result.put("GRP_SN", grpSn);
        result.put("RESULT", flag);
        setAjax(result);
    }

    public String savePayPlanFrontData() throws Exception
    {
        return this.getData().getString("PAY_PLAN_SEL_PLAN_TYPE", "P");
    }

    /**
     * 获取集团产品元素信息
     * 
     * @return
     * @throws Exception
     */
    public IDataset saveProductElemensFrontData() throws Exception
    {

        String selectElementStr = getData().getString("SELECTED_ELEMENTS", "[]");

        IDataset selectElements = new DatasetList(selectElementStr);
        return selectElements;
    }

    public IDataset saveProductParamInfoFrontData() throws Exception
    {

        IDataset resultset = new DatasetList();
        IData result = new DataMap();
        IDataset productParamAttrset = new DatasetList();
        IData productParam = getData("pam", true);
        if (IDataUtil.isEmpty(productParam))
            return null;
        Iterator<String> iterator = productParam.keySet().iterator();
        while (iterator.hasNext())
        {
            IData productParamAttr = new DataMap();
            String key = iterator.next();
            Object value = productParam.get(key);
            productParamAttr.put("ATTR_CODE", key);
            productParamAttr.put("ATTR_VALUE", value);
            if ("SHORT_CODE".equals(key))
            {
                shortCode = (String) value;
            }
            productParamAttrset.add(productParamAttr);

        }

        result.put("PRODUCT_ID", getGrpProductId());
        result.put("PRODUCT_PARAM", productParamAttrset);
        resultset.add(result);
        return resultset;
    }

    /**
     * 获取资源信息
     * 
     * @return
     * @throws Exception
     */
    public IDataset saveResInfoFrontData() throws Exception
    {

        String resInfoStr = getData().getString("DYNATABLE_RES_RECORD", "[]");

        IDataset resinfos = new DatasetList(resInfoStr);
        return resinfos;
    }

    public abstract void setCond(IData info);

    public abstract void setDynParam(IData dynparam);

    public abstract void setInfo(IData info);

    public void setMebPlusProducts(IDataset mebPlusProducts)
    {
        this.mebPlusProducts = mebPlusProducts;
    }

    public void setPackages(IDataset packages)
    {
        this.packages = packages;
    }

    public abstract void setPkgParam(IData pkgparam);

    public abstract void setProductCtrlInfo(IData productCtrlInfo);

    public abstract void setResList(IDataset idata);

    public abstract void setRoleList(IDataset roleList);

    public void vpnNumdeal(IRequestCycle cycle) throws Exception
    {
        // 查询用户VPN信息
        IData userVpnData = UCAInfoIntfViewUtil.qryUserVpnInfoByUserId(this, grpUserId);

        int maxUsers = userVpnData.getInt("MAX_USERS");

        IData info = new DataMap();
        info.put("USER_ID_A", grpUserId);
        info.put("RELATION_TYPE_CODE", "20");
        info.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());
        // modify by lixiuyu@20120208
        IDataset vpnRela = CSViewCall.call(this, "CS.RelaUUInfoQrySVC.getRelaCoutByPK", info);
        int vpnAllNum = 0;
        if (IDataUtil.isNotEmpty(vpnRela))
            vpnAllNum = vpnRela.getData(0).getInt("RECORDCOUNT");
        // IDataset vpnRela = UserInfoQry.getRelaUUInfoByUserIda(pd,info,pd.getPagination());
        // int vpnAllNum = vpnRela.size();
        if (vpnAllNum + 1 > maxUsers)
        {
            // j2ee common.warn("此集团用户数已达到最大用户数，不能再新增用户，请注销部分用户或修改此集团最大用户数后办理成员新增业务！");
        }
    }
}
