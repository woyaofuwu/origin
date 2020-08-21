
package com.asiainfo.veris.crm.order.soa.person.rule.run.plat.wlan;

import java.util.List;

import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.util.DESUtil;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.PlatException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.configdata.plat.PlatOfficeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.AttrData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.PlatSvcData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AttrTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PlatSvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.utils.PlatUtils;

public class WlanAttrRule extends BreBase implements IBREScript
{

    /**
	 * 
	 */
    private static final long serialVersionUID = 2835160491976381792L;

    // 00000_00015_00016_00017_00018_00019_00020_00014_00021_00022 新的流量套餐
    public static final String WLAN_PACKAGE_STR = "00000_00015_00016_00017_00018_00019_00020_00014_00021_00022_40001_40002_40003_40004";

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        UcaData uca = (UcaData) databus.get(PlatConstants.RULE_UCA);
        PlatSvcData psd = (PlatSvcData) databus.get(PlatConstants.RULE_PLATSVC);
        PlatOfficeData officeData = psd.getOfficeData();
        List<AttrData> platSvcAttrList = psd.getAttrs(); // 页面提交的属性和属性值
        String instId = "";
        List<PlatSvcTradeData> pstds = uca.getUserPlatSvcByServiceId(psd.getElementId());
        PlatSvcTradeData pstd = null;
        if (pstds != null && pstds.size() > 0)
        {
            pstd = pstds.get(0);
            instId = pstd.getInstId();
        }

        // 如果是WLAN
        if (PlatConstants.PLAT_WLAN.equals(officeData.getBizTypeCode()) || "92".equals(officeData.getBizTypeCode()))
        {
            // 密码判断
            if (PlatConstants.OPER_MODIFY_PASSWORD.equals(psd.getOperCode()))
            {
                AttrTradeData passwordAttr = uca.getUserAttrsByRelaInstIdAttrCode(instId, "AIOBS_PASSWORD");
//                String oldPassword = PlatUtils.getAttrValue("OLD_PASSWORD", platSvcAttrList);
                String newPassword = PlatUtils.getAttrValue("AIOBS_PASSWORD", platSvcAttrList);

                if (passwordAttr == null)
                {
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, PlatException.CRM_PLAT_74.toString(), "原用户资料不存在WLAN密码属性");
                    return false;
                }
                String userPassword = passwordAttr.getAttrValue();

                if (PlatConstants.IS_ENCRYPT_PASSWORD)
                {
                    userPassword = DESUtil.decrypt(userPassword);// 需要对密码解密 进行比较
                }
//                if (!oldPassword.equals(userPassword))
//                {
//                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, PlatException.CRM_PLAT_0975_12.toString(), PlatException.CRM_PLAT_0975_12.getValue());
//                }
                if (StringUtils.isBlank(newPassword))
                {
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, PlatException.CRM_PLAT_74.toString(), "用户新密码不能为空");
                    return false;
                }
                if (!PlatUtils.checkIsNumberAndChar(newPassword) || newPassword.length() < 8 || newPassword.length() > 18)
                {
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, PlatException.CRM_PLAT_0975_13.toString(), "WLAN密码只能是8-18位的数字或字母");
                    return false;
                } 
             /*   if (!PlatUtils.checkIsNumberAndChar(newPassword) || newPassword.length() < 8 || newPassword.length() > 18)
                {
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, PlatException.CRM_PLAT_74.toString(), "WLAN密码只能是8-18位的数字或字母");
                    return false;
                }*/
                if (PlatUtils.isSimplePwd(newPassword))
                {
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, PlatException.CRM_PLAT_0975_13.toString(), PlatException.CRM_PLAT_0975_13.getValue());
                    return false;
                }

//                if (oldPassword.equals(newPassword))
//                {
//                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, PlatException.CRM_PLAT_0975_11.toString(), PlatException.CRM_PLAT_0975_11.getValue());
//                    return false;
//                }

//                psd.setAttrs(PlatUtils.removeAttr("OLD_PASSWORD", platSvcAttrList));
            }
            else if (PlatConstants.OPER_ORDER.equals(psd.getOperCode()))
            {
                String newPassword = PlatUtils.getAttrValue("AIOBS_PASSWORD", platSvcAttrList);
                if (PlatUtils.isSimplePwd(newPassword))
                {
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, PlatException.CRM_PLAT_0975_13.toString(), PlatException.CRM_PLAT_0975_13.getValue());
                    return false;
                }

                if (!PlatUtils.checkIsNumberAndChar(newPassword))
                {
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, PlatException.CRM_PLAT_74.toString(), "WLAN密码只能是数字与字母组合");
                    return false;
                }

            }

            // 业务级别判断
            if ("06_08".indexOf(psd.getOperCode()) >= 0 && PlatConstants.PLAT_WLAN.equals(officeData.getBizTypeCode()))
            {
                String busiLevel = PlatUtils.getAttrValue("001", platSvcAttrList);
                if (StringUtils.isBlank(busiLevel) || "10_11".indexOf(busiLevel) < 0)
                {
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, PlatException.CRM_PLAT_0975_14.toString(), PlatException.CRM_PLAT_0975_14.getValue() + busiLevel);
                    return false;
                }
            }

            if (PlatConstants.OPER_USER_DATA_MODIFY.equals(psd.getOperCode()))
            {
                String busiLevel = PlatUtils.getAttrValue("001", platSvcAttrList);

                AttrTradeData attrTradeData = uca.getUserAttrsByRelaInstIdAttrCode(instId, "001");

                if (busiLevel.equals(attrTradeData.getAttrValue()))
                {
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, PlatException.CRM_PLAT_74.toString(), "业务级别没有变更,请选择要变更的业务级别");
                    return false;
                }
            }

            if (PlatConstants.OPER_RESTORE.equals(psd.getOperCode()))
            {
                if (pstd != null)
                {
                    if ("F".equals(pstd.getRemark()))
                    {
                        BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, PlatException.CRM_PLAT_74.toString(), "不能主动恢复前次操作为封顶暂停的操作");
                        return false;
                    }
                }
            }

            // 如果是套餐订购 退订 变更，对套餐的有效性判断
            if ("06_10_11_12".indexOf(psd.getOperCode()) >= 0 && PlatConstants.PLAT_WLAN.equals(officeData.getBizTypeCode()))
            {
                String attr_401 = PlatUtils.getAttrValue("401", platSvcAttrList);
                String attr_401_2 = PlatUtils.getAttrValue("401_2", platSvcAttrList);
                String selType = PlatUtils.getAttrValue("SEL_TYPE", platSvcAttrList);

                if ("1".equals(selType))
                {
                    if ("10".equals(psd.getOperCode()))
                    {
                        if ("00000".equals(attr_401))
                        {
                            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, PlatException.CRM_PLAT_74.toString(), "转换为标准套餐不能通过套餐订购操作，请通过退订原套餐操作");
                            return false;
                        }
                    }

                    if ("".equals(attr_401))
                    {
                        BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, PlatException.CRM_PLAT_74.toString(), "时长套餐不能为空");
                        return false;
                    }
                    else
                    {
                        if ((WLAN_PACKAGE_STR.indexOf(attr_401) < 0) && ("06_10_12".indexOf(psd.getOperCode()) >= 0))
                        {
                            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, PlatException.CRM_PLAT_74.toString(), "套餐编码参数错误(只能受理新流量套餐)");
                            return false;
                        }
                    }

                }

                if ("2".equals(selType))
                {
                    if ("".equals(attr_401_2))
                    {
                        BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, PlatException.CRM_PLAT_74.toString(), "流量套餐不能为空");
                        return false;
                    }
                    else
                    {
                        if ((WLAN_PACKAGE_STR.indexOf(attr_401_2) < 0) && ("06_10_12".indexOf(psd.getOperCode()) >= 0))
                        {
                            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, PlatException.CRM_PLAT_74.toString(), "套餐编码参数错误(只能受理新流量套餐)");
                            return false;
                        }
                    }

                }

            }
            
            //订购任我用套餐，对用户的主套餐档次进行校验
            if("06_10_12".indexOf(psd.getOperCode()) >= 0 && PlatConstants.PLAT_WLAN.equals(officeData.getBizTypeCode())){//订购任我用资费对于主套餐档次的限制
                String attr_401_2 = PlatUtils.getAttrValue("401_2", platSvcAttrList);
                if(!StringUtils.isEmpty(attr_401_2)&&"40001_40002_40003_40004".indexOf(attr_401_2)>=0){
                    List<DiscntTradeData> userDiscnts = uca.getUserDiscnts();
                    boolean exitsDiscnt = false;
                    for(DiscntTradeData userDiscnt : userDiscnts){
                    	String discntCode = userDiscnt.getDiscntCode();
                        if("40001".equals(attr_401_2)){
                        	IDataset attrConfigs = CommparaInfoQry.getCommNetInfo("CSM", "2780", discntCode);
                        	if(IDataUtil.isNotEmpty(attrConfigs) && "0".equals(attrConfigs.first().getString("PARA_CODE1").trim())){
                        		exitsDiscnt = true;
                        		break;
                        	}
                        }else if("40002".equals(attr_401_2)){
                        	IDataset attrConfigs = CommparaInfoQry.getCommNetInfo("CSM", "2780", discntCode);
                        	if(IDataUtil.isNotEmpty(attrConfigs) && "10".equals(attrConfigs.first().getString("PARA_CODE1").trim())){
                        		exitsDiscnt = true;
                        		break;
                        	}
                        }else if("40003".equals(attr_401_2)){
                        	IDataset attrConfigs = CommparaInfoQry.getCommNetInfo("CSM", "2780", discntCode);
                        	if(IDataUtil.isNotEmpty(attrConfigs) && "1".equals(attrConfigs.first().getString("PARA_CODE1").trim())){
                        		exitsDiscnt = true;
                        		break;
                        	}
                        }else if("40004".equals(attr_401_2)){
                        	IDataset attrConfigs = CommparaInfoQry.getCommNetInfo("CSM", "2780", discntCode);
                        	if(IDataUtil.isNotEmpty(attrConfigs) && "5".equals(attrConfigs.first().getString("PARA_CODE1").trim())){
                        		exitsDiscnt = true;
                        		break;
                        	}
                        }
                    }
                    
                    if(!exitsDiscnt){
                        BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, PlatException.CRM_PLAT_74.toString(), "用户的主套餐档次不能订购此WLAN任我用套餐");
                        return false;	
                    }
                }  
            }
        }

        return true;
    }

}
