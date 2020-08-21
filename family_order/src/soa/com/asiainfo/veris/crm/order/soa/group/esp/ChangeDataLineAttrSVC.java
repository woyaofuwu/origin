package com.asiainfo.veris.crm.order.soa.group.esp;

import com.ailk.biz.service.BizRoute;
import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.bizservice.base.CSAppCall;
import com.ailk.bizservice.dao.Dao;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.group.esop.query.EweNodeQry;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformEomsStateBean;

public class ChangeDataLineAttrSVC extends CSBizService{
	
	private static final long serialVersionUID = 1L;
	
	public IDataset changeDataLineAttr(IData map) throws Exception
    {
		String ibsysId = map.getString("IBSYSID","");
		String busiformId =  map.getString("BUSIFORM_ID","");
		if(StringUtils.isBlank(busiformId)){
			CSAppException.apperr(CrmCommException.CRM_COMM_103,"入参BUSIFORM_ID为空！");
		}
		IDataset eweInfo = EweNodeQry.qryEweByBusiformId(busiformId);
		if(DataUtils.isEmpty(eweInfo)){
			 CSAppException.apperr(CrmCommException.CRM_COMM_103,"根据该订单号"+busiformId+"查询产品信息无数据！");
		}
		ibsysId = eweInfo.first().getString("BI_SN");
		IDataset relaInfo = EweNodeQry.qryBySubBusiformId(busiformId);
		if(IDataUtil.isEmpty(relaInfo)){
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据SUB_BUSIFORM_ID:["+busiformId+"]获取数据失败。请确认TF_B_EWE_RELE表数据是否正确！");
		}
		String recordNum = relaInfo.first().getString("RELE_VALUE");
		IData param = new DataMap();
		param.put("IBSYSID", ibsysId);
		param.put("RECORD_NUM", "0");
		IDataset groupInfos = Dao.qryByCodeParser("TF_B_EOP_PRODUCT", "SEL_BY_PK", param, Route.getJourDb(BizRoute.getRouteId()));
        if(DataUtils.isEmpty(groupInfos))
        {
        	 CSAppException.apperr(CrmCommException.CRM_COMM_103,"根据该订单号查询产品信息无数据！");
        }
        String productIdOld = groupInfos.first().getString("PRODUCT_ID");
        String productId = groupInfos.first().getString("PRODUCT_ID");
        IData newParam1  = new DataMap();
        newParam1.put("IBSYSID", ibsysId);
        newParam1.put("RECORD_NUM", "0");
        newParam1.put("ATTR_CODE", "PRODUCT_ID_B");
        IDataset newproductIdInfo = Dao.qryByCodeParser("TF_B_EOP_ATTR", "SEL_BY_IBSYSID_RECORDNUM_ATTRCODE",newParam1 , Route.getJourDb(BizRoute.getRouteId()));
        if(DataUtils.isNotEmpty(newproductIdInfo))
        {
        	productId=newproductIdInfo.first().getString("ATTR_VALUE");
        }
        
        //根据订单号查询变更集团的SERIAL_NUMBER
        IData oldParam  = new DataMap();
        oldParam.put("IBSYSID", ibsysId);
        oldParam.put("RECORD_NUM", "0");
        oldParam.put("ATTR_CODE", "USER_ID_A");
        IDataset oldGrpInfo = Dao.qryByCodeParser("TF_B_EOP_ATTR", "SEL_BY_IBSYSID_RECORDNUM_ATTRCODE",oldParam , Route.getJourDb(BizRoute.getRouteId()));
        if(DataUtils.isEmpty(oldGrpInfo))
        {
        	 CSAppException.apperr(CrmCommException.CRM_COMM_103,"根据该订单号查询原集团无数据！");
        }
        String olduserId =  oldGrpInfo.first().getString("ATTR_VALUE");
        param.put("RECORD_NUM", recordNum);
        IDataset productSub = Dao.qryByCodeParser("TF_B_EOP_PRODUCT_SUB", "SEL_BY_PK", param, Route.getJourDb(BizRoute.getRouteId()));
        if(DataUtils.isEmpty(productSub))
        {
        	 CSAppException.apperr(CrmCommException.CRM_COMM_103,"根据该订单号查询子产品信息无数据！");
        }
        String serialNuber= productSub.first().getString("SERIAL_NUMBER");
        
        //根据订单号查询变更集团的SERIAL_NUMBER
        IData newParam  = new DataMap();
        newParam.put("IBSYSID", ibsysId);
        newParam.put("RECORD_NUM", "0");
        newParam.put("ATTR_CODE", "GRP_SERIAL_NUMBER_B");
        IDataset newGrpInfo = Dao.qryByCodeParser("TF_B_EOP_ATTR", "SEL_BY_IBSYSID_RECORDNUM_ATTRCODE",newParam , Route.getJourDb(BizRoute.getRouteId()));
        if(DataUtils.isEmpty(productSub))
        {
        	 CSAppException.apperr(CrmCommException.CRM_COMM_103,"根据该订单号查询归属集团无数据！");
        }
        String newserialNuber = newGrpInfo.first().getString("ATTR_VALUE");
        
        IData user = UcaInfoQry.qryUserInfoBySnForGrp(newserialNuber);
        IData input  = new DataMap();
        input.put("PRODUCT_ID", productId);
        input.put("PRODUCT_ID_OLD", productIdOld);
        
        input.put("USER_ID", user.getString("USER_ID")); // 新集团的user_id
        input.put("SERIAL_NUMBER", serialNuber); //成员的SERIAL_NUMBER
        input.put("USER_EPARCHY_CODE", user.getString("EPARCHY_CODE"));
        
        input.put("GRP_SERIAL_NUMBER", user.getString("SERIAL_NUMBER")); //新集团的SERIAL_NUMBER
        input.put("OLD_GRP_USERID", olduserId); //老集团的user_id
        
        // input.put("GRP_USER_ID", userId);
        map.put("RECORD_NUM",recordNum);
        map = DatalineEspUtil.getEosInfo(map,productId,user.getString("EPARCHY_CODE"));
        input.put("EOS", new DatasetList(map));
        IDataset result = CSAppCall.call("SS.ChangeDataLineAttrElementSVC.crtOrder",input);
       // IDataset result = GrpInvoker.ivkProduct(input, BizCtrlType.CreateMember, "ChangeUserClass");
        IDataset eomsInfos = WorkformEomsStateBean.qryEomsStateByIbsysidTradeId(ibsysId,recordNum);
        if(IDataUtil.isEmpty(eomsInfos)){
        	CSAppException.apperr(CrmCommException.CRM_COMM_103,"通过IBSYSID:"+ibsysId+"和RECORD_NUM"+recordNum+"查询TF_B_EOP_EOMS_STATE表数据无信息！");
        }
        IData eomsInfo =  eomsInfos.first();
        String productNo =  eomsInfo.getString("PRODUCT_NO");
        //生成台账后修改合同信息
        /**
         * 查询老的集团专线信息，更新老的专线数据为空
         */
        IData contractinputOld  =  new DataMap();
        contractinputOld.put("USER_ID", olduserId);
        contractinputOld.put("PRODUCT_ID", productIdOld);
		IDataset contractInfoOlds = CSAppCall.call("CM.ConstractGroupSVC.getContractListByUserId",contractinputOld);// new DatasetList("[{\"C_NET_TYPE_CODE\":null,\"C_CONTRACT_REMOVE_DATE\":null,\"C_RENEW_END_DATE\":null,\"C_CONTRACT_COMPLETE_DATE\":null,\"C_RSRV_STR1\":null,\"C_RSRV_STR2\":null,\"C_CONTRACT_AUTO_RENEW_CYCLE\":null,\"C_CONTRACT_ID\":\"2018111600119104\",\"C_UPDATE_TIME\":\"2019-03-28 04:03:55\",\"C_CONTRACT_ACCEPT_DATE\":null,\"C_PIGEONHOLE_DATE\":\"2018-11-16 05:11:05\",\"C_RSRV_NUM1\":null,\"C_RSRV_NUM2\":null,\"C_CONTRACT_SUBTYPE_CODE\":null,\"C_RSRV_NUM3\":null,\"C_CONTRACT_WRITE_CITY\":\"海南\",\"C_CLERK_CONTACT_MODE\":null,\"C_CONTRACT_STATE_NOTE\":null,\"C_CONTRACT_MANAGER\":null,\"C_TECHN_CONTACT_INFO\":null,\"C_CONTRACT_WRITE_TYPE\":\"0\",\"C_CONTRACT_IN_DATE\":\"2018-11-30 05:11:34\",\"C_CONT_FEE\":\"23\",\"C_CONTRACT_WRITER\":\"234234\",\"C_PRODUCT_START_DATE\":\"2018-11-16 05:11:22\",\"C_UPDATE_DEPART_ID\":\"36601\",\"C_DEVELOP_STAFF_ID\":\"SUPERUSR\",\"C_CONTRACT_NAME\":\"234234324\",\"C_CONTRACT_RECV_SITE\":null,\"C_DEVELOP_DEPART_ID\":\"35541\",\"C_CONTRACT_END_DATE\":\"2018-11-30 11:11:59\",\"C_TECHN_CONTACT_MODE\":null,\"C_CONTRACT_FILE_ID\":\"111614274062:notes.txt\",\"C_CONTRACT_STATE_CODE\":\"1\",\"C_UPDATE_CITY_CODE\":null,\"C_CLERK_CONTACT_INFO\":null,\"C_REMARK\":null,\"C_CONTRACT_BBOSS_CODE\":null,\"C_RSRV_DATE1\":\"2019-03-28 04:03:55\",\"C_RSRV_DATE2\":null,\"C_CONTRACT_CONMAN\":null,\"C_CONTRACT_CONTENT\":null,\"C_CLERK_STAFF_ID\":null,\"C_CONTRACT_IS_AUTO_RENEW\":\"0\",\"C_PERFER_PALN\":\"23\",\"C_CONTRACT_FLAG\":\"1\",\"C_PIGEONHOLE_DEPART_ID\":\"35541\",\"C_RSRV_TAG2\":null,\"C_RSRV_TAG1\":\"1\",\"C_CONTRACT_TYPE_CODE\":\"0\",\"C_CONTRACT_START_DATE\":\"2018-11-16 05:11:22\",\"C_UPDATE_STAFF_ID\":\"TESTER01\",\"C_TECHN_STAFF_ID\":null,\"C_CONTRACT_WRITE_DATE\":\"2018-11-14 05:11:53\",\"C_RSRV_STR5\":null,\"C_RSRV_STR6\":null,\"C_RSRV_STR3\":null,\"C_RSRV_STR4\":null,\"C_USER_ID\":\"-1\",\"C_POATT_TYPE\":\"1\",\"C_RELA_CONTRACT_ID\":null,\"C_CUST_ID\":\"1114090205222576\",\"C_CONTRACT_LEVEL\":null,\"C_PRODUCT_END_DATE\":\"2018-11-30 11:11:59\",\"C_PIGEONHOLE_STAFF_ID\":\"SUPERUSR\"},{\"CUST_ID\":\"1114090205222576\",\"STATE\":null,\"RSRV_NUM1\":null,\"RSRV_NUM2\":null,\"RSRV_NUM3\":null,\"RSRV_STR9\":\"2018111610248730001\",\"RSRV_STR7\":null,\"RSRV_STR8\":null,\"UPDATE_DEPART_ID\":\"36601\",\"RSRV_STR22\":null,\"RSRV_STR21\":null,\"RSRV_STR20\":null,\"RSRV_STR26\":null,\"RSRV_STR25\":null,\"RSRV_STR24\":null,\"RSRV_STR23\":null,\"REMARK\":null,\"RSRV_STR29\":null,\"RSRV_STR28\":null,\"RSRV_STR27\":null,\"CONTRACT_ID\":\"2018111600119104\",\"UPDATE_TIME\":\"2019-03-28 16:20:55.0\",\"PRODUCT_ID\":\"7010\",\"RSRV_DATE2\":null,\"RSRV_DATE1\":null,\"RSRV_STR5\":null,\"UPDATE_STAFF_ID\":\"TESTER01\",\"RSRV_STR6\":null,\"RSRV_STR3\":\"0\",\"RSRV_STR4\":\"0\",\"RSRV_STR1\":\"10\",\"RSRV_STR11\":null,\"RSRV_STR2\":\"1200\",\"RSRV_STR10\":null,\"RSRV_STR30\":null,\"RSRV_STR15\":null,\"RSRV_STR14\":null,\"RSRV_STR13\":null,\"LINE_NO\":\"2018111610248730001\",\"RSRV_STR12\":null,\"LINE_NAME\":null,\"RSRV_STR19\":null,\"RSRV_TAG2\":null,\"RSRV_STR18\":null,\"RSRV_STR17\":null,\"RSRV_STR16\":null,\"RSRV_TAG1\":null}]");
		if(DataUtils.isEmpty(contractInfoOlds)){
			 CSAppException.apperr(CrmCommException.CRM_COMM_103,"获取合同资费失败！");
		}
		IData commdata = contractInfoOlds.first();
		if(IDataUtil.isNotEmpty(commdata)){//公共部分去前缀
			String[] names = commdata.getNames();
	        for(int i = 0; i < names.length; ++i) {
	        	if(names[i].startsWith("C_")) {
	        		commdata.put(names[i].substring(2), commdata.get(names[i]));
	            }
	        }
            
			
		}
		contractInfoOlds.remove(0);
		
		IData contractProduct = new DataMap();
		for(int i=0;i<contractInfoOlds.size();i++){
			IData contract = contractInfoOlds.getData(i);
			if(contract.getString("LINE_NO").equals(productNo)){
				contractProduct = contract;
				contract.put("RSRV_STR30", "该专线已过户");
				contract.put("STATE", "0");
			}

		}
		
		commdata.put("DIRECTLINE", contractInfoOlds);
		commdata.put("PRODUCT_ID", productIdOld);
		System.out.println("zoulu  chgContractOld:"+commdata);
		CSAppCall.call("CM.ConstractGroupSVC.updateDirectlineContract",commdata);
		System.out.println("zoulu resultOld:");
		
        //新集团的userId查出新的集团合同
		//根据user_id 和产品ID查询合同信息
        IData inparam = new DataMap();
        inparam.put("CUST_ID", user.getString("CUST_ID"));
        inparam.put("PRODUCT_ID", productId);
        inparam.put("LINE_NOS", productNo);
        IDataset contracts = CSAppCall.call("CM.ConstractGroupSVC.qryContractByCustIdProductId", inparam);
       
        if(IDataUtil.isNotEmpty(contracts)){
        	return result;
        }
        if(IDataUtil.isEmpty(contractProduct)){
        	 CSAppException.apperr(CrmCommException.CRM_COMM_103,"根据userId："+olduserId+"和产品编码："+productId+"查询专线实例号:"+productNo+"合同为空");
        }
		IData contractinput  =  new DataMap();
		contractinput.put("USER_ID", user.getString("USER_ID"));
		contractinput.put("PRODUCT_ID", productId);
		IDataset contractInfos = CSAppCall.call("CM.ConstractGroupSVC.getContractListByUserId",contractinput);// new DatasetList("[{\"C_NET_TYPE_CODE\":null,\"C_CONTRACT_REMOVE_DATE\":null,\"C_RENEW_END_DATE\":null,\"C_CONTRACT_COMPLETE_DATE\":null,\"C_RSRV_STR1\":null,\"C_RSRV_STR2\":null,\"C_CONTRACT_AUTO_RENEW_CYCLE\":null,\"C_CONTRACT_ID\":\"2018111600119104\",\"C_UPDATE_TIME\":\"2019-03-28 04:03:55\",\"C_CONTRACT_ACCEPT_DATE\":null,\"C_PIGEONHOLE_DATE\":\"2018-11-16 05:11:05\",\"C_RSRV_NUM1\":null,\"C_RSRV_NUM2\":null,\"C_CONTRACT_SUBTYPE_CODE\":null,\"C_RSRV_NUM3\":null,\"C_CONTRACT_WRITE_CITY\":\"海南\",\"C_CLERK_CONTACT_MODE\":null,\"C_CONTRACT_STATE_NOTE\":null,\"C_CONTRACT_MANAGER\":null,\"C_TECHN_CONTACT_INFO\":null,\"C_CONTRACT_WRITE_TYPE\":\"0\",\"C_CONTRACT_IN_DATE\":\"2018-11-30 05:11:34\",\"C_CONT_FEE\":\"23\",\"C_CONTRACT_WRITER\":\"234234\",\"C_PRODUCT_START_DATE\":\"2018-11-16 05:11:22\",\"C_UPDATE_DEPART_ID\":\"36601\",\"C_DEVELOP_STAFF_ID\":\"SUPERUSR\",\"C_CONTRACT_NAME\":\"234234324\",\"C_CONTRACT_RECV_SITE\":null,\"C_DEVELOP_DEPART_ID\":\"35541\",\"C_CONTRACT_END_DATE\":\"2018-11-30 11:11:59\",\"C_TECHN_CONTACT_MODE\":null,\"C_CONTRACT_FILE_ID\":\"111614274062:notes.txt\",\"C_CONTRACT_STATE_CODE\":\"1\",\"C_UPDATE_CITY_CODE\":null,\"C_CLERK_CONTACT_INFO\":null,\"C_REMARK\":null,\"C_CONTRACT_BBOSS_CODE\":null,\"C_RSRV_DATE1\":\"2019-03-28 04:03:55\",\"C_RSRV_DATE2\":null,\"C_CONTRACT_CONMAN\":null,\"C_CONTRACT_CONTENT\":null,\"C_CLERK_STAFF_ID\":null,\"C_CONTRACT_IS_AUTO_RENEW\":\"0\",\"C_PERFER_PALN\":\"23\",\"C_CONTRACT_FLAG\":\"1\",\"C_PIGEONHOLE_DEPART_ID\":\"35541\",\"C_RSRV_TAG2\":null,\"C_RSRV_TAG1\":\"1\",\"C_CONTRACT_TYPE_CODE\":\"0\",\"C_CONTRACT_START_DATE\":\"2018-11-16 05:11:22\",\"C_UPDATE_STAFF_ID\":\"TESTER01\",\"C_TECHN_STAFF_ID\":null,\"C_CONTRACT_WRITE_DATE\":\"2018-11-14 05:11:53\",\"C_RSRV_STR5\":null,\"C_RSRV_STR6\":null,\"C_RSRV_STR3\":null,\"C_RSRV_STR4\":null,\"C_USER_ID\":\"-1\",\"C_POATT_TYPE\":\"1\",\"C_RELA_CONTRACT_ID\":null,\"C_CUST_ID\":\"1114090205222576\",\"C_CONTRACT_LEVEL\":null,\"C_PRODUCT_END_DATE\":\"2018-11-30 11:11:59\",\"C_PIGEONHOLE_STAFF_ID\":\"SUPERUSR\"},{\"CUST_ID\":\"1114090205222576\",\"STATE\":null,\"RSRV_NUM1\":null,\"RSRV_NUM2\":null,\"RSRV_NUM3\":null,\"RSRV_STR9\":\"2018111610248730001\",\"RSRV_STR7\":null,\"RSRV_STR8\":null,\"UPDATE_DEPART_ID\":\"36601\",\"RSRV_STR22\":null,\"RSRV_STR21\":null,\"RSRV_STR20\":null,\"RSRV_STR26\":null,\"RSRV_STR25\":null,\"RSRV_STR24\":null,\"RSRV_STR23\":null,\"REMARK\":null,\"RSRV_STR29\":null,\"RSRV_STR28\":null,\"RSRV_STR27\":null,\"CONTRACT_ID\":\"2018111600119104\",\"UPDATE_TIME\":\"2019-03-28 16:20:55.0\",\"PRODUCT_ID\":\"7010\",\"RSRV_DATE2\":null,\"RSRV_DATE1\":null,\"RSRV_STR5\":null,\"UPDATE_STAFF_ID\":\"TESTER01\",\"RSRV_STR6\":null,\"RSRV_STR3\":\"0\",\"RSRV_STR4\":\"0\",\"RSRV_STR1\":\"10\",\"RSRV_STR11\":null,\"RSRV_STR2\":\"1200\",\"RSRV_STR10\":null,\"RSRV_STR30\":null,\"RSRV_STR15\":null,\"RSRV_STR14\":null,\"RSRV_STR13\":null,\"LINE_NO\":\"2018111610248730001\",\"RSRV_STR12\":null,\"LINE_NAME\":null,\"RSRV_STR19\":null,\"RSRV_TAG2\":null,\"RSRV_STR18\":null,\"RSRV_STR17\":null,\"RSRV_STR16\":null,\"RSRV_TAG1\":null}]");
		if(DataUtils.isEmpty(contractInfos)){
			 CSAppException.apperr(CrmCommException.CRM_COMM_103,"获取合同资费失败！");
		}
		
		commdata = contractInfos.first();
		if(IDataUtil.isNotEmpty(commdata)){//公共部分去前缀
			String[] names = commdata.getNames();
	        for(int i = 0; i < names.length; ++i) {
	        	if(names[i].startsWith("C_")) {
	        		commdata.put(names[i].substring(2), commdata.get(names[i]));
	            }
	        }
            
			
		}
		contractInfos.remove(0); //去除公共部分
		contractProduct.put("CUST_ID", user.getString("CUST_ID"));
		contractProduct.put("CONTRACT_ID", commdata.getString("CONTRACT_ID"));
		contractProduct.put("STATE", "0");
		contractInfos.add(contractProduct);
		commdata.put("DIRECTLINE", contractInfos);
		commdata.put("PRODUCT_ID", productId);
		System.out.println("zoulu  chgContractNew:"+commdata);
		CSAppCall.call("CM.ConstractGroupSVC.updateDirectlineContract",commdata);
		System.out.println("zoulu resultNew:");
		return result;
		
		
    }

}
