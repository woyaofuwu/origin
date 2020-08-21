
package com.asiainfo.veris.crm.order.soa.person.busi.changecustowner.order;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset; 
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.AcctTimeEnv;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.AcctTimeEnvManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeAcctInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.ftthmodermapply.FTTHModermApplyBean;

/** 
 * @ClassName: ChangeCustExpireSVC
 * @Description: 过户完工1分钟后，等待新的账户同步到账务，需要调账务接口将FTTH光猫原用户的账户金额转到新用户
 * @version: v1.0.0
 * @author: chenxy3 
 */
public class ChangeCustExpireSVC extends CSBizService
{
	private static Logger logger = Logger.getLogger(ChangeCustExpireSVC.class);
	public IDataset dealExpire(IData mainTrade) throws Exception
	{
		IDataset result = new DatasetList();
		// TODO Auto-generated method stub
        IData param = new DataMap();
        String tradeId = mainTrade.getString("TRADE_ID");
        String tradeTypeCode=mainTrade.getString("TRADE_TYPE_CODE");
        String serialNumber = mainTrade.getString("SERIAL_NUMBER");
        String userId = mainTrade.getString("USER_ID");
        String newCustId = mainTrade.getString("CUST_ID");
        String newEparchyCode = mainTrade.getString("EPARCHY_CODE");
        String newCityCode = mainTrade.getString("CITY_CODE"); 
        
        if("100".equals(tradeTypeCode)){
	        FTTHModermApplyBean bean= BeanManager.createBean(FTTHModermApplyBean.class);
	        param.put("SERIAL_NUMBER", serialNumber);
	        IDataset users=bean.getUserModermInfo(param);
	        //考虑到可能用户操作了申领光猫，但是还没有串号，但是已经扣钱，因此只要有押金，就允许办理
	        String deposit="";
	        if(users!=null && users.size()>0){
	        	//deposit=users.getData(0).getString("RSRV_STR2","");
	        	//调账务提供的接口将原来用户的宽带光猫存折的钱转移到新的用户（注意过户业务可能生成新的账户）；
	        	
	        	String oldUser=mainTrade.getString("RSRV_STR8");
	        	String oldName=oldUser.substring(0,oldUser.indexOf(","));
	        	String oldPsptId=oldUser.substring(oldUser.lastIndexOf(",")+1);
	        	//获取新旧ACCT_ID
	        	IDataset tradeAcct=TradeAcctInfoQry.getTradeAccountByTradeId(tradeId);
	        	//取新用户的默认账户ACCT_ID
	        	String newAcctId= "";
	        	String oldAcctId="";
	        	 if(IDataUtil.isNotEmpty(tradeAcct)){
	        		newAcctId=tradeAcct.getData(0).getString("ACCT_ID"); 
	 	        	oldAcctId=tradeAcct.getData(0).getString("RSRV_STR3");
			      }
	        	
	        	if(newAcctId!=null && oldAcctId!=null && !newAcctId.equals(oldAcctId)){
			        String oldCustId=tradeAcct.getData(0).getString("RSRV_STR1");  
		        	
		        	IData param2=new DataMap();
			        param2.put("CUST_ID", oldCustId); 
			        param2.put("PSPT_ID", oldPsptId); 
			        //取原用户的ACCT_ID
			        //IDataset old_accts=AcctInfoQry.qryAcctInfoByPsptid(param2);  
			        String oldEparchyCode="";
			        String oldCityCode="";
			        IDataset custInfo=bean.getCustByIdPsptid(param2);
			        if(IDataUtil.isNotEmpty(custInfo)){
			        	oldEparchyCode=custInfo.getData(0).getString("EPARCHY_CODE");
				        oldCityCode=custInfo.getData(0).getString("CITY_CODE"); 
			        }
			     
			        IData callParam=new DataMap();
			        callParam.put("SERIAL_NUMBER", serialNumber);
			        callParam.put("USER_ID_NEW", userId); 
			        callParam.put("CUST_ID_NEW", newCustId);
			        callParam.put("ACCT_ID_NEW", newAcctId);	        
			        callParam.put("EPARCHY_CODE_NEW", newEparchyCode);
			        callParam.put("CITY_CODE_NEW", newCityCode);
			        
			        callParam.put("USER_ID", userId); 
			        callParam.put("CUST_ID", oldCustId);
			        callParam.put("ACCT_ID", oldAcctId);	        
			        callParam.put("EPARCHY_CODE", oldEparchyCode);
			        callParam.put("CITY_CODE", oldCityCode);
			        
			        //接口调用：将原账户“宽带光猫押金存折”转移到新账户（过户）
			        /*
		        	入参： USER_ID -- 原用户ID
				          ACCT_ID -- 原账户ID
				          CUST_ID -- 原客户ID
				          USER_ID_NEW -- 新用户ID（与USER_ID一样）
				          ACCT_ID_NEW -- 新账户ID
				          CUST_ID_NEW -- 新客户ID
				          EPARCHY_CODE -- 原账户区域编码
				          EPARCHY_CODE_NEW -- 新账户区域编码
				          CITY_CODE -- 原城市编码
				          CITY_CODE_NEW -- 新城市编码
				          SERIAL_NUMBER -- 原手机号码
				         
				         出参：  RESULT_CODE0 -- 0：成功 1：失败
			              RESULT_INFO -- 返回信息
		        	*/
			        IData callRtn= AcctCall.passToNewAccount(callParam);
			        String resultCode=callRtn.getString("RESULT_CODE","");//0-成功 1-失败
			        if(!"".equals(resultCode) && "0".equals(resultCode)){
		    			// 成功！ 处理other表  将光猫信息重新开始  					
						bean.updateUserOtherStartdate(callParam);
		    		}else{
		    			CSAppException.appError("61319", "调用接口AM_CRM_TransFeeAcctFTTH转存押金错误:"+callRtn.getString("RESULT_INFO"));
		    		} 
	        	}
	        } 
        }
		return result;
	}
}