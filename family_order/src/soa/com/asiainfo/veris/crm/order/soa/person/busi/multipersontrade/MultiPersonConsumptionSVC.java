package com.asiainfo.veris.crm.order.soa.person.busi.multipersontrade;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.broadband.WidenetInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQrySVC;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.person.busi.batactivecancel.BatActiveCancelBean;
import com.asiainfo.veris.crm.order.soa.person.busi.familytrade.order.requestdata.FamilyUnionPayReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.familytrade.order.requestdata.UnionPayMemberData;
import com.asiainfo.veris.crm.order.soa.person.common.action.sms.PerSmsAction;

public class MultiPersonConsumptionSVC extends CSBizService{
	
	public IData QryMemberInfo(IData input) throws Exception
    {
		MultiPersonConsumptionBean bean = BeanManager.createBean(MultiPersonConsumptionBean.class);
        return bean.QryMemberInfo(input);
    }
	/**
	 * 短厅校验主号是否满足办理条件
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IData checkMemberData(IData input)throws Exception
	{
		IData resultData=new DataMap();
		resultData.put("RESULT_CODE", "0000");
		resultData.put("RESULT_INFO", "success");
		
		String mainSN=input.getString("SERIAL_NUMBER"); //主号
		String memberSN=input.getString("SERIAL_NUMBER_B");  //副号
		
		IData userInfo = UcaInfoQry.qryUserInfoBySn(mainSN);
		if(IDataUtil.isEmpty(userInfo))
		{
			resultData.put("RESULT_CODE", "2998");
			resultData.put("RESULT_INFO", "主号资料不存在");
			return resultData;
		}
		
		IData userInfoB = UcaInfoQry.qryUserInfoBySn(memberSN);
		if(IDataUtil.isEmpty(userInfoB))
		{
			resultData.put("RESULT_CODE", "2998");
			resultData.put("RESULT_INFO", "副号资料不存在");
			return resultData;
		}
		
		UcaData uca = UcaDataFactory.getNormalUca(mainSN); 
		UcaData ucaB = UcaDataFactory.getNormalUca(memberSN); 
		//校验主号是否满足办理条件，且下发二次确认短信
		IData drParam = new DataMap();
		drParam.put("SUBSYS_CODE", "CSM");
		drParam.put("PARAM_ATTR", "8383");
		drParam.put("PARAM_CODE", "DR_DISCNT");
		drParam.put("USER_ID", uca.getUserId());
		IDataset discntList = RelaUUInfoQry.qryPrincipalDiscnt(drParam);
		if (IDataUtil.isEmpty(discntList)) 
		{
			resultData.put("RESULT_CODE", "2998");
			resultData.put("RESULT_INFO", "主号需要办理【宽带1+多人约消活动】才可以组网！");
			return resultData;
		}
		
		/*IData params = new DataMap();
		params.put("SUBSYS_CODE", "CSM");
		params.put("PARAM_ATTR", "8383");
		params.put("PARAM_CODE", "DR_DEPLOY_PRODUCT");
		params.put("USER_ID", uca.getUserId());
		IDataset productList = RelaUUInfoQry.qryPrincipalDiscnt(params);
		if (IDataUtil.isNotEmpty(productList)) 
		{
			resultData.put("RESULT_CODE", "2998");
			resultData.put("RESULT_INFO", "办理企业尊享套餐不允许办理多人约消用户！");
			return resultData;
		}
		
		//一卡付多号校验
		IDataset payMoreCards = RelaUUInfoQry.queryRelaUUBySnb(mainSN, "97");
		if(IDataUtil.isNotEmpty(payMoreCards))
		{
			resultData.put("RESULT_CODE", "2998");
			resultData.put("RESULT_INFO", "该号码存在【一卡付多号】关系，不允许办理！");
			return resultData;
		}
		
		//一卡双号校验
		IDataset payDoubleCards = RelaUUInfoQry.queryRelaUUBySnb(mainSN, "30");
		if(IDataUtil.isNotEmpty(payDoubleCards))
		{
			resultData.put("RESULT_CODE", "2998");
			resultData.put("RESULT_INFO", "该号码存在【一卡双号】关系，不允许办理！");
			return resultData;
		}
		
		//剔除TD固话（TDYD）、移动公话B（G009）、移动公话C（G014）、随e行（G005）等品牌。移动公话存在两个品牌
		if("TDYD".equals(uca.getBrandCode()) || "G005".equals(uca.getBrandCode()) 
			|| "G009".equals(uca.getBrandCode()) || "G014".equals(uca.getBrandCode()))
		{
			resultData.put("RESULT_CODE", "2998");
			resultData.put("RESULT_INFO", "该号码不满足参与条件，建议更换其他号码！");
			return resultData;
		}*/
		
		if (mainSN.startsWith("0898"))
        {
			resultData.put("RESULT_CODE", "2998");
			resultData.put("RESULT_INFO", "0898用户不允许办理！");
			return resultData;
        }
		
		/*IDataset snRelationSet = RelaUUInfoQry.queryRelaUUBySnb(mainSN,"56");
		if (IDataUtil.isNotEmpty(snRelationSet)) 
		{
			resultData.put("RESULT_CODE", "2998");
			resultData.put("RESULT_INFO", "该号码存在统一付费关系，不允许办理！");
			return resultData;
		}*/
		
		/*String smsContent = mainSN+"现正在办理多人约消畅享宽带，需要您一起助力，若同意，请回复Y，若不同意，请回复N，若24小时内未回复，视同为不同意。中国移动海南公司";
		
		//下发提醒短信
		IData smsInfo = new DataMap();
		smsInfo.put("POST_PHONE", memberSN);
		smsInfo.put("CONTENT", smsContent);
		smsInfo.put("USER_ID_B", ucaB.getUserId());
		smsInfo.put("TRADE_STAFF_ID", getVisit().getStaffId());
		smsInfo.put("TRADE_DEPART_ID", getVisit().getDepartId());
		sendSMS(smsInfo);*/
		
		return resultData;
	}
	/**
	 * 短厅添加、删除成员
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IData operatorMember(IData input)throws Exception
	{
		IData resultData=new DataMap();
		resultData.put("RESULT_CODE", "0000");
		resultData.put("RESULT_INFO", "success");
		
		//主号删除成员，或者成员退出组网。发起号都默认为主号
		String mainSN=input.getString("SERIAL_NUMBER"); //发起号
		String memberSN=input.getString("SERIAL_NUMBER_B"); //接收号
		
		UcaData uca = UcaDataFactory.getNormalUca(mainSN);
		UcaData ucaB = UcaDataFactory.getNormalUca(memberSN);
		
		//组网添加成员
		if("0".equals(input.getString("OPERATOR_TYPE")))
		{
			/*IData userInfo = UcaInfoQry.qryUserInfoBySn(mainSN);
			if(IDataUtil.isEmpty(userInfo))
			{
				resultData.put("RESULT_CODE", "2998");
				resultData.put("RESULT_INFO", "主号资料不存在，或主号号码为空");
				return resultData;
			}
			
			IData userInfoB = UcaInfoQry.qryUserInfoBySn(memberSN);
			if(IDataUtil.isEmpty(userInfoB))
			{
				resultData.put("RESULT_CODE", "2998");
				resultData.put("RESULT_INFO", "副号资料不存在，或副号号码为空");
				return resultData;
			}*/
			
			//构造入参
			IData param=new DataMap();
			param.put("SERIAL_NUMBER", mainSN);
			param.put("CUST_NAME", uca.getCustomer().getCustName());
			param.put("FMY_VERIFY_MODE","2"); //短厅校验默认为2；2是免密码校验
			
			//构造成员
			IDataset memberList=new DatasetList();
			IData memberData=new DataMap();
			memberData.put("tag", "0");
			memberData.put("SERIAL_NUMBER_B", memberSN);
			memberData.put("CUST_NAME", ucaB.getCustomer().getCustName());
			memberList.add(memberData);
			
			param.put("MEB_LIST", memberList.toString());
			
			IDataset results=CSAppCall.call("SS.GroupCreateRegSVC.tradeReg", param);
			if(IDataUtil.isEmpty(results)){
				resultData.put("RESULT_CODE", "2998");
				resultData.put("RESULT_INFO", "添加成员失败");
				return resultData;
			}
		
		}else if("1".equals(input.getString("OPERATOR_TYPE")))//删除成员	
		{
			//如果发起号既不是主号也不是副号，返回2998
			IDataset mainList = RelaUUInfoQry.getRelationsByUserIdAndTypeAndRoleCodeB("61", uca.getUserId(), "1");
			if (IDataUtil.isEmpty(mainList))
			{
				IDataset memberList = RelaUUInfoQry.getRelationsByUserIdAndTypeAndRoleCodeB("61", uca.getUserId(), "2");
				if (IDataUtil.isEmpty(memberList)) 
				{
					resultData.put("RESULT_CODE", "2998");
					resultData.put("RESULT_INFO", "该号码查询不到多人约消组网关系，不允许办理");
					return resultData;
				}
				
			}
			
			//发起号和接收号必须存在同一个组网关系
			IDataset mainUserid = RelaUUInfoQry.queryRelaUUBySnb(mainSN,"61"); //发起号
			if (IDataUtil.isNotEmpty(mainUserid)) 
			{
				String mainUseridA = mainUserid.getData(0).getString("USER_ID_A");
				
				IDataset memberUserid = RelaUUInfoQry.queryRelaUUBySnb(memberSN,"61");//接收号
				if (IDataUtil.isNotEmpty(memberUserid)) 
				{
					String memberUseridA = memberUserid.getData(0).getString("USER_ID_A");
					if (!(mainUseridA.equals(memberUseridA))) 
					{
						resultData.put("RESULT_CODE", "2998");
						resultData.put("RESULT_INFO", "发起号和接收号不在同一个组网关系内，不允许办理");
						return resultData;
					}
				}else 
				{
					resultData.put("RESULT_CODE", "2998");
					resultData.put("RESULT_INFO", "接收号不存在多人约消组网关系，不允许办理");
					return resultData;
				}
				
			}else 
			{
				resultData.put("RESULT_CODE", "2998");
				resultData.put("RESULT_INFO", "发起号不存在多人约消组网关系，不允许办理");
				return resultData;
			}
			
			IData userInfo = UcaInfoQry.qryUserInfoBySn(mainSN);
			if(IDataUtil.isEmpty(userInfo))
			{
				resultData.put("RESULT_CODE", "2998");
				resultData.put("RESULT_INFO", "发起号资料不存在");
				return resultData;
			}
			
			IData userInfoB = UcaInfoQry.qryUserInfoBySn(memberSN);
			if(IDataUtil.isEmpty(userInfoB))
			{
				resultData.put("RESULT_CODE", "2998");
				resultData.put("RESULT_INFO", "接收号资料不存在");
				return resultData;
			}
			
			IData param=new DataMap();
			
			IDataset memberList = new DatasetList();
			IData memberData = new DataMap();
			memberData.put("tag", "1");
			memberData.put("SERIAL_NUMBER_B", memberSN);
			memberList.add(memberData);
			
			param.put("MEB_LIST", memberList.toString());
			param.put("SERIAL_NUMBER", mainSN);
			
			IDataset results=CSAppCall.call("SS.DelGroupNetMemberRegSVC.tradeReg", param);
			if(IDataUtil.isEmpty(results))
			{
				resultData.put("RESULT_CODE", "2998");
				resultData.put("RESULT_INFO", "删除成员失败");
				return resultData;
			}
			
		}else
		{
			resultData.put("RESULT_CODE", "2998");
			resultData.put("RESULT_INFO", "操作类型OPERATOR_TYPE值不符合");
		}
		
		return resultData;
	}
	
	/*
     * 短信下发
     * */
	/*public static void sendSMS(IData input) throws Exception
    
    {
    	// 拼短信表参数
    	IDataUtil.chkParam(input, "POST_PHONE");//发送短信的号码
    	IDataUtil.chkParam(input, "CONTENT");
 
        IData param = new DataMap();
        param.put("NOTICE_CONTENT", input.getString("CONTENT"));
        param.put("EPARCHY_CODE", input.getString("EPARCHY_CODE","0898"));
        param.put("IN_MODE_CODE", "0");
        param.put("RECV_OBJECT", input.getString("POST_PHONE"));
        param.put("RECV_ID", input.getString("USER_ID_B"));
        param.put("REFER_STAFF_ID", input.getString("TRADE_STAFF_ID",""));
        param.put("REFER_DEPART_ID", input.getString("TRADE_DEPART_ID",""));
        param.put("REMARK", "多人约消畅享宽带校验提醒");
        String seq = SeqMgr.getSmsSendId();
        long seq_id = Long.parseLong(seq);
        param.put("SMS_NOTICE_ID", seq_id);
        param.put("PARTITION_ID", seq_id % 1000);
        param.put("SEND_COUNT_CODE", "1");
        param.put("REFERED_COUNT", "0");
        param.put("CHAN_ID", "11");
        param.put("SMS_NET_TAG", "0");
        param.put("RECV_OBJECT_TYPE", "00");
        param.put("SMS_TYPE_CODE", "20");
        param.put("SMS_KIND_CODE", "02");
        param.put("NOTICE_CONTENT_TYPE", "0");
        param.put("FORCE_REFER_COUNT", "1");
        param.put("FORCE_OBJECT", "10086");
        param.put("SMS_PRIORITY", "3000");
        param.put("DEAL_STATE", "15");
        param.put("SEND_TIME_CODE", "1");
        param.put("SEND_OBJECT_CODE", "6");
        param.put("REFER_TIME", SysDateMgr.getSysTime());
        param.put("DEAL_TIME", SysDateMgr.getSysTime());
        param.put("MONTH", SysDateMgr.getCurMonth());
        param.put("DAY", SysDateMgr.getCurDay());
        param.put("DEAL_STAFFID", getVisit().getStaffId());
        param.put("DEAL_DEPARTID", getVisit().getDepartId());

        Dao.insert("ti_o_sms", param);
    }*/
	
	/**
	 * 主号添加成员时，对成员的校验
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IData checkGroupByMenber(IData input) throws Exception
	{
		IData resultData=new DataMap();
		resultData.put("RESULT_CODE", "0000");
		resultData.put("RESULT_INFO", "success");
		
		String mainSN = input.getString("SERIAL_NUMBER");
		String memberSN = input.getString("SERIAL_NUMBER_B");
		UcaData uca = UcaDataFactory.getNormalUca(mainSN);
		UcaData ucaB = UcaDataFactory.getNormalUca(memberSN);

		IDataset userList = UserInfoQry.getUnnormalUserInfoBySn(memberSN);
		if (IDataUtil.isNotEmpty(userList)) 
		{
			resultData.put("RESULT_CODE", "2998");
			resultData.put("RESULT_INFO", "该号码处于非正常状态！");
			return resultData;
		}
		
		//查询成员号码是否为宽带用户
        IDataset memberSet = WidenetInfoQry.getUserWidenetInfoBySerialNumber("KD_" + memberSN);
        if (IDataUtil.isNotEmpty(memberSet))
        {
            resultData.put("RESULT_CODE", "2998");
            resultData.put("RESULT_INFO", "成员号码必须是非宽带用户，不允许办理！");
            return resultData;
        }
        
        //剔除已存在成员
        IDataset memberList = RelaUUInfoQry.getRelationsByUserIdAndTypeAndRoleCodeB("61", ucaB.getUserId(), "2");
        if (IDataUtil.isNotEmpty(memberList)) 
        {
			resultData.put("RESULT_CODE", "2998");
			resultData.put("RESULT_INFO", "该成员已经存在组网关系，不允许办理！");
			return resultData;
		}
        
        //主副号码不能一致
        if (memberSN.equals(mainSN)) 
        {
			resultData.put("RESULT_CODE", "2998");
			resultData.put("RESULT_INFO", "主副号码一致，不允许办理！");
			return resultData;
		}
        
        IDataset mainList = RelaUUInfoQry.getRelationsByUserIdAndTypeAndRoleCodeB("61", uca.getUserId(), "1");
        String userIDA = "";
        if (IDataUtil.isNotEmpty(mainList)) {
        	 userIDA = mainList.getData(0).getString("USER_ID_A");
        	//组网关系不能超过3人
             IDataset allRelation = RelaUUInfoQry.qryAllRelation(userIDA);
             if (IDataUtil.isNotEmpty(allRelation) && allRelation.size()==3) 
             {
     			resultData.put("RESULT_CODE", "2998");
     			resultData.put("RESULT_INFO", "添加的成员数量已达到上限，不能再添加！");
     			return resultData;
     		}
		}
        
        
        //一卡付多号校验
		IDataset payMoreCards = RelaUUInfoQry.queryRelaUUBySnb(memberSN, "97");
		if(IDataUtil.isNotEmpty(payMoreCards))
		{
			resultData.put("RESULT_CODE", "2998");
			resultData.put("RESULT_INFO", "该成员存在【一卡付多号】关系，不允许办理！");
			return resultData;
		}
		
		//一卡双号校验
		IDataset payDoubleCards = RelaUUInfoQry.queryRelaUUBySnb(memberSN, "30");
		if(IDataUtil.isNotEmpty(payDoubleCards))
		{
			resultData.put("RESULT_CODE", "2998");
			resultData.put("RESULT_INFO", "该成员存在【一卡双号】关系，不允许办理！");
			return resultData;
		}
		
		//剔除TD固话（TDYD）、移动公话B（G009）、移动公话C（G014）、随e行（G005）等品牌。移动公话存在两个品牌
		if("TDYD".equals(ucaB.getBrandCode()) || "G005".equals(ucaB.getBrandCode()) 
			|| "G009".equals(ucaB.getBrandCode()) || "G014".equals(ucaB.getBrandCode()))
		{
			resultData.put("RESULT_CODE", "2998");
			resultData.put("RESULT_INFO", "该号码不满足参与条件，建议更换其他号码！");
			return resultData;
		}
		
		if (memberSN.startsWith("0898"))
        {
			resultData.put("RESULT_CODE", "2998");
			resultData.put("RESULT_INFO", "0898用户不允许办理！");
			return resultData;
        }
		
		IDataset snRelationSet = RelaUUInfoQry.queryRelaUUBySnb(memberSN,"56");
		if (IDataUtil.isNotEmpty(snRelationSet)) 
		{
			resultData.put("RESULT_CODE", "2998");
			resultData.put("RESULT_INFO", "该号码存在统一付费关系，不允许办理！");
			return resultData;
		}

		IData drParam = new DataMap();
		drParam.put("SUBSYS_CODE", "CSM");
		drParam.put("PARAM_ATTR", "8383");
		drParam.put("PARAM_CODE", "DR_DEPLOY_PRODUCT");
		drParam.put("USER_ID", ucaB.getUserId());
		IDataset discntList = RelaUUInfoQry.qryPrincipalDiscnt(drParam);
		if (IDataUtil.isNotEmpty(discntList)) 
		{
			resultData.put("RESULT_CODE", "2998");
			resultData.put("RESULT_INFO", "办理企业尊享套餐不允许办理多人约消用户！");
			return resultData;
		}
		
		return resultData;
	}
	
}
