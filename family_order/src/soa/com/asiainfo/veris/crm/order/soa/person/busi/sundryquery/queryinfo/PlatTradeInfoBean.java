
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.queryinfo;

import org.apache.log4j.Logger;

import com.ailk.biz.service.BizRoute;
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.BizException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.ProvinceUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustomerInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.TwoCheckSms;





public class PlatTradeInfoBean extends CSBizBean
{
    private static transient final Logger logger = Logger.getLogger(PlatTradeInfoBean.class);
    
    //用户光大信息查询借口
     public IData  queryUserInfoInft(IData input) throws Exception
    {
    	 IData ret = new DataMap();
    	 String serialnumber = input.getString("SERIAL_NUMBER");//手机号码
    	 String psptypecode = input.getString("PSPT_TYPE_CODE");//证件类型
    	 String psptid = input.getString("PSPT_ID");//证件号码
    	 String custnume = input.getString("CUST_NAME");//用户姓名
    	 
    	 if("".equals(serialnumber)||serialnumber==null)
    	 {
    		 CSAppException.apperr(CrmUserException.CRM_USER_114);
    	 }
    	 if("".equals(psptypecode)||psptypecode==null)
    	 {
    		 CSAppException.appError("2015041600","证件类型不能为空，请重新输入");
    	 }
    	 if("".equals(psptid)||psptid==null)
    	 {
    		 CSAppException.appError("2015041601","身份证号不能为空，请重新输入");
    	 }
    	 if("".equals(custnume)||custnume==null)
    	 {
    		 CSAppException.appError("2015041602","用户姓名不能为空，请重新输入");
    	 }
    	 
    	 IData userinfos = UcaInfoQry.qryUserInfoBySn(serialnumber);//获取用户信息
    	 
    	 if(IDataUtil.isEmpty(userinfos))
    	 {
    		 CSAppException.apperr(CrmUserException.CRM_USER_1);
    	 }
    	 
    	 IDataset custinfos = CustomerInfoQry.queryCustInfoBySN(serialnumber);//获取客户信息
    	 
    	 if(IDataUtil.isEmpty(custinfos))
    	 {
    		 CSAppException.apperr(CrmUserException.CRM_USER_1);
    	 }
    	 
    	 String custnume_a = custinfos.getData(0).getString("CUST_NAME");//客户名称
    	 String psptid_a = custinfos.getData(0).getString("PSPT_ID");//证件号码
    	 
    	 ret.put("SERIAL_NUMBER", serialnumber);//手机号码
    	 ret.put("CUST_NAME", "匹配");//姓名
    	 ret.put("PSPT_ID", "匹配");//证件号码
    	 
    	 if(!custnume_a.endsWith(custnume))
    	 {
    		 ret.put("CUST_NAME", "不匹配");
    	 }
    	 
    	 if(!psptid_a.endsWith(psptid))
    	 {
    		 ret.put("PSPT_ID", "不匹配");
    	 }
    	 //证件类型
    	 //ret.put("PSPT_TYPE", StaticUtil.getStaticValue("TD_S_PASSPORTTYPE", custinfos.getData(0).getString("PSPT_TYPE_CODE")));
    	 ret.put("PSPT_TYPE", StaticUtil.getStaticValue("TD_S_PASSPORTTYPE", psptypecode));

    	 
    	 String opendate = userinfos.getString("OPEN_DATE");//开户时间
    	 
    	 UcaData uca = null;
         uca = UcaDataFactory.getUcaByUserId(userinfos.getString("USER_ID"));
         String strCreditClass = uca.getUserCreditClass();//信用等级
    	 
         ret.put("STAR_NUM", strCreditClass);//星级
         
         int onlietime = SysDateMgr.dayInterval(SysDateMgr.decodeTimestamp(opendate, SysDateMgr.PATTERN_STAND),SysDateMgr.decodeTimestamp(SysDateMgr.getSysTime(), SysDateMgr.PATTERN_STAND));
         
         //在网时长
    	 ret.put("ONLINE_TIME",String.valueOf(onlietime));
    	 
    	 String date = SysDateMgr.addDays(SysDateMgr.getSysTime(), -182);
    	 
    	 date = SysDateMgr.decodeTimestamp(date,SysDateMgr.PATTERN_STAND);
    	 
    	 IData param = new DataMap();
    	 
    	 param.put("USER_ID", userinfos.getString("USER_ID"));
    	 param.put("START_DATE", date);
    	 
    	 IDataset svcstates = Dao.qryByCode("TF_F_USER_SVCSTATE", "SEL_BY_USER_UPT", param, Route.getJourDb(BizRoute.getRouteId()));   	 
    	 
    	 int k=0;
    	 int stopcount=0;//近半年停机次数
    	 int perstopcount=0;//近半年单次停机次数
    	 
    	 if(IDataUtil.isNotEmpty(svcstates))
    	 {
    		 for(int i=0,size1=svcstates.size();i<size1;i++)
    		 {
    			 if(!"0".equals(svcstates.getData(i).getString("STATE_CODE")) && !"N".equals(svcstates.getData(i).getString("STATE_CODE")))
    			 {
    				 stopcount++;
    			 }
    		 }
    		 
    		 ret.put("STOP_NUM1",stopcount);
    		 
    		 for(int n=0,size=svcstates.size();n<size;)
    		 {
    			 IData svcstate1 = svcstates.getData(n);
    			 if(!"0".equals(svcstate1.getString("STATE_CODE")) && !"N".equals(svcstate1.getString("STATE_CODE")))
    			 {
    				 int j ;
    				 for(j=n+1;j<size;)
    				 {
    					 IData svcstate2 = svcstates.getData(j);
    					 if("0".equals(svcstate2.getString("STATE_CODE")) || "N".equals(svcstate2.getString("STATE_CODE")) )
    					 {
    						 String date1 = svcstate1.getString("START_DATE") ;
    						 String date2 = svcstate2.getString("START_DATE") ;
    						 int tmpdate = SysDateMgr.dayInterval(SysDateMgr.decodeTimestamp(date1, SysDateMgr.PATTERN_STAND),SysDateMgr.decodeTimestamp(date2, SysDateMgr.PATTERN_STAND));
    						 if(tmpdate>6)
    						 {
    							 k=j+1;
    							 perstopcount++ ;
    							 break;
    						 }
    						 else
    						 {
    							 k=j+1;
    							 break;
    						 }
    					 }
    					 j++;
    				 }
    				 if(j==size)
    				 {
    					 String date1 = svcstate1.getString("START_DATE") ;
						 int tmpdate = SysDateMgr.dayInterval(SysDateMgr.decodeTimestamp(date1, SysDateMgr.PATTERN_STAND),SysDateMgr.decodeTimestamp(SysDateMgr.getSysTime(), SysDateMgr.PATTERN_STAND));
						 if(tmpdate>6)
						 {
							 perstopcount++ ;				 
						 }
						 k=j+1;
						 break;
    				 }
    			 }
    			 if(n>k || n==k)
    			 {
    				 n++;
    			 }
    			 else
    			 {
    				 n=k;
    			 }
    		 }
    		 
    		 ret.put("STOP_NUM2",perstopcount);
    	 }    	 
    	 else
    	 {
    		 ret.put("STOP_NUM1", "0");
    		 ret.put("STOP_NUM2", "0");
    	 }
    	 
    
    	 ret.put("ARPU", "-1");
    	 IDataset fees = UserInfoQry.getUserAvgPayFee(userinfos.getString("USER_ID"),"6");
    	 if(IDataUtil.isNotEmpty(fees))
    	 {
    		 ret.put("ARPU", fees.getData(0).getString("PARAM_CODE"));
    	 }
    	 
    	 String date1 = SysDateMgr.getSysTime();
    	 date1 = SysDateMgr.decodeTimestamp(date1,SysDateMgr.PATTERN_TIME_YYYYMM);
    	// String date2 = SysDateMgr.addDays(SysDateMgr.getSysDate(), -182);
    	// date2 = SysDateMgr.decodeTimestamp(date2,SysDateMgr.PATTERN_TIME_YYYYMM);
    	 
    	 IData param1 = new DataMap();
    	 param1.put("X_PAY_USER_ID",userinfos.getString("USER_ID"));
    	 param1.put("END_CYCLE_ID",date1);
    	 param1.put("START_CYCLE_ID",date1); 
    	 IDataOutput output = CSAppCall.callAcct("AM_QueryNewUserBill", param1, false);
    	 IData allInfo = output.getData().getData(0);
    	 //IDataset allInfos=CSAppCall.call("http://10.200.130.83:10000/service","AM_QueryNewUserBill", param1, false);   	 
    	 //IData allInfo = allInfos.getData(0);
    	 IDataset billinfos = allInfo.getDataset("BILL_INFOS");
    	 IData billinfo =new DataMap();
    	 for(int i=0,size=billinfos.size();i<size;i++)
    	 {
    		 billinfo = billinfos.getData(i);
    		 if("1. 套餐及固定费".equals(billinfo.getString("INTEGRATE_ITEM")))
    			 break;
    	 }
    	 //billinfo = billinfos.getData(0);
    	 ret.put("DIST", String.format("%1$3.2f", billinfo.getLong("FEE") / 100.0));
    	 
    	 return ret ;
    }
     
     //邀约业务受理借口
     public IData  platTradeInft(IData input) throws Exception
     {
    	 IData ret = new DataMap();
    	 
    	 IData param = new DataMap();
    	 String serialnumber = input.getString("SERIAL_NUMBER");//用户手机号码
    	 String smscontent   = input.getString("SMS_CONTENT");//要下发的短信内容
    	 String recvobject   = input.getString("RECV_OBJECT");//下发短信的号码
    	 String exprietime   = input.getString("EXPIRE_TIME");//失效时间,小时
    	 String platcode     = input.getString("PLAT_CODE");//平台编码
    	 
    	 int tmptime = (int) (Float.parseFloat(exprietime) * 60 * 60);
    	    	 
    	 exprietime = SysDateMgr.addSecond(SysDateMgr.getSysTime(),tmptime);
    	 
    	 IData userinfos = UcaInfoQry.qryUserInfoBySn(serialnumber);//获取用户信息
    	 
    	 if(IDataUtil.isEmpty(userinfos))
    	 {
    		 CSAppException.apperr(CrmUserException.CRM_USER_1);
    	 }
    	 
    	 param.put("PLAT_CODE", platcode);
    	 param.put("ACCEPT_TIME", SysDateMgr.getSysTime());
    	 param.put("SERIAL_NUMBER", serialnumber);
    	 param.put("USER_ID", userinfos.getString("USER_ID"));
    	 param.put("RECV_OBJECT", recvobject);
    	 param.put("EXPIRE_TIME", exprietime);
    	// param.put("REPLY_TIME", "");
    	 param.put("REPLY_STATUS", "0"); 	 
    	 param.put("SMS_CONTENT", smscontent);
    	 param.put("REPLY_CONTENT", "");
    	 IData result = insetsms(param);
    	 ret.put("X_REQUEST_ID",result.getString("REQUEST_ID"));
    	 try
    	 {  		
    		param.put("REQUEST_ID", result.getString("REQUEST_ID"));
    		Dao.executeUpdateByCodeCode("TL_B_PLAT_TRADE", "INSERT_PLAT_TRADE", param);
	    	ret.put("X_RESULTCODE", "0000");
	    	ret.put("X_RESULTINFO", "业务办理成功,下发短信的号码为 ："+ recvobject);	    	
    	 }
    	 catch(Exception e)
    	 {
    		 ret.put("X_RESULTCODE", "-1");
        	 ret.put("X_RESULTINFO",e.getMessage());
    	 }
  	     
    	 return ret ;
     }
     //邀约业务回复接口
     public IData  replyTradeInft(IData input) throws Exception
     {
    	 IData ret = new DataMap();
    	 IData param = new DataMap();
    	 
    	 String serialnumber = input.getString("SERIAL_NUMBER");//用户手机号码
    	 String requestid   = input.getString("REQUEST_ID");//回复 短信的号码 
    	 String replaytext     = input.getString("REPLY_CONTENT");//回复 内容 
    	 
    	 param.put("REQUEST_ID", requestid);
    	 //param.put("SERIAL_NUMBER", serialnumber);
    	 IDataset platinfos = Dao.qryByCode("TL_B_PLAT_TRADE", "SEL_PLAT_TRADE_BY_PK", param);
    	 if(IDataUtil.isNotEmpty(platinfos))
    	 {
    		 IData platinfo = platinfos.getData(0);
    		 String expritime =platinfo.getString("EXPIRE_TIME") ;
    		// expritime = SysDateMgr.decodeTimestamp(expritime,SysDateMgr.PATTERN_STAND);
    		 if(expritime.compareTo(SysDateMgr.getSysTime())<0)
    		 {
    			 ret.put("X_RESULTCODE", "2001");
            	 ret.put("X_RESULTINFO","已失效");
            	 return ret ;
    		 }

    		 if(!serialnumber.equals(platinfo.getString("SERIAL_NUMBER")))
    		 {
    			 ret.put("X_RESULTCODE", "-1");
            	 ret.put("X_RESULTINFO","手机号码不相同");
            	 return ret ;
    		 }
    		 
    		 if("1".equals(platinfo.getString("REPLY_STATUS")))
    		 {
    			 ret.put("X_RESULTCODE", "-1");
            	 ret.put("X_RESULTINFO","用户已回复");
            	 return ret ;
    		 }
    		 
			 param.clear();
			 //param.put("SERIAL_NUMBER", serialnumber);
			 param.put("REQUEST_ID", requestid);
			 param.put("REPLY_CONTENT", replaytext);
			 param.put("REPLY_TIME", input.getString("REPLY_TIME",SysDateMgr.getSysTime()));
			 param.put("REPLY_STATUS", "1");
			 try
			 {
				 Dao.executeUpdateByCodeCode("TL_B_PLAT_TRADE", "UPD_PLAT_TRADE_BY_PK", param);
				 ret.put("X_RESULTCODE", "0");
	        	 ret.put("X_RESULTINFO","回复成功");
	        	 return ret ;
			 }
			 catch(Exception e)
			 {
				 ret.put("X_RESULTCODE", "-1");
	        	 ret.put("X_RESULTINFO",e.getMessage());
	        	 return ret ;
			 }
    		 
    	 }
    	 else
    	 {
    		 ret.put("X_RESULTCODE", "2002");
        	 ret.put("X_RESULTINFO","没有对应的记录");
        	 return ret ;
    	 }
    	 
     }
     //邀约业务查询接口
     public IDataset  queryTradeInft(IData input) throws Exception
     {
    	// IDataset ret = null ;
    	 IData param = new DataMap();
    	 
    	 String serialnumber = input.getString("SERIAL_NUMBER");//用户手机号码
    	 String acceptime  = input.getString("ACCEPT_TIME");//受理时间
    	 String platcode   = input.getString("PLAT_CODE");//平台编码
    	 String recvobject   = input.getString("RECV_OBJECT");//短信回复号码
    	 param.put("SERIAL_NUMBER", serialnumber);
    	 param.put("ACCEPT_TIME", acceptime);
    	 param.put("PLAT_CODE", platcode);
    	 param.put("RECV_OBJECT", recvobject);
    	 IDataset results = Dao.qryByCode("TL_B_PLAT_TRADE", "SEL_PLAT_TRADE_BY_PAR", param);
    	 return results ;   	 
     }
     
     public IData insetsms(IData input) throws Exception
     {        
         IData sendInfo = new DataMap();
         sendInfo.put("REMARK", "光大征信业务");
         sendInfo.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
         sendInfo.put("SMS_CONTENT", input.getString("SMS_CONTENT"));
         sendInfo.put("SMS_TYPE",BofConst.EXP_REMIND);
         sendInfo.put("OPR_SOURCE", "1");

         // 插二次短信表
         IData preOderData = new DataMap();
         preOderData.put("SVC_NAME", "SS.PlatTradeInfoSVC.replyTradeInft");
         preOderData.put("PRE_TYPE",BofConst.EXP_REMIND);
         preOderData.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
         //preOderData.put("FORCE_OBJECT", input.getString("RECV_OBJECT"));
         IData twoCheckData = TwoCheckSms.twoCheck("-1", 48, preOderData, sendInfo);// 插入2次短信表
         
         String request_id = twoCheckData.getString("REQUEST_ID");
         String acceptMonth = StrUtil.getAcceptMonthById(request_id);
         if (ProvinceUtil.isProvince(ProvinceUtil.HAIN))
         {// 海南

             request_id = request_id.substring(request_id.length() - 8);

             String svcName = preOderData.getString("SVC_NAME", CSBizBean.getVisit().getXTransCode());
             String twoCheck3 = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_STATIC", new String[]
             { "TYPE_ID", "DATA_NAME" }, "DATA_ID", new String[]
             { "BMC_TWOCHECK_CODE3", svcName });

             if ("".equals(twoCheck3) || twoCheck3 ==null)
            	 
                 CSAppException.apperr(BizException.CRM_BIZ_11);

             request_id = twoCheck3 + request_id + acceptMonth;
         }
         twoCheckData.put("REQUEST_ID", request_id); 
         return twoCheckData ;
     }    
}
