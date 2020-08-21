
package com.asiainfo.veris.crm.order.soa.group.saleactive;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleActiveInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupBean;

public class SaleActiveStopBean extends GroupBean
{
    protected SaleActiveBeanReqData reqData = null;

    /**
     * 生成其它台帐数据（生成台帐后）
     * 
     * @author sungq3
     * @throws Exception
     */
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();

        // 处理优惠
        decodeSaleDiscnts();

        // 终止营销活动
        decodeSaleActives();
    }

    protected void decodeSaleDiscnts() throws Exception
    {
        IDataset discnts = reqData.getSaleDiscnts();
        for (int i = 0, size = discnts.size(); i < size; i++)
        {
            IData discnt = discnts.getData(i);
            String user_id = discnt.getString("USER_ID");

            IData param = new DataMap();
            param.put("USER_ID", user_id);
            // param.put("PRODUCT_ID", discnt.getString("PRODUCT_ID"));
            // param.put("PACKAGE_ID", discnt.getString("PACKAGE_ID"));
            param.put("DISCNT_CODE", discnt.getString("DISCNT_CODE"));
            IDataset userDiscnts = CSAppCall.call("CS.UserDiscntInfoQrySVC.getUserProdDisByUserIdProdIdPkgIdDisIdEndDate", param);
            if (IDataUtil.isNotEmpty(userDiscnts))
            {
                IData userDiscnt = userDiscnts.getData(0);
                String instId = userDiscnt.getString("INST_ID");
                String endDate = discnt.getString("END_DATE");
                if (StringUtils.isNotEmpty(instId) && StringUtils.isNotEmpty(endDate))
                {
                    IData inparam = new DataMap();
                    inparam.put("USER_ID", user_id);
                    inparam.put("INST_TYPE", "D");
                    inparam.put("RELA_INST_ID", instId);
                    inparam.put(Route.ROUTE_EPARCHY_CODE, Route.CONN_CRM_CG);
                    IDataset userAttrs = CSAppCall.call("CS.UserAttrInfoQrySVC.getUserAttrByUserIdInstid", inparam);
                    for (int j = 0, len = userAttrs.size(); j < len; j++)
                    {
                        IData userAttr = userAttrs.getData(j);

                        userAttr.put("TRADE_ID", getTradeId());
                        userAttr.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(getTradeId()));
                        userAttr.put("MODIFY_TAG", "1"); // 1-删除
                        userAttr.put("END_DATE", endDate);
                        super.addTradeAttr(userAttr);
                        // paramDataset.add(userAttr);
                    }

                    userDiscnt.put("MODIFY_TAG", "1"); // 1-删除
                    userDiscnt.put("END_DATE", endDate);
                    userDiscnt.put("TRADE_ID", getTradeId());
                    userDiscnt.put("ACCEPT_DATE", reqData.getAcceptTime()); // 受理时间
                    userDiscnt.put("UPDATE_TIME", SysDateMgr.getSysDate());
                    userDiscnt.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
                    userDiscnt.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
                    userDiscnt.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
                    userDiscnt.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
                    userDiscnt.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());

                    super.addTradeDiscnt(userDiscnt);
                }
            }
        }
    }

    protected void decodeSaleActives() throws Exception
    {
        IData saleActive = reqData.getSaleActive();
        String user_id = saleActive.getString("USER_ID");
        String campn_id = saleActive.getString("CAMPN_ID");
        String relation_trade_id = saleActive.getString("RELATION_TRADE_ID");
        String endDate = saleActive.getString("END_DATE");
        IDataset saleActives = UserSaleActiveInfoQry.querySaleActiveByUserIdCampnIdTradeId(user_id, campn_id, relation_trade_id);
        if (IDataUtil.isNotEmpty(saleActives))
        {
            for (int i = 0, size = saleActives.size(); i < size; i++)
            {
                IData data = saleActives.getData(i);
                data.put("MODIFY_TAG", "1"); // 1-删除
                data.put("END_DATE", endDate);
                data.put("TRADE_ID", getTradeId());
                data.put("ACCEPT_DATE", reqData.getAcceptTime()); // 受理时间
                data.put("UPDATE_TIME", SysDateMgr.getSysDate());
                data.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
                data.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
                data.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
                data.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
                data.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());

                super.addTradeSaleActive(data);
            }
        }
    }

    protected BaseReqData getReqData() throws Exception
    {
        return new SaleActiveBeanReqData();
    }

    protected void initReqData() throws Exception
    {
        super.initReqData();

        reqData = (SaleActiveBeanReqData) getBaseReqData();
    }

    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);

        reqData.setSaleDiscnts(map.getDataset("SALE_DISCNT"));
        reqData.setSaleActive(map.getData("SALE_ACTIVE"));
    }

    protected void makUca(IData map) throws Exception
    {
        makUcaForGrpNormal(map);
    }

    protected String setTradeTypeCode() throws Exception
    {
        return "3607"; // 集团营销活动
    }
}
