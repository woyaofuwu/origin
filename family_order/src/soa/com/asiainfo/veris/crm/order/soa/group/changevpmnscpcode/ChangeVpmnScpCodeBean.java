
package com.asiainfo.veris.crm.order.soa.group.changevpmnscpcode;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderBaseBean;

public class ChangeVpmnScpCodeBean extends GroupOrderBaseBean
{

    /**
     * 构造函数
     */
    public ChangeVpmnScpCodeBean()
    {

    }

    @Override
    public void actOrderDataOther(IData map) throws Exception
    {
        IData data = new DataMap();
        data.put("USER_ID", map.getString("USER_ID"));
        data.put("SCP_CODE", map.getString("NEW_SCP_CODE"));
        Dao.save("TF_F_USER_VPN", data, new String[]
        { "USER_ID" }); // j2ee: 原逻辑就是直接更改资料表，不动
        IDataset relas = RelaUUInfoQry.getRelaUUInfoByUserIdBAndRelaTypeCode(map.getString("USER_ID"), "20");

        if (IDataUtil.isNotEmpty(relas))
        {
            // 成员 批量发指令
            IData param = new DataMap();
            param.put("USER_ID", map.getString("USER_ID")); // 集团user_ID
            param.put("PRODUCT_ID", map.getString("PRODUCT_ID")); // 8000
            param.put("NEW_SCP_CODE", map.getString("NEW_SCP_CODE"));
            param.put("OLD_SCP_CODE", map.getString("OLD_SCP_CODE"));
            for (int i = 0; i < relas.size(); i++)
            {
                IData rela = relas.getData(i);

                param.put("SERIAL_NUMBER", rela.getString("SERIAL_NUMBER_B")); // 成员

                if (StringUtils.isNotBlank(param.getString("SERIAL_NUMBER")))
                {
                    IDataset ds = CSAppCall.call("SS.ChangeVPMNScpCode.crtTrade", param);
                }
            }
        }
    }

    @Override
    protected String setOrderTypeCode() throws Exception
    {
        return "3153"; // 集团VPMN修改SCP的业务类型编码【3153】
    }

}
