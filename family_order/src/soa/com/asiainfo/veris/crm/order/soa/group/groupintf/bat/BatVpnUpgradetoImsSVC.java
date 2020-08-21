
package com.asiainfo.veris.crm.order.soa.group.groupintf.bat;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bat.GroupBatService;

public class BatVpnUpgradetoImsSVC extends GroupBatService
{
    private static final String SERVICE_NAME = "SS.BatVpnUpgradetoImsSVC.crtTrade";

    /**
     * 作用： 批量初始化其他信息(子类继承)
     * 
     * @throws Exception
     */
    @Override
    public void batInitialSub(IData batData) throws Exception
    {
        svcName = SERVICE_NAME;

    }

    /**
     * 作用： 批量校验其他信息(子类实现)
     * 
     * @throws Exception
     */
    @Override
    public void batValidateSub(IData batData) throws Exception
    {
        batData.put("TRADE_TYPE_CODE", "2528");

        // String serialNumber = IDataUtil.chkParam(batData, "SERIAL_NUMBER");// 成员服务号码
        // 校验集团用户信息
        IData inparam = new DataMap();
        inparam.put("USER_ID", condData.getString("USER_ID"));
        chkGroupUCAByUserId(inparam);

        // 校验成员三户信息
        // inparam.clear();
        // inparam.put("SERIAL_NUMBER", batData.getString("SERIAL_NUMBER"));
        // chkMemberUCABySerialNumber(inparam);

        // 判断服务号码状态
        // if (!"0".equals(getMebUcaData().getUser().getUserStateCodeset()))
        // {
        // CSAppException.apperr(CrmUserException.CRM_USER_471, serialNumber);
        // }
        // 分散账期修改 add start
        // IData mebUserAcctDay = DiversifyAcctUtil.getUserAcctDay(getMebUcaData().getUser().getUserId());
        //
        // if (mebUserAcctDay == null || mebUserAcctDay.size() == 0)
        // {
        // CSAppException.apperr(AcctDayException.CRM_ACCTDAY_16, batData.getString("SERIAL_NUMBER"));
        // }
        // 获取用户账期分布
        // String acctTag = mebUserAcctDay.getString("USER_ACCTDAY_DISTRIBUTION");

        // String startCycleId = batData.getString("START_CYCLE_ID", "");
        // String endCycleId = batData.getString("END_CYCLE_ID", "");

        // 如果START_CYCLE_ID为6位,则转为8位
        // if (!startCycleId.trim().equals("") && startCycleId.length() == 6)
        // {
        // startCycleId = startCycleId + "01";
        // // YYYYMMDD转换为YYYY-MM-DD格式
        // String startCycleDate = startCycleId.substring(0, 4) + "-" + startCycleId.substring(4, 6) + "-" +
        // startCycleId.substring(6, 8);
        // // 重新计算账期开始时间
        // if (GroupBaseConst.UserDaysDistribute.TRUE.getValue().equals(acctTag))
        // {
        // // 获取传入时间startCycleDate的开始账期
        // String firstDayThisAcct = AcctDateUtils.getFirstCycleDayThisAcct(startCycleDate,
        // mebUserAcctDay.getString("ACCT_DAY"), mebUserAcctDay.getString("FIRST_DATE"),
        // mebUserAcctDay.getString("START_DATE"));
        // batData.put("START_CYCLE_ID", firstDayThisAcct.replaceAll("-", ""));
        // }
        // else if (GroupBaseConst.UserDaysDistribute.FALSE_TRUE.getValue().equals(acctTag))
        // {
        // String firstDayThisAcct = mebUserAcctDay.getString("FIRST_DAY_NEXTACCT");
        // int months = SysDateMgr.monthInterval(SysDateMgr.getNowCyc(), startCycleId.substring(0, 6));
        // if (months > 1)
        // {
        // // 获取传入时间startCycleDate的开始账期
        // firstDayThisAcct = AcctDateUtils.getFirstCycleDayThisAcct(startCycleDate,
        // mebUserAcctDay.getString("NEXT_ACCT_DAY"), mebUserAcctDay.getString("NEXT_FIRST_DATE"),
        // mebUserAcctDay.getString("NEXT_START_DATE"));
        // }
        // batData.put("START_CYCLE_ID", firstDayThisAcct.replaceAll("-", ""));
        // }
        // }
        // // 如果END_CYCLE_ID为6位,则转为8位
        // if (!endCycleId.trim().equals("") && endCycleId.length() == 6)
        // {
        // // 获取本月末时间YYYYMMDD
        // batData.put("END_CYCLE_ID", SysDateMgr.getDateForYYYYMMDD(SysDateMgr.getAddMonthsLastDay(0, endCycleId)));
        // }
        // add end
    }

    /**
     * 构造规则数据
     */
    @Override
    protected void builderRuleData(IData batData) throws Exception
    {
        ruleData.put("RULE_BIZ_TYPE_CODE", "chkBeforeForGrp");
        ruleData.put("RULE_BIZ_KIND_CODE", "chk");
        // 集团信息
        ruleData.put("PRODUCT_ID", getGrpUcaData().getProductId());
        ruleData.put("CUST_ID", getGrpUcaData().getCustId());
        ruleData.put("USER_ID", getGrpUcaData().getUserId());
        // 成员信息
        // ruleData.put("EPARCHY_CODE_B", getMebUcaData().getUser().getEparchyCode());
        // ruleData.put("USER_ID_B", getMebUcaData().getUserId());
        // ruleData.put("BRAND_CODE_B", getMebUcaData().getBrandCode());
        // ruleData.put(Route.ROUTE_EPARCHY_CODE, getMebUcaData().getUser().getEparchyCode());
    }

    @Override
    public void builderSvcData(IData batData) throws Exception
    {
        svcData.put("USER_ID", IDataUtil.getMandaData(condData, "USER_ID"));
        svcData.put("PRODUCT_ID", "8001"); // IDataUtil.getMandaData(condData, "PRODUCT_ID")
        svcData.put(Route.USER_EPARCHY_CODE, getGrpUcaData().getUser().getEparchyCode());
        // 添加VPMN集团新增必选服务800
        IDataset productElements = new DatasetList();
        // IData element = new DataMap();
        // element.put("INST_ID", "");
        // element.put("ELEMENT_TYPE_CODE", "S");
        // element.put("ELEMENT_ID", "800");
        // element.put("PRODUCT_ID", "8000");
        // element.put("PACKAGE_ID", "80000001");
        // element.put("START_DATE", SysDateMgr.getSysTime());
        // element.put("END_DATE", SysDateMgr.getTheLastTime());
        // productElements.add(element);
        svcData.put("ELEMENT_INFO", productElements); // 集团元素
        svcData.put("GRP_PACKAGE", IDataUtil.getMandaData(condData, "GRP_PACKAGE")); // 集团成员定制

    }

}
