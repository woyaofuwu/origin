package com.asiainfo.veris.crm.order.soa.person.busi.family.mfc;

import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.RouteInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.SmsSend;
import org.apache.log4j.Logger;

import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;


public class FamilyGroupBean extends CSBizBean{
	protected static final Logger log = Logger.getLogger(FamilyGroupBean.class);

	/**
	 * 群组信息查询
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IDataset qryBossGroupInfo(IData input) throws Exception {
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
		param.put("KIND_ID", "MFCGroupQuery_BBOSS_0_0");	//需要替换新的kind_id
        IDataset rets= IBossCall.dealInvokeUrl("MFCGroupQuery_BBOSS_0_0","IBOSS6", param); 

		if(IDataUtil.isEmpty(rets)){
			CSAppException.apperr(CrmCommException.CRM_COMM_103,"网络异常！");	
		}	
		if(log.isDebugEnabled()){
			log.debug("一级boss返回的值"+rets);
		}
		return rets;
	}		
	/**
	 * 群组信息变更
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IDataset updateBossGroupInfo(IData input) throws Exception {
		IData result =new DataMap();
		IDataset resultInfo =new DatasetList();
//		String oprNumber =input.getString("OPR_NUMBER");
//		String memNumber= input.getString("MEM_NUMBER");
//		if(oprNumber.equals(memNumber) && (input.getString("POID_CODE")!="" || input.getString("POID_LABLE")!="")){
//			result.put("RSP_CODE", "99");
//			result.put("RSP_DESC", "副号码只能变更副号码标签");
//			resultInfo.add(result);
//			return  resultInfo;	
//		}
		//这里需要增加 POID_CODE 唯一性的判断
		//通过群组编码，主号码查poidcode 与入参判断，一样就不可以，不一样才可以，在这里还要下沉js里 poidcode 两位的检验。
		String poidCode =input.getString("POID_CODE");
		if(StringUtils.isNotBlank(poidCode)){
			if(poidCode.length()!=2){
				result.put("RSP_CODE","5000" );
				result.put("RSP_DESC","群组编码必须是两位数字！！");
				result.put("OPR_TIME",SysDateMgr.getSysTime());
				result.put("CUSTOMER_PHONE",input.getString("CUSTOMER_PHONE",""));
				result.put("PRODUCT_OFFERING_ID",input.getString("PRODUCT_OFFERING_ID"));
				resultInfo.add(result);
				return resultInfo;
			}
			int pCode = Integer.valueOf(poidCode);
			if(pCode<1 || pCode>99){
				result.put("RSP_CODE","5000" );
				result.put("RSP_DESC","两位群组编码格式不对");
				result.put("OPR_TIME",SysDateMgr.getSysTime());
				result.put("CUSTOMER_PHONE",input.getString("CUSTOMER_PHONE",""));
				result.put("PRODUCT_OFFERING_ID",input.getString("PRODUCT_OFFERING_ID"));
				resultInfo.add(result);
				return resultInfo;
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
					resultInfo.add(result);
					return resultInfo;
				}
			}else{
				IDataset uuinfos = MfcCommonUtil.getRelationUusByUserSnRole( input.getString("CUSTOMER_PHONE",""), "MF","1",null);
				if(IDataUtil.isNotEmpty(uuinfos)){
					for(int a=0;a<uuinfos.size();a++){
						String code =uuinfos.getData(a).getString("RSRV_STR2").substring(16); 
						if(poidCode.equals(code)){
							result.put("RSP_CODE","5000" );
							result.put("RSP_DESC","两位群组编码已存在！");
							result.put("OPR_TIME",SysDateMgr.getSysTime());
							result.put("CUSTOMER_PHONE",input.getString("CUSTOMER_PHONE",""));
							result.put("PRODUCT_OFFERING_ID",input.getString("PRODUCT_OFFERING_ID"));
							resultInfo.add(result);
							return resultInfo;
						}
					}
				}
			}
		}
		IData param =new DataMap();//调一级Boss的入参
		IDataset extendinfo =new DatasetList();
		param.put("COMPANY_ID", "898");
		param.put("BUSINESS_TYPE", input.getString("BUSINESS_TYPE","1"));
		param.put("PRODUCT_CODE", input.getString("PRODUCT_CODE"));
		param.put("PRODUCT_OFFERING_ID", input.getString("PRODUCT_OFFERING_ID",""));
		param.put("CUSTOMER_PHONE", input.getString("CUSTOMER_PHONE",""));
		param.put("POID_CODE", input.getString("POID_CODE",""));
		param.put("POID_LABLE", input.getString("POID_LABLE",""));
		param.put("MEM_NUMBER", input.getString("MEM_NUMBER",""));
		param.put("MEM_LABLE", input.getString("MEM_LABLE",""));
		param.put("EXTEND_INFO",extendinfo);
		param.put("BIZ_VERSION", input.getString("BIZ_VERSION","1.0.0"));
		param.put("KIND_ID", "MFCInfoModify_BBOSS_0_0");	//需要替换新的kind_id
        IDataset rets= IBossCall.dealInvokeUrl("MFCInfoModify_BBOSS_0_0","IBOSS6", param); 

		if(IDataUtil.isEmpty(rets)){
			CSAppException.apperr(CrmCommException.CRM_COMM_103,"网络异常！");	
		}	
		if(log.isDebugEnabled()){
			log.debug("一级boss返回的值"+rets);
		}
		IData data = (IDataUtil.isEmpty(rets)) ? new DataMap() : rets.getData(0);
		if (IDataUtil.isNotEmpty(data) && "00".equals(data.getString("RSP_CODE", ""))){
			input.put("COMPANY_ID", "898");
			input.put("BUSINESS_TYPE", param.getString("BUSINESS_TYPE","1"));
			input.put("BIZ_VERSION", param.getString("BIZ_VERSION","1.0.0"));
			changeGruopInfoSync(input);//g更新省侧数据
		}

		return rets;
	}

	/**
	 * 用户状态变更接口
	 * @param input
	 * @return
	 */
	public IDataset changStateSendBBoss(IData input)throws Exception{
		MfcCommonUtil.checkPramByKeys(input,"COMPANY_ID,PKG_TIME,PKG_SEQ,CHG_USER_SUM,BIZ_VERSION,CHG_USER");
		IData inBoss = new DataMap();
		IData output = new DataMap();
		output.put("SYNC_CODE","00");
		String seq = SeqMgr.getInstId();
		inBoss.put("COMPANY_ID",input.getString("COMPANY_ID"));
		inBoss.put("PKG_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
		inBoss.put("PKG_SEQ", seq.substring(0,6));
		inBoss.put("BIZ_VERSION", "1.0.0");
		inBoss.put("CHG_USER_SUM", input.getString("CHG_USER_SUM"));
		inBoss.put("CHG_USER", input.getDataset("CHG_USER"));
		inBoss.put("KIND_ID","MFCMemStatus_BBOSS_0_0");

		
        IDataset rets= IBossCall.dealInvokeUrl("MFCMemStatus_BBOSS_0_0","IBOSS6", inBoss); 

		if(IDataUtil.isNotEmpty(rets) && "00".equals(rets.getData(0).getString("SYNC_CODE"))){
			//插log表
			IData inparam =new DataMap();
			inparam.put("SEQ_ID",SeqMgr.getInstId() );
			inparam.put("PARTITION_ID",SysDateMgr.getCurMonth()); //分区标识
			inparam.put("PO_ORDER_NUMBER", seq );
			inparam.put("CUSTOMER_PHONE",input.getDataset("CHG_USER").getData(0).getString("USER_VALUE"));
			inparam.put("BUSINESS_TYPE", "1");
			inparam.put("CUSTOMER_TYPE", "1");
			inparam.put("COMPANY_ID", input.getString("COMPANY_ID"));
			inparam.put("ORDER_TYPE", "0");
			inparam.put("PRODUCT_CODE", "MFC");
			inparam.put("ACTION", "99");
			inparam.put("ORDER_SOURCE_ID", "01");
			inparam.put("BIZ_VERSION", "1.0.0");
			inparam.put("MEM_NUMBER",input.getDataset("CHG_USER").getData(0).getString("USER_VALUE"));
			inparam.put("MEM_ORDER_NUMBER", seq);
			inparam.put("FINISH_TIME",input.getDataset("CHG_USER").getData(0).getString("STATUS_OPR_TIME"));
			inparam.put("REMARK", "用户状态变更");
			inparam.put("EFF_TIME",SysDateMgr.getSysDateYYYYMMDDHHMMSS() );
			inparam.put("EXP_TIME",SysDateMgr.END_TIME_FOREVER  );
			inparam.put("RSRV_STR2",CSBizBean.getVisit().getStaffId());
			inparam.put("RSRV_STR3","0");// 在途  工单状态设 0
			Dao.insert("TF_B_FAMILY_LOG", inparam, Route.CONN_CRM_CEN);
		}else{
			output.put("SYNC_CODE",IDataUtil.isNotEmpty(rets)?rets.getData(0).getString("SYNC_CODE") : "01" );
			IData faildata = new DataMap();
			faildata.put("RSP_CODE","01");
			faildata.put("RSP_DESC","上发时接收响应异常");
			faildata.put("USER_VALUE",input.getDataset("CHG_USER").getData(0).getString("USER_VALUE"));
			//插log表
			IData inparam =new DataMap();
			inparam.put("SEQ_ID",SeqMgr.getInstId() );
			inparam.put("PARTITION_ID",SysDateMgr.getCurMonth()); //分区标识
			inparam.put("PO_ORDER_NUMBER", seq );
			inparam.put("CUSTOMER_PHONE",input.getDataset("CHG_USER").getData(0).getString("USER_VALUE"));
			inparam.put("BUSINESS_TYPE", "1");
			inparam.put("CUSTOMER_TYPE", "1");
			inparam.put("COMPANY_ID", input.getString("COMPANY_ID"));
			inparam.put("ORDER_TYPE", "0");
			inparam.put("PRODUCT_CODE", "MFC");
			inparam.put("ACTION", "98");
			inparam.put("ORDER_SOURCE_ID", "01");
			inparam.put("BIZ_VERSION", "1.0.0");
			inparam.put("MEM_NUMBER",input.getDataset("CHG_USER").getData(0).getString("USER_VALUE"));
			inparam.put("MEM_ORDER_NUMBER", seq);
			inparam.put("FINISH_TIME",input.getDataset("CHG_USER").getData(0).getString("STATUS_OPR_TIME"));
			inparam.put("REMARK", "用户状态变更上发时接收响应异常");
			inparam.put("EFF_TIME",SysDateMgr.getSysDateYYYYMMDDHHMMSS() );
			inparam.put("EXP_TIME",SysDateMgr.END_TIME_FOREVER  );
			inparam.put("RSRV_STR2",CSBizBean.getVisit().getStaffId());
			inparam.put("RSRV_STR3","0");// 在途  工单状态设 0
			output.put("FAIL_LIST",IDataUtil.isNotEmpty(rets)?rets.getData(0).getDataset("FAIL_LIST") : new DatasetList(faildata));
		}

		return  new DatasetList(output);
	}
	/**
	 * 群组信息同步落地接口
	 * @param input
	 * @return
	 */
	public IData changeGruopInfoSync(IData input)throws Exception{
		MfcCommonUtil.checkPramByKeys(input,"COMPANY_ID,BUSINESS_TYPE,PRODUCT_OFFERING_ID,CUSTOMER_PHONE,BIZ_VERSION");
		String customerPhone  =  input.getString("CUSTOMER_PHONE");
		String productOffingId =  input.getString("PRODUCT_OFFERING_ID");
		String memNumber  =  input.getString("MEM_NUMBER","");
		String poidCode  = input.getString("POID_CODE","");//群组编码
		String poidLable  = input.getString("POID_LABLE","");//业务订购实例标签
		String memLable  = input.getString("MEM_LABLE","");//成员标签

		//输出参数
		IData output = new DataMap();
		output.put("RSP_CODE","00");
		output.put("RSP_DESC","OK");

		if(log.isDebugEnabled()){
			log.debug("===========changeGruopInfoSync========input==="+input);
		}
		//查询TF_B_FAMILY_OTHER 有没有，有直接更新，没有新增
		IData qryParam = new DataMap();
		qryParam.put("PRODUCT_OFFERING_ID",productOffingId);
		String productCode = "";
		//校验通过 主号 + productOffingId 确认该家庭是否存在
		IDataset ralationUUset = MfcCommonUtil.getRelationUusByUserSnRole(customerPhone, "MF","1" ,qryParam);
		if(IDataUtil.isEmpty(ralationUUset)){
			output.put("RSP_CODE","02");
			output.put("RSP_DESC","根据[PRODUCT_OFFERING_ID]"+productOffingId+"没有查询到有效的家庭");
			return  output ;
		}else{
			String remark =  ralationUUset.getData(0).getString("REMARK","");

			if(StringUtils.contains(remark,MfcCommonUtil.PRODUCT_CODE_ZF)){
				productCode = MfcCommonUtil.PRODUCT_CODE_ZF ;
			}else if(StringUtils.contains(remark,MfcCommonUtil.PRODUCT_CODE_5G3)) {
				productCode = MfcCommonUtil.PRODUCT_CODE_5G3;
			}else if(StringUtils.contains(remark,MfcCommonUtil.PRODUCT_CODE_5G4)) {
				productCode = MfcCommonUtil.PRODUCT_CODE_5G4;
			}else if(StringUtils.contains(remark,MfcCommonUtil.PRODUCT_CODE_5G5)) {
				productCode = MfcCommonUtil.PRODUCT_CODE_5G5;
			}else if(StringUtils.contains(remark,MfcCommonUtil.PRODUCT_CODE_TF6)) {
				productCode = MfcCommonUtil.PRODUCT_CODE_TF6;
			}else if(StringUtils.contains(remark,MfcCommonUtil.PRODUCT_CODE_TF7)) {
				productCode = MfcCommonUtil.PRODUCT_CODE_TF7;
			}else if(StringUtils.contains(remark,MfcCommonUtil.PRODUCT_CODE_TF8)) {
				productCode = MfcCommonUtil.PRODUCT_CODE_TF8;
			}else if(StringUtils.contains(remark,MfcCommonUtil.PRODUCT_CODE_TF9)) {
				productCode = MfcCommonUtil.PRODUCT_CODE_TF9;
			}else if(StringUtils.contains(remark,MfcCommonUtil.PRODUCT_CODE_TF10)) {
				productCode = MfcCommonUtil.PRODUCT_CODE_TF10;
			}else if(StringUtils.contains(remark,MfcCommonUtil.PRODUCT_CODE_TF11)) {
				productCode = MfcCommonUtil.PRODUCT_CODE_TF11;
			}else {
				productCode = MfcCommonUtil.PRODUCT_CODE_TF;
			}
		}
		String  poidChangeTag = "0" ;//变更群组编码 或 业务订购实例标签 标识

		String  custFlag = "0";
		boolean memLableChangeTag = false ;//变更成员标签标识
		if(StringUtils.isNotBlank(poidCode) && StringUtils.isNotBlank(poidLable)){//变更群组编码 或 业务订购实例标签
			poidChangeTag = "1";
		}else if(StringUtils.isNotBlank(poidCode)){
			poidChangeTag = "2";
		}else if(StringUtils.isNotBlank(poidLable)){
			poidChangeTag = "3";
		}else{
			poidChangeTag = "0";
			//只变更成员标签，查询条件加上副号，只查询一条
			if(StringUtils.isNotBlank(memLable)){//变更成员标签
				if(StringUtils.equals(memNumber,customerPhone)){
                    custFlag = "2";
				}
				//qryParam.put("MEM_NUMBER",memNumber);
				memLableChangeTag = true;
			}else {
				//什么都没有变更，直接返回
				output.put("RSP_DESC","未变更");
				return output;
			}
		}

		//变更了群组变更群组编码 或 业务订购实例标签 标识   附带变更了成员标签
		if(StringUtils.isNotBlank(memLable)){//变更成员标签
			memLableChangeTag = true;

			if(StringUtils.equals(memNumber,customerPhone)){
				custFlag = "1";
			}
		}


		qryParam.put("CUSTOMER_PHONE",customerPhone);
		qryParam.put("FINISH_TAG","1");//有效记录


		//只有当只变更了成员标签的时候，查询出来是一条，其他都查询整个家庭的所有记录
		IDataset qryData = MfcCommonUtil.qryFamilyOtherInfo(qryParam);

		if(log.isDebugEnabled()){
			log.debug("===========changeGruopInfoSync========qryData==="+qryData);
		}
		//存在一种情况，存量数据原有家庭，现在基础上新加一个成员，那么Other表只有一条记录，再此基础更新也只更新一条，需要将其他成员数据补齐
		String userIdA = ralationUUset.getData(0).getString("USER_ID_A");
		IDataset familyMemAll = MfcCommonUtil.getSEL_USER_ROLEA(userIdA,null,"MF",qryParam);
		if(log.isDebugEnabled()){
			log.debug("===========changeGruopInfoSync========familyMemAll==="+familyMemAll);
		}
		if(IDataUtil.isNotEmpty(qryData)){
			//家庭成员有效数目，以UU关系的数目为准，一般情况都是qryData数量和familyMemAll数量一致
			//由于存量数据的影响，存在qryData数量小于familyMemAll数量，需要新增数据
			for(int i = 0; i<familyMemAll.size(); i++ ){
				IData UUInfo = familyMemAll.getData(i);

				boolean tag = true ; //默认当这条记录qryData中没有，需要新增
				for(int j=0; j < qryData.size();j++){
					IData otherInfo = qryData.getData(j);
					String sn =  otherInfo.getString("MEM_NUMBER","");
					if(StringUtils.equals(otherInfo.getString("ROLE_CODE_B"),UUInfo.getString("ROLE_CODE_B")) ){
						if("1".equals(otherInfo.getString("ROLE_CODE_B"))){
							sn=otherInfo.getString("CUSTOMER_PHONE","");
						}else{
							sn=otherInfo.getString("MEM_NUMBER","");
						}
					}
					//当other表数据和uu关系的一样就更新，否则需要新增
					if(StringUtils.equals(UUInfo.getString("SERIAL_NUMBER_B"),sn)){
						String uuid = otherInfo.getString("UUID");
						String otherMemNumber =   otherInfo.getString("MEM_NUMBER","");
						String poidCodeOld =   otherInfo.getString("POID_CODE","");
						String poidLableOld =   otherInfo.getString("POID_LABLE","");
						if(StringUtils.isBlank(poidCodeOld)){
							poidCodeOld =  otherInfo.getString("PRODUCT_OFFERING_ID","").substring(16);//默认 PRODUCT_OFFERING_ID 后两位
						}

				if(StringUtils.isBlank(poidLableOld)){//默认  群+POID_CODE
					poidLableOld = "群"+poidCodeOld;
				}
				IData updata = new DataMap();
				updata.put("UUID",uuid);

				IData smsData = new DataMap();
				smsData.put("CUSTOMER_PHONE",customerPhone);
				smsData.put("MEM_NUMBER",otherMemNumber);
				smsData.put("ROLE_CODE_B",otherInfo.getString("ROLE_CODE_B"));
				smsData.put("POID_CHANGE_TAG",poidChangeTag);
				smsData.put("POID_CODE_NEW",poidCode);
				smsData.put("POID_LABLE_NEW",poidLable);
				smsData.put("POID_CODE_OLD",poidCodeOld);
				smsData.put("POID_LABLE_OLD",poidLableOld);
				smsData.put("PRODUCT_CODE",productCode);

				updata.put("POID_CODE",otherInfo.getString("POID_CODE",""));
				updata.put("POID_LABLE",otherInfo.getString("POID_LABLE",""));
				updata.put("MEM_LABLE",otherInfo.getString("MEM_LABLE",""));
				updata.put("RSRV_STR1",otherInfo.getString("POID_CODE",""));
				updata.put("RSRV_STR2",otherInfo.getString("POID_LABLE",""));
				updata.put("RSRV_STR3",otherInfo.getString("MEM_LABLE",""));
				if("1".equals(poidChangeTag)){
					updata.put("POID_CODE",poidCode);
					updata.put("POID_LABLE",poidLable);
				}else if("2".equals(poidChangeTag)){
					updata.put("POID_CODE",poidCode);
				}else if("3".equals(poidChangeTag)){
					updata.put("POID_LABLE",poidLable);
				}
				if (memLableChangeTag) {
					if (StringUtils.equals(custFlag, "2")&& StringUtils.isBlank(otherInfo.getString("MEM_NUMBER", ""))
									&& StringUtils.equals(otherInfo.getString("CUSTOMER_PHONE", ""), memNumber)) {
								updata.put("MEM_LABLE", memLable);
					} else if (StringUtils.equals(custFlag, "1")&& StringUtils.isBlank(otherInfo.getString("MEM_NUMBER", ""))
									&& StringUtils.equals(otherInfo.getString("CUSTOMER_PHONE", ""), memNumber)) {
								updata.put("MEM_LABLE", memLable);
					} else if (StringUtils.equals(custFlag, "0")&& StringUtils.equals(otherMemNumber,memNumber)) {
								updata.put("MEM_LABLE", memLable);
					}
				}
				tag = false;//uuInfo当前一条数据在qryData中存在，直接更新，不要在新增

				int updatesum = MfcCommonUtil.updFamilyOtherInfo(updata);
				if(log.isDebugEnabled()){
							log.debug("===========changeGruopInfoSync========更新表的记录行数==="+updatesum);
							log.debug("===========changeGruopInfoSync========updatesum==="+updatesum);
				}
						if(MfcCommonUtil.PRODUCT_CODE_TF.equals(productCode) || MfcCommonUtil.PRODUCT_CODE_ZF.equals(productCode))
						{
							sendSmsForChangeGruopInfo(smsData);//发送群组信息变更通知短信
						}else {
							sendSmsForChangeGruopInfoNew(smsData);//发送群组信息变更通知短信
						}

			}
		}
				//当前一条数据在qryData中不存在，要在新增
				if(tag){
					String roleCodeB = UUInfo.getString("ROLE_CODE_B");
					String poidCodeOld =  productOffingId.substring(16);//默认 PRODUCT_OFFERING_ID 后两位
					String poidLableOld = "群"+poidCodeOld;
					String memberLable = "";
					String memberNumber = "";
					if("2".equals(roleCodeB)){
						memberNumber = UUInfo.getString("SERIAL_NUMBER_B");
						memberLable = memberNumber.substring(7);
					}

					IData insertFamilyOther = new DataMap();
					insertFamilyOther.put("UUID",SeqMgr.getInstId());
					insertFamilyOther.put("PRODUCT_OFFERING_ID",productOffingId);
					insertFamilyOther.put("PRODUCT_CODE",productCode);
					insertFamilyOther.put("CUSTOMER_PHONE",customerPhone);
					insertFamilyOther.put("MEM_NUMBER",memberNumber);
					insertFamilyOther.put("ROLE_CODE_B",roleCodeB);
					insertFamilyOther.put("POID_CODE",poidCodeOld);//赋默认值
					insertFamilyOther.put("POID_LABLE",poidLableOld);//赋默认值
					insertFamilyOther.put("MEM_LABLE",memberLable);//赋默认值
					insertFamilyOther.put("RSRV_STR1",poidCodeOld);
					insertFamilyOther.put("RSRV_STR2",poidLableOld);
					insertFamilyOther.put("RSRV_STR3",memberLable);
					insertFamilyOther.put("FINISH_TAG","1");
					insertFamilyOther.put("ADD_TIME",SysDateMgr.getSysDateYYYYMMDD());
					if("1".equals(poidChangeTag)){
						insertFamilyOther.put("POID_CODE",poidCode);
						insertFamilyOther.put("POID_LABLE",poidLable);
					}else if("2".equals(poidChangeTag)){
						insertFamilyOther.put("POID_CODE",poidCode);
					}else if("3".equals(poidChangeTag)){
						insertFamilyOther.put("POID_LABLE",poidLable);
					}

					if(memLableChangeTag && StringUtils.equals(memberNumber,memNumber) ){
						insertFamilyOther.put("MEM_LABLE",memLable);
					}
					boolean insertBoolean = Dao.insert("TF_B_FAMILY_OTHER",insertFamilyOther, Route.CONN_CRM_CEN);
					if(log.isDebugEnabled()){
						log.debug("===========changeGruopInfoSync========新增记录成功或失败==="+insertBoolean);
					}
					IData smsData = new DataMap();
					smsData.put("CUSTOMER_PHONE",customerPhone);
					smsData.put("MEM_NUMBER",memberNumber);
					smsData.put("PRODUCT_CODE",productCode);
					smsData.put("ROLE_CODE_B",roleCodeB);
					smsData.put("POID_CHANGE_TAG",poidChangeTag);
					smsData.put("POID_CODE_NEW",poidCode);
					smsData.put("POID_LABLE_NEW",poidLable);
					smsData.put("POID_CODE_OLD",poidCodeOld);
					smsData.put("POID_LABLE_OLD",poidLableOld);
					if(MfcCommonUtil.PRODUCT_CODE_TF.equals(productCode) || MfcCommonUtil.PRODUCT_CODE_ZF.equals(productCode))
					{
						sendSmsForChangeGruopInfo(smsData);//发送群组信息变更通知短信
					}else {
						sendSmsForChangeGruopInfoNew(smsData);//发送群组信息变更通知短信
					}
					//sendSmsForChangeGruopInfo(smsData);//发送群组信息变更通知短信
				}
			}
		}else{
			//新增记录
			if(IDataUtil.isNotEmpty(familyMemAll)){
				for(int i = 0 ; i < familyMemAll.size() ; i++){
					IData familyData = familyMemAll.getData(i);
					String roleCodeB = familyData.getString("ROLE_CODE_B");
					String poidCodeOld =  productOffingId.substring(16);//默认 PRODUCT_OFFERING_ID 后两位
					String poidLableOld = "群"+poidCodeOld;
					String memberLable = "";
					String memberNumber = "";
					if("2".equals(roleCodeB)){
						memberNumber = familyData.getString("SERIAL_NUMBER_B");
						memberLable = memberNumber.substring(7);
					}

					IData insertFamilyOther = new DataMap();
					insertFamilyOther.put("UUID",SeqMgr.getInstId());
					insertFamilyOther.put("PRODUCT_OFFERING_ID",productOffingId);
					insertFamilyOther.put("PRODUCT_CODE",productCode);
					insertFamilyOther.put("CUSTOMER_PHONE",customerPhone);
					insertFamilyOther.put("MEM_NUMBER",memberNumber);
					insertFamilyOther.put("ROLE_CODE_B",roleCodeB);
					insertFamilyOther.put("POID_CODE",poidCodeOld);//赋默认值
					insertFamilyOther.put("POID_LABLE",poidLableOld);//赋默认值
					insertFamilyOther.put("MEM_LABLE",memberLable);//赋默认值
					insertFamilyOther.put("RSRV_STR1",poidCodeOld);
					insertFamilyOther.put("RSRV_STR2",poidLableOld);
					insertFamilyOther.put("RSRV_STR3",memberLable);
					insertFamilyOther.put("FINISH_TAG","1");
					insertFamilyOther.put("ADD_TIME",SysDateMgr.getSysDateYYYYMMDD());
					if("1".equals(poidChangeTag)){
						insertFamilyOther.put("POID_CODE",poidCode);
						insertFamilyOther.put("POID_LABLE",poidLable);
					}else if("2".equals(poidChangeTag)){
						insertFamilyOther.put("POID_CODE",poidCode);
					}else if("3".equals(poidChangeTag)){
						insertFamilyOther.put("POID_LABLE",poidLable);
					}

					if(memLableChangeTag && StringUtils.equals(memberNumber,memNumber) ){
						insertFamilyOther.put("MEM_LABLE",memLable);
					}
					boolean insertBoolean = Dao.insert("TF_B_FAMILY_OTHER",insertFamilyOther, Route.CONN_CRM_CEN);
					if(log.isDebugEnabled()){
						log.debug("===========changeGruopInfoSync========新增记录成功或失败==="+insertBoolean);
					}
					IData smsData = new DataMap();
					smsData.put("CUSTOMER_PHONE",customerPhone);
					smsData.put("MEM_NUMBER",memberNumber);
					smsData.put("PRODUCT_CODE",productCode);
					smsData.put("ROLE_CODE_B",roleCodeB);
					smsData.put("POID_CHANGE_TAG",poidChangeTag);
					smsData.put("POID_CODE_NEW",poidCode);
					smsData.put("POID_LABLE_NEW",poidLable);
					smsData.put("POID_CODE_OLD",poidCodeOld);
					smsData.put("POID_LABLE_OLD",poidLableOld);
					if(MfcCommonUtil.PRODUCT_CODE_TF.equals(productCode) || MfcCommonUtil.PRODUCT_CODE_ZF.equals(productCode))
					{
						sendSmsForChangeGruopInfo(smsData);//发送群组信息变更通知短信
					}else {
						sendSmsForChangeGruopInfoNew(smsData);//发送群组信息变更通知短信
					}
					//sendSmsForChangeGruopInfo(smsData);//发送群组信息变更通知短信
				}
			}
		}
		return output;
	}

	public void sendSmsForChangeGruopInfo(IData smsData) throws Exception{
		String poidChangeTag = smsData.getString("POID_CHANGE_TAG");
		String roleCodeB = smsData.getString("ROLE_CODE_B");
		String customerPhone = smsData.getString("CUSTOMER_PHONE");
		String memNumber = smsData.getString("MEM_NUMBER","");
		IData sendSmsData = new DataMap();
		sendSmsData.put("CUSTOMER_PHONE",customerPhone);
		sendSmsData.put("MEM_NUMBER",memNumber);
		String templateId = "";
		//发短信
		IDataset config = CommparaInfoQry.getCommparaAllCol("CSM","2018","MCF_SEND_SMS_POID", "ZZZZ");

		if(IDataUtil.isEmpty(config)){
			return;
		}

		if("1".equals(config.getData(0).getString("PARA_CODE8"))){
			return;
		}
		String smsContent = "";
		String reciver = "";
		if(MfcCommonUtil.PRODUCT_CODE_ZF.equals(smsData.getString("PRODUCT_CODE"))){
			templateId = config.getData(0).getString("PARA_CODE1");
		}else{
			templateId = config.getData(0).getString("PARA_CODE2");
		}

		if("1".equals(poidChangeTag)){
			sendSmsData.put("POID_CODE_NEW",smsData.getString("POID_CODE_NEW"));
			sendSmsData.put("POID_LABLE_NEW",smsData.getString("POID_LABLE_NEW"));
			sendSmsData.put("POID_CODE_OLD",smsData.getString("POID_CODE_OLD"));
			sendSmsData.put("POID_LABLE_OLD",smsData.getString("POID_LABLE_OLD"));
			if("1".equals(roleCodeB)){//主号短信
				reciver = customerPhone;
			}else if("2".equals(roleCodeB)){
				reciver = memNumber;
			}
		}else if("2".equals(poidChangeTag)){

			sendSmsData.put("POID_CODE_NEW",smsData.getString("POID_CODE_NEW"));
			sendSmsData.put("POID_CODE_OLD",smsData.getString("POID_CODE_OLD"));
			if("1".equals(roleCodeB)){//主号短信
				reciver = customerPhone;
			}else if("2".equals(roleCodeB)){
				reciver = memNumber;
			}
		}else{
			return;
		}
		if(StringUtils.isBlank(templateId)){
			return;
		}
		smsContent = MfcCommonUtil.getSmsContentByTemplateId(templateId,sendSmsData);
		if (IDataUtil.isNotEmpty(RouteInfoQry.getMofficeInfoBySn(reciver))) {
			IData sendInfo = new DataMap();
			sendInfo.put("EPARCHY_CODE",RouteInfoQry.getEparchyCodeBySn(reciver));
			sendInfo.put("RECV_OBJECT", reciver);
			sendInfo.put("RECV_ID", reciver);
			sendInfo.put("SMS_PRIORITY", "50");
			sendInfo.put("NOTICE_CONTENT",smsContent );
			sendInfo.put("REMARK", "群组信息变更");
			sendInfo.put("FORCE_OBJECT", "10086");
			SmsSend.insSms(sendInfo,RouteInfoQry.getEparchyCodeBySn(reciver));
		}
	}

	public void sendSmsForChangeGruopInfoNew(IData smsData) throws Exception{
		String poidChangeTag = smsData.getString("POID_CHANGE_TAG");
		String roleCodeB = smsData.getString("ROLE_CODE_B");
		String customerPhone = smsData.getString("CUSTOMER_PHONE");
		String memNumber = smsData.getString("MEM_NUMBER","");
		IData sendSmsData = new DataMap();
		sendSmsData.put("CUSTOMER_PHONE",customerPhone);
		sendSmsData.put("MEM_NUMBER",memNumber);
		String templateId = "";
		//发短信
		IDataset config = CommparaInfoQry.getCommparaAllCol("CSM","2018","MCF_SEND_5GSMS_POID", "ZZZZ");
		
		IDataset zfbconfig = CommparaInfoQry.getCommparaAllCol("CSM","2018","MCF_SEND_ZFBSMS_POID", "ZZZZ");

		IDataset ywconfig = CommparaInfoQry.getCommparaAllCol("CSM","2018","MCF_SEND_YWSMS_POID", "ZZZZ");

        if(MfcCommonUtil.PRODUCT_CODE_TF6.equals(smsData.getString("PRODUCT_CODE", "")) || MfcCommonUtil.PRODUCT_CODE_TF7.equals(smsData.getString("PRODUCT_CODE", "")) || MfcCommonUtil.PRODUCT_CODE_TF8.equals(smsData.getString("PRODUCT_CODE", "")))
        {
        	if(IDataUtil.isEmpty(zfbconfig)){
    			return;
    		}

    		if("1".equals(zfbconfig.getData(0).getString("PARA_CODE8"))){
    			return;
    		}
    		String smsContent = "";
    		String reciver = "";
    		templateId = zfbconfig.getData(0).getString("PARA_CODE1");


    		if("1".equals(poidChangeTag)){
    			sendSmsData.put("POID_CODE_NEW",smsData.getString("POID_CODE_NEW"));
    			sendSmsData.put("POID_LABLE_NEW",smsData.getString("POID_LABLE_NEW"));
    			sendSmsData.put("POID_CODE_OLD",smsData.getString("POID_CODE_OLD"));
    			sendSmsData.put("POID_LABLE_OLD",smsData.getString("POID_LABLE_OLD"));
    			if("1".equals(roleCodeB)){//主号短信
    				reciver = customerPhone;
    			}else if("2".equals(roleCodeB)){
    				reciver = memNumber;
    			}
    		}else if("2".equals(poidChangeTag)){

    			sendSmsData.put("POID_CODE_NEW",smsData.getString("POID_CODE_NEW"));
    			sendSmsData.put("POID_CODE_OLD",smsData.getString("POID_CODE_OLD"));
    			if("1".equals(roleCodeB)){//主号短信
    				reciver = customerPhone;
    			}else if("2".equals(roleCodeB)){
    				reciver = memNumber;
    			}
    		}else{
    			return;
    		}
    		if(StringUtils.isBlank(templateId)){
    			return;
    		}
    		smsContent = MfcCommonUtil.getSmsContentByTemplateId(templateId,sendSmsData);
    		if (IDataUtil.isNotEmpty(RouteInfoQry.getMofficeInfoBySn(reciver))) {
    			IData sendInfo = new DataMap();
    			sendInfo.put("EPARCHY_CODE",RouteInfoQry.getEparchyCodeBySn(reciver));
    			sendInfo.put("RECV_OBJECT", reciver);
    			sendInfo.put("RECV_ID", reciver);
    			sendInfo.put("SMS_PRIORITY", "50");
    			sendInfo.put("NOTICE_CONTENT",smsContent );
    			sendInfo.put("REMARK", "群组信息变更");
    			sendInfo.put("FORCE_OBJECT", "10086");
    			SmsSend.insSms(sendInfo,RouteInfoQry.getEparchyCodeBySn(reciver));
    		}
        }else if(MfcCommonUtil.PRODUCT_CODE_TF9.equals(smsData.getString("PRODUCT_CODE", "")) || MfcCommonUtil.PRODUCT_CODE_TF10.equals(smsData.getString("PRODUCT_CODE", "")) || MfcCommonUtil.PRODUCT_CODE_TF11.equals(smsData.getString("PRODUCT_CODE", ""))){
        	if(IDataUtil.isEmpty(ywconfig)){
    			return;
    		}

    		if("1".equals(ywconfig.getData(0).getString("PARA_CODE8"))){
    			return;
    		}
    		String smsContent = "";
    		String reciver = "";
    		templateId = ywconfig.getData(0).getString("PARA_CODE1");


    		if("1".equals(poidChangeTag)){
    			sendSmsData.put("POID_CODE_NEW",smsData.getString("POID_CODE_NEW"));
    			sendSmsData.put("POID_LABLE_NEW",smsData.getString("POID_LABLE_NEW"));
    			sendSmsData.put("POID_CODE_OLD",smsData.getString("POID_CODE_OLD"));
    			sendSmsData.put("POID_LABLE_OLD",smsData.getString("POID_LABLE_OLD"));
    			if("1".equals(roleCodeB)){//主号短信
    				reciver = customerPhone;
    			}else if("2".equals(roleCodeB)){
    				reciver = memNumber;
    			}
    		}else if("2".equals(poidChangeTag)){

    			sendSmsData.put("POID_CODE_NEW",smsData.getString("POID_CODE_NEW"));
    			sendSmsData.put("POID_CODE_OLD",smsData.getString("POID_CODE_OLD"));
    			if("1".equals(roleCodeB)){//主号短信
    				reciver = customerPhone;
    			}else if("2".equals(roleCodeB)){
    				reciver = memNumber;
    			}
    		}else{
    			return;
    		}
    		if(StringUtils.isBlank(templateId)){
    			return;
    		}
    		smsContent = MfcCommonUtil.getSmsContentByTemplateId(templateId,sendSmsData);
    		if (IDataUtil.isNotEmpty(RouteInfoQry.getMofficeInfoBySn(reciver))) {
    			IData sendInfo = new DataMap();
    			sendInfo.put("EPARCHY_CODE",RouteInfoQry.getEparchyCodeBySn(reciver));
    			sendInfo.put("RECV_OBJECT", reciver);
    			sendInfo.put("RECV_ID", reciver);
    			sendInfo.put("SMS_PRIORITY", "50");
    			sendInfo.put("NOTICE_CONTENT",smsContent );
    			sendInfo.put("REMARK", "群组信息变更");
    			sendInfo.put("FORCE_OBJECT", "10086");
    			SmsSend.insSms(sendInfo,RouteInfoQry.getEparchyCodeBySn(reciver));
    		}
        }else{
        	if(IDataUtil.isEmpty(config)){
    			return;
    		}

    		if("1".equals(config.getData(0).getString("PARA_CODE8"))){
    			return;
    		}
    		String smsContent = "";
    		String reciver = "";
    		templateId = config.getData(0).getString("PARA_CODE1");


    		if("1".equals(poidChangeTag)){
    			sendSmsData.put("POID_CODE_NEW",smsData.getString("POID_CODE_NEW"));
    			sendSmsData.put("POID_LABLE_NEW",smsData.getString("POID_LABLE_NEW"));
    			sendSmsData.put("POID_CODE_OLD",smsData.getString("POID_CODE_OLD"));
    			sendSmsData.put("POID_LABLE_OLD",smsData.getString("POID_LABLE_OLD"));
    			if("1".equals(roleCodeB)){//主号短信
    				reciver = customerPhone;
    			}else if("2".equals(roleCodeB)){
    				reciver = memNumber;
    			}
    		}else if("2".equals(poidChangeTag)){

    			sendSmsData.put("POID_CODE_NEW",smsData.getString("POID_CODE_NEW"));
    			sendSmsData.put("POID_CODE_OLD",smsData.getString("POID_CODE_OLD"));
    			if("1".equals(roleCodeB)){//主号短信
    				reciver = customerPhone;
    			}else if("2".equals(roleCodeB)){
    				reciver = memNumber;
    			}
    		}else{
    			return;
    		}
    		if(StringUtils.isBlank(templateId)){
    			return;
    		}
    		smsContent = MfcCommonUtil.getSmsContentByTemplateId(templateId,sendSmsData);
    		if (IDataUtil.isNotEmpty(RouteInfoQry.getMofficeInfoBySn(reciver))) {
    			IData sendInfo = new DataMap();
    			sendInfo.put("EPARCHY_CODE",RouteInfoQry.getEparchyCodeBySn(reciver));
    			sendInfo.put("RECV_OBJECT", reciver);
    			sendInfo.put("RECV_ID", reciver);
    			sendInfo.put("SMS_PRIORITY", "50");
    			sendInfo.put("NOTICE_CONTENT",smsContent );
    			sendInfo.put("REMARK", "群组信息变更");
    			sendInfo.put("FORCE_OBJECT", "10086");
    			SmsSend.insSms(sendInfo,RouteInfoQry.getEparchyCodeBySn(reciver));
    		}
        }
		
	}
}
