
package com.asiainfo.veris.crm.order.soa.person.rule.run.plat.common;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.PlatException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.configdata.plat.PlatOfficeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.AttrData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.PlatSvcData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.AttrItemInfoQry;

/**
 * 校验平台服务的 服务属性是否符合规则
 * 
 * @author xiekl
 */
public class PlatSvcAttrCheckRule extends com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase implements IBREScript
{

    /**
     * 根据操作类型 过滤属性
     * 
     * @param operCode
     * @param attrList
     * @return
     */
    private IDataset filterAttrByOperCode(String operCode, IDataset attrList)
    {
        IDataset filterAttrList = new DatasetList();
        if (attrList != null && attrList.size() > 0)
        {
            for (int i = 0; i < attrList.size(); i++)
            {
                IData attr = attrList.getData(i);
                if ((attr.getString("DISPLAY_CONDITION") == null || "".equals(attr.getString("DISPLAY_CONDITION"))) && "04_05_07".indexOf(operCode) == -1)
                {
                    filterAttrList.add(attr);
                }
                else if (attr.getString("DISPLAY_CONDITION").indexOf(operCode) != -1)
                {
                    filterAttrList.add(attr);
                }
            }
        }
        return filterAttrList;
    }

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        // TODO Auto-generated method stub
        UcaData uca = (UcaData) databus.get(PlatConstants.RULE_UCA);
        PlatSvcData psd = (PlatSvcData) databus.get(PlatConstants.RULE_PLATSVC);
        PlatOfficeData officeData = psd.getOfficeData();
        List<AttrData> platSvcAttrList = psd.getAttrs(); // 页面提交的属性和属性值
        IDataset operAttrConfigList = null; // 当前操作类型 所包含的属性
        boolean attrIsNull = true;
        IDataset attrConfigList = AttrItemInfoQry.getElementItemA4Plat(officeData.getServiceId(), "ZZZZ");
        operAttrConfigList = this.filterAttrByOperCode(psd.getOperCode(), attrConfigList);

        // 如果是普通业务
        if (!IDataUtil.isEmpty(operAttrConfigList) && !"6".equals(CSBizBean.getVisit().getInModeCode()))
        {
            for (int i = 0; i < operAttrConfigList.size(); i++)
            {
                IData attrConfig = operAttrConfigList.getData(i);
                if (!attrIsNull)
                {
                    attrIsNull = true;
                }

                if (attrConfig.getString("ATTR_CODE").equals("OLD_PASSWORD"))
                {
                    continue;
                }

                // 如果该条属性配置数据要求值为非空 ATTR_CAN_NULL为0，且提交的数据包含了该属性的非空数据
                if ("0".equals(attrConfig.getString("ATTR_CAN_NULL")))
                {
                    for (int j = 0; j < platSvcAttrList.size(); j++)
                    {
                        AttrData platSvcAttr = platSvcAttrList.get(j);
                        if (platSvcAttr.getAttrCode().equals(attrConfig.getString("ATTR_CODE")) && !StringUtils.isEmpty(platSvcAttr.getAttrValue()))
                        {
                            attrIsNull = false;
                        }
                    }
                    if (attrIsNull)
                    {
                        BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, PlatException.CRM_PLAT_74.toString(), "[" + psd.getOfficeData().getServiceName() + "]" + "业务的" + "[" + attrConfig.getString("ATTR_LABLE") + "]属性为必填值，请确认已经填写后提交");
                        return false;
                    }
                }

            }
        }

        return true;
    }

}
