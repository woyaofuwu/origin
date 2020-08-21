
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commonmethod.GrpCommonBean;

public class TradeGrpMerchInfoQrySVC extends CSBizService
{

    private static final long serialVersionUID = 1L;

    /**
     * 根据业务流水号查询所有TF_B_TRADE_GRP_MERCH信息 all merchinfo
     * 
     * @author weixb3 modify by ft 2014-07-30
     * @Description
     * @throws Exception
     * @param cycle
     */
    public IDataset qryAllMerchInfoByTradeId(IData input) throws Exception
    {

        String tradeId = input.getString("TRADE_ID");

        return TradeGrpMerchInfoQry.qryAllMerchInfoByTradeId(tradeId, getPagination());
    }

    /**
     * @Function:
     * @Description:据用户id集团编码全网商品编码查询商品信息
     * @param：
     * @return：
     * @throws：
     * @version:
     * @author:chenyi
     * @date: 下午3:32:50 2013-10-23
     */
    public IDataset qryMerchInfoByGrpUid(IData input) throws Exception
    {
        String userid = input.getString("USER_ID", "");
        String group_id = input.getString("GROUP_ID", "");
        String merch_spec_code = input.getString("MERCH_SPEC_CODE", "");
        return TradeGrpMerchInfoQry.qryMerchInfoByGrpUid(userid, group_id, merch_spec_code);
    }

    /**
     * 根据主台账表的order_id查询子台账merch表tradeId和groupId
     * 
     * @author ft
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset qryMerchInfoByMainTradeOrderId(IData param) throws Exception
    {

        String mainTradeOrderId = param.getString("ORDER_ID", "");

        return TradeGrpMerchInfoQry.qryMerchInfoByMainTradeOrderId(mainTradeOrderId);
    }

    /**
     * 根据TRADE_ID 查询有效的TF_B_TEADE_GRP_MERCH表信息 sysdate>start_date and sysdate<end_date
     * 
     * @author weixb3 modify by ft 2014-07-30
     * @Description
     * @throws Exception
     * @param cycle
     */
    public IDataset qryMerchInfoByTradeId(IData input) throws Exception
    {
        String tradeId = input.getString("TRADE_ID");

        return TradeGrpMerchInfoQry.qryMerchInfoByTradeId(tradeId, null);
    }

    /**
     * @Description:根据商品订单号查询在线台帐信息
     * @author weixb3 modify by ft 2014-07-31
     * @date 2013/8/21
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset qryMerchOnlineInfoByMerchOfferId(IData param) throws Exception
    {
        String merchOfferId = param.getString("MERCH_OFFER_ID");

        return TradeGrpMerchInfoQry.qryMerchOnlineInfoByMerchOfferId(merchOfferId, getPagination());
    }

    /**
     * 查询出TF_B_TRADE中需要有预受理BBOSS的数据
     * 
     * @author weixb3
     * @Description 特殊查询
     * @throws Exception
     * @param cycle
     */
    public IDataset queryBbossTrade(IData input) throws Exception
    {

        return TradeGrpMerchInfoQry.queryBbossTrade(input.getString("START_DATE", ""), input.getString("END_DATE", ""), input.getString("POSPECNUMBER", ""), input.getString("PRODUCTSPECNUMBER", ""), input.getString("GROUP_ID", ""), getPagination());
    }

    /**
     * @author weixb3
     * @Description 根据TRADE_ID 查询出TF_B_TRADE_GRP_MERCHP 表的数据 有效数据
     * @throws Exception
     * @param cycle
     */
    public IDataset queryTradeGrpMerchp(IData input) throws Exception
    {
        // 1- 定义返回数据
        IDataset returnVal = new DatasetList();

        // 2- 根据商品台账编号获取相应的产品台账编号
        String merchTradeId = input.getString("TRADE_ID");
        IDataset productTradeInfoList = GrpCommonBean.getProductTradeInfo(merchTradeId);
        if (IDataUtil.isEmpty(productTradeInfoList))
        {
            return new DatasetList();
        }

        // 3- 根据产品台账编号获取TF_B_TRADE_GRP_MERCHP 表的数据
        for (int i = 0; i < productTradeInfoList.size(); i++)
        {
            IData productTradeInfo = productTradeInfoList.getData(i);
            String productTradeId = productTradeInfo.getString("TRADE_ID");
            IDataset grpMerchpTradeInfoList = TradeGrpMerchpInfoQry.qryGrpMerchpByTradeId(productTradeId, getPagination());
            if (IDataUtil.isNotEmpty(grpMerchpTradeInfoList))
            {
                returnVal.addAll(grpMerchpTradeInfoList);
            }
        }

        // 4- 返回结果
        return returnVal;
    }

    /**
     * @author weixb3
     * @Description:根据trade_id更改台帐表状态
     * @throws Exception
     * @param cycle
     */
    public void updateMerchStatusByTradeId(IData input) throws Exception
    {

        TradeGrpMerchInfoQry.updateMerchStatusByTradeId(input.getString("TRADE_ID"), input.getString("STATUS"));
    }

    /**
     * @param inparams
     * @param page
     * @author weixb3
     * @return
     * @throws Exception
     */
    public IDataset updateTradeForGrpBBoss(IData input) throws Exception
    {
        TradeGrpMerchInfoQry.updateTradeForGrpBBoss(input);
        return null;
    }

}
