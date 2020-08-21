
package com.asiainfo.veris.crm.order.soa.person.busi.oceanfront.trade;

import org.apache.log4j.Logger;

import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.oceanfront.action.finish.OceanFrontFinishAction;
import com.asiainfo.veris.crm.order.soa.person.busi.oceanfront.requsetdata.OceanFrontReqData;

/**
 * REQ201805160025_2018年海洋通业务办理开发需求
 * <br/>
 * 
 * @author zhuoyingzhi
 * @date 20180601
 * 
 */
public class OceanFrontTrade extends BaseTrade implements ITrade
{
	public static final Logger logger=Logger.getLogger(OceanFrontTrade.class);
	/**
	 * 修改other表的报停,报开标识
	 */
    public void createBusiTradeData(BusiTradeData btd) throws Exception
    { 
    	OceanFrontReqData  oceanReq=(OceanFrontReqData) btd.getRD();
	    String userId=btd.getMainTradeData().getUserId();
	    IDataset  userOtherList=UserOtherInfoQry.getUserOther(userId, "HYT");
	    logger.debug("--OceanFrontTrade----userOtherList:"+userOtherList);
	    if(IDataUtil.isNotEmpty(userOtherList)){
	    	OtherTradeData otherTradeData = new OtherTradeData(userOtherList.getData(0));
				    	otherTradeData.setModifyTag(BofConst.MODIFY_TAG_UPD);
				    	//报开：1     报停：0
				    	otherTradeData.setRsrvStr5(oceanReq.getOpenStopType());//报停\报开   标识
				    	otherTradeData.setRsrvStr6(oceanReq.getOceanRmark());//备注
	      logger.debug("--OceanFrontTrade----otherTradeData:"+otherTradeData);
            btd.add(userId, otherTradeData);
	    }
    } 
}
