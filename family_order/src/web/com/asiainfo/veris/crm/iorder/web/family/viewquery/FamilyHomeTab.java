package com.asiainfo.veris.crm.iorder.web.family.viewquery;

import org.apache.log4j.Logger;
import org.apache.tapestry.IRequestCycle;

import com.ailk.bizcommon.route.Route;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.pub.consts.IUpcConst;
import com.asiainfo.veris.crm.iorder.pub.consts.View360Const;
import com.asiainfo.veris.crm.iorder.pub.family.consts.Family360Constants;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.upc.UpcViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 *
 * @author zhangxi
 *
 */
public abstract class FamilyHomeTab extends PersonBasePage {

	private static Logger log = Logger.getLogger(FamilyHomeTab.class);

	/**
	 * 家庭基础信息查询
	 * @param cycle
	 * @throws Exception
	 */
	public void queryFamilyInfo(IRequestCycle cycle) throws Exception {

		IData param = getData();

		String normalFamily = param.getString("NORMAL_FAMILY_CHECK");
		if("on".equals(normalFamily)){
			param.put("REMOVE_TAG", "0");
		}

		IData custResults = CSViewCall.callone(this, "SS.GetFamily360ViewSVC.qryFamilyInfoBySerialNumber", param);
		IData baseResults = CSViewCall.callone(this, "SS.GetFamily360ViewSVC.queryFamilyBaseInfo", param);

		IDataset familyCustInfoSet = new DatasetList();
		IData familyCustInfo = new DataMap();

		if(DataUtils.isNotEmpty(custResults)){
			familyCustInfoSet = (DatasetList)custResults.get("FAMILY_CUST_INFO_SET");
		}

		if(DataUtils.isNotEmpty(familyCustInfoSet)){
			familyCustInfo = familyCustInfoSet.first();
		}

		IData familyProductInfo = new DataMap();
		if(DataUtils.isNotEmpty(baseResults)){
			//填充产品信息
			fillProductInfo(baseResults,familyProductInfo);

		}

		setFamilyCustInfo(familyCustInfo);
		setFamilyProductInfo(familyProductInfo);
	}

	//填充产品信息
	private void fillProductInfo(IData baseResults, IData familyProductInfo) throws Exception {

		// 翻译产品名称 品牌名称
        String productId = baseResults.getString("PRODUCT_ID","");
        String brandCode = baseResults.getString("BRAND_CODE","");

        if (StringUtils.isNotBlank(productId) || StringUtils.isNotBlank(brandCode)) {
            IData param = new DataMap();

            familyProductInfo.put("PRODUCT_ID",productId);
            familyProductInfo.put("BRAND_CODE",brandCode);

            param.put("PRODUCT_ID", productId);
            param.put("BRAND_CODE", brandCode);
            IData result = CSViewCall.callone(this, "SS.CreateRedMemberSVC.getUserName", param);
            if (IDataUtil.isNotEmpty(result)) {
            	familyProductInfo.put("PRODUCT_NAME", result.getString("PRODUCT_NAME"));
            	familyProductInfo.put("BRAND_NAME", result.getString("BRAND_NAME"));
            }
        }

        // 产品描述
        if (StringUtils.isNotBlank(productId)) {
            String productDesc = UpcViewCall.queryOfferExplainByOfferId(this, IUpcConst.ELEMENT_TYPE_CODE_PRODUCT, productId);
            familyProductInfo.put("PRODUCT_DESC", productDesc);
        }

        // 翻译下月产品名称 品牌名称
        String nextProductId = baseResults.getString("B_PRODUCT_ID","");
        String nextBrandCode = baseResults.getString("B_BRAND_CODE","");
        if (StringUtils.isNotBlank(nextProductId) || StringUtils.isNotBlank(nextBrandCode)) {
            IData param2 = new DataMap();
            param2.put("PRODUCT_ID", nextProductId);
            param2.put("BRAND_CODE", nextBrandCode);
            IData result2 = CSViewCall.callone(this, "SS.CreateRedMemberSVC.getUserName", param2);
            if (IDataUtil.isNotEmpty(result2)) {
            	familyProductInfo.put("B_PRODUCT_NAME", result2.getString("PRODUCT_NAME"));
            	familyProductInfo.put("B_BRAND_NAME", result2.getString("BRAND_NAME"));
            }
        }

	}

	/**
	 * 家庭成员关系查询
	 * @param cycle
	 * @throws Exception
	 */
	public void querySubFamilyInfos(IRequestCycle cycle) throws Exception {

		IData param = getData();
		param.put(Route.ROUTE_EPARCHY_CODE, param.getString("EPARCHY_CODE"));

		IDataset memberResults = CSViewCall.call(this, "SS.GetFamily360ViewSVC.queryMemberInfo", param);

		setFamilyMemberInfos(memberResults);
	}

	/**
	 * 查询我的家消费记录和套餐余量
	 * @param cycle
	 * @throws Exception
	 */
	public void acctInfoQuery(IRequestCycle cycle) throws Exception {

		if(log.isDebugEnabled()){
			log.debug("查询我的家消费记录和套餐余量web start...");
		}

		IData param = getData();

		String queryArea = param.getString("QUERY_AREA", "");

		if(USER_BILL.equals(queryArea)){

            IDataset billInfos = new DatasetList();

            billInfos = CSViewCall.call(this, "SS.GetUser360ViewSVC.queryUserBillInfo", param);


            IData month = new DataMap();

//            IData billInfo1 = new DataMap();
//            billInfo1.put("INTEGRATE_ITEM_CODE", "1");
//            billInfo1.put("INTEGRATE_ITEM", "套餐及固定费用");
//            billInfo1.put("MONTH_BEFORE_LAST", "202005");
//            billInfo1.put("BEFORE_LAST_FEE", "18800");
//            billInfo1.put("LAST_MONTH", "202006");
//            billInfo1.put("LAST_FEE", "18800");
//            billInfo1.put("THIS_MONTH", "202007");
//            billInfo1.put("NOW_FEE", "17300");
//
//            billInfos.add(billInfo1);

            month.put("MONTH_BEFORE_LAST", billInfos.first().getString("MONTH_BEFORE_LAST"));
            month.put("LAST_MONTH", billInfos.first().getString("LAST_MONTH"));
            month.put("THIS_MONTH", billInfos.first().getString("THIS_MONTH"));

            setMonth(month);
            setBillInfos(billInfos);

		}
		else if(USER_ALLOWANCE.equals(queryArea)){
			IData usageInfo = new DataMap();

			//usageInfo = CSViewCall.callone(this, "SS.GetUser360ViewSVC.queryUserConsumptionInfo", param);

			usageInfo.put("GPRS_TOTAL", "40000");
			usageInfo.put("GPRS_USED", "24000");
			usageInfo.put("GPRS_BALANCE", "17000");
			usageInfo.put("GPRS_RATE", "0.6");

			usageInfo.put("CALL_TOTAL", "40000");
			usageInfo.put("CALL_USED", "24000");
			usageInfo.put("CALL_BALANCE", "17000");
			usageInfo.put("CALL_RATE", "0.6");

			usageInfo.put("TEXT_TOTAL", "500");
			usageInfo.put("TEXT_USED", "200");
			usageInfo.put("TEXT_BALANCE", "300");
			usageInfo.put("TEXT_RATE", "0.4");

			usageInfo.put("WLAN_TOTAL", "40000");
			usageInfo.put("WLAN_USED", "24000");
			usageInfo.put("WLAN_BALANCE", "17000");
			usageInfo.put("WLAN_RATE", "0.6");

			setUsageInfo(usageInfo);
		}

		if(log.isDebugEnabled()){
			log.debug("查询我的家消费记录和套餐余量web end...");
		}
	}

	/**
	 * 查询家庭成员信息
	 * @param cycle
	 * @throws Exception
	 */
	public void queryFamilyMemberInfo(IRequestCycle cycle) throws Exception {
		IData data = getData();

		// 如果查询所有信息复选框勾选，将 SelectTag = 1 传入后台
		String selectTag = "true".equals(data.getString("QUERY_ALL", "false")) ? "1" : "0";
		data.put("SelectTag", selectTag);

		if (StringUtils.isNotBlank(data.getString("USER_ID", ""))) {
			data.put(Route.ROUTE_EPARCHY_CODE, data.getString("EPARCHY_CODE", getTradeEparchyCode()));
			IDataset productInfo = CSViewCall.call(this, "SS.GetUser360ViewSVC.qryProductInfo", data);
			IDataset svcInfo = CSViewCall.call(this, "SS.GetUser360ViewSVC.qryUserSvcInfo", data);
			IDataset discntInfo = CSViewCall.call(this, "SS.GetUser360ViewSVC.qryUserDiscntInfo", data);
			IDataset platInfo;
			if ("1".equals(selectTag)) {
				platInfo = CSViewCall.call(this, "CS.PlatComponentSVC.getUserPlatSvcs12", data);
			} else {
				platInfo = CSViewCall.call(this, "CS.PlatComponentSVC.getUserPlatSvcs11", data);
			}

			if (IDataUtil.isNotEmpty(platInfo)) {
				for (Object obj : platInfo) {
					IData plat = (IData) obj;
					String bizStateColor = View360Const.PLATSVC_COMMON
							+ View360Const.PLATSVC_COLOR.get(plat.getString("BIZ_STATE_CODE"));
					plat.put("BIZ_STATE_COLOR", bizStateColor);
				}
			}

			IDataset returnProductList = new DatasetList();
			IDataset configList = pageutil.getStaticList("OFFER_CODE_NO_DISPLAY_MODE");
			for (int i = 0; i < productInfo.size(); i++) {
				IData productData = productInfo.getData(i);
				String productId = productData.getString("PRODUCT_ID");
				if (IDataUtil.isNotEmpty(DataHelper.filter(configList, "DATA_ID=" + productId))) {// 是否存在
																									// 在则不显示
					continue;

				}
				returnProductList.add(productData);
			}

			setProductInfos(returnProductList);
			setSvcInfos(svcInfo);
            setDiscntInfos(discntInfo);
            setPlatInfos(platInfo);
		}
	}

	/**
	 * 获取图标
	 * @param index
	 * @return
	 */
	public String imgClassSelector(int index) {
		return Family360Constants.BILL_COMMON + Family360Constants.BILL_IMG.get(index);
	}

    private static final String USER_BILL = "billTablePart";
    private static final String USER_ALLOWANCE = "allowancePart";

    public abstract void setMonth(IData month);
    public abstract void setBillInfos(IDataset billInfos);
    public abstract void setUsageInfo(IData usageInfo);
    public abstract void setFamilyCustInfo(IData familyCustInfo);
    public abstract void setFamilyProductInfo(IData familyProductInfo);
    public abstract void setFamilyMemberInfos(IDataset familyMemberInfos);
	public abstract void setProductInfos(IDataset productInfos);
    public abstract void setSvcInfos(IDataset svcInfos);
    public abstract void setDiscntInfos(IDataset discntInfos);
    public abstract void setPlatInfos(IDataset platInfos);

}
