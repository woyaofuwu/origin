package com.asiainfo.veris.crm.order.soa.person.common.util;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeOfferRelInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeSaleActive;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOfferRelInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;

public class UndoOfferRelBakDeal
{

    public static void dealOfferRelBak(IData mainTrade) throws Exception
    {
        String tradeId = mainTrade.getString("TRADE_ID");
        String intfId = mainTrade.getString("INTF_ID");
        String userId = mainTrade.getString("USER_ID");
        String tradeTypeCode = mainTrade.getString("TRADE_TYPE_CODE");
        
        IDataset tradeOfferRels = TradeOfferRelInfoQry.getOfferRelBakByTradeId(tradeId);
        if(IDataUtil.isNotEmpty(tradeOfferRels))//存在OFFER_REL_BAK数据，说明是新系统办的业务，不需要处理
        {
            return;
        }
        
        //不存在OFFER_REL_BAK数据，说明是老系统办的业务，需要新增OFFER_REL_BAK数据
        String offerCode = null;
        String offerType = null;
        String offerInsId = null;
        String startDate = null;
        String endDate = null;

        boolean isSaleActive = intfId.indexOf("TF_B_TRADE_SALE_ACTIVE") > -1 ? true : false;
        if(isSaleActive)
        {
            IDataset tradeSaleActives = TradeSaleActive.getTradeSaleActiveByTradeId(tradeId);
            IData tradeSaleActive = tradeSaleActives.getData(0);
            
            offerCode = tradeSaleActive.getString("PACKAGE_ID");
            offerType = "K";
            offerInsId = tradeSaleActive.getString("INST_ID");
            startDate = tradeSaleActive.getString("START_DATE");
            endDate = tradeSaleActive.getString("END_DATE");
        }
        else
        {
            IData userMainProduct = null;
            
            IDataset tradeMainProducts = TradeProductInfoQry.getAllTradeBakProductByTradeId(tradeId);
            for(Object obj : tradeMainProducts)
            {
                IData tradeMainProduct = (IData) obj;
                String mainTag = tradeMainProduct.getString("MAIN_TAG");
                String tmpEndDate = tradeMainProduct.getString("END_DATE");
                if("1".equals(mainTag) && SysDateMgr.compareTo(tmpEndDate, SysDateMgr.getSysTime()) > 0)
                {
                    userMainProduct = tradeMainProduct;
                    break;
                }
            }
            
            if(IDataUtil.isEmpty(userMainProduct))
            {
                IDataset userMainProducts = UserProductInfoQry.queryUserMainProduct(userId);
                String minEndDate = null;
                for(Object obj : userMainProducts)
                {
                    IData mainProduct = (IData) obj;
                    String tmpEndDate = mainProduct.getString("END_DATE");
                    if(StringUtils.isBlank(minEndDate))
                    {
                        minEndDate = tmpEndDate;
                        userMainProduct = mainProduct;
                    }
                    if(StringUtils.isNotBlank(minEndDate) && SysDateMgr.compareTo(tmpEndDate, minEndDate) < 0)
                    {
                        minEndDate = tmpEndDate;
                        userMainProduct = mainProduct;
                    }
                }
            }
            
            offerCode = userMainProduct.getString("PRODUCT_ID");
            offerType = "P";
            offerInsId = userMainProduct.getString("INST_ID");
            startDate = userMainProduct.getString("START_DATE");
            endDate = userMainProduct.getString("END_DATE");
        }
        
        IData commParam = new DataMap();
        commParam.put("TRADE_ID", tradeId);
        commParam.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(tradeId));
        commParam.put("PARTITION_ID", StrUtil.getPartition4ById(userId));
        commParam.put("OFFER_CODE", offerCode);
        commParam.put("OFFER_TYPE", offerType);
        commParam.put("OFFER_INS_ID", offerInsId);
        commParam.put("USER_ID", userId);
        commParam.put("START_DATE", startDate);
        commParam.put("END_DATE", endDate);
        commParam.put("REMARK", "UndoDealOfferRelAction新增");
        
        if(intfId.indexOf("TF_B_TRADE_SVC") > -1)
        {
            dealOfferRelBakBySvc(commParam);
        }
        
        if(intfId.indexOf("TF_B_TRADE_DISCNT") > -1)
        {
            dealOfferRelBakByDiscnt(commParam);
        }
        
        if(intfId.indexOf("TF_B_TRADE_PLATSVC") > -1)
        {
            
        }
    }
    
    private static void dealOfferRelBakBySvc(IData commParam) throws Exception
    {
        String tradeId = commParam.getString("TRADE_ID");
        IDataset tradeBakSvcs = TradeSvcInfoQry.getValidTradeBakSvcByTradeId(tradeId);
        IDataset tradeSvcs = TradeSvcInfoQry.getTradeSvcByTradeId(tradeId);
        IDataset offerRels = buildValidOfferRelBakInfos(tradeBakSvcs, tradeSvcs, commParam, "S");
            
        Dao.insert("TF_B_TRADE_OFFER_REL_BAK", offerRels);
    }
    
    private static void dealOfferRelBakByDiscnt(IData commParam) throws Exception
    {
        String tradeId = commParam.getString("TRADE_ID");
        IDataset tradeBakDiscnts = TradeDiscntInfoQry.getValidTradeBakDiscntByTradeId(tradeId);
        IDataset tradeDiscnts = TradeDiscntInfoQry.getTradeDiscntByTradeId(tradeId);
        IDataset offerRels = buildValidOfferRelBakInfos(tradeBakDiscnts, tradeDiscnts, commParam, "D");
            
        Dao.insert("TF_B_TRADE_OFFER_REL_BAK", offerRels);
    }

    private static String getStartDate(String offerStartDate, String relOfferStartDate) throws Exception
    {
        if(SysDateMgr.compareTo(relOfferStartDate, offerStartDate) > 0){
            return relOfferStartDate;
        }
        else{
            return offerStartDate;
        }
    }
    
    private static String getEndDate(String offerEndDate, String relOfferEndDate) throws Exception
    {
        if(SysDateMgr.compareTo(relOfferEndDate, offerEndDate) > 0){
            return offerEndDate;
        }
        else{
            return relOfferEndDate;
        }
    }
    
    private static IDataset buildValidOfferRelBakInfos(IDataset bakInfos, IDataset tradeInfos, IData commParam, String offerType) throws Exception
    {
        IDataset result = new DatasetList();
        
        for(int i=0,tradeSize=tradeInfos.size();i<tradeSize;i++)
        {
            IData tradeInfo = tradeInfos.getData(i);
            String modifyTag = tradeInfo.getString("MODIFY_TAG");
            if(BofConst.MODIFY_TAG_ADD.equals(modifyTag))
            {
                continue;
            }
            
            String tradeInstId = tradeInfo.getString("INST_ID");
            
            for(int j=0,bakSize=bakInfos.size();j<bakSize;j++)
            {
                IData bakInfo = bakInfos.getData(j);
                String bakInstId = bakInfo.getString("INST_ID");
                if(tradeInstId.equals(bakInstId))
                {
                    String bakUserId = bakInfo.getString("USER_ID");
                    String startDate = bakInfo.getString("START_DATE");
                    String endDate = bakInfo.getString("END_DATE");
                    String updateTime = bakInfo.getString("UPDATE_TIME");
                    String updateStaffId = bakInfo.getString("UPDATE_STAFF_ID");
                    String updateDepartId = bakInfo.getString("UPDATE_DEPART_ID");
                    
                    IData offerRel = new DataMap();
                    offerRel.putAll(commParam);
                    offerRel.put("INST_ID", getOfferRelInstId(commParam.getString("OFFER_INS_ID"), bakInstId));
                    offerRel.put("GROUP_ID", getGroupId(commParam.getString("OFFER_CODE"), commParam.getString("OFFER_TYPE"), getOfferCode(bakInfo, offerType), offerType));
                    offerRel.put("REL_OFFER_CODE", getOfferCode(bakInfo, offerType));
                    offerRel.put("REL_OFFER_TYPE", offerType);
                    offerRel.put("REL_OFFER_INS_ID", bakInstId);
                    offerRel.put("REL_USER_ID", bakUserId);
                    offerRel.put("REL_TYPE", "C");
                    offerRel.put("START_DATE", getStartDate(commParam.getString("START_DATE"), startDate));
                    offerRel.put("END_DATE", getEndDate(commParam.getString("END_DATE"), endDate));
                    offerRel.put("UPDATE_TIME", updateTime);
                    offerRel.put("UPDATE_STAFF_ID", updateStaffId);
                    offerRel.put("UPDATE_DEPART_ID", updateDepartId);
                    
                    result.add(offerRel);
                    break;
                }
            }
        }
        
        return result;
    }
    
    private static String getOfferCode(IData offerInfo, String offerType) throws Exception
    {
        if("S".equals(offerType))
        {
            return offerInfo.getString("SERVICE_ID");
        }
        else if("D".equals(offerType))
        {
            return offerInfo.getString("DISCNT_CODE");
        }
        
        return null;
    }
    
    private static String getGroupId(String offerCode, String offerType, String relOfferCode, String relOfferType) throws Exception
    {
        IDataset groupInfos = UpcCall.qryGroupInfoByOfferIdAndGroupOfferId(offerCode, offerType, relOfferCode, relOfferType);
        if(IDataUtil.isNotEmpty(groupInfos))
        {
            return groupInfos.getData(0).getString("GROUP_ID");
        }
        
        return "0";
    }
    
    private static String getOfferRelInstId(String offerInsId, String relOfferInsId) throws Exception
    {
        IData offerRel = UserOfferRelInfoQry.qryUserAllOfferRelInfoByOfferInsIdAndRelOfferInsId(offerInsId, relOfferInsId);
        return offerRel.getString("INST_ID");
    }
}
