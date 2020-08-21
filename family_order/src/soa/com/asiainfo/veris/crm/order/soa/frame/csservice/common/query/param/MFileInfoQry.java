
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class MFileInfoQry
{

    /**
     * 查询上传文件的状态，9表示已经完成上传
     * 
     * @param file_name
     * @param file_kind
     * @return
     * @author shenzw3
     */

    public static boolean getFileLoaded(String file_name, String file_kind) throws Exception
    {

        // 传进来的file_name即file_id+.文件后缀 ,所以需要截取
        String[] file_id = new String[2];
        if (file_name.indexOf(".") > -1)
        {
            file_id = file_name.split("\\.");
        }
        else
        {
            return false;
        }
        // getVisit().setRouteEparchyCode( CSBizBean.getTradeEparchyCode());

        if ("".equals(StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_M_FILE", new String[]
        { "FILE_ID" }, "FILE_KIND", new String[]
        { file_id[0] })))
        {
            return false;
        }

        if (StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_M_FILE", new String[]
        { "FILE_ID" }, "FILE_KIND", new String[]
        { file_id[0] }).equals(file_kind))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * @Description:文件是否已经上传
     * @author hud
     * @date
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     *             （表未找到 路由无法判断 暂时屏蔽掉）
     */
    // public static IDataset ifFileSend(String fileName) throws Exception
    // {
    // IData param = new DataMap();
    // param.put("FILE_NAME", fileName);
    //		
    // SQLParser parser = new SQLParser(param);
    // parser.addSQL(" select 1 from tl_b_ibfiletrade_plus a, tl_b_ibfiletrade b");
    // parser.addSQL(" where  a.sysid = b.sysid ");
    // parser.addSQL(" where a.trade_id = b.trade_id");
    // parser.addSQL(" and a.file_name =:FILE_NAME");
    // parser.addSQL(" and b.busi_type = 'BBSS_Attachment'");
    // parser.addSQL(" and b.dealtag = '50' ");
    // parser.addSQL(" and rownum = 1");
    // return Dao.qryByParse(parser, new Pagination());
    // }

    /*
     * @description 根据文件编号查询文件对应的信息
     * @author xunyl
     * @date 2013-12-04
     */
    public static IDataset qryFileInfoListByFileID(String fileId) throws Exception
    {
        IData data = new DataMap();
        data.put("FILE_ID", fileId);

        SQLParser parser = new SQLParser(data);
        parser.addSQL("select t.file_id,t.file_name,t.ftp_site,t.file_path,t.file_type,t.file_kind,");
        parser.addSQL("round(t.file_size/1024,2)||'K' file_size,t.crea_staff,t.crea_time ");
        parser.addSQL("FROM  WD_F_FTPFILE t ");
        parser.addSQL("WHERE 1=1 ");
        parser.addSQL("AND t.file_id =:FILE_ID ");

        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
    }

    /**
     * 根据STAFF_ID查询该员工导入的文件类型
     * 
     * @author zhujm 2009-03-06
     * @param cycle
     * @throws Throwable
     */
    public static IDataset queryFileInfo(String createStaffID, String fileName, String startDate, String endDate, String fileCode, Pagination page) throws Exception
    {
        IData data = new DataMap();
        data.put("CREATE_STAFF_ID", createStaffID.toUpperCase());
        data.put("FILE_NAME", fileName);
        data.put("START_DATE", startDate);
        data.put("END_DATE", endDate);
        data.put("FILE_CODE", fileCode);

        SQLParser parser = new SQLParser(data);
        parser.addSQL("select t.file_id,t.file_name,t.ftp_site,t.file_path,t.file_type,t.file_kind,");
        parser.addSQL("round(t.file_size/1024,2)||'K' file_size,t.crea_staff,t.crea_time ");
        parser.addSQL("FROM  WD_F_FTPFILE t ");
        parser.addSQL("where 1=1");
        parser.addSQL(" AND t.crea_staff=:CREATE_STAFF_ID ");
        parser.addSQL(" AND t.file_name LIKE '%'||:FILE_NAME||'%'");
        parser.addSQL(" AND t.file_id=:FILE_CODE");
        parser.addSQL(" AND t.crea_time>=to_date(:START_DATE,'YYYY-MM-DD')");
        parser.addSQL(" AND t.crea_time<=to_date(:END_DATE,'YYYY-MM-DD')+1 ");
        // 查所有集团的
        parser.addSQL(" AND t.ftp_site='groupserv' ORDER BY crea_time");

        return Dao.qryByParse(parser, page, Route.CONN_CRM_CEN);
    }

    public static IDataset queryFileInfobyId(String fileId) throws Exception
    {
        IData data = new DataMap();
        data.put("FILE_ID", fileId);

        SQLParser parser = new SQLParser(data);
        parser.addSQL("SELECT t.file_id,t.file_name,t.file_type,t.crea_staff, ");
        parser.addSQL(" t.crea_time,t.file_path ");
        parser.addSQL("FROM td_m_file t ");
        parser.addSQL("WHERE 1=1 ");
        parser.addSQL("AND t.file_id like :FILE_ID ");

        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);

    }
    
    /**
     * @description 根据文件名称获取文件列表
     * @author xunyl
     * @date 2015-03-30
     */
    public static IDataset qryFileInfoListByFileName(String fileName)throws Exception{
        IData data = new DataMap();
        data.put("FILE_NAME", fileName);
        
        SQLParser parser = new SQLParser(data);
        parser.addSQL("select t.file_id,t.file_name,t.crea_time ");
        parser.addSQL("FROM  WD_F_FTPFILE_BBOSS t ");
        parser.addSQL("WHERE 1=1 ");
        parser.addSQL("AND t.file_name =:FILE_NAME ");

        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
    }
}
