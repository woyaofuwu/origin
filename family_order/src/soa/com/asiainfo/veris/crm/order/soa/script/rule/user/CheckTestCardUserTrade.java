package com.asiainfo.veris.crm.order.soa.script.rule.user;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.script.rule.utils.RuleUtils;
/**
 * 
 * REQ201609060001_2016年下半年测试卡功能优化（二）
 * <br/>
 * @author zhuoyingzhi
 * 20160926
 *
 */
public class CheckTestCardUserTrade extends BreBase implements IBREScript{
	
	
	static Logger logger=Logger.getLogger(CheckTestCardUserTrade.class);
	
	/**
	 * 返回true 表示拦截  ，false表示不拦截
	 */
	private static final long serialVersionUID = 1L;
	
	 public boolean run(IData databus, BreRuleParam ruleParam) throws Exception{
		 
		        boolean bResult = false;
		        // 判断用户资料是否存在
		        if (!RuleUtils.existsUserById(databus))
		        {
		            return bResult;
		        }
		        
		        if(CSBizBean.getVisit().getStaffId().indexOf("CREDIT") > -1)//信控过来不要做校验
		        {
		            return false;
		        }
		        
	   			String serialNumber = databus.getString("SERIAL_NUMBER");
	   			IDataset  userInfo=UserInfoQry.getUserInfoBySerailNumber("0", serialNumber);
	   			
	   			if(IDataUtil.isNotEmpty(userInfo)){
	   				String strUserId=userInfo.getData(0).getString("USER_ID");
	   			    IDataset callset=UserOtherInfoQry.getUserOther(strUserId, "TEST_CARD_USER");
		   			if(IDataUtil.isNotEmpty(callset)){
				   		   String rsrvValue=callset.getData(0).getString("RSRV_VALUE");
				   		   if("0".equals(rsrvValue)){
				   			   //限制办理渠道
				   			   //权限判断
					           boolean testcardprTradepriv = StaffPrivUtil.isFuncDataPriv(this.getVisit().getStaffId(), "TESTCARDPR_TRADEPRIV");
					           logger.debug("----------testcardprTradepriv---------------------"+testcardprTradepriv+","+this.getVisit().getStaffId());
					           if(!testcardprTradepriv){
				   				   //无权限(拦截)
				   				   bResult=true;
				   			   }
				   		   }
		   			}
	   			}
	   	     if (logger.isDebugEnabled())
	   	        logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<CheckTestCardUserTrade " + bResult + "<<<<<<<<<<<<<<<<<<<");	   			
	        return bResult;
	   }
	 
}
