
package com.asiainfo.veris.crm.order.soa.group.creategroupuser;


import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.group.common.GroupBaseConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import org.apache.log4j.Logger;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.creategroupuser.CreateGroupUser;

import java.util.Iterator;


/**
 * @description 政企权益订购
 *   offerCode = 110000023413
 * @author zhengkai5
 */
public class CreateGroupRightsUser extends CreateGroupUser
{        
    private static transient Logger logger = Logger.getLogger(CreateGroupRightsUser.class);


    @Override
    public void actTradeBefore() throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>> CreateGroupRightsUser -- actTradeBefore()>>>>>>>>>>>>>>>>>>");

        super.actTradeBefore();
    }

    /*
     * @description 登记产品子表
     * @remark 集团权益产品类型为Q，不能通过P来查询产品信息
     * @author zhengkai5
     * @date 2013-05-25
     * */
    public void actTradePrdAndPrdParams() throws Exception
    {
        IData productIdset = reqData.cd.getProductIdSet();

        // 添加主产品信息
        productIdset.put(reqData.getGrpProductId(), CSBaseConst.TRADE_MODIFY_TAG.Add.getValue());

        IDataset productInfoset = new DatasetList();
        Iterator<String> iterator = productIdset.keySet().iterator();
        while (iterator.hasNext())
        {
            String productId = iterator.next();

            String productMode = UProductInfoQry.getProductModeByProductId(productId,BofConst.ELEMENT_TYPE_CODE_RIGHTS);

            if (GroupBaseConst.PRODUCT_MODE.USER_MAIN_PRODUCT.getValue().equals(productMode) || GroupBaseConst.PRODUCT_MODE.USER_PLUS_PRODUCT.getValue().equals(productMode))
            {
                IData productPlus = new DataMap();

                productPlus.put("PRODUCT_ID", productId); // 产品标识
                productPlus.put("PRODUCT_MODE", productMode); // 产品的模式

                String instId = SeqMgr.getInstId();

                productPlus.put("INST_ID", instId); // 实例标识
                productPlus.put("START_DATE", getAcceptTime()); // 开始时间
                productPlus.put("END_DATE", SysDateMgr.getTheLastTime()); // 结束时间
                productPlus.put("MODIFY_TAG", CSBaseConst.TRADE_MODIFY_TAG.Add.getValue());
                productPlus.put("USER_ID", reqData.getUca().getUser().getUserId());
                // 如果是集团产品，则需要设置
                productPlus.put("MAIN_TAG", productPlus.getString("PRODUCT_MODE").equals(GroupBaseConst.PRODUCT_MODE.USER_MAIN_PRODUCT.toString()) ? "1" : "0");// 主产品标记：0-否，1-是
                productInfoset.add(productPlus);
            }
        }

        super.addTradeProduct(productInfoset);
    }

    @Override
    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);

        reqData.setGrpProductId(map.getString("GROUP_RIGHTS_ID"));   // 政企权益ID

    }
}
