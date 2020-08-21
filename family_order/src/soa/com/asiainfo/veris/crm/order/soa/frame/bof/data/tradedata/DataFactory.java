
package com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata;

import java.util.ArrayList;
import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AccountAcctDayTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AccountTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AcctConsignTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AttrTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.BankMainSignTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.BankSubSignTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.BlackWhiteTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CNoteInfoTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CreditTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustFamilyMebTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustFamilyTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustGroupTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustPersonTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustomerTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DevelopTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DeviceTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ElementTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ExtTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.FeeTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.GiftFeeTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.GrpCenpayTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.GrpMemPlatSvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.GrpMerchMebTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ImpuTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MebCenpayTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.NetNpTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OfferRelTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherFeeTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PayMoneyTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PayRelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PlatSvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PostTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ProductTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.RelaTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.RelationBBTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.RelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.RelationxxtTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.RentTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ResTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SaleActiveTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SaleDepositTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SaleGoodsTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ScoreTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SimCardComPFeeTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcStateTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.TelephoneTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserAcctDayTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserPayItemTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserPayPlanTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserSpecialePayTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.VipTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.VpnMemTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.WideNetActTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.WideNetTradeData;

public class DataFactory<K extends BaseTradeData>
{
    private static DataFactory factory = new DataFactory();

    public static DataFactory getInstance()
    {
        return factory;
    }

    public DataFactory()
    {

    }

    public K getData(String tableName, IData param) throws Exception
    {
        if ("TF_B_TRADE_ACCOUNT_ACCTDAY".equals(tableName))
        {
            AccountAcctDayTradeData data = new AccountAcctDayTradeData(param);
            return (K) data;
        }
        else if ("TF_B_TRADE_ACCOUNT".equals(tableName))
        {
            AccountTradeData data = new AccountTradeData(param);
            return (K) data;
        }
        else if ("TF_B_TRADE_ACCT_CONSIGN".equals(tableName))
        {
            AcctConsignTradeData data = new AcctConsignTradeData(param);
            return (K) data;
        }
        else if ("TF_B_TRADE_ATTR".equals(tableName))
        {
            AttrTradeData data = new AttrTradeData(param);
            return (K) data;
        }
        else if ("TF_B_TRADE_BLACKWHITE".equals(tableName))
        {
            BlackWhiteTradeData data = new BlackWhiteTradeData(param);
            return (K) data;
        }
        else if ("TF_B_TRADE_CNOTE_INFO".equals(tableName))
        {
            CNoteInfoTradeData data = new CNoteInfoTradeData(param);
            return (K) data;
        }
        else if ("TF_B_TRADE_CREDIT".equals(tableName))
        {
            CreditTradeData data = new CreditTradeData(param);
            return (K) data;
        }
        else if ("TF_B_TRADE_CUST_FAMILYMEB".equals(tableName))
        {
            CustFamilyMebTradeData data = new CustFamilyMebTradeData(param);
            return (K) data;
        }
        else if ("TF_B_TRADE_CUST_FAMILY".equals(tableName))
        {
            CustFamilyTradeData data = new CustFamilyTradeData(param);
            return (K) data;
        }
        else if ("TF_B_TRADE_CUST_GROUP".equals(tableName))
        {
            CustGroupTradeData data = new CustGroupTradeData(param);
            return (K) data;
        }
        else if ("TF_B_TRADE_CUSTOMER".equals(tableName))
        {
            CustomerTradeData data = new CustomerTradeData(param);
            return (K) data;
        }
        else if ("TF_B_TRADE_CUST_PERSON".equals(tableName))
        {
            CustPersonTradeData data = new CustPersonTradeData(param);
            return (K) data;
        }
        else if ("TF_B_TRADE_DEVELOP".equals(tableName))
        {
            DevelopTradeData data = new DevelopTradeData(param);
            return (K) data;
        }
        else if ("TF_B_TRADEFEE_DEVICE".equals(tableName))
        {
            DeviceTradeData data = new DeviceTradeData(param);
            return (K) data;
        }
        else if ("TF_B_TRADE_DISCNT".equals(tableName))
        {
            DiscntTradeData data = new DiscntTradeData(param);
            return (K) data;
        }
        else if ("TF_B_TRADE_ELEMENT".equals(tableName))
        {
            ElementTradeData data = new ElementTradeData(param);
            return (K) data;
        }
        else if ("TF_B_TRADE_EXT".equals(tableName))
        {
            ExtTradeData data = new ExtTradeData(param);
            return (K) data;
        }
        else if ("TF_B_TRADEFEE_SUB".equals(tableName))
        {
            FeeTradeData data = new FeeTradeData(param);
            return (K) data;
        }
        else if ("TF_B_TRADEFEE_GIFTFEE".equals(tableName))
        {
            GiftFeeTradeData data = new GiftFeeTradeData(param);
            return (K) data;
        }
        else if ("TF_B_TRADE_GRP_MEB_PLATSVC".equals(tableName))
        {
            GrpMemPlatSvcTradeData data = new GrpMemPlatSvcTradeData(param);
            return (K) data;
        }
        else if ("TF_B_TRADE_IMPU".equals(tableName))
        {
            ImpuTradeData data = new ImpuTradeData(param);
            return (K) data;
        }
        else if ("TF_B_TRADE".equals(tableName) || "TF_BH_TRADE".equals(tableName))
        {
            MainTradeData data = new MainTradeData(param);
            return (K) data;
        }
        else if ("TF_B_TRADE_NETNP".equals(tableName))
        {
            NetNpTradeData data = new NetNpTradeData(param);
            return (K) data;
        }
        else if ("TF_B_TRADEFEE_OTHERFEE".equals(tableName))
        {
            OtherFeeTradeData data = new OtherFeeTradeData(param);
            return (K) data;
        }
        else if ("TF_B_TRADE_OTHER".equals(tableName))
        {
            OtherTradeData data = new OtherTradeData(param);
            return (K) data;
        }
        else if ("TF_B_TRADEFEE_PAYMONEY".equals(tableName))
        {
            PayMoneyTradeData data = new PayMoneyTradeData(param);
            return (K) data;
        }
        else if ("TF_B_TRADE_PAYRELATION".equals(tableName))
        {
            PayRelationTradeData data = new PayRelationTradeData(param);
            return (K) data;
        }
        else if ("TF_B_TRADE_PLATSVC".equals(tableName))
        {
            PlatSvcTradeData data = new PlatSvcTradeData(param);
            return (K) data;
        }
        else if ("TF_B_TRADE_POST".equals(tableName))
        {
            PostTradeData data = new PostTradeData(param);
            return (K) data;
        }
        else if ("TF_B_TRADE_PRODUCT".equals(tableName))
        {
            ProductTradeData data = new ProductTradeData(param);
            return (K) data;
        }
        else if ("TF_B_TRADE_RELATION_XXT".equals(tableName))
        {
            RelationxxtTradeData data = new RelationxxtTradeData(param);
            return (K) data;
        }
        else if ("TF_B_TRADE_RELATION".equals(tableName))
        {
            RelationTradeData data = new RelationTradeData(param);
            return (K) data;
        }
        else if ("TF_B_TRADE_RELA".equals(tableName))
        {
            RelaTradeData data = new RelaTradeData(param);
            return (K) data;
        }
        else if ("TF_B_TRADE_RENT".equals(tableName))
        {
            RentTradeData data = new RentTradeData(param);
            return (K) data;
        }
        else if ("TF_B_TRADE_RES".equals(tableName))
        {
            ResTradeData data = new ResTradeData(param);
            return (K) data;
        }
        else if ("TF_B_TRADE_SALE_ACTIVE".equals(tableName))
        {
            SaleActiveTradeData data = new SaleActiveTradeData(param);
            return (K) data;
        }
        else if ("TF_B_TRADE_SALE_DEPOSIT".equals(tableName))
        {
            SaleDepositTradeData data = new SaleDepositTradeData(param);
            return (K) data;
        }
        else if ("TF_B_TRADE_SALE_GOODS".equals(tableName))
        {
            SaleGoodsTradeData data = new SaleGoodsTradeData(param);
            return (K) data;
        }
        else if ("TF_B_TRADE_SCORE".equals(tableName))
        {
            ScoreTradeData data = new ScoreTradeData(param);
            return (K) data;
        }
        else if ("TF_B_TRADE_SIMCARDCOMPFEE".equals(tableName))
        {
            SimCardComPFeeTradeData data = new SimCardComPFeeTradeData(param);
            return (K) data;
        }
        else if ("TF_B_TRADE_SVCSTATE".equals(tableName))
        {
            SvcStateTradeData data = new SvcStateTradeData(param);
            return (K) data;
        }
        else if ("TF_B_TRADE_SVC".equals(tableName))
        {
            SvcTradeData data = new SvcTradeData(param);
            return (K) data;
        }
        else if ("TF_B_TRADE_USER_ACCTDAY".equals(tableName))
        {
            UserAcctDayTradeData data = new UserAcctDayTradeData(param);
            return (K) data;
        }
        else if ("TF_B_TRADE_USER_PAYITEM".equals(tableName))
        {
            UserPayItemTradeData data = new UserPayItemTradeData(param);
            return (K) data;
        }
        else if ("TF_B_TRADE_USER_PAYPLAN".equals(tableName))
        {
            UserPayPlanTradeData data = new UserPayPlanTradeData(param);
            return (K) data;
        }
        else if ("TF_B_TRADE_USER".equals(tableName))
        {
            UserTradeData data = new UserTradeData(param);
            return (K) data;
        }
        else if ("TF_B_TRADE_VIP".equals(tableName))
        {
            VipTradeData data = new VipTradeData(param);
            return (K) data;
        }
        else if ("TF_B_TRADE_VPN_MEB".equals(tableName))
        {
            VpnMemTradeData data = new VpnMemTradeData(param);
            return (K) data;
        }
        else if ("TF_B_TRADE_WIDENET_ACT".equals(tableName))
        {
            WideNetActTradeData data = new WideNetActTradeData(param);
            return (K) data;
        }
        else if ("TF_B_TRADE_WIDENET".equals(tableName))
        {
            WideNetTradeData data = new WideNetTradeData(param);
            return (K) data;
        }
        else if ("TF_B_TRADE_GRP_MERCH_MEB".equals(tableName))
        {
            GrpMerchMebTradeData data = new GrpMerchMebTradeData(param);
            return (K) data;
        }
        else if ("TF_B_TRADE_RELATION_BB".equals(tableName))
        {
            RelationBBTradeData data = new RelationBBTradeData(param);
            return (K) data;
        }
        else if ("TF_B_TRADE_RELATION_XXT".equals(tableName))
        {
        	RelationxxtTradeData data = new RelationxxtTradeData(param);
            return (K) data;
        }
        else if ("TF_B_TRADE_BANK_SUBSIGN".equals(tableName))
        {
            BankSubSignTradeData data = new BankSubSignTradeData(param);
            return (K) data;
        }
        else if ("TF_B_TRADE_BANK_MAINSIGN".equals(tableName))
        {
            BankMainSignTradeData data = new BankMainSignTradeData(param);
            return (K) data;
        }
        else if ("TF_B_TRADE_TELEPHONE".equals(tableName))
        {
            TelephoneTradeData data = new TelephoneTradeData(param);
            return (K) data;
        }
        else if ("TF_B_TRADE_GRP_CENPAY".equals(tableName))
        {
            GrpCenpayTradeData data = new GrpCenpayTradeData(param);
            return (K) data;
        }
        else if ("TF_B_TRADE_MEB_CENPAY".equals(tableName))
        {
            MebCenpayTradeData data = new MebCenpayTradeData(param);
            return (K) data;
        }
        else if ("TF_B_TRADE_USER_SPECIALEPAY".equals(tableName))
        {
        	UserSpecialePayTradeData data = new UserSpecialePayTradeData(param);
            return (K) data;
        }
        else if ("TF_B_TRADE_OFFER_REL".equals(tableName))
        {
            OfferRelTradeData data = new OfferRelTradeData(param);
            return (K) data;
        }
        else
        {
            return null;
        }
    }

    public List<K> getData(String tableName, IDataset datas) throws Exception
    {
        if (IDataUtil.isNotEmpty(datas))
        {
            int size = datas.size();
            List<K> result = new ArrayList<K>();
            for (int i = 0; i < size; i++)
            {
                result.add(this.getData(tableName, datas.getData(i)));
            }
            return result;
        }
        else
        {
            return null;
        }
    }
}
