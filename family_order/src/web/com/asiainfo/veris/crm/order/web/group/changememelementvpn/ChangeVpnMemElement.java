
package com.asiainfo.veris.crm.order.web.group.changememelementvpn;

import java.util.Iterator;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.VpmnUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.relationuuinfo.RelationUUInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.attrbiz.AttrBizInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.util.GroupProductUtilView;

public abstract class ChangeVpnMemElement extends GroupBasePage
{
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

    public void init(IRequestCycle cycle) throws Exception
    {
        // PageData pd = getPageData();
        // setSelectedElements(null);
        // if (pd.getContext().getStaffId()!=null && pd.getContext().getStaffId().startsWith("HNYD")){
        // getTradeData().setInModeCode("1");
        // }
        // String ip = getIpAddr(pd.getRequest());
        //
        // if (ip!=null && ip.startsWith("10")){
        // getTradeData().put("IF_GROUPINFOVIEW","false");
        // }else{
        // getTradeData().put("IF_GROUPINFOVIEW","true");
        // }
        // pd.setTransfer("tradeData", getTradeData().toString());
        // pd.setParameter("SELECTED_ELEMENTS", "");
    }

    public void initProductInfo(IRequestCycle cycle) throws Throwable
    {
        String grpUserId = getData().getString("GRP_USER_ID", "");
        String grpSn = getData().getString("GRP_USER_ID", "");
        String grpId = getData().getString("GROUP_ID", "");
        String mebuserId = getData().getString("MEB_USER_ID", "");
        String mebsn = getData().getString("MEB_SERIAL_NUMBER", "");
        String mebeparchycode = getData().getString("MEB_EPARCHY_CODE", "");
        productId = getData().getString("GRP_PRODUCT_ID", "");

        // 获取产品控制信息
        IData productCtrlInfo = AttrBizInfoIntfViewUtil.qryChgMbProductCtrlInfoByProductId(this, productId);
        setProductCtrlInfo(productCtrlInfo);

        // 获取成员账期信息
        IData mebUserAcctDayInfo = UCAInfoIntfViewUtil.qryMebUserAcctDayInfoUserId(this, mebuserId, mebeparchycode);

        // 获取产品组件的已选区初始参数
        IData cond = new DataMap();
        cond.put("PRODUCT_ID", productId);
        cond.put("USER_ID", mebuserId);
        cond.put("EPARCHY_CODE", mebeparchycode);
        cond.put("TRADE_TYPE_CODE", productCtrlInfo.getData("TradeTypeCode").getString("ATTR_VALUE"));
        cond.put(Route.ROUTE_EPARCHY_CODE, mebeparchycode);

        String acctDay = mebUserAcctDayInfo.getString("USER_ACCTDAY_ACCT_DAY", "");
        String firstDate = mebUserAcctDayInfo.getString("USER_ACCTDAY_FIRST_DATE", "");
        String nextAcctDay = mebUserAcctDayInfo.getString("USER_ACCTDAY_NEXT_ACCT_DAY", "");
        String nextFirstDate = mebUserAcctDayInfo.getString("USER_ACCTDAY_NEXT_FIRST_DATE", "");
        cond.put("ACCT_DAY", acctDay);
        cond.put("FIRST_DATE", firstDate);
        cond.put("NEXT_ACCT_DAY", nextAcctDay);
        cond.put("NEXT_FIRST_DATE", nextFirstDate);
        setCond(cond);

        // 获取产品组件的包列表区初始参数
        IData pkgParam = new DataMap();
        pkgParam.put("PRODUCT_ID", productId);
        pkgParam.put("GRP_USER_ID", grpUserId);
        pkgParam.put("EPARCHY_CODE", mebeparchycode);
        pkgParam.put(Route.USER_EPARCHY_CODE, mebeparchycode);
        setPkgParam(pkgParam);

        // 获取产品参数页面的的初始参数
        IData dynParam = new DataMap();
        dynParam.put("PRODUCT_ID", productId);
        dynParam.put("GRP_USER_ID", grpUserId);
        dynParam.put("GRP_SN", grpSn);
        dynParam.put("MEB_USER_ID", mebuserId);
        dynParam.put("MEB_EPARCHY_CODE", mebeparchycode);
        dynParam.put("EPARCHY_CODE", mebeparchycode);
        dynParam.put("MEB_SERIAL_NUMBER", mebsn);
        dynParam.put("GROUP_ID", grpId);
        dynParam.put(Route.ROUTE_EPARCHY_CODE, mebeparchycode);
        setDynParam(dynParam);

        // 展示成员角色信息
        IData info = new DataMap();
        info.put("ROLE_CODE_B", RelationUUInfoIntfViewUtil.qryRelaUURoleCodeBByUserIdBAndUserIdA(this, mebuserId, grpUserId, mebeparchycode));
        setInfo(info);
        setRoleList(GroupProductUtilView.qryRoleListByProductId(this, productId));
        setProductId(productId);
        setResList(GroupProductUtilView.initResList(this, getData().getString("MEB_USER_ID", ""), getData().getString("GRP_USER_ID", ""), getData().getString("MEB_EPARCHY_CODE", "")));
        IData resultInfo = new DataMap();
        resultInfo.put("DYN_PARAM", dynParam);
        resultInfo.put("COND", cond);
        resultInfo.put("PKG_PARAM", pkgParam);
        setAjax(resultInfo);
    }

    public void onInitTrade(IRequestCycle cycle) throws Exception
    {
        // PageData pd = getPageData();
        // setSelectedElements(null);
        //
        // if (pd.getContext().getStaffId()!=null && pd.getContext().getStaffId().startsWith("HNYD")){
        // getTradeData().setInModeCode("1");
        // }
        // String ip = getIpAddr(pd.getRequest());
        //
        // if (ip!=null && ip.startsWith("10")){
        // getTradeData().put("IF_GROUPINFOVIEW","false");
        // }else{
        // getTradeData().put("IF_GROUPINFOVIEW","true");
        // }
        // pd.setTransfer("tradeData", getTradeData().toString());
        // pd.setParameter("SELECTED_ELEMENTS", "");
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
        inparam.put("REMARK", getData().getString("parm_REMARK"));
        inparam.put("MEM_ROLE_B", getData().getString("ROLE_CODE_B", ""));
        inparam.put("PRODUCT_ID", getGrpProductId());
        IDataset productElements = saveProductElemensFrontData();
        IDataset productParam = saveProductParamInfoFrontData();
        IDataset resinfos = saveResInfoFrontData();
        inparam.put("ELEMENT_INFO", productElements);
        inparam.put("RES_INFO", resinfos);

        // 费用信息
        inparam.put("X_TRADE_FEESUB", getData().getString("X_TRADE_FEESUB"));
        inparam.put("X_TRADE_PAYMONEY", getData().getString("X_TRADE_PAYMONEY"));

        if (IDataUtil.isNotEmpty(productParam))
            inparam.put("PRODUCT_PARAM_INFO", productParam);

        IDataset result = CSViewCall.call(this, "CS.ChangeMemElementSvc.changeMemElement", inparam);
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

    public abstract void setCond(IData info);

    public abstract void setDynParam(IData dynParam);

    public abstract void setInfo(IData info);

    public abstract void setPkgParam(IData pkgParam);

    public abstract void setProductCtrlInfo(IData productCtrlInfo);

    public abstract void setResList(IDataset idata);

    public abstract void setRoleList(IDataset roleList);
}
