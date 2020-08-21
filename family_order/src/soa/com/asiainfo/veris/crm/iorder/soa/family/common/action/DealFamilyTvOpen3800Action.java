package com.asiainfo.veris.crm.iorder.soa.family.common.action;

import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.iorder.pub.family.consts.FamilyConstants;
import com.asiainfo.veris.crm.iorder.pub.family.exception.FamilyException;
import com.asiainfo.veris.crm.iorder.soa.family.common.query.FamilyUserMemberQuery;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.FamilyMemberChaTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.FamilyUserMemberTradeData;
import com.asiainfo.veris.crm.order.soa.person.busi.topsetbox.order.requestdata.TopSetBoxRequestData;

/**
 * 家庭魔百和新开新增member属性
 * @author zhangxi
 *
 */
public class DealFamilyTvOpen3800Action implements ITradeAction {

	@SuppressWarnings("unchecked")
	@Override
	public void executeAction(@SuppressWarnings("rawtypes") BusiTradeData btd) throws Exception {

		TopSetBoxRequestData reqData = (TopSetBoxRequestData)btd.getRD();
		DataMap input = (DataMap) reqData.getPageRequestData();

		String familyMemberInstId = input.getString("FAMILY_MEMBER_INST_ID");

		IDataset  results = FamilyUserMemberQuery.queryMembersByUserInstId(familyMemberInstId);

		if(IDataUtil.isEmpty(results)){
			CSAppException.apperr(FamilyException.CRM_FAMILY_18,familyMemberInstId);
		}

		FamilyUserMemberTradeData familyMemberInfo = new FamilyUserMemberTradeData(results.first());

		 FamilyMemberChaTradeData fmtd = new FamilyMemberChaTradeData();
		 fmtd.setInstId(SeqMgr.getInstId());
         fmtd.setFamilyUserId(familyMemberInfo.getFamilyUserId());
         fmtd.setMemberUserId(reqData.getUca().getUserId());
         fmtd.setChaType(FamilyConstants.CHA_TYPE.MANAGER);
         fmtd.setRelInstId(familyMemberInstId);
         fmtd.setChaCode(FamilyConstants.FamilyMemCha.FAMILY_TOPSETBOX.getValue());
         fmtd.setChaValue(reqData.getResNo());
         fmtd.setStartDate(familyMemberInfo.getStartDate());
         fmtd.setEndDate(familyMemberInfo.getEndDate());
         fmtd.setModifyTag(familyMemberInfo.getModifyTag());
         btd.add(reqData.getUca().getSerialNumber(), fmtd);

	}

}
