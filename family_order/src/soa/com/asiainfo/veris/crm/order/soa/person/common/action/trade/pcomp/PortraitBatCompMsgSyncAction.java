package com.asiainfo.veris.crm.order.soa.person.common.action.trade.pcomp;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.ailk.bizcommon.util.SysDateMgr;
import com.ailk.bizservice.base.CSBizBean;
import com.ailk.bizservice.seq.SeqMgr;
import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;

/**
 * 批量物联网开户,图片保存other表
 * @author zhuoyingzhi
 * @date 20170908
 *
 */
public class PortraitBatCompMsgSyncAction implements ITradeAction {

	private static Logger logger = Logger.getLogger(PortraitBatCompMsgSyncAction.class);
	/**
     * 照片ID信息获取保存
     * @param btd
     * @throws Exception
     */
	public void executeAction(BusiTradeData btd) throws Exception
	{
		String tradeTypeCode = btd.getTradeTypeCode();
		UcaData uca = new UcaData();
		IData picInfo = new DataMap();
		String picId = ""; 
		String angentpicId = "";
		String str4picId="";//责任人
		
		IData  pageData=btd.getRD().getPageRequestData();
		String batchOperType = pageData.getString("BATCH_OPER_TYPE", "");
		printLog("tradeTypeCode = ",tradeTypeCode);
		logger.debug("--TradeBatPicIdSynRegAction---getPageRequestData:"+btd.getRD().getPageRequestData());

		//品牌
		String brandCode=btd.getMainTradeData().getBrandCode();
        /**
         * REQ201707170020_新增物联卡开户人像采集功能
         * @author zhuoyingzhi
         * @date  20170907
         */	
		if("10".equals(tradeTypeCode)&&"PWLW".equals(brandCode)&&"CREATEPREUSER_PWLW".equals(batchOperType)){
			
            //客户图片
        	picId=pageData.getString("custInfo_PIC_ID", "");
            //经办人图片
        	angentpicId=pageData.getString("custInfo_AGENT_PIC_ID", "");
			
            logger.debug("--PortraitCompMsgSyncAction10-picId--"+picId+",angentpicId:"+angentpicId);
			if(StringUtils.isBlank(picId)&& StringUtils.isBlank(angentpicId)){ //不存在照片ID信息则直接返回，不进行保存
				return;
			}	                    
			uca=btd.getRD().getUca();
			picInfo = getPicInfo(picId,angentpicId,"",uca);
			addOtherTrade(picInfo,btd,batchOperType);
		}
		/**
		 * REQ201806190020_新增行业应用卡批量开户人像比对功能
		 * @author zhuoyingzhi
		 * @date 20180725
		 */
		else if("10".equals(tradeTypeCode)&&"CREATEPREUSER_M2M".equals(batchOperType)){
			//行业应用卡
			
            //责任人图片
			str4picId=pageData.getString("custInfo_RSRV_STR4_PIC_ID", "");
        	
            //经办人图片
        	angentpicId=pageData.getString("custInfo_AGENT_PIC_ID", "");
			
            logger.debug("--PortraitCompMsgSyncAction10-str4picId--"+str4picId+",angentpicId:"+angentpicId);
			if(StringUtils.isBlank(str4picId)&& StringUtils.isBlank(angentpicId)){ //不存在照片ID信息则直接返回，不进行保存
				return;
			}	                    
			uca=btd.getRD().getUca();
			picInfo = getPicInfo("",angentpicId,str4picId,uca);
			addOtherTrade(picInfo,btd,batchOperType);
		}		
	}
	
	/**
     * 照片ID信息获取处理
     * @param param,btd
     * @throws Exception
     */
	public IData getPicInfo(String picId,String angentpicId,String str4picId,UcaData uca) throws Exception
	{
		IData param = new DataMap();
		param.put("PIC_ID", picId);
		param.put("AGENT_PIC_ID", angentpicId);
		param.put("STR4_PIC_ID", str4picId);//责任人
		param.put("CUST_ID", uca.getCustId());
		param.put("USER_ID", uca.getUserId());
		param.put("PSPT_TYPE_CODE", uca.getCustomer().getPsptTypeCode());
		param.put("PSPT_ID", uca.getCustomer().getPsptId());
		param.put("SERIAL_NUMBER", uca.getSerialNumber());
		return param;
	}
	
	/**
     * 照片ID信息保存至其他台账表
     * @param param,btd
     * @throws Exception
     */
	public void addOtherTrade(IData param,BusiTradeData btd,String batchOperType) throws Exception 
	{
		OtherTradeData otherTradeData = new OtherTradeData();
		//新客户照片信息存储
		otherTradeData.setInstId(SeqMgr.getInstId());
		otherTradeData.setUserId(param.getString("USER_ID"));
		otherTradeData.setRsrvValueCode("BAT_PIC_ID");//照片ID
		otherTradeData.setRsrvValue("客户人像采集照片ID存储");
		otherTradeData.setRsrvStr1(param.getString("PIC_ID"));//照片ID值
		otherTradeData.setRsrvStr2(param.getString("FORM_PIC_ID",""));
		otherTradeData.setRsrvStr3(param.getString("AGENT_PIC_ID"));
		otherTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
		otherTradeData.setStartDate(SysDateMgr.getSysTime());
		otherTradeData.setEndDate(SysDateMgr.getEndCycle20501231());
		otherTradeData.setDepartId(CSBizBean.getVisit().getDepartId());
        otherTradeData.setStaffId(CSBizBean.getVisit().getStaffId());
        otherTradeData.setRsrvStr4(batchOperType);//批量操作类型
        otherTradeData.setRsrvStr5(param.getString("STR4_PIC_ID"));//责任人
		btd.add(param.getString("SERIAL_NUMBER"), otherTradeData);
	}
	
	/**
     * 日志打印
     * @param name,value
     * @throws Exception
     */
	public void printLog(String name ,String value)
	{
		if(logger.isDebugEnabled()){
			logger.debug("<<<<<<<<<<<<<<<<<<<<<<<<PortraitCompMsgSyncAction "+name+value+"<<<<<<<<<<<<<<<<<<<<<<<<");
		}
		
	}
}
