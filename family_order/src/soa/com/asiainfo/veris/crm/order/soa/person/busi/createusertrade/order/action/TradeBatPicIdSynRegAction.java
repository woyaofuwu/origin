package com.asiainfo.veris.crm.order.soa.person.busi.createusertrade.order.action;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.ailk.biz.BizEnv;
import com.ailk.bizservice.dao.Dao;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.sysorg.StaffInfoQry;
import com.itextpdf.text.pdf.codec.Base64.InputStream;

/**
 * 物联网批量开户人像比对受理单编号与照片编号同步接口调用
 * @author zhuoyingzhi
 * @date 20170907
 */
public class TradeBatPicIdSynRegAction implements ITradeAction{

	private static Logger logger = Logger.getLogger(TradeBatPicIdSynRegAction.class);
	
	/**
     * 人像比对受理单编号与照片编号同步接口调用
     * @param cycle
     * @throws Exception
     */
	@Override
	public void executeAction(BusiTradeData btd) throws Exception {
		// TODO Auto-generated method stub
		UcaData uca = btd.getRD().getUca();
		String tradeId = btd.getRD().getTradeId();
		String tradeTypeCode = btd.getTradeTypeCode();
		//
		String batchOperType = btd.getRD().getPageRequestData().getString("BATCH_OPER_TYPE", "");
		printLog("TRADE_ID = ",tradeId);
		String operateId = btd.getMainTradeData().getBatchId();
		logger.debug("--TradeBatPicIdSynRegAction---operateId:"+operateId+"getPageRequestData:"+btd.getRD().getPageRequestData());
		if("CREATEPREUSER_PWLW".equals(batchOperType)||"CREATEPREUSER_M2M".equals(batchOperType)){
			//批量物联网开户
			//物联网品牌
			List<OtherTradeData> other = btd.getTradeDatas(TradeTableEnum.TRADE_OTHER);
			IData picinfo = getPicId(other,tradeTypeCode);
			printLog("picinfo = ",picinfo.toString());
			if(null == picinfo || picinfo.isEmpty()){
				return;
			}
			
			// 调用接口
			IData inParam = new DataMap();
			//开户first_pic_id表示新户主或经办人
			if("0".equals(picinfo.getString("PIC_TAG"))){
				
				inParam.put("first_pic_id", picinfo.get("PIC_ID"));
				
			}else if("0".equals(picinfo.getString("AGENT_PIC_TAG"))){
				
				inParam.put("first_pic_id", picinfo.get("AGENT_PIC_ID"));
				
			}else if("0".equals(picinfo.getString("STR4_PIC_TAG"))){
				//责任人
				inParam.put("first_pic_id", picinfo.get("STR4_PIC_ID"));
			}
			
			inParam.put("trade_id", tradeId);
			inParam.put("op_code", tradeTypeCode);
			inParam.put("phone", uca.getSerialNumber());
			inParam.put("work_no", CSBizBean.getVisit().getStaffId());
			inParam.put("org_info", CSBizBean.getVisit().getDepartId());
			IDataset ds = StaffInfoQry.qryStaffInfoByStaffId(CSBizBean.getVisit().getStaffId());
			if (ds != null && ds.size() == 1) {
				inParam.put("work_name", ds.getData(0).getString("STAFF_ID"));
			}
			IData param = new DataMap();
			param.put("DEPART_ID", CSBizBean.getVisit().getDepartId());
			ds = Dao.qryByCode("TD_M_DEPART", "SEL_ALL_BY_PK", param);
			if (ds != null && ds.size() == 1) {
				inParam.put("org_name", ds.getData(0).getString("DEPART_ID"));
			}
			inParam.put("op_time", btd.getRD().getAcceptTime());
			
			//批次号
			inParam.put("batch_number", getBatdealBatchId(operateId));
			
			
			JSONObject jSONObject = null;
			jSONObject = JSONObject.fromObject(inParam);

			String   contentJson = jSONObject.toString();
			IData ibossData = new DataMap();
			ibossData.put("buffer", contentJson);

			try {
				printLog("--TradeBatPicIdSynRegAction-SynCall=======contentJson==",contentJson);
				String strResult=sendAutoAudit(contentJson);
				printLog("---TradeBatPicIdSynRegAction--SynCall result = ",strResult);
			} catch (Exception e) {
				if(logger.isInfoEnabled()) logger.info(e.getMessage());
				throw e;
			}			
		}
	}	
	
	
	/**
     * 获取照片ID
     * @param btd
     * @throws Exception
     */
	private IData getPicId (List<OtherTradeData> others, String tradeTypeCode) throws Exception
	{
		IData pictada = new DataMap();
		
		String picid = "";
		String agentpicid = "";
		String picTag = "0";
		String str4picId="";//责任人
		
		if(others != null && others.size() > 0)
        {
            for(OtherTradeData other : others)
            {
                if("BAT_PIC_ID".equals(other.getRsrvValueCode())){
                	picid = other.getRsrvStr1();
                	agentpicid = other.getRsrvStr3();
                	str4picId=other.getRsrvStr5();//责任人
                	picTag = "1";
                }
            }
        }
		
		if("0".equals(picTag)){
			return new DataMap();
		}
		
    	if(StringUtils.isNotBlank(picid))
    	{//客户照片ID存在
    		pictada.put("PIC_ID", picid);
    		pictada.put("PIC_TAG", "0");
    	}
    	else
    	{
    		pictada.put("PIC_TAG", "1");
    	}
    	if(StringUtils.isNotBlank(agentpicid))
    	{//经办人照片ID存在
    		pictada.put("AGENT_PIC_ID", agentpicid);
    		pictada.put("AGENT_PIC_TAG", "0");
    	}else{
    		pictada.put("AGENT_PIC_TAG", "1");
    	}
    	
    	
    	if(StringUtils.isNotBlank(str4picId))
    	{//责任人照片ID存在
    		pictada.put("STR4_PIC_ID", str4picId);
    		pictada.put("STR4_PIC_TAG", "0");
    	}
    	else
    	{
    		pictada.put("STR4_PIC_TAG", "1");
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
			logger.debug("<<<<<<<<<<<<<<<<<<<<<<<<TradeBatPicIdSynRegAction "+name+value+"<<<<<<<<<<<<<<<<<<<<<<<<");
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
		InputStreamReader input=null;
		if (flag) {
			try {
					String line = null; 
					inStream = (InputStream) httpConn.getInputStream();
				//	serviceLogger.info("|#受理单保存|#接收自动稽核响应|#工单流水号=" + saveBillRequ.getTradeId());
					input=new InputStreamReader(inStream,"UTF-8");
					br = new BufferedReader(input);
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
						br.close();
						input.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
					//	serviceLogger.info("|#受理单保存|#接收自动稽核响应|#关闭流失败|#工单流水号=" + saveBillRequ.getTradeId(),e);
						if(logger.isInfoEnabled()) logger.info(e.getMessage());
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

	/**
	 * 获取BatdealBatchId
	 * <br/>
	 * 由于TF_B_TRADE_BATDEAL表的OPERATE_ID对应台账表的BATCH_ID,
	 * 所以要转换一下
	 * @param operateId
	 * @return
	 * @throws Exception
	 */
	public String  getBatdealBatchId(String operateId) throws Exception{
		IData batParam=new DataMap();
		batParam.put("OPERATE_ID", operateId);
		IDataset batdealInfo = Dao.qryByCode("TF_B_TRADE_BATDEAL",
				"SEL_BY_OPERATEID", batParam,
				Route.getJourDb(Route.CONN_CRM_CG));
		if(IDataUtil.isEmpty(batdealInfo)){
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "通过台帐BATCH_ID获取TF_B_TRADE_BATDEAL不存在");
		}
		//获取批量任务中的batchId
		String batchId=batdealInfo.getData(0).getString("BATCH_ID", "");	
		return batchId;
	}
	
}
