package com.asiainfo.veris.crm.order.soa.person.busi.family.mfc;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.session.SessionManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.person.busi.family.mfc.order.action.finish.MemberReconciliationFinishAction;
import com.ailk.bizcommon.util.SysDateMgr;
import com.ailk.bizservice.dao.CrmDAO;
import com.ailk.bizservice.dao.Dao;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.ailk.database.dao.DAOManager;
import com.ailk.database.dbconn.DBConnection;
import com.ailk.database.util.SQLParser;

public class SynMfcMemInfoBean extends CSBizBean
{
	private static final transient Logger log = Logger.getLogger(SynMfcMemInfoBean.class);
	
	/**
	 * 3.14. 省内群组成员关系查询
	 * @param inParam
	 * @return
	 * @throws Exception
	 */
	public IDataset getMeb(IData inParam)throws Exception
	{  
		
		IDataset recodeList = new DatasetList();
		IDataset resultList = new DatasetList();
		//客户标识，1代表主号，2代表成员号，3代表全部
		String  memFlag=inParam.getString("MEM_FLAG", "0");
		IData resultMap = new DataMap();
		IData resultMapInfo = new DataMap();
		
		IDataset resultMapList =  new DatasetList();
		if("1".equals(memFlag)){
			IData map = new DataMap();
			map.put("CUSTOMER_PHONE", inParam.getString("MEM_PHONE", "0"));
			map.put("PRODUCT_CODE", inParam.getString("PRODUCT_CODE", "0"));
			//RSRV_STR10,1-订阅,2-不订阅,3-取消订阅
			map.put("RSRV_STR10", inParam.getString("SUB_SCRIBE", "0"));
			 recodeList=MfcCommonUtil.getMeb(map);
			 if(recodeList.size()>0){
				 resultMap.put("RSP_CODE", "00");
				 resultMap.put("RSP_DESC", "成功");
				 resultMapInfo.put("PRODUCT_OFFERING_ID", recodeList.getData(0).getString("PRODUCT_OFFERING_ID",""));
				 resultMapInfo.put("POID_CODE", recodeList.getData(0).getString("POID_CODE",""));
				 resultMapInfo.put("POID_LABLE", recodeList.getData(0).getString("POID_LABLE",""));
				 resultMapInfo.put("CUSTOMER_PHONE", recodeList.getData(0).getString("CUSTOMER_PHONE",""));
				 for(int i=0;recodeList.size()>i;i++){
					 IData resultMapMeb = new DataMap();
					 resultMapMeb.put("MEM_FLAG", "1");//1是主号，2是成员号
					 resultMapMeb.put("MEM_TYPE", "1");
					 resultMapMeb.put("MEM_NUMBER", recodeList.getData(i).getString("MEM_NUMBER",""));
					 resultMapMeb.put("MEM_LABLE", recodeList.getData(i).getString("MEM_LABLE",""));
					 resultMapMeb.put("EFF_TIME", recodeList.getData(i).getString("EFF_TIME",""));
					 resultMapMeb.put("EXP_TIME", recodeList.getData(i).getString("EXP_TIME",""));
					// resultMapList.put("MEMBER_LIST", resultMapMeb);
					 resultMapList.add(resultMapMeb);
					 
				 }
				 resultMapInfo.put("MEMBER_LIST", resultMapList);
				 IDataset resultMapList1 =  new DatasetList();
				 resultMapList1.add(resultMapInfo);
				 resultMap.put("RSP_RESULT", resultMapList1);
				 resultList.add(resultMap);
			 }else{
				 resultMap.put("RSP_CODE", "08");
				 resultMap.put("RSP_DESC", "根据查询条件找不到查询结果");
				 resultList.add(resultMap);
			 }
			
		}else if("2".equals(memFlag)){
			IData map = new DataMap();
			map.put("MEM_NUMBER", inParam.getString("MEM_PHONE", "0"));
			map.put("PRODUCT_CODE", inParam.getString("PRODUCT_CODE", "0"));
			//RSRV_STR10,1-订阅,2-不订阅,3-取消订阅
			map.put("RSRV_STR10", inParam.getString("SUB_SCRIBE", "0"));
			 recodeList=MfcCommonUtil.getMebFu(map);
			 if(recodeList.size()>0){
				 resultMap.put("RSP_CODE", "00");
				 resultMap.put("RSP_DESC", "成功");
				 resultMapInfo.put("PRODUCT_OFFERING_ID", recodeList.getData(0).getString("PRODUCT_OFFERING_ID",""));
				 resultMapInfo.put("POID_CODE", recodeList.getData(0).getString("POID_CODE",""));
				 resultMapInfo.put("POID_LABLE", recodeList.getData(0).getString("POID_LABLE",""));
				 resultMapInfo.put("CUSTOMER_PHONE", recodeList.getData(0).getString("CUSTOMER_PHONE",""));
				 for(int i=0;recodeList.size()>i;i++){
					 String role=recodeList.getData(i).getString("ROLE_CODE_B","");
					 IData resultMapMeb = new DataMap();
					 resultMapMeb.put("MEM_FLAG", recodeList.getData(i).getString("ROLE_CODE_B"));//1是主号，2是成员号
					 if(recodeList.getData(i).getString("ROLE_CODE_B").equals("1")){
						 resultMapMeb.put("MEM_NUMBER", recodeList.getData(i).getString("CUSTOMER_PHONE",""));
					 }else{
						 resultMapMeb.put("MEM_NUMBER", recodeList.getData(i).getString("MEM_NUMBER",""));
					 }
					 resultMapMeb.put("MEM_TYPE", "1");
					
					 resultMapMeb.put("MEM_LABLE", recodeList.getData(i).getString("MEM_LABLE",""));
					 resultMapMeb.put("EFF_TIME", recodeList.getData(i).getString("EFF_TIME",""));
					 resultMapMeb.put("EXP_TIME", recodeList.getData(i).getString("EXP_TIME",""));
					// resultMapList.put("MEMBER_LIST", resultMapMeb);
					 resultMapList.add(resultMapMeb);
					
				 }
				 resultMapInfo.put("MEMBER_LIST", resultMapList);
				 IDataset resultMapList1 =  new DatasetList();
				 resultMapList1.add(resultMapInfo);
				 resultMap.put("RSP_RESULT", resultMapList1);	
				 resultList.add(resultMap);
			 }else{
				 resultMap.put("RSP_CODE", "08");
				 resultMap.put("RSP_DESC", "根据查询条件找不到查询结果");
				 resultList.add(resultMap);
			 }
			
		}else{
			
			IData map = new DataMap();
			map.put("CUSTOMER_PHONE", inParam.getString("MEM_PHONE", "0"));
			map.put("MEM_NUMBER", inParam.getString("MEM_PHONE", "0"));
			map.put("PRODUCT_CODE", inParam.getString("PRODUCT_CODE", "0"));
			//RSRV_STR10,1-订阅,2-不订阅,3-取消订阅
			map.put("RSRV_STR10", inParam.getString("SUB_SCRIBE", "0"));
			 recodeList=MfcCommonUtil.getMebAll(map);
			 if(recodeList.size()>0){
				 resultMap.put("RSP_CODE", "00");
				 resultMap.put("RSP_DESC", "成功");
				 resultMapInfo.put("PRODUCT_OFFERING_ID", recodeList.getData(0).getString("PRODUCT_OFFERING_ID",""));
				 resultMapInfo.put("POID_CODE", recodeList.getData(0).getString("POID_CODE",""));
				 resultMapInfo.put("POID_LABLE", recodeList.getData(0).getString("POID_LABLE",""));
				 resultMapInfo.put("CUSTOMER_PHONE", recodeList.getData(0).getString("CUSTOMER_PHONE",""));
				 for(int i=0;recodeList.size()>i;i++){
					 String role=recodeList.getData(i).getString("ROLE_CODE_B","");
					 IData resultMapMeb = new DataMap();
					 resultMapMeb.put("MEM_FLAG", recodeList.getData(i).getString("ROLE_CODE_B"));//1是主号，2是成员号
					 if(recodeList.getData(i).getString("ROLE_CODE_B").equals("1")){
						 resultMapMeb.put("MEM_NUMBER", recodeList.getData(i).getString("CUSTOMER_PHONE",""));
					 }else{
						 resultMapMeb.put("MEM_NUMBER", recodeList.getData(i).getString("MEM_NUMBER",""));
					 }
					 resultMapMeb.put("MEM_TYPE", "1");
					
					 resultMapMeb.put("MEM_LABLE", recodeList.getData(i).getString("MEM_LABLE",""));
					 resultMapMeb.put("EFF_TIME", recodeList.getData(i).getString("EFF_TIME",""));
					 resultMapMeb.put("EXP_TIME", recodeList.getData(i).getString("EXP_TIME",""));
					// resultMapList.put("MEMBER_LIST", resultMapMeb);
					 resultMapList.add(resultMapMeb);
					
				 }
				 resultMapInfo.put("MEMBER_LIST", resultMapList);
				 IDataset resultMapList1 =  new DatasetList();
				 resultMapList1.add(resultMapInfo);
				 resultMap.put("RSP_RESULT", resultMapList1);	
				 resultList.add(resultMap);
			 }else{
				 resultMap.put("RSP_CODE", "08");
				 resultMap.put("RSP_DESC", "根据查询条件找不到查询结果");
				 resultList.add(resultMap);
			 }
		}
		
		
		return  resultList;
	}
	/**
	 * 3.3成员管理
	 * @param inParam
	 * @return
	 * @throws Exception
	 */
	public void synMeb(IData inParam)throws Exception
	{    	
		IDataUtil.chkParam(inParam, "PO_ORDER_NUMBER");    	
		IDataUtil.chkParam(inParam, "PRODUCT_CODE");
		IDataUtil.chkParam(inParam, "PRODUCT_OFFERING_ID");
		String custPhone = IDataUtil.chkParam(inParam, "CUSTOMER_PHONE");
		IDataUtil.chkParam(inParam, "BUSINESS_TYPE");
		IDataUtil.chkParam(inParam, "ORDER_SOURCE");        
		IDataUtil.chkParam(inParam, "COMPANY_ID");
		String orederType = IDataUtil.chkParam(inParam, "ORDER_TYPE");        
		String action = IDataUtil.chkParam(inParam, "ACTION");
		IDataUtil.chkParam(inParam, "BIZ_VERSION");

		IDataUtil.chkParam(inParam, "PRODUCT_ORDER_MEMBER");
		IDataset productMember = new DatasetList(inParam.getString("PRODUCT_ORDER_MEMBER"));
		inParam.put("PRODUCT_ORDER_MEMBER", productMember);
		
		try
		{
		if("1".equals(orederType))
		{//BBOSS下发成员省开通确认
			//调用二次短信确认接口   intf
			try
			{				
				if (log.isDebugEnabled())
				{
					log.debug("11111111111111111111111111SS.VirtulFamilyTwoCheckIntfSVC.addMemberVirtulFamilyTwoCheck11111111111111inParam="+inParam);
				}
				IDataset resultList = CSAppCall.call("SS.VirtulFamilyTwoCheckIntfSVC.addMemberVirtulFamilyTwoCheck", inParam);
				if(DataUtils.isEmpty(resultList))
				{
					CSAppException.apperr(CrmCommException.CRM_COMM_103, "调用二次短信确认接口失败");
				}
				if (log.isDebugEnabled())
				{
					log.debug("11111111111111111111111111SS.VirtulFamilyTwoCheckIntfSVC.addMemberVirtulFamilyTwoCheck11111111111111resultList="+resultList);
				}
			}
			catch(Exception e)
			{
				CSAppException.apperr(CrmCommException.CRM_COMM_103, e.getMessage());
			}
		}
		else if("2".equals(orederType))
		{//BBOSS下发成员省归档
			
			// 更新 log 表 工单状态标识 为 1   表示完成
			IData logInfo = new DataMap();
			logInfo.put("RSRV_STR3", "1");
			logInfo.put("PO_ORDER_NUMBER", inParam.getString("PO_ORDER_NUMBER",""));
			logInfo.put("CUSTOMER_PHONE", custPhone);
			IDataset data = inParam.getDataset("PRODUCT_ORDER_MEMBER");
			String menNumber = data.getData(0).getString("MEM_NUMBER");
			logInfo.put("MEM_NUMBER", menNumber);
			updateLog(logInfo);
			//默认值生成，如果没有则插默认值
			IDataset updateInfo =new DatasetList();//更新数据集合
			IDataset insertInfo =new DatasetList();//新增数据集合
			String poidCode = inParam.getString("PRODUCT_OFFERING_ID").substring(16);
    		String poLable ="群"+poidCode;
			if("50".equals(action)){
					IDataset memUU = MfcCommonUtil.getRelationUusByUserSnRole(inParam.getString("CUSTOMER_PHONE"), "MF","", inParam);
					if(IDataUtil.isEmpty(memUU)){//主号没有uu关系  外省主号
			   			IData otherInfo = new DataMap();
            			otherInfo.put("PO_ORDER_NUMBER",inParam.getString("PO_ORDER_NUMBER"));
            			otherInfo.put("PRODUCT_OFFERING_ID",inParam.getString("PRODUCT_OFFERING_ID"));
            			otherInfo.put("UUID", SeqMgr.getInstId());
            			otherInfo.put("CUSTOMER_PHONE",custPhone);
            			otherInfo.put("ROLE_CODE_B","1");
            			otherInfo.put("POID_LABLE",inParam.getString("POID_LABLE",poLable));
            			otherInfo.put("POID_CODE",inParam.getString("POID_CODE",poidCode));
            			otherInfo.put("PRODUCT_CODE",inParam.getString("PRODUCT_CODE"));
            			otherInfo.put("ADD_TIME",SysDateMgr.getSysTime());
            			otherInfo.put("FINISH_TAG","1");//1--有效    2--失效
            			otherInfo.put("REMARK","创建家庭网");
            			otherInfo.put("UUID",SeqMgr.getInstId());
            			otherInfo.put("EFF_TIME",SysDateMgr.getSysDateYYYYMMDDHHMMSS() );
            			otherInfo.put("EXP_TIME",SysDateMgr.END_TIME_FOREVER);
            			otherInfo.put("ACTION",inParam.getString("ACTION"));
            			Dao.insert("TF_B_FAMILY_OTHER", otherInfo, Route.CONN_CRM_CEN);	
            			IDataset memberList = new DatasetList(inParam.getString("PRODUCT_ORDER_MEMBER"));  
            			if (log.isDebugEnabled())
    		        	{
    		        		log.debug("=======特殊处理memberlist====================="+memberList);
    		        	}
            			if(IDataUtil.isNotEmpty(memberList)){
		           			for(int a=0;a<memberList.size();a++){
		           				if(!custPhone.equals(memberList.getData(a).getString("MEM_NUMBER"))){//副号码列表里循环  过滤主号那条
		           					IData insertsData = new DataMap();
			                		String memLable = memberList.getData(a).getString("MEM_NUMBER").substring(7);
			                		insertsData.put("PO_ORDER_NUMBER",inParam.getString("PO_ORDER_NUMBER"));
			                		insertsData.put("PRODUCT_OFFERING_ID",inParam.getString("PRODUCT_OFFERING_ID"));
			                		insertsData.put("CUSTOMER_PHONE",custPhone);
			                		insertsData.put("ROLE_CODE_B","2");
			                		insertsData.put("POID_LABLE",inParam.getString("POID_LABLE",poLable));
			                		insertsData.put("POID_CODE",inParam.getString("POID_CODE",poidCode));
			                		insertsData.put("PRODUCT_CODE",inParam.getString("PRODUCT_CODE"));
			                		insertsData.put("MEM_LABLE",memberList.getData(a).getString("MEM_LABLE",memLable));
			                		insertsData.put("MEM_NUMBER",memberList.getData(a).getString("MEM_NUMBER"));
			                		insertsData.put("ADD_TIME",SysDateMgr.getSysTime());
			                		insertsData.put("REMARK","成员管理");
			                		insertsData.put("EFF_TIME",SysDateMgr.getSysDateYYYYMMDDHHMMSS() );
			                		//	otherInfo.put("EXP_TIME",SysDateMgr.END_TIME_FOREVER);
			                		insertsData.put("ACTION",inParam.getString("ACTION"));
			            			insertInfo.add(insertsData);		           					
		           				}		           				
		           			}
            			}         			
					}else{
        		IDataset memberList = new DatasetList(inParam.getString("PRODUCT_ORDER_MEMBER"));   
        		for(int x=0;x<memberList.size();x++){
					IDataset newsInfo=MfcCommonUtil.selMemberinfo(inParam.getString("PRODUCT_OFFERING_ID"),custPhone,memberList.getData(x).getString("MEM_NUMBER"));
		    		if(IDataUtil.isNotEmpty(newsInfo)){
	           			//for(int a=0;a<memberList.size();a++){
	            			IData otherInfo = new DataMap();
	                		String memLable = memberList.getData(x).getString("MEM_NUMBER").substring(7);
	           				otherInfo.put("PRODUCT_OFFERING_ID",inParam.getString("PRODUCT_OFFERING_ID"));
	        				otherInfo.put("PO_ORDER_NUMBER",inParam.getString("PO_ORDER_NUMBER"));
	            			otherInfo.put("POID_CODE",inParam.getString("POID_CODE",poidCode));
	            			otherInfo.put("POID_LABLE",inParam.getString("POID_LABLE",poLable));
	            			otherInfo.put("MEM_LABLE",memberList.getData(x).getString("MEM_LABLE",memLable));
	            			otherInfo.put("MEM_NUMBER",memberList.getData(x).getString("MEM_NUMBER"));          			
	            			otherInfo.put("EFF_TIME",SysDateMgr.getSysDateYYYYMMDDHHMMSS() );
	            		//	otherInfo.put("EXP_TIME",SysDateMgr.END_TIME_FOREVER);
	            			otherInfo.put("ACTION",inParam.getString("ACTION"));
	            			otherInfo.put("REMARK","成员管理");
	            			updateInfo.add(otherInfo);
	           			//}
	        			//更新other表的 有效标识 群组编码
	        			
	        		}else{//没有，新增一条记录
	        			//for(int b=0;b<memberList.size();b++){
	            			IData otherInfo = new DataMap();
	                		String memLable = memberList.getData(x).getString("MEM_NUMBER").substring(7);
	        				otherInfo.put("PO_ORDER_NUMBER",inParam.getString("PO_ORDER_NUMBER"));
	            			otherInfo.put("PRODUCT_OFFERING_ID",inParam.getString("PRODUCT_OFFERING_ID"));
	            			otherInfo.put("CUSTOMER_PHONE",custPhone);
	            			otherInfo.put("ROLE_CODE_B","2");
	            			otherInfo.put("POID_LABLE",inParam.getString("POID_LABLE",poLable));
	            			otherInfo.put("POID_CODE",inParam.getString("POID_CODE",poidCode));
	            			otherInfo.put("PRODUCT_CODE",inParam.getString("PRODUCT_CODE"));
	            			otherInfo.put("MEM_LABLE",memberList.getData(x).getString("MEM_LABLE",memLable));
	            			otherInfo.put("MEM_NUMBER",memberList.getData(x).getString("MEM_NUMBER"));
	            			otherInfo.put("ADD_TIME",SysDateMgr.getSysTime());
	            			otherInfo.put("REMARK","成员管理");
	            			otherInfo.put("EFF_TIME",SysDateMgr.getSysDateYYYYMMDDHHMMSS() );
	                		//	otherInfo.put("EXP_TIME",SysDateMgr.END_TIME_FOREVER);
	                		otherInfo.put("ACTION",inParam.getString("ACTION"));
	            			insertInfo.add(otherInfo);
	            			if (log.isDebugEnabled())
	    		        	{
	    		        		log.debug("insertInfo=add============================="+insertInfo);
	    		        	}
	        			//}      			
	        		}
        		}
					}    
			}else if("51".equals(action)){
        		IDataset memberList = new DatasetList(inParam.getString("PRODUCT_ORDER_MEMBER"));        		
           			for(int a=0;a<memberList.size();a++){
    					IDataset newsInfo=MfcCommonUtil.selMemberinfo(inParam.getString("PRODUCT_OFFERING_ID"),custPhone,memberList.getData(a).getString("MEM_NUMBER"));
    	           		if(IDataUtil.isNotEmpty(newsInfo)){
    	           			IData otherInfo = new DataMap();
	                		String memLable = memberList.getData(a).getString("MEM_NUMBER").substring(7);
	           				otherInfo.put("PRODUCT_OFFERING_ID",inParam.getString("PRODUCT_OFFERING_ID"));
	        				otherInfo.put("PO_ORDER_NUMBER",inParam.getString("PO_ORDER_NUMBER"));
	            			otherInfo.put("POID_CODE",inParam.getString("POID_CODE",poidCode));
	            			otherInfo.put("POID_LABLE",inParam.getString("POID_LABLE",poLable));
	            			otherInfo.put("MEM_LABLE",memberList.getData(a).getString("MEM_LABLE",memLable));
	            			otherInfo.put("MEM_NUMBER",memberList.getData(a).getString("MEM_NUMBER"));          			
	            			otherInfo.put("EFF_TIME",SysDateMgr.getSysDateYYYYMMDDHHMMSS() );
	            		//	otherInfo.put("EXP_TIME",SysDateMgr.END_TIME_FOREVER);
	            			otherInfo.put("ACTION",inParam.getString("ACTION"));
	            			otherInfo.put("REMARK","成员管理");
	            			updateInfo.add(otherInfo);
    	           		}else{
    	            			IData otherInfo = new DataMap();
    	                		String memLable = memberList.getData(a).getString("MEM_NUMBER").substring(7);
    	        				otherInfo.put("PO_ORDER_NUMBER",inParam.getString("PO_ORDER_NUMBER"));
    	            			otherInfo.put("PRODUCT_OFFERING_ID",inParam.getString("PRODUCT_OFFERING_ID"));
    	            			otherInfo.put("CUSTOMER_PHONE",custPhone);
    	            			otherInfo.put("ROLE_CODE_B","2");
    	            			otherInfo.put("POID_LABLE",inParam.getString("POID_LABLE",poLable));
    	            			otherInfo.put("POID_CODE",inParam.getString("POID_CODE",poidCode));
    	            			otherInfo.put("PRODUCT_CODE",inParam.getString("PRODUCT_CODE"));
    	            			otherInfo.put("MEM_LABLE",memberList.getData(a).getString("MEM_LABLE",memLable));
    	            			otherInfo.put("MEM_NUMBER",memberList.getData(a).getString("MEM_NUMBER"));
    	            			otherInfo.put("ADD_TIME",SysDateMgr.getSysTime());
    	            			otherInfo.put("REMARK","成员管理");
    	            			otherInfo.put("EFF_TIME",SysDateMgr.getSysDateYYYYMMDDHHMMSS() );
    	                		//	otherInfo.put("EXP_TIME",SysDateMgr.END_TIME_FOREVER);
    	                		otherInfo.put("ACTION",inParam.getString("ACTION"));
    	            			insertInfo.add(otherInfo);    	        			 
    	           		}
           			}	        				        		
			}
    	
       		IData oprData =new DataMap();
       		oprData.put("UPDATE_INFO",updateInfo );
       		oprData.put("INSERT_INFO",insertInfo );
       		if (log.isDebugEnabled())
        	{
        		log.debug("11111111111111111111111111SynMfcMemInfoBean11111111111111updateInfo"+updateInfo);
        		log.debug("11111111111111111111111111SynMfcMemInfoBean11111111111111insertInfo"+insertInfo);

        	}
			//归档时必填参数
			if("50".equals(action))
			{//增加成员
				if(!isDealMemList(inParam))
				{//校验成员列表是否需要处理
					if (log.isDebugEnabled())
		        	{
		        		log.debug("11111111111111111111111111SynMfcMemInfoBean11111111111111isDealMemList");
		        	}
					return;
				}
				inParam.put("OPER_TYPE", "03");
				inParam.put("TRADE_TYPE_CODE", "2582");
				inParam.put("ORDER_TYPE_CODE", "2582");
			}
			else if("51".equals(action))
			{//删除成员
				inParam.put("OPER_TYPE", "04");
				inParam.put("TRADE_TYPE_CODE", "2583");
				inParam.put("ORDER_TYPE_CODE", "2583");
			}
			else
			{//操作类型错误
				CSAppException.apperr(CrmCommException.CRM_COMM_695, "ACTION");
			}
			
			String serialNumber = "";//路由号码
			String serialNumberList = "";//本省号码列表
			IDataset routeA =  ResCall.getMphonecodeInfo(custPhone);
			if(DataUtils.isNotEmpty(routeA))
			{//主号码为本省号码,不能删除所有成员及虚拟家庭网
				serialNumber = custPhone;
			}
			else{			
				for(int i=0;i<productMember.size();i++)
				{//循环校验成员列表信息
					IData member = productMember.getData(i);
					String sn = IDataUtil.chkParam(member, "MEM_NUMBER");	
					IDataset routeB =  ResCall.getMphonecodeInfo(sn);
					if(DataUtils.isNotEmpty(routeB))
					{//获取本省号码路由
						serialNumber = sn;
						serialNumberList += sn;
					}
				}
			}
			
			//家庭网校验
			IData familyData = checkDelAll(serialNumberList,inParam);
			if(!"00".equals(familyData.getString("RSP_CODE")))
			{
				CSAppException.apperr(CrmCommException.CRM_COMM_103, familyData.getString("RSP_DESC"));
			}
			
			if("05".equals(inParam.getString("ORDER_SOURCE")))
			{//销户流程不走工单，只登记对账
				if (log.isDebugEnabled())
	        	{
	        		log.debug("11111111111111111111111111SynMfcMemInfoBean11111111111111最后一个成员注销");
	        	}
				//更新对账表
				String finishTime =  productMember.getData(0).getString("FINISH_TIME");
    			IData input = new DataMap();
    			input.put("CUSTOMER_PHONE", custPhone);
    			input.put("ACTION", "51");
				input.put("PRODUCT_OFFERING_ID", inParam.getString("PRODUCT_OFFERING_ID"));
    			input.put("EXP_TIME",finishTime);
    			input.put("OPR_TIME", finishTime);
    			input.put("FINISH_TIME",finishTime);
    			if(!"0".equals(familyData.getString("SYN_ALL","1")))
    			{//仅销户处理当前号码，其他成员信息不处理
    				String member = "";
    				for(int i=0;i<productMember.size();i++)
    				{
    					if(!custPhone.equals(productMember.getData(i).getString("MEM_NUMBER")))
    					{//防止主号码在成员列表内
    						member = productMember.getData(i).getString("MEM_NUMBER");
    						break;
    					}
    				}
    				input.put("MEM_NUMBER",member);
    			}
    			if("1".equals(inParam.getString("IS_SEND_TYPE","0")))
    			{//对账接口发起的销户流程
    				input.put("RSRV_STR1", "ACCT");
    			}
    			input.put("SYS_DATE", finishTime);
					MfcCommonUtil.updateSync(input);
//					IData indata = new DataMap();
//    				indata.put("MEM_NUMBER", input.getString("MEM_NUMBER"));
//    				indata.put("CUSTOMER_PHONE",custPhone );
//    				indata.put("PO_ORDER_NUMBER",inParam.getString("PO_ORDER_NUMBER"));
//    				indata.put("RSRV_STR3","1");
//    				indata.put("ACTION","51");
//    				MfcCommonUtil.updateLog(indata);
    				IDataset newsInfos=MfcCommonUtil.getPoidCodeByPOORDERNUMBER(custPhone,null,inParam.getString("PRODUCT_OFFERING_ID"));
               		 poidCode = inParam.getString("PRODUCT_OFFERING_ID").substring(16);
            		 poLable ="群"+poidCode;
               		if(IDataUtil.isNotEmpty(newsInfos)){
            			//更新other表的 有效标识 群组编码
            			IData otherInfo = new DataMap();
            			otherInfo.put("PRODUCT_OFFERING_ID",inParam.getString("PRODUCT_OFFERING_ID"));
            			otherInfo.put("POID_CODE",inParam.getString("POID_CODE",poidCode));
            			otherInfo.put("POID_LABLE",inParam.getString("POID_LABLE",poLable));
            			otherInfo.put("FINISH_TAG","2");
            			otherInfo.put("REMARK","注销家庭网");
            			otherInfo.put("CUSTOMER_PHONE",custPhone);
            			otherInfo.put("PO_ORDER_NUMBER",inParam.getString("PO_ORDER_NUMBER"));
            			otherInfo.put("PRODUCT_CODE",inParam.getString("PRODUCT_CODE"));
            			otherInfo.put("MEM_NUMBER",input.getString("MEM_NUMBER"));
            			otherInfo.put("EFF_TIME",SysDateMgr.getSysDateYYYYMMDDHHMMSS() );
                		otherInfo.put("EXP_TIME",SysDateMgr.getSysDateYYYYMMDDHHMMSS());
                		otherInfo.put("ACTION",inParam.getString("ACTION"));
            			MfcCommonUtil.updateDeleteInfos(otherInfo);//这里要新加一个sql 
            		}else{//没有，新增一条记录
            			IData otherInfo = new DataMap();
            			otherInfo.put("PO_ORDER_NUMBER",inParam.getString("PO_ORDER_NUMBER"));
            			otherInfo.put("UUID", SeqMgr.getInstId());
            			otherInfo.put("PRODUCT_OFFERING_ID",inParam.getString("PRODUCT_OFFERING_ID"));
            			otherInfo.put("CUSTOMER_PHONE",custPhone);
            			otherInfo.put("ROLE_CODE_B","1");
            			otherInfo.put("POID_LABLE",inParam.getString("POID_LABLE",poLable));
            			otherInfo.put("POID_CODE",inParam.getString("POID_CODE",poidCode));
            			otherInfo.put("PRODUCT_CODE",inParam.getString("PRODUCT_CODE"));
            			otherInfo.put("MEM_NUMBER",input.getString("MEM_NUMBER"));
            			otherInfo.put("ADD_TIME",SysDateMgr.getSysTime());
            			otherInfo.put("FINISH_TAG","2");//1--有效    2--失效
            			otherInfo.put("REMARK","注销家庭网");
            			otherInfo.put("UUID",SeqMgr.getInstId());
            			otherInfo.put("EFF_TIME",SysDateMgr.getSysDateYYYYMMDDHHMMSS() );
                		otherInfo.put("EXP_TIME",SysDateMgr.getSysDateYYYYMMDDHHMMSS());
                		otherInfo.put("ACTION",inParam.getString("ACTION"));
            			Dao.insert("TF_B_FAMILY_OTHER", otherInfo, Route.CONN_CRM_CEN);	
            		}
				return;
			}
			
			if(StringUtils.isBlank(serialNumber))
			{//本次归档不存在本省号码，获取原UU表中的本省号码
					serialNumber = SynMfcUserInfoBean.getThisProSn(custPhone,inParam.getString("PRODUCT_OFFERING_ID"));
			}
			inParam.put("SERIAL_NUMBER", serialNumber);
			
			//生成业务工单
//			try
//			{
				if (log.isDebugEnabled())
	        	{
	        		log.debug("11111111111111111111111111SS.TransProFamilyTradeSVC.tradeReg11111111111111inParam="+inParam);
	        	}
				IDataset resultList = CSAppCall.call("SS.TransProFamilyTradeSVC.tradeReg", inParam);
				if(DataUtils.isEmpty(resultList)){
					CSAppException.apperr(CrmCommException.CRM_COMM_103, "业务受理失败");
				}
				
				MfcCommonUtil.oprDate(oprData,action,inParam.getString("PRODUCT_OFFERING_ID"),custPhone,inParam.getString("PO_ORDER_NUMBER"));
				if (log.isDebugEnabled())
	        	{
	        		log.debug("11111111111111111111111111SS.TransProFamilyTradeSVC.tradeReg11111111111111resultList="+resultList);
	        	}
				//        		String tradeId = resultList.size()>0?resultList.getData(0).getString("TRADE_ID"):"";
//			}
//			catch(Exception e)
//			{
//				CSAppException.apperr(CrmCommException.CRM_COMM_103, e.getMessage());
//			}
		}
		else
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_695, "ORDER_TYPE");
		}
		}
		catch(Exception e)
		{
			SessionManager.getInstance().rollback();

		}
	}
	
	private void updateLog(IData inData) throws Exception{
    	DBConnection conn = new DBConnection("cen1", true, false);
        try
        {
            SQLParser parser = new SQLParser(inData);
            parser.addSQL(" UPDATE TF_B_FAMILY_LOG SET ");
            parser.addSQL(" RSRV_STR3 = :RSRV_STR3 ");
            parser.addSQL(" WHERE 1=1 ");
            parser.addSQL(" AND PO_ORDER_NUMBER = :PO_ORDER_NUMBER ");
            parser.addSQL(" AND CUSTOMER_PHONE = :CUSTOMER_PHONE  ");
            parser.addSQL(" AND MEM_NUMBER = :MEM_NUMBER ");
            
            CrmDAO dao = DAOManager.createDAO(CrmDAO.class, Route.CONN_CRM_CEN);
            dao.executeUpdate(conn, parser.getSQL(), parser.getParam());

            conn.commit();
        }catch (Exception e)
        {
            conn.rollback();
            CSAppException.apperr(CrmCommException.CRM_COMM_103, e.getMessage());
        }
        finally
        {
            conn.close();
        }
    }
	
	private boolean isDealMemList(IData inParam) throws Exception {
		IDataset productMember = new DatasetList(inParam.getString("PRODUCT_ORDER_MEMBER"));
		
		for(int i=0;i<productMember.size();i++)
		{
			IData memb = productMember.getData(i);
			//role_code_b字段不传，因为
			IDataset memUU = MfcCommonUtil.getRelationUusByUserSnRole(memb.getString("MEM_NUMBER"), "MF", "",inParam);
			if(DataUtils.isNotEmpty(memUU))
			{//UU表存在号码记录
				productMember.remove(i);
			}
		}
		
		if(productMember.size()>0)
		{			
			inParam.put("PRODUCT_ORDER_MEMBER", productMember);
			return true;
		}
		return false;
	}

	//独立建/删家操作
	private IData checkDelAll(String serialNumberList,IData inParam) throws Exception 
	{
		IData result = new DataMap();
		result.put("RSP_CODE", "00");
		int thisProveNum = 0;
		String custPhone = inParam.getString("CUSTOMER_PHONE");
		String operType = inParam.getString("OPER_TYPE");//03新增04删除
		String productOfferId = inParam.getString("PRODUCT_OFFERING_ID");
		
		IDataset relationAll = new DatasetList();
		//主号UU关系
		IDataset relaUUDatas = MfcCommonUtil.getRelationUusByUserSnRole(custPhone, "MF","1",inParam);
		if (log.isDebugEnabled())
		{
			log.debug("1111111111111TransProFamilyTrade11111111closeFamily111111relaUUDatas="+relaUUDatas);
		}
		IDataset routeA = ResCall.getMphonecodeInfo(custPhone);
		
		if("04".equals(operType))
		{//删除成员
			if(DataUtils.isNotEmpty(routeA))
			{//主号码为本省号码,不能删除所有成员也不存在删除虚拟家庭网
				return result;
			}
			if(DataUtils.isNotEmpty(relaUUDatas))
			{//主号家庭网下的所有副号码UU关系
				String userIdA = relaUUDatas.getData(0).getString("USER_ID_A");
				relationAll= MfcCommonUtil.getSEL_USER_ROLEA(userIdA , "2" , "MF",inParam);
			}
			if(DataUtils.isNotEmpty(relationAll))
			{//存在副号码UU关系且不是主号省
				for(int i=0;i<relationAll.size();i++)
				{
					IData relationB = relationAll.getData(i);
					if(!serialNumberList.contains(relationB.getString("SERIAL_NUMBER_B")) && "1".equals(relationB.getString("RSRV_STR1")))
					{//成员号码不会在本次操作中删除且为本省号码
						thisProveNum++;
					}
				}
			}
			if(thisProveNum <= 0)
			{//本省家庭网号码不存在，删除虚拟家庭网资料
				//更新 other表
				//更新other表的 有效标识 群组编码
    			IData otherInfo = new DataMap();
    			String poidCode = inParam.getString("PRODUCT_OFFERING_ID").substring(16);
        		String poLable ="群"+poidCode;
    			otherInfo.put("PRODUCT_OFFERING_ID",inParam.getString("PRODUCT_OFFERING_ID"));
    			otherInfo.put("POID_CODE",inParam.getString("POID_CODE",poidCode));
    			otherInfo.put("POID_LABLE",inParam.getString("POID_LABLE",poLable));
    			otherInfo.put("FINISH_TAG","2");
    			otherInfo.put("REMARK","删除时本省号码不存在，删除家庭网");
    			otherInfo.put("CUSTOMER_PHONE",custPhone);
    			otherInfo.put("PO_ORDER_NUMBER",inParam.getString("PO_ORDER_NUMBER"));
    			otherInfo.put("PRODUCT_CODE",inParam.getString("PRODUCT_CODE"));
    			otherInfo.put("EFF_TIME",SysDateMgr.getSysDateYYYYMMDDHHMMSS() );
    			otherInfo.put("EXP_TIME",SysDateMgr.getSysDateYYYYMMDDHHMMSS());
    			otherInfo.put("ACTION",inParam.getString("ACTION"));
    			otherInfo.put("MEM_LABLE",inParam.getString("MEM_LABLE"));
    			MfcCommonUtil.updateDestoryInfo(otherInfo);//这里要新加一个sql 
				IData familyData = SynMfcUserInfoBean.getOrDelVirtulFamilySVC(custPhone, "2581",productOfferId);
				if(!"00".equals(familyData.getString("RSP_CODE")))
				{
					return familyData;
				}
				if("05".equals(inParam.getString("ORDER_SOURCE")))
				{//副号销户且删除家庭网资料，更新所有对账记录
					result.put("SYN_ALL", "0");//同步表更新登记所有同步数据
					return result;
				}
				//删除所有成员信息UU关系记录
				inParam.put("IS_FULL", "0");
			}
			return result;
		}		
		else
		{
			if(DataUtils.isEmpty(relaUUDatas))
			{//不存在家庭网信息且为新增成员操作，需要新增家庭网
				IData familyData = SynMfcUserInfoBean.getOrDelVirtulFamilySVC(custPhone,"2580",productOfferId);
				if(!"00".equals(familyData.getString("RSP_CODE")))
				{
					return familyData;
				}
				else
				{
					inParam.put("USER_ID_A", familyData.getString("USER_ID"));
					inParam.put("SERIAL_NUMBER_A", familyData.getString("SERIAL_NUMBER"));
					return result;
				}
			}
		}
		return result;
	}

}
