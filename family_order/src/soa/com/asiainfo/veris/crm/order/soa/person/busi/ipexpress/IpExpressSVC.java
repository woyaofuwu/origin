
package com.asiainfo.veris.crm.order.soa.person.busi.ipexpress;

import org.apache.commons.lang.StringUtils;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.IpExpressException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.util.DataSetUtils;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;

public class IpExpressSVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    public IDataset checkPhone(IData dataInfo) throws Exception
    {
        IpExpressBean bean = BeanManager.createBean(IpExpressBean.class);
        return bean.checkPhone(dataInfo);
    }

    /*
     * 接口
     */
    public IDataset getIpExpress(IData userInfo) throws Exception
    {

        // 查询已绑定号码
        IpExpressBean bean = BeanManager.createBean(IpExpressBean.class);
        if(StringUtils.isEmpty(userInfo.getString("RELATION_TYPE_CODE",""))){
        	userInfo.put("RELATION_TYPE_CODE","50");
        }
        IDataset results = bean.getUURelationInfo(userInfo, getPagination());
        if (DataSetUtils.isBlank(results))
        {
            CSAppException.apperr(IpExpressException.CRM_IPEXPRESS_4);
        }
        return results;
    }

    public IData getIpExpressInfo(IData userInfo) throws Exception
    {
        IpExpressBean bean = BeanManager.createBean(IpExpressBean.class);
        // IDataset returnInfos = new DatasetList();
        // returnInfos.add(bean.getIpExpressInfo(userInfo));
        return bean.getIpExpressInfo(userInfo);
    }

    public IDataset getSvcByProductId(IData productInfo) throws Exception
    {
        IpExpressBean bean = BeanManager.createBean(IpExpressBean.class);

        return bean.getSvcByProductId(productInfo);
    }
}
