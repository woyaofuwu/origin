
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.groupintf.transtrade.esp;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.exception.ProductException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UItemAInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaBBInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserGrpInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commonmethod.GrpCommonBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.ProductUtil;


public class EspMebCommonBean
{
	protected static final Logger log = Logger.getLogger(EspMebCommonBean.class);

    public static String getMemberUserId(IData map) throws Exception
    {
        // 1- 定义成员用户编号
        String memUserId = "";

        // 2- 获取成员手机号码
        String memSerialNumber = map.getString("SERIAL_NUMBER");

        // 3- 根据成员手机号获取成员用户信息
        IDataset memberUserInfoList = UserGrpInfoQry.getMemberUserInfoBySn(memSerialNumber);
        
        if(IDataUtil.isNotEmpty(memberUserInfoList)){
        	 memUserId = memberUserInfoList.getData(0).getString("USER_ID");
        }

     

        return memUserId;
    }
    public static String getProductUserId(IData map) throws Exception
    {
    	String productSpecCode = map.getString("PRODUCT_NUMBER");
    	String productOrderId=map.getString("PRODUCT_ORDER_ID");
        IData input=new DataMap();
        input.put("RSRV_VALUE_CODE", "ESPG");
        input.put("RSRV_STR4", productOrderId);
        input.put("RSRV_STR6", productSpecCode);//other表备用字段5存的是省内产品,6是全网产品ID
        IDataset productUserInfoList =getUserOtherInfoForEsp(input);//根据EC产品实例ID查USER_OTHER表
        String productUserId ="";
        if (IDataUtil.isNotEmpty(productUserInfoList))
        {
        	productUserId = productUserInfoList.getData(0).getString("USER_ID");
//            CSAppException.apperr(ProductException.CRM_PRODUCT_141, IntfField.BbossGrpMemBizRet.ERR11[0], IntfField.BbossGrpMemBizRet.ERR11[1], productOrderId);
        }        
       return productUserId;
    }
    public static boolean isBBRelExist(String productUserId, String memUserId,String productId) throws Exception
    {
        // 1- 定义返回结果
        boolean isExist = false;

        // 2- 查询台帐表
        IDataset merchMemTradeBBs = qryRelaBBInfoListByuserIdAB(productUserId, memUserId, Route.CONN_CRM_CG);
        if (IDataUtil.isNotEmpty(merchMemTradeBBs))
        {
            IData merchMemTradeBB = merchMemTradeBBs.getData(0);
            if (merchMemTradeBB.getString("MODIFY_TAG").equals("0"))
            {
                isExist = true;
            }
        }

        // 3- 查询资料表
        if (!isExist)
        {
//        	String productId = GrpCommonBean.getProductIdByUserId(productUserId);
            // 此处实际上既有商品的查询也有产品的查询，统一当作产品查询不影响结果
            String relationTypeCode = GrpCommonBean.getRelationTypeCodeByProdId(productId, "", true);
            IDataset merchMemBBs = RelaBBInfoQry.getBBByUserIdAB(productUserId, memUserId, "1", relationTypeCode);
            if (IDataUtil.isNotEmpty(merchMemBBs))
            {
                isExist = true;
            }
        }

        // 4- 返回结果
        return isExist;
    }
    public static IDataset getUserOtherInfoForEsp(IData map) throws Exception
    {
    	return Dao.qryByCodeParser("TF_F_USER_OTHER", "SEL_USEROTHER_INFO_FOR_ESP", map,Route.CONN_CRM_CG);
    }
    public static IDataset qryRelaBBInfoListByuserIdAB(String USER_ID_A, String USER_ID_B, String routId) throws Exception
    {
        IData inData = new DataMap();
        inData.put("USER_ID_A", USER_ID_A);
        inData.put("USER_ID_B", USER_ID_B);

        SQLParser sp = new SQLParser(inData);
        sp.addSQL("select b.MODIFY_TAG");
        sp.addSQL(" from TF_B_TRADE a, TF_B_TRADE_RELATION b");
        sp.addSQL(" where 1=1");
        sp.addSQL(" and a.trade_id=b.trade_id");
//        sp.addSQL(" and a.trade_type_code in ('4680','4681','4682')");
        sp.addSQL(" and b.user_id_a=:USER_ID_A");
        sp.addSQL(" and b.user_id_b=:USER_ID_B");
//        sp.addSQL(" and a.update_staff_id='IBOSS000'");
        sp.addSQL(" order by a.exec_time");

        return Dao.qryByParse(sp, Route.getJourDb(routId));
    }
    /**
     * 获取成员扩展属性
     * @param map
     * @return
     * @throws Exception
     */
    protected static IDataset getProductParamInfo(IData map) throws Exception
    {
        // 1- 定义产品属性对象集
        IDataset productParamInfoList = new DatasetList();
        String productSpecCode = map.getString("PRODUCT_NUMBER"); 
    	String productId = GrpCommonBean.merchToProduct(productSpecCode, 2 ,null);//查biz表的对应关系配置
        IDataset extendInfos=map.getDataset("EXTENDS");
        if(IDataUtil.isNotEmpty(extendInfos)){
        	   IDataset dsParam = new DatasetList();
               for (int i = 0; i<extendInfos.size();i++)
               {
                   IData tmpData = new DataMap();
                   tmpData.put("ATTR_CODE", extendInfos.getData(i).getString("CHARACTER_ID"));
                   tmpData.put("ATTR_VALUE", extendInfos.getData(i).getString("CHARACTER_VALUE"));
                   tmpData.put("ATTR_NAME",extendInfos.getData(i).getString("CHARACTER_NAME"));
                   tmpData.put("STATE", "ADD");
                   dsParam.add(tmpData);
               }
               IData productParamObj = new DataMap();
               productParamObj.put("PRODUCT_PARAM", dsParam);
               productParamObj.put("PRODUCT_ID", productId);// 成员基础产品编号
               productParamInfoList.add(productParamObj);
        }      

        // 2- 返回产品属性对象
        return productParamInfoList;
    }
   

    protected static String getMebElementInfo(String productID) throws Exception
    {
    	 // 1- 定义成员基本产品的产品编号
        String memProductId = "";

        // 2- 获取集团产品对应的成员产品
        IDataset memProductSet = ProductUtil.getMebProduct(productID);
        if(log.isDebugEnabled()){
       	 log.debug("getMebElementInfo-memProductSet"+memProductSet);
         }

        // 3- 循环成员产品找到对应的基本成员产品
        for (int row = 0; row < memProductSet.size(); row++)
        {
            IData memProduct = (IData) memProductSet.get(row);
            // 成员附加基本产品
            if (memProduct.getString("FORCE_TAG").equals("1"))
            {
                memProductId = memProduct.getString("PRODUCT_ID_B");
                break;
            }
        }
        if(log.isDebugEnabled()){
          	 log.debug("getMebElementInfo-memProductId"+memProductId);
            }
        // 4- 返回成员产品编号
        return memProductId;
    }
    protected static IDataset getElementInfo(IData map, String memProductId) throws Exception
    {
    	 IDataset elementInfoList = new DatasetList();
    	 IDataset elementRates=map.getDataset("MEMBER_RATE_PLAN");
    	 if(IDataUtil.isNotEmpty(elementRates)){
    		 //截止以前的老资费及其属性  	
             delOldDiscntInfo(map,memProductId,elementInfoList);
             //添加新资费及其属性
             addNewDiscntInfo(map,memProductId,elementInfoList); 
    	 }
    	  	     	 
    	 return elementInfoList;
    }  
    protected static void delOldDiscntInfo(IData map, String memProductId,IDataset elementInfoList) throws Exception
    {
    	String memUserId =getMemberUserId(map);
    	String productUserId = getProductUserId(map);
        IDataset discntDataset = UserDiscntInfoQry.getUserDiscntByUserIdAB(memUserId,productUserId); 	   
        if(log.isDebugEnabled()){
   	    log.debug("delOldDiscntInfo-discntDataset"+discntDataset);
   	    }   	  
        if (IDataUtil.isNotEmpty(discntDataset))
   	    {
          
   	      for(int i=0;i<discntDataset.size();i++){
   	    	 IData discntInfo=discntDataset.getData(i);
   	    	 IData elementData=new DataMap();
   	        // 1- 添加资费实例ID，受理时默认为""
         	 elementData.put("INST_ID", discntInfo.getString("INST_ID"));
   
             // 2- 添加元素类型，资费类型对应为"D"，服务类型对应为"S"
         	 elementData.put("ELEMENT_TYPE_CODE", "D");
   
             // 3- 删除资费状态，默认为"1"
         	 elementData.put("MODIFY_TAG", "1");
             // 4- 添加产品产品编号
         	 elementData.put("PRODUCT_ID", memProductId);
             // 5- 添加元素ID
         	 elementData.put("ELEMENT_ID", discntInfo.getString("DISCNT_CODE"));
   
             // 6- 添加包信息
         	 elementData.put("PACKAGE_ID", discntInfo.getString("PACKAGE_ID"));
   
             // 9- 添加开始时间
         	 elementData.put("START_DATE", discntInfo.getString("START_DATE"));
   
             // 10- 添加结束时间
         	 String startDate=discntInfo.getString("START_DATE");
         	 String endDate=SysDateMgr.getLastDateThisMonth();
//         	 if(SysDateMgr.getTimeDiff(startDate,endDate,SysDateMgr.PATTERN_STAND)<0){
//         		endDate=startDate;
//         	 }
         	 elementData.put("END_DATE", endDate);
         	 
         	 IDataset discntAttrInfo=UserAttrInfoQry.getUserAttrForEsp(memUserId,discntInfo.getString("INST_ID"));
         	 if(log.isDebugEnabled()){
           	    log.debug("delOldDiscntInfo-discntAttrInfo"+discntAttrInfo);
           	    }   
         	 IDataset attrParams=new DatasetList();
         	 if(IDataUtil.isNotEmpty(discntAttrInfo)){        		
         		for(int j=0;j<discntAttrInfo.size();j++){
         			IData attrParam = new DataMap();
         			IData elementAttr=discntAttrInfo.getData(j);        			
         			attrParam.put("ATTR_CODE", elementAttr.getString("ATTR_CODE"));
         			IDataset attrInfos=UItemAInfoQry.queryOfferChaAndValByCond(discntInfo.getString("DISCNT_CODE"), "D","",elementAttr.getString("ATTR_CODE"));
              		attrParam.put("ATTR_NAME",attrInfos.getData(0).getString("ATTR_LABLE"));
              		attrParam.put("ATTR_VALUE",elementAttr.getString("ATTR_VALUE"));
                    attrParams.add(attrParam);	 
         		}
         		elementData.put("ATTR_PARAM", attrParams);
         	 }
         	 elementInfoList.add(elementData);
             if(log.isDebugEnabled()){
              	 log.debug("delOldDiscntInfo-elementInfoList"+elementInfoList);
             }
   	      }
   	   
   	   }
    }
    protected static void addNewDiscntInfo(IData map, String memProductId,IDataset elementInfoList) throws Exception
    {
    	IDataset elementRates=map.getDataset("MEMBER_RATE_PLAN");
   	    if(log.isDebugEnabled()){
          	 log.debug("addNewDiscntInfo-elementRates"+elementRates);
         }
   	    if(IDataUtil.isNotEmpty(elementRates)){   	
   	      for(int i=0;i<elementRates.size();i++){ 
   	    	IData elementData=new DataMap();
         	 String EspelementId=elementRates.getData(i).getString("MEMBER_PATE_PLANID"); 
         	 String elementId=GrpCommonBean.merchToProduct(EspelementId, 1 ,memProductId);
         	 IDataset result=ProductInfoQry.getElementByProductIdElemId(memProductId,elementId);
         	if(log.isDebugEnabled()){
              	 log.debug("addNewDiscntInfo-EspelementId"+EspelementId+"---"+elementId);
              	 log.debug("addNewDiscntInfo-result"+result);
                }
         	 if(IDataUtil.isEmpty(result)){
         		CSAppException.apperr(ProductException.CRM_PRODUCT_1, memProductId+"|"+elementId); 
         	 }
         	 String elementTypeCode=result.getData(0).getString("ELEMENT_TYPE_CODE");
             // 1- 添加资费实例ID，受理时默认为""
         	 elementData.put("INST_ID", "");
   
             // 2- 添加元素类型，资费类型对应为"D"，服务类型对应为"S"
         	 elementData.put("ELEMENT_TYPE_CODE", elementTypeCode);
   
             // 3- 添加资费状态，默认为"0"
         	 elementData.put("MODIFY_TAG", "0");
             // 4- 添加产品产品编号
         	 elementData.put("PRODUCT_ID", memProductId);
             // 5- 添加元素ID
         	 elementData.put("ELEMENT_ID", elementId);
   
             // 6- 添加包信息
         	 elementData.put("PACKAGE_ID", result.getData(0).getString("PACKAGE_ID"));
   
             // 9- 添加开始时间
         	 elementData.put("START_DATE", map.getString("EFF_DATE",SysDateMgr.getFirstDayOfNextMonth()));
   
             // 10- 添加结束时间
         	 elementData.put("END_DATE", SysDateMgr.getTheLastTime());
         	//获取资费参数(从传过来的参数中获取？)
          	IDataset elementAttrs=elementRates.getData(i).getDataset("RATE_PARAM");
          	if(log.isDebugEnabled()){
             	 log.debug("addNewDiscntInfo-elementAttrs"+elementAttrs);
               }
          	IDataset attrParams = new DatasetList();         	
          	if(IDataUtil.isNotEmpty(elementAttrs)){          	 
          	  for(int a=0;a<elementAttrs.size();a++){  
          		 IData attrParam = new DataMap();
          		 String attrCode=elementAttrs.getData(a).getString("PARAMETER_NUMBER");
          		 String attrValue=elementAttrs.getData(a).getString("PARAM_VALUE");          		
          		 IDataset attrInfos=UItemAInfoQry.queryOfferChaAndValByCond(elementId,elementTypeCode, "", attrCode);
          		if(log.isDebugEnabled()){
               	 log.debug("addNewDiscntInfo-attrCode"+attrCode+"---"+attrValue);
               	 log.debug("addNewDiscntInfo-attrInfos"+attrInfos);
                 }
          		 attrParam.put("ATTR_CODE", attrCode);
          		 attrParam.put("ATTR_NAME", attrInfos.getData(0).getString("ATTR_LABLE"));
          		 attrParam.put("ATTR_VALUE", attrValue);
                 attrParams.add(attrParam);	             
          	 }        	 
          	}
          	elementData.put("ATTR_PARAM", attrParams);
            elementInfoList.add(elementData);
            if(log.isDebugEnabled()){
              	 log.debug("addNewDiscntInfo-elementInfoList"+elementInfoList);
             }
         	}
   	    }
    }
}
