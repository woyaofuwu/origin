
package com.asiainfo.veris.crm.order.web.group.grpimsutil;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;

public class GrpImsUtilView extends GroupBasePage
{
    /**
     * 判断是否需要创建成员
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static boolean getCreateMebFlag(IBizCommon bp, IData data) throws Exception
    {
        IData idata = CSViewCall.callone(bp, "SS.GroupImsUtilBeanSVC.getCreateMebFlag", data);
        if (IDataUtil.isNotEmpty(idata))
        {
            return idata.getBoolean("FLAG");
        }
        return true;
    }

    /**
     * 获取impu表扩展字段4字符串
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static String getImpuStr4(IBizCommon bp, String userId, String userType, int index, String eparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("USER_TYPE", userType);
        param.put("INDEX", index);
        param.put(Route.ROUTE_EPARCHY_CODE, eparchyCode);
        IData idata = CSViewCall.callone(bp, "SS.GroupImsUtilBeanSVC.getImpuStr4", param);
        if (IDataUtil.isNotEmpty(idata))
        {
            return idata.getString("SHORT_CODE");
        }
        return "";
    }

    public boolean checkImsShortCode(IBizCommon bp, IData data) throws Exception
    {
        String shortCode = data.getString("SHORT_CODE");
        if ("".equals(shortCode))
        {
            data.put("ERROR_MESSAGE", "短号码不能为空！");
            return false;
        }

        if (!"6".equals(shortCode.substring(0, 1)))
        {
            data.put("ERROR_MESSAGE", "短号码第一位必须为6，请重新选择短号！");
            return false;
        }

        if ("60".equals(shortCode.substring(0, 2)))
        {
            data.put("ERROR_MESSAGE", "短号码不能以60开头，请重新选择短号！");
            return false;
        }

        if ("61860".equals(shortCode))
        {
            data.put("ERROR_MESSAGE", "该短号与系统服务号码相同，请重新选择短号！");
            return false;
        }

        if (shortCode.length() > 6)
        {
            data.put("ERROR_MESSAGE", "该短号长度超过设定的短号长度6,请重新输入短号！");
            return false;
        }

        boolean flag = true;
        flag = selImsShortCode(bp, data);
        if (!flag)
        {
            data.put("ERROR_MESSAGE", "该短号已经被使用，请重新输入短号！");
        }

        return flag;
    }

    public boolean selImsShortCode(IBizCommon bp, IData data) throws Exception
    {
        String shortCode = data.getString("SHORT_CODE", "");
        String usecustId = data.getString("USER_ID_A", "");
        String eparchyCode = data.getString("EPARCHY_CODE");

        IData inparams = new DataMap();
        inparams.put("SHORT_CODE", shortCode);
        inparams.put("USER_ID_A", usecustId);
        inparams.put("EPARCHY_CODE", eparchyCode);
        inparams.put(Route.ROUTE_EPARCHY_CODE, eparchyCode);

        IDataset dataset = CSViewCall.call(bp, "CS.UserImpuInfoQrySVC.selShortCodeByUserId", inparams);
        if (IDataUtil.isNotEmpty(dataset))
        {
            return false;
        }
        return true;
    }
    
    public boolean checkImsVpnShortCode(IBizCommon bp, IData data) throws Exception
    {
        String shortCode = data.getString("SHORT_CODE");
        if ("".equals(shortCode))
        {
            data.put("ERROR_MESSAGE", "短号码不能为空！");
            return false;
        }

        if (!"6".equals(shortCode.substring(0, 1)))
        {
            data.put("ERROR_MESSAGE", "短号码第一位必须为6，请重新选择短号！");
            return false;
        }

        if ("60".equals(shortCode.substring(0, 2)))
        {
            data.put("ERROR_MESSAGE", "短号码不能以60开头，请重新选择短号！");
            return false;
        }

        if ("61860".equals(shortCode))
        {
            data.put("ERROR_MESSAGE", "该短号与系统服务号码相同，请重新选择短号！");
            return false;
        }

        if (shortCode.length() > 6)
        {
            data.put("ERROR_MESSAGE", "该短号长度超过设定的短号长度6,请重新输入短号！");
            return false;
        }

        boolean flag = true;
        flag = selImsVpnShortCode(bp, data);
        if (!flag)
        {
            data.put("ERROR_MESSAGE", "该短号已经被使用，请重新输入短号！");
        }

        return flag;
    }

    public boolean selImsVpnShortCode(IBizCommon bp, IData data) throws Exception
    {
        String shortCode = data.getString("SHORT_CODE", "");
        String usecustId = data.getString("USER_ID_A", "");
        String eparchyCode = data.getString("EPARCHY_CODE");

        IData inparams = new DataMap();
        inparams.put("SHORT_CODE", shortCode);
        inparams.put("USER_ID_A", usecustId);
        inparams.put("EPARCHY_CODE", eparchyCode);
        inparams.put(Route.ROUTE_EPARCHY_CODE, eparchyCode);

        IDataset dataset = CSViewCall.call(bp, "CS.UserImpuInfoQrySVC.selImsVpnShortCodeByUserId", inparams);
        if (IDataUtil.isNotEmpty(dataset))
        {
            return false;
        }
        return true;
    }
    
}
