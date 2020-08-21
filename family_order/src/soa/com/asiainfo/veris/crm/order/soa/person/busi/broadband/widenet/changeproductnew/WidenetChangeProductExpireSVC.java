
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.changeproductnew;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.trade.UTradeHisInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeProductInfoQry;

/**
 * 宽带预约产品变更到期提醒处理服务
 * 
 * @author chenzm
 */
public class WidenetChangeProductExpireSVC extends CSBizService
{

    public void dealExpire(IData mainTrade) throws Exception
    {
        String tradeId = mainTrade.getString("TRADE_ID");
        String userId = mainTrade.getString("USER_ID");

        // 查历史台账 如存在未返销的 才进行处理
        IData mainHiTrade = UTradeHisInfoQry.qryTradeHisByPk(tradeId, "0", null);

        if (IDataUtil.isNotEmpty(mainHiTrade))
        {
            // 宽带预约产品变更TI_B_USER同步处理
            this.syncUserInfo(tradeId, userId);
        }
    }

    /**
     * @Description: 是否产品变更
     * @param tradeId
     * @return
     * @throws Exception
     */
    public boolean isProductChange(String tradeId) throws Exception
    {
        IDataset productTrade = TradeProductInfoQry.getTradeProductBySelByTradeModify(tradeId, BofConst.MODIFY_TAG_ADD);
        if (IDataUtil.isNotEmpty(productTrade))
        {
            for (int i = 0; i < productTrade.size(); i++)
            {
                if ("1".equals(productTrade.getData(i).getString("MAIN_TAG")))
                {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @Description: 同步用户主产品、品牌信息给账务侧
     * @author chenzm
     * @param tradeId
     * @param userId
     * @throws Exception
     */
    public void syncUserInfo(String tradeId, String userId) throws Exception
    {
        if (this.isProductChange(tradeId))
        {
            String syncSequence = SeqMgr.getSyncIncreIdDB();
            String curDay = SysDateMgr.getCurDay();

            StringBuilder querySQL = new StringBuilder();
            
            querySQL.append("SELECT '" + syncSequence + "' SYNC_SEQUENCE,'" + curDay + "' SYNC_DAY,'8' MODIFY_TAG,'" + tradeId + "' TRADE_ID, MOD(B.USER_ID, 10000) PARTITION_ID, B.USER_ID, B.CUST_ID, B.USECUST_ID, ");
            querySQL.append("B.EPARCHY_CODE,B.CITY_CODE, B.CITY_CODE_A, B.USER_PASSWD, B.USER_DIFF_CODE, B.USER_TYPE_CODE, ");
            querySQL.append("B.USER_TAG_SET, B.USER_STATE_CODESET, B.NET_TYPE_CODE, B.SERIAL_NUMBER, B.CONTRACT_ID, B.ACCT_TAG, ");
            querySQL.append("B.PREPAY_TAG, B.MPUTE_MONTH_FEE, B.MPUTE_DATE, B.FIRST_CALL_TIME, B.LAST_STOP_TIME, B.CHANGEUSER_DATE,");
            querySQL.append("B.IN_NET_MODE, B.IN_DATE, B.IN_STAFF_ID,B.IN_DEPART_ID, B.OPEN_MODE, B.OPEN_DATE, B.OPEN_STAFF_ID, ");
            querySQL.append("B.OPEN_DEPART_ID, B.DEVELOP_STAFF_ID, B.DEVELOP_DATE, B.DEVELOP_DEPART_ID, B.DEVELOP_CITY_CODE, ");
            querySQL.append("B.DEVELOP_EPARCHY_CODE, B.DEVELOP_NO, B.ASSURE_CUST_ID, B.ASSURE_TYPE_CODE,B.ASSURE_DATE, B.REMOVE_TAG, ");
            querySQL.append("B.PRE_DESTROY_TIME, B.DESTROY_TIME,B.REMOVE_EPARCHY_CODE, B.REMOVE_CITY_CODE, B.REMOVE_DEPART_ID, ");
            querySQL.append("B.REMOVE_REASON_CODE,B.UPDATE_TIME, B.UPDATE_STAFF_ID, B.UPDATE_DEPART_ID, B.REMARK, B.RSRV_NUM1, ");
            querySQL.append("B.RSRV_NUM2, B.RSRV_NUM3, B.RSRV_NUM4, B.RSRV_NUM5, B.RSRV_STR1, B.RSRV_STR2,B.RSRV_STR3, B.RSRV_STR4, ");
            querySQL.append("B.RSRV_STR5, B.RSRV_STR6, B.RSRV_STR7, B.RSRV_STR8,B.RSRV_STR9, B.RSRV_STR10, B.RSRV_DATE1, B.RSRV_DATE2, ");
            querySQL.append("B.RSRV_DATE3, B.RSRV_TAG1,B.RSRV_TAG2, B.RSRV_TAG3, C.BRAND_CODE , C.PRODUCT_ID ");
            querySQL.append("FROM TF_F_USER B,  TF_F_USER_INFOCHANGE C ");
            querySQL.append("WHERE B.USER_ID = '" + userId + "' ");
            querySQL.append("AND B.USER_ID = C.USER_ID ");
            querySQL.append("AND C.PARTITION_ID = MOD(B.USER_ID, 10000)");
            querySQL.append("AND SYSDATE BETWEEN C.START_DATE AND C.END_DATE");

            IData userParam = new DataMap();
            userParam.put("USER_ID", userId);
            
            IDataset results =Dao.qryBySql(querySQL,userParam);
            
            if (IDataUtil.isNotEmpty(results))
            {
                for (int i = 0; i < results.size(); i++)
                {
                    results.getData(i).put("SCORE_VALUE", "0");
                    results.getData(i).put("BASIC_CREDIT_VALUE", "0");
                    results.getData(i).put("CREDIT_VALUE", "0");
                    results.getData(i).put("CREDIT_CLASS", "0");
                }
                    
                Dao.insert("TI_B_USER", results, Route.getJourDbDefault());
                
                IData syncData = new DataMap();
                syncData.put("SYNC_SEQUENCE", syncSequence);
                syncData.put("SYNC_DAY", curDay);
                syncData.put("SYNC_TYPE", "0");
                syncData.put("TRADE_ID", tradeId);
                syncData.put("STATE", "0");
                syncData.put("REMARK", "宽带产品变更到期提醒用户产品品牌同步【" + userId + "】");

                Dao.insert("TI_B_SYNCHINFO", syncData, Route.getJourDb());
            }
        }
    }
}
