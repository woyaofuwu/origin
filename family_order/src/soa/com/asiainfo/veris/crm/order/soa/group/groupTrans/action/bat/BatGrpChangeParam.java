package com.asiainfo.veris.crm.order.soa.group.groupTrans.action.bat;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.BatException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.group.common.base.trans.ITrans;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserGrpPlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;

public class BatGrpChangeParam implements ITrans {
	@Override
	public void transRequestData(IData batData) throws Exception {
		// 初始化数据

		// 校验请求参数
		checkRequestDataSub(batData);

		// 构建服务请求数据
		builderSvcData(batData);

		// 根据条件判断调用服务
		setSVC(batData);
	}

	protected void checkRequestDataSub(IData batData) throws Exception {

		IData condData = batData.getData("condData", new DataMap());
		String opercode = IDataUtil.chkParam(condData, "OPERCODE");
		// 集团编码和产品编码不能为空
		if (StringUtils.isBlank(batData.getString("DATA1"))
				|| StringUtils.isBlank(batData.getString("DATA2"))) {
			CSAppException.apperr(BatException.CRM_BAT_100);
		}
		// 集团产品资料变更每日最大短信数,每月最大短信数,企业签名不全为空
		if ("08".equals(opercode)
				&& StringUtils.isBlank(batData.getString("DATA3"))
				&& StringUtils.isBlank(batData.getString("DATA4"))
				&& StringUtils.isBlank(batData.getString("DATA5"))) {
			CSAppException.apperr(BatException.CRM_BAT_101);
		}

		String groupId = batData.getString("DATA1");
		String productId = batData.getString("DATA2");

		IData data = new DataMap();
		data.put("GROUP_ID", groupId);

		boolean bFind = false;
		IDataset serviceInfos = UserGrpPlatSvcInfoQry
				.getGrpPackagePlatService(groupId);
		for (int i = 0; i < serviceInfos.size(); i++) {
			IData serviceData = serviceInfos.getData(i);
			if (IDataUtil.isNotEmpty(serviceData)) {
				String sProductId = serviceData.getString("PRODUCT_ID");
				if (sProductId.equals(productId)) {
					bFind = true;
					break;
				}

			}
		}
		if (!bFind) {
			CSAppException.apperr(BatException.CRM_BAT_102);
		}
	}

	protected void builderSvcData(IData batData) throws Exception {
		IData svcData = batData.getData("svcData", new DataMap());

		String groupid = batData.getString("DATA1");
		String productid = batData.getString("DATA2");
		String maxPerDay = batData.getString("DATA3");
		String maxMonDay = batData.getString("DATA4");
		String textSign = batData.getString("DATA5");
		String rsrvtag2 = "普通级";
		String maxNumber = "100000";

		IDataset serviceInfos = UserGrpPlatSvcInfoQry
				.getGrpPackagePlatService(groupid);
		IData groupdata = UcaInfoQry.qryGrpInfoByGrpId(groupid);
		if (IDataUtil.isNotEmpty(groupdata)) {
			String classid = groupdata.getString("CLASS_ID");
			if (classid == "5" || classid == "6" || classid == "A1"
					|| classid == "A2") {
				rsrvtag2 = "金牌级";
				maxNumber = "5000000";
			}
			if (classid == "7" || classid == "8" || classid == "B1"
					|| classid == "B2") {
				rsrvtag2 = "银牌级";
				maxNumber = "1000000";
			}
			if (StringUtils.isNotBlank(batData.getString("DATA3"))) {
				if (Integer.valueOf(batData.getString("DATA3")) > Integer
						.valueOf(maxNumber)) {
					CSAppException.apperr(BatException.CRM_BAT_105, rsrvtag2);

				}

			}
			if (StringUtils.isNotBlank(batData.getString("DATA4"))) {
				if (Integer.valueOf(batData.getString("DATA4")) > Integer
						.valueOf(maxNumber)) {
					CSAppException.apperr(BatException.CRM_BAT_105, rsrvtag2);

				}

			}

		}
		String sProductId = "";
		String sUserId = "";

		IDataset modElements = new DatasetList();
		for (int i = 0; i < serviceInfos.size(); i++) {
			IData serviceData = serviceInfos.getData(i);
			if (IDataUtil.isNotEmpty(serviceData)) {
				sProductId = serviceData.getString("PRODUCT_ID");
				
				String sServiceId = serviceData.getString("SERVICE_ID");
				if (sProductId.equals(productid)) {
					sUserId = serviceData.getString("USER_ID");
					IDataset userElements = new DatasetList();
					IData modElement = new DataMap();
                     
					userElements = UserSvcInfoQry.getGrpSvcInfoByUserId(
							sUserId, sServiceId);
					if (IDataUtil.isNotEmpty(userElements)) {
						IData userElement = userElements.getData(0);
						IData serviceInparam = new DataMap();
						serviceInparam.put("USER_ID", sUserId);
						serviceInparam.put("PRODUCT_ID", sProductId);
						serviceInparam.put("PACKAGE_ID",
								userElement.getString("PACKAGE_ID", ""));
						serviceInparam.put("SERVICE_ID", sServiceId);
						serviceInparam.put("EPARCHY_CODE", "0898");
						IDataset serviceparamset = CSAppCall
								.call("CS.DealSpecialServerParamSvc.loadSpecialServerParam",
										serviceInparam);
						IData serviceparam = serviceparamset.getData(1);
						serviceparam.put("CANCLE_FLAG", "false");
						if (StringUtils.isNotBlank(maxPerDay)) {
							serviceparam.getData("PLATSVC").put(
									"pam_MAX_ITEM_PRE_DAY", maxPerDay);

						}
						if (StringUtils.isNotBlank(maxMonDay)) {
							serviceparam.getData("PLATSVC").put(
									"pam_MAX_ITEM_PRE_MON", maxMonDay);

						}
						if (StringUtils.isNotBlank(textSign)) {
							serviceparam.getData("PLATSVC").put(
									"pam_TEXT_ECGN_ZH", textSign);

						}
						serviceparam.getData("PLATSVC").put("pam_OPER_STATE",
								"08");

						modElement.put("INST_ID",
								userElement.getString("INST_ID"));
						modElement.put("START_DATE",
								userElement.getString("START_DATE"));
						modElement.put("ELEMENT_TYPE_CODE", "S");
						modElement.put("MODIFY_TAG", "2");
						modElement.put("PRODUCT_ID", sProductId);
						modElement.put("END_DATE",
								userElement.getString("END_DATE"));
						modElement.put("ATTR_PARAM", serviceparamset);
						modElement.put("PACKAGE_ID",
								userElement.getString("PACKAGE_ID"));
						modElement.put("ELEMENT_ID", sServiceId);
						modElements.add(modElement);
					} else {
						CSAppException.apperr(BatException.CRM_BAT_104);

					}
				}

			}
		}
		if("".equals(sUserId))
		{
			CSAppException.apperr(BatException.CRM_BAT_104);
			
		}
		svcData.put("PRODUCT_ID", productid);
		svcData.put("USER_ID", sUserId);
		svcData.put("BUSI_CTRL_TYPE", "ChgUs");
		svcData.put("USER_EPARCHY_CODE", "0898");
		svcData.put("ELEMENT_INFO", modElements);
	}

	// 根据条件判断调用服务
	protected void setSVC(IData batData) throws Exception {
		String svcName = "";
		IData condData = batData.getData("condData", new DataMap());
		IData svcData = batData.getData("svcData", new DataMap());

		String operType = IDataUtil.chkParam(condData, "OPERCODE");

		if ("08".equals(operType)) {// 资料变更
			svcName = "CS.ChangeUserElementSvc.changeUserElement";
		} else {
			CSAppException.apperr(BatException.CRM_BAT_103);
		}

		svcData.put("REAL_SVC_NAME", svcName);
	}
}
