package com.asiainfo.veris.crm.order.soa.frame.csservice.common.component.product;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.ProductException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.group.common.GroupBaseConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.util.DataSetUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.Clone;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UElementLimitInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UPackageElementInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductCompInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductMebInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.USvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.ProductModuleData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.DiscntData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.PlatSvcData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.SvcData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.util.ProductModuleCalDate;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.util.ProductModuleCalDateExtend;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.util.ProductTimeEnv;
import com.asiainfo.veris.crm.order.soa.frame.bof.query.BofQuery;
import com.asiainfo.veris.crm.order.soa.frame.bof.util.OfferUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustVipInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.AttrBizInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.AttrItemInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductFeeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductPkgInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ServiceResInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserPlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.utils.ProductUtils;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.ElementUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.GroupTradeAfterUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.rule.BizRule;

public class SelectedElementSVC extends CSBizService
{
	private static final Logger log = Logger.getLogger(SelectedElementSVC.class);

	/*
	 * @BBOSS侧的集团受理与变更使用了同一个参数页面，页面中没有分情况处理产品信息，因此要在这里分情况处理 @author xunyl @Date
	 * 2013-05-16
	 */
	public IDataset dealBBossElements(IData param) throws Exception
	{

		// 1 定义返回值

		IDataset returnVal = new DatasetList();

		// 2 获取传入参数值

		boolean isReOpen = param.getBoolean("IS_REOPEN");// 是否重新打开标志，true表示页面是同一次操作再次被访问
		String productOperType = param.getString("PRODUCT_OPER_TYPE");// 产品操作类型
		String productId = param.getString("PRODUCT_ID");// 产品编号

		// 3 处理不同操作下的元素
		IData inparam = new DataMap();
		if ((GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_ADD.getValue().equals(productOperType) || GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_PREDEAL.getValue().equals(productOperType)) && isReOpen == false)
		{// 产品新增或者预受理，初次访问

			inparam.put("PRODUCT_ID", productId);
			returnVal = this.getGrpUserOpenElements(param);
		} else if ((GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_ADD.getValue().equals(productOperType) || GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_PREDEAL.getValue().equals(productOperType)) && isReOpen == true)
		{// 产品新增或者预受理，再次访问

			IDataset tempProductElements = param.getDataset("TEMP_PRODUCTS_ELEMENT");
			IDataset resultList = new DatasetList();
			IData result = new DataMap();
			result.put("SELECTED_ELEMENTS", tempProductElements);
			resultList.add(result);
			return resultList;
		} else if (isReOpen == false)
		{// 产品变更，初次访问

			String userId = param.getString("USER_ID");// 产品用户编号
			inparam.put("PRODUCT_ID", productId);
			inparam.put("USER_ID", userId);
			returnVal = this.getGrpUserChgElements(inparam);
		} else
		{// 产品变更，再次访问

			IDataset tempProductElements = param.getDataset("TEMP_PRODUCTS_ELEMENT");
			IDataset resultList = new DatasetList();
			IData result = new DataMap();
			result.put("SELECTED_ELEMENTS", tempProductElements);
			resultList.add(result);
			return resultList;
		}

		return returnVal;
	}

	/**
	 * @Description: 费用处理【SERVICE_ID=19】
	 * @param userId
	 * @param elementId
	 * @param elementTypeCode
	 * @param userElements
	 * @return
	 * @throws Exception
	 * @author: maoke
	 * @date: Jul 2, 2014 2:56:19 PM
	 */
	public IData dealFeeByServiceId19(String userId, String elementId, String elementTypeCode, IDataset userElements) throws Exception
	{
		String staffId = CSBizBean.getVisit().getStaffId();

		IData result = new DataMap();
		result.clear();

		if ("19".equals(elementId) && BofConst.ELEMENT_TYPE_CODE_SVC.equals(elementTypeCode))
		{
			// 10086客服客服工号不显示押金发票号码
			if (staffId.indexOf("HNYD") != -1)
			{
				return result;
			}
			if (IDataUtil.isNotEmpty(userElements))
			{
				for (int i = 0, size = userElements.size(); i < size; i++)
				{
					IData user = userElements.getData(i);

					String tempElementId = user.getString("ELEMENT_ID");
					String tempElementTypeCode = user.getString("ELEMENT_TYPE_CODE");
					String modifyTag = user.getString("MODIFY_TAG");
					String startDate = SysDateMgr.decodeTimestamp(user.getString("START_DATE"), SysDateMgr.PATTERN_STAND_YYYYMMDD);

					if (BofConst.ELEMENT_TYPE_CODE_SVC.equals(tempElementTypeCode) && "15".equals(tempElementId) && "exist".equals(modifyTag) && startDate.compareTo("1998-09-18") < 0)
					{
						String fee = "80000";

						// 个人大客户不收取押金
						if (CustVipInfoQry.isPersonCustVip(userId))
						{
							fee = "0";
						}

						result.put("DEAL_FEE", "true");
						result.put("FEE", fee);

						break;
					}
				}
			}
		}
		return result;
	}

	public IDataset dealSelectedElementAttrs(IData element, String eparchyCode) throws Exception
	{

		IDataset attrs = new DatasetList();
		IDataset attrItemAList = AttrItemInfoQry.getElementItemA(element.getString("ELEMENT_TYPE_CODE"), element.getString("ELEMENT_ID"), eparchyCode);
		// ADC、MAS弹窗
		if (IDataUtil.isNotEmpty(attrItemAList) && element.getString("ELEMENT_TYPE_CODE", "").equals(BofConst.ELEMENT_TYPE_CODE_SVC))
		{
			for (int i = 0; i < attrItemAList.size(); i++)
			{
				IData attrItemAMap = attrItemAList.getData(i);
				String attrtypecode = attrItemAMap.getString("ATTR_TYPE_CODE", "0");
				if (attrtypecode.equals("9"))
				{
					element.put("ATTR_PARAM_TYPE", "9");
					IData userattr = new DataMap();
					userattr.put("PARAM_VERIFY_SUCC", "false");
					attrs.add(userattr);
					return attrs;
				}
			}
		}

		attrs = this.makeAttrs(null, attrItemAList);
		return attrs;
	}

	public IDataset dealSelectedElements(IData data) throws Exception
	{

		String eparchyCode = data.getString("EPARCHY_CODE");
		String userIdA = data.getString("USER_ID_A", "-1");
		this.setRouteId(eparchyCode);
		IDataset addElements = new DatasetList(data.getString("ELEMENTS"));
		IDataset userElements = new DatasetList(data.getString("SELECTED_ELEMENTS"));
		boolean is695Or696Selected = false;
		int size = addElements.size();
		for (int i = 0; i < size; i++)
		{
			IData element = addElements.getData(i);

			String basicDateString = "";
			boolean effectNow = data.getString("EFFECT_NOW", "").equals("true") ? true : false;
			if (BofConst.MODIFY_TAG_DEL.equals(element.getString("MODIFY_TAG", "")))
			{
				basicDateString = data.getString("BASIC_CANCEL_DATE", "");
			} else
			{
				basicDateString = data.getString("BASIC_START_DATE", "");
			}
			ElementUtil.dealSelectedElementStartDateAndEndDate(element, basicDateString, effectNow, eparchyCode, userElements);

			// 处理ATTRPARAM
			if (BofConst.MODIFY_TAG_ADD.equals(element.getString("MODIFY_TAG", "")))
			{
				IDataset attrs = ElementUtil.dealSelectedElementAttrs(element, eparchyCode);

				if (attrs != null && attrs.size() > 0)
				{
					element.put("ATTR_PARAM", attrs);
				}
			}
		}

		// 产品元素依赖互斥校验，需要模拟台帐数据

		List<String> indexes = new ArrayList<String>();
		if (!"".equals(data.getString("TRADE_TYPE_CODE", "")))
		{
			IDataset tradeSvcs = new DatasetList();
			IDataset tradeDiscnts = new DatasetList();
			IDataset userSvcs = new DatasetList();
			IDataset userDiscnts = new DatasetList();
			String sysTimeString = SysDateMgr.getSysTime();
			for (int i = 0; i < size; i++)
			{
				IData element = addElements.getData(i);
				indexes.add(element.getString("ITEM_INDEX", ""));
				IData ruleElement = new DataMap();
				ruleElement.put("USER_ID_A", userIdA);
				if (BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(element.getString("ELEMENT_TYPE_CODE")))
				{
					ruleElement.put("USER_ID", data.getString("USER_ID", "-1"));
					ruleElement.put("DISCNT_CODE", element.getString("ELEMENT_ID"));
					ruleElement.putAll(element);
					if (ruleElement.getString("INST_ID", "").equals(""))
					{
						ruleElement.put("INST_ID", "" + i);
					}
					ElementUtil.reCalcElementDateByNowTime(ruleElement, sysTimeString);
					tradeDiscnts.add(ruleElement);
					userDiscnts.add(ruleElement);

				} else if (BofConst.ELEMENT_TYPE_CODE_SVC.equals(element.getString("ELEMENT_TYPE_CODE")))
				{
					ruleElement.put("USER_ID", data.getString("USER_ID", "-1"));
					ruleElement.put("SERVICE_ID", element.getString("ELEMENT_ID"));
					ruleElement.putAll(element);
					if (ruleElement.getString("INST_ID", "").equals(""))
					{
						ruleElement.put("INST_ID", "" + i);
					}
					ElementUtil.reCalcElementDateByNowTime(ruleElement, sysTimeString);
					tradeSvcs.add(ruleElement);
					userSvcs.add(ruleElement);

				}
			}

			if (userElements != null && userElements.size() > 0)
			{
				size = userElements.size();
				for (int i = 0; i < size; i++)
				{
					IData element = userElements.getData(i);
					String itemIndex = element.getString("ITEM_INDEX", "");
					IData ruleElement = new DataMap();
					if ("0_1".equals(element.getString("MODIFY_TAG")))
					{
						continue;
					}

					if (indexes.contains(itemIndex))
					{
						continue;
					}

					if (BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(element.getString("ELEMENT_TYPE_CODE")))
					{
						ruleElement.put("USER_ID", data.getString("USER_ID", "-1"));
						ruleElement.put("DISCNT_CODE", element.getString("ELEMENT_ID"));
						ruleElement.putAll(element);
						if (BofConst.MODIFY_TAG_ADD.equals(element.getString("MODIFY_TAG")) || BofConst.MODIFY_TAG_UPD.equals(element.getString("MODIFY_TAG")) || BofConst.MODIFY_TAG_DEL.equals(element.getString("MODIFY_TAG")))
						{
							ElementUtil.reCalcElementDateByNowTime(ruleElement, sysTimeString);
						}
						userDiscnts.add(ruleElement);
					} else if (BofConst.ELEMENT_TYPE_CODE_SVC.equals(element.getString("ELEMENT_TYPE_CODE")))
					{
						ruleElement.put("USER_ID", data.getString("USER_ID", "-1"));
						ruleElement.put("SERVICE_ID", element.getString("ELEMENT_ID"));
						ruleElement.putAll(element);
						if (BofConst.MODIFY_TAG_ADD.equals(element.getString("MODIFY_TAG")) || BofConst.MODIFY_TAG_UPD.equals(element.getString("MODIFY_TAG")) || BofConst.MODIFY_TAG_DEL.equals(element.getString("MODIFY_TAG")))
						{
							ElementUtil.reCalcElementDateByNowTime(ruleElement, sysTimeString);
						}
						userSvcs.add(ruleElement);
					}
				}
			}

			IDataset tradeMains = new DatasetList();
			IData tradeMain = new DataMap();
			tradeMain.put("TRADE_EPARCHY_CODE", eparchyCode);
			tradeMain.put("TRADE_TYPE_CODE", data.getString("TRADE_TYPE_CODE"));
			tradeMain.put("IN_MODE_CODE", "0");
			tradeMain.put("USER_ID", data.getString("USER_ID"));
			tradeMains.add(tradeMain);
			IData ruleParam = new DataMap();
			ruleParam.put("TF_B_TRADE_SVC", tradeSvcs);
			ruleParam.put("TF_B_TRADE_DISCNT", tradeDiscnts);
			// ruleParam.put("TF_F_USER_SVC_AFTER", userSvcs);
			// ruleParam.put("TF_F_USER_DISCNT_AFTER", userDiscnts);
			IDataset svcList = GroupTradeAfterUtil.getTradeChkAfterSvcs(ruleParam, data.getString("USER_ID"));
			ruleParam.put("TF_F_USER_SVC_AFTER", svcList);
			IDataset discntListt = GroupTradeAfterUtil.getTradeChkAfterDiscnt(ruleParam, data.getString("USER_ID"));
			ruleParam.put("TF_F_USER_DISCNT_AFTER", discntListt);
			ruleParam.put("IS_COMPONENT", "true");
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
						errorInfo.append(error.getString("TIPS_INFO"));
					}
					addElements.getData(0).put("ERROR_INFO", errorInfo.toString());
				}
			}
		}

		if (userElements != null && userElements.size() > 0)
		{
			size = userElements.size();
			for (int i = 0; i < size; i++)
			{
				IData element = userElements.getData(i);

				if (("695".equals(element.getString("ELEMENT_ID")) || "696".equals(element.getString("ELEMENT_ID"))))
				{
					is695Or696Selected = true;// 是否之前
				}
			}
		}
		// 加载相关费用
		size = addElements.size();
		for (int i = 0; i < size; i++)
		{
			IData element = addElements.getData(i);
			if ("0".equals(element.getString("MODIFY_TAG")))
			{
				// 特殊处理军网28、40元取消套餐后订购其他套餐开始时间的展示
				if (is695Or696Selected)
				{
					element.put("START_DATE", SysDateMgr.getFirstDayOfNextMonth());
				}
				// IDataset feeConfigs =
				// ProductFeeInfoQry.getElementFee(data.getString("TRADE_TYPE_CODE"),
				// CSBizBean.getVisit().getInModeCode(), "",
				// element.getString("ELEMENT_TYPE_CODE"),
				// element.getString("PRODUCT_ID"), element
				// .getString("PACKAGE_ID"), "-1",
				// element.getString("ELEMENT_ID"), eparchyCode, "3");
				IDataset feeConfigs = ProductFeeInfoQry.getElementFeeList(data.getString("TRADE_TYPE_CODE"), element.getString("ELEMENT_TYPE_CODE"), element.getString("ELEMENT_ID"), BofConst.ELEMENT_TYPE_CODE_PRODUCT, element.getString("PRODUCT_ID"), element.getString("PACKAGE_ID"));
				if (IDataUtil.isEmpty(feeConfigs))
				{
					continue;
				}
				int feeSize = feeConfigs.size();
				IDataset feeDatas = new DatasetList();
				for (int j = 0; j < feeSize; j++)
				{
					IData feeConfig = feeConfigs.getData(j);
					if (!"0".equals(feeConfig.getString("PAY_MODE")))
					{
						continue;
					}
					IData feeData = new DataMap();
					feeData.put("TRADE_TYPE_CODE", data.getString("TRADE_TYPE_CODE"));
					feeData.put("MODE", feeConfig.getString("FEE_MODE"));
					feeData.put("CODE", feeConfig.getString("FEE_TYPE_CODE"));
					feeData.put("FEE", feeConfig.getString("FEE"));
					feeData.put("ELEMENT_ID", element.getString("ELEMENT_ID"));
					feeDatas.add(feeData);
				}
				element.put("FEE_DATA", feeDatas);

			}
			// //特殊处理军网28、40元取消套餐后结束时间的展示
			if (("695".equals(element.getString("ELEMENT_ID")) || "696".equals(element.getString("ELEMENT_ID"))) && "1".equals(element.getString("MODIFY_TAG")))
			{
				element.put("END_DATE", SysDateMgr.getLastDateThisMonth());
			}
		}
		return addElements;
	}

	public IDataset dealSelectedElementsForChg(IData data) throws Exception
	{
		// 对于元素的处理，如果用户有下月生效的产品，则产品组件展示的是下月生效的产品包元素，如果元素在用户的原产品下有，则以原产品的生效方式为准
		// 如果元素在用户的原产品下没有，则以新产品的生效方式为准
		String eparchyCode = data.getString("EPARCHY_CODE");
		String bookFlag = "0"; // 超享套餐使用
		this.setRouteId(eparchyCode);
		String userProductId = data.getString("USER_PRODUCT_ID");
		String nextProductId = data.getString("NEXT_PRODUCT_ID");
		String bookingDate = data.getString("BOOKING_DATE", "");
		String sysDate = SysDateMgr.getSysTime();

		List<ProductModuleData> pmds = new ArrayList<ProductModuleData>();

		// 设置ProductTimeEnv
		ProductTimeEnv env = new ProductTimeEnv();

		// 该值只有主产品变换的时候才有
		if (!data.getString("BASIC_START_DATE", "").equals("") && !data.getString("BASIC_CANCEL_DATE", "").equals(""))
		{
			// BASIC_START_DATE:本次产品生效时间,本组件basicStartDateControlId的值,从SelectedElementSVC.getUserElements的NEW_PRODUCT_START_DATE取值
			env.setBasicAbsoluteStartDate(data.getString("BASIC_START_DATE"));
			env.setBasicAbsoluteCancelDate(data.getString("BASIC_CANCEL_DATE"));
		}
		// 当不存在预约产品时 绝对时间取选定的预约时间【如存在预约】
		if (StringUtils.isBlank(nextProductId) && ProductUtils.isBookingChange(bookingDate))
		{
			env.setBasicAbsoluteStartDate(bookingDate);
			env.setBasicAbsoluteCancelDate(SysDateMgr.getLastSecond(bookingDate));
			bookFlag = "1";
		}

		IDataset elements = new DatasetList(data.getString("ELEMENTS"));
		int size = elements.size();

		// 处理中间状态 作为新增状态处理
		for (int i = 0; i < size; i++)
		{
			if (BofConst.MODIFY_TAG_MIDDLE.equals(elements.getData(i).getString("MODIFY_TAG")))
			{
				elements.getData(i).put("MODIFY_TAG", BofConst.MODIFY_TAG_ADD);
				elements.getData(i).put("OLD_MODIFY_TAG", BofConst.MODIFY_TAG_MIDDLE);// 存放原始状态
			}
		}

		for (int i = 0; i < size; i++)
		{
			IData element = elements.getData(i);
			
			//add by zhangxing3 for REQ201812250005关于开发集团客户一企一策一码的需求 start
			
			if ("110".equals(data.getString("TRADE_TYPE_CODE", "")) && BofConst.MODIFY_TAG_ADD.equals(element.getString("MODIFY_TAG", ""))
					&& BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(element.getString("ELEMENT_TYPE_CODE")))
			{
				String userId = data.getString("USER_ID", "");
				String discntCode = element.getString("ELEMENT_ID","");
	            //System.out.println("-----------dealSelectedElementsForChg----------userId:"+userId);
	            //System.out.println("-----------dealSelectedElementsForChg----------discntCode:"+discntCode);

				boolean limitTag = ElementUtil.checkDiscntGroupPolicyRule(userId,discntCode);
	            //System.out.println("-----------dealSelectedElementsForChg----------limitTag:"+limitTag);

				if(!limitTag){
					elements.getData(0).put("ERROR_INFO", "您不符合折扣套餐策略,不能办理折扣套餐:"+discntCode+"!!");
				}
			}			
			//add by zhangxing3 for REQ201812250005关于开发集团客户一企一策一码的需求 end

			// 设置产品模型数据
			ProductModuleData pmd = null;
			if (element.getString("ELEMENT_TYPE_CODE", "").equals(BofConst.ELEMENT_TYPE_CODE_SVC))
			{
				pmd = new SvcData(element);
			} else if (BofConst.ELEMENT_TYPE_CODE_PLATSVC.equals(element.getString("ELEMENT_TYPE_CODE")))
			{
				if (BofConst.MODIFY_TAG_ADD.equals(element.getString("MODIFY_TAG")))
				{
					element.put("OPER_CODE", PlatConstants.OPER_ORDER);
				} else if (BofConst.MODIFY_TAG_DEL.equals(element.getString("MODIFY_TAG")))
				{
					element.put("OPER_CODE", PlatConstants.OPER_CANCEL_ORDER);
				}
				pmd = new PlatSvcData(element);
				pmd.setPkgElementConfig(element.getString("PACKAGE_ID"));
			} else
			{
				pmd = new DiscntData(element);
			}

			// 当存在预约产品时 绝对时间取预约产品生效时间【此时不存在预约情况】
			if (StringUtils.isNotBlank(nextProductId) && !ProductUtils.isBookingChange(bookingDate))
			{
				IDataset userOldProductElements = ProductInfoQry.getProductElements(userProductId, eparchyCode);

				IData oldConfig = this.getTransElement(userOldProductElements, pmd.getElementId(), pmd.getElementType());
				if (IDataUtil.isNotEmpty(oldConfig))// 如果元素存在老产品下面 以老产品配置方式计算
				{
					pmd.setPkgElementConfig(oldConfig.getString("PACKAGE_ID"));
					// 如果元素是删除 且开始时间大于系统时间 那么终止此元素的结束时间为开始时间的前一秒 其他走配置方式
					if (BofConst.MODIFY_TAG_DEL.equals(pmd.getModifyTag()) && pmd.getStartDate() != null && pmd.getStartDate().compareTo(sysDate) > 0)
					{
						pmd.setCancelAbsoluteDate(SysDateMgr.getLastSecond(pmd.getStartDate()));
					}
				} else
				// 如果元素只存在新产品下 则设置元素绝对生效时间为产品的开始时间 设置绝对失效时间为产品生效时间前一秒
				{
					pmd.setEnableTag("4");
					pmd.setStartAbsoluteDate(data.getString("NEXT_PRODUCT_START_DATE"));
					pmd.setCancelAbsoluteDate(SysDateMgr.getLastSecond(data.getString("NEXT_PRODUCT_START_DATE")));
				}
			}

			// 计算时间
			if (BofConst.MODIFY_TAG_ADD.equals(pmd.getModifyTag()))
			{
				if ("5".equals(pmd.getEnableTag()) && BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(pmd.getElementType()))
				{
					if ("1".equals(bookFlag) && ("3370".equals(pmd.getElementId()) || "3372".equals(pmd.getElementId()) || "3377".equals(pmd.getElementId()) || "3378".equals(pmd.getElementId())))
					{// 超享卡特殊处理
						element.put("EFFECT_NOW_START_DATE", SysDateMgr.firstDayOfDate(bookingDate, 1));
						element.put("EFFECT_NOW_END_DATE", SysDateMgr.endDate(element.getString("EFFECT_NOW_START_DATE"), pmd.getEndEnableTag(), pmd.getEndAbsoluteDate(), pmd.getEndOffSet(), pmd.getEndUnit()));
					} else
					{
						element.put("EFFECT_NOW_START_DATE", SysDateMgr.getFirstDayOfNextMonth());
						element.put("EFFECT_NOW_END_DATE", SysDateMgr.endDate(element.getString("EFFECT_NOW_START_DATE"), pmd.getEndEnableTag(), pmd.getEndAbsoluteDate(), pmd.getEndOffSet(), pmd.getEndUnit()));
					}
				} else
				{
					element.put("EFFECT_NOW_START_DATE", sysDate);
					element.put("EFFECT_NOW_END_DATE", SysDateMgr.endDate(element.getString("EFFECT_NOW_START_DATE"), pmd.getEndEnableTag(), pmd.getEndAbsoluteDate(), pmd.getEndOffSet(), pmd.getEndUnit()));
				}
				pmd.setStartDate(null);
				String startDate = ProductModuleCalDate.calStartDate(pmd, env);
				element.put("START_DATE", startDate);
				pmd.setEndDate(null);
				String endDate = ProductModuleCalDate.calEndDate(pmd, startDate);
				element.put("END_DATE", endDate);
				// if("1".equals(bookFlag)&&("3370".equals(pmd.getElementId())||"3372".equals(pmd.getElementId())||"3377".equals(pmd.getElementId())||"3378".equals(pmd.getElementId()))){
				// //超享卡
				// element.put("OLD_EFFECT_NOW_START_DATE", startDate);
				// element.put("OLD_EFFECT_NOW_END_DATE", endDate);
				// element.put("START_DATE",
				// element.getString("EFFECT_NOW_START_DATE"));
				// element.put("END_DATE",
				// element.getString("EFFECT_NOW_END_DATE"));
				// }
				if (data.getString("EFFECT_NOW", "").equals("true"))
				{
					element.put("OLD_EFFECT_NOW_START_DATE", startDate);
					element.put("OLD_EFFECT_NOW_END_DATE", endDate);
					element.put("START_DATE", element.getString("EFFECT_NOW_START_DATE"));
					element.put("END_DATE", element.getString("EFFECT_NOW_END_DATE"));
				}
				IDataset attrs = dealSelectedElementAttrs(element, eparchyCode);

				if (attrs != null && attrs.size() > 0)
				{
					element.put("ATTR_PARAM", attrs);
				}
				if ("3".equals(pmd.getEnableTag()) && StringUtils.isBlank(env.getBasicAbsoluteStartDate()))
				{
					element.put("CHOICE_START_DATE", "true");
				}
				if ("2".equals(pmd.getEndEnableTag())/*
													 * ||
													 * StaffPrivUtil.isFuncDataPriv
													 * (
													 * this.getVisit().getStaffId
													 * (),
													 * "SYS_CRM_DISCNTDATECHG")
													 */)
				{
					element.put("SELF_END_DATE", "true");// 自选时间
				}

				/*
				 * 节假日元素办理时间 获取元素的办理时间
				 */
				if (BofConst.MODIFY_TAG_ADD.equals(pmd.getModifyTag()) && pmd.getElementType().equals(BofConst.ELEMENT_TYPE_CODE_DISCNT))
				{
					IDataset elementSpecialTimeLimit = CommparaInfoQry.queryComparaByAttrAndCode1("CSM", "7686", pmd.getElementId(), pmd.getElementType());
					if (IDataUtil.isNotEmpty(elementSpecialTimeLimit))
					{
						// 获取时间配置
						IData dateLimitConfig = elementSpecialTimeLimit.getData(0);
						// 有效期配置
						String validBeginDate = dateLimitConfig.getString("PARA_CODE4", "");
						String validEndDate = dateLimitConfig.getString("PARA_CODE5", "");

						String curDate = SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND);

						if (StringUtils.isNotBlank(validBeginDate) && StringUtils.isNotBlank(validEndDate))
						{
							// 如果在有效期内
							if (curDate.compareTo(validBeginDate) > 0 && curDate.compareTo(validEndDate) < 0)
							{
								/*
								 * 判断是在哪个规定的时间内办理 如果假期前办理，就在假期第一天开始生效
								 * 如果是假期当中办理，就是马上生效 终止时间都为假期的最后一天
								 */
								String holidayBeforeBegin = dateLimitConfig.getString("PARA_CODE6", "");
								String holidayBeforeEnd = dateLimitConfig.getString("PARA_CODE7", "");
								String holidayBeforeStartDate = dateLimitConfig.getString("PARA_CODE8", "");

								String holidayBegin = dateLimitConfig.getString("PARA_CODE9", "");
								String holidayEnd = dateLimitConfig.getString("PARA_CODE10", "");

								String allEndDay = dateLimitConfig.getString("PARA_CODE12", "");

								if (StringUtils.isNotBlank(holidayBeforeBegin) && StringUtils.isNotBlank(holidayBeforeEnd))
								{
									if (curDate.compareTo(holidayBeforeBegin) > 0 && curDate.compareTo(holidayBeforeEnd) < 0)
									{
										element.put("START_DATE", holidayBeforeStartDate);
									}
								}
								if (StringUtils.isNotBlank(holidayBegin) && StringUtils.isNotBlank(holidayEnd))
								{
									if (curDate.compareTo(holidayBegin) > 0 && curDate.compareTo(holidayEnd) < 0)
									{
										element.put("START_DATE", curDate);
									}
								}

								element.put("END_DATE", allEndDay);
							}
						}
					}
					
					
					IDataset elementHolidayTimeLimit = CommparaInfoQry.queryComparaByAttrAndCode1("CSM", "1806", pmd.getElementId(), pmd.getElementType());
					if (IDataUtil.isNotEmpty(elementHolidayTimeLimit))
					{						
						// 获取时间配置
						IData dateLimitConfig = elementHolidayTimeLimit.getData(0);
						// 有效期配置
						
						//本月第一天
						String validBeginDate = SysDateMgr.getFirstDayOfThisMonth();
						//本月最后一天
						String validEndDate = SysDateMgr.getLastDateThisMonth();

						String curDate = SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND);

						if (StringUtils.isNotBlank(validBeginDate) && StringUtils.isNotBlank(validEndDate))
						{
							// 如果在有效期内
							if (curDate.compareTo(validBeginDate) > 0 && curDate.compareTo(validEndDate) < 0)
							{								
								//标志：1表示走本月减特定天数；2表示本月几号
								String timeFlag = dateLimitConfig.getString("PARA_CODE6", "1");
								if("1".equals(timeFlag))
								{
									String timeday = dateLimitConfig.getString("PARA_CODE7", "7");
									String holidayBeforeBegin = SysDateMgr.getFirstDayOfThisMonth();
									String holidayBeforeEnd = SysDateMgr.addDays(validEndDate, -Integer.parseInt(timeday));
									if (StringUtils.isNotBlank(holidayBeforeBegin) && StringUtils.isNotBlank(holidayBeforeEnd))
									{
										if (curDate.compareTo(holidayBeforeBegin) > 0 && curDate.compareTo(holidayBeforeEnd) < 0)
										{
											element.put("START_DATE", SysDateMgr.addDays(holidayBeforeEnd, 0));
										}
									}
								}else if("2".equals(timeFlag))
								{
									String timeday = dateLimitConfig.getString("PARA_CODE8", "23");
									String holidayBeforeBegin = SysDateMgr.getFirstDayOfThisMonth();
									//yyyy-MM-dd
									String timeend = SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND_YYYYMMDD);
									String holidayBeforeEnd = timeend.substring(0, 8) + timeday;
									if (StringUtils.isNotBlank(holidayBeforeBegin) && StringUtils.isNotBlank(holidayBeforeEnd))
									{
										if (curDate.compareTo(holidayBeforeBegin) > 0 && curDate.compareTo(holidayBeforeEnd) < 0)
										{
											element.put("START_DATE", SysDateMgr.addDays(holidayBeforeEnd, 0));
										}
									}
								}else
								{
									element.put("START_DATE", curDate);
								}
								
								if(StringUtils.isBlank(element.getString("START_DATE")))
								{
									element.put("START_DATE", curDate);
								}
								element.put("END_DATE", SysDateMgr.getLastDateThisMonth());
							}
						}
					}
				}

				// REQ201506020003 假日流量套餐开发需求 start by songlm 20150619
				// 6600该优惠元素配置生效失效时间类型为4，即使用绝对时间，用TD_B_PACKAGE_ELEMENT表中配置的生效、失效时间。但如果在生效与失效时间期间办理，需要立即生效。
				if ("6600".equals(pmd.getElementId()) && (sysDate.compareTo(startDate) > 0))
				{
					element.put("START_DATE", sysDate);
				}
				// end

			} else if (BofConst.MODIFY_TAG_DEL.equals(pmd.getModifyTag()))
			{
				element.put("EFFECT_NOW_START_DATE", element.getString("START_DATE"));
				element.put("EFFECT_NOW_END_DATE", sysDate);
				String cancelTag = pmd.getCancelTag();
				//套餐取消方式
				element.put("CANCEL_MODE",cancelTag);
				if (StringUtils.isNotBlank(cancelTag) && !"4".equals(pmd.getCancelTag()))
				{
					pmd.setEndDate(null);
				}
				String cancelDate = ProductModuleCalDate.calCancelDate(pmd, env);
				element.put("END_DATE", cancelDate);
				if (data.getString("EFFECT_NOW", "").equals("true"))
				{
					element.put("OLD_EFFECT_NOW_START_DATE", element.getString("START_DATE"));
					element.put("OLD_EFFECT_NOW_END_DATE", cancelDate);
					element.put("END_DATE", element.getString("EFFECT_NOW_END_DATE"));
				}
			}
			pmds.add(pmd);
		}

		// 产品变更 特殊计算时间类调用
		String tradeTypeCode = data.getString("TRADE_TYPE_CODE");
		if (StringUtils.isNotBlank(tradeTypeCode))
		{
			UcaData uca = null;
			String userId = data.getString("USER_ID", "");
			if (StringUtils.isNotBlank(userId) && !"-1".equals(userId) && !"undefined".equals(userId) && !"null".equals(userId))// 存在userId的就构建UCA
			// 否则给个空【开户没有userId】
			{
				uca = UcaDataFactory.getUcaByUserId(data.getString("USER_ID"));
			}
			ProductModuleCalDateExtend.calElementDate(tradeTypeCode, elements, env, uca, pmds);
		}

		// 前台流量王时间特殊处理
		this.GprsKingDiscntDateDeal(elements, new DatasetList(data.getString("SELECTED_ELEMENTS")));

		// 恢复中间状态
		for (int i = 0; i < size; i++)
		{
			if (StringUtils.isNotBlank(elements.getData(i).getString("OLD_MODIFY_TAG")) && BofConst.MODIFY_TAG_MIDDLE.equals(elements.getData(i).getString("OLD_MODIFY_TAG")))
			{
				elements.getData(i).put("MODIFY_TAG", BofConst.MODIFY_TAG_MIDDLE);
				elements.getData(i).put("OLD_MODIFY_TAG", "");// 恢复后清空临时原始状态
			}
		}

		// 产品元素依赖互斥校验，需要模拟台帐数据
		List<String> indexes = new ArrayList<String>();
		if (!"".equals(data.getString("TRADE_TYPE_CODE", "")))
		{
			IDataset tradeSvcs = new DatasetList();
			IDataset tradeDiscnts = new DatasetList();
			IDataset userSvcs = new DatasetList();
			IDataset userDiscnts = new DatasetList();
			for (int i = 0; i < size; i++)
			{
				IData element = elements.getData(i);
				indexes.add(element.getString("ITEM_INDEX", ""));
				IData ruleElement = new DataMap();
				ruleElement.put("USER_ID_A", "-1");
				if (BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(element.getString("ELEMENT_TYPE_CODE")))
				{
					ruleElement.put("USER_ID", data.getString("USER_ID", "-1"));
					ruleElement.put("DISCNT_CODE", element.getString("ELEMENT_ID"));
					ruleElement.putAll(element);
					if (ruleElement.getString("INST_ID", "").equals(""))
					{
						ruleElement.put("INST_ID", "" + i);
					}
					tradeDiscnts.add(ruleElement);
					userDiscnts.add(ruleElement);

				} else if (BofConst.ELEMENT_TYPE_CODE_SVC.equals(element.getString("ELEMENT_TYPE_CODE")))
				{
					ruleElement.put("USER_ID", data.getString("USER_ID", "-1"));
					ruleElement.put("SERVICE_ID", element.getString("ELEMENT_ID"));
					ruleElement.putAll(element);
					if (ruleElement.getString("INST_ID", "").equals(""))
					{
						ruleElement.put("INST_ID", "" + i);
					}
					tradeSvcs.add(ruleElement);
					userSvcs.add(ruleElement);

				}
			}

			// 调用规则前组织数据
			IDataset userElements = new DatasetList(data.getString("SELECTED_ELEMENTS"));
			if (IDataUtil.isNotEmpty(userElements) && userElements.size() > 0)
			{
				size = userElements.size();
				for (int i = 0; i < size; i++)
				{
					IData element = userElements.getData(i);
					if (IDataUtil.isEmpty(element))
					{
						continue;
					}
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
					if (BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(element.getString("ELEMENT_TYPE_CODE")))
					{
						ruleElement.put("USER_ID", data.getString("USER_ID", "-1"));
						ruleElement.put("DISCNT_CODE", element.getString("ELEMENT_ID"));
						ruleElement.putAll(element);
						if (StringUtils.isNotBlank(element.getString("NEW_PRODUCT_ID")) && StringUtils.isNotBlank(element.getString("NEW_PACKAGE_ID")))
						{
							ruleElement.put("PRODUCT_ID", element.getString("NEW_PRODUCT_ID"));
							ruleElement.put("PACKAGE_ID", element.getString("NEW_PACKAGE_ID"));
						}
						userDiscnts.add(ruleElement);
					} else if (BofConst.ELEMENT_TYPE_CODE_SVC.equals(element.getString("ELEMENT_TYPE_CODE")))
					{
						ruleElement.put("USER_ID", data.getString("USER_ID", "-1"));
						ruleElement.put("SERVICE_ID", element.getString("ELEMENT_ID"));
						ruleElement.putAll(element);
						if (StringUtils.isNotBlank(element.getString("NEW_PRODUCT_ID")) && StringUtils.isNotBlank(element.getString("NEW_PACKAGE_ID")))
						{
							ruleElement.put("PRODUCT_ID", element.getString("NEW_PRODUCT_ID"));
							ruleElement.put("PACKAGE_ID", element.getString("NEW_PACKAGE_ID"));
						}
						userSvcs.add(ruleElement);
					}
				}
			}

			// 规则调用
			IDataset tradeMains = new DatasetList();
			IData tradeMain = new DataMap();
			tradeMain.put("TRADE_EPARCHY_CODE", eparchyCode);
			tradeMain.put("TRADE_TYPE_CODE", data.getString("TRADE_TYPE_CODE"));
			tradeMain.put("IN_MODE_CODE", "0");
			tradeMain.put("USER_ID", data.getString("USER_ID"));
			tradeMains.add(tradeMain);
			IData ruleParam = new DataMap();
			ruleParam.put("TF_B_TRADE_SVC", tradeSvcs);
			ruleParam.put("TF_B_TRADE_DISCNT", tradeDiscnts);
			ruleParam.put("TF_F_USER_SVC_AFTER", userSvcs);
			ruleParam.put("TF_F_USER_DISCNT_AFTER", userDiscnts);
			ruleParam.put("IS_COMPONENT", "true");
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
						errorInfo.append(error.getString("TIPS_INFO") + "<br>");
					}
					elements.getData(0).put("ERROR_INFO", errorInfo.toString());
				}
			}
		}

		// 加载相关费用
		size = elements.size();
		for (int i = 0; i < size; i++)
		{
			IData element = elements.getData(i);
			if ("0".equals(element.getString("MODIFY_TAG")))
			{
				IDataset feeDatas = new DatasetList();

				// 海南国际长途、漫游费用特殊处理
				String userId = data.getString("USER_ID");
				String elementId = element.getString("ELEMENT_ID");
				String elementTypeCode = element.getString("ELEMENT_TYPE_CODE");
				IDataset userElements = new DatasetList(data.getString("SELECTED_ELEMENTS"));
				// REQ202004240028_关于国漫作为基础服务的开发需求 去除国漫押金 by wuhao5 20200509
//				if ("110".equals(tradeTypeCode))
//				{
//					if (!isDealFeeByServiceId15(userId, elementId, elementTypeCode, userElements))
//					{
//						continue;
//					}
//
//					IData feeSvc19 = dealFeeByServiceId19(userId, elementId, elementTypeCode, userElements);
//					if (IDataUtil.isNotEmpty(feeSvc19))
//					{
//						String fee19 = feeSvc19.getString("FEE");
//						if ("true".equals(feeSvc19.getString("DEAL_FEE")) && !"0".equals(fee19))
//						{
//							IData feeData = new DataMap();
//							feeData.put("TRADE_TYPE_CODE", tradeTypeCode);
//							feeData.put("MODE", "1");
//							feeData.put("CODE", "3");
//							feeData.put("FEE", fee19);
//							feeDatas.add(feeData);
//
//							element.put("FEE_DATA", feeDatas);
//							continue;
//						}
//					}
//					// 办理国际漫游的前提条件是办理国际长途，只需判断国际长途是否要走星级流程即可
//					if (("15".equals(elementId) || "19".equals(elementId)) && BofConst.ELEMENT_TYPE_CODE_SVC.equals(elementTypeCode))
//					{
//						// REQ201410240002新的星级服务体系下，优化国际漫游业务开通门槛
//						UcaData uca = null;
//						uca = UcaDataFactory.getUcaByUserId(data.getString("USER_ID"));
//						String strAcctBlance = uca.getAcctBlance();
//						String strCreditClass = uca.getUserCreditClass();
//						int iCreditClass = Integer.parseInt(strCreditClass);
//						int iAcctBlance = Integer.parseInt(strAcctBlance) / 100;
//						// 是否满足星级服务流程，预存款开通条件
//						boolean bIsClass = false;
//						if (-1 == iCreditClass || 0 == iCreditClass)
//						{
//							if (iAcctBlance < 500)
//							{
//								bIsClass = true;
//							}
//						}
////						else if (1 == iCreditClass || 2 == iCreditClass)
////						{
////							if (iAcctBlance < 500)
////							{
////								bIsClass = true;
////							}
////						}
//						// 如果不满足星级服务流程需求，需要走原来的流程
//						if (!bIsClass)
//						{
//							continue;
//						}
//					}
//				}
				// 海南国际长途、漫游费用特殊处理 END
				// IDataset feeConfigs =
				// ProductFeeInfoQry.getElementFee(data.getString("TRADE_TYPE_CODE"),
				// CSBizBean.getVisit().getInModeCode(), "",
				// element.getString("ELEMENT_TYPE_CODE"),
				// element.getString("PRODUCT_ID"), element
				// .getString("PACKAGE_ID"), "-1",
				// element.getString("ELEMENT_ID"), eparchyCode, "3");
				IDataset feeConfigs = ProductFeeInfoQry.getElementFeeList(data.getString("TRADE_TYPE_CODE"), BofConst.ELEMENT_TYPE_CODE_PRODUCT, element.getString("PRODUCT_ID"), element.getString("ELEMENT_TYPE_CODE"), element.getString("ELEMENT_ID"), element.getString("PACKAGE_ID"));
				if (IDataUtil.isEmpty(feeConfigs))
				{
					continue;
				}
				int feeSize = feeConfigs.size();

				for (int j = 0; j < feeSize; j++)
				{
					IData feeConfig = feeConfigs.getData(j);
					if (!"0".equals(feeConfig.getString("PAY_MODE")))
					{
						continue;
					}
					IData feeData = new DataMap();
					feeData.put("TRADE_TYPE_CODE", data.getString("TRADE_TYPE_CODE"));
					feeData.put("MODE", feeConfig.getString("FEE_MODE"));
					feeData.put("CODE", feeConfig.getString("FEE_TYPE_CODE"));
					feeData.put("FEE", feeConfig.getString("FEE"));
					feeDatas.add(feeData);
				}
				element.put("FEE_DATA", feeDatas);
			}
		}
		return elements;
	}

	/**
	 * 前台流量王时间处理【先选择流量王,而后选择对应GPRS，处理不到】
	 * 
	 * @param elements
	 *            本次操作元素集合
	 * @param selectedElements
	 *            已经存在所有元素集合
	 * @throws Exception
	 */
	public void GprsKingDiscntDateDeal(IDataset elements, IDataset selectedElements) throws Exception
	{
		// 只针对本次操作中有流量王优惠，且已经存在对应依赖的GPRS优惠时候才做处理
		if (IDataUtil.isNotEmpty(elements))
		{
			for (int i = 0, size = elements.size(); i < size; i++)
			{
				IData element = elements.getData(i);

				String modifyTag = element.getString("MODIFY_TAG");
				String elementId = element.getString("ELEMENT_ID");
				String elementTypeCode = element.getString("ELEMENT_TYPE_CODE");
				// String discntType
				// =DiscntInfoQry.getDiscntTypeByDiscntCode(elementId);
				String discntType = UDiscntInfoQry.getTableNameValue("TD_B_DTYPE_DISCNT", "DISCNT_TYPE_CODE", elementTypeCode, elementId);

				// 本次添加的流量王
				if (BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(elementTypeCode) && BofConst.MODIFY_TAG_ADD.equals(modifyTag) && "LLCX".equals(discntType))
				{
					// IDataset relyDiscnts =
					// ElemLimitInfoQry.getGprsKingDiscntByGprs(elementId);

					IDataset relyDiscnts = new DatasetList();
					IDataset eleLimitA = UElementLimitInfoQry.queryElementLimitByElementIdB(elementId, BofConst.ELEMENT_TYPE_CODE_DISCNT, "2");
					IDataset allGPRSDiscnts = UPackageElementInfoQry.queryPackageElementsByProductIdDisctypeCode("", "5");

					for (int limita = 0; limita < eleLimitA.size(); limita++)
					{
						IData temp = eleLimitA.getData(limita);
						for (int j = 0, discntSize = allGPRSDiscnts.size(); j < discntSize; j++)
						{
							IData gprsDiscnt = allGPRSDiscnts.getData(j);
							if (temp.getString("ELEMENT_ID_B").equals(gprsDiscnt.getString("DISCNT_CODE")))
							{
								relyDiscnts.add(temp);

							}
						}
					}

					if (IDataUtil.isNotEmpty(relyDiscnts))
					{
						String relyElementId = relyDiscnts.getData(0).getString("ELEMENT_ID_B");// 依赖的那个GPRS优惠

						String tempStartDate = SysDateMgr.END_DATE_FOREVER;
						String tempEndDate = SysDateMgr.END_DATE_FOREVER;

						String llcxStartDate = SysDateMgr.decodeTimestamp(element.getString("START_DATE"), SysDateMgr.PATTERN_STAND);
						String llcxEndDate = SysDateMgr.decodeTimestamp(element.getString("END_DATE"), SysDateMgr.PATTERN_STAND);

						boolean existsGprs = false;

						for (int j = 0, selectSize = selectedElements.size(); j < selectSize; j++)
						{
							IData selectElement = selectedElements.getData(j);

							String selectElementId = selectElement.getString("ELEMENT_ID");
							String selectElementTypeCode = selectElement.getString("ELEMENT_TYPE_CODE");

							// 存在对应依赖的GPRS优惠
							if (BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(selectElementTypeCode) && selectElementId.equals(relyElementId))
							{
								String gprsStartDate = SysDateMgr.decodeTimestamp(selectElement.getString("START_DATE"), SysDateMgr.PATTERN_STAND);
								String gprsEndDate = SysDateMgr.decodeTimestamp(selectElement.getString("END_DATE"), SysDateMgr.PATTERN_STAND);

								if (tempStartDate.compareTo(gprsStartDate) > 0)
								{
									tempStartDate = gprsStartDate;
								}
								if (tempEndDate.compareTo(gprsEndDate) > 0)
								{
									tempEndDate = gprsEndDate;
								}

								existsGprs = true;
							}
						}
						if (existsGprs)// 当本次操作存在对应依赖的GPRS优惠的时候，才重新赋值流量王的开始结束时间
						{
							if (llcxStartDate.compareTo(tempStartDate) < 0)
							{
								element.put("START_DATE", tempStartDate);
							}
							if (llcxEndDate.compareTo(tempEndDate) > 0)
							{
								element.put("END_DATE", tempEndDate);
							}
						}
					}
				}
			}
		}
	}

	public IDataset dealWidenetSelectedElementsForChg(IData data) throws Exception
	{
		// 对于元素的处理，如果用户有下月生效的产品，则产品组件展示的是下月生效的产品包元素，如果元素在用户的原产品下有，则以原产品的生效方式为准
		// 如果元素在用户的原产品下没有，则以新产品的生效方式为准
		String eparchyCode = data.getString("EPARCHY_CODE");
		this.setRouteId(eparchyCode);
		String userProductId = data.getString("USER_PRODUCT_ID");
		String nextProductId = data.getString("NEXT_PRODUCT_ID");
		String bookingDate = data.getString("BOOKING_DATE", "");
		String tradeTypeCode = data.getString("TRADE_TYPE_CODE", "");

		// 接收扩展字段(宽带业务传入的营销活动ID)
		String elementExtendField = data.getString("ELEMENT_EXTEND_FIELD", "");

		String sysDate = SysDateMgr.getSysTime();

		List<ProductModuleData> pmds = new ArrayList<ProductModuleData>();

		// 设置ProductTimeEnv,目前只设置绝对生效、失效时间
		ProductTimeEnv env = new ProductTimeEnv();
		if (!data.getString("BASIC_START_DATE", "").equals("") && !data.getString("BASIC_CANCEL_DATE", "").equals(""))
		{
			// 该值只有主产品变换的时候才有
			// BASIC_START_DATE:本次产品生效时间,本组件basicStartDateControlId的值,从SelectedElementSVC.getUserElements的NEW_PRODUCT_START_DATE取值
			env.setBasicAbsoluteStartDate(data.getString("BASIC_START_DATE"));
			env.setBasicAbsoluteCancelDate(data.getString("BASIC_CANCEL_DATE"));
		}
		if (StringUtils.isNotBlank(nextProductId) && !ProductUtils.isBookingChange(bookingDate))// 当存在预约产品时
		// 绝对时间取预约产品生效时间【此时不存在预约情况】
		{
			env.setBasicAbsoluteStartDate(data.getString("NEXT_PRODUCT_START_DATE"));
			env.setBasicAbsoluteCancelDate(SysDateMgr.getLastSecond(data.getString("NEXT_PRODUCT_START_DATE")));
		}
		if (StringUtils.isBlank(nextProductId) && ProductUtils.isBookingChange(bookingDate))// 当不存在预约产品时
		// 绝对时间取选定的预约时间【如存在预约】
		{
			env.setBasicAbsoluteStartDate(bookingDate);
			env.setBasicAbsoluteCancelDate(SysDateMgr.getLastSecond(bookingDate));
		}

		IDataset elements = new DatasetList(data.getString("ELEMENTS"));
		int size = elements.size();
		for (int i = 0; i < size; i++)
		{
			IData element = elements.getData(i);

			if (StringUtils.isEmpty(element.getString("ELEMENT_ID", "")))
			{
				continue;
			}
			// start add by xuzh5 2018-7-30 17:17:03 
			 String product_id= element.getString("PRODUCT_ID","");
		     String element_id=element.getString("ELEMENT_ID","");
		     if("84013441".equals(element_id)){
		    	   if(!"20150140".equals(product_id) && !"84010438".equals(product_id))
		    		   CSAppException.appError("-1","该优惠只支持【FTTH宽带产品50M套餐】进行办理");
		    	   if("600".equals(data.getString("TRADE_TYPE_CODE", "")))
		    		   CSAppException.appError("-1","该优惠限宽带已完工进行办理");
		    	   if("606".equals(data.getString("TRADE_TYPE_CODE", "")))
		    		   CSAppException.appError("-1","请在宽带产品变更办理该优惠");
		    	   
		    	   IDataset DatasetList=ProductInfoQry.getUserProductByUserIdForGrp(data.getString("USER_ID",""));
		    	   if(DatasetList.size()>0){
		    		   String productid=DatasetList.getData(0).getString("PRODUCT_ID","");
		    		   if(!"20150140".equals(productid) && !"84010438".equals(productid))
			    		   CSAppException.appError("-1","不支持预约50M办理该优惠");
		    	   }
		    	   
		       }
		  // end add by xuzh5 2018-7-30 17:17:03 
			
			ProductModuleData pmd = null;
			if (element.getString("ELEMENT_TYPE_CODE", "").equals(BofConst.ELEMENT_TYPE_CODE_SVC))
			{
				pmd = new SvcData(element);
			} else if (BofConst.ELEMENT_TYPE_CODE_PLATSVC.equals(element.getString("ELEMENT_TYPE_CODE")))
			{
				if (BofConst.MODIFY_TAG_ADD.equals(element.getString("MODIFY_TAG")))
				{
					element.put("OPER_CODE", PlatConstants.OPER_ORDER);
				} else if (BofConst.MODIFY_TAG_DEL.equals(element.getString("MODIFY_TAG")))
				{
					element.put("OPER_CODE", PlatConstants.OPER_CANCEL_ORDER);
				}
				pmd = new PlatSvcData(element);
				pmd.setPkgElementConfig(element.getString("PACKAGE_ID"));
			} else
			{
				pmd = new DiscntData(element);

				// 如果如果用户选择了宽带营销活动，则用户不能选择宽带包年优惠
				if (StringUtils.isNotBlank(elementExtendField))
				{
					if (StringUtils.isNotBlank(pmd.getElementId()))
					{
						// 查询改优惠是否在包年优惠配置参数存在
						IDataset saleActiveList = CommparaInfoQry.getCommparaByCodeCode1("CSM", "532", "600", pmd.getElementId());

						if (IDataUtil.isNotEmpty(saleActiveList))
						{
							CSAppException.appError("-1", "选择宽带营销包后不能同时选择包年资费！");
						}

						// 查询改优惠是否在包年优惠配置参数存在
						IDataset saleActiveList2 = CommparaInfoQry.getCommparaByCodeCode1("CSM", "532", "699", pmd.getElementId());

						if (IDataUtil.isNotEmpty(saleActiveList2))
						{
							if(StringUtils.isNotBlank(saleActiveList2.getData(0).getString("PARA_CODE17", "")))
							{
								CSAppException.appError("-1", "选择宽带营销包后不能同时选择" + saleActiveList2.getData(0).getString("PARA_CODE17"));
							}else
							{
								CSAppException.appError("-1", "选择宽带营销包后不能同时选择VIP体验套餐！");
							}
							
						}
						
						// 查询改优惠是否在包年优惠配置参数存在
						IDataset discntDatasets = CommparaInfoQry.getCommparaByCodeCode1("CSM", "532", "MONTH_DISCNT", pmd.getElementId());

						if (IDataUtil.isNotEmpty(discntDatasets))
						{
							CSAppException.appError("-1", "选择宽带营销包后不能同时选择宽带包月套餐！");
						}
					}
				}
			}

			if (BofConst.MODIFY_TAG_ADD.equals(pmd.getModifyTag()))
			{
				if ("5".equals(pmd.getEnableTag()) && BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(pmd.getElementType()))
				{
					element.put("EFFECT_NOW_START_DATE", SysDateMgr.getFirstDayOfNextMonth());
					element.put("EFFECT_NOW_END_DATE", SysDateMgr.endDate(element.getString("EFFECT_NOW_START_DATE"), pmd.getEndEnableTag(), pmd.getEndAbsoluteDate(), pmd.getEndOffSet(), pmd.getEndUnit()));
				} else
				{
					element.put("EFFECT_NOW_START_DATE", sysDate);
					element.put("EFFECT_NOW_END_DATE", SysDateMgr.endDate(element.getString("EFFECT_NOW_START_DATE"), pmd.getEndEnableTag(), pmd.getEndAbsoluteDate(), pmd.getEndOffSet(), pmd.getEndUnit()));
				}
				pmd.setStartDate(null);
				String startDate = ProductModuleCalDate.calStartDate(pmd, env);
				element.put("START_DATE", startDate);

				/**
				 * REQ201509230028 调整宽带标准资费和开展免费提速 宽带包年套餐的续约的时间特殊处理 songlm
				 * 20151208
				 */
				// start
				// 判断必须是新增优惠才进
				if (BofConst.MODIFY_TAG_ADD.equals(pmd.getModifyTag()) && pmd.getElementType().equals(BofConst.ELEMENT_TYPE_CODE_DISCNT))
				{
					// 特殊配置
					IDataset specialElementTimeConfig = CommparaInfoQry.queryComparaByAttrAndCode1("CSM", "527", pmd.getElementId(), pmd.getElementType());
					if (IDataUtil.isNotEmpty(specialElementTimeConfig))
					{
						// 获取用户已有的优惠
						IDataset userDiscntElements = UserDiscntInfoQry.getNowVaildByUserIdDiscnt(data.getString("USER_ID"), pmd.getElementId());
						if (IDataUtil.isNotEmpty(userDiscntElements))
						{
							// 获取包年优惠结束时间，将结束时间的下月1日作为新优惠的开始时间
							String userDiscntEndDate = userDiscntElements.getData(0).getString("END_DATE");
							startDate = SysDateMgr.firstDayOfDate(userDiscntEndDate, 1);
							element.put("START_DATE", startDate);
						}
					}
					
					if ("601".equals(tradeTypeCode) || "606".equals(tradeTypeCode))
					{
						// 查询改优惠是否在包年优惠配置参数存在
						IDataset discntDatasets = CommparaInfoQry.getCommparaByCodeCode1("CSM", "532", "MONTH_DISCNT", pmd.getElementId());

						if (IDataUtil.isNotEmpty(discntDatasets))
						{
							CSAppException.appError("-1", "该包月优惠只能在宽带开户时选择办理！");
						}
					}
					//无手机度假宽带用户到期后续约
					if ("680".equals(tradeTypeCode) || "681".equals(tradeTypeCode) || "686".equals(tradeTypeCode))
					{
						// 查询改优惠是否在包年优惠配置参数存在
						//IDataset discntDatasets = CommparaInfoQry.getCommparaByCodeCode1("CSM", "532", "MONTH_DISCNT", pmd.getElementId());

						if ("84074442".equals(pmd.getElementId()))
						{
							CSAppException.appError("-1", "该月套餐【84074442】只能用于度假宽带用户续约时选择办理！");
						}
					}
					//无手机度假宽带用户到期后续约
				}
				// end

				pmd.setEndDate(null);
				String endDate = ProductModuleCalDate.calEndDate(pmd, startDate);
				element.put("END_DATE", endDate);
				if (data.getString("EFFECT_NOW", "").equals("true"))
				{
					element.put("OLD_EFFECT_NOW_START_DATE", startDate);
					element.put("OLD_EFFECT_NOW_END_DATE", endDate);
					element.put("START_DATE", element.getString("EFFECT_NOW_START_DATE"));
					element.put("END_DATE", element.getString("EFFECT_NOW_END_DATE"));
				}
				IDataset attrs = dealSelectedElementAttrs(element, eparchyCode);

				if (attrs != null && attrs.size() > 0)
				{
					element.put("ATTR_PARAM", attrs);
				}
				if ("3".equals(pmd.getEnableTag()) && StringUtils.isBlank(env.getBasicAbsoluteStartDate()))
				{
					element.put("CHOICE_START_DATE", "true");
				}
			} else if (BofConst.MODIFY_TAG_DEL.equals(pmd.getModifyTag()))
			{
				element.put("EFFECT_NOW_START_DATE", element.getString("START_DATE"));
				element.put("EFFECT_NOW_END_DATE", sysDate);

				pmd.setEndDate(null);
				String cancelDate = ProductModuleCalDate.calCancelDate(pmd, env);
				element.put("END_DATE", cancelDate);
				if (data.getString("EFFECT_NOW", "").equals("true"))
				{
					element.put("OLD_EFFECT_NOW_START_DATE", element.getString("START_DATE"));
					element.put("OLD_EFFECT_NOW_END_DATE", cancelDate);
					element.put("END_DATE", element.getString("EFFECT_NOW_END_DATE"));
				}

			}

			pmds.add(pmd);
		}

		// 产品元素依赖互斥校验，需要模拟台帐数据
		List<String> indexes = new ArrayList<String>();
		if (!"".equals(data.getString("TRADE_TYPE_CODE", "")))
		{
			IDataset tradeSvcs = new DatasetList();
			IDataset tradeDiscnts = new DatasetList();
			IDataset userSvcs = new DatasetList();
			IDataset userDiscnts = new DatasetList();
			for (int i = 0; i < size; i++)
			{
				IData element = elements.getData(i);
				indexes.add(element.getString("ITEM_INDEX", ""));
				IData ruleElement = new DataMap();
				ruleElement.put("USER_ID_A", "-1");
				if (BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(element.getString("ELEMENT_TYPE_CODE")))
				{
					ruleElement.put("USER_ID", data.getString("USER_ID", "-1"));
					ruleElement.put("DISCNT_CODE", element.getString("ELEMENT_ID"));
					ruleElement.putAll(element);
					if (ruleElement.getString("INST_ID", "").equals(""))
					{
						ruleElement.put("INST_ID", "" + i);
					}
					tradeDiscnts.add(ruleElement);
					userDiscnts.add(ruleElement);

				} else if (BofConst.ELEMENT_TYPE_CODE_SVC.equals(element.getString("ELEMENT_TYPE_CODE")))
				{
					ruleElement.put("USER_ID", data.getString("USER_ID", "-1"));
					ruleElement.put("SERVICE_ID", element.getString("ELEMENT_ID"));
					ruleElement.putAll(element);
					if (ruleElement.getString("INST_ID", "").equals(""))
					{
						ruleElement.put("INST_ID", "" + i);
					}
					tradeSvcs.add(ruleElement);
					userSvcs.add(ruleElement);

				}
			}
			IDataset userElements = new DatasetList(data.getString("SELECTED_ELEMENTS"));
			if (userElements != null && userElements.size() > 0)
			{
				size = userElements.size();
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
					if (BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(element.getString("ELEMENT_TYPE_CODE")))
					{
						ruleElement.put("USER_ID", data.getString("USER_ID", "-1"));
						ruleElement.put("DISCNT_CODE", element.getString("ELEMENT_ID"));
						ruleElement.putAll(element);
						if (StringUtils.isNotBlank(element.getString("NEW_PRODUCT_ID")) && StringUtils.isNotBlank(element.getString("NEW_PACKAGE_ID")))
						{
							ruleElement.put("PRODUCT_ID", element.getString("NEW_PRODUCT_ID"));
							ruleElement.put("PACKAGE_ID", element.getString("NEW_PACKAGE_ID"));
						}
						userDiscnts.add(ruleElement);
					} else if (BofConst.ELEMENT_TYPE_CODE_SVC.equals(element.getString("ELEMENT_TYPE_CODE")))
					{
						ruleElement.put("USER_ID", data.getString("USER_ID", "-1"));
						ruleElement.put("SERVICE_ID", element.getString("ELEMENT_ID"));
						ruleElement.putAll(element);
						if (StringUtils.isNotBlank(element.getString("NEW_PRODUCT_ID")) && StringUtils.isNotBlank(element.getString("NEW_PACKAGE_ID")))
						{
							ruleElement.put("PRODUCT_ID", element.getString("NEW_PRODUCT_ID"));
							ruleElement.put("PACKAGE_ID", element.getString("NEW_PACKAGE_ID"));
						}
						userSvcs.add(ruleElement);
					}
				}
			}

			IDataset tradeMains = new DatasetList();
			IData tradeMain = new DataMap();
			tradeMain.put("TRADE_EPARCHY_CODE", eparchyCode);
			tradeMain.put("TRADE_TYPE_CODE", data.getString("TRADE_TYPE_CODE"));
			tradeMain.put("IN_MODE_CODE", "0");
			tradeMain.put("USER_ID", data.getString("USER_ID"));
			tradeMains.add(tradeMain);
			IData ruleParam = new DataMap();
			ruleParam.put("TF_B_TRADE_SVC", tradeSvcs);
			ruleParam.put("TF_B_TRADE_DISCNT", tradeDiscnts);
			ruleParam.put("TF_F_USER_SVC_AFTER", userSvcs);
			ruleParam.put("TF_F_USER_DISCNT_AFTER", userDiscnts);
			ruleParam.put("IS_COMPONENT", "true");
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
						errorInfo.append(error.getString("TIPS_INFO") + "<br>");
					}
					elements.getData(0).put("ERROR_INFO", errorInfo.toString());
				}
			}
		}

		// 加载相关费用
		size = elements.size();

		// 无手机宽带要求算好金额
		String productId_old = "";
		String packageId_old = "";
		String elementId_old = "";
		String startDate_old = "";
		if ("681".equals(data.getString("TRADE_TYPE_CODE")))
		{
			for (int i = 0; i < size; i++)
			{
				IData element = elements.getData(i);
				if ("1".equals(element.getString("MODIFY_TAG")) && "D".equals(element.getString("ELEMENT_TYPE_CODE")))
				{
					productId_old = element.getString("PRODUCT_ID");
					packageId_old = element.getString("PACKAGE_ID");
					elementId_old = element.getString("ELEMENT_ID");
					startDate_old = element.getString("START_DATE");
					break;
				}
			}
		}
		String booktag = data.getString("BOOKTAG","");
		if(null == booktag || !"1".equals(booktag)){
		for (int i = 0; i < size; i++)
		{
			IData element = elements.getData(i);
			if ("0".equals(element.getString("MODIFY_TAG")))
			{
				IDataset feeDatas = new DatasetList();

				IDataset feeConfigs = ProductFeeInfoQry.getElementFeeList(data.getString("TRADE_TYPE_CODE"), BofConst.ELEMENT_TYPE_CODE_PRODUCT, element.getString("PRODUCT_ID"), element.getString("ELEMENT_TYPE_CODE"), element.getString("ELEMENT_ID"), element.getString("PACKAGE_ID"));
				if (IDataUtil.isEmpty(feeConfigs))
				{
					continue;
				}
				int feeSize = feeConfigs.size();

				for (int j = 0; j < feeSize; j++)
				{
					IData feeConfig = feeConfigs.getData(j);
					if (!"0".equals(feeConfig.getString("PAY_MODE")) || "0".equals(feeConfig.getString("FEE")))
					{
						continue;
					}
					IData feeData = new DataMap();
					feeData.put("TRADE_TYPE_CODE", data.getString("TRADE_TYPE_CODE"));
					feeData.put("MODE", feeConfig.getString("FEE_MODE"));
					feeData.put("CODE", feeConfig.getString("FEE_TYPE_CODE"));
					feeData.put("FEE", feeConfig.getString("FEE"));
					if ("600".equals(data.getString("TRADE_TYPE_CODE")) || "611".equals(data.getString("TRADE_TYPE_CODE")) || "612".equals(data.getString("TRADE_TYPE_CODE")))
					{
						// 特殊用户金额默认为零修改
						if (StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "CRM_WIDEOPENFEE"))
						{
							feeData.put("FEE", "0");
						}
					}

					if ("613".equals(data.getString("TRADE_TYPE_CODE")))
					{
						if (!"521".equals(feeConfig.getString("FEE_TYPE_CODE")))
						{
							if (StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "CRM_WIDEOPENFEE"))
							{
								feeData.put("FEE", "0");
							}
						}
					}

					// 无手机宽带要求算好金额
					if ("681".equals(data.getString("TRADE_TYPE_CODE")))
					{
						// 新宽带包年费用-（原宽带包年费用
						// -截取到角（原宽带包年费用/12）*新宽带包年产品生效之日前已经出账的月份）
						// 只存在升档重算，降档不管。
						productId_old = productId_old + "";
						packageId_old = packageId_old + "";
						elementId_old = elementId_old + "";

						String fee_new = feeConfig.getString("FEE");
						String fee_old = "0";// 取旧产品的费用

						if (StringUtils.isNotEmpty(productId_old) && StringUtils.isNotEmpty(elementId_old))
						{
							IDataset fee_old_list = ProductFeeInfoQry.getElementFeeList("681", BofConst.ELEMENT_TYPE_CODE_PRODUCT, productId_old, BofConst.ELEMENT_TYPE_CODE_DISCNT, elementId_old, packageId_old);
							if (fee_old_list != null && fee_old_list.size() > 0)
							{

								for (int k = 0; k < fee_old_list.size(); k++)
								{
									if (!"0".equals(feeConfig.getString("PAY_MODE")) || "0".equals(feeConfig.getString("FEE")))
									{
										continue;
									}

									fee_old = fee_old_list.getData(k).getString("FEE", "");
									break;
								}

								// 升档才处理
								if (Integer.parseInt(fee_new) != Integer.parseInt(fee_old))
								{
									String today = element.getString("START_DATE");
									SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
									Calendar c1 = Calendar.getInstance();
									Calendar c2 = Calendar.getInstance();
									c1.setTime(sdf.parse(today));
									c2.setTime(sdf.parse(startDate_old));
									int year1 = c1.get(Calendar.YEAR);
									int month1 = c1.get(Calendar.MONTH);
									int year2 = c2.get(Calendar.YEAR);
									int month2 = c2.get(Calendar.MONTH);
									int useMon = 0;
									if (year1 == year2)
									{
										useMon = month1 - month2;
									} else
									{
										useMon = (year1 - year2) * 12 + (month1 - month2);
									}
									// 月份大于0才计算
									if (useMon >= 0)
									{
										String paraCode8="";
										String paraCode9="";
										String paraCode10="";
										int monFee = 0;
										IDataset commparaInfos = CommparaInfoQry.getCommparaByCodeCode1("CSM","532","681",elementId_old);
							        	
							            boolean flag = true;   
						                if(DataSetUtils.isNotBlank(commparaInfos))
						                {
						                	paraCode8=commparaInfos.getData(0).getString("PARA_CODE8","");
						    				paraCode9=commparaInfos.getData(0).getString("PARA_CODE9","0");
						    				paraCode10=commparaInfos.getData(0).getString("PARA_CODE10","0");
						                    if("1".equals(paraCode8))
						                    {
						                    	flag = false;
						                    }
						                }
						                if(flag)
						                {
						                	int fee_final = Integer.parseInt(fee_new) - (Integer.parseInt(fee_old) - (Integer.parseInt(fee_old) / 12) * useMon);
											feeData.put("FEE", fee_final);
						                }else
						                {
						                	if(useMon<=11)
						                    {
						                    	monFee = Integer.parseInt(paraCode9);
						                    }else
						                    {
						                    	monFee = Integer.parseInt(paraCode10);
						                    }
						                	int fee_final = Integer.parseInt(fee_new) - (Integer.parseInt(fee_old) - monFee * useMon);
											feeData.put("FEE", fee_final);
						                }
										
										
										//int fee_final = Integer.parseInt(fee_new) - (Integer.parseInt(fee_old) - (Integer.parseInt(fee_old) / 12) * useMon);
										//feeData.put("FEE", fee_final);
										// log.info("("**********cxy******fee_final="+fee_final+"*****feeData="+feeData);
									}
								}
							}
						}
					}

					feeDatas.add(feeData);
				}
				element.put("FEE_DATA", feeDatas);
				// log.info("("**********cxy******element="+element);

			}
		}
		}
		return elements;
	}

	/**
	 * 查询管理节点所需要展现的元素 trade表数据和 user表数据综合 chenyi 13-10-26
	 * 
	 * @return
	 */
	public IDataset getBBossManageElements(IData param) throws Exception
	{

		// 查询台帐表数据
		IDataset resultElements = getGrpUserChgElementsPreTrade(param);
		IDataset tradeElements = new DatasetList();
		if (IDataUtil.isNotEmpty(resultElements))
		{
			tradeElements = resultElements.getData(0).getDataset("SELECTED_ELEMENTS");
		}

		// 查询资料表数据
		// param.put(Route.USER_EPARCHY_CODE,param.getString("EPARCHY_CODE"));
		resultElements = getGrpUserChgElements(param);
		IDataset userElements = new DatasetList();
		if (IDataUtil.isNotEmpty(resultElements))
		{
			userElements = resultElements.getData(0).getDataset("SELECTED_ELEMENTS");
		}

		// 比较资料表和台帐表数据 台账 tradeElements 资料 userElements
		// 查出来后，只要在tradeElements的列表中加上一个
		// userElements列表里不包含在tradeElements中的元素
		for (int i = 0, sizeI = tradeElements.size(); i < sizeI; i++)
		{
			IData tradeElement = tradeElements.getData(i);
			for (int j = userElements.size(); j > 0; j--)
			{
				IData userElement = userElements.getData(j - 1);
				// 如果element_id为一样就移除userElements的该元素
				if (tradeElement.getString("ELEMENT_ID").equals(userElement.getString("ELEMENT_ID")))
				{
					userElements.remove(j - 1);
				}
			}
		}

		tradeElements.addAll(userElements);

		resultElements.clear();
		IData result = new DataMap();
		result.put("SELECTED_ELEMENTS", tradeElements);
		resultElements.add(result);
		return resultElements;
	}

	/**
	 * 获取END_DATE时间通过TD_S_COMMPARA表配置
	 * 
	 * @param commpara
	 * @param elementId
	 * @param elementTypeCode
	 * @param cancelDate
	 * @return
	 * @throws Exception
	 */
	public String getCancelDateByCommpara(IDataset commpara, String elementId, String elementTypeCode, String cancelDate) throws Exception
	{
		for (int i = 0; i < commpara.size(); i++)
		{
			String oldDiscnt = commpara.getData(i).getString("PARA_CODE1");
			String workFlag = commpara.getData(i).getString("PARA_CODE3");

			if (oldDiscnt.equals(elementId) && BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(elementTypeCode))
			{
				if ("0".equals(workFlag))// 可配置para_code3,0为分散用户也结束到月末,1为结束到账期末
				{
					cancelDate = SysDateMgr.getLastDateThisMonth();
				} else
				{
					cancelDate = SysDateMgr.cancelDate("3", "", "", "");// 本账期末
				}
			}
		}

		return cancelDate;
	}

	public IDataset getElementAttrs(IData param) throws Exception
	{
		if (StringUtils.isBlank(param.getString("PRODUCT_ID")) && StringUtils.isNotBlank(param.getString("GRP_PRODUCT_ID")))
		{
			// 集团成员业务 成员产品id没传 但是传了集团产品id 这里转换一把
			param.put("PRODUCT_ID", UProductMebInfoQry.getMemberMainProductByProductId(param.getString("GRP_PRODUCT_ID")));
		}
		IDataset attrs = AttrItemInfoQry.getElementAttrs(param.getString("ELEMENT_TYPE_CODE"), param.getString("ELEMENT_ID"), param.getString("PRODUCT_ID"), param.getString("EPARCHY_CODE"));
		return attrs;
	}

	public IDataset getGrpMebChgElements(IData param) throws Exception
	{

		IData data = new DataMap();
		data.put("USER_ID", param.getString("MEB_USER_ID"));
		data.put("USER_ID_A", param.getString("GRP_USER_ID"));
		String routeEparchyCode = CSBizBean.getUserEparchyCode();
		IDataset userElement = UserSvcInfoQry.getValidElementFromPackageByUserA(param.getString("MEB_USER_ID"), param.getString("GRP_USER_ID"));

		// SP服务
		data.put("PRODUCT_ID", param.getString("PRODUCT_ID"));
		IDataset spElement = UserPlatSvcInfoQry.getGrpPlatSvcByUserId(param.getString("MEB_USER_ID"), param.getString("PRODUCT_ID"));
		userElement.addAll(spElement);

		// // 查询用户是否有只能办理一次的优惠
		// IDataset onlyList =
		// UserDiscntInfoQry.queryOnlyDiscntByUserID(param.getString("MEB_USER_ID"),
		// param.getString("PRODUCT_ID"));
		// if (IDataUtil.isNotEmpty(onlyList))
		// {
		// userElement.addAll(onlyList);
		// }

		for (int i = 0; i < userElement.size(); i++)
		{
			IData map = userElement.getData(i);
			map.put("MODIFY_TAG", "exist");

			IData tempparam = new DataMap();
			tempparam.put("PRODUCT_ID", map.getString("PRODUCT_ID", ""));
			tempparam.put("PACKAGE_ID", map.getString("PACKAGE_ID", ""));
			IData packageinfo = ProductPkgInfoQry.getProductPackageRelNoPriv(map.getString("PRODUCT_ID", ""), map.getString("PACKAGE_ID", ""), routeEparchyCode);

			map.put("PACKAGE_FORCE_TAG", IDataUtil.isEmpty(packageinfo) ? "0" : packageinfo.getString("FORCE_TAG", "0"));

			tempparam.put("ELEMENT_TYPE_CODE", map.getString("ELEMENT_TYPE_CODE", ""));
			tempparam.put("ELEMENT_ID", map.getString("ELEMENT_ID", ""));

			IDataset pkgelementset = UPackageElementInfoQry.queryElementInfosByElementData(map);

			map.put("ELEMENT_FORCE_TAG", IDataUtil.isEmpty(pkgelementset) ? "0" : pkgelementset.getData(0).getString("FORCE_TAG", "0"));

			// ADC、MAS弹窗
			IDataset adcPara = new DatasetList();
			if (map.getString("ELEMENT_TYPE_CODE", "").equals(BofConst.ELEMENT_TYPE_CODE_SVC))
			{
				adcPara = AttrBizInfoQry.getBizAttr(map.getString("ELEMENT_ID", ""), "S", "ServPage", map.getString("PRODUCT_ID", ""), null);
			}
			if (IDataUtil.isNotEmpty(adcPara))
			{
				IDataset userattrs = new DatasetList();
				IData userattr = new DataMap();

				userattr.put("PARAM_VERIFY_SUCC", "true");
				userattrs.add(userattr);
				map.put("ATTR_PARAM", userattrs);
			} else
			{

				IDataset elementParamdataset = UserAttrInfoQry.getUserAttrByUserIdInstid(param.getString("USER_ID"), map.getString("ELEMENT_TYPE_CODE", ""), map.getString("INST_ID", ""));
				if (elementParamdataset != null && elementParamdataset.size() > 0)
				{
					IDataset userAttrs = new DatasetList();

					// 属性表中fixfee相关取值除100
					for (int j = 0; j < elementParamdataset.size(); j++)
					{

						IData servparam = (IData) elementParamdataset.get(j);
						IData userAttr = new DataMap();
						String attrcode = servparam.getString("ATTR_CODE", "");
						String attrvalue = servparam.getString("ATTR_VALUE", "");
						userAttr.put("ATTR_CODE", attrcode);
						userAttr.put("ATTR_VALUE", attrvalue);

						IDataset grpFeeList = StaticUtil.getStaticList("GROUP_TRASN_FEE", attrcode);
						if (IDataUtil.isNotEmpty(grpFeeList))
						{
							userAttr.put("ATTR_VALUE", Integer.parseInt(attrvalue) / 100);
						}
						userAttrs.add(userAttr);
					}

					IDataset attrItemAList = AttrItemInfoQry.getElementItemA(map.getString("ELEMENT_TYPE_CODE", ""), map.getString("ELEMENT_ID"), routeEparchyCode);
					IDataset attrs = this.makeAttrs(userAttrs, attrItemAList);
					map.put("ATTR_PARAM", attrs);
				}
			}
		}

		IDataset resultList = new DatasetList();
		IData result = new DataMap();
		result.put("SELECTED_ELEMENTS", userElement);
		resultList.add(result);
		return resultList;
	}

	public IDataset getGrpMebOpenElements(IData param) throws Exception
	{

		IDataset selectedElements = new DatasetList();
		String baseProductId = param.getString("PRODUCT_ID");
		if (StringUtils.isBlank(baseProductId))
		{
			return new DatasetList();
		}
		String eparchyCode = CSBizBean.getUserEparchyCode();
		boolean effectNow = param.getBoolean("EFFECT_NOW", false);
		String bookingDate = param.getString("PRODUCT_PRE_DATE", "");
		String busi_type = param.getString("BUSI_TYPE", "");
		String meb_user_id = param.getString("MEB_USER_ID", "");
		String page_flag = param.getString("page", "");
		String grp_user_id = param.getString("GRP_USER_ID", "");
		if ("10005744".equals(baseProductId))// 如果是学护卡批量新增和注销时立即生效
		{
			effectNow = true;
		}
		IDataset forceSvcs = ProductInfoQry.getMebProductForceSvc(baseProductId, eparchyCode);
		if (forceSvcs != null && forceSvcs.size() > 0)
		{
			int size = forceSvcs.size();
			for (int i = 0; i < size; i++)
			{
				IData svc = forceSvcs.getData(i);
				ElementUtil.dealSelectedElementStartDateAndEndDate(svc, bookingDate, effectNow, eparchyCode);

				// ADC、MAS弹窗
				IDataset adcset = AttrBizInfoQry.getBizAttr(svc.getString("ELEMENT_ID", ""), "S", "ServPage", svc.getString("PRODUCT_ID", ""), null);
				if (IDataUtil.isNotEmpty(adcset))
				{
					svc.put("ATTR_PARAM_TYPE", "9");
					IData serviceInparam = new DataMap();
					serviceInparam.put("USER_ID_A", param.getString("GRP_USER_ID"));
					serviceInparam.put("PRODUCT_ID", svc.getString("PRODUCT_ID", ""));
					serviceInparam.put("PACKAGE_ID", svc.getString("PACKAGE_ID", ""));
					serviceInparam.put("SERVICE_ID", svc.getString("ELEMENT_ID", ""));
					serviceInparam.put("EPARCHY_CODE", eparchyCode);
					IDataset serviceparamset = CSAppCall.call("CS.DealSpecialServerParamSvc.loadSpecialServerParam", serviceInparam);
					svc.put("ATTR_PARAM", serviceparamset);
					selectedElements.add(svc);
				} else
				{
					IDataset attrItemAList = AttrItemInfoQry.getElementItemA("S", svc.getString("ELEMENT_ID"), eparchyCode);
					IDataset attrs = this.makeAttrs(null, attrItemAList);
					svc.put("ATTR_PARAM", attrs);
					selectedElements.add(svc);
				}

			}
		}

		IDataset forceDiscnts = ProductInfoQry.getMebProductForceDiscnt(baseProductId, eparchyCode);
		if (forceDiscnts != null && forceDiscnts.size() > 0)
		{
			int size = forceDiscnts.size();
			for (int i = 0; i < size; i++)
			{
				IData discnt = forceDiscnts.getData(i);
				ElementUtil.dealSelectedElementStartDateAndEndDate(discnt, bookingDate, effectNow, eparchyCode);
				IDataset attrItemAList = AttrItemInfoQry.getElementItemA("D", discnt.getString("ELEMENT_ID"), eparchyCode);
				IDataset attrs = this.makeAttrs(null, attrItemAList);
				if("960402".equals(baseProductId) && "96041177".equals(discnt.getString("ELEMENT_ID")))
				{
					 IDataset attrInfos = UserAttrInfoQry.getUserAttrByUserId(grp_user_id,"000117601"); //获取新增时赠送有效期的值
			            if(IDataUtil.isNotEmpty(attrInfos))
			            {
			            	attrs.getData(0).put("ATTR_VALUE",attrInfos.getData(0).getString("ATTR_VALUE"));
			            }
					
				}
				discnt.put("ATTR_PARAM", attrs);

				if ("6013".equals(baseProductId) && ("CrtMb".equals(busi_type) || "group.bat.batworkphonecreatmember.CreatMemberBatByWP".equals(page_flag)))
				{
					int sum = UserDiscntInfoQry.getDiscntByMUIdToCommpara(meb_user_id, discnt.getString("ELEMENT_ID", ""), grp_user_id);
					if (sum == 0)
					{
						continue;
					}
				}

				selectedElements.add(discnt);
			}
		}

		IDataset forcePlatsvcs = ProductInfoQry.getMebProductForcePlatSvc(baseProductId, eparchyCode);
		if (forcePlatsvcs != null && forcePlatsvcs.size() > 0)
		{
			int size = forcePlatsvcs.size();
			for (int i = 0; i < size; i++)
			{
				IData platSvc = forcePlatsvcs.getData(i);
				ElementUtil.dealSelectedElementStartDateAndEndDate(platSvc, bookingDate, effectNow, eparchyCode);
				selectedElements.add(platSvc);
			}
		}

		DataHelper.sort(selectedElements, "ELEMENT_TYPE_CODE", IDataset.TYPE_STRING, IDataset.ORDER_ASCEND, "MODIFY_TAG", IDataset.TYPE_STRING, IDataset.ORDER_ASCEND);

		IData result = new DataMap();
		IDataset resultList = new DatasetList();
		result.put("SELECTED_ELEMENTS", selectedElements);
		resultList.add(result);
		return resultList;
	}

	public IDataset getGrpUserChgElements(IData param) throws Exception
	{
		String userId = param.getString("USER_ID");
		String productId = param.getString("PRODUCT_ID");
		String userEparchyCode = CSBizBean.getUserEparchyCode();
		if (userId == null || "".equals(userId))
		{
			return null;
		}

		String brandCode = UProductInfoQry.getBrandCodeByProductId(productId);

		IDataset userElement = new DatasetList();
		if (brandCode.equals("DLBG"))
		{
			String relationTypeCode = UProductCompInfoQry.getRelationTypeCodeByProductId(productId);
			IDataset uuInfos = RelaUUInfoQry.getRelaUUInfoByUserIdaForGrp(userId, relationTypeCode, null);
			if (IDataUtil.isNotEmpty(uuInfos))
			{
				for (int i = 0; i < uuInfos.size(); i++)
				{
					// 动力100服务挂在主产品上，资费分别挂在子产品上
					userElement = UserSvcInfoQry.getValidElementFromPackageByUserAndUserA(uuInfos.getData(i).getString("USER_ID_B", ""), userId);
					if (IDataUtil.isNotEmpty(userElement))
					{
						break;
					}
				}

				for (int i = 0; i < userElement.size(); i++)
				{
					IData map = userElement.getData(i);

					// 动力100资费转换
					IData meberUserInfo = UserInfoQry.getGrpUserInfoByUserIdForGrp(map.getString("USER_ID"), "0");
					if (IDataUtil.isNotEmpty(meberUserInfo))
					{
						IDataset datas = AttrBizInfoQry.getBizAttrByDynamic(meberUserInfo.getString("PRODUCT_ID"), "D", "DIS", map.getString("ELEMENT_ID"), null);
						if (datas != null && datas.size() > 0)
						{
							map.put("ELEMENT_ID", datas.getData(0).getString("ATTR_VALUE"));
							IData discntinfo = UDiscntInfoQry.getDiscntInfoByPk(map.getString("ELEMENT_ID"));
							if (IDataUtil.isNotEmpty(discntinfo))
								map.put("ELEMENT_NAME", discntinfo.getString("DISCNT_NAME"));
						}
					}

					map.put("MODIFY_TAG", "exist");

					IData packageinfo = ProductPkgInfoQry.getProductPackageRelNoPriv(map.getString("PRODUCT_ID", ""), map.getString("PACKAGE_ID", ""), null);
					map.put("PACKAGE_FORCE_TAG", IDataUtil.isEmpty(packageinfo) ? "0" : packageinfo.getString("FORCE_TAG", "0"));

					IDataset pkgelementset = UPackageElementInfoQry.queryElementInfosByElementData(map);

					map.put("ELEMENT_FORCE_TAG", IDataUtil.isEmpty(pkgelementset) ? "0" : pkgelementset.getData(0).getString("FORCE_TAG", "0"));

					IDataset elementParamdataset = UserAttrInfoQry.getUserAttrByUserIdInstid(map.getString("USER_ID"), map.getString("ELEMENT_TYPE_CODE", ""), map.getString("INST_ID", ""));
					if (elementParamdataset != null && elementParamdataset.size() > 0)
					{
						IDataset userAttrs = new DatasetList();

						for (int j = 0; j < elementParamdataset.size(); j++)
						{

							IData servparam = (IData) elementParamdataset.get(j);
							IData userAttr = new DataMap();
							String attrcode = servparam.getString("ATTR_CODE", "");
							String attrvalue = servparam.getString("ATTR_VALUE", "");
							userAttr.put("ATTR_CODE", attrcode);
							userAttr.put("ATTR_VALUE", attrvalue);

							IDataset grpFeeList = StaticUtil.getStaticList("GROUP_TRASN_FEE", attrcode);
							if (IDataUtil.isNotEmpty(grpFeeList))
							{
								userAttr.put("ATTR_VALUE", Integer.parseInt(attrvalue) / 100);
							}

							userAttrs.add(userAttr);
						}

						map.put("ATTR_PARAM", userAttrs);
					}

					map.put("USER_ID", userId);
				}

			}
		} else
		{
			IData temp = new DataMap();
			temp.put("PRODUCT_ID", productId);
			temp.put("USER_ID", userId);
			userElement = UserSvcInfoQry.getElementFromPackageByUser(userId, productId, null);

			// IDataset plusProducts =
			// ProductInfoQry.getPlusProductByProdId(userEparchyCode,
			// productId);
			// if (IDataUtil.isNotEmpty(plusProducts))
			// {
			// for (int k = 0; k < plusProducts.size(); k++)
			// {
			// IDataset tempUserElement =
			// UserSvcInfoQry.getElementFromPackageByUser(userId,
			// plusProducts.getData(k).getString("PRODUCT_ID"), null);
			// if (IDataUtil.isNotEmpty(tempUserElement))
			// userElement.addAll(tempUserElement);
			// }
			// }

			for (int i = 0; i < userElement.size(); i++)
			{
				IData map = userElement.getData(i);
				String elementTypeCode = map.getString("ELEMENT_TYPE_CODE", "");
				map.put("MODIFY_TAG", "exist");

				IData packageinfo = ProductPkgInfoQry.getProductPackageRelNoPriv(map.getString("PRODUCT_ID", ""), map.getString("PACKAGE_ID", ""), userEparchyCode);
				map.put("PACKAGE_FORCE_TAG", IDataUtil.isEmpty(packageinfo) ? "0" : packageinfo.getString("FORCE_TAG", "0"));

				IDataset pkgelementset = UPackageElementInfoQry.queryElementInfosByElementData(map);
				map.put("ELEMENT_FORCE_TAG", IDataUtil.isEmpty(pkgelementset) ? "0" : pkgelementset.getData(0).getString("FORCE_TAG", "0"));

				// ADC、MAS弹窗
				IDataset adcPara = new DatasetList();
				if (elementTypeCode.equals(BofConst.ELEMENT_TYPE_CODE_SVC))
				{
					// adcPara =
					// AttrItemInfoQry.getAttrItemAByADC(map.getString("ELEMENT_ID",
					// ""), "S", userEparchyCode, "9");
					adcPara = AttrBizInfoQry.getBizAttr(map.getString("ELEMENT_ID", ""), "S", "ServPage", productId, null);
				}

				if (IDataUtil.isNotEmpty(adcPara))
				{

					map.put("ATTR_PARAM_TYPE", "9");
					IData serviceInparam = new DataMap();
					serviceInparam.put("USER_ID", userId);
					serviceInparam.put("PRODUCT_ID", map.getString("PRODUCT_ID", ""));
					serviceInparam.put("PACKAGE_ID", map.getString("PACKAGE_ID", ""));
					serviceInparam.put("SERVICE_ID", map.getString("ELEMENT_ID", ""));
					serviceInparam.put("EPARCHY_CODE", userEparchyCode);
					IDataset serviceparamset = CSAppCall.call("CS.DealSpecialServerParamSvc.loadSpecialServerParam", serviceInparam);
					map.put("ATTR_PARAM", serviceparamset);

				} else
				{
					IDataset elementParamdataset = UserAttrInfoQry.getUserAttrByUserIdInstid(userId, map.getString("ELEMENT_TYPE_CODE", ""), map.getString("INST_ID", ""));
					if (elementParamdataset != null && elementParamdataset.size() > 0)
					{
						IDataset userAttrs = new DatasetList();

						// 属性表中fixfee相关取值除100
						for (int j = 0; j < elementParamdataset.size(); j++)
						{

							IData servparam = (IData) elementParamdataset.get(j);
							IData userAttr = new DataMap();
							String attrcode = servparam.getString("ATTR_CODE", "");
							String attrvalue = servparam.getString("ATTR_VALUE", "");
							userAttr.put("ATTR_CODE", attrcode);
							userAttr.put("ATTR_VALUE", attrvalue);

							// 特殊处理公交WIFI流量总额的展示值，因为之前账务需要集团传流量总额的值为字节，所以这里要转为M。
							if ("7362".equals(attrcode))
							{
								long attrvalueTmp = Long.parseLong(attrvalue) / 1024 / 1024;
								attrvalue = String.valueOf(attrvalueTmp);
								userAttr.put("ATTR_VALUE", attrvalue);
							}
							if("100001".equals(attrcode))
							{
								attrvalue = attrvalue.concat("Mb");
								userAttr.put("ATTR_VALUE", attrvalue);
								
							}

							IDataset grpFeeList = StaticUtil.getStaticList("GROUP_TRASN_FEE", attrcode);
							if (IDataUtil.isNotEmpty(grpFeeList))
							{
								if ("30011117".equals(attrcode) || "30011107".equals(attrcode) || "30011337".equals(attrcode) || "30011327".equals(attrcode) || "30011137".equals(attrcode) || "30011127".equals(attrcode) || "30011237".equals(attrcode) || "30011227".equals(attrcode) || "40011107".equals(attrcode) || "40011108".equals(attrcode) || "40011127".equals(attrcode) || "40011128".equals(attrcode) || "40011227".equals(attrcode) || "40011228".equals(attrcode) || "40011327".equals(attrcode) || "40011328".equals(attrcode))
								{
									userAttr.put("ATTR_VALUE", Float.parseFloat(attrvalue) / 1000);

								} else if ("30011118".equals(attrcode) || "30011108".equals(attrcode) || "30011338".equals(attrcode) || "30011328".equals(attrcode) || "30011138".equals(attrcode) || "30011128".equals(attrcode) || "30011238".equals(attrcode) || "30011228".equals(attrcode))
								{
									userAttr.put("ATTR_VALUE", Integer.parseInt(attrvalue) / 100);

								} else
								{
									userAttr.put("ATTR_VALUE", Integer.parseInt(attrvalue) / 100);

								}

							}
							IDataset grpFeeListBy = StaticUtil.getStaticList("GROUP_TRASN_FEE_BY", attrcode);
							if (IDataUtil.isNotEmpty(grpFeeListBy))
							{
								if ("30011116".equals(attrcode) || "30011106".equals(attrcode) || "30011336".equals(attrcode) || "30011326".equals(attrcode))
								{
									userAttr.put("ATTR_VALUE", Integer.parseInt(attrvalue) / 100);

								} else if ("30011136".equals(attrcode) || "30011126".equals(attrcode) || "30011236".equals(attrcode) || "30011226".equals(attrcode))
								{
									userAttr.put("ATTR_VALUE", Integer.parseInt(attrvalue) / 300);

								}
							}

							userAttrs.add(userAttr);
						}
						IDataset attrItemAList = AttrItemInfoQry.getElementItemA(map.getString("ELEMENT_TYPE_CODE", ""), map.getString("ELEMENT_ID"), userEparchyCode);
						IDataset attrs = this.makeAttrs(userAttrs, attrItemAList);
						map.put("ATTR_PARAM", attrs);
					}
				}
			}

		}

		IDataset resultList = new DatasetList();
		IData result = new DataMap();
		result.put("SELECTED_ELEMENTS", userElement);
		resultList.add(result);
		return resultList;

	}

	/**
	 * 获取预受理台账中的元素信息
	 * 
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public IDataset getGrpUserChgElementsPreTrade(IData param) throws Exception
	{

		String tradeId = param.getString("TRADE_ID", "");
		if (tradeId == null || "".equals(tradeId))
		{
			return null;
		}

		IDataset userElement = new DatasetList();
		// 查台帐表
		IData temp = new DataMap();
		temp.put("TRADE_ID", tradeId);
		userElement = TradeSvcInfoQry.getElementFromPackageByTradeId(tradeId);

		for (int i = 0; i < userElement.size(); i++)
		{
			IData map = userElement.getData(i);
			String elementTypeCode = map.getString("ELEMENT_TYPE_CODE", "");

			IData packageinfo = ProductPkgInfoQry.getProductPackageRelNoPriv(map.getString("PRODUCT_ID", ""), map.getString("PACKAGE_ID", ""), null);
			map.put("PACKAGE_FORCE_TAG", IDataUtil.isEmpty(packageinfo) ? "0" : packageinfo.getString("FORCE_TAG", "0"));

			IDataset pkgelementset = UPackageElementInfoQry.queryElementInfosByElementData(map);

			map.put("ELEMENT_FORCE_TAG", IDataUtil.isEmpty(pkgelementset) ? "0" : pkgelementset.getData(0).getString("FORCE_TAG", "0"));

			IDataset elementParamdataset = TradeAttrInfoQry.getUserAttrByUserIdInstid(tradeId, map.getString("ELEMENT_TYPE_CODE", ""), map.getString("INST_ID", ""));
			if (elementParamdataset != null && elementParamdataset.size() > 0)
			{
				IDataset userAttrs = new DatasetList();

				// 属性表中fixfee相关取值除100
				for (int j = 0; j < elementParamdataset.size(); j++)
				{

					IData servparam = (IData) elementParamdataset.get(j);
					IData userAttr = new DataMap();
					String attrcode = servparam.getString("ATTR_CODE", "");
					String attrvalue = servparam.getString("ATTR_VALUE", "");
					userAttr.put("ATTR_CODE", attrcode);
					userAttr.put("ATTR_VALUE", attrvalue);

					IDataset grpFeeList = StaticUtil.getStaticList("GROUP_TRASN_FEE", attrcode);
					if (IDataUtil.isNotEmpty(grpFeeList))
					{
						userAttr.put("ATTR_VALUE", Integer.parseInt(attrvalue) / 100);
					}

					userAttrs.add(userAttr);
				}

				map.put("ATTR_PARAM", userAttrs);
			}

		}
		IDataset resultList = new DatasetList();
		IData result = new DataMap();
		result.put("SELECTED_ELEMENTS", userElement);
		resultList.add(result);
		return resultList;

	}

	public IDataset getGrpUserOpenElements(IData param) throws Exception
	{

		IDataset selectedElements = new DatasetList();
		String baseProductId = param.getString("PRODUCT_ID");
		String eparchyCode = CSBizBean.getUserEparchyCode();
		boolean effectNow = param.getBoolean("EFFECT_NOW", false);
		IDataset forceSvcs = ProductInfoQry.getProductForceSvc(baseProductId, eparchyCode);
		if (forceSvcs != null && forceSvcs.size() > 0)
		{
			int size = forceSvcs.size();
			for (int i = 0; i < size; i++)
			{
				boolean flag = false;
				IData svc = forceSvcs.getData(i);
				ElementUtil.dealSelectedElementStartDateAndEndDate(svc, null, effectNow, eparchyCode);
				IData tempparam = new DataMap();
				tempparam.put("ID", svc.getString("ELEMENT_ID", ""));
				tempparam.put("ID_TYPE", "S");
				tempparam.put("ATTR_TYPE_CODE", "9");

				// ADC、MAS弹窗
				IDataset adcset = AttrBizInfoQry.getBizAttr(svc.getString("ELEMENT_ID", ""), "S", "ServPage", baseProductId, null);
				if (IDataUtil.isNotEmpty(adcset))
				{
					svc.put("ATTR_PARAM_TYPE", "9");
					IData serviceInparam = new DataMap();
					serviceInparam.put("USER_ID", "");
					serviceInparam.put("GROUP_ID", param.getString("GROUP_ID", ""));
					serviceInparam.put("CUST_ID", param.getString("CUST_ID", ""));
					serviceInparam.put("PRODUCT_ID", svc.getString("PRODUCT_ID", ""));
					serviceInparam.put("PACKAGE_ID", svc.getString("PACKAGE_ID", ""));
					serviceInparam.put("SERVICE_ID", svc.getString("ELEMENT_ID", ""));
					serviceInparam.put("EPARCHY_CODE", eparchyCode);
					IDataset serviceparamset = CSAppCall.call("CS.DealSpecialServerParamSvc.loadSpecialServerParam", serviceInparam);
					svc.put("ATTR_PARAM", serviceparamset);
					selectedElements.add(svc);
				} else
				{
					IDataset attrItemAList = AttrItemInfoQry.getElementItemA("S", svc.getString("ELEMENT_ID"), eparchyCode);
					IDataset attrs = this.makeAttrs(null, attrItemAList);
					svc.put("ATTR_PARAM", attrs);
					selectedElements.add(svc);
				}

			}
		}

		IDataset forceDiscnts = ProductInfoQry.getProductForceDiscnt(baseProductId, eparchyCode);
		if (forceDiscnts != null && forceDiscnts.size() > 0)
		{
			int size = forceDiscnts.size();
			for (int i = 0; i < size; i++)
			{
				IData discnt = forceDiscnts.getData(i);
				ElementUtil.dealSelectedElementStartDateAndEndDate(discnt, null, effectNow, eparchyCode);

				IDataset attrItemAList = AttrItemInfoQry.getElementItemA("D", discnt.getString("ELEMENT_ID"), eparchyCode);
				IDataset attrs = this.makeAttrs(null, attrItemAList);
				discnt.put("ATTR_PARAM", attrs);

				selectedElements.add(discnt);
			}
		}

		DataHelper.sort(selectedElements, "ELEMENT_TYPE_CODE", IDataset.TYPE_STRING, IDataset.ORDER_ASCEND, "MODIFY_TAG", IDataset.TYPE_STRING, IDataset.ORDER_ASCEND);

		IData result = new DataMap();
		IDataset resultList = new DatasetList();
		result.put("SELECTED_ELEMENTS", selectedElements);
		resultList.add(result);
		return resultList;
	}

	private String getProductChangeDate(String oldProductId, String newProductId, IData temp) throws Exception
	{

		String productChangeDate = null;
		IData productTran = UpcCall.queryOfferTransOffer(oldProductId, "P", newProductId, "P");
		if (productTran != null && productTran.size() > 0)
		{
			temp.putAll(productTran);
			String enableTag = productTran.getString("ENABLE_MODE");

			if (enableTag.equals("0"))
			{// 立即生效
				productChangeDate = SysDateMgr.getSysTime();
			} else if ((enableTag.equals("1")) || (enableTag.equals("2")))
			{// 下帐期生效

				productChangeDate = SysDateMgr.getFirstDayOfNextMonth();
			} else if (enableTag.equals("3"))
			{
				// 按原产品的生效方效

				IData productInfo = UProductInfoQry.qryProductByPK(oldProductId);
				String enableTagOld = productInfo.getString("ENABLE_TAG");

				if ((enableTagOld.equals("0")) || (enableTagOld.equals("2")))
				{// 立即生效
					productChangeDate = SysDateMgr.getSysTime();
				} else if (enableTagOld.equals("1"))
				{// 下帐期生效

					productChangeDate = SysDateMgr.getFirstDayOfNextMonth();
				}
			} else if (enableTag.equals("4"))
			{
				// 按新产品的生效方式

				IData productInfo = UProductInfoQry.qryProductByPK(newProductId);
				String enableTagNew = productInfo.getString("NEW_PRODUCT_ID");

				if ((enableTagNew.equals("0")) || (enableTagNew.equals("2")))
				{// 立即生效
					productChangeDate = SysDateMgr.getSysTime();
				} else if (enableTagNew.equals("1"))
				{// 下帐期生效

					productChangeDate = SysDateMgr.getFirstDayOfNextMonth();
				}
			}
		}
		return productChangeDate;
	}

	private IData getProductDefaultLong(IDataset productElements) throws Exception
	{
		int size = productElements.size();
		for (int i = 0; i < size; i++)
		{
			IData element = productElements.getData(i);
			String elementId = element.getString("ELEMENT_ID");
			if ("1".equals(element.getString("ELEMENT_DEFAULT_TAG")) && ("13".equals(elementId) || "14".equals(elementId) || "15".equals(elementId)) && (BofConst.ELEMENT_TYPE_CODE_SVC.equals(element.getString("ELEMENT_TYPE_CODE"))))
			{
				return element;
			}
		}
		return null;
	}

	private IData getProductDefaultRoam(IDataset productElements) throws Exception
	{
		int size = productElements.size();
		for (int i = 0; i < size; i++)
		{
			IData element = productElements.getData(i);
			String elementId = element.getString("ELEMENT_ID");
			if ("1".equals(element.getString("ELEMENT_DEFAULT_TAG")) && ("16".equals(elementId) || "17".equals(elementId) || "18".equals(elementId) || "19".equals(elementId) || "100".equals(elementId) || "101".equals(elementId) || "122".equals(elementId) || "134".equals(elementId)) && (BofConst.ELEMENT_TYPE_CODE_SVC.equals(element.getString("ELEMENT_TYPE_CODE"))))
			{
				return element;
			}
		}
		return null;
	}

	private IDataset getProductForceElement(IDataset productElements) throws Exception
	{
		int size = productElements.size();
		IDataset result = new DatasetList();
		for (int i = 0; i < size; i++)
		{
			IData element = productElements.getData(i);
			if ("1".equals(element.getString("PACKAGE_FORCE_TAG")) && "1".equals(element.getString("ELEMENT_FORCE_TAG")))
			{
				result.add(element);
			}
		}
		// 默认元素
		for (int i = 0; i < size; i++)
		{
			IData element = productElements.getData(i);
			if ("1".equals(element.getString("ELEMENT_DEFAULT_TAG")) && !"1".equals(element.getString("ELEMENT_FORCE_TAG")))
			{
				// 为了防止互斥报错，漫游和长途等服务不需要处理。
				String element_id = "|" + element.getString("ELEMENT_ID") + "|";
				if ("|14|15|18|19|".indexOf(element_id) < 0)
				{
					result.add(element);
				}
			}
		}
		return result;
	}

	private IData getTransElement(IDataset productElements, String elementId, String elementType)
	{

		if (productElements == null || productElements.size() <= 0)
		{
			return null;
		}
		int size = productElements.size();
		for (int i = 0; i < productElements.size(); i++)
		{
			IData element = productElements.getData(i);
			if (element.getString("ELEMENT_ID").equals(elementId) && element.getString("ELEMENT_TYPE_CODE").equals(elementType))
			{
				return element;
			}
		}
		return null;
	}

	private IData getTransElementforNetNp(IDataset productElements, IData element, String inEparchyCode) throws Exception
	{
		IData temp = new DataMap();
		String elementId = element.getString("ELEMENT_ID");
		String elementType = element.getString("ELEMENT_TYPE_CODE");
		String productId = element.getString("PRODUCT_ID");
		String packageId = element.getString("PACKAGE_ID");

		if (IDataUtil.isEmpty(productElements))
		{
			return null;
		}

		for (int i = 0; i < productElements.size(); i++)
		{
			IData productElement = productElements.getData(i);
			if (productElement.getString("ELEMENT_ID").equals(elementId) && productElement.getString("ELEMENT_TYPE_CODE").equals(elementType))
			{
				temp = productElement;
				break;
			}
		}

		if (IDataUtil.isEmpty(temp) && BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(elementType))// 这里还要判断该优惠是否为平台服务绑定优惠,是否为全省,地州统一优惠
		{
			if ("50000000".equals(productId))// 判断是否为平台优惠
			{
				temp = element;
				temp.put("DISABLED", "true");// 平台优惠不可以取消,因为和平台服务绑定了.
			} else
			// 判断是否为全省或地州优惠
			{
				// 全省优惠配置在参数550中
				IDataset ids = CommparaInfoQry.getCommparaAllCol("CSM", "550", elementId, inEparchyCode);

				if (IDataUtil.isNotEmpty(ids))// 为全省优惠,要继承
				{
					temp = element;
				} else
				{
					if (elementId.length() == 8)// 优惠不是8位，不处理
					{
						IDataset cityDiscnt = CommparaInfoQry.getCommparaAllCol("CSM", "551", elementId.substring(2, 8), inEparchyCode);

						if (IDataUtil.isNotEmpty(cityDiscnt))// 统一支撑的优惠(地州)
						{
							temp = element;
							// 更新产品,包,优惠ID
							String newProduct = "-1";
							String newElementId = "-1";
							String newpackageId = "-1";

							if (productId.length() == 8)
							{
								newProduct = inEparchyCode.substring(2, 4) + productId.substring(2, 8);// 携入地EPARCHY_CODE后2位+产品ID后6位
							}

							if (packageId.length() == 8)
							{
								newpackageId = inEparchyCode.substring(2, 4) + packageId.substring(2, 8);// 携入地EPARCHY_CODE后2位+包ID后6位
							} else
							{
								newpackageId = cityDiscnt.getData(0).getString("PARA_CODE1");
							}

							newElementId = inEparchyCode.substring(2, 4) + elementId.substring(2, 8);// 地州统一优惠编码为携入地后2位+优惠编码后6位

							temp.put("PRODUCT_ID", newProduct);
							temp.put("ELEMENT_ID", newElementId);
							temp.put("PACKAGE_ID", newpackageId);
							temp.put("DISABLED", "true");// 能继承的全省和地州统一营销活动不能取消
						}
					}

				}
			}
		}
		return temp;
	}

	private IDataset getUserAllDiscnt(String userId, String outEparchyCode) throws Exception
	{
		// 查询用户所有的优惠
		IDataset userDiscntElementsAll = UserDiscntInfoQry.queryAllUserDiscntsInSelectedElements(userId, outEparchyCode);
		// 查询用户主产品和附加产品的优惠
		IDataset userDiscntElementsProduct = UserDiscntInfoQry.queryUserDiscntsInSelectedElements(userId, outEparchyCode);

		int size = userDiscntElementsProduct.size();

		if (size != userDiscntElementsAll.size())// 如果查询出来的数量是一样的,则数据内容也是一样的.就不用循环判断了.
		{
			// 合并用户的优惠,因为查询所有的优惠时候没有查询必选包和必选元素
			for (int i = 0; i < userDiscntElementsAll.size(); i++)
			{
				boolean flag = false;
				for (int k = 0; k < size; k++)
				{
					if (userDiscntElementsAll.getData(i).getString("ELEMENT_ID").equals(userDiscntElementsProduct.getData(k).getString("ELEMENT_ID")))
					{
						flag = true;
						break;
					}
				}

				if (!flag)
				{
					userDiscntElementsProduct.add(userDiscntElementsAll.getData(i));
				}

			}
		}

		return userDiscntElementsProduct;
	}

	private IDataset getUserAllSvc(String userId, String outEparchyCode) throws Exception
	{
		// 查询用户所有的服务
		IDataset userSvcElementsAll = UserSvcInfoQry.queryAllUserSvcsInSelectedElements(userId, outEparchyCode);
		// 查询用户主产品和附加产品的服务
		IDataset userSvcElementsProduct = UserSvcInfoQry.queryUserSvcsInSelectedElements(userId, outEparchyCode);

		int size = userSvcElementsProduct.size();

		if (size != userSvcElementsAll.size())// 如果查询出来的数量是一样的,则数据内容也是一样的.就不用循环判断了.
		{
			// 合并用户的优惠,因为查询所有的优惠时候没有查询必选包和必选元素
			for (int i = 0; i < userSvcElementsAll.size(); i++)
			{
				boolean flag = false;
				for (int k = 0; k < size; k++)
				{
					if (userSvcElementsAll.getData(i).getString("ELEMENT_ID").equals(userSvcElementsProduct.getData(k).getString("ELEMENT_ID")))
					{
						flag = true;
						break;
					}
				}

				if (!flag)
				{
					userSvcElementsProduct.add(userSvcElementsAll.getData(i));
				}

			}
		}

		return userSvcElementsProduct;
	}

	private IDataset getUserAttrByRelaInstId(IDataset userAttrs, String relaInstId)
	{

		IDataset temp = new DatasetList();
		int size = userAttrs.size();
		for (int i = 0; i < size; i++)
		{
			IData userAttr = userAttrs.getData(i);
			if (relaInstId.equals(userAttr.getString("RELA_INST_ID")))
			{
				IData map = new DataMap();
				map.put("ATTR_CODE", userAttr.getString("ATTR_CODE"));
				map.put("ATTR_VALUE", userAttr.getString("ATTR_VALUE"));
				temp.add(map);
			}
		}
		return temp;
	}

	public IDataset getUserElements(IData param) throws Exception
	{

		String eparchyCode = param.getString("EPARCHY_CODE");
		this.setRouteId(eparchyCode);
		IData result = new DataMap();
		IDataset resultList = new DatasetList();
		String userId = param.getString("USER_ID");
		if (userId == null || "".equals(userId))
		{
			return null;
		}

		IDataset userSvcElements = UserSvcInfoQry.queryUserSvcsInSelectedElements(userId);
		IDataset userDiscntElements = UserDiscntInfoQry.queryUserDiscntsInSelectedElements(userId);

		IDataset userMainProducts = UserProductInfoQry.queryUserMainProduct(userId);
		IDataset userOfferRels = BofQuery.queryUserAllOfferRelByUserId(userId, eparchyCode);
		userSvcElements = OfferUtil.fillStructAndFilter(userSvcElements, userOfferRels);
		userDiscntElements = OfferUtil.fillStructAndFilter(userDiscntElements, userOfferRels);
		// 当有下月生效的新主品时，需要对优惠元素串和服务元素串进行过滤

		// 判断用户是否有下月生效的主产品
		String userProductId = param.getString("USER_PRODUCT_ID", "");
		String nextProductId = "";
		String sysDate = SysDateMgr.getSysTime();
		if (IDataUtil.isNotEmpty(userMainProducts))
		{
			int size = userMainProducts.size();
			for (int i = 0; i < size; i++)
			{
				IData userProduct = userMainProducts.getData(i);
				if (userProduct.getString("START_DATE").compareTo(sysDate) > 0)
				{
					nextProductId = userProduct.getString("PRODUCT_ID");
				} else
				{
					userProductId = userProduct.getString("PRODUCT_ID");
				}
			}
		}
		String newProductId = param.getString("NEW_PRODUCT_ID", "");

		IDataset nowProductRelationElements = new DatasetList();
		IDataset nextProductRelationElements = new DatasetList();

		if (StringUtils.isBlank(nextProductId))
		{
			nowProductRelationElements = UProductInfoQry.queryAllProductElements(userProductId);
		} else
		{
			nextProductRelationElements = UProductInfoQry.queryAllProductElements(nextProductId);
		}
		IDataset allElements = OfferUtil.unionAll(nextProductRelationElements, nowProductRelationElements);

		IDataset selectedElements = new DatasetList();
		DataHelper.sort(userSvcElements, "INST_ID", IDataset.TYPE_STRING);
		DataHelper.sort(userDiscntElements, "INST_ID", IDataset.TYPE_STRING);
		OfferUtil.fillElements(userSvcElements, userDiscntElements, allElements);
		IDataset userAttrs = UserAttrInfoQry.queryUserAllAttrs(userId);
		// 因为查出来的元素是连带属性一起查的，因此一个元素如果有多个属性的话，会有多条记录，需要进行合并
		if (userSvcElements != null && userSvcElements.size() > 0)
		{
			int size = userSvcElements.size();
			for (int i = 0; i < size; i++)
			{
				IData svc = userSvcElements.getData(i);
				IDataset userElementAttrs = this.getUserAttrByRelaInstId(userAttrs, svc.getString("INST_ID"));
				IDataset attrResult = this.makeAttrs(userElementAttrs, svc.getDataset("ATTR_PARAM"));
				if (attrResult != null && attrResult.size() > 0)
				{
					svc.put("ATTR_PARAM", attrResult);
				}
			}
		}
		if (userDiscntElements != null && userDiscntElements.size() > 0)
		{
			int size = userDiscntElements.size();
			for (int i = 0; i < size; i++)
			{
				IData discnt = userDiscntElements.getData(i);
				// IDataset userElementAttrs =
				// this.getUserAttrByRelaInstId(userAttrs,
				// discnt.getString("INST_ID"));
				// IDataset attrResult = this.makeAttrs(userElementAttrs,
				// discnt.getDataset("ATTR_PARAM"));
				IDataset attrResult = this.dealDiscntInfo(discnt, userAttrs);
				if (attrResult != null && attrResult.size() > 0)
				{
					discnt.put("ATTR_PARAM", attrResult);
				}
			}
		}

		// // 查询用户现有产品下的附加产品集合
		// List<String> plusProductIds = new ArrayList<String>();
		// if (StringUtils.isNotBlank(nextProductId))
		// {
		// IDataset plusProducts =
		// ProductInfoQry.getPlusProducts(userProductId);
		// if (IDataUtil.isNotEmpty(plusProducts))
		// {
		// for (int i = 0; i < plusProducts.size(); i++)
		// {
		// plusProductIds.add(plusProducts.getData(i).getString("PRODUCT_ID_B"));
		// }
		// }
		// }
		if (userSvcElements.size() > 0)
		{
			selectedElements.addAll(userSvcElements);
		}
		if (userDiscntElements.size() > 0)
		{
			selectedElements.addAll(userDiscntElements);
		}
		if (!"".equals(newProductId) && !userProductId.equals(newProductId))
		{
			// 转换了主产品
			if (StringUtils.isNotBlank(nextProductId))
			{
				CSAppException.apperr(ProductException.CRM_PRODUCT_195);
			}
			IData productTran = new DataMap();

			String productChangeDate = "";
			String bookingDate = param.getString("BOOKING_DATE");
			if (StringUtils.isNotBlank(bookingDate) && ProductUtils.isBookingProductChange(bookingDate, userProductId, newProductId))
			{
				productChangeDate = bookingDate;
			} else
			{
				productChangeDate = this.getProductChangeDate(userProductId, newProductId, productTran);
			}

			IData newProductInfo = UProductInfoQry.qryProductByPK(newProductId);
			String newBrandCode = newProductInfo.getString("BRAND_CODE");
			String oldProductEndDate = SysDateMgr.getLastSecond(productChangeDate);
			result.put("NEW_PRODUCT_START_DATE", productChangeDate);
			result.put("OLD_PRODUCT_END_DATE", oldProductEndDate);

			IDataset newProductElements = UProductInfoQry.queryAllProductElements(newProductId);
			if (selectedElements != null && selectedElements.size() > 0)
			{
				int size = selectedElements.size();
				String defaultLong = "13";
				String defaultRoam = "16";
				String defaultLongName = "本地通话";
				String defaultRoamName = "本地漫游";
				IData longElement = this.getProductDefaultLong(newProductElements);
				if (IDataUtil.isNotEmpty(longElement))
				{
					defaultLong = longElement.getString("ELEMENT_ID");
					defaultLongName = USvcInfoQry.getSvcNameBySvcId(defaultLong);
				}
				IData roamElement = this.getProductDefaultRoam(newProductElements);
				if (IDataUtil.isNotEmpty(roamElement))
				{
					defaultRoam = roamElement.getString("ELEMENT_ID");
					defaultRoamName = USvcInfoQry.getSvcNameBySvcId(defaultRoam);
				}
				for (int i = 0; i < size; i++)
				{
					IData element = selectedElements.getData(i);
					IData trans = this.getTransElement(newProductElements, element.getString("ELEMENT_ID"), element.getString("ELEMENT_TYPE_CODE"));

					if (trans == null || trans.size() <= 0)
					{
						ProductTimeEnv env = new ProductTimeEnv();
						env.setBasicAbsoluteStartDate(productChangeDate);
						env.setBasicAbsoluteCancelDate(oldProductEndDate);
						// 不能转换，删除

						element.put("MODIFY_TAG", BofConst.MODIFY_TAG_DEL);
						element.put("EFFECT_NOW_START_DATE", element.getString("START_DATE"));
						element.put("EFFECT_NOW_END_DATE", SysDateMgr.getSysTime());
						element.put("DISABLED", "true");
						if (BofConst.ELEMENT_TYPE_CODE_SVC.equals(element.getString("ELEMENT_TYPE_CODE")))
						{
							element.put("END_DATE", oldProductEndDate);

							String elementId = element.getString("ELEMENT_ID");
							// 如果是长途或者国际漫游的服务，需要默认开一个

							if (("13".equals(elementId) || "14".equals(elementId) || "15".equals(elementId)) && !"30286002".equals(newProductId))
							{
								IData longTrans = this.getTransElement(newProductElements, defaultLong, "S");
								if (longTrans == null)
								{
									CSAppException.apperr(CrmUserException.CRM_USER_731);
								} else
								{
									if("110".equals(param.getString("TRADE_TYPE_CODE", "")) && "15".equals(defaultLong)) {
										continue;
									}
									IData longRange = new DataMap();
									longRange.put("ELEMENT_ID", defaultLong);
									longRange.put("ELEMENT_TYPE_CODE", "S");
									longRange.put("MODIFY_TAG", BofConst.MODIFY_TAG_ADD);
									longRange.put("PRODUCT_ID", newProductId);
									longRange.put("PACKAGE_ID", longTrans.getString("PACKAGE_ID"));
									longRange.put("ELEMENT_NAME", defaultLongName);
									longRange.put("MAIN_TAG", "0");
									longRange.put("START_DATE", productChangeDate);
									longRange.put("END_DATE", SysDateMgr.getTheLastTime());
									longRange.put("PACKAGE_FORCE_TAG", "0");
									longRange.put("ELEMENT_FORCE_TAG", "0");
									longRange.put("EFFECT_NOW_START_DATE", SysDateMgr.getSysTime());
									longRange.put("EFFECT_NOW_END_DATE", longRange.getString("END_DATE"));
									selectedElements.add(longRange);
								}
							} else if (("16".equals(elementId) || "17".equals(elementId) || "18".equals(elementId) || "19".equals(elementId) || "100".equals(elementId) || "101".equals(elementId) || "122".equals(elementId) || "134".equals(elementId)) && !"G005".equals(newBrandCode) && !"30286002".equals(newProductId))
							{
								IData roamTrans = this.getTransElement(newProductElements, defaultRoam, "S");
								if (roamTrans == null)
								{
									CSAppException.apperr(CrmUserException.CRM_USER_730);
								} else
								{
									if("110".equals(param.getString("TRADE_TYPE_CODE", "")) && "19".equals(defaultRoam)){
										continue;
									}
									IData roam = new DataMap();
									roam.put("ELEMENT_ID", defaultRoam);
									roam.put("ELEMENT_TYPE_CODE", "S");
									roam.put("MODIFY_TAG", BofConst.MODIFY_TAG_ADD);
									roam.put("PRODUCT_ID", newProductId);
									roam.put("PACKAGE_ID", roamTrans.getString("PACKAGE_ID"));
									roam.put("ELEMENT_NAME", defaultRoamName);
									roam.put("MAIN_TAG", "0");
									roam.put("START_DATE", productChangeDate);
									roam.put("END_DATE", SysDateMgr.getTheLastTime());
									roam.put("PACKAGE_FORCE_TAG", "0");
									roam.put("ELEMENT_FORCE_TAG", "0");
									roam.put("EFFECT_NOW_START_DATE", SysDateMgr.getSysTime());
									roam.put("EFFECT_NOW_END_DATE", roam.getString("END_DATE"));
									selectedElements.add(roam);
								}
							}
						} else if (BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(element.getString("ELEMENT_TYPE_CODE")))
						{
							DiscntData discnt = new DiscntData(element);
							element.put("END_DATE", ProductModuleCalDate.calCancelDate(discnt, env));
							element.put("EFFECT_NOW_START_DATE", element.getString("START_DATE"));
							element.put("EFFECT_NOW_END_DATE", SysDateMgr.getSysTime());
						}
					} else
					{
						element.put("NEW_PRODUCT_ID", trans.getString("PRODUCT_ID"));
						element.put("NEW_PACKAGE_ID", trans.getString("PACKAGE_ID"));
						if (BofConst.ELEMENT_TYPE_CODE_SVC.equals(element.getString("ELEMENT_TYPE_CODE")))
						{
							// 如果能继承，判断新产品默认开通的长途漫游服务是否级别更高，如果是，则删除原来的，订购新产品下的
							String elementId = element.getString("ELEMENT_ID");
							if (("13".equals(elementId) || "14".equals(elementId) || "15".equals(elementId)) && !"30286002".equals(newProductId) && elementId.compareTo(defaultLong) < 0)
							{
								IData longTrans = this.getTransElement(newProductElements, defaultLong, "S");
								if(!"110".equals(param.getString("TRADE_TYPE_CODE", "")) || !"15".equals(defaultLong)){
									element.put("MODIFY_TAG", BofConst.MODIFY_TAG_DEL);
									element.put("END_DATE", oldProductEndDate);
									element.put("EFFECT_NOW_START_DATE", element.getString("START_DATE"));
									element.put("EFFECT_NOW_END_DATE", SysDateMgr.getSysTime());
								}

								IData longRange = new DataMap();
								longRange.put("ELEMENT_ID", defaultLong);
								longRange.put("ELEMENT_TYPE_CODE", "S");
								longRange.put("MODIFY_TAG", BofConst.MODIFY_TAG_ADD);
								longRange.put("PRODUCT_ID", newProductId);
								longRange.put("PACKAGE_ID", longTrans.getString("PACKAGE_ID"));
								longRange.put("ELEMENT_NAME", defaultLongName);
								longRange.put("MAIN_TAG", "0");
								longRange.put("START_DATE", productChangeDate);
								longRange.put("END_DATE", SysDateMgr.getTheLastTime());
								longRange.put("PACKAGE_FORCE_TAG", "0");
								longRange.put("ELEMENT_FORCE_TAG", "0");
								longRange.put("EFFECT_NOW_START_DATE", SysDateMgr.getSysTime());
								longRange.put("EFFECT_NOW_END_DATE", longRange.getString("END_DATE"));

								if("110".equals(param.getString("TRADE_TYPE_CODE", "")) && "15".equals(defaultLong)) {
									continue;
								}

								selectedElements.add(longRange);

							} else if (("16".equals(elementId) || "17".equals(elementId) || "18".equals(elementId) || "19".equals(elementId) || "100".equals(elementId) || "101".equals(elementId) || "122".equals(elementId) || "134".equals(elementId)) && !"G005".equals(newBrandCode) && !"30286002".equals(newProductId) && elementId.compareTo(defaultRoam) < 0)
							{
								IData roamTrans = this.getTransElement(newProductElements, defaultRoam, "S");
								if(!"110".equals(param.getString("TRADE_TYPE_CODE", "")) || !"19".equals(defaultRoam)){
									element.put("MODIFY_TAG", BofConst.MODIFY_TAG_DEL);
									element.put("END_DATE", oldProductEndDate);
									element.put("EFFECT_NOW_START_DATE", element.getString("START_DATE"));
									element.put("EFFECT_NOW_END_DATE", SysDateMgr.getSysTime());
								}
								IData roam = new DataMap();
								roam.put("ELEMENT_ID", defaultRoam);
								roam.put("ELEMENT_TYPE_CODE", "S");
								roam.put("MODIFY_TAG", BofConst.MODIFY_TAG_ADD);
								roam.put("PRODUCT_ID", newProductId);
								roam.put("PACKAGE_ID", roamTrans.getString("PACKAGE_ID"));
								roam.put("ELEMENT_NAME", defaultRoamName);
								roam.put("MAIN_TAG", "0");
								roam.put("START_DATE", productChangeDate);
								roam.put("END_DATE", SysDateMgr.getTheLastTime());
								roam.put("PACKAGE_FORCE_TAG", "0");
								roam.put("ELEMENT_FORCE_TAG", "0");
								roam.put("EFFECT_NOW_START_DATE", SysDateMgr.getSysTime());
								roam.put("EFFECT_NOW_END_DATE", roam.getString("END_DATE"));

								if("110".equals(param.getString("TRADE_TYPE_CODE", "")) && "19".equals(defaultRoam)){
									continue;
								}

								selectedElements.add(roam);
							}
						}
					}
				}
			}
			IDataset forceElements = this.getProductForceElement(newProductElements);
			if (forceElements != null && forceElements.size() > 0)
			{
				int size = forceElements.size();
				for (int i = 0; i < size; i++)
				{
					boolean flag = false;
					IData element = forceElements.getData(i);
					for (int j = 0; j < selectedElements.size(); j++)
					{
						IData selectedElement = selectedElements.getData(j);
						if (element.getString("ELEMENT_ID").equals(selectedElement.getString("ELEMENT_ID")) && element.getString("ELEMENT_TYPE_CODE", "").equals(selectedElement.getString("ELEMENT_TYPE_CODE")))
						{
							// 如果已经存在，则元素包和元素必选和默认重新赋值
							selectedElement.put("PACKAGE_FORCE_TAG", element.getString("PACKAGE_FORCE_TAG"));
							selectedElement.put("ELEMENT_FORCE_TAG", element.getString("ELEMENT_FORCE_TAG"));
							selectedElement.put("PACKAGE_DEFAULT_TAG", element.getString("PACKAGE_DEFAULT_TAG"));
							selectedElement.put("ELEMENT_DEFAULT_TAG", element.getString("ELEMENT_DEFAULT_TAG"));
							flag = true;
							break;
						}
					}
					if (!flag)
					{
						IDataset attrItemAList = AttrItemInfoQry.getElementItemA(element.getString("ELEMENT_TYPE_CODE"), element.getString("ELEMENT_ID"), eparchyCode);
						IDataset attrs = this.makeAttrs(null, attrItemAList);
						if (attrs != null && attrs.size() > 0)
						{
							element.put("ATTR_PARAM", attrs);
						}
						element.remove("START_DATE");
						element.remove("END_DATE");
						ProductTimeEnv env = new ProductTimeEnv();
						env.setBasicAbsoluteStartDate(productChangeDate);
						ProductModuleData forceElement = null;
						if (BofConst.ELEMENT_TYPE_CODE_SVC.equals(element.getString("ELEMENT_TYPE_CODE")))
						{
							forceElement = new SvcData(element);
							element.put("EFFECT_NOW_START_DATE", SysDateMgr.getSysTime());
							element.put("EFFECT_NOW_END_DATE", SysDateMgr.endDate(element.getString("EFFECT_NOW_START_DATE"), forceElement.getEndEnableTag(), forceElement.getEndAbsoluteDate(), forceElement.getEndOffSet(), forceElement.getEndUnit()));
						} else if (BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(element.getString("ELEMENT_TYPE_CODE")))
						{
							forceElement = new DiscntData(element);
							if ("5".equals(forceElement.getEnableTag()))
							{
								element.put("EFFECT_NOW_START_DATE", SysDateMgr.getFirstDayOfNextMonth());
								element.put("EFFECT_NOW_END_DATE", SysDateMgr.endDate(element.getString("EFFECT_NOW_START_DATE"), forceElement.getEndEnableTag(), forceElement.getEndAbsoluteDate(), forceElement.getEndOffSet(), forceElement.getEndUnit()));
							} else
							{
								element.put("EFFECT_NOW_START_DATE", SysDateMgr.getSysTime());
								element.put("EFFECT_NOW_END_DATE", SysDateMgr.endDate(element.getString("EFFECT_NOW_START_DATE"), forceElement.getEndEnableTag(), forceElement.getEndAbsoluteDate(), forceElement.getEndOffSet(), forceElement.getEndUnit()));
							}
						}
						String startDate = ProductModuleCalDate.calStartDate(forceElement, env);
						element.put("START_DATE", startDate);
						String endDate = ProductModuleCalDate.calEndDate(forceElement, startDate);

						// 无手机宽带移机，宽带类型变化连带宽带产品变更，平行变更为新宽带类型下同速率的产品、服务、优惠，其中优惠结束时间需要保持不变
						if ("686".equals(param.getString("TRADE_TYPE_CODE", "")) && (BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(element.getString("ELEMENT_TYPE_CODE"))))
						{
							IDataset userDiscnts = UserDiscntInfoQry.getAllValidDiscntByUserId(userId);
							if (IDataUtil.isNotEmpty(userDiscnts))
							{
								IData userDiscnt = userDiscnts.getData(0);// 原本想采用关联配置，但还需要增加配置来维护。暂时不增加配置，直接读取用户有效优惠的第一条。无手机宽带目前只有包年优惠，无其他优惠。
								endDate = userDiscnt.getString("END_DATE");
							}
						}
						// end

						element.put("END_DATE", endDate);
						element.put("MODIFY_TAG", BofConst.MODIFY_TAG_ADD);
						selectedElements.add(element);
					}
				}
			}
			// 加载相关费用
			int size = selectedElements.size();

			// 无手机宽带要求算好金额
			String productId_old = "";
			String packageId_old = "";
			String elementId_old = "";
			String startDate_old = "";
			if ("681".equals(param.getString("TRADE_TYPE_CODE")))
			{
				for (int i = 0; i < size; i++)
				{
					IData element = selectedElements.getData(i);
					if ("1".equals(element.getString("MODIFY_TAG")) && "D".equals(element.getString("ELEMENT_TYPE_CODE")))
					{
						productId_old = element.getString("PRODUCT_ID");
						packageId_old = element.getString("PACKAGE_ID");
						elementId_old = element.getString("ELEMENT_ID");
						startDate_old = element.getString("START_DATE");
						break;
					}
				}
			}

			for (int i = 0; i < size; i++)
			{
				IData element = selectedElements.getData(i);
				if ("0".equals(element.getString("MODIFY_TAG")))
				{
					IDataset feeDatas = new DatasetList();

					// 海南国际长途、漫游费用特殊处理
					String tradeTypeCode = param.getString("TRADE_TYPE_CODE");
					String elementId = element.getString("ELEMENT_ID");
					String elementTypeCode = element.getString("ELEMENT_TYPE_CODE");
					IDataset userElements = selectedElements;
					// REQ202004240028_关于国漫作为基础服务的开发需求 去除国漫押金 by wuhao5 20200509
//					if ("110".equals(tradeTypeCode))
//					{
//						if (!isDealFeeByServiceId15(userId, elementId, elementTypeCode, userElements))
//						{
//							continue;
//						}
//
//						IData feeSvc19 = dealFeeByServiceId19(userId, elementId, elementTypeCode, userElements);
//						if (IDataUtil.isNotEmpty(feeSvc19))
//						{
//							String fee19 = feeSvc19.getString("FEE");
//							if ("true".equals(feeSvc19.getString("DEAL_FEE")) && !"0".equals(fee19))
//							{
//								IData feeData = new DataMap();
//								feeData.put("TRADE_TYPE_CODE", tradeTypeCode);
//								feeData.put("MODE", "1");
//								feeData.put("CODE", "3");
//								feeData.put("FEE", fee19);
//								feeDatas.add(feeData);
//
//								element.put("FEE_DATA", feeDatas);
//								continue;
//							}
//						}
//					}
					// 海南国际长途、漫游费用特殊处理 END

					// IDataset feeConfigs = new
					// DatasetList();ProductFeeInfoQry.getElementFee(param.getString("TRADE_TYPE_CODE"),
					// CSBizBean.getVisit().getInModeCode(), "",
					// element.getString("ELEMENT_TYPE_CODE"),
					// element.getString("PRODUCT_ID"),
					// element.getString("PACKAGE_ID"), "-1",
					// element.getString("ELEMENT_ID"), eparchyCode, "3");

					IDataset feeConfigs = ProductFeeInfoQry.getElementFeeList(param.getString("TRADE_TYPE_CODE"), BofConst.ELEMENT_TYPE_CODE_PRODUCT, element.getString("PRODUCT_ID"), element.getString("ELEMENT_TYPE_CODE"), element.getString("ELEMENT_ID"), element.getString("PACKAGE_ID"));

					if (IDataUtil.isEmpty(feeConfigs))
					{
						continue;
					}
					int feeSize = feeConfigs.size();

					for (int j = 0; j < feeSize; j++)
					{
						IData feeConfig = feeConfigs.getData(j);
						if (!"0".equals(feeConfig.getString("PAY_MODE")))
						{
							continue;
						}
						IData feeData = new DataMap();
						feeData.put("TRADE_TYPE_CODE", param.getString("TRADE_TYPE_CODE"));
						feeData.put("MODE", feeConfig.getString("FEE_MODE"));
						feeData.put("CODE", feeConfig.getString("FEE_TYPE_CODE"));
						feeData.put("FEE", feeConfig.getString("FEE"));
						feeDatas.add(feeData);

						// 无手机宽带要求算好金额
						if ("681".equals(param.getString("TRADE_TYPE_CODE")))
						{
							// 新宽带包年费用-（原宽带包年费用
							// -截取到角（原宽带包年费用/12）*新宽带包年产品生效之日前已经出账的月份）
							// 只存在升档重算，降档不管。
							productId_old = productId_old + "";
							packageId_old = packageId_old + "";
							elementId_old = elementId_old + "";

							String fee_new = feeConfig.getString("FEE");
							String fee_old = "0";// 取旧产品的费用

							if (StringUtils.isNotEmpty(productId_old) && StringUtils.isNotEmpty(elementId_old))
							{
								IDataset fee_old_list = ProductFeeInfoQry.getElementFeeList("681", BofConst.ELEMENT_TYPE_CODE_PRODUCT, productId_old, BofConst.ELEMENT_TYPE_CODE_DISCNT, elementId_old, packageId_old);
								if (fee_old_list != null && fee_old_list.size() > 0)
								{
									fee_old = fee_old_list.getData(0).getString("FEE", "");
									// 升档才处理
									if (Integer.parseInt(fee_new) != Integer.parseInt(fee_old))
									{
										String today = element.getString("START_DATE");
										SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
										Calendar c1 = Calendar.getInstance();
										Calendar c2 = Calendar.getInstance();
										c1.setTime(sdf.parse(today));
										c2.setTime(sdf.parse(startDate_old));
										int year1 = c1.get(Calendar.YEAR);
										int month1 = c1.get(Calendar.MONTH);
										int year2 = c2.get(Calendar.YEAR);
										int month2 = c2.get(Calendar.MONTH);
										int useMon = 0;
										if (year1 == year2)
										{
											useMon = month1 - month2;
										} else
										{
											useMon = (year1 - year2) * 12 + (month1 - month2);
										}
										// 月份大于0才计算
										if (useMon >= 0)
										{
											int fee_final = Integer.parseInt(fee_new) - (Integer.parseInt(fee_old) - (Integer.parseInt(fee_old) / 12) * useMon);
											feeData.put("FEE", fee_final);
											// log.info("("**********cxy******fee_final="+fee_final+"*****feeData="+feeData);
										}
									}
								}
							}
						}

					}
					element.put("FEE_DATA", feeDatas);
				}
			}
			//REQ201705220010
			this.filterUnForceElements(userProductId, selectedElements, newProductElements);
		}

		// 海南需求START_DATE
		this.setDisabledDiscntData(selectedElements);
		// 海南需求END_DATE

		DataHelper.sort(selectedElements, "ELEMENT_TYPE_CODE", IDataset.TYPE_STRING, IDataset.ORDER_ASCEND, "MODIFY_TAG", IDataset.TYPE_STRING, IDataset.ORDER_ASCEND);
		result.put("SELECTED_ELEMENTS", selectedElements);
		resultList.add(result);
		return resultList;
	}
	
	/**
	 * 改造：前台操作用户变更主产品，用新的产品下的元素配置更新前台的操作选择，
	 * 例如：原主套餐来电显示是必选，新的主套餐是可选，在前台界面选择新的主套餐后来电显示要变成可选项
	 * @throws Exception
	 */
	public void filterUnForceElements(String userProductId,IDataset selectedElements,IDataset newProductElements)throws Exception
	{
		IDataset filterParams = CommparaInfoQry.getCommpara("CSM", "9183",userProductId, "0898");
		if(!IDataUtil.isEmpty(filterParams) && !IDataUtil.isEmpty(newProductElements))
		{
			IData param = filterParams.getData(0);
			String filterElements = param.getString("PARA_CODE1")+param.getString("PARA_CODE23","");
			if(StringUtils.isNotEmpty(filterElements))
			{
				IDataset forceElements = this.getProductForceElement(newProductElements);
				
				if(selectedElements != null && selectedElements.size() > 0)
				{
					for(int j=0;j<selectedElements.size();j++)
					{
						IData selectElement = selectedElements.getData(j);
						String elementId = selectElement.getString("ELEMENT_ID");
						if(StringUtils.indexOf(filterElements, "|"+elementId+"|")!= -1)
						{
							if (forceElements != null && forceElements.size() > 0)
							{
								int size = forceElements.size();
								boolean existTag = false;
								for (int i = 0; i < size; i++)
								{
									IData foreElement = forceElements.getData(i);
									if(StringUtils.equals(elementId, foreElement.getString("ELEMENT_ID")))
									{
										existTag =true;
										break;
									}
								}
								
								if(!existTag)
								{
									selectElement.put("ELEMENT_FORCE_TAG", "0");
									selectElement.put("DISABLED", "false");
								}
							}
						}
					}
				}
			}
		}
	}

	/**
	 * 携号申请产品组件使用
	 * 
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public IDataset getUserNetNpElements(IData param) throws Exception
	{
		IData result = new DataMap();
		IDataset resultList = new DatasetList();
		String userId = param.getString("USER_ID");
		String inEparchyCode = param.getString("IN_EPARCHY_CODE");
		String outEparchyCode = param.getString("OUT_EPARCHY_CODE");
		String newProductId = param.getString("NEW_PRODUCT_ID");
		String execTime = param.getString("EXEC_TIME");// 生效时间
		String execTimeValue = "";// 生效时间
		if ("0".equals(execTime))// 下月一号生效
		{
			execTimeValue = SysDateMgr.getFirstDayOfNextMonth();
		} else if ("1".equals(execTime))// 下下月一号生效
		{
			execTimeValue = SysDateMgr.getDateNextMonthFirstDay(SysDateMgr.getFirstDayOfNextMonth());
		}

		if (StringUtils.isBlank(userId))
		{
			return null;
		}

		IDataset userSvcElements = getUserAllSvc(userId, outEparchyCode);
		IDataset userDiscntElements = getUserAllDiscnt(userId, outEparchyCode);
		IDataset selectedElements = new DatasetList();
		DataHelper.sort(userSvcElements, "INST_ID", IDataset.TYPE_STRING);
		DataHelper.sort(userDiscntElements, "INST_ID", IDataset.TYPE_STRING);
		IDataset userAttrs = UserAttrInfoQry.queryUserAllAttrs(userId, outEparchyCode);
		// 因为查出来的元素是连带属性一起查的，因此一个元素如果有多个属性的话，会有多条记录，需要进行合并

		IDataset svcResult = new DatasetList();
		if (userSvcElements != null && userSvcElements.size() > 0)
		{
			String instId = "";
			int size = userSvcElements.size();
			IData tempSvc = null;
			for (int i = 0; i < size; i++)
			{
				IData userSvc = userSvcElements.getData(i);
				if (!instId.equals(userSvc.getString("INST_ID")))
				{
					if (tempSvc != null)
					{
						svcResult.add(tempSvc);
						tempSvc = null;
					}
					if ("".equals(userSvc.getString("ATTR_CODE", "")))
					{
						userSvc.remove("ATTR_CODE");
						svcResult.add(userSvc);
					} else
					{
						tempSvc = userSvc;
						IDataset attrTempList = new DatasetList();
						IData attrTemp = new DataMap();
						attrTemp.put("ATTR_CODE", userSvc.getString("ATTR_CODE"));
						attrTemp.put("ATTR_VALUE", "");
						attrTempList.add(attrTemp);
						tempSvc.remove("ATTR_CODE");
						tempSvc.put("ATTR_PARAM", attrTempList);
						if (i == size - 1)
						{
							svcResult.add(tempSvc);
						}
					}
				} else
				{
					IData attrTemp = new DataMap();
					attrTemp.put("ATTR_CODE", userSvc.getString("ATTR_CODE"));
					attrTemp.put("ATTR_VALUE", "");
					tempSvc.getDataset("ATTR_PARAM").add(attrTemp);
					if (i == size - 1)
					{
						svcResult.add(tempSvc);
					}
				}
				instId = userSvc.getString("INST_ID");
			}
			size = svcResult.size();
			for (int i = 0; i < size; i++)
			{
				IData svc = svcResult.getData(i);
				IDataset userElementAttrs = this.getUserAttrByRelaInstId(userAttrs, svc.getString("INST_ID"));
				IDataset attrResult = this.makeAttrs(userElementAttrs, svc.getDataset("ATTR_PARAM"));
				if (attrResult != null && attrResult.size() > 0)
				{
					svc.put("ATTR_PARAM", attrResult);
				}
			}
		}
		IDataset discntResult = new DatasetList();
		if (userDiscntElements != null && userDiscntElements.size() > 0)
		{
			String instId = "";
			int size = userDiscntElements.size();
			IData tempDiscnt = null;
			for (int i = 0; i < size; i++)
			{
				IData userDiscnt = userDiscntElements.getData(i);
				if (!instId.equals(userDiscnt.getString("INST_ID")))
				{
					if (tempDiscnt != null)
					{
						discntResult.add(tempDiscnt);
						tempDiscnt = null;
					}
					if ("".equals(userDiscnt.getString("ATTR_CODE", "")))
					{
						userDiscnt.remove("ATTR_CODE");
						discntResult.add(userDiscnt);
					} else
					{
						tempDiscnt = userDiscnt;
						IDataset attrTempList = new DatasetList();
						IData attrTemp = new DataMap();
						attrTemp.put("ATTR_CODE", userDiscnt.getString("ATTR_CODE"));
						attrTemp.put("ATTR_VALUE", "");
						attrTempList.add(attrTemp);
						tempDiscnt.remove("ATTR_CODE");
						tempDiscnt.put("ATTR_PARAM", attrTempList);
						if (i == size - 1)
						{
							discntResult.add(tempDiscnt);
						}
					}
				} else
				{
					IData attrTemp = new DataMap();
					attrTemp.put("ATTR_CODE", userDiscnt.getString("ATTR_CODE"));
					attrTemp.put("ATTR_VALUE", "");
					tempDiscnt.getDataset("ATTR_PARAM").add(attrTemp);
					if (i == size - 1)
					{
						discntResult.add(tempDiscnt);
					}
				}
				instId = userDiscnt.getString("INST_ID");
			}
			size = discntResult.size();
			for (int i = 0; i < size; i++)
			{
				IData discnt = discntResult.getData(i);
				IDataset userElementAttrs = this.getUserAttrByRelaInstId(userAttrs, discnt.getString("INST_ID"));
				IDataset attrResult = this.makeAttrs(userElementAttrs, discnt.getDataset("ATTR_PARAM"));
				if (attrResult != null && attrResult.size() > 0)
				{
					discnt.put("ATTR_PARAM", attrResult);
				}
			}
		}

		String userProductId = param.getString("USER_PRODUCT_ID", "");

		if (svcResult.size() > 0)
		{
			selectedElements.addAll(svcResult);
		}
		if (discntResult.size() > 0)
		{
			selectedElements.addAll(discntResult);
		}
		if (!"".equals(newProductId) && !userProductId.equals(newProductId))
		{
			String productChangeDate = execTimeValue;
			IData newProductInfo = UProductInfoQry.qryProductByPK(newProductId);
			String newBrandCode = newProductInfo.getString("BRAND_CODE");
			String oldProductEndDate = SysDateMgr.addDays(productChangeDate, -1) + SysDateMgr.getEndTime235959();
			result.put("NEW_PRODUCT_START_DATE", productChangeDate);
			result.put("OLD_PRODUCT_END_DATE", oldProductEndDate);

			IDataset newProductElements = ProductInfoQry.getProductElements(newProductId, inEparchyCode);
			if (selectedElements != null && selectedElements.size() > 0)
			{
				int size = selectedElements.size();
				String defaultLong = "13";
				String defaultRoam = "16";
				String defaultLongName = "本地通话";
				String defaultRoamName = "本地漫游";
				IData longElement = this.getProductDefaultLong(newProductElements);
				if (IDataUtil.isNotEmpty(longElement))
				{
					defaultLong = longElement.getString("ELEMENT_ID");
					defaultLongName = USvcInfoQry.getSvcNameBySvcId(defaultLong);
				}
				IData roamElement = this.getProductDefaultRoam(newProductElements);
				if (IDataUtil.isNotEmpty(roamElement))
				{
					defaultRoam = roamElement.getString("ELEMENT_ID");
					defaultRoamName = USvcInfoQry.getSvcNameBySvcId(defaultRoam);
				}
				for (int i = 0; i < size; i++)
				{
					IData element = selectedElements.getData(i);
					// 携号优惠继承逻辑
					IData trans = this.getTransElementforNetNp(newProductElements, element, inEparchyCode);

					if (IDataUtil.isEmpty(trans))
					{
						// 不能转换，删除

						element.put("MODIFY_TAG", BofConst.MODIFY_TAG_DEL);
						element.put("EFFECT_NOW_START_DATE", element.getString("START_DATE"));
						element.put("EFFECT_NOW_END_DATE", SysDateMgr.getSysTime());
						element.put("DISABLED", "true");
						if (BofConst.ELEMENT_TYPE_CODE_SVC.equals(element.getString("ELEMENT_TYPE_CODE")))
						{
							element.put("END_DATE", oldProductEndDate);

							String elementId = element.getString("ELEMENT_ID");
							// 如果是长途或者国际漫游的服务，需要默认开一个

							if (("13".equals(elementId) || "14".equals(elementId) || "15".equals(elementId)) && !"30286002".equals(newProductId))
							{
								IData longTrans = this.getTransElement(newProductElements, defaultLong, "S");
								if (longTrans == null)
								{
									CSAppException.apperr(CrmUserException.CRM_USER_731);
								} else
								{
									IData longRange = new DataMap();
									longRange.put("ELEMENT_ID", defaultLong);
									longRange.put("ELEMENT_TYPE_CODE", "S");
									longRange.put("MODIFY_TAG", BofConst.MODIFY_TAG_ADD);
									longRange.put("PRODUCT_ID", newProductId);
									longRange.put("PACKAGE_ID", longTrans.getString("PACKAGE_ID"));
									longRange.put("ELEMENT_NAME", defaultLongName);
									longRange.put("MAIN_TAG", "0");
									longRange.put("START_DATE", productChangeDate);
									longRange.put("END_DATE", SysDateMgr.getTheLastTime());
									longRange.put("PACKAGE_FORCE_TAG", "0");
									longRange.put("ELEMENT_FORCE_TAG", "0");
									longRange.put("EFFECT_NOW_START_DATE", SysDateMgr.getSysTime());
									longRange.put("EFFECT_NOW_END_DATE", longRange.getString("END_DATE"));
									selectedElements.add(longRange);
								}
							} else if (("16".equals(elementId) || "17".equals(elementId) || "18".equals(elementId) || "19".equals(elementId) || "100".equals(elementId) || "101".equals(elementId) || "122".equals(elementId) || "134".equals(elementId)) && !"G005".equals(newBrandCode) && !"30286002".equals(newProductId))
							{
								IData roamTrans = this.getTransElement(newProductElements, defaultRoam, "S");
								if (roamTrans == null)
								{
									CSAppException.apperr(CrmUserException.CRM_USER_730);
								} else
								{
									IData roam = new DataMap();
									roam.put("ELEMENT_ID", defaultRoam);
									roam.put("ELEMENT_TYPE_CODE", "S");
									roam.put("MODIFY_TAG", BofConst.MODIFY_TAG_ADD);
									roam.put("PRODUCT_ID", newProductId);
									roam.put("PACKAGE_ID", roamTrans.getString("PACKAGE_ID"));
									roam.put("ELEMENT_NAME", defaultRoamName);
									roam.put("MAIN_TAG", "0");
									roam.put("START_DATE", productChangeDate);
									roam.put("END_DATE", SysDateMgr.getTheLastTime());
									roam.put("PACKAGE_FORCE_TAG", "0");
									roam.put("ELEMENT_FORCE_TAG", "0");
									roam.put("EFFECT_NOW_START_DATE", SysDateMgr.getSysTime());
									roam.put("EFFECT_NOW_END_DATE", roam.getString("END_DATE"));
									selectedElements.add(roam);
								}
							}
						} else if (BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(element.getString("ELEMENT_TYPE_CODE")))
						{
							element.put("END_DATE", oldProductEndDate);
							element.put("EFFECT_NOW_START_DATE", element.getString("START_DATE"));
							element.put("EFFECT_NOW_END_DATE", SysDateMgr.getSysTime());
						}
					} else
					{
						element.put("NEW_PRODUCT_ID", trans.getString("PRODUCT_ID"));
						element.put("NEW_PACKAGE_ID", trans.getString("PACKAGE_ID"));
						if (BofConst.ELEMENT_TYPE_CODE_SVC.equals(element.getString("ELEMENT_TYPE_CODE")))
						{
							// 如果能继承，判断新产品默认开通的长途漫游服务是否级别更高，如果是，则删除原来的，订购新产品下的
							String elementId = element.getString("ELEMENT_ID");
							if (("13".equals(elementId) || "14".equals(elementId) || "15".equals(elementId)) && !"30286002".equals(newProductId) && elementId.compareTo(defaultLong) < 0)
							{
								IData longTrans = this.getTransElement(newProductElements, defaultLong, "S");
								element.put("MODIFY_TAG", BofConst.MODIFY_TAG_DEL);
								element.put("END_DATE", oldProductEndDate);
								IData longRange = new DataMap();
								longRange.put("ELEMENT_ID", defaultLong);
								longRange.put("ELEMENT_TYPE_CODE", "S");
								longRange.put("MODIFY_TAG", BofConst.MODIFY_TAG_ADD);
								longRange.put("PRODUCT_ID", newProductId);
								longRange.put("PACKAGE_ID", longTrans.getString("PACKAGE_ID"));
								longRange.put("ELEMENT_NAME", defaultLongName);
								longRange.put("MAIN_TAG", "0");
								longRange.put("START_DATE", productChangeDate);
								longRange.put("END_DATE", SysDateMgr.getTheLastTime());
								longRange.put("PACKAGE_FORCE_TAG", "0");
								longRange.put("ELEMENT_FORCE_TAG", "0");
								longRange.put("EFFECT_NOW_START_DATE", SysDateMgr.getSysTime());
								longRange.put("EFFECT_NOW_END_DATE", longRange.getString("END_DATE"));
								selectedElements.add(longRange);

							} else if (("16".equals(elementId) || "17".equals(elementId) || "18".equals(elementId) || "19".equals(elementId) || "100".equals(elementId) || "101".equals(elementId) || "122".equals(elementId) || "134".equals(elementId)) && !"G005".equals(newBrandCode) && !"30286002".equals(newProductId) && elementId.compareTo(defaultRoam) < 0)
							{
								IData roamTrans = this.getTransElement(newProductElements, defaultRoam, "S");
								element.put("MODIFY_TAG", BofConst.MODIFY_TAG_DEL);
								element.put("END_DATE", oldProductEndDate);
								IData roam = new DataMap();
								roam.put("ELEMENT_ID", defaultRoam);
								roam.put("ELEMENT_TYPE_CODE", "S");
								roam.put("MODIFY_TAG", BofConst.MODIFY_TAG_ADD);
								roam.put("PRODUCT_ID", newProductId);
								roam.put("PACKAGE_ID", roamTrans.getString("PACKAGE_ID"));
								roam.put("ELEMENT_NAME", defaultRoamName);
								roam.put("MAIN_TAG", "0");
								roam.put("START_DATE", productChangeDate);
								roam.put("END_DATE", SysDateMgr.getTheLastTime());
								roam.put("PACKAGE_FORCE_TAG", "0");
								roam.put("ELEMENT_FORCE_TAG", "0");
								roam.put("EFFECT_NOW_START_DATE", SysDateMgr.getSysTime());
								roam.put("EFFECT_NOW_END_DATE", roam.getString("END_DATE"));
								selectedElements.add(roam);
							}
						}
					}
				}
			}
			IDataset forceElements = this.getProductForceElement(newProductElements);
			if (forceElements != null && forceElements.size() > 0)
			{
				int size = forceElements.size();
				for (int i = 0; i < size; i++)
				{
					boolean flag = false;
					IData element = forceElements.getData(i);
					for (int j = 0; j < selectedElements.size(); j++)
					{
						IData selectedElement = selectedElements.getData(j);
						if (element.getString("ELEMENT_ID").equals(selectedElement.getString("ELEMENT_ID")) && element.getString("ELEMENT_TYPE_CODE", "").equals(selectedElement.getString("ELEMENT_TYPE_CODE")))
						{
							element.put("PACKAGE_FORCE_TAG", "1");
							element.put("ELEMENT_FORCE_TAG", "1");
							flag = true;
							break;
						}
					}
					if (!flag)
					{
						IDataset attrItemAList = AttrItemInfoQry.getElementItemA(element.getString("ELEMENT_TYPE_CODE"), element.getString("ELEMENT_ID"), inEparchyCode);
						IDataset attrs = this.makeAttrs(null, attrItemAList);
						if (attrs != null && attrs.size() > 0)
						{
							element.put("ATTR_PARAM", attrs);
						}
						element.remove("START_DATE");
						element.remove("END_DATE");
						ProductTimeEnv env = new ProductTimeEnv();
						env.setBasicAbsoluteStartDate(productChangeDate);
						ProductModuleData forceElement = null;
						if (BofConst.ELEMENT_TYPE_CODE_SVC.equals(element.getString("ELEMENT_TYPE_CODE")))
						{
							forceElement = new SvcData(element);
							element.put("EFFECT_NOW_START_DATE", SysDateMgr.getSysTime());
							element.put("EFFECT_NOW_END_DATE", SysDateMgr.endDate(element.getString("EFFECT_NOW_START_DATE"), forceElement.getEndEnableTag(), forceElement.getEndAbsoluteDate(), forceElement.getEndOffSet(), forceElement.getEndUnit()));
						} else if (BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(element.getString("ELEMENT_TYPE_CODE")))
						{
							forceElement = new DiscntData(element);
							if ("5".equals(forceElement.getEnableTag()))
							{
								element.put("EFFECT_NOW_START_DATE", SysDateMgr.getFirstDayOfNextMonth());
								element.put("EFFECT_NOW_END_DATE", SysDateMgr.endDate(element.getString("EFFECT_NOW_START_DATE"), forceElement.getEndEnableTag(), forceElement.getEndAbsoluteDate(), forceElement.getEndOffSet(), forceElement.getEndUnit()));
							} else
							{
								element.put("EFFECT_NOW_START_DATE", SysDateMgr.getSysTime());
								element.put("EFFECT_NOW_END_DATE", SysDateMgr.endDate(element.getString("EFFECT_NOW_START_DATE"), forceElement.getEndEnableTag(), forceElement.getEndAbsoluteDate(), forceElement.getEndOffSet(), forceElement.getEndUnit()));
							}
						}
						String startDate = ProductModuleCalDate.calStartDate(forceElement, env);
						element.put("START_DATE", productChangeDate);
						String endDate = ProductModuleCalDate.calEndDate(forceElement, startDate);
						element.put("END_DATE", endDate);
						element.put("MODIFY_TAG", BofConst.MODIFY_TAG_ADD);
						if (BofConst.ELEMENT_TYPE_CODE_SVC.equals(element.getString("ELEMENT_TYPE_CODE")))
						{
							element.put("ELEMENT_NAME", USvcInfoQry.getSvcNameBySvcId(element.getString("ELEMENT_ID")));
						} else if (BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(element.getString("ELEMENT_TYPE_CODE")))
						{
							element.put("ELEMENT_NAME", UDiscntInfoQry.getDiscntNameByDiscntCode(element.getString("ELEMENT_ID")));
						}

						selectedElements.add(element);
					}
				}
			}
		}
		DataHelper.sort(selectedElements, "ELEMENT_TYPE_CODE", IDataset.TYPE_STRING, IDataset.ORDER_ASCEND, "MODIFY_TAG", IDataset.TYPE_STRING, IDataset.ORDER_ASCEND);
		result.put("SELECTED_ELEMENTS", selectedElements);
		resultList.add(result);
		return resultList;
	}

	public IDataset getUserOpenElements(IData param) throws Exception
	{
		String eparchyCode = param.getString("EPARCHY_CODE");
		this.setRouteId(eparchyCode);
		String newProductId = param.getString("NEW_PRODUCT_ID", "");
		IDataset selectedElements = new DatasetList();
		IData result = new DataMap();
		IDataset resultList = new DatasetList();
		if ("1".equals(param.getString("B_REOPEN_TAG")))
		{
			String userId = param.getString("USER_ID");
			if (userId == null || "".equals(userId))
			{
				return null;
			}

			IDataset userSvcElements = UserSvcInfoQry.queryUserSvcsInSelectedElements(userId);
			IDataset userDiscntElements = UserDiscntInfoQry.queryUserDiscntsInSelectedElements(userId);
			IDataset userOfferRels = BofQuery.queryUserAllOfferRelByUserId(userId, eparchyCode);
			userSvcElements = OfferUtil.fillStructAndFilter(userSvcElements, userOfferRels);
			userDiscntElements = OfferUtil.fillStructAndFilter(userDiscntElements, userOfferRels);

			DataHelper.sort(userSvcElements, "INST_ID", IDataset.TYPE_STRING);
			DataHelper.sort(userDiscntElements, "INST_ID", IDataset.TYPE_STRING);
			IDataset userAttrs = UserAttrInfoQry.queryUserAllAttrs(userId);
			// 因为查出来的元素是连带属性一起查的，因此一个元素如果有多个属性的话，会有多条记录，需要进行合并

			IDataset svcResult = new DatasetList();
			if (userSvcElements != null && userSvcElements.size() > 0)
			{
				String instId = "";
				int size = userSvcElements.size();
				IData tempSvc = null;
				for (int i = 0; i < size; i++)
				{
					IData userSvc = userSvcElements.getData(i);
					if (!instId.equals(userSvc.getString("INST_ID")))
					{
						if (tempSvc != null)
						{
							svcResult.add(tempSvc);
							tempSvc = null;
						}
						if ("".equals(userSvc.getString("ATTR_CODE", "")))
						{
							userSvc.remove("ATTR_CODE");
							svcResult.add(userSvc);
						} else
						{
							tempSvc = userSvc;
							IDataset attrTempList = new DatasetList();
							IData attrTemp = new DataMap();
							attrTemp.put("ATTR_CODE", userSvc.getString("ATTR_CODE"));
							attrTemp.put("ATTR_VALUE", "");
							attrTempList.add(attrTemp);
							tempSvc.remove("ATTR_CODE");
							tempSvc.put("ATTR_PARAM", attrTempList);
							if (i == size - 1)
							{
								svcResult.add(tempSvc);
							}
						}
					} else
					{
						IData attrTemp = new DataMap();
						attrTemp.put("ATTR_CODE", userSvc.getString("ATTR_CODE"));
						attrTemp.put("ATTR_VALUE", "");
						tempSvc.getDataset("ATTR_PARAM").add(attrTemp);
						if (i == size - 1)
						{
							svcResult.add(tempSvc);
						}
					}
					instId = userSvc.getString("INST_ID");
				}
				size = svcResult.size();
				for (int i = 0; i < size; i++)
				{
					IData svc = svcResult.getData(i);
					IDataset userElementAttrs = this.getUserAttrByRelaInstId(userAttrs, svc.getString("INST_ID"));
					IDataset attrResult = this.makeAttrs(userElementAttrs, svc.getDataset("ATTR_PARAM"));
					if (attrResult != null && attrResult.size() > 0)
					{
						svc.put("ATTR_PARAM", attrResult);
					}
				}
			}
			IDataset discntResult = new DatasetList();
			if (userDiscntElements != null && userDiscntElements.size() > 0)
			{
				String instId = "";
				int size = userDiscntElements.size();
				IData tempDiscnt = null;
				for (int i = 0; i < size; i++)
				{
					IData userDiscnt = userDiscntElements.getData(i);
					if (!instId.equals(userDiscnt.getString("INST_ID")))
					{
						if (tempDiscnt != null)
						{
							discntResult.add(tempDiscnt);
							tempDiscnt = null;
						}
						if ("".equals(userDiscnt.getString("ATTR_CODE", "")))
						{
							userDiscnt.remove("ATTR_CODE");
							discntResult.add(userDiscnt);
						} else
						{
							tempDiscnt = userDiscnt;
							IDataset attrTempList = new DatasetList();
							IData attrTemp = new DataMap();
							attrTemp.put("ATTR_CODE", userDiscnt.getString("ATTR_CODE"));
							attrTemp.put("ATTR_VALUE", "");
							attrTempList.add(attrTemp);
							tempDiscnt.remove("ATTR_CODE");
							tempDiscnt.put("ATTR_PARAM", attrTempList);
							if (i == size - 1)
							{
								discntResult.add(tempDiscnt);
							}
						}
					} else
					{
						IData attrTemp = new DataMap();
						attrTemp.put("ATTR_CODE", userDiscnt.getString("ATTR_CODE"));
						attrTemp.put("ATTR_VALUE", "");
						tempDiscnt.getDataset("ATTR_PARAM").add(attrTemp);
						if (i == size - 1)
						{
							discntResult.add(tempDiscnt);
						}
					}
					instId = userDiscnt.getString("INST_ID");
				}
				size = discntResult.size();
				for (int i = 0; i < size; i++)
				{
					IData discnt = discntResult.getData(i);
					IDataset userElementAttrs = this.getUserAttrByRelaInstId(userAttrs, discnt.getString("INST_ID"));
					IDataset attrResult = this.makeAttrs(userElementAttrs, discnt.getDataset("ATTR_PARAM"));
					if (attrResult != null && attrResult.size() > 0)
					{
						discnt.put("ATTR_PARAM", attrResult);
					}
				}
			}

			if (svcResult.size() > 0)
			{

				selectedElements.addAll(svcResult);
			}
			if (discntResult.size() > 0)
			{

				selectedElements.addAll(discntResult);
			}
		} else
		{
			ProductTimeEnv env = new ProductTimeEnv();
			String startDate = SysDateMgr.getSysTime();
			env.setBasicAbsoluteStartDate(startDate);
			result.put("NEW_PRODUCT_START_DATE", startDate);
			IData productInfos = UProductInfoQry.qryProductByPK(newProductId);
			// IDataset newProductElements =
			// ProductInfoQry.getProductElements(newProductId, eparchyCode);
			IDataset newProductElements = ProductUtils.offerToElement(UpcCall.qryAllOffersByOfferIdWithForceTagDefaultTagFilter(BofConst.ELEMENT_TYPE_CODE_PRODUCT, newProductId, null, null), newProductId);
			// REQ201512070012 关于在开户流程中直接开通安心包业务的需求@author yanwu
			// 开户界面、批量预开户界面，安心包（优惠编码6633）业务默认勾选（开户过程中也可以手工取消勾选）
			/*
			 * boolean bTag = false; IData e = new DataMap();
			 */
			String strTradeTypeCode = param.getString("TRADE_TYPE_CODE", "-1");
			String strBatchOpreType = param.getString("BATCH_OPER_TYPE", "");

			if (("10".equals(strTradeTypeCode) || "40".equals(strTradeTypeCode)) && StringUtils.isBlank(strBatchOpreType))
			{

				if (IDataUtil.isNotEmpty(newProductElements))
				{

					for (int i = 0; i < newProductElements.size(); i++)
					{

						IData ProductElement = newProductElements.getData(i);
						String strElement = ProductElement.getString("ELEMENT_ID", "");
						if ("6633".equals(strElement))
						{
							if (("1".equals(ProductElement.getString("PACKAGE_DEFAULT_TAG")) || "1".equals(ProductElement.getString("PACKAGE_FORCE_TAG"))) && ("1".equals(ProductElement.getString("ELEMENT_DEFAULT_TAG")) || "1".equals(ProductElement.getString("ELEMENT_FORCE_TAG"))))
							{
								// bTag = false;
							} else
							{
								// bTag = true;
								ProductElement.put("PACKAGE_FORCE_TAG", "0");
								ProductElement.put("PACKAGE_DEFAULT_TAG", "1");
								ProductElement.put("ELEMENT_FORCE_TAG", "0");
								ProductElement.put("ELEMENT_DEFAULT_TAG", "1");
								// e = ProductElement;
							}
							break;
						}

					}

				}

			}

			IDataset forceElements = getUserOpenProductForceElement(newProductElements);
			if (forceElements != null && forceElements.size() > 0)
			{

				int size = forceElements.size();
				for (int i = 0; i < size; i++)
				{

					IData element = forceElements.getData(i);

					IDataset attrItemAList = AttrItemInfoQry.getElementItemA(element.getString("ELEMENT_TYPE_CODE"), element.getString("ELEMENT_ID"), eparchyCode);
					IDataset attrs = this.makeAttrs(null, attrItemAList);
					if (attrs != null && attrs.size() > 0)
					{
						element.put("ATTR_PARAM", attrs);
					}
					element.remove("START_DATE");
					element.remove("END_DATE");
					ProductModuleData forceElement = null;
					if (BofConst.ELEMENT_TYPE_CODE_SVC.equals(element.getString("ELEMENT_TYPE_CODE")))
					{
						forceElement = new SvcData(element);
					} else if (BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(element.getString("ELEMENT_TYPE_CODE")))
					{
						forceElement = new DiscntData(element);
					}
					startDate = ProductModuleCalDate.calStartDate(forceElement, env);
					element.put("START_DATE", startDate);
					String endDate = ProductModuleCalDate.calEndDate(forceElement, startDate);
					element.put("END_DATE", endDate);
					element.put("MODIFY_TAG", BofConst.MODIFY_TAG_ADD);
					if (BofConst.ELEMENT_TYPE_CODE_SVC.equals(element.getString("ELEMENT_TYPE_CODE")))
					{
						element.put("ELEMENT_NAME", USvcInfoQry.getSvcNameBySvcId(element.getString("ELEMENT_ID")));
					} else if (BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(element.getString("ELEMENT_TYPE_CODE")))
					{
						element.put("ELEMENT_NAME", UDiscntInfoQry.getDiscntNameByDiscntCode(element.getString("ELEMENT_ID")));
					}
					selectedElements.add(element);

				}
			}

			if (IDataUtil.isNotEmpty(productInfos))
			{
				if ("NET0".equals(productInfos.getString("BRAND_CODE")) || "NET3".equals(productInfos.getString("BRAND_CODE")) || "NET4".equals(productInfos.getString("BRAND_CODE")))
				{
					if (forceElements != null && forceElements.size() > 0)
					{

						int size = forceElements.size();
						IDataset resList = new DatasetList();
						IDataset serviceRes = new DatasetList();
						for (int i = 0; i < size; i++)
						{
							IData element = forceElements.getData(i);
							if ("S".equals(element.getString("ELEMENT_TYPE_CODE")))
							{
								serviceRes.clear();
								serviceRes = ServiceResInfoQry.queryServiceResInfo(element.getString("ELEMENT_ID"));
								for (int j = 0; j < serviceRes.size(); j++)
								{
									serviceRes.getData(j).put("MODIFIED", "1");
									if ("L".equals(serviceRes.getData(j).getString("RES_TYPE_CODE")))
									{
										continue;
									} else
									{
										resList.add(serviceRes.getData(i));
									}
								}

							}

						}
						result.put("RES_LIST", resList);

					}
				}
			}
		}

		// 加载相关费用
		int size = selectedElements.size();
		for (int i = 0; i < size; i++)
		{
			IData element = selectedElements.getData(i);
			if ("0".equals(element.getString("MODIFY_TAG")))
			{
				// IDataset feeConfigs =
				// ProductFeeInfoQry.getElementFee(param.getString("TRADE_TYPE_CODE"),
				// CSBizBean.getVisit().getInModeCode(), "",
				// element.getString("ELEMENT_TYPE_CODE"),
				// element.getString("PRODUCT_ID"), element
				// .getString("PACKAGE_ID"), "-1",
				// element.getString("ELEMENT_ID"), eparchyCode, "3");
				IDataset feeConfigs = ProductFeeInfoQry.getElementFeeList(param.getString("TRADE_TYPE_CODE"), BofConst.ELEMENT_TYPE_CODE_PRODUCT, element.getString("PRODUCT_ID"), element.getString("ELEMENT_TYPE_CODE"), element.getString("ELEMENT_ID"), element.getString("PACKAGE_ID"));
				if (IDataUtil.isEmpty(feeConfigs))
				{
					continue;
				}
				int feeSize = feeConfigs.size();
				IDataset feeDatas = new DatasetList();
				for (int j = 0; j < feeSize; j++)
				{
					IData feeConfig = feeConfigs.getData(j);
					if (!"0".equals(feeConfig.getString("PAY_MODE")))
					{
						continue;
					}
					IData feeData = new DataMap();
					feeData.put("TRADE_TYPE_CODE", param.getString("TRADE_TYPE_CODE"));
					feeData.put("MODE", feeConfig.getString("FEE_MODE"));
					feeData.put("CODE", feeConfig.getString("FEE_TYPE_CODE"));
					feeData.put("FEE", feeConfig.getString("FEE"));
					feeDatas.add(feeData);
				}
				element.put("FEE_DATA", feeDatas);
			}
		}
		DataHelper.sort(selectedElements, "ELEMENT_TYPE_CODE", IDataset.TYPE_STRING, IDataset.ORDER_ASCEND, "MODIFY_TAG", IDataset.TYPE_STRING, IDataset.ORDER_ASCEND);
		result.put("SELECTED_ELEMENTS", selectedElements);
		resultList.add(result);
		return resultList;
	}

	private IDataset getUserOpenProductForceElement(IDataset productElements) throws Exception
	{
		if (IDataUtil.isEmpty(productElements))
		{
			return null;
		}
		int size = productElements.size();
		IDataset result = new DatasetList();
		for (int i = 0; i < size; i++)
		{
			IData element = productElements.getData(i);
			if (("1".equals(element.getString("PACKAGE_DEFAULT_TAG")) || "1".equals(element.getString("PACKAGE_FORCE_TAG"))) && ("1".equals(element.getString("ELEMENT_DEFAULT_TAG")) || "1".equals(element.getString("ELEMENT_FORCE_TAG"))))
			{
				result.add(element);
			}
		}
		return result;
	}

	public IDataset getWidenetUserOpenElements(IData param) throws Exception
	{
		String eparchyCode = param.getString("EPARCHY_CODE");
		this.setRouteId(eparchyCode);
		String newProductId = param.getString("NEW_PRODUCT_ID", "");

		IDataset resultList = new DatasetList();

		if (StringUtils.isEmpty(newProductId))
		{
			return resultList;
		}

		ProductTimeEnv env = new ProductTimeEnv();
		String startDate = SysDateMgr.getSysTime();
		IDataset selectedElements = new DatasetList();
		IData result = new DataMap();

		// 必选或者默认的元素
		IDataset forceElements = UpcCall.qryAllOffersByOfferIdWithForceTagDefaultTagFilter(BofConst.ELEMENT_TYPE_CODE_PRODUCT, newProductId, "1", "1");

		// IDataset newProductElements =
		// ProductInfoQry.getProductElements(newProductId, eparchyCode);

		forceElements = ProductUtils.offerToElement(forceElements, newProductId);

		// 过滤无权限资费
		// ElementPrivUtil.filterElementListByPriv(getVisit().getStaffId(),
		// forceElements);
		// IDataset forceElements =
		// getUserOpenProductForceElement(newProductElements);
		if (forceElements != null && forceElements.size() > 0)
		{
			int size = forceElements.size();
			for (int i = 0; i < size; i++)
			{
				IData element = forceElements.getData(i);

				IDataset attrItemAList = AttrItemInfoQry.getElementItemA(element.getString("ELEMENT_TYPE_CODE"), element.getString("ELEMENT_ID"), eparchyCode);

				IDataset attrs = this.makeAttrs(null, attrItemAList);
				if (attrs != null && attrs.size() > 0)
				{
					element.put("ATTR_PARAM", attrs);
				}
				element.remove("START_DATE");
				element.remove("END_DATE");
				ProductModuleData forceElement = null;
				if (BofConst.ELEMENT_TYPE_CODE_SVC.equals(element.getString("ELEMENT_TYPE_CODE")))
				{
					forceElement = new SvcData(element);
				} else if (BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(element.getString("ELEMENT_TYPE_CODE")))
				{
					forceElement = new DiscntData(element);
				}
				startDate = ProductModuleCalDate.calStartDate(forceElement, env);
				element.put("START_DATE", startDate);
				String endDate = ProductModuleCalDate.calEndDate(forceElement, startDate);
				element.put("END_DATE", endDate);
				element.put("MODIFY_TAG", BofConst.MODIFY_TAG_ADD);
				if (BofConst.ELEMENT_TYPE_CODE_SVC.equals(element.getString("ELEMENT_TYPE_CODE")))
				{
					element.put("ELEMENT_NAME", USvcInfoQry.getSvcNameBySvcId(element.getString("ELEMENT_ID")));
				} else if (BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(element.getString("ELEMENT_TYPE_CODE")))
				{
					element.put("ELEMENT_NAME", UDiscntInfoQry.getDiscntNameByDiscntCode(element.getString("ELEMENT_ID")));
				}
				selectedElements.add(element);

			}
		}

		String booktag =  param.getString("BOOKTAG","");
		if(booktag ==null || !"1".equals(booktag)){
		// 加载相关费用
		int size = selectedElements.size();
		for (int i = 0; i < size; i++)
		{
			IData element = selectedElements.getData(i);
			if ("0".equals(element.getString("MODIFY_TAG")))
			{
				IDataset feeConfigs = ProductFeeInfoQry.getElementFeeList(param.getString("TRADE_TYPE_CODE"), BofConst.ELEMENT_TYPE_CODE_PRODUCT, element.getString("PRODUCT_ID"), element.getString("ELEMENT_TYPE_CODE"), element.getString("ELEMENT_ID"), element.getString("PACKAGE_ID"));
				if (IDataUtil.isEmpty(feeConfigs))
				{
					continue;
				}
				int feeSize = feeConfigs.size();
				IDataset feeDatas = new DatasetList();
				for (int j = 0; j < feeSize; j++)
				{
					IData feeConfig = feeConfigs.getData(j);
					if (!"0".equals(feeConfig.getString("PAY_MODE")))
					{
						continue;
					}
					IData feeData = new DataMap();
					feeData.put("TRADE_TYPE_CODE", param.getString("TRADE_TYPE_CODE"));
					feeData.put("MODE", feeConfig.getString("FEE_MODE"));
					feeData.put("CODE", feeConfig.getString("FEE_TYPE_CODE"));
					feeData.put("FEE", feeConfig.getString("FEE"));
					if ("600".equals(param.getString("TRADE_TYPE_CODE")) || "611".equals(param.getString("TRADE_TYPE_CODE")) || "612".equals(param.getString("TRADE_TYPE_CODE")))
					{
						// 特殊用户金额默认为零修改
						if (StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "CRM_WIDEOPENFEE"))
						{
							feeData.put("FEE", "0");
						}
					}

					if ("613".equals(param.getString("TRADE_TYPE_CODE")))
					{
						if (!"521".equals(feeConfig.getString("FEE_TYPE_CODE")))
						{
							if (StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "CRM_WIDEOPENFEE"))
							{
								feeData.put("FEE", "0");
							}
						}
					}
					feeDatas.add(feeData);
				}
				element.put("FEE_DATA", feeDatas);
			}
		}
		}
		DataHelper.sort(selectedElements, "ELEMENT_TYPE_CODE", IDataset.TYPE_STRING, IDataset.ORDER_ASCEND, "MODIFY_TAG", IDataset.TYPE_STRING, IDataset.ORDER_ASCEND);
		result.put("SELECTED_ELEMENTS", selectedElements);
		resultList.add(result);
		return resultList;

	}

	/**
	 * @Description: 是否处理费用【false 不处理 SERVICE_ID=15】
	 * @param userId
	 * @param elementId
	 * @param elementTypeCode
	 * @param userElements
	 * @return
	 * @throws Exception
	 * @author: maoke
	 * @date: Jul 2, 2014 2:56:09 PM
	 */
	public boolean isDealFeeByServiceId15(String userId, String elementId, String elementTypeCode, IDataset userElements) throws Exception
	{
		String staffId = CSBizBean.getVisit().getStaffId();

		if ("15".equals(elementId) && BofConst.ELEMENT_TYPE_CODE_SVC.equals(elementTypeCode))
		{
			IDataset foregift = UserOtherInfoQry.getUserOtherservByPK(userId, "FG", "0", null);

			if (IDataUtil.isNotEmpty(foregift))
			{
				for (int i = 0, size = foregift.size(); i < size; i++)
				{
					IData gift = foregift.getData(i);
					String rsrvNum1 = gift.getString("RSRV_NUM1", "");
					String rsrvNum2 = gift.getString("RSRV_NUM2", "");

					if ("3".equals(rsrvNum1) && "80000".equals(rsrvNum2))
					{
						return false;
					}
				}
			}

			// 个人大客户不收取押金
			if (CustVipInfoQry.isPersonCustVip(userId))
			{
				return false;
			}

			// 10086客服客服工号不显示押金发票号码
			// 20150609 QR-20150505-03 客服工号办理国际漫游没有免押金功能
			/**
			 * if (staffId.indexOf("HNYD") != -1) { return false; } else {
			 * return true; }
			 **/
		}
		return true;
	}

	private IDataset makeAttrs(IDataset userAttrs, IDataset elementItemAList)
	{

		if (elementItemAList != null && elementItemAList.size() > 0)
		{
			int size = elementItemAList.size();
			IDataset returnAttrs = new DatasetList();
			for (int i = 0; i < size; i++)
			{
				IData attr = new DataMap();
				IData itemA = elementItemAList.getData(i);
				attr.put("ATTR_CODE", itemA.getString("ATTR_CODE"));
				attr.put("ATTR_VALUE", "");
				if (userAttrs != null && userAttrs.size() > 0)
				{
					int uSize = userAttrs.size();
					for (int j = 0; j < uSize; j++)
					{
						IData userAttr = userAttrs.getData(j);
						if (itemA.getString("ATTR_CODE").equals(userAttr.getString("ATTR_CODE")))
						{
							attr.put("ATTR_VALUE", userAttr.getString("ATTR_VALUE"));
							break;
						}
					}
				} else
				{
					attr.put("ATTR_VALUE", itemA.getString("ATTR_INIT_VALUE"));
				}
				returnAttrs.add(attr);
			}
			return returnAttrs;
		} else
		{
			return null;
		}
	}

	/**
	 * 集团产品成员新增、集团产品成员变更获取元素
	 * 
	 * @param param
	 * @author weixb3
	 * @return
	 * @throws Exception
	 */
	public IDataset memBBossElements(IData param) throws Exception
	{

		// 是否重新打开标志，true表示页面是同一次操作再次被访问
		boolean isReOpen = param.getBoolean("IS_REOPEN");
		// 操作类型
		String productOperType = param.getString("PRODUCT_OPER_TYPE");

		IData inparam = new DataMap();

		// 新增操作
		if ("1".equals(productOperType))
		{
			// 首次访问
			if (false == isReOpen)
			{
				inparam.put("MEB_USER_ID", param.getString("MEB_USER_ID"));
				inparam.put("GRP_USER_ID", param.getString("GRP_USER_ID"));
                inparam.put("PRODUCT_ID", param.getString("PRODUCT_ID"));

				return this.getGrpMebOpenElements(inparam);
			} else
			{

				IDataset tempProductElements = param.getDataset("TEMP_PRODUCTS_ELEMENT");
				IDataset resultList = new DatasetList();
				IData result = new DataMap();
				result.put("SELECTED_ELEMENTS", tempProductElements);
				resultList.add(result);
				return resultList;

			}
		}
		// 变更操作
		else
		{
			// 首次访问
			if (false == isReOpen)
			{
				inparam.put("MEB_USER_ID", param.getString("MEB_USER_ID"));
				inparam.put("GRP_USER_ID", param.getString("GRP_USER_ID"));
				inparam.put("PRODUCT_ID", param.getString("PRODUCT_ID"));
				return this.getGrpMebChgElements(inparam);
			} else
			{
				IDataset tempProductElements = param.getDataset("TEMP_PRODUCTS_ELEMENT");
				IDataset resultList = new DatasetList();
				IData result = new DataMap();
				result.put("SELECTED_ELEMENTS", tempProductElements);
				resultList.add(result);
				return resultList;
			}
		}
	}

	/**
	 * @Description: 对于终止到月底的优惠，且元素配置的cancel_tag=3的时候 DISABLED
	 * @param selectedElements
	 * @throws Exception
	 * @author: maoke
	 * @date: Aug 30, 2014 2:40:10 PM
	 */
	public void setDisabledDiscntData(IDataset selectedElements) throws Exception
	{
		for (int i = 0, size = selectedElements.size(); i < size; i++)
		{
			IData element = selectedElements.getData(i);

			String elementTypeCode = element.getString("ELEMENT_TYPE_CODE", "");
			String modifyTag = element.getString("MODIFY_TAG");
			String disabled = element.getString("DISABLED", "");

			if (BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(elementTypeCode) && "exist".equals(modifyTag) && !"true".equals(disabled))
			{

				String lastDateThissMonth = SysDateMgr.getLastDateThisMonth();
				String elementEndDate = element.getString("END_DATE", "");

				if ((elementEndDate + SysDateMgr.END_DATE).equals(lastDateThissMonth))
				{
					String elementId = element.getString("ELEMENT_ID");
					String packageId = element.getString("PACKAGE_ID");

					IDataset pkgElemInfos = BofQuery.getServElementByPk(packageId, elementId, BofConst.ELEMENT_TYPE_CODE_DISCNT);

					if (IDataUtil.isNotEmpty(pkgElemInfos))
					{
						String cancelTag = pkgElemInfos.getData(0).getString("CANCEL_TAG");

						if (StringUtils.isNotBlank(cancelTag) && "3".equals(cancelTag))
						{
							selectedElements.getData(i).put("DISABLED", "true");
						}
					}
				}
			}
		}
	}

	public IDataset getElementAttr4ProFlow(IData param) throws Exception
	{
		IDataset attrs = AttrItemInfoQry.getElementAttr4ProFlow(param);
		return attrs;
	}

	/**
	 * 处理资费信息和资费属性信息,针对视频流量资费做了特殊处理
	 * 
	 * @author chenmw3
	 * @date 2017-05-18
	 * @param discnt
	 * @param userAttrs
	 * @return
	 * @throws Exception
	 */
	private IDataset dealDiscntInfo(IData discnt, IDataset userAttrs) throws Exception
	{
		String elementId = discnt.getString("ELEMENT_ID");
		String instId = discnt.getString("INST_ID");
		IDataset elementAttrParam = discnt.getDataset("ATTR_PARAM");
		IDataset commparaInfos = CommparaInfoQry.getCommparaByCode1("CSM", "2017", elementId, "IS_VIDEO_PKG", null);
		// 如果不是视频流量信息
		if (IDataUtil.isEmpty(commparaInfos))
		{
			IDataset userElementAttrs = this.getUserAttrByRelaInstId(userAttrs, instId);
			IDataset attrResult = this.makeAttrs(userElementAttrs, elementAttrParam);
			return attrResult;
		}

		// 筛选出用户资费的属性信息
		IDataset userDiscntAttrs = new DatasetList();
		for (int i = 0, size = userAttrs.size(); i < size; i++)
		{
			IData userElementAttr = userAttrs.getData(i);
			String relaInstId = userElementAttr.getString("RELA_INST_ID");
			if (instId.equals(relaInstId))
			{
				IData userElementAttrClone = (IData) Clone.deepClone(userElementAttr);
				userDiscntAttrs.add(userElementAttrClone);
			}
		}

		IDataset returnAttrs = new DatasetList();
		if (IDataUtil.isEmpty(elementAttrParam))
		{
			return returnAttrs;
		}
		for (int i = 0, size = elementAttrParam.size(); i < size; i++)
		{
			IData attr = new DataMap();
			IData item = elementAttrParam.getData(i);
			String itemAttrCode = item.getString("ATTR_CODE");
			attr.put("ATTR_CODE", itemAttrCode);
			if (IDataUtil.isEmpty(userDiscntAttrs))
			{
				attr.put("ATTR_VALUE", item.getString("ATTR_INIT_VALUE"));
				returnAttrs.add(attr);
				continue;
			}
			// 需要取目前生效的attrValue
			String attrValue = userDiscntAttrs.getData(0).getString("ATTR_VALUE");
			for (int j = 0, uSize = userDiscntAttrs.size(); j < uSize; j++)
			{
				IData userDiscntAttr = userDiscntAttrs.getData(j);
				String userDiscntAttrCode = userDiscntAttr.getString("ATTR_CODE");
				String userDiscntAttrValue = userDiscntAttr.getString("ATTR_VALUE");
				if (!itemAttrCode.equals(userDiscntAttrCode))
				{
					continue;
				}
				Date sysDate = SysDateMgr.string2Date(SysDateMgr.getSysTime(), SysDateMgr.PATTERN_STAND);
				Date startDate = SysDateMgr.string2Date(userDiscntAttr.getString("START_DATE"), SysDateMgr.PATTERN_STAND);
				Date endDate = SysDateMgr.string2Date(userDiscntAttr.getString("END_DATE"), SysDateMgr.PATTERN_STAND);
				long sysTime = sysDate.getTime();
				long startTime = startDate.getTime();
				long endTime = endDate.getTime();
				if (sysTime >= startTime && sysTime <= endTime)
				{
					attrValue = userDiscntAttrValue;
					break;
				}
			}
			attr.put("ATTR_VALUE", attrValue);
			returnAttrs.add(attr);
		}
		return returnAttrs;
	}
}
