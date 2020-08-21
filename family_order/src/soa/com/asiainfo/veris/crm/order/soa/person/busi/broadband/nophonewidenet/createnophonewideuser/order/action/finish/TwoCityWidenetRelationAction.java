package com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.createnophonewideuser.order.action.finish;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeUserInfoQry;
import com.asiainfo.veris.crm.order.soa.person.common.util.WideNetUtil;

/**
 * 记录北京移动号码与生成的无手机宽带账号的关系
 * @author zhengkai5
 */
public class TwoCityWidenetRelationAction implements ITradeFinishAction {

    @Override
    public void executeAction(IData mainTrade) throws Exception {

        // 两城两宽标识
        String twoCityTag = mainTrade.getString("RSRV_STR7");
        if ("1".equals(twoCityTag)) {
            IData input = new DataMap();
            String startDate = SysDateMgr.getSysTime();
            String endDate = SysDateMgr.END_DATE_FOREVER;
            String instId = SeqMgr.getInstId();

            input.put("START_DATE", startDate);
            input.put("END_DATE", endDate);
            input.put("UPDATE_TIME", startDate);
            input.put("INST_ID", instId);
            input.put("TRADE_ID", mainTrade.getString("TRADE_ID"));
            input.put("ACCEPT_MONTH", mainTrade.getString("ACCEPT_MONTH"));
            input.put("SERIAL_NUMBER_B", mainTrade.getString("SERIAL_NUMBER")); // 海南宽带号码
            input.put("USER_ID_B", mainTrade.getString("USER_ID"));             // 海南宽带号码
            input.put("SERIAL_NUMBER_A", mainTrade.getString("RSRV_STR8"));     // 北京移动号码
            input.put("RELATION_TYPE_CODE", "TC");                                 // 关系类型
            input.put("MODIFY_TAG", BofConst.MODIFY_TAG_ADD);                      // 操作类型

            Dao.insert("TF_F_TWO_CITY_WIDENET", input);

            // 修改预受理表中状态
            IData param = new DataMap();
            param.put("SERIAL_NUMBER", mainTrade.getString("RSRV_STR8"));
            param.put("ACCEPT_TAG", "0");//预受理状态；
            IDataset WIDENET_SYNC = Dao.qryByCodeParser("TF_F_WIDENET_SYNC", "SEL_WIDENET_SYNC_BY_SERIALNUMBER", param);
            if (IDataUtil.isNotEmpty(WIDENET_SYNC)) {
                IData info = WIDENET_SYNC.first();
                info.put("ACCEPT_TAG", "2");
                info.put("REMARKS", "已开户");
                info.put("OPEN_DATE", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
                Dao.update("TF_F_WIDENET_SYNC", info);

                // 调北京接口通知北京宽带已经开通成功
                IData BJparam = new DataMap();
                String tradeId = mainTrade.getString("TRADE_ID");
                IDataset userInfo = TradeUserInfoQry.getTradeUserByTradeId(tradeId);
                if (IDataUtil.isNotEmpty(userInfo)) {
                    BJparam.put("PASSWORD", userInfo.first().getString("RSRV_STR2"));  // 宽带密码明文
                }
                BJparam.put("SERIAL_NUMBER", mainTrade.getString("RSRV_STR8"));        // 北京移动号码
                BJparam.put("BROADBAND_NEMBER", mainTrade.getString("SERIAL_NUMBER")); // 宽带号码
                BJparam.put("WIDENET_ADDR", info.getString("WIDENET_ADDR"));           // 宽带地址
                BJparam.put("OFFER", info.getString("DISCNT_TYPE"));                   // 宽带套餐名称
                BJparam.put("RESULTCODE", "0");            // 回单结果
                BJparam.put("RESULTINFO", "宽带开户成功！"); // 回单信息
                IData abilityResult = WideNetUtil.buildAbilityData(BJparam);

                if ("00000".equals(abilityResult.getString("resCode"))) {
                    IData BJRetInfo = new DataMap(abilityResult.getString("result"));
                    if (!"00000".equals(BJRetInfo.getString("X_RESULTCODE")))
                        CSAppException.apperr(CrmCommException.CRM_COMM_103, "两城两宽调用北京回单接口失败：" + BJRetInfo.getString("X_RESULTINFO"));
                } else {
                    CSAppException.apperr(CrmCommException.CRM_COMM_103, "两城两宽调用能力开放平台失败：" + abilityResult.getString("resMsg"));
                }
            }
        }
    }
}
