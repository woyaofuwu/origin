
package com.asiainfo.veris.crm.order.soa.person.rule.run.productchange;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.consts.PersonConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustVipInfoQry;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @ClassName: CheckIntfGlobeRomingLimit.java
 * @Description: 校验办理国际漫游规则、接口使用【CheckTradeBefore】
 * @version: v1.0.0
 * @author: maoke
 * @date: Jun 18, 2014 2:24:13 PM Modification History: Date Author Version Description
 *        -------------------------------------------------------* Jun 18, 2014 maoke v1.0.0 修改原因
 */
public class CheckIntfGlobeRomingLimit extends BreBase implements IBREScript
{

    @Override
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        // 判断用户是否符合通过接口办理国际长途、漫游业务条件
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

                        if (BofConst.ELEMENT_TYPE_CODE_SVC.equals(elementTypeCode) && BofConst.MODIFY_TAG_ADD.equals(modifyTag))
                        {
                            if (PersonConst.SERVICE_ID_19.equals(elementId))
                            {
                                String errorMsg = "";

                                String brandCode = databus.getString("BRAND_CODE");
                                String custId = databus.getString("CUST_ID");

                                IDataset custVipInfos = CustVipInfoQry.qryVipInfoByCustId(custId);

                                if (!"G001".equals(brandCode) && IDataUtil.isNotEmpty(custVipInfos))
                                {
                                    String vipClassId = custVipInfos.getData(0).getString("VIP_CLASS_ID", "");
                                    if (!"1".equals(vipClassId) && !"2".equals(vipClassId) && !"3".equals(vipClassId) && !"4".equals(vipClassId))
                                    {
                                        errorMsg = "全球通非钻金银贵卡客户只能在营业厅开通国际长途、漫游服务！";
                                    }
                                }
                                else
                                {
                                    errorMsg = "非全球通客户只能在营业厅开通国际长途、漫游服务！";
                                }

                                if (StringUtils.isNotBlank(errorMsg))
                                {
                                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "500104", errorMsg);

                                    return true;
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
