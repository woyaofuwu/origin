
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.component.saleactive;

import java.math.BigDecimal;
import java.util.List;

import com.asiainfo.veris.crm.order.soa.frame.bcf.query.RouteInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import org.apache.log4j.Logger;

import com.ailk.common.BaseException;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.util.Utility;
import com.ailk.database.dao.DAOManager;
import com.ailk.database.dao.impl.BaseDAO;
import com.ailk.database.dbconn.DBConnection;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.session.SessionManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SaleActiveTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.acct.AcctInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;

public class SaleActiveCheckSnBean extends CSBizBean
{
	private static Logger logger = Logger.getLogger(SaleActiveCheckSnBean.class);
    public IData checkIfUseHaveNeedProd(IData params) throws Exception
    {	
    	IData rtnData=new DataMap();
    	
    	String productId =params.getString("PRODUCT_ID","");
    	String packageId = params.getString("PACKAGE_ID","");
    	String userId=params.getString("USER_ID","");
    	if (logger.isDebugEnabled())
            logger.debug(">>>>> 进入 SaleActiveCheckSnBean>>>>>USER_ID:"+userId+",PRODUCT_ID:"+productId+",PACKAGE_ID:"+packageId);
    	//1、查询配置表，是否存在被办理的产品配置
    	IDataset commparas=CommparaInfoQry.getCommparaAllColByParser("CSM","9956", productId, "0898");
    	if(commparas !=null && commparas.size()>0){ 
    		for(int i =0;i<commparas.size();i++){
    			String needCheckProd=commparas.getData(i).getString("PARA_CODE1");//需要校验的产品
    			//校验的产品是要求用户存在还是不存在，0-要求没有  1-要求有
    			String needCheckIfExist=commparas.getData(i).getString("PARA_CODE2");
    			//老用户证件和新用户证件号码校验的，0-要求没有  1-要求有
    			String needCheckPsidExist=commparas.getData(i).getString("PARA_CODE3");
    			
		    	IData inData=new DataMap();
		    	String checkSN=params.getString("CHECK_SERIAL_NUMBER","");
		    	
		    	
		    	String checkUserId="";
		    	IDataset checkUserInfos=UserInfoQry.getAllUserInfoBySn(checkSN);
		    	if(checkUserInfos!=null && checkUserInfos.size()>0){
		    		for(int k=0;k<checkUserInfos.size();k++){
		    			String removeTag=checkUserInfos.getData(k).getString("REMOVE_TAG","");
		    			if("0".equals(removeTag)){
		    				checkUserId=checkUserInfos.getData(k).getString("USER_ID","");
		    			}
		    		}
		    		if("".equals(checkUserId)){
		    			rtnData.put("SN_HAVE_PRODUCT", "N");
			        	rtnData.put("SN_HAVE_USE", "N");
			        	return rtnData;
		    		}
		    	}else{
		    		rtnData.put("SN_HAVE_PRODUCT", "N");
		        	rtnData.put("SN_HAVE_USE", "N");
		        	return rtnData;
		    	}
		    	inData.put("CHECK_USER_ID", checkUserId);
		    	inData.put("PRODUCT_ID", needCheckProd);
		        IDataset userProds=this.checkIfUserHaveProduct(inData);
		        //1、第一种情况：要求被校验手机号要存在该产品。
		        if("1".equals(needCheckIfExist)){
			        if(userProds!=null && userProds.size()>0){
			        	rtnData.put("SN_HAVE_PRODUCT", "Y");
			        	String isUsed=userProds.getData(0).getString("RSRV_TAG2");
			        	
			        	String psptid=UcaDataFactory.getUcaByUserId(userId).getCustomer().getPsptId();
			        	String oldpsptid=UcaDataFactory.getUcaByUserId(checkUserId).getCustomer().getPsptId();
			        	if("1".equals(needCheckPsidExist)){	
				        	if(!psptid.equals(oldpsptid)||("".equals(psptid)&&"".equals(oldpsptid))){
				        		rtnData.put("SN_HAVE_USE", "Z"); 
				        		break;
				        	}	
			        	}
			        	if("Y".equals(isUsed)){
			        		//判断用户是否被校验过：只要存在一个就退出循环
			        		rtnData.put("SN_HAVE_USE", "Y"); 
			        		break;
			        	}else{
			        		rtnData.put("SN_HAVE_USE", "N");
			        		
			        		String toDay=SysDateMgr.getSysDateYYYYMMDD();
			        		IData input = new DataMap();
			            	input.put("USER_ID", userId);
			            	input.put("TRADE_TYPE_CODE", "240");
			            	input.put("TO_DAY", toDay);
			            	IDataset oldCustInfos=this.qryTempInfoByUserId(input);
			            	if(oldCustInfos!=null &&oldCustInfos.size()>0){
			            		input.put("RSRV_STR1", checkSN); //输入的老用户手机号
			            		input.put("RSRV_STR2", productId);//办理的活动PRODUCT_ID
			            		input.put("RSRV_STR3", needCheckProd);//老用户要求存在的PRODUCT_ID 
			            		input.put("RSRV_STR4", checkUserId);//老用户的USER_ID 
			            		this.updTemplate(input);			            		
			            	}else{
				        		IData inparams=new DataMap();
				        		inparams.put("TRADE_TYPE_CODE", "240");
				        		inparams.put("USER_ID", userId);//办理活动用户的USER_ID
				        		inparams.put("DEAL_TAG", "0");   
				        		inparams.put("RSRV_STR1", checkSN); //输入的老用户手机号
				        		inparams.put("RSRV_STR2", productId); //办理的活动PRODUCT_ID
				        		inparams.put("RSRV_STR3", needCheckProd);//老用户要求存在的PRODUCT_ID
				        		inparams.put("RSRV_STR4", checkUserId);//老用户的USER_ID
				        		inparams.put("RSRV_STR5", toDay);//要处理的日期YYYYMMDD
				        		this.insTemplate(inparams);
			            	}
			        		break;
			        	}
			        }else{
			        	rtnData.put("SN_HAVE_PRODUCT", "N");
			        	rtnData.put("SN_HAVE_USE", "N");
			        } 
		        }
    		}
    	}else{
    		rtnData.put("SN_HAVE_USE", "N");
    		rtnData.put("SN_HAVE_PRODUCT", "N");
    	}
    	
    	//add by zhangxing3 for 输入的号码须办理PARA_CODE1活动包后且新老用户号码证件号一致方可办理PARAM_CODE活动包
    	String paramCode=productId+"|"+packageId;
    	IDataset commparainfo=CommparaInfoQry.getCommparaAllColByParser("CSM","9771", paramCode, "0898");
    	if (logger.isDebugEnabled())
            logger.debug(">>>>> 进入 SaleActiveCheckSnBean1>>>>>commparainfo:"+commparainfo);
    	if(commparainfo !=null && commparainfo.size()>0){ 
    		 
    		for(int i =0;i<commparainfo.size();i++){
    			
    			if("-1".equals(commparainfo.getData(i).getString("PARA_CODE1")))
    			{
    				//0：日；1：月；
    				String dateformaT = commparainfo.getData(i).getString("PARA_CODE3");
    				String dateformatnuM = commparainfo.getData(i).getString("PARA_CODE2");
    				String infomsG = commparainfo.getData(i).getString("PARA_CODE20");
    				String checkSN=params.getString("CHECK_SERIAL_NUMBER","");
    				IDataset checkUserInfos=UserInfoQry.getAllUserInfoBySn(checkSN);
    				if(IDataUtil.isNotEmpty(checkUserInfos)){
    					String opendatE =  checkUserInfos.getData(0).getString("OPEN_DATE");
    					
    					String checkdatS = "";
    					String checkuserID =  checkUserInfos.getData(0).getString("USER_ID");
    					if("0".equals(dateformaT))
        				{
    						checkdatS =SysDateMgr.addDays(SysDateMgr.getSysDate(), -Integer.parseInt(dateformatnuM));
        				}else if ("1".equals(dateformaT)) {
        					checkdatS = SysDateMgr.addMonths(SysDateMgr.getSysDate(), -Integer.parseInt(dateformatnuM));
    					}else{
    						rtnData.put("SN_HAVE_PRODUCT", "K");
    			        	rtnData.put("SN_MSG", infomsG);
    			        	return rtnData;
    					}
    					opendatE = SysDateMgr.addDays(opendatE,0);
    					
    					if(opendatE.compareTo(checkdatS)<0)
    					{
    						rtnData.put("SN_HAVE_PRODUCT", "K");
    			        	rtnData.put("SN_MSG", infomsG);
    			        	return rtnData;
    					}else
    					{
    						rtnData.put("SN_HAVE_PRODUCT", "Y");
    						
    						IDataset checktive = UserOtherInfoQry.getOtherInfoByPspt("4GGSALEACTIVE", checkSN, checkuserID);
    						if(IDataUtil.isNotEmpty(checktive))
    						{
    							rtnData.put("SN_HAVE_USE", "Y");
    							return rtnData;
    						}
    						
    						rtnData.put("SN_HAVE_USE", "N");		        		
			        		String toDay=SysDateMgr.getSysDateYYYYMMDD();
			        		IData input = new DataMap();
			            	input.put("USER_ID", userId);
			            	input.put("TRADE_TYPE_CODE", "240");
			            	input.put("TO_DAY", toDay);
			            	input.put("RSRV_STR2", "Y");
			            	IDataset oldCustInfos=this.qryTempInfoByUserId(input);
			            	if(oldCustInfos!=null &&oldCustInfos.size()>0){
			            		input.put("RSRV_STR1", checkSN); //输入的老用户手机号
			            		input.put("RSRV_STR2", "Y");
			            		input.put("RSRV_STR3", opendatE);
			            		input.put("RSRV_STR4", checkuserID);//老用户的USER_ID 
			            		this.updTemplate(input);			            		
			            	}else{
				        		IData inparams=new DataMap();
				        		inparams.put("TRADE_TYPE_CODE", "240");
				        		inparams.put("USER_ID", userId);//办理活动用户的USER_ID
				        		inparams.put("DEAL_TAG", "0");   
				        		inparams.put("RSRV_STR1", checkSN); //输入的老用户手机号
				        		inparams.put("RSRV_STR2", "Y"); 
				        		inparams.put("RSRV_STR3", opendatE);
				        		inparams.put("RSRV_STR4", checkuserID);//老用户的USER_ID
				        		inparams.put("RSRV_STR5", toDay);//要处理的日期YYYYMMDD
				        		this.insTemplate(inparams);
			            	}
			            	return rtnData;      	
    					}
			    	}else{
			    		rtnData.put("SN_HAVE_PRODUCT", "K");
			    		rtnData.put("SN_MSG", infomsG);
			        	return rtnData;
			    	}
    			}else{   			
	    			String needCheckProd=commparainfo.getData(i).getString("PARA_CODE1").substring(0, 8);//需要校验的产品
	    			String needCheckPack=commparainfo.getData(i).getString("PARA_CODE1").substring(9, 17);//需要校验的产品包
	    			if (logger.isDebugEnabled())
	    	            logger.debug(">>>>> 进入 SaleActiveCheckSnBean2>>>>>needCheckProd:"+needCheckProd+",needCheckPack:"+needCheckPack);
	    			//校验的产品是要求用户存在还是不存在，0-要求没有  1-要求有
	    			String needCheckIfExist=commparainfo.getData(i).getString("PARA_CODE2");
	    			//老用户证件和新用户证件号码校验的，0-要求没有  1-要求有
	    			String needCheckPsidExist=commparainfo.getData(i).getString("PARA_CODE3");
	    			
			    	IData inData=new DataMap();
			    	String checkSN=params.getString("CHECK_SERIAL_NUMBER","");
			    	
			    	
			    	String checkUserId="";
			    	IDataset checkUserInfos=UserInfoQry.getAllUserInfoBySn(checkSN);
			    	if (logger.isDebugEnabled())
	    	            logger.debug(">>>>> 进入 SaleActiveCheckSnBean3>>>>>checkUserInfos:"+checkUserInfos);
			    	if(checkUserInfos!=null && checkUserInfos.size()>0){
			    		for(int k=0;k<checkUserInfos.size();k++){
			    			String removeTag=checkUserInfos.getData(k).getString("REMOVE_TAG","");
			    			if("0".equals(removeTag)){
			    				checkUserId=checkUserInfos.getData(k).getString("USER_ID","");
			    			}
			    		}
			    		if("".equals(checkUserId)){
			    			rtnData.put("SN_HAVE_PRODUCT", "N");
				        	rtnData.put("SN_HAVE_USE", "N");
				        	return rtnData;
			    		}
			    	}else{
			    		rtnData.put("SN_HAVE_PRODUCT", "N");
			        	rtnData.put("SN_HAVE_USE", "N");
			        	return rtnData;
			    	}
			    	inData.put("CHECK_USER_ID", checkUserId);
			    	inData.put("PRODUCT_ID", needCheckProd);
			    	inData.put("PACKAGE_ID", needCheckPack);
			        IDataset userPacks=this.checkIfUserHavePackage(inData);
			        if (logger.isDebugEnabled())
	    	            logger.debug(">>>>> 进入 SaleActiveCheckSnBean5>>>>>userPacks:"+userPacks);
			        //1、第一种情况：要求被校验手机号要存在该产品包。
			        if("1".equals(needCheckIfExist)){
				        if(userPacks!=null && userPacks.size()>0){
				        	rtnData.put("SN_HAVE_PRODUCT", "Y");
				        	String isUsed=userPacks.getData(0).getString("RSRV_TAG2");
				        	
				        	String psptid=UcaDataFactory.getUcaByUserId(userId).getCustomer().getPsptId();
				        	String oldpsptid=UcaDataFactory.getUcaByUserId(checkUserId).getCustomer().getPsptId();
				        	if("1".equals(needCheckPsidExist)){	
					        	if(!psptid.equals(oldpsptid)||("".equals(psptid)&&"".equals(oldpsptid))){
					        		rtnData.put("SN_HAVE_USE", "Z"); 
					        		break;
					        	}	
				        	}
				        	if("Y".equals(isUsed)){
				        		//判断用户是否被校验过：只要存在一个就退出循环
				        		rtnData.put("SN_HAVE_USE", "Y"); 
				        		break;
				        	}else{
				        		rtnData.put("SN_HAVE_USE", "N");
				        		
				        		String toDay=SysDateMgr.getSysDateYYYYMMDD();
				        		IData input = new DataMap();
				            	input.put("USER_ID", userId);
				            	input.put("TRADE_TYPE_CODE", "240");
				            	input.put("TO_DAY", toDay);
				            	IDataset oldCustInfos=this.qryTempInfoByUserId(input);
				            	if(oldCustInfos!=null &&oldCustInfos.size()>0){
				            		input.put("RSRV_STR1", checkSN); //输入的老用户手机号
				            		input.put("RSRV_STR2", paramCode);//办理的活动PACKAGE_ID
				            		input.put("RSRV_STR3", needCheckProd+"|"+needCheckPack);//老用户要求存在的PACKAGE_ID 
				            		input.put("RSRV_STR4", checkUserId);//老用户的USER_ID 
				            		this.updTemplate(input);			            		
				            	}else{
					        		IData inparams=new DataMap();
					        		inparams.put("TRADE_TYPE_CODE", "240");
					        		inparams.put("USER_ID", userId);//办理活动用户的USER_ID
					        		inparams.put("DEAL_TAG", "0");   
					        		inparams.put("RSRV_STR1", checkSN); //输入的老用户手机号
					        		inparams.put("RSRV_STR2", paramCode); //办理的活动PACKAGE_ID
					        		inparams.put("RSRV_STR3", needCheckProd+"|"+needCheckPack);//老用户要求存在的PACKAGE_ID
					        		inparams.put("RSRV_STR4", checkUserId);//老用户的USER_ID
					        		inparams.put("RSRV_STR5", toDay);//要处理的日期YYYYMMDD
					        		this.insTemplate(inparams);
				            	}
				        		break;
				        	}
				        }else{
				        	rtnData.put("SN_HAVE_PRODUCT", "N");
				        	rtnData.put("SN_HAVE_USE", "N");
				        } 
			        }
	    		}
    		}
    	}
    	else
    	{
    		rtnData.put("SN_HAVE_USE", "N");
    		rtnData.put("SN_HAVE_PRODUCT", "N");
    	}
    	if (logger.isDebugEnabled())
            logger.debug(">>>>> 进入 SaleActiveCheckSnBean6>>>>>rtnData:"+rtnData);
        return rtnData;
    }

    /**
     * 查询录入的手机号是否存在营销活动产品
     * */
    public static IDataset checkIfUserHaveProduct(IData inData)throws Exception
    {
    	IData param=new DataMap();
    	param.put("USER_ID", inData.getString("CHECK_USER_ID"));   
    	param.put("PRODUCT_ID", inData.getString("PRODUCT_ID"));   
        return Dao.qryByCode("TF_F_USER_SALE_ACTIVE", "SEL_SALE_ACTIVE_SN_IFUSE", param);
    }
    
    /**
     * 查询录入的手机号是否存在营销活动产品包
     * */
    public IDataset checkIfUserHavePackage(IData inData)throws Exception
    {
    	IData param=new DataMap();
    	param.put("USER_ID", inData.getString("CHECK_USER_ID"));
    	param.put("PRODUCT_ID", inData.getString("PRODUCT_ID"));
    	param.put("PACKAGE_ID", inData.getString("PACKAGE_ID"));   
        return Dao.qryByCode("TF_F_USER_SALE_ACTIVE", "SEL_SALE_ACTIVE_UP_IFUSE", param);
    }
    
    
    /**
     * 老用户号码满足要求，需要插到临时表，用于之后办理活动。
     * */
    public void insTemplate(IData inparams) throws Exception
    {

        Dao.executeUpdateByCodeCode("TF_F_ACTIVE_TEMPLATE", "INS_F_ACTIVE_TEMPLATE", inparams);
    }
    
    /**
     * 查询临时表已经保存的老用户信息
     * */
    public IDataset qryTempInfoByUserId(IData input) throws Exception
    {
    	SQLParser parser = new SQLParser(input); 
  	  
        parser.addSQL(" select T.* "); 
        parser.addSQL(" from TF_F_ACTIVE_TEMPLATE t ");
        parser.addSQL(" where t.trade_type_code=:TRADE_TYPE_CODE ");
        parser.addSQL(" AND T.USER_ID=:USER_ID "); 
        parser.addSQL(" AND T.DEAL_TAG='0' "); 
        parser.addSQL(" AND T.RSRV_STR5=:TO_DAY "); 
        parser.addSQL(" AND T.RSRV_STR2=:RSRV_STR2 "); 

        return Dao.qryByParse(parser); 
    	
    }
    /**
     * 老用户号码满足要求，需要插到临时表，如果已经存在，则只做更新
     * */
    public void updTemplate(IData inparams) throws Exception
    {

        Dao.executeUpdateByCodeCode("TF_F_ACTIVE_TEMPLATE", "UPD_ACTIVE_TEMPLATE_INFO", inparams);
    }
    
    /**
     * 办理完成后要删除临时表数据
     * */
    public void delTemplate(IData inparams) throws Exception
    {

        Dao.executeUpdateByCodeCode("TF_F_ACTIVE_TEMPLATE", "DEL_ACTIVE_TEMPLATE_INFO_240", inparams);
    }
    
    /**
     * 老用户号码所需要的营销活动打上已经校验的标记
     * */
    public void updCheckSnTag(IData inparams) throws Exception
    {

        Dao.executeUpdateByCodeCode("TF_F_USER_SALE_ACTIVE", "UPD_SALE_ACTIVE_SN_IFUSE", inparams);
    }
    /**
     * 老用户号码所需要的营销活动打上已经校验的标记
     * */
    public void updCheckSnTagByPack(IData inparams) throws Exception
    {

        Dao.executeUpdateByCodeCode("TF_F_USER_SALE_ACTIVE", "UPD_SALE_ACTIVE_SN_IFUSE_BY_PACK", inparams);
    }
    
    /**
     * 老号码校验：（按顺序，优先级）
     * 1、移动公话、无线固话 、随e行、物联卡、一卡双号副卡、一卡付多号的副卡号码不允许办理
     * 2、网龄是否满1年
     * 3、同证件号码已满5张
     * 4、是否办理过该活动
     * 5、是否欠费
     * 6、与新号码的身份证是否相同 
     * */
    public IData checkOldUseForActive(IData params) throws Exception
    {	
    	IData rtnData=new DataMap();
    	
    	String productId =params.getString("PRODUCT_ID","");
    	String packageId = params.getString("PACKAGE_ID","");
    	String userId=params.getString("USER_ID","");//老用户USER_ID
    	
    	return rtnData;
    }
    
    /**
     * 新号码校验：
     * 1、号码必须是激活不超过48小时的  OPEN_48_HOUR Y:48小时内 N:超过48小时
     * 2、与新号码的身份证是否相同      PSPT_ID_SAME   Y:相同    N：不相同
     * 3、是否办理过该活动   SN_HAVE_PRODUCT      Y:办理过   N:没办理过 
     * 4、不能是多终端共享业务数据     SHARE_INFO_TYPE  Y:属于   N:不属于
     * 5、不能统一付费业务数据。          RELATION_UU_TYPE  Y:属于   N:不属于
     * 6、0存折+332存折不能大于0  SN_FEE_TYPE    Y:大于0   N：不大于0  
     * */
    public IData checkNewUseForActive(IData params) throws Exception
    {	
    	IData rtnData=new DataMap();
    	
    	String productId =params.getString("PRODUCT_ID","");
    	String packageId = params.getString("PACKAGE_ID","");
    	String serialNumber=params.getString("AUTH_SERIAL_NUMBER");
    	String userId=params.getString("USER_ID","");//老用户USER_ID
    	String checkSN=params.getString("CHECK_SERIAL_NUMBER","");//新用户号码。
    	String checkUserId="";
    	 
    	String checkResult="1";
    	
    	String paramAttr="9957";
    	String prodId_b="";//新用户号码需要绑定的PRODUCT_ID 这里还没有选包，所以不保存PACKAGE_ID_B
    	String paraCode5="";//N:不用校验新、老号码证件号码是否一致 add by zhangxing3 for 大派送第二季
    	String paraCode7="";//N:不用校验第三季增加校验 add by lizj for 大派送第三季
    	String paraCode8="";//Y:校验第四季增加校验 add by lizj for 大派送第四季
    	String paraCode17="";//优化2018老客户感恩大派送（升级）-新增新卡预存50需求存折 add by lizj 
    	String paraCode11="";//REQ201906180019关于调整2018抢4G手机红包活动新卡规则的需求 add by wuhao5
    	IDataset commsets = CommparaInfoQry.getCommparaAllCol("CSM",paramAttr, productId, "0898"); 
    	if(commsets!=null && commsets.size()>0){
    		prodId_b=commsets.getData(0).getString("PARA_CODE3","");
    		paraCode5=commsets.getData(0).getString("PARA_CODE5","");//add by zhangxing3 for 大派送第二季
    		paraCode7=commsets.getData(0).getString("PARA_CODE7","");//add by lizj for 大派送第三季
    		paraCode8=commsets.getData(0).getString("PARA_CODE8","");//add by lizj for 大派送第四季
    		paraCode17=commsets.getData(0).getString("PARA_CODE17","");
    		paraCode11=commsets.getData(0).getString("PARA_CODE11","");
    	}

    	/*
    	 * 1、号码必须是激活不超过48消失的  OPEN_48_HOUR Y:48小时内 N:超过48小时
    	 * */
    	IData param=new DataMap();
    	param.put("CHECK_SERIAL_NUMBER", checkSN);
    	IDataset checkUserInfos = null;
    	if("TRUE".equals(params.getString("BAT_TAG","")))
    	{
    		 checkUserInfos=checkIfUserOpen48HourForBat(param);
    	}
    	else {
    		checkUserInfos=checkIfUserOpen48Hour(param);
    	}

    	if(checkUserInfos!=null && checkUserInfos.size()>0){
			rtnData.put("OPEN_48_HOUR", "Y");
			checkUserId=checkUserInfos.getData(0).getString("USER_ID");
    	}else{
    		rtnData.put("OPEN_48_HOUR", "N");
    		return rtnData;
    	}


    	IData inparam=new DataMap();
    	inparam.put("USER_ID", userId);
    	inparam.put("CHECK_USER_ID", checkUserId);
    	inparam.put("CHECK_SERIAL_NUMBER",params.getString("CHECK_SERIAL_NUMBER",""));
    	inparam.put("BAT_TAG",params.getString("BAT_TAG",""));//add by fufn 
    	
    	//优化2018老客户感恩大派送（升级）-新增新卡预存50条件的需求
    	inparam.clear();
    	inparam.put("SERIAL_NUMBER",checkSN);
    	if(checkBalanceDeposit(inparam,paraCode17)||StringUtils.isBlank(paraCode17))
    	{
    		/**       
	  	       * 新号码对应老号码是否作为主卡与新号码办统一付费业务   
	  	     * */  
	  		inparam.clear();
	  		inparam.put("USER_ID_B", userId);
	  		inparam.put("ROLE_CODE_B","1");
	  		inparam.put("RELATION_TYPE_CODE","56");
	  		inparam.put("CHECK_USER_ID",checkUserId);
	  		String ifmainCard = checkIfMainCard(inparam);
	  		if("true".equals(ifmainCard))
	  		{
	  			rtnData.put("MAIN_CARD", "Y");
	  		}
	  		else {
	  			rtnData.put("MAIN_CARD", "N");
	  			checkResult="0";
	  		}
    	}
				
		//第三次活动校验（大派送升级活动2018）
		if(!"N".equals(paraCode7)){
			
			 /**       
		       * 新号码是否办理亲亲网业务
		     * */  
			inparam.clear();
			inparam.put("USER_ID_B", checkUserId);
			inparam.put("RELATION_TYPE_CODE","45");
			String ifFamily = checkIfFamily(inparam);
			if("true".equals(ifFamily))
			{
				rtnData.put("FAMILY_CARD", "Y");
			}
			else {
				rtnData.put("FAMILY_CARD", "N");
				checkResult="0";
			}
			
			/**       
		       * 新号码是否办理宽带业务（或预约）
		     * */  
			inparam.clear();
			inparam.put("SERIAL_NUMBER", checkSN);
			inparam.put("CANCEL_TAG","0");
			inparam.put("TRADE_TYPE_CODE","600");
			String ifWidenet = checkIfWidenet(inparam);
			if("true".equals(ifWidenet))
			{
				rtnData.put("WIDENET_TYPE", "Y");
			}
			else {
				rtnData.put("WIDENET_TYPE", "N");
				checkResult="0";
			}
			
		}
		
		
		//第四次活动校验（购机活动）
		if("Y".equals(paraCode8)){
			 /**       
		       * 老号码是否办理亲亲网业务
		     * */  
			inparam.clear();
			inparam.put("USER_ID_B", userId);
			inparam.put("RELATION_TYPE_CODE","45");
			String ifFamilyOld = checkIfFamily(inparam);
			if("true".equals(ifFamilyOld))
			{
				rtnData.put("FAMILY_CARD_OLD", "Y");
			}
			else {
				rtnData.put("FAMILY_CARD_OLD", "N");
				checkResult="0";
			}
			
			/**       
		       * 老号码是否办理宽带业务（或预约）
		     * */  
			inparam.clear();
			inparam.put("SERIAL_NUMBER", serialNumber);
			inparam.put("CANCEL_TAG","0");
			inparam.put("TRADE_TYPE_CODE","600");
			String ifWidenetOld = checkIfWidenet(inparam);
			if("true".equals(ifWidenetOld))
			{
				rtnData.put("WIDENET_TYPE_OLD", "Y");
			}
			else {
				rtnData.put("WIDENET_TYPE_OLD", "N");
				checkResult="0";
			}
		}
		
    	/*
    	 * 2、与新号码的身份证是否相同      PSPT_ID_SAME Y:相同    N：不相同
    	 * */
    	//System.out.println("===============checkNewUseForActive=============PARA_CODE5:"+paraCode5);
    	if(!"N".equals(paraCode5)){//N:不用校验新、老号码证件号码是否一致 add by zhangxing3 for 大派送第二季
	    	String ifPsptSame=checkIfUserPsptIdSame(inparam);
	    	if("true".equals(ifPsptSame)){
	    		rtnData.put("PSPT_ID_SAME", "Y");
	    	}else{
	    		rtnData.put("PSPT_ID_SAME", "N");
	    		checkResult="0";
	    	}
    	}
    	/*3、是否办理过该活动   SN_HAVE_PRODUCT  Y:办理过   N:没办理过
    	 * */
    	inparam.clear();
    	inparam.put("USER_ID", checkUserId);
    	inparam.put("PRODUCT_ID", prodId_b); 
    	String ifHadAction=checkIfHadActive(inparam);
    	if("true".equals(ifHadAction)){
    		rtnData.put("SN_HAVE_PRODUCT", "Y");
   		checkResult="0";
    	}else{
    		rtnData.put("SN_HAVE_PRODUCT", "N"); 
    	}
  	
   	inparam.clear();
    	inparam.put("USER_ID", checkUserId);
        String strProductId = checkIfActive(inparam);
       if("true".equals(strProductId))
       {
        	rtnData.put("HAVE_ACTIVE", "Y");
    		checkResult="0";
        }
       else {
        	rtnData.put("HAVE_ACTIVE", "N");
        }
    	
   	/*4、不能是多终端共享业务数据     SHARE_INFO_TYPE  Y:属于   N:不属于
    	 * */
    	inparam.clear();
    	inparam.put("USER_ID_B", checkUserId);
    	if(!"N".equals(paraCode5)){//N:不用校验:不能是多终端共享业务数据   add by zhangxing3 for 大派送第二季
	    	String ifShareInfo=checkIfShareRelaInfo(inparam);
	    	if("true".equals(ifShareInfo)){
	    		rtnData.put("SHARE_INFO_TYPE", "Y");
	    		checkResult="0";
	    	}else{
	    		rtnData.put("SHARE_INFO_TYPE", "N");
	    	}
    	}
    	/*5、不能统一付费业务数据。          RELATION_UU_TYPE  Y:属于   N:不属于
   	 * */
    	if(!"N".equals(paraCode5)){//N:不用校验:不能统一付费业务数据   add by zhangxing3 for 大派送第二季
	    	String ifRelaInfo=checkIfRelationUUInfo(inparam);
	    	if("true".equals(ifRelaInfo)){
	    		rtnData.put("RELATION_UU_TYPE", "Y");
	    		checkResult="0";
	    	}else{
	    		rtnData.put("RELATION_UU_TYPE", "N");
	    	}
    	}
    	
    	/*
    	 *6、 0存折+332存折不能大于0  SN_FEE_TYPE    Y:大于0   N：不大于0
   	  * */
    	inparam.clear();
    	inparam.put("SERIAL_NUMBER", checkSN);
    	if(!"N".equals(paraCode5)){//N:不用校验:0存折+332存折不能大于0 add by zhangxing3 for 大派送第二季
	    	String ifFeeZero=checkIfFeeRemainZero(inparam);
	    	if("true".equals(ifFeeZero)){
	    		rtnData.put("SN_FEE_TYPE", "Y");
	    		checkResult="0";
	    	}else{
	    		rtnData.put("SN_FEE_TYPE", "N");
	    	}
    	}
		if("Y".equals(paraCode11)){
    		boolean haveDiscnt = checkHaveDiscnt(checkSN,productId);
	    	if(haveDiscnt){
	    		rtnData.put("HAVE_DISCNT", "Y");
	    		checkResult="0";
	    	}else{
	    		rtnData.put("HAVE_DISCNT", "N");
	    	}
    	}
    	String mainNumCheckTag=params.getString("MAIN_SERINUM_CHECK_TAG","");
    	if(mainNumCheckTag==null || "".equals(mainNumCheckTag)){
    		mainNumCheckTag="1";
    	}
    	//校验通过插临时表
    	if("1".equals(checkResult) && "1".equals(mainNumCheckTag)){
	    	IData insertData=new DataMap(); 
	    	insertData.put("TRADE_TYPE_CODE","240");
	    	insertData.put("USER_ID",userId);//主号USER_ID
	    	insertData.put("SERIAL_NUMBER",serialNumber);//主号serial_number
	    	insertData.put("CHECK_USER_ID",checkUserId);//弹出框校验号码USER_ID
	    	insertData.put("CHECK_SERIAL_NUMBER",checkSN);//弹出框校验号码SERIAL_NUMBER
	    	insertData.put("PRODUCT_ID",productId);//主号办理的活动
	    	insertData.put("PRODUCT_ID_B",prodId_b); 
	    	
	    	
	    	delNewTempInfo(insertData);//插入前先删除0的数据
	    	
	    	inserTemData(insertData);
    	}
    	
    	return rtnData;
    }
    /**
     * 号码是否存在活动(只要存在就不行，非一定要有效)
     * */
    public String checkIfHadActive(IData params) throws Exception{
    	String rtnFlag="false";
    	
    	SQLParser parser = new SQLParser(params); 
        parser.addSQL(" select t.* from Tf_f_User_sale_active t where t.user_id=:USER_ID  and t.product_id=:PRODUCT_ID "); 
        IDataset infos=  Dao.qryByParse(parser); 
        if(infos!=null && infos.size() >0){
        	rtnFlag="true";
        }
    	
    	return rtnFlag;
    }
    
    /**
     * 新号码是否存在2018新入网套餐月费打折活动、2018话音赠送促销活动活动
     * */
    public String checkIfActive(IData params) throws Exception{
        IDataset iDataset= Dao.qryByCode("TF_F_USER_SALE_ACTIVE", "SEL_HONGHAI_UID", params);
        if(iDataset.size()>0){
        	return "true";
        }else{
        	return "false";
        }
    }
    
	    /**       
	     * 新号码对应存折余额校验   
	   * */       
	  public Boolean checkBalanceDeposit(IData params,String paraCode17) throws Exception{ 
		  BigDecimal balance =new BigDecimal(0);
		  if(params.getString("SERIAL_NUMBER")!=null){
			  IDataset retData = CSAppCall.call("AM_CRM_QryAccountDepositByAcctId", params);
	          if(retData.size()>0&&StringUtils.isNotBlank(paraCode17)){    
	        	  for(int i=0;i<retData.size();i++){
	        		 if(paraCode17.contains(retData.getData(i).getString("DEPOSIT_CODE")))
	        		 {
	        			 balance=balance.add(new BigDecimal(retData.getData(i).getString("DEPOSIT_BALANCE")).divide(new BigDecimal(100)));
	        		 }
	        	  }
	          }
	      }
		  Boolean flag=true;
		  if(balance.compareTo(new BigDecimal(50))>=0)
	       {
			  flag=false;
	       }
		  
		  return flag;
	  }
	
	 /**       
       * 新号码对应老号码是否作为主卡与新号码办统一付费业务   
     * */       
    public String checkIfMainCard(IData params) throws Exception{       
        IDataset iDataset= Dao.qryByCode("TF_F_RELATION_UU", "SEL_USER_IDA", params);//获取老号码统付关系（主卡）
        if(iDataset.size()>0){    
        	String iDatasetA = iDataset.getData(0).getString("USER_ID_A");
        	String checkUserId = params.getString("CHECK_USER_ID");
        	params.put("USER_ID_B", checkUserId);
        	params.put("ROLE_CODE_B","2");
        	IDataset iDatasetAll= Dao.qryByCode("TF_F_RELATION_UU", "SEL_USER_IDA", params);//获取新号码统付关系（副卡）
        	 if(iDatasetAll.size()>0){ 
        		 String iDatasetAllA = iDatasetAll.getData(0).getString("USER_ID_A");
        		 if(StringUtils.isNotBlank(iDatasetAllA)){
        			 //判断老号码和新号码是否为统一付费关系
        			if(iDatasetAllA.equals(iDatasetA)){
        				 return "true";  
        			}else{
               		     return "false";   
               	    }
        		 }else{
            		 return "false";   
            	 }
        		 
        	 }else{
        		 return "false";   
        	 }
                
        }else{       
            return "false";       
        }       
    } 
    
    /**       
     * 新号码是否办理宽带业务
   * */       
  public String checkIfWidenet(IData params) throws Exception{  
      String tag="false";
      //宽带预约（宽带需求收集界面登记地址）
      SQLParser parser2 = new SQLParser(params); 
      parser2.addSQL(" select t.* from TF_F_WIDENET_BOOK t where t.RSRV_STR2=:SERIAL_NUMBER"); 
      IDataset infos2=  Dao.qryByParse(parser2); 
      if(infos2.size()>0){       
    	  tag = "true";       
      }
      //宽带预约（宽带开户界面登记地址）
      IData inparam=new DataMap();
      inparam.put("MOBILE_NO", params.getString("SERIAL_NUMBER"));
      if(params.getString("SERIAL_NUMBER")!=null){
    	  IDataset retData = CSAppCall.call("PB.AddrPreDeal.qryAddrPreDealList", inparam);
          if(retData.size()>0){       
        	  tag = "true";       
          }
      }
      params.put("SERIAL_NUMBER", "KD_"+params.get("SERIAL_NUMBER"));
      IDataset iDataset2= Dao.qryByCodeParser("TF_F_USER_WIDENET", "SEL_BY_SERIAL_NUMBER_WIDENET", params); 
      if(iDataset2.size()>0){       
    	  tag = "true";       
      } 
      IDataset iDataset= Dao.qryByCode("TF_B_TRADE", "SEL_BY_SN", params);   
      if(iDataset.size()>0){       
    	  tag = "true";       
      }
	    
	return tag;   
  } 
    
    /**       
     * 新号码必须办理亲亲网   
   * */       
	  public String checkIfFamily(IData params) throws Exception{       
	      IDataset iDataset= Dao.qryByCodeParser("TF_F_RELATION_UU", "SEL_USER_IDA", params);       
	      if(iDataset.size()>0){       
	          return "true";       
	      }else{       
	          return "false";       
	      }       
	  } 
    
    /**
     * 身份证是否一致
     * */
    public String checkIfUserPsptIdSame(IData params) throws Exception{
    	String rtnFlag="false";
    	String userId=params.getString("USER_ID","");
    	String checkUserId=params.getString("CHECK_USER_ID","");
    	String psptid=UcaDataFactory.getUcaByUserId(userId).getCustomer().getPsptId();//老用户
    	String checkpsptid=UcaDataFactory.getUcaByUserId(checkUserId).getCustomer().getPsptId();//新用户（弹框输入号码）
    	if("TRUE".equals(params.getString("BAT_TAG","")))//add by fufn 老客户感恩大派送线上售卡优化  批量导入新用户证件数据从TD_B_POSTCARD_INFO取
    	{
    		IData param=new DataMap();
        	param.put("SERIAL_NUMBER", params.getString("CHECK_SERIAL_NUMBER",""));
            IDataset iDataset= Dao.qryByCode("TD_B_POSTCARD_INFO", "SEL_BY_SERIAL_NUMBER", param);
            if(iDataset.size()>0){
            	checkpsptid=iDataset.getData(0).getString("PSPT_ID", "");
            }else{
            	return "false";
            }
    	}
    	if(psptid!=null && checkpsptid !=null  ){
    		if(psptid.equals(checkpsptid)){
    			rtnFlag="true"; 
    		}else{
    			if(psptid.length()==15 && checkpsptid.length()==18){
    				String checkpsptid15=checkpsptid.substring(0,6)+checkpsptid.substring(8,17);
    				if(psptid.equals(checkpsptid15)){
    					rtnFlag="true"; 
    				}
    			}
    		}
    	} 
    	return rtnFlag;
    }
    
    /**
     * 多终端共享业务数据
     * */
    public String checkIfShareRelaInfo(IData params) throws Exception{
    	String rtnFlag="false";
    	
    	SQLParser parser = new SQLParser(params); 
        parser.addSQL(" select t.*  from TF_F_USER_SHARE_RELA t where t.user_id_b=:USER_ID_B and sysdate < t.end_date "); 
        IDataset infos=  Dao.qryByParse(parser); 
        if(infos!=null && infos.size() >0){
        	rtnFlag="true";
        }
    	return rtnFlag;
    }
    
    /**
     * 统一付费业务数据
     * */
    public String checkIfRelationUUInfo(IData params) throws Exception{
    	String rtnFlag="false";
    	SQLParser parser = new SQLParser(params); 
        parser.addSQL(" select t.* from tf_f_relation_uu t where t.user_id_b=:USER_ID_B and t.relation_type_code in ('56','34','U8') and sysdate < t.end_date "); 
        IDataset infos=  Dao.qryByParse(parser); 
        if(infos!=null && infos.size() >0){
        	rtnFlag="true";
        }
    	return rtnFlag;
    }
    
    /**
     * 新号办理营销活动时，判断卡预存款是否大于0（避免代理商漏操作清退），如大于0则无法办理活动（活动规则新增）
     * 0存折  + 332存折  
     * */
    public String checkIfFeeRemainZero(IData params) throws Exception{
    	String rtnFlag="false";
    	String DEPOSIT_BALANCE_0="0";//0存折金额
    	String DEPOSIT_BALANCE_332="0";//332存折
    	//3、获取默认账户  （acct_id)		
    	IDataset accts=AcctInfoQry.qryAcctDefaultIdBySn(params.getString("SERIAL_NUMBER"));
    	String acctId=accts.getData(0).getString("ACCT_ID");
    	IData param = new DataMap();
    	param.put("ACCT_ID", acctId); 
    	/**调用账务查询接口*/
    	IDataset checkCash= AcctCall.queryAcctDeposit(param); 
    	 
    	for(int j=0;j<checkCash.size();j++){
			IData acctInfo=checkCash.getData(j);
    		String DEPOSIT_CODE=acctInfo.getString("DEPOSIT_CODE");//存折编码
    		
    		if("0".equals(DEPOSIT_CODE)){ 
    			DEPOSIT_BALANCE_0=""+(Integer.parseInt(DEPOSIT_BALANCE_0)+Integer.parseInt(acctInfo.getString("DEPOSIT_BALANCE")));//存折余额
    		}
    		if("332".equals(DEPOSIT_CODE)){ 
    			DEPOSIT_BALANCE_332=""+(Integer.parseInt(DEPOSIT_BALANCE_332)+Integer.parseInt(acctInfo.getString("DEPOSIT_BALANCE")));//存折余额
    		}
    	}
    	if((Integer.parseInt(DEPOSIT_BALANCE_0) + Integer.parseInt(DEPOSIT_BALANCE_332))>0){
    		rtnFlag="true";
    	}	
    	return rtnFlag;
    }
    
    
    /**
     * 号码必须是激活不超过7天的  OPEN_48_HOUR Y:7天内 N:超过7天
     * */
    public IDataset checkIfUserOpen48Hour(IData params) throws Exception{
    	 
    	SQLParser parser = new SQLParser(params); 
        parser.addSQL(" select t.*,t.rowid from tf_F_user t where t.SERIAL_NUMBER= :CHECK_SERIAL_NUMBER and t.open_date > sysdate -7 and t.remove_tag='0' ");
        IDataset infos=  Dao.qryByParse(parser); 
        
    	return infos;
    }
    

   public IDataset checkIfUserOpen48HourForBat(IData params) throws Exception{
   	 
    	SQLParser parser = new SQLParser(params); 
        parser.addSQL(" select t.*,t.rowid from tf_F_user t where t.SERIAL_NUMBER= :CHECK_SERIAL_NUMBER and t.acct_tag='2' and t.remove_tag='0' "); 
        IDataset infos=  Dao.qryByParse(parser); 
        
    	return infos;
    }
    
    
    /**
     * TRADE_TYPE_CODE     VARCHAR2(4)  Y                台账类型，如240=营销活动受理 
		USER_ID             NUMBER(16)   Y                主号USER_ID          
		SERIAL_NUMBER       VARCHAR2(40) Y                主号serial_number    
		CHECK_USER_ID       NUMBER(16)   Y                弹出框校验号码USER_ID 
		CHECK_SERIAL_NUMBER VARCHAR2(40) Y                弹出框校验号码SERIAL_NUMBER 
		PRODUCT_ID          VARCHAR2(10) Y                主号办理的活动       
		PACKAGE_ID          VARCHAR2(10) Y                主号办理的包         
		PRODUCT_ID_B        VARCHAR2(10) Y                弹出框校验号码需要绑定的活动 
		PACKAGE_ID_B        VARCHAR2(10) Y                弹出框校验号码需要绑定的活动 
		DEAL_TAG            VARCHAR2(3)  Y                0-校验通过，1-B号码成功办理活动 
		DEAL_TIME           DATE         Y                处理时间             
		RSRV_STR1           VARCHAR2(50) Y                                     
		RSRV_STR2           VARCHAR2(50) Y                                     
		RSRV_STR3           VARCHAR2(50) Y                                     
		RSRV_STR4           VARCHAR2(50) Y                                     
		RSRV_STR5           VARCHAR2(50) Y   
     * */
    public void inserTemData(IData params) throws Exception{
    	DBConnection conn = null;
    	try 
		{
    		conn = SessionManager.getInstance().getAsyncConnection("crm1");
			IData inparams=new DataMap();
	    	inparams.put("TRADE_TYPE_CODE","240");//台账类型，如240=营销活动受理
	    	inparams.put("USER_ID",params.getString("USER_ID",""));//主号USER_ID
	    	inparams.put("SERIAL_NUMBER",params.getString("SERIAL_NUMBER",""));//主号serial_number
	    	inparams.put("CHECK_USER_ID",params.getString("CHECK_USER_ID",""));//弹出框校验号码USER_ID
	    	inparams.put("CHECK_SERIAL_NUMBER",params.getString("CHECK_SERIAL_NUMBER",""));//弹出框校验号码SERIAL_NUMBER
	    	inparams.put("PRODUCT_ID",params.getString("PRODUCT_ID",""));//主号办理的活动
	    	inparams.put("PACKAGE_ID","");//主号办理的包
	    	inparams.put("PRODUCT_ID_B",params.getString("PRODUCT_ID_B",""));//弹出框校验号码需要绑定的活动
	    	inparams.put("PACKAGE_ID_B","");//这里还没选包，所以无法选择具体包保存。
	    	inparams.put("DEAL_TAG","0");//0-校验通过，1-B号码成功办理活动
	    	inparams.put("DEAL_TIME",SysDateMgr.getSysTime());//处理时间
	    	inparams.put("RSRV_STR1","");
	    	inparams.put("RSRV_STR2","");
	    	inparams.put("RSRV_STR3","");
	    	inparams.put("RSRV_STR4","");
	    	inparams.put("RSRV_STR5","");  
        	BaseDAO dao = DAOManager.createDAO(BaseDAO.class);
			dao.insert(conn, "TF_F_NEW_ACTIVE_TEMPLATE", inparams);
	    	conn.commit();
		}
    	catch (Exception e1) 
		{ 
			if(conn != null)
			{
				conn.rollback();
			}			
			CSAppException.appError("2001", e1.getMessage());
		} 
		finally 
		{
			if(conn != null)
			{
				conn.close();
			}
		} 
    }
    
    /**
     * 查询临时表已经保存的老用户信息
     * */
    public IDataset qryNewTempInfo(IData input) throws Exception
    {
    	SQLParser parser = new SQLParser(input); 
  	  
        parser.addSQL(" select T.* "); 
        parser.addSQL(" from TF_F_NEW_ACTIVE_TEMPLATE t ");
        parser.addSQL(" where t.trade_type_code=:TRADE_TYPE_CODE ");
        parser.addSQL(" AND T.SERIAL_NUMBER=:SERIAL_NUMBER "); 
        parser.addSQL(" AND T.USER_ID=:USER_ID "); 
        parser.addSQL(" AND T.DEAL_TAG='0' "); 
        parser.addSQL(" AND T.DEAL_TIME >= SYSDATE-2 "); 
        parser.addSQL(" AND T.PRODUCT_ID=:PRODUCT_ID "); 

        return Dao.qryByParse(parser); 
    	
    }
    
    /**
     * 通过新号码查询临时表已经保存的老用户信息
     * */
    public IDataset qryNewTempInfo2(IData input) throws Exception
    {
    	SQLParser parser = new SQLParser(input); 
  	  
        parser.addSQL(" select T.* "); 
        parser.addSQL(" from TF_F_NEW_ACTIVE_TEMPLATE t ");
        parser.addSQL(" where t.trade_type_code=:TRADE_TYPE_CODE ");
        parser.addSQL(" AND T.CHECK_SERIAL_NUMBER=:SERIAL_NUMBER "); 
        parser.addSQL(" AND T.CHECK_USER_ID=:USER_ID "); 
        parser.addSQL(" AND T.DEAL_TAG='0' "); 
        parser.addSQL(" AND T.DEAL_TIME >= SYSDATE-2 "); 
        parser.addSQL(" AND T.PRODUCT_ID=:PRODUCT_ID "); 

        return Dao.qryByParse(parser); 
    	
    }
    
    /**
     * 老用户号码所需要的营销活动打上已经校验的标记
     * */
    public void updNewTempInfo(IData inparams) throws Exception
    {

        Dao.executeUpdateByCodeCode("TF_F_USER_SALE_ACTIVE", "UPD_TEM_NEW_ACTIVE_DEAL", inparams);
    }
    
    /**
     * 插入前要先删除临时表数据
     * */
    public void delNewTempInfo(IData inparams) throws Exception
    {

        Dao.executeUpdateByCodeCode("TF_F_USER_SALE_ACTIVE", "DEL_TEM_NEW_ACTIVE_DEAL", inparams);
    }
    
    /**
     * 查询新用户的服务状态信息。
     * */
    public IDataset qryNewUserSvcstateInfo(IData params) throws Exception{
    	 
    	SQLParser parser = new SQLParser(params); 
        parser.addSQL(" select t.* from Tf_f_User_Svcstate t where t.user_id=:CHECK_USER_ID and t.service_id='0' and sysdate < t.end_date "); 
        IDataset infos=  Dao.qryByParse(parser); 
        
    	return infos;
    }
    
    
    /** 
   	* 20170906 chenxy3 
   	* 捞取“集团关键人”号码池中对应的号码是否办理了这个活动，并已激活使用
   	* 
   	* tf_sm_troop_member 
   	* 
   	* PRODUCT_ID=66000207，package_id=60091124  --2017老客户感恩大派送活动-集团客户（赠送新卡300元话费）
   	* */
    public void checkGroupUserIfUseActive(IData param) throws Exception{
    	 IData data=new DataMap();
    	 String serialNum=param.getString("SERIAL_NUMBER","");
    	 String userId=param.getString("USER_ID",""); 
    	 data.put("SERIAL_NUMBER", serialNum);
    	 data.put("USER_ID", userId);
    	 IDataset comms=CommparaInfoQry.getCommparaInfoBy5("CSM", "9958", "66000207", "1", "0898", null);
     	 for(int n=0; n< comms.size(); n++){
	    	 //号码B办理营销活动
	 		 String activeTradeId="";
	 		 IData saleactiveData = new DataMap();
	         saleactiveData.put("SERIAL_NUMBER",serialNum);
	         saleactiveData.put("PRODUCT_ID", comms.getData(n).getString("PARA_CODE3"));
	         saleactiveData.put("PACKAGE_ID", comms.getData(n).getString("PARA_CODE4"));
	         try{
		         IDataset saleActive=CSAppCall.call("SS.SaleActiveRegSVC.tradeReg4Intf", saleactiveData);
		         if(saleActive!=null && saleActive.size()>0){
		         	activeTradeId=saleActive.getData(0).getString("TRADE_ID","");
		         	data.put("TRADE_ID", activeTradeId);
		         	data.put("DEAL_TAG", "1");
		         	data.put("DEAL_RESULT", "succ");
		         } 
		         
	         }catch(Exception e){
		    	String error =  Utility.parseExceptionMessage(e);
		    	String[] errorArray = error.split(BaseException.INFO_SPLITE_CHAR);
				if(errorArray.length >= 2)
				{
					String strException = errorArray[0];
					String strExceptionMessage = errorArray[1];
					data.put("DEAL_TAG", "9");
					data.put("DEAL_RESULT", "号码【"+serialNum+"】营销活动办理失败:"+strExceptionMessage);
				}
				else
				{
					data.put("DEAL_TAG", "9");
					data.put("DEAL_RESULT", "号码【"+serialNum+"】*营销活动办理失败:"+error);
				}  
	         }
	         inserBatTemData(data);
     	 }
    }
    //校验新卡是否包含配置中的优惠  add by wuhao5
    public boolean checkHaveDiscnt(String checkSn,String productId) throws Exception{
    	boolean rtnFlag = false;
    	UcaData uca = UcaDataFactory.getNormalUca(checkSn);
    	List<DiscntTradeData> userDiscnt = uca.getUserDiscnts();
        IDataset commpara1849 = CommparaInfoQry.getCommparaByCode1("CSM","1849",productId,"0898");
        if(IDataUtil.isNotEmpty(commpara1849) && commpara1849.size() > 0){                	
    		IData commData = commpara1849.getData(0);
    		String[] disCodes = commData.getString("PARA_CODE3").split("\\|");
    		for(int j = 0;j < disCodes.length ;j ++){
    			String disCode = disCodes[j];
    	    	for(DiscntTradeData disData : userDiscnt){    	    		
    	    		if(disData.getDiscntCode().equals(disCode)){
    	    			rtnFlag = true;
    	    		}
    	    	}
    		}
        }    	
    	return rtnFlag;
    }
    
    public void inserBatTemData(IData params) throws Exception{
    	IData inparams=new DataMap();
    	inparams.put("USER_ID",params.getString("USER_ID",""));//USER_ID
    	inparams.put("SERIAL_NUMBER",params.getString("SERIAL_NUMBER",""));
    	inparams.put("DEAL_TAG",params.getString("DEAL_TAG",""));//1-成功办理活动  其他值失败
    	inparams.put("DEAL_TIME",SysDateMgr.getSysTime());//处理时间
    	inparams.put("DEAL_RESULT",params.getString("DEAL_RESULT",""));//处理结果
    	inparams.put("RSRV_STR1",params.getString("TRADE_ID",""));
    	inparams.put("RSRV_STR2","");
    	inparams.put("RSRV_STR3","");
    	inparams.put("RSRV_STR4","");
    	inparams.put("RSRV_STR5","");
		Dao.insert("TF_F_BAT_ACTIVE_TEMPLATE", inparams);
    }
}
