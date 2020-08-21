
package com.asiainfo.veris.crm.order.soa.person.busi.plat.officedata;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.PlatException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;

public class PlatOfficeDataSVC extends CSBizService
{

    /**
     * 删除SP业务
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public IData delSPBiz(IData param) throws Exception
    {
        IData resultMap = new DataMap();
        IDataset delCondition = param.getDataset("CONDITION");
        if (delCondition == null || delCondition.isEmpty())
        {
            CSAppException.apperr(PlatException.CRM_PLAT_74, "删除条件不能为空");
        }
        int[] delResult = PlatOfficeDataBean.delSPBiz(delCondition);
        resultMap.put("RESULT", delResult);
        return resultMap;
    }

    /**
     * 删除SP信息
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public IData delSPInfo(IData param) throws Exception
    {
        IData resultMap = new DataMap();
        IDataset delCondition = param.getDataset("CONDITION");
        if (delCondition == null || delCondition.isEmpty())
        {
            CSAppException.apperr(PlatException.CRM_PLAT_74, "删除条件不能为空");
        }
        int[] delResult = PlatOfficeDataBean.delSPInfo(delCondition);
        resultMap.put("RESULT", delResult);
        return resultMap;
    }

    /**
     * 批量导入SP业务信息
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public IData importSpBizInfo(IData param) throws Exception
    {
        PlatOfficeDataBean.importSpBiz(param.getString("FILE_NAME"), param.getDataset("SP_BIZ"), "CRM");
        return new DataMap();
    }

    /**
     * 批量导入SP信息
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public IData importSpInfo(IData param) throws Exception
    {
        PlatOfficeDataBean.importSpInfo(param.getString("FILE_NAME"), param.getDataset("SP_INFO"), "CRM");
        return new DataMap();
    }

    /**
     * 查询SP业务批量信息
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset queryBizBat(IData param) throws Exception
    {
        return PlatOfficeDataBean.queryBizBat(param, this.getPagination());
    }

    /**
     * 查询SPBiz导入详情
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset queryBizBatDtl(IData param) throws Exception
    {
        return PlatOfficeDataBean.queryBizBatDtl(param, this.getPagination());
    }

    public IDataset querySPBiz(IData param) throws Exception
    {
        return PlatOfficeDataBean.querySPBiz(param, this.getPagination());
    }

    /**
     * 查询SP信息
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset querySPInfo(IData param) throws Exception
    {
        return PlatOfficeDataBean.querySPInfo(param, this.getPagination());
    }

    /**
     * 查询SP批量信息
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset querySPInfoBat(IData param) throws Exception
    {
        return PlatOfficeDataBean.querySPInfoBat(param, this.getPagination());
    }

    /**
     * 查询SP导入详情
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset querySPInfoBatDtl(IData param) throws Exception
    {
        return PlatOfficeDataBean.querySPInfoBatDtl(param, this.getPagination());
    }

    /**
     * 保存SP业务信息
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public IData saveSPBiz(IData param) throws Exception
    {
        IData result = new DataMap();
        boolean flag = PlatOfficeDataBean.saveSPBiz(param);
        result.put("RESULT", flag);
        return result;
    }

    /**
     * 保存SP信息
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public IData saveSPInfo(IData param) throws Exception
    {
        IData result = new DataMap();
        boolean flag = PlatOfficeDataBean.saveSPInfo(param);
        result.put("RESULT", flag);
        return result;

    }
}
