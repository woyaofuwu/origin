package com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.widenetmove;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.person.common.util.WideNetUtil;

public class NoPhoneWidenetMoveSVC extends CSBizService
{

    private static final long serialVersionUID = 1L;
    
    /**
     * 获取宽带主产品信息
     * */
    public IDataset getUserProductInfo(IData input) throws Exception
    {
        NoPhoneWidenetMoveBean widenetMoveBean = BeanManager.createBean(NoPhoneWidenetMoveBean.class);
        IDataset dataset = widenetMoveBean.getUserProductInfo(input);
        return dataset;
    }
    
    /**
     * 仅仅是为了得到WIDENETMOVE_FIRST或报错拦截不允许移机
     * 1、是否是宽带开户首月（a.用openDate与sysdate判断；b.用是否有生效优惠判断）不能移机，如果办理工号有WIDENETMOVE_FIRST权限则WIDENETMOVE_FIRST=1
     * 2、判断本月是否做过移机，根据widenet表的start_date与sysdate判断，widenet表的start_date这个字段在移机回单的时候，会进行更新
     * */
    public IDataset judgeIsCanMove(IData input) throws Exception
    {
        NoPhoneWidenetMoveBean widenetMoveBean = BeanManager.createBean(NoPhoneWidenetMoveBean.class);
        IDataset dataset = widenetMoveBean.judgeIsCanMove(input);
        return dataset;
    }
    
    /**
     * 获取宽带类型列表
     * */
    public IDataset showProdMode(IData input) throws Exception
    {
        NoPhoneWidenetMoveBean widenetMoveBean = BeanManager.createBean(NoPhoneWidenetMoveBean.class);
        IDataset dataset = widenetMoveBean.showProdMode(input);
        return dataset;
    }
    
    /**
     * 获取宽带用户other表中光猫信息  RSRV_VALUE_CODE='FTTH'
     * */
    public IDataset getModelInfo(IData input) throws Exception
    {
        NoPhoneWidenetMoveBean widenetMoveBean = BeanManager.createBean(NoPhoneWidenetMoveBean.class);
        input.put("TAB_NAME", "TF_F_USER_OTHER");
        input.put("SQL_REF", "SEL_USER_OTHER_FTTHMODERM");
        
        IDataset dataset = widenetMoveBean.getModelInfo(input);
        return dataset;
    }
    
    /**
     * 作用未知，获取6131配置
     * */
    public IDataset getSaleActiveComm(IData input) throws Exception
    {
        NoPhoneWidenetMoveBean widenetMoveBean = BeanManager.createBean(NoPhoneWidenetMoveBean.class);
        IDataset result = widenetMoveBean.getSaleActiveComm(input);
        
        //只有当时查询宽带营销活动时才调用营销包权限过滤接口
        if ("178".equals(input.getString("PARAM_ATTR")))
        {
            result = WideNetUtil.filterWideSaleActiveListByPriv(getVisit().getStaffId(), result);
        }
        
        return result;
    }
    
    /**
     * 获取产品信息，同时判断是否存在预约主产品变更IS_CHG_OTHER，和判断是否存在宽带包年优惠并传出相应值IS_HAS_YEAR_DISCNT、HAS_EFF_YEAR
     * */
    public IDataset getWidenetProductInfo(IData input) throws Exception
    {
    	NoPhoneWidenetMoveBean widenetMoveBean = BeanManager.createBean(NoPhoneWidenetMoveBean.class);
        IDataset dataset = widenetMoveBean.getWidenetProductInfo(input);
        return dataset;
    }
    
    /**
     * 根据AreaCode得到光猫厂家
     * */
    public IDataset getStaticInfoOnly(IData input) throws Exception
    {
        NoPhoneWidenetMoveBean widenetMoveBean = BeanManager.createBean(NoPhoneWidenetMoveBean.class);
        IDataset dataset = widenetMoveBean.getStaticInfoOnly(input);
        return dataset;
    }
    
    /**
     * 获取无手机宽带优惠信息
     * */
    public IDataset getUserDiscntInfo(IData input) throws Exception
    {
        NoPhoneWidenetMoveBean widenetMoveBean = BeanManager.createBean(NoPhoneWidenetMoveBean.class);
        IDataset dataset = widenetMoveBean.getUserDiscntInfo(input);
        return dataset;
    }
}
