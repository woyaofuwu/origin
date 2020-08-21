
package com.asiainfo.veris.crm.order.soa.person.rule.run.productchange;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @ClassName: GrpVpmnFirstMonthFreeLimit.java
 * @Description: 特殊处理集团V网首月功能费套餐（优惠编码3318） IN_MODE_CODE=5是短信营业厅
 * @version: v1.0.0
 * @author: maoke
 * @date: Aug 21, 2014 8:36:18 PM Modification History: Date Author Version Description
 *        -------------------------------------------------------* Aug 21, 2014 maoke v1.0.0 修改原因
 */
public class GrpVpmnFirstMonthFreeLimit extends BreBase implements IBREScript
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

                        if (BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(elementTypeCode) && "3318".equals(elementId) && BofConst.MODIFY_TAG_ADD.equals(modifyTag))
                        {
                            String serialNumber = databus.getString("SERIAL_NUMBER");
                            String userId = databus.getString("USER_ID");
                            String relationStartDate = "";
                            String discntStartDate = "";

                            IDataset userRelation = RelaUUInfoQry.getRelationUUInfoByDeputySn(userId, "20", null);
                            if (IDataUtil.isEmpty(userRelation))
                            {
                                String errorMsg = "该号码【" + serialNumber + "】不属于任何VPMN集团成员!";

                                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "1101201", errorMsg);

                                return true;
                            }
                            else
                            {
                                relationStartDate = userRelation.getData(0).getString("START_DATE");
                            }

                            UcaData uca = (UcaData) databus.get("UCADATA");

                            List<DiscntTradeData> userDiscnts = uca.getUserDiscntsByDiscntCodeArray("1285,1286,1391,686");
                            if (userDiscnts != null && userDiscnts.size() > 0)
                            {
                                for (DiscntTradeData userDiscnt : userDiscnts)
                                {
                                    String userDisnctCode = userDiscnt.getElementId();
                                    String relationTypeCode = userDiscnt.getRelationTypeCode();

                                    if ("20".equals(relationTypeCode) && ("1285".equals(userDisnctCode) || "1286".equals(userDisnctCode) || "1391".equals(userDisnctCode) || "686".equals(userDisnctCode)))
                                    {
                                        discntStartDate = userDiscnt.getStartDate();
                                        break;
                                    }
                                }
                            }
                            else
                            {
                                String errorMsg = "该号码【" + serialNumber + "】没有任何VPMN集团优惠!";

                                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "1101203", errorMsg);

                                return true;
                            }

                            if (!(StringUtils.isNotBlank(relationStartDate) && StringUtils.isNotBlank(discntStartDate) && SysDateMgr.getMonthForDate(relationStartDate).equals(SysDateMgr.getCurMonth())
                                    && SysDateMgr.getMonthForDate(discntStartDate).equals(SysDateMgr.getCurMonth()) && relationStartDate.equals(discntStartDate)))
                            {
                                String errorMsg = "该号码【" + serialNumber + "】不是当月新加入V网用户!";

                                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "1101203", errorMsg);

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
