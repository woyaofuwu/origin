
package com.asiainfo.veris.crm.order.web.person.plat.officedata;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.parser.ExcelConfig;
import com.ailk.common.util.parser.ImpExpUtil;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.PlatException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;

/**
 * 平台业务局数据管理
 * 
 * @author xiekl
 */
public abstract class PlatOfficeDataMgr extends CSBasePage
{
    public void delSPBiz(IRequestCycle cycle) throws Exception
    {
        IData param = this.getData();
        String bizCodes = this.getParameter("BIZ_CODES");
        String[] bizCodeArray = bizCodes.split(",");

        if (bizCodeArray != null && bizCodeArray.length > 0)
        {
            IDataset deleteBizs = new DatasetList();
            for (String temp : bizCodeArray)
            {
                String[] biz = temp.split("_SPLIT_");
                IData spbiz = new DataMap();
                spbiz.put("SP_CODE", biz[0]);
                spbiz.put("BIZ_CODE", biz[1]);
                spbiz.put("BIZ_STATUS", biz[2]);
                spbiz.put("END_DATE", SysDateMgr.getSysDate());
                spbiz.put("UPDATE_TIME", SysDateMgr.getSysTime());
                spbiz.put("UPDATE_STAFF_ID", this.getVisit().getStaffId());
                spbiz.put("UPDATE_DEPART_ID", this.getVisit().getDepartId());
                deleteBizs.add(spbiz);
            }

            param.put("CONDITION", deleteBizs);
            IDataset resultList = CSViewCall.call(this, "SS.PlatOfficeDataSVC.delSPBiz", param);
            this.querySPBiz(cycle);
        }
        else
        {
            CSViewException.apperr(PlatException.CRM_PLAT_74, "没有选择要删除的记录!");
        }

        // this.redirectToMsg("业务信息删除成功", this.getPageName(), "querySPBiz");
    }

    public void delSPInfo(IRequestCycle cycle) throws Exception
    {
        IData param = this.getData();
        String bizCodes = this.getParameter("BIZ_CODES");
        String[] bizCodeArray = bizCodes.split(",");

        if (bizCodeArray != null && bizCodeArray.length > 0)
        {
            IDataset deleteBizs = new DatasetList();
            for (String temp : bizCodeArray)
            {
                String[] biz = temp.split("_SPLIT_");
                IData spbiz = new DataMap();
                spbiz.put("SP_ID", biz[0]);
                spbiz.put("SP_STATUS", biz[1]);
                spbiz.put("START_DATE", biz[2].substring(0, 10));
                spbiz.put("END_DATE", SysDateMgr.getSysDate());
                spbiz.put("UPDATE_TIME", SysDateMgr.getSysTime());
                spbiz.put("UPDATE_STAFF_ID", this.getVisit().getStaffId());
                spbiz.put("UPDATE_DEPART_ID", this.getVisit().getDepartId());
                deleteBizs.add(spbiz);
            }

            param.put("CONDITION", deleteBizs);
            IDataset resultList = CSViewCall.call(this, "SS.PlatOfficeDataSVC.delSPInfo", param);
            this.querySPInfo(cycle);
        }
        else
        {
            CSViewException.apperr(PlatException.CRM_PLAT_74, "没有选择要删除的记录!");
        }

        // this.redirectToMsg("业务信息删除成功", this.getPageName(), "querySPBiz");
    }

    public void importSPBiz(IRequestCycle cycle) throws Exception
    {
        IData data = this.getData();

        String fileId = data.getString("FILE_ID");

        ImpExpUtil.getImpExpManager().getFileAction().setVisit(getVisit());

        IData array = ImpExpUtil.beginImport(null, fileId, ExcelConfig.getSheets("import/bat/SPBizImport.xml"));
        IDataset[] suc = (IDataset[]) array.get("right");// 解析成功的数据
        IDataset[] err = (IDataset[]) array.get("error");// 解析失败的数据
        int sucCount = array.getInt("rightCount");// 解析成功的数据总条数
        int errCount = array.getInt("errorCount");// 解析失败的数据总条数
        IDataset bizInfos = suc[0];

        IData param = new DataMap();
        param.put("FILE_NAME", fileId);
        param.put("SP_BIZ", bizInfos);
        param.put("USER_EPARCHY_CODE", this.getTradeEparchyCode());

        CSViewCall.call(this, "SS.PlatOfficeDataSVC.importSpBizInfo", param);

        setCondition(this.getData("cond"));

    }

    public void importSPInfo(IRequestCycle cycle) throws Exception
    {
        IData data = this.getData();

        String fileId = data.getString("FILE_ID");

        ImpExpUtil.getImpExpManager().getFileAction().setVisit(getVisit());

        IData array = ImpExpUtil.beginImport(null, fileId, ExcelConfig.getSheets("import/bat/SPInfoImport.xml"));
        IDataset[] suc = (IDataset[]) array.get("right");// 解析成功的数据
        IDataset[] err = (IDataset[]) array.get("error");// 解析失败的数据
        int sucCount = array.getInt("rightCount");// 解析成功的数据总条数
        int errCount = array.getInt("errorCount");// 解析失败的数据总条数

        IDataset spInfos = suc[0];
        IData param = new DataMap();
        param.put("FILE_NAME", fileId);
        param.put("SP_INFO", spInfos);
        param.put("USER_EPARCHY_CODE", this.getTradeEparchyCode());

        CSViewCall.call(this, "SS.PlatOfficeDataSVC.importSpInfo", param);
        setCondition(this.getData("cond"));
    }

    /**
     * 查询SP业务
     * 
     * @param cycle
     * @throws Exception
     */
    public void querySPBiz(IRequestCycle cycle) throws Exception
    {
        IData param = this.getData("cond", true);
        IDataOutput resultList = CSViewCall.callPage(this, "SS.PlatOfficeDataSVC.querySPBiz", param, this.getPagination("queryNav"));
        this.setInfos(resultList.getData());
        this.setPageCount(resultList.getDataCount());
        this.setCondition(param);
    }

    /**
     * 查询SP批量信息
     * 
     * @param cycle
     * @throws Exception
     */
    public void querySPBizBat(IRequestCycle cycle) throws Exception
    {
        IData param = this.getData("cond", true);
        if (StringUtils.isNotBlank(param.getString("IMPORT_DATE_START")))
        {
            param.put("IMPORT_DATE_START", param.getString("IMPORT_DATE_START") + SysDateMgr.START_DATE_FOREVER);

        }
        if (StringUtils.isNotBlank(param.getString("IMPORT_DATE_END")))
        {
            param.put("IMPORT_DATE_END", param.getString("IMPORT_DATE_END") + SysDateMgr.END_DATE);
        }

        IDataOutput resultList = CSViewCall.callPage(this, "SS.PlatOfficeDataSVC.queryBizBat", param, this.getPagination("queryNav"));
        this.setInfos(resultList.getData());
        this.setPageCount(resultList.getDataCount());
        this.setCondition(param);
    }

    /**
     * 查询SP业务批量详细信息
     * 
     * @param cycle
     * @throws Exception
     */
    public void querySPBizBatDetail(IRequestCycle cycle) throws Exception
    {
        IData param = this.getData();
        IDataOutput resultList = CSViewCall.callPage(this, "SS.PlatOfficeDataSVC.queryBizBatDtl", param, this.getPagination("queryNav"));
        this.setInfos(resultList.getData());
        this.setPageCount(resultList.getDataCount());
        this.setCondition(param);
    }

    /**
     * 查询SP信息
     * 
     * @param cycle
     * @throws Exception
     */
    public void querySPInfo(IRequestCycle cycle) throws Exception
    {
        IData param = this.getData("cond", true);
        IDataOutput resultList = CSViewCall.callPage(this, "SS.PlatOfficeDataSVC.querySPInfo", param, this.getPagination("queryNav"));
        this.setInfos(resultList.getData());
        this.setPageCount(resultList.getDataCount());
        this.setCondition(param);
    }

    /**
     * 查询SP批量信息
     * 
     * @param cycle
     * @throws Exception
     */
    public void querySPInfoBat(IRequestCycle cycle) throws Exception
    {
        IData param = this.getData("cond", true);

        if (StringUtils.isNotBlank(param.getString("IMPORT_DATE_START")))
        {
            param.put("IMPORT_DATE_START", param.getString("IMPORT_DATE_START") + SysDateMgr.START_DATE_FOREVER);

        }
        if (StringUtils.isNotBlank(param.getString("IMPORT_DATE_END")))
        {
            param.put("IMPORT_DATE_END", param.getString("IMPORT_DATE_END") + SysDateMgr.END_DATE);
        }
        IDataOutput resultList = CSViewCall.callPage(this, "SS.PlatOfficeDataSVC.querySPInfoBat", param, this.getPagination("queryNav"));
        this.setInfos(resultList.getData());
        this.setPageCount(resultList.getDataCount());
        this.setCondition(param);
    }

    /**
     * 查询SP批量导入详细信息
     * 
     * @param cycle
     * @throws Exception
     */
    public void querySPInfoBatDetail(IRequestCycle cycle) throws Exception
    {
        IData param = this.getData();
        IDataOutput resultList = CSViewCall.callPage(this, "SS.PlatOfficeDataSVC.querySPInfoBatDtl", param, this.getPagination("queryNav"));
        this.setInfos(resultList.getData());
        this.setPageCount(resultList.getDataCount());
        this.setCondition(param);
    }

    /**
     * 保存SP信息
     * 
     * @param cycle
     * @throws Exception
     */
    public void saveSPBiz(IRequestCycle cycle) throws Exception
    {
        IData param = this.getData("cond", true);
        this.setUpdateStaffInfo(param);

        IDataset resultList = CSViewCall.call(this, "SS.PlatOfficeDataSVC.saveSPBiz", param);
        if (resultList != null && !resultList.isEmpty())
        {
            IData result = resultList.getData(0);
            boolean saveFlag = result.getBoolean("RESULT");
            if (saveFlag)
            {
                this.setAjax("RESULT", "1");
            }
            else
            {
                this.setAjax("RESULT", "0");
            }
        }
        else
        {
            this.setAjax("RESULT", "0");
        }
    }

    /**
     * 保存SP信息
     * 
     * @param cycle
     * @throws Exception
     */
    public void saveSPInfo(IRequestCycle cycle) throws Exception
    {
        IData param = this.getData("cond", true);
        this.setUpdateStaffInfo(param);

        IDataset resultList = CSViewCall.call(this, "SS.PlatOfficeDataSVC.saveSPInfo", param);
        if (resultList != null && !resultList.isEmpty())
        {
            IData result = resultList.getData(0);
            boolean saveFlag = result.getBoolean("RESULT");
            if (saveFlag)
            {
                this.setAjax("RESULT", "1");
            }
            else
            {
                this.setAjax("RESULT", "0");
            }
        }
        else
        {
            this.setAjax("RESULT", "0");
        }
    }

    public abstract void setCondition(IData condition);

    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);

    public abstract void setPageCount(long count);

    public void setUpdateStaffInfo(IData param) throws Exception
    {
        param.put("UPDATE_STAFF_ID", getVisit().getStaffId());
        param.put("UPDATE_DEPART_ID", getVisit().getDepartId());
        param.put("UPDATE_TIME", SysDateMgr.getSysTime());
    }
}
