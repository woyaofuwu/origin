
package com.asiainfo.veris.crm.order.web.group.param.adc.newxxt;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.dynamichtml.IProductParamDynamic;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcintf.datainfo.userinfo.relationxxtinfo.RelationXXTInfoIntf;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.relationxxtinfo.RelationXXTInfoIntfViewUtil;

public class CreateMemParamInfo extends IProductParamDynamic
{

    public IData initCrtMb(IBizCommon bp, IData data) throws Throwable
    {
        IData result = super.initCrtMb(bp, data);
        IData parainfo = result.getData("PARAM_INFO");
        IDataset snList = new DatasetList();
        IData snData = new DataMap();

        IDataset operTypes = new DatasetList();
        IData opertype1 = new DataMap();
        opertype1.put("DATA_ID", "0");
        opertype1.put("DATA_NAME", "新增");
        operTypes.add(opertype1);

        snData.put("OPER_TYPES", operTypes);
        snList.add(snData);

        parainfo.put("SNLIST", snList);
        parainfo.put("pam_NOTIN_SERIAL_NUMBER", data.getString("MEB_SERIAL_NUMBER"));
        parainfo.put("pam_GRP_USER_ID", data.getString("GRP_USER_ID"));
        
        parainfo.put("pam_GROUP_ID", data.getString("GROUP_ID"));

        return result;
    }

    public IData initChgMb(IBizCommon bp, IData data) throws Throwable
    {
        IData result = super.initChgMb(bp, data);
        IData parainfo = result.getData("PARAM_INFO");
        IDataset snList = new DatasetList();

        IDataset operTypes1 = new DatasetList();

        IData snData = new DataMap();

        IData opertype1 = new DataMap();
        IData opertype2 = new DataMap();
        IData opertype3 = new DataMap();

        IData temp = null;
        IData map = null;

        IDataset stuParmLis = null;
        String mem_serialNumber = data.getString("MEB_SERIAL_NUMBER");
        String ec_user_id = data.getString("GRP_USER_ID");
        String type = "D";

        opertype1.put("DATA_ID", "0");
        opertype1.put("DATA_NAME", "新增");
        operTypes1.add(opertype1);
        snData.put("OPER_TYPES", operTypes1);

        // 查询同一集团 同一个付费号下所有代付号码
        IDataset outMemDataset = RelationXXTInfoIntfViewUtil.queryMemInfoBySNandUserIdA(bp, mem_serialNumber, ec_user_id);

        for (int i = 0, outSize = outMemDataset.size(); i < outSize; i++)
        {
            IDataset resultParmLis = new DatasetList();
            IDataset operTypes2 = new DatasetList();
            IData snParam = new DataMap();

            temp = outMemDataset.getData(i);
            String outSN = temp.getString("SERIAL_NUMBER_B");
            if (StringUtils.isNotBlank(outSN))
            {
                // 用代付号码设置操作类型
                opertype2.put("DATA_ID", "2");
                opertype2.put("DATA_NAME", "变更");

                opertype3.put("DATA_ID", "1");
                opertype3.put("DATA_NAME", "注销");

                operTypes2.add(opertype2);
                operTypes2.add(opertype3);
                // 分别查询校讯通每个代付号下的学生参数信息
                stuParmLis = RelationXXTInfoIntfViewUtil.qryStuDisParamInfoBySnAUserIdASnBDsiType(bp, mem_serialNumber, ec_user_id, outSN, type);
            }
            if (IDataUtil.isNotEmpty(stuParmLis))
            {
                for (int j = 0, jSize = stuParmLis.size(); j < jSize; j++)
                {// 转换为动态表格识别的数据
                    // {"STUD_VALUE":"ll","STUD_KEY":"stu_name1","ELEMENT_ID":"4320","tag":"0"}]
                    IData sutParam = new DataMap();
                    map = stuParmLis.getData(j);
                    sutParam.put("STUD_NAME", map.getString("NAME", ""));// 学生姓名
                    sutParam.put("STUD_KEY", map.getString("RSRV_STR1", ""));// 学生参数
                    sutParam.put("ELEMENT_ID", map.getString("ELEMENT_ID", ""));// 学生绑定的优惠
                    resultParmLis.add(sutParam);
                }

                snParam.put("pam_NOTIN_STU_PARAM_LIST", resultParmLis);
                snParam.put("pam_NOTIN_OUT_SN", outSN);
                snParam.put("OPER_TYPES", operTypes2);
                snList.add(snParam);
            }
        }
        if (snList.size() < 3) {
            snList.add(snData);
        }
        parainfo.put("SNLIST", snList);
        parainfo.put("pam_NOTIN_SERIAL_NUMBER", data.getString("MEB_SERIAL_NUMBER"));
        parainfo.put("pam_GRP_USER_ID", ec_user_id);
        parainfo.put("pam_GROUP_ID", data.getString("GROUP_ID"));

        return result;
    }

    /**
     * 校验1个计费号码最多绑定3个家长服务号码（短信接收号码）
     */
    public IData checkMaxNotinOutSn(IBizCommon bp, IData data) throws Throwable {
        IData result = new DataMap();
        IData results = new DataMap();
        String notinOutSn = data.getString("NOTIN_OUT_SN", "");

        IDataset resultSet = RelationXXTInfoIntf.qryMemInfoBySNandUserIdA(bp, notinOutSn);

        String flag = "false";
        if (resultSet.size() < 3) {
            flag = "true";
        }

        result.put("IS_FLAG", flag);
        results.put("AJAX_DATA", result);
        return results;
    }

    public IData checkMobileNumber(IBizCommon bp, IData data) throws Throwable{
        IData results = new DataMap();
        IData result = new DataMap();

        String serialnumber = data.getString("SERIAL_NUMBER");

        IDataset resultSet = RelationXXTInfoIntf.qrymsisdn(bp, serialnumber);

        String flag = "false";
        if (resultSet.size() > 0) {
            flag = "true";
        }

        result.put("IS_FLAG", flag);
        results.put("AJAX_DATA", result);

        return results;
    }
}
