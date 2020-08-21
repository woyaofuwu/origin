package com.asiainfo.veris.crm.order.soa.frame.csservice.group.changeuserelement;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.group.common.GroupBaseConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PoRatePlanIcbQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.*;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commoncomponent.BbossDisAttrTransBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commoncomponent.BbossIAGWCloudMASDealBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commoncomponent.BbossPayBizInfoDealbean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commonmethod.GrpCommonBean;

import org.apache.log4j.Logger;

public class ChangeJKDTDiscntProductTrade extends ChangeUserElement
{
    private static Logger log = Logger.getLogger(ChangeBBossMerchPUserTrade.class);

    // XXX 处理基类取不到业务类型
    @Override
    public String setTradeTypeCode() throws Exception
    {
        // 1- 继承基类处理
        //super.setTradeTypeCode();

       return "2333";
    }
    /*
     * @descrition 查询被删除的参数是否能够找到对应的状态为新增的台帐信息
     * @author xunyl
     * @date 2013-10-24
     */
    protected static boolean isExistAddAttrInfo(String delParamCode, IDataset paramInfoList) throws Exception
    {
        boolean isExistAddAttr = false;

        IData paramInfo = new DataMap();
        for (int i = 0; i < paramInfoList.size(); i++)
        {
            paramInfo = paramInfoList.getData(i);
            String paramCode = paramInfo.getString("ATTR_CODE");
            String state = paramInfo.getString("STATE");
            if (delParamCode.equals(paramCode) && GroupBaseConst.PRODUCT_ATTR_STATUS_DESC.ATTR_ADD.getValue().equals(state))
            {
                isExistAddAttr = true;
                break;
            }
        }

        return isExistAddAttr;
    }

    protected ChangeBBossUserReqData reqData = null;

    /*
     * @descriton 修改产品参数资料数据，此表本因由基类处理，但是由于BBOSS侧的数据比较特殊，前台会将修改的参数形成两条记录传到后台，而基类只处理一条记录的情况
     * 因此此方法重写基类方法，BBOSS侧的自己处理发生变更的参数
     * @author xunyl
     * @date 2013-05-14
     */
    @Override
    public void actTradePrdParams() throws Exception
    {

        // 1-获取产品参数信息
        IDataset productParamInfos = reqData.getBBOSS_PRODUCT_PARAM_INFO();
        if (null == productParamInfos || productParamInfos.size() <= 0)
        {
            return;
        }
        // 因为变更时子类每次只处理一个产品，因此产品参数的List中一般只会有一条记录
        IData productParamInfo = productParamInfos.getData(0);

        // 2-处理产品参数资料表
        IDataset regProductParamInfos = new DatasetList();// 产品参数集(符合参数台账表数据)

        // 2-1获取前台传递过来的所有参数值
        IDataset params = productParamInfo.getDataset("PRODUCT_PARAM");
        if (params == null || params.size() <= 0)
        {
            return;
        }

        // 2-2获取原属性资料表中的最大下标值，变更中新增属性的下标从原有的最大下标开始每次新增1
        String userId = reqData.getUca().getUser().getUserId();// 产品用户编号

        // 2-3对参数值实现分类处理
        for (int i = 0; i < params.size(); i++)
        {
            IData param = params.getData(i);

            // 根据产品用户编号和产品参数编码获取该参数的资料信息
            String paramCode = param.getString("ATTR_CODE");// 产品参数编码
            String paraValue = param.getString("ATTR_VALUE");// 产品参数值
            String attrGroup = param.getString("ATTR_GROUP");// 属性组编号
            String paramName = param.getString("ATTR_NAME");// 产品参数名称
            if (StringUtils.isBlank(attrGroup))
            {
                attrGroup = null;
            }

            // 一般情况下，上面查询出的list中至多有一条记录，没有则说明是新增的参数，新增参数得组装全部参数订单数据
            String state = param.getString("STATE");
            if (GroupBaseConst.PRODUCT_ATTR_STATUS_DESC.ATTR_ADD.getValue().equals(state))
            {
                String modifyTag = CSBaseConst.TRADE_MODIFY_TAG.Add.getValue();
                addAttrTradeInfo(paramCode, paramName, paraValue, attrGroup, modifyTag, regProductParamInfos);
                continue;
            }
            else if (GroupBaseConst.PRODUCT_ATTR_STATUS_DESC.ATTR_DEL.getValue().equals(state))
            {
                IDataset instParams = UserAttrInfoQry.qryBbossUserAttrForGroupNew(userId, attrGroup, paramCode);
                if (IDataUtil.isEmpty(instParams) && StringUtils.isNotEmpty(paraValue))
                {
                    continue;
                    //CSAppException.apperr(CrmUserException.CRM_USER_1);
                }
                else if (IDataUtil.isEmpty(instParams) && StringUtils.isEmpty(paraValue))
                {
                    continue;
                }

                IData instParam = instParams.getData(0);
                instParam.put("MODIFY_TAG", CSBaseConst.TRADE_MODIFY_TAG.DEL.getValue());
                instParam.put("RSRV_STR3", paramName);// 设置名称(老数据割接没有名称)
                instParam.put("RSRV_STR4", attrGroup);// 设置属性组(老数据割接没有属性组编号)
                instParam.put("END_DATE", getAcceptTime());
                instParam.put("RSRV_STR5", GroupBaseConst.PARMA_STATUS.PARAM_DEL.getValue());// BBOSS侧参数状态，服开拼报文用
                regProductParamInfos.add(instParam);

                // 如果该当被删除的参数在在参数列表中找不到对应的状态为新增的记录，则需要新增一条值为""的参数台帐信息
                if (!isExistAddAttrInfo(paramCode, params) && StringUtils.isEmpty(attrGroup))
                {
                    addAttrTradeInfo(paramCode, paramName, "", attrGroup, "F", regProductParamInfos);
                }
            }
        }

        // 3-参数订单入表
        this.addTradeAttr(regProductParamInfos);
    }

    /**
     * 生成其它台帐数据（生成台帐后）
     *
     * @author xunyl
     * @Date 2013-05-07
     * @throws Exception
     */
    @Override
    public void actTradeSub() throws Exception
    {
        // 1- 继承基类处理
        super.actTradeSub();

        // 2- 如果产品操作类型为暂停，恢复，预取消，预取消恢复订购时，需要修改服务表状态
        String merchpOperType = reqData.getMerchpOperType();

        // 3- 修改BBOSS侧的产品表数据(TF_F_USER_GRP_MERCHP)
        this.infoRegDataEntireMerchP(merchpOperType);

        // 4- 登记other表，服务开通侧用
        infoRegDataOther(merchpOperType);

        // 5-特殊业务 需要登记特殊表
        infoRegDataSpecial();

    }

    /*
     * @description 新增产品参数(modifyTag为F时表示该台账不转资料)
     * @author xunyl
     * @date 2013-10-24
     */
    protected void addAttrTradeInfo(String paramCode, String paramName, String paramValue, String attrGroup, String modifyTag, IDataset regProductParamInfos) throws Exception
    {
        // 1- 定义参数对象
        IData newParam = new DataMap();

        // 2- 添加参数类型
        newParam.put("INST_TYPE", "P");

        // 3- 添加参数状态
        newParam.put("MODIFY_TAG", modifyTag);

        // 4- 添加参数编号
        newParam.put("ATTR_CODE", paramCode);

        // 5- 添加参数名称
        newParam.put("RSRV_STR3", paramName);// 参数名称

        // 6- 添加参数值
        newParam.put("ATTR_VALUE", checkParamFileUpLoad(paramCode, paramValue));

        // 7- 添加属性组编号
        newParam.put("RSRV_STR4", attrGroup);

        // 8- 添加开始时间
        newParam.put("START_DATE", getAcceptTime());

        // 9- 添加结束时间
        newParam.put("END_DATE", SysDateMgr.getTheLastTime());

        // 10- 添加用户编号
        String userId = reqData.getUca().getUser().getUserId();
        newParam.put("USER_ID", userId);

        // 11- 添加实例编号
        newParam.put("INST_ID", SeqMgr.getInstId());
        IDataset productInfoList = UserProductInfoQry.getUserProductByUserIdProductId(reqData.getUca().getUserId(), reqData.getUca().getProductId());
        String relainstId = productInfoList.getData(0).getString("INST_ID");
        newParam.put("RELA_INST_ID", relainstId);

        // 12- 添加BBOSS侧参数状态，服开拼报文用
        newParam.put("RSRV_STR5", GroupBaseConst.PARMA_STATUS.PARAM_ADD.getValue());

        // 13- 将新增产品参数添加至参数列表
        regProductParamInfos.add(newParam);

        // 14- 公众服务云基础产品中的费用折扣，还需添加一些资费参数(原则上不建议做这样的特殊处理)
        if ("1116013004".equals(paramCode))
        {
            String productId = reqData.getUca().getProductId();
            BbossDisAttrTransBean.addIcbTradeInfoForChgUsParams(userId, productId, paramValue, regProductParamInfos);
        }
    }

    /*
     * @description 校验产品参数附件是否上传
     * @author xunyl
     * @date 2013-12-03
     */
    protected String checkParamFileUpLoad(String attrCode, String attrValue) throws Exception
    {
        // 1- 检查参数是否为附件类型
        String paramType = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_BBOSS_ATTR", new String[]
                { "ATTR_CODE" }, "EDIT_TYPE", new String[]
                { attrCode });
        if (StringUtils.equals("UPLOAD", paramType) && !"6".equals(CSBizBean.getVisit().getInModeCode()))
        {
            attrValue = GrpCommonBean.checkFileState(attrValue);
        }
        return attrValue;
    }

    /*
     * @description 根据资费编码到trade_discnt表查找inst_id,用于与资费参数进行关联
     * @author xunyl
     * @date 2013-09-03
     */
    protected String getInstIdByDiscntCode(String discntCode,String merchpDisModifyTag) throws Exception
    {
        // 1- 定义返回对象
        String instId = "";

        // 2- 获取资费对象
        IDataset merchPDsts = bizData.getTradeDiscnt();

        // 2- 循环产品资费信息，获取相应的inst_id
        for (int i = 0; i < merchPDsts.size(); i++)
        {
            IData merchPDst = merchPDsts.getData(i);
            String elementId = merchPDst.getString("DISCNT_CODE");
            String disModifyTag = merchPDst.getString("MODIFY_TAG");
            if (discntCode.equals(elementId) && merchpDisModifyTag.equals(disModifyTag))
            {
                instId = merchPDst.getString("INST_ID");
                break;
            }
        }

        // 3- 返回数据
        return instId;
    }

    @Override
    protected BaseReqData getReqData() throws Exception
    {
        return new ChangeBBossUserReqData();
    }

    /*
     * @description 根据接口规范的产品操作编号获取服务开通侧配置的产品操作编号(供服务开通用)
     * @author xunyl
     * @date 2013-08-26
     */
    protected String getServMerchpOpType(String merchpOpType) throws Exception
    {
        // 1- 定义返回值
        String servMerchpOpType = "";

        // 2-获取对应的产品编号
        if (merchpOpType.equals(GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_ADD.getValue()))
        {
            servMerchpOpType = "06";
        }
        else if (merchpOpType.equals(GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_CANCLE.getValue()))
        {
            servMerchpOpType = "07";
        }
        else if (merchpOpType.equals(GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_PASTE.getValue()))
        {
            servMerchpOpType = "04";
        }
        else if (merchpOpType.equals(GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_CONTINUE.getValue()))
        {
            servMerchpOpType = "05";
        }
        else if (merchpOpType.equals(GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_MODIFY_DISCNT.getValue()))
        {
            servMerchpOpType = "15";
        }
        else if (merchpOpType.equals(GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_MODIFY_MEB.getValue()))
        {
            servMerchpOpType = "16";
        }
        else if (merchpOpType.equals(GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_MODIFY_PARAM.getValue()))
        {
            servMerchpOpType = "19";
        }
        else if (merchpOpType.equals(GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_PREDEAL.getValue()))
        {
            servMerchpOpType = "10";
        }
        else if (merchpOpType.equals(GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_PREDESTORY.getValue()))
        {
            servMerchpOpType = "11";
        }
        else if (merchpOpType.equals(GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_CANCLEPREDESTORY.getValue()))
        {
            servMerchpOpType = "12";
        }
        else if (merchpOpType.equals(GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_MODIFY_PROV.getValue()))
        {
            servMerchpOpType = "13";
        }
        else if (merchpOpType.equals(GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_COPREDEAL.getValue()))
        {
            servMerchpOpType = "14";
        }
        else if (merchpOpType.equals(GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_CODEAL.getValue()))
        {
            servMerchpOpType = "25";
        }
        else if (merchpOpType.equals(GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_CONTINUE_MEBFLUX.getValue()))
        {
            servMerchpOpType = "23";
        }
        else if (merchpOpType.equals(GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_PASTE_MEBFLUX.getValue()))
        {
            servMerchpOpType = "22";
        }

        // 3- 返回服务开通侧产品操作编号
        return servMerchpOpType;
    }

    public IData getTradeUserExtendData() throws Exception
    {

        IData userExtenData = super.getTradeUserExtendData();

        String merchpOperType = reqData.getMerchpOperType();

        userExtenData.put("MODIFY_TAG", CSBaseConst.TRADE_MODIFY_TAG.MODI.getValue());
        if (merchpOperType.equals(GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_CONTINUE.getValue()) || // 产品恢复
                merchpOperType.equals(GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_CANCLEPREDESTORY.getValue())||
                merchpOperType.equals(GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_CONTINUE_MEBFLUX.getValue()))// 预取消恢复
        {
            userExtenData.put("USER_STATE_CODESET", "0");
            userExtenData.put("RSRV_STR5", "A"); // 用户状态 A 开通
        }
        else if (merchpOperType.equals(GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_PASTE.getValue())||
                merchpOperType.equals(GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_PASTE_MEBFLUX.getValue()))// 产品暂停
        {
            userExtenData.put("USER_STATE_CODESET", "3");
            userExtenData.put("RSRV_STR5", "N"); // 用户状态 A 开通
        }
        else if (merchpOperType.equals(GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_PREDESTORY.getValue()))// 预取消
        {
            userExtenData.put("USER_STATE_CODESET", "5");
            userExtenData.put("RSRV_STR5", "D"); // 用户状态 A 开通
        }

        return userExtenData;

    }

    /**
     * @description 业务台帐BBOSS产品用户订购表子表
     * @author xunyl
     * @Date 2013-05-08
     */
    public void infoRegDataEntireMerchP(String merchpOperType) throws Exception
    {
        // 1-1 获取用户编号
        String userId = reqData.getUca().getUser().getUserId();

        // 1-2 根据用户编号获取产品信息
        IDataset products = UserEcrecepProductInfoQry.qryEcrecepProductInfoByUserIdMerchSpecProductSpecStatus(userId, null, null, null, null);
        if (IDataUtil.isEmpty(products))
        {
            CSAppException.apperr(GrpException.CRM_GRP_713,"USER_ID:"+userId+"在TF_F_USER_GRP_MERCHP表无有效用户信息，请联系系统管理员！");
        }
        if (products.size()>1)
        {
            CSAppException.apperr(GrpException.CRM_GRP_713,"USER_ID:"+userId+"在TF_F_USER_GRP_MERCHP表有多条有效记录，请联系系统管理员！");
        }
        // 一般来说一个产品用户编号对应一条产品用户记录，因此这里取第一条
        IData grpMerchPInfo = products.getData(0);


        // 1-3 根据不同的产品操作类型修改产品表信息
        String productOrderId = "";

        if (StringUtils.equals("6", CSBizBean.getVisit().getInModeCode()))
        {
            productOrderId = reqData.getBBOSS_PRODUCT_INFO().getString("PRODUCT_ORDER_ID"); // 产品订单号
            grpMerchPInfo.put("PRODUCT_OFFER_ID", reqData.getBBOSS_PRODUCT_INFO().getString("PRODUCT_OFFER_ID")); // 产品订单编号

        }
        else
        {

            productOrderId = SeqMgr.getBBossProductIdForGrp();// 产品订单编号
        }

        grpMerchPInfo.put("STATE", "MODI");
        grpMerchPInfo.put("MODIFY_TAG", CSBaseConst.TRADE_MODIFY_TAG.MODI.getValue());
        grpMerchPInfo.put("RSRV_STR1", merchpOperType);// 产品操作类型
        grpMerchPInfo.put("PRODUCT_ORDER_ID", productOrderId);

        // 1-4 产品操作类型为修改资费
        if (merchpOperType.equals(GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_MODIFY_DISCNT.getValue()) || merchpOperType.equals(GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_MODIFY_LOCALDISCNT.getValue()))
        {
            IDataset productDcts = reqData.cd.getDiscnt();
            if (productDcts != null && productDcts.size() > 0)
            {
                // 处理产品资费
                this.infoRegDataMerchPDct(productDcts, productOrderId);
            }
        }

        // 1-5 修改BBOSS侧产品表数据
        this.addTradeEcrecepProduct(IDataUtil.idToIds(grpMerchPInfo));

        IData epData = GrpCommonBean.actEcrecepProcedure(reqData.getUca().getUser().getUserId(),
                SeqMgr.getInstId(), reqData.getUca().getUser().getSerialNumber(), grpMerchPInfo.getString
                        ("PRODUCT_ORDER_ID"), grpMerchPInfo.getString("PRODUCT_OFFER_ID", ""), merchpOperType,
                "A", "A");
        this.addTradeEcrecepProcedure(epData);
    }

    /**
     * @description 业务台帐BBOSS产品资费表子表
     * @author xunyl
     * @Date 2013-05-08
     */
    protected void infoRegDataMerchPDct(IDataset productDcts, String productOrderId) throws Exception
    {
        // 产品资费数据集
        IDataset merchpDiscnts = new DatasetList();

        // 分情况处理资费
        for (int i = 0; i < productDcts.size(); i++)
        {
            IData productDct = productDcts.getData(i);

            // 1 新增 资费状态为ADD
            // 1-1 获取用户编号
            String userId = reqData.getUca().getUser().getUserId();

            // 1-2 根据产品用户编号获取集团公司返回的产品订单编号
            String productOfferId = "";// 集团公司返回的产品订单编号,默认为空串
            IDataset productUsers = UserGrpMerchpInfoQry.qryMerchpInfoByUserIdMerchSpecProductSpecStatus(userId, null, null, null, null);
            if (productUsers != null && productUsers.size() > 0)// 一般来说一个产品用户编号只会对应一条产品用户数据
            {
                IData productUser = productUsers.getData(0);
                productOfferId = productUser.getString("PRODUCT_OFFER_ID");
            }

            // 1-3 封装新增资费数据
            String productId = reqData.getUca().getProductId();// 产品编号
            String merchSpecCode = reqData.getOUT_MERCH_INFO().getString("MERCH_SPEC_CODE");// 商品规格编号
            String merchpSpecCode = GrpCommonBean.productJKDTToMerch(productId, 0);// 产品规格编号
            String productDiscntCode = GrpCommonBean.productJKDTToMerch(productDct.getString("DISCNT_CODE"), 1);// BBOSS产品优惠编码
           
            IData bbossProductInfoData = reqData.getBBOSS_PRODUCT_INFO();
            String poRatePolicyEffRule = "2";//套餐生效规则
            if(IDataUtil.isNotEmpty(bbossProductInfoData)){
            	poRatePolicyEffRule = bbossProductInfoData.getString("PO_RATE_POLICY_EFF_RULE","1");
            }    
            
            if (null == productDiscntCode)
            {
                productDiscntCode = "";
            }
            if (productDct.getString("MODIFY_TAG").equals("0") && !"".equals(productDiscntCode))
            {
                productDct.put("USER_ID", userId);
                productDct.put("MERCH_SPEC_CODE", merchSpecCode);// 商品规格编号
                productDct.put("PRODUCT_ORDER_ID", productOrderId);// 产品订单编号
                productDct.put("PRODUCT_OFFER_ID", productOfferId);// 集团公司的产品订单号
                productDct.put("PRODUCT_SPEC_CODE", merchpSpecCode);// 产品规格编号
                
                if("2".equals(poRatePolicyEffRule)){ 
                	//下账期生效，下月第一天生效
                	productDct.put("START_DATE", SysDateMgr.getFirstDayOfNextMonth());
                }else if("4".equals(poRatePolicyEffRule)){
                	//下一天生效
                	productDct.put("START_DATE", SysDateMgr.getTomorrowDate());
                }else {
                	//poRatePolicyEffRule为1，立即生效
                	productDct.put("START_DATE", getAcceptTime());
                }
                       
                productDct.put("END_DATE", SysDateMgr.getTheLastTime());
                productDct.put("PRODUCT_DISCNT_CODE", productDiscntCode);
                productDct.put("MODIFY_TAG", "0");
                productDct.put("INST_ID", SeqMgr.getInstId());
                productDct.put("RELA_INST_ID", getInstIdByDiscntCode(productDct.getString("DISCNT_CODE"),"0"));
                productDct.put("RSRV_STR1", GroupBaseConst.PARMA_STATUS.PARAM_ADD.getValue());// BBOSS侧参数状态，服开拼报文用
                merchpDiscnts.add(productDct);
            }

            // 2 删除 资费状态为DEL
            else if (productDct.getString("MODIFY_TAG").equals("1") && !"".equals(productDct.getString("DISCNT_CODE", "")))
            {
                IDataset datas = UserGrpMerchDiscntInfoQry.getUserProductDiscnt(userId, merchSpecCode, merchpSpecCode, productDiscntCode, null);
                if (datas != null && datas.size() > 0)// 一般来说，查询出的数据只有一条
                {
                    IData data = datas.getData(0);
                    data.put("MODIFY_TAG", "1");
                    
                    if("2".equals(poRatePolicyEffRule)){ 
                    	//下账期生效，本月最后一天失效                	
                    	data.put("END_DATE", SysDateMgr.getLastDateThisMonth());               
                    }else if("4".equals(poRatePolicyEffRule)){	//今天23：59：59失效
                    	data.put("END_DATE", SysDateMgr.getSysDate()+" 23:59:59");               	 
                    }else {	
                    	//poRatePolicyEffRule为1，立即失效
                    	data.put("END_DATE", getAcceptTime());
                    }

                    data.put("RSRV_STR1", GroupBaseConst.PARMA_STATUS.PARAM_DEL.getValue());// BBOSS侧参数状态，服开拼报文用
                    data.put("RELA_INST_ID", getInstIdByDiscntCode(productDct.getString("DISCNT_CODE"),"1"));
                    merchpDiscnts.add(data);
                }
            }

            // 3 变更 资费状态为MODI 此种情况下要产生两条台帐数据，一条新增，一条删除
            else if (productDct.getString("MODIFY_TAG").equals("2") && !"".equals(productDiscntCode))
            {
                // 新增数据
                IData newProductDct = new DataMap();
                newProductDct.put("USER_ID", userId);
                newProductDct.put("MERCH_SPEC_CODE", merchSpecCode);// 商品规格编号
                newProductDct.put("PRODUCT_ORDER_ID", productOrderId);// 产品订单编号
                newProductDct.put("PRODUCT_OFFER_ID", productOfferId);// 集团公司的产品订单号
                newProductDct.put("PRODUCT_SPEC_CODE", merchpSpecCode);// 产品规格编号
                
                if("2".equals(poRatePolicyEffRule)){ 
                	//下账期生效，下月第一天生效
                	newProductDct.put("START_DATE", SysDateMgr.getFirstDayOfNextMonth());
                }else if("4".equals(poRatePolicyEffRule)){
                	//下一天生效
                	newProductDct.put("START_DATE", SysDateMgr.getTomorrowDate());
                }else {
                	//poRatePolicyEffRule为1，立即生效
                	newProductDct.put("START_DATE", getAcceptTime());
                }
       
                newProductDct.put("END_DATE", SysDateMgr.getTheLastTime());
                newProductDct.put("PRODUCT_DISCNT_CODE", productDiscntCode);
                newProductDct.put("MODIFY_TAG", "0");
                newProductDct.put("INST_ID", SeqMgr.getInstId());
                newProductDct.put("RELA_INST_ID", getInstIdByDiscntCode(productDct.getString("DISCNT_CODE"),"2"));
                newProductDct.put("RSRV_STR1", GroupBaseConst.PARMA_STATUS.PARAM_ADD.getValue());// BBOSS侧参数状态，服开拼报文用
                merchpDiscnts.add(newProductDct);

                // 删除数据
                IDataset datas = UserGrpMerchDiscntInfoQry.getUserProductDiscnt(userId, merchSpecCode, merchpSpecCode, productDiscntCode, null);
                if (datas != null && datas.size() > 0)// 一般来说，查询出的数据只有一条
                {
                    IData data = datas.getData(0);
                    data.put("MODIFY_TAG", "1");
                    
                    if("2".equals(poRatePolicyEffRule)){ 
                    	//下账期生效，本月最后一天失效                	
                    	data.put("END_DATE", SysDateMgr.getLastDateThisMonth());               
                    }else if("4".equals(poRatePolicyEffRule)){	//今天23：59：59失效
                    	data.put("END_DATE", SysDateMgr.getSysDate()+" 23:59:59");               	 
                    }else {	
                    	//poRatePolicyEffRule为1，立即失效
                    	data.put("END_DATE", getAcceptTime());
                    }

                    data.put("RSRV_STR1", GroupBaseConst.PARMA_STATUS.PARAM_DEL.getValue());// BBOSS侧参数状态，服开拼报文用
                    data.put("RELA_INST_ID", getInstIdByDiscntCode(productDct.getString("DISCNT_CODE"),"2"));
                    merchpDiscnts.add(data);
                }
            }
        }

        // 登记产品资费台帐表
        this.addTradeGrpMerchpDiscnt(merchpDiscnts);
    }

    /*
     * @description 登记other表信息，供服务开通用
     * @author xunyl
     * @date 2013-08-26
     */
    protected void infoRegDataOther(String merchpOperType) throws Exception
    {
        IData serviceInfo = new DataMap();
        serviceInfo.put("USER_ID", reqData.getUca().getUser().getUserId());
        serviceInfo.put("RSRV_VALUE_CODE", "BBSS");
        serviceInfo.put("RSRV_VALUE", "集团BBOSS标志");
        serviceInfo.put("RSRV_STR9", "7810");// 服务开通侧集团service_id对应为7810
        serviceInfo.put("OPER_CODE", getServMerchpOpType(merchpOperType));
        serviceInfo.put("MODIFY_TAG", CSBaseConst.TRADE_MODIFY_TAG.MODI.getValue());
        serviceInfo.put("START_DATE", getAcceptTime());
        serviceInfo.put("END_DATE", SysDateMgr.getTheLastTime());
        serviceInfo.put("INST_ID", SeqMgr.getInstId());
        this.addTradeOther(serviceInfo);
    }

    /**
     * chenyi 2014-7-14 特殊业务登记特殊表
     *
     * @throws Exception
     */
    protected void infoRegDataSpecial() throws Exception
    {
        // 流量统付业务登记tf_b_trade_GRP_CENPAY 同步字段给账务
        boolean isFlux = BbossPayBizInfoDealbean.isFluxTFBusiness(reqData.getGrpProductId());// 判断是否统付业务
        // 如果为流量统付产品，则需要同步字段给账务
        if (isFlux)
        {
            IDataset productParamInfos = reqData.getBBOSS_PRODUCT_PARAM_INFO();
            IDataset grpCenPayDataset = new DatasetList();

            //获取集团产品编码
            String product_spec_code = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_ATTR_BIZ", new String[]{ "ID", "ID_TYPE", "ATTR_CODE", "ATTR_OBJ" }, "ATTR_VALUE", new String[]{ "1", "B", reqData.getGrpProductId(), "PRO" });
            //国际流量统付
            if ("99910".equals(product_spec_code))
            {
                grpCenPayDataset = BbossPayBizInfoDealbean.getDataGrpCenpayForInternational(productParamInfos, reqData.getUca().getUserId());
            }
            else
            {
                grpCenPayDataset = BbossPayBizInfoDealbean.getDataGrpCenpay(productParamInfos, reqData.getUca().getUserId());
            }

            this.addTradeGrpCenpay(grpCenPayDataset);
        }

        // 行业网关云MAS业务需要等级TF_B_TRADE_GRP_PLATSVC 同步给服务开通
//        if (BbossIAGWCloudMASDealBean.isIAGWCloudMAS(GrpCommonBean.productJKDTToMerch(reqData.getUca().getProductId(), 0)))
//        {
//            String userid = reqData.getUca().getUserId();
//            String operState = "08"; // 01:订购 02:注销 04:暂停 05:恢复 08:用户信息变更
//            String merchpOperType = reqData.getMerchpOperType(); // 取产品操作类型
//            IData productParamInfos = new DataMap();
//            if (GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_PASTE.getValue().equals(merchpOperType))
//            {
//                operState = "04";
//            }
//            if (GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_CONTINUE.getValue().equals(merchpOperType))
//            {
//                operState = "05";
//            }
//            if (GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_MODIFY_PARAM.getValue().equals(merchpOperType))
//            {
//                operState = "08";
//            }
//            if (GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_MODIFY_PARAM.getValue().equals(merchpOperType))
//            {
//                productParamInfos = BbossIAGWCloudMASDealBean.prepareProductDataForChg(reqData.getBBOSS_PRODUCT_PARAM_INFO(), userid);
//            }
//            else
//            {
//                productParamInfos = BbossIAGWCloudMASDealBean.prepareProductData(userid);
//            }
//            String serialNumber = reqData.getUca().getUser().getSerialNumber();
//            String product_spec_code = GrpCommonBean.productJKDTToMerch(reqData.getUca().getProductId(), 0);
//            String group_id = reqData.getUca().getCustGroup().getGroupId();
//
//            IData grpPlatSVCData = BbossIAGWCloudMASDealBean.makDataForGrpPlatSVC(userid, group_id, operState, product_spec_code, productParamInfos, serialNumber);
//            this.addTradeGrpPlatsvc(grpPlatSVCData);
//        }

        // 和对讲产品
        if(StringUtils.equals("9101101", GrpCommonBean.productJKDTToMerch(reqData.getGrpProductId(),0))){
            //2-1 获取合同结束时间及是否到期自动续约的参数值
            String contractEndDate = "";
            String newContractEndDate = "";
            String oldisRenew = "";
            String isRenew = "";
            IDataset productParamInfoList = reqData.cd.getProductParamList(reqData.getUca().getProductId());
            if (IDataUtil.isEmpty(productParamInfoList))
            {
                return;
            }
            for(int i=0,sizeI=productParamInfoList.size();i<sizeI;i++){
                IData param=productParamInfoList.getData(i);
                String state = param.getString("STATE");
                if ("91011010001".equals(param.getString("ATTR_CODE"))){
                    contractEndDate = param.getString("ATTR_VALUE");
                    if (GroupBaseConst.PRODUCT_ATTR_STATUS_DESC.ATTR_ADD.getValue().equals(state)) {
                        newContractEndDate = param.getString("ATTR_VALUE");
                    }
                }else if ("91011010002".equals(param.getString("ATTR_CODE"))){
                    oldisRenew = param.getString("ATTR_VALUE");
                    if (GroupBaseConst.PRODUCT_ATTR_STATUS_DESC.ATTR_ADD.getValue().equals(state)) {
                        isRenew = param.getString("ATTR_VALUE");
                    }
                }
            }
            if(StringUtils.equals("1", isRenew)){
                String remark = "和对讲业务，到期不自动续约，需要系统自动注销";
                if (!StringUtils.isBlank(newContractEndDate)) {
                    contractEndDate = newContractEndDate;
                }
                rigistDestyTable(remark,contractEndDate);
            } else if (StringUtils.equals("0", isRenew)) {
                String userid = reqData.getUca().getUserId();
                IData deleteCon = new DataMap();
                deleteCon.put("USER_ID", userid);
                Dao.delete("TF_TP_BBOSS_DESTROY_INFO", deleteCon,Route.CONN_CRM_CEN);
            } else if (!StringUtils.isBlank(newContractEndDate)) {
                String remark = "和对讲业务，到期不自动续约，需要系统自动注销";
                updateDestyTable(remark, newContractEndDate);
            }
        }
        //BBOSS产品配置化发送二次白名单给SIMS平台
        if (BbossIAGWCloudMASDealBean.isIAGWTOSIMS(GrpCommonBean.productJKDTToMerch(reqData.getGrpProductId(), 0)))
        {
            String userid = reqData.getUca().getUserId();
            String operState = "01"; // 01:订购 02:注销 04:暂停 05:恢复 08:用户信息变更
            IData productParamInfos = reqData.cd.getProductParamMap(reqData.getUca().getProductId());
            String serialNumber = reqData.getUca().getUser().getSerialNumber();
            String product_spec_code = GrpCommonBean.productJKDTToMerch(reqData.getUca().getProductId(), 0);
            String group_id = reqData.getUca().getCustGroup().getGroupId();
            productParamInfos.put("PRODUCT_ID", reqData.getUca().getProductId());
            IData grpPlatSVCData = BbossIAGWCloudMASDealBean.makDataForGrpPlatSVC(userid, group_id, operState, product_spec_code, productParamInfos, serialNumber);

            setEcBusiChgInfo(grpPlatSVCData);
        }
    }
    /**
     * 作用：IBOSS同步数据表记录
     *
     * @author zhangbo18 2017-04-05
     * @param data
     * @throws Exception
     */
    public void setEcBusiChgInfo(IData data) throws Exception
    {
        IData ecBusiInfo = new DataMap();
        String insid = SeqMgr.getInstId();
        ecBusiInfo.put("INST_ID", insid);
        ecBusiInfo.put("EC_ID", data.getString("GROUP_ID"));
        ecBusiInfo.put("EC_OPR_CODE", "04");
        ecBusiInfo.put("PRODUCT_NAME", data.getString("BIZ_NAME"));
        ecBusiInfo.put("PRODUCT_ID", data.getString("INST_ID"));
        ecBusiInfo.put("BIZ_CODE", data.getString("BIZ_CODE"));
        ecBusiInfo.put("BASE_ACCESSNO", data.getString("EC_BASE_IN_CODE"));
        ecBusiInfo.put("SERV_CODE", data.getString("SERV_CODE"));
        ecBusiInfo.put("SERV_TYPE", data.getString("SERV_TYPE"));
        ecBusiInfo.put("SOURCE", "02");
        ecBusiInfo.put("OPER_STATE", data.getString("OPER_STATE"));
        ecBusiInfo.put("BIZ_STATUS", data.getString("BIZ_STATUS"));
        ecBusiInfo.put("RB_LIST", data.getString("BIZ_ATTR"));
        ecBusiInfo.put("CONFIRMFLAG", data.getString("CONFIRMFLAG"));
        ecBusiInfo.put("ACCESSMODEL", data.getString("ACCESS_MODE"));
        ecBusiInfo.put("TEXT_SIGN_FLAG", data.getString("IS_TEXT_ECGN"));
        ecBusiInfo.put("TEXT_SIGN_DEFAULT", data.getString("DEFAULT_ECGN_LANG"));
        ecBusiInfo.put("TEXT_SIGN_ZH", data.getString("TEXT_ECGN_ZH"));
        ecBusiInfo.put("TEXT_SIGN_EN", data.getString("TEXT_ECGN_EN"));
        ecBusiInfo.put("BIZ_OPR_CODE", data.getString("OPER_STATE"));

        CSAppCall.call("SS.GroupInfoChgSVC.synChgEcBusiInfo", ecBusiInfo);
    }
    /*
     * @description 初始化RD
     * @author xunyl
     * @date 2013-05-06
     */
    @Override
    protected void initReqData() throws Exception
    {
        super.initReqData();
        reqData = (ChangeBBossUserReqData) getBaseReqData();
    }

    /*
     * @description 修改产品用户表信息
     * @author xunyl
     * @Date 2013-05-07
     */

    /*
     * @description 将前台传递过来的BBOSS数据放入RD中
     * @author xunyl
     * @date 2013-05-06
     */
    public void makBBossReqData(IData map) throws Exception
    {
        String merchpOperType = map.getString("PRODUCT_OPER_TYPE");

        // 设置产品操作类型
        reqData.setMerchpOperType(merchpOperType);

        // 设置商品创建时的反馈信息
        reqData.setOUT_MERCH_INFO(map.getData("OUT_MERCH_INFO"));

        // 设置产品参数信息(因为基类不能对变更的参数进行处理，BBOSS侧得自己处理)
        if (merchpOperType.equals(GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_MODIFY_PARAM.getValue()) || merchpOperType.equals(GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_PASTE.getValue())
                || merchpOperType.equals(GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_MODIFY_DISCNT.getValue()) || merchpOperType.equals(GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_MODIFY_MEB.getValue())
                || merchpOperType.equals(GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_MODIFY_PROV.getValue()) || merchpOperType.equals(GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_CONTINUE.getValue())
                ||merchpOperType.equals(GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_PASTE_MEBFLUX.getValue())||merchpOperType.equals(GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_CONTINUE_MEBFLUX.getValue()))
        {
            reqData.setBBOSS_PRODUCT_PARAM_INFO(map.getDataset("PRODUCT_PARAM_INFO"));
        }

        // 将产品信息保存至RD中，创建tf_f_user_grp_merchp表数据时用
        reqData.setBBOSS_PRODUCT_INFO(map.getData("BBOSS_PRODUCT_INFO"));
    }

    /*
     * @description 給RD賦值
     * @author xunyl
     * @date 2013-04-25
     */
    @Override
    protected void makReqData(IData map) throws Exception
    {
        makBBossReqData(map);
        super.makReqData(map);
    }

    /**
     * @description bboss资费修改是特殊处理 新增一条删除一条，基类的这个方法处理不了
     * @author penghb
     * @date 2014-09-03
     */
    protected void modifyDiscnt() throws Exception
    {

    }

    /*
     * @description 修改服务状态表数据
     * @author xunyl
     * @date 2013-05-07
     */
    protected void processSvcState(String merchpOperType) throws Exception
    {
        // 1 定义服务状态数据集合
        IDataset svcState = new DatasetList();

        // 2 获取用户服务表数据
        String userId = reqData.getUca().getUser().getUserId();
        IDataset userSvcInfo = UserSvcInfoQry.queryUserSvcByUserIdAll(userId);
        if (IDataUtil.isEmpty(userSvcInfo))
        {
            return;
        }

        // 3 循环获取用户服务状态数据
        for (int i = 0; i < userSvcInfo.size(); i++)
        {
            IData userSvc = userSvcInfo.getData(i);// 单个服务数据
            // 获取服务编号
            String serviceId = userSvc.getString("SERVICE_ID");

            // 根据用户编号和服务编号获取服务状态数据
            IDataset userSvcStateInfo = UserSvcStateInfoQry.getUserLastStateByUserSvc(userId, serviceId);
            for (int j = 0; j < userSvcStateInfo.size(); j++)
            {
                // 根据不同的产品操作类型来确定新的状态值
                String state = "0";
                if (GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_PASTE.getValue().equals(merchpOperType) || GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_PASTE_MEBFLUX.getValue().equals(merchpOperType) )
                {// 产品暂停
                    state = "3";
                }
                else if (GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_PREDESTORY.getValue().equals(merchpOperType))
                {// 产品预取消
                    state = "5";
                }
                else
                {// 产品恢复或恢复预取消
                    state = "0";
                }

                // 删除一条服务状态数据
                IData userSvcState = userSvcStateInfo.getData(j);
                userSvcState.put("MODIFY_TAG", CSBaseConst.TRADE_MODIFY_TAG.DEL.getValue());
                userSvcState.put("END_DATE", getAcceptTime());// 获取系统当前日期

                // 新增一条服务状态数据
                IData userSvcState1 = new DataMap();
                userSvcState1.putAll(userSvcState);
                userSvcState1.put("STATE_CODE", state);// 新的状态
                userSvcState1.put("START_DATE", getAcceptTime());
                userSvcState1.put("MODIFY_TAG", CSBaseConst.TRADE_MODIFY_TAG.Add.getValue());
                userSvcState1.put("END_DATE", SysDateMgr.getTheLastTime());
                userSvcState1.put("INST_ID", SeqMgr.getInstId());

                svcState.add(userSvcState);
                svcState.add(userSvcState1);
            }
        }

        // 4 添加用户服务状态数据
        super.addTradeSvcstate(svcState);
    }

    /*
     * @description 重写基类方法(基类登记参数时没有登记参数名称，BBOSS侧需要登记参数名称)
     * @author xunyl
     * @date 2013-08-26
     */
    protected void setTradeAttr(IData map) throws Exception
    {
        // 调用基类处理
        super.setTradeAttr(map);

        // 如果是资费参数
        if ("D".equals(map.getString("INST_TYPE", "")))
        {
            // 查询是否是ICB参数
            String number = map.getString("ATTR_CODE", "");
            IDataset IcbSet = PoRatePlanIcbQry.getIcbsByParameterNumber(number);
            if (IcbSet != null && IcbSet.size() > 0)
            {
                map.put("RSRV_STR3", IcbSet.getData(0).getString("PARAMETERNAME", ""));// ICB参数名称
            }
            else
            {
                map.put("IS_NEED_PF", "0");// 是否需要发服开 0为不发，1或"" 是要发服开
            }
        }
    }

    /*
     * @descripiton 重写基类的登记主台账方法,BBOSS侧默认为全部需要发送服务开通
     * @author xunyl
     * @date 2013-08-21
     */
    protected void setTradeBase() throws Exception
    {
        // 1- 调用基类方法注入值
        super.setTradeBase();

        // 2- 子类修改OLCOM_TAG值，BBOSS侧默认设置为１
        String specProductId = GrpCommonBean.productJKDTToMerch(reqData.getUca().getProductId(), 0);
        IData data = bizData.getTrade();
        String merchpOperType = reqData.getMerchpOperType();
        if ("6".equals(CSBizBean.getVisit().getInModeCode()) || merchpOperType.equals(GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_MODIFY_LOCALDISCNT.getValue()))
        {
            // 渠道类型为IBOSS或者本地资费变更
            data.put("OLCOM_TAG", "0");
        }
        else
        {
            data.put("OLCOM_TAG", "1");
        }
        //集客大厅受理逻辑  daidl
        IDataset dataset = CommparaInfoQry.getCommparaInfos("CSM", "9079", specProductId);
        if (IDataUtil.isNotEmpty(dataset)) {
        	//读配置判断是否发指令
        	data.put("OLCOM_TAG", dataset.getData(0).getString("PARA_CODE1"));
            //读配置判断是否等待服开回单
            data.put("PF_WAIT", dataset.getData(0).getString("PARA_CODE2"));
        }
    }

    /**
     * @description 登记一键注销表
     * @author xunyl
     * @date 2016-01-20
     */
    private void rigistDestyTable(String remark,String destroyTime)throws Exception{
        IData destroyTableInfo = new DataMap();
        destroyTableInfo.put("USER_ID", reqData.getUca().getUserId());
        destroyTableInfo.put("DESTROY_TYPE", "1");//1为集团注销
        destroyTableInfo.put("GROUP_ID", reqData.getUca().getCustGroup().getGroupId());
        destroyTableInfo.put("PRODUCT_ID", reqData.getUca().getProductId());
        destroyTableInfo.put("EPARCHY_CODE", CSBizBean.getUserEparchyCode());
        destroyTableInfo.put("REMARK", remark);
        destroyTableInfo.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());
        destroyTableInfo.put("ANTI_INTF_FLAG","1");//反向接口
        destroyTableInfo.put("DESTROY_TIME",destroyTime);
        destroyTableInfo.put("UPDATE_TIEM",SysDateMgr.getSysTime());
        Dao.insert("TF_TP_BBOSS_DESTROY_INFO", destroyTableInfo,Route.CONN_CRM_CEN);
    }

    /**
     * @description 登记一键注销表
     * @author xunyl
     * @date 2016-01-20
     */
    private void updateDestyTable(String remark,String destroyTime)throws Exception{
        IData destroyTableInfo = new DataMap();
        destroyTableInfo.put("USER_ID", reqData.getUca().getUserId());
        destroyTableInfo.put("DESTROY_TYPE", "1");//1为集团注销
        destroyTableInfo.put("GROUP_ID", reqData.getUca().getCustGroup().getGroupId());
        destroyTableInfo.put("PRODUCT_ID", reqData.getUca().getProductId());
        destroyTableInfo.put("EPARCHY_CODE", CSBizBean.getUserEparchyCode());
        destroyTableInfo.put("REMARK", remark);
        destroyTableInfo.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());
        destroyTableInfo.put("ANTI_INTF_FLAG","1");//反向接口
        destroyTableInfo.put("DESTROY_TIME",destroyTime);
        destroyTableInfo.put("UPDATE_TIEM",SysDateMgr.getSysTime());
        Dao.update("TF_TP_BBOSS_DESTROY_INFO", destroyTableInfo, new String[]
                { "USER_ID"}, Route.CONN_CRM_CEN);
    }
}