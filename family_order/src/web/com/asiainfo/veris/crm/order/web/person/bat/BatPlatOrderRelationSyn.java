
package com.asiainfo.veris.crm.order.web.person.bat;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.util.parser.ExcelConfig;
import com.ailk.common.util.parser.ImpExpUtil;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.BatException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;

/**
 * 批量同步各类平台业务
 * 
 * @author songxw
 */
public abstract class BatPlatOrderRelationSyn extends CSBasePage
{
    public void importPlatOrderRelation(IRequestCycle cycle) throws Exception
    {
        IData data = this.getData();

        String fileId = data.getString("FILE_ID");

        ImpExpUtil.getImpExpManager().getFileAction().setVisit(getVisit());

        IData array = ImpExpUtil.beginImport(null, fileId, ExcelConfig.getSheets("import/bat/PlatOrderRelationSyn.xml"));
        IDataset[] suc = (IDataset[]) array.get("right");// 解析成功的数据

        IDataset platdatas = suc[0];
        IData param = new DataMap();
        param.put("FILE_NAME", fileId);
        param.put("PLAT_SYN", platdatas);
        param.put("USER_EPARCHY_CODE", this.getTradeEparchyCode());

        CSViewCall.call(this, "SS.PlatOrderRelationSVC.importPlatOrderRelation", param);
        setCondition(this.getData("cond"));
    }

    /**
     * 查询平台业务订购关系同步批量信息
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryPlatOrderRelationBat(IRequestCycle cycle) throws Exception
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

        IDataOutput resultList = CSViewCall.callPage(this, "SS.PlatOrderRelationSVC.queryPlatOrderRelationBat", param, this.getPagination("queryNav"));
        this.setInfos(resultList.getData());
        this.setPageCount(resultList.getDataCount());
        this.setCondition(param);
    }
    
    /**
     * 查询平台业务订购关系同步批量详细信息
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryPlatOrderRelationBatDtl(IRequestCycle cycle) throws Exception
    {
        IData param = this.getData();
        IDataOutput resultList = CSViewCall.callPage(this, "SS.PlatOrderRelationSVC.queryPlatOrderRelationBatDtl", param, this.getPagination("queryNav"));
        this.setInfos(resultList.getData());
        this.setPageCount(resultList.getDataCount());
        this.setCondition(param);
    }
    
    /**
     * 启动批量业务信息
     * 
     * @param cycle
     * @throws Exception
     */
    public void startBatDeals(IRequestCycle cycle) throws Exception
    {
        String importIds = getParameter("IMPORT_IDS");
        String[] importIdSet = StringUtils.split(importIds, ",");
        for (int i = 0, size = importIdSet.length; i < size; i++)
        {
            String importId = importIdSet[i];
            IData data = new DataMap();
            data.put("IMPORT_ID", importId);
            data.put(Route.USER_EPARCHY_CODE, getTradeEparchyCode());
            IDataset batDataSet = CSViewCall.call(this, "SS.PlatOrderRelationSVC.qryPlatDataBatByPK", data);
            IData batData = batDataSet.getData(0);
            if (IDataUtil.isEmpty(batData))
            {
                CSViewException.apperr(BatException.CRM_BAT_48, importId);
            }
            if (!(batData.getString("DEAL_FLAG")).equals("0"))
            {
            	CSViewException.apperr(BatException.CRM_BAT_35);
            }
            String showInfo = "批量数据启动成功!";
            CSViewCall.call(this, "SS.PlatOrderRelationSVC.startBatDeal", data);       
            IData idata = new DataMap();
            idata.put("result", showInfo);
            setAjax(idata);
        }

        queryPlatOrderRelationBat(cycle);// 初始化批量查询页面
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
