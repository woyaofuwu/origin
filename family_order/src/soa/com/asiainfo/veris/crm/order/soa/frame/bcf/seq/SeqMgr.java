package com.asiainfo.veris.crm.order.soa.frame.bcf.seq;

import java.util.UUID;

import com.ailk.biz.bean.BizBean;
import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.ProvinceUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UAreaInfoQry;

public class SeqMgr {

	protected static final String fillupFigure(String instr, int num, String str)
			throws Exception {
		StringBuilder strbuf = new StringBuilder();
		int len = instr.length();
		if (len < num) {
			for (int i = 0; i < (num - len); i++) {
				strbuf.append(str);
			}
		} else if (len > num) // 该逻辑按原函数逻辑处理
		{
			instr = instr.substring(len - num);
		}
		strbuf.append(instr);
		return strbuf.toString();
	}

	public static String getAcctId() throws Exception {
		String seqId = Dao.getSequence(SeqAcctId.class);
		return seqId;
	}

	public static String getActivityId() throws Exception {
		String seqId = Dao.getSequence(SeqActivityId.class);
		return seqId;
	}

	public static String getAttachId() throws Exception {
		return Dao.getSequence(SeqAttachId.class);
	}

	public static String getBadnessId() throws Exception {
		String seqId = Dao.getSequence(SeqBadnessId.class, Route.CONN_CRM_CEN);
		return seqId;
	}

	public static String getRelationId() throws Exception {
		String seqId = Dao.getSequence(SeqRelationId.class, Route.CONN_CRM_CEN);
		return seqId;
	}

	public static String getBankSignId() throws Exception {
		String seqId = Dao.getSequence(SeqBankSignId.class);
		return seqId;
	}

	public static String getBatchId() throws Exception {
		String seqId = "";

		if (ProvinceUtil.isProvince(ProvinceUtil.HNAN)) {
			seqId = Dao.getSequence(SeqBatchId2.class, Route.CONN_CRM_CEN);
		} else {
			seqId = Dao.getSequence(SeqBatchId.class, Route.CONN_CRM_CEN);
		}

		return seqId;
	}

	public static String getBatchId2() throws Exception {
		String seqId = Dao.getSequence(SeqBatchId2.class);

		return seqId;
	}

	public static String getBatchTaskId() throws Exception {
		String seqId = Dao.getSequence(SeqBatchTaskId.class);
		return seqId;
	}

	public static String getBBossMerchIdForGrp() throws Exception {
		String seqId = Dao.getSequence(SeqBBossMerchIdForGrp.class,
				Route.CONN_CRM_CG);
		return seqId;
	}

	public static String getBBossProductIdForGrp() throws Exception {
		String seqId = Dao.getSequence(SeqBbossProductIdForGrp.class,
				Route.CONN_CRM_CG);
		return seqId;
	}

	public static String getBillSynId() throws Exception {
		String seqId = Dao.getSequence(SeqBillSynId.class);
		return seqId;
	}

	public static String getBlackId() throws Exception {
		String seqId = Dao.getSequence(SeqBlackId.class);
		return seqId;
	}

	public static String getBlackUserId() throws Exception {
		String seqId = Dao
				.getSequence(SeqBlackuserId.class, Route.CONN_CRM_CEN);
		return seqId;
	}

	public static String getBlackUserLogId() throws Exception {
		String seqId = Dao.getSequence(SeqBlackuserLogId.class,
				Route.CONN_CRM_CEN);
		return seqId;
	}

	public static String getBookingId() throws Exception {
		String seqId = Dao.getSequence(SeqBookingId.class);
		return seqId;
	}

	public static String getBpmId() throws Exception {
		String seqId = Dao.getSequence(SeqBpmId.class);
		return seqId;
	}

	public static String getBroadBandId() throws Exception {
		String seqId = Dao.getSequence(SeqBroadBandId.class);
		return seqId;
	}

	public static String getBusiElementId() throws Exception {
		String seqId = Dao.getSequence(SeqMSCFBusiId.class, Route.CONN_MS);
		return seqId;
	}

	public static String getBusiId() throws Exception {
		String seqId = Dao.getSequence(SeqMSCFBusiId.class, Route.CONN_MS);
		return seqId;
	}

	public static String getBusiNodeId() throws Exception {
		String seqId = Dao.getSequence(SeqMSCFBusiId.class, Route.CONN_MS);
		return seqId;
	}

	public static String getBuyOutTeleQuLogId() throws Exception {
		String seqId = Dao.getSequence(SeqBuyOutTeleQuLogId.class);
		return seqId;
	}

	public static String getCenBatchId() throws Exception {
		String seqId = Dao.getSequence(SeqCenBatchId.class, Route.CONN_CRM_CEN);
		return seqId;
	}

	public static String getCenIbSysId() throws Exception {
		String seqId = Dao.getSequence(SeqCenIbSysId.class, Route.CONN_CRM_CEN);
		return seqId;
	}

	public static String getCenIbSysSubId() throws Exception {

		String seqId = Dao.getSequence(SeqCenIbSysSubId.class,
				Route.CONN_CRM_CEN);
		return seqId;
	}

	public static String getCenImportId() throws Exception {
		String seqId = Dao.getSequence(SeqImportId.class, Route.CONN_CRM_CEN);
		return seqId;
	}

	public static String getCgExtendId() throws Exception {
		String seqId = Dao.getSequence(SeqExtendId.class, Route.CONN_CRM_CG);
		return seqId;
	}

	public static String getChargeId() throws Exception {

		String seqId = Dao.getSequence(SeqChargeId.class);
		return seqId;
	}

	public static String getCheckBuy() throws Exception {
		String seqId = Dao.getSequence(SeqCheckBuy.class);
		return seqId;
	}

	public static String getChnlRequestId() throws Exception {
		String seqId = Dao.getSequence(SeqRequestId.class, Route.CONN_CRM_CEN);
		return seqId;
	}

	public static String getChnlTouchId() throws Exception {
		String seqId = Dao.getSequence(SeqChnlTouchId.class);
		return seqId;
	}

	public static String getCommendId() throws Exception {

		String seqId = Dao.getSequence(SeqCommendId.class);
		return seqId;
	}

	public static String getContactId() throws Exception {
		String seqId = Dao.getSequence(SeqMSCFBusiId.class, Route.CONN_MS);
		return seqId;
	}

	public static String getContractId() throws Exception {
		String seqId = Dao.getSequence(SeqContractId.class);

		return seqId;
	}

	public static String getHitEggId() throws Exception {
		String seqId = Dao.getSequence(SeqHitEggID.class);

		return seqId;
	}

	public static String getCopApplyId() throws Exception {
		String seqId = Dao.getSequence(SeqCopApplyId.class, Route.CONN_CRM_CEN);
		return seqId;
	}

	public static String getCopBusiChangeId() throws Exception {
		String seqId = Dao.getSequence(SeqCopBusiChangeId.class,
				Route.CONN_CRM_CEN);
		return seqId;
	}

	public static String getCopInnovApplyId() throws Exception {
		String seqId = Dao.getSequence(SeqCopInnovApplyId.class,
				Route.CONN_CRM_CEN);
		return seqId;
	}

	public static String getCopLatentApplyId() throws Exception {
		String seqId = Dao.getSequence(SeqCopLatentApplyId.class,
				Route.CONN_CRM_CEN);
		return seqId;
	}

	public static String getCopLog() throws Exception {
		String seqId = Dao.getSequence(SeqCopLog.class, Route.CONN_CRM_CEN);
		return seqId;
	}

	public static String getCopPartnerId() throws Exception {
		String seqId = Dao.getSequence(SeqCopPartnerId.class,
				Route.CONN_CRM_CEN);
		return seqId;
	}

	public static String getCopProjectId() throws Exception {
		String seqId = Dao.getSequence(SeqCopProjectId.class,
				Route.CONN_CRM_CEN);
		return seqId;
	}

	public static String getCopServiceId() throws Exception {
		String seqId = Dao.getSequence(SeqCopServiceId.class,
				Route.CONN_CRM_CEN);
		return seqId;
	}

	public static String getCopServiceParms() throws Exception {
		String seqId = Dao.getSequence(SeqCopServiceParms.class,
				Route.CONN_CRM_CEN);
		return seqId;
	}

	public static String getCorpBizCode() throws Exception {

		String seqId = Dao.getSequence(SeqCorpBizCode.class);
		return seqId;
	}

	public static String getCrmBfasId() throws Exception {
		String seqId = Dao.getSequence(SeqCrmBfasId.class);
		return seqId;
	}

	public static String getCrmOperId() throws Exception {
		String seqId = Dao.getSequence(SeqCrmOperId.class);
		return seqId;
	}

	public static String getCtrmProId() throws Exception {
		return Dao.getSequence(SeqCtrmProId.class, Route.CONN_CRM_CEN);
	}

	public static String getCustContact() throws Exception {
		String seqId = Dao.getSequence(SeqCustContact.class);
		return seqId;
	}

	public static String getCustContactTrace() throws Exception {
		String seqId = Dao.getSequence(SeqCustContactTrace.class);
		return seqId;
	}

	public static String getCustId() throws Exception {

		String seqId = Dao.getSequence(SeqCustId.class);
		return seqId;
	}

	public static String getDandeLionId() throws Exception {
		String seqId = Dao.getSequence(SeqDandeLionId.class);
		return seqId;
	}

	public static String getDocId() throws Exception {
		String seqId = Dao.getSequence(SeqHelpDocId.class, Route.CONN_CRM_CEN);// SEQ_HELPDOC_ID
		return seqId;
	}

	public static String getDummyUserId() throws Exception {
		String seqId = Dao.getSequence(SeqDummyUserId.class);
		return seqId;
	}

	public static String getEpaperAcceptId() throws Exception {
		String seqId = Dao.getSequence(SeqEpaperAcceptId.class,
				Route.CONN_CRM_CEN);
		return seqId;
	}

	public static String getEpaperCertId() throws Exception {
		String seqId = Dao.getSequence(SeqEpaperCertId.class,
				Route.CONN_CRM_CEN);
		return seqId;
	}

	public static String getEpaperId() throws Exception {
		String seqId = Dao.getSequence(SeqEpaperId.class, Route.CONN_CRM_CEN);
		return seqId;
	}

	public static String getEpaperLogId() throws Exception {
		String seqId = Dao
				.getSequence(SeqEpaperLogId.class, Route.CONN_CRM_CEN);
		return seqId;
	}

	public static String getEpaperTransId() throws Exception {
		String seqId = Dao.getSequence(SeqEpaperTransId.class,
				Route.CONN_CRM_CEN);
		return seqId;
	}

	public static String getErrorInfoId() throws Exception {
		String seqId = Dao.getSequence(SeqErrorInfoId.class, Route.CONN_CRM_CEN);
		return seqId;
	}


	public static String getExtendId() throws Exception {
		String seqId = Dao.getSequence(SeqExtendId.class);
		return seqId;
	}

	public static String getFeeFlowId() throws Exception {
		String seqId = Dao.getSequence(SeqFeeFlowId.class);
		return seqId;
	}

	public static String getFeeLogId() throws Exception {
		String seqId = Dao.getSequence(SeqFeeLogId.class);
		return seqId;
	}

	public static String getFeeNoteId() throws Exception {
		String seqId = Dao.getSequence(SeqFeeNoteId.class);
		return seqId;
	}

	public static String getFeeRegLogId() throws Exception {
		String seqId = Dao.getSequence(SeqFeeRegLogId.class);
		return seqId;
	}

	public static String getFileId() throws Exception {
		String seqId = Dao.getSequence(SeqFileId.class);
		return seqId;
	}

	public static String getFlowId() throws Exception {
		String seqId = Dao.getSequence(SeqFlowId.class);
		return seqId;
	}

	public static String getGroupId() throws Exception {
		String seqId = Dao.getSequence(SeqGroupId.class);
		return seqId;
	}

	public static String getGrpApplyId() throws Exception {
		String seqId = Dao.getSequence(SeqGrpApplyId.class, Route.CONN_CRM_CG);
		return seqId;
	}

	public static String getGrpBizIbsysid() throws Exception {
		return Dao.getSequence(SeqSubscribeGroupBiz.class);
	}

	public static String getGrpBuzApplyIdForGrp() throws Exception {
		String seqId = Dao.getSequence(SeqGrpBuzApplyIdForGrp.class,
				Route.CONN_CRM_CG);
		return seqId;
	}

	public static String getGrpMolist() throws Exception {
		String seqId = Dao.getSequence(SeqGrpMolist.class);
		return seqId;
	}

	public static String getHomeId() throws Exception {
		String seqId = Dao.getSequence(SeqHomeId.class);
		return seqId;
	}

	public static String getHssAliasId() throws Exception {
		String seqId = Dao.getSequence(SeqHssAliasId.class, Route.CONN_CRM_CEN);
		return seqId;
	}

	public static String getIbSysIdForUip() throws Exception {
		String seqId = Dao.getSequence(SeqIbSysIdForUip.class,
				Route.CONN_CRM_CG);
		return seqId;
	}

	public static String getImportId() throws Exception {
		String seqId = Dao.getSequence(SeqImportId.class);
		return seqId;
	}

	public static String getInfoId() throws Exception {
		String seqId = Dao.getSequence(SeqInfoId.class, Route.CONN_CRM_CEN);
		return seqId;
	}

	public static String getInfoInstId() throws Exception {
		String seqId = Dao.getSequence(SeqInfoInstId.class, Route.CONN_CRM_CEN);
		return seqId;
	}

	public static String getInstId() throws Exception {
		String seqId = Dao.getSequence(SeqInstId.class);
		return seqId;
	}

	public static String getInstId(String epachyCode) throws Exception {
		String seqId = Dao.getSequence(SeqInstId.class, epachyCode);
		return seqId;
	}

	public static String getIntegralAcctId() throws Exception {
		String seqId = Dao.getSequence(SeqIntegralAcctId.class);
		return seqId;
	}

	public static String getJobExecLogId() throws Exception {
		String seqId = Dao.getSequence(SeqJobExecLogId.class, Route.CONN_MS);
		return seqId;
	}

	public static String getJobMessageId() throws Exception {
		String seqId = Dao.getSequence(SeqJobMessageId.class, Route.CONN_MS);
		return seqId;
	}

	public static String getJobTrackId() throws Exception {
		String seqId = Dao.getSequence(SeqJobTrackId.class, Route.CONN_MS);
		return seqId;
	}

	public static String getJTVWId() throws Exception {
		String seqId = Dao.getSequence(SeqJTVWId.class);
		return seqId;
	}

	/*public static String getJzhConnectId() throws Exception {
		String seqId = Dao.getSequence(SeqJzhConnectId.class, Route.CONN_CC);
		return seqId;
	}*/

	/*public static String getJzhPatrolId() throws Exception {
		String seqId = Dao.getSequence(SeqJzhPatrolId.class, Route.CONN_CC);
		return seqId;
	}*/

	/*public static String getJzhValidateId() throws Exception {
		String seqId = Dao.getSequence(SeqJzhValidateId.class, Route.CONN_CC);
		return seqId;
	}*/

	public static String getLaunchLogId() throws Exception {
		String seqId = Dao.getSequence(SeqLaunchLogId.class);
		return seqId;
	}

	public static String getLogId() throws Exception {
		String seqId = Dao.getSequence(SeqLogId.class, Route.CONN_CRM_CG);
		return seqId;
	}

	public static String getLogIdForCrm() throws Exception {
		String seqId = Dao.getSequence(SeqLogId.class);
		return seqId;
	}

	public static String getLotterySeq() throws Exception {
		String seqId = Dao.getSequence(SeqUecLottery.class);
		return seqId;
	}

	public static String getMaxNumberLine() throws Exception {
		String seqId = Dao.getSequence(SeqEsopForzhzg.class);
		return seqId;
	}

	public static String getMerchantExtendId() throws Exception {
		String seqId = Dao.getSequence(SeqMerchantExtendId.class,
				Route.CONN_CRM_CEN);
		return seqId;
	}

	public static String getMsAreaId() throws Exception {

		String seqId = Dao.getSequence(SeqMSAreaId.class, Route.CONN_MS);
		return seqId;
	}

	public static String getMsBookId() throws Exception {
		String seqId = Dao.getSequence(SeqMsBookId.class, Route.CONN_MS);
		return seqId;
	}

	public static String getMscfItemId() throws Exception {
		String seqId = Dao.getSequence(SeqMSCFItemId.class, Route.CONN_MS);
		return seqId;
	}

	public static String getMscfTempId() throws Exception {
		String seqId = Dao.getSequence(SeqMSCFTempId.class, Route.CONN_MS);
		return seqId;
	}

	public static String getMsclBespeakTaskID() throws Exception {
		String seqId = Dao.getSequence(SeqMSCLBespeakTaskID.class,
				Route.CONN_MS);
		return seqId;
	}

	public static String getMsclFlowId() throws Exception {
		String seqId = Dao.getSequence(SeqMSCFItemId.class, Route.CONN_MS);
		return seqId;
	}

	public static String getMsCommonId() throws Exception {
		String seqId = Dao.getSequence(SeqMsCommonId.class, Route.CONN_MS);
		return seqId;
	}

	public static String getMsCrmObjId() throws Exception {
		String seqId = Dao.getSequence(SeqMsCrmObjId.class, Route.CONN_MS);
		return seqId;
	}

	public static String getMsGrantId() throws Exception {
		String seqId = Dao.getSequence(SeqMSGrant.class, Route.CONN_MS);
		return seqId;
	}

	public static String getMsGrantMsgId() throws Exception {
		String seqId = Dao.getSequence(SeqMsGrantMsgId.class, Route.CONN_MS);
		return seqId;
	}

	public static String getMsInfoID() throws Exception {
		String seqId = Dao.getSequence(SeqMsInfoID.class, Route.CONN_CRM_CEN);
		return seqId;
	}

	public static String getMsInstID() throws Exception {
		String seqId = Dao.getSequence(SeqMsInstID.class, Route.CONN_MS);
		return seqId;
	}

	public static String getMsJobId() throws Exception {
		String seqId = Dao.getSequence(SeqJobId.class, Route.CONN_MS);
		return seqId;
	}

	public static String getMspcCampnId() throws Exception {
		String seqId = Dao.getSequence(SeqMSPCCampnId.class, Route.CONN_MS);
		return seqId;
	}

	public static String getMspcCampnIndexId() throws Exception {
		String seqId = Dao
				.getSequence(SeqMSPCCampnIndexId.class, Route.CONN_MS);
		return seqId;
	}

	public static String getMspcCampnObjId() throws Exception {
		String seqId = Dao.getSequence(SeqMSPCCampnObjId.class, Route.CONN_MS);
		return seqId;
	}

	public static String getMspcCampnObjrelId() throws Exception {
		String seqId = Dao.getSequence(SeqMSPCCampnObjrelId.class,
				Route.CONN_MS);
		return seqId;
	}

	public static String getMspcCampnOtherId() throws Exception {
		String seqId = Dao
				.getSequence(SeqMSPCCampnOtherId.class, Route.CONN_MS);
		return seqId;
	}

	public static String getMspcCampnParamId() throws Exception {
		String seqId = Dao
				.getSequence(SeqMSPCCampnParamId.class, Route.CONN_MS);
		return seqId;
	}

	public static String getMspcCampnProgId() throws Exception {
		String seqId = Dao.getSequence(SeqMSPCCampnProgId.class, Route.CONN_MS);
		return seqId;
	}

	public static String getMspcCampnRelId() throws Exception {
		String seqId = Dao.getSequence(SeqMSPCCampnRelId.class, Route.CONN_MS);
		return seqId;
	}

	public static String getMspcCampnRelobjId() throws Exception {
		String seqId = Dao.getSequence(SeqMSPCCampnRelobjId.class,
				Route.CONN_MS);
		return seqId;
	}

	public static String getMspcCampnResId() throws Exception {
		String seqId = Dao.getSequence(SeqMSPCCampnResId.class, Route.CONN_MS);
		return seqId;
	}

	public static String getMspcCampnRuleId() throws Exception {
		String seqId = Dao.getSequence(SeqMSPCCampnRuleId.class, Route.CONN_MS);
		return seqId;
	}

	public static String getMspcCampnSpecId() throws Exception {
		String seqId = Dao.getSequence(SeqMSPCCampnSpecId.class, Route.CONN_MS);
		return seqId;
	}

	public static String getMspcCampnTeamId() throws Exception {
		String seqId = Dao.getSequence(SeqMSPCCampnTeamId.class, Route.CONN_MS);
		return seqId;
	}

	public static String getMSPCMemberImpBacthId() throws Exception {

		String seqId = Dao.getSequence(SeqMSPCMemberImpBacthId.class,
				Route.CONN_MS);
		return seqId;
	}

	public static String getMspcRelRuleId() throws Exception {
		String seqId = Dao.getSequence(SeqMSPCRelRuleId.class, Route.CONN_MS);
		return seqId;
	}

	public static String getMsphChnlChartId() throws Exception {
		String seqId = Dao.getSequence(SeqMSPHChnlChartId.class, Route.CONN_MS);
		return seqId;
	}

	public static String getMsphChnlId() throws Exception {
		String seqId = Dao.getSequence(SeqMSPHChnlId.class, Route.CONN_MS);
		return seqId;
	}

	public static String getMsphChnlParamId() throws Exception {
		String seqId = Dao.getSequence(SeqMSPHChnlParamId.class, Route.CONN_MS);
		return seqId;
	}

	public static String getMsphChnlTempId() throws Exception {
		String seqId = Dao.getSequence(SeqMSPHChnlTempId.class, Route.CONN_MS);
		return seqId;
	}

	public static String getMsPlanId() throws Exception {
		String seqId = Dao.getSequence(SeqMsPlanId.class, Route.CONN_MS);
		return seqId;
	}

	public static String getMspoCampnApproveId() throws Exception {
		String seqId = Dao.getSequence(SeqMSPOCampnApproveId.class,
				Route.CONN_MS);
		return seqId;
	}

	public static String getShxiMspoCampnApproveId() throws Exception {
		String seqId = Dao.getSequence(SeqMSPOCampnApproveId.class,
				Route.CONN_CRM_CEN);
		return seqId;
	}

	public static String getMspoCampnMemberId() throws Exception {
		String seqId = Dao.getSequence(SeqMSPOCampnMemberId.class,
				Route.CONN_MS);
		return seqId;
	}

	public static String getMspoCampnTroopId() throws Exception {
		String seqId = Dao
				.getSequence(SeqMSPOCampnTroopId.class, Route.CONN_MS);
		return seqId;
	}

	public static String getMspoCampnTroopruleId() throws Exception {
		String seqId = Dao.getSequence(SeqMSPOCampnTroopruleId.class,
				Route.CONN_MS);
		return seqId;
	}

	public static String getMspoOcustId() throws Exception {
		String seqId = Dao.getSequence(SeqMSPOOcustId.class, Route.CONN_MS);
		return seqId;
	}

	public static String getMsptAreaDetailId() throws Exception {
		String seqId = Dao
				.getSequence(SeqMSPTAreaDetailId.class, Route.CONN_MS);
		return seqId;
	}

	public static String getMsptCampnTouchId() throws Exception {
		String seqId = Dao
				.getSequence(SeqMSPTCampnTouchId.class, Route.CONN_MS);
		return seqId;
	}

	public static String getMsptTouchCode() throws Exception {
		String seqId = Dao.getSequence(SeqMSPTTouchCode.class, Route.CONN_MS);
		return seqId;
	}

	public static String getMsptTouchCondId() throws Exception {
		String seqId = Dao.getSequence(SeqMSPTTouchCondId.class, Route.CONN_MS);
		return seqId;
	}

	public static String getMsptTouchDetailId() throws Exception {
		String seqId = Dao.getSequence(SeqMSPTTouchDetailId.class,
				Route.CONN_MS);
		return seqId;
	}

	public static String getMsRangeObjId() throws Exception {
		String seqId = Dao.getSequence(SeqMsRangeObjId.class, Route.CONN_MS);
		return seqId;
	}

	public static String getMsRuleId() throws Exception {
		String seqId = Dao.getSequence(SeqMsRuleId.class, Route.CONN_MS);
		return seqId;
	}

	public static String getMsSqlRefId() throws Exception {
		String seqId = Dao.getSequence(SeqMsSqlRefID.class, Route.CONN_MS);
		return seqId;
	}

	public static String getMsTaskId() throws Exception {
		String seqId = Dao.getSequence(SeqMsTaskId.class, Route.CONN_MS);
		return seqId;
	}

	public static String getMsTeamId() throws Exception {
		String seqId = Dao.getSequence(SeqMsTeamId.class, Route.CONN_MS);
		return seqId;
	}

	public static String getMsTeamMemberId() throws Exception {
		String seqId = Dao.getSequence(SeqMsTeamMemberId.class, Route.CONN_MS);
		return seqId;
	}

	public static String getNflBusiId() throws Exception {
		String seqId = Dao.getSequence(SeqMSCFBusiId.class, Route.CONN_MS);
		return seqId;
	}

	public static String getNpAuditId() throws Exception {
		String seqId = Dao.getSequence(SeqNpAuditId.class, Route.CONN_CRM_CEN);
		return seqId;
	}

	public static String getNpJobId() throws Exception {
		String seqId = Dao.getSequence(SeqNpJobId.class,
				CSBizBean.getTradeEparchyCode());
		return seqId;
	}

	public static String getBarcodeId() throws Exception {
		String seqId = Dao.getSequence(SeqBarcodeId.class, Route.CONN_CRM_CEN);
		return seqId;
	}

	public static String getBarcodeKey() throws Exception {
		String seqId = Dao.getSequence(SeqBarcodeKey.class, Route.CONN_CRM_CEN);
		return seqId;
	}

	public static String getObjcustFilter() throws Exception {
		String seqId = Dao.getSequence(SeqObjcustFilter.class, Route.CONN_MS);
		return seqId;
	}

	public static String getOperId() throws Exception {
		String seqId = Dao.getSequence(SeqOperId.class, Route.CONN_CRM_CEN);
		return seqId;
	}
	public static String getOperId1() throws Exception {
		String seqId = Dao.getSequence(SeqOperId.class);
		return seqId;
	}
	public static String getOperIdCode() throws Exception {
		String seqId = Dao.getSequence(SeqOperIdCode.class);
		return seqId;
	}

	public static String getOprSeqForUip() throws Exception {
		String seqId = Dao.getSequence(SeqOprSeqForUip.class);
		return seqId;
	}

	public static String getOrderId() throws Exception {
		String seqId = Dao.getSequence(SeqOrderId.class);
		return seqId;
	}

	public static String getNewOrderId(String acceptTime) throws Exception {
		String seqId = Dao.getSequence(SeqOrderId.class, null, acceptTime);
		return seqId;
	}

	public static String getNewOrderIdFromDb(String acceptTime) throws Exception {
		String nextval = getSeqIdFromDb("seq_order_id");
		StringBuilder strbuf = new StringBuilder();
		strbuf.append(getOrderno());
		strbuf.append(SysDateMgr.decodeTimestamp(acceptTime, "yyMMdd")); // 取8位系统时间，yyyyMMdd
		int len = nextval.length();
		if (len < 8) {
			for (int i = 0; i < (8 - len); i++) {
				strbuf.append("0");
			}
		} else if (len > 8) // 该逻辑按原函数逻辑处理
		{
			nextval = nextval.substring(len - 8);
		}
		strbuf.append(nextval);
		return strbuf.toString();
	}

	public static String getOrderId(String eparchyCode) throws Exception {
		String seqId = Dao.getSequence(SeqOrderId.class, eparchyCode);
		return seqId;
	}

	public static String getOrderId(String eparchyCode, String acceptTime) throws Exception {
		String seqId = Dao.getSequence(SeqOrderId.class, eparchyCode, acceptTime);
		return seqId;
	}

	public static String getOrderLogId() throws Exception {
		String seqId = Dao.getSequence(SeqOrderLogId.class);
		return seqId;
	}

	protected static final String getOrderno() throws Exception {
		String eparchyCode = CSBizBean.getUserEparchyCode();

		IDataset dataset = UAreaInfoQry.qryAreaByPk(eparchyCode, null, null);

		// 初始值
		String iv_orderno = "";

		if (dataset.size() > 0) {
			iv_orderno = dataset.getData(0).getString("ORDER_NO", "");
			iv_orderno = fillupFigure(iv_orderno, 2, "9");
		}

		return iv_orderno;
	}

	public static String getOrdReqId() throws Exception {
		String seqId = Dao.getSequence(SeqOrdReqSeq.class);
		return seqId;
	}

	public static String getOuterTradeId() throws Exception {
		String seqId = Dao.getSequence(SeqOuterTradeId.class);
		return seqId;
	}

	public static String getOutGrpId() throws Exception {
		String seqId = Dao.getSequence(SeqOutGrpId.class);
		return seqId;
	}

	public static String getOutNetGrpmemberId() throws Exception {
		String seqId = Dao.getSequence(SeqOutNetGrpmemberId.class);
		return seqId;
	}

	public static String getPasserrLogId() throws Exception {
		String seqId = Dao.getSequence(SeqPasserrLogId.class);
		return seqId;
	}

	public static String getPatchIdForUip() throws Exception {
		String seqId = Dao.getSequence(SeqPatchIdForUip.class,
				Route.CONN_CRM_CG);
		return seqId;
	}

	public static String getPbssBizProdInstId() throws Exception {
		String seqId = Dao.getSequence(SeqPbssBizProdInstId.class,
				Route.CONN_CRM_CEN);
		return seqId;
	}

	public static String getPbssBizSubsId() throws Exception {
		String seqId = Dao.getSequence(SeqPbssBizSubsId.class,
				Route.CONN_CRM_CEN);
		return seqId;
	}

	/**
	 * add by liaoshanwu 流量实体卡交易包流水号
	 *
	 * @return
	 * @throws Exception
	 */
	public static String getPkgSeqId() throws Exception {
		String seqId = Dao.getSequence(SeqPkgId.class);
		return seqId;
	}

	public static String getPlanId() throws Exception {
		String seqId = Dao.getSequence(SeqPlanId.class);
		return seqId;
	}

	public static String getPlatformId() throws Exception {
		String seqId = Dao.getSequence(SeqMSCFBusiId.class, Route.CONN_MS);
		return seqId;
	}

	public static String getPlatOfficeDataImportId() throws Exception {
		String seqId = Dao.getSequence(SeqPlatOfficeDataImportId.class);
		return seqId;
	}

	public static String getPosTradeId() throws Exception {
		String seqId = Dao.getSequence(SeqPosTradeId.class);
		return seqId;
	}

	public static String getPosLogId() throws Exception {
		String seqId = Dao.getSequence(SeqPosLogId.class, Route.CONN_CRM_CEN);
		return seqId;
	}

	public static String getPotradeIdForGrp() throws Exception {
		String seqId = Dao.getSequence(SeqPotradeIdForGrp.class,
				Route.CONN_CRM_CG);
		return seqId;
	}

	public static String getPreSmsSendId() throws Exception {
		String seqId = Dao.getSequence(SeqPreSmsSendId.class,
				Route.CONN_CRM_CEN);
		return seqId;
	}

	public static String getPrintId() throws Exception {
		String seqId = Dao.getSequence(SeqPrintId.class);
		return seqId;
	}

	public static String getRealId() throws Exception {
		if (ProvinceUtil.isProvince(ProvinceUtil.TJIN) || ProvinceUtil.isProvince(ProvinceUtil.SHXI)
				|| ProvinceUtil.isProvince(ProvinceUtil.QHAI)) {
			String seqId = Dao.getSequence(SeqRealId.class, Route.CONN_CRM_CEN);
			return seqId;
		} else {
			String seqId = Dao.getSequence(SeqRealId.class);
			return seqId;
		}
	}

	public static String getRealIdPush() throws Exception
	{
		String seqId = Dao.getSequence(SeqRealId.class, Route.CONN_CRM_CEN);
		return seqId;
	}

	public static String getRecordId() throws Exception {
		String seqId = Dao.getSequence(SeqRecordId.class, Route.CONN_CRM_CEN);
		return seqId;
	}

	public static String getRemindCode() throws Exception {
		return Dao.getSequence(SeqRemindCode.class,Route.CONN_CRM_CEN);
	}

	public static String getReqSaleGoodsId() throws Exception {
		String seqId = Dao.getSequence(SeqReqSaleGoodsId.class,
				Route.CONN_CRM_CEN);
		return seqId;
	}

	public static String getResWaringId() throws Exception {
		String seqId = Dao.getSequence(SeqResWaringId.class, Route.CONN_MS);
		return seqId;
	}

	public static String getSaleAcceptId() throws Exception {

		String seqId = Dao.getSequence(SeqSaleAcceptId.class);
		return seqId;
	}

	public static String getSaleOrderId() throws Exception {
		String seqId = Dao
				.getSequence(SeqSaleOrderId.class, Route.CONN_CRM_CEN);
		return seqId;
	}

	public static String getScoreAcctId() throws Exception {
		String seqId = Dao.getSequence(SeqScoreAcctId.class);
		return seqId;
	}

	public static String getScoreAcctIdCG() throws Exception {
		String seqId = Dao.getSequence(SeqScoreAcctIdCG.class,
				Route.CONN_CRM_CG);
		return seqId;
	}

	public static String getScoreRelId() throws Exception {
		String seqId = Dao.getSequence(SeqScoreRelId.class);
		return seqId;
	}

	public static String getScoreRuleId() throws Exception {
		String seqId = Dao
				.getSequence(SeqScoreRuleId.class, Route.CONN_CRM_CEN);
		return seqId;
	}

	private static String getSeqIdFromDb(String seqName) throws Exception {
		SQLParser parser = new SQLParser(new DataMap());
		parser.addSQL(" SELECT " + seqName + ".nextval OUTSTR FROM dual ");
		//IDataset results = Dao.qryByParse(parser,Route.getJourDb(BizBean.getVisit().getStaffEparchyCode()));
		IDataset results = Dao.qryByParse(parser,Route.getJourDb(BizRoute.getRouteId()));// modify by duhj

		return results.getData(0).getString("OUTSTR");
	}

	public static String getSeqUacctId(String seqName) throws Exception {
		return getSeqIdFromDb(seqName);
	}

	public static String getSeqMSABatchId() throws Exception {
		String seqId = Dao.getSequence(SeqMSABatchId.class, Route.CONN_MS);
		return seqId;
	}

	public static String getSeqMSAPlanId() throws Exception {
		String seqId = Dao.getSequence(SeqMSAPlanId.class, Route.CONN_MS);
		return seqId;
	}

	public static String getSeqMSARelationId() throws Exception {
		String seqId = Dao.getSequence(SeqMSARelationId.class, Route.CONN_MS);
		return seqId;
	}

	public static String getSeqMSDLockId() throws Exception {
		String seqId = Dao.getSequence(SeqMSDLockId.class, Route.CONN_MS);
		return seqId;
	}

	public static String getSeqMsTempletSmsId() throws Exception {
		String seqId = Dao.getSequence(SeqMsTempletSmsId.class, Route.CONN_MS);
		return seqId;
	}

	public static String getSeqSmsTwoCheckId() throws Exception {
		String seqId = Dao.getSequence(SeqSmsTwoCheckId.class,
				Route.CONN_CRM_CEN);
		return seqId;
	}

	public static String getSeqVipId() throws Exception {
		String seqId = Dao.getSequence(SeqViptId.class);
		return seqId;
	}

	public static String getSeqWorldexpo() throws Exception {
		String seqId = Dao.getSequence(SeqWorldexpo.class);
		return seqId;
	}

	public static String getSimCardNo() throws Exception {
		String seqId = Dao.getSequence(SeqSimCardNo.class, Route.CONN_CRM_CEN);
		return seqId;
	}

	public static String getSmsblId() throws Exception {

		String seqId = Dao.getSequence(SeqSmsblId.class);
		return seqId;
	}

	public static String getSmsSendId() throws Exception {

		String seqId = Dao.getSequence(SeqSmsSendId.class);
		return seqId;
	}

	public static String getSmsSendIdForGrp() throws Exception {
		String seqId = Dao.getSequence(SeqSmsSendIdForGrp.class,
				Route.CONN_CRM_CG);
		return seqId;
	}

	public static String getSmsTemplateId() throws Exception {
		String seqId = Dao.getSequence(SeqSmsTemplateId.class,
				Route.CONN_CRM_CEN);
		return seqId;
	}

	public static String getSpbureImportId() throws Exception {
		return Dao.getSequence(SeqSpbureImportId.class, Route.CONN_CRM_CEN);
	}

	public static String getSubIbsysid() throws Exception {
		return Dao.getSequence(SeqSubIbsysid.class);
	}

	private static String getSyncIdFromDb() throws Exception {
		SQLParser parser = new SQLParser(new DataMap());
		parser.addSQL(" SELECT SEQ_SYNC_INCRE_ID.nextval OUTSTR FROM dual ");
		IDataset results = Dao.qryByParse(parser,  Route.getJourDbDefault());
		return results.getData(0).getString("OUTSTR");
	}

	public static String getSyncIncreId() throws Exception {

		String seqId = Dao.getSequence(SeqSyncIncreId.class, Route.getJourDbDefault());
		return seqId;
	}

	public static String getSyncIncreIdDB() throws Exception {
		String nextval = getSyncIdFromDb();
		StringBuilder strbuf = new StringBuilder();
		strbuf.append(SysDateMgr.getSysDate("yyyyMMdd")); // 取8位系统时间，yyyyMMdd
		int len = nextval.length();
		if (len < 8) {
			for (int i = 0; i < (8 - len); i++) {
				strbuf.append("0");
			}
		} else if (len > 8) // 该逻辑按原函数逻辑处理
		{
			nextval = nextval.substring(len - 8);
		}
		strbuf.append(nextval);
		return strbuf.toString();
	}

	public static String getSYnTradeIdForGrp() throws Exception {
		String seqId = Dao.getSequence(SeqSynTradeIdForGrp.class,
				Route.getJourDb(Route.CONN_CRM_CG));
		return seqId;
	}

	public static String getSYnTradeId() throws Exception {
		String seqId = Dao.getSequence(SeqSynTradeId.class);
		return seqId;
	}

	public static String getSysIbSysId() throws Exception {
		String seqId = Dao.getSequence(SeqSysIbSysIdForGrp.class,
				Route.CONN_CRM_CG);
		return seqId;
	}

	public static String getTaxApplyTaskId() throws Exception {
		String seqId = Dao.getSequence(SeqTaxApplyTaskId.class);

		return seqId;
	}

	public static String getTaxInstId() throws Exception {

		String seqId = Dao.getSequence(SeqTaxInstId.class);
		return seqId;
	}

	public static String getTestUserId() throws Exception {
		String seqId = Dao.getSequence(SeqTestUserId.class);
		return seqId;
	}

	public static String getTiTroopmembLockid() throws Exception {
		String seqId = Dao.getSequence(SeqTiTroopmembLockid.class,
				Route.CONN_MS);
		return seqId;
	}

	public static String getTJNumBizCode() throws Exception {
		String seqId = Dao.getSequence(SeqTJNumBizCode.class);
		return seqId;
	}

	public static String getTradeId() throws Exception {
		String seqId = Dao.getSequence(SeqTradeId.class);
		return seqId;
	}

	public static String getNewTradeId(String acceptTime) throws Exception {
		String seqId = Dao.getSequence(SeqTradeId.class, null, acceptTime);
		return seqId;
	}

	public static String getTradeId(String epachyCode) throws Exception {
		String seqId = Dao.getSequence(SeqTradeId.class, epachyCode);
		return seqId;
	}

	public static String getTradeId(String epachyCode, String acceptTime) throws Exception {
		String seqId = Dao.getSequence(SeqTradeId.class, epachyCode, acceptTime);
		return seqId;
	}

	public static String getTradeSmsId() throws Exception {
		String seqId = Dao.getSequence(SeqTradeSmsId.class);
		return seqId;
	}

	public static String getTradeIdForUip() throws Exception {
		String seqId = Dao.getSequence(SeqTradeIdForUip.class,
				Route.CONN_CRM_CG);
		return seqId;
	}

	public static String getTradeIdFromDb() throws Exception {
		String nextval = getSeqIdFromDb("seq_trade_id");
		StringBuilder strbuf = new StringBuilder();
		strbuf.append(getOrderno());
		strbuf.append(SysDateMgr.getSysDate("yyMMdd")); // 取8位系统时间，yyyyMMdd
		int len = nextval.length();
		if (len < 8) {
			for (int i = 0; i < (8 - len); i++) {
				strbuf.append("0");
			}
		} else if (len > 8) // 该逻辑按原函数逻辑处理
		{
			nextval = nextval.substring(len - 8);
		}
		strbuf.append(nextval);
		return strbuf.toString();
	}

	public static String getNewTradeIdFromDb(String acceptTime) throws Exception {
		String nextval = getSeqIdFromDb("seq_trade_id");
		StringBuilder strbuf = new StringBuilder();
		strbuf.append(getOrderno());
		strbuf.append(SysDateMgr.decodeTimestamp(acceptTime, "yyMMdd")); // 取8位系统时间，yyyyMMdd
		int len = nextval.length();
		if (len < 8) {
			for (int i = 0; i < (8 - len); i++) {
				strbuf.append("0");
			}
		} else if (len > 8) // 该逻辑按原函数逻辑处理
		{
			nextval = nextval.substring(len - 8);
		}
		strbuf.append(nextval);
		return strbuf.toString();
	}

	public static String getTransAcctId() throws Exception {
		String seqId = Dao.getSequence(SeqTransAcctId.class);
		return seqId;
	}

	public static String getTransId() throws Exception {
		String seqId = Dao.getSequence(SeqTransId.class);
		return seqId;
	}

	public static String getTransId(String eparchyCode) throws Exception {
		String seqId = Dao.getSequence(SeqTransId.class, eparchyCode);
		return seqId;
	}

	public static String getTransUserId() throws Exception {
		String seqId = Dao.getSequence(SeqTransUserId.class);
		return seqId;
	}

	public static String getUAcctId() throws Exception {
		String seqId = Dao.getSequence(SeqUAcctId.class);
		return seqId;
	}

	public static String getUipSysId() throws Exception {
		String seqId = Dao.getSequence(SeqUipSysIdForGrp.class,
				Route.CONN_CRM_CG);
		return seqId;
	}

	public static String getUpccIbsysId() throws Exception {
		String seqId = Dao.getSequence(SeqUpccIbsysId.class);
		return seqId;
	}

	public static String getUserClusterId() throws Exception {
		String seqId = Dao.getSequence(SeqUserClusterId.class,
				Route.CONN_CRM_CEN);
		return seqId;
	}

	public static String getUserId() throws Exception {

		String seqId = Dao.getSequence(SeqUserId.class);
		return seqId;
	}

	public static String getUUID() throws Exception {
		String uuId = UUID.randomUUID().toString();

		return uuId;
	}

	public static String getVipCardNo() throws Exception {
		String seqId = Dao.getSequence(SeqVipCardNo4.class);
		return seqId;
	}

	public static String getVpmnIdIdForGrp() throws Exception {
		String seqId = Dao
				.getSequence(SeqVpmnIdForGrp.class, Route.CONN_CRM_CG);
		return seqId;
	}

	public static String getVpmnSerialForGrp() throws Exception {
		String seqId = Dao.getSequence(SeqVpmnSerialForGrp.class,
				Route.CONN_CRM_CG);
		return seqId;
	}

	public static String getVpnIdIdForGrp() throws Exception {
		String seqId = Dao.getSequence(SeqVpnIdForGrp.class, Route.CONN_CRM_CG);
		return seqId;
	}

	public static String getVpnNo() throws Exception {
		String seqId = Dao.getSequence(SeqVpnNo.class, Route.CONN_CRM_CG);
		return seqId;
	}

	public static String getWidnetAcctId() throws Exception {

		String seqId = Dao.getSequence(SeqWidnetAcctId.class);
		return seqId;
	}

	public static String getWlwBizCode() throws Exception {
		String seqId = Dao.getSequence(SeqWlwBizCode.class);
		return seqId;
	}

	public static String getMobileG3LogId() throws Exception {
		String seqId = Dao.getSequence(SeqMobileG3LogId.class);
		return seqId;
	}

	public static String getMiniSn() throws Exception {
		String seqId = Dao.getSequence(SeqMiniSn.class, Route.CONN_CRM_CG);
		return seqId;
	}

	public static String getMiniGroup() throws Exception {
		String seqId = Dao.getSequence(SeqMiniGroup.class, Route.CONN_CRM_CG);
		return seqId;
	}

	public static String getBizLogId() throws Exception {
		return Dao.getSequence(SeqBizLogId.class);
	}

	public static String getSecurityId() throws Exception {
		String seqId = Dao.getSequence(SeqSecurityId.class);
		return seqId;
	}

	/** ========================= Add by zengym5 ========================= */
	public static String getGoodsOperId() throws Exception {
		String seqId = Dao.getSequence(SeqGoodsOperId.class, Route.CONN_CRM_CEN);
		return seqId;
	}

	public static String getGoodsPriceId() throws Exception {
		String seqId = Dao.getSequence(SeqGoodsPriceId.class, Route.CONN_CRM_CEN);
		return seqId;
	}

	public static String getGoodsLabelId() throws Exception {
		String seqId = Dao.getSequence(SeqGoodsLabelId.class, Route.CONN_CRM_CEN);
		return seqId;
	}

	public static String getGoodsCategoryId() throws Exception {
		String seqId = Dao.getSequence(SeqGoodsCategoryId.class, Route.CONN_CRM_CEN);
		return seqId;
	}


	public static String getGoodsGoodsId() throws Exception {
		String seqId = Dao.getSequence(SeqGoodsGoodsId.class, Route.CONN_CRM_CEN);
		return seqId;
	}

	public static String getGoodsPublishId() throws Exception {
		String seqId = Dao.getSequence(SeqGoodsPublishId.class, Route.CONN_CRM_CEN);
		return seqId;
	}
	/** ================================================================= */

	public static String getWidenetBookOrderId() throws Exception {
		return Dao.getSequence(SeqWidenetBookOrderId.class, Route.CONN_CRM_CEN);
	}

	public static String getSaleGoodsId() throws Exception {
		return Dao.getSequence(SeqSaleGoodsId.class, Route.CONN_CRM_CEN);
	}

	public static String getServiceParamId() throws Exception {
		String seqId = Dao.getSequence(SeqServiceParamId.class,
				Route.CONN_CRM_CEN);
		return seqId;
	}

	public static String getCmServiceId() throws Exception {
		return Dao.getSequence(SeqCmsServiceId.class);
	}

	public static String getSaBusiId() throws Exception {
		return Dao.getSequence(SeqSaBusiId.class);
	}

	public static String getVipGolfId() throws Exception {
		return Dao.getSequence(SeqVipGolfId.class);
	}

	public static String getVipGolfLogId() throws Exception {
		return Dao.getSequence(SeqVipGolfLogId.class);
	}

	public static String getLogIdForCen() throws Exception {
		String seqId = Dao.getSequence(SeqLogId.class);
		return seqId;
	}

	public static String getSeqRuleId() throws Exception {
		String seqId = Dao.getSequence(SeqRuleId.class, Route.CONN_CRM_CEN);
		return seqId;
	}

	public static String getSeqFinancialId() throws Exception {
		String seqId = Dao.getSequence(SeqFinancialId.class);
		return seqId;
	}

	public static String getApplApprId() throws Exception
	{
		String seqId = Dao.getSequence(SeqApplApprId.class);
		return seqId;
	}

	public static String getSeqRepaiId() throws Exception
	{
		String seqId = Dao.getSequence(SeqRepaiId.class);
		return seqId;
	}

	public static String getChnlAccesEquipId() throws Exception
	{
		String seqId = Dao.getSequence(SeqChnlAccesEquipId.class);
		return seqId;
	}

	public static String getChnlApplyId() throws Exception
	{
		String seqId = Dao.getSequence(SeqChnlApplyId.class);
		return seqId;
	}

	public static String getChnlChargeId() throws Exception
	{
		String seqId = Dao.getSequence(SeqChnlChargeId.class);
		return seqId;
	}

	public static String getCHL_CHECK_ID() throws Exception
	{
		String seqId = Dao.getSequence(SeqCHL_CHECK_ID.class);
		return seqId;
	}

	public static String getCHLH_SAID() throws Exception
	{
		String seqId = Dao.getSequence(SeqCHLH_SAID.class,Route.CONN_CRM_CEN);
		return seqId;
	}

	public static String getSA_STAFF_EXAM_ID() throws Exception
	{
		String seqId = Dao.getSequence(SeqSA_STAFF_EXAM_ID.class,Route.CONN_CRM_CEN);
		return seqId;
	}

	public static String getCHL_SAID() throws Exception
	{
		String seqId = Dao.getSequence(SeqCHL_SAID.class,Route.CONN_CRM_CEN);
		return seqId;
	}

	public static String getCHL_SA_COMPACTID() throws Exception
	{
		String seqId = Dao.getSequence(SeqCHL_SA_COMPACTID.class,Route.CONN_CRM_CEN);
		return seqId;
	}

	public static String getCHNL_PARTNER_ID() throws Exception
	{
		String seqId = Dao.getSequence(SeqCHNL_PARTNER_ID.class,Route.CONN_CRM_CEN);
		return seqId;
	}

	public static String getSA_AUDIT_ID() throws Exception
	{
		String seqId = Dao.getSequence(SeqSA_AUDIT_ID.class,Route.CONN_CRM_CEN);
		return seqId;
	}

	public static String getEmailNoticeId() throws Exception {
		return Dao.getSequence(SeqEmailNoticeId.class, Route.CONN_CRM_CEN);
	}

	public static String getIntfId() throws Exception {
		return Dao.getSequence(SeqIntfId.class);
	}

	public static String getCustContactId() throws Exception {
		return Dao.getSequence(SeqCustContactId.class);
	}

	public static String getCustContactTraceId() throws Exception {
		return Dao.getSequence(SeqCustContactTraceId.class);
	}

	public static String getBookingTradeId() throws Exception {
		return Dao.getSequence(SeqBookingTradeId.class);
	}

	/**
	 * 短信通知工单流水
	 *
	 * @return
	 * @throws Exception
	 */
	public static String getSmsNoticeId() throws Exception
	{
		return Dao.getSequence(SeqSmsNoticeId.class);
	}

	public static String getSalSquID() throws Exception
	{
		String seqId = Dao.getSequence(SeqSalSquId.class,Route.CONN_CRM_CEN);
		return seqId;
	}

	public static String getCustRequId() throws Exception {
		String seqId = Dao.getSequence(SeqCustRequId.class, Route.CONN_CRM_CEN);
		return seqId;
	}

	public static String getCustRequFlowID() throws Exception {
		String seqId = Dao.getSequence(SeqCustRequFlowId.class, Route.CONN_CRM_CEN);
		return seqId;
	}

	public static String getBizInfoID() throws Exception {
		String seqId = Dao.getSequence(SeqBizInfoId.class, Route.CONN_CRM_CEN);
		return seqId;
	}

	public static String getReplyErrorID() throws Exception {
		String seqId = Dao.getSequence(SeqReplyErrorId.class, Route.CONN_CRM_CEN);
		return seqId;
	}

	public static String getGroupOrderId() throws Exception {
		String seqId = Dao.getSequence(SeqGroupOrderId.class, Route.CONN_CRM_CG);
		return seqId;
	}

	public static String getGroupPrtDetailId() throws Exception {
		String seqId = Dao.getSequence(SeqGroupPrtDetailId.class, Route.CONN_CRM_CG);
		return seqId;
	}

	public static String getMSPOTroopUseID() throws Exception {
		String seqId = Dao.getSequence(SeqMSPOTroopUseId.class, Route.CONN_CRM_CEN);
		return seqId;
	}

	public static String getSelfHelpCardId() throws Exception {
		return Dao.getSequence(SeqSelfHelpCardId.class);
	}

	public static String getSchoolId() throws Exception {
		String seqId = Dao.getSequence(SeqSchoolId.class, Route.CONN_CRM_CEN);
		return seqId;
	}

	/***
	 * 平安互助群组编号
	 * @return
	 * @throws Exception
	 */
	public static String getSafeGroupCode() throws Exception
	{
		String seqId = Dao.getSequence(SeqSafeGroupCode.class);
		return seqId;
	}

	/***
	 * 6995和家庭同步序列
	 * zhangbo18
	 * @return
	 * @throws Exception
	 */
	public static String getPlanFamilyCircleSeq() throws Exception
	{
		String seqId = Dao.getSequence(SeqPlanFamilyCircle.class);
		return seqId;
	}

	public static String getSeqPlan() throws Exception
	{
		String seqId = Dao.getSequence(SeqPlan.class);
		return seqId;
	}

	public static String getSeqRuuid() throws Exception
	{
		String seqId = Dao.getSequence(SeqRuuid.class);
		return seqId;
	}

	public static String getSeqExpireId() throws Exception
	{
		String seqId = Dao.getSequence(SeqExpireId.class);
		return seqId;
	}
	public static String getSeqTempletId() throws Exception
	{
		String seqId = Dao.getSequence(TempletIdSeq.class,Route.CONN_CRM_CEN);
		return seqId;
	}

	public static String getSeqOperId() throws Exception
	{
		String seqId = Dao.getSequence(OperIdSeq.class,Route.CONN_CRM_CEN);
		return seqId;
	}

	public static String getSeqApnIpId() throws Exception
	{
		String seqId = Dao.getSequence(SeqApnIpId.class,Route.CONN_CRM_CEN);
		return seqId;
	}

	public static String getSeqCmpayLogId() throws Exception
	{
		String seqId = Dao.getSequence(SeqCmpayLogId.class);
		return seqId;
	}

	public static String getSeqUserIdentCode() throws Exception
	{
		String seqId = Dao.getSequence(SeqUserIdentCode.class);
		return seqId;
	}

	public static String getTermAppOperLogId() throws Exception
	{
		String seqId = Dao.getSequence(SeqTermAppIntfLogId.class);
		return seqId;
	}

	public static String getPreCreateuserPsptId() throws Exception
	{
		String seqId = Dao.getSequence(SeqPreCreateuserPsptId.class);
		return seqId;
	}

	public static String getGPRSCardLogId() throws Exception
	{
		String seqId = Dao.getSequence(SeqGPRSCardLogId.class,Route.CONN_CRM_CEN);
		return seqId;
	}

	public static String getUserExchScoreId() throws Exception
	{
		String seqId = Dao.getSequence(SeqUserExchScoreId.class);
		return seqId;
	}
	public static String getSeqCustImportId() throws Exception
	{
		String seqId = Dao.getSequence(SeqCustImportId.class);
		return seqId;
	}

	public static String getXmlInfoId() throws Exception
	{
		String seqId = Dao.getSequence(SeqXmlInfoId.class, Route.CONN_CRM_CEN);
		return seqId;
	}


	public static String getWorkCheckId() throws Exception
	{
		String seqId = Dao.getSequence(SeqWorkChkId.class);
		return seqId;
	}


	public static String getHandOverId()throws Exception
	{
		String seqId = Dao.getSequence(SeqHandOverId.class);
		return seqId;
	}

	public static String getRedGiftMid()throws Exception
	{
		String seqId = Dao.getSequence(SeqRedGiftId.class);
		return seqId;
	}

	public static String getEmbId()throws Exception
	{
		String seqId = Dao.getSequence(SeqEmbId.class);
		return seqId;
	}

	public static String getBWManagerId()throws Exception
	{
		String seqId = Dao.getSequence(SeqBWManagerId.class);
		return seqId;
	}

	public static String getTTGHResId()throws Exception
	{
		String seqId = Dao.getSequence(SeqTTGHResId.class);
		return seqId;
	}


	public static String getMinutesId() throws Exception
	{
		String seqId = Dao.getSequence(SeqMinutesId.class);
		return seqId;
	}

	public static String getHandleComId() throws Exception
	{
		String seqId = Dao.getSequence(SeqHandleComId.class);
		return seqId;
	}
	public static String getScoreRaffleId() throws Exception
	{
		String seqId = Dao.getSequence(SeqScoreRaffleId.class,Route.CONN_CRM_CEN);
		return seqId;
	}
	public static String getMailReqSnId() throws Exception
	{
		String seqId = Dao.getSequence(MailReqSnSeq.class,Route.CONN_CRM_CEN);
		return seqId;
	}
	public static String getWxwbId() throws Exception {
		String seqId = Dao.getSequence(SeqWxwbId.class);

		return seqId;
	}
	public static String getseqId() throws Exception {
		String seqId = Dao.getSequence(SeqSPSeqID.class);

		return seqId;
	}

	public static String getMeLogId() throws Exception
	{
		String seqId = Dao.getSequence(SeqMeLogId.class);

		return seqId;
	}

	public static String getGroupWorknote() throws Exception
	{
		String seqId = Dao.getSequence(SeqGroupWorknote.class);

		return seqId;
	}

	public static String getProjectId() throws Exception
	{
		String seqId = Dao.getSequence(SeqProjectId.class);

		return seqId;
	}
	public static String getEspSynId() throws Exception {
		String seqId = Dao.getSequence(SeqEspSynId.class,Route.CONN_CRM_CEN);
		return seqId;
	}

	/* 车联网审批序列号
	 */

	public static String getCarGroupSeq() throws Exception
	{
		String seqId =  Dao.getSequence(SeqCarGroup.class);
		return seqId;
	}

	public static String getIbsysId() throws Exception {
		String seqId = Dao.getSequence(SeqIbsysId.class, Route.getJourDb(Route.getJourDb()));
		return seqId;
	}

	public static String getSubIbsysId() throws Exception {
		String seqId = Dao.getSequence(SeqSubIbsysid.class, Route.getJourDb(Route.getJourDb()));
		return seqId;
	}

	public static String getAsynId() throws Exception {
		String seqId = Dao.getSequence(SeqAsynId.class, Route.getJourDb(Route.getJourDb()));
		return seqId;
	}
	
	public static String getGardenActivityInfoSeq() throws Exception
    {
		String seqId =  Dao.getSequence(SeqGardenActivityInfo.class, Route.CONN_CRM_CG);
		return seqId;
    }
	
	public static String getAttrSeq() throws Exception {
		String seqId = Dao.getSequence(SeqEopAttr.class, Route.getJourDb(Route.getJourDb()));
		return seqId;
	}

	public static String getBusiformNodeId() throws Exception {
		String seqId = Dao.getSequence(BusiformNodeIdSeq.class,Route.getJourDb(Route.getJourDb()));
		return seqId;
	}
	public static String getArchivesId() throws Exception{
		String seqId = Dao.getSequence(ArchivesIdSeq.class, Route.CONN_CRM_CG);
		return seqId;
	}
	public static String getAgreementId() throws Exception{
		String seqId = Dao.getSequence(AgreemnetIdSeq.class, Route.CONN_CRM_CG);
		return seqId;
	}
	public static String getTpOrderId() throws Exception{
		String seqId = Dao.getSequence(TpOrderSeq.class);
		return seqId;
	}
	public static String getTpOrderDetailId() throws Exception{
		String seqId = Dao.getSequence(TpOrderDetailSeq.class);
		return seqId;
	}
	public static String getTpOrderRelId() throws Exception{
		String seqId = Dao.getSequence(TpOrderRelSeq.class);
		return seqId;
	}
	public static String getTpOrderOperId() throws Exception{
		String seqId = Dao.getSequence(TpOrderOperSeq.class);
		return seqId;
	}

}
