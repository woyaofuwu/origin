
package com.asiainfo.veris.crm.order.soa.group.internetofthings;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.USvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcStateInfoQry;

/**
 * 物联网相关信息查询服务
 * 
 * @author xiekl
 */
public class WlwQuerySVC extends CSBizService
{

    private static final String memIotSvcId = "9014";

    /**
     * 获取用户所有服务的服务状态
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset qryUserServiceState(IData param) throws Exception
    {
        IDataset serviceStateList = new DatasetList();
        String userId = param.getString("USER_ID");

        IDataset userSvcList = UserSvcInfoQry.qryWlwUserSvcByUserId(userId);
        IDataset userSvcstateList = UserSvcStateInfoQry.getUserNowSvcStateByUserIdNow(userId, BizRoute.getRouteId());
        if (IDataUtil.isEmpty(userSvcList))
        {
        	CSAppException.apperr(CrmCommException.CRM_COMM_103,"用户未订购物联网");
        }

        for (int i = 0; i < userSvcList.size(); i++)
        {
            IData userSvc = userSvcList.getData(i);
            String serviceId = userSvc.getString("SERVICE_ID");
            
            //翻译service_name
            String serviceName = USvcInfoQry.getSvcNameBySvcId(serviceId);
            userSvc.put("SERVICE_NAME", serviceName);
            
            String userIdA = userSvc.getString("USER_ID_A");
            boolean flag = false;

            // 个人的服务 和主服务不暂停
            if (memIotSvcId.equals(serviceId) || "-1".equals(userIdA))
            {
                continue;
            }

            // 只有物联网产品的服务暂停恢复

                if (IDataUtil.isNotEmpty(userSvcstateList))
                {
                    for (int j = 0; j < userSvcstateList.size(); j++)
                    {
                        IData userSvcState = userSvcstateList.getData(j);
                        // 如果有服务状态数据则设置当前服务状态为服务状态数据中状态
                        if (serviceId.equals(userSvcState.getString("SERVICE_ID")))
                        {
                            flag = true;
                            userSvc.put("INST_ID", userSvcState.getString("INST_ID"));
                            userSvc.put("START_DATE", userSvcState.getString("START_DATE"));
                            userSvc.put("END_DATE", userSvcState.getString("END_DATE"));
                            userSvc.put("STATE_CODE", userSvcState.getString("STATE_CODE"));
                            userSvc.put("USER_ID_A", userIdA);
                        }
                    }

                    // 如果没有服务状态数据，则默认服务状态为开通
                    if (!flag)
                    {
                        userSvc.put("STATE_CODE", "0");
                    }

                    serviceStateList.add(userSvc);
                }

        }

        return serviceStateList;
    }
}
