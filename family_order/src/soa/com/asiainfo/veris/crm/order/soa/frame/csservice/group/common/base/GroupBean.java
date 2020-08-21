
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base;

import java.util.List;

import com.ailk.biz.BizEnv;
import com.ailk.biz.service.BizRoute;
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.ProvinceUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.pub.util.TaxCalcUtils;
import com.asiainfo.veris.crm.order.pub.util.TaxUtils;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bizctrl.BizCtrlInfo;
import com.asiainfo.veris.crm.order.soa.frame.bcf.group.common.base.BizData;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UBankInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UTradeTypeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UStaffInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.util.DataBusUtils;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.fee.PayMoneyData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.bof.query.BofQuery;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.GrpBaseAudiInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ElementTaxInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.FeeItemTaxInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ParamInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.sysorg.StaffInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.SmsSend;

public class GroupBean extends GroupTradeBaseBean
{
    protected GroupReqData reqData = null;

    public IData getProductInfoByElement(IDataset productInfoCaches, IData elementData) throws Exception
    {
        String productId = elementData.getString("PRODUCT_ID");
        if (StringUtils.isBlank(productId) || StringUtils.equals("-1", productId))
        {
            return null;
        }
        
        if (IDataUtil.isEmpty(productInfoCaches))
        {
            IDataset tradeProducts = bizData.getTradeProduct();
            IDataset userProducts = BofQuery.getUserAllProducts(elementData.getString("USER_ID"), BizRoute.getRouteId());
            
            IDataset future = new DatasetList();
            if (userProducts != null && tradeProducts != null)
            {
                future = DataBusUtils.getFuture(userProducts, tradeProducts, new String[]{ "INST_ID" });
                future = DataBusUtils.filterInValidDataByEndDate(future);
            }
            
            productInfoCaches.addAll(future);
        }
        
        for (int i = 0, size = productInfoCaches.size(); i < size; i++)
        {
            IData userProduct = productInfoCaches.getData(i);
            if (StringUtils.equals(userProduct.getString("PRODUCT_ID"), productId))
            {
                return userProduct;
            }
        }
        
        return null;
    }
    
    @Override
    protected void actTradeBase() throws Exception
    {
        super.actTradeBase();

        // ESOP信息登记
        actTradeEsopExt();
    }

    @Override
    protected void actTradeBefore() throws Exception
    {

        super.actTradeBefore();

    }

    private void actTradeEsopExt() throws Exception
    {

        IDataset dataset = reqData.cd.getEosParam();

        if (IDataUtil.isEmpty(dataset))
        {
            return;
        }

        for (int i = 0; i < dataset.size(); i++)
        {
            IData data = dataset.getData(i);
            data.put("RSRV_STR10", "EOS");
        }

        addTradeExt(dataset);
    }

    // 集团特殊处理
    protected void actTradePayMode() throws Exception
    {
        IDataset dataset = new DatasetList();

        List<PayMoneyData> payMoneyList = reqData.getPayMoneyList();

        if (payMoneyList == null || payMoneyList.size() == 0)
        {
            return;
        }

        for (PayMoneyData fd : payMoneyList)
        {
            dataset.add(fd.toData());
        }

        // 处理付费方式子表
        super.addTradefeePayMoney(dataset);

        IDataset deferDataset = new DatasetList();
        IDataset checkDataset = new DatasetList();

        for (int i = 0, row = dataset.size(); i < row; i++)
        {
            IData data = dataset.getData(i);

            // 得到付款方式
            String payMoneyCode = data.getString("PAY_MONEY_CODE");

            if (payMoneyCode.equals("Z"))
            {
                // 支票
                checkDataset.add(data);
            }
            else if (payMoneyCode.equals("A"))
            {
                // 挂帐
                deferDataset.add(data);
            }
        }

        // 处理支票子表
        if (checkDataset.size() > 0)
        {
            setRegCheck(checkDataset);
        }

        // 处理挂账子表
        if (deferDataset.size() > 0)
        {
            IDataset commParaInfos = CommparaInfoQry.getCommParas("CSM", "7788", reqData.getUca().getProductId(), "DEFER", CSBizBean.getUserEparchyCode());// 配置挂账信息

            if (IDataUtil.isNotEmpty(commParaInfos))
            {
                IData commParaInfo = commParaInfos.getData(0);
                for (int i = 0, size = deferDataset.size(); i < size; i++)
                {
                    IData deferData = deferDataset.getData(i);
                    deferData.put("ACT_TAG", commParaInfo.getString("PARA_CODE2", "1"));
                    deferData.put("FEE_MODE", commParaInfo.getString("PARA_CODE3", "0")); // 费用类型：0-营业费用项，1-押金，2-预存
                    deferData.put("FEE_TYPE_CODE", commParaInfo.getString("PARA_CODE4")); // 营业费用类型
                    deferData.put("DEFER_CYCLE_ID", commParaInfo.getString("PARA_CODE5", "-1")); // 账期
                    deferData.put("DEFER_ITEM_CODE", commParaInfo.getString("PARA_CODE6")); // 挂帐帐目:为明细帐目,不指定时挂到默认的帐目
                }
            }

            super.addTradefeeDefer(deferDataset);
        }
    }

    @Override
    protected void actTradeTaxLog() throws Exception
    {
        super.actTradeTaxLog();

        // 判断是否支持营改增
        if (TaxUtils.isYgzTag() == false)
        {
            return;
        }

        // 获取增值税信息
        IDataset tradeFeeTaxList = bizData.getTradeFeeTax();

        if (IDataUtil.isEmpty(tradeFeeTaxList))
        {
            return;
        }

        // 获取主台账信息
        IData tradeData = bizData.getTrade();

        // 打印过增值税发票的, 不允许返销, 即使要返销走冲红审批流程, 不打印
        if (!"0".equals(tradeData.getString("CANCEL_TAG", "0")))
        {
            return;
        }

        // 保存增值税业务受理信息
        IData taxLogData = new DataMap();

        String tradeTypeCode = tradeData.getString("TRADE_TYPE_CODE"); // 业务类型
        String tradeTypeName = UTradeTypeInfoQry.getTradeTypeName(tradeTypeCode); // 业务名称
        String operFee = tradeData.getString("OPER_FEE"); // 营业费用

        taxLogData.put("TRADE_TYPE_CODE", tradeTypeCode);
        taxLogData.put("TRADE_TYPE_NAME", tradeTypeName);
        taxLogData.put("CUST_ID", reqData.getUca().getCustId());
        taxLogData.put("CUST_NAME", reqData.getUca().getCustGroup().getCustName());
        taxLogData.put("ACCT_ID", reqData.getUca().getAcctId());
        taxLogData.put("SERIAL_NUMBER", reqData.getUca().getSerialNumber());
        taxLogData.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        taxLogData.put("FEE", operFee);
        taxLogData.put("ACCEPT_DATE", getAcceptTime());
        taxLogData.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        taxLogData.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
        taxLogData.put("CANCEL_TAG", tradeData.getString("CANCEL_TAG", "0"));

        super.addTradeTaxLog(taxLogData);
    }

    @Override
    public void callOutIntf() throws Exception
    {
        super.callOutIntf();
    }

    @Override
    protected void chkTradeAfter() throws Exception
    {
        super.chkTradeAfter();
    }

    @Override
    protected BaseReqData getReqData() throws Exception
    {
        return new GroupReqData();
    }

    @Override
    protected void getTradeAfterElementData(IData ruleParam, IData tradeAllData, IData tableDataClone) throws Exception
    {
        String userId = reqData.getUca().getUserId();
        String userIdA = "-1";
        ruleParam.put("USER_ID", userId);
        ruleParam.put("USER_ID_A", userIdA);
        super.getTradeAfterElementData(ruleParam, tradeAllData, tableDataClone);
    }

    @Override
    protected void initReqData() throws Exception
    {
        super.initReqData();

        reqData = (GroupReqData) getBaseReqData();
    }

    @Override
    protected void makInit(IData map) throws Exception
    {
        super.makInit(map);

        map.put("IS_GROUP_BIZ", true);// 集团操作
    }

    @Override
    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);

        reqData.setGrpProductId(reqData.getUca().getProductId());// 设置集团产品ID

        // 处理合同信息
        IData userInfo = map.getData("USER_INFO");
        if (IDataUtil.isNotEmpty(userInfo))
        {
            String contractId = userInfo.getString("CONTRACT_ID");
            if (StringUtils.isNotBlank(contractId))
            {
                reqData.setContractId(contractId);
            }
        }
    }

    protected final void makUcaForGrpNormal(IData map) throws Exception
    {
        UcaData uca = null;

        IData param = new DataMap();

        // trans
        if (map.containsKey("USER_ID"))
        {
            String userId = map.getString("USER_ID");
            param.put("USER_ID", userId);

            uca = UcaDataFactory.getNormalUcaByUserIdForGrp(param);
        }
        else if (map.containsKey("SERIAL_NUMBER"))
        {
            // 如果包含USER_ID,则转化为SERIAL_NUMBER查三户信息

            String sn = map.getString("SERIAL_NUMBER");
            param.put("SERIAL_NUMBER", sn);

            uca = UcaDataFactory.getNormalUcaBySnForGrp(param);// 这个有问题？uca为null
        }

        reqData.setUca(uca);

    }

    @Override
    protected void regTrade() throws Exception
    {
        super.regTrade();

        IData data = bizData.getTrade();

        data.put("CUST_ID", reqData.getUca().getGrpCustId()); // 客户标识
        data.put("CUST_NAME", reqData.getUca().getCustGroup().getCustName()); // 客户名称
        data.put("USER_ID", reqData.getUca().getUserId()); // 用户标识
        data.put("ACCT_ID", reqData.getUca().getAcctId()); // 帐户标识

        data.put("SERIAL_NUMBER", reqData.getUca().getSerialNumber()); // 服务号码

        data.put("EPARCHY_CODE", reqData.getUca().getUser().getEparchyCode()); // 归属地州
        data.put("CITY_CODE", reqData.getUca().getUser().getCityCode()); // 归属业务区
        data.put("PRODUCT_ID", reqData.getUca().getProductId()); // 产品标识
        data.put("BRAND_CODE", reqData.getUca().getBrandCode()); // 品牌编码

        data.put("CUST_ID_B", "-1"); // 客户标识B：关联业务中的B客户标识，通常为一集团客户或虚拟客户。对于非关联业务填-1。
        data.put("USER_ID_B", "-1"); // 用户标识B：关联业务中的B用户标识，通常为一集团用户或虚拟用户。对于非关联业务填-1。
        data.put("ACCT_ID_B", "-1"); // 
    }

    @Override
    protected String setOrderTypeCode() throws Exception
    {
        return "0";
    }

    protected void setSmsCfgData(IData cfgData) throws Exception
    {
        super.setSmsCfgData(cfgData);

        cfgData.put("PRODUCT_ID", reqData.getUca().getProductId());// 子类覆盖
    }

    // 此api湖南用,等湖南短信统一对象后,去掉
    @Override
    protected void setSmsVarData(BizData bizData, IData varName, IData varData) throws Exception
    {
        super.setSmsVarData(bizData, varName, varData);

        // 集团名称
        if (varName.containsKey("GROUP_NAME"))
        {
            String groupName = reqData.getUca().getCustGroup().getCustName();
            varData.put("GROUP_NAME", groupName);
        }

        // 集团编码
        if (varName.containsKey("GROUP_ID"))
        {
            String groupId = reqData.getUca().getCustGroup().getGroupId();
            varData.put("GROUP_ID", groupId);
        }

        // 产品名称
        String productId = "";
        if (varName.containsKey("PRODUCT_ID") || varName.containsKey("PRODUCT_NAME"))
        {
            productId = bizData.getTrade().getString("PRODUCT_ID");
            varData.put("PRODUCT_ID", productId);

            String product_name = UProductInfoQry.getProductNameByProductId(productId);
            varData.put("PRODUCT_NAME", varData.getString("PRODUCT_NAME", product_name));
        }

        // 品牌名称
        if (varName.containsKey("BRAND_NAME"))
        {
            String brandName = UBankInfoQry.getBankNameByBankCode(productId);
            varData.put("BRAND_NAME", brandName);
        }

        // 客户经理名称
        if (varName.containsKey("CUST_MANAGER_NAME"))
        {
            String custManagerId = reqData.getUca().getCustGroup().getCustManagerId();
            String custManagerName = UStaffInfoQry.getCustManageNameByCustManagerId(custManagerId);
            varData.put("CUST_MANAGER_NAME", custManagerName);
        }
        // 执行时间
        if (varName.containsKey("EXEC_TIME"))
        {
            varData.put("EXEC_TIME", reqData.getAcceptTime());
        }
    }

    @Override
    protected void setTradeFeeTax(IData map) throws Exception
    {
        super.setTradeFeeTax(map);

        String tradeTypeCode = getTradeTypeCode();
        String productId = reqData.getUca().getProductId();

        String elementId = map.getString("DISCNT_GIFT_ID");
        String feeMode = map.getString("FEE_MODE");
        String feeTypeCode = map.getString("FEE_TYPE_CODE");

        IDataset taxList = null;
        String type = CSBaseConst.TAX_TYPE.SALE.getValue();
        String rate = "0";
        String discount = "0";

        boolean elementIdFlag = true;

        if (ProvinceUtil.isProvince(ProvinceUtil.HAIN))
        {

            elementIdFlag = !"-1".equals(elementId);

        }
        if (StringUtils.isNotEmpty(elementId) && elementIdFlag) // 处理元素收费
        {
            taxList = ElementTaxInfoQry.qryTaxByElementId(tradeTypeCode, productId, elementId, feeMode, feeTypeCode, CSBizBean.getUserEparchyCode());
        }
        else
        // 处理产品收费
        {
            taxList = FeeItemTaxInfoQry.qryTaxByTradeProductFee(tradeTypeCode, productId, feeMode, feeTypeCode, CSBizBean.getUserEparchyCode());
        }

        if (IDataUtil.isNotEmpty(taxList))
        {
            IData elementTaxData = taxList.getData(0);
            type = elementTaxData.getString("TYPE", CSBaseConst.TAX_TYPE.SALE.getValue());
            rate = elementTaxData.getString("RATE", "0");
            discount = elementTaxData.getString("DISCOUNT", "0");
        }

        map.put("TYPE", type);
        map.put("RATE", rate);
        map.put("FACT_PAY_FEE", map.getString("FEE"));
        map.put("DISCOUNT", discount);

        // 计算税率
        TaxCalcUtils.getTradeFeeTaxForCalculate(IDataUtil.idToIds(map));
    }

    @Override
    protected String setTradeTypeCode() throws Exception
    {

        // 设置产品信息
        String productId = reqData.getUca().getProductId();

        // 得到产品控制信息
        BizCtrlInfo productCtrlInfo = reqData.getProductCtrlInfo(productId);

        // 得到业务类型
        String tradeTypeCode = productCtrlInfo.getTradeTypeCode();

        // 设置业务类型
        return tradeTypeCode;
    }

    @Override
    protected void updTradeProcessTagSet() throws Exception
    {
        super.updTradeProcessTagSet();

        IDataset eosDataset = reqData.cd.getEosParam();

        if (IDataUtil.isNotEmpty(eosDataset))
        {
            setProcessTag(6, "1");
        }
    }
    
    /**
     * 生成集团业务稽核工单
     * REQ201804280001集团合同管理界面优化需求
     * @param map
     * @throws Exception
     * @author chenzg
     * @date 2018-7-3
     */
    protected void actGrpBizBaseAudit(IData map) throws Exception
    {
    	boolean actVoucherFlag = BizEnv.getEnvBoolean("grp.biz.audit", false);
        if(actVoucherFlag){
        	/*集团产品增删改业务不需要上传凭证,但需要生成稽核工单*/
        	String auditStaffId = map.getString("AUDIT_STAFF_ID", "");
        	if(StringUtils.isBlank(auditStaffId)){
        		return;
        	}
        	//过滤掉不需要生成集团业务稽核工单的业务类型
        	String tradeTypeCode = this.reqData.getTradeType().getTradeTypeCode();
        	IDataset params = ParamInfoQry.getCommparaByParamattr("CSM", "840", tradeTypeCode, "0898");
        	if(IDataUtil.isNotEmpty(params)){
        		return;
        	}
        	String auditId = ""; 
        	String contractId = "";
        	if(StringUtils.isNotBlank(map.getString("ORIG_BATCH_ID", ""))){
        		auditId = map.getString("ORIG_BATCH_ID", "");	//批量任务的批次号不为空就取批次号
        	}else{
        		auditId = this.getTradeId();					//不然就取业务流水号
        	}
        	if(StringUtils.isNotBlank(this.reqData.getContractId())){
        		contractId = this.reqData.getContractId();	//合同变更时
        	}else{
        		contractId = this.reqData.getUca().getUser().getContractId(); //集团产品新增、注销时
        	}
        	IDataset tradeDiscnts = new DatasetList();
        	IDataset grpMebDiscnts = new DatasetList();
        	//集团产品定制的成员优惠
        	IDataset grpPackage = this.bizData.getTradeGrpPackage();
        	if(IDataUtil.isNotEmpty(grpPackage)){
        		grpMebDiscnts = DataHelper.filter(grpPackage, "ELEMENT_TYPE_CODE=D");
            	tradeDiscnts.addAll(grpMebDiscnts);
        	}        	 
        	//集团产品的优惠
        	IDataset grpDiscnts = this.bizData.getTradeDiscnt();
        	tradeDiscnts.addAll(grpDiscnts);
        	
        	String addDisncts = "";
        	String delDiscnts = "";
        	String modDiscnts = "";
        	if(IDataUtil.isNotEmpty(tradeDiscnts)){
        	   for(int i=0;i<tradeDiscnts.size();i++){
        		   IData each = tradeDiscnts.getData(i);
        		   String modifyTag = each.getString("MODIFY_TAG", "");
	        	   String discntCode = each.getString("ELEMENT_ID", "");
	        	   discntCode = StringUtils.isBlank(discntCode) ? each.getString("DISCNT_CODE", "") : discntCode;
	        	   if(TRADE_MODIFY_TAG.Add.getValue().equals(modifyTag)){
	        		   addDisncts += StringUtils.isNotBlank(addDisncts) ? ","+discntCode : discntCode;
	        	   }else if(TRADE_MODIFY_TAG.DEL.getValue().equals(modifyTag)){
	        		   delDiscnts += StringUtils.isNotBlank(delDiscnts) ? ","+discntCode : discntCode;
	        	   }else if(TRADE_MODIFY_TAG.MODI.getValue().equals(modifyTag)){
	        		   modDiscnts += StringUtils.isNotBlank(modDiscnts) ? ","+discntCode : discntCode;
        		   }
        	   }
        	}
        	
        	IData param = new DataMap();
        	param.put("AUDIT_ID", auditId);													//批量业务的批次号或业务流水号trade_id
        	param.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(auditId));					//受理月份：受理时间的月份，可作为分区字段。正常情况下可从trade_id的第5、6位获得。
        	param.put("BIZ_TYPE", StringUtils.isNotBlank(this.reqData.getBatchId()) ? "2" : "1");			//业务工单类型：1-单条，2-批量业务
        	param.put("TRADE_TYPE_CODE", tradeTypeCode);									//业务类型编码：见参数表TD_S_TRADETYPE
        	param.put("GROUP_ID", this.reqData.getUca().getCustGroup().getGroupId());		//集团客户编码
        	param.put("CUST_NAME", this.reqData.getUca().getCustGroup().getCustName());		//集团客户名称
        	param.put("GRP_SN", this.reqData.getUca().getSerialNumber());					//集团产品编码
        	param.put("CONTRACT_ID", contractId);											//合同编号
        	param.put("VOUCHER_FILE_LIST", map.getString("VOUCHER_FILE_LIST", ""));			//凭证信息上传文件ID
        	param.put("ADD_DISCNTS", addDisncts);											//新增优惠
        	param.put("DEL_DISCNTS", delDiscnts);											//删除优惠
        	param.put("MOD_DISCNTS", modDiscnts);											//变更优惠
        	param.put("STATE", "0");														//稽核单状态:0-初始，1-稽核通过，2-稽核不通过
        	param.put("IN_DATE", SysDateMgr.getSysTime());									//提交时间
        	param.put("IN_STAFF_ID", CSBizBean.getVisit().getStaffId());					//提交工号
        	param.put("AUDIT_STAFF_ID", auditStaffId);										//稽核人工号
        	boolean smsFlag=true;
			   if(StringUtils.isNotBlank(this.reqData.getBatchId())){
				   IDataset audiInfos=GrpBaseAudiInfoQry.queryGrpBaseAuditInfoForPK(param);
				   if(audiInfos!=null&&audiInfos.size()>0){
					   smsFlag=false;
				   }
			   }
        	GrpBaseAudiInfoQry.addGrpBaseAuditInfo(param);
        	/*************************REQ201810100001优化政企集中稽核相关功能需求 begin*************************/
        	if(StringUtils.isNotBlank(auditStaffId)){
				   if(smsFlag){
					   IDataset staffs = StaffInfoQry.queryValidStaffById(auditStaffId);
					   if(IDataUtil.isNotEmpty(staffs)){
							IData staff = staffs.getData(0);
							String staffSn = staff.getString("SERIAL_NUMBER", "");
							if(StringUtils.isNotBlank(staffSn)){
								String grpSn = this.reqData.getUca().getCustGroup().getGroupId();
								String tradeType = StaticUtil.getStaticValue(CSBizService.getVisit(), "TD_S_TRADETYPE", "TRADE_TYPE_CODE", "TRADE_TYPE", tradeTypeCode);
								String smsContent = "集团编码:"+grpSn+",业务类型:"+tradeType+",工单流水号/批次号"+auditId+"已提交稽核,请及时稽核!";
								IData smsdata = new DataMap();
						        smsdata.put("EPARCHY_CODE", "0898");
						        smsdata.put("RECV_OBJECT", staffSn);// 工号手机号码
						        smsdata.put("NOTICE_CONTENT", smsContent);
						        smsdata.put("RECV_ID", "-1");
						        smsdata.put("SMS_TYPE_CODE", "20");// 用户办理业务通知
						        smsdata.put("FORCE_START_TIME", "");
						        smsdata.put("FORCE_END_TIME", "");
						        smsdata.put("REMARK", "");
						        SmsSend.insSms(smsdata);
							}
						}
					   
				   }
        	}
        	
        	
			   
			 /*************************REQ201810100001优化政企集中稽核相关功能需求  end*************************/
			   
        }
    }
}
