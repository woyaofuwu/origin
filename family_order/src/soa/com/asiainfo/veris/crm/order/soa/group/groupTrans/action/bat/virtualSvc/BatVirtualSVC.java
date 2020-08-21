
package com.asiainfo.veris.crm.order.soa.group.groupTrans.action.bat.virtualSvc;

import org.apache.log4j.Logger;

import com.ailk.biz.client.BizServiceFactory;
import com.ailk.biz.data.ServiceResponse;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.BatException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderService;

public class BatVirtualSVC extends GroupOrderService
{
    private static final long serialVersionUID = 2720278928941132346L;

    private static transient Logger log = Logger.getLogger(BatVirtualSVC.class);

    /**
     * trans 批量虚拟服务入口
     * 
     * @param batData
     * @return
     * @throws Exception
     */
    public IDataset batExec(IData batData) throws Exception
    {
        String svcName = batData.getString("REAL_SVC_NAME");

        // 调用服务
        if (StringUtils.isEmpty(svcName))
        {
            CSAppException.apperr(BatException.CRM_BAT_51);
        }

        if (IDataUtil.isEmpty(batData))
        {
            CSAppException.apperr(BatException.CRM_BAT_52);
        }

        // 虚拟服务调真实服务的时候 数据已经转换 确认不需要再trans转换了
        batData.put("IS_NEED_TRANS", false);

        if (log.isDebugEnabled())
        {
            log.debug("//////////////////////////////////////////虚拟服务批量接口调用 Call真实的服务 开始//////////////////////////////////////");
            log.debug("服务名：svcName = " + svcName + "服务参数：svcData = " + batData);
        }
        IDataset dataset =new DatasetList();
        if(svcName.startsWith("CCF.")){
        	  ServiceResponse response = BizServiceFactory.call(svcName, batData, null);
              IData out = response.getBody();
              out.put("ORDER_ID", SeqMgr.getOrderId());
              dataset.add(out);
        }else{
        	 dataset = CSAppCall.call(svcName, batData);
        }
        

        if (log.isDebugEnabled())
        {
            log.debug("服务调用返回dataset : " + dataset);
            log.debug("//////////////////////////////////////////虚拟服务批量接口调用 Call真实的服务 结束//////////////////////////////////////");
        }

        return dataset;
    }
}
