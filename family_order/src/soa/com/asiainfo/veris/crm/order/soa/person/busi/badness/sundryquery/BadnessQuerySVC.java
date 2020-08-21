
package com.asiainfo.veris.crm.order.soa.person.busi.badness.sundryquery;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.person.busi.badnessinfo.BadnessInfoBean;

public class BadnessQuerySVC extends CSBizService
{

    public IDataset queryBadHastenInfo(IData data) throws Exception
    {
        BadnessQueryBean bean = new BadnessQueryBean();
        return bean.queryBadHastenInfo(data, this.getPagination());
    }

    public IDataset queryBadnessInfoImpeach(IData data) throws Exception
    {
        BadnessQueryBean bean = new BadnessQueryBean();
        return bean.queryBadnessInfoImpeach(data, this.getPagination());
    }

    public IDataset queryBadnessReleaseInfo(IData data) throws Exception
    {
        BadnessQueryBean bean = new BadnessQueryBean();
        return bean.queryBadnessReleaseInfo(data, this.getPagination());
    }

    public IDataset queryBaseBadnessInfo(IData data) throws Exception
    {
        BadnessQueryBean bean = new BadnessQueryBean();
        return bean.queryBaseBadnessInfo(data, this.getPagination());
    }

    public IDataset queryOtherBadnessInfo(IData data) throws Exception
    {
        BadnessQueryBean bean = new BadnessQueryBean();
        return bean.queryOtherBadnessInfo(data);
    }

    public IDataset queryOtherProvBadnessInfo(IData data) throws Exception
    {
        BadnessQueryBean bean = new BadnessQueryBean();
        return bean.queryOtherProvBadnessInfo(data, this.getPagination());
    }

    public IDataset queryReporterBlack(IData data) throws Exception
    {
        BadnessQueryBean bean = new BadnessQueryBean();
        return bean.queryReporterBlack(data);
    }

    /**
     * @Function: submitBadnessInfos
     * @Description: 本地举报处理
     * @date May 29, 2014 2:44:07 PM
     * @param data
     * @return
     * @throws Exception
     * @author longtian3
     */
    public IDataset submitBadnessInfos(IData data) throws Exception
    {
        BadnessQueryBean bean = new BadnessQueryBean();
        return bean.submitBadnessInfos(data);
    }

    /**
     * @Function: tempDeal
     * @Description: 临时说明提交
     * @date May 29, 2014 2:44:18 PM
     * @param data
     * @return
     * @throws Exception
     * @author longtian3
     */
    public IDataset tempDeal(IData data) throws Exception
    {
        BadnessQueryBean bean = new BadnessQueryBean();
        return bean.tempDeal(data);
    }
    /**
     * @Function: tempDeal
     * @Description: 临时说明提交
     * @date May 29, 2014 2:44:18 PM
     * @param data
     * @return
     * @throws Exception
     * @author longtian3
     */
    public void SMSRemind(IData data) throws Exception
    {
        BadnessQueryBean bean = new BadnessQueryBean();
        bean.SMSRemind(data);
    }
    public IData queryInfectLogInfo(IData param) throws Exception
    {
    	IDataset dataset = new DatasetList();
    	dataset = Dao.qryByCodeParser("TI_B_POISONING_SITUATION", "SEL_BY_USER_FILTER", param,getPagination(), Route.CONN_CRM_CEN);
    	IData data = new DataMap();
	    data.put("RET", dataset);
		return data;
    }
   
}
