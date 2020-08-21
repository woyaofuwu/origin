package com.asiainfo.veris.crm.order.soa.group.esop.eopintf;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.group.esop.query.EweAsynBean;

public class EopIntfSVC extends CSBizService
{

    private static final long serialVersionUID = 1L;
    private static  Logger logger = Logger.getLogger(EopIntfSVC.class);

    /**
     * 原接口：ITF_EOS_TcsGrpBusi-SaveGrpbiz
     * @param param
     * @return
     * @throws Exception
     */
    public IData saveEopNode(IData param) throws Exception
    {
        EopNodeDealBean bean = new EopNodeDealBean();
        return bean.saveEopNode(param);
    }
    
    /**
     * 原接口：ITF_EOS_TcsGrpBusi-SaveAndSend
     * @param param
     * @return
     * @throws Exception
     */
    public IData saveEopNodeAndDrive(IData param) throws Exception
    {
        EopNodeDealBean bean = new EopNodeDealBean();
        IData result = bean.saveEopNode(param);
        if("0".equals(result.getString("X_RESULTCODE")))
        {
        	IData data = new DataMap();

            if(StringUtils.isNotBlank(result.getString("SUB_IBSYSID"))) {
                data.put("SUB_IBSYSID", result.getString("SUB_IBSYSID"));
            }
            //data.put("SUB_IBSYSID", result.getString("SUB_IBSYSID"));
        	data.put("IBSYSID", result.getString("IBSYSID"));
        	data.put("BUSIFORM_ID", result.getString("BUSIFORM_ID"));
        	data.put("NODE_ID", param.getString("NODE_ID"));
        	logger.debug(">>>>>>>>>>>>>>>>>进入驱动流程服务WorkformDriveSVC>>>>>>>>>>>>>>>>>"+data.toString());

            //CSAppCall.call("SS.WorkformDriveSVC.execute", data);
        	EweAsynBean.saveAsynInfo(data);
        }
        return result;
    }
    
    /**
     * 原接口：ITF_EOS_TcsGrpBusi-SaveAndSend
     * @param param
     * @return
     * @throws Exception
     */
    public IData saveAttrInfoAndDrive(IData param) throws Exception
    {
        EopNodeDealBean bean = new EopNodeDealBean();
        IData result = bean.saveAttrInfoAndDrive(param);
        if("0".equals(result.getString("X_RESULTCODE")))
        {
        	IData data = new DataMap();

            if(StringUtils.isNotBlank(result.getString("SUB_IBSYSID"))) {
                data.put("SUB_IBSYSID", result.getString("SUB_IBSYSID"));
            }
            //data.put("SUB_IBSYSID", result.getString("SUB_IBSYSID"));
        	data.put("IBSYSID", result.getString("IBSYSID"));
        	data.put("BUSIFORM_ID", result.getString("BUSIFORM_ID"));
        	data.put("NODE_ID", param.getString("NODE_ID"));
        	logger.debug(">>>>>>>>>>>>>>>>>进入驱动流程服务WorkformDriveSVC>>>>>>>>>>>>>>>>>"+data.toString());

            //CSAppCall.call("SS.WorkformDriveSVC.execute", data);
        	EweAsynBean.saveAsynInfo(data);
        }
        return result;
    }
    
    /**
     * 原接口：ITF_EOS_TcsGrpBusi-SaveAndSend
     * @param param
     * @return
     * @throws Exception
     */
    public IData saveEopSubAndDrive(IData param) throws Exception
    {
    	EopNodeDealBean bean = new EopNodeDealBean();
        IData result = bean.saveEopSubAndDrive(param);
        if("0".equals(result.getString("X_RESULTCODE")))
        {
        	IData data = new DataMap();

        	data.put("IBSYSID", result.getString("IBSYSID"));
        	//data.put("SUB_IBSYSID", result.getString("SUB_IBSYSID"));
        	data.put("BUSIFORM_ID", result.getString("BUSIFORM_ID"));
        	data.put("NODE_ID", param.getString("NODE_ID"));
        	logger.debug(">>>>>>>>>>>>>>>>>进入驱动流程服务WorkformDriveSVC>>>>>>>>>>>>>>>>>"+data.toString());

            //CSAppCall.call("SS.WorkformDriveSVC.execute", data);
        	EweAsynBean.saveAsynInfo(data);
        }
        return result;
    }
    
    
    /**
     * 原接口：ITF_EOS_TcsGrpBusi-SaveAndSend
     * @param param
     * @return
     * @throws Exception
     */
    public IData saveEopSubTradeAndDrive(IData param) throws Exception
    {
    	EopNodeDealBean bean = new EopNodeDealBean();
        IData result = bean.saveEopSubTradeAndDrive(param);
        if("0".equals(result.getString("X_RESULTCODE")))
        {
        	IData data = new DataMap();

        	data.put("IBSYSID", result.getString("IBSYSID"));
        	//data.put("SUB_IBSYSID", result.getString("SUB_IBSYSID"));
        	data.put("BUSIFORM_ID", result.getString("BUSIFORM_ID"));
        	data.put("NODE_ID", param.getString("NODE_ID"));
        	logger.debug(">>>>>>>>>>>>>>>>>进入驱动流程服务WorkformDriveSVC>>>>>>>>>>>>>>>>>"+data.toString());

            //CSAppCall.call("SS.WorkformDriveSVC.execute", data);
        	EweAsynBean.saveAsynInfo(data);
        }
        return result;
    }
    
    /**
     * 原接口：ITF_EOS_TcsGrpBusi-SaveAndSend
     * @param param
     * @return
     * @throws Exception
     */
    public IData bbossManagerSaveEopAndDrive(IData param) throws Exception
    {
        EopNodeDealBean bean = new EopNodeDealBean();
        IData result = bean.saveEosData(param);
        if(IDataUtil.isNotEmpty(result))
        {
            param.put("IBSYSID", result.getString("IBSYSID"));
            param.put("SUB_IBSYSID", result.getString("SUB_IBSYSID"));
            param.put("BPM_TEMPLET_ID", result.getString("BPM_TEMPLET_ID"));
            param.put("OPER_TYPE", result.getString("OPER_TYPE"));
            param.put("BUSI_CODE", result.getString("BUSI_CODE"));
            param.put("IN_MODE_CODE", result.getString("IN_MODE_CODE","0"));
            CSAppCall.call("SS.WorkformRegSVC.execute", param);
        }
        return result;
    }
    /**
     * 原接口：ITF_EOS_TcsGrpBusi-CreatGrpBizOrder CreatGrpBizOrderForEoms
     * @param param
     * @return
     * @throws Exception
     */
    public IData createSubscriberOrder(IData param) throws Exception
    {
        ApplyReqBean bean = new ApplyReqBean();
        return bean.createSubscriberOrder(param);
    }
    
    /**
     * 专线驳回重派单比较esop属性与tradeattr是否一致，不一致则更新tradeattr
     * @param param
     * @throws Exception
     */
    public void comparaAttrInfo(IData param) throws Exception
    {
        AttrInfoComparaBean bean = new AttrInfoComparaBean();
        bean.comparaAttrInfo(param);
    }
    
    /**
     * 专线完工更新eopproduct表tradeid，如果是最后一条专线完工，则驱动流程
     * @param param
     * @throws Exception
     */
    public void dealLineAfterTradeFinish(IData param) throws Exception
    {
        LineFinishBean bean = new LineFinishBean();
        bean.dealLineAfterTradeFinish(param);
    }
    
    public void updateEopProduct(IData param) throws Exception
    {
    	EopNodeDealBean bean = new EopNodeDealBean();
    	//param.put("USER_ID", param.getString("USER_ID",""));
        bean.saveEopProduct(param);
    }
    public void updEopProductSub(IData param)throws Exception
    {
    	EopNodeDealBean bean = new EopNodeDealBean();
        bean.updEopProductSub(param);
    }
}
