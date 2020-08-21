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
import com.asiainfo.veris.crm.order.soa.person.busi.changecustinfo.order.requestdata.ModifyCustInfoReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.changecustowner.order.requestdata.ChangeCustOwnerReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.createusertrade.order.requestdata.CreateUserRequestData;
public class PortraitCompMsgSyncAction implements ITradeAction {

	private static Logger logger = Logger.getLogger(PortraitCompMsgSyncAction.class);
	/**
     * 照片ID信息获取保存
     * @param btd
     * @throws Exception
     */
	public void executeAction(BusiTradeData btd) throws Exception
	{
		String tradeTypeCode = btd.getTradeTypeCode();
		IData pageData = new DataMap();
		UcaData uca = new UcaData();
		IData picInfo = new DataMap();
		String picId = ""; 
		String angentpicId = ""; 
		printLog("tradeTypeCode = ",tradeTypeCode);
		if("10".equals(tradeTypeCode))
		{//开户类型
			CreateUserRequestData createUserRD = (CreateUserRequestData) btd.getRD();
			pageData = createUserRD.getPageRequestData();
			uca = createUserRD.getUca();
			picId = pageData.getString("PIC_ID","");
			angentpicId = pageData.getString("AGENT_PIC_ID","");
			printLog("开户picId = ",picId);
			printLog("开户angentpicId = ",angentpicId);
			printLog("--angentpicId = ",angentpicId);
			if(StringUtils.isBlank(picId) && StringUtils.isBlank(angentpicId)){ //不存在照片ID信息则直接返回，不进行保存
				return;
			}
			picInfo = getPicInfo(picId,angentpicId,uca);
			printLog("开户picInfo = ",picInfo.toString());
			addOtherTrade(picInfo,btd);
		}
		else if("100".equals(tradeTypeCode))
		{//过户类型
			ChangeCustOwnerReqData changeCustownerRD = (ChangeCustOwnerReqData) btd.getRD();
			pageData = changeCustownerRD.getPageRequestData();
			uca = changeCustownerRD.getUca();
			picId = pageData.getString("PIC_ID","");
			angentpicId = pageData.getString("AGENT_PIC_ID","");
			String formpicId = pageData.getString("FORM_PIC_ID","");
			printLog("过户picId = ",picId);
			printLog("--angentpicId = ",angentpicId);
			printLog("--formpicId = ",formpicId);
			printLog("----过户类型picInfo---",picInfo.toString());
			if(StringUtils.isBlank(picId) && StringUtils.isBlank(angentpicId) && StringUtils.isBlank(formpicId)){ //不存在照片ID信息则直接返回，不进行保存
				return;
			}
			
			picInfo = getPicInfo(picId,angentpicId,uca);
			picInfo.put("FORM_PIC_ID", formpicId);
			printLog("过户picInfo = ",picInfo.toString());
			addOtherTrade(picInfo,btd);
		}
		else if("60".equals(tradeTypeCode))
		{//客户资料变更
			ModifyCustInfoReqData modifyCustInfoRD = (ModifyCustInfoReqData) btd.getRD();
			pageData = modifyCustInfoRD.getPageRequestData();
			uca = modifyCustInfoRD.getUca();
			picId = pageData.getString("PIC_ID","");
			angentpicId = pageData.getString("AGENT_PIC_ID","");
			printLog("客户资料变更picId = ",picId);
			if(StringUtils.isBlank(picId) && StringUtils.isBlank(angentpicId)){ //不存在照片ID信息则直接返回，不进行保存
				return;
			}
			picInfo = getPicInfo(picId,angentpicId,uca);
			printLog("客户资料变更picInfo = ",picInfo.toString());
			addOtherTrade(picInfo,btd);
			
		}
		else if("73".equals(tradeTypeCode)){
			/**
			 * REQ201705270006_关于人像比对业务优化需求
			 * @author zhuoyingzhi
			 * @date 20170701
			 */
			 //用户密码重置
			IData  pageIdata=btd.getRD().getPageRequestData();
			//从界面获取图片id
			picId =pageIdata.getString("custInfo_PIC_ID", "");
			angentpicId = pageIdata.getString("custInfo_AGENT_PIC_ID","");
			printLog("用户密码重置picInfo====:",picInfo.toString()+",==pageIdata=====:"+pageIdata+",angentpicId:"+angentpicId);
			if(StringUtils.isBlank(picId)&& StringUtils.isBlank(angentpicId)){ //不存在照片ID信息则直接返回，不进行保存
				return;
			}
			uca=btd.getRD().getUca();
			picInfo = getPicInfo(picId,angentpicId,uca);
			addOtherTrade(picInfo,btd);
		}else if("310".equals(tradeTypeCode)){
			/**
			 * REQ201705270006_关于人像比对业务优化需求
			 * @author zhuoyingzhi
			 * @date 20170701
			 */
			 //复机
			IData  pageIdata=btd.getRD().getPageRequestData();
			//从界面获取图片id
			picId =pageIdata.getString("custInfo_PIC_ID", "");
			angentpicId = pageIdata.getString("custInfo_AGENT_PIC_ID","");
			printLog("复机业务picInfo====:",picInfo.toString()+",angentpicId"+angentpicId+",pageIdata:"+pageIdata);
			if(StringUtils.isBlank(picId)&& StringUtils.isBlank(angentpicId)){ //不存在照片ID信息则直接返回，不进行保存
				return;
			}
			uca=btd.getRD().getUca();
			picInfo = getPicInfo(picId,angentpicId,uca);
			addOtherTrade(picInfo,btd);
		}else if("142".equals(tradeTypeCode)){
			/**
			 * REQ201705270006_关于人像比对业务优化需求
			 * @author zhuoyingzhi
			 * @date 20170701
			 */
			 //换卡(补卡)
			IData  pageIdata=btd.getRD().getPageRequestData();
			//从界面获取图片id
			picId =pageIdata.getString("custInfo_PIC_ID", "");
			
			
			//从界面获取   换卡补卡类型(0=补卡 1=换卡)
			String  remoteCardType=pageIdata.getString("REMOTECARD_TYPE", "");
			
			angentpicId = pageIdata.getString("custInfo_AGENT_PIC_ID","");
			
			printLog("换卡(补卡)picInfo==pageIdata==:",pageIdata+",remoteCardType:"+remoteCardType+",angentpicId"+angentpicId);
			
			if(StringUtils.isBlank(picId) && !"0".equals(remoteCardType)&& StringUtils.isBlank(angentpicId)){ //不存在照片ID信息则直接返回，不进行保存
				return;
			}
			uca=btd.getRD().getUca();
			picInfo = getPicInfo(picId,angentpicId,uca);
			addOtherTrade(picInfo,btd);
		}else if("416".equals(tradeTypeCode)){
            /**	 	 
             * REQ201704270011_关于购买大额有价卡的业务流程优化	 	 
             * <br/>	 	 
             * 有价卡销售	 	 
             * @author zhuoyingzhi	 	 
             * @date 20170717	 	 
             */	 	 
            IData  pageIdata=btd.getRD().getPageRequestData();	 	 
            //从界面获取图片id	 	 
            picId =pageIdata.getString("custInfo_PIC_ID", "");	 	 
            printLog("有价卡销售业务picInfo====:",picInfo.toString());	 	 
            if(StringUtils.isBlank(picId)){ //不存在照片ID信息则直接返回，不进行保存	 	 
                return;	 	 
            }	 	 
            uca=btd.getRD().getUca();	 	 
            picInfo = getPicInfo(picId,angentpicId,uca);	 	 
            addOtherTrade(picInfo,btd);			
		}else if("290".equals(tradeTypeCode)){

			IData  pageIdata=btd.getRD().getPageRequestData();
			//从界面获取图片id
			picId =pageIdata.getString("custInfo_PIC_ID", "");
			printLog("押金业务picInfo====:",picInfo.toString());
			if(StringUtils.isBlank(picId)){ //不存在照片ID信息则直接返回，不进行保存
				return;
			}
			uca=btd.getRD().getUca();
			picInfo = getPicInfo(picId,angentpicId,uca);
			addOtherTrade(picInfo,btd);
		}
		else if("494".equals(tradeTypeCode)){

			IData  pageIdata=btd.getRD().getPageRequestData();
			//从界面获取图片id
			picId =pageIdata.getString("custInfo_USE_PIC_ID", "");
			printLog("单位证件激活picInfo====:",picInfo.toString());
			if(StringUtils.isBlank(picId)){ //不存在照片ID信息则直接返回，不进行保存
				return;
			}
			uca=btd.getRD().getUca();
			picInfo = getPicInfo(picId,angentpicId,uca);
			addOtherTrade(picInfo,btd);
		}
	}
	
	/**
     * 照片ID信息获取处理
     * @param param,btd
     * @throws Exception
     */
	public IData getPicInfo(String picId,String angentpicId,UcaData uca) throws Exception
	{
		IData param = new DataMap();
		param.put("PIC_ID", picId);
		param.put("AGENT_PIC_ID", angentpicId);
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
	public void addOtherTrade(IData param,BusiTradeData btd) throws Exception 
	{
		OtherTradeData otherTradeData = new OtherTradeData();
		//新客户照片信息存储
		otherTradeData.setInstId(SeqMgr.getInstId());
		otherTradeData.setUserId(param.getString("USER_ID"));
		otherTradeData.setRsrvValueCode("PIC_ID");//照片ID
		otherTradeData.setRsrvValue("客户人像采集照片ID存储");
		otherTradeData.setRsrvStr1(param.getString("PIC_ID"));//照片ID值
		otherTradeData.setRsrvStr2(param.getString("FORM_PIC_ID",""));
		otherTradeData.setRsrvStr3(param.getString("AGENT_PIC_ID"));
		otherTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
		otherTradeData.setStartDate(SysDateMgr.getSysTime());
		otherTradeData.setEndDate(SysDateMgr.getEndCycle20501231());
		otherTradeData.setDepartId(CSBizBean.getVisit().getDepartId());
        otherTradeData.setStaffId(CSBizBean.getVisit().getStaffId());
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
