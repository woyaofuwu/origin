
package com.asiainfo.veris.crm.order.soa.group.task.exp;

import com.ailk.biz.impexp.ExportTaskExecutor;
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.AsynDealVisitUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;

public class GrpSpecialPayQueryExportTask extends ExportTaskExecutor
{

    @Override
    public IDataset executeExport(IData param, Pagination arg1) throws Exception
    {
        AsynDealVisitUtil.dealVisitInfo(param);

        String serialNum = param.getString("cond_SERIAL_NUMBER");
        String productId = param.getString("cond_PRODUCT_ID");
        String groupId = param.getString("cond_GROUP_ID");
        String state = param.getString("cond_STATE");

        IData inputParam = new DataMap();
        inputParam.put("SERIAL_NUMBER", serialNum);
        inputParam.put("PRODUCT_ID", productId);
        inputParam.put("GROUP_ID", groupId);
        inputParam.put("STATE", state);

        IDataset dataOutput = CSAppCall.call("SS.GroupInfoQuerySVC.qryGrpSpecialPayInfo", inputParam);
        for (int i = 0; i < dataOutput.size(); i++)
        {
            IData data = dataOutput.getData(i);
            String attr[] = data.getNames();
            for (int j = 0; j < attr.length; j++)
            {
                if ("PAY_MODE_CODE".equals(attr[j]))
                {
                    String value = data.getString("PAY_MODE_CODE");
                    String payModeCode = StaticUtil.getStaticValue(getVisit(), "TD_S_PAYMODE", "PAY_MODE_CODE", "PAY_MODE", value);
                    dataOutput.getData(i).put("PAY_MODE_CODE", payModeCode);
                }
                else if ("BANK_CODE".equals(attr[j]))
                {
                    String value = data.getString("BANK_CODE");
                    String bankCode = StaticUtil.getStaticValue(getVisit(), "TD_B_BANK", "BANK_CODE", "BANK", value);
                    dataOutput.getData(i).put("BANK_CODE", bankCode);
                }
                else if ("PAYITEM_CODE".equals(attr[j]))
                {
                    String value = data.getString("PAYITEM_CODE");
                    String payItemCode = StaticUtil.getStaticValue(getVisit(), "TD_B_ITEM", "ITEM_ID", "ITEM_NAME", value);
                    dataOutput.getData(i).put("PAYITEM_CODE", payItemCode);
                }
                else if ("CITY_CODE".equals(attr[j]))
                {
                    String value = data.getString("CITY_CODE");
                    String cityCode = StaticUtil.getStaticValue(getVisit(), "TD_M_AREA", "AREA_CODE", "AREA_NAME", value);
                    dataOutput.getData(i).put("CITY_CODE", cityCode);
                }
            }

        }
        return dataOutput;
    }

}
