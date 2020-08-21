
package com.asiainfo.veris.crm.order.soa.person.busi.usergrpdiscntspecdeal.order.trade;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.pub.util.ArrayUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AttrTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OfferRelTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOfferRelInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.usergrpdiscntspecdeal.order.requestdata.UserGrpDiscntSpecDealReqData;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @ClassName: UserDiscntSpecDealTrade.java
 * @Description: 用户优惠特殊处理台账处理
 * @version: v1.0.0
 * @author: maoke
 * @date: May 28, 2014 9:30:00 AM Modification History: Date Author Version Description
 *        -------------------------------------------------------* May 28, 2014 maoke v1.0.0 修改原因
 */
public class UserGrpDiscntSpecDealTrade extends BaseTrade implements ITrade
{
    @Override
    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        UserGrpDiscntSpecDealReqData request = (UserGrpDiscntSpecDealReqData) btd.getRD();

        UcaData uca = btd.getRD().getUca();

        String userId = uca.getUserId();
        String instId = request.getInstId();
        String startDate = request.getStartDate();
        String oldStartDate = request.getOldStartDate();
        String endDate = request.getEndDate();
        String oldEndDate = request.getOldEndDate();
        String discntRemark = request.getDiscntRemark();
        String tradeTypeCode = request.getTradeType().getTradeTypeCode();
        
        DiscntTradeData userDiscntTrade = uca.getUserDiscntByInstId(instId);

        if (userDiscntTrade != null)// 当前有效的
        {
            DiscntTradeData discntTd = userDiscntTrade.clone();

            if (!startDate.equals(oldStartDate))
            {
                discntTd.setStartDate(startDate);
            }
            if (!endDate.equals(oldEndDate))
            {
                discntTd.setEndDate(endDate);
            }

            discntTd.setModifyTag(BofConst.MODIFY_TAG_UPD);
            discntTd.setRemark(discntRemark);

            btd.add(uca.getSerialNumber(), discntTd);
            this.processOfferRel(uca, discntTd, btd);
        }
        else
        {// 已失效的
        	        	
            IDataset discntDatas = UserDiscntInfoQry.getUserDiscntByStartEndDate(userId, SysDateMgr.getLastMonthLastDate(), startDate);

            if (IDataUtil.isNotEmpty(discntDatas))
            {
                for (int i = 0, size = discntDatas.size(); i < size; i++)
                {
                    IData discnt = discntDatas.getData(i);

                    String tempInstId = discnt.getString("INST_ID");

                    if (instId.equals(tempInstId))
                    {
                        DiscntTradeData discntTd = new DiscntTradeData(discnt);

                        if (!startDate.equals(oldStartDate))
                        {
                            discntTd.setStartDate(startDate);
                        }
                        if (!endDate.equals(oldEndDate))
                        {
                            discntTd.setEndDate(endDate);
                        }

                        discntTd.setModifyTag(BofConst.MODIFY_TAG_UPD);
                        discntTd.setRemark(discntRemark);

                        btd.add(uca.getSerialNumber(), discntTd);
                        this.processOverOfferRel(uca, discntTd, btd);
                        break;
                    }
                }
            }
        }
        
        List<AttrTradeData> attrTDs = uca.getUserAttrsByRelaInstId(instId);
		  if (attrTDs != null && !attrTDs.isEmpty())
	        {
	            for (int i = 0; i < attrTDs.size(); i++)
	            {	            	
	               AttrTradeData attrTd = new AttrTradeData();
	               attrTd = attrTDs.get(i).clone();
	               attrTd.setEndDate(endDate);
	               attrTd.setRemark(discntRemark);
	               attrTd.setModifyTag(BofConst.MODIFY_TAG_UPD);
	               btd.add(uca.getSerialNumber(), attrTd);
	            }
        }

        btd.getMainTradeData().setRemark(request.getRemark());
    }
    
    private void processOfferRel(UcaData uca, DiscntTradeData dtd, BusiTradeData btd) throws Exception{
    	String instId = dtd.getInstId();
    	List<OfferRelTradeData> offerRels = uca.getOfferRelByRelUserIdAndRelOfferInsId(instId);
    	if(ArrayUtil.isEmpty(offerRels)){
    		return;
    	}
    	
    	for(OfferRelTradeData offerRel : offerRels){
    		String startDate = offerRel.getStartDate();
    		String endDate = offerRel.getEndDate();
    		boolean isUpdate = false;
    		if(SysDateMgr.compareTo(startDate, dtd.getStartDate()) != 0){
    			offerRel.setStartDate(dtd.getStartDate());
    			isUpdate = true;
    		}
    		if(SysDateMgr.compareTo(endDate, dtd.getEndDate()) != 0){
    			offerRel.setEndDate(dtd.getEndDate());
    			isUpdate = true;
    		}
    		if(isUpdate){
    			offerRel.setModifyTag(BofConst.MODIFY_TAG_UPD);
    			btd.add(uca.getSerialNumber(), offerRel);
    		}
    	}
    }
    
    private void processOverOfferRel(UcaData uca,DiscntTradeData dtd, BusiTradeData btd) throws Exception{
    	String instId = dtd.getInstId();
    	IDataset offerRels = UserOfferRelInfoQry.qryUserAllOfferRelByRelOfferInsId(instId);
    	if(IDataUtil.isEmpty(offerRels)){
    		return;
    	}
    	
    	for(Object obj : offerRels){
    		IData offerRel = (IData)obj;
    		String startDate = offerRel.getString("START_DATE");
    		String endDate = offerRel.getString("END_DATE");
    		boolean isUpdate = false;
    		if(SysDateMgr.compareTo(startDate, dtd.getStartDate()) != 0){
    			offerRel.put("START_DATE", dtd.getStartDate());
    			isUpdate = true;
    		}
    		if(SysDateMgr.compareTo(endDate, dtd.getEndDate()) != 0){
    			offerRel.put("END_DATE", dtd.getEndDate());
    			isUpdate = true;
    		}
    		
    		if(isUpdate){
    			OfferRelTradeData tmp = new OfferRelTradeData(offerRel);
    			tmp.setModifyTag(BofConst.MODIFY_TAG_UPD);
    			btd.add(uca.getSerialNumber(), tmp);
    		}
    	}
    }
}
