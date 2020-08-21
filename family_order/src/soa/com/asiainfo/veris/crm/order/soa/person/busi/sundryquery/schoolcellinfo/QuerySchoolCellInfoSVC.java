
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.schoolcellinfo;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class QuerySchoolCellInfoSVC extends CSBizService
{
    private static final long serialVersionUID = 4829236996987004886L;

    /**
     * 查询校园卡名称
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset querySchoolCards(IData param) throws Exception
    {
        QuerySchoolCellInfoBean bean = BeanManager.createBean(QuerySchoolCellInfoBean.class);
        IDataset areas = bean.querySchoolCards();
        return areas;
    }

    /**
     * 功能：校园卡基站信息查询 作者：GongGuang
     */
    public IDataset querySchoolCellInfo(IData data) throws Exception
    {
        QuerySchoolCellInfoBean bean = (QuerySchoolCellInfoBean) BeanManager.createBean(QuerySchoolCellInfoBean.class);
        return bean.querySchoolCellInfo(data, getPagination());
    }
}
