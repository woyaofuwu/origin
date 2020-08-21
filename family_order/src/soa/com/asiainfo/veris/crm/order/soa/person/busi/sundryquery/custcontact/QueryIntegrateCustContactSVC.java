
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.custcontact;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class QueryIntegrateCustContactSVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    public IDataset custContactInit(IData input) throws Exception
    {
        QueryIntegrateCustContactBean bean = (QueryIntegrateCustContactBean) BeanManager.createBean(QueryIntegrateCustContactBean.class);
        IData data = new DataMap();
        IDataset datas = new DatasetList();

        data = bean.custContactInit();
        datas.add(data);
        return datas;
    }

    /*
     * public IDataset custContactIsReadOnly(IData input) throws Exception { QueryIntegrateCustContactBean bean =
     * (QueryIntegrateCustContactBean) BeanManager.createBean(QueryIntegrateCustContactBean.class); IData data = new
     * DataMap(); IDataset datas = new DatasetList(); data.put("RESULT", bean.custContactIsReadOnly()); datas.add(data);
     * return datas; }
     */

    public IDataset getCustContactSubInfos(IData input) throws Exception
    {
        QueryIntegrateCustContactBean bean = (QueryIntegrateCustContactBean) BeanManager.createBean(QueryIntegrateCustContactBean.class);
        IDataset datas = new DatasetList();

        datas = bean.getCustContactSubInfos(input);
        return datas;
    }

    public IDataset modifyIntegrateCustContact(IData input) throws Exception
    {
        QueryIntegrateCustContactBean bean = (QueryIntegrateCustContactBean) BeanManager.createBean(QueryIntegrateCustContactBean.class);

        return bean.modifyIntegrateCustContact(input, new Pagination(20));
    }

    /**
     * @data 2013-4-10
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset queryCustContact(IData input) throws Exception
    {
        QueryIntegrateCustContactBean bean = (QueryIntegrateCustContactBean) BeanManager.createBean(QueryIntegrateCustContactBean.class);

        return bean.queryCustContact(input, getPagination());
    }

}
