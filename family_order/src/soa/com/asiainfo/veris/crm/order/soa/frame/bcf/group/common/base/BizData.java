
package com.asiainfo.veris.crm.order.soa.frame.bcf.group.common.base;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.Obj2Xml;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.log.LogBaseBean;

import java.util.Iterator;

public final class BizData
{
    private final IData bd = new DataMap();

    private String routeId = "";

    public BizData()
    {
        try
        {
            routeId = BizRoute.getRouteId();
        }
        catch (Exception e)
        {
            
            e.printStackTrace();
        }
    }

    /**
     * 从inData参数中获取td，再将td中的订单信息和台帐信息记录到对应的表中
     * 
     * @param inData
     *            入参，必须包含key为tradeData的td对象
     * @return 返回主台帐信息的IData对象
     * @throws Exception
     */
    public void commit() throws Exception
    {

        Iterator it = bd.keySet().iterator();

        IDataset idsRecord = null;

        while (it.hasNext())
        {
            String tableName = (String) it.next();

            idsRecord = bd.getDataset(tableName);

            if (IDataUtil.isEmpty(idsRecord))
            {
                continue;
            }

            Dao.insert(tableName, idsRecord, Route.getJourDb(routeId));
        }
    }

    public IData getBizData()
    {

        return bd;
    }

    private IData getMyMap(TradeTableEnum entityName) throws Exception
    {

        IDataset ids = getMySet(entityName);

        IData map = null;

        if (IDataUtil.isEmpty(ids))
        {
            map = new DataMap();
            ids.add(map);
        }
        else
        {
            map = ids.getData(0);
        }

        return map;
    }

    private IDataset getMySet(TradeTableEnum entityName)
    {

        String key = entityName.getValue();

        IDataset ids = bd.getDataset(key);

        if (IDataUtil.isEmpty(ids))
        {
            ids = new DatasetList();

            bd.put(key, ids);
        }

        return ids;
    }

    public IDataset getOcsBatdeal()
    {

        return getMySet(TradeTableEnum.OCS_BATDEAL);
    }

    public IData getOrder() throws Exception
    {

        return getMyMap(TradeTableEnum.ORDER);
    }

    public IDataset getOrderSub()
    {

        return getMySet(TradeTableEnum.ORDER_SUB);
    }

    public String getRoute()
    {
        return routeId;
    }

    public IDataset getSms()
    {

        return getMySet(TradeTableEnum.Sms);
    }

    public IData getTrade() throws Exception
    {

        return getMyMap(TradeTableEnum.TRADE_MAIN);
    }

    public IDataset getTradeAccount()
    {

        return getMySet(TradeTableEnum.TRADE_ACCOUNT);
    }

    public IDataset getTradeAccountAcctday()
    {

        return getMySet(TradeTableEnum.TRADE_ACCOUNT_ACCTDAY);
    }

    public IDataset getTradeAcctConsign()
    {

        return getMySet(TradeTableEnum.TRADE_ACCT_CONSIGN);
    }

    public IDataset getTradeAppeal()
    {

        return getMySet(TradeTableEnum.TRADE_APPEAL);
    }

    public IDataset getTradeAttr()
    {

        return getMySet(TradeTableEnum.TRADE_ATTR);
    }

    public IDataset getTradeBatdeal()
    {

        return getMySet(TradeTableEnum.TRADE_BATDEAL);
    }

    public IDataset getTradeBatpboss()
    {

        return getMySet(TradeTableEnum.TRADE_BATPBOSS);
    }

    public IDataset getTradeBlackwhite()
    {

        return getMySet(TradeTableEnum.TRADE_BLACKWHITE);
    }

    public IDataset getTradeBrandchange()
    {

        return getMySet(TradeTableEnum.TRADE_BRANDCHANGE);
    }

    public IDataset getTradeCalllog()
    {

        return getMySet(TradeTableEnum.TRADE_CALLLOG);
    }

    public IDataset getTradeCnoteInfo()
    {

        return getMySet(TradeTableEnum.TRADE_CNOTE_INFO);
    }

    public IDataset getTradeCredit()
    {

        return getMySet(TradeTableEnum.TRADE_CREDIT);
    }

    public IDataset getTradeCustFamily()
    {

        return getMySet(TradeTableEnum.TRADE_CUST_FAMILY);
    }

    public IDataset getTradeCustFamilymeb()
    {

        return getMySet(TradeTableEnum.TRADE_CUST_FAMILYMEB);
    }

    public IDataset getTradeCustGroup()
    {

        return getMySet(TradeTableEnum.TRADE_CUST_GROUP);
    }

    public IDataset getTradeCustomer()
    {

        return getMySet(TradeTableEnum.TRADE_CUSTOMER);
    }

    public IDataset getTradeCustPerson()
    {

        return getMySet(TradeTableEnum.TRADE_CUST_PERSON);
    }

    public IDataset getTradeDataLine()
    {

        return getMySet(TradeTableEnum.TRADE_USER_DATALINE);
    }

    public IDataset getTradeDataLineAttr()
    {

        return getMySet(TradeTableEnum.TRADE_DATALINE_ATTR);
    }

    public IDataset getTradeDetail()
    {

        return getMySet(TradeTableEnum.TRADE_DETAIL);
    }

    public IDataset getTradeDevelop()
    {

        return getMySet(TradeTableEnum.TRADE_DEVELOP);
    }

    public IDataset getTradeDevlog()
    {

        return getMySet(TradeTableEnum.TRADE_DEVLOG);
    }

    public IDataset getTradeDiscnt()
    {

        return getMySet(TradeTableEnum.TRADE_DISCNT);
    }

    public IDataset getTradeElement()
    {

        return getMySet(TradeTableEnum.TRADE_ELEMENT);
    }

    public IDataset getTradeEvent()
    {

        return getMySet(TradeTableEnum.TRADE_EVENT);
    }

    public IDataset getTradeEventParam()
    {

        return getMySet(TradeTableEnum.TRADE_EVENT_PARAM);
    }

    public IDataset getTradeExt()
    {

        return getMySet(TradeTableEnum.TRADE_EXT);
    }

    public IDataset getTradefeeCheck()
    {

        return getMySet(TradeTableEnum.TRADE_FEECHECK);
    }

    public IDataset getTradefeeDefer()
    {

        return getMySet(TradeTableEnum.TRADE_FEEDEFER);
    }

    public IDataset getTradefeeDevice()
    {

        return getMySet(TradeTableEnum.TRADE_FEEDEVICE);
    }

    public IDataset getTradefeeGiftfee()
    {

        return getMySet(TradeTableEnum.TRADE_GIFTFEE);
    }
    
    public IDataset getTradefeeOtherfee()
    {

        return getMySet(TradeTableEnum.TRADE_OTHERFEE);
    }

    public IDataset getTradefeePaymoney()
    {

        return getMySet(TradeTableEnum.TRADE_PAYMONEY);
    }

    public IDataset getTradefeeSub()
    {

        return getMySet(TradeTableEnum.TRADE_FEESUB);
    }

    public IDataset getTradeFeeTax()
    {

        return getMySet(TradeTableEnum.TRADE_FEETAX);
    }

    public IDataset getTradeGroup()
    {

        return getMySet(TradeTableEnum.TRADE_GROUP);
    }

    public IDataset getTradeGroupmember()
    {

        return getMySet(TradeTableEnum.TRADE_GROUPMEMBER);
    }

    public IDataset getTradeGrpCenpay()
    {

        return getMySet(TradeTableEnum.TRADE_GRP_CENPAY);
    }

    public IDataset getTradeGrpMebPlatsvc()
    {

        return getMySet(TradeTableEnum.TRADE_GRP_MEB_PLATSVC);
    }

    public IDataset getTradeGrpMerch()
    {

        return getMySet(TradeTableEnum.TRADE_GRP_MERCH);
    }

    public IDataset getTradeGrpMerchDiscnt()
    {

        return getMySet(TradeTableEnum.TRADE_GRP_MERCH_DISCNT);
    }

    public IDataset getTradeGrpMerchMeb()
    {

        return getMySet(TradeTableEnum.TRADE_GRP_MERCH_MEB);
    }

    public IDataset getTradeGrpMerchp()
    {

        return getMySet(TradeTableEnum.TRADE_GRP_MERCHP);
    }

    public IDataset getTradeGrpMerchpDiscnt()
    {

        return getMySet(TradeTableEnum.TRADE_GRP_MERCHP_DISCNT);
    }

    public IDataset getTradeGrpMolist()
    {

        return getMySet(TradeTableEnum.TRADE_GRP_MOLIST);
    }

    public IDataset getTradeGrpPackage()
    {

        return getMySet(TradeTableEnum.TRADE_GRP_PACKAGE);
    }

    public IDataset getTradeGrpPlatsvc()
    {

        return getMySet(TradeTableEnum.TRADE_GRP_PLATSVC);
    }

    public IDataset getTradeImpu()
    {

        return getMySet(TradeTableEnum.TRADE_IMPU);
    }

    public IDataset getTradeIntegralAcct()
    {

        return getMySet(TradeTableEnum.TRADE_INTEGRALACCT);
    }

    public IDataset getTradeMbmp()
    {

        return getMySet(TradeTableEnum.TRADE_MBMP);
    }

    public IDataset getTradeMbmpPlus()
    {

        return getMySet(TradeTableEnum.TRADE_MBMP_PLUS);
    }

    public IDataset getTradeMbmpSub()
    {

        return getMySet(TradeTableEnum.TRADE_MBMP_SUB);
    }

    public IDataset getTradeMebCenpay()
    {

        return getMySet(TradeTableEnum.TRADE_MEB_CENPAY);
    }

    public IDataset getTradeMerchMbDis()
    {

        return getMySet(TradeTableEnum.TRADE_GRP_MERCH_MB_DIS);
    }

    public IDataset getTrademgrInstance()
    {

        return getMySet(TradeTableEnum.TRADE_MGR_INSTANCE);
    }

    public IDataset getTrademgrpbossInstance()
    {

        return getMySet(TradeTableEnum.TRADE_MGRPBOSS_INSTANCE);
    }

    public IDataset getTradeMpute()
    {

        return getMySet(TradeTableEnum.TRADE_MPUTE);
    }

    public IDataset getTradeNode()
    {

        return getMySet(TradeTableEnum.TRADE_NODE);
    }

    public IDataset getTradeOcs()
    {

        return getMySet(TradeTableEnum.TRADE_OCS);
    }

    public IDataset getTradeOther()
    {

        return getMySet(TradeTableEnum.TRADE_OTHER);
    }

    public IDataset getTradeOutprovGrp()
    {

        return getMySet(TradeTableEnum.TRADE_OUTPROV_GRP);
    }

    public IDataset getTradePayrelation()
    {

        return getMySet(TradeTableEnum.TRADE_PAYRELATION);
    }

    public IDataset getTradePbossFinish()
    {

        return getMySet(TradeTableEnum.TRADE_PBOSS_FINISH);
    }

    public IDataset getTradePerson()
    {

        return getMySet(TradeTableEnum.TRADE_PERSON);
    }

    public IDataset getTradePlatsvc()
    {

        return getMySet(TradeTableEnum.TRADE_PLATSVC);
    }

    public IDataset getTradePlatsvcAttr()
    {

        return getMySet(TradeTableEnum.TRADE_PLATSVC_ATTR);
    }

    public IDataset getTradePost()
    {

        return getMySet(TradeTableEnum.TRADE_POST);
    }

    public IDataset getTradePredeal()
    {

        return getMySet(TradeTableEnum.TRADE_PREDEAL);
    }

    public IDataset getTradeProduct()
    {

        return getMySet(TradeTableEnum.TRADE_PRODUCT);
    }

    public IDataset getTradeQueue()
    {

        return getMySet(TradeTableEnum.TRADE_QUEUE);
    }

    public IDataset getTradeRelation()
    {

        return getMySet(TradeTableEnum.TRADE_RELATION);
    }

    public IDataset getTradeRelationAa()
    {

        return getMySet(TradeTableEnum.TRADE_RELATION_AA);
    }

    public IDataset getTradeRelationBb()
    {

        return getMySet(TradeTableEnum.TRADE_RELATION_BB);
    }

    public IDataset getTradeRelationXxt()
    {

        return getMySet(TradeTableEnum.TRADE_RELATION_XXT);
    }

    public IDataset getTradeRent()
    {

        return getMySet(TradeTableEnum.TRADE_RENT);
    }

    public IDataset getTradeRes()
    {

        return getMySet(TradeTableEnum.TRADE_RES);
    }

    public IDataset getTradeSaleActive()
    {

        return getMySet(TradeTableEnum.TRADE_SALEACTIVE);
    }

    public IDataset getTradeSaleDeposit()
    {

        return getMySet(TradeTableEnum.TRADE_SALEDEPOSIT);
    }

    public IDataset getTradeSaleGoods()
    {

        return getMySet(TradeTableEnum.TRADE_SALEGOODS);
    }

    public IDataset getTradeScore()
    {

        return getMySet(TradeTableEnum.TRADE_SCORE);
    }

    public IDataset getTradeScoreRelation()
    {

        return getMySet(TradeTableEnum.TRADE_SCORERELATION);
    }

    public IDataset getTradeSimcardcompfee()
    {

        return getMySet(TradeTableEnum.TRADE_SIMCARDCOMPFEE);
    }

    public IDataset getTradeSms()
    {

        return getMySet(TradeTableEnum.TRADE_SMS);
    }

    public IDataset getTradeStaff() throws Exception
    {

        return getMySet(TradeTableEnum.TRADE_BHSTAFF);
    }

    public IDataset getTradeSvc()
    {

        return getMySet(TradeTableEnum.TRADE_SVC);
    }

    public IDataset getTradeSvcstate()
    {

        return getMySet(TradeTableEnum.TRADE_SVCSTATE);
    }

    public IDataset getTradeSysCode()
    {

        return getMySet(TradeTableEnum.TRADE_SYSCODE);
    }

    public IDataset getTradeTaxLog()
    {

        return getMySet(TradeTableEnum.TRADE_TAXLOG);
    }

    public IDataset getTradeUser()
    {

        return getMySet(TradeTableEnum.TRADE_USER);
    }

    public IDataset getTradeUserAcctday()
    {

        return getMySet(TradeTableEnum.TRADE_USER_ACCTDAY);
    }

    public IDataset getTradeUserPayitem()
    {

        return getMySet(TradeTableEnum.TRADE_USER_PAYITEM);
    }

    public IDataset getTradeUserPayplan()
    {

        return getMySet(TradeTableEnum.TRADE_USER_PAYPLAN);
    }

    public IDataset getTradeUserSpecialepay()
    {

        return getMySet(TradeTableEnum.TRADE_USER_SPECIALEPAY);
    }

    public IDataset getTradeVip()
    {

        return getMySet(TradeTableEnum.TRADE_VIP);
    }

    public IDataset getTradeVpn()
    {

        return getMySet(TradeTableEnum.TRADE_VPN);
    }

    public IDataset getTradeVpnMeb()
    {

        return getMySet(TradeTableEnum.TRADE_VPN_MEB);
    }

    public IDataset getTradeWidenet()
    {

        return getMySet(TradeTableEnum.TRADE_WIDENET);
    }

    public IDataset getTradeWidenetAct()
    {

        return getMySet(TradeTableEnum.TRADE_WIDENET_ACCT);
    }

    public IDataset getTwocheckSms()
    {

        return getMySet(TradeTableEnum.TRADE_TWOCHECK_SMS);
    }
    
    public IDataset getTradeEpibolicRela()
    {

        return getMySet(TradeTableEnum.TRADE_EPIBOLICRELA);
    }
    
    public IDataset getTradeDataPckInStock()
    {

        return getMySet(TradeTableEnum.TRADE_DATAPCK_INSTOCK);
    }
    
    public IDataset getTradeDataPckOutStock()
    {

        return getMySet(TradeTableEnum.TRADE_DATAPCK_OUTSTOCK);
    }
    
    public IDataset getTradeAcctDiscnt()
    {

        return getMySet(TradeTableEnum.TRADE_ACCT_DISCNT);
    }
    
    public IDataset getTradeDataPck()
    {

        return getMySet(TradeTableEnum.TRADE_DATAPCK);
    }
    
    public IDataset getTradeOfferRel()
    {

        return getMySet(TradeTableEnum.TRADE_OFFER_REL);
    }
    
    public IDataset getTradePrice()
    {

        return getMySet(TradeTableEnum.TRADE_PRICE);
    }
    
    public IDataset getTradePricePlan()
    {

        return getMySet(TradeTableEnum.TRADE_PRICE_PLAN);
    }

    public void logToFile(StringBuilder logFile) throws Exception
    {
        Obj2Xml.toFile(LogBaseBean.LOG_PATH, logFile.toString(), bd);
    }
    
    public IDataset getTradeEcrecepOffer()
    {
        return getMySet(TradeTableEnum.TRADE_ECRECEP_OFFER);
    }

    public IDataset getTradeEcrecepProcedure()
    {
        return getMySet(TradeTableEnum.TRADE_ECRECEP_PROCEDURE);
    }

    public IDataset getTradeEcrecepProduct()
    {
        return getMySet(TradeTableEnum.TRADE_ECRECEP_PRODUCT);
    }

    public IDataset getTradeEcrecepMem() {
        return getMySet(TradeTableEnum.TRADE_ECRECEP_MEM);
    }
  
}
