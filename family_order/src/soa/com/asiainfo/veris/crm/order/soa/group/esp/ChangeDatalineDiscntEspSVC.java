package com.asiainfo.veris.crm.order.soa.group.esp;

import java.util.Iterator;

import com.ailk.biz.service.BizRoute;
import com.ailk.biz.util.StaticUtil;
import com.ailk.bizservice.base.CSAppCall;
import com.ailk.bizservice.base.CSBizBean;
import com.ailk.bizservice.dao.Dao;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.pub.consts.EcEsopConstants;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductCompInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.group.esop.query.EopParamTransQry;
import com.asiainfo.veris.crm.order.soa.group.esop.query.EweConfigQry;
import com.asiainfo.veris.crm.order.soa.group.esop.query.EweNodeQry;

public class ChangeDatalineDiscntEspSVC extends CSBizService{

	private static final long serialVersionUID = 1L;
	
	public IDataset changeUserDatalineOrder(IData map) throws Exception
    {
		IDataset result = new DatasetList();
		String ibsysId = map.getString("IBSYSID","");
		String busiformId =  map.getString("BUSIFORM_ID","");
		String cancelTag  = map.getString("CANCEL_TAG","");
		String recordNum = map.getString("RECORD_NUM","");
		String userId = "";
		String mebProductId = "";
		String mebUserId ="";
		if(StringUtils.isBlank(busiformId)){
			CSAppException.apperr(CrmCommException.CRM_COMM_103,"入参BUSIFORM_ID为空！");
		}
		IDataset eweInfo = EweNodeQry.qryEweByBusiformId(busiformId);
		if(DataUtils.isEmpty(eweInfo)){
			 CSAppException.apperr(CrmCommException.CRM_COMM_103,"根据该订单号"+busiformId+"查询产品信息无数据！");
		}
		ibsysId = eweInfo.first().getString("BI_SN");
		IData param = new DataMap();
		param.put("IBSYSID", ibsysId);
		param.put("RECORD_NUM", "0");
		IDataset groupInfos = Dao.qryByCodeParser("TF_B_EOP_PRODUCT", "SEL_BY_PK", param, Route.getJourDb(BizRoute.getRouteId()));
        if(DataUtils.isEmpty(groupInfos))
        {
        	 CSAppException.apperr(CrmCommException.CRM_COMM_103,"根据该订单号查询产品信息无数据！");
        }
	    String productId = groupInfos.first().getString("PRODUCT_ID");
	    userId = groupInfos.first().getString("USER_ID");
	    IDataset lineEomsSubData =  new DatasetList();
	    if("7010".equals(productId)){
	    	lineEomsSubData = DatalineEspUtil.getVoipData(ibsysId,"apply");
	    }else{
	    	param.put("RECORD_NUM", recordNum);
	    	param.put("NODE_ID", "apply");
	    	lineEomsSubData =CSAppCall.call("SS.SubscribeViewInfoSVC.qryEweAttributesByNodeIdIbsysid", param);
	    }
		
		if(DataUtils.isEmpty(lineEomsSubData))
        {
        	 CSAppException.apperr(CrmCommException.CRM_COMM_103,"根据该订单号查询无业务数据！");
        }
		if(!"7010".equals(productId)){
			param.put("RECORD_NUM", recordNum);
			IDataset mebInfos = Dao.qryByCodeParser("TF_B_EOP_PRODUCT_SUB", "SEL_BY_PK", param, Route.getJourDb(BizRoute.getRouteId()));
	        if(DataUtils.isEmpty(mebInfos))
            {
            	 CSAppException.apperr(CrmCommException.CRM_COMM_103,"根据该订单号查询产品信息无数据！");
            }
            mebProductId = mebInfos.first().getString("PRODUCT_ID");
            mebUserId =  mebInfos.first().getString("USER_ID");
            userId = mebUserId;
		}
		String paramName = getParamName(productId);
		//查询出公共信息所需字段	
		IDataset  configInfos = EweConfigQry.qryDistinctValueDescByParamName("PBOSS_CHANGE_INFO_CONFIG", paramName, EcEsopConstants.STATE_VALID);
		if(DataUtils.isEmpty(configInfos))
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据CONFIGNAME:[PBOSS_CHANGE_INFO_CONFIG]，PARAMNAME:["+paramName+"],获取数据失败。请确认TD_B_EWE_CONFIG表数据是否正确！");
		}
		String lineConf = configInfos.first().getString("PARAMVALUE");//StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_EWE_CONFIG", new String[]{ "CONFIGNAME", "PARAMNAME" }, "PARAMVALUE", new String[]{ "PBOSS_CHANGE_INFO_CONFIG", paramName});
		                  		
		int remark = 1;	// 专线条数从1开始
		IDataset lineInfo = new DatasetList();
		IData tempLine = new DataMap();
		for (int i = 0; i < lineEomsSubData.size(); i++) {
			IData data = lineEomsSubData.getData(i);
			if (remark != data.getInt("RECORD_NUM")) {	// 不相等则是新专线
				remark = data.getInt("RECORD_NUM");
				lineInfo.add(tempLine);
				tempLine = new DataMap();
			}
			String fieldName = data.getString("ATTR_CODE", "");
			if (lineConf.contains(fieldName + "|")) {
				if(fieldName.contains("pam_NOTIN_LINE_NUMBER_CODE")){
					tempLine.put(fieldName, data.getInt("ATTR_VALUE") - 1);
				}else if(fieldName.contains("PRODUCTNO")){
					tempLine.put("pam_NOTIN_PRODUCT_NUMBER", data.getString("ATTR_VALUE", ""));
				}
				else{
					tempLine.put(fieldName, data.getString("ATTR_VALUE", ""));
				}
			}
			tempLine.put("USER_ID", userId);
			if("true".equals(cancelTag)){
				if("7010".equals(productId)){
					tempLine.put("USER_ID", userId);
				}else{
					tempLine.put("USER_ID", mebUserId);
				}
			}
			
			
		}
		lineInfo.add(tempLine);	//最后一条专线
		
		//拼写入参
		IData input = new DataMap();
		input.put("BUSI_CTRL_TYPE ", "ChgUs");
		input.put("PRODUCT_ID", productId);
		if("7010".equals(productId)){
			userId= lineInfo.getData(0).getString("USER_ID");
			input.put("USER_ID", lineInfo.getData(0).getString("USER_ID"));
			//input.put("USER_ID", "1119030739636660");
			IData user = new DataMap();
			user = UcaInfoQry.qryGrpInfoByUserId(lineInfo.getData(0).getString("USER_ID"));
			input.put("USER_EPARCHY_CODE", user.getString("EPARCHY_CODE"));
			//input.put("USER_EPARCHY_CODE", "0898");
			IData dataLineInfo = new DataMap();
			IDataset productParam = new DatasetList();
			
	        dataLineInfo.put("ATTR_CODE", "NOTIN_AttrInternet");//专线参数信息
	        dataLineInfo.put("ATTR_VALUE", lineInfo.toString());
	        productParam.add(dataLineInfo);
	        
	        IData productParamInfo = new DataMap();
	        productParamInfo.put("PRODUCT_ID", productId);
	        productParamInfo.put("PRODUCT_PARAM", productParam); // 产品参数信息
			input.put("PRODUCT_PARAM_INFO", IDataUtil.idToIds(productParamInfo));
			input.put("CANCEL_TAG", cancelTag);
			map = DatalineEspUtil.getEosInfo(map,productId,user.getString("EPARCHY_CODE"));
			map.put("RSRV_STR7", "DISCNTCHANGE");
			input.put("EOS", new DatasetList(map));
			result = CSAppCall.call("SS.ChangeVoipUserElementSVC.crtOrder",input);
		}else{
			for(int i =0;i<lineInfo.size();i++){
				IData line = lineInfo.getData(i);
				if(IDataUtil.isEmpty(line)){
					continue;
				}
				IDataset lines  = new DatasetList();
				input.put("USER_ID", userId);
				IData user = new DataMap();
				user = UcaInfoQry.qryGrpInfoByUserId(userId);
				input.put("USER_EPARCHY_CODE", user.getString("EPARCHY_CODE"));
				
				String mebOfferCode = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_EWE_CONFIG", new String[] { "CONFIGNAME", "PARAMNAME" }, "PARAMVALUE", new String[] { "MEM_PRODUCT_ID", productId});
				//查询必选服务和优惠
		        IData ElementsParam = new DataMap();
		        ElementsParam.put("PRODUCT_ID",mebOfferCode);
		        ElementsParam.put("USER_ID", lineInfo.getData(i).getString("USER_ID"));
		        ElementsParam.put("USER_EPARCHY_CODE",  user.getString("EPARCHY_CODE"));
		        IDataset ElementsInfoList = CSAppCall.call("CS.SelectedElementSVC.getGrpUserOpenElements", ElementsParam);
		        IDataset selectElements = ElementsInfoList.getData(0).getDataset("SELECTED_ELEMENTS");
		        DataLineDiscntConst.changeElementIdToProductId(productId,lineInfo.getData(i).getString("USER_ID"),selectElements);
		        IDataset selectElement = new DatasetList();
		        for(int j = 0;j<selectElements.size();j++){
		        	IData element = selectElements.getData(j);
		        	if(DataLineDiscntConst.getElementIdToProductId(productId,lineInfo.getData(i).getString("USER_ID")).equals(element.getString("ELEMENT_ID"))
//		        			||DataLineDiscntConst.datalineElementId.equals(element.getString("ELEMENT_ID"))
		        			||DataLineDiscntConst.imsElementId.equals(element.getString("ELEMENT_ID"))){
		        		element.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
		        		selectElement.add(element);
		        	}
		        }
				IData dataLineInfo = new DataMap();
				IDataset productParam = new DatasetList();
				lines.add(line);
		        dataLineInfo.put("ATTR_CODE", "NOTIN_AttrInternet");//专线参数信息
		        dataLineInfo.put("ATTR_VALUE", lines.toString());
		        productParam.add(dataLineInfo);
		        
		        IData productParamInfo = new DataMap();
		        productParamInfo.put("PRODUCT_ID", productId);
		        productParamInfo.put("PRODUCT_PARAM", productParam); // 产品参数信息
		        productParamInfo.put("ELEMENT_INFO_LIST",selectElement);//已有必选信息
				input.put("PRODUCT_PARAM_INFO", IDataUtil.idToIds(productParamInfo));
				input.put("CANCEL_TAG", cancelTag);
				map = DatalineEspUtil.getEosInfo(map,productId,user.getString("EPARCHY_CODE"));
				map.put("RSRV_STR7", "DISCNTCHANGE");
				input.put("EOS", new DatasetList(map));
				input.put("ELEMENT_INFO_LIST", selectElement);
				if("7011".equals(productId)||"70111".equals(productId)||"70112".equals(productId)){
					result = CSAppCall.call("SS.ChangeNetinMemberElementSVC.crtOrder",input);
				}else if("7012".equals(productId)||"70121".equals(productId)||"70122".equals(productId)){
					result = CSAppCall.call("SS.ChangeDatalineMemberElementSVC.crtOrder",input);
				}else if("7016".equals(productId)){
					result = CSAppCall.call("SS.ChangeImsMemberElementSVC.crtOrder",input);
				}
			}
		}
		if(!"true".equals(cancelTag)){
			//根据user_id查询合同信息
			if("7011".equals(productId) || "7012".equals(productId)|| "7016".equals(productId)
					||"70111".equals(productId) || "70112".equals(productId)||"70121".equals(productId) || "70122".equals(productId)){
				String relaTypeCode = ProductCompInfoQry.getRelaTypeCodeByProductId(productId);
				IDataset list  = RelaUUInfoQry.getGrpRelaUUInfoByUserIdBAndRelaTypeCode(userId,relaTypeCode);//根据userId查询集团userId
				if(DataUtils.isEmpty(list)){
					 CSAppException.apperr(CrmCommException.CRM_COMM_103,"获取集团资料失败！");
				}
				userId = list.first().getString("USER_ID_A");
			}
			//根据user_id 和产品ID查询合同信息
			IData contractinput  =  new DataMap();
			contractinput.put("USER_ID", userId);
			contractinput.put("PRODUCT_ID", productId);
			IDataset contractInfos = CSAppCall.call("CM.ConstractGroupSVC.getContractListByUserId",input);// new DatasetList("[{\"C_NET_TYPE_CODE\":null,\"C_CONTRACT_REMOVE_DATE\":null,\"C_RENEW_END_DATE\":null,\"C_CONTRACT_COMPLETE_DATE\":null,\"C_RSRV_STR1\":null,\"C_RSRV_STR2\":null,\"C_CONTRACT_AUTO_RENEW_CYCLE\":null,\"C_CONTRACT_ID\":\"2018111600119104\",\"C_UPDATE_TIME\":\"2019-03-28 04:03:55\",\"C_CONTRACT_ACCEPT_DATE\":null,\"C_PIGEONHOLE_DATE\":\"2018-11-16 05:11:05\",\"C_RSRV_NUM1\":null,\"C_RSRV_NUM2\":null,\"C_CONTRACT_SUBTYPE_CODE\":null,\"C_RSRV_NUM3\":null,\"C_CONTRACT_WRITE_CITY\":\"海南\",\"C_CLERK_CONTACT_MODE\":null,\"C_CONTRACT_STATE_NOTE\":null,\"C_CONTRACT_MANAGER\":null,\"C_TECHN_CONTACT_INFO\":null,\"C_CONTRACT_WRITE_TYPE\":\"0\",\"C_CONTRACT_IN_DATE\":\"2018-11-30 05:11:34\",\"C_CONT_FEE\":\"23\",\"C_CONTRACT_WRITER\":\"234234\",\"C_PRODUCT_START_DATE\":\"2018-11-16 05:11:22\",\"C_UPDATE_DEPART_ID\":\"36601\",\"C_DEVELOP_STAFF_ID\":\"SUPERUSR\",\"C_CONTRACT_NAME\":\"234234324\",\"C_CONTRACT_RECV_SITE\":null,\"C_DEVELOP_DEPART_ID\":\"35541\",\"C_CONTRACT_END_DATE\":\"2018-11-30 11:11:59\",\"C_TECHN_CONTACT_MODE\":null,\"C_CONTRACT_FILE_ID\":\"111614274062:notes.txt\",\"C_CONTRACT_STATE_CODE\":\"1\",\"C_UPDATE_CITY_CODE\":null,\"C_CLERK_CONTACT_INFO\":null,\"C_REMARK\":null,\"C_CONTRACT_BBOSS_CODE\":null,\"C_RSRV_DATE1\":\"2019-03-28 04:03:55\",\"C_RSRV_DATE2\":null,\"C_CONTRACT_CONMAN\":null,\"C_CONTRACT_CONTENT\":null,\"C_CLERK_STAFF_ID\":null,\"C_CONTRACT_IS_AUTO_RENEW\":\"0\",\"C_PERFER_PALN\":\"23\",\"C_CONTRACT_FLAG\":\"1\",\"C_PIGEONHOLE_DEPART_ID\":\"35541\",\"C_RSRV_TAG2\":null,\"C_RSRV_TAG1\":\"1\",\"C_CONTRACT_TYPE_CODE\":\"0\",\"C_CONTRACT_START_DATE\":\"2018-11-16 05:11:22\",\"C_UPDATE_STAFF_ID\":\"TESTER01\",\"C_TECHN_STAFF_ID\":null,\"C_CONTRACT_WRITE_DATE\":\"2018-11-14 05:11:53\",\"C_RSRV_STR5\":null,\"C_RSRV_STR6\":null,\"C_RSRV_STR3\":null,\"C_RSRV_STR4\":null,\"C_USER_ID\":\"-1\",\"C_POATT_TYPE\":\"1\",\"C_RELA_CONTRACT_ID\":null,\"C_CUST_ID\":\"1114090205222576\",\"C_CONTRACT_LEVEL\":null,\"C_PRODUCT_END_DATE\":\"2018-11-30 11:11:59\",\"C_PIGEONHOLE_STAFF_ID\":\"SUPERUSR\"},{\"CUST_ID\":\"1114090205222576\",\"STATE\":null,\"RSRV_NUM1\":null,\"RSRV_NUM2\":null,\"RSRV_NUM3\":null,\"RSRV_STR9\":\"2018111610248730001\",\"RSRV_STR7\":null,\"RSRV_STR8\":null,\"UPDATE_DEPART_ID\":\"36601\",\"RSRV_STR22\":null,\"RSRV_STR21\":null,\"RSRV_STR20\":null,\"RSRV_STR26\":null,\"RSRV_STR25\":null,\"RSRV_STR24\":null,\"RSRV_STR23\":null,\"REMARK\":null,\"RSRV_STR29\":null,\"RSRV_STR28\":null,\"RSRV_STR27\":null,\"CONTRACT_ID\":\"2018111600119104\",\"UPDATE_TIME\":\"2019-03-28 16:20:55.0\",\"PRODUCT_ID\":\"7010\",\"RSRV_DATE2\":null,\"RSRV_DATE1\":null,\"RSRV_STR5\":null,\"UPDATE_STAFF_ID\":\"TESTER01\",\"RSRV_STR6\":null,\"RSRV_STR3\":\"0\",\"RSRV_STR4\":\"0\",\"RSRV_STR1\":\"10\",\"RSRV_STR11\":null,\"RSRV_STR2\":\"1200\",\"RSRV_STR10\":null,\"RSRV_STR30\":null,\"RSRV_STR15\":null,\"RSRV_STR14\":null,\"RSRV_STR13\":null,\"LINE_NO\":\"2018111610248730001\",\"RSRV_STR12\":null,\"LINE_NAME\":null,\"RSRV_STR19\":null,\"RSRV_TAG2\":null,\"RSRV_STR18\":null,\"RSRV_STR17\":null,\"RSRV_STR16\":null,\"RSRV_TAG1\":null}]");
			if(DataUtils.isEmpty(contractInfos)){
				 CSAppException.apperr(CrmCommException.CRM_COMM_103,"获取合同资费失败！");
			}
			
			IData commdata = contractInfos.first();
			if(IDataUtil.isNotEmpty(commdata)){//公共部分去前缀
				String[] names = commdata.getNames();
		        for(int i = 0; i < names.length; ++i) {
		        	if(names[i].startsWith("C_")) {
		        		commdata.put(names[i].substring(2), commdata.get(names[i]));
		            }
		        }
	            
				
			}
			contractInfos.remove(0); //去除公共部分
			for(int i=0;i<contractInfos.size();i++){
				IData contract = contractInfos.getData(i);
				for(int j=0;j<lineInfo.size();j++){
					IData line = lineInfo.getData(j);
					if(contract.getString("LINE_NO").equals(line.getString("pam_NOTIN_LINE_INSTANCENUMBER"))){
						IDataset params = EopParamTransQry.queryParamTransByOperType("CONTRACT_TRANS");
						doTransData(line,params,contract);
					}
				}
			}
			
			commdata.put("DIRECTLINE", contractInfos);
			commdata.put("PRODUCT_ID", productId);
			System.out.println("zoulu  chgContract:"+commdata);
			result = CSAppCall.call("CM.ConstractGroupSVC.updateDirectlineContract",commdata);
			System.out.println("zoulu result:"+result);
		}
		
		return result;
    }
	
	private void doTransData(IData productAttrInfo, IDataset transDatas,IData contract)
    {
        for (int i = 0; i < transDatas.size(); i++)
        {
            IData transData = transDatas.getData(i);
            String transName = transData.getString("PARAM_CODE","");
            String chgParamCode = transData.getString("CHG_PARAM_CODE","");
            Iterator<String> itr = productAttrInfo.keySet().iterator();
    		while(itr.hasNext())
    		{
    			String key = itr.next();
    			String value = productAttrInfo.getString(key);
    			if(transName.equals(key))
    				contract.put(chgParamCode, value);
    		}
        }
    }

	
	private static String getParamName(String productId) {
		String configName = "CHANGE_";
		if ("7012".equals(productId)||"70121".equals(productId)||"70122".equals(productId)) {
			configName += "DLINE";
		} else if ("7010".equals(productId)){
			configName += "VOIP";
		} else {
			configName += "INET";
		}
		
		return configName;
	}

}
