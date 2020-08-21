package com.asiainfo.veris.crm.order.soa.person.busi.realnamemgr.order.action;

import java.util.Calendar;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.util.DataSetUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeCustomerInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeRelaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.realnamemgr.NationalOpenLimitBean;

/**
 * 
 * @author ouyang 一证五号完工action 删除号码
 * 
 */
public class OpenLimitDeleteAction implements ITradeFinishAction
{
    public static Logger logger=Logger.getLogger(OpenLimitDeleteAction.class);

    @Override
    public void executeAction(IData mainTrade) throws Exception
    {
   	    logger.debug("------OpenLimitDeleteAction-----------------");
   	    String serialNumber = mainTrade.getString("SERIAL_NUMBER");
	    String tradeTypeCode = mainTrade.getString("TRADE_TYPE_CODE");
   	    String seq ="";
        String cityCode = mainTrade.getString("CITY_CODE", "").trim();
        if (cityCode.equals("HNHN") || cityCode.equals("HNSJ")) {// HNHN HNSJ的号码，不做任何事情

        } else {

            NationalOpenLimitBean bean = (NationalOpenLimitBean) BeanManager.createBean(NationalOpenLimitBean.class);
            boolean isIgnoreCall = bean.isIgnoreCall();
            String brandCode = mainTrade.getString("BRAND_CODE");
            
            IDataset userInfos = UserInfoQry.queryUserOpendateByUserId(mainTrade.getString("USER_ID"));
            String  OPEN_DATE = mainTrade.getString("ACCEPT_DATE");
            if(!userInfos.isEmpty() && null != userInfos){
            	OPEN_DATE = userInfos.getData(0).getString("OPEN_DATE","");
            }
            
            if("MDRP".equals(brandCode)){
            	//关于做好一号双终端业务相关问题优化改造的通知 第二个改造点：eSIM一号双终端(副号为非实体卡)不进行一证五号校验
            	IDataset tradeDatas = TradeInfoQry.queryTradeInfoByOidAndCodeAndTag(mainTrade.getString("ORDER_ID"), "396", "0");//根据ORDER_ID查TF_B_TRADE表(10和396两笔数据)
    			String tradeId = "";
    			if(IDataUtil.isNotEmpty(tradeDatas)){
    				tradeId = tradeDatas.getData(0).getString("TRADE_ID");//获取396工单TRADE_ID
    			}else{//TF_B_TRADE表里没数据再查TF_BH_TRADE表
    				IDataset tradeHDatas = TradeInfoQry.getHisTradeInfoByOrderId(mainTrade.getString("ORDER_ID"), "396", "0");
    				if(IDataUtil.isNotEmpty(tradeHDatas)){
    				   tradeId = tradeHDatas.getData(0).getString("TRADE_ID");//获取396工单TRADE_ID
    				}
    			}
				IDataset relationTradeDatas = TradeRelaInfoQry.getRelaUUByTradeIdRelationTypeCode(tradeId,"OM");//OM为一号多终端
				if(relationTradeDatas.isEmpty()){
					return;
				}
				for(int i=0; i<relationTradeDatas.size(); i++ ){
					String modifyTag = relationTradeDatas.getData(i).getString("MODIFY_TAG");
					if(BofConst.MODIFY_TAG_DEL.equals(modifyTag)){	
						String auxType = relationTradeDatas.getData(i).getString("RSRV_TAG1");// 副号类型 1：eSIM卡  2：实体卡
						if("1".equals(auxType)){//类型为eSIM卡
							continue;//eSIM一号双终端(副号为非实体卡)不进行一证五号校验
						}		
						serialNumber = relationTradeDatas.getData(i).getString("SERIAL_NUMBER_B");
						this.dealDelete(mainTrade, serialNumber, bean, isIgnoreCall,null,OPEN_DATE);
					}					
				}
    			return;//一号多终端的副号码不上传一证五号平台
    		}
            if("60".equals(tradeTypeCode) || "62".equals(tradeTypeCode)){
				if("MOSP".equals(brandCode))
				{
					return;//和多号副号做资料变更的时候不做一证五号处理
				}
			}
            //start--wangsc10--20181204
          
            //add by zhangxing3 for REQ201906040011关于非实名关停号码自动删除一证五号平台数据的需求
            if("7220".equals(tradeTypeCode) && !"StopNotRealName".equals(mainTrade.getString("RSRV_STR9",""))){

				return;//非实名制停机触发的7220业务进行一证五号处理，其他情况不处理。
			}
            //add by zhangxing3 for REQ201906040011关于非实名关停号码自动删除一证五号平台数据的需求
            
            if("3798".equals(tradeTypeCode)){//和多号 一次可能取消多个
				IDataset relationTradeDatas = TradeRelaInfoQry.getTradeRelaByTradeIdRelaType(mainTrade.getString("TRADE_ID"),"M2");
				if(relationTradeDatas.isEmpty()){
					return;
				}
				for(int i=0; i<relationTradeDatas.size(); i++ ){
					String modifyTag = relationTradeDatas.getData(i).getString("MODIFY_TAG");
					if(BofConst.MODIFY_TAG_DEL.equals(modifyTag)){	
						String category=relationTradeDatas.getData(i).getString("USER_ID_B").substring(relationTradeDatas.getData(i).getString("USER_ID_B").length()-1);
						if("1".equals(category)){
							continue;//实体副号码不处理
						}
						serialNumber = relationTradeDatas.getData(i).getString("SERIAL_NUMBER_B");
						seq =relationTradeDatas.getData(i).getString("RSRV_STR4");
						this.dealDelete(mainTrade, serialNumber, bean, isIgnoreCall,seq,OPEN_DATE);
					}					
				}
				return;
			}
            //要包含预销号的  remove_tag 0,1,3
            IDataset userInfo = Dao.qryByCodeParser("TF_F_USER", "SEL_USER_FOR_OPENLIMIT", mainTrade);
            
            if (IDataUtil.isNotEmpty(userInfo)) {
                IData custInfo = UcaInfoQry.qryCustInfoByCustId(userInfo.getData(0).getString("CUST_ID"));
                if (IDataUtil.isNotEmpty(custInfo)) {
                    String custName = custInfo.getString("CUST_NAME", "").trim();
                    String psptTypeCode = custInfo.getString("PSPT_TYPE_CODE", "").trim();
                    String psptId = custInfo.getString("PSPT_ID", "").trim();
                    if (psptTypeCode.equals("D") || psptTypeCode.equals("E") || psptTypeCode.equals("G") || psptTypeCode.equals("L") || psptTypeCode.equals("M")) {
                        //如开户证件是单位证件类型，则需同步使用人信息
                        //RSRV_STR5 名称              RSRV_STR6 证件类型                     RSRV_STR7 证件号码
                        custName = custInfo.getString("RSRV_STR5", "").trim();
                        psptTypeCode = custInfo.getString("RSRV_STR6", "").trim();
                        psptId = custInfo.getString("RSRV_STR7", "").trim();
                    }
                    if (custName!=null&&custName.length() > 0 &&psptTypeCode!=null&& psptTypeCode.length() > 0 &&psptId!=null&& psptId.length() > 0) {
                        IData compara = new DataMap();
                        compara.put("SUBSYS_CODE", "CSM");
                        compara.put("PARAM_ATTR", "2552");
                        compara.put("PARAM_CODE", psptTypeCode);
                        compara.put("EPARCHY_CODE", "ZZZZ");
                        IDataset openLimitResult = CommparaInfoQry.getCommparaAllCol(compara.getString("SUBSYS_CODE", ""), compara.getString("PARAM_ATTR", ""), compara.getString("PARAM_CODE", ""), compara.getString("EPARCHY_CODE", ""));

                        /**
                         * REQ201707130011_全国一证五号考核相关优化（二）
                         * 把bean.checkPspt(custInfo.getString("PSPT_TYPE_CODE"));
                         * 修改为
                         * bean.checkPspt(psptTypeCode);
                         * @author zhuoyingzhi
                         * @date 20170718
                         */
                        //证件类型转换
                        IDataset checkResults = bean.checkPspt(psptTypeCode);
                        
                        
                        if (IDataUtil.isNotEmpty(checkResults)) {
                            //同步数据
                            IData synParam = new DataMap();
                            IData custData = new DataMap();
                            IDataset custDataset = new DatasetList();
                            //获取交易流水号
                            custData = bean.addSeq(custData);
                            custData.put("IDV", mainTrade.getString("SERIAL_NUMBER"));
                            custData.put("HOME_PROV", mainTrade.getString("EPARCHY_CODE"));
                            custData.put("CUSTOMER_NAME", custName);
                            custData.put("ID_CARD_TYPE", checkResults.getData(0).getString("PARA_CODE1"));
                            custData.put("ID_CARD_NUM", psptId);
                            custData.put("OPR", "02");
                          //custData.put("EFFETI_TIME", mainTrade.getString("ACCEPT_DATE"));
                            custData.put("EFFETI_TIME", OPEN_DATE);//入网时间，格式YYYYMMDDHH24MISS，平台将以该字段记录用户手机号码入网时间。填写14位真实入网时间,开户，销户，过户，资料变更都需要填写，平台以省端为准更新入网时间，同时记录入库时间。
                            custData.put("OPR_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
                            custData.put("CHECKING_DATE", SysDateMgr.getSysDateYYYYMMDD());

                            custData.put("ID_VALUE", mainTrade.getString("SERIAL_NUMBER"));
                            custData.put("EPARCHY_CODE", mainTrade.getString("EPARCHY_CODE"));
                            custData.put("UPDATE_TIME", SysDateMgr.getSysDate());
                            custData.put("UPDATE_STAFF_ID", mainTrade.getString("UPDATE_STAFF_ID"));
                            custData.put("UPDATE_DEPART_ID", mainTrade.getString("UPDATE_DEPART_ID"));
                            custData.put("REMARK", mainTrade.getString("REMARK"));
                            
                            
                            //判断用户品牌和是否为行业应用卡批量开户
                            boolean  isFlag=bean.isCheckBrandAndHYYYKBATCHOPEN(mainTrade);
                            
                            logger.debug("OpenLimitDeleteAction---isIgnoreCall:"+isIgnoreCall+",openLimitResult:"+openLimitResult+",isFlag:"+isFlag);

                            if (!isIgnoreCall && DataSetUtils.isNotBlank(openLimitResult) && isFlag) {
                            	/**
                            	 * REQ201705210001_全国一证五号考核相关优化
                            	 * @author zhuoyingzhi
                            	 * @date 20170602
                            	 * 修改为直接插表，不调用实时接口。
                            	 */
                                //调用同步接口
//                                custDataset.add(custData);
//                                synParam.put("USER_DATASET", custDataset);
//                                try {
//                                    bean.userInfoSyn(synParam);
//                                } catch (Exception e) {
//                                    //重发处理
//                                }
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
                                
                                //写入IBOSS扫描操作的预占表
                                /**
                                 *  20170701确认需求方案：
                                 *  如果是客户资料变更或过户，当客户中、证件类型、证件号码,  20181218wangsc10客户姓名(调修改接口)
                                 *  其中有一个值变更,才入表   
                                 * @author zhuoyingzhi
                                 * @date 20170701
                                 */
                                if("60".equals(tradeTypeCode)){
                                	//客户资料变更或过户
                                	if(bean.isCheckOpenLimitDeleteCustInfo(mainTrade, psptTypeCode, psptId)){
                                		//证件类型、证件号码   	修改过   入表
                                		bean.regCampOnIBOSS(custData);
                                	}
                                }else{
                                	 bean.regCampOnIBOSS(custData);
                                }
                            	
                            }
                        	/**
                        	 * REQ201705210001_全国一证五号考核相关优化
                        	 * @author zhuoyingzhi
                        	 * @date 20170602
                        	 * 废掉TF_F_OPENLIMIT_CHECK表,把信息全部记录在
                        	 * TF_F_OPENLIMIT_CAMPON_IBOSS表
                        	 */
                            //登记一证多号对账表
//                            custData.put("ID_VALUE", mainTrade.getString("SERIAL_NUMBER"));
//                            custData.put("EPARCHY_CODE", mainTrade.getString("EPARCHY_CODE"));
//                            custData.put("UPDATE_TIME", SysDateMgr.getSysDate());
//                            custData.put("UPDATE_STAFF_ID", mainTrade.getString("UPDATE_STAFF_ID"));
//                            custData.put("UPDATE_DEPART_ID", mainTrade.getString("UPDATE_DEPART_ID"));
//                            custData.put("REMARK", mainTrade.getString("REMARK"));
//                            bean.regOpenLimitCheck(custData);
                        }

                    }

                }
            }
        }
    }
    
private void dealDelete(IData mainTrade,String serialNumber,NationalOpenLimitBean bean,boolean isIgnoreCall,String seq, String OPEN_DATE) throws Exception {
    	
    	IDataset userInfo = Dao.qryByCodeParser("TF_F_USER", "SEL_USER_FOR_OPENLIMIT", mainTrade);

        if (IDataUtil.isNotEmpty(userInfo)) {
            IData custInfo = UcaInfoQry.qryCustInfoByCustId(userInfo.getData(0).getString("CUST_ID"));
            if (IDataUtil.isNotEmpty(custInfo)) {
                String custName = custInfo.getString("CUST_NAME", "").trim();
                String psptTypeCode = custInfo.getString("PSPT_TYPE_CODE", "").trim();
                String psptId = custInfo.getString("PSPT_ID", "").trim();
                if (psptTypeCode.equals("D") || psptTypeCode.equals("E") || psptTypeCode.equals("G") || psptTypeCode.equals("L") || psptTypeCode.equals("M")) {
                    //如开户证件是单位证件类型，则需同步使用人信息
                    //RSRV_STR5 名称              RSRV_STR6 证件类型                     RSRV_STR7 证件号码
                    custName = custInfo.getString("RSRV_STR5", "").trim();
                    psptTypeCode = custInfo.getString("RSRV_STR6", "").trim();
                    psptId = custInfo.getString("RSRV_STR7", "").trim();
                }
                if (custName!=null&&custName.length() > 0 &&psptTypeCode!=null&& psptTypeCode.length() > 0 &&psptId!=null&& psptId.length() > 0) {
                    IData compara = new DataMap();
                    compara.put("SUBSYS_CODE", "CSM");
                    compara.put("PARAM_ATTR", "2552");
                    compara.put("PARAM_CODE", psptTypeCode);
                    compara.put("EPARCHY_CODE", "ZZZZ");
                    IDataset openLimitResult = CommparaInfoQry.getCommparaAllCol(compara.getString("SUBSYS_CODE", ""), compara.getString("PARAM_ATTR", ""), compara.getString("PARAM_CODE", ""), compara.getString("EPARCHY_CODE", ""));

                    //证件类型转换
                    IDataset checkResults = bean.checkPspt(custInfo.getString("PSPT_TYPE_CODE"));
                    if (IDataUtil.isNotEmpty(checkResults)) {
                        //同步数据
                        IData synParam = new DataMap();
                        IData custData = new DataMap();
                        IDataset custDataset = new DatasetList();
                        if(StringUtils.isNotBlank(seq))
                        {
                        	custData.put("SEQ", seq);
                        }
                        //获取交易流水号
                        custData = bean.addSeq(custData);
                        custData.put("IDV", serialNumber);
                        custData.put("HOME_PROV", mainTrade.getString("EPARCHY_CODE"));
                        custData.put("CUSTOMER_NAME", custName);
                        custData.put("ID_CARD_TYPE", checkResults.getData(0).getString("PARA_CODE1"));
                        custData.put("ID_CARD_NUM", psptId);
                        custData.put("OPR", "02");
                      //custData.put("EFFETI_TIME", mainTrade.getString("ACCEPT_DATE"));
                        custData.put("EFFETI_TIME", OPEN_DATE);//入网时间，格式YYYYMMDDHH24MISS，平台将以该字段记录用户手机号码入网时间。填写14位真实入网时间,开户，销户，过户，资料变更都需要填写，平台以省端为准更新入网时间，同时记录入库时间。
                        custData.put("OPR_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
                        custData.put("CHECKING_DATE", SysDateMgr.getSysDateYYYYMMDD());

                        custData.put("ID_VALUE", serialNumber);
                        custData.put("EPARCHY_CODE", mainTrade.getString("EPARCHY_CODE"));
                        custData.put("UPDATE_TIME", SysDateMgr.getSysDate());
                        custData.put("UPDATE_STAFF_ID", mainTrade.getString("UPDATE_STAFF_ID"));
                        custData.put("UPDATE_DEPART_ID", mainTrade.getString("UPDATE_DEPART_ID"));
                        custData.put("REMARK", mainTrade.getString("REMARK"));
                        
                        
                        //判断用户品牌和是否为行业应用卡批量开户
                        boolean  isFlag=bean.isCheckBrandAndHYYYKBATCHOPEN(mainTrade);

                        if (!isIgnoreCall && DataSetUtils.isNotBlank(openLimitResult) && isFlag) {

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
                            
                            bean.regCampOnIBOSS(custData);
                        }
                    }

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