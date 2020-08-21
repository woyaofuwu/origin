/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.billsecprotectuserlistqrysvc;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap; 
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

/**
 * @CREATED by 
 */
public class BillSecProtectUserListQrySVC extends CSBizService
{
    private static final long serialVersionUID = 6328471594629158805L;

   
    /**
     * 
     * zhengdx 20180629 
     * 计费安全保护用户清单查询
     * SS.BillSecProtectUserListQrySVC.qryBillSecProtectUserList
     */
    public IDataset qryBillSecProtectUserList(IData inparams) throws Exception
    { 
    	String serialNumber=inparams.getString("SERIAL_NUMBER");
    	String protectTypeCode=inparams.getString("PROTECT_TYPE_CODE");
    	
    	IDataset qryList= BillSecProtectUserListQryBean.qryBillSecProtectUserList(serialNumber,protectTypeCode,getPagination());

    	return qryList;
    }
}
