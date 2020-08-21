package com.asiainfo.veris.crm.order.soa.person.busi.bat;

import java.util.ArrayList;
import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.rule.BizRule;

public class BatTaskCreateSVC extends CSBizService
{

	/**
	 * @Fields serialVersionUID :
	 */
	private static final long serialVersionUID = -1756769134328846009L;

	private IData createRuleData(IData data) throws Exception
	{
		String tradeDataStr = data.getString("tradeData");
		if (tradeDataStr == null)
		{
			return new DataMap();
		}

		IDataset td = new DatasetList(tradeDataStr);

		IData result = new DataMap();

		String eparchyCode = getVisit().getStaffEparchyCode();
		eparchyCode = eparchyCode == null ? data.getString("TRADE_EPARCHY_CODE") : eparchyCode;
		result.put("TRADE_EPARCHY_CODE", eparchyCode);
		result.put("USER_ID", td.getData(0).getString("USER_ID"));
		result.put("BRAND_CODE", td.getData(0).getString("BRAND_CODE"));
		result.put("PRODUCT_ID", td.getData(0).getString("PRODUCT_ID"));
		result.put("PRODUCT_ID_NEW", data.getString("PRODUCT_ID", ""));
		result.put("PACKAGE_ID_NEW", data.getString("PACKAGE_ID", ""));

		String strVipClass = td.getData(0).getString("VIP_TYPE_CODE", "") + td.getData(0).getString("CLASS_ID", "");
		if ("".equals(strVipClass))
		{
			strVipClass = "***";
		}
		result.put("VIP_LEVEL", strVipClass);
		result.put("VIP_TYPE", strVipClass);
		result.put("OPEN_DATE", td.getData(0).getString("OPEN_DATE"));

		IDataset ds = null;
		if (ds == null || ds.size() == 0)
		{
			result.put("BRAND_NO", "***");
		} else
		{
			result.put("BRAND_NO", ds.getData(0).getString("BRAND_NO", ""));
		}

		return result;
	}

	public IDataset getElementInfo(IData data) throws Exception
	{
		String userId = data.getString("USER_ID");
		return UserSvcInfoQry.getElementInfo(userId);
	}

	/**
	 * @author yanwu
	 * 
	 * @param data
	 * @return 生成个人产品树数据 for 开户 指定了某个产品集合的情况下调用该方法 在组件中传入customProducts参数
	 * @throws Exception
	 */
	public IDataset loadProductsForBatM2M(IData data) throws Exception
	{
		String productTypeCode = data.getString("PRODUCT_TYPE_CODE");
		if (productTypeCode == null || "".equals(productTypeCode.trim()))
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "751001:个人产品树缺少产品类型[PRODUCT_TYPE_CODE:" + productTypeCode + "]");
		}

		IDataset customProductsTmp = ProductInfoQry.getProductsByTypeForMallNoPriv(productTypeCode, getVisit().getStaffEparchyCode());

		IDataset customProducts2 = seFilterEnableProduct(customProductsTmp, data);
		String strAttr = data.getString("PARAM_ATTR", "1994");
		IDataset customProducts = new DatasetList();
		IDataset filterProducts = CommparaInfoQry.getCommparaByParaAttr("CSM", strAttr, getVisit().getStaffEparchyCode());
		if (filterProducts != null && filterProducts.size() > 0)
		{
			for (int j = 0; j < customProducts2.size(); j++)
			{
				for (int i = 0; i < filterProducts.size(); i++)
				{
					if (customProducts2.getData(j).getString("PACKAGE_ID").equals(filterProducts.getData(i).getString("PARA_CODE1")))
					{
						customProducts.add(customProducts2.getData(j));
					}
				}
			}
		}

		return customProducts;
	}

	/*
	 * 生成个人产品树数据 for 开户 指定了某个产品集合的情况下调用该方法 在组件中传入customProducts参数
	 */
	public IDataset loadPersonProductsTreeForBatCreateuser(IData data) throws Exception
	{
		String productTypeCode = data.getString("PRODUCT_TYPE_CODE");
		// setEparchyCode(getVisit().getStaffEparchyCode());
		if (productTypeCode == null || "".equals(productTypeCode.trim()))
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "751001:个人产品树缺少产品类型[PRODUCT_TYPE_CODE:" + productTypeCode + "]");
		}

		// xiekl 物联网处理
		if (productTypeCode.equals("PWLW"))
		{
			return new DatasetList();
		}// 物联网end

		// String checkboxAction = data.getString("CHECKBOX_ACTION");
		// String textAction = data.getString("TEXT_ACTION");
		// IDataset disabledProducts = data.getDataset("DISABLED_PRODUCTS");
		// IData productType =
		// ProductTypeInfoQry.getProductTypeByCode(productTypeCode);

		IDataset customProductsTmp = ProductInfoQry.getProductsByTypeForMallNoPriv(productTypeCode, getVisit().getStaffEparchyCode());
		// IDataset customProductsTmp2 = new DatasetList();
		// if(IDataUtil.isNotEmpty(customProductsTmp)){
		// for(int i=0;i<customProductsTmp.size();i++){
		// IData customProduct = customProductsTmp.getData(i);
		// String productId = customProduct.getString("PRODUCT_ID");
		// if (StaffPrivUtil.isProdPriv(getVisit().getStaffId(), productId))
		// {
		// customProductsTmp2.add(customProduct);
		// // }
		// }
		// }

		IDataset customProducts2 = seFilterEnableProduct(customProductsTmp, data);

		/*----------modify by chenzg@20131118---卡号激活流程以及号码批量预开进行品牌套餐限制(REQ201311120008)----begin----*/
		/**
		 * 新增产品类型对应只能选择主套餐配置处理 请业务支撑部对号码批量预开界面号码可预配的品牌套餐进行限定，仅允许配以下2种品牌套餐：
		 * 全球通标准套餐、神州行标准套餐
		 */
		IDataset customProducts = new DatasetList();
		IDataset filterProducts = CommparaInfoQry.getCommparaByParaAttr("CSM", "1993", getVisit().getStaffEparchyCode());
		if (filterProducts != null && filterProducts.size() > 0)
		{
			for (int j = 0; j < customProducts2.size(); j++)
			{
				for (int i = 0; i < filterProducts.size(); i++)
				{
					if (customProducts2.getData(j).getString("PACKAGE_ID").equals(filterProducts.getData(i).getString("PARA_CODE1")))
					{
						customProducts.add(customProducts2.getData(j));
					}
				}
			}
		} else
		{
			filterProducts = CommparaInfoQry.getCommparaByParaAttr("CSM", "993", getVisit().getStaffEparchyCode());
			data.put("FILTER_PRODUCTS", filterProducts);

			for (int j = 0; j < customProducts2.size(); j++)
			{
				boolean flag = true;
				for (int i = 0; i < filterProducts.size(); i++)
				{
					if (customProducts2.getData(j).getString("PACKAGE_ID").equals(filterProducts.getData(i).getString("PARA_CODE1")))
					{
						flag = false;
					}
				}
				if (flag)
				{
					customProducts.add(customProducts2.getData(j));
				}
			}
		}
		/*----------modify by chenzg@20131118---卡号激活流程以及号码批量预开进行品牌套餐限制(REQ201311120008)----end------*/
		// setProductsNode(node0,customProducts2,disabledProducts,checkboxAction,textAction,data);

		return customProducts;
	}

	private IData seFilteerEnableElement(IData inData) throws Exception
	{
		IDataset listResult = inData.getDataset("ELEMENTS") == null ? new DatasetList() : inData.getDataset("ELEMENTS");
		inData.put("RESULT", listResult);
		return inData;
	}

	private IDataset seFilterEnableProduct(IDataset tmp, IData data) throws Exception
	{
		IData tempData = createRuleData(data);
		tempData.put("ELEMENTS", tmp);
		tempData = this.seFilteerEnableElement(tempData);

		return tempData.getDataset("RESULT");
	}

	public IDataset checkProducts(IData data) throws Exception
	{
		IDataset elements = new DatasetList();
		IData returnInfo = new DataMap();
		returnInfo.put("RESULT_CODE", "1");
		String eparchyCode = data.getString("EPARCHY_CODE");
		this.setRouteId(eparchyCode);

		// String curTime = SysDateMgr.getSysTime();
		// 产品元素依赖互斥校验，需要模拟台帐数据
		List<String> indexes = new ArrayList<String>();
		if (!"".equals(data.getString("TRADE_TYPE_CODE", "")))
		{
			IDataset tradeSvcs = new DatasetList();
			IDataset tradeAttrs = new DatasetList();
			IDataset tradeDiscnts = new DatasetList();
			IDataset userSvcs = new DatasetList();
			IDataset userAttrs = new DatasetList();
			IDataset userDiscnts = new DatasetList();

			// 调用规则前组织数据
			IDataset userElements = new DatasetList(data.getString("SELECTED_ELEMENTS"));
			if (userElements != null && userElements.size() > 0)
			{
				int size = userElements.size();
				for (int i = 0; i < size; i++)
				{
					IData element = userElements.getData(i);
					String itemIndex = element.getString("ITEM_INDEX", "");
					IData ruleElement = new DataMap();
					ruleElement.put("USER_ID_A", "-1");// 给规则用
					if ("0_1".equals(element.getString("MODIFY_TAG")))
					{
						continue;
					}

					if (indexes.contains(itemIndex))
					{
						continue;
					}

					String startDate = element.getString("START_DATE");
					String startDateS = startDate;
					if (StringUtils.isNotEmpty(startDate) && startDate.length() > 10)
					{
						startDateS = startDate.substring(0, 10);
						startDateS += SysDateMgr.START_DATE_FOREVER;
					}
					element.put("START_DATE", startDateS);

					String attrParamsStr = element.getString("ATTR_PARAM");
					IDataset attrParam = new DatasetList();
					if (StringUtils.isNotEmpty(attrParamsStr))
					{
						attrParam = new DatasetList(attrParamsStr);
						if (IDataUtil.isNotEmpty(attrParam))
						{
							for (int t = 0; t < attrParam.size(); t++)
							{
								IData ruleattr = new DataMap();
								ruleattr.putAll(attrParam.getData(t));
								ruleattr.put("START_DATE", startDateS);
								ruleattr.put("END_DATE", element.getString("END_DATE"));
								ruleattr.put("MODIFY_TAG", element.getString("MODIFY_TAG"));
								ruleattr.put("USER_ID", data.getString("USER_ID", "-1"));
								ruleattr.put("RELA_INST_ID", element.getString("INST_ID"));
								ruleattr.put("INST_TYPE", element.getString("ELEMENT_TYPE_CODE"));
								tradeAttrs.add(ruleattr);
								userAttrs.add(ruleattr);
							}
						}
					}

					if (BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(element.getString("ELEMENT_TYPE_CODE")))
					{
						ruleElement.put("USER_ID", data.getString("USER_ID", "-1"));
						ruleElement.put("DISCNT_CODE", element.getString("ELEMENT_ID"));

						ruleElement.putAll(element);
						if (StringUtils.isNotBlank(element.getString("PRODUCT_ID")) && StringUtils.isNotBlank(element.getString("PACKAGE_ID")))
						{
							ruleElement.put("PRODUCT_ID", element.getString("PRODUCT_ID"));
							ruleElement.put("PACKAGE_ID", element.getString("PACKAGE_ID"));
						}
						tradeDiscnts.add(ruleElement);
						userDiscnts.add(ruleElement);
					} else if (BofConst.ELEMENT_TYPE_CODE_SVC.equals(element.getString("ELEMENT_TYPE_CODE")))
					{
						ruleElement.put("USER_ID", data.getString("USER_ID", "-1"));
						ruleElement.put("SERVICE_ID", element.getString("ELEMENT_ID"));

						ruleElement.putAll(element);
						if (StringUtils.isNotBlank(element.getString("PRODUCT_ID")) && StringUtils.isNotBlank(element.getString("PACKAGE_ID")))
						{
							ruleElement.put("PRODUCT_ID", element.getString("PRODUCT_ID"));
							ruleElement.put("PACKAGE_ID", element.getString("PACKAGE_ID"));
						}
						tradeSvcs.add(ruleElement);
						userSvcs.add(ruleElement);
					}
				}
			}

			// 规则调用
			IDataset tradeMains = new DatasetList();
			IData tradeMain = new DataMap();
			tradeMain.put("PRODUCT_TYPE_CODE", data.getString("PRODUCT_TYPE_CODE"));
			tradeMain.put("NEW_PRODUCT_ID", data.getString("NEW_PRODUCT_ID"));
			tradeMain.put("TRADE_EPARCHY_CODE", eparchyCode);
			tradeMain.put("TRADE_TYPE_CODE", data.getString("TRADE_TYPE_CODE"));
			tradeMain.put("IN_MODE_CODE", "0");
			tradeMain.put("USER_ID", data.getString("USER_ID", "-1"));
			tradeMains.add(tradeMain);
			IData ruleParam = new DataMap();
			ruleParam.put("TF_B_TRADE_SVC", tradeSvcs);
			ruleParam.put("TF_B_TRADE_ATTR", tradeAttrs);
			ruleParam.put("TF_B_TRADE_DISCNT", tradeDiscnts);
			ruleParam.put("TF_F_USER_SVC_AFTER", userSvcs);
			ruleParam.put("TF_F_USER_ATTR_AFTER", userAttrs);
			ruleParam.put("TF_F_USER_DISCNT_AFTER", userDiscnts);
			ruleParam.put("IS_COMPONENT", "false");
			ruleParam.put("TF_B_TRADE", tradeMains);
			IData result = BizRule.bre4ProductLimitNeedFormat(ruleParam);
			if (IDataUtil.isNotEmpty(result))
			{
				IDataset errors = result.getDataset("TIPS_TYPE_ERROR");
				if (IDataUtil.isNotEmpty(errors))
				{
					int errorSize = errors.size();
					StringBuilder errorInfo = new StringBuilder();
					for (int i = 0; i < errorSize; i++)
					{
						IData error = errors.getData(i);
						errorInfo.append(error.getString("TIPS_INFO") + "</br>");
					}
					returnInfo.put("ERROR_INFO", errorInfo.toString());
					returnInfo.put("RESULT_CODE", "2");
				}
			}
			elements.add(returnInfo);
			return elements;
		}
		elements.add(returnInfo);
		return elements;
	}

	/**
	 * 联系号码校验接口 @yanwu
	 * 
	 * @param cycle
	 * @throws Exception
	 */
	public IDataset checkPhone(IData param) throws Exception
	{
		IDataset elements = new DatasetList();
		String sn = param.getString("PHONE", "");
		IData data = null;
		try
		{
			data = UcaInfoQry.qryUserInfoBySn(sn);
			if (IDataUtil.isNotEmpty(data))
			{
				data.put("X_RESULT_CODE", "0");
				data.put("X_RESULTINFO", "ok");
			} else
			{
				data = new DataMap();
				data.put("X_RESULT_CODE", "-1");
				data.put("X_RESULTINFO", "联系号码必须是有效的移动号码!");
			}
		} catch (Exception e)
		{
			data = new DataMap();
			data.put("X_RESULT_CODE", "-1");
			data.put("X_RESULTINFO", "联系号码必须是有效的移动号码!");
		}
		elements.add(data);
		return elements;
	}
}
