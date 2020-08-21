package com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.createnophonewideuser.order.action.undofinish;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.person.common.util.WideNetUtil;

public class CancelTwoCityWidenetAction implements ITradeFinishAction {

    @Override
    public void executeAction(IData mainTrade) throws Exception {

        // 修改临时受理表TF_F_WIDENET_SYNC中状态
        String twoCityTag = mainTrade.getString("RSRV_STR7"); // 两城两宽标识
        if ("1".equals(twoCityTag)) {
            // 修改预受理表中状态
            IData param = new DataMap();
            param.put("SERIAL_NUMBER", mainTrade.getString("RSRV_STR8"));
            param.put("ACCEPT_TAG", "2");//预受理状态： 0：预受理，1：撤单，2：已开通，3：已拆机
            IDataset WIDENET_SYNC = Dao.qryByCodeParser("TF_F_WIDENET_SYNC", "SEL_WIDENET_SYNC_BY_SERIALNUMBER", param);
            if (IDataUtil.isNotEmpty(WIDENET_SYNC)) {
                IData info = WIDENET_SYNC.first();
                info.put("ACCEPT_TAG", "1");
                info.put("REMARKS", "已撤单");
                info.put("CANCEL_DATE", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
                Dao.update("TF_F_WIDENET_SYNC", info);

                // 调用北京接口通知北京用户已取消宽带
                IData BJparam = new DataMap();
                BJparam.put("SERIAL_NUMBER", mainTrade.getString("RSRV_STR8"));        // 北京移动号码
                BJparam.put("BROADBAND_NEMBER", mainTrade.getString("SERIAL_NUMBER")); // 宽带号码
                BJparam.put("WIDENET_ADDR", info.getString("WIDENET_ADDR"));           // 宽带地址
                BJparam.put("OFFER", info.getString("DISCNT_TYPE"));                   // 宽带套餐名称
                BJparam.put("PASSWORD", "111111");                                        // 宽带密码
                BJparam.put("RESULTCODE", "99");           // 回单结果
                BJparam.put("RESULTINFO", "用户主动撤单！"); // 回单信息
                IData abilityResult = WideNetUtil.buildAbilityData(BJparam);

                if ("00000".equals(abilityResult.getString("resCode"))) {
                    IData BJRetInfo = new DataMap(abilityResult.getString("result"));
                    if (!"00000".equals(BJRetInfo.getString("X_RESULTCODE")))
                        CSAppException.apperr(CrmCommException.CRM_COMM_103, "两城两宽调用北京撤单接口失败：" + BJRetInfo.getString("X_RESULTINFO"));
                } else {
                    CSAppException.apperr(CrmCommException.CRM_COMM_103, "两城两宽调用能力开放平台失败：" + abilityResult.getString("resMsg"));
                }
            }
        }
    }
}
