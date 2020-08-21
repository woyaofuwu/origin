
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetcreateuser;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: CttBroadbandSVC.java
 * @Description: 铁通宽带业务view服务
 * @version: v1.0.0
 * @author: yxd
 * @date: 2014-3-3 上午10:41:40 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-3-3 yxd v1.0.0 修改原因
 */
public class CttBroadbandBean extends CSBizBean
{
    /**
     * @Function: getProductBySpec()
     * @Description: 根据产品规格获取产品
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: yxd
     * @date: 2014-3-19 下午5:24:03 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-3-19 yxd v1.0.0 修改原因
     */
	//铁通宽带业务已经下线了，要删除，有多处调用的地方，属于个人业务，涉及的代码由个人处理比较合适
    public IDataset getProductBySpec(String prodSpc) throws Exception
    {
        IData param = new DataMap();
        param.put("PROD_SPEC_TYPE", prodSpc);
        IDataset productSpecRelas = Dao.qryByCode("TD_S_PRODUCT_SPEC_RELA", "SEL_BY_PRODUCT_SPEC", param, Route.CONN_CRM_CEN);
        
        if (IDataUtil.isNotEmpty(productSpecRelas))
        {
            IData productSpecRela = null;
            
            for (int i = 0; i < productSpecRelas.size(); i++)
            {
                productSpecRela = productSpecRelas.getData(i);
                        
                productSpecRela.put("PRODUCT_NAME", UProductInfoQry.getProductNameByProductId(productSpecRela.getString("PRODUCT_ID")));   
            }
        }
        
        return productSpecRelas;
    }
}
