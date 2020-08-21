
package com.asiainfo.veris.crm.order.soa.person.busi.badness.sundryquery;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.consts.PersonConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSExportTaskExecutor;

public class ExportBadnessManage extends CSExportTaskExecutor
{

    public void chkDataByStaticValue(IData data, String colName, String typeId) throws Exception
    {
        String value = data.getString(colName);
        if (StringUtils.isBlank(value))
        {
            return;
        }

        String dataName = StaticUtil.getStaticValue(typeId, value);
        data.put(colName, dataName);
    }

    public IDataset executeExport(IData data, Pagination page) throws Exception
    {
        IData param = data.subData("cond", true);
        data.putAll(param);
        IDataset result = null;
        String tag = data.getString("TAG_PARAM");
        getVisit().setStaffId(data.getString("STAFF_ID", "SUPERUSR"));// 设置登陆员工,因为导出需要校验权限
        if ("1".equals(tag))
        {// 不良信息举报处理
            data.put("STATE", PersonConst.STATE_NORMAL);
            data.put("REPORT_CUST_PROVINCE", PersonConst.PROVINCE_CODE);
            data.put("BADNESS_INFO_PROVINCE", PersonConst.PROVINCE_CODE);
            getVisit().setLoginEparchyCode(CSBizBean.getTradeEparchyCode());
            result = CSAppCall.call("SS.BadnessQuerySVC.queryBadnessInfoImpeach", data);
        }
        else if ("2".equals(tag))
        {// 不良信息举报回复
            data.put("STATE", PersonConst.STATE_HEAD);
            result = CSAppCall.call("SS.BadnessQuerySVC.queryBaseBadnessInfo", data);

        }
        else if ("3".equals(tag))
        {// 不良信息举报归档
            data.put("STATE", PersonConst.STATE_FEEDBACK);
            data.put("REPORT_CUST_PROVINCE", PersonConst.PROVINCE_CODE);
            data.put("BADNESS_INFO_PROVINCE", PersonConst.PROVINCE_CODE);
            result = CSAppCall.call("SS.BadnessQuerySVC.queryBadHastenInfo", data);

        }
        else if ("4".equals(tag))
        {// 不良信息举报退回
            data.put("STATE", PersonConst.STATE_HEAD);
            result = CSAppCall.call("SS.BadnessQuerySVC.queryBaseBadnessInfo", data);

        }
        else if ("5".equals(tag))
        {// 不良信息举报催办
            data.put("STATE", PersonConst.STATE_NORMAL);
            data.put("REPORT_CUST_PROVINCE", PersonConst.PROVINCE_CODE);
            data.put("BADNESS_INFO_PROVINCE", PersonConst.PROVINCE_CODE);
            result = CSAppCall.call("SS.BadnessQuerySVC.queryBadHastenInfo", data);
        }
        else if ("6".equals(tag))
        {// 不良信息举报查询
            result = CSAppCall.call("SS.BadnessQuerySVC.queryOtherProvBadnessInfo", data);
        }
        else if ("7".equals(tag))
        {// 信息查询
            result = CSAppCall.call("SS.BadnessInfoSVC.queryBadnessInfos", data);
        }

        if (IDataUtil.isNotEmpty(result))
        {
            for (int i = 0, size = result.size(); i < size; i++)
            {
                IData temp = result.getData(i);
                chkDataByStaticValue(temp, "DEAL_RAMARK", "BAD_INFO_DEAL_RESULT");
                chkDataByStaticValue(temp, "REPORT_CUST_LEVEL", "IBOSS_LEVEL");
                chkDataByStaticValue(temp, "IMPORTANT_LEVEL", "IMPORTANT_LEVEL");
                chkDataByStaticValue(temp, "REPORT_CUST_PROVINCE", "BAD_PROVINCE_CODE");
                chkDataByStaticValue(temp, "RECV_PROVINCE", "BAD_PROVINCE_CODE");

                chkDataByStaticValue(temp, "TARGET_PROVINCE", "BAD_PROVINCE_CODE");
                chkDataByStaticValue(temp, "SERV_REQUEST_TYPE", "BAD_INFO_SERV_REQUEST_TYPE");
                chkDataByStaticValue(temp, "REPORT_TYPE_CODE", "REPORT_TYPE_CODE");
                chkDataByStaticValue(temp, "BADNESS_INFO_PROVINCE", "BAD_PROVINCE_CODE");
                chkDataByStaticValue(temp, "HASTEN_STATE", "BAD_INFO_STATE");

                chkDataByStaticValue(temp, "RECV_IN_TYPE", "RECV_IN_TYPE");
                chkDataByStaticValue(temp, "ADD_RESULT", "HLR_BLACK_ADD_RESULT");
                chkDataByStaticValue(temp, "SOURCE_DATA", "HLR_BLACK_SOURCE");
                chkDataByStaticValue(temp, "HANDLING_STATE", "HLR_RELEASE_HANDING");
            }
        }
        return result;
    }
}
