
package com.asiainfo.veris.crm.order.soa.person.busi.batprintinvoice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.ailk.biz.BizEnv;
import com.ailk.bizservice.dao.Dao;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.sysorg.StaffInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.createusertrade.order.action.TradeBatPicIdSynRegAction;
import com.itextpdf.text.pdf.codec.Base64.InputStream;

import net.sf.json.JSONObject;

public class BatPrintInvoiceSVC extends CSBizService
{
	private static Logger logger = Logger.getLogger(BatPrintInvoiceSVC.class);
    private static final long serialVersionUID = 1L;

    /**
     * 批量打印
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset printTrade(IData input) throws Exception
    {
        IDataset printInfos = new DatasetList();
        BatPrintInvoiceBean bean = BeanManager.createBean(BatPrintInvoiceBean.class);
        printInfos.add(bean.printTrade(input));
        return printInfos;
    }

    /**
     * 查询批量打印数据
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset queryPrintInfo(IData input) throws Exception
    {
        BatPrintInvoiceBean bean = BeanManager.createBean(BatPrintInvoiceBean.class);
        return bean.queryPrintInfo(input, getPagination());
    }
    
    /**
     * 生成电子工单数据
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset createPrintInfo(IData input) throws Exception
    {
    	IDataset printInfos = new DatasetList();
        BatPrintInvoiceBean bean = BeanManager.createBean(BatPrintInvoiceBean.class);
        printInfos.add(bean.createPrintInfo(input));
        return printInfos;
    }
    
    //REQ201811130004优化物联网卡相关界面及功能——BOSS侧
    //查询打印电子工单数据  wuhao5
    public IDataset importData(IData input) throws Exception
    {
        BatPrintInvoiceBean bean = BeanManager.createBean(BatPrintInvoiceBean.class);
        return bean.importData(input);        
    }  

    //打印电子工单数据时传值给东软  wuhao5
    public void getTradeBatPicIdSyn(IData input) throws Exception
    {
    	BatPrintInvoiceBean bean = BeanManager.createBean(BatPrintInvoiceBean.class);
        bean.getTradeBatPicIdSyn(input);
    }
}
