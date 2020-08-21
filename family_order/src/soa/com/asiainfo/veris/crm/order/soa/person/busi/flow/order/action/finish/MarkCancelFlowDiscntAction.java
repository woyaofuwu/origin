package com.asiainfo.veris.crm.order.soa.person.busi.flow.order.action.finish;

import java.util.List;
import org.apache.log4j.Logger;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

/**
 * 
 * @author cy
 * 关于修改一级能开配置咪咕爱奇艺联合会员产品优惠到期后自动退订规则通知需求
 *在定向视频流量包主动退订时打标记，以便区分年包等资费自动失效去同步给平台
 */
public class MarkCancelFlowDiscntAction implements ITradeAction {
           
	private static final Logger logger = Logger.getLogger(MarkCancelFlowDiscntAction.class);				

	public void executeAction(BusiTradeData btd) throws Exception {
		logger.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>执行MarkCancelFlowDiscntAction26--------"+btd);
		BaseReqData reqData = btd.getRD();
		UcaData uca = reqData.getUca();
		List<DiscntTradeData> userDiscnts = uca.getUserDiscnts();
		logger.debug("checkPBossHDJ--userDiscnts="+userDiscnts);
		if(userDiscnts==null){
			return;
		}
		for (int i = 0; i < userDiscnts.size(); i++) {
			DiscntTradeData userDiscntItem = userDiscnts.get(i);
			logger.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>执行MarkCancelFlowDiscntAction36--------"+userDiscntItem);
			if(BofConst.MODIFY_TAG_DEL.equals(userDiscntItem.getModifyTag())){//退订资费的时候
				//如果是定向视频流量年包，半年包类的自动失效的
				logger.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>执行MarkCancelFlowDiscntAction38--------"+userDiscntItem);
				 IDataset paraList = CommparaInfoQry.getCommparaByCodeCode1("CSM", "2017", userDiscntItem.getDiscntCode(), "YEAR_VIDEO_PKG");
					logger.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>执行MarkCancelFlowDiscntAction41--------"+paraList);
			     if(IDataUtil.isEmpty(paraList)){
			    	 continue; 
			     }
			     userDiscntItem.setRsrvStr5("A");//打标记代表已经同步过
					logger.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>执行MarkCancelFlowDiscntAction47--------"+userDiscntItem);			     
			}
		}
	}
}
