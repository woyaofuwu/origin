package com.asiainfo.veris.crm.order.web.person.addresslist;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class QryBlackList extends PersonBasePage{


    /**
     * 关闭页面调用归档接口和更新表数据
     * 
     * @param cycle
     * @throws Exception
     */
    public void closePage(IRequestCycle cycle) throws Exception
    {
        IData pageData = getData();
        IDataset infos = new DatasetList();

        String indictSeq = pageData.getString("INDICTSEQ");

        // 判断已经生成服务请求标识和未归档的查询，才调用关闭查询页面归档接口
        if (indictSeq != null && ("0".equals(pageData.getString("PIGEONHOLE"))))
        {
            infos = CSViewCall.call(this, "SS.BaseCommRecordSVC.ClosePage", pageData);
        }
        setAjax(infos);
    }

    public void initPage(IRequestCycle cycle) throws Exception
    {
        IData data = getData();

        data.put("PHONE_NUM", data.getString("SERIAL_NUMBER", ""));

        setCond(data);
    }

    /**
     * 黑名单查询方法
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryBaseRecord(IRequestCycle cycle) throws Exception
    {
        IData condParams = getData("cond", true);

        IData queryData = new DataMap();

        String param1 = condParams.getString("PARA_CODE1", ""); // 手机号码
        String param2 = condParams.getString("PARA_CODE2", ""); // 查询类型
        String param3 = condParams.getString("PARA_CODE3", ""); // 开始时间
        String param4 = condParams.getString("PARA_CODE4", ""); // 结束时间
        StringBuilder operateConditions = new StringBuilder();
        
        operateConditions.append(param1);
        operateConditions.append("|");
        operateConditions.append(param2);//
        operateConditions.append("|");
        operateConditions.append(param3);
        operateConditions.append("|");
        operateConditions.append(param4);
        

        // modify by huanghui 20140707 for HAIN J2EE 参数
        queryData.put("KIND_ID", "BIP2C092_T2002092_0_0");
        queryData.put("CALLERNO", param1);
        queryData.put("SVCTYPEID", "01010549");// 服务请求分类编码
        queryData.put("CONTACTCHANNEL", "08");// 受理渠道
        queryData.put("SERVICETYPEID", "164");// 业务类别
        queryData.put("OPERATETYPEID", "01001");// 操作类型
        queryData.put("OPERATECONDITIONS", operateConditions.toString());

        IDataset infos = CSViewCall.call(this, "SS.BaseCommRecordSVC.subBussQureySerive", queryData);

        IDataset results = infos.getData(0).getDataset("PDTS_INFO");
        IData result = infos.getData(0).getData("PAGE_INFO");
        result.put("CALLERNO", param1);
        IData reportPage = infos.getData(0).getData("REPORTPAGE");
        // getStaticValue修改
//        IDataset res = getStaticValue(results);
        setCond(condParams);
        setInfos(results);
        setResult(result);
        setReportPage(reportPage);
    }

    /**
     * 黑名单查询方法,查询剩余的记录
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryOtherRecord(IRequestCycle cycle) throws Exception
    {
        IData pageData = getData();

        int currPage = getPagination().getCurrent();

        pageData.put("CURR_PAGE", currPage);

        IDataset infos = CSViewCall.call(this, "SS.BaseCommRecordSVC.subUnfinishBussQuerySerive", pageData);

        IData result = infos.getData(0).getData("PAGE_INFO");
        IDataset results = infos.getData(0).getDataset("PDTS_INFO");
        IData reportPage = result.getData("REPORTPAGE");
        // getStaticValue修改
//        IDataset res = getStaticValue(results);
        setResult(result);
        setInfos(results);
        setReportPage(reportPage);
    }

    // 将页面getStaticValue写到view层java中
//    public IDataset getStaticValue(IDataset res) throws Exception
//    {
//        for (int i = 0; i < res.size(); i++)
//        {
//            IData da = new DataMap();
//            da = res.getData(i);
//            da.put("RSRV_STR0", pageutil.getStaticValue("MOBILEMARKET_BLACK_RESULT", da.getString("RSRV_STR0")));
//            da.put("RSRV_STR1", pageutil.getStaticValue("MOBILEMARKET_BLACK_STATE", da.getString("RSRV_STR1")));
//        }
//        return res;
//    }

    public abstract void setCond(IData cond);

    public abstract void setInfos(IDataset infos);

    public abstract void setReportPage(IData reportPage);

    public abstract void setResult(IData result);

}
