package com.asiainfo.veris.crm.order.soa.person.busi.family.mfc;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import org.apache.log4j.Logger;

import com.ailk.bizservice.dao.CrmDAO;
import com.ailk.bizservice.dao.Dao;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.dao.DAOManager;
import com.ailk.database.dbconn.DBConnection;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.RouteInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.MsisdnInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.SmsSend;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;

public class FamilyAllNetBusiManageBean extends CSBizBean{
	
	//检验号码是否是移动号码
	private static final String CHINA_MOBILE_PATTERN = "(^1(3[4-9]|4[7]|5[0-27-9]|7[8]|8[2-478])\\d{8}$)|(^1705\\d{7}$)";
	protected static final Logger log = Logger.getLogger(FamilyAllNetBusiManageBean.class);
	public IDataset loadInfo(IData input) throws Exception{
		String serial_number = input.getString("SERIAL_NUMBER");
		String relation_type_code = "MF";
		IDataset uuinfos = MfcCommonUtil.getRelationUusByUserSnRole( serial_number, relation_type_code,null,null);
		if(IDataUtil.isNotEmpty(uuinfos)){
			for(int i=0;i<uuinfos.size();i++){
				//设置默认值
				String productOfferingId= uuinfos.getData(i).getString("RSRV_STR2");
				String poidCode = productOfferingId.substring(16);
				String poidLable ="群"+poidCode;
				if("1".equals(uuinfos.getData(i).getString("ROLE_CODE_B"))){
					uuinfos.getData(i).put("MAIN_NUMBER",uuinfos.getData(i).getString("SERIAL_NUMBER_B"));
					IDataset otherInfo = MfcCommonUtil.getPoidCodeByOfferingID(productOfferingId,serial_number,"1");
					if(IDataUtil.isNotEmpty(otherInfo)){
						uuinfos.getData(i).put("POID_CODE",otherInfo.getData(0).getString("POID_CODE"));
						uuinfos.getData(i).put("POID_LABLE",otherInfo.getData(0).getString("POID_LABLE"));
						uuinfos.getData(i).put("MEM_LABLE",otherInfo.getData(0).getString("MEM_LABLE",uuinfos.getData(i).getString("SERIAL_NUMBER_B").substring(7)));					
					}else{
						uuinfos.getData(i).put("POID_CODE",poidCode);
						uuinfos.getData(i).put("POID_LABLE",poidLable);
						uuinfos.getData(i).put("MEM_LABLE",uuinfos.getData(i).getString("SERIAL_NUMBER_B").substring(7));
					}
				}else{
					IDataset uuA = MfcCommonUtil.getSEL_USER_ROLEA(uuinfos.getData(i).getString("USER_ID_A"),"1","MF",null);
					if(IDataUtil.isNotEmpty(uuA)){
						uuinfos.getData(i).put("MAIN_NUMBER", uuA.getData(0).getString("SERIAL_NUMBER_B"));
						IDataset otherInfo = MfcCommonUtil.getPoidCodeByOfferingID(productOfferingId,uuA.getData(0).getString("SERIAL_NUMBER_B"),"1");
						if(IDataUtil.isNotEmpty(otherInfo)){
							uuinfos.getData(i).put("POID_CODE",otherInfo.getData(0).getString("POID_CODE"));
							uuinfos.getData(i).put("POID_LABLE",otherInfo.getData(0).getString("POID_LABLE"));
							uuinfos.getData(i).put("MEM_LABLE",otherInfo.getData(0).getString("MEM_LABLE",uuA.getData(0).getString("SERIAL_NUMBER_B").substring(7)));
						}else{
							uuinfos.getData(i).put("POID_CODE",poidCode);
							uuinfos.getData(i).put("POID_LABLE",poidLable);
							uuinfos.getData(i).put("MEM_LABLE",uuA.getData(0).getString("SERIAL_NUMBER_B").substring(7));
						}
					}
				}
				String userIdA = uuinfos.getData(i).getString("USER_ID_A");
				int count = MfcCommonUtil.getCountByUserIdA(userIdA,"MF");
				uuinfos.getData(i).put("VALIATE_MEM_COUNT",count);
			}
			if (log.isDebugEnabled())
        	{
        		log.debug("=======特殊处理uuinfos====================="+uuinfos);
        	}


			return  uuinfos;
		
		}else{	
			return new DatasetList();
			
		}
	}
	/**
	 * 查省内不同地方的成员
	 * @param userIdA
	 * @param relationTypeCode
	 * @param page
	 * @return
	 * @throws Exception
	 */
	 public static IDataset getRelaUUInfoByUserIda(String userIdA, String relationTypeCode, Pagination page) throws Exception
	    {

	        IData inparam = new DataMap();
	        inparam.put("USER_ID_A", userIdA);
	        inparam.put("RELATION_TYPE_CODE", relationTypeCode);

	        return Dao.qryByCodeParserAllCrm("TF_F_RELATION_UU", "SEL_USER_A", inparam, page,true);
	    }
	/**
	 * 遍历全库xinxi
	 * @param serialnumberB
	 * @param relationTypeCode
	 * @return
	 * @throws Exception
	 */
	  public static IDataset queryRelaUUBySnb(String serialNumberB, String relationTypeCode) throws Exception
	    {
	        IData param = new DataMap();
	        param.put("SERIAL_NUMBER_B", serialNumberB);
	        param.put("RELATION_TYPE_CODE", relationTypeCode);
	        return Dao.qryByCodeAllCrm("TF_F_RELATION_UU", "SEL_SNA_BY_SNB_RTYPE", param,true);
	    }
	  
	  
//	public static IDataset getSEL_USER_ROLEA( String serialnumberB, String relationTypeCode) throws Exception {
//		IData inparam = new DataMap();
//		inparam.put("ROLE_CODE_B", serialnumberB);
//		inparam.put("RELATION_TYPE_CODE", relationTypeCode);
//		return Dao.qryByCodeAllCrm("TF_F_RELATION_UU", "SEL_USER_ROLEA", inparam,true);
//	}
	public IData checkAddMeb(IData input) throws Exception{
		
		String mainNum = input.getString("SERIAL_NUMBER");// 主号码
        String mebNum = input.getString("SERIAL_NUMBER_B");// 副号码
        String productCode = input.getString("PRODUCT_CODE");// 产品编码

        IData data = new DataMap();// 返回结果
        IData mainUser = UcaInfoQry.qryUserInfoBySn(mainNum);
        if (IDataUtil.isEmpty(mainUser))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_117, mainNum);
        }
        String product= input.getString("PRODUCT_CODE");
  	  	IData sunFamily =MfcCommonUtil.checkCount(mebNum,product);
  	  if(!"00".equals(sunFamily.getString("RSP_CODE"))){
      	CSAppException.apperr(CrmUserException.CRM_USER_783, "该号码在当前类型的群组中已达上限，无法继续添加！");


  	  }
  	  
	  	IData qryData = new DataMap();
	    qryData.put("MEM_TYPE", "1");
	    qryData.put("CUSTOMER_PHONE", "");
	    qryData.put("BIZ_VERSION", "1.0.0");
	    qryData.put("PRODUCT_OFFERING_ID", input.getString("PRODUCT_OFFERING_ID"));
	    qryData.put("MEM_AREA_CODE", "");
	    qryData.put("MEM_NUMBER", "");
	    qryData.put("PRODUCT_CODE", "");
	    qryData.put("BUSINESS_TYPE", "1");
	    IDataset rtDataset =bossRealTimeInfo(qryData);
	    if (IDataUtil.isNotEmpty(rtDataset)){
	    	String rsp_code = rtDataset.getData(0).getString("BIZ_ORDER_RESULT");
	    	String rsp_desc = rtDataset.getData(0).getString("RSP_DESC");
	    	if(StringUtils.isNotBlank(rsp_code)){
	    		if(!"00".equals(rsp_code)){
	            	CSAppException.apperr(CrmUserException.CRM_USER_783, "不存在该群组【"+input.getString("PRODUCT_OFFERING_ID")+"】");
	    		}else{
	    			IDataset memberList=rtDataset.getData(0).getDataset("RSP_RESULT").getData(0).getDataset("MEMBER_LIST");//正式成员
	    			IDataset memberTranList=rtDataset.getData(0).getDataset("RSP_RESULT").getData(0).getDataset("MEM_TRAN_LIST");//在途成员
	    			for(int k=0;k<memberList.size();k++){
	    				String memNumber = memberList.getData(k).getString("MEM_NUMBER");
	    				if(mebNum.equals(memNumber)){
	    	            	CSAppException.apperr(CrmUserException.CRM_USER_783, "该成员【"+mebNum+"】已存在");
	    				}
	    			}
	    			if(IDataUtil.isNotEmpty(memberTranList)){
	    				for(int z=0;z<memberTranList.size();z++){
	    					String tranMemNumber = memberTranList.getData(z).getString("MEM_NUMBER");
	    					if(mebNum.equals(tranMemNumber)&&"MFC000002".equals(productCode)){
		    	            	CSAppException.apperr(CrmUserException.CRM_USER_783, "主号添加需成员号二次确认，需24小时内回复短信“是”同意添加后方可添加成功");
	    					}else if(mebNum.equals(tranMemNumber)){
		    	            	CSAppException.apperr(CrmUserException.CRM_USER_783, "该号码仍有在途未完工工单，请稍后再试");
	    					}
	    				}
	    			}
	    		}
	    	}
	    }
  	  
        //判断成员个数，和副号码有没有在成员列表里
		IDataset uuinfos = MfcCommonUtil.getRelationUusByUserSnRole( mainNum, "MF","1",input);
		if(IDataUtil.isEmpty(uuinfos)){
        	CSAppException.apperr(CrmUserException.CRM_USER_783, "不存在该群组【"+input.getString("PRODUCT_OFFERING_ID")+"】");
		}else{
			IData uuinfo = uuinfos.getData(0);
			String user_id_a = uuinfo.getString("USER_ID_A");
			String sn_a = uuinfo.getString("SERIAL_NUMBER_A");
			IDataset allfamilys = MfcCommonUtil.getSEL_USER_ROLEA( user_id_a,null, "MF",null);
			if(allfamilys.size() -1 >17){
	        	CSAppException.apperr(CrmUserException.CRM_USER_783, "该家庭网【"+input.getString("PRODUCT_OFFERING_ID")+"】群组成员已经到达上限，无法继续添加");
			}
			IDataset relationUULists = MfcCommonUtil.getSEL_USER_ROLEA(user_id_a,"2","MF",null);
			if(IDataUtil.isNotEmpty(relationUULists)){
				for(int i = 0, size = relationUULists.size(); i < size; i++){
					String snb = relationUULists.getData(i).getString("SERIAL_NUMBER_B");
					if(mebNum.equals(snb)){
			        	CSAppException.apperr(CrmUserException.CRM_USER_783, "该号码已经在群组【"+input.getString("PRODUCT_OFFERING_ID")+"】里，无法重复添加");

					}
				}
			}

			
		}

        /**
         * 检验号码是否是移动号码
         */
		IData isMobile = MsisdnInfoQry.getMsisonBySerialnumberAsp(mebNum,"1");
		if(IDataUtil.isEmpty(isMobile)){
			//集团全网版本要求支持网间号码办理全国亲情网
			IData mebNumInfo = UcaInfoQry.qryUserInfoBySn(mebNum,RouteInfoQry.getEparchyCodeBySn(mebNum));//加上remove_tag 的条件
			if(IDataUtil.isNotEmpty(mebNumInfo))
			{
				String userid = mebNumInfo.getString("USER_ID");
	        	IData npNumber = MfcCommonUtil.checkNpNumber(userid);
	        	if(!"99".equals(npNumber.getString("RSP_CODE"))){
	            	CSAppException.apperr(CrmUserException.CRM_USER_783, "该号码不是移动号码，不可作为副卡，添加到家庭网！");
	        	}
			}else
			{
	        	CSAppException.apperr(CrmUserException.CRM_USER_783, "该号码不是移动号码，不可作为副卡，添加到家庭网！");

			}
		}
       /* boolean isMobile = RouteInfoQry.isMfcChinaMobile(mebNum);
        if(!isMobile){
        	CSAppException.apperr(CrmUserException.CRM_USER_783, "该号码不是移动号码，不可作为副卡，添加到家庭网！");
        }*/
//        /**
//         * 检验号码是否是移动号码
//         */
//        if(!mebNum.matches(CHINA_MOBILE_PATTERN)){
//        	CSAppException.apperr(CrmUserException.CRM_USER_783, "该号码不是移动号码，不可作为副卡，添加到家庭网！");
//        }
        
        IData mebNumInfo = UcaInfoQry.qryUserInfoBySn(mebNum,RouteInfoQry.getEparchyCodeBySn(mebNum));//加上remove_tag 的条件
        IDataset mebNumInfoS = UserInfoQry.getAllDestroyUserInfoBySn(mebNum);//加上remove_tag 的条件

        if(IDataUtil.isEmpty(mebNumInfo) && IDataUtil.isEmpty(isMobile) ){
        	CSAppException.apperr(CrmUserException.CRM_USER_783, "该用户状态异常，无法办理业务！！！");
        }
        if(IDataUtil.isNotEmpty(mebNumInfo)){
        	/*String userid = mebNumInfo.getString("USER_ID");
        	IData npNumber = MfcCommonUtil.checkNpNumber(userid);
        	if(!"00".equals(npNumber.getString("RSP_CODE"))){
        		CSAppException.apperr(CrmUserException.CRM_USER_783, "该号码是携号转网用户，无法办理业务");
        	}*/
        	UcaData ucaData =UcaDataFactory.getNormalUca(mebNum);
        	if(StringUtils.isNotBlank(ucaData.getBrandCode())&&StringUtils.equals("PWLW", ucaData.getBrandCode())){
        		CSAppException.apperr(CrmUserException.CRM_USER_783, "该号码为物联网用户，无法办理业务");
        	}
        }
	  IData destroyParam = new DataMap();
	  destroyParam.put("ACTION", "01");
	  destroyParam.put("CUSTOMER_PHONE", mainNum);
	  IDataset destroyList= Dao.qryByCodeParser("TF_B_FAMILY_LOG", "SEL_MFC_BYMEBNO", destroyParam,Route.CONN_CRM_CEN);
	  if(IDataUtil.isNotEmpty(destroyList)){
	      	CSAppException.apperr(CrmUserException.CRM_USER_783, "该主号有在途注销未完工工单，无法添加成员");
	  }
	  IData onParam = new DataMap();
	  onParam.put("MEM_NUMBER", mebNum);
	  onParam.put("ACTION", "50");
	  onParam.put("MEM_TYPE", "1");
	  IDataset resultTranList= Dao.qryByCodeParser("TF_B_FAMILY_LOG", "SEL_MFC_BYMEBNO", onParam,Route.CONN_CRM_CEN); //查询数据库
	  if(IDataUtil.isNotEmpty(resultTranList)){
	      	CSAppException.apperr(CrmUserException.CRM_USER_783, "该号码仍有在途未完工工单，请稍后再试");
	  }
        return data;
	}
	
	
	
	/**
	 * 提供给短厅的副号码校验接口
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IData checkAddMebForMessage(IData input) throws Exception{
		
		IData data = new DataMap();// 返回结果
		data.put("RSP_CODE", "00");
    	data.put("RSP_DESC", "成功");
		String mainNum = input.getString("SERIAL_NUMBER");// 主号码
        String mebNum = input.getString("SERIAL_NUMBER_B");// 副号码
        String productCode = input.getString("PRODUCT_CODE");//产品编码
        boolean isphonenumber =isPhoneNumber(mebNum);
        if(!isphonenumber){
        	data.put("RSP_CODE", "97");
        	data.put("RSP_DESC", "号码格式不对");	
        	return data;
        }
       /**
        * 校验主号码状态
        */
       /* IData mainUser = UcaInfoQry.qryUserInfoBySn(mainNum);
        if (IDataUtil.isEmpty(mainUser))
        {
        	data.put("RSP_CODE", "98");
        	data.put("RSP_DESC", "副号对应主号码资料不存在");
        	return data;
           // CSAppException.apperr(CrmUserException.CRM_USER_117, mainNum);
        }*/
        IData qryData = new DataMap();
	    qryData.put("MEM_TYPE", "1");
	    qryData.put("CUSTOMER_PHONE", "");
	    qryData.put("BIZ_VERSION", "1.0.0");
	    qryData.put("PRODUCT_OFFERING_ID", input.getString("PRODUCT_OFFERING_ID"));
	    qryData.put("MEM_AREA_CODE", "");
	    qryData.put("MEM_NUMBER", "");
	    qryData.put("PRODUCT_CODE", "");
	    qryData.put("BUSINESS_TYPE", "1");
	    IDataset rtDataset =bossRealTimeInfo(qryData);
	    if (IDataUtil.isNotEmpty(rtDataset)){
	    	String rsp_code = rtDataset.getData(0).getString("BIZ_ORDER_RESULT");
	    	String rsp_desc = rtDataset.getData(0).getString("RSP_DESC");
	    	if(StringUtils.isNotBlank(rsp_code)){
	    		if(!"00".equals(rsp_code)){
	    			data.put("RSP_CODE", "99");
					data.put("RSP_DESC", "不存在该群组【"+input.getString("PRODUCT_OFFERING_ID")+"】");
					return data;
	    		}else{
	    			IDataset memberList=rtDataset.getData(0).getDataset("RSP_RESULT").getData(0).getDataset("MEMBER_LIST");//正式成员
	    			IDataset memberTranList=rtDataset.getData(0).getDataset("RSP_RESULT").getData(0).getDataset("MEM_TRAN_LIST");//在途成员
	    			for(int k=0;k<memberList.size();k++){
	    				String memNumber = memberList.getData(k).getString("MEM_NUMBER");
	    				if(mebNum.equals(memNumber)){
	    					data.put("RSP_CODE", "99");
	    					data.put("RSP_DESC", "该成员【"+mebNum+"】已存在");
	    					return data;
	    				}
	    			}
	    			if(IDataUtil.isNotEmpty(memberTranList)){
	    				if(StringUtils.isBlank(productCode)){
	    					productCode=rtDataset.getData(0).getDataset("RSP_RESULT").getData(0).getString("PRODUCT_CODE");
	    				}
	    				for(int z=0;z<memberTranList.size();z++){
	    					String tranMemNumber = memberTranList.getData(z).getString("MEM_NUMBER");
	    					if(mebNum.equals(tranMemNumber)&&"MFC000002".equals(productCode)){
	    						data.put("RSP_CODE", "99");
	    						data.put("RSP_DESC", "主号添加需成员号二次确认，需24小时内回复短信“是”同意添加后方可添加成功");
	    						return data;
	    					}else if(mebNum.equals(tranMemNumber)){
	    						data.put("RSP_CODE", "99");
	    						data.put("RSP_DESC", "该号码仍有在途未完工工单，请稍后再试");
	    						return data;
	    					}
	    				}
	    			}
	    		}
	    	}
	    }
        
        
	    /**
	     * 判断家庭网的成员个数
	     */
		IDataset uua =MfcCommonUtil.getRelationUusByUserSnRole(mainNum,"MF","1",input);
		if(IDataUtil.isNotEmpty(uua)){
			String usera = uua.getData(0).getString("USER_ID_A");
			if(StringUtils.isNotBlank(usera)){
				IDataset uuainfo = MfcCommonUtil.getSEL_USER_ROLEA(usera, "2","MF",null);
				int size =uuainfo.size()+1;
				if(size>18){
					data.put("RSP_CODE", "96");
					data.put("RSP_DESC", "副号码已经达上限");
					return data;
				}
				for(int i = 0; i<uuainfo.size(); i++){
					String snb = uuainfo.getData(i).getString("SERIAL_NUMBER_B");
					if(mebNum.equals(snb)){
			        	data.put("RSP_CODE", "99");
			        	data.put("RSP_DESC", "该号码已经在群组【"+input.getString("PRODUCT_OFFERING_ID")+"】里，无法重复添加");
			        	return data;
					}
				}
			}
		}/*else{		   
			data.put("RSP_CODE", "99");
        	data.put("RSP_DESC", "不存在该群组【"+input.getString("PRODUCT_OFFERING_ID")+"】");
        	return data;
		}*/
        /**
         * 检验号码是否是移动号码
         */
		IData isMobile = MsisdnInfoQry.getMsisonBySerialnumberAsp(mebNum,"1");
		if(IDataUtil.isEmpty(isMobile)){
        	data.put("RSP_CODE", "99");
        	data.put("RSP_DESC", "该号码不是移动号码，不可作为副卡，添加到家庭网！");
        	return data;
		}
       /* boolean isMobile = RouteInfoQry.isMfcChinaMobile(mebNum);
        if(!isMobile){
        	data.put("RSP_CODE", "99");
        	data.put("RSP_DESC", "该号码不是移动号码，不可作为副卡，添加到家庭网！");
        	return data;
        }*/
        
        IData mebNumInfo = UcaInfoQry.qryUserInfoBySn(mebNum,RouteInfoQry.getEparchyCodeBySn(mebNum));//加上remove_tag 的条件
        IDataset mebNumInfoS = UserInfoQry.getAllDestroyUserInfoBySn(mebNum);//加上remove_tag 的条件
        if(IDataUtil.isEmpty(mebNumInfo) && IDataUtil.isEmpty(isMobile) ){
        	data.put("RSP_CODE", "99");
        	data.put("RSP_DESC", "该用户状态异常，无法办理业务！！！");
        	return data;
        }
        if(IDataUtil.isNotEmpty(mebNumInfo)){
        	
        	/*String userid = mebNumInfo.getString("USER_ID");
        	IData npNumber = MfcCommonUtil.checkNpNumber(userid);
        	if(!"00".equals(npNumber.getString("RSP_CODE"))){
        		data.put("RSP_CODE", "99");
        		data.put("RSP_DESC", "该号码是携号转网用户，无法办理业务");
        		return data;
        	}*/
        	UcaData ucaData =UcaDataFactory.getNormalUca(mebNum);
        	if(StringUtils.isNotBlank(ucaData.getBrandCode())&&StringUtils.equals("PWLW", ucaData.getBrandCode())){
        		data.put("RSP_CODE", "99");
        		data.put("RSP_DESC", "该号码为物联网用户，无法办理业务");
        		return data;
        	}
        }
	  IData destroyParam = new DataMap();
	  destroyParam.put("ACTION", "01");
	  destroyParam.put("CUSTOMER_PHONE", mainNum);
	  IDataset destroyList= Dao.qryByCodeParser("TF_B_FAMILY_LOG", "SEL_MFC_BYMEBNO", destroyParam,Route.CONN_CRM_CEN);
	  if(IDataUtil.isNotEmpty(destroyList)){
			data.put("RSP_CODE", "99");
        	data.put("RSP_DESC", "该主号有在途注销未完工工单，无法添加成员");
        	return data;
	  }
	  IData onParam = new DataMap();
	  onParam.put("MEM_NUMBER", mebNum);
	  onParam.put("ACTION", "50");
	  onParam.put("MEM_TYPE", "1");
	  IDataset resultTranList= Dao.qryByCodeParser("TF_B_FAMILY_LOG", "SEL_MFC_BYMEBNO", onParam,Route.CONN_CRM_CEN); //查询数据库
	  if(IDataUtil.isNotEmpty(resultTranList)){
	      	data.put("RSP_CODE", "99");
        	data.put("RSP_DESC", "该号码仍有在途未完工工单，请稍后再试");
        	return data;
	  }
        return data;
	}
	public static boolean isPhoneNumber(String phoneNumber){
        //手机号码长度
        int phoneLength=phoneNumber.length();
        //第一位是不是0
        String phoneOne=phoneNumber.substring(0,1);
        //是不是 +86形式
        int is86=phoneNumber.indexOf("+86");
        //是纯数字  并且长度等于11   并且第一位不是0   并且 不包含+86
        return  isNumeric(phoneNumber)&&phoneLength==11&&!phoneOne.equals("0")&&is86==-1;
    }
	  public static boolean isNumeric(String str){
	        Pattern pattern = Pattern.compile("[0-9]*");
	        Matcher isNum = pattern.matcher(str);
	        if( !isNum.matches() ){
	            return false;
	        }
	        return true;
	    }
	
/**
 * 上发接口
 * @param input
 * @return
 * @throws Exception
 */
	public IData putMeb(IData input) throws Exception{
		IData result =new DataMap();
		String OrderSourceID = input.getString("ORDER_SOURCE");
		String oprtype = input.getString("ACTION");
		IData onParam = new DataMap();
		if(input.getString("CUSTOMER_PHONE").length()!= 11){
			result.put("RSP_CODE","4000" );
			result.put("RSP_DESC","主号码位数不对");
			result.put("OPR_TIME",SysDateMgr.getSysTime());
			result.put("CUSTOMER_PHONE",input.getString("CUSTOMER_PHONE",""));
			result.put("PRODUCT_OFFERING_ID",input.getString("PRODUCT_OFFERING_ID"));
			return result;	
		}
		if("01".equals(oprtype)){
			//预防重复注销问题
			/*IData indata =new DataMap();
			indata.put("PRODUCT_OFFERING_ID", input.getString("PRODUCT_OFFERING_ID"));
			IDataset destoryinfo = MfcCommonUtil.getRelationUusByUserSnRole(input.getString("CUSTOMER_PHONE"),"MF","1",indata);
			if(IDataUtil.isEmpty(destoryinfo)){
				result.put("RSP_CODE","3999" );
	        	result.put("RSP_DESC","该号码注销的家庭网信息已经不存在，无法办理业务！");
	        	result.put("OPR_TIME",SysDateMgr.getSysTime());
	        	result.put("CUSTOMER_PHONE",input.getString("CUSTOMER_PHONE",""));
	        	result.put("PRODUCT_OFFERING_ID",input.getString("PRODUCT_OFFERING_ID"));
	        	return result;
			}*/		
			  onParam.put("CUSTOMER_PHONE", input.getString("CUSTOMER_PHONE"));
			  onParam.put("ACTION", "01");
			  IDataset resultTranList= Dao.qryByCodeParser("TF_B_FAMILY_LOG", "SEL_MFC_BYMEBNO", onParam,Route.CONN_CRM_CEN); //查询数据库
			  IDataset tradeBySN = TradeInfoQry.getMainTradeBySN(input.getString("CUSTOMER_PHONE"),"2583");
			  if(IDataUtil.isNotEmpty(resultTranList)||IDataUtil.isNotEmpty(tradeBySN)){
			      	CSAppException.apperr(CrmUserException.CRM_USER_783, "该号码仍有在途未完工工单，请稍后再试！");
			  }
			  	 IData qryData = new DataMap();
			     qryData.put("MEM_TYPE", "1");
			     qryData.put("CUSTOMER_PHONE", "");
			     qryData.put("BIZ_VERSION", "1.0.0");
			     qryData.put("PRODUCT_OFFERING_ID", input.getString("PRODUCT_OFFERING_ID"));
			     qryData.put("MEM_AREA_CODE", "");
			     qryData.put("MEM_NUMBER", "");
			     qryData.put("PRODUCT_CODE", "");
			     qryData.put("BUSINESS_TYPE", "1");
			     IDataset rtDataset =bossRealTimeInfo(qryData);
			     if (IDataUtil.isNotEmpty(rtDataset)){
			     	String rsp_code = rtDataset.getData(0).getString("BIZ_ORDER_RESULT");
			     	String rsp_desc = rtDataset.getData(0).getString("RSP_DESC");
			     	if(StringUtils.isNotBlank(rsp_code)){
			     		if(!"00".equals(rsp_code)){
			            	CSAppException.apperr(CrmUserException.CRM_USER_783, "不存在该群组【"+input.getString("PRODUCT_OFFERING_ID")+"】");
			     		}
			     	}
			     }
		}
		if("00".equals(oprtype)){
			onParam.put("CUSTOMER_PHONE", input.getString("CUSTOMER_PHONE"));
			onParam.put("ACTION", "00");
			IDataset resultTranList= Dao.qryByCodeParser("TF_B_FAMILY_LOG", "SEL_MFC_BYMEBNO", onParam,Route.CONN_CRM_CEN); //查询数据库
			if(IDataUtil.isNotEmpty(resultTranList)){
				CSAppException.apperr(CrmUserException.CRM_USER_783, "该号码仍有在途未完工工单，请稍后再试！");
			}
			//这里需要增加 POID_CODE 唯一性的判断
			//通过群组编码，主号码查poidcode 与入参判断，一样就不可以，不一样才可以，在这里还要下沉js里 poidcode 两位的检验。
			String poidCode =input.getString("POID_CODE");
			if(StringUtils.isNotBlank(poidCode)){
				if(poidCode.length()!=2){
					result.put("RSP_CODE","5000" );
					result.put("RSP_DESC","两位群组编码格式不对");
					result.put("OPR_TIME",SysDateMgr.getSysTime());
					result.put("CUSTOMER_PHONE",input.getString("CUSTOMER_PHONE",""));
					result.put("PRODUCT_OFFERING_ID",input.getString("PRODUCT_OFFERING_ID"));
					return result;
				}
				int pCode = Integer.valueOf(poidCode);
				if(pCode<1 && pCode>99){
					result.put("RSP_CODE","5000" );
					result.put("RSP_DESC","两位群组编码格式不对");
					result.put("OPR_TIME",SysDateMgr.getSysTime());
					result.put("CUSTOMER_PHONE",input.getString("CUSTOMER_PHONE",""));
					result.put("PRODUCT_OFFERING_ID",input.getString("PRODUCT_OFFERING_ID"));
					return result;
				}
				//判断唯一性（加上开始时间结束时间）
				IDataset codeInfo =MfcCommonUtil.getPoidCodeByOfferingID(input.getString("PRODUCT_OFFERING_ID"),input.getString("CUSTOMER_PHONE"),"1");//这里要加一个sql
				if(IDataUtil.isNotEmpty(codeInfo)){
					String pdCode = codeInfo.getData(0).getString("POID_CODE");
					if(pdCode.equals(poidCode)){
						result.put("RSP_CODE","5000" );
						result.put("RSP_DESC","两位群组编码已存在！");
						result.put("OPR_TIME",SysDateMgr.getSysTime());
						result.put("CUSTOMER_PHONE",input.getString("CUSTOMER_PHONE",""));
						result.put("PRODUCT_OFFERING_ID",input.getString("PRODUCT_OFFERING_ID"));
						return result;
					}
				}else{
					IDataset uuinfos = MfcCommonUtil.getRelationUusByUserSnRole( input.getString("CUSTOMER_PHONE"), "MF","1",null);
					if(IDataUtil.isNotEmpty(uuinfos)){
						for(int a=0;a<uuinfos.size();a++){
							String code =uuinfos.getData(a).getString("RSRV_STR2").substring(16); 
							if(poidCode.equals(code)){
								result.put("RSP_CODE","5000" );
								result.put("RSP_DESC","两位群组编码已存在！");
								result.put("OPR_TIME",SysDateMgr.getSysTime());
								result.put("CUSTOMER_PHONE",input.getString("CUSTOMER_PHONE",""));
								result.put("PRODUCT_OFFERING_ID",input.getString("PRODUCT_OFFERING_ID"));
								return result;
							}
						}
					}
				}
			}
			
		}
		/**
		 * 生成一个32位的序列
		 */
		 UUID uuid = UUID.randomUUID();
		 String POOrderNumber=uuid.toString().replace("-", "");
		 String  seqid =uuid.toString().replace("-", "");
	  	 IData param =new DataMap();
		 param.put("KIND_ID", "MFCOrderService_BBOSS_0_0");
         param.put("COMPANY_ID","898" );
         param.put("ORDER_TYPE","0" );
       //  param.put("PRODUCT_CODE","MFC000001" );
           param.put("PRODUCT_CODE",input.getString("PRODUCT_CODE") );

         param.put("ORDER_SOURCE",input.getString("ORDER_SOURCE") );
         param.put("CUSTOMER_TYPE","1");
         param.put("CUSTOMER_PHONE",input.getString("CUSTOMER_PHONE") );
         param.put("PO_ORDER_NUMBER",POOrderNumber );
         param.put("BIZ_VERSION","1.0.2");
         param.put("ACTION", input.getString("ACTION"));
         param.put("PRODUCT_OFFERING_ID", input.getString("PRODUCT_OFFERING_ID",""));
         param.put("POID_CODE", input.getString("POID_CODE"));
 		 param.put("POID_LABLE", input.getString("POID_LABLE"));

         log.debug("param="+param);
         IDataset rets= IBossCall.dealInvokeUrl("MFCOrderService_BBOSS_0_0","IBOSS6", param); 
    	 IData data = (IDataUtil.isEmpty(rets)) ? new DataMap() : rets.getData(0);
    	 if (IDataUtil.isNotEmpty(data) && "00".equals(data.getString("RSP_CODE", "")))
         {
		

		/**
		 * 新建一个idata  用来存放插表的数据（因为会有数据转换）
		 */
		
    	//调接口成功则查日志表TF_B_FAMILY_LOG

		
		IData inparam =new DataMap();
		inparam.put("SEQ_ID",seqid);
		inparam.put("PARTITION_ID",SysDateMgr.getCurMonth()); //分区标识
		inparam.put("ORDER_SOURCE_ID",input.get("ORDER_SOURCE") );//订单来源
		inparam.put("COMPANY_ID","898" );//订单来源省编码
		inparam.put("ORDER_TYPE","0");//订单类型
		inparam.put("PRODUCT_CODE",input.getString("PRODUCT_CODE") );//产品编码
		inparam.put("PO_ORDER_NUMBER", rets.getData(0).getString("PO_ORDER_NUMBER"));//业务订单号
		inparam.put("ACTION",input.get("ACTION") );//操作类型
		inparam.put("CUSTOMER_TYPE","1" );
		inparam.put("CUSTOMER_PHONE",input.get("CUSTOMER_PHONE") );
		inparam.put("EFF_TIME",SysDateMgr.getSysDateYYYYMMDDHHMMSS() );
		inparam.put("EXP_TIME",SysDateMgr.END_TIME_FOREVER);
		inparam.put("BIZ_VERSION","1.0.2");
	//	inparam.put("EXTEND_INFO","" );
		inparam.put("PRODUCT_OFFERING_ID",input.getString("PRODUCT_OFFERING_ID","") );
		inparam.put("RATE_NAME","" );
		inparam.put("CUSOMER_NAME","" );
		inparam.put("CUSTOMER_LOCATION","" );
		inparam.put("FINISH_TIME",SysDateMgr.END_TIME_FOREVER );
		inparam.put("REMARK",input.get("NOTES") );
		inparam.put("RSRV_STR3","0"); //在途工单 状态 设  0
		Boolean isEshopOrder =	Dao.insert("TF_B_FAMILY_LOG", inparam, Route.CONN_CRM_CEN);

	        if(!isEshopOrder){
	        	result.put("RSP_CODE", "2998");
	        	result.put("RSP_DESC","其他错误");
	        	result.put("PO_ORDER_NUMBER",rets.getData(0).getString("PO_ORDER_NUMBER"));
	        	
	        }else{
	        	result.put("RSP_CODE",data.getString("RSP_CODE") );
	        	result.put("RSP_DESC",data.getString("RSP_DESC"));
	        	result.put("PO_ORDER_NUMBER",rets.getData(0).getString("PO_ORDER_NUMBER"));
	        	if("00".equals(oprtype)){
	     			IData sendInfo = new DataMap();
	    			sendInfo.put("EPARCHY_CODE", CSBizBean.getVisit().getStaffEparchyCode());
	    		    sendInfo.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());
	    		    sendInfo.put("RECV_OBJECT", input.getString("CUSTOMER_PHONE",""));
	    		    sendInfo.put("RECV_ID", input.getString("USER_ID", "0"));
	    		    sendInfo.put("SMS_PRIORITY", input.getString("PRIORITY", "50"));
	    			String smsContent = "尊敬的用户，您已成功发送组网指令，系统正在处理，成功后会有成功组网的短信提醒，请您稍候，无需重复发送组网指令。【中国移动】";
	    			sendInfo.put("NOTICE_CONTENT", smsContent);
	    		    sendInfo.put("REMARK", "组网指令发送成功");
	    			SmsSend.insSms(sendInfo);		
	     		}
	        }
	        log.debug("我要打印这个result啦="+result);
	        return result;
		// TODO Auto-generated method stub
         }else {
        	 IData inparam2 =new DataMap();
     		inparam2.put("SEQ_ID",seqid);
     		inparam2.put("PARTITION_ID",SysDateMgr.getCurMonth()); //分区标识
     		inparam2.put("ORDER_SOURCE_ID",input.get("ORDER_SOURCE") );//订单来源
     		inparam2.put("COMPANY_ID","898" );//订单来源省编码
     		inparam2.put("ORDER_TYPE","0");//订单类型
     		inparam2.put("PRODUCT_CODE",input.getString("PRODUCT_CODE") );//产品编码
     		inparam2.put("PO_ORDER_NUMBER", rets.getData(0).getString("PO_ORDER_NUMBER"));//业务订单号
     		inparam2.put("ACTION",input.get("ACTION") );//操作类型
     		inparam2.put("CUSTOMER_TYPE","1" );
     		inparam2.put("CUSTOMER_PHONE",input.get("CUSTOMER_PHONE") );
     		inparam2.put("EFF_TIME",SysDateMgr.getSysDateYYYYMMDDHHMMSS() );
     		inparam2.put("BIZ_VERSION","1.0.2");
     		inparam2.put("PRODUCT_OFFERING_ID",input.getString("PRODUCT_OFFERING_ID","") );
     		inparam2.put("RATE_NAME","" );
     		inparam2.put("CUSOMER_NAME","" );
     		inparam2.put("RSP_CODE",data.getString("RSP_CODE",data.getString("X_RSPCODE")));
     		inparam2.put("RSP_DESC",data.getString("RSP_DESC",data.getString("X_RSPDESC")));
     		inparam2.put("RSRV_STR3","2"); 
     		inparam2.put("RSRV_STR4",SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND)); 
     		
     		Boolean isEshopOrder =	Dao.insert("TF_B_FAMILY_LOG", inparam2, Route.CONN_CRM_CEN);
     	        if(!isEshopOrder){
     	        	result.put("RSP_CODE", "2998");
     	        	result.put("RSP_DESC","其他错误");
     	        	result.put("PO_ORDER_NUMBER",rets.getData(0).getString("PO_ORDER_NUMBER"));
     	        	
     	        }else{
     	        	result.put("RSP_CODE",data.getString("RSP_CODE"));
     	        	result.put("RSP_DESC",data.getString("RSP_DESC"));
     	        	result.put("PO_ORDER_NUMBER",rets.getData(0).getString("PO_ORDER_NUMBER"));
     	        }
         }
 		
     	 log.debug("我要打印这个result啦="+result);
    //	System.out.println("我要打印这个result啦"+result);
    	  return result;
	}
	
	
	/**
	 * 校验主号码
	 * @param input
	 * @return
	 * @throws Exception
	 * @author huangyq
	 */
	private IData checkMainSerialNumber(IData input) throws Exception{
		String serialNumber = input.getString("CUSTOMER_PHONE");
		IDataset ucaInfo = UserInfoQry.getUserInfoBySn(serialNumber,"0");//加上remove_tag 的条件
		IData result=new DataMap();
		result.put("RSP_CODE", "00");
    	result.put("RSP_DESC", "校验成功！");
		try {
			// 调用账管接口，查欠费与否
			IData doweFee = AcctCall.getOweFeeByUserId(ucaInfo.getData(0).getString("USER_ID"));
			if (IDataUtil.isNotEmpty(doweFee)&& doweFee.getInt("ACCT_BALANCE", 0) < 0) {
				result.put("RSP_CODE", "99");
				result.put("RSP_DESC", "用户欠费！");
				return result;
			}
		} catch (Exception e) {
			result.put("RSP_CODE", "99");
			result.put("RSP_DESC", e.getMessage());
		}
		return result;
	}
	
	/**
	 * 校验副号码
	 * @param input
	 * @return
	 * @throws Exception
	 * @author huangyq
	 */
	private IData checkMebSerialNumber(IData input) throws Exception{
//		String serialNumber = input.getString("MEM_NUMBER"); // 成员号码（副号）
		IDataset data = input.getDataset("PRODUCT_ORDER_MEMBER");
		String menNumber = data.getData(0).getString("MEM_NUMBER");
//		IDataset ucaInfo = UserInfoQry.getUserInfoBySn(serialNumber,"0");//加上remove_tag 的条件
		IData ucaInfo = UcaInfoQry.qryUserInfoBySn(menNumber,RouteInfoQry.getEparchyCodeBySn(menNumber));
        
		IData result=new DataMap();
		result.put("RSP_CODE", "00");
    	result.put("RSP_DESC", "副号校验成功！");
		if (IDataUtil.isEmpty(ucaInfo)) {// 未查到有效的用户资料 （非销户）
			result.put("RSP_CODE", "99");
	    	result.put("RSP_DESC", "用户信息不存在！！！");
	    	return result;
		} else {
			try{
				if(!"0".equals(ucaInfo.getString("USER_STATE_CODESET",""))){
					result.put("RSP_CODE", "14");
			    	result.put("RSP_DESC", "添加号码状态异常！");
			    	return result;
                }
				//副号码状态异常
			/*	IDataset mebNumInfoS = UserInfoQry.getAllDestroyUserInfoBySn(menNumber);
		        if(IDataUtil.isNotEmpty(mebNumInfoS)&&IDataUtil.isEmpty(ucaInfo)){
		        	result.put("RSP_CODE", "14");
			    	result.put("RSP_DESC", "添加号码状态异常！");
			    	return result;
		        }*/
				// 校验 是否移动号码 ；是否携号
		        IData isMobile = MsisdnInfoQry.getMsisonBySerialnumberAsp(menNumber,"1");
				if(IDataUtil.isEmpty(isMobile)){
					
					IData mebNumInfo = UcaInfoQry.qryUserInfoBySn(menNumber,RouteInfoQry.getEparchyCodeBySn(menNumber));//加上remove_tag 的条件
					if(IDataUtil.isNotEmpty(mebNumInfo))
					{
						String userid = mebNumInfo.getString("USER_ID");
			        	IData npNumber = MfcCommonUtil.checkNpNumber(userid);
			        	if(!"99".equals(npNumber.getString("RSP_CODE"))){
			        		result.put("RSP_CODE", "14");
					    	result.put("RSP_DESC", "添加号码不是移动号码！");
					    	return result;
			        	}
					}else{
		        		result.put("RSP_CODE", "14");
				    	result.put("RSP_DESC", "添加号码不是移动号码！");
				    	return result;
		        	}
					
					
			    }
				// 调用账管接口，查欠费与否
				/*IData doweFee = AcctCall.getOweFeeByUserId(ucaInfo.getString("USER_ID"));
				if(IDataUtil.isNotEmpty(doweFee) && doweFee.getInt("ACCT_BALANCE", 0) < 0){
					result.put("RSP_CODE", "99");
			    	result.put("RSP_DESC", "用户已欠费！");
			    	return result;
				}*/
		        
		    	//副号是否已入家庭网 校验（ 副号码可能不在主号省，做全库查询）
				IDataset mebRelationUULists = MfcCommonUtil.getRelationUusByUserSnRole(menNumber,"MF",null,null);
				String action = input.getString("ACTION");// 操作类型 50-新增，有数据报错；51-删除，无数据报错；
				 String product= input.getString("PRODUCT_CODE");
				  IData sunFamily =MfcCommonUtil.checkCount(menNumber,product);	        
				if(("50".equals(action)&&IDataUtil.isNotEmpty(mebRelationUULists)&&"99".equals(sunFamily.getString("RSP_CODE"))) || ("51".equals(action)&&IDataUtil.isEmpty(mebRelationUULists))){
					result.put("RSP_CODE", "99");
					String desc = ("50".equals(action))?"该号码已入10个家庭网，不能再加入其他家庭网":"该号码还未加入家庭网，不能删除";
			    	result.put("RSP_DESC", desc);
				}
			}catch(Exception e){
				result.put("RSP_CODE", "99");
		    	result.put("RSP_DESC", e.getMessage());
			}
		}
		return result;
	}
	
	  private void checkPramByKeys(IData data ,String keyNamesStr) throws Exception
	    {
	        String keyNames [] = keyNamesStr.split(",");
	       for (String strColName : keyNames)
	        {
	            IDataUtil.chkParam(data, strColName);
	        }
	       
	    }
	    private void updateInfo(IData inData) throws Exception
	    {

	        DBConnection conn = new DBConnection("cen1", true, false);
	        try
	        {
	            SQLParser parser = new SQLParser(inData);
	            parser.addSQL(" UPDATE TF_B_FAMILY_LOG SET ");
	            parser.addSQL(" OPR_TIME        = :OPR_TIME, ");    
                parser.addSQL(" PRODUCT_OFFERING_ID   = :PRODUCT_OFFERING_ID, ");
//	            parser.addSQL(" PO_SPEC_NUMBER   = :PO_SPEC_NUMBER, ");
                parser.addSQL(" RSRV_STR3   = :RSRV_STR3, ");
	            parser.addSQL(" ACTION   = :ACTION, ");
	            parser.addSQL(" CUSTOMER_TYPE   = :CUSTOMER_TYPE, ");
	            parser.addSQL(" CUSTOMER_PHONE   = :CUSTOMER_PHONE, ");
	            parser.addSQL(" BIZ_VERSION   = :BIZ_VERSION, ");
	            parser.addSQL(" BUSINESS_TYPE   = :BUSINESS_TYPE, "); 
	            parser.addSQL(" ORDER_TYPE = :ORDER_TYPE, ");
	            parser.addSQL(" RSP_TYPE = :RSP_TYPE, ");
	            parser.addSQL(" MEM_TYPE = :MEM_TYPE, ");
	            parser.addSQL(" MEM_NUMBER = :MEM_NUMBER, ");
	            parser.addSQL(" MEM_ORDER_NUMBER = :MEM_ORDER_NUMBER, ");
	            parser.addSQL(" EXP_TIME  = :EXP_TIME, ");
	            parser.addSQL(" RSP_CODE = :RSP_CODE, ");
	            parser.addSQL(" RSP_DESC = :RSP_DESC, ");
	            parser.addSQL(" INFO_CODE = :INFO_CODE, ");
	            parser.addSQL(" INFO_VALUE = :INFO_VALUE, ");
	            parser.addSQL(" PO_ORDER_NUMBER   = :PO_ORDER_NUMBER ");
	            parser.addSQL(" WHERE 1=1 ");
	            parser.addSQL(" AND PO_ORDER_NUMBER = :PO_ORDER_NUMBER  ");
	            parser.addSQL(" AND PRODUCT_OFFERING_ID = :PRODUCT_OFFERING_ID ");
	            parser.addSQL(" AND CUSTOMER_PHONE = :CUSTOMER_PHONE ");
	            parser.addSQL(" AND MEM_NUMBER = :MEM_NUMBER ");
	            parser.addSQL(" AND ACTION = :ACTION ");

	            CrmDAO dao = DAOManager.createDAO(CrmDAO.class, Route.CONN_CRM_CEN);
	            dao.executeUpdate(conn, parser.getSQL(), parser.getParam());

	            conn.commit();
	        }
	        catch (Exception e)
	        {
	            conn.rollback();
	            
	            CSAppException.apperr(CrmCommException.CRM_COMM_103, e.getMessage());
	        }
	        finally
	        {
	            conn.close();
	        }
	    }
/**
 * 3.2开通家庭网BBOSS反馈失败通知落地接口，处理日志表
 * 成员管理确认是对成员管理的确认应答 ，处理日志表
 * @param input
 * @return
 * @throws Exception
 */
public IData dealLog(IData input)throws Exception {
	String tag = input.getString("KIND_TYPE");//一级boss会返回一个区分3.2还是3.4的标识   根据此标识来判断是哪个接口。
	IData result = new DataMap();
	result.put("RESULT_CODE", "00");
	result.put("RESULT_DESC", "成功");
	try{
	String expDate=SysDateMgr.getSysDateYYYYMMDDHHMMSS();
	IDataset extendinfos = input.getDataset("EXTEND_INFO");
		String code ="";
		String value="";
	    if(IDataUtil.isNotEmpty(extendinfos)){	    
			for(int i=0; i<extendinfos.size();i++){
				  if(i==0){
						code =extendinfos.getData(i).getString("INFO_CODE");
						value =extendinfos.getData(i).getString("INFO_VALUE");
					}else{
						code+=","+extendinfos.getData(i).getString("INFO_CODE");
						value+=","+extendinfos.getData(i).getString("INFO_VALUE");
					}
			}
	    }
	if("MU".equals(tag)){
		//mu时是走3.2 接口  ，处理日志表
	    checkPramByKeys(input,"OPR_TIME,PO_ORDER_NUMBER,PRODUCT_ORDER_RSPCODE,PO_SPEC_NUMBER,OPERATION_SUBTYPE_ID,CUSTOMER_TYPE,CUSTOMER_PHONE,BIZ_VERSION");	   
	    input.put("INFO_CODE", code);
	    input.put("INFO_VALUE", value);
	    input.put("RSP_CODE", input.getString("PRODUCT_ORDER_RSPCODE",""));
	    input.put("RSP_DESC", input.getString("PRODUCT_ORDER_RSPDESC",""));
	    input.put("ACTION", input.getString("OPERATION_SUBTYPE_ID",""));
	    input.put("EXP_TIME", expDate);
	    input.put("RSRV_STR3", "1");// 工单状态  修改 为  1  表示 完工
	    updateInfo(input);
	}else if("MM".equals(tag)){	//MM时是走3.4 接口  ，处理日志表
		checkPramByKeys(input,"PO_ORDER_NUMBER,PRODUCT_OFFERING_ID,BUSINESS_TYPE,ORDER_TYPE,RSP_TYPE,CUSTOMER_PHONE,BIZ_VERSION");	
		IDataset mebList=input.getDataset("RSLT");
		if(IDataUtil.isEmpty(mebList)){
			CSAppException.apperr(CrmCommException.CRM_COMM_103,"RSLT节点不存在！");	
		}
		for(int i=0; i<mebList.size();i++){
			IData mebInfo=mebList.getData(i);
			mebInfo.put("PO_ORDER_NUMBER", input.getString("PO_ORDER_NUMBER",""));
			mebInfo.put("PRODUCT_OFFERING_ID", input.getString("PRODUCT_OFFERING_ID",""));
			mebInfo.put("CUSTOMER_PHONE", input.getString("CUSTOMER_PHONE",""));
			mebInfo.put("BUSINESS_TYPE", input.getString("BUSINESS_TYPE",""));
			mebInfo.put("ORDER_TYPE", input.getString("ORDER_TYPE",""));
			mebInfo.put("RSP_TYPE", input.getString("RSP_TYPE",""));
			mebInfo.put("BIZ_VERSION", input.getString("BIZ_VERSION",""));
			mebInfo.put("INFO_CODE", code);
			mebInfo.put("INFO_VALUE", value);
			mebInfo.put("EXP_TIME", expDate);
			mebInfo.put("RSRV_STR3", "1");// 工单状态  修改 为  1  表示 完工
			String repCode=mebInfo.getString("RSP_CODE","");
			IData sendInfo = new DataMap();
			sendInfo.put("EPARCHY_CODE", CSBizBean.getVisit().getStaffEparchyCode());
		    sendInfo.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());
		    sendInfo.put("RECV_OBJECT", input.getString("CUSTOMER_PHONE",""));
		    sendInfo.put("RECV_ID", input.getString("USER_ID", "0"));
		    sendInfo.put("SMS_PRIORITY", input.getString("PRIORITY", "50"));
			IData iData = new DataMap();
			iData.put("PRODUCT_OFFERING_ID",input.getString("PRODUCT_OFFERING_ID",""));
			IDataset relaUUDatas =  MfcCommonUtil.getRelationUusByUserSnRole(input.getString("CUSTOMER_PHONE",""),"MF","1",iData);
			IDataset config = CommparaInfoQry.getCommparaAllCol("CSM", "2019", "MCF_SENDFAIL_SMS", "ZZZZ");

			IDataset fgconfig = CommparaInfoQry.getCommparaAllCol("CSM", "2019", "MCF_SENDFAIL_5GSMS", "ZZZZ");

			IDataset zfbconfig = CommparaInfoQry.getCommparaAllCol("CSM", "2019", "MCF_SENDFAIL_ZFBSMS", "ZZZZ");

			IDataset ywconfig = CommparaInfoQry.getCommparaAllCol("CSM", "2019", "MCF_SENDFAIL_YWSMS", "ZZZZ");

			
			Boolean isZF=false;
			String templateId = "";
			String remark = relaUUDatas.getData(0).getString("REMARK","");
			if(IDataUtil.isNotEmpty(relaUUDatas)){

				if (remark.contains(MfcCommonUtil.PRODUCT_CODE_ZF) || remark.contains(MfcCommonUtil.PRODUCT_CODE_5G3) || remark.contains(MfcCommonUtil.PRODUCT_CODE_5G4) || remark.contains(MfcCommonUtil.PRODUCT_CODE_5G5) 
						|| remark.contains(MfcCommonUtil.PRODUCT_CODE_TF6) || remark.contains(MfcCommonUtil.PRODUCT_CODE_TF7) || remark.contains(MfcCommonUtil.PRODUCT_CODE_TF8) 
						|| remark.contains(MfcCommonUtil.PRODUCT_CODE_TF9) || remark.contains(MfcCommonUtil.PRODUCT_CODE_TF10) || remark.contains(MfcCommonUtil.PRODUCT_CODE_TF11)){
					isZF=true;
				}else {
					isZF=false;
				}
			}
			String poidCode = MfcCommonUtil.getPoidInfoAndLable(input.getString("CUSTOMER_PHONE"),input.getString("PRODUCT_OFFERING_ID"),null).getString("POID_CODE");

			if("16".equals(repCode) ){

				if(IDataUtil.isNotEmpty(fgconfig) && (remark.contains(MfcCommonUtil.PRODUCT_CODE_5G3) || remark.contains(MfcCommonUtil.PRODUCT_CODE_5G4) || remark.contains(MfcCommonUtil.PRODUCT_CODE_5G5)
						)){
					templateId = fgconfig.getData(0).getString("PARA_CODE4","");
				}else if(IDataUtil.isNotEmpty(zfbconfig) && (remark.contains(MfcCommonUtil.PRODUCT_CODE_TF6) || remark.contains(MfcCommonUtil.PRODUCT_CODE_TF7) || remark.contains(MfcCommonUtil.PRODUCT_CODE_TF8)
						)){
					templateId = zfbconfig.getData(0).getString("PARA_CODE4","");
				}else if(IDataUtil.isNotEmpty(ywconfig) && (remark.contains(MfcCommonUtil.PRODUCT_CODE_TF9) || remark.contains(MfcCommonUtil.PRODUCT_CODE_TF10) || remark.contains(MfcCommonUtil.PRODUCT_CODE_TF11)
						)){
					templateId = ywconfig.getData(0).getString("PARA_CODE4","");
				}else if (IDataUtil.isNotEmpty(config)){//超出群组数量
					if(isZF){//自付
						templateId = config.getData(0).getString("PARA_CODE4","");
					}else{//统付
						templateId = config.getData(0).getString("PARA_CODE1","");
					}
				}

				IData inData = new DataMap();
				inData.put("MFC_MEM_PHONES",mebInfo.getString("MEM_NUMBER",""));
				inData.put("PRODUCT_OFFERING_ID",poidCode);

				String smsContent = MfcCommonUtil.getSmsContentByTemplateId(templateId,inData);
		        sendInfo.put("NOTICE_CONTENT", smsContent);
		        sendInfo.put("REMARK", "主号添加副号失败");
		  
			}else if("01".equals(repCode)){
				 sendInfo.put("NOTICE_CONTENT", "订单号错误");
			     sendInfo.put("REMARK", "主号添加副号失败");
			}else if("02".equals(repCode)){
				 sendInfo.put("NOTICE_CONTENT", "产品编码错误");
			     sendInfo.put("REMARK", "主号添加副号失败");
			}else if("03".equals(repCode)){
				 sendInfo.put("NOTICE_CONTENT", "业务订购实例ID错误(群组编码错误)");
			     sendInfo.put("REMARK", "主号添加副号失败");
			}else if("04".equals(repCode)){
				 sendInfo.put("NOTICE_CONTENT", "客户标识错误");
			     sendInfo.put("REMARK", "主号添加副号失败");
			}else if("05".equals(repCode)){
				 sendInfo.put("NOTICE_CONTENT", "业务类型错误");
			     sendInfo.put("REMARK", "主号添加副号失败");
			}else if("06".equals(repCode)){
				 sendInfo.put("NOTICE_CONTENT", "订单来源错误");
			     sendInfo.put("REMARK", "主号添加副号失败");
			}else if("07".equals(repCode)){
				 sendInfo.put("NOTICE_CONTENT", "订单来源省编码错误");
			     sendInfo.put("REMARK", "主号添加副号失败");
			}else if("08".equals(repCode)){
				 sendInfo.put("NOTICE_CONTENT", "订单类型错误");
			     sendInfo.put("REMARK", "主号添加副号失败");
			}else if("09".equals(repCode)){
				 sendInfo.put("NOTICE_CONTENT", "操作类型错误");
			     sendInfo.put("REMARK", "主号添加副号失败");
			}else if("10".equals(repCode)){
				 sendInfo.put("NOTICE_CONTENT", "成员类型错误");
			     sendInfo.put("REMARK", "主号添加副号失败");
			}else if("11".equals(repCode)){
				 sendInfo.put("NOTICE_CONTENT", "成员区号错误");
			     sendInfo.put("REMARK", "主号添加副号失败");
			}else if("12".equals(repCode)){
				 sendInfo.put("NOTICE_CONTENT", "成员号码错误");
			     sendInfo.put("REMARK", "主号添加副号失败");
			}else if("14".equals(repCode)){
				 sendInfo.put("NOTICE_CONTENT", "用户状态不正常");
			     sendInfo.put("REMARK", "主号添加副号失败");
			}else if ("13".equals(repCode)){

				if(IDataUtil.isNotEmpty(fgconfig) && (remark.contains(MfcCommonUtil.PRODUCT_CODE_5G3) || remark.contains(MfcCommonUtil.PRODUCT_CODE_5G4) || remark.contains(MfcCommonUtil.PRODUCT_CODE_5G5))){
					templateId = fgconfig.getData(0).getString("PARA_CODE3","");
				}else if(IDataUtil.isNotEmpty(zfbconfig) && (remark.contains(MfcCommonUtil.PRODUCT_CODE_TF6) || remark.contains(MfcCommonUtil.PRODUCT_CODE_TF7) || remark.contains(MfcCommonUtil.PRODUCT_CODE_TF8)
						)){
					templateId = zfbconfig.getData(0).getString("PARA_CODE3","");
				}else if(IDataUtil.isNotEmpty(ywconfig) && (remark.contains(MfcCommonUtil.PRODUCT_CODE_TF9) || remark.contains(MfcCommonUtil.PRODUCT_CODE_TF10) || remark.contains(MfcCommonUtil.PRODUCT_CODE_TF11)
						)){
					templateId = ywconfig.getData(0).getString("PARA_CODE3","");
				}else if(IDataUtil.isNotEmpty(config)){
					if(isZF){//自付
						templateId = config.getData(0).getString("PARA_CODE3","");
					}else{//统付
						templateId = config.getData(0).getString("PARA_CODE3","");
					}
				}

				IData inData = new DataMap();
				inData.put("MFC_MEM_PHONES",mebInfo.getString("MEM_NUMBER",""));
				inData.put("PRODUCT_OFFERING_ID",poidCode);
				String smsContent = MfcCommonUtil.getSmsContentByTemplateId(templateId,inData);
		        sendInfo.put("NOTICE_CONTENT", smsContent);
		        sendInfo.put("REMARK", "副号码加入失败");
			}else if ("15".equals(repCode)){//24小时未确认

				if(IDataUtil.isNotEmpty(fgconfig) && (remark.contains(MfcCommonUtil.PRODUCT_CODE_5G3) || remark.contains(MfcCommonUtil.PRODUCT_CODE_5G4) || remark.contains(MfcCommonUtil.PRODUCT_CODE_5G5))){
					templateId = fgconfig.getData(0).getString("PARA_CODE6","");
				}else if(IDataUtil.isNotEmpty(zfbconfig) && (remark.contains(MfcCommonUtil.PRODUCT_CODE_TF6) || remark.contains(MfcCommonUtil.PRODUCT_CODE_TF7) || remark.contains(MfcCommonUtil.PRODUCT_CODE_TF8)
						)){
					templateId = zfbconfig.getData(0).getString("PARA_CODE6","");
				}else if(IDataUtil.isNotEmpty(ywconfig) && (remark.contains(MfcCommonUtil.PRODUCT_CODE_TF9) || remark.contains(MfcCommonUtil.PRODUCT_CODE_TF10) || remark.contains(MfcCommonUtil.PRODUCT_CODE_TF11)
						)){
					templateId = ywconfig.getData(0).getString("PARA_CODE6","");
				}else if(IDataUtil.isNotEmpty(config)){
					if(isZF){//自付
						templateId = config.getData(0).getString("PARA_CODE6","");
					}else{//统付
						templateId = config.getData(0).getString("PARA_CODE5","");
					}
				}

				IData inData = new DataMap();
				inData.put("MFC_MEM_PHONES",mebInfo.getString("MEM_NUMBER",""));
				inData.put("PRODUCT_OFFERING_ID",poidCode);
				String smsContent = MfcCommonUtil.getSmsContentByTemplateId(templateId,inData);
		        sendInfo.put("NOTICE_CONTENT", smsContent);
		        sendInfo.put("REMARK", "副号码加入失败");
			}else if("97".equals(repCode)){
				 sendInfo.put("NOTICE_CONTENT", "开通超时");
			     sendInfo.put("REMARK", "主号添加副号失败");
			}else{
				sendInfo.put("NOTICE_CONTENT", "其他错误");
			    sendInfo.put("REMARK", "主号添加副号失败");
			}
			SmsSend.insSms(sendInfo);
			updateInfo(mebInfo);
		}	
		}	
	}catch(Exception e){
		result.put("RESULT_CODE", "99");
		result.put("RESULT_DESC", "失败原因："+e.getMessage());	
	}
	return result;
}

public static IDataset getRelationUusByUserSnRole(String serialNumberB, String relationTypeCode, String roleCodeB) throws Exception {
	IData iparam = new DataMap();
	iparam.put("SERIAL_NUMBER_B", serialNumberB);
	iparam.put("RELATION_TYPE_CODE", relationTypeCode);
	iparam.put("ROLE_CODE_B", roleCodeB);
	return Dao.qryByCode("TF_F_RELATION_UU", "SEL_USER_SN_ROLENEW", iparam);
}
/**
 * 3.3 新增删除上发接口
 * @param input
 * @return
 * @throws Exception
 */
public IData controlInfo(IData input2) throws Exception {
	
	IData input = new DataMap(input2.toString());
	IData result =new DataMap();
	System.out.println("000000====input===="+input);	
	/**
	 * 生成一个32位的序列
	 */
	IDataset meblist =input.getDataset("PRODUCT_ORDER_MEMBER");
	System.out.println("meblist====meblist===="+meblist);	

	String sn =input.getString("CUSTOMER_PHONE");
	 String tag = input.getString("ACTION");
	 if("51".equals(tag)){
	 	 //判断输入的主号和副号对应的主号是否对应
		 IDataset dataset = input.getDataset("PRODUCT_ORDER_MEMBER");
		 String memNumber=dataset.getData(0).getString("MEM_NUMBER");
		 IDataset userBInfo = RelaUUInfoQry.getRelationUusBySnBTypeCode(memNumber, "MF");
		 if(IDataUtil.isNotEmpty(userBInfo)){
			 //获取虚拟号
			 String userIdA =userBInfo.getData(0).getString("USER_ID_A", "");

			 //通过虚拟号获取关联的手机号码信息
			 IDataset userBInfo1=RelaUUInfoQry.getUserRelationRole2(userIdA,"MF","1");

			 if(IDataUtil.isNotEmpty(userBInfo1)){
				 String snDB = userBInfo1.first().getString("SERIAL_NUMBER_B");
				 log.debug("sn:"+sn);
				 log.debug("snDB:"+snDB);
				 if(!sn.equals(snDB)){
					 CSAppException.apperr(CrmUserException.CRM_USER_783, "输入的主号不正确，请确认！");
				 }
			 }
		 }


		  IData destroyParam = new DataMap();
		  destroyParam.put("ACTION", "01");
		  destroyParam.put("CUSTOMER_PHONE", sn);
		  IDataset destroyList= Dao.qryByCodeParser("TF_B_FAMILY_LOG", "SEL_MFC_BYMEBNO", destroyParam,Route.CONN_CRM_CEN);
		  if(IDataUtil.isNotEmpty(destroyList)){
		      	CSAppException.apperr(CrmUserException.CRM_USER_783, "该主号有在途注销未完工工单，请稍后再试！");
		  }
		 
		 IData onParam = new DataMap();
		 ;
		 boolean isExistMemBer=false;
		  onParam.put("MEM_NUMBER", dataset.getData(0).getString("MEM_NUMBER"));
		  onParam.put("ACTION", "51");
		  IDataset resultTranList= Dao.qryByCodeParser("TF_B_FAMILY_LOG", "SEL_MFC_BYMEBNO", onParam,Route.CONN_CRM_CEN); //查询数据库
		  if(IDataUtil.isNotEmpty(resultTranList)){
		      	CSAppException.apperr(CrmUserException.CRM_USER_783, "该号码仍有在途未完工工单，请稍后再试！");
		  }
		  	
		  	 IData qryData = new DataMap();
		     qryData.put("MEM_TYPE", "1");
		     qryData.put("CUSTOMER_PHONE", "");
		     qryData.put("BIZ_VERSION", "1.0.0");
		     qryData.put("PRODUCT_OFFERING_ID", input.getString("PRODUCT_OFFERING_ID"));
		     qryData.put("MEM_AREA_CODE", "");
		     qryData.put("MEM_NUMBER", "");
		     qryData.put("PRODUCT_CODE", "");
		     qryData.put("BUSINESS_TYPE", "1");
		     IDataset rtDataset =bossRealTimeInfo(qryData);
		     if (IDataUtil.isNotEmpty(rtDataset)){
		     	String rsp_code = rtDataset.getData(0).getString("BIZ_ORDER_RESULT");
		     	String rsp_desc = rtDataset.getData(0).getString("RSP_DESC");
		     	if(StringUtils.isNotBlank(rsp_code)){
		     		if(!"00".equals(rsp_code)){
		            	CSAppException.apperr(CrmUserException.CRM_USER_783, "不存在该群组【"+input.getString("PRODUCT_OFFERING_ID")+"】");
		     		}else{
		    			IDataset memberList=rtDataset.getData(0).getDataset("RSP_RESULT").getData(0).getDataset("MEMBER_LIST");//正式成员
		    			for(int k=0;k<memberList.size();k++){
		    				String bmemNumber = memberList.getData(k).getString("MEM_NUMBER");
		    				if(memNumber.equals(bmemNumber)){
		    					isExistMemBer=true;
		    					break;
		    				}
		    			}
		     		}
		     	}
		     	if(!isExistMemBer){
	            	CSAppException.apperr(CrmUserException.CRM_USER_783, "不存在该成员【"+memNumber+"】");
		     	}
		     }
	 }

//	IDataset relationUULists =MfcCommonUtil.getRelationUusByUserSnRole(sn,"MF","1",null);//qryAllRelaUUBySnB(mebNumber,relationTypeCode );			
	 IData tradelist =new DataMap();
		/** 
		 * 新增更新的字段
		 */
		String OrderSource = input.getString("ORDER_SOURCE");
		if("02".equals(OrderSource)){//短厅过来的
			tradelist.put("TRADE_STAFF_ID", input.getString("TRADE_STAFF_ID"));
			tradelist.put("TRADE_DEPART_ID", input.getString("TRADE_DEPART_ID"));
			tradelist.put("TRADE_CITY_CODE", input.getString("TRADE_CITY_CODE"));
			tradelist.put("TRADE_EPARCHY_CODE", input.getString("TRADE_EPARCHY_CODE"));
			tradelist.put("IN_MODE_CODE", input.getString("IN_MODE_CODE"));
		}else{
			tradelist.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
			tradelist.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
			tradelist.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
			tradelist.put("TRADE_EPARCHY_CODE",CSBizBean.getVisit().getStaffEparchyCode());
			tradelist.put("IN_MODE_CODE",CSBizBean.getVisit().getInModeCode());
		}
	//if(IDataUtil.isNotEmpty(relationUULists)){
		input.put("CUSTOMER_PHONE", sn);

		/**
		 * 别人吊我  传  号码  成员   action  订单来源
		 */

		IData param=new DataMap();

		//param.put("PRODUCT_CODE", "MFC000001");//后续改造  从前台获取  区分自付统付
        param.put("PRODUCT_CODE",input.getString("PRODUCT_CODE") );

		param.put("PRODUCT_OFFERING_ID", input.getString("PRODUCT_OFFERING_ID"));
		param.put("BUSINESS_TYPE", "1");
		param.put("COMPANY_ID", "898");
		param.put("ORDER_TYPE", "0");
		param.put("BIZ_VERSION", "1.0.1");
		param.put("PO_ORDER_NUMBER", SeqMgr.getInstId());
		param.put("CUSTOMER_PHONE", input.get("CUSTOMER_PHONE"));
		param.put("PRODUCT_ORDER_MEMBER", meblist);
		param.put("ACTION", input.get("ACTION"));
		param.put("ORDER_SOURCE", input.get("ORDER_SOURCE"));
		param.put("KIND_ID", "MFCMemService_BBOSS_0_0");
		IDataset rets= IBossCall.dealInvokeUrl("MFCMemService_BBOSS_0_0","IBOSS6", param);  
		IData data = (IDataUtil.isEmpty(rets)) ? new DataMap() : rets.getData(0);
		log.debug("data="+data);
		if (IDataUtil.isNotEmpty(data) && "0000".equals(data.getString("X_RSPCODE", "")))
		{
			Boolean isEshopOrder  =true;

			String snList ="";
			try{
				for(int i=0;i<meblist.size();i++){
					String SEQ_ID=SeqMgr.getInstId();
					IData inparam =new DataMap();

					inparam.put("SEQ_ID",SEQ_ID );
					inparam.put("PARTITION_ID",SysDateMgr.getCurMonth()); //分区标识
					inparam.put("PO_ORDER_NUMBER", rets.getData(0).getString("PO_ORDER_NUMBER") );
					inparam.put("PRODUCT_CODE",input.getString("PRODUCT_CODE") );//产品编码

					inparam.put("PRODUCT_OFFERING_ID", input.getString("PRODUCT_OFFERING_ID",""));
					inparam.put("CUSTOMER_PHONE",input.get("CUSTOMER_PHONE"));
					inparam.put("BUSINESS_TYPE", "1");
					inparam.put("ORDER_SOURCE_ID", input.get("ORDER_SOURCE"));
					inparam.put("CUSTOMER_TYPE", "1");
					inparam.put("COMPANY_ID", "898");
					inparam.put("ORDER_TYPE", "0");
					inparam.put("ACTION", input.get("ACTION"));
					inparam.put("BIZ_VERSION", "1.0.1");
					inparam.put("INFO_CODE", input.get("INFO_CODE"));
					inparam.put("INFO_VALUE", input.get("INFO_VALUE"));
					inparam.put("MEM_TYPE",  meblist.getData(i).getString("MEM_TYPE",""));
					inparam.put("MEM_AREA_CODE", meblist.getData(i).getString("MEM_AREA_CODE",""));
					inparam.put("MEM_NUMBER",meblist.getData(i).getString("MEM_NUMBER"));
					inparam.put("MEM_ORDER_NUMBER", meblist.getData(i).getString("MEM_ORDER_NUMBER"));
					inparam.put("FINISH_TIME", input.get("FINISH_TIME"));
					inparam.put("REMARK", input.get("NOTES"));
					inparam.put("EFF_TIME",SysDateMgr.getSysDateYYYYMMDDHHMMSS() );
					inparam.put("EXP_TIME",SysDateMgr.END_TIME_FOREVER  );
					inparam.put("RSRV_STR1",input.getString("DEL_TAG") );
					inparam.put("RSRV_STR3","0");
					isEshopOrder =	Dao.insert("TF_B_FAMILY_LOG", inparam, Route.CONN_CRM_CEN);
					if(!isEshopOrder){
						snList = snList+meblist.getData(i).getString("MEM_NUMBER")+",";
					}else{
						result.put("RSP_CODE", "0000");
						result.put("RSP_DESC", "插表成功");
					}
				}
				if(StringUtils.isNotBlank(snList))
				{//存在成员号码登记日志表失败
					result.put("RSP_CODE","0000");
					log.debug("=================controlInfo====================");
					log.debug("成员号码:"+snList.substring(0, snList.length()-1)+"日志信息处理失败");
				}
			}catch(Exception e){
				result.put("RSP_CODE","0000");
				log.debug("=================controlInfo====================");
				result.put("RSP_DESC",e.getMessage());
			}
			return  result;
		}else {
			result.put("RSP_CODE","2999");
			result.put("RSP_DESC","系统错误请联系管理员");
			result.put("OPR_TIME",SysDateMgr.getSysTime());
			result.put("CUSTOMER_PHONE",input.getString("CUSTOMER_PHONE",""));

		}
		return result;
	//}

}

	/**
	 * 成员管理（新增/删除）时的校验
	 * @param input
	 * @throws Exception
	 * @author huangyq
	 */
	public IData checkMebManage(IData input) throws Exception{
		IData result = new DataMap();
		result.put("RSP_CODE", "00");
		result.put("RSP_DESC", "成员管理业务校验成功！");
		//异网号码不校验
		if("3".equals(input.getString("MEM_TYPE", "")))
		{
			return result;
		}
		
		// 校验 主号+副号
		IData mebRs = checkMebSerialNumber(input); // 副号校验
		if(!"00".equals(mebRs.getString("RSP_CODE"))){
			result.put("RSP_CODE", mebRs.getString("RSP_CODE"));
			result.put("RSP_DESC", mebRs.getString("RSP_DESC"));
		}
		return result;
	}
/**
 * 主号码开通家庭网校验落地接口
 * @param input
 * @return
 * @throws Exception
 */
public IData checkMeb(IData input) throws Exception {
	IData param =new DataMap();
	param.put("SERIAL_NUMBER", input.getString("CUSTOMER_PHONE"));
	param.put("REMOVE_TAG", "0");
	log.debug("checkMeb==========入参"+input);
	// TODO Auto-generated method stub
	//String SN= input.getString("SERIAL_NUMBER");
	String serialNumber = input.getString("CUSTOMER_PHONE");
	log.debug("serialNumber=============="+serialNumber);
	IData ucaInfo = UcaInfoQry.qryUserInfoBySn(serialNumber,RouteInfoQry.getEparchyCodeBySn(serialNumber));//加上remove_tag 的条件
//	IData ucaInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
	log.debug("用户资料"+ucaInfo);
    String flag = "" ;
	if(IDataUtil.isEmpty(ucaInfo)){//未查到有效的用户资料  （非销户）
		flag = "99" ;
    }else{
    	 if( !"0".equals(ucaInfo.getString("USER_STATE_CODESET",""))){
    	     	flag = "99" ;
    	      }
    	    
    }
      //用户状态非正常
     
    log.debug("打印flag"+flag);
    IData result=new DataMap();
    result.put("RSP_CODE", "00");
	result.put("RSP_DESC", "成功");
    if("99".equals(flag)){
    	
    	result.put("RSP_CODE", "99");
    	result.put("RSP_DESC", "该用户状态异常，无法办理业务！！！");
    	return result;
    }else{
    /*	// 创家业务受理时 主号码校验
    	IData rs = checkMainSerialNumber(input);
    	if(!"00".equals(rs.getString("RSP_CODE"))){
    		result.put("RSP_CODE", rs.getString("RSP_CODE"));
        	result.put("RSP_DESC", rs.getString("RSP_DESC"));
    	}else{
    		result.put("RSP_CODE", "00");
        	result.put("RSP_DESC", "成功");
    	}*/
    }
    String product= input.getString("PRODUCT_CODE");
	  IData sunFamily =MfcCommonUtil.checkCount(serialNumber,product);
	  if(!"00".equals(sunFamily.getString("RSP_CODE"))){
			result.put("RSP_CODE", "99");
			result.put("RSP_DESC",sunFamily.getString("RSP_DESC") );
			return result;
	  }
	  //携号转网
	  /*String userid = ucaInfo.getString("USER_ID");
	  IData npNumber = MfcCommonUtil.checkNpNumber(userid);
	  if(IDataUtil.isNotEmpty(npNumber)){
		  String rspCode = npNumber.getString("RSP_CODE");
		  if("99".equals(rspCode)){
			  	result.put("RSP_CODE",rspCode );
				result.put("RSP_DESC","该号码是携号转网用户，无法办理业务");
				return result;
		  }
	  }*/
	  UcaData ucaData =UcaDataFactory.getNormalUca(serialNumber);
	  if(StringUtils.isNotBlank(ucaData.getBrandCode())&&StringUtils.equals("PWLW", ucaData.getBrandCode())){
			result.put("RSP_CODE", "99");
			result.put("RSP_DESC","该号码为物联网用户，无法办理业务");
			return result;
	  }
	
	return result;
}

private void updateSyncInfo(IData inData) throws Exception
{
    DBConnection conn = new DBConnection("cen1", true, false);
    try
    {
        SQLParser parser = new SQLParser(inData);
        parser.addSQL(" UPDATE TI_B_MFC_SYNC SET ");
        parser.addSQL(" QRY_TAG        = :QRY_TAG, ");
        parser.addSQL(" QRY_FILE_NAME   = :QRY_FILE_NAME ");
        parser.addSQL(" WHERE BUSINESS_TYPE   = : BUSINESS_TYPE ");
        parser.addSQL(" AND BIZ_VERSION   = : BIZ_VERSION ");
        parser.addSQL(" AND PRODUCT_OFFERING_ID   = : PRODUCT_OFFERING_ID ");
        parser.addSQL(" AND CUSTOMER_PHONE   = : CUSTOMER_PHONE ");
        parser.addSQL(" AND MEM_TYPE    = : MEM_TYPE  ");
        parser.addSQL(" AND MEM_AREA_CODE   = : MEM_AREA_CODE ");
        parser.addSQL(" AND  MEM_NUMBER   = :  MEM_NUMBER ");

        CrmDAO dao = DAOManager.createDAO(CrmDAO.class, Route.CONN_CRM_CEN);
        dao.executeUpdate(conn, parser.getSQL(), parser.getParam());

        conn.commit();
    }
    catch (Exception e)
    {
        conn.rollback();
        
        CSAppException.apperr(CrmCommException.CRM_COMM_103, e.getMessage());
    }
    finally
    {
        conn.close();
    }
}
public IDataset getMfcMainPhoneInfo(IData input) throws Exception{
	IDataset ret=new DatasetList();
	IData result=new DataMap();
	String custPhone="";
	String mebNumber=input.getString("SERIAL_NUMBER_B");
	String relationTypeCode=input.getString("RELATION_TYPE_CODE");
	String roleCodeB=input.getString("ROLE_CODE_B");
	IDataset uua =MfcCommonUtil.getRelationUusByUserSnRole(mebNumber,relationTypeCode,roleCodeB,null);//qryAllRelaUUBySnB(mebNumber,relationTypeCode );	
	if(IDataUtil.isNotEmpty(uua)){
		String userIdA=uua.getData(0).getString("USER_ID_A");
		IDataset mfcinfo =MfcCommonUtil.getSEL_USER_ROLEA(userIdA,"1",relationTypeCode,null);
			if(IDataUtil.isNotEmpty(mfcinfo)){
				custPhone = mfcinfo.getData(0).getString("SERIAL_NUMBER_B");
				result.put("CUSTOMER_PHONE", custPhone);
				ret.add(result);

		}
	}
	if(IDataUtil.isEmpty(ret)){
		 CSAppException.apperr(CrmCommException.CRM_COMM_103,mebNumber+"所对应的主号不存在！");	
	}	
	return ret;
}

public IDataset bossInfo(IData input) throws Exception{
	
	/**
	 * 入参检查
	 */

	IDataUtil.chkParam(input, "BUSINESS_TYPE");//业务类型
	IDataUtil.chkParam(input, "BIZ_VERSION");//业务版本
	
	IData result =new DataMap(); //定义一个返回入参
	IData param =new DataMap();//调一级Boss的入参
	IDataset extendinfo =new DatasetList();
	param.put("BUSINESS_TYPE", input.getString("BUSINESS_TYPE"));
	param.put("PRODUCT_CODE", input.getString("PRODUCT_CODE"));//新增请求参数产品编码
	param.put("PRODUCT_OFFERING_ID", input.getString("PRODUCT_OFFERING_ID",""));
	param.put("CUSTOMER_PHONE", input.getString("CUSTOMER_PHONE",""));
	param.put("MEM_TYPE", input.getString("MEM_TYPE",""));
	param.put("MEM_AREA_CODE", input.getString("MEM_AREA_CODE",""));
	param.put("MEM_NUMBER", input.getString("MEM_NUMBER",""));
	param.put("EXTEND_INFO",extendinfo);
	param.put("BIZ_VERSION", input.getString("BIZ_VERSION"));
	param.put("KIND_ID", "MFCMemQuery_BBOSS_0_0");	
    IDataset rets= IBossCall.dealInvokeUrl("MFCMemQuery_BBOSS_0_0","IBOSS6", param); 
    if(IDataUtil.isEmpty(rets)){
		 CSAppException.apperr(CrmCommException.CRM_COMM_103,"查询无结果！");	
	}	
	
   return rets;
    
 
}

/**
 * 省BOSS查询BBOSS实时接口
 * @param input
 * @return
 * @throws Exception
 */
public IDataset bossRealTimeInfo(IData input) throws Exception {
	IData param =new DataMap();//调一级Boss的入参
	IDataset extendinfo =new DatasetList();
	param.put("BUSINESS_TYPE", input.getString("BUSINESS_TYPE"));
	param.put("PRODUCT_CODE", input.getString("PRODUCT_CODE",""));//新增请求参数产品编码
	param.put("PRODUCT_OFFERING_ID", input.getString("PRODUCT_OFFERING_ID",""));
	param.put("CUSTOMER_PHONE", input.getString("CUSTOMER_PHONE",""));
	param.put("MEM_TYPE", input.getString("MEM_TYPE",""));
	param.put("MEM_AREA_CODE", input.getString("MEM_AREA_CODE",""));
	param.put("MEM_NUMBER", input.getString("MEM_NUMBER",""));
	param.put("EXTEND_INFO",extendinfo);
	param.put("BIZ_VERSION", input.getString("BIZ_VERSION"));
	param.put("KIND_ID", "MFCMemQueryReal_BBOSS_0_0");	
	IDataset rets= IBossCall.dealInvokeUrl("MFCMemQueryReal_BBOSS_0_0","IBOSS6", param);
	if(IDataUtil.isEmpty(rets)){
		 CSAppException.apperr(CrmCommException.CRM_COMM_103,"网络异常！");	
	}	
	if(log.isDebugEnabled()){
		 log.debug("一级boss返回的值"+rets);
	}
    return rets;
}

/**
 * 省BOSS或其他系统查询订单状态接口
 * @param input
 * @return
 * @throws Exception
 */
	public IData familyOrderStatusQuery(IData input) throws Exception {
		/**
		 * 入参检查
		 */
		IDataUtil.chkParam(input, "BUSINESS_TYPE");// 业务类型
		IDataUtil.chkParam(input, "CUSTOMER_PHONE");
		IDataUtil.chkParam(input, "PO_ORDER_NUMBER");
		IDataUtil.chkParam(input, "BIZ_VERSION");// 业务版本
		IData result = new DataMap(); // 定义一个返回入参
		IData param = new DataMap();// 调一级Boss的入参
		IDataset extendinfo = new DatasetList();
		param.put("BUSINESS_TYPE", input.getString("BUSINESS_TYPE"));
		param.put("CUSTOMER_PHONE", input.getString("CUSTOMER_PHONE"));
		param.put("PO_ORDER_NUMBER", input.getString("PO_ORDER_NUMBER"));
		param.put("MEM_ORDER_NUMBER", input.getString("MEM_ORDER_NUMBER", ""));
		param.put("EXTEND_INFO", extendinfo);
		param.put("BIZ_VERSION", input.getString("BIZ_VERSION"));
		param.put("KIND_ID", "MFCOrderQuery_BBOSS_0_0");
		IDataset rets= IBossCall.dealInvokeUrl("MFCOrderQuery_BBOSS_0_0","IBOSS6", param);		
		if (IDataUtil.isNotEmpty(rets)) {
			if ("00".equals(rets.getData(0).getString("RSP_CODE"))) {
				result.put("RSP_CODE", "00");
				result.put("RSP_DESC", "查询成功");
				// 得到订单信息
				IDataset orderlists = rets.getData(0).getDataset("ORDER_LIST");
				// 拼凑页面显示的数据
				IDataset orderlist = new DatasetList();
				if (IDataUtil.isNotEmpty(orderlists)) {
					for (int i = 0; i < orderlists.size(); i++) {
						IDataset idata = rets.getData(0).getDataset("ORDER_DETAIL");
						if (IDataUtil.isNotEmpty(idata)) {
							for (int j = 0; j < idata.size(); j++) {
								IData res = new DataMap();
								res.put("PO_ORDER_NUMBER", orderlists.getData(i).getString("PO_ORDER_NUMBER"));
								res.put("MEM_ORDER_NUMBER",orderlists.getData(i).getString("MEM_ORDER_NUMBER"));
								String orderstatus = orderlists.getData(i).getString("ORDER_STATUS");
								if ("00".equals(orderstatus)) {
									res.put("ORDER_STATUS", "成功");
								} else if ("02".equals(orderstatus)) {
									res.put("ORDER_STATUS", "进行中");
								} else {
									res.put("ORDER_STATUS", "失败");
								}
								res.put("ORDER_STATUS_DESC",orderlists.getData(i).getString("ORDER_STATUS_DESC"));
								res.put("PROCESSOR", orderlists.getData(i).getString("PROCESSOR"));
								// 到达时间处理
								res.put("RECEIVE_TIME", orderlists.getData(i).getString("RECEIVE_TIME"));
								// 订单轨迹详情数据
								res.put("NODE_SEQ",idata.getData(j).getString("NODE_SEQ"));
								res.put("NODE_TRANS_ID", idata.getData(j).getString("NODE_TRANS_ID"));
								res.put("NODE_STATUS_DESC", idata.getData(j).getString("NODE_STATUS_DESC"));
								res.put("NODE_PROCESSOR", idata.getData(j).getString("NODE_PROCESSOR"));
								// 环节到达时间处理
								res.put("NODE_RECEIVE_TIME", idata.getData(j).getString("NODE_RECEIVE_TIME"));
								orderlist.add(res);
							}
						}
						else {
							IData res = new DataMap();
							res.put("PO_ORDER_NUMBER", orderlists.getData(i).getString("PO_ORDER_NUMBER"));
							res.put("MEM_ORDER_NUMBER",orderlists.getData(i).getString("MEM_ORDER_NUMBER"));
							String orderstatus = orderlists.getData(i).getString("ORDER_STATUS");
							if ("00".equals(orderstatus)) {
								res.put("ORDER_STATUS", "成功");
							} else if ("02".equals(orderstatus)) {
								res.put("ORDER_STATUS", "进行中");
							} else {
								res.put("ORDER_STATUS", "失败");
							}
							res.put("ORDER_STATUS_DESC",
									orderlists.getData(i).getString(
											"ORDER_STATUS_DESC"));
							res.put("PROCESSOR", orderlists.getData(i).getString("PROCESSOR"));
							// 到达时间处理
							res.put("RECEIVE_TIME", orderlists.getData(i).getString("RECEIVE_TIME"));
							orderlist.add(res);
						}
					}
				}
				result.put("ORDER_LIST", orderlist);
			} else {
				result.put("RSP_CODE", rets.getData(0).getString("RSP_CODE"));
				result.put("RSP_DESC", rets.getData(0).getString("RSP_DESC"));
			}
		} else {
			result.put("RSP_CODE", "99");
			result.put("RSP_DESC", "网络异常");
		}
		log.debug("一级boss返回的值" + result);
		return result;
	}

public IData qryMebList(IData input) throws Exception {

	IData result = new DataMap();
	IDataset meblist = new DatasetList();
	String serialnumber = input.getString("SERIAL_NUMBER");
	String relationTypeCode = "MF";
	IData ucaInfo = UcaInfoQry.qryUserInfoBySn(serialnumber,RouteInfoQry.getEparchyCodeBySn(serialnumber));
	String productCode = input.getString("PRODUCT_CODE");
	if(IDataUtil.isEmpty(ucaInfo)){
		result.put("RSP_CODE", "2999");
		result.put("RSP_DESC", "用户资料不存在或者号码所属外省！");

	} else {

		IDataset uua = MfcCommonUtil.getRelationUusByUserSnRole(serialnumber, relationTypeCode, "", null);

		if (IDataUtil.isNotEmpty(uua)) {
			for (int i = 0; i < uua.size(); i++) {
				String userida = uua.getData(i).getString("USER_ID_A");//虚拟家庭的userid
				String remark = uua.getData(i).getString("REMARK");
				boolean flag = true;
				if(MfcCommonUtil.PRODUCT_CODE_ZF.equals(productCode)){//自付
					if(StringUtils.contains(remark,productCode)){
						flag = false ;
					}
				}else if(MfcCommonUtil.PRODUCT_CODE_5G3.equals(productCode)){//5G家庭会员群组
					if(StringUtils.contains(remark,productCode)){
						flag = false ;
					}
				}else if(MfcCommonUtil.PRODUCT_CODE_5G4.equals(productCode)){//5G家庭套餐群组
					if(StringUtils.contains(remark,productCode)){
						flag = false ;
					}
				}else if(MfcCommonUtil.PRODUCT_CODE_5G5.equals(productCode)){//5G融合套餐群组
					if(StringUtils.contains(remark,productCode)){
						flag = false ;
					}
				}else if(MfcCommonUtil.PRODUCT_CODE_TF6.equals(productCode)){//全国亲情网(支付宝版月包)
					if(StringUtils.contains(remark,productCode)){
						flag = false ;
					}
				}else if(MfcCommonUtil.PRODUCT_CODE_TF7.equals(productCode)){//全国亲情网(支付宝版季包)
					if(StringUtils.contains(remark,productCode)){
						flag = false ;
					}
				}else if(MfcCommonUtil.PRODUCT_CODE_TF8.equals(productCode)){//全国亲情网(支付宝版年包)
					if(StringUtils.contains(remark,productCode)){
						flag = false ;
					}
				}else if(MfcCommonUtil.PRODUCT_CODE_TF9.equals(productCode)){//全国亲情网(异网版月包)
					if(StringUtils.contains(remark,productCode)){
						flag = false ;
					}
				}else if(MfcCommonUtil.PRODUCT_CODE_TF10.equals(productCode)){//全国亲情网(异网版季包)
					if(StringUtils.contains(remark,productCode)){
						flag = false ;
					}
				}else if(MfcCommonUtil.PRODUCT_CODE_TF11.equals(productCode)){//全国亲情网(异网版年包)
					if(StringUtils.contains(remark,productCode)){
						flag = false ;
					}
				}else if(MfcCommonUtil.PRODUCT_CODE_TF.equals(productCode)){//统付
					if(!StringUtils.contains(remark,MfcCommonUtil.PRODUCT_CODE_ZF)){
						flag = false ;
					}
				}
				if(flag){
					continue;
				}
				IDataset uuainfo = MfcCommonUtil.getSEL_USER_ROLEA(userida, null, relationTypeCode, null);//查询该家庭网下的所有成员
				IData rspList = new DataMap();
				IDataset mebList = new DatasetList();
				if (IDataUtil.isNotEmpty(uuainfo)) {
					String productOfferingID = uuainfo.getData(0).getString("RSRV_STR2");//业务订购实例ID  群组编码
					for (int j = 0; j < uuainfo.size(); j++) {
						IData memberUU = uuainfo.getData(j);
						IData mebMap = new DataMap();
						mebMap.put("MEB_NUMBER", memberUU.getString("SERIAL_NUMBER_B"));//成员号码
						//UU关系RSRV_STR1: 1本省  2外省
						mebMap.put("PRV_TYPE", StringUtils.equals("1", memberUU.getString("RSRV_STR1")) ? "01" : "02");//省内省外标识01省内  02省外
						//UU关系ROLE_CODE_B：1主号  2副号
						mebMap.put("NUM_TYPE", memberUU.getString("ROLE_CODE_B"));//主副号码标识 主号码为1，副号码为2
						mebList.add(mebMap);
					}
					rspList.put("PRODUCT_OFFERING_ID", productOfferingID);
					rspList.put("MEB_LIST", mebList);
					meblist.add(rspList);
				}

			}
			result.put("RSP_CODE", "00");
			result.put("RSP_DESC", "成功");
			result.put("RSP_LIST", meblist);
			result.put("OPR_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
		} else {
			result.put("RSP_CODE", "98");
			result.put("RSP_DESC", "该号码未加入任何家庭网");
		}
	}
	return result;
}
	



/**对账接口
 * @param param  SERIAL_NUMBER(主号)  PRODUCT_ORDER_MEMBER(Dataset)
 * @return
 * @throws Exception
 */
public IData checkBill(IData param) throws Exception
 {
		
		IData result = new DataMap();
		result.put("RSP_CODE", "00");
		IData inparam = new DataMap();
		String action = param.getString("ACTION","");
		String svcName = "";
		String custPhone = param.getString("CUSTOMER_PHONE", "");
		//公共参数
		inparam.put("IN_MODE_CODE","6");
		inparam.put("TRADE_CITY_CODE",param.getString("TRADE_CITY_CODE", ""));
		inparam.put("EPARCHY_CODE",param.getString("EPARCHY_CODE", ""));
		
		
		inparam.put("PRODUCT_CODE", param.getString("PRODUCT_CODE", ""));
		inparam.put("PO_ORDER_NUMBER", SeqMgr.getInstId());
		inparam.put("CUSTOMER_PHONE", custPhone);
		inparam.put("ACTION", action); 
		inparam.put("ORDER_TYPE", "2");
		inparam.put("BIZ_VERSION", "1.0.0");
		inparam.put("IS_SEND_TYPE", "1");//0 发短信， 1 不发
		inparam.put("COMPANY_ID", "898");
		if("50".equals(action)){//新增  或 
			checkParam(param);
			IDataset productOrderMem = new DatasetList(param.getString("MEMBER_LIST"));
			if (log.isDebugEnabled())
	      	{
	      		log.debug("============checkBill========param="+param);
	      	}
			//BBOSS文件中包含外省号码，对账表 外省号码已删除。
			Boolean flag = isContinue(param);
			if(flag){
				return result;
			}
			for(int i = 0; i < productOrderMem.size();i++){
				IData in = new DataMap();
				IData productOrderMember = productOrderMem.getData(i);
				in.put("ACTION", param.getString("ACTION"));
				in.put("CUSTOMER_PHONE", param.getString("CUSTOMER_PHONE"));
				in.put("MEM_NUMBER",  productOrderMember.getString("MEM_NUMBER"));
				IDataset syncInfo = qrySyncInfo(in);
				if (log.isDebugEnabled())
    	      	{
    	      		log.debug("============checkBill=====syncInfo="+syncInfo);
    	      	}
				//重复新增
				if(IDataUtil.isNotEmpty(syncInfo) && "50".equals(syncInfo.getData(0).getString("ACTION"))){
		 	      	result.put("RSP_DESC", "重复新增");
	    	      	return result;
				//特殊处理：对于由于时间原因把开机差异当做新增下发的
				}else if(IDataUtil.isNotEmpty(syncInfo) && "53".equals(syncInfo.getData(0).getString("ACTION"))){
					inparam.put("ORDER_SOURCE", "01");//订单来源:01-归属地营业厅02-短信营业厅03-掌上营业厅04-10086热线05-用户销户（包括用户退网、过户等）
	    			inparam.put("OPERATION_SUBTYPE_ID", "03");//00-新增业务订购01-取消业务订购02-主号停机03-主号复开机
	    			inparam.put("PRODUCT_OFFERING_ID", param.getString("PRODUCT_OFFERING_ID",""));
	    			inparam.put("CUSTOMER_TYPE", "1");
	    			if (log.isDebugEnabled())
	    	      	{
	    	      		log.debug("============checkBill=====开机入参="+inparam);
	    	      	}
	    			svcName = "SS.SynMfcUserInfoSVC.synUser";
				}else {
					if(StringUtils.equals(param.getString("CUSTOMER_PHONE"),productOrderMember.getString("MEM_NUMBER"))){
						inparam.put("ORDER_SOURCE", "01");
						inparam.put("OPERATION_SUBTYPE_ID", "00");
						String poidCodeOld =  param.getString("PRODUCT_OFFERING_ID").substring(16);//默认 PRODUCT_OFFERING_ID 后两位
						String poidLableOld = "群"+poidCodeOld;
						inparam.put("POID_CODE", poidCodeOld);
						inparam.put("POID_LABLE", poidLableOld);
						inparam.put("PRODUCT_OFFERING_ID", param.getString("PRODUCT_OFFERING_ID",""));
						svcName = "SS.SynMfcUserInfoSVC.synUser";
					}else{
						productOrderMember.put("MEM_LABLE", productOrderMember.getString("MEM_NUMBER").substring(7));
						inparam.put("PRODUCT_ORDER_MEMBER", new DatasetList(productOrderMember));
						inparam.put("ORDER_SOURCE", "01");
						inparam.put("BUSINESS_TYPE", "1");
						inparam.put("PRODUCT_OFFERING_ID", param.getString("PRODUCT_OFFERING_ID",""));
						svcName = "SS.SynMfcMemInfoSVC.synMeb";
					}
				}
			
			}
			
		}else if("51".equals(action)){//删除
			checkParam(param);
			inparam.put("PRODUCT_ORDER_MEMBER", new DatasetList(param.getString("MEMBER_LIST"))); 
			inparam.put("ORDER_SOURCE", "01");
			inparam.put("BUSINESS_TYPE", "1");
			inparam.put("PRODUCT_OFFERING_ID", param.getString("PRODUCT_OFFERING_ID","")); 
			svcName = "SS.SynMfcMemInfoSVC.synMeb";
		}else if("52".equals(action)){//销户
			inparam.put("ORDER_SOURCE", "05");//订单来源:01-归属地营业厅02-短信营业厅03-掌上营业厅04-10086热线05-用户销户（包括用户退网、过户等）
			inparam.put("OPERATION_SUBTYPE_ID", "01");//00-新增业务订购01-取消业务订购02-主号停机03-主号复开机
			inparam.put("CUSTOMER_TYPE", "1");
			inparam.put("PRODUCT_OFFERING_ID", param.getString("PRODUCT_OFFERING_ID",""));
			svcName = "SS.SynMfcUserInfoSVC.synUser";
		}else if("53".equals(action)){//报停
			//主号UU关系
	        IDataset relaUUDatas = MfcCommonUtil.getRelationUusByUserSnRole(custPhone,"MF","1",null);
	      	//
	      	if (log.isDebugEnabled())
	      	{
	      		log.debug("============checkBill========relaUUDatas="+relaUUDatas);
	      	}
	      	if(IDataUtil.isNotEmpty(relaUUDatas)){
	      		inparam.put("CUSTOMER_TYPE", relaUUDatas.getData(0).getString("SERIAL_NUMBER_A"));
	      		inparam.put("USER_ID_A", relaUUDatas.getData(0).getString("USER_ID_A"));
	      		IData ucaInfo =UcaInfoQry.qryUserInfoBySnForGrp(relaUUDatas.getData(0).getString("SERIAL_NUMBER_A"));
	      		//判断是否已经停机
	      		if(IDataUtil.isNotEmpty(ucaInfo) && !"5".equals(ucaInfo.getString("USER_STATE_CODESET",""))){
	      			inparam.put("ORDER_SOURCE", "01");//订单来源:01-归属地营业厅02-短信营业厅03-掌上营业厅04-10086热线05-用户销户（包括用户退网、过户等）
	    			inparam.put("OPERATION_SUBTYPE_ID", "02");//00-新增业务订购01-取消业务订购02-主号停机03-主号复开机
	    			inparam.put("PRODUCT_OFFERING_ID", param.getString("PRODUCT_OFFERING_ID",""));
	    			inparam.put("CUSTOMER_TYPE", "1");
	    			svcName = "SS.SynMfcUserInfoSVC.synUser";
	      		}else{
	      			result.put("RSP_CODE", "99");
	    	      	result.put("RSP_DESC", "已停机");
	    	      	return result;
	      		}
	      	}else{
	      		result.put("RSP_CODE", "99");
		      	result.put("RSP_DESC", "主号UU关系不存在");
		      	return result;
	      	}
		}


//调成员管理接口
try
{
	if (log.isDebugEnabled())
	{
		log.debug("=============checkBill=========inparam="+inparam);
	}
	
	CSAppCall.call(svcName, inparam);
}
catch(Exception e)
{
	result.put("RSP_CODE", "99");
    result.put("RSP_DESC", e.getMessage());
} 
return result;
}

/**
 * 副号省删除了最后一条副号+外省号码
 * 对账：BBOSS文件中包含外省号码，对账表 外省号码已删除。
 * 则IBoss会下发外省号码的新增成员的差异
 * @param param
 * @return
 * @throws Exception
 */
private Boolean isContinue(IData param) throws Exception {
	Boolean res = true;
	IDataset productOrderMem = new DatasetList(param.getString("MEMBER_LIST"));
	if(IDataUtil.isNotEmpty(productOrderMem)){
		for(int i = 0; i< productOrderMem.size();i++){
			IData productOrderMember = productOrderMem.getData(i);
			if(IDataUtil.isNotEmpty(ResCall.getMphonecodeInfo(productOrderMember.getString("MEM_NUMBER")))){
				res = false;
				return res ;
			}
		}
	}
	String custPhone = param.getString("CUSTOMER_PHONE");
	//通过主号获取UU关系，从而获取家庭网
	IDataset relationUULists = MfcCommonUtil.getRelationUusByUserSnRole(custPhone,"MF","1",null);
	if(IDataUtil.isEmpty(relationUULists)){
		if(!res){
			return false;
		}else{
			return true; 
		}
		 
	}
	//其他副号UU关系
	IDataset relationAll= MfcCommonUtil.getSEL_USER_ROLEA(relationUULists.getData(0).getString("USER_ID_A") , "2","MF",null);
	//UU关系：RSRV_STR1 判断省内 外
	if(IDataUtil.isNotEmpty(relationAll)){
		for(int i = 0; i< relationAll.size();i++){
			IData productOrderMember = relationAll.getData(i);
			if("1".equals(productOrderMember.getString("RSRV_STR1",""))){
				res = false ;
				break;
			}else{
				res = true;
			}
		}
	}
	
	
	return res;
}

private IDataset qrySyncInfo(IData param)throws Exception{
	IData input = new DataMap();
	input.put("ACTION", param.getString("ACTION"));
	input.put("CUSTOMER_PHONE", param.getString("CUSTOMER_PHONE"));
	input.put("MEM_NUMBER",  param.getString("MEM_NUMBER"));
	IDataset membList = Dao.qryByCodeParser("TI_B_MFC_SYNC", "SEL_MFC_BYMEMNUM", input,Route.CONN_CRM_CEN);
	return membList;
}
private void checkParam(IData param) throws Exception {
	IDataUtil.chkParamNoStr(param, "PRODUCT_CODE");// 产品编码
    IDataUtil.chkParamNoStr(param, "PRODUCT_OFFERING_ID");// 业务订购实例ID
    IDataUtil.chkParamNoStr(param, "CUSTOMER_PHONE");// 主号
    IDataUtil.chkParamNoStr(param, "BUSINESS_TYPE");// 业务类型
    
    IDataset MemberList = new DatasetList(param.getString("MEMBER_LIST"));
    if(IDataUtil.isNotEmpty(MemberList)){
    	for(int i = 0;i<MemberList.size();i++){
    		IData mem = MemberList.getData(i);
    		if(IDataUtil.isNotEmpty(MemberList)){
    			IDataUtil.chkParam(mem, "MEM_TYPE");// 成员类型
                IDataUtil.chkParam(mem, "MEM_NUMBER");// 成员号码
    		}else{
    			CSAppException.apperr(CrmCommException.CRM_COMM_103, "711000:接口参数检查，输入参数[MEMBER_LIST]异常！");
    		}
    		
    	}
      }else{
          CSAppException.apperr(CrmCommException.CRM_COMM_103, "711000:接口参数检查，输入参数[MEMBER_LIST]异常！");
      }
	
}

public IDataset queryFileByRSP(IData input) throws Exception {
	  IData param =new DataMap();
	  param.put("QRY_FILE_NAME",input.get("QRY_FILE_NAME"));
	  
	  IDataset result =getRspFileInfo(param);
	  if(IDataUtil.isEmpty(result)){
		  CSAppException.apperr(CrmUserException.CRM_USER_783, "数据还没有刷新，请再试一试！");
	  }
	// TODO Auto-generated method stub
	return result;
}
public static IDataset getRspFileInfo(IData param) throws Exception
{
    SQLParser parser = new SQLParser(param);
    IData inparam =new DataMap();
	inparam.put("QRY_FILE_NAME",param.get("QRY_FILE_NAME") );
    parser.addSQL(" SELECT a.SEQ_ID,a.PRODUCT_CODE,a.PRODUCT_OFFERING_ID,a.ACTION,a.CUSTOMER_PHONE, ");
    parser.addSQL(" a.BUSINESS_TYPE,a.MEM_TYPE,a.MEM_AREA_CODE,a.MEM_NUMBER,a.FINISH_TIME,a.EFF_TIME,");
    parser.addSQL(" a.EXP_TIME,a.OPR_TIME,a.RSRV_STR1,a.RSRV_STR2,a.RSRV_STR3,a.RSRV_STR4,a.RSRV_STR5");
    parser.addSQL(" FROM TI_B_MFCQRY_SYNC  a  WHERE 1=1");
    parser.addSQL(" AND QRY_FILE_NAME = :QRY_FILE_NAME");
    parser.addSQL(" AND a.PRODUCT_OFFERING_ID = :a.PRODUCT_OFFERING_ID");
    return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
}
	/**
	 * 查询个人业务成员数据实时返回的接口
	 * @param input
	 * @return IDataset
	 * @author liuzhen6
	 * @throws Exception
	 */
	public  IData getMfcInfoForBBOSS(IData input) throws Exception{
	   IData result = new DataMap();
	   //校验必填参数
	   checkPramByKeys(input,"BUSINESS_TYPE,MEM_TYPE,BIZ_VERSION");	 
	   result.put("RSP_CODE", "00");//返回码
	   result.put("RSP_DESC", "成功");//描述
	   
	   IDataset rspResult = new DatasetList();//查询结果
	   
	   //校验业务类型
	   if(!("1".equals(input.getString("BUSINESS_TYPE"))||"2".equals(input.getString("BUSINESS_TYPE")))) {
		   result.put("RSP_CODE", "01");//返回码
		   result.put("RSP_DESC", "业务类型错误");//描述
		   return result;
	   }
	   //校验-业务订购实例ID、客户标识、成员号码至少有一个是必填
	   if(StringUtils.isEmpty(input.getString("PRODUCT_OFFERING_ID"))&&
			   StringUtils.isEmpty(input.getString("CUSTOMER_PHONE"))&&
			   StringUtils.isEmpty(input.getString("MEM_NUMBER"))){
		   result.put("RSP_CODE", "99");//返回码
		   result.put("RSP_DESC", "业务订购实例ID、客户标识、成员号码至少有一个填写");//描述
		   return result;
	   }
	   //校验成员类型
	   if(!("1".equals(input.getString("MEM_TYPE"))||"2".equals(input.getString("MEM_TYPE")))) {
		   result.put("RSP_CODE", "04");//返回码
		   result.put("RSP_DESC", "成员类型错误");//描述
		   return result;
	   }
	   
	   Set<String> proOffIdSet = new HashSet<String>();
	   //查询成员列表
	   IData listParam = new DataMap();
	   listParam.put("RSRV_STR2", input.getString("PRODUCT_OFFERING_ID"));
	   if(StringUtils.isNotEmpty(input.getString("CUSTOMER_PHONE"))){
		   listParam.put("SERIAL_NUMBER_B", input.getString("CUSTOMER_PHONE"));
		   listParam.put("ROLE_CODE_B", "1");
	   }else {
		   listParam.put("SERIAL_NUMBER_B", input.getString("MEM_NUMBER"));
		   listParam.put("ROLE_CODE_B", "2");
	   }
	   IDataset resultList= Dao.qryByCodeParserAllCrm("TF_F_RELATION_UU", "SEL_MFC_BYPMNUM", listParam,null, true);//查询数据库
	   if(IDataUtil.isNotEmpty(resultList)){
		   if(resultList.size()>100) {
			   result.put("RSP_CODE", "07");//返回码
			   result.put("RSP_DESC", "成员超过100个请调用MFCMemQuery服务查询");//描述
			   return result;
		   }
		   for(int i=0;i<resultList.size();i++) {
			   if(StringUtils.isNotBlank(resultList.getData(i).getString("RSRV_STR2"))){
				   if(StringUtils.isBlank(input.getString("PRODUCT_CODE"))){
					   proOffIdSet.add(resultList.getData(i).getString("RSRV_STR2"));//得到每一个家庭的标识
				   }
				   if("MFC000002".equals(input.getString("PRODUCT_CODE"))
						   &&StringUtils.contains(resultList.getData(i).getString("REMARK"), "MFC000002")){
					   proOffIdSet.add(resultList.getData(i).getString("RSRV_STR2"));//得到每一个家庭的标识
				   }
				   
				   if("MFC000001".equals(input.getString("PRODUCT_CODE"))
						   &&!(StringUtils.contains(resultList.getData(i).getString("REMARK"), "MFC000002"))){
					   proOffIdSet.add(resultList.getData(i).getString("RSRV_STR2"));//得到每一个家庭的标识
				   }

				   if("MFC000003".equals(input.getString("PRODUCT_CODE"))
						   &&!(StringUtils.contains(resultList.getData(i).getString("REMARK"), "MFC000002"))){
					   proOffIdSet.add(resultList.getData(i).getString("RSRV_STR2"));//得到每一个家庭的标识
				   }

				   if("MFC000004".equals(input.getString("PRODUCT_CODE"))
						   &&!(StringUtils.contains(resultList.getData(i).getString("REMARK"), "MFC000002"))){
					   proOffIdSet.add(resultList.getData(i).getString("RSRV_STR2"));//得到每一个家庭的标识
				   }

				   if("MFC000005".equals(input.getString("PRODUCT_CODE"))
						   &&!(StringUtils.contains(resultList.getData(i).getString("REMARK"), "MFC000002"))){
					   proOffIdSet.add(resultList.getData(i).getString("RSRV_STR2"));//得到每一个家庭的标识
				   }
				   
				   if("MFC000006".equals(input.getString("PRODUCT_CODE"))
						   &&!(StringUtils.contains(resultList.getData(i).getString("REMARK"), "MFC000006"))){
					   proOffIdSet.add(resultList.getData(i).getString("RSRV_STR2"));//得到每一个家庭的标识
				   }

				   if("MFC000007".equals(input.getString("PRODUCT_CODE"))
						   &&!(StringUtils.contains(resultList.getData(i).getString("REMARK"), "MFC000007"))){
					   proOffIdSet.add(resultList.getData(i).getString("RSRV_STR2"));//得到每一个家庭的标识
				   }

				   if("MFC000008".equals(input.getString("PRODUCT_CODE"))
						   &&!(StringUtils.contains(resultList.getData(i).getString("REMARK"), "MFC000008"))){
					   proOffIdSet.add(resultList.getData(i).getString("RSRV_STR2"));//得到每一个家庭的标识
				   }
				   
				   if("MFC000009".equals(input.getString("PRODUCT_CODE"))
						   &&!(StringUtils.contains(resultList.getData(i).getString("REMARK"), "MFC000009"))){
					   proOffIdSet.add(resultList.getData(i).getString("RSRV_STR2"));//得到每一个家庭的标识
				   }

				   if("MFC000010".equals(input.getString("PRODUCT_CODE"))
						   &&!(StringUtils.contains(resultList.getData(i).getString("REMARK"), "MFC000010"))){
					   proOffIdSet.add(resultList.getData(i).getString("RSRV_STR2"));//得到每一个家庭的标识
				   }

				   if("MFC000011".equals(input.getString("PRODUCT_CODE"))
						   &&!(StringUtils.contains(resultList.getData(i).getString("REMARK"), "MFC000011"))){
					   proOffIdSet.add(resultList.getData(i).getString("RSRV_STR2"));//得到每一个家庭的标识
				   }
			   }
		   }
		   if(!proOffIdSet.isEmpty()){
			   for (Iterator<String> iter=proOffIdSet.iterator(); iter.hasNext();){
				   String pro_off_id=iter.next();
				   IData rspRes = new DataMap();//每一个家庭
				   rspRes.put("PRODUCT_OFFERING_ID", pro_off_id);
				   IDataset memberList = new DatasetList();//成员列表
				   for(int i=0;i<resultList.size();i++) {
					   if(pro_off_id.equals(resultList.getData(i).getString("RSRV_STR2"))){
						   if(StringUtils.contains(resultList.getData(i).getString("REMARK"), "MFC000002")){
							   rspRes.put("PRODUCT_CODE", "MFC000002");
						   }else if(StringUtils.contains(resultList.getData(i).getString("REMARK"), "MFC000003"))
						   {
							   rspRes.put("PRODUCT_CODE", "MFC000003");
						   }else if(StringUtils.contains(resultList.getData(i).getString("REMARK"), "MFC000004"))
						   {
							   rspRes.put("PRODUCT_CODE", "MFC000004");
						   }else if(StringUtils.contains(resultList.getData(i).getString("REMARK"), "MFC000005"))
						   {
							   rspRes.put("PRODUCT_CODE", "MFC000005");
						   }else if(StringUtils.contains(resultList.getData(i).getString("REMARK"), "MFC000006"))
						   {
							   rspRes.put("PRODUCT_CODE", "MFC000006");
						   }else if(StringUtils.contains(resultList.getData(i).getString("REMARK"), "MFC000007"))
						   {
							   rspRes.put("PRODUCT_CODE", "MFC000007");
						   }else if(StringUtils.contains(resultList.getData(i).getString("REMARK"), "MFC000008"))
						   {
							   rspRes.put("PRODUCT_CODE", "MFC000008");
						   }else if(StringUtils.contains(resultList.getData(i).getString("REMARK"), "MFC000009"))
						   {
							   rspRes.put("PRODUCT_CODE", "MFC000009");
						   }else if(StringUtils.contains(resultList.getData(i).getString("REMARK"), "MFC000010"))
						   {
							   rspRes.put("PRODUCT_CODE", "MFC000010");
						   }else if(StringUtils.contains(resultList.getData(i).getString("REMARK"), "MFC000011"))
						   {
							   rspRes.put("PRODUCT_CODE", "MFC000011");
						   }else{
							   rspRes.put("PRODUCT_CODE", "MFC000001");
						   }
						   IData memdata =new DataMap();
						   memdata.put("MEN_TYPE", resultList.getData(i).getString("RSRV_STR3"));//成员类型
						   memdata.put("MEM_AREA_CODE", resultList.getData(i).getString("RSRV_STR4"));//成员区号
						   memdata.put("MEM_FLAG", resultList.getData(i).getString("ROLE_CODE_B"));//成员标识
						   if("1".equals(resultList.getData(i).getString("ROLE_CODE_B"))) {
							   rspRes.put("CUSTOMER_PHONE", resultList.getData(i).getString("SERIAL_NUMBER_B"));
							   memdata.put("MEM_NUMBER", resultList.getData(i).getString("SERIAL_NUMBER_B"));//成员号码
						   }else if("2".equals(resultList.getData(i).getString("ROLE_CODE_B"))) {
							   memdata.put("MEM_NUMBER", resultList.getData(i).getString("SERIAL_NUMBER_B"));//成员号码
						   }
						   memdata.put("EFF_TIME", resultList.getData(i).getString("START_DATE"));//生效时间
						   memdata.put("EXP_TIME", resultList.getData(i).getString("END_DATE"));//失效时间
						   memdata.put("NOTES", "");//备注
						   memberList.add(memdata);
					   }
				   }
				   rspRes.put("MEMBER_LIST", memberList);
				   rspResult.add(rspRes);//将家庭信息添加到查询结果中
			   }
		   }
	   }
	   //在途成员列表
	   IData onParam = new DataMap();
	   onParam.put("BUSINESS_TYPE", input.getString("BUSINESS_TYPE"));
	   onParam.put("MEM_TYPE", input.getString("MEM_TYPE"));
	   onParam.put("BIZ_VERSION", input.getString("BIZ_VERSION"));
	   onParam.put("RSRV_STR2", input.getString("PRODUCT_OFFERING_ID"));
	   onParam.put("CUSTOMER_PHONE", input.getString("CUSTOMER_PHONE"));
	   onParam.put("MEM_NUMBER", input.getString("MEM_NUMBER"));
	   IDataset resultTranList= Dao.qryByCodeParser("TF_B_FAMILY_LOG", "SEL_MFC_BYONROAD", onParam,Route.CONN_CRM_CEN); //查询数据库
	   if(IDataUtil.isNotEmpty(resultTranList)) {
		   if(resultTranList.size()>100) {
			   result.put("RSP_CODE", "07");//返回码
			   result.put("RSP_DESC", "成员超过100个请调用MFCMemQuery服务查询");//描述
			   return result;
		   }
		   if(IDataUtil.isNotEmpty(rspResult)){
			   for(int i=0;i<rspResult.size();i++){//循环每一个家庭，将在途成员信息添加进去
				   String pro_off_id = rspResult.getData(i).getString("PRODUCT_OFFERING_ID");
				   IDataset tranMemberList = new DatasetList();//在途成员列表
				   for(int j=0;j<resultTranList.size();j++) {
					   if(pro_off_id.equals(resultTranList.getData(j).getString("RSRV_STR2"))){
						   IData tempTrandata =new DataMap();
						   //成员标识
						   if(StringUtils.isEmpty(resultTranList.getData(j).getString("MEM_NUMBER"))) {
							   tempTrandata.put("MEM_FLAG", "1");
						   }else{
							   tempTrandata.put("MEM_FLAG", "2");
						   }
						   tempTrandata.put("MEM_TYPE", resultTranList.getData(j).getString("MEM_TYPE"));//成员类型
						   tempTrandata.put("MEM_AREA_CODE", resultTranList.getData(j).getString("MEM_AREA_CODE"));//成员区号
						   tempTrandata.put("MEM_NUMBER", resultTranList.getData(j).getString("MEM_NUMBER"));//成员号码
						   tempTrandata.put("NOTES", "");//备注
						   tranMemberList.add(tempTrandata);
					   }
				   }
				   rspResult.getData(i).put("MEM_TRAN_LIST", tranMemberList);
			   }
		   }
	   }
	   if(IDataUtil.isNotEmpty(resultList)&&IDataUtil.isNotEmpty(resultTranList)){
		   if(resultList.size()+resultTranList.size()>100) {
			   result.put("RSP_CODE", "07");//返回码
			   result.put("RSP_DESC", "成员超过100个请调用MFCMemQuery服务查询");//描述
			   return result;
		   }
	   }
	   //新增反馈结果
	   if(IDataUtil.isEmpty(resultList)&&IDataUtil.isEmpty(resultTranList)){
		   result.put("RSP_CODE", "08");//返回码
		   result.put("RSP_DESC", "根据查询条件找不到查询结果");//描述
		   return result;
	   }
	   result.put("RSP_RESULT", rspResult);
	   return result;
	}
	public static IDataset getSEL_USER_ROLE(String serialNumberB,  String relationTypeCode,String userIdA) throws Exception {
		IData inparam = new DataMap();
		inparam.put("SERIAL_NUMBER_B", serialNumberB);
		inparam.put("RELATION_TYPE_CODE", relationTypeCode);
		inparam.put("USER_ID_A", userIdA);
		return Dao.qryByCodeParserAllCrm("TF_F_RELATION_UU", "SEL_BY_SNAMFC", inparam,null, true);
	}
	public static IDataset getSEL_SERIAL_NUMBERA(String serialNumberA,  String relationTypeCode) throws Exception {
		IData inparam = new DataMap();
		inparam.put("SERIAL_NUMBER_A", serialNumberA);
		inparam.put("RELATION_TYPE_CODE", relationTypeCode);
		return Dao.qryByCodeParserAllCrm("TF_F_RELATION_UU", "SEL_BY_SNA", inparam,null, true);
	}
	public IDataset loadMebInfo(IData input) throws Exception {
		String sn_b =input.getString("SERIAL_NUMBER_B");
		String userIdA=input.getString("USER_ID_A");
		String role = input.getString("ROLE_CODE_B");
		IDataset uua =MfcCommonUtil.getSEL_USER_ROLEA(userIdA,"","MF",null);
		IDataset uuInfoLists=new DatasetList();
		String mainNumber="";
		if(IDataUtil.isNotEmpty(uua)){
		    for(int a=0;a<uua.size();a++){
		    	if("1".equals(uua.getData(a).getString("ROLE_CODE_B"))){
		    		mainNumber=uua.getData(a).getString("SERIAL_NUMBER_B");
		    		break;
		    	}
		    }
			for(int i=0;i<uua.size();i++){
				IData uuMap=uua.getData(i);
				//查询号码是主号并且查出的主号列表
				if("主号".equals(role)&&"2".equals(uua.getData(i).getString("ROLE_CODE_B"))){
					String memLable =uua.getData(i).getString("SERIAL_NUMBER_B").substring(7);			
					IDataset otherInfo =MfcCommonUtil.getPoidCodeBymemnumber(uua.getData(i).getString("RSRV_STR2"),uua.getData(i).getString("SERIAL_NUMBER_B"));
					if(IDataUtil.isEmpty(otherInfo)){
						uuMap.put("MEM_LABLE", memLable);
					}else{
						uuMap.put("MEM_LABLE",otherInfo.getData(0).getString("MEM_LABLE") );
					}
					uuMap.put("MAIN_NUMBER", mainNumber);
					uuInfoLists.add(uuMap);                                                                                                                                
				}else if("副号".equals(role)&&sn_b.equals(uua.getData(i).getString("SERIAL_NUMBER_B"))){
					String memLable =uua.getData(i).getString("SERIAL_NUMBER_B").substring(7);			
					IDataset otherInfo =MfcCommonUtil.getPoidCodeBymemnumber(uua.getData(i).getString("RSRV_STR2"),uua.getData(i).getString("SERIAL_NUMBER_B"));
					if(IDataUtil.isEmpty(otherInfo)){
						uuMap.put("MEM_LABLE", memLable);
					}else{
						uuMap.put("MEM_LABLE",otherInfo.getData(0).getString("MEM_LABLE") );
					}
					uuMap.put("MAIN_NUMBER", mainNumber);
					uuInfoLists.add(uuMap);
				}
			}
		}
		return uuInfoLists;
		
	}
	

}

