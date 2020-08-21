package com.asiainfo.veris.crm.order.soa.person.busi.gprslimit;

import java.util.StringTokenizer;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.SmsSend;

public class GprsDealBean extends CSBizBean{

	  public void limitGprs() throws Exception{
		  IData param=new DataMap();
		  String recvObject = "";
		  String paraCode1 = "";
		  param.put("RSRV_STR1", "0");
		  IDataset userInfos = Dao.qryByCode("TF_F_USER_GRPSLIMIT", "SEL_GRRS_LIMIT_USERS", param); 
		  if(IDataUtil.isNotEmpty(userInfos)){
			  
		  IDataset commData = CommparaInfoQry.getCommPkInfo("CSM", "1109", "GPRS_LIMIT", "0898");
		  if(IDataUtil.isNotEmpty(commData)){
			  paraCode1 = commData.getData(0).getString("PARA_CODE1");
		  }else{
			  CSAppException.apperr(CrmCommException.CRM_COMM_103, "请先配置自动限速处理阀值！");
		  }
		  
		  if(userInfos.size()>Integer.parseInt(paraCode1)){
			  
			  String para_code2 = commData.getData(0).getString("PARA_CODE2");
			  
			    if(StringUtils.isBlank(para_code2)){
			        return;
			    }
			    else if(para_code2.indexOf(",") == -1)
	          	{
	      			recvObject =  para_code2;
	      			insertSms(recvObject,paraCode1,userInfos.size());
	      			return;
	          	}
	          	else
	          	{
	                  StringTokenizer token = new StringTokenizer(para_code2, ",");
	                  //token.countTokens()
	                  while (token.hasMoreTokens())
	                  {
	                	  recvObject = token.nextToken();
	                      insertSms(recvObject,paraCode1,userInfos.size());
	          	      }
	                  return;
	          		}
			  }else{
		  
			  for(int i=0;i<userInfos.size();i++){
					//取用户信息
	    			IData users=userInfos.getData(i);  
	    			users.put("STAFF_ID", "SUPERUSR");
	    			users.put("LOGIN_EPARCHY_CODE", "0898");
	    			users.put("STAFF_EPARCHY_CODE", "0898");
	    			users.put("IN_MODE_CODE", "0");
	    			users.put("CITY_CODE", "HNSJ");
	    			users.put("DEPART_ID", "36601");
	    			users.put("DEPART_CODE", "HNSJ0000"); 
	    			try{
	        		IDataset result = CSAppCall.call("SS.GprsLockSVC.tradeReg", users);
	    			IData inparam = new DataMap();
	    			inparam.put("RSRV_STR1", "1");
	    			inparam.put("RSRV_STR2", "");
	    			inparam.put("USER_ID",users.getString("USER_ID"));
    				Dao.executeUpdateByCodeCode("TF_F_USER_GRPSLIMIT", "UPD_STATUS_BY_UID", inparam);
	    			}catch(Exception ex){
	    			IData inparam = new DataMap();
	    			String msg = ex.getMessage() == null? "":ex.getMessage();
					msg = msg.length() > 100 ? msg.substring(0, 100) : msg;
	    			inparam.put("RSRV_STR1","2");
	    			inparam.put("RSRV_STR2",msg);
	    			inparam.put("USER_ID",users.getString("USER_ID"));
	    				Dao.executeUpdateByCodeCode("TF_F_USER_GRPSLIMIT", "UPD_STATUS_BY_UID", inparam);
	    			}
			  }
			}
		  }
	  }
	  
	    public void insertSms(String recvObject, String paraCode1, int counts) throws Exception
	    {
	    	
	    	UcaData ucaDataOut = UcaDataFactory.getNormalUca(recvObject);
	    	String sysDate =SysDateMgr.getSysTime();
	    	String sysDateChina = SysDateMgr.decodeTimestamp(sysDate,SysDateMgr.PATTERN_CHINA_TIME);
	    	StringBuilder SMSInfo = new StringBuilder();
	    	SMSInfo.append("你好，今日新增需要流量限速的用户共"+counts+"位，超过阀值"+paraCode1+"，请核查确认后手动限速。");
	    	IData input = new DataMap();
	    	String sms_notice_id=SeqMgr.getSmsSendId();
			input.put("SMS_NOTICE_ID",sms_notice_id);
			input.put("PARTITION_ID", sms_notice_id.substring(sms_notice_id.length() - 4));
			input.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
			input.put("BRAND_CODE", "");
			input.put("IN_MODE_CODE", getVisit().getInModeCode());
			input.put("CHAN_ID","11");
			input.put("SMS_NET_TAG","0");
			input.put("SEND_COUNT_CODE","1");
			input.put("SEND_TIME_CODE","1");
			input.put("RECV_OBJECT_TYPE","00");
			input.put("RECV_OBJECT",recvObject);
			input.put("RECV_ID",ucaDataOut.getUser().getUserId());
			input.put("SMS_TYPE_CODE","20");
			input.put("SMS_KIND_CODE","02");
			input.put("NOTICE_CONTENT_TYPE","0");
			input.put("REFERED_COUNT","0");
			input.put("FORCE_REFER_COUNT","1");//NOTICE_CONTENT
			input.put("NOTICE_CONTENT", SMSInfo);
			//input.put("FORCE_OBJECT", forceObject);
			//input.put("FORCE_START_TIME",sysDate);//指定发送次数
			//input.put("FORCE_END_TIME", "");
			input.put("SMS_PRIORITY",1000);//短信优先级
			input.put("REFER_TIME",sysDate);//提交时间
			input.put("REFER_STAFF_ID",getVisit().getStaffId());//提交员工
			input.put("REFER_DEPART_ID",getVisit().getDepartId());//提交部门
			input.put("DEAL_TIME",sysDate);//处理时间
			input.put("DEAL_STAFFID", getVisit().getStaffId());
			input.put("DEAL_DEPARTID", getVisit().getDepartId());
			input.put("DEAL_STATE","15");//处理状态:0－未处理
			input.put("REMARK", "GPRS限速告警");
			input.put("MONTH",Integer.parseInt(sysDate.substring(5, 7)));
			input.put("DAY",Integer.parseInt(sysDate.substring(8, 10)));
			SmsSend.insSms(input);
	    }
}
