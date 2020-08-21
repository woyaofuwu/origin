package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.reg;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.collections.CollectionUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CreditTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

/**
 * 核对用户的星级是否能够办理业务
 * 核对用户的开户时间是否能够办理业务
 */
public class CheckUserCreditClassOpenDateProdAction implements ITradeAction {

	@Override
	public void executeAction(BusiTradeData btd) throws Exception {

		String tradeTypeCode = btd.getTradeTypeCode();
		UcaData uca = btd.getRD().getUca();

		// 获取用户的星级
		String strCreditClass = null;
		if ("310".equals(tradeTypeCode)) { // 如果是复机
			strCreditClass = "-1"; // 用户销户的时候，星级已经全部清空
		} else {
			strCreditClass = uca.getUserCreditClass();
		}
		if(StringUtils.isBlank(strCreditClass))
		{
			strCreditClass="-1";
		}

		int iCreditClass = Integer.parseInt(strCreditClass);

		List<DiscntTradeData> userDiscnts = uca.getUserDiscnts();
		if (CollectionUtils.isNotEmpty(userDiscnts) && userDiscnts.size() > 0) {
			for (DiscntTradeData userDiscnt : userDiscnts) {
				String modifyTag = userDiscnt.getModifyTag();
				if (BofConst.MODIFY_TAG_ADD.equals(modifyTag)) {
					String discntCode = userDiscnt.getDiscntCode();

					// 获取验证的数据星级
					IDataset creditclassRules = CommparaInfoQry.getCommparaInfoByCode("CSM", "1808", "D", discntCode, "0898");

					if (IDataUtil.isNotEmpty(creditclassRules)) {
						boolean isValid = checkCreditValid(iCreditClass,
								creditclassRules);
						if (!isValid) {
							CSAppException.apperr(CrmCommException.CRM_COMM_103, "用户的星级"+iCreditClass+"不具备订购优惠" + discntCode);
						}
					}
					
					// 获取验证的数据入网
					IDataset opendateRules = CommparaInfoQry.getCommparaInfoByCode("CSM", "1809", "D", discntCode, "0898");
					if (IDataUtil.isNotEmpty(opendateRules)) {
						IData userInfos = UcaInfoQry.qryUserInfoByUserId(userDiscnt.getUserId());
						String flag = opendateRules.getData(0).getString("PARA_CODE2");//0：天；1：月；2：年
						String openDate = opendateRules.getData(0).getString("PARA_CODE3","");
						// 判断开户时间
				        if ("0".equals(flag) && SysDateMgr.addDays(userInfos.getString("OPEN_DATE"), Integer.parseInt(openDate)).compareTo(SysDateMgr.getSysDate()) > 0)
				        {
				        	CSAppException.apperr(CrmCommException.CRM_COMM_103, "用户的开户时间"+userInfos.getString("OPEN_DATE")+"不具备订购优惠" + discntCode);
				        }
				        
				        if ("1".equals(flag) && SysDateMgr.addMonths(userInfos.getString("OPEN_DATE"), Integer.parseInt(openDate)).compareTo(SysDateMgr.getSysDate()) > 0)
				        {
				        	CSAppException.apperr(CrmCommException.CRM_COMM_103, "用户的开户时间"+userInfos.getString("OPEN_DATE")+"不具备订购优惠" + discntCode);
				        }
				        
				        if ("2".equals(flag) && SysDateMgr.addYears(userInfos.getString("OPEN_DATE"), Integer.parseInt(openDate)).compareTo(SysDateMgr.getSysDate()) > 0)
				        {
				        	CSAppException.apperr(CrmCommException.CRM_COMM_103, "用户的开户时间"+userInfos.getString("OPEN_DATE")+"不具备订购优惠" + discntCode);
				        }
					}
					
					// 根据优惠绑定信用度
					IDataset creditRules = CommparaInfoQry.getCommparaInfoByCode("CSM", "1810", "D", discntCode, "0898");
			        if (IDataUtil.isNotEmpty(creditRules))
			        {
			            CreditTradeData creditTradeData = new CreditTradeData();
			            int score = Integer.parseInt(creditRules.getData(0).getString("PARA_CODE2","0"));
			            creditTradeData.setUserId(btd.getRD().getUca().getUserId());
			            creditTradeData.setCreditValue("" + score);
			            creditTradeData.setCreditMode("addCredit");// 信用度类型标识'addCredit-增加扣减信用度;callCredit-触发信控';
			            creditTradeData.setCreditGiftMonths("1");// 赠送信用度月份数
			            creditTradeData.setStartDate(userDiscnt.getStartDate());
			            creditTradeData.setEndDate(userDiscnt.getEndDate());
			            creditTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
			            creditTradeData.setRsrvStr10(creditRules.getData(0).getString("PARA_CODE4",""));//（信用度和其它信用度不叠加，取最高信用度即可）  特殊标志
			            btd.add(btd.getRD().getUca().getSerialNumber(), creditTradeData);
			        }
				}
				
				if(BofConst.MODIFY_TAG_DEL.equals(modifyTag))
				{
					String discntCode = userDiscnt.getDiscntCode();
					// 根据优惠绑定信用度
					IDataset creditRules = CommparaInfoQry.getCommparaInfoByCode("CSM", "1810", "D", discntCode, "0898");
			        if (IDataUtil.isNotEmpty(creditRules))
			        {
			            CreditTradeData creditTradeData = new CreditTradeData();
			            int score = Integer.parseInt(creditRules.getData(0).getString("PARA_CODE3","0"));
			            creditTradeData.setUserId(btd.getRD().getUca().getUserId());
			            creditTradeData.setCreditValue("" + score);
			            creditTradeData.setCreditMode("addCredit");// 信用度类型标识'addCredit-增加扣减信用度;callCredit-触发信控';
			            creditTradeData.setCreditGiftMonths("1");// 赠送信用度月份数
			            creditTradeData.setStartDate(userDiscnt.getStartDate());
			            creditTradeData.setEndDate(userDiscnt.getEndDate());
			            creditTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
			            creditTradeData.setRsrvStr10(creditRules.getData(0).getString("PARA_CODE4",""));//（信用度和其它信用度不叠加，取最高信用度即可）  特殊标志
			            btd.add(btd.getRD().getUca().getSerialNumber(), creditTradeData);
			        }
				}
			}
		}
	}

	public boolean checkCreditValid(int userCredit, IDataset configRules)
			throws Exception {
		boolean isValid = false;

		for (int i = 0, size = configRules.size(); i < size; i++) {
			String minClass = configRules.getData(i)
					.getString("PARA_CODE2", "");
			String maxClass = configRules.getData(i)
					.getString("PARA_CODE3", "");

			if (!minClass.equals("") && !maxClass.equals("")) {
				int minClassI = Integer.parseInt(minClass);
				int maxClassI = Integer.parseInt(maxClass);

				if (userCredit >= minClassI && userCredit <= maxClassI) {
					isValid = true;
				}
			} else if (!minClass.equals("")) {
				int minClassI = Integer.parseInt(minClass);

				if (userCredit >= minClassI) {
					isValid = true;
				}
			} else if (!maxClass.equals("")) {
				int maxClassI = Integer.parseInt(maxClass);

				if (userCredit <= maxClassI) {
					isValid = true;
				}
			}

		}

		return isValid;
	}
}
