
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.interboss;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.util.Utility;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class CostDiscountQrySVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    public IDataset qryContDiscntInfo(IData input) throws Exception
    {
        CostDiscntQry bean = new CostDiscntQry();
        IDataset result = bean.qryContDiscntInfo(input, getPagination());

        if (result.size() < 1)
        {
            Utility.error("获取有价卡成本折让信息无数据！");
        }
        return result;
    }

    public IDataset qryContPara(IData input) throws Exception
    {

        if ("getCommparaInfoByAttr".equals(input.getString("TAG", "")))
        {
            IData indata = new DataMap();
            indata.put("SQL_REF", "SEL_BY_ATTR_CODE");
            indata.put("TABLE_NAME", "TD_S_COMMPARA");
            indata.put("SUBSYS_CODE", "CSM");
            indata.put("PARAM_ATTR", "1268");
            indata.put("PARA_CODE", "2");
            indata.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());

            return new CostDiscntQry().qryActParam(indata);
        }
        else if ("getAreaCode".equals(input.getString("TAG", "")))
        {
            IData indata = new DataMap();
            return new CostDiscntQry().qryArea(input.getString("cond_EPARCHY_CODE", ""));
        }
        else
            return null;
    }

    // public IDataset getCommparaInfoByAttr(IData input) throws Exception
    // {
    // IData indata = new DataMap();
    // indata.put("SQL_REF", "SEL_BY_ATTR_CODE");
    // indata.put("TABLE_NAME", "TD_S_COMMPARA");
    // indata.put("SUBSYS_CODE", "CSM");
    // indata.put("PARAM_ATTR", "1268");
    // indata.put("PARA_CODE", "2");
    // // indata.put("EPARCHY_CODE", bizContext.getTradeEparchyCode());
    // // BizDAO dao = CSAppEntity.getBizDAO(bizContext);
    // // IDataset set = dao.queryListByCodeCode ( "TD_S_COMMPARA", "SEL_BY_PARAATTR", indata);
    // return null;
    // }
}
