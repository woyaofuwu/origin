package com.asiainfo.veris.crm.order.soa.person.common.action.sms;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.bof.query.BofQuery;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradePlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.secconfirmlog.SecConfirmLogQry;
import org.apache.log4j.Logger;


public class RecordSecConfirmLogAction implements ITradeFinishAction {

    private static transient Logger logger = Logger.getLogger(RecordSecConfirmLogAction.class);

    @Override
    public void executeAction(IData mainTrade) throws Exception {
        String tradeTypeCode = mainTrade.getString("TRADE_TYPE_CODE");
        String tradeId = mainTrade.getString("TRADE_ID");
        String orderId = mainTrade.getString("ORDER_ID");
        String serialNumber = mainTrade.getString("SERIAL_NUMBER");
        String userId = mainTrade.getString("USER_ID");
        String acceptDate = mainTrade.getString("ACCEPT_DATE");
        String inmodecode = mainTrade.getString("IN_MODE_CODE");

        try {
            UcaData uca = UcaDataFactory.getNormalUca(serialNumber);
            IData input = new DataMap();
            String channelSource = SecConfirmLogQry.getChannelSource(inmodecode);
            IDataset channelReconfirms = SecConfirmLogQry.queryChannelReconfirmByCond(channelSource);
            if (IDataUtil.isNotEmpty(channelReconfirms)) {
                input.put("RE_WAY", channelReconfirms.getData(0).getString("RE_CONFIRM_WAY"));
            }
            input.put("SERIAL_NUMBER", uca.getSerialNumber());
            input.put("USER_ID", userId);
            input.put("ORDER_ID", orderId);
            input.put("TRADE_ID1", tradeId);//业务受理流水号
            input.put("CONFIRM_LOG", "");//二次确认行为日志
            input.put("CONFIRM_TIME", acceptDate);
            input.put("CHANNEL_SOURCE", channelSource);//受理渠道
            input.put("BOOK_TIME", acceptDate);
            input.put("CREATE_TIME", acceptDate);
            input.put("IN_MODE_CODE", inmodecode);
            IDataset tradeproducts = TradeProductInfoQry.getTradeProductByTradeId(tradeId);//产品
            if (IDataUtil.isNotEmpty(tradeproducts)) {
                for (int i = 0; i < tradeproducts.size(); i++) {
                    IData tradeproduct = tradeproducts.getData(i);
                    if (BofConst.MODIFY_TAG_ADD.equals(tradeproduct.getString("MODIFY_TAG"))) {//业务受理记录日志
                        String elementId = tradeproduct.getString("PRODUCT_ID");
                        String instId = tradeproduct.getString("INST_ID");
//                        String ctrmProductId = SecConfirmLogQry.getCrmProductsInfo(elementId, "P", uca.getUserEparchyCode());
                        IDataset orderReconfirms = UpcCall.queryOrderReconfirmByInternalCode(elementId);
                        if(IDataUtil.isNotEmpty(orderReconfirms)){
                        	String commodityCode = orderReconfirms.getData(0).getString("COMMODITY_CODE");
                        	if (StringUtils.isNotEmpty(commodityCode)) {
                                input.put("INST_ID", instId);
                                input.put("COMMODITY_CODE", commodityCode);
                                input.put("INST_TYPE", "P");
                                input.put("ELEMENT_ID", elementId);
                                input.put("RE_CONFIRM", orderReconfirms.getData(0).getString("RE_CONFIRM_WAY"));
                                input.put("END_DATE", tradeproduct.getString("END_DATE"));
                                if (isSecConfirm(input)) {
                                    SecConfirmLogQry.recordSecConfirmLog(input);
                                }
                            }
                        }
                    } else if (BofConst.MODIFY_TAG_DEL.equals(tradeproduct.getString("MODIFY_TAG"))) {
                        //如果是业务取消  将受理表日志搬到取消表
                        IData param = new DataMap();
                        param.put("TRADE_ID2", tradeId);
                        param.put("CREATE_TIME", acceptDate);
                        param.put("INST_ID", tradeproduct.getString("INST_ID"));
                        param.put("USER_ID", tradeproduct.getString("USER_ID"));
                        SecConfirmLogQry.recordCancelSecConfirmLog(param);
                        SecConfirmLogQry.deleteSecConfirmLog(param);
                    }
                }
            } 
            IDataset tradesvcs = TradeSvcInfoQry.getTradeSvcByTradeId(tradeId);//服务
            if (IDataUtil.isNotEmpty(tradesvcs)) {
                for (int i = 0; i < tradesvcs.size(); i++) {
                    IData tradesvc = tradesvcs.getData(i);
                    if (BofConst.MODIFY_TAG_ADD.equals(tradesvc.getString("MODIFY_TAG"))) {
                        String elementId = tradesvc.getString("SERVICE_ID");
                        String instId = tradesvc.getString("INST_ID");
                        //String ctrmProductId = SecConfirmLogQry.getCrmProductsInfo(elementId, "S", uca.getUserEparchyCode());
                        IDataset orderReconfirms = UpcCall.queryOrderReconfirmByInternalCode(elementId);
                        if(IDataUtil.isNotEmpty(orderReconfirms)){
                        	String commodityCode = orderReconfirms.getData(0).getString("COMMODITY_CODE");
                        	if (StringUtils.isNotEmpty(commodityCode)) {
                                input.put("INST_ID", instId);
                                input.put("COMMODITY_CODE", commodityCode);
                                input.put("INST_TYPE", "S");
                                input.put("ELEMENT_ID", elementId);
                                input.put("RE_CONFIRM", orderReconfirms.getData(0).getString("RE_CONFIRM_WAY"));
                                input.put("END_DATE", tradesvc.getString("END_DATE"));
                                if (isSecConfirm(input)) {
                                    SecConfirmLogQry.recordSecConfirmLog(input);
                                }
                            }
                        }
                        
                    } else if (BofConst.MODIFY_TAG_DEL.equals(tradesvc.getString("MODIFY_TAG"))) {
                        //如果是业务取消  将受理表日志搬到取消表
                        IData param = new DataMap();
                        param.put("TRADE_ID2", tradeId);
                        param.put("CREATE_TIME", acceptDate);
                        param.put("INST_ID", tradesvc.getString("INST_ID"));
                        param.put("USER_ID", tradesvc.getString("USER_ID"));
                        SecConfirmLogQry.recordCancelSecConfirmLog(param);
                        SecConfirmLogQry.deleteSecConfirmLog(param);
                    }
                }
            }

            IDataset tradediscnts = TradeDiscntInfoQry.getTradeDiscntByTradeId(tradeId);//优惠
            if (IDataUtil.isNotEmpty(tradediscnts)) {
                for (int i = 0; i < tradediscnts.size(); i++) {
                    IData tradediscnt = tradediscnts.getData(i);
                    if (BofConst.MODIFY_TAG_ADD.equals(tradediscnt.getString("MODIFY_TAG"))) {
                        String elementId = tradediscnt.getString("DISCNT_CODE");
                        String instId = tradediscnt.getString("INST_ID");
                       // String ctrmProductId = SecConfirmLogQry.getCrmProductsInfo(elementId, "D", uca.getUserEparchyCode());
                        IDataset orderReconfirms = UpcCall.queryOrderReconfirmByInternalCode(elementId);
                        if(IDataUtil.isNotEmpty(orderReconfirms)){
                        	String commodityCode = orderReconfirms.getData(0).getString("COMMODITY_CODE");
                        	 if (StringUtils.isNotEmpty(commodityCode)) {
                                 input.put("INST_ID", instId);
                                 input.put("COMMODITY_CODE", commodityCode);
                                 input.put("INST_TYPE", "D");
                                 input.put("ELEMENT_ID", elementId);
                                 input.put("RE_CONFIRM", orderReconfirms.getData(0).getString("RE_CONFIRM_WAY"));
                                 input.put("END_DATE", tradediscnt.getString("END_DATE"));
                                 if (isSecConfirm(input)) {//进行了二次确认的才记录
                                     SecConfirmLogQry.recordSecConfirmLog(input);
                                 }
                             }
                        }
                       
                    } else if (BofConst.MODIFY_TAG_DEL.equals(tradediscnt.getString("MODIFY_TAG"))) {
                        //如果是业务取消  将受理表日志搬到取消表
                        IData param = new DataMap();
                        param.put("TRADE_ID2", tradeId);
                        param.put("CREATE_TIME", acceptDate);
                        param.put("INST_ID", tradediscnt.getString("INST_ID"));
                        param.put("USER_ID", tradediscnt.getString("USER_ID"));
                        SecConfirmLogQry.recordCancelSecConfirmLog(param);
                        SecConfirmLogQry.deleteSecConfirmLog(param);
                    }
                }
            }

            IDataset tradeplatsvcs = TradePlatSvcInfoQry.getTradePlatSvcByTradeId(tradeId);
            if (IDataUtil.isNotEmpty(tradeplatsvcs)) {
                for (int i = 0; i < tradeplatsvcs.size(); i++) {
                    IData tradeplatsvc = tradeplatsvcs.getData(i);
                    if (BofConst.MODIFY_TAG_ADD.equals(tradeplatsvc.getString("MODIFY_TAG"))) {
                        String elementId = tradeplatsvc.getString("SERVICE_ID");
                        String instId = tradeplatsvc.getString("INST_ID");
                        IDataset platInfoByServiceId = BofQuery.getPlatInfoByServiceId(elementId);
                        if (IDataUtil.isNotEmpty(platInfoByServiceId)) {
                            IData data = platInfoByServiceId.getData(0);
                            String spCode = data.getString("SP_CODE", "");
                            String bizCode = data.getString("BIZ_CODE", "");
                            input.put("COMMODITY_CODE", "");
                            input.put("INST_ID", instId);
                            input.put("SERV_TYPE", "");
                            input.put("SP_CODE", spCode);
                            input.put("OPERATOR_CODE", bizCode);
                            input.put("INST_TYPE", "Z");
                            input.put("ELEMENT_ID", elementId);
                            input.put("RE_CONFIRM", data.getString("SECCONFIRM_TAG"));
                            input.put("END_DATE", tradeplatsvc.getString("END_DATE"));
                            if (isSecConfirm(input)) {//进行了二次确认的才记录
                                SecConfirmLogQry.recordSecConfirmLog(input);
                            }
                        }
                    } else if (BofConst.MODIFY_TAG_DEL.equals(tradeplatsvc.getString("MODIFY_TAG"))) {
                        //如果是业务取消  将受理表日志搬到取消表
                        IData param = new DataMap();
                        param.put("TRADE_ID2", tradeId);
                        param.put("CREATE_TIME", acceptDate);
                        param.put("INST_ID", tradeplatsvc.getString("INST_ID"));
                        param.put("USER_ID", tradeplatsvc.getString("USER_ID"));
                        SecConfirmLogQry.recordCancelSecConfirmLog(param);
                        SecConfirmLogQry.deleteSecConfirmLog(param);
                    }
                }
            }
        }catch (Exception e){
            logger.debug(e);
        }
    }

    //查询局数据判断是否二次确认
    private boolean isSecConfirm(IData param)throws Exception {
        boolean confirmFlag = false;
        if("1".equals(param.getString("RE_CONFIRM"))){
            confirmFlag = true;
        }
        if(confirmFlag){//需要发二次确认
            //特例局数据
            IDataset specialReconfirms= SecConfirmLogQry.querySepcReconfirmCond(param);
            if(IDataUtil.isNotEmpty(specialReconfirms)){
                param.put("RE_WAY", specialReconfirms.getData(0).getString("RE_CONFIRM_WAY"));
            }
            //如果二次确认方式为空或者不是以1开头的确认方式，则不记录日志
	       	if(StringUtils.isEmpty(param.getString("RE_WAY")) || !param.getString("RE_WAY").startsWith("1")){
				return false;
			}
            if("102".equals(param.getString("RE_WAY"))){//短信二次确认
                param.put("CONFIRM_LOG", "是");
            }else if("101".equals(param.getString("RE_WAY"))){
                param.put("CONFIRM_LOG","服务密码" );
            }else if("103".equals(param.getString("RE_WAY"))){
                param.put("CONFIRM_LOG","电子签名" );
            }else if("104".equals(param.getString("RE_WAY"))){
                param.put("CONFIRM_LOG","业务受理单" );
            }
        }
        return confirmFlag;
    }
}