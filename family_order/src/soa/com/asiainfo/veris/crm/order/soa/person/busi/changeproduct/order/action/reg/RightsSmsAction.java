
package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.reg;

import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.collections.CollectionUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ProductTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SmsTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import org.apache.log4j.Logger;

import java.util.Iterator;
import java.util.List;

/**
 *发送权益短信处理类
 */
public class RightsSmsAction implements ITradeAction
{
	private static Logger log = Logger.getLogger(RightsSmsAction.class);

	@SuppressWarnings("unchecked")
	@Override
    public void executeAction(BusiTradeData btd) throws Exception
	{
		List<ProductTradeData> tradeProduct = btd.getTradeDatas(TradeTableEnum.TRADE_PRODUCT);
		if(CollectionUtils.isEmpty(tradeProduct)){
			return;
		}

		String addProductId = "";
		String delProductId = "";
		String addProductName = "";
		String delProductName = "";
		String startDate="";
		for(ProductTradeData product : tradeProduct){
			if(CSBaseConst.TRADE_MODIFY_TAG.Add.getValue().equals(product.getModifyTag())){
				addProductId = product.getProductId();
				addProductName = UProductInfoQry.getProductNameByProductId(addProductId);  //获取产品名称
				startDate = product.getStartDate();
			}else{
				delProductId = product.getProductId();
				delProductName = UProductInfoQry.getProductNameByProductId(delProductId);  //获取产品名称
			}
		}

		UcaData uca = btd.getRD().getUca();
		List<DiscntTradeData> discntTradeDataList = btd.getTradeDatas(TradeTableEnum.TRADE_DISCNT);
		String addDiscntCode = UpcCall.getMustChoseElementByOfferCode(addProductId,uca.getUserId(),discntTradeDataList);
		String delDiscntCode = UpcCall.getMustChoseElementByOfferCode(delProductId,uca.getUserId(),discntTradeDataList);

		//调用账务查询权益
		IDataset addRights = new DatasetList();
		IDataset delRights = new DatasetList();
		try{

			addRights = AcctCall.gerRightByDiscnt(addDiscntCode);
			delRights = AcctCall.gerRightByDiscnt(delDiscntCode);

			log.debug(uca.getSerialNumber()+"addRights111111111111"+addRights.toString());
			log.debug(uca.getSerialNumber()+"delRights111111111111"+delRights.toString());

			dropNullRights(addRights);
			dropNullRights(delRights);


		}catch (Exception e){
			log.error(uca.getSerialNumber()+"主产品变更短信异常addDiscntCode"+addDiscntCode,e);
			log.error(uca.getSerialNumber()+"主产品变更短信异常delDiscntCode"+delDiscntCode,e);
		}

		log.debug(uca.getSerialNumber()+"addRights"+addRights.toString());
		log.debug(uca.getSerialNumber()+"delRights"+delRights.toString());


/**
 * [{"FEEPOLICY_ID":"84018424","IS_TIME":"0","IS_BROADBAND":"1",
 * "BROAD_MAXRATE":"500M","IS_MAGIC":"1","FEEPOLICY_NAME":"家庭158元套餐（达量不限速）","IS_RING":"0"}]
 */
		String addContain ="";
		String delContain ="";
		String delUserContain ="";
		if(IDataUtil.isNotEmpty(addRights)){
			IData temp = addRights.first();
			addContain = getRightByData(temp,uca.getUserId(),uca.getSerialNumber(), 0);
		}
		if(IDataUtil.isNotEmpty(delRights)){
			IData temp = delRights.first();
			delContain = getRightByData(temp,uca.getUserId(),uca.getSerialNumber(),0);
			delUserContain = getRightByData(temp,uca.getUserId(),uca.getSerialNumber(),1);
		}



		String smsContent = "您好！您办理了产品变更业务，新办理的产品为" + addProductName ;
		if(StringUtils.isNotBlank(addContain)){
			smsContent += "，该套餐包含" + addContain +"。";
		}else{
			smsContent +="，该套餐未包含功能型业务权益。";
		}
		smsContent += "取消的产品为"+delProductName;
		if(StringUtils.isNotBlank(delContain)){
			smsContent += "，该套餐包含" + delContain +"。";
		}else{
			smsContent +="，该套餐未包含功能型业务权益。";
		}

		startDate=startDate.substring(0,10);
		smsContent +=	"您办理的"+addProductName+"于"+startDate+"生效，变更生效后，" +
				"已开通的"+delUserContain+"业务将按新生效套餐的标准收取，" +
				"您可发短信0000到10086查询、取消已开通业务（宽带及魔百和电视业务等可通过营业厅变更、取消），更多详情可拨打10086热线咨询。" +
				"【中国移动】 ";




		SmsTradeData std = new SmsTradeData();
		//建立短信发送对象，并给各个字段赋值。可参考数据库sms表给字段赋默认值。
		std.setSmsNoticeId(SeqMgr.getSmsSendId());
		std.setEparchyCode(CSBizBean.getVisit().getStaffEparchyCode());
		std.setBrandCode("-1");
		std.setInModeCode(CSBizBean.getVisit().getInModeCode());
		std.setSmsNetTag("0");
		std.setChanId("11");
		std.setSendObjectCode("6");
		std.setSendTimeCode("1");
		std.setSendCountCode("1");
		std.setRecvObjectType("00");
		std.setRecvId("-1");
		std.setSmsTypeCode("20");
		std.setSmsKindCode("02");
		std.setNoticeContentType("0");
		std.setReferedCount("0");
		std.setForceReferCount("1");
		std.setForceObject("");
		std.setForceStartTime("");
		std.setForceEndTime("");
		std.setSmsPriority("50");
		std.setReferTime(SysDateMgr.getSysTime());
		std.setReferDepartId(CSBizBean.getVisit().getDepartId());
		std.setReferStaffId(CSBizBean.getVisit().getStaffId());
		std.setDealTime(SysDateMgr.getSysTime());
		std.setDealStaffid(CSBizBean.getVisit().getStaffId());
		std.setDealDepartid(CSBizBean.getVisit().getDepartId());
		std.setDealState("0");// 处理状态，0：未处理
		std.setRemark("主产品变更下发权益短信");
		std.setRevc1("");
		std.setRevc2("");
		std.setRevc3("");
		std.setRevc4("");
		std.setMonth(SysDateMgr.getSysTime().substring(5, 7));
		std.setDay(SysDateMgr.getSysTime().substring(8, 10));
		std.setCancelTag("0");
		//发送号码
		std.setRecvObject(uca.getSerialNumber());
		// 短信
		std.setNoticeContent(smsContent);
		// 发送号码
		std.setForceObject("10086");
		btd.add(uca.getSerialNumber(), std);

	}

	private void dropNullRights(IDataset addRights) {
		Iterator it = addRights.iterator();
		while(it.hasNext()){
			IData data = (IData)it.next();
			if("0".equals(data.getString("IS_TIME"))
					&&"0".equals(data.getString("IS_BROADBAND"))
					&&"0".equals(data.getString("IS_MAGIC"))
					&&"0".equals(data.getString("IS_RING"))){
				it.remove();
			}

		}







	}

	/**
	 *
	 * @param temp
	 * @param userId
	 * @param serialNumber
	 * @param flag
	 * @return
	 */
	private String getRightByData(IData temp, String userId, String serialNumber, int flag) throws Exception {
		String result = "";
		if("1".equals(temp.getString("IS_BROADBAND"))){
			if(1==flag){
				//查询是否办理宽带
				String wSerialNumber = serialNumber.indexOf("KD_")>-1 ? serialNumber:"KD_" + serialNumber;
				IData userInfo = UcaInfoQry.qryUserInfoBySn(wSerialNumber);
				if(IDataUtil.isNotEmpty(userInfo)){
					result += "宽带、";
				}
			}else{
				result += temp.getString("BROAD_MAXRATE")+"宽带、";
			}
		}
		if("1".equals(temp.getString("IS_MAGIC"))){
			if(1==flag){
				//查询是否开通魔百和
				IDataset boxInfos = UserResInfoQry.qrySetTopBoxByUserIdAndTag1(userId, "4", "J");
				if(IDataUtil.isNotEmpty(boxInfos)){
					result += "魔百和、";
				}
			}else{
				result += "魔百和、";
			}
		}
		if("1".equals(temp.getString("IS_TIME"))){
			if(flag==1){
				IDataset userSvcDataset = UserSvcInfoQry.getSvcUserId(userId, "230");
				if(IDataUtil.isEmpty(userSvcDataset)){
					result += "全时通、";
				}
			}else{
				result += "全时通、";

			}
		}
		if("1".equals(temp.getString("IS_RING"))){
			if(flag==1){
				IDataset userSvcDataset = UserSvcInfoQry.getSvcUserId(userId, "20");
				if(IDataUtil.isEmpty(userSvcDataset)){
					result += "彩铃、";
				}
			}else{
				result += "彩铃、";

			}
		}

		if(StringUtils.isNotBlank(result)){
			return result.substring(0,result.length()-1);
		}else{
			return "";
		}
	}
}
