
package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.pmd;

import java.util.Iterator;
import java.util.List;

import com.asiainfo.veris.crm.order.pub.consts.PersonConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.IProductModuleAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.ProductModuleTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @ClassName: GlobeRomingServicePmdAction.java
 * @Description: 国际漫游服务处理
 * @version: v1.0.0
 * @author: maoke
 * @date: Jun 18, 2014 2:46:31 PM Modification History: Date Author Version Description
 *        -------------------------------------------------------* Jun 18, 2014 maoke v1.0.0 修改原因
 */
public class GlobeRomingServicePmdAction implements IProductModuleAction
{

    @Override
    public void executeProductModuleAction(ProductModuleTradeData dealPmtd, UcaData uca, BusiTradeData btd) throws Exception
    {
        if (!btd.isProductChange())
        {
            String modifyTag = dealPmtd.getModifyTag();
            String instId = dealPmtd.getInstId();

            if (BofConst.MODIFY_TAG_ADD.equals(modifyTag))
            {
                this.openGlobeRomingSvc(instId, uca, btd);
            }
        }

    }

    /**
     * @Description: 开通国际漫游处理
     * @param uca
     * @param btd
     * @throws Exception
     * @author: maoke
     * @date: Jun 18, 2014 2:49:51 PM
     */
    public void openGlobeRomingSvc(String instId, UcaData uca, BusiTradeData btd) throws Exception
    {
        /**
         * 简单业务说明： 这个接口是单独开通国际漫游，前提是已经开通国际长途 1、国际漫游依赖国际长途服务，若国际长途未开通，则不能办理业务； 2、国际漫游和国内漫游互斥，若存在国内漫游，则取消；
         * 3、国内长途和国际长途互斥，若存在国内长途也取消；
         */
        
        String serialNumber = uca.getSerialNumber();

        // 判断国际漫游是否添加
        SvcTradeData globeRomingSvc = uca.getUserSvcByInstId(instId);

        // 终止时间为新添加元素前一秒
        String endDate = SysDateMgr.getLastSecond(globeRomingSvc.getStartDate());

        if (globeRomingSvc != null)
        {
            // 为本次添加
            if (BofConst.MODIFY_TAG_ADD.equals(globeRomingSvc.getModifyTag()))
            {
                // 终止生效国内长途和国内漫游
                List<SvcTradeData> homeSvcs = uca.getUserSvcsBySvcIdArray(PersonConst.SERVICE_ID_14 + "," + PersonConst.SERVICE_ID_18); 
                
                if (homeSvcs != null && homeSvcs.size() > 0)
                {
                    for (SvcTradeData homeSvc : homeSvcs)
                    {   
                        String modifyTag = homeSvc.getModifyTag();

                        if (!BofConst.MODIFY_TAG_DEL.equals(modifyTag))
                        {
                        	  //台账中已经有删除的，不需要构造删除了，会导致删除台账重复
                            if (isExistsDelService(btd,homeSvc.getElementId()))
                            {
                                continue;
                            }
                            //本次台账中添加的则不能删除
                             if (isExistsAddService(btd,homeSvc.getElementId()))
                            {
                                continue;
                            }
                            SvcTradeData svcTd = new SvcTradeData(homeSvc.toData());

                            svcTd.setModifyTag(BofConst.MODIFY_TAG_DEL);
                            svcTd.setEndDate(endDate);

                            btd.add(serialNumber, svcTd);
                        }
                    }
                }
            }
        }
    }
    
    
    public static boolean isExistsDelService( BusiTradeData btd,String serviceId)
    {
    
        List list = btd.getTradeDatas(TradeTableEnum.TRADE_SVC);
        
        for (Iterator iterator = list.iterator(); iterator.hasNext();)
        {
            SvcTradeData item = (SvcTradeData) iterator.next();
            
            if (null !=serviceId && serviceId.equals(item.getElementId()) && BofConst.MODIFY_TAG_DEL.equals(item.getModifyTag()))
            {
                return Boolean.TRUE;
            }
            
        }
        return Boolean.FALSE;
    }
    
      public static boolean isExistsAddService( BusiTradeData btd,String serviceId)
    {
    
        List list = btd.getTradeDatas(TradeTableEnum.TRADE_SVC);
        
        for (Iterator iterator = list.iterator(); iterator.hasNext();)
        {
            SvcTradeData item = (SvcTradeData) iterator.next();
            
            if (null !=serviceId && serviceId.equals(item.getElementId()) && BofConst.MODIFY_TAG_ADD.equals(item.getModifyTag()))
            {
                return Boolean.TRUE;
            }
            
        }
        return Boolean.FALSE;
    }
}
