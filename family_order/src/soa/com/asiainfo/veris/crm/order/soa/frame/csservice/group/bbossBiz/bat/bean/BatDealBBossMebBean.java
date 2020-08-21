
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.bat.bean;

import com.ailk.biz.BizVisit;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.service.session.SessionManager;
import com.asiainfo.veris.crm.order.pub.exception.BatException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.BatBaseBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.bat.BatTradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.bat.unit.BatDealBBossUnit;

/**
 * BBOSS成员新增、暂停、恢复、变更、注销批量
 * 
 * @author fanti3
 */

public class BatDealBBossMebBean extends BatBaseBean
{           
    /**
     * BBOSS成员批量处理start
     * 
     * @param batchID
     * @param batchOperType
     * @throws Exception
     */
    public void startBatDealBBossMember(String batchId, String batchOperType) throws Exception
    {
        //1- 查询批量信息是否存在
        IData inparam = new DataMap();
        inparam.put("BATCH_ID", batchId);
        inparam.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(batchId));
        IDataset batchdata = BatTradeInfoQry.queryBatch(batchId,null);
        if (IDataUtil.isEmpty(batchdata))
        {
            CSAppException.apperr(BatException.CRM_BAT_35);
        }
        
        //2- 更新表状态
        IData batParam = new DataMap();
        batParam.put("BATCH_ID", batchId);
        batParam.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(batchId));
        batParam.put("DEAL_STATE", "1");
        batParam.put("BATCH_ID", batchId);
        batParam.put("ACTIVE_FLAG", "1");
        batParam.put("ACTIVE_TIME", BatDealBBossUnit.getBatDealDate());
        Dao.save("TF_B_TRADE_BAT", batParam, new String[] { "BATCH_ID" },Route.getJourDb(Route.CONN_CRM_CG));
        
        //3- 多线程处理成员明细信息
        inparam.clear();
        inparam.put("BATCH_OPER_TYPE", batchOperType);
        inparam.put("BATCH_ID", batchId);
       /*2017-07-05  跟周麟确认用多线程没什么用  
        StartBBossBatThread pro = new StartBBossBatThread(inparam, (BizVisit)CSBizBean.getVisit(),SessionManager.getInstance().peek()) {};        
        pro.start();
        */
	    //2- 调用服务处理
	    CSAppCall.call("CS.BatDealBBossMebSubSVC.dealBBossMebSub", inparam);

    }

}
