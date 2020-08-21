
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.schoolwidenettask;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

public class SchoolWidenetTaskSVC extends CSBizService
{

    /**
     * 校园宽带服务开通接口表捞取数据
     * 
     * @author chenzm
     * @param input
     * @throws Exception
     */

    public IData dealTradeInterface(IData input) throws Exception
    {

        IData result = SchoolWidenetTaskBean.CreateStringMain(input);
        IData result2 = new DataMap();
        if ("0".equals(result.getString("X_RESULTCODE")))
        {
            IDataset commparaInfos = CommparaInfoQry.getCommpara("CSM", "89", input.getString("TRADE_TYPE_CODE"), CSBizBean.getTradeEparchyCode());
            if (IDataUtil.isNotEmpty(commparaInfos))
            {
                String flag = commparaInfos.getData(0).getString("PARA_CODE1");
                if ("1".equals(flag))
                {
                    String strServe2 = result.getString("SERVE_STRING2");
                    // 强制离线
                    result2 = SchoolWidenetTaskBean.sendHttpClient(strServe2);

                }
            }

            String strServe = result.getString("SERVE_STRING");
            // 3、调城市热点接口开通服务
            result = SchoolWidenetTaskBean.sendHttpClient(strServe);
            result.put("X_RESULTINFO", result.getString("X_RESULTINFO") + "||强制离线：" + result2.getString("X_RESULTINFO"));
        }

        return result;
    }

}
