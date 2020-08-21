package com.asiainfo.veris.crm.order.soa.person.busi.realnamemgr.order.action;

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
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustPersonInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeCustPersonInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeRelaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.realnamemgr.NationalOpenLimitBean;

/**
 * 
 * @author ouyang 一证五号完工action 增加号码
 * 
 */
public class OpenLimitModifyAction implements ITradeFinishAction
{

    public static Logger logger=Logger.getLogger(OpenLimitModifyAction.class);

    @Override
    /*
     * 复机专用
     */
    public void executeAction(IData mainTrade) throws Exception
    {
    	 logger.debug("------OpenLimitAddAction-----------------");
        //mainTrade.getString("CITY_CODE");
        String cityCode = mainTrade.getString("CITY_CODE", "").trim();
        String tradeTypeCode= mainTrade.getString("TRADE_TYPE_CODE", "");
        String serialNumber = mainTrade.getString("SERIAL_NUMBER");
        String seq ="";
        
        if (cityCode.equals("HNHN") || cityCode.equals("HNSJ")) {// HNHN HNSJ的号码，不做任何事情

        } else {

            NationalOpenLimitBean bean = (NationalOpenLimitBean) BeanManager.createBean(NationalOpenLimitBean.class);
            boolean isIgnoreCall = bean.isIgnoreCall();

            //客户信息
            IDataset custPerTradeInfo = TradeCustPersonInfoQry.getTradeCustPersonByTradeId(mainTrade.getString("TRADE_ID"));
            String brandCode = mainTrade.getString("BRAND_CODE");
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
				boolean isAdd = false;
				for(int i=0; i<relationTradeDatas.size(); i++ ){
					String modifyTag = relationTradeDatas.getData(i).getString("MODIFY_TAG");
					if(BofConst.MODIFY_TAG_ADD.equals(modifyTag)){	
						String auxType = relationTradeDatas.getData(i).getString("RSRV_TAG1");// 副号类型 1：eSIM卡  2：实体卡
						if("1".equals(auxType)){//类型为eSIM卡
							continue;//eSIM一号双终端(副号为非实体卡)不进行一证五号校验
						}		
						isAdd = true;
						break;
					}					
				}
				if(!isAdd){
					return;
				}
    			//return;//一号多终端的副号码不上传一证五号平台
    		}
            if("60".equals(tradeTypeCode) || "62".equals(tradeTypeCode)){
				if("MOSP".equals(brandCode))
				{
					return;//和多号副号做资料变更的时候不做一证五号处理
				}
			}
            if(StringUtils.equals("3798", tradeTypeCode))//和多号,一次只能绑定一个
            {
            	IDataset relationTradeDatas = TradeRelaInfoQry.getTradeRelaByTradeIdRelaType(mainTrade.getString("TRADE_ID"),"M2");
				if(relationTradeDatas.isEmpty()){
					return;
				}
				boolean isAdd = false;
				for(int i=0; i<relationTradeDatas.size(); i++ ){
					String modifyTag = relationTradeDatas.getData(i).getString("MODIFY_TAG");
					if(BofConst.MODIFY_TAG_ADD.equals(modifyTag)){	
						String category=relationTradeDatas.getData(i).getString("USER_ID_B").substring(relationTradeDatas.getData(i).getString("USER_ID_B").length()-1);
						if("1".equals(category)){
							continue;//实体副号码不处理
						}
						serialNumber = relationTradeDatas.getData(i).getString("SERIAL_NUMBER_B");
						seq = relationTradeDatas.getData(i).getString("RSRV_STR4");
						isAdd = true;
						break;
					}					
				}
				if(!isAdd){
					return;
				}
				if(IDataUtil.isEmpty(custPerTradeInfo))
				{
					custPerTradeInfo = CustPersonInfoQry.getPerInfoByCustId(mainTrade.getString("CUST_ID"));
				}
            }
            
            //start--wangsc10--20181204
            IDataset userInfos = UserInfoQry.queryUserOpendateByUserId(mainTrade.getString("USER_ID"));
            String  OPEN_DATE = mainTrade.getString("ACCEPT_DATE");
            if(!userInfos.isEmpty() && null != userInfos){
            	OPEN_DATE = userInfos.getData(0).getString("OPEN_DATE","");
            }
            
            String custName = custPerTradeInfo.getData(0).getString("CUST_NAME","");
            String psptTypeCode = custPerTradeInfo.getData(0).getString("PSPT_TYPE_CODE", "").trim();
            String psptId = custPerTradeInfo.getData(0).getString("PSPT_ID");
            if (psptTypeCode.equals("D") || psptTypeCode.equals("E") || psptTypeCode.equals("G") || psptTypeCode.equals("L") || psptTypeCode.equals("M")) {
                //如开户证件是单位证件类型，则需同步使用人信息
                //RSRV_STR5 名称              RSRV_STR6 证件类型                     RSRV_STR7 证件号码
                custName = custPerTradeInfo.getData(0).getString("RSRV_STR5", "").trim();
                psptTypeCode = custPerTradeInfo.getData(0).getString("RSRV_STR6", "").trim();
                psptId = custPerTradeInfo.getData(0).getString("RSRV_STR7", "").trim();                 
            }
            
            if (custName!=null&&custName.length() > 0 &&psptTypeCode!=null&& psptTypeCode.length() > 0 &&psptId!=null&& psptId.length() > 0) {
                IData compara = new DataMap();
                compara.put("SUBSYS_CODE", "CSM");
                compara.put("PARAM_ATTR", "2552");
                compara.put("PARAM_CODE", psptTypeCode);
                compara.put("EPARCHY_CODE", "ZZZZ");
                IDataset openLimitResult = CommparaInfoQry.getCommparaAllCol(compara.getString("SUBSYS_CODE", ""), compara.getString("PARAM_ATTR", ""), compara.getString("PARAM_CODE", ""), compara.getString("EPARCHY_CODE", ""));

                //证件类型转换
                IDataset checkResults = bean.checkPspt(psptTypeCode);
                if (IDataUtil.isNotEmpty(checkResults)) {
                    String psptTypeCodeChange = checkResults.getData(0).getString("PARA_CODE1");

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
                    custData.put("ID_CARD_TYPE", psptTypeCodeChange);
                    custData.put("ID_CARD_NUM", psptId);
                    IData custInfo = UcaInfoQry.qryCustInfoByCustId(mainTrade.getString("CUST_ID"));
                	if(IDataUtil.isNotEmpty(custInfo)){
                		String  oldCustName=custInfo.getString("CUST_NAME", "");
                		if(!oldCustName.equals(custName)){
                			//客户姓名有修改过
                			custData.put("RSRV_STR2", custName);
                		}
                	}
                	custData.put("OPR", "03");
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

                    /*            //先进行预占操作
                                IData input = new DataMap();
                                input.put("CUST_NAME", custName);
                                input.put("PSPT_TYPE_CODE", psptTypeCode);
                                input.put("PSPT_ID", psptId);
                                input.put("TRADE_TYPE_CODE", mainTrade.getString("TRADE_TYPE_CODE",""));
                                input.put("SERIAL_NUMBER", mainTrade.getString("SERIAL_NUMBER",""));
                                IDataset campDs =bean.idCheckAndCampOn(input);                        
                                */
                    //判断用户品牌和是否为行业应用卡批量开户
                    boolean  isFlag=bean.isCheckBrandAndHYYYKBATCHOPEN(mainTrade);
                    
                    logger.debug("OpenLimitAddAction---isIgnoreCall:"+isIgnoreCall+",openLimitResult:"+openLimitResult+",isFlag:"+isFlag);
                    System.out.println("================wang"+"OpenLimitAddAction---isIgnoreCall:"+isIgnoreCall+",openLimitResult:"+openLimitResult+",isFlag:"+isFlag);
                    if (!isIgnoreCall && DataSetUtils.isNotBlank(openLimitResult) && isFlag) {

                    	System.out.println("================wang");
                    	/**
                    	 * REQ201705210001_全国一证五号考核相关优化
                    	 * @author zhuoyingzhi
                    	 * @date 20170608
                    	 * 修改为直接插表，不调用实时接口。
                    	 */
                    	custData.put("CHECKING_DATE", SysDateMgr.getSysDateYYYYMMDD());
                    	/**
                    	 * 往后添加5秒中,为了区别OpenLimitDeleteAction完工时OPR_TIME一样
                    	 */
            		    String  sysdata5=SysDateMgr.decodeTimestamp(SysDateMgr.addSecond(SysDateMgr.getSysDateYYYYMMDDHHMMSS(), 5), SysDateMgr.PATTERN_STAND_SHORT);
                        custData.put("OPR_TIME",  sysdata5);
                    	if(IDataUtil.isNotEmpty(custInfo)){
                    		String oldCustName=custInfo.getString("CUST_NAME", "");
                    		if(!oldCustName.equals(custName)){
                    			//客户姓名有修改过
                    			custData.put("RSRV_STR2", custName);
                    		}
                    	}
                    	custData.put("OPR", "03");
                    	//custData.put("EFFETI_TIME", mainTrade.getString("ACCEPT_DATE"));
                        custData.put("EFFETI_TIME", OPEN_DATE);//入网时间，格式YYYYMMDDHH24MISS，平台将以该字段记录用户手机号码入网时间。填写14位真实入网时间,开户，销户，过户，资料变更都需要填写，平台以省端为准更新入网时间，同时记录入库时间。
                    	
                    	IDataUtil.chkParam(custData, "CHECKING_DATE");
                    	IDataUtil.chkParam(custData, "OPR_TIME");
                    	IDataUtil.chkParam(custData, "OPR");
                    	IDataUtil.chkParam(custData, "EFFETI_TIME");
                    	IDataUtil.chkParam(custData, "HOME_PROV");
                        if(custData.getString("HOME_PROV","").trim().length()>3){
                            String homeProv  = custData.getString("HOME_PROV","").trim();
                            custData.put("HOME_PROV", homeProv.substring(homeProv.length()-3));
                        }
                        custData.put("TRADE_TYPE_CODE", mainTrade.getString("TRADE_TYPE_CODE", ""));
                        
                        //预占字段  0是预占
                        custData.put("STATE", "0");
                        
                        
                        //写入IBOSS扫描操作的预占表
                        if("60".equals(tradeTypeCode) || "100".equals(tradeTypeCode)){
                        	//客户资料变更或过户
                        	if(bean.isCheckOpenLimitModifyCustInfo(mainTrade, custName)){
                        		//客户姓名 修改过   入表
                        		bean.regCampOnIBOSS(custData);
                        	}
                        }else{
                        	 bean.regCampOnIBOSS(custData);
                        }

                        /*
                        //查询预占凭证
                        IData param = new DataMap();
                        param.put("ID_CARD_TYPE", custData.getString("ID_CARD_TYPE"));
                        param.put("ID_CARD_NUM", custData.getString("ID_CARD_NUM"));
                        param.put("CAMP_ON", "01");
                        IDataset PSeqInfos = bean.selCampOn(param);
                        //如果找不到预占凭证，认为可能是网络原因之类的导致没有预占信息，不同步，等待对账处理
                        if (IDataUtil.isNotEmpty(PSeqInfos)) {
                            custData.put("P_SEQ", PSeqInfos.getData(0).getString("P_SEQ", ""));

                            custDataset.add(custData);
                            synParam.put("USER_DATASET", custDataset);
                            //调用同步接口
                            try {
                                bean.userInfoSyn(synParam);
                            } catch (Exception e) {
                                //重发处理
                                //e.printStackTrace();
                            }
                        }
                        */
                        }

                    //登记一证多号对账表
                   // bean.regOpenLimitCheck(custData);

                }
            }
        }
    }
}
