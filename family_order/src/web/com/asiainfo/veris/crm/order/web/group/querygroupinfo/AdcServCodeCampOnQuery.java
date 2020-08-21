
package com.asiainfo.veris.crm.order.web.group.querygroupinfo;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public abstract class AdcServCodeCampOnQuery extends CSBasePage
{

    /**
     * @Description: 查询预占的服务号码
     * @author
     * @date
     * @param cycle
     * @throws Exception
     */
    public void queryServCodeInfo(IRequestCycle cycle) throws Exception
    {
        IData condData = getData();
        IData ctrlInfo = new DataMap();

        IDataOutput dop = CSViewCall.callPage(this, "SS.AdcCamponSvrCodeSvc.getAdcCamponSvrcodeInfoForSearch", condData, getPagination("infonav"));
        IDataset servCodeInfo = dop.getData();

        if (IDataUtil.isEmpty(servCodeInfo))
        {
            ctrlInfo.put("strHint", "没有符合条件的查询结果！");
        } else {
            ctrlInfo.put("strHint", "查询成功！");
        }

        setCtrlInfo(ctrlInfo);
        setCondition(condData);
        setInfos(servCodeInfo);
        setInfoCount(dop.getDataCount());
    }

    /**
     * 删除预占的服务号码
     *
     * @param cycle
     * @throws Exception
     */
    public void deteleServCode(IRequestCycle cycle) throws Exception {
        IData data = getData();
        String flag = "true";

        String svrCode = data.getString("svrCode", "");
        String custManageId = data.getString("custManageId", "");

        IData param = new DataMap();
        param.put("SVR_CODE", svrCode);
        param.put("CUST_MANAGER_ID", custManageId);

        IDataset output = CSViewCall.call(this, "SS.AdcCamponSvrCodeSvc.cancelCamponServcode", param);

        if (!IDataUtil.isEmpty(output)) {
            IData resultMap = output.getData(0);
            int updCnt = Integer.parseInt(resultMap.getString("UPDCNT"));

            if (updCnt > 0) {
                flag = "true";
            } else {
                flag = "false";
            }
        } else {
            flag = "false";
        }

        this.queryServCodeInfo(cycle);

        IData result = new DataMap();
        result.put("result", flag);
        setAjax(result);
    }

    public abstract void setCondition(IData condition);
    public abstract void setCtrlInfo(IData ctrlInfo);
    public abstract void setInfoCount(long infoCount);
    public abstract void setInfos(IDataset infos);

}
