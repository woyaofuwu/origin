
package com.asiainfo.veris.crm.order.soa.person.rule.run.modifycustinfo;
 

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustomerInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

/**
 * 判定是否黑名单用户 
 * REQ201510090022 关于新建黑名单库的需求
 * chenxy3 20151022
 * 客户资料变更、过户、复机
 */
public class CheckSnIfBlackListUser extends BreBase implements IBREScript
{
	private static Logger logger = Logger.getLogger(CheckSnIfBlackListUser.class);
     
    public boolean run(IData databus, BreRuleParam param) throws Exception
    {	
    	String xChoiceTag = databus.getString("X_CHOICE_TAG");//0-查询时校验 1=提交时候校验

        if (StringUtils.isBlank(xChoiceTag) || StringUtils.equals("0", xChoiceTag))// 0-查询时校验
	    {
        	String userId = databus.getString("USER_ID");
        	String serialNumber = databus.getString("SERIAL_NUMBER");
	    	String tradeTypeCode = databus.getString("TRADE_TYPE_CODE");  
	    	String psptid=databus.getString("PSPT_ID","");  
	    	IDataset commparas=CommparaInfoQry.getCommparaAllColByParser("CSM","1121", tradeTypeCode, "0898");
	        if(commparas!=null && commparas.size()>0 && !"".equals(psptid)){ 
	        	String code="0";//编码，0=非黑名单； 9=黑名单用户
	        	String msg="本次办理不成功。您的证件名下的手机号码";//黑名单用户拼串：您的证件名下的手机号码***********已欠费*元，手机号码***********已欠费*元 ，请及时缴费。
	        	
	        	/**
	        	 * REQ201604290001 关于调整黑名单系统功能的需求-20160405
	        	 * chenxy3 20160606
	        	 * */
	        	if("60".equals(tradeTypeCode)){
	        		String custId="";
	        		String acctTag="";
	        		String userSetInfo=databus.getString("TF_F_USER","");
	        		if(!"".equals(userSetInfo)){
	        			IDataset userset=new DatasetList(userSetInfo);
	        			custId=userset.getData(0).getString("CUST_ID","");
	        			acctTag=userset.getData(0).getString("ACCT_TAG","");
	        			if("0".equals(acctTag)){
	        				return false;
	        			}
	        		}
	        		
	        		IDataset custset=CustomerInfoQry.getCustInfoByCustidAndPsptid(custId,psptid);
	        		if(custset!=null && custset.size()>0){
	        			String isRealName=custset.getData(0).getString("IS_REAL_NAME","");
	        			if("1".equals(isRealName)){
	        				return false;
	        			}
	        		}
	        	}
	        	
                //变更主产品时候才处理 调用接口
	        	IData inparam=new DataMap();
	        	inparam.put("PSPT_ID", psptid);
	        	IDataset callSet=AcctCall.qryBlackListByPsptId(inparam);
	        	
	        	//*******************测试用**************************
//	        	IData input = new DataMap();
//	        	input.put("PSPT_ID", psptid); 
//	        	SQLParser parser = new SQLParser(input); 
//	      	  
//	            parser.addSQL(" select t.*  from  TF_F_USER_BLACKLIST_test t");  
//	            parser.addSQL(" where t.pspt_id = :PSPT_ID "); 
//	            callSet= Dao.qryByParse(parser); 
	            //*******************测试用**************************
	        	String fee="0";
	            String feeOther="";
	            if (IDataUtil.isNotEmpty(callSet) )
	            {
	            	for(int i=0;i<callSet.size();i++){
	            		IData blackInfo=callSet.getData(i);
	            		String owe_sn=blackInfo.getString("SERIAL_NUMBER","");
	            		String owe_fee=blackInfo.getString("OWE_FEE","0");
	            		if(owe_fee!=null && !"0".equals(owe_fee)){
	            			code="9";
	            			msg+=""+owe_sn+"已欠费"+owe_fee+"元。";
	            			if(!"".equals(owe_fee) && owe_sn.equals(serialNumber)){
	            				fee=owe_fee;
	            			}
	            			feeOther=feeOther+"号码"+owe_sn+"欠费"+owe_fee+"元;";
	            		}
	            		if(i> 45 && i<callSet.size()){
	            			feeOther = feeOther + "未完，不一一展示。";
	            			break;
	            		}
	            	} 
	            	if("9".equals(code)){
	            		msg+="请及时缴费，以便正常使用该证件办理业务。";
	            		String tipsMsg=msg;
	            		//插黑名单日志表
	            		IData insData=new DataMap();
	            		insData.put("USER_ID",userId); 
	                	insData.put("SERIAL_NUMBER",serialNumber);   
	                	insData.put("PSPT_ID",psptid);         
	                	insData.put("IN_MODE_CODE",CSBizBean.getVisit().getInModeCode());     
	                	insData.put("UPDATE_STAFF_ID",CSBizBean.getVisit().getStaffId());  
	                	insData.put("UPDATE_DEPART_ID",CSBizBean.getVisit().getDepartId());      
	                	insData.put("TRADE_TYPE_CODE",tradeTypeCode);  
	                	insData.put("FEE", fee);              
	                	insData.put("OTHER_FEE", feeOther);  
	            		CSAppCall.call("SS.BlackUserManageSVC.insertBlackUserCheckLogInfo", insData);
	            		
	            		//阻止
		   	            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 151022123, tipsMsg);
	            	}
	            }
	        }  
	    }
        return false;
    } 
}
