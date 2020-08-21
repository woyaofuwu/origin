
package com.asiainfo.veris.crm.order.soa.group.sendgift;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class GiftMarkeQrySVC extends CSBizService
{
    private static final long serialVersionUID = 1L;
    
    /**
     * 根据SERIAL_NUMBER查客户信息及集团信息
     * @param input
     * @return
     * @throws Exception
     * @author chenhh6
     */
    public IDataset getCustAndGroupInfoBySn(IData input) throws Exception
    {
        return GiftMarkeQry.getCustAndGroupInfoBySn(input, null);
    }
    
    /**
     * 保存礼品赠送记录
     * @param input
     * @return
     * @throws Exception
     * @author chenhh6
     */
    public IData saveSendGiftInfo(IData input) throws Exception
    {
       return GiftMarkeQry.saveSendGiftInfo(input);
    }

    /**
     * 集团礼品营销活动的查询
     * @param inParam
     * @return
     * @throws Exception
     * @author chenhh6
     */
    public IDataset querySendGiftInfo(IData inParam) throws Exception
    { 
    	return GiftMarkeQry.querySendGiftInfo(inParam,getPagination());
    }
    
    /**
     * 查询集团礼品信息
     * @param input
     * @return
     * @throws Exception
     * @author chenhh6
     */
    public IDataset getGiftInfo(IData input) throws Exception
    {
        return GiftMarkeQry.getGiftInfo(input, null);
    }
}
