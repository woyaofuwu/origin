 
package com.asiainfo.veris.crm.order.web.frame.csview.common.component.auth;

import org.apache.tapestry.IRequestCycle;

import com.ailk.cache.memcache.util.SharedCache;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.ParamException;
import com.asiainfo.veris.crm.order.pub.exception.TradeTypeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.util.Des;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class AuthCheck extends PersonBasePage
{

    public abstract IDataset getCheckMode();

    public void init(IRequestCycle cycle) throws Exception
    {

        IData data = getData();
        String identity = data.getString("IDENTITY_CHECK_TAG");
        String tmp = "";
        String noUserPassWd = data.getString("NO_USER_PASSWD", "false");
        IDataset checkMode = new DatasetList();
        String tradeTypeCode=data.getString("TRADE_TYPE_CODE","");
        if (getVisit().getStaffId().startsWith("HNYD"))
        {  
            // 海南异地用户只有服务密码认证方式
            IData temp = new DataMap();
            temp.put("key", "1");
            temp.put("value", "服务密码");
            checkMode.add(0, temp);
        }else if(data.getString("TRADE_TYPE_CODE","").trim().equals("71")){//71 用户密码变更
            if ("true".equals(noUserPassWd))
            {
                CSViewException.apperr(ParamException.CRM_PARAM_519);               
            }
            
            tmp = identity.substring(1, 2);
            if ("1".equals(tmp) && !"true".equals(noUserPassWd))
            {
                IData temp = new DataMap();
                temp.put("key", "1");
                temp.put("value", "服务密码");
                checkMode.add(0, temp);
            }             

        }
        else if (identity.length() > 4)  
        {
            tmp = identity.substring(0, 1);

            if ("1".equals(tmp))
            {
                IData temp = new DataMap();
                temp.put("key", "0");
                temp.put("value", "客户证件");
                checkMode.add(temp);
            }

            tmp = identity.substring(1, 2);

            if ("1".equals(tmp) && !"true".equals(noUserPassWd))
            {
                IData temp = new DataMap();
                temp.put("key", "1");
                temp.put("value", "服务密码");
                checkMode.add(0, temp);
            }

            tmp = identity.substring(2, 3);
            if ("1".equals(tmp))
            {
                IData temp = new DataMap();
                temp.put("key", "2");
                temp.put("value", "SIM卡号(或白卡号)+密码");
                checkMode.add(temp);
            }

            tmp = identity.substring(3, 4);
            if ("1".equals(tmp))
            {
                IData temp = new DataMap();
                temp.put("key", "3");
                temp.put("value", "服务号码+证件");
                checkMode.add(temp);
            }

            tmp = identity.substring(4, 5);
            if ("1".equals(tmp))
            {
                IData temp = new DataMap();
                temp.put("key", "4");
                temp.put("value", "证件号码+服务密码");
                checkMode.add(temp);
            }
            
            //对产品变更做特殊处理
            boolean isPriv=StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "PRIV_PWDAUTH_110");
            if("110".equals(tradeTypeCode)&&isPriv){
            	IData temp = new DataMap();
				temp.put("key", "7");
				temp.put("value", "验证码");
				checkMode.add(temp);
            }
              
            if (identity.length() > 5){
	            tmp = identity.substring(5, 6);
				if ("1".equals(tmp)){
					IData temp = new DataMap();
					temp.put("key", "5");
					temp.put("value", "SIM卡号(或白卡号)+验证码");
					checkMode.add(temp);
				}
            }
            
            if (identity.length() > 6){
	            tmp = identity.substring(6, 7);
				if ("1".equals(tmp)){
					IData temp = new DataMap();
					temp.put("key", "6");
					temp.put("value", "服务密码+验证码");
					checkMode.add(temp);
				}
            }
            /**
             * REQ201606270002 非实名用户关停改造需求
             * chenxy3 2016-06-28
             * */
            if (identity.length() > 7){
	            tmp = identity.substring(7, 8);
				if ("1".equals(tmp)&&!("110".equals(tradeTypeCode)))
				{
					//宽带开户，魔百和开户，IMS固话开户,要做权限判断
			        if ("600".equals(tradeTypeCode)||"4800".equals(tradeTypeCode)||"6800".equals(tradeTypeCode) ||"606".equals(tradeTypeCode))
					{
			        	//是否有短信确认码权限
			        	if (StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "WIDE_PRODUCT_VERIFY_CODE"))
			        	{
			        		IData temp = new DataMap();
			    			temp.put("key", "7");
			    			temp.put("value", "验证码");
			    			checkMode.add(temp);
			        	}
			        }
			        else
			        {
			        	IData temp = new DataMap();
						temp.put("key", "7");
						temp.put("value", "验证码");
						checkMode.add(temp);
			        }
				}
            }
            if (identity.length() > 8){
	            tmp = identity.substring(8, 9);
				if ("1".equals(tmp)){
					IData temp = new DataMap();
					temp.put("key", "8");
					temp.put("value", "SIM卡号(或白卡号)");
					checkMode.add(temp);
				}
            }
        }
        this.setCheckMode(checkMode);
        
        /*REQ201501050004关于补换卡身份证件校验的需求
        * chenxy3 2015-01-21
        * 获取用户DATARIGHT权限，用以判定是否允许手动录入身份证号码  
        * */
        String TRADE_TYPE_CODE = data.getString("TRADE_TYPE_CODE");
        String staff=getVisit().getStaffId();
    	String highpriv = StaffPrivUtil.isFuncDataPriv(staff, "HIGH_PRIV")==true?"1":"0"; 
        IData id = new DataMap();
    	id.put("TRADE_TYPE_CODE", TRADE_TYPE_CODE);
    	id.put("HIGH_PRIV", highpriv);
    	if("142".equals(TRADE_TYPE_CODE)){
    		String remoteCardRight=StaffPrivUtil.isFuncDataPriv(staff, "REMOTECARD_RIGHT")==true?"1":"0";
    		id.put("REMOTECARD_RIGHT", remoteCardRight);
    	}
    	this.setInfo(id); 

        if (checkMode.size() == 0)
        {
            CSViewException.apperr(TradeTypeException.CRM_TRADETYPE_2);
        }
        String sessionId =cycle.getRequestContext().getRequest().getSession().getId();
    	String serialNum = data.getString("SERIAL_NUMBER");
    	if(serialNum!=null && !"".equals(serialNum)){
    		Des desObj = new Des();
    	    if(SharedCache.keyExist("SERIALNUMBERCACHE"+sessionId)){
    	    	SharedCache.delete("SERIALNUMBERCACHE"+sessionId); 
    	    }
    	    String serialNum_enc=desObj.setEncPwd(serialNum)+"xxyy"; 
    	    SharedCache.set("SERIALNUMBERCACHE"+sessionId, serialNum_enc, 300); //放到缓存
    	}

    }
     
    
    public void sendVerifyCode(IRequestCycle cycle) throws Exception{
    	IData data = getData();
    	IDataset infos = CSViewCall.call(this, "CS.AuthCheckSVC.sendSmsVerifyCode", data);
    	setAjax(infos.getData(0));
    }

    public abstract void setCheckMode(IDataset checkMode);
    public abstract void setInfo(IData info);

}
