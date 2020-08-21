
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class TradeOtherInfoQrySVC extends CSBizService
{

    private static final long serialVersionUID = 1L;

    public static IDataset queryUserOtherInfoByUserId(IData inparam) throws Exception
    {
        return TradeOtherInfoQry.queryUserOtherInfoByUserId(inparam);
    }

    /**
     * 查询出TF_B_TRADE_OTHER中状态为未操作的状态
     * 
     * @param idata
     * @return
     * @throws Exception
     */
    public IDataset queryBbossManageDetailInfo(IData idata) throws Exception
    {
        String cond_GROUP_ID = idata.getString("cond_GROUP_ID");
        String cond_OPERATE_FLAG = idata.getString("cond_OPERATE_FLAG");
        String cond_POSPECNUMBER = idata.getString("cond_POSPECNUMBER");
        String cond_START_DATE = idata.getString("cond_START_DATE");
        String cond_END_DATE = idata.getString("cond_END_DATE");
        String TRADE_ID = idata.getString("TRADE_ID");
        String cond_PRODUCTSPECNUMBER = idata.getString("cond_PRODUCTSPECNUMBER");
        return TradeOtherInfoBean.queryBbossManageDetailInfo(cond_GROUP_ID, cond_OPERATE_FLAG, cond_POSPECNUMBER, cond_START_DATE, cond_END_DATE, TRADE_ID, cond_PRODUCTSPECNUMBER,getPagination());
        //return TradeOtherInfoQry.queryBbossManageDetailInfo(cond_GROUP_ID, cond_OPERATE_FLAG, cond_POSPECNUMBER, cond_START_DATE, cond_END_DATE, TRADE_ID, cond_PRODUCTSPECNUMBER, getPagination());
    }

    /**
     * 查询esop bboss管理节点数据
     * 
     * @param idata
     * @return
     * @throws Exception
     */
    public IDataset queryBbossManageInfoByEsop(IData idata) throws Exception
    {
        return TradeOtherInfoQry.queryBbossManageInfoByEsop(idata.getString("IBSYSID"));
    }

    /**
     * @Description 根据TRADEID查询预受理登记在TF_B_TRADE_OTHER中的数据
     * @author weixb3
     * @date
     * @param param
     * @return IDataset
     * @throws Exception
     */
    public IDataset queryBbossManageInfobyTradeIdUserId(IData idata) throws Exception
    {
        String TRADE_ID = idata.getString("TRADE_ID");
        String USER_ID = idata.getString("USER_ID");
        String RSRV_VALUE_CODE = idata.getString("RSRV_VALUE_CODE");
        String MODIFY_TAG = idata.getString("MODIFY_TAG");
        return TradeOtherInfoQry.queryBbossManageInfobyTradeIdUserId(TRADE_ID, USER_ID, RSRV_VALUE_CODE, MODIFY_TAG);
    }

    /**
     * 查询IDC预受理资料子表
     * 
     * @param param
     * @return IDataset
     * @throws Exception
     */
    public IDataset queryNeedStartPreidcTradeC(IData idata) throws Exception
    {
        String SERIAL_NUMBER = idata.getString("SERIAL_NUMBER");
        String CUST_NAME = idata.getString("CUST_NAME");
        String START_DATE = idata.getString("START_DATE");
        String END_DATE = idata.getString("END_DATE");
        String BUIS_NAME = idata.getString("BUIS_NAME");
        return TradeOtherInfoQry.queryNeedStartPreidcTradeC(SERIAL_NUMBER, CUST_NAME, START_DATE, END_DATE, BUIS_NAME, getPagination());
    }

    /**
     * chenyi 2014-7-25 工单状态查询
     * 
     * @param idata
     * @return
     * @throws Exception
     */
    public IDataset queryPoTradeState(IData idata) throws Exception
    {
        String SYNC_SEQUENCE = idata.getString("SYNC_SEQUENCE");
        return TradeOtherInfoQry.queryPoTradeState(SYNC_SEQUENCE);
    }

    /**
     * 根据条件参数查询出工单状态信息
     * 
     * @param param
     * @return IDataset
     * @throws Exception
     */
    public IDataset queryPoTradeStateAttr(IData idata) throws Exception
    {
        String SYNC_SEQUENCE = idata.getString("SYNC_SEQUENCE");
        String INFO_TYPE = idata.getString("INFO_TYPE");
        String INFO_TAG = idata.getString("INFO_TAG");
        String ORDERING_ID = idata.getString("ORDERING_ID");
        return TradeOtherInfoQry.queryPoTradeStateAttr(SYNC_SEQUENCE, INFO_TYPE, INFO_TAG, ORDERING_ID);
    }

    /**
     * 查询idc预约参数
     * 
     * @param idata
     * @return
     * @throws Exception
     */
    public IDataset queryPreidcIdcParams(IData idata) throws Exception
    {
        String trade_id = idata.getString("TRADE_ID");
        String subscribeType = idata.getString("SUBSCRIBE_TYPE");
        return TradeOtherInfoQry.queryPreidcIdcParams(trade_id, subscribeType);
    }

    /**
     * 根据条件参数查询出工单状态信息
     * 
     * @param param
     * @return IDataset
     * @throws Exception
     */
    public IDataset queryProvCprt(IData idata) throws Exception
    {
        String EC_CODE = idata.getString("EC_CODE");
        String MERCH_ORDER_ID = idata.getString("MERCH_ORDER_ID");
        String MERCH_SPEC_CODE = idata.getString("MERCH_SPEC_CODE");
        String IF_PROVCPRT = idata.getString("IF_PROVCPRT");
        String IF_ANS = idata.getString("IF_ANS");
        String SYNC_STATE = idata.getString("SYNC_STATE");
        String START_DATE = idata.getString("START_DATE");
        String END_DATE = idata.getString("END_DATE");
        return TradeOtherInfoQry.queryProvCprt(EC_CODE, MERCH_ORDER_ID, MERCH_SPEC_CODE, IF_PROVCPRT, IF_ANS, SYNC_STATE, START_DATE, END_DATE, getPagination());
    }

    /**
     * 查询IDC预受理资料单条详细情况
     * 
     * @param param
     * @return IDataset
     * @throws Exception
     */
    public IDataset queryStartPreidcByTrade(IData idata) throws Exception
    {
        String trade_id = idata.getString("TRADE_ID");
        return TradeOtherInfoQry.queryStartPreidcByTrade(trade_id);
    }

    /**
     * 查询工单订购的子业务数
     * 
     * @param param
     * @return IDataset
     * @throws Exception
     */
    public IDataset querySubscribeTypeByTradeId(IData idata) throws Exception
    {
        String trade_id = idata.getString("TRADE_ID");
        return TradeOtherInfoQry.querySubscribeTypeByTradeId(trade_id);
    }

    /**
     * 根据TradeId 和 Rsrv_Value_Code 查询出TF_B_TRADE_OTHRE数据
     * 
     * @param param
     * @return IDataset
     * @throws Exception
     */
    public IDataset queryTradeOtherByTradeIdAndRsrvValueCode(IData idata) throws Exception
    {
        String trade_id = idata.getString("TRADE_ID");
        String rsrv_value_code = idata.getString("RSRV_VALUE_CODE");
        return TradeOtherInfoQry.queryTradeOtherByTradeIdAndRsrvValueCode(trade_id, rsrv_value_code);
    }
}
