
package com.asiainfo.veris.crm.order.soa.person.rule.run.vip;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UVipClassInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustVipInfoQry;

/**
 * 检验是否存在VIP客户
 * 
 * @author liutt
 */
public class CheckExistsCustVip extends BreBase implements IBREScript
{

    @Override
    public boolean run(IData databus, BreRuleParam param) throws Exception
    {
        String xChoiceTag = databus.getString("X_CHOICE_TAG");
        if (StringUtils.isBlank(xChoiceTag) || "0".equals(xChoiceTag))// 查询号码时校验
        {
            String userId = databus.getString("USER_ID");
            IDataset custInfos = CustVipInfoQry.qryVipInfoByUserId(userId);
            if (IDataUtil.isNotEmpty(custInfos))
            {
                String vipClassId = custInfos.getData(0).getString("VIP_CLASS_ID", "");
                String vipTypeCode = custInfos.getData(0).getString("VIP_TYPE_CODE", "");
                if (StringUtils.isNotBlank(vipClassId) && StringUtils.isNotBlank(vipTypeCode))
                {
                    String vipClassName = UVipClassInfoQry.getVipClassNameByVipTypeCodeClassId(vipTypeCode, vipClassId);
                    if (StringUtils.isBlank(vipClassName))
                    {
                        vipClassName = "大客户";
                    }
                    databus.put("CUST_VIP_CLASS_NAME", vipClassName);
                    return true;
                }
            }
        }
        return false;
    }

}
