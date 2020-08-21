
package com.asiainfo.veris.crm.order.soa.person.rule.run.productchange;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.ProductTroopMemberQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @ClassName: CheckProductDiscntLimit.java
 * @Description: REQ201408010002 神州行30、50元套餐开发需求【TradeCheckBefore】
 * @version: v1.0.0
 * @author: maoke
 * @date: Sep 3, 2014 4:26:03 PM Modification History: Date Author Version Description
 *        -------------------------------------------------------* Sep 3, 2014 maoke v1.0.0 修改原因
 */
public class CheckProductDiscntLimit extends BreBase implements IBREScript
{

    /**
     * @Description: 返回报错信息
     * @param elementTypeCode
     * @param elementId
     * @param openDate
     * @param serialNumber
     * @param eparchyCode
     * @return
     * @throws Exception
     * @author: maoke
     * @date: Sep 3, 2014 5:50:14 PM
     */
    public String checkProductDisnct(String elementTypeCode, String elementId, String openDate, String serialNumber, String eparchyCode) throws Exception
    {
        String paraParam = "";
        String errorMsg = "";

        if (BofConst.ELEMENT_TYPE_CODE_PRODUCT.equals(elementTypeCode))
        {
            paraParam = "01";

        }
        if (BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(elementTypeCode))
        {
            paraParam = "02";

        }

        IDataset commpara1690 = CommparaInfoQry.getCommparaByAttrCode1("CSM", "1690", paraParam, eparchyCode, null);
        if (IDataUtil.isNotEmpty(commpara1690))
        {
            for (int i = 0, size = commpara1690.size(); i < size; i++)
            {
                IData commparaData = commpara1690.getData(i);

                String paramCode = commparaData.getString("PARAM_CODE", ""); // 产品编码
                String paraCode1 = commparaData.getString("PARA_CODE1", ""); // 类别编码
                String paraCode2 = commparaData.getString("PARA_CODE2", ""); // 比较天数
                String paraCode3 = commparaData.getString("PARA_CODE3", ""); // 集群用户标识
                String paraCode17 = commparaData.getString("PARA_CODE17", ""); // 天数提示信息
                String paraCode18 = commparaData.getString("PARA_CODE18", ""); // 集群提示信息

                if (!paraCode2.matches("^[0-9]*$"))
                {
                    errorMsg = "TD_S_COMMPARA表【1609】参数PARA_CODE2字段非数字!";
                }

                IDataset commpara1594 = CommparaInfoQry.getEnableCommparaInfoByCode12("CSM", "1594", paramCode, elementId, paraCode1, eparchyCode);
                if (IDataUtil.isNotEmpty(commpara1594))// 判断用户开通天数是否达到约定的天数
                {
                    int days = SysDateMgr.dayInterval(openDate, SysDateMgr.getSysDate());

                    if (Integer.parseInt(paraCode2) > 0 && days > Integer.parseInt(paraCode2))
                    {
                        String elementName = "";

                        if (BofConst.ELEMENT_TYPE_CODE_PRODUCT.equals(elementTypeCode))
                        {
                            elementName = UProductInfoQry.getProductNameByProductId(elementId);
                            errorMsg = "尊敬的用户，您好！您开户时间【"+openDate+"】大于【"+paraCode2+"】天不满足办理产品【" + elementName + "】的条件，详询10086！";
                        }
                        if (BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(elementTypeCode))
                        {
                            elementName = UDiscntInfoQry.getDiscntNameByDiscntCode(elementId);
                            errorMsg = "尊敬的用户，您好！您开户时间【"+openDate+"】大于【"+paraCode2+"】天不满足办理优惠【" + elementName + "】的条件，详询10086！";
                        }
                    }
                }
                else
                // 校验用户集群信息
                {
                    if (StringUtils.isNotBlank(paraCode3))
                    {
                        IDataset tRoopDataset = ProductTroopMemberQry.qryTroopMemberByCodeId(serialNumber, paraCode3);

                        if (IDataUtil.isNotEmpty(tRoopDataset))
                        {
                            errorMsg = paraCode18;
                        }
                    }
                }
            }
        }
        return errorMsg;
    }

    @Override
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        String xChoiceTag = databus.getString("X_CHOICE_TAG");

        if (StringUtils.isBlank(xChoiceTag) || StringUtils.equals("1", xChoiceTag))// 提交时校验，依赖请求数据
        {
            IData reqData = databus.getData("REQDATA");// 请求的数据

            UcaData uca = (UcaData) databus.get("UCADATA");

            String openDate = uca.getUser().getOpenDate();
            String eparchyCode = uca.getUserEparchyCode();
            String serialNumber = databus.getString("SERIAL_NUMBER");

            if (IDataUtil.isNotEmpty(reqData))
            {
                // 校验变更的新产品
                String newProductId = reqData.getString("NEW_PRODUCT_ID");// 新产品
                String userProductId = databus.getString("PRODUCT_ID");// 老产品
                if (StringUtils.isNotBlank(newProductId) && !userProductId.equals(newProductId))
                {
                    String errorMsg = this.checkProductDisnct(BofConst.ELEMENT_TYPE_CODE_PRODUCT, newProductId, openDate, serialNumber, eparchyCode);

                    if (StringUtils.isNotBlank(errorMsg))
                    {
                        BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 12062739, errorMsg);

                        return true;
                    }
                }

                // 校验优惠
                IDataset selectedElements = new DatasetList(reqData.getString("SELECTED_ELEMENTS"));
                if (IDataUtil.isNotEmpty(selectedElements))
                {
                    for (int i = 0, size = selectedElements.size(); i < size; i++)
                    {
                        IData element = selectedElements.getData(i);

                        String elementTypeCode = element.getString("ELEMENT_TYPE_CODE");
                        String elementId = element.getString("ELEMENT_ID");
                        String modifyTag = element.getString("MODIFY_TAG");

                        if (BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(elementTypeCode) && BofConst.MODIFY_TAG_ADD.equals(modifyTag))
                        {
                            String errorMsg = this.checkProductDisnct(BofConst.ELEMENT_TYPE_CODE_DISCNT, elementId, openDate, serialNumber, eparchyCode);

                            if (StringUtils.isNotBlank(errorMsg))
                            {
                                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 12062739, errorMsg);

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
