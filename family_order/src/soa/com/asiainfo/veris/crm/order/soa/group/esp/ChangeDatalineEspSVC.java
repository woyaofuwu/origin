package com.asiainfo.veris.crm.order.soa.group.esp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import org.apache.log4j.Logger;

import com.ailk.biz.service.BizRoute;
import com.ailk.biz.util.StaticUtil;
import com.ailk.bizservice.base.CSAppCall;
import com.ailk.bizservice.dao.Dao;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;

public class ChangeDatalineEspSVC  extends CSBizService{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static transient Logger logger = Logger.getLogger(ChangeDatalineEspSVC.class);
	
	public IDataset changeDatalineOrder(IData map) throws Exception
    {
		//入参
		String recordNum = map.getString("RECORD_NUM");
		String ibsysId = map.getString("IBSYSID");
		String nodeId = map.getString("NODE_ID");
		String cancelTag  = map.getString("CANCEL_TAG","");
		String changGrpTag  =map.getString("CHANGEGRP_TAG","");  
		String mebProductId = "";
		String mebUserId = "";
		IData user = new DataMap();
		//通过ibSysid查询集团userId
        IData groupInfo = new DataMap();
        IData groupparam = new DataMap();
        groupparam.put("IBSYSID", ibsysId);
        groupparam.put("RECORD_NUM", "0");
        IDataset groupInfos = Dao.qryByCodeParser("TF_B_EOP_PRODUCT", "SEL_BY_PK", groupparam, Route.getJourDb(BizRoute.getRouteId()));
        if(DataUtils.isEmpty(groupInfos))
        {
        	CSAppException.apperr(CrmCommException.CRM_COMM_103,"根据该订单号查询产品信息无数据！");
        }
        groupInfo = groupInfos.first();
		String userId = groupInfo.getString("USER_ID");
		String productId = groupInfo.getString("PRODUCT_ID");
		 groupparam.put("RECORD_NUM", recordNum);
		if(!"7010".equals(productId)&&!"true".equals(changGrpTag)){
			IDataset mebInfos = Dao.qryByCodeParser("TF_B_EOP_PRODUCT_SUB", "SEL_BY_PK", groupparam, Route.getJourDb(BizRoute.getRouteId()));
            if(DataUtils.isEmpty(mebInfos))
            {
            	 CSAppException.apperr(CrmCommException.CRM_COMM_103,"根据该订单号查询产品信息无数据！");
            }
            mebProductId = mebInfos.first().getString("PRODUCT_ID");
            mebUserId =  mebInfos.first().getString("USER_ID");
            if(StringUtils.isEmpty(mebUserId)){
            	CSAppException.apperr(CrmCommException.CRM_COMM_103,"根据该订单号查询成员信息无数据！请核对TF_B_EOP_PRODUCT_SUB表数据");
            }
            user = UcaInfoQry.qryGrpInfoByUserId(mebUserId);
		}else{
			user = UcaInfoQry.qryGrpInfoByUserId(userId);
		}
		// 1、从EOSP获取专线数据
        IDataset resultDataLineData = new DatasetList();
        IData resultDataset = new DataMap(); 
        IData inputParam = new DataMap();
        inputParam.put("NODE_ID", nodeId);
        inputParam.put("IBSYSID", ibsysId);
        inputParam.put("PRODUCT_ID", productId);
        inputParam.put("OPER_CODE", "14");
        inputParam.put("RECORD_NUM", recordNum);
        inputParam.put("CANCEL_TAG", cancelTag);
        resultDataLineData = getEsopInfos(inputParam); //获取专线信息
        resultDataset = mergeData(resultDataLineData); //转换参数
        
        //2、查询集团信息
        IData param = new DataMap();
        param.put("USER_ID", userId);
        // 查询集团信息
        UcaData grpUcaData = UcaDataFactory.getNormalUcaByUserIdForGrp(param);
        
        String contractId = ""; 
        IDataset contracts = getcontractsInfo(resultDataset,grpUcaData.getCustId(),productId);
        if(IDataUtil.isNotEmpty(contracts)){
        	contractId = contracts.getData(0).getString("CONTRACT_ID");
        }
      // contractId ="2018103100118435";//测试用后面删除viop专线2016021900077709 数据专线2018103100118435
        //3、查询集团资源信息 资源信息都不要了
       /* IDataset resList = initResList(userId,"-1",Route.CONN_CRM_CG);
        if (IDataUtil.isNotEmpty(resList))
        {
            for (int i = 0, ilen = resList.size(); i < ilen; i++)
            {
                IData item = resList.getData(i);
                String resTypeCode = item.getString("RES_TYPE_CODE");
                if (StringUtils.isNotEmpty(resTypeCode))
                {
                    item.put("RES_TYPE", StaticUtil.getStaticValueDataSource(getVisit(), Route.CONN_RES, "RES_TYPE", "RES_TYPE_ID", "RES_TYPE_NAME", resTypeCode));
                }
            }
        }
        String resinfos = (resList == null ? "[]" : resList.toString());*/
        
        /**4、集团产品参数信息**/
        IDataset productParamInfo = new DatasetList();
        IDataset productParam = new DatasetList();
        IData result = new DataMap();
        result.put("PRODUCT_ID", productId);
        IData info = new DataMap();
        if("7010".equals(productId)){
        	 //查询页面产品参数信息
            inputParam.put("OPER_CODE", "12");
            IDataset esopParam = getEsopInfos(inputParam);
           
            if (IDataUtil.isNotEmpty(esopParam))
            {
                info.putAll(esopParam.getData(0));
            }
            info.put("NOTIN_METHOD_NAME", "ChgUs");
           
            IData userInParam = new DataMap();
            userInParam.put("USER_ID", userId);
            userInParam.put("REMOVE_TAG", "0");
            IDataset userParamInfo = CSAppCall.call("CS.UserInfoQrySVC.getTradeUserInfoByUserIdAndTag", userInParam);
            if (null != userParamInfo && userParamInfo.size() > 0)
            {
                IData userData = (IData) userParamInfo.get(0);
                info.put("NOTIN_DETMANAGER_INFO", userData.get("RSRV_STR7"));
                info.put("NOTIN_DETMANAGER_PHONE", userData.get("RSRV_STR8"));
                info.put("NOTIN_DETADDRESS", userData.get("RSRV_STR9"));
                info.put("NOTIN_PROJECT_NAME", userData.get("RSRV_STR10"));

            }
            
            //查询基本信息
            info = getViopInfo(resultDataset,info,userId,nodeId,contractId,grpUcaData.getCustId());
           // productParam = saveProductParamInfoFrontData(info); //页面参数信息
        }else if("7011".equals(productId)||"70111".equals(productId)||"70112".equals(productId)){
        	 //查询页面产品参数信息
            inputParam.put("OPER_CODE", "12");
            IDataset esopParam = getEsopInfos(inputParam);
           // IData info = new DataMap();
            if (IDataUtil.isNotEmpty(esopParam))
            {
                info.putAll(esopParam.getData(0));
            }
            info.put("NOTIN_METHOD_NAME", "ChgUs");
            IData userInParam = new DataMap();
            userInParam.put("USER_ID", userId);
            userInParam.put("REMOVE_TAG", "0");
            IDataset userInfo = CSAppCall.call("CS.UserInfoQrySVC.getTradeUserInfoByUserIdAndTag", userInParam);
            if (null != userInfo && userInfo.size() > 0)
            {
                IData userData = (IData) userInfo.get(0);
                info.put("NOTIN_DETMANAGER_INFO", userData.get("RSRV_STR7"));
                info.put("NOTIN_DETMANAGER_PHONE", userData.get("RSRV_STR8"));
                info.put("NOTIN_DETADDRESS", userData.get("RSRV_STR9"));
                info.put("NOTIN_PROJECT_NAME", userData.get("RSRV_STR10"));
            }
            if("true".equals(changGrpTag)){
            	info.put("NOTIN_COMMON_DATA", resultDataset.getDataset("COMMON_DATA"));
            }else{
            	//查询基本信息
                info = getInterInfo(resultDataset,info,mebUserId,nodeId,contractId,grpUcaData.getCustId(),recordNum,null,productId);
            }
          //   productParam = saveProductParamInfoFrontData(info); //页面参数信息
            
        }else if("7012".equals(productId)||"70121".equals(productId)||"70122".equals(productId)){
        	//查询页面产品参数信息
            inputParam.put("OPER_CODE", "12");
            IDataset esopParam = getEsopInfos(inputParam);
          //  IData info = new DataMap();
            if (IDataUtil.isNotEmpty(esopParam))
            {
                info.putAll(esopParam.first());
            }
            info.put("NOTIN_METHOD_NAME", "ChgUs");
            // 调用后台服务，查专线名称
            IData dataline = new DataMap();
            dataline.put("ID", productId);
            dataline.put("ID_TYPE", "P");
            dataline.put("ATTR_CODE", "SP_LINE");
            dataline.put("EPARCHY_CODE", "ZZZZ");

            IDataset dataLineInfo = CSAppCall.call("CS.AttrItemInfoQrySVC.qryAttrItemBByIdAndIdtypeAttrCode", param);
            info.put("DATALINE_INFO", dataLineInfo);
            if("true".equals(changGrpTag)){
            	info.put("NOTIN_COMMON_DATA", resultDataset.getDataset("COMMON_DATA"));
            }else{
            	//查询基本信息
                info = getDatalineInfo(resultDataset,info,mebUserId,map.getString("NODE_ID"),contractId,grpUcaData.getCustId());
            }
              //  productParam = saveProductParamInfoFrontData(info); //页面参数信息
        }else if("7016".equals(productId)){
        	 //查询页面产品参数信息
            inputParam.put("OPER_CODE", "12");
            IDataset esopParam = getEsopInfos(inputParam);
           // IData info = new DataMap();
            if (IDataUtil.isNotEmpty(esopParam))
            {
                info.putAll(esopParam.getData(0));
            }
            info.put("NOTIN_METHOD_NAME", "ChgUs");
            IData userInParam = new DataMap();
            userInParam.put("USER_ID", userId);
            userInParam.put("REMOVE_TAG", "0");
            IDataset userInfo = CSAppCall.call("CS.UserInfoQrySVC.getTradeUserInfoByUserIdAndTag", userInParam);
            if (null != userInfo && userInfo.size() > 0)
            {
                IData userData = (IData) userInfo.get(0);
                info.put("NOTIN_DETMANAGER_INFO", userData.get("RSRV_STR7"));
                info.put("NOTIN_DETMANAGER_PHONE", userData.get("RSRV_STR8"));
                info.put("NOTIN_DETADDRESS", userData.get("RSRV_STR9"));
                info.put("NOTIN_PROJECT_NAME", userData.get("RSRV_STR10"));
            }
            if("true".equals(changGrpTag)){
            	info.put("NOTIN_COMMON_DATA", resultDataset.getDataset("COMMON_DATA"));
            }else{
            	//查询基本信息
                info = getInterInfo(resultDataset,info,mebUserId,nodeId,contractId,grpUcaData.getCustId(),recordNum,null,DataLineDiscntConst.imsElementId);
            }
          //   productParam = saveProductParamInfoFrontData(info); //页面参数信息
            
        }
       // info.putAll(DatalineEspUtil.getCommInfo(ibsysId));
        productParam = saveProductParamInfoFrontData(info); //页面参数信息
        result.put("PRODUCT_PARAM", productParam);
        productParamInfo.add(result);
       
        IData ElementsParam = new DataMap();
        if("7010".equals(productId)){
        	ElementsParam.put("PRODUCT_ID",productId);
        }else{
        	ElementsParam.put("PRODUCT_ID", mebProductId);
        }
       
        ElementsParam.put("USER_EPARCHY_CODE", user.getString("EPARCHY_CODE"));
        IDataset ElementsInfoList = DatalineEspUtil.getElementsInfo(ElementsParam);
        IDataset selectElements = ElementsInfoList.getData(0).getDataset("SELECTED_ELEMENTS");
        DataLineDiscntConst.changeElementIdToProductId(productId,mebUserId,selectElements);
        IDataset selectElement = new DatasetList();
        for(int i = 0;i<selectElements.size();i++){
        	IData element = selectElements.getData(i);
        	if(DataLineDiscntConst.getElementIdToProductId(productId,mebUserId).equals(element.getString("ELEMENT_ID"))
//        			||DataLineDiscntConst.datalineElementId.equals(element.getString("ELEMENT_ID"))
        			||DataLineDiscntConst.imsElementId.equals(element.getString("ELEMENT_ID"))){
        		element.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
        		selectElement.add(element);
        	}
        }
        
        /**拼数据**/
		IData svcData = new DataMap();
	    svcData.put("USER_ID", userId);
	    svcData.put("PRODUCT_ID", productId);
	    svcData.put("REMARK", "ESOP改造除去BOSS页面");//勇哥说暂时写死，这是页面的备注信息
	    
	   // svcData.put("RES_INFO", new DatasetList());//资源信息
	   // svcData.put("GRP_PACKAGE_INFO", new DatasetList()); //集团产品信息，目前置空
	    if("7010".equals(productId)){
	    	svcData.put("ELEMENT_INFO", new DatasetList());  //获取产品元素信息都是为空，所以我直接置空；
	    }else{
	    	svcData.put("ELEMENT_INFO_LIST", selectElement);
	    }
	    
	   
	    svcData.put(Route.USER_EPARCHY_CODE,  grpUcaData.getUserEparchyCode());
	  //  svcData.put("AUDIT_STAFF_ID","");//稽核信息，由于专线产品都不用稽核所以直接写死为空；
	    
	    //集团产品参数信息
	    if (IDataUtil.isNotEmpty(productParamInfo))
	      svcData.put("PRODUCT_PARAM_INFO", productParamInfo);
	    // ESOP参数
	   map = DatalineEspUtil.getEosInfo(map,productId,grpUcaData.getUserEparchyCode());
	    svcData.put("EOS", new DatasetList(map));
	    
	    IData userInfo = new DataMap();
	    userInfo.put("CONTRACT_ID",contractId);
	    svcData.put("USER_INFO", userInfo);
	    svcData.put("BUSI_CTRL_TYPE", BizCtrlType.ChangeUserDis); //业务类型为产品变更
	    svcData.put("CANCEL_TAG",cancelTag); //业务类型为产品变更
	  //  String changGrpTag  =map.getString("CHANGEGRP_TAG","");  
	    svcData.put("CHANGEGRP_TAG",changGrpTag); //业务类型为产品变更
	    // 费用信息
	  /*  svcData.put("X_TRADE_FEESUB", null);//专线变更没有台账费用直接写死
	    svcData.put("X_TRADE_PAYMONEY",null);//专线变更没有台账费用直接写死
	    svcData.put("CUST_ID", "1115080405235562");
	    svcData.put("SERIAL_NUMBER", groupInfo.getString("SERIAL_NUMBER"));
	    svcData.put("ACCT_ID", "1116012805843520");
	    svcData.put("EFFECT_NOW", false);
	    svcData.put("AUDIT_STAFF_ID", "");*/
	    IDataset results = new DatasetList();
	    if("7010".equals(productId)){
			 results = CSAppCall.call("SS.ChangeVoipUserElementSVC.crtOrder", svcData);
		}else if("7011".equals(productId)||"70111".equals(productId)||"70112".equals(productId)){
			results = CSAppCall.call("SS.ChangeNetinMemberElementSVC.crtOrder", svcData);
		}else if("7012".equals(productId)||"70121".equals(productId)||"70122".equals(productId)){
			results = CSAppCall.call("SS.ChangeDatalineMemberElementSVC.crtOrder", svcData);
		}else if("7016".equals(productId)){
			results = CSAppCall.call("SS.ChangeImsMemberElementSVC.crtOrder", svcData);
		}
		return results;
    }
	
	/**
     * 解析专线数据
     * 
     * @param dataset
     * @param httpResult
     * @return
     * @throws Exception
     */
    protected static IData mergeData(IDataset dataset, IDataset httpResult) throws Exception
    {
        IData resultData = new DataMap();
        IDataset comData = new DatasetList();
        IDataset dataLineAttr = new DatasetList();

        if (null != httpResult && httpResult.size() > 0)
        {
            IData dataLine = httpResult.getData(0);
            if (null != dataLine && dataLine.size() > 0)
            {
                IData totalData = dataLine.getData("DLINE_DATA");
                IData commonData = totalData.getData("COMM_DATA_MAP");
                IDataset lineDataList = totalData.getDataset("LINE_DATA_LIST");

                // 公共数据
                for (int i = 0; i < commonData.size(); i++)
                {
                    IData attrValue = new DataMap();
                    String attr[] = commonData.getNames();
                    attrValue.put("ATTR_CODE", attr[i]);
                    attrValue.put("ATTR_VALUE", commonData.getString(attr[i]));
                    comData.add(attrValue);
                }

                // 专线数据
                for (int j = 0; j < lineDataList.size(); j++)
                {
                    IData data = lineDataList.getData(j);
                    IData attrValue = new DataMap();
                    for (int k = 0; k < data.size(); k++)
                    {
                        String attr[] = data.getNames();
                        attrValue.put(attr[k], data.getString(attr[k]));
                    }
                    dataLineAttr.add(attrValue);
                }
            }
        }

        resultData.put("COMMON_DATA", comData);
        resultData.put("DLINE_DATA", dataLineAttr);

        return resultData;
    }
    
    /**
     * 根据custId,productId查询合同信息
     * @throws Exception 
     */
    private IDataset getcontractsInfo(IData resultDataset,String custId,String productId) throws Exception{
    	 //加载合同信息
        StringBuilder lines = new StringBuilder();
        if(IDataUtil.isNotEmpty(resultDataset)){
            IDataset datalineData = resultDataset.getDataset("DLINE_DATA",new DatasetList());
            if (null != datalineData && datalineData.size() > 0)
            {
                for (int i = 0; i < datalineData.size(); i++)
                {
                    String line = datalineData.getData(i).getString("PRODUCTNO");
                    lines.append("'").append(line).append("'");
                    if (i != datalineData.size()-1){
                        lines.append(",");
                    }
                }
            }
        }
        IData inparam = new DataMap();
        inparam.put("CUST_ID", custId);
        inparam.put("PRODUCT_ID", productId);
        inparam.put("LINE_NOS", lines.toString());
        IDataset contracts = CSAppCall.call("CM.ConstractGroupSVC.qryContractByCustIdProductId", inparam); //测试暂时写死
        return contracts;
    }
    
    /**
     * 初始用户的资源信息，组织res格式数据
     * 
     * @param bc
     * @param userId
     * @param userIdA
     * @param routeId
     * @throws Exception
     */
    public static IDataset initResList(String userId, String userIdA, String routeId) throws Exception
    {

        IData idata = new DataMap();
        idata.put("USER_ID", userId);
        idata.put("USER_ID_A", userIdA);
        idata.put(Route.ROUTE_EPARCHY_CODE, routeId);
        IDataset resInfo = CSAppCall.call("CS.UserResInfoQrySVC.getUserResByUserIdA", idata);

        IDataset datasetList = new DatasetList();
        if (IDataUtil.isNotEmpty(resInfo))
        {
            for (int i = 0; i < resInfo.size(); i++)
            {
                IData param = new DataMap();
                param.put("RES_TYPE_CODE", resInfo.get(i, "RES_TYPE_CODE"));
                param.put("RES_CODE", resInfo.get(i, "RES_CODE"));
                param.put("MODIFY_TAG", "EXIST");
                param.put("CHECKED", "true");
                param.put("DISABLED", "true");
                datasetList.add(param);
            }
        }

        return datasetList;
    }
    
    /**
     * 从esop获取数据
     */
    /**
     * 初始用户的资源信息，组织res格式数据
     * 
     * @param bc
     * @param userId
     * @param userIdA
     * @param routeId
     * @throws Exception
     */
    public static IDataset getEsopInfos(IData map) throws Exception
    {
    	// 1、从EOSP获取专线数据
    	IDataset httResultSetDataset  = new DatasetList();
    	String operCode = map.getString("OPER_CODE");
        IData inputParam = new DataMap();
        inputParam.put("NODE_ID", map.getString("NODE_ID", ""));
        inputParam.put("IBSYSID", map.getString("IBSYSID", ""));
        inputParam.put("SUB_IBSYSID", map.getString("SUB_IBSYSID", ""));
        inputParam.put("PRODUCT_ID", map.getString("PRODUCT_ID"));
        inputParam.put("RECORD_NUM", map.getString("RECORD_NUM",""));
        if("12".equals(operCode)){
        	httResultSetDataset = DatalineEspUtil.doEsopInfo(inputParam);
        }else{
        	httResultSetDataset = DatalineEspUtil.getDataLineInfo(inputParam);
        }
       /* //获取esop信息
        logger.error("调用ESOP接口：----------------" + inputParam.toString());
        IDataset httResultSetDataset  = CSAppCall.call("SS.ESOPQcsGrpBusiIntfSvc.getEosInfo", inputParam);
        logger.error("RESULT_FRO_ESOP:" + httResultSetDataset.toString());*/
        
       
        return httResultSetDataset;
    }
    
    /**
     * 转换产品参数信息
     */
    public static IData mergeData(IDataset httResultSetDataset) throws Exception
    {
        IData resultDataset = new DataMap(); 
        IDataset dataset = new DatasetList();
    	if (null != httResultSetDataset && httResultSetDataset.size() > 0)
        {
            IData dataLine = httResultSetDataset.getData(0);
            IData data = dataLine.getData("DLINE_DATA");
            if (null != data && data.size() > 0)
            {
                resultDataset = mergeData(dataset, httResultSetDataset);
            }
        }
        
		return resultDataset;
    	
    }
    
    
    /**
     * 转换产品参数信息
     */
    public static IDataset saveProductParamInfoFrontData(IData resultSetDataset) throws Exception
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
            productParamAttrset.add(productParamAttr);

        }
        
		return productParamAttrset;
    	
    }
    
    /*
    * 转换VIOP产品参数信息
    */
   public static IData getViopInfo(IData resultDataset,IData info,String userId,String bossModify,String contractId,String custId) throws Exception
   {
	   // 查询专线信息
       IData inparme = new DataMap();
       inparme.put("USER_ID", userId);
       inparme.put("RSRV_VALUE_CODE", "N001");
       IDataset userAttrInfo = CSAppCall.call("CS.TradeOtherInfoQrySVC.queryUserOtherInfoByUserId", inparme);

       // 中继信息查询
       IData inparmeZj = new DataMap();
       inparmeZj.put("USER_ID", userId);
       inparmeZj.put("RSRV_VALUE_CODE", "VOIP");
       IDataset userAttrInfoZj = CSAppCall.call("CS.TradeOtherInfoQrySVC.queryUserOtherInfoByUserId", inparmeZj);
       if (null != resultDataset && resultDataset.size() > 0)
       {
		   IDataset pageDataline = new DatasetList();
	       IDataset pageDataZJ = new DatasetList();
	
	       IDataset commonData = resultDataset.getDataset("COMMON_DATA");
	       IDataset datalineData = resultDataset.getDataset("DLINE_DATA");
	       boolean flag = true;
	       for (int i = 0; i < commonData.size(); i++)
	       {
	           IData sheetType = commonData.getData(i);
	           if("SHEETTYPE".equals(sheetType.getString("ATTR_CODE")) && "32".equals(sheetType.getString("ATTR_VALUE"))){
	               flag = false;
	           }
	       }
	    // 专线信息
	       if (null != datalineData && datalineData.size() > 0 && null != userAttrInfo && userAttrInfo.size() > 0 && flag)
	       {
	
	           for (int i = 0; i < datalineData.size(); i++)
	           {
	               IData esopdataline = datalineData.getData(i);
	               IData userData = new DataMap();
	               for (int j = 0; j < userAttrInfo.size(); j++)
	               {
	                   IData userAttrData = userAttrInfo.getData(j);
	
	                   if (esopdataline.getString("PRODUCTNO").equals(userAttrData.getString("RSRV_STR7")))
	                   {
	                       String numberCode = (String) userAttrData.get("RSRV_VALUE");
	                       userData.put("pam_NOTIN_LINE_NUMBER_CODE", Integer.valueOf(numberCode) - 1);
	                       userData.put("pam_NOTIN_LINE_NUMBER", userAttrData.get("RSRV_STR1"));
	                       userData.put("pam_NOTIN_LINE_BROADBAND", esopdataline.get("BANDWIDTH"));
	                       userData.put("pam_NOTIN_LINE_PRICE", userAttrData.get("RSRV_STR3"));
	
	                       // 安装调试费、一次性费用START_DATE不在当月显示为0
	                       SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	                       Date nowDate = new Date();
	                       String startDate4 = userAttrData.getString("START_DATE");
	                       int nowYear = nowDate.getYear();
	                       int nowMonth = nowDate.getMonth();
	                       int startYear = sdf.parse(startDate4).getYear();
	                       int startMonth = sdf.parse(startDate4).getMonth();
	
	                       if (nowMonth == startMonth && nowYear == startYear){
	                           userData.put("pam_NOTIN_INSTALLATION_COST", userAttrData.get("RSRV_STR4"));
	                           userData.put("pam_NOTIN_ONE_COST", userAttrData.get("RSRV_STR5"));
	
	                       }else{
	                           userData.put("pam_NOTIN_INSTALLATION_COST", "0");
	                           userData.put("pam_NOTIN_ONE_COST", "0");
	                       }
	
	                       String productNo2 = userAttrData.getString("RSRV_STR7","");
	                       IData paramIn = new DataMap();
	                       paramIn.put("USER_ID", userId);
	                       paramIn.put("PRODUCT_NO", productNo2);
	                       IDataset userDataline = CSAppCall.call("CS.TradeDataLineAttrInfoQrySVC.qryUserDatalineByProductNO", paramIn);
	                       if(IDataUtil.isNotEmpty(userDataline)){
	                           String tradeName = userDataline.getData(0).getString("RSRV_STR5","");
	                           userData.put("pam_LINE_TRADE_NAME", tradeName);
	                       } else {
	                           userData.put("pam_LINE_TRADE_NAME", "");
	                       }
	                       
	                       userData.put("pam_NOTIN_PRODUCT_NUMBER", userAttrData.get("RSRV_STR7"));
	                       userData.put("pam_NOTIN_LINE_INSTANCENUMBER", userAttrData.get("RSRV_STR9"));
	
	                      // if ("creatGrpOrder".equals(bossModify)){
	                           //通过合同编码，查询合同的专线信息。
	                           IData contractParam = new DataMap();
	                           //contractParam.put("CONTRACT_ID", contractId);
	                           contractParam.put("PRODUCT_ID", info.getString("PRODUCT_ID"));
	                          // contractParam.put("CUST_ID", custId);
	                           contractParam.put("LINE_NO", userAttrData.get("RSRV_STR7"));
	                           IDataset contratSet = CSAppCall.call("CM.ConstractGroupSVC.qryContractLineInfoByLineNo", contractParam);
	                           if (IDataUtil.isNotEmpty(contratSet)){
	                               IData tempLine = contratSet.getData(0);
	//                               userData.put("pam_LINE_TRADE_NAME", tempLine.getString("LINE_NAME",""));//专线名称
	                               userData.put("pam_NOTIN_LINE_BROADBAND",tempLine.getString("RSRV_STR1","0"));//专线宽带
	                               userData.put("pam_NOTIN_LINE_PRICE",tempLine.getString("RSRV_STR2","0"));//专线价格
	                               userData.put("pam_NOTIN_INSTALLATION_COST",tempLine.getString("RSRV_STR3","0"));//安装调试费
	                               userData.put("pam_NOTIN_ONE_COST",tempLine.getString("RSRV_STR4","0"));//一次性通信服务费
	                               userData.put("pam_NOTIN_VOICE",tempLine.getString("RSRV_STR15","0"));//语音通信费
	                           }
	                      // }
	                       
	                   }
	               }
	               pageDataline.add(userData); 
	           }
	       }else{
	           //如果SHEETTYPE=32新增专线
	           if(null != datalineData && datalineData.size() > 0){
	               IData lineNum = new DataMap();
	               int linenumber = 0;
	               lineNum.put("USER_ID", userId);
	               IDataset maxLineNumer = CSAppCall.call("SS.BookTradeSVC.getMaxLineNumberByUserId",lineNum);
	               if(null != maxLineNumer && maxLineNumer.size() >0 ){
	                   IData line = maxLineNumer.getData(0);
	                   linenumber = Integer.valueOf(line.getString("LINE_NUMBER_CODE"));
	               }
	
	               for (int i = 0; i < datalineData.size(); i++)
	               {
	                   IData dataline = datalineData.getData(i);
	                   IData user = new DataMap();
	                   linenumber = linenumber + 1;
	                   user.put("pam_NOTIN_LINE_NUMBER_CODE", linenumber-1);
	                   user.put("pam_NOTIN_LINE_NUMBER", "专线" + linenumber);
	                   user.put("pam_NOTIN_LINE_BROADBAND", dataline.getString("BANDWIDTH"));
	                   user.put("pam_NOTIN_LINE_PRICE", "0");
	                   user.put("pam_NOTIN_INSTALLATION_COST", "0");
	                   user.put("pam_NOTIN_ONE_COST", "0");
	                   user.put("pam_NOTIN_IP_PRICE", "0");
	                   user.put("pam_NOTIN_PRODUCT_NUMBER", dataline.getString("PRODUCTNO"));
	                   user.put("pam_NOTIN_LINE_INSTANCENUMBER", dataline.getString("PRODUCTNO"));
	
	                   user.put("pam_LINE_TRADE_NAME", dataline.getString("TRADENAME",""));
	                   
	                   if ("bossModify".equals(bossModify)){
	                       //通过合同编码，查询合同的专线信息。
	                       IData contractParam = new DataMap();
	                       contractParam.put("CONTRACT_ID", contractId);
	                       contractParam.put("PRODUCT_ID", info.getString("PRODUCT_ID"));
	                       contractParam.put("CUST_ID", custId);
	                       contractParam.put("LINE_NO", dataline.get("PRODUCTNO"));
	                       IDataset contratSet = null;//CSViewCall.call(bp, "CM.ConstractGroupSVC.qryContractLineInfoByLineNo", contractParam);
	                       if (IDataUtil.isNotEmpty(contratSet)){
	                           IData tempLine = contratSet.getData(0);
	//                           user.put("pam_LINE_TRADE_NAME", tempLine.getString("LINE_NAME",""));//专线名称
	                           user.put("pam_NOTIN_LINE_BROADBAND",tempLine.getString("RSRV_STR1","0"));//专线宽带
	                           user.put("pam_NOTIN_LINE_PRICE",tempLine.getString("RSRV_STR2","0"));//专线价格
	                           user.put("pam_NOTIN_INSTALLATION_COST",tempLine.getString("RSRV_STR3","0"));//安装调试费
	                           user.put("pam_NOTIN_ONE_COST",tempLine.getString("RSRV_STR4","0"));//一次性通信服务费
	                           user.put("pam_NOTIN_VOICE",tempLine.getString("RSRV_STR15","0"));//语音通信费
		                          
	                       }
	                   }
	
	                   pageDataline.add(user);
	               }
	           }
	       }
	
	    // 中继信息
	       if (null != userAttrInfoZj && userAttrInfoZj.size() > 0)
	       {
	
	           for (int i = 0; i < userAttrInfoZj.size(); i++)
	           {
	               IData dataInfo = userAttrInfoZj.getData(i);
	               dataInfo.put("pam_NOTIN_ZJ_NUMBER", dataInfo.getString("RSRV_VALUE"));
	               dataInfo.put("pam_NOTIN_ZJ_TYPE", dataInfo.getString("RSRV_STR1"));
	               dataInfo.put("pam_NOTIN_SUPER_NUMBER", dataInfo.getString("RSRV_STR2"));
	               dataInfo.put("pam_NOTIN_TYPE_NAME", dataInfo.getString("RSRV_STR3"));
	
	               pageDataZJ.add(dataInfo);
	           }
	       }
	
	       info.put("VISP_INFO", pageDataline);
	       info.put("VISP_INFO_ZJ", pageDataZJ);
	       info.put("NOTIN_AttrInternet", pageDataline);
	       info.put("NOTIN_DATALINE_DATA", datalineData);
	       info.put("NOTIN_COMMON_DATA", commonData);

       }else{
       // ESOP无数据从CRM取
       IDataset dataset = new DatasetList();
       IDataset datasetzj = new DatasetList();

       // 专线信息
       if (null != userAttrInfo && userAttrInfo.size() > 0)
       {
           for (int i = 0; i < userAttrInfo.size(); i++)
           {
               IData userAttrData = userAttrInfo.getData(i);
               IData userData = new DataMap();
               String numberCode = (String) userAttrData.get("RSRV_VALUE");
               userData.put("pam_NOTIN_LINE_NUMBER_CODE", Integer.valueOf(numberCode) - 1);
               userData.put("pam_NOTIN_LINE_NUMBER", userAttrData.get("RSRV_STR1"));
               userData.put("pam_NOTIN_LINE_BROADBAND", userAttrData.get("RSRV_STR2"));
               userData.put("pam_NOTIN_LINE_PRICE", userAttrData.get("RSRV_STR3"));

               // 安装调试费、一次性费用START_DATE不在当月显示为0
               SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
               Date nowDate = new Date();
               String startDate4 = userAttrData.getString("START_DATE");
               int nowYear = nowDate.getYear();
               int nowMonth = nowDate.getMonth();
               int startYear = sdf.parse(startDate4).getYear();
               int startMonth = sdf.parse(startDate4).getMonth();

               if (nowMonth == startMonth && nowYear == startYear){
                   userData.put("pam_NOTIN_INSTALLATION_COST", userAttrData.get("RSRV_STR4"));
                   userData.put("pam_NOTIN_ONE_COST", userAttrData.get("RSRV_STR5"));

               }else{
                   userData.put("pam_NOTIN_INSTALLATION_COST", "0");
                   userData.put("pam_NOTIN_ONE_COST", "0");
               }

               String productNo2 = userAttrData.getString("RSRV_STR7","");
               IData paramIn = new DataMap();
               paramIn.put("USER_ID", userId);
               paramIn.put("PRODUCT_NO", productNo2);
               IDataset userDataline = CSAppCall.call("CS.TradeDataLineAttrInfoQrySVC.qryUserDatalineByProductNO", paramIn);
               if(IDataUtil.isNotEmpty(userDataline)){
                   String tradeName = userDataline.getData(0).getString("RSRV_STR5","");
                   userData.put("pam_LINE_TRADE_NAME", tradeName);
               } else {
                   userData.put("pam_LINE_TRADE_NAME", "");
               }
               
               userData.put("pam_NOTIN_PRODUCT_NUMBER", userAttrData.get("RSRV_STR7"));
               userData.put("pam_NOTIN_LINE_INSTANCENUMBER", userAttrData.get("RSRV_STR9"));

               if ("bossModify".equals(bossModify)){
                   //通过合同编码，查询合同的专线信息。
                   IData contractParam = new DataMap();
                   contractParam.put("CONTRACT_ID", contractId);
                   contractParam.put("PRODUCT_ID", info.getString("PRODUCT_ID"));
                   contractParam.put("CUST_ID", custId);
                   contractParam.put("LINE_NO", userAttrData.get("RSRV_STR7"));
                   IDataset contratSet = CSAppCall.call("CM.ConstractGroupSVC.qryContractLineInfoByLineNo", contractParam);
                   if (IDataUtil.isNotEmpty(contratSet)){
                       IData tempLine = contratSet.getData(0);
//                       userData.put("pam_LINE_TRADE_NAME", tempLine.getString("LINE_NAME",""));//专线名称
                       userData.put("pam_NOTIN_LINE_BROADBAND",tempLine.getString("RSRV_STR1","0"));//专线宽带
                       userData.put("pam_NOTIN_LINE_PRICE",tempLine.getString("RSRV_STR2","0"));//专线价格
                       userData.put("pam_NOTIN_INSTALLATION_COST",tempLine.getString("RSRV_STR3","0"));//安装调试费
                       userData.put("pam_NOTIN_ONE_COST",tempLine.getString("RSRV_STR4","0"));//一次性通信服务费
                       userData.put("pam_NOTIN_VOICE",tempLine.getString("RSRV_STR15","0"));//语音通信费
                       
                   }
               }



               dataset.add(userData);
           }
       }

       // 中继信息
       if (null != userAttrInfoZj && userAttrInfoZj.size() > 0)
       {

           for (int i = 0; i < userAttrInfoZj.size(); i++)
           {
               IData dataInfo = userAttrInfoZj.getData(i);
               dataInfo.put("pam_NOTIN_ZJ_NUMBER", dataInfo.getString("RSRV_VALUE"));
               dataInfo.put("pam_NOTIN_ZJ_TYPE", dataInfo.getString("RSRV_STR1"));
               dataInfo.put("pam_NOTIN_SUPER_NUMBER", dataInfo.getString("RSRV_STR2"));
               dataInfo.put("pam_NOTIN_TYPE_NAME", dataInfo.getString("RSRV_STR3"));

               datasetzj.add(dataInfo);
           }
       }

       info.put("VISP_INFO", dataset);
       info.put("VISP_INFO_ZJ", datasetzj);
       info.put("NOTIN_AttrInternet", dataset);
   	}
       
       /**重新拼装**/
       IData infos = new DataMap();
       infos.put("NOTIN_DETMANAGER_PHONE", info.getString("NOTIN_DETMANAGER_PHONE","")); //管理员电话
       infos.put("NOTIN_DETMANAGER_INFO", info.getString("NOTIN_DETMANAGER_INFO","")); //管理员信息
       infos.put("NOTIN_DETADDRESS", info.getString("NOTIN_DETADDRESS","")); //联系地址
       infos.put("NOTIN_PROJECT_NAME", info.getString("NOTIN_PROJECT_NAME","")); //项目名称
       infos.put("NOTIN_OLD_ZJ_NUMBER", info.getDataset("VISP_INFO_ZJ").getData(0).getString("pam_NOTIN_OLD_ZJ_NUMBER")); //中继号 暂时处理一个
       infos.put("NOTIN_SUPER_NUMBER", info.getDataset("VISP_INFO_ZJ").getData(0).getString("pam_NOTIN_SUPER_NUMBER")); //总机号 暂时处理一个
       infos.put("NOTIN_TYPE_NAME", info.getDataset("VISP_INFO_ZJ").getData(0).getString("pam_NOTIN_TYPE_NAME")); //类型 暂时处理一个
       infos.put("NOTIN_ZJ_NUMBER", info.getDataset("VISP_INFO_ZJ").getData(0).getString("pam_NOTIN_ZJ_NUMBER")); //中继号 暂时处理一个
       infos.put("NOTIN_ZJ_TYPE", info.getDataset("VISP_INFO_ZJ").getData(0).getString("pam_NOTIN_ZJ_TYPE")); //类型 暂时处理一个
       
       infos.put("NOTIN_LINE_INSTANCENUMBER", info.getDataset("VISP_INFO").getData(0).getString("pam_NOTIN_LINE_INSTANCENUMBER")); //专线实例号
       infos.put("NOTIN_LINE_NUMBER_CODE", info.getDataset("VISP_INFO").getData(0).getString("NOTIN_LINE_NUMBER_CODE")); //专线CODE
       infos.put("NOTIN_OLD_ZJ_ATTR", info.getString("NOTIN_OLD_ZJ_ATTR","")); //老的中继信息
       infos.put("NOTIN_CHANGE_DISABLED", info.getString("NOTIN_CHANGE_DISABLED","")); //
       infos.put("NOTIN_LINE_PRICE", info.getDataset("VISP_INFO").getData(0).getString("pam_NOTIN_LINE_PRICE")); //专线价格
       infos.put("NOTIN_INSTALLATION_COST", info.getDataset("VISP_INFO").getData(0).getString("pam_NOTIN_INSTALLATION_COST")); //安装调试费
       infos.put("LINE_TRADE_NAME", info.getDataset("VISP_INFO").getData(0).getString("pam_LINE_TRADE_NAME")); //专线名称
       infos.put("NOTIN_LINE_NUMBER", info.getDataset("VISP_INFO").getData(0).getString("pam_NOTIN_LINE_NUMBER")); //专线
       infos.put("NOTIN_ONE_COST", info.getDataset("VISP_INFO").getData(0).getString("pam_NOTIN_ONE_COST")); //一次性服务费
       infos.put("NOTIN_LINE_BROADBAND", info.getDataset("VISP_INFO").getData(0).getString("pam_NOTIN_LINE_BROADBAND")); //专线宽带（兆）
       infos.put("NOTIN_PRODUCT_NUMBER", info.getDataset("VISP_INFO").getData(0).getString("pam_NOTIN_PRODUCT_NUMBER")); //业务标识
       
       infos.put("NOTIN_ZJ_ATTR", new DatasetList().toString()); //由于此接口只用于变更和注销，中继号信息不变故传空
       infos.put("NOTIN_DATALINE_DATA", info.getString("NOTIN_DATALINE_DATA","")); //
       infos.put("NOTIN_COMMON_DATA", info.getString("NOTIN_COMMON_DATA","")); //
       infos.put("NOTIN_METHOD_NAME", info.getString("NOTIN_METHOD_NAME","")); //
       infos.put("NOTIN_AttrInternet", info.getString("NOTIN_AttrInternet","")); //
          	
	   return infos;
   	
   	}
   /*
    * 转换互联网产品参数信息
    */
   public static IData getInterInfo(IData resultDataset,IData info,String userId,String bossModify,String contractId,String custId,String recordNum,String discntId,String productId) throws Exception
	{
	   // 根据userId查询专线资费信息
	   IData elementInfo = new DataMap();
	   if(discntId==null){
		   elementInfo=DatalineEspUtil.getElementInfo(userId,DataLineDiscntConst.getElementIdToProductId(productId,userId));
	   }else{
		   elementInfo=DatalineEspUtil.getElementInfo(userId,discntId);
	   }
       IData infos = new DataMap();
       if (null != resultDataset && resultDataset.size() > 0)
       {
           IDataset pageDataline = new DatasetList();

           IDataset commonData = resultDataset.getDataset("COMMON_DATA");
           IDataset datalineData = resultDataset.getDataset("DLINE_DATA");
           boolean flag = true;

           for (int i = 0; i < commonData.size(); i++)
           {
               IData sheetType = commonData.getData(i);
               if("SHEETTYPE".equals(sheetType.getString("ATTR_CODE")) && "32".equals(sheetType.getString("ATTR_VALUE"))){
                   flag = false;
               }
           }

           if (null != datalineData && datalineData.size() > 0 && null != elementInfo && elementInfo.size() > 0 && flag)
           {

               for (int i = 0; i < datalineData.size(); i++)
               {
                   IData esopdataline = datalineData.getData(i);

                  
                       if (esopdataline.getString("PRODUCTNO").equals(elementInfo.getString(DataLineDiscntConst.productNO)))
                       {
                           IData userData = new DataMap();

                           userData.put("pam_NOTIN_LINE_NUMBER_CODE", recordNum);
                           userData.put("pam_NOTIN_LINE_NUMBER", "专线"+recordNum);
                           userData.put("pam_NOTIN_LINE_BROADBAND",elementInfo.getString(DataLineDiscntConst.bandWidth));
                           userData.put("pam_NOTIN_LINE_PRICE",elementInfo.getString(DataLineDiscntConst.productPrice));

                           // 安装调试费、一次性费用START_DATE不在当月显示为0
                           SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                           Date nowDate = new Date();
                           String startDate4 = elementInfo.getString("START_DATE");
                           int nowYear = nowDate.getYear();
                           int nowMonth = nowDate.getMonth();
                           int startYear = sdf.parse(startDate4).getYear();
                           int startMonth = sdf.parse(startDate4).getMonth();

                           if (nowMonth == startMonth && nowYear == startYear){
                               userData.put("pam_NOTIN_INSTALLATION_COST", elementInfo.getString(DataLineDiscntConst.cost));
                               userData.put("pam_NOTIN_ONE_COST", elementInfo.getString(DataLineDiscntConst.oneCost));

                           }else{
                               userData.put("pam_NOTIN_INSTALLATION_COST", "0");
                               userData.put("pam_NOTIN_ONE_COST", "0");
                           }
                           
                           String productNo2 = elementInfo.getString(DataLineDiscntConst.productNO);
                           IData paramIn = new DataMap();
                           paramIn.put("USER_ID", userId);
                           paramIn.put("PRODUCT_NO", productNo2);
                           IDataset userDataline = CSAppCall.call("CS.TradeDataLineAttrInfoQrySVC.qryUserDatalineByProductNO", paramIn);
                           if(IDataUtil.isNotEmpty(userDataline)){
                               String tradeName = userDataline.getData(0).getString("RSRV_STR5","");
                               userData.put("pam_LINE_TRADE_NAME", tradeName);
                           } else {
                               userData.put("pam_LINE_TRADE_NAME", "");
                           }
                           
                           userData.put("pam_NOTIN_IP_PRICE", elementInfo.getString(DataLineDiscntConst.ipPrice));
                           userData.put("pam_NOTIN_PRODUCT_NUMBER", elementInfo.getString(DataLineDiscntConst.productNO));
                           userData.put("pam_NOTIN_LINE_INSTANCENUMBER", elementInfo.getString(DataLineDiscntConst.productNO));
                           //add by chenzg@20180620
                           userData.put("pam_NOTIN_SOFTWARE_PRICE", elementInfo.getString(DataLineDiscntConst.softwarePrice));
                           userData.put("pam_NOTIN_NET_PRICE", elementInfo.getString(DataLineDiscntConst.netPrice));


                         //  if("bossModify".equals(bossModify)){
                               //通过合同编码，查询合同的专线信息。
                               IData contractParam = new DataMap();
                               contractParam.put("CONTRACT_ID", contractId);
                               contractParam.put("PRODUCT_ID", info.getString("PRODUCT_ID"));
                               contractParam.put("CUST_ID", custId);
                               contractParam.put("LINE_NO", elementInfo.getString(DataLineDiscntConst.productNO));
                               IDataset contratSet = CSAppCall.call("CM.ConstractGroupSVC.qryContractLineInfoByLineNo", contractParam);
                               if (IDataUtil.isNotEmpty(contratSet)){
                                   IData tempLine = contratSet.getData(0);
//                                   userData.put("pam_LINE_TRADE_NAME", tempLine.getString("LINE_NAME",""));//专线名称
                                   userData.put("pam_NOTIN_LINE_BROADBAND",tempLine.getString("RSRV_STR1","0"));//专线宽带
                                   userData.put("pam_NOTIN_LINE_PRICE",tempLine.getString("RSRV_STR2","0"));//专线价格
                                   userData.put("pam_NOTIN_INSTALLATION_COST",tempLine.getString("RSRV_STR3","0"));//安装调试费
                                   userData.put("pam_NOTIN_ONE_COST",tempLine.getString("RSRV_STR4","0"));//一次性通信服务费
                                   userData.put("pam_NOTIN_IP_PRICE",tempLine.getString("RSRV_STR10","0"));//IP地址使用费
                                   
                                   //add by chenzg@20180620
                                   userData.put("pam_NOTIN_SOFTWARE_PRICE", tempLine.getString("RSRV_STR11","0"));	//软件应用服务费（元）
                                   userData.put("pam_NOTIN_NET_PRICE", tempLine.getString("RSRV_STR12","0"));	//网络技术支持服务费（元）
                               }
                         //  }

                           pageDataline.add(userData);

                       }
               } 
           }/*else{   互联网专线新增不走此接口
               //如果SHEETTYPE=32新增专线
               if(null != datalineData && datalineData.size() > 0){
                   IData lineNum = new DataMap();
                   int linenumber = 0;
                   lineNum.put("USER_ID", userId);
                   IDataset maxLineNumer = CSAppCall.call("SS.BookTradeSVC.getMaxLineNumberByUserId",lineNum);
                   if(null != maxLineNumer && maxLineNumer.size() >0 ){
                       IData line = maxLineNumer.getData(0);
                       linenumber = Integer.valueOf(line.getString("LINE_NUMBER_CODE"));
                   }

                   for (int i = 0; i < datalineData.size(); i++)
                   {
                       IData dataline = datalineData.getData(i);
                       IData user = new DataMap();
                       linenumber = linenumber + 1;
                       user.put("pam_NOTIN_LINE_NUMBER_CODE", linenumber-1);
                       user.put("pam_NOTIN_LINE_NUMBER", "专线" + linenumber);
                       user.put("pam_NOTIN_LINE_BROADBAND", dataline.getString("BANDWIDTH"));
                       user.put("pam_NOTIN_LINE_PRICE", "0");
                       user.put("pam_NOTIN_INSTALLATION_COST", "0");
                       user.put("pam_NOTIN_ONE_COST", "0");
                       user.put("pam_NOTIN_IP_PRICE", "0");
                       user.put("pam_NOTIN_PRODUCT_NUMBER", dataline.getString("PRODUCTNO"));
                       user.put("pam_NOTIN_LINE_INSTANCENUMBER", dataline.getString("PRODUCTNO"));

                       user.put("pam_LINE_TRADE_NAME", dataline.getString("TRADENAME",""));
                       
                       //add by chenzg@20180620
                       user.put("pam_NOTIN_SOFTWARE_PRICE", "0");
                       user.put("pam_NOTIN_NET_PRICE", "0");
                       
                       if("bossModify".equals(bossModify)){
                           //通过合同编码，查询合同的专线信息。
                           IData contractParam = new DataMap();
                           contractParam.put("CONTRACT_ID", contractId);
                           contractParam.put("PRODUCT_ID", info.getString("PRODUCT_ID"));
                           contractParam.put("CUST_ID", custId);
                           contractParam.put("LINE_NO", dataline.get("PRODUCTNO"));
                           IDataset contratSet = CSAppCall.call("CM.ConstractGroupSVC.qryContractLineInfoByLineNo", contractParam);
                           if (IDataUtil.isNotEmpty(contratSet)){
                               IData tempLine = contratSet.getData(0);
//                               user.put("pam_LINE_TRADE_NAME", tempLine.getString("LINE_NAME",""));//专线名称
                               user.put("pam_NOTIN_LINE_BROADBAND",tempLine.getString("RSRV_STR1","0"));//专线宽带
                               user.put("pam_NOTIN_LINE_PRICE",tempLine.getString("RSRV_STR2","0"));//专线价格
                               user.put("pam_NOTIN_INSTALLATION_COST",tempLine.getString("RSRV_STR3","0"));//安装调试费
                               user.put("pam_NOTIN_ONE_COST",tempLine.getString("RSRV_STR4","0"));//一次性通信服务费
                               user.put("pam_NOTIN_IP_PRICE",tempLine.getString("RSRV_STR10","0"));//IP地址使用费
                               //add by chenzg@20180620
                               user.put("pam_NOTIN_SOFTWARE_PRICE",tempLine.getString("RSRV_STR11","0"));//IP地址使用费
                               user.put("pam_NOTIN_NET_PRICE",tempLine.getString("RSRV_STR12","0"));//IP地址使用费
                           }
                       }

                       pageDataline.add(user);
                   }
               }
           }*/

           info.put("VISP_INFO", pageDataline);
           info.put("NOTIN_AttrInternet", pageDataline);
           info.put("NOTIN_OLD_AttrInternet", pageDataline);
           info.put("NOTIN_DATALINE_DATA", datalineData);
           info.put("NOTIN_COMMON_DATA", commonData);
           //infos.putAll(info);
       }
       else
       {
           // ESOP无数据从CRM取
           if (null != elementInfo && elementInfo.size() > 0)
           {
        	   	   IDataset dataset = new DatasetList();
                   IData userData = new DataMap();
                   String numberCode = recordNum;
                   userData.put("pam_NOTIN_LINE_NUMBER_CODE", numberCode);
                   userData.put("pam_NOTIN_LINE_NUMBER", "专线"+numberCode);
                   userData.put("pam_NOTIN_LINE_BROADBAND", elementInfo.getString(DataLineDiscntConst.bandWidth));
                   userData.put("pam_NOTIN_LINE_PRICE", elementInfo.getString(DataLineDiscntConst.productPrice));

                   // 安装调试费、一次性费用START_DATE不在当月显示为0
                   SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                   Date nowDate = new Date();
                   String startDate4 = elementInfo.getString("START_DATE");
                   int nowYear = nowDate.getYear();
                   int nowMonth = nowDate.getMonth();
                   int startYear = sdf.parse(startDate4).getYear();
                   int startMonth = sdf.parse(startDate4).getMonth();

                   if (nowMonth == startMonth && nowYear == startYear){
                       userData.put("pam_NOTIN_INSTALLATION_COST", elementInfo.getString(DataLineDiscntConst.cost));
                       userData.put("pam_NOTIN_ONE_COST", elementInfo.getString(DataLineDiscntConst.oneCost));

                   }else{
                       userData.put("pam_NOTIN_INSTALLATION_COST", "0");
                       userData.put("pam_NOTIN_ONE_COST", "0");
                   }
                   
                   String productNo2 = elementInfo.getString(DataLineDiscntConst.productNO);
                   IData paramIn = new DataMap();
                   paramIn.put("USER_ID", userId);
                   paramIn.put("PRODUCT_NO", productNo2);
                   IDataset userDataline = CSAppCall.call("CS.TradeDataLineAttrInfoQrySVC.qryUserDatalineByProductNO", paramIn);
                   if(IDataUtil.isNotEmpty(userDataline)){
                       String tradeName = userDataline.getData(0).getString("RSRV_STR5","");
                       userData.put("pam_LINE_TRADE_NAME", tradeName);
                   } else {
                       userData.put("pam_LINE_TRADE_NAME", "");
                   }
                   
                   userData.put("pam_NOTIN_IP_PRICE", elementInfo.getString(DataLineDiscntConst.ipPrice));
                   userData.put("pam_NOTIN_PRODUCT_NUMBER", elementInfo.getString(DataLineDiscntConst.productNO));
                   userData.put("pam_NOTIN_LINE_INSTANCENUMBER", elementInfo.getString(DataLineDiscntConst.productNO));
                   
                   //add by chenzg@20180620
                   userData.put("pam_NOTIN_SOFTWARE_PRICE", elementInfo.getString(DataLineDiscntConst.softwarePrice));
                   userData.put("pam_NOTIN_NET_PRICE",elementInfo.getString(DataLineDiscntConst.netPrice));

                   if("bossModify".equals(bossModify)){
                       //通过合同编码，查询合同的专线信息。
                       IData contractParam = new DataMap();
                       contractParam.put("CONTRACT_ID", contractId);
                       contractParam.put("PRODUCT_ID", info.getString("PRODUCT_ID"));
                       contractParam.put("CUST_ID", custId);
                       contractParam.put("LINE_NO", elementInfo.getString(DataLineDiscntConst.productNO));
                       IDataset contratSet = CSAppCall.call("CM.ConstractGroupSVC.qryContractLineInfoByLineNo", contractParam);
                       if (IDataUtil.isNotEmpty(contratSet)){
                           IData tempLine = contratSet.getData(0);
//                           userData.put("pam_LINE_TRADE_NAME", tempLine.getString("LINE_NAME",""));//专线名称
                           userData.put("pam_NOTIN_LINE_BROADBAND",tempLine.getString("RSRV_STR1","0"));//专线宽带
                           userData.put("pam_NOTIN_LINE_PRICE",tempLine.getString("RSRV_STR2","0"));//专线价格
                           userData.put("pam_NOTIN_INSTALLATION_COST",tempLine.getString("RSRV_STR3","0"));//安装调试费
                           userData.put("pam_NOTIN_ONE_COST",tempLine.getString("RSRV_STR4","0"));//一次性通信服务费
                           userData.put("pam_NOTIN_IP_PRICE",tempLine.getString("RSRV_STR10","0"));//IP地址使用费
                         //add by chenzg@20180620
                           userData.put("pam_NOTIN_SOFTWARE_PRICE",tempLine.getString("RSRV_STR11","0"));//软件应用服务费（元）
                           userData.put("pam_NOTIN_NET_PRICE",tempLine.getString("RSRV_STR12","0"));//网络技术支持服务费（元）
                       }
                   }


               dataset.add(userData);
               info.put("VISP_INFO", dataset);
               info.put("NOTIN_AttrInternet", dataset);
       }
       
   }
       infos.put("NOTIN_DETMANAGER_PHONE", info.getString("NOTIN_DETMANAGER_PHONE",""));//管理员电话
       infos.put("NOTIN_DETMANAGER_INFO", info.getString("NOTIN_DETMANAGER_INFO",""));//管理员信息
       infos.put("NOTIN_DETADDRESS", info.getString("NOTIN_DETADDRESS",""));//联系地址
       infos.put("NOTIN_PROJECT_NAME", info.getString("NOTIN_PROJECT_NAME",""));//项目名称
       if(DataUtils.isEmpty(info.getDataset("VISP_INFO"))){
    	   CSAppException.apperr(CrmCommException.CRM_COMM_103,"根据该订单号查询无专线数据资料。请核查专线资料是否一致！");
       }
       infos.put("NOTIN_LINE_NUMBER_CODE", info.getDataset("VISP_INFO").getData(0).getString("pam_NOTIN_LINE_NUMBER_CODE", ""));//项目名称
       infos.put("NOTIN_LINE_BROADBAND", info.getDataset("VISP_INFO").getData(0).getString("pam_NOTIN_LINE_BROADBAND", ""));//专线宽带（兆）
       infos.put("NOTIN_LINE_PRICE", info.getDataset("VISP_INFO").getData(0).getString("pam_NOTIN_LINE_PRICE", ""));//专线价格
       infos.put("LINE_TRADE_NAME", info.getDataset("VISP_INFO").getData(0).getString("pam_LINE_TRADE_NAME", ""));//专线名称
       infos.put("NOTIN_INSTALLATION_COST", info.getDataset("VISP_INFO").getData(0).getString("pam_NOTIN_INSTALLATION_COST", ""));//安装调试费（元）
       infos.put("NOTIN_ONE_COST", info.getDataset("VISP_INFO").getData(0).getString("pam_NOTIN_ONE_COST", ""));//一次性通信费
       infos.put("NOTIN_IP_PRICE", info.getDataset("VISP_INFO").getData(0).getString("pam_NOTIN_IP_PRICE", ""));//IP地址使用费
       infos.put("NOTIN_SOFTWARE_PRICE", info.getDataset("VISP_INFO").getData(0).getString("pam_NOTIN_SOFTWARE_PRICE", ""));//软件应用服务费
       infos.put("NOTIN_NET_PRICE", info.getDataset("VISP_INFO").getData(0).getString("pam_NOTIN_NET_PRICE", ""));//网络技术支持服务费（元）
       infos.put("NOTIN_LINE_INSTANCENUMBER", info.getDataset("VISP_INFO").getData(0).getString("pam_NOTIN_LINE_INSTANCENUMBER", ""));//专线实例号
       infos.put("NOTIN_PRODUCT_NUMBER", info.getDataset("VISP_INFO").getData(0).getString("pam_NOTIN_PRODUCT_NUMBER", ""));//业务标识
       //esop数据
       infos.put("TRADEID", info.getDataset("VISP_INFO").getData(0).getString("pam_TRADEID", ""));//业务查勘序号
       infos.put("PRODUCTNO", info.getDataset("VISP_INFO").getData(0).getString("pam_PRODUCTNO", ""));//业务标识
       infos.put("BIZSECURITYLV", info.getDataset("VISP_INFO").getData(0).getString("pam_BIZSECURITYLV", ""));//业务保障等级
       infos.put("BANDWIDTH", info.getDataset("VISP_INFO").getData(0).getString("pam_BANDWIDTH", ""));//业务宽带
       infos.put("PORTARATE", info.getDataset("VISP_INFO").getData(0).getString("pam_PORTARATE", ""));//客户端接口类型
       infos.put("PORTAINTERFACETYPE", info.getDataset("VISP_INFO").getData(0).getString("pam_PORTAINTERFACETYPE", ""));//业务端口支持最大速率
       infos.put("CUSAPPSERVIPADDNUM", info.getDataset("VISP_INFO").getData(0).getString("pam_CUSAPPSERVIPADDNUM", ""));//客户申请公网IP地址数
       infos.put("DOMAINNAME", info.getDataset("VISP_INFO").getData(0).getString("pam_DOMAINNAME", ""));//域名
       infos.put("MAINDOMAINADD", info.getDataset("VISP_INFO").getData(0).getString("pam_MAINDOMAINADD", ""));//主域名服务器地址
       infos.put("PROVINCEA", info.getDataset("VISP_INFO").getData(0).getString("pam_PROVINCEA", ""));//安装地址所属省份
       infos.put("CITYA", info.getDataset("VISP_INFO").getData(0).getString("pam_CITYA", ""));//安装地址所属地市
       infos.put("AREAA", info.getDataset("VISP_INFO").getData(0).getString("pam_AREAA", ""));//安装地址所属区县
       infos.put("COUNTYA", info.getDataset("VISP_INFO").getData(0).getString("pam_COUNTYA", ""));//街道/乡镇/路/巷/弄/行政村
       infos.put("VILLAGEA", info.getDataset("VISP_INFO").getData(0).getString("pam_VILLAGEA", ""));//门牌/小区/单位/自然村/组
       infos.put("PORTACUSTOM", info.getDataset("VISP_INFO").getData(0).getString("pam_PORTACUSTOM", ""));//用户名称
       infos.put("PORTACONTACT", info.getDataset("VISP_INFO").getData(0).getString("pam_PORTACONTACT", ""));//用户技术联系人
       infos.put("PORTACONTACTPHONE", info.getDataset("VISP_INFO").getData(0).getString("pam_PORTACONTACTPHONE", ""));//用户技术联系电话

       infos.put("NOTIN_COMMON_DATA", info.getString("NOTIN_COMMON_DATA",""));//
       infos.put("NOTIN_AttrInternet", info.getString("NOTIN_AttrInternet",""));//
       infos.put("NOTIN_DATALINE_DATA", info.getString("NOTIN_DATALINE_DATA",""));//
       infos.put("NOTIN_CHANGE_DISABLED", info.getString("NOTIN_CHANGE_DISABLED",""));//
       return infos;
   }
   /*
    * 转换数据专线产品参数信息
    */
   public static IData getDatalineInfo(IData resultDataset,IData info,String userId,String bossModify,String contractId,String custId) throws Exception
   {
	   if (null != resultDataset && resultDataset.size() > 0)
       {
           IDataset commonData = resultDataset.getDataset("COMMON_DATA");
           IDataset datalineData = resultDataset.getDataset("DLINE_DATA");
           IDataset AttrInternets = new DatasetList();
           if (null != datalineData && datalineData.size() > 0)
           {
               for (int i = 0; i < datalineData.size(); i++)
               {
                   IData dataline = datalineData.getData(i);
                   int number = i + 1;
                   dataline.put("pam_NOTIN_LINE_NUMBER_CODE", String.valueOf(i));
                   dataline.put("pam_NOTIN_LINE_NUMBER", "专线" + String.valueOf(number));
                   dataline.put("pam_NOTIN_LINE_BROADBAND", dataline.get("BANDWIDTH"));
                   dataline.put("pam_NOTIN_PRODUCT_NUMBER", dataline.get("PRODUCTNO"));
                   dataline.put("pam_NOTIN_LINE_INSTANCENUMBER", dataline.get("PRODUCTNO"));

                   String cityA = StaticUtil.getStaticValue(null, "TD_M_AREA", "AREA_NAME", "AREA_CODE", dataline.getString("CITYA"));
                   String cityZ = StaticUtil.getStaticValue(null, "TD_M_AREA", "AREA_NAME", "AREA_CODE", dataline.getString("CITYZ"));

                   dataline.put("pam_NOTIN_A_CITY", cityA);
                   dataline.put("pam_NOTIN_Z_CITY", cityZ);

                   //专线价格
                   dataline.put("pam_NOTIN_LINE_PRICE", "0");
                   //安装调试费
                   dataline.put("pam_NOTIN_INSTALLATION_COST", "0");
                   //一次性通信服务费
                   dataline.put("pam_NOTIN_ONE_COST", "0");
                   //add by chenzg@20180620
                   dataline.put("pam_NOTIN_SOFTWARE_PRICE", "0");
                   dataline.put("pam_NOTIN_NET_PRICE", "0");
                   dataline.put("pam_NOTIN_A_PERCENT","40%");//A端所在市县分成比例
                   dataline.put("pam_NOTIN_Z_PERCENT","40%");//Z端所在市县分成比例
                   dataline.put("pam_NOTIN_GROUP_PERCENT","20");
                   dataline.put("pam_NOTIN_SLA","0");
                   dataline.put("pam_LINE_TRADE_NAME", dataline.getString("TRADENAME",""));//专线名称
                   
                   //集团所在市县分成比例 pam_NOTIN_GROUP_PERCENT
                   //A端所在市县分成比例 pam_NOTIN_A_PERCENT
                   //Z端所在市县分成比例 pam_NOTIN_Z_PERCENT
                   //通过合同编码，查询合同的专线信息。
                   IData contractParam = new DataMap();
                   contractParam.put("CONTRACT_ID", contractId);
                   contractParam.put("PRODUCT_ID", info.getString("PRODUCT_ID"));
                   contractParam.put("CUST_ID", custId);
                   contractParam.put("LINE_NO", dataline.get("PRODUCTNO"));
                   IDataset contratSet = CSAppCall.call("CM.ConstractGroupSVC.qryContractLineInfoByLineNo", contractParam);//测试暂时注释掉

                   if (IDataUtil.isNotEmpty(contratSet)){
                       IData tempLine = contratSet.getData(0);
//                       dataline.put("pam_LINE_TRADE_NAME", tempLine.getString("LINE_NAME",""));//专线名称
                       dataline.put("pam_NOTIN_LINE_BROADBAND",tempLine.getString("RSRV_STR1","0"));//专线宽带
                       dataline.put("pam_NOTIN_LINE_PRICE",tempLine.getString("RSRV_STR2","0"));//专线价格
                       dataline.put("pam_NOTIN_INSTALLATION_COST",tempLine.getString("RSRV_STR3","0"));//安装调试费
                       dataline.put("pam_NOTIN_ONE_COST",tempLine.getString("RSRV_STR4","0"));//一次性通信服务费
                       dataline.put("pam_NOTIN_GROUP_PERCENT",tempLine.getString("RSRV_STR6","20%"));//集团所在市县分成比例
                       dataline.put("pam_NOTIN_A_PERCENT",tempLine.getString("RSRV_STR7","40%"));//A端所在市县分成比例
                       dataline.put("pam_NOTIN_Z_PERCENT",tempLine.getString("RSRV_STR8","40%"));//Z端所在市县分成比例
                       //add by chenzg@20180620
                       dataline.put("pam_NOTIN_SOFTWARE_PRICE",tempLine.getString("RSRV_STR11","0"));//软件应用服务费
                       dataline.put("pam_NOTIN_NET_PRICE",tempLine.getString("RSRV_STR12","0"));//网络技术支持服务费
                       dataline.put("pam_NOTIN_SLA", tempLine.getString("RSRV_STR16","0"));//SLA服务费（元/月）
                   }
                   IData AttrInternet = new DataMap();
                   AttrInternet.put("pam_NOTIN_TAG", String.valueOf(i));//TAG
	               	AttrInternet.put("pam_NOTIN_LINE_NUMBER", "专线" + String.valueOf(number));//专线
	               	AttrInternet.put("pam_NOTIN_LINE_NUMBER_CODE", String.valueOf(i));//专线CODE
	               	AttrInternet.put("pam_NOTIN_LINE_BROADBAND",dataline.getString("pam_NOTIN_LINE_BROADBAND"));//专线带宽（兆）
	               	AttrInternet.put("pam_NOTIN_LINE_PRICE", dataline.getString("pam_NOTIN_LINE_PRICE"));//专线价格（元）
	               	AttrInternet.put("pam_LINE_TRADE_NAME", dataline.getString("pam_LINE_TRADE_NAME"));//专线名称
	               	AttrInternet.put("pam_NOTIN_LINE_INSTANCENUMBER",dataline.getString("pam_NOTIN_LINE_INSTANCENUMBER"));//专线实例号
	               	AttrInternet.put("pam_NOTIN_PRODUCT_NUMBER", dataline.getString("pam_NOTIN_PRODUCT_NUMBER"));//业务标识
	               	AttrInternet.put("pam_NOTIN_INSTALLATION_COST", dataline.getString("pam_NOTIN_INSTALLATION_COST"));//安装调试费
	               	AttrInternet.put("pam_NOTIN_ONE_COST", dataline.getString("pam_NOTIN_ONE_COST"));//一次性通信服务费
	               	AttrInternet.put("pam_NOTIN_SOFTWARE_PRICE", dataline.getString("pam_NOTIN_SOFTWARE_PRICE"));//
	               	AttrInternet.put("pam_NOTIN_NET_PRICE", dataline.getString("pam_NOTIN_NET_PRICE"));//
	               	AttrInternet.put("pam_NOTIN_GROUP_PERCENT", dataline.getString("pam_NOTIN_GROUP_PERCENT"));///集团所在市县分成比例
	               	AttrInternet.put("pam_NOTIN_A_PERCENT", dataline.getString("pam_NOTIN_A_PERCENT"));//A端所在市县分成比例
	               	AttrInternet.put("pam_NOTIN_Z_PERCENT", dataline.getString("pam_NOTIN_Z_PERCENT"));//Z端所在市县分成比例
	               	AttrInternet.put("pam_NOTIN_SLA", dataline.getString("pam_NOTIN_SLA"));//SLA服务费（元/月）
	
	               	AttrInternets.add(AttrInternet);
               }
           }
           info.put("VISP_INFO", datalineData);
           info.put("NOTIN_DATALINE_DATA", datalineData);
           info.put("NOTIN_COMMON_DATA", commonData);
           info.put("NOTIN_AttrInternet", AttrInternets);
       }
	   IData infos = new DataMap();
	   infos.put("NOTIN_DETMANAGER_PHONE", info.getString("NOTIN_DETMANAGER_PHONE",""));//管理员电话
       infos.put("NOTIN_DETMANAGER_INFO", info.getString("NOTIN_DETMANAGER_INFO",""));//管理员信息
       infos.put("NOTIN_DETADDRESS", info.getString("NOTIN_DETADDRESS",""));//联系地址
       infos.put("NOTIN_PROJECT_NAME", info.getString("NOTIN_PROJECT_NAME",""));//项目名称
       
       infos.put("NOTIN_LINE_NUMBER", info.getDataset("VISP_INFO").getData(0).getString("pam_NOTIN_LINE_NUMBER", ""));//专线
       infos.put("NOTIN_LINE_NUMBER_CODE", info.getDataset("VISP_INFO").getData(0).getString("pam_NOTIN_LINE_NUMBER_CODE", ""));//专线（code）
       infos.put("NOTIN_LINE_BROADBAND", info.getDataset("VISP_INFO").getData(0).getString("pam_NOTIN_LINE_BROADBAND", ""));//专线宽带（兆）
       infos.put("NOTIN_LINE_PRICE", info.getDataset("VISP_INFO").getData(0).getString("pam_NOTIN_LINE_PRICE", ""));//专线价格
       infos.put("LINE_TRADE_NAME", info.getDataset("VISP_INFO").getData(0).getString("pam_LINE_TRADE_NAME", ""));//专线名称
       infos.put("NOTIN_LINE_INSTANCENUMBER", info.getDataset("VISP_INFO").getData(0).getString("pam_NOTIN_LINE_INSTANCENUMBER", ""));//专线实例号
       infos.put("NOTIN_PRODUCT_NUMBER", info.getDataset("VISP_INFO").getData(0).getString("pam_NOTIN_PRODUCT_NUMBER", ""));//业务标识
       infos.put("NOTIN_GROUP_CITY", info.getDataset("VISP_INFO").getData(0).getString("pam_NOTIN_GROUP_CITY", ""));//集团客户所在市县
       infos.put("NOTIN_A_CITY", info.getDataset("VISP_INFO").getData(0).getString("pam_NOTIN_A_CITY", ""));//A端所在市县
       infos.put("NOTIN_Z_CITY", info.getDataset("VISP_INFO").getData(0).getString("pam_NOTIN_Z_CITY", ""));//Z端所在市县
       infos.put("NOTIN_GROUP_PERCENT", info.getDataset("VISP_INFO").getData(0).getString("pam_NOTIN_GROUP_PERCENT", ""));//集团所在市县分成比例
       infos.put("NOTIN_A_PERCENT", info.getDataset("VISP_INFO").getData(0).getString("pam_NOTIN_A_PERCENT", ""));//A端所在市县分成比例
       infos.put("NOTIN_Z_PERCENT", info.getDataset("VISP_INFO").getData(0).getString("pam_NOTIN_Z_PERCENT", ""));//Z端所在市县分成比例
       infos.put("NOTIN_INSTALLATION_COST", info.getDataset("VISP_INFO").getData(0).getString("pam_NOTIN_INSTALLATION_COST", ""));//安装调试费
       infos.put("NOTIN_ONE_COST", info.getDataset("VISP_INFO").getData(0).getString("pam_NOTIN_ONE_COST", ""));//一次性通信服务费
       infos.put("NOTIN_SOFTWARE_PRICE", info.getDataset("VISP_INFO").getData(0).getString("pam_NOTIN_SOFTWARE_PRICE", ""));//软件应用服务费
       infos.put("NOTIN_NET_PRICE", info.getDataset("VISP_INFO").getData(0).getString("pam_NOTIN_NET_PRICE", ""));//网络技术支持服务费
       infos.put("NOTIN_NET_PRICE", info.getDataset("VISP_INFO").getData(0).getString("pam_NOTIN_NET_PRICE", ""));//网络技术支持服务费
       infos.put("NOTIN_NET_PRICE", info.getDataset("VISP_INFO").getData(0).getString("pam_NOTIN_NET_PRICE", ""));//网络技术支持服务费
       infos.put("NOTIN_A_CITY_NAME",StaticUtil.getStaticValue(null, "TD_M_AREA", "AREA_NAME", "AREA_CODE", info.getDataset("VISP_INFO").getData(0).getString("pam_NOTIN_A_CITY", "")));//A端所在市县
       infos.put("NOTIN_Z_CITY_NAME", StaticUtil.getStaticValue(null, "TD_M_AREA", "AREA_NAME", "AREA_CODE", info.getDataset("VISP_INFO").getData(0).getString("pam_NOTIN_Z_CITY", "")));//Z端所在市县
       infos.put("NOTIN_GROUP_CITY_NAME", StaticUtil.getStaticValue(null, "TD_M_AREA", "AREA_NAME", "AREA_CODE", info.getDataset("VISP_INFO").getData(0).getString("pam_NOTIN_GROUP_CITY", "")));//

       
       infos.put("NOTIN_AttrInternet", info.getString("NOTIN_AttrInternet", ""));
       infos.put("NOTIN_METHOD_NAME", info.getString("NOTIN_METHOD_NAME", ""));
       infos.put("NOTIN_COMMON_DATA", info.getString("NOTIN_COMMON_DATA", ""));
       infos.put("NOTIN_DATALINE_DATA", info.getString("NOTIN_DATALINE_DATA", ""));
       infos.put("NOTIN_CHANGE_DISABLED", info.getString("NOTIN_CHANGE_DISABLED", ""));

	   return infos;
	   
	   
   }
   
   
   
    
}
