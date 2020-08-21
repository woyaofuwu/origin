
package com.asiainfo.veris.crm.order.soa.person.busi.familytrade;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class FamilyCreateSVC extends CSBizService
{

    public IData checkAddMeb(IData input) throws Exception
    {
        FamilyCreateBean bean = BeanManager.createBean(FamilyCreateBean.class);
        return bean.checkAddMeb(input);
    }

    public IDataset checkAddMebMul(IData input) throws Exception
    {
        FamilyCreateBean bean = BeanManager.createBean(FamilyCreateBean.class);
        return bean.checkAddMebMul(input);
    }

    /**
     * 亲亲网组建/新增成员受理接口
     * 
     * @param input
     * @return
     * @throws Exception
     * @author zhouwu
     */
    public IData createFamily(IData input) throws Exception
    {
        FamilyCreateBean bean = BeanManager.createBean(FamilyCreateBean.class);
        return bean.createFamily(input);
    }

    /**
     * 亲亲网组建/新增成员受理接口
     *
     * @param input
     * @return
     * @throws Exception
     * @author
     */
    public IData createFamilyNew(IData input) throws Exception
    {
        FamilyCreateBean bean = BeanManager.createBean(FamilyCreateBean.class);
        return bean.createFamilyNew(input);
    }

    /**
     * 亲亲网组建/新增成员校验接口
     * 
     * @param input
     * @return
     * @throws Exception
     * @author zhouwu
     */
    public IData createFamilyCheck(IData input) throws Exception
    {
        FamilyCreateBean bean = BeanManager.createBean(FamilyCreateBean.class);
        return bean.createFamilyCheck(input);
    }

    public IDataset getAllMebByMainSn(IData input) throws Exception
    {
        FamilyCreateBean bean = BeanManager.createBean(FamilyCreateBean.class);
        return bean.getAllMebByMainSn(input);
    }

    public IData getAllMebList(IData input) throws Exception
    {
        FamilyCreateBean bean = BeanManager.createBean(FamilyCreateBean.class);
        return bean.getAllMebList(input);
    }

    public IDataset getRejectRemindInfo(IData input) throws Exception
    {
        FamilyCreateBean bean = BeanManager.createBean(FamilyCreateBean.class);
        return bean.getRejectRemindInfo(input);
    }

    public IData getViceMebList(IData input) throws Exception
    {
        FamilyCreateBean bean = BeanManager.createBean(FamilyCreateBean.class);
        return bean.getViceMebList(input);
    }

    public IData loadDestroyFamilyInfo(IData input) throws Exception
    {
        FamilyCreateBean bean = BeanManager.createBean(FamilyCreateBean.class);
        return bean.loadDestroyFamilyInfo(input);
    }
    
    /**
     * 获取亲亲网套餐升级界面信息
     * 
     * @param input
     * @return
     * @throws Exception
     * @author yanwu
     */
    public IData loadUpdateFamilyInfo(IData input) throws Exception
    {
        FamilyCreateBean bean = BeanManager.createBean(FamilyCreateBean.class);
        return bean.loadUpdateFamilyInfo(input);
    }

    public IData loadFamilyCreateInfo(IData input) throws Exception
    {
        FamilyCreateBean bean = BeanManager.createBean(FamilyCreateBean.class);
        return bean.loadFamilyCreateInfo(input);
    }
    
    public IDataset obtainShortCodes(IData input)throws Exception{
    	return StaticUtil.getStaticList("FAMILY_SHORT");
    }

    public IData loadFamilyCreateInfoNew(IData input) throws Exception
    {
        FamilyCreateBean bean = BeanManager.createBean(FamilyCreateBean.class);
        return bean.loadFamilyCreateInfoNew(input);
    }
}
