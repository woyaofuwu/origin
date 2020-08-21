package com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.verb;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupBaseReqData;

public class GDelSubScribeOfferVerb extends GVerb
{
    private String userId;

    private String userIdA;

    private String expireDate;
    
    private boolean isAutoDelMainOfferCha = true;

    public GDelSubScribeOfferVerb() throws Exception
    {
        super();
    }

    public GDelSubScribeOfferVerb(String userId, String userIdA) throws Exception
    {
        super();
        this.userId = userId;
        this.userIdA = userIdA;
    }

    public GDelSubScribeOfferVerb(String userId) throws Exception
    {
        super();
        this.userId = userId;
    }

    public void setExpireDate(String expireDate)
    {
        this.expireDate = expireDate;
    }
    
    public boolean isAutoDelMainOfferCha()
    {
        return isAutoDelMainOfferCha;
    }

    public void setAutoDelMainOfferCha(boolean isAutoDelMainOfferCha)
    {
        this.isAutoDelMainOfferCha = isAutoDelMainOfferCha;
    }

    public IData run(GroupBaseReqData reqData) throws Exception
    {

        if (StringUtils.isEmpty(userId))
            return null;

        // 全局失效时间
        String gloabExpireDate = expireDate;
        if (StringUtils.isEmpty(gloabExpireDate))
            gloabExpireDate = reqData.getAcceptTime();

        // 查询用户产品表生成销售品信息
        IDataset userProductList =  UserProductInfoQry.getProductInfo(userId, userIdA);

        IData offerEntity = new DataMap();
        if (IDataUtil.isNotEmpty(userProductList))
        {
            for (int i = 0, size = userProductList.size(); i < size; i++)
            {
                IData oe = userProductList.getData(i);
                oe.put("OFFER_TYPE", BofConst.ELEMENT_TYPE_CODE_PRODUCT);
                GDelOfferVerb dpov = new GDelOfferVerb(oe);
                dpov.setUserIdA(userIdA);
                if (!isAutoDelMainOfferCha())
                {
                    dpov.setAutoDelMainOfferCha(isAutoDelMainOfferCha);
                }
                offerEntity = dpov.run(reqData);
            }
        }

        // 查询服务表生产销售品信息
        IDataset userSvcList = UserSvcInfoQry.getUserProductSvc(userId, userIdA, null);

        if (IDataUtil.isNotEmpty(userSvcList))
        {
            for (int i = 0, size = userSvcList.size(); i < size; i++)
            {
                IData oe = userSvcList.getData(i);
                oe.put("OFFER_TYPE", BofConst.ELEMENT_TYPE_CODE_SVC);
                GDelOfferVerb dsov = new GDelOfferVerb(oe);
                dsov.setUserIdA(userIdA);
                dsov.run(reqData);

            }
        }

        // 查询用户资费信息
        IDataset userDiscntList = UserDiscntInfoQry.getUserProductDis(userId, userIdA);

        for (int i = 0, size = userDiscntList.size(); i < size; i++)
        {
            IData oe = userDiscntList.getData(i);
            oe.put("OFFER_TYPE", BofConst.ELEMENT_TYPE_CODE_DISCNT);
            GDelOfferVerb ddov = new GDelOfferVerb(oe);
            ddov.setUserIdA(userIdA);
            ddov.run(reqData);
        }
        
        // 查询用户资源信息
        IDataset userResList = UserResInfoQry.getUserProductRes(userId, userIdA, null);

        for (int i = 0, size = userResList.size(); i < size; i++)
        {
            IData oe = userResList.getData(i);
            oe.put("OFFER_TYPE", BofConst.ELEMENT_TYPE_CODE_RES);
            GDelOfferVerb drov = new GDelOfferVerb(oe);
            drov.setAutoDelUpOfferRel(false);// 没有绑定关系可以删
            drov.setUserIdA(userIdA);
            drov.run(reqData);
        }

        return offerEntity;
    }
}
