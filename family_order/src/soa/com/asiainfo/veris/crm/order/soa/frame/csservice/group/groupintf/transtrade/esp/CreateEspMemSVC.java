
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.groupintf.transtrade.esp;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.ProductException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UItemAInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commonmethod.GrpCommonBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderService;

public class CreateEspMemSVC extends GroupOrderService
{
	protected static final Logger log = Logger.getLogger(CreateEspMemSVC.class);
	
    private static final long serialVersionUID = 1L;
    protected static String memUserId = "";// 成员用户编号

    protected static String productId = "";// 省内产品编号

    protected static String productUserId = "";// 产品用户编号

    public final IDataset crtOrder(IData map) throws Exception
    {
    	//1-封装数据成基类需要的
    	 IDataset ret=new DatasetList();
    	 IData input=new DataMap();
    	 IData returnVal = new DataMap();
    	 returnVal = makeEspMebInfoData(map);
        // 2- 调用订单处理类进行处理
    	 if("00".equals(returnVal.getString("RESULT_CODE"))){
    		 CreateEspMemBean bean = new CreateEspMemBean(); 
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
    	if(log.isDebugEnabled()){
        	 log.debug("makeEspMebInfoData-map"+map);
          }
    	IData returnVal=new DataMap();
    	returnVal.put("RESULT_CODE","00");
    	returnVal.put("RESULT_INFO", "处理成功！");
    	//获取成员用户ID
    	memUserId = EspMebCommonBean.getMemberUserId(map);
    	if(StringUtils.isEmpty(memUserId)){
        	IData param = new DataMap();
            param.put("SUBSYS_CODE", "CSM");
            param.put("PARAM_ATTR", "1975");
            param.put("PARAM_CODE", map.getString("PRODUCT_NUMBER"));
            param.put("EPARCHY_CODE", "0898");
            IDataset params =  Dao.qryByCode("TD_S_COMMPARA", "SEL1_PK_TD_S_COMMPARA", param, Route.CONN_CRM_CEN);
    		if(null == params || params.isEmpty()){
	    		returnVal.put("RESULT_CODE", "03");
	        	returnVal.put("RESULT_INFO", "成员号码错误,成员用户信息不存在！");
	        	return returnVal;
    		}
    	}
    	//获取集团产品用户ID
    	productUserId=EspMebCommonBean.getProductUserId(map);
    	if(StringUtils.isEmpty(productUserId)){
    		returnVal.put("RESULT_CODE", "11");
        	returnVal.put("RESULT_INFO", "产品订购关系编码错误,未找到省内记录！");
        	return returnVal;
    	}
    	IData groupUserInfo=UserInfoQry.getGrpUserInfoByUserIdForGrp(productUserId,"0");
   	    if(IDataUtil.isEmpty(groupUserInfo)){
   	    	returnVal.put("RESULT_CODE", "99");
        	returnVal.put("RESULT_INFO", "集团用户资料不存在！");
        	return returnVal;	
        }
     	String productSpecCode = map.getString("PRODUCT_NUMBER");
    	productId = GrpCommonBean.merchToProduct(productSpecCode, 2 ,null);//查biz表的对应关系配置
    	
    	 // 检查成员和产品间的BB关系是否存在，存在需要抛出异常
        boolean isBBRelExist = false;
        if (!"".equals(memUserId))
        {
            isBBRelExist =EspMebCommonBean.isBBRelExist(productUserId, memUserId,productId);
        }
        if (isBBRelExist)
        {
        	 returnVal.put("RESULT_CODE", "98");
         	 returnVal.put("RESULT_INFO", "UU关系已存在,请勿重复添加成员！");
         	 return returnVal;
//             CSAppException.apperr(UUException.CRM_UU_88);
        }
        // 定义产品对象
        IData merchpInfo = new DataMap();

        // 3- 添加产品用户编号
        merchpInfo.put("USER_ID", productUserId);

        // 添加手机号
        String memSerialNumber = map.getString("SERIAL_NUMBER", "");
        merchpInfo.put("SERIAL_NUMBER", memSerialNumber);

        //添加角色编号(1:成员)
        merchpInfo.put("MEM_ROLE_B", "1");

        // 添加产品编号
        merchpInfo.put("PRODUCT_ID", productId);

        // 添加产品属性
        IDataset productParamInfoList = EspMebCommonBean.getProductParamInfo(map);
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

        // 添加产品信息
        IData productInfo = new DataMap();
        productInfo.put("MEB_TYPE", map.getString("MEMBER_TYPE_ID", ""));
        productInfo.put("EFF_DATE", map.getString("EFFDATE", ""));
        merchpInfo.put("PRODUCT_INFO", productInfo);

        // 添加反向受理标记(反向受理不发服务开通)
        merchpInfo.put("IN_MODE_CODE", "6");

        if(log.isDebugEnabled()){
       	 log.debug("makeEspMebInfoData-merchpInfo"+merchpInfo);
         }
        // 将产品对象添加至返回数据
        returnVal.put("ORDER_INFO", IDataUtil.idToIds(merchpInfo));
               
    	return returnVal;
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
         	 IDataset result=ProductInfoQry.getElementByProductIdElemId(memProductId,elementId);
         	if(log.isDebugEnabled()){
              	 log.debug("getElementInfo-EspelementId"+EspelementId+"---"+elementId);
              	 log.debug("getElementInfo-result"+result);
                }
         	 if(IDataUtil.isEmpty(result)){
         		CSAppException.apperr(ProductException.CRM_PRODUCT_1, memProductId+"|"+elementId); 
         	 }
         	 String elementTypeCode=result.getData(0).getString("ELEMENT_TYPE_CODE");
             // 1- 添加资费实例ID，受理时默认为""
         	 elementData.put("INST_ID", "");
   
             // 2- 添加元素类型，资费类型对应为"D"，服务类型对应为"S"
         	 elementData.put("ELEMENT_TYPE_CODE", elementTypeCode);
   
             // 3- 添加资费状态，受理时默认为"0"
         	 elementData.put("MODIFY_TAG", "0");
             // 4- 添加产品产品编号
         	 elementData.put("PRODUCT_ID", memProductId);
             // 5- 添加元素ID
         	 elementData.put("ELEMENT_ID", elementId);
   
             // 6- 添加包信息
         	 elementData.put("PACKAGE_ID", result.getData(0).getString("PACKAGE_ID"));
   
             // 9- 添加开始时间
         	 elementData.put("START_DATE", map.getString("EFF_DATE",SysDateMgr.getSysTime()));
   
             // 10- 添加结束时间
         	 elementData.put("END_DATE", SysDateMgr.getTheLastTime());
         	//获取资费参数(从传过来的参数中获取？)
          	IDataset elementAttrs=elementRates.getData(i).getDataset("RATE_PARAM");
          	if(log.isDebugEnabled()){
             	 log.debug("getElementInfo-elementAttrs"+elementAttrs);
               }
          	IDataset attrParams = new DatasetList();         	
          	if(IDataUtil.isNotEmpty(elementAttrs)){       	
          	 for(int a=0;a<elementAttrs.size();a++){
          		 IData attrParam = new DataMap();
          		 String attrCode=elementAttrs.getData(a).getString("PARAMETER_NUMBER");
          		 String attrValue=elementAttrs.getData(a).getString("PARAM_VALUE");          		
          		 IDataset attrInfos=UItemAInfoQry.queryOfferChaAndValByCond(elementId,elementTypeCode,"",attrCode);
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
    	 return elementInfoList;
    }
    

}
