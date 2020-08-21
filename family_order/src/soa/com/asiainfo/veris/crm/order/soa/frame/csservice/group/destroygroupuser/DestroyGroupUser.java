
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.destroygroupuser;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.group.common.base.GrpModuleData;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserGrpPkgInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserPayPlanInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserVpnInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.ElementModel;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.ElementUtil;

public class DestroyGroupUser extends GroupBean
{
    protected GrpModuleData moduleData = new GrpModuleData();

    protected DestroyGroupUserReqData reqData = null;

    protected void actTradeOther() throws Exception
    {
        String user_id = reqData.getUca().getUser().getUserId();
        IData inparams = new DataMap();
        inparams.put("USER_ID", user_id);
        inparams.put("PARTITION_ID", user_id.substring(user_id.length() - 4));

        IDataset otherList = UserOtherInfoQry.getUserOtherInfoByAllUserId(inparams);

        if (IDataUtil.isNotEmpty(otherList))
        {
            for (int i = 0, sz = otherList.size(); i < sz; i++)
            {
                IData data = otherList.getData(i);

                data.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());

                if (reqData.isIfBooking())
                {
                    data.put("END_DATE", SysDateMgr.getLastDateThisMonth());
                }
                else
                {
                    data.put("END_DATE", getAcceptTime());
                }

                // BBOSS要求不发
                if ("BOSG".equals(reqData.getUca().getBrandCode()))
                {
                    data.put("IS_NEED_PF", "0");// 1或者是空： 发指令 0不发指令
                }
            }
        }

        addTradeOther(otherList);
    }

    /**
     * 定制的付费计划注销
     * 
     * @throws Exception
     */
    protected void actTradePayPlan() throws Exception
    {
        // 查询用户付费计划
        IDataset userPayPlanList = UserPayPlanInfoQry.getUserPayPlanByUserId(reqData.getUca().getUserId(), null, null);// 集团订购-1可不传，兼容老系统BBOSS
        // user_id_a放的商品的user_id

        if (IDataUtil.isEmpty(userPayPlanList))
        {
            return;
        }

        // 注销用户付费计划
        for (int i = 0, row = userPayPlanList.size(); i < row; i++)
        {
            IData userPayPlanData = userPayPlanList.getData(i);

            userPayPlanData.put("END_DATE", getAcceptTime());
            userPayPlanData.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
        }

        super.addTradeUserPayplan(userPayPlanList);
    }

    /**
     * 注销用户付费关系
     * 
     * @throws Exception
     */
    protected void actTradePayRela() throws Exception
    {
        // 查询用户付费关系信息
        IData payRelaData = UcaInfoQry.qryDefaultPayRelaByUserId(reqData.getUca().getUser().getUserId());

        if (IDataUtil.isEmpty(payRelaData))
        {
            return;
        }

        // 注销用户付费关系信息
        payRelaData.put("END_CYCLE_ID", SysDateMgr.getNowCyc());
        payRelaData.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());

        super.addTradePayrelation(payRelaData);
    }

    /**
     * 注销产品子表
     * 
     * @throws Exception
     */
    protected void actTradePrd() throws Exception
    {
        // 查询用户产品信息
        IDataset userProductList = reqData.cd.getProduct();// 先直接取 没有数据再查一次

        if (IDataUtil.isEmpty(userProductList))
        {
            userProductList = UserProductInfoQry.getProductInfo(reqData.getUca().getUserId(), null, Route.CONN_CRM_CG);// 集团订购-1可不传，兼容老系统BBOSS
            if (IDataUtil.isEmpty(userProductList))
            {
                return;
            }
        }

        IDataset userProductAttrDataset = new DatasetList();

        for (int i = 0, row = userProductList.size(); i < row; i++)
        {
            IData userProductData = userProductList.getData(i);

            userProductData.put("END_DATE", getAcceptTime());
            userProductData.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());

            String instId = userProductData.getString("INST_ID");

            // 处理产品参数信息
            if (StringUtils.isNotBlank(instId))
            {
                // 查询产品参数信息
                IDataset userProductAttrList = UserAttrInfoQry.qryUserAttrByUserRelaInstId(reqData.getUca().getUserId(), instId);

                // 注销产品参数信息
                if (IDataUtil.isNotEmpty(userProductAttrList))
                {
                    for (int j = 0, jRow = userProductAttrList.size(); j < jRow; j++)
                    {
                        IData userProductAttrData = userProductAttrList.getData(j);
                        userProductAttrData.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                        userProductAttrData.put("END_DATE", userProductData.getString("END_DATE"));
                    }

                    userProductAttrDataset.addAll(userProductAttrList);
                }
            }
        }

        super.addTradeAttr(userProductAttrDataset);

        super.addTradeProduct(userProductList);
    }

    @Override
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();

        // 用户资料注销
        actTradeUser();

        // 产品子表注销
        actTradePrd();

        // 付费关系注销
        actTradePayRela();

        // 付费计划注销
        actTradePayPlan();

        // 服务状态表
        actTradeSvcState();

        // other表
        actTradeOther(); // add by licheng@移动电话会议 hainan
    }

    /**
     * 注销用户资料
     * 
     * @throws Exception
     */
    protected void actTradeUser() throws Exception
    {
        IData userData = reqData.getUca().getUser().toData();

        String reasonCode = reqData.getReasonCode();
        String reasonName = StaticUtil.getStaticValue("TD_B_REMOVE_REASON_GROUP", reqData.getReasonCode());

        userData.put("REMOVE_TAG", "2"); // 注销标志：0-正常、1-主动预销号、2-主动销号、3-欠费预销号、4-欠费销号、5-开户返销、6-过户注销
        userData.put("DESTROY_TIME", getAcceptTime());

        userData.put("REMOVE_EPARCHY_CODE", CSBizBean.getVisit().getStaffEparchyCode()); // 注销地市
        userData.put("REMOVE_CITY_CODE", CSBizBean.getVisit().getCityCode()); // 注销市县
        userData.put("REMOVE_DEPART_ID", CSBizBean.getVisit().getDepartId()); // 注销渠道
        userData.put("REMOVE_REASON_CODE", reasonCode); // 注销原因
        userData.put("USER_STATE_CODESET", "1"); // 用户主体服务状态集：见服务状态参数表

        userData.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue()); // 修改标志

        userData.put("REMARK", reasonName); // 暂填注销原因的中文解释

        userData.put("RSRV_STR2", reasonCode);
        userData.put("RSRV_STR8", reasonName);

        super.addTradeUser(userData);
    }

    @Override
    protected BaseReqData getReqData() throws Exception
    {
        return new DestroyGroupUserReqData();
    }

    /**
     * VPN资料注销
     * 
     * @param Datas
     *            VPN参数
     * @author liaoyi
     * @throws Exception
     */
    protected final void infoRegDataVpn() throws Exception
    {

        String userId = reqData.getUca().getUser().getUserId();

        IDataset userVpnList = UserVpnInfoQry.qryUserVpnByUserId(userId);

        // 用户VPN信息
        if (IDataUtil.isEmpty(userVpnList))
        {
            return;
        }

        IData userVpnData = userVpnList.getData(0);

        // 后台完工流程自动填写
        userVpnData.put("REMOVE_TAG", "1");// 注销标志：0-正常、1-已注销

        if (reqData.isIfBooking())
        {
            userVpnData.put("REMOVE_DATE", SysDateMgr.getLastDateThisMonth());// 注销时间
        }
        else
        {
            userVpnData.put("REMOVE_DATE", getAcceptTime());// 注销时间
        }

        userVpnData.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());// 修改标志

        super.addTradeVpn(userVpnData);
    }

    @Override
    protected final void initProductCtrlInfo() throws Exception
    {

        String productId = reqData.getUca().getProductId();
        getProductCtrlInfo(productId, BizCtrlType.DestoryUser);
    }

    @Override
    protected void initReqData() throws Exception
    {
        super.initReqData();

        reqData = (DestroyGroupUserReqData) getBaseReqData();
    }

    @Override
    protected void makInit(IData map) throws Exception
    {
        super.makInit(map);
        moduleData.getMoudleInfo(map);
    }

    @Override
    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);

        reqData.setReasonCode(map.getString("REASON_CODE"));
        reqData.setIfBooking(map.getBoolean("IF_BOOKING"));

        makReqDataElement();
        
    }

    /**
     * 封装产品元素信息
     * 
     * @throws Exception
     */
    private void makReqDataElement() throws Exception
    {
        IDataset userElementList = ProductInfoQry.qryUserProductElement(reqData.getUca().getUser().getUserId(), null);// 集团订购-1可不传，兼容老系统BBOSS
        // user_id_a放的商品的user_id

        if (IDataUtil.isEmpty(userElementList))
        {
            return;
        }

        IDataset svcDataset = new DatasetList();
        IDataset dctDataset = new DatasetList();
        IDataset resDataset = new DatasetList();
        IDataset paramDataset = new DatasetList();

        for (int i = 0, iSize = userElementList.size(); i < iSize; i++)
        {
            IData userElementData = userElementList.getData(i); // 取每个元素

            if (reqData.isIfBooking())
            {
                userElementData.put("END_DATE", SysDateMgr.getLastDateThisMonth());
            }
            else if("R".equals(userElementData.getString("ELEMENT_TYPE_CODE")))
            {
                userElementData.put("END_DATE", this.getAcceptTime());
            }
            else
            {
                ElementModel model = new ElementModel(userElementData);

                String cancelDate = ElementUtil.getCancelDateForDstUs(model, getAcceptTime());

                userElementData.put("END_DATE", cancelDate);
            }

            userElementData.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue()); // 注销

            if ("S".equals(userElementData.getString("ELEMENT_TYPE_CODE"))) // 服务
            {
                svcDataset.add(userElementData);
            }
            else if ("D".equals(userElementData.getString("ELEMENT_TYPE_CODE"))) // 优惠
            {
                dctDataset.add(userElementData);
            }
            else if ("R".equals(userElementData.getString("ELEMENT_TYPE_CODE"))) // 资源
            {
                resDataset.add(userElementData);
            }

            String instId = userElementData.getString("INST_ID");

            // 处理元素参数信息
            if (StringUtils.isNotBlank(instId))
            {
                // 查询用户参数信息
                IDataset userAttrList = UserAttrInfoQry.qryUserAttrByUserRelaInstId(reqData.getUca().getUserId(), instId);

                // 注销用户参数信息
                if (IDataUtil.isNotEmpty(userAttrList))
                {
                    for (int j = 0, jRow = userAttrList.size(); j < jRow; j++)
                    {
                        IData userAttrData = userAttrList.getData(j);
                        userAttrData.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                        userAttrData.put("END_DATE", userElementData.getString("END_DATE"));
                    }

                    paramDataset.addAll(userAttrList);
                }
            }
        }

        // 设置数据
        reqData.cd.putSvc(svcDataset);
        reqData.cd.putDiscnt(dctDataset);
        reqData.cd.putRes(resDataset);
        reqData.cd.putElementParam(paramDataset);

        makReqDataGrpPackage();
    }

    /**
     * 处理集团定制信息
     * 
     * @throws Exception
     */
    private void makReqDataGrpPackage() throws Exception
    {
        IDataset userGrpPkgList = UserGrpPkgInfoQry.getUserGrpPackage(reqData.getUca().getUser().getUserId(), Route.CONN_CRM_CG);

        if (IDataUtil.isEmpty(userGrpPkgList))
        {
            return;
        }

        // 注销集团定制信息
        for (int i = 0, iSzie = userGrpPkgList.size(); i < iSzie; i++)
        {
            IData userGrpPkgData = userGrpPkgList.getData(i);

            userGrpPkgData.put("END_DATE", reqData.isIfBooking() ? SysDateMgr.getLastDateThisMonth() : getAcceptTime());

            userGrpPkgData.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
        }

        // 设置数据
        reqData.cd.putGrpPackage(userGrpPkgList);
    }

    @Override
    protected final void makUca(IData map) throws Exception
    {
        makUcaForGrpNormal(map);
    }

    @Override
    protected void updTradeProcessTagSet() throws Exception
    {
        super.updTradeProcessTagSet();

        String score = ""; // reqData.getUca().getUser().getScoreValue();
        String iClearScoreTag;
        if (score.equals(""))
        {
            iClearScoreTag = "0";
        }
        else
        {
            iClearScoreTag = (Integer.valueOf(score) > 0) ? "1" : "0";
        }

        setProcessTag(1, iClearScoreTag);
    }
    
    protected void setTradeSvc(IData map) throws Exception
    {
        map.put("PACKAGE_ID", "-1");// svc discnt platsvc 表这两个字段填写-1
        map.put("PRODUCT_ID", "-1");
    }
    
    protected void setTradeDiscnt(IData map) throws Exception
    {
        map.put("PACKAGE_ID", "-1");// svc discnt platsvc 表这两个字段填写-1
        map.put("PRODUCT_ID", "-1");
    }
    
    protected void setTradePlatsvc(IData map) throws Exception
    {
        map.put("PACKAGE_ID", "-1");// svc discnt platsvc 表这两个字段填写-1
        map.put("PRODUCT_ID", "-1");
    }
}
