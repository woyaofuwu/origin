package com.asiainfo.veris.crm.order.soa.group.esp;

import com.ailk.bizservice.base.CSAppCall;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductElementInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductMebInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderService;
import com.asiainfo.veris.crm.order.soa.group.esop.query.SubscribeViewInfoBean;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformAttrBean;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformSubscribeBean;

public class ChangeIdcEspSVC extends GroupOrderService {
	public IDataset changeUserIdcOrder(IData map) throws Exception
    {
    	String ibsysId = map.getString("IBSYSID");
    	IDataset esopData = WorkformSubscribeBean.qryWorkformSubscribeByIbsysid(ibsysId);
    	if(DataUtils.isEmpty(esopData))
        {
        	 CSAppException.apperr(CrmCommException.CRM_COMM_103,"根据订单号查询流程信息错误!");
        }
    	IData groupInfo = esopData.first();
    	String groupId = groupInfo.getString("GROUP_ID");
    	String productId = groupInfo.getString("BUSI_CODE");
    	IData user = UcaInfoQry.qryGrpInfoByGrpId(groupId);
    	String custId= user.getString("CUST_ID");
    	 //1、查询必选服务和优惠
        IData ElementsParam = new DataMap();
        ElementsParam.put("PRODUCT_ID",productId);
        ElementsParam.put("EFFECT_NOW", false);
        ElementsParam.put("GROUP_ID", groupId);
        ElementsParam.put("CUST_ID", user.getString("CUST_ID"));
        ElementsParam.put("USER_EPARCHY_CODE", user.getString("EPARCHY_CODE"));
        IDataset ElementsInfoList = DatalineEspUtil.getElementsInfo(ElementsParam);
        
        IDataset subScribes =  SubscribeViewInfoBean.qryWorkformNodeByIbsysid(ibsysId, "apply");//第一个节点名
    	if(DataUtils.isEmpty(subScribes))
        {
        	 CSAppException.apperr(CrmCommException.CRM_COMM_103,"根据订单号查询该节点无参数信息!");
        }
    	String subIbsyId  = subScribes.first().getString("SUB_IBSYSID");
//    	IDataset contractInfo = WorkformAttrBean.qryAttrBySubIbsysidAndAttrCode(subIbsyId, "IDC_ContractId");//获取合同号
//    	if(DataUtils.isEmpty(contractInfo))
//        {
//        	 CSAppException.apperr(CrmCommException.CRM_COMM_103,"根据订单号查询无合同信息!");
//        }
//    	String contractId = contractInfo.first().getString("ATTR_VALUE");

        
        //2、查询已有付费计划信息
//        IDataset infosDataset = getPayPlanInfo(productId);
        
        //3、获取服务号码
        
//        IDataset serialNumberInfo = WorkformAttrBean.qryAttrBySubIbsysidAndAttrCode(subIbsyId, "IDC_SERIAL_NUMBER");//获取合同号
//    	if(DataUtils.isEmpty(serialNumberInfo))
//        {
//        	 CSAppException.apperr(CrmCommException.CRM_COMM_103,"根据订单号查询无号码信息!");
//        }
//    	String serialNumber = serialNumberInfo.first().getString("ATTR_VALUE");
//        String serialNumber = null;
//        		createSerialNumber(groupId,productId,user.getString("EPARCHY_CODE"));
    	 
        //4、获取账户信息
//        IData acctInfo= DatalineEspUtil.getAcctInfo(ibsysId);
//        acctInfo = transformAcctInfo(acctInfo,user,contractId);
    	IDataset userIdInfo = WorkformAttrBean.qryAttrBySubIbsysidAndAttrCode(subIbsyId, "IDC_USER_ID");//获取合同号
    		if(DataUtils.isEmpty(userIdInfo))
    		{
    			CSAppException.apperr(CrmCommException.CRM_COMM_103,"根据订单号查询无user_id信息!");
    		}
		String userId = userIdInfo.first().getString("ATTR_VALUE");
        //拼装入参信息
        IData inparam = new DataMap();
        
        /*IData resInfo = new DataMap();
        resInfo.put("DISABLED", "true");
        resInfo.put("MODIFY_TAG", "0");
        resInfo.put("RES_CODE", serialNumber);
        resInfo.put("RES_TYPE_CODE", "L");
        resInfo.put("CHECKED", "true");
        inparam.put("RES_INFO", new DatasetList(resInfo));*/
        inparam.put("USER_ID", userId);
        inparam.put("X_TRADE_FEESUB", null);
        inparam.put("X_TRADE_PAYMONEY", null);
        inparam.put("CUST_ID", custId);
        /*if(StringUtils.isEmpty(acctInfo.getString("ACCT_ID",""))){
        	inparam.put("ACCT_IS_ADD", true);
        }
        if(StringUtils.isNotEmpty(acctInfo.getString("ACCT_ID","")))
        {
        	inparam.put("ACCT_ID", acctInfo.getString("ACCT_ID"));
        }
        inparam.put("ACCT_INFO", acctInfo);*/
        //inparam.put("ACCT_ID", contractList.getData(0).getString("ACCT_ID",""));
        //inparam.put("ACCT_ID", "1114090205653902");//接口中获取，map.getString("ACCT_ID");
        inparam.put("GRP_PACKAGE_INFO",dealGrpPakcageInfo(productId,user.getString("EPARCHY_CODE")));//产品参数信息
        
        /*IDataset eweInfo = EweNodeQry.qryEweByIbsysid(ibsysId,"0");
        if(DataUtils.isEmpty(eweInfo))
        {
        	 CSAppException.apperr(CrmCommException.CRM_COMM_103,"根据订单号查询无流程信息！");
        }
        map.put("BUSIFORM_ID", eweInfo.first().getString("BUSIFORM_ID"));
        map = DatalineEspUtil.getEosInfo(map, productId, user.getString("EPARCHY_CODE"));
       
        inparam.put("EOS", IDataUtil.idToIds(map));*/
        inparam.put("EFFECT_NOW", false);
        inparam.put("PRODUCT_ID", productId);
        inparam.put("BUSI_CTRL_TYPE", BizCtrlType.ChangeUserDis);
//        inparam.put("PLAN_INFO",infosDataset);//付费计划信息
//        inparam.put("SERIAL_NUMBER", serialNumber);
      //  inparam.put("PRODUCT_PARAM_INFO", IDataUtil.idToIds(productParamInfo));
        
        /*IData contract = new DataMap();
        //String contractId = "2018112000119345";//接口中获取，map.getString("CONTRACT_ID");
        contract.put("CONTRACT_ID", contractId);
        inparam.put("USER_INFO", contract);//合同信息
*/        inparam.put("AUDIT_STAFF_ID", "");//
        inparam.put("USER_EPARCHY_CODE", user.getString("EPARCHY_CODE"));
        inparam.put("REMARK", "");
        
        /*IDataset paramInfo = new DatasetList();
        IData paramp = new DataMap();
        paramp.put("PRODUCT_ID", productId);
        IDataset productParam = new DatasetList();
        IData param = new DataMap();
        //inputParams.getDataset("DLINE_DATA").getData(arg0)
        IData projectNames =  WorkformAttrBean.qryAttrByIbsysidRecordCode(ibsysId,"PROJECTNAME","0");
        if(IDataUtil.isEmpty(projectNames)){
        	 CSAppException.apperr(CrmCommException.CRM_COMM_103,"根据订单号查询无项目名称信息!");
        }
        String projectName = projectNames.getString("ATTR_VALUE");*/
        /*param.put("NOTIN_DETMANAGER_INFO", "");
        param.put("NOTIN_DETMANAGER_PHONE", "");
        param.put("NOTIN_DETADDRESS", "");
        param.put("NOTIN_PROJECT_NAME", projectName);*/
        IDataset paramInfo = new DatasetList();
        IData paramp = new DataMap();
        IDataset productParam = new DatasetList();
        IData paramData = new DataMap();
        paramData.put("IBSYSID", ibsysId);
        paramData.put("NODE_ID", "apply");
        
        
//        IData param = new DataMap();
//        param.put("PROJECT_NAME", "");
//        
//        
//        IDataset workformAttrList= WorkformAttrBean.getNewInfoByIbsysidAndNodeId(paramData);
//        for (int w=0,wsize=workformAttrList.size();w<wsize;w++){
//        	IData workformAttr= workformAttrList.getData(w);
//        	if(IDataUtil.isNotEmpty(workformAttr)&&workformAttr.getString("ATTR_CODE", "").indexOf("IDC_")>=0){
//                param.put(workformAttr.getString("ATTR_CODE"), workformAttr.getString("ATTR_VALUE"));
//        	}
//        }

        
       // param.putAll(DatalineEspUtil.getCommInfo(ibsysId));
//        productParam = DatalineEspUtil.saveProductParamInfoFrontData(param);
        /*paramp.put("PRODUCT_PARAM", productParam);
        paramInfo.add(paramp);
        inparam.put("PRODUCT_PARAM_INFO",paramInfo);*/
//        inparam.put("IDCPARAM", param);
        IDataset applyConfirmList=new DatasetList();
        IData paramCom = new DataMap();
        if("7041".equals(productId)){
        	paramData.put("NODE_ID", "apply");
            IDataset workformAttr7041List= WorkformAttrBean.getNewInfoByIbsysidAndNodeId(paramData);
            paramData.put("NODE_ID", "applyConfirm");
            IDataset workformAttr7041List1= WorkformAttrBean.getNewInfoByIbsysidAndNodeId(paramData);
            workformAttr7041List.addAll(workformAttr7041List1);
            paramData.put("NODE_ID", "applyConfirmWait");
            IDataset workformAttr7041List2= WorkformAttrBean.getNewInfoByIbsysidAndNodeId(paramData);
            workformAttr7041List.addAll(workformAttr7041List2);

            for (int w=0,wsize=workformAttr7041List.size();w<wsize;w++){
            	IData workformAttr7041= workformAttr7041List.getData(w);
            	if(IDataUtil.isNotEmpty(workformAttr7041)){
//                    param.put(workformAttr7041.getString("ATTR_CODE"), workformAttr7041.getString("ATTR_VALUE"));
                    String recordNum=workformAttr7041.getString("RECORD_NUM","");
                    if(recordNum.matches("^[0-9]*$")){
                    	int recordNumInt=Integer.valueOf(recordNum);
                    	if(recordNumInt==0){
                    		if("IDC_Price".equals(workformAttr7041.getString("ATTR_CODE"))){
                    			paramCom.put(IdcDiscntConst.discntElementId_price,workformAttr7041.getString("ATTR_VALUE"));
        					}else if("IDC_Discount".equals(workformAttr7041.getString("ATTR_CODE"))){
        						paramCom.put(IdcDiscntConst.discntElementId_discount,workformAttr7041.getString("ATTR_VALUE"));
        					}else{
        						paramCom.put(workformAttr7041.getString("ATTR_CODE"), workformAttr7041.getString("ATTR_VALUE"));
        					}
//                    		paramCom.put(workformAttr7041.getString("ATTR_CODE"), workformAttr7041.getString("ATTR_VALUE"));
                    	}else{
                    		int num =recordNumInt-1;
                    		if(applyConfirmList.size()<recordNumInt){
                    			for(int size=applyConfirmList.size();size<recordNumInt;size++){
                    				IData newData=new DataMap();
                    				newData.put("RECORD_NUM",size+1);
                    				applyConfirmList.add(newData);
                    			}
                    		}
                    		applyConfirmList.getData(num).put(workformAttr7041.getString("ATTR_CODE"), workformAttr7041.getString("ATTR_VALUE"));
                    	}
                    	
                    }
            	}
            }
            inparam.put("IDCPARAM", paramCom);
            inparam.put("IDCPARAMLIST", applyConfirmList);
        }
        
        IDataset result = new DatasetList();
        inparam.put("PF_WAIT", "0");
        
        map = DatalineEspUtil.getEosInfo(map, productId, user.getString("EPARCHY_CODE"));
        inparam.put("EOS", IDataUtil.idToIds(map));
      //必选信息填入 begin 
//        String price=paramCom.getString("7041011","0");
//        String discount=paramCom.getString("7041012","100");
        String price=paramCom.getString(IdcDiscntConst.discntElementId_price,"0");
        String discount=paramCom.getString(IdcDiscntConst.discntElementId_discount,"100");
        IDataset selectedElements=ElementsInfoList.getData(0).getDataset("SELECTED_ELEMENTS");
        System.out.println("ChangeIdcEspSVC-changeUserIdcOrder selectedElements:"+selectedElements);
        for (int s=0,ssize=selectedElements.size();s<ssize;s++){
        	IData selectedElement=selectedElements.getData(s);
//        	if(IdcDiscntConst.discntElementId.equals(selectedElement.getString("ELEMENT_ID", ""))){
        		selectedElement.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
        		if(selectedElement.get("ATTR_PARAM")!=null){
            		IDataset selectedElementsParams=selectedElement.getDataset("ATTR_PARAM",  new DatasetList("[]"));
                    for (int p=0,psize=selectedElementsParams.size();p<psize;p++){
                    	IData selectedElementsParam=selectedElementsParams.getData(p);
                    	if(IdcDiscntConst.discntElementId_price.equals(selectedElementsParam.getString("ATTR_CODE", ""))){//费用
                    		selectedElementsParam.put("ATTR_VALUE", price);
                    	}else if(IdcDiscntConst.discntElementId_discount.equals(selectedElementsParam.getString("ATTR_CODE", ""))){//折扣
                    		selectedElementsParam.put("ATTR_VALUE", discount);
                    	}
                    }
        		}
//        	}
        }
        inparam.put("ELEMENT_INFO",selectedElements);
        //必选信息填入 end 
        System.out.println("ChangeIdcEspSVC-changeUserIdcOrder inparam:"+inparam);

        result =CSAppCall.call("SS.ChangeGroupUserIdcSVC.crtOrder", inparam);
    	
    	return result;
    	
    }
	
	/**
     * 获取用户付费计划
     * @throws Exception 
     * 
     */
    private IDataset getPayPlanInfo(String productId) throws Exception{
    	// 查产品是否配置了付费参数
        IDataset payPans = new DatasetList();
        
        IData map = new DataMap();
        map.put("ID", productId);
        map.put("ID_TYPE", "P");
        map.put("ATTR_OBJ", "0");
        map.put("ATTR_CODE", "PAYMODECODE");
        IDataset dsPayConfigs = CSAppCall.call("CS.AttrBizInfoQrySVC.getBizAttr", map);
        IData dsPayConfig = new DataMap();
        if (IDataUtil.isNotEmpty(dsPayConfigs))
        {
        	dsPayConfig = dsPayConfigs.getData(0);
        }
        if (IDataUtil.isNotEmpty(dsPayConfig))
        {
            // 如果配置了付费参数，就按付费参数中的显示
            String[] configs = dsPayConfig.getString("ATTR_VALUE", "").split(",");
            for (int i = 0; i < configs.length; i++)
            {
                IData payPaln = new DataMap();
                payPaln.put("PLAN_TYPE", configs[i]);
                payPans.add(payPaln);
            }
        }
        // 集团付费计划中的付费账目
        String itemDesc = "";
        IData itemsParam = new DataMap();
        itemsParam.put("SUBSYS_CODE", "CGM");
        itemsParam.put("PARAM_ATTR", "1");
        itemsParam.put("PARAM_CODE", productId);
        IDataset payItems = CSAppCall.call("CS.CommparaInfoQrySVC.getPayItemsParam", itemsParam);//CommParaInfoIntfViewUtil.qryPayItemsParamByGrpProductId(bc, productId);
        if (IDataUtil.isNotEmpty(payItems))
        {
            // 付费账目描述
            for (int i = 0; i < payItems.size(); i++)
            {
                IData item = payItems.getData(i);

                itemDesc = itemDesc + item.getString("NOTE_ITEM", "") + "(" + item.getString("PARA_CODE1", "") + ")";
                if (i < payItems.size() - 1)
                    itemDesc += ",";
            }
        }

        if (IDataUtil.isEmpty(payPans))
        {
            // 如果没有取到配置的付费类型，则按默认为个人付费，如果配置了集团付费账目则将集团付费也作为默认付费账目
            IData payPaln = new DataMap();
            payPaln.put("MODIFY_TAG", "0");
            payPaln.put("PLAN_TYPE_CODE", "P");
            payPans.add(payPaln);
            // 如果配置了集团付费账目则将集团付费也作为默认付费账目
            if (StringUtils.isNotBlank(itemDesc))
            {
                IData payPalnG = new DataMap();
                payPaln.put("MODIFY_TAG", "0");
                payPalnG.put("PLAN_TYPE_CODE", "G");
                payPans.add(payPalnG);
            }
        }

       return new DatasetList(payPans);
    	 //查询已有付费计划信息
       // IData inparamPlan = new DataMap();
      //  inparamPlan.put("USER_ID", userId);
       // inparamPlan.put("USER_ID_A", "-1");
       // IDataset infosDataset = CSAppCall.call("CS.UserPayPlanInfoQrySVC.getPayPlanInfosByUserIdForGrp", inparamPlan);
      //  return infosDataset;
    }
	private IData transformAcctInfo(IData accountInfo,IData user,String contractId) throws Exception
    {
        IData acctInfo = new DataMap();
        if(DataUtils.isEmpty(accountInfo))
        {
            return acctInfo;
        }
        String acctId = accountInfo.getString("ACCT_ID");
        if(StringUtils.isNotBlank(acctId))
        {
            acctInfo.put("ACCT_ID", acctId);
        }
        else
        {
            acctInfo.putAll(accountInfo);
        }
        
        acctInfo.put("PAY_NAME", accountInfo.getString("ACCT_NAME","0"));
        acctInfo.put("PAY_MODE_CODE", accountInfo.getString("ACCT_TYPE","0"));
        
        //现金直接返回
        if(acctInfo.getString("PAY_MODE_CODE","0").equals("0")){
        	return acctInfo;
        }
        
        //托收
        if(acctInfo.getString("PAY_MODE_CODE","0").equals("1"))
        	acctInfo.put("PAYMENT_ID", "4");
        
        acctInfo.put("START_CYCLE_ID", SysDateMgr.getSysDate().replace("-", "").substring(0, 6));
        acctInfo.put("END_CYCLE_ID", SysDateMgr.getEndCycle205012());
        acctInfo.put("BANK_ACCT_NAME", acctInfo.getString("BANK_NAME"));
        acctInfo.put("CONSIGN_MODE", "1");
        acctInfo.put("ACT_TAG", "1");
        acctInfo.put("RSRV_STR1", "1");
        acctInfo.put("MODIFY_TAG", "0");
        acctInfo.put("EPARCHY_CODE", user.getString("EPARCHY_CODE"));
    	acctInfo.put("USER_EPARCHY_CODE",user.getString("EPARCHY_CODE"));
       
    	//合同号
    	acctInfo.put("CONTRACT_NO", contractId);
        	
        return acctInfo;
    }
	
	/**
	    * 
	    * @param grpProductId
	    * @param eparchyCode
	    * @param grpUserId
	    * @throws Exception
	    */
	    
	    public IDataset dealGrpPakcageInfo(String grpProductId, String eparchyCode) throws Exception
	    {
	        IData inparam = new DataMap();
	        inparam.put("PRODUCT_ID", grpProductId);
	        inparam.put("EPARCHY_CODE", eparchyCode);
	        //IDataset mebPkgList =  CSAppCall.call("CS.ProductPkgInfoQrySVC.getMebForcePackageByGrpProId", inparam);
	  

	        IDataset elementList = new DatasetList();
	        
	        String mebProductId = UProductMebInfoQry.getMemberMainProductByProductId(grpProductId);
	        if (StringUtils.isBlank(mebProductId))
	        {
	            return new DatasetList();
	        }
	        elementList = UProductElementInfoQry.queryForceElementsByProductId(mebProductId);
	        IDataset userGrpPackageList = new DatasetList();
	        for (int i = 0; i < elementList.size(); i++)
	        {
	            IData temp = elementList.getData(i);
	            IData userGrpPackage = new DataMap();
	            userGrpPackage.put("PRODUCT_ID", temp.getString("PRODUCT_ID"));
	            userGrpPackage.put("ELEMENT_ID", temp.getString("ELEMENT_ID"));
	            userGrpPackage.put("PACKAGE_ID", temp.getString("PACKAGE_ID"));
	            userGrpPackage.put("ELEMENT_FORCE_TAG", temp.getString("ELEMENT_FORCE_TAG"));
	            userGrpPackage.put("ELEMENT_NAME", temp.getString("ELEMENT_NAME"));
	            userGrpPackage.put("ELEMENT_TYPE_CODE", temp.getString("ELEMENT_TYPE_CODE"));
	            userGrpPackage.put("MODIFY_TAG", "0");
	            userGrpPackageList.add(userGrpPackage);
	        }
	        return userGrpPackageList;
	    }
}
