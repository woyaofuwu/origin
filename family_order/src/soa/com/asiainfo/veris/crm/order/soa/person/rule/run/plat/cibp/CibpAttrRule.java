
package com.asiainfo.veris.crm.order.soa.person.rule.run.plat.cibp;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.PlatException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.configdata.plat.PlatOfficeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.AttrData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.PlatSvcData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.utils.PlatUtils;

public class CibpAttrRule extends BreBase implements IBREScript
{

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        UcaData uca = (UcaData) databus.get(PlatConstants.RULE_UCA);
        PlatSvcData psd = (PlatSvcData) databus.get(PlatConstants.RULE_PLATSVC);
        PlatOfficeData officeData = psd.getOfficeData();
        List<AttrData> platSvcAttrList = psd.getAttrs(); // 页面提交的属性和属性值

        String operCode = psd.getOperCode();
        String bizCode = officeData.getBizCode();

        if ("20830000".equals(bizCode))
        {
            if ("06".equals(operCode))
            {
                if (StringUtils.isBlank(PlatUtils.getAttrValue("STB_ID", platSvcAttrList)))
                {
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, PlatException.CRM_PLAT_74.toString(), "机顶盒ID不能为空");
                    return false;
                }
                if (PlatUtils.getAttrValue("STB_ID", platSvcAttrList).length() < 15 || PlatUtils.getAttrValue("STBID", platSvcAttrList).length() > 32)
                {
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, PlatException.CRM_PLAT_74.toString(), "机顶盒ID长度为15-32位数字!");
                    return false;
                }
            }
            if ("08".equals(operCode))
            {
                if (StringUtils.isBlank(PlatUtils.getAttrValue("STB_ID", platSvcAttrList)))
                {
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, PlatException.CRM_PLAT_74.toString(), "机顶盒ID不能为空");
                    return false;
                }
                // if(StringUtils.isBlank(PlatUtils.getAttrValue("OLDSTBID", platSvcAttrList))){
                // BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, PlatException.CRM_PLAT_74.toString(),
                // "旧机顶盒ID不能为空");
                // return false;
                // }
                if (PlatUtils.getAttrValue("STB_ID", platSvcAttrList).length() < 15 || PlatUtils.getAttrValue("STBID", platSvcAttrList).length() > 32 || PlatUtils.getAttrValue("STBID", platSvcAttrList).length() < 15
                        || PlatUtils.getAttrValue("STBID", platSvcAttrList).length() > 32)
                {
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, PlatException.CRM_PLAT_74.toString(), "机顶盒ID长度为15-32位数字!");
                    return false;
                }
            }
        }
        return true;
    }

}
