
package com.asiainfo.veris.crm.order.soa.group.internetofthings;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ParamInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcStateInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupBaseReqData;

public class IotGprsPondUtil extends CSBizBean
{

    /**
     * 处理 TF_B_TRADE_SVCSTATE 的数据
     */
    public static IDataset actTradeSvcState(GroupBaseReqData reqData) throws Exception
    {
        IDataset svcstateSet = new DatasetList();

        IDataset svcDatas = reqData.cd.getSvc();
        for (int row = svcDatas.size() - 1; row >= 0; row--)
        {
            IData map = svcDatas.getData(row);

            if ("99010012".equals(map.getString("ELEMENT_ID")) || "99010013".equals(map.getString("ELEMENT_ID")))
            {
                if ("0".equals(map.getString("MODIFY_TAG")))
                {
                    IData state = new DataMap();
                    state.put("USER_ID", reqData.getUca().getUserId());
                    state.put("SERVICE_ID", map.getString("ELEMENT_ID"));
                    state.put("INST_ID", SeqMgr.getInstId());
                    state.put("MAIN_TAG", "0");

                    if ("1".equals(map.getString("IS_NEED_PF")))
                    {
                        state.put("STATE_CODE", "0"); // 流量池状态: 0-开启; E-关闭;1-取消 。 跟原来登记在TF_B_GPRSPOND的时候不同
                    }
                    else
                    {
                        state.put("STATE_CODE", "E");
                    }
                    state.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                    state.put("START_DATE", SysDateMgr.getSysTime());
                    state.put("END_DATE", SysDateMgr.getTheLastTime());
                    svcstateSet.add(state);
                }
                else if ("1".equals(map.getString("MODIFY_TAG")))
                {
                    String userId = reqData.getUca().getUser().getUserId();
                    IDataset results = UserSvcStateInfoQry.getUserLastStateByUserSvc(userId, map.getString("ELEMENT_ID"));

                    for (int i = 0; i < results.size(); i++)
                    {
                        IData stateDel = results.getData(i);

                        stateDel.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                        stateDel.put("END_DATE", SysDateMgr.getSysTime());

                        IData stateAdd = new DataMap();
                        stateAdd.putAll(map);
                        stateAdd.put("STATE_CODE", "1");// 销户
                        stateAdd.put("START_DATE", SysDateMgr.getSysTime());
                        stateAdd.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                        stateAdd.put("END_DATE", SysDateMgr.getTheLastTime());
                        stateAdd.put("INST_ID", SeqMgr.getInstId());
                        svcstateSet.add(stateDel);
                        svcstateSet.add(stateAdd);
                    }
                }
            }
        }
        return svcstateSet;
    }

    /*
     * 查询 成员数量
     */
    public static int getMemNumAllCrm(String userId) throws Exception
    {
        IDataset uu = RelaUUInfoQry.getNormalMemNumAllCrm(userId);
        int memNum = 0;
        for (int i = 0; i < uu.size(); i++)
        {
            memNum += uu.getData(i).getInt("MEM_NUM"); // 计算各库总成员数
        }
        return memNum;
    }

    /**
     * @Description: 查询物联网服务状态
     * @return: 0或者 1。不是0状态时，返回1.
     */
    public static String getSvcState(String userId, String serviceId) throws Exception
    {
        IDataset results = UserSvcStateInfoQry.getUserLastStateByUserSvc(userId, serviceId);

        for (int i = 0; i < results.size(); i++)
        {
            IData map = results.getData(i);
            if ("0".equals(map.getString("STATE_CODE")))
            {
                return "0";
            }
        }
        return "1";
    }

    /*
     * 查询 流量池生效人次配置
     */
    public static int getValidNum() throws Exception
    {
        IDataset pondPara = ParamInfoQry.getCommparaByCode("CSM", "9016", "gprsopond", null);
        if (IDataUtil.isEmpty(pondPara))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "请配置流量池生效人次.");
        }
        return pondPara.getData(0).getInt("PARA_CODE1");
    }

}
