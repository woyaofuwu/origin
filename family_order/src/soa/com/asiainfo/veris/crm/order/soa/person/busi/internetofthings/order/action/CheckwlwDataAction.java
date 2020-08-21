package com.asiainfo.veris.crm.order.soa.person.busi.internetofthings.order.action;

import java.util.List;

import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

/**
 * 专门校验、限制物联网产品操作类
 * 
 * @author pengxin
 */
public class CheckwlwDataAction implements ITradeAction {

	@SuppressWarnings("rawtypes")
	public void executeAction(BusiTradeData btd) throws Exception 
	{
		BaseReqData reqData = btd.getRD();
		UcaData uca = reqData.getUca();
		String tradeTypeCode = reqData.getTradeType().getTradeTypeCode();
		
		if (!"PWLW".equals(uca.getBrandCode())) {
			return;
		}
		
		if ("110".equals(tradeTypeCode)) {// 产品变更
			
        	// 达量降速自动降速资费的时间处理
        	dealSlowDownDiscnt(uca,tradeTypeCode,btd);

		} else if ("10".equals(tradeTypeCode)) {// 开户
			
        	// 达量降速自动降速资费的时间处理
        	dealSlowDownDiscnt(uca,tradeTypeCode,btd);
        	
		} 
	}

	private void dealSlowDownDiscnt(UcaData uca, String tradeTypeCode, BusiTradeData btd) throws Exception {
		
		//获取自动达量降速（月包）产品的资费编码
		IDataset autoSlowdownDiscntConfig = CommparaInfoQry.getInfoParaCode1_2("CSM","9013","I00010101602","I00010101010");
		if(IDataUtil.isEmpty(autoSlowdownDiscntConfig)){
			return ;
		}
		
		StringBuilder autoSlowdownDiscntsb = new StringBuilder(100); 
		for(int i = 0; i < autoSlowdownDiscntConfig.size(); i++){
			autoSlowdownDiscntsb.append(autoSlowdownDiscntConfig.getData(i).getString("PARAM_CODE")).append(",");
		}
		String autoSlowdownDiscnts = autoSlowdownDiscntsb.toString();
		List<DiscntTradeData> slowDownDiscntTradeList = uca.getUserDiscntsByDiscntCodeArray(autoSlowdownDiscnts);
		if(slowDownDiscntTradeList.isEmpty()){
		   return;	
		}
		
		if ("10".equals(tradeTypeCode)){//开户的时候，因为所有产品都是立即生效，需要代码修改自动降速资费的生效时间为下月初
			for(DiscntTradeData discntTradeData : slowDownDiscntTradeList ){
				discntTradeData.setStartDate(SysDateMgr.getFirstDayOfNextMonth());
			}
		}
		
        if ("110".equals(tradeTypeCode)){//退订还未生效的自动降速资费，需要修改失效时间为原生效时间
			for(DiscntTradeData discntTradeData : slowDownDiscntTradeList ){
				String startDate = discntTradeData.getStartDate();
				String modifyTag = discntTradeData.getModifyTag();
				if(BofConst.MODIFY_TAG_DEL.equals(modifyTag) && SysDateMgr.compareTo(startDate, SysDateMgr.getSysTime())>0){
					discntTradeData.setEndDate(startDate);
				}
			}
		}
	}

	
}
