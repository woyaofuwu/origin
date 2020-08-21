package com.asiainfo.veris.crm.order.soa.person.busi.flow;

import org.apache.log4j.Logger;

import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;

public class SynCancelFlowDiscntSVC extends CSBizService {
	private static final long serialVersionUID = 1L;
	static Logger log = Logger.getLogger(SynCancelFlowDiscntSVC.class);

	/**
	 * Aee月底调用，如果是年包，半年包等自动失效时生成退订工单，为了同步给第三方电商与内容计费
	 * 
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public IDataset SynFlowInfos(IData input) throws Exception {
		IDataset changeResult = new DatasetList();
		log.error("SynCancelFlowDiscntSVCxxxxxxxxxxxxxxxxxx31 " + input);

		if (!"".equals(input.getString("USER_ID", "").trim()) && !"".equals(input.getString("DISCNT_CODE", "").trim()) && !"".equals(input.getString("START_DATE", "").trim())) {
			IDataset userInfo = UserInfoQry.getUserInfoByUserId(input.getString("USER_ID", "").trim());
			if (IDataUtil.isEmpty(userInfo)) {
				return null;
			}
			IDataset paraList = CommparaInfoQry.getCommparaByCode1("CSM", "2017", input.getString("DISCNT_CODE", "").trim(), "YEAR_VIDEO_PKG", null);
			log.error("SynCancelFlowDiscntSVCxxxxxxxxxxxxxxxxxx41 " + paraList);

			if (IDataUtil.isEmpty(paraList)) {
				return null;
			}
			
			try {
				IData inParam = new DataMap();
				inParam.put("SERIAL_NUMBER", userInfo.getData(0).getString("SERIAL_NUMBER"));
				inParam.put("ELEMENT_ID", input.getString("DISCNT_CODE"));
				inParam.put("ELEMENT_TYPE_CODE", BofConst.ELEMENT_TYPE_CODE_DISCNT);
				inParam.put("MODIFY_TAG", BofConst.MODIFY_TAG_DEL);
				inParam.put("START_DATE", input.getString("START_DATE"));
				inParam.put("END_DATE", SysDateMgr.getLastDateThisMonth());
				inParam.put("IN_MODE_CODE", "0");
				inParam.put("BOOKING_TAG", "0");
				inParam.put("NO_TRADE_LIMIT", "TRUE");
				inParam.put("SKIP_RULE", "TRUE");

				inParam.put("TRADE_STAFF_ID","SUPERUSR");
				inParam.put("TRADE_EPARCHY_CODE",  CSBizBean.getTradeEparchyCode());
				inParam.put("TRADE_DEPART_ID",  "36601");
				inParam.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
				inParam.put("STAFF_ID", "SUPERUSR");
				inParam.put("DEPART_ID", "36601");
	   	       /*inParam.put("STAFF_ID", "SUPERUSR");
	   	     inParam.put("TRADE_EPARCHY_CODE", "0898");
	   	    inParam.put("DEPART_ID", "36601");*/
	   	   		
				log.error("SynCancelFlowDiscntSVCxxxxxxxxxxxxxxxxxx45 " + inParam);
				changeResult = CSAppCall.call("SS.ChangeProductRegSVC.ChangeProduct", inParam);
				log.error("SynCancelFlowDiscntSVCxxxxxxxxxxxxxxxxxx48 " + changeResult);
				
			} catch (Exception ex) {
				if (log.isDebugEnabled()) {
					log.debug("--SynCancelFlowDiscntSVC--" + ex.getMessage());
				}
				return null;
			}
			
		} else {/*

			// 是否为自动到期类的年包或半年包等
			IDataset paraList = CommparaInfoQry.getCommparaByCode1("CSM", "2017", null, "YEAR_VIDEO_PKG", null);
			log.error("SynCancelFlowDiscntSVCxxxxxxxxxxxxxxxxxx61 " + paraList);
			if (IDataUtil.isEmpty(paraList)) {
				return null;
			}
			for (int i = 0; i < paraList.size(); i++) {
				IData comparaInfo = paraList.getData(i);
				String discnt = comparaInfo.getString("PARAM_CODE");
				IData param = new DataMap();
				param.put("DISCNT_CODE", discnt);
				param.put("END_DATE", SysDateMgr.getAddMonthsLastDay(1));
				// 查出月底失效的资费，要么是主动退订了的，要么是自动到期的资费
				IDataset userDiscnt = UserDiscntInfoQry.getDiscntByEndDate(param);
				log.error("SynCancelFlowDiscntSVCxxxxxxxxxxxxxxxxxx73 " + userDiscnt);

				if (IDataUtil.isEmpty(userDiscnt)) {
    			continue;
				}
				for (int j = 0; j < userDiscnt.size(); j++) {
					IData discntInfo = userDiscnt.getData(j);
					// IDataset
					// userInfo=UserInfoQry.getUserInfoByUserId(discntInfo.getString("USER_ID"),
					// "0",input.getString("ROUTE_EPARCHY_CODE","0898"));
					IDataset userInfo = UserInfoQry.getUserInfoByUserId(discntInfo.getString("USER_ID"));

					log.error("SynCancelFlowDiscntSVCxxxxxxxxxxxxxxxxxx85 " + userInfo);

					if (IDataUtil.isEmpty(userInfo)) {
    				continue;
					}
					// 根据资费表的inst_id查如果在台账表对应的modify_tag=1则代表该笔资料是主动退订的，如果modify_tag=0,则代表是自动失效的，并没有主动退订过
					IDataset tradeInfo = TradeDiscntInfoQry.getTradeByUserIdAndDiscntCode(discntInfo);
					log.error("SynCancelFlowDiscntSVCxxxxxxxxxxxxxxxxxx92 " + tradeInfo);
					
					 * SELECT
					 * b.user_id,b.trade_id,b.discnt_code,b.inst_id,b.update_time
					 * ,b.modify_tag FROM tf_b_TRADE_discnt b WHERE
					 * b.user_id=:USER_ID and b.discnt_code=:DISCNT_CODE and
					 * b.inst_id=:INST_ID and b.modify_tag='1' and rownum<=1
					 * order by b.update_time desc
					 
					if (IDataUtil.isNotEmpty(tradeInfo)) {
    				continue;//主动退订则不处理
					}
	
					// 满足以上条件则调产品变更生成退订工单
					try {
						IData inParam = new DataMap();
						inParam.put("SERIAL_NUMBER", userInfo.getData(0).getString("SERIAL_NUMBER"));
						inParam.put("ELEMENT_ID", discntInfo.getString("DISCNT_CODE"));
						inParam.put("ELEMENT_TYPE_CODE", BofConst.ELEMENT_TYPE_CODE_DISCNT);
						inParam.put("MODIFY_TAG", BofConst.MODIFY_TAG_DEL);
						inParam.put("START_DATE", discntInfo.getString("START_DATE"));
						inParam.put("END_DATE", SysDateMgr.getLastDateThisMonth());
						log.error("SynCancelFlowDiscntSVCxxxxxxxxxxxxxxxxxx114 " + inParam);
						changeResult = CSAppCall.call("SS.ChangeProductRegSVC.ChangeProduct", inParam);
						log.error("SynCancelFlowDiscntSVCxxxxxxxxxxxxxxxxxx116 " + changeResult);

					} catch (Exception ex) {
						if (log.isDebugEnabled()) {
							log.debug("--SynCancelFlowDiscntSVC--" + ex.getMessage());
						}
    				continue;
					}
				}

			}
		*/}

		return changeResult;
	}
}
