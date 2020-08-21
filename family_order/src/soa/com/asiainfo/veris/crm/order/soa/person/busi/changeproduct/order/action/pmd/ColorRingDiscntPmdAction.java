
package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.pmd;

import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.consts.PersonConst;
import com.asiainfo.veris.crm.order.pub.exception.UserDiscntException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.IProductModuleAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.ProductModuleTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.DiscntInfoQry;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @ClassName: ColorRingDiscntPmdAction.java
 * @Description: 彩铃套餐优惠【TD_S_DISCNT_TYPE=Q】
 * @version: v1.0.0
 * @author: maoke
 * @date: Jun 12, 2014 7:34:20 PM Modification History: Date Author Version Description
 *        -------------------------------------------------------* Jun 12, 2014 maoke v1.0.0 修改原因
 */
public class ColorRingDiscntPmdAction implements IProductModuleAction
{
    @Override
    public void executeProductModuleAction(ProductModuleTradeData dealPmtd, UcaData uca, BusiTradeData btd) throws Exception
    {
        if (!btd.isProductChange())
        {
            String modifyTag = dealPmtd.getModifyTag();
            String elementId = dealPmtd.getElementId();

            String discntElementType = UDiscntInfoQry.getDiscntTypeByDiscntCode(elementId);

            if (StringUtils.isNotBlank(discntElementType) && (PersonConst.DISCNT_TYPE_Q.equals(discntElementType)))
            {
                if (BofConst.MODIFY_TAG_ADD.equals(modifyTag))
                {

                }
                if (BofConst.MODIFY_TAG_DEL.equals(modifyTag))
                {
                    String discntName = UDiscntInfoQry.getDiscntNameByDiscntCode(elementId);

                    CSAppException.apperr(UserDiscntException.CRM_USER_DISCNT_6, discntName);
                }
            }
        }
    }
}
