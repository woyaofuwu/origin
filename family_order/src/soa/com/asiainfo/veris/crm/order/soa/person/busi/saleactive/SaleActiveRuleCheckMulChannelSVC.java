package com.asiainfo.veris.crm.order.soa.person.busi.saleactive;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

/**
 * @author 梁端刚
 * @version V1.0
 * @date 2020/1/8 15:24
 */
public class SaleActiveRuleCheckMulChannelSVC extends CSBizService {
    /**
     *  获取营销活动
     * */
    public IDataset queryCampnTypes(IData userInfo) throws Exception{
        IData param=new DataMap();
        SaleActiveRuleCheckMulChannelBean bean= BeanManager.createBean(SaleActiveRuleCheckMulChannelBean.class);
        return bean.queryCampnTypes(param);
    }

    /**
     * 根据活动类型获取产品*
     * */
    public IDataset queryProductsByLabel(IData input) throws Exception
    {
        SaleActiveRuleCheckMulChannelBean bean= BeanManager.createBean(SaleActiveRuleCheckMulChannelBean.class);
        IDataset results = bean.querySaleActiveProductByLabel(input);
        return results;
    }

    /**
     * 根据产品ID获取包*
     * */
    public IDataset queryPackageByProdID(IData input) throws Exception
    {
        SaleActiveRuleCheckMulChannelBean bean= BeanManager.createBean(SaleActiveRuleCheckMulChannelBean.class);
        IDataset results = bean.queryPackageByProdID(input);
        return results;
    }
}
