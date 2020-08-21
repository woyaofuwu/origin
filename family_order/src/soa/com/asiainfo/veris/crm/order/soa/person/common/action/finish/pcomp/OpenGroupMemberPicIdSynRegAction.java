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
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.sysorg.StaffInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.itextpdf.text.pdf.codec.Base64.InputStream;

/**
 * REQ201801150022_新增IMS号码开户人像比对功能
 * <br/>
 * 人像比对受理单编号与照片编号同步接口调用
 * @author zhuoyingzhi
 * @date 20180329
 *
 */
public class OpenGroupMemberPicIdSynRegAction implements ITradeFinishAction{

	private static Logger logger = Logger.getLogger(OpenGroupMemberPicIdSynRegAction.class);
	/**
     * 人像比对受理单编号与照片编号同步接口调用
     * @param cycle
     * @throws Exception
     */
	public void executeAction (IData mainTrade) throws Exception
	{
		String tradeId = mainTrade.getString("TRADE_ID","");
		String userId = mainTrade.getString("USER_ID","");
		String custId = mainTrade.getString("CUST_ID","");
		String tradeTypeCode = mainTrade.getString("TRADE_TYPE_CODE","");
		
		
		//品牌 IMSG
		String  brandCode= mainTrade.getString("BRAND_CODE","");
		System.out.println("----OpenGroupMemberPicIdSynRegAction11---brandCode:"+brandCode);
		if(!"IMSG".equals(brandCode)){
			//非IMS语音品牌
			return;
		}
		
		
		//台帐里面的batchid
		String tradeBatchId=mainTrade.getString("BATCH_ID", "");
		
		IData picinfo =new DataMap();
		
		//是否为批量开户标识
		boolean isGroup=false;

		
		//单个集团成员开户
		picinfo=getPicId(tradeId, userId, custId, tradeTypeCode, "PIC_ID");
		
		if(IDataUtil.isEmpty(picinfo)){
			//非单个集团成员开户
			
			//IMS批量成员开户
			picinfo=getPicId(tradeId, userId, custId, tradeTypeCode, "BAT_GROUP_PIC_ID");
			if(IDataUtil.isNotEmpty(picinfo)&&!"".equals(tradeBatchId)){
				//IMS批量操作
				isGroup=true;
			}
		}
		
		
		
		printLog("picinfo = ",picinfo.toString());
		System.out.println("----OpenGroupMemberPicIdSynRegAction11---picinfo:"+picinfo);
		
		if(null == picinfo || picinfo.isEmpty()){
			//没有摄像,不需要同步信息
			return;
		}
		
		
		
		// 调用接口
		IData inParam = new DataMap();
	
		//开户first_pic_id表示新户主或经办人
		if("0".equals(picinfo.getString("PIC_TAG"))){
			inParam.put("first_pic_id", picinfo.get("PIC_ID"));
		}else if("0".equals(picinfo.getString("AGENT_PIC_TAG"))){
			inParam.put("first_pic_id", picinfo.get("AGENT_PIC_ID"));
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

		
	    if(isGroup){
			//台帐里面的batchid对应的批量任务里面的operateId,所以要获取真正的batchid就需要换成一下
			String batchId=getBatdealBatchId(tradeBatchId);
			//批次号
			inParam.put("batch_number", batchId);
	    }	
		
		System.out.println("--OpenGroupMemberPicIdSynRegAction--inParam:"+inParam);
		
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
	private IData getPicId (String tradeId,String userId,String custId,String tradeTypeCode,String rsrvValueCode) throws Exception
	{
		IData pictada = new DataMap();
	    //BAT_GROUP_PIC_ID
		IDataset pictaginfo =UserOtherInfoQry.getOtherInfoByCodeUserId(userId, rsrvValueCode);
		
		System.out.println("---OpenGroupMemberPicIdSynRegAction---pictaginfo:"+pictaginfo+",userId:"+userId);
		
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
