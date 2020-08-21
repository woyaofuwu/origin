
package com.asiainfo.veris.crm.order.soa.person.busi.sharemeal.order.action;

import java.util.List;

import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ShareInfoTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ShareRelaTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ShareTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.sharemeal.ShareInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.sharemeal.order.requestdata.ShareMealReqData;

public class ShareTradePreAction implements ITradeAction
{

    // @Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
        ShareMealReqData reqData = (ShareMealReqData) btd.getRD();
        String userId = reqData.getUca().getUserId();
        String stadate = "";
        String endDate = "";
        String deldate = "";
        //start-wangsc10-20190402先查询用户下预约生效的可以共享的4g资费，如果没有再查已经生效的可以共享的4g资费。20190131wangsc10
        IDataset discntInfo = null;
        discntInfo = ShareInfoQry.queryDiscntsNEW(userId);
        if(IDataUtil.isNotEmpty(discntInfo) && null != discntInfo && discntInfo.size() > 0){
        	stadate = discntInfo.getData(0).getString("DISCNT_START_DATE");
        	endDate = discntInfo.getData(0).getString("DISCNT_END_DATE");
        	deldate =  btd.getRD().getAcceptTime();//如果删除全部副卡，那么主卡的结束时间为立即结束
        	//查询用户当前的共享成员,当成员中有已经生效的共享关系，则在删除主卡时，主卡的截止时间是本月底
        	String time = "N";
        	IDataset member = ShareInfoQry.queryMember(userId);
        	for (int i = 0; i < member.size(); i++) {
        		String stadateOLD = member.getData(i).getString("START_DATE","");
        		if(stadateOLD.compareTo(SysDateMgr.getSysTime()) < 0){
        			time = "Y";
        		}
			}
        	List<ShareRelaTradeData> relaList = btd.get("TF_B_TRADE_SHARE_RELA");
        	for (int i = 0; i < relaList.size(); i++) {
        		ShareRelaTradeData data = relaList.get(i);
        		String modifyTag = data.getModifyTag();
        		String roleCode = data.getRoleCode();
        		String stadateNew = data.getStartDate();//取新加副卡的生效时间
        		if(roleCode.equals("01")){//主卡：有预约生效的共享资源处理
        			if(modifyTag.equals(BofConst.MODIFY_TAG_ADD)){
            			data.setStartDate(stadate);
            		}
            		
            		if(modifyTag.equals(BofConst.MODIFY_TAG_DEL) && time.equals("N")){
            			data.setEndDate(deldate);
            		}else if(modifyTag.equals(BofConst.MODIFY_TAG_DEL) && time.equals("Y")){
            			data.setEndDate(SysDateMgr.getLastDateThisMonth());
            		}
        		}
        		if(roleCode.equals("02")){//副卡：有预约生效的共享资源处理
        			if(modifyTag.equals(BofConst.MODIFY_TAG_ADD)){
            			data.setStartDate(stadate);
            			data.setEndDate(endDate);
            		}
        			
            		if(modifyTag.equals(BofConst.MODIFY_TAG_DEL) && stadateNew.compareTo(SysDateMgr.getSysTime()) > 0){//如果是副卡删除，并且副卡是预约生效的，截止时间为立即截止。
            			data.setEndDate(deldate);
            		}
        		}
        		
			}
        	//优惠表的处理
        	List<ShareTradeData> shareList = btd.get("TF_B_TRADE_SHARE");
        	for (int i = 0; i < shareList.size(); i++) {
        		ShareTradeData data = shareList.get(i);
        		String modifyTag = data.getModifyTag();
        		String stadateShare = data.getStartDate();
        		if(modifyTag.equals(BofConst.MODIFY_TAG_ADD)){
        			data.setStartDate(stadate);
        			data.setEndDate(endDate);
        		}
        		if(modifyTag.equals(BofConst.MODIFY_TAG_DEL) && stadateShare.compareTo(SysDateMgr.getSysTime()) > 0){
        			data.setEndDate(deldate);
        		}else if(modifyTag.equals(BofConst.MODIFY_TAG_DEL) && stadateShare.compareTo(SysDateMgr.getSysTime()) <= 0){
        			data.setEndDate(SysDateMgr.getLastDateThisMonth());
        		}
			}
        	//定义表的处理
        	List<ShareInfoTradeData> shareInfoList = btd.get("TF_B_TRADE_SHARE_INFO");
        	for (int i = 0; i < shareInfoList.size(); i++) {
        		ShareInfoTradeData data = shareInfoList.get(i);
        		String modifyTag = data.getModifyTag();
        		String stadateShareInfo = data.getStartDate();
        		if(modifyTag.equals(BofConst.MODIFY_TAG_ADD)){
        			data.setStartDate(stadate);
        			data.setEndDate(endDate);
        		}
        		if(modifyTag.equals(BofConst.MODIFY_TAG_DEL) && stadateShareInfo.compareTo(SysDateMgr.getSysTime()) > 0){
        			data.setEndDate(deldate);
        		}else if(modifyTag.equals(BofConst.MODIFY_TAG_DEL) && stadateShareInfo.compareTo(SysDateMgr.getSysTime()) <= 0){
        			data.setEndDate(SysDateMgr.getLastDateThisMonth());
        		}
			}
        }
    }
}
