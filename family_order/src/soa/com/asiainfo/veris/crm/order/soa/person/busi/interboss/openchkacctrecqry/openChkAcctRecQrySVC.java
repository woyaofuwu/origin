/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.interboss.openchkacctrecqry;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap; 
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.person.busi.interboss.remotewritecard.RemoteWriteCardIntfBean;

/**
 * @CREATED by 
 */
public class openChkAcctRecQrySVC extends CSBizService
{
    private static final long serialVersionUID = 6328473597629158705L;

   
    /**
     * 
     * zhengdx 20180724
     * 能力开放平台日对账查询
     * SS.openChkAcctRecQrySVC.qryOpenChkAcctRecList
     */
    public IDataset qryOpenChkAcctRecList(IData inparams) throws Exception
    { 
    	openChkAcctRecQryBean bean = (openChkAcctRecQryBean) BeanManager.createBean(openChkAcctRecQryBean.class);
    	IDataset qryList= bean.qryOpenChkAcctRecList(inparams,getPagination());
      
    	return qryList;
    }
    
    public IDataset qryOpenChkAcctRecDayAll(IData inparams) throws Exception
    { 
    	openChkAcctRecQryBean bean = (openChkAcctRecQryBean) BeanManager.createBean(openChkAcctRecQryBean.class);
    	IDataset qryList= bean.qryOpenChkAcctRecDayAll(inparams,getPagination());

    	return qryList;
    }
}
