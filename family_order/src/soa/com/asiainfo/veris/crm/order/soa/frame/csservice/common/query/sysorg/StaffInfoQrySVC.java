package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.sysorg;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UStaffInfoQry;

public class StaffInfoQrySVC extends CSBizService {

    private static final long serialVersionUID = 3012015752242592866L;

    public static IDataset qryManagerIdJobType(IData param) throws Exception {
        return StaffInfoQry.qryManagerIdJobType(param);
    }

    public static IDataset qryStaffInfoByStaffId(IData param) throws Exception {
        String staffId = param.getString("STAFF_ID");
        return IDataUtil.idToIds(UStaffInfoQry.qryStaffInfoByPK(staffId));
    }

    public static IDataset qryStaffInfoByStaffId4Vpmn(IData param) throws Exception {
        String staffId = param.getString("STAFF_ID");
        return IDataUtil.idToIds(StaffInfoQry.qryStaffInfoByPK(staffId));
    }

    public static IDataset queryDataRightByIdNumCode(IData param) throws Exception {
        String staffId = param.getString("STAFF_ID");
        String dataCode = param.getString("DATA_CODE");
        String dataType = param.getString("DATA_TYPE");
        return StaffInfoQry.queryDataRightByIdNumCode(staffId, dataCode, dataType);
    }

    public static IDataset queryGrpRightByIdCode(IData param) throws Exception {
        String staffId = param.getString("STAFF_ID");
        String rightCode = param.getString("RIGHT_CODE");
        String userProductCode = param.getString("USER_PRODUCT_CODE");
        return StaffInfoQry.queryGrpRightByIdCode(staffId, rightCode, userProductCode);
    }

    /* @description 根据员工编号TD_M_STAFF和TD_M_STAFF_BBOSS连表查询联系人电话和总部用户名
     * 
     * @author xunyl
     * 
     * @date 2013-07-31 */
    public static IDataset queryStaffInfoForBBoss(IData param) throws Exception {
        String staffId = param.getString("STAFF_ID");
        return StaffInfoQry.qryStaffInfoForBBoss(staffId);
    }

    /**
     * 获取操作员工联系号码的信息
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset getStaffInfo(IData param) throws Exception {
        return StaffInfoQry.getStaffInfo(param);
    }

    /**
     * 根据名称过滤分管客户经理
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset qryFilterManagerIdJobType(IData param) throws Exception {
        return StaffInfoQry.qryFilterManagerIdJobType(param);
    }

    /**
     * 4.1.1 工号校验接口
     * 
     * @param param
     * @return
     * @author wukw3 add 20170614
     * @throws Exception
     */
    public static IData checkStaffInfo(IData param) throws Exception {
        IData returnData = new DataMap();

        IDataUtil.chkParam(param, "STAFF_ID");
        IDataUtil.chkParam(param, "STAFF_NAME");
        IDataUtil.chkParam(param, "SERIAL_NUMBER");
        IDataUtil.chkParam(param, "DEPART_CODE");

        String strStaffId = param.getString("STAFF_ID");
        String strStaffName = param.getString("STAFF_NAME", "");
        String strDepartCode = param.getString("DEPART_CODE");
        String strSerialNumber = param.getString("SERIAL_NUMBER", "");

        String strQUERY_RESULT_CODE = "-1";
        String strQUERY_RESULT_INFO = "工号不在！";

        IData staffInfo = StaffInfoQry.qryStaffInfoByPK(strStaffId);

        if ("0".equals(staffInfo.getString("DIMISSION_TAG", ""))) {
            strQUERY_RESULT_CODE = "0";
            strQUERY_RESULT_INFO = "正常";
            if (!strSerialNumber.equals(staffInfo.getString("SERIAL_NUMBER", ""))) {
                strQUERY_RESULT_CODE = "3";
                strQUERY_RESULT_INFO = "联系方式不对";
            }
            ;
            if (!strStaffName.equals(staffInfo.getString("STAFF_NAME", ""))) {
                strQUERY_RESULT_CODE = "4";
                strQUERY_RESULT_INFO = "姓名不对";
            }
            ;
            String departCode = StaticUtil.getStaticValue(getVisit(), "TD_M_DEPART", "DEPART_ID", "DEPART_CODE", staffInfo.getString("DEPART_ID", ""));
            if (!strDepartCode.equals(departCode)) {
                strQUERY_RESULT_CODE = "5";
                strQUERY_RESULT_INFO = "部门不对";
            }
        }
        ;
        if ("1".equals(staffInfo.getString("DIMISSION_TAG", ""))) {
            strQUERY_RESULT_CODE = "1";
            strQUERY_RESULT_INFO = "已经离职";
        }
        ;
        if ("2".equals(staffInfo.getString("DIMISSION_TAG", ""))) {
            strQUERY_RESULT_CODE = "2";
            strQUERY_RESULT_INFO = "冻结";
        }
        ;

        returnData.put("RESULT_CODE", strQUERY_RESULT_CODE);
        returnData.put("RESULT_INFO", strQUERY_RESULT_INFO);

        return returnData;

    }

    public static IDataset queryCustManagerStaffById(IData param) throws Exception {
        String custManagerId = param.getString("CUST_MANAGER_ID");
        return StaffInfoQry.qryCustManagerStaffById(custManagerId);
    }

    /**
     * 根据名称过滤代理商
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset qryFilterAgentMgrDepartId(IData param) throws Exception
    {
        return StaffInfoQry.qryFilterAgentMgrDepartId(param);
    }
    
}
