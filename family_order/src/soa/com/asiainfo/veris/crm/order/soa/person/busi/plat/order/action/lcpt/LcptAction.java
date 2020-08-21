package com.asiainfo.veris.crm.order.soa.person.busi.plat.order.action.lcpt;

import java.util.ArrayList;
import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.IProductModuleAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.configdata.plat.PlatOfficeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.PlatSvcData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.ProductModuleTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PlatSvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.plat.PlatInfoQry;

public class LcptAction implements IProductModuleAction {

	@Override
	public void executeProductModuleAction(ProductModuleTradeData dealPmtd,
			UcaData uca, BusiTradeData btd) throws Exception {
		// TODO Auto-generated method stub
		PlatSvcTradeData pstd = (PlatSvcTradeData) dealPmtd;
		
		//当操作码为03，则表示此笔交易为变更操作
		//但操作码仍修改为订购，在扩展字段中区分
		IData prd = btd.getRD().getPageRequestData();
		
		IData param = new DataMap();
		param.put("USER_ID", uca.getUserId());
		param.put("BIZ_TYPE_CODE", "78");
		IDataset userPlatSvc = PlatInfoQry.getUserPlatSvcAndBizTypeCode(param, "SEL_BY_ID_TYPE");
		
		if (("03".equals(prd.getString("RSRV_STR5")) || 
				(null != userPlatSvc && !userPlatSvc.isEmpty()))
				&& PlatConstants.OPER_ORDER.equals(pstd.getOperCode())){
			pstd.setRsrvStr5("03");
		}
		
		List<PlatSvcTradeData> platSvcTradeDatas = uca.getUserPlatSvcs();
		List<SvcTradeData> userSvcList = uca.getUserSvcs();

		List<PlatSvcTradeData> userTempGpsps = new ArrayList<PlatSvcTradeData>();
		PlatOfficeData officeData = ((PlatSvcData) pstd.getPmd()).getOfficeData();
		IDataset gpspConfigs = PlatInfoQry.getPlatsvcLimit(officeData.getBizTypeCode(), 
				pstd.getElementId(), pstd.getOperCode());

		int count = gpspConfigs.size();
		IData timeConfig = new DataMap();
		for (int i = 0; i < count; i++) {
			IData config = gpspConfigs.getData(i);
			String serviceId = config.getString("SERVICE_ID_L");
			 String limitType = config.getString("LIMIT_TYPE");
			 String svcType = config.getString("SVC_TYPE");
			if("Z".equals(svcType) && "1".equals(limitType)){
				userTempGpsps.addAll(uca.getUserPlatSvcByServiceId(serviceId));
				timeConfig.put(serviceId, config);
			}
		}
		
		if (PlatConstants.OPER_CANCEL_ORDER.equals(pstd.getOperCode())) {
			pstd.setEndDate(SysDateMgr.getLastDateThisMonth());
			pstd.setBizStateCode(PlatConstants.STATE_OK);
		}else if(PlatConstants.OPER_ORDER.equals(pstd.getOperCode())){
			IData userPlat = null;
			if (null != userPlatSvc && userPlatSvc.size() > 0){
				userPlat = userPlatSvc.getData(0);
			}
			
			if(userTempGpsps.size()>0 || (null != userPlat && SysDateMgr.getFirstDayOfNextMonth().compareTo(userPlat.getString("END_DATE")) > 0)){
				pstd.setIsNeedPf("1");
				pstd.setStartDate(SysDateMgr.getFirstDayOfNextMonth());
			}else{
				pstd.setIsNeedPf("1");
				pstd.setStartDate(SysDateMgr.getSysTime());
			}
			
			
		}
		
		if (userTempGpsps.size() > 0) {
			for (int j = 0; j < userTempGpsps.size(); j++) {

				PlatSvcTradeData tempGpsp = userTempGpsps.get(j);

				if (tempGpsp.getElementId().equals(pstd.getElementId())
						|| !tempGpsp.getModifyTag().equals(
								BofConst.MODIFY_TAG_USER)) {
					continue;
				}

				PlatSvcTradeData gpspnew = tempGpsp.clone();
				gpspnew.setModifyTag(BofConst.MODIFY_TAG_DEL);
				gpspnew.setOperCode(PlatConstants.OPER_CANCEL_ORDER);
				gpspnew.setOprSource(pstd.getOprSource());
				gpspnew.setBizStateCode(PlatConstants.STATE_CANCEL);
				gpspnew.setOperTime(pstd.getOperTime());
				gpspnew.setIsNeedPf("1");
				
				if (tempGpsp.getStartDate().compareTo(
						SysDateMgr.getLastDateThisMonth()) > 0) {
					gpspnew.setEndDate(SysDateMgr.getLastSecond(pstd.getOperTime()
					));

				} else {
					gpspnew.setEndDate(SysDateMgr.getLastDateThisMonth());

				}
				btd.add(uca.getSerialNumber(), gpspnew);

			}
		}
	}
}
