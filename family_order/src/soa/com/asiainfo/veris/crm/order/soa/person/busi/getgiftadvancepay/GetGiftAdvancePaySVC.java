
package com.asiainfo.veris.crm.order.soa.person.busi.getgiftadvancepay;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeHistoryInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleActiveInfoQry;

public class GetGiftAdvancePaySVC extends CSBizService
{

    private static final long serialVersionUID = 1L;

    /**
     * 记录用户身份校验
     * 
     * @param input
     *            [USER_ID,SERIAL_NUMBER,TRADE_TYPE_CODE,OPEN_DATE,ACCT_TAG,BRAND_CODE]
     * @return
     * @throws Exception
     */
    public IDataset checkAcceptTrade(IData input) throws Exception
    {
        String openDate = input.getString("OPEN_DATE", "").substring(0, 7);
        String acctTag = input.getString("ACCT_TAG");
        String userId = input.getString("USER_ID");

        if ((openDate.compareTo(SysDateMgr.getSysDate("yyyy-MM")) < 0) && acctTag.equals("0"))
        {
            // common.error("不是新客户，不能办理本业务！");
            CSAppException.apperr(CrmCommException.CRM_COMM_1026);
        }

        IDataset tmpSet = TradeHistoryInfoQry.getInfosBySnTradeTypeCode(input.getString("TRADE_TYPE_CODE"), input.getString("SERIAL_NUMBER"), userId);
        if (IDataUtil.isNotEmpty(tmpSet))
        {
            // common.error("用户已办理过预存话费送礼品业务!");
            CSAppException.apperr(CrmCommException.CRM_COMM_1027);
        }

        tmpSet = RelaUUInfoQry.getRelatInfosBySelUserIdA(userId, "30", "2");
        if (IDataUtil.isNotEmpty(tmpSet))
        {
            // common.error("用户是一卡双号副卡不能办理本业务!");
            CSAppException.apperr(CrmCommException.CRM_COMM_1028);
        }

        tmpSet = RelaUUInfoQry.getRelatInfosBySelUserIdA(userId, "97", "2");
        if (IDataUtil.isNotEmpty(tmpSet))
        {
            // common.error("用户是一卡付多号副卡不能办理本业务!");
            CSAppException.apperr(CrmCommException.CRM_COMM_1029);
        }

        String brandCode = input.getString("BRAND_CODE", "");
        if ("G005".equals(brandCode) || "G009".equals(brandCode) || "G011".equals(brandCode) || "G014".equals(brandCode))
        {
            // common.error("用户是移动公话或随e行用户，不能办理本业务!");
            CSAppException.apperr(CrmCommException.CRM_COMM_1030);
        }

        tmpSet = UserDiscntInfoQry.getUserByDiscntCode(userId, "270");
        if (IDataUtil.isNotEmpty(tmpSet))
        {
            // common.error("用户是移动公司员工，不能办理本业务!");
            CSAppException.apperr(CrmCommException.CRM_COMM_1031);
        }

        // CAMPN_TYPE = YX03 代表 购机
        tmpSet = UserSaleActiveInfoQry.queryInfosByUserIdProcessCampn(userId, "0", "YX03");
        if (IDataUtil.isNotEmpty(tmpSet))
        {
            // common.error("用户办理过购机，不能办理本业务!");//注意还未生效的也要判断
            CSAppException.apperr(CrmCommException.CRM_COMM_1032);
        }

        // CAMPN_TYPE = YX02 代表礼包
        tmpSet = UserSaleActiveInfoQry.queryInfosByUserIdProcessCampn(userId, "0", "YX02");
        if (tmpSet != null && tmpSet.size() > 0)
        {
            // common.error("用户办理过礼包，不能办理本业务!");//注意还未生效的也要判断
            CSAppException.apperr(CrmCommException.CRM_COMM_1033);
        }
        // 设置预存款发票打印时 加上如下数据
        IData data = new DataMap();
        data.put("PRINT_DATA", "尊敬的客户，感谢您参加预存话费送礼品活动，参加活动预存的话费不作退款处理。");
        IDataset dataSet = new DatasetList();
        dataSet.add(data);
        return dataSet;
    }

}
