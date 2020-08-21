
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UTradeTypeInfoQry;

public class TradeTypeInfoQrySVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    public IDataset getTradeType(IData input) throws Exception
    {
        IDataset datas = new DatasetList();
        IData data = UTradeTypeInfoQry.getTradeType(input.getString("TRADE_TYPE_CODE"), input.getString("EPARCHY_CODE"));
        datas.add(data);
        return datas;
    }
    
    
    /**
     * 配置该业务是否必须打印免填单，配置了是可以不打印，不配置是必须打印
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset getTradePrintParam(IData input) throws Exception
    {
        return CommparaInfoQry.getCommparaAllCol("CSM","7890",input.getString("TRADE_TYPE_CODE"),input.getString("EPARCHY_CODE"));
    }

    /**
     * @author fengsl
     * @date 2013-04-16 查询订单类型
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset getTradeTypeForGrp(IData input) throws Exception
    {
        IDataset data = TradeTypeInfoQry.getTradeTypeForGrp();
        return data;
    }

    public IDataset qryTradeTypeByEpachyCodeAndprtTradeTeeTag(IData input) throws Exception
    {
        String eaprchyCode = input.getString("EPACHY_CODE");
        String tag = input.getString("PRT_TRADEFF_TAG");
        return TradeTypeInfoQry.qryTradeTypeByEpachyCodeAndprtTradeTeeTag(eaprchyCode, tag);
    }

    /**
     * 查询可返销的业务
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset queryCancelTradeType(IData input) throws Exception
    {
        String eaprchyCode = input.getString("EPACHY_CODE");
        String tradeTypeCode = input.getString("CANCEL_TYPE_CODE");
        String netTypeCode = input.getString("NET_TYPE_CODE");
        String rsrvStr1 = input.getString("RSRV_STR1");
        return TradeTypeInfoQry.queryCancelTradeType(tradeTypeCode, eaprchyCode, netTypeCode, rsrvStr1);
    }

    /**
     * 查询无线固话可返销的业务
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset queryTDCancelTradeType(IData input) throws Exception
    {
        String eaprchyCode = input.getString("EPACHY_CODE");
        String netTypeCode = input.getString("NET_TYPE_CODE");
        return TradeTypeInfoQry.queryTDCancelTradeType(eaprchyCode, netTypeCode);
    }

    /**
     * 查询可以返销的业务类型信息
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset queryUNDOTradeTypeInfos(IData input) throws Exception
    {
        String eaprchyCode = input.getString("EAPRCHY_CODE");
        return TradeTypeInfoQry.queryUNDOTradeTypeInfos(eaprchyCode);
    }

}
