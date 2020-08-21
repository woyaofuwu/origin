package com.asiainfo.veris.crm.order.soa.frame.csservice.group.ecsync;


import com.ailk.biz.util.TimeUtil;
import com.ailk.bizcommon.route.Route;
import com.ailk.bizservice.base.CSBizService;
import com.ailk.bizservice.dao.Dao;
import com.ailk.bizservice.seq.SeqMgr;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.util.DataUtils;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.ArrayUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.GrpInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserGrpInfoQry;

public class ModifyAllNetEcSyncInfoSVC  extends CSBizService{
	
	IData result = new DataMap();

	public IData modifyAllNetEcSyncInfo(IData param) throws Exception {
		String desc = "";
		
		String Channel = param.getString("CHANNEL");//操作来源
		String ParentCustomerNumber = param.getString("PARENT_CUSTOMER_NUMBER");//上级集团编码 0-表示没有上级
		String OprSeq = param.getString("OPR_SEQ");//发起方操作的流水
		String OprTime = param.getString("OPR_TIME");//操作时间
		String BizType = param.getString("BIZ_TYPE");//业务类型代码
		String OprCode = param.getString("OPR_CODE");//获取操作编码01，新增；02，修改；03，删除 
		
		
		if(StringUtils.isBlank(OprCode)){
			return operateFail(OprSeq,"操作编码信息为空！");
		}
		if(!"01".equals(OprCode)&&!"02".equals(OprCode)&&!"03".equals(OprCode)){
			return operateFail(OprSeq,"操作编码信息有误！");
		}
		//集团数组
		IDataset custinfos = param.getDataset("CUSTOMER_INFO");
		//客户经理数组
		IDataset custManagers = param.getDataset("CUSTOMER_MANAGER_INFO");
		//关键人集合数组
		IDataset keypersons = param.getDataset("KEY_PERSON");
		
		if(custinfos== null || custinfos.size() == 0){
			return operateFail(OprSeq,"集团信息为空！");
		}
		if(custManagers== null || custManagers.size() == 0){
			return operateFail(OprSeq,"集团客户经理信息为空！");
		}
		if(keypersons== null || keypersons.size() == 0){
			return operateFail(OprSeq,"集团关键人信息为空！");
		}

		IData custinfo = custinfos.getData(0);
		IData custManagerInfo = custManagers.getData(0);
		String sysTime = TimeUtil.getSysTime();
		//设置集团信息
		IData cust = setCustomerInfo(custinfo);
		cust.put("UPDATE_TIME", sysTime);
		//设置客户经理信息
		IData manager = setCustomerManagerInfo(custManagerInfo);
		manager.put("UPDATE_TIME", sysTime);
		String eparchyCode = cust.getString("EPARCHY_CODE","");
		if(StringUtils.isBlank(eparchyCode)){
			return operateFail(OprSeq,"地州信息为空！");
			
		}
		
		
		//开始对每组数据进行新增修改删除操作
		if("01".equals(OprCode)){
			//新增
			if(StringUtils.isBlank(manager.getString("CUST_MANAGER_ID"))){
				//如果没有传工号信息,自己新增，取值格式 TS开头，后加六位序列数字，如TS000001
				manager.put("CUST_MANAGER_ID", getStaffNum());
			}
			String custId = SeqMgr.getCustId();
			cust.put("CUST_ID", custId);
			cust.put("CITY_CODE", custinfo.getString("LOCATION"));
			cust.put("GROUP_ID", SeqMgr.getGroupId());
			cust.put("REMOVE_TAG", "0");
			cust.put("CUST_MANAGER_ID", manager.getString("CUST_MANAGER_ID"));
			String org_id = custinfo.getString("ORG_ID","0");
			cust.put("RSRV_STR1", org_id);
			String regionId = custinfo.getString("REGION_ID","");
			cust.put("RSRV_STR2", regionId);
			boolean custflag = Dao.insert("TF_F_CUST_GROUP", cust,Route.CONN_CRM_CG);
			if(!custflag){
				return operateFail(OprSeq,"新增集团信息失败！");
			}
			
			//记录日志
			CustLog(cust, "0");
			
			String partition_id = (Long.parseLong(custId)%10000)+"";
			//-----start 客户信息拼装----------
			IData ncustomer = new DataMap();
			ncustomer.put("PARTITION_ID", partition_id);
			ncustomer.put("CUST_ID", custId);
			ncustomer.put("CUST_NAME", cust.getString("CUST_NAME"));
			ncustomer.put("CUST_TYPE", "1");
			ncustomer.put("CUST_STATE", "0");
			ncustomer.put("OPEN_LIMIT", "0");
			ncustomer.put("EPARCHY_CODE", eparchyCode);
			ncustomer.put("SCORE_VALUE", "0");
			ncustomer.put("CREDIT_CLASS", "0");
			ncustomer.put("BASIC_CREDIT_VALUE", "0");
			ncustomer.put("CREDIT_VALUE", "0");
			ncustomer.put("DEVELOP_STAFF_ID", getVisit().getStaffId());
			ncustomer.put("DEVELOP_DEPART_ID", getVisit().getDepartId());
			ncustomer.put("IN_STAFF_ID", getVisit().getStaffId());
			ncustomer.put("IN_DEPART_ID", getVisit().getDepartId());
			ncustomer.put("IN_DATE", sysTime);
			ncustomer.put("REMOVE_TAG", "0");
			ncustomer.put("UPDATE_TIME", sysTime);
			ncustomer.put("UPDATE_STAFF_ID", getVisit().getStaffId());
			ncustomer.put("UPDATE_DEPART_ID", getVisit().getDepartId());
			ncustomer.put("PSPT_TYPE_CODE",tranPsptType(custinfo.getString("LICENCE_TYPE")));
			ncustomer.put("PSPT_ID",custinfo.getString("TAX_NUM"));
			ncustomer.put("CITY_CODE", getVisit().getCityCode());
			ncustomer.put("RSRV_STR5", custinfo.getString("ORG_BIZ_CODE"));//新增OrgBizCode省公司组织机构标识字段；   
			//-----end 客户信息拼装----------
			Dao.insert("TF_F_CUSTOMER", ncustomer,Route.CONN_CRM_CG);
			
			IData extend = new DataMap();
			extend.put("EXTEND_ID", SeqMgr.getExtendId());
			extend.put("RSRV_NUM1", custinfo.getString("IS_ORG"));
			extend.put("RSRV_STR1", custinfo.getString("ORG_CUSTOMER_CODE"));
			extend.put("EXTEND_TAG", "SOCINFO");
			extend.put("EXTEND_VALUE", custId);
			boolean extendflag = Dao.insert("TF_F_CUST_GROUP_EXTEND", extend,Route.CONN_CRM_CG);
			if(!extendflag){
				return operateFail(OprSeq,"新增直管客户信息失败！");
				
			}
			
			IData ext = new DataMap();
			ext.put("EXTEND_ID", SeqMgr.getExtendId());
			ext.put("RSRV_TAG15", custinfo.getString("VALUELEVEL_ID"));
			ext.put("EXTEND_VALUE", custId);
			ext.put("EXTEND_TAG", "grpEx");
			boolean extflag = Dao.insert("TF_F_CUST_GROUP_EXTEND", ext,Route.CONN_CRM_CG);
			if(!extflag){
				return operateFail(OprSeq,"新增客户级别信息失败！");
				
			}
			
			if(StringUtils.isBlank(custManagerInfo.getString("STAFF_NUMBER",""))){
				boolean managerflag = Dao.insert("TF_F_CUST_MANAGER_STAFF", manager,Route.CONN_CRM_CEN);
				if(!managerflag){
					return operateFail(OprSeq,"新增客户经理信息失败！");
				}
			}else{
				IData man = new DataMap();
				man.put("CUST_MANAGER_ID",custManagerInfo.getString("STAFF_NUMBER",""));
				IDataset maninfos = Dao.qryByCode("TF_F_CUST_MANAGER_STAFF", "SEL_BY_PK", man, Route.CONN_CRM_CEN);
				if(DataUtils.isEmpty(maninfos)){
					boolean managerflag = Dao.insert("TF_F_CUST_MANAGER_STAFF", manager,Route.CONN_CRM_CEN);
					if(!managerflag){
						return operateFail(OprSeq,"新增客户经理信息失败！");
					}
				}
			}
			
			//新增进出口标识编码 chenhh6
			if(StringUtils.isNotBlank(custinfo.getString("CUSTGLOBAL_TRADEID"))){
				IData global = new DataMap();
				global.put("EXTEND_ID", SeqMgr.getExtendId());
				global.put("EXTEND_VALUE", custId);
				global.put("EXTEND_TAG", "IOT");
				global.put("RSRV_STR1", custinfo.getString("CUSTGLOBAL_TRADEID"));
				boolean globalflag = Dao.insert("TF_F_CUST_GROUP_EXTEND", global,Route.CONN_CRM_CG);
				if(!globalflag){
					return operateFail(OprSeq,"新增进出口标识信息失败！");
				}
			}
			
			for(int i=0; i<keypersons.size(); i++){
				IData keyperson = keypersons.getData(i);
				//设置关键人信息
				IData person = setKeyPerson(keyperson); 
				person.put("EXTEND_ID", SeqMgr.getExtendId());
				person.put("EXTEND_VALUE", custId);
				boolean personflag = Dao.insert("TF_F_CUST_GROUP_EXTEND", person,Route.CONN_CRM_CG);
				if(!personflag){
					return operateFail(OprSeq,"新增关键人信息失败！");
					
				}
			}

			desc= "新增成功！";
		}else if("02".equals(OprCode)){
			//修改
			String custNum = custinfo.getString("CUSTOMER_NUMBER","");
			IDataset entinfos = UserGrpInfoQry.queryCustGrpByGID(custNum);
			if(entinfos.size()>0){
				String custId = entinfos.getData(0).getString("CUST_ID");
				String custMgrId = entinfos.getData(0).getString("CUST_MANAGER_ID");
				cust.put("CUST_ID",custId);
				cust.put("CUST_MANAGER_ID",custMgrId);
				cust.put("GROUP_ID", entinfos.getData(0).getString("GROUP_ID"));
				String org_id = custinfo.getString("ORG_ID","0");
				cust.put("RSRV_STR1", org_id);
				String regionId = custinfo.getString("REGION_ID","");
				cust.put("RSRV_STR2", regionId);
				//记录日志
				CustLog(entinfos.getData(0), "1");			
				Boolean custflag = Dao.save("TF_F_CUST_GROUP", cust,Route.CONN_CRM_CG);
				if(!custflag){
					return operateFail(OprSeq,"修改集团信息失败！");
					
				}
				//记录日志
				cust.put("CITY_CODE", custinfo.getString("LOCATION"));
				cust.put("REMOVE_TAG", "0");
				CustLog(cust, "2");
				
				manager.put("CUST_MANAGER_ID", custMgrId);
				IData man = new DataMap();
				man.put("CUST_MANAGER_ID",custMgrId);
				IDataset maninfos = Dao.qryByCode("TF_F_CUST_MANAGER_STAFF", "SEL_BY_PK", man, Route.CONN_CRM_CEN);
				if(maninfos!=null && maninfos.size()>0){
					boolean managerflag = Dao.save("TF_F_CUST_MANAGER_STAFF", manager,Route.CONN_SYS);
					if(!managerflag){
						return operateFail(OprSeq,"修改客户经理信息失败！");
					}
				}else {
					return operateFail(OprSeq,"没有客户经理记录，不能修改！");
				}
				//删除之前关键人信息，再做新增
				IData delExtend = new DataMap();
				delExtend.put("EXTEND_VALUE", custId);
				Dao.delete("TF_F_CUST_GROUP_EXTEND", delExtend,new String[] { "EXTEND_VALUE" },Route.CONN_CRM_CG);
				
				IData extend = new DataMap();
				extend.put("EXTEND_ID", SeqMgr.getExtendId());
				extend.put("RSRV_NUM1", custinfo.getString("IS_ORG"));
				extend.put("RSRV_STR1", custinfo.getString("ORG_CUSTOMER_CODE"));
				extend.put("EXTEND_TAG", "SOCINFO");
				extend.put("EXTEND_VALUE", custId);
				boolean extendflag = Dao.insert("TF_F_CUST_GROUP_EXTEND", extend,Route.CONN_CRM_CG);
				if(!extendflag){
					return operateFail(OprSeq,"修改直管客户信息失败！");
					
				}
				
				IData ext = new DataMap();
				ext.put("EXTEND_ID", SeqMgr.getExtendId());
				ext.put("RSRV_TAG15", custinfo.getString("VALUELEVEL_ID"));
				ext.put("EXTEND_VALUE", custId);
				ext.put("EXTEND_TAG", "grpEx");
				boolean extflag = Dao.insert("TF_F_CUST_GROUP_EXTEND", ext,Route.CONN_CRM_CG);
				if(!extflag){
					return operateFail(OprSeq,"修改客户级别信息失败！");
					
				}
				
				//修改进出口标识编码 chenhh6
				if(StringUtils.isNotBlank(custinfo.getString("CUSTGLOBAL_TRADEID"))){
					IData global = new DataMap();
					global.put("EXTEND_ID", SeqMgr.getExtendId());
					global.put("EXTEND_VALUE", custId);
					global.put("EXTEND_TAG", "IOT");
					global.put("RSRV_STR1", custinfo.getString("CUSTGLOBAL_TRADEID"));
					boolean globalflag = Dao.insert("TF_F_CUST_GROUP_EXTEND", global,Route.CONN_CRM_CG);
					if(!globalflag){
						return operateFail(OprSeq,"修改进出口标识信息失败！");
					}
				}
				
				for(int i=0; i<keypersons.size(); i++){
					IData keyperson = keypersons.getData(i);
					
					//设置关键人信息
					IData person = setKeyPerson(keyperson); 
					person.put("EXTEND_ID", SeqMgr.getExtendId());
					person.put("EXTEND_VALUE", custId);
					boolean personflag = Dao.insert("TF_F_CUST_GROUP_EXTEND", person,Route.CONN_CRM_CG);
					if(!personflag){
						return operateFail(OprSeq,"修改关键人信息失败！");
						
					}
				}
			
			}else {
				return operateFail(OprSeq,"没有集团记录，不能修改！");
			}
			
			desc= "修改成功！";
		}else if("03".equals(OprCode)){
			//删除
			String custNum = custinfo.getString("CUSTOMER_NUMBER","");
			IDataset infos = UserGrpInfoQry.queryCustGrpByGID(custNum);
			if(infos!=null && infos.size()>0){
				String custId = infos.getData(0).getString("CUST_ID");
				String custMgrId = infos.getData(0).getString("CUST_MANAGER_ID");
				//记录日志
				CustLog(infos.getData(0), "3");
				
				IData info = infos.getData(0);
				info.put("REMOVE_TAG", "1");
				boolean custflag = Dao.save("TF_F_CUST_GROUP", info,Route.CONN_CRM_CG);
				if(!custflag){
					return operateFail(OprSeq,"删除集团信息失败！");
					
				}
				
				IData man = new DataMap();
				man.put("CUST_MANAGER_ID",custMgrId);
				IDataset managerinfos = Dao.qryByCode("TF_F_CUST_MANAGER_STAFF", "SEL_BY_PK", man, Route.CONN_CRM_CEN);
				if(managerinfos!=null && managerinfos.size()>0){
					IData managerinfo = managerinfos.getData(0);
					managerinfo.put("VALID_TAG", "0");//客户经理失效
					boolean managerflag = Dao.save("TF_F_CUST_MANAGER_STAFF", managerinfo,Route.CONN_SYS);
					if(!managerflag){
						return operateFail(OprSeq,"删除客户经理信息失败！");
						
					}
				} else {
					return operateFail(OprSeq,"没有客户经理记录，不能删除！");
				}
				
			} else {
				return operateFail(OprSeq,"没有集团记录，不能删除！");
				
			}
			 desc= "删除成功！";
		}

		
		result.put("OPR_SEQ", OprSeq);
		result.put("BIZ_CODE", "0000");
		result.put("BIZ_DESC", desc);
		result.put("X_RSPTYPE", "0");
		result.put("X_RSPCODE", "0000");
		result.put("X_RSPDESC", desc);
		
		return result;

    }
	
	/**
	 * 操作失败
	 * @param oprSeq
	 * @param desc
	 */
	private IData operateFail(String oprSeq,String desc){
		result.put("OPR_SEQ", oprSeq);
		result.put("BIZ_CODE", "2999");
		result.put("BIZ_DESC", desc);
		result.put("X_RSPTYPE", "2");
		result.put("X_RSPCODE", "2999");
		result.put("X_RSPDESC", desc);
		return result;
	}
	/**
	 * 设置集团信息
	 * @param custinfo
	 * @return
	 */
	private IData setCustomerInfo(IData custinfo) {
		IData cust = new DataMap();
		cust.put("MP_GROUP_CUST_CODE", custinfo.getString("CUSTOMER_NUMBER",""));
		cust.put("CUST_NAME", custinfo.getString("CUSTOMER_NAME",""));
		cust.put("CLASS_ID", getClassId(custinfo.getString("CREDIT_LEVEL_ID","")));
		cust.put("SUBCLASS_ID", custinfo.getString("CUSTOMER_RANK_ID",""));
		cust.put("RSRV_TAG2", custinfo.getString("LOYALTY_LEVEL_ID",""));
		cust.put("BUSI_LICENCE_NO", custinfo.getString("TAX_NUM",""));
		cust.put("JURISTIC_NAME", custinfo.getString("CORPORATION",""));
		cust.put("REG_MONEY", custinfo.getString("LOGIN_FINANCING",""));
		cust.put("SUB_CALLING_TYPE_CODE", custinfo.getString("INDUSTRY_ID",""));
		cust.put("ENTERPRISE_TYPE_CODE", Integer.valueOf(custinfo.getString("ORGANIZATION_TYPE_ID",""))+"");
		cust.put("ENTERPRISE_SIZE_CODE", custinfo.getString("EMPLOYEE_AMOUNT_ID",""));
		cust.put("USER_NUM", custinfo.getString("MEMBER_COUNT",""));
		cust.put("EPARCHY_CODE", custinfo.getString("LOCATION",""));
		cust.put("POST_CODE", custinfo.getString("POST_CODE",""));
		cust.put("GROUP_ADDR", custinfo.getString("ADDRESS_FULL_NAME",""));
		cust.put("GROUP_MEMO", custinfo.getString("BACKGROUND",""));
		cust.put("REMARK", custinfo.getString("DESCRIPTION",""));
		cust.put("SERV_LEVEL", custinfo.getString("CUSTOMER_SERVLEVEL",""));
		cust.put("ORG_STRUCT_CODE", custinfo.getString("ORG_CODE",""));
		cust.put("YEAR_GAIN", custinfo.getString("ANNUAL_SALES",""));
		cust.put("PROVINCE_CODE", custinfo.getString("CUSTOMER_LOCATION",""));
		cust.put("BUSI_LICENCE_TYPE", tranBusiLicenceType(custinfo.getString("LICENCE_TYPE","")));
		cust.put("RSRV_STR7", "从CTBOSSS同步过来的集团信息");
		cust.put("ORG_BIZ_CODE", custinfo.getString("ORG_BIZ_CODE",""));
		return cust;
	}
	/**
	 * 设置客户经理信息
	 * @param custManagerInfo
	 * @return
	 */
	private IData setCustomerManagerInfo(IData custManagerInfo) {
		IData manager = new DataMap();
		manager.put("CUST_MANAGER_ID", custManagerInfo.getString("STAFF_NUMBER",""));
		manager.put("CUST_MANAGER_NAME", custManagerInfo.getString("STAFF_NAME",""));
		manager.put("LINK_PHONE", custManagerInfo.getString("CONTACT_PHONE",""));
		manager.put("SERIAL_NUMBER", custManagerInfo.getString("MOBILE_PHONE",""));
		manager.put("FAX", custManagerInfo.getString("CONTACT_FAX",""));
		manager.put("EMAIL", custManagerInfo.getString("E_MAIL",""));
		manager.put("RSRV_STR1", custManagerInfo.getString("LEADER_NAME",""));;
		manager.put("RSRV_STR2", custManagerInfo.getString("LEADER_TEL",""));
		return manager;
	}
	/**
	 * 设置关键人信息
	 * @param keyperson
	 * @return
	 */
	private IData setKeyPerson(IData keyperson) {
		IData person = new DataMap();
		person.put("RSRV_STR1", keyperson.getString("ROLE",""));
		person.put("RSRV_STR2", keyperson.getString("PARTY_NAME",""));
		person.put("RSRV_STR3", keyperson.getString("SEX",""));
		person.put("RSRV_STR30", keyperson.getString("CONTACT_PHONE",""));
		person.put("RSRV_STR4", keyperson.getString("TITLE",""));
		person.put("RSRV_STR5", keyperson.getString("ALIAS",""));
		person.put("RSRV_DATE1", keyperson.getString("BIRTHDAY",""));
		person.put("RSRV_STR6", keyperson.getString("MEMORIAL",""));
		person.put("RSRV_STR7", keyperson.getString("MATE",""));
		person.put("RSRV_STR8", keyperson.getString("SECRETARY",""));
		person.put("RSRV_STR9", keyperson.getString("SCHOOL",""));
		person.put("RSRV_STR10", keyperson.getString("CLASSMATES",""));
		person.put("RSRV_STR11", keyperson.getString("HOBBY",""));
		person.put("RSRV_STR12", keyperson.getString("LEADER",""));
		person.put("RSRV_STR13", keyperson.getString("LEADER_DEPT",""));
		person.put("RSRV_STR16", keyperson.getString("VASSAL",""));
		person.put("RSRV_STR14", keyperson.getString("INTERCOURSE",""));
		person.put("EXTEND_TAG", "keym");
		return person;
	}
	
	/**
	 * 获取工号信息
	 * @return
	 * @throws Exception
	 */
	private String getStaffNum() throws Exception{

		IData param = new DataMap();
		SQLParser parser = new SQLParser(param);
	    parser.addSQL("select TF_F_CUST_MANAGER_STAFF$SEQ.nextval as STAFF_NUM_ID from dual ");
	    IDataset dataset = Dao.qryByParse(parser,  null, Route.CONN_CRM_CEN);
	    String custMgId = dataset.getData(0).getString("STAFF_NUM_ID");
		StringBuilder strB = new StringBuilder();
		//不足六位前面补0
		for(int i=custMgId.length(); i<6; i++){
			strB.append("0");
		}
		custMgId = "TS"+strB.toString()+custMgId;
		return custMgId;
	}

	/**
	 * 证件类型转换
	 * @param psptType
	 * @return
	 */
	private String tranBusiLicenceType(String psptType){
		//1、三证合一营业执照2、营业执照3、组织机构代码证4、税务登记证5、事业单位法人证书6、社会团体法人登记证书
		if("1".equals(psptType) || "3".equals(psptType) || "4".equals(psptType) 
				|| "5".equals(psptType) || "6".equals(psptType)){
			return psptType;
		} 
		if("2".equals(psptType)){
			//2-营业执照
			return "E";
		} 
		return "E";
	}
	/**
	 * 证件类型转换
	 * @param psptType
	 * @return
	 */
	private String tranPsptType(String psptType){
		if("1".equals(psptType) || "2".equals(psptType)){//1-三证合一营业执照  2-营业执照
			return "E";
		} 
		if("3".equals(psptType)){//3-组织机构代码证	
			return "M";
		}
		if("5".equals(psptType)){//5-事业单位法人证书
			return "G";
		}
		if("6".equals(psptType)){//6-社会团体法人登记证书
			return "L";
		}
		if("7".equals(psptType)){//7-单位证明
			return "D";
		}
		if("9".equals(psptType)){//9-军队代码
			return "C";
		}
		return "Z";
	}
	/**
	 * 集团级别转换
	 * @param classId
	 * @return
	 */
	private String getClassId(String classId){
		int i = Integer.valueOf(classId)+4;
		return i+"";
	}
	
	/**
	 * 记录日志
	 * @param idataset
	 * @param modifyTag
	 * @throws Exception
	 */
	private void CustLog(IData idataset,String modifyTag) throws Exception{
		String logId = SeqMgr.getLogId();
		idataset.put("SYNC_SEQUENCE", logId);
		idataset.put("MODIFY_TAG", modifyTag);
		idataset.put("SYNC_DAY", TimeUtil.getCurDay());
		Dao.insert("TI_B_CUST_GROUP", idataset, Route.CONN_CRM_CG);
	}
	
}
