
package com.asiainfo.veris.crm.iorder.web.person.broadband.nophonewidenet.createnophonewideuser;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.ProductPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;
import org.apache.tapestry.IRequestCycle;

public abstract class NoPhoneWideUserCreateNew extends PersonBasePage
{
    private final static String ARMY_CARD  = "3"; // 军人身份证

    private final static String BJ_ID_CARD = "1"; // 外地身份证

    /**
     * 初始化方法
     *
     * @author yuyj3
     * @param cycle
     * @throws Exception
     */
    public void onInitTrade(IRequestCycle cycle) throws Exception
    {
        IData param = new DataMap();

        //手写输入权限
        IData qryRight = new DataMap();
        qryRight = initInputRight(qryRight);
        qryRight.put("INPUT_PERMISSION", qryRight.getString("INPUT_PERMISSION"));
        qryRight.put("INPUT_SIM_TAG", qryRight.getString("INPUT_SIM_TAG"));//sim卡号局方要求不要，弄个开关，免得后面又需要
        //qryRight.put("PORTRAIT_DISCRIMINATION", qryRight.getString("PORTRAIT_DISCRIMINATION"));
        setQryRight(qryRight);

        param.put("TRADE_TYPE_CODE", "680");

        //预约施工时间只能选择48小时之后
        String minDate = SysDateMgr.getAddHoursDate(SysDateMgr.getSysTime(), 48);//SysDateMgr.addDays(2);

        param.put("MIN", minDate);
        
        if("Y".equals(qryRight.getString("INPUT_SIM_TAG"))){
            setIsNeed(true);
        }
        
        setInfo(param);
    }

    /**
     * 获得可以开户证件类型
     * @param cycle
     * @throws Exception
     * @author yuyj3
     */
    public void queryPsptTypeList(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        data.put(Route.ROUTE_EPARCHY_CODE, "0898");
        IData resultData = CSViewCall.callone(this, "SS.NoPhoneWideUserCreateSVC.queryPsptTypeList", data);
        setAjax(resultData);
    }

    /**
     * 校验宽带账号
     * @param cycle
     * @throws Exception
     * @author yuyj3
     */
    public void checkWidenetAcctId(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        data.put(Route.ROUTE_EPARCHY_CODE, "0898");
        IData resultData = CSViewCall.callone(this, "SS.NoPhoneWideUserCreateSVC.checkWidenetAcctId", data);
        setAjax(resultData);
    }


    /**
     * 获得可用的宽带账号
     * @param cycle
     * @throws Exception
     * @author yuyj3
     */
    public void getValidWideNetAccountId(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        data.put(Route.ROUTE_EPARCHY_CODE, "0898");
        IData resultData = CSViewCall.callone(this, "SS.NoPhoneWideUserCreateSVC.getValidWideNetAccountId", data);
        setAjax(resultData);
    }


    /**
     * 释放宽带账号资源
     * @param cycle
     * @throws Exception
     * @author yuyj3
     */
    public void releaseWideNetAcct(IRequestCycle cycle) throws Exception {
        IData data = getData();

        data.put(Route.ROUTE_EPARCHY_CODE, "0898");
        IData resultData = CSViewCall.callone(this, "SS.NoPhoneWideUserCreateSVC.releaseSelOccupiedSn", data);
        setAjax(resultData);
    }

    /**
     * 获取产品费用
     *
     * @author yuyj3
     * @param cycle
     * @throws Exception
     */
    public void getProductFeeInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        data.put(Route.ROUTE_EPARCHY_CODE, data.getString("EPARCHY_CODE"));
        data.put("TRADE_TYPE_CODE", data.getString("TRADE_TYPE_CODE"));
        data.put("PRODUCT_ID", data.getString("PRODUCT_ID"));
        data.put("PACKAGE_ID", "");
        data.put("ELEMENT_ID", data.getString("ELEMENT_ID"));
        data.put("ELEMENT_TYPE_CODE", "P");
        data.put("TRADE_FEE_TYPE", "3");
        data.put("EPARCHY_CODE", data.getString("EPARCHY_CODE"));
        IDataset dataset = CSViewCall.call(this, "CS.ProductFeeInfoQrySVC.getProductFeeInfo", data);
        setAjax(dataset);
    }

    /**
     * 根据产品类型获得产品信息
     * @param cycle
     * @throws Exception
     * @author yuyj3
     */
    public void changeWideProductType(IRequestCycle cycle) throws Exception
    {
        IData data = getData();

        data.put(Route.ROUTE_EPARCHY_CODE, "0898");

        IData resultData = CSViewCall.callone(this, "SS.NoPhoneWideUserCreateSVC.getWidenetProductInfoByWideType", data);

        IDataset wideModemStyle  = resultData.getDataset("WIDE_MODEM_STYLE");

        IDataset productList  = resultData.getDataset("PRODUCT_LSIT");

        // 获取魔百和信息
        IDataset topSetBoxProducts = CSViewCall.call(this, "SS.NoPhoneWideUserCreateSVC.loadTopSetBoxInfo", data);
        if (IDataUtil.isNotEmpty(topSetBoxProducts)) {
            // 魔百和信息
            setTopSetBoxProducts(topSetBoxProducts);
        }

        // 产品权限控制
        ProductPrivUtil.filterProductListByPriv(this.getVisit().getStaffId(), productList);
        setProductList(productList);
        setWideModemStyleList(wideModemStyle);
        setAjax(resultData);
    }

    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData data = getData();

        String routeId = data.getString("EPARCHY_CODE");
        data.put(Route.ROUTE_EPARCHY_CODE, routeId);

        // 客服工号，HAIN, 则默认到0898
        if (StringUtils.isBlank(routeId) || routeId.length() != 4 || !StringUtils.isNumeric(routeId))
        {
            data.put(Route.ROUTE_EPARCHY_CODE, "0898");
        }

        IDataset dataset = CSViewCall.call(this, "SS.NoPhoneWideUserCreateRegSVC.tradeReg", data);
        setAjax(dataset);
    }

    /**
     * 获得光猫费用
     * @param cycle
     * @throws Exception
     * @author yuyj3
     */
    public void getModemDeposit(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        data.put(Route.ROUTE_EPARCHY_CODE, "0898");
        IData result = CSViewCall.callone(this, "SS.NoPhoneWideUserCreateSVC.getModemDeposit", data);

        this.setAjax(result);
    }

    public void getCreateWideUserStyle(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        //刷新开户方式
        IDataset dataset = CSViewCall.call(this, "SS.NoPhoneWideUserCreateSVC.getCreateWideUserStyle", data);
        setAjax(dataset);
    }

    /**
     * 输入新开户号码后的校验，获取开户信息
     * @param cycle
     * @throws Exception
     */
    public void checkSerialNumber(IRequestCycle cycle) throws Exception {
        IData data = getData();
        //号码限制标识，1为号码限制在td_s_commpara param_attr=2828中
        data.put("NUMBER_LIMIT_FLAG","1" );
        //将业务类型传过去
        data.put("TRADE_TYPE_CODE","4900" );
        data.put(Route.ROUTE_EPARCHY_CODE, "0898");
        IDataset dataset = CSViewCall.call(this, "SS.NoPhoneWideUserCreateSVC.checkSerialNumber", data);
        setEditInfo(dataset.first());
        setAjax(dataset);
    }

    /**
     * 输入SIM卡后的校验，获取卡信息
     * @param cycle
     * @throws Exception
     */
    public void checkSimCardNo(IRequestCycle cycle) throws Exception {
        IData data = getData();
        IDataset dataset = CSViewCall.call(this, "SS.CreatePersonUserSVC.checkSimCardNo", data);
        setEditInfo(dataset.first());
        setAjax(dataset);
    }

    /**
     * 查询互联网电视机顶盒基础优惠包【0】和可选优惠包【2】
     * @param cycle
     * @throws Exception
     */
    public void queryTopSetBoxDiscntPackagesByPID(IRequestCycle cycle) throws Exception {
        IData data = getData();
        data.put(Route.ROUTE_EPARCHY_CODE, "0898");
        IData retData = CSViewCall.callone(this, "SS.NoPhoneWideUserCreateSVC.queryTopSetBoxDiscntPackagesByPID", data);

        IDataset basePackages = retData.getDataset("B_P");
        IDataset optionPackages = retData.getDataset("O_P");
        setBasePackages(basePackages);
        setOptionPackages(optionPackages);

        //获取魔百和费用
        IData result = CSViewCall.callone(this, "SS.NoPhoneWideUserCreateSVC.gettopsetboxfee", data);
        String fee = result.getString("PARA_CODE2");
        String month = data.getString("TOP_SET_BOX_TIME");
        String totalFee = String.valueOf(Integer.parseInt(month)*Integer.parseInt(fee));
        retData.put("TOP_SET_BOX_FEE", totalFee);

        setAjax(retData);
    }

    /**
     * 魔百和营销活动依赖校验
     * @param cycle
     * @throws Exception
     */
    public void checkSaleActiveDependence(IRequestCycle cycle) throws Exception {
        IData data = getData();
        data.put(Route.ROUTE_EPARCHY_CODE, "0898");
        IData result = CSViewCall.callone(this, "SS.NoPhoneWideUserCreateSVC.checkSaleActiveDependence", data);
        this.setAjax(result);
    }

    public void queryCheckSaleActiveFee(IRequestCycle cycle) throws Exception {
        IData data = getData();
        data.put(Route.ROUTE_EPARCHY_CODE, "0898");
        IData result = CSViewCall.callone(this, "SS.NoPhoneWideUserCreateSVC.queryCheckSaleActiveFee", data);
        this.setAjax(result);
    }

    public void checkSelectedDiscnts(IRequestCycle cycle) throws Exception {
        IData data = getData();
        data.put(Route.ROUTE_EPARCHY_CODE, "0898");
        IData result = CSViewCall.callone(this, "SS.NoPhoneWideUserCreateSVC.checkSelectedDiscnts", data);
        this.setAjax(result);
    }

    public void settopsetboxfee(IRequestCycle cycle) throws Exception {
        IData data = getData();
        data.put(Route.ROUTE_EPARCHY_CODE, "0898");
        IData result = CSViewCall.callone(this, "SS.NoPhoneWideUserCreateSVC.gettopsetboxfee", data);

        String fee = result.getString("PARA_CODE2");
        String month = data.getString("TOP_SET_BOX_TIME");
        String totalFee = String.valueOf(Integer.parseInt(month)*Integer.parseInt(fee));

        data.put("TOP_SET_BOX_FEE", totalFee);
        setAjax(data);
    }

    /**
     * 校验两城两宽北京移动号码是否已预受理，即在TF_F_WIDENET_SYNC表有预受理登记
     * @param cycle
     * @throws Exception
     */
    public void checkBJWidenetSn(IRequestCycle cycle) throws Exception {
        IData data = getData();
        IData result = CSViewCall.callone(this, "SS.NoPhoneWideUserCreateSVC.checkBJWidenetSn", data);
        if (IDataUtil.isNotEmpty(result)) {
            String psptTypeName;
            String psptTypeCode = result.getString("PSPT_TYPE_CODE");
            IDataset psptInfo = pageutil.getStaticList("TD_S_PASSPORTTYPE2", psptTypeCode);
            if (IDataUtil.isNotEmpty(psptInfo)) {
                psptTypeName = psptInfo.first().getString("DATA_NAME");
            } else if (ARMY_CARD.equals(psptTypeCode)) {
                psptTypeName = "军人身份证";
            } else {
                psptTypeName = "外地身份证";
                result.remove("PSPT_TYPE_CODE");
                result.put("PSPT_TYPE_CODE", BJ_ID_CARD);
            }
            result.put("PSPT_TYPE_NAME", psptTypeName);
            setAjax(result);
        }
    }

    /**REQ201809300006
     * @author wuck3
     */
    public void checkInputRight(IRequestCycle cycle) throws Exception{
        IData data = getData();
        data.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
        IData result = CSViewCall.callone(this, "SS.NoPhoneWideUserCreateSVC.checkInputRight", data);
        setAjax(result);
    }

    /**
     * 初始化权限
     * @param cycle
     * @throws Exception
     * @author wuck3
     */
    public IData initInputRight(IData param) throws Exception{
        IData data = new DataMap();
        param.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
        IDataset dataset = CSViewCall.call(this, "SS.NoPhoneWideUserCreateSVC.initInputRight", param);
        data.put("INPUT_PERMISSION",dataset.getData(0).getString("INPUT_PERMISSION"));
        //data.put("PORTRAIT_DISCRIMINATION",dataset.getData(0).getString("PORTRAIT_DISCRIMINATION"));
        data.put("INPUT_SIM_TAG", dataset.getData(0).getString("INPUT_SIM_TAG"));//sim卡号局方要求不要，弄个开关，免得后面又需要       
        return data;
    }

    /**
     * 人像信息比对员工信息
     *
     * @author dengyi
     * @param clcle
     * @throws Exception
     */
    public void isCmpPic(IRequestCycle clcle) throws Exception
    {
        IData data = getData();
        IData param = new DataMap();

        param.putAll(data);
        param.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());

        IDataset dataset = CSViewCall.call(this, "SS.CreatePersonUserSVC.isCmpPic", param);
        setAjax(dataset.getData(0));
    }

    /**
     * 一机多宽“统付主号码”的身份证号和界面上输入的身份证号必须一致检验
     * @param cycle
     * @throws Exception
     */
    public void getPsptBySn(IRequestCycle cycle) throws Exception {
        IData data = getData();
        IDataset result = CSViewCall.call(this, "CS.CustomerInfoQrySVC.getCustInfoBySn", data);
        setAjax(result.first());
    }

    public void checkPaySerialNumber(IRequestCycle cycle) throws Exception{
        IData data = getData();
        data.put("SERIAL_NUMBER", data.getString("PAY_SERIAL_NUMBER"));
        IData result = CSViewCall.callone(this, "SS.NophoneWideUnionPaySVC.checkPaySerialNumber", data);
        setAjax(result);
    }

    public abstract void setQryRight(IData qryRight);
    public abstract void setInfo(IData info);

    public abstract void setProductList(IDataset productList);

    public abstract void setWideModemStyleList(IDataset wideModemStyleList);

    public abstract void setEditInfo(IData editInfo);

    public abstract void setBasePackages(IDataset basePackages);

    public abstract void setOptionPackages(IDataset optionPackages);

    public abstract void setTopSetBoxProducts(IDataset topSetBoxProducts);
    
    public abstract void setIsNeed(boolean can);

}
