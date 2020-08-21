
package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.pmd;

import java.util.List;

import com.ailk.biz.util.StaticUtil;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.IProductModuleAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.DiscntData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.ProductModuleTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OfferRelTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.util.ProductModuleCalDate;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @ClassName: RequiredPkgDiscntPmdAction.java
 * @Description: 必选包优惠处理,新增的时候删除老的必选优惠【select * from td_s_static t where t.type_id = 'INTF_REQUIRED_PKG'】
 * @version: v1.0.0
 * @author: maoke
 * @date: Jun 27, 2014 3:02:54 PM Modification History: Date Author Version Description
 *        -------------------------------------------------------* Jun 27, 2014 maoke v1.0.0 修改原因
 */
public class RequiredPkgDiscntPmdAction implements IProductModuleAction
{

    @Override
    public void executeProductModuleAction(ProductModuleTradeData dealPmtd, UcaData uca, BusiTradeData btd) throws Exception
    {
        if (!btd.isProductChange())
        {
            String modifyTag = dealPmtd.getModifyTag();
            String instId = dealPmtd.getInstId();
            String endDate = SysDateMgr.getLastSecond(uca.getUserDiscntByInstId(instId).getStartDate());

            if (BofConst.MODIFY_TAG_ADD.equals(modifyTag))
            {
                String packageId = "";
                String productId = "";
                String elementId = dealPmtd.getElementId();
                
                OfferRelTradeData selfRel = uca.getLastMainProductRel(dealPmtd.getInstId());
                if(selfRel != null){
                	packageId = selfRel.getGroupId();
                	productId = selfRel.getOfferCode();
                }

                List<DiscntTradeData> userList = uca.getUserDiscnts();

                if (userList != null && userList.size() > 0)
                {
                    for (DiscntTradeData userDiscnt : userList)
                    {
                    	OfferRelTradeData offerRel = uca.getLastMainProductRel(userDiscnt.getInstId());
                        String tempPackageId = "";
                        String tempProductId = "";
                        if(offerRel != null){
                        	tempPackageId = offerRel.getGroupId();
                        	tempProductId = offerRel.getOfferCode();
                        }
                        
                        String tempModifyTag = userDiscnt.getModifyTag();
                        String tempElementId = userDiscnt.getDiscntCode();

                        String staticPkg = StaticUtil.getStaticValue("INTF_REQUIRED_PKG", tempPackageId);
                        if (StringUtils.isNotBlank(staticPkg))
                        {
                            // 同产品同包产品时候
                            if (StringUtils.isNotBlank(tempPackageId) && tempPackageId.equals(packageId) && tempProductId.equals(productId))
                            {
                                if (!BofConst.MODIFY_TAG_DEL.equals(tempModifyTag) && !elementId.equals(tempElementId))
                                {
                                    DiscntTradeData discntTd = new DiscntTradeData(userDiscnt.toData());
                                    
                                    if("10004453".equals(tempPackageId))//4G自选的包根据配置计算取消结束时间
                                    {
                                    	DiscntData userDiscntElement = new DiscntData(userDiscnt.toData());
                                    	
                                    	endDate = ProductModuleCalDate.calCancelDate(userDiscntElement, btd.getProductTimeEnv());
                                    }
                                    
                                    discntTd.setEndDate(endDate);
                                    discntTd.setModifyTag(BofConst.MODIFY_TAG_DEL);

                                    btd.add(btd.getRD().getUca().getSerialNumber(), discntTd);

                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
