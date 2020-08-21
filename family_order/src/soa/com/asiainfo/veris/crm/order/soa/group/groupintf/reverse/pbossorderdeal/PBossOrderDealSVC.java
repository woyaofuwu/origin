package com.asiainfo.veris.crm.order.soa.group.groupintf.reverse.pbossorderdeal;

import com.ailk.bizservice.base.CSAppCall;
import com.ailk.bizservice.base.CSBizService;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;

public class PBossOrderDealSVC extends CSBizService
{ 
    private static final long serialVersionUID = 1L;

    /*
     * @description pboss瞬时报文处理
     * @author liuzz
     * @date 2016-08-23
     */
    public static IDataset dealPbossOrder(IData map) throws Exception
    {
        IDataset result = new DatasetList(); 
        
        System.out.println("<<<<<<<开始调用车联网反向业务>>>>>>>>");
        System.out.println("<<<<<<<start_wangyf>>>>>>>>");
        System.out.println("<<<<<<<map>>>>>>>>" + map);
        
        IDataset ecBizInfos = IDataUtil.chkParamDataset(map, "EC_BIZ_INFO");
        for (int i = 0; i < ecBizInfos.size(); i++)
        {//循环获取每个客户的订单对象
            IData ecBiz = ecBizInfos.getData(i);
            String custId = IDataUtil.chkParam(ecBiz, "CUSTOMER_NUMBER"); 
            
            IDataset bizInfos = IDataUtil.chkParamDataset(ecBiz, "BIZ_INFO");
            System.out.println("bizInfos=" + bizInfos);
            
            for (int j = 0; j < bizInfos.size(); j++)
            {//循环获取每个客户的每组业务数据
                IData reqData = new DataMap();
                System.out.println("bizInfos.getData(j)=" + bizInfos.getData(j));
                reqData = PBossOrderDeal.dealPbossOrder(custId, bizInfos.getData(j));
                System.out.println("<<<<<<<reqData>>>>>>>>" + reqData);
                String operCode = bizInfos.getData(j).getString("OPR_CODE");
                if ("01".equals(operCode))
                {//订购业务
                    result = CSAppCall.call("CS.CreateGroupUserSvc.createGroupUser", reqData);
                }
                else if ("02".equals(operCode))
                {//终止业务
                    result = CSAppCall.call("CS.DestroyGroupUserSvc.destroyGroupUser", reqData);
                }
                else if ("03".equals(operCode))
                {//变更业务
                    result = CSAppCall.call("CS.ChangeUserElementSvc.changeUserElement", reqData);
                	//result = CSAppCall.call("CS.ChangeUserElementForWlwSvc.changeUserElement", reqData);
                }
            }
        }
        
        System.out.println("<<<<<<<end_wangyf>>>>>>>>" + result);
        
        return result;
    }
}
