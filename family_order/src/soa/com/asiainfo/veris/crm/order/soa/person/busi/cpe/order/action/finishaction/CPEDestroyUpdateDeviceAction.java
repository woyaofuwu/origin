
package com.asiainfo.veris.crm.order.soa.person.busi.cpe.order.action.finishaction;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.TradeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.HwTerminalCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.acct.AcctInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.SmsSend;
import com.asiainfo.veris.crm.order.soa.person.busi.cpe.CPEActiveBean;

/**
 * 
 * 
 * @ClassName: DestroyUpdateDeviceAction.java
 * @Description: 
 *  REQ201612260011_新增CPE终端退回和销户界面,
 *  押金原路返回、押金沉淀、退还设备、发送短信(主号)
 * @version: v1.0.0
 * @author: zhuoyingzhi
 * @date: 2017-2-13
 */
public class CPEDestroyUpdateDeviceAction implements ITradeFinishAction
{
	private static final Logger log = Logger.getLogger(CPEDestroyUpdateDeviceAction.class);
	
    @Override
    public void executeAction(IData mainTrade) throws Exception
    {
        // TODO Auto-generated method stub
    	//获取手机号码
	   	 IData param=new DataMap();
	   	 String serialNumber= mainTrade.getString("SERIAL_NUMBER");
	 	 param.put("SERIAL_NUMBER", serialNumber);   
	     IDataset results = Dao.qryByCode("TF_F_USER_PRODUCT", "SEL_PRODUCT_CPE_BY_SN", param);
    	 
	     if(IDataUtil.isNotEmpty(results)){        
	         if(serialNumber !=null){
	             if(serialNumber.startsWith("147")){
	             	//只处理手机号码是147开头的手机号码
	                 String userId = mainTrade.getString("USER_ID");
	                 if(StringUtils.isEmpty(userId)){
	                 	CSAppException.apperr(TradeException.CRM_TRADE_258);
	                 }
	                 
	                //预存没有用完怎么处理
	                 /**
	                  * REQ201711060010 系统需求名称 9003 CPE押金存折优化申请单
	                  * <br/>
	                  *  如果用户有押金进行强制清退，如果没有押金也不做清退的拦截。
	                  * @author zhuoyingzhi
	                  * @date 20180123
	                  */
	                boolean  cpe9003=isAcctDepositByAcctId(userId);
	                
	                log.debug("--CPEDestroyUpdateDeviceAction---cpe9003:"+cpe9003);
	                String mainSn="";
	                String str7=mainTrade.getString("RSRV_STR7", "");
	                if("0".equals(str7)&&cpe9003){
	             	   //押金原路返回
	             	   
	                    //对押金原路返回
	             	   mainSn=adjustFeeDeposit(userId);
	     	           //退还设备
	     	           cancelDevice(userId, mainTrade);
	     	           //发送短信
	     	           if(!"".equals(mainSn) && mainSn!=null){
	     	        	    sendSms(mainSn);
	     	           }
	                }else if("1".equals(str7)&&cpe9003){
	             	   //押金沉淀
	             	   mainSn=backFee(userId);
	     	           //发送短信
	     	           if(!"".equals(mainSn) && mainSn!=null){
	     	        	    sendSms(mainSn);
	     	           }
	                }
	             }
	         }
	     }
    }
    
    
   /**
    * 
    *  押金原路返回
    * @param userId
    * @throws Exception
    */
   public String  adjustFeeDeposit(String userId) throws Exception{
	   
     //先取押金金额commpara表param_attr=701 取0账户
	  IDataset paras=CommparaInfoQry.getCommparaAllCol("CSM", "701", "0", "0898");
	  String deposit=paras.getData(0).getString("PARA_CODE1");
	  //主号
	  String mainSn="";  
     /**
      * 为了获取主号码的信息
      * <br/>
      * 20180408修改为添加类型查询
      */
     //IDataset userBRelaInfos = RelaUUInfoQry.queryValidRelaUUByUserIDB(userId);
     IDataset userBRelaInfos =RelaUUInfoQry.getRelaByUserIdbAndRelaTypeCode(userId, "CP");
     
     if(IDataUtil.isNotEmpty(userBRelaInfos)){
	        IData userBRelaInfoData = userBRelaInfos.getData(0);
	        String relationTypeCode = userBRelaInfoData.getString("RELATION_TYPE_CODE");
	        
	        //虚拟号码的userid
	        String userIdA = userBRelaInfoData.getString("USER_ID_A");
	        
	        //查询主号码
	        IDataset relaUUDataset = RelaUUInfoQry.getRelationsByUserIdA(relationTypeCode, userIdA, "1");
	        if(IDataUtil.isNotEmpty(relaUUDataset)){
	            IData relaUUData = relaUUDataset.getData(0);
	            String userIdB = relaUUData.getString("USER_ID_B");
	            //主手机号码
	            mainSn = relaUUData.getString("SERIAL_NUMBER_B");
	
	            //获取默认账户  （acct_id)
		    	IDataset accts=AcctInfoQry.qryAcctDefaultIdBySn(mainSn);
		    	
		    	if(IDataUtil.isNotEmpty(accts)){
			    	String mainAcctId=accts.getData(0).getString("ACCT_ID");
			    	String mainCustId=accts.getData(0).getString("CUST_ID");
			    	String mainCityCode=accts.getData(0).getString("CITY_CODE");
			    	String mainEparchyCode=accts.getData(0).getString("EPARCHY_CODE");
		
		         	IData transParam=new DataMap();
		         	transParam.put("DEPOSIT_CODE_OUT", "9003");
		         	transParam.put("TRANS_BUSINESS_TYPE","2");
		         	//主号码的userid
		         	transParam.put("USER_ID", userIdB);
		         	transParam.put("ACCT_ID", mainAcctId);
		         	transParam.put("CUST_ID", mainCustId);
		         	transParam.put("TRADE_FEE", deposit);
		         	transParam.put("EPARCHY_CODE", mainEparchyCode);
		         	transParam.put("CITY_CODE", mainCityCode);
		         	transParam.put("SERIAL_NUMBER", mainSn); 
		     		IData inAcct=AcctCall.adjustFee(transParam);//调用账务接口
		     		String result=inAcct.getString("RESULT_CODE","");
		     		if(!"".equals(result) && !"0".equals(result)){ 
		     			CSAppException.apperr(CrmCommException.CRM_COMM_103, "调用接口AM_CRM_TransFeeInFTTH解冻CPE业务押金错误:"+inAcct.getString("RESULT_INFO"));
		     		}		    		
		    	} 
	        }
        }
     return mainSn;
   }
   
   /**
    * 退还设备
    * @param userId
    * @param mainTrade
    * @throws Exception
    */
   public void cancelDevice(String userId,IData mainTrade)throws Exception{
	   
   	       CPEActiveBean bean = BeanManager.createBean(CPEActiveBean.class); 
	        //获取原来串号信息
	   		IDataset otherInfo=bean.qryCPEOtherInfo(userId,"CPE_DEVICE");
	   		if(IDataUtil.isEmpty(otherInfo)){
	   			CSAppException.apperr(CrmCommException.CRM_COMM_103,"获取原来设备信息异常!userId:"+userId+",otherInfo:"+otherInfo);
	   		}
	   		
	   		IData other=otherInfo.getData(0);
	   		//购机用户的手机号码
	   		String memberSn=other.getString("RSRV_STR3", "");
	   		//串号
	   		String resNo=other.getString("RSRV_STR4", "");
	   		//INST_ID
	   		String tradeId=other.getString("INST_ID", "");
	   		
	   		//销售时间
	   		String startDate=other.getString("START_DATE", "");
	   
		   	IData param = new DataMap();
			param.put("RES_NO", resNo);//串号
			param.put("SALE_FEE", "0");//销售费用:不是销售传0
			param.put("PARA_VALUE1", memberSn);//购机用户的手机号码
			param.put("PARA_VALUE7", "0");//代办费
			param.put("TRADE_ID",  tradeId);//台账流水 
			param.put("X_CHOICE_TAG", "1");//0-终端销售,1—终端销售退货
			param.put("RES_TYPE_CODE", "4");//资源类型,终端的传入4
			param.put("PRODUCT_MODE", "0");
			param.put("PARA_VALUE11", startDate);//销售时间
			param.put("PARA_VALUE15", "0");//客户购机折让价格
			param.put("PARA_VALUE16", "0");
			param.put("PARA_VALUE17", "0");
			param.put("PARA_VALUE18", "0");//客户实缴费用总额  //如果没有合约，就和实际付款相等就可以。 
			param.put("PARA_VALUE9", "03");//客户捆绑合约类型 //合约类型：01—全网统一预存购机 02—全网统一购机赠费 03—预存购机 
			param.put("PARA_VALUE1", memberSn);//客户号码
			param.put("STAFF_ID", other.getString("UPDATE_STAFF_ID", ""));//销售员工
			param.put("RES_TRADE_CODE", "IMobileDeviceModifyState");
		
			IDataset sysResults = HwTerminalCall.occupyTerminalByTerminalId(param);
			if(!StringUtils.equals(sysResults.first().getString("X_RESULTCODE"), "0")){//0为成功，其他失败
				String x_resultinfo=sysResults.first().getString("X_RESULTINFO");
				if(StringUtils.isNotBlank(sysResults.first().getString("X_RESULTINFO"))){
					CSAppException.apperr(CrmCommException.CRM_COMM_103,x_resultinfo);
				}
				CSAppException.apperr(CrmCommException.CRM_COMM_103,"华为接口调用异常！");
			} 
   }
   

   
   /**
    * 押金沉淀(主号码)
    * @param userId  符号的userid
    * @throws Exception
    */
   public String backFee(String userId)throws Exception{
	     //先取押金金额commpara表param_attr=701 取0账户
		  IDataset paras=CommparaInfoQry.getCommparaAllCol("CSM", "701", "0", "0898");
		  String deposit=paras.getData(0).getString("PARA_CODE1");
		
		  String mainSn="";
	     /**
	      * 为了获取主号码的信息
	      * <br/>
	      * 20180408 修改为添加关系类型查询
	      */
	     //IDataset userBRelaInfos = RelaUUInfoQry.queryValidRelaUUByUserIDB(userId);
	     IDataset userBRelaInfos =RelaUUInfoQry.getRelaByUserIdbAndRelaTypeCode(userId, "CP");
	     
	     if(IDataUtil.isNotEmpty(userBRelaInfos)){
		        IData userBRelaInfoData = userBRelaInfos.getData(0);
		        String relationTypeCode = userBRelaInfoData.getString("RELATION_TYPE_CODE");
		        
		        //虚拟号码的userid
		        String userIdA = userBRelaInfoData.getString("USER_ID_A");
		        
		        //查询主号码
		        IDataset relaUUDataset = RelaUUInfoQry.getRelationsByUserIdA(relationTypeCode, userIdA, "1");
		        if(IDataUtil.isNotEmpty(relaUUDataset)){
		            IData relaUUData = relaUUDataset.getData(0);
		            String userIdB = relaUUData.getString("USER_ID_B");
		            //主手机号码
		            mainSn = relaUUData.getString("SERIAL_NUMBER_B");
		
		            //获取默认账户  （acct_id)
			    	IDataset accts=AcctInfoQry.qryAcctDefaultIdBySn(mainSn);
			    	if(IDataUtil.isNotEmpty(accts)){
				    	String mainAcctId=accts.getData(0).getString("ACCT_ID");
				    	
						IData inparams=new DataMap();
					    inparams.put("USER_ID", userIdB);
					    inparams.put("SERIAL_NUMBER", mainSn); 
					    
					    
						IData depositeInfo=new DataMap();
						depositeInfo.put("DEPOSIT_CODE", "9003");
						depositeInfo.put("TRANS_FEE", deposit);//沉淀资金
						IDataset depositeInfos=new DatasetList();
						depositeInfos.add(depositeInfo);
						
						inparams.put("DEPOSIT_INFOS", depositeInfos);
						
					    inparams.put("ACCT_ID", mainAcctId);
					    
					    inparams.put("CHANNEL_ID", "15000");
					    inparams.put("PAYMENT_ID", "100021");//资金沉淀
					    inparams.put("PAYMENT_OP", "16001");
					    inparams.put("PAY_FEE_MODE_CODE", "0");
					    inparams.put("REMARK", "CPE业务押金存折资金沉淀");
					    inparams.put("FORCE_TAG", "1");
					    
					    IData inAcct = AcctCall.foregiftDeposite(inparams);
					    if(IDataUtil.isNotEmpty(inAcct))
					    {
					    	String result = inAcct.getString("X_RESULTCODE","");
					    	if(!"".equals(result)&&"0".equals(result)){
					    	}else{
					    		//押金沉淀失败
				     			CSAppException.apperr(CrmCommException.CRM_COMM_103, "调用接口AM_CRM_GMFeeDeposit,CPE业务,押金沉淀错误:"+inAcct.getString("RESULT_INFO"));
					    	}
					    }
			    	}

		        }
	        }
	  return mainSn;
   }
   
   /**
    * 给主号码发送短信
    * @param mainSn
    * @throws Exception
    */
	public void sendSms(String  mainSn) throws Exception{
		String msg = "尊敬的客户，已受理取消CPE绑定付费关系和取消CPE业务。";
		//发送短信通知
		IData inparam = new DataMap();
        inparam.put("NOTICE_CONTENT", msg);
        inparam.put("RECV_OBJECT", mainSn);
        inparam.put("RECV_ID", mainSn);
        inparam.put("REFER_STAFF_ID", CSBizBean.getVisit().getStaffId());
        inparam.put("REFER_DEPART_ID",CSBizBean.getVisit().getDepartId());
        inparam.put("REMARK", "CPE销户发送短信");
        SmsSend.insSms(inparam);
	}
	
	/**
	 * REQ201711060010 系统需求名称 9003 CPE押金存折优化申请单
	 * <br/>
	 * 判断 CPE业务押金存折  账本是否存在
	 * @author zhuoyingzhi
	 * @date 20180116
	 * @param acctId
	 * @return
	 * @throws Exception
	 */
	public boolean  isAcctDepositByAcctId(String cpeUserId)throws Exception{
		boolean  flag=false;
		 
		IData param=new DataMap();
		String mainAcctId=getMainNumAcctIdByCpeUserId(cpeUserId);
		
	    log.debug("--CPEDestroyUpdateDeviceAction---isAcctDepositByAcctId:"+mainAcctId);
	    if(!"".equals(mainAcctId)&&mainAcctId!=null){
			param.put("ACCT_ID", mainAcctId);
			//AcctCall.queryAcctDeposit()在生产上已经存在
			IDataset acctList=AcctCall.queryAcctDeposit(param);
			if(IDataUtil.isNotEmpty(acctList)){
				//存在 用户实时月账本
				for(int i=0;i<acctList.size();i++){
					String depositCode=acctList.getData(i).getString("DEPOSIT_CODE", "");
					if("9003".equals(depositCode)){
						//存在  CPE业务押金存折
						flag=true;
						break;
					}
				}
			}	    	
	    }
		return flag;
	}
	
    /**
     * REQ201711060010 系统需求名称 9003 CPE押金存折优化申请单
     * <br/>
     * 通过cpe号码获取主号的acctid
     * @param cpeUserId
     * @return
     * @throws Exception
     * @author zhuoyingzhi
     * @date 20180123
     */
	public  String  getMainNumAcctIdByCpeUserId(String cpeUserId)throws Exception{
		 String mainAcctId="";
	     IDataset userBRelaInfos =RelaUUInfoQry.getRelaByUserIdbAndRelaTypeCode(cpeUserId, "CP");
	     
	     if(IDataUtil.isNotEmpty(userBRelaInfos)){
		        IData userBRelaInfoData = userBRelaInfos.getData(0);
		        String relationTypeCode = userBRelaInfoData.getString("RELATION_TYPE_CODE");
		        
		        //虚拟号码的userid
		        String userIdA = userBRelaInfoData.getString("USER_ID_A");
		        
		        //查询主号码
		        IDataset relaUUDataset = RelaUUInfoQry.getRelationsByUserIdA(relationTypeCode, userIdA, "1");

		        if(IDataUtil.isNotEmpty(relaUUDataset)){
		            IData relaUUData = relaUUDataset.getData(0);
		            //主手机号码
		            String mainSn = relaUUData.getString("SERIAL_NUMBER_B");
		
		            //获取默认账户  （acct_id)
			    	IDataset accts=AcctInfoQry.qryAcctDefaultIdBySn(mainSn);
				    //System.out.println("---CPEDestroyUpdateDeviceAction---getMainNumAcctIdByCpeUserIdaccts:"+accts);
			    	if(IDataUtil.isNotEmpty(accts)){
				    	 mainAcctId=accts.getData(0).getString("ACCT_ID");					  
			    	}
		        }
	        }		
		return mainAcctId;
	}
}
