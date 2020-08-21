package com.asiainfo.veris.crm.order.soa.group.changememelement;

import java.util.Iterator;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.Clone;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.AttrBizInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductMebInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeDataLineAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderBaseBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.GrpInvoker;
import com.asiainfo.veris.crm.order.soa.group.dataline.DatalineUtil;
import com.asiainfo.veris.crm.order.soa.group.esp.DataLineDiscntConst;
import com.asiainfo.veris.crm.order.soa.group.esp.DatalineEspUtil;

public class ChangeNetinMemberElementBean extends GroupOrderBaseBean {

    public void actOrderDataOther(IData map) throws Exception {
        String mainTradeId = "";
        IDataset attrInternet = new DatasetList(map.getString("PRODUCT_PARAM_INFO"));
        IData mapInfos = (IData) Clone.deepClone(map);
        // 修改主用户
        // GrpInvoker.ivkProduct(map, BizCtrlType.ChangeUserDis, "CreateUserClass");

        // 解析专线数据
        IDataset internet = DatalineUtil.parseDataInfo(attrInternet);

        IDataset commonData = DatalineUtil.parseCommonDataInfo(attrInternet);

        IDataset dataline = DatalineUtil.parseDataLineInfo(attrInternet);

        // 校验同一个专线实例是否存在未完工的工单
        if (IDataUtil.isNotEmpty(dataline)) {
            for (int j = 0; j < dataline.size(); j++) {
                IData lineSet = dataline.getData(j);
                if (IDataUtil.isNotEmpty(lineSet)) {
                    String numberCode = lineSet.getString("PRODUCTNO", "");
                    if (StringUtils.isNotBlank(numberCode)) {
                        IDataset lineData = TradeDataLineAttrInfoQry.qryDatalineInstanceByProductNo(numberCode);
                        if (IDataUtil.isNotEmpty(lineData)) {
                            CSAppException.apperr(GrpException.CRM_GRP_713, "该专线实例" + numberCode + "存在未完工的工单!");
                        }

                    }
                }
            }
        }

        // 合同变更时,dataline是空的
        if (IDataUtil.isEmpty(dataline)) {
            if (IDataUtil.isNotEmpty(internet)) {
                for (int j = 0; j < internet.size(); j++) {
                    IData lineSet = internet.getData(j);
                    if (IDataUtil.isNotEmpty(lineSet)) {
                        String numberCode = lineSet.getString("pam_NOTIN_PRODUCT_NUMBER", "");
                        if (StringUtils.isNotBlank(numberCode)) {
                            IDataset lineData = TradeDataLineAttrInfoQry.qryDatalineInstanceByProductNo(numberCode);
                            if (IDataUtil.isNotEmpty(lineData)) {
                                CSAppException.apperr(GrpException.CRM_GRP_713, "该专线实例" + numberCode + "存在未完工的工单!!");
                            }

                        }
                    }
                }
            }
        }

        if ("true".equals(map.getString("CHANGEGRP_TAG"))) {
            IData param = (IData) Clone.deepClone(map);
            param.put("COMMON_DATA", commonData);
            GrpInvoker.ivkProduct(param, BizCtrlType.ChangeUserDis, "ChangeUserDis");
        } else if (null != internet && internet.size() > 0) {
            for (int i = 0; i < internet.size(); i++) {
                IData maps = new DataMap();
                IData inter = internet.getData(i);
                String lineNumberCode = inter.getString("pam_NOTIN_PRODUCT_NUMBER");

                if (null != dataline && dataline.size() > 0) {
                    for (int j = 0; j < dataline.size(); j++) {
                        IData datas = dataline.getData(j);
                        String numberCode = datas.getString("PRODUCTNO");
                        if (numberCode.equals(lineNumberCode)) {
                            maps = (IData) Clone.deepClone(map);
                            maps.put("ATTRINTERNET", inter);
                            maps.put("DATALINE", datas);
                            maps.put("COMMON_DATA", commonData);

                            // 处理成员信息
                            changeMemberElement(maps, commonData, datas, mainTradeId, lineNumberCode, mapInfos, internet, inter);

                            // GrpInvoker.ivkProduct(maps, BizCtrlType.ChangeUserDis, "CreateClass");
                        }
                    }
                } else if (null == dataline || dataline.size() < 1) {
                    maps = (IData) Clone.deepClone(map);
                    maps.put("ATTRINTERNET", inter);
                    // 变更成员
                    maps = (IData) Clone.deepClone(maps);
                    String productId = maps.getString("PRODUCT_ID");
                    if ("7011".equals(productId)||"70111".equals(productId)||"70112".equals(productId)) {
                        String mebOfferCode = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_EWE_CONFIG", new String[] { "CONFIGNAME", "PARAMNAME" }, "PARAMVALUE", new String[] { "MEM_PRODUCT_ID", productId });
                        maps.put("PRODUCT_ID", mebOfferCode);
                        // 查询专线信息
                        IData inparam = new DataMap();
                        inparam.put("SHEET_TYPE", "6");
                        inparam.put("PRODUCT_NO", lineNumberCode);
                        IDataset datalineList = TradeOtherInfoQry.queryUserDataLineByProductNo(inparam);
                        if (IDataUtil.isNotEmpty(datalineList)) {
                            String userIdB = datalineList.getData(0).getString("USER_ID");
                            maps.put("USER_ID", userIdB);
                        }
                        //根据53资费来的，不需要判断了
                        /*IData result  =  DatalineEspUtil.getOnceDiscntAttr(SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND), maps.getString("USER_ID"), maps.getString("PRODUCT_ID"));
                        Boolean onceDiscnt  = result.getBoolean("FLAG");
                        if(onceDiscnt){
                    		inter.put("pam_NOTIN_INSTALLATION_COST", "0");
                    	}*/
                        maps.put("ELEMENT_LIST", DatalineUtil.getElementInfo(map.getDataset("ELEMENT_INFO_LIST"), inter, mebOfferCode));
                        GrpInvoker.ivkProduct(maps, BizCtrlType.ChangeMemberDis, "ChangeMemberDis");
                    }
                    // GrpInvoker.ivkProduct(maps, BizCtrlType.ChangeUserDis, "CreateClass");
                }
            }
        }

        // // 获取主用户TRADE
        // List<BizData> gbd = DataBusManager.getDataBus().getGrpBizData();
        //
        // if (null != gbd && gbd.size() > 0) {
        // IDataset user = gbd.get(0).getTradeUser();
        // mainTradeId = user.getData(0).getString("TRADE_ID");
        // } else {
        // CSAppException.apperr(GrpException.CRM_GRP_713, "本笔订单未生成订单，请检查入参!!");
        // }

        // // 建立部分依赖关系
        // List<BizData> bd = DataBusManager.getDataBus().getGrpBizData();
        // for (int i = 0; i < bd.size(); i++) {
        // IDataset other = bd.get(i).getTradeOther();
        //
        // if (null != other && other.size() > 0) {
        //
        // DatalineUtil.createLimit(mainTradeId, other.getData(0).getString("TRADE_ID"));
        //
        // }
        // }

    }

    @Override
    protected String setOrderTypeCode() throws Exception {
        return "3088";
    }

    public void changeMemberElement(IData maps, IDataset commonData, IData datas, String mainTradeId, String lineNumberCode, IData mapInfos, IDataset internet, IData inter) throws Exception {
        String productId = maps.getString("PRODUCT_ID");
        String userIdA = maps.getString("USER_ID");
        String eparchyTypeCode = CSBizBean.getTradeEparchyCode(); // 成员地州
        String userIdB = "";
        String serialNumberB = "";
        String serialNumberA = "";
        String flag = "2";// 2修改专线 ;0新增专线 ;1 删除专线
        String mebOfferCode = "";// 获取成员专线offerCode
        if (null != commonData && commonData.size() > 0) {
            for (int i1 = 0; i1 < commonData.size(); i1++) {
                IData sheetTypeData = commonData.getData(i1);
                if ("SHEETTYPE".equals(sheetTypeData.getString("ATTR_CODE"))) {
                    if ("32".equals(sheetTypeData.getString("ATTR_VALUE"))) {
                        flag = "0"; // 0新增专线
                    } else if ("34".equals(sheetTypeData.getString("ATTR_VALUE"))) {
                        flag = "1"; // 1 删除专线
                    }

                }
            }
        }
        if ("7011".equals(productId)||"70111".equals(productId)||"70112".equals(productId)) {
            mebOfferCode = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_EWE_CONFIG", new String[] { "CONFIGNAME", "PARAMNAME" }, "PARAMVALUE", new String[] { "MEM_PRODUCT_ID", productId });
            maps.put("PRODUCT_ID", mebOfferCode);
            // 查询专线信息
            IData inparam = new DataMap();
            inparam.put("SHEET_TYPE", "6");
            inparam.put("PRODUCT_NO", lineNumberCode);
            IData userAInfos = UcaInfoQry.qryUserInfoByUserId(userIdA);
            if (IDataUtil.isNotEmpty(userAInfos)) {
                serialNumberA = userAInfos.getString("SERIAL_NUMBER");
            } else {
                CSAppException.apperr(GrpException.CRM_GRP_713, "根据集团用户标识【" + userIdA + "】，查询服务号码失败！");
            }
            IDataset datalineList = TradeOtherInfoQry.queryUserDataLineByProductNo(inparam);
            if (IDataUtil.isNotEmpty(datalineList)) {
                userIdB = datalineList.getData(0).getString("USER_ID");

                IData userBInfos = UcaInfoQry.qryUserInfoByUserId(userIdB);

                if (IDataUtil.isNotEmpty(userBInfos)) {
                    serialNumberB = userBInfos.getString("SERIAL_NUMBE");
                } else {
                    CSAppException.apperr(GrpException.CRM_GRP_713, "根据成员用户标识【" + userIdB + "】，查询服务号码失败！");
                }
                maps.put("USER_ID", userIdB);
                maps.put("SERIAL_NUMBER", serialNumberB);
            } else {
                if ("0".equals(flag)) {
                    mapInfos.put("TRADE_ID", mainTradeId);
                    mapInfos.put("SERIAL_NUMBER", serialNumberA);
                    IData grpInfos = UcaInfoQry.qryGrpInfoByUserId(userIdA);
                    String custId = grpInfos.getString("CUST_ID");
                    mapInfos.put("CUST_ID", custId);
                    createNewMeb(mapInfos);
                } else {
                    CSAppException.apperr(GrpException.CRM_GRP_713, "根据专线实列号【" + lineNumberCode + "】，未查询到专线信息！");
                }
            }
        }

        if ("1".equals(flag)) {
            IData desrtoylineInfo = new DataMap();
            desrtoylineInfo.put("COMMON_DATA", commonData);
            desrtoylineInfo.put("LINE_DATA", datas);
            desrtoylineInfo.put("TRADE_ID", mainTradeId);
            desrtoylineInfo.put("SERIAL_NUMBER", serialNumberB);
            desrtoylineInfo.put("USER_ID", userIdA);
            desrtoylineInfo.put("USER_EPARCHY_CODE", eparchyTypeCode);
            CSAppCall.call("SS.DestoryDatalineGroupMemberSVC.crtTrade", desrtoylineInfo);
        } else if ("2".equals(flag)) {
        	String inserttime  = "";
        	if(IDataUtil.isNotEmpty(EOS)){
        		inserttime = DatalineEspUtil.getChangeDate(EOS.first());//获取计费结束时间
        	}
        	IData result  =  DatalineEspUtil.getOnceDiscntAttr(inserttime, userIdB, maps.getString("PRODUCT_ID"));
        	Boolean onceDiscnt  = result.getBoolean("FLAG");
        	String chaneMode = "";
        	for(int i=0;i<commonData.size();i++){
        		IData com =  commonData.getData(i);
        		if("CHANGEMODE".equals(com.getString("ATTR_CODE"))){
        			chaneMode = com.getString("ATTR_VALUE");
        		}
        	}
        	
        	String changflag = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_EWE_CONFIG", new String[] { "CONFIGNAME", "PARAMNAME" }, "PARAMVALUE", new String[] { "ONECEATTRCHANGEMODE", chaneMode });
        	if(StringUtils.isNotEmpty(changflag)){
        		if(onceDiscnt){	
            		IDataset elementInfo =  new DatasetList();
            		IData element = new DataMap();
            		element.put("ELEMENT_ID", DataLineDiscntConst.ONCEElementId);
            		element.put("MODIFY_TAG", "0");
            		element.put("ELEMENT_TYPE_CODE", "D");
            		element.put("PRODUCT_ID", productId);
            		element.put("PACKAGE_ID", "0");
            		element.put("INST_ID", "");
            		element.put("START_DATE",inserttime);
            		element.put("END_DATE", SysDateMgr.getAddMonthsLastDayNoEnv(0, inserttime));
            		IDataset attrParam =  new DatasetList();
            		IData param =  new DataMap();
            		param.put("ATTR_VALUE", inter.getString("pam_NOTIN_INSTALLATION_COST", "0"));
            		param.put("ATTR_CODE", "59701004");
            		attrParam.add(param);
            		element.put("ATTR_PARAM", attrParam);
            		elementInfo.add(element);
            		maps.put("ELEMENT_INFO",elementInfo);//新增资费
            	}else{
            		IData input = new DataMap();
                    input.put("USER_ID", maps.getString("USER_ID"));
                    input.put("PRODUCT_ID", productId);
                    IData lineInfo = CSAppCall.callOne("CS.UserAttrInfoQrySVC.getDiscountByUserId", input);
            		String startDate  = result.getString("START_DATE");
            		startDate = SysDateMgr.decodeTimestamp(startDate, SysDateMgr.PATTERN_TIME_YYYYMM);
            		String inserttimee = SysDateMgr.decodeTimestamp(inserttime, SysDateMgr.PATTERN_TIME_YYYYMM);
            		if(Double.parseDouble(startDate) == Double.parseDouble(inserttimee)){
            			Double onceOld = Double.parseDouble(lineInfo.getString(DataLineDiscntConst.cost,"0")); 
                        Double onceNew = Double.parseDouble(inter.getString("pam_NOTIN_INSTALLATION_COST", "0"));
                		String once =  String.valueOf(onceOld + onceNew);
                		once = once.substring(0, once.indexOf("."));
                        IDataset elementInfo =  new DatasetList();
                		IData element = new DataMap();
                		element.put("ELEMENT_ID", DataLineDiscntConst.ONCEElementId);
                		element.put("MODIFY_TAG", "0");
                		element.put("ELEMENT_TYPE_CODE", "D");
                		element.put("PRODUCT_ID", productId);
                		element.put("PACKAGE_ID", "0");
                		element.put("INST_ID", "");
                		element.put("START_DATE",inserttime);
                		element.put("END_DATE", SysDateMgr.getAddMonthsLastDayNoEnv(0, inserttime));
                		IDataset attrParam =  new DatasetList();
                		IData param =  new DataMap();
                		param.put("ATTR_VALUE", once);
                		param.put("ATTR_CODE", "59701004");
                		attrParam.add(param);
                		element.put("ATTR_PARAM", attrParam);
                		elementInfo.add(element);
                		maps.put("ELEMENT_INFO",elementInfo);//新增资费
            		}else if((Double.parseDouble(startDate) < Double.parseDouble(inserttimee)) || (Double.parseDouble(startDate) > Double.parseDouble(inserttimee))){
            			String  onceOld  = lineInfo.getString(DataLineDiscntConst.cost,"0");
                        String  onceNew =  inter.getString("pam_NOTIN_INSTALLATION_COST", "0");
                        IDataset elementInfo =  new DatasetList();
                		IData element = new DataMap();
                		element.put("ELEMENT_ID", DataLineDiscntConst.ONCEElementId);
                		element.put("MODIFY_TAG", "0");
                		element.put("ELEMENT_TYPE_CODE", "D");
                		element.put("PRODUCT_ID", productId);
                		element.put("PACKAGE_ID", "0");
                		element.put("INST_ID", "");
                		element.put("START_DATE",inserttime);
                		element.put("END_DATE", SysDateMgr.getAddMonthsLastDayNoEnv(0, inserttime));
                		IDataset attrParam =  new DatasetList();
                		IData param =  new DataMap();
                		param.put("ATTR_VALUE", onceNew);
                		param.put("ATTR_CODE", "59701004");
                		attrParam.add(param);
                		element.put("ATTR_PARAM", attrParam);
                		elementInfo.add(element);
                		
                		IData elements = new DataMap();
                		elements.put("ELEMENT_ID", DataLineDiscntConst.ONCEElementId);
                		elements.put("MODIFY_TAG", "0");
                		elements.put("ELEMENT_TYPE_CODE", "D");
                		elements.put("PRODUCT_ID", productId);
                		elements.put("PACKAGE_ID", "0");
                		elements.put("INST_ID", "");
                		elements.put("START_DATE",result.getString("START_DATE"));
                		elements.put("END_DATE", SysDateMgr.getAddMonthsLastDayNoEnv(0, result.getString("START_DATE")));
                		IDataset attrParams =  new DatasetList();
                		IData params =  new DataMap();
                		params.put("ATTR_VALUE", onceOld);
                		params.put("ATTR_CODE", "59701004");
                		
                		elements.put("ATTR_PARAM", attrParams);
                		elementInfo.add(elements);
                		maps.put("ELEMENT_INFO",elementInfo);//新增资费
            		}
            		
            	}
        	}
        	
        	maps.put("INSERT_TIME", inserttime);
            maps.put("ELEMENT_LIST", DatalineUtil.getElementInfo(maps.getDataset("ELEMENT_INFO_LIST"), inter, mebOfferCode));
            GrpInvoker.ivkProduct(maps, BizCtrlType.ChangeMemberDis, "ChangeMemberDis");
        }
    }

    private void createNewMeb(IData map) throws Exception {
        // 创建集团用户信息
        int pfWait = 1;
        String mainTradeId = "";
        String serialNumber = "";
        String productId = map.getString("PRODUCT_ID");

        IDataset attrInternet = new DatasetList(map.getString("PRODUCT_PARAM_INFO"));
        // 判断是否开环
        pfWait = DatalineUtil.getDatalineOrderPfWait(attrInternet);
        map.put("PF_WAIT", pfWait);

        // 变更时新增专线
        mainTradeId = map.getString("TRADE_ID");
        serialNumber = map.getString("SERIAL_NUMBER");
        // 解析专线数据
        IDataset internet = DatalineUtil.parseDataInfo(attrInternet);

        IDataset commonData = DatalineUtil.parseCommonDataInfo(attrInternet);

        IDataset dataline = DatalineUtil.parseDataLineInfo(attrInternet);

        if (null != internet && internet.size() > 0) {
            for (int i = 0; i < internet.size(); i++) {
                IData inter = internet.getData(i);
                if (null != dataline && dataline.size() > 0) {
                    IData datas = dataline.getData(i);
                    datas.putAll(inter);
                }
            }
        }

        // 生成成员SERIAL_NUMBER
        String mebProductId = ProductMebInfoQry.getMemberMainProductByProductId(productId);
        IDataset tradeInfos = AttrBizInfoQry.getBizAttr(mebProductId, "P", BizCtrlType.CreateUser, "TradeTypeCode", null);
        String tradeTypeCode = tradeInfos.first().getString("ATTR_VALUE", "");
        if (StringUtils.isEmpty(tradeTypeCode)) {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据" + mebProductId + "没找到业务类型");
        }
        if (null != internet && internet.size() > 0) {
            for (int i = 0; i < internet.size(); i++) {
                IData inter = internet.getData(i);
                String lineNumberCode = inter.getString("pam_NOTIN_LINE_NUMBER_CODE");

                if (null != dataline && dataline.size() > 0) {
                    for (int j = 0; j < dataline.size(); j++) {
                        IData datas = dataline.getData(j);
                        String numberCode = datas.getString("pam_NOTIN_LINE_NUMBER_CODE");

                        if (numberCode.equals(lineNumberCode)) {
                            // 避免服务号码的重复 add begin
                            IData param = new DataMap();
                            param.put("PRODUCT_ID", productId);
                            param.put(Route.USER_EPARCHY_CODE, CSBizBean.getUserEparchyCode());

                            IDataset grpSnData = new DatasetList();
                            for (int k = 0; k < 10; k++) {
                                grpSnData = CSAppCall.call("CS.GrpGenSnSVC.genGrpSn", param);

                                String serialNumberMeb = grpSnData.first().getString("SERIAL_NUMBER", "");

                                if (StringUtils.isEmpty(serialNumberMeb)) {
                                    break;
                                }

                                IData userList = UcaInfoQry.qryUserMainProdInfoBySnForGrp(serialNumberMeb);
                                param.clear();
                                param.put("SERIAL_NUMBER", serialNumber);
                                param.put(Route.USER_EPARCHY_CODE, CSBizBean.getUserEparchyCode());
                                param.put("TRADE_TYPE_CODE", tradeTypeCode);
                                IDataset tradeList = CSAppCall.call("CS.TradeInfoQrySVC.getMainTradeBySN", param);

                                if (IDataUtil.isEmpty(userList) && IDataUtil.isEmpty(tradeList)) {
                                    break;
                                }
                            }
                            // 避免服务号码的重复 add end

                            String mebSerialNumber = grpSnData.first().getString("SERIAL_NUMBER", "");

                            // 获取专线名
                            String lineName = "";
                            Iterator<String> it = inter.keySet().iterator();
                            while (it.hasNext()) {
                                String lineNamekey = it.next();
                                if ("pam_NOTIN_LINE_NUMBER".equals(lineNamekey)) {
                                    lineName = inter.getString(lineNamekey, "");
                                    break;
                                }
                            }

                            // 添加成员所需数据
                            map.put("TRADE_ID", mainTradeId);
                            map.put("GRP_SERIAL_NUMBER", serialNumber);
                            map.put("SERIAL_NUMBER", mebSerialNumber);
                            map.put("PAY_NAME", lineName);
                            // 成员新增账户
                            map.put("ACCT_IS_ADD", true);
                            map.put("ATTRINTERNET", inter);
                            map.put("DATALINE", datas);
                            map.put("COMMON_DATA", commonData);
                            map.put("CUST_ID", map.getString("CUST_ID"));
                            GrpInvoker.ivkProduct(map, BizCtrlType.CreateMember, "CreateUserClass");

                        }
                    }
                }
            }
        }
    }

}
