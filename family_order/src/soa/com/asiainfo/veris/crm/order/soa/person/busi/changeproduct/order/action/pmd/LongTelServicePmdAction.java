
package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.pmd;

import java.util.Iterator;
import java.util.List;

import com.ailk.biz.bean.BizBean;
import com.asiainfo.veris.crm.order.pub.consts.PersonConst;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.IProductModuleAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.ProductModuleTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.ChangeProductActionUtils;
import com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.requestdata.ChangeProductReqData;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @ClassName: LongTelServicePmdAction.java
 * @Description: 国际长途服务相应处理
 * @version: v1.0.0
 * @author: maoke
 * @date: Jun 17, 2014 11:25:34 AM Modification History: Date Author Version Description
 *        -------------------------------------------------------* Jun 17, 2014 maoke v1.0.0 修改原因
 */
public class LongTelServicePmdAction implements IProductModuleAction
{

    /**
     * @Description: 1、取消国际长途时，若存在国际漫游，则取消； 2、若不存在国内长途、漫游，则开通
     * @param uca
     * @param btd
     * @throws Exception
     * @author: maoke
     * @date: Jun 17, 2014 11:35:54 AM
     */
    public void deleteGlobeLongSvc(ProductModuleTradeData dealPmtd, UcaData uca, BusiTradeData btd) throws Exception
    {
        // 1.判断是否存在国际长途服务,存在则取消 15
        // 2.判断是否存在国际漫游服务,存在则取消 19
        // 3.判断是否存在国内长途服务,不存在则开通 14
        // 4.判断是否存在国内漫游服务,不存在则开通 18
        
        
        String serialNumber = btd.getRD().getUca().getSerialNumber();

        
        // 国际及港澳台长途 调用此类时已经取消,不再处理

        // 取消国际漫游
        List<SvcTradeData> globeRomingSvc = uca.getUserSvcBySvcId(PersonConst.SERVICE_ID_19);

        if (globeRomingSvc != null && globeRomingSvc.size() > 0)
        {
            for (SvcTradeData RomingSvc : globeRomingSvc)
            {
                String modifyTag = RomingSvc.getMainTag();

                if (!BofConst.MODIFY_TAG_DEL.equals(modifyTag) && !GlobeRomingServicePmdAction.isExistsDelService(btd,RomingSvc.getElementId()))
                {   
                    SvcTradeData svcTd = new SvcTradeData(RomingSvc.toData());

                    svcTd.setModifyTag(BofConst.MODIFY_TAG_DEL);
                    svcTd.setEndDate(uca.getUserSvcByInstId(dealPmtd.getInstId()).getEndDate());

                    btd.add(serialNumber, svcTd);
                }
            }
        }
 
        // 默认添加国内长途
        List<SvcTradeData> homeLongSvc = uca.getUserSvcBySvcId(PersonConst.SERVICE_ID_14);
        if (homeLongSvc.isEmpty() && !isExistsAddService(btd,PersonConst.SERVICE_ID_14))
        { 
            ChangeProductActionUtils.addSvcTradeByServiceId(PersonConst.SERVICE_ID_14, uca, btd);
        }

        // 默认添加国内漫游
        List<SvcTradeData> homeRomingSvc = uca.getUserSvcBySvcId(PersonConst.SERVICE_ID_18);
        if (homeRomingSvc.isEmpty() && !isExistsAddService(btd,PersonConst.SERVICE_ID_18))
        {
            ChangeProductActionUtils.addSvcTradeByServiceId(PersonConst.SERVICE_ID_18, uca, btd);
        } 
    }

    @Override
    public void executeProductModuleAction(ProductModuleTradeData dealPmtd, UcaData uca, BusiTradeData btd) throws Exception
    {
        if (!btd.isProductChange())
        {
            String modifyTag = dealPmtd.getModifyTag();

            if (BofConst.MODIFY_TAG_ADD.equals(modifyTag))
            {
                openGlobeLongSvc(dealPmtd, uca, btd);
            }
            if (BofConst.MODIFY_TAG_DEL.equals(modifyTag))
            {
                deleteGlobeLongSvc(dealPmtd, uca, btd);
            }
        }
    }

    /**
     * @Description: 开通国际长途处理【费用处理单独ACTION】
     * @param uca
     * @param btd
     * @throws Exception
     * @author: maoke
     * @date: Jun 17, 2014 8:26:54 PM
     */
    public void openGlobeLongSvc(ProductModuleTradeData dealPmtd, UcaData uca, BusiTradeData btd) throws Exception
    {
        String serialNumber = uca.getSerialNumber();
      

        // 开通国际长途时，如果用户要求同时开通国际漫游，则RSRV_STR1字段填国际漫游,否则为空
        ChangeProductReqData request = (ChangeProductReqData) btd.getRD();

        if (PersonConst.SERVICE_ID_19.equals(request.getOptionParam1()))
        {
            // 添加国际漫游
            List<SvcTradeData> globeRomingSvcs = uca.getUserSvcBySvcId(PersonConst.SERVICE_ID_19);
            if (globeRomingSvcs.isEmpty())
            {
                ChangeProductActionUtils.addSvcTradeByServiceId(PersonConst.SERVICE_ID_19, uca, btd);

                // 添加国际漫游同时如有国内漫游则取消国内漫游
                List<SvcTradeData> homeRoams = uca.getUserSvcBySvcId(PersonConst.SERVICE_ID_18);

                if (homeRoams != null && homeRoams.size() > 0)
                {
                    for (SvcTradeData homeroam : homeRoams)
                    {
                        String modifyTag = homeroam.getModifyTag();

                        if (!BofConst.MODIFY_TAG_DEL.equals(modifyTag) && !GlobeRomingServicePmdAction.isExistsDelService(btd,homeroam.getElementId()) )
                        {
                            SvcTradeData svcTd = homeroam.clone();

                            svcTd.setModifyTag(BofConst.MODIFY_TAG_DEL);
                            svcTd.setEndDate(btd.getRD().getAcceptTime());

                            btd.add(serialNumber, svcTd);
                        }
                    }
                }
            }
        }

        // 终止生效国内长途 如存在
        List<SvcTradeData> homeSvcs = uca.getUserSvcBySvcId(PersonConst.SERVICE_ID_14);

        if (homeSvcs != null && homeSvcs.size() > 0)
        {
            for (SvcTradeData homeSvc : homeSvcs)
            {
                String modifyTag = homeSvc.getModifyTag();

                if (!BofConst.MODIFY_TAG_DEL.equals(modifyTag) && !GlobeRomingServicePmdAction.isExistsDelService(btd,homeSvc.getElementId()))
                {
                    SvcTradeData svcTd = homeSvc.clone();

                    svcTd.setModifyTag(BofConst.MODIFY_TAG_DEL);
                    svcTd.setEndDate(btd.getRD().getAcceptTime());

                    btd.add(serialNumber, svcTd);
                }
            }
        }
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
