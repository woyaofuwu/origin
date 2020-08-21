
package com.asiainfo.veris.crm.order.soa.person.busi.infomanage;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;

public class InformationManageSVC extends CSBizService
{

    private static final long serialVersionUID = 1L;

    public void checkCommonValue(IData data) throws Exception
    {
        if (!data.getString("MODIFY_TAG", "").equals("0") && !data.getString("MODIFY_TAG", "").equals("1"))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "450000:MODIFY_TAG字段不能为空且只能为0或1");
        }
    }

    public IData createOrUpdateInfoManage(IData data) throws Exception
    {
        InformationManageBean bean = (InformationManageBean) BeanManager.createBean(InformationManageBean.class);
        bean.checkCreateOrUpdateValue(data);
        return bean.createOrUpdateInfoManage(data);
    }

    public IData delInfoManage(IData data) throws Exception
    {
        InformationManageBean bean = (InformationManageBean) BeanManager.createBean(InformationManageBean.class);
        bean.checkDelValue(data);
        return bean.delInfoManage(data);
    }

    public IDataset getInformationByUserId(IData input) throws Exception
    {
        InformationManageBean bean = (InformationManageBean) BeanManager.createBean(InformationManageBean.class);

        IDataset ret = bean.getAllInformationInfo(input);
        return ret;
    }

    public IData infoManageDeal(IData data) throws Exception
    {
        checkCommonValue(data);
        String modify_tag = data.getString("MODIFY_TAG", "");
        IData result = new DataMap();
        if (modify_tag.equals("0"))
        {
            result = createOrUpdateInfoManage(data);
        }
        else if (modify_tag.equals("1"))
        {
            result = delInfoManage(data);
        }
        else
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "450000:MODIFY_TAG字段不能为空且只能为0或1");
        }
        IData returnData = new DataMap();
        returnData.put("X_CHECK_INFO", "Trade OK!");
        returnData.put("X_CHECK_TAG", "0");
        returnData.putAll(result);
        return returnData;
    }

}
