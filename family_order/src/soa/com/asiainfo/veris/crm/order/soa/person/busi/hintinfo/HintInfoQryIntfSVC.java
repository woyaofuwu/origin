
package com.asiainfo.veris.crm.order.soa.person.busi.hintinfo;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeHintInfoQry;

public class HintInfoQryIntfSVC extends CSBizService
{
    public IDataset getHintInfo(IData data) throws Exception
    {
        String eparchyCode = data.getString("EPARCHY_CODE", this.getTradeEparchyCode());
        String tradeTypeCode = data.getString("TRADE_TYPE_CODE", "");
        if (StringUtils.isBlank(tradeTypeCode))
        {// "10001", "TRADE_TYPE_CODE字段为空！
            CSAppException.apperr(CrmUserException.CRM_USER_1156);
        }
        String elementTypeCode = data.getString("ELEMENT_TYPE_CODE", "");// 元素类型：A-业务级；P-产品级；S-服务级；D-优惠级
        if (!"A".equals(elementTypeCode))
        {
            String elementId = data.getString("ELEMENT_ID", "");
            if (StringUtils.isBlank(elementId))
            {// "10002", "ELEMENT_ID字段为空，当业务类型为产品/服务/优惠时，该字段必填！"
                CSAppException.apperr(CrmUserException.CRM_USER_1157);
            }
        }
        String inModeCode = data.getString("IN_MODE_CODE", "");
        if (StringUtils.isBlank(inModeCode))
        {// 10003", "IN_MODE_CODE字段为空！
            CSAppException.apperr(CrmUserException.CRM_USER_1158);
        }
        String execMode = data.getString("EXEC_MODE", "");// 提醒时机：0-事前提醒；1-事中提醒；2-事后提醒
        if (StringUtils.isBlank(execMode))
        {// 10001", "EXEC_MODE字段为空！
            CSAppException.apperr(CrmUserException.CRM_USER_1159);
        }
        String hintType = data.getString("HINT_TYPE", "");// 提示方式：0-界面；1-短信；2-打印
        if (StringUtils.isBlank(hintType))
        {// 10001", "HINT_TYPE字段为空！
            CSAppException.apperr(CrmUserException.CRM_USER_1160);
        }
        IDataset dataset = TradeHintInfoQry.qryHintInfoByTrade("1", eparchyCode, data);// TAG 1-已审核

        return dataset;
    }
}
