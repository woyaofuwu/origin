package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.reg;

import java.util.List;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.pccbusiness.PCCBusinessQry;


public class ChangeDiscntReserVationAction  implements ITradeAction{
	
	protected static final Logger log = Logger.getLogger(ChangeDiscntReserVationAction.class);

	@Override
	public void executeAction(BusiTradeData btd) throws Exception {
		
		String pre_type=btd.getRD().getPageRequestData().getString("PRE_TYPE","0");
		if(BofConst.PRE_TYPE_CHECK.equals(pre_type)){ //预校验不受理
			return;
		}
		
		UcaData uca = btd.getRD().getUca();
		
		List<DiscntTradeData> discntTrades=btd.getTradeDatas(TradeTableEnum.TRADE_DISCNT);
		for(DiscntTradeData discnt:discntTrades){
			String discnt_code = discnt.getDiscntCode();
			String mod_tag = discnt.getModifyTag();
			//优惠时间
			String startDate =SysDateMgr.decodeTimestamp(discnt.getStartDate(),"yyyy-MM-dd");
			//获取当前时间
			String timeSet =SysDateMgr.decodeTimestamp(SysDateMgr.getSysDateYYYYMMDDHHMMSS(),"yyyy-MM-dd");
			
			if(!mod_tag.equals(BofConst.MODIFY_TAG_ADD) && !mod_tag.equals(BofConst.MODIFY_TAG_DEL)){
			   continue;	
			}
			//是否存在指定优惠
			IDataset offers = CommparaInfoQry.getCommparaInfoBy5("CSM", "9108", "RESTRICTIVE_DISCNT", discnt_code ,CSBizBean.getTradeEparchyCode(),null);
			boolean isTagid = false;
			if (IDataUtil.isNotEmpty(offers)) {
				isTagid=true;
			}
			
			if(mod_tag.equals(BofConst.MODIFY_TAG_ADD) && isTagid){
					
				//立即生效时
				if (timeSet.compareTo(startDate)==0) {
						IData inParamNew = new DataMap();
						inParamNew.put("USER_ID", uca.getUserId());
						inParamNew.put("USRIDENTIFIER", "86"+uca.getSerialNumber());
						inParamNew.put("ACCEPT_MONTH", SysDateMgr.getCurMonth());
						IDataset result= PCCBusinessQry.qryPccOperationTypeForSubscriber(inParamNew);
						if (IDataUtil.isNotEmpty(result)) {
							String usrStatus = result.getData(0).getString("USR_STATUS", "");
							String execState = result.getData(0).getString("EXEC_STATE", "");
							//usrStatus 1为解速标识；2、3、4、6、8为限速标识
							//execState 处理状态 0-入库、1-处理中、2-处理完成、9-处理失败
							if (("2".equals(usrStatus) ||"3".equals(usrStatus) ||"4".equals(usrStatus)||
								"6".equals(usrStatus) ||"8".equals(usrStatus)) && "2".equals(execState)) 
							{
								SvcTradeData svcData = new SvcTradeData();
								svcData.setUserId(uca.getUserId());
								svcData.setUserIdA("-1");
								svcData.setElementId("84071642");
								svcData.setProductId(discnt.getProductId());
								svcData.setPackageId("-1");
								svcData.setMainTag("0");
								svcData.setModifyTag(BofConst.MODIFY_TAG_ADD);
								svcData.setStartDate(SysDateMgr.getSysDate());
								svcData.setEndDate(SysDateMgr.getLastDateThisMonth4WEB());
								svcData.setInstId(SeqMgr.getInstId());
								btd.add(btd.getRD().getUca().getSerialNumber(), svcData);
								
							}
						}
				}
				break;
			}
		}
	}
}