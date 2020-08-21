package com.asiainfo.veris.crm.iorder.soa.family.busi.destroy.order.trade;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AccountAcctDayTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AccountTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustFamilyTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.IntegralAcctTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.IntegralPlanTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PayRelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ProductTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ResTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ScoreRelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserAcctDayTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.diversifyacct.AcctDayDateUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.PayRelaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.score.ScoreAcctInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.score.ScorePlanInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.score.ScoreRelationQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAcctDayInfoQry;

/**
 * @Description 家庭注销台账
 * @Auther: wuwangfeng
 * @Date: 2020/8/11 10:52
 * @version: V1.0
 */
public class FamilyDestroyTrade extends BaseTrade implements ITrade 
{

	@Override
	public void createBusiTradeData(BusiTradeData btd) throws Exception {
		UcaData uca = btd.getRD().getUca();
		// 注销家庭用户资料
        destoryUserTradeData(uca,btd);
        // 注销家庭客户资料
        destoryCustFamilyTradeData(uca,btd);
        // 注销家庭产品资料
        destoryProductTradeData(uca,btd);
        // 注销家庭服务资料
        destorySvcTrade(uca,btd);
        // 注销家庭优惠资料
        destoryDiscntTrade(uca,btd);
        // 注销家庭账户资料
        destoryAccountTrade(uca,btd);
        // 注销用户账期资料
        destoryOpenUserAcctDayData(uca,btd);
        // 注销账户账期资料
        destoryOpenAccountAcctDayData(uca,btd);
        // 注销家庭付费关系资料
        destoryPayRelationTrade(uca,btd);
        // 注销家庭资源资料
        destoryResTradeData(uca,btd);
        // 注销家庭计费资料
        destoryScoreAcctData(uca,btd);
	}
	
	private void destoryUserTradeData(UcaData uca,BusiTradeData btd) throws Exception
	{
		UserTradeData utd = uca.getUser();
		utd.setDestroyTime(btd.getRD().getAcceptTime());
		utd.setModifyTag(BofConst.MODIFY_TAG_DEL);
		utd.setRemoveTag("2");// 2-主动消号
		btd.add(uca.getSerialNumber(), utd);
	}
	
	private void destoryCustFamilyTradeData(UcaData uca,BusiTradeData btd) throws Exception
	{
		CustFamilyTradeData cftd = uca.getCustFamily();
		cftd.setRemoveDate(btd.getRD().getAcceptTime());
		cftd.setRemoveTag(BofConst.MODIFY_TAG_DEL);
		cftd.setRemoveReason("家庭注销终止！");
		cftd.setModifyTag(BofConst.MODIFY_TAG_DEL);
		btd.add(uca.getSerialNumber(), cftd);
	}
	
	private void destoryProductTradeData(UcaData uca,BusiTradeData btd) throws Exception
	{
		List<ProductTradeData> ptdList = uca.getUserProducts();
		for (int i = 0; i < ptdList.size(); i++) {
			ProductTradeData ptd = ptdList.get(i);
			ptd.setEndDate(btd.getRD().getAcceptTime());
			ptd.setModifyTag(BofConst.MODIFY_TAG_DEL);
			btd.add(uca.getSerialNumber(), ptd);
		}
	}
		
	private void destorySvcTrade(UcaData uca,BusiTradeData btd) throws Exception
	{
		List<SvcTradeData> stdList = uca.getUserSvcs();
		for (int i = 0; i < stdList.size(); i++) {
			SvcTradeData std = stdList.get(i);
			std.setEndDate(btd.getRD().getAcceptTime());
			std.setModifyTag(BofConst.MODIFY_TAG_DEL);
			btd.add(uca.getSerialNumber(), std);
		}
	}
		
	private void destoryDiscntTrade(UcaData uca,BusiTradeData btd) throws Exception
	{
		List<DiscntTradeData> dtdList = uca.getUserDiscnts();
		for (int i = 0; i < dtdList.size(); i++) {
			DiscntTradeData dtd = dtdList.get(i);
			dtd.setEndDate(btd.getRD().getAcceptTime());
			dtd.setModifyTag(BofConst.MODIFY_TAG_DEL);
			btd.add(uca.getSerialNumber(), dtd);
		}
	}
	
	private void destoryAccountTrade(UcaData uca,BusiTradeData btd) throws Exception
	{
		AccountTradeData atd = uca.getAccount();
		atd.setRemoveDate(btd.getRD().getAcceptTime());
		atd.setRemoveTag(BofConst.MODIFY_TAG_DEL);
		atd.setModifyTag(BofConst.MODIFY_TAG_DEL);
		btd.add(uca.getSerialNumber(), atd);
	}	
	
	private void destoryOpenUserAcctDayData(UcaData uca,BusiTradeData btd) throws Exception
	{
        String userId = uca.getUserId();
        String userEparchyCode = uca.getUserEparchyCode();
        IDataset userAcctDayList = UcaInfoQry.qryUserAcctDaysByUserId(userId, userEparchyCode);
        if (IDataUtil.isNotEmpty(userAcctDayList))
        {
            for (int i = 0, count = userAcctDayList.size(); i < count; i++)
            {            	                
            	UserAcctDayTradeData uadtd = new UserAcctDayTradeData(userAcctDayList.getData(i));
            	uadtd.setEndDate(btd.getRD().getAcceptTime());
            	uadtd.setModifyTag(BofConst.MODIFY_TAG_DEL);
            	btd.add(uca.getSerialNumber(), uadtd);	
            }
        }
	}
	
	private void destoryOpenAccountAcctDayData(UcaData uca,BusiTradeData btd) throws Exception
	{
		String acctId = uca.getAcctId();
        IDataset accountAcctDayList = UserAcctDayInfoQry.getAccountAcctDay(acctId);
        if (IDataUtil.isNotEmpty(accountAcctDayList))
        {
            for (int i = 0, count = accountAcctDayList.size(); i < count; i++)
            {
            	AccountAcctDayTradeData aadtd = new AccountAcctDayTradeData(accountAcctDayList.getData(i));
            	aadtd.setEndDate(btd.getRD().getAcceptTime());
            	aadtd.setModifyTag(BofConst.MODIFY_TAG_DEL);
            	btd.add(uca.getSerialNumber(), aadtd);
            }
        }
	}
	
	// 终止用户付费关系 
	private void destoryPayRelationTrade(UcaData uca,BusiTradeData btd) throws Exception
	{
        String userId = uca.getUserId();
        String acctId = uca.getAcctId();
        String serialNumber = uca.getSerialNumber();
        String nowAcycLastDay = AcctDayDateUtil.getCycleIdLastDayThisAcct(userId);
        String lastCycleIdLastAcct = AcctDayDateUtil.getCycleIdLastDayLastAcct(userId);

        // 取所有未失效的付费关系
        IDataset payRelationList = PayRelaInfoQry.queryUserAfterPayRelByCycId(userId, nowAcycLastDay);
        if (IDataUtil.isNotEmpty(payRelationList))
        {
            for (int i = 0, size = payRelationList.size(); i < size; i++)
            {
                PayRelationTradeData data = new PayRelationTradeData(payRelationList.getData(i));
                data.setEndCycleId(nowAcycLastDay);// 全部终止到本账期结束
                data.setModifyTag(BofConst.MODIFY_TAG_DEL);
                btd.add(serialNumber, data);
            }
        }
        IDataset acctList = PayRelaInfoQry.getAcctPayReltionNow(acctId, "1", "1", null);
        boolean bFindUser = false;
        if (IDataUtil.isNotEmpty(acctList))
        {
            for (int i = 0, size = acctList.size(); i < size; i++)
            {
                String tempUserId = acctList.getData(i).getString("USER_ID");
                // 过滤掉本身这条，且只处理下账期还有效的记录
                if (userId.equals(tempUserId))
                    continue;
                if (IDataUtil.isNotEmpty(UcaInfoQry.qryUserInfoByUserId(tempUserId)))
                {
                    bFindUser = true;
                    break;
                }
            }
        }

        if (!bFindUser)// 不存在有效的用户了，则需要将该账户所有的付费关系终止掉
        {
        	List<BaseTradeData> payRelationTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_PAYRELATION);
            IDataset acctAllList = PayRelaInfoQry.qryPayRelaByAcctID2(acctId, "0", "1", lastCycleIdLastAcct);
            if (IDataUtil.isNotEmpty(acctAllList))
            {
                for (int i = 0, size = acctAllList.size(); i < size; i++)
                {
                    IData tempAcctData = acctAllList.getData(i);
                    String tempUserId = tempAcctData.getString("USER_ID");
                    if (StringUtils.equals(userId, tempUserId))
                        continue;// 当前用户不需要再处理了
                    
                    //如果统付的副号用户已经删除过，不需要再删了。
                    if (payRelationTradeDatas != null && payRelationTradeDatas.size() > 0)
                    {
                    	boolean delFlag = true;
                        for (BaseTradeData payRelationTradeData : payRelationTradeDatas)
                        {
                        	String payUserId = payRelationTradeData.toData().getString("USER_ID");
                        	String payAcctId = payRelationTradeData.toData().getString("ACCT_ID");
                        	if(payUserId.equals(tempUserId) && acctId.equals(payAcctId)){
                        		delFlag = false;
                        	}
                        }
                        if(!delFlag){
                        	continue;
                        }
                    }

                    PayRelationTradeData data = new PayRelationTradeData(tempAcctData);
                    data.setEndCycleId(nowAcycLastDay);// 当前账期
                    data.setModifyTag(BofConst.MODIFY_TAG_DEL);
                    btd.add(serialNumber, data);
                }
            }
        }
    }
	
	private void destoryResTradeData(UcaData uca,BusiTradeData btd) throws Exception
	{
		List<ResTradeData> rtdList = uca.getUserAllRes();
		for (int i = 0; i < rtdList.size(); i++) {
			ResTradeData rtd = rtdList.get(i);
			rtd.setEndDate(btd.getRD().getAcceptTime());
			rtd.setModifyTag(BofConst.MODIFY_TAG_DEL);
			btd.add(uca.getSerialNumber(), rtd);
		}
	}
	
	// 终止用户积分账户和积分计划相关资料 NG4.0
	private void destoryScoreAcctData(UcaData uca,BusiTradeData btd) throws Exception
	{
		// 插TF_B_TRADE_INTEGRALACCT、TF_B_TRADE_SCORERELATION 和 TF_B_TRADE_INTEGRALPLAN
        String serialNumber = uca.getSerialNumber();
        String userId = uca.getUserId();
        String userEparchyCode = uca.getUserEparchyCode();
        // 终止积分账户
        IDataset integralAcctDataset = ScoreAcctInfoQry.queryScoreAcctInfoByUserId(userId, "10A", userEparchyCode);
        if (IDataUtil.isNotEmpty(integralAcctDataset))
        {
            for (int i = 0, count = integralAcctDataset.size(); i < count; i++)
            {
                IntegralAcctTradeData integralAcctTradeData = new IntegralAcctTradeData(integralAcctDataset.getData(i));
                integralAcctTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL);
                integralAcctTradeData.setEndDate(btd.getRD().getAcceptTime());
                integralAcctTradeData.setStatus("10E");
                btd.add(serialNumber, integralAcctTradeData);
            }
        }

        // 终止积分计划
        IDataset integralAcctPlanDataset = ScorePlanInfoQry.queryScorePlanInfoByUserId(userId, "10A");
        if (IDataUtil.isNotEmpty(integralAcctPlanDataset))
        {
            for (int i = 0, count = integralAcctPlanDataset.size(); i < count; i++)
            {
                IntegralPlanTradeData planTradeData = new IntegralPlanTradeData(integralAcctPlanDataset.getData(i));
                planTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL);
                planTradeData.setEndDate(btd.getRD().getAcceptTime());
                planTradeData.setStatus("10E");
                btd.add(serialNumber, planTradeData);
            }
        }

        // 终止积分账户关系
        IDataset scoreRelationDataset = ScoreRelationQry.queryEffectiveRelByUserId(userId, "1", userEparchyCode);
        if (IDataUtil.isNotEmpty(scoreRelationDataset))
        {
            for (int i = 0, count = scoreRelationDataset.size(); i < count; i++)
            {
                ScoreRelationTradeData scoreRelationTradeData = new ScoreRelationTradeData(scoreRelationDataset.getData(i));
                scoreRelationTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL);
                scoreRelationTradeData.setEndDate(StringUtils.replace(StringUtils.substring(btd.getRD().getAcceptTime(), 0, 10), "-", ""));
                btd.add(serialNumber, scoreRelationTradeData);
            }
        }
    }

}
