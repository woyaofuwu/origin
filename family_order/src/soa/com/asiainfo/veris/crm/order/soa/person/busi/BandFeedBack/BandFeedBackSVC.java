package com.asiainfo.veris.crm.order.soa.person.busi.BandFeedBack;

import com.ailk.biz.util.StaticUtil;
import com.ailk.bizcommon.route.Route;
import com.ailk.bizcommon.util.SysDateMgr;
import com.ailk.bizservice.base.CSBizBean;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.util.SQLParser;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.person.busi.auth.AbilityEncrypting;

public class BandFeedBackSVC extends CSBizService
{
    private static StringBuilder getInterFaceSQL;

    static
    {
        getInterFaceSQL = new StringBuilder().append("select t.* FROM td_s_bizenv t where t.param_name = :PARAM_NAME and t.state= 'U' "); 
    }
    
    public IDataset queryOrderInfos(IData input) throws Exception
    {
        BandFeedBackBean bean = (BandFeedBackBean) BeanManager.createBean(BandFeedBackBean.class);
        return bean.queryOrderInfos(input, getPagination());
    }
    
    public IDataset queryReturnInfos(IData input) throws Exception
    {
        BandFeedBackBean bean = (BandFeedBackBean) BeanManager.createBean(BandFeedBackBean.class);
        return bean.queryReturnInfos(input, getPagination());
    }
    
    public IDataset updateStatus(IData input) throws Exception
    {
        BandFeedBackBean bean = (BandFeedBackBean) BeanManager.createBean(BandFeedBackBean.class);
        return bean.updateStatus(input);
    }
    
    public IData feedBackOrderStatus(IData input) throws Exception
    {
        int i = input.getInt("LOOP_NUM");

        IData returnData = new DataMap();
        returnData.put("RESULT_CODE", "0000");
        returnData.put("RESULT_INFO", "成功。");
        IData param1 = new DataMap();
        param1.put("PARAM_NAME", "crm.ABILITY.UP");
        IDataset Abilityurls = Dao.qryBySql(getInterFaceSQL, param1, "cen");
        String Abilityurl = "";
        if (Abilityurls != null && Abilityurls.size() > 0)
        {
            Abilityurl = Abilityurls.getData(0).getString("PARAM_VALUE", "");
        }
        else
        {
            CSAppException.appError("-1", "crm.ABILITY.UP接口地址未在TD_S_BIZENV表中配置");
        }
        String apiAddress = Abilityurl;
        //能力编码
        //String abilityCode = "CIP00065";
        String appId=StaticUtil.getStaticValue("ABILITY_APP_ID", "1");// 应用ID
        
        String orderType = input.getString("ORDER_TYPE");
        if("0".equals(orderType)){
            IData result = new DataMap();
            String status = input.getString("STATE","");
            result.put("UPDATE_STAFF_ID",CSBizBean.getVisit().getStaffId());
            result.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
            result.put("ORDER_ID", input.getString("ORDER_ID",""));//订单编号/等订单和退订接口表建好后改表字段
            result.put("SUBORDER_ID", input.getString("SUBORDER_ID",""));//子订单编号
            //result.put("returnId", info.getString("OID",""));//退单编号
            try   
            {     
                IData abilityData = new DataMap();
                abilityData.put("OrderId", input.getString("ORDER_ID",""));//订单编号/等订单和退订接口表建好后改表字段
                abilityData.put("SubOrderId", input.getString("SUBORDER_ID",""));//子订单编号
                abilityData.put("returnId", "");//退单编号
                abilityData.put("OrderTime", SysDateMgr.decodeTimestamp(input.getString("CREATE_TIME",""), SysDateMgr.PATTERN_STAND_SHORT));//业务订购时间
                abilityData.put("Status", input.getString("STATE",""));//订单状态
                abilityData.put("statusDesc","");//订单状态描述
                abilityData.put("UpdateTime",  SysDateMgr.decodeTimestamp(input.getString("UPDATE_TIME",""), SysDateMgr.PATTERN_STAND_SHORT));//状态变更时间
                if("SE".equals(status))
                {//已发货状态时需反馈物流信息
                    IData shipInfo=new DataMap();
                    shipInfo.put("ShipmentCompanyCode", StaticUtil.getStaticValue("SHIP_CONPANY_NO", "1"));//物流公司编码
                    shipInfo.put("ShipmentCompany", StaticUtil.getStaticValue("SHIP_COMPANY_NAME", "1"));//物流公司名称
                    shipInfo.put("ShipmentNo",StaticUtil.getStaticValue("SHIP_NO", "1"));//物流单号/运单编码
                    abilityData.put("ShipmentInfo", shipInfo);
                }
                if("IN".equals(status))
                {//订单状态status=IN（安装中）时，省公司需要返回additionalInfo字段
                    String addition = input.getString("ADDITION");
                    String [] additionInfo = addition.split(";");
                    String addition_name = additionInfo[0];
                    String addition_phone = additionInfo[1];
                    String addition_remark = "";
                    if(additionInfo.length>2){
                        addition_remark = additionInfo[2];
                    }
                    abilityData.put("OpContactName", addition_name);//订单受理联系人姓名
                    abilityData.put("OpContactNo", StaticUtil.getStaticValue("ADDITION_STAFF_ID", "1"));//订单受理联系人工号
                    abilityData.put("OpContactPhone", addition_phone);//订单受理联系人电话
                    abilityData.put("OpComments", addition_remark);//订单受理备注信息
                }    
                IData stopResult = AbilityEncrypting.callAbilityPlatCommon(apiAddress,abilityData);
                String resCode=stopResult.getString("resCode");
                String X_RSPCODE="";
                String X_RSPDESC="";
                if(!"00000".equals(resCode))
                {   
                    X_RSPCODE=stopResult.getString("resCode");  
                    X_RSPDESC=stopResult.getString("resMsg");
                }    
                else  
                {
                    IData out=stopResult.getData("result");
                    X_RSPCODE=out.getString("BizCode");
                    X_RSPDESC=out.getString("BizDesc");
                }
                result.put("RESULT_CODE", X_RSPCODE);
                result.put("RESULT_INFO", X_RSPDESC);
                returnData.put("RESULT_CODE", X_RSPCODE);
                returnData.put("RESULT_INFO", X_RSPDESC);
            }
            catch (Exception e)
            {   
                result.put("RESULT_CODE", "-1");
                result.put("RESULT_INFO", "调能力平台异常导致反馈失败:" + (e.getMessage() == null ? "" : e.getMessage().length() > 128 ? e.getMessage().substring(0, 128) : e.getMessage()));     
                returnData.put("RESULT_CODE", "9999");
                returnData.put("RESULT_INFO", "调能力平台异常导致反馈失败:" + (e.getMessage() == null ? "" : e.getMessage().length() > 128 ? e.getMessage().substring(0, 128) : e.getMessage())); 
            }   
            int nR1 = i + 1;
            result.put("RSRV_STR1", nR1);
            updateOrderStatusInfo(result);
        } else if("1".equals(orderType)){
            IData result = new DataMap();
            String status = input.getString("STATUS","");
            result.put("OPR_NUM", input.getString("OPR_NUM",""));//操作流水
            result.put("UPDATE_STAFF_ID",CSBizBean.getVisit().getStaffId());
            result.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
            result.put("ORDER_ID", input.getString("ORDER_ID",""));//订单编号/等订单和退订接口表建好后改表字段
            result.put("SUB_ORDER_ID", input.getString("SUB_ORDER_ID",""));//子订单编号
            result.put("RETURN_ID", input.getString("RETURN_ID",""));//退单编号
            try
            {  
                IData abilityData = new DataMap();
                abilityData.put("OrderId", input.getString("ORDER_ID",""));//订单编号/等订单和退订接口表建好后改表字段
                abilityData.put("SubOrderId", input.getString("SUB_ORDER_ID",""));//子订单编号
                abilityData.put("ReturnId", input.getString("RETURN_ID",""));//退单编号
                abilityData.put("OrderTime", SysDateMgr.decodeTimestamp(input.getString("ACCEPT_DATE",""), SysDateMgr.PATTERN_STAND_SHORT));//业务订购时间
                abilityData.put("Status", input.getString("STATUS",""));//订单状态
                //abilityData.put("statusDesc", "");//订单状态描述
                abilityData.put("UpdateTime", SysDateMgr.decodeTimestamp(input.getString("UPDATE_TIME",""), SysDateMgr.PATTERN_STAND_SHORT));//状态变更时间
                if("SE".equals(status))
                {//已发货状态时需反馈物流信息
                    IData shipInfo=new DataMap();
                    shipInfo.put("ShipmentCompanyCode", StaticUtil.getStaticValue("SHIP_CONPANY_NO", "1"));//物流公司编码
                    shipInfo.put("ShipmentCompany", StaticUtil.getStaticValue("SHIP_COMPANY_NAME", "1"));//物流公司名称
                    shipInfo.put("ShipmentNo",StaticUtil.getStaticValue("SHIP_NO", "1"));//物流单号/运单编码
                    abilityData.put("ShipmentInfo", shipInfo);
                }
                if("IN".equals(status))
                {//订单状态status=IN（安装中）时，省公司需要返回additionalInfo字段
                    String addition = input.getString("ADDITION");
                    String [] additionInfo = addition.split(";");
                    String addition_name = additionInfo[0];
                    String addition_phone = additionInfo[1];
                    String addition_remark = "";
                    if(additionInfo.length>2){
                        addition_remark = additionInfo[2];
                    }
                    abilityData.put("OpContactName", addition_name);//订单受理联系人姓名
                    abilityData.put("OpContactNo", StaticUtil.getStaticValue("ADDITION_STAFF_ID", "1"));//订单受理联系人工号
                    abilityData.put("OpContactPhone", addition_phone);//订单受理联系人电话
                    abilityData.put("OpComments", addition_remark);//订单受理备注信息
                }
                IData stopResult = AbilityEncrypting.callAbilityPlatCommon(apiAddress, abilityData);
                String resCode = stopResult.getString("resCode");
                String X_RSPCODE = "";
                String X_RSPDESC = "";
                if(!"00000".equals(resCode))
                {
                    X_RSPCODE = stopResult.getString("resCode");
                    X_RSPDESC = stopResult.getString("resMsg");
                }
                else
                {    
                    IData out = stopResult.getData("result");
                    X_RSPCODE = out.getString("BizCode");
                    X_RSPDESC = out.getString("BizDesc");
                }
                result.put("RESULT_CODE", X_RSPCODE);
                result.put("RESULT_INFO", X_RSPDESC);
                returnData.put("RESULT_CODE", X_RSPCODE);
                returnData.put("RESULT_INFO", X_RSPDESC);
                returnData.putAll(stopResult);
            } 
            catch (Exception e)
            {
                result.put("RESULT_CODE", "-1");
                result.put("RESULT_INFO", "调能力平台异常导致反馈失败:" + (e.getMessage() == null ? "" : e.getMessage().length() > 128 ? e.getMessage().substring(0, 128) : e.getMessage()));
                returnData.put("RESULT_CODE", "9999");
                returnData.put("RESULT_INFO", "调能力平台异常导致反馈失败:" + (e.getMessage() == null ? "" : e.getMessage().length() > 128 ? e.getMessage().substring(0, 128) : e.getMessage()));
            }
            int nR1 = i + 1;
            result.put("RSRV_STR1", nR1);
            updateReturndStatusInfo(result);
        }
        
       return returnData;
    }
    
    public IDataset getOrderStatus() throws Exception
    {
        return Dao.qryByCode("TF_B_CTRM_GERLSUBORDER", "SEL_ORDER_INFO", null,Route.CONN_CRM_CEN);
    }
    
    public IDataset getReturndStatus() throws Exception
    {
        return Dao.qryByCode("TF_B_CTRM_RETURN", "SEL_RETURND_INFO", null,Route.CONN_CRM_CEN);
    }
    
    public static int updateOrderStatusInfo(IData result)throws Exception
    {
        SQLParser sqlParser = new SQLParser(result);
        sqlParser.addSQL(" update TF_B_CTRM_GERLSUBORDER ");
        sqlParser.addSQL(" set RSRV_STR1 = :RSRV_STR1 , DEAL_STATE = :RESULT_CODE , DEAL_DESC = :RESULT_INFO , UPDATE_TIME = sysdate , UPDATE_STAFF_ID  = :UPDATE_STAFF_ID , UPDATE_DEPART_ID = :UPDATE_DEPART_ID ");
        sqlParser.addSQL(" WHERE ORDER_ID = :ORDER_ID AND SUBORDER_ID = :SUBORDER_ID ");
        return Dao.executeUpdate(sqlParser,Route.CONN_CRM_CEN);
    }
    
    public static int updateReturndStatusInfo(IData result)throws Exception
    {
        SQLParser sqlParser = new SQLParser(result);
        sqlParser.addSQL(" update TF_B_CTRM_RETURN ");
        sqlParser.addSQL(" set RSRV_STR1 = :RSRV_STR1 , IS_SYNC = '1' , RSRV_STR2 = :RESULT_CODE ,RSRV_STR3 = :RESULT_INFO , UPDATE_TIME = sysdate , UPDATE_STAFF_ID = :UPDATE_STAFF_ID , UPDATE_DEPART_ID = :UPDATE_DEPART_ID ");
        sqlParser.addSQL(" WHERE OPR_NUM = :OPR_NUM ");
        return Dao.executeUpdate(sqlParser,Route.CONN_CRM_CEN);
    }
}
