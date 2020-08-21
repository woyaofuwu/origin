package com.asiainfo.veris.crm.order.soa.person.busi.ziyoubusiness;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.basequeryrecord.BaseQueryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SingleAbilityQryBean extends BaseQueryBean {
    private static final Logger logger = LoggerFactory.getLogger(SingleAbilityQryBean.class);

    public IDataset qrySingleAbility(IData input) throws Exception {
        String id = input.getString("ID");
        String provinceid = input.getString("PROVINCEID");
        //自己拼出来
        String indictseq = this.getIndictSequence(provinceid);
        String serviceTypeId = input.getString("SERVICETYPEID");
        String kindId="BIP2C138_T2002138_0_0";
        input.put("KIND_ID",kindId);
        //调iboss
        IData ibossResponse = IBossCall.singleAbilityQureySeriveIBOSS(kindId,id,serviceTypeId,indictseq);

        String originTime = SysDateMgr.getSysTime();//用户查询时间
        String serNumber = "13125649858";

        logger.debug("---------------IBOSS查询结果："+ibossResponse);

        //数据库插入记录
        this.insertPlatLog(indictseq, originTime, serNumber, serviceTypeId, id);
        //返回报文
        IData result = new DataMap();
        IDataset results = new DatasetList();
        IData requestResult = new DataMap();

        String requestElement = ibossResponse.getString("REQUESTELEMENTS");
//        IDataset requestResults = this.resolveReturnStringForAbility(requestElement);
        String returnElement = ibossResponse.getString("RETURNELEMENTS");
        //IDataset returnResults = this.resolveReturnStringForAbility(returnElement);
        IDataset returnArry = new DatasetList();
        IData element = new DataMap();
        element.put("RSRV_STR0",requestElement);
        element.put("RSRV_STR1",returnElement);
        returnArry.add(element);
        //result.put("RequestElements", requestResults);
        result.put("ReturnElements", returnArry);
        results.add(result);
        return results;
    }
    public String getIndictSequence(String provinceID) throws Exception
    {
        String tempSeq = SeqMgr.getCustContact();
        String finalSeq = "";
        String indictSeq = "";
        if (tempSeq.length() == 7)
        {
            finalSeq = tempSeq;
        }
        else
        {
            finalSeq = tempSeq.substring(tempSeq.length() - 7, tempSeq.length());
        }
        indictSeq = SysDateMgr.getNowCycle() + "CSVC" + provinceID + finalSeq;
        return indictSeq;
    }
    public void insertPlatLog(String indictseq, String originTime, String serNumber, String serviceTypeId, String operateTypeId) throws Exception {
        IData param = new DataMap();
        param.put("INDICT_SEQ", indictseq);
        param.put("ACCEPT_MONTH", originTime.substring(5, 7));
        param.put("REQ_TIME", originTime);
        param.put("STATE", "0");
        param.put("SERIAL_NUMBER", serNumber);
        param.put("TRADE_STAFF_ID", getVisit().getStaffId());
        param.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        param.put("SERVICE_TYPE_ID", serviceTypeId);
        param.put("OPERATE_TYPE_ID", operateTypeId);
        param.put("IN_MODE_CODE", "0");
        this.InsertPlatREQ(param);

    }
    public void InsertPlatREQ(IData input) throws Exception {
        Dao.insert("TF_B_PLATREQ_LOG", input);
    }
}
