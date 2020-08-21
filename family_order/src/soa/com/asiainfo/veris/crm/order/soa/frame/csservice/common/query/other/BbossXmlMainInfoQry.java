package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

/**
 * @description 该类用于查询IBOSS过来的全网报文数据(包括TF_TP_BBOSS_XML_INFO表和TF_TP_BBOSS_XML_CONTENT表)
 * @author xunyl
 * @date 2015-02-05
 *
 */
public class BbossXmlMainInfoQry {

	protected static final Logger log = Logger.getLogger(BbossXmlMainInfoQry.class);
	
	/**
	 * @description 根据主键seq_id查询全网报文主表信息
	 * @author xunyl
	 * @date 2015-02-05
	 */
	public static IDataset qryXmlMainInfoBySeqId(String seqId)throws Exception{
		IData param = new DataMap();
	    param.put("SEQ_ID", seqId);

	    SQLParser parser = new SQLParser(param);
	    parser.addSQL("SELECT ");
	    parser.addSQL("M.SEQ_ID, ");
	    parser.addSQL("M.PKG_SEQ, ");
        parser.addSQL("M.XML_ACTION, ");
	    parser.addSQL("M.BIPCODE, ");
	    parser.addSQL("M.TRANDS_IDO, ");
	    parser.addSQL("M.PO_SPEC_NUMBER, ");
	    parser.addSQL("M.EC_CUSTOMER_NUMBER, ");
	    parser.addSQL("M.PO_ORDER_NUMBER, ");
	    parser.addSQL("M.PRODUCT_OFFER_ID, ");
	    parser.addSQL("M.PRODUCT_ORDER_NUMBER, ");
	    parser.addSQL("M.MEMBER_ORDER_NUMBER, ");        
        parser.addSQL("M.SERIAL_NUMBER, ");
	    parser.addSQL("M.OPEN_RESULT_CODE, ");
        parser.addSQL("M.OPEN_RESULT_DESC, ");  
        parser.addSQL("M.MEMBER_ORDER_RATE, ");
        parser.addSQL("M.IBOSS_RESULT, ");
	    parser.addSQL("M.LOCATE_TIME, ");
	    parser.addSQL("M.DEAL_TIME, ");
	    parser.addSQL("M.DEAL_STATE, ");
	    parser.addSQL("M.ERROR_INFO_1, ");
	    parser.addSQL("M.ERROR_INFO_2, ");
	    parser.addSQL("M.ERROR_INFO_3, ");
	    parser.addSQL("M.ERROR_INFO_4, ");
	    parser.addSQL("M.ERROR_INFO_5, ");
	    parser.addSQL("M.ERROR_INFO_6, ");
	    parser.addSQL("M.ERROR_INFO_7, ");
	    parser.addSQL("M.ERROR_INFO_8, ");
	    parser.addSQL("M.ERROR_INFO_9, ");
	    parser.addSQL("M.ERROR_INFO_10, ");
	    parser.addSQL("M.RSRV_STR1, ");
	    parser.addSQL("M.RSRV_STR2, ");
	    parser.addSQL("M.RSRV_STR3, ");
	    parser.addSQL("M.RSRV_STR4, ");
	    parser.addSQL("M.RSRV_STR5, ");
	    parser.addSQL("M.RSRV_STR6, ");
	    parser.addSQL("M.RSRV_STR7, ");
	    parser.addSQL("M.RSRV_STR8, ");
	    parser.addSQL("M.RSRV_STR9, ");
	    parser.addSQL("M.RSRV_STR10 ");
	    parser.addSQL("FROM TF_TP_BBOSS_XML_INFO M ");
	    parser.addSQL("WHERE 1=1 ");
	    parser.addSQL("AND M.SEQ_ID = :SEQ_ID ");

	    return Dao.qryByParse(parser,Route.CONN_CRM_CEN);
	}
	
	/**
     * @description 根据主键trans_ido查询全网报文主表信息
     * @author xunyl
     * @date 2015-02-05
     */
    public static IDataset qryXmlMainInfoBytransIdo(String transIdo)throws Exception{
        IData param = new DataMap();
        param.put("TRANDS_IDO", transIdo);

        SQLParser parser = new SQLParser(param);
        parser.addSQL("SELECT ");
        parser.addSQL("M.SEQ_ID, ");
        parser.addSQL("M.XML_ACTION, ");
        parser.addSQL("M.BIPCODE, ");
        parser.addSQL("M.TRANDS_IDO, ");
        parser.addSQL("M.PO_SPEC_NUMBER, ");
        parser.addSQL("M.EC_CUSTOMER_NUMBER, ");
        parser.addSQL("M.PO_ORDER_NUMBER, ");
        parser.addSQL("M.PRODUCT_OFFER_ID, ");
        parser.addSQL("M.PRODUCT_ORDER_NUMBER, ");
        parser.addSQL("M.MEMBER_ORDER_NUMBER, ");        
        parser.addSQL("M.SERIAL_NUMBER, ");
        parser.addSQL("M.OPEN_RESULT_CODE, ");
        parser.addSQL("M.OPEN_RESULT_DESC, "); 
        parser.addSQL("M.MEMBER_ORDER_RATE, ");
        parser.addSQL("M.IBOSS_RESULT, ");
        parser.addSQL("M.LOCATE_TIME, ");
        parser.addSQL("M.DEAL_TIME, ");
        parser.addSQL("M.DEAL_STATE, ");
        parser.addSQL("M.ERROR_INFO_1, ");
        parser.addSQL("M.ERROR_INFO_2, ");
        parser.addSQL("M.ERROR_INFO_3, ");
        parser.addSQL("M.ERROR_INFO_4, ");
        parser.addSQL("M.ERROR_INFO_5, ");
        parser.addSQL("M.ERROR_INFO_6, ");
        parser.addSQL("M.ERROR_INFO_7, ");
        parser.addSQL("M.ERROR_INFO_8, ");
        parser.addSQL("M.ERROR_INFO_9, ");
        parser.addSQL("M.ERROR_INFO_10, ");
        parser.addSQL("M.RSRV_STR1, ");
        parser.addSQL("M.RSRV_STR2, ");
        parser.addSQL("M.RSRV_STR3, ");
        parser.addSQL("M.RSRV_STR4, ");
        parser.addSQL("M.RSRV_STR5, ");
        parser.addSQL("M.RSRV_STR6, ");
        parser.addSQL("M.RSRV_STR7, ");
        parser.addSQL("M.RSRV_STR8, ");
        parser.addSQL("M.RSRV_STR9, ");
        parser.addSQL("M.RSRV_STR10 ");
        parser.addSQL("FROM TF_TP_BBOSS_XML_INFO M ");
        parser.addSQL("WHERE 1=1 ");
        parser.addSQL("AND M.TRANDS_IDO = :TRANDS_IDO ");

        return Dao.qryByParse(parser,Route.CONN_CRM_CEN);
    }

    /**
     * @description 获取延时工单数据
     * @author xunyl
     * @date 2015-02-05
     */
    public static IDataset qryXmlMainInfoListByDealState(String dealState,String chanelCode,String threadCount)throws Exception{
        IData param = new DataMap();
        param.put("DEAL_STATE", dealState);
        param.put("CHANEL_CODE", chanelCode);
        param.put("THREAD_COUNT", threadCount);
        
        SQLParser parser = new SQLParser(param);
        parser.addSQL("SELECT ");
        parser.addSQL("M.SEQ_ID, ");
        parser.addSQL("M.XML_ACTION, ");
        parser.addSQL("M.BIPCODE, ");
        parser.addSQL("M.TRANDS_IDO, ");
        parser.addSQL("M.PO_SPEC_NUMBER, ");
        parser.addSQL("M.EC_CUSTOMER_NUMBER, ");
        parser.addSQL("M.PO_ORDER_NUMBER, ");
        parser.addSQL("M.PRODUCT_OFFER_ID, ");
        parser.addSQL("M.PRODUCT_ORDER_NUMBER, ");
        parser.addSQL("M.MEMBER_ORDER_NUMBER, ");
        parser.addSQL("M.SERIAL_NUMBER, ");
        parser.addSQL("M.OPEN_RESULT_CODE, ");
        parser.addSQL("M.OPEN_RESULT_DESC, ");  
        parser.addSQL("M.MEMBER_ORDER_RATE, ");
        parser.addSQL("M.IBOSS_RESULT, ");
        parser.addSQL("M.LOCATE_TIME, ");
        parser.addSQL("M.DEAL_TIME, ");
        parser.addSQL("M.DEAL_STATE, ");
        parser.addSQL("M.ERROR_INFO_1, ");
        parser.addSQL("M.ERROR_INFO_2, ");
        parser.addSQL("M.ERROR_INFO_3, ");
        parser.addSQL("M.ERROR_INFO_4, ");
        parser.addSQL("M.ERROR_INFO_5, ");
        parser.addSQL("M.ERROR_INFO_6, ");
        parser.addSQL("M.ERROR_INFO_7, ");
        parser.addSQL("M.ERROR_INFO_8, ");
        parser.addSQL("M.ERROR_INFO_9, ");
        parser.addSQL("M.ERROR_INFO_10, ");  
        parser.addSQL("M.RSRV_STR1, ");
        parser.addSQL("M.RSRV_STR2, ");
        parser.addSQL("M.RSRV_STR3, ");
        parser.addSQL("M.RSRV_STR4, ");
        parser.addSQL("M.RSRV_STR5, ");
        parser.addSQL("M.RSRV_STR6, ");
        parser.addSQL("M.RSRV_STR7, ");
        parser.addSQL("M.RSRV_STR8, ");
        parser.addSQL("M.RSRV_STR9, ");
        parser.addSQL("M.RSRV_STR10 "); 
        parser.addSQL("FROM TF_TP_BBOSS_XML_INFO M ");
        parser.addSQL("WHERE 1=1 ");
        parser.addSQL("AND M.DEAL_STATE = :DEAL_STATE ");
        parser.addSQL("AND SUBSTR(M.SERIAL_NUMBER,11,1) = :CHANEL_CODE ");
        parser.addSQL("AND ROWNUM <= :THREAD_COUNT ");
        parser.addSQL("ORDER BY M.LOCATE_TIME ASC ");
        
        return Dao.qryByParse(parser,Route.CONN_CRM_CEN);
    }

	/**
	 * @description 获取延时工单报文数据
	 * @author xunyl
	 * @date 2015-02-27
	 */
	public static IDataset qryXmlContentInfoBySeqId(String seqId)throws Exception{
		IData param = new DataMap();
	    param.put("SEQ_ID", seqId);

	    SQLParser parser = new SQLParser(param);
	    parser.addSQL("SELECT ");
	    parser.addSQL("M.SEQ_ID, ");
	    parser.addSQL("M.TRANDS_IDO, ");
	    parser.addSQL("M.XML_CONTENT_1, ");
	    parser.addSQL("M.XML_CONTENT_2, ");
	    parser.addSQL("M.XML_CONTENT_3, ");
	    parser.addSQL("M.XML_CONTENT_4, ");
	    parser.addSQL("M.XML_CONTENT_5, ");
	    parser.addSQL("M.XML_CONTENT_6, ");
	    parser.addSQL("M.XML_CONTENT_7, ");
	    parser.addSQL("M.XML_CONTENT_8, ");
	    parser.addSQL("M.XML_CONTENT_9, ");
	    parser.addSQL("M.XML_CONTENT_10, ");
	    parser.addSQL("M.RSRV_STR1, ");
	    parser.addSQL("M.RSRV_STR2, ");
	    parser.addSQL("M.RSRV_STR3, ");
	    parser.addSQL("M.RSRV_STR4, ");
	    parser.addSQL("M.RSRV_STR5, ");
	    parser.addSQL("M.RSRV_STR6, ");
	    parser.addSQL("M.RSRV_STR7, ");
	    parser.addSQL("M.RSRV_STR8, ");
	    parser.addSQL("M.RSRV_STR9, ");
	    parser.addSQL("M.RSRV_STR10 ");
	    parser.addSQL("FROM TF_TP_BBOSS_XML_CONTENT M ");
	    parser.addSQL("WHERE 1=1 ");
	    parser.addSQL("AND M.SEQ_ID = :SEQ_ID ");

	    return Dao.qryByParse(parser,Route.CONN_CRM_CEN);
	}

	/**
     * @descirption 根据TRANDS_IDO和DEAL_STATE查询报文数据
     * @author xunyl
     * @date 2015-09-23
     */
    public static IDataset qryXmlMainInfo(String transIdo,String dealState)throws Exception{
        IData param = new DataMap();
        param.put("TRANDS_IDO", transIdo);
        param.put("DEAL_STATE", dealState);
        
        SQLParser parser = new SQLParser(param);
        parser.addSQL("SELECT ");
        parser.addSQL("M.SEQ_ID, ");
        parser.addSQL("M.XML_ACTION, ");
        parser.addSQL("M.BIPCODE, ");
        parser.addSQL("M.TRANDS_IDO, ");
        parser.addSQL("M.PO_SPEC_NUMBER, ");
        parser.addSQL("M.EC_CUSTOMER_NUMBER, ");
        parser.addSQL("M.PO_ORDER_NUMBER, ");
        parser.addSQL("M.PRODUCT_OFFER_ID, ");
        parser.addSQL("M.PRODUCT_ORDER_NUMBER, ");
        parser.addSQL("M.MEMBER_ORDER_NUMBER, ");        
        parser.addSQL("M.SERIAL_NUMBER, ");
        parser.addSQL("M.OPEN_RESULT_CODE, ");
        parser.addSQL("M.OPEN_RESULT_DESC, ");  
        parser.addSQL("M.MEMBER_ORDER_RATE, ");
        parser.addSQL("M.IBOSS_RESULT, ");
        parser.addSQL("M.LOCATE_TIME, ");
        parser.addSQL("M.DEAL_TIME, ");
        parser.addSQL("M.DEAL_STATE, ");
        parser.addSQL("M.ERROR_INFO_1, ");
        parser.addSQL("M.ERROR_INFO_2, ");
        parser.addSQL("M.ERROR_INFO_3, ");
        parser.addSQL("M.ERROR_INFO_4, ");
        parser.addSQL("M.ERROR_INFO_5, ");
        parser.addSQL("M.ERROR_INFO_6, ");
        parser.addSQL("M.ERROR_INFO_7, ");
        parser.addSQL("M.ERROR_INFO_8, ");
        parser.addSQL("M.ERROR_INFO_9, ");
        parser.addSQL("M.ERROR_INFO_10, ");  
        parser.addSQL("M.RSRV_STR1, ");
        parser.addSQL("M.RSRV_STR2, ");
        parser.addSQL("M.RSRV_STR3, ");
        parser.addSQL("M.RSRV_STR4, ");
        parser.addSQL("M.RSRV_STR5, ");
        parser.addSQL("M.RSRV_STR6, ");
        parser.addSQL("M.RSRV_STR7, ");
        parser.addSQL("M.RSRV_STR8, ");
        parser.addSQL("M.RSRV_STR9, ");
        parser.addSQL("M.RSRV_STR10 "); 
        parser.addSQL("FROM TF_TP_BBOSS_XML_INFO M ");
        parser.addSQL("WHERE 1=1 ");
        parser.addSQL("AND M.TRANDS_IDO = :TRANDS_IDO ");
        parser.addSQL("AND M.DEAL_STATE = :DEAL_STATE ");

        return Dao.qryByParse(parser,Route.CONN_CRM_CEN);
    }
    
    /**
     * @description 删除工单开通完成的单子(针对叠加包和成员的工单开通)
     * @author xunyl
     * @date 2015-09-23
     */
    public static int delXmlMainInfo(String transIdo,String dealState)throws Exception{
        IData param = new DataMap();
        param.put("TRANDS_IDO", transIdo);
        param.put("DEAL_STATE", dealState);
        
        SQLParser parser = new SQLParser(param);
        parser.addSQL("DELETE ");       
        parser.addSQL("TF_TP_BBOSS_XML_INFO M ");
        parser.addSQL("WHERE 1=1 ");
        parser.addSQL("AND M.TRANDS_IDO = :TRANDS_IDO ");
        parser.addSQL("AND M.DEAL_STATE = :DEAL_STATE ");

        return Dao.executeUpdate(parser, Route.CONN_CRM_CEN);
    }
    
    /**
     * @description 删除工单开通完成的单子(针对叠加包和成员的工单开通)
     * @author xunyl
     * @date 2016-04-08
     */
    public static void delXmlMainInfo(IDataset bbossXmlInfoList)throws Exception{
    	//log.info("("====================chenyr test delXmlMainInfo =============== bbossXmlInfoList: " + bbossXmlInfoList);
        //log.info("("====================chenyr test delXmlMainInfo =============== bbossXmlInfoList size: " + bbossXmlInfoList.size());
    	
        if(IDataUtil.isEmpty(bbossXmlInfoList)){
            return ;
        }
        
        Dao.delete("TF_TP_BBOSS_XML_INFO", bbossXmlInfoList, new String[]{"SEQ_ID"}, Route.CONN_CRM_CEN);
//        for(int i=0;i<bbossXmlInfoList.size();i++){
//            IData bbossXmlInfo = bbossXmlInfoList.getData(i);
//            Dao.delete("TF_TP_BBOSS_XML_INFO", dataset, keys, routeId);
//        }
    }
    
    /**
     * @description 更改TF_TP_BBOSS_XML_INFO表状态
     * @author xunyl
     * @date 2016-05-13
     */
    public static void updXmlMainDealState(String seqId,String dealState)throws Exception{
        IData inparam = new DataMap();
        inparam.put("SEQ_ID", seqId);
        inparam.put("DEAL_STATE", dealState);

        StringBuilder sql = new StringBuilder(1000);
        sql.append("update ");
        sql.append("TF_TP_BBOSS_XML_INFO  m ");
        sql.append("set  m.DEAL_STATE=:DEAL_STATE ");
        sql.append(",m.DEAL_TIME=sysdate ");
        sql.append("WHERE m.SEQ_ID = TO_NUMBER(:SEQ_ID) ");        

        Dao.executeUpdate(sql, inparam, Route.CONN_CRM_CEN);
    }
    
    /**
     * @description 更改TF_TP_BBOSS_XML_INFO表类别
     * @author daidl
     * @date 2019-04-16
     */
    public static void updXmlMainXmlAction(String seqId,String xmlaction)throws Exception{
        IData inparam = new DataMap();
        inparam.put("SEQ_ID", seqId);
        inparam.put("XML_ACTION", xmlaction);

        StringBuilder sql = new StringBuilder(1000);
        sql.append("update ");
        sql.append("TF_TP_BBOSS_XML_INFO  m ");
        sql.append("set  m.XML_ACTION=:XML_ACTION, ");
        sql.append("m.DEAL_TIME=sysdate ");
        sql.append("WHERE m.SEQ_ID = TO_NUMBER(:SEQ_ID) ");        

        Dao.executeUpdate(sql, inparam, Route.CONN_CRM_CEN);
    }
    
    /**
	 * @description 根据主键pkgseq,sn查询全网报文主表信息
	 * 
	 * @date 2018-12-13
	 */
	public static IDataset qryXmlMainInfoByPkgseqAndSn(String pkgseq,String sn ,String bipcode )throws Exception{
		IData param = new DataMap();
	    param.put("PKG_SEQ", pkgseq);
	    param.put("SERIAL_NUMBER", sn);
	    param.put("BIPCODE", bipcode);

	    SQLParser parser = new SQLParser(param);
	    parser.addSQL("SELECT ");
	    parser.addSQL("M.SEQ_ID, ");
        parser.addSQL("M.XML_ACTION, ");
	    parser.addSQL("M.BIPCODE, ");
	    parser.addSQL("M.TRANDS_IDO, ");
	    parser.addSQL("M.PO_SPEC_NUMBER, ");
	    parser.addSQL("M.EC_CUSTOMER_NUMBER, ");
	    parser.addSQL("M.PO_ORDER_NUMBER, ");
	    parser.addSQL("M.PRODUCT_OFFER_ID, ");
	    parser.addSQL("M.PRODUCT_ORDER_NUMBER, ");
	    parser.addSQL("M.MEMBER_ORDER_NUMBER, ");        
        parser.addSQL("M.SERIAL_NUMBER, ");
        parser.addSQL("M.PKG_SEQ, ");
	    parser.addSQL("M.OPEN_RESULT_CODE, ");
        parser.addSQL("M.OPEN_RESULT_DESC, ");  
        parser.addSQL("M.MEMBER_ORDER_RATE, ");
        parser.addSQL("M.IBOSS_RESULT, ");
	    parser.addSQL("M.LOCATE_TIME, ");
	    parser.addSQL("M.DEAL_TIME, ");
	    parser.addSQL("M.DEAL_STATE, ");
	    parser.addSQL("M.ERROR_INFO_1, ");
	    parser.addSQL("M.ERROR_INFO_2, ");
	    parser.addSQL("M.ERROR_INFO_3, ");
	    parser.addSQL("M.ERROR_INFO_4, ");
	    parser.addSQL("M.ERROR_INFO_5, ");
	    parser.addSQL("M.ERROR_INFO_6, ");
	    parser.addSQL("M.ERROR_INFO_7, ");
	    parser.addSQL("M.ERROR_INFO_8, ");
	    parser.addSQL("M.ERROR_INFO_9, ");
	    parser.addSQL("M.ERROR_INFO_10, ");
	    parser.addSQL("M.RSRV_STR1, ");
	    parser.addSQL("M.RSRV_STR2, ");
	    parser.addSQL("M.RSRV_STR3, ");
	    parser.addSQL("M.RSRV_STR4, ");
	    parser.addSQL("M.RSRV_STR5, ");
	    parser.addSQL("M.RSRV_STR6, ");
	    parser.addSQL("M.RSRV_STR7, ");
	    parser.addSQL("M.RSRV_STR8, ");
	    parser.addSQL("M.RSRV_STR9, ");
	    parser.addSQL("M.RSRV_STR10 ");
	    parser.addSQL("FROM TF_TP_BBOSS_XML_INFO M ");
	    parser.addSQL("WHERE 1=1 ");
	    parser.addSQL("AND M.PKG_SEQ = :PKG_SEQ ");
	    parser.addSQL("AND M.SERIAL_NUMBER = :SERIAL_NUMBER ");
	    parser.addSQL("AND M.BIPCODE = :BIPCODE ");

	    return Dao.qryByParse(parser,Route.CONN_CRM_CEN);
	}
	
    /**
     * @description 更改TF_TP_BBOSS_XML_INFO表OPEN_RESULT_CODE，OPEN_RESULT_DESC
     * @date 2018-12-13
     */
    public static void updXmlMainopenResultCode(String seqId,String openResultCode,String openResultDesc)throws Exception{
        IData inparam = new DataMap();
        inparam.put("SEQ_ID", seqId);
        inparam.put("OPEN_RESULT_CODE", openResultCode);
        inparam.put("OPEN_RESULT_DESC", openResultDesc);

        StringBuilder sql = new StringBuilder(1000);
        sql.append("update ");
        sql.append("TF_TP_BBOSS_XML_INFO  m ");
        sql.append("set  m.OPEN_RESULT_CODE= :OPEN_RESULT_CODE, ");
        sql.append("m.OPEN_RESULT_DESC= :OPEN_RESULT_DESC, ");
        sql.append("m.DEAL_TIME=sysdate ");
        sql.append("WHERE m.SEQ_ID = :SEQ_ID ");        

        Dao.executeUpdate(sql, inparam, Route.CONN_CRM_CEN);
    }
}
