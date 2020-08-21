
package com.asiainfo.veris.crm.order.soa.frame.bcf.query.product;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.cfg.OfferCfg;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;

public final class UPlatSvcInfoQry
{
    /**
     * 根据服务标识查询服务名称
     * 
     * @param svcId
     * @return
     * @throws Exception
     */
    public static String getSvcNameBySvcId(String svcId) throws Exception
    {
        try
        {
            return UpcCall.qryOfferNameByOfferTypeOfferCode(svcId, BofConst.ELEMENT_TYPE_CODE_PLATSVC);
        }
        catch (Exception e)
        {
            return "";
        }
    }
    
    public static String getSvcExplainBySvcId(String svcId) throws Exception
    {
        try
        {
            OfferCfg offercfg = OfferCfg.getInstance(svcId, BofConst.ELEMENT_TYPE_CODE_PLATSVC);
            if (offercfg == null)
                return "";

            else
                return offercfg.getDescription();
        }
        catch (Exception e)
        {
            return "";
        }
    }

    /**
     * 根据服务标识查询服务配置信息
     * 
     * @param serviceId
     * @return
     * @throws Exception
     */
    public static IData qryServInfoBySvcId(String serviceId) throws Exception
    {
        OfferCfg offercfg= OfferCfg.getInstance(serviceId, BofConst.ELEMENT_TYPE_CODE_PLATSVC);
        if(offercfg == null)
            return null;
        
        IData svcInfo = new DataMap();
        svcInfo.put("SERVICE_ID", offercfg.getOfferCode());
        svcInfo.put("SERVICE_NAME", offercfg.getOfferName());
        svcInfo.put("NET_TYPE_CODE", offercfg.getNetTypeCode());
        
        svcInfo.putAll(getPlatSvcExtChaInfoByProductId(serviceId));//放入扩展字段
        
        return svcInfo;
    }
   
    /**查询TD_M_SP_BIZ 的rsrv_str9值返回
     * TD_M_SP_INFO B, TD_M_SP_BIZ C, TD_B_PLATSVC 的合集 
     * @author hefeng
     * @param serviceId
     * @return
     * @throws Exception
     */
    public static IDataset qryServInfoByrsrvStr(String rsrvstr9) throws Exception
    {
        IDataset result=UpcCall.qryServInfoByrsrvStr(rsrvstr9);

        
        return result;
    }
    
    /**BUG20190226160500产品变更短信提醒内容存在bug,产品内容有null值展示在短信里。具体见附件，请优化。wangsc10-20190228-根据SERVICE_ID查值返回
     * TD_M_SP_INFO B, TD_M_SP_BIZ C, TD_B_PLATSVC 的合集 
     * @author 
     * @param serviceId
     * @return
     * @throws Exception
     */
    public static IDataset qryServInfoByServiceId(String serviceId) throws Exception
    {
        IDataset result=UpcCall.qryServInfoByServiceId(serviceId);

        
        return result;
    }

    /**
     * 
     * @Title: getPlatSvcExtChaInfoByProductId  
     * @Description: 获取平台服务表备用字段
     * @param @param serviceId
     * @param @return
     * @param @throws Exception    设定文件  
     * @return IData    返回类型  
     * @throws
     */
    public static IData getPlatSvcExtChaInfoByProductId(String serviceId) throws Exception
    {
        IDataset results = UpcCall.qryOfferExtChaByOfferId(serviceId, BofConst.ELEMENT_TYPE_CODE_PLATSVC, "TD_B_PLATSVC");
        if (IDataUtil.isEmpty(results))
        {
            return new DataMap();
        }
        return results.getData(0);
    }
}
