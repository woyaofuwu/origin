
package com.asiainfo.veris.crm.order.soa.person.busi.ftthbusimodemapply;

 
import org.apache.log4j.Logger;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.util.DataSetUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.HwTerminalCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeBhQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.ftthmodemmanage.FTTHBusiModemManageBean;

/**
 * REQ201511190036 关于开发商务宽带业务免押金0元租用光猫界面的需求
 * chenxy3
 * 2015-12-8 
 * */
public class FTTHBusiModemApplyBean extends CSBizBean
{
	private static transient Logger logger = Logger.getLogger(FTTHBusiModemApplyBean.class);
	/**
	 * 判断集团产品编码（即办理业务的号码）的product_id 是否 （7341 集团商务宽带产品） ，
	 * 如果不是不能办理
	 * */
	public static IDataset checkFTTHBusi(IData inParam) throws Exception
    {
		IData param = new DataMap();
        param.put("USER_ID", inParam.getString("USER_ID"));
        param.put("PRODUCT_ID", inParam.getString("PRODUCT_ID","7341"));
        IDataset userProd = Dao.qryByCode("TF_F_USER_PRODUCT", "SEL_FTTHBUSI_SN_PRODUCTID", param);
        return userProd;
    } 
	
	/**
	 * 判断录入的宽带号码是否存在TRADE表未完工记录 
	 * */
	public static IDataset checkKDNumInTrade(IData inParam) throws Exception
    {
	    IDataset resultDataset = null;
	    
	    IData param = new DataMap();
        param.put("KD_NUMBER", inParam.getString("KD_NUMBER")); 
        param.put("CUSTID_GROUP", inParam.getString("CUSTID_GROUP")); 
        IDataset kdTrade = Dao.qryByCode("TF_B_TRADE", "SEL_TRADE_BY_KD_NUMBER", param, Route.getJourDb(BizRoute.getTradeEparchyCode()));
        
        if (IDataUtil.isNotEmpty(resultDataset))
        {
            String userId = kdTrade.getData(0).getString("USER_ID");
            
            param = new DataMap();
            param.put("USER_ID", userId);
            
            IDataset modemInfos = checkApplyModem(param);
            
            //没有申领过光猫才返回
            if (IDataUtil.isEmpty(modemInfos))
            {
                resultDataset = kdTrade;
            }
        }
        
        return resultDataset;
    } 
	
	/**
	 * 判断录入的宽带号码是否已经申领过光猫
	 * 含TRADE表关联。
	 * */
	public static IDataset checkKDNumInOther(IData inParam) throws Exception
    {
	    IData param = new DataMap();
        
        IDataset resultDataset = null;
        
        param.put("SERIAL_NUMBER", inParam.getString("KD_NUMBER")); 
        param.put("CANCEL_TAG", "0");
        param.put("TRADE_TYPE_CODE", "613");
        
        IDataset mainTrade = TradeInfoQry.queryTradeBySnAndTypeCode(param);
        
        if (IDataUtil.isNotEmpty(mainTrade))
        {
            param.clear();
            param.put("KD_NUMBER", inParam.getString("KD_NUMBER")); 
            param.put("USER_ID", mainTrade.getData(0).getString("USER_ID")); 
            
            resultDataset = Dao.qryByCode("TF_F_USER_OTHER", "SEL_OTHER_BY_KD_NUMBER", param);
            
        }
        
        return resultDataset;
    } 
	
	/**
	 * 判断录入的宽带号码是否已经申领过光猫
	 * 单独查OTHER表。
	 * */
	public static IDataset checkApplyModem(IData inParam) throws Exception
    {
		IData param = new DataMap();
        param.put("USER_ID", inParam.getString("USER_ID"));  
        IDataset otherInfos = Dao.qryByCode("TF_F_USER_OTHER", "SEL_OTHER_FTTH_BUSI_BY_USERID", param);
        return otherInfos;
    } 
	
	/**
     * 更新申领光猫串号
     * RSRV_STR1
     * 入参：USER_ID RES_NO
     * */
	public static int updFtthBusiResNO(IData inParam) throws Exception
    {
		IData param = new DataMap();
        param.put("USER_ID", inParam.getString("USER_ID"));  
        param.put("RES_NO", inParam.getString("RES_NO"));  
        int updResult=Dao.executeUpdateByCodeCode("TF_F_USER_OTHER", "UPD_FTTH_BUSI_RESID", param);
        return updResult;
    } 
	
	/**
	 * FTTH_GROUP 获取“CRM > 个人业务 > 宽带业务 > FTTH宽带业务 > FTTH宽带用户开户”开户失败的用户 
	 * 流程：
	 * 1、取TF_BH_TRADE表A状态的记录。
	 * 2、取TF_BH_TRADE_FTTH临时表标记4的记录。
	 * 3、查看OTHER表如果存在光猫串号，就调用华为接口退光猫
	 * 4、退光猫成功：A 更新other表的end_date字段，终止它。B 如果存在临时表记录，状态置为1
	 * 5、退光猫失败：A 如果是TRADE查出来的，插入FTTH临时表，等下次再跑。 B 如果是临时表查的，那就不做更新，等下次还跑。
	 * */
    public void returnResByWilenFailUser(IData inParam) throws Exception
    {
    	IDataset users=new DatasetList();
    	users.addAll(FTTHBusiModemApplyBean.getTradeFtthFail());//FTTH_GROUP 获取开户失败用户信息，结果集是否含判断领取光猫rsrv_str1
	    
    	//取临时表数据
    	IDataset tempUsers=FTTHBusiModemApplyBean.getTempTabFtthFail();
    	users.addAll(tempUsers);
    	if(users!=null && users.size()>0){
	   		for(int i=0;i<users.size();i++){
	   			IData user=users.getData(i); 
	   			String resId=user.getString("RSRV_STR1");  
	   			String userId=user.getString("USER_ID");
	   			String tempTag=user.getString("TEMP_TAG");
	   			String tradeId=user.getString("TRADE_ID");
	   			//String tradeIdOther=user.getString("OTHER_TRADE_ID");
	   			
	   			/**取A记录下的9记录的信息，取得撤单成功的工号及部门，用于退光猫*/
   				String rtnStaffId="";//必传--退光猫获取A记录下9记录的操作员 
   				String rtnDepartId="";//必传 --退光猫获取A记录下9记录的操作员部门
   				IDataset returnTrades=FTTHBusiModemApplyBean.getFtthReturnTrade(user);
   				if(returnTrades!=null && returnTrades.size()>0){
   					rtnStaffId=returnTrades.getData(0).getString("TRADE_STAFF_ID",user.getString("TRADE_STAFF_ID"));//必传--退光猫获取A记录下9记录的操作员 
   					rtnDepartId=returnTrades.getData(0).getString("TRADE_DEPART_ID",user.getString("TRADE_DEPART_ID"));//必传 --退光猫获取A记录下9记录的操作员部门
   				}
	   			try{ 
		   			//集团的只退光猫 
		   			if(resId!=null && !"".equals(resId) && resId.length()>1){
		   				String custId=user.getString("CUST_ID");
			   			IDataset custInfo=FTTHBusiModemApplyBean.getCustInfoByCustid(custId);
			   			if(custInfo!=null && custInfo.size()>0){
			   				String custName=custInfo.getData(0).getString("CUST_NAME");
			   				user.put("CUST_NAME", custName);
			   			}
			   			user.put("RES_NO", resId);
			   			user.put("TRADE_STAFF_ID", rtnStaffId);
			   			user.put("TRADE_DEPART_ID", rtnDepartId);
		   				boolean rtnFlag=FTTHBusiModemApplyBean.returnFtthModem(user);
		   				if(rtnFlag){
		   					IData updData=new DataMap();
		   					updData.put("USER_ID", userId);
			   				//退还成功 1、更新OTHER表
		   					FTTHBusiModemApplyBean.updFtthFailOtherEnd(updData); 
		   					if("1".equals(tempTag)){
		   						//2、如果存在临时表数据：更新临时表
		   						updData.put("TRADE_ID", tradeId);
			   					updData.put("DEAL_TAG", "1");
			   					FTTHBusiModemApplyBean.updFtthFailTempTab(updData);
		   					}
			   			}
		   			}
	   			}catch(Exception e){
	   				//1、如果是捞A的记录报错导致无法退还光猫，则插入临时表，等下次再办理
	   				//2、如果是临时表记录错误，则不改动，让它下次再跑一次。
	   				if(logger.isInfoEnabled()) logger.info(e);
	   				IData updData=new DataMap();
	   				if("1".equals(tempTag)){
	   					
   						//2、如果存在临时表数据：更新临时表
   						updData.put("TRADE_ID", tradeId);
	   					updData.put("DEAL_TAG", "9");
	   					FTTHBusiModemApplyBean.updFtthFailTempTab(updData);
   					}else{
   						//更新使other表，记录信息。
   						updData.put("USER_ID", userId);
   						String errInfo=e.getMessage();
   						if(errInfo.length()>250){
   							errInfo=errInfo.substring(0,240);
   						}
   						updData.put("ERR_INFO", errInfo);
   						FTTHBusiModemApplyBean.updFtthFailErrInfo(updData);
   					}
	   			}
	   		}
	   	} 
    }
    
    /**
     * 调用华为接口，退还光猫
     * */
	public static boolean returnFtthModem(IData inParam) throws Exception{
		Boolean rtnFlag=true;
		IData param = new DataMap();
    	param.put("RES_NO", inParam.getString("RES_NO"));//终端串号
//    	param.put("REMARK", "");//备注
    	param.put("PARA_VALUE1", inParam.getString("SERIAL_NUMBER"));//PARA_VALUE1
    	param.put("SALE_FEE", 0);//销售费用 非销售时为0
//    	param.put("PARA_VALUE3", "");//活动标识
//    	param.put("PARA_VALUE4", "");//用户编码
//    	param.put("PARA_VALUE5", "");//返销时间
    	param.put("PARA_VALUE7", 0);//代办费
//    	param.put("DEVICE_COST", "");//进货价格
//    	param.put("PARA_VALUE8", "");//销售政策
    	param.put("TRADE_ID", SeqMgr.getTradeIdFromDb());//台账流水
    	param.put("X_CHOICE_TAG", "1");//0-终端销售,1—终端销售退货
    	param.put("ES_TYPE_CODE", "4");//资源类型,终端的传入4
    	param.put("CONTRACT_ID", inParam.getString("OTHER_TRADE_ID"));//销售订单号
    	param.put("PRODUCT_MODE", "0");//全网统一操盘合约机销售标志
    	param.put("X_RES_NO_S", inParam.getString("RES_NO"));//终端串号
    	param.put("X_RES_NO_E", inParam.getString("RES_NO"));//终端串号
    	param.put("PARA_VALUE13", "0");//是否有销售酬金  0-没有 1-有
    	param.put("PARA_VALUE14", "0");//裸机价格
    	param.put("PARA_VALUE15", "0");//客户购机折让价格
    	param.put("PARA_VALUE16", "0");//客户预存话费
    	param.put("PARA_VALUE17", "0");//客户实际购机款
    	param.put("PARA_VALUE18", "0");//客户实缴费用总额
    	param.put("PARA_VALUE9", "03");//客户捆绑合约类型
    	param.put("SERIAL_NUMBER", inParam.getString("SERIAL_NUMBER"));//客户号码
    	param.put("PARA_VALUE1", inParam.getString("SERIAL_NUMBER"));//客户号码 
    	param.put("USER_NAME", new String(inParam.getString("CUST_NAME").getBytes("UTF-8"),"GBK"));//客户名称
    	param.put("STAFF_ID", inParam.getString("UPDATE_STAFF_ID"));//销售员工
    	param.put("TRADE_STAFF_ID", inParam.getString("TRADE_STAFF_ID"));//员工（要求是领设备的业务办理员工）
    	param.put("TRADE_DEPART_ID", inParam.getString("TRADE_DEPART_ID"));//部门（要求是领设备的业务办理员工所在部门）
    		
    	param.put("RES_TRADE_CODE", "IMobileDeviceModifyState");
    	
    	param.put("FTTH_RTN_MODEM", "BUSI");
    	
    	//IDataset results = HwTerminalCall.returnTopSetBoxTerminal(param); 
    	IDataset results = HwTerminalCall.callHwRtnTerminalIntf("ITF_MONNI", param);
    	if(!"0".equals(results.getData(0).getString("X_RESULTCODE")))
    	{
    		rtnFlag=false; 
    	    if (logger.isDebugEnabled())
            {
                 logger.error("FTTH宽带装机失败，光猫退机失败！rtnset:"+results);
            }
    	}else{
    		rtnFlag=true;
            if (logger.isDebugEnabled())
            {
               logger.error("FTTH宽带：光猫退机成功！rtnset:"+results);
            }
        } 
    	return rtnFlag;
	}
	
	/**
	 * 获取装机失败TRADE表A状态的用户信息
	 * 查TF_BH_TRADE。
	 * 用于处理装机失败退光猫的信息。
	 * */
	public static IDataset getTradeFtthFail() throws Exception
    {
		IData param = new DataMap(); 
		
		IDataset resultDataset = new DatasetList();
		
		param.put("TRADE_TYPE_CODE", "613");
		param.put("SUBSCRIBE_STATE", "A");
		
        IDataset failUsers = Dao.qryByCode("TF_BH_TRADE", "SEL_FTTH_BUSI_TRADE_FAIL", param, Route.getJourDb(BizRoute.getTradeEparchyCode()));
        
        if (IDataUtil.isNotEmpty(failUsers))
        {
            for (int i = 0; i < failUsers.size(); i++)
            {
                IData failUser = failUsers.getData(i);
                
                
                IDataset busiModemInfos = UserOtherInfoQry.getUserOtherByUserIdStr5(failUser.getString("USER_ID"), "FTTH_GROUP", failUser.getString("TRADE_ID"));
                
                if (IDataUtil.isNotEmpty(busiModemInfos))
                {
                    failUser.put("RSRV_STR1", busiModemInfos.getData(0).getString("RSRV_STR1"));
                    failUser.put("UPDATE_STAFF_ID", busiModemInfos.getData(0).getString("UPDATE_STAFF_ID"));
                    failUser.put("OTHER_TRADE_ID", busiModemInfos.getData(0).getString("TRADE_ID"));
                    
                    resultDataset.add(failUser);
                }
            }
        }
        
        return resultDataset;
    } 
	
	/**
	 * 获取TF_BH_TRADE_FTTH临时表数据
	 * 用于一起处理装机失败退光猫的信息。
	 * */
	public static IDataset getTempTabFtthFail() throws Exception
    {
//		IData param = new DataMap();
//        IDataset otherInfos = Dao.qryByCode("TF_BH_TRADE", "SEL_FTTH_BUSI_TRADE_FAIL_TEMPTAB", param);
//        return otherInfos;
	    IDataset resuleDataset = new DatasetList();
	    
	    IDataset bhTradeFtthInfos = FTTHBusiModemManageBean.qryTfBhTradeFtthInfo();
        
        if (IDataUtil.isNotEmpty(bhTradeFtthInfos))
        {
            String tradeId = "";
            
            for (int i = 0; i < bhTradeFtthInfos.size(); i++)
            {
                tradeId = bhTradeFtthInfos.getData(i).getString("TRADE_ID");
 
                if (StringUtils.isNotEmpty(tradeId))
                {
                    IDataset resultDataTemps = TradeBhQry.queryBhTradeInfoByTradeIdAndTradeCode(tradeId, "613", "A");
                    
                    if (IDataUtil.isNotEmpty(resultDataTemps))
                    {
                        IData resultDataTemp = resultDataTemps.getData(0);
                        
                        IDataset userOtherInfos = UserOtherInfoQry.getUserOther(resultDataTemp.getString("USER_ID"), "FTTH_GROUP");
                        
                        if (IDataUtil.isNotEmpty(userOtherInfos))
                        {
                            resultDataTemp.put("RSRV_STR1", userOtherInfos.getData(0).getString("RSRV_STR1"));
                            resultDataTemp.put("UPDATE_STAFF_ID", userOtherInfos.getData(0).getString("UPDATE_STAFF_ID"));
                            resultDataTemp.put("OTHER_TRADE_ID", userOtherInfos.getData(0).getString("TRADE_ID"));
                            
                            resuleDataset.add(resultDataTemp);
                        }
                        
                    }
                } 
            }
        }
        
        return resuleDataset;
    } 
	
	/**
     * 装机失败终止光猫OTHER表记录
     * */
	public static int updFtthFailOtherEnd(IData inParam) throws Exception
    {
		IData param = new DataMap();
        param.put("USER_ID", inParam.getString("USER_ID"));  
        int updResult=Dao.executeUpdateByCodeCode("TF_F_USER_OTHER", "UPD_OTHER_FTTH_BUSI_END", param);
        return updResult;
    } 
	
	
	/**
     * 装机失败--对于存在TRADEID的终止TF_BH_TRADE_FTTH临时表记录
     * */
	public static int updFtthFailTempTab(IData inParam) throws Exception
    {
		IData param = new DataMap();
        param.put("TRADE_ID", inParam.getString("TRADE_ID"));  
        param.put("DEAL_TAG", inParam.getString("DEAL_TAG"));  
        int updResult=Dao.executeUpdateByCodeCode("TF_BH_TRADE_FTTH", "UPD_FTTH_BUSI_TEMPTAB", param);
        return updResult;
    } 
	
	/**
     * 装机失败--对于存在TRADEID的终止TF_BH_TRADE_FTTH临时表记录
     * */
	public static int updFtthFailErrInfo(IData inParam) throws Exception
    {
		IData param = new DataMap();
        param.put("USER_ID", inParam.getString("USER_ID"));  
        int updResult=Dao.executeUpdateByCodeCode("TF_BH_TRADE_FTTH", "UPD_OTHER_FTTH_BUSI_ERR", param);
        return updResult;
    } 
	
	/**
     * 装机失败--对于存在TRADEID的终止TF_BH_TRADE_FTTH临时表记录
     * */
	public static int insFtthFailTempTab(IData inParam) throws Exception
    {
		IData param = new DataMap();
        param.put("TRADE_ID", inParam.getString("TRADE_ID"));  
        param.put("DEAL_TAG", inParam.getString("DEAL_TAG"));  
        int updResult=Dao.executeUpdateByCodeCode("TF_BH_TRADE_FTTH", "INS_FTTH_BUSI_TEMPTAB", param);
        return updResult;
    } 
	
	/**
	 * 取cust_name
	 * */
	public static IDataset getCustInfoByCustid(String custId) throws Exception
    {

        IData param = new DataMap();
        param.put("CUST_ID", custId);  
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT T.* FROM TF_F_CUSTOMER T  WHERE  T.CUST_ID=:CUST_ID ");

        return Dao.qryByParse(parser);
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
    	return Dao.qryByParse(parser, Route.getJourDb(getTradeEparchyCode()));
    	
    }
    
    
    /**
     * REQ201604080019 关于优化商务宽带成员用户查询界面的需求
     * chenxy3 20160421
     * */
	public static IDataset queryFTTHBusiMem(IData inParam,Pagination pagen) throws Exception
    {
		SQLParser parser = new SQLParser(inParam); 
        parser.addSQL(" select T.SERIAL_NUMBER SERIAL_NUMBER_B1, T1.STAND_ADDRESS,T1.CONTACT,T1.CONTACT_PHONE," +
        		"T1.PHONE,T.SERIAL_NUMBER FTTH_ACOUNT," +
        		"to_char(T.ACCEPT_DATE,'YYYY-MM-DD HH24:MI:SS') ACCEPT_DATE2," +
        		"to_char(T.ACCEPT_DATE,'YYYY-MM-DD HH24:MI:SS') START_DATE,t.* "); 
        parser.addSQL(" from TF_B_TRADE t,TF_B_TRADE_WIDENET t1  "); 
        parser.addSQL(" where t.trade_id=t1.trade_id "); 
        parser.addSQL(" AND T.TRADE_TYPE_CODE in ('613','600') "); 
        parser.addSQL(" AND T.serial_number like 'KD_'||:SERIAL_NUMBER||'%' "); 
        parser.addSQL(" AND T.ACCEPT_DATE >= TO_DATE(:START_DATE,'YYYY-MM-DD') "); 
        parser.addSQL(" AND T.ACCEPT_DATE <= TO_DATE(:END_DATE,'YYYY-MM-DD HH24:MI:SS') "); 
        parser.addSQL(" AND T.TRADE_STAFF_ID=:STAFF_ID "); 
        parser.addSQL(" AND T.TRADE_DEPART_ID=:DEPART_ID "); 
    	return Dao.qryByParse(parser,pagen, Route.getJourDb(getTradeEparchyCode()));
    } 
	
	 /**
     * 判断是否开户时是否已经申领过光猫
     * 入参：TRADE_ID
     * */
    public static IDataset checkApplyModemByTradeId(IData inparam)throws Exception{
    	String rsrv_value_code = "FTTH_GROUP";
    	String tradeId = inparam.getString("TRADE_ID");
    	IDataset userOtherInfos= TradeOtherInfoQry.getTradeOtherByTradeIdRsrvCode(tradeId,rsrv_value_code);
    	IDataset userOthers = new DatasetList();
    	if(DataSetUtils.isNotBlank(userOtherInfos)){
    		int tradeSize = userOtherInfos.size();
        	for(int i = 0 ; i < tradeSize ; i++){
        		if("0".equals(userOtherInfos.getData(i).getString("MODIFY_TAG"))){
        			userOthers.add(userOtherInfos.getData(i));
        		}
        	}
    	}
    	return userOthers;
    } 
}
