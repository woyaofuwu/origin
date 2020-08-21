
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.sysorg;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class DepartInfoQry
{
    /**
     * 查询代理商
     * 
     * @param agentCode
     * @param validFlag
     * @return
     * @throws Exception
     */
    public static IDataset getAgentDepart(String agentCode, String validFlag) throws Exception
    {
        IData param = new DataMap();
        param.put("AGENT_CODE", agentCode);
        param.put("VALIDFLAG", validFlag);
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT T.DEPART_ID,T.DEPART_CODE,T.DEPART_NAME,T.AREA_CODE FROM TD_M_DEPART T ");
        parser.addSQL(" WHERE 1 = 1 ");
        parser.addSQL("   AND T.DEPART_CODE = :AGENT_CODE ");
        parser.addSQL("   AND T.VALIDFLAG = :VALIDFLAG ");
        parser.addSQL("   AND T.END_DATE > SYSDATE ");
        return Dao.qryByParse(parser, Route.CONN_SYS);
    }
    
    /**
     * 根据起始终止段查询代理商
     * 
     * @param  
     * @param  startDepartCode,endDepartCode, "0"
     * @return
     * @throws Exception
     */
    public static IDataset getAgentDepart(String startDepartCode, String endDepartCode  ,String validFlag) throws Exception
    {
        IData param = new DataMap();
        param.put("START_AGENT_NO", startDepartCode);
        param.put("END_AGENT_NO", endDepartCode);
        param.put("VALIDFLAG", validFlag);
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT T.DEPART_ID,T.DEPART_CODE,T.DEPART_NAME,T.AREA_CODE FROM TD_M_DEPART T ");
        parser.addSQL(" WHERE 1 = 1 ");
        parser.addSQL("   AND T.DEPART_CODE >= :START_AGENT_NO ");
        parser.addSQL("   AND T.DEPART_CODE <= :END_AGENT_NO ");        
        parser.addSQL("   AND T.VALIDFLAG = :VALIDFLAG ");
        parser.addSQL("   AND T.END_DATE > SYSDATE ");
        return Dao.qryByParse(parser, Route.CONN_SYS);
    }

    /**
     * 根据eparchyCode,cityCode,departId,strParaCode查询代理商信息
     * 
     * @author chenzm
     * @param eparchyCode
     * @param cityCode
     * @param departId
     * @param strParaCode
     * @returnIDataset
     * @throws Exception
     */
    public static IDataset getAgentInfoByDepartId(String eparchyCode, String cityCode, String departId, String strParaCode) throws Exception
    {
        IData param = new DataMap();
        param.put("CITY_CODE", cityCode);
        param.put("EPARCHY_CODE", eparchyCode);
        param.put("PARA_CODE2", strParaCode);
        param.put("DEPART_ID", departId);
        return Dao.qryByCode("TD_M_DEPART", "SEL_BY_ID_OR_NAME", param, Route.CONN_SYS);
    }

    /**
     * 根据eparchyCode,cityCode,strParaCode查询代理商信息
     * 
     * @author chenzm
     * @param eparchyCode
     * @param cityCode
     * @param strParaCode
     * @returnIDataset
     * @throws Exception
     */
    public static IDataset getAgentInfoByNotManagerId(String eparchyCode, String cityCode, String strParaCode) throws Exception
    {
        IData param = new DataMap();
        param.put("CITY_CODE", cityCode);
        param.put("EPARCHY_CODE", eparchyCode);
        param.put("PARA_CODE2", strParaCode);
        return Dao.qryByCode("TD_M_DEPART", "SEL_BY_ID_OR_NAME", param, Route.CONN_SYS);
    }

    /**
     * 根据area_code查询DEPART_ID, DEPART_NAME, depart_code信息
     * 
     * @param rsvalue2
     * @param area_code
     * @param validflag
     *            '0'
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset getDepartByAreaCode(String rsvalue2, String area_code, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("RSVALUE2", rsvalue2);
        param.put("AREA_CODE", area_code);

        return Dao.qryByCode("TD_M_DEPART", "SEL_BY_DEVELOP_DEPART", param, pagination, Route.CONN_SYS);
    }

    /**
     * 返回某地市的部门列表
     * 
     * @param eparchyCode
     * @param cityCode
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-6-9
     */
    public static IDataset getDeptSelectDatas(String eparchyCode, String cityCode) throws Exception
    {
        IData param = new DataMap();
        param.put("AREA_CODE", cityCode);
        param.put("RSVALUE2", eparchyCode);

        IDataset dataset = new DatasetList();

        IData allDepts = new DataMap();
        allDepts.put("DEPART_NAME_ID", "所有部门");
        allDepts.put("DEPART_ID", "");

        dataset.add(allDepts);
        dataset.addAll(Dao.qryByCode("TD_M_DEPART", "SEL_QUDAO", param, Route.CONN_SYS));

        return dataset;
    }
    public static IDataset getDepartById(String departId) throws Exception
	{
		IData data = new DataMap();
		data.put("DEPART_ID", departId);
		return Dao.qryByCode("TD_M_DEPART", "SEL_BY_DEPART_ID", data, Route.CONN_SYS);
	}

    /**
     * 功能说明：查询部门类别列表
     * 
     * @author luojh 2009-3-4 10:54
     * @param data
     * @return 统计结果集
     */
    public static IDataset queryDepartkindList(String eparchyCode) throws Exception
    {
        IData data = new DataMap();
        data.put("EPARCHY_CODE", eparchyCode);
        return Dao.qryByCode("TD_M_DEPARTKIND", "SEL_BY_ALL", data, Route.CONN_SYS);
    }
    
    public static boolean checkDepartIsParentDepart(String departId, String parentDepartId, String validFlag) throws Exception
    {
        IData param = new DataMap();
        param.put("VALIDFLAG", validFlag);
        param.put("PARENT_DEPART_ID", parentDepartId);
        param.put("DEPART_ID", departId);
        
        IDataset datas=Dao.qryByCode("TD_M_DEPART", "CHECK_DEPART_IS_PARENT", param, Route.CONN_SYS);
        if(IDataUtil.isNotEmpty(datas)){
        	return true;
        }else{
        	return false;
        }
    }
}
