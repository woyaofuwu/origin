
package com.asiainfo.veris.crm.order.soa.person.rule.run.productchange;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.USvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.AttrItemInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserBankMainSignInfoQry;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @ClassName: CheckAutoRechargeSvcLimit.java
 * @Description: 校验易联自动充值(service_id=98)【TradeCheckBefore】
 * @version: v1.0.0
 * @author: maoke
 * @date: Jul 17, 2014 8:45:46 PM Modification History: Date Author Version Description
 *        -------------------------------------------------------* Jul 17, 2014 maoke v1.0.0 修改原因
 */
public class CheckAutoRechargeSvcLimit extends BreBase implements IBREScript
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

                        String elementTypeCode = element.getString("ELEMENT_TYPE_CODE");
                        String serviceId = element.getString("ELEMENT_ID");
                        String modifyTag = element.getString("MODIFY_TAG");

                        if (BofConst.ELEMENT_TYPE_CODE_SVC.equals(elementTypeCode) && "98".equals(serviceId) && (BofConst.MODIFY_TAG_ADD.equals(modifyTag) || BofConst.MODIFY_TAG_UPD.equals(modifyTag)))
                        {
                            String errorMsg = "";

                            String userId = databus.getString("USER_ID");
                            String serialNumber = databus.getString("SERIAL_NUMBER");
                            String serviceName = USvcInfoQry.getSvcNameBySvcId("98");

                            IDataset bankInfos = UserBankMainSignInfoQry.qryBankByPk(serialNumber, userId);
                            if (IDataUtil.isNotEmpty(bankInfos))
                            {
                                IDataset attrs = element.getDataset("ATTR_PARAM");

                                if (IDataUtil.isNotEmpty(attrs))
                                {
                                    for (int j = 0, attrSize = attrs.size(); j < attrSize; j++)
                                    {
                                        IData attr = attrs.getData(j);

                                        String attrCode = attr.getString("ATTR_CODE");
                                        String attrValue = attr.getString("ATTR_VALUE");

                                        IDataset attrItemB = AttrItemInfoQry.getAttrItemBInfoByFieldCode("98", BofConst.ELEMENT_TYPE_CODE_SVC, attrCode, attrValue, CSBizBean.getTradeEparchyCode());

                                        if (IDataUtil.isEmpty(attrItemB))
                                        {
                                            if ("V98FZ".equals(attrCode))
                                            {
                                                errorMsg = "#服务子台账判断:服务【98|" + serviceName + "】不存在易联自动充值阀值[" + attrValue + "]元！)";
                                            }
                                            if ("V98JE".equals(attrCode))
                                            {
                                                errorMsg = "#服务子台账判断:服务【98|" + serviceName + "】不存在易联自动充值金额[" + attrValue + "]元！)";
                                            }

                                            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "514398", errorMsg);

                                            return true;
                                        }
                                    }
                                }
                            }
                            else
                            {
                                errorMsg = "#服务子台账判断:服务【98|" + serviceName + "】不存在易联签约用户,请办理易联签约后重新办理！";
                                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "514398", errorMsg);

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
