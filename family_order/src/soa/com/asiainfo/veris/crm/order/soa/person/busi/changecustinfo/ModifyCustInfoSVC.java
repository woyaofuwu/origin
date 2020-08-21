
package com.asiainfo.veris.crm.order.soa.person.busi.changecustinfo;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CustException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.SccCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustomerInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserIdentInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleActiveInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.passwdmgr.PasswdAssistant;
import com.asiainfo.veris.crm.order.soa.person.busi.queryuser.queryNewCardIdUserBean;

public class ModifyCustInfoSVC extends CSBizService
{

    protected static Logger log = Logger.getLogger(ModifyCustInfoSVC.class);

    private static final long serialVersionUID = 1L;

    /**
     * 获取客户实名制预受理信息与构机信息
     * 
     * @param custInfo
     * @param userId
     * @param serialNumber
     * @return
     * @throws Exception
     */
    private IData getCustCommInfo(IData custInfo, String userId, String serialNumber) throws Exception
    {
        IData custCommInfo = new DataMap();
        // 查询用户实名制预登记信息
        IDataset dataset = UserOtherInfoQry.getUserOtherUserId(userId, "REAL", null);
        if (IDataUtil.isNotEmpty(dataset))
        {
            // 如果实名制预受理时 没有填写相应的资料 则用 客户原来的资料代替
            IData data = dataset.getData(0);
            if (StringUtils.isNotBlank(data.getString("RSRV_STR2")))
            {
                custCommInfo.put("REAL_CUST_NAME", data.getString("RSRV_STR2"));
            }
            else
            {
                custCommInfo.put("REAL_CUST_NAME", custInfo.getString("CUST_NAME"));
            }

            if (StringUtils.isNotBlank(data.getString("RSRV_STR3")))
            {
                custCommInfo.put("REAL_PSPT_TYPE_CODE", data.getString("RSRV_STR3"));
            }
            else
            {
                custCommInfo.put("REAL_PSPT_TYPE_CODE", custInfo.getString("PSPT_TYPE_CODE"));
            }

            if (StringUtils.isNotBlank(data.getString("RSRV_STR4")))
            {
                custCommInfo.put("REAL_PSPT_ID", data.getString("RSRV_STR4"));
            }
            else
            {
                custCommInfo.put("REAL_PSPT_ID", custInfo.getString("PSPT_ID"));
            }

            if (StringUtils.isNotBlank(data.getString("RSRV_STR5")))
            {
                custCommInfo.put("REAL_PSPT_ADDR", data.getString("RSRV_STR5"));
            }
            else
            {
                custCommInfo.put("REAL_PSPT_ADDR", custInfo.getString("PSPT_ADDR"));
            }

            if (StringUtils.isNotBlank(data.getString("RSRV_STR6")))
            {
                custCommInfo.put("REAL_PHONE", data.getString("RSRV_STR6"));
            }
            else
            {
                custCommInfo.put("REAL_PHONE", custInfo.getString("PHONE"));
            }
            custCommInfo.put("REAL_REG", "1");// 存在实名制预登记记录
        }
        else
        {
            custCommInfo.put("REAL_CUST_NAME", "");
            custCommInfo.put("REAL_PSPT_TYPE_CODE", "");
            custCommInfo.put("REAL_PSPT_ID", "");
            custCommInfo.put("REAL_PSPT_ADDR", "");
            custCommInfo.put("REAL_PHONE", "");
            custCommInfo.put("REAL_REG", "0");// 不存在实名制预登记记录
        }
        // 根据用户标识USER_ID查询用户购机信息
        IDataset purchaseDataset = UserSaleActiveInfoQry.getPurchaseInfoByUserId(userId, "0", null);
        if (!purchaseDataset.isEmpty())
        {
            custCommInfo.put("IS_IN_PURCHASE", "1");// 用做前台判断，如果用户还处在营销活动期限内，则不能够修改客户名称
        }
        else
        {
            custCommInfo.put("IS_IN_PURCHASE", "0");
        }
        // 用户实名制用TF_F_CUSTOMER表中的IS_REAL_NAME字段来进行判断
        if ("1".equals(custInfo.getString("IS_REAL_NAME", "")))
        {
            custCommInfo.put("REAL_NAME", "true");
        }
        else
        {
            custCommInfo.put("REAL_NAME", "");
        }
        // 是否有特殊资料修改权限
        if (StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "MODIFYSPECUSTINFO"))
        {
            custCommInfo.put("STAFF_SPECIAL_RIGTH", "true");
        }
        else
        {
            custCommInfo.put("STAFF_SPECIAL_RIGTH", "false");
        }
        // 如果员工拥有特殊权限，且用户为实名制，则记录查询日志。
        if ("true".equals(custCommInfo.getString("REAL_NAME")) && "true".equals(custCommInfo.getString("STAFF_SPECIAL_RIGTH")))
        {
            IData params = new DataMap();
            params.put("USER_ID", userId);
            params.put("SERIAL_NUMBER", serialNumber);
            params.put("REMARK", "记录特殊权限工号查询实名制用户资料！");
            writeLanuchLog(params);
        }
        return custCommInfo;
    }

    /**
     * 查询客户相关资料信息
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public IData getCustInfo(IData param) throws Exception
    {
        IData resultData = new DataMap();
        String custId = param.getString("CUST_ID");
        IData custInfo = new DataMap();
        IData personData = UcaInfoQry.qryPerInfoByCustId(custId);
        String remark = personData.getString("REMARK","");
        custInfo.put("USE", personData.getString("RSRV_STR5",""));
        custInfo.put("USE_PSPT_TYPE_CODE", personData.getString("RSRV_STR6",""));
        custInfo.put("USE_PSPT_ID", personData.getString("RSRV_STR7",""));
        custInfo.put("USE_PSPT_ADDR", personData.getString("RSRV_STR8",""));
        
        custInfo.put("PASS_NUMBER", personData.getString("RSRV_STR3",""));//港澳居住证通行证号码
        custInfo.put("LSSUE_NUMBER", personData.getString("RSRV_STR4",""));//港澳居住证签证次数
        
        if (IDataUtil.isEmpty(personData))
        {
            CSAppException.apperr(CustException.CRM_CUST_69);// 获取个人客户资料无数据!(考虑资料未全异常资料的情况下)
        }

        custInfo.putAll(personData);
        // 查个人客户
        custInfo.putAll(UcaInfoQry.qryCustomerInfoByCustId(custId));

        // 如果客户的证件号码有效期为空，则置为SysDateMgr.END_DATE_FOREVER
        String psptEndDate = custInfo.getString("PSPT_END_DATE", "");
        if ("".equals(psptEndDate))
        {
            custInfo.put("PSPT_END_DATE", SysDateMgr.END_DATE_FOREVER);
        }
        IData custCommInf = getCustCommInfo(custInfo, param.getString("USER_ID"), param.getString("SERIAL_NUMBER"));
        custInfo.put("REMARK", remark);
        resultData.put("COMM_INFO", custCommInf);
        resultData.put("CUST_INFO", custInfo);
        return resultData;

    }

    /**
     * 记录特殊权限工号查询实名制用户资料
     * 
     * @param params
     * @return
     * @throws Exception
     */
    public IData writeLanuchLog(IData params) throws Exception
    {
        UcaData ucadata = UcaDataFactory.getNormalUca(params.getString("SERIAL_NUMBER"));

        String eparchyCode = CSBizBean.getTradeEparchyCode();
        String systime = SysDateMgr.getSysTime();
        String tradeId = SeqMgr.getTradeId();
        IData inparam = new DataMap();
        String netTypeCode = ucadata.getUser().getNetTypeCode();
        if (StringUtils.isBlank(netTypeCode))
            netTypeCode = "00";

        inparam.put("TRADE_ID", tradeId);// 业务流水号
        inparam.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(tradeId));// 受理月份：受理时间的月份，可作为分区字段。正常情况下可从trade_id的第5、6位获得。
        inparam.put("BATCH_ID", "0");
        inparam.put("ORDER_ID", SeqMgr.getOrderId());
        inparam.put("CAMPN_ID", "0");
        inparam.put("TRADE_TYPE_CODE", "2101");// 业务类型编码：见参数表TD_S_TRADETYPE
        inparam.put("PRIORITY", "0");// 优先级：值越大越优先（同一用户间以受理时间先后为准）
        inparam.put("SUBSCRIBE_TYPE", "0");// 定单类型：0-普通立即执行，1-普通预约执行，100-批量立即执行，101-批量预约执行，200-信控执行
        inparam.put("SUBSCRIBE_STATE", "0");
        inparam.put("NEXT_DEAL_TAG", "0");
        inparam.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());
        inparam.put("CUST_ID", ucadata.getCustId());
        inparam.put("CUST_NAME", ucadata.getCustomer().getCustName());
        inparam.put("USER_ID", params.getString("USER_ID"));
        inparam.put("ACCT_ID", ucadata.getAcctId());
        inparam.put("SERIAL_NUMBER", params.getString("SERIAL_NUMBER"));
        inparam.put("NET_TYPE_CODE", netTypeCode);
        inparam.put("EPARCHY_CODE", eparchyCode);
        inparam.put("CITY_CODE", ucadata.getUser().getCityCode());
        inparam.put("PRODUCT_ID", ucadata.getProductId());
        inparam.put("BRAND_CODE", ucadata.getBrandCode());
        inparam.put("ACCEPT_DATE", systime);
        inparam.put("UPDATE_TIME", systime);
        inparam.put("UPDATE_STAFF_ID", getVisit().getStaffId());
        inparam.put("UPDATE_DEPART_ID", getVisit().getDepartId());
        inparam.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        inparam.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        inparam.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
        inparam.put("TRADE_EPARCHY_CODE", eparchyCode);
        inparam.put("OPER_FEE", "0");
        inparam.put("FOREGIFT", "0");
        inparam.put("ADVANCE_PAY", "0");
        inparam.put("PROCESS_TAG_SET", "0");
        inparam.put("OLCOM_TAG", "0");
        inparam.put("FEE_STATE", "0");
        inparam.put("FINISH_DATE", systime);
        inparam.put("EXEC_TIME", systime);
        inparam.put("CANCEL_TAG", "0");
        inparam.put("PF_WAIT", "0");// 是否发开通
        inparam.put("REMARK", params.get("REMARK"));
        Dao.insert("TF_BH_TRADE", inparam,Route.getJourDb(eparchyCode));
        return inparam;
    }
    /*****************************************************************
	 * 移动商城：客户信息修改
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IData modifyCustInfo4ScoreMall(IData input) throws Exception{
		
		IData result = new DataMap();
		result.put("OPR_TIME", SysDateMgr.getSysDate("yyyyMMddHHmmss"));
		
		String serialNumber = input.getString("SERIAL_NUMBER"),
				email = input.getString("EMAIL"), postAddress = input.getString("POST_ADDRESS"),
				postCode = input.getString("POST_CODE"), phone = input.getString("PHONE");
		// 先对身份凭证进行鉴权
		String identCode = input.getString("IDENT_CODE", "");
		String businessCode = input.getString("BUSINESS_CODE", "");
		String identCodeType = input.getString("IDENT_CODE_TYPE", "");
		String identCodeLevel = input.getString("IDENT_CODE_LEVEL", "");
		String userType = input.getString("USER_TYPE", "");
		String upType = input.getString("UPTYPE", "");//移动商城V2.5.4 add by dengyi5 
		String birthDay = input.getString("BIRTHDAY", "").trim();//移动商城V2.5.4 add by dengyi5 
		
		IDataset idents = UserIdentInfoQry.checkIdent(identCode, businessCode, identCodeType, identCodeLevel, serialNumber);
		if (IDataUtil.isEmpty(idents))
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_915);
		}

		if (StringUtils.equals(identCodeType, "02") && StringUtils.isBlank(businessCode))
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_1103);
		}

		SccCall.getUSPRequestInfo(serialNumber, userType, identCode, identCodeType, identCodeLevel, "identAuth");

		
		//1.查询客户信息， 并将客户信息设置到参数中
		IDataset custInfoList = CustomerInfoQry.queryCustInfoBySN(serialNumber);
		if(custInfoList.isEmpty()){
			result.put("X_RESULTCODE", "397");
    		result.put("X_RECORDNUM", "1");
    		result.put("X_RESULTINFO", "获取客户信息无数据！");
    		return result;
		}
		IData custInfo = UcaInfoQry.qryPerInfoByCustId(custInfoList.getData(0).getString("CUST_ID"));
		if(custInfo.isEmpty()){
			result.put("X_RESULTCODE", "396");
    		result.put("X_RECORDNUM", "1");
    		result.put("X_RESULTINFO", "获取客户详细信息无数据！");
    		return result;
		}
		
		custInfo.put("CITY_CODE_A", custInfoList.getData(0).getString("CITY_CODE_A"));
		input.putAll(custInfo);
		//2.将本次修改信息设置到参数中
		if(email != null)
			input.put("EMAIL", email);
		if(postAddress != null)
			input.put("POST_ADDRESS", postAddress);
		if(postCode != null)
			input.put("POST_CODE", postCode);
		if(phone != null)
			input.put("PHONE", phone);
		if("1".equals(upType)){//移动商城V2.5.4 add by dengyi5 
			if(log.isDebugEnabled())
			{
				log.debug("---modifyCustInfo4ScoreMall---input="+input);
				log.debug("---modifyCustInfo4ScoreMall---BIRTHDAY="+birthDay);
			}
			if(StringUtils.isBlank(birthDay))
			{
				result.put("X_RESULTCODE", "-1");
	    		result.put("X_RECORDNUM", "1");
	    		result.put("X_RESULTINFO", "设置生日日期,请输入要修改的信息[生日日期]！");
	    		return result;
			}
			
			//已存在生日信息不允许修改
			IData inParam = new DataMap();
			inParam.put("SERIAL_NUMBER", serialNumber);
			inParam.put("BIRTH_TAG", "1");
			IData resultdata = CSAppCall.call("SS.InterRoamingSVC.roamBirthQry",inParam).getData(0);
			if(StringUtils.isNotBlank(resultdata.getString("BIRTHDAY","")))
			{
				result.put("X_RESULTCODE", "2000");
	    		result.put("X_RECORDNUM", "1");
	    		result.put("X_RESULTINFO", "已存在生日日期，不能修改！");
	    		return result;
			}
			input.put("BIRTHDAY", birthDay);//这里重新赋值，避免被覆盖
		}
		
		//3.登记客户信息
		input.put("SERIAL_NUMBER",serialNumber);
		IDataset dataset = CSAppCall.call("SS.ModifyCustInfoRegSVC.tradeReg", input);
		result.put("X_RESULTCODE", "0");
		result.put("X_RESULTINFO", "TRADE OK！");
		result.put("X_RECORDNUM", "1");
		result.put("TRADE_ID", dataset.getData(0).getString("TRADE_ID"));
    		
		return result;
	}
	
	/**
     * REQ201506020023 证件办理业务触发完善客户信息
     * 捞取TF_B_AUTO_UPD_PSPTID未处理的数据进行调用资料变更服务处理.
     * @param params
     * @return
     * @throws Exception
     */
    public void autoUpdPsptidAddr(IData input) throws Exception{
    	//1、获取TF_B_AUTO_UPD_PSPTID未处理的数据
    	IDataset custs=CustomerInfoQry.queryAutoUpdPsptid(new DataMap());
    	
    	if(custs!=null && custs.size()>0){
    		for(int i=0;i<custs.size();i++){
	    		IData cust=custs.getData(i); 
	    		String autoTag="1";
	            String autoResInfo="SUCCESS";
	            String custid_old=cust.getString("CUST_ID","");
	            String user_id=cust.getString("USER_ID","");
	            String psptId_old=cust.getString("PSPT_ID","");
	            IData idData=new DataMap();
	            /**
	             * 20150928发现“过户”后cust_id改变。
	             * 导致修改的身份证信息有问题
	             * 要求临时表的cust_id与user_id必须同时存在tf_f_user表才允许变更
	             * */
	            idData.put("USER_ID", user_id);
	            idData.put("CUST_ID", custid_old);
	            IDataset userInfo=UserInfoQry.queryUserInfoByUseridAndCustid(idData);
	            if(userInfo!=null && userInfo.size()>0){
		            IData params =params = new DataMap();
		    		try{
			            IDataset custInfos = CSAppCall.call("SS.ModifyCustInfoSVC.getCustInfo", cust);
			            if(custInfos!= null && custInfos.size()>0){
			            	/**
			            	 * 20150928如果用户来变更身份证则不做处理
			            	 * 1、当前证件类型不是0或者1不再处理
			            	 * 2、当前的身份证号码截取15位与临时表的截取15位比对不上，不处理。
			            	 * */
			            	IData custInfoNew=custInfos.getData(0).getData("CUST_INFO");
			            	String psptType_new=custInfoNew.getString("PSPT_TYPE_CODE","");
			            	String psptId_new=custInfoNew.getString("PSPT_ID","");
			            	if("1".equals(psptType_new) || "0".equals(psptType_new)){
			            		String psptId_old_15 = PasswdAssistant.standPsptId(psptType_new, psptId_old);
				            	String psptId_new_15 = PasswdAssistant.standPsptId(psptType_new, psptId_new);
				            	if (StringUtils.equals(psptId_old_15, psptId_new_15))
			        	        {   
				            		params.putAll(custInfoNew); 
					            	params.put("USER_ID", cust.getString("USER_ID"));
						            params.put("SERIAL_NUMBER", cust.getString("SERIAL_NUMBER"));
						            params.put("CHECK_MODE", cust.getString("CHECK_MODE"));// getData()如果没有传入后台，则需要取CHECK_MODE传入，否则认证方式会错乱
						            params.put("TRADE_TYPE_CODE", "60");
						            params.put("REMARK", "系统自动触发完善证件号码或地址信息");
						            params.put("PSPT_ID", cust.getString("PSPT_ID",""));
						          //冼乃捷  工号改成SUPERUSR  depart_id改成36601 业务区都是HNSJ INMODE=SD
						            //CSBizBean.getVisit().setStaffId("ITFSM000");//待确认
						            CSBizBean.getVisit().setStaffId("SUPERUSR");
						            CSBizBean.getVisit().setInModeCode("SD");
						            CSBizBean.getVisit().setDepartId("36601");  
						            CSBizBean.getVisit().setCityCode("HNSJ");
						            params.put("PSPT_ADDR", cust.getString("PSPT_ADDR")); 
					            	//2、调用服务修改，产生工单。
					            	IDataset results = CSAppCall.call("SS.ModifyCustInfoRegSVC.tradeReg", params);
			        	        }else{
			        	        	autoTag="9";
					            	autoResInfo="该用户可能已经变更了证件号码，不做处理！";
			        	        }
			            	}else{
			            		autoTag="9";
				            	autoResInfo="该用户可能已经变更了证件类型，不做处理！";
			            	}
			            }else{
			            	autoTag="9";
			            	autoResInfo="查询客户资料变更SS.ModifyCustInfoSVC.getCustInfo执行失败,未找到数据！";
			            } 		            
		            }catch(Exception e){
		            	autoTag="9";
		            	String errMsg=e.getMessage() ;
		            	 
		            	if(errMsg.length()>0 && errMsg.length()>40){
		            		autoResInfo="资料变更执行失败:"+errMsg.substring(0,40);
		            	}else if(errMsg.length()>0 && errMsg.length()<40){
		            		autoResInfo="资料变更执行失败:"+errMsg.substring(0,errMsg.length());
		            	}else{
		            		autoResInfo="资料变更执行失败!";
		            	}
		            }
		            //3、处理成功后，变更标记
		            StringBuilder sql = new StringBuilder(1000);
		            params.put("AUTO_TAG", autoTag);
		            params.put("AUTO_RESULT_INTO", autoResInfo);
		            params.put("USER_ID", user_id);
		            params.put("CUST_ID", custid_old);
		            
		        	
		        	sql.append(" update TF_B_AUTO_UPD_PSPTID t ");
		        	sql.append(" set t.auto_tag=:AUTO_TAG,T.AUTO_PSPT_TIME=SYSDATE,T.AUTO_RESULT_INTO=:AUTO_RESULT_INTO ");
		        	sql.append(" where T.USER_ID=:USER_ID");
		        	sql.append(" AND T.CUST_ID=:CUST_ID ");
		        	sql.append(" AND T.AUTO_TAG='0' ");
		            Dao.executeUpdate(sql, params);
	    		}else{
	    			StringBuilder sql = new StringBuilder(1000);
	    			autoTag="9";
	    			autoResInfo="该用户的user_id、cust_id已经无法找到user表数据，可能已经过户";
	    			idData.put("AUTO_TAG", autoTag);
	    			idData.put("AUTO_RESULT_INTO", autoResInfo);
		            
		        	
		        	sql.append(" update TF_B_AUTO_UPD_PSPTID t ");
		        	sql.append(" set t.auto_tag=:AUTO_TAG,T.AUTO_PSPT_TIME=SYSDATE,T.AUTO_RESULT_INTO=:AUTO_RESULT_INTO ");
		        	sql.append(" where T.USER_ID=:USER_ID");
		        	sql.append(" AND T.CUST_ID=:CUST_ID ");
		        	sql.append(" AND T.AUTO_TAG='0' ");
		            Dao.executeUpdate(sql, idData);
	    		} 
    		}
    	} 
    }
    
    /**
     * 客户资料变更
     * 选择集团证件（单位证件、营业执照、事业单位法人证书、社会团体法人登记证书、组织机构代码证）用户资料若满足以下条件：
     * tf_f_user_other where user_id=**** and rsrv_value_code='HYYYKBATCHOPEN'
     * 则可以不输入使用人名称、使用人证件类型、使用人证件号码、使用人证件地址。
     * chenxy3 2016-08-18
     * */
    public IDataset checkGroupPsptInfo(IData input) throws Exception{
    	String user_id=input.getString("USER_ID","");
    	String rsrv_value_code="HYYYKBATCHOPEN";
    	return UserOtherInfoQry.getUserOtherByUserRsrvValueCodeByEc(user_id, rsrv_value_code) ;
    }
	public IData SavePsptIdAndPsptAdrr(IData input) throws Exception {
		IData resultMap = new DataMap();
		resultMap.put("0000", "正常");
		IDataset staticInfo = CommparaInfoQry.getCommNetInfo("CSM", "1085", "VERIFY_IDCARD");
		log.error("ModifyCustInfoSVCxxxxxxxxxxxx429 "+staticInfo);
		boolean isVerify = IDataUtil.isNotEmpty(staticInfo) && staticInfo.getData(0).getBoolean("PARA_CODE1");// 是否进行验证, 返回true为验证, false不验证
		if(!isVerify){
			resultMap.put("2999", "证件验证开关【1085】未开启!");
			return resultMap;
		}
		String pspt_id = input.getString("PSPT_ID", "").trim();
		String pspt_addr = input.getString("PSPT_ADDR", "").trim();
		String update_time = SysDateMgr.getSysTime();
		input.put("UPDATE_TIME", update_time);
		if (pspt_id.length() > 0 && pspt_addr.length() > 0) {
			IDataset ds = Dao.qryByCode("TF_F_PSPT_ADDR_INFO", "SEL_BY_PSPTID", input);
			if (ds != null && ds.size() > 0) {
				Dao.update("TF_F_PSPT_ADDR_INFO", input, new String[] { "PSPT_ID" });
			} else {
				Dao.insert("TF_F_PSPT_ADDR_INFO", input);
			}
		} else {
			resultMap.put("2999", "入参不能为空值!");
		}
		return resultMap;
	}	
	public void modifyCustinfoFromVerifyCardOnPWLW(IData input) throws Exception {
		IData cond = new DataMap();
		cond.put("STATUS", "0");// 0 未被获取处理； 1 已被获取处理；
		IDataset ds = Dao.qryByCode("TF_F_PSPT", "SEL_BY_PSPTID_1", cond);
		if (IDataUtil.isNotEmpty(ds)) {
			for (int ij = 0; ij < ds.size(); ij++) {
				IData redata = ds.getData(ij);
				String psptId = redata.getString("PSPT_ID", "").trim();
				String psptTypeCode = redata.getString("PSPT_TYPE_CODE", "").trim();
				String serialNumber = redata.getString("SERIAL_NUMBER", "").trim();
				String name = redata.getString("CUST_NAME", "").trim();
				String remark = redata.getString("REMARK", "").trim();// "资料因物联网卡"+serialNumber+"根据工商联系统及公安系统校验证件结果进行覆盖"
				boolean update = false;
				IData qryData = new DataMap();
				qryData.put("SERIAL_NUMBER", serialNumber);
				IDataset ispwlwds = CSAppCall.call("SS.CreatePersonUserSVC.isPwlwOper", qryData);
				if (ispwlwds != null && ispwlwds.size() > 0) {
					if (ispwlwds.first().getBoolean("FLAG")) {// 是物联网号码
						update = true;
					}
				}
				if (update) {
					IDataset qryCustomerDs = CustomerInfoQry.getCustIdByPspt(psptId);
					List<String> alreadyModUserList = new ArrayList<String>();
					if (qryCustomerDs != null && qryCustomerDs.size() > 0) {
						for (int i = 0; i < qryCustomerDs.size(); i++) {
							String custName = qryCustomerDs.getData(i).getString("CUST_NAME", "").trim();
							String PSPT_TYPE_CODE = qryCustomerDs.getData(i).getString("PSPT_TYPE_CODE", "").trim();
							if (!name.equals(custName) && PSPT_TYPE_CODE.equals(psptTypeCode)) {
								String custid = qryCustomerDs.getData(i).getString("CUST_ID", "").trim();
								IDataset qryUserDs = UserInfoQry.getAllNormalUserInfoByCustId_2(custid);
								if (qryUserDs != null && qryUserDs.size() > 0) {
									for (int j = 0; j < qryUserDs.size(); j++) {
										String other_serialNumber = qryUserDs.getData(j).getString("SERIAL_NUMBER", "").trim();
										String other_userid = qryUserDs.getData(j).getString("USER_ID", "").trim();
										String other_custid = qryUserDs.getData(j).getString("CUST_ID", "").trim();
										if (alreadyModUserList.contains(other_serialNumber)) {
											continue;
										}
										IData data1 = new DataMap();
										data1.put("SERIAL_NUMBER", other_serialNumber);
										data1.put("USER_ID", other_userid);
										data1.put("CUST_ID", other_custid);
										IDataset custInfos = CSAppCall.call("SS.ModifyCustInfoSVC.getCustInfo", data1);
										
								        //营业执照
										IData ENTERPRISEparams = new DataMap();
										ENTERPRISEparams.put("USER_ID", other_userid);
										ENTERPRISEparams.put("RSRV_VALUE_CODE", "ENTERPRISE");
										ENTERPRISEparams.put("SERIAL_NUMBER", other_serialNumber);
								        IDataset datasetENTERPRISE = CSAppCall.call("CS.UserOtherQrySVC.getUserOtherUserId", ENTERPRISEparams);
								        if(datasetENTERPRISE!=null&&datasetENTERPRISE.size()>0){
								            IData data = datasetENTERPRISE.getData(0);
								            custInfos.getData(0).getData("CUST_INFO").put("legalperson", data.getString("RSRV_STR1"));
								            custInfos.getData(0).getData("CUST_INFO").put("startdate", data.getString("RSRV_STR2"));
								            custInfos.getData(0).getData("CUST_INFO").put("termstartdate", data.getString("RSRV_STR3"));
								            custInfos.getData(0).getData("CUST_INFO").put("termenddate", data.getString("RSRV_STR4"));
								        }
								        //组织机构代码证
								        IData ORGparams = new DataMap();
								        ORGparams.put("USER_ID", other_userid);
								        ORGparams.put("RSRV_VALUE_CODE", "ORG");
								        ORGparams.put("SERIAL_NUMBER", other_serialNumber);
								        IDataset datasetORG = CSAppCall.call("CS.UserOtherQrySVC.getUserOtherUserId", ORGparams);
								        if(datasetORG!=null&&datasetORG.size()>0){
								            IData data = datasetORG.getData(0);
								            custInfos.getData(0).getData("CUST_INFO").put("orgtype", data.getString("RSRV_STR1"));
								            custInfos.getData(0).getData("CUST_INFO").put("effectiveDate", data.getString("RSRV_STR2")); 
								            custInfos.getData(0).getData("CUST_INFO").put("expirationDate", data.getString("RSRV_STR3"));
								        }
								        custInfos.getData(0).getData("CUST_INFO").put("AGENT_CUST_NAME",custInfos.getData(0).getData("CUST_INFO").getString("RSRV_STR7",""));
								        custInfos.getData(0).getData("CUST_INFO").put("AGENT_PSPT_TYPE_CODE",custInfos.getData(0).getData("CUST_INFO").getString("RSRV_STR8",""));
								        custInfos.getData(0).getData("CUST_INFO").put("AGENT_PSPT_ID",custInfos.getData(0).getData("CUST_INFO").getString("RSRV_STR9",""));
								        custInfos.getData(0).getData("CUST_INFO").put("AGENT_PSPT_ADDR",custInfos.getData(0).getData("CUST_INFO").getString("RSRV_STR10",""));
										IData params = custInfos.getData(0).getData("CUST_INFO");
										params.put("TRADE_TYPE_CODE", "60");
										params.put("IS_NEED_SMS", false);// 不发送短信
										params.putAll(data1);
										params.put("REMARK", remark);
										params.put("SKIP_RULE", "TRUE");
										params.put("CUST_NAME", name);
										alreadyModUserList.add(other_serialNumber);
										IDataset updateds =  CSAppCall.call("SS.ModifyCustInfoRegSVC.tradeReg", params);
									}
								}
							}
						}
					}
				}
				boolean deleteflag =  Dao.delete("TF_F_PSPT", redata,new String[]{"PSPT_ID", "PSPT_TYPE_CODE","SERIAL_NUMBER","CUST_NAME","STATUS","REMARK"});
				redata.put("STATUS", "1");// 0 未被获取处理； 1 已被获取处理；
				redata.put("UPDATE_TIME", SysDateMgr.getSysTime());
				redata.put("ACCEPT_MONTH", SysDateMgr.getCurMonth()); 
				boolean insertflag = Dao.insert("TF_FH_PSPT", redata);
			}
		}			
	}
	
	/**
     * AEE自动任务
     * REQ201904240014 关于新开户用户新增“纯新增”标识的需求
     * @param userInfo
     * @throws Exception
     */
    public void dealExpire(IData inParam) throws Exception
    {
    	IData dealParam = new DataMap(inParam.getString("DEAL_COND"));
		//REQ201904240014 关于新开户用户新增“纯新增”标识的需求
    	IData redata = new DataMap();
		redata.put("CARD_ID_NUM",dealParam.getString("CARD_ID_NUM"));
		queryNewCardIdUserBean bean = BeanManager.createBean(queryNewCardIdUserBean.class);
		IDataset queryInfos = bean.Query(redata);
		System.out.println("纯新增标识"+queryInfos);
		if (IDataUtil.isEmpty(queryInfos)){
	        IData otherTD = new DataMap();
	        String userId = inParam.getString("USER_ID");
	        otherTD.put("USER_ID", userId);
	        otherTD.put("PARTITION_ID", StrUtil.getPartition4ById(userId));
	        otherTD.put("RSRV_VALUE_CODE", "NEW_USER_TAG");
	        otherTD.put("RSRV_VALUE", inParam.getString("SERIAL_NUMBER"));
	        otherTD.put("RSRV_STR1", dealParam.getString("STAFF_ID"));
	        otherTD.put("RSRV_STR2", SysDateMgr.getSysTime());
	        otherTD.put("RSRV_STR5", dealParam.getString("STAFF_NAME"));
	        otherTD.put("START_DATE", SysDateMgr.getSysTime());
	        otherTD.put("END_DATE", SysDateMgr.getTheLastTime());
	        otherTD.put("MODIFY_TAG", BofConst.MODIFY_TAG_ADD);
	        otherTD.put("STAFF_ID", dealParam.getString("STAFF_ID"));
	        otherTD.put("DEPART_ID", dealParam.getString("DEPART_ID"));
	        String tradeId = inParam.getString("TRADE_ID");
	        otherTD.put("TRADE_ID", tradeId);
	        otherTD.put("UPDATE_TIME", SysDateMgr.getSysTime());
	        otherTD.put("UPDATE_STAFF_ID", dealParam.getString("STAFF_ID"));
	        otherTD.put("UPDATE_DEPART_ID", dealParam.getString("DEPART_ID"));
	        otherTD.put("INST_ID", SeqMgr.getInstId());
	        Dao.insert("TF_F_USER_OTHER", otherTD);
		}
    }
}
