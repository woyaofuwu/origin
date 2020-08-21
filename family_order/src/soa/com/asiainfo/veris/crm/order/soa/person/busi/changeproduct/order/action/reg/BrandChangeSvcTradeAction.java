
package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.reg;

import java.util.List;

import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.requestdata.ChangeProductReqData;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @ClassName: BrandChangeSvcTradeAction.java
 * @Description: 用户品牌转为非全球通品牌，移动秘书业务将取消 主叫隐藏业务为全球通客户特有的业务，若该套餐变更为神州行（或动感地带）套餐，主叫隐藏业务将取消
 * @version: v1.0.0
 * @author: maoke
 * @date: Jun 7, 2014 10:14:05 AM Modification History: Date Author Version Description
 *        -------------------------------------------------------* Jun 7, 2014 maoke v1.0.0 修改原因
 */
public class BrandChangeSvcTradeAction implements ITradeAction
{

    /**
     * 只有全球通，动感地带以及一部分神州行用户能办理【返回true,需要取消移动秘书】
     * 
     * @param newBrand
     * @return
     * @throws Exception
     */
    public boolean cancelMobileSeretaryTag(String newBrand, String newProductId) throws Exception
    {
        String[] productList =
        { "10000867", "10005600", "10002115", "10001310" };

        if ("G001".equals(newBrand) || "G010".equals(newBrand))
        {
            return false;
        }

        if ("G002".equals(newBrand))
        {
            for (int i = 0; i < productList.length; i++)
            {
                if (newProductId.equals(productList[i]))
                {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
        if (btd.isBrandChange())
        {
            ChangeProductReqData request = (ChangeProductReqData) btd.getRD();

            String newBrand = request.getNewMainProduct().getBrandCode();
            String newProductId = request.getNewMainProduct().getProductId();

            List<SvcTradeData> userSvcs = btd.getRD().getUca().getUserSvcs();

            if (userSvcs != null && userSvcs.size() > 0)
            {
                for (SvcTradeData userSvc : userSvcs)
                {
                    String elementId = userSvc.getElementId();
                    String modifyTag = userSvc.getModifyTag();

                    if (("31".equals(elementId) && cancelMobileSeretaryTag(newBrand, newProductId)) || (("59".equals(elementId) || "60".equals(elementId)) && !"G001".equals(newBrand)))
                    {
                        if (!BofConst.MODIFY_TAG_DEL.equals(modifyTag))
                        {
                            SvcTradeData svcTradeData = userSvc.clone();

                            svcTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL);
                            svcTradeData.setEndDate(request.getAcceptTime());// 时间注意
                            // 老代码td.getBaseCommInfo().getString("PRO_END_DATE")

                            btd.add(btd.getRD().getUca().getSerialNumber(), svcTradeData);
                        }
                    }
                }
            }
        }
    }
}
