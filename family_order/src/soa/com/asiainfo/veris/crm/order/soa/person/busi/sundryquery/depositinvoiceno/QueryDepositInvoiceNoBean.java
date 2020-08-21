
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.depositinvoiceno;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.QueryDepositInvoiceNoQry;

public class QueryDepositInvoiceNoBean extends CSBizBean
{
    public IDataset queryUserCustByInvoiceNo(IData data, Pagination page) throws Exception
    {

        String routeEparchyCode = this.getTradeEparchyCode();// BizRoute.getRouteId();
        String invoiceNo = data.getString("INVOICE_NO", "");
        IDataset dataSet = QueryDepositInvoiceNoQry.queryUserCustByInvoiceNo(invoiceNo, routeEparchyCode, page);
        return dataSet;
    }

    public IDataset queryUserCustBySerial(IData data, Pagination page) throws Exception
    {

        String routeEparchyCode = this.getTradeEparchyCode();// BizRoute.getRouteId();
        String serialNumber = data.getString("SERIAL_NUMBER", "");
        /**
         * REQ201512020036 用户押金发票号码查询界面优化
         * chenxy3 20151221
         * 查过户、销户的
         * */
        IDataset dataSet = new DatasetList();
        String userSnType=data.getString("USER_SN_TYPE","");
        if("1".equals(userSnType)){
        	dataSet = QueryDepositInvoiceNoQry.queryUserCustBySerial1(serialNumber, routeEparchyCode, page);
        	if(dataSet==null || dataSet.size()==0){
        		dataSet = QueryDepositInvoiceNoQry.queryUserCustBySerial11(serialNumber, routeEparchyCode, page);//销户历史
        	}

        }else{
        	dataSet = QueryDepositInvoiceNoQry.queryUserCustBySerial(serialNumber, routeEparchyCode, page);
        }
        return dataSet;
    }

    public IDataset queryUserOtherByInvoiceNo(IData data, Pagination page) throws Exception
    {

        String routeEparchyCode = this.getTradeEparchyCode();// BizRoute.getRouteId();
        String invoiceNo = data.getString("INVOICE_NO", "");
        IDataset dataSet = QueryDepositInvoiceNoQry.queryUserOtherByInvoiceNo(invoiceNo, routeEparchyCode, page);
        return dataSet;
    }

    /**
     * 功能： 作者：GongGuang
     */
    public IDataset queryUserOtherByUser(IData data, Pagination page) throws Exception
    {

        String routeEparchyCode = this.getTradeEparchyCode();// BizRoute.getRouteId();
        String serialNumber = data.getString("SERIAL_NUMBER", "");
        
        /**
         * REQ201512020036 用户押金发票号码查询界面优化
         * chenxy3 20151221
         * 查过户、销户的
         * */
        String userSnType=data.getString("USER_SN_TYPE","");
        IDataset dataSet = new DatasetList();
        if("1".equals(userSnType)){
        	dataSet = QueryDepositInvoiceNoQry.queryUserOtherByUser1(serialNumber, routeEparchyCode, page);//销户
        	if(dataSet==null || dataSet.size()==0){
        		dataSet = QueryDepositInvoiceNoQry.queryUserOtherByUser11(serialNumber, routeEparchyCode, page);//销户历史
        		if(dataSet==null || dataSet.size()==0){
        			dataSet = QueryDepositInvoiceNoQry.queryUserOtherByUser21(serialNumber, routeEparchyCode, page);//过户后再销户，导致USER_ID变了，到了RSRV_STR4
        		}
        	}
        }else if("2".equals(userSnType)){
        	dataSet = QueryDepositInvoiceNoQry.queryUserOtherByUser2(serialNumber, routeEparchyCode, page);//过户
        }else{
        	dataSet = QueryDepositInvoiceNoQry.queryUserOtherByUser(serialNumber, routeEparchyCode, page);//正常
        }
        
        return dataSet;
    }
}
