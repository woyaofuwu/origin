
package com.asiainfo.veris.crm.order.soa.group.destroygroupuser;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQrySVC;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.destroygroupuser.DestroyGroupUser;

public class DestroyQqzxGroupUser extends DestroyGroupUser
{
    public DestroyQqzxGroupUser()
    {

    }

    public void actTradeUser() throws Exception
    {
        String userId = reqData.getUca().getUserId();
        IData inparam = new DataMap();
        inparam.put("USER_ID", userId);
        inparam.put("REMOVE_TAG", "0");

        IDataset userData = UserInfoQrySVC.getGrpUserInfoByUserId(inparam);

        if (null != userData && userData.size() > 0)
        {
            IData userExtenData = userData.getData(0);

            userExtenData.put("REMOVE_TAG", "2");
            userExtenData.put("USER_STATE_CODESET", "6");
            userExtenData.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
        }
        super.actTradeUser();
    }

    public void setRegAfterData() throws Exception
    {
        super.actTradeSub();
        delSvcState();
    }

    public void setRegBeforeData() throws Exception
    {
        super.actTradeBefore();
    }

    /**
     * 删除服务状态
     * 
     * @throws Exception
     */
    public void delSvcState() throws Exception
    {
        IDataset result = new DatasetList();
        String userId = reqData.getUca().getUserId();
        IDataset srvData = reqData.cd.getSvc();

        for (int j = 0; j < srvData.size(); j++)
        {
            IData svc = srvData.getData(j);
            String serviceId = svc.getString("SERVICE_ID");
            IDataset results = UserSvcInfoQry.getUserSvcStateByUserId(userId, serviceId);

            for (int i = 0; i < results.size(); i++)
            {
                IData map = results.getData(i);
                map.put("STATE", "DEL");
                map.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                map.put("END_DATE", SysDateMgr.getTheLastTime());

                IData map1 = new DataMap();
                map1.putAll(map);

                String maintag = map1.getString("MAIN_TAG", "0");

                if ("1".equals(maintag))
                {
                    map1.put("STATE_CODE", "6");// 销户
                }
                else
                {
                    map1.put("STATE_CODE", "1");// 销户
                }
                map1.put("START_DATE", getAcceptTime());
                map1.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                map1.put("END_DATE", SysDateMgr.getTheLastTime());

                result.add(map);
                result.add(map1);
            }
        }

    }
}
