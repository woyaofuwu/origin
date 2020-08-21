
package com.asiainfo.veris.crm.order.soa.person.busi.internetofthings.order.action;

import java.util.Iterator;
import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.collections.CollectionUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.iot.IotQuery;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

/**
 * 物联网将订购了测试期优惠保存到TF_INTERNETOFTHING_BOOK表
 * 
 * @author xiekl
 */
public class GeneIotBookAction implements ITradeAction
{

    public void executeAction(BusiTradeData btd) throws Exception
    {
        BaseReqData reqData = btd.getRD();
        UcaData uca = reqData.getUca();
        String tradeTypeCode = reqData.getTradeType().getTradeTypeCode();

        if (!"PWLW".equals(uca.getBrandCode()) 
         || !("10".equals(tradeTypeCode) || "274".equals(tradeTypeCode) || "268".equals(tradeTypeCode) || "277".equals(tradeTypeCode)))
        {
            return;
        }

        if ("10".equals(tradeTypeCode))
        {
            insertIotBook(btd, uca);
        }

        if ("274".equals(tradeTypeCode) || "268".equals(tradeTypeCode))
        {
            updateIotBook(btd, uca);
        }

        if("277".equals(tradeTypeCode))
        {
        	updateIotBookDealTag(btd, uca);
        }

    }

    /**
     * 获取物联网测试期优惠
     * 
     * @param uca
     * @return
     * @throws Exception
     */
    private DiscntTradeData getTestDiscnt(UcaData uca) throws Exception
    {
        DiscntTradeData testDiscnt = null;
        IDataset testConfigList = CommparaInfoQry.getCommByParaAttr("CSM", "9013", "0731");
        for (int i = 0; i < testConfigList.size(); i++)
        {
            IData config = testConfigList.getData(i);
            String strP5 = config.getString("PARA_CODE5", "");
            String strP20 = config.getString("PARA_CODE20", "");
            if (!strP20.startsWith("NB") && "1".equals(strP5))
            {
            	String strPC = config.getString("PARAM_CODE", "");
                List<DiscntTradeData> discntList = uca.getUserDiscntByDiscntId(strPC);
                if (CollectionUtils.isNotEmpty(discntList))
                {
                    testDiscnt = discntList.get(0);
                }
            }
        }
        return testDiscnt;
    }

    private DiscntTradeData getTestDiscntFormTradeDatas(BusiTradeData btd,String discntCode)
    {
        List<DiscntTradeData> tradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_DISCNT);
        DiscntTradeData testDiscnt = null;
        for (Iterator iterator = tradeDatas.iterator(); iterator.hasNext();)
        {
            DiscntTradeData discntTradeData = (DiscntTradeData) iterator.next();
             
            if (discntTradeData.getDiscntCode().equals(discntCode))
            {
                testDiscnt = discntTradeData;
                break;
            }
            
        }
        return testDiscnt;
    }

    private void insertIotBook(BusiTradeData btd, UcaData uca) throws Exception
    {
    	IData param = btd.getRD().getPageRequestData();
    	String strCMQ = param.getString("CHEN_MO_QI", "");
    	String strAT = param.getString("ACCT_TAG", "");
        DiscntTradeData testDiscnt = getTestDiscnt(uca);
        if (testDiscnt != null && !"1".equals(strCMQ))
        {
            IData bookData = new DataMap();
            bookData.put("INST_ID", SeqMgr.getInstId());
            bookData.put("USER_ID", uca.getUserId());
            bookData.put("SERIAL_NUMBER", uca.getSerialNumber());
            bookData.put("OLD_STATE_CODE", "0");
            bookData.put("NEW_STATE_CODE", "1");
            bookData.put("TRAN_DATE", testDiscnt.getEndDate());
            bookData.put("DEAL_TAG", "0");// 默认为未处理
            bookData.put("EXC_TIME", SysDateMgr.getSysTime());
            bookData.put("RESULT_CODE", "");
            bookData.put("RESULT_INFO", "");
            bookData.put("TRADE_ID", btd.getTradeId());
            bookData.put("RSRV_STR1", btd.getMainTradeData().getOrderId());
            bookData.put("RSRV_STR2", strCMQ + ", " + strAT);
            bookData.put("UPDATE_TIME", SysDateMgr.getSysTime());
            bookData.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
            bookData.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
            Dao.insert("TF_F_INTERNETOFTHINGS_BOOK", bookData, Route.CONN_CRM_CEN);
        }
        else if("1".equals(strCMQ))
        {
        	IData bookData = new DataMap();
            bookData.put("INST_ID", SeqMgr.getInstId());
            bookData.put("USER_ID", uca.getUserId());
            bookData.put("SERIAL_NUMBER", uca.getSerialNumber());
            bookData.put("OLD_STATE_CODE", "1");
            bookData.put("NEW_STATE_CODE", "2");
            String strED = SysDateMgr.firstDayOfMonth(SysDateMgr.getSysTime(), 6);
            bookData.put("TRAN_DATE", strED);
            bookData.put("DEAL_TAG", "0");// 默认为未处理
            bookData.put("EXC_TIME", SysDateMgr.getSysTime());
            bookData.put("RESULT_CODE", "");
            bookData.put("RESULT_INFO", "");
            bookData.put("TRADE_ID", btd.getTradeId());
            bookData.put("RSRV_STR1", btd.getMainTradeData().getOrderId());
            bookData.put("RSRV_STR2", strCMQ + ", " + strAT);
            bookData.put("UPDATE_TIME", SysDateMgr.getSysTime());
            bookData.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
            bookData.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
            Dao.insert("TF_F_INTERNETOFTHINGS_BOOK", bookData, Route.CONN_CRM_CEN);
        }
    }

    private void updateIotBook(BusiTradeData btd, UcaData uca) throws Exception
    {
        DiscntTradeData testDiscnt = getTestDiscnt(uca);
        if (testDiscnt != null)
        {
            IData bookData = IotQuery.queryUserIotBook(uca.getUserId(), "0", "1", "0", null);
            if (bookData == null)
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "用户测试期转换记录不存在");
            }

            bookData.put("TRAN_DATE", testDiscnt.getEndDate());
            Dao.update("TF_F_INTERNETOFTHINGS_BOOK", bookData, new String[]
            { "INST_ID" }, Route.CONN_CRM_CEN);
        }
    }
    
    private void updateIotBookDealTag(BusiTradeData btd, UcaData uca) throws Exception
    {
    	IData param = new DataMap();
    	param.put("SERIAL_NUMBER", uca.getSerialNumber()); 
    	
    	StringBuilder sql = new StringBuilder(1000);
    	sql.append(" UPDATE TF_F_INTERNETOFTHINGS_BOOK T ");
    	sql.append(" SET T.DEAL_TAG = '1', T.UPDATE_TIME = SYSDATE ");
    	sql.append(" WHERE T.SERIAL_NUMBER = :SERIAL_NUMBER ");
        Dao.executeUpdate(sql, param, Route.CONN_CRM_CEN);
    }

}
