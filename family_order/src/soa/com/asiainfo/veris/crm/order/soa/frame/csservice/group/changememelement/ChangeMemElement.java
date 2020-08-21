
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.changememelement;

import java.util.Iterator;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.group.common.base.GrpModuleData;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.MFileInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductCompInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductMebInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.SvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.MemberBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.GroupModuleParserBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.GrpAttrUtil;

public class ChangeMemElement extends MemberBean
{
    protected GrpModuleData moduleData = new GrpModuleData();

    protected ChangeMemElementReqData reqData = null;

    protected void actTradePrd() throws Exception
    {
        IData productIdset = reqData.cd.getProductIdSet();

        if (IDataUtil.isEmpty(productIdset))
        {
            return;
        }

        IDataset productInfoset = new DatasetList();
        Iterator<String> iterator = productIdset.keySet().iterator();
        while (iterator.hasNext())
        {
            String key = iterator.next();
            String state = productIdset.getData(key).getString("MODIFY_TAG");
            if (state.equals(TRADE_MODIFY_TAG.Add.getValue()))
            {
                String userId = reqData.getUca().getUser().getUserId();
                String userIdA = reqData.getGrpUca().getUser().getUserId();

                // 判断是否订购过这个产品
                IDataset products = UserProductInfoQry.qryGrpMebProduct(userId, userIdA);
                products = DataHelper.filter(products, "PRODUCT_ID=" + key);
                if (IDataUtil.isNotEmpty(products))
                    continue;

                IData productPlus = new DataMap();

                productPlus.put("USER_ID", reqData.getUca().getUserId());
                productPlus.put("USER_ID_A", reqData.getGrpUca().getUserId());

                productPlus.put("PRODUCT_ID", key); // 产品标识
                productPlus.put("PRODUCT_MODE", productIdset.getData(key).getString("PRODUCT_MODE"));// 产品模式
                productPlus.put("BRAND_CODE", UProductInfoQry.getBrandCodeByProductId(key));

                productPlus.put("INST_ID", SeqMgr.getInstId()); // 实例标识
                productPlus.put("START_DATE", getAcceptTime()); // 开始时间
                productPlus.put("END_DATE", SysDateMgr.getTheLastTime()); // 结束时间
                productPlus.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());

                productInfoset.add(productPlus);
            }
            else if (state.equals(TRADE_MODIFY_TAG.DEL.getValue()))
            {
                IData inparams = new DataMap();

                inparams.put("USER_ID", reqData.getUca().getUserId());
                inparams.put("USER_ID_A", reqData.getGrpUca().getUser().getUserId());

                inparams.put("PRODUCT_ID", key);
                inparams.put("PRODUCT_MODE", productIdset.getData(key).getString("PRODUCT_MODE"));

                IDataset userProducts = UserProductInfoQry.GetUserProductInfo(reqData.getUca().getUserId(), reqData.getGrpUca().getUser().getUserId(), key, productIdset.getData(key).getString("PRODUCT_MODE"), null);

                if (IDataUtil.isEmpty(userProducts))
                    continue;

                IData userProduct = userProducts.getData(0);
                userProduct.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                userProduct.put("END_DATE", getAcceptTime());

                productInfoset.add(userProduct);
            }
        }

        super.addTradeProduct(productInfoset);
    }

    protected void actTradePrdParam() throws Exception
    {
        String mebBaseProductId = ProductMebInfoQry.getMemberMainProductByProductId(reqData.getGrpUca().getProductId());

        IData productParam = reqData.cd.getProductParamMap(mebBaseProductId);

        if (IDataUtil.isEmpty(productParam))
        {
            return;
        }

        IData userProductData = UserProductInfoQry.getUserProductBykey(reqData.getUca().getUserId(), mebBaseProductId, reqData.getGrpUca().getUserId(), null);

        String instId = userProductData.getString("INST_ID");

        IData param = new DataMap();
        param.put("START_DATE", getAcceptTime());
        param.put("END_DATE", SysDateMgr.getTheLastTime());

        // 处理产品参数信息
        IDataset productParamList = GrpAttrUtil.dealAttrParam(productParam, reqData.getUca().getUserId(), "P", instId, param);

        // 过滤以NOTIN_开头的属性，这种属性不需要插表
        super.filterParamAttr("NOTIN_", productParamList);
        if (IDataUtil.isNotEmpty(productParamList))
        {
            super.addTradeAttr(productParamList);
        }
    }

    protected void actTradeRelationUU() throws Exception
    {
        String role_code_b = reqData.getMemRoleB();

        if (StringUtils.isEmpty(role_code_b))
            return;

        IDataset relaUUList = RelaUUInfoQry.getUUInfoByUserIdAB(reqData.getUca().getUserId(), reqData.getGrpUca().getUserId(), ""); // 变更的时候查UU关系必须有一条记录，这里不校验为空的情况，然这种情况报错，修数据
        if (IDataUtil.isEmpty(relaUUList))
            return;

        IData relaUU = relaUUList.getData(0);

        if (StringUtils.equals(role_code_b, relaUU.getString("ROLE_CODE_B")))
            return;

        relaUU.put("ROLE_CODE_B", role_code_b);
        relaUU.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());

        super.addTradeRelation(relaUU);

        reqData.setIsChange(true);
    }
    
    @Override
    public void actTradeSub() throws Exception
    {

        super.actTradeSub();

        // 产品和个性化参数
        actTradePrdParam();

        // 产品子表
        actTradePrd();

        // 处理UU关系
        actTradeRelationUU();

        // 服务状态表
        super.actTradeSvcState();

        insertMebUploadFiles();
        
    }

    @Override
    protected BaseReqData getReqData() throws Exception
    {
        return new ChangeMemElementReqData();
    }

    @Override
    protected void initProductCtrlInfo() throws Exception
    {

        String productId = reqData.getGrpUca().getProductId();
        getProductCtrlInfo(productId, BizCtrlType.ChangeMemberDis);
    }

    @Override
    protected void initReqData() throws Exception
    {
        super.initReqData();

        reqData = (ChangeMemElementReqData) getBaseReqData();
    }
    
    @Override
    protected void makInit(IData map) throws Exception
    {
        super.makInit(map);

        moduleData.getMoudleInfo(map);
        
        reqData.setMebFileShow(map.getString("MEB_FILE_SHOW",""));        
        reqData.setMebFileList(map.getString("MEB_FILE_LIST", ""));
    }

    @Override
    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);

        reqData.setMemRoleB(map.getString("MEM_ROLE_B"));

        makReqDataElement();
    }

    /**
     * 解析产品元素信息
     * 
     * @throws Exception
     */
    public void makReqDataElement() throws Exception
    {
        // 解析产品元素
        GroupModuleParserBean.mebElement(reqData, moduleData);

        // 解析产品参数
        makReqDataProductParam();

        // 解析资源
        GroupModuleParserBean.mebRes(reqData, moduleData);
    }

    public void makReqDataProductParam() throws Exception
    {
        // 产品参数
        IDataset productParamList = moduleData.getProductParamInfo();// reqInfos.getDataset("PRODUCT_PARAM_INFO");

        // 处理用户产品和产品参数
        for (int i = 0, size = productParamList.size(); i < size; i++)
        {
            // 产品参数
            IData paramData = productParamList.getData(i);

            String baseMemProduct = ProductMebInfoQry.getMemberMainProductByProductId(reqData.getGrpUca().getProductId());

            IDataset productAttr = paramData.getDataset("PRODUCT_PARAM");

            reqData.cd.putProductParamList(baseMemProduct, productAttr);
        }
    }

    @Override
    protected void makUca(IData map) throws Exception
    {
        this.makUcaForMebNormal(map);
    }

    protected void modTradeData() throws Exception
    {
        super.modTradeData();

        if (reqData.getIsChange())
        {
            return;
        }

        String allTables = bizData.getTrade().getString("INTF_ID");

        if (allTables.contains(TradeTableEnum.TRADE_SVC.getValue()))
        {
            return;
        }
        if (allTables.contains(TradeTableEnum.TRADE_DISCNT.getValue()))
        {
            return;
        }
        if (allTables.contains(TradeTableEnum.TRADE_RES.getValue()))
        {
            return;
        }
        if (allTables.contains(TradeTableEnum.TRADE_ATTR.getValue()))
        {
            return;
        }
        if (allTables.contains(TradeTableEnum.TRADE_OTHER.getValue()))
        {
            return;
        }
        if (allTables.contains(TradeTableEnum.TRADE_POST.getValue()))
        {
            return;
        }
        if (allTables.contains(TradeTableEnum.TRADE_PRODUCT.getValue()))
        {
            return;
        }
        if (allTables.contains(TradeTableEnum.TRADE_PLATSVC.getValue()))
        {
            return;
        }
        if (allTables.contains(TradeTableEnum.TRADE_GRP_MEB_PLATSVC.getValue()))
        {
            return;
        }
        if (allTables.contains(TradeTableEnum.TRADE_BLACKWHITE.getValue()))
        {
            return;
        }
        if (allTables.contains(TradeTableEnum.TRADE_PAYRELATION.getValue()))
        {
            return;
        }

        CSAppException.apperr(GrpException.CRM_GRP_419);

    }

    @Override
    protected void setTradeAttr(IData map) throws Exception
    {
        super.setTradeAttr(map);

        map.put("USER_ID", reqData.getUca().getUserId());

        map.put("USER_ID_A", map.getString("USER_ID_A", reqData.getGrpUca().getUserId())); // 用户标识A
    }

    @Override
    protected void setTradeDiscnt(IData map) throws Exception
    {
        super.setTradeDiscnt(map);
        
        map.put("USER_ID", map.getString("USER_ID", reqData.getUca().getUserId()));// 用户标识
        map.put("USER_ID_A", map.getString("USER_ID_A", reqData.getGrpUca().getUserId()));// 用户标识A：关联服务中的A用户标识，通常为一集团用户或虚拟用户。对于非关联服务填-1。

        map.put("SPEC_TAG", map.getString("SPEC_TAG", "2")); // 特殊优惠标记：0-正常产品优惠，1-特殊优惠，2-关联优惠。
        String relationTypeCode = ProductCompInfoQry.getRelaTypeCodeByProductId(reqData.getGrpUca().getProductId());
        map.put("RELATION_TYPE_CODE", map.getString("RELATION_TYPE_CODE", relationTypeCode)); // 关系类型
    }

    @Override
    protected void setTradefeeDefer(IData map) throws Exception
    {
        super.setTradefeeDefer(map);

        map.put("USER_ID", reqData.getUca().getUserId());// 用户标识
    }

    @Override
    protected void setTradefeePaymoney(IData map) throws Exception
    {
        super.setTradefeePaymoney(map);

        map.put("USER_ID", reqData.getUca().getUserId());// 用户标识
    }

    @Override
    protected void setTradefeeSub(IData map) throws Exception
    {
        super.setTradefeeSub(map);

        map.put("USER_ID", reqData.getUca().getUserId());// 用户标识
    }

    @Override
    protected void setTradeOther(IData map) throws Exception
    {
        super.setTradeOther(map);

        map.put("USER_ID", map.getString("USER_ID", reqData.getUca().getUserId()));

        map.put("PROCESS_TAG", map.getString("TRADE_TAG", "")); // 处理标志

        map.put("INST_ID", map.getString("INST_ID", SeqMgr.getInstId()));
    }

    @Override
    protected void setTradePlatsvcAttr(IData map) throws Exception
    {
        super.setTradePlatsvcAttr(map);
        map.put("USER_ID", map.getString("USER_ID", reqData.getUca().getUserId())); // 用户标识
        map.put("SERIAL_NUMBER", map.getString("SERIAL_NUMBER", reqData.getUca().getSerialNumber()));
    }

    @Override
    protected void setTradeProduct(IData map) throws Exception
    {
        super.setTradeProduct(map);
        
        map.put("USER_ID", map.getString("USER_ID", reqData.getUca().getUserId()));// 用户标识);// 用户标识
        map.put("USER_ID_A", map.getString("USER_ID_A", reqData.getGrpUca().getUserId()));
        // 用户标识A：关联服务中的A用户标识，通常为一集团用户或虚拟用户。对于非关联服务填-1。

        String productId = map.getString("PRODUCT_ID");
        map.put("PRODUCT_ID", productId); // 产品标识

        map.put("PRODUCT_MODE", map.getString("PRODUCT_MODE", "00")); // 产品的模式：00:基本产品，01:附加产品

        String brandCode = UProductInfoQry.getBrandCodeByProductId(productId);
        map.put("BRAND_CODE", brandCode); // 品牌编码

        map.put("INST_ID", map.getString("INST_ID", "0")); // 实例标识

        map.put("START_DATE", map.getString("START_DATE", SysDateMgr.getSysTime())); // 开始时间
        map.put("END_DATE", map.getString("END_DATE", SysDateMgr.getTheLastTime())); // 结束时间

        map.put("MAIN_TAG", map.getString("MAIN_TAG", "0"));// 主产品标记：0-否，1-是
    }

    @Override
    protected void setTradeRelation(IData map) throws Exception
    {

        super.setTradeRelation(map);

        map.put("USER_ID_A", map.getString("USER_ID_A", reqData.getGrpUca().getUserId())); // A用户标识：对应关系类型参数表中的A角，通常为一集团用户或虚拟用户
        map.put("SERIAL_NUMBER_A", map.getString("SERIAL_NUMBER_A", reqData.getGrpUca().getSerialNumber())); // A服务号码
        map.put("USER_ID_B", map.getString("USER_ID_B", reqData.getUca().getUserId())); // B用户标识：对应关系类型参数表中的B角，通常为普通用户
        map.put("SERIAL_NUMBER_B", map.getString("SERIAL_NUMBER_B", reqData.getUca().getSerialNumber())); // B服务号码
        map.put("MODIFY_TAG", map.getString("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue()));
    }

    @Override
    protected void setTradeRes(IData map) throws Exception
    {
        super.setTradeRes(map);

        map.put("USER_ID", reqData.getUca().getUser().getUserId());
        map.put("USER_ID_A", reqData.getGrpUca().getUser().getUserId());
    }

    @Override
    protected void setTradeSvc(IData map) throws Exception
    {
        super.setTradeSvc(map);

        map.put("USER_ID", reqData.getUca().getUser().getUserId());
        map.put("USER_ID_A", reqData.getGrpUca().getUser().getUserId());// 用户标识A：关联服务中的A用户标识，通常为一集团用户或虚拟用户。对于非关联服务填-1。

        map.put("SERVICE_ID", map.getString("ELEMENT_ID"));
        String mainTag = SvcInfoQry.queryMainTagByPackageIdAndServiceId(map.getString("PRODUCT_ID"), map.getString("PACKAGE_ID"), map.getString("ELEMENT_ID"));
        map.put("MAIN_TAG", mainTag);// 主体服务标志：0-否，1-是

        map.put("PACKAGE_ID", map.getString("PACKAGE_ID", "0"));// 包标识

    }

    @Override
    protected void setTradeUser(IData map) throws Exception
    {
        super.setTradeUser(map);

        map.put("CUST_ID", reqData.getUca().getCustPerson().getCustId());
        map.put("USER_ID", reqData.getUca().getUser().getUserId());

        map.put("EPARCHY_CODE", CSBizBean.getUserEparchyCode());
        map.put("CITY_CODE", CSBizBean.getVisit().getCityCode());

        map.put("USER_PASSWD", ""); // 用户密码

        map.put("IN_DATE", getAcceptTime());
        map.put("IN_STAFF_ID", CSBizBean.getVisit().getStaffId());
        map.put("IN_DEPART_ID", CSBizBean.getVisit().getDepartId());

        map.put("OPEN_DATE", getAcceptTime());
        map.put("OPEN_STAFF_ID", CSBizBean.getVisit().getStaffId());
        map.put("OPEN_DEPART_ID", CSBizBean.getVisit().getDepartId());

        map.put("DEVELOP_STAFF_ID", CSBizBean.getVisit().getStaffId()); // 发展渠道
        map.put("DEVELOP_DEPART_ID", CSBizBean.getVisit().getDepartId()); // 发展业务区

        map.put("REMOVE_TAG", "0");
    }

    @Override
    protected void setTradeUserPayplan(IData map) throws Exception
    {
        super.setTradeUserPayplan(map);

        map.put("USER_ID", map.getString("USER_ID", reqData.getUca().getUserId()));// 用户标识
        map.put("USER_ID_A", map.getString("USER_ID_A", reqData.getGrpUca().getUserId())); // 用户标识A
    }

    @Override
    protected void setTradeUserSpecialepay(IData map) throws Exception
    {
        super.setTradeUserSpecialepay(map);

        map.put("USER_ID", map.getString("USER_ID", reqData.getUca().getUserId()));// 用户标识
        map.put("USER_ID_A", map.getString("USER_ID_A", reqData.getGrpUca().getUserId())); // 用户标识A
    }

    @Override
    protected void setTradeVpnMeb(IData map) throws Exception
    {
        super.setTradeVpnMeb(map);

        map.put("USER_ID", map.getString("USER_ID", reqData.getUca().getUserId()));// 用户标识
        map.put("USER_ID_A", map.getString("USER_ID_A", reqData.getGrpUca().getUserId())); // 用户标识A
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
    
}
