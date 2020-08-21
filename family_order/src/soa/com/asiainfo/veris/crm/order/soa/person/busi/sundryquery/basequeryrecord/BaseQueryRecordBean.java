
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.basequeryrecord;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.log4j.Logger;

import com.ailk.biz.ftpmgr.FtpFileAction;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.common.file.FileUtil;
import com.ailk.common.util.FileManHelper;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.BaseQueryRecordQry;

public class BaseQueryRecordBean extends BaseQueryBean
{
    static transient final Logger logger = Logger.getLogger(BaseQueryRecordBean.class);

    // 普通附件
    String current = DateFormatUtils.format(new Date(), "yyyyMMdd");

    int number = 0;

    /**
     * 协助处理请求归档
     * 
     * @param indictSeq
     * @throws Exception
     */
    public IData AssistRequestArchival(IData input) throws Exception
    {
        IData data = new DataMap();
        IData rsult = new DataMap();
        data.put("INDICTSEQ", input.getString("INDICTSEQ", ""));
        data.put("HANDLINGDEPT", CSBizBean.getVisit().getDepartId() + "-" + CSBizBean.getVisit().getDepartName());
        data.put("HANDLINGSTAFF", CSBizBean.getVisit().getStaffId() + "-" + CSBizBean.getVisit().getStaffName());
        data.put("HANDLINGCOMMENT", input.getString("HANDLINGCOMMENT", ""));
        data.put("TIME", SysDateMgr.getSysTime());
        data.put("PHONENUM", input.getString("PHONENUM", ""));
        IDataset list = IBossCall.AssistRequestArchival(data);
        if (null == list || list.isEmpty() || !"0000".equals(list.getData(0).getString("X_RSPCODE", "")))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "调用IBOSS协助请求归档接口失败！");
        }
        else
        {
            IData param = new DataMap();
            // 这个是不是要提取到一个方法中去(是不是放入对应的bean中去)
            param.put("INDICTSEQ", data.getString("INDICTSEQ", ""));
            param.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
            param.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
            param.put("UPDATE_TIME", data.getString("TIME", ""));
            param.put("ASSISTREQ_STATUS", "4"); // 1－未派发 2－已派发 3－已回复 4－已归档
            int bool = BaseQueryRecordQry.AssistRequestArchival(param);
            if (bool > 0)
            {
                rsult.put("OPR_TIME".toUpperCase(), SysDateMgr.getSysTime());
                rsult.put("RSPCODE".toUpperCase(), "00");
                rsult.put("RSP_DESC".toUpperCase(), "OK");
            }
            else
            {
                rsult.put("RSPTYPE", "2");
                rsult.put("RSPCODE", "2998");
                rsult.put("RSP_DESC", "受理失败");
            }
        }
        return rsult;
    }

    /**
     * 协助处理请求催办
     * 
     * @param indictSeq
     * @throws Exception
     */
    public IDataset AssistRequestHasten(IData input) throws Exception
    {
        IData data = new DataMap();
        data.put("INDICTSEQ", input.getString("INDICTSEQ", ""));
        data.put("HANDLINGCOMMENT", input.getString("HANDLINGCOMMENT", ""));
        data.put("HANDLINGDEPT", CSBizBean.getVisit().getDepartId() + "-" + CSBizBean.getVisit().getDepartName());
        data.put("HANDLINGSTAFF", CSBizBean.getVisit().getStaffId() + "-" + CSBizBean.getVisit().getStaffName());
        data.put("PHONENUM", input.getString("PHONENUM", ""));
        IDataset list = IBossCall.AssistRequestHasten(data);
        if (null == list || list.isEmpty())
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "调用IBOSS协助处理请求催办接口失败！");
        }
        return list;
    }

    /**
     * 协助处理请求日志查询
     * 
     * @param indictSeq
     * @throws Exception
     */
    public IDataset AssistRequestLogQurey(IData input, Pagination page) throws Exception
    {
        return BaseQueryRecordQry.AssistRequestLogQurey(input, page);
    }

    /**
     * 协助处理请求查询
     * 
     * @param indictSeq
     * @throws Exception
     */
    public IDataset AssistRequestQurey(IData input) throws Exception
    {
        IData data = new DataMap();
        data.put("INDICTSEQ", input.getString("INDICTSEQ", ""));
        IDataset list = IBossCall.AssistRequestQurey(data);
        if (null == list || list.isEmpty())
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "调用IBOSS协助处理请求查询接口失败！");
        }
        return list;
    }

    /**
     * 协助处理请求回复(落地报文处理)
     * 
     * @param indictSeq
     * @throws Exception
     */
    public IData AssistRequestReply(IData input) throws Exception
    {
        String indictSeq = input.getString("INDICTSEQ", "");
        String indictRslt = input.getString("INDICTRSLT", "");// 协助处理请求处理结果
        String handlingDept = input.getString("HANDLINGDEPT", "");// 回复部门
        String handlingStaff = input.getString("HANDLINGSTAFF", "");// 回复人
        String staffContactPhone = input.getString("STAFFCONTACTPHONE", "");// 回复人联系电话
        String replyTime = input.getString("REPLYTIME", "");// 回复时间
        String attachs = input.getString("ATTACH_NAME", "[无]"); // 回复附件 REQUEST_FILEATTACH
        String records = input.getString("RECORD_FILE", "[无]"); // 回复附件 REQUEST_FILEATTACH
        IData param = new DataMap();
        // 这个是不是要提取到一个方法中去(是不是放入对应的bean中去)
        param.put("INDICTSEQ", indictSeq);
        param.put("ASSISTREQ_RSLT", indictRslt);
        param.put("REPLY_DEPT", handlingDept);
        param.put("REPLY_STAFF", handlingStaff);
        param.put("REPLY_PHONE", staffContactPhone);
        param.put("REPLY_TIME", replyTime);
        param.put("ASSISTREQ_STATUS", "3"); // 1－未派发 2－已派发 3－已回复 4－已归档
        param.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        param.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        param.put("UPDATE_TIME", SysDateMgr.getSysTime());
        param.put("ANSWER_FILEATTACH", attachs);
        param.put("ANSWER_VEDIOATTACH", records);
        int bool = BaseQueryRecordQry.AssistRequestReply(param);
        IData data = new DataMap();// 返回结果
        if (bool > 0)
        {
            data.put("OPR_TIME".toUpperCase(), SysDateMgr.getSysTime());
            data.put("RSPCODE".toUpperCase(), "00");
            data.put("RSP_DESC".toUpperCase(), "OK");
        }
        else
        {
            data.put("RSPTYPE", "2");
            data.put("RSPCODE", "2998");
            data.put("RSP_DESC", "受理失败");
        }
        return data;
    }

    /**
     * 协助处理请求派发
     * 
     * @param indictSeq
     * @throws Exception
     */
    public IData AssistRequestSend(IData input) throws Exception
    {
        IData data = new DataMap();
        IData data1 = new DataMap();// 返回结果
        String provinceID = this.getProvinceID();
        String indictSeq = this.getIndictSequence(provinceID);
        data.put("INDICTSEQ", indictSeq);
        data.put("HOMEPROV", provinceID);
        data.put("URGENTID", input.getString("URGENTID", ""));
        data.put("ASSISTREQUESTTIME", input.getString("ASSISTREQUESTTIME", ""));
        data.put("DEADTIME", input.getString("DEADTIME", ""));
        data.put("ASSISTREQUESTSTAFF", CSBizBean.getVisit().getStaffId());
        data.put("SERVICETYPEID", input.getString("SERVICETYPEID", ""));
        data.put("OPERATETYPEID", input.getString("OPERATETYPEID", ""));
        data.put("ASSISTREQUESTTITLE", input.getString("ASSISTREQUESTTITLE", ""));
        data.put("ASSISTREQUESTCONTENT", input.getString("ASSISTREQUESTCONTENT", ""));
        data.put("PRETREATMENT", input.getString("PRETREATMENT", ""));

        data.put("COMPANYNAME", input.getString("COMPANYNAME", ""));
        data.put("SPCODE", input.getString("SPCODE", ""));
        data.put("FELLBACKDATE", input.getString("FELLBACKDATE", ""));
        data.put("FELLBACKFACT", input.getString("FELLBACKFACT", ""));
        data.put("REFERUSERCOUNT", input.getString("REFERUSERCOUNT", ""));
        data.put("SOCIALEFFECT", input.getString("SOCIALEFFECT", ""));
        IDataset ATTACH_NAME = doOtherFileInfo("WFSEND_0018_", input.getString("ATTACH_LIST", ""), "731");
        IDataset RECORD_FILE = doOtherFileInfo("WFSEND_0017_", input.getString("RECORD_LIST", ""), "731");
        data.put("ATTACH_NAME", ATTACH_NAME);
        data.put("RECORD_FILE", RECORD_FILE);
        IDataset bussResult = IBossCall.AssistRequestSend(data);
        // 业务处理成功，做下一步
        if (null == bussResult || bussResult.size() == 0)
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "调用IBOSS协助处理请求派发接口失败！");
        }
        else
        {
            // 构造输入串，插入TF_B_ASSISTREQ_LOG表
            IData param = new DataMap();
            param.put("INDICTSEQ", indictSeq);
            param.put("ASSISTREQ_TIME", data.getString("ASSISTREQUESTTIME", ""));
            param.put("DEAD_TIME", data.getString("DEADTIME", ""));
            param.put("ASSISTREQ_STAFF", data.getString("ASSISTREQUESTSTAFF", ""));
            param.put("SERVICE_TYPE", data.getString("SERVICETYPEID", ""));
            param.put("URGENT_ID", data.getString("URGENTID", ""));
            param.put("OPERATE_TYPE", data.getString("OPERATETYPEID", "")); // ?
            param.put("ASSISTREQ_TITLE", data.getString("ASSISTREQUESTTITLE", ""));
            param.put("ASSISTREQ_CONTENT", data.getString("ASSISTREQUESTCONTENT", ""));
            param.put("ASSISTREQ_RSLT", data.getString("PRETREATMENT", ""));
            param.put("REMARK", "协助请求已派发");
            param.put("ASSISTREQ_STATUS", "2"); // 1－未派发 2－已派发 3－已回复 4－已归档
            param.put("REQUEST_FILEATTACH", data.getString("ATTACH_NAME", ""));
            param.put("REQUEST_VEDIOATTACH", data.getString("RECORD_FILE", ""));
            param.put("ANSWER_FILEATTACH", "[无]");
            param.put("ANSWER_VEDIOATTACH", "[无]");
            param.put("FILECOUNT", ATTACH_NAME.size() + RECORD_FILE.size());
            boolean bool = BaseQueryRecordQry.AssistRequestSend(param);
            if (bool)
            {
                data1.put("INDICTSEQ", indictSeq);
                data1.put("OPR_TIME".toUpperCase(), SysDateMgr.getSysTime());
                data1.put("RSPCODE".toUpperCase(), "00");
                data1.put("RSP_DESC".toUpperCase(), "OK");
            }
            else
            {
                data1.put("RSPTYPE", "2");
                data1.put("RSPCODE", "2998");
                data1.put("RSP_DESC", "受理失败");
            }

        }
        return data1;
    }

    public IDataset doOtherFileInfo(String top, String fileName, String provinceID) throws Exception
    {
        // 10746245:网页问题1.doc/10746249:邕州海关3.jpg/10746250:壁纸20140315193127.jpg
        // WFSEND_0018_AAA_YYYYMMDD_NNNNNN.XXX
        String[] fileIDs = fileName.split(",");
        IDataset dataset = new DatasetList();

        FtpFileAction ftpFileAction = new FtpFileAction();
        ftpFileAction.setVisit(getVisit());

        IData config = ftpFileAction.getFtpConfig("personserv", getVisit());
        FileUtil fileUtil = new FileUtil(config);
        fileUtil.setAutoRelease(false);
        boolean changed = false;
        String filePath = "incoming/csvc";
        try
        {
            changed = fileUtil.changeDirectory(filePath);
        }
        catch (Exception e)
        {
            changed = false;
        }
        if (!changed && ftpFileAction.createDirectory(fileUtil, filePath))
            ftpFileAction.changeDirectory(fileUtil, filePath);

        for (int i = 0; i < fileIDs.length; i++)
        {
            StringBuilder sb = new StringBuilder(top);
            String fileType = "";
            String fileID = fileIDs[i];
            IData files = ftpFileAction.query(fileID);
            if (null != files && !files.isEmpty())
            {
                fileType = files.getString("fileName", "").substring(files.getString("fileName", "").lastIndexOf("."));
                // 得到文件的后缀名
                sb.append(provinceID + "_").append(getIntanceNumber()).append(fileType);
                File file = ftpFileAction.download(fileID);
                InputStream in = new FileInputStream(file);
                int fileSize = in.available();
                fileUtil.uploadFile(in, sb.toString());
                ftpFileAction.delete(fileID);
                IData di = new DataMap();
                di.putAll(getVisit().getAll());
                di.put("FILE_ID", fileID);
                di.put("FILE_NAME", sb.toString());
                di.put("FTP_SITE", "personserv");
                di.put("FILE_SIZE", String.valueOf(fileSize));
                di.put("FILE_PATH", filePath);
                di.put("FILE_TYPE", "1");
                di.put("FILE_KIND", "1");
                di.put("ACTION", String.valueOf(3));
                CSAppCall.call("SYS_FtpFileMgr", di);
                dataset.add(sb.toString());
                in.close();
            }
        }
        fileUtil.releaseResourse();
        return dataset;
    }

    public String downloadout(String fileName) throws Exception
    {
        FtpFileAction ftpFileAction = new FtpFileAction();
        ftpFileAction.setVisit(getVisit());

        IData config = ftpFileAction.getFtpConfig("personserv", getVisit());
        FileUtil fileUtil = new FileUtil(config);
        fileUtil.setAutoRelease(false);
        boolean changed = false;
        String filePath = "outcoming/csvc";
        fileUtil.setAutoRelease(false);
        try
        {
            changed = fileUtil.changeDirectory(filePath);
        }
        catch (Exception e)
        {
            changed = false;
        }
        if (!changed && ftpFileAction.createDirectory(fileUtil, filePath))
            ftpFileAction.createDirectory(fileUtil, filePath);

        StringBuilder attachs = new StringBuilder();// 回复附件 REQUEST_FILEATTACH
        String attch[] = fileName.replaceAll("\"", "").replace("[", "").replace("]", "").split(",");

        IDataset ds = new DatasetList();
        for (int i = 0; i < attch.length; i++)
        {
            IData di = new DataMap();
            di.putAll(getVisit().getAll());
            di.put("FILE_ID", "");
            di.put("FILE_NAME", attch[i]);
            di.put("FTP_SITE", "personserv");
            di.put("FILE_SIZE", "");
            di.put("FILE_PATH", "outgoing/csvc");
            di.put("FILE_TYPE", "1");
            di.put("FILE_KIND", "1");
            di.put("ACTION", String.valueOf(3));
            ds = CSAppCall.call("SYS_FtpFileMgr", di);
            attachs.append(attch[i]).append("-").append(ds.getData(0).getString("FILE_ID", "")).append(",");
            File file = new File(attch[i]);
            OutputStream out = FileManHelper.getOutputStream(file);
            fileUtil.retrieveFile(out, fileName);
            InputStream in = new FileInputStream(file);
            int fileSize = in.available();
            fileUtil.uploadFile(in, ds.getData(0).getString("FILE_ID", ""));
        }
        fileUtil.releaseResourse();
        return attachs.toString();
    }

    public String getIntanceNumber() throws Exception
    {
        StringBuilder strbuffer = new StringBuilder(current);
        strbuffer.append("_");
        String count = "";
        number = number + 1;

        count = number + "";
        int len = count.length();
        if (len < 6)
        {
            for (int j = 0; j < (6 - len); j++)
            {
                strbuffer.append("0");
            }
        }

        String strNumber = strbuffer.append(count + "").toString();
        return strNumber;
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
        if(serNumber == null || "".equals(serNumber)){
        	serNumber = " ";
        }
        param.put("SERIAL_NUMBER", serNumber);
        param.put("TRADE_STAFF_ID", getVisit().getStaffId());
        param.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        param.put("SERVICE_TYPE_ID", serviceTypeId);
        param.put("OPERATE_TYPE_ID", operateTypeId);
        param.put("IN_MODE_CODE", "0");
        this.InsertPlatREQ(param);
    }

    public IDataset subBussQureySerive(IData input) throws Exception
    {
        String provinceID = this.getProvinceID();
        String indictSeq = this.getIndictSequence(provinceID);
        String originTime = SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND);

        input.put("HOMEPROV", provinceID);
        input.put("INDICTSEQ", indictSeq);
        input.put("SVCCITY", CSBizBean.getTradeEparchyCode());
        input.put("ORIGINTIME", originTime);
        input.put("ACCEPTTIME", originTime);

        IDataset results = this.BussQureySerive(input);
        
        String serNumber = input.getString("CALLERNO");
        String serviceTypeId = input.getString("SERVICETYPEID");
        String operateTypeId = input.getString("OPERATETYPEID");

        IData data = results.getData(0);
        IData RsltTotalCnt = data.getData("RSLT_CNT");

        // 取得最大记录数
        long MaxRecordNum = Long.parseLong(RsltTotalCnt.getString("RSLTTOTALCNT"));

        // 取得每页记录数
        int RsltPageCurrCnt = Integer.parseInt(RsltTotalCnt.getString("RSLTPAGECURRCNT"));

        this.insertPlatLog(indictSeq, originTime, serNumber, serviceTypeId, operateTypeId);

        // 将请求服务标识、第一次查询时间和最大记录数保存到页面中，使之在queryOtherRecord方法能使用到
        IData tempParam = new DataMap();
        tempParam.put("INDICTSEQ", indictSeq);
        tempParam.put("ORIGINTIME", originTime);
        tempParam.put("MAXRECORDNUM", MaxRecordNum);
        tempParam.put("RSLTPAGECURRCNT", RsltPageCurrCnt);
        // 构造IData类型，存储记录点击的页数和次数
        IData reportPage = new DataMap();
        reportPage.put("ENTERPAGE" + 0, "1");
        reportPage.put("ENTERTIME", "1");
        data.put("REPORTPAGE", reportPage);
        // 将归档标识记入页面中 0 为归档 1 已归档
        tempParam.put("PIGEONHOLE", "0");

        // 判断是不是一页记录，如果只有一页记录，那么第一次查询后就归档
        if (RsltPageCurrCnt != 0)
        {
            if ((1 == Math.ceil(MaxRecordNum / RsltPageCurrCnt) + 1) || (MaxRecordNum == RsltPageCurrCnt))
            {
                this.updatePlatLog(indictSeq, "1");
                // 将归档标识记入页面中
                tempParam.put("PIGEONHOLE", "1");
            }
        }
        data.put("PAGE_INFO", tempParam);

        return results;
    }
    
    /**
     * 用户轨迹查询
     */
    public IDataset subBussQureyAction(IData input) throws Exception
    {
        String provinceID = this.getProvinceID();
        String indictSeq = this.getIndictSequence(provinceID);
        String originTime = SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND);
        
        input.put("HOMEPROV", provinceID);
        input.put("INDICTSEQ", indictSeq);
        input.put("SVCCITY", CSBizBean.getTradeEparchyCode());
        input.put("ORIGINTIME", originTime);
        input.put("ACCEPTTIME", originTime);

        IDataset results = this.BussQureySerive1(input);

        String trade_id = input.getString("TRADE_ID");
        String serviceTypeId = input.getString("SERVICETYPEID");
        String operateTypeId = input.getString("OPERATETYPEID");

        IData data = results.getData(0);
        IData RsltTotalCnt = data.getData("RSLT_CNT");
        
        // 取得最大记录数
        long MaxRecordNum = Long.parseLong(RsltTotalCnt.getString("RSLTTOTALCNT"));

        // 取得每页记录数
        int RsltPageCurrCnt = Integer.parseInt(RsltTotalCnt.getString("RSLTPAGECURRCNT"));

        //this.insertPlatLog(indictSeq, originTime, trade_id, serviceTypeId, operateTypeId);

        // 将请求服务标识、第一次查询时间和最大记录数保存到页面中，使之在queryOtherRecord方法能使用到
        IData tempParam = new DataMap();
        tempParam.put("INDICTSEQ", indictSeq);
        tempParam.put("ORIGINTIME", originTime);
        tempParam.put("MAXRECORDNUM", MaxRecordNum);
        tempParam.put("RSLTPAGECURRCNT", RsltPageCurrCnt);
        // 构造IData类型，存储记录点击的页数和次数
        IData reportPage = new DataMap();
        reportPage.put("ENTERPAGE" + 0, "1");
        reportPage.put("ENTERTIME", "1");
        data.put("REPORTPAGE", reportPage);
      // 将归档标识记入页面中 0 为归档 1 已归档
        tempParam.put("PIGEONHOLE", "0");

        // 判断是不是一页记录，如果只有一页记录，那么第一次查询后就归档
       /* if (RsltPageCurrCnt != 0)
        {
            if ((1 == Math.ceil(MaxRecordNum / RsltPageCurrCnt) + 1) || (MaxRecordNum == RsltPageCurrCnt))
            {
                this.updatePlatLog(indictSeq, "1");
                // 将归档标识记入页面中
                tempParam.put("PIGEONHOLE", "1");
            }
        }*/
        data.put("PAGE_INFO", tempParam);

        return results;
    }
    
    /**
     * 用户轨迹查询未获取的数据
     */
        public IDataset subUnfinishBussQureyAction(IData input) throws Exception
        {
            String indictSeq = input.getString("INDICTSEQ");
            String originTime = input.getString("ORIGINTIME");
            String pigeOnHole = input.getString("PIGEONHOLE");
            long MaxRecordNum = input.getLong("MAXRECORDNUM");
            int RsltPageCurrCnt = input.getInt("RSLTPAGECURRCNT");
            int currPage = input.getInt("CURR_PAGE");
            IData reportPage = new DataMap(input.getString("REPORTPAGE"));

            if ("1".equals(pigeOnHole))
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "您已经获取了全部查询记录，如果您需要再次浏览记录，请重新点击查询按钮！");
            }

            // 判断时间是否已经超过了10分钟
            String sysDate = SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND);
            Date startDate = SysDateMgr.string2Date(originTime, "yyyy-MM-dd HH:mm:ss");
            Date enDate = SysDateMgr.string2Date(sysDate, "yyyy-MM-dd HH:mm:ss");

            IDataset infos = new DatasetList();

            long diff = (enDate.getTime() - startDate.getTime()) / 60000;

            if (diff <= 10)
            {
                // 未到达业务查询服务输入参数构造
                IData nuQueryData = new DataMap();
                nuQueryData.put("KIND_ID", "BIP2C092_T2002092_0_0");
                nuQueryData.put("INDICTSEQ", indictSeq);
                nuQueryData.put("QUERYPAGENUM", currPage);

                infos = this.UnfinishBussQureySerive1(nuQueryData);

                // 新接收的数据点击记录，记入页面REPORTPAGE中
                int enterTime = Integer.parseInt(reportPage.getString("ENTERTIME"));

                reportPage.put("ENTERPAGE" + enterTime, currPage);
                enterTime++;
                reportPage.put("ENTERTIME", enterTime);

                input.put("REPORTPAGE", reportPage);
                input.put("MAXRECORDNUM", MaxRecordNum);
                input.put("RSLTPAGECURRCNT", RsltPageCurrCnt);

                // 检查是否数据全部接收完毕
                boolean continueOk = true;// true表示页面没有完全点完，还有页面记录没有接收到，false表示全部记录都接收完毕

                int PageNumber;

                if (MaxRecordNum % RsltPageCurrCnt == 0)
                {
                    PageNumber = (int) Math.ceil(MaxRecordNum / RsltPageCurrCnt) + 1;
                }
                else
                {
                    PageNumber = (int) Math.ceil(MaxRecordNum / RsltPageCurrCnt) + 2;
                }

                for (int c = 1; c < PageNumber; c++)
                {
                    for (int j = 0; j < reportPage.size() - 1; j++)
                    {
                        if (c == reportPage.getInt("ENTERPAGE" + j))
                        {
                            continueOk = false;
                        }
                    }
                    if (continueOk == true)
                    {
                        break;
                    }
                    if (c == PageNumber - 1)
                    {
                        continueOk = false;
                    }
                    else
                    {
                        continueOk = true;
                    }
                }

                if (!continueOk)
                {
                    this.updatePlatLog(indictSeq, "1");

                    input.put("PIGEONHOLE", "1");
                }

            }
            else
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "距第一次查询服务请求超过10分钟，为获取到最新数据，请重新点击查询按钮！");
            }

            IData data = new DataMap();
            IDataset dataset = new DatasetList();

            data.put("PAGE_INFO", input);
            data.put("PDTS_INFO", infos);

            dataset.add(data);

            return dataset;
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

        this.updatePlatLog(indictSeq, "2");

        return null;
    }

    public IData subSubmitProcess(IData input) throws Exception
    {
        String serialNum = input.getString("CALLERNO");
        String provinceID = input.getString("HOMEPROV");
        String serviceTypeId = input.getString("SERVICETYPEID");
        String operateTypeId = input.getString("OPERATETYPEID");

        String indictSeq = this.getIndictSequence(provinceID);
        String originTime = SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND);

        input.put("INDICTSEQ", indictSeq);
        input.put("SVCCITY", CSBizBean.getTradeEparchyCode());
        input.put("ORIGINTIME", originTime);
        input.put("ACCEPTTIME", originTime);

        IData result = this.BussTransact(input);
        // 调用办理业务接口，不管成功失败同时调用归档接口
        if ("00".equals(result.getString("OPERRTNSTAID")))
        {
            this.insertPlatLog(indictSeq, originTime, serialNum, serviceTypeId, operateTypeId);

            this.updatePlatLog(indictSeq, "0");
        }
        else
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "平台处理异常，操作不成功！");
        }

        return result;
    }

    public IDataset subUnfinishBussQureySerive(IData input) throws Exception
    {
        String indictSeq = input.getString("INDICTSEQ");
        String originTime = input.getString("ORIGINTIME");
        String pigeOnHole = input.getString("PIGEONHOLE");
        long MaxRecordNum = input.getLong("MAXRECORDNUM");
        int RsltPageCurrCnt = input.getInt("RSLTPAGECURRCNT");
        int currPage = input.getInt("CURR_PAGE");
        IData reportPage = new DataMap(input.getString("REPORTPAGE"));

        if ("1".equals(pigeOnHole))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "您已经获取了全部查询记录，如果您需要再次浏览记录，请重新点击查询按钮！");
        }

        // 判断时间是否已经超过了10分钟
        String sysDate = SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND);
        Date startDate = SysDateMgr.string2Date(originTime, "yyyy-MM-dd HH:mm:ss");
        Date enDate = SysDateMgr.string2Date(sysDate, "yyyy-MM-dd HH:mm:ss");

        IDataset infos = new DatasetList();

        long diff = (enDate.getTime() - startDate.getTime()) / 60000;

        if (diff <= 10)
        {
            // 未到达业务查询服务输入参数构造
            IData nuQueryData = new DataMap();
            nuQueryData.put("KIND_ID", "BIP2C093_T2002093_0_0");
            nuQueryData.put("INDICTSEQ", indictSeq);
            nuQueryData.put("QUERYPAGENUM", currPage);

            infos = this.UnfinishBussQureySerive(nuQueryData);

            // 新接收的数据点击记录，记入页面REPORTPAGE中
            int enterTime = Integer.parseInt(reportPage.getString("ENTERTIME"));

            reportPage.put("ENTERPAGE" + enterTime, currPage);
            enterTime++;
            reportPage.put("ENTERTIME", enterTime);

            input.put("REPORTPAGE", reportPage);
            input.put("MAXRECORDNUM", MaxRecordNum);
            input.put("RSLTPAGECURRCNT", RsltPageCurrCnt);

            // 检查是否数据全部接收完毕
            boolean continueOk = true;// true表示页面没有完全点完，还有页面记录没有接收到，false表示全部记录都接收完毕

            int PageNumber;

            if (MaxRecordNum % RsltPageCurrCnt == 0)
            {
                PageNumber = (int) Math.ceil(MaxRecordNum / RsltPageCurrCnt) + 1;
            }
            else
            {
                PageNumber = (int) Math.ceil(MaxRecordNum / RsltPageCurrCnt) + 2;
            }

            for (int c = 1; c < PageNumber; c++)
            {
                for (int j = 0; j < reportPage.size() - 1; j++)
                {
                    if (c == reportPage.getInt("ENTERPAGE" + j))
                    {
                        continueOk = false;
                    }
                }
                if (continueOk == true)
                {
                    break;
                }
                if (c == PageNumber - 1)
                {
                    continueOk = false;
                }
                else
                {
                    continueOk = true;
                }
            }

            if (!continueOk)
            {
                this.updatePlatLog(indictSeq, "1");

                input.put("PIGEONHOLE", "1");
            }

        }
        else
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "距第一次查询服务请求超过10分钟，为获取到最新数据，请重新点击查询按钮！");
        }

        IData data = new DataMap();
        IDataset dataset = new DatasetList();

        data.put("PAGE_INFO", input);
        data.put("PDTS_INFO", infos);

        dataset.add(data);

        return dataset;
    }

    /**
     * 更新日志表
     * 
     * @param indictSeq
     * @throws Exception
     */
    public void updatePlatLog(String indictSeq, String hisTag) throws Exception
    {
        String remark = "";

        if ("0".equals(hisTag))
        {
            remark = "业务办理服务执行完毕归档";
        }
        if ("1".equals(hisTag))
        {
            remark = "查询数据获取完毕归档";
        }
        if ("2".equals(hisTag))
        {
            remark = "关闭查询页面归档";
        }

        IBossCall.BussToHisIBOSS(indictSeq, remark);

        // 更新TF_B_PLATREQ_LOG表
        IData updParam = new DataMap();
        updParam.put("INDICT_SEQ", indictSeq);
        updParam.put("FINISH_DEPART_ID", getVisit().getDepartId());
        updParam.put("FINISH_STAFF_ID", getVisit().getStaffId());
        updParam.put("REMARK", remark);

        this.UpdatePlatREQ(updParam);
    }
}
