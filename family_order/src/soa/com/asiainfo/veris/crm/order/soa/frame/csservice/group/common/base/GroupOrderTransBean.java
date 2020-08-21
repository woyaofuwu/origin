
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.BizException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.group.common.GroupBaseConst;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;

public class GroupOrderTransBean
{
    public static IData groupOrderTrans(UcaData uca, OrderData order) throws Exception
    {

        String productId = order.getCurrentProductId();
        IData productCtrlInfo = order.getProductCtrlInfo(productId);
        if (null == productCtrlInfo || productCtrlInfo.size() == 0)
        {
            return new DataMap();
        }
        String tradeTypeCode = productCtrlInfo.getData("TradeTypeCode").getString("ATTR_VALUE");
        order.setTradeTypeCode(tradeTypeCode);

        String ctrlClass = productCtrlInfo.getData("CreateClass").getString("ATTR_VALUE");

        IData data = (IData) invoker(ctrlClass, "createTrade", new Object[]
        { uca, order }, new Class[]
        { UcaData.class, OrderData.class });

        return data;
    }

    public static IData groupOrderTransNoProductCtrl(UcaData uca, OrderData order) throws Exception
    {

        String ctrlClass = "com.linkage.csserv.bean.group.creategroupacct.CreateGroupAcct";
        GroupOrderTransBean beam = new GroupOrderTransBean();
        IData data = (IData) beam.invoker(ctrlClass, "createTrade", new Object[]
        { uca, order }, new Class[]
        { UcaData.class, OrderData.class });
        return data;
    }

    private static Object invoker(String className, String methodName, Object[] objectGroup, Class[] classGroup) throws Exception
    {

        try
        {
            Class groupClass = Class.forName(GroupBaseConst.groupClassPath + className);

            Constructor cons = groupClass.getConstructor(new Class[]
            {});
            Object object = cons.newInstance(new Object[]
            {});

            Method method = groupClass.getMethod(methodName, classGroup);

            return method.invoke(object, objectGroup);
        }
        catch (Exception e)
        {
            if (e.getMessage() != null)
            {
                CSAppException.apperr(BizException.CRM_BIZ_5, e.getMessage().replace("@", ""));
            }
            else if (e.getCause() != null)
            {
                String errorMsg = e.getCause().toString();

                int indexA = errorMsg.lastIndexOf("#");

                if (indexA >= 0)
                {
                    CSAppException.apperr(BizException.CRM_BIZ_5, errorMsg.substring(indexA, errorMsg.length()));
                }

                int index = errorMsg.indexOf("@");

                if (index >= 0)
                {
                    CSAppException.apperr(BizException.CRM_BIZ_5, errorMsg.substring(errorMsg.indexOf("@"), errorMsg.length()));
                }

                throw e;
            }
            else
            {
                throw e;
            }
        }

        return null;
    }
}
