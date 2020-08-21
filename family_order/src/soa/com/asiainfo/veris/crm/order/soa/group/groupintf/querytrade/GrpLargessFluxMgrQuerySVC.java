
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

public class GrpLargessFluxMgrQuerySVC extends CSQryBizService
{
    private static final long serialVersionUID = 1L;

    /**
     * xuyt 集团产品编码查询接口（办理畅享流量营销活动）
     *
     * @param data
     * @param pg
     * @return
     */
    public IDataset QueryByGroupID(IData data) throws Throwable
    {
        String group_id = data.getString("GROUP_ID");
        if (StringUtils.isBlank(group_id))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "集团客户编码不可为空！");
        }
        
        IDataset ds = GrpUserQryIntf.getUserProductByGroupIdFlux(data, getPagination());
        
        if(IDataUtil.isEmpty(ds))
        {
         // 返回参数设置
            IData result = new DataMap();
            result.put("X_RESULTCODE", "0"); // 正常状态
            result.put("X_RESULTINFO", "查询不到已办理的集团产品编码！"); // 正常状态描述
            return IDataUtil.idToIds(result);
        }else
        {
            return ds; 
        }
            
        
    }
    
    /**
     * xuyt 查询集团客户返还的剩余流量查询接口
     *
     * @param data
     * @param pg
     * @return
     */
    public IDataset QueryCondPart(IData data) throws Throwable
    {
        String serial_number = data.getString("GRP_SERIAL_NUMBER");
        if (StringUtils.isBlank(serial_number))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "集团产品编码不可为空！");
        }
        data.put("SERIAL_NUMBER", serial_number);
        
        IDataset ds = LargessFluxGrpMainSVC.qryUserGrpGfffInfoFLUX(data, getPagination());
        if(IDataUtil.isEmpty(ds))
        {
         // 返回参数设置
            IData result = new DataMap();
            result.put("X_RESULTCODE", "0"); // 正常状态
            result.put("X_RESULTINFO", "查询集团客户返还的剩余流量无数据！"); // 正常状态描述
            return IDataUtil.idToIds(result);
        }else
        {
            return ds; 
        }
    }
}
