package com.asiainfo.veris.crm.order.soa.script.rule.svcstate;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;

/**
 * 
 * 关于增加紧急开机使用条件限制的需求 
 * <br/>
 * 有往月欠费的客户不能申请紧急开机。即客户申请紧急开机时，如果有大于2个月的欠费，则不可以申请
 * @author zyz
 * 
 * 20161008
 * 账务侧接口做变更，新增JF_TAG字段，1表可以做紧急开机操作、0表否
 */
public class CheckFeeLimitByServiceState extends BreBase implements IBREScript{
	
	static Logger logger=Logger.getLogger(CheckFeeLimitByServiceState.class);
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	 public boolean run(IData databus, BreRuleParam ruleParam) throws Exception{
		        boolean bResult = true;
	   			String serialNumber = databus.getString("SERIAL_NUMBER");
	   			IDataset  userInfo=UserInfoQry.getUserInfoBySerailNumber("0", serialNumber);
	   			if(IDataUtil.isNotEmpty(userInfo)){
	   				String strUserId=userInfo.getData(0).getString("USER_ID");
	   				
		   			//调用账务接口
		   			IDataset callset=AcctCall.getUserOweFee(strUserId);
		   			
		   			if(IDataUtil.isNotEmpty(callset)){
		   				   //欠费
				   		   String feeStr=callset.getData(0).getString("JF_TAG");
				   			if("1".equals(feeStr)){
				   				//无欠费
				   				bResult=false;
				   			}else{
				   				bResult=true;
				   			}
		   			}else{
		   				//无欠费
		   				bResult=false;
		   			}
	   			}else{
	   				//用户信息不存在
	   				CSAppException.apperr(CrmCommException.CRM_COMM_1172);
	   			}
	        return bResult;
	   }
	 
}
