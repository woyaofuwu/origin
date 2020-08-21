
package com.asiainfo.veris.crm.order.soa.group.groupintf.querytrade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.group.common.base.CSQryBizService;
import com.asiainfo.veris.crm.order.soa.group.largessfluxmgrbean.LargessFluxGrpMainSVC;

public class GrpSubLargessFluxMgrQuerySVC extends CSQryBizService
{
    private static final long serialVersionUID = 1L;
    
    /**
     * xuyt 集团畅享流量分配记录查询接口
     *
     * @param data
     * @param pg
     * @return
     */
    public IDataset QueryInfos(IData data) throws Throwable
    {
        String enet_info_query = data.getString("ENET_INFO_QUERY");
        String serial_number = data.getString("SERIAL_NUMBER");
        if (StringUtils.isBlank(enet_info_query))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "查询方式不可为空！");
        }
        if (StringUtils.isBlank(serial_number))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "查询号码不可为空！");
        }
        
        if(!("0".equals(enet_info_query) || "1".equals(enet_info_query)))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "查询方式编码无效！");
        }
        
        
        if("0".equals(enet_info_query))
        {
            data.put("SERIAL_NUMBER_A", serial_number); //0：主分配编码
        }else
        {
            data.put("SERIAL_NUMBER_B", serial_number); //1：被分配号码
        }
        
        IDataset ds = LargessFluxGrpMainSVC.qryUserGrpSubGfffInfoFLUX(data, getPagination());
        if(IDataUtil.isEmpty(ds))
        {
         // 返回参数设置
            IData result = new DataMap();
            result.put("X_RESULTCODE", "0"); // 正常状态
            result.put("X_RESULTINFO", "查询集团畅享流量分配记录无数据！"); // 正常状态描述
            return IDataUtil.idToIds(result);
        }else
        {
            return ds; 
        }
    }
}
