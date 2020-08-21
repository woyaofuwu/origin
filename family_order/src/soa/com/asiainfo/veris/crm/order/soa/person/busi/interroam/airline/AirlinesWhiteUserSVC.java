
package com.asiainfo.veris.crm.order.soa.person.busi.interroam.airline;

import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.ailk.service.bean.BeanManager;

public class AirlinesWhiteUserSVC extends CSBizService {

    private static final long serialVersionUID = 1L;

    public IDataset queryAirlineswhite(IData userInfo) throws Exception {
        Pagination pagination = getPagination();
        AirlinesWhiteUserBean bean = (AirlinesWhiteUserBean) BeanManager.createBean(AirlinesWhiteUserBean.class);
        return bean.queryAirlineswhite(userInfo, pagination);
    }

    public IData createAirlinesWhite(IData userInfo) throws Exception {
        AirlinesWhiteUserBean bean = (AirlinesWhiteUserBean) BeanManager.createBean(AirlinesWhiteUserBean.class);
        return bean.createAirlinesWhite(userInfo);
    }

    public IData deleteAirlinesWhite(IData userInfo) throws Exception {
        AirlinesWhiteUserBean bean = (AirlinesWhiteUserBean) BeanManager.createBean(AirlinesWhiteUserBean.class);
        return bean.deleteAirlinesWhite(userInfo);
    }

    public IData batImportAirList(IData userInfo) throws Exception {
        AirlinesWhiteUserBean bean = (AirlinesWhiteUserBean) BeanManager.createBean(AirlinesWhiteUserBean.class);
        return bean.batImportAirList(userInfo);
    }

    // AEE定时任务调用接口，将REMOVE_TAG为2的白名单用户进行退订同步国漫，然后将REMOVE_TAG改成1
    public void batChangeUserState() throws Exception {
        AirlinesWhiteUserBean bean = (AirlinesWhiteUserBean) BeanManager.createBean(AirlinesWhiteUserBean.class);
        bean.batChangeUserState();
    }

    // 用于“全球通无限尊享计划套餐”套餐费打八折订购/取消（供短厅使用，包含短信下发）
    public IData gsmUnlimitedPackage8ForSms(IData input) throws Exception {
        AirlinesWhiteUserBean bean = (AirlinesWhiteUserBean) BeanManager.createBean(AirlinesWhiteUserBean.class);
        return bean.gsmUnlimitedPackage8ForSms(input);
    }

    // 国漫专属叠加日包、国漫专属叠加月包订购/取消接口（供短厅使用，包含短信下发）
    public IData interRoamDayForSms(IData input) throws Exception {
        AirlinesWhiteUserBean bean = (AirlinesWhiteUserBean) BeanManager.createBean(AirlinesWhiteUserBean.class);
        return bean.interRoamDayForSms(input);
    }
}
