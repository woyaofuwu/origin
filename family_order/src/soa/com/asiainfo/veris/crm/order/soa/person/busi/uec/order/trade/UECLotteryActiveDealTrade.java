
package com.asiainfo.veris.crm.order.soa.person.busi.uec.order.trade;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.UECLotteryException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.UecLotteryInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.uec.order.requestdata.UECLotteryActiveDealReqData;

public class UECLotteryActiveDealTrade extends BaseTrade implements ITrade
{

    @Override
    public void createBusiTradeData(BusiTradeData bd) throws Exception
    {
        UECLotteryActiveDealReqData reqData = (UECLotteryActiveDealReqData) bd.getRD();
        UcaData uca = reqData.getUca();

        String preTradeId = reqData.getPreTradeId();
        String execName = reqData.getExecName();

        IDataset result = UecLotteryInfoQry.queryUecLotteryByTrade(preTradeId);
        if (IDataUtil.isEmpty(result))
        {
            CSAppException.apperr(UECLotteryException.CRM_UECLOTTERY_3, preTradeId);
        }
        IData uecLotteryInfo = result.getData(0);
        if ("0".equals(uecLotteryInfo.getString("DEAL_FLAG")) || "1".equals(uecLotteryInfo.getString("EXEC_FLAG")))
        {
            CSAppException.apperr(UECLotteryException.CRM_UECLOTTERY_4, preTradeId);
        }

        String prizeTypeName = StaticUtil.getStaticValue("UECLOTTERY_PRIZE_TYPE_CODE" + uecLotteryInfo.getString("ACTIVITY_NUMBER"), uecLotteryInfo.getString("PRIZE_TYPE_CODE"));
        if (StringUtils.isBlank(execName))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_913, prizeTypeName);
        }
        if (StringUtils.isBlank(prizeTypeName) || prizeTypeName.indexOf(execName) == -1)
        {
            CSAppException.apperr(UECLotteryException.CRM_UECLOTTERY_5, prizeTypeName);
        }

        // 拼主台账
        MainTradeData mainTD = bd.getMainTradeData();
        mainTD.setRsrvStr3(prizeTypeName);
        mainTD.setRsrvStr4(uecLotteryInfo.getString("ACCEPT_DATE"));
        mainTD.setRsrvStr5(execName);
        mainTD.setRsrvStr6(preTradeId);

        // 修改TF_B_TRADE_UECLOTTERY
        IData param = new DataMap();
        param.put("TRADE_ID", preTradeId);
        param.put("USER_ID", uca.getUserId());
        param.put("UPDATE_STAFF_ID", getVisit().getStaffId());
        param.put("UPDATE_DEPART_ID", getVisit().getDepartId());
        param.put("UPDATE_TIME", reqData.getAcceptTime());
        param.put("EXEC_TIME", reqData.getAcceptTime());
        param.put("EXEC_FLAG", "1");
        param.put("REVC1", execName);

        StringBuilder sql = new StringBuilder(1000);

        sql.append(" UPDATE TF_B_TRADE_UECLOTTERY A SET  ");
        sql.append(" A.EXEC_FLAG= :EXEC_FLAG ");
        sql.append(" ,A.UPDATE_STAFF_ID= :UPDATE_STAFF_ID ");
        sql.append(" ,A.UPDATE_DEPART_ID= :UPDATE_DEPART_ID ");
        sql.append(" ,A.UPDATE_TIME= TO_DATE(:UPDATE_TIME,'YYYY-MM-DD HH24:MI:SS') ");
        sql.append(" ,A.EXEC_TIME= TO_DATE(:EXEC_TIME,'YYYY-MM-DD HH24:MI:SS') ");
        sql.append(" ,A.REVC1= :REVC1 ");
        sql.append(" WHERE A.TRADE_ID= :TRADE_ID AND A.USER_ID= :USER_ID ");
        sql.append(" AND A.DEAL_FLAG='1' AND A.EXEC_FLAG='0' ");

        int count = Dao.executeUpdate(sql, param);
        if (count < 1)
        {
            CSAppException.apperr(UECLotteryException.CRM_UECLOTTERY_2);
        }
    }

}
