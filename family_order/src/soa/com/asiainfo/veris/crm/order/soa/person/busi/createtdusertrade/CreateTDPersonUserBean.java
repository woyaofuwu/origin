package com.asiainfo.veris.crm.order.soa.person.busi.createtdusertrade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import org.apache.log4j.Logger;

public class CreateTDPersonUserBean extends CSBizBean
{
	protected static Logger log = Logger.getLogger(CreateTDPersonUserBean.class);



	/*
	 * 全网一证5号校验
	 */
	public IDataset checkProvinceMorePsptId(IData input) throws Exception
	{
		IDataset ajaxDataset = new DatasetList();
		// 定义返回前端的数据结构和内容
		IData ajaxData = new DataMap();
		ajaxData.put("MSG", "OK");
		ajaxData.put("CODE", "0");
		// 获取传入参数信息
		String custName = input.getString("CUST_NAME", "").trim();
		String psptId = input.getString("PSPT_ID", "").trim();
		String psptTypeCode = input.getString("PSPT_TYPE_CODE", "");
		String serialNumber = input.getString("SERIAL_NUMBER", "").trim();// 客户资料变更是需要传，其他不用
		String tradeTypeCode = input.getString("TRADE_TYPE_CODE", "").trim();// 客户资料变更是需要传，其他不用
		String custId = input.getString("CUST_ID","").trim(); // 无线固话复机是需要传的

		log.debug("checkProvinceMorePsptId------------------>tradeTypeCode"+tradeTypeCode);
		log.debug("checkProvinceMorePsptId------------------>cust_id"+custId);

		if ("3813".equals(tradeTypeCode)){// 如果是无线固话复机业务，需要根据cust_id 获取到客户资料的证件类型、证件号和姓名
			IData custPersonInfo = UcaInfoQry.qryPerInfoByCustId(custId);
			if (IDataUtil.isNotEmpty(custPersonInfo)){
				custName = custPersonInfo.getString("CUST_NAME");
				psptId = custPersonInfo.getString("PSPT_ID");
				psptTypeCode = custPersonInfo.getString("PSPT_TYPE_CODE");
			}
		}

		log.debug("checkProvinceMorePsptId------------------>psptTypeCode"+psptTypeCode);
		log.debug("checkProvinceMorePsptId------------------>psptId"+psptId);
		log.debug("checkProvinceMorePsptId------------------>custName"+custName);

		// 查找证件类型的限制开户数量
		IDataset openLimitResult = CommparaInfoQry.getCommparaAllCol("CSM", "2556", psptTypeCode, "ZZZZ");
		log.debug("checkProvinceMorePsptId------------------>openLimitResult"+openLimitResult);
		if (openLimitResult.isEmpty())
		{// 如果本地配置没有该证件类型的限制数量配置，则直接返回
			ajaxDataset.add(ajaxData);
			// 记录日志
			return ajaxDataset;
		}

		// 进行省内一证五号校验
		if (!"".equals(custName) && !"".equals(psptId) && !"".equals(psptTypeCode))
		{
			String acctTag = "";
			if (serialNumber.length() > 0 && tradeTypeCode.equals("3811"))
			{// 客户资料变更业务，获取用户的激活状态
				IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
				acctTag = userInfo.getString("ACCT_TAG", "").trim();
			}
			log.debug("cccn-----acctTag"+acctTag);

			// 获取实名制认证的无线固话用户数量
			int rCount = UserInfoQry.getRealNameTDUserCountByPspt(custName, psptId);
			log.debug("cccn-----rCount"+rCount);
			// 根据证件号码在表：TF_F_FIXPHONE_PSPT_LIMT中查找该 证件号 允许的实名制开户数目值。默认为5个
			int rLimit = UserInfoQry.getRealNameTDUserLimitByPspt(custName, psptId);
			log.debug("cccn-----rLimit"+rLimit);

			if (rCount >= rLimit)
			{// 超过允许的实名制开户数目，拦截限制
				ajaxData.put("MSG", "【本省一证多号】校验: 证件号码【" + psptId + "】实名制开户的数量已达到最大值【" + rLimit + "个】，请更换其它证件！");
				if (acctTag.equals("0"))
				{// 如果tf_f_user的acct_tag=0，正常状态，仅提示不限制，其他状态值，需要限制
					ajaxData.put("CODE", "2");
				}else {
					ajaxData.put("CODE", "1");
				}
			}
			// 当月入网数量限制
			int openLimitNumForMonth = openLimitResult.getData(0).getInt("PARA_CODE2", 0);

			if (openLimitNumForMonth != 0){// 不为0 标识有配置当月入网数量限制
				int params = UserInfoQry.getRealNameTDUserCountByDay(custName,psptId);
				if(params>0){
					ajaxData.put("MSG2", "您的证件号码【"+psptId+"】在近七天曾办理过开户，存在被不法分子冒用进行欺诈违法活动的风险，请您核实本次开户是否本人使用？");
					ajaxData.put("CODE2", "0");
				}
			}
			ajaxDataset.add(ajaxData);
		}
		return ajaxDataset;
	}
}
