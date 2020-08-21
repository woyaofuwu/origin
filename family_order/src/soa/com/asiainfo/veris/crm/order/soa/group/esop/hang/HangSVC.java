package com.asiainfo.veris.crm.order.soa.group.esop.hang;

import java.util.Iterator;

import com.ailk.biz.util.StaticUtil;
import com.ailk.bizservice.dao.Dao;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.group.esop.query.EweAsynBean;
import com.asiainfo.veris.crm.order.soa.group.esop.query.EweConfigQry;
import com.asiainfo.veris.crm.order.soa.group.esop.query.EweNodeQry;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformAttrBean;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformEomsDetailBean;
import com.ailk.common.util.DataUtils;
import com.ailk.common.util.Utility;

public class HangSVC extends CSBizService{

	private static final long serialVersionUID = 1L;
	
	public IDataset hangApply(IData input) throws Exception{
		String serialNo =  getValue("serialNo", input, false);
		String productNo =  getValue("ProductNo", input, false);
		String applyType =  getValue("applyType", input, false);
		String applyReason =  getValue("applyReason", input, false);
		String applyPerson =  getValue("applyPerson", input, false);
		String contactPhone =  getValue("applyPersonContactPhone", input, false);
		String cityName = input.getString("cityName", "");
		String taskName = input.getString("taskName", "");

		IData param = new DataMap();
		param.put("SERIALNO", serialNo);
		param.put("PRODUCT_NO", productNo);
		IDataset emos = WorkformEomsDetailBean.qryEomsStateBySerialNo(param);
		if(IDataUtil.isEmpty(emos)){
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据ESOP工单编号"+serialNo+"和专线实例号"+productNo+"查询无资料，不能办理该业务！");
		}
		/*String busiState = emos.first().getString("BUSI_STATE");
		if(!"B".equals(busiState)&&!"D".equals(busiState)){
			String valueDesc = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_EWE_CONFIG", new String[]{ "CONFIGNAME", "PARAMVALUE" }, "VALUEDESC", new String[]{ "EOMS_BUSI_STATE", busiState});
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据ESOP工单编号"+serialNo+"和专线实例号"+productNo+"查询工单状态为"+valueDesc+"，不能办理该业务！");
		}*/
		IData params = new DataMap();
		params.put("RECORD_NUM", emos.first().getString("RECORD_NUM"));
		params.put("IBSYSID", emos.first().getString("IBSYSID"));
		
        IData eweData = CSAppCall.callOne("SS.EweNodeQrySVC.qryEweByibsysIdRecordnum", params);//查询是否有子流程
        if(IDataUtil.isEmpty(eweData)){
        	CSAppException.apperr(CrmCommException.CRM_COMM_103, "该工单无子流程，无法挂起！");
        }
        
        String bpmTempletId = eweData.getString("BPM_TEMPLET_ID");
        String ibsysId = emos.first().getString("IBSYSID");
        String recordNum = emos.first().getString("RECORD_NUM");
        String productId = eweData.getString("BUSI_CODE");
        String busiformId = eweData.getString("SUB_BUSIFORM_ID");
	    String recordNumNow=emos.first().getString("RECORD_NUM");

    	String bizrange=null;
    	String biaRange="";
	    String cityA="";
	    String cityZ="";
	    if("7012".equals(productId)||"70121".equals(productId)||"70122".equals(productId)){
	    	IData inputAttrIn = new DataMap();
		    inputAttrIn.put("IBSYSID", ibsysId);
		    inputAttrIn.put("NODE_ID", "eomsProess");
		    IDataset eomsDat = CSAppCall.call( "SS.SubscribeViewInfoSVC.qryEweAttributesByNodeIdIbsysid", inputAttrIn);
			if(eomsDat != null && eomsDat.size() > 0){
				for(int j= 0;j<eomsDat.size();j++){
					IData emossub = eomsDat.getData(j);
					if("BIZRANGE".equals(emossub.getString("ATTR_CODE",""))){
						biaRange=emossub.getString("ATTR_VALUE","");//业务范围
						if("省内跨地市".equals(biaRange)){
					    	bizrange="0";
						}
					}
					if(recordNumNow.equals(emossub.getString("RECORD_NUM",""))&&"CITYA".equals(emossub.getString("ATTR_CODE",""))){
						cityA=emossub.getString("ATTR_VALUE","");//业务范围
					}
					if(recordNumNow.equals(emossub.getString("RECORD_NUM",""))&&"CITYZ".equals(emossub.getString("ATTR_CODE",""))){
						cityZ=emossub.getString("ATTR_VALUE","");//业务范围
					}
					
					
				}
			}
	    }
    	if(!cityName.equals(cityA)&&!cityName.equals(cityZ)){
    		bizrange=null;
    	}
    	if("0".equals(bizrange)){
        	IDataset configList = EweConfigQry.qryByConfigName("HANG_TASKNAME","0");
        	if (DataUtils.isNotEmpty(configList))
            {
            	for(int configListi=0,configListsize=configList.size();configListi<configListsize;configListi++){
            		IData idcOperType=configList.getData(configListi);
            		if(taskName.equals(idcOperType.getString("PARAMNAME"))){//当处于指定环节时，走老挂起接口
            			bizrange=null;
            			break;
            		}
            	}
            }
    	}
        
        IDataset eweNode = EweNodeQry.qryEweNodeByBusiformIdState(busiformId,"0");
        String nodeId = ""; // 等待综资回复
        String busiformNodeId = "";
        if(IDataUtil.isEmpty(eweNode)){
            eweNode = EweNodeQry.qryEweNodeByBusiformIdState(busiformId,"W");
            if(IDataUtil.isNotEmpty(eweNode)){
            	nodeId = eweNode.first().getString("NODE_ID"); // 等待综资回复
                busiformNodeId = eweNode.first().getString("BUSIFORM_NODE_ID");
                if(!"waitUnhangProess".equals(eweNode.first().getString("NODE_ID"))){
                	CSAppException.apperr(CrmCommException.CRM_COMM_103, "该工单节点为"+nodeId+"，无法挂起！");
                }
    	        if("省内跨地市".equals(biaRange)){
//        	        	String busiformId = relaInfo.getString("BUSIFORM_ID","");
    	        	IData paramReleIn = new DataMap();
    	        	paramReleIn.put("BUSIFORM_ID", eweNode.first().getString("BUSIFORM_ID"));
//        	        	paramReleIn.put("VALID_TAG", "0");
    	            IDataset releInfos = Dao.qryByCode("TF_B_EWE_RELE", "SEL_BY_BUSIFORMID", paramReleIn, Route.getJourDb(Route.getCrmDefaultDb()));
//        				IDataset releInfos = WorkformReleQry.qryWorkFormReleByBusiformId(busiformId);--
    				boolean flag=true;//是否找到分支数据
    				for(int i = 0 ; i < releInfos.size() ; i ++)
    				{
    					IData releInfo = releInfos.getData(i);
    					if("HANGCITY_NUM".equals(releInfo.getString("RELE_CODE", ""))&&(
    							(cityA.equals(input.getString("cityName"))&&"1".equals(releInfo.getString("RELE_VALUE", "")))
    						  ||(cityZ.equals(input.getString("cityName"))&&"2".equals(releInfo.getString("RELE_VALUE", "")))
    						  )){
    						flag=false;
    						String subBusiformId = releInfo.getString("SUB_BUSIFORM_ID", "");
    				    	IData paramNode = new DataMap();
    						paramNode.put("BUSIFORM_ID",subBusiformId);
    						paramNode.put("STATE","0");
    					    IDataset eomsNodeList = CSAppCall.call( "SS.EweNodeQrySVC.qryEweNodeByBusiformIdState", paramNode);
//        					    relaInfo.put("BUSIFORM_NODE_ID",eomsNodeList.first().getString("BUSIFORM_NODE_ID"));
    			            if(IDataUtil.isNotEmpty(eweNode)){
    			            	busiformNodeId=eomsNodeList.first().getString("BUSIFORM_NODE_ID");
        					    if(!"waitConfirm".equals(eomsNodeList.first().getString("NODE_ID"))){
        		                	CSAppException.apperr(CrmCommException.CRM_COMM_103, "该工单节点为"+eomsNodeList.first().getString("NODE_ID")+"，无法挂起！");
        		                }
//            					    relaInfo.put("NODE_ID", eomsNodeList.first().getString("NODE_ID"));
        					    nodeId=eomsNodeList.first().getString("NODE_ID");
//            					    relaInfo.put("BUSIFORM_ID", subBusiformId);
        					    busiformId=subBusiformId;
//            					    relaInfo.put("RECORD_NUM", releInfo.getString("RELE_VALUE", ""));
        					    recordNum=releInfo.getString("RELE_VALUE", "");
//            					    relaInfo.put("BPM_TEMPLET_ID", "eomsUnhangProess");
        					    bpmTempletId="eomsUnhangProess";
        					    break;
    			            }
    			            else{
    		                	CSAppException.apperr(CrmCommException.CRM_COMM_103, "该工单节点异常，无法挂起！");
    			            }
    					    
    					}
    				}
    				if(flag){
	                	CSAppException.apperr(CrmCommException.CRM_COMM_103, "该工单节点地市未找到，无法挂起，请重试！");
    				}
    	        }else{
                	CSAppException.apperr(CrmCommException.CRM_COMM_103, "该工单节点异常，无法挂起，或重试一次！");
                }
            }else{
            	CSAppException.apperr(CrmCommException.CRM_COMM_103, "该工单不为跨地市专线，无法挂起！");
            }
            
        }else{
        	nodeId = eweNode.first().getString("NODE_ID"); // 等待综资回复
            busiformNodeId = eweNode.first().getString("BUSIFORM_NODE_ID");
            if(!"waitConfirm".equals(nodeId)){
            	CSAppException.apperr(CrmCommException.CRM_COMM_103, "该工单节点为"+nodeId+"，无法挂起！");
            }
        }
        IDataset results =  new DatasetList();
        IData result =  new DataMap();
        result.put("BUSIFORM_NODE_ID", busiformNodeId);
        result.put("NODE_ID", nodeId);
        result.put("BUSIFORM_ID", busiformId);
        result.put("IBSYSID", ibsysId);
        result.put("RECORD_NUM", recordNum);
        result.put("PRODUCT_ID", productId);
        result.put("BPM_TEMPLET_ID", bpmTempletId);
        if(bizrange!=null){
            result.put("BIZRANGEFLAE", bizrange);
        }
        results.add(result);
        return results;
        //getSubmitInfo(input,ibsysId,recordNum,productId,busiformId,nodeId,busiformNodeId,eomsFlag,bpmTempletId); //驱动到挂起申请节点
        /* IDataset  eweNode = EweNodeQry.qryEweNodeByIbsysidState(ibsysId,"0");
        String hangNode = eweNode.first().getString("NODE_ID"); //挂起节点
        String busiformNodeId = eweNode.first().getString("BUSIFORM_NODE_ID");
        Boolean eomsFlag= true;
        input.put("hangFlag", "true");
        IDataset result = getSubmitInfo(input,ibsysId,recordNum,productId,busiformId,hangNode,busiformNodeId,eomsFlag,bpmTempletId);
        */
       // updateEomsState(ibsysId,productNo,nodeId);
       // return result;
	}
	
	public IDataset replyhangWorkSheet(IData input) throws Exception{
		String serialNo =  getValue("serialNo", input, false);
		String productNo =  getValue("ProductNo", input, false);
		String applyType =  getValue("applyType", input, false);
		String applyResult =  getValue("applyResult", input, false);
		String applyPerson =  getValue("applyPerson", input, false);
		String contactPhone =  getValue("applyPersonContactPhone", input, false);
		String cityName = input.getString("cityName", "");
		String taskName = input.getString("taskName", "");
		IData param = new DataMap();
		param.put("SERIALNO", serialNo);
		param.put("PRODUCT_NO", productNo);
		IDataset emos = WorkformEomsDetailBean.qryEomsStateBySerialNo(param);
		if(IDataUtil.isEmpty(emos)){
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据ESOP工单编号"+serialNo+"和专线实例号"+productNo+"查询无资料，不能办理该业务！");
		}
		String busiState = emos.first().getString("BUSI_STATE");
		/*if(!"G".equals(busiState)&&!"B".equals(busiState)&&!"D".equals(busiState)){
			String valueDesc = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_EWE_CONFIG", new String[]{ "CONFIGNAME", "PARAMVALUE" }, "VALUEDESC", new String[]{ "EOMS_BUSI_STATE", busiState});
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据ESOP工单编号"+serialNo+"和专线实例号"+productNo+"查询工单状态为"+valueDesc+"，不能办理该业务！");
		}*/
		IData params = new DataMap();
		params.put("RECORD_NUM", emos.first().getString("RECORD_NUM"));
		params.put("IBSYSID", emos.first().getString("IBSYSID"));
		
        IData eweData = CSAppCall.callOne("SS.EweNodeQrySVC.qryEweByibsysIdRecordnum", params);//查询是否有子流程
        if(IDataUtil.isEmpty(eweData)){
        	CSAppException.apperr(CrmCommException.CRM_COMM_103, "该工单无子流程，无法挂起！");
        }
        String bpmTempletId = eweData.getString("BPM_TEMPLET_ID");
        String ibsysId = emos.first().getString("IBSYSID");
        String recordNum = emos.first().getString("RECORD_NUM");
        String productId = eweData.getString("BUSI_CODE");
        String busiformId = eweData.getString("SUB_BUSIFORM_ID");
       
        
        IDataset eweNode = EweNodeQry.qryEweNodeByBusiformIdState(busiformId,"0");
        String bizrange=null;
    	String biaRange="";
	    String cityA="";
	    String cityZ="";
	    String recordNumNow=emos.first().getString("RECORD_NUM");
	    if("7012".equals(productId)||"70121".equals(productId)||"70122".equals(productId)){
	    	IData inputAttrIn = new DataMap();
		    inputAttrIn.put("IBSYSID", ibsysId);
		    inputAttrIn.put("NODE_ID", "eomsProess");
		    IDataset eomsDat = CSAppCall.call( "SS.SubscribeViewInfoSVC.qryEweAttributesByNodeIdIbsysid", inputAttrIn);
			if(eomsDat != null && eomsDat.size() > 0){
				for(int j= 0;j<eomsDat.size();j++){
					IData emossub = eomsDat.getData(j);
					if("BIZRANGE".equals(emossub.getString("ATTR_CODE",""))){
						biaRange=emossub.getString("ATTR_VALUE","");//业务范围
						if("省内跨地市".equals(biaRange)){
					    	bizrange="0";
						}
					}
					
					if(recordNumNow.equals(emossub.getString("RECORD_NUM",""))&&"CITYA".equals(emossub.getString("ATTR_CODE",""))){
						cityA=emossub.getString("ATTR_VALUE","");//A端地市
					}
					if(recordNumNow.equals(emossub.getString("RECORD_NUM",""))&&"CITYZ".equals(emossub.getString("ATTR_CODE",""))){
						cityZ=emossub.getString("ATTR_VALUE","");//Z端地市
					}
					
					
				}
			}
	    }
    	if(!cityName.equals(cityA)&&!cityName.equals(cityZ)){
    		bizrange=null;
    	}
    	if("0".equals(bizrange)){
        	IDataset configList = EweConfigQry.qryByConfigName("HANG_TASKNAME","0");
        	if (DataUtils.isNotEmpty(configList))
            {
            	for(int configListi=0,configListsize=configList.size();configListi<configListsize;configListi++){
            		IData idcOperType=configList.getData(configListi);
            		if(taskName.equals(idcOperType.getString("PARAMNAME"))){//当处于指定环节时，走老挂起接口
            			bizrange=null;
            			break;
            		}
            	}
            }
    	}
    	String nodeId = ""; // 等待综资回复
        String busiformNodeId = "";
        if(IDataUtil.isEmpty(eweNode)){
            eweNode = EweNodeQry.qryEweNodeByBusiformIdState(busiformId,"W");
            if(IDataUtil.isNotEmpty(eweNode)){
            	nodeId = eweNode.first().getString("NODE_ID"); // 等待综资回复
                busiformNodeId = eweNode.first().getString("BUSIFORM_NODE_ID");
                if(!"waitUnhangProess".equals(eweNode.first().getString("NODE_ID"))){
                	CSAppException.apperr(CrmCommException.CRM_COMM_103, "该工单节点为"+nodeId+"，无法挂起！");
                }
    	        if("省内跨地市".equals(biaRange)){
//        	        	String busiformId = relaInfo.getString("BUSIFORM_ID","");
    	        	IData paramReleIn = new DataMap();
    	        	paramReleIn.put("BUSIFORM_ID", eweNode.first().getString("BUSIFORM_ID"));
//        	        	paramReleIn.put("VALID_TAG", "0");
    	            IDataset releInfos = Dao.qryByCode("TF_B_EWE_RELE", "SEL_BY_BUSIFORMID", paramReleIn, Route.getJourDb(Route.getCrmDefaultDb()));
//        				IDataset releInfos = WorkformReleQry.qryWorkFormReleByBusiformId(busiformId);--
    				boolean flag=true;//是否找到分支数据
    				for(int i = 0 ; i < releInfos.size() ; i ++)
    				{
    					IData releInfo = releInfos.getData(i);
    					if("HANGCITY_NUM".equals(releInfo.getString("RELE_CODE", ""))&&(
    							(cityA.equals(input.getString("cityName"))&&"1".equals(releInfo.getString("RELE_VALUE", "")))
    						  ||(cityZ.equals(input.getString("cityName"))&&"2".equals(releInfo.getString("RELE_VALUE", "")))
    						  )){
    						flag=false;
    						String subBusiformId = releInfo.getString("SUB_BUSIFORM_ID", "");
    				    	IData paramNode = new DataMap();
    						paramNode.put("BUSIFORM_ID",subBusiformId);
    						paramNode.put("STATE","0");
    					    IDataset eomsNodeList = CSAppCall.call( "SS.EweNodeQrySVC.qryEweNodeByBusiformIdState", paramNode);
//        					    relaInfo.put("BUSIFORM_NODE_ID",eomsNodeList.first().getString("BUSIFORM_NODE_ID"));
    			            if(IDataUtil.isNotEmpty(eweNode)){
    			            	busiformNodeId=eomsNodeList.first().getString("BUSIFORM_NODE_ID");
        					    if(!"waitConfirm".equals(eomsNodeList.first().getString("NODE_ID"))){
        		                	CSAppException.apperr(CrmCommException.CRM_COMM_103, "该工单节点为"+eomsNodeList.first().getString("NODE_ID")+"，无法挂起！");
        		                }
//            					    relaInfo.put("NODE_ID", eomsNodeList.first().getString("NODE_ID"));
        					    nodeId=eomsNodeList.first().getString("NODE_ID");
//            					    relaInfo.put("BUSIFORM_ID", subBusiformId);
        					    busiformId=subBusiformId;
//            					    relaInfo.put("RECORD_NUM", releInfo.getString("RELE_VALUE", ""));
        					    recordNum=releInfo.getString("RELE_VALUE", "");
//            					    relaInfo.put("BPM_TEMPLET_ID", "eomsUnhangProess");
        					    bpmTempletId="eomsUnhangProess";
        					    break;
    			            }
    			            else{
    		                	CSAppException.apperr(CrmCommException.CRM_COMM_103, "该工单节点数据异常，无法挂起！");
    			            }
    					    
    					}
    				}
    				if(flag){
	                	CSAppException.apperr(CrmCommException.CRM_COMM_103, "该工单节点地市未找到，无法挂起，请重试！");
    				}
    	        }
    	        else{
                	CSAppException.apperr(CrmCommException.CRM_COMM_103, "该工单不为跨地市专线，无法挂起！");
                }	
            }else{
            	CSAppException.apperr(CrmCommException.CRM_COMM_103, "该工单节点异常，无法挂起，或重试一次！");
            }
            
        }else{
        	nodeId = eweNode.first().getString("NODE_ID"); // 等待综资回复
            busiformNodeId = eweNode.first().getString("BUSIFORM_NODE_ID");
            if(!"waitConfirm".equals(nodeId)){
            	CSAppException.apperr(CrmCommException.CRM_COMM_103, "该工单节点为"+nodeId+"，无法挂起！");
            }
        }
        IDataset results =  new DatasetList();
        IData result =  new DataMap();
        result.put("BUSIFORM_NODE_ID", busiformNodeId);
        result.put("NODE_ID", nodeId);
        result.put("BUSIFORM_ID", busiformId);
        result.put("IBSYSID", ibsysId);
        result.put("RECORD_NUM", recordNum);
        result.put("PRODUCT_ID", productId);
        result.put("BPM_TEMPLET_ID", bpmTempletId);
        if(bizrange!=null){
            result.put("BIZRANGEFLAE", bizrange);
        }
        results.add(result);
        return results;
	}
	public IDataset conditionReplyhangWorkSheet(IData input) throws Exception{
		IDataset results = new DatasetList();
		IData result =  new DataMap();
		String ibsysId = input.getString("BI_SN");
		String busiformId  = input.getString("BUSIFORM_ID");
		String bpmTempletId  = input.getString("BPM_TEMPLET_ID");
		IDataset releInfo = EweNodeQry.qryBySubBusiformId(busiformId);
		if(IDataUtil.isEmpty(releInfo)){
	        CSAppException.apperr(CrmCommException.CRM_COMM_103, "该工单"+busiformId+"无子流程!");
	    }
		String recordNum =  releInfo.first().getString("RELE_VALUE");
		if("eomsUnhangProess".equals(bpmTempletId)){
			busiformId=releInfo.first().getString("BUSIFORM_ID");
			IDataset releInfoNew = EweNodeQry.qryBySubBusiformId(busiformId);
			if(IDataUtil.isEmpty(releInfoNew)){
		        CSAppException.apperr(CrmCommException.CRM_COMM_103, "该工单"+busiformId+"无子流程!");
		    }
			recordNum =  releInfoNew.first().getString("RELE_VALUE");
		}
		IData agree = WorkformAttrBean.qryAttrByIbsysidRecordCode(ibsysId,"agreeResult",recordNum);
		String agreeResult = agree.getString("ATTR_VALUE");
		if("同意".equals(agreeResult)){
			result.put("applyResult", "1");
		}else{
			result.put("applyResult", "0");
		}
		results.add(result);
		return results;
		
	}
	
	public IDataset applyUnhangWorkSheet(IData input) throws Exception{
		String serialNo =  getValue("serialNo", input, false);
		String productNo =  getValue("ProductNo", input, false);
		String applyType =  getValue("applyType", input, false);
		String applyResult =  getValue("applyResult", input, false);
		String applyPerson =  getValue("applyPerson", input, false);
		String contactPhone =  getValue("applyPersonContactPhone", input, false);
		IData param = new DataMap();
		param.put("SERIALNO", serialNo);
		param.put("PRODUCT_NO", productNo);
		IDataset emos = WorkformEomsDetailBean.qryEomsStateBySerialNo(param);
		if(IDataUtil.isEmpty(emos)){
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据ESOP工单编号"+serialNo+"和专线实例号"+productNo+"查询无资料，不能办理该业务！");
		}
		
		IData params = new DataMap();
		params.put("RECORD_NUM", emos.first().getString("RECORD_NUM"));
		params.put("IBSYSID", emos.first().getString("IBSYSID"));
		
        IData eweData = CSAppCall.callOne("SS.EweNodeQrySVC.qryEweByibsysIdRecordnum", params);//查询是否有子流程
        if(IDataUtil.isEmpty(eweData)){
        	CSAppException.apperr(CrmCommException.CRM_COMM_103, "该工单无子流程，无法解挂！");
        }
        String bpmTempletId = eweData.getString("BPM_TEMPLET_ID");
        String ibsysId = emos.first().getString("IBSYSID");
        String recordNum = emos.first().getString("RECORD_NUM");
        String productId = eweData.getString("BUSI_CODE");
        String busiformId = eweData.getString("SUB_BUSIFORM_ID");//修改非客户原因挂起时导致插入异步表挂在主流程上
        IDataset eweNode = EweNodeQry.qryEweNodeByIbsysidStateRecordNum(ibsysId,"0",recordNum);
        String nodeId = ""; // 等待综资回复
        String busiformNodeId = "";
        if(IDataUtil.isEmpty(eweNode)){//为空查询第二种可能性
            eweNode = EweNodeQry.qryEweNodeByIbsysidStateRecordNum(ibsysId,"W",recordNum);;
            if(IDataUtil.isNotEmpty(eweNode)){
            	nodeId = eweNode.first().getString("NODE_ID"); // 等待综资回复
                busiformNodeId = eweNode.first().getString("BUSIFORM_NODE_ID");
                if(!"waitUnhangProess".equals(eweNode.first().getString("NODE_ID"))){
            		CSAppException.apperr(CrmCommException.CRM_COMM_103, "该工单节点为"+nodeId+"，无法解挂！");
                }
                
        	    String recordNumNow=emos.first().getString("RECORD_NUM");
                String biaRange="";
        	    String cityA="";
        	    String cityZ="";
        	    if("7012".equals(productId)||"70121".equals(productId)||"70122".equals(productId)){
        	    	IData inputAttrIn = new DataMap();
        		    inputAttrIn.put("IBSYSID", ibsysId);
        		    inputAttrIn.put("NODE_ID", "eomsProess");
        		    IDataset eomsDat = CSAppCall.call( "SS.SubscribeViewInfoSVC.qryEweAttributesByNodeIdIbsysid", inputAttrIn);
        			if(eomsDat != null && eomsDat.size() > 0){
        				for(int j= 0;j<eomsDat.size();j++){
        					IData emossub = eomsDat.getData(j);
        					if("BIZRANGE".equals(emossub.getString("ATTR_CODE",""))){
        						biaRange=emossub.getString("ATTR_VALUE","");//业务范围
        					}
        					if(recordNumNow.equals(emossub.getString("RECORD_NUM",""))&&"CITYA".equals(emossub.getString("ATTR_CODE",""))){
        						cityA=emossub.getString("ATTR_VALUE","");//业务范围
        					}
        					if(recordNumNow.equals(emossub.getString("RECORD_NUM",""))&&"CITYZ".equals(emossub.getString("ATTR_CODE",""))){
        						cityZ=emossub.getString("ATTR_VALUE","");//业务范围
        					}
        					
        					
        				}
        			}
        	        if("省内跨地市".equals(biaRange)){
//        	        	String busiformId = relaInfo.getString("BUSIFORM_ID","");
        	        	IData paramReleIn = new DataMap();
        	        	paramReleIn.put("BUSIFORM_ID", eweNode.first().getString("BUSIFORM_ID"));
//        	        	paramReleIn.put("VALID_TAG", "0");
        	            IDataset releInfos = Dao.qryByCode("TF_B_EWE_RELE", "SEL_BY_BUSIFORMID", paramReleIn, Route.getJourDb(Route.getCrmDefaultDb()));
//        				IDataset releInfos = WorkformReleQry.qryWorkFormReleByBusiformId(busiformId);--
        				
        				for(int i = 0 ; i < releInfos.size() ; i ++)
        				{
        					IData releInfo = releInfos.getData(i);
        					if("HANGCITY_NUM".equals(releInfo.getString("RELE_CODE", ""))&&(
        							(cityA.equals(input.getString("cityName"))&&"1".equals(releInfo.getString("RELE_VALUE", "")))
        						  ||(cityZ.equals(input.getString("cityName"))&&"2".equals(releInfo.getString("RELE_VALUE", "")))
        						  )){
        						String subBusiformId = releInfo.getString("SUB_BUSIFORM_ID", "");
        				    	IData paramNode = new DataMap();
        						paramNode.put("BUSIFORM_ID",subBusiformId);
        						paramNode.put("STATE","0");
        					    IDataset eomsNodeList = CSAppCall.call( "SS.EweNodeQrySVC.qryEweNodeByBusiformIdState", paramNode);
        					    if(IDataUtil.isNotEmpty(eweNode)){
        					    	busiformNodeId=eomsNodeList.first().getString("BUSIFORM_NODE_ID");
            					    if(!"waitUnhangWorkSheet".equals(eomsNodeList.first().getString("NODE_ID"))){
            					    	 CSAppException.apperr(CrmCommException.CRM_COMM_103, "该工单子子流程节点为"+eomsNodeList.first().getString("NODE_ID")+"，无法解挂！");
            		                }
//            					    relaInfo.put("NODE_ID", eomsNodeList.first().getString("NODE_ID"));
            					    nodeId=eomsNodeList.first().getString("NODE_ID");
//            					    relaInfo.put("BUSIFORM_ID", subBusiformId);
            					    busiformId=subBusiformId;
//            					    relaInfo.put("RECORD_NUM", releInfo.getString("RELE_VALUE", ""));
            					    recordNum=releInfo.getString("RELE_VALUE", "");
//            					    relaInfo.put("BPM_TEMPLET_ID", "eomsUnhangProess");
            					    bpmTempletId="eomsUnhangProess";
            					    break;
        					    }
	    			            else{
	    		                	CSAppException.apperr(CrmCommException.CRM_COMM_103, "该工单节点数据异常，无法解挂！");
	    			            }
        					    
        					}
        				}
        	        }
        			
        	    }
                
                
                
                
            }else{
            	CSAppException.apperr(CrmCommException.CRM_COMM_103, "该工单节点数据异常，无法解挂！");
            }
            
        }else{
        	nodeId = eweNode.first().getString("NODE_ID"); // 等待资管解挂回复
        	busiformNodeId = eweNode.first().getString("BUSIFORM_NODE_ID");
        	if(!"waitUnhangWorkSheet".equals(nodeId)){
        		CSAppException.apperr(CrmCommException.CRM_COMM_103, "该工单节点为"+nodeId+"，无法解挂！");
        	}
        }
        Boolean eomsFlag = true;
        //IDataset result =  getSubmitInfo(input,ibsysId,recordNum,productId,busiformId,nodeId,busiformNodeId,eomsFlag,bpmTempletId);
        IDataset results = new DatasetList();
        IData result =  new DataMap();
        result.put("BUSIFORM_NODE_ID", busiformNodeId);
        result.put("NODE_ID", nodeId);
        result.put("BUSIFORM_ID", busiformId);
        result.put("IBSYSID", ibsysId);
        result.put("RECORD_NUM", recordNum);
        result.put("PRODUCT_ID", productId);
        result.put("BPM_TEMPLET_ID", bpmTempletId);
        results.add(result);
        return results;
	}
	
	
	 /**
     * 拼数据登记esop数据以及流程数据
     * @param flag
     * @param productId
     * @param oattrs
     * @param ibSysId
     * @param recordNum
     * @return
     * @throws Exception
     */
    public IDataset getSubmitInfo(IData input,String ibsysId,String recordNum,String productId,String busiformId,String nodeId,String busiformNodeId,Boolean eomsFlag,String bpmTempletId) throws Exception {
    	
    	  // 赋值到存储对象中
    	IDataset attrList = new DatasetList();
    	attrList = saveFrontData(input,recordNum);
    	IData submitData = new DataMap();
    	
    	IData other = new DataMap();
    	
    	IData nodeIds = new DataMap();
    	nodeIds.put("NODE_ID", nodeId);
    	
    	IData commonData = new DataMap();
    	commonData.put("PRODUCT_ID", productId);
    	commonData.put("IBSYSID", ibsysId);
    	commonData.put("BUSIFORM_ID", busiformId);
    	commonData.put("BPM_TEMPLET_ID", bpmTempletId);
    	//commonData.put("BUSIFORM_ID", busiformId);
    	commonData.put("NODE_ID", nodeId);
    	
    	/*other.put("RECORD_NUM", recordNum);
    	if(!"".equals(input.getString("applyType",""))){
    		other.put("ATTR_CODE", "applyType");//存入other表的标记
        	other.put("ATTR_VALUE", input.getString("applyType"));
    	}
    	if(!"".equals(input.getString("agreeResult",""))){
    		other.put("ATTR_CODE", "agreeResult");//存入other表的标记
        	other.put("ATTR_VALUE", input.getString("agreeResult"));
    	}
    	if(!"".equals(input.getString("agreeResult",""))){
    		other.put("ATTR_CODE", "agreeResult");//存入other表的标记
        	other.put("ATTR_VALUE", input.getString("agreeResult"));
    	}*/
    	
    	IDataset otherList = new DatasetList();
    	otherList = saveFrontData(input,recordNum);
    	//otherList.add(other);
    	
    	IData eomslist = new DataMap();
    	eomslist.put("RECORD_NUM", recordNum);
    	eomslist.put("BUSI_ID", busiformNodeId);
    	eomslist.put("GROUP_SEQ", "0");
    	submitData.put("NODE_TEMPLETE", nodeIds);
    	submitData.put("COMMON_DATA", commonData);
    	submitData.put("OTHER_LIST", otherList);
    	submitData.put("CUSTOM_ATTR_LIST", attrList);
    	if(eomsFlag){
    		submitData.put("EOMS_LIST", eomslist);
    	}
    	
		IData submitParam = ScrDataSoaTrans.buildWorkformSvcParam(submitData);
		IDataset result =  CSAppCall.call( "SS.WorkformRegisterSVC.register", submitParam);
		//String newSubIbsysId =  result.first().getString("SUB_IBSYSID");   
		//EweNodeQry.updWorkformNodeByPk(busiformNodeId, newSubIbsysId, EcEsopConstants.STATE_VALID);
		commonData.putAll(input);
		EweAsynBean.saveAsynInfo(commonData);
		return result;
    }
    
    /**
     * 更新eoms表数据
     * @param flag
     * @param productId
     * @param oattrs
     * @param ibSysId
     * @param recordNum
     * @return
     * @throws Exception
     */
    public void updateEomsState(String ibsysId,String productNo,String nodeId) throws Exception {
    	 String busiState = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_EWE_CONFIG", new String[]{ "CONFIGNAME", "PARAMNAME" }, "PARAMVALUE", new String[]{ "EOMS_BUSI_STATE", nodeId});
    	 IData updateParam = new DataMap();
     	 updateParam.put("IBSYSID", ibsysId);
         updateParam.put("PRODUCT_NO",productNo);
         updateParam.put("BUSI_STATE",busiState);
         CSAppCall.call("SS.WorkformEomsDetailSVC.updateDetailInfo", updateParam);
    }

	
    
    
    /**
     * 转换产品参数信息
     */
    public static IDataset saveFrontData(IData resultSetDataset,String recordNum) throws Exception
    {
    	IDataset productParamAttrset = new DatasetList();
    	Iterator<String> iterator = resultSetDataset.keySet().iterator();
        while (iterator.hasNext())
        {
            IData productParamAttr = new DataMap();
            String key = iterator.next();
            
	    	 Object value = resultSetDataset.get(key);
	         productParamAttr.put("ATTR_CODE", key);
	         productParamAttr.put("ATTR_VALUE", value);
	         productParamAttr.put("RECORD_NUM", recordNum);
	         if("applyPerson".equals(key)){
	         	productParamAttr.put("ATTR_NAME", "申请人姓名");
	         }else if("applyPersonContactPhone".equals(key)){
	         	productParamAttr.put("ATTR_NAME", "申请人联系电话");
	         }else if("applyReason".equals(key)){
	         	productParamAttr.put("ATTR_NAME", "申请原因");
	         }else if("days".equals(key)){
	         	productParamAttr.put("ATTR_NAME", "延长天数");
	         }else if("applyType".equals(key)){
		        productParamAttr.put("ATTR_NAME", "申请类型");
		     }
	         productParamAttrset.add(productParamAttr);
          
        }
        
		return productParamAttrset;
    	
    }
	
	/**
	 * 判断入参是否为空
	* @Title: getString 
	* @Description: TODO 
	* @param @param key
	* @param @param data
	* @param @param isNull
	* @param @return   
	* @return String    
	* @throws
	 */
	public static String getValue(String key, IData data, boolean isNull) {

		String result = data.getString(key, "");
		if (!isNull && StringUtils.isEmpty(result)) {
			if (StringUtils.isEmpty(key)) {
				String message = "-1:,KEY不能为空!";

				Utility.error(message);
			}else{
				String message = "-1:,"+key+"值不能为空!";

				Utility.error(message);
			}
		}
		return result;
	}


}
