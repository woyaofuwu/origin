package com.asiainfo.veris.crm.order.soa.person.rule.run.createuser;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.TagInfoQry;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @Description: 开户提交校验
 * @author: sunxin
 */
public class CreatePersonUserBaseCheck extends BreBase implements IBREScript
{

	private static Logger logger = Logger.getLogger(CreatePersonUserBaseCheck.class);

	public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
	{

		if (logger.isDebugEnabled())
			logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 CreatePersonUserCheck() >>>>>>>>>>>>>>>>>>");
		String xChoiceTag = databus.getString("X_CHOICE_TAG");
		boolean bResult = false;
		String strError = null;
		if (StringUtils.isBlank(xChoiceTag) || StringUtils.equals("1", xChoiceTag))// 提交时校验，依赖请求数据
		{

			/* 获取业务资源台账信息 */
			/*
			 * IDataset resTrade = databus.getDataset("TF_B_TRADE_RES"); String
			 * simNo=""; String strImsi1 =""; // 开始逻辑取值，取出开户的simno 和imsi for
			 * (int i=0;i<resTrade.size();i++) { IData trade =
			 * resTrade.getData(i); if
			 * ("1".equals(trade.getString("RES_TYPE_CODE"))) {
			 * simNo=trade.getString("RES_CODE");
			 * strImsi1=trade.getString("IMSI"); } } if(!"".equals(simNo))
			 * simCardInfo = getResInfo(pd,strSimCardNo,"1",
			 * 0,"IGetSimCardInfo"); if(simCardInfo != null &&
			 * simCardInfo.size() > 0){ String strImsi2 =
			 * simCardInfo.getString("IMSI", ""); if( !strImsi2.equals(strImsi1)
			 * ){ BreTipsHelp.addNorTipsInfo(databus,
			 * BreFactory.TIPS_TYPE_ERROR, 751013,
			 * "登记前校验：SIM卡与IMSI不一致，建议刷新开户页面重试！"); } }
			 */
			IData reqData = databus.getData("REQDATA");// 请求的数据
			UcaData ucaData = (UcaData) databus.get("UCADATA");
			String sn = ucaData.getSerialNumber();
			String productId = reqData.getString("PRODUCT_ID");
			// 查询TD_S_TAG表

			IData data = TagInfoQry.queryTagInfo("PRODUCT_PHONE_LIMIT");

			// 号段专属产品校验 REQ201211290005 add by wenhj 2013.02.04 ---------- start
			if (IDataUtil.isNotEmpty(data))
			{
				IDataset limitData = CommparaInfoQry.getProductPhoneLimit(sn, productId);
				IData productdata = UProductInfoQry.qryProductByPK(productId);
				String strProductName = "";
				if (IDataUtil.isNotEmpty(productdata))
				{
					strProductName = productdata.getString("PRODUCT_NAME");
				}

				if (limitData.size() == 1)
				{
					if ("1".equals(limitData.get(0, "RSRV_STR1", "")))
					{
						strError = "该专属号码【" + sn + "】不能选择产品【" + productId + "|" + strProductName + "】！";
						BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 751013, strError);
						return true;
					}
					if ("2".equals(limitData.get(0, "RSRV_STR1", "")))
					{
						strError = "该号码【" + sn + "】不能选择专属产品【" + productId + "|" + strProductName + "】！";
						BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 751013, strError);
						return true;
					}
				}
				if (limitData.size() == 2)
				{
					IDataset temp = CommparaInfoQry.getProductPhoneLimit1(sn, productId);
					if (temp.size() == 0)
					{
						strError = "该专属号码【" + sn + "】不能选择专属产品【" + productId + "|" + strProductName + "】！";
						BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 751013, strError);
						return true;
					}
				}
			}
			// 号段专属产品校验 REQ201211290005 add by wenhj 2013.02.04 ---------- end

		}
		if (logger.isDebugEnabled())
			logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 CreatePersonUserCheck() " + bResult + "<<<<<<<<<<<<<<<<<<<");
		return false;
	}
}
