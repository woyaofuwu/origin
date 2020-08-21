
package com.asiainfo.veris.crm.order.soa.person.busi.ziyoubusiness;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZiYouBusinessQryBean extends ZiYouBusinessBaseBean
{

    private static final Logger logger = LoggerFactory.getLogger(ZiYouBusinessQryBean.class);

    public IDataset queryBlackListInfo(IData input) throws Exception {

        String provinceID = input.getString("PROVINCEID");
        String indictseq = this.getIndictSequence(provinceID);//服务请求标识 数据构造： YYYYMMDD＋CSVC＋3位省代码＋7位流水号
        String originTime = SysDateMgr.getSysTime();//用户查询时间
        String mgmt_district =  CSBizBean.getUserEparchyCode();

        //调用iboss查询数据
        input.put("INDICTSEQ",indictseq);
        input.put("ORIGINTIME",originTime);
        input.put("SVCCITY",mgmt_district);
        input.put("SUBSLEVEL", this.getSubsLevel(input.getString("CALLERNO")));//根据手机号码查询
        IDataset ibossResponse = this.BussQureySerive(input);
        IDataset pdts = ibossResponse.getData(0).getDataset("PDTS_INFO");
        logger.debug("---------------IBOSS查询结果："+ibossResponse);

        //构造输入串，插入TF_B_PLATREQ_LOG表
        String serNumber = input.getString("CALLERNO");
        String serviceTypeId = input.getString("SERVICETYPEID");
        String operateTypeId = input.getString("OPERATETYPEID");
        this.insertPlatLog(indictseq, originTime, serNumber, serviceTypeId, operateTypeId);

        // 将请求服务标识、第一次查询时间和最大记录数保存到页面中，使之在queryOtherRecord方法能使用到
        IData rsltTotalCnt = ibossResponse.getData(0).getData("RSLT_CNT");
        IData nextRecordParam = new DataMap();
        long maxRecordNum = Long.parseLong(rsltTotalCnt.getString("RSLTTOTALCNT"));//取得最大记录数
        int RsltPageCurrCnt = Integer.parseInt(rsltTotalCnt.getString("RSLTPAGECURRCNT"));
        IData param1 = new DataMap();
        param1.put("INDICTSEQ", indictseq);
        param1.put("ORIGINTIME", originTime);
        param1.put("MAXRECORDNUM", String.valueOf(maxRecordNum));
        param1.put("RSLTPAGECURRCNT", String.valueOf(RsltPageCurrCnt));
        nextRecordParam.put("TEMPPARAM1", param1);

        //存储点击次数和点击页数
        IData reportPage = new DataMap();
        reportPage.put("ENTERPAGE"+0, "1");//点击的页数
        reportPage.put("ENTERTIME", "1");//点击的次数
        nextRecordParam.put("REPORTPAGE", reportPage);

        // 将归档标识记入页面中 0 为未归档 1 已归档
        IData pigeonHole = new DataMap();
        pigeonHole.put("PIGEONHOLE", "0");
        nextRecordParam.put("PIGEONHOLE", pigeonHole);

        // 判断是不是一页记录，如果只有一页记录，那么第一次查询后就归档
        if (RsltPageCurrCnt != 0)
        {
            if (1 == Math.ceil(maxRecordNum / RsltPageCurrCnt) + 1 || new Long(maxRecordNum).intValue() == RsltPageCurrCnt)
            {
                //调用IBoss进行归档并更新表TF_B_PLATREQ_LOG
                this.updatePlatLog(indictseq, "1");
                nextRecordParam.getData("PIGEONHOLE").put("PIGEONHOLE", "1");
            }
        }

        IData result = new DataMap();
        IDataset results = new DatasetList();
        result.put("OUTDATA", pdts);
        result.put("NEXT_RECORD_PARAM", nextRecordParam);
        results.add(result);
        return results;
    }

    /**
     * 插入操作日志表
     *
     * @param indictSeq
     * @param originTime
     * @param serNumber
     * @param serviceTypeId
     * @param operateTypeId
     * @throws Exception
     */
    public void insertPlatLog(String indictSeq, String originTime, String serNumber, String serviceTypeId, String operateTypeId) throws Exception
    {
        IData param = new DataMap();
        param.put("INDICT_SEQ", indictSeq);
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

    /**
     * 关闭页面触发方法
     *
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset subClosePage(IData input) throws Exception
    {
        String indictSeq = input.getString("INDICTSEQ");
        String pageOnHole = input.getString("PIGEONHOLE");
        //判断已经生成服务请求标识和未归档的查询，才调用关闭查询页面归档接口
        if(indictSeq != null && ("0".equals(pageOnHole))){
            this.updatePlatLog(indictSeq, "2");
        }
        return null;
    }

}
