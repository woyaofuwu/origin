package com.asiainfo.veris.crm.order.soa.person.busi.smartaddvalue.order;

import java.math.BigDecimal;

import org.apache.log4j.Logger;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.abilityopenplatform.AbilityPlatCheckRelativeQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserClassInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserPlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleActiveInfoQry;
import com.asiainfo.veris.crm.order.soa.person.common.util.WideNetUtil;

public class DredgeSmartNetworkIntfBean extends CSBizBean{
	
	
	static Logger logger = Logger.getLogger(DredgeSmartNetworkIntfBean.class);
	
	/*
	* @Function: checkQualificate
    * @Description:	校验能否办理业务接口
    * @param: @param data
    * @param: @return
    * @param: @throws Exception
    * @return：IData
    */
	public IData checkQualificate(IData input) throws Exception {

		IData data = new DataMap();
		data.put("X_RESULTCODE",  "0000");
		data.put("X_RESULTINFO",  "正常!");
		
		IData iparam = new DataMap();
		iparam.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
		iparam.put("REMOVE_TAG", "0");
		IDataset  dataSet= AbilityPlatCheckRelativeQry.getUserInfoBySn(iparam);
	    if(dataSet==null||dataSet.size()==0){
	    	 data.put("X_RESULTCODE","2009");
   		     data.put("X_RESULTINFO","用户号码状态不正常!");
   		     return data;
 		}
		
	    iparam.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
		IDataset wideNets =getUserWidenetInfoBySerialNumber(iparam);
		if(wideNets==null||wideNets.size()==0){
			data.put("X_RESULTCODE",  "1101");
			data.put("X_RESULTINFO",  input.getString("SERIAL_NUMBER")+"号码未办理宽带");
			return data;
		}else{
			 /*if (StringUtils.equals("4", wideNets.getData(0).getString("RSRV_STR2")))
             {
				data.put("X_RESULTCODE",  "1102");
				data.put("X_RESULTINFO",  input.getString("SERIAL_NUMBER")+"号码宽带不能为校园宽带！");
				 return data;
             }*/
			 
			 IDataset noFinishTrade=TradeInfoQry.getMainTradeByUserIdTypeCode(wideNets.getData(0).getString("USER_ID"),"606","");
			 if(noFinishTrade!=null && noFinishTrade.size()>0){
				 data.put("X_RESULTCODE",  "1103");
			     data.put("X_RESULTINFO",  "用户存在宽带移机未完工单，请等待完工后再办理！");
			     return data;
			 }
			 
		}
		
		
//		if(!"0000".equals(checkFeeBeforeSubmit(input).getString("X_RESULTCODE"))){
//			data.put("X_RESULTCODE", checkFeeBeforeSubmit(input).getString("X_RESULTCODE"));
//			data.put("X_RESULTINFO", checkFeeBeforeSubmit(input).getString("X_RESULTINFO"));
//		}
				
		
		
		return data;
	}
	
	/**
	 * 校验能否办理业务接口
	 * @author: wuwangfeng
	 * @date: 2020/5/12 9:42
	 */
	public IData checkQualificateNew(IData input) throws Exception {
		IData data = new DataMap();
		data.put("X_RESULTCODE", "0000");
		data.put("X_RESULTINFO", "操作成功");
							    		
		IData res = checkQualificate(input);
		if(!"0000".equals(res.getString("X_RESULTCODE"))){
			data.put("X_RESULTCODE", res.getString("X_RESULTCODE"));
			data.put("X_RESULTINFO", res.getString("X_RESULTINFO"));
			return data;
		}
		
		// 优惠校验
		String checkCode = input.getString("CHECK_CODE", ""); //校验模式
		if("2".equals(checkCode)){
			String discntCode = input.getString("DISCNT_CODE", "");
			if("".equals(discntCode)){
				CSAppException.apperr(CrmCommException.CRM_COMM_103, "优惠编码[DISCNT_CODE]不能为空！");
			}else{
				// 根据SN查询用户全球通等级信息
				IData userClassInfo = UserClassInfoQry.queryUserClassBySN(input);
				String userClass = "";
				if(userClassInfo != null){
					userClass = userClassInfo.getString("USER_CLASS");
		        }
				IDataset alls = StaticUtil.getStaticList("SMART_THIRD_TYPE_ALL");
		        IDataset halfs = StaticUtil.getStaticList("SMART_THIRD_TYPE_HALF");
		        IDataset zeros = StaticUtil.getStaticList("SMART_THIRD_TYPE_ZERO");
		        if("1".equals(userClass)){
		            for (int i = 0; i < zeros.size(); i++) {
		                if(discntCode.equals(zeros.getData(i).getString("DATA_ID"))){
		                    CSAppException.apperr(CrmCommException.CRM_COMM_103, "仅限全球通金卡及以上客户办理!");
		                }
		            }
		            for (int i = 0; i < alls.size(); i++) {
		                if(discntCode.equals(alls.getData(i).getString("DATA_ID"))){
		                	CSAppException.apperr(CrmCommException.CRM_COMM_103, "全球通银卡及以上用户不能办理原价包!");
		                }
		            }
		        }else if("2".equals(userClass)||"3".equals(userClass)||"4".equals(userClass)){
		            for (int i = 0; i < halfs.size(); i++) {
		                if(discntCode.equals(halfs.getData(i).getString("DATA_ID"))){
		                	CSAppException.apperr(CrmCommException.CRM_COMM_103, "仅限全球通银卡客户办理!");
		                }
		            }
		            for (int i = 0; i < alls.size(); i++) {
		                if(discntCode.equals(alls.getData(i).getString("DATA_ID"))){
		                	CSAppException.apperr(CrmCommException.CRM_COMM_103, "全球通银卡及以上用户不能办理原价包!");
		                }
		            }
		        }else{
		            for (int i = 0; i < zeros.size(); i++) {
		                if(discntCode.equals(zeros.getData(i).getString("DATA_ID"))){
		                	CSAppException.apperr(CrmCommException.CRM_COMM_103, "仅限全球通金卡及以上客户办理!");
		                }
		            }
		            for (int i = 0; i < halfs.size(); i++) {
		                if(discntCode.equals(halfs.getData(i).getString("DATA_ID"))){
		                	CSAppException.apperr(CrmCommException.CRM_COMM_103, "仅限全球通银卡客户办理!");
		                }
		            }
		        }
			}			
		}		
		
		return data;
	}
	
	/*
	* @Function: getDiscnts
    * @Description:	获取可办理套餐接口
    * @param: @param data
    * @param: @return
    * @param: @throws Exception
    * @return：IData
    */
	public IData getDiscnts(IData input) throws Exception {

		IData data = new DataMap();
		data.put("X_RESULTCODE",  "0000");
		data.put("X_RESULTINFO",  "正常");
		IDataset discntInfoList = new DatasetList();
		
		if(!StringUtils.isBlank(input.getString("SERIAL_NUMBER"))){
			IData res = checkQualificate(input);
			if(!"0000".equals(res.getString("X_RESULTCODE"))){
				data.put("X_RESULTCODE", res.getString("X_RESULTCODE"));
				data.put("X_RESULTINFO", res.getString("X_RESULTINFO"));
			}else{
				IDataset commparaInfos9211 = CommparaInfoQry.queryComparaByAttrAndCode1("CSM","9211",null,null);
				if(IDataUtil.isNotEmpty(commparaInfos9211)){
					 for(int i=0;i<commparaInfos9211.size();i++){
						 IData info = new DataMap();
						 info.put("DISCNT_CODE", commparaInfos9211.getData(i).getString("PARAM_CODE"));
						 info.put("CLASS_TYPE", commparaInfos9211.getData(i).getString("PARA_CODE1"));
						 info.put("DISCNT_NAME", commparaInfos9211.getData(i).getString("PARA_CODE3"));
						 info.put("DEVICE_NAME", commparaInfos9211.getData(i).getString("PARA_CODE17"));
						 if(!"1".equals( commparaInfos9211.getData(i).getString("PARA_CODE8"))){
							 discntInfoList.add(info);
						 }

					 }
					 data.put("DISCNT_INFO_LIST", discntInfoList);
				}
			}
			
		}else{
			IDataset commparaInfos9211 = CommparaInfoQry.queryComparaByAttrAndCode1("CSM","9211",null,null);
			if(IDataUtil.isNotEmpty(commparaInfos9211)){
				for(int i=0;i<commparaInfos9211.size();i++){
					 IData info = new DataMap();
					 info.put("DISCNT_CODE", commparaInfos9211.getData(i).getString("PARAM_CODE"));
					 info.put("CLASS_TYPE", commparaInfos9211.getData(i).getString("PARA_CODE1"));
					 info.put("DISCNT_NAME", commparaInfos9211.getData(i).getString("PARA_CODE3"));
					 info.put("DEVICE_NAME", commparaInfos9211.getData(i).getString("PARA_CODE17"));
					 if(!"1".equals( commparaInfos9211.getData(i).getString("PARA_CODE8"))){
						 discntInfoList.add(info);
					 }
				 }
				 data.put("DISCNT_INFO_LIST", discntInfoList);
			}
		}
		
		
		return data;
	}
	
	/**
	 * 获取可办理套餐接口
	 * @author: wuwangfeng
	 * @date: 2020/5/12 9:42
	 */
	public IData getDiscntsNew(IData input) throws Exception {
		IData data = new DataMap();
		data.put("X_RESULTCODE", "0000");
		data.put("X_RESULTINFO", "操作成功");
		IDataset discntInfoList = new DatasetList();
						
		IDataset commparaInfos9211 = CommparaInfoQry.queryComparaByAttrAndCode1("CSM","9211",null,null);
		if(IDataUtil.isNotEmpty(commparaInfos9211)){
			for(int i=0;i<commparaInfos9211.size();i++){
				String para_code13 = commparaInfos9211.getData(i).getString("PARA_CODE13");
				if("1".equals(para_code13) || "2".equals(para_code13) || "3".equals(para_code13)){
					IData info = new DataMap();
					info.put("DISCNT_CODE", commparaInfos9211.getData(i).getString("PARAM_CODE"));
					info.put("CLASS_TYPE", commparaInfos9211.getData(i).getString("PARA_CODE1"));
					info.put("DISCNT_NAME", commparaInfos9211.getData(i).getString("PARA_CODE3"));
					info.put("DEVICE_NAME", commparaInfos9211.getData(i).getString("PARA_CODE17"));
					info.put("DISPLAY_TYPE", commparaInfos9211.getData(i).getString("PARA_CODE13"));
					discntInfoList.add(info);						
				}
		    }
			data.put("DISCNT_INFO_LIST", discntInfoList);
		}
				
		return data;
	}
	
	/*
	* @Function: dredgeSubmit
    * @Description:	提交业务接口
    * @param: @param data
    * @param: @return
    * @param: @throws Exception
    * @return：IData
    */
	public IData dredgeSubmit(IData input) throws Exception {

		IData data = new DataMap();
		data.put("X_RESULTCODE",  "0000");
		data.put("X_RESULTINFO",  "提交成功"); 
		
		if(StringUtils.isBlank(input.getString("FIRST_TYPE"))&&StringUtils.isBlank(input.getString("THIRD_TYPE"))){
			data.put("X_RESULTCODE", "1002");
			data.put("X_RESULTINFO", "第一类“和优家”或第三类是“宽带装维延伸服务”必选一项！");
		}
		
		if(StringUtils.isBlank(input.getString("FIRST_TYPE"))&&StringUtils.isNotBlank(input.getString("SECOND_TYPE"))){
			data.put("X_RESULTCODE", "1003");
			data.put("X_RESULTINFO", "选择第二类前需选第一类！");
		}
		
		if(StringUtils.isNotBlank(input.getString("FIRST_TYPE"))&&StringUtils.isNotBlank(input.getString("THIRD_TYPE"))){
			data.put("X_RESULTCODE", "1004");
			data.put("X_RESULTINFO", "第一类“和优家”或第三类是“宽带装维延伸服务”只能选一项！");
		}
		
		IData check = checkFeeBeforeSubmit(input);
		if(!"0000".equals(check.getString("X_RESULTCODE"))&&!"0000".equals(data.getString("X_RESULTCODE"))){
			data.put("X_RESULTCODE", check.getString("X_RESULTCODE"));
			data.put("X_RESULTINFO", check.getString("X_RESULTINFO"));
		}else{
			input.put("TRADE_TYPE_CODE", "870");
			IData callData = CSAppCall.call( "SS.DredgeSmartNetworkRegSVC.tradeReg", input).getData(0);
			data.put("TRADE_ID",  callData.getString("TRADE_ID", ""));
		}
		
		return data;
	}
	
	/**
	 * 提交业务接口
	 * @author: wuwangfeng
	 * @date: 2020/5/12 9:42
	 */
	public IData dredgeSubmitNew(IData input) throws Exception {
		IData data = new DataMap();
		data.put("X_RESULTCODE", "0000");
		data.put("X_RESULTINFO", "操作成功");
		
		// 优惠校验
		if(!"".equals(input.getString("THIRD_TYPE", ""))){
			input.put("CHECK_CODE", "2");
			input.put("DISCNT_CODE", input.getString("THIRD_TYPE"));
			IData res = checkQualificateNew(input);
			if(!"0000".equals(res.getString("X_RESULTCODE"))){
				data.put("X_RESULTCODE", res.getString("X_RESULTCODE"));
				data.put("X_RESULTINFO", res.getString("X_RESULTINFO"));
				return data;
			}
		}		
		
		if(StringUtils.isBlank(input.getString("FIRST_TYPE")) && StringUtils.isBlank(input.getString("THIRD_TYPE"))){
			data.put("X_RESULTCODE", "1002");
			data.put("X_RESULTINFO", "全家WIFI礼包（设备+调试）和全家WIFI调测服务包必选一项！");
			return data;
		}
		
		if(StringUtils.isBlank(input.getString("FIRST_TYPE")) && StringUtils.isNotBlank(input.getString("SECOND_TYPE"))){
			data.put("X_RESULTCODE", "1003");
			data.put("X_RESULTINFO", "选择全家WIFI礼包（新增接入点）前需选择全家WIFI礼包（设备+调试）!");
			return data;
		}
		
		if(StringUtils.isNotBlank(input.getString("FIRST_TYPE")) && StringUtils.isNotBlank(input.getString("THIRD_TYPE"))){
			data.put("X_RESULTCODE", "1004");
			data.put("X_RESULTINFO", "全家WIFI礼包（设备+调试）和全家WIFI调测服务包只能选一项！");
			return data;
		}
		
		IData check = checkFeeBeforeSubmit(input);
		if(!"0000".equals(check.getString("X_RESULTCODE"))){
			data.put("X_RESULTCODE", check.getString("X_RESULTCODE"));
			data.put("X_RESULTINFO", check.getString("X_RESULTINFO"));
		}else{
			input.put("TRADE_TYPE_CODE", "870");
			IData callData = CSAppCall.call( "SS.DredgeSmartNetworkRegSVC.tradeReg", input).getData(0);
			data.put("TRADE_ID",  callData.getString("TRADE_ID", ""));
		}
				
		return data;
	}
	
	/*
	* @Function: checkQualificate
    * @Description:	提交前费用校验接口
    * @param: @param data
    * @param: @return
    * @param: @throws Exception
    * @return：IData
    */
	public IData checkFeeBeforeSubmit(IData input) throws Exception {

		IData data = new DataMap();
		IDataUtil.chkParam(input, "SERIAL_NUMBER");
		data.put("X_RESULTCODE",  "0000");
		data.put("X_RESULTINFO",  "可以办理!");
		data.put("RESULTE_TIME", SysDateMgr.getSysDate("yyyyMMddHHmmss"));
		
    	String firstType = input.getString("FIRST_TYPE");
    	String secondType = input.getString("SECOND_TYPE");
    	String thirdType = input.getString("THIRD_TYPE");

    	String isMonth="0";
        BigDecimal fee =new BigDecimal(0) ;
    	if(!(StringUtils.isBlank(firstType)&&StringUtils.isBlank(secondType)&&StringUtils.isBlank(thirdType))){
    		
    		IDataset commparaInfos9211 = CommparaInfoQry.queryComparaByAttrAndCode1("CSM","9211",null,null);
        	if(IDataUtil.isNotEmpty(commparaInfos9211)){
        		String firstName =null;
        		String secondName =null;
        		String thirdName =null;
        		for(int i=0;i<commparaInfos9211.size();i++){
					//REQ202003240027  优化智能组网资费的需求—BOSS侧 按月套餐校验单月费用
					int paraCode9 = commparaInfos9211.getData(i).getInt("PARA_CODE9",1);//按月套餐月份

                    if(commparaInfos9211.getData(i).getString("PARAM_CODE").equals(firstType)){
				    	if(StringUtils.isNotBlank(commparaInfos9211.getData(i).getString("PARA_CODE4"))){
				    	   firstName = commparaInfos9211.getData(i).getString("PARA_CODE3");
							fee=new BigDecimal(commparaInfos9211.getData(i).getString("PARA_CODE4","0"))
									.divide(new BigDecimal(paraCode9), 0)
									.add(fee);
				    	}
                        if(paraCode9>1){
                            isMonth="1";
                        }
				    }
				    if(commparaInfos9211.getData(i).getString("PARAM_CODE").equals(secondType)){
				    	if(StringUtils.isNotBlank(commparaInfos9211.getData(i).getString("PARA_CODE4"))){
				    		secondName = commparaInfos9211.getData(i).getString("PARA_CODE3");
							fee=new BigDecimal(commparaInfos9211.getData(i).getString("PARA_CODE4","0"))
									.divide(new BigDecimal(paraCode9), 0)
									.add(fee);
				    	}
                        if(paraCode9>1){
                            isMonth="1";
                        }
				    }
                    if(commparaInfos9211.getData(i).getString("PARAM_CODE").equals(thirdType)){
                    	if(StringUtils.isNotBlank(commparaInfos9211.getData(i).getString("PARA_CODE4"))){
                    		thirdName = commparaInfos9211.getData(i).getString("PARA_CODE3");
							fee=new BigDecimal(commparaInfos9211.getData(i).getString("PARA_CODE4","0"))
									.divide(new BigDecimal(paraCode9), 0)
									.add(fee);
				    	}
                        if(paraCode9>1){
                            isMonth="1";
                        }
				    }


        		}
        		
        		String serialNumber = input.getString("SERIAL_NUMBER");	    	
                String leftFee = WideNetUtil.qryBalanceDepositBySn(serialNumber);
                
                String errMsg =null;
                if(new BigDecimal(leftFee).compareTo(fee) < 0)
                {  
                	if(!(StringUtils.isBlank(firstType)&&StringUtils.isBlank(secondType)&&StringUtils.isBlank(thirdType))){
                		 errMsg="办理需要"+fee.divide(new BigDecimal(100), 0)+"元；您的账户存折可用余额不足，请先办理缴费!";
                	}
//                	if(StringUtils.isNotBlank(firstType)&&StringUtils.isNotBlank(secondType)){
//                		 errMsg="办理"+firstName+";"+secondName+"需要"+fee.divide(new BigDecimal(100), 0)+"元；您的账户存折可用余额不足，请先办理缴费!";
//                	}
//                	if(StringUtils.isNotBlank(firstType)&&StringUtils.isNotBlank(secondType)&&StringUtils.isNotBlank(thirdType)){
//                		 errMsg="办理"+firstName+";"+secondName+";"+thirdName+"需要"+fee.divide(new BigDecimal(100), 0)+"元；您的账户存折可用余额不足，请先办理缴费!";
//                	}
                      
                	data.put("X_RESULTCODE",  "61314");
            		data.put("X_RESULTINFO",  errMsg);
                }
//                else{
//                	if(StringUtils.isNotBlank(firstType)){
//               		    errMsg="办理"+firstName+"需要"+fee.divide(new BigDecimal(100), 0)+"元。";
//               	    }
//	               	if(StringUtils.isNotBlank(firstType)&&StringUtils.isNotBlank(secondType)){
//	               		 errMsg="办理"+firstName+";"+secondName+"需要"+fee.divide(new BigDecimal(100), 0)+"元。";
//	               	}
//	               	if(StringUtils.isNotBlank(thirdType)){
//	               		 errMsg="办理"+thirdName+"需要"+fee.divide(new BigDecimal(100), 0)+"元。";
//	               	}
//                	data.put("X_RESULTINFO",  errMsg);
//                }
        	}
    		
    	}
        data.put("IS_MONTH",  isMonth);
        data.put("ALL_FEE",fee.divide(new BigDecimal(100), 0));
		return data;
	}
	
	
	/**
     * 获取宽带用户信息
     * @param SerialNumber
     * @return
     * @throws Exception
     */
    public static IDataset getUserWidenetInfoBySerialNumber(IData param) throws Exception
    {
        param.put("SERIAL_NUMBER", "KD_"+param.getString("SERIAL_NUMBER"));
        return Dao.qryByCodeParser("TF_F_USER_WIDENET", "SEL_BY_SERIAL_NUMBER_WIDENET", param);
    }
    
	
	/*
	* @Function: dredgeSubmit
    * @Description:	是否办理相关业务办理查询接口
    * @param: @param data
    * @param: @return
    * @param: @throws Exception
    * @return：IData
    */
	public IData queryTradedBySn(IData input) throws Exception {
		
		String serialNumber = IDataUtil.chkParam(input, "SERIAL_NUMBER");
		
		IData data = new DataMap();
		data.put("X_RESULTCODE",  "0000");
		data.put("X_RESULTINFO",  "请求成功"); 
		data.put("TRADETYPE1",  "0");
		data.put("TRADETYPE2",  "0");
		data.put("TRADETYPE3",  "0");
		data.put("TRADETYPE4",  "0");
		
		String wideSerialNumber="";
		if(serialNumber.startsWith("KD_")){
			serialNumber = serialNumber.replace("KD_", "");
		}else{
			wideSerialNumber = "KD_"+serialNumber;
		}
		
		IData userInfo = UcaInfoQry.qryUserInfoBySnAllCrm(serialNumber);
	    if (IDataUtil.isNotEmpty(userInfo))
        {
	    	String userId = userInfo.getString("USER_ID");
	    	
	    	 //是否办理和目业务 0 未办理，1办理
	        IDataset heMuSaleActives = UserSaleActiveInfoQry.getHeMuSaleActiveByUserId2(userId);
	        if (IDataUtil.isNotEmpty(heMuSaleActives))
	        {
	        	data.put("TRADETYPE3",  "1");
	        } 
	        
	        //是否办理办理智能组网 0 未办理，1办理
	        IDataset commparaInfos9211 = CommparaInfoQry.queryComparaByAttrAndCode1("CSM","9211",null,null);
	        if(IDataUtil.isNotEmpty(commparaInfos9211)){
	        	boolean flg = false;
	            for(int i=0;i<commparaInfos9211.size();i++){
	                IDataset allDiscnt = UserDiscntInfoQry.getAllDiscntByUser_3(userId,null);
	                String paramCode = commparaInfos9211.getData(i).getString("PARAM_CODE");
	                if(IDataUtil.isNotEmpty(allDiscnt)){
	                	 for(int j=0;j<allDiscnt.size();j++){
	                		 String discntCode = allDiscnt.getData(j).getString("DISCNT_CODE","");
	                		 if(discntCode.equals(paramCode)){
	                			 flg = true ;
	                			 break;
	                		 }
	                	 }
	                }
	            }
	            if (flg)
	            {
	            	data.put("TRADETYPE4",  "1");
	            } 
	        }
	        
	        IData wideUserInfo = UcaInfoQry.qryUserInfoBySnAllCrm(wideSerialNumber);
	        if (IDataUtil.isNotEmpty(wideUserInfo)){
	        	//判断是否无手机宽带
				IData wideTypeInfo = WideNetUtil.getWideNetTypeInfo(serialNumber);
				boolean noPhoneflg = false;
				if ("Y".equals(wideTypeInfo.getString("IS_NOPHONE_WIDENET"))) {
					noPhoneflg = true;
				}
				
				//是否办理魔百和业务 0 未办理，1办理
				IData wUserInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
				String wUserId = wUserInfo.getString("USER_ID");
				if(noPhoneflg){
					String userIdB = wUserInfo.getString("USER_ID");
					IData userInfoA = getRelaUUInfoByUserIdB(userIdB);
					wUserId = userInfoA.getString("USER_ID_A");
				}
				IDataset platSvcInfos = UserPlatSvcInfoQry.getPlatSvcByUserBizType(wUserId, "51");//biz_type_code=51为互联网电视类的平台服务
		        if (IDataUtil.isNotEmpty(platSvcInfos))
		        {
		        	data.put("TRADETYPE1",  "1");
		        }
		        
		        //是否办理 IMS 0 未办理，1办理
		        IDataset uuInfo = RelaUUInfoQry.getRelationUUInfoByDeputySn(wideTypeInfo.getString("USER_ID"), "MS",null);
		        if (IDataUtil.isNotEmpty(uuInfo))
		        {
		        	data.put("TRADETYPE2",  "1");
		        } 
	        	
	        }
	        	
        }
		
		
        
		
		return data ;
	}
	
	 //通过UU关系表，获取147号码
    public IData getRelaUUInfoByUserIdB(String userIdB) throws Exception
    {
        IDataset relaUUInfos = RelaUUInfoQry.getAllRelationByUidBRelaTypeRoleB(userIdB,"47","1");
        if(IDataUtil.isEmpty(relaUUInfos))
        {
        	return null; 
        }
        return relaUUInfos.first();
    }
    
}
