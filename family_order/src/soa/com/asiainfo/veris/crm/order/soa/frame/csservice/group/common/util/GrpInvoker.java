
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util;

import java.lang.reflect.Constructor;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.BizException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.InModeCodeUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bizctrl.BizCtrlBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bizctrl.BizCtrlInfo;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.exception.impl.BofException;

public final class GrpInvoker
{

    public static Object invoker(String className, String functionName, Object[] objectGroup, Class[] classGroup) throws Exception
    {

        try
        {
            Class groupClass = Class.forName(className);
            Constructor cons = groupClass.getConstructor(new Class[]
            {});
            Object object = cons.newInstance(new Object[]
            {});
            java.lang.reflect.Method method = groupClass.getMethod(functionName, classGroup);

            return method.invoke(object, objectGroup);
        }
        catch (Exception e)
        {
            if (e.getMessage() != null)
            {
                String errorMsg = e.getMessage().toString();

                CSAppException.apperr(BizException.CRM_BIZ_5, errorMsg.replace("@", ""));
            }
            throw e;
        }
    }

    /**
     * @param ivkParam
     * @param ctrlType
     * @param ivkClass
     * @return
     * @throws Exception
     */
    public static IDataset ivkProduct(IData ivkParam, String ctrlType, String ivkClass) throws Exception
    {

        String inModeCode = CSBizBean.getVisit().getInModeCode();

        boolean isNotSale = InModeCodeUtil.isNotSale(inModeCode);// 非营业前台和客服

        String productId = ivkParam.getString("PRODUCT_ID", "");

        IData userInfos = null;
        // 如果产品id为空
        if (StringUtils.isBlank(productId))
        {
            String userId = ivkParam.getString("USER_ID", "");

            if (StringUtils.isNotBlank(userId))
            {
                // 根据userId查询
                userInfos = UcaInfoQry.qryUserMainProdInfoByUserIdForGrp(userId);
                if (IDataUtil.isEmpty(userInfos))
                {
                    CSAppException.apperr(BofException.CRM_BOF_011);
                }
                productId = userInfos.getString("PRODUCT_ID");

            }
            else
            {
                // 根据sn查询productId
                String sn = ivkParam.getString("SERIAL_NUMBER", "");

                if (StringUtils.isNotBlank(sn))
                {
                    userInfos = UcaInfoQry.qryUserMainProdInfoBySn(sn);
                    if (IDataUtil.isEmpty(userInfos))
                    {
                        CSAppException.apperr(BofException.CRM_BOF_002);
                    }
                    productId = userInfos.getString("PRODUCT_ID");

                }
            }

            ivkParam.put("CUST_ID", userInfos.getString("CUST_ID"));// 集团客户标识
        }

        ivkParam.put("PRODUCT_ID", productId);

        // 获取处理类名称
        BizCtrlInfo ctrlInfo = BizCtrlBean.qryProductCtrlInfo(productId, ctrlType);

        String if_centretype = ivkParam.getString("IF_CENTRETYPE", ""); // 融合V网标识
        if (productId.equals("8000") && if_centretype.equals("2")) // 融合V网
        {
            ivkClass = "CenCreateClass";
        }
        String ctrlClass = ctrlInfo.getAttrValue(ivkClass);

        // 获取业务类型
        String tradeTypeCode = ctrlInfo.getTradeTypeCode();
        ivkParam.put("TRADE_TYPE_CODE", tradeTypeCode);


		        // 集客大厅特殊产品配置参数
        if(ctrlInfo.getCtrlInfo().containsKey("JKDTCreateClass")){
        	  ctrlClass = ctrlInfo.getAttrValue("JKDTCreateClass");
        	  tradeTypeCode = ctrlInfo.getAttrValue("JKDTTradeTypeCode");
        	  ivkParam.put("TRADE_TYPE_CODE", tradeTypeCode);
        }


        // 获取规则配置
        /*
         * String validateCtrlClass = ctrlInfo.getAttrValue("Validate"); String
         * validateMethod=ctrlInfo.getAttrStr1Value("Validate");
         */

        /*
         * //规则校验,前台和客服不需要校验 if(StringUtils.isNotEmpty(validateCtrlClass) && StringUtils.isNotEmpty(validateMethod)){
         * invoker(validateCtrlClass,validateMethod,new Object[]{ivkParam }, new Class[]{IData.class }); }
         */

        // 反射处理(子)类
        Object obj = invoker(ctrlClass, "crtTrade", new Object[]
        { ivkParam }, new Class[]
        { IData.class });

        return (IDataset) obj;
    }
}
