package com.asiainfo.veris.crm.order.soa.person.busi.np.createnpuser;


import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.bizservice.base.CSBizBean;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserNpInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;

public class FastNpUserNewSVC extends CSBizService {

	/**
	 *快速携回
	 */

	public IData checkSnNPInfo(IData input) throws Exception
    {
		FastNpUserNewBean bean = BeanManager.createBean(FastNpUserNewBean.class);
		return bean.checkSnNPInfo(input);
    }
	
	public IData insUserOther(IData input) throws Exception {
		FastNpUserNewBean bean = BeanManager.createBean(FastNpUserNewBean.class);
		return bean.insUserOther(input);
	}
}
