
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.widenetinterface;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInterfaceInfoQry;

public class WidenetInterfaceSVC extends CSBizService
{

    private static final long serialVersionUID = 1L;

    public IDataset execInterface(IData input) throws Exception
    {
        WidenetInterfaceBean widenetInterfaceBean = BeanManager.createBean(WidenetInterfaceBean.class);
        widenetInterfaceBean.dealTradeInterfaceForBat(input);
        IDataset results = widenetInterfaceBean.getAllTradeInterface();
        return results;
    }

    public IDataset getTradeInterface(IData input) throws Exception
    {
        String tradeId = input.getString("TRADE_ID");
        String serialNumber = input.getString("SERIAL_NUMBER");
        String execResult = input.getString("EXEC_RESULT");
        String tradeTypeCode = input.getString("TRADE_TYPE_CODE");
        String beginDate = input.getString("START_DATE");
        String endDate = input.getString("END_DATE");
        return TradeInterfaceInfoQry.getTradeInterfaceInfo(tradeId, serialNumber, execResult, tradeTypeCode, beginDate, endDate, getPagination());
    }

    /**
     * 初始化方法
     * 
     * @author chenzm
     * @param input
     * @throws Exception
     */

    public IDataset onInitTrade(IData input) throws Exception
    {
        WidenetInterfaceBean widenetInterfaceBean = BeanManager.createBean(WidenetInterfaceBean.class);
        return widenetInterfaceBean.onInitTrade(input);
    }

    public void restartInterface(IData input) throws Exception
    {
        IDataset tradeInterfaces = getTradeInterface(input);
        if (IDataUtil.isEmpty(tradeInterfaces))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "该单子【" + input.getString("TRADEID") + "】已经在系统中不存在！");
        }
        WidenetInterfaceBean widenetInterfaceBean = BeanManager.createBean(WidenetInterfaceBean.class);
        widenetInterfaceBean.dealTradeInterfaceForWeb(tradeInterfaces.getData(0));

    }

}
