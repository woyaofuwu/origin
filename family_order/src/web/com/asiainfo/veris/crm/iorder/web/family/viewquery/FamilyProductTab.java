package com.asiainfo.veris.crm.iorder.web.family.viewquery;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.pub.consts.View360Const;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 *
 * @author zhangxi
 *
 */
public abstract class FamilyProductTab extends PersonBasePage {

	/**
	 * 家庭产品基础信息查询
	 * @param cycle
	 * @throws Exception
	 */
	public void queryFamilyInfo(IRequestCycle cycle) throws Exception {

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




	public abstract void setProductInfos(IDataset productInfos);
    public abstract void setSvcInfos(IDataset svcInfos);
    public abstract void setDiscntInfos(IDataset discntInfos);
    public abstract void setPlatInfos(IDataset platInfos);

}
