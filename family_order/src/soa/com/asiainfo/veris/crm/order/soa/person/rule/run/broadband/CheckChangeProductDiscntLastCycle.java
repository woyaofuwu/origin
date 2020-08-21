
package com.asiainfo.veris.crm.order.soa.person.rule.run.broadband;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @ClassName: CheckChangeProductDiscntLastCycle.java
 * @Description: 校验产品变更时是否有有效的资费【auth】
 * @version: v1.0.0
 * @author: likai3
 * @date: May 23, 2014 2:55:01 PM Modification History: Date Author Version Description
 *        -------------------------------------------------------* May 23, 2014 likai3 v1.0.0 修改原因
 */
public class CheckChangeProductDiscntLastCycle extends BreBase implements IBREScript
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public IData getNewDiscntDay(IData databus) throws Exception
    {
        IData data = new DataMap();
        IDataset selectedElements = new DatasetList(databus.getString("TF_B_TRADE_DISCNT"));
        if (!IDataUtil.isNull(selectedElements) && IDataUtil.isNotEmpty(selectedElements))
        {
            for (int i = 0; i < selectedElements.size(); i++)
            {
                IData element = selectedElements.getData(i);
                String elementTypeCode = element.getString("ELEMENT_TYPE_CODE");
                if (StringUtils.equals("0", element.getString("MODIFY_TAG")))
                {
                    String strSD = element.getString("START_DATE", SysDateMgr.getSysDate());
                    String strED = element.getString("END_DATE", SysDateMgr.getSysDate());
                    int newdays = SysDateMgr.dayInterval(strSD, strED);
                    data.put("NEW_DAYS", newdays);
                    data.put("START_DATE", strSD);
                    data.put("END_DATE", strED);
                }
            }
        }
        return data;
    }

    @Override
    public boolean run(IData databus, BreRuleParam param) throws Exception
    {
        String errorMsg = "";
        String xChoiceTag = databus.getString("X_CHOICE_TAG");

        if (StringUtils.isBlank(xChoiceTag) || StringUtils.equals("1", xChoiceTag))// auth组建调用的时候校验
        {
            IDataset userDiscntInfos = UserDiscntInfoQry.getAllValidDiscntByUserId(databus.getString("USER_ID"));
            
            //start by songlm J2EE铁通营改增遗留需求--宽带套餐变更只能变更生效时间最晚的套餐（针对宽带续费立即生效，而在用户优惠表中产生生效时间还未到的优惠）
            Boolean discntCount = true;
            IDataset userValidAndFutureDiscntInfos = UserDiscntInfoQry.getSpecDiscnt(databus.getString("USER_ID"));
            if(userValidAndFutureDiscntInfos.size() > 1)//如果存在还未生效的优惠
            {
            	discntCount = false;
            }
            //end
            
            IData newData = getNewDiscntDay(databus);
            if (IDataUtil.isNotEmpty(newData) && IDataUtil.isNotEmpty(userDiscntInfos))
            {
                for (int i = 0; i < userDiscntInfos.size(); i++)
                {
                    if (userDiscntInfos.getData(i).getString("PRODUCT_ID").equals("-1"))
                    {
                        userDiscntInfos.remove(i);
                    }
                }
                IData oldData = !userDiscntInfos.isEmpty() ? userDiscntInfos.getData(0) : null;
                if (IDataUtil.isEmpty(oldData))
                {
                    String erroInfo = "没有有效的资费，请先续费！";
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "972503", errorMsg);
                }
                
                if(discntCount)
                {
                	if (oldData.getInt("RSRV_NUM1") > 31 && newData.getInt("NEW_DAYS") - oldData.getInt("RSRV_NUM1") >= 0)
                    {
                        String oldEndDate = SysDateMgr.decodeTimestamp(oldData.getString("END_DATE"), SysDateMgr.PATTERN_STAND);
                        String currentCycle = SysDateMgr.getNowCyc();
                        String lastUserCycle = SysDateMgr.decodeTimestamp(SysDateMgr.getAddMonthsNowday(-1, oldEndDate), "yyyyMM");

                        // by zengzb 2010-12-09 增加 oldData.getInt("RSRV_NUM1") >31
                        // 条件过滤掉原优惠为包月优惠的情况,对于原优惠为包月优惠时允许做产品变更.
                        if (oldData.getInt("RSRV_NUM1") > 31 && currentCycle.compareTo(lastUserCycle) >= 0)
                        {
                            String erroInfo = "由于宽带用户已进入最后一个计费账期，不能受理产品变更！";
                            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "972501", erroInfo);
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
                            String erroInfo = "由于宽带用户使用已经超过新优惠的时长，不能受理产品变更！";
                            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "972502", erroInfo);
                        }
                    }
                }
                
            }
        }
        return false;
    }
}
