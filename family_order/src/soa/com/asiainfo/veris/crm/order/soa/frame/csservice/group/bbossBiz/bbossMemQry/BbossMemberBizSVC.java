package com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.bbossMemQry;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderService;

public class BbossMemberBizSVC extends GroupOrderService{
	
    private static final long serialVersionUID = 1L;

    /**
	 * chenyi
	 * 2015-2-9
	 * 获取成员查询信息
	 * @param data
	 * @return
	 * @throws Exception
	 */
    public static final IDataset getOrderInfo(IData data) throws Exception
    {

          IDataset  result = BbossMemberBiz.getOrderInfo(data);
          return result;

    }
}
