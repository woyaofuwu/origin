
package com.asiainfo.veris.crm.order.soa.frame.bcf.dao;

import org.apache.log4j.Logger;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.statement.Parameter;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;

public final class Dao
{
    private final static Logger logger = Logger.getLogger(Dao.class);

    public final static void callProc(String name, String[] paramName, IData paramValue) throws Exception
    {
        CrmDAO dao = getCrmDAO(null);

        dao.callProc(name, paramName, paramValue);
    }

    public final static void callProc(String name, String[] paramName, IData paramValue, String routeId) throws Exception
    {
        CrmDAO dao = getCrmDAO(routeId);

        dao.callProc(name, paramName, paramValue);
    }

    public final static Object callFunc(String name, String paramNames[], IData params, int returnType) throws Exception
    {
        CrmDAO dao = getCrmDAO(null);

        return dao.callFunc(name, paramNames, params, returnType);
    }

    public final static Object callFunc(String name, String paramNames[], IData params, int returnType, String routeId) throws Exception
    {
        CrmDAO dao = getCrmDAO(routeId);

        return dao.callFunc(name, paramNames, params, returnType);
    }

    public final static boolean delete(String tabName, IData data) throws Exception
    {
        CrmDAO dao = getCrmDAO(null);

        return dao.delete(tabName, data);
    }

    public final static boolean delete(String tabName, IData data, String routeId) throws Exception
    {
        CrmDAO dao = getCrmDAO(routeId);

        return dao.delete(tabName, data);
    }

    public final static boolean delete(String tabName, IData data, String[] keys) throws Exception
    {
        CrmDAO dao = getCrmDAO(null);

        return dao.delete(tabName, data, keys);
    }

    public final static boolean delete(String tabName, IData data, String[] keys, String routeId) throws Exception
    {
        CrmDAO dao = getCrmDAO(routeId);

        return dao.delete(tabName, data, keys);
    }

    public final static int[] delete(String tabName, IDataset dataset, String[] keys, String routeId) throws Exception
    {
        CrmDAO dao = getCrmDAO(routeId);

        return dao.delete(tabName, dataset, keys);
    }

    public final static boolean deleteByRowId(String tabName, IData data) throws Exception
    {
        CrmDAO dao = getCrmDAO(null);

        return dao.delete(tabName, data, new String[]
        { "ROWID" });
    }

    public final static boolean deleteByRowId(String tabName, IData data, String routeId) throws Exception
    {
        CrmDAO dao = getCrmDAO(routeId);

        return dao.delete(tabName, data, new String[]
        { "ROWID" });
    }

    public final static int[] deleteByRowId(String tabName, IDataset dataset) throws Exception
    {
        CrmDAO dao = getCrmDAO(null);

        return dao.delete(tabName, dataset, new String[]
        { "ROWID" });
    }

    public final static int[] deleteByRowId(String tabName, IDataset dataset, String routeId) throws Exception
    {
        CrmDAO dao = getCrmDAO(routeId);

        return dao.delete(tabName, dataset, new String[]
        { "ROWID" });
    }

    public final static int[] executeBatch(StringBuilder sql, IDataset param) throws Exception
    {
        CrmDAO dao = getCrmDAO(null);

        return dao.executeBatch(sql.toString(), param);
    }

    public final static int[] executeBatch(StringBuilder sql, IDataset param, String routeId) throws Exception
    {
        CrmDAO dao = getCrmDAO(routeId);

        return dao.executeBatch(sql.toString(), param);
    }

    public final static int[] executeBatch(StringBuilder sql, Parameter[] params, String routeId) throws Exception
    {
        CrmDAO dao = getCrmDAO(routeId);

        return dao.executeBatch(sql.toString(), params);
    }

    public final static int[] executeBatchByCodeCode(String tabname, String sqlref, IDataset params) throws Exception
    {
        CrmDAO dao = getCrmDAO(null);

        return dao.executeBatchByCodeCode(tabname, sqlref, params);
    }

    public final static int[] executeBatchByCodeCode(String tabname, String sqlref, IDataset params, String routeId) throws Exception
    {
        CrmDAO dao = getCrmDAO(routeId);

        return dao.executeBatchByCodeCode(tabname, sqlref, params);
    }

    public final static int executeUpdate(SQLParser parser) throws Exception
    {
        CrmDAO dao = getCrmDAO(null);

        return dao.executeUpdate(parser);
    }

    public final static int executeUpdate(SQLParser parser, String routeId) throws Exception
    {
        CrmDAO dao = getCrmDAO(routeId);

        return dao.executeUpdate(parser);
    }

    /**
     * @deprecated Method executeUpdate is deprecated
     */
    @Deprecated
    public final static int executeUpdate(StringBuilder sql) throws Exception
    {
        CrmDAO dao = getCrmDAO(null);

        return dao.executeUpdate(sql.toString());
    }

    public final static int executeUpdate(StringBuilder sql, IData param) throws Exception
    {
        CrmDAO dao = getCrmDAO(null);

        return dao.executeUpdate(sql.toString(), param);
    }

    public final static int executeUpdate(StringBuilder sql, IData param, String rountId) throws Exception
    {
        CrmDAO dao = getCrmDAO(rountId);

        return dao.executeUpdate(sql.toString(), param);
    }

    /**
     * @deprecated Method executeUpdate is deprecated
     */
    @Deprecated
    public final static int executeUpdate(StringBuilder sql, String routeId) throws Exception
    {
        CrmDAO dao = getCrmDAO(routeId);

        return dao.executeUpdate(sql.toString());
    }

    public final static int executeUpdateByCodeCode(String tabname, String sqlref, IData param) throws Exception
    {
        CrmDAO dao = getCrmDAO(null);

        return dao.executeUpdateByCodeCode(tabname, sqlref, param);
    }

    public final static int executeUpdateByCodeCode(String tabname, String sqlref, IData param, String routeId) throws Exception
    {
        CrmDAO dao = getCrmDAO(routeId);

        return dao.executeUpdateByCodeCode(tabname, sqlref, param);
    }

    public final static int executeUpdates(SQLParser parser) throws Exception
    {
        CrmDAO dao = getCrmDAO(null);

        return dao.executeUpdate(parser);
    }

    public final static int getCount(String sql, IData param) throws Exception
    {
        CrmDAO dao = getCrmDAO(null);

        return dao.getCount(sql, param);
    }

    /**
     * 指定路由
     * 
     * @param routeId
     * @return
     * @throws Exception
     */
    private final static CrmDAO getCrmDAO(String routeId) throws Exception
    {
        if (StringUtils.isBlank(routeId))
        {
            routeId = BizRoute.getRouteId();

            if (logger.isDebugEnabled())
            {
                logger.debug("getCrmDAO路由连接到[" + routeId + "]");
            }
        }
        else
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("getCrmDAO指定连接到[" + routeId + "]");
            }
        }

        return CrmDAO.createDAO(CrmDAO.class, routeId);
    }

    public final static String getSequence(Class<?> clazz) throws Exception
    {
        String result = getSequence(clazz, null);
        return result;
    }

    public final static String getSequence(Class<?> clazz, String epachyCode) throws Exception
    {
        CrmDAO dao = getCrmDAO(epachyCode);

        String result = "";

        if (StringUtils.isBlank(epachyCode))
        {
            result = dao.getSequence(clazz);
        }
        else
        {
            result = dao.getSequence(clazz, epachyCode);
        }

        return result;
    }
    
    public final static String getSequence(Class<?> clazz, String epachyCode, String acceptTime) throws Exception
    {
        CrmDAO dao = getCrmDAO(epachyCode);

        String result = "";

        if (StringUtils.isBlank(epachyCode))
        {
            result = dao.getSequence(clazz);
        }
        else
        {
            result = dao.getSequence(clazz, epachyCode);
        }
        
        if (StringUtils.isNotBlank(result) && StringUtils.isNotBlank(acceptTime) && result.length() > 8) 
        {
        	result = result.replaceFirst(result.substring(2, 8), SysDateMgr.getChinaDate(acceptTime, "yyMMdd"));
		}

        return result;
    }

    public final static boolean insert(String tabName, IData param) throws Exception
    {
        CrmDAO dao = getCrmDAO(null);

        boolean result = dao.insert(tabName, param);

        return result;
    }

    public final static boolean insert(String tabName, IData param, String routeId) throws Exception
    {
        CrmDAO dao = getCrmDAO(routeId);

        return dao.insert(tabName, param);
    }

    public final static int[] insert(String tabName, IDataset idsRecord) throws Exception
    {
        CrmDAO dao = getCrmDAO(null);

        return dao.insert(tabName, idsRecord);
    }

    public final static int[] insert(String tabName, IDataset param, String routeId) throws Exception
    {
        CrmDAO dao = getCrmDAO(routeId);

        return dao.insert(tabName, param);
    }

    public final static int[] inserts(String tabName, IDataset param) throws Exception
    {
        CrmDAO dao = getCrmDAO(null);

        return dao.insert(tabName, param);
    }

    public final static IDataset qryByCode(String tabName, String sqlref, IData param) throws Exception
    {
        CrmDAO dao = getCrmDAO(null);

        IDataset ids = dao.queryListByCodeCode(tabName, sqlref, param);

        return ids;
    }

    public final static IDataset qryByCode(String tabName, String sqlref, IData param, Pagination page) throws Exception
    {
        CrmDAO dao = getCrmDAO(null);

        IDataset ids = dao.queryListByCodeCode(tabName, sqlref, param, page);

        return ids;
    }

    public final static IDataset qryByCode(String tabName, String sqlref, IData param, Pagination page, String routeId) throws Exception
    {
        CrmDAO dao = getCrmDAO(routeId);

        IDataset ids = dao.queryListByCodeCode(tabName, sqlref, param, page);

        return ids;
    }

    public final static IDataset qryByCode(String tabName, String sqlref, IData param, String routeId) throws Exception
    {
        CrmDAO dao = getCrmDAO(routeId);

        IDataset ids = dao.queryListByCodeCode(tabName, sqlref, param);

        return ids;
    }

    public final static IDataset qryByCodeAllCrm(String tabName, String sqlref, IData param, boolean allCrmDb) throws Exception
    {
        IDataset allData = new DatasetList();
        IDataset oneData = null;

        for (String routeId : Route.getAllCrmDb())
        {
            CrmDAO dao = getCrmDAO(routeId);

            oneData = dao.queryListByCodeCode(tabName, sqlref, param);

            if (IDataUtil.isNotEmpty(oneData))
            {
                allData.addAll(oneData);

                if (!allCrmDb)
                {
                    break;
                }
            }
        }

        return allData;
    }
    
    public final static IDataset qryByCodeAllJour(String tabName, String sqlref, IData param, boolean allJourDb) throws Exception
    {
        IDataset allData = new DatasetList();
        IDataset oneData = null;

        for (String routeId : Route.getAllJourDb())
        {
            CrmDAO dao = getCrmDAO(Route.getJourDb(routeId));

            oneData = dao.queryListByCodeCode(tabName, sqlref, param);

            if (IDataUtil.isNotEmpty(oneData))
            {
                allData.addAll(oneData);

                if (!allJourDb)
                {
                    break;
                }
            }
        }

        return allData;
    }

    /**
     * 总记录数
     * 
     * @param param
     * @param tabName
     * @param sqlref
     * @return
     * @throws Exception
     */
    public final static int qryByCodeAllCrmCount(IData param, String tabName, String sqlref) throws Exception
    {

        IDataset listResult = new DatasetList();
        IDataset listTmp = null;
        CrmDAO dao = null;

        for (String routeId : Route.getAllCrmDb())
        {
            dao = getCrmDAO(routeId);

            listTmp = dao.queryListByCodeCode(tabName, sqlref, param);

            if (!listResult.containsAll(listTmp))
            {
                listResult.addAll(listTmp);
            }
        }

        return listResult.size();
    }

    public final static IData qryByCodeOnlyOne(String tabName, String sqlref, IData param) throws Exception
    {
        CrmDAO dao = getCrmDAO(null);

        IDataset ids = dao.queryListByCodeCode(tabName, sqlref, param);

        if (IDataUtil.isEmpty(ids))
        {
            return null;
        }

        return ids.getData(0);
    }

    public final static IDataset qryByCodeParser(String tabName, String sqlref, IData param) throws Exception
    {
        CrmDAO dao = getCrmDAO(null);

        IDataset ids = dao.queryListByCodeCodeParser(tabName, sqlref, param);

        return ids;
    }

    public final static IDataset qryByCodeParser(String tabName, String sqlref, IData param, Pagination page) throws Exception
    {
        CrmDAO dao = getCrmDAO(null);

        IDataset ids = dao.queryListByCodeCodeParser(tabName, sqlref, param, page);

        return ids;
    }

    public final static IDataset qryByCodeParser(String tabName, String sqlref, IData param, Pagination page, String routeId) throws Exception
    {
        CrmDAO dao = getCrmDAO(routeId);

        IDataset ids = dao.queryListByCodeCodeParser(tabName, sqlref, param, page);

        return ids;
    }

    public final static IDataset qryByCodeParser(String tabName, String sqlref, IData param, String routeId) throws Exception
    {
        CrmDAO dao = getCrmDAO(routeId);

        IDataset ids = dao.queryListByCodeCodeParser(tabName, sqlref, param);

        return ids;
    }

    /**
     * @param param
     * @param tabName
     * @param sqlref
     * @return
     * @throws Exception
     */
    public final static IDataset qryByCodeParserAllCrm(String tabName, String sqlref, IData param, Pagination page, boolean allCrmDb) throws Exception
    {

        IDataset allData = new DatasetList();
        IDataset oneData = null;
        CrmDAO dao = null;

        for (String routeId : Route.getAllCrmDb())
        {
            dao = getCrmDAO(routeId);

            oneData = dao.queryListByCodeCodeParser(tabName, sqlref, param, page);

            if (IDataUtil.isNotEmpty(oneData))
            {
                allData.addAll(oneData);

                if (!allCrmDb)
                {
                    break;
                }
            }
        }

        return allData;
    }

    public final static IDataset qryByParse(SQLParser parser) throws Exception
    {
        CrmDAO dao = getCrmDAO(null);

        IDataset ids = dao.queryList(parser);

        return ids;
    }

    public final static IDataset qryByParse(SQLParser parser, Pagination page) throws Exception
    {
        CrmDAO dao = getCrmDAO(null);

        IDataset ids = dao.queryList(parser, page);

        return ids;
    }

    public final static IDataset qryByParse(SQLParser parser, Pagination page, String routeId) throws Exception
    {
        CrmDAO dao = getCrmDAO(routeId);

        IDataset ids = dao.queryList(parser, page);

        return ids;
    }

    public final static IDataset qryByParse(SQLParser parser, String routeId) throws Exception
    {
        CrmDAO dao = getCrmDAO(routeId);

        IDataset ids = dao.queryList(parser);

        return ids;
    }

    public final static IDataset qryByParseAllCrm(SQLParser parser, boolean allDB) throws Exception
    {
        IDataset idsAll = new DatasetList();
        IDataset idsOne = null;
        CrmDAO dao = null;

        for (String routeId : Route.getAllCrmDb())
        {

            dao = getCrmDAO(routeId);

            idsOne = dao.queryList(parser);

            if (IDataUtil.isNotEmpty(idsOne))
            {
                idsAll.addAll(idsOne);

                if (!allDB)
                {
                    break;
                }
            }
        }

        return idsAll;

    }

    public final static IData qryByPK(String tabName, IData data) throws Exception
    {
        CrmDAO dao = getCrmDAO(null);

        IData idata = dao.queryByPK(tabName, data);

        return idata;
    }

    public final static IData qryByPK(String tabName, IData data, String routeId) throws Exception
    {
        CrmDAO dao = getCrmDAO(routeId);

        IData idata = dao.queryByPK(tabName, data);

        return idata;
    }

    public final static IData qryByPK(String tabName, IData data, String[] keys) throws Exception
    {
        CrmDAO dao = getCrmDAO(null);

        IData idata = dao.queryByPK(tabName, data, keys);
        return idata;
    }

    public final static IData qryByPK(String tabName, IData data, String[] keys, String routeId) throws Exception
    {
        CrmDAO dao = getCrmDAO(routeId);

        IData idata = dao.queryByPK(tabName, data, keys);

        return idata;
    }

    public final static IData qryByPK(String s1, String[] param) throws Exception
    {
        return null;
    }

    public final static IData qryByPK(String tabName, String[] keys, String[] values) throws Exception
    {
        CrmDAO dao = getCrmDAO(null);

        IData idata = dao.queryByPK(tabName, keys, values);

        return idata;
    }

    public final static IData qryByPK(String tabName, String[] keys, String[] values, String routeId) throws Exception
    {
        CrmDAO dao = getCrmDAO(routeId);

        IData idata = dao.queryByPK(tabName, keys, values);

        return idata;
    }

    public final static boolean qryByRecordCount(String tabName, String sqlref, IData param) throws Exception
    {
        String routeId = param.getString("X_CONN_DB_CODE");
        if(StringUtils.isNotEmpty(routeId)&&routeId.equals("jour")){
        	routeId=Route.getJourDb(BizRoute.getRouteId());
        }
        return qryByRecordCount(tabName, sqlref, param, routeId);
    }

    public final static boolean qryByRecordCount(String tabName, String sqlref, IData param, String routeId) throws Exception
    {
        CrmDAO dao = getCrmDAO(routeId);

        IDataset list = dao.queryListByCodeCode(tabName, sqlref, param);

        boolean bResult = false;

        if ("TD_S_CPARAM".equals(tabName))
        {
            bResult = list.getData(0).getInt("RECORDCOUNT") > 0 ? true : false;
        }
        else
        {
            bResult = list.size() > 0 ? true : false;
        }

        return bResult;
    }

    public final static IDataset qryBySql(StringBuilder sql, IData param) throws Exception
    {
        CrmDAO dao = getCrmDAO(null);

        IDataset ids = dao.queryList(sql.toString(), param);

        return ids;
    }

    public final static IDataset qryBySql(StringBuilder sql, IData param, Pagination page) throws Exception
    {
        CrmDAO dao = getCrmDAO(null);

        IDataset ids = dao.queryList(sql.toString(), param, page);

        return ids;
    }

    public final static IDataset qryBySql(StringBuilder sql, IData param, Pagination page, String routeId) throws Exception
    {
        CrmDAO dao = getCrmDAO(routeId);

        IDataset ids = (page == null) ? dao.queryList(sql.toString(), param) : dao.queryList(sql.toString(), param, page);

        return ids;
    }

    public final static IDataset qryBySql(StringBuilder sql, IData param, String routeId) throws Exception
    {
        CrmDAO dao = getCrmDAO(routeId);

        IDataset ids = dao.queryList(sql.toString(), param);

        return ids;
    }

    public final static IDataset qryBySqlAllCrm(StringBuilder sql, IData param, boolean allCrmDb) throws Exception
    {
        IDataset allData = new DatasetList();
        IDataset oneData = null;

        for (String routeId : Route.getAllCrmDb())
        {
            CrmDAO dao = getCrmDAO(routeId);

            oneData = dao.queryList(sql.toString(), param);

            if (IDataUtil.isNotEmpty(oneData))
            {
                allData.addAll(oneData);

                if (!allCrmDb)
                {
                    break;
                }
            }
        }
        return allData;
    }

    public final static boolean save(String tabName, IData param) throws Exception
    {
        CrmDAO dao = getCrmDAO(null);

        boolean result = dao.save(tabName, param);

        return result;
    }

    public final static boolean save(String tabName, IData param, String routeId) throws Exception
    {
        CrmDAO dao = getCrmDAO(routeId);

        return dao.save(tabName, param);
    }

    public final static boolean save(String tabName, IData param, String[] keys) throws Exception
    {
        CrmDAO dao = getCrmDAO(null);

        return dao.save(tabName, param, keys);
    }

    public final static boolean save(String tabName, IData param, String[] keys, String routeId) throws Exception
    {
        CrmDAO dao = getCrmDAO(routeId);

        return dao.save(tabName, param, keys);
    }

    public final static boolean save(String tabName, IData param, String[] keys, String values[]) throws Exception
    {
        CrmDAO dao = getCrmDAO(null);

        return dao.save(tabName, param, keys, values);
    }

    public final static boolean update(String tabName, IData idata) throws Exception
    {
        CrmDAO dao = getCrmDAO(null);

        boolean result = dao.update(tabName, idata);

        return result;
    }

    public final static boolean update(String tabName, IData idata, String[] keys) throws Exception
    {
        CrmDAO dao = getCrmDAO(null);

        boolean result = dao.update(tabName, idata, keys);

        return result;
    }

    public final static boolean update(String tabName, IData idata, String[] keys, String routeId) throws Exception
    {
        CrmDAO dao = getCrmDAO(routeId);

        boolean result = dao.update(tabName, idata, keys);

        return result;
    }

    public final static boolean update(String tabName, IData idata, String[] keys, String[] values) throws Exception
    {
        CrmDAO dao = getCrmDAO(null);

        boolean result = dao.update(tabName, idata, keys, values);

        return result;
    }

    public final static boolean update(String tabName, IData idata, String[] keys, String[] values, String routeId) throws Exception
    {
        CrmDAO dao = getCrmDAO(routeId);

        boolean result = dao.update(tabName, idata, keys, values);

        return result;
    }
}
