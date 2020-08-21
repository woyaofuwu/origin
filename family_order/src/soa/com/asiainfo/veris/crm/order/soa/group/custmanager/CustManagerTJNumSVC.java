
package com.asiainfo.veris.crm.order.soa.group.custmanager;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderService;

public class CustManagerTJNumSVC extends GroupOrderService
{
    public IDataset checkCustManagerStaff(IData param) throws Exception
    {
        CustManagerTJNumBean bean = new CustManagerTJNumBean();

        return bean.checkCustManagerStaff(param);
    }

    public IDataset checkGbmBySerialNumber(IData param) throws Exception
    {
        CustManagerTJNumBean bean = new CustManagerTJNumBean();

        return bean.checkGbmBySerialNumber(param);
    }

    public IDataset checkSerialNumberCont(IData param) throws Exception
    {
        CustManagerTJNumBean bean = new CustManagerTJNumBean();

        return bean.checkSerialNumberCont(param);
    }

    public IDataset countCustManagerTjNum(IData param) throws Exception
    {
        CustManagerTJNumBean bean = new CustManagerTJNumBean();

        return bean.checkSerialNumberCont(param);

    }

    public IDataset insertCustManagerTJNum(IData param) throws Exception
    {
        CustManagerTJNumBean bean = new CustManagerTJNumBean();

        return bean.insertCustManagerTJNum(param);
    }

    public IDataset queryCustManagerTjNums(IData param) throws Exception
    {
        CustManagerTJNumBean bean = new CustManagerTJNumBean();

        return bean.queryCustManagerTjNums(param);

    }

    public IDataset queryProductsByType(IData param) throws Exception
    {
        CustManagerTJNumBean bean = new CustManagerTJNumBean();

        return bean.queryProductsByType(param);
    }

    public IDataset custManagerTJNum(IData map) throws Exception
    {
        CustManagerTJNumBean bean = new CustManagerTJNumBean();

        return bean.custManagerTJNum(map);
    }
}
