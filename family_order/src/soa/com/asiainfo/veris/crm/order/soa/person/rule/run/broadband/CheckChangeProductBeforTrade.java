
package com.asiainfo.veris.crm.order.soa.person.rule.run.broadband;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @ClassName: CheckChangeProductBeforTrade.java
 * @Description: 校验产品变更【TradeCheckBefore】
 * @version: v1.0.0
 * @author: likai3
 * @date: May 23, 2014 2:55:01 PM Modification History: Date Author Version Description
 *        -------------------------------------------------------* May 23, 2014 likai3 v1.0.0 修改原因
 */
public class CheckChangeProductBeforTrade extends BreBase implements IBREScript
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private IData getNewDiscntDays(IDataset elementList) throws Exception
    {
        IData data = new DataMap();
        for (int j = 0; j < elementList.size(); j++)
        {
            String elementTypeCode = elementList.getData(j).getString("ELEMENT_TYPE_CODE", "");
            if (BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(elementTypeCode))
            {
                String state = elementList.getData(j).getString("STATE");
                String strSD = "";
                String strED = "";
                if (state.equals("0"))
                {
                    strSD = elementList.getData(j).getString("START_DATE", SysDateMgr.getSysTime());
                    strED = elementList.getData(j).getString("END_DATE", SysDateMgr.END_DATE_FOREVER);

                    int newdays = SysDateMgr.dayInterval(strSD, strED);
                    data.put("NEW_DAYS", newdays);
                    data.put("START_DATE", strSD);
                    data.put("END_DATE", strED);
                }
            }
        }
        return data;
    }

    private IData getUserDiscnt(IData databus) throws Exception
    {

        IDataset userDiscntInfos = UserDiscntInfoQry.getAllValidDiscntByUserId(databus.getString("USER_ID"));
        for (int i = 0; i < userDiscntInfos.size(); i++)
        {
            if (userDiscntInfos.getData(i).getString("PRODUCT_ID").equals("-1"))
            {
                userDiscntInfos.remove(i);
            }
        }
        return !userDiscntInfos.isEmpty() ? userDiscntInfos.getData(0) : null;
    }

    @Override
    public boolean run(IData databus, BreRuleParam param) throws Exception
    {
        String errorMsg = "";
        String xChoiceTag = databus.getString("X_CHOICE_TAG");

        if (StringUtils.isBlank(xChoiceTag) || StringUtils.equals("1", xChoiceTag))// 提交时校验，依赖请求数据
        {
            IData oldData = getUserDiscnt(databus);

            IData reqData = databus.getData("REQDATA");// 请求的数据

            if (IDataUtil.isNotEmpty(reqData))
            {
                IDataset selectedElements = new DatasetList(reqData.getString("SELECTED_ELEMENTS"));
                IData newData = getNewDiscntDays(selectedElements);

                if (IDataUtil.isNotEmpty(newData) && IDataUtil.isNotEmpty(oldData))
                {
                    if (oldData.getInt("RSRV_NUM1") > 31 && newData.getInt("NEW_DAYS") - oldData.getInt("RSRV_NUM1") >= 0)
                    {
                        String oldEndDate = SysDateMgr.decodeTimestamp(oldData.getString("END_DATE"), "yyyy-MM-dd HH:mm:ss");

                        String currentCycle = SysDateMgr.getNowCyc();
                        String lastUserCycle = SysDateMgr.decodeTimestamp(SysDateMgr.getAddMonthsNowday(-1, oldEndDate), "yyyyMM");

                        if (oldData.getInt("RSRV_NUM1") > 31 && currentCycle.compareTo(lastUserCycle) >= 0)
                        {
                            errorMsg = "由于宽带用户已进入最后一个计费账期，不能受理产品变更！";
                            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "-1", errorMsg);
                            return true;
                        }
                    }
                    else
                    {
                        int currentUserDays = 0;
                        int newUserDays = 0;

                        currentUserDays = SysDateMgr.dayInterval(oldData.getString("START_DATE"), SysDateMgr.getSysTime());

                        newUserDays = SysDateMgr.dayInterval(newData.getString("START_DATE"), newData.getString("END_DATE"));

                        if (currentUserDays >= newUserDays)
                        {
                            errorMsg = "由于宽带用户使用已经超过新优惠的时长，不能受理产品变更！";
                            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "-1", errorMsg);
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

}
