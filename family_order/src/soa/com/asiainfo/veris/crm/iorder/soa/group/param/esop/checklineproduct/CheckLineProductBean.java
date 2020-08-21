package com.asiainfo.veris.crm.iorder.soa.group.param.esop.checklineproduct;

import com.ailk.biz.service.BizRoute;
import com.ailk.bizservice.dao.Dao;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductElementInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductMebInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.plat.PlatInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.GrpInvoker;
import com.asiainfo.veris.crm.order.soa.group.esop.query.SubscribeViewInfoBean;
import com.asiainfo.veris.crm.order.soa.group.esp.DatalineEspUtil;

public class CheckLineProductBean extends CSBizBean {
	
	private static String dataLineSvc = "952";
	
     public static void checkLineProduct(IData data) throws Exception
     {
    	 String ibsysId = data.getString("IBSYSID");
    	 String userIdA = "";
    	 String sizeFlage =null;
    	 String groupId  =null;
         IDataset attrInfos = SubscribeViewInfoBean.qryEopServiceAttributes(data.getString("IBSYSID"));
         for(int k =0;k<attrInfos.size();k++)
         {
        	IData attrInfo = attrInfos.getData(k);
        	if("SIZE_FLAG".equals(attrInfo.getString("ATTR_CODE")))
        	{
        		sizeFlage =attrInfo.getString("ATTR_VALUE");
        	}
        	if("CUSTOMNO".equals(attrInfo.getString("ATTR_CODE")))
        	{
        		groupId = attrInfo.getString("ATTR_VALUE");
        	}
        	if("USER_ID_A".equals(attrInfo.getString("ATTR_CODE"))){
        		userIdA = attrInfo.getString("ATTR_VALUE");
        	}
         }
         
		 IData params = new DataMap();
         params.put("GROUP_ID", groupId);
         IData groupInfo = CSAppCall.callOne("CS.UcaInfoQrySVC.qryGrpInfoByGrpId", params);
         String custId =null;
         if(DataUtils.isNotEmpty(groupInfo)){
          custId = groupInfo.getString("CUST_ID");
         
         }
         //根据IbsysId查询产品编码
         IData eweparam = new DataMap();
         eweparam.put("BI_SN", ibsysId);
         IDataset eweInfos = Dao.qryByCodeParser("TF_B_EWE", "SEL_BY_BISN", eweparam, Route.getJourDb(BizRoute.getRouteId()));
         if(IDataUtil.isEmpty(eweInfos)){
        	 CSAppException.apperr(CrmCommException.CRM_COMM_103,"根据订单号查询TF_B_EWE表流程信息为空!");
         }
         String eweProductId  =  eweInfos.first().getString("BUSI_CODE");
         params.put("CUST_ID", custId);
         IDataset grpUserList = CSAppCall.call("CS.UserInfoQrySVC.getUserInfoByCstIdForGrp", params);
         boolean flag = false;
         if (DataUtils.isNotEmpty(grpUserList))
         {
        	 for(int i =0;i<grpUserList.size();i++)
        	 {
        		IData userData = grpUserList.getData(i);
        		String productId = userData.getString("PRODUCT_ID");
        		if(productId.equals(eweProductId))
        		{
        			flag= true;
        			break;
        		}
        	 }
         }
         if(!flag)
         {
        	 IData param = new DataMap();
     		param.put("EPARCHY_CODE", getVisit().getStaffEparchyCode());
     		param.put("GROUP_ID", groupId);
     		param.put("PRODUCT_ID", eweProductId);
     		param.put("USER_ID_A", userIdA);
     	    IData dataInfos = createUserDatalineOrder(param);

  		  // 生成主用户
  	     IDataset result = GrpInvoker.ivkProduct(dataInfos, BizCtrlType.CreateUser, "CreateUserClass"); 
         }
        
         //这段逻辑获取值就是错的，后面再改吧！
        /* if(sizeFlage.equals("true")){
          
       // 调用服务数据
          IData svcData = new DataMap();
          svcData.put("USER_ID", data.getString("USER_ID"));
          svcData.put("PRODUCT_ID", eweProductId);
          svcData.put(Route.USER_EPARCHY_CODE, getUserEparchyCode());
          svcData.put("REASON_CODE", "修改集团归属注销集团用户");
          svcData.put("REMARK", "修改集团归属");
          svcData.put("DESTROY_ATTR", "OWEFEE"); // 欠费注销
          svcData.put("AUDIT_STAFF_ID", getVisit().getStaffId() );
          // 调用服务
          CSAppCall.call("CS.DestroyGroupUserSvc.destroyGroupUser", svcData);
         }*/
           
     }

     public static IData createUserDatalineOrder(IData map) throws Exception
     {
     	String groupId = map.getString("GROUP_ID");
     	String productId = map.getString("PRODUCT_ID");
     	String userIdA = map.getString("USER_ID_A");
     	IData user = UcaInfoQry.qryGrpInfoByGrpId(groupId);
     	IData userA = UcaInfoQry.qryGrpInfoByUserId(userIdA);
     	String custId= user.getString("CUST_ID");
     	 //1\查询必选服务和优惠
         IData ElementsParam = new DataMap();
         ElementsParam.put("PRODUCT_ID",productId);
         ElementsParam.put("EFFECT_NOW", false);
         ElementsParam.put("GROUP_ID", groupId);
         ElementsParam.put("CUST_ID", user.getString("CUST_ID"));
         ElementsParam.put("USER_EPARCHY_CODE", user.getString("EPARCHY_CODE"));
         IDataset ElementsInfoList = DatalineEspUtil.getElementsInfo(ElementsParam);
        
         //查询原集团服务参数
         IData svcParam =  new DataMap();
         svcParam.put("USER_ID",userIdA);
         svcParam.put("SERVICE_ID",dataLineSvc);
         IDataset svcInfos = PlatInfoQry.getAutoPayContractInfo(svcParam);
         if(IDataUtil.isEmpty(svcInfos)){
        	 CSAppException.apperr(CrmCommException.CRM_COMM_103,"根据USER_ID"+userIdA+"查询服务参数为空！");
         }
         //7012服务参数设置
         if("7012".equals(productId)){
         	IDataset elements = ElementsInfoList.getData(0).getDataset("SELECTED_ELEMENTS");
         	for(int i =0;i<elements.size();i++){
         		IData element = elements.getData(i);
         		if(dataLineSvc.equals(element.getString("ELEMENT_ID"))){
         			IData attrParam = element.getDataset("ATTR_PARAM").getData(0);
         			attrParam.put("ATTR_VALUE", svcInfos.first().getString("ATTR_VALUE"));
         		}
         	}
         }
         
         //2、查询已有付费计划信息
         IDataset infosDataset = getPayPlanInfo(productId);
         
         //3、获取服务号码
         String serialNumber = createSerialNumber(groupId,productId,user.getString("EPARCHY_CODE"));
     	 
         //4、获取原有集团账户id
         IData acctInfos = UcaInfoQry.qryDefaultPayRelaByUserId(userIdA);
         if(IDataUtil.isEmpty(acctInfos)){
        	 CSAppException.apperr(CrmCommException.CRM_COMM_103,"根据USER_ID"+userIdA+"查询默认付费账户为空！");
         }
         String acctId =  acctInfos.getString("ACCT_ID");
         
         //5、获取原有集团产品属性
         IData  productParams = new DataMap();
         productParams.put("USER_ID", userIdA);
         productParams.put("PRODUCT_ID", productId);
         productParams.put("INST_TYPE", "P");
         IDataset productInfos = CSAppCall.call("CS.UserAttrInfoQrySVC.getUserProductAttrByUP", productParams);
         IData productInfo =  DatalineEspUtil.saveProductParamInfoFrontDataset(productInfos);
         //拼装入参信息
         IData inparam = new DataMap();
         
         IData resInfo = new DataMap();
         resInfo.put("DISABLED", "true");
         resInfo.put("MODIFY_TAG", "0");
         resInfo.put("RES_CODE", serialNumber);
         resInfo.put("RES_TYPE_CODE", "L");
         resInfo.put("CHECKED", "true");
         inparam.put("RES_INFO", new DatasetList(resInfo));
         
         inparam.put("X_TRADE_FEESUB", null);
         inparam.put("X_TRADE_PAYMONEY", null);
         inparam.put("CUST_ID", custId);
         inparam.put("ELEMENT_INFO",ElementsInfoList.getData(0).getDataset("SELECTED_ELEMENTS"));//已有必选信息
       //inparam.put("ACCT_ID", contractList.getData(0).getString("ACCT_ID",""));
         inparam.put("ACCT_ID", acctId);//接口中获取，map.getString("ACCT_ID");
         inparam.put("GRP_PACKAGE_INFO",dealGrpPakcageInfo(productId,user.getString("EPARCHY_CODE")));//产品参数信息
         
        
         inparam.put("EFFECT_NOW", false);
         inparam.put("PRODUCT_ID", productId);
         inparam.put("PLAN_INFO",infosDataset);//付费计划信息
         inparam.put("SERIAL_NUMBER", serialNumber);
       //  inparam.put("PRODUCT_PARAM_INFO", IDataUtil.idToIds(productParamInfo));
         
         IData contract = new DataMap();
         String contractId = userA.getString("CONTRACT_ID");//接口中获取，map.getString("CONTRACT_ID");
         contract.put("CONTRACT_ID", contractId);
         inparam.put("USER_INFO", contract);//合同信息
         inparam.put("AUDIT_STAFF_ID", "");//
         inparam.put("USER_EPARCHY_CODE", user.getString("EPARCHY_CODE"));
         inparam.put("REMARK", "");
         
         IDataset paramInfo = new DatasetList();
         IData paramp = new DataMap();
         paramp.put("PRODUCT_ID", productId);
         IDataset productParam = new DatasetList();
         IData param = new DataMap();
         //inputParams.getDataset("DLINE_DATA").getData(arg0)
         param.put("NOTIN_DETMANAGER_INFO", "");
         param.put("NOTIN_DETMANAGER_PHONE", "");
         param.put("NOTIN_DETADDRESS", "");
         param.put("NOTIN_PROJECT_NAME", "");
         param.putAll(productInfo);
         productParam = DatalineEspUtil.saveProductParamInfoFrontData(param);
         paramp.put("PRODUCT_PARAM", productParam);
         paramInfo.add(paramp);
         inparam.put("PRODUCT_PARAM_INFO",paramInfo);

         IDataset result = new DatasetList();
         inparam.put("PF_WAIT", "1");
     	
       
     	
     	return inparam;
     	
     }
     

 	/**
 	 * Create SerialNumber
 	 * 
 	 * @param pageData
 	 * @throws Exception
 	 */
 	public static String createSerialNumber(String groupId, String productId,
 			String grpUserEparchyCode) throws Exception {
 		if (StringUtils.isEmpty(productId)) {
 			return null;
 		}

 		// 避免服务号码的重�?add begin
 		IData param = new DataMap();
 		param.put("PRODUCT_ID", productId);
 		param.put(Route.USER_EPARCHY_CODE, grpUserEparchyCode);

 		IData grpSnData = new DataMap();
 		for (int i = 0; i < 10; i++) {
 			grpSnData = CSAppCall.call("CS.GrpGenSnSVC.genGrpSn", param)
 					.getData(0);

 			String serialNumber = grpSnData.getString("SERIAL_NUMBER", "");

 			if (StringUtils.isEmpty(serialNumber)) {
 				break;
 			}

 			IData params = new DataMap();
 			params.put("SERIAL_NUMBER", serialNumber);
 			IDataset userList = CSAppCall.call(
 					"CS.UcaInfoQrySVC.qryUserMainProdInfoBySnForGrp", params);
 			if (IDataUtil.isEmpty(userList)) {
 				break;
 			}
 		}
 		// 避免服务号码的重�?add end

 		String serialNumber = grpSnData.getString("SERIAL_NUMBER", "");

 		return serialNumber;
 	}

	  /**
    * 获取用户付费计划
    * @throws Exception 
    * 
    */
   private static IDataset getPayPlanInfo(String productId) throws Exception{
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
   	
   }
   
   /**
    * 
    * @param grpProductId
    * @param eparchyCode
    * @param grpUserId
    * @throws Exception
    */
    
    public static IDataset dealGrpPakcageInfo(String grpProductId, String eparchyCode) throws Exception
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
