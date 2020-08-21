package com.asiainfo.veris.crm.iorder.web.family.handle;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizHttpHandler;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public class FamilyHandler extends CSBizHttpHandler {


    /**
     * 添加已有角色，检验是否可以添加
     * duhj
     * @throws Exception
     */
    public void checkAddChilrenRole() throws Exception {
        IData data = getData();
        data.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
        IDataset dataset = CSViewCall.call(this, "SS.FamilyWideNetSVC.checkAddChilrenRole", data);
        setAjax(dataset);
    }

    /**
     * 获取手机下的角色，当前只有一条，延申一下，后续可能发展多条宽带、多个固话
     * duhj
     * @throws Exception
     */
    public void queryRoleUserList() throws Exception {
        IData data = getData();
        data.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
        IDataset dataset = CSViewCall.call(this, "SS.FamilyWideNetSVC.queryRoleUserList", data);
        setAjax(dataset);
    }

	/**
	 * 宽带页面初始化 duhj
	 *
	 * @throws Exception
	 */
	public void roleWideInit() throws Exception {
		IData data = getData();
		data.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
		data.put("serialNumber", data.getString("SERIAL_NUMBER"));

		String openType  = data.getString("OPEN_TYPE");
		if("NEW".equals(openType)){
		       IData resultData = CSViewCall.callone(this, "SS.MergeWideUserCreateSVC.loadChildInfo", data);
		        resultData.put("WIDE_AREA_CODE", StaticUtil.getStaticList("COP_AREA_CODE"));//地区下拉框
		        resultData.put("WIDE_PRODUCT_TYPE", StaticUtil.getStaticList("WIDE_PRODUCT_TYPE"));//宽带产品类型下拉框
		        // 预约施工时间只能选择48小时之后
		        String minDate = SysDateMgr.getAddHoursDate(SysDateMgr.getSysTime(), 48);
		        String maxDate = SysDateMgr.addDays2(30);
		        resultData.put("SUGGEST_DATE_MAX", maxDate);//预约时间
		        resultData.put("SUGGEST_DATE_MIN", minDate);
		        //光猫调测费
		        IDataset modelActiveSales = this.getSaleActiveByParaCode1();
		        resultData.put("MODEL_ACTIVE_SALE", modelActiveSales);
		        setAjax(resultData);
		}else if("OLD".equals(openType)){
            IData resultData = CSViewCall.callone(this, "SS.FamilyWideNetSVC.getUserWideNetInfos", data);
            setAjax(resultData);
		}


	}

    /**
     * 光猫营销活动检验，沿用原有检验逻辑
     * @throws Exception
     * @author duhj
     */
    public void checkSaleActive() throws Exception
    {
        IData data = getData();
        data.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
        //如果选择了优惠则需要判断这些优惠是否是包年优惠，如果是包年优惠则不能选择宽带营销活动
        IData result = CSViewCall.callone(this, "SS.MergeWideUserCreateSVC.checkSaleActive", data);
        this.setAjax(result);
    }

    /**
     * 光猫营销活动费用校验
     * @throws Exception
     * @author duhj
     */
    public void queryCheckSaleActiveFee() throws Exception
    {
        IData data = getData();
        data.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
        IData result = CSViewCall.callone(this, "SS.MergeWideUserCreateSVC.queryCheckSaleActiveFee", data);
        this.setAjax(result);
    }
	private IDataset getSaleActiveByParaCode1()  throws Exception
    {
        IData data = new DataMap();
        data.put("PARAM_CODE", "600");
        data.put("PARA_CODE1", "FTTH");
        data.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
        IDataset dataset = CSViewCall.call(this, "SS.MergeWideUserCreateSVC.getSaleActiveByParaCode1", data);
        if(dataset != null && dataset.size() > 0){
            for (int i = 0; i < dataset.size(); i++) {
                IData iData = dataset.getData(i);
                String paraCode5 = iData.getString("PARA_CODE5","");
                if(paraCode5.equals("79082840") || paraCode5.equals("79082818")){//家庭过滤掉扶贫包，因为家庭宽带没有活动，扶贫活动与扶贫调测费绑定
                    dataset.remove(i);
                    i--;                    
                }
            }
        }
        return dataset;
    }


    /**
     * 提交前费用校验(家庭宽带统一付费，这里主要检验光猫和魔百和调测费)
     * @param cycle
     * @throws Exception
     * @author duhj
     */
    public void checkFeeBeforeSubmit() throws Exception
    {
        IData data = getData();
        data.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
        IData result = CSViewCall.callone(this, "SS.MergeWideUserCreateSVC.checkFeeBeforeSubmit", data);
        this.setAjax(result);
    }


    /**
     * 查询当前角色下是否有子角色，页面渲染角色按钮
     * 1.手机下是否有宽带
     * 2.宽带下是否有固话和TV
     * @throws Exception
     * duhj
     */
    public void checkIsHasChildrenRole() throws Exception
    {
        IData data = getData();
        data.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
        IData result = CSViewCall.callone(this, "SS.FamilyWideNetSVC.checkIsHasChildrenRole", data);
        this.setAjax(result);
    }

    /**
	 * FamilyTv处理
	 * ZhangXi
	 *
	 * @throws Exception
	 */
	public void familyTvOper() throws Exception {
		IData data = getData();
		IDataset dataset = new DatasetList();
		String operFlag = data.getString("OPER_FLAG", "");

		//页面初始化
		if("roleTvInit".equals(operFlag)){
			dataset = CSViewCall.call(this, "SS.FamilyTvOpenSVC.familyTvOpenCheck", data);
	        setAjax(dataset.first());//魔百和产品列表
	        return;
		}

		//根据魔百和产品Id查询基础优惠包和可选优惠包
		if("qryPackagesByPID".equals(operFlag)){
			dataset = CSViewCall.call(this, "SS.FamilyTvOpenSVC.qryPackagesByPID", data);
	        setAjax(dataset.first());
	        return;
		}

		//魔百和调测费活动校验
		if("checkSaleActive".equals(operFlag)){
			 IData result = CSViewCall.callone(this, "SS.InternetTvOpenSVC.checkSaleActive", data);
			 setAjax(result);
			 return;
		}

		//营销活动校验
		if("queryCheckSaleActiveFee".equals(operFlag)){
			IData result = CSViewCall.callone(this, "SS.InternetTvOpenSVC.queryCheckSaleActiveFee", data);
	        this.setAjax(result);
		}

		//获取魔百和信息
		if("queryTopsetBoxInfo".equals(operFlag)){
			dataset = CSViewCall.call(this, "SS.FamilyTvOpenSVC.queryTopsetBoxInfo", data);
			setAjax(dataset);
	        return;
		}

	}


	/**
	 * 获取商品订购信息
	 *
	 * @throws Exception
	 */
	public void dealAddOffers() throws Exception {
		IData data = getData();
		data.put(Route.ROUTE_EPARCHY_CODE, data.getString("EPARCHY_CODE"));
		IDataset elements = CSViewCall.call(this, "SS.FamilySVC.dealAddOffers", data);
		IData ajax = new DataMap();
		ajax.put("OFFERS", elements);
		for (int i = 0; i < elements.size(); i++) {
			IData element = elements.getData(i);
			if (StringUtils.isNotBlank(element.getString("RULE_TYPE", ""))) {
				elements.remove(i);
			}
		}
		setAjax(ajax);
	}

	/**
	 * 触点业务校验
	 *
	 * @throws Exception
	 */
	public void check() throws Exception {
		IData data = getData();
		if (StringUtils.isBlank(data.getString(Route.ROUTE_EPARCHY_CODE)))
			data.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
		IDataset dataset = CSViewCall.call(this, "SS.FamilySVC.check", data);
		this.setAjax(IDataUtil.isEmpty(dataset) ? new DataMap() : dataset.getData(0));
	}

	/**
	 * 触点业务参数转换
	 *
	 * @throws Exception
	 */
	public void trans() throws Exception {
		IData data = getData();
		String eparchyCode = data.getString(Route.ROUTE_EPARCHY_CODE);
		if (StringUtils.isBlank(eparchyCode) || "undefined".equals(eparchyCode)) {
			data.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
		}
		IDataset dataset = CSViewCall.call(this, "SS.FamilySVC.offersFilter", data);
		this.setAjax(IDataUtil.isEmpty(dataset) ? new DataMap() : dataset.getData(0));
	}

	public void filterOffers() throws Exception {
		IData data = getData();
		String eparchyCode = data.getString(Route.ROUTE_EPARCHY_CODE);
		if (StringUtils.isBlank(eparchyCode) || "undefined".equals(eparchyCode)) {
			data.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
		}
		IDataset dataset = CSViewCall.call(this, "SS.FamilySVC.filterOffers", data);
		this.setAjax(IDataUtil.isEmpty(dataset) ? new DataMap() : dataset.getData(0));
	}

	/**
	 * @author yuyz
	 * @Description 校验号码
	 * @Date 14:59 2020/7/17
	 * @Param []
	 * @return void
	 **/
	public void roleImsInit() throws Exception {
		IData data = getData();
		IDataset dataset = new DatasetList();// CSViewCall.call(this, "SS.IMSLandLineSVC.checkAuthSerialNum", data);
		IData retData = new DataMap();// dataset.first();
		setAjax(retData);

	}
	/**
	* @author yuyz
	* @Description 校验固话号码
	* @Date 17:11 2020/7/17
	* @Param []
	* @return void
	**/
	public void checkFixPhoneNum() throws Exception{
		IData userData = getData();
		String fixNum = userData.getString("FIX_NUMBER", "");
		userData.put("FIX_NUMBER", "0898"+fixNum);
		IDataset results = CSViewCall.call(this, "SS.IMSLandLineSVC.checkFixPhoneNum", userData);
		setAjax(results.first());
	}
	/**
	* @author yuyz
	* @Description
	* @Date 17:20 2020/7/21
	* @Param []
	* @return void
	**/
	public void isCmpPic() throws Exception{
		IData data = getData();
		IData param = new DataMap();

		param.putAll(data);
		param.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());

		IDataset dataset = CSViewCall.call(this, "SS.CreatePersonUserSVC.isCmpPic", param);
		setAjax(dataset.getData(0));
	}

    /**
     * @Description: 手机成员角色初始化
     * @Param: []
     * @return: void
     * @Author: zhenggang
     * @Date: 2020/7/27 20:00
     */
    public void checkPhoneRole() throws Exception
    {
        IData data = getData();
        IDataset dataset = CSViewCall.call(this, "SS.FamilySVC.checkPhoneRole", data);
        setAjax(dataset.first());
    }


	/**
	 * @return void
	 * @author yuyz
	 * @Description 查询已有固话商品信息
	 * @Date 10:39 2020/7/31
	 * @Param []
	 **/
	public void queryImsExistOffers() throws Exception {
		IData data = getData();
		IData imsProd = new DataMap();
		data.put(Route.ROUTE_EPARCHY_CODE, data.getString("EPARCHY_CODE"));
		IDataset userProductInfos = CSViewCall.call(this, "SS.FamilySVC.queryImsMainProductInfo", data);
		if (IDataUtil.isNotEmpty(userProductInfos)) {
			IData userProductInfo = userProductInfos.first();
			String brandCode = userProductInfo.getString("BRAND_CODE");
			String productName = userProductInfo.getString("RSRV_STR5");
			imsProd.put("BRAND_NAME", brandCode);
			imsProd.put("PRODUCT_NAME", productName);
			imsProd.put("PRODUCT_ID", userProductInfo.getString("PRODUCT_ID", ""));
			imsProd.put("MOBILE_PRODUCT_ID",userProductInfo.getString("MOBILE_PRODUCT_ID"));
		}
		setAjax(imsProd);
	}
	/**
	* @author yuyz
	* @Description 释放固话号码预占
	* @Date 19:51 2020/8/3
	* @Param []
	* @return void
	**/
	public void relaseImsOccupy()throws Exception{
		IData data = getData();
		String fixNum = data.getString("FIX_NUMBER", "");
		data.put("FIX_NUMBER", "0898"+fixNum);
		IDataset results = CSViewCall.call(this, "SS.FamilySVC.relaseImsOccupy", data);
		setAjax(results.first());
	}
}
