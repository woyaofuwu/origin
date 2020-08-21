package com.asiainfo.veris.crm.order.soa.person.busi.family.mfc;

import com.ailk.bizcommon.util.SysDateMgr;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.util.DataUtils;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.template.TemplateQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserNpInfoQry;

public class MfcCommonUtil
{
	public static final String PRODUCT_CODE_TF = "MFC000001";//全国亲情网（统付版）
	public static final String PRODUCT_CODE_ZF = "MFC000002";//全国亲情网（自付版）
	public static final String PRODUCT_CODE_5G3= "MFC000003";//5G家庭会员群组
	public static final String PRODUCT_CODE_5G4= "MFC000004";//5G家庭套餐群组
	public static final String PRODUCT_CODE_5G5= "MFC000005";//5G融合套餐群组

	public static final String PRODUCT_CODE_TF6= "MFC000006";//全国亲情网(支付宝版月包)
	public static final String PRODUCT_CODE_TF7= "MFC000007";//全国亲情网(支付宝版季包)
	public static final String PRODUCT_CODE_TF8= "MFC000008";//全国亲情网(支付宝版年包)
	public static final String PRODUCT_CODE_TF9= "MFC000009";//全国亲情网(异网版月包)
	public static final String PRODUCT_CODE_TF10= "MFC000010";//全国亲情网(异网版季包)
	public static final String PRODUCT_CODE_TF11= "MFC000011";//全国亲情网(异网版年包)

		public static final int MFC_FAMILY_COUNT = 10;//全国亲情网（自付版）
	
		public static IDataset getMeb(IData map) throws Exception{
			StringBuilder sql = new StringBuilder(1000);
			sql.append("UPDATE ");
			sql.append("TF_B_FAMILY_OTHER A ");
			sql.append("SET A.RSRV_STR10=:RSRV_STR10 ");
			sql.append("WHERE A.EXP_TIME>SYSDATE ");
			sql.append("AND A.CUSTOMER_PHONE=:CUSTOMER_PHONE ");
			sql.append("AND A.PRODUCT_CODE=:PRODUCT_CODE ");
			sql.append("AND A.FINISH_TAG='1' ");
			//更新订阅数据
			Dao.executeUpdate(sql, map, Route.CONN_CRM_CEN);
			 return Dao.qryByCodeParser("TF_B_FAMILY_OTHER", "SEL_MEMBER_01", map, Route.CONN_CRM_CEN);

		}
		public static IDataset getMebFu(IData map) throws Exception{
			StringBuilder sql = new StringBuilder(1000);
			sql.append("UPDATE ");
			sql.append("TF_B_FAMILY_OTHER A ");
			sql.append("SET A.RSRV_STR10=:RSRV_STR10 ");
			sql.append("WHERE A.EXP_TIME>SYSDATE ");
			sql.append("AND  A.MEM_NUMBER=:MEM_NUMBER ");
			sql.append("AND A.PRODUCT_CODE=:PRODUCT_CODE ");
			sql.append("AND A.FINISH_TAG='1' ");
			//更新订阅数据
			Dao.executeUpdate(sql, map, Route.CONN_CRM_CEN);
			 return Dao.qryByCodeParser("TF_B_FAMILY_OTHER", "SEL_MEMBER_02", map, Route.CONN_CRM_CEN);

		}
		public static IDataset getMebAll(IData map) throws Exception{
			
			StringBuilder sql = new StringBuilder(1000);
			sql.append("UPDATE ");
			sql.append("TF_B_FAMILY_OTHER A ");
			sql.append("SET A.RSRV_STR10=:RSRV_STR10 ");
			sql.append("WHERE A.EXP_TIME>SYSDATE ");
			sql.append("AND (A.CUSTOMER_PHONE=:CUSTOMER_PHONE OR A.MEM_NUMBER=:MEM_NUMBER) ");
			sql.append("AND A.PRODUCT_CODE=:PRODUCT_CODE ");
			sql.append("AND A.FINISH_TAG='1' ");
			//更新订阅数据
			Dao.executeUpdate(sql, map, Route.CONN_CRM_CEN);
			return Dao.qryByCodeParser("TF_B_FAMILY_OTHER", "SEL_MEMBER_03", map, Route.CONN_CRM_CEN);

		}
		
		public static IDataset getDataUp(IData map) throws Exception{
			
			 return Dao.qryByCodeParser("TF_B_FAMILY_OTHER", "SEL_MEMBER_04", map, Route.CONN_CRM_CEN);

		}
		
	//根据业务订购实例ID查询用户有效资费
	public static IDataset queryDiscntInfoByUserIdAndDisCode(String userId, String discntCode,String productOfferingID) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("DISCNT_CODE", discntCode);
        param.put("RSRV_STR1", productOfferingID);
        return Dao.qryByCodeParser("TF_F_USER_DISCNT", "SEL_BY_USERID_DISCNT_RSRV1", param);
    }
	
	public static IDataset getRelationUusByUserSnRole(String serialNumberB, String relationTypeCode, String roleCodeB ,IData inData) throws Exception {
		IData iparam = new DataMap();
		iparam.put("SERIAL_NUMBER_B", serialNumberB);
		iparam.put("RELATION_TYPE_CODE", relationTypeCode);
		iparam.put("ROLE_CODE_B", roleCodeB);
		if(DataUtils.isNotEmpty(inData))
		{//空判断，避免出现空指针
			iparam.put("PRODUCT_OFFERING_ID", inData.getString("PRODUCT_OFFERING_ID",""));
		}
		return Dao.qryByCodeParserAllCrm("TF_F_RELATION_UU", "SEL_USER_SN_ROLE_MF", iparam,null,true);
	}
	
	public static IDataset getSEL_USER_ROLEA(String userIdA, String roleCodeB, String relationTypeCode,IData inData) throws Exception {
		IData inparam = new DataMap();
		inparam.put("USER_ID_A", userIdA);
		inparam.put("ROLE_CODE_B", roleCodeB);
		inparam.put("RELATION_TYPE_CODE", relationTypeCode);
		if(DataUtils.isNotEmpty(inData))
		{//空判断，避免出现空指针
			inparam.put("PRODUCT_OFFERING_ID", inData.getString("PRODUCT_OFFERING_ID"));
		}
		return Dao.qryByCodeParserAllCrm("TF_F_RELATION_UU", "SEL_USER_ROLEA_MF", inparam,null, true);
	}
	
	/**
	 * 查询家庭网内无成员的(新办理家庭网且无同步添加成员号或已订购家庭网且删除最后一张成员号)
	 * @param roleCodeB
	 * @param relationTypeCode
	 * @return
	 * @throws Exception
	 */
	public static IDataset getSEL_USERIDA_NOMEMBER(String roleCodeB, String relationTypeCode) throws Exception {
		IData inparam = new DataMap();
		inparam.put("ROLE_CODE_B", roleCodeB);
		inparam.put("RELATION_TYPE_CODE", relationTypeCode);

		return Dao.qryByCodeParserAllCrm("TF_F_RELATION_UU", "SEL_ALL_BY_ROLEB_TYPE", inparam,null, true);
	}
	//根据TRADE_ID查询台账登记的UU表记录
	public static IDataset getUserRelationByTradeId(String tradeId) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("TRADE_ID", tradeId);
        return Dao.qryByCode("TF_B_TRADE_RELATION", "SEL_BY_PK", inparams,Route.getJourDb());
    }
	
	//跟新同步表记录
	public static void updateSync(IData input) throws Exception
	{
		SQLParser parser = new SQLParser(input);
		parser.addSQL(" UPDATE TI_B_MFC_SYNC SET ACTION = :ACTION ");
		parser.addSQL("  ,EXP_TIME = :EXP_TIME ");
		parser.addSQL("  ,OPR_TIME = :OPR_TIME ");	        
		parser.addSQL("  ,FINISH_TIME = :FINISH_TIME ");	        
		parser.addSQL("  ,RSRV_STR1 = :RSRV_STR1 ");	        
		parser.addSQL("  WHERE CUSTOMER_PHONE = :CUSTOMER_PHONE " );
		parser.addSQL("  and SEQ_ID=:SEQ_ID " );
		parser.addSQL("  and MEM_NUMBER = :MEM_NUMBER " );
		parser.addSQL("  and PRODUCT_OFFERING_ID = :PRODUCT_OFFERING_ID " );
		parser.addSQL("  and ACTION = :BEFORE_ACTION " );//复机需要条件
		parser.addSQL("  and to_date(EXP_TIME, 'yyyy-mm-dd hh24:mi:ss')> to_date(:SYS_DATE, 'yyyy-mm-dd hh24:mi:ss') " );//停机销户根据当前时间过滤
		Dao.executeUpdate(parser, Route.CONN_CRM_CEN);
	}
	    public static IData checkCount(String sn,String product) throws Exception{
 		IData result = new DataMap();
 		result.put("RSP_CODE", "00");
		result.put("RSP_DESC", "成功");
 		IDataset mebRelationUULists = MfcCommonUtil.getRelationUusByUserSnRole(sn,"MF",null,null);
 		if((IDataUtil.isNotEmpty(mebRelationUULists))){
 			int i=0;
 			int j=0;
			int x=0;
			int y=0;
			int z=0;
			int q=0,w=0,e=0,r=0,t=0,u=0;
 			for (int k=0; k< mebRelationUULists.size();k++){
				   if(StringUtils.contains(mebRelationUULists.getData(k).getString("REMARK"), "MFC000002")){
						i++;
					}else if(StringUtils.contains(mebRelationUULists.getData(k).getString("REMARK"), "MFC000003")) {
				   		x++;
				   }else if(StringUtils.contains(mebRelationUULists.getData(k).getString("REMARK"), "MFC000004"))
				   {
					    y++;
				   }else if(StringUtils.contains(mebRelationUULists.getData(k).getString("REMARK"), "MFC000005"))
				   {
					    z++;
				   }else if(StringUtils.contains(mebRelationUULists.getData(k).getString("REMARK"), "MFC000006")) {
					   q++;
				   }else if(StringUtils.contains(mebRelationUULists.getData(k).getString("REMARK"), "MFC000007"))
				   {
					   w++;
				   }else if(StringUtils.contains(mebRelationUULists.getData(k).getString("REMARK"), "MFC000008"))
				   {
					   e++;
				   }else if(StringUtils.contains(mebRelationUULists.getData(k).getString("REMARK"), "MFC000009")) {
					   r++;
				   }else if(StringUtils.contains(mebRelationUULists.getData(k).getString("REMARK"), "MFC000010"))
				   {
					   t++;
				   }else if(StringUtils.contains(mebRelationUULists.getData(k).getString("REMARK"), "MFC000011"))
				   {
					   u++;
				   }else{
						j++;
				   }
				}
				if("MFC000002".equals(product)&&i>=10){
					result.put("RSP_CODE", "99");
					result.put("RSP_DESC", "个付群组已达上限");
				}else if("MFC000001".equals(product)&&j>=10){
					result.put("RSP_CODE", "99");
					result.put("RSP_DESC", "统付群组已达上限");
				}else if("MFC000003".equals(product)&&x>=10){
					result.put("RSP_CODE", "99");
					result.put("RSP_DESC", "5G家庭会员群组");
				}else if("MFC000004".equals(product)&&y>=10){
					result.put("RSP_CODE", "99");
					result.put("RSP_DESC", "5G家庭套餐群组");
				}else if("MFC000005".equals(product)&&z>=10){
					result.put("RSP_CODE", "99");
					result.put("RSP_DESC", "5G融合套餐群组");
				}else if("MFC000006".equals(product)&&q>=10){
					result.put("RSP_CODE", "99");
					result.put("RSP_DESC", "全国亲情网(支付宝版月包)");
				}else if("MFC000007".equals(product)&&w>=10){
					result.put("RSP_CODE", "99");
					result.put("RSP_DESC", "全国亲情网(支付宝版季包)");
				}else if("MFC000008".equals(product)&&e>=10){
					result.put("RSP_CODE", "99");
					result.put("RSP_DESC", "全国亲情网(支付宝版年包)");
				}else if("MFC000009".equals(product)&&r>=10){
					result.put("RSP_CODE", "99");
					result.put("RSP_DESC", "全国亲情网(异网版月包)");
				}else if("MFC000010".equals(product)&&t>=10){
					result.put("RSP_CODE", "99");
					result.put("RSP_DESC", "全国亲情网(异网版季包)");
				}else if("MFC000011".equals(product)&&u>=10){
					result.put("RSP_CODE", "99");
					result.put("RSP_DESC", "全国亲情网(异网版年包)");
				}
			}
 		return result;
 	}
	    /**
		 * 通过userid查询np表，判断号码是不是携号转网用户
		 * @param userid
		 * @param 
		 * @return
		 */
	    public static IData checkNpNumber(String userid) throws Exception{
	    	IDataset idsNpUser = UserNpInfoQry.qryUserNpInfosByUserId(userid);
	    	IData result =new DataMap();
			if(IDataUtil.isNotEmpty(idsNpUser)) {
				IData idNpUser = idsNpUser.first();
				String strNpTag = idNpUser.getString("NP_TAG", "");
				if ("1".equals(strNpTag)) {
					result.put("RSP_CODE", "99");
					result.put("RSP_DESC", "该号码是携号转网用户");

				} else {
					result.put("RSP_CODE", "00");
					result.put("RSP_DESC", "该号码不是携号转网用户");
				}
			} else {
					result.put("RSP_CODE", "00");
					result.put("RSP_DESC", "该号码不是携号转网用户");
			}
					return result;
	    }

	/**
	 * 通过templateId获取到短信模板，并将其中变量替换
	 * @param templateId
	 * @param iData
	 * @return
	 */
	public static String getSmsContentByTemplateId(String templateId,IData iData) throws Exception{
		//根据模板ID获取短信
		IData smsTemplateData = TemplateQry.qryTemplateContentByTempateId(templateId);
		String content = "";
		if(IDataUtil.isNotEmpty(smsTemplateData)){
			content = smsTemplateData.getString("TEMPLATE_CONTENT1","");
		}
		if(IDataUtil.isEmpty(iData)){
			return content;
		}
		// 注意：短信模板的占位名需要和iData里的key保持一致
		String [] keyNames = iData.getNames();
		for(int i = 0; i<keyNames.length;i++){
			String keyName = keyNames[i];
			String regex = "@\\{"+keyName+"\\}";
			content = content.replaceAll(regex, iData.getString(keyName));
		}

		return content;
	}
	
	public static IDataset qryByParam134(String subsysCode, String paramAttr, String paramCode, String paraCode1, String paraCode3, String paraCode4) throws Exception
    {
        IData param = new DataMap();
        param.put("SUBSYS_CODE", subsysCode);
        param.put("PARAM_ATTR", paramAttr);
        param.put("PARAM_CODE", paramCode);
        param.put("PARA_CODE1", paraCode1);
        param.put("PARA_CODE3", paraCode3);
        param.put("PARA_CODE4", paraCode4);
        return Dao.qryByCodeParser("TD_S_COMMPARA", "SEL_BY_PARA_CODE1to4", param, Route.CONN_CRM_CEN);
    }
	/**
	 * 根据PRODUCT_OFFERING_ID、CUSTOMER_PHONE、FAMILY_TAG 查询TF_B_FAMILY_OTHER
	 * @param in
	 * @return
	 * @throws Exception
	 */
    public static IDataset qryFamilyOtherInfo(IData in) throws Exception{
		IData param = new DataMap();
		param.put("PRODUCT_OFFERING_ID", in.getString("PRODUCT_OFFERING_ID"));//业务实例ID
		param.put("CUSTOMER_PHONE", in.getString("CUSTOMER_PHONE"));//主号
		param.put("FINISH_TAG", in.getString("FINISH_TAG"));//有效标识

		if(StringUtils.isNotBlank(in.getString("MEM_NUMBER"))){
			param.put("MEM_NUMBER", in.getString("MEM_NUMBER"));
		}

		return  Dao.qryByCodeParser("TF_B_FAMILY_OTHER", "SEL_POI_PHONE", param, Route.CONN_CRM_CEN);
	}

	/**
	 * 校验入参
	 * @param data
	 * @param keyNamesStr
	 * @throws Exception
	 */
	public static void checkPramByKeys(IData data, String keyNamesStr) throws Exception
	{
		String keyNames[] = keyNamesStr.split(",");
		for (String strColName : keyNames)
		{
			IDataUtil.chkParam(data, strColName);
		}

	}
	public static void updateOtherInfo(IData inData) throws Exception{
		Dao.executeUpdateByCodeCode("TF_B_FAMILY_OTHER", "UPDATE_OTHER_OTHERDATA", inData, Route.CONN_CRM_CEN);

	}
	public static void updateStopOtherInfo(IData inData) throws Exception{
		Dao.executeUpdateByCodeCode("TF_B_FAMILY_OTHER", "UPDATE_OTHER_STOPDATA", inData, Route.CONN_CRM_CEN);

	}
	public static void updateStopOtherInfoS(IData inData) throws Exception{
		Dao.executeUpdateByCodeCode("TF_B_FAMILY_OTHER", "UPDATE_OTHER_STOPDATAS", inData, Route.CONN_CRM_CEN);

	}
	public static void updateAgainOtherInfo(IData inData) throws Exception{
		Dao.executeUpdateByCodeCode("TF_B_FAMILY_OTHER", "UPDATE_OTHER_AGAINDATA", inData, Route.CONN_CRM_CEN);

	}
	public static void updateAgainOtherInfoS(IData inData) throws Exception{
		Dao.executeUpdateByCodeCode("TF_B_FAMILY_OTHER", "UPDATE_OTHER_AGAINDATAS", inData, Route.CONN_CRM_CEN);

	}
	public static void updateMember(IData inData) throws Exception{
		Dao.executeUpdateByCodeCode("TF_B_FAMILY_OTHER", "UPDATE_MEMBER_DESTORY", inData, Route.CONN_CRM_CEN);

	}
	public static void updateOtherInfos(IData inData) throws Exception{
		Dao.executeUpdateByCodeCode("TF_B_FAMILY_OTHER", "UPDATE_OTHER_OTHERDATAS", inData, Route.CONN_CRM_CEN);

	}
	//通过poOrderNumber等查ACTION --新增sql1
	public static IDataset getPoidCodeByOfferingID( String productOfferingID, String custPhone,String roleCodeb ) throws Exception {
		IData iparam = new DataMap();
		iparam.put("PRODUCT_OFFERING_ID", productOfferingID);
		iparam.put("CUSTOMER_PHONE", custPhone);
		iparam.put("ROLE_CODE_B", roleCodeb);
        return Dao.qryByCodeParser("TF_B_FAMILY_OTHER", "SEL_POIDCODE_BY_OFFERINGID", iparam, Route.CONN_CRM_CEN);
	}
	public static IDataset getPoidCodeByPOORDERNUMBER( String custPhone,String poOrderNumber,String productOfferingId) throws Exception {
		IData iparam = new DataMap();
		iparam.put("CUSTOMER_PHONE", custPhone);
		iparam.put("PO_ORDER_NUMBER", poOrderNumber);
		iparam.put("PRODUCT_OFFERING_ID", productOfferingId);
        return Dao.qryByCodeParser("TF_B_FAMILY_OTHER", "SEL_POIDCODE_BY_POORDERNUMBER", iparam, Route.CONN_CRM_CEN);
	}
	public static IDataset getPoidCodeByMemberPoOrderNumber( String poOrderNumber, String memNumber ,String productOfferingId) throws Exception {
		IData iparam = new DataMap();
		iparam.put("PO_ORDER_NUMBER", poOrderNumber);
		iparam.put("MEM_NUMBER", memNumber);
		iparam.put("PRODUCT_OFFERING_ID", productOfferingId);
        return Dao.qryByCodeParser("TF_B_FAMILY_OTHER", "SEL_POIDCODE_BY_MEMNUMBERPONUMBER", iparam, Route.CONN_CRM_CEN);
	}
	//通过poOrderNumber等查ACTION --新增sql2
	public static IDataset getPoidCodeBymemnumber( String productOfferingID, String memnumber ) throws Exception {
		IData iparam = new DataMap();
		iparam.put("PRODUCT_OFFERING_ID", productOfferingID);
		iparam.put("MEM_NUMBER", memnumber);
        return Dao.qryByCodeParser("TF_B_FAMILY_OTHER", "SEL_POIDCODE_BY_MEMBERNUMBER", iparam, Route.CONN_CRM_CEN);
	}
	public static boolean oprDate(IData input, String action,String productofferingid,String custphone,String poOrderNumber) throws Exception {
		String poidCode = productofferingid.substring(16);
		String poLable ="群"+poidCode;
		if("50".equals(action)){
				IDataset updateinfo =input.getDataset("UPDATE_INFO");	
				if(IDataUtil.isNotEmpty(updateinfo)){
					for(int a=0;a<updateinfo.size();a++){
						IData otherInfo = new DataMap();
						String memLable = updateinfo.getData(a).getString("MEM_NUMBER").substring(7);
						otherInfo.put("PRODUCT_OFFERING_ID",updateinfo.getData(a).getString("PRODUCT_OFFERING_ID"));
						otherInfo.put("POID_CODE",updateinfo.getData(a).getString("POID_CODE",poidCode));
						otherInfo.put("POID_LABLE",updateinfo.getData(a).getString("POID_LABLE",poLable));
						otherInfo.put("MEM_LABLE",updateinfo.getData(a).getString("MEM_LABLE",memLable));
						otherInfo.put("MEM_NUMBER",updateinfo.getData(a).getString("MEM_NUMBER"));
						otherInfo.put("PO_ORDER_NUMBER",updateinfo.getData(a).getString("PO_ORDER_NUMBER"));
						otherInfo.put("FINISH_TAG","1");
						otherInfo.put("EFF_TIME",SysDateMgr.getSysDateYYYYMMDDHHMMSS() );
	        			otherInfo.put("EXP_TIME",SysDateMgr.END_TIME_FOREVER);
	            		otherInfo.put("ACTION",action);
						otherInfo.put("REMARK","新增成员");
						updateOtherInfos(otherInfo);//这里要新加一个sql  根据主号  po_order_number,产品编码 更新字段
					}	
		}else{//没有，新增一条记录
				IDataset insertinfo =input.getDataset("INSERT_INFO");			
				for(int b=0;b<insertinfo.size();b++){
					IData otherInfo = new DataMap();
					String memLable = insertinfo.getData(b).getString("MEM_NUMBER").substring(7);
					otherInfo.put("PO_ORDER_NUMBER",insertinfo.getData(b).getString("PO_ORDER_NUMBER"));
					otherInfo.put("PRODUCT_OFFERING_ID",insertinfo.getData(b).getString("PRODUCT_OFFERING_ID"));
					otherInfo.put("CUSTOMER_PHONE",custphone);
					otherInfo.put("ROLE_CODE_B","2");
        			otherInfo.put("UUID", SeqMgr.getInstId());
					otherInfo.put("POID_LABLE",insertinfo.getData(b).getString("POID_LABLE",poLable));
					otherInfo.put("POID_CODE",insertinfo.getData(b).getString("POID_CODE",poidCode));
					otherInfo.put("PRODUCT_CODE",insertinfo.getData(b).getString("PRODUCT_CODE"));
					otherInfo.put("MEM_LABLE",insertinfo.getData(b).getString("MEM_LABLE",memLable));
					otherInfo.put("MEM_NUMBER",insertinfo.getData(b).getString("MEM_NUMBER"));
					otherInfo.put("ADD_TIME",SysDateMgr.getSysTime());
					otherInfo.put("FINISH_TAG","1");//1--有效    2--失效
					otherInfo.put("EFF_TIME",SysDateMgr.getSysDateYYYYMMDDHHMMSS() );
        			otherInfo.put("EXP_TIME",SysDateMgr.END_TIME_FOREVER);
            		otherInfo.put("ACTION",action);
					otherInfo.put("REMARK","新增成员");
        			otherInfo.put("UUID",SeqMgr.getInstId());
					Dao.insert("TF_B_FAMILY_OTHER", otherInfo, Route.CONN_CRM_CEN);		
				} 

			}
		}else if("51".equals(action)){
				IDataset updateinfo =input.getDataset("UPDATE_INFO");	
				if(IDataUtil.isNotEmpty(updateinfo)){
					for(int a=0;a<updateinfo.size();a++){
						IData otherInfo = new DataMap();
						String memLable = updateinfo.getData(a).getString("MEM_NUMBER").substring(7);
						otherInfo.put("PRODUCT_OFFERING_ID",updateinfo.getData(a).getString("PRODUCT_OFFERING_ID"));
						otherInfo.put("POID_CODE",updateinfo.getData(a).getString("POID_CODE",poidCode));
						otherInfo.put("POID_LABLE",updateinfo.getData(a).getString("POID_LABLE",poLable));
						otherInfo.put("MEM_LABLE",updateinfo.getData(a).getString("MEM_LABLE",memLable));
						otherInfo.put("MEM_NUMBER",updateinfo.getData(a).getString("MEM_NUMBER"));
						otherInfo.put("CUSTOMER_PHONE",custphone);
						otherInfo.put("PO_ORDER_NUMBER",updateinfo.getData(a).getString("PO_ORDER_NUMBER"));
						otherInfo.put("FINISH_TAG","2");
						otherInfo.put("EFF_TIME",SysDateMgr.getSysDateYYYYMMDDHHMMSS() );
	        			otherInfo.put("EXP_TIME",SysDateMgr.getSysDateYYYYMMDDHHMMSS() );
	            		otherInfo.put("ACTION",action);
						otherInfo.put("REMARK","删除成员");
						updateDeleteInfos(otherInfo);//这里要新加一个sql  根据主号  po_order_number,产品编码 更新字段					
					}
				}else{
					//没有，新增一条记录
					IDataset insertinfo =input.getDataset("INSERT_INFO");			
					for(int b=0;b<insertinfo.size();b++){
						IData otherInfo = new DataMap();
						String memLable = insertinfo.getData(b).getString("MEM_NUMBER").substring(7);
						otherInfo.put("PO_ORDER_NUMBER",insertinfo.getData(b).getString("PO_ORDER_NUMBER"));
						otherInfo.put("PRODUCT_OFFERING_ID",insertinfo.getData(b).getString("PRODUCT_OFFERING_ID"));
						otherInfo.put("CUSTOMER_PHONE",custphone);
						otherInfo.put("ROLE_CODE_B","2");
						otherInfo.put("POID_LABLE",insertinfo.getData(b).getString("POID_LABLE",poLable));
						otherInfo.put("POID_CODE",insertinfo.getData(b).getString("POID_CODE",poidCode));
						otherInfo.put("PRODUCT_CODE",insertinfo.getData(b).getString("PRODUCT_CODE"));
						otherInfo.put("MEM_LABLE",insertinfo.getData(b).getString("MEM_LABLE",memLable));
						otherInfo.put("MEM_NUMBER",insertinfo.getData(b).getString("MEM_NUMBER"));
						otherInfo.put("ADD_TIME",SysDateMgr.getSysTime());
						otherInfo.put("FINISH_TAG","2");//1--有效    2--失效
						otherInfo.put("EFF_TIME",SysDateMgr.getSysDateYYYYMMDDHHMMSS() );
	        			otherInfo.put("EXP_TIME",SysDateMgr.getSysDateYYYYMMDDHHMMSS() );
	            		otherInfo.put("ACTION",action);
						otherInfo.put("REMARK","删除成员");
	        			otherInfo.put("UUID",SeqMgr.getInstId());
						Dao.insert("TF_B_FAMILY_OTHER", otherInfo, Route.CONN_CRM_CEN);	
				}
				}		
		}

		return true;

	}
	/**
	 * 更新TF_B_FAMILY_OTHER
	 * @param in
	 * @throws Exception
	 */
	public static int updFamilyOtherInfo(IData in) throws Exception
	{
		IData param=new DataMap();

		if(StringUtils.isNotBlank(in.getString("POID_CODE"))){
			param.put("POID_CODE", in.getString("POID_CODE"));
		}
		if(StringUtils.isNotBlank(in.getString("POID_LABLE"))){
			param.put("POID_LABLE", in.getString("POID_LABLE"));
		}
		if(StringUtils.isNotBlank(in.getString("MEM_LABLE"))){
			param.put("MEM_LABLE", in.getString("MEM_LABLE"));
		}

		param.put("UUID", in.getString("UUID"));
		param.put("RSRV_STR1", in.getString("RSRV_STR1"));
		param.put("RSRV_STR2", in.getString("RSRV_STR2"));
		param.put("RSRV_STR3", in.getString("RSRV_STR3"));
		param.put("ADD_TIME", SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND));
		StringBuilder sql = new StringBuilder(1000);
		sql.append("update ");
		sql.append("TF_B_FAMILY_OTHER  m ");
		sql.append("set  m.POID_CODE= :POID_CODE, ");
		sql.append("m.POID_LABLE= :POID_LABLE , ");
		sql.append("m.ADD_TIME= TO_DATE(:ADD_TIME, 'YYYY-MM-DD HH24:MI:SS') , ");
		sql.append("m.RSRV_STR1= :RSRV_STR1 , ");
		sql.append("m.RSRV_STR2= :RSRV_STR2 , ");
		sql.append("m.RSRV_STR3= :RSRV_STR3 , ");
		sql.append("m.MEM_LABLE= :MEM_LABLE ");
		sql.append("WHERE m.UUID = :UUID ");

		return Dao.executeUpdate(sql, param, Route.CONN_CRM_CEN);
	}
	/**
	 * 通过主号+PRODUCT_OFFERING_ID 或者副号来获取POID_CODE ,POID_LABLE或者MEM_LABLE
	 */
	public static IData getPoidInfoAndLable(String custPhone ,String PRODUCT_OFFERING_ID,String memNumber)throws Exception{
		IData in = new DataMap();
		IData output = new DataMap();

		String poidCode = "";
		if(StringUtils.isNotBlank(PRODUCT_OFFERING_ID)){
			poidCode =  PRODUCT_OFFERING_ID.substring(16);//默认 PRODUCT_OFFERING_ID 后两位
			output.put("POID_CODE",poidCode);
			output.put("POID_LABLE","群"+poidCode);
		}
		if(StringUtils.isNotBlank(memNumber)){
			output.put("MEM_LABLE",memNumber.substring(7));
		}
		in.put("PRODUCT_OFFERING_ID",PRODUCT_OFFERING_ID);
		in.put("CUSTOMER_PHONE",custPhone);
		in.put("FINISH_TAG","1");
		in.put("MEM_NUMBER",memNumber);
		IDataset qryOtherinfo = qryFamilyOtherInfo(in);
		if (IDataUtil.isNotEmpty(qryOtherinfo)){
			for(int i=0;i<qryOtherinfo.size();i++){
				if(StringUtils.isNotBlank(memNumber)){//副号有值，查询应该是只有一条有效记录
					if(StringUtils.isNotBlank(qryOtherinfo.getData(i).getString("MEM_LABLE"))){
						output.put("MEM_LABLE",qryOtherinfo.getData(i).getString("MEM_LABLE"));
					}
					if(StringUtils.isNotBlank(qryOtherinfo.getData(i).getString("POID_CODE"))){
						output.put("POID_CODE",qryOtherinfo.getData(i).getString("POID_CODE"));
					}
					if(StringUtils.isNotBlank(qryOtherinfo.getData(i).getString("POID_LABLE"))){
						output.put("POID_LABLE",qryOtherinfo.getData(i).getString("POID_LABLE"));
					}
				}else{
					//没有副号，可以查出整个家庭的记录，以主号的记录为主
					if ("1".equals(qryOtherinfo.getData(i).getString("ROLE_CODE_B"))){
						if(StringUtils.isNotBlank(qryOtherinfo.getData(i).getString("POID_CODE"))){
							output.put("POID_CODE",qryOtherinfo.getData(i).getString("POID_CODE"));
						}
						if(StringUtils.isNotBlank(qryOtherinfo.getData(i).getString("POID_LABLE"))){
							output.put("POID_LABLE",qryOtherinfo.getData(i).getString("POID_LABLE"));
						}
					}
				}

			}
		}
		return  output;
	}
	public static void updateDestoryInfo(IData inData) throws Exception{
		Dao.executeUpdateByCodeCode("TF_B_FAMILY_OTHER", "UPDATE_DESTORY_DATAS", inData, Route.CONN_CRM_CEN);

	}
	public static void updateDeleteInfos(IData inData) throws Exception{
		Dao.executeUpdateByCodeCode("TF_B_FAMILY_OTHER", "UPDATE_OTHER_DELETEDATAS", inData, Route.CONN_CRM_CEN);

	}
	public static IDataset selMemberinfo( String productOfferingID, String custPhone,String memNumber ) throws Exception {
		IData iparam = new DataMap();
		iparam.put("PRODUCT_OFFERING_ID", productOfferingID);
		iparam.put("CUSTOMER_PHONE", custPhone);
		iparam.put("MEM_NUMBER", memNumber);

        return Dao.qryByCodeParser("TF_B_FAMILY_OTHER", "SEL_MEMBER_INFO", iparam, Route.CONN_CRM_CEN);
	}
	public static IDataset queryProductCodeByUserId(String userId,String productofferingId ,String realationTyprCode,String discntCode) throws Exception
    {
		IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("RSRV_STR1", productofferingId);
        param.put("RELATION_TYPE_CODE", realationTyprCode);
        param.put("DISCNT_CODE", discntCode);

        return Dao.qryByCodeParser("TF_F_USER_DISCNT", "SEL_BY_USERID_DISCNT_RSRV2", param);
    }
	public static IDataset selPoidCodeByPOORDERNUMBER( String custPhone,String poOrderNumber,String productOfferingId) throws Exception {
		IData iparam = new DataMap();
		iparam.put("CUSTOMER_PHONE", custPhone);
		iparam.put("PO_ORDER_NUMBER", poOrderNumber);
		iparam.put("PRODUCT_OFFERING_ID", productOfferingId);
        return Dao.qryByCodeParser("TF_B_FAMILY_OTHER", "SEL_POIDCODE_BY_NUMBER", iparam, Route.CONN_CRM_CEN);
	}
	
	/**
	 *EFF_TIME失效时间倒序
	 * @param productOfferingID
	 * @param custPhone
	 * @return
	 * @throws Exception
	 */
	public static IDataset qryMemberinfo( String productOfferingID, String custPhone ) throws Exception {
		IData iparam = new DataMap();
		iparam.put("PRODUCT_OFFERING_ID", productOfferingID);
		iparam.put("CUSTOMER_PHONE", custPhone);

		return Dao.qryByCodeParser("TF_B_FAMILY_OTHER", "SEL_FAMILY_EFFDATE", iparam, Route.CONN_CRM_CEN);
	}
	/**
	 * 没有时间，end_date倒序
	 * @param serialNumberB
	 * @param relationTypeCode
	 * @param roleCodeB
	 * @param inData
	 * @return
	 * @throws Exception
	 */
	public static IDataset getRelationUusBySnRb(String serialNumberB, String relationTypeCode, String roleCodeB ,IData inData) throws Exception {
		IData iparam = new DataMap();
		iparam.put("SERIAL_NUMBER_B", serialNumberB);
		iparam.put("RELATION_TYPE_CODE", relationTypeCode);
		iparam.put("ROLE_CODE_B", roleCodeB);
		if(DataUtils.isNotEmpty(inData))
		{//空判断，避免出现空指针
			iparam.put("PRODUCT_OFFERING_ID", inData.getString("PRODUCT_OFFERING_ID"));
		}
		return Dao.qryByCodeParserAllCrm("TF_F_RELATION_UU", "SEL_USER_SN_RB_MF", iparam,null,true);
	}
	
	
    
    /**
     * 
     * @param operType  操作类型
     * @return
     * @throws Exception
     */
    public static String getSmsInfo(int operType,IData param ,String productCode) throws Exception{
    	String res ="";
    	IDataset config = CommparaInfoQry.getCommparaAllCol("CSM","2018","MCF_SEND_SMS", "ZZZZ");

		IDataset fgconfig = CommparaInfoQry.getCommparaAllCol("CSM","2018","MCF_SEND_5GSMS", "ZZZZ");

		IDataset zfbconfig = CommparaInfoQry.getCommparaAllCol("CSM","2018","MCF_SEND_ZFBSMS", "ZZZZ");

		IDataset ywconfig = CommparaInfoQry.getCommparaAllCol("CSM","2018","MCF_SEND_YWSMS", "ZZZZ");

		IData iData = new DataMap();
    	String templateId = "";
    	switch (operType) {
    	case 1:
			 // 主号成功办理组网
			if(MfcCommonUtil.PRODUCT_CODE_TF.equals(productCode)){//统付
				templateId = config.getData(0).getString("PARA_CODE7","");
			}else if(MfcCommonUtil.PRODUCT_CODE_ZF.equals(productCode)){//自付
				templateId = config.getData(0).getString("PARA_CODE8","");
			}else if(MfcCommonUtil.PRODUCT_CODE_5G3.equals(productCode)){//5G家庭会员群组
				templateId = fgconfig.getData(0).getString("PARA_CODE10","");
			}else if(MfcCommonUtil.PRODUCT_CODE_5G4.equals(productCode)){//5G家庭套餐群组
				templateId = fgconfig.getData(0).getString("PARA_CODE10","");
			}else if(MfcCommonUtil.PRODUCT_CODE_5G5.equals(productCode)){//5G融合套餐群组
				templateId = fgconfig.getData(0).getString("PARA_CODE10","");
			}else if(MfcCommonUtil.PRODUCT_CODE_TF6.equals(productCode)){//全国亲情网(支付宝版月包)
				templateId = zfbconfig.getData(0).getString("PARA_CODE10","");
			}else if(MfcCommonUtil.PRODUCT_CODE_TF7.equals(productCode)){//全国亲情网(支付宝版季包)
				templateId = zfbconfig.getData(0).getString("PARA_CODE22","");
			}else if(MfcCommonUtil.PRODUCT_CODE_TF8.equals(productCode)){//全国亲情网(支付宝版年包)
				templateId = zfbconfig.getData(0).getString("PARA_CODE23","");
			}else if(MfcCommonUtil.PRODUCT_CODE_TF9.equals(productCode)){//全国亲情网(异网版月包)
				templateId = ywconfig.getData(0).getString("PARA_CODE10","");
			}else if(MfcCommonUtil.PRODUCT_CODE_TF10.equals(productCode)){//全国亲情网(异网版季包)
				templateId = ywconfig.getData(0).getString("PARA_CODE22","");
			}else if(MfcCommonUtil.PRODUCT_CODE_TF11.equals(productCode)){//全国亲情网(异网版年包)
				templateId = ywconfig.getData(0).getString("PARA_CODE23","");
			}
			iData.putAll(param);
			res =MfcCommonUtil.getSmsContentByTemplateId(templateId,iData);
			break;
    	case 2:
			// 主号取消全国亲情网 成员号短信
			if(MfcCommonUtil.PRODUCT_CODE_TF.equals(productCode)){//统付
				templateId = config.getData(0).getString("PARA_CODE11","");
			}else if(MfcCommonUtil.PRODUCT_CODE_ZF.equals(productCode)){//自付
				templateId = config.getData(0).getString("PARA_CODE12","");
			}else if(MfcCommonUtil.PRODUCT_CODE_5G3.equals(productCode)){//5G家庭会员群组
				templateId = fgconfig.getData(0).getString("PARA_CODE11","");
			}else if(MfcCommonUtil.PRODUCT_CODE_5G4.equals(productCode)){//5G家庭套餐群组
				templateId = fgconfig.getData(0).getString("PARA_CODE11","");
			}else if(MfcCommonUtil.PRODUCT_CODE_5G5.equals(productCode)){//5G融合套餐群组
				templateId = fgconfig.getData(0).getString("PARA_CODE11","");
			}else if(MfcCommonUtil.PRODUCT_CODE_TF6.equals(productCode)){//全国亲情网(支付宝版月包)
				templateId = zfbconfig.getData(0).getString("PARA_CODE11","");
			}else if(MfcCommonUtil.PRODUCT_CODE_TF7.equals(productCode)){//全国亲情网(支付宝版季包)
				templateId = zfbconfig.getData(0).getString("PARA_CODE11","");
			}else if(MfcCommonUtil.PRODUCT_CODE_TF8.equals(productCode)){//全国亲情网(支付宝版年包)
				templateId = zfbconfig.getData(0).getString("PARA_CODE11","");
			}else if(MfcCommonUtil.PRODUCT_CODE_TF9.equals(productCode)){//全国亲情网(异网版月包)
				templateId = ywconfig.getData(0).getString("PARA_CODE11","");
			}else if(MfcCommonUtil.PRODUCT_CODE_TF10.equals(productCode)){//全国亲情网(异网版季包)
				templateId = ywconfig.getData(0).getString("PARA_CODE11","");
			}else if(MfcCommonUtil.PRODUCT_CODE_TF11.equals(productCode)){//全国亲情网(异网版年包)
				templateId = ywconfig.getData(0).getString("PARA_CODE11","");
			}
			iData.putAll(param);
			res =MfcCommonUtil.getSmsContentByTemplateId(templateId,iData);
			break;
		case 3:
			//主号成功添加成员号-被添加副号短信
			if(MfcCommonUtil.PRODUCT_CODE_TF.equals(productCode)){//统付
				templateId = config.getData(0).getString("PARA_CODE15","");
			}else if(MfcCommonUtil.PRODUCT_CODE_ZF.equals(productCode)){//自付
				templateId = config.getData(0).getString("PARA_CODE16","");
			}else if(MfcCommonUtil.PRODUCT_CODE_5G3.equals(productCode)){//5G家庭会员群组
				templateId = fgconfig.getData(0).getString("PARA_CODE12","");
			}else if(MfcCommonUtil.PRODUCT_CODE_5G4.equals(productCode)){//5G家庭套餐群组
				templateId = fgconfig.getData(0).getString("PARA_CODE12","");
			}else if(MfcCommonUtil.PRODUCT_CODE_5G5.equals(productCode)){//5G融合套餐群组
				templateId = fgconfig.getData(0).getString("PARA_CODE12","");
			}else if(MfcCommonUtil.PRODUCT_CODE_TF6.equals(productCode)){//全国亲情网(支付宝版月包)
				templateId = zfbconfig.getData(0).getString("PARA_CODE12","");
			}else if(MfcCommonUtil.PRODUCT_CODE_TF7.equals(productCode)){//全国亲情网(支付宝版季包)
				templateId = zfbconfig.getData(0).getString("PARA_CODE12","");
			}else if(MfcCommonUtil.PRODUCT_CODE_TF8.equals(productCode)){//全国亲情网(支付宝版年包)
				templateId = zfbconfig.getData(0).getString("PARA_CODE12","");
			}else if(MfcCommonUtil.PRODUCT_CODE_TF9.equals(productCode)){//全国亲情网(异网版月包)
				templateId = ywconfig.getData(0).getString("PARA_CODE12","");
			}else if(MfcCommonUtil.PRODUCT_CODE_TF10.equals(productCode)){//全国亲情网(异网版季包)
				templateId = ywconfig.getData(0).getString("PARA_CODE12","");
			}else if(MfcCommonUtil.PRODUCT_CODE_TF11.equals(productCode)){//全国亲情网(异网版年包)
				templateId = ywconfig.getData(0).getString("PARA_CODE12","");
			}
			iData.putAll(param);
			res =MfcCommonUtil.getSmsContentByTemplateId(templateId,iData);
			break;
		case 4:
			//主号成功添加成员号-其他副号短信
			if(MfcCommonUtil.PRODUCT_CODE_TF.equals(productCode)){//统付
				templateId = config.getData(0).getString("PARA_CODE17","");
			}else if(MfcCommonUtil.PRODUCT_CODE_ZF.equals(productCode)){//自付
				templateId = config.getData(0).getString("PARA_CODE18","");
			}else if(MfcCommonUtil.PRODUCT_CODE_5G3.equals(productCode)){//5G家庭会员群组
				templateId = fgconfig.getData(0).getString("PARA_CODE13","");
			}else if(MfcCommonUtil.PRODUCT_CODE_5G4.equals(productCode)){//5G家庭套餐群组
				templateId = fgconfig.getData(0).getString("PARA_CODE13","");
			}else if(MfcCommonUtil.PRODUCT_CODE_5G5.equals(productCode)){//5G融合套餐群组
				templateId = fgconfig.getData(0).getString("PARA_CODE13","");
			}else if(MfcCommonUtil.PRODUCT_CODE_TF6.equals(productCode)){//全国亲情网(支付宝版月包)
				templateId = zfbconfig.getData(0).getString("PARA_CODE13","");
			}else if(MfcCommonUtil.PRODUCT_CODE_TF7.equals(productCode)){//全国亲情网(支付宝版季包)
				templateId = zfbconfig.getData(0).getString("PARA_CODE13","");
			}else if(MfcCommonUtil.PRODUCT_CODE_TF8.equals(productCode)){//全国亲情网(支付宝版年包)
				templateId = zfbconfig.getData(0).getString("PARA_CODE13","");
			}else if(MfcCommonUtil.PRODUCT_CODE_TF9.equals(productCode)){//全国亲情网(异网版月包)
				templateId = ywconfig.getData(0).getString("PARA_CODE13","");
			}else if(MfcCommonUtil.PRODUCT_CODE_TF10.equals(productCode)){//全国亲情网(异网版季包)
				templateId = ywconfig.getData(0).getString("PARA_CODE13","");
			}else if(MfcCommonUtil.PRODUCT_CODE_TF11.equals(productCode)){//全国亲情网(异网版年包)
				templateId = ywconfig.getData(0).getString("PARA_CODE13","");
			}
			iData.putAll(param);
			res =MfcCommonUtil.getSmsContentByTemplateId(templateId,iData);
			break;
		case 5:
			// 主号删减成员号或成员号主动退出 被删减副号短信
			if(MfcCommonUtil.PRODUCT_CODE_TF.equals(productCode)){//统付
				templateId = config.getData(0).getString("PARA_CODE21","");
			}else if(MfcCommonUtil.PRODUCT_CODE_ZF.equals(productCode)){//自付
				templateId = config.getData(0).getString("PARA_CODE22","");
			}else if(MfcCommonUtil.PRODUCT_CODE_5G3.equals(productCode)){//5G家庭会员群组
				templateId = fgconfig.getData(0).getString("PARA_CODE14","");
			}else if(MfcCommonUtil.PRODUCT_CODE_5G4.equals(productCode)){//5G家庭套餐群组
				templateId = fgconfig.getData(0).getString("PARA_CODE14","");
			}else if(MfcCommonUtil.PRODUCT_CODE_5G5.equals(productCode)){//5G融合套餐群组
				templateId = fgconfig.getData(0).getString("PARA_CODE14","");
			}else if(MfcCommonUtil.PRODUCT_CODE_TF6.equals(productCode)){//全国亲情网(支付宝版月包)
				templateId = zfbconfig.getData(0).getString("PARA_CODE14","");
			}else if(MfcCommonUtil.PRODUCT_CODE_TF7.equals(productCode)){//全国亲情网(支付宝版季包)
				templateId = zfbconfig.getData(0).getString("PARA_CODE14","");
			}else if(MfcCommonUtil.PRODUCT_CODE_TF8.equals(productCode)){//全国亲情网(支付宝版年包)
				templateId = zfbconfig.getData(0).getString("PARA_CODE14","");
			}else if(MfcCommonUtil.PRODUCT_CODE_TF9.equals(productCode)){//全国亲情网(异网版月包)
				templateId = ywconfig.getData(0).getString("PARA_CODE14","");
			}else if(MfcCommonUtil.PRODUCT_CODE_TF10.equals(productCode)){//全国亲情网(异网版季包)
				templateId = ywconfig.getData(0).getString("PARA_CODE14","");
			}else if(MfcCommonUtil.PRODUCT_CODE_TF11.equals(productCode)){//全国亲情网(异网版年包)
				templateId = ywconfig.getData(0).getString("PARA_CODE14","");
			}
			iData.putAll(param);
			res =MfcCommonUtil.getSmsContentByTemplateId(templateId,iData);
			break;
		case 6:
			// 主号删减成员号或成员号主动退出 其他副号短信
			if(MfcCommonUtil.PRODUCT_CODE_TF.equals(productCode)){//统付
				templateId = config.getData(0).getString("PARA_CODE23","");
			}else if(MfcCommonUtil.PRODUCT_CODE_ZF.equals(productCode)){//自付
				templateId = config.getData(0).getString("PARA_CODE24","");
			}else if(MfcCommonUtil.PRODUCT_CODE_5G3.equals(productCode)){//5G家庭会员群组
				templateId = fgconfig.getData(0).getString("PARA_CODE15","");
			}else if(MfcCommonUtil.PRODUCT_CODE_5G4.equals(productCode)){//5G家庭套餐群组
				templateId = fgconfig.getData(0).getString("PARA_CODE15","");
			}else if(MfcCommonUtil.PRODUCT_CODE_5G5.equals(productCode)){//5G融合套餐群组
				templateId = fgconfig.getData(0).getString("PARA_CODE15","");
			}else if(MfcCommonUtil.PRODUCT_CODE_TF6.equals(productCode)){//全国亲情网(支付宝版月包)
				templateId = zfbconfig.getData(0).getString("PARA_CODE15","");
			}else if(MfcCommonUtil.PRODUCT_CODE_TF7.equals(productCode)){//全国亲情网(支付宝版季包)
				templateId = zfbconfig.getData(0).getString("PARA_CODE15","");
			}else if(MfcCommonUtil.PRODUCT_CODE_TF8.equals(productCode)){//全国亲情网(支付宝版年包)
				templateId = zfbconfig.getData(0).getString("PARA_CODE15","");
			}else if(MfcCommonUtil.PRODUCT_CODE_TF9.equals(productCode)){//全国亲情网(异网版月包)
				templateId = ywconfig.getData(0).getString("PARA_CODE15","");
			}else if(MfcCommonUtil.PRODUCT_CODE_TF10.equals(productCode)){//全国亲情网(异网版季包)
				templateId = ywconfig.getData(0).getString("PARA_CODE15","");
			}else if(MfcCommonUtil.PRODUCT_CODE_TF11.equals(productCode)){//全国亲情网(异网版年包)
				templateId = ywconfig.getData(0).getString("PARA_CODE15","");
			}
			iData.putAll(param);
			res =MfcCommonUtil.getSmsContentByTemplateId(templateId,iData);
			break;
		case 7:
			// 主号成功添加成员号 主号短信
			if(MfcCommonUtil.PRODUCT_CODE_TF.equals(productCode)){//统付
				templateId = config.getData(0).getString("PARA_CODE13","");
			}else if(MfcCommonUtil.PRODUCT_CODE_ZF.equals(productCode)){//自付
				templateId = config.getData(0).getString("PARA_CODE14","");
			}else if(MfcCommonUtil.PRODUCT_CODE_5G3.equals(productCode)){//5G家庭会员群组
				templateId = fgconfig.getData(0).getString("PARA_CODE16","");
			}else if(MfcCommonUtil.PRODUCT_CODE_5G4.equals(productCode)){//5G家庭套餐群组
				templateId = fgconfig.getData(0).getString("PARA_CODE16","");
			}else if(MfcCommonUtil.PRODUCT_CODE_5G5.equals(productCode)){//5G融合套餐群组
				templateId = fgconfig.getData(0).getString("PARA_CODE16","");
			}else if(MfcCommonUtil.PRODUCT_CODE_TF6.equals(productCode)){//全国亲情网(支付宝版月包)
				templateId = zfbconfig.getData(0).getString("PARA_CODE16","");
			}else if(MfcCommonUtil.PRODUCT_CODE_TF7.equals(productCode)){//全国亲情网(支付宝版季包)
				templateId = zfbconfig.getData(0).getString("PARA_CODE16","");
			}else if(MfcCommonUtil.PRODUCT_CODE_TF8.equals(productCode)){//全国亲情网(支付宝版年包)
				templateId = zfbconfig.getData(0).getString("PARA_CODE16","");
			}else if(MfcCommonUtil.PRODUCT_CODE_TF9.equals(productCode)){//全国亲情网(异网版月包)
				templateId = ywconfig.getData(0).getString("PARA_CODE16","");
			}else if(MfcCommonUtil.PRODUCT_CODE_TF10.equals(productCode)){//全国亲情网(异网版季包)
				templateId = ywconfig.getData(0).getString("PARA_CODE24","");
			}else if(MfcCommonUtil.PRODUCT_CODE_TF11.equals(productCode)){//全国亲情网(异网版年包)
				templateId = ywconfig.getData(0).getString("PARA_CODE25","");
			}
			iData.putAll(param);
			res =MfcCommonUtil.getSmsContentByTemplateId(templateId,iData);
			break;
		case 8:
			// 主号删减成员号或成员号主动退出 主号短信
			if(MfcCommonUtil.PRODUCT_CODE_TF.equals(productCode)){//统付
				templateId = config.getData(0).getString("PARA_CODE19","");
			}else if(MfcCommonUtil.PRODUCT_CODE_ZF.equals(productCode)){//自付
				templateId = config.getData(0).getString("PARA_CODE20","");
			}else if(MfcCommonUtil.PRODUCT_CODE_5G3.equals(productCode)){//5G家庭会员群组
				templateId = fgconfig.getData(0).getString("PARA_CODE17","");
			}else if(MfcCommonUtil.PRODUCT_CODE_5G4.equals(productCode)){//5G家庭套餐群组
				templateId = fgconfig.getData(0).getString("PARA_CODE17","");
			}else if(MfcCommonUtil.PRODUCT_CODE_5G5.equals(productCode)){//5G融合套餐群组
				templateId = fgconfig.getData(0).getString("PARA_CODE17","");
			}else if(MfcCommonUtil.PRODUCT_CODE_TF6.equals(productCode)){//全国亲情网(支付宝版月包)
				templateId = zfbconfig.getData(0).getString("PARA_CODE17","");
			}else if(MfcCommonUtil.PRODUCT_CODE_TF7.equals(productCode)){//全国亲情网(支付宝版季包)
				templateId = zfbconfig.getData(0).getString("PARA_CODE17","");
			}else if(MfcCommonUtil.PRODUCT_CODE_TF8.equals(productCode)){//全国亲情网(支付宝版年包)
				templateId = zfbconfig.getData(0).getString("PARA_CODE17","");
			}else if(MfcCommonUtil.PRODUCT_CODE_TF9.equals(productCode)){//全国亲情网(异网版月包)
				templateId = ywconfig.getData(0).getString("PARA_CODE17","");
			}else if(MfcCommonUtil.PRODUCT_CODE_TF10.equals(productCode)){//全国亲情网(异网版季包)
				templateId = zfbconfig.getData(0).getString("PARA_CODE24","");
			}else if(MfcCommonUtil.PRODUCT_CODE_TF11.equals(productCode)){//全国亲情网(异网版年包)
				templateId = zfbconfig.getData(0).getString("PARA_CODE25","");
			}
			iData.putAll(param);
			res =MfcCommonUtil.getSmsContentByTemplateId(templateId,iData);
			break;
		case 9:
			// 取消全国亲情网主号短信
			if(MfcCommonUtil.PRODUCT_CODE_TF.equals(productCode)){//统付
				templateId = config.getData(0).getString("PARA_CODE9","");
			}else if(MfcCommonUtil.PRODUCT_CODE_ZF.equals(productCode)){//自付
				templateId = config.getData(0).getString("PARA_CODE10","");
			}else if(MfcCommonUtil.PRODUCT_CODE_5G3.equals(productCode)){//5G家庭会员群组
				templateId = fgconfig.getData(0).getString("PARA_CODE18","");
			}else if(MfcCommonUtil.PRODUCT_CODE_5G4.equals(productCode)){//5G家庭套餐群组
				templateId = fgconfig.getData(0).getString("PARA_CODE18","");
			}else if(MfcCommonUtil.PRODUCT_CODE_5G5.equals(productCode)){//5G融合套餐群组
				templateId = fgconfig.getData(0).getString("PARA_CODE18","");
			}else if(MfcCommonUtil.PRODUCT_CODE_TF6.equals(productCode)){//全国亲情网(支付宝版月包)
				templateId = zfbconfig.getData(0).getString("PARA_CODE18","");
			}else if(MfcCommonUtil.PRODUCT_CODE_TF7.equals(productCode)){//全国亲情网(支付宝版季包)
				templateId = zfbconfig.getData(0).getString("PARA_CODE18","");
			}else if(MfcCommonUtil.PRODUCT_CODE_TF8.equals(productCode)){//全国亲情网(支付宝版年包)
				templateId = zfbconfig.getData(0).getString("PARA_CODE18","");
			}else if(MfcCommonUtil.PRODUCT_CODE_TF9.equals(productCode)){//全国亲情网(异网版月包)
				templateId = ywconfig.getData(0).getString("PARA_CODE18","");
			}else if(MfcCommonUtil.PRODUCT_CODE_TF10.equals(productCode)){//全国亲情网(异网版季包)
				templateId = ywconfig.getData(0).getString("PARA_CODE18","");
			}else if(MfcCommonUtil.PRODUCT_CODE_TF11.equals(productCode)){//全国亲情网(异网版年包)
				templateId = ywconfig.getData(0).getString("PARA_CODE18","");
			}
			iData.putAll(param);
			res =MfcCommonUtil.getSmsContentByTemplateId(templateId,iData);
			break;
		default:
			break;
		}
		return res;

	}

	/**
	 * 根据userIdA或取成员总量
	 * @param userIdA
	 * @param relationTypeCode
	 * @return
	 */
    public static int getCountByUserIdA(String userIdA, String relationTypeCode) throws Exception {

		IData data = new DataMap();
		data.put("USER_ID_A", userIdA);
		data.put("RELATION_TYPE_CODE", relationTypeCode);

		IDataset result = Dao.qryByCode("TF_F_RELATION_UU","SEL_COUNT_BY_USERIDA",data);
		int count = result.first().getInt("COUNT");


		return count;
    }
}
