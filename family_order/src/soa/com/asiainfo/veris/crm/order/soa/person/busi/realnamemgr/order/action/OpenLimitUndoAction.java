package com.asiainfo.veris.crm.order.soa.person.busi.realnamemgr.order.action;

import java.util.Calendar;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.util.DataSetUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeCustPersonInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeCustomerInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.realnamemgr.NationalOpenLimitBean;


/**
 *   
 * @author ouyang 一证五号完工action 返销
 * 
 */
public class OpenLimitUndoAction implements ITradeFinishAction
{

    @Override
    public void executeAction(IData mainTrade) throws Exception
    {

        String cityCode = mainTrade.getString("CITY_CODE", "").trim();
        System.out.println("OpenLimitUndoAction.javaxxxxxxxxxxxxxxxxxx 31 "+mainTrade);

         if (cityCode.equals("HNHN") || cityCode.equals("HNSJ")) {// HNHN HNSJ的号码，不做任何事情

        } else { 

            NationalOpenLimitBean bean = (NationalOpenLimitBean) BeanManager.createBean(NationalOpenLimitBean.class);
            boolean isIgnoreCall = bean.isIgnoreCall();

            //客户信息
            IDataset custPerTradeInfo = TradeCustPersonInfoQry.getTradeCustPersonByTradeId(mainTrade.getString("TRADE_ID"));

            String custName = custPerTradeInfo.getData(0).getString("CUST_NAME");
            String psptTypeCode = custPerTradeInfo.getData(0).getString("PSPT_TYPE_CODE", "").trim();
            String psptId = custPerTradeInfo.getData(0).getString("PSPT_ID");
            if (psptTypeCode.equals("D") || psptTypeCode.equals("E") || psptTypeCode.equals("G") || psptTypeCode.equals("L") || psptTypeCode.equals("M")) {
                //如开户证件是单位证件类型，则需同步使用人信息
                //RSRV_STR5 名称              RSRV_STR6 证件类型                     RSRV_STR7 证件号码
                custName = custPerTradeInfo.getData(0).getString("RSRV_STR5","").trim();
                psptTypeCode = custPerTradeInfo.getData(0).getString("RSRV_STR6", "").trim();
                psptId = custPerTradeInfo.getData(0).getString("RSRV_STR7","").trim();
            }
            System.out.println("OpenLimitUndoAction.javaxxxxxxxxxxxxxxxxxx 51 "+custName+"  :  "+psptTypeCode+"  :  "+psptId);
            if (custName!=null&&custName.length() > 0 &&psptTypeCode!=null&& psptTypeCode.length() > 0 &&psptId!=null&& psptId.length() > 0) {
                IData compara = new DataMap();
                compara.put("SUBSYS_CODE", "CSM");
                compara.put("PARAM_ATTR", "2552");
                compara.put("PARAM_CODE", psptTypeCode);
                compara.put("EPARCHY_CODE", "ZZZZ");
                IDataset openLimitResult = CommparaInfoQry.getCommparaAllCol(compara.getString("SUBSYS_CODE", ""), compara.getString("PARAM_ATTR", ""), compara.getString("PARAM_CODE", ""), compara.getString("EPARCHY_CODE", ""));
                System.out.println("OpenLimitUndoAction.javaxxxxxxxxxxxxxxxxxx 59 "+openLimitResult);
                //证件类型转换
                IDataset checkResults = bean.checkPspt(psptTypeCode);
                if (IDataUtil.isNotEmpty(checkResults)) {
//                    IData synParam = new DataMap();
                    IData custData = new DataMap();
                    IDataset custDataset = new DatasetList();
                    //获取交易流水号
                    custData = bean.addSeq(custData);
                    custData.put("IDV", mainTrade.getString("SERIAL_NUMBER"));
                    custData.put("HOME_PROV", mainTrade.getString("EPARCHY_CODE"));
                    custData.put("CUSTOMER_NAME", custName);
                    custData.put("ID_CARD_TYPE", checkResults.getData(0).getString("PARA_CODE1"));
                   // custData.put("ID_CARD_TYPE", "A");//测试用
                    custData.put("ID_CARD_NUM", psptId);
                    custData.put("OPR", "02");
                    //custData.put("EFFETI_TIME", mainTrade.getString("ACCEPT_DATE"));
                    //开户时，需同步一证五号平台数据，插接口表的语句的EFFETI_TIME字段要求为char类型字段yyyyMMddHHmmss取办理这笔业务的时间--wangsc-20190424
                    custData.put("EFFETI_TIME", SysDateMgr.decodeTimestamp(mainTrade.getString("ACCEPT_DATE"), SysDateMgr.PATTERN_STAND_SHORT));
                    custData.put("OPR_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
                    custData.put("CHECKING_DATE", SysDateMgr.getSysDateYYYYMMDD());
                    
                    custData.put("ID_VALUE", mainTrade.getString("SERIAL_NUMBER"));
                    custData.put("EPARCHY_CODE", mainTrade.getString("EPARCHY_CODE"));
                    custData.put("UPDATE_TIME", SysDateMgr.getSysDate());
                    custData.put("UPDATE_STAFF_ID", mainTrade.getString("UPDATE_STAFF_ID"));
                    custData.put("UPDATE_DEPART_ID", mainTrade.getString("UPDATE_DEPART_ID"));
                    custData.put("REMARK", mainTrade.getString("REMARK"));
                    
                    if (!isIgnoreCall && DataSetUtils.isNotBlank(openLimitResult)) {
                        custDataset.add(custData);
//                        synParam.put("USER_DATASET", custDataset);
                        
                        IDataUtil.chkParam(custData, "CHECKING_DATE");
                        IDataUtil.chkParam(custData, "OPR_TIME");
                        IDataUtil.chkParam(custData, "OPR");
                        IDataUtil.chkParam(custData, "EFFETI_TIME");
                        IDataUtil.chkParam(custData, "HOME_PROV");
                        if(custData.getString("HOME_PROV","").trim().length()>3){
                            String homeProv  = custData.getString("HOME_PROV","").trim();
                            custData.put("HOME_PROV", homeProv.substring(homeProv.length()-3));
                        }
                        //预占字段  1是解除预占
                        custData.put("STATE", "1");
                        
                        custData.put("TRADE_TYPE_CODE", mainTrade.getString("TRADE_TYPE_CODE", ""));        
                        
                        //中国移动网状网系统接口规范-全网用户数据查询平台分册V3.0.0（SOAP）-2020-2-6
                        dealParam(mainTrade,custData,bean,checkResults.getData(0).getString("PARA_CODE1"),psptId);
                        
                        System.out.println("OpenLimitUndoAction.javaxxxxxxxxxxxxxxxxxx 103 "+custData);
                        
                        bean.regCampOnIBOSS(custData);                        
                        
/*                        //调用同步接口
                        try {
                            bean.userInfoSyn(synParam);
                        } catch (Exception e) {
                            //重发处理
                        }*/
                    }
                    
/*                    //登记一证多号对账表
                    custData.put("ID_VALUE", mainTrade.getString("SERIAL_NUMBER"));
                    custData.put("EPARCHY_CODE", mainTrade.getString("EPARCHY_CODE"));
                    custData.put("UPDATE_TIME", SysDateMgr.getSysDate());
                    custData.put("UPDATE_STAFF_ID", mainTrade.getString("UPDATE_STAFF_ID"));
                    custData.put("UPDATE_DEPART_ID", mainTrade.getString("UPDATE_DEPART_ID"));
                    custData.put("REMARK", mainTrade.getString("REMARK"));
                    bean.regOpenLimitCheck(custData);*/
                }
            }
        }
    }
    
    /**
     * 中国移动网状网系统接口规范-全网用户数据查询平台分册V3.0.0（SOAP）-2020-2-6
     * @param mainTrade
     * @param custData
     * @param bean
     * @param psptTypeCodeChange
     * @param psptId
     * @throws Exception
     */
    private void dealParam(IData mainTrade,IData custData,NationalOpenLimitBean bean,String psptTypeCodeChange,String psptId) throws Exception{
    	//------------中国移动网状网系统接口规范-全网用户数据查询平台分册V3.0.0（SOAP）-2020-2-6   接口新增参数开始----------
        //新增客户年龄、操作员工工号、渠道编码、是否代理开户、代理人证件类型、代理人证件号码、操作场景标识、是否集团客户等异常入网风险监测分析所需的关键字段。
        String rsrvStr5 = "";//操作员工工号、渠道编码、操作场景标识、是否集团客户、客户证件类型、客户年龄
        String rsrvStr3 = "";//是否代理开户、代理人证件类型、代理人证件号码、
        String updateStaffId = mainTrade.getString("UPDATE_STAFF_ID");//操作员工工号
        String channelId = "";//(该字段选填，先不填看看)各省的渠道标识，遵照全网渠道统一编码规则，详见附录7.7(该字段选填，先不填看看)
        String oprScene = "S99";//操作场景标识：默认S99其它，有匹配记录就置换匹配参数
        //操作场景编码转换
        IDataset checkScenes = CommparaInfoQry.getCommparaAllCol("CSM", "3553",mainTrade.getString("TRADE_TYPE_CODE", ""),"ZZZZ");
        if (IDataUtil.isNotEmpty(checkScenes)) {
        	oprScene = checkScenes.getData(0).getString("PARA_CODE1");
        }
        String isGroup = "";//(该字段选填，先不填看看)都是读取的TF_B_TRADE_CUST_PERSON/TF_F_CUST_PERSON,所以应该都是个人客户？     ----这个字段只能填写0或者1如果是集团客户，则填写1；如果不是集团客户填写0；
        rsrvStr5 = updateStaffId + "|" + channelId + "|" + oprScene + "|" + isGroup + "|" + psptTypeCodeChange;
        
        String age = "";
      	//psptId;//根据证件号码计算年龄-----开户客户的年龄，对于证件类型为“身份证”的客户，“客户年龄”字段为必填，其他证件类型可选填。
        if ("00".equals(psptTypeCodeChange)) {
        	age = psptToAge(psptId);
        	rsrvStr5 = rsrvStr5 + "|" + age;
		}
        //-------------
        //是否代理开户-----这个字段只能填写0或者1如果不是代理开户，则填写0；如果是代理开户填写1;
        //代理人证件类型----参见7.2客户证件类型编码
        //代理人证件号码----如果是代理开户必须填代理人的证件号码MD5值；(营业传的应该只是正常值入表)
        String IsAgent = "0";
        rsrvStr3 = IsAgent;
        //-------------------------
        IDataset idsUserPspt1 = TradeCustomerInfoQry.getTradeCustomerByTradeId(mainTrade.getString("TRADE_ID"));
        if (IDataUtil.isNotEmpty(idsUserPspt1)) {
        	String agentIDCardType= idsUserPspt1.getData(0).getString("RSRV_STR8");
        	String agentIDCardNumber= idsUserPspt1.getData(0).getString("RSRV_STR9");
        	if (StringUtils.isNotBlank(agentIDCardType) && StringUtils.isNotBlank(agentIDCardNumber)) {
        		IsAgent = "1";
            	rsrvStr3 = IsAgent;
            	//证件类型转换
                IDataset checkPsptType = bean.checkPspt(agentIDCardType);
                if (IDataUtil.isNotEmpty(checkPsptType)) {
                    agentIDCardType = checkPsptType.getData(0).getString("PARA_CODE1");
                }
                agentIDCardNumber = agentIDCardNumber.toUpperCase();
            	rsrvStr3 = rsrvStr3+"|"+agentIDCardType+"|"+agentIDCardNumber;
			}
        }
        custData.put("RSRV_STR5", rsrvStr5);
        custData.put("RSRV_STR3", rsrvStr3);
        //------------中国移动网状网系统接口规范-全网用户数据查询平台分册V3.0.0（SOAP）-2020-2-6   接口新增参数结束----------
    }

    private String psptToAge(String psptId){
    	String age = "0";
    	try {
    		if(psptId != null && !"".equals(psptId) ){
            	if (psptId.length() == 18){
            		Calendar cal = Calendar.getInstance();
                    int yearNow = cal.get(Calendar.YEAR);
                    int monthNow = cal.get(Calendar.MONTH)+1;
                    int dayNow = cal.get(Calendar.DATE);

                    int year = Integer.valueOf(psptId.substring(6, 10));
                    int month = Integer.valueOf(psptId.substring(10,12));
                    int day = Integer.valueOf(psptId.substring(12,14));

                    if ((month < monthNow) || (month == monthNow && day<= dayNow) ){
                        age = String.valueOf(yearNow - year);
                    }else {
                        age = String.valueOf(yearNow - year-1);
                    }
                    //开发到这，证件计算年龄还需要继续搞，注意15位和18位身份证计算年龄
                }else if (psptId.length() == 15){
                	Calendar cal = Calendar.getInstance();
                    int yearNow = cal.get(Calendar.YEAR);
                    int monthNow = cal.get(Calendar.MONTH)+1;
                    int dayNow = cal.get(Calendar.DATE);

                    int year = Integer.valueOf("19"+psptId.substring(6, 8));
                    int month = Integer.valueOf(psptId.substring(8,10));
                    int day = Integer.valueOf(psptId.substring(10,12));

                    if ((month < monthNow) || (month == monthNow && day<= dayNow) ){
                        age = String.valueOf(yearNow - year);
                    }else {
                        age = String.valueOf(yearNow - year-1);
                    }
                }else {
                	age = "0";
    			}
            }
    	} catch (Exception e) {
    		// TODO: handle exception
    		age = "0";
    	}
    	return age;
    }
}
