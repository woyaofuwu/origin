
package com.asiainfo.veris.crm.order.soa.person.busi.np.npapplycancelin;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: InNpApplyCancelSVC.java
 * @Description:1503--携入申请取消（携入方落地）
 * @version: v1.0.0
 * @author: lijm3
 * @date: 2014-5-7 下午4:08:05 Modification History: Date Author Version Description
 *        ---------------------------------------------------------* 2014-5-7 lijm3 v1.0.0 修改原因
 */
public class InNpApplyCancelSVC extends CSBizService
{

    public IDataset getNpTradeInfos(IData param) throws Exception
    {
        InNpApplyCancelBean bean = BeanManager.createBean(InNpApplyCancelBean.class);
        return bean.getNpTradeInfos(param);
    }

    /**
     * 总部申请没有传SERIAL_NUMBER
     */
    public void setTrans(IData input)
    {
        String serialNumber = input.getString("NPCODE");
        if (StringUtils.isNotBlank(serialNumber))
        {
            input.put("SERIAL_NUMBER", serialNumber);
        }
    }

    public IDataset tradeReg(IData param) throws Exception
    {
        InNpApplyCancelBean bean = BeanManager.createBean(InNpApplyCancelBean.class);
        return bean.tradeReg(param);
    }

}
