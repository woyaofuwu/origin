
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.userimsiinfo;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class QueryUserImsiInfoSVC extends CSBizService
{
    private static final long serialVersionUID = 4829236996987004886L;

    /**
     * 功能：获取IMSI数据文件FTP配置参数 作者：GongGuang
     */
    public IDataset getImsiFtpParams(IData data) throws Exception
    {
        QueryUserImsiInfoBean bean = (QueryUserImsiInfoBean) BeanManager.createBean(QueryUserImsiInfoBean.class);
        return bean.getImsiFtpParams(data, getPagination());
    }
}
