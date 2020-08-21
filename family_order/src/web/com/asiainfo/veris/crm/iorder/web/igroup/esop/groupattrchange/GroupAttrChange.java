package com.asiainfo.veris.crm.iorder.web.igroup.esop.groupattrchange;


import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.web.frame.icsview.common.svcutil.datainfo.uca.IUCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.iorder.web.igroup.esop.esopcommon.EopBasePage;
import com.asiainfo.veris.crm.iorder.web.igroup.esop.esopcommon.ScrDataTrans;
import com.asiainfo.veris.crm.iorder.web.igroup.util.IUpcViewCall;
import com.asiainfo.veris.crm.order.pub.consts.UpcConst;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.ProductPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.upc.UpcViewCall;

public abstract class GroupAttrChange extends EopBasePage {

	public abstract void setGroupInfo(IData groupInfo) throws Exception;

	public abstract void setCustMgrInfo(IData custMgrInfo) throws Exception;
	
	public abstract void setCustInfo(IData custInfo) throws Exception;

	public abstract void setInfo(IData info) throws Exception;

	public abstract void setOffer(IData offer) throws Exception;
	
	public abstract void setOffers(IDataset offers) throws Exception;

	public abstract void setInfos(IDataset dataset);
	
	public abstract void setEcAccountList(IDataset ecAccountList) throws Exception;
	
	IData commData = new DataMap();
	
	IData nodeInfo = new DataMap();
	
	IData offerData = new DataMap();
	
	String groupIdB = "";
	String groupIdAA = "";
	
	String busiFormId = "";
	public void initPageAttr(IRequestCycle cycle) throws Exception {
		IData data =  getData();
		String ibsysId = getData().getString("IBSYSID");
		String productId=  getData().getString("PRODUCT_ID");
		busiFormId = getData().getString("BUSIFORM_ID");
        if (StringUtils.isBlank(ibsysId)) {
            return;
        }
        IData groupInfo =  new DataMap();
        groupInfo.put("IBSYSID", data.getString("IBSYSID")); 
        groupInfo.put("NODE_ID", data.getString("NODE_ID")); 
        groupInfo.put("ATTR_CODE","GROUP_ID"); 
        groupInfo.put("RECORD_NUM","0"); 
        IData group = CSViewCall.callone(this, "SS.WorkformAttrSVC.qryAttrByIbsysidRecordNumNodeId", groupInfo);
        String groupId = group.getString("ATTR_VALUE");
        /*IData workformData = new DataMap();
        if (StringUtils.isNotEmpty(ibsysId)) 
        {
        	 workformData = WorkfromViewCall.qryWorkformSubscribeByIbsysid(this, ibsysId);
             if (IDataUtil.isNotEmpty(workformData)) 
             {
            	 groupId = workformData.getString("GROUP_ID");
     		 }
		}*/
        if (StringUtils.isNotBlank(groupId))
        {
        	queryCustGroupByGroupId(groupId);
		}
        groupIdB = groupId;
        IData productIdBInfo =  new DataMap();
        productIdBInfo.put("IBSYSID", data.getString("IBSYSID")); 
        productIdBInfo.put("NODE_ID", data.getString("NODE_ID")); 
        productIdBInfo.put("ATTR_CODE","PRODUCT_ID_B"); 
        productIdBInfo.put("RECORD_NUM","0"); 
        IData productIdBData = CSViewCall.callone(this, "SS.WorkformAttrSVC.qryAttrByIbsysidRecordNumNodeId", productIdBInfo);
        String productIdB = productIdBData.getString("ATTR_VALUE");
        //查询产品名
        IData offerInfoB = IUpcViewCall.getOfferInfoByOfferCode(productIdB);
        String productIdBName=offerInfoB.getString("OFFER_NAME");
        //查询原集团
        String groupIdA =  data.getString("GROUP_ID");
        groupIdAA = groupIdA;
        IData info = new DataMap();
        
        info.put("PRODUCT_ID_B", productIdB);
        IDataset productIdBList = new DatasetList();
        IData productIdBMap =  new DataMap();
        productIdBMap.put("DATA_ID", productIdB);
        productIdBMap.put("DATA_NAME", productIdBName);
        productIdBList.add(productIdBMap);
        info.put("DATALINE_PRODUCT_ID_B_LIST", productIdBList);
        
        info.put("GROUP_IDB", groupId);
        info.put("GROUP_IDA", groupIdA);
        IData groupInfos =  new DataMap();
        if (StringUtils.isNotBlank(groupIdA))
        {
        	groupInfos = queryGroupByGroupIdA(groupIdA);
		}
        info.putAll(groupInfos);
        //setInfo(info);
        //super.initPage(cycle);
        
        //查询归属集团用户
        IData groupBParam =  new DataMap();
        groupBParam.put("IBSYSID", data.getString("IBSYSID")); 
        groupBParam.put("NODE_ID", data.getString("NODE_ID")); 
        groupBParam.put("ATTR_CODE","GRP_SERIAL_NUMBER_B"); 
        groupBParam.put("RECORD_NUM","0"); 
        IData groupB = CSViewCall.callone(this, "SS.WorkformAttrSVC.qryAttrByIbsysidRecordNumNodeId", groupBParam);
        if(IDataUtil.isEmpty(groupB)){
        	CSViewException.apperr(CrmCommException.CRM_COMM_103, "该工单归属集团用户为空，请查询tf_b_eop_attr表中GRP_SERIAL_NUMBER_B字段是否为空！");
        }
        String serialNumberB =  groupB.getString("ATTR_VALUE");
        IDataset serialNumberList = new DatasetList();
        IData serialNumberMap =  new DataMap();
        serialNumberMap.put("SERIAL_NUMBER", serialNumberB);
        serialNumberList.add(serialNumberMap);
        info.put("DATALINE_SERIAL_NUMBER_LIST", serialNumberList);
        info.put("DATALINE_SERIAL_NUMBER", serialNumberB);
        info.put("PRODUCT_ID", data.getString("PRODUCT_ID"));
        
        //查询专线信息
        IData userIdAInfo =  new DataMap();
        userIdAInfo.put("IBSYSID", ibsysId);
        userIdAInfo.put("RECORD_NUM", "0");
        IData userIdInfo = CSViewCall.callone(this, "SS.WorkformProductSVC.qryEopProductByIbsysId", userIdAInfo);
        if(IDataUtil.isEmpty(userIdInfo)){
        	CSViewException.apperr(CrmCommException.CRM_COMM_103, "该工单集团用户为空，请查询tf_b_eop_product表数据！");
        }
        String userIdA =userIdInfo.getString("USER_ID");
        IData param  = new DataMap();
        param.put("USER_ID", userIdA);
        param.put("PRODUCT_ID", productId);
        param.put("IBSYSID", ibsysId);
        param.put("NODE_ID", data.getString("NODE_ID"));
        IData infos = queryDatalineInfos(param);
        info.putAll(infos);
        info.put("readonly", "true");
        
        //查询产品名
        IData offerInfo = IUpcViewCall.getOfferInfoByOfferCode(productId);
        info.put("OFFER_NAME", offerInfo.getString("OFFER_NAME"));
        info.put("OFFER_CODE", productId);
        //查询公共信息和订单级信息
        param.put("RECORD_NUM", "0");
        IDataset commons =   CSViewCall.call(this, "SS.WorkformAttrSVC.qryEopAttrByIbsysidNodeid", param);
        for(int j=0;j < commons.size();j++){
        	info.put(commons.getData(j).getString("ATTR_CODE"), commons.getData(j).getString("ATTR_VALUE"));
        	if("ATTR_FILE".equals(commons.getData(j).getString("ATTR_CODE"))){
					String file = commons.getData(j).getString("ATTR_VALUE");
					String[] files = file.split("\\|");
				//	fileInfo.put("FILE_NAME", files[0]);
					info.put("CONTRACT_FILE_ID", files[1]);
				//	fileInfos.add(fileInfo);
					
					info.put("CONTRACT_FILE_LIST", file);
					IData fileList =  new DataMap();
					fileList.put("FILE_NAME", files[0]);
					fileList.put("FILE_ID", files[1]);
					fileList.put("ATTACH_TYPE", "P");
					info.put("C_FILE_LIST_ATTACH", fileList);
					info.put("C_CONTRACT_FILE_ID", files[1]);
				
        	}
        	if("BUSI_FILE".equals(commons.getData(j).getString("ATTR_CODE"))){
        		IDataset datas = new DatasetList(
        				commons.getData(j).getString("ATTR_VALUE"));
        		
        		String files = "";
        		String fileIds = "";
				for(int i=0;i<datas.size();i++){
					String file =  datas.getData(i).getString("FILE_NAME");
					String fileId =  datas.getData(i).getString("FILE_ID");
					if(i == datas.size()-1){
						files = files+file;
						fileIds = fileIds +fileId;
					}else{
						files = files+file+",";
						fileIds = fileIds +fileId+",";
					}
				}
				info.put("ATTACH_FILE_LIST", datas.toString());
				info.put("ATTACH_FILE_ID", fileIds);
				info.put("ATTACH_FILE_NAME", files);
        	}
        }
        info.put("IBSYSID", ibsysId);
        info.put("BUSIFORM_ID", busiFormId);
        setGroupInfo(info);
        
        // 加载账户信息
        IData acctInfo = new DataMap();
        if ("1".equals(info.getString("ACCT_TYPE", ""))) {
            acctInfo.put("ACCT_ID", info.getString("ACCT_ID", ""));
        }
        acctInfo.put("OPEN_DATE", info.getString("OPEN_DATE", ""));
        acctInfo.put("REGION_ID", info.getString("REGION_ID", ""));
        acctInfo.put("REGION_NAME", info.getString("REGION_NAME", ""));
        acctInfo.put("CITY_CODE", info.getString("CITY_CODE", ""));
        acctInfo.put("ACCT_NAME", info.getString("ACCT_NAME", ""));
        acctInfo.put("RSRV_STR8", info.getString("RSRV_STR8", ""));
        acctInfo.put("RSRV_STR9", info.getString("RSRV_STR9", ""));
        acctInfo.put("ACCT_TYPE", info.getString("ACCT_TYPE", ""));
        acctInfo.put("ACCT_CONTRACT_ID", info.getString("ACCT_CONTRACT_ID", ""));
        acctInfo.put("SUPER_BANK_CODE", info.getString("SUPER_BANK_CODE", ""));
        acctInfo.put("BANK_CODE", info.getString("BANK_CODE", ""));
        acctInfo.put("BANK_NAME", info.getString("BANK_NAME", ""));
        acctInfo.put("BANK_ACCT_NO", info.getString("BANK_ACCT_NO", ""));
        info.put("ACCT_INFO", acctInfo);
        
        IDataset configInfos = pageutil.getList("TD_B_EWE_CONFIG", "PARAMVALUE", "PARAMNAME", "CONFIGNAME", "MEM_PRODUCT_ID");
        String mebOfferCode = "";
        for (int i = 0; i < configInfos.size(); i++) {
            String paramName = configInfos.getData(i).getString("PARAMNAME");
            if (paramName.equals(productId)) {
                mebOfferCode = configInfos.getData(i).getString("PARAMVALUE");
            }
        }
        IData mebOfferData = UpcViewCall.queryOfferByOfferId(this, UpcConst.ELEMENT_TYPE_CODE_PRODUCT, mebOfferCode, "Y");
        if ("7010".equals(productId)) {
            mebOfferCode = "-1";
        }
        info.put("MEB_OFFER_CODE", mebOfferCode);
        if (IDataUtil.isNotEmpty(mebOfferData)) {
        	info.put("MEB_OFFER_NAME", mebOfferData.getString("OFFER_NAME", ""));
        	info.put("MEB_OFFER_ID", mebOfferData.getString("OFFER_ID", ""));
        }
        setInfo(info);
        
	}
	/**
	 * 根据groupId查询集团信息
	 * @param groupId
	 * @throws Exception
	 */
	private void queryCustGroupByGroupId(String groupId) throws Exception
    {
        IData group = UCAInfoIntfViewUtil.qryGrpCustInfoByGrpId(this, groupId);
        setGroupInfo(group);
        
        String custMgrId = group.getString("CUST_MANAGER_ID");
        if (StringUtils.isNotEmpty(custMgrId))
        {
            IData managerInfo = UCAInfoIntfViewUtil.qryCustManagerByCustManagerId(this, custMgrId);
            setCustMgrInfo(managerInfo);
        }
    }
	
	/**
	 * 根据groupId查询集团信息
	 * @param groupId
	 * @throws Exception
	 */
	public void queryCustGroupByGroupIdCycle(IRequestCycle cycle) throws Exception
    {
		IData data = getData();
		String groupId  = data.getString("GROUP_IDA");
        IData group = UCAInfoIntfViewUtil.qryGrpCustInfoByGrpId(this, groupIdB);
        IData esopInfo = new DataMap();
        esopInfo.put("GROUP_ID", group.getString("GROUP_ID"));
        esopInfo.put("GROUP_NAME", group.getString("CUST_NAME"));
        esopInfo.put("CUST_ID", group.getString("CUST_ID"));
        group.put("ESOP_INFO", esopInfo);
        String servLevel = group.getString("SERV_LEVEL");
        if (StringUtils.isEmpty(servLevel))
        {
            group.put("SERV_LEVEL", "4");
        }
        
        String servLevel2 = StaticUtil.getStaticValue(this.getVisit(), "TD_S_STATIC", new String[] { "TYPE_ID", "DATA_ID" }, "DATA_NAME", new String[] { "CUSTGROUP_SERVLEVEL", group.getString("SERV_LEVEL") });
        group.put("SERV_LEVEL", servLevel2);
        
    	String classId = StaticUtil.getStaticValue(this.getVisit(), "TD_S_STATIC", new String[] { "TYPE_ID", "DATA_ID" }, "DATA_NAME", new String[] { "CUSTGROUP_CLASSID", group.getString("CLASS_ID") });
    	group.put("CLASS_ID", classId);
        //根据集团的地市获取相对应的区县
    	String city = StaticUtil.getStaticValue(this.getVisit(), "TD_M_AREA", new String[] { "AREA_CODE" }, "AREA_NAME", new String[] { group.getString("CITY_CODE") });
        IDataset areaList = StaticUtil.getList(null, "TD_S_STATIC", "DATA_ID", null, new String[]{ "TYPE_ID", "DATA_NAME" }, new String[]{ "CHANGE_AREA_BY_CITY", city});
        IDataset area = new DatasetList();
        for (int i = 0; i < areaList.size(); i++) {
            IData temp = areaList.getData(i);
            temp.put("DATA_ID", temp.getString("DATA_ID", ""));
            temp.put("DATA_NAME", temp.getString("DATA_ID", ""));
            area.add(temp);
		}
        group.put("AREAS", area);
    	
        setGroupInfo(group);
        
        String custMgrId = group.getString("CUST_MANAGER_ID");
        if (StringUtils.isNotEmpty(custMgrId))
        {
            IData managerInfo = UCAInfoIntfViewUtil.qryCustManagerByCustManagerId(this, custMgrId);
            setCustMgrInfo(managerInfo);
        }
        IData info = new DataMap();
        IData grouoInfoA = queryGroupByGroupIdA(groupIdAA);
        info.put("readonly", "true");
        info.putAll(grouoInfoA);
        setInfo(info);
    }
	    
	
	
	public void submit(IRequestCycle cycle) throws Exception {
           IData data =getData();
           String ibsysId = data.getString("IBSYSID");
           IDataset row = new DatasetList(data.getString("ROWDATAS"));
		   IData submitData =new DataMap();
		   String sizeFlag = data.getString("MEBSIZE");
		   IData flag = new DataMap();
		   flag.put("ATTR_CODE", "SIZE_FLAG");
		   flag.put("ATTR_VALUE",sizeFlag);
		   flag.put("ATTR_NAME", "是否全部成员");
		   flag.put("RECORD_NUM", "0");
		   //存附件
		   IData attrFile = new DataMap();
		   attrFile.put("ATTR_CODE", "ATTR_FILE");
		   attrFile.put("ATTR_VALUE", data.getString("ATTR_FILE"));
		   attrFile.put("ATTR_NAME", "过户依据");
		   attrFile.put("RECORD_NUM", "0");
		   IData busiFile = new DataMap();
		   busiFile.put("ATTR_CODE", "BUSI_FILE");
		   busiFile.put("ATTR_VALUE", data.getString("BUSI_FILE"));
		   busiFile.put("ATTR_NAME", "业务协议");
		   busiFile.put("RECORD_NUM", "0");
		 
		   IDataset attrList = new DatasetList();
		   attrList = new DatasetList(data.getString("ATTR_LIST"));
		   attrList.add(flag);
		   attrList.add(attrFile);
		   attrList.add(busiFile);
		   if(StringUtils.isNotEmpty(data.getString("CONTRACT_ID"))){
			   IData contract = new DataMap();
			   contract.put("ATTR_CODE", "CONTRACT_ID");
			   contract.put("ATTR_VALUE", data.getString("CONTRACT_ID"));
			   contract.put("ATTR_NAME", "合同编号");
			   contract.put("ATTR_TYPE", "1");
			   contract.put("RECORD_NUM", "0");
			   attrList.add(contract);
		   }
		  
		   nodeInfo.put("NODE_ID", "apply");	
		   IData offerDatas = new DataMap(data.getString("OFFER_DATA"));
		   nodeInfo.put("PRODUCT_ID",offerDatas.getString("OFFER_CODE"));	
		   
		   //受理集团信息 
		   //productId = offerData.getString("OFFER_CODE");
		   if(StringUtils.isNotBlank(ibsysId)){
			   commData.put("IBSYSID", ibsysId);
			   commData.put("BUSIFORM_ID", data.getString("BUSIFORM_ID"));
			   commData.put("NODE_ID", "apply");
			   submitData.put("IS_READONLY", "true");
			   
		   }
		   commData.put("PRODUCT_ID", offerDatas.getString("OFFER_CODE"));
		   commData.put("PRODUCT_ID_B", offerDatas.getString("OFFER_CODE_B"));
		   commData.put("BUSIFORM_OPER_TYPE", "27");
		   commData.put("IN_MODE_CODE", "0");
		   commData.put("BUSI_CODE", offerDatas.getString("OFFER_CODE"));
		   offerData = UpcViewCall.queryOfferByOfferId(this, "P", offerDatas.getString("OFFER_CODE"),"");
		   
		   IData custData = UCAInfoIntfViewUtil.qryGrpCustInfoByGrpId(this, data.getString("GROUP_ID"));
		  // IData custData = groupAInfo.getData("GROUPA");
		   IData busiSpecRele = new DataMap();
		   busiSpecRele.put("BPM_TEMPLET_ID", "GROUPATTRCHANGE");
		   busiSpecRele.put("BUSI_TYPE", "P");
		   busiSpecRele.put("BUSI_CODE", offerDatas.getString("OFFER_CODE"));
		   submitData.put("NODE_TEMPLETE", nodeInfo);
		   submitData.put("BUSI_SPEC_RELE", busiSpecRele);
		   submitData.put("COMMON_DATA", commData);
		   submitData.put("EOMS_ATTR_LIST", attrList);
		   submitData.put("DIRECTLINE_DATA", new DatasetList(data.getString("DIRECTLINE_DATA")));
		  if(StringUtils.isBlank(ibsysId)){
			   submitData.put("CUST_DATA", custData);
		  }else{
			  submitData.put("CUST_ID", custData.getString("CUST_ID"));
			  submitData.put("CONTRACT_ID", data.getString("CONTRACT_ID"));
			  
		  }
			   submitData.put("OFFER_DATA", new DataMap(data.getString("OFFER_DATA")));
			   submitData.put("ORDER_DATA", new DataMap(data.getString("ORDER_DATA")));
		  // }
		   submitData.put("ATTACH_LIST", new DatasetList(data.getString("ATTACH_LIST")));
		   submitData.put("CONTRACT_DATA", new DatasetList(data.getString("CONTRACT_DATA")));
		   
		   attrList  = sendRowData(row,attrList);
		   checkContractDate(submitData);
		   try{
			   
			   submitData = buildAttrOtherSvcParam(submitData);
			   IData submitParam = ScrDataTrans.buildWorkformSvcParam(submitData);
			   if(StringUtils.isNotBlank(ibsysId)){
				   IData map = new DataMap();
				   map.put("IBSYSID", ibsysId);
				   CSViewCall.call(this, "SS.WorkformProductSVC.delProductByIbsysid", map);
			   }
			   IDataset result = CSViewCall.call(this, "SS.WorkformRegisterSVC.register", submitParam);
			   this.setAjax(result.first());
		   }catch(Exception e){
			   CSViewException.apperr(CrmCommException.CRM_COMM_103, "调用登记服务失败！"+e.getMessage());
		   }
		   //submitData.put("EOMS_ATTR_LIST", attrList);
		 		/*   IData res = new DataMap();
		   res.put("IBSYSID", result.getData(0).getString("IBSYSID"));
		   res.put("SUB_IBSYSID", result.getData(0).getString("SUB_IBSYSID"));
		   res.put("NODE_ID", result.getData(0).getString("NODE_ID"));
		   res.put("attr", attrList);
		   
		   CSViewCall.call(this, "SS.WorkFormInsertAttrSVC.insetAttr", res);*/
		   
		   
	}
	public IData buildAttrOtherSvcParam(IData param) throws Exception {
		
		 
		 // getGrpSN(param);
        IDataset contractData = param.getDataset("CONTRACT_DATA");
        IData temp = null;
        String isReadonly = param.getString("IS_READONLY", "");
        // 审核通过后会更新合同，所以审核不通过再次提交时，合同ID不用变。
        if (!"true".equals(isReadonly)) {
            if (IDataUtil.isNotEmpty(contractData)) {
                for (int i = 0; i < contractData.size(); i++) {
                    temp = contractData.getData(i);
                    if (temp.getString("ATTR_CODE").equals("C_IF_NEW_CONTRACT")) {
                        if (temp.getString("ATTR_VALUE").equals("0")) {
                        	param = getAttrContractId(param);
                            break;
                        }
                    }
                }
            }
        } else {
        	 	IData input = new DataMap();
		        IDataset contractDataInfo = param.getDataset("CONTRACT_DATA");
		        if (IDataUtil.isNotEmpty(contractDataInfo)) {
		        	  for (int i = 0; i < contractDataInfo.size(); i++) {
				            temp = new DataMap();
				            IData workformAttr = new DataMap();
				            temp = contractData.getData(i);
				            workformAttr = new DataMap();
				            String key = temp.getString("ATTR_CODE");
				            if (key.startsWith("C_")) {
				                workformAttr.put(key.substring(2), temp.getString("ATTR_VALUE"));
				                input.putAll(workformAttr);
				            }
				        }
		        	  	IDataset attachs = param.getDataset("ATTACH_LIST");
				        for(int j = 0;j<attachs.size();j++){
				        	IData attach = attachs.getData(j);
				        	String attachType = attach.getString("ATTACH_TYPE");
				        	if("C".equals(attachType)){
				        		input.put("CONTRACT_FILE_ID", attach.getString("FILE_ID")+":"+attach.getString("FILE_NAME"));
				        	}
				        	
				        }
				        input.put("IF_NEW_CONTRACT","1");
				  //      input.put("CUST_ID", param.getString("CUST_ID"));
				        input.put("CONTRACT_ID", param.getString("CONTRACT_ID"));
				        input.put("PRODUCT_ID", param.getData("COMMON_DATA").getString("PRODUCT_ID_B"));
				        IDataset directLine = new DatasetList();
				        IData tempc = new DataMap();
				        tempc.put("ATTR_VALUE", param.getString("CONTRACT_ID"));
				        tempc.put("ATTR_NAME", "合同编号");
				        tempc.put("ATTR_CODE", "CONTRACT_ID");
				        contractData.add(temp);
				        param.put("CONTRACT_DATA", contractData);
				        input.put("DIRECTLINE", directLine);
				        CSViewCall.call(this, "CM.ConstractGroupSVC.updateDirectlineContract", input);
		        }
		      
            }

            // 删除产品信息，避免再次添加报错
         //   param.remove("OFFER_DATA");
            return param;
     }
	private IDataset sendRowData(IDataset rowData,IDataset attrList)throws Exception
	{
		 IDataset rowDataNew=new DatasetList();
		 rowDataNew=rowData;
		 String map[]={"tag","TRADE_ID_SUB","END_DATE","START_DATE","USER_ID_B","SERIAL_NUMBER_B"};
		 for(int j=0,sizej=map.length;j<sizej;j++){
			 if(rowDataNew.toString().length()<2000){
				 break;
			 }
			 String str=map[j];
			 for(int i=0,size=rowDataNew.size();i<size;i++){
				 IData rowDataMap= rowDataNew.getData(i);
				 rowDataMap.remove(str);
//				 IData rowDataNowMap = new DataMap();
//				 rowDataNowMap.put("PRODUCTNO", rowDataMap.getString("PRODUCTNO",""));
//				 rowDataNowMap.put("SERIAL_NUMBER_B", rowDataMap.getString("SERIAL_NUMBER_B",""));
//				 rowDataNew.add(rowDataNowMap);
			 }
		 }
		 if(rowDataNew.toString().length()>2000){
        	 CSViewException.apperr(CrmCommException.CRM_COMM_103, "过户专线条数过多，请减少条数并分多笔订单过户！");
		 }
		 
		
		 IData size = new DataMap();
		 size.put("ATTR_CODE", "rowData");
		 size.put("ATTR_VALUE",rowDataNew);
		 size.put("ATTR_NAME", "过户成员专线");
		 size.put("RECORD_NUM", "0");
		
		attrList.add(size);
		return attrList;
	}
	
	
	/**
	 * 查询集团客户信息
	 * 
	 * @param cycle
	 * @throws Exception
	 */
	public void queryGroupByGroupId(IRequestCycle cycle) throws Exception {
		//查询集团客户信息
		String groupId = getData().getString("GROUP_IDA");
		String offerCode =  getData().getString("PRODUCT_ID");
		IData groupInfo = UCAInfoIntfViewUtil.qryGrpCustInfoByGrpId(this, groupId);
		//groupAInfo.put("GROUPA", groupInfo);
		IDataset configInfos = pageutil.getList("TD_B_EWE_CONFIG", "PARAMVALUE", "PARAMNAME", "CONFIGNAME", "MEM_PRODUCT_ID");
        String mebOfferCode = "";
        for (int i = 0; i < configInfos.size(); i++) {
            String paramName = configInfos.getData(i).getString("PARAMNAME");
            if (paramName.equals(offerCode)) {
                mebOfferCode = configInfos.getData(i).getString("PARAMVALUE");
            }
        }
        IData mebOfferData = UpcViewCall.queryOfferByOfferId(this, UpcConst.ELEMENT_TYPE_CODE_PRODUCT, mebOfferCode, "Y");
        if ("7010".equals(offerCode)) {
            mebOfferCode = "-1";
        }
        groupInfo.put("MEB_OFFER_CODE", mebOfferCode);
        if (IDataUtil.isNotEmpty(mebOfferData)) {
        	groupInfo.put("MEB_OFFER_NAME", mebOfferData.getString("OFFER_NAME", ""));
        	groupInfo.put("MEB_OFFER_ID", mebOfferData.getString("OFFER_ID", ""));
        }
        groupInfo.put("OFFER_CODE", offerCode);
        IData offerInfo = IUpcViewCall.getOfferInfoByOfferCode(offerCode);
        groupInfo.put("OFFER_ID", offerInfo.getString("OFFER_ID"));
        groupInfo.put("OFFER_NAME", offerInfo.getString("OFFER_NAME"));
		
		if(IDataUtil.isNotEmpty(groupInfo)){
			//查询客户经理信息
			String custMgrId = groupInfo.getString("CUST_MANAGER_ID");
			if (StringUtils.isNotEmpty(custMgrId)) {
				IData managerInfo = UCAInfoIntfViewUtil.qryCustManagerByCustManagerId(this, custMgrId);
				this.setCustInfo(managerInfo);
			
			}
			
	        IDataset offers = new DatasetList();
	     
	        String custId= groupInfo.getString("CUST_ID");
	        //根据集团查询已订购的专线产品
	        IDataset insOffers = IUCAInfoIntfViewUtil.qryUserAndProductByCustIdForGrp(this, custId);
		
		 if (DataUtils.isNotEmpty(insOffers))
	        {
	            String groupName = "";
	            IData groupData = UCAInfoIntfViewUtil.qryGrpCustInfoByGrpCustId(this, custId);
	            if (DataUtils.isNotEmpty(groupData))
	            {
	                groupId = groupData.getString("GROUP_ID");
	                groupName = groupData.getString("CUST_NAME");
	            }
	            for (int j = 0, size = insOffers.size(); j < size; j++)
	            {
	                IData insoffer = insOffers.getData(j);
	                String productId = insoffer.getString("PRODUCT_ID");
	                //只展示专线订购信息
	                if(StringUtils.isNotEmpty(offerCode) && productId.equals(offerCode)){
	              /*  	
	                }
	                if("7011".equals(productId)||"7012".equals(productId)){*/
	                	IData PMoffer = UpcViewCall.queryOfferByOfferId(this, "P", productId,"");

	                	if(DataUtils.isEmpty(PMoffer)){
	                		continue;
	                	}
	                	
	                	IData param = new DataMap();
	                	param.put("PRODUCT_ID", productId);
	                	nodeInfo.put("PRODUCT_ID", productId);
	                	//如果树上没有，直接remove
	                	IDataset productTypeDataset = CSViewCall.call(this, "CS.GroupCatalogsQrySVC.getProductTypesByProductId", param);
	                	
	                	if (DataUtils.isEmpty(productTypeDataset))
	                	{
	                		continue;
	                	}
	                	String accessNum = insoffer.getString("SERIAL_NUMBER");
	                	String subscriberInsId = insoffer.getString("USER_ID");
	                	IData offer = new DataMap();
	                	offer.put("USER_ID", subscriberInsId);
	                	offer.put("SERIAL_NUMBER", accessNum);
	                	offer.put("OFFER_CODE", productId);
	                	offer.put("OFFER_ID", PMoffer.getString("OFFER_ID", ""));
	                	offer.put("OFFER_NAME", PMoffer.getString("OFFER_NAME", ""));
	                	offer.put("BRAND_CODE", insoffer.getString("BRAND_CODE", ""));
	                	offer.put("GROUP_ID", groupId);
	                	offer.put("GROUP_NAME", groupName);
	                	offer.put("PRODUCT_ID", productId);
	                	offers.add(offer);
	                }
	            }
	        }
	        
	      //根据登录工号权限筛选可以办理的产品
	        if (IDataUtil.isNotEmpty(offers))
	        {
	            for (int i = 0, size = offers.size(); i < size; i++)
	            {
	                IData offer = offers.getData(i);
	                offer.put("PRODUCT_ID", offer.getString("OFFER_CODE"));
	                offer.put("PRODUCT_NAME", offer.getString("OFFER_NAME"));
	            }
	        }
	        ProductPrivUtil.filterProductListByPriv(this.getVisit().getStaffId(), offers);
	        
	        setInfos(offers);
	        
		}
		String isready =  getData().getString("IS_READONLY","");
		groupInfo.put("readonly", isready);
		this.setInfo(groupInfo);
		this.setAjax(groupInfo);
		if("true".equals(isready)){
			IData info = new DataMap();
			 IDataset serialNumberList = new DatasetList();
		        IData serialNumberMap =  new DataMap();
		        String serialNumberB  = getData().getString("GRP_SN","");
		        serialNumberMap.put("SERIAL_NUMBER", serialNumberB);
		        serialNumberList.add(serialNumberMap);
		        info.put("DATALINE_SERIAL_NUMBER_LIST", serialNumberList);
		        info.put("DATALINE_SERIAL_NUMBER", serialNumberB);
	        this.setGroupInfo(info);
		}else{
			queryCustGroupByGroupId(cycle);
		}
	}
	
	public void queryDatalineInfos(IRequestCycle cycle) throws Exception
    {
		IData data = this.getData();
	    String  userId = data.getString("USER_ID");
		String  productId = data.getString("productId");
		commData.put("PRODUCT_ID", productId);
		commData.put("USER_ID", userId);
		IDataset result = CSViewCall.call(this,"SS.WorkFormSVC.qryDatalineTradeAttrInfos", data);
		this.setOffers(result);
		setAjax(result); 
		IData paramAttr = new DataMap();
     	paramAttr.put("USER_ID", userId);
     	IData info =  new DataMap();
 		IDataset commonData = CSViewCall.call(this, "CS.UserAttrInfoQrySVC.queryUserAllAttrs", paramAttr); //根据user_id查询公共信息
 		for (int i = 0; i < commonData.size(); i++) {
 			String key = commonData.getData(i).getString("ATTR_CODE");
 			String value = commonData.getData(i).getString("ATTR_VALUE");
 			info.put(key, value);
 		}
 		info.put("USER_ID_A", userId);
 		this.setInfo(info);
    }
	
	public IData queryDatalineInfos(IData data) throws Exception
    {
	    String  userId = data.getString("USER_ID");
		String  productId = data.getString("PRODUCT_ID");
		commData.put("PRODUCT_ID", productId);
		commData.put("USER_ID", userId);
		IDataset results  =  new DatasetList();
		results  =  CSViewCall.call(this,"SS.WorkformProductSubSVC.qryProductByIbsysid", data);
		for(int i = 0;i < results.size();i++){
			IData result =  results.getData(i);
			data.put("RECORD_NUM", result.getString("RECORD_NUM"));
			IDataset datas =  CSViewCall.call(this,"SS.WorkformAttrSVC.qryEopAttrByIbsysidNodeid", data);
			for(int j=0;j < datas.size();j++){
				result.put(datas.getData(j).getString("ATTR_CODE"), datas.getData(j).getString("ATTR_VALUE"));
			}
		}
		//IDataset result = CSViewCall.call(this,"SS.WorkFormSVC.qryDatalineTradeAttrInfos", data);
		this.setOffers(results);
		IData paramAttr = new DataMap();
     	paramAttr.put("USER_ID", userId);
     	IData info =  new DataMap();
 		IDataset commonData = CSViewCall.call(this, "CS.UserAttrInfoQrySVC.queryUserAllAttrs", paramAttr); //根据user_id查询公共信息
 		for (int i = 0; i < commonData.size(); i++) {
 			String key = commonData.getData(i).getString("ATTR_CODE");
 			String value = commonData.getData(i).getString("ATTR_VALUE");
 			info.put(key, value);
 		}
 		info.put("USER_ID_A", userId);
 		return info;
    }
	
	
	  /**
     * 查询集团客户信息
     * @param cycle
     * @throws Exception
     */
    public void queryCustGroupByGroupId(IRequestCycle cycle) throws Exception
    {
    	IData info = new DataMap();
        String groupId = getData().getString("GROUP_IDB");
        String offerCode = getData().getString("PRODUCT_ID");
    	IData groupInfo = UCAInfoIntfViewUtil.qryGrpCustInfoByGrpId(this, groupId);
        String custId = groupInfo.getString("CUST_ID");
        IData input = new DataMap();
        input.put("CUST_ID", custId);
        input.put("PRODUCT_ID", offerCode);
        IDataset datalineSerialNumberList = CSViewCall.call(this, "CS.UserInfoQrySVC.getUserInfoByCstIdProIdForGrp", input);
        info.put("DATALINE_SERIAL_NUMBER_LIST", datalineSerialNumberList);
        info.put("PRODUCT_ID", offerCode);
        if(IDataUtil.isNotEmpty(datalineSerialNumberList)){
        	info.put("OFFER_NAME", datalineSerialNumberList.first().getString("PRODUCT_NAME"));
        }
        IDataset productBList=StaticUtil.getList(null, "TD_S_STATIC","DATA_ID","DATA_NAME",
        		new String[] {
                "TYPE_ID", "PDATA_ID"
            }, new String[] {
                "EOP_PRODUCT_ID_B_"+offerCode, offerCode
            });
        info.put("DATALINE_PRODUCT_ID_B_LIST", productBList);
        info.put("PRODUCT_ID_B", offerCode);

        this.setGroupInfo(info);
        setAjax(datalineSerialNumberList);
    }

    

    /**
     * 检查合同是否失效
     * 
     * @param param
     * @throws Exception
     */
    public void checkContractDate(IData param) throws Exception {
    	IData offerdata =  param.getData("OFFER_DATA");
    	String userId = offerdata.getString("USER_ID");
    	 IData input = new DataMap();
    	 input.put("USER_ID", userId);
    	 input.put("REMOVE_TAG", "0");
        IDataset userInfos = CSViewCall.call(this, "CS.UserInfoQrySVC.queryUserInfoByUserIdAndTag", input);
            if(IDataUtil.isNotEmpty(userInfos)) {
            	String serialNumber = userInfos.getData(0).getString("SERIAL_NUMBER");
            	IData contractInput = new DataMap();
            	contractInput.put("CONTRACT_ID", userInfos.getData(0).getString("CONTRACT_ID"));
            	IDataset contracts = CSViewCall.call(this, "CS.CustContractInfoQrySVC.qryContractInfoByContractIdForGrp", contractInput);
            	if (null != contracts && contracts.size() > 0) {
            		for (int i = 0; i < contracts.size(); i++) {
            			IData temp = contracts.getData(i);
            			if (SysDateMgr.compareTo(SysDateMgr.getSysTime(),temp.getString("CONTRACT_END_DATE")) > 0) {
            				 CSViewException.apperr(CrmCommException.CRM_COMM_103, "该产品服务号【" +serialNumber+ "】的合同已失效，请先维护合同再办理业务！");
            			}
            		}
            	}
            	
            }
        

    }

    /**
     * 查询账户信息
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryEcAccountList(IRequestCycle cycle) throws Exception {
        String custId = this.getData().getString("CUST_ID");
        IDataset accounts = UCAInfoIntfViewUtil.qryGrpAcctInfosByCustId(this, custId);
        setEcAccountList(accounts);

    }
    
    /**
     * 初始化获得该集团下的合同
     * 
     * @param cycle
     * @throws Exception
     */
    public void initContract(IRequestCycle cycle) throws Exception {
        IData info = new DataMap();
        IData param = new DataMap();
        String iFNew = getData().getString("IF_NEW");
        String custId = getData().getString("CUST_ID");
        String serialNumber = getData().getString("SERIAL_NUMBER");
        // 审核不通过，重回APPLY节点记号
        String isReadonly = getData().getString("IS_READONLY");
        if (iFNew.equals("1")) {
            param.put("SERIAL_NUMBER", serialNumber);
            param.put("REMOVE_TAG", "0");
            IDataset userInfos = CSViewCall.call(this, "CS.GroupCatalogsQrySVC.qrySelAllBySn", param);
            if(IDataUtil.isNotEmpty(userInfos)) {
            	IData contractInput = new DataMap();
            	contractInput.put("CONTRACT_ID", userInfos.getData(0).getString("CONTRACT_ID"));
            	IDataset contracts = CSViewCall.call(this, "CS.CustContractInfoQrySVC.qryContractInfoByContractIdForGrp", contractInput);
            	IDataset c = new DatasetList();
            	if (null != contracts && contracts.size() > 0) {
            		for (int i = 0; i < contracts.size(); i++) {
            			IData temp = contracts.getData(i);
            			if (null != temp.getString("CONTRACT_NAME")) {
            				temp.put("CONTRACT", temp.getString("CONTRACT_ID", "") + "|" + temp.getString("CONTRACT_NAME", ""));
            				c.add(temp);
            			}
            		}
            	}
            	info.put("CONTRACTS", c);
            }
        } else if (iFNew.equals("0")) {
            String staffId = getVisit().getStaffId();
            String departId = getVisit().getDepartId();
            info.put("C_CUST_ID", custId);
            info.put("C_DEVELOP_STAFF_ID", staffId);
            info.put("C_DEVELOP_DEPART_ID", departId);
        }

        if ("true".equals(isReadonly)) {
            IData input = new DataMap();
            input.put("IBSYSID", getData().getString("IBSYSID"));
            input.put("NODE_ID", "apply");
            input.put("RECORD_NUM", "0");
            IDataset attrDatas = CSViewCall.call(this, "SS.WorkformAttrSVC.getNewInfoByIbsysidAndNodeId", input);
            if (IDataUtil.isNotEmpty(attrDatas)) {
                for (int i = 0; i < attrDatas.size(); i++) {
                    info.put(attrDatas.getData(i).getString("ATTR_CODE"), attrDatas.getData(i).getString("ATTR_VALUE"));
                }
            }

            if("1".equals(info.getString("C_IF_NEW_CONTRACT"))) {

                param.put("SERIAL_NUMBER", serialNumber);
                param.put("REMOVE_TAG", "0");
                IDataset userInfos = CSViewCall.call(this, "CS.GroupCatalogsQrySVC.qrySelAllBySn", param);
                if(IDataUtil.isNotEmpty(userInfos)) {
                    IData contractInput = new DataMap();
                    contractInput.put("CONTRACT_ID", userInfos.getData(0).getString("CONTRACT_ID"));
                    IDataset contracts = CSViewCall.call(this, "CS.CustContractInfoQrySVC.qryContractInfoByContractIdForGrp", contractInput);
                    IDataset c = new DatasetList();
                    if(null != contracts && contracts.size() > 0) {
                        for (int i = 0; i < contracts.size(); i++) {
                            IData temp = contracts.getData(i);
                            if(null != temp.getString("CONTRACT_NAME")) {
                                temp.put("CONTRACT", temp.getString("CONTRACT_ID", "") + "|" + temp.getString("CONTRACT_NAME", ""));
                                c.add(temp);
                            }
                        }
                    }
                    info.put("CONTRACTS", c);
                }

                String contractId = info.getString("CONTRACT_ID");
                IData cparam = new DataMap();
                cparam.put("CONTRACT_ID", contractId);
                cparam.put("CUST_ID", custId);
                IData contractInfo = CSViewCall.callone(this, "SS.GrpLineInfoQrySVC.queryContractInfo", cparam);
                String contractStr = contractInfo.getString("CONTRACT_ID") + "|" + contractInfo.getString("CONTRACT_NAME");
                //contractStr = contractStr.replace(":", "|");
                info.put("CONTRACT", contractStr);
            } else {
                // 获取合同附件
                String subIbsysid = attrDatas.first().getString("SUB_IBSYSID");
                IData inparam = new DataMap();
                inparam.put("IBSYSID", getData().getString("IBSYSID"));
                inparam.put("SUB_IBSYSID", subIbsysid);
                inparam.put("RECORD_NUM", "0");
                IDataset attachList = CSViewCall.call(this, "SS.WorkFormSVC.queryByAttach", inparam);
                if(IDataUtil.isNotEmpty(attachList)) {
                    for (int i = 0; i < attachList.size(); i++) {
                        IData attachData = attachList.getData(i);
                        if("C".equals(attachData.getString("ATTACH_TYPE"))) {
                            info.put("C_CONTRACT_FILE_ID", attachData.getString("FILE_ID"));
                            IData fileData = new DataMap();
                            fileData.put("FILE_ID", attachData.getString("FILE_ID"));
                            fileData.put("FILE_NAME", attachData.getString("ATTACH_NAME"));
                            fileData.put("ATTACH_TYPE", attachData.getString("ATTACH_TYPE"));
                            // info.put("C_FILE_LIST", fileData.toString());
                            setAjax(fileData);
                        }
                    }
                }
            }

        }

        setInfo(info);
    }
    
    /**
	 * 查询集团客户信息
	 * 
	 * @param cycle
	 * @throws Exception
	 */
	public void queryGroupByGroupIdA(IRequestCycle cycle) throws Exception {
		// 查询集团客户信息
		IData data = getData();
		String grouIdA = data.getString("GROUP_ID");
		IData groupInfo = UCAInfoIntfViewUtil.qryGrpCustInfoByGrpId(this,
				grouIdA);
		if (IDataUtil.isNotEmpty(groupInfo)) {
			// 查询客户经理信息
			String custMgrId = groupInfo.getString("CUST_MANAGER_ID");
			if (StringUtils.isNotEmpty(custMgrId)) {
				IData managerInfo = UCAInfoIntfViewUtil
						.qryCustManagerByCustManagerId(this, custMgrId);
				//this.setCustInfo(managerInfo);
				groupInfo.putAll(managerInfo);
			}

		}
		this.setInfo(groupInfo);
		this.setAjax(groupInfo);
	}
	
	 /**
		 * 查询集团客户信息
		 * 
		 * @param cycle
		 * @throws Exception
		 */
		public IData queryGroupByGroupIdA(String grouIdA) throws Exception {
			// 查询集团客户信息
			IData groupInfo = UCAInfoIntfViewUtil.qryGrpCustInfoByGrpId(this,
					grouIdA);
			if (IDataUtil.isNotEmpty(groupInfo)) {
				// 查询客户经理信息
				String custMgrId = groupInfo.getString("CUST_MANAGER_ID");
				if (StringUtils.isNotEmpty(custMgrId)) {
					IData managerInfo = UCAInfoIntfViewUtil
							.qryCustManagerByCustManagerId(this, custMgrId);
					//this.setCustInfo(managerInfo);
					groupInfo.putAll(managerInfo);
				}

			}
			return groupInfo;
		}
		
		 public IData getAttrContractId(IData submitData) throws Exception {
		        IData input = new DataMap();
		        IDataset contractData = submitData.getDataset("CONTRACT_DATA");
		        for (int i = 0; i < contractData.size(); i++) {
		            IData temp = new DataMap();
		            IData workformAttr = new DataMap();
		            temp = contractData.getData(i);
		            workformAttr = new DataMap();
		            String key = temp.getString("ATTR_CODE");
		            if (key.startsWith("C_")) {
		                workformAttr.put(key.substring(2), temp.getString("ATTR_VALUE"));
		                input.putAll(workformAttr);
		            }
		        }
		        IDataset attachs = submitData.getDataset("ATTACH_LIST");
		        for(int j = 0;j<attachs.size();j++){
		        	IData attach = attachs.getData(j);
		        	String attachType = attach.getString("ATTACH_TYPE");
		        	if("C".equals(attachType)){
		        		input.put("CONTRACT_FILE_ID", attach.getString("FILE_ID")+":"+attach.getString("FILE_NAME"));
		        	}
		        	
		        }
		        IDataset directlineDate = submitData.getDataset("DIRECTLINE_DATA");
		        IData line = new DataMap();
		        IDataset dataLine = new DatasetList();
		      /*  if (IDataUtil.isNotEmpty(directlineDate)) {
		            for (int i = 0; i < directlineDate.size(); i++) {
		                IData temp = new DataMap();
		                IData dAttr = new DataMap();
		                IDataset temp2 = directlineDate.getDataset(i);
		                for (int j = 0; j < temp2.size(); j++) {
		                    temp = temp2.getData(j);
		                    String key = temp.getString("ATTR_CODE");
		                    if (key.contains("NOTIN_")) {
		                        dAttr.put(StringUtils.substringAfter(key, "NOTIN_"), temp.getString("ATTR_VALUE"));
		                    }
		                }
		                dataLine.add(dAttr);
		            }
		        }*/
		        line.put("DIRECTLINE", dataLine);
		        input.putAll(line);
		     //   input.putAll(input);
		    //    input.put("CUST_ID", input.getString("C_CUST_ID"));
		     //   input.put("PRODUCT_START_DATE", input.getString("C_PRODUCT_START_DATE"));
		     //   input.put("PRODUCT_END_DATE", input.getString("C_PRODUCT_END_DATE"));
		        input.put("PRODUCT_ID", submitData.getData("OFFER_DATA").getString("OFFER_CODE_B"));
		      //  input.put("POATT_TYPE", input.getString("C_POATT_TYPE"));

		        IData resultInfo = CSViewCall.callone(this, "CM.ConstractGroupSVC.insertDirectlineContract", input);
		        String contractId = resultInfo.getString("CONTRACT_ID");
		        IData temp = new DataMap();
		        temp.put("ATTR_VALUE", contractId);
		        temp.put("ATTR_NAME", "合同编号");
		        temp.put("ATTR_CODE", "CONTRACT_ID");
		        contractData.add(temp);
		        submitData.put("CONTRACT_DATA", contractData);
		        return submitData;
		    }

}
