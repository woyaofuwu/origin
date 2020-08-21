
package com.asiainfo.veris.crm.order.soa.person.busi.score.scoreexchange;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

public class ScoreHBExchangeRegSVC extends CSBizService
{
    public IData ScoreHBExchange(IData input) throws Exception
    {
        IData param = new DataMap() ;
        IData param1 = new DataMap();
        DatasetList param2 = new DatasetList ();
        String ruleid = "";
        IData result = new DataMap();
        String message = "" ;
        
        IDataset paramSet =  CommparaInfoQry.getCommByParaAttr("CSM","1923",this.getTradeEparchyCode());
        
        if(IDataUtil.isEmpty(paramSet))
        {
        	message = "获取积分兑换电子券金额配置失败";
    		result.put("RETCODE", "2998");
    		result.put("RETMSG", message );
    		result.put("X_RSPTYPE", "2");
    		result.put("X_RSPCODE", "2998");
    		result.put("X_RSPDESC",message);
    		result.put("X_RESULTINFO",message);
    		result.put("X_RESULTCODE","-1");
    		return result ;
        }       
        
        ruleid = paramSet.getData(0).getString("PARA_CODE2","");
        
        if("".equals(input.getString("MD10_SUBJECT")))
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
        
        if(input.getString("MD10_SUBJECT").length()<3)
        {
        	message = "电子券金额单位为分，请重新输入";
    		result.put("RETCODE", "2998");
    		result.put("RETMSG", message );
    		result.put("X_RSPTYPE", "2");
    		result.put("X_RSPCODE", "2998");
    		result.put("X_RSPDESC",message);
    		result.put("X_RESULTINFO",message);
    		result.put("X_RESULTCODE","-1");
    		return result ;
        }
        
        int evalue = input.getInt("MD10_SUBJECT")/100 ;
        
        param.put("EVALUE",String.valueOf(evalue));

        param.put("RULE_ID", ruleid);
        param.put("COUNT", "1");
        param2.add(param);      
        param1.put("EXCHANGE_DATA",param2.toString());
        param1.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));       
        try
        {
	        IData data = CSAppCall.call("SS.ScoreExchangeRegSVC.tradeReg", param1).getData(0);
	        if(IDataUtil.isNotEmpty(data) && !data.getString("TRADE_ID").isEmpty() && !data.getString("TRADE_ID").equals("-1"))
	        {         	
	        	message = "和包电子券兑换成功";
	    		result.put("RETCODE", "0000");
	    		result.put("RETMSG", message );
	    		result.put("X_RSPTYPE", "0");
	    		result.put("X_RSPCODE", "0000");
	    		result.put("X_RSPDESC","ok");
	    		result.put("X_RESULTINFO","ok");
	    		result.put("X_RESULTCODE","00");
	            return result;
	        }
        }
        catch (Exception ex)
        {
        	message =ex.getMessage();
        	result.put("RETCODE", "2998");
    		result.put("RETMSG", message );
    		result.put("X_RSPTYPE", "2");
    		result.put("X_RSPCODE", "2998");
    		result.put("X_RSPDESC",message);
    		result.put("X_RESULTINFO",message);
    		result.put("X_RESULTCODE","-1");
            return result;
        }

        return  null;
    }
    /**
     * REQ201703030013_新增积分兑换等值和包电子券业务
     * <br/>
     * 积分兑换和电子劵结果查询
     * @param input
     * @return
     * @throws Exception
     */
    public IData queryScoreExchange(IData input) throws Exception{
    	IData  result=new DataMap();
    	
        ScoreExchangeBean scoreExchangeBean = (ScoreExchangeBean) BeanManager.createBean(ScoreExchangeBean.class);
    	try {
    		 String REQ_ID=input.getString("REQ_ID", "");
    		 String SERIAL_NUMBER=input.getString("SERIAL_NUMBER", "");
    		 if("".equals(REQ_ID)){
    			 CSAppException.apperr(CrmCommException.CRM_COMM_103,"请求业务流水号不能为空");
    		 }
    		 
    		 if("".equals(SERIAL_NUMBER)){
    			 CSAppException.apperr(CrmCommException.CRM_COMM_103,"手机号码不能为空");
    		 }
    		 
    		 IData ucaInfo=UcaInfoQry.qryUserInfoBySn(SERIAL_NUMBER);
    		 if(IDataUtil.isEmpty(ucaInfo)){
    			 CSAppException.apperr(CrmCommException.CRM_COMM_103,"用户信息不存在");
    		 }
    		 
    		 IDataset  list=scoreExchangeBean.queryScoreExchange(input);
    		 if(IDataUtil.isNotEmpty(list)){
    			 result.put("DEAL_REUSLT", "积分兑换和电子劵成功");
    			 
    			 result.put("TRADE_ID", list.getData(0).getString("TRADE_ID",""));
    		 }else{
    			 result.put("DEAL_REUSLT", "积分兑换和电子劵失败");
    		 }
    		
		} catch (Exception e) {
			result.put("DEAL_REUSLT", e.getMessage());
		}
		return result;
    }
}
