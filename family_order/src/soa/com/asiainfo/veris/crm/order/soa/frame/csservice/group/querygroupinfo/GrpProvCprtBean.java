
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.querygroupinfo;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.bboss.GrpProvCprtDAO;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.bboss.OrderOpenDao;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.GrpInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeGrpMerchInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeGrpMerchpInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeProductInfoQry;

/**
 * TODO BBOSS工单状态 业务处理类
 * 
 * @author jch
 */
/*
 * 修改历史 $Log: GrpProvCprtBean.java,v $ 修改历史 Revision 1.27 2013/04/09 18:26:16 luoy 修改历史 *** empty log message *** 修改历史
 * 修改历史 Revision 1.26 2013/04/05 18:06:57 luoy 修改历史 *** empty log message *** 修改历史 修改历史 Revision 1.25 2013/04/05
 * 16:40:11 luoy 修改历史 *** empty log message *** 修改历史 修改历史 Revision 1.24 2013/04/05 08:30:31 luoy 修改历史 *** empty log
 * message *** 修改历史 修改历史 Revision 1.23 2013/04/04 15:53:02 luoy 修改历史 *** empty log message *** 修改历史 修改历史 Revision 1.22
 * 2013/04/03 17:32:03 luoy 修改历史 *** empty log message *** 修改历史 修改历史 Revision 1.21 2013/04/03 16:22:28 luoy 修改历史 ***
 * empty log message *** 修改历史 修改历史 Revision 1.20 2013/04/03 14:34:11 luoy 修改历史 *** empty log message *** 修改历史 修改历史
 * Revision 1.19 2013/04/01 17:56:38 luoy 修改历史 *** empty log message *** 修改历史 修改历史 Revision 1.18 2013/04/01 02:50:11
 * luoy 修改历史 *** empty log message *** 修改历史 修改历史 Revision 1.17 2013/03/26 08:01:33 luoy 修改历史 *** empty log message ***
 * 修改历史 修改历史 Revision 1.16 2013/03/24 10:35:59 luoy 修改历史 *** empty log message *** 修改历史 修改历史 Revision 1.15 2013/03/19
 * 07:03:24 luoy 修改历史 *** empty log message *** 修改历史 修改历史 Revision 1.14 2013/03/18 04:08:30 luoy 修改历史 *** empty log
 * message *** 修改历史 修改历史 Revision 1.13 2013/03/17 09:55:25 luoy 修改历史 *** empty log message *** 修改历史 修改历史 Revision 1.12
 * 2013/03/16 21:34:03 luoy 修改历史 *** empty log message *** 修改历史 修改历史 Revision 1.11 2013/03/05 10:17:07 luoy 修改历史 ***
 * empty log message *** 修改历史 修改历史 Revision 1.10 2013/03/05 05:34:45 luoy 修改历史 *** empty log message *** 修改历史 修改历史
 * Revision 1.9 2013/03/04 15:49:35 luoy 修改历史 *** empty log message *** 修改历史 修改历史 Revision 1.8 2013/03/01 19:44:10 luoy
 * 修改历史 *** empty log message *** 修改历史 修改历史 Revision 1.7 2013/03/01 12:09:52 huyong 修改历史 *** empty log message *** 修改历史
 * 修改历史 Revision 1.6 2013/02/28 18:46:37 luoy 修改历史 *** empty log message *** 修改历史 修改历史 Revision 1.5 2013/02/28 12:12:56
 * huyong 修改历史 *** empty log message *** 修改历史 修改历史 Revision 1.4 2013/02/28 05:10:30 luoy 修改历史 *** empty log message ***
 * 修改历史 修改历史 Revision 1.3 2013/02/28 04:32:51 luoy 修改历史 *** empty log message *** 修改历史 修改历史 Revision 1.2 2013/02/28
 * 04:19:37 luoy 修改历史 *** empty log message *** 修改历史 修改历史 Revision 1.1 2013/02/27 11:11:03 huyong 修改历史 *** empty log
 * message *** 修改历史 修改历史 Revision 1.61 2013/02/27 07:50:13 huyong 修改历史 *** empty log message *** 修改历史 修改历史 Revision 1.60
 * 2013/02/23 16:10:13 luoy 修改历史 *** empty log message *** 修改历史 修改历史 Revision 1.59 2013/02/21 09:43:00 luoy 修改历史 ***
 * empty log message *** 修改历史 修改历史 Revision 1.58 2013/02/21 08:29:51 luoy 修改历史 *** empty log message *** 修改历史 修改历史
 * Revision 1.57 2013/02/21 06:49:51 luoy 修改历史 *** empty log message *** 修改历史 修改历史 Revision 1.56 2013/02/20 17:26:20
 * luoy 修改历史 *** empty log message *** 修改历史 修改历史 Revision 1.55 2013/02/20 05:44:06 huyong 修改历史 *** empty log message ***
 * 修改历史 修改历史 Revision 1.54 2013/02/19 12:00:29 huyong 修改历史 *** empty log message *** 修改历史 修改历史 Revision 1.53 2013/02/19
 * 06:42:51 huyong 修改历史 *** empty log message *** 修改历史 修改历史 Revision 1.52 2013/02/19 06:02:33 huyong 修改历史 *** empty log
 * message *** 修改历史 修改历史 Revision 1.51 2013/02/19 03:58:39 luoy 修改历史 *** empty log message *** 修改历史 修改历史 Revision 1.50
 * 2013/01/31 17:30:27 luoy 修改历史 *** empty log message *** 修改历史 修改历史 Revision 1.49 2013/01/31 16:30:10 luoy 修改历史 ***
 * empty log message *** 修改历史 修改历史 Revision 1.48 2013/01/28 10:25:05 luoy 修改历史 *** empty log message *** 修改历史 修改历史
 * Revision 1.47 2013/01/23 03:36:19 lifen 修改历史 *** empty log message *** 修改历史 修改历史 Revision 1.46 2012/12/31 06:17:49
 * luoy 修改历史 *** empty log message *** 修改历史 修改历史 Revision 1.45 2012/12/31 04:36:42 luoy 修改历史 *** empty log message ***
 * 修改历史 修改历史 Revision 1.44 2012/12/26 10:06:58 luoy 修改历史 *** empty log message *** 修改历史 修改历史 Revision 1.43 2012/12/26
 * 02:24:55 luoy 修改历史 *** empty log message *** 修改历史 修改历史 Revision 1.42 2012/12/25 09:28:18 luoy 修改历史 *** empty log
 * message *** 修改历史 修改历史 Revision 1.41 2012/12/25 06:09:13 luoy 修改历史 *** empty log message *** 修改历史 修改历史 Revision 1.40
 * 2012/12/24 03:28:45 luoy 修改历史 *** empty log message *** 修改历史 修改历史 Revision 1.39 2012/12/24 02:34:37 luoy 修改历史 ***
 * empty log message *** 修改历史 修改历史 Revision 1.38 2012/12/14 03:43:23 luoy 修改历史 *** empty log message *** 修改历史 修改历史
 * Revision 1.37 2012/12/05 03:43:04 luoy 修改历史 *** empty log message *** 修改历史 修改历史 Revision 1.36 2012/12/05 02:38:39
 * luoy 修改历史 *** empty log message *** 修改历史 修改历史 Revision 1.35 2012/11/28 17:25:39 luoy 修改历史 *** empty log message ***
 * 修改历史 修改历史 Revision 1.34 2012/11/22 01:48:53 luoy 修改历史 *** empty log message *** 修改历史 修改历史 Revision 1.33 2012/11/20
 * 07:36:14 huyong 修改历史 *** empty log message *** 修改历史 修改历史 Revision 1.32 2012/11/20 02:34:39 huyong 修改历史 *** empty log
 * message *** 修改历史 修改历史 Revision 1.31 2012/11/19 03:39:17 luoy 修改历史 *** empty log message *** 修改历史 修改历史 Revision 1.30
 * 2012/11/13 09:34:49 luoy 修改历史 *** empty log message *** 修改历史 修改历史 Revision 1.29 2012/11/12 18:38:46 luoy 修改历史 ***
 * empty log message *** 修改历史 修改历史 Revision 1.28 2012/11/12 16:30:36 luoy 修改历史 *** empty log message *** 修改历史 修改历史
 * Revision 1.27 2012/11/12 15:38:52 luoy 修改历史 *** empty log message *** 修改历史 修改历史 Revision 1.26 2012/11/11 17:57:27
 * luoy 修改历史 *** empty log message *** 修改历史 修改历史 Revision 1.25 2012/11/08 04:36:14 luoy 修改历史 *** empty log message ***
 * 修改历史 修改历史 Revision 1.24 2012/11/07 10:30:51 luoy 修改历史 *** empty log message *** 修改历史 修改历史 Revision 1.23 2012/11/06
 * 10:11:51 luoy 修改历史 *** empty log message *** 修改历史 修改历史 Revision 1.22 2012/11/06 09:45:16 luoy 修改历史 *** empty log
 * message *** 修改历史 修改历史 Revision 1.21 2012/11/06 07:28:06 luoy 修改历史 *** empty log message *** 修改历史 修改历史 Revision 1.20
 * 2012/10/28 09:33:15 luoy 修改历史 *** empty log message *** 修改历史 修改历史 Revision 1.19 2012/10/23 13:25:00 luoy 修改历史 ***
 * empty log message *** 修改历史 修改历史 Revision 1.18 2012/10/22 04:02:24 luoy 修改历史 *** empty log message *** 修改历史 修改历史
 * Revision 1.17 2012/10/19 10:10:06 luoy 修改历史 *** empty log message *** 修改历史 修改历史 Revision 1.16 2012/10/09 17:59:23
 * luoy 修改历史 *** empty log message *** 修改历史 修改历史 Revision 1.15 2012/10/09 09:27:04 luoy 修改历史 *** empty log message ***
 * 修改历史 修改历史 Revision 1.14 2012/10/09 08:41:47 luoy 修改历史 *** empty log message *** 修改历史 修改历史 Revision 1.13 2012/10/09
 * 07:26:10 luoy 修改历史 *** empty log message *** 修改历史 修改历史 Revision 1.12 2012/10/09 04:58:52 luoy 修改历史 *** empty log
 * message *** 修改历史 修改历史 Revision 1.11 2012/10/09 03:42:31 luoy 修改历史 *** empty log message *** 修改历史 修改历史 Revision 1.10
 * 2012/10/09 01:57:06 luoy 修改历史 *** empty log message *** 修改历史 修改历史 Revision 1.9 2012/10/08 17:03:40 luoy 修改历史 ***
 * empty log message *** 修改历史 修改历史 Revision 1.8 2012/10/08 08:36:13 luoy 修改历史 *** empty log message *** 修改历史 修改历史
 * Revision 1.7 2012/10/08 07:16:33 luoy 修改历史 *** empty log message *** 修改历史 修改历史 Revision 1.6 2012/10/07 18:15:02 luoy
 * 修改历史 *** empty log message *** 修改历史 修改历史 Revision 1.5 2012/09/29 05:53:04 luoy 修改历史 *** empty log message *** 修改历史
 * 修改历史 Revision 1.4 2012/09/29 02:18:18 luoy 修改历史 *** empty log message *** 修改历史 修改历史 Revision 1.3 2012/09/28 08:45:16
 * luoy 修改历史 *** empty log message *** 修改历史 修改历史 Revision 1.2 2012/09/28 08:19:15 luoy 修改历史 *** empty log message ***
 * 修改历史 Revision 1.1 2012/09/27 04:12:10 luoy *** empty log message *** Revision 1.2 2012/01/05 09:12:29 jiangch ***
 * empty log message *** Revision 1.1 2011/12/31 06:44:16 jiangch *** empty log message ***
 */
public class GrpProvCprtBean
{

    /**
     * @Description: 根据传入的para 插入到TF_PO_TRADE_State
     * @author jch
     * @date
     * @param
     * @param
     * @param
     * @return
     * @throws Exception
     */
    public boolean inserPotradeState(IData para) throws Exception
    {

        return OrderOpenDao.inserPotradeState(para);
    }

    /**
     * @Description: 根据传入的属性结果 IDataset 里面含DataMap 插入TF_B_POTRADE_STATE_ATTR
     * @author jch
     * @date
     * @param
     * @param
     * @param
     * @return
     * @throws Exception
     */
    public void inserPotradeStateAttr(IDataset paras) throws Exception
    {

        OrderOpenDao.inserPotradeStateAttr(paras);
    }

    /**
     * @Description: 根据传入的para 插入记录TF_PRODUCT_TRADE 表
     * @author jch
     * @date
     * @param
     * @param
     * @param
     * @return
     * @throws Exception
     */
    public boolean inserProductTrade(IData para) throws Exception
    {

        return OrderOpenDao.inserProductTrade(para);
    }

    /**
     * @Description: 根据EC_CODE 查询出TF_F_CUST_GROUP表的集团信息，走cg库
     * @author jch
     * @date
     * @param
     * @param
     * @param
     * @return
     * @throws Exception
     */
    public IDataset queryCustGroupInfoByMpCustCode(IData param) throws Exception
    {
        return GrpInfoQry.queryCustGroupInfoByMpCustCode(param.getString("MP_GROUP_CUST_CODE"), null);

    }

    /**
     * @Description: 根据TRADE 查询出TF_B_TRADE_DISCNT 内关联TD_B_DISTINCT 信息
     * @author jch
     * @date
     * @param
     * @param
     * @param
     * @return
     * @throws Exception
     */
    public IDataset queryDiscntTrade(IData param) throws Exception
    {
        return TradeDiscntInfoQry.queryDiscntTrade(param.getString("TRADE_ID"), param.getString("USER_ID"), null);

    }

    /**
     * @Description: 根据TRADE_ID 查询出TF_B_TRADE_MERCH表的信息
     * @author jch
     * @date
     * @param
     * @param
     * @param
     * @return
     * @throws Exception
     */
    public IDataset queryMerchTradebyTradeId(IData param, Pagination pagination) throws Exception
    {
        return TradeGrpMerchInfoQry.qryMerchInfoByTradeId(param.getString("TRADE_ID"), pagination);

    }

    /**
     * @Description: 查询出TF_B_POTRADE_STATE 表中的 信息
     * @author jch
     * @date
     * @param
     * @param
     * @param
     * @return
     * @throws Exception
     */
    public IDataset queryPoTradeState(IData param) throws Exception
    {
        return TradeOtherInfoQry.queryPoTradeState(param.getString("SYNC_SEQUENCE"));

    }

    /**
     * @Description: 根据 SYNC_SEQUENCE and INFO_TAG ,INTO_TYPE,ORDERING_ID 查询出 TF_B_POTRADE_STATE_ATTR 表的信息INFO_TAG P 产品
     *               O商品 INFO_TYPE CUST_MANAGER客户经理 RespPerson 当前状态负责人信息 SUB_TYPE_INFO 代表组 ORDERING_ID BBOSS产品订单号
     * @author jch
     * @date
     * @param
     * @param
     * @param
     * @return
     * @throws Exception
     */
    public IDataset queryPoTradeStateAttr(IData param) throws Exception
    {
        return TradeOtherInfoQry.queryPoTradeStateAttr(param.getString("SYNC_SEQUENCE"), param.getString("INFO_TYPE"), param.getString("INFO_TAG"), param.getString("ORDERING_ID"));
    }

    /**
     * @Description: 根据集团BBOSS产品订单号和同步序列号 查询出 跨省工单状态产品表(TF_B_PRODUCTTRADE)
     * @author jch
     * @date
     * @param
     * @param
     * @param
     * @return
     * @throws Exception
     */
    public IDataset queryProductTrade(IData param) throws Exception
    {
        return TradeProductInfoQry.queryProductTrade(param.getString("SYNC_SEQUENCE"), param.getString("ORDER_NUMBER"));

    }

    /**
     * @Description: 查询工单状态
     * @author jch
     * @date
     * @param
     * @param
     * @param
     * @return
     * @throws Exception
     */
    public IDataset queryProvCprt(IData param, Pagination pagination) throws Exception
    {
        return TradeOtherInfoQry.queryProvCprt(param.getString("EC_CODE"), param.getString("MERCH_ORDER_ID"), param.getString("MERCH_SPEC_CODE"), param.getString("IF_PROVCPRT"), param.getString("IF_ANS"), param.getString("SYNC_STATE"), param
                .getString("START_DATE"), param.getString("END_DATE"), pagination);
    }

    /**
     * @Description: 根据TRADE_ID INSTTYPE,USER_ID查询出TF_B_TRADE_ATTR表的数据 查询出有效的属性
     * @author jch
     * @date
     * @param
     * @param
     * @param
     * @return
     * @throws Exception
     */
    public IDataset queryTradeGrpAttrByTradeId(IData param) throws Exception
    {
        return TradeAttrInfoQry.queryTradeGrpAttrByTradeId(param.getString("TRADE_ID"), param.getString("USER_ID"), param.getString("INST_TYPE"), null);
    }

    /**
     * @Description: 根据TRADE_ID 查询出TF_B_TRADE_GRP_MERCHP 表的数据 有效数据
     * @author jch
     * @date
     * @param
     * @param
     * @param
     * @return
     * @throws Exception
     */
    public IDataset queryTradeGrpMerchp(IData param) throws Exception
    {
        return TradeGrpMerchpInfoQry.qryGrpMerchpByTradeId(param.getString("TRADE_ID"), null);
    }

    /**
     * @Description: 根据TradeId 和 Rsrv_Value_Code 查询出TF_B_TRADE_OTHRE数据
     * @author jch
     * @date
     * @param
     * @param
     * @param
     * @return
     * @throws Exception
     */
    public IDataset queryTradeOtherByTradeIdAndRsrvValueCode(IData param) throws Exception
    {
        return TradeOtherInfoQry.queryTradeOtherByTradeIdAndRsrvValueCode(param.getString("TRADE_ID"), param.getString("RSRV_VALUE_CODE"));

    }

    /**
     * @Description: 根据TRADE_ID取台帐产品表信息
     * @author jch
     * @date
     * @param
     * @param
     * @param
     * @return
     * @throws Exception
     */
    public IDataset queryTradeProdbyTradeId(IData param, Pagination pagination) throws Exception
    {

        return TradeProductInfoQry.getTradeProductByTradeId(param.getString("TRADE_ID", ""));

    }

    /**
     * @Description:根据SYNC_SEQUENCE 更改 IF_ANS 是否反馈 和 保存RSRV_STR1 字段为 下一个节点的序列号
     * @author jch
     * @date
     * @param
     * @param
     * @param
     * @return
     * @throws Exception
     */
    public void updatePoTradeState(IData parm) throws Exception
    {

        GrpProvCprtDAO.updatePoTradeState(parm.getString("IF_ANS"), parm.getString("NEXT_SYNC_SEQUENCE"), parm.getString("SYNC_SEQUENCE"));
    }

}
