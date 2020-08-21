
package com.asiainfo.veris.crm.order.web.group.upgradevpmn;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.VpmnUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.group.common.GroupBaseConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.base.MessageBox;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.attrbiz.AttrBizInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.product.productcompinfo.ProductCompInfoIntfViewUtil;

public abstract class UpgradeVpmnToCntrx extends CSBasePage
{
    public abstract void setInfos(IDataset infos);

    public abstract void setVpmnInfo(IData vpmnInfo);

    public abstract void setCond(IData param);

    public abstract void setUseTag(String use_tag);

    public abstract void setInitSvc(String initSvc);

    public abstract void setProductId(String productId);

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

    public IDataset saveUserGrpPackageInfoFrontData() throws Exception
    {

        String grpPackageInfoStr = getData().getString("SELECTED_GRPPACKAGE_LIST", "[]");

        if ("".equals(grpPackageInfoStr))
        {
            return new DatasetList();
        }

        IDataset grpPackageInfos = new DatasetList(grpPackageInfoStr);
        return grpPackageInfos;
    }

    /**
     * 提交
     * 
     * @param cycle
     * @throws Exception
     */
    public void onSubmitBaseTrade(IRequestCycle cycle) throws Exception
    {
        IData getData = getData();

        // 服务数据
        // 集团变更的必传参数
        IData svcData = new DataMap();
        svcData.put("USER_ID", getData().getString("GRP_USER_ID", null));
        svcData.put("PRODUCT_ID", "8000");
        svcData.put(Route.USER_EPARCHY_CODE, getData().getString("GRP_USER_EPARCHYCODE"));
        IDataset productElements = saveProductElemensFrontData();
        IDataset userGrpPackaeInfos = saveUserGrpPackageInfoFrontData();
        svcData.put("ELEMENT_INFO", productElements); // 集团元素
        svcData.put("GRP_PACKAGE_INFO", userGrpPackaeInfos); // 集团成员定制
        // 成员变更的必传参数
        // inparam.put("USER_ID", getData().getString("GRP_USER_ID", ""));
        svcData.put("SERIAL_NUMBER", getData().getString("MEB_SERIAL_NUMBER", ""));
        svcData.put("MEM_DISCNT_CODE", getData().getString("DISCNT_CODE"));
        // svcData.put("PRODUCT_ID", "8000");

        // 调用服务
        IDataset retDataset = CSViewCall.call(this, "SS.UpgradeVpnBeanSVC.crtBatUptoIms", svcData);

        // 设置返回值
        setAjax(retDataset);
    }

    /**
     * 产品组件初始化
     * 
     * @param cycle
     * @throws Exception
     */
    public void initialProduct(IRequestCycle cycle) throws Exception
    {

        String productId = "8001";
        String grpUserEparchyCode = getData().getString("GRP_USER_EPARCHYCODE");
        setProductId(productId);

        // 获取定制信息
        String useTagString = ProductCompInfoIntfViewUtil.qryUseTagStrByProductId(this, productId);
        setUseTag(GroupBaseConst.GroupDesignFlag.GroupDesignYes.getValue().equals(useTagString) ? "true" : "false");

        // 获取产品控制信息
        IData productCtrlInfo = AttrBizInfoIntfViewUtil.qryCrtUsProductCtrlInfoByProductId(this, productId);
        String groupId = getData().getString("GROUP_ID", "");
        // 获取产品组件的初始条件信息
        IData cond = new DataMap();
        cond.put("EPARCHY_CODE", grpUserEparchyCode);
        cond.put(Route.USER_EPARCHY_CODE, grpUserEparchyCode);
        cond.put(Route.ROUTE_EPARCHY_CODE, grpUserEparchyCode);
        cond.put("TRADE_TYPE_CODE", productCtrlInfo.getData("TradeTypeCode").getString("ATTR_VALUE"));
        cond.put("EFFECT_NOW", true);
        cond.put("PRODUCT_ID", productId);
        setCond(cond);
        String initSvc = "CS.SelectedElementSVC.getGrpUserOpenElements"; // 待选区 产品包 初始服务
        setInitSvc(initSvc);
        IData resultInfo = new DataMap();
        resultInfo.put("PRODUCT_ID", productId);
        resultInfo.put("COND", cond); // 已选产品元素
        resultInfo.put("PKG_PARAM", cond); // 待选区 产品包
        setAjax(resultInfo);

    }

    /**
     * 查询V网VPN资料
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryVpnInfos(IRequestCycle cycle) throws Exception
    {
        String grpProductId = getData().getString("GRP_PRODUCT_ID");
        if (!"8000".equals(grpProductId))
        {
            CSViewException.apperr(VpmnUserException.VPMN_USER_124);
        }
        String grpUserId = getData().getString("GRP_USER_ID");
        String grpCustName = getData().getString("CUST_NAME");
        String memProductId = "22000020"; // 成员vpmn产品id
        String grpSn = getData().getString("GRP_SN");

        // 查询VPMN资料
        IData userVpnInfo = UCAInfoIntfViewUtil.qryUserVpnInfoByUserId(this, grpUserId, false);
        if (IDataUtil.isEmpty(userVpnInfo))
        {
            CSViewException.apperr(VpmnUserException.VPMN_USER_122, userVpnInfo.getString("SERIAL_NUMBER"));
        }
        if ("2".equals(userVpnInfo.getString("VPN_USER_CODE")))
        {// 判断是否为融合V网
            CSViewException.apperr(VpmnUserException.VPMN_USER_123, userVpnInfo.getString("SERIAL_NUMBER"));
        }
        userVpnInfo.put("VPN_NAME", grpCustName);
        userVpnInfo.put("SERIAL_NUMBER", grpSn);
        userVpnInfo.put("BRAND_CODE", grpSn);
        userVpnInfo.put("PRODUCT_NAME", getData().getString("GRP_PRODUCT_NAME"));

        setVpmnInfo(userVpnInfo);

        IData params = new DataMap();
        params.put("PRODUCT_ID", memProductId);
        IDataset memdiscnts = CSViewCall.call(this, "CS.DiscntInfoQrySVC.getDiscntByProduct", params); // TD_B_DISCNT", "SEL_BY_PRODUCT"

        setInfos(memdiscnts); // 成员358优惠 下拉列表
        initialProduct(cycle); // 产品组件初始化
    }

    /**
     * 提交台账
     * 
     * @param cycle
     * @throws Exception
     */
    public void confirm(IRequestCycle cycle) throws Exception
    {
        String routeeparchycocde = getTradeEparchyCode();
        // j2ee tradeData.setRouteEparchyCode(routeeparchycocde);
        // j2ee tradeData.setTradeTypeCode("2528");
        // j2ee pd.setRouteEparchy(routeeparchycocde);
        String ctrlClass = "HAIN.group.upgradeVpn.UpgradeVpnBean";
        // j2ee GroupBaseBean.invoker(ctrlClass, "validchk", new Object[]
        // j2ee { pd, getTradeData(), "BaseInfo", null }, new Class[]
        // j2ee { PageData.class, TradeData.class, String.class, IData.class });

        // j2ee Class groupClass = Class.forName("com.linkage.saleserv.bean.HAIN.group.upgradeVpn.UpgradeVpnBean");
        // j2ee Constructor cons = groupClass.getConstructor(new Class[]
        // j2ee {});
        // j2ee Object object = cons.newInstance(new Object[]
        // j2ee {});

        // j2ee java.lang.reflect.Method method = groupClass.getMethod("tradeReg", new Class[]
        // j2ee { PageData.class, TradeData.class });
        // j2ee IDataset dataset = (IDataset) method.invoke(object, new Object[]
        // j2ee { pd, getTradeData() });
        // j2ee TradeData xd = new TradeData();

        // j2ee IDataset printinfos = getTradeData().getPrintInfo();
        // j2ee if (printinfos != null)
        {
            // j2ee xd.setPrintInfo(printinfos);
        }
        // j2ee xd.setTradeId(getTradeData().getTradeId());
        // j2ee if (dataset == null || dataset.size() <= 0)
        {
            // j2ee pd.setTransfer("tradeData", "[]");
        }
        // j2ee else
        {
            // j2ee pd.setTransfer("tradeData", dataset.toString());
        }

    }

    /**
     * 订单提交后弹出批次号
     * 
     * @param cycle
     * @throws Exception
     */
    public void redirectToMsgBox(IRequestCycle cycle) throws Exception
    {

        // j2ee TradeData td = getTradeData();
        // if (null != td && td.getString("IF_RAISEVPN", "").equals("true"))
        // {
        //
        // String batch_id = getTradeData().getString("BATCH_ID", "");
        // String relation_batch_id = getTradeData().getString("RELATION_BATCH_ID", "");
        // String tmp = "";
        // if (!"".equals(relation_batch_id))
        // {
        // tmp = "集团批次号：" + relation_batch_id + "！</br>";
        // }
        // if (!"".equals(batch_id))
        // {
        // tmp += "集团成员批次号：" + batch_id + "！</br>";
        // }
        // redirectToMsgBox(this.MESSAGE_CONFIRM, "业务受理成功！</br>" + tmp +
        // "<span class='e_strong'>查看批次任务详情请点击【批次查看】！</span>", getBatchBtns(), null);
        // }
        // else
        // {
        // super.redirectToMsgBox(cycle);
        // }

    }

    /**
     * 订单提交后 批次号展示框点击查看批量业务
     * 
     * @return
     * @throws Exception
     */
    public IDataset getBatchBtns() throws Exception
    {
        IDataset defaultBtns = null;// j2ee super.getDefaultBtns();
        // 跳转按钮
        MessageBox.Button btn3 = new MessageBox.Button();
        btn3.setButtonName("批次查看");
        btn3.setCaption("集团批量业务");
        btn3.setFunction("redirectToNav('group.bat.batdeal.GrpBatTradeDeal', 'initalDeal', null, 'contentframe');");
        defaultBtns.add(btn3);

        return defaultBtns;
    }

    /**
     * 集团产品元素加载 j2ee 产品组件来实现
     * 
     * @param cycle
     * @throws Exception
     */
    // public void initialProduct(IRequestCycle cycle) throws Exception
    // {
    // j2ee PageData pd = getPageData();
    // IDataset result = new DatasetList();
    // IDataset memResult = new DatasetList();
    // ProductBean bean = new ProductBean();
    //
    // // 查询选择产品的所有服务的itema配置
    // IData elementparma = new DataMap();
    // elementparma.put("PRODUCT_ID", productId);
    // elementparma.put("ELEMENT_TYPE_CODE", "S");// 表示为服务
    // elementparma.put("ATTR_TYPE_CODE", "9");// 表示为弹出窗口 显示元素参数
    // IDataset productsvcItema = AttrItemInfoQry.getelementItemaByProductId(pd, elementparma);
    //
    // IData productInfo = ProductUtil.getProductInfo(pd, productId);
    //
    // IData data = new DataMap();
    // data.put("USER_ID", userId);
    // data.put("PRODUCT_ID", productId);
    // data.put("PRODUCT_MODE", productInfo.getString("PRODUCT_MODE"));
    // data.put("STATE", "EXIST");
    // data.put("SERVICE_ITEMAS", productsvcItema);
    // data.put("NEED_SERV_PARAM", "true");
    // data.put("GRP_CUSTOMIZE", "false");
    // data.put("ELEMENT_INDEX", "1");
    // GroupBaseBean.setDbConCode(pd, "cg");
    // IDataset userElement = bean.getElementFromPackageByUser(pd, data);
    // for (int i = 0; i < userElement.size(); i++)
    // {
    // IData map = userElement.getData(i);
    // IData param = new DataMap();
    // param.put("USER_ID", userId);
    // param.put("INST_TYPE", map.getString("ELEMENT_TYPE_CODE", ""));
    // param.put("INST_ID", map.getString("INST_ID", ""));
    // GroupBaseBean.setDbConCode(pd, getTradeData().getUserInfo().getString("EPARCHY_CODE", ""));
    // IDataset elementParamdataset = UserAttrQuery.getUserAttrByUserIdInstid(pd, param);
    // if (elementParamdataset != null && elementParamdataset.size() > 0)
    // {
    // if (map.getString("ELEMENT_TYPE_CODE", "").equals("S"))
    // {
    // map.put("SERV_PARAM", elementParamdataset);
    // map.put("HAS_SERV_PARAM", "true");
    // }
    // else if (map.getString("ELEMENT_TYPE_CODE", "").equals("D"))
    // {
    // map.put("DISCNT_PARAM", elementParamdataset);
    // map.put("HAS_DISCNT_PARAM", "true");
    // }
    // }
    // }
    // userElement = GroupBaseBean.sortProductElement(userElement, "PACKAGE_ID");
    // GroupProductUnit.operelementsdata(pd, userElement, result, data, true);
    //
    // IData data1 = new DataMap();
    // data1.put("PRODUCT_ID", productId);
    // IData compProductInfo = new ProductBean().getProductFromComp(pd, data1);
    //
    // // 集团定制
    // IDataset faResult = new DatasetList();
    // if (compProductInfo.getString("USE_TAG").equals(GroupBaseFactory.GroupDesignFlag.GroupDesignYes.getValue()))
    // {
    // // 已定制服务
    // GroupBaseBean.setDbConCode(pd, "cg");
    // IDataset memSvc = bean.getGrpCustomizeServByUserId(pd, data);
    // memSvc = memSvc.filter("PRODUCT_ID=22000020");
    // for (int i = 0; i < memSvc.size(); i++)
    // {
    // IData temData = new DataMap();
    // IData sourceData = (IData) memSvc.get(i);
    // temData.put("PRODUCT_ID", sourceData.getString("PRODUCT_ID"));
    // IData product = bean.getProductByPK(pd, temData);
    // sourceData.put("PRODUCT_MODE", product.getString("PRODUCT_MODE"));
    // sourceData.put("STATE", "EXIST");
    // sourceData.put("NEED_SERV_PARAM", "false");
    // sourceData.put("GRP_CUSTOMIZE", "true");
    // }
    //
    // // 已定制SP服务
    // GroupBaseBean.setDbConCode(pd, "cg");
    // IDataset spSvc = bean.getGrpCustomizeSpByUserId(pd, data);
    // spSvc = spSvc.filter("PRODUCT_ID=22000020");
    // for (int i = 0; i < spSvc.size(); i++)
    // {
    // IData temData = new DataMap();
    // IData sourceData = (IData) spSvc.get(i);
    // temData.put("PRODUCT_ID", sourceData.getString("PRODUCT_ID"));
    // IData product = bean.getProductByPK(pd, temData);
    // sourceData.put("PRODUCT_MODE", product.getString("PRODUCT_MODE"));
    // sourceData.put("STATE", "EXIST");
    // sourceData.put("NEED_SERV_PARAM", "false");
    // sourceData.put("GRP_CUSTOMIZE", "true");
    // }
    //
    // // 已定制优惠
    // GroupBaseBean.setDbConCode(pd, "cg");
    // IDataset memDiscnt = bean.getGrpCustomizeDiscntByUserId(pd, data);
    // memDiscnt = memDiscnt.filter("PRODUCT_ID=22000020");
    // for (int i = 0; i < memDiscnt.size(); i++)
    // {
    // IData temData = new DataMap();
    // IData sourceData = (IData) memDiscnt.get(i);
    // temData.put("PRODUCT_ID", sourceData.getString("PRODUCT_ID"));
    // IData product = bean.getProductByPK(pd, temData);
    // sourceData.put("PRODUCT_MODE", product.getString("PRODUCT_MODE"));
    // sourceData.put("STATE", "EXIST");
    // sourceData.put("NEED_SERV_PARAM", "false");
    // sourceData.put("GRP_CUSTOMIZE", "false");
    //
    // }
    // memDiscnt.addAll(memSvc);
    // memDiscnt.addAll(spSvc);
    // memDiscnt = GroupBaseBean.sortProductElement(memDiscnt, "PACKAGE_ID");
    // GroupProductUnit.operelementsdata(pd, memDiscnt, memResult, null, true);
    //
    // IDataset memPlusProduct = new DatasetList();
    // // 组合产品附加产品
    // IDataset memProductList = ProductUtil.getMebProduct(pd, productId);
    //
    // setMemBasePlusProduct(null);
    // for (int row = 0; row < memProductList.size(); row++)
    // {
    // IData memProduct = (IData) memProductList.get(row);
    // if (memProduct.getString("FORCE_TAG").equals("1"))
    // {
    // // 成员附加基本产品
    // setMemBasePlusProduct(memProduct.getString("PRODUCT_ID_B"));
    // }
    // else
    // memPlusProduct.add(memProduct);
    // }
    //
    // setSelectedMemPlusProduct(memPlusProduct);
    // setSelectedMemElements(memResult);
    // faResult.addAll(memResult);
    // }
    // setSelectedElements(result);
    // faResult.addAll(result);
    // pd.setParameter("SELECTED_ELEMENTS", faResult.toString());
    //
    // pd.setTransfer("BBOSS_USER_ID", userId);// 为了在BBOSS的参数页面能够使用USER_ID的值
    // }

    private IDataset getSelectedProduct() throws Exception
    {
        // String strSelectedProducts = pd.getData().getString("grpProductTreeSelected");
        // IDataset dataset = new DatasetList(strSelectedProducts);
        // return dataset;
        return null;
    }

    /**
     * @author zhujm 点击左侧产品包树中的节点后 调用此方法 刷新右侧下方的包展现区域
     * @param cycle
     * @throws Exception
     */
    public void getPackage(IRequestCycle cycle) throws Exception
    {
        // PageData pd = getPageData();
        // pd.setParameter("PRODUCT_ID", pd.getParameter("REFRESH_PACK_PRODUCT_ID", ""));
        // pd.setParameter("PACKAGE_ID", pd.getParameter("REFRESH_PACK_PACKAGE_ID", ""));
        // pd.setParameter("PRODUCT_MODE", pd.getParameter("REFRESH_PACK_PRODUCT_MODE", ""));
        // String extendElements = pd.getParameter("EXTEND_ELEMENTS", "");
        //
        // if (extendElements.length() > 0)
        // {
        // IDataset extendElementsSet = new DatasetList(extendElements);
        // setExtendElements(extendElementsSet);
        // }
    }

    /*
     * 在ajax刷新服务参数时 传入对应的包id
     */
    public void setPackage(IRequestCycle cycle) throws Exception
    {
        // PageData pd = getPageData();
        //
        // IDataset tmp = new DatasetList();
        // IData data = new DataMap();
        // data.put("DATA_ID", pd.getData().getString("PACKAGE_ID"));
        // data.put("EYEABLE", "TRUE");
        // tmp.add(data);
        //
        // setPackages(tmp);
        // pd.setTransfer("tradeData", getTradeData().toString());
    }

}
