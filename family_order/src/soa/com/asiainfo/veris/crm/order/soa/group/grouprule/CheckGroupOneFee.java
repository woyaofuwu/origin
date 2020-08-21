
package com.asiainfo.veris.crm.order.soa.group.grouprule;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

public class CheckGroupOneFee extends BreBase implements IBREScript
{

    private static final long serialVersionUID = 2115858094128338121L;

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        IDataset feeList = new DatasetList();

        IData feeData_9100 = new DataMap();
        feeData_9100.put("FEE_MODE", "0"); // 营业费用
        feeData_9100.put("FEE_TYPE_CODE", "9100"); // 开户费
        feeData_9100.put("FEE_TYPE_NAME", "开户费");
        feeData_9100.put("FEE", "0");
        feeList.add(feeData_9100);

        IData feeData_9200 = new DataMap();
        feeData_9200.put("FEE_MODE", "0"); // 营业费用
        feeData_9200.put("FEE_TYPE_CODE", "9200"); // 业务调试费
        feeData_9200.put("FEE_TYPE_NAME", "业务调试费");
        feeData_9200.put("FEE", "0");
        feeList.add(feeData_9200);

        IData feeData_9300 = new DataMap();
        feeData_9300.put("FEE_MODE", "0"); // 营业费用
        feeData_9300.put("FEE_TYPE_CODE", "9300"); // 铃音制作费
        feeData_9300.put("FEE_TYPE_NAME", "铃音制作费");
        feeData_9300.put("FEE", "0");
        feeList.add(feeData_9300);

        IData feeData_9400 = new DataMap();
        feeData_9400.put("FEE_MODE", "0"); // 营业费用
        feeData_9400.put("FEE_TYPE_CODE", "9400"); // 铃音定制费
        feeData_9400.put("FEE_TYPE_NAME", "铃音定制费");
        feeData_9400.put("FEE", "0");
        feeList.add(feeData_9400);

        databus.put("FEELIST", feeList);

        return false;
    }

}
