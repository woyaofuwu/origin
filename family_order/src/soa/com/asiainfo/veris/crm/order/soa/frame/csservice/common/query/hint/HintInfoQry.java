
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.hint;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.mvel.rule.CRMMVELMiscCache;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.mvel.script.MVELExecutor;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UBrandInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.template.TemplateQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustVipInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.GrpInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.GrpMebInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ParamInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.TagInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.PayRelaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeReceiptInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserPlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserRecommInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleActiveInfoQry;

public class HintInfoQry
{
	/**
	 * ngboss外框展示的所有提示信息
	 * 
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static IDataset getAllHintInfos(IData input) throws Exception
	{
		IDataset returnDataset = new DatasetList();

		String userId = input.getString("USER_ID");
		String serialNumber = input.getString("SERIAL_NUMBER");
		String strEparchyCode = CSBizBean.getVisit().getStaffEparchyCode(); // 原来取的是pd.getContext().getEpachyId()，即登录员工归属

		input.put("GROUP_BRAND", UBrandInfoQry.getBrandNameByBrandCode(input.getString("BRAND_CODE", "全球通")));

		// 基本信息需靠前显示
		// 原CSBizBean里的逻辑
		IDataset hintInfos = getHintInfo(input);
		if (IDataUtil.isNotEmpty(hintInfos))
		{
			IData hintInfo = hintInfos.getData(0);

			String rsrv_str = "";

			String str1 = hintInfo.getString("HINT_INFO1", "");
			String str2 = hintInfo.getString("HINT_INFO2", "");
			String str3 = hintInfo.getString("HINT_INFO3", "");
			String str4 = hintInfo.getString("HINT_INFO4", "");
			String str5 = hintInfo.getString("HINT_INFO5", "");

			if (!"".equals(str1) || !"".equals(str2) || !"".equals(str3) || !"".equals(str4) || !"".equals(str5))
			{
				rsrv_str = str1 + "~" + str2 + "~" + str3 + "~" + str4 + "~" + str5;
			}

			String[] strArry = rsrv_str.split("~");
			int num = 1;

			for (int i = 0; i < strArry.length; i++)
			{
				if ("".equals(strArry[i]))
				{
					continue;
				}

				IData temp = new DataMap();
				temp.put("KEY", "HINT" + num);
				temp.put("VALUE", strArry[i]);
				returnDataset.add(temp);

				num++;
			}
		}

		// HXYD-YZ-REQ-20110418-010岳阳分公司关于在BOSS前台用户基本信息中添加长话（漫游）话务营销目标客户标识的需求
		// add by pengzq 2011-05-08
		IData rDualInfo = new DataMap();
		IDataset tmpDataset = TagInfoQry.getTagInfo(strEparchyCode, "TAG_ROAM_PRINT", "0", null);
		if (IDataUtil.isNotEmpty(tmpDataset))
		{
			rDualInfo = tmpDataset.getData(0);
		}

		String strTagChar = rDualInfo.getString("TAG_CHAR", "");
		String strTagInfo = rDualInfo.getString("TAG_INFO", "");

		if ("1".equals(strTagChar))
		{
			String strR1Msg = "岳阳长话营销目标客户：";
			IDataset retulstList = UserOtherInfoQry.getUserOtherservByPK(userId, "R1", "0", null);
			if (IDataUtil.isNotEmpty(retulstList))
			{
				strR1Msg += "是";
			}
			else
			{
				strR1Msg += "否";
			}

			IData tmpR1 = new DataMap();
			tmpR1.put("KEY", "ROAM_INFO1");
			tmpR1.put("VALUE", strR1Msg);
			returnDataset.add(tmpR1);
		}

		if ("1".equals(strTagInfo))
		{
			String strR1Msg = "岳阳漫游营销目标客户：";
			IDataset retulstList = UserOtherInfoQry.getUserOtherservByPK(userId, "R2", "0", null);
			if (IDataUtil.isNotEmpty(retulstList))
			{
				strR1Msg += "是";
			}
			else
			{
				strR1Msg += "否";
			}

			IData tmpR2 = new DataMap();
			tmpR2.put("KEY", "ROAM_INFO2");
			tmpR2.put("VALUE", strR1Msg);
			returnDataset.add(tmpR2);
		}

		// 1、 用户目前【是/不是】VIP客户（级别为：钻/金/银）
		if ("2".equals(strTagChar))
		{
			IDataset custvipInfos = CustVipInfoQry.qryVipInfoBySn(serialNumber);
			if (IDataUtil.isNotEmpty(custvipInfos))
			{
				IData vipInfo = custvipInfos.getData(0);
				String vipClassId = vipInfo.getString("VIP_CLASS_ID", ""); // 客户级别
				String viplevel = null;
				// 钻卡，如果没有通用参数配置则默认最多随从为1，
				if ("1".equals(vipClassId))
				{
					viplevel = "钻卡)";
				}
				// 金卡，如果没有通用参数配置则默认最多随从为1，
				else if ("2".equals(vipClassId))
				{
					viplevel = "金卡)";
				}
				// 银卡，如果没有通用参数配置则默认最多随从为1，
				else if ("3".equals(vipClassId))
				{
					viplevel = "银卡)";
				}

				String strR1Msg = "用户目前【是】VIP客户(级别为：" + viplevel;
				IData tmpR3 = new DataMap();
				tmpR3.put("KEY", "ROAM_INFO3");
				tmpR3.put("VALUE", strR1Msg);
				returnDataset.add(tmpR3);
			}
		}

		// 2、 用户目前【是/不是】红名单客户
		boolean redFlag = false;

		// modify by xiongjian2，这里调用信控接口获取红名单信息，兼容错误，即使调用错误，也不影响前台展示和业务受理
		try
		{
			redFlag = AcctCall.checkIsRedUser(userId);
		}
		catch (Exception e)
		{

		}

		if (redFlag)
		{
			String strR2Msg = "用户目前【是】红名单客户";

			IData tmpR4 = new DataMap();
			tmpR4.put("KEY", "ROAM_INFO4");
			tmpR4.put("VALUE", strR2Msg);
			returnDataset.add(tmpR4);
		}

		// 3、 用户目前【是/不是】托收客户
		String acctId = null;
		IData acctInfo = new DataMap();
		IData dataset = UcaInfoQry.qryPayRelaByUserId(userId);
		if (IDataUtil.isNotEmpty(dataset))
		{
			// 原逻辑查的是TF_F_ACCOUNT-SEL_BY_PK1，未找到该sql
			IData accountInfos = UcaInfoQry.qryAcctInfoByAcctId(dataset.getString("ACCT_ID"));
			if (IDataUtil.isNotEmpty(accountInfos))
			{
				acctId = accountInfos.getString("ACCT_ID");
				acctInfo = accountInfos;
			}
		}

		if ("1".equals(acctInfo.get("PAY_MODE_CODE")))
		{
			String strR3Msg = "用户目前【是】托收客户";

			IData tmpR5 = new DataMap();
			tmpR5.put("KEY", "ROAM_INFO5");
			tmpR5.put("VALUE", strR3Msg);
			returnDataset.add(tmpR5);
		}

		// 4、 用户目前【是/不是】手机钱包客户
		IDataset set = UserPlatSvcInfoQry.queryUserPlatByUserType(userId, "21");
		if (IDataUtil.isNotEmpty(set))
		{
			String strR4Msg = "用户目前【是】手机钱包客户";
			IData tmpR6 = new DataMap();
			tmpR6.put("KEY", "ROAM_INFO6");
			tmpR6.put("VALUE", strR4Msg);

			returnDataset.add(tmpR6);
		}

		// 5、 用户目前【是/不是】付费关系客户
		IDataset pay1set = PayRelaInfoQry.queryNormalPayre(userId, acctId, "-1", "1");
		if (IDataUtil.isNotEmpty(pay1set))
		{
			String strR7Msg = "用户目前【是】普通付费关系客户";

			IData tmpR9 = new DataMap();
			tmpR9.put("KEY", "ROAM_INFO9");
			tmpR9.put("VALUE", strR7Msg);
			returnDataset.add(tmpR9);
		}

		// 6、 用户目前【是/不是】高级付费关系客户
		IDataset pay2set = PayRelaInfoQry.getPayRelatInfoByUserIdVALID2(userId);
		if (IDataUtil.isNotEmpty(pay2set))
		{
			String strR8Msg = "用户目前【是】高级付费关系客户";

			IData tmpR10 = new DataMap();
			tmpR10.put("KEY", "ROAM_INFO10");
			tmpR10.put("VALUE", strR8Msg);
			returnDataset.add(tmpR10);
		}

		// 7、 用户目前【是/不是】鲤鱼江80元保干客户 其中鲤鱼江80元包干通过客户目前是否存在优惠（35000217 鲤鱼江80元保底）进行判断；
		if (ParamInfoQry.existsUserDiscnt(userId, "3500021"))
		{
			String strR5Msg = "用户目前【是】鲤鱼江80元保干客户";

			IData tmpR7 = new DataMap();
			tmpR7.put("KEY", "ROAM_INFO7");
			tmpR7.put("VALUE", strR5Msg);
			returnDataset.add(tmpR7);
		}

		// 8、 用户目前【是/不是】300元包干客户 其中300元包干客户通过客户目前
		// 是否存在优惠（35001097 重点客户话费包干300元或者优惠35288000 重点客户话费包干300元(新)）进行判断；
		if (ParamInfoQry.existsUserDiscnt(userId, "35001097"))
		{
			String strR6Msg = "用户目前【是】300元包干客户";

			IData tmpR8 = new DataMap();
			tmpR8.put("KEY", "ROAM_INFO8");
			tmpR8.put("VALUE", strR6Msg);
			returnDataset.add(tmpR8);
		}

		if ("3".equals(strTagInfo))
		{
			String strR1Msg = "预警集团成员：";
			IDataset retulstList = UserOtherInfoQry.getUserOtherservByPK(userId, "SX", "0", null);
			if (IDataUtil.isNotEmpty(retulstList))
			{
				strR1Msg += "是";

				IData tmpSX = new DataMap();
				tmpSX.put("KEY", "ROAM_INFOSX");
				tmpSX.put("VALUE", strR1Msg);
				returnDataset.add(tmpSX);
			}
		}

		// HXYD-YZ-REQ-20120113-036关于长沙分公司新增集团客户加入集团时间提示的界面需求
		// td_s_tag CS_TAG_ROAM_PRINT_CS 类型1,长沙集团成员客户
		IData groupShowtaginfo = new DataMap();
		IDataset tagInfos = TagInfoQry.getTagInfo(strEparchyCode, "CS_TAG_ROAM_PRINT_CS", "0", null);
		if (IDataUtil.isNotEmpty(tagInfos))
		{
			groupShowtaginfo = tagInfos.getData(0);
		}

		String grpstrTagChar = groupShowtaginfo.getString("TAG_CHAR", "");
		String grpstrTaginfo = groupShowtaginfo.getString("TAG_INFO", "");
		if ("1".equals(grpstrTagChar))
		{
			if (userId.length() == 16)
			{
				IDataset retulstList = GrpMebInfoQry.queryGrpInfoByUserId(userId);

				String strR1Msg = "该客户";
				if (retulstList != null && retulstList.size() > 0)
				{
					IData groupmemberInfo = retulstList.getData(0);
					strR1Msg += groupmemberInfo.getString("JOIN_DATE") + " 加入 " + groupmemberInfo.getString("GROUP_CUST_NAME");
				}
				else
				{
					strR1Msg += "非集团成员";
				}

				IData tmpR1 = new DataMap();
				tmpR1.put("KEY", "ROAM_INFO9");
				tmpR1.put("VALUE", strR1Msg);
				returnDataset.add(tmpR1);
			}
		}

		/* 物理集团关键人 HXYD-YZ-REQ-20110504-005关于客户类型进行系统前台窗口提示的需求 */
		String strGroupKeyMan = getGroupKeyMan(userId);
		if (strGroupKeyMan != null && !"".equals(strGroupKeyMan))
		{
			IData keyData = new DataMap();
			keyData.put("KEY", "KEY_INFO1");
			keyData.put("VALUE", "<span style=\"color:red\">" + strGroupKeyMan + "</span>");
			returnDataset.add(keyData);
		}

		/* 三倍包打档次 HXYD-YZ-REQ-20110809-024关于永州分公司三倍包打享实惠促销方案的补充需求 */
		IData inparam = new DataMap();
		inparam.put("USER_ID", userId);
		inparam.put("RSRV_VALUE_CODE", "MARUP");

		IDataset otherInfos = UserOtherInfoQry.queryUserOtherByUserValueCode(userId, "MARUP");
		if (IDataUtil.isNotEmpty(otherInfos))
		{
			IData other = otherInfos.getData(0);
			String valueCode = other.getString("RSRV_VALUE_CODE").trim();
			String value = valueCode.substring(5);
			String info = "三倍包打" + value + "档次";

			IData keyData = new DataMap();
			keyData.put("KEY", "KEY_INFO2");
			keyData.put("VALUE", "<span style=\"color:red\">" + info + "</span>");
			returnDataset.add(keyData);
		}

		// 集团彩铃成员
		IDataset relaUUs = RelaUUInfoQry.getRelationUUInfoByDeputySn(userId, "26", null);
		if (IDataUtil.isNotEmpty(relaUUs))
		{
			IData test = new DataMap();
			test.put("KEY", "ROAM_INFO10");
			test.put("VALUE", "<span style=\"color:red\">该客户为集团彩铃成员，可办理集团彩铃2013年年包。</span>");
			returnDataset.add(test);
		}

		/* 业务推荐 */
		IDataset recomms = UserRecommInfoQry.queryElementNameByUserId(userId);

		if (recomms.size() > 0)
		{
			IData temp = new DataMap();
			temp.put("KEY", "HINT_INFO1");
			temp.put("VALUE", "<strong>新业务推荐列表</strong>");
			temp.put("VALUE", "<strong>新业务推荐列表</strong> <a href=\"#nogo\" onclick=\"openNavByUrl('【新业务推荐】','/personserv/personserv?service=page/speservice.NewSvcRecomdInfo&listener=onInitTrade&SERIAL_NUMBER=" + serialNumber + "&QUERY_TAG=1"
					+ "')\">【受理】</a>");
			returnDataset.add(temp);

			for (int i = 0; i < recomms.size(); ++i)
			{
				IData tempData = new DataMap();
				tempData = recomms.getData(i);
				tempData.put("KEY", "HINT_INFO" + (i + 1));
				tempData.put("VALUE", tempData.getString("ELEMENT_NAME", ""));
				returnDataset.add(tempData);
			}
		}

		// 怀化分公司2012年未稳定客户在系统中进行标识的需求申请
		IData HHSwitch = new DataMap();
		IDataset HHSwitchs = TagInfoQry.getTagInfo(strEparchyCode, "HH_TAG_ROAM_PRINT_HH", "0", null);
		if (IDataUtil.isNotEmpty(HHSwitchs))
		{
			HHSwitch = HHSwitchs.getData(0);
		}

		String hhTagChar = HHSwitch.getString("TAG_CHAR", "");
		String hhTaginfo = HHSwitch.getString("TAG_INFO", "");
		if ("1".equals(hhTagChar))
		{
			if (userId.length() == 16)
			{
				// 1.是否办理四惠
				IDataset resultList = UserSaleActiveInfoQry.queryUserSaleActiveByUserId(userId);
				if (IDataUtil.isNotEmpty(resultList))
				{
					IData tempData = new DataMap();
					tempData.put("KEY", "HH_INFO");
					tempData.put("VALUE", "是否办理四惠：否");
					returnDataset.add(tempData);
				}
				else
				{
					for (int i = 0; i < resultList.size(); ++i)
					{
						IData tempData = new DataMap();
						tempData = resultList.getData(i);
						tempData.put("KEY", "HH_INFO" + (i + 1));
						tempData.put("VALUE", "是否办理四惠：'" + tempData.getString("PRODUCT_NAME") + "',活动截止时间:" + tempData.getString("END_DATE"));
						returnDataset.add(tempData);
					}
				}

				// 2.是否加入四网
				// 测试环境和正式环境不一样，测试环境uop_cen1在同一个物理库，可以用uop_cen1.,但是正式环境不在一个物理库，但是可以用物化视图
				IDataset resultList1 = RelaUUInfoQry.QueryRelaUUByUserId2(userId);
				if (IDataUtil.isNotEmpty(resultList1))
				{
					IData tempData = new DataMap();
					tempData.put("KEY", "HH1_INFO");
					tempData.put("VALUE", "是否加入四网：否");
					returnDataset.add(tempData);
				}
				else
				{
					for (int i = 0; i < resultList1.size(); ++i)
					{
						IData tempData = new DataMap();
						tempData = resultList1.getData(i);
						tempData.put("KEY", "HH1_INFO" + (i + 1));
						tempData.put("VALUE", "是否加入四网：" + tempData.getString("RELATION_TYPE_NAME"));
						returnDataset.add(tempData);
					}
				}
			}
		}
		/** 湘潭分公司短信中心设错用户提示信息HXYD-YZ-REQ-20130905-014 by yangyq 2013-12-25 15:17:09 **/
		Long smspValue = Long.parseLong(userId) % 10000;
		IDataset smsCenteErrorInfo = UserOtherInfoQry.getUserOtherInfo(userId, "SMS_CENTER_ERROR", smspValue.toString(), "1");
		if (IDataUtil.isNotEmpty(smsCenteErrorInfo))
		{
			IData temp = new DataMap();
			temp.put("KEY", "HINT_INFO1");
			temp.put("VALUE", "<strong>用户短信中心号码设置错误</strong> <a href=\"#nogo\" onclick=\"openNavByUrl('【用户短信中心号码设置错误】','/personserv/personserv?service=page/smscentersnerror.SetSmsCenterSNInfo&listener=initPage&SERIAL_NUMBER=" + serialNumber
					+ "&QUERY_TAG=1" + "')\">【受理】</a>");
			returnDataset.add(temp);
		}

		return returnDataset;
	}

	/**
	 * 集团关键人信息
	 * 
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	private static String getGroupKeyMan(String userId) throws Exception
	{
		String strGroupKeyMan = "";

		IDataset keyMans = GrpInfoQry.queryGrpKeyManByUserId(userId);
		if (IDataUtil.isNotEmpty(keyMans))
		{
			String strGroupID = keyMans.getData(0).getString("GROUP_ID", "");
			String strCustID = keyMans.getData(0).getString("GROUP_CUST_ID", "");
			String strRsrvStr1 = keyMans.getData(0).getString("RSRV_STR1", "");
			String strGroupName = "";
			String strDataName = "";

			// 关键人级别信息
			if (!"".equals(strRsrvStr1))
			{
				strDataName = StaticUtil.getStaticValue("GROUPKEYMAN_KEYMANLEVEL", strRsrvStr1);
			}

			if (strDataName != null && !"".equals(strDataName))
			{
				strGroupKeyMan += "【" + strDataName + "】";
			}
			else
			{
				strGroupKeyMan += "【集团关键人】";
			}

			strGroupKeyMan += strGroupName;
		}

		return strGroupKeyMan;
	}

	/**
	 * 获取提示信息（小栏框）
	 * 
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public static IDataset getHintInfo(IData input) throws Exception
	{
		IDataUtil.chkParam(input, "SERIAL_NUMBER");
		IDataUtil.chkParam(input, "TRADE_TYPE_CODE");

		UcaData uca = null;
		try
		{
			uca = UcaDataFactory.getNormalUca(input.getString("SERIAL_NUMBER"));
		}
		catch (Exception e)
		{
			return new DatasetList();
		}

		IDataset returnDataset = new DatasetList();
		IData returnData = new DataMap();

		// 模板查询
		IDataset tradeReceipts = new DatasetList();
		String tradeTypeCode = input.getString("TRADE_TYPE_CODE");
		String productId = input.getString("PRODUCT_ID");
		String brandCode = input.getString("BRAND_CODE");
		String eparchyCode = CSBizBean.getTradeEparchyCode();

		// 先根据传入的业务类型TRADE_TYPE_CODE去查，如果没查到，则查通用配置TRADE_TYPE_CODE=-1
		tradeReceipts = TradeReceiptInfoQry.getReceiptInfoByPk(tradeTypeCode, brandCode, productId, "Y", eparchyCode, null);
		if (IDataUtil.isEmpty(tradeReceipts))
		{
			tradeReceipts = TradeReceiptInfoQry.getReceiptInfoByPk("-1", brandCode, productId, "Y", eparchyCode, null);
		}

		// 如果没查到配置，则直接返回
		if (IDataUtil.isEmpty(tradeReceipts))
		{
			return returnDataset;
		}

		// 解析模板，原来只配置了RECEIPT_INFO1、RECEIPT_INFO2
		IData tradeReceipt = tradeReceipts.getData(0);
		// 关联TD_B_TEMPLATE获取模板内容
		IData templet = TemplateQry.qryTemplateContentByTempateId(tradeReceipt.getString("TEMPLATE_ID"));
		if (IDataUtil.isEmpty(templet))
		{
			return returnDataset;
		}

		String receiptInfo1 = templet.getString("TEMPLATE_CONTENT1");
		String receiptInfo2 = templet.getString("TEMPLATE_CONTENT2");

		String strContent1 = "";
		String strContent2 = "";
		String replaceReceiptInfo1 = "";
		String replaceReceiptInfo2 = "";

		String[] aReceiptInfo1 = receiptInfo1.split("~");
		String[] aReceiptInfo2 = receiptInfo2.split("~");

		// td_s_crm_mvelmisc解析准备
		MVELExecutor exector = new MVELExecutor();
		exector.setMiscCache(CRMMVELMiscCache.getMacroCache());
		exector.prepare(input, uca);

		// 解析RECEIPT_INFO1
		for (String strReceiptInfo1 : aReceiptInfo1)
		{
			replaceReceiptInfo1 = exector.applyTemplate(strReceiptInfo1);
			if (replaceReceiptInfo1 != null && !"".equals(replaceReceiptInfo1))
			{
				strContent1 += replaceReceiptInfo1;
				strContent1 += "~";
			}
		}

		// 解析RECEIPT_INFO2
		for (String strReceiptInfo2 : aReceiptInfo2)
		{
			replaceReceiptInfo2 = exector.applyTemplate(strReceiptInfo2);
			if (replaceReceiptInfo2 != null && !"".equals(replaceReceiptInfo2))
			{
				strContent2 += replaceReceiptInfo2;
				strContent2 += "~";
			}
		}
		returnData.put("PRODUCT_NAME", UProductInfoQry.getProductNameByProductId(productId));
		returnData.put("HINT_INFO1", strContent1);
		returnData.put("HINT_INFO2", strContent2);
		returnData.put("RESULT_CODE", "0");
		returnDataset.add(returnData);

		return returnDataset;
	}
}
