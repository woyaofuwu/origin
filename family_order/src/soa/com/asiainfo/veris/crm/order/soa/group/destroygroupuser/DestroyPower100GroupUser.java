
package com.asiainfo.veris.crm.order.soa.group.destroygroupuser;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductCompInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.destroygroupuser.DestroyGroupUser;

public class DestroyPower100GroupUser extends DestroyGroupUser
{
    private IDataset relationUUs = null; // 动力100用户和子用户的关系

    public DestroyPower100GroupUser()
    {
    }

    /**
     * 生成登记信息
     * 
     * @author 孙翰韬
     * @throws Exception
     */
    public void actTradeBefore() throws Exception
    {

        super.actTradeBefore();

        // 获取动力100用户下的所有子用户
        getRelaData();
        // 将子产品的组合产品的服务、资费、参数注销
        infoRegDataPower100Element();
    }

    public void actTradeSub() throws Exception
    {
        super.actTradeSub();

        // 注销关系
        infoRegDataRela();

    }

    /**
     * 获取动力100用户下的所有子用户
     * 
     * @throws Exception
     */
    public void getRelaData() throws Exception
    {

        String relationTypeCode = ProductCompInfoQry.getRelaTypeCodeByProductId(reqData.getUca().getProductId()); // 关系类型
        // setDbConCode(pd, "cg");

        relationUUs = RelaUUInfoQry.getRelaUUInfoByUserIdaForGrp(reqData.getUca().getUserId(), relationTypeCode, null);
        if (IDataUtil.isEmpty(relationUUs))
        {
            CSAppException.apperr(GrpException.CRM_GRP_255);
        }
    }

    /**
     * 将子产品的组合产品级产品元素注销
     * 
     * @throws Exception
     */
    public void infoRegDataPower100Element() throws Exception
    {
        for (int i = 0; i < relationUUs.size(); i++)
        {
            IData relationUU = relationUUs.getData(i);

            IDataset elements = ProductInfoQry.qryUserProductElement(relationUU.getString("USER_ID_B"), reqData.getUca().getUserId());

            for (int j = 0, size = elements.size(); j < size; j++)
            {
                IData element = elements.getData(j);
                String instId = element.getString("INST_ID");

                element.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                element.put("END_DATE", SysDateMgr.getLastDateThisMonth());
                // element.put("END_DATE", getCancelDate(element.getString("START_DATE"),
                // element.getString("END_DATE")));
                String elementType = element.getString("ELEMENT_TYPE_CODE", "");
                // 服务
                if (elementType.equals("S"))
                {
                    // commData.getDataset("SVC").add(element);
                    reqData.cd.getSvc().add(element);
                }
                // 优惠
                else if (elementType.equals("D"))
                {
                    // commData.getDataset("DISCNT").add(element);
                    reqData.cd.getDiscnt().add(element);
                }
                // 资源
                else if (elementType.equals("R"))
                {
                    // commData.getDataset("RES").add(element);
                    reqData.cd.getRes().add(element);
                }

                IDataset params = UserAttrInfoQry.getUserAttrByUserIdInstidForGrp(relationUU.getString("USER_ID_B"), elementType, instId);

                if (IDataUtil.isEmpty(params))
                {
                    continue;
                }

                for (int k = 0; k < params.size(); k++)
                {
                    IData param = params.getData(k);
                    param.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                    param.put("END_DATE", SysDateMgr.getLastDateThisMonth());
                }

                // commData.getDataset("PARAM").addAll(params);
                reqData.cd.putElementParam(params);
            }
        }
    }

    /**
     * 关系注销
     * 
     * @throws Exception
     */
    public void infoRegDataRela() throws Exception
    {
        for (int i = 0; i < relationUUs.size(); i++)
        {
            IData relationUU = relationUUs.getData(i);
            relationUU.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
            relationUU.put("END_DATE", getAcceptTime());
        }
        this.addTradeRelation(relationUUs);
    }
}
