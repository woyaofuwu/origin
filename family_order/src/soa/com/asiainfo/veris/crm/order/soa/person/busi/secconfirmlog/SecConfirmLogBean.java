package com.asiainfo.veris.crm.order.soa.person.busi.secconfirmlog;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.RouteInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.bof.query.BofQuery;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.abilityopenplatform.AbilityPlatCheckRelativeQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.*;
import com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.secconfirmlog.SecConfirmLogQry;

public class SecConfirmLogBean extends CSBizBean
{

    public IData acceptLog(IData input) throws Exception
    {
        IData result = new DataMap();
        String serial_number = IDataUtil.chkParamNoStr(input,"SERIAL_NUMBER");
        String channel_source = IDataUtil.chkParamNoStr(input,"CHANNEL_SOURCE");//操作渠道来源
        String re_way = IDataUtil.chkParamNoStr(input,"RE_WAY");//二次确认方式
        String confirm_log = IDataUtil.chkParamNoStr(input,"CONFIRM_LOG");//二次确认行为日志
        String orderID = IDataUtil.chkParamNoStr(input,"ORDER_ID");//订单ID
        String commodityCode = input.getString("COMMODITY_CODE");//全网统一资费编码
        String elementId = input.getString("ELEMENT_ID");//省内编码
        String servType = input.getString("SERV_TYPE");//业务类型
        String spCode = input.getString("SP_CODE");//企业代码
        String operatorCode = input.getString("OPERATOR_CODE");//业务代码
        String biztypecode = input.getString("BIZ_TYPE_CODE");//业务类型代码

        String eparchyCode = RouteInfoQry.getEparchyCodeBySnForCrm(serial_number);
        UcaData uca = UcaDataFactory.getNormalUca(serial_number);
        //COMMODITY_CODE或SERV_TYPE + SP_CODE + OPERATOR_CODE + MEMBER_TYPE 确定一个业务，日志中两个必须有一个存在
        if(StringUtils.isEmpty(elementId)){
            if(StringUtils.isNotEmpty(commodityCode)){
                //查询出本地编码
                IDataset dataSet = AbilityPlatCheckRelativeQry.getCtrmProductByBossId(commodityCode, eparchyCode);
                if(IDataUtil.isNotEmpty(dataSet)){
                    elementId = dataSet.getData(0).getString("ELEMENT_ID");
                    if(elementId.isEmpty()){
                        elementId = dataSet.getData(0).getString("PRODUCT_ID");
                    }
                }
            }else if(StringUtils.isNotEmpty(spCode) && StringUtils.isNotEmpty(operatorCode)){
                IDataset platinfos = new DatasetList();
                if (StringUtils.isNotEmpty(biztypecode)) {
                    platinfos =  UpcCall.qryOffersBySpCond(spCode, operatorCode, biztypecode);
                }else {
                    platinfos = UpcCall.qryOffersBySpCond(spCode, operatorCode,"");
                }
                if(IDataUtil.isNotEmpty(platinfos)){
                    elementId = platinfos.getData(0).getString("SERVICE_ID");
                }
            }
        }else{
            if(StringUtils.isEmpty(commodityCode)){
                //根据本地编码查询出全网编码
                commodityCode = SecConfirmLogQry.getCrmProductsInfo(elementId,"",uca.getUserEparchyCode());
            }
            if(StringUtils.isEmpty(spCode) && StringUtils.isEmpty(operatorCode)){
                IDataset platInfoByServiceId = BofQuery.getPlatInfoByServiceId(elementId);
                if(IDataUtil.isNotEmpty(platInfoByServiceId)){
                    spCode = platInfoByServiceId.getData(0).getString("SP_CODE");
                    operatorCode = platInfoByServiceId.getData(0).getString("BIZ_CODE");
                }
            }
        }


        if((StringUtils.isEmpty(commodityCode))&&(StringUtils.isEmpty(servType) && StringUtils.isEmpty(spCode) && StringUtils.isEmpty(operatorCode))){
            result.put("X_RESULTCODE", "1");
            result.put("X_RESULTINFO", "COMMODITY_CODE或SERV_TYPE + SP_CODE + OPERATOR_CODE + MEMBER_TYPE必须有一个存在");
            return result;
        }
        if(StringUtils.isNumeric(orderID)) {
            IDataset tradeinfos = TradeBhQry.queryTradeInfoByOrderId(orderID, null);
            if (IDataUtil.isEmpty(tradeinfos)) {
                tradeinfos = TradeInfoQry.getTradeInfobyOrderId(orderID);
            }
            if (IDataUtil.isNotEmpty(tradeinfos)) {
                IData instInfo = getInstInfo(tradeinfos, elementId);
                if (IDataUtil.isNotEmpty(instInfo)) {
                    input.put("INST_ID", instInfo.getString("INST_ID"));
                    input.put("TRADE_ID1", instInfo.getString("TRADE_ID"));
                    input.put("INST_TYPE", instInfo.getString("ELEMENT_TYPE"));
                }
            }
        }
        input.put("USER_ID", uca.getUserId());
        input.put("ELEMENT_ID", elementId);
        SecConfirmLogQry.recordSecConfirmLog(input);
        result.put("X_RESULTCODE", "0");
        result.put("X_RESULTINFO", "成功");
        return result;
    }

    public IDataset querySecConfirmLog(IData input) throws Exception
    {
        UcaData uca = UcaDataFactory.getNormalUca(input.getString("SERIAL_NUMBER"));
        input.put("USER_ID", uca.getUserId());
        input.put("SERV_TYPE", input.getString("SERV_TYPE"));
        input.put("BOOK_TIME", input.getString("STRAT_BOOK_TIME"));
        input.put("CONFIRM_TIME",  input.getString("END_BOOK_TIME"));
        return SecConfirmLogQry.querySecConfirmLog(input);
    }


    public void autoCleanSecConfirmLog(IData input) throws Exception
    {
        //将业务取消后5个月的数据清除掉
        SecConfirmLogQry.cleanSecConfirmLog(input);
    }

    private IData getInstInfo(IDataset tradeinfos,String elementId)throws Exception{
        IData result = new DataMap();
        for(int i = 0; i < tradeinfos.size(); i++) {
            IData tradeinfo = tradeinfos.getData(i);
            String intfID = tradeinfo.getString("INTF_ID");
            String tradeID = tradeinfo.getString("TRADE_ID");
            String[] tableNames = intfID.split(",");
            for (String tableName : tableNames) {
                if ("TF_B_TRADE_SVC".equals(tableName)) {
                    IDataset tradesvcs = TradeSvcInfoQry.getTradeSvcByTradeId(tradeID);
                    IDataset svcsfilter = DataHelper.filter(tradesvcs, "SERVICE_ID=" + elementId);
                    if (IDataUtil.isNotEmpty(svcsfilter)) {
                        result.put("INST_ID", svcsfilter.getData(0).getString("INST_ID"));
                        result.put("ELEMENT_TYPE", "S");
                        result.put("TRADE_ID", tradeID);
                        break;
                    }
                }
                if ("TF_B_TRADE_DISCNT".equals(tableName)) {
                    IDataset tradediscnts = TradeDiscntInfoQry.getTradeDiscntInfosByDiscntCode(tradeID, elementId);
//                IDataset discntsfilter = DataHelper.filter(tradediscnts, "DISCNT_CODE=" + elementId);
                    if (IDataUtil.isNotEmpty(tradediscnts)) {
                        result.put("INST_ID", tradediscnts.getData(0).getString("INST_ID"));
                        result.put("ELEMENT_TYPE", "D");
                        result.put("TRADE_ID", tradeID);
                        break;
                    }
                }
                if ("TF_B_TRADE_PLATSVC".equals(tableName)) {
                    IDataset tradeplatsvcs = TradePlatSvcInfoQry.getTradePlatSvcByTradeId(tradeID);
                    IDataset platsvcsfilter = DataHelper.filter(tradeplatsvcs, "SERVICE_ID=" + elementId);
                    if (IDataUtil.isNotEmpty(platsvcsfilter)) {
                        result.put("INST_ID", platsvcsfilter.getData(0).getString("INST_ID"));
                        result.put("ELEMENT_TYPE", "Z");
                        result.put("TRADE_ID", tradeID);
                        break;
                    }
                }
                if ("TF_B_TRADE_PRODUCT".equals(tableName)) {
                    IDataset tradeproducts = TradeProductInfoQry.getTradeProductByTradeId(tradeID);
                    IDataset productsfilter = DataHelper.filter(tradeproducts, "PRODUCT_ID=" + elementId);
                    if (IDataUtil.isNotEmpty(productsfilter)) {
                        result.put("INST_ID", productsfilter.getData(0).getString("INST_ID"));
                        result.put("ELEMENT_TYPE", "P");
                        result.put("TRADE_ID", tradeID);
                        break;
                    }
                }
            }
        }
        return result;
    }
}