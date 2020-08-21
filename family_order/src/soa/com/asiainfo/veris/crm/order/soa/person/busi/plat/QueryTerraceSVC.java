
package com.asiainfo.veris.crm.order.soa.person.busi.plat;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.ParamException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.MSpInfoQry;

public class QueryTerraceSVC extends CSBizService
{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public IData SPQuery(IData data) throws Exception
    {
        /*
         * TRADE_STAFF_ID(工号)--必输 BIZ_NAME（业务名称）--必输 SP_NAME（企业名称） BIZ_TYPE_CODE(业务类型编码) MAX_RETRUE(最大返回记录数)--必输 输出的参数
         * SP_CODE 企业代码 SP_NAME 企业名称 BIZ_CODE 业务代码 BIZ_NAME 业务名称 BIZ_TYPE_CODE 业务类型编码 PRICE 业务资费 BIZ_DESC 业务描述 BIZ_TYPE
         * 平台名称
         */
        String tradeStaffId = IDataUtil.chkParam(data, "TRADE_STAFF_ID");
        String bizName = IDataUtil.chkParam(data, "BIZ_NAME");
        String maxRetrue = IDataUtil.chkParam(data, "MAX_RETRUE");

        String spName = data.getString("SP_NAME", "");
        String bizTypeCode = data.getString("BIZ_TYPE_CODE", "");
        if (StringUtils.isEmpty(tradeStaffId))
        {
            CSAppException.apperr(ParamException.CRM_PARAM_20);
        }
        IDataset ds = MSpInfoQry.queryTerrace(spName, bizName, bizTypeCode, maxRetrue);
        if (IDataUtil.isEmpty(ds))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "未查询到数据！请核对参数后查询！");
        }

        return ds.toData();
    }

}
