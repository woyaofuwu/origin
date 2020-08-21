package com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.recepHallOrderOpen.productDeal;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import org.apache.log4j.Logger;

import java.io.Serializable;

public class FixedPhoneCloudVedioMessageSVC extends CSBizService {

    private static final long serialVersionUID = -3963317842456369670L;

    private static final Logger log = Logger.getLogger(FixedPhoneCloudVedioMessageSVC.class);

    public static IDataset fixedPhoneCloudVedioCreateUser(IData map) throws Exception {
        FixedPhoneCloudVedioMessageBean bean = BeanManager.createBean(FixedPhoneCloudVedioMessageBean.class);

        return bean.fixedPhoneCloudVedioCreateUser(map);
    }

    public static IDataset fixedPhoneCloudVedioCancleUser(IData map) throws Exception {
        FixedPhoneCloudVedioMessageBean bean = BeanManager.createBean(FixedPhoneCloudVedioMessageBean.class);

        return bean.fixedPhoneCloudVedioCancleUser(map);
    }
}
