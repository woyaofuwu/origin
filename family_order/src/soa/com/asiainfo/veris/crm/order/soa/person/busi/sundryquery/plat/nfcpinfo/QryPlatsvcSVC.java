
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.plat.nfcpinfo;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

/**
 * @CREATED by gongp@2013-10-9 修改历史 Revision 2013-10-9 下午05:18:34
 */
public class QryPlatsvcSVC extends CSBizService
{

    /**
	 * 
	 */
    private static final long serialVersionUID = 3664907592180270227L;

    /**
     * 查询手机支付缴费记录
     * 
     * @param param
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2013-9-20
     */
    public IDataset gryPayState(IData param) throws Exception
    {
        QryPlatSvcBean bean = (QryPlatSvcBean) BeanManager.createBean(QryPlatSvcBean.class);
        return bean.qryPayState(param);
    }

    /**
     * 查询手机钱包应用下载及开通
     * 
     * @param param
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2013-9-20
     */
    public IDataset qryNFCPInfo(IData param) throws Exception
    {
        QryPlatSvcBean bean = (QryPlatSvcBean) BeanManager.createBean(QryPlatSvcBean.class);
        return bean.qryNFCPInfo(param);
    }


    @SuppressWarnings("static-access")
	public IDataset getFamilyCircle(IData param) throws Exception{
        QryPlatSvcBean bean = (QryPlatSvcBean) BeanManager.createBean(QryPlatSvcBean.class);
    	return bean.queryFamilyCircle(param);
    }
    
    public IDataset synFamilyCircleInfoForIBoss(IData param) throws Exception{
    	QryPlatSvcBean bean = (QryPlatSvcBean) BeanManager.createBean(QryPlatSvcBean.class);
    	return bean.synFamilyCircleInfoForIBoss(param);
    }
    
    @SuppressWarnings("static-access")
	public IDataset getSafeGroup(IData param) throws Exception{
        QryPlatSvcBean bean = (QryPlatSvcBean) BeanManager.createBean(QryPlatSvcBean.class);
    	return bean.querySafeGroup(param);
    }
}
