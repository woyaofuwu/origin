
package com.asiainfo.veris.crm.order.soa.group.groupintf.bat;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.group.common.GroupBaseConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.pub.util.AcctDateUtils;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.diversifyacct.DiversifyAcctUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleActiveInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bat.GroupBatService;

public class BatWlanPayRelationChgSVC extends GroupBatService
{

    /**
     * 高级付费关系变更
     */
    private static final long serialVersionUID = 1L;

    private static final String SERVICE_NAME = "SS.WlanPayRelationChgSVC.wlanPayRelationChg";

    @Override
    public void batInitialSub(IData batData) throws Exception
    {
        svcName = SERVICE_NAME;

    }

    @Override
    public void batValidateSub(IData batData) throws Exception
    {
        // 得到业务参数
        String actId = IDataUtil.getMandaData(condData, "ACCT_ID");
        String payItemCode = IDataUtil.getMandaData(condData, "PAYITEM_CODE");
        String userIdA = IDataUtil.getMandaData(condData, "USER_ID");
        String sn = IDataUtil.getMandaData(condData, "SERIAL_NUMBER");
        String operType = IDataUtil.getMandaData(condData, "OPER_TYPE");// 1:新增,0:删除

        IData info = new DataMap();
        info.put("SERIAL_NUMBER", sn);
        info.put("OPER_TYPE", operType);
        info.put("ACCT_ID", actId);
        info.put("PAYITEM_CODE", payItemCode);
        info.put("LIMIT_TYPE", IDataUtil.getMandaData(condData, "LIMIT_TYPE"));
        info.put("LIMIT", IDataUtil.getMandaData(condData, "LIMIT7"));
        info.put("OPER_TYPE", operType);
        info.put("START_CYCLE_ID", IDataUtil.getMandaData(condData, "START_CYCLE"));
        info.put("END_CYCLE_ID", IDataUtil.getMandaData(condData, "END_CYCLE"));
        info.put("COMPLEMENT_TAG", IDataUtil.getMandaData(condData, "COMPLEMENT_TAG"));
        info.put("USER_ID_A", userIdA);
        queryMemberInfo(info);

        // 分散账期修改 WLAN成员订购绑定和集团付费账户的付费关系,因为必须要求成员为自然月账期 add start
        if ("true".equals(DiversifyAcctUtil.getJudeAcctDayTag(info)))
        {
            IData mebUserAcctDay = DiversifyAcctUtil.getUserAcctDay(getMebUcaData().getUserId());
            if (mebUserAcctDay == null || mebUserAcctDay.size() == 0)
            {
                CSAppException.apperr(GrpException.CRM_GRP_657, sn);
            }
            // 排除非自然月账期的情况
            DiversifyAcctUtil.checkUserAcctDayWithWarn(mebUserAcctDay, sn, "1", true);
            // 获取用户账期分布
            String userAcctDayDistribution = DiversifyAcctUtil.userAcctDayDistribution(mebUserAcctDay, "1");

            String startCycleId = info.getString("START_CYCLE_ID");
            String endCycleId = info.getString("END_CYCLE_ID");
            // 如果START_CYCLE_ID为6位,则转为8位
            if (startCycleId.length() == 6)
            {
                startCycleId = startCycleId + "01";
            }
            // 如果END_CYCLE_ID为6位,则转为8位
            if (endCycleId.length() == 6)
            {
                // 获取本月末时间YYYYMMDD
                // info.put("END_CYCLE_ID", SysDateMgr.getDateLastMonthSec(endCycleId.substring(0, 4) + "-" +
                // endCycleId.substring(4, 6) + "-01"));
                info.put("END_CYCLE_ID", endCycleId + "01");
            }

            // YYYYMMDD格式转化为YYYY-MM-DD格式
            String startCycleDate = startCycleId.substring(0, 4) + "-" + startCycleId.substring(4, 6) + "-" + startCycleId.substring(6, 8);

            // 重新计算账期开始时间
            if (GroupBaseConst.UserDaysDistribute.TRUE.getValue().equals(userAcctDayDistribution))
            {
                // 获取传入时间startCycleDate的开始账期
                String firstDayThisAcct = AcctDateUtils.getFirstCycleDayThisAcct(startCycleDate, mebUserAcctDay.getString("ACCT_DAY"), mebUserAcctDay.getString("FIRST_DATE"), mebUserAcctDay.getString("START_DATE"));
                info.put("START_CYCLE_ID", firstDayThisAcct.replaceAll("-", ""));

            }
            else if (GroupBaseConst.UserDaysDistribute.FALSE_TRUE.getValue().equals(userAcctDayDistribution))
            {
                String firstDayThisAcct = mebUserAcctDay.getString("FIRST_DAY_NEXTACCT");
                int months = SysDateMgr.monthInterval(SysDateMgr.getNowCyc(), startCycleId.substring(0, 6));
                if (months > 1)
                {
                    // 获取传入时间startCycleDate的开始账期
                    firstDayThisAcct = AcctDateUtils.getFirstCycleDayThisAcct(startCycleDate, mebUserAcctDay.getString("NEXT_ACCT_DAY"), mebUserAcctDay.getString("NEXT_FIRST_DATE"), mebUserAcctDay.getString("NEXT_START_DATE"));
                }
                info.put("START_CYCLE_ID", firstDayThisAcct.replaceAll("-", ""));
            }
        }
        // 终止付费关系用到
        String startCycleId = info.getString("START_CYCLE_ID");
        startCycleId = startCycleId.substring(0, 4) + "-" + startCycleId.substring(4, 6) + "-" + startCycleId.substring(6, 8);
        info.put("DEL_CYCLE_ID", SysDateMgr.addDays(startCycleId, -1).replaceAll("-", ""));
        // add end

        batData.put("PAY_INFO", info);
        // 查业务类型限制表
        // param.clear();
        // param.put("TRADE_TYPE_CODE","3601" );
        // param.put("USER_ID", td.getMemUserInfo().getString("USER_ID"));
        // param.put("BRAND_CODE", "ZZZZ");
        // param.put("LIMIT_ATTR", "0");
        // param.put("LIMIT_TAG", "0");
        // param.put("EPARCHY_CODE", "0898");
        // CSAppEntity dao = new CSAppEntity(pd);
        // IDataset tradeTypeLimitList = dao.queryListByCodeCode("TD_S_TRADETYPE_LIMIT",
        // "SEL_EXISTS_LIMIT_TRADETYPECODE", param);
        // if(tradeTypeLimitList != null && tradeTypeLimitList.size() >0){
        // common.error("业务受限提示：用户有未完工的VPMN统一付费限制业务");
        // }
    }

    @Override
    public void builderSvcData(IData batData) throws Exception
    {
        // TODO Auto-generated method stub
        svcData.put("PAY_INFO", batData.getData("PAY_INFO"));
        svcData.put("SERIAL_NUMBER", condData.getString("SERIAL_NUMBER"));
        svcData.put("USER_ID", condData.getString("USER_ID"));
    }

    /**
     * 查询成员资料
     * 
     * @author tengg
     * @param pd
     * @param param
     * @param td
     * @throws Exception
     */
    public void queryMemberInfo(IData param) throws Exception
    {
        IData data = new DataMap();

        // 校验集团三户信息
        IData inparam = new DataMap();
        inparam.put("USER_ID", param.getString("USER_ID_A"));
        chkGroupUCAByUserId(inparam);

        // 成员三户信息校验
        data.put("SERIAL_NUMBER", param.getString("SERIAL_NUMBER"));
        chkMemberUCABySerialNumber(data);

        String operType = param.getString("OPER_TYPE");
        IData info = new DataMap();
        info.put("USER_ID", getMebUcaData().getUserId());// 成员用户id
        if ("1".equals(operType))
        {

            IDataset purchase = UserSaleActiveInfoQry.queryPurchaseInfo(info);

            if (IDataUtil.isNotEmpty(purchase))
            {
                for (int i = 0; i < purchase.size(); i++)
                {
                    IData datainfo = purchase.getData(i);
                    IDataset comInfos = CommparaInfoQry.getCommparaAllColByParser("CSM", "9987", datainfo.getString("PRODUCT_ID"), "0898");
                    if (IDataUtil.isNotEmpty(comInfos))
                    {
                        CSAppException.apperr(GrpException.CRM_GRP_655);
                    }
                }
            }
        }
        // 一卡多号
        IDataset relationInfos = RelaUUInfoQry.queryRelationInfo(getMebUcaData().getUserId());
        if (null != relationInfos && relationInfos.size() > 0)
        {
            IData relationInfo = (IData) relationInfos.get(0);
            IData payrelationInfo = UcaInfoQry.qryDefaultPayRelaByUserId(relationInfo.getString("USER_ID_A"));
            if (IDataUtil.isNotEmpty(payrelationInfo))
            {
                String acctIda = ((IData) payrelationInfo.get(0)).getString("ACCT_ID");
                String acctId = getMebUcaData().getAcctId();
                if (acctIda.equals(acctId))
                {
                    CSAppException.apperr(GrpException.CRM_GRP_656);
                }

            }
        }
    }

}
