package com.asiainfo.veris.crm.order.soa.group.groupofferremind;


import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UStaffInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;



public class RemindExpireEcGroupDiscntSVC extends CSBizService{

	private static final long serialVersionUID = 3518074013533042087L;

	/**
     * 集团产品短信到期提醒
     * 
     */
    public IDataset remindExpireEcGroupDiscnt(IData input) throws Exception
    {
    	IDataset returnDatas = new DatasetList();
    	IData returnData = new DataMap();
    	//查询参数表配置
    	IDataset paraInfos = queryCommParaByParaAttrAndParaCode("2018","2018HNPR");
    	for(int i=0;i<paraInfos.size();i++){
    		IData paraInfo = paraInfos.getData(i);
    		IDataUtil.chkParam(paraInfo, "PARA_CODE1");
        	IDataUtil.chkParam(paraInfo, "PARA_CODE2");
        	IDataUtil.chkParam(paraInfo, "PARA_CODE3");
        	IDataUtil.chkParam(paraInfo, "PARA_CODE4");
    		String smsContent = "";
    		String discntCode = paraInfo.getString("PARA_CODE1");//进行到期提醒的产品ID
    		String days = paraInfo.getString("PARA_CODE2");//到期提醒的天数
    		String smsInfoFlag = paraInfo.getString("PARA_CODE3");//短信模板标志
    		String nbIotProductId = paraInfo.getString("PARA_CODE4");//NB-IoT产品ID
    		String ecDiscntName = paraInfo.getString("PARAM_NAME","");
    		
    		//通过产品id查询出订购该产品的集团UU关系
    		//IDataset groupUUInfos = queryGroupUUByProductId(nbIotProductId);
    		IDataset groupUUInfos = queryGroupUserIdAByProductId(nbIotProductId);
    		for(int j=0;j<groupUUInfos.size();j++){
    			IData groupUUInfo = groupUUInfos.getData(j);
    			String userIdA = groupUUInfo.getString("USER_ID");
    			//通过集团user_id_a查询出成员user_id
    			//IDataset groupMemberUUInfos = queryGroupMemberUUByuserIdA(userIdA);
    			IDataset groupMemberUUInfos = queryGrpMebUUByuserIdA(userIdA);
    			if(DataUtils.isEmpty(groupMemberUUInfos)){
    				continue;
    			}
    			
    			for(int k=0;k<groupMemberUUInfos.size();k++)
    			{
    				String userIdB = groupMemberUUInfos.getData(k).getString("USER_ID_B");
        			//查询年包资费是否在配置天数后结束
        			IDataset discntInfos = queryGroupMemberDiscntByDiscntCodeAndUserIdAndDays(userIdB,discntCode,days);
        			//存在年包资费并且年包资费在配置天数后结束
        			if(DataUtils.isNotEmpty(discntInfos)){
        				//通过集团user_id_a查询出集团信息
        				IData groupInfo = UcaInfoQry.qryGrpInfoByUserId(userIdA);
        				String custManagerId = groupInfo.getString("CUST_MANAGER_ID");
        				String groupName = groupInfo.getString("CUST_NAME");
        				//String ecDiscntName = queryDiscntInfosByDiscntCode(discntCode).getData(0).getString("DISCNT_NAME");
        				String endDate = discntInfos.getData(0).getString("END_DATE");
        				//拼接短信模板
        				if("NB-IoT".equals(smsInfoFlag)){
        					smsContent = "您好，" + groupName + "订购的" + ecDiscntName + "将于" + endDate + "到期，";
        					smsContent = smsContent + "为了不影响客户的正常使用，请您联系客户续订套餐，感谢您的合作！中国移动";
        				}
        				//通过custManagerId查询客户经理信息
        				IData custmanagerinfos = UStaffInfoQry.qryCustManagerInfoByCustManagerId(custManagerId);
        				if (IDataUtil.isNotEmpty(custmanagerinfos)){
        					String serialNumber = custmanagerinfos.getString("SERIAL_NUMBER");
        					//下发提醒短信
        					IData smsInfo = new DataMap();
        					smsInfo.put("POST_PHONE", serialNumber);
        					smsInfo.put("CONTENT", smsContent);
        					sendSMS(smsInfo);
        				}
        			}
        			
    			}
    			
    		}
    	}
    	returnData.put("BIZ_CODE", "0000");
        returnData.put("BIZ_DESC", "操作成功");
        returnDatas.add(returnData);
	    return returnDatas;
    }

    /*
     * 短信下发
     * */
	public static void sendSMS(IData input) throws Exception
    
    {
    	// 拼短信表参数
    	IDataUtil.chkParam(input, "POST_PHONE");//发送短信的号码
    	IDataUtil.chkParam(input, "CONTENT");
 
        IData param = new DataMap();
        param.put("NOTICE_CONTENT", input.getString("CONTENT"));
        param.put("EPARCHY_CODE", input.getString("EPARCHY_CODE","0898"));
        param.put("IN_MODE_CODE", "0");
        param.put("RECV_OBJECT", input.getString("POST_PHONE"));
        param.put("RECV_ID", "99999999");
        param.put("REFER_STAFF_ID", input.getString("TRADE_STAFF_ID",""));
        param.put("REFER_DEPART_ID", input.getString("TRADE_DEPART_ID",""));
        param.put("REMARK", "产品到期提醒");
        String seq = SeqMgr.getSmsSendId();
        long seq_id = Long.parseLong(seq);
        param.put("SMS_NOTICE_ID", seq_id);
        param.put("PARTITION_ID", seq_id % 1000);
        param.put("SEND_COUNT_CODE", "1");
        param.put("REFERED_COUNT", "0");
        param.put("CHAN_ID", "C009");
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
        param.put("ISSTAT", "0");
        param.put("TIMEOUT", "0");

        Dao.insert("ti_o_sms", param);
    }
    
	/*
     * 通过paraAttr和paraCode查询commpara表
     * */
    private IDataset queryCommParaByParaAttrAndParaCode(String paraAttr,String paraCode) throws Exception{
    	IData param = new DataMap();
    	param.put("PARAM_ATTR", paraAttr);
    	param.put("PARAM_CODE", paraCode);
    	SQLParser parser = new SQLParser(param);
        parser.addSQL("SELECT P.PARAM_ATTR,");
        parser.addSQL("		  P.PARAM_CODE, ");
        parser.addSQL("		  P.PARAM_NAME, ");
        parser.addSQL("		  P.PARA_CODE1, ");
        parser.addSQL("		  P.PARA_CODE2, ");
        parser.addSQL("		  P.PARA_CODE3, ");
        parser.addSQL("		  P.PARA_CODE4 ");
        parser.addSQL("	 FROM TD_S_COMMPARA P ");
        parser.addSQL("		WHERE P.PARAM_ATTR = :PARAM_ATTR ");
        parser.addSQL("		AND P.PARAM_CODE = :PARAM_CODE ");
        parser.addSQL("		AND SYSDATE BETWEEN START_DATE AND END_DATE ");
        IDataset paraInfos = Dao.qryByParse(parser,Route.CONN_CRM_CEN);
    	return paraInfos;
    }

    /*
     * 查询优惠具体信息
     * */
    private IDataset queryDiscntInfosByDiscntCode(String discntCode) throws Exception{
    	IData param = new DataMap();
		param.put("DISCNT_CODE", discntCode);
		SQLParser parser = new SQLParser(param);
		parser.addSQL(" SELECT p.discnt_name ");
		parser.addSQL(" FROM td_b_discnt p ");
		parser.addSQL(" WHERE p.discnt_code = :DISCNT_CODE ");
		IDataset discntInfos = Dao.qryByParse(parser,Route.CONN_CRM_CEN);
		return discntInfos;
    }
    
    
    /*
     * 查询集团UU关系信息，通过product_id
     * */
    private IDataset queryGroupUUByProductId(String productId) throws Exception{
    	IData param = new DataMap();
		param.put("PRODUCT_ID", productId);
		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_BY_PRODUCTID", param);
    }
   
    private IDataset queryGroupUserIdAByProductId(String productId) throws Exception{
    	IData param = new DataMap();
		param.put("PRODUCT_ID", productId);
		return Dao.qryByCode("TF_F_USER_PRODUCT", "SEL_USERIDA_BY_PRODUCTID", param);
    }
    
    /*
     * 查询集团成员UU关系信息，通过集团user_id
     * */
    private IDataset queryGroupMemberUUByuserIdA(String userIdA) throws Exception{
    	IData param = new DataMap();
		param.put("USER_ID_A", userIdA);
		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_ALL_VALID_BY_USER_IDA", param);
    }
    
    /*
     * 查询集团成员UU关系信息，通过集团user_id
     * */
    private IDataset queryGrpMebUUByuserIdA(String userIdA) throws Exception{
    	IData param = new DataMap();
		param.put("USER_ID_A", userIdA);
		SQLParser parser = new SQLParser(param);
		parser.addSQL(" SELECT USER_ID_A, SERIAL_NUMBER_A, USER_ID_B, SERIAL_NUMBER_B ");
		parser.addSQL(" FROM TF_F_RELATION_UU ");
		parser.addSQL(" WHERE USER_ID_A = TO_NUMBER(:USER_ID_A) ");
		parser.addSQL("  AND END_DATE > SYSDATE ");
		return Dao.qryByParse(parser);
    }
    
    /*
     * 查询参数表配置的资费在配置天数后结束的记录
     * */
    private IDataset queryGroupMemberDiscntByDiscntCodeAndUserIdAndDays(String userId,String discntCode,String days) throws Exception{
    	IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("DISCNT_CODE", discntCode);
		//IDataset discntInfos = Dao.qryByCode("TF_F_USER_DISCNT", "SEL_BY_USERID_DISCNT_ALL", param);
		IDataset discntInfos = UserDiscntInfoQry.getAllDiscntByUser(userId,discntCode);
		IDataset Infos = new DatasetList();
	    for(int i = 0; i < discntInfos.size(); i++){
        	IData ecValue = discntInfos.getData(i);       	
        	String endDate =  ecValue.getString("END_DATE");
        	String sysDate = SysDateMgr.getSysDate();
        	if(SysDateMgr.dayInterval(sysDate,endDate)>Integer.parseInt(days)){
        		continue;
        	}        	
        	Infos.add(ecValue);     		
        }
		return Infos;
    }
    
}
