
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.opengroupmember;

import java.util.Iterator;

import net.sf.json.JSONArray;

import com.ailk.biz.util.Encryptor;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DESUtil;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCardException;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.exception.ParamException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.BizException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.group.common.GroupBaseConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.util.DataSetUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bizctrl.BizCtrlInfo;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.group.common.base.GrpModuleData;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.RouteInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AccountTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustPersonTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustomerTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.CCCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.bat.BatTaskInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustPersonInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.AttrBizInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserGrpInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.MemberBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.GroupImsIMPUUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.GroupModuleParserBean;

public class OpenGroupMemberBean extends MemberBean
{
    private String encryptImsPwd = "";
    
    // 用于本机联调测试
    public static IDataset checkResourceForIOTMphone(String occupy_type_code, String x_get_mode, String serial_number, String res_type_code, String depart_id) throws Exception
    {
        IData inData = new DataMap();
        inData.put("OCCUPY_TYPE_CODE", occupy_type_code);// 选占方式 0:开户选占 , 1:用户随机选占, 2－大屏幕选号, 3－预定
        inData.put("X_GET_MODE", x_get_mode); // 获取方式
        inData.put("SERIAL_NUMBER", serial_number); // 检测号码
        inData.put("RES_TYPE_CODE", res_type_code);// 资源类型
        inData.put("DEPART_ID", depart_id);// 选占部门
        setPublicParam(inData);

        return CSAppCall.call("http://192.168.100.208:7001/service", "TM.ResPhoneIntfSvc.preOccupyPhoneNum", inData, true);
        // return CSAppCall.callTerminal("TM.ResPhoneIntfSvc.selOccupyPhoneNum", inData, false).getData();
    }

    /**
     * 手机号选占资源,检查资源,选占资源
     * 
     * @param param
     * @return
     */
    public static IData checkResourceForMphone(IData param) throws Exception
    {

        IDataUtil.chkParam(param, "SERIAL_NUMBER");

        IDataset resultList = ResCall.checkResourceForMphone("0", param.getString("SERIAL_NUMBER"), "0"); // 调用资源接口检验手机号码是否被占用

        return resultList.size() > 0 ? resultList.getData(0) : new DataMap();
    }

    /**
     * 生成集团产品成员用户服务号码
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset genGrpMebSn(IData param) throws Exception
    {
        String productId = param.getString("PRODUCT_ID");
        String groupId = param.getString("GROUP_ID");

        String grpMebSn = genGrpMebSn(productId, groupId);

        IData data = new DataMap();
        data.put("GRP_MEB_SN", grpMebSn);

        IDataset dataset = new DatasetList();

        dataset.add(data);

        return dataset;
    }

    /**
     * 生成集团产品成员用户服务号码
     * 
     * @param productId
     * @param groupId
     * @return
     * @throws Exception
     */
    public static String genGrpMebSn(String productId, String groupId) throws Exception
    {

        IData data = new DataMap();

        // 得到服务号码生成规则配置
        data.put("ID", productId);
        data.put("ID_TYPE", "P");
        data.put("ATTR_OBJ", "0");
        data.put("ATTR_CODE", "genMebSn");

        IDataset dataList = AttrBizInfoQry.getBizAttr(productId, "P", "0", "genMebSn", null);
        if (dataList.size() == 0)
        {
            CSAppException.apperr(GrpException.CRM_GRP_486, productId);
        }
        // 得到配置数据
        data = dataList.getData(0);
        String attrValue = data.getString("ATTR_VALUE", "");

        // 得到配置名称，根据[;]
        String ruleName[] = attrValue.split(";");

        // 根据配置规则生成服务号码，如：[PRODUCT_ID,1,2;EPARCHY_CODE,3,4]
        String strEparchyCode = CSBizBean.getTradeEparchyCode();
        String seqVpmnId = "";
        String seqId = "";

        String sn = "";
        String tmp = "";

        String ruelValue[];
        String value = "";
        int valueMin = -1;
        int valueMax = -1;
        @SuppressWarnings("unused")
        int length = 0;

        for (String element : ruleName)
        {
            value = element;

            // 得到配置名称，根据[,]
            ruelValue = value.split(",");

            // 初始化
            value = "";
            valueMin = -1;
            valueMax = -1;

            // 得到配置项目
            for (int index = 0; index < ruelValue.length; index++)
            {
                tmp = ruelValue[index].trim();

                switch (index)
                {
                    case 0:
                    {
                        value = tmp; // 名称
                        break;
                    }
                    case 1:
                    {
                        if ((tmp == null) || tmp.equals(""))
                        {
                            valueMin = -1;
                        }
                        else
                        {
                            valueMin = Integer.valueOf(tmp); // sub小值
                        }
                        break;
                    }
                    case 2:
                    {
                        if ((tmp == null) || tmp.equals(""))
                        {
                            valueMax = -1;
                        }
                        else
                        {
                            valueMax = Integer.valueOf(tmp); // sub大值
                        }
                        break;
                    }
                }
            }

            if (value.equals("PRODUCT_ID"))
            {
                // 如果是产品
                if (productId.length() < 4)
                    productId += "00000";
                sn = sn + productId.substring(0, 4);
            }
            else if (value.equals("EPARCHY_CODE"))
            {
                // 如果是地州
                sn = sn + strEparchyCode.substring(valueMin, valueMax);
            }
            else if (value.equals("SEQ_VPMN_ID"))
            {
                // 如果是V网序列号
                seqVpmnId = SeqMgr.getVpmnIdIdForGrp();
                sn = sn + seqVpmnId.substring(valueMin, valueMax);
            }
            else if (value.equals("SEQ_ID"))
            {
                // 如果是序列号
                seqId = SeqMgr.getVpmnIdIdForGrp();
                sn = sn + seqId.substring(valueMin, valueMax);
            }
            else if (value.equals("GROUP_ID"))
            {
                // 集团编码
                sn = sn + groupId;
            }
            else if (value.equals("SELF_DEF"))
            {
                // 固定长度
                length = value.length();
                continue;
            }
            else
            {
                // 默认固定字符串
                sn = sn + value;
            }
        }
        return sn;
    }

    // 公共信息参数设置
    private static void setPublicParam(IData inData) throws Exception
    {
        inData.put(Route.USER_EPARCHY_CODE, CSBizBean.getUserEparchyCode());// 用户归属地州
        inData.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());// 用户交易地州
    }

    private final GrpModuleData moduleData = new GrpModuleData();

    protected OpenGroupMemberReqData reqData = null;

    /**
     * 账户信息
     * 
     * @throws Exception
     */
    public void actTradeAcctInfo() throws Exception
    {

        IData acctData = reqData.getUca().getAccount().toData();

        // 0 不合户 1合户
        if (acctData != null && "0".equals(acctData.getString("SAME_ACCT", "0")))
        {
            acctData.put("START_CYCLE_ID", SysDateMgr.getNowCyc()); // 开始时间
            acctData.put("END_CYCLE_ID", SysDateMgr.getEndCycle205012()); // 结束时间

            acctData.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());

            addTradeAccount(acctData);
        }

    }

    @Override
    public void actTradeBefore() throws Exception
    {
        super.actTradeBefore();

        // 处理产品信息
        GroupModuleParserBean.mebElement(reqData, moduleData);

        // 处理产品参数信息
        makReqDataProductParam();

        // 处理资源
        makReqDataRes();

    }

    /**
     * 组织客户Customer信息
     * 
     * @throws Exception
     */
    public void actTradeCustomer() throws Exception
    {

        IData customer = reqData.getUca().getCustomer().toData();

        customer.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());

        addTradeCustomer(customer);

    }

    /**
     * 组织客户CustPerson信息
     * 
     * @throws Exception
     */
    public void actTradeCustPerson() throws Exception
    {

        IData custperson = reqData.getUca().getCustPerson().toData();
        custperson.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
        addTradeCustPerson(custperson);
        //add by chenzg@20161121 REQ201611070005关于IMS产品开户界面的实名制优化的需求
        this.actTradeCustPersonOther();
        //add by chenzg@20170116 REQ201612280011关于开户等界面单位证件核验优化的需求
        this.actTradeOther();

    }
    /**
     * 组织客户责任人信息
     * @throws Exception
     * @Author:chenzg
     * @Date:2016-11-21
     */
    public void actTradeCustPersonOther() throws Exception{
    	IData pageRequestData = this.reqData.getPageRequestData();
    	if(IDataUtil.isNotEmpty(pageRequestData)){
    		IData memCustInfo = pageRequestData.getData("MEM_CUST_INFO");
    		if(IDataUtil.isNotEmpty(memCustInfo)){
    			String strRsrvstr2 = memCustInfo.getString("RSRV_STR2", "");	//责任人姓名
            	String strRsrvstr3 = memCustInfo.getString("RSRV_STR3", "");	//责任人证件类型
            	String strRsrvstr4 = memCustInfo.getString("RSRV_STR4", "");	//责任人证件号码
            	String strRsrvstr5 = memCustInfo.getString("RSRV_STR5", "");	//责任人证件地址
            	
            	if( StringUtils.isNotBlank(strRsrvstr2) || 
            		StringUtils.isNotBlank(strRsrvstr3) ||
                  	StringUtils.isNotBlank(strRsrvstr4) || 
                  	StringUtils.isNotBlank(strRsrvstr5) )
            	{
            		String strCustId = reqData.getUca().getCustPerson().getCustId();
                	String strPartition_id = strCustId.substring(strCustId.length() - 4);
                	String strTradeTpyeCode = pageRequestData.getString("TRADE_TYPE_CODE");
        			IDataset list = CustPersonInfoQry.qryCustPersonOtherByCustId(strCustId);
        			if( IDataUtil.isNotEmpty(list) ){
        				IData custPersonOtherData = list.first();
        	        	custPersonOtherData.put("UPDATE_TIME", SysDateMgr.getSysTime());
        	        	custPersonOtherData.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        	        	custPersonOtherData.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        	        	custPersonOtherData.put("RSRV_STR1", strTradeTpyeCode);
        	        	custPersonOtherData.put("RSRV_STR2", strRsrvstr2);
        	        	custPersonOtherData.put("RSRV_STR3", strRsrvstr3);
        	        	custPersonOtherData.put("RSRV_STR4", strRsrvstr4);
        	        	custPersonOtherData.put("RSRV_STR5", strRsrvstr5);
        				Dao.update("TF_F_CUST_PERSON_OTHER", custPersonOtherData, new String[] { "PARTITION_ID", "CUST_ID" });
        			}else{
        				IData custPersonOtherData = new DataMap();
        	        	custPersonOtherData.put("PARTITION_ID", strPartition_id);
        	        	custPersonOtherData.put("CUST_ID", strCustId);
        	        	custPersonOtherData.put("USE_NAME", "");
        	        	custPersonOtherData.put("USE_PSPT_TYPE_CODE", "");
        	        	custPersonOtherData.put("USE_PSPT_ID", "");
        	        	custPersonOtherData.put("USE_PSPT_ADDR", "");
        	        	custPersonOtherData.put("CREATE_TIME", SysDateMgr.getSysTime());
        	        	custPersonOtherData.put("UPDATE_TIME", SysDateMgr.getSysTime());
        	        	custPersonOtherData.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        	        	custPersonOtherData.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        	        	custPersonOtherData.put("REMARK", "集团成员开户-责任人录入");
        	        	custPersonOtherData.put("RSRV_STR1", strTradeTpyeCode);
        	        	custPersonOtherData.put("RSRV_STR2", strRsrvstr2);
        	        	custPersonOtherData.put("RSRV_STR3", strRsrvstr3);
        	        	custPersonOtherData.put("RSRV_STR4", strRsrvstr4);
        	        	custPersonOtherData.put("RSRV_STR5", strRsrvstr5);
        				Dao.insert("TF_F_CUST_PERSON_OTHER", custPersonOtherData);
        			}
        			
        			if(StringUtils.isNotEmpty(memCustInfo.getString("RSRV_STR2", ""))){
        	        	IData agent = new DataMap();
        	        	agent.put("CUST_ID", strCustId);
        	        	agent.put("PARTY_ROLE_SPEC_ID", "1004");//1002：经办人；1003：使用人；1004：责任人
        	        	agent.put("CUST_NAME",strRsrvstr2);
        	        	agent.put("PSPT_TYPE_CODE", strRsrvstr3);
        	        	agent.put("PSPT_ID", strRsrvstr4);
        	        	agent.put("PSPT_ADDR", strRsrvstr5);
        	        	CCCall.modifyAttnUserInfo(agent);
        	        }
        			
            	}
    		}
    	}
    }
    /**
     * 记录营业执照和组织机构信息
     * @throws Exception
     * @Author:chenzg
     * @Date:2017-1-16
     */
    public void actTradeOther() throws Exception
    {
    	IData pageRequestData = this.reqData.getPageRequestData();
    	if(IDataUtil.isNotEmpty(pageRequestData))
    	{
    		IData memCustInfo = pageRequestData.getData("MEM_CUST_INFO");
    		if(IDataUtil.isNotEmpty(memCustInfo))
    		{
    			String psptTypeCode = memCustInfo.getString("PSPT_TYPE_CODE", "");	//证件类型
    			//营业执照
    			if("E".equals(psptTypeCode))
    			{
    				String legalperson = memCustInfo.getString("legalperson","").trim();//法人
					String startdate = memCustInfo.getString("startdate","").trim();//成立日期
					String termstartdate = memCustInfo.getString("termstartdate","").trim();//营业开始时间
					String termenddate = memCustInfo.getString("termenddate","").trim();//营业结束时间
					OtherTradeData otherTD = new OtherTradeData();
					otherTD.setUserId(this.reqData.getUca().getUserId());
					otherTD.setRsrvValueCode("ENTERPRISE");
					otherTD.setRsrvValue("营业执照");
					otherTD.setRsrvStr1(legalperson);
					otherTD.setRsrvStr2(startdate);
					otherTD.setRsrvStr3(termstartdate);
					otherTD.setRsrvStr4(termenddate);
					otherTD.setRsrvStr5(CSBizBean.getVisit().getStaffName());
					otherTD.setStartDate(SysDateMgr.getSysTime());
					otherTD.setEndDate(SysDateMgr.getTheLastTime());
					otherTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
					otherTD.setStaffId(CSBizBean.getVisit().getStaffId());
					otherTD.setDepartId(CSBizBean.getVisit().getDepartId());
					otherTD.setInstId(SeqMgr.getInstId());
					this.addTradeOther(otherTD.toData());
    			}
    			//组织机构
    			else if("M".equals(psptTypeCode))
    			{
    				String orgtype = memCustInfo.getString("orgtype","").trim();//机构类型
    				String effectiveDate = memCustInfo.getString("effectiveDate","").trim();//有效日期
    				String expirationDate = memCustInfo.getString("expirationDate","").trim();//失效日期
    				OtherTradeData otherTD = new OtherTradeData();
    				otherTD.setUserId(this.reqData.getUca().getUserId());
    				otherTD.setRsrvValueCode("ORG");
    				otherTD.setRsrvValue("组织机构代码证");
    				otherTD.setRsrvStr1(orgtype);
    				otherTD.setRsrvStr2(effectiveDate);
    				otherTD.setRsrvStr3(expirationDate);
    				otherTD.setRsrvStr5(CSBizBean.getVisit().getStaffName());
    				otherTD.setStartDate(SysDateMgr.getSysTime());
    				otherTD.setEndDate(SysDateMgr.getTheLastTime());
    				otherTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
    				otherTD.setStaffId(CSBizBean.getVisit().getStaffId());
    				otherTD.setDepartId(CSBizBean.getVisit().getDepartId());
    				otherTD.setInstId(SeqMgr.getInstId());
    				this.addTradeOther(otherTD.toData());
    			}
    		}
    	}
    }

    /**
     * impu台帐表录入
     * 
     * @throws Exception
     */
    public void actTradeIMSIMPU() throws Exception
    {

        IData inData = new DataMap();

        inData.put("INST_ID", SeqMgr.getInstId());
        inData.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
        inData.put("USER_ID", reqData.getUca().getUserId());

        StringBuilder strImpi = new StringBuilder("");
        StringBuilder strTel = new StringBuilder("");
        StringBuilder strSip = new StringBuilder("");
        String custInfoTeltype = reqData.getCustInfoTeltype();

        // 号码是+86+去0的区号＋号码,如果开的号码是073166666666等号，把0去掉
        String serialNumber = reqData.getUca().getUser().getSerialNumber();
        /*
         * if (serialNumber.substring(0, 1).equals("0")) { serialNumber = serialNumber.substring(1); }
         */
        GroupImsIMPUUtil.genImsIMPI(serialNumber, strImpi, custInfoTeltype);
        GroupImsIMPUUtil.genImsIMPU(serialNumber, strTel, strSip, custInfoTeltype);

        inData.put("TEL_URL", strTel.toString());
        inData.put("SIP_URL", strSip.toString());
        inData.put("IMPI", strImpi.toString());
        inData.put("IMS_USER_ID", serialNumber);
        inData.put("IMS_PASSWORD", encryptImsPwd); // IMS门户网站密码 IMS密码15位 由字母+数字组成，区分字母大小写，且是由BOSS系统随机生成。 必须加密  关于优化IMS业务开通默认密码的需求  不在是123456
        inData.put("START_DATE", getAcceptTime());
        inData.put("END_DATE", SysDateMgr.getTheLastTime());

        // 用户类型:如固定电话用户、移动电话用户 0-固定用户 1-移动用户 2-PC客户端
        String userType = "";
        if (custInfoTeltype.equals("1"))
            userType = "0";
        else
            userType = "2";

        inData.put("RSRV_STR1", userType);

        // 登记一个ENUM的信息
        String tmp = strTel.toString();
        tmp = tmp.replaceAll("\\+", "");
        char[] c = tmp.toCharArray();
        String str2 = "";
        for (int i = c.length - 1; i >= 1; i--)
        {

            str2 += String.valueOf(c[i]);
            str2 += ".";
        }
        str2 += "6.8.e164.arpa";
        String str3 = "";
        for (int i = 3; i >= 1; i--)
        {

            str3 += String.valueOf(c[i]);
            str3 += ".";
        }
        str3 += "6.8.e164.arpa";
        inData.put("RSRV_STR2", str2);
        inData.put("RSRV_STR3", str3);
        inData.put("RSRV_STR4", "");
        inData.put("RSRV_STR5", "");

        addTradeImpu(inData);

        //预占
        ResCall.resEngrossForMphone(serialNumber);
    }

    /**
     * 普通付费关系
     * 
     * @throws Exception
     */
    public void actTradePayRela() throws Exception
    {

        IData data = new DataMap();

        data.put("ACCT_ID", reqData.getUca().getAcctId());
        data.put("USER_ID", reqData.getUca().getUserId());
        data.put("PAYITEM_CODE", "-1"); // 付费帐目编码
        data.put("ACCT_PRIORITY", "0"); // 帐户优先级：当一个用户的某个帐目由多个帐户为其付费时的顺序
        data.put("USER_PRIORITY", "0"); // 用户优先级：基于帐户做优惠时，作用在用户上按优先级进行
        data.put("BIND_TYPE", "0"); // 绑定帐户方式：0-按优先级，1-按金额几何平分
        data.put("ACT_TAG", "1"); // 作用标志：0-不作用，1-作用
        data.put("DEFAULT_TAG", "1"); // 默认标志
        data.put("LIMIT_TYPE", "0"); // 限定方式：0-不限定，1-金额，2-比例
        data.put("LIMIT", "0"); // 限定值
        data.put("COMPLEMENT_TAG", "0"); // 是否补足：0-不补足，1-补足
        data.put("INST_ID", SeqMgr.getInstId());
        data.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue()); // 状态属性：0-增加，1-删除，2-变更
        data.put("START_CYCLE_ID", SysDateMgr.getNowCycle());
        data.put("END_CYCLE_ID", SysDateMgr.getEndCycle20501231());

        this.addTradePayrelation(data);
    }

    /**
     * 产品子表
     * 
     * @param brandCode
     * @throws Exception
     */
    public void actTradeProduct() throws Exception
    {

        IData productIdset = reqData.cd.getProductIdSet();

        // 添加主产品信息
        productIdset.put(reqData.getUca().getProductId(), TRADE_MODIFY_TAG.Add.getValue());

        IDataset productInfoset = new DatasetList();
        Iterator<String> iterator = productIdset.keySet().iterator();
        while (iterator.hasNext())
        {
            IData productPlus = new DataMap();

            String key = iterator.next();

            productPlus.put("PRODUCT_ID", key); // 产品标识

            String productMode = UProductInfoQry.getProductModeByProductId(key);

            productPlus.put("PRODUCT_MODE", productMode); // 产品的模式
            // productPlus.put("MAIN_TAG", "1");

            productPlus.put("USER_ID_A", "-1");

            // 产品INST_ID
            String instId = SeqMgr.getInstId();
            productPlus.put("INST_ID", instId);
            productPlus.put("START_DATE", getAcceptTime()); // 开始时间
            productPlus.put("END_DATE", SysDateMgr.getTheLastTime()); // 结束时间
            productPlus.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());

            productInfoset.add(productPlus);

            if (productMode.equals(GroupBaseConst.PRODUCT_MODE.MEM_MAIN_PLUS_PRODUCT.getValue()) || productMode.equals(GroupBaseConst.PRODUCT_MODE.MEM_BASE_PRODUCT.getValue()))
            {

                IDataset productParam = reqData.cd.getProductParamList(key);
                if (IDataUtil.isNotEmpty(productParam))
                {
                    IDataset dataset = new DatasetList();
                    for (int i = 0, size = productParam.size(); i < size; i++)
                    {
                        IData paramData = productParam.getData(i);
                        String keyParam = paramData.getString("ATTR_CODE");
                        String valueParam = paramData.getString("ATTR_VALUE");

                        IData map = new DataMap();

                        map.put("INST_TYPE", "P");
                        map.put("RELA_INST_ID", instId);
                        map.put("INST_ID", SeqMgr.getInstId());
                        map.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());

                        map.put("USER_ID", reqData.getUca().getUser().getUserId());

                        map.put("ATTR_CODE", keyParam);
                        map.put("ATTR_VALUE", valueParam);

                        map.put("START_DATE", getAcceptTime());
                        map.put("END_DATE", SysDateMgr.getTheLastTime());

                        dataset.add(map);
                    }
                    super.addTradeAttr(dataset);
                }
            }
        }
        super.addTradeProduct(productInfoset);
    }

    /**
     * 生成其它台帐数据(生成台帐后)
     */
    @Override
    public void actTradeSub() throws Exception
    {

        super.actTradeSub();

        // 用户资料
        actTradeUser();

        // 产品子表
        actTradeProduct();

        // 付费关系
        actTradePayRela();

        // 服务状态表(重要,账务出账用到)
        actTradeSvcState();

        // 处理客户表信息
        actTradeCustomer();

        // 处理个人客户表信息
        actTradeCustPerson();

        // 处理账户信息
        actTradeAcctInfo();

        // impu台帐表录入
        actTradeIMSIMPU();
        
        
        /**
         * REQ201801150022_新增IMS号码开户人像比对功能
         * <br/>
         * IMS固话批量开户_摄像信息保存与同步
         * @author zhuoyingzhi
         * @date 20180330
         */
        addIMSBatPicInfoTradeUserOther();
        //单个集团成员开户
        addIMSPicInfoTradeUserOther();
    }

    /**
     * 用户资料
     * 
     * @param brandCode
     * @throws Exception
     */
    public void actTradeUser() throws Exception
    {

        IData userData = reqData.getUca().getUser().toData();

        if (IDataUtil.isNotEmpty(userData))
        {
            userData.put("USER_TYPE_CODE", "V"); // IMS用户类型V

            String userId = reqData.getUca().getUserId();

            String userPasswd = "123456"; // 初始用户密码
            userPasswd = Encryptor.fnEncrypt(userPasswd, userId.substring(userId.length() - 9));
            userData.put("USER_PASSWD", userPasswd);

            userData.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());

            String productId = reqData.getUca().getProductId();

            userData.put("USER_DIFF_CODE", "0");
            if (productId.equals("6105"))
                userData.put("ACCT_TAG", "Z");
            else
                userData.put("ACCT_TAG", "0");
        }

        addTradeUser(userData);
    }

    /**
     * 作用：对手机号码有效性的校验 0--手机号码
     * 
     * @param param
     * @return 有价卡信息
     */
    public IData checkMpNumber(IData param) throws Exception
    {

        String serialNumber = param.getString("RES_VALUE", "");

        if ("".equals(serialNumber))
            CSAppException.apperr(ParamException.CRM_PARAM_268);

        // 手机号选占资源,检查资源,选占资源
        IData checkResNumber = checkResourceForMphone(param);
        // 弹出信息提示
        redirectToMsgForCallSvc(checkResNumber);

        if (null != checkResNumber && "0".equals(checkResNumber.getString("X_RESULTCODE")))
        {
            IDataset resDataSet = new DatasetList();

            IData resTmp = new DataMap();
            resTmp.put("RES_CODE", serialNumber);
            resTmp.put("MOFFICE_ID", checkResNumber.getString("MOFFICE_ID"));
            resTmp.put("RES_TYPE_CODE", param.getString("RES_TYPE_CODE"));
            resTmp.put("RES_TYPE", param.getString("RES_TYPE"));
            resTmp.put("IMSI", checkResNumber.getString("IMSI"));
            resTmp.put("SIM_CARD_NO", checkResNumber.getString("SIM_CARD_NO"));

            resDataSet.add(resTmp);

            checkResNumber.put("RES_LIST", resDataSet);

            String simCardNo = checkResNumber.getString("SIM_CARD_NO", "");
            if (!"".equals(simCardNo))
            {
                IData resData = new DataMap();
                resData.put("RES_VALUE", simCardNo);
                resData.put("RES_TYPE", "1");

                // 对SIM卡进行有效性的校验
                IData simCard = checkSimResource(resData);
                if (null == simCard)
                {
                    CSAppException.apperr(CrmCardException.CRM_CARD_128);
                }
            }

        }

        return checkResNumber;

    }

    /**
     * 作用：对外省移动号码进行是否校验 1、判断号码是否移动号段 2、判断号码是否本省移动号码 3、判断号码是否已经存在 M--外省移动号码
     * 
     * @author
     * @param param
     * @return data
     */
    public IData checkOtherProvincesMobileNumber(IData param) throws Exception
    {

        String serialNumber = param.getString("RES_VALUE", "");
        String restypecode = param.getString("RES_TYPE_CODE");
        String restype = param.getString("RES_TYPE");

        IData result = new DataMap();

        // 判断号码是否移动号段
        // 判断是否中国移动号段 GroupUtil.isMobileNumber 写死的，需要修改为从参数表TD_M_CODEAREA中获取号段前三位
        boolean isMobNumFlag = RouteInfoQry.isChinaMobileNumber(serialNumber);
        if (!isMobNumFlag)
        {
            result.put("X_RESULTCODE", "-1");
            result.put("X_RESULTINFO", serialNumber + "号码非移动号段，请输入新号码！");
        }
        else
        {
            // 判断号码是否本省移动号码
            String mofficeInfo = RouteInfoQry.getEparchyCodeBySn(serialNumber);
            if (StringUtils.isNotBlank(mofficeInfo))
            {
                result.put("X_RESULTCODE", "-1");
                result.put("X_RESULTINFO", serialNumber + "号码为本省移动号码，请输入新号码！");
            }
            else
            {
                // 判断号码是否已经存在(查询CG库及4个CRM库)
                String userInfo = RouteInfoQry.getEparchyCodeBySnForCrm(serialNumber);

                if (StringUtils.isNotBlank(userInfo))
                {
                    result.put("X_RESULTCODE", "-1");
                    result.put("X_RESULTINFO", serialNumber + "号码已经生成了资料，请重新输入新号码！");
                }
                else
                {
                    result.put("X_RESULTCODE", "0");
                    result.put("X_RESULTINFO", "OK");

                    IDataset resDataSet = new DatasetList();
                    IData resTmp = new DataMap();
                    resTmp.put("RES_CODE", serialNumber);
                    resTmp.put("RES_TYPE_CODE", restypecode);
                    resTmp.put("RES_TYPE", restype);
                    resDataSet.add(resTmp);

                    result.put("RES_LIST", resDataSet);

                }
            }
        }

        return result;

    }

    /**
     * 对外网号码进行是否重复校验 1、判断是否本省移动号码 2、判断系统中是否已经有号码 H--网外号码
     * 
     * @param param
     * @return data
     */
    public IData checkOuterNumber(IData param) throws Exception
    {
        String productID = param.getString("PRODUCT_ID", "");
        String serialNumber = param.getString("RES_VALUE", "");
        String restypecode = param.getString("RES_TYPE_CODE");
        String restype = param.getString("RES_TYPE");

        IData result = new DataMap();

        IDataset userInfos = UserGrpInfoQry.getMemberUserInfoBySn(serialNumber);
        if (IDataUtil.isNotEmpty(userInfos))
        {
            result.put("X_RESULTCODE", "-1");
            result.put("X_RESULTINFO", serialNumber + "号码已经生成了资料，请输入新号码！");
        }
        else
        {
            String brandCode = UProductInfoQry.getBrandCodeByProductId(productID);

            if ("IMSG".equals(brandCode))
            {
                String str = serialNumber.substring(0, 4);
                if (!"0898".equals(str))
                {
                    result.put("X_RESULTCODE", "-1");
                    result.put("X_RESULTINFO", serialNumber + "号码非固话号码，IMS语音成员用户开户必须为固话号码，请输入新号码！");
                }
                else
                {
                    IData verifyParam = new DataMap();
                    verifyParam.put("TRADE_TYPE_CODE", "3008");
                    verifyParam.put("SERIAL_NUMBER", serialNumber);
                    verifyParam.put(Route.ROUTE_EPARCHY_CODE, param.getString("ROUTE_EPARCHY_CODE", "0898"));
                    CSAppCall.call( "CS.CheckTradeSVC.verifyUnFinishTrade", verifyParam);
                    
                    // 选占
                    ResCall.checkResourceForMphone("0", serialNumber, "0");
                    result.put("X_RESULTCODE", "0");
                    result.put("X_RESULTINFO", "OK");

                    IDataset resDataSet = new DatasetList();
                    IData resTmp = new DataMap();
                    resTmp.put("RES_CODE", serialNumber);
                    resTmp.put("RES_TYPE_CODE", restypecode);
                    resTmp.put("RES_TYPE", restype);
                    resDataSet.add(resTmp);

                    result.put("RES_LIST", resDataSet);
                }
            }
            else
            {
                // 其他产品
                result.put("X_RESULTCODE", "0");
                result.put("X_RESULTINFO", "OK");

                IDataset resDataSet = new DatasetList();
                IData resTmp = new DataMap();
                resTmp.put("RES_CODE", serialNumber);
                resTmp.put("RES_TYPE_CODE", restypecode);
                resTmp.put("RES_TYPE", restype);
                resDataSet.add(resTmp);

                result.put("RES_LIST", resDataSet);
            }

        }
        return result;
    }

    /**
     * SIM资源可用校验
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public IData checkResourceForSim(IData param) throws Exception
    {

        IDataset resultList = ResCall.checkResourceForSim("0", param.getString("RES_VALUE", ""), param.getString("SIM_CARD_NO"), "1"); // 调资源接口检验SIM卡资源

        return resultList.size() > 0 ? resultList.getData(0) : new DataMap();
    }

    /**
     * 资源信息校验总方法
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public IData checkResourceSn(IData param) throws Exception
    {

        IData resultData = null;

        String resTypeCode = param.getString("RES_TYPE_CODE");

        if ("0".equals(resTypeCode))
        {
            resultData = checkOuterNumber(param);
        }
        else if ("1".equals(resTypeCode))
        {
            resultData = checkSimResource(param);
        }
        else if ("W".equals(resTypeCode))
        {
            resultData = checkOuterNumber(param);
        }

        return resultData;

    }

    /**
     * 对SIM卡进行有效性的校验 1--SIM卡号
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public IData checkSimResource(IData param) throws Exception
    {

        String simCardNo = param.getString("RES_VALUE", "");
        param.put("SIM_CARD_NO", simCardNo);
        if ("".equals(simCardNo))
            CSAppException.apperr(ParamException.CRM_PARAM_206);

        // SIM资源可用校验
        IData checkSimData = checkResourceForSim(param);

        redirectToMsgForCallSvc(checkSimData);

        if (null != checkSimData && "0".equals(checkSimData.getString("X_RESULTCODE")))
        {

            IDataset resDataSet = new DatasetList();

            IData resTmp = new DataMap();
            resTmp.put("IMSI", checkSimData.getString("IMSI"));
            resTmp.put("RES_CODE", checkSimData.getString("SIM_CARD_NO"));
            resTmp.put("RES_TYPE_CODE", param.getString("RES_TYPE"));
            resTmp.put("KI", checkSimData.getString("KI"));
            resTmp.put("MOFFICE_ID", checkSimData.getString("MOFFICE_ID"));
            resDataSet.add(resTmp);

            checkSimData.put("RES_LIST", resDataSet);

        }

        return checkSimData;

    }

    @Override
    protected BaseReqData getReqData() throws Exception
    {
        return new OpenGroupMemberReqData();
    }

    @Override
    protected final void initProductCtrlInfo() throws Exception
    {
        String productId = reqData.getUca().getProductId();
        getProductCtrlInfo(productId, BizCtrlType.OpenGroupMeb);
    }

    @Override
    protected void initReqData() throws Exception
    {
        super.initReqData();
        reqData = (OpenGroupMemberReqData) getBaseReqData();
    }

    @Override
    protected final void makInit(IData map) throws Exception
    {
        super.makInit(map);
        moduleData.getMoudleInfo(map);
        if(IDataUtil.isEmpty(reqData.getPageRequestData())){
        	reqData.setPageRequestData(map);
        }
    }

    @Override
    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);

        // 获取资源信息
        IDataset resList = moduleData.getResInfo();
        reqData.cd.putRes(resList);

        // 获取产品参数信息
        IDataset productParamInfo = moduleData.getProductParamInfo();
        reqData.setProductParamInfo(productParamInfo);

        // 获取产品信息
        IDataset productInfo = moduleData.getElementInfo();
        reqData.setProductInfo(productInfo);

        reqData.setCustInfoTeltype(map.getString("CUST_INFO_TELTYPE"));

        BizCtrlInfo crtlInfo = reqData.getProductCtrlInfo(reqData.getUca().getProductId());
        String tradeTypeCode = crtlInfo.getTradeTypeCode();
        if (StringUtils.isNotBlank(tradeTypeCode))
            map.put("TRADE_TYPE_CODE", tradeTypeCode);
        
        //生成IMS密码15位 由字母+数字组成，区分字母大小写，且是由BOSS系统随机生成。 必须加密  关于优化IMS业务开通默认密码的需求
        String imsPwd = StrUtil.getRandomNumAndChar(15);
        encryptImsPwd = DESUtil.encrypt(imsPwd);//加密。 服开再解密
        /**
         * REQ201801150022_新增IMS号码开户人像比对功能
         * @author zhuoyingzhi
         * @date 20180330
         */
        reqData.setbatchId(map.getString("BATCH_ID"));
        //System.out.println("--OpenGroupMemberBean--map:"+map);
    }

    /**
     * 组织产品参数信息
     * 
     * @throws Exception
     */
    private void makReqDataProductParam() throws Exception
    {

        // 产品参数
        IDataset productParamInfos = reqData.getProductParamInfo();

        // 处理用户产品和产品参数
        if (productParamInfos != null && productParamInfos.size() > 0)
        {
            for (int i = 0, size = productParamInfos.size(); i < size; i++)
            {
                // 产品参数
                IData productParam = productParamInfos.getData(i);
                if (productParam != null)
                {
                    String productId = productParam.getString("PRODUCT_ID");
                    IDataset productAttr = productParam.getDataset("PRODUCT_PARAM");
                    reqData.cd.putProductParamList(productId, productAttr);
                }
            }
        }

    }

    /**
     * 处理资源
     * 
     * @throws Exception
     */
    public void makReqDataRes() throws Exception
    {

        IDataset resDataset = new DatasetList();

        // 其他资源
        IDataset resDatas = reqData.cd.getRes();
        if (resDatas != null && resDatas.size() > 0)
        {
            for (int i = 0, size = resDatas.size(); i < size; i++)
            {
                IData resData = resDatas.getData(i);

                resData.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                resData.put("IMSI", "0"); // IMSI
                resData.put("START_DATE", getAcceptTime());
                resData.put("END_DATE", SysDateMgr.getTheLastTime());
                resData.put("INST_ID", SeqMgr.getInstId());
                resData.put("USER_ID", reqData.getUca().getUserId());
                resData.put("USER_ID_A", "-1");
                if(StringUtils.isNotEmpty(reqData.getUca().getSerialNumber()))
                {
                    resData.put("RES_CODE", reqData.getUca().getSerialNumber());
                }
                resDataset.add(resData);
            }
        }

        reqData.cd.putRes(resDataset);

    }

    @Override
    protected final void makUca(IData map) throws Exception
    {
        String productId = map.getString("PRODUCT_ID", "");
        String grpCustId = map.getString("CUST_ID", "");

        String brandCode = UProductInfoQry.getBrandCodeByProductId(productId);
        IData productInfo = UProductInfoQry.qryProductByPK(productId);

        String netTypeCode = productInfo.getString("NET_TYPE_CODE", "00");

        UcaData uca = new UcaData();
        UcaData grpUca = new UcaData();

        String custId = SeqMgr.getCustId();

        IData memCustInfo = map.getData("MEM_CUST_INFO");
        IData memUserInfo = map.getData("MEM_USER_INFO");
        IData memAcctInfo = map.getData("MEM_ACCT_INFO");

        // 客户信息
        IData customerData = new DataMap();
        if (brandCode.equals("SRLG"))
            customerData.put("CUST_ID", grpCustId);
        else
            customerData.put("CUST_ID", custId);

        customerData.put("CUST_NAME", memCustInfo.getString("CUST_NAME", ""));
        customerData.put("CUST_TYPE", "0"); // 暂定为0
        customerData.put("CUST_STATE", "0");
        customerData.put("IS_REAL_NAME", memCustInfo.getString("REAL_NAME", "0"));
        customerData.put("PSPT_TYPE_CODE", memCustInfo.getString("PSPT_TYPE_CODE", ""));
        customerData.put("PSPT_ID", memCustInfo.getString("PSPT_ID", ""));
        customerData.put("OPEN_LIMIT", "0");
        customerData.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        customerData.put("CITY_CODE", CSBizBean.getVisit().getCityCode());
        customerData.put("IN_STAFF_ID", CSBizBean.getVisit().getStaffId());
        customerData.put("IN_DEPART_ID", CSBizBean.getVisit().getDepartId());
        customerData.put("IN_DATE", getAcceptTime());
        customerData.put("REMOVE_TAG", "0");
        customerData.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        customerData.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        customerData.put("UPDATE_TIME", getAcceptTime());
        customerData.put("REMARK", memUserInfo.getString("REMARK", ""));
        //--------add by chenzg@20161118--begin---REQ201611070005关于IMS产品开户界面的实名制优化的需求--------------
        customerData.put("RSRV_STR7", memCustInfo.getString("AGENT_CUST_NAME", ""));			//经办人名称
        customerData.put("RSRV_STR8", memCustInfo.getString("AGENT_PSPT_TYPE_CODE", ""));		//经办人证件类型
        customerData.put("RSRV_STR9", memCustInfo.getString("AGENT_PSPT_ID", ""));				//经办人证件号码
        customerData.put("RSRV_STR10", memCustInfo.getString("AGENT_PSPT_ADDR", ""));			//经办人证件地址
        
        if(StringUtils.isNotEmpty(memCustInfo.getString("AGENT_CUST_NAME", ""))){
        	IData agent = new DataMap();
        	agent.put("CUST_ID", customerData.getString("CUST_ID"));
        	agent.put("PARTY_ROLE_SPEC_ID", "1002");//1002：经办人；1003：使用人；1004：责任人
        	agent.put("CUST_NAME", customerData.getString("RSRV_STR7"));
        	agent.put("PSPT_TYPE_CODE", customerData.getString("RSRV_STR8"));
        	agent.put("PSPT_ID", customerData.getString("RSRV_STR9"));
        	agent.put("PSPT_ADDR", customerData.getString("RSRV_STR10"));
        	CCCall.modifyAttnUserInfo(agent);
        }
        
        //--------add by chenzg@20161118--end-----REQ201611070005关于IMS产品开户界面的实名制优化的需求--------------
        CustomerTradeData customer = new CustomerTradeData(customerData);
        uca.setCustomer(customer);

        // 个人客户信息
        IData custPersonData = new DataMap();
        if (brandCode.equals("SRLG"))
            custPersonData.put("CUST_ID", grpCustId);
        else
            custPersonData.put("CUST_ID", custId);
        String birthday = memCustInfo.getString("BIRTHDAY", "");
        if(birthday == null || "".equals(birthday))
        {
        	custPersonData.put("BIRTHDAY", "1900-01-01");
        }
        else
        {
        	custPersonData.put("BIRTHDAY", birthday);
        }
        custPersonData.put("PSPT_TYPE_CODE", memCustInfo.getString("PSPT_TYPE_CODE", ""));
        custPersonData.put("PSPT_ID", memCustInfo.getString("PSPT_ID", ""));
        custPersonData.put("PSPT_ADDR", memCustInfo.getString("PSPT_ADDRESS", ""));
        custPersonData.put("CUST_NAME", memCustInfo.getString("CUST_NAME", ""));
        custPersonData.put("POST_CODE", memCustInfo.getString("POST_CODE", ""));
        custPersonData.put("CONTACT_PHONE", memCustInfo.getString("CONTACT_PHONE", ""));
        custPersonData.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        custPersonData.put("CITY_CODE", CSBizBean.getVisit().getCityCode());
        custPersonData.put("REMOVE_TAG", "0");
        custPersonData.put("REMARK", memUserInfo.getString("REMARK", ""));
        //--------add by chenzg@20161118--begin---REQ201611070005关于IMS产品开户界面的实名制优化的需求--------------
        custPersonData.put("RSRV_STR5", memCustInfo.getString("USE", ""));						//使用人姓名
        custPersonData.put("RSRV_STR6", memCustInfo.getString("USE_PSPT_TYPE_CODE", ""));		//使用人证件类型
        custPersonData.put("RSRV_STR7", memCustInfo.getString("USE_PSPT_ID", ""));				//使用人证件号码
        custPersonData.put("RSRV_STR8", memCustInfo.getString("USE_PSPT_ADDR", ""));			//使用人证件地址
        
        if(StringUtils.isNotEmpty(memCustInfo.getString("USE", ""))){
        	IData agent = new DataMap();
        	agent.put("CUST_ID", custPersonData.getString("CUST_ID"));
        	agent.put("PARTY_ROLE_SPEC_ID", "1003");//1002：经办人；1003：使用人；1004：责任人
        	agent.put("CUST_NAME", custPersonData.getString("RSRV_STR5"));
        	agent.put("PSPT_TYPE_CODE", custPersonData.getString("RSRV_STR6"));
        	agent.put("PSPT_ID", custPersonData.getString("RSRV_STR7"));
        	agent.put("PSPT_ADDR", custPersonData.getString("RSRV_STR8"));
        	CCCall.modifyAttnUserInfo(agent);
        }
        
        //--------add by chenzg@20161118--end-----REQ201611070005关于IMS产品开户界面的实名制优化的需求--------------
        custPersonData.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        custPersonData.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        custPersonData.put("UPDATE_TIME", getAcceptTime());
        CustPersonTradeData custperson = new CustPersonTradeData(custPersonData);
        uca.setCustPerson(custperson);

        // 用户信息
        IData userData = new DataMap();

        String userid = SeqMgr.getUserId();

        userData.put("USER_ID", userid);
        userData.put("CUST_ID", customerData.getString("CUST_ID"));
        userData.put("USECUST_ID", customerData.getString("CUST_ID"));
        userData.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode()); // 归属地市
        userData.put("CITY_CODE", CSBizBean.getVisit().getCityCode());

        userData.put("USER_TYPE_CODE", memUserInfo.getString("USER_TYPE_CODE", "")); // 用户类型
        userData.put("USER_STATE_CODESET", "0");

        userData.put("NET_TYPE_CODE", netTypeCode);
        userData.put("SERIAL_NUMBER", map.getString("SERIAL_NUMBER", "")); // 服务号码

        userData.put("IN_DATE", getAcceptTime());
        userData.put("IN_STAFF_ID", CSBizBean.getVisit().getStaffId());
        userData.put("IN_DEPART_ID", CSBizBean.getVisit().getDepartId());

        userData.put("OPEN_MODE", "0");
        userData.put("OPEN_DATE", getAcceptTime());
        userData.put("OPEN_STAFF_ID", CSBizBean.getVisit().getStaffId());
        userData.put("OPEN_DEPART_ID", CSBizBean.getVisit().getDepartId());

        userData.put("DEVELOP_DEPART_ID", CSBizBean.getVisit().getDepartId());
        userData.put("DEVELOP_CITY_CODE", CSBizBean.getVisit().getCityCode());

        userData.put("REMOVE_TAG", "0");

        userData.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        userData.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        userData.put("UPDATE_TIME", getAcceptTime());

        userData.put("REMARK", memUserInfo.getString("REMARK", ""));

        UserTradeData user = new UserTradeData(userData);
        uca.setUser(user);

        // 账户信息
        IData accountData = new DataMap();

        String acctId = SeqMgr.getAcctId();

        accountData.put("ACCT_ID", acctId);

        accountData.put("CUST_ID", customerData.getString("CUST_ID"));
        accountData.put("PAY_NAME", memAcctInfo.getString("PAY_NAME", ""));
        accountData.put("PAY_MODE_CODE", memAcctInfo.getString("PAY_MODE_CODE", ""));
        accountData.put("NET_TYPE_CODE", netTypeCode);
        accountData.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        accountData.put("CITY_CODE", CSBizBean.getVisit().getCityCode());
        accountData.put("SCORE_VALUE", "0");
        accountData.put("BASIC_CREDIT_VALUE", "0");
        accountData.put("CREDIT_VALUE", "0");
        accountData.put("REMOVE_TAG", "0");
        accountData.put("OPEN_DATE", getAcceptTime());
        accountData.put("ACCT_DIFF_CODE", "0");

        accountData.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        accountData.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        accountData.put("UPDATE_TIME", getAcceptTime());

        AccountTradeData account = new AccountTradeData(accountData);
        uca.setAccount(account);

        uca.setProductId(productId);
        uca.setBrandCode(userData.getString("BRAND_CODE", UProductInfoQry.getBrandCodeByProductId(productId)));

        // 成员的uca
        reqData.setUca(uca);

        // 集团信息
        UserTradeData grpUserData = new UserTradeData();
        grpUserData.setCustId(grpCustId);

        grpUserData.setUserId("-1");
        grpUserData.setSerialNumber("-1");

        grpUca.setUser(grpUserData);

        grpUca.setProductId(productId);

        AccountTradeData grpAccount = new AccountTradeData();

        grpAccount.setAcctId("-1");

        grpUca.setAccount(grpAccount);

        // 集团的uca
        reqData.setGrpUca(grpUca);
    }

    /**
     * 弹出信息提示
     * 
     * @param obj
     * @throws Exception
     */
    public void redirectToMsgForCallSvc(Object obj) throws Exception
    {
        String xResultCode = "";
        String xResultInfo = "";
        IData xResultData = null;
        if (obj instanceof IDataset)
        {
            xResultData = ((IDataset) obj).getData(0);
        }
        else if (obj instanceof IData)
        {
            xResultData = ((IData) obj);
        }
        xResultCode = xResultData.getString("X_RESULTCODE", "0");
        xResultInfo = xResultData.getString("X_RESULTINFO", "");
        if (!"0".equals(xResultCode))
        {
            CSAppException.apperr(BizException.CRM_BIZ_5, xResultInfo);
        }
    }

    @Override
    protected void regTrade() throws Exception
    {
        super.regTrade();

        IData data = bizData.getTrade();

        data.put("NET_TYPE_CODE", reqData.getUca().getUser().getNetTypeCode());
    }

    @Override
    protected void setTradeAttr(IData map) throws Exception
    {
        super.setTradeAttr(map);
        map.put("USER_ID", reqData.getUca().getUserId());
    }

    @Override
    protected void setTradeDiscnt(IData map) throws Exception
    {
        super.setTradeDiscnt(map);

        map.put("USER_ID", map.getString("USER_ID", reqData.getUca().getUserId()));// 用户标识
        map.put("USER_ID_A", map.getString("USER_ID_A", reqData.getGrpUca().getUserId()));

        map.put("DISCNT_CODE", map.getString("ELEMENT_ID", ""));// 优惠编码
        map.put("SPEC_TAG", map.getString("SPEC_TAG", "0")); // 特殊优惠标记
        map.put("RELATION_TYPE_CODE", map.getString("RELATION_TYPE_CODE")); // 关系类型
        map.put("INST_ID", map.getString("INST_ID", "0"));// 实例标识

        map.put("START_DATE", map.getString("START_DATE", "").equals("") ? getAcceptTime() : map.getString("START_DATE"));
        map.put("END_DATE", map.getString("END_DATE", "").equals("") ? SysDateMgr.getEndCycle20501231() : map.getString("END_DATE"));// 结束时间
    }

    @Override
    protected void setTradePayrelation(IData map) throws Exception
    {
        super.setTradePayrelation(map);

        map.put("USER_ID", map.getString("USER_ID", reqData.getUca().getUserId()));
        map.put("ACCT_ID", map.getString("ACCT_ID", reqData.getUca().getAcctId()));
        map.put("INST_ID", SeqMgr.getInstId()); // 实例标识
    }

    @Override
    protected void setTradeProduct(IData map) throws Exception
    {
        super.setTradeProduct(map);
        map.put("USER_ID", map.getString("USER_ID", reqData.getUca().getUserId()));// 用户标识);// 用户标识
        map.put("USER_ID_A", map.getString("USER_ID_A", reqData.getGrpUca().getUserId()));

        String productId = map.getString("PRODUCT_ID");
        map.put("PRODUCT_ID", productId); // 产品标识
        map.put("PRODUCT_MODE", map.getString("PRODUCT_MODE", "00"));
        String brandCode = UProductInfoQry.getBrandCodeByProductId(productId);
        map.put("BRAND_CODE", brandCode); // 品牌编码
        map.put("INST_ID", map.getString("INST_ID", "0")); // 实例标识
        map.put("START_DATE", map.getString("START_DATE", SysDateMgr.getSysTime()));
        map.put("END_DATE", map.getString("END_DATE", SysDateMgr.getTheLastTime()));

        map.put("MAIN_TAG", map.getString("PRODUCT_MODE").equals(GroupBaseConst.PRODUCT_MODE.MEM_BASE_PRODUCT.toString()) ? "1" : "0");// 主产品标记：0-否，1-是
    }

    @Override
    protected void setTradeSvc(IData map) throws Exception
    {
        super.setTradeSvc(map);
        map.put("SERVICE_ID", map.getString("ELEMENT_ID", ""));// 服务标识
        map.put("USER_ID", reqData.getUca().getUser().getUserId());
        map.put("USER_ID_A", reqData.getGrpUca().getUser().getUserId());// 用户标识A
        map.put("PACKAGE_ID", map.getString("PACKAGE_ID", "0"));// 包标识
        map.put("INST_ID", SeqMgr.getInstId());
        map.put("MAIN_TAG", map.getString("MAIN_TAG", "0"));// 主体服务标志
    }

    @Override
    protected void setTradeUser(IData map) throws Exception
    {
        super.setTradeUser(map);

        map.put("PREPAY_TAG", "0");
        map.put("MPUTE_MONTH_FEE", "0");
        map.put("ACCT_TAG", "0");

        // String brandCode = BrandInfoQry.getBrandCodeByProductId(reqData.getUca().getProductId());
        // map.put("BRAND_CODE", brandCode);

    }
    
    /**
     * REQ201801150022_新增IMS号码开户人像比对功能
     * <br/>
     * IMS固话批量开户,图片保存other表
     * @throws Exception
     * @author zhuoyingzhi
     * @date 20180329
     */
    public void addIMSBatPicInfoTradeUserOther() throws Exception{
    	String productId =  reqData.getUca().getProductId();
		IData otherData = new DataMap();
		//固话云视讯
		if("801111".equals(productId)){
	            //上面的productId不配会引起空指针异常 先写成801111，后面根据产商品配置修改代码
	            otherData.put("USER_ID", reqData.getUca().getUserId());//userId
	            otherData.put("RSRV_VALUE_CODE", "ENUM");
	            otherData.put("RSRV_VALUE", "固化云视讯DNS/ENUM服务");
	            otherData.put("RSRV_STR9", "8173");// 用于服务开通，service_id
	            otherData.put("OPER_CODE", "0");// 用于服务开通，注册用0
	            otherData.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
	            otherData.put("START_DATE", reqData.getAcceptTime());
	            otherData.put("END_DATE", SysDateMgr.getTheLastTime());
	            otherData.put("INST_ID", SeqMgr.getInstId());
	            otherData.put("IS_NEED_PF", "1");//貌似是传1发服开，0不发服开
	            super.addTradeOther(otherData);
	        }
    	IData pageRequestData = this.reqData.getPageRequestData();
    	if(IDataUtil.isNotEmpty(pageRequestData))
    	{
    		String  batchOperType=pageRequestData.getString("BATCH_OPER_TYPE", "");
    		
    		System.out.println("addPicInfoTradeUserOther--batchOperType:"+batchOperType+",pageRequestData:"+pageRequestData);
    		
    		if("BATOPENGROUPMEM".equals(batchOperType)){
    			//IMS固话批量开户
    			IData param = new DataMap();
    			
    			//台帐里面的batchid对应的批量任务里面的operateId,所以要获取真正的batchid就需要换成一下
    			IData batdeal=getBatdealBatchId(reqData.getBatchId());
  
    			//System.out.println("----addIMSPicInfoTradeUserOthertradeId-----batdeal:"+batdeal);
    			
    			//任务批次编码
    			String batchTaskId=batdeal.getString("BATCH_TASK_ID","");
    			//请求参数
    			IData  batTaskInfo=BatTaskInfoQry.qryBatTaskByBatchTaskId(batchTaskId);
    			//System.out.println("addPicInfoTradeUserOther--batdeal:"+batdeal+",batchId:"+batchId+",tradeId:"+tradeId+",batTaskInfo:"+batTaskInfo);
    			
    			JSONArray array_rx = new JSONArray();
    			array_rx.element(batTaskInfo.getString("CODING_STR1", ""));
    			DatasetList ds_rx = new DatasetList(array_rx.toString());
                //客户图片
                String	picId="";
                //经办人图片
            	String  angentpicId="";
            	
	    		if (DataSetUtils.isNotBlank(ds_rx)) {
	    			    //客户摄像标识
	    			     picId=ds_rx.getData(0).getString("custInfo_PIC_ID","");
	    				//经办人摄像标识
	    				angentpicId=ds_rx.getData(0).getString("custInfo_AGENT_PIC_ID","");
	                	//0-已采集，1-未采集
	                	if(!"".equals(picId)&&picId != null){
	                		//客户已经摄像
	                		param.put("PIC_TAG", "0");
	                	}else{
	                    	if(!"".equals(angentpicId)&&angentpicId != null){
	                    		//经办人已经摄像
	                    		param.put("AGENT_PIC_TAG", "0");
	                    	}else{
	                    		//无摄像，则不需要保存other表
	                			return;
	                    	}
	                	}
	                }
    			
            	//System.out.println("addPicInfoTradeUserOther--picId:"+picId+",angentpicId:"+angentpicId);
            	
        		UcaData uca=this.reqData.getUca();
        		
        		param.put("PIC_ID", picId);
        		param.put("AGENT_PIC_ID", angentpicId);
        		param.put("CUST_ID", uca.getCustId());
        		param.put("USER_ID", uca.getUserId());
        		param.put("PSPT_TYPE_CODE", uca.getCustomer().getPsptTypeCode());
        		param.put("PSPT_ID", uca.getCustomer().getPsptId());
        		param.put("SERIAL_NUMBER", uca.getSerialNumber());
        		
        		
        		//System.out.println("addPicInfoTradeUserOther--param:"+param+",uca:"+uca);
        		
        		
        		OtherTradeData otherTradeData = new OtherTradeData();
        		//新客户照片信息存储
        		otherTradeData.setInstId(SeqMgr.getInstId());
        		otherTradeData.setUserId(param.getString("USER_ID"));
        		otherTradeData.setRsrvValueCode("BAT_GROUP_PIC_ID");//照片ID
        		otherTradeData.setRsrvValue("客户人像采集照片ID存储");
        		otherTradeData.setRsrvStr1(picId);//照片ID值_客户摄像标识
        		otherTradeData.setRsrvStr3(angentpicId);//经办人摄像标识
        		otherTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
        		otherTradeData.setStartDate(SysDateMgr.getSysTime());
        		otherTradeData.setEndDate(SysDateMgr.getEndCycle20501231());
        		otherTradeData.setDepartId(CSBizBean.getVisit().getDepartId());
                otherTradeData.setStaffId(CSBizBean.getVisit().getStaffId());
                otherTradeData.setRsrvStr4("BATOPENGROUPMEM");//IMS固话批量开户
        		//摄像信息保存到other表
        		this.addTradeOther(otherTradeData.toData());
    		}
    	}
    }
    
    /**
     * REQ201801150022_新增IMS号码开户人像比对功能
     * <br/>
     * 单个集团成员开户
     * @throws Exception
     */
    public void addIMSPicInfoTradeUserOther() throws Exception{
		//集团成员开户(单个)
		IData  pageIdata=this.reqData.getPageRequestData();
		String prouctId=pageIdata.getString("PRODUCT_ID","");
		IData memCustInfo = pageIdata.getData("MEM_CUST_INFO");
		//System.out.println(",==pageIdata=====:"+pageIdata+",prouctId:"+prouctId+",memCustInfo:"+memCustInfo);
		if(!"801110".equals(prouctId)||IDataUtil.isEmpty(memCustInfo)){
			//非IMS语音
			return;
		}
		
		//从界面获取图片id
	    String	picId =memCustInfo.getString("PIC_ID", "");
		String  angentpicId = memCustInfo.getString("AGENT_PIC_ID","");
		//System.out.println(",==memCustInfo=====:"+memCustInfo+",angentpicId:"+angentpicId);
		if(StringUtils.isBlank(picId) && StringUtils.isBlank(angentpicId)){ //不存在照片ID信息则直接返回，不进行保存
			return;
		}
		
		UcaData uca=this.reqData.getUca();
		OtherTradeData otherTradeData = new OtherTradeData();
		//新客户照片信息存储
		otherTradeData.setInstId(SeqMgr.getInstId());
		otherTradeData.setUserId(uca.getUserId());
		otherTradeData.setRsrvValueCode("PIC_ID");//照片ID,把GROUP_PIC_ID修改为PIC_ID
		otherTradeData.setRsrvValue("客户人像采集照片ID存储");
		otherTradeData.setRsrvStr1(picId);//照片ID值_客户摄像标识
		otherTradeData.setRsrvStr3(angentpicId);//经办人摄像标识
		otherTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
		otherTradeData.setStartDate(SysDateMgr.getSysTime());
		otherTradeData.setEndDate(SysDateMgr.getEndCycle20501231());
		otherTradeData.setDepartId(CSBizBean.getVisit().getDepartId());
        otherTradeData.setStaffId(CSBizBean.getVisit().getStaffId());
        otherTradeData.setRsrvStr4("单个集团成员开户");
		//摄像信息保存到other表
		this.addTradeOther(otherTradeData.toData());
    }
    
    
	/**
	 * 获取BatdealBatchId
	 * <br/>
	 * 由于TF_B_TRADE_BATDEAL表的OPERATE_ID对应台账表的BATCH_ID,
	 * 所以要转换一下
	 * @param operateId
	 * @return
	 * @throws Exception
	 */
	public IData  getBatdealBatchId(String operateId) throws Exception{
		IData batParam=new DataMap();
		batParam.put("OPERATE_ID", operateId);
		IDataset batdealInfo = Dao.qryByCode("TF_B_TRADE_BATDEAL",
				"SEL_BY_OPERATEID", batParam,
				Route.getJourDb(Route.CONN_CRM_CG));
		if(IDataUtil.isEmpty(batdealInfo)){
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "通过台帐BATCH_ID获取TF_B_TRADE_BATDEAL不存在");
		}
		//获取批量任务中的batchId
		//String batchId=batdealInfo.getData(0).getString("BATCH_ID", "");	
		return batdealInfo.getData(0);
	}
    protected void setTradeBase() throws Exception{
        // 1- 调用基类方法注入值
        super.setTradeBase();
        IData data = bizData.getTrade();
        data.put("PRODUCT_ID",reqData.getUca().getProductId());

    }
}
