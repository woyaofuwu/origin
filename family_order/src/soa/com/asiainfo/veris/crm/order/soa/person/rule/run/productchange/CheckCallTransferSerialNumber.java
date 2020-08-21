
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

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @ClassName: CheckCallTransferSerialNumber.java
 * @Description: 校验呼叫转移号码【TradeCheckBefore】
 * @version: v1.0.0
 * @author: maoke
 * @date: May 23, 2014 2:55:01 PM Modification History: Date Author Version Description
 *        -------------------------------------------------------* May 23, 2014 maoke v1.0.0 修改原因
 */
public class CheckCallTransferSerialNumber extends BreBase implements IBREScript
{
    @Override
    public boolean run(IData databus, BreRuleParam param) throws Exception
    {
        String errorMsg = "";
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

                        String elementTypeCode = element.getString("ELEMENT_TYPE_CODE");
                        String elementId = element.getString("ELEMENT_ID");
                        String modifyTag = element.getString("MODIFY_TAG");

                        if (BofConst.ELEMENT_TYPE_CODE_SVC.equals(elementTypeCode) && ("12".equals(elementId) || "3007".equals(elementId) || "3008".equals(elementId) || "3009".equals(elementId) || "3010".equals(elementId)))
                        {
                            IDataset attrs = element.getDataset("ATTR_PARAM");

                            if (IDataUtil.isNotEmpty(attrs))
                            {
                                for (int j = 0, atrrSize = attrs.size(); j < atrrSize; j++)
                                {
                                    IData attr = attrs.getData(j);

                                    String attrCode = attr.getString("ATTR_CODE");
                                    String attrValue = attr.getString("ATTR_VALUE");

                                    if (BofConst.MODIFY_TAG_ADD.equals(modifyTag) || BofConst.MODIFY_TAG_UPD.equals(modifyTag))
                                    {
                                        if (("V" + elementId + "V1").equals(attrCode) && !"12599".equals(attrValue))// 呼叫转移ATTR_CODE
                                        // 12599为核版本时添加
                                        {
                                            // 处理呼转号码
                                            if(StringUtils.isEmpty(attrValue))
                                            {
                                                errorMsg = "呼转号码不能为空!";

                                                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "110", errorMsg);

                                                return true;
                                            }
                                            
                                            if (!attrValue.matches("^[0-9]*$"))
                                            {
                                                errorMsg = "您输入的呼转号码【" + attrValue + "】格式错误，手机号码请直接填写，固定电话请按(区号+电话)格式输入!";

                                                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "110", errorMsg);

                                                return true;
                                            }
                                            if ("0".equals(attrValue.substring(0, 1)) || "1".equals(attrValue.substring(0, 1)))
                                            {
                                                if ("1".equals(attrValue.substring(0, 1)))// 校验手机号码
                                                {
                                                    if (attrValue.trim().length() < 11)
                                                    {
                                                        errorMsg = "您输入的呼转号码【" + attrValue + "】格式错误，手机号码请直接填写，固定电话请按(区号+电话)格式输入!";

                                                        BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 110, errorMsg);

                                                        return true;
                                                    }
                                                    else if ("13800898509".equals(attrValue.trim()) || "13800898309".equals(attrValue.trim()))
                                                    {
                                                        errorMsg = "您输入的呼转号码【" + attrValue + "】不能作为呼转对象!";

                                                        BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "110", errorMsg);

                                                        return true;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
}
