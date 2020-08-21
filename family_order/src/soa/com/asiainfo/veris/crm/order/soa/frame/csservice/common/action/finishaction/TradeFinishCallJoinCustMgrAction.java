
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.action.finishaction;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.exception.TradeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.GrpMebInfoQry;

public class TradeFinishCallJoinCustMgrAction implements ITradeFinishAction
{
    private final static Logger logger = Logger.getLogger(TradeFinishCallJoinCustMgrAction.class);

    public void executeAction(IData mainTrade) throws Exception
    {
        Long tradeId = mainTrade.getLong("TRADE_ID");
        String tradeTypeCode = mainTrade.getString("TRADE_TYPE_CODE");
        String memUserId = mainTrade.getString("USER_ID");
        String memEparchyCode = mainTrade.getString("EPARCHY_CODE");
        String memSn = mainTrade.getString("SERIAL_NUMBER");
        String grpCustId = mainTrade.getString("CUST_ID_B");
        String tagSet = mainTrade.getString("PROCESS_TAG_SET");
        String firstTag = tagSet.substring(0, 1);
        if ("0".equals(firstTag)) // PROCESS_TAG_SET标志位为1才做处理
        {
            return;
        }
        String operType = ""; // 通过operType来控制 '1'加入指定集团 '2'可能要先注销，然后加入，'3'只注销
        if ("2954".equals(tradeTypeCode)) // 集团彩铃成员新增
        {
            operType = "2";
        }
        else if ("3034".equals(tradeTypeCode)) // 集团VPMN成员新增
        {
            IDataset grpMebInfo = GrpMebInfoQry.getGroupMemberInfoByUserId(memUserId, memEparchyCode);
            if (IDataUtil.isNotEmpty(grpMebInfo)) // 如果客户管理关系表中已经存在该用户的关系,那么不再添加
            {
                return;
            }
            operType = "1";
        }
        else if ("2957".equals(tradeTypeCode) || "3037".equals(tradeTypeCode)) // 集团彩铃成员或VPMN成员注销
        {

            operType = "3";
        }
        else
        {
            return;
        }

        IData grpInfo = UcaInfoQry.qryGrpInfoByCustId(grpCustId);
        if (IDataUtil.isEmpty(grpInfo))
        {
            CSAppException.apperr(GrpException.CRM_GRP_824, grpCustId);
        }
        if (!"0".equals(grpInfo.getString("REMOVE_TAG")))
        { // 0:正常，1：销档
            CSAppException.apperr(GrpException.CRM_GRP_824, grpCustId);
        }
        String groupId = grpInfo.getString("GROUP_ID");

        String staffDepartId = mainTrade.getString("TRADE_DEPART_ID");
        String staffId = mainTrade.getString("TRADE_STAFF_ID");
        String staffCityCode = mainTrade.getString("TRADE_CITY_CODE");

        String[] paramName =
        { "IN_TRADE_ID", "IN_DEAL_STAFF_ID", "IN_DEAL_DEPART_ID", "IN_DEAL_EPARCHY_CODE", "IN_DEAL_CITY_CODE", "IN_GROUP_ID", "IN_SERIAL_NUMBER", "IN_MEMBER_BELONG", "IN_RSRV_STR1", "IN_RSRV_STR2", "OUT_RESULT_CODE", "OUT_RESULT_INFO" };

        IData paramValue = new DataMap();
        paramValue.put("IN_TRADE_ID", tradeId);
        paramValue.put("IN_DEAL_DEPART_ID", staffDepartId);
        paramValue.put("IN_DEAL_STAFF_ID", staffId);
        paramValue.put("IN_DEAL_EPARCHY_CODE", memEparchyCode);
        paramValue.put("IN_DEAL_CITY_CODE", staffCityCode);
        paramValue.put("IN_GROUP_ID", groupId);
        paramValue.put("IN_SERIAL_NUMBER", memSn);
        paramValue.put("IN_MEMBER_BELONG", "");
        paramValue.put("IN_RSRV_STR1", "");
        paramValue.put("IN_RSRV_STR2", operType);
        // paramValue.put("OUT_RESULT_CODE", "");
        // paramValue.put("OUT_RESULT_INFO", "");
        Dao.callProc("p_Cms_Insertgpmforgrouppp1", paramName, paramValue, memEparchyCode);

        // 是否成功
        String resultCode = paramValue.getString("OUT_RESULT_CODE");

        if (!"0".equals(resultCode))
        {
            String resultInfo = paramValue.getString("OUT_RESULT_INFO");

            if (logger.isDebugEnabled())
            {
                logger.debug("处理同步集团成员客户表异常，VRESULT_CODE=[" + resultCode + "] VRESULT_INFO=[" + resultInfo + "]");
            }

            CSAppException.apperr(TradeException.CRM_TRADE_331, resultCode, resultInfo);
        }

    }
}
