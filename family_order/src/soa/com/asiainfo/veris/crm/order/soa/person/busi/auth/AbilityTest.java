package com.asiainfo.veris.crm.order.soa.person.busi.auth;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class AbilityTest extends CSBizService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final Logger log = Logger.getLogger(AbilityTest.class);

	public IDataset test(IData data) throws Exception{
        IData test = new DataMap();
        test.put("CHANNEL_ID", "320");
        test.put("ORDER_ID", "01");
        test.put("EXT_ORDER_ID", "01");
        test.put("CREATE_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
        test.put("BUYER_NICK_NAME", "XXX");
        test.put("BUYER_EMAIL", "XXX@XX.COM");
        test.put("BUYER_MESSAGE", "xxxXXXxxxXXXxxxXXXxxxXXX");
        test.put("TRADE_MEMO", "iphoneXXX Plus");
        test.put("SELLER_MEMO", "记得给好评哦,亲!");
        test.put("SHOP_CODE", "0001");
        test.put("SHOP_NAME", "七号当铺");
        test.put("SELLER_ID", "XXXXXX");
        //IDataset paymentInfo = new DatasetList();
        IData payment = new DataMap();
        payment.put("CHARGE_TYPE", "0");
        payment.put("TOTAL_FEE", 500000);
        payment.put("PAYMENT", 500000);
        payment.put("PAYMENT_TYPE", "ALIPAY-WAP");
        payment.put("PAYMENT_ORDERID", "201709260001");
        payment.put("PAYMENT_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
        payment.put("PAYMENT_ORGANIZATION", "xxxxxxx");
        payment.put("PATMENT_SERVICEFEE", 100);
        payment.put("SERVICE_FEE_REFUNDABLE", "1");
        payment.put("PAYMENT_SERVICE_FEE", "1");
        payment.put("IS_SERVICE_FEE_REFUNDABLE", "1");
        test.put("PAYMENT_INFO", payment);
        //paymentInfo.add(payment);
        //test.put("PAYMENT_INFO", paymentInfo);
        test.put("NEED_DISTRIBUTION", "1");
        
        //IDataset consinfo = new DatasetList();
        IData cons = new DataMap();
        cons.put("NAME", "小明");
        cons.put("COUNTRY", "Z国");
        cons.put("PROVINCE", "XXX省");
        cons.put("CITY", "XX市");
        cons.put("DISTRICT", "XX区XXX街道XXX号");
        cons.put("ADDRESS", "XXXXXXXX");
        cons.put("POSTCODE", "225300");
        cons.put("MOBILEPHONE", "10000000000");
        cons.put("LAND_LINE", "0731-81234567");
        test.put("CONSIGNEE_INFO", cons);
        //consinfo.add(cons);
        //test.put("CONSIGNEE_INFO", consinfo);
        
        test.put("NEED_INVOICE", "1");
        //IDataset invoiceInfo = new DatasetList();
        IData invoice = new DataMap();
        invoice.put("INVOICE_TYPE", "1");
        invoice.put("INVOICE_TITLE", "个人");
        invoice.put("INVOICE_MEMO", ".......");
        test.put("INVOICE_INFO", invoice);
        //invoiceInfo.add(invoice);
        //test.put("INVOICE_INFO", invoiceInfo);
        
        IDataset suborder_list = new DatasetList();
        IData suborder = new DataMap();
        suborder.put("SUB_ORDER_ID", "01");
        suborder.put("SUB_EXT_ORDER_ID", "01");
        suborder.put("ORDER_ID", "01");
        
        //IDataset subscribe = new DatasetList();
        IData suborder_trade = new DataMap();
        suborder_trade.put("NUMBER_OPR_TYPE", "01");
        suborder_trade.put("SERVICE_NO", "10000000000");
        suborder_trade.put("SERVICE_NO_TYPE", "01");
        suborder_trade.put("NUMBER_BRAND", "1");
        suborder_trade.put("QUANTITY", 10);
        suborder_trade.put("NUMBER_PRICE", 200000);
        suborder_trade.put("SIM_PRICE", 100000);
        suborder_trade.put("LEGAL_NAME", "XXX");
        suborder_trade.put("CERTIFICATE_TYPE", "XX");
        suborder_trade.put("CERTIFICATE_NO", "000000000000000000");
        
        //IDataset goodsInfo = new DatasetList();
        IData goods = new DataMap();
        goods.put("GOODS_ID", "03");
        goods.put("GOODS_TITLE", "不知道是啥东西");
        goods.put("AMOUNT", 10);
        goods.put("PRICE", 100000);
        goods.put("GOODS_PROVINCE", "XX省");
        goods.put("GOODS_CITY", "XX市");
        
        
        IDataset productList = new DatasetList();
        IData product = new DataMap();
        product.put("PRODUCT_ID", "5024");
        product.put("PRODUCT_TYPE", "001");
        product.put("SERVICEID_LIST", "SSSS;SSS;XXX;QQQQ;AA");
        //goods.put("PRODUCT_LIST", product);
        productList.add(product);
        suborder.put("PRODUCT_LIST", productList);
        //goodsInfo.add(goods);
        
//        IDataset additional = new DatasetList();
        IData addition = new DataMap();
        addition.put("ACTIVITY_CODE", "999020010000016");
        addition.put("ACTIVITY_TYPE", "10100");
        addition.put("ACTIVITY_NAME", "test");
        addition.put("ADJUST_REASON", "test");
        //additional.add(addition);
        suborder.put("ACTIVITY_INFO", addition);
        
        //suborder.put("GOODS_INFO", goodsInfo);
        suborder.put("GOODS_INFO", goods);
        suborder.put("SUB_SCRIBER_INFO", suborder_trade);
        //subscribe.add(suborder_trade);
        //suborder.put("SUB_SCRIBER_INFO", subscribe);
        suborder.put("SUBTOTAL_FEE", 500000);
        suborder.put("ADJUST_FEE", 0);
        suborder.put("ORDER_STATUS", "AC");
        suborder_list.add(suborder);
        test.put("SUB_ORDER_LIST", suborder_list);
        
//        String result = test.toString();
        return CSAppCall.call("SS.AbilityPlatOrderSVC.synOrderInfo", test);
    }
}
