
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.createwideuser.order.action.finish;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.WidenetException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradePbossAttrInfoQry;

/**
 * 由于安装地址错误导致上门施工时重新选择宽带用户的安装地址并返回给订单管理，所以CRM必须取出新的地址来修改
 * 
 * @author chenzm
 */
public class UpdTradeWideNetAddrAction implements ITradeFinishAction
{

    public void executeAction(IData mainTrade) throws Exception
    {
        String tradeId = mainTrade.getString("TRADE_ID");
        String userId = mainTrade.getString("USER_ID");
        String addr = "RM_INSTALL_ADDRESS";// 与服开约定
        String addrCode = "RM_INSTALL_ADDRESS_CODE";// 与服开约定
        IDataset attrInfos = TradePbossAttrInfoQry.getTradePbossAttr(tradeId, addr);
        if (IDataUtil.isEmpty(attrInfos))
        {
            return;
        }
        IDataset attrCodeInfos = TradePbossAttrInfoQry.getTradePbossAttr(tradeId, addrCode);
        if (IDataUtil.isEmpty(attrCodeInfos))
        {
            CSAppException.apperr(WidenetException.CRM_WIDENET_12, tradeId, addrCode);
        }
        String installAddress = attrInfos.getData(0).getString("ATTR_VALUE");
        String installAddressCode = attrCodeInfos.getData(0).getString("ATTR_VALUE");

        // 如服开有修改则CRM修改台账表内的用户地址
        IData data = new DataMap();
        data.put("TRADE_ID", tradeId);
        data.put("USER_ID", userId);
        data.put("STAND_ADDRESS", installAddress);
        data.put("STAND_ADDRESS_CODE", installAddressCode);
        data.put("REMARK", "施工修改安装地址");
        int count = Dao.executeUpdateByCodeCode("TF_B_TRADE_WIDENET", "UPD_ADDR_BY_TRADEID", data, Route.getJourDb(BizRoute.getTradeEparchyCode()));
        if (count != 1)
        {
            CSAppException.apperr(WidenetException.CRM_WIDENET_13);
        }

    }

}
