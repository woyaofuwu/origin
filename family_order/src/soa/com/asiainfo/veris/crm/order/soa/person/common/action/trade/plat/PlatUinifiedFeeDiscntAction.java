
package com.asiainfo.veris.crm.order.soa.person.common.action.trade.plat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PlatSvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.plat.PlatInfoQry;

/**
 * 统一费用处理 平台服务多个服务共用一个优惠 td_s_compara 3746属性配置了的优惠 如果当所有服务都退订了，则需要退订该优惠
 * 
 * @author xiekl
 */
public class PlatUinifiedFeeDiscntAction implements ITradeAction
{

    public void executeAction(BusiTradeData btd) throws Exception
    {
        UcaData uca = btd.getRD().getUca();
        String serialNumber = uca.getSerialNumber();
        IDataset configList = PlatInfoQry.getPlatUinifiedFeeDiscntConfig(BizRoute.getRouteId(), null);
        Map<String, List> configMap = new HashMap<String, List>();

        // 首先将配置按优惠归类 一个统一付费优惠下面的多个服务归类
        if (!IDataUtil.isEmpty(configList))
        {
            int length = configList.size();
            for (int i = 0; i < length; i++)
            {
                IData uinifiedFeeConfig = configList.getData(i);
                String discntId = uinifiedFeeConfig.getString("DISCNT_CODE");

                if (configMap.get(discntId) == null)
                {
                    configMap.put(discntId, new ArrayList());
                }
                else
                {
                    List configSvcList = configMap.get(discntId);
                    configSvcList.add(uinifiedFeeConfig);
                }
            }
        }

        // 遍历配置
        Iterator iterator = configMap.keySet().iterator();
        while (iterator.hasNext())
        {
            String discntCode = (String) iterator.next();
            List svcConfigList = configMap.get(discntCode);

            List<DiscntTradeData> ucaDiscntList = uca.getUserDiscntByDiscntId(discntCode);
            // 如果此统一优惠没有相关服务了，且用户资料还有此 优惠，则删除此优惠
            if (ucaDiscntList != null && !ucaDiscntList.isEmpty())
            {
                for (int j = 0; j < svcConfigList.size(); j++)
                {
                    IData config = (IData) svcConfigList.get(j);
                    String svcId = config.getString("SERVICE_ID");
                    int count = 0;

                    // 判断用户是否存在这些服务 包括平台服务和一般服务
                    if ("Z".equals(config.getString("ELEMENT_TYPE_CODE")))
                    {
                        List<PlatSvcTradeData> ucaPlatSvcList = uca.getUserPlatSvcByServiceId(svcId);
                        for (int k = 0; k < ucaPlatSvcList.size(); k++)
                        {
                            PlatSvcTradeData platSvc = ucaPlatSvcList.get(k);
                            if (platSvc != null)
                            {
                                if (!PlatConstants.STATE_CANCEL.equals(platSvc.getBizStateCode()))
                                {
                                    count++;
                                }
                            }
                        }
                    }
                    else
                    {
                        List<SvcTradeData> ucaSvcList = uca.getUserSvcBySvcId(svcId);
                        for (int m = 0; m < ucaSvcList.size(); m++)
                        {
                            SvcTradeData svc = ucaSvcList.get(m);
                            if (svc != null)
                            {
                                if (!BofConst.MODIFY_TAG_DEL.equals(svc.getModifyTag()))
                                {
                                    count++;
                                }
                            }
                        }
                    }

                    // 如果没有服务了则生成删除优惠订单
                    if (count == 0)
                    {
                        for (int k = 0; k < ucaDiscntList.size(); k++)
                        {
                            DiscntTradeData ucaDiscnt = ucaDiscntList.get(k);
                            if (BofConst.MODIFY_TAG_USER.equals(ucaDiscnt.getModifyTag()))
                            {
                                DiscntTradeData tdd = ucaDiscnt.clone();
                                tdd.setModifyTag("1");
                                String enableTag = config.getString("ENABLE_TAG", "0");
                                // 0立即终止 1月底终止
                                if ("0".equals(enableTag))
                                {
                                    tdd.setEndDate(SysDateMgr.getSysDate());
                                }
                                else if ("1".equals(enableTag))
                                {
                                    tdd.setEndDate(SysDateMgr.getLastDateThisMonth());
                                }
                                btd.add(serialNumber, tdd);

                                continue;
                            }
                        }
                    }
                }
            }
        }
    }

}
