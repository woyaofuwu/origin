package com.asiainfo.veris.crm.order.soa.group.task.imp;

import com.ailk.biz.impexp.ImportTaskExecutor;
import com.ailk.biz.util.StaticUtil;
import com.ailk.cache.memcache.util.SharedCache;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ParamInfoQry;

/**
 **中小企业成员号码导入
 * @author xieqj
 * @Date 2019年11月28日
 */
public class ShortMemberImportTask extends ImportTaskExecutor{

	@Override
	public IDataset executeImport(IData data, IDataset dataset) throws Exception {
		//导入文件不为空
		if (IDataUtil.isEmpty(dataset))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "未获取到表格数据，或表格未填写完整！");
        }
		
		IDataset infos = new DatasetList();
		String operType = data.getString("OPER_TYPE");
		String productId = data.getString("PRODUCT_ID");
		String custId = data.getString("CUST_ID");
		String ecOfferStr = data.getString("EC_OFFER");
		IData ecOffer = new DataMap(ecOfferStr);
		
		for(int i =0;i< dataset.size();i++){
			IData memberData = new DataMap();
			IData importData =  dataset.getData(i);
			IDataUtil.chkParam(importData,"SERIAL_NUMBER");
			IDataUtil.chkParam(importData,"SHORT_CODE");
			
			if ("DelMeb".equals(operType)) {
				shortCodeValidateVpn(importData.getString("SHORT_CODE",""));
				//变更删除成员
				String ecUserId = ecOffer.getString("USER_ID");
				//调用 业务规则校验 成员号码
                String tradeTypeCode = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_ATTR_BIZ", new String[]
                        { "ID", "ID_TYPE", "ATTR_OBJ", "ATTR_CODE" }, "ATTR_VALUE", new String[]
                        { productId, "P", BizCtrlType.DestoryMember, "TradeTypeCode" });
				checkMebBaseInfoRule(ecUserId, importData.getString("SERIAL_NUMBER", ""), BizCtrlType.DestoryMember, tradeTypeCode, productId);

			} 
			memberData.put("SERIAL_NUMBER", importData.getString("SERIAL_NUMBER",""));
			memberData.put("SHORT_CODE", importData.getString("SHORT_CODE",""));
			infos.add(memberData);
		}
		
		//变更/开通  新增
		if (!"DelMeb".equals(operType)) {
			IData param = new DataMap();
			param.put("IS_BATCH", true);
			param.put("PRODUCT_ID", productId);
			param.put("CUST_ID", custId);
			//获取userId
			String ecSerialNumber = data.getString("EC_SERIAL_NUMBER");
			param.put("SERIAL_NUMBER", ecSerialNumber);
			IData userInfo = CSAppCall.callOne("CS.UcaInfoQrySVC.qryUserInfoBySn", param);

			param.put("EC_USER_ID", userInfo.getString("USER_ID"));
			param.put("CHECK_MEMBERLIST", dataset);
			// 复用开通和变更时的导入校验
			IDataset validaResults = CSAppCall.call("SS.EcIntegrationMebChkSVC.checkMemberImsVpmn", param);
			StringBuilder errorMsg = new StringBuilder();
			for (int i = 0, len = validaResults.size(); i < len; i++) {
				IData resulData =  validaResults.getData(i);
				if (DataUtils.isNotEmpty(resulData.getDataset("FAIL_LIST"))) {
					IDataset failList = resulData.getDataset("FAIL_LIST");
					for (int j = 0, size = failList.size(); j < size; j++) {
						IData resultInfo =  failList.getData(j);
						String errorString = resultInfo.getString("IMPORT_ERROR");
						if (errorString.indexOf("`") >= 0) {
							errorString = errorString.substring(errorString.indexOf("`")+1, errorString.lastIndexOf("`")-1);
						}
						errorMsg.append(" 号码【"+resultInfo.getString("SERIAL_NUMBER")+"】， "+errorString+" ");
					}
					CSAppException.apperr(CrmCommException.CRM_COMM_103, errorMsg.toString());
				}
			}
		}
		
		SharedCache.set("IMPORT_MEMBER_INFO"+productId, infos);
		return null;
	}
	
	
	/**
	 ** 短号码校验
	 * @param shortCode
	 * @throws Exception
	 * @Date 2019年11月29日
	 * @author xieqj 
	 */
	private void shortCodeValidateVpn(String shortCode) throws Exception {
		// 短号码校验
		if (StringUtils.isEmpty(shortCode)) {
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "短号码为空，请输入后再验证！");
		} else if (shortCode.indexOf(" ") != -1) {
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "短号码中含有空格，请去掉！");
		} else if (shortCode.length() < 3 || shortCode.length() > 6) {
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "短号长度只能为3~6，请检查!");
		} else if (!shortCode.startsWith("6")) {
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "短号码必须以【6】开头，请检查!");
		} else if (shortCode.startsWith("60")) {
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "短号码不能以【60】开头，请检查！");
		}
		IDataset dataset = ParamInfoQry.getCommparaByCode("BMS", "259", "", "0898");
		if (IDataUtil.isNotEmpty(dataset)) {
			IData commData = dataset.getData(0);
			StringBuilder strBuffer = new StringBuilder();
			strBuffer.append(commData.getString("PARA_CODE23"));
			strBuffer.append(commData.getString("PARA_CODE24"));
			strBuffer.append(commData.getString("PARA_CODE25"));

			String str = strBuffer.toString();
			if (0 <= str.indexOf("|" + shortCode + "|")) {
				CSAppException.apperr(CrmCommException.CRM_COMM_103, "短号码不能是特殊代码，短号为【" + shortCode + "】请检查！");
			}
		}
	}
	
	/**
    *
    ** 调用公共业务规则，校验成员号码
    *
    * */
	private void checkMebBaseInfoRule(String grpUserId, String mebSN, String operType, String tradeTypeCode,
			String ecOfferCode) throws Exception {
		try {
			String svc = "";
			if (BizCtrlType.CreateMember.equals(operType)) {
				svc = "CS.chkGrpMebOrder";
			} else if (BizCtrlType.ChangeMemberDis.equals(operType)) {
				svc = "CS.chkGrpMebChg";
			} else if (BizCtrlType.DestoryMember.equals(operType)) {
				svc = "CS.chkGrpMebDestory";
			}

			IData conParam = new DataMap();
			if ("8001".equals(ecOfferCode)) // 融合V网
			{
				conParam.put("IF_CENTRETYPE", "2");
			}
			conParam.put("CHK_FLAG", "BaseInfo");
			conParam.put("USER_ID", grpUserId);
			conParam.put("SERIAL_NUMBER", mebSN);
			conParam.put("TRADE_TYPE_CODE", tradeTypeCode);
			conParam.put("PRODUCT_ID", ecOfferCode);
			CSAppCall.call(svc, conParam);
		} catch (Exception e) {
			CSAppException.apperr(CrmCommException.CRM_COMM_103, e.getMessage().substring(e.getMessage().indexOf("`")+1, e.getMessage().lastIndexOf("`")));
		}
	}

}
