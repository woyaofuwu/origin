package com.asiainfo.veris.crm.order.soa.person.busi.family.mfc;

import org.apache.log4j.Logger;

import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.bizcommon.util.SysDateMgr;
import com.ailk.bizservice.dao.CrmDAO;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.database.dao.DAOManager;
import com.ailk.database.dbconn.DBConnection;
import com.ailk.database.util.SQLParser;
import com.ailk.service.session.SessionManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.RouteInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.SmsSend;

public class SynMfcUserInfoBean extends CSBizBean 
{
	private static final transient Logger log = Logger.getLogger(SynMfcUserInfoBean.class);
	/**
     * 3.1跨省家庭网同步接口
     * @param inParam
     * @return
     * @throws Exception
     */
    public IData synUser(IData inParam)throws Exception 
    {    	
    	String orderSource = IDataUtil.chkParam(inParam, "ORDER_SOURCE");
        IDataUtil.chkParam(inParam, "COMPANY_ID");
        String orederType = IDataUtil.chkParam(inParam, "ORDER_TYPE");
        String orederNum = IDataUtil.chkParam(inParam, "PO_ORDER_NUMBER");
        IDataUtil.chkParam(inParam, "PRODUCT_CODE");
        String operSubType = IDataUtil.chkParam(inParam, "ACTION");
        IDataUtil.chkParam(inParam, "CUSTOMER_TYPE");
        String custPhone = IDataUtil.chkParam(inParam, "CUSTOMER_PHONE");
        IDataUtil.chkParam(inParam, "BIZ_VERSION");
    	
        IData result = new DataMap();
        result.put("PO_ORDER_NUMBER", orederNum);        
        result.put("CUSTOMER_PHONE", custPhone);
        result.put("RSP_CODE", "00");
        result.put("OPR_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
    	try
    	{ 
    		if("1".equals(orederType))
    		{//BBOSS下发主号省受理
    			//调用校验主号码状态接口   intf
    			try
    			{
    				if (log.isDebugEnabled())
    				{
    					log.debug("11111111111111111111111111SS.FamilyAllNetBusiManageSVC.checkMeb11111111111111inParam="+inParam);
    				}
    				IDataset resultList = CSAppCall.call("SS.FamilyAllNetBusiManageSVC.checkMeb", inParam);
    				if(DataUtils.isEmpty(resultList)){
    					result.put("RSP_CODE", "99");
    					result.put("RSP_DESC", "校验主号码状态业务受理失败");
    				}
    				if (log.isDebugEnabled())
    				{
    					log.debug("11111111111111111111111111SS.FamilyAllNetBusiManageSVC.checkMeb11111111111111resultList="+resultList);
    				}
    				result.putAll(resultList.getData(0));
    			}
    			catch(Exception e)
    			{
    				result.put("RSP_CODE", "99");
    				result.put("RSP_DESC", e.getMessage());
    			} 
    			return result;
    		}
    		else if("2".equals(orederType))
    		{//BBOSS下发省/其他渠道/三期计费系统归档

    			// 更新 log 表 工单状态标识 为 1，表示完工
    			IData logInfo = new DataMap();
    			logInfo.put("RSRV_STR3", "1");
    			logInfo.put("PO_ORDER_NUMBER", inParam.getString("PO_ORDER_NUMBER",""));
    			logInfo.put("CUSTOMER_PHONE", custPhone);
    			updateLog(logInfo);

    			//归档时必填参数
    			String productOfferId = IDataUtil.chkParam(inParam, "PRODUCT_OFFERING_ID");
    			if(!"1".equals(inParam.getString("IS_SEND_TYPE","0")))
    			{//非对账入口需要校验必传
    				IDataUtil.chkParam(inParam, "FINISH_TIME");
    				IDataUtil.chkParam(inParam, "EFF_TIME");
    				IDataUtil.chkParam(inParam, "EXP_TIME");
    			}

    			IDataset relationUULists = MfcCommonUtil.getRelationUusByUserSnRole(custPhone,"MF","1",inParam);
    			if("00".equals(operSubType))
    			{//新增业务订购
    				if(DataUtils.isEmpty(relationUULists))
    				{//不存在家庭网
    					//调用CreateVirtulFamilySVC接口建家 intf
    					IData familyData = getOrDelVirtulFamilySVC(custPhone,"2580",productOfferId);
    					if(!"00".equals(familyData.getString("RSP_CODE")))
    					{
    						result.putAll(familyData);
    						return result;
    					}
    					else
    					{
    						//不走页面过来的，先查 如果有更新 ，如果没有新增
                    		//默认值生成，如果没有则插默认值
                    		String poidCode = inParam.getString("PRODUCT_OFFERING_ID").substring(16);
                    		String poLable ="群"+poidCode;
                    		//没有，新增一条记录
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
                    		
    						inParam.put("USER_ID_A", familyData.getString("USER_ID"));
    						inParam.put("SERIAL_NUMBER_A", familyData.getString("SERIAL_NUMBER"));
    					}
    					inParam.put("ACTION", "50");
    					inParam.put("OPER_TYPE", "01");
    					inParam.put("TRADE_TYPE_CODE", "2582");
    					inParam.put("ORDER_TYPE_CODE", "2582");
    				}
    				else
    				{
    					result.put("RSP_CODE", "10");
    					result.put("RSP_DESC", "订单重复");
    					return result;
    				}

    			}
    			else if("01".equals(operSubType))
    			{//取消业务订购
    				IData familyData = getOrDelVirtulFamilySVC(custPhone,"2581",productOfferId);
    				if(!"00".equals(familyData.getString("RSP_CODE")))
    				{
    					result.putAll(familyData);
    					return result;
    				}
        		if(!"05".equals(orderSource)){
        			//主号销户不能用poordernumber更新，做一个分支
              		//默认值生成，如果没有则插默认值
            		IDataset newsInfo=MfcCommonUtil.getPoidCodeByPOORDERNUMBER(custPhone,null,inParam.getString("PRODUCT_OFFERING_ID"));
            		String poidCode = inParam.getString("PRODUCT_OFFERING_ID").substring(16);
            		String poLable ="群"+poidCode;
               		if(IDataUtil.isNotEmpty(newsInfo)){
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
            			otherInfo.put("EFF_TIME",SysDateMgr.getSysDateYYYYMMDDHHMMSS() );
            			otherInfo.put("EXP_TIME",SysDateMgr.getSysDateYYYYMMDDHHMMSS());
            			otherInfo.put("ACTION",inParam.getString("ACTION"));
            			otherInfo.put("MEM_LABLE",inParam.getString("MEM_LABLE"));
            			MfcCommonUtil.updateDestoryInfo(otherInfo);//这里要新加一个sql 
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
            			otherInfo.put("ADD_TIME",SysDateMgr.getSysTime());
            			otherInfo.put("FINISH_TAG","2");//1--有效    2--失效
            			otherInfo.put("REMARK","注销家庭网");
            			otherInfo.put("UUID",SeqMgr.getInstId());
            			otherInfo.put("EFF_TIME",SysDateMgr.getSysDateYYYYMMDDHHMMSS() );
            			otherInfo.put("EXP_TIME",SysDateMgr.getSysDateYYYYMMDDHHMMSS() );
            			otherInfo.put("ACTION",inParam.getString("ACTION"));
            			Dao.insert("TF_B_FAMILY_OTHER", otherInfo, Route.CONN_CRM_CEN);	
            		}
        		}
    				if("05".equals(orderSource))
    				{//主号销户流程
    					String finishTime =  inParam.getString("FINISH_TIME",SysDateMgr.getSysDateYYYYMMDDHHMMSS());
    					IData input = new DataMap();
    					input.put("CUSTOMER_PHONE", custPhone);
    					input.put("ACTION", "52");
    					input.put("PRODUCT_OFFERING_ID", inParam.getString("PRODUCT_OFFERING_ID"));
    					input.put("EXP_TIME",finishTime);
    					input.put("OPR_TIME", finishTime);
    					input.put("FINISH_TIME",finishTime);
    					if("1".equals(inParam.getString("IS_SEND_TYPE","0")))
    					{//对账接口发起的销户流程
    						input.put("RSRV_STR1", "ACCT");
    					}
    					input.put("SYS_DATE", finishTime);
    					IDataset newsInfo=MfcCommonUtil.getPoidCodeByPOORDERNUMBER(custPhone,null,inParam.getString("PRODUCT_OFFERING_ID"));
                   		String poidCode = inParam.getString("PRODUCT_OFFERING_ID").substring(16);
                		String poLable ="群"+poidCode;
                   		if(IDataUtil.isNotEmpty(newsInfo)){
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
                			otherInfo.put("EFF_TIME",SysDateMgr.getSysDateYYYYMMDDHHMMSS() );
                			otherInfo.put("EXP_TIME",SysDateMgr.getSysDateYYYYMMDDHHMMSS());
                			otherInfo.put("ACTION",inParam.getString("ACTION"));
    						otherInfo.put("MEM_LABLE",inParam.getString("MEM_LABLE"));
                			MfcCommonUtil.updateDestoryInfo(otherInfo);//这里要新加一个sql 
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
                			otherInfo.put("ADD_TIME",SysDateMgr.getSysTime());
                			otherInfo.put("FINISH_TAG","2");//1--有效    2--失效
                			otherInfo.put("REMARK","注销家庭网");
                			otherInfo.put("UUID",SeqMgr.getInstId());
                			otherInfo.put("EFF_TIME",SysDateMgr.getSysDateYYYYMMDDHHMMSS() );
                			otherInfo.put("EXP_TIME",SysDateMgr.getSysDateYYYYMMDDHHMMSS());
                			otherInfo.put("ACTION",inParam.getString("ACTION"));
    						otherInfo.put("MEM_LABLE",inParam.getString("MEM_LABLE"));
                			Dao.insert("TF_B_FAMILY_OTHER", otherInfo, Route.CONN_CRM_CEN);	
                		}
    					sendSmsOther(custPhone);
    					MfcCommonUtil.updateSync(input);
    					return result;
    				}
    				//业务取消流程
    				inParam.put("ACTION", "51");
    				inParam.put("OPER_TYPE", "02");
    				inParam.put("TRADE_TYPE_CODE", "2583");
    				inParam.put("ORDER_TYPE_CODE", "2583");
    			}
    			else if("02".equals(operSubType)||"03".equals(operSubType))
    			{//主号停复机

    				if(DataUtils.isEmpty(relationUULists))
    				{//不存在家庭网
    					//        			CSAppException.apperr(CrmCommException.CRM_COMM_103, "主号不存在家庭网，操作类型【主号停机/主号复开机】错误");
    					result.put("RSP_CODE", "99");
    					result.put("RSP_DESC", "主号不存在家庭网，操作类型【主号停机/复开机】错误");
    					return result;
    				}
    				IData input = new DataMap();
    				input.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
    				input.put(Route.USER_EPARCHY_CODE, getUserEparchyCode());
    				input.put("USER_ID_A", relationUULists.getData(0).getString("USER_ID_A"));//虚拟家庭用户ID
    				input.put("SERIAL_NUMBER_A", relationUULists.getData(0).getString("SERIAL_NUMBER_A"));//虚拟家庭号码
    				input.put("MAIN_SERIAL_NUMBER", custPhone);//主号
    				input.put("PRODUCT_CODE", inParam.getString("PRODUCT_CODE",""));//产品编码
    				input.put("PRODUCT_OFFERING_ID", inParam.getString("PRODUCT_OFFERING_ID",""));//业务订购实例ID
    				input.put("FINISH_TIME", inParam.getString("FINISH_TIME",SysDateMgr.getSysDateYYYYMMDDHHMMSS()));//归档时间
    				input.put("EFF_TIME", inParam.getString("EFF_TIME",SysDateMgr.getSysDateYYYYMMDDHHMMSS()));//生效时间
    				input.put("EXP_TIME", inParam.getString("EXP_TIME","20991231235959"));//失效时间
    				if("02".equals(operSubType))
    				{//停机
    					//默认值生成，如果没有则插默认值
                		IDataset newsInfo=MfcCommonUtil.getPoidCodeByPOORDERNUMBER(custPhone,null,inParam.getString("PRODUCT_OFFERING_ID"));
                		String poidCode = inParam.getString("PRODUCT_OFFERING_ID").substring(16);
                		String poLable ="群"+poidCode;
                   		if(IDataUtil.isNotEmpty(newsInfo)){
                			//更新other表的 有效标识 群组编码
                			IData otherInfo = new DataMap();
                			otherInfo.put("PRODUCT_OFFERING_ID",inParam.getString("PRODUCT_OFFERING_ID"));
                			otherInfo.put("POID_CODE",inParam.getString("POID_CODE",poidCode));
                			otherInfo.put("POID_LABLE",inParam.getString("POID_LABLE",poLable));
                			otherInfo.put("FINISH_TAG","2");
                			otherInfo.put("REMARK","停机");
                			otherInfo.put("CUSTOMER_PHONE",custPhone);
                			otherInfo.put("PO_ORDER_NUMBER",inParam.getString("PO_ORDER_NUMBER"));
                			otherInfo.put("PRODUCT_CODE",inParam.getString("PRODUCT_CODE"));
                			otherInfo.put("EFF_TIME",SysDateMgr.getSysDateYYYYMMDDHHMMSS() );
                			otherInfo.put("EXP_TIME",SysDateMgr.getSysDateYYYYMMDDHHMMSS());
                			otherInfo.put("ACTION",inParam.getString("ACTION"));
                			//Dao.update("TF_B_FAMILY_OTHER", otherInfo,new String[]{"UUID","FINISH_TAG"},Route.CONN_CRM_CEN);
                			MfcCommonUtil.updateStopOtherInfo(otherInfo);//这里要新加一个sql  根据主号  po_order_number,产品编码 更新字段
                		}else{//没有，新增一条记录
                			IData otherInfo = new DataMap();
                			otherInfo.put("PO_ORDER_NUMBER",inParam.getString("PO_ORDER_NUMBER"));
                			otherInfo.put("UUID", SeqMgr.getInstId());
                			otherInfo.put("PRODUCT_OFFERING_ID",inParam.getString("PRODUCT_OFFERING_ID"));
                			otherInfo.put("CUSTOMER_PHONE",custPhone);
                			otherInfo.put("ROLE_CODE_B","1");
                			otherInfo.put("UUID", SeqMgr.getInstId());
                			otherInfo.put("POID_LABLE",inParam.getString("POID_LABLE",poLable));
                			otherInfo.put("POID_CODE",inParam.getString("POID_CODE",poidCode));
                			otherInfo.put("PRODUCT_CODE",inParam.getString("PRODUCT_CODE"));
                			otherInfo.put("ADD_TIME",SysDateMgr.getSysTime());
                			otherInfo.put("FINISH_TAG","2");//1--有效    2--失效
                			otherInfo.put("REMARK","停机");
                			otherInfo.put("UUID",SeqMgr.getInstId());
                			otherInfo.put("EFF_TIME",SysDateMgr.getSysDateYYYYMMDDHHMMSS() );
                			otherInfo.put("EXP_TIME",SysDateMgr.getSysDateYYYYMMDDHHMMSS());
                			otherInfo.put("ACTION",inParam.getString("ACTION"));
                			Dao.insert("TF_B_FAMILY_OTHER", otherInfo, Route.CONN_CRM_CEN);	
                		}
        			input.put("IS_SEND_TYPE", inParam.getString("IS_SEND_TYPE","0"));//对账标记,1:对账流程，0：正常流程
    					input.put("TRADE_TYPE_CODE", "2584");
    					input.put("ORDER_TYPE_CODE", "2584");
    				}
    				else
    				{//复机
    					//默认值生成，如果没有则插默认值
                		IDataset newsInfo=MfcCommonUtil.selPoidCodeByPOORDERNUMBER(custPhone,null,inParam.getString("PRODUCT_OFFERING_ID"));
                		String poidCode = inParam.getString("PRODUCT_OFFERING_ID").substring(16);
                		String poLable ="群"+poidCode;
                		if(IDataUtil.isNotEmpty(newsInfo)){
                			//更新other表的 有效标识 群组编码
                			IData otherInfo = new DataMap();
                			otherInfo.put("PRODUCT_OFFERING_ID",inParam.getString("PRODUCT_OFFERING_ID"));
                			otherInfo.put("POID_CODE",inParam.getString("POID_CODE",poidCode));
                			otherInfo.put("POID_LABLE",inParam.getString("POID_LABLE",poLable));
                			otherInfo.put("FINISH_TAG","1");
                			otherInfo.put("REMARK","复机");
                			otherInfo.put("CUSTOMER_PHONE",custPhone);
                			otherInfo.put("PO_ORDER_NUMBER",inParam.getString("PO_ORDER_NUMBER"));
                			otherInfo.put("PRODUCT_CODE",inParam.getString("PRODUCT_CODE"));
                			otherInfo.put("EFF_TIME",SysDateMgr.getSysDateYYYYMMDDHHMMSS() );
                			otherInfo.put("EXP_TIME",SysDateMgr.END_TIME_FOREVER);
                			otherInfo.put("ACTION",inParam.getString("ACTION"));
                			
                			MfcCommonUtil.updateStopOtherInfoS(otherInfo);
                		}else{//没有，新增一条记录
                			IData otherInfo = new DataMap();
                			otherInfo.put("UUID", SeqMgr.getInstId());
                			otherInfo.put("PO_ORDER_NUMBER",inParam.getString("PO_ORDER_NUMBER"));
                			otherInfo.put("PRODUCT_OFFERING_ID",inParam.getString("PRODUCT_OFFERING_ID"));
                			otherInfo.put("CUSTOMER_PHONE",custPhone);
                			otherInfo.put("ROLE_CODE_B","1");
                			otherInfo.put("POID_LABLE",inParam.getString("POID_LABLE",poLable));
                			otherInfo.put("POID_CODE",inParam.getString("POID_CODE",poidCode));
                			otherInfo.put("PRODUCT_CODE",inParam.getString("PRODUCT_CODE"));
                			otherInfo.put("ADD_TIME",SysDateMgr.getSysTime());
                			otherInfo.put("FINISH_TAG","1");//1--有效    2--失效
                			otherInfo.put("REMARK","复机");
                			otherInfo.put("UUID",SeqMgr.getInstId());
                			otherInfo.put("EFF_TIME",SysDateMgr.getSysDateYYYYMMDDHHMMSS() );
                			otherInfo.put("EXP_TIME",SysDateMgr.END_TIME_FOREVER);
                			otherInfo.put("ACTION",inParam.getString("ACTION"));
                			Dao.insert("TF_B_FAMILY_OTHER", otherInfo, Route.CONN_CRM_CEN);	
                		}
    					input.put("TRADE_TYPE_CODE", "2585");
    					input.put("ORDER_TYPE_CODE", "2585");
    				}
    				//调用停复机接口   intf
    				IData resultstate = callMfcStateSVC(input);
    				result.putAll(resultstate);
    				return result;
    			}
    			else
    			{//操作类型错误
    				result.put("RSP_CODE", "07");
    				result.put("RSP_DESC", "操作类型错误");
    				return result;
    			}

    			//生成业务工单
    			//        	try
    			//        	{   
    			inParam.put("SERIAL_NUMBER", getThisProSn(custPhone,productOfferId));
    			if (log.isDebugEnabled())
    			{
    				log.debug("11111111111111111111111111SS.TransProFamilyTradeSVC.tradeReg11111111111111inParam="+inParam);
    			}
    			IDataset resultList = CSAppCall.call("SS.TransProFamilyTradeSVC.tradeReg", inParam);
    			if(DataUtils.isEmpty(resultList)){
    				result.put("RSP_CODE", "99");
    				result.put("RSP_DESC", "业务受理失败");
    			}
    			if (log.isDebugEnabled())
    			{
    				log.debug("11111111111111111111111111SS.TransProFamilyTradeSVC.tradeReg11111111111111resultList="+resultList);
    			}
    			//        		String tradeId = resultList.size()>0?resultList.getData(0).getString("TRADE_ID"):"";
    			//        	}
    			//        	catch(Exception e)
    			//        	{
    			//        		result.put("RSP_CODE", "99");
    			//                result.put("RSP_DESC", e.getMessage());
    			//        	}
    			result.put("PRODUCT_OFFERING_ID", productOfferId);
    		}
    		else
    		{
    			result.put("RSP_CODE", "03");
    			result.put("RSP_DESC", "订单类型错误");
    		}
    	}
	catch(Exception e)
	{
		SessionManager.getInstance().rollback();
		result.put("RSP_CODE", "99");
        result.put("RSP_DESC", e.getMessage());
	}
    	return result;
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
    
	//调用停复机接口
	private IData callMfcStateSVC(IData input) throws Exception 
	{
		IData result = new DataMap();
        result.put("RSP_CODE", "00");
//		try
//    	{   
			if (log.isDebugEnabled())
        	{
        		log.debug("11111111111111111111111111SS.ChangeMfcStateSVC.tradeReg11111111111111input="+input);
        	}
    		IDataset resultList = CSAppCall.call("SS.ChangeMfcStateSVC.tradeReg", input);
    		if(DataUtils.isEmpty(resultList)){
    			result.put("RSP_CODE", "99");
    			result.put("RSP_DESC", "主号停机/主号复开机处理异常");
    			return result;
    		}
    		if (log.isDebugEnabled())
        	{
        		log.debug("11111111111111111111111111SS.ChangeMfcStateSVC.tradeReg11111111111111resultList="+resultList);
        	}
    		return resultList.getData(0);
//    	}
//    	catch(Exception e)
//    	{
//    		result.put("RSP_CODE", "99");
//            result.put("RSP_DESC", e.getMessage());
//    	}
//		return result;
	}

	//创建/删除家庭网
	public static IData getOrDelVirtulFamilySVC(String custPhone,String tradeTypeCode,String productOfferId) throws Exception {
		IData result = new DataMap();
        result.put("RSP_CODE", "00");
		IData input = new DataMap();
		input.put("TRADE_TYPE_CODE", tradeTypeCode);
		input.put("SERIAL_NUMBER", custPhone);
		input.put("PRODUCT_OFFERING_ID", productOfferId);
		input.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
		input.put(Route.USER_EPARCHY_CODE, getUserEparchyCode());
		if (log.isDebugEnabled())
		{
			log.debug("1111111111111TransProFamilyTrade11111111getOrDelVirtulFamilySVC111111input="+input);
		}
//		try
//    	{
			IDataset resultList = CSAppCall.call("SS.CreateVirtulFamilySVC.crtTrade", input);
			if(DataUtils.isEmpty(resultList))
			{
				result.put("RSP_CODE", "99");
    			result.put("RSP_DESC", "虚拟家庭网业务受理失败,业务类型【"+tradeTypeCode+"】业务订购实例ID【"+productOfferId+"】");
    			return result;
			}
			if (log.isDebugEnabled())
			{
				log.debug("1111111111111TransProFamilyTrade11111111getOrDelVirtulFamilySVC111111result="+resultList);
			}
			result.put("USER_ID", resultList.getData(0).getString("USER_ID",""));
			result.put("SERIAL_NUMBER", resultList.getData(0).getString("SERIAL_NUMBER",""));
//    	}
//		catch(Exception e)
//    	{
//			result.put("RSP_CODE", "99");
//            result.put("RSP_DESC", e.getMessage());
//    	}
		//		测试数据
		//		input.put("USER_ID", "1115031615520146");
		//		input.put("SERIAL_NUMBER", "mf123456789");
		return result;
	}
	
	//获取本省号码路由
	public static String getThisProSn(String custPhone,String productOfferId) throws Exception 
	{
		String sn = "";
		IDataset routeA = ResCall.getMphonecodeInfo(custPhone);
		if(DataUtils.isNotEmpty(routeA))
		{//主号码为本省号码
			return custPhone;
		}
		IDataset relationAll = new DatasetList();
		//主号UU关系
		
		IData inData = new DataMap();
		inData.put("PRODUCT_OFFERING_ID", productOfferId);
		IDataset relaUUDatas = MfcCommonUtil.getRelationUusByUserSnRole(custPhone, "MF","1",inData);
		if (log.isDebugEnabled())
		{
			log.debug("1111111111111TransProFamilyTrade11111111closeFamily111111relaUUDatas="+relaUUDatas);
		}
		if(DataUtils.isNotEmpty(relaUUDatas))
		{//主号家庭网下的所有副号码UU关系
			String userIdA = relaUUDatas.getData(0).getString("USER_ID_A");
			relationAll= MfcCommonUtil.getSEL_USER_ROLEA(userIdA , "2" , "MF",inData);
		}
		else
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "主号不存在家庭网信息");
		}
		
		if(DataUtils.isNotEmpty(relationAll))
		{
			for(int i=0;i<relationAll.size();i++)
			{
				IData relationB = relationAll.getData(i);
				if("1".equals(relationB.getString("RSRV_STR1")))
				{//成员号码不会在本次操作中删除且为本省号码
					return relationB.getString("SERIAL_NUMBER_B");
				}
			}
		}
		else
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "无法获取本省号码信息");
		}
		return sn;
	}
		/**
		 * 主号销户，给副号发短信
		 * @param custPhone
		 * @throws Exception 
		 */
		public  void sendSmsOther(String custPhone) throws Exception{
			IDataset memberList = getMFCByCustPAction(custPhone,"50");
			if(IDataUtil.isNotEmpty(memberList)){
				for(int i = 0;i<memberList.size();i++){
					IData member = memberList.getData(i);
					String memNumber = member.getString("MEM_NUMBER");
					if(IDataUtil.isNotEmpty(ResCall.getMphonecodeInfo(memNumber))){
						IData sendInfo = new DataMap();
		    			sendInfo.put("EPARCHY_CODE", RouteInfoQry.getEparchyCodeBySn(memNumber));
		    			sendInfo.put("RECV_OBJECT", memNumber);
		    			sendInfo.put("RECV_ID", memNumber);
		    			sendInfo.put("SMS_PRIORITY", "50");
		    			String content = "尊敬的用户，您加入的全国亲情网主号"+custPhone+"已销号，您加入的全国亲情网下月起将会失效，全国亲情网原有成员间将不再享受国内语音互打免费优惠，拨打语音将会按照正常费用收取，请您关注，谢谢。【中国移动】";
		    			sendInfo.put("NOTICE_CONTENT", content);
		    			sendInfo.put("REMARK", "主号销户其他副号短信");
		    			sendInfo.put("FORCE_OBJECT", "10086");
		    			SmsSend.insSms(sendInfo,RouteInfoQry.getEparchyCodeBySn(memNumber));
					}
				}
			}	
		}
		
		/**
		 * 查对账表
		 * @param custPhone
		 * @param action
		 * @return
		 * @throws Exception
		 */
		public static IDataset getMFCByCustPAction(String custPhone, String action) throws Exception {
			IData iparam = new DataMap();
			iparam.put("CUSTOMER_PHONE", custPhone);
			iparam.put("ACTION", action);
			return Dao.qryByCodeParser("TI_B_MFC_SYNC", "SEL_MFC_BYACTICP", iparam,Route.CONN_CRM_CEN);
			
		}
}
