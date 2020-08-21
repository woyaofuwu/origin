
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commoncomponent;

import org.apache.log4j.Logger;
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.group.common.GroupBaseConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaBBInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commonmethod.GrpCommonBean;

/**
 * @description 某些业务有特殊要求，需要通过产品参数控制资费或者资费控制产品参数
 * @author xunyl
 * @date 2014-08-05
 */
public class BbossDisAttrTransBean
{
	protected static final Logger log = Logger.getLogger(BbossDisAttrTransBean.class);
    /**
     * @description 通过产品参数将折扣率同步至各产品的资费(典型场景为公众服务云业务)
     * @author xunyl
     * @date 2014-08-05
     */
    public static void addDisAttrByProductAttr(IData map, String merchOperType) throws Exception
    {
        if (merchOperType.equals(GroupBaseConst.MERCH_STATUS.MERCH_ADD.getValue()))
        {
            addIcbTradeInfoForCrtUs(map);
        }
        else if (merchOperType.equals(GroupBaseConst.MERCH_STATUS.MERCH_MODIFY_GROUP.getValue()))
        {
            addIcbTradeInfoForChgUsRela(map);
        }else if(merchOperType.equals(GroupBaseConst.MERCH_STATUS.MERCH_MODIFY_PARAM.getValue())||merchOperType.equals(GroupBaseConst.MERCH_STATUS.MERCH_ADD.getValue())){
        	checkTestDateValue(map,merchOperType);
        }
    }
    private static void checkTestDateValue(IData map,String merchOperType) throws Exception
    {
    	 IDataset productInfoList = map.getDataset("ORDER_INFO");
    	 log.debug("checkTestDateValue-productInfoList"+productInfoList);
         if (IDataUtil.isEmpty(productInfoList))
         {
             return;
         }
         for (int i = 0; i < productInfoList.size(); i++)
         {
             IData productInfo = productInfoList.getData(i);
             String userId=productInfo.getString("USER_ID","");
             IDataset bbossParamInfoList = productInfo.getDataset("PRODUCT_PARAM_INFO");
             log.debug("checkTestDateValue-bbossParamInfoList"+bbossParamInfoList);
             if (IDataUtil.isEmpty(bbossParamInfoList))
             {
                 continue;
             }
             IData bbossParamInfo = bbossParamInfoList.getData(0);
             // 2- 获取产品参数集
             IDataset productParamInfoList = bbossParamInfo.getDataset("PRODUCT_PARAM");
             log.debug("checkTestDateValue-productParamInfoList"+productParamInfoList);
             if (IDataUtil.isEmpty(productParamInfoList))
             {
                 return;
             }

             // 3- 循环产品参数集获取折扣值
             for (int j = 0; j < productParamInfoList.size(); j++)
             {
                 IData productParamInfo = productParamInfoList.getData(j);
                 String paramCode = productParamInfo.getString("ATTR_CODE");
                 log.debug("checkTestDateValue-paramCode"+paramCode);
                 if(StringUtils.isNotBlank(paramCode) && "1116013003".equals(paramCode)){
                 	String testDateValue=SysDateMgr.decodeTimestamp(productParamInfo.getString("ATTR_VALUE",""), SysDateMgr.PATTERN_STAND_YYYYMMDD);
                 	if(merchOperType.equals(GroupBaseConst.MERCH_STATUS.MERCH_ADD.getValue())){
                 		String month1=SysDateMgr.getAddMonthsLastDay(0);
                 		String month2=SysDateMgr.getAddMonthsLastDay(2);
                 		if(SysDateMgr.compareTo(testDateValue, month1)<0||SysDateMgr.compareTo(testDateValue, month2)>0){
                    		 CSAppException.apperr(CrmCommException.CRM_COMM_103,paramCode+"可取值范围为：当月最后一天到下下月最后一天");
                    	}
                 	}else if(merchOperType.equals(GroupBaseConst.MERCH_STATUS.MERCH_MODIFY_PARAM.getValue())){
                 		IDataset UserAttrInfo=UserAttrInfoQry.getGrpAttrInfoAttrCode(userId, paramCode, "P");
                 		log.debug("---UserAttrInfo="+UserAttrInfo);
                 		if(IDataUtil.isNotEmpty(UserAttrInfo)){
                 			if(UserAttrInfo.size()>=2){
                         		CSAppException.apperr(CrmCommException.CRM_COMM_103, "免费测试期只可以延长一次！");
                         	}
                 			String oldValue="";
                 			for(int a=0;a<UserAttrInfo.size();a++){
                 				String endDate=SysDateMgr.decodeTimestamp(UserAttrInfo.getData(a).getString("END_DATE",""), SysDateMgr.PATTERN_STAND_YYYYMMDD);
                 				String sysdate=SysDateMgr.getSysDate();
                 				if(SysDateMgr.compareTo(endDate,sysdate)>0){//有效的那条
                 					oldValue=SysDateMgr.decodeTimestamp(UserAttrInfo.getData(a).getString("ATTR_VALUE",""), SysDateMgr.PATTERN_STAND_YYYYMMDD);
                 				    break;
                 				}
                 			}
                 			String month=SysDateMgr.getAddMonthsLastDay(4,oldValue);//因为此方法偏移时算了本月，所以延长三个月从下月开始，应该是4
                        	log.debug("--testDateValue="+testDateValue+"--month"+month);
                        	log.debug("--11="+SysDateMgr.compareTo(testDateValue, month));
                         	if(SysDateMgr.compareTo(testDateValue, month)>0){
                         		 CSAppException.apperr(CrmCommException.CRM_COMM_103, "变更测试期结束时间最多可延长三个月！");
                         	}
                 		}    		
                 	}
                 }          
             }
         }
    }

    /**
     * @description 查询各产品的资费信息并添加资费参数
     * @author xunyl
     * @date 2014-08-06
     */
    private static void addDisIcbParamInfo(IData productInfo, String account) throws Exception
    {
        // 1- 产品不存在资费信息，无需添加资费参数
        IDataset elementInfoList = productInfo.getDataset("ELEMENT_INFO");
        if (IDataUtil.isEmpty(elementInfoList))
        {
            return;
        }

        // 2- 添加折扣参数
        for (int i = 0; i < elementInfoList.size(); i++)
        {
            // 2-1 元素类型非资费的，无需添加资费参数
            IData elementInfo = elementInfoList.getData(i);
            String elementTypeCode = elementInfo.getString("ELEMENT_TYPE_CODE");
            if (!"D".equals(elementTypeCode))
            {
                continue;
            }

            // 2-2 获取资费对应ICB参数信息
            IDataset icbParamInfoList = elementInfo.getDataset("ATTR_PARAM");
            if (IDataUtil.isEmpty(icbParamInfoList))
            {
                icbParamInfoList = new DatasetList();
                elementInfo.put("ATTR_PARAM", icbParamInfoList);
            }

            // 2-3 添加折扣参数
            IData icbParam = new DataMap();
            String elementId = elementInfo.getString("ELEMENT_ID");
            icbParam.put("ATTR_CODE", "ACCOUT_" + elementId);
            icbParam.put("ATTR_VALUE", account);
            icbParam.put("ATTR_NAME", "套餐折扣");
            icbParamInfoList.add(icbParam);
        }
    }

    /**
     * @description 商品属性变更
     * @author xunyl
     * @date 2014-08-05
     */
    public static void addIcbTradeInfoForChgUsParams(String productUserId, String productId, String accout, IDataset paramInfoList) throws Exception
    {
        // 1- 根据产品用户编号查找出商品用户编号
        String merchRelationTypeCode = GrpCommonBean.getRelationTypeCodeByProdId("", productId, true);
        IDataset relaBBInfoList = RelaBBInfoQry.qryRelaBBInfoByUserIdBRelaTypeCode(productUserId, merchRelationTypeCode, Route.CONN_CRM_CG);
        if (IDataUtil.isEmpty(relaBBInfoList))
        {
            return;
        }
        IData relaBBInfo = relaBBInfoList.getData(0);
        String merchUserId = relaBBInfo.getString("USER_ID_A");

        // 2- 根据商品用户编号关联出对应的BB关系信息
        relaBBInfoList = RelaBBInfoQry.qryRelaBBInfoByRoleCodeBForGrp(merchUserId, merchRelationTypeCode, "0");
        if (IDataUtil.isEmpty(relaBBInfoList))
        {
            return;
        }

        // 3- 循环BB关系，查找各产品用户下的资费信息,并登记修改的资费参数信息
        for (int i = 0; i < relaBBInfoList.size(); i++)
        {
            // 3-1 获取用户参数信息，包括产品参数、资费参数和服务参数
            relaBBInfo = relaBBInfoList.getData(i);
            productUserId = relaBBInfo.getString("USER_ID_B");
            IDataset userAttrInfoList = UserAttrInfoQry.getUserAttrByUserId(productUserId);
            if (IDataUtil.isEmpty(userAttrInfoList))
            {
                continue;
            }

            // 3-2 循环用户参数列表，处理资费参数信息
            for (int j = 0; j < userAttrInfoList.size(); j++)
            {
                IData userAttrInfo = userAttrInfoList.getData(j);
                String instType = userAttrInfo.getString("INST_TYPE");
                if (!"D".equals(instType))
                {
                    continue;
                }
                String attrCode = userAttrInfo.getString("ATTR_CODE");
                if (attrCode.indexOf("ACCOUT_") == 0)
                {
                    userAttrInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                    userAttrInfo.put("ATTR_VALUE", accout);
                    userAttrInfo.put("IS_NEED_PF", "0");
                    paramInfoList.add(userAttrInfo);
                }
            }
        }
    }

    /**
     * @description 商品组成关系变更
     * @author xunyl
     * @date 2014-08-05
     */
    private static void addIcbTradeInfoForChgUsRela(IData map) throws Exception
    {
        // 1- 如果产品列表中没有新增的产品，则无需添加资费的折扣参数，直接返回
        IDataset productInfoList = map.getDataset("ORDER_INFO");
        if (IDataUtil.isEmpty(productInfoList))
        {
            return;
        }
        int i = 0;
        for (i = 0; i < productInfoList.size(); i++)
        {
            IData productInfo = productInfoList.getData(i);
            String dealType = productInfo.getString("DEAL_TYPE");
            if (GroupBaseConst.GROUP_CHANGE_DEAL_TYPE.MERCH_CHANGE_PRODUCT_ADD.getValue().equals(dealType))
            {
                break;
            }
        }
        if (i == productInfoList.size())
        {
            return;
        }

        // 2- 获取折扣属性值
        String account = "";
        account = qryAccountValFroChgUsRela(map);

        // 3- 查询各产品的资费信息并添加资费参数
        for (int j = 0; j < productInfoList.size(); j++)
        {
            IData productInfo = productInfoList.getData(j);
            String dealType = productInfo.getString("DEAL_TYPE");
            if (!GroupBaseConst.GROUP_CHANGE_DEAL_TYPE.MERCH_CHANGE_PRODUCT_ADD.getValue().equals(dealType))
            {
                continue;
            }
            addDisIcbParamInfo(productInfo, account);
        }
    }

    /**
     * @description 集团受理
     * @author xunyl
     * @date 2014-08-05
     */
    private static void addIcbTradeInfoForCrtUs(IData map) throws Exception
    {
        // 1- 获取折扣属性值
        String account = "";
        IDataset productInfoList = map.getDataset("ORDER_INFO");
        if (IDataUtil.isEmpty(productInfoList))
        {
            return;
        }
        for (int i = 0; i < productInfoList.size(); i++)
        {
            IData productInfo = productInfoList.getData(i);
            IDataset bbossParamInfoList = productInfo.getDataset("PRODUCT_PARAM_INFO");
            if (IDataUtil.isEmpty(bbossParamInfoList))
            {
                continue;
            }
            IData bbossParamInfo = bbossParamInfoList.getData(0);
            String productId = bbossParamInfo.getString("PRODUCT_ID");
            String merchpSpecNum = GrpCommonBean.productToMerch(productId, 0);
            if ("111601".equals(merchpSpecNum))
            {
                account = qryAccountValFroCrtUs(bbossParamInfo);
            }
        }

        // 2- 查询各产品的资费信息并添加资费参数
        for (int j = 0; j < productInfoList.size(); j++)
        {
            IData productInfo = productInfoList.getData(j);
            addDisIcbParamInfo(productInfo, account);
        }
    }

    /**
     * @description 获取商产品组成关系变更时的套餐折扣值
     * @author xunyl
     * @date 2014-08-06
     */
    private static String qryAccountValFroChgUsRela(IData map) throws Exception
    {
        // 1- 定义返回的折扣值
        String accoutValue = "100";

        // 2- 查找商产品BB关系信息
        IData merchInfo = map.getData("MERCH_INFO");
        String merchId = merchInfo.getString("PRODUCT_ID");
        String merchRelationTypeCode = GrpCommonBean.getRelationTypeCodeByProdId(merchId, "", true);
        String merchUserId = merchInfo.getString("USER_ID");
        IDataset relaBBInfoList = RelaBBInfoQry.qryRelaBBInfoByRoleCodeBForGrp(merchUserId, merchRelationTypeCode, "0");
        if (IDataUtil.isEmpty(relaBBInfoList))
        {
            return accoutValue;
        }

        // 3- 根据BB关系记录查找及产品中的折扣值
        for (int j = 0; j < relaBBInfoList.size(); j++)
        {
            IData relaBBInfo = relaBBInfoList.getData(j);
            String userIdB = relaBBInfo.getString("USER_ID_B");
            IDataset productUserInfoList = UserProductInfoQry.queryProductByUserId(userIdB);
            if (IDataUtil.isEmpty(productUserInfoList))
            {
                continue;
            }
            IData productUserInfo = productUserInfoList.getData(0);
            String bossProductId = productUserInfo.getString("PRODUCT_ID");
            String bbossProductId = GrpCommonBean.productToMerch(bossProductId, 0);
            if (!"111601".equals(bbossProductId))
            {
                continue;
            }
            String productUserId = productUserInfo.getString("USER_ID");
            IDataset userAttrInfoList = UserAttrInfoQry.getUserAttr(productUserId, "P", "1116013004", null);
            if (IDataUtil.isEmpty(userAttrInfoList))
            {
                break;
            }
            IData userAttrInfo = userAttrInfoList.getData(0);
            accoutValue = userAttrInfo.getString("ATTR_VALUE");
            break;
        }

        // 4- 返回折扣值
        return accoutValue;
    }

    /**
     * @description 获取集团受理时的套餐折扣值
     * @author xunyl
     * @date 2014-08-06
     */
    private static String qryAccountValFroCrtUs(IData bbossParamInfo) throws Exception
    {
        // 1- 定义返回的折扣值
        String accoutValue = "100";

        // 2- 获取产品参数集
        IDataset productParamInfoList = bbossParamInfo.getDataset("PRODUCT_PARAM");
        if (IDataUtil.isEmpty(productParamInfoList))
        {
            return accoutValue;
        }

        // 3- 循环产品参数集获取折扣值
        for (int i = 0; i < productParamInfoList.size(); i++)
        {
            IData productParamInfo = productParamInfoList.getData(i);
            String paramCode = productParamInfo.getString("ATTR_CODE");
            if (StringUtils.isNotBlank(paramCode) && "1116013004".equals(paramCode))
            {
                accoutValue = productParamInfo.getString("ATTR_VALUE");
                break;
            }
        }

        // 4- 返回折扣值
        return accoutValue;
    }
    
    /**
   	 * 对某些需要特殊处理的产品，同步产品属性到本地资费
   	 * @param map
   	 * @return
   	 * @throws Exception
   	 */
   	public static void addProductAndDisInfoToLocDis(IData map, String productId, String merchOperType) throws Exception {
   		
       	IData productInfo=getProductInfo(map,productId);
       	//获取产品信息
       	if(IDataUtil.isEmpty(productInfo)){
       		return;
       	}
       	
       		//集团新增或修改商品组成关系
       	if(merchOperType.equals(GroupBaseConst.MERCH_STATUS.MERCH_ADD.getValue())||merchOperType.equals(GroupBaseConst.MERCH_STATUS.MERCH_MODIFY_GROUP.getValue()))
           {	
       			
       		IDataset disParams = CommparaInfoQry.getCommparaAllColByParser("CSM", "4889", "BBOSS_DISATTRTOLOCDISATTR", "ZZZZ");
       		if(IDataUtil.isEmpty(disParams)){
       	     	return;
       	     }   		
       		//获取到需要转本地的资费信息，将此资费属性值同步到本地资费
           	addDisInfoToDisParam(productInfo, disParams);
           	
       		    		
       		IDataset productParams = CommparaInfoQry.getCommparaAllColByParser("CSM", "4889", "BBOSS_PROATTRTOLOCDISATTR", "ZZZZ");
       		if(IDataUtil.isEmpty(productParams)){
       	     	return;
       	     }
       		//获取需要转本地的产品属性信息，将产品属性值放到本地资费
   			addProAttrInfoToDisParam(productInfo, productParams);


       		//修改集团订购资费	
            }else if(merchOperType.equals(GroupBaseConst.MERCH_STATUS.MERCH_MODIFY_DISCNT.getValue())){
            	
        		IDataset disParams = CommparaInfoQry.getCommparaAllColByParser("CSM", "4889", "BBOSS_DISATTRTOLOCDISATTR", "ZZZZ");
        		if(IDataUtil.isEmpty(disParams)){
        	     	return;
        	     }
        		for(int i=0;i<disParams.size();i++){
        	     	//获取到需要转本地的资费信息，将此资费属性值同步到本地资费
        			IData disParam = disParams.getData(i);    			
        			addDisInfoToDisParamForChg(productInfo, disParam);
        	        	     	
        	     }
       	 
           	//修改订购产品属性
   	    }else if (merchOperType.equals(GroupBaseConst.MERCH_STATUS.MERCH_MODIFY_PARAM.getValue())){
       		
   	    	IDataset productParams = CommparaInfoQry.getCommparaAllColByParser("CSM", "4889", "BBOSS_PROATTRTOLOCDISATTR", "ZZZZ");
       		if(IDataUtil.isEmpty(productParams)){
       	     	return;
       	     }
       		
       		addProInfoToDisParamForChg(productInfo, productParams);

   		}
   		
   	}
    
   	
   	public static IData getProductInfo(IData map,String targetedProductId) throws Exception{
    	IDataset productsInfo=map.getDataset("ORDER_INFO");
    	IData productInfo=new DataMap();
    	String productId="";
    	if(IDataUtil.isEmpty(productsInfo)){
    		return productInfo;
    	}
    	for(int i=0;i<productsInfo.size();i++){
    		IData temp=productsInfo.getData(i);
    		productId=temp.getString("PRODUCT_ID","");
    		String bbossProductId=GrpCommonBean.productToMerch(productId, 0);
    		if(targetedProductId.equals(bbossProductId)){
    			productInfo=temp;
    			return productInfo;
    		}
    	}
    	return productInfo;
    }
    
    /**
     * 将资费属性同步到本地资费
     * @param productInfo
     * @param param
     * @throws Exception
     */
    public static void addDisInfoToDisParam(IData productInfo, IDataset params) throws Exception{
    	IDataset elementInfos = productInfo.getDataset("ELEMENT_INFO",new DatasetList());
    	if(IDataUtil.isEmpty(elementInfos)){
    		return;
    	}
		IData elementInfo = new DataMap();

	    for(int i=0;i<elementInfos.size();i++){	    		

	        for(int j=0; j<params.size(); j++){
	    	    String paramElementId = params.getData(j).getString("PARA_CODE1");//获取配置中的全网资费 element_id
	    	    //控制同一个本地资费只需拼一次
	    	    if(j>=1&&params.getData(j).getString("PARA_CODE3").equals(params.getData(j-1).getString("PARA_CODE3"))){
	    			continue;
	    		}
	    		IData element = elementInfos.getData(i);
				String elementId = element.getString("ELEMENT_ID","");
				String bbossElementId = GrpCommonBean.productToMerch(elementId, 1);//将省内资费ID转为全网资费ID
	    		if(bbossElementId.equals(paramElementId)){	    	    	
	    			
	    	    	elementInfo.put("START_DATE", element.getString("START_DATE"));
	    	    	elementInfo.put("END_DATE", element.getString("END_DATE"));
	    			elementInfo.put("PRODUCT_ID", "-1");
	    			elementInfo.put("PACKAGE_ID", "-1");
	    			elementInfo.put("ELEMENT_ID", params.getData(j).getString("PARA_CODE3"));	
	    			elementInfo.put("ELEMENT_TYPE_CODE", "D");
	    			elementInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
	    			
	    			IDataset disparam = CommparaInfoQry.getCommparaByCodeCode1("CSM", "4889", "BBOSS_DISATTRTOLOCDISATTR", paramElementId);//拿到同一种全网资费的相关配置
	    			IDataset disAttrParamList =  new DatasetList();
	    			for(int k=0;k<disparam.size();k++){
	    				if(disparam.getData(k).getString("PARA_CODE3").equals(paramElementId)){
	    					IData disAttrParam = new DataMap();
	    					disAttrParam.put("ATTR_CODE",disparam.getData(k).getString("PARA_CODE4"));
	    					disAttrParam.put("ATTR_VALUE",getDisAttrValueByProductInfo(productInfo, disparam.getData(k).getString("PARA_CODE1"), disparam.getData(k).getString("PARA_CODE2")));
	    					disAttrParamList.add(disAttrParam);
	    				}
	    			}
	    		
	    			elementInfo.put("ATTR_PARAM", disAttrParamList);
	    			elementInfos.add(elementInfo);
	    			
	    			
	    			
	    		}
	    		
	    	}

    	}
    	productInfo.put("ELEMENT_INFO", elementInfos);
    }
    
    /**
     * 获取资费属性值
     * @param productInfo
     * @param data
     * @return
     * @throws Exception
     */
    public static String getDisAttrValueByProductInfo(IData productInfo,String taretedElementId,String attrCode) throws Exception{
    	String attrValue="";
    	IDataset elementsInfo=productInfo.getDataset("ELEMENT_INFO");
		for(int j=0;j<elementsInfo.size();j++){
			IData element=elementsInfo.getData(j);
			String elementId=element.getString("ELEMENT_ID","");
			String bbossElementId=GrpCommonBean.productToMerch(elementId, 1);
			if(((bbossElementId==null||"".equals(bbossElementId))&&elementId.equals(taretedElementId))||taretedElementId.equals(bbossElementId)){
				IDataset elementParams=element.getDataset("ATTR_PARAM");
				for(int z=0;z<elementParams.size();z++){
					IData elementParam=elementParams.getData(z);
					if(elementParam.getString("ATTR_CODE").equals(attrCode)){
						attrValue=elementParam.getString("ATTR_VALUE");
					}
				}
			}
		}
		return attrValue;
    }
    
    /**
     * 将产品属性值同步到本地资费
     * @param productInfo
     * @param proSeatNumberAttrCode
     * @param disSeatNumberAttrCode
     * @param disInfos
     * @throws Exception
     */
    public static void addProAttrInfoToDisParam(IData productInfo, IDataset params) throws Exception{
    	IDataset elementInfos = productInfo.getDataset("ELEMENT_INFO",new DatasetList());//获取前台资费信息    	
    	IDataset productParams = productInfo.getDataset("PRODUCT_PARAM_INFO",new DatasetList());//获取前台产品参数信息
    	
    	if(IDataUtil.isEmpty(productParams)){
    		return;
    	}
    	for(int i=0; i<productParams.size(); i++){//循环前台参数信息，如果前台产品属性符合配置，则进行产品属性转本地资费操作
    		IData productParam = productParams.getData(i);
    		IDataset productAttrs = productParam.getDataset("PRODUCT_PARAM");

    		for(int j=0;j<productAttrs.size();j++){
        		for(int k=0; k<params.size(); k++){
        	    	String proAttrCode = params.getData(k).getString("PARA_CODE1");  //获取配置中的全网产品属性ID
        	    	IData productAttr = productAttrs.getData(j);
					
        	    	if(proAttrCode.equals(productAttr.getString("ATTR_CODE"))){
				    	for(int m=0; m<elementInfos.size(); m++){
				    		IData elementInfo = elementInfos.getData(m);
				    		if(elementInfo.getString("ELEMENT_ID").equals(productParam.getString("PARA_CODE4"))){
				    			
				    			IDataset disAttrParamList =  new DatasetList();
								IData disAttrParam1 = new DataMap();
								disAttrParam1.put("ATTR_CODE", params.getData(k).getString("PARA_CODE3"));//约定的本地资费属性编码
								disAttrParam1.put("ATTR_VALUE", productAttr.getString("ATTR_VALUE"));//获取产品属性值
								
								disAttrParamList.add(disAttrParam1);
								
								elementInfo.put("ATTR_PARAM", disAttrParamList);
								elementInfos.add(elementInfo);

				    	}
				    }														
				 }
        	 }
    		
    	  }
    	
       }
    	productInfo.put("ELEMENT_INFO", elementInfos);
    }
    
    
    /**
     * 资费变更时，将资费属性值同步到本地资费
     * @param productInfo
     * @param param
     * @throws Exception
     */
    public static void addDisInfoToDisParamForChg(IData productInfo,IData param) throws Exception{
    	IDataset elementInfos = productInfo.getDataset("ELEMENT_INFO",new DatasetList());
    	if(IDataUtil.isEmpty(elementInfos)){
    		return;
    	}
    	String paramElementId = param.getString("PARA_CODE1");//获取配置中的全网资费 element_id
    	String paramAttrCode = param.getString("PARA_CODE2");//获取配置中的全网资费属性attr_code
    	
    	for(int i=0;i<elementInfos.size();i++){
    		IData element = elementInfos.getData(i);
        	IDataset elementParams=element.getDataset("ATTR_PARAM");

			String elementId = element.getString("ELEMENT_ID","");
			String bbossElementId = GrpCommonBean.productToMerch(elementId, 1);//将省内资费ID转为全网资费ID
 
    		if(bbossElementId.equals(paramElementId)){
    			for(int j=0; j<elementParams.size(); j++){
    				IData elementParam=elementParams.getData(j);
    				if(elementParam.getString("ATTR_CODE").equals(paramAttrCode)){
    					
    					String userId = productInfo.getString("USER_ID");
    			    	IDataset userDisInfos = UserDiscntInfoQry.queryDiscntByUserIdAndDiscntCode(userId, param.getString("PARA_CODE1"));
    			
    			    	IData userDisInfo = userDisInfos.getData(0);
    					IData elementInfo = new DataMap();
    					
    			    	elementInfo.put("START_DATE", userDisInfo.getString("START_DATE"));
    			    	elementInfo.put("END_DATE", userDisInfo.getString("END_DATE"));
    					elementInfo.put("PRODUCT_ID", userDisInfo.getString("PRODUCT_ID"));
    					elementInfo.put("PACKAGE_ID", userDisInfo.getString("PACKAGE_ID"));
    					elementInfo.put("ELEMENT_ID", userDisInfo.getString("DISCNT_CODE"));	
    					elementInfo.put("ELEMENT_TYPE_CODE", "D");
    					elementInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
    					
    					IDataset disAttrParamList =  new DatasetList();
    					IData disAttrParam = new DataMap();
    					disAttrParam.put("ATTR_CODE",param.getString("PARA_CODE4"));
    					disAttrParam.put("ATTR_VALUE",elementParam.getString("ATTR_VALUE"));
    					disAttrParamList.add(disAttrParam);
    						
    					elementInfo.put("ATTR_PARAM", disAttrParamList);
    					elementInfos.add(elementInfo);
    					
    					productInfo.put("ELEMENT_INFO", elementInfos);
    		    	  
    				}	
    	
    			}
    		}
    	}
    	
    }
    /**
     * 产品属性变更时，同步产品属性值到本地资费 
     * @param productInfo
     * @param param
     * @throws Exception
     */
    public static void addProInfoToDisParamForChg(IData productInfo,IDataset params) throws Exception{
    	
    	IDataset elementInfos=productInfo.getDataset("ELEMENT_INFO",new DatasetList());
    	
    	IDataset productParams = productInfo.getDataset("PRODUCT_PARAM_INFO",new DatasetList());//获取前台产品参数信息
    	if(IDataUtil.isEmpty(productParams)){
    		return;
    	}    	
    	for(int i=0; i<params.size(); i++){
	    	IData elementInfo = new DataMap();
	    	String userId=productInfo.getString("USER_ID");
	    	IDataset userDisInfos=UserDiscntInfoQry.queryDiscntByUserIdAndDiscntCode(userId, params.getData(i).getString("PARA_CODE2"));
	    	IData userDisInfo=userDisInfos.getData(0);
	    	elementInfo.put("START_DATE", userDisInfo.getString("START_DATE"));
	    	elementInfo.put("END_DATE", userDisInfo.getString("END_DATE"));
			elementInfo.put("PRODUCT_ID", userDisInfo.getString("PRODUCT_ID"));
			elementInfo.put("PACKAGE_ID", userDisInfo.getString("PACKAGE_ID"));
			elementInfo.put("ELEMENT_ID", userDisInfo.getString("DISCNT_CODE"));
			elementInfo.put("ELEMENT_TYPE_CODE", "D");
			elementInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
			IDataset disAttrParamList =  new DatasetList();
			IData disAttrParam1 = new DataMap();
			disAttrParam1.put("ATTR_CODE",params.getData(i).getString("PARA_CODE3"));//本地资费attr_code
			disAttrParam1.put("ATTR_VALUE", getProAttrInfoByProductInfo(productInfo, params.getData(i).getString("PARA_CODE1")));
			disAttrParamList.add(disAttrParam1);
			elementInfo.put("ATTR_PARAM", disAttrParamList);
			elementInfos.add(elementInfo);
			productInfo.put("ELEMENT_INFO", elementInfos);
    	}
    
    }    
    /**
     * 根据全网产品属性ID获取产品属性值(通用)
     * @param productInfo
     * @param proAttrCode
     * @return
     * @throws Exception
     */
    public static String getProAttrInfoByProductInfo(IData productInfo,String proAttrCode) throws Exception{
    	String attrInfo = "";
    	IDataset productParams=productInfo.getDataset("PRODUCT_PARAM_INFO",new DatasetList());
    	if(IDataUtil.isEmpty(productParams)){
    		return attrInfo;
    	}  	
    	
    	for(int i=0;i<productParams.size();i++){
    		IData productParam=productParams.getData(i);
    		String productId=productParam.getString("PRODUCT_ID","");
    		String bbossProductId=GrpCommonBean.productToMerch(productId, 0);
    		String merchId = StaticUtil.getStaticValue("BBOSS_ISNEEDPROTODIS", productId); 
    		if(bbossProductId.equals(merchId)){//商品全网编码
    			IDataset productAttrs=productParam.getDataset("PRODUCT_PARAM");
    			for(int j=0;j<productAttrs.size();j++){
    				IData productAttr=productAttrs.getData(j);
    				String state=productAttr.getString("STATE");
    				if(("ADD".equals(state)||"MODI".equals(state))&&proAttrCode.equals(productAttr.getString("ATTR_CODE"))){
    					attrInfo=productAttr.getString("ATTR_VALUE");
    				}
    			}
    		}
    	}
    	
    	return attrInfo;
    }
    
}
