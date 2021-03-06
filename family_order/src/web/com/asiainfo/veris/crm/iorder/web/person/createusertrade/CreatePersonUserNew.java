
package com.asiainfo.veris.crm.iorder.web.person.createusertrade;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.search.SearchResponse;
import com.ailk.search.client.SearchClient;
import com.asiainfo.veris.crm.iorder.pub.consts.IUpcConst;
import com.asiainfo.veris.crm.iorder.web.igroup.util.IUpcViewCall;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.ElementPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.PackagePrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.ProductPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.util.DataSetUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.upc.UpcViewCall;
import com.asiainfo.veris.crm.order.web.person.simcardmgr.SimCardBasePage;
import org.apache.log4j.Logger;
import org.apache.poi.util.StringUtil;
import org.apache.tapestry.IRequestCycle;

import java.util.HashMap;
import java.util.Map;

public abstract class CreatePersonUserNew extends SimCardBasePage
{
    private static transient final Logger logger = Logger.getLogger(CreatePersonUserNew.class);
    /**
     * 输入新开户号码后的校验，获取开户信息
     *
     * @author sunxin
     * @param cycle
     * @throws Exception
     */
    public void checkSerialNumber(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IDataset dataset = CSViewCall.call(this, "SS.CreatePersonUserSVC.checkSerialNumber", data);
        dataset.first().put("OPEN_TYPE", data.getString("OPEN_TYPE"));
        setEditInfo(dataset.first());
        IDataset productTypeList = dataset.first().getDataset("PRODUCT_TYPE_LIST");
        IDataset acctInfoDays = dataset.first().getDataset("ACCT_INFO_DAYS");
        IDataset packageinfos = dataset.first().getDataset("PACKAGES_SALE");
        IDataset cityCodeList = dataset.first().getDataset("CITY_CODE_LIST");
        StringBuilder productTypeStr = new StringBuilder();
        for (Object obj : productTypeList) {
            IData productType = (IData) obj;
            productTypeStr.append(productType.getString("PRODUCT_TYPE_CODE")).append(",");
        }

        setProductTypeList(productTypeStr.toString());
        setAcctInfoDays(acctInfoDays);
        setPackageInfos(packageinfos);
        setCityCodeList(cityCodeList);
        setAjax(dataset);

        // 查询"热门产品"和"推荐产品"
        IDataset hotRecommSales = new DatasetList();
        IData param = new DataMap();
        param.put("POPUTRADECODE", "3"); // 仅查询产品套餐类
        param.put("STAFF_ID", getVisit().getStaffId());
        IData saleInfos = CSViewCall.callone(this, "CS.WelcomeSVC.getHotAndRecInfo", param);
        if (IDataUtil.isNotEmpty(saleInfos)) {
            IDataset hotSales = saleInfos.getData("HOTINFOS").getDataset("PRODUCTS");
            IDataset recommSales = saleInfos.getData("RECINFOS").getDataset("PRODUCTS");
            if (IDataUtil.isNotEmpty(hotSales))
                hotRecommSales.addAll(hotSales);
            if (IDataUtil.isNotEmpty(recommSales))
                hotRecommSales.addAll(recommSales);
            for (Object obj : hotRecommSales) {
                IData sale = (IData) obj;
                IData offerInfo = UpcViewCall.queryOfferByOfferId(this, IUpcConst.ELEMENT_TYPE_CODE_PRODUCT, sale.getString("OFFER_CODE"));
                String brandCode = IUpcViewCall.queryBrandCodeByOfferId(offerInfo.getString("OFFER_ID"));
                sale.put("BRAND_CODE", brandCode);
            }
        }
        setHotRecomms(hotRecommSales);
    }

    /**
     * 输入SIM卡后的校验，获取卡信息
     *
     * @author sunxin
     * @param cycle
     * @throws Exception
     */
    public void checkSimCardNo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IDataset dataset = CSViewCall.call(this, "SS.CreatePersonUserSVC.checkSimCardNo", data);
        setEditInfo(dataset.first());
        setAjax(dataset);
    }

    /**
     * 人像信息比对员工信息
     *
     * @author dengyi
     * @param cycle
     * @throws Exception
     */
    public void isCmpPic(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IData param = new DataMap();

        param.putAll(data);
        param.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());

        IDataset dataset = CSViewCall.call(this, "SS.CreatePersonUserSVC.isCmpPic", param);
        setAjax(dataset.getData(0));
    }

    /**
     * 获取产品费用
     *
     * @author sunxin
     * @param cycle
     * @throws Exception
     */
    public void getProductFeeInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        data.put(Route.ROUTE_EPARCHY_CODE, data.getString("EPARCHY_CODE"));
        IDataset dataset = CSViewCall.call(this, "SS.CreatePersonUserSVC.getProductFeeInfo", data);
        setAjax(dataset);
    }

    /**
     * 获取网上选号可以选择的产品
     *
     * @author sunxin
     * @param cycle
     * @throws Exception
     */
    public void getProductForNet(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IDataset dataset = new DatasetList();
        data.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
        dataset = CSViewCall.call(this, "SS.CreatePersonUserSVC.getProductForNet", data);
        setAjax(dataset);
    }

    /**
     * 获取147号码可以选择的产品
     *
     * @author sunxin
     * @param cycle
     * @throws Exception
     */
    public void getProductForSpc(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IDataset dataset = new DatasetList();
        data.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
        dataset = CSViewCall.call(this, "SS.CreatePersonUserSVC.getProductForSpc", data);
        setAjax(dataset);
    }

    /**
     * 根据type_id, eparchy_code获取静态参数，由于框架不支持，所以自己写 的服务
     *
     * @author chenzm
     * @param typeId
     * @throws Exception
     */
    public IDataset getStaticListByTypeIdEparchy(String typeId) throws Exception
    {
        IData data = new DataMap();

        data.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());

        data.put("TYPE_ID", typeId);

        IDataset dataset = CSViewCall.call(this, "CS.StaticInfoQrySVC.getStaticListByTypeIdEparchy", data);

        return dataset;
    }

    /**
     * 费用重算
     *
     * @author chenzm
     * @param cycle
     * @throws Exception
     */
    public void mputeFee(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IDataset dataset = CSViewCall.call(this, "SS.CreatePersonUserSVC.mputeFee", data);
        setAjax(dataset);
    }

    /**
     * 初始化方法
     *
     * @author chenzm
     * @param cycle
     * @throws Exception
     */
    public void onInitTrade(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        // 记录页面初始化时间点，存入台帐表EXEC_ACTION字段
        String execAction = SysDateMgr.getSysTime();
        data.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
        IDataset dataset = CSViewCall.call(this, "SS.CreatePersonUserSVC.onInitTrade", data);

        /*
         * @chenxy3@REQ201501160002 关于BOSS系统开户界面限制身份证号手工录入的需求
         * 新增获取部门类型编码，如果是100或者500以外的部门需要做限制
         * */
        String departKindCode = StaticUtil.getStaticValue(getVisit(), "TD_M_DEPART", "DEPART_ID", "DEPART_KIND_CODE", this.getVisit().getDepartId());
        dataset.first().put("DEPART_KIND_CODE", departKindCode);
        dataset.first().put("EXEC_ACTION", execAction);
        
        //add by zhangxing3 for QR-20190611-03关于经办人人像比对失败也开户成功问题
        IDataset dataset1 = CSViewCall.call(this, "SS.CreatePersonUserSVC.isCmpPic", data);
        if (IDataUtil.isNotEmpty(dataset1))
        {
        	dataset.first().put("CMP_TAG", dataset1.getData(0).getString("CMPTAG",""));
        }
        //add by zhangxing3 for QR-20190611-03关于经办人人像比对失败也开户成功问题
        
        setInitInfo(dataset.first());
        // setAcctInfoDays(acctInfoDays);
        // 代理商开户时，初始化代理商开户预存款档次
        if ("AGENT_OPEN".equals(dataset.first().getString("OPEN_TYPE", "")))
        {
            IDataset AdvanceData = CSViewCall.call(this, "SS.CreatePersonUserSVC.queryAdvanceFees", data);
            setAdvanceFeeList(AdvanceData);

            // REQ201502050013放号政策调整需求 songlm
            data.put("PARAM_ATTR", "518");//518为已激活用户对应的包

            //获取赠送话费的选项
            IDataset PresentFeeList = CSViewCall.call(this, "SS.CreatePersonUserSVC.queryPresentFees", data);

            //工号的包权限控制
            PackagePrivUtil.filterPackageListByPriv(getVisit().getStaffId(), PresentFeeList);

            setPresentFeeList(PresentFeeList);

            //设置默认项
            if (IDataUtil.isNotEmpty(PresentFeeList))
            {
                IData PresentFeeParam = new DataMap();
                PresentFeeParam.put("AGENT_PRESENT_FEE", "");

                for (int i = 0, size = PresentFeeList.size(); i < size; i++)
                {
                    IData PresentFee = PresentFeeList.getData(i);
                    if("1".equals(PresentFee.getString("DEFAULT_VALUE","")))
                    {
                        PresentFeeParam.put("AGENT_PRESENT_FEE", PresentFee.getString("DATA_ID",""));
                    }
                }

                setPresentFee(PresentFeeParam);

            }

        }
    }

    /**
     * 业务提交
     *
     * @author sunxin
     * @param cycle
     * @throws Exception
     */
    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData data = getData();

        //物联网开户 联系号码校验 @yanwu
        String strOpenType = data.getString("OPEN_TYPE");
        if( "IOT_OPEN".equals(strOpenType) ){
            IData putData = new DataMap();
            putData.put("IN_MODE_CODE", "0");
            putData.put("SERIAL_NUMBER", data.getString("PHONE",""));
            putData.put("PHONE", data.getString("PHONE",""));
            putData.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
            CSViewCall.call(this, "SS.CreatePersonUserSVC.checkPhone", putData);

            data.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
            CSViewCall.call(this, "SS.ChangeProductIntfSVC.checkPwlwApnNameK", data);
        }
        if(StringUtils.equals("8", strOpenType)){
            data.put("REMARK", "OneCardOneDevice");
        }
        IDataset result = CSViewCall.call(this, "SS.CreatePersonUserRegSVC.tradeReg", data);
        
        //REQ201904190005全网用户数据查询平台分册V2.2.0改造需求
        IData bizCode = new DataMap();
		bizCode.put("SUBSYS_CODE", "CSM");
		bizCode.put("PARAM_ATTR", "9124");
		bizCode.put("PARAM_CODE", "SAVE_PIC_TAG");
		IDataset commparas = CSViewCall.call(this, "CS.CommparaInfoQrySVC.getCommparaInfoByBizCode", bizCode);
        if (IDataUtil.isNotEmpty(commparas))
        {
        	if("1".equals(commparas.getData(0).getString("PARA_CODE1", "0")))
        	{
		        if(DataSetUtils.isBlank(result)) {
					CSViewException.apperr(CrmCommException.CRM_COMM_595);
				}
				String orderId = result.first().getString("ORDER_ID","");
		        String tradeId = result.first().getString("TRADE_ID","");
		        String sid=SysDateMgr.getSysDateYYYYMMDDHHMMSS();
		        orderId=orderId.substring(0, orderId.length()-4);
		        data.put("MOBILENUM", data.getString("SERIAL_NUMBER",""));
		        data.put("IDCARDNUM", data.getString("PSPT_ID",""));  
		        data.put("REMOTE_ORDER_ID", "COP898" + sid + orderId);
		        data.put("TRADE_ID", tradeId);
		        //System.out.println("-----------zhangxing3KaiHu------------params:"+data);
		        CSViewCall.call(this, "SS.RemoteDestroyUserSVC.insPsptFrontBack", data);
        	}
        }
        //REQ201904190005全网用户数据查询平台分册V2.2.0改造需求
        
        setAjax(result);
    }

    /**
     * 开户是否绑定号码活动处理 需要完善，sunxin
     *
     * @author sunxin
     * @param cycle
     * @throws Exception
     */
    public void querySaleActive(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IDataset dataset = CSViewCall.call(this, "SS.CreatePersonUserSVC.querySaleActive", data);
        setAjax(dataset);
    }

    /**
     * REQ201602290007 关于入网业务人证一致性核验提醒的需求
     * 获取是否需要弹出框，chenxy3
     * 2016-03-08 chenxy3
     */
    public void checkNeedBeforeCheck(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IDataset dataset = CSViewCall.call(this, "SS.BeforeCheckSVC.checkNeedBeforeCheck", data);
        setAjax(dataset.getData(0));
    }

    /**
     * 资源释放
     *
     * @author sunxin
     * @param cycle
     * @throws Exception
     */
    public void releaseSingleRes(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        data.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
        IDataset dataset = CSViewCall.call(this, "SS.CreatePersonUserSVC.releaseSingleRes", data);
        setAjax(dataset);
    }

    public void search(IRequestCycle cycle) throws Exception
    {
        IData param = this.getData();
        String searchText = param.getString("SEARCH_NAME");
        String eparchyCode = param.getString("EPARCHY_CODE");
        String productId = param.getString("PRODUCT_ID");
        String searchType = param.getString("SEARCH_TYPE");
        if (StringUtils.isNotBlank(searchText) && searchText.length() >= 2)
        {
            if ("1".equals(searchType))
            {
                // 产品搜索
                SearchResponse resp = SearchClient.search("PM_OFFER_OPEN_USER", searchText, 0, 20);
                IDataset datas = resp.getDatas();
                ProductPrivUtil.filterProductListByPriv(this.getVisit().getStaffId(), datas);
                this.setAjax(datas);
            }
            else if ("2".equals(searchType))
            {
                // 元素搜索
                Map<String, String> searchData = new HashMap<String, String>();
                searchData.put("PRODUCT_ID", productId);
                SearchResponse resp = SearchClient.search("PM_OFFER_ELEMENT", searchText, searchData, 0, 10);
                IDataset datas = resp.getDatas();
                ElementPrivUtil.filterElementListByPriv(this.getVisit().getStaffId(), datas);
                this.setAjax(datas);
            }
        }
    }

    /**
     * 个人用户开户权限校验
     * @param cycle
     * @throws Exception
     * @author zhaohj3
     * @date 2018-1-4 16:50:50
     */
    public void hasPriv(IRequestCycle cycle) throws Exception
    {
        IData para = this.getData();

        String privId = para.getString("PRIV_ID");
        boolean hasPriv = StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), privId);

        IData privData = new DataMap();
        privData.put("HAS_PRIV", hasPriv);
        privData.put("STAFF_ID", getVisit().getStaffId());

        setAjax(privData);
    }
    
    /**
     * @Description: 选择产品时业务提示信息
     * @param:参数描述
     * @return：返回结果描述
     * @version: v1.0.0
     * @author: xurf3
     * @date: 2019-01-03
     */
    public void createPersonUserTipsInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();

        IData infos = CSViewCall.callone(this, "SS.CreatePersonUserSVC.createPersonUserTipsInfo", data);

        setAjax(infos);
    }


    /**
     * @param clcle
     * @throws Exception
     * @Description: IMEI校验
     */
    public void verifyIMEI(IRequestCycle clcle) throws Exception
    {
        IData data = getData();
        IDataset dataset = CSViewCall.call(this, "SS.BestUseMobileSVC.verifyIMEI", data);
        setAjax(dataset.getData(0));
    }

    /**
     * @param clcle
     * @throws Exception
     * @Description: 前台检索eSIM设备合法性校验结果
     */
    public void queryCheckResult(IRequestCycle clcle) throws Exception
    {
        IData data = getData();
        data.put("IN_TYPE", "eSIM");
        IDataset dataset = CSViewCall.call(this, "SS.BestUseMobileSVC.queryCheckResult", data);
        setAjax(dataset.getData(0));
    }

    public void getESIMSerialNumber(IRequestCycle clcle) throws Exception
    {
        IData data = getData();
        IDataset dataset = CSViewCall.call(this, "SS.CreatePersonUserSVC.getESIMSerialNumber", data);
        setESIMSerialNumberList(dataset);
        setAjax(dataset);
    }
    public abstract void setESIMSerialNumberList(IDataset esimSerialNumberList);

    public abstract void setAcctInfoDays(IDataset acctInfoDays);

    public abstract void setBankCodeList(IDataset bankCodeList);

    public abstract void setCityCodeList(IDataset cityCodeList);

    public abstract void setEditInfo(IData editInfo);

    public abstract void setInitInfo(IData initInfo);

    public abstract void setHotRecomms(IDataset hotRecomms);

    public abstract void setPackageInfos(IDataset packageinfos);

    public abstract void setProductTypeList(String productTypeList);

    public abstract void setReturnInfo(IData returnInfo);

    public abstract void setAdvanceFeeList(IDataset AdvanceFeeList);

    public abstract void setPresentFeeList(IDataset PresentFeeList);//REQ201502050013放号政策调整需求 by songlm

    public abstract void setPresentFee(IData presentFee);//REQ201502050013放号政策调整需求 by songlm
}
