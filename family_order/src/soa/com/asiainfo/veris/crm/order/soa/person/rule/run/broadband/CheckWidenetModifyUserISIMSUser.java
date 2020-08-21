
package com.asiainfo.veris.crm.order.soa.person.rule.run.broadband;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

/**
 * @Description: 宽带过户时判断是否有IMS家庭固话
 * @version: v1.0.0
 * @author: zhuoyingzhi
 * @date 20171120
 */
public class CheckWidenetModifyUserISIMSUser extends BreBase implements IBREScript
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
	static  Logger logger=Logger.getLogger(CheckWidenetModifyUserISIMSUser.class);


    @Override
    public boolean run(IData databus, BreRuleParam param) throws Exception
    {
    	String serialNumber = databus.getString("SERIAL_NUMBER", "");
    	IData params=new DataMap();
    	if(!"".equals(serialNumber)&& serialNumber != null){
    		if(serialNumber.indexOf("KD_") == -1){
    			//不存在
    			
    		}else{
    			//存在
    			serialNumber=serialNumber.replaceAll("KD_", "");
    		}
    		
        	params.put("SERIAL_NUMBER", serialNumber);
        	IData ImsInfo=CSAppCall.callOne("SS.ChangeSvcStateSVC.getIMSInfoBySerialNumber", params);
        	logger.debug("----CheckWidenetModifyUserISIMSUser-----ImsInfo---"+ImsInfo);
        	if(IDataUtil.isNotEmpty(ImsInfo)){
        		//IMS家庭固话  有IMS家庭固话业务,不允许办理宽带过户
        		logger.debug("----CheckWidenetModifyUserISIMSUser--------");
    			return true;
        	}
    	}
        return false;
    }
}
