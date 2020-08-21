package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.sysorg;

import com.ailk.biz.util.StaticUtil;
import com.ailk.bizservice.base.CSBizBean;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UAreaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UStaffInfoQry;

public class StaffInfoQry {

    public static IDataset getStaffAreaInfoByID(String staffId) throws Exception {
        IData data = UStaffInfoQry.qryStaffInfoByPK(staffId);

        if (IDataUtil.isEmpty(data)) {
            return new DatasetList();
        }

        // 业务区编码
        String cityCode = data.getString("CITY_CODE");

        // 业务区名称
        String areaName = UAreaInfoQry.getAreaNameByAreaCode(cityCode);

        IData map = new DataMap();

        map.put("STAFF_NAME", data.getString("STAFF_NAME", ""));
        map.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER", ""));
        map.put("AREA_NAME", areaName);

        IDataset ids = IDataUtil.idToIds(map);

        return ids;
    }

    public static IDataset getStaffDataRightInfoByStaffIdAndDisCode(String staffId, String discntCode) throws Exception {
        IData param = new DataMap();
        param.put("STAFF_ID", staffId);
        param.put("DISCNT_CODE", discntCode);
        SQLParser parser = new SQLParser(param);
        parser.addSQL("SELECT 1 FROM tf_m_staffdataright C,tf_m_roledataright D ");
        parser.addSQL(" WHERE C.staff_id=:STAFF_ID ");
        parser.addSQL(" AND D.data_code=TO_CHAR(:DISCNT_CODE) ");
        parser.addSQL(" AND C.data_code=D.role_code AND C.right_attr='1'AND C.right_tag='1' AND D.data_type='D' ");

        IDataset dataset = Dao.qryByParse(parser, Route.CONN_SYS);
        return dataset;
    }

    /**
     * 获取Role_Id的信息
     * 
     * @param inData
     * @return String
     * @throws Exception
     */
    public static IDataset getStaffRoleInfoByID(String staffId) throws Exception {
        IData param = new DataMap();
        param.put("STAFF_ID", staffId);
        SQLParser parser = new SQLParser(param);
        parser.addSQL("SELECT DISTINCT C.ROLE_CODE, C.ROLE_NAME ");
        parser.addSQL("FROM TF_M_ROLEFUNCRIGHT A, TF_M_STAFFFUNCRIGHT B, TD_M_ROLE C ");
        parser.addSQL("WHERE A.ROLE_CODE(+) = C.ROLE_CODE ");
        parser.addSQL("AND B.RIGHT_CODE = C.ROLE_CODE ");
        parser.addSQL("AND B.RIGHT_ATTR = '1'  AND B.RIGHT_TAG = '1' AND B.STAFF_ID = :STAFF_ID ");
        IDataset dataset = Dao.qryByParse(parser, Route.CONN_SYS);
        return dataset;
    }

    /**
     * 员工是否具有选择该资费的权限
     * 
     * @param staffId
     * @param discntCode
     * @return
     * @throws Exception
     */
    public static IDataset isExistRightByStaffIdAndDiscntCode(String staffId, String discntCode) throws Exception {
        IData param = new DataMap();
        param.put("STAFF_ID", staffId);
        param.put("DISCNT_CODE", discntCode);
        SQLParser parser = new SQLParser(param);
        parser.addSQL("select count(1) AS COUNT_NUM from TF_M_STAFFDATARIGHT C, TF_M_ROLEDATARIGHT R ");
        parser.addSQL("where C.DATA_CODE = R.ROLE_CODE ");
        parser.addSQL("AND C.RIGHT_ATTR = '1' ");
        parser.addSQL("AND C.RIGHT_TAG = '1' ");
        parser.addSQL("AND R.DATA_TYPE = 'D' ");
        parser.addSQL("AND R.DATA_CODE = TO_CHAR(:DISCNT_CODE) ");
        parser.addSQL("AND C.STAFF_ID = :STAFF_ID ");
        parser.addSQL("AND ROWNUM < 2 ");
        IDataset dataset = Dao.qryByParse(parser, Route.CONN_SYS);
        return dataset;
    }

    public static IDataset qryManagerIdJobType(IData param) throws Exception {
        return Dao.qryByCode("TF_F_CUST_MANAGER_STAFF", "SEL_MANAGERID_JOBTYPE", param, Route.CONN_CRM_CEN);
    }

    public static IDataset qryStaffInfoByStaffId(String staffId) throws Exception {
        IData param = new DataMap();
        param.put("STAFF_ID", staffId);
        return Dao.qryByCode("TD_M_STAFF", "SEL_ALL_BY_PK", param, Route.CONN_CRM_CEN);
    }

    /* @description 根据员工编号TD_M_STAFF和TD_M_STAFF_BBOSS连表查询联系人电话和总部用户名
     * 
     * @author xunyl
     * 
     * @date 2013-07-31 */
    public static IDataset qryStaffInfoForBBoss(String staffId) throws Exception {
        IData param = new DataMap();
        param.put("STAFF_ID", staffId);
        return Dao.qryByCode("TD_M_STAFF", "SEL_BY_STAFFID_BBOSS", param, Route.CONN_CRM_CEN);
    }

    public static IDataset queryDataRightByIdNumCode(String staffId, String dataCode, String dataType) throws Exception {
        IData param = new DataMap();
        param.put("STAFF_ID", staffId);
        param.put("DATA_CODE", dataCode);
        param.put("DATA_TYPE", dataType);
        return Dao.qryByCode("TF_M_STAFFDATARIGHT", "SEL_BY_STAFFID_DATACODE", param, Route.CONN_SYS);
    }

    public static IData qryStaffInfoByPK(String staffId) throws Exception {
        IData param = new DataMap();
        param.put("STAFF_ID", staffId);

        IDataset ids = Dao.qryByCode("TD_M_STAFF", "SEL_BY_PK_2", param, Route.CONN_SYS);

        return IDataUtil.isNotEmpty(ids) ? ids.getData(0) : new DataMap();
    }

    /**
     * 查询某个工号的登陆时间限制
     * 
     * @param inparams
     * @return
     * @throws Exception
     */
    public static IDataset queryDateTimeByStaffId(String staffId) throws Exception {
        IData param = new DataMap();
        param.put("STAFF_ID", staffId);
        return Dao.qryByCode("TD_M_STAFF", "SEL_BY_DATETIME", param, Route.CONN_SYS);
    }

    public static IDataset queryGrpRightByIdCode(String staffId, String rightCode, String userProductCode) throws Exception {
        IData param = new DataMap();
        param.put("STAFF_ID", staffId);
        param.put("RIGHT_CODE", rightCode);
        param.put("USER_PRODUCT_CODE", userProductCode);
        return Dao.qryByCodeParser("TF_M_STAFF_GRP_RIGHT", "SEL_SHORTCODERIGHT_BY_STAFF_ID", param, Route.getCrmDefaultDb());
    }

    // todo
    /**
     * 查询电话呼入的信息
     * 
     * @author fengsl
     * @date 2013-02-27
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset queryStaffInfoByNumber(IData param, Pagination pagination) throws Exception {
        SQLParser callInParser = new SQLParser(param);

        callInParser.addSQL(" select t.serial_number LINK_MAN_PHONEN,t.staff_name LINK_MAN_NMAE from td_m_staff t where  t.serial_number=:SERIAL_NUMBER and t.dimission_tag='0' and rownum<2 ");

        return Dao.qryByParse(callInParser, pagination, Route.CONN_CRM_CG);
    }

    public static IDataset queryValidStaffById(String staffId) throws Exception {
        IData param = new DataMap();
        param.put("STAFF_ID", staffId);

        return Dao.qryByCode("TD_M_STAFF", "SEL_VALID_BY_PK", param, Route.CONN_SYS);
    }

    /**
     * @Function:
     * @Description:信控 用户获取同步总部的用户信息
     * @author:chenyi
     * @throws Exception
     * @date: 下午3:32:50 2013-10-23
     */
    public static IDataset qryStaffInfo(IData inpamra) throws Exception {
        SQLParser callInParser = new SQLParser(inpamra);
        callInParser.addSQL(" SELECT T1.STAFF_NUMBER, T2.SERIAL_NUMBER,T2.STAFF_ID,t2.staff_name FROM uop_cen1.TD_M_STAFF_BBOSS T1  RIGHT JOIN uop_cen1.TD_M_STAFF T2 ");
        callInParser.addSQL(" ON T1.STAFF_ID = T2.STAFF_ID  WHERE  SYSDATE BETWEEN T2.START_DATE AND T2.END_DATE  AND rownum<2 ");
        return Dao.qryByParse(callInParser, Route.CONN_CRM_CEN);
    }

    /**
     * 获取操作员工联系号码的信息
     * 
     * @param inData
     * @return String
     * @throws Exception
     */
    public static IDataset getStaffInfo(IData inData) throws Exception {
        IDataset staffInfoset = Dao.qryByCode("TD_M_STAFF", "SEL_BY_PK", inData, Route.CONN_CRM_CEN);

        return staffInfoset;
    }

    /**
     * 根据员工联系号码获取操作员的信息
     * 
     * @param inData
     * @return String
     * @throws Exception
     */
    public static IDataset getStaffInfoByNumber(IData inData) throws Exception {
        IDataset staffInfoset = Dao.qryByCode("TD_M_STAFF", "SEL_BY_NUMBER", inData, Route.CONN_CRM_CEN);

        return staffInfoset;
    }

    /**
     * 根据名称过滤分管客户经理
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset qryFilterManagerIdJobType(IData param) throws Exception {
        SQLParser parser = new SQLParser(param);
        parser.addSQL("SELECT DISTINCT A.CUST_MANAGER_ID,");
        parser.addSQL(" B.CUST_MANAGER_NAME ");
        parser.addSQL(" FROM TF_SM_MANAGER_JOBTYPE A, ");
        parser.addSQL(" TF_F_CUST_MANAGER_STAFF B ");
        parser.addSQL(" WHERE A.CUST_MANAGER_ID = B.CUST_MANAGER_ID ");
        parser.addSQL(" 	AND A.JOB_TYPE IN ('2', '6') ");
        parser.addSQL("		AND B.CUST_MANAGER_NAME LIKE '%' || :CUST_MANAGER_NAME || '%' ");
        parser.addSQL(" ORDER BY A.CUST_MANAGER_ID ASC ");
        IDataset dataset = Dao.qryByParse(parser, Route.CONN_CRM_CEN);
        return dataset;
    }

    /* @description 根据总部用户名查询省工号
     * 
     * @author songxw
     * 
     * @date 2018-10-17 */
    public static IDataset qryStaffInfoByBboss(String staffNumber) throws Exception {
        IData param = new DataMap();
        param.put("STAFF_NUMBER", staffNumber);
        SQLParser parser = new SQLParser(param);
        parser.addSQL("SELECT STAFF_ID,STAFF_NAME FROM TD_M_STAFF_BBOSS ");
        parser.addSQL(" WHERE STAFF_NUMBER = :STAFF_NUMBER ");
        IDataset dataset = Dao.qryByParse(parser, Route.CONN_CRM_CEN);
        return dataset;
    }

    public static IDataset qryCustManagerStaffById(String custManagerId) throws Exception {
        IData inparam = new DataMap();
        inparam.put("CUST_MANAGER_ID", custManagerId);

        return Dao.qryByCode("TF_F_CUST_MANAGER_STAFF", "SEL_BY_PK", inparam, Route.CONN_CRM_CEN);
    }

    public static String getStaffSnByStaffId(String staffId) throws Exception {
        return StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_M_STAFF", "STAFF_ID", "SERIAL_NUMBER", staffId);
    }

    /**
	 * 根据名称过滤代理商
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public static IDataset qryFilterAgentMgrDepartId(IData param) throws Exception 
	{
		IDataset staffInfoset =  Dao.qryByCode("TD_M_DEPART","SEL_AGENT_BY_DEPART_NAME", param, Route.CONN_CRM_CEN);
		
		return staffInfoset;
	}
	
}
