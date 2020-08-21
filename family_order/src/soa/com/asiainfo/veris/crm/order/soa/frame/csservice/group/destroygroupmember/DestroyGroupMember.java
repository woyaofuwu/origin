
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.destroygroupmember;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.pub.frame.bcf.group.common.GroupBaseConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.MFileInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.AttrBizInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductCompInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductMebInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.PayRelaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserPayItemInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserPayPlanInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserPlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSpecialPayInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserVpnInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.MemberBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.ElementModel;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.ElementUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.GrpPfUtil;

public class DestroyGroupMember extends MemberBean
{
    protected DestroyGroupMemberReqData reqData = null;

    /**
     * 处理付费关系()
     * 
     * @throws Exception
     */
    protected void actTradePayRela() throws Exception
    {
        String userId = reqData.getUca().getUserId();
        String userIdA = reqData.getGrpUca().getUserId();

        // 查询用户付费计划
        IDataset userPayPlanList = UserPayPlanInfoQry.getGrpMemPayPlanByUserId(userId, userIdA);

        if (IDataUtil.isNotEmpty(userPayPlanList))
        {
            // 1、注销成员付费计划
            IData userPayPlanData = userPayPlanList.getData(0);
            userPayPlanData.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
            userPayPlanData.put("END_DATE", diversifyBooking ? SysDateMgr.getLastDateThisMonth() : getAcceptTime());

            super.addTradeUserPayplan(userPayPlanData);

            // 2、注销付费关系表和代付表
            if ("G".equals(userPayPlanData.getString("PLAN_TYPE_CODE")))
            {
                String endCycleId = SysDateMgr.getNowCyc();
                String planId = userPayPlanData.getString("PLAN_ID");

                IDataset userPayItemList = UserPayItemInfoQry.getUserPayItemByPlayId(userId, planId, null);

                if (IDataUtil.isNotEmpty(userPayItemList))
                {
                    for (int i = 0, row = userPayItemList.size(); i < row; i++)
                    {
                        IData userPayItemData = userPayItemList.getData(i);
                        userPayItemData.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                        userPayItemData.put("END_CYCLE_ID", endCycleId);
                    }

                    super.addTradeUserPayitem(userPayItemList);
                }

                // 查询代付表
                IDataset userSpecialPayList = UserSpecialPayInfoQry.qryUserSpecialPay(userId, userIdA);

                if (IDataUtil.isEmpty(userSpecialPayList))
                {
                    return;
                }

                // 信控数据处理
                boolean flag = false;

                if (StringUtils.isNotBlank(reqData.getGrpUca().getProductId()))
                {
                    IDataset attrBizList = AttrBizInfoQry.getBizAttr(reqData.getGrpUca().getProductId(), "P", "PayRelChg", "EsopBat", null);

                    if (IDataUtil.isNotEmpty(attrBizList))
                    {
                        flag = true;
                    }
                }

                for (int i = 0, row = userSpecialPayList.size(); i < row; i++)
                {
                    IData userSpecialPayData = userSpecialPayList.getData(i);
                    userSpecialPayData.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                    userSpecialPayData.put("END_CYCLE_ID", endCycleId);

                    // 注销特殊付费信息
                    super.addTradeUserSpecialepay(userSpecialPayData);

                    String acctId = userSpecialPayData.getString("ACCT_ID");
                    String payitemCode = userSpecialPayData.getString("PAYITEM_CODE");

                    IDataset userPayRelaList = PayRelaInfoQry.getPyrlByPk(userId, acctId, payitemCode, userSpecialPayData.getString("START_CYCLE_ID"), null);

                    IDataset payRelaList = new DatasetList();

                    // 信控处理act_tag = '2'的数据
                    if (flag)
                    {
                        String startCycleId = "";
                        IDataset allPyaRelaList = PayRelaInfoQry.getPayrelationByUserIdAndAcctId(acctId, userId, null);

                        if (IDataUtil.isNotEmpty(userPayRelaList))
                        {
                            startCycleId = userPayRelaList.getData(0).getString("START_CYCLE_ID", "");
                        }

                        for (int j = 0, size = allPyaRelaList.size(); j < size; j++)
                        {
                            IData payRelaData = allPyaRelaList.getData(j);
                            // 分散账期改动
                            String lastCycleThisMonth = SysDateMgr.getLastCycleThisMonth();

                            if (StringUtils.isNotBlank(startCycleId) && !startCycleId.equals(payRelaData.getString("START_CYCLE_ID", "")) && payRelaData.getString("END_CYCLE_ID").compareTo(lastCycleThisMonth) > 0)
                            {
                                // 删除付费关系
                                payRelaData.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                                payRelaData.put("END_CYCLE_ID", endCycleId);

                                payRelaList.add(payRelaData);
                            }
                        }
                    }

                    for (int k = 0, kRow = userPayRelaList.size(); k < kRow; k++)
                    {
                        IData userPayRelaData = userPayRelaList.getData(0);
                        userPayRelaData.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                        userPayRelaData.put("END_CYCLE_ID", endCycleId);

                        // 删除付费关系表
                        payRelaList.add(userPayRelaData);
                    }

                    super.addTradePayrelation(payRelaList);
                }

            }
        }

    }

    /**
     * 处理产品和产品参数信息
     * 
     * @throws Exception
     */
    protected void actTradePrdAndPrdParam() throws Exception
    {
    	//String productId = ProductMebInfoQry.getMemberMainProductByProductId(reqData.getGrpUca().getProductId());
    	String strBrandCode = reqData.getGrpUca().getBrandCode();
    	IDataset userProductList = new DatasetList();
    	if("PWLW".equals(strBrandCode) || "WLWG".equals(strBrandCode))
    	{
    		
    		userProductList = UserProductInfoQry.qryGrpMebProductEnd(reqData.getUca().getUserId(), reqData.getGrpUca().getUserId());
    		
    	}else{
    		
    		userProductList = UserProductInfoQry.qryGrpMebProduct(reqData.getUca().getUserId(), reqData.getGrpUca().getUserId());
    		
    	}
        

        for (int i = 0, row = userProductList.size(); i < row; i++)
        {
            // 注销产品信息
            IData userProductData = userProductList.getData(i);
            userProductData.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
            userProductData.put("END_DATE", diversifyBooking ? SysDateMgr.getLastDateThisMonth() : getAcceptTime());

            super.addTradeProduct(userProductData);

            // 注销成员主产品参数
            if (GroupBaseConst.PRODUCT_MODE.MEM_MAIN_PLUS_PRODUCT.getValue().equals(userProductData.getString("PRODUCT_MODE")))
            {
                IDataset userAttrList = UserAttrInfoQry.qryUserAttrByUserRelaInstId(reqData.getUca().getUserId(), userProductData.getString("INST_ID"));

                if (IDataUtil.isEmpty(userAttrList))
                {
                    continue;
                }

                for (int j = 0, jRow = userAttrList.size(); j < jRow; j++)
                {
                    IData userAttrData = userAttrList.getData(j);

                    userAttrData.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                    userAttrData.put("END_DATE", diversifyBooking ? SysDateMgr.getLastDateThisMonth() : getAcceptTime());
                }

                super.addTradeAttr(userAttrList);
            }
        }
    }

    /**
     * 注销UU关系
     * 
     * @throws Exception
     */
    protected void actTradeRelationUU() throws Exception
    {
        String relationTypeCode = ProductCompInfoQry.getRelaTypeCodeByProductId(reqData.getGrpUca().getProductId());

        IDataset relaList = RelaUUInfoQry.qryUU(reqData.getGrpUca().getUser().getUserId(), reqData.getUca().getUserId(), relationTypeCode, null);

        if (IDataUtil.isEmpty(relaList))
        {
            return;
        }

        IData relaData = relaList.getData(0);
        relaData.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
        relaData.put("END_DATE", diversifyBooking ? SysDateMgr.getLastDateThisMonth() : getAcceptTime());

        super.addTradeRelation(relaData);
    }
    
    @Override
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();

        // 产品子表、产品参数表
        actTradePrdAndPrdParam();

        // 服务状态表
        super.actTradeSvcState();

        // 关系表
        actTradeRelationUU();

        // 付费计划表、付费关系表
        actTradePayRela();

        insertMebUploadFiles();
    }

    /**
     * 处理成员退订的时候是否退出集团成员
     * 
     * @param map
     * @throws Exception
     */
    protected void dealJoin_in(IData map) throws Exception
    {
        String join_in = map.getString("JOIN_IN", "");

        // 1 退出集团
        if ("1".equals(join_in))
        {
            setProcessTag(1, "1");
        }
    }

    @Override
    protected BaseReqData getReqData() throws Exception
    {
        return new DestroyGroupMemberReqData();
    }

    /**
     * 成员VPN子表
     * 
     * @param Datas
     *            VPN参数
     * @author liaoyi
     * @throws Exception
     */
    public void infoRegDataVpn() throws Exception
    {

        IData inmap = new DataMap();
        inmap.put("USER_ID", reqData.getUca().getUserId());
        inmap.put("USER_ID_A", reqData.getGrpUca().getUserId());

        IData vpnInfo = UserVpnInfoQry.getMemberVpnByUserId(inmap);
        if (IDataUtil.isNotEmpty(vpnInfo)) // 成员登记的VPN个性信息台帐信息
        {
            vpnInfo.put("REMOVE_TAG", "1");// 注销标志：0-正常、1-已注销
            vpnInfo.put("REMOVE_DATE", diversifyBooking ? SysDateMgr.getLastDateThisMonth() : getAcceptTime());// 注销时间
            vpnInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
            this.addTradeVpnMeb(vpnInfo);
        }
    }

    @Override
    public String getProductInstId() throws Exception
    {
        IDataset productS =  bizData.getTradeProduct();
        if(IDataUtil.isEmpty(productS))
        {
            IDataset userProductList = null;
            String strBrandCode = reqData.getGrpUca().getBrandCode();
            if("PWLW".equals(strBrandCode) || "WLWG".equals(strBrandCode))
            {
                
                userProductList = UserProductInfoQry.qryGrpMebProductEnd(reqData.getUca().getUserId(), reqData.getGrpUca().getUserId());
                
            }else{
                
                userProductList = UserProductInfoQry.qryGrpMebProduct(reqData.getUca().getUserId(), reqData.getGrpUca().getUserId());
                
            }
            
            if(IDataUtil.isEmpty(userProductList))
            {
                return "";
            }
            else
            {
                return userProductList.getData(0).getString("INST_ID", "");
            }
            
        }
        else
        {
            return productS.getData(0).getString("INST_ID","");
        }
    }
    
    @Override
    protected final void initProductCtrlInfo() throws Exception
    {
        String productId = reqData.getGrpUca().getProductId();

        getProductCtrlInfo(productId, BizCtrlType.DestoryMember);
    }

    @Override
    protected void initReqData() throws Exception
    {
        super.initReqData();

        reqData = (DestroyGroupMemberReqData) getBaseReqData();
    }

    protected void makInit(IData map) throws Exception
    {
        if (StringUtils.isNotEmpty(map.getString("PRODUCT_PRE_DATE")) || reqData.isIfBooking())
        {
            diversifyBooking = true;

            reqData.setDiversifyBooking(diversifyBooking);
        }
        
        reqData.setMebFileShow(map.getString("MEB_FILE_SHOW",""));        
        reqData.setMebFileList(map.getString("MEB_FILE_LIST", ""));

        reqData.setEos(map.getDataset("EOS"));  // EOS 成员注销，需要入tradeExt表  add by zhengkai5
        
    }

    @Override
    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);

        dealJoin_in(map); // 处理成员退订的时候是否退出集团成员

        makReqDataElement();
    }

    /**
     * 注销产品元素
     * 
     * @throws Exception
     */
    public void makReqDataElement() throws Exception
    {
        IDataset svcList = new DatasetList();
        IDataset discntList = new DatasetList();
        IDataset spSvcList = new DatasetList();
        IDataset resList = new DatasetList();
        IDataset paramList = new DatasetList();

        // 成员基本产品
        String baseMebProductId = ProductMebInfoQry.getMemberMainProductByProductId(reqData.getGrpUca().getProductId());

        reqData.setBaseMebProductId(baseMebProductId);
        
        String strBrandCode = reqData.getGrpUca().getBrandCode();
        IDataset userElementList =  new DatasetList();
    	if(StringUtils.equals("PWLW", strBrandCode) || StringUtils.equals("WLWG", strBrandCode))
    	{
    		// 物联网品牌
    		userElementList = ProductInfoQry.qryPwlwProductElement(reqData.getUca().getUserId(), reqData.getGrpUca().getUserId());
    		
    	}else{
    		// 查询成员用户服务、资费和资源信息
    		userElementList = ProductInfoQry.qryUserProductElement(reqData.getUca().getUserId(), reqData.getGrpUca().getUserId());
    		
    	}

        // 查询成员用户平台服务信息
        IDataset userSpSvcList = UserPlatSvcInfoQry.getGrpPlatSvcByUserId(reqData.getUca().getUserId(), reqData.getGrpUca().getProductId());

        if (IDataUtil.isNotEmpty(userSpSvcList))
        {
            userElementList.addAll(userSpSvcList);
        }

        // 处理元素信息
        for (int i = 0, row = userElementList.size(); i < row; i++)
        {
            IData userElementData = userElementList.getData(i); // 取每个元素

            if (StringUtils.equals(BofConst.ELEMENT_TYPE_CODE_RES, userElementData.getString("ELEMENT_TYPE_CODE")))
            {
                userElementData.put("END_DATE", getAcceptTime());
            }
            else 
            {
                ElementModel model = new ElementModel(userElementData);

                // 获取元素结束时间
                String cancelDate = ElementUtil.getCancelDateForDstMb(model, SysDateMgr.getSysTime());
                //String cancelDate = ElementUtil.getCancelDateForDstMb(model, getAcceptTime());

                // 关于优化M2M套餐生效规则的需求 start
                if (("D".equals(userElementData.getString("ELEMENT_TYPE_CODE")) && "70500102".equals(userElementData.getString("PACKAGE_ID")))
                        || ("S".equals(userElementData.getString("ELEMENT_TYPE_CODE")) && "70500101".equals(userElementData.getString("PACKAGE_ID"))))
                { // 优惠
                    userElementData.put("END_DATE", SysDateMgr.getLastDateThisMonth());
                }
                else
                {
                    userElementData.put("END_DATE", diversifyBooking ? SysDateMgr.getLastDateThisMonth() : cancelDate);
                }
            }
            //关于优化M2M套餐生效规则的需求  end  
            userElementData.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
            userElementData.put("DIVERSIFY_ACCT_TAG", "1"); // 加入分散账期处理标记

            String elementType = userElementData.getString("ELEMENT_TYPE_CODE", "");
            String elementId = userElementData.getString("ELEMENT_ID");

            if (elementType.equals("S")) // 服务
            {
                String isNeedPf = GrpPfUtil.getSvcPfState(userElementData.getString("MODIFY_TAG"), reqData.getUca().getUserId(), elementId);

                userElementData.put("IS_NEED_PF", isNeedPf);

                svcList.add(userElementData);
            }
            else if (elementType.equals("D")) // 优惠
            {
                discntList.add(userElementData);
            }
            else if (elementType.equals("R")) // 资源
            {
                resList.add(userElementData);
            }

            else if (elementType.equals("Z")) // SP服务
            {
                userElementData.put("SERVICE_ID", elementId);

                spSvcList.add(userElementData);
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

                    paramList.addAll(userAttrList);
                }
            }
        }

        // 设置数据
        reqData.cd.putSvc(svcList);
        reqData.cd.putSpSvc(spSvcList);
        reqData.cd.putDiscnt(discntList);
        reqData.cd.putRes(resList);
        reqData.cd.putElementParam(paramList);
    }

    @Override
    protected final void makUca(IData map) throws Exception
    {
        this.makUcaForMebNormal(map);
    }

    /**
     * 
     * @throws Exception
     */
    private void insertMebUploadFiles() throws Exception
    {
    	
        String mebFileShow = reqData.getMebFileShow();
        if(StringUtils.isNotBlank(mebFileShow) 
        		&& StringUtils.equals("true", mebFileShow)){
            
            String fileList = reqData.getMebFileList();
            if(StringUtils.isNotEmpty(fileList)){
            	
            	String userIdb = reqData.getUca().getUserId();//成员user_id
                String serialNumberB = reqData.getUca().getSerialNumber();//成员号码
                String partitionId = StrUtil.getPartition4ById(userIdb);
                
                String serialNumberA = reqData.getGrpUca().getSerialNumber();//成员号码
                String groupId = reqData.getGrpUca().getCustGroup().getGroupId();
                String custName = reqData.getGrpUca().getCustGroup().getCustName();
                String staffId =  CSBizBean.getVisit().getStaffId();
                String createTime = SysDateMgr.getSysTime();
                String productId = reqData.getGrpProductId();
                String tradeTypeCode = reqData.getTradeType().getTradeTypeCode();
                String tradeTypeName = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_TRADETYPE", "TRADE_TYPE_CODE", "TRADE_TYPE", tradeTypeCode);
                
            	String[] fileArray = fileList.split(",");
            	for (int i = 0; i < fileArray.length; i++)
                {
            		IData fileData = new DataMap();
            		String fileId = fileArray[i];
            		
	        		String fileName = "";
	                IDataset fileDatas = MFileInfoQry.qryFileInfoListByFileID(fileId);
	                if(IDataUtil.isNotEmpty(fileDatas)){
	                     fileName = fileDatas.getData(0).getString("FILE_NAME","");
	                }
                 
	                fileData.put("PARTITION_ID",  partitionId);
	                fileData.put("USER_ID",  userIdb);
                    fileData.put("FILE_ID",  fileId);
                    fileData.put("GROUP_ID",  groupId);
                    fileData.put("SERIAL_NUMBER_A",  serialNumberA);
                    fileData.put("GROUP_ID",  groupId);
                    fileData.put("CUST_NAME",  custName);
                    fileData.put("PRODUCT_ID",  productId);
                    fileData.put("CREATE_STAFF", staffId);
                    fileData.put("CREATE_TIME",  createTime);
                    fileData.put("TRADE_TYPE_CODE",  tradeTypeCode);
                    fileData.put("TRADE_TYPE",  tradeTypeName);
                    fileData.put("TRADE_TAG",  "3");
                    fileData.put("TRADE_ID",  getTradeId());
                    fileData.put("FILE_NAME", fileName);
                    fileData.put("SERIAL_NUMBER_B",  serialNumberB);
                    
                    Dao.insert("TF_F_GROUP_FTPFILE", fileData, Route.getCrmDefaultDb());
                    
                }
            }
        }
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
