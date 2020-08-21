
package com.asiainfo.veris.crm.order.web.person.sparkplansmgr;

import java.io.File;
import java.io.FileInputStream;

import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.parser.ExcelConfig;
import com.ailk.common.util.parser.ImpExpUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class SparkPlansDepartDistribute extends PersonBasePage
{

    public void assignSparkPlans(IRequestCycle cycle) throws Exception
    {
        IData pageData = getData();
        CSViewCall.call(this, "SS.SparkPlansMgrSVC.assignSparkPlans", pageData);
    }

    public void financeConfirminit(IRequestCycle cycle) throws Exception
    {
        IData param = getData();
        String outStaffId = param.getString("STAFF_ID");
        // 处理传入的数据，只显示批次号
        param.put("STAFF_ID", getVisit().getStaffId());
        param.put("STAFF_NAME", getVisit().getStaffName());
        param.put("DEPART_NAME", getVisit().getDepartName());
        param.put("OUT_STAFF_ID", outStaffId);
        String tmp = StaticUtil.getStaticValue(getVisit(), "TD_M_STAFF", "STAFF_ID", "DEPART_ID", outStaffId);
        String outName = StaticUtil.getStaticValue(getVisit(), "TD_M_DEPART", "DEPART_ID", "DEPART_NAME", tmp);
        param.put("OUT_DEPART_NAME", outName);
        param.put("IN_STAFF_ID", param.getString("STAFF_ID_F"));
        tmp = StaticUtil.getStaticValue(getVisit(), "TD_M_STAFF", "STAFF_ID", "DEPART_ID", param.getString("STAFF_ID_F"));
        String inName = StaticUtil.getStaticValue(getVisit(), "TD_M_DEPART", "DEPART_ID", "DEPART_NAME", tmp);
        param.put("IN_DEPART_NAME", inName);
        param.put("OPER_TITLE", param.getString("PACKAGE_NAME"));

        param.put("PRINT_DATE", SysDateMgr.getSysTime());

        setInfo(param);

        IDataset rusult = new DatasetList();
        rusult.add(param);
        setInfos(rusult);
    }

    public void importAssignInfo(IRequestCycle cycle) throws Exception
    {
        IData pageData = getData();
        String fileId = pageData.getString("FILE_ID");
        ImpExpUtil.getImpExpManager().getFileAction().setVisit(getVisit());

        IData array = ImpExpUtil.beginImport(null, fileId, ExcelConfig.getSheets("import/sparkplansmgr/SparkPlansAssignImport.xml"));
        IDataset[] suc = (IDataset[]) array.get("right");// 解析成功的数据
        IDataset[] err = (IDataset[]) array.get("error");// 解析失败的数据
        int sucCount = array.getInt("rightCount");// 解析成功的数据总条数
        int errCount = array.getInt("errorCount");// 解析失败的数据总条数

        IDataset importDataList = suc[0];
        pageData.put("IMPORT_DATA_LIST", importDataList);
        IData result = CSViewCall.call(this, "SS.SparkPlansMgrSVC.importAssignInfo", pageData).getData(0);

        IDataset succList = result.getDataset("SUCCLIST");
        IDataset failList = result.getDataset("FAILLIST");
        failList.addAll(err[0]);

        IData ajaxData = new DataMap();
        if (IDataUtil.isNotEmpty(failList))
        {
            String fileIdE = ImpExpUtil.getImpExpManager().getFileAction().createFileId();
            String fileName = fileIdE + ".xls";
            File errorFile = ImpExpUtil.writeDataToFile("xls", new IDataset[]
            { failList }, "personserv", fileIdE, null, "export/sparkplansmgr/SparkPlansAssignImportFail.xml");
            String errorFileId = ImpExpUtil.getImpExpManager().getFileAction().upload(new FileInputStream(errorFile), "personserv", "upload/export", fileName);
            String errorUrl = ImpExpUtil.getDownloadPath(errorFileId, fileName);

            ajaxData.put("FAIL_SIZE", failList.size());
            ajaxData.put("ERROR_URL", errorUrl);
        }
        ajaxData.put("SUCC_SIZE", succList.size());
        ajaxData.put("TOTAL_SIZE", failList.size() + succList.size());

        setAjax(ajaxData);
    }

    public void importBackInfo(IRequestCycle cycle) throws Exception
    {
        IData pageData = getData();
        String fileId = pageData.getString("FILE_ID");
        ImpExpUtil.getImpExpManager().getFileAction().setVisit(getVisit());

        IData array = ImpExpUtil.beginImport(null, fileId, ExcelConfig.getSheets("import/sparkplansmgr/SparkPlansBackImport.xml"));
        IDataset[] suc = (IDataset[]) array.get("right");// 解析成功的数据
        IDataset[] err = (IDataset[]) array.get("error");// 解析失败的数据
        int sucCount = array.getInt("rightCount");// 解析成功的数据总条数
        int errCount = array.getInt("errorCount");// 解析失败的数据总条数

        IDataset importDataList = suc[0];
        pageData.put("IMPORT_DATA_LIST", importDataList);
        IData result = CSViewCall.call(this, "SS.SparkPlansMgrSVC.importBackInfo", pageData).getData(0);

        IDataset succList = result.getDataset("SUCCLIST");
        IDataset failList = result.getDataset("FAILLIST");
        failList.addAll(err[0]);

        IData ajaxData = new DataMap();
        if (IDataUtil.isNotEmpty(failList))
        {
            String fileIdE = ImpExpUtil.getImpExpManager().getFileAction().createFileId();
            String fileName = fileIdE + ".xls";
            File errorFile = ImpExpUtil.writeDataToFile("xls", new IDataset[]
            { failList }, "personserv", fileIdE, null, "export/sparkplansmgr/SparkPlansBackImportFail.xml");
            String errorFileId = ImpExpUtil.getImpExpManager().getFileAction().upload(new FileInputStream(errorFile), "personserv", "upload/export", fileName);
            String errorUrl = ImpExpUtil.getDownloadPath(errorFileId, fileName);

            ajaxData.put("FAIL_SIZE", failList.size());
            ajaxData.put("ERROR_URL", errorUrl);
        }
        ajaxData.put("SUCC_SIZE", succList.size());
        ajaxData.put("TOTAL_SIZE", failList.size() + succList.size());

        setAjax(ajaxData);
    }

    public void queryAssignLog(IRequestCycle cycle) throws Exception
    {
        IData data = getData();

        IDataOutput dataCount = CSViewCall.callPage(this, "SS.SparkPlansMgrSVC.queryAssignLog", data, getPagination("nav"));
        setCount(dataCount.getDataCount());
        setInfos(dataCount.getData());
    }

    public void querysparkPlans(IRequestCycle cycle) throws Exception
    {
        IData pageData = getData();
        IDataOutput dataCount = CSViewCall.callPage(this, "SS.SparkPlansMgrSVC.querysparkPlans", pageData, getPagination());
        setInfos(dataCount.getData());
        setCount(dataCount.getDataCount());
    }

    public abstract void setCount(long count);

    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);
}
