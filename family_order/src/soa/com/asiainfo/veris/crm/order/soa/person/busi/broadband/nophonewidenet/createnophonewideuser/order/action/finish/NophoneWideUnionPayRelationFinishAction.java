package com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.createnophonewideuser.order.action.finish;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;

/**
 *  无手机宽带开户一机多宽统付，生成“统付主号码”与 本次业务无手机宽带的统付关系
 * @author luys
 * */
public class NophoneWideUnionPayRelationFinishAction implements ITradeFinishAction {

    @Override
    public void executeAction(IData mainTrade) throws Exception {

        String tradeId = mainTrade.getString("TRADE_ID");

        IDataset tradeOtherInfos = TradeOtherInfoQry.getTradeOtherByTradeIdRsrvCode(tradeId, "ONESN_MANYWIDE");
        if (IDataUtil.isNotEmpty(tradeOtherInfos)) {
            IData tradeOtherInfo=tradeOtherInfos.getData(0);
            // 增加统一付费UU关系
            createRelationUU(tradeOtherInfo,mainTrade);
//            // 付费关系  完工插表不会同步账务，不会统付，挪到登记action   modify_by_duhj_kd 20200526
//            createPayRelation(tradeOtherInfo,mainTrade);
        }
    }
    /**
     * 付费关系
     *
     * @param mainTrade
     * @param tradeOtherInfo
     * @throws Exception
     */
    private void createPayRelation(IData tradeOtherInfo,IData mainTrade) throws Exception
    {
        String paySn = tradeOtherInfo.getString("RSRV_STR1","");
        IDataset payUsers = UserInfoQry.getEffUserInfoBySn(paySn,"0",null);
        if(IDataUtil.isEmpty(payUsers)){
            CSAppException.appError("-1", "付费号码"+paySn+"无有效用户资料！");
        }
        IData mainPayRelations = UcaInfoQry.qryDefaultPayRelaByUserId(payUsers.getData(0).getString("USER_ID"));

        if (IDataUtil.isEmpty(mainPayRelations))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "付费号码无默认付费帐户！");
        }
        IData input  = new DataMap();
        String strUserId=mainTrade.getString("USER_ID","");
        input.put("USER_ID",strUserId);
        input.put("PARTITION_ID",strUserId.substring(strUserId.length() - 4));
        input.put("ACCT_ID",mainPayRelations.getString("ACCT_ID"));
        input.put("PAYITEM_CODE","40001");
        input.put("ACCT_PRIORITY","0");
        input.put("USER_PRIORITY","0");
        input.put("BIND_TYPE","1");
        input.put("START_CYCLE_ID",SysDateMgr.getNowCycle());
        input.put("END_CYCLE_ID",SysDateMgr.getEndCycle20501231());
        input.put("ACT_TAG","1");
        input.put("DEFAULT_TAG","0");
        input.put("LIMIT_TYPE","0");
        input.put("LIMIT","0");
        input.put("COMPLEMENT_TAG","0");
        input.put("INST_ID",SeqMgr.getInstId());
        Dao.insert("TF_A_PAYRELATION",input);
    }
    /**
     * 增加统一付费UU关系
     * @param mainTrade
     * @param tradeOtherInfo
     * @throws Exception
     */
    private void createRelationUU(IData tradeOtherInfo,IData mainTrade) throws Exception
    {
        String paySn = tradeOtherInfo.getString("RSRV_STR1","");
        IDataset payUsers = UserInfoQry.getEffUserInfoBySn(paySn,"0",null);
        if(IDataUtil.isEmpty(payUsers)){
            CSAppException.appError("-1", "付费号码"+paySn+"无有效用户资料！");
        }
        IData input  = new DataMap();
        String strUserId=mainTrade.getString("USER_ID","");
        input.put("USER_ID_A",strUserId);
        String strUserIdB=payUsers.getData(0).getString("USER_ID", "");
        input.put("USER_ID_B",strUserIdB);
        input.put("PARTITION_ID",strUserIdB.substring(strUserIdB.length() - 4));
        input.put("SERIAL_NUMBER_A",mainTrade.getString("SERIAL_NUMBER",""));
        input.put("SERIAL_NUMBER_B",paySn);
        input.put("RELATION_TYPE_CODE","58");
        input.put("ROLE_CODE_A","0");
        input.put("ROLE_CODE_B","1");
        input.put("INST_ID",SeqMgr.getInstId());
        input.put("START_DATE",mainTrade.getString("ACCEPT_DATE",""));
        input.put("END_DATE",SysDateMgr.getTheLastTime());
        Dao.insert("TF_F_RELATION_UU",input);
    }
}
