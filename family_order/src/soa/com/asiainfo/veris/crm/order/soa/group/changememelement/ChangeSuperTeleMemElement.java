
package com.asiainfo.veris.crm.order.soa.group.changememelement;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductCompInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.changememelement.ChangeMemElement;

public class ChangeSuperTeleMemElement extends ChangeMemElement
{

    public ChangeSuperTeleMemElement()
    {

    }

    /**
     * 覆盖父类的方法,保存预留字段信息
     */
    public void regTrade() throws Exception
    {
        super.regTrade();

        IData tradeData = bizData.getTrade();

        tradeData.put("RSRV_STR1", reqData.getGrpUca().getUserId());
        tradeData.put("RSRV_STR2", ProductCompInfoQry.getRelaTypeCodeByProductId(reqData.getGrpUca().getProductId()));
        tradeData.put("RSRV_STR3", reqData.getGrpUca().getSerialNumber());
        tradeData.put("RSRV_STR6", reqData.getGrpUca().getCustomer().getCustName());

        String discntCode = "";
        IDataset discntList = reqData.cd.getDiscnt();
        if (IDataUtil.isNotEmpty(discntList))
        {
            for (int i = 0; i < discntList.size(); i++)
            {
                IData discntData = discntList.getData(i);
                if (discntData.getString("MODIFY_TAG", "").equals(TRADE_MODIFY_TAG.Add.getValue()))
                {
                    discntCode = discntData.getString("DISCNT_CODE");// 只能取一个discntCode,为什么?
                    break;
                }
            }
        }
        tradeData.put("RSRV_STR8", discntCode);
        tradeData.put("RSRV_STR10", "");

        /*
         * if (isOutNet.equals("1")) { tradeData.put("PRODUCT_ID", reqData.getGrpUca().getProductId()); }
         */

        String processTagSet = "00";// 处理标志位
        /*
         * if (reqData.getUca().getBrandCode().equals("VPMN")) { processTagSet += "1"; } else { processTagSet += "0"; }
         * if (isOutNet.equals("1")) { processTagSet += "2"; } else { processTagSet += "0"; }
         */

        tradeData.put("PROCESS_TAG_SET", processTagSet);
    }

}
