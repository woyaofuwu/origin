
package com.asiainfo.veris.crm.order.soa.person.busi.platlastquery;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import com.ailk.biz.BizEnv;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQry;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;


/**
 * 查询平台业务最后办理记录
 * 
 * @author xiekl
 */
public class QueryPlatServiceLastSVC extends CSBizService {

	/**
	 * trade_id 查询用户办理平台业务所有信息
	 * 
	 * @param
	 * @return
	 * @throws Exception
	 */
	public IDataset qryUserTransPlatSvcs(IData inputParam) throws Exception {

		String serialNumber = inputParam.getString("SERIAL_NUMBER");
		if (serialNumber == null || "".equals(serialNumber)) {
			CSAppException.apperr(CrmCommException.CRM_COMM_172);
		}

		// 第一步，查询用户信息
		IData users = UcaInfoQry.qryUserInfoBySn(inputParam.getString("SERIAL_NUMBER"));
		if (IDataUtil.isEmpty(users)) {
			CSAppException.apperr(CrmCommException.CRM_COMM_906);
		}

		// 第二步，查询客户信息
		String userId = users.getString("USER_ID", "");
		String custId = users.getString("CUST_ID", "");
		String spCode = inputParam.getString("SP_CODE");
		
		
		
		String bizTypeCode ="";
		if (inputParam.getString("BIZ_TYPE_CODE").length()>0) {
			String[] platStr = inputParam.getString("BIZ_TYPE_CODE").split("\\|");
			bizTypeCode=platStr[5];
		}
		
		//String bizTypeCode = inputParam.getString("BIZ_TYPE_CODE");
		String operCode = inputParam.getString("OPER_CODE");
		String bizStateCode = inputParam.getString("BIZ_STATE_CODE");
		String bizCode = inputParam.getString("BIZ_CODE");

		// 查询出结果集与产商品结构循环取结果集
//          IDataset userPlatSvcList = UserPlatSvcInfoQry.qryUserPlatSvcs(userId, bizTypeCode, operCode, spCode, bizCode, bizStateCode);
//		return Dao.qryByCodeParser("TF_F_USER_PLATSVC_TRACE", sqlRef, param);
		IData queryParam = new DataMap();
		queryParam.put("USER_ID", users.getString("USER_ID", ""));
		IDataset userPlatSvcList = Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_BY_USER_ID_QUERY", queryParam);
		IDataset resultList = new DatasetList();
		for (int i = 0; i < userPlatSvcList.size(); i++) {
			IData userPlatSvc = userPlatSvcList.getData(i);
			IDataset upcDatas = new DatasetList();
			try {
				upcDatas = UpcCall.querySpServiceAndProdByCond(spCode, bizCode, bizTypeCode,
						userPlatSvc.getString("SERVICE_ID", ""));
			} catch (Exception e) {

			}

			if (null != upcDatas && upcDatas.size() > 0) {
				IData upcData = upcDatas.getData(0);
				// Z.BIZ_NAME,P.SERVICE_NAME,
				// Z.BILL_TYPE,Z.PRICE,M.SP_NAME,P.SP_CODE,P.BIZ_CODE,P.BIZ_TYPE_CODE," + "
				// Z.SERV_MODE
				userPlatSvc.put("BIZ_NAME", upcData.getString("BIZ_NAME"));
				userPlatSvc.put("SERVICE_NAME", upcData.getString("SERVICE_NAME"));
				userPlatSvc.put("BILL_TYPE", upcData.getString("BILL_TYPE"));
				userPlatSvc.put("PRICE", upcData.getString("PRICE"));
				userPlatSvc.put("SP_NAME", upcData.getString("SP_NAME"));
				userPlatSvc.put("SP_CODE", upcData.getString("SP_CODE"));
				userPlatSvc.put("BIZ_CODE", upcData.getString("BIZ_CODE"));
				userPlatSvc.put("BIZ_TYPE_CODE", upcData.getString("BIZ_TYPE_CODE"));
				userPlatSvc.put("SERV_MODE", upcData.getString("SERV_MODE"));

				String serviceId = userPlatSvc.getString("SERVICE_ID", "");
				String bizType = userPlatSvc.getString("BIZ_TYPE_CODE", "");
				String relaInstId = userPlatSvc.getString("RELA_INST_ID", "");

				// 处理无线音乐会员
				if (serviceId.equals("98001901") && !bizType.equals("")) {
					IData attr = UserAttrInfoQry.getUserAttrByRelaInstIdAndAttrCode(userId, relaInstId, "302", null);
					if ("2".equals(attr.getString("ATTR_VALUE", ""))) {
						userPlatSvc.put("PRICE", "5000");
						userPlatSvc.put("BILL_TYPE", "2");
						userPlatSvc.put("BIZ_NAME", "无线音乐平台高级会员");
						userPlatSvc.put("BIZ_TYPE", "无线音乐平台高级会员");
					}

					if ("1".equals(attr.getString("ATTR_VALUE", ""))) {
						userPlatSvc.put("BIZ_NAME", "无线音乐平台普通会员");
						userPlatSvc.put("BIZ_TYPE", "无线音乐平台普通会员");
						userPlatSvc.put("PRICE", "0");
						userPlatSvc.put("BILL_TYPE", "0");
					}
				}

				// 处理动感短信会员业务
				if (bizType.equals("DX")) {
					IData attr = UserAttrInfoQry.getUserAttrByRelaInstIdAndAttrCode(userId, relaInstId, "8899", null);
					if ("XSMT".equals(attr.getString("ATTR_VALUE", ""))) {
						userPlatSvc.put("PRICE", "0");
						userPlatSvc.put("BILL_TYPE", "0");
						userPlatSvc.put("BIZ_NAME", "动感短信免费鉴赏会员业务");
						userPlatSvc.put("BIZ_TYPE", "动感短信免费鉴赏会员业务");
					}

					if ("XSMTC3".equals(attr.getString("ATTR_VALUE", ""))) {
						userPlatSvc.put("BIZ_NAME", "动感短信普通会员业务");
						userPlatSvc.put("BIZ_TYPE", "动感短信普通会员业务");
						userPlatSvc.put("PRICE", "3000");
						userPlatSvc.put("BILL_TYPE", "2");
					}

					if ("XSMTC5".equals(attr.getString("ATTR_VALUE", ""))) {
						userPlatSvc.put("BIZ_NAME", "动感短信高级会员业务");
						userPlatSvc.put("BIZ_TYPE", "动感短信高级会员业务");
						userPlatSvc.put("PRICE", "5000");
						userPlatSvc.put("BILL_TYPE", "2");
					}
				}
				resultList.add(userPlatSvc);
			}
		}
		List<Object> list = resultList;
		JSONArray json = JSONArray.fromObject(list);
		IDataset ds = DatasetList.fromJSONArray(json);
		return ds;

	}

	
	

	/**
	 * 发送平台时间 等相关数据 关联在查询tf_bh_trade tf_b_trade_platsvc
	 * 
	 * @param inputParam
	 * @return
	 * @throws Exception
	 */
	public IDataset qryUserLastTradeSendPlatSvcs(IData inputParam) throws Exception {

		IData queryTradeParam = new DataMap();
		queryTradeParam.put("SERIAL_NUMBER", inputParam.getString("SERIAL_NUMBER"));
		queryTradeParam.put("SERVICE_ID", inputParam.getString("SERVICE_ID"));
		IDataset platBTrade = null;
		IDataset returnData = new DatasetList();
		// 不选路由
		platBTrade = Dao.qryByCode("TF_B_TRADE_TRADE_PLATSVC", "SEL_BY_USERID", queryTradeParam);// 工单表
		if (null == platBTrade || platBTrade.size() == 0) {
			platBTrade = Dao.qryByCode("TF_BH_TRADE_TRADE_PLATSVC", "SEL_BY_USERID", queryTradeParam);// 工单历史表
		}
		
		
		try {
			
			if (null != platBTrade && platBTrade.size() > 0) {// 数指请求
				IData userTrade = platBTrade.getData(0);
				IData queryParam = new DataMap();
				queryParam.put("SUBSCRIBE_ID", userTrade.getString("TRADE_ID"));// 业务流水关联查询
				queryParam.put("KIND_ID", "UserTradeTradePlatsvc_BBOSS_0_0");
				System.out.println("TF_B_IBPLAT_SYN---SelectdataStart ");
//				IDataset idataset= CSAppCall.callfukai("QueryIBPlatReceiver", queryParam, false).getData();
				String httpServiceAddr = BizEnv.getEnvString("crm.DataComUrl");//获取数据地址
				IData idata = getDataComAppIBPlat(queryParam,httpServiceAddr);
//				IDataset idataset = IBossCall.callHttpIBOSS20("IBOSS", queryParam);
				System.out.println("TF_B_IBPLAT_SYN---Selectdatas :" + idata.toString());
				if (null != idata ) {
					if (Integer.parseInt(idata.getString("X_COUNT"))>1){
						for (int i=0;i<Integer.parseInt(idata.getString("X_COUNT"));i++){
							IData responseData = new DataMap();
							responseData.put("PLAT_SEND_TIME", idata.getDataset("UPDATE_TIME").get(i).toString());// 发送平台时间
							responseData.put("REMOTE_RSLT_DESC1",  idata.getDataset("REMOTE_RSLT_DESC1").get(i).toString());// 远程一级应答码描述（平台响应结果说明REMOTE_RSLT_DESC1）
							responseData.put("REMOTE_RSLT_CODE1", idata.getDataset("REMOTE_RSLT_CODE1").get(i).toString());// 远程一级应答码（发送平台状态REMOTE_RSLT_CODE1）
							responseData.put("PLAT_SYN_ID", idata.getDataset("PLAT_SYN_ID").get(i).toString());
							responseData.put("BUSI_SIGN", idata.getDataset("BUSI_SIGN").get(i).toString());
							responseData.put("SUBSCRIBE_ID", idata.getDataset("SUBSCRIBE_ID").get(i).toString());
							responseData.putAll(platBTrade.getData(0));
							returnData.add(responseData);
						}
					}else if (Integer.parseInt(idata.getString("X_COUNT"))>0){

						IData responseData = new DataMap();
						responseData.put("PLAT_SEND_TIME", idata.getString("UPDATE_TIME").toString());// 发送平台时间
						responseData.put("REMOTE_RSLT_DESC1",  idata.getString("REMOTE_RSLT_DESC1").toString());// 远程一级应答码描述（平台响应结果说明REMOTE_RSLT_DESC1）
						responseData.put("REMOTE_RSLT_CODE1", idata.getString("REMOTE_RSLT_CODE1").toString());// 远程一级应答码（发送平台状态REMOTE_RSLT_CODE1）
						responseData.put("PLAT_SYN_ID", idata.getString("PLAT_SYN_ID").toString());
						responseData.put("BUSI_SIGN", idata.getString("BUSI_SIGN").toString());
						responseData.put("SUBSCRIBE_ID", idata.getString("SUBSCRIBE_ID").toString());
						responseData.putAll(platBTrade.getData(0));
						returnData.add(responseData);
					} else {
						userTrade.put("PLAT_SEND_TIME", "");// 发送平台时间
						userTrade.put("REMOTE_RSLT_DESC1", "");// 远程一级应答码描述（平台响应结果说明REMOTE_RSLT_DESC1）
						userTrade.put("REMOTE_RSLT_CODE1", "");// 远程一级应答码（发送平台状态REMOTE_RSLT_CODE1）
						userTrade.put("PLAT_SYN_ID", "");
						userTrade.put("BUSI_SIGN", "");
						userTrade.put("SUBSCRIBE_ID", "");
						returnData.add(userTrade);
					}


	
				} else {
					userTrade.put("PLAT_SEND_TIME", "");// 发送平台时间
					userTrade.put("REMOTE_RSLT_DESC1", "");// 远程一级应答码描述（平台响应结果说明REMOTE_RSLT_DESC1）
					userTrade.put("REMOTE_RSLT_CODE1", "");// 远程一级应答码（发送平台状态REMOTE_RSLT_CODE1）
					userTrade.put("PLAT_SYN_ID", "");
					userTrade.put("BUSI_SIGN", "");
					userTrade.put("SUBSCRIBE_ID", "");
					returnData.add(userTrade);
	
				}

			}
			
		} catch (Exception e) {
			  if (null!=platBTrade&&platBTrade.size()>0) {//数指请求
				  IData userTrade = platBTrade.getData(0);
					userTrade.put("PLAT_SEND_TIME", "");// 发送平台时间
					userTrade.put("REMOTE_RSLT_DESC1", "");// 远程一级应答码描述（平台响应结果说明REMOTE_RSLT_DESC1）
					userTrade.put("REMOTE_RSLT_CODE1", "");// 远程一级应答码（发送平台状态REMOTE_RSLT_CODE1）
					userTrade.put("PLAT_SYN_ID", "");
					userTrade.put("BUSI_SIGN", "");
					userTrade.put("SUBSCRIBE_ID", "");
				  returnData.add(userTrade);
			  }
		}
	

		return returnData;
	}
	
	
	
	
	
	
	
	/**
	 * 重新发送报文到数指
	 * ，重新发送用户的该条平台业务的当前状态至数指平台 并更新状态 实现重跑数据指令
	 * 
	 * @param inputParam
	 * @return
	 * @throws Exception
	 */
	public IData anewSendPlatsDataForDataComSvcs(IData inputParam) throws Exception {

		IData queryAnewParam = new DataMap();
		queryAnewParam.put("BUSI_SIGN", inputParam.getString("BUSI_SIGN"));
		queryAnewParam.put("PLAT_SYN_ID", inputParam.getString("PLAT_SYN_ID"));
		queryAnewParam.put("SUBSCRIBE_ID", inputParam.getString("SUBSCRIBE_ID"));
		queryAnewParam.put("SERVICE_ID", inputParam.getString("SERVICE_ID"));
		IData returnData = new DataMap();

		try {
				String httpServiceAddr = BizEnv.getEnvString("crm.AnewDataComUrl");//获取重跑数据地址
				IData responseData = getDataComAppIBPlat(queryAnewParam,httpServiceAddr);
				System.out.println("TF_B_IBPLAT_SYN---Selectdatas :" + responseData.toString());
				if (null != responseData ) {
					returnData.put("IS_SUCCESS", responseData.getString("X_RESULT_ISSUCCESS"));
	
				} else {
					returnData.put("IS_SUCCESS", "-1");

				}

			
			
		} catch (Exception e) {
			returnData.put("IS_SUCCESS", "-1");
			return returnData;
		}
	

		return returnData;
	}
	
	
	
	/***
	 * 数指调用
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static IData getDataComAppIBPlat(IData data,String httpServiceAddr) throws Exception {
		HttpClient client = new DefaultHttpClient();

		HttpParams hp = client.getParams();
		hp.setParameter("Content-Encoding", "gbk");
		HttpConnectionParams.setConnectionTimeout(hp, 3000);
		HttpConnectionParams.setSoTimeout(hp, 3000);

		// client.getParams().setParameter("Content-Encoding", "GBK");
		HttpResponse response = null;
		HttpPost uriRequest = null;
		String webPageContent = null;
		try {

			// 地址配置为服务开通接收地址 数据接收

//			String httpServiceAddr = "http://10.200.130.80:5577/QueryIBPlatInfoReceiver";
			uriRequest = new HttpPost(httpServiceAddr);
			uriRequest.setHeader("Content-type", "text/xml; charset=gbk");
			uriRequest.setHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows 2000)");

			// 注意toBudeString方法是否可用

			String datastr = toWadeString(data);
//	      logger.debug(datastr);
			InputStreamEntity reqEntity = new InputStreamEntity(new ByteArrayInputStream(datastr.getBytes("gbk")),
					-1L);

			reqEntity.setContentType("binary/octet-stream");
			reqEntity.setChunked(true);
			uriRequest.setEntity(reqEntity);
			System.out.println("uriRequest:" + uriRequest);
			response = client.execute(uriRequest);
		} catch (IOException e) {
			System.out.println("调用服务开通接口失败，错误信息：" + e.toString());
			throw new Exception("调用服务开通接口失败，错误信息：" + e.toString());
		} finally {
		}

		HttpEntity entity = response.getEntity();
		BufferedReader in = null;
		if (entity != null) {
			try {
				in = new BufferedReader(new InputStreamReader(entity.getContent(), "gbk"));
				StringBuffer buffer = new StringBuffer();
				String line = null;
				while ((line = in.readLine()) != null) {
					buffer.append(line);
				}
				webPageContent = buffer.toString();
				System.out.println(webPageContent);

			} catch (IOException e) {
				System.out.println("调用服务开通接口失败，错误信息：" + e.toString());
				throw new Exception("调用服务开通接口失败，错误信息：" + e.toString());
			}
		}

//		Map map = new DataMap(strToList(webPageContent));
//		Map map = new DataMap(strToList(webPageContent).get(0).toString());
		IDataset iDataset=new DatasetList();
		iDataset.addAll(strToList(webPageContent));
//		IData out = new DataMap();
//		out.putAll(map);
		System.out.println("" + iDataset);
		return iDataset.getData(0);
	}

	public static String toWadeString(IData data) {
		StringBuffer str = new StringBuffer();
		str.append("{");
		Iterator it = data.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entity = (Map.Entry) it.next();
			Object key = entity.getKey();
			Object value = entity.getValue();
			str.append(key + "=");
			if (value == null) {
				str.append("[\"\"]");
			} else if ((value instanceof JSONObject)) {
				IData object = new DataMap();
				object.putAll((Map) value);
				str.append(toWadeString(object));
			} else if ((value instanceof JSONArray)) {
				IDataset object = new DatasetList();
				object.addAll((List) value);
				str.append(toWadeString(object));
			} else if ((value instanceof Map)) {
				str.append(toWadeString(((IData) value)));
			} else if ((value instanceof List)) {
				str.append(toWadeString(((IDataset) value)));
			} else {
				str.append("[\"" + value + "\"]");
			}
			if (it.hasNext()) {
				str.append(", ");
			}
		}
		str.append("}");
		return str.toString();
	}

	public static String toWadeString(List datas) {
		StringBuffer str = new StringBuffer();
		str.append("[");

		if (datas != null && datas.size() > 0) {
			for (int i = 0; i < datas.size(); i++) {
				Object value = datas.get(i);
				if (value == null) {
					str.append("\"\"");
				} else if ((value instanceof JSONObject)) {
					IData object = new DataMap();
					object.putAll((Map) value);
					str.append(toWadeString(object));
				} else if ((value instanceof JSONArray)) {
					IDataset object = new DatasetList();
					object.addAll((List) value);
					str.append(toWadeString(object));
				} else if ((value instanceof Map)) {
					str.append(toWadeString((IData) value));
				} else if ((value instanceof List)) {
					str.append(toWadeString((IDataset) value));
				} else {
					str.append("\"" + value + "\"");
				}
				if (i != datas.size() - 1) {
					str.append(", ");
				}
			}
		}
		str.append("]");
		return str.toString();
	}

	public static List strToList(String value) throws Exception {
		if (value == null) {
			return null;
		}
		String orgstr = value;

		Pattern pattern = Pattern.compile("([\\[|\\{]+\")");
		Matcher matcher = pattern.matcher(value);
		boolean iswadestr = (!matcher.find()) || (!value.startsWith(matcher.group()));

		StringBuffer str = new StringBuffer();
		String regstr = iswadestr ? "(\\r)|(\\n)|(\\$)|(\\\\)|(\", \")|([\\[(, )]?\"[\\](, )]?)"
				: "(\\r)|(\\n)|(\\$)|(\\\\)|([\\{,]\"\\d*\\w*[:]?\\w*\":)";
		pattern = Pattern.compile(regstr);
		matcher = pattern.matcher(value);
		while (matcher.find()) {
			String group = matcher.group();
			if ("\r".equals(group)) {
				matcher.appendReplacement(str, "!~5~!");
			} else if ("\n".equals(group)) {
				matcher.appendReplacement(str, "!~6~!");
			} else if ("$".equals(group)) {
				matcher.appendReplacement(str, "!~7~!");
			} else if ("\"".equals(group)) {
				matcher.appendReplacement(str, "!~8~!");
			} else if ("\\".equals(group)) {
				matcher.appendReplacement(str, "!~9~!");
			} else if ((!iswadestr) && ((group.startsWith("{\"")) || (group.startsWith(",\"")))
					&& (group.endsWith("\":"))) {
				matcher.appendReplacement(str, group.replaceAll("\"", "!~a~!"));
			}
		}
		matcher.appendTail(str);

		value = str.toString();
		str = new StringBuffer();
		pattern = Pattern.compile(iswadestr ? "(\".*?\"[\\],])" : "(\".*?\"[\\]\\},])");
		matcher = pattern.matcher(value);
		while (matcher.find()) {
			String group = matcher.group();
			String prefix = group.substring(0, 1);
			String suffix = group.substring(group.length() - 2);
			group = group.substring(1, group.length() - 2);

			StringBuffer substr = new StringBuffer();
			Pattern subpattern = Pattern.compile("(\\{)|(\\[)|(\\])|(,)|(\")");
			Matcher submatcher = subpattern.matcher(group);
			while (submatcher.find()) {
				String subgroup = submatcher.group();
				if ("{".equals(subgroup)) {
					submatcher.appendReplacement(substr, "!~1~!");
				} else if ("[".equals(subgroup)) {
					submatcher.appendReplacement(substr, "!~2~!");
				} else if ("]".equals(subgroup)) {
					submatcher.appendReplacement(substr, "!~3~!");
				} else if (",".equals(subgroup)) {
					submatcher.appendReplacement(substr, "!~4~!");
				} else if ("\"".equals(subgroup)) {
					submatcher.appendReplacement(substr, "!~8~!");
				}
			}
			submatcher.appendTail(substr);
			matcher.appendReplacement(str, prefix + substr + suffix);
		}
		matcher.appendTail(str);
		if (iswadestr) {
			value = str.toString();
			str = new StringBuffer();
			pattern = Pattern.compile(
					"(=?[\\{\\[][\\{\\}\\[\\]]*(, [\"]?)?[\\{\\}\\[]*)|([\\}\\]]*(, [\"]?)[\\{\\[]*)|(\", \")");
			matcher = pattern.matcher(value);
			while (matcher.find()) {
				String group = matcher.group();
				if (group.startsWith("=")) {
					group = "\":" + group.substring(1);
				}
				if (group.endsWith("{")) {
					group = group + "\"";
				} else if (group.endsWith(" ")) {
					group = group + "\"";
				}
				group = group.replaceFirst(" ", "");
				matcher.appendReplacement(str, group);
			}
			matcher.appendTail(str);

			value = str.toString();
			str = new StringBuffer();
			pattern = Pattern.compile("(:\\[\".*?\"\\])");
			matcher = pattern.matcher(value);
			while (matcher.find()) {
				String group = matcher.group();
				if (!Pattern.compile("(\",\")|(\",\\[)").matcher(group).find()) {
					matcher.appendReplacement(str, group.substring(0, 1) + group.substring(2, group.length() - 1));
				}
			}
			matcher.appendTail(str);
		}
		value = str.toString();
		str = new StringBuffer();
		pattern = Pattern.compile("(!~1~!)|(!~2~!)|(!~3~!)|(!~4~!)|(!~5~!)|(!~6~!)|(!~7~!)|(!~8~!)|(!~9~!)|(!~a~!)");
		matcher = pattern.matcher(value);
		while (matcher.find()) {
			String group = matcher.group();
			if ("!~1~!".equals(group)) {
				matcher.appendReplacement(str, "{");
			} else if ("!~2~!".equals(group)) {
				matcher.appendReplacement(str, "[");
			} else if ("!~3~!".equals(group)) {
				matcher.appendReplacement(str, "]");
			} else if ("!~4~!".equals(group)) {
				matcher.appendReplacement(str, ",");
			} else if ("!~5~!".equals(group)) {
				matcher.appendReplacement(str, "\\\\r");
			} else if ("!~6~!".equals(group)) {
				matcher.appendReplacement(str, "\\\\n");
			} else if ("!~7~!".equals(group)) {
				matcher.appendReplacement(str, "\\$");
			} else if ("!~8~!".equals(group)) {
				matcher.appendReplacement(str, "\\\\\"");
			} else if ("!~9~!".equals(group)) {
				matcher.appendReplacement(str, "\\\\\\\\");
			} else if ((!iswadestr) && ("!~a~!".equals(group))) {
				matcher.appendReplacement(str, "\"");
			}
		}
		matcher.appendTail(str);
		value = str.toString();
		if ((!value.startsWith("[")) || (!value.endsWith("]"))) {
			value = "[" + value + "]";
		}
		return (List) JSONSerializer.toJSON(value);
	}
}
