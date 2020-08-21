package com.asiainfo.veris.crm.order.soa.person.busi.onecardmultino.action;

import java.util.List;

import org.apache.log4j.Logger;

import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.collections.CollectionUtils;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PlatSvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.onecardmultino.OneCardMultiNoBean;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
/**
 * BUG20190730160553和多号500000局数据未绑定减免优惠问题
 * 和多号大类的业务代码500000局数据按平台要求，应是用户订购后两个月内免费使用
 * 用户第一次办和多号订购500000局数据绑定减免优惠99999062
 * @author mqx 20190807
 *
 */
public class OneCardMultiNoDiscountAction implements ITradeAction
{
    protected static Logger log = Logger.getLogger(OneCardMultiNoDiscountAction.class);
    
    public void executeAction(BusiTradeData btd) throws Exception
    {
        log.debug("*******OneCardMultiNoDiscountAction************");
        List<PlatSvcTradeData> tradePlatSvcs = btd.getTradeDatas(TradeTableEnum.TRADE_PLATSVC);
        if(CollectionUtils.isNotEmpty(tradePlatSvcs)) {
        	PlatSvcTradeData platSvcTrade = tradePlatSvcs.get(0);
        	String serviceId = platSvcTrade.getElementId();
        	String modifyTag = platSvcTrade.getModifyTag();
        	
            log.debug("*******OneCardMultiNoDiscountAction************serviceId="+serviceId+";modifyTag="+modifyTag);
        	//如果是500000局数据
        	if("84053951".equals(serviceId)){
        		String userId = platSvcTrade.getUserId();
        		String instId = SeqMgr.getInstId();
        		
        		if(BofConst.MODIFY_TAG_ADD.equals(modifyTag)){
	        		//新增500000局数据
        			IDataset userDiscnt = UserDiscntInfoQry.getAllDiscntByUser_2(userId,"99999062");//查用户所有的优惠
                    log.debug("*******OneCardMultiNoDiscountAction************adduserDiscnt="+userDiscnt);

	        		if(IDataUtil.isNotEmpty(userDiscnt)&&userDiscnt.size()!=0){
	        			return;//绑定过减免优惠
	        		}else {
	        			DiscntTradeData discntTradeData = new DiscntTradeData();
	        			discntTradeData.setRemark("订购500000局数据绑定减免优惠");
	        			discntTradeData.setModifyTag("0");//0:新增,1:删除,2:修改
	        			discntTradeData.setUserId(userId);
	        			discntTradeData.setInstId(instId);	
	        			discntTradeData.setStartDate(SysDateMgr.getSysTime());
	        			discntTradeData.setEndDate(SysDateMgr.getNextMonthLastDate());
	        			discntTradeData.setUserIdA("-1");
	        			discntTradeData.setSpecTag("2"); // 特殊优惠标记
	        			discntTradeData.setPackageId("-1");
	        			discntTradeData.setProductId("-1");
	        			
	        			discntTradeData.setRelationTypeCode(OneCardMultiNoBean.RELATION_TYPE_CODE);
	        			discntTradeData.setElementId("99999062");
	        			discntTradeData.setElementType(BofConst.ELEMENT_TYPE_CODE_DISCNT);
	                    log.debug("*******OneCardMultiNoDiscountAction************discntTradeData="+discntTradeData);

	        			btd.add(btd.getMainTradeData().getSerialNumber(), discntTradeData);	
	        		}
        		}
        		
        		if(BofConst.MODIFY_TAG_DEL.equals(modifyTag)){
	        		//取消500000局数据
            		IDataset userDiscnt = UserDiscntInfoQry.getAllDiscntByUserId(userId,"99999062");//查用户有效的优惠
                    log.debug("*******OneCardMultiNoDiscountAction************deluserDiscnt="+userDiscnt);

	        		if(IDataUtil.isEmpty(userDiscnt)){
	        			return;//未绑定过减免优惠
	        		}else {
	        			DiscntTradeData discntTradeData = new DiscntTradeData(userDiscnt.getData(0));
	        			discntTradeData.setRemark("取消订购500000局数据解绑减免优惠");
	        			discntTradeData.setModifyTag("1");//0:新增,1:删除,2:修改
	        			discntTradeData.setEndDate(SysDateMgr.getSysTime());
	                    log.debug("*******OneCardMultiNoDiscountAction************discntTradeData="+discntTradeData);

	        			btd.add(btd.getMainTradeData().getSerialNumber(), discntTradeData);	
	        		}
        		}
        	}
        	
        }
        
    }
    
}
