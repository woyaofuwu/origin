
package com.asiainfo.veris.crm.order.soa.person.common.action.trade.plat;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.ailk.biz.exception.BizException;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.org.apache.commons.lang3.time.DateFormatUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.configdata.plat.PlatOfficeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AttrTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PlatSvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.plat.PlatInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQry;


/**
 * 平台服务订购关系记录营销活动ID
 * @author huping
 *
 */
public class PlatSvcAddCampaignIDAction implements ITradeAction
{
	private static transient Logger log = Logger.getLogger(PlatSvcAddCampaignIDAction.class);

    @Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
    	UcaData uca = btd.getRD().getUca();  
    	
        List<PlatSvcTradeData> pstds = btd.getTradeDatas(TradeTableEnum.TRADE_PLATSVC);
        
        log.debug("PlatSvcAddCampaignIDAction--pstds="+pstds);
        
        if(pstds.size() == 0 || pstds == null)
        {
        	return ;
        }
        List<DiscntTradeData> userDiscnts = uca.getUserDiscnts();
        
        for (int i = 0; i < pstds.size(); i++)
        {
        	PlatSvcTradeData psd = pstds.get(i);
        	if(!BofConst.MODIFY_TAG_ADD.equals(psd.getModifyTag())){
        		continue ; 
        	}
			String serviceId = psd.getElementId();
			PlatOfficeData officeData = PlatOfficeData.getInstance(serviceId);
			
			//获取用户会员级别
			String memberType = null;
			if(officeData.getBizTypeCode().equals("81")){
		        if(userDiscnts != null && userDiscnts.size() > 0){
		        	 memberType = getUserMemberType(userDiscnts);
		        }
			}
			log.debug("PlatSvcAddCampaignIDAction--memberType="+memberType);
//			IDataset platInfoByServiceId = BofQuery.getPlatInfoByServiceId(serviceId);
//			if(IDataUtil.isEmpty(platInfoByServiceId)){
//				continue;
//			}
//			IData data= platInfoByServiceId.getData(0);
//			String offerId= data.getString("OFFER_ID","");
			
	      //判断是否从接口接入
			String campaignId = "";
			String startDate = psd.getStartDate();
			List<AttrTradeData> attrTradeDatas = psd.getAttrTradeDatas();
			
			//平台发起带有营销活动ID的订购
			if(attrTradeDatas != null && attrTradeDatas.size() > 0){
				log.debug("PlatSvcAddCampaignIDAction--attrTradeDatas="+attrTradeDatas);
				for (int j = 0; j < attrTradeDatas.size(); j++){
					AttrTradeData attrTradeData = attrTradeDatas.get(j);
					if(attrTradeData.getAttrCode().equals("CAMPAIGN_ID")){
						campaignId = attrTradeData.getAttrValue();
						log.debug("PlatSvcAddCampaignIDAction--campaignId1="+campaignId);
						IDataset campaignInfo = PlatInfoQry.queryCampaignInfoByCampaignId(campaignId,null); 
						log.debug("PlatSvcAddCampaignIDAction--campaignInfo="+campaignInfo);
						if(IDataUtil.isEmpty(campaignInfo)){
							BizException.bizerror("4046", "营销案在省侧不存在");//4046营销案在省侧不存在  3026用户已超出可办理次数
						}else{
							IDataset campaignDateInfo = PlatInfoQry.queryCampaignInfoByCampaignId(campaignId,SysDateMgr.getSysDateYYYYMMDD());
							log.debug("PlatSvcAddCampaignIDAction--campaignDateInfo="+campaignDateInfo);
							if(IDataUtil.isEmpty(campaignDateInfo)){
								BizException.bizerror("4047", "营销活动已过期");//4047营销活动已过期
							}else{
								boolean flag = true;
								for(Object campaignObj : campaignDateInfo){
									IData campaignData = (IData)campaignObj;
									String spCodeInfo = campaignData.getString("SP_CODE");
									String bizCodeInfo = campaignData.getString("OPERATOR_CODE");
									if(officeData.getSpCode().equals(spCodeInfo) && officeData.getBizCode().equals(bizCodeInfo)){
										flag = false;
										break;
									}
								}
								if(flag){
									BizException.bizerror("4046", "该平台服务在省侧无此营销案");
								}
								
							}
						}
						break;
					}
				}
			}
			
			if(StringUtils.isEmpty(campaignId) && !"6".equals(CSBizBean.getVisit().getInModeCode())){
		        //到局数据表中查询出营销活动ID
				campaignId = getCampaignId(memberType, serviceId, SysDateMgr.getSysDateYYYYMMDD());//营销活动ID
				
				log.debug("PlatSvcAddCampaignIDAction--campaignId2="+campaignId);
				
				//将营销活动ID存入属性表中
				if(StringUtils.isNotEmpty(campaignId)){ 
					//本月存在退订，退订当月不可参加营销活动，平台服务和属性都要下月1号生效
					IDataset platsvcs = PlatInfoQry.queryPlatSvcEstateThisMonth(uca.getUserId(),serviceId,SysDateMgr.getFirstDayOfThisMonth(),SysDateMgr.getLastDateThisMonth(),uca.getUserEparchyCode());
					if(IDataUtil.isNotEmpty(platsvcs)){         //本月存在退订
						Date date = SysDateMgr.string2Date(SysDateMgr.getFirstDayOfNextMonth(), "yyyy-MM-dd");
						String firstDayOfNextMonth = DateFormatUtils.format(date, "yyyyMMdd");
						
						campaignId = getCampaignId(memberType, serviceId, firstDayOfNextMonth);
						
						log.debug("PlatSvcAddCampaignIDAction--campaignId3="+campaignId);
						
						if(StringUtils.isNotEmpty(campaignId)){ //存在有效营销活动
							psd.setStartDate(SysDateMgr.getFirstDayOfNextMonth());        //平台服务和属性都要下月1号生效
						}else{
							continue;
						}
					}
					
					AttrTradeData attrTD = new AttrTradeData();
			        attrTD.setUserId(uca.getUserId());
			        attrTD.setInstType("Z");
			        attrTD.setInstId(SeqMgr.getInstId());
			        attrTD.setElementId(serviceId);
			        attrTD.setAttrCode("CAMPAIGN_ID");
			        attrTD.setAttrValue(campaignId);
			        attrTD.setStartDate(psd.getStartDate());
			        attrTD.setEndDate(psd.getEndDate());
			        attrTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
			        attrTD.setRelaInstId(psd.getInstId());
			        btd.add(btd.getMainTradeData().getSerialNumber(), attrTD);
				} 
			}
			
			if(StringUtils.isNotEmpty(campaignId)){ 
				//判断用户以前是否参加过此营销活动,包括失效的数据
				IDataset userattr = UserAttrInfoQry.getUserAllAttrByUserIdAndAttrCode(uca.getUserId(),"CAMPAIGN_ID",campaignId);
				
				log.debug("PlatSvcAddCampaignIDAction--userattr="+userattr);
				
				if(IDataUtil.isNotEmpty(userattr)){//以前参加过
					if("6".equals(CSBizBean.getVisit().getInModeCode())){   //如果是平台过来非首次参加营销活动的，业务处理失败
						BizException.bizerror("3026", "用户已超出可办理次数");
					}
					
					IDataset firstDateinfo = UserAttrInfoQry.getUserAllAttrByUserIdAndAttrCode(uca.getUserId(),"CAMPAIGN_FIRSTDATE",null);
					if(IDataUtil.isNotEmpty(firstDateinfo)){
						startDate = firstDateinfo.getData(0).getString("ATTR_VALUE");
					}
				}
				
		        AttrTradeData attrTD1 = new AttrTradeData();
		        attrTD1.setUserId(uca.getUserId());
		        attrTD1.setInstType("Z");
		        attrTD1.setInstId(SeqMgr.getInstId());
		        attrTD1.setElementId(serviceId);
		        attrTD1.setAttrCode("CAMPAIGN_FIRSTDATE");
		        attrTD1.setAttrValue(startDate);
		        attrTD1.setStartDate(psd.getStartDate());
		        attrTD1.setEndDate(psd.getEndDate());
		        attrTD1.setModifyTag(BofConst.MODIFY_TAG_ADD);
		        attrTD1.setRelaInstId(psd.getInstId());
		        btd.add(btd.getMainTradeData().getSerialNumber(), attrTD1);
			}
        }
    }
    
    private String getUserMemberType(List<DiscntTradeData> userDiscnts)throws Exception {
    	String memberType = "0";
        if(userDiscnts != null && userDiscnts.size() > 0){
        	for(DiscntTradeData discntTrade: userDiscnts){
        		// 查询咪咕文化会员级别优惠的配置
        		IDataset miguMemberTypeinfo = CommparaInfoQry.getCommparaByCodeCode1("CSM","2018","MIGU_MEMBER_TYPE",discntTrade.getDiscntCode());
                if (IDataUtil.isNotEmpty(miguMemberTypeinfo))
                {
                	memberType = miguMemberTypeinfo.getData(0).getString("PARA_CODE2");
                	break;
                }
        	}
        }
        return memberType;
    } 
    
    private String getCampaignId(String memberType, String offerCode, String startDate)throws Exception {
    	String campaignId = null;
    	//到局数据表中查询出营销活动ID
		IDataset campaignInfo = PlatInfoQry.queryMiguCampInfoByServiceId(memberType, offerCode, startDate);  
		
		log.debug("PlatSvcAddCampaignIDAction--campaignInfo="+campaignInfo);
		
		if(IDataUtil.isNotEmpty(campaignInfo)){
			campaignId = campaignInfo.getData(0).getString("CAMPAIGN_ID");
		}
		return campaignId;
    }
}
