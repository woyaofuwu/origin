
package com.asiainfo.veris.crm.order.soa.person.rule.run.productchange;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @ClassName: ShortSnSmsFreeDiscntLimit.java
 * @Description: 优惠 21401 特殊校验
 * @version: v1.0.0
 * @author: maoke
 * @date: Aug 21, 2014 8:36:57 PM Modification History: Date Author Version Description
 *        -------------------------------------------------------* Aug 21, 2014 maoke v1.0.0 修改原因
 */
public class ShortSnSmsFreeDiscntLimit extends BreBase implements IBREScript
{
    @Override
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        String xChoiceTag = databus.getString("X_CHOICE_TAG");

        if (StringUtils.isBlank(xChoiceTag) || StringUtils.equals("1", xChoiceTag))// 提交时校验，依赖请求数据
        {
            IData reqData = databus.getData("REQDATA");// 请求的数据

            if (IDataUtil.isNotEmpty(reqData))
            {
                IDataset selectedElements = new DatasetList(reqData.getString("SELECTED_ELEMENTS"));

                if (IDataUtil.isNotEmpty(selectedElements))
                {
                    for (int i = 0, size = selectedElements.size(); i < size; i++)
                    {
                        IData element = selectedElements.getData(i);

                        String elementId = element.getString("ELEMENT_ID");
                        String elementTypeCode = element.getString("ELEMENT_TYPE_CODE");
                        String modifyTag = element.getString("MODIFY_TAG");

                        if (BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(elementTypeCode) && "21401".equals(elementId) && BofConst.MODIFY_TAG_ADD.equals(modifyTag))
                        {
                            String serialNumber = databus.getString("SERIAL_NUMBER");

                            IDataset vpmnSpecInfos = UserInfoQry.getSpecVpmnUserInfoBySn(serialNumber, "000001", "1");

                            if (IDataUtil.isEmpty(vpmnSpecInfos))
                            {
                                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "21401", "该号码【" + serialNumber + "】不是最近连续三个月（不含当月）未使用过短号短信的VPMN用户,不能办理短号短信免费体验套餐!");

                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
}
