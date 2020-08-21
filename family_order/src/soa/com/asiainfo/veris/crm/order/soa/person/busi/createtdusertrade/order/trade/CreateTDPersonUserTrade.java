
package com.asiainfo.veris.crm.order.soa.person.busi.createtdusertrade.order.trade;

import java.util.List;

import com.ailk.biz.service.BizRoute;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UBankInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AcctConsignTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustomerTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PayRelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ResTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.person.busi.createtdusertrade.order.requestdata.CreateTDPersonUserRequestData;
import com.asiainfo.veris.crm.order.soa.person.busi.createusertrade.order.trade.CreateUserTrade;

public class CreateTDPersonUserTrade extends CreateUserTrade implements ITrade
{
    /**
     * @Function: createAcctConsignTradeData()
     * @Description: 托收
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: yxd
     * @date: 2014-8-7 下午10:07:07 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-8-7 yxd v1.0.0 修改原因
     */
    private void createAcctConsignTradeData(BusiTradeData btd) throws Exception
    {
        AcctConsignTradeData acctConsignTD = new AcctConsignTradeData();
        CreateTDPersonUserRequestData createPersonUserRD = (CreateTDPersonUserRequestData) btd.getRD();
        String serialNumber = createPersonUserRD.getUca().getUser().getSerialNumber();
        acctConsignTD.setAcctId(createPersonUserRD.getUca().getAcctId());
        acctConsignTD.setPayModeCode(createPersonUserRD.getUca().getAccount().getPayModeCode());// 帐户付费类型：0-现金，1-托收，2-代扣
        acctConsignTD.setEparchyCode(BizRoute.getRouteId());
        acctConsignTD.setCityCode(createPersonUserRD.getCityCode());
        acctConsignTD.setSuperBankCode(createPersonUserRD.getSuperBankCode());
        acctConsignTD.setBankCode(createPersonUserRD.getUca().getAccount().getBankCode());
        acctConsignTD.setBankAcctNo(createPersonUserRD.getUca().getAccount().getBankAcctNo());
        String bankName = UBankInfoQry.getBankNameByBankCode(createPersonUserRD.getUca().getAccount().getBankCode());
        acctConsignTD.setBankAcctName(bankName);
        acctConsignTD.setConsignMode("1");// 托收方式：默认为1
        acctConsignTD.setPaymentId("4");// 储值方式：默认为4
        acctConsignTD.setPayFeeModeCode("4");
        acctConsignTD.setActTag("1");
        acctConsignTD.setPriority("0");
        acctConsignTD.setInstId(SeqMgr.getInstId());
        acctConsignTD.setStartCycleId(SysDateMgr.getNowCyc());
        acctConsignTD.setEndCycleId(SysDateMgr.getEndCycle205012());
        acctConsignTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
        btd.add(serialNumber, acctConsignTD);
    }

    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        CreateTDPersonUserRequestData createPersonUserRD = (CreateTDPersonUserRequestData) btd.getRD();
        super.createBusiTradeData(btd);
        
        //BUS201812190003 关于在固话开户界面新增“铁通迁转固话”标签的需求 by mengqx 20190123
        btd.getMainTradeData().setRsrvStr4(createPersonUserRD.getIsTTtransfer());
        
        if (createPersonUserRD.getUca().getAccount().getPayModeCode().equals("1"))
        {
            createAcctConsignTradeData(btd);
        }
        createUserTradeData(btd);
        createPayrelationTradeData(btd);
        // 处理无线固话号码和SIM卡资源台帐信息 RSRV_STR5：01来区分其他号码和sim卡，以便统一用action处理
        //this.processResTradeRsrv5(btd);
        //处理无线固话批量预开标记：根据业务类型TRADE_TYPE_CODE=3822以及TF_B_TRADE的RSRV_STR6=1来决定送局停指令
        this.processMainTradeRsrv6(btd);
        //2017-11-30号补充经办人信息
        this.createCustomerTradeData(btd);
    }

    /**
     * @Function: createPayrelationTradeData()
     * @Description: 付费关系
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: yxd
     * @date: 2014-8-7 下午9:59:49 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-8-7 yxd v1.0.0 修改原因
     */
    private void createPayrelationTradeData(BusiTradeData btd) throws Exception
    {
        PayRelationTradeData payrelationTD = new PayRelationTradeData();
        CreateTDPersonUserRequestData createPersonUserRD = (CreateTDPersonUserRequestData) btd.getRD();
        String serialNumber = createPersonUserRD.getUca().getUser().getSerialNumber();
        payrelationTD.setUserId(createPersonUserRD.getUca().getUserId());
        payrelationTD.setAcctId(createPersonUserRD.getUca().getAcctId());
        payrelationTD.setPayitemCode("-1");
        payrelationTD.setAcctPriority("0");
        payrelationTD.setUserPriority("0");
        payrelationTD.setBindType("1");
        payrelationTD.setStartCycleId(SysDateMgr.getNowCycle());
        payrelationTD.setEndCycleId(SysDateMgr.getEndCycle20501231());
        payrelationTD.setActTag("1");
        payrelationTD.setDefaultTag("1");
        payrelationTD.setLimitType("0");
        payrelationTD.setLimit("0");
        payrelationTD.setComplementTag("0");
        payrelationTD.setRemark(createPersonUserRD.getRemark());
        payrelationTD.setInstId(SeqMgr.getInstId());
        payrelationTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
        btd.add(serialNumber, payrelationTD);
    }

    /**
     * @Function: createUserTradeData()
     * @Description: 处理密码卡初始密码
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: yxd
     * @date: 2014-9-2 上午10:53:12 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-9-2 yxd v1.0.0 修改原因
     */
    public void createUserTradeData(BusiTradeData btd) throws Exception
    {
        CreateTDPersonUserRequestData createPersonUserRD = (CreateTDPersonUserRequestData) btd.getRD();
        List<UserTradeData> userTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_USER);
        for (UserTradeData userTradeData : userTradeDatas)
		{
			if ("1".equals(createPersonUserRD.getDefaultPwdFlag()))
			{
				userTradeData.setUserPasswd(createPersonUserRD.getCardPasswd());
				userTradeData.setRsrvTag1("1");// 密码卡
				userTradeData.setRsrvStr3(createPersonUserRD.getPassCode());
			}
			// 批开时，TF_F_USER表的RSRV_NUM1记为1
			if (StringUtils.equals("3822", btd.getTradeTypeCode()))
			{
				userTradeData.setRsrvNum1("1");
				userTradeData.setAcctTag("2");
				userTradeData.setOpenMode("1");
			}
			userTradeData.setRsrvTag3(createPersonUserRD.getOpenType());
			//设置发展员工信息
			userTradeData.setDevelopDate(SysDateMgr.getSysDate());
			userTradeData.setDevelopCityCode(super.getVisit().getCityCode());
			userTradeData.setDevelopEparchyCode(super.getTradeEparchyCode());
			userTradeData.setDevelopDepartId(super.getVisit().getDepartId());
			userTradeData.setDevelopStaffId(super.getVisit().getStaffId());
		}
    }

    /**
     * @Function: processResTradeRsrv5()
     * @Description: 处理无线固话号码和SIM卡资源台帐信息 RSRV_STR5：01来区分其他号码和sim卡，以便统一用action处理预占和实占
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: yxd
     * @date: 2014-8-11 下午2:53:28 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-8-11 yxd v1.0.0 修改原因
     */
    private void processResTradeRsrv5(BusiTradeData btd) throws Exception
    {
        List<ResTradeData> resTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_RES);
        for (ResTradeData resTradeData : resTradeDatas)
        {
            resTradeData.setRsrvStr5("01");
        }
    }
    /**
     * 
    * @Function: processMainTradeRsrv6()
    * @Description: 批开：根据业务类型TRADE_TYPE_CODE=3822以及TF_B_TRADE的RSRV_STR6=1来决定送局停指令
    *
    * @param:
    * @return：
    * @throws：异常描述
    *
    * @version: v1.0.0
    * @author: yxd
    * @date: 2014-9-21 下午4:37:49
    *
    * Modification History:
    * Date         Author          Version            Description
    *---------------------------------------------------------*
    * 2014-9-21      yxd         v1.0.0               修改原因
     */
    private void processMainTradeRsrv6(BusiTradeData btd) throws Exception
	{
		if (StringUtils.equals("3822", btd.getTradeTypeCode()))
		{
			MainTradeData mainTD = btd.getMainTradeData();
			mainTD.setRsrvStr6("1");
		}
	}
    
    
    /**
     * 客户资料表
     * @param btd
     * @throws Exception
     */
    public void createCustomerTradeData(BusiTradeData btd) throws Exception
    {
    	CreateTDPersonUserRequestData createPersonUserRD = (CreateTDPersonUserRequestData) btd.getRD();

        List<CustomerTradeData> customerTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_CUSTOMER);
        for (CustomerTradeData customerTradeData : customerTradeDatas)
        {

            if ("1".equals(createPersonUserRD.getRealName()))
            {
                customerTradeData.setIsRealName(createPersonUserRD.getRealName());
                customerTradeData.setRsrvStr3(SysDateMgr.getSysTime());
            }
            customerTradeData.setRsrvStr7(createPersonUserRD.getAgentCustName());// 经办人名称
            customerTradeData.setRsrvStr8(createPersonUserRD.getAgentPsptTypeCode());// 经办人证件类型
            customerTradeData.setRsrvStr9(createPersonUserRD.getAgentPsptId());// 经办人证件号码
            customerTradeData.setRsrvStr10(createPersonUserRD.getAgentPsptAddr());// 经办人证件地址
        }
    }
}
