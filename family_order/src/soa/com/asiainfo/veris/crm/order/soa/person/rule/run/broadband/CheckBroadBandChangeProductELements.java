
package com.asiainfo.veris.crm.order.soa.person.rule.run.broadband;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.USvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ParamInfoQry;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @ClassName: CheckBroadBandChangeProductELements.java
 * @Description: 校验服务和选择的资费对应关系【TradeCheckBefore】
 * @version: v1.0.0
 * @author: likai3
 * @date: May 23, 2014 2:55:01 PM Modification History: Date Author Version Description
 *        -------------------------------------------------------* May 23, 2014 likai3 v1.0.0 修改原因
 */
public class CheckBroadBandChangeProductELements extends BreBase implements IBREScript
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Override
    public boolean run(IData databus, BreRuleParam param) throws Exception
    {
        String errorMsg = "";
        String xChoiceTag = databus.getString("X_CHOICE_TAG");

        String tmpSvcSpeed = ""; // 速率
        String svcName = ""; // 服务名称
        int count = 0;
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
                        String mainTag = element.getString("MAIN_TAG");
                        String state = element.getString("STATE");

                        if (BofConst.ELEMENT_TYPE_CODE_SVC.equals(elementTypeCode) && !mainTag.equalsIgnoreCase("1") && !state.equalsIgnoreCase("2"))
                        {
                            // 从参数表中查询配置的短信提醒内容
                            IDataset commparas = ParamInfoQry.getCommparaByCode("CSM", "1127", elementId, CSBizBean.getTradeEparchyCode());
                            if (commparas.size() == 0)
                            {
                                errorMsg = "未在TD_S_COMMPARA表配置服务参数![1127][" + elementId + "]";
                                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "-1", errorMsg);
                                return true;
                            }

                            String rate = commparas.getData(0).getString("PARA_CODE1");
                            tmpSvcSpeed = Integer.parseInt(rate) / 1024 + "M";

                            if (!"".equals(tmpSvcSpeed))
                            {
                                svcName = USvcInfoQry.getSvcNameBySvcId(elementId);
                                count++;// 由于目前只能有一种速率,多的速率,页面会做判断.
                            }
                        }
                    }
                    if (count > 1)
                    {
                        errorMsg = "您选择了两种速率，请重新选择！";
                        BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "-1", errorMsg);
                        return true;
                    }

                    for (int i = 0, size = selectedElements.size(); i < size; i++)
                    {
                        IData element = selectedElements.getData(i);

                        String elementTypeCode = element.getString("ELEMENT_TYPE_CODE");
                        String elementId = element.getString("ELEMENT_ID");
                        String state = element.getString("STATE");
                        if (BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(elementTypeCode) && !state.equalsIgnoreCase("U"))
                        {
                            String discntName = UDiscntInfoQry.getDiscntNameByDiscntCode(elementId);
                            if (discntName.indexOf(tmpSvcSpeed) < 0)
                            {
                                errorMsg = "所选择的优惠[" + discntName + "]与选择的服务[" + svcName + "]不匹配!";
                                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "-1", errorMsg);
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
