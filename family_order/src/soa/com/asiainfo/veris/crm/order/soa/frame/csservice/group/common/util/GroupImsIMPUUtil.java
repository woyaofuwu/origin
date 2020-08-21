
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util;

import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

public class GroupImsIMPUUtil extends CSBizBean
{

    /**
     * 生成IMPI
     * 
     * @author tengg
     * @param serialNumber
     * @param strImpi
     * @param telType
     * @throws Exception
     */
    public static void genImsIMPI(String serialNumber, StringBuilder strImpi, String telType) throws Exception
    {

        String provice = getVisit().getProvinceCode();
        IDataset imsDomains = CommparaInfoQry.getCommparaAllCol("CGM", "9980", provice, "ZZZZ");
        String imsDomain = "";
        if (IDataUtil.isEmpty(imsDomains))
        {
            // imsDomain = "@ims." + getVisit().getProvinceCode() + ".chinamobile.com";
            imsDomain = "@ims.hi.chinamobile.com";
        }
        else
        {
            imsDomain = imsDomains.getData(0).getString("PARAM_NAME", "");
        }

        if ("1".equals(telType))
        { // 固定终端（SIP硬终端或POTS话机）
            strImpi.append(serialNumber).append(imsDomain);
        }
        if ("2".equals(telType))
        { // 无卡PC客户端
            strImpi.append(serialNumber).append("_s").append(imsDomain);
        }
        if ("3".equals(telType))
        { // 签约IMS的CS手机（如签约一号通）
            strImpi.append(serialNumber).append(imsDomain);
        }
    }

    /**
     * 生成IMPU
     * 
     * @author tengg
     * @param serialNumber
     * @param strTel
     * @param strSip
     * @param telType
     * @throws Exception
     */
    public static void genImsIMPU(String serialNumber, StringBuilder strTel, StringBuilder strSip, String telType) throws Exception
    {

        String provice = getVisit().getProvinceCode();
        IDataset imsDomains = CommparaInfoQry.getCommparaAllCol("CGM", "9980", provice, "ZZZZ");
        String imsDomain = "";
        if (IDataUtil.isEmpty(imsDomains))
        {
            // imsDomain = "@ims." + getVisit().getProvinceCode() + ".chinamobile.com";
            imsDomain = "@ims.hi.chinamobile.com";
        }
        else
        {
            imsDomain = imsDomains.getData(0).getString("PARAM_NAME", "");
        }

        if ("1".equals(telType))
        { // 固定终端（SIP硬终端或POTS话机）
            strTel.append(serialNumber);
            strSip.append(serialNumber).append(imsDomain);
        }
        if ("2".equals(telType))
        { // 无卡PC客户端
            strTel.append(serialNumber);
            strSip.append(serialNumber).append("_s").append(imsDomain);
        }
        if ("3".equals(telType))
        { // 签约IMS的CS手机（如签约一号通）
            strTel.append(serialNumber);
            strSip.append(serialNumber).append(imsDomain);
        }
    }

}
