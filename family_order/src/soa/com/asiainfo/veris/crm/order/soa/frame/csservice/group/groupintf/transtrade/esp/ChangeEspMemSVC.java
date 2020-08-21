
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
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderService;

public class ChangeEspMemSVC extends GroupOrderService
{
	protected static final Logger log = Logger.getLogger(ChangeEspMemSVC.class);
    private static final long serialVersionUID = 1L;

    IData productGoodInfos = new DataMap();// BBOSS侧的商产品信息
    protected static String memUserId = "";// 成员用户编号

    protected static String productId = "";// 省内产品编号

    protected static String productUserId = "";// 产品用户编号

    public final IDataset crtOrder(IData map) throws Exception
    {      
    	IDataset ret=new DatasetList();
        IData returnVal = new DataMap();
        IData input=new DataMap();
        returnVal = makeEspMebInfoData(map);
        if("00".equals(returnVal.getString("RESULT_CODE"))){
        ChangeEspMemBean bean = new ChangeEspMemBean();
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
    	IData merchpInfo = new DataMap();

        productUserId = EspMebCommonBean.getProductUserId(map);
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

        //获取成员用户信息，不存在抛出异常
        String memUserId = EspMebCommonBean.getMemberUserId(map);
        if(StringUtils.isEmpty(memUserId)){
    		returnVal.put("RESULT_CODE", "03");
        	returnVal.put("RESULT_INFO", "成员号码错误,成员用户信息不存在！");
        	return returnVal;
    	}
        String oSubTypeID = map.getString("ACTION", "");
     //  根据变更的不同类型拼产品数据
        if ("2".equals(oSubTypeID)|| "3".equals(oSubTypeID) || "4".equals(oSubTypeID))
        {// 变更成员类型、暂停成员、成员恢复
            makeChgMemDate(oSubTypeID, map, merchpInfo);
        }
        else if ("6".equals(oSubTypeID))
        {// 变更成员扩展属性、重置序列号
            makeChgMemAttrData(oSubTypeID, map, merchpInfo);

        }
        else
        {
        	returnVal.put("RESULT_CODE", "04");
        	returnVal.put("RESULT_INFO", "操作类型错误！");
        	return returnVal;
//            CSAppException.apperr(TradeException.CRM_TRADE_2002);
        }

       //将产品信息添加至返回结果集
        returnVal.put("ORDER_INFO", IDataUtil.idToIds(merchpInfo));
               
    	return returnVal;
    }
    /**
     * 拼装操作类型对应为变更成员类型、暂停成员、恢复成员的产品数据
     * @param oSubTypeID
     * @param map
     * @param merchpInfo
     * @throws Exception
     */
    protected static void makeChgMemDate(String oSubTypeID, IData map, IData merchpInfo) throws Exception
    {
        // 1- 添加产品用户编号
        merchpInfo.put("USER_ID", productUserId);

        // 2- 添加手机号码
        String memSerialNumber = map.getString("SERIAL_NUMBER", "");
        merchpInfo.put("SERIAL_NUMBER", memSerialNumber);

        // 3- 添加产品编号
        IData productUserInfo = UserInfoQry.getGrpUserInfoByUserId(productUserId, "0", Route.CONN_CRM_CG);
        merchpInfo.put("PRODUCT_ID", productUserInfo.getString("PRODUCT_ID"));

        // 4- 添加ESP侧产品信息
        IData productInfo = new DataMap();
        productInfo.put("MEB_TYPE", map.getString("MEMBER_TYPE_ID", ""));
        productInfo.put("MEB_OPER_CODE", oSubTypeID);
        productInfo.put("USER_ID", productUserId);
        merchpInfo.put("PRODUCT_INFO", productInfo);

        // 5- 添加反向受理标记(反向受理不发服务开通)
        merchpInfo.put("IN_MODE_CODE", "6");
    }
    /**
     * 拼装操作类型对应为变更成员扩展属性、重置序列号的产品数据
     * @param oSubTypeID
     * @param map
     * @param merchpInfo
     * @throws Exception
     */
    protected static void makeChgMemAttrData(String oSubTypeID, IData map, IData merchpInfo) throws Exception
    {
        // 1- 添加产品用户编号
        merchpInfo.put("USER_ID", productUserId);

        // 2- 添加手机号码
        String memSerialNumber = map.getString("SERIAL_NUMBER", "");
        merchpInfo.put("SERIAL_NUMBER", memSerialNumber);

        // 3- 添加产品编号
        IData productUserInfo = UserInfoQry.getGrpUserInfoByUserId(productUserId, "0", Route.CONN_CRM_CG);
        merchpInfo.put("PRODUCT_ID", productUserInfo.getString("PRODUCT_ID"));

        // 4- 添加扩展属性信息
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
        	elementInfo=EspMebCommonBean.getElementInfo(map,memProductId);
		}
       
        if (null == elementInfo || elementInfo.size() == 0)
        {
            merchpInfo.put("ELEMENT_INFO", "");
        }
        else
        {
            merchpInfo.put("ELEMENT_INFO", elementInfo);
        }

        // 4- 添加ESP侧产品信息
        IData productInfo = new DataMap();
        productInfo.put("MEB_TYPE", map.getString("USER_TYPE", ""));
        productInfo.put("MEB_OPER_CODE", oSubTypeID);
        productInfo.put("USER_ID", productUserId);
        merchpInfo.put("PRODUCT_INFO", productInfo);

        // 5- 添加反向受理标记(反向受理不发服务开通)
        merchpInfo.put("IN_MODE_CODE", "6");
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
            	   //1-新增记录
                   IData tmpData = new DataMap();
                   tmpData.put("ATTR_CODE", extendInfos.getData(i).getString("CHARACTER_ID"));
                   tmpData.put("ATTR_VALUE", extendInfos.getData(i).getString("CHARACTER_VALUE"));
                   tmpData.put("ATTR_NAME",extendInfos.getData(i).getString("CHARACTER_NAME"));
                   tmpData.put("STATE", GroupBaseConst.MEB_ATTR_STATUS_DESC.ATTR_ADD.getValue());
                   dsParam.add(tmpData);

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
   
       
}
