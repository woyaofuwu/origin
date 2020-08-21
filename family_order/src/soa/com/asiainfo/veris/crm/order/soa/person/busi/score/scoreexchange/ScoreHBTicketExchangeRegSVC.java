
package com.asiainfo.veris.crm.order.soa.person.busi.score.scoreexchange;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;

public class ScoreHBTicketExchangeRegSVC extends CSBizService
{
    public IData ScoreHBExchange(IData input) throws Exception
    {
        IData param = new DataMap() ;
        IData param1 = new DataMap();
        DatasetList param2 = new DatasetList ();
        String ruleid = "";
        IData result = new DataMap();
        String message = "" ;
        
		IDataUtil.chkParam(input, "PAY_AMOUNT");
		IDataUtil.chkParam(input, "REQ_ID");
		IDataUtil.chkParam(input, "SERIAL_NUMBER");
		
		IDataUtil.chkParam(input, "ACTION_ID");
		IDataUtil.chkParam(input, "PRO_ID");
        
        IDataset paramSet =  CommparaInfoQry.getCommByParaAttr("CSM","1924",this.getTradeEparchyCode());
        
        if(IDataUtil.isEmpty(paramSet))
        {
        	message = "获取积分兑换电子券配置失败";
    		result.put("RETCODE", "2998");
    		result.put("RETMSG", message );
    		result.put("X_RSPTYPE", "2");
    		result.put("X_RSPCODE", "2998");
    		result.put("X_RSPDESC",message);
    		result.put("X_RESULTINFO",message);
    		result.put("X_RESULTCODE","-1");
    		return result ;
        }       
        
        ruleid = paramSet.getData(0).getString("PARA_CODE2","");//因为兑换的金额是变化的，给个虚拟的编码rule_id=666888，td_s_commpara表里面配置
        
        if("".equals(input.getString("PAY_AMOUNT")))//兑换和包电子券金额 ,精确到分
        {
        	message = "电子券金额不能为空";
    		result.put("RETCODE", "2998");
    		result.put("RETMSG", message );
    		result.put("X_RSPTYPE", "2");
    		result.put("X_RSPCODE", "2998");
    		result.put("X_RSPDESC",message);
    		result.put("X_RESULTINFO",message);
    		result.put("X_RESULTCODE","-1");
    		return result ;
        }
        
 
       
        int evalue = input.getInt("PAY_AMOUNT") ;//参数传过来就是分
        if(evalue<0)//兑换和包电子券金额 为负数，不能兑换
        {
        	message = "兑换和包电子券金额小于0,不能兑换";
    		result.put("RETCODE", "2998");
    		result.put("RETMSG", message );
    		result.put("X_RSPTYPE", "2");
    		result.put("X_RSPCODE", "2998");
    		result.put("X_RSPDESC",message);
    		result.put("X_RESULTINFO",message);
    		result.put("X_RESULTCODE","-1");
    		return result ;
        }
        
        param.put("HBEVALUE",String.valueOf(evalue));//REQ201703030013

        param.put("RULE_ID", ruleid);
        param.put("COUNT", "1");
        param.put("REQ_ID", input.getString("REQ_ID"));//请求业务流水号 
        param.put("ACTION_ID", input.getString("ACTION_ID"));//活动编号
        param.put("PRO_ID", input.getString("PRO_ID"));//券别编号


        param2.add(param);      
        param1.put("EXCHANGE_DATA",param2.toString());
        param1.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));       
//        try
//        {
	        IData data = CSAppCall.call("SS.ScoreExchangeRegSVC.tradeReg", param1).getData(0);
	        if(IDataUtil.isNotEmpty(data) && !data.getString("TRADE_ID").isEmpty() && !data.getString("TRADE_ID").equals("-1"))
	        { 
	        	IData  params=new DataMap();
	        	//PAY_AMOUNT传值为分，  分转换为元，再用公式求得积分值 A=round(B/0.012,0) 积分值A等于B除以0.012的值进行四舍五入保留整数部分
	        	// 所以最终为 PAY_AMOUNT/1.2
	        	int needscore =(int) Math.round(input.getInt("PAY_AMOUNT")/1.2);
	        	params.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
	        	//params.put("HBEVALUE", input.getInt("PAY_AMOUNT")/100.00);
	        	Double hbevalue = input.getInt("PAY_AMOUNT")/100.00 ;
	        	params.put("HBEVALUE", hbevalue);
	        	params.put("SCORE_VALUE", needscore);

	        	//登记成功发短信
	        	this.sendSMS(params);
	        	message = "和包电子券兑换成功";
	    		result.put("RETCODE", "0000");
	    		result.put("RETMSG", message );
	    		result.put("X_RSPTYPE", "0");
	    		result.put("X_RSPCODE", "0000");
	    		result.put("X_RSPDESC","ok");
	    		result.put("X_RESULTINFO","ok");
	    		result.put("X_RESULTCODE","00");
	    		result.put("TRADE_ID", data.getString("TRADE_ID"));

	            return result;
	        }
      //  }
//        catch (Exception ex)
//        {
//        	message =ex.getMessage();
//        	result.put("RETCODE", "2998");
//    		result.put("RETMSG", message );
//    		result.put("X_RSPTYPE", "2");
//    		result.put("X_RSPCODE", "2998");
//    		result.put("X_RSPDESC",message);
//    		result.put("X_RESULTINFO",message);
//    		result.put("X_RESULTCODE","-1");
//            return result;
//        }

        return  null;
    }
    
    //下发短信
    public static void sendSMS(IData input) throws Exception
    {			
	    IDataset users = UserInfoQry.getUserInfoBySerailNumber("0",input.getString("SERIAL_NUMBER",""));
	    String  user_id="";
	    if(IDataUtil.isNotEmpty(users)){
	    	  user_id=users.getData(0).getString("USER_ID");
	    }
	        // 拼短信表参数
	        IData param = new DataMap();
	        param.put("NOTICE_CONTENT", "尊敬的客户，您已成功使用"+input.getString("SCORE_VALUE","")+"积分兑换"+input.getString("HBEVALUE","")+"元和包电子券。【中国移动】");
	        param.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
	        param.put("IN_MODE_CODE", "0");
	        param.put("RECV_OBJECT", input.getString("SERIAL_NUMBER"));
	        param.put("RECV_ID", user_id);//user_id
	        param.put("REFER_STAFF_ID", CSBizBean.getVisit().getStaffId());
	        param.put("REFER_DEPART_ID", CSBizBean.getVisit().getDepartId());

	        param.put("REMARK", "业务短信通知");
	        String seq = SeqMgr.getSmsSendId();
	        long seq_id = Long.parseLong(seq);
	        param.put("SMS_NOTICE_ID", seq_id);
	        param.put("PARTITION_ID", seq_id % 1000);
	        param.put("SEND_COUNT_CODE", "1");
	        param.put("REFERED_COUNT", "0");
	        param.put("CHAN_ID", "11");
	        param.put("SMS_NET_TAG", "0");
	        param.put("RECV_OBJECT_TYPE", "00");
	        param.put("SMS_TYPE_CODE", "20");
	        param.put("SMS_KIND_CODE", "07");
	        param.put("NOTICE_CONTENT_TYPE", "0");
	        param.put("FORCE_REFER_COUNT", "1");
	        param.put("FORCE_OBJECT", "");
	        param.put("SMS_PRIORITY", "50");
	        param.put("DEAL_STATE", "15");
	        param.put("SEND_TIME_CODE", "1");
	        param.put("SEND_OBJECT_CODE", "6");
	        param.put("REFER_TIME", SysDateMgr.getSysTime());
	        param.put("DEAL_TIME", SysDateMgr.getSysTime());
	        param.put("MONTH", SysDateMgr.getCurMonth());
	        param.put("DAY", SysDateMgr.getCurDay());
	        param.put("ISSTAT", "0");
	        param.put("TIMEOUT", "0");
	
	        Dao.insert("ti_o_sms", param,Route.getCrmDefaultDb());
  		
    }
    
}
