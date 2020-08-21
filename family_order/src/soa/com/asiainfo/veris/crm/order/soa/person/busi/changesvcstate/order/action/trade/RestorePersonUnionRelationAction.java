package com.asiainfo.veris.crm.order.soa.person.busi.changesvcstate.order.action.trade;

import org.apache.commons.lang.StringUtils;

import com.ailk.common.data.IData;

import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PayRelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.RelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.PayRelaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;

public class RestorePersonUnionRelationAction implements ITradeAction {

	@Override
	public void executeAction(BusiTradeData btd) throws Exception {
		
		String user_id = btd.getRD().getUca().getUserId();
		String acct_Id = btd.getRD().getUca().getAcctId();
		String mainSn = btd.getRD().getUca().getSerialNumber();
		IDataset otherData = queryUserOtherInfosByIdRsrvCode(null, user_id, "END_PAYREL");
		if(IDataUtil.isNotEmpty(otherData)){
			if("7325解除统付关系".equals(otherData.getData(0).getString("RSRV_VALUE"))){
				IData mebParam = new DataMap();
				IData mainParam = new DataMap();
				mainParam.put("USER_ID_B", user_id);
				mainParam.put("RELATION_TYPE_CODE", "56");
				mainParam.put("ROLE_CODE_B", "1");
				IDataset mainNum = RelaUUInfoQry.qryAllMebInfoRelaAndMain(mainParam);// 查全部
				if (IDataUtil.isNotEmpty(mainNum)) {
						System.out.println("===========mainNum======"+mainNum.getData(0).toString());
						String vuid_a=mainNum.getData(0).getString("USER_ID_A");
						mebParam.put("USER_ID_A",vuid_a );
						mebParam.put("RELATION_TYPE_CODE", "56");
						IDataset AllMebAndMain = RelaUUInfoQry.qryAllMebInfoRelaAndMain(mebParam);
						boolean flag=false;
						IData mainData =new DataMap();
						for (int k = 0; k < AllMebAndMain.size(); k++) {
							IData data = AllMebAndMain.getData(k);
							String rid=data.getString("ROLE_CODE_B");
							if ("2".equals(rid)&&"欠费停机后信控发起的7325解除统付关系-物联网副号".equals(data.getString("REMARK"))) {
								RelationTradeData newRelation = new RelationTradeData(data);
								newRelation.setStartDate(SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND));
								newRelation.setEndDate("2050-12-31 23:59:59");
								newRelation.setModifyTag(BofConst.MODIFY_TAG_UPD);
								newRelation.setRemark("缴费开机-恢复个人代付关系");
								btd.add(mainSn, newRelation);
								IData param = new DataMap();
								param.put("USER_ID_A", data.getString("USER_ID_A"));
								param.put("RELATION_TYPE_CODE", "56");
								param.put("ROLE_CODE_B", "1");
								IDataset dataset = RelaUUInfoQry.qryAllMebInfoRelaAndMain(param);
								if(IDataUtil.isNotEmpty(dataset)){
									System.out.println("===========dataset======"+dataset.getData(0).toString());
									mainData=dataset.getData(0);
									flag=true;
								}
								IDataset payrela = PayRelaInfoQry.getPayrelationByUserIdAndAcctId(acct_Id,newRelation.getUserIdB(), null);
								for (int j = 0; j < payrela.size(); j++) {
									String remark=payrela.getData(j).getString("REMARK");
									if(StringUtils.isBlank(remark)){
										continue;
									}else if ("41000".equals(payrela.getData(j).getString("PAYITEM_CODE"))
											&&"欠费停机后信控发起的7325解除统付关系-物联网副号".equals(remark)) {
										PayRelationTradeData payRelationData = new PayRelationTradeData(payrela.getData(j));
										payRelationData.setStartCycleId(SysDateMgr.getSysDateYYYYMMDD());
										payRelationData.setEndCycleId("20501231");
										payRelationData.setModifyTag(BofConst.MODIFY_TAG_UPD);
										payRelationData.setInstId(payrela.getData(j).getString("INST_ID"));
										payRelationData.setRemark("缴费开机-恢复个人代付关系");
										btd.add(mainSn, payRelationData);
										break;
									}
								}
							}
						}
						if(flag){	
							RelationTradeData newRelation = new RelationTradeData(mainData);
							newRelation.setStartDate(SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND));
							newRelation.setEndDate("2050-12-31 23:59:59");
							newRelation.setModifyTag(BofConst.MODIFY_TAG_UPD);
							newRelation.setRemark("缴费开机-恢复个人代付关系");
							btd.add(mainSn, newRelation);
							//终止othder表的结束时间
							OtherTradeData otherTradeData = new OtherTradeData(otherData.getData(0));
			                otherTradeData.setEndDate(SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND));
			                otherTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL); 
			                btd.add(btd.getRD().getUca().getSerialNumber(), otherTradeData);   
						}		
				}
			}
		}
	}
	/**
	 * 获取TF_F_USER_OTHER表数据
	 * 
	 * @param tradeId
	 * @param userId
	 * @param rsrvCode
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryUserOtherInfosByIdRsrvCode(String tradeId, String userId, String rsrvCode) throws Exception
	{
		IData data = new DataMap();
		data.put("USER_ID", userId);
		data.put("RSRV_VALUE_CODE", rsrvCode);
		IDataset userOtherInfos = Dao.qryByCode("TF_F_USER_OTHER", "SEL_OTHERINFO_BY_USERID_RSRVCODE", data);
		if(IDataUtil.isNotEmpty(userOtherInfos)){
			return userOtherInfos;
		}
		return null;
	}
}
