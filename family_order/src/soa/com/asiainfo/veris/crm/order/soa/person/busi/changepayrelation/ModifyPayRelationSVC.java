
package com.asiainfo.veris.crm.order.soa.person.busi.changepayrelation;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.diversifyacct.DiversifyAcctUtil;

public class ModifyPayRelationSVC extends CSBizService
{

    protected static Logger log = Logger.getLogger(ModifyPayRelationSVC.class);

    private static final long serialVersionUID = 1L;

    /**
     * 取业务参数
     */
    public IData getBusiParam(IData inParams) throws Exception
    {
        String str = DiversifyAcctUtil.getFirstDayThisAcct(inParams.getString("USER_ID"));// 获取用户当前账期
        IData result = new DataMap();
        result.put("START_CYCLE_ID", SysDateMgr.decodeTimestamp(str, "yyyyMMdd"));
        return result;
    }

}
