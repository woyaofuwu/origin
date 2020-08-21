
package com.asiainfo.veris.crm.order.soa.person.busi.ziyoubusiness;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.basequeryrecord.BaseQueryBean;

public class ZiYouBusinessBaseBean extends BaseQueryBean
{

    private static final Logger logger = LoggerFactory.getLogger(ZiYouBusinessBaseBean.class);

   
    /**
     * 更新日志表
     *
     * @param indictSeq
     * @throws Exception
     */
    public void updatePlatLog(String indictSeq, String hisTag) throws Exception
    {
        String remark = "";

        if ("0".equals(hisTag))
        {
            remark = "业务办理服务执行完毕归档";
        }
        if ("1".equals(hisTag))
        {
            remark = "查询数据获取完毕归档";
        }
        if ("2".equals(hisTag))
        {
            remark = "关闭查询页面归档";
        }

        IBossCall.BussToHisIBOSS(indictSeq, remark);

        // 更新TF_B_PLATREQ_LOG表
        IData updParam = new DataMap();
        updParam.put("INDICT_SEQ", indictSeq);
        updParam.put("FINISH_DEPART_ID", getVisit().getDepartId());
        updParam.put("FINISH_STAFF_ID", getVisit().getStaffId());
        updParam.put("REMARK", remark);

        this.UpdatePlatREQ(updParam);
    }
}
