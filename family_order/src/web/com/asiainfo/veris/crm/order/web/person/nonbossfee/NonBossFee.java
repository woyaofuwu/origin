
package com.asiainfo.veris.crm.order.web.person.nonbossfee;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class NonBossFee extends PersonBasePage
{
	
	/**
	 * 返销
	 * 
    public void cancelNonBossFeeLog(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        data.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
        IDataset dataset = CSViewCall.call(this, "SS.NonBossFeeSVC.cancelNonBossFeeLog", data);
        setAjax(dataset.getData(0));
    }
    */
  
    
    /**
     * 查询费用列表
     * 
     * @param cycle
     * @throws Exception
     */
    public void onInitTrade(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        
        IDataset feeItems = CSViewCall.call(this, "SS.NonBossFeeSVC.queryNonBossFeeItems", data);
        setFeeItems(feeItems);
        /*
         * REQ201409250007201409非出账业务收款及发票管理需求
         * 获取“非出账业务参数配置”下拉列表内容
         * chenxy3 2015-2-13
         */
        IDataset companyname = CSViewCall.call(this, "SS.NonBossFeeUserItemMgrSVC.getCompanyName", data);
        setCompname(companyname);
        
        data.put("LIST_TYPE", "fee");
        IDataset feetype = CSViewCall.call(this, "SS.NonBossFeeItemMgrSVC.getListType", data);
        setFeetype(feetype);
        
        data.put("LIST_TYPE", "invoice");
        IDataset invoicetype = CSViewCall.call(this, "SS.NonBossFeeItemMgrSVC.getListType", data);
        setInvoicetype(invoicetype);
        
        data.put("LIST_TYPE", "tax");
        IDataset taxtype = CSViewCall.call(this, "SS.NonBossFeeItemMgrSVC.getListType", data);
        setTaxtype(taxtype); 
    }

    /**
     * 业务提交
     * 
     * @param cycle
     * @throws Exception
     */
    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {

        IData data = getData();
        data.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
        String feeInfo = data.getString("FEE_INFO", "[]");
        data.put("FEE_LIST", new DatasetList(feeInfo));
        data.remove("FEE_INFO");
        IDataset dataset = CSViewCall.call(this, "SS.NonBossFeeSVC.insertNonBossFee", data);
        setAjax(dataset.getData(0));
    }

    /**
     * 打印发票
     * 
     * @param cycle
     * @throws Exception
     */
    public void printNonBossFeeTrade(IRequestCycle cycle) throws Exception
    {

        IData data = getData();
        data.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
        String feeInfo = data.getString("FEE_INFO", "[]");
        data.put("FEE_LIST", new DatasetList(feeInfo));
        data.remove("FEE_INFO");
        IDataset dataset = CSViewCall.call(this, "SS.NonBossFeeSVC.printNonBossFeeTrade", data);
        setAjax(dataset);

    }

    public void queryNonBossFeeLog(IRequestCycle cycle) throws Exception
    {
        IData data = getData("cond", true);
        Pagination page = getPagination("recordNav");
        data.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());

        IDataOutput result = CSViewCall.callPage(this, "SS.NonBossFeeSVC.queryNonBossFeeLog", data, page);
        setRecordCount(result.getDataCount());
        setInfos(result.getData());
    }

    /**
     * 查询用户全称
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryPayNameRemark(IRequestCycle cycle) throws Exception
    {

        IData data = getData();
        IDataset payNames = CSViewCall.call(this, "SS.NonBossFeeSVC.queryPayNameRemark", data);
        this.setInfos(payNames);
    }

    /**
     * 非BOSS收款补录补打
     * 
     * @param cycle
     * @throws Exception
     */
    public void rePrintNonBossFee(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        data.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
        IDataset dataset = CSViewCall.call(this, "SS.NonBossFeeSVC.rePrintNonBossFee", data);
        setAjax(dataset);
    }
    
    /**
     * REQ201409250007201409非出账业务收款及发票管理需求
     * 获取用户全称及用户类型的对应关系
     * chenxy3 2015-2-13
     * */
    public void getPayUserName(IRequestCycle cycle) throws Exception
    {
    	IData conditions = this.getData(); 
        IDataset userInfos = CSViewCall.call(this, "SS.NonBossFeeUserItemMgrSVC.getNonBossFeeUserItems", conditions);
        String userType="";
        if(userInfos!=null&&userInfos.size()>0){
        	userType=userInfos.getData(0).getString("PARAM_NAME");
        }
        IData id=new DataMap();
        id.put("PARAM_NAME", userType);
        setAjax(id);
    }
    
    /**
     * REQ201409250007201409非出账业务收款及发票管理需求
     * 获取用户全称及用户类型的对应关系
     * chenxy3 2015-2-13
     * */
    public void getFeeTypeRelation(IRequestCycle cycle) throws Exception
    {
    	IData conditions = this.getData(); 
    	IDataset feeItems = CSViewCall.call(this, "SS.NonBossFeeItemMgrSVC.getNonBossFeeItems", conditions);
        String TAX_TYPE="";
        String TAX_RATE="";
        String INVOICE_TYPE="";
        String REMARK="";
        if(feeItems!=null&&feeItems.size()>0){
        	TAX_TYPE=feeItems.getData(0).getString("PARA_CODE1");
        	TAX_RATE=feeItems.getData(0).getString("PARA_CODE2");
        	INVOICE_TYPE=feeItems.getData(0).getString("PARA_CODE3");
        	REMARK=feeItems.getData(0).getString("REMARK");
        }
        IData id=new DataMap();
        id.put("TAX_TYPE", TAX_TYPE);
        id.put("TAX_RATE", TAX_RATE);
        id.put("INVOICE_TYPE", INVOICE_TYPE);
        id.put("REMARK", REMARK);
        setAjax(id);
    }

    public abstract void setCond(IData cond);

    public abstract void setFee(IData fee);

    public abstract void setFeeItems(IDataset feeItems);

    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);

    public abstract void setRecordCount(long recordCount);
    
    /**
     * REQ201409250007201409非出账业务收款及发票管理需求
     */
    public abstract void setCompname(IDataset compname);
    public abstract void setFeetype(IDataset feetype);
    public abstract void setInvoicetype(IDataset invoicetype);
    public abstract void setTaxtype(IDataset invoicetype);
}
