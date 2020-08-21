
package com.asiainfo.veris.crm.order.soa.person.busi.plat;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;

public class platBureDataIntfImport
{

    /**
     * 导入SP业务信息。CRM前台导入使用，接口同步使用。
     * 
     * @param pd
     * @param data
     * @param dataset
     * @param operSource
     *            操作来源CRM前台操作传入CRM，接口同步传入IBOSS
     * @throws Exception
     */
    public static void importSPBiz(String fileName, IDataset dataset, String operSource) throws Exception
    {
        String import_id = SeqMgr.getSpbureImportId();
        IData data = new DataMap();

        data.put("IMPORT_ID", import_id);
        data.put("DATA_TYPE", operSource.indexOf("DSMP") > -1 ? "DSMP_BIZ" : "BIZ_INFO");
        data.put("IMPORT_TIME", SysDateMgr.getSysTime());
        data.put("IMPORT_STAFF_ID", CSBizBean.getVisit().getStaffId());
        data.put("IMPORT_DEPART_ID", CSBizBean.getVisit().getDepartId());
        data.put("DEAL_FLAG", "0");
        data.put("FILE_NAME", fileName);
        data.put("OPER_SOURCE", operSource);

        Dao.insert("TD_B_BUREDATA_BATIMPORT", data);
        IDataset bizdtls = new DatasetList();
        for (int i = 0; i < dataset.size(); i++)
        {
            IData element = (IData) dataset.get(i);
            IData bizdtl = new DataMap();
            bizdtl.put("IMPORT_ID", import_id);

            String expireDate = element.getString("EXPIRE_DATE");
            String importDate = element.getString("IMPORT_DATE");
            String validDate = element.getString("VALID_DATE");

            if (StringUtils.isBlank(expireDate))
            {
                element.put("EXPIRE_DATE", SysDateMgr.END_DATE_FOREVER);
            }
            if (StringUtils.isBlank(importDate))
            {
                element.put("IMPORT_DATE", SysDateMgr.getSysTime());
            }
            if (StringUtils.isBlank(validDate))
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
        Dao.insert("TD_B_BUREDATA_IMPORT_BIZDTL", bizdtls);
        // 异步处理
        // DaoHelper.callProc(pd.getDBConn(BaseFactory.CENTER_CONNECTION_NAME),"PKG_CMS_BUREDATA.p_bat_buredata_deal",
        // inParam, data);

    }

    /**
     * 导入SP企业信息。CRM前台导入使用，接口同步使用。
     * 
     * @param pd
     * @param data
     * @param dataset
     * @param operSource
     *            操作来源CRM前台操作传入CRM，接口同步传入IBOSS
     * @throws Exception
     */
    public static void importSPInfo(String fileName, IDataset dataset, String operSource) throws Exception
    {
        String import_id = SeqMgr.getSpbureImportId();
        IData data = new DataMap();
        data.put("IMPORT_ID", import_id);
        data.put("DATA_TYPE", operSource.indexOf("DSMP") > -1 ? "DSMP_SP" : "SP_INFO");
        data.put("IMPORT_TIME", SysDateMgr.getSysTime());
        data.put("IMPORT_STAFF_ID", CSBizBean.getVisit().getStaffId());
        data.put("IMPORT_DEPART_ID", CSBizBean.getVisit().getDepartId());
        data.put("FILE_NAME", fileName);
        data.put("DEAL_FLAG", "0");
        data.put("OPER_SOURCE", operSource);
        data.put("REMARK", "批量导入SP业务信息！");

        Dao.insert("TD_B_BUREDATA_BATIMPORT", data);

        IDataset spdtls = new DatasetList();
        for (Object obj : dataset)
        {
            IData element = (IData) obj;
            IData spdtl = new DataMap();
            spdtl.put("IMPORT_ID", import_id);

            String expireDate = element.getString("EXPIRE_DATE");
            String importDate = element.getString("IMPORT_DATE");
            String validDate = element.getString("VALID_DATE");

            if (StringUtils.isBlank(expireDate))
            {
                element.put("EXPIRE_DATE", SysDateMgr.END_DATE_FOREVER);
            }
            if (StringUtils.isBlank(importDate))
            {
                element.put("IMPORT_DATE", SysDateMgr.getSysTime());
            }
            if (StringUtils.isBlank(validDate))
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
        Dao.insert("TD_B_BUREDATA_IMPORT_SPDTL", spdtls);
        // 异步处理
        // DaoHelper.callProc(pd.getDBConn(BaseFactory.CENTER_CONNECTION_NAME),"PKG_CMS_BUREDATA.p_bat_buredata_deal",
        // inParam, data);

    }

}
