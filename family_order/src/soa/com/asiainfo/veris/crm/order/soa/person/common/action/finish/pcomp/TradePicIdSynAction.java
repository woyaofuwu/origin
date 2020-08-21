package com.asiainfo.veris.crm.order.soa.person.common.action.finish.pcomp;


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
import com.ailk.bizservice.dao.Dao;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.sysorg.StaffInfoQry;
import com.itextpdf.text.pdf.codec.Base64.InputStream;

public class TradePicIdSynAction implements ITradeFinishAction{

	private static Logger logger = Logger.getLogger(TradePicIdSynAction.class);
	/**
     * 人像比对受理单编号与照片编号同步接口调用
     * @param cycle
     * @throws Exception
     */
	public void executeAction (IData mainTrade) throws Exception
	{
		String tradeId = mainTrade.getString("TRADE_ID");
		String userId = mainTrade.getString("USER_ID");
		String custId = mainTrade.getString("CUST_ID");
		String tradeTypeCode = mainTrade.getString("TRADE_TYPE_CODE");
		printLog("TRADE_ID = ",tradeId);
		
		IData picinfo = getPicId(tradeId,userId,custId,tradeTypeCode);
		printLog("picinfo = ",picinfo.toString());
		if(null == picinfo || picinfo.isEmpty()){
			return;
		}
		// 调用接口
		IData inParam = new DataMap();
		if("100".equals(tradeTypeCode))
		{//过户first_pic_id表示原户主，second_pic_id表示新户主或经办人
			inParam.put("first_pic_id", picinfo.get("FORM_PIC_ID"));
			if("0".equals(picinfo.getString("PIC_TAG"))){
				inParam.put("second_pic_id", picinfo.get("PIC_ID"));
			}else if("0".equals(picinfo.getString("AGENT_PIC_TAG"))){
				inParam.put("second_pic_id", picinfo.get("AGENT_PIC_ID"));
			}
		}else
		{//开户first_pic_id表示新户主或经办人
			if("0".equals(picinfo.getString("PIC_TAG"))){
				inParam.put("first_pic_id", picinfo.get("PIC_ID"));
			}else if("0".equals(picinfo.getString("AGENT_PIC_TAG"))){
				inParam.put("first_pic_id", picinfo.get("AGENT_PIC_ID"));
			}
		}
		inParam.put("trade_id", tradeId);
		inParam.put("op_code", tradeTypeCode);
		inParam.put("phone", mainTrade.getString("SERIAL_NUMBER"));
		inParam.put("work_no", mainTrade.getString("TRADE_STAFF_ID"));
		inParam.put("org_info", mainTrade.getString("TRADE_DEPART_ID"));
		IDataset ds = StaffInfoQry.qryStaffInfoByStaffId(mainTrade.getString("TRADE_STAFF_ID"));
		if (ds != null && ds.size() == 1) {
			inParam.put("work_name", ds.getData(0).getString("STAFF_ID"));
		}
		IData param = new DataMap();
		param.put("DEPART_ID", mainTrade.getString("TRADE_DEPART_ID"));
		ds = Dao.qryByCode("TD_M_DEPART", "SEL_ALL_BY_PK", param);
		if (ds != null && ds.size() == 1) {
			inParam.put("org_name", ds.getData(0).getString("DEPART_ID"));
		}
		inParam.put("op_time", mainTrade.getString("ACCEPT_DATE"));

		JSONObject jSONObject = null;
		jSONObject = JSONObject.fromObject(inParam);

		String   contentJson = jSONObject.toString();
		IData ibossData = new DataMap();
		ibossData.put("buffer", contentJson);

		try {
			//IDataset callSyn = SynCall.synPicTrade(ibossData);
			String strResult=sendAutoAudit(contentJson);
			printLog("SynCall result = ",strResult);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	/**
     * 获取照片ID
     * @param btd
     * @throws Exception
     */
	private IData getPicId (String tradeId,String userId,String custId,String tradeTypeCode) throws Exception
	{
		IData pictada = new DataMap();
		IData param =new DataMap();
		
		param.put("TRADE_ID", tradeId);
		param.put("PIC_ID_TAG", "PIC_ID");
    	IDataset pictaginfo = Dao.qryByCode("TF_B_TRADE_OTHER", "SEL_BY_USERID_RSRVVALUE", param, Route.getJourDbDefault());
    	
    	if(null == pictaginfo || pictaginfo.isEmpty() ){
    		return new DataMap();
    	}
    	
    	String picid = pictaginfo.getData(0).getString("RSRV_STR1","");
    	if(StringUtils.isNotBlank(picid))
    	{//照片ID存在
    		pictada.put("PIC_ID", picid);
    		pictada.put("PIC_TAG", "0");
    	}
    	else
    	{
    		pictada.put("PIC_TAG", "1");
    	}
    	String agentpicid = pictaginfo.getData(0).getString("RSRV_STR3","");
    	if(StringUtils.isNotBlank(agentpicid))
    	{//经办人照片ID存在
    		pictada.put("AGENT_PIC_ID", agentpicid);
    		pictada.put("AGENT_PIC_TAG", "0");
    	}else{
    		pictada.put("AGENT_PIC_TAG", "1");
    	}
    	if("100".equals(tradeTypeCode)){
    		String formpicid = pictaginfo.getData(0).getString("RSRV_STR2","");
    		if(StringUtils.isNotBlank(formpicid))
    		{//过户的原客户照片ID存在
    			pictada.put("FORM_PIC_ID", formpicid);
    			pictada.put("FORM_PIC_TAG", "0");
    		}else
    		{
    			pictada.put("FORM_PIC_ID", "1");
    		}
    	}
		return pictada;
	}
	
	/**
     * 日志打印
     * @param name,value
     * @throws Exception
     */
	public void printLog(String name ,String value)
	{
		if(logger.isDebugEnabled()){
			logger.debug("<<<<<<<<<<<<<<<<<<<<<<<<TradePicIdSynAction "+name+value+"<<<<<<<<<<<<<<<<<<<<<<<<");
		}
		
	}
	
	/**
	 * 1.2受理单编号与照片编号同步接口
	 * @param saveBillRequ
	 */
	private String sendAutoAudit(String str){
		OutputStreamWriter out = null;
		URL httpurl = null;
		HttpURLConnection httpConn=null;
		boolean flag = false;
		try{
	       String url = BizEnv.getEnvString("crm.pic.syn.url");
		  //String url ="http://localhost:8080/idvs/get_boss_custpic_info"; 
			httpurl = new URL(url);
			httpConn = (HttpURLConnection) httpurl.openConnection();
			httpConn.setRequestMethod("POST");
			httpConn.setDoOutput(true);
			httpConn.setDoInput(true);
			httpConn.setConnectTimeout(10000);
			httpConn.setReadTimeout(10000);
			httpConn.setRequestProperty("content-type", "text/html");
			out = new OutputStreamWriter(httpConn.getOutputStream(), "UTF-8");
			out.write(str);
			out.flush();
			//serviceLogger.info("|#受理单保存|#发送自动稽核请求|#工单流水号=" + saveBillRequ.getTradeId() + "|#消息内容=" + autoAuditStr+"|#发送成功");
			flag =true;
		}catch(Exception e){
			//serviceLogger.error("|#受理单保存|#发送自动稽核请求失败,工单流水号：=" + saveBillRequ.getTradeId(), e);
		}finally{
			if(null!=out){
				try {
					out.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
				//	serviceLogger.error("|#受理单保存|#发送自动稽核请求|#关闭流失败,工单流水号：=" + saveBillRequ.getTradeId(), e);
				}
			}
		}
		InputStream inStream = null;
		String strResult = null;
		BufferedReader br = null;
		if (flag) {
			try {
					String line = null; 
					inStream = (InputStream) httpConn.getInputStream();
				//	serviceLogger.info("|#受理单保存|#接收自动稽核响应|#工单流水号=" + saveBillRequ.getTradeId());
					br = new BufferedReader(new InputStreamReader(inStream,"UTF-8"));
					StringBuilder sb = new StringBuilder();
			        while((line = br.readLine())!=null){
						sb.append(line);				
			        }
				strResult = sb.toString();
				//System.out.print(strResult);
			//	serviceLogger.info("|#受理单保存|#接收自动稽核响应|#工单流水号=" + saveBillRequ.getTradeId() + "|#响应状态码=" +strResult);
			} catch (Exception e) {
			//	serviceLogger.info("|#受理单保存|#接收自动稽核响应失败|#工单流水号=" + saveBillRequ.getTradeId(),e);
			} finally {
				if (inStream != null) {
					try {
						inStream.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
					//	serviceLogger.info("|#受理单保存|#接收自动稽核响应|#关闭流失败|#工单流水号=" + saveBillRequ.getTradeId(),e);
					}
				}
				if (httpConn != null) {
					httpConn.disconnect();
					httpConn = null;
				}
			}
		}
		return strResult;
	}	
}
