
package com.asiainfo.veris.crm.order.soa.person.busi.score.scoreconvert;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class ScoreConvertSVC extends CSBizService
{
    public IData cancelConvertGiftOrder(IData input) throws Exception
    {
        ScoreConvertBean scoreConvertBean = (ScoreConvertBean) BeanManager.createBean(ScoreConvertBean.class);
        return scoreConvertBean.cancelConvertGiftOrder(input);
    }

    public IData getCommInfo(IData input) throws Exception
    {
        ScoreConvertBean scoreConvertBean = (ScoreConvertBean) BeanManager.createBean(ScoreConvertBean.class);
        return scoreConvertBean.getCommInfo(input);
    }

    public IDataset queryCity(IData input) throws Exception
    {
        ScoreConvertBean scoreConvertBean = (ScoreConvertBean) BeanManager.createBean(ScoreConvertBean.class);
        return scoreConvertBean.getIBOSSCity(input);
    }

    public IDataset queryConvertGiftOrder(IData input) throws Exception
    {
        ScoreConvertBean scoreConvertBean = (ScoreConvertBean) BeanManager.createBean(ScoreConvertBean.class);
        return scoreConvertBean.queryConvertGiftOrder(input);
    }

    public IDataset queryConvertRecord(IData input) throws Exception
    {
        ScoreConvertBean scoreConvertBean = (ScoreConvertBean) BeanManager.createBean(ScoreConvertBean.class);
        return scoreConvertBean.queryConvertRecord(input, getPagination());
    }

    public IDataset queryDistrict(IData input) throws Exception
    {
        ScoreConvertBean scoreConvertBean = (ScoreConvertBean) BeanManager.createBean(ScoreConvertBean.class);
        return scoreConvertBean.getIBOSSDistrict(input);
    }

    public IData queryGiftCount(IData input) throws Exception
    {
        ScoreConvertBean scoreConvertBean = (ScoreConvertBean) BeanManager.createBean(ScoreConvertBean.class);
        return scoreConvertBean.queryGiftCount(input);
    }

    public IDataset queryGifts(IData input) throws Exception
    {
        ScoreConvertBean scoreConvertBean = (ScoreConvertBean) BeanManager.createBean(ScoreConvertBean.class);
        return scoreConvertBean.queryGifts(input, getPagination());
    }
}
