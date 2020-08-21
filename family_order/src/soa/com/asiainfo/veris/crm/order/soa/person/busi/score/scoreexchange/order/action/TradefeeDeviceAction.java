
package com.asiainfo.veris.crm.order.soa.person.busi.score.scoreexchange.order.action;

import java.util.List;

import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DeviceTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;

public class TradefeeDeviceAction implements ITradeAction
{

    @Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
        List<DeviceTradeData> deviceTDList = btd.getTradeDatas(TradeTableEnum.TRADE_FEEDEVICE);
        BaseReqData req = btd.getRD();
        String preType = req.getPreType();
        String isConFirm = req.getIsConfirm();
        String tradeTypeCode = btd.getTradeTypeCode();
        if (StringUtils.isNotBlank(preType) && !"true".equals(isConFirm))
        {
            // 如果是预受理且不是二次回复确认受理，则不处理
            return;
        }
        for (DeviceTradeData deviceTD : deviceTDList)
        {
            IDataset result = ResCall.updateValueCardInfoIntf(deviceTD.getDeviceNoS(), deviceTD.getDeviceNoE(), null, "3", "1", CSBizBean.getVisit().getDepartId(), tradeTypeCode);// FEE_TAG为1表示赠送
            if (IDataUtil.isNotEmpty(result) && result.getData(0).getInt("X_RESULTCODE") != 0)
            {
                // 调用资源更新接口出错！
                CSAppException.apperr(CrmUserException.CRM_USER_867);
            }
        }
    }
}
