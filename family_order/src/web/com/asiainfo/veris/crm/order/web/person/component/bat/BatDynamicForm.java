
package com.asiainfo.veris.crm.order.web.person.component.bat;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.consts.PersonConst;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.PackagePrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizDynamicComponent;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;

public abstract class BatDynamicForm extends CSBizDynamicComponent
{

    // 批量买断开户
    private void batActiveCreateUser(IMarkupWriter writer, IRequestCycle cycle) throws Exception
    {
        IData params = new DataMap();
        params = getPage().getData();

        initBatPre(params);
        initProductSel(params);
        IDataset advanceFees = CSViewCall.call(this, "CS.BatDealSVC.getAdvanceFees", params);
        setListD(advanceFees);
        includeScript(writer, "scripts/person/bat/batactivecreateuser/batactivecreateuser.js");
        includeScript(writer, "scripts/person/bat/batdeal/productsel.js");
        writer.printRaw("<script language=\"javascript\">\n");
        writer.printRaw("$(function(){\n");
        //writer.printRaw("packageList.eparchyCode='" + getVisit().getStaffEparchyCode() + "';\n");
        writer.printRaw("batactivecreateuser.checkPaymode();\n");
        writer.printRaw("setBrandCode();\n");
        writer.printRaw("});\n");
        writer.printRaw("</script>\n");
        setEparchyCode(this.getVisit().getStaffEparchyCode());
    }

    // 营销活动（入乡情网送农信通）批量办理
    private void batCountryNet(IMarkupWriter writer, IRequestCycle cycle) throws Exception
    {
        IData params = new DataMap();
        params = getPage().getData();
        String productId = params.getString("PRODUCT_ID", "");
        if (null == productId || "".equals(productId))
        {
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "该获取乡情网活动产品ID为空！");
        }
        IData paramA = new DataMap();
        paramA.clear();
        paramA.put("PRODUCT_ID", productId);
        paramA.put("TRADE_STAFF_ID", "SUPERUSR");
        IDataset output = com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall.call(this, "CS.BatDealSVC.queryGiftSalePackageByPRoductId", paramA);
        IDataset productPackageIds = new DatasetList();
        if (output != null && IDataUtil.isNotEmpty(output))
        {
            for (int i = 0; i < output.size(); i++)
            {
                IData productPackageId = new DataMap();
                String PRODUCT_ID = output.getData(i).getString("PRODUCT_ID", "");
                String PACKAGE_ID = output.getData(i).getString("PACKAGE_ID", "");
                productPackageId.put("PRODUCT_PACKAGE_ID", PRODUCT_ID + "-" + PACKAGE_ID);
                productPackageId.put("PACKAGE_NAME", output.getData(i).getString("PACKAGE_NAME", ""));
                productPackageIds.add(productPackageId);
            }
        }
        if (IDataUtil.isNotEmpty(productPackageIds))
        {
            setListC(productPackageIds);
        }
    }

    // 固话批量装机
    public void batCreateFixedUser(IMarkupWriter writer, IRequestCycle cycle) throws Exception
    {
        IData params = new DataMap();
        params = getPage().getData();

        initProductSel(params);

        setEditInfo(params);
        includeScript(writer, "scripts/person/bat/batdeal/productsel.js");
        writer.printRaw("<script language=\"javascript\">\n");
        writer.printRaw("$(function(){\n");
        //writer.printRaw("packageList.eparchyCode='" + getVisit().getStaffEparchyCode() + "';\n");
        writer.printRaw("setBrandCode();\n");
        writer.printRaw("});\n");
        writer.printRaw("</script>\n");
        setEparchyCode(getVisit().getStaffEparchyCode());
    }

    // 千群百号装机
    public void batCreateTrunkUser(IMarkupWriter writer, IRequestCycle cycle) throws Exception
    {
        IData params = new DataMap();
        params = getPage().getData();

        initProductSel(params);

        setEditInfo(params);
        includeScript(writer, "scripts/person/bat/batdeal/productsel.js");
        writer.printRaw("<script language=\"javascript\">\n");
        writer.printRaw("$(function(){\n");
        //writer.printRaw("packageList.eparchyCode='" + getVisit().getStaffEparchyCode() + "';\n");
        writer.printRaw("setBrandCode();\n");
        writer.printRaw("});\n");
        writer.printRaw("</script>\n");
        setEparchyCode(getVisit().getStaffEparchyCode());
    }

    // 批量办理套餐
    private void batDiscntChg(IMarkupWriter writer, IRequestCycle cycle) throws Exception
    {
        IData params = new DataMap();
        params = getPage().getData();
        String discnt_code = params.getString("DISCNT_CODE", "");
        if (null == discnt_code || "".equals(discnt_code))
        {
            IData data = new DataMap();
            data.put("START_DATE", SysDateMgr.getSysTime());
            setInfo(data);
        }
        else
        {
            IData param = new DataMap();
            param.put("ID", discnt_code);
            param.put("ATTR_OBJ", "0");
            param.put("ID_TYPE", "D");
            IDataset attr = com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall.call(this, "CS.BatDealSVC.queryAttrsByElement", param);
            setListD(attr);
        }
        includeScript(writer, "scripts/person/bat/batchgdiscnt/batdiscntchg.js");
    }
    
    // 批量办理特殊套餐
    private void batDiscntChgSpec(IMarkupWriter writer, IRequestCycle cycle) throws Exception
    {
        IData params = new DataMap();
        params = getPage().getData();
        String discnt_code = params.getString("DISCNT_CODE", "");
        if (null == discnt_code || "".equals(discnt_code))
        {
            IData data = new DataMap();
            data.put("START_DATE", SysDateMgr.getSysTime());
            setInfo(data);
        }
        else
        {
            IData param = new DataMap();
            param.put("ID", discnt_code);
            param.put("ATTR_OBJ", "0");
            param.put("ID_TYPE", "D");
            IDataset attr = com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall.call(this, "CS.BatDealSVC.queryAttrsByElement", param);
            setListD(attr);
        }
        includeScript(writer, "scripts/person/bat/batchgdiscnt/batdiscntchgspec.js");
    }
    
    // 集团产品特殊优惠变更
    private void batGrpDiscntChgSpec(IMarkupWriter writer, IRequestCycle cycle) throws Exception
    {        
        IData params = new DataMap();
        params = getPage().getData();
        IData info = new DataMap();
        IDataset list = new DatasetList();

        IData data1 = new DataMap();
        data1.put("DATA_ID", "1");
        data1.put("DATA_NAME", "上月底");
        data1.put("ALL_DATA_NAME", "(1)上月底");
        
        IData data2 = new DataMap();
        data2.put("DATA_ID", "2");
        data2.put("DATA_NAME", "当前");
        data2.put("ALL_DATA_NAME", "(2)当前");

        IData data3 = new DataMap();
        data3.put("DATA_ID", "3");
        data3.put("DATA_NAME", "本月底");
        data3.put("ALL_DATA_NAME", "(3)本月底");

        IData data4 = new DataMap();
        data4.put("DATA_ID", "4");
        data4.put("DATA_NAME", "永久");
        data4.put("ALL_DATA_NAME", "(4)永久");

        list.add(data1);
        list.add(data2);
        list.add(data3);
        list.add(data4);
        
        info.put("LIST", list);
        setInfo(info);
        
    
        String svcFlag = params.getString("SVC_FLAG", "");
        if ("1".equals(svcFlag))
        {// 批量服务变更
            includeScript(writer, "scripts/person/bat/batchgdiscnt/batgrpdiscntchgspec.js");
        }
    }
    

    // 目标用户批量导入
    private void batHintMessageChg(IMarkupWriter writer, IRequestCycle cycle) throws Exception
    {
        IData data = new DataMap();
        data.put("START_DATE", SysDateMgr.getSysDate());
        data.put("END_DATE", SysDateMgr.END_TIME_FOREVER);
        setInfo(data);
        includeScript(writer, "scripts/person/bat/bathintmessagechg/bathintmessagechg.js");
    }

    // 批量修改帐户银行资料
    private void batModifyacycinfo(IMarkupWriter writer, IRequestCycle cycle) throws Exception
    {
        includeScript(writer, "scripts/person/bat/batmodifyacycinfo/batmodifyacycinfo.js");
        writer.printRaw("<script language=\"javascript\">\n");
        writer.printRaw("$(function(){\n");
        //writer.printRaw("packageList.eparchyCode='" + getVisit().getStaffEparchyCode() + "';\n");
        writer.printRaw("batmodifyacycinfo.checkPaymode();\n");
        writer.printRaw("});\n");
        writer.printRaw("</script>\n");
    }

    // 批量业务平台业务
    private void batPlatForm(IMarkupWriter writer, IRequestCycle cycle) throws Exception
    {
        IData params = new DataMap();
        params = getPage().getData();
        String clickTag = params.getString("CLICK_TAG", "");
        String spCode = params.getString("SP_CODE", "");
        String bizCode = params.getString("BIZ_CODE", "");
        String bizTypeCode = params.getString("BIZ_TYPE_CODE", "");
        String bizProcessTag = "";
        IDataset operInfos = new DatasetList();
        IData data = new DataMap();
        data.put("START_DATE1", SysDateMgr.getSysTime());
        setInfo(data);
        if (StringUtils.isNotBlank(clickTag) && clickTag.equals("QUERY_OPER_INFO"))
        {
            IData param = new DataMap();
            param.clear();
            param.put("SP_CODE", spCode);
            if (bizCode.contains("#"))
            {
                bizCode = bizCode.replace("#", "+");
            }
            param.put("BIZ_CODE", bizCode);
            param.put("BIZ_TYPE_CODE", bizTypeCode);
            operInfos = com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall.call(this, "CS.BatDealSVC.getOperTypeBySpAndBiz", param);
            if (IDataUtil.isNotEmpty(operInfos))
            {
                bizProcessTag = ((IData) (operInfos.get(0))).getString("BIZ_PROCESS_TAG");
                operInfos = getOperType(bizProcessTag);
                if (StringUtils.isNotBlank(bizTypeCode))
                {
                    // PIMM批量只有注册和注销
                    if ("10".equals(bizTypeCode))
                    {
                        IData remove = new DataMap();
                        for (int i = 0; i < operInfos.size(); i++)
                        {
                            remove = (IData) operInfos.get(i);
                            if (!remove.getString("OPER_CODE").equals("01") && !remove.getString("OPER_CODE").equals("02"))
                            {
                                operInfos.remove(remove);
                                i--;
                            }
                        }
                    }
                    // DSMP
                    if ("03".equals(bizTypeCode) || "04".equals(bizTypeCode) || "05".equals(bizTypeCode))
                    {
                        IData remove = new DataMap();
                        for (int i = 0; i < operInfos.size(); i++)
                        {
                            remove = (IData) operInfos.get(i);
                            if (!remove.getString("OPER_CODE").equals("06") && !remove.getString("OPER_CODE").equals("07"))
                            {
                                operInfos.remove(remove);
                                i--;
                            }
                        }
                    }
                }
                setOperations(operInfos);
            }
        }
        includeScript(writer, "scripts/person/bat/platform/platform.js");
    }

    // 无线固话批量预开户
    private void batPreCreateTDUser(IMarkupWriter writer, IRequestCycle cycle) throws Exception
    {
        IData data = new DataMap();
        data = getPage().getData();

        initBatPreTDInfo(data, "0");
        initProductSel(data);

        setEditInfo(data);
        setInfo(data);
        includeScript(writer, "scripts/person/bat/batprecreateuser/batprecreateuser.js");
        includeScript(writer, "scripts/person/bat/batdeal/productsel.js");
        writer.printRaw("<script language=\"javascript\">\n");
        writer.printRaw("$(function(){\n");
        //writer.printRaw("packageList.eparchyCode='" + getVisit().getStaffEparchyCode() + "';\n");
        writer.printRaw("batprecreateuser.checkPaymode();\n");
        writer.printRaw("setBrandCode();\n");
        writer.printRaw("});\n");
        writer.printRaw("</script>\n");
        setEparchyCode(getVisit().getStaffEparchyCode());
    }

    // 批量预开户初始化
    private void batPreCreateUser(IData params) throws Exception
    {
        IData info = new DataMap();
        initBatPreInfo(info, "0");
        setInfo(info);
        
        // REQ201502050013放号政策调整需求 songlm 
        IData data = new DataMap();
        data.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
        data.put("PARAM_ATTR", "519");//519为未激活用户对应的包
        
        //获取赠送话费的选项
    	IDataset PresentFeeList = CSViewCall.call(this, "SS.CreatePersonUserSVC.queryPresentFees", data);
    	
    	//工号的包权限控制
        PackagePrivUtil.filterPackageListByPriv(getVisit().getStaffId(), PresentFeeList);
        
    	setPresentFeeList(PresentFeeList);
    	
    	//设置默认项
    	if (IDataUtil.isNotEmpty(PresentFeeList))
		{
    		IData PresentFeeParam = new DataMap();
    		PresentFeeParam.put("AGENT_PRESENT_FEE", "");
    		
			for (int i = 0, size = PresentFeeList.size(); i < size; i++)
			{
				IData PresentFee = PresentFeeList.getData(i);
				if("1".equals(PresentFee.getString("DEFAULT_VALUE","")))
				{
					PresentFeeParam.put("AGENT_PRESENT_FEE", PresentFee.getString("DATA_ID",""));
				}
			}
			
			setPresentFee(PresentFeeParam);

		}
        //end
    	
        setEparchyCode(getVisit().getStaffEparchyCode());
    }
    

    // 批量预开户||物联网批量预开户||校园营销批量预开户初始化
    private void batPreCreateUser(IMarkupWriter writer, IRequestCycle cycle) throws Exception
    {
        IData params = new DataMap();
        params = getPage().getData();

        initBatPre(params);
        initProductSel(params);

        includeScript(writer, "scripts/person/bat/batprecreateuser/batprecreateuser.js");
        includeScript(writer, "scripts/person/bat/batdeal/productsel.js");
        includeScript(writer, "scripts/csserv/component/person/CommLib.js");
        writer.printRaw("<script language=\"javascript\">\n");
        writer.printRaw("$(function(){\n");
        //writer.printRaw("packageList.eparchyCode='" + getVisit().getStaffEparchyCode() + "';\n");
        writer.printRaw("batprecreateuser.checkPaymode();\n");
        writer.printRaw("setBrandCode();\n");
        writer.printRaw("});\n");
        writer.printRaw("</script>\n");
        setEparchyCode(this.getVisit().getStaffEparchyCode());
    }
    
    // 批量异网预开户
    private void batPreCreateHUser(IMarkupWriter writer, IRequestCycle cycle) throws Exception
    {
        IData params = new DataMap();
        params = getPage().getData();

        initBatPre(params);
        initProductSel(params);

        includeScript(writer, "scripts/person/bat/batprecreateuser/batprecreateuser.js");
        includeScript(writer, "scripts/person/bat/batdeal/productsel.js");
        includeScript(writer, "scripts/csserv/component/person/CommLib.js");
      /*  writer.printRaw("<script language=\"javascript\">\n");
        writer.printRaw("$(function(){\n");
        //writer.printRaw("packageList.eparchyCode='" + getVisit().getStaffEparchyCode() + "';\n");
       // writer.printRaw("batprecreateuser.checkPaymode();\n");
        writer.printRaw("setBrandCode();\n");
        writer.printRaw("});\n");
        writer.printRaw("</script>\n");*/
        setEparchyCode(this.getVisit().getStaffEparchyCode());
    }
    
    // 物联网批量开户初始化
    private void batCreateUserPWLW(IMarkupWriter writer, IRequestCycle cycle) throws Exception
    {
        IData params = new DataMap();
        params = getPage().getData();
        IData data = new DataMap();
        
        data.put("ACCT_TAG", "0");
        data.put("USER_PASSWD", "");
        data.put("CHECK_DEPART_ID", getVisit().getDepartId());
        
        data.put("CUST_NAME", "");
        data.put("PHONE", "");
        data.put("USER_TYPE_CODE", "0");
        data.put("PSPT_TYPE_CODE", "0");
        data.put("PSPT_ID", "");
        data.put("BIRTHDAY", "");
        data.put("PSPT_ADDR", "");
        data.put("PSPT_END_DATE", "");
        data.put("POST_ADDRESS", "");
        data.put("POST_CODE", "");
        data.put("CALLING_TYPE_CODE", "01");
        
        data.put("AGENT_CUST_NAME", "");
        data.put("AGENT_PSPT_TYPE_CODE", "");
        data.put("AGENT_PSPT_ID", "");
        data.put("AGENT_PSPT_ADDR", "");
        
        data.put("RSRV_STR2", "");
        data.put("RSRV_STR3", "");
        data.put("RSRV_STR4", "");
        data.put("RSRV_STR5", "");

        data.put("PAY_NAME", "");
        data.put("PAY_MODE_CODE", "");
        data.put("ACCT_DAY", "");
        data.put("SUPER_BANK_CODE", "");
        data.put("BANK_CODE", "");
        data.put("BANK_ACCT_NO", "");
        
        IData put = new DataMap();
        put.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
        IDataset acctInfoDays = CSViewCall.call(this, "CS.AcctInfoQrySVC.qryAcctInfoDay", put);
        IDataset acctInfoDaysnew=new DatasetList();;
        IData data1 = new DataMap();
		for (int i = 0; i < acctInfoDays.size(); i++) {//批量物流网开户，	用户结账日  写死 1日20170803
			data1=acctInfoDays.getData(i);
			if(data1.getString("PARAM_NAME").equals("1日")){
				acctInfoDaysnew.add(data1);
				acctInfoDays=acctInfoDaysnew;
				break;
			}
		}
     
        data.put("acctInfoDays", acctInfoDays);
        data.put("ACCT_DAY", "");
        
        /**
         * REQ201804020010_物联网卡批量开户责任人、经办人等证件类型新增军人身份证
         * <br/>
         *  查看是否有查询 军人身份证 权限
         * @author zhuoyingzhi
         * @date 20180423
         */
        boolean staffPriv=StaffPrivUtil.isFuncDataPriv(this.getVisit().getStaffId(), "PASSPORTTYPE_PRV");
        if(!staffPriv){
            //下拉需要过滤的值
            data.put("EXCLUDE_FIELD_VALUE", "3");
        }else{
        	//无需过滤
        	data.put("EXCLUDE_FIELD_VALUE", "");
        }

        
        
        setInfo(data);
        setEparchyCode(this.getVisit().getStaffEparchyCode());
        setEditInfo(params);
        
        params.put("m2mFlag", "1");
        params.put("OPEN_TYPE_CODE", PersonConst.IOT_OPEN);
        initProductInfo(params);
        
        includeScript(writer, "scripts/person/bat/batprecreateuser/batcreateuserpwlw.js");
        includeScript(writer, "scripts/person/bat/batdeal/productsel.js");
        includeScript(writer, "scripts/csserv/component/person/CommLib.js");
        includeScript(writer, "scripts/csserv/component/person/passwordset/PasswordSet.js");
        includeScript(writer, "scripts/csserv/common/validate/custvalidate.js");
        writer.printRaw("<script language=\"javascript\">\n");
        writer.printRaw("$(function(){\n");
        //writer.printRaw("packageList.eparchyCode='" + getVisit().getStaffEparchyCode() + "';\n");
        writer.printRaw("batprecreateuser.checkPaymode();\n");
        writer.printRaw("setBrandCode();\n");
        writer.printRaw("});\n");
        writer.printRaw("</script>\n");
        setEparchyCode(this.getVisit().getStaffEparchyCode());
    }
    
    // 行业应用卡批量开户初始化
    private void batCreateUserM2M(IMarkupWriter writer, IRequestCycle cycle) throws Exception
    {
        IData params = new DataMap();
        params = getPage().getData();
        IData data = new DataMap();
        
        data.put("ACCT_TAG", "0");
        data.put("USER_PASSWD", "");
        data.put("CHECK_DEPART_ID", getVisit().getDepartId());
        
        data.put("CUST_NAME", "");
        data.put("PHONE", "");
        data.put("USER_TYPE_CODE", "8");
        data.put("PSPT_TYPE_CODE", "0");
        data.put("PSPT_ID", "");
        data.put("BIRTHDAY", "");
        data.put("PSPT_ADDR", "");
        data.put("PSPT_END_DATE", "");
        data.put("POST_ADDRESS", "");
        data.put("POST_CODE", "");
        data.put("CALLING_TYPE_CODE", "01");
        
        data.put("AGENT_CUST_NAME", "");
        data.put("AGENT_PSPT_TYPE_CODE", "");
        data.put("AGENT_PSPT_ID", "");
        data.put("AGENT_PSPT_ADDR", "");
        
        data.put("USE", "");
        data.put("USE_PSPT_TYPE_CODE", "");
        data.put("USE_PSPT_ID", "");
        data.put("USE_PSPT_ADDR", "");
        
        data.put("RSRV_STR2", "");
        data.put("RSRV_STR3", "");
        data.put("RSRV_STR4", "");
        data.put("RSRV_STR5", "");

        data.put("PAY_NAME", "");
        data.put("PAY_MODE_CODE", "");
        data.put("ACCT_DAY", "");
        data.put("SUPER_BANK_CODE", "");
        data.put("BANK_CODE", "");
        data.put("BANK_ACCT_NO", "");
        
        IData put = new DataMap();
        put.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
        IDataset acctInfoDays = CSViewCall.call(this, "CS.AcctInfoQrySVC.qryAcctInfoDay", put);
        data.put("acctInfoDays", acctInfoDays);
        data.put("ACCT_DAY", "");
        
        setInfo(data);
        setEparchyCode(this.getVisit().getStaffEparchyCode());
        setEditInfo(params);
        
        params.put("m2mFlag", "0");
        params.put("OPEN_TYPE_CODE", "");
        initM2MProductInfo(params);
        
        includeScript(writer, "scripts/person/bat/batprecreateuser/batcreateuserpm2m.js");
        includeScript(writer, "scripts/person/bat/batdeal/productsel.js");
        includeScript(writer, "scripts/csserv/component/person/CommLib.js");
        includeScript(writer, "scripts/csserv/component/person/passwordset/PasswordSet.js");
        includeScript(writer, "scripts/csserv/common/validate/custvalidate.js");
        writer.printRaw("<script language=\"javascript\">\n");
        writer.printRaw("$(function(){\n");
        //writer.printRaw("packageList.eparchyCode='" + getVisit().getStaffEparchyCode() + "';\n");
        writer.printRaw("batprecreateuser.checkPaymode();\n");
        writer.printRaw("setBrandCode();\n");
        writer.printRaw("});\n");
        writer.printRaw("</script>\n");
        setEparchyCode(this.getVisit().getStaffEparchyCode());
    }
    
 // 目标用户批量导入
    private void batCreateUserBNBD(IMarkupWriter writer, IRequestCycle cycle) throws Exception
    {
        IData data = new DataMap();
        data.put("SN", "");
        data.put("CONTACT_PHONE", "");
        data.put("CONTACT", "");
        data.put("PHONE", "");
        setInfo(data);
        includeScript(writer, "scripts/person/bat/batprecreateuser/batcreateuserpbnbd.js");
    }

    // 物联网批量预开户初始化
    private void batPwlwPreCreateUser(IData params) throws Exception
    {
        IData data = new DataMap();
        initBatPreInfo(data, "1");
        setInfo(data);
        setEparchyCode(this.getVisit().getStaffEparchyCode());
    }

    // 短信白名单
    private void batRedMember(IMarkupWriter writer, IRequestCycle cycle) throws Exception
    {
        IData data = new DataMap();
        data.put("END_DATE", SysDateMgr.END_TIME_FOREVER);
        setInfo(data);
        includeScript(writer, "scripts/person/bat/redmember/batredmember.js");
    }

    // 批量办理营销活动
    private void batSaleActive(IMarkupWriter writer, IRequestCycle cycle) throws Exception
    {
        IData params = new DataMap();
        params = getPage().getData();
        String productId = params.getString("PRODUCT_ID", "");
        if (null == productId || "".equals(productId))
        {
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "产品ID为空！");
        }
        IData paramA = new DataMap();
        paramA.clear();
        paramA.put("PRODUCT_ID", productId);
        paramA.put("TRADE_STAFF_ID", "SUPERUSR");
        IDataset output = com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall.call(this, "CS.BatDealSVC.queryPackages", paramA);
        IDataset productPackageIds = new DatasetList();
        if (output != null && IDataUtil.isNotEmpty(output))
        {
            for (int i = 0; i < output.size(); i++)
            {
                IData productPackageId = new DataMap();
                String PARAM_CODE = output.getData(i).getString("PARAM_CODE", "");
                String PARA_CODE1 = output.getData(i).getString("PARA_CODE1", "");
                String PARA_CODE2 = output.getData(i).getString("PARA_CODE2", "");
                productPackageId.put("PRODUCT_PACKAGE_ID", PARAM_CODE + "-" + PARA_CODE1 + "-" + PARA_CODE2);
                productPackageId.put("PACKAGE_NAME", output.getData(i).getString("PARAM_NAME", ""));
                productPackageIds.add(productPackageId);
            }
        }
        if (IDataUtil.isNotEmpty(productPackageIds))
        {
            setListC(productPackageIds);
        }
        /**
         * micy@REQ201712080007 - 关于批量办理营销活动付费方式优化
         */
        includeScript(writer, "scripts/person/bat/batdeal/batsaleactive.js");
        /**
         * micy@REQ201712080007 - 关于批量办理营销活动付费方式优化
         */
    }

    
    private void batPcrfChg(IMarkupWriter writer, IRequestCycle cycle) throws Exception{
        
        IData params = new DataMap();
        params = getPage().getData();
        IData info = new DataMap();
        IDataset list = new DatasetList();
        
        IData commpara = new DataMap();
        commpara.put("SUBSYS_CODE", "CSM");
        commpara.put("PARAM_ATTR", "3996");
        commpara.put("PARAM_CODE", "IoTGprsSVC");
        commpara.put("EPARCHY_CODE", "0898");
        IDataset gprsSVCs = CSViewCall.call(this, "CS.CommparaInfoQrySVC.getCommpara", commpara);
        
        if(IDataUtil.isNotEmpty(gprsSVCs)){
            for(int i = 0 ; i<gprsSVCs.size();i++){
                IData initinfo = new DataMap();
                initinfo.put("SERVICE_ID", gprsSVCs.getData(i).getString("PARA_CODE1"));
                initinfo.put("SERVICE_NAME", gprsSVCs.getData(i).getString("PARAM_NAME"));
                initinfo.put("ALL_SERVICE_NAME", "("+gprsSVCs.getData(i).getString("PARA_CODE1")+")"+gprsSVCs.getData(i).getString("PARAM_NAME"));
                list.add(initinfo);
            }
        }
        info.put("LIST", list);
        setInfo(info);
    
        String svcFlag = params.getString("SVC_FLAG", "");
        if ("1".equals(svcFlag))
        {// 批量服务变更
            includeScript(writer, "scripts/person/bat/batpcrfchg/batpcrfchg.js");
        }
    }
	
	    // 批量办理营销活动终止
    private void batSaleActiveend(IMarkupWriter writer, IRequestCycle cycle) throws Exception
    {
        IData params = new DataMap();
        params = getPage().getData();
        
        IData paramA = new DataMap();
        paramA.clear();
        paramA.put("TRADE_STAFF_ID", "SUPERUSR");

        IDataset output = com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall.call(this, "CS.BatDealSVC.queryPackagesend", paramA);
        IDataset productPackageIds = new DatasetList();
        if (output != null && IDataUtil.isNotEmpty(output))
        {
            for (int i = 0; i < output.size(); i++)
            {
                IData productPackageId = new DataMap();
                String PARAM_CODE = output.getData(i).getString("PARAM_CODE", "");
                String PARA_CODE1 = output.getData(i).getString("PARA_CODE1", "");
                String PARA_CODE2 = output.getData(i).getString("PARA_CODE2", "");
                productPackageId.put("PRODUCT_PACKAGE_ID", PARAM_CODE + "-" + PARA_CODE1 + "-" + PARA_CODE2);
                productPackageId.put("PACKAGE_NAME", output.getData(i).getString("PARAM_NAME", ""));
                productPackageIds.add(productPackageId);
            }
        }
        if (IDataUtil.isNotEmpty(productPackageIds))
        {
            setListC(productPackageIds);
        }
    }

	
    
    // 批量服务变更
    private void batServiceChg(IMarkupWriter writer, IRequestCycle cycle) throws Exception
    {
        IData params = new DataMap();
        params = getPage().getData();
       

        String service_id = params.getString("SERVICE_ID", "");
        if (null == service_id || "".equals(service_id))
        {
            IData data = new DataMap();
            String end_date = SysDateMgr.getTheLastTime();
            String status = "0";

            data.put("START_DATE", SysDateMgr.getSysTime());
            data.put("END_DATE", end_date);
            data.put("MODIFY_TAG", status);
            setInfo(data);
        }
        else
        {
            IData param = new DataMap();
            param.put("ID", service_id);
            param.put("ATTR_OBJ", "0");
            param.put("ID_TYPE", "S");
            IDataset attr = com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall.call(this, "CS.BatDealSVC.queryAttrsByElement", param);
            setListD(attr);
        }
        
        // 此处其实没用，预留
        String svcFlag = params.getString("SVC_FLAG", "");
        if ("1".equals(svcFlag))
        {// 批量服务变更
        	includeScript(writer, "scripts/person/bat/batservicechg/batservicechg.js");
        }
        else if ("2".equals(svcFlag))
        {// 批量服务暂停
        	includeScript(writer, "scripts/person/bat/batservicechg/batservicechg.js");
        }
        else if ("3".equals(svcFlag))
        {// 批量服务恢复
        	includeScript(writer, "scripts/person/bat/batservicechg/batservicechg.js");
        }
        else if ("4".equals(svcFlag))
        {// 无条件特殊批量服务变更
        	includeScript(writer, "scripts/person/bat/batservicechg/batservicechgspec.js");
        }
        
        
    }

    /**
     * 按产品按包将元素分类 原则：相同产品、相同包下的元素为一个List,包在ELEMENTS的List里，在ELEMENTS外的是一个Map，最外层为一个大的List
     * 
     * @param oldList
     * @return
     * @throws Exception
     */
    public IDataset changeElements(IDataset oldList) throws Exception
    {
        IDataset newList = new DatasetList();
        IDataset tempList = new DatasetList();
        IData oldData = null;
        IData newData = null;

        String comProductId = "";
        String comPackageId = "";
        String oldProductId = "";
        String oldPackageId = "";
        String oldProductMode = "";

        // oldList.sort("PRODUCT_ID", IDataset.TYPE_STRING, IDataset.ORDER_ASCEND, "PACKAGE_ID", IDataset.TYPE_STRING,
        // IDataset.ORDER_ASCEND);
        for (int i = 0; i < oldList.size(); i++)
        {
            oldData = oldList.getData(i);
            oldProductId = oldData.getString("PRODUCT_ID");
            oldPackageId = oldData.getString("PACKAGE_ID");
            oldProductMode = oldData.getString("PRODUCT_MODE");
            if (comProductId.equals(oldProductId) && comPackageId.equals(oldPackageId))
            {// 产品与包都相同时,只生成一个{ELEMENTS=[...],PRODUCT_ID=,PACKAGE_ID}
                tempList.add(oldData);
            }
            else
            {// 其它情况都要生成多个{ELEMENTS=[...],PRODUCT_ID=,PACKAGE_ID}
                newData = new DataMap();
                tempList = new DatasetList();
                tempList.add(oldData);
                newData.put("ELEMENTS", tempList);
                newData.put("PRODUCT_ID", oldProductId);
                newData.put("PACKAGE_ID", oldPackageId);
                newData.put("PRODUCT_MODE", oldProductMode);
                comProductId = oldProductId;
                comPackageId = oldPackageId;
                newList.add(newData);
            }
        }
        return newList;
    }

    @Override
    public String getHtmlTemplate() throws Exception
    {
        String batchOperType = getPage().getParameter("BATCH_OPER_TYPE", "");
        if (StringUtils.isBlank(batchOperType))
        {
            return "";
        }
        IData cond = new DataMap();
        cond.put("BATCH_OPER_TYPE", batchOperType);
        IDataset output = com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall.call(this, "CS.BatDealSVC.queryBatchTypeByCode", cond);

        if (IDataUtil.isNotEmpty(output))
        {
            IData batchType = (IData) (output.getData(0));
            String childHtml = batchType.getString("COMP_POPACTION");
            if (StringUtils.isNotBlank(childHtml))
            {
				// return ECLTemplate.getTemplate(childHtml);
				return childHtml;
            }
            return "";
        }
        return "";
    }

    // 批量业务平台业务获取业务类型
    private IDataset getOperType(String bizProcessTag) throws Exception
    {
        IDataset opers = new DatasetList();
        for (int i = 0; i < bizProcessTag.length(); i++)
        {
            String j = bizProcessTag.charAt(i) + "";

            if (!j.equals("1"))
            {
                continue;
            }
            IData oper = new DataMap();
            switch (i)
            {

                case 0:
                    oper.put("OPER_CODE", "01");
                    oper.put("OPER_TYPE", "用户注册");// --第一位 del
                    opers.add(oper);
                    break;
                case 1:
                    oper.put("OPER_CODE", "02");
                    oper.put("OPER_TYPE", "用户注销");// --第二位 del
                    opers.add(oper);
                    break;
                case 2:
                    oper.put("OPER_CODE", "03");
                    oper.put("OPER_TYPE", "密码修改");// --第三位
                    opers.add(oper);
                    break;
                case 3:
                    oper.put("OPER_CODE", "04");
                    oper.put("OPER_TYPE", "业务暂停");// --第四位
                    opers.add(oper);
                    break;
                case 4:
                    oper.put("OPER_CODE", "05");
                    oper.put("OPER_TYPE", "业务恢复");// --第五位
                    opers.add(oper);
                    break;
                case 5:
                    oper.put("OPER_CODE", "06");
                    oper.put("OPER_TYPE", "订购");// --第六位
                    opers.add(oper);
                    break;
                case 6:
                    oper.put("OPER_CODE", "07");
                    oper.put("OPER_TYPE", "退订");// 第七位
                    opers.add(oper);
                    break;
                case 7:
                    oper.put("OPER_CODE", "08");
                    oper.put("OPER_TYPE", "用户资料变更");// 第八位
                    opers.add(oper);
                    break;
                case 8:
                    oper.put("OPER_CODE", "11");
                    oper.put("OPER_TYPE", "赠送");// 第九位 del
                    opers.add(oper);
                    break;
                case 9:
                    oper.put("OPER_CODE", "14");
                    oper.put("OPER_TYPE", "用户主动暂停");// 第十位 del
                    opers.add(oper);
                    break;
                case 10:
                    oper.put("OPER_CODE", "15");
                    oper.put("OPER_TYPE", "用户主动恢复");// 十一位 del
                    opers.add(oper);
                    break;
                case 11:
                    oper.put("OPER_CODE", "90");
                    oper.put("OPER_TYPE", "服务开关开");// 第十二位
                    opers.add(oper);
                    break;
                case 12:
                    oper.put("OPER_CODE", "91");
                    oper.put("OPER_TYPE", "服务开关关");// 十三位
                    opers.add(oper);
                    break;
                case 13:
                    oper.put("OPER_CODE", "89");
                    oper.put("OPER_TYPE", "SP全退订");// 第十四位 --直接写历史表，完工处理？ del
                    opers.add(oper);
                    break;
                case 14:
                    oper.put("OPER_CODE", "97");
                    oper.put("OPER_TYPE", "全业务恢复");// --第十伍位 --直接写历史表，完工处理？ del
                    opers.add(oper);
                    break;
                case 15:
                    oper.put("OPER_CODE", "98");
                    oper.put("OPER_TYPE", "全业务暂停");// --第十六位 del
                    opers.add(oper);
                    break;
                case 16:
                    oper.put("OPER_CODE", "99");
                    oper.put("OPER_TYPE", "全业务退订");// --第十七位 del
                    opers.add(oper);
                    break;
                case 17:
                    oper.put("OPER_CODE", "14");
                    oper.put("OPER_TYPE", "点播");// --第十八位
                    opers.add(oper);
                    break;
                case 18:
                    oper.put("OPER_CODE", "16");
                    oper.put("OPER_TYPE", "充值"); // --第十九位 编码待定
                    opers.add(oper);
                    break;
                case 19:
                    oper.put("OPER_CODE", "17");
                    oper.put("OPER_TYPE", "预约"); // --第二十位
                    opers.add(oper);
                    break;
                case 20:
                    oper.put("OPER_CODE", "18");
                    oper.put("OPER_TYPE", "预约取消"); // --第二十一位
                    opers.add(oper);
                    break;
                case 21:
                    oper.put("OPER_CODE", "19");
                    oper.put("OPER_TYPE", "挂失"); // --第二十二位 编码待定
                    opers.add(oper);
                    break;
                case 22:
                    oper.put("OPER_CODE", "20");
                    oper.put("OPER_TYPE", "解挂"); // --第二十三位 编码待定
                    opers.add(oper);
                    break;
                case 23:
                    oper.put("OPER_CODE", "10");
                    oper.put("OPER_TYPE", "套餐订购"); // --第二十四位
                    opers.add(oper);
                    break;
                case 24:
                    oper.put("OPER_CODE", "11");
                    oper.put("OPER_TYPE", "套餐退订"); // --第二十五位
                    opers.add(oper);
                    break;
                case 25:
                    oper.put("OPER_CODE", "09");
                    oper.put("OPER_TYPE", "密码重置"); // --第二十六位
                    opers.add(oper);
                    break;
                case 26:
                    oper.put("OPER_CODE", "12");
                    oper.put("OPER_TYPE", "套餐变更"); // --第二十七位
                    opers.add(oper);
                    break;
                case 29:
                    oper.put("OPER_CODE", "88");
                    oper.put("OPER_TYPE", "套餐变更"); // --第三十位 del
                    opers.add(oper);
                    break;
                default:
                    ;
            }

        }
        return opers;
    }

    public void initBatPre(IData params) throws Exception
    {
        String preFlag = params.getString("PRE_FLAG", "");
        if ("1".equals(preFlag) || "6".equals(preFlag) || "9".equals(preFlag))
        {// 预开户
            batPreCreateUser(params);
        }
        else if ("2".equals(preFlag))
        {// 物联网预开
            batPwlwPreCreateUser(params);
        }
        setEditInfo(params);
    }

    // 预开界面元素初始化
    private void initBatPreInfo(IData info, String m2mFlag) throws Exception
    {
        info.put("PSPT_ID", "111111111111111111");
        info.put("ADDRESS", "海南移动");
        info.put("POST_CODE", "570000");
        info.put("LINK_PHONE", "10086");
        if ("1".equals("m2mFlag"))
        {
            info.put("USER_TYPE_CODE", "F");
        }
        else
        {
            info.put("USER_TYPE_CODE", "B");
        }
        info.put("ACCT_TAG", "2");
        info.put("PAY_MODE_CODE", "0");
    }

    // 无线固话批量预开户初始化
    private void initBatPreTDInfo(IData info, String m2mFlag) throws Exception
    {
        info.put("PSPT_ID", "111111111111111111");
        info.put("ADDRESS", "海南移动");
        info.put("POST_CODE", "570000");
        info.put("LINK_PHONE", "10086");
        if ("1".equals("m2mFlag"))
        {
            info.put("USER_TYPE_CODE", "F");
        }
        else
        {
            info.put("USER_TYPE_CODE", "B");
        }
        info.put("ACCT_TAG", "2");
        info.put("PAY_MODE_CODE", "0");
    }
    
 // 或者可操作的产品类型队列
    private void initM2MProductInfo(IData params) throws Exception
    {
    	IDataset returnDatas = CSViewCall.call(this, "SS.CreatePersonUserSVC.createProductInfo", params);
    	IData returnData = returnDatas.getData(0);
        IDataset productTypeList = returnData.getDataset("PRODUCT_TYPE_LIST");
        //IDataset forceElements = returnData.getDataset("FORCE_ELEMENTS");
        //IDataset moreProductList = returnData.getDataset("MORE_PRODUCT_LIST");
        if (productTypeList != null && productTypeList.size() > 0)
        {
        	boolean IsInt = false;
            IDataset productTypeListForPreUser = new DatasetList();
            for (int i = 0; i < productTypeList.size(); i++)
            {
                String product_type_code = productTypeList.getData(i).getString("PRODUCT_TYPE_CODE");
                if ( !product_type_code.equals("0300") && !product_type_code.equals("0900") )
                {
                    continue;
                }
                else
                {
                	IsInt = true;
                    productTypeListForPreUser.add(productTypeList.getData(i));
                }
            }
            setProductTypeList(productTypeListForPreUser);
            if( IsInt ){
            	if( IDataUtil.isNotEmpty(productTypeListForPreUser) ){
            		IData productType = productTypeListForPreUser.first();
            		String product_type_code = productType.getString("PRODUCT_TYPE_CODE");
            		String product_type_name = productType.getString("PRODUCT_TYPE_NAME");
            		params.put("PRODUCT_TYPE_CODE", product_type_code);
            		params.put("PRODUCT_TYPE_NAME", product_type_name);
            	}
            }
        }
    }

    // 或者可操作的产品类型队列
    private void initProductInfo(IData params) throws Exception
    {
        if ("1".equals(params.getString("m2mFlag")))
        {
            params.put("EXISTS_BIND_BRAND", "PWLW");
        }
        // CreatePersonUserBean.setPayMoneyList(td);// 将第一个页面的付款信息带到第二个页面
        // 处理通常及特殊情况：绑定产品，品牌，默认标记
        params.put("B_REOPEN_TAG", "0");
        IDataset returnDatas = CSViewCall.call(this, "SS.CreatePersonUserSVC.createProductInfo", params);
        IData returnData = returnDatas.getData(0);
        IDataset productTypeList = returnData.getDataset("PRODUCT_TYPE_LIST");
        IDataset forceElements = returnData.getDataset("FORCE_ELEMENTS");
        IDataset moreProductList = returnData.getDataset("MORE_PRODUCT_LIST");

        if (params.getBoolean("B_REOPEN_TAG", false))
        {
            IDataset forceElementsInit = CSViewCall.call(this, "SS.BatTaskCreateSVC.getElementInfo", params);
            if (IDataUtil.isEmpty(forceElementsInit))
            {
                forceElements = new DatasetList();
            }
            else
            {
                forceElements = changeElements(forceElementsInit);
            }
        }
        if (productTypeList != null && productTypeList.size() > 0)
        {
            IDataset productTypeListForPreUser = new DatasetList();
            for (int i = 0; i < productTypeList.size(); i++)
            {
                String product_type_code = productTypeList.getData(i).getString("PRODUCT_TYPE_CODE");
                if (!product_type_code.equals("0100") && !product_type_code.equals("0400") && !product_type_code.equals("0300") && !product_type_code.equals("WLW0") && !product_type_code.equals("0A00") && !product_type_code.equals("0200")
                        && !product_type_code.equals("0900")&& !product_type_code.equals("MOSP"))
                {// add by yangqf 增加物联网产品品牌
                    continue;
                }
                else
                {
                    productTypeListForPreUser.add(productTypeList.getData(i));
                }
            }
            setProductTypeList(productTypeListForPreUser);
        }

        if (forceElements != null && forceElements.size() > 0)
        {
            setElements(forceElements);
        }
        if (moreProductList != null && moreProductList.size() > 0)
        {
            setBindMoreProducts(moreProductList);
        }
    }

    // 产品组件初始化
    public void initProductSel(IData params) throws Exception
    {
        setEparchyCode(getVisit().getStaffEparchyCode());
        String preFlag = params.getString("PRE_FLAG");
        if ("1".equals(preFlag) || "6".equals(preFlag) || "9".equals(preFlag))
        {// 批量预开户 或者 批量买断开户 或者 校园营销批量预开户
            initProductInfo(params);
        }
        else if ("2".equals(preFlag))
        {// 批量物联网开户
            params.put("m2mFlag", "1");
            params.put("OPEN_TYPE_CODE", PersonConst.IOT_OPEN);
            initProductInfo(params);
        }
        else if ("3".equals(preFlag) || "5".equals(preFlag))
        {// 固话批量装机 或者 千群百号装机
            params.put("PARENT_PTYPE_CODE", "3000");
            IDataset returnDatas = CSViewCall.call(this, "CS.BatDealSVC.getProductsType", params);
            setProductTypeList(returnDatas);
        }
        else if ("4".equals(preFlag))
        {// 无线固话批量预开户
            params.put("PARENT_PTYPE_CODE", "5000");
            IDataset returnDatas = CSViewCall.call(this, "CS.BatDealSVC.getProductsType", params);
            setProductTypeList(returnDatas);
        }
    }

    @Override
    public void renderComponent(StringBuilder builder, IMarkupWriter writer, IRequestCycle cycle) throws Exception
    {
        String batchOperType = getPage().getParameter("BATCH_OPER_TYPE", "");
        // 批量物联网策略变更
        if (StringUtils.isNotBlank(batchOperType) && batchOperType.equals("BATPCRFCHG"))
        {
            batPcrfChg(writer, cycle);
        }
        
        // 批量服务变更
        if (StringUtils.isNotBlank(batchOperType) && batchOperType.equals("SERVICECHG"))
        {
            batServiceChg(writer, cycle);
        }
        // 无条件特殊批量服务变更
        if (StringUtils.isNotBlank(batchOperType) && batchOperType.equals("SERVICECHGSPEC"))
        {
        	batServiceChg(writer, cycle);
        }
        // 批量服务暂停
        if (StringUtils.isNotBlank(batchOperType) && batchOperType.equals("SUSPENDSERVICE"))
        {
            batServiceChg(writer, cycle);
        }
        // 批量服务恢复
        if (StringUtils.isNotBlank(batchOperType) && batchOperType.equals("RESUMESERVICE"))
        {
            batServiceChg(writer, cycle);
        }
        // 批量业务平台业务
        if (StringUtils.isNotBlank(batchOperType) && batchOperType.equals("BATPLATFORM"))
        {
            batPlatForm(writer, cycle);
        }
        // 批量预开户
        if (StringUtils.isNotBlank(batchOperType) && batchOperType.equals("CREATEPREUSER"))
        {
            batPreCreateUser(writer, cycle);
        }
        // 批量异网预开户
        if (StringUtils.isNotBlank(batchOperType) && batchOperType.equals("BATOTHERCREATEUSER"))
        {
            batPreCreateHUser(writer, cycle);
        }
        // 批量买断开户
        if (StringUtils.isNotBlank(batchOperType) && batchOperType.equals("BATACTIVECREATEUSER"))
        {
            batActiveCreateUser(writer, cycle);
        }
        // 批量办理套餐
        if (StringUtils.isNotBlank(batchOperType) && batchOperType.equals("DISCNTCHG"))
        {
            batDiscntChg(writer, cycle);
        }
        // 批量办理特殊套餐
        if (StringUtils.isNotBlank(batchOperType) && batchOperType.equals("DISCNTCHGSPEC"))
        {
            batDiscntChgSpec(writer, cycle);
        }
        // 集团产品特殊优惠变更
        if (StringUtils.isNotBlank(batchOperType) && batchOperType.equals("GRPDISCNTCHGSPEC"))
        {
            batGrpDiscntChgSpec(writer, cycle);
        }
        // 固话批量装机
        if (StringUtils.isNotBlank(batchOperType) && batchOperType.equals("BATCREATEFIXEDUSER"))
        {
            batCreateFixedUser(writer, cycle);
        }
        // 千群百号装机
        if (StringUtils.isNotBlank(batchOperType) && batchOperType.equals("BATCREATETRUNKUSER"))
        {
            batCreateTrunkUser(writer, cycle);
        }
        // 物联网开户
        if (StringUtils.isNotBlank(batchOperType) && batchOperType.equals("CREATEPREUSER_PWLW"))
        {
            //batPreCreateUser(writer, cycle);
        	batCreateUserPWLW(writer, cycle);
        }
        // 行业应用卡批量开户
        if (StringUtils.isNotBlank(batchOperType) && batchOperType.equals("CREATEPREUSER_M2M"))
        {
        	batCreateUserM2M(writer, cycle);
        }
        // 商务宽带批量开户
        if (StringUtils.isNotBlank(batchOperType) && batchOperType.equals("CREATEPREUSER_BNBD"))
        {
        	batCreateUserBNBD(writer, cycle);
        }
        // 校园营销批量预开户
        if (StringUtils.isNotBlank(batchOperType) && batchOperType.equals("CREATEPREUSER_SCHOOL"))
        {
            batPreCreateUser(writer, cycle);
        }
        // 无线固话批量预开户
        if (StringUtils.isNotBlank(batchOperType) && batchOperType.equals("CREATEPRETDUSER"))
        {
            batPreCreateTDUser(writer, cycle);
        }
        // 营销活动（入乡情网送农信通）批量办理
        if (StringUtils.isNotBlank(batchOperType) && batchOperType.equals("COUNTRYNETACTIVE"))
        {
            batCountryNet(writer, cycle);
        }
        // 批量办理营销活动
        if (StringUtils.isNotBlank(batchOperType) && batchOperType.equals("SALEACTIVE"))
        {
            batSaleActive(writer, cycle);
        }
        // 批量办理营销活动终止
        if (StringUtils.isNotBlank(batchOperType) && batchOperType.equals("SALEACTIVEEND"))
        {
            batSaleActiveend(writer, cycle);
        }
        
        // 目标用户批量导入
        if (StringUtils.isNotBlank(batchOperType) && batchOperType.equals("INFOMANAGE"))
        {
            batHintMessageChg(writer, cycle);
        }
        // 批量修改账户银行资料
        if (StringUtils.isNotBlank(batchOperType) && batchOperType.equals("MODIFYACYCINFO"))
        {
            batModifyacycinfo(writer, cycle);
        }
        // 短信白名单
        if (StringUtils.isNotBlank(batchOperType) && batchOperType.equals("REDMEMBER"))
        {
            batRedMember(writer, cycle);
        }
        
        /**
         * 批量返销营销活动
         * 2016-01-25
         * chenxy3
         * */
        // 批量返销营销活动
        if (StringUtils.isNotBlank(batchOperType) && batchOperType.equals("BATACTIVECANCEL"))
        {
        	batActiveCancel(writer, cycle);
        }
    }
    
    /**
     * 批量返销营销活动
     * 2016-01-25
     * chenxy3
     * */
    private void batActiveCancel(IMarkupWriter writer, IRequestCycle cycle) throws Exception
    {
        IData params = new DataMap();
        params = getPage().getData();

        initBatPre(params);
        initProductSel(params);

        includeScript(writer, "scripts/person/bat/batdeal/batactivecancel.js");
        IDataset activeList = CSViewCall.call(this, "SS.BatActiveCancelSVC.queryCampnTypes", params);
        setBatcampnTypes(activeList);
    }
    
    public abstract void setBindMoreProducts(IDataset moreProductList);

    public abstract void setEditInfo(IData editinfo);

    public abstract void setElements(IDataset forceElements);

    public abstract void setEparchyCode(String eparchyCode);

    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);

    public abstract void setListA(IDataset listA);

    public abstract void setListB(IDataset listB);

    public abstract void setListC(IDataset listC);

    public abstract void setListD(IDataset listD);

    public abstract void setOperations(IDataset operations);

    public abstract void setPackageId(String packageId);

    public abstract void setProductTypeList(IDataset productTypeList);

    public abstract void setResId(String resId);
    
    public abstract void setBatcampnTypes(IDataset activeList); 

    public abstract void setPresentFeeList(IDataset PresentFeeList);//REQ201502050013放号政策调整需求 by songlm
    
    public abstract void setPresentFee(IData presentFee);//REQ201502050013放号政策调整需求 by songlm
}
