
package com.asiainfo.veris.crm.order.soa.frame.bcf.query.product;

import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;

public final class USvcStateInfoQry
{
    /**
     * 根据服务标识和服务状态查询服务指令类型
     * 
     * @param svcId
     * @param stateCode
     * @return
     * @throws Exception
     */
    public static String getSvcIntfModeBySvcIdStateCode(String svcId, String stateCode) throws Exception
    {
        IDataset prodStaData = UpcCall.qryOfferFuncStaByAnyOfferIdStatus(svcId,BofConst.ELEMENT_TYPE_CODE_SVC, stateCode);
        
        if(IDataUtil.isEmpty(prodStaData))
        {
            return "";
        }

        return prodStaData.getData(0).getString("INTF_MODE");
        
    }

    /**
     * 根据服务标识和服务状态查询服务状态名称
     * 
     * @param svcId
     * @param stateCode
     * @return
     * @throws Exception
     */
    public static String getSvcStateNameBySvcIdStateCode(String svcId, String stateCode) throws Exception
    {
        IDataset prodStaData = UpcCall.qryOfferFuncStaByAnyOfferIdStatus(svcId,BofConst.ELEMENT_TYPE_CODE_SVC, stateCode);
        
        if(IDataUtil.isEmpty(prodStaData))
        {
            return "";
        }
        else
        {
            return prodStaData.getData(0).getString("STATUS_NAME");
        }
        
    }

    /**
     * 根据服务标识和服务状态查询服务状态配置信息
     * 
     * @param serviceId
     * @param stateCodeset
     * @return
     * @throws Exception
     */
    public static IDataset qryStateNameBySvcIdStateCode(String serviceId, String stateCodeset) throws Exception
    {
        if (StringUtils.isBlank(stateCodeset))
        {
            return new DatasetList();
        }
        
        IDataset prodStaData = UpcCall.qryOfferFuncStaByAnyOfferIdStatus(serviceId, BofConst.ELEMENT_TYPE_CODE_SVC, null);
        
        if (IDataUtil.isEmpty(prodStaData))
        {
            return prodStaData;
        }
        
        for (int i = prodStaData.size()-1; i >= 0; i--)
        {
            if (StringUtils.indexOf(stateCodeset, prodStaData.getData(i).getString("STATE_CODE")) == -1)
            {
                prodStaData.remove(i);
            }
        }

        return prodStaData;
    }

    /**
     * 根据服务标识查询服务配置信息
     * 
     * @param serviceId
     * @return
     * @throws Exception
     */
    public static IDataset qrySvcStateBySvcId(String serviceId) throws Exception
    {
//        IData param = new DataMap(); 
//        param.put("SERVICE_ID", serviceId);
// 
//        return Dao.qryByCode("TD_S_SERVICESTATE", "SEL_SERVICESTATE", param, Route.CONN_CRM_CEN);
    	return UpcCall.qryOfferFuncStaByAnyOfferIdStatus(serviceId,BofConst.ELEMENT_TYPE_CODE_SVC, null);
    }
}
