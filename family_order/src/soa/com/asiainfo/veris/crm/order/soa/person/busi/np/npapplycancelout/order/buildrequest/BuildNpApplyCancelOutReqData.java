package com.asiainfo.veris.crm.order.soa.person.busi.np.npapplycancelout.order.buildrequest;



import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserNpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeHistoryInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.np.npapplycancelout.order.requestdata.NpApplyCancelOutReqData;

public class BuildNpApplyCancelOutReqData extends BaseBuilder implements
		IBuilder {

	@Override
	public void buildBusiRequestData(IData param, BaseReqData brd)
			throws Exception {
		String serialNumber = param.getString("SERIAL_NUMBER");
		if(StringUtils.isBlank(serialNumber)){
		    
		    serialNumber = param.getString("NPCODE");
		}
		
		NpApplyCancelOutReqData reqData = (NpApplyCancelOutReqData) brd;
		String userTagSet = reqData.getUca().getUser().getUserTagSet();
	
		String cancel_tag = "";
		if(StringUtils.isBlank(userTagSet)){
			cancel_tag = "0";
		}else if("4".equals(userTagSet.substring(0, 1)) || "8".equals(userTagSet.substring(0, 1))){
			CSAppException.apperr(CrmUserNpException.CRM_USER_NP_116034, serialNumber);
		}

		reqData.setCancelTag(cancel_tag);
		if(StringUtils.isBlank(cancel_tag)){
			IDataset ids = TradeInfoQry.getTradeInfoBySelByCheckNofinish(reqData.getUca().getUserId(), reqData.getUca().getCustId(), "42");
			if(IDataUtil.isNotEmpty(ids)){
				int recordCount = ids.getData(0).getInt("RECORDCOUNT");
				if(recordCount>=1){
					CSAppException.apperr(CrmUserNpException.CRM_USER_NP_116037, serialNumber);
				}
			}
		}
		
		
		IDataset tradeHis = TradeHistoryInfoQry.getTradeHisInfos(reqData.getUca().getUserId());
		
		if (IDataUtil.isEmpty(tradeHis) || tradeHis.size()!=1)
        {
		    CSAppException.apperr(CrmUserNpException.CRM_USER_NP_9527, "115000 获取SERIAL_NUMBER=["+serialNumber+"],TRADE_TYPE_CODE=41的历史台账信息失败！");
        }
		String oldTradeId = tradeHis.getData(0).getString("TRADE_ID");
		reqData.setCancelTradeId(oldTradeId);
		reqData.setCancelType(param.getString("CANCEL_TYPE",""));
//		if(IDataUtil.isNotEmpty(tradeHis) && tradeHis.size() != 1){
//			IDataset ids =TradeInfoQry.getTradeInfosByUserIdCancelTag(reqData.getUca().getUserId(),"0");
//			if(IDataUtil.isEmpty(ids)){
//				reqData.setCancelType("0");
//			}
//		}
		
	}

	@Override
	public BaseReqData getBlankRequestDataInstance() {
		return new NpApplyCancelOutReqData();
	}

	 protected void checkBefore(IData input, BaseReqData reqData) throws Exception
    {
     
    }
}
