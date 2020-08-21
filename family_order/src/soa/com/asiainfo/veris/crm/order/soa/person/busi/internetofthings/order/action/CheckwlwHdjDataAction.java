package com.asiainfo.veris.crm.order.soa.person.busi.internetofthings.order.action;

import java.util.List;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AttrTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.internetofthings.IotConstants;

/**
 * 
 * @author chenye6
 * 校验和对讲物联网新增资费
 *
 */
public class CheckwlwHdjDataAction implements ITradeAction {
	private static final Logger logger = Logger.getLogger(CheckwlwHdjDataAction.class);			
	@SuppressWarnings("rawtypes")
	public void executeAction(BusiTradeData btd) throws Exception {
		BaseReqData reqData = btd.getRD();
		UcaData uca = reqData.getUca();
		String tradeTypeCode = reqData.getTradeType().getTradeTypeCode();
		if (!"PWLW".equals(uca.getBrandCode())) {
			return;
		}
		if ("110".equals(tradeTypeCode)) {// 产品变更
        	//校验物联网和对讲
        	checkPBossHDJ(tradeTypeCode,uca,btd);
		} else if ("10".equals(tradeTypeCode)) {// 开户
        	//校验物联网和对讲
        	checkPBossHDJ(tradeTypeCode,uca,btd);
		} 
	}
	
	public void checkPBossHDJ(String tradeTypeCode,UcaData uca,BusiTradeData btd) throws Exception {
		List<DiscntTradeData> userDiscnts = uca.getUserDiscnts();
		logger.debug("checkPBossHDJ--userDiscnts="+userDiscnts);
		if(userDiscnts==null){
			return;
		}
		boolean dflag=false;
		boolean aflag=true;
		for (int i = 0; i < userDiscnts.size(); i++) {
			DiscntTradeData userDiscntItem = userDiscnts.get(i);
			IData configItem = IotConstants.IOT_DISCNT_CONFIG.getData(userDiscntItem.getElementId());
			logger.debug("checkPBossHDJ--configItem="+configItem);
			if(IDataUtil.isNotEmpty(configItem)&&"I00010900011".equals(configItem.getString("PARA_CODE1",""))){
				List<AttrTradeData> attrTradeList = btd.get("TF_B_TRADE_ATTR");
				logger.debug("checkPBossHDJ--attrTradeList="+attrTradeList);
				if(attrTradeList==null){
					return;
				}
				for(int j=0;j<attrTradeList.size();j++){
					AttrTradeData attrInfo=attrTradeList.get(j);
					logger.debug("checkPBossHDJ--AttrCode="+attrInfo.getAttrCode());
					logger.debug("checkPBossHDJ--AttrValue="+attrInfo.getAttrValue());
					if("110".equals(tradeTypeCode)&&BofConst.MODIFY_TAG_UPD.equals(userDiscntItem.getModifyTag())&&"Discount".equals(attrInfo.getAttrCode())){
						CSAppException.apperr(CrmCommException.CRM_COMM_103, "固费折扣率不可变更!");
					}
					if("Discount".equals(attrInfo.getAttrCode())&&(Integer.valueOf(attrInfo.getAttrValue())<70)){
						dflag=true;
					}
					if("ApprovalNum".equals(attrInfo.getAttrCode())&&StringUtils.isNotEmpty(attrInfo.getAttrValue())&&(attrInfo.getAttrValue()).length()<=32
							&&!"1".equals(attrInfo.getModifyTag())){
						aflag=false;
					}
				}
				IData userAttrInfo=UserAttrInfoQry.getUserAttrByRelaInstIdAndAttrCode(userDiscntItem.getUserId(), userDiscntItem.getInstId(),"Discount",uca.getUser().getEparchyCode());
				if(IDataUtil.isNotEmpty(userAttrInfo)&&StringUtils.isNotBlank(userAttrInfo.getString("ATTR_VALUE",""))&&Integer.valueOf(userAttrInfo.getString("ATTR_VALUE",""))<70){
					dflag=true;
				}
				if(dflag&&aflag){
					CSAppException.apperr(CrmCommException.CRM_COMM_103, "当固费折扣率低于底线(7折)时，总部市场部批复文号必须填写并且不能超过32位!");
				}
			}
		}
	}
}
