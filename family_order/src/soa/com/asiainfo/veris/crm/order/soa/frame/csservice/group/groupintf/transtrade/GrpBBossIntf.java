
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.groupintf.transtrade;

import com.ailk.biz.bean.file.FileBean;
import com.ailk.biz.util.TimeUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.session.SessionManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.Clone;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cenpaygfffesop.GrpCenpayGfffEsopMgrQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.bat.intf.BatDealBBossRevsMebFileBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.bbossOrderRegist.bbossCenterControl;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.bbossOrderRegist.bbossMgrOrderDelayBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.IntfField;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.groupintf.transtrade.bboss.BBossMemberChgIntf;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.groupintf.transtrade.bboss.OnePayMemIntf;

import org.apache.log4j.Logger;

public class GrpBBossIntf extends CSBizService
{
    protected static final Logger log = Logger.getLogger(GrpBBossIntf.class);

    private static final long serialVersionUID = 1L;

    /*
     * @description 集团一点代付,集团行业应用卡反向批量接口
     * @author
     * @date 22014-6-13
     */
    public static IDataset batDealBBossRevsMebFile(IData iData) throws Exception
    {
        return BatDealBBossRevsMebFileBean.dealBBossMebFile(iData);
    }

    /**
     * CHENYI 2015-2-9
     * 成员接口查询
     * @param iData
     * @return
     * @throws Exception
     */
    public static IDataset getOrderInfo(IData iData) throws Exception
    {
        IDataset ids = new DatasetList();
        try
        {
            return GrpIntf.getOrderInfo(iData);
        }
        catch (Exception e)
        {
            SessionManager.getInstance().rollback();// 提交失败后整个事物回滚
            IData errorInfo = getErrorInfo(e,iData);
            IData result_data = new DataMap();// 返回结果
            result_data.put("RSPCODE".toUpperCase(), IntfField.DATABASE_ERR[0]);
            result_data.put("RSP_DESC".toUpperCase(), IntfField.DATABASE_ERR[1]);
            result_data.put("X_RESULTCODE".toUpperCase(), IntfField.DATABASE_ERR[0]);// 其他错误
            result_data.put("X_RESULTINFO".toUpperCase(), IntfField.DATABASE_ERR[1] + "," + errorInfo.getString("ERROR", ""));
            result_data.put("X_RSPCODE", "2998");
            result_data.put("X_RSPDESC", IntfField.DATABASE_ERR[1]);
            result_data.put("X_RSPTYPE", "2");
            result_data.put("X_RESULTINFO_DETAIL".toUpperCase(), errorInfo.getString("ERROR_DETAIL", ""));// 详细错误
            ids.add(result_data);
            return ids;
        }

    }
    /**
     * CHENYI 2014-4-8
     * 
     * @param iData
     * @return
     * @throws Exception
     */
    public static IDataset bbossPayBiz(IData iData) throws Exception
    {
        IData reqData = (IData)Clone.deepClone(iData);
        IDataset ids = new DatasetList();
        try{
            return GrpIntf.bbossPayBiz(iData);
        }
        catch (Exception e)
        {
            SessionManager.getInstance().rollback();//提交失败后整个事物回滚
            reqData.put("SERIAL_NUMBER", reqData.get("MEMBER_NUMBER"));
            IData errorInfo = getErrorInfo(e,reqData);
            IData result_data = new DataMap();// 返回结果
            result_data.put("RSPCODE".toUpperCase(), IntfField.DATABASE_ERR[0]);
            result_data.put("RSP_DESC".toUpperCase(), IntfField.DATABASE_ERR[1]);
            result_data.put("X_RESULTCODE".toUpperCase(), IntfField.DATABASE_ERR[0]);// 其他错误
            result_data.put("X_RESULTINFO".toUpperCase(), IntfField.DATABASE_ERR[1] + "," + errorInfo.getString("ERROR", ""));
            result_data.put("X_RSPCODE", "2998");
            result_data.put("X_RSPDESC", IntfField.DATABASE_ERR[1]);
            result_data.put("X_RSPTYPE", "2");
            result_data.put("X_RESULTINFO_DETAIL".toUpperCase(), errorInfo.getString("ERROR_DETAIL", ""));// 详细错误
            ids.add(result_data);
            return ids;
        }
    
    }

    /*
     * @description 集团业务反向接口
     * @author xunyl
     * @date 2013-09-20
     */
    public static IDataset dealBbossGroupBiz(IData data) throws Exception
    {
        IDataset ids = new DatasetList();
        try
        {
            return GrpIntf.dealBbossGroupBiz(data);
        }
        catch (Exception e)
        {
            SessionManager.getInstance().rollback();//提交失败后整个事物回滚
            String errRspCode="";
            String errRspDesc="";
            if (e.getMessage().indexOf("`") >= 1)
            {
            	  errRspCode = e.getMessage().substring(0,
                         e.getMessage().indexOf("`"));
                  errRspDesc = e.getMessage().substring(
                         e.getMessage().indexOf("`") + 1);
                  if (errRspDesc.length() > 300) {
                      errRspDesc.substring(0, 300);

                  }
            }
            IData errorInfo = getErrorInfo(e,data);
            if ("1".equals(data.getString("TURN_FLAG", "")))
            {
                data.put("ERROR_MESSAGE", errorInfo.getString("ERROR", ""));
                CSAppException.apperr(CrmUserException.CRM_USER_913, e.getMessage());// 如果是管理流程落地转订购关系的话,抛出异常,管理信息会继续处理这个异常
            }
            IData result_data = new DataMap();// 返回结果
            result_data.put("OPR_TIME", SysDateMgr.getSysDate("yyyyMMddhhmmss"));
            result_data.put("EC_SERIAL_NUMBER", data.getString("EC_SERIAL_NUMBER", ""));
            result_data.put("SUBSCRIBE_ID", data.getString("SUBSCRIBE_ID", ""));
            result_data.put("PSUBSCRIBE_ID", IDataUtil.getDatasetSpecl("PSUBSCRIBE_ID", data)); // 产品订单号
            result_data.put("RSPCODE".toUpperCase(), "99");
            result_data.put("RSP_DESC".toUpperCase(), IntfField.DATABASE_ERR[1]);
            result_data.put("X_RESULTCODE".toUpperCase(), "99");// 其他错误
            result_data.put("X_RESULTINFO".toUpperCase(), errRspDesc);
            result_data.put("X_RSPCODE", "2998");
            result_data.put("X_RSPDESC", IntfField.DATABASE_ERR[1]);
            result_data.put("X_RESULTINFO_DETAIL".toUpperCase(), errorInfo.getString("ERROR_DETAIL", ""));// 详细错误
            result_data.put("X_RSPTYPE", "2");
            ids.add(result_data);
            return ids;
        }
    }

    /**
     * 集团业务规则校验 - ctrl层
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset EcCheckInfoBiz(IData data) throws Exception {
        //返回值
        IDataset ids = new DatasetList();
        try {
            //成员业务处理
            return GrpIntf.dealEcCheckInfoBiz(data);
        } catch (Exception e ) {
            IData errorInfo = getErrorInfo(e,data);
            IData result_data = new DataMap();// 返回结果
            result_data.put("RSPCODE".toUpperCase(), IntfField.DATABASE_ERR[0]);
            result_data.put("RSP_DESC".toUpperCase(), IntfField.DATABASE_ERR[1]);
            result_data.put("X_RESULTCODE".toUpperCase(), IntfField.DATABASE_ERR[0]);// 其他错误
            result_data.put("X_RESULTINFO".toUpperCase(), IntfField.DATABASE_ERR[1] + "," + errorInfo.getString("ERROR", ""));
            result_data.put("X_RSPCODE", "2998");
            result_data.put("X_RSPDESC", IntfField.DATABASE_ERR[1]);
            result_data.put("X_RSPTYPE", "2");
            result_data.put("X_RESULTINFO_DETAIL".toUpperCase(), errorInfo.getString("ERROR_DETAIL", ""));// 详细错误
            ids.add(result_data);
            return ids;
        }
    }

    /**
     *
     * @description 成员业务反向接口 V2.0
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset dealReceptionHallMemberService(IData data) throws Exception {
        //返回值
        IDataset ids = new DatasetList();
        try {
            //成员业务处理
            return GrpIntf.dealReceptionHallMemberService(data);
        } catch (Exception e ) {
            IData errorInfo = getErrorInfo(e,data);
            IData result_data = new DataMap();// 返回结果
            result_data.put("RSPCODE".toUpperCase(), IntfField.DATABASE_ERR[0]);
            result_data.put("RSP_DESC".toUpperCase(), IntfField.DATABASE_ERR[1]);
            result_data.put("X_RESULTCODE".toUpperCase(), IntfField.DATABASE_ERR[0]);// 其他错误
            result_data.put("X_RESULTINFO".toUpperCase(), IntfField.DATABASE_ERR[1] + "," + errorInfo.getString("ERROR", ""));
            result_data.put("X_RSPCODE", "2998");
            result_data.put("X_RSPDESC", IntfField.DATABASE_ERR[1]);
            result_data.put("X_RSPTYPE", "2");
            result_data.put("X_RESULTINFO_DETAIL".toUpperCase(), errorInfo.getString("ERROR_DETAIL", ""));// 详细错误
            ids.add(result_data);
            return ids;
        }
    }

    /*
     * @description 成员业务反向接口
     * @author xunyl
     * @date 2013-09-20
     */
    public static IDataset dealBbossMemBiz(IData data) throws Exception
    {
        IDataset ids = new DatasetList();
        try
        {
            return GrpIntf.dealBbossMemBiz(data);
        }
        catch (Exception e)
        {
            SessionManager.getInstance().rollback();//提交失败后整个事物回滚
            IData errorInfo = getErrorInfo(e,data);
            IData result_data = new DataMap();// 返回结果
            result_data.put("RSPCODE".toUpperCase(), IntfField.DATABASE_ERR[0]);
            result_data.put("RSP_DESC".toUpperCase(), IntfField.DATABASE_ERR[1]);
            result_data.put("X_RESULTCODE".toUpperCase(), IntfField.DATABASE_ERR[0]);// 其他错误
            result_data.put("X_RESULTINFO".toUpperCase(), IntfField.DATABASE_ERR[1] + "," + errorInfo.getString("ERROR", ""));
            result_data.put("X_RSPCODE", "2998");
            result_data.put("X_RSPDESC", IntfField.DATABASE_ERR[1]);
            result_data.put("X_RSPTYPE", "2");
            result_data.put("X_RESULTINFO_DETAIL".toUpperCase(), errorInfo.getString("ERROR_DETAIL", ""));// 详细错误
            ids.add(result_data);
            return ids;
        }
    }

    /*
     * @description 商品订单处理失败通知业务反向接口
     * @author xunyl
     * @date 2013-09-20
     */
    public static IDataset dealBbossOrderDealFaildBiz(IData data) throws Exception
    {
        IDataset ids = new DatasetList();
        try
        {
            return GrpIntf.dealBbossOrderDealFaildBiz(data);
        }
        catch (Exception e)
        {
            SessionManager.getInstance().rollback();//提交失败后整个事物回滚
            IData errorInfo = getErrorInfo(e,data);
            IData result_data = new DataMap();// 返回结果
            result_data.put("RSPCODE".toUpperCase(), IntfField.DATABASE_ERR[0]);
            result_data.put("RSP_DESC".toUpperCase(), IntfField.DATABASE_ERR[1]);
            result_data.put("X_RESULTCODE".toUpperCase(), IntfField.DATABASE_ERR[0]);// 其他错误
            result_data.put("X_RESULTINFO".toUpperCase(), IntfField.DATABASE_ERR[1] + "," + errorInfo.getString("ERROR", ""));
            result_data.put("X_RESULTINFO_DETAIL".toUpperCase(), errorInfo.getString("ERROR_DETAIL", ""));// 详细错误
            result_data.put("X_RSPCODE", "2998");
            result_data.put("X_RSPDESC", IntfField.DATABASE_ERR[1]);
            result_data.put("X_RSPTYPE", "2");
            ids.add(result_data);
            return ids;
        }
    }

    /*
     * @description BBOSS向省BOSS下发工单开通业务反向接口
     * @author xunyl
     * @date 2013-09-20
     */
    public static IDataset dealBbossOrderOpenBiz(IData data) throws Exception
    {
        IDataset ids = new DatasetList();
        try
        {
            return GrpIntf.dealBbossOrderOpenBiz(data);
        }
        catch (Exception e)
        {
            SessionManager.getInstance().rollback();//提交失败后整个事物回滚
            IData errorInfo = getErrorInfo(e,data);
            IData result_data = new DataMap();// 返回结果
            result_data.put("RSPCODE".toUpperCase(), IntfField.DATABASE_ERR[0]);
            result_data.put("RSP_DESC".toUpperCase(), IntfField.DATABASE_ERR[1]);
            result_data.put("X_RESULTCODE".toUpperCase(), IntfField.DATABASE_ERR[0]);// 其他错误
            result_data.put("X_RESULTINFO".toUpperCase(), IntfField.DATABASE_ERR[1] + "," + errorInfo.getString("ERROR", ""));
            result_data.put("X_RSPCODE", "2998");
            result_data.put("X_RSPDESC", IntfField.DATABASE_ERR[1]);
            result_data.put("X_RSPTYPE", "2");
            result_data.put("X_RESULTINFO_DETAIL".toUpperCase(), errorInfo.getString("ERROR_DETAIL", ""));// 详细错误
            ids.add(result_data);
            return ids;
        }
    }
    /*
     * @description BBOSS向省BOSS下发工单开通业务反向接口
     * @author xunyl
     * @date 2018-11-13
     */
    public static IDataset dealJKDTBbossOrderOpenBiz(IData data) throws Exception
    {
        IDataset ids = new DatasetList();       
        try
        {
        	return GrpIntf.dealJKDTBbossOrderOpenBiz(data);
        }
        catch (Exception e)
        {
            SessionManager.getInstance().rollback();//提交失败后整个事物回滚
            IData errorInfo = getErrorInfo(e,data);
            IData result_data = new DataMap();// 返回结果
            result_data.put("RSPCODE".toUpperCase(), IntfField.DATABASE_ERR[0]);
            result_data.put("RSP_DESC".toUpperCase(), IntfField.DATABASE_ERR[1]);
            result_data.put("X_RESULTCODE".toUpperCase(), IntfField.DATABASE_ERR[0]);// 其他错误
            result_data.put("X_RESULTINFO".toUpperCase(), IntfField.DATABASE_ERR[1] + "," + errorInfo.getString("ERROR", ""));
            result_data.put("X_RSPCODE", "2998");
            result_data.put("X_RSPDESC", IntfField.DATABASE_ERR[1]);
            result_data.put("X_RSPTYPE", "2");
            result_data.put("X_RESULTINFO_DETAIL".toUpperCase(), errorInfo.getString("ERROR_DETAIL", ""));// 详细错误
            ids.add(result_data);
            return ids;
        }
    }
    /**
   	 * @description 该方法用于记录外围接口数据的主信息
   	 * @author xunyl
   	 * @date 2018-11-15
   	 */
   	private static String recordBBossXMLMainInfo(IData map,String dealState)throws Exception {
   		//1- 定义报文主表信息对象
   		IData bbossXmlMainInfo = new DataMap();
   		
   		//2- 添加主键编号
   		String seqId = TimeUtil.getSysDate("yyyyMMdd", true)+SeqMgr.getXmlInfoId();
   		bbossXmlMainInfo.put("SEQ_ID", seqId);
   		
   		//3- 添加BIPCODE		
   		bbossXmlMainInfo.put("BIPCODE", "POOrderService_BBOSS_1_0");
   		
   		//4- 获取TRANDS_IDO
   		String transIdo = map.getString("TRANSIDO");
   		if(StringUtils.isNotEmpty(transIdo)){
   			bbossXmlMainInfo.put("TRANDS_IDO", transIdo);
   		}else{
   			bbossXmlMainInfo.put("TRANDS_IDO", map.getString("IBSYSID"));//文件接口没有TRANDS_IDO 只有IBSYSID；
   		}								
   		
   		//5- BIPCODE为集团业务接口，则获取集团客户编码、商品订单号		
   		String ecCustomerNumber = map.getString("EC_SERIAL_NUMBER");
   		String poOrderNumber = map.getString("SUBSCRIBE_ID");
   		bbossXmlMainInfo.put("EC_CUSTOMER_NUMBER", ecCustomerNumber);
   		bbossXmlMainInfo.put("PO_ORDER_NUMBER", poOrderNumber);
   		bbossXmlMainInfo.put("XML_ACTION", "05");		
   					
   		//6- 添加报文的落地时间
   		bbossXmlMainInfo.put("LOCATE_TIME", SysDateMgr.getSysTime());
   		
   		//7- 添加报文的处理时间
   		bbossXmlMainInfo.put("DEAL_TIME", SysDateMgr.getSysTime());
   		
   		//8- 添加报文处理状态（0-延迟处理，1-处理失败）
   		bbossXmlMainInfo.put("DEAL_STATE",dealState);
   		
   		//9- 添加处理结果编码
   		bbossXmlMainInfo.put("OPEN_RESULT_CODE",map.getString("OPEN_RESULT_CODE",""));
   		
   		//10- 添加处理结果说明
   		bbossXmlMainInfo.put("OPEN_RESULT_DESC",map.getString("OPEN_RESULT_DESC",""));
   		
   		//11- 初始化IBOSS_RESULT为-1，供IBOSS使用
   		bbossXmlMainInfo.put("IBOSS_RESULT", "-1");
   		
   		//12-标识集客大厅集团业务
   		bbossXmlMainInfo.put("RSRV_STR1", "JKDTGrpBiz");
   		//13- 调用方法保存
   		Dao.delete("TF_TP_BBOSS_XML_INFO", bbossXmlMainInfo, Route.CONN_CRM_CEN);
   		Dao.insert("TF_TP_BBOSS_XML_INFO", bbossXmlMainInfo,Route.CONN_CRM_CEN);
   		
   		//14- 返回主键
   		return seqId;
   	}

    /*
     * @description 工单流转状态同步反向接口(配合省协助受理/预受理也会涉及到工单流转，需要反馈配合省协助落实情况)
     * @author xunyl
     * @date 2013-09-20
     */
    public static IDataset dealBbossOrderStateBiz(IData data) throws Exception
    {
        IDataset ids = new DatasetList();
        try
        {
            return GrpIntf.dealBbossOrderStateBiz(data);
        }
        catch (Exception e)
        {
            SessionManager.getInstance().rollback();//提交失败后整个事物回滚
            IData errorInfo = getErrorInfo(e,data);
            IData result_data = new DataMap();// 返回结果
            result_data.put("OPR_TIME", SysDateMgr.getSysDate("yyyyMMddhhmmss"));
            result_data.put("EC_SERIAL_NUMBER", data.getString("EC_SERIAL_NUMBER", ""));
            result_data.put("SUBSCRIBE_ID", data.getString("SUBSCRIBE_ID", ""));
            result_data.put("PSUBSCRIBE_ID", IDataUtil.getDatasetSpecl("PSUBSCRIBE_ID", data)); // 产品订单号
            result_data.put("RSPCODE".toUpperCase(), "99");
            result_data.put("RSP_DESC".toUpperCase(), IntfField.DATABASE_ERR[1]);
            result_data.put("X_RESULTCODE".toUpperCase(), "99");// 其他错误
            result_data.put("X_RESULTINFO".toUpperCase(), IntfField.DATABASE_ERR[1] + "," + errorInfo.getString("ERROR", ""));
            result_data.put("X_RSPCODE", "2998");
            result_data.put("X_RSPDESC", IntfField.DATABASE_ERR[1]);
            result_data.put("X_RSPTYPE", "2");
            result_data.put("X_RESULTINFO_DETAIL".toUpperCase(), errorInfo.getString("ERROR_DETAIL", ""));// 详细错误
            ids.add(result_data);
            return ids;
        }
    }

    /**
     * @descripiton 异常信息采集
     * @author xunyl
     * @date 2014-08-21
     */
    private static IData getErrorInfo(Exception e,IData data) throws Exception
    {
        // 1- 定义返回对象
        IData errorInfo = new DataMap();

        // 2- 获取详细信息，用于查看日志
        String detailErr = com.ailk.common.util.Utility.getStackTrace(e);
        errorInfo.put("ERROR_DETAIL", detailErr);

        // 3- 去掉异常信息中的特殊符号
        String strError = "";
        if (e.getMessage() != null)
        {
            strError = e.getMessage().replace("@", "");
            strError = strError.replace("#", "");
        }
        else if (e.getCause() != null && e.getCause().getMessage() != null)
        {
            strError = e.getCause().getMessage().replace("@", "");
            strError = strError.replace("#", "");
        }

        // 4- 截取异常信息中最后一个CAUSE_BY的前200个字符，用于发送BBOSS
        int i = strError.lastIndexOf("Caused by:");
        if (i >= 0)
        {
            strError = strError.substring(i);
        }
        if (strError.length() > 200)
        {
            strError = strError.substring(0, 200);
        }
        errorInfo.put("ERROR", strError);
        
        //5- 登记CRM异常信息
        data.put("DEAL_STATE", "1");//1代表处理失败
        data.put("ERROR_DETAIL", detailErr);
        CSAppCall.call("CS.bbossCenterControlSVC.rigisitXmlInfo",data);  

        // 6- 返回异常信息
        return errorInfo;
    }

    // 一点支付成员附件处理
    public static IDataset onePayMem(IData iData) throws Throwable
    {

        return OnePayMemIntf.importData(iData);
    }

    /**
     * chenyi BBOSS向省BOSS下发管理节点接口
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset synBBossGrpMgrBiz(IData data) throws Exception
    {

        IDataset ids = new DatasetList();

        try
        {
            //判断是否需要延时处理，延时处理场合，先直接反馈应答信息
            boolean isNeedDelay = bbossMgrOrderDelayBean.isNeedDelay(data);
            if(isNeedDelay){
                bbossCenterControl.rigistXmlData(data,"0");//0代表延时处理
                IDataset mgrRspInfoList = bbossMgrOrderDelayBean.getMgrRspInfoList(data);
                return mgrRspInfoList;
            }
            
            return GrpIntf.synBBossGrpMgrBiz(data);

        }
        catch (Exception e)
        {   
            SessionManager.getInstance().rollback();//提交失败后整个事物回滚
            IData errorInfo = getErrorInfo(e,data);
            IData result_data = new DataMap();// 返回结果
            result_data.put("OPR_TIME", SysDateMgr.getSysDate("yyyyMMddhhmmss"));
            result_data.put("RSPCODE".toUpperCase(), IntfField.DATABASE_ERR[0]);
            result_data.put("RSP_DESC".toUpperCase(), IntfField.DATABASE_ERR[1]);
            result_data.put("X_RESULTCODE".toUpperCase(), IntfField.DATABASE_ERR[0]);// 其他错误
            result_data.put("X_RESULTINFO".toUpperCase(), IntfField.DATABASE_ERR[1] + "," + errorInfo.getString("ERROR", ""));
            result_data.put("EC_SERIAL_NUMBER", data.getString("EC_SERIAL_NUMBER", ""));
            result_data.put("SUBSCRIBE_ID", data.getString("SUBSCRIBE_ID", ""));
            result_data.put("PRODUCT_ORDERNUMBER", IDataUtil.getDatasetSpecl("PSUBSCRIBE_ID", data)); // 产品订单号
            IDataset temp = new DatasetList();
            temp.add("99");
            result_data.put("PRODUCT_ORDER_RSP_CODE", temp);
            IDataset temp1 = new DatasetList();
            temp1.add(IntfField.DATABASE_ERR[1] + "," + errorInfo.getString("ERROR", ""));
            result_data.put("PRODUCT_ORDER_RSP_DESC", temp1);
            result_data.put("X_RSPCODE", "2998");
            result_data.put("X_RSPDESC", IntfField.DATABASE_ERR[1]);
            result_data.put("X_RSPTYPE", "2");
            result_data.put("X_RESULTINFO_DETAIL".toUpperCase(), errorInfo.getString("ERROR_DETAIL", ""));// 详细错误
            ids.add(result_data);
            return ids;
        }
    }

    /*
     * @description 商产品规格同步接口
     * @author xunyl
     * @date 2013-09-27
     */
    public static IDataset synBBossPoInfo(IData data) throws Exception
    {
        IDataset ids = new DatasetList();
        try
        {
            return UpcCall.processBBossSyncInfo(data);  //GrpIntf.synBBossPoInfo(data);
        }
        catch (Exception e)
        {
            SessionManager.getInstance().rollback();//提交失败后整个事物回滚
            IData errorInfo = getErrorInfo(e,data);
            IData result_data = new DataMap();// 返回结果
            result_data.put("RSPCODE".toUpperCase(), IntfField.DATABASE_ERR[0]);
            result_data.put("RSP_DESC".toUpperCase(), IntfField.DATABASE_ERR[1]);
            result_data.put("X_RESULTCODE".toUpperCase(), IntfField.DATABASE_ERR[0]);// 其他错误
            result_data.put("X_RESULTINFO".toUpperCase(), IntfField.DATABASE_ERR[1] + "," + errorInfo.getString("ERROR", ""));
            result_data.put("X_RSPCODE", "2998");
            result_data.put("X_RSPDESC", IntfField.DATABASE_ERR[1]);
            result_data.put("X_RSPTYPE", "2");
            result_data.put("X_RESULTINFO_DETAIL".toUpperCase(), errorInfo.getString("ERROR_DETAIL", ""));// 详细错误
            ids.add(result_data);
            return ids;
        }

    }
    /**
     * 集客大厅产品规格信息同步
     * @param data
     * @return
     * @throws Exception
     */
    
    public static IDataset jkdtPoInfo(IData data) throws Exception
    {
        IDataset ids = new DatasetList();
        try
        {
            return UpcCall.processJkdtSyncInfo(data);  //GrpIntf.synBBossPoInfo(data);
        }
        catch (Exception e)
        {
            SessionManager.getInstance().rollback();//提交失败后整个事物回滚
            IData errorInfo = getErrorInfo(e,data);
            IData result_data = new DataMap();// 返回结果
            result_data.put("RSPCODE".toUpperCase(), IntfField.DATABASE_ERR[0]);
            result_data.put("RSP_DESC".toUpperCase(), IntfField.DATABASE_ERR[1]);
            result_data.put("X_RESULTCODE".toUpperCase(), IntfField.DATABASE_ERR[0]);// 其他错误
            result_data.put("X_RESULTINFO".toUpperCase(), IntfField.DATABASE_ERR[1] + "," + errorInfo.getString("ERROR", ""));
            result_data.put("X_RSPCODE", "2998");
            result_data.put("X_RSPDESC", IntfField.DATABASE_ERR[1]);
            result_data.put("X_RSPTYPE", "2");
            result_data.put("X_RESULTINFO_DETAIL".toUpperCase(), errorInfo.getString("ERROR_DETAIL", ""));// 详细错误
            ids.add(result_data);
            return ids;
        }

    }


    /**
     * 集团成员退订接口
     * 
     * @param inParam
     * @return
     * @throws Exception
     */
    public IDataset destoryGrpMebOrder(IData inParam) throws Exception
    {

        DestroyGrpMebOrder.destroyGrpMebOrder(inParam);

        return null;
    }

    /**
     * 一卡通业务成员变更
     * 
     * @param inParam
     * @return
     * @throws Throwable
     */
    public IDataset iBOTBBossDataUDR(IData inParam) throws Throwable
    {
        BBossMemberChgIntf bean = new BBossMemberChgIntf();

        return bean.MemberChgSync(inParam);
    }

    /**
     * 修改成员资费信息
     * 
     * @param inParam
     * @return
     * @throws Exception
     */
    public IDataset operModifyMebDiscnt(IData inParam) throws Exception
    {
        return ModifyMebDiscnt.modifyMebDiscnt(inParam);
    }

    /**
     * BOSS响应M2M下发报文
     * 
     * @param inParam
     * @return
     * @throws Throwable
     */
    public IDataset tcsGrpM2MAnswerIntf(IData inParam) throws Throwable
    {
        // M2M接口反馈调用处理
        return M2MOrderIntf.IntfM2MAnswerDeal(inParam);
    }

    /**
     * @Function:
     * @Description: 对于集团下发的BIP4B257 T4101035 报文 直接返回成功
     * @author:chenyi
     * @date: 下午3:32:50 2014-9-21
     */
    public static IDataset dealBbossMebOrderOpenBiz(IData data) throws Exception
    {
        IDataset ids = new DatasetList();
        try
        {
            return GrpIntf.dealBbossMebOrderOpenBiz(data);
        }
        catch (Exception e)
        {
            SessionManager.getInstance().rollback();//提交失败后整个事物回滚
            IData errorInfo = getErrorInfo(e,data);
            IData result_data = new DataMap();// 返回结果
            result_data.put("RSPCODE".toUpperCase(), IntfField.DATABASE_ERR[0]);
            result_data.put("RSP_DESC".toUpperCase(), IntfField.DATABASE_ERR[1]);
            result_data.put("X_RESULTCODE".toUpperCase(), IntfField.DATABASE_ERR[0]);// 其他错误
            result_data.put("X_RESULTINFO".toUpperCase(), IntfField.DATABASE_ERR[1] + "," + errorInfo.getString("ERROR", ""));
            result_data.put("X_RSPCODE", "2998");
            result_data.put("X_RSPDESC", IntfField.DATABASE_ERR[1]);
            result_data.put("X_RSPTYPE", "2");
            result_data.put("X_RESULTINFO_DETAIL".toUpperCase(), errorInfo.getString("ERROR_DETAIL", ""));// 详细错误
            ids.add(result_data);
            return ids;
        }
    }
    
    /**
     * @Function:
     * @Description: 对于集团下发的报文 直接返回成功
     * @date: 2018-12-4
     */
    public static IDataset dealJKDTMemberOrderOpenBiz(IData data) throws Exception
    {
        IDataset ids = new DatasetList();
        try
        {
            return GrpIntf.dealJKDTMemberOrderOpenBiz(data);
        }
        catch (Exception e)
        {
            SessionManager.getInstance().rollback();//提交失败后整个事物回滚
            IData errorInfo = getErrorInfo(e,data);
            IData result_data = new DataMap();// 返回结果
            result_data.put("RSPCODE".toUpperCase(), IntfField.DATABASE_ERR[0]);
            result_data.put("RSP_DESC".toUpperCase(), IntfField.DATABASE_ERR[1]);
            result_data.put("X_RESULTCODE".toUpperCase(), IntfField.DATABASE_ERR[0]);// 其他错误
            result_data.put("X_RESULTINFO".toUpperCase(), IntfField.DATABASE_ERR[1] + "," + errorInfo.getString("ERROR", ""));
            result_data.put("X_RSPCODE", "2998");
            result_data.put("X_RSPDESC", IntfField.DATABASE_ERR[1]);
            result_data.put("X_RSPTYPE", "2");
            result_data.put("X_RESULTINFO_DETAIL".toUpperCase(), errorInfo.getString("ERROR_DETAIL", ""));// 详细错误
            ids.add(result_data);
            return ids;
        }
    }
    
    /**
     * @descripiton BBOSS下行附件入wd_f_ftpfile表，供下载用
     * @author xunyl
     * @date 2015-03-26
     */
    public static IDataset rigistFtpfileTab(IData data)throws Exception{     
        IDataset dealResultInfoList = new DatasetList();
        IData dealResultInfo = new DataMap();       
        dealResultInfo.put("RSPCODE".toUpperCase(), "00");
        dealResultInfo.put("RSP_DESC","处理成功");      
        dealResultInfoList.add(dealResultInfo);
        
        try
        {
            IData ftpFileInfo = new DataMap();
            FileBean fileBean = new FileBean();
            String fileId = fileBean.createFileId();
            ftpFileInfo.put("FILE_ID", fileId);
            String fileName = data.getString("FILE_NAME");
            ftpFileInfo.put("FILE_NAME", fileName);
            ftpFileInfo.put("FTP_SITE", "groupserv");
            ftpFileInfo.put("FILE_PATH", "bbossprovusers");
            ftpFileInfo.put("FILE_TYPE", "1");
            ftpFileInfo.put("FILE_KIND", "9");//文件处理完成
            ftpFileInfo.put("CREA_STAFF", "IBOSS");
            ftpFileInfo.put("CREA_TIME", SysDateMgr.getSysTime());
            Dao.insert("WD_F_FTPFILE", ftpFileInfo,Route.CONN_CRM_CEN);         
            Dao.insert("WD_F_FTPFILE_BBOSS", ftpFileInfo,Route.CONN_CRM_CEN);
            
            dealResultInfo.put("FILE_ID", fileId);
            return dealResultInfoList;
        }
        catch (Exception e)
        {
            dealResultInfoList.getData(0).put("RSPCODE", "99");
            dealResultInfoList.getData(0).put("RSP_DESC", "WD_F_FTPFILE表记录新增失败");           
            return dealResultInfoList;
        }
        
    }
    
    /**
     * 国际流量统付(集团)订购关系同步接口
     * @author cmw
     * @date 2017-01-09
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset synchGrpProdGprsRoam(IData data) throws Exception{
    	IDataset ids = new DatasetList();
        try{
            return GrpIntf.synchGrpProdGprsRoam(data);
        }catch (Exception e){
        	SessionManager.getInstance().rollback();//提交失败后整个事物回滚
        	IData errorInfo = getErrorInfo(e,data);
            IData result_data = new DataMap();// 返回结果
            result_data.put("RSPCODE".toUpperCase(), IntfField.DATABASE_ERR[0]);
            result_data.put("RSP_DESC".toUpperCase(), IntfField.DATABASE_ERR[1]);
            result_data.put("X_RESULTCODE".toUpperCase(), IntfField.DATABASE_ERR[0]);// 其他错误
            result_data.put("X_RESULTINFO".toUpperCase(), IntfField.DATABASE_ERR[1] + "," + errorInfo.getString("ERROR", ""));
            result_data.put("X_RSPCODE", "2998");
            result_data.put("X_RSPDESC", IntfField.DATABASE_ERR[1]);
            result_data.put("X_RSPTYPE", "2");
            result_data.put("X_RESULTINFO_DETAIL".toUpperCase(), errorInfo.getString("ERROR_DETAIL", ""));// 详细错误
            result_data.put("RESULT_CODE", "1");
            result_data.put("RESULT_INFO", IntfField.DATABASE_ERR[1]);
            ids.add(result_data);
            return ids;
        }
    }

    /**
     * @descripiton 违规信息同步接口，叠加包的暂停和恢复
     * @author wangzc7
     * @date 2017-08-01
     */
	public static IDataset synBbossMebFoul(IData data)throws Exception{
		String orderType = IDataUtil.chkParam(data, "ORDER_TYPE");//操作类型 22-暂停添加成员/叠加包 23-恢复添加成员/叠加包
		String productOfferingId = IDataUtil.chkParam(data, "ORDER_ID");//订购关系ID
		String illegOrderNumber = IDataUtil.chkParam(data, "ILLEG_ORDER_NUMBER");//省内违规信息工单号
		IDataset dealResultInfoList = new DatasetList();
		IData dealResultInfo = new DataMap(); 
		if(!"22".equals(orderType) && !"23".equals(orderType))
		{
			dealResultInfo.put("RSPCODE", "02");
	        dealResultInfo.put("RSP_DESC","操作类型错误！"); 
	        dealResultInfoList.add(dealResultInfo);
	        return dealResultInfoList;
		}
		//查询所有订购信息
		IDataset grpCenPayInfo = GrpCenpayGfffEsopMgrQry.getUserGrpCenPayByUserIdProductOfferId(productOfferingId);
		if(IDataUtil.isEmpty(grpCenPayInfo)){
			dealResultInfo.put("RSPCODE", "04");
	        dealResultInfo.put("RSP_DESC","订购关系ID不存在！"); 
	        dealResultInfoList.add(dealResultInfo);
	        return dealResultInfoList;
		}
		for (int i = 0; i < grpCenPayInfo.size(); i++) {
			String rsrvStr5 = grpCenPayInfo.getData(i).getString("RSRV_STR5");
			if("F".equals(rsrvStr5) && "22".equals(orderType)){
				dealResultInfo.put("RSPCODE", "99");
		        dealResultInfo.put("RSP_DESC","重复下发违规暂停！"); 
		        dealResultInfoList.add(dealResultInfo);
		        return dealResultInfoList;
			}else if("G".equals(rsrvStr5) && "23".equals(orderType)){
				dealResultInfo.put("RSPCODE", "99");
		        dealResultInfo.put("RSP_DESC","重复下发违规恢复！"); 
		        dealResultInfoList.add(dealResultInfo);
		        return dealResultInfoList;
			}
		}
		bbossCenterControl.rigistXmlData(data,"0");//0代表延时处理
		//根据productOfferingId修改暂停恢复的标记。
		if("22".equals(orderType)){
			GrpCenpayGfffEsopMgrQry.updateFoulTagByProductOfferId(productOfferingId,"F");
		}else if("23".equals(orderType)){
			GrpCenpayGfffEsopMgrQry.updateFoulTagByProductOfferId(productOfferingId,"G");
		}
        dealResultInfo.put("ILLEG_ORDER_NUMBER", illegOrderNumber);
        dealResultInfo.put("RSPCODE", "00");
        dealResultInfo.put("RSP_DESC","处理成功");      
        dealResultInfoList.add(dealResultInfo);
        return dealResultInfoList;
	}

	/*
     * @description 集合大厅发起-集团业务反向接口
     * @author xunyl
     * @date 2013-09-20
     */
    public static IDataset dealJKDTGroupBiz(IData data) throws Exception
    {
        IDataset ids = new DatasetList();
        IDataset results = new DatasetList();
        try
        {
        	results = GrpIntf.dealJKDTGroupBiz(data);
        	String code = results.getData(0).getString("RSPCODE", "00");
        	String dealState = "1";
        	if("00".equals(code)){
        		dealState = "2";
        	}else{
        		dealState = "1";
        	}
        	recordBBossXMLMainInfo(data,dealState);
            return results;
        }
        catch (Exception e)
        {
            SessionManager.getInstance().rollback();//提交失败后整个事物回滚
            String errRspCode="";
            String errRspDesc="";
            if (e.getMessage().indexOf("`") >= 1)
            {
            	  errRspCode = e.getMessage().substring(0,
                         e.getMessage().indexOf("`"));
                  errRspDesc = e.getMessage().substring(
                         e.getMessage().indexOf("`") + 1);
                  if (errRspDesc.length() > 300) {
                      errRspDesc.substring(0, 300);

                  }
            }
            IData errorInfo = getErrorInfo(e,data);
            if ("1".equals(data.getString("TURN_FLAG", "")))
            {
                data.put("ERROR_MESSAGE", errorInfo.getString("ERROR", ""));
                CSAppException.apperr(CrmUserException.CRM_USER_913, e.getMessage());// 如果是管理流程落地转订购关系的话,抛出异常,管理信息会继续处理这个异常
            }
            IData result_data = new DataMap();// 返回结果
            result_data.put("OPR_TIME", SysDateMgr.getSysDate("yyyyMMddhhmmss"));
            result_data.put("EC_SERIAL_NUMBER", data.getString("EC_SERIAL_NUMBER", ""));
            result_data.put("SUBSCRIBE_ID", data.getString("SUBSCRIBE_ID", ""));
            result_data.put("PSUBSCRIBE_ID", IDataUtil.getDatasetSpecl("PSUBSCRIBE_ID", data)); // 产品订单号
            result_data.put("RSPCODE".toUpperCase(), "99");
            result_data.put("RSP_DESC".toUpperCase(), IntfField.DATABASE_ERR[1]);
            result_data.put("X_RESULTCODE".toUpperCase(), "99");// 其他错误
            result_data.put("X_RESULTINFO".toUpperCase(), errRspDesc);
            result_data.put("X_RSPCODE", "2998");
            result_data.put("X_RSPDESC", IntfField.DATABASE_ERR[1]);
            result_data.put("X_RESULTINFO_DETAIL".toUpperCase(), errorInfo.getString("ERROR_DETAIL", ""));// 详细错误
            result_data.put("X_RSPTYPE", "2");
            ids.add(result_data);
            recordBBossXMLMainInfo(data,"1");
            return ids;
        }
    }
}
