
package com.asiainfo.veris.crm.order.soa.person.busi.plat.order.action.cmmb;

import java.util.ArrayList;
import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.IProductModuleAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.PlatReload;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.ProductModuleTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PlatSvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

public class CmmbAction implements IProductModuleAction
{

    @Override
    public void executeProductModuleAction(ProductModuleTradeData dealPmtd, UcaData uca, BusiTradeData btd) throws Exception
    {
        PlatSvcTradeData pstd = (PlatSvcTradeData) dealPmtd;
        List<PlatSvcTradeData> userTempCmmbs = new ArrayList<PlatSvcTradeData>();
        IDataset cmmbConfigs = CommparaInfoQry.getCommByParaAttr("CSM", "3705", null);
        int size = cmmbConfigs.size();
        IData timeConfig = new DataMap();
        for (int i = 0; i < size; i++)
        {
            IData config = cmmbConfigs.getData(i);
            String serviceId = config.getString("PARA_CODE1");
            userTempCmmbs.addAll(uca.getUserPlatSvcByServiceId(serviceId));
            timeConfig.put(serviceId, config);
        }
        userTempCmmbs.addAll(uca.getUserPlatSvcByServiceId(PlatReload.cmmbStandard));
        size = userTempCmmbs.size();
        List<PlatSvcTradeData> userCmmbs = new ArrayList<PlatSvcTradeData>();
        for (int i = 0; i < size; i++)
        {
            // 因为本次受理的服务已经加入到uca中了，所以取用户现有的CMMB包N月套餐要排除本次受理的服务
            PlatSvcTradeData temp = userTempCmmbs.get(i);
            if (!temp.getInstId().equals(pstd.getInstId()))
            {
                userCmmbs.add(temp);
            }
        }
        if (PlatConstants.OPER_ORDER.equals(pstd.getOperCode()) && !"64".equals(pstd.getOprSource()))
        {
            if (userCmmbs.size() <= 0)
            {
                if (!pstd.getElementId().equals(PlatReload.cmmbStandard))
                {
                    pstd.setEndDate(SysDateMgr.endDateOffset(pstd.getStartDate(), timeConfig.getData(pstd.getElementId()).getString("PARA_CODE2"), "3"));
                }
            }
            else
            {
                size = userCmmbs.size();
                boolean isNeedUpdFirst = false;
                boolean isNeedUpdFirstMon = false;
                if (pstd.getFirstDate().equals(pstd.getStartDate()))
                {
                    isNeedUpdFirst = true;
                }
                if (pstd.getFirstDateMon().equals(pstd.getStartDate()))
                {
                    isNeedUpdFirstMon = true;
                }
                pstd.setStartDate(SysDateMgr.getFirstDayOfNextMonth());
                if (isNeedUpdFirst)
                {
                    pstd.setFirstDate(pstd.getStartDate());
                }
                if (isNeedUpdFirstMon)
                {
                    pstd.setFirstDateMon(pstd.getStartDate());
                }
                if (!pstd.getElementId().equals(PlatReload.cmmbStandard))
                {
                    pstd.setEndDate(SysDateMgr.endDateOffset(pstd.getStartDate(), timeConfig.getData(pstd.getElementId()).getString("PARA_CODE2"), "2"));
                }
                for (int i = 0; i < size; i++)
                {
                    PlatSvcTradeData cmmb = userCmmbs.get(i);
                    if (cmmb.getEndDate().compareTo(SysDateMgr.getLastDateThisMonth()) > 0)
                    {
                        // 凡是终止时间大于本月末的，做退订处理
                        PlatSvcTradeData cmmbTrade = cmmb.clone();
                        cmmbTrade.setModifyTag(BofConst.MODIFY_TAG_DEL);
                        cmmbTrade.setOperCode(PlatConstants.OPER_CANCEL_ORDER);
                        cmmbTrade.setOprSource(pstd.getOprSource());
                        cmmbTrade.setBizStateCode(PlatConstants.STATE_CANCEL);
                        cmmbTrade.setOperTime(pstd.getOperTime());
                        cmmbTrade.setEndDate(SysDateMgr.getLastSecond(pstd.getStartDate()));
                        if ("53".equals(pstd.getOprSource()))
                        {
                            cmmbTrade.setIsNeedPf("0");
                        }
                        else
                        {
                            cmmbTrade.setIsNeedPf("1");
                        }
                        btd.add(uca.getSerialNumber(), cmmbTrade);
                    }
                }
            }
        }
        else if (PlatConstants.OPER_CANCEL_ORDER.equals(pstd.getOperCode()))
        {
            String thisMonthEnd = SysDateMgr.getLastDateThisMonth();
            if (pstd.getStartDate().compareTo(thisMonthEnd) < 0)
            {
                // 表示退订的是当月有效的服务，需要将其余的一并退订
                size = userCmmbs.size();
                for (int i = 0; i < size; i++)
                {
                    PlatSvcTradeData cmmb = userCmmbs.get(i);
                    if (cmmb.getModifyTag().equals(BofConst.MODIFY_TAG_USER) && cmmb.getStartDate().compareTo(thisMonthEnd) > 0)
                    {
                        PlatSvcTradeData cmmbTrade = cmmb.clone();
                        cmmbTrade.setModifyTag(BofConst.MODIFY_TAG_DEL);
                        cmmbTrade.setOperCode(PlatConstants.OPER_CANCEL_ORDER);
                        cmmbTrade.setOprSource(pstd.getOprSource());
                        cmmbTrade.setOperTime(pstd.getOperTime());
                        cmmbTrade.setEndDate(pstd.getOperTime());
                        cmmbTrade.setBizStateCode(PlatConstants.STATE_CANCEL);
                        if ("53".equals(pstd.getOprSource()))
                        {
                            cmmbTrade.setIsNeedPf("0");
                        }
                        else
                        {
                            cmmbTrade.setIsNeedPf("1");
                        }
                        btd.add(uca.getSerialNumber(), cmmbTrade);
                    }
                }
            }
        }
    }

}
