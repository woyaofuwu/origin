package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.changesvcstate.order.action.reg;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SaleActiveTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcStateTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * 融合套餐的宽带与手机同步开、停机的配置需求
 *
 * tanzheng
 */
public class DealIntegratedPackageAction implements ITradeAction {
	protected static Logger log = Logger.getLogger(DealIntegratedPackageAction.class);
	@Override
	public void executeAction(BusiTradeData btd) throws Exception {
		log.debug(">>>>>>>>>>>>>>>DealIntegratedPackageAction Start>>>>>>>>>>>");
		String orderTypeCode = btd.getRD().getOrderTypeCode();
		if(log.isDebugEnabled()){
			log.debug(">>>>>>>>>>>>>>>DealIntegratedPackageAction line_32 >>>>>"+orderTypeCode+">>>>>>"+orderTypeCode);
		}
		if(!"7220".equals(orderTypeCode)){
			return;
		}
		String KDserialNumber = btd.getRD().getUca().getSerialNumber();
		String productId = btd.getRD().getUca().getProductId();
		if(log.isDebugEnabled()){
			log.debug(">>>>>>>>>>>>>>>DealIntegratedPackageAction line_21 >>>>>"+KDserialNumber+">>>>>>"+productId);
		}



		String serialNumber = KDserialNumber.replace("KD_","");
		UcaData snUcaData = UcaDataFactory.getNormalUca(serialNumber);
		String snProductId = snUcaData.getProductId();
		List<SaleActiveTradeData> actList = snUcaData.getUserSaleActiveByProductId("69908001");
		if(actList.size()>0){
			if(log.isDebugEnabled()){
				log.debug(">>>>>>>>>>>>>>>DealIntegratedPackageAction line_51 有宽带1+活动>>>>>"+serialNumber+">>>>>>"+actList);
			}
			return;
		}

		if(log.isDebugEnabled()){
			log.debug(">>>>>>>>>>>>>>>DealIntegratedPackageAction line_40 >>>>>"+serialNumber+">>>>>>"+snProductId);
		}

		IData param = new DataMap();
		param.put("SUBSYS_CODE","CSM");
		param.put("PARAM_ATTR","6190");
		param.put("PARAM_CODE",snProductId);
		IDataset config = CommparaInfoQry.getCommparaInfoByPara(param);

		String rateSvcId ="";
		if(IDataUtil.isEmpty(config)){
			log.error(">>>>>>>>>>>>>>>DealIntegratedPackageAction line_34 不是融合套餐>>>>>>>>>>>");
			return;
		}else{
			List<SvcTradeData> svcList = btd.getRD().getUca().getUserSvcs();
			for(SvcTradeData svcData : svcList){
				if("0".equals(svcData.getMainTag())){
					rateSvcId = svcData.getElementId();
					break;
				}
			}
			log.debug(">>>>>>>>>>>>>>>DealIntegratedPackageAction line_49 速率服务ID>>>>>>>>>>>"+rateSvcId);
			param.clear();
			param.put("SUBSYS_CODE","CSM");
			param.put("PARAM_ATTR","4000");
			param.put("PARAM_CODE",rateSvcId);
			IDataset rateConfig = CommparaInfoQry.getCommparaInfoByPara(param);
			if(IDataUtil.isEmpty(rateConfig)){
				log.error(">>>>>>>>>>>>>>>DealIntegratedPackageAction line_56 根据速率serviceId查找不到对应的速率配置>>>>>>>>>>>");
				return;
			}else{
				IData configData = config.first();
				IData rateConfigData = rateConfig.first();
				//当用户本身的宽带速率大于融合套餐赠送的速率的时候不做处理
				if(rateConfigData.getInt("PARA_CODE1")>configData.getInt("PARA_CODE1")){
					log.error(">>>>>>>>>>>>>>>DealIntegratedPackageAction line_63 用户本身的宽带速率大于融合套餐赠送的速率>>>>>>>>>>>");
					return;
				}else{

					List<SvcStateTradeData> tradesSvcDataList = btd.getTradeDatas(TradeTableEnum.TRADE_SVCSTATE);
					for (int j = 0, size = tradesSvcDataList.size(); j < size; j++)
					{
						SvcStateTradeData svcStateData = tradesSvcDataList.get(j);
						if ("1".equals(svcStateData.getModifyTag()))
						{
							svcStateData.setEndDate(SysDateMgr.getLastMonthLastDate());
						}
						else if ("0".equals(svcStateData.getModifyTag()))
						{
							svcStateData.setStartDate(SysDateMgr.getLastMonthLastDate());
						}
						svcStateData.setRemark("宽带融合套餐停机关联宽带立即停机");
					}

				}
			}
		}
		log.debug(">>>>>>>>>>>>>>>DealIntegratedPackageAction End>>>>>>>>>>>");
	}

}
