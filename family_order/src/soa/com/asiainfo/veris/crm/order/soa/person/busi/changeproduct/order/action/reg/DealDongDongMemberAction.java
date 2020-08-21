package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.reg;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PlatSvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

/**
 * 1，订购DongDong会员，判断用户话费余额；
 * 2，订购DongDong会员，自动绑定全国亲情网10元优惠减免；
 * 3，订购DongDong会员连续包月产品，除绑定全国亲情网10元优惠减免外再绑定对应sp服务，连带退订；
 * 4，支持DongDong会员不同档次产品变更
 * 5, 订购DongDong会员,通过受理营销活动发放和包电子券
 * @author lihb3
 *
 */
public class DealDongDongMemberAction implements ITradeAction{
	
	private static final Logger logger = Logger.getLogger(DealDongDongMemberAction.class);
	
	public Map<String, IData> DISCNT_CONFIG_MAP = new HashMap<String, IData>();
	/*
	static{
		try{
			DISCNT_CONFIG_MAP = loadConfig("DongDongMember");
			for (Map.Entry<String, IData> entry : DISCNT_CONFIG_MAP.entrySet()) {

				// logger.error("key= " + entry.getKey() + " and value= "
				// + entry.getValue());
				logger.error("DealDongDongMemberActionxxxxxxxxxxxxxxxxxxx50 " + entry.getKey()+"\t"+entry.getValue());

			}
			logger.debug(">>>>>>>>>>DongDongMember>>>>>>>>>>"+DISCNT_CONFIG_MAP.toString());
		} catch (Exception e){
			e.printStackTrace();
		}
	}*/
	
	@Override
	public void executeAction(BusiTradeData btd) throws Exception {
		
//		Map<String, IData> DISCNT_CONFIG_MAP = new HashMap<String, IData>();
		try{
			DISCNT_CONFIG_MAP = loadConfig("DongDongMember");
			for (Map.Entry<String, IData> entry : DISCNT_CONFIG_MAP.entrySet()) {

				// logger.error("key= " + entry.getKey() + " and value= "
				// + entry.getValue());
				logger.error("DealDongDongMemberActionxxxxxxxxxxxxxxxxxxx50 " + entry.getKey()+"\t"+entry.getValue());

			}
			logger.debug(">>>>>>>>>>DongDongMember>>>>>>>>>>"+DISCNT_CONFIG_MAP.toString());
		} catch (Exception e){
			e.printStackTrace();
		}
		
		
		UcaData uca = btd.getRD().getUca();
		logger.error("DealDongDongMemberActionxxxxxxxxxxxxxxxxxxx62 " + btd);

		List<DiscntTradeData> tradeDiscnts = btd.get("TF_B_TRADE_DISCNT");
		logger.error("DealDongDongMemberActionxxxxxxxxxxxxxxxxxxx65 " + tradeDiscnts);
		
		if(tradeDiscnts.isEmpty()){
			return;
		}
		logger.debug(">>>>>>>>>>>>>>>>>DealDongDongMemberAction is run>>>>>>>>>>>>>>>");
		boolean dealDongDongDiscnt = false;
		String fee = "";
		String familyDiscntCode = "";
		String newPlatSvcCode = "";
		String oldPlatSvcCode = "";
		String longTimeDsicntCode = "";//连续包月会员套餐编码
		String activeId = "";//发放电子券所需活动编码
		DiscntTradeData newDiscntTradeData = null;
		DiscntTradeData cancelDiscntTradeData = null;
		
		/*
		select t.* from  td_s_commpara     t  where 1=1  and t.param_attr='1114'   ; 
		PARAM_CODE为套餐编码
		PARA_CODE1为DongDongMember		
		PARA_CODE2为套餐价格单位分
		PARA_CODE3为亲情网10元减免资费
		PARA_CODE4为发放电子券所需的活动号
		PARA_CODE5为连续包月会员对应的sp服务编码
		PARA_CODE6为电子券营销活动PACKAGE_ID
		PARA_CODE7为电子券营销活动PRODUCT_ID 
		*/
		
		for(int i = 0; i < tradeDiscnts.size(); i++){			
			DiscntTradeData tradeDiscnt = tradeDiscnts.get(i);
			logger.error("DealDongDongMemberActionxxxxxxxxxxxxxxxxxxx95 " + tradeDiscnt);

			String discntCode = tradeDiscnt.getDiscntCode();
			String modifyTag = tradeDiscnt.getModifyTag();
			
			IData discntConfig = DISCNT_CONFIG_MAP.get(discntCode);
			if(discntConfig != null){
				logger.error("DealDongDongMemberActionxxxxxxxxxxxxxxxxxxx102 " + discntConfig);

				dealDongDongDiscnt = true;	
				familyDiscntCode = discntConfig.getString("PARA_CODE3");//亲情网10元减免资费
				logger.error("DealDongDongMemberActionxxxxxxxxxxxxxxxxxxx106 " + discntConfig);
				
				if(BofConst.MODIFY_TAG_ADD.equals(modifyTag)){
					
					newDiscntTradeData = tradeDiscnt;
					fee = discntConfig.getString("PARA_CODE2");//套餐价格单位分
					newPlatSvcCode = discntConfig.getString("PARA_CODE5");//连续包月会员对应的sp服务编码
					activeId = discntConfig.getString("PARA_CODE4");//发放电子券所需的活动号
					logger.error("DealDongDongMemberActionxxxxxxxxxxxxxxxxxxx114 " + fee+"\t"+newPlatSvcCode+"\t"+activeId);
					
				}
				if(BofConst.MODIFY_TAG_DEL.equals(modifyTag)){
					cancelDiscntTradeData = tradeDiscnt;
					oldPlatSvcCode = discntConfig.getString("PARA_CODE5");//连续包月会员对应的sp服务编码
					logger.error("DealDongDongMemberActionxxxxxxxxxxxxxxxxxxx120 " +oldPlatSvcCode);

				}
				if(StringUtils.isNotBlank(newPlatSvcCode) || StringUtils.isNotBlank(oldPlatSvcCode)){
					longTimeDsicntCode = discntCode;
					logger.error("DealDongDongMemberActionxxxxxxxxxxxxxxxxxxx125 " +longTimeDsicntCode);
					
				}
			}
		}
		logger.error("DealDongDongMemberActionxxxxxxxxxxxxxxxxxxx130 " +dealDongDongDiscnt);
		
		if(!dealDongDongDiscnt){
			return;
		}
		
		boolean addDiscnt = (newDiscntTradeData !=null);
		boolean cancelDiscnt = (cancelDiscntTradeData !=null);
		boolean changeDiscnt = (addDiscnt && cancelDiscnt);
		logger.error("DealDongDongMemberActionxxxxxxxxxxxxxxxxxxx139 " + addDiscnt+"\t"+cancelDiscnt+"\t"+changeDiscnt);
		
		if(changeDiscnt){
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "不允许在同一笔订购中同时订购、退订dongdong会员产品");
		}
		
		DiscntTradeData oldDiscntTradeData = null;
		if(addDiscnt && !changeDiscnt){//判断是否存在已生效的dongdong会员
			List<DiscntTradeData> userDicntDatas = uca.getUserDiscnts();
			logger.error("DealDongDongMemberActionxxxxxxxxxxxxxxxxxxx148 " + userDicntDatas);

			for(int i = 0; i < userDicntDatas.size(); i++){
				DiscntTradeData userDiscnt = userDicntDatas.get(i);
				logger.error("DealDongDongMemberActionxxxxxxxxxxxxxxxxxxx152 " + userDiscnt);
				
	            String openDiscntCode = userDiscnt.getDiscntCode();
				logger.error("DealDongDongMemberActionxxxxxxxxxxxxxxxxxxx155 " + openDiscntCode+"\t"+userDiscnt.getModifyTag());
				
	            if(BofConst.MODIFY_TAG_ADD.equals(userDiscnt.getModifyTag())){ 
	            	continue;
	            }
	            IData discntConfig = DISCNT_CONFIG_MAP.get(openDiscntCode);
	            if(discntConfig != null){
	            	changeDiscnt = true;
	            	oldDiscntTradeData = userDiscnt;
	            	String platSvcId = discntConfig.getString("PARA_CODE5");//连续包月会员对应的sp服务编码
					logger.error("DealDongDongMemberActionxxxxxxxxxxxxxxxxxxx165 " + platSvcId);
					
	            	if(StringUtils.isNotBlank(platSvcId)){
	            		oldPlatSvcCode = platSvcId;
	            		longTimeDsicntCode = openDiscntCode;
						logger.error("DealDongDongMemberActionxxxxxxxxxxxxxxxxxxx170 " + oldPlatSvcCode+"\t"+longTimeDsicntCode);

	            	}
	            	break;
	            }
			}
		}
		
		//1,判断余额
		logger.error("DealDongDongMemberActionxxxxxxxxxxxxxxxxxxx179 " + addDiscnt+"\t"+fee);

		if(addDiscnt && StringUtils.isNotBlank(fee)){
			int balance = Integer.valueOf(uca.getAcctBlance());//实时余额(分)
			logger.error("DealDongDongMemberActionxxxxxxxxxxxxxxxxxxx183 " + balance+"\t"+fee);

			if(balance < Integer.valueOf(fee)){
				//CSAppException.apperr(CrmCommException.CRM_COMM_103, "用户余额不足,不能订购此套餐");
			}			
		}
		
		//2,订购DongDong会员，自动绑定全国亲情网10元优惠减免
		if(addDiscnt && !changeDiscnt && StringUtils.isNotBlank(familyDiscntCode)){
			logger.error("DealDongDongMemberActionxxxxxxxxxxxxxxxxxxx192 " + addDiscnt+"\t"+changeDiscnt+"\t"+familyDiscntCode);

			geneFamilyDiscntTrade(newDiscntTradeData,uca,btd,familyDiscntCode);
		}
		
		//3.订购DongDong会员连续包月产品，除绑定全国亲情网10元优惠减免外再绑定对应sp服务，连带退订
		if(StringUtils.isNotBlank(newPlatSvcCode) && StringUtils.isNotBlank(oldPlatSvcCode)){
			logger.error("DealDongDongMemberActionxxxxxxxxxxxxxxxxxxx199 " + newPlatSvcCode+"\t"+oldPlatSvcCode);
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "用户已订购DongDong会员连续包月产品");
		}
		if(cancelDiscnt && StringUtils.isBlank(oldPlatSvcCode)){
			logger.error("DealDongDongMemberActionxxxxxxxxxxxxxxxxxxx203 " + cancelDiscnt+"\t"+oldPlatSvcCode);

			CSAppException.apperr(CrmCommException.CRM_COMM_103, "一次性付费产品不允许退订");
		}		
		if(addDiscnt && !changeDiscnt && StringUtils.isNotBlank(newPlatSvcCode)){//订购DongDong会员连续包月产品
			logger.error("DealDongDongMemberActionxxxxxxxxxxxxxxxxxxx208 " + addDiscnt+"\t"+changeDiscnt+"\t"+newPlatSvcCode);

			genePlatSvcTrade(newDiscntTradeData,uca,btd,newPlatSvcCode);
		}
		if(cancelDiscnt && !changeDiscnt && StringUtils.isNotBlank(oldPlatSvcCode)){//退订DongDong会员连续包月产品
			logger.error("DealDongDongMemberActionxxxxxxxxxxxxxxxxxxx213 " + addDiscnt+"\t"+changeDiscnt+"\t"+oldPlatSvcCode);

			cancelPlatSvcTrade(uca,btd,oldPlatSvcCode);
			cancelfamilyDiscntTrade(uca,btd,familyDiscntCode);
		}
		
		//4，支持DongDong会员不同档次产品变更
		logger.error("DealDongDongMemberActionxxxxxxxxxxxxxxxxxxx220 " +changeDiscnt);
		
		if(changeDiscnt){
			
			if(oldDiscntTradeData.getDiscntCode().equals(longTimeDsicntCode)){//原套餐为连续包月产品,新套餐是一次性付费产品
				logger.error("DealDongDongMemberActionxxxxxxxxxxxxxxxxxxx226 " +longTimeDsicntCode);
				
				cancellongTimeDsicnt(uca,btd,oldDiscntTradeData);//截止原连续包月产品
				cancelPlatSvcTrade(uca,btd,oldPlatSvcCode);//截止原连续包月产品绑定的sp
				String startDate = SysDateMgr.getFirstDayOfNextMonth();
				newDiscntTradeData.setStartDate(startDate);//新套餐开始时间设为下月初
				newDiscntTradeData.setRsrvStr1("DONGDONG"+String.valueOf(Integer.parseInt(startDate.substring(5,7))));
				
				newDiscntTradeData.setEndDate(SysDateMgr.getAddMonthsLastDay(2,newDiscntTradeData.getEndDate()));//新套餐截止时间后移一月
				logger.error("DealDongDongMemberActionxxxxxxxxxxxxxxxxxxx235 " +newDiscntTradeData);

			}else{//原套餐为一次性付费产品,新套餐开始时间设为老产品失效之后下月初
				logger.error("DealDongDongMemberActionxxxxxxxxxxxxxxxxxxx232 " +longTimeDsicntCode);
				
				String startDate = newDiscntTradeData.getStartDate();
				String endDate = newDiscntTradeData.getEndDate();
				String newStartDate = SysDateMgr.getNextSecond(oldDiscntTradeData.getEndDate());
				newDiscntTradeData.setStartDate(newStartDate);
				newDiscntTradeData.setRsrvStr1("DONGDONG"+String.valueOf(Integer.parseInt(startDate.substring(5,7))));

				if(!newDiscntTradeData.getDiscntCode().equals(longTimeDsicntCode)){//新套餐为一次性付费产品,截止时间后移
					String newEnddate = SysDateMgr.getAddMonthsLastDay(SysDateMgr.compareTo(newStartDate, startDate)+1,endDate);
					newDiscntTradeData.setEndDate(newEnddate);
					logger.error("DealDongDongMemberActionxxxxxxxxxxxxxxxxxxx241 " +newDiscntTradeData);

				}
			}
		}
		
		//设置dongdong会员订购标志，用于完工发放电子券,变更档次的时候不用，因为新产品的生效时间起码为下月1号，由AEE发电子券
		logger.error("DealDongDongMemberActionxxxxxxxxxxxxxxxxxxx246 " +addDiscnt+"\t"+changeDiscnt);
		
		if(addDiscnt && !changeDiscnt){
			MainTradeData mainTradeData = btd.getMainTradeData();
			mainTradeData.setRsrvStr6("DONGDONG");
			mainTradeData.setRsrvStr10(activeId);
			logger.error("DealDongDongMemberActionxxxxxxxxxxxxxxxxxxx252 " +mainTradeData);

			geneSaleActiveTrade(newDiscntTradeData,uca,btd);
			newDiscntTradeData.setRsrvStr1("DONGDONG"+SysDateMgr.getCurMonth());
			logger.error("DealDongDongMemberActionxxxxxxxxxxxxxxxxxxx256 " +newDiscntTradeData);			
		}
		logger.error("DealDongDongMemberActionxxxxxxxxxxxxxxxxxxx261 " +newDiscntTradeData);
		
	}
	
    private void cancelfamilyDiscntTrade(UcaData uca, BusiTradeData btd, String familyDiscntCode) throws Exception {
		logger.error("DealDongDongMemberActionxxxxxxxxxxxxxxxxxxx262 " +familyDiscntCode);
    	List<DiscntTradeData> familyDiscntTrade = uca.getUserDiscntByDiscntId(familyDiscntCode);
		logger.error("DealDongDongMemberActionxxxxxxxxxxxxxxxxxxx264 " +familyDiscntTrade);
		
    	if(familyDiscntTrade.isEmpty()){
    		return;
    	}
		
    	DiscntTradeData discntTradeData = familyDiscntTrade.get(0).clone();
    	discntTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL);
    	discntTradeData.setEndDate(SysDateMgr.getLastDateThisMonth());
		logger.error("DealDongDongMemberActionxxxxxxxxxxxxxxxxxxx273 " +discntTradeData);
		
    	btd.add(uca.getSerialNumber(), discntTradeData);
    	
	}

	private void cancellongTimeDsicnt(UcaData uca, BusiTradeData btd, DiscntTradeData oldDiscntTradeData) throws Exception {
		logger.error("DealDongDongMemberActionxxxxxxxxxxxxxxxxxxx279 " + oldDiscntTradeData);
		
    	DiscntTradeData discntTradeData = oldDiscntTradeData.clone();
    	discntTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL);
    	discntTradeData.setEndDate(SysDateMgr.getLastDateThisMonth());
		logger.error("DealDongDongMemberActionxxxxxxxxxxxxxxxxxxx284 " + discntTradeData);
		
    	btd.add(uca.getSerialNumber(), discntTradeData);
	}
	
	private void cancelPlatSvcTrade(UcaData uca, BusiTradeData btd, String oldPlatSvcCode) throws Exception {
		logger.error("DealDongDongMemberActionxxxxxxxxxxxxxxxxxxx290 " + oldPlatSvcCode);

    	List<PlatSvcTradeData> userPlatSvcs = btd.getRD().getUca().getUserPlatSvcByServiceId(oldPlatSvcCode);
		logger.error("DealDongDongMemberActionxxxxxxxxxxxxxxxxxxx293 " + userPlatSvcs);

		if (userPlatSvcs.isEmpty()){
			return;
		}
		
        PlatSvcTradeData platSvcTradeData = userPlatSvcs.get(0);
		platSvcTradeData.setBizStateCode(PlatConstants.STATE_CANCEL);
        platSvcTradeData.setOperCode(PlatConstants.OPER_CANCEL_ORDER);
        platSvcTradeData.setEndDate(SysDateMgr.getLastDateThisMonth());
        platSvcTradeData.setOprSource("08");
        platSvcTradeData.setIsNeedPf(BofConst.IS_NEED_PF_YES);
        platSvcTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL);
        platSvcTradeData.setOperTime(SysDateMgr.getSysTime());
		logger.error("DealDongDongMemberActionxxxxxxxxxxxxxxxxxxx306 " + platSvcTradeData);

        btd.add(uca.getSerialNumber(), platSvcTradeData);
	}
	
	private void genePlatSvcTrade(DiscntTradeData newDiscntTradeData,UcaData uca, BusiTradeData btd, String platSvcCode) throws Exception {
		logger.error("DealDongDongMemberActionxxxxxxxxxxxxxxxxxxx312 " + platSvcCode);
		if (platSvcCode != null && platSvcCode.trim().length()>0) {
			
		
    	String sysDate = SysDateMgr.getSysTime();
    	PlatSvcTradeData platSvcTradeData = new PlatSvcTradeData();
		platSvcTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
		platSvcTradeData.setBizStateCode(PlatConstants.STATE_OK); //A-正常，N-暂停，E-退订，L-挂失
		platSvcTradeData.setOperCode(PlatConstants.OPER_ORDER); //08-资料变更，07-退订，06-订购，04-暂停，05-恢复
		platSvcTradeData.setIsNeedPf(BofConst.IS_NEED_PF_YES);	
		platSvcTradeData.setOprSource("08");		
		platSvcTradeData.setStartDate(newDiscntTradeData.getStartDate());
		platSvcTradeData.setEndDate(newDiscntTradeData.getEndDate());
		platSvcTradeData.setOperTime(sysDate);
		platSvcTradeData.setFirstDate(sysDate);
		platSvcTradeData.setFirstDateMon(sysDate);		
		platSvcTradeData.setUserId(newDiscntTradeData.getUserId());
		platSvcTradeData.setInstId(SeqMgr.getInstId());
		platSvcTradeData.setPackageId("-1");
		platSvcTradeData.setProductId(newDiscntTradeData.getProductId());
		platSvcTradeData.setElementId(platSvcCode);
		platSvcTradeData.setElementType(BofConst.ELEMENT_TYPE_CODE_PLATSVC);	
		platSvcTradeData.setAllTag("02"); 
		platSvcTradeData.setActiveTag("1");
		platSvcTradeData.setRemark("订购DongDong会员连续包月产品，连带订购SP");
		logger.error("DealDongDongMemberActionxxxxxxxxxxxxxxxxxxx335连带订购SP " + platSvcTradeData);
		
		btd.add(uca.getSerialNumber(), platSvcTradeData);			
		}
	}
	
	private void geneFamilyDiscntTrade(DiscntTradeData newDiscntTradeData, UcaData uca, BusiTradeData btd, String familyDiscntCode) throws Exception {
		logger.error("DealDongDongMemberActionxxxxxxxxxxxxxxxxxxx341 " + familyDiscntCode);

        DiscntTradeData discntTradeData = new DiscntTradeData();
        discntTradeData.setUserId(newDiscntTradeData.getUserId());
        discntTradeData.setElementId(familyDiscntCode);
        discntTradeData.setUserIdA("-1");
        discntTradeData.setProductId(newDiscntTradeData.getProductId());
        discntTradeData.setPackageId("-1");
        discntTradeData.setSpecTag("0");
        discntTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
        discntTradeData.setInstId(SeqMgr.getInstId());
        discntTradeData.setStartDate(newDiscntTradeData.getStartDate());
        discntTradeData.setEndDate(newDiscntTradeData.getEndDate());
        discntTradeData.setRemark("订购DongDong会员，赠送全国亲情网10元优惠减免");
		logger.error("DealDongDongMemberActionxxxxxxxxxxxxxxxxxxx341赠送全国亲情网10元优惠减免 " + discntTradeData);

        btd.add(uca.getSerialNumber(), discntTradeData);	
	}
	
	private void geneSaleActiveTrade(DiscntTradeData newDiscntTradeData,UcaData uca, BusiTradeData btd) throws Exception {
		logger.error("DealDongDongMemberActionxxxxxxxxxxxxxxxxxxx361 " + newDiscntTradeData);

		IData discntConfig = DISCNT_CONFIG_MAP.get(newDiscntTradeData.getDiscntCode());
		if(IDataUtil.isNotEmpty(discntConfig)){
			logger.error("DealDongDongMemberActionxxxxxxxxxxxxxxxxxxx365 " + discntConfig);

			String packageId = discntConfig.getString("PARA_CODE6","").trim();//电子券营销活动PACKAGE_ID
			String productId = discntConfig.getString("PARA_CODE7","").trim();//电子券营销活动PRODUCT_ID	
			if (!"".equals(packageId) && !"".equals(productId)) {

				IData saleParam = new DataMap();
				saleParam.put("PACKAGE_ID", packageId);
				saleParam.put("SERIAL_NUMBER", uca.getSerialNumber());
				saleParam.put("PRODUCT_ID", productId);
				logger.error("DealDongDongMemberActionxxxxxxxxxxxxxxxxxxx373 " + saleParam);

				IDataset ds = CSAppCall.call("SS.SaleActiveRegSVC.tradeReg4Intf", saleParam);

				logger.error("DealDongDongMemberActionxxxxxxxxxxxxxxxxxxx376营销活动 " + ds);
			}	    	
		}
	}
	
	public Map<String, IData> loadConfig(String paraCode1) throws Exception
    {
		/*
		select t.* from  td_s_commpara     t  where 1=1  and t.param_attr='1114'   ; 
		PARAM_CODE为套餐编码
		PARA_CODE1为DongDongMember		
		PARA_CODE2为套餐价格单位分
		PARA_CODE3为亲情网10元减免资费
		PARA_CODE4为发放电子券所需的活动号
		PARA_CODE5为连续包月会员对应的sp服务编码
		PARA_CODE6为电子券营销活动PACKAGE_ID
		PARA_CODE7为电子券营销活动PRODUCT_ID 
		*/
        Map<String, IData> configMap = new HashMap<String, IData>();
        IDataset configList = CommparaInfoQry.getCommparaByCode1("CSM","1114",paraCode1,"ZZZZ");
		logger.error("DealDongDongMemberActionxxxxxxxxxxxxxxxxxxx386 " + configList);

        for (int i = 0; i < configList.size(); i++)
        {
            IData config = configList.getData(i);
            configMap.put(config.getString("PARAM_CODE"), config);
        }        
        return configMap;
    }
	
}
