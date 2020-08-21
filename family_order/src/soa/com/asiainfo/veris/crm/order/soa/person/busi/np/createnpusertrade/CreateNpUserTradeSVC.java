
package com.asiainfo.veris.crm.order.soa.person.busi.np.createnpusertrade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;

public class CreateNpUserTradeSVC extends CSBizService
{

    public IData checkAndInitProduct(IData param) throws Exception
    {
        CreateNpUserTradeBean bean = BeanManager.createBean(CreateNpUserTradeBean.class);
        IData data = new DataMap();
        data.put("checkSerialNumber", bean.checkSerialNumber(param));
        data.put("getProductInfos", bean.getProductInfos(param));
        return data;
    }

    public IDataset checkRealNameLimitByPspt(IData input) throws Exception
    {
        IDataset ajaxDataset = new DatasetList();
        String custName = input.getString("custName");
        String psptId = input.getString("psptId");
        IData param = new DataMap();
        if (!"".equals(custName) && !"".equals(psptId))
        {
            param.put("CUST_NAME", custName);
            param.put("PSPT_ID", psptId);

            int rCount = UserInfoQry.getRealNameUserCountByPspt2(custName, psptId);// 改造成强对象
            int rLimit = UserInfoQry.getRealNameUserLimitByPspt(custName, psptId);

            IData ajaxData = new DataMap();

            if (rCount < rLimit)
            {
                ajaxData.put("MSG", "OK");
                ajaxData.put("CODE", "0");
            }
            else
            {
                ajaxData.put("MSG", "证件号码【" + psptId + "】实名制开户的数量已达到最大值【" + rLimit + "个】，请更换其它证件！");
                ajaxData.put("CODE", "1");
            }
            ajaxDataset.add(ajaxData);

        }
        return ajaxDataset;
    }

    public IData checkSerialNumber(IData param) throws Exception
    {
        CreateNpUserTradeBean bean = BeanManager.createBean(CreateNpUserTradeBean.class);

        return bean.checkSerialNumber(param);
    }

    public IData checkSimCardNo(IData param) throws Exception
    {
        CreateNpUserTradeBean bean = BeanManager.createBean(CreateNpUserTradeBean.class);

        return bean.checkSimCardNo(param);
    }

    public IDataset getProductFeeInfo(IData param) throws Exception
    {
        CreateNpUserTradeBean bean = BeanManager.createBean(CreateNpUserTradeBean.class);
        return bean.getProductFeeInfo(param);
    }

    public IData getProductInfos(IData param) throws Exception
    {
        CreateNpUserTradeBean bean = BeanManager.createBean(CreateNpUserTradeBean.class);
        return bean.getProductInfos(param);
    }
    
    public IData checkBeforeNpUser(IData param) throws Exception
    {
    	CreateNpUserTradeBean bean = BeanManager.createBean(CreateNpUserTradeBean.class);
    	return bean.checkBeforeNpUser(param);
    }
}
