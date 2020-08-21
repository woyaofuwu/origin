
package com.asiainfo.veris.crm.order.soa.person.busi.intf;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.time.DateUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.person.busi.view360.Qry360THInfoBean;

public class QueryAndSetSPSVC extends CSBizService
{

    /**
     * 根据入参查询梦网业务开关状态
     * 
     * @param inParam
     * @throws Exception
     */
    public IData querySPOpenOff(IData inputData) throws Exception
    {
    	getVisit().setLoginEparchyCode("0898");
    	getVisit().setStaffEparchyCode("0898");
    	QueryAndSetSPBean bean = BeanManager.createBean(QueryAndSetSPBean.class);
    	return bean.querySPOpenOff(inputData); 
    }

    /**
     * 根据入参设置梦网业务开关状态
     * 
     * @param data
     * @throws Exception
     */
    public IData setSPOpenOff(IData data) throws Exception
    {
    	getVisit().setLoginEparchyCode("0898");
    	getVisit().setStaffEparchyCode("0898");
    	QueryAndSetSPBean bean = BeanManager.createBean(QueryAndSetSPBean.class);
    	return bean.setSPOpenOff(data); 
    }
}
