
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.groupintf.transtrade.esp;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.group.common.GroupBaseConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UItemAInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commonmethod.GrpCommonBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.IntfField;

public class DestroyEspMemSVC extends GroupOrderService
{
    private static final long serialVersionUID = 1L;
    
    protected static String memUserId = "";// 成员用户编号

    protected static String productId = "";// 省内产品编号

    protected static String productUserId = "";// 产品用户编号
    protected static final Logger log = Logger.getLogger(DestroyEspMemSVC.class);

    public final IDataset dealEspMebBiz(IData map) throws Exception
    {
    	IDataset ret=new DatasetList();
    	IData input=new DataMap();
        IData returnVal = new DataMap();
        returnVal = makeEspMebInfoData(map);        
    	// 2- 调用订单处理类进行处理
        if("00".equals(returnVal.getString("RESULT_CODE"))){
        DestroyEspMemBean bean = new DestroyEspMemBean();
        bean.crtOrder(returnVal);
        }
        input.put("RESULT_CODE", returnVal.getString("RESULT_CODE"));
        input.put("RESULT_INFO", returnVal.getString("RESULT_INFO"));
        ret.add(input);
        return ret;
    }
    /**
     * 拼装产品数据
     * @param map
     * @return
     * @throws Exception
     */
    public static IData makeEspMebInfoData(IData map) throws Exception
    {
    	IData returnVal=new DataMap();
    	returnVal.put("RESULT_CODE","00");
    	returnVal.put("RESULT_INFO", "处理成功！");
    	//获取成员用户ID
    	memUserId = EspMebCommonBean.getMemberUserId(map);
    	if(StringUtils.isEmpty(memUserId)){
    		returnVal.put("RESULT_CODE", "03");
        	returnVal.put("RESULT_INFO", "成员号码错误,成员用户信息不存在！");
        	return returnVal;
    	}
    	//获取集团产品用户ID
    	productUserId=EspMebCommonBean.getProductUserId(map);
    	if(StringUtils.isEmpty(productUserId)){
    		returnVal.put("RESULT_CODE", "11");
        	returnVal.put("RESULT_INFO", "产品订购关系编码错误,未找到省内记录！");
        	return returnVal;
    	}
    	IData productUserInfo = UserInfoQry.getGrpUserInfoByUserId(productUserId, "0", Route.CONN_CRM_CG);
    	if(IDataUtil.isEmpty(productUserInfo)){
   	    	returnVal.put("RESULT_CODE", "99");
        	returnVal.put("RESULT_INFO", "集团用户资料不存在！");
        	return returnVal;	
        }
        productId = productUserInfo.getString("PRODUCT_ID");
    	
    	 // 检查成员和产品间的BB关系是否存在，存在需要抛出异常
        boolean isBBRelExist = false;
        if (!"".equals(memUserId))
        {
            isBBRelExist =EspMebCommonBean.isBBRelExist(productUserId, memUserId,productId);
        }
        if (!isBBRelExist)
        {
        	returnVal.put("RESULT_CODE", "99");
        	returnVal.put("RESULT_INFO", "操作重复:已经不存在用户关系不需要删除");
        	return returnVal;
//           CSAppException.apperr(UUException.CRM_UU_90);
        }
        // 定义产品对象
        IData merchpInfo = new DataMap();

        // 3- 添加产品用户编号
        merchpInfo.put("USER_ID", productUserId);

        // 添加手机号
        String memSerialNumber = map.getString("SERIAL_NUMBER", "");
        merchpInfo.put("SERIAL_NUMBER", memSerialNumber);

     // 添加产品属性
        IDataset productParamInfoList = getProductParamInfo(map);
        merchpInfo.put("PRODUCT_PARAM_INFO", productParamInfoList);
        IDataset elementInfo = new DatasetList();
        // 添加产品元素信息
        String memProductId =EspMebCommonBean.getMebElementInfo(productId);
        if (StringUtils.isNotBlank(memProductId))//配置了成员产品
        {
        	if(log.isDebugEnabled()){
           	 log.debug("进入获取资费信息");
             }
        	elementInfo=getElementInfo(map,memProductId);
		}
       
        if (null == elementInfo || elementInfo.size() == 0)
        {
            merchpInfo.put("ELEMENT_INFO", "");
        }
        else
        {
            merchpInfo.put("ELEMENT_INFO", elementInfo);
        }
        // 添加产品编号
        merchpInfo.put("PRODUCT_ID", productId);

        // 添加反向受理标记(反向受理不发服务开通)
        merchpInfo.put("IN_MODE_CODE", "6");
        
        merchpInfo.put("INTER_TYPE",map.getString(IntfField.ANTI_INTF_FLAG[0]));

        // 将产品对象添加至返回数据
        returnVal.put("ORDER_INFO", IDataUtil.idToIds(merchpInfo));
               
    	return returnVal;
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
        
        IDataset extendInfos=map.getDataset("EXTENDS");
        if(IDataUtil.isNotEmpty(extendInfos)){
        	   IDataset dsParam = new DatasetList();
               for (int i = 0; i<extendInfos.size();i++)
               {
                   // 2- 删除记录
                   IData tmpData1 = new DataMap();
                   tmpData1.put("ATTR_CODE", extendInfos.getData(i).getString("CHARACTER_ID"));
                   tmpData1.put("ATTR_VALUE",extendInfos.getData(i).getString("CHARACTER_VALUE"));
                   tmpData1.put("ATTR_NAME", extendInfos.getData(i).getString("CHARACTER_NAME"));
                   tmpData1.put("STATE", GroupBaseConst.MEB_ATTR_STATUS_DESC.ATTR_DEL.getValue());
                   dsParam.add(tmpData1);
               }
               IData productParamObj = new DataMap();
               productParamObj.put("PRODUCT_PARAM", dsParam);
               productParamObj.put("PRODUCT_ID", productId);// 成员基础产品编号
               productParamInfoList.add(productParamObj);
        }      

        // 2- 返回产品属性对象
        return productParamInfoList;
    }
    protected static IDataset getElementInfo(IData map, String memProductId) throws Exception
    {
    	IDataset elementInfoList = new DatasetList();
   	    IDataset elementRates=map.getDataset("MEMBER_RATE_PLAN");
   	    if(log.isDebugEnabled()){
          	 log.debug("getElementInfo-elementRates"+elementRates);
            }
   	    if(IDataUtil.isNotEmpty(elementRates)){
   	    	
         for(int i=0;i<elementRates.size();i++){   
        	 IData elementData=new DataMap();
        	 String EspelementId=elementRates.getData(i).getString("MEMBER_PATE_PLANID"); 
        	 String elementId=GrpCommonBean.merchToProduct(EspelementId, 1 ,memProductId);
        	 IDataset discntDataset = UserDiscntInfoQry.queryDiscntByUserIdAndDiscntCode(memUserId,elementId);
       	 if(IDataUtil.isNotEmpty(discntDataset)){
        		// 1- 添加资费实例ID，受理时默认为""
            	 elementData.put("INST_ID", discntDataset.getData(0).getString("INST_ID"));
      
                // 2- 添加元素类型，资费类型对应为"D"，服务类型对应为"S"
            	 elementData.put("ELEMENT_TYPE_CODE", "D");
      
                // 3- 添加资费状态，受理时默认为"0"
            	 elementData.put("MODIFY_TAG", "1");
                // 4- 添加产品产品编号
            	 elementData.put("PRODUCT_ID", discntDataset.getData(0).getString("PRODUCT_ID"));
                // 5- 添加元素ID
            	 elementData.put("ELEMENT_ID", elementId);
      
                // 6- 添加包信息
            	 elementData.put("PACKAGE_ID", discntDataset.getData(0).getString("PACKAGE_ID"));
      
                // 9- 添加开始时间
            	 elementData.put("START_DATE", discntDataset.getData(0).getString("START_DATE"));
      
                // 10- 添加结束时间
//            	 String startDate=discntDataset.getData(0).getString("START_DATE");
             	 String endDate=SysDateMgr.getLastDateThisMonth();
//             	 if(SysDateMgr.getTimeDiff(startDate,endDate,SysDateMgr.PATTERN_STAND)<0){
//             		endDate=startDate;
//             	 }
             	 elementData.put("END_DATE", endDate);
            	 
            	 IDataset discntAttrInfo=UserAttrInfoQry.getUserAttrForEsp(memUserId,discntDataset.getData(0).getString("INST_ID"));
            	//获取资费参数(从传过来的参数中获取？)
//             	IDataset elementAttrs=elementRates.getData(i).getDataset("RATE_PARAM");
             	if(log.isDebugEnabled()){
                	 log.debug("getElementInfo-discntAttrInfo"+discntAttrInfo);
                  }
             	IDataset attrParams = new DatasetList();         	
             	if(IDataUtil.isNotEmpty(discntAttrInfo)){          	 
             	 for(int a=0;a<discntAttrInfo.size();a++){   
             		 IData attrParam = new DataMap();
             		 String attrCode=discntAttrInfo.getData(a).getString("ATTR_CODE");
             		 String attrValue=discntAttrInfo.getData(a).getString("ATTR_VALUE");          		
             		IDataset attrInfos=UItemAInfoQry.queryOfferChaAndValByCond(elementId,"D", "", attrCode);
             		if(log.isDebugEnabled()){
                  	 log.debug("getElementInfo-attrCode"+attrCode+"---"+attrValue);
                  	 log.debug("getElementInfo-attrInfos"+attrInfos);
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
                 	 log.debug("getElementInfo-elementInfoList"+elementInfoList);
                }
            	} 
        	 }           
   	 }
   	 return elementInfoList;
    }
}
