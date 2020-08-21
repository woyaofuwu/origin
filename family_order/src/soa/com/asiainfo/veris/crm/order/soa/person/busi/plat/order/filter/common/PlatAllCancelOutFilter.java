
package com.asiainfo.veris.crm.order.soa.person.busi.plat.order.filter.common;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterOut;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.configdata.plat.PlatOfficeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PlatSvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;

/**
 * 全退订出参转换接口
 * 
 * @author xiekl
 */
public class PlatAllCancelOutFilter implements IFilterOut
{

    @Override
    public IData transfterDataOut(IData input, BusiTradeData btd) throws Exception
    {
        UcaData uca = btd.getRD().getUca();
        IDataset bizTypeCodes = new DatasetList();
        List<PlatSvcTradeData> userPlatSvcList = uca.getUserPlatSvcsByModi();
        for (PlatSvcTradeData userPlatSvc : userPlatSvcList)
        {
            bizTypeCodes.add(PlatOfficeData.getInstance(userPlatSvc.getElementId()).getBizTypeCode());
        }
        input.put("BIZ_TYPE_CODE", bizTypeCodes);
        return input;
    }

}
