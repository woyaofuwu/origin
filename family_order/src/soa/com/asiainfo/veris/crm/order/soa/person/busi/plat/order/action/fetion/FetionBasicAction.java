
package com.asiainfo.veris.crm.order.soa.person.busi.plat.order.action.fetion;

import java.util.ArrayList;
import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.IProductModuleAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.ProductModuleTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PlatSvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.plat.PlatInfoQry;

public class FetionBasicAction implements IProductModuleAction
{

    @Override
    public void executeProductModuleAction(ProductModuleTradeData dealPmtd, UcaData uca, BusiTradeData btd) throws Exception
    {
        PlatSvcTradeData pstd = (PlatSvcTradeData) dealPmtd;
        if (PlatConstants.OPER_ORDER.equals(pstd.getOperCode()))
        {
            return;
        }
        // 飞信基础服务需要处理与之相关的扩展服务
        IDataset limitServices = PlatInfoQry.queryFetionLimit(pstd.getElementId(), pstd.getOperCode());
        List<PlatSvcTradeData> userFetions = new ArrayList<PlatSvcTradeData>();
        if (limitServices != null && limitServices.size() > 0)
        {
            int size = limitServices.size();
            for (int i = 0; i < size; i++)
            {
                IData limitService = limitServices.getData(i);
                String serviceId = limitService.getString("SERVICE_ID", limitService.getString("OFFER_CODE"));
                userFetions.addAll(uca.getUserPlatSvcByServiceId(serviceId));
            }
        }
        if (userFetions.size() > 0)
        {
            int size = userFetions.size();
            for (int i = 0; i < size; i++)
            {
                PlatSvcTradeData userFetion = userFetions.get(i);
                userFetion.setOperCode(pstd.getOperCode());
                userFetion.setOprSource(pstd.getOprSource());
                userFetion.setBizStateCode(pstd.getBizStateCode());
                userFetion.setOperTime(pstd.getOperTime());
                userFetion.setEndDate(pstd.getEndDate());
                userFetion.setIsNeedPf("0");
                userFetion.setModifyTag(pstd.getModifyTag());
                if (BofConst.MODIFY_TAG_USER.equals(userFetion.getModifyTag()))
                {
                    btd.add(uca.getSerialNumber(), userFetion);
                }
            }
        }

    }

}
