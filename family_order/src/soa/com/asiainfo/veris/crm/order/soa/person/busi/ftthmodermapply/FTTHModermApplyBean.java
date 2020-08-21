
package com.asiainfo.veris.crm.order.soa.person.busi.ftthmodermapply;
import org.apache.log4j.Logger;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.database.util.SQLParser;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.WidenetException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.acct.AcctInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.broadband.WidenetInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.ftthbusimodemapply.FTTHBusiModemApplyBean;

/**
 * REQ201505210004关于新增FTTH光猫办理流程的需求
 * CHENXY3
 * 2015-06-08
 * */
public class FTTHModermApplyBean extends CSBizBean
{
	private static final Logger log = Logger.getLogger(FTTHModermApplyBean.class);
	/**
	 * 获取用户是否存在光猫信息TF_F_USER_OTHER
	 * 
	 * */
	public static IDataset getUserModermInfo(IData inParam) throws Exception
    {
		IData param = new DataMap();
        param.put("SERIAL_NUMBER", inParam.getString("SERIAL_NUMBER"));
        IDataset userModerms = Dao.qryByCode("TF_F_USER_OTHER", "SEL_USER_OTHER_FTTHMODERM", param);
        return userModerms;
    }
	
	/**
	 * 获取用户是否存在FTTH宽带信息
	 * TF_F_USER_PRODUCT
	 * 
	 * */
//	public static IDataset getUserFTTHProd(IData inParam) throws Exception
//    {
//		IData param = new DataMap();
//        param.put("SERIAL_NUMBER", inParam.getString("SERIAL_NUMBER"));
//        IDataset userModerms = Dao.qryByCode("TF_F_USER_PRODUCT", "SEL_FTTH_BY_SERIALNUMBER", param);
//        return userModerms;
//    }
	
	/**
	 * 获取用户是否存在FTTH宽带信息 
	 * TF_B_TRADE TF_BH_TRADE
	 * 
	 * */
	public static IDataset getUserFTTHProdWWG(IData inParam) throws Exception
    {
		IData param = new DataMap();
        param.put("SERIAL_NUMBER", inParam.getString("SERIAL_NUMBER"));
        IDataset userModerms = Dao.qryByCode("TF_BH_TRADE", "SEL_TRADE_FTTH_BY_SERIALNUMBER", param, Route.getJourDb(BizRoute.getTradeEparchyCode()));
        return userModerms;
    }
	
	/**
	 * 获取用户是否存在宽带1+信息
	 * TF_F_USER_SALE_ACTIVE
	 * 
	 * */
	public static IDataset getUserWilenInfoAct(IData inParam) throws Exception
    {
		IData param = new DataMap();
        param.put("SERIAL_NUMBER", inParam.getString("SERIAL_NUMBER"));
        IDataset userModerms = Dao.qryByCode("TF_F_USER_SALE_ACTIVE", "SEL_ACTIVE_WILEN_BY_SERINUM", param);
        return userModerms;
    }
	
	/**
	 * 获取用户是否存在宽带1+信息
	 * TF_F_USER_SALEACTIVE_BOOK
	 * 
	 * */
	public static IDataset getUserWilenInfoBook(IData inParam) throws Exception
    {
		IData param = new DataMap();
        param.put("SERIAL_NUMBER", inParam.getString("SERIAL_NUMBER"));
        IDataset userModerms = Dao.qryByCode("TF_F_USER_SALEACTIVE_BOOK", "SEL_BOOK_WILEN_BY_SERINUM", param);
        return userModerms;
    }
	
	/**
	 * 获取用户是否存在宽带1+信息
	 * TF_F_USER_SALEACTIVE_BOOK
	 * 
	 * */
	public static IDataset getCustByIdPsptid(IData inParam) throws Exception
    {
		IData param = new DataMap();
        param.put("CUST_ID", inParam.getString("CUST_ID"));
        param.put("PSPT_ID", inParam.getString("PSPT_ID"));
        IDataset userModerms = Dao.qryByCode("TF_F_CUSTOMER", "SEL_BY_CUSTID_PSPTID", param);
        return userModerms;
    }
	
	/**
	 * 判断用户是否办理FTTH宽带
	 * 如果存在FTTH宽带，判断是否存在宽带1+活动
	 * 如果存在宽带1+活动返回1
	 * 如果只存在FTTH宽带返回2
	 * 不存在返回0
	 * */
	public  String getPayMoneyType(IData inParam) throws Exception{
		boolean isExist=false;
		String payType="0";//该状态对应TD_S_COMMPARA表的param_attr=6131的param_code
//		IDataset wilens=this.getUserFTTHProd(inParam);
		
		String serialNumber = inParam.getString("SERIAL_NUMBER");
		
		if (!serialNumber.startsWith("KD_"))
		{
		    serialNumber = "KD_" + serialNumber;
		}
		
		IDataset widenetInfos = WidenetInfoQry.getUserWidenetInfoBySerialNumber(serialNumber);
		
		if (IDataUtil.isEmpty(widenetInfos))
		{
		    CSAppException.apperr(WidenetException.CRM_WIDENET_4);
		}
		
		//3,5分别为移动FTTH,铁通FTTH宽带
		if("3".equals(widenetInfos.getData(0).getString("RSRV_STR2"))
		        || "5".equals(widenetInfos.getData(0).getString("RSRV_STR2")))
		{
			isExist=true;
		}else{
		    IDataset dataset = getUserFTTHProdWWG(inParam);
			if(dataset!=null && dataset.size()>0){
				isExist=true;
			}
		}
		
		if(isExist){
			IDataset wilen1s= getUserWilenInfoAct(inParam);
			if(wilen1s!=null && wilen1s.size()>0){
				payType="1";//存在宽带1+，状态为1，扣100块押金
			}else{
				wilen1s= getUserWilenInfoBook(inParam);
				if(wilen1s!=null && wilen1s.size()>0){
					payType="1";//存在宽带1+，状态为1，扣100块押金
				}else{
					payType="2";//不存在宽带1+，状态为2，扣200块押金
				}
			}
		}
		return payType;
	}
	
	/**
	 * 
	 * 更新TF_F_USER_OTHER用户光猫串号
	 * */
    public void updModermNumber(IData inParam) throws Exception
    {
    	IData param = new DataMap();
        param.put("RES_NO", inParam.getString("RES_NO"));
        param.put("SERIAL_NUMBER", inParam.getString("SERIAL_NUMBER"));
        Dao.executeUpdateByCodeCode("TF_F_USER_OTHER", "UPD_USER_OTHER_FTTHMODERM", param); 
    }    
    
    /**
	 * 1、获取“CRM > 个人业务 > 宽带业务 > FTTH宽带业务 > FTTH宽带用户开户”开户失败的用户
	 * 根据用户的TF_F_USER_OTHER表，判断用户（手机号码）是否申领了光猫；
	 * 如果是申领了光猫则调账务接口，将宽带光猫押金存折里的钱转到现金存折；
	 * 结束用户的TF_F_USER_OTHER资料  
	 * 
	 * REQ201604060008 光猫申领撤单调转账接口后缴费日志表办理工号显示SUPERUSR优化
	 * 20160411 
	 * 1、退押金要求传工号及部门，为申领时候的操作员及部门。
	 * 2、退光猫要求传撤单的铁通工号部门，即A记录的下的9记录的工号。
	 * */
    public void checkWilenFailUser(IData inParam) throws Exception
    {
    	IDataset users=new DatasetList();
    	try{
	    	for(int k=1;k<13;k++){
	    		IData parts=new DataMap();
	    		String part=String.format("%02d", k);
	    		parts.put("PARTITIONCODE", part);
	    		users.addAll(this.getWilenFailUser(parts));//获取开户失败用户信息，结果集是否含判断领取光猫rsrv_str1
	    	}
    	}catch(Exception e){
    		//log.info("(e);
    	}
    	//取临时表数据
    	IData tempData=new DataMap();
    	tempData.put("DEAL_TAG","0");
    	IDataset tempUsers=this.getFtthTempBhTrade(tempData);
    	users.addAll(tempUsers);
    	if(users!=null && users.size()>0){
	   			for(int i=0;i<users.size();i++){
	   				IData user=users.getData(i);
	   				String tradeId=user.getString("TRADE_ID");
	   				/**取A记录下的9记录的信息，取得撤单成功的工号及部门，用于退光猫*/
	   				String rtnStaffId="";//必传--退光猫获取A记录下9记录的操作员 
   					String rtnDepartId="";//必传 --退光猫获取A记录下9记录的操作员部门
	   				IDataset returnTrades=this.getFtthReturnTrade(user);
	   				if(returnTrades!=null && returnTrades.size()>0){
	   					rtnStaffId=returnTrades.getData(0).getString("TRADE_STAFF_ID",user.getString("TRADE_STAFF_ID"));//必传--退光猫获取A记录下9记录的操作员 
	   					rtnDepartId=returnTrades.getData(0).getString("TRADE_DEPART_ID",user.getString("TRADE_DEPART_ID"));//必传 --退光猫获取A记录下9记录的操作员部门
	   				}
	   				//注意这里取的都是宽带的NUMBER，要替换
	   				String serialNumber=user.getString("SERIAL_NUMBER").replace("KD_", "");
	   				String eparchyCode=user.getString("EPARCHY_CODE");
	   				String cityCode=user.getString("CITY_CODE");
	   				
	   				//判断是否申领了光猫，取TF_F_USER_OTHER表的RSRV_STR1存在串号
	   				IData getParam=new DataMap();
	   				getParam.put("SERIAL_NUMBER", serialNumber);
	   				IDataset userModerms=this.getUserModermInfo(getParam);
	   				//应注意，光猫申领保存时候已经扣押金，而不是在外部掉接口的时候才扣，因此无论是否存在串号都要返还
	   				if(userModerms!=null && userModerms.size()>0){
	   					IData endParams=new DataMap();
		   				String userId=userModerms.getData(0).getString("USER_ID");
		   				String modermNum=userModerms.getData(0).getString("RSRV_STR1","");
		   				String deposit=userModerms.getData(0).getString("RSRV_STR2","");//押金  
		   				String otherTradeId=userModerms.getData(0).getString("TRADE_ID");
		   				String applyStaffId=userModerms.getData(0).getString("STAFF_ID");//必传--退押金时候传申领操作员工号
			   			String applyDepartId=userModerms.getData(0).getString("DEPART_ID");//必传 --退押金时候传申领操作员工号部门
		   				String applyCityCode=cityCode;
			   			String tradeID6131=userModerms.getData(0).getString("TRADE_ID");
			   			IData tradeData=new DataMap();
			   			tradeData.put("TRADE_ID", userModerms.getData(0).getString("TRADE_ID"));
			   			IDataset tradeset=getFtthApplyTradeInfo(tradeData);
			   			if(tradeset!=null && tradeset.size()>0){
			   				applyStaffId=tradeset.getData(0).getString("TRADE_STAFF_ID");
			   				applyDepartId=tradeset.getData(0).getString("TRADE_DEPART_ID");
			   				applyCityCode=tradeset.getData(0).getString("TRADE_CITY_CODE");
			   			}
		   				try{
			   				IData params=new DataMap();
			   					//取默认账户
			   				IDataset accts=AcctInfoQry.qryAcctDefaultIdBySn(serialNumber); 
			   				String acctId=accts.getData(0).getString("ACCT_ID");
			   				String custId=accts.getData(0).getString("CUST_ID");
			   				params.put("USER_ID", userId);
			   				params.put("ACCT_ID",acctId);
			   				params.put("CUST_ID",custId);
			   				params.put("TRADE_FEE",deposit);
			   				params.put("EPARCHY_CODE",eparchyCode);
			   				params.put("CITY_CODE",cityCode);
			   				params.put("SERIAL_NUMBER",serialNumber);
			   				params.put("TRADE_STAFF_ID", applyStaffId);//退押金时候传申领光猫时候的操作员工号部门TF_F_USER_OTHER
	   						params.put("TRADE_DEPART_ID", applyDepartId);//退押金时候传申领光猫时候的操作员工号部门TF_F_USER_OTHER
	   						params.put("TRADE_CITY_CODE", applyCityCode);
	   						params.put("SUB_SYS","RESSERV_TF_RH_SALE_DEAL");
	   						/**
			   					 * 新增特殊权限允许押金为0的情况
			   					 * chenxy3 20160317
			   					 * 因可能存在押金是0，修改成只有大于0的才调账务接口，=0的情况直接模拟成账务接口
			   					 * 已经调用成功的情况。
			   			    * */		
			   				IData callRtn=new DataMap();
			   				if(!"".equals(deposit) && Integer.parseInt(deposit)>0){	
			   					//将“宽带光猫押金存折”转移到现金存折
			   					callRtn=AcctCall.adjustFee(params); 
		   					}else{
		   						callRtn.put("RESULT_CODE", "0");
		   					}
		   					
		   					String resultCode=callRtn.getString("RESULT_CODE","");
		   					if(!"".equals(resultCode)&&"0".equals(resultCode)){
		   						if(!"".equals(modermNum) && !"0".equals(modermNum)){
			   						/**
				   					 * REQ201511190036 关于开发商务宽带业务免押金0元租用光猫界面的需求
				   					 * 退还光猫
				   					 * */ 
			   						IData param=new DataMap();
			   						param.put("SERIAL_NUMBER", serialNumber);//PARA_VALUE1
			   						param.put("TRADE_ID", tradeId);//台账流水
			   						param.put("OTHER_TRADE_ID", otherTradeId);
			   						param.put("RES_NO", modermNum);//终端串号 
			   						param.put("CUST_ID", custId);//客户名称
			   						param.put("TRADE_STAFF_ID", rtnStaffId);//已经修改，取A记录下的9记录的操作员TRADE_STAFF_ID
			   						param.put("TRADE_DEPART_ID", rtnDepartId);//已经修改，取A记录下的9记录的操作员部门TRADE_DEPART_ID
			   						param.put("FTTH_RTN_MODEM", "PERSON");
			   						boolean rtnFlag=this.returnFtthModem(param); 
				   					if(!rtnFlag){
				   						endParams.put("CALL_INFO", "押金返还成功，光猫退还失败！");
				   					}
		   						}
		   						//if接口调用成功，结束用户的TF_F_USER_OTHER资料   					
		   	   					this.updateUserOtherEnddate(params);
		   	   					//更新被捞取的数据，避免下次继续捞取
		   	   					endParams.put("TRADE_ID",tradeId);
		   	   					endParams.put("RSRV_STR10","1");
		   	   					this.updateBhTradeFlag(endParams);
		   	   					
		   	   					//更新临时表标记
				   				if(tempUsers!=null && tempUsers.size()>0){
				   	   				IData tempPara=new DataMap();
					   				tempPara.put("TRADE_ID", tradeId);
					   				tempPara.put("DEAL_TAG", "1");
				   					this.updateFtthTempTrade(tempPara);
				   				}
				   				/**
			   					 * REQ201512160017 光猫押金释放后下发告知短信的开发需求
			   					 * 退押金成功下发短信： 
			   					 * 20151218 chenxy3
			   					 * */  
				   				//新增：可能有0元押金的，那就不发短信
				   				if(!"".equals(deposit) && Integer.parseInt(deposit)>0){
				   					IData smsData = new DataMap();
				   					smsData.put("SERIAL_NUMBER",serialNumber);
				   					smsData.put("USER_ID",userId);
				   					String sysTime=SysDateMgr.getSysTime();
				   					sysTime=sysTime.substring(sysTime.indexOf(":")-2);
				   					String content="尊敬的客户，您之前缴纳的光猫押金"+Integer.parseInt(deposit)*0.01+"元，已于"+SysDateMgr.getSysDateYYYYMMDD().substring(0,4)+"年"+SysDateMgr.getCurMonth()+"月"+SysDateMgr.getCurDay()+"日 "+sysTime+" 释放并存入您的话费账户，请及时查询，详情请咨询当地移动营业厅或拨打10086。中国移动海南公司。";
				   					smsData.put("NOTICE_CONTENT",content);
				   					smsData.put("STAFF_ID","SUPERUSR");
				   					smsData.put("DEPART_ID","36601");
				   					smsData.put("REMARK","FTTH退押金短信");
				   					smsData.put("BRAND_CODE","");
				   					smsData.put("FORCE_START_TIME","");
				   					smsData.put("FORCE_END_TIME","");
				   					smsSent(smsData);
				   				}
		   					}else{
		   						String rtnInfo=callRtn.getString("RESULT_INFO","");
		   						if(!"".equals(rtnInfo)&& rtnInfo.length()>98){
		   							rtnInfo=rtnInfo.substring(0,98);
		   						}
		   						params.put("RESULT_INFO", rtnInfo);
		   						params.put("CALL_INTEFACE", "AM_CRM_TransFeeOutFTTH");
		   						this.updateUserOtherCallFalse(params);
		   						
		   					//更新临时表标记
				   				if(tempUsers!=null && tempUsers.size()>0){
				   	   				IData tempPara=new DataMap();
					   				tempPara.put("TRADE_ID", tradeId);
					   				tempPara.put("DEAL_TAG", "9");
				   					this.updateFtthTempTrade(tempPara);
				   				}
		   					} 
		   				}catch(Exception e){
		   					//log.info("(e);
		   					IData errParam=new DataMap();
		   					errParam.put("USER_ID", userId);
		   					errParam.put("RESULT_INFO", "SS.FTTHModermApplySVC.checkWilenFailUser执行错误。");
		   					errParam.put("CALL_INTEFACE", "");
							this.updateUserOtherCallFalse(errParam);
							//更新被捞取的数据，避免下次继续捞取
	   	   					endParams.put("TRADE_ID",tradeId);
	   	   					endParams.put("RSRV_STR10","99");
	   	   					this.updateBhTradeFlag(endParams);
	   	   					//更新临时表标记
			   				if(tempUsers!=null && tempUsers.size()>0){
			   	   				IData tempPara=new DataMap();
				   				tempPara.put("TRADE_ID", tradeId);
				   				tempPara.put("DEAL_TAG", "9");
			   					this.updateFtthTempTrade(tempPara);
			   				 }
		   				} 
	   				}	 
	   			}
	   		} 
    }
    
    /**
     * 获取开户失败用户信息 
     */
    public static IDataset getWilenFailUser(IData inparams) throws Exception
    {  
        SQLParser parser = new SQLParser(inparams);
    	String PARTITIONCODE=inparams.getString("PARTITIONCODE","");
        parser.addSQL(" select t.*  ");
        parser.addSQL(" from tf_bh_trade PARTITION(PAR_TF_BH_TRADE_"+PARTITIONCODE+") t  ");
        parser.addSQL(" where t.subscribe_STATE='A' ");
        parser.addSQL(" AND T.TRADE_TYPE_CODE='613' "); 
        parser.addSQL(" AND T.ACCEPT_DATE>=trunc(sysdate,'dd') "); 
        parser.addSQL(" AND T.ACCEPT_DATE<trunc(sysdate,'dd')+1 "); 
        parser.addSQL(" AND T.ACCEPT_MONTH=TO_NUMBER(TO_CHAR(SYSDATE,'MM')) "); 
        parser.addSQL(" AND (T.RSRV_STR10 IS NULL OR T.RSRV_STR10 NOT IN ('1','99'))");
        parser.addSQL(" and REPLACE(T.SERIAL_NUMBER, 'KD_', '') IN ");
        parser.addSQL(" (select B.SERIAL_NUMBER ");
        parser.addSQL(" from tf_f_user_other a, TF_F_USER B ");
        parser.addSQL(" where A.USER_ID = B.USER_ID ");
        parser.addSQL(" AND a.rsrv_value_code = 'FTTH' ");
        parser.addSQL(" and b.remove_tag='0' ");
        parser.addSQL(" AND SYSDATE BETWEEN A.START_DATE AND A.END_DATE) ");
  
    	return Dao.qryByParse(parser,Route.getJourDb(BizRoute.getTradeEparchyCode()));
    }
    
    /**
     * 退押金要求传的工号是光猫申领时候的工号及部门
	 * 取工单记录的相关信息
     */
    public static IDataset getFtthApplyTradeInfo(IData inparams) throws Exception
    {  
        
        SQLParser parser = new SQLParser(inparams); 
        parser.addSQL("select t.* ");
        parser.addSQL("from tf_bh_trade t ");
        parser.addSQL("where t.trade_id=:TRADE_ID "); 
    	return Dao.qryByParse(parser, Route.getJourDb(BizRoute.getTradeEparchyCode()));
    }
    
    /**
     * 获取开户失败用户信息 
     */
    public static IDataset getFtthTempBhTrade(IData inparams) throws Exception
    {  
        
        SQLParser parser = new SQLParser(inparams); 
        parser.addSQL(" select t2.*  ");
        parser.addSQL(" from uop_crm1.tf_bh_trade_ftth t ,tf_bh_trade t2");
        parser.addSQL(" where t.trade_id=t2.trade_id and t.deal_tag=:DEAL_TAG "); 
        parser.addSQL(" and t2.subscribe_STATE='A' and t2.TRADE_TYPE_CODE='613' "); 
    	return Dao.qryByParse(parser, Route.getJourDb(BizRoute.getTradeEparchyCode()));
    }
    
    /**
     * 获取开户失败用户信息 
	 * 即A记录下相同TRADE_ID的9的记录
     */
    public static IDataset getFtthReturnTrade(IData inparams) throws Exception
    {  
        
        SQLParser parser = new SQLParser(inparams); 
        parser.addSQL(" select t2.*  ");
        parser.addSQL(" from tf_bh_trade t2");
        parser.addSQL(" where t2.trade_id=:TRADE_ID "); 
        parser.addSQL(" and t2.subscribe_STATE='9' and t2.TRADE_TYPE_CODE='613' "); 
    	return Dao.qryByParse(parser, Route.getJourDb(BizRoute.getTradeEparchyCode()));
    }
    
    /**
     * 终止用户的光猫领取记录     *  
     * */
    public static void updateFtthTempTrade(IData params) throws Exception
    {
    	IData param = new DataMap();
    	param.put("TRADE_ID", params.getString("TRADE_ID")); 
    	param.put("DEAL_TAG", params.getString("DEAL_TAG")); 
    	StringBuilder sql = new StringBuilder(1000);
    	
    	sql.append(" update tf_bh_trade_ftth t ");
    	sql.append(" set t.DEAL_TAG=:DEAL_TAG,T.DEAL_TIME=SYSDATE ");
    	sql.append(" where  t.trade_id=:TRADE_ID");  
        Dao.executeUpdate(sql, param);
    }
    
    /**
	 * 2、获取申请光猫已满3年的用户
	 * 根据用户的TF_F_USER_OTHER表，判断用户（手机号码）是否申领了光猫；
	 * 如果是申领了光猫则调账务接口，将宽带光猫押金存折里的钱转到现金存折；
	 * 结束用户的TF_F_USER_OTHER资料 
	 * 20170711修改，循环改到AEE处理
	 * */
   public void checkThreeYearsUser(IData user) throws Exception
   {
//   		IDataset users=this.getThreeYearsUser(new DataMap());//获取申请光猫已满3年的用户
//   		
//   		if(users!=null && users.size()>0){
//   			for(int i=0;i<users.size();i++){
//   				IData user=users.getData(i);
   				String serialNumber=user.getString("SERIAL_NUMBER");
   				String userid=user.getString("USER_ID");
   				String custid=user.getString("CUST_ID");
   				String modermNum=user.getString("RSRV_STR1");
   				String deposit=user.getString("RSRV_STR2","");//押金
   				String eparchycode=user.getString("EPARCHY_CODE"); 
   		    	String citycode=user.getString("CITY_CODE"); 
   		    	String applyStaffId=user.getString("STAFF_ID");//必传--退押金时候传申领操作员工号
	   			String applyDepartId=user.getString("DEPART_ID");//必传 --退押金时候传申领操作员工号部门
	   			String applyCityCode=citycode;
	   			IData tradeData=new DataMap();
	   			tradeData.put("TRADE_ID", user.getString("TRADE_ID",""));
	   			IDataset tradeset=getFtthApplyTradeInfo(tradeData);
	   			if(tradeset!=null && tradeset.size()>0){
	   				applyStaffId=tradeset.getData(0).getString("TRADE_STAFF_ID");
	   				applyDepartId=tradeset.getData(0).getString("TRADE_DEPART_ID");
	   				applyCityCode=tradeset.getData(0).getString("TRADE_CITY_CODE");
	   			}
   				try{	   				
	   				//3、获取默认账户  （acct_id)
	   		    	IDataset accts=AcctInfoQry.qryAcctDefaultIdBySn(serialNumber);
	   		    	String acctId=accts.getData(0).getString("ACCT_ID"); 
	   				//if(modermNum!=null && !"".equals(modermNum)){
	   		    	/**
	   		    	 * 押金可能有0的情况，改成只有>0才退押金及发短信,因为这里即便退了也不终止记录，只是
	   		    	 * 将押金置为0，因此如果已经是0的什么都不做。
	   		    	 * chenxy3 20160318
	   		    	 * */
	   		    	if(!"".equals(deposit) && Integer.parseInt(deposit)>0){
	   					IData params=new DataMap(); 
	   					params.put("USER_ID", userid);
	   					params.put("ACCT_ID", acctId);
	   					params.put("CUST_ID", custid);
	   					params.put("TRADE_FEE", deposit);
	   					params.put("EPARCHY_CODE", eparchycode);
	   					params.put("CITY_CODE", citycode);
	   		    		params.put("SERIAL_NUMBER", serialNumber); 
	   		    		params.put("TRADE_STAFF_ID", "HNSJ0000");//退押金时候传申领光猫时候的操作员工号部门TF_F_USER_OTHER
   						params.put("TRADE_DEPART_ID", "36601");//退押金时候传申领光猫时候的操作员工号部门TF_F_USER_OTHER
   						params.put("TRADE_CITY_CODE","HNSJ");
   						params.put("SUB_SYS","RESSERV_TF_RH_SALE_DEAL");
   						//调用接口，将【押金】——>【现金】
	   					IData callRtn=AcctCall.adjustFee(params);//将宽带光猫押金存折里的钱转到现金存折
	   					String callRtnType=callRtn.getString("RESULT_CODE","");//0-成功 1-失败
	   					if(!"".equals(callRtnType)&&"0".equals(callRtnType)){
		   					//if接口调用成功，只清空押金					
		   					this.updateUserOtherDeposit(params); 
		   					
		   					/**
		   					 * REQ201512160017 光猫押金释放后下发告知短信的开发需求
		   					 * 退押金成功下发短信： 
		   					 * 20151218 chenxy3
		   					 * */ 
		   		            
		   		            //插短信
		   					IData smsData = new DataMap();
		   					smsData.put("SERIAL_NUMBER",serialNumber);
		   					smsData.put("USER_ID",userid);
		   					String sysTime=SysDateMgr.getSysTime();
		   					sysTime=sysTime.substring(sysTime.indexOf(":")-2);
		   					String content="尊敬的客户，您之前缴纳的光猫押金"+Integer.parseInt(deposit)*0.01+"元，已于"+SysDateMgr.getSysDateYYYYMMDD().substring(0,4)+"年"+SysDateMgr.getCurMonth()+"月"+SysDateMgr.getCurDay()+"日 "+sysTime+" 释放并存入您的话费账户，请及时查询，详情请咨询当地移动营业厅或拨打10086。中国移动海南公司。";
		   					smsData.put("NOTICE_CONTENT",content);
		   					smsData.put("STAFF_ID","SUPERUSR");
		   					smsData.put("DEPART_ID","36601");
		   					smsData.put("REMARK","FTTH退押金短信");
		   					smsData.put("BRAND_CODE","");
		   					smsData.put("FORCE_START_TIME","");
		   					smsData.put("FORCE_END_TIME","");
		   					smsSent(smsData);
	   					}else{
	   						String rtnInfo=callRtn.getString("RESULT_INFO","");
	   						if(!"".equals(rtnInfo) && rtnInfo.length()>98){
	   							rtnInfo=rtnInfo.substring(0,98);
	   						}
	   						params.put("RESULT_INFO", rtnInfo);
	   						params.put("CALL_INTEFACE", "AM_CRM_TransFeeOutFTTH");
	   						this.updateUserOtherCallFalse(params);
	   					} 
	   				}
   				}catch(Exception e){
   					//log.info("(e);
   					String errInfo=e.getMessage().substring(0, e.getMessage().length() > 400 ? 400 : e.getMessage().length());
   					IData errParam=new DataMap();
   					errParam.put("USER_ID", userid);
   					errParam.put("RESULT_INFO", errInfo);
   					errParam.put("CALL_INTEFACE", "");
					this.updateUserOtherCallFalse(errParam);
   				}
   			 
    }
    
    /**
     * 获取申请光猫已满3年的用户
     * （未完工）SEL_FTTH_FULL3YEAR
     */
    public static IDataset getThreeYearsUser(IData inparams) throws Exception
    {  
        IData param = new DataMap(); 
        IDataset users = Dao.qryByCode("TF_F_USER_OTHER", "SEL_FTTH_FULL3YEAR", param);
        return users;
    }
    
    /**
     * 终止用户的光猫领取记录     *  
     * */
    public static void updateUserOtherEnddate(IData params) throws Exception
    {
    	IData param = new DataMap();
    	param.put("USER_ID", params.getString("USER_ID")); 
    	
    	StringBuilder sql = new StringBuilder(1000);
    	
    	sql.append(" update TF_F_USER_OTHER t ");
    	sql.append(" set t.end_date=sysdate ,t.UPDATE_TIME=sysdate,t.rsrv_str4=:CALL_INFO");
    	sql.append(" where  t.rsrv_value_code = 'FTTH' "); 
    	sql.append(" and sysdate between t.start_date and t.end_date ");
    	sql.append(" and t.user_id=:USER_ID ");
        Dao.executeUpdate(sql, param);
    }
    
    /**
     * 满3年退押金问题：考虑到宽带拆机还要用这条记录，不允许终止     *  
     * 只是把押金的金额改了清空。
     * chenxy3 20160126
     * */
    public static void updateUserOtherDeposit(IData params) throws Exception
    {
    	IData param = new DataMap();
    	param.put("USER_ID", params.getString("USER_ID")); 
    	
    	StringBuilder sql = new StringBuilder(1000);
    	
    	sql.append(" update TF_F_USER_OTHER t ");
    	sql.append(" set t.rsrv_str2='0' ,t.UPDATE_TIME=sysdate,t.rsrv_str7='2',t.rsrv_str30='满3年退押金成功',t.remark='满3年退押金'");
    	sql.append(" where  t.rsrv_value_code = 'FTTH' "); 
    	sql.append(" and sysdate between t.start_date and t.end_date ");
    	sql.append(" and t.user_id=:USER_ID ");
        Dao.executeUpdate(sql, param);
    }
    
    /**
     * 对于宽带开户失败的用户，捞取一次后必须打上标记，不然会反复捞取。
     * 记录于TF_BH_TRADE的RSRV_STR10字段
     * */
    public static void updateBhTradeFlag(IData params) throws Exception
    {
    	IData param = new DataMap();
    	param.put("TRADE_ID", params.getString("TRADE_ID")); 
    	param.put("RSRV_STR10", params.getString("RSRV_STR10"));
    	StringBuilder sql = new StringBuilder(1000);
    	
    	sql.append(" UPDATE tf_bh_trade t ");
    	sql.append(" SET T.RSRV_STR10=:RSRV_STR10");
    	sql.append(" where t.subscribe_STATE='A' AND T.TRADE_TYPE_CODE='613'");
    	sql.append(" AND t.trade_id=:TRADE_ID");
        Dao.executeUpdate(sql, param, Route.getJourDb(BizRoute.getTradeEparchyCode()));
    }
    
    
    /**
     * 更新调用接口失败信息  *  
     * */
    public static void updateUserOtherCallFalse(IData params) throws Exception
    {
    	IData param = new DataMap();
    	param.put("USER_ID", params.getString("USER_ID")); 
    	
    	param.put("CALL_INTEFACE", params.getString("CALL_INTEFACE")); 
    	param.put("RESULT_INFO", params.getString("RESULT_INFO")); 
    	StringBuilder sql = new StringBuilder(1000);
    	
    	sql.append(" update TF_F_USER_OTHER t ");
    	sql.append(" set t.rsrv_str29=:CALL_INTEFACE,t.rsrv_str30=:RESULT_INFO ");
    	sql.append(" where  t.rsrv_value_code = 'FTTH' "); 
    	sql.append(" and sysdate between t.start_date and t.end_date ");
    	sql.append(" and t.user_id=:USER_ID ");
        Dao.executeUpdate(sql, param);
    }
    
    /**
     * 修改用户的开始时间为当前时间     *  
     * */
    public static void updateUserOtherStartdate(IData params) throws Exception
    {
    	IData param = new DataMap();
    	param.put("USER_ID", params.getString("USER_ID")); 
    	
    	StringBuilder sql = new StringBuilder(1000);
    	
    	sql.append(" update TF_F_USER_OTHER t ");
    	sql.append(" set t.start_date=sysdate ");
    	sql.append(" where  t.rsrv_value_code = 'FTTH' "); 
    	sql.append(" and sysdate between t.start_date and t.end_date ");
    	sql.append(" and t.user_id=:USER_ID ");
        Dao.executeUpdate(sql, param);
    } 
    
    /**
	 * REQ201511190036 关于开发商务宽带业务免押金0元租用光猫界面的需求
	 * 单独退还光猫接口
	 * 要求tf_bh_trade_ftth t 插入记录DEAL_TAG="5"
	 *     tf_bh_trade 存在A的记录。
	 * */ 
	public IDataset getTempFtthUserReturnModem(IData inparam) throws Exception{
		//取临时表数据
		IDataset rtnFlagset=new DatasetList();
    	IData tempData=new DataMap();
    	tempData.put("DEAL_TAG","5");
    	IDataset tempUsers=this.getFtthTempBhTrade(tempData);
    	for(int i=0;i<tempUsers.size();i++){
    		IData user=tempUsers.getData(i);
			String tradeId=user.getString("TRADE_ID");
			String custId=user.getString("CUST_ID");
			//注意这里取的都是宽带的NUMBER，要替换
			String serialNumber=user.getString("SERIAL_NUMBER").replace("KD_", "");
			String eparchyCode=user.getString("EPARCHY_CODE");
			String cityCode=user.getString("CITY_CODE");
			//判断是否申领了光猫，取TF_F_USER_OTHER表的RSRV_STR1存在串号
			IData getParam=new DataMap();
			getParam.put("SERIAL_NUMBER", serialNumber);
			IDataset userModerms=this.getUserModermInfo(getParam);
			//应注意，光猫申领保存时候已经扣押金，而不是在外部掉接口的时候才扣，因此无论是否存在串号都要返还
			if(userModerms!=null && userModerms.size()>0){
				IData endParams=new DataMap();
   				String userId=userModerms.getData(0).getString("USER_ID");
   				String modermNum=userModerms.getData(0).getString("RSRV_STR1");//串号
   				IData param=new DataMap();
   				param.put("SERIAL_NUMBER", serialNumber);//PARA_VALUE1
   				param.put("TRADE_ID", tradeId);//台账流水
   				param.put("RES_NO", modermNum);//终端串号 
   				param.put("CUST_ID", custId);//客户名称
   				param.put("UPDATE_STAFF_ID", user.getString("UPDATE_STAFF_ID"));//销售员工 
				param.put("UPDATE_DEPART_ID", user.getString("UPDATE_DEPART_ID"));//已经修改，取A记录的操作员部门
   				boolean rtnFlag=this.returnFtthModem(param); 
   				rtnFlagset.add(rtnFlag);
   				IData updData=new DataMap();
   				if(rtnFlag){ 
   					updData.put("TRADE_ID", tradeId);
   					updData.put("DEAL_TAG", "1");
   					this.updateFtthTempTrade(updData); 
   				}else{
   					updData.put("TRADE_ID", tradeId);
   					updData.put("DEAL_TAG", "9");
   					this.updateFtthTempTrade(updData);
   				}
			}    
    	}    	
    	return rtnFlagset;
	}
	 
    /**
		 * REQ201511190036 关于开发商务宽带业务免押金0元租用光猫界面的需求
		 * 退还光猫
		 * */ 
    public static boolean returnFtthModem(IData inparam) throws Exception{
    	boolean rtnFlag=true;
    	FTTHBusiModemApplyBean bean= BeanManager.createBean(FTTHBusiModemApplyBean.class);
    	
    	String custId=inparam.getString("CUST_ID","");
    	IDataset custInfo=bean.getCustInfoByCustid(custId);
		if(custInfo!=null && custInfo.size()>0){
			String custName=custInfo.getData(0).getString("CUST_NAME");
			inparam.put("CUST_NAME", custName);
		}
		return bean.returnFtthModem(inparam); 
    }
    
    /**
	 * REQ201512160017 光猫押金释放后下发告知短信的开发需求
	 * 退押金成功下发短信：code_code where sql_ref=INSERT_TI_O_SMS
	 * 20151218 chenxy3
	 * 新增一个通用的SMS发送方法
	 * */
    public static void smsSent(IData param) throws Exception{
    	
    	   String serialNumber=param.getString("SERIAL_NUMBER","");
    	   String userId=param.getString("USER_ID","");
    	   String content=param.getString("NOTICE_CONTENT","");
    	   String staffId=param.getString("STAFF_ID",getVisit().getStaffId());
    	   String departId=param.getString("DEPART_ID",getVisit().getDepartId());
    	   String remark=param.getString("REMARK","");
    	   String brandCode=param.getString("BRAND_CODE","");
    	   String forStartTime=param.getString("FORCE_START_TIME","");
    	   String forEndTime=param.getString("FORCE_END_TIME","");
		   IData smsData = new DataMap();

			// 插入短信表
           String seq = SeqMgr.getSmsSendId();
           long seq_id = Long.parseLong(seq);
           long partition_id = seq_id % 1000;
           
           smsData.put("SMS_NOTICE_ID",seq_id);
           smsData.put("PARTITION_ID",partition_id);
           smsData.put("EPARCHY_CODE",CSBizBean.getTradeEparchyCode());
           smsData.put("BRAND_CODE",brandCode);
           smsData.put("IN_MODE_CODE",getVisit().getInModeCode());
           smsData.put("SMS_NET_TAG","0");
           smsData.put("CHAN_ID","GTM");
           smsData.put("SEND_OBJECT_CODE","1");
           smsData.put("SEND_TIME_CODE","1");
           smsData.put("SEND_COUNT_CODE","");
           smsData.put("RECV_OBJECT_TYPE","00");
           smsData.put("RECV_OBJECT",serialNumber);
           smsData.put("RECV_ID",userId);
           smsData.put("SMS_TYPE_CODE","20");
           smsData.put("SMS_KIND_CODE","08");
           smsData.put("NOTICE_CONTENT_TYPE","0");
           smsData.put("NOTICE_CONTENT",content);
           smsData.put("REFERED_COUNT","");
           smsData.put("FORCE_REFER_COUNT","1");
           smsData.put("FORCE_OBJECT","10086");
           smsData.put("FORCE_START_TIME",forStartTime);
           smsData.put("FORCE_END_TIME",forEndTime);
           smsData.put("SMS_PRIORITY","1001");
           smsData.put("REFER_TIME",SysDateMgr.getSysDateYYYYMMDDHHMMSS());
           smsData.put("REFER_STAFF_ID",staffId);
           smsData.put("REFER_DEPART_ID",departId);
           smsData.put("DEAL_TIME",SysDateMgr.getSysDateYYYYMMDDHHMMSS());
           smsData.put("DEAL_STAFFID",staffId);
           smsData.put("DEAL_DEPARTID",departId);
           smsData.put("DEAL_STATE","15");
           smsData.put("REMARK",remark);
           smsData.put("REVC1","");
           smsData.put("REVC2","");
           smsData.put("REVC3","");
           smsData.put("REVC4","");
           smsData.put("MONTH",SysDateMgr.getCurMonth());
           smsData.put("DAY",SysDateMgr.getCurDay());

           Dao.insert("TI_O_SMS", smsData);
    }
}
