
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetchangemodifyacct.order.action;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;

public class ModifyUserInfoChange implements ITradeFinishAction
{

    public void executeAction(IData mainTrade) throws Exception
    {
        String userId = mainTrade.getString("USER_ID");
        String tradeId = mainTrade.getString("TRADE_ID");
        String tradeTypeCode = mainTrade.getString("TRADE_TYPE_CODE");
        String newAcctId = mainTrade.getString("RSRV_STR1");
        IDataset userInfoChange = UserInfoQry.getUserInfoChgByUserId(userId);
        if (IDataUtil.isNotEmpty(userInfoChange))
        {
            IData userInfo = userInfoChange.getData(0);

            String productId = userInfo.getString("PRODUCT_ID");
            String brandCode = userInfo.getString("BRAND_CODE");
            String imsi = userInfo.getString("IMSI");

            int count = UserInfoQry.updataUserInfoChange(userId);
            IData addParam = new DataMap();
            addParam.put("USER_ID", userId);
            addParam.put("PARTITION_ID", userId.substring(userId.length() - 4));
            addParam.put("TRADE_TYPE_CODE", tradeTypeCode);
            addParam.put("RELATION_TRADE_ID", tradeId);
            addParam.put("PRODUCT_ID", productId);
            addParam.put("BRAND_CODE", brandCode);
            addParam.put("SERIAL_NUMBER", newAcctId);
            addParam.put("IMSI", imsi);
            addParam.put("INST_ID", SeqMgr.getInstId());
            addParam.put("NET_TYPE_CODE", "11");
            addParam.put("START_DATE", SysDateMgr.getSysTime());
            addParam.put("END_DATE", SysDateMgr.END_DATE_FOREVER);
            addParam.put("UPDATE_STAFF_ID", CSBizService.getVisit().getStaffId());
            addParam.put("UPDATE_DEPART_ID", CSBizService.getVisit().getDepartCode());
            addParam.put("REMARK", "修改宽带账号新增");
            Dao.insert("TF_F_USER_INFOCHANGE", addParam);
        }

    }

}
