
package com.asiainfo.veris.crm.order.soa.group.groupTrans.action.bat;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.group.common.base.trans.ITrans;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;

/**
 * 集团注销服务转换
 * 
 * @author penghaibo
 */
public class BatDestroyGrpUserDistributeTrans implements ITrans
{

    @Override
    public void transRequestData(IData batData) throws Exception
    {
        String svcName = "";

        IData svcData = batData.getData("svcData", new DataMap());

        IData condData = batData.getData("condData", new DataMap());

        String productId = IDataUtil.chkParam(condData, "PRODUCT_ID");

        String brandCode = UProductInfoQry.getBrandCodeByProductId(productId);
        
        String receptionHallMem = condData.getString("RECEPTIONHALLMEM", "");
        
        if ("BOSG".equals(brandCode))
        {
        	if("JKDT".equals(receptionHallMem)){
        		svcData.put("ROUTE_EPARCHY_CODE", condData.getString("ROUTE_EPARCHY_CODE","0971"));//接口路由
        		svcData.put("ACTION", condData.getString("ACTION","0"));//操作类型
        		svcData.put("RECEPTIONHALLMEM", receptionHallMem);//集客大厅受理标记
            	svcName="CS.DestroyJKDTUserSVC.dealDelBBossBiz";
            }
        	else
        	{
                 svcName = "CS.DestroyBBossUserSVC.dealDelBBossBiz";
        	}
        }
        else if ("6100".equals(productId))
        {
            svcName = "SS.DestroySuperTeleGroupUserSVC.crtOrder";
        }
        else if ("6130".equals(productId))
        {
            svcName = "SS.DestroyCentrexSuperTeleGroupUserSVC.crtOrder";
        }
        
        else
        {
            svcName = "CS.DestroyGroupUserSvc.destroyGroupUser";
        }

        svcData.put("REAL_SVC_NAME", svcName);
    }

}
