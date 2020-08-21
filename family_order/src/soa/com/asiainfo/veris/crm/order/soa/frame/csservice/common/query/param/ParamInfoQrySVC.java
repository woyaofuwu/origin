
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class ParamInfoQrySVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    public static IDataset getCommparaByParamattr(IData input) throws Exception
    {
        String subsysCode = input.getString("SUBSYS_CODE");
        String paramAttr = input.getString("PARAM_ATTR");
        String paramCode = input.getString("PARAM_CODE");
        String eparchyCode = input.getString("EPARCHY_CODE");
        return ParamInfoQry.getCommparaByParamattr(subsysCode, paramAttr, paramCode, eparchyCode);
    }

    /**
     * 查询commpara表
     * 
     * @param param
     * @return
     * @throws Exception
     *             wangjx 2013-7-25
     */
    public IDataset getCommparaByCode(IData param) throws Exception
    {
        String subsysCode = param.getString("SUBSYS_CODE");
        String paramAttr = param.getString("PARAM_ATTR");
        String paramCode = param.getString("PARAM_CODE", "");
        String eparchyCode = param.getString("EPARCHY_CODE", "");

        IDataset dataset = ParamInfoQry.getCommparaByCode(subsysCode, paramAttr, paramCode, eparchyCode);

        return dataset;
    }

    public IDataset getCommparaInfoBy5(IData param) throws Exception
    {

        String subSysCode = param.getString("SUBSYS_CODE", "");
        String paramAttr = param.getString("PARAM_ATTR", "");
        String paramCode = param.getString("PARAM_CODE", "");
        String paraCode1 = param.getString("PARA_CODE1", "");
        String eparchCode = param.getString("EPARCHY_CODE", "");
        IDataset data = ParamInfoQry.getCommparaInfoBy5(subSysCode, paramAttr, paramCode, paraCode1, eparchCode);
        return data;
    }

    /**
     * PARAM_ATTR、SYBSYS_CODE、PARAM_CODE
     * 
     * @param iData
     * @return
     * @throws Exception
     */
    public IDataset getCommparaInfoByAttrAndParam(IData iData) throws Exception
    {
        return ParamInfoQry.getCommparaInfoByAttrAndParam(iData);
    }

    /**
     * 获取配置集团预存兑奖比例 根据是否专线
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset getGrpPreSavePrizeRateByCon(IData input) throws Exception
    {
        String qtickType = input.getString("QTICK_TYPE");
        IDataset data = ParamInfoQry.getGrpPreSavePrizeRateByCon(qtickType);

        return data;
    }

    /**
     * @author fuzn
     * @date 2013-03-08
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset getSelOnlyByAttrOrdered(IData param) throws Exception
    {
        String subSysCode = param.getString("SUBSYS_CODE", "");
        String paramAttr = param.getString("PARAM_ATTR", "");
        String eparchCode = param.getString("EPARCHY_CODE", "");
        IDataset data = ParamInfoQry.getSelOnlyByAttrOrdered(subSysCode, paramAttr, eparchCode, null);
        return data;
    }
}
