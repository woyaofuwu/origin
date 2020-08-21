
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.querygroupinfo;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.exception.ProductException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bizctrl.BizCtrlBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bizctrl.BizCtrlInfo;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.bboss.GroupBBossManageDao;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeGrpMerchInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeGrpMerchpInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.dealBBossFaildOrder.DealBBossFaildOrder;

/**
 * @author weixb3
 */
public class GroupBBossManage extends CSBizBean
{
    /**
     * 取消专线订单
     * 
     * @param param
     * @throws Exception
     */
    public void cancelSend(IData param) throws Exception
    {
        IDataset merchPInfos = TradeGrpMerchpInfoQry.qryMerchpInfoByTradeIdUserIds(param.getString("TRADE_ID"), param.getString("USER_ID"), null);

        IDataset ds = new DatasetList();
        for (int i = 0; i < merchPInfos.size(); i++)
        {
            ds.add(merchPInfos.getData(i).getString("PRODUCT_ORDER_ID"));
        }

        DealBBossFaildOrder.modifyTradeInfo(ds, "0", "省BOSS取消");

        // 获取merch表，merch_order_id
        IDataset merchInfos = TradeGrpMerchInfoQry.qryMerchInfoByTradeId(param.getString("TRADE_ID"), null);

        if (IDataUtil.isEmpty(merchInfos))
        {
            CSAppException.apperr(ProductException.CRM_PRODUCT_209);
        }

        IData datamap = new DataMap();
        datamap.put("TRADE_ID", param.getString("TRADE_ID"));
        datamap.put("MERCH_ORDER_ID", merchInfos.getData(0).getString("MERCH_ORDER_ID"));

        DealBBossFaildOrder.delMerchInfo(datamap, "0", "省BOSS取消");

    }

    /**
     * 处理相关的台账信息
     * 
     * @author weixb3
     * @param param
     * @throws Exception
     */
    public void updataCreateBefor(IData param) throws Exception
    {
        GroupBBossManageDao manageDao = new GroupBBossManageDao();

        // 判断一下是否有发送了受理报文，等待BBOSS归档的专线
        IDataset ds = TradeInfoQry.queryOrderTradeByTrade(param.getString("TRADE_ID"));
        String merchUserId = ds.getData(0).getString("USER_ID");// 主台账里保存的是商品用户的ID
        for (int i = 0; i < ds.size(); i++)
        {
            IData trade = ds.getData(i);
            String rsrvStr10 = trade.getString("RSRV_STR10", "");
            if ("".equals(rsrvStr10))
                CSAppException.apperr(GrpException.CRM_GRP_420);
        }

        IData merchParam = new DataMap();
        merchParam.put("USER_ID", merchUserId);

        BizCtrlInfo bizCtrlInfo = new BizCtrlInfo();

        String productId = param.getString("PRODUCT_ID");

        // 商品用户是否已经生成资料
        IData userList = UcaInfoQry.qryUserInfoByUserIdForGrp(merchUserId);
        if (userList != null && userList.size() > 0)
        {
            param.put("MERCH_EXISTS", "true");
        }
        else
        {
            // 查询是否有专线商品新增的订单
            bizCtrlInfo = BizCtrlBean.qryProductCtrlInfo(productId, BizCtrlType.CreateUser);

            String tradeTypeCode = bizCtrlInfo.getTradeTypeCode();

            IDataset tradeList = TradeInfoQry.getUserTradeByUserID(tradeTypeCode, merchUserId, null, "0", Route.CONN_CRM_CG, null);
            if (tradeList != null && tradeList.size() > 0)
            {
                param.put("MERCH_EXISTS", "true");
            }
        }

        bizCtrlInfo = BizCtrlBean.qryProductCtrlInfo(productId, BizCtrlType.ChangeUserDis);
        String ModTradeTypeCode = bizCtrlInfo.getTradeTypeCode();

        bizCtrlInfo = BizCtrlBean.qryProductCtrlInfo(productId, BizCtrlType.CreateUser);
        String AddTradeTypeCode = bizCtrlInfo.getTradeTypeCode();

        // 如果不存商品用户 则表示以前的专线业务失败 则确保当前台账的商品用户操作类型、主台账业务类型、账户操作类型为新增
        if (!"true".equals(param.getString("MERCH_EXISTS", "")))
        {
            IData merch = new DataMap();
            merch.put("TRADE_ID", param.getString("TRADE_ID"));
            merch.put("USER_ID", merchUserId);
            merch.put("MODIFY_TAG", "0");
            merch.put("TRADE_TYPE_CODE", AddTradeTypeCode);

            manageDao.updateTradeMerch(merch);
            manageDao.updateTradeMerchUser(merch);
            manageDao.updateTradeTypeCode(merch);
            manageDao.updateTradeAcct(merch);
            param.put("MERCH_EXISTS", "true");
        }

        if ("true".equals(param.getString("MERCH_EXISTS", "")))
        {
            param.put("NEW_TRADE_TYPE_CODE", ModTradeTypeCode);
        }
        else
        {
            param.put("NEW_TRADE_TYPE_CODE", AddTradeTypeCode);
        }

        // 剩余子用户处理
        int cnt = manageDao.updateTradeUser(param);
        if (cnt > 0)
        {
            manageDao.updateTradeAttr(param);
            manageDao.updateTradeDiscnt(param);
            manageDao.updateTradeSvc(param);
            manageDao.updateTradeSvcState(param);
            manageDao.updateTradeRes(param);

            manageDao.updateTradePayRelaction(param);
            manageDao.updateTradeRelaction(param);
            manageDao.updateTradeProduct(param);

            manageDao.updateTradeMerchp(param);
            manageDao.updateTradeMerchpDiscnt(param);

            // 复制商品 商品用户 主台账 账户信息操作类型变为2
            manageDao.copyTradeMerch(param);
            manageDao.copyTradeMerchUser(param);
            manageDao.copyTradeMain(param);
            manageDao.copyTradeAcct(param);
            manageDao.copyTradeOther(param);
        }
    }

}
