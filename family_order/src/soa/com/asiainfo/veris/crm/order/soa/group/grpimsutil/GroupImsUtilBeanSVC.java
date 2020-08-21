
package com.asiainfo.veris.crm.order.soa.group.grpimsutil;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.GroupImsUtil;

public class GroupImsUtilBeanSVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    public static IDataset ifMofficeTelePhoneUser(IData idata) throws Exception
    {
        return GroupImsUtil.ifMofficeTelePhoneUser(idata);
    }

    public IDataset getCreateMebFlag(IData indata) throws Exception
    {
        String cust_id = indata.getString("CUST_ID");
        String meb_user_id = indata.getString("MEB_USER_ID");
        // 判断是否还有订购的ims业务，如果没有则需要发创建成员报文到Centrex平台
        boolean result = GroupImsUtil.getCreateMebFlag(cust_id, meb_user_id);
        IData data = new DataMap();
        data.put("FLAG", result);
        IDataset dataset = new DatasetList();
        dataset.add(data);
        return dataset;
    }

    public IDataset getImpuStr4(IData indata) throws Exception
    {
        String user_id = indata.getString("USER_ID");
        String user_type = indata.getString("USER_TYPE");
        int index = indata.getInt("INDEX");
        // 获取impu表扩展字段4字符串
        String result = GroupImsUtil.getImpuStr4(user_id, user_type, index);
        IData data = new DataMap();
        data.put("SHORT_CODE", result);
        IDataset dataset = new DatasetList();
        dataset.add(data);
        return dataset;
    }
}
