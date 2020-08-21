package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.reg;

//import java.util.Collections;
import java.util.List;

import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.requestdata.ChangeProductReqData;

/**
 * 办理视频彩铃服务时，后台同时绑定“视频彩铃服务减免优惠”
 * @author Administrator
 *
 */
public class BindVideoDiscntAction implements ITradeAction{

	@Override
	public void executeAction(BusiTradeData btd) throws Exception {
		
		ChangeProductReqData changeProductRD=(ChangeProductReqData)btd.getRD();
		
		UcaData uca=changeProductRD.getUca();
		
		//获取服务台账数据
		List<SvcTradeData> svcTrade=btd.getTradeDatas(TradeTableEnum.TRADE_SVC);
		if(svcTrade!=null&&svcTrade.size()>0){
			for(SvcTradeData svc:svcTrade){
				//视频彩铃服务
				if("20171201".equals(svc.getElementId())){
					//新增
					if(BofConst.MODIFY_TAG_ADD.equals(svc.getModifyTag())){
						List<SvcTradeData> volteSvc=uca.getUserSvcBySvcId("190");
						if(volteSvc==null||volteSvc.size()==0){
							CSAppException.apperr(CrmCommException.CRM_COMM_103,"办理视频彩铃必须有VoLTE服务");
						}
						/*List<SvcTradeData> clSvc=uca.getUserSvcBySvcId("20");
						if(clSvc==null||clSvc.size()==0){
							CSAppException.apperr(CrmCommException.CRM_COMM_103,"办理视频彩铃必须有彩铃服务");
						}*/
						if("2018".equals(SysDateMgr.getSysTime().substring(0, 4))){
							addDiscnt(btd, uca);
						}
					//删除
					}else if(BofConst.MODIFY_TAG_DEL.equals(svc.getModifyTag())){
						endDiscnt(btd, uca);
					}
				}
			}
		}
		
	}
	
	/**
	 * 添加优惠
	 * @param btd
	 * @param uca
	 * @throws Exception
	 */
    private void addDiscnt(BusiTradeData btd, UcaData uca)throws Exception{
    	  DiscntTradeData newDiscnt = new DiscntTradeData();
		  newDiscnt.setUserId(uca.getUserId());
	      newDiscnt.setProductId("-1");
	      newDiscnt.setPackageId("-1");
	      newDiscnt.setElementId("84010238");
	      newDiscnt.setInstId(SeqMgr.getInstId());
	      newDiscnt.setModifyTag(BofConst.MODIFY_TAG_ADD);
	      newDiscnt.setSpecTag("0");
	      newDiscnt.setStartDate(SysDateMgr.getSysTime());
	      newDiscnt.setEndDate("2018-12-31 23:59:59");
	      newDiscnt.setRemark("办理视频彩铃服务时，后台同时绑定“视频彩铃服务减免优惠”");
	      btd.add(uca.getSerialNumber(), newDiscnt);
    }
    /**
	 * 结束优惠
	 * @param btd
	 * @param uca
	 * @throws Exception
	 */
    private void endDiscnt(BusiTradeData btd, UcaData uca)throws Exception{
    	List<DiscntTradeData> discntTrade=uca.getUserDiscntByDiscntId("84010238");
    	if(discntTrade!=null&&discntTrade.size()>0){
    		DiscntTradeData discnt=discntTrade.get(0).clone();
    		discnt.setEndDate(SysDateMgr.getSysTime());
    		discnt.setModifyTag(BofConst.MODIFY_TAG_DEL);
			btd.add(uca.getSerialNumber(), discnt);
    	}
    }
}
