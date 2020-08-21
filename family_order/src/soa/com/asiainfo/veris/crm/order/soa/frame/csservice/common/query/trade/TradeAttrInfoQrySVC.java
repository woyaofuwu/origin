
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class TradeAttrInfoQrySVC extends CSBizService
{

    private static final long serialVersionUID = 1L;

    /**
     * @description 根据台帐编号获取对应的产品参数
     * @author xunyl
     * @date 2013-11-15
     */
    public static IDataset getTradeAttrByInstType(IData inparam) throws Exception
    {
        String tradeId = inparam.getString("TRADE_ID");
        String instType = inparam.getString("INST_TYPE");
        return TradeAttrInfoQry.getTradeAttrByInstType(tradeId, instType);
    }

    /**
     * @description 根据台帐编号获取对应的产品参数
     * @author xunyl
     * @date 2013-11-15
     */
    public static IDataset getTradeAttrInfoByInstType(IData inparam) throws Exception
    {
        String tradeId = inparam.getString("TRADE_ID");
        String instType = inparam.getString("INST_TYPE");
        return TradeAttrInfoQry.getTradeAttrInfoByInstType(tradeId, instType);
    }

    /**
     * 根据属性编号和属性值查询属性台账表，判断该属性值之前是否被占用
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public static IDataset qryTradeAttrByAttrCodeAttrValue(IData input) throws Exception
    {
        String paramCode = input.getString("ATTR_CODE");
        String paramValue = input.getString("ATTR_VALUE");

        return TradeAttrInfoQry.qryTradeAttrByAttrCodeAttrValue(paramCode, paramValue);
    }

    /**
     * @authorchenyi
     * @Description 查询trade表属性
     * @throws Exception
     * @param cycle
     */
    public IDataset getAttrByTradeID(IData input) throws Exception
    {
        return TradeAttrInfoQry.getAttrByTradeID(input.getString("TRADE_ID"), input.getString("MODIFY_TAG"));
    }

    /**
     * @author chneyi
     * @Description 根据TRADE_ID ,ATTR_CODE查询出TF_B_TRADE_ATTR表的数据 查询属性
     * @throws Exception
     * @param cycle
     */
    public IDataset getTradeAttrByTradeIDandAttrCode(IData input) throws Exception
    {
        return TradeAttrInfoQry.getTradeAttrByTradeIDandAttrCode(input.getString("TRADE_ID"), input.getString("ATTR_CODE", ""), getPagination());
    }

    /**
     * @author chenyi 查询有效属性
     * @Description SEL_BY_TRADEID_INSTID
     * @throws Exception
     * @param cycle
     */
    public IDataset getTradeAttrInfoByTradeIDAttrCode(IData input) throws Exception
    {
        String tradeId = input.getString("TRADE_ID");
        String attrCode = input.getString("ATTR_CODE");
        String modifyTag = input.getString("MODIFY_TAG");
        return TradeAttrInfoQry.getTradeAttrInfoByTradeIDAttrCode(tradeId, attrCode, modifyTag);
    }

    /**
     * @author weixb3
     * @Description SEL_BY_TRADEID_INSTID
     * @throws Exception
     * @param cycle
     */
    public IDataset getUserAttrByTradeIDInstid(IData input) throws Exception
    {

        return TradeAttrInfoQry.getUserAttrByTradeIDInstid(input.getString("TRADE_ID"), input.getString("INST_TYPE"), input.getString("INST_ID"), input.getString("USER_ID"), input.getString("BBOSS_FLAG"), getPagination());
    }

    /**
     * @author weixb3
     * @Description 未注销 通过trade_id查出产品受理时添加的参数
     * @throws Exception
     * @param cycle
     */
    public IDataset getUserProductAttrValuebyTradeIdAndUserId(IData input) throws Exception
    {
        return TradeAttrInfoQry.getUserProductAttrValuebyTradeIdAndUserId(input.getString("TRADE_ID"), input.getString("INST_TYPE"), input.getString("ATTR_CODE"), input.getString("USER_ID"), getPagination());
    }

    /**
     * @author weixb3
     * @Description 通过trade_id查出产品受理时添加的参数
     * @throws Exception
     * @param cycle
     */
    public IDataset getUserProductAttrValuebyTradeIdAndUserId_ENDDATA(IData input) throws Exception
    {
        return TradeAttrInfoQry.getUserProductAttrValuebyTradeIdAndUserId_ENDDATA(input.getString("TRADE_ID"), input.getString("INST_TYPE"), input.getString("ATTR_CODE"), input.getString("USER_ID"), getPagination());
    }

    /**
     * @author weixb3
     * @Description 根据TRADE_ID INSTTYPE,USER_ID查询出TF_B_TRADE_ATTR表的数据 查询出有效的属性
     * @throws Exception
     * @param cycle
     */
    public IDataset queryTradeGrpAttrByTradeId(IData input) throws Exception
    {
        return TradeAttrInfoQry.queryTradeGrpAttrByTradeId(input.getString("TRADE_ID"), input.getString("USER_ID"), input.getString("INST_TYPE"), getPagination());
    }

    /**
     * chenyi 清空服开标识
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset updStr1ByTradeid(IData input) throws Exception
    {

        TradeAttrInfoQry.updStr1ByTradeid(input.getString("TRADE_ID"));
        return null;
    }

}
