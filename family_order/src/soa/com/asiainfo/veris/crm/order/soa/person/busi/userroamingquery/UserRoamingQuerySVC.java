package com.asiainfo.veris.crm.order.soa.person.busi.userroamingquery;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.Utility;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.UserRoamingIdSeq;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.StaticInfoQry;

public class UserRoamingQuerySVC extends CSBizService
{
	private static final long serialVersionUID = 1L;

    private static final Logger log = Logger.getLogger(UserRoamingQuerySVC.class);

	/**
	 *  用户漫游信息查询
	 * @param input
	 * @return
	 * @throws Exception
	 */
	 public IDataset queryUserRoamingInfo(IData input) throws Exception
	 {
		 IDataset results = new DatasetList();

		 try {
			 String serialNumber = IDataUtil.chkParam(input, "SERIAL_NUMBER");
			 //操作流水号规则,省BOSS的编码规则－3位省代码+14位组包时间YYYYMMDDHH24MMSS+6位流水号（定长），序号从000001开始，增量步长为1。
			 String seq = Dao.getSequence(UserRoamingIdSeq.class);
			 
	         if (StringUtils.length(seq) > 6)
	         {
	        	 seq = seq.substring(seq.length() - 6);
	         }
	         else if (StringUtils.length(seq) < 6)
	         {
	        	 seq = "000000".substring(0, 6 - StringUtils.length(seq)) + seq;
	         }
	
	         //省编号
			 String provCode = StaticInfoQry.qryProvCode(getVisit().getProvinceCode());
	         if (StringUtils.isBlank(provCode))
	         {
	            CSAppException.apperr(CrmCommException.CRM_COMM_310);
	         }
	
	         String msgTransactionID = provCode + SysDateMgr.getSysDate("yyyyMMddHHmmss") + seq;
		        
			 //开始拼接报文给IBOSS
			 IData param = new DataMap();
			 param.put("KIND_ID", "BIP3A320_T3000323_0_0");
			 param.put("MSG_TRANSACTION_ID", msgTransactionID);
			 
			 param.put("MSISDN", serialNumber);
			 param.put("PROV_CODE", provCode);
			 param.put("OPR_TIME", SysDateMgr.getSysDate("yyyyMMddHHmmss"));
			 
			 //调用IBOSS网对网系统
			 results = IBossCall.dealInvokeUrl("BIP3A320_T3000323_0_0", "IBOSS6", param);
			 
			 if (log.isDebugEnabled()) {
				 log.debug("用户漫游信息返回结果：" + results.toString());
			 }
			 
			 recordInfos(results);
		} catch (Exception e) {
			Utility.error(e);
		}
		 
		 return results;
	 }

	 /**
	    *  用户漫游信息查询结果记录到数据库中
	    *  IBOSS返回多少条漫游记录，数据库中就保存多少条数据，主键需要创建序列号。 
	    *  其中参数按照 参数个数，参数名称：参数值|…… 的格式存储
	  * @param input
	  * @return
	  * @throws Exception
	  * 
	  * */
	private void recordInfos(IDataset results) throws Exception {
		IDataset params = new DatasetList();
		IData param = new DataMap();
		
		//开始解析IBOSS返回结果
		IData result = results.getData(0);
		if (null != result) {
			IData userRoamingInfoQryRsp = result.getDataset("USER_ROAMING_INFO_QRY_RSP").getData(0);
			if (null != userRoamingInfoQryRsp) {
				//流水号、返回结果码和描述
				param.put("MSG_TRANSACTION_ID", IDataUtil.chkParamNoStr(userRoamingInfoQryRsp ,"MSG_TRANSACTION_ID"));
				param.put("CFM_RESULT_CODE", IDataUtil.chkParamNoStr(userRoamingInfoQryRsp ,"CFM_RESULT_CODE"));
				param.put("CFM_RESULT_CODE_DESC", IDataUtil.chkParamNoStr(userRoamingInfoQryRsp ,"CFM_RESULT_CODE_DESC"));
				
				//查询后的应答信息解析
				IDataset userRoamingInfos = userRoamingInfoQryRsp.getDataset("USER_ROAMING_INFO");
				if (null != userRoamingInfos) {
					IData userRoamingInfo = userRoamingInfos.getData(0);
					//手机号码、省代码和查询的时间
					param.put("SERIAL_NUMBER", IDataUtil.chkParamNoStr(userRoamingInfo ,"MSISDN"));
					param.put("PROVINCE_ID", IDataUtil.chkParamNoStr(userRoamingInfo ,"PROV_CODE"));
					param.put("OPER_TIME", IDataUtil.chkParamNoStr(userRoamingInfo ,"OPR_TIMSI"));
			        
					//与业务相关的扩展信息解析
					//参数按照 参数个数，参数名称：参数值|…… 的格式拼装到PARAM_INFO字段中
					IDataset servParamInfos = userRoamingInfo.getDataset("SERV_PARAM_INFO");
					if (null != servParamInfos && servParamInfos.size() > 0) {
						IData servParamInfo = servParamInfos.getData(0);
						StringBuffer sb = new StringBuffer();
						String paraNum = IDataUtil.chkParamNoStr(servParamInfo ,"PARA_NUM");
						IDataset paraInfos = servParamInfo.getDataset("PARA_INFO");
						//如果没有参数信息，不需要再后面拼装“，”
						if (null != paraInfos && paraInfos.size() > 0) {
							sb.append(paraNum).append(",");
							for(int i = 0 ; i<paraInfos.size(); i++){
								IData paraInfo = paraInfos.getData(i);
								String paraName = IDataUtil.chkParamNoStr(paraInfo ,"PARA_NAME");
								String paraValue = IDataUtil.chkParamNoStr(paraInfo ,"PARA_VALUE");
								//如果参数是最后一个，不需要再后面拼装 | 
								if (i == (paraInfos.size() - 1)) {
									sb.append(paraName).append(":").append(paraValue);
								} else {
									sb.append(paraName).append(":").append(paraValue).append("|");
								}
							}
						} else {
							sb.append(paraNum);
						}
						param.put("PARAM_INFO", sb.toString());
					}
					
					//用户漫游查询应答信息的解析
					IDataset roamingInfos = userRoamingInfo.getDataset("ROAMING_INFO");
					//如果有查询为空，那么用户应该没有漫游，数据库中就插入空的
					if (null != roamingInfos && roamingInfos.size() > 0) {
						for(int i = 0 ; i<roamingInfos.size(); i++){
							IData roamingInfo = roamingInfos.getData(i);
							IDataset userRoamingCountry = roamingInfo.getDataset("USER_ROAMING_COUNTRY");
							if (null != userRoamingCountry && userRoamingCountry.size() > 0) {
								for(int j = 0 ; j<userRoamingCountry.size(); j++){
									//由于漫游信息是很多条，所以先要把一样的参数复制进来，再设置漫游信息，否则不可以批量新增
									IData newParam = new DataMap();
									newParam.putAll(param);
									
									newParam.put("USER_ROAMING_DATE", IDataUtil.chkParamNoStr(roamingInfo ,"USER_ROAMING_DATE"));
									newParam.put("USER_ROAMING_COUNTRY", IDataUtil.chkParamNoStr(userRoamingCountry.getData(j) ,"COUNTRY_NAME"));
									newParam.put("ID", Dao.getSequence(UserRoamingIdSeq.class));
									params.add(newParam);
								}
							}
						}
					} else {
						param.put("ID", Dao.getSequence(UserRoamingIdSeq.class));
						params.add(param);
					}
					Dao.insert("TF_B_USER_ROAMING", params);
				}
			}
		}
		
	}
	 
}
