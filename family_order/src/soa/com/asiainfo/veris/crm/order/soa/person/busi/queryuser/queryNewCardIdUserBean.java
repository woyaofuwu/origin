package com.asiainfo.veris.crm.order.soa.person.busi.queryuser;

import org.apache.log4j.Logger;

import com.ailk.biz.bean.BizBean;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCallIntf;

public class queryNewCardIdUserBean extends CSBizBean {
	
	private final static Logger logger = Logger.getLogger(queryNewCardIdUserBean.class);

	public IDataset Query(IData data) throws Exception {

		/*
		 * 
		 * 你好！ 经与业务部门沟通，开发方案如下： 1、时间是第一查询条件：根据身份证，查询自开户之日起183天的这段时间
		 * 2、这段时间内，用户身份证号下是否有号码 （1）有号码，且是正常状态-------判断结果：不符合“新身份证用户的定义”
		 * （2）有号码，且是已销号状态（数据库查询条件用like'%销号%'），看最新的销号时间是否在183天内
		 * 是------判断结果：不符合“新身份证用户的定义” 不是-------符合定义 3、是否有“停机保号”的优惠，根据优惠的结束时间来判断
		 * （1）结束时间在半年内或者到2050年-----不符合定义 （2）结束时间在半年之前------符合定义
		 * 4、半年内是否有过户的业务，有就不符合 5、半年内是否有客户资料变更业务，有就不符合
		 */
//		System.out.println("queryNewCardIdUserBeanxxxxxxxxxxxxxxxxx15 " + data);
//		System.out.println("QqueryNewCardIdUserBeanxxxxxxxxxxxxxxxxxaaaaaaaaaaaa16 " + data);
		IDataset reDs = new DatasetList();

		// select add_months(sysdate,-12) from dual ;
		// select to_date(to_char(sysdate-183,'yyyy-mm-dd'),'yyyy-mm-dd') from
		// dual ;


		String pstpId = data.getString("CARD_ID_NUM", "").trim();		
		IData param = new DataMap();
		param.put("PSPT_ID", pstpId);

		String routeId = Route.getJourDbDefault();
		if (routeId == null || "".equals(routeId)) {
			routeId = "0898";
		}
		
		// 半年内是否有过户的业务，有就不符合
		param.clear();
		param.put("PSPT_ID", pstpId);
		param.put("TRADE_TYPE_CODE", "100");
		IDataset ds_4 = Dao.qryByCode("TF_BH_TRADE", "SEL_TRADETYPECODE_1", param, routeId);
//		System.out.println("QqueryNewCardIdUserBeanxxxxxxxxxxxxxxxxxaaaaaaaaaaaa77 " + ds_4);

		if (ds_4 != null && ds_4.size() > 0) {
			return ds_4;
		}
		
		// 半年内是否有客户资料变更的业务，有就不符合
		param.clear();
		param.put("PSPT_ID", pstpId);
		param.put("TRADE_TYPE_CODE", "60");
		IDataset ds_5 = Dao.qryByCode("TF_BH_TRADE", "SEL_TRADETYPECODE_1", param, routeId);
//		System.out.println("QqueryNewCardIdUserBeanxxxxxxxxxxxxxxxxxaaaaaaaaaaaa95 " + ds_5);

		if (ds_5 != null && ds_5.size() > 0) {
			return ds_5;
		}
		
		param.clear();
		param.put("PSPT_ID", pstpId);
		// 1.检查该身份证下，有号码
		IDataset ds_1 = Dao.qryByCode("TF_F_USER", "SEL_USER_BY_IDCARD_1", param, Route.getCrmDefaultDb());
//		System.out.println("QqueryNewCardIdUserBeanxxxxxxxxxxxxxxxxxaaaaaaaaaaaa36 " + ds_1);

		if (ds_1 == null || ds_1.size() == 0) {
			return reDs;
		}
		
		// 最近183天内，是否有开户open_date并且目前状态正常的号码 remove_tag=0		
		IDataset ds_2 = Dao.qryByCode("TF_F_USER", "SEL_USER_BY_IDCARD_2", param, Route.getCrmDefaultDb());
//		System.out.println("QqueryNewCardIdUserBeanxxxxxxxxxxxxxxxxxaaaaaaaaaaaa46 " + ds_2);

		if (ds_2 != null && ds_2.size() > 0) {
			return ds_2;
		}
		
		// 最近183天内，是否过销户的记录
		IDataset ds_3 = Dao.qryByCode("TF_F_USER", "SEL_USER_BY_IDCARD_3", param, Route.getCrmDefaultDb());
//		System.out.println("QqueryNewCardIdUserBeanxxxxxxxxxxxxxxxxxaaaaaaaaaaaa56 " + ds_3);

		if (ds_3 != null && ds_3.size() > 0) {
			return ds_3;
		}
		
//		System.out.println("QqueryNewCardIdUserBeanxxxxxxxxxxxxxxxxxaaaaaaaaaaaa69 " + routeId);
		
		for (int i = 0; i < ds_1.size(); i++) {
			IData userData = ds_1.getData(i);
			String userid = userData.getString("USER_ID");
			String serialnumber = userData.getString("SERIAL_NUMBER");			
			String removetag = userData.getString("REMOVE_TAG","").trim();	
			
			if(removetag.equals("0")||removetag.equals("1")||removetag.equals("3")){
				return ds_1;
			}
			
			param.clear();
			param.put("USER_ID", userid);
			param.put("SERIAL_NUMBER", serialnumber);
			IDataset ds_8 = Dao.qryByCode("TF_F_USER", "SEL_USER_BY_IDCARD_4", param, Route.getCrmDefaultDb());
			if (ds_8 != null && ds_8.size() > 0) {
				return ds_8;
			}
			
			IDataset ds_9 = Dao.qryByCode("TF_F_USER", "SEL_USER_BY_IDCARD_5", param, Route.getCrmDefaultDb());
			if (ds_9 != null && ds_9.size() > 0) {
				return ds_9;
			}
			
			/*
			
			//报停
			param.clear();
			param.put("USER_ID", userid);
			param.put("SERIAL_NUMBER", serialnumber);
			param.put("TRADE_TYPE_CODE", "131");
			IDataset ds_6 = Dao.qryByCode("TF_BH_TRADE", "SEL_TRADETYPECODE", param, routeId);
//			System.out.println("QqueryNewCardIdUserBeanxxxxxxxxxxxxxxxxxaaaaaaaaaaaa107 " + ds_6);			
			if (ds_6 != null && ds_6.size() > 0) {
				return ds_6;
			}
			
			//局方停机
			param.clear();
			param.put("USER_ID", userid);
			param.put("SERIAL_NUMBER", serialnumber);
			param.put("TRADE_TYPE_CODE", "136");
			IDataset ds_7 = Dao.qryByCode("TF_BH_TRADE", "SEL_TRADETYPECODE", param, routeId);
//			System.out.println("QqueryNewCardIdUserBeanxxxxxxxxxxxxxxxxxaaaaaaaaaaaa114 " + ds_7);			
			if (ds_7 != null && ds_7.size() > 0) {
				return ds_7;
			}*/
			
		}
		
		return reDs;
		
	}
	
	public IDataset QueryAllUserByPsptId(IData data) throws Exception {

		IDataset allUsers = new DatasetList();
		
		String pstpId = data.getString("CARD_ID_NUM", "").trim();		
		IData param = new DataMap();
		
		param.put("SERVICE_ID", "0");
		IDataset allUserTypes =  Dao.qryByCode("TD_S_SERVICESTATE", "SEL_SERVICESTATE", param, Route.CONN_CRM_CEN);
		
		param.clear();
		param.put("PSPT_ID", pstpId);
		// 1.检查该身份证下，有号码
		allUsers = Dao.qryByCode("TF_F_USER", "SEL_USER_BY_IDCARD_ALL", param, Route.getCrmDefaultDb());
		if (allUsers != null && allUsers.size() > 0) {
			for (int i = 0; i < allUsers.size(); i++) {
				IData userData = allUsers.getData(i);
				String userid = userData.getString("USER_ID","").trim();
				String serialnumber = userData.getString("SERIAL_NUMBER","").trim();
				String userStateCodeset = userData.getString("USER_STATE_CODESET","").trim();
				
				//用户号码模糊化处理 
				if(!"".equals(serialnumber)){
					String newSerialNumber = serialnumber.substring(0,3)+"****"+serialnumber.substring(7, serialnumber.length());
					userData.put("SERIAL_NUMBER", newSerialNumber);
				}
				
				userData.put("USER_TYPE", "0");
				userData.put("CARD_ID_NUM", pstpId);
				userData.put("INFOMSG", "该用户为非新身份证用户（即非纯新增用户）");
				if (allUserTypes != null && allUserTypes.size() > 0){
					for (int j= 0; j < allUserTypes.size(); j++){
						IData userType = allUserTypes.getData(j);
						String stateCode = userType.getString("STATE_CODE","").trim();
						String stateName = userType.getString("STATE_NAME","").trim();
						if(stateCode.equals(userStateCodeset)){
							userData.put("SN_STATE", stateName);
							break;
						}else{
							userData.put("SN_STATE", "");
						}
					}
				}
				param.clear();
				param.put("USER_ID", userid);
				IDataset mainProducts = Dao.qryByCode("TF_F_USER_PRODUCT", "SEL_USER_MAIN_PRODUCT", param, Route.getCrmDefaultDb());
				if(mainProducts != null && mainProducts.size() > 0){
					IData mainProduct = mainProducts.getData(0);
					String productId = mainProduct.getString("PRODUCT_ID","");
					IDataset offers = UpcCallIntf.queryOfferByOfferId("P", productId, "");
					if(offers != null && offers.size() > 0){
						IData offer = offers.getData(0);
						userData.put("DESCRIPTION", offer.getString("DESCRIPTION",""));
					}else{
						userData.put("DESCRIPTION", "");
					}
				}else{
					userData.put("DESCRIPTION", "");
				}
				
			}
		}
		
		return allUsers;
		
	}

}
