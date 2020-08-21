
package com.asiainfo.veris.crm.order.soa.person.busi.plat.order.filter.wlan;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DESUtil;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.PlatException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.trans.AttrTrans;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.trans.OperCodeTrans;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterIn;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AttrTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PlatSvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.utils.PlatUtils;

/**
 * Wlan针对电子渠道的入参转换
 * 
 * @author bobo
 */
public class WlanEChannelFilter implements IFilterIn
{

    private static final String SERVICE_ID = "98002401"; // 大众WLAN服务

    // 换成新套餐后为00000
    private static String WLAN_FLOW_PKG_STRS = "00000_00001_00002_00003_00004_00005_00006_00007";// 流量套餐

    // 换成新套餐后为 00015_00016_00017_00018_00019_00020
    private static String WLAN_TIME_PKG_STRS = "00011_00012_00013_00015_00016_00017_00018_00019_00020_40003_40002_40004_40001";// 时长套餐

    private static String WLAN_GAS_PKG_STRS = "00014_00021_00022"; // 加油包

    @Override
    public void transferDataInput(IData input) throws Exception
    {
        OperCodeTrans.operCodeTrans(input);
        // String bizCode = input.getString("BIZ_CODE", "");
        IDataset attrs = new DatasetList();

        String infoCode = "";
        // 普通形式的info_code,info_value方式传属性
        String infoValue = AttrTrans.getInfoValue(input, "INFO_VALUE");
        String operCode = input.getString("OPER_CODE");

        // 特殊处理啊。 电渠道传的参数蛋疼，重置还传INFO_CODE,INFO_VALUE过来
        if (PlatConstants.OPER_RESET.equals(operCode))
        {
            input.remove("INFO_CODE");
            input.remove("INFO_VALUE");
        }

        if (StringUtils.isNotEmpty(infoValue) && "06_10_11_12_16".indexOf(operCode) > -1)
        {
            if (WLAN_FLOW_PKG_STRS.indexOf(infoValue) >= 0)
            {
                infoCode = "401";
                IData attrParam = new DataMap();
                attrParam.put("ATTR_CODE", "SEL_TYPE");
                attrParam.put("ATTR_VALUE", "1");
                attrs.add(attrParam);
            }
            if (WLAN_TIME_PKG_STRS.indexOf(infoValue) >= 0)
            {
                infoCode = "401_2";
                IData attrParam = new DataMap();
                attrParam.put("ATTR_CODE", "SEL_TYPE");
                attrParam.put("ATTR_VALUE", "2");
                attrs.add(attrParam);
            }
            if (WLAN_GAS_PKG_STRS.indexOf(infoValue) >= 0)
            {
                infoCode = "401_3";
            }

            // if (StringUtils.isBlank(infoCode))
            // {
            // CSAppException.apperr(PlatException.CRM_PLAT_0975_3);
            // }

            input.put("INFO_CODE", infoCode);
        }

        // 套餐用尽后通知,传的INFO_CODE可能为空
        if (PlatConstants.OPER_TC_USEROUT_NOTICE.equals(operCode))
        {
            String tcCode = input.getString("INFO_VALUE", "");
            if (WLAN_FLOW_PKG_STRS.indexOf(tcCode) >= 0)
            {
                infoCode = "401";
            }
            if (WLAN_TIME_PKG_STRS.indexOf(tcCode) >= 0)
            {
                infoCode = "401_2";
            }
            if (WLAN_GAS_PKG_STRS.indexOf(tcCode) >= 0)
            {
                infoCode = "401_3";
            }
            input.put("INFO_CODE", infoCode);
        }
        // 将属性转换成ATTR_PARAM的形式
        AttrTrans.trans(input);

        // 短厅发WLAN开通报文，需处理业务级别和密码
        if (PlatConstants.OPER_ORDER.equals(operCode))
        {
            String password = input.getString("PASSWD", PlatUtils.geneComplexRandomPassword()); // 接口有可能使用该字段传参数
            if (password.equals(""))
            {
                password = PlatUtils.geneComplexRandomPassword();
            }

            if (StringUtils.isBlank(AttrTrans.getInfoValue(input, "401")) && StringUtils.isBlank(AttrTrans.getInfoValue(input, "401_2")))
            {
                IData attrPackage = new DataMap();
                attrPackage.put("ATTR_CODE", "401");
                attrPackage.put("ATTR_VALUE", "00000");
                attrs.add(attrPackage);
            }

            if (StringUtils.isBlank(AttrTrans.getInfoValue(input, "001")))
            {
                IData attrBusiLevel = new DataMap();
                attrBusiLevel.put("ATTR_CODE", "001");
                attrBusiLevel.put("ATTR_VALUE", "10");
                attrs.add(attrBusiLevel);
            }

            if (StringUtils.isBlank(AttrTrans.getInfoValue(input, "AIOBS_PASSWORD")))
            {
                IData attrPasswd = new DataMap();
                attrPasswd.put("ATTR_CODE", "AIOBS_PASSWORD");
                attrPasswd.put("ATTR_VALUE", password);
                attrs.add(attrPasswd);
            }

            if (StringUtils.isBlank(AttrTrans.getInfoValue(input, "SEL_TYPE")))
            {
                IData selType = new DataMap();
                selType.put("ATTR_CODE", "SEL_TYPE");
                selType.put("ATTR_VALUE", "1");
                attrs.add(selType);
            }

        }
        // 如果是套餐订购，如果用户未注册，直接转换为用户注册
        if (PlatConstants.OPER_ORDER_TC.equals(operCode))
        {
            UcaData uca = UcaDataFactory.getNormalUca(input.getString("SERIAL_NUMBER"));
            if (uca == null)
            {
                CSAppException.apperr(PlatException.CRM_PLAT_1001_5);
            }

            // 如果用户没有订购，则修改为订购的操作
            List<PlatSvcTradeData> platSvcList = uca.getUserPlatSvcByServiceId(SERVICE_ID);
            if (platSvcList == null || platSvcList.isEmpty())
            {
                input.put("OPER_CODE", PlatConstants.OPER_ORDER);
                if (StringUtils.isBlank(AttrTrans.getInfoValue(input, "401")) && StringUtils.isBlank(AttrTrans.getInfoValue(input, "401_2")))
                {
                    IData attrPackage = new DataMap();
                    attrPackage.put("ATTR_CODE", "401");
                    attrPackage.put("ATTR_VALUE", "00000");
                    attrs.add(attrPackage);
                }

                if (StringUtils.isBlank(AttrTrans.getInfoValue(input, "001")))
                {
                    IData attrBusiLevel = new DataMap();
                    attrBusiLevel.put("ATTR_CODE", "001");
                    attrBusiLevel.put("ATTR_VALUE", "10");
                    attrs.add(attrBusiLevel);
                }

                if (StringUtils.isBlank(AttrTrans.getInfoValue(input, "AIOBS_PASSWORD")))
                {
                    IData attrPasswd = new DataMap();
                    attrPasswd.put("ATTR_CODE", "AIOBS_PASSWORD");
                    attrPasswd.put("ATTR_VALUE", PlatUtils.geneComplexRandomPassword());
                    attrs.add(attrPasswd);
                }

                if (StringUtils.isBlank(AttrTrans.getInfoValue(input, "SEL_TYPE")))
                {
                    IData selType = new DataMap();
                    selType.put("ATTR_CODE", "SEL_TYPE");
                    selType.put("ATTR_VALUE", "1");
                    attrs.add(selType);
                }
            }
            else
            {
                // 如果已经订购，则发套餐订购
                if (StringUtils.isBlank(AttrTrans.getInfoValue(input, "401")) && StringUtils.isBlank(AttrTrans.getInfoValue(input, "401_2"))
                		&& StringUtils.isBlank(AttrTrans.getInfoValue(input, "401_3")))
                {
                    CSAppException.apperr(PlatException.CRM_PLAT_0975_2);
                }
            }
        }

        // 密码修改
        if (PlatConstants.OPER_MODIFY_PASSWORD.equals(operCode))
        {
            String oldPassWord = input.getString("OLDPASSWD", input.getString("PASSWD"));
            String newPassWord = input.getString("NEW_PASSWD", input.getString("NEWPASSWD"));
            // 老密码
            if (StringUtils.isBlank(oldPassWord))
            {
                CSAppException.apperr(PlatException.CRM_PLAT_0975_10);
            }

            if (StringUtils.isBlank(newPassWord))
            {
                CSAppException.apperr(PlatException.CRM_PLAT_0975_10, "新密码不能为空");
            }

            IData attrOldPass = new DataMap();
            attrOldPass.put("ATTR_CODE", "OLD_PASSWORD");
            attrOldPass.put("ATTR_VALUE", oldPassWord);
            attrs.add(attrOldPass);
            IData attrNewPass = new DataMap();
            attrNewPass.put("ATTR_CODE", "AIOBS_PASSWORD");
            attrNewPass.put("ATTR_VALUE", newPassWord);
            attrs.add(attrNewPass);
        }

        if (PlatConstants.OPER_RESET.equals(operCode))
        {
            // 转化为密码修改
//            input.put("OPER_CODE", PlatConstants.OPER_MODIFY_PASSWORD);
//            IData attrNewPasswd = new DataMap();
//            attrNewPasswd.put("ATTR_CODE", "AIOBS_PASSWORD");
//            attrNewPasswd.put("ATTR_VALUE", PlatUtils.geneComplexRandomPassword());
//            attrs.add(attrNewPasswd);

            UcaData uca = UcaDataFactory.getNormalUca(input.getString("SERIAL_NUMBER"));
            if (uca == null)
            {
                CSAppException.apperr(PlatException.CRM_PLAT_1001_5);
            }

            List<PlatSvcTradeData> platSvcList = uca.getUserPlatSvcByServiceId(SERVICE_ID);
//            PlatSvcTradeData platSvc = null;
            if (platSvcList != null && !platSvcList.isEmpty())
            {
//                platSvc = platSvcList.get(0);
//                AttrTradeData attrData = uca.getUserAttrsByRelaInstIdAttrCode(platSvc.getInstId(), "AIOBS_PASSWORD");
//                if (attrData == null)
//                {
//                    CSAppException.apperr(PlatException.CRM_PLAT_0975_10);
//                }
//
//                IData attrOldPasswd = new DataMap();
//                attrOldPasswd.put("ATTR_CODE", "OLD_PASSWORD");
//                attrOldPasswd.put("ATTR_VALUE", DESUtil.decrypt(attrData.getAttrValue()));
//                attrs.add(attrOldPasswd);
            }
            else
            {

                CSAppException.apperr(PlatException.CRM_PLAT_0913);
            }

        }

//        // 退订套餐操作，将退订WLAN
//        if (operCode.equals(PlatConstants.OPER_CANCEL_TC))
//        {
//            input.put("OPER_CODE", "07");
//        }

        if (IDataUtil.isNotEmpty(attrs))
        {
            IDataset attrParams = input.getDataset("ATTR_PARAM");
            if (IDataUtil.isEmpty(attrParams))
            {
                input.put("ATTR_PARAM", attrs);
            }
            else
            {
                attrParams.addAll(attrs);
            }
        }

    }

}
