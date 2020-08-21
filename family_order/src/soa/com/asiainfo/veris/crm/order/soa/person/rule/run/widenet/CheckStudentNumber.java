
package com.asiainfo.veris.crm.order.soa.person.rule.run.widenet;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

public class CheckStudentNumber extends BreBase implements IBREScript
{

    // 检验校园宽带学号
    public boolean run(IData databus, BreRuleParam paramData) throws Exception
    {
        String xChoiceTag = databus.getString("X_CHOICE_TAG");

        if (StringUtils.isBlank(xChoiceTag) || StringUtils.equals("1", xChoiceTag))// 提交时校验，依赖请求数据
        {
            IData reqData = databus.getData("REQDATA");// 请求的数据
            String studentNumber = reqData.getString("STUDENT_NUMBER");
            String paraCode1 = "";
            int iLength = studentNumber.length();

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
                        if (BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(elementTypeCode) && BofConst.MODIFY_TAG_ADD.equals(modifyTag))
                        {
                            IDataset commParaInfos = CommparaInfoQry.getCommpara("CSM", "82", elementId, CSBizBean.getTradeEparchyCode());

                            if (IDataUtil.isNotEmpty(commParaInfos))
                            {
                                paraCode1 = commParaInfos.getData(0).getString("PARA_CODE1");
                            }
                            else
                            {
                                paraCode1 = "10";
                            }
                            if (iLength != Integer.parseInt(paraCode1))
                            {
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
