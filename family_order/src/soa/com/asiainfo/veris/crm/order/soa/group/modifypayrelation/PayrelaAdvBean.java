
package com.asiainfo.veris.crm.order.soa.group.modifypayrelation;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmAccountException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.exception.PayRelationTradeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.group.common.GroupBaseConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.AcctTimeEnvManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.InModeCodeUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.diversifyacct.DiversifyAcctUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.acct.AcctInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.NoteItemInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.PayRelaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.MemberBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.checkmgr.CheckForGrp;

public class PayrelaAdvBean extends MemberBean
{

    private final IData baseCommInfo = new DataMap();

    protected PayrelaAdvReqData reqData = null;

    public void actTradeSub() throws Exception
    {
        super.actTradeSub();
        geneTradePayrelation();
        specialePayReg();

        if ("V0HN001010".equals(reqData.getGrpUca().getSerialNumber()))
        {
            actOtherTrade();
        }
    }

    public void actOtherTrade() throws Exception
    {
        IData othe = new DataMap();
        othe.put("USER_ID", reqData.getGrpUca().getUserId());
        othe.put("RSRV_VALUE_CODE", "30");
        othe.put("RSRV_VALUE", "50");
        othe.put("STATE", "ADD");
        othe.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
        othe.put("START_DATE", getAcceptTime());
        othe.put("END_DATE", SysDateMgr.getEndCycle20501231());
        othe.put("INST_ID", SeqMgr.getInstId());
        addTradeOther(othe);
    }

    /**
     * 生成付费关系子台帐,合进了批量逻辑
     */
    private void geneTradePayrelation() throws Exception
    {
        String acctId = reqData.getGrpUca().getAcctId();// 集团账户id
        String userId = reqData.getUca().getUserId();// 成员用户标识
        // 该成员号码已经与此账户已经绑定了付费关系，业务不能继续
        IDataset acctInfos = PayRelaInfoQry.getPayRelatInfoByUserIdNow(userId);
        if (IDataUtil.isNotEmpty(acctInfos))
        {
            for (int i = 0, iSize = acctInfos.size(); i < iSize; i++)
            {
                IData acctInfo = acctInfos.getData(i);
                if (acctId.equals(acctInfo.getString("ACCT_ID")))
                {
                    CSAppException.apperr(PayRelationTradeException.CRM_PAYRELATION_22, acctId);
                }
            }
        }
        IDataset dataset = new DatasetList();
        IData inParams = new DataMap();
        inParams.put("USER_ID", userId);// 成员user_id
        inParams.put("ACCT_ID", acctId);// 集团acct_id
        inParams.put("PAYITEM_CODE", baseCommInfo.get("PAYITEM_CODE"));
        inParams.put("ACCT_PRIORITY", "0");
        inParams.put("USER_PRIORITY", "0");
        inParams.put("BIND_TYPE", "1");
        inParams.put("START_CYCLE_ID", baseCommInfo.get("START_CYCLE_ID"));
        inParams.put("END_CYCLE_ID", baseCommInfo.get("END_CYCLE_ID"));
        inParams.put("ACT_TAG", "1");
        inParams.put("DEFAULT_TAG", "0");

        inParams.put("STATE", "ADD");
        inParams.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
        inParams.put("LIMIT_TYPE", baseCommInfo.getString("LIMIT_TYPE"));
        inParams.put("LIMIT", baseCommInfo.getString("LIMIT"));
        inParams.put("COMPLEMENT_TAG", baseCommInfo.getString("COMPLEMENT_TAG"));
        inParams.put("INST_ID", SeqMgr.getInstId());

        dataset.add(inParams);

        this.addTradePayrelation(inParams);
    }

    // 综合帐目编码串
    public String getNoteItemCodes(IData data) throws Exception
    {
        // 获取页面勾选的一级综合帐目编码
        String itemcodes = data.getString("PAYITEM_CODE");

        if (StringUtils.isEmpty(itemcodes))
            CSAppException.apperr(CrmAccountException.CRM_ACCOUNT_93);
        
        //根据31676一级综合帐目编码进行特殊处理
        if ( "31676".equals(itemcodes) ){
        	return itemcodes;
        }

        // 根据一级综合帐目编码查询出二级明细帐目编码

        String[] s = itemcodes.split(",");
        StringBuilder sb = new StringBuilder();// 一级综合帐目编码拼串, 用于查询
        for (int i = 0, iSize = s.length; i < iSize; i++)
        {
            sb.append("'" + s[i] + "'");
            if (i == iSize - 1)// 最后一个不需要,号
                continue;
            sb.append(",");
        }

        // 获取二级明细帐目编码
        IDataset dataset = NoteItemInfoQry.queryNoteItems2(sb.toString());

        if (IDataUtil.isEmpty(dataset))
            CSAppException.apperr(CrmAccountException.CRM_ACCOUNT_94);

        StringBuilder sb2 = new StringBuilder();

        for (int i = 0, iSize = dataset.size(); i < iSize; i++)
        {
            IData itemData = dataset.getData(i);
            String noteItemCode = itemData.getString("NOTE_ITEM_CODE");
            sb2.append(noteItemCode);
            if (i == iSize - 1)// 最后一个不需要|号
                continue;
            sb2.append("|");
        }

        return sb2.toString();
    }

    @Override
    protected BaseReqData getReqData() throws Exception
    {
        return new PayrelaAdvReqData();
    }

    @Override
    protected void initReqData() throws Exception
    {
        super.initReqData();

        reqData = (PayrelaAdvReqData) getBaseReqData();
    }

    @Override
    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);

        String startCycleId = map.getString("START_CYCLE_ID");
        String endCycleId = map.getString("END_CYCLE_ID");

        baseCommInfo.put("START_CYCLE_ID", startCycleId);
        baseCommInfo.put("END_CYCLE_ID", endCycleId);
        baseCommInfo.put("LIMIT_TYPE", map.getString("LIMIT_TYPE"));

        baseCommInfo.put("LIMIT", StringUtils.isEmpty(map.getString("LIMIT")) ? 0 : map.getString("LIMIT"));
        baseCommInfo.put("COMPLEMENT_TAG", map.getString("COMPLEMENT_TAG"));

        baseCommInfo.put("ACCT_PRIORITY", map.getString("ACCT_PRIORITY"));
        baseCommInfo.put("newSnInfo_CheckAll", map.getString("newSnInfo_CheckAll"));
        baseCommInfo.put("PAYITEM_CODE", map.getString("PAYITEM_CODE"));

        baseCommInfo.put("acctSmsOrder", map.getString("acctSmsOrder"));

        String crmSmsOrder = map.getString("crmSmsOrder");
        if (!StringUtils.equals(crmSmsOrder, "1"))
        {// crmSmsOrder非1时,都不需要发订购完工短信
            reqData.setNeedSms(false);
        }

        setPayItemInfo(baseCommInfo);

        // 此处逻辑为接口or批量使用,重新计算时间
        if (!InModeCodeUtil.isIntf(CSBizBean.getVisit().getInModeCode(), map.getString(GroupBaseConst.X_SUBTRANS_CODE), map.getString("BATCH_ID")))
            return;

        IData mebUserAcctDay = DiversifyAcctUtil.getUserAcctDay(reqData.getUca().getUserId());
        if (IDataUtil.isEmpty(mebUserAcctDay))
        {
            CSAppException.apperr(GrpException.CRM_GRP_657, reqData.getUca().getSerialNumber());
        }
        // 排除非自然月账期的情况,此处在tradeBaseBean中有统一校验,见checkMebDiversify方法

        // DiversifyAcctUtil.checkUserAcctDayWithWarn(mebUserAcctDay, sn, "1", true);
        // 获取用户账期分布
        String userAcctDayDistribution = DiversifyAcctUtil.userAcctDayDistribution(mebUserAcctDay, "1");

        // 如果START_CYCLE_ID为6位,则转为8位
        if (startCycleId.length() == 6)
        {
            startCycleId = startCycleId + "01";
        }
        // 如果END_CYCLE_ID为6位,则转为8位
        if (endCycleId.length() == 6)
        {
            // 获取本月末时间YYYYMMDD
            endCycleId = SysDateMgr.decodeTimestamp(endCycleId + "01", SysDateMgr.PATTERN_STAND_YYYYMMDD);
            endCycleId = SysDateMgr.getDateLastMonthSec(endCycleId);
            endCycleId = SysDateMgr.decodeTimestamp(endCycleId, SysDateMgr.PATTERN_TIME_YYYYMMDD);
        }

        // YYYYMMDD格式转化为YYYY-MM-DD格式
        String startCycleDate = startCycleId.substring(0, 4) + "-" + startCycleId.substring(4, 6) + "-" + startCycleId.substring(6, 8);

        String firstDate = AcctTimeEnvManager.getAcctTimeEnv().getFirstDate();
        String nextAcctDay = AcctTimeEnvManager.getAcctTimeEnv().getNextAcctDay();
        String acctDay = AcctTimeEnvManager.getAcctTimeEnv().getAcctDay();
        String startDate = AcctTimeEnvManager.getAcctTimeEnv().getStartDate();
        String nextStartDate = AcctTimeEnvManager.getAcctTimeEnv().getNextStartDate();
        String nextFirstDate = AcctTimeEnvManager.getAcctTimeEnv().getNextFirstDate();

        // 重新计算账期开始时间
        if (GroupBaseConst.UserDaysDistribute.TRUE.getValue().equals(userAcctDayDistribution))
        {
            // 获取传入时间startCycleDate的开始账期
            String firstDayThisAcct = SysDateMgr.getFirstCycleDayThisAcct(startCycleDate, acctDay, firstDate, startDate);
            startCycleId = firstDayThisAcct.replaceAll("-", "");

        }
        else if (GroupBaseConst.UserDaysDistribute.FALSE_TRUE.getValue().equals(userAcctDayDistribution))
        {
            String firstDayThisAcct = mebUserAcctDay.getString("FIRST_DAY_NEXTACCT");
            int months = SysDateMgr.monthInterval(SysDateMgr.getNowCyc(), startCycleId.substring(0, 6));
            if (months > 1)
            {
                // 获取传入时间startCycleDate的开始账期
                firstDayThisAcct = SysDateMgr.getFirstCycleDayThisAcct(startCycleDate, nextAcctDay, nextFirstDate, nextStartDate);
            }
            startCycleId = firstDayThisAcct.replaceAll("-", "");
        }
        else
        {
            CSAppException.apperr(GrpException.CRM_GRP_713, "用户存在非自然月账期,办理该业务必须要求自然月账期,请先进行账期变更!");
        }
        baseCommInfo.put("START_CYCLE_ID", startCycleId);
        baseCommInfo.put("END_CYCLE_ID", endCycleId);
    }

    @Override
    protected void makUca(IData map) throws Exception
    {
        super.makUcaForMebNormal(map); // 必须包含成员SERIAL_NUMBER和集团的USER_ID
    }

    protected void regTrade() throws Exception
    {
        super.regTrade();
        IData data = bizData.getTrade();

    }

    public void setPayItemInfo(IData data) throws Exception
    {

        String check_all = data.getString("newSnInfo_CheckAll");
        String payitem_code = "";

        // 处理付费帐目编码
        if (check_all != null && !"".equals(check_all)) // 如果前台勾选了“综合帐目全选”，编码为“-1”
        {
            payitem_code = "-1";
        }
        else
        // 如果前台没有勾选了“综合帐目全选”，则由账务流程返回编码
        {
            String itemStr = getNoteItemCodes(data);

            //根据31676一级综合帐目编码进行特殊处理
            if ( "31676".equals(itemStr) ){
            	payitem_code = itemStr;
            }else{
            	// 调账务接口
                IData payItemsCodes = AcctInfoQry.getPayItemCodeForGrp(itemStr);

                payitem_code = payItemsCodes.getString("PAYITEM_CODE");
            }
        }

        baseCommInfo.put("PAYITEM_CODE", payitem_code);
    }

    protected String setTradeTypeCode() throws Exception
    {
        return "3601";
    }

    /**
     * 集团代付费子表
     * 
     * @throws Exception
     * @author xj
     */
    private void specialePayReg() throws Exception
    {

        IData inParams = new DataMap();
        // IData baseCommInfo = tradeData.getBaseCommInfo();
        inParams.put("USER_ID", reqData.getUca().getUserId());// 成员用户标识
        inParams.put("USER_ID_A", reqData.getGrpUca().getUserId());// 集团用户标识
        inParams.put("ACCT_ID", reqData.getGrpUca().getAcctId());// 集团账户标识
        inParams.put("ACCT_ID_B", reqData.getUca().getAcctId());// 成员账户标识
        inParams.put("PAYITEM_CODE", baseCommInfo.get("PAYITEM_CODE"));// 付费帐目编码
        inParams.put("STATE", "ADD");
        inParams.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
        inParams.put("START_CYCLE_ID", baseCommInfo.getString("START_CYCLE_ID")); // 起始帐期
        inParams.put("END_CYCLE_ID", baseCommInfo.getString("END_CYCLE_ID", "")); // 终止帐期
        inParams.put("BIND_TYPE", "1"); // 绑定帐户方式：0-按优先级，1-按金额几何平分
        inParams.put("LIMIT_TYPE", baseCommInfo.getString("LIMIT_TYPE")); // 限定方式：0-不限定，1-金额，2-比例
        inParams.put("LIMIT", baseCommInfo.getString("LIMIT", "0")); // 限定值ֵ
        inParams.put("COMPLEMENT_TAG", baseCommInfo.getString("COMPLEMENT_TAG", "")); // 是否补足：0-不补足，1-补足
        inParams.put("INST_ID", SeqMgr.getInstId());

        inParams.put("RSRV_STR3", baseCommInfo.getString("acctSmsOrder", ""));// 月初话费短信提醒
        // 分散账期修改???????

        this.addTradeUserSpecialepay(inParams);

    }

    protected void checkMebDiversify(IData map) throws Exception
    {
        map.put("PAY_TYPE_CODE", "G");// 集团付费,便于校验成员账期等信息
        super.checkMebDiversify(map);
    }

    // 重写调用规则校验
    protected void chkTradeBefore(IData map) throws Exception
    {
    	//add by chenzg@20161208 校验集团产品用户是否欠费
        CheckForGrp.chkGrpUserIsOwnFee(reqData.getGrpUca().getUserId());
        IData inData = new DataMap();
        inData.put("CHK_FLAG", "PayRelaAdv");
        inData.put("TRADE_TYPE_CODE", setTradeTypeCode());
        inData.put("SERIAL_NUMBER", map.getString("SERIAL_NUMBER"));
        inData.put("PRODUCT_ID", "-1");
        inData.put("USER_ID", reqData.getUca().getUserId());
        inData.put("CUST_ID", reqData.getGrpUca().getCustId());
        inData.put("ACCT_ID", reqData.getUca().getAcctId());
        
        inData.put("GROUP_ACCT_ID", reqData.getGrpUca().getAcctId());
        inData.put("GROUP_ACCT_CITY_CODE", reqData.getGrpUca().getAccount().getCityCode());
        inData.put("USER_CITY_CODE", reqData.getUca().getUser().getCityCode());
        
        CheckForGrp.chkPayRelaAdvChg(inData);
    }
}
