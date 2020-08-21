package com.asiainfo.veris.crm.iorder.web.igroup.minorec.phonepay;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.TreeItem;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;
import com.wade.web.v5.tapestry.component.tree.TreeFactory;
import com.wade.web.v5.tapestry.component.tree.TreeParam;
import org.apache.tapestry.IRequestCycle;

import java.util.Map;

public abstract class PhoneQRCodePay extends GroupBasePage {

    public void queryInfos(IRequestCycle cycle) throws Exception{

        IData input = new DataMap();
        input.put("GROUP_ID",getData().getString("GROUP_ID"));
        input.put("PRODUCT_ID",getData().getString("PRODUCT_ID"));
        input.put("SERIAL_NUMBER",getData().getString("SERIAL_NUMBER"));
        input.put("ACCT_ID",getData().getString("ACCT_ID"));

        IDataOutput output = CSViewCall.callPage(this,"SS.PhoneQRCodePaySVC.queryInfos",input,getPagination("navbar1"));
        setInfos(output.getData());
        setInfoCount(output.getDataCount());

    }

    public void queryOrderInfos(IRequestCycle cycle) throws Exception{
        IData input = new DataMap();
        input.put("GROUP_ID",getData().getString("GROUP_ID"));
        input.put("PRODUCT_ID",getData().getString("PRODUCT_ID"));
        input.put("SERIAL_NUMBER",getData().getString("SERIAL_NUMBER"));
        input.put("ACCT_ID",getData().getString("ACCT_ID"));

        IDataset output = CSViewCall.call(this,"SS.PhoneQRCodePaySVC.queryOrderInfos",input);
        setOrderInfos(output);

    }

    public void queryCustInfoByName(IRequestCycle cycle) throws Exception{
        IData input = new DataMap();
        String custName = getData().getString("CUST_NAME");
        if(StringUtils.isBlank(custName)){
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "未获取到输入的集团名称！");
        }
        input.put("CUST_NAME",custName);

        IDataset output = CSViewCall.call(this,"SS.PhoneQRCodePaySVC.queryCustInfoByName",input);

        setCustInfos(output);

    }

    public void submitPay(IRequestCycle cycle) throws Exception{
        IData data =getData();
        //转换为分
        double money = data.getDouble("PAY_NUM");
        int changeMoney = (int)(money*100);
        data.put("PAY_NUM",changeMoney);
        //data.put(Route.ROUTE_EPARCHY_CODE, getVisit().getLoginEparchyCode());
        IDataset result = CSViewCall.call(this,"SS.PhoneQRCodePaySVC.tradeReg",data);

        if(DataUtils.isNotEmpty(result)){
            IDataset goodLists = new DatasetList();
            IData temp =new DataMap();
            temp.put("PEER_TRADE_ID", result.first().getString("TRADE_ID"));
            temp.put("GOODS_NAME",  result.first().getString("TRADE_NAME","业务费用"));
            temp.put("GOODS_PRICE", data.getString("PAY_NUM"));
            temp.put("GOODS_NUM", "1");
            temp.put("TOTAL_MONEY", data.getString("PAY_NUM"));
            goodLists.add(temp);

            IData input = new DataMap();
            input.put("PEER_ORDER_ID", result.first().getString("ORDER_ID"));
            input.put("ORDER_FEE", data.getString("PAY_NUM"));
            String sn = data.getString("SERIAL_NUMBER");
            if("undefined".equals(sn) || StringUtils.isBlank(sn))
            {
                sn = "无号码";
            }
            input.put("SERIAL_NUMBER", sn);
            input.put("ORDER_DESC","订单总费用");
            input.put("MERCHANT_ID", "10001");
            //input.put("PLAT_TYPE",data.getString("PAY_TYPE"));
            input.put("GOODS_LIST", goodLists.toString());
            IDataset dataset = CSViewCall.call(this, "payment.order.IPayAccessSV.createPayDetail", input);
            if(DataUtils.isNotEmpty(dataset)){
                IData ajaxData = dataset.first();
                ajaxData.put("TRADE_ID",result.first().getString("TRADE_ID"));
                ajaxData.put("SERIAL_NUMBER",data.getString("SERIAL_NUMBER"));
                ajaxData.put("TRADE_EPARCHY_CODE",getVisit().getLoginEparchyCode());
                this.setAjax(ajaxData);
            }
        }

    }

    public void doPayThisTrade(IRequestCycle cycle) throws Exception{
        IData data = getData();

        //转换为分
        double money = data.getDouble("FEE");
        int changeMoney = (int)(money*100);
        data.put("FEE",changeMoney);

        IDataset goodLists = new DatasetList();
        IData temp =new DataMap();
        temp.put("PEER_TRADE_ID", data.getString("TRADE_ID"));
        temp.put("GOODS_NAME",  data.getString("TRADE_NAME","业务费用"));
        temp.put("GOODS_PRICE", data.getString("FEE"));
        temp.put("GOODS_NUM", "1");
        temp.put("TOTAL_MONEY", data.getString("FEE"));
        goodLists.add(temp);

        IData input = new DataMap();
        input.put("PEER_ORDER_ID", data.getString("ORDER_ID"));
        input.put("ORDER_FEE", data.getString("FEE"));
        String sn = data.getString("SERIAL_NUMBER");
        if("undefined".equals(sn) || StringUtils.isBlank(sn))
        {
            sn = "无号码";
        }
        input.put("SERIAL_NUMBER", sn);
        input.put("ORDER_DESC","订单总费用");
        input.put("MERCHANT_ID", "10001");
        //input.put("PLAT_TYPE",data.getString("PAY_TYPE"));
        input.put("GOODS_LIST", goodLists.toString());
        IDataset dataset = CSViewCall.call(this, "payment.order.IPayAccessSV.createPayDetail", input);
        if(DataUtils.isNotEmpty(dataset)){
            IData ajaxData = dataset.first();
            ajaxData.put("TRADE_ID",data.getString("TRADE_ID"));
            ajaxData.put("SERIAL_NUMBER",data.getString("SERIAL_NUMBER"));
            ajaxData.put("TRADE_EPARCHY_CODE",getVisit().getLoginEparchyCode());
            this.setAjax(ajaxData);
        }
    }

    public void payTrade(IRequestCycle cycle)throws Exception
    {
        IData data = this.getData();
        data.put("EPARCHY_CODE", data.getString("EPARCHY_CODE", getTradeEparchyCode()));
        IDataset infos = CSViewCall.call(this, "SS.PhoneQRCodePaySVC.payTrade", data);
        IData info = new DataMap();
        if (null != infos && infos.size() > 0)
        {
            info = infos.getData(0);
        }
        this.setAjax(info);

    }

    public void cancelTrade(IRequestCycle cycle) throws Exception{
        IData data = getData();
        IData param = new DataMap();
        param.put("TRADE_ID",data.getString("TRADE_ID"));
        param.put("ORDER_ID",data.getString("ORDER_ID"));
        IData result = CSViewCall.callone(this,"SS.PhoneQRCodePaySVC.cancelTrade",param);
        setAjax(result);
    }

    /**
     * 作用：仓库树加载方法
     * @param cycle
     * @throws Exception
     */
    public void loadProductTree(IRequestCycle cycle) throws Exception{
        IData pageData = this.getData();
        pageData.put("PARENT_PTYPE_CODE", "1000");
        pageData.put("TEST_TYPE", "1");
        //pageData.put("RECURSION", "1");
        IData temp = new DataMap();

        TreeItem root1 = new TreeItem("root", null, "集团产品列表", false,true);
        IDataset catalog = CSViewCall.call(this,"SS.QryAgreementSVC.queryTestProductTypeAndInfo", pageData);//
        //IDataset catalog = result.getData();

        if(catalog != null && catalog.size() > 0)
        {
            TreeItem[] trees = new TreeItem[catalog.size()];
            for (int i = 0; i < catalog.size(); i++) {

                IData type = catalog.getData(i);

                trees[i] = new TreeItem(catalog.getData(i).getString("PRODUCT_TYPE_CODE"),root1,catalog.getData(i).getString("PRODUCT_TYPE_NAME"),false,true);

                temp.put("TEST_TYPE", "2");
                temp.put("PRODUCT_TYPE_CODE", type.getString("PRODUCT_TYPE_CODE"));

                IDataset offer = CSViewCall.call(this,"SS.QryAgreementSVC.queryTestProductTypeAndInfo", temp);
                //IDataset offer = offerInfo.getData();
                if(offer != null && offer.size() > 0)
                {
                    TreeItem[] nodes = new TreeItem[offer.size()];
                    for (int j = 0; j < offer.size(); j++) {

                        nodes[j] = new TreeItem(offer.getData(j).getString("PRODUCT_ID"),trees[i],offer.getData(j).getString("PRODUCT_NAME"),offer.getData(j).getString("PRODUCT_TYPE_CODE"),true);

                    }
                }
            }

        }

        TreeParam param = TreeParam.getTreeParam(cycle);
        Map treeData = TreeFactory.buildTreeData(param, new TreeItem[]{root1});
        setAjax(treeData);
    }


    public abstract void setInfos(IDataset infos) throws Exception;
    public abstract void setOrderInfos(IDataset orderInfos) throws Exception;
    public abstract void setCustInfos(IDataset custInfos) throws Exception;
    public abstract void setInfoCount(long infoCount) throws Exception;
}
