
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
 * WLan针对一级boss的输入转换
 * 
 * @author xiekl
 */
public class WlanIbossFilter implements IFilterIn
{ 

    private static final String SERVICE_ID = "98002401"; // 大众WLAN服务

    // 换成新套餐后为00000
    private static String WLAN_FLOW_PKG_STRS = "00000_00001_00002_00003_00004_00005_00006_00007";// 流量套餐

    // 换成新套餐后为 00015_00016_00017_00018_00019_00020
    private static String WLAN_TIME_PKG_STRS = "00011_00012_00013_00014_00015_00016_00017_00018_00019_00020_40001_40002_40003_40004";// 时长套餐

    private static String WLAN_GAS_PKG_STRS = "00014_00021_00022"; // 加油包

    @Override
    public void transferDataInput(IData input) throws Exception
    {
    	//二次确认回复，不需要走filter
    	String isConfirm = input.getString("IS_CONFIRM");
    	if("true".equals(isConfirm))
    	{
    		return ;
    	}
    	
        OperCodeTrans.operCodeTrans(input);
        String bizCode = input.getString("BIZ_CODE", "");
        IDataset attrs = new DatasetList();

        // 套餐编码可能放到bizCode中
        String infoCode = "";
        if (!"".equals(bizCode) && !"REG_SP".equals(bizCode))
        {

            if (WLAN_FLOW_PKG_STRS.indexOf(bizCode) >= 0)
            {
                infoCode = "401";
            }
            if (WLAN_TIME_PKG_STRS.indexOf(bizCode) >= 0)
            {
                infoCode = "401_2";
            }
            if (WLAN_GAS_PKG_STRS.indexOf(bizCode) >= 0)
            {
                infoCode = "401_3";
            }

            if ("".equals(infoCode))
            {
                CSAppException.apperr(PlatException.CRM_PLAT_0975_3);
            }
            input.put("INFO_CODE", infoCode);
            input.put("INFO_VALUE", bizCode);
        }
        AttrTrans.trans(input);

        // 普通形式的info_code,info_value方式传属性
        String infoValue = AttrTrans.getInfoValue(input, "401");
        int icount = 0;
        if (infoValue != null)
        {
            icount++;
            if (WLAN_FLOW_PKG_STRS.indexOf(infoValue) < 0)
            {
                CSAppException.apperr(PlatException.CRM_PLAT_0975_3);
            }

            IData attrParam = new DataMap();
            attrParam.put("ATTR_CODE", "SEL_TYPE");
            attrParam.put("ATTR_VALUE", "1");
            attrs.add(attrParam);
        }

        infoValue = AttrTrans.getInfoValue(input, "401_2");
        if (infoValue != null)
        {
            icount++;
            if (WLAN_TIME_PKG_STRS.indexOf(infoValue) < 0)
            {
                CSAppException.apperr(PlatException.CRM_PLAT_0975_3);
            }

            IData attrParam = new DataMap();
            attrParam.put("ATTR_CODE", "SEL_TYPE");
            attrParam.put("ATTR_VALUE", "2");
            attrs.add(attrParam);
        }
        infoValue = AttrTrans.getInfoValue(input, "401_3");
        if (infoValue != null)
        {
            icount++;
            if (WLAN_GAS_PKG_STRS.indexOf(infoValue) < 0)
            {
                CSAppException.apperr(PlatException.CRM_PLAT_0975_3);
            }
        }
        if (icount > 1)
        {
            CSAppException.apperr(PlatException.CRM_PLAT_74, "不能同时受理两个以上套餐");
        }

        input.put("SP_CODE", "REG_SP"); // 取默认值
        input.put("BIZ_CODE", "REG_SP"); // 取默认值

        String operCode = input.getString("OPER_CODE");

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
            input.put("OPER_CODE", PlatConstants.OPER_MODIFY_PASSWORD);
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
            PlatSvcTradeData platSvc = null;
            if (platSvcList != null && !platSvcList.isEmpty())
            {
                platSvc = platSvcList.get(0);
//                AttrTradeData attrData = uca.getUserAttrsByRelaInstIdAttrCode(platSvc.getInstId(), "AIOBS_PASSWORD");
//                if (attrData == null)
//                {
//                    CSAppException.apperr(PlatException.CRM_PLAT_0975_10);
//                }

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

        // 如果是套餐订购
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
                    attrPasswd.put("ATTR_VALUE", input.getString("PASSWD", PlatUtils.geneComplexRandomPassword()));
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
                if (StringUtils.isBlank(AttrTrans.getInfoValue(input, "401")) && StringUtils.isBlank(AttrTrans.getInfoValue(input, "401_2")))
                {
                    CSAppException.apperr(PlatException.CRM_PLAT_0975_2);
                }
            }
        }

        if (PlatConstants.OPER_CHANGE_TC.equals(operCode))
        {
            if (StringUtils.isBlank(AttrTrans.getInfoValue(input, "401")) && StringUtils.isBlank(AttrTrans.getInfoValue(input, "401_2")))
            {
                CSAppException.apperr(PlatException.CRM_PLAT_0975_2);
            }
        }
        // 如果是套餐退订，传的套餐编码为00000，则注销
        if (PlatConstants.OPER_CANCEL_TC.equals(operCode))
        {
            if ("00000".equals(AttrTrans.getInfoValue(input, "401")))
            {
                input.put("OPER_CODE", PlatConstants.OPER_CANCEL_ORDER);
            }
        }

        // 一级WAP订购WLAN开通报文，需处理业务级别和密码
        if (PlatConstants.OPER_ORDER.equals(operCode))
        {
            IData attrPackage = new DataMap();
            attrPackage.put("ATTR_CODE", "401");
            attrPackage.put("ATTR_VALUE", "00000");
            attrs.add(attrPackage);

            IData attrPasswd = new DataMap();
            attrPasswd.put("ATTR_CODE", "AIOBS_PASSWORD");
            attrPasswd.put("ATTR_VALUE", input.getString("PASSWD", PlatUtils.geneComplexRandomPassword()));
            attrs.add(attrPasswd);

            IData attrBusiLevel = new DataMap();
            attrBusiLevel.put("ATTR_CODE", "001");
            attrBusiLevel.put("ATTR_VALUE", "10");
            attrs.add(attrBusiLevel);

            if (StringUtils.isBlank(AttrTrans.getInfoValue(input, "SEL_TYPE")))
            {
                IData selType = new DataMap();
                selType.put("ATTR_CODE", "SEL_TYPE");
                selType.put("ATTR_VALUE", "1");
                attrs.add(selType);
            }
        }

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
