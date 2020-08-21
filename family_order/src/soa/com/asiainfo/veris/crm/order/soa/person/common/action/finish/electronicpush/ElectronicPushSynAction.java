package com.asiainfo.veris.crm.order.soa.person.common.action.finish.electronicpush;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.ailk.biz.BizEnv;
import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.bizservice.dao.Dao;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.acct.AcctInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustomerInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.sysorg.StaffInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.changeepostinfo.ModifyEPostInfoBean;
import com.itextpdf.text.pdf.codec.Base64.InputStream;

public class ElectronicPushSynAction implements ITradeFinishAction {

	private static Logger logger = Logger
			.getLogger(ElectronicPushSynAction.class);

	public void executeAction(IData mainTrade) throws Exception {
		String inmodeCode = mainTrade.getString("IN_MODE_CODE", "");
		String tradetypeCode = mainTrade.getString("TRADE_TYPE_CODE", "");
		IDataset commparas = CommparaInfoQry.getCommparaInfoBy1To7("CSM", "8725", "ELEPUSH", inmodeCode, tradetypeCode);
		if (IDataUtil.isNotEmpty(commparas)) {
			String serialNumber = mainTrade.getString("SERIAL_NUMBER", "");
			String erarchyCode = mainTrade.getString("EPARCHY_CODE", "0898");
			String acctID = mainTrade.getString("ACCT_ID", "");
			String updatestaffID = mainTrade.getString("TRADE_STAFF_ID", "");
			String updatedepartID = mainTrade.getString("TRADE_DEPART_ID", "");
			String userID = mainTrade.getString("USER_ID", "");

			String commparaFlag = commparas.getData(0).getString("PARA_CODE4", "");

			// 账管接口通用参数
			IData accparam = new DataMap();
			accparam.put("ACCT_TYPE", "0"); // 个人用户设置
			accparam.put("EPARCHY_CODE", erarchyCode); // 手机号码地州编码
			accparam.put("USER_ID", userID);
			accparam.put("ACCT_ID", acctID);
			accparam.put("PARTITION_ID", StrUtil.getPartition4ById(acctID));
			accparam.put("SERIAL_NUMBER", serialNumber);
			accparam.put("NEW_FLAG", "0"); // 是否为新开户

			// 接口参数
			accparam.put("TYPE", "0"); // 月结
			accparam.put("FLAG", "0"); // 新增
			accparam.put("SMS_NUMBER", serialNumber);
			accparam.put("EMAIL_NUMBER", serialNumber + "@139.com");
			accparam.put("PUSH_DATE", ""); // 推送时间
			accparam.put("PUSH_CHANNEL", "2");
			accparam.put("PUSH_FLAG", "2");

			//宽带开户特殊操作
			if ("1".equals(commparaFlag)) {
				if ("600".equals(tradetypeCode)) {
					
					if(!mainTrade.getString("SERIAL_NUMBER", "").contains("KD_"))
					{
						return ;
					}
					
					serialNumber = mainTrade.getString("SERIAL_NUMBER", "").substring(3);
					IData inparam = new DataMap();
					inparam.put("SERIAL_NUMBER", serialNumber);
					IDataset postInfos = ModifyEPostInfoBean.qryEPostInfo(inparam);
					
					//查询是否已经设置
					if(IDataUtil.isNotEmpty(postInfos))
					{
						for(int i=0;i<postInfos.size();i++)
						{
							IData map = postInfos.getData(i);
							String rsrvStr3 = map.getString("RSRV_STR3", "");
							String rsrvStr4 = map.getString("RSRV_STR4", "");
							String postTag = map.getString("POST_TAG", "");
							if("".equals(rsrvStr3) || "0".equals(rsrvStr3))
							{
								rsrvStr3 = "1"; 
								if("".equals(rsrvStr4))
								{
									rsrvStr4 = serialNumber + "@139.com";
								}
								ModifyEPostInfoBean.upBusiCashPost(serialNumber, postTag, rsrvStr3, rsrvStr4);
							}
						}
					}else{
						//由于宽带开户特殊性，需要转成手机号码的数据
						commparaFlag = "";
						IDataset userInfo = UserInfoQry.getUserInfoBySn(serialNumber,"0");
						if(IDataUtil.isEmpty(userInfo))
						{
							return ;
						}
						
						userID = userInfo.getData(0).getString("USER_ID");
						erarchyCode = userInfo.getData(0).getString("EPARCHY_CODE");
						String custId = userInfo.getData(0).getString("CUST_ID");
						IDataset acctset = AcctInfoQry.getAcctInfoByCustId(custId, erarchyCode);
						if (IDataUtil.isNotEmpty(acctset)) {
							acctID = acctset.getData(0).getString("ACCT_ID");
						} else {
							IData payrela = UcaInfoQry.qryDefaultPayRelaByUserId(userID, erarchyCode);
							if (IDataUtil.isEmpty(payrela)) {
								payrela = UcaInfoQry.qryLastPayRelaByUserId(userID, erarchyCode);
								if (IDataUtil.isNotEmpty(payrela)) {
									acctID = payrela.getString("ACCT_ID");
								}
							} else {
								acctID = payrela.getString("ACCT_ID");
							}
						}
						
						//覆盖初始数据
						accparam.put("EPARCHY_CODE", erarchyCode); // 手机号码地州编码
						accparam.put("USER_ID", userID);
						accparam.put("ACCT_ID", acctID);
						accparam.put("PARTITION_ID", StrUtil.getPartition4ById(acctID));
						accparam.put("SERIAL_NUMBER", serialNumber);
						accparam.put("NEW_FLAG", "1"); // 是否为新开户：0新用户，1老用户
						accparam.put("FLAG", "0"); // 新增
						accparam.put("SMS_NUMBER", serialNumber);
						accparam.put("EMAIL_NUMBER", serialNumber + "@139.com");
					}

				}
			} 
			
			//无数据新增操作
			if("".equals(commparaFlag)){
				IData inparam = new DataMap();
				inparam.put("SERIAL_NUMBER", serialNumber);
				IDataset postInfos = ModifyEPostInfoBean.qryEPostInfo(inparam);
				if (IDataUtil.isNotEmpty(postInfos)) {
					ModifyEPostInfoBean.delBusiCashPost(serialNumber, "0");
					ModifyEPostInfoBean.delBusiCashPost(serialNumber, "1");
					ModifyEPostInfoBean.delBusiCashPost(serialNumber, "2");
				}

				// 入表通用参数
				IData tabdata = new DataMap();
				tabdata.put("PARTITION_ID", StrUtil.getPartition4ById(userID));
				tabdata.put("SERIAL_NUMBER", serialNumber);
				tabdata.put("EPARCHY_CODE", erarchyCode);
				tabdata.put("USER_ID", userID);
				tabdata.put("UPDATE_STAFF_ID", updatestaffID);
				tabdata.put("UPDATE_DEPART_ID", updatedepartID);
				tabdata.put("UPDATE_TIME", SysDateMgr.getSysDate());
				tabdata.put("RSRV_STR1", "1");
				tabdata.put("RSRV_STR2", "");
				tabdata.put("RSRV_STR3", "1");
				tabdata.put("RSRV_STR4", serialNumber + "@139.com");

				IDataset results = new DatasetList();
				results = AcctCall.setEPostInfo(accparam);
				if (results.size() > 0 && "0000".equals(results.getData(0).getString("RESULT_CODE"))) 
				{
					ModifyEPostInfoBean.addMonPost(tabdata, "0", "2", serialNumber, serialNumber + "@139.com", "1");
				} else {
					CSAppException.apperr(CrmCommException.CRM_COMM_103, results.getData(0).getString("RESULT_INFO"));
				}

				accparam.put("TYPE", "1"); // 现金

				results = AcctCall.setEPostInfo(accparam);
				if (results.size() > 0 && "0000".equals(results.getData(0).getString("RESULT_CODE"))) 
				{
					ModifyEPostInfoBean.addBusiCashPost(tabdata, "1", "2", serialNumber, serialNumber + "@139.com");
				} else {
					CSAppException.apperr(CrmCommException.CRM_COMM_103, results.getData(0).getString("RESULT_INFO"));
				}

				accparam.put("TYPE", "2"); // 日常

				results = AcctCall.setEPostInfo(accparam);
				if (results.size() > 0 && "0000".equals(results.getData(0).getString("RESULT_CODE"))) {
					ModifyEPostInfoBean.addBusiCashPost(tabdata, "2", "2", serialNumber, serialNumber + "@139.com");
				} else {
					CSAppException.apperr(CrmCommException.CRM_COMM_103, results.getData(0).getString("RESULT_INFO"));
				}
			}
		}
	}

}
