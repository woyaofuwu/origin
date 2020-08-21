
package com.asiainfo.veris.crm.order.soa.person.common.action.trade.plat;

import java.util.List;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.template.TemplateBean;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AttrTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PlatSvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SmsTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.SmsSend;

/**
 * 关于WLAN能力化提升支撑系统改造方案
 * 1,如果用户已开通WLAN功能,开通188元及以上主套餐,则连带开通0元任我用权益。
 * 2,在开通WLAN功能时,如果用户为188元及以上主套餐,则连带开通0元任我用权益。
 * @author lihb3 2018.7.19
 *
 */

public class WlanRelaDiscntAction implements ITradeAction
{
    private static Logger log = Logger.getLogger(WlanRelaDiscntAction.class);
	@Override
	public void executeAction(BusiTradeData btd) throws Exception
	{
	    log.info("WlanRelaDiscntActionxxxxxxxxxxxxxxxxxxx40 "+btd); 
		String platServiceId = "98002401";
		UcaData uca = btd.getRD().getUca();
		String tradeTypeCode = btd.getTradeTypeCode();
		
		//获取0元任我用权益的资费编码
		IDataset discntConfigList = CommparaInfoQry.getCommparaByCode1("CSM", "3700", platServiceId, "40001", null);
		if(IDataUtil.isEmpty(discntConfigList)){
			return;
		}
		
		if("110".equals(tradeTypeCode)){
			orderWlanLimitedDiscntByProduct(uca,platServiceId,discntConfigList,btd);
		}

		if("3700".equals(tradeTypeCode)){
			dealCancelWlanDiscnt(uca,platServiceId,btd);
			orderWlanLimitedDiscntByPlatsvc(uca,platServiceId,discntConfigList,btd);
		}
		
	}

	private void orderWlanLimitedDiscntByProduct(UcaData uca,String platServiceId, IDataset discntConfigList, BusiTradeData btd) throws Exception {
		List<PlatSvcTradeData> userPlatSvcs = uca.getUserPlatSvcByServiceId(platServiceId);
	    log.info("WlanRelaDiscntActionxxxxxxxxxxxxxxxxxxx64 "+platServiceId);
		if (userPlatSvcs.isEmpty()){
			return;
		}
		
		String zeroDiscntCode = discntConfigList.first().getString("PARA_CODE2");
		List<DiscntTradeData> dicntDatas = uca.getUserDiscntByDiscntId(zeroDiscntCode);
	    log.info("WlanRelaDiscntActionxxxxxxxxxxxxxxxxxxx72 "+dicntDatas);
		if(!dicntDatas.isEmpty()){//已订购的不处理
			return;
		}
		
		//判断用户主套餐是否符合订购规则
		List<DiscntTradeData> tradeDiscnts = btd.getTradeDatas(TradeTableEnum.TRADE_DISCNT);
	    log.info("WlanRelaDiscntActionxxxxxxxxxxxxxxxxxxx80 "+tradeDiscnts);
		if(tradeDiscnts.isEmpty()){
			return;
		}
		
		boolean checked = false;
		DiscntTradeData mainDiscntTradeData = null;
		for(int i = 0; i < tradeDiscnts.size(); i++){
			DiscntTradeData tradeDiscnt = tradeDiscnts.get(i);
		    log.info("WlanRelaDiscntActionxxxxxxxxxxxxxxxxxxx80 "+tradeDiscnt);
            String modifyTag = tradeDiscnt.getModifyTag();
            String openDiscntCode = tradeDiscnt.getDiscntCode();
            if(BofConst.MODIFY_TAG_ADD.equals(modifyTag)){
        		IDataset attrConfigs = CommparaInfoQry.getCommparaByCode1("CSM", "2780", openDiscntCode,"0", null);
        		if(IDataUtil.isNotEmpty(attrConfigs)){
        		    log.info("WlanRelaDiscntActionxxxxxxxxxxxxxxxxxxx97 "+tradeDiscnt);
        			mainDiscntTradeData = tradeDiscnt;
        			checked = true;
        			break;
        		}
            }
		}
		
		if(!checked || (mainDiscntTradeData == null)){
			return;
		}
        log.info("WlanRelaDiscntActionxxxxxxxxxxxxxxxxxxx110 "+checked);
		
        //添加WLAN平台服务、套餐变更属性台账
		List<AttrTradeData> attrTradeDatas = uca.getUserAttrs();
		boolean existsOldAttrs = false;
		for(AttrTradeData attrTradeData : attrTradeDatas){
			if("401_2".equals(attrTradeData.getAttrCode())){
				attrTradeData.setEndDate(SysDateMgr.getLastDateThisMonth());
				attrTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL);
		        btd.add(uca.getSerialNumber(), attrTradeData); //终止原流量套餐
		        existsOldAttrs = true;
			}
		}
		
        PlatSvcTradeData platSvcTradeData = userPlatSvcs.get(0);
        platSvcTradeData.setOperCode(existsOldAttrs? PlatConstants.OPER_CHANGE_TC:PlatConstants.OPER_ORDER_TC);
        platSvcTradeData.setOprSource("08");
        platSvcTradeData.setIsNeedPf("1");
        platSvcTradeData.setModifyTag("2");
        platSvcTradeData.setOperTime(SysDateMgr.getSysDateYYYYMMDDHHMMSS());
        btd.add(uca.getSerialNumber(), platSvcTradeData);
        
        //添加属性台账
		AttrTradeData attrTradeData = new AttrTradeData();
		attrTradeData.setAttrCode(discntConfigList.first().getString("PARA_CODE3"));
		attrTradeData.setAttrValue(discntConfigList.first().getString("PARA_CODE1"));
		attrTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
		attrTradeData.setInstId(SeqMgr.getInstId());
		attrTradeData.setRelaInstId(platSvcTradeData.getInstId());
		attrTradeData.setStartDate(mainDiscntTradeData.getStartDate());
		attrTradeData.setEndDate(mainDiscntTradeData.getEndDate());
		attrTradeData.setElementId(platSvcTradeData.getElementId());
		attrTradeData.setUserId(uca.getUserId());
		attrTradeData.setInstType("Z");
        btd.add(uca.getSerialNumber(), attrTradeData); 
				
        DiscntTradeData discntTradeData = new DiscntTradeData();
        discntTradeData.setUserId(uca.getUserId());
        discntTradeData.setElementId(zeroDiscntCode);
        discntTradeData.setUserIdA("-1");
        discntTradeData.setProductId(platSvcTradeData.getProductId());
        discntTradeData.setPackageId(platSvcTradeData.getPackageId());
        discntTradeData.setSpecTag("0");
        discntTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
        discntTradeData.setInstId(SeqMgr.getInstId());
        discntTradeData.setStartDate(mainDiscntTradeData.getStartDate());
        discntTradeData.setEndDate(mainDiscntTradeData.getEndDate());
        discntTradeData.setRemark("188以上主套餐动绑定0元权益");
        btd.add(uca.getSerialNumber(), discntTradeData);; //添加0元权益资费台账		
	}
	
	@SuppressWarnings("unchecked")
	private void orderWlanLimitedDiscntByPlatsvc(UcaData uca,String platServiceId, IDataset discntConfigList, BusiTradeData btd) throws Exception {
		List<PlatSvcTradeData> platSvcTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_PLATSVC);
	      log.info("WlanRelaDiscntActionxxxxxxxxxxxxxxxxxxx139 "+platSvcTradeDatas);

		List<MainTradeData> mainTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_MAIN);
	      log.info("WlanRelaDiscntActionxxxxxxxxxxxxxxxxxxx168 "+mainTradeDatas.get(0));

		String inModeCode = mainTradeDatas.get(0).getInModeCode();
	      log.info("WlanRelaDiscntActionxxxxxxxxxxxxxxxxxxx172 "+inModeCode);

		if(platSvcTradeDatas.isEmpty()){
			return;
		}
		
		//判断是否是订购WLAN功能
		boolean checked = false;
		PlatSvcTradeData wlanPlatSvcTradeData = null;
		for(int i = 0; i < platSvcTradeDatas.size(); i++){
			PlatSvcTradeData platSvcTradeData = platSvcTradeDatas.get(i);
            String modifyTag = platSvcTradeData.getModifyTag();
            String openServiceId = platSvcTradeData.getElementId();
            if((BofConst.MODIFY_TAG_ADD.equals(modifyTag) || BofConst.MODIFY_TAG_UPD.equals(modifyTag))&& platServiceId.equals(openServiceId)){
            	wlanPlatSvcTradeData = platSvcTradeData;
      	        log.info("WlanRelaDiscntActionxxxxxxxxxxxxxxxxxxx154 "+wlanPlatSvcTradeData);

    			checked = true;
    			break;
            }
		}
		
		if(!checked || (wlanPlatSvcTradeData == null)){
			return;
		}		
	    log.info("WlanRelaDiscntActionxxxxxxxxxxxxxxxxxxx192 ");

		String zeroDiscntCode = discntConfigList.first().getString("PARA_CODE2");

		//如果用户本次已订购了0元WLAN权益
		boolean exists = false;
		List<DiscntTradeData> tradeDiscnts = btd.getTradeDatas(TradeTableEnum.TRADE_DISCNT);
        for(DiscntTradeData tradeDiscnt : tradeDiscnts){
        	if(zeroDiscntCode.equals(tradeDiscnt.getDiscntCode()) && "0".equals(tradeDiscnt.getModifyTag())){
        		exists = true;
        		
        		/*//--------------------------------------------------------------------------
            	if(inModeCode.equals("5")){        		
            		List<DiscntTradeData> zeroDicntDatas = uca.getUserDiscntByDiscntId(zeroDiscntCode);
            		if(!zeroDicntDatas.isEmpty()){//已订购的不处理
            			tradeDiscnt.setModifyTag(BofConst.MODIFY_TAG_UPD);
            			tradeDiscnt.setEndDate(SysDateMgr.END_DATE_FOREVER);
            			
            			List<AttrTradeData> baseAttrTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_ATTR);
            			for(int i = 0; i < baseAttrTradeDatas.size(); i++){
            				String attrCode = baseAttrTradeDatas.get(i).getAttrCode();
            				String attrValue = baseAttrTradeDatas.get(i).getAttrValue();
							if ("401_2".equals(attrCode) && "40001".equals(attrValue)){				
            					 baseAttrTradeDatas.get(i).setModifyTag(BofConst.MODIFY_TAG_UPD);
            					 baseAttrTradeDatas.get(i).setEndDate(SysDateMgr.END_DATE_FOREVER);
            				}
            			}
            			
            		}
            	}*/
        		
        		//--------------------------------------------------------------------------            	
        		break;
        	}
        }
		String notieContet = "尊敬的客户您好，为感谢您的长期支持，我公司针对主套餐188元以上或流量模组150元以上客户免费赠送WLAN任我用权益，每月可在CMCC及CMCC-WEB热点下免费使用100GBWLAN流量，100GB后将暂时关闭WLAN功能，下月自动恢复。根据您的消费情况已为您免费赠送WLAN任我用权益，如您订购了其他WLAN收费套餐，可自行退订。您可在手机系统中配置CMCC热点的自动认证功能，首次配置成功后，无需输入账号和密码，即可完成自动认证登录，享受更加便捷的互联网服务。配置方法请见链接：http://dx.10086.cn/Z3A7NFb 【中国移动】";
	    log.info("WlanRelaDiscntActionxxxxxxxxxxxxxxxxxxx208 "+notieContet);

	    log.info("WlanRelaDiscntActionxxxxxxxxxxxxxxxxxxx221 "+exists);

        if(exists){
        	//如果是短厅过来的订购，需发短信给用户
        	if(inModeCode.equals("5")){        		
        		
                IData smsData = getCommonSmsInfo(btd, new DataMap());
                smsData.put("NOTICE_CONTENT", notieContet);
                insTradeSMS(btd, smsData);  
        	}
        	return;
        }
        
		List<DiscntTradeData> zeroDicntDatas = uca.getUserDiscntByDiscntId(zeroDiscntCode);
		if(!zeroDicntDatas.isEmpty()){//已订购的不处理
			return;
		}
        
		//判断主套餐是否符合规则
		checked = false;
		List<DiscntTradeData> userDicntDatas = uca.getUserDiscnts();
		for(int i = 0; i < userDicntDatas.size(); i++){
			DiscntTradeData tradeDiscnt = userDicntDatas.get(i);
            String openDiscntCode = tradeDiscnt.getDiscntCode();
        	if(SysDateMgr.compareTo(tradeDiscnt.getStartDate(), SysDateMgr.getSysTime())>0){
        		continue;//还未生效的资费即开始时间大于当前时间的不判断.
        	}
    		IDataset attrConfigs = CommparaInfoQry.getCommparaByCode1("CSM", "2780", openDiscntCode,"0", null);
    	    log.info("WlanRelaDiscntActionxxxxxxxxxxxxxxxxxxx178 "+ attrConfigs);
    		if(IDataUtil.isNotEmpty(attrConfigs)){
    			checked = true;
    			break;
    		}				
		}		
	    log.info("WlanRelaDiscntActionxxxxxxxxxxxxxxxxxxx250 "+checked);
		if(!checked){
			return;
		}

		//如果用户同时开通了基础资费,去掉基础资费，只保留0元权益
		List<AttrTradeData> baseAttrTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_ATTR);
	    log.info("WlanRelaDiscntActionxxxxxxxxxxxxxxxxxxx257 "+ baseAttrTradeDatas);
		for(int i = 0; i < baseAttrTradeDatas.size(); i++){
			String attrCode = baseAttrTradeDatas.get(i).getAttrCode();
			String attrValue = baseAttrTradeDatas.get(i).getAttrValue();
			String modifyTag = baseAttrTradeDatas.get(i).getModifyTag();
			if( "0".equals(modifyTag) &&   (("401".equals(attrCode))||("401_2".equals(attrCode))) && "00000".equals(attrValue)){				
				baseAttrTradeDatas.remove(i);
			}
			if( ("SEL_TYPE".equals(attrCode))){			
				baseAttrTradeDatas.get(i).setAttrValue("2");//流量套餐
			}
		}
		
		AttrTradeData attrTradeData = new AttrTradeData();
		attrTradeData.setAttrCode(discntConfigList.first().getString("PARA_CODE3"));
		attrTradeData.setAttrValue(discntConfigList.first().getString("PARA_CODE1"));
		attrTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
		attrTradeData.setInstId(SeqMgr.getInstId());
		attrTradeData.setRelaInstId(wlanPlatSvcTradeData.getInstId());
		attrTradeData.setStartDate(wlanPlatSvcTradeData.getStartDate());
		attrTradeData.setEndDate(wlanPlatSvcTradeData.getEndDate());
		attrTradeData.setElementId(wlanPlatSvcTradeData.getElementId());
		attrTradeData.setUserId(uca.getUserId());
		attrTradeData.setInstType("Z");
        btd.add(uca.getSerialNumber(), attrTradeData); //添加套餐变更属性台账
	    log.info("WlanRelaDiscntActionxxxxxxxxxxxxxxxxxxx214 "+ attrTradeData);
        
        DiscntTradeData discntTradeData = new DiscntTradeData();
        discntTradeData.setUserId(uca.getUserId());
        discntTradeData.setElementId(zeroDiscntCode);
        discntTradeData.setUserIdA("-1");
        discntTradeData.setProductId(wlanPlatSvcTradeData.getProductId());
        discntTradeData.setPackageId(wlanPlatSvcTradeData.getPackageId());
        discntTradeData.setSpecTag("0");
        discntTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
        discntTradeData.setInstId(SeqMgr.getInstId());
        discntTradeData.setStartDate(wlanPlatSvcTradeData.getStartDate());
        discntTradeData.setEndDate(wlanPlatSvcTradeData.getEndDate());
        discntTradeData.setRemark("188以上主套餐动绑定0元权益");
        btd.add(uca.getSerialNumber(), discntTradeData);; //添加0元权益资费台账	
	    log.info("WlanRelaDiscntActionxxxxxxxxxxxxxxxxxxx229 "+ discntTradeData);

        
    	//如果是短厅过来的订购，需发短信给用户
    	if(inModeCode.equals("5")){
            IData smsData = getCommonSmsInfo(btd, new DataMap());
            smsData.put("NOTICE_CONTENT", notieContet);
            insTradeSMS(btd, smsData);  
    	}
	}
	
	//处理退订任我用套餐时，将截止时间由立即改为月末
	private void dealCancelWlanDiscnt(UcaData uca, String platServiceId, BusiTradeData btd) throws Exception {
		List<PlatSvcTradeData> platSvcTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_PLATSVC);
		if(platSvcTradeDatas.isEmpty()){
			return;
		}
		
		//判断是否是套餐退订
		boolean checked = false;
		PlatSvcTradeData wlanPlatSvcTradeData = null;
		for(int i = 0; i < platSvcTradeDatas.size(); i++){
			PlatSvcTradeData platSvcTradeData = platSvcTradeDatas.get(i);
            String openServiceId = platSvcTradeData.getElementId();
            String operCode = platSvcTradeData.getOperCode();
            if(platServiceId.equals(openServiceId) && PlatConstants.OPER_CANCEL_TC.equals(operCode)){
            	wlanPlatSvcTradeData = platSvcTradeData;
    			checked = true;
    			break;
            }
		}
		
		if(!checked || (wlanPlatSvcTradeData == null)){
			return;
		}
		
		IDataset attrConfigs1 = CommparaInfoQry.getCommparaByCodeCode1("CSM", "3700", platServiceId, "40001");
		IDataset attrConfigs2 = CommparaInfoQry.getCommparaByCodeCode1("CSM", "3700", platServiceId, "40002");
		IDataset attrConfigs3 = CommparaInfoQry.getCommparaByCodeCode1("CSM", "3700", platServiceId, "40003");
		IDataset attrConfigs4 = CommparaInfoQry.getCommparaByCodeCode1("CSM", "3700", platServiceId, "40004");
		StringBuffer newWlanDiscntCodes = new StringBuffer();
		if(IDataUtil.isNotEmpty(attrConfigs1)){
			newWlanDiscntCodes.append(attrConfigs1.first().getString("PARA_CODE2")).append(",");
		}
		if(IDataUtil.isNotEmpty(attrConfigs2)){
			newWlanDiscntCodes.append(attrConfigs2.first().getString("PARA_CODE2")).append(",");
		}
		if(IDataUtil.isNotEmpty(attrConfigs3)){
			newWlanDiscntCodes.append(attrConfigs3.first().getString("PARA_CODE2")).append(",");
		}
		if(IDataUtil.isNotEmpty(attrConfigs4)){
			newWlanDiscntCodes.append(attrConfigs4.first().getString("PARA_CODE2")).append(",");
		}
		
		if(newWlanDiscntCodes.toString().trim().length()==0){
			return;
		}
		
		String[] wlanDiscntCodes =newWlanDiscntCodes.toString().split(",");
		
		List<DiscntTradeData> tradeDiscnts = btd.getTradeDatas(TradeTableEnum.TRADE_DISCNT);
		for(DiscntTradeData tradeDiscnt : tradeDiscnts){
			String discntCode = tradeDiscnt.getDiscntCode();
			String modifyTag = tradeDiscnt.getModifyTag();
			if(BofConst.MODIFY_TAG_DEL.equals(modifyTag)){
				for(String wlanDiscntCode : wlanDiscntCodes){
					if(discntCode.equals(wlanDiscntCode)){
						tradeDiscnt.setEndDate(SysDateMgr.getLastDateThisMonth());
					}
				}
			}			
		}	
		
		String[] newWlanattrs = {"40001","40002","40003","40004"};
		List<AttrTradeData> atrrDiscnts = btd.getTradeDatas(TradeTableEnum.TRADE_ATTR);
		for(AttrTradeData attrTradeData : atrrDiscnts){
			String attrValue = attrTradeData.getAttrValue();
			String modifyTag = attrTradeData.getModifyTag();
			if(BofConst.MODIFY_TAG_DEL.equals(modifyTag)){
				for(String newWlanattr : newWlanattrs ){
					if(newWlanattr.equals(attrValue)){
						attrTradeData.setEndDate(SysDateMgr.getLastDateThisMonth());
					}
				}	
			}
		}
	
	}
	public static void insTradeSMS(BusiTradeData btd, IData smsData) throws Exception
    {                System.out.println("WlanRelaDiscntActionxxxxxxxxxxxxxxxxxxx376 "+ btd);
    				 log.info("WlanRelaDiscntActionxxxxxxxxxxxxxxxxxxx376 "+ btd);

        String sysdate = btd.getRD().getAcceptTime();

        SmsTradeData std = new SmsTradeData();
        std.setSmsNoticeId(smsData.getString("SMS_NOTICE_ID", SeqMgr.getSmsSendId()));
        std.setEparchyCode(smsData.getString("EPARCHY_CODE", CSBizBean.getUserEparchyCode()));
        std.setBrandCode(smsData.getString("BRAND_CODE"));
        std.setInModeCode(CSBizBean.getVisit().getInModeCode());
        std.setSmsNetTag(smsData.getString("SMS_NET_TAG", "0"));
        std.setChanId(smsData.getString("CHAN_ID", "11"));
        std.setSendObjectCode(smsData.getString("SEND_OBJECT_CODE", "6"));
        std.setSendTimeCode(smsData.getString("SEND_TIME_CODE", "1"));
        std.setSendCountCode(smsData.getString("SEND_COUNT_CODE", "1"));
        std.setRecvObjectType(smsData.getString("RECV_OBJECT_TYPE", "00"));
        std.setRecvObject(smsData.getString("RECV_OBJECT"));
        std.setRecvId(smsData.getString("RECV_ID", "-1"));
        // std.setSmsTypeCode(smsData.getString("SMS_TYPE_CODE", "20"));
        std.setSmsTypeCode(smsData.getString("SMS_TYPE_CODE", "I0"));
        std.setSmsKindCode(smsData.getString("SMS_KIND_CODE", "02"));
        std.setNoticeContentType(smsData.getString("NOTICE_CONTENT_TYPE", "0"));
        std.setReferedCount(smsData.getString("REFERED_COUNT", "0"));
        std.setForceReferCount(smsData.getString("FORCE_REFER_COUNT", "1"));
        std.setForceObject(smsData.getString("FORCE_OBJECT"));
        std.setForceStartTime(smsData.getString("FORCE_START_TIME", ""));
        std.setForceEndTime(smsData.getString("FORCE_END_TIME", ""));
        std.setSmsPriority(smsData.getString("SMS_PRIORITY", "50"));
        std.setReferTime(smsData.getString("REFER_TIME", sysdate));
        std.setReferDepartId(smsData.getString("REFER_DEPART_ID", CSBizBean.getVisit().getDepartId()));
        std.setReferStaffId(smsData.getString("REFER_STAFF_ID", CSBizBean.getVisit().getStaffId()));
        std.setDealTime(smsData.getString("DEAL_TIME", sysdate));
        std.setDealStaffid(smsData.getString("DEAL_STAFFID", CSBizBean.getVisit().getStaffId()));
        std.setDealDepartid(smsData.getString("DEAL_DEPARTID", CSBizBean.getVisit().getDepartId()));
        std.setDealState("0");// 处理状态，0：未处理
        std.setRemark(smsData.getString("REMARK"));
        std.setRevc1(smsData.getString("REVC1"));
        std.setRevc2(smsData.getString("REVC2"));
        std.setRevc3(smsData.getString("REVC3"));
        std.setRevc4(smsData.getString("REVC4"));
        std.setMonth(sysdate.substring(5, 7));
        std.setDay(sysdate.substring(8, 10));
        std.setCancelTag(smsData.getString("CANCEL_TAG"));

        // 短信截取
        String content = smsData.getString("NOTICE_CONTENT", "");
        int charLength = SmsSend.getCharLength(content, 4000);
        content = content.substring(0, charLength);
        std.setNoticeContent(content);

        btd.add(btd.getRD().getUca().getUser().getSerialNumber(), std);
        System.out.println("WlanRelaDiscntActionxxxxxxxxxxxxxxxxxxx426 "+ std);
        log.info("WlanRelaDiscntActionxxxxxxxxxxxxxxxxxxx426 "+ std);
        
    }
	
	@SuppressWarnings("rawtypes")
    protected IData getCommonSmsInfo(BusiTradeData btd, IData smsInfo) throws Exception
    {
        IData smsData = new DataMap();
        
        smsData.put("TRADE_ID", btd.getTradeId());
        String specSn = smsInfo.getString("SPEC_SERIAL_NUMBER", "");
        smsData.put("RECV_OBJECT", specSn == "" ? btd.getRD().getUca().getSerialNumber() : specSn);
        String specUserId = smsInfo.getString("SPEC_USER_ID", "");
        smsData.put("RECV_ID", specUserId == "" ? btd.getRD().getUca().getUserId() : specUserId);
        smsData.put("EPARCHY_CODE", CSBizBean.getUserEparchyCode());
        smsData.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());
        smsData.put("SMS_PRIORITY", "50");
        smsData.put("CANCEL_TAG", btd.getMainTradeData().getCancelTag());
        smsData.put("REMARK", "业务短信通知");
        smsData.put("NOTICE_CONTENT_TYPE", smsInfo.getString("SMS_TYPE"));
        smsData.put("REVC4", smsInfo.getString("_TEMPLATE_ID", "").equals("") ? smsInfo.getString("TEMPLATE_ID") : smsInfo.getString("_TEMPLATE_ID", ""));// 对应模板ID
        
        String tempLateId = smsInfo.getString("TEMPLATE_ID");
        if (StringUtils.isNotEmpty(tempLateId))
        {
            IData templateIds = TemplateBean.getTemplateInfoByPk(tempLateId);
            if (IDataUtil.isNotEmpty(templateIds))
            {
                smsData.put("SMS_TYPE_CODE", templateIds.getString("TEMPLATE_TYPE"));
                smsData.put("SMS_KIND_CODE", templateIds.getString("TEMPLATE_KIND"));
                smsData.put("SMS_PRIORITY", templateIds.getString("SMS_PRIORITY"));
            }
        }
        return smsData;
    }
}
