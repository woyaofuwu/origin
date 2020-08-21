
package com.asiainfo.veris.crm.order.web.person.assistReqProcess;

import java.net.URLDecoder;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonQueryPage;

public abstract class AssistRequestProcess extends PersonQueryPage
{
    /**
     * 协助处理请求归档
     * 
     * @param indictSeq
     * @throws Exception
     */
    public void AssistRequestArchival(IRequestCycle cycle) throws Exception
    {
        IData condParams = getData("cond", true);
        // 封装数据
        // 调用公用方法接口，协助处理请求归档接口
        IDataset infos = CSViewCall.call(this, "SS.BaseCommRecordSVC.AssistRequestArchival", condParams);
        setAjax(infos.getData(0));
    }

    /**
     * 协助处理请求催办
     * 
     * @param indictSeq
     * @throws Exception
     */
    public void AssistRequestHasten(IRequestCycle cycle) throws Exception
    {
        IData condParams = getData("cond", true);
        // 调用公用方法接口，催办接口
        IDataset infos = CSViewCall.call(this, "SS.BaseCommRecordSVC.AssistRequestHasten", condParams);
        setAjax(infos);
    }

    /**
     * 协助处理请求派发
     * 
     * @param indictSeq
     * @throws Exception
     */
    @SuppressWarnings("static-access")
    public void AssistRequestSend(IRequestCycle cycle) throws Exception
    {
        IData condParams = getData("cond", true);
        IData data = new DataMap();
        Date date = new Date();

        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.MINUTE, 4 * 60);

        String assistRequestTime = new DateFormatUtils().format(date, "yyyy-MM-dd HH:mm:ss");// 请求提交时间
        String deadTime = new DateFormatUtils().format(c.getTime(), "yyyy-MM-dd HH:mm:ss"); // 请求回复时间
        String serviceTypeId = condParams.getString("SERVICE_TYPE", ""); // 业务类别
        String operateTypeId = "03001"; // 操作类型
        // 协助处理请求简述
        String assistRequestTitle = condParams.getString("SUBMIT_MODE1", "");
        // 协助处理请求内容
        String assistRequestContent = condParams.getString("SUBMIT_MODE2", "");
        // 建议
        String Pretreatment = condParams.getString("SUBMIT_MODE3", "");

        String UrgentId = condParams.getString("URGENTID", "");

        String COMPANYNAME = condParams.getString("COMPANYNAME", "");
        String SPCODE = condParams.getString("SPCODE", "");
        String FELLBACKDATE = condParams.getString("FELLBACKDATE", "");
        String FELLBACKFACT = condParams.getString("FELLBACKFACT", "");
        String REFERUSERCOUNT = condParams.getString("REFERUSERCOUNT", "");
        String SOCIALEFFECT = condParams.getString("SOCIALEFFECT", "");

        String ATTACH_LIST = condParams.getString("ATTACH_LIST", "");
        String RECORD_LIST = condParams.getString("RECORD_LIST", "");
        //
        data.put("COMPANYNAME", COMPANYNAME);
        data.put("SPCODE", SPCODE);
        data.put("FELLBACKDATE", FELLBACKDATE);
        data.put("FELLBACKFACT", FELLBACKFACT);
        data.put("REFERUSERCOUNT", REFERUSERCOUNT);
        data.put("SOCIALEFFECT", SOCIALEFFECT);

        data.put("URGENTID", UrgentId);
        data.put("ASSISTREQUESTTIME", assistRequestTime);
        data.put("DEADTIME", deadTime);
        data.put("SERVICETYPEID", serviceTypeId);
        data.put("OPERATETYPEID", operateTypeId); // ?
        data.put("ASSISTREQUESTTITLE", assistRequestTitle);
        data.put("ASSISTREQUESTCONTENT", assistRequestContent);
        data.put("PRETREATMENT", Pretreatment);
        data.put("ATTACH_LIST", ATTACH_LIST);
        data.put("RECORD_LIST", RECORD_LIST);
        IDataset infos = CSViewCall.call(this, "SS.BaseCommRecordSVC.AssistRequestSend", data); // 调用办理业务接口，不管成功失败同时调用归档接口
        setAjax(infos.getData(0));
    }

    public void downFile(IRequestCycle cycle) throws Exception
    {
        IData condParams = getData();
        // String attch[]=condParams.getString("attch","").split(",");
        // String vedio[]=condParams.getString("vedio","").split(",");
        // IDataset ATTCH=new DatasetList();
        // for(int i=0;i<attch.length;i++){
        // IData para=new DataMap();
        // para.put("attch", attch[i].split("-")[0]);
        // para.put("attch_id", attch[i].split("-")[1]);
        // ATTCH.add(para);
        // }
        // IDataset VEDIO=new DatasetList();
        // for(int i=0;i<vedio.length;i++){
        // IData para=new DataMap();
        // para.put("vedio", vedio[i].split("-")[0]);
        // para.put("vedio_id", vedio[i].split("-")[1]);
        // VEDIO.add(para);
        // }
        String URL = "ftp://10.200.120.254/";
        String attch[] = condParams.getString("attch", "").split(",");
        String vedio[] = condParams.getString("vedio", "").split(",");
        IDataset ATTCH = new DatasetList();
        for (int i = 0; i < attch.length; i++)
        {
            IData para = new DataMap();
            para.put("attch", attch[i]);
            para.put("attch_id", URL + attch[i]);
            ATTCH.add(para);
        }
        IDataset VEDIO = new DatasetList();
        for (int i = 0; i < vedio.length; i++)
        {
            IData para = new DataMap();
            para.put("vedio", vedio[i]);
            para.put("vedio_id", URL + vedio[i]);
            VEDIO.add(para);
        }
        condParams.put("attch", ATTCH);
        condParams.put("vedio", VEDIO);
        setAttch(ATTCH);
        setVedio(VEDIO);
    }

    @SuppressWarnings("unused")
    private String getSuffixByFileName(String fileName)
    {
        int index = fileName.lastIndexOf(".");
        if (index != -1)
            return fileName.substring(index);
        else
            return "";
    }

    public void init(IRequestCycle cycle) throws Exception
    {
        IData temp = new DataMap();
        setCond(temp);
    }

    /**
     * 协助请求查询
     * 
     * @create_date 2012-4-19
     */
    public void queryAssistRecord(IRequestCycle cycle) throws Exception
    {
        IData condParams = getData();
        IData info = new DataMap();
        String busyTag = condParams.getString("BUSYTAG", "RECORD");
        info.put("BUSYTAG", condParams.getString("BUSYTAG", "RECORD"));
        if (busyTag.equals("RECORD"))
        {
            // 调用公用方法接口，协助处理请求查询
            IDataset pdts = CSViewCall.call(this, "SS.BaseCommRecordSVC.AssistRequestQurey", condParams);
            ;
            info.put("INDICTSEQ", pdts.getData(0).getString("INDICTSEQ", ""));
            info.put("STATUS", pdts.getData(0).getString("STATUS", ""));
            info.put("CURRENTNODE", pdts.getData(0).getString("CURRENTNODE", ""));
            info.put("HANDLINGDEPT", pdts.getData(0).getString("HANDLINGDEPT", ""));
            info.put("HANDLINGSTAFF", pdts.getData(0).getString("HANDLINGSTAFF", ""));
            info.put("STAFFCONTACTPHONE", pdts.getData(0).getString("STAFFCONTACTPHONE", ""));
            info.put("LOGLIST", pdts.getData(0).getString("LOGLIST", ""));
        }
        else if (!busyTag.equals("RECORD"))
        {
            info.put("INDICTSEQ", condParams.getString("INDICTSEQ", ""));
            info.put("SERVICE_TYPE", URLDecoder.decode(URLDecoder.decode(condParams.getString("SERVICE_TYPE", ""), "UTF-8"), "UTF-8"));
            info.put("ASSISTREQ_TITLE", URLDecoder.decode(URLDecoder.decode(condParams.getString("ASSISTREQ_TITLE", ""), "UTF-8"), "UTF-8"));
        }
        setInfo(info);

    }

    /**
     * 协助请求日志查询
     * 
     * @create_date 2012-4-19
     */
    public void queryAssistRecordLog(IRequestCycle cycle) throws Exception
    {
        IData condParams = getData("cond", true);
        // 调用公用方法接口，协助处理请求查询
        IDataOutput pdata = CSViewCall.callPage(this, "SS.BaseCommRecordSVC.AssistRequestLogQurey", condParams, getPagination("navt"));
        setCount(pdata.getDataCount());
        setInfos(pdata.getData());
        setCond(condParams);
    }

    /**
     * 该方法 暂时没用到-------
     * 
     * @create_date 2012-4-19
     */
    public abstract void setAttch(IDataset ATTCH);

    public abstract void setCond(IData cond);

    public abstract void setCount(long count);

    public abstract void setInfo(IData Info);

    public abstract void setInfos(IDataset infos);

    public abstract void setVedio(IDataset VEDIO);
}
