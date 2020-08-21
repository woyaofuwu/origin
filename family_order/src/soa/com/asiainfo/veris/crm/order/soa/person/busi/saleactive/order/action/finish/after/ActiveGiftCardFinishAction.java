package com.asiainfo.veris.crm.order.soa.person.busi.saleactive.order.action.finish.after;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeSaleActive;
import com.asiainfo.veris.crm.order.soa.person.busi.saleactive.SaleActiveBean;
import com.google.zxing.common.detector.MathUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.log4j.Logger;

import java.util.Random;

/**
 * 关于开发“新人入网，见面有礼”活动的需求
 * @author wuhao5
 * 如果是配置的活动，调用卡券赠送接口赠送卡券
 */
public class ActiveGiftCardFinishAction implements ITradeFinishAction
{
	private static transient Logger logger = Logger.getLogger(ActiveGiftCardFinishAction.class);
    public void executeAction(IData mainTrade) throws Exception
    {
    	logger.debug("进入ActiveGiftCardFinishAction》》》》》》》》》》》》》》");
    	IDataset tradeSaleActives = TradeSaleActive.getTradeSaleActiveByTradeId(mainTrade.getString("TRADE_ID"));
    	if (IDataUtil.isNotEmpty(tradeSaleActives) && tradeSaleActives.size() > 0){
			for (int i = 0; i < tradeSaleActives.size(); i++) {
				IData tradeSaleActive = tradeSaleActives.getData(i);
				if (tradeSaleActive.getString("MODIFY_TAG").equals(BofConst.MODIFY_TAG_ADD)) {
					String productId=tradeSaleActive.getString("PRODUCT_ID");
					String packageId=tradeSaleActive.getString("PACKAGE_ID");
					IDataset commParaInfo1288 = CommparaInfoQry.getCommparaByCodeCode1("CSM", "1288", productId,packageId);
					logger.debug("进入commParaInfo1288》》》》》》》》》》》》》》"+commParaInfo1288.toString());
					if (IDataUtil.isNotEmpty(commParaInfo1288) && commParaInfo1288.size() > 0) {
						String loginType = "0";//登录方式 0--手机号登录
						String loginNo = mainTrade.getString("SERIAL_NUMBER");//登录账号 ：手机号
						String channelId = "22";//渠道编码 22 --省份调用
						String oprType = "2";//操作类型 2：领取（默认）
						String batchID = commParaInfo1288.first().getString("PARA_CODE3");//券码
						//每笔调用唯一标识 规则：渠道编码+省份编码+YYYYMMDDHHmmssSSS +6位流水 如：0010020170705184812000202038
						String serialNumber = "22" + "898" + SysDateMgr.getSysDateYYYYMMDDHHMMSS() + "000" + RandomStringUtils.random(6,"0123456789");
						String obtainDate = SysDateMgr.getSysDateYYYYMMDD();//领取日期：对账的时候使用,例如：20171129
						String regionCode = "898";//省份编码 898--海南
						String taskId = "";//任务id 后期扩展使用
						String obtainBusi = "";//领取业务编码 后期扩展使用
						String orderId = "";//订单号 订单领取的时候，传入订单号
						String extendPar1 = "";//扩展字段1	预留字段
						String extendPar2 = "";//扩展字段2	预留字段
                        if(StringUtils.isBlank(batchID)) {
                            CSAppException.apperr(CrmCommException.CRM_COMM_103,"TD_S_COMMPARA表PARAM_CODE = 1288的对应配置PARA_CODE3券码为空");
                        }

						IData params = new DataMap();
						params.put("LoginType",loginType);
						params.put("LoginNo",loginNo);
						params.put("ChannelId",channelId);
						params.put("OprType",oprType);
						params.put("BatchID",batchID);
						params.put("SerialNumber",serialNumber);
						params.put("ObtainDate",obtainDate);
						params.put("RegionCode",regionCode);
						params.put("TaskId",taskId);
						params.put("ObtainBusi",obtainBusi);
						params.put("OrderId",orderId);
						params.put("ExtendPar1",extendPar1);
						params.put("ExtendPar2",extendPar2);

						IData rtnData = new DataMap();
						rtnData.put("params",params);
						try {
							CSAppCall.call("SS.PcardReceivetoIboss.pcardReceive", rtnData);
						}catch (Exception e) {
							String error = e.getMessage();
							if(error != null && error.length() > 2000) {
								error = error.substring(0,2000);
							}
							CSAppException.apperr(CrmCommException.CRM_COMM_103,"调用卡券领取接口失败 error：" + error);
						}
						break;
					}
				}
			}
        }else {
    		return;
		}
    }
}