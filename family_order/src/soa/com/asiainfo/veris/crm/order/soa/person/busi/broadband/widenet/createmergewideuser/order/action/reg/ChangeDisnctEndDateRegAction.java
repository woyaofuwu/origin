package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.createmergewideuser.order.action.reg;

import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

import java.util.List;

/**
 * REQ201808070001 海工商最新的校园宽带需求
 * 海工商宽带主产品是动感地带超享卡60元宽带融合套餐 用户，实行25号规则，即25号以前收取，25号以后不收取，次月1号正常收费
 */
public class ChangeDisnctEndDateRegAction implements ITradeAction {

    @Override
    public void executeAction(BusiTradeData btd) throws Exception {
        List<DiscntTradeData> userDsicnts =  btd.getRD().getUca().getUserDiscnts();
        String eparchyCode = btd.getRD().getUca().getUserEparchyCode();
        for (int i = 0; i < userDsicnts.size(); i++) {
            DiscntTradeData discntTradeData = userDsicnts.get(i);
            String startDate = discntTradeData.getStartDate();
            IDataset comparaList = CommparaInfoQry.getCommpara("CSM","3978", discntTradeData.getDiscntCode(),eparchyCode);
            if (IDataUtil.isNotEmpty(comparaList) && SysDateMgr.getIntDayByDate(startDate) >= 25)
            {
                discntTradeData.setEndDate(SysDateMgr.getDateLastMonthSec(startDate));
            }
        }
    }
}
