
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;

public class RelaXxtInfoQry
{
    /**
     * 查询同一集团 同一个付费号 某异号下某一学生的数据
     *
     * @param serialNumber
     * @param outSn
     * @param elementId
     * @param ecUserId
     * @return
     * @throws Exception
     */
    public static IDataset qryOutSnInfobySnaAndSnbAndEleId(String serialNumber, String outSn, String elementId, String ecUserId) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER_A", serialNumber);
        param.put("SERIAL_NUMBER_B", outSn);
        param.put("ELEMENT_ID", elementId);
        param.put("EC_USER_ID", ecUserId);
        IDataset xxtOutMemInfo = Dao.qryByCode("TF_F_RELATION_XXT", "SEL_XXT_BY_SNA_SNB_ECUSERID_ELEMENTID", param);
        return xxtOutMemInfo;
    }

    /**
     * 查询同一集团 同一个付费号下某异号的数据
     *
     * @param serialNumber
     * @param userId
     * @return
     * @throws Exception
     */
    public static IDataset qryOutSnInfobyStuKey(String serialNumber, String outSn, String stuKey, String ecUserId) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER_A", serialNumber);
        param.put("SERIAL_NUMBER_B", outSn);
        param.put("RSRV_STR1", stuKey);
        param.put("EC_USER_ID", ecUserId);
        IDataset xxtOutMemInfo = Dao.qryByCode("TF_F_RELATION_XXT", "SEL_XXT_BY_STUKEY_OUTSN", param);
        return xxtOutMemInfo;
    }

    public static IDataset qryOutSnInfobyStuKeyThisMonthEnd(String serialNumber, String outSn, String stuKey, String ecUserId) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER_A", serialNumber);
        param.put("SERIAL_NUMBER_B", outSn);
        param.put("RSRV_STR1", stuKey);
        param.put("EC_USER_ID", ecUserId);

        IDataset xxtOutMemInfo = Dao.qryByCode("TF_F_RELATION_XXT", "SEL_XXT_BY_STUKEY_OUTSN_THISMONTH_END", param);
        return xxtOutMemInfo;
    }


    /**
     * 查询这个月是否有退订相同组的资费，如果有，则日期截止到上个月底
     *
     * @param userId
     * @param serialNumberB
     * @param type
     * @return
     * @throws Exception
     *             (mebUserId, outSn, GrpUserId, rsrv_str2)
     */
    public static IDataset qryStuDisInfoBySnAUserIdASnBStr2(String userIda, String outSn, String GrpUserId, String rsrv_str2) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID_A", userIda);
        param.put("SERIAL_NUMBER_B", outSn);
        param.put("EC_USER_ID", GrpUserId);
        param.put("GROUP", rsrv_str2);
        IDataset xxtOutMemInfo = Dao.qryByCode("TF_F_RELATION_XXT", "SEL_THISMON_ORDERED_DISCNT", param);
        return xxtOutMemInfo;
    }

    /**
     * 查询校讯通学生参数信息
     *
     * @param serialNumber
     * @param userId
     * @param serialNumberB
     * @param type
     * @return
     * @throws Exception
     */
    public static IDataset qryStuDisParamInfoBySnAUserIdASnBDsiType(String serialNumber, String userId, String serialNumberB, String type) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER_A", serialNumber);
        param.put("EC_USER_ID", userId);
        param.put("SERIAL_NUMBER_B", serialNumberB);
        param.put("ELEMENT_TYPE_CODE", type);
        IDataset xxtOutMemInfo = Dao.qryByCode("TF_F_RELATION_XXT", "SEL_XXT_DISCNT_BY_SNA_GRPUSERID_SNB_TYPECODE", param);
        return xxtOutMemInfo;
    }

    /**
     * 查询同一集团 同一个付费号下所有代付号码
     *
     * @param serialNumber
     * @param userId
     * @return
     * @throws Exception
     */
    public static IDataset queryMemInfoByOutSnandUserIdA(String outSn, String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER_B", outSn);
        param.put("EC_USER_ID", userId);
        IDataset xxtOutMemInfo = Dao.qryByCode("TF_F_RELATION_XXT", "CHECK_MUTIL_NUMBER", param);
        return xxtOutMemInfo;
    }

    /**
     * 查询同一集团 同一个付费号下某异号的所有数据
     *
     * @param outSn
     * @param mainSn
     * @param grpUserId
     * @return
     * @throws Exception
     */
    public static IDataset queryMemInfoByOutSnMebUserIdGrpUserId(String outSn, String mainSn, String grpUserId) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER_B", outSn);
        param.put("SERIAL_NUMBER_A", mainSn);
        param.put("EC_USER_ID", grpUserId);
        IDataset xxtOutMemInfo = Dao.qryByCode("TF_F_RELATION_XXT", "SEL_XXT_OUTSN_MAINSN_ECUSERID", param);
        return xxtOutMemInfo;
    }

    /**
     * 查询同一集团 同一个付费号下所有代付号码
     *
     * @param serialNumber
     * @param userId
     * @return
     * @throws Exception
     */
    public static IDataset queryMemInfoBySNandUserIdA(String serialNumber, String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER_A", serialNumber);
        param.put("EC_USER_ID", userId);
        IDataset xxtOutMemInfo = Dao.qryByCode("TF_F_RELATION_XXT", "SEL_BY_USRID1", param);
        return xxtOutMemInfo;
    }

    /**
     * 查询学护卡家长号码订购校讯通产品的信息
     * 用户学护卡
     * @param serialNumber
     * @return
     * @throws Exception
     */
    public static IDataset qryMemInfoBySNandUserIdA(String serialNumber) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER_A", serialNumber);
        IDataset xxtOutMemInfo = Dao.qryByCode("TF_F_RELATION_XXT", "SEL_BY_SN_ECUSRID", param);
        return xxtOutMemInfo;
    }
    
    
    

    /*
     * 根据SERIAL_NUMBER_A或者SERIAL_NUMBER_B查询TF_F_RELATION_XXT 校讯通异网号码互查 界面使用
     */
    public static IDataset queryXxtInfoBySnaOrSnb(IData param) throws Exception
    {
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT A.USER_ID_A,A.SERIAL_NUMBER_A,A.SERIAL_NUMBER_B,                     ");
        parser.addSQL("        A.ELEMENT_TYPE_CODE,A.ELEMENT_ID AS POINT_CODE, A.INST_ID,                          ");
        parser.addSQL("        A.START_DATE,A.END_DATE,A.NAME,A.EC_USER_ID,A.RELA_INST_ID,A.SERVICE_ID,A.RSRV_STR1    ");
        parser.addSQL("   FROM TF_F_RELATION_XXT A                                        ");
        parser.addSQL("  WHERE 1 = 1                                                          ");
        parser.addSQL(" AND A.SERIAL_NUMBER_A=:SERIAL_NUMBER_A                                ");
        parser.addSQL(" AND A.SERIAL_NUMBER_B=:SERIAL_NUMBER_B                                ");
        parser.addSQL(" AND A.end_date>SYSDATE                                                ");

        IDataset dataset = Dao.qryByParse(parser);
        if(IDataUtil.isNotEmpty(dataset))
        {
        	for(int i=0;i<dataset.size();i++)
        	{
        		IData data = dataset.getData(i);
        		String pointCode = data.getString("POINT_CODE");
        		String pointName = UDiscntInfoQry.getDiscntNameByDiscntCode(pointCode);
        		data.put("POINT_NAME", pointName);
        	}
        }
        return dataset;
    }

    /*
     * 根据SERIAL_NUMBER_A或者SERIAL_NUMBER_B查询TF_F_RELATION_XXT 校讯通异网号码互查 界面使用
     */
    public static IDataset queryXxtInfoBySnaGroup(IData param) throws Exception
    {
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT USER_ID_A,SERIAL_NUMBER_A,SERIAL_NUMBER_B,                     ");
        parser.addSQL("        ELEMENT_TYPE_CODE,ELEMENT_ID, INST_ID,                          ");
        parser.addSQL("        START_DATE,END_DATE,NAME,EC_USER_ID,RELA_INST_ID,SERVICE_ID,RSRV_STR1    ");
        parser.addSQL("   FROM TF_F_RELATION_XXT a                                            ");
        parser.addSQL("  WHERE 1 = 1                                                          ");
        parser.addSQL(" AND a.SERIAL_NUMBER_A=:SERIAL_NUMBER_A                                ");
        parser.addSQL(" AND a.SERIAL_NUMBER_B=:SERIAL_NUMBER_B                                ");
        parser.addSQL(" AND a.EC_USER_ID <> :EC_USER_ID                                       ");
        parser.addSQL(" AND a.end_date>SYSDATE                                                ");

        return Dao.qryByParse(parser);
    }

    public static IDataset qrymsisdn(IData param) throws Exception
    {
    	SQLParser parser = new SQLParser(param);
        parser.addSQL(" select a.* from td_m_msisdn a");
        parser.addSQL(" where 1=1 ");
        parser.addSQL(" and called_type = '1' ");
        parser.addSQL(" and :SERIAL_NUMBER between a.begin_msisdn and a.end_msisdn ");
        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
    }

    public static IDataset qryMemInfoBySNForUIP(String serialNumber) throws Exception
    { 
        IData param = new DataMap();
        param.put("SERIAL_NUMBER_A", serialNumber);
        IDataset xxtOutMemInfo = Dao.qryByCode("TF_F_RELATION_XXT", "SEL_BY_SN_FOR_UIP", param);
        return xxtOutMemInfo;
    }

    public static IDataset qryMemInfoBySNForUIPDestroy(String serialNumber, String ecUserId) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER_A", serialNumber);
        param.put("EC_USER_ID", ecUserId);
        IDataset xxtOutMemInfo = Dao.qryByCode("TF_F_RELATION_XXT", "SEL_BY_SN_FOR_DESTROY", param);
        
        if (IDataUtil.isNotEmpty(xxtOutMemInfo))
        {
            for (int i = 0, size = xxtOutMemInfo.size(); i < size; i++)
            {
                IData iData = xxtOutMemInfo.getData(i);
                IData discntInfoData = UDiscntInfoQry.getDiscntInfoByPk(iData.getString("ELEMENT_ID"));
                iData.put("DISCNT_NAME", discntInfoData.getString("DISCNT_NAME"));
                iData.put("BILLFLG", discntInfoData.getString("BILLFLG"));
                iData.put("PRICE", discntInfoData.getString("PRICE"));
            }
        }
        
        return xxtOutMemInfo;
    }
    
    public static IDataset getRelationXxtIData(String serialNumber) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER_A", serialNumber);
        IDataset xxtOutMemInfo = Dao.qryByCode("TF_F_RELATION_XXT", "SEL_BY_SERIAL_NUMBER_A", param);
        return xxtOutMemInfo;
    }

    public static IDataset getRelationXxtData(String serialNumber) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER_A", serialNumber);
        IDataset xxtOutMemInfo = Dao.qryByCode("TF_F_RELATION_XXT", "SEL_BY_SERIAL_NUMBER_A_1", param);
        return xxtOutMemInfo;
    }
    
    public static IDataset getSerialNumberIData(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId); 
        IDataset xxtOutMemInfo = Dao.qryByCode("TF_F_USER", "SEL_BY_USER_ID_STR", param);
        return xxtOutMemInfo;
    } 
    
    /*
     * 根据SERIAL_NUMBER_A查询TF_F_RELATION_XXT 校讯通异网号码互查 界面使用
     */
    public static IDataset queryXxtInfoBySnGroup(IData param) throws Exception
    {
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT USER_ID_A,SERIAL_NUMBER_A,SERIAL_NUMBER_B,                     ");
        parser.addSQL("        ELEMENT_TYPE_CODE,ELEMENT_ID, INST_ID,                          ");
        parser.addSQL("        START_DATE,END_DATE,NAME,EC_USER_ID,RELA_INST_ID,SERVICE_ID,RSRV_STR1    ");
        parser.addSQL("   FROM TF_F_RELATION_XXT a                                            ");
        parser.addSQL("  WHERE 1 = 1                                                          ");
        parser.addSQL(" AND a.SERIAL_NUMBER_A=:SERIAL_NUMBER_A                                ");
        //parser.addSQL(" AND a.SERIAL_NUMBER_B=:SERIAL_NUMBER_B                                ");
        parser.addSQL(" AND a.EC_USER_ID <> :EC_USER_ID                                       ");
        parser.addSQL(" AND a.end_date>SYSDATE                                                ");

        return Dao.qryByParse(parser);
    }

    /**REQ201910140021_关于和教育互动业务成员批量变更归属学校的需求
     *add by sundz
     * @param ecUserId
     * @return
     * @throws Exception
     */
    public static IDataset qryMemInfoByECANDUserIdA(String ecUserId,String userIdA,String serviceId) throws Exception
    {
        IData param = new DataMap();
        param.put("EC_USER_ID", ecUserId);
        param.put("USER_ID_A", userIdA);
        param.put("SERVICE_ID", serviceId);
        IDataset xxtOutMemInfo = Dao.qryByCode("TF_F_RELATION_XXT", "SEL_BY_EC_USER_ID_A_AND_SERVICE_ID", param);
        return xxtOutMemInfo;
    }


}
