
package com.asiainfo.veris.crm.order.soa.group.minorec.queryAudit;

import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformModiTraceBean;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformReleBean;

public class QryAuditInfoSVC extends CSBizService
{
    private static final long serialVersionUID = 1L;
    
    private static final Integer WITH_RATE = 1024;
    
    /**
     * 
    * @Title: qryAuditStaffInfoByIbsysid 
    * @Description: 根绝IBSYSID BPM_TEMPLET_ID查询稽核信息
    * @param IBSYSID BPM_TEMPLET_ID
    * @return
    * @throws Exception IDataset
    * @author zhangzg
    * @date 2019年11月11日上午11:39:59
     */
    public static IDataset qryInfoByBpmTempletIdAndBiSn(IData param) throws Exception{
        IDataset results = QryAuditInfoBean.qryInfoByBpmTempletIdAndBiSn(param);
        return results;
    }
    
    /**
     * 
    * @Title: qryAuditInfoByIbsysid 
    * @Description: 查询稽核信息  TF_B_EOP_ATTR
    * @param param
    * @return
    * @throws Exception IData
    * @author zhangzg
    * @date 2019年11月19日上午10:50:44
     */
    public static IData qryAuditInfoByIbsysid(IData param) throws Exception
    {
        param.put("IBSYSID", param.getString("IBSYSID",""));
        param.put("NODE_ID", param.getString("NODE_ID",""));
        IData result = new DataMap();
        result = QryAuditInfoBean.qryAuditInfoByIbsysid(param);
        if(IDataUtil.isNotEmpty(result)) {
            return result;
        }
        return null;
    }
    
    /**
     * 
    * @Title: qryElecDataForFusercommunication 
    * @Description: 查询多媒体桌面电话电子协议信息 
    * @param AGREEMENT_ID PARAM_CODE
    * @return
    * @throws Exception IDataset
    * @author zhangzg
    * @date 2019年11月21日下午4:07:16
     */
    public static IDataset qryElecDataForFusercommunication(IData param) throws Exception
    {
        IDataset result = QryAuditInfoBean.qryElecDataForAudit(param);
        if(IDataUtil.isNotEmpty(result)) {
            return result;
        }
        return null;
    }
    
    /**
     * 
    * @Title: qryFinishDataInDiscnt 
    * @Description: 查询资费信息 （融合通信 来电显示3元资费  22220101）
    * @param USER_ID DISCNT_CODE  
    * @return
    * @throws Exception IDataset
    * @author zhangzg
    * @date 2019年11月21日下午4:34:34
     */
    public static IDataset qryFinishDataInDiscnt(IData param) throws Exception
    {
        IDataset result = QryAuditInfoBean.qryFinishDataInDiscnt(param);
        if(IDataUtil.isNotEmpty(result)) {
            return result;
        }
        return null;
    }
    
    /**
    * @Title: qryFinishDataInSVC 
    * @Description: 查询订购服务资料 （融合通信 多媒体彩铃服务 10122824）
    * @param SERVICE_ID USER_ID
    * @return
    * @throws Exception IDataset
    * @author zhangzg
    * @date 2019年11月21日下午4:37:38
     */
    public static IDataset qryFinishDataInSVC(IData param) throws Exception
    {
        IDataset result = QryAuditInfoBean.qryFinishDataInSVC(param);
        if(IDataUtil.isNotEmpty(result)) {
            return result;
        }
        return null;
    }
    
    /**
     * 
    * @Title: qryFinishDataInUserAttr 
    * @Description: 查询资费信息 （多媒体桌面电话集团自费 800109）
    * @param DISCNT_CODE USER_ID
    * @return
    * @throws Exception IDataset
    * @author zhangzg
    * @date 2019年11月21日下午4:41:31
     */
    public static IDataset qryFinishDataInUserAttr(IData param) throws Exception
    {
        IDataset result = QryAuditInfoBean.qryFinishDataInUserAttr(param);
        if(IDataUtil.isNotEmpty(result)) {
            return result;
        }
        return null;
    }
    
    /**
    * @Title: qryFinishDataInRelaionUU 
    * @Description: 稽核政企宽带条数 TF_F_RELATION_UU 
    * @param USER_ID
    * @return
    * @throws Exception IDataset
    * @author zhangzg
    * @date 2019年11月21日下午4:44:55
     */
    public static IDataset qryFinishDataInRelaionUU(IData param) throws Exception
    {
        IDataset result = QryAuditInfoBean.qryFinishDataInRelaionUU(param);
        if(IDataUtil.isNotEmpty(result)) {
            return result;
        }
        return null;
    }
    
    /**
     * 
    * @Title: qryFinishDataForBandwith 
    * @Description: 稽核企业宽带 带宽 
    * @param USER_ID
    * @return
    * @throws Exception IDataset
    * @author zhangzg
    * @date 2019年11月21日下午4:50:30
     */
    public static IDataset qryFinishDataForBandwith(IData param) throws Exception
    {
        IDataset result = QryAuditInfoBean.qryFinishDataForBandwith(param);
        if(IDataUtil.isEmpty(result)) {
            return new DatasetList();
        }
        // 根据产品ID 查询 速率
        String productId = result.getData(0).getString("PRODUCT_ID");
        String RSRV_STR1 = "";
        IDataset forceElements = UpcCall.qryAllOffersByOfferIdWithForceTagDefaultTagFilter(BofConst.ELEMENT_TYPE_CODE_PRODUCT, productId, "1", "1");
        if (!IDataUtil.isEmpty(forceElements))
        {
            for (int j = 0; j < forceElements.size(); j++)
            {
                IData forceElement = forceElements.getData(j);
                if ("S".equals(forceElement.getString("OFFER_TYPE")))
                {
                    IDataset rates = CommparaInfoQry.getCommpara("CSM", "4000", forceElement.getString("OFFER_CODE"), "0898");
                    if (!IDataUtil.isEmpty(rates))
                    {
                        RSRV_STR1 = rates.getData(0).getString("PARA_CODE1", "");
                        if (!StringUtils.isEmpty(RSRV_STR1))
                        {
                            int rate = Integer.parseInt(RSRV_STR1) / WITH_RATE;
                            RSRV_STR1 = String.valueOf(rate);
                        }
                    }
                }
            }
        }
        IData rateData = new DataMap();
        result.clear();
        rateData.put("RATE", RSRV_STR1);
        result.add(rateData);
        return result;
    }
    
    /**
     * 
    * @Title: insertModiTrace 
    * @Description: TODO 
    * @param param
    * @return
    * @throws Exception IDataset
    * @author zhangzg
    * @date 2019年11月23日下午5:10:46
     */
    public static IDataset insertModiTrace(IData param) throws Exception{
        IData modiTraceData = new DataMap();
           modiTraceData.put("IBSYSID", param.getString("IBSYSID"));
           modiTraceData.put("ACCEPT_MONTH", SysDateMgr.getCurMonth());
           modiTraceData.put("MAIN_IBSYSID", param.getString("MAIN_IBSYSID"));
           modiTraceData.put("ATTR_MAIN_VALUE", param.getString("ATTR_MAIN_VALUE"));
           modiTraceData.put("ATTR_CODE", "BUSIFORM_ID");
           modiTraceData.put("ATTR_TYPE", "F");
           modiTraceData.put("ATTR_VALUE", param.getString("BUSIFORM_ID"));
           modiTraceData.put("STAFF_ID", getVisit().getStaffId());
           modiTraceData.put("CITY_CODE",getVisit().getCityCode());
           modiTraceData.put("DEPART_ID", getVisit().getDepartId());
           modiTraceData.put("EPARCHY_CODE", getVisit().getLoginEparchyCode());
           modiTraceData.put("ACCEPT_DATE", SysDateMgr.getSysDate());
           modiTraceData.put("RSRV_STR1", param.getString("RSRV_STR1"));
           boolean modiTraceResult = WorkformModiTraceBean.insertModiTrace(modiTraceData);
           if(!modiTraceResult) {
               CSAppException.apperr(GrpException.CRM_GRP_713, "插入TF_B_EOP_MODI_TRACE表信息失败！");
           }
           return new DatasetList();
      }
    
    /**
     * 
    * @Title: qryEweInfoByBusiformId 
    * @Description: 稽核查询主流程信息
    * @param BUSIFORM_ID
    * @return
    * @throws Exception IDataset
    * @author zhangzg
    * @date 2019年11月23日下午5:32:54
     */
    public static IDataset qryEweInfoByBusiformId(IData param) throws Exception
    {
        IDataset result = QryAuditInfoBean.qryEweInfoByBusiformId(param);
        if(IDataUtil.isNotEmpty(result)) {
            return result;
        }
        return null;
    }
    
    /**
     * 
    * @Title: qryEopProductByIbsysidAndBusiformId 
    * @Description: 稽核子流程查询产品编码 
    * @param BUSIFORM_ID  IBSYSID
    * @return
    * @throws Exception IDataset
    * @author zhangzg
    * @date 2019年11月23日下午5:58:17
     */
    public static IDataset qryEopProductByIbsysidAndBusiformId(IData param) throws Exception
    {
        IDataset result = QryAuditInfoBean.qryEopProductByIbsysidAndBusiformId(param);
        if(IDataUtil.isEmpty(result)) {
            result = QryAuditInfoBean.qryEopProductHByIbsysidAndBusiformId(param);
        }
        return result;
    }
    
    /**
     * 
    * @Title: qryParentBpmtempletIdByIbsysId 
    * @Description: 查询父流程的BPM_TEMPLET_ID 
    * @param IBSYSID
    * @return
    * @throws Exception IDataset
    * @author zhangzg
    * @date 2019年11月26日下午5:31:42
     */
    public static IDataset qryParentBpmtempletIdByIbsysId(IData param) throws Exception
    {
        IDataset result = QryAuditInfoBean.qryParentBpmtempletIdByIbsysId(param);
        if(IDataUtil.isNotEmpty(result)) {
            return result;
        }
        return null;
    }
    
    /**
    * @Title: qryDataLineWorkForm 
    * @Description: 稽核报表查询
    * @param param
    * @return
    * @throws Exception IDataset
    * @author zhangzg
    * @date 2019年11月26日下午8:31:35
     */
    public IDataset qryDataLineWorkForm(IData param) throws Exception
    {
        IDataset result = QryAuditInfoBean.qryDataLineWorkForm(param, getPagination());
        if(IDataUtil.isNotEmpty(result)) {
            return result;
        }
        return null;
    }
    
    /**
    * @Title: qrySubRelaInfoByTemplet 
    * @Description: 查询父子流程关系表 
    * @param BPM_TEMPLET_ID
    * @return
    * @throws Exception IDataset
    * @author zhangzg
    * @date 2019年11月28日上午11:49:51
     */
    public IDataset qrySubRelaInfoByTemplet(IData param) throws Exception
    {
        IDataset result = QryAuditInfoBean.qrySubRelaInfoByTemplet(param);
        if(IDataUtil.isNotEmpty(result)) {
            return result;
        }
        return null;
    }
    
    /**
    * @Title: qryRecordNumInEopProductByIbsysid 
    * @Description: 通过bisysid查询RECORD_NUM 
    * @param IBSYSID
    * @return
    * @throws Exception IDataset
    * @author zhangzg
    * @date 2019年12月2日上午10:36:41
     */
    public IDataset qryRecordNumInEopProductByIbsysid(IData param) throws Exception
    {
        IDataset result = QryAuditInfoBean.qryRecordNumInEopProductByIbsysid(param);
        if(IDataUtil.isNotEmpty(result)) {
            return result;
        }
        return null;
    }
    
    public IDataset qryEweReleBySubBusiformId(IData param) throws Exception
    {
        String busiformId = param.getString("BUSIFORM_ID");
        IDataset result = QryAuditInfoBean.qryEweReleBySubBusiformId(busiformId);
        if(IDataUtil.isEmpty(result)) {
            result = WorkformReleBean.qryBySubBusiformId(busiformId);
        }
        return result;
    }
    
    public IData qryProductByPk(IData param) throws Exception
    {
        String ibsysid = param.getString("IBSYSID");
        String recordNum = param.getString("RECORD_NUM");
        IData result = QryAuditInfoBean.qryProductByPk(ibsysid, recordNum);
        if(IDataUtil.isEmpty(result)) {
            result = QryAuditInfoBean.qryProductHByPk(ibsysid, recordNum);
        }
        return result;
    }
    
    public IData qryProductHByPk(IData param) throws Exception
    {
        String ibsysid = param.getString("IBSYSID");
        String recordNum = param.getString("RECORD_NUM");
        IData result = QryAuditInfoBean.qryProductHByPk(ibsysid, recordNum);
        if(IDataUtil.isNotEmpty(result)) {
            return result;
        }
        return null;
    }
    
    public IData qryRecordNumByAuditIbsysid(IData param) throws Exception
    {
        String ibsysid = param.getString("IBSYSID");
        IData result = QryAuditInfoBean.qryRecordNumByAuditIbsysid(ibsysid);
        if(IDataUtil.isNotEmpty(result)) {
            return result;
        }
        return null;
    }
    
    public IDataset qryAuditInfoEweByBpmAndIbsysId(IData param) throws Exception
    {
        IDataset result = QryAuditInfoBean.qryAuditInfoEweByBpmAndIbsysId(param);
        if(IDataUtil.isNotEmpty(result)) {
            return result;
        }
        return null;
    }
    
    /**
     * 
    * @Title: qryEspProductInfosBySerialNumber 
    * @Description: 查询ESP生成资料信息  TF_F_USER_ATTR
    * @param SERIAL_NUMBER
    * @return
    * @throws Exception IDataset
    * @author zhangzg
    * @date 2020年1月6日上午10:10:23
     */
    public IDataset qryEspProductInfosBySerialNumber(IData param) throws Exception
    {
        String serialNumber = param.getString("SERIAL_NUMBER");
        IDataset result = QryAuditInfoBean.qryEspProductInfosBySerialNumber(serialNumber);
        if(IDataUtil.isNotEmpty(result)) {
            return result;
        }
        return null;
    }
    
}
