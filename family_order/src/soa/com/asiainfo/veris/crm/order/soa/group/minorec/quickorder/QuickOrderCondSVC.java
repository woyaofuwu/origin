package com.asiainfo.veris.crm.order.soa.group.minorec.quickorder;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.group.esop.query.QuickOrderCondBean;

public class QuickOrderCondSVC extends CSBizService{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static IDataset getEspProductApplyInfo(IData param) throws Exception
	{
		return QuickOrderCondBean.getEspProductApplyInfo(param);
	}

    /**
     * 根据CUST_ID、IBSYSID以及产品编码获取客户预受理快速办理的产品相关信息
     *
     * @param
     * @return
     * @throws Exception
     * @author zhengkai5
     */
    public static IDataset getConInfoByIbsysidAndSnAndProductId(IData param) throws Exception
    {
        IDataUtil.chkParam(param , "PRODUCT_ID");
        IDataUtil.chkParam(param , "IBSYSID");
        IDataUtil.chkParam(param , "RSRV_STR1");

        return QuickOrderCondBean.getConInfoByIbsysidAndSnAndProductId(param);
    }
}
