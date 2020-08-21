
package com.asiainfo.veris.crm.order.soa.person.busi.plat.officedata;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.exception.PlatException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.plat.PlatOfficeDataQry;

public class PlatOfficeDataBean extends CSBizBean
{

    /**
     * 删除Sp业务信息
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public static int[] delSPBiz(IDataset param) throws Exception
    {
        StringBuilder sql = new StringBuilder("UPDATE TD_M_SP_BIZ SET BIZ_STATUS = 'N',BIZ_STATE_CODE = 'N',END_DATE = TO_DATE(:END_DATE,'YYYY-MM-DD'),");
        sql.append("UPDATE_TIME = TO_DATE(:UPDATE_TIME,'YYYY-MM-DD HH24:MI:SS'),UPDATE_STAFF_ID = :UPDATE_STAFF_ID,UPDATE_DEPART_ID = :UPDATE_DEPART_ID");
        sql.append(" WHERE SP_CODE = :SP_CODE AND BIZ_CODE = :BIZ_CODE");
        int[] a = new int[param.size()];
        for (int i = 0; i < param.size(); i++)
        {
            if ("N".equals(((IData) param.get(i)).getString("BIZ_STATUS")))
            {
                Dao.delete("TD_M_SP_BIZ", param.getData(i));
            }
            else
            {
                a[i] = Dao.executeUpdate(sql, param.getData(i));
            }
        }
        return a;
    }

    /**
     * 删除SP信息
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public static int[] delSPInfo(IDataset param) throws Exception
    {
        StringBuilder sql = new StringBuilder("UPDATE TD_M_SP_INFO SET SP_STATUS = 'N',END_DATE = TO_DATE(:END_DATE,'YYYY-MM-DD'),");
        sql.append("UPDATE_TIME = TO_DATE(:UPDATE_TIME,'YYYY-MM-DD HH24:MI:SS'),UPDATE_STAFF_ID = :UPDATE_STAFF_ID,UPDATE_DEPART_ID = :UPDATE_DEPART_ID");
        sql.append(" WHERE SP_ID = :SP_ID AND SP_STATUS = :SP_STATUS AND TO_CHAR(START_DATE,'YYYY-MM-DD') = :START_DATE");
        int[] a = new int[param.size()];
        for (int i = 0; i < param.size(); i++)
        {
            if ("N".equals(((IData) param.get(i)).getString("SP_STATUS")))
            {
                Dao.delete("TD_M_SP_INFO", param.getData(i));
            }
            else
            {
                a[i] = Dao.executeUpdate(sql, param.getData(i));
            }
        }
        return a;
    }

    /**
     * 局数据企业信息导入
     * 
     * @param fileName
     * @param dataset
     * @param operSource
     * @throws Exception
     */
    public static void importSpBiz(String fileName, IDataset dataset, String operSource) throws Exception
    {
        String[] inParam =
        { "v_resultcode", "v_resultinfo" };
        String importId = SeqMgr.getSpbureImportId();
        IData data = new DataMap();

        data.put("IMPORT_ID", importId);
        data.put("DATA_TYPE", operSource.indexOf("DSMP") > -1 ? "DSMP_BIZ" : "BIZ_INFO");
        data.put("IMPORT_TIME", SysDateMgr.getSysTime());
        data.put("IMPORT_STAFF_ID", getVisit().getStaffId());
        data.put("IMPORT_DEPART_ID", getVisit().getDepartId());
        data.put("DEAL_FLAG", "0");
        data.put("FILE_NAME", fileName);
        data.put("OPER_SOURCE", operSource);

        Dao.insert("TD_B_BUREDATA_BATIMPORT", data, Route.CONN_CRM_CEN);
        IDataset bizdtls = new DatasetList();
        for (int i = 0; i < dataset.size(); i++)
        {
            IData element = (IData) dataset.get(i);
            IData bizdtl = new DataMap();
            bizdtl.put("IMPORT_ID", importId);

            String expireDate = element.getString("EXPIRE_DATE");
            String importDate = element.getString("IMPORT_DATE");
            String validDate = element.getString("VALID_DATE");

            if (expireDate == null || expireDate.length() == 0)
            {
                element.put("EXPIRE_DATE", SysDateMgr.getTheLastTime());
            }
            if (importDate == null || importDate.length() == 0)
            {
                element.put("IMPORT_DATE", SysDateMgr.getSysTime());
            }
            if (validDate == null || validDate.length() == 0)
            {
                element.put("VALID_DATE", SysDateMgr.getSysTime());
            }

            bizdtl.put("KIND_ID", element.getString("KIND_ID"));
            bizdtl.put("SERV_TYPE", element.getString("SERV_TYPE"));
            bizdtl.put("SP_CODE", element.getString("SP_CODE"));
            bizdtl.put("BIZ_CODE", element.getString("BIZ_CODE"));
            bizdtl.put("BIZ_NAME", element.getString("BIZ_NAME"));
            bizdtl.put("BIZ_DESC", element.getString("BIZ_DESC"));
            bizdtl.put("BIZ_TYPE_CODE", element.getString("BIZ_TYPE_CODE"));
            bizdtl.put("ACCESS_MODE", element.getString("ACCESS_MODE"));
            bizdtl.put("OTHER_BAL_OBJ1", element.getString("OTHER_BAL_OBJ1"));
            bizdtl.put("OTHER_BAL_OBJ2", element.getString("OTHER_BAL_OBJ2"));
            bizdtl.put("BILL_FLAG", element.getString("BILL_FLAG"));
            bizdtl.put("FEE", element.getString("FEE"));
            bizdtl.put("VALID_DATE", element.getString("VALID_DATE"));
            bizdtl.put("EXPIRE_DATE", element.getString("EXPIRE_DATE"));
            bizdtl.put("BAL_PROP", element.getString("BAL_PROP"));
            bizdtl.put("COUNT", element.getString("COUNT"));
            bizdtl.put("SERV_ATTR", element.getString("SERV_ATTR"));
            bizdtl.put("IS_THIRD_VALIDATE", element.getString("IS_THIRD_VALIDATE"));
            bizdtl.put("RE_CONFIRM", element.getString("RE_CONFIRM"));
            bizdtl.put("SERV_PROPERTY", element.getString("SERV_PROPERTY"));
            bizdtl.put("BIZ_STATUS", element.getString("BIZ_STATUS"));
            bizdtl.put("PROV_ADDR", element.getString("PROV_ADDR"));
            bizdtl.put("PROV_PORT", element.getString("PROV_PORT"));
            bizdtl.put("USAGE_DESC", element.getString("USAGE_DESC"));
            bizdtl.put("INTRO_URL", element.getString("INTRO_URL"));
            bizdtl.put("INFO_CODE", element.getString("INFO_CODE"));
            bizdtl.put("INFO_VALUE", element.getString("INFO_VALUE"));
            bizdtl.put("OPER_TYPE", element.getString("OPER_TYPE"));
            bizdtl.put("IMPORT_DATE", element.getString("IMPORT_DATE"));
            bizdtl.put("OPR_SOURCE", data.getString("OPER_SOURCE"));
            bizdtl.put("DEAL_RESULT", element.getString("DEAL_RESULT"));
            bizdtl.put("REMARK", element.getString("REMARK"));
            bizdtl.put("FILE_NAME", data.getString("FILE_NAME"));
            bizdtl.put("RSRT_STR1", element.getString("RSRT_STR1"));
            bizdtl.put("RSRT_STR2", element.getString("RSRT_STR2"));
            bizdtl.put("RSRT_STR3", element.getString("RSRT_STR3"));
            bizdtl.put("RSRT_STR4", element.getString("RSRT_STR4"));
            bizdtl.put("RSRT_STR5", element.getString("RSRT_STR5"));

            bizdtl.put("SECCONFIRM_TAG", element.getString("DCONFIRM_FLAG"));// 二次确认标识
            bizdtl.put("FEEWARN_TAG", element.getString("DEDUCT_CLUE"));// 扣费提醒标识
            bizdtl.put("CANCEL_TAG", element.getString("QUERY_TD"));// 统一查询退定标识
            bizdtl.put("CONTENT_CODE", element.getString("CONTENT_CODE"));// 音乐内容编码
            bizdtl.put("MEMBER_TYPE", element.getString("MEMBER_TYPE"));// 会员属性

            bizdtl.put("DEDUCT_CLUE", element.getString("DEDUCT_CLUE"));// 扣费提醒标识
            bizdtl.put("QUERY_TD", element.getString("QUERY_TD"));// 统一查询退定标识
            bizdtl.put("DEDUCT_MODE", element.getString("DEDUCT_MODE"));// 包月计费模式
            // 1）计费类型取值：3（包月计费），9（按栏目包月计费）时，该字段填写有效值为：0：立即计费1：72小时后计费;2）计费类型取值：其它，该字段填空
            bizdtl.put("OPEN_CMD", element.getString("OPEN_CMD"));// 开通状态 0：不开通1：开通
            bizdtl.put("DAY_CONVERT", element.getString("DAY_CONVERT"));// 是否按天折算标识
            // 计费类型取值：3（包月计费），9（按栏目包月计费）时，该字段填写有效值为：0：不按天折算；1：按天折算;2）计费类型取值：其它，该字段填空'

            bizdtls.add(bizdtl);
        }

        Dao.insert("TD_B_BUREDATA_IMPORT_BIZDTL", bizdtls, Route.CONN_CRM_CEN);
        // 异步处理
        Dao.callProc("PKG_CMS_BUREDATA.p_bat_buredata_deal", inParam, data, Route.CONN_CRM_CEN);

    }

    /**
     * 局数据服务信息导入
     * 
     * @param fileName
     * @param dataset
     * @param operSource
     * @throws Exception
     */
    public static void importSpInfo(String fileName, IDataset dataset, String operSource) throws Exception
    {
        String[] inParam =
        { "v_resultcode", "v_resultinfo" };
        String importId = SeqMgr.getSpbureImportId();
        IData data = new DataMap();
        data.put("IMPORT_ID", importId);
        data.put("DATA_TYPE", operSource.indexOf("DSMP") > -1 ? "DSMP_SP" : "SP_INFO");
        data.put("IMPORT_TIME", SysDateMgr.getSysTime());
        data.put("IMPORT_STAFF_ID", getVisit().getStaffId());
        data.put("IMPORT_DEPART_ID", getVisit().getDepartId());
        data.put("FILE_NAME", fileName);
        data.put("DEAL_FLAG", "0");
        data.put("OPER_SOURCE", operSource);
        data.put("REMARK", "批量导入SP业务信息！");

        boolean flag = Dao.insert("TD_B_BUREDATA_BATIMPORT", data, Route.CONN_CRM_CEN);

        IDataset spdtls = new DatasetList();
        for (Object obj : dataset)
        {
            IData element = (IData) obj;
            IData spdtl = new DataMap();
            spdtl.put("IMPORT_ID", importId);

            String expireDate = element.getString("EXPIRE_DATE");
            String importDate = element.getString("IMPORT_DATE");
            String validDate = element.getString("VALID_DATE");

            if (expireDate == null || expireDate.length() == 0)
            {
                element.put("EXPIRE_DATE", SysDateMgr.getTheLastTime());
            }
            if (importDate == null || importDate.length() == 0)
            {
                element.put("IMPORT_DATE", SysDateMgr.getSysTime());
            }
            if (validDate == null || validDate.length() == 0)
            {
                element.put("VALID_DATE", SysDateMgr.getSysTime());
            }

            spdtl.put("KIND_ID", element.getString("KIND_ID"));
            spdtl.put("SERV_TYPE", element.getString("SERV_TYPE"));
            spdtl.put("SP_CODE", element.getString("SP_CODE"));
            spdtl.put("SP_NAME", element.getString("SP_NAME"));
            spdtl.put("SP_NAME_EN", element.getString("SP_NAME_EN"));
            spdtl.put("SP_SHORT_NAME", element.getString("SP_SHORT_NAME"));
            spdtl.put("SP_TYPE", element.getString("SP_TYPE"));
            spdtl.put("SP_STATUS", element.getString("SP_STATUS"));
            spdtl.put("SERV_CODE", element.getString("SERV_CODE"));
            spdtl.put("PROV_CODE", element.getString("PROV_CODE"));
            spdtl.put("BAL_PROV", element.getString("BAL_PROV"));
            spdtl.put("DEV_CODE", element.getString("DEV_CODE"));
            spdtl.put("VALID_DATE", element.getString("VALID_DATE"));
            spdtl.put("EXPIRE_DATE", element.getString("EXPIRE_DATE"));
            spdtl.put("SP_ATTR", element.getString("SP_ATTR"));
            spdtl.put("SP_DESC", element.getString("SP_DESC"));
            spdtl.put("CS_TEL", element.getString("CS_TEL"));
            spdtl.put("CS_URL", element.getString("CS_URL"));
            spdtl.put("SETTLE_MODEL", element.getString("SETTLE_MODEL"));
            spdtl.put("OPER_TYPE", element.getString("OPER_TYPE"));
            spdtl.put("IMPORT_DATE", element.getString("IMPORT_DATE"));
            spdtl.put("OPR_SOURCE", data.getString("OPER_SOURCE"));
            spdtl.put("DEAL_RESULT", element.getString("DEAL_RESULT"));
            spdtl.put("REMARK", element.getString("REMARK"));
            spdtl.put("FILE_NAME", data.getString("FILE_NAME"));
            spdtl.put("RSRT_STR1", element.getString("RSRT_STR1"));
            spdtl.put("RSRT_STR2", element.getString("RSRT_STR2"));
            spdtl.put("RSRT_STR3", element.getString("RSRT_STR3"));
            spdtl.put("RSRT_STR4", element.getString("RSRT_STR4"));
            spdtl.put("RSRT_STR5", element.getString("RSRT_STR5"));
            spdtl.put("OPER_MODE", element.getString("OPER_MODE"));
            spdtl.put("OPER_MODE", element.getString("OPER_MODE"));
            spdtl.put("OPER_MOD", element.getString("OPER_MOD"));// 运营模式 0：非中国移动运营1：中国移动运营
            spdtls.add(spdtl);
        }
        Dao.insert("TD_B_BUREDATA_IMPORT_SPDTL", spdtls, Route.CONN_CRM_CEN);
        // 异步处理
        Dao.callProc("PKG_CMS_BUREDATA.p_bat_buredata_deal", inParam, data, Route.CONN_CRM_CEN);

    }

    /**
     * 查询SP批量信息
     * 
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset queryBizBat(IData param, Pagination pagination) throws Exception
    {
        return PlatOfficeDataQry.queryBizBat(param, pagination);
    }

    /**
     * 查询SPBiz导入详情
     * 
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset queryBizBatDtl(IData param, Pagination pagination) throws Exception
    {
        return PlatOfficeDataQry.queryBizBatDtl(param, pagination);
    }

    /**
     * 查询SP业务信息
     * 
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset querySPBiz(IData param, Pagination pagination) throws Exception
    {
        return PlatOfficeDataQry.querySPBiz(param, pagination);
    }

    /**
     * 查询SP信息
     * 
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset querySPInfo(IData param, Pagination pagination) throws Exception
    {
        return PlatOfficeDataQry.querySPInfo(param, pagination);
    }

    /**
     * 查询SP批量信息
     * 
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset querySPInfoBat(IData param, Pagination pagination) throws Exception
    {
        return PlatOfficeDataQry.querySPInfoBat(param, pagination);
    }

    /**
     * 查询SP导入详情
     * 
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset querySPInfoBatDtl(IData param, Pagination pagination) throws Exception
    {
        return PlatOfficeDataQry.querySPInfoBatDtl(param, pagination);
    }

    /**
     * 保存SP业务信息
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public static boolean saveSPBiz(IData param) throws Exception
    {
        param.put("OPER_TYPE", "ADD");
        param.put("BIZ_TYPE", param.getString("BIZ_TYPE_CODE"));
        param.put("RSRV_STR2", param.getString("RE_CONFIRM"));
        param.put("RSRV_STR3", param.getString("IS_THIRD_VALIDATE"));
        param.put("BIZ_DESC", param.getString("BIZ_DESC"));
        param.put("BIZ_STATUS", "A");

        String count = param.getString("BILL_TYPE", "");
        if (count.equals("5"))
        {
            param.put("COUNT", param.getString("NUM_TIME", "0"));
        }
        else if (count.equals("7"))
        {
            param.put("COUNT", param.getString("NUM_DAY", "0"));
        }
        else
        {
            param.put("COUNT", "0");
        }

        IData bizCheckParam = new DataMap();
        bizCheckParam.putAll(param);
        bizCheckParam.put("SERV_TYPE", param.getString("SERV_TYPE"));
        bizCheckParam.put("SP_CODE", param.getString("SP_CODE"));
        bizCheckParam.put("OPERATOR_CODE", param.getString("BIZ_CODE"));
        bizCheckParam.put("OPERATOR_NAME", param.getString("BIZ_NAME"));
        bizCheckParam.put("OTHER_BAL_OBJ1", param.getString("COUNT_SIDE1"));
        bizCheckParam.put("OTHER_BAL_OBJ2", param.getString("COUNT_SIDE2"));
        bizCheckParam.put("BILL_FLAG", param.getString("BILL_TYPE"));
        bizCheckParam.put("FEE", param.getString("PRICE"));
        bizCheckParam.put("VALID_DATE", param.getString("START_DATE", "").replaceAll("-", ""));
        bizCheckParam.put("EXPIRE_DATE", param.getString("END_DATE", "").replaceAll("-", ""));
        bizCheckParam.put("BAL_PROP", param.getString("COUNT_PCT"));
        bizCheckParam.put("COUNT", param.getString("COUNT"));
        bizCheckParam.put("SERV_ATTR", param.getString("BIZ_ATTR"));
        bizCheckParam.put("IS_THIRD_VALIDATE", param.getString("IS_THIRD_VALIDATE"));
        bizCheckParam.put("RE_CONFIRM", param.getString("RE_CONFIRM"));
        bizCheckParam.put("SERV_PROPERTY", param.getString("SERV_PROPERTY"));
        bizCheckParam.put("BIZ_STATUS", param.getString("BIZ_STATE_CODE"));
        bizCheckParam.put("OPER_TYPE", param.getString("OPER_TYPE", ""));
        IData result = spBizCheck(bizCheckParam);
        if (!"0".equals(result.getString("X_RESULTCODE")))
        {
            CSAppException.apperr(PlatException.CRM_PLAT_74, "数据校验失败!<br>" + "业务代码：" + result.getString("BIZ_CODE") + "<br>" + result.getString("X_RESULTCODE") + ":" + result.getString("v_resultinfo"));
        }
        return Dao.insert("TD_M_SP_BIZ", param);
    }

    /**
     * 保存SP信息
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public static boolean saveSPInfo(IData param) throws Exception
    {
        param.put("OPER_TYPE", "ADD");
        param.put("SP_ID", param.getString("SP_CODE"));
        IData checkParam = new DataMap();
        checkParam.putAll(param);
        checkParam.put("PROV_CODE", checkParam.getString("IN_PROVINCE"));
        checkParam.put("BAL_PROV", checkParam.getString("CON_PROVINCE"));
        checkParam.put("VALID_DATE", checkParam.getString("START_DATE", "").replaceAll("-", ""));
        checkParam.put("EXPIRE_DATE", checkParam.getString("END_DATE", "").replaceAll("-", ""));
        checkParam.put("DESCRIPTION", checkParam.getString("SP_DESC"));
        checkParam.put("DEV_CODE", checkParam.getString("PLAT_CODE"));

        IData result = spInfoCheck(checkParam);
        if (!"0".equals(result.getString("X_RESULTCODE")))
        {
            CSAppException.apperr(PlatException.CRM_PLAT_74, "数据校验失败!<br>" + "企业代码：" + result.getString("SP_CODE") + "<br>" + result.getString("X_RESULTCODE") + ":" + result.getString("v_resultinfo"));

        }
        return Dao.insert("TD_M_SP_INFO", param);
    }

    /**
     * 检查SP业务信息，接口使用。
     * 
     * @param pd
     * @param spInfos
     * @throws Exception
     */
    public static IData spBizCheck(IData bizInfo) throws Exception
    {
        if (bizInfo == null || bizInfo.size() == 0)
        {
            CSAppException.apperr(PlatException.CRM_PLAT_74, "没有需要处理的业务！");
        }

        String oper_type = bizInfo.getString("OPER_TYPE", "");

        if (!"ADD".equals(oper_type) && !"MOD".equals(oper_type) && !"DEL".equals(oper_type))
        {
            CSAppException.apperr(PlatException.CRM_PLAT_74, "操作类型编码不符合规范!");
        }

        IData ret = PlatOfficeDataQry.bizCheck(bizInfo);

        String resultInfo = ret.getString("X_RESULTINFO", "");
        String xResultCheck = "FFFF";
        if (resultInfo.indexOf(":") > -1)
        {
            xResultCheck = resultInfo.substring(0, resultInfo.indexOf(":"));
        }
        ret.put("X_RESULTCHECK", xResultCheck);
        return ret;
    }

    /**
     * 检查SP企业信息，接口使用。
     * 
     * @param pd
     * @param spInfos
     * @throws Exception
     */
    public static IData spInfoCheck(IData spInfo) throws Exception
    {
        if (spInfo == null || spInfo.size() == 0)
        {
            CSAppException.apperr(PlatException.CRM_PLAT_74, "没有需要处理的业务！");
        }

        String oper_type = spInfo.getString("OPER_TYPE", "");

        if (!"ADD".equals(oper_type) && !"MOD".equals(oper_type) && !"DEL".equals(oper_type))
        {
            CSAppException.apperr(PlatException.CRM_PLAT_74, "操作类型编码不符合规范!");
        }
        IData ret = PlatOfficeDataQry.spInfoCheck(spInfo);

        String resultInfo = ret.getString("X_RESULTCODE");
        String xResultCheck = "FFFF";
        if (resultInfo.indexOf(":") > -1)
        {
            xResultCheck = resultInfo.substring(0, resultInfo.indexOf(":"));
        }
        ret.put("X_RESULTCHECK", xResultCheck);
        return ret;
    }
}
