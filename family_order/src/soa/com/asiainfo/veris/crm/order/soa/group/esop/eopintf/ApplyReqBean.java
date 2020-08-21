package com.asiainfo.veris.crm.order.soa.group.esop.eopintf;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CustException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.exception.TradeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.group.common.ScrData;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductMebInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.cfg.OfferCfg;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.GrpInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductCompInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;
import com.asiainfo.veris.crm.order.soa.group.esop.query.BusiFlowReleInfoQuery;
import com.asiainfo.veris.crm.order.soa.group.esop.query.EweBusiSpecReleInfoQry;
import com.asiainfo.veris.crm.order.soa.group.esop.query.EweConfigQry;
import com.asiainfo.veris.crm.order.soa.group.esop.query.QryFlowNodeDescBean;

public class ApplyReqBean extends EopIntfBaseBean {

    public IData createSubscriberOrder(IData param) throws Exception {
        IData result = new DataMap();

        //判断是否为集团用户（集团用户无需停机，停成员即可）
        IData checkData = checkIsNeedCredit(param);

        if(!checkData.getBoolean("IS_GRP_USER")) {
            result.put("X_RESULTCODE", FAIL_CODE);//返回失败
            result.put("X_RESULTINFO", checkData.getString("RESULTINFO"));
            return result;
        }

        //1、转换信控的操作码，加载专线用户数据
        dealOperCode(param);

        //2、加载公共信息
        IData commonData = loadCommonData(param);

        //3、加载用户数据
        loadUserInfo(param, commonData);

        //4、加载主表信息
        IData eopSubscriber = loadSubscriber(commonData);

        //5、加载节点信息
        IData eopNode = loadEopNode(commonData);

        //6、加载产品数据
        IData eopProduct = loadProduct(commonData);
        
        //7、加载成员产品数据
        IData eopProductSub = loadProductSub(commonData);

        //8、加载产品属性
        IDataset eopAttrList = loadProductAttr(param);

        //9、准备参数，调开通申请服务
        ScrData scrData = new ScrData();
        scrData.setWorkformSubscribe(eopSubscriber);
        scrData.setWorkformNode(eopNode);
        scrData.setWorkformProduct(new DatasetList(eopProduct));
        if("7010".equals(commonData.getString("PRODUCT_ID"))){
            scrData.setWorkformProductSub(loadVOIPProductSub(commonData, param));
        }else{
            scrData.setWorkformProductSub(new DatasetList(eopProductSub));
        }
        scrData.setWorkformAttr(eopAttrList);
        commonData.put("OPER_TYPE", commonData.getString("BUSIFORM_OPER_TYPE"));

        IData input = new DataMap();
        input.put("EOSAttr", scrData);
        input.put("EOSCom", commonData);

        IDataset dataset = CSAppCall.call("SS.WorkformRegisterSVC.register", input);
        result.put("IBSYSID", dataset.first().getString("IBSYSID", ""));
        result.put("X_RESULTCODE", SUCCESS_CODE);//保存成功，返回code为0
        result.put("X_RESULTINFO", SUCCESS_INFO);

        return result;
    }

    private IDataset loadProductAttr(IData param) throws Exception {
        IDataset eopAttrList = new DatasetList();
        String productId = param.getString("PRODUCT_ID");
        if("7010".equals(productId) || "7011".equals(productId) || "7012".equals(productId)) {
            return eopAttrList;
        }
        String execMonth = SysDateMgr.getCurMonth();
        String sysTime = SysDateMgr.getSysTime();
//        int seq = 0;

        IData eopAttr = dealComonEopAttr(execMonth, sysTime);
        eopAttr.put("ATTR_CODE", "TITLE");//属性编号
        eopAttr.put("ATTR_VALUE", param.getString("OPER_NAME"));//属性值       
        eopAttr.put("ATTR_NAME", "主题");//属性名称  
        eopAttrList.add(eopAttr);

        IDataset prodAttrList = param.getDataset("PARAMS");
        if(IDataUtil.isNotEmpty(prodAttrList)) {
            for (int i = 0, sizeI = prodAttrList.size(); i < sizeI; i++) {
                IDataset tmpAttrList = prodAttrList.getDataset(i);
                for (int j = 0, sizeJ = tmpAttrList.size(); j < sizeJ; j++) {
                    IData attr = dealComonEopAttr(execMonth, sysTime);
                    attr.put("RECORD_NUM", i);
                    attr.put("ATTR_CODE", tmpAttrList.getData(j).getString("PARAM_CODE"));//属性编号
                    attr.put("ATTR_VALUE", tmpAttrList.getData(j).getString("PARAM_VALUE"));//属性值                  
                    attr.put("ATTR_NAME", tmpAttrList.getData(j).getString("PARAM_NAME"));//属性名称   
                    eopAttrList.add(attr);
                }
            }
        }
        return eopAttrList;
    }

    private IData loadProduct(IData commonData) throws Exception {
        IData eopProduct = new DataMap();
        eopProduct.put("USER_ID", commonData.getString("USER_ID"));
        eopProduct.put("SERIAL_NUMBER", commonData.getString("SERIAL_NUMBER"));
        eopProduct.put("PRODUCT_ID", commonData.getString("PRODUCT_ID"));
        eopProduct.put("PRODUCT_NAME", commonData.getString("PRODUCT_NAME"));
        eopProduct.put("RSRV_STR1", commonData.getString("OFFER_ID"));
        eopProduct.put("RECORD_NUM", "0");

        return eopProduct;
    }

    private IData loadProductSub(IData commonData) throws Exception {
        IData eopProductSub = new DataMap();
        String mebUserId = commonData.getString("MEB_USER_ID");
        if(StringUtils.isBlank(mebUserId)) {
            return eopProductSub;
        }
        eopProductSub.put("USER_ID", commonData.getString("MEB_USER_ID"));
        eopProductSub.put("MEB_SERIAL_NUMBER", commonData.getString("MEB_SERIAL_NUMBER"));
        eopProductSub.put("PRODUCT_ID", commonData.getString("MEB_PRODUCT_ID"));
        eopProductSub.put("PRODUCT_NAME", commonData.getString("MEB_PRODUCT_NAME"));
        eopProductSub.put("RSRV_STR1", commonData.getString("MEB_OFFER_ID"));
        eopProductSub.put("RECORD_NUM", "1");

        return eopProductSub;
    }

    private IDataset loadVOIPProductSub(IData commonData, IData param) throws Exception {
        IDataset eopProductSubList = new DatasetList();
        IDataset datalines = null;
        String bpmTempletId = commonData.getString("BPM_TEMPLET_ID");
        IData inparam = new DataMap();
        inparam.put("USER_ID", param.getString("USER_ID"));
        if("CREDITDIRECTLINEPARSE".equals(bpmTempletId)) {
            datalines = TradeOtherInfoQry.queryUserDataLineByUserId(inparam);
        } else if("CREDITDIRECTLINECONTINUE".equals(bpmTempletId)) {
            datalines = TradeOtherInfoQry.queryUserDataLineByUserIdAndProductNoForDatalineKJ(inparam);
        }
        if(IDataUtil.isEmpty(datalines)){
            CSAppException.apperr(GrpException.CRM_GRP_713, "未获取到有效专线信息！");
        }
        for (int i = 0; i < datalines.size(); i++) {
            IData eopProductSub = new DataMap();
            eopProductSub.put("PRODUCT_ID", "-1");
            eopProductSub.put("RECORD_NUM", i + 1);
            eopProductSubList.add(eopProductSub);
        }
        return eopProductSubList;
    }

    private IData loadSubscriber(IData commonData) throws Exception {
        IData eopSubscriber = new DataMap();

        eopSubscriber.put("GROUP_ID", commonData.getString("GROUP_ID"));
        eopSubscriber.put("CUST_NAME", commonData.getString("CUST_NAME"));
        eopSubscriber.put("RSRV_STR1", commonData.getString("TRADE_TYPE_CODE"));
        eopSubscriber.put("RSRV_STR3", StaticUtil.getStaticValue("URGENCY_LEVEL", commonData.getString("URGENCY_LEVEL")));
        eopSubscriber.put("RSRV_STR4", commonData.getString("TITLE"));
        eopSubscriber.put("FLOW_LEVEL", StaticUtil.getStaticValue("URGENCY_LEVEL", commonData.getString("URGENCY_LEVEL")));
        eopSubscriber.put("FLOW_DESC", commonData.getString("TITLE"));

        return eopSubscriber;
    }

    private IData loadEopNode(IData commonData) throws Exception {
        IData eopNode = new DataMap();

        eopNode.put("PRODUCT_ID", commonData.getString("PRODUCT_ID"));
        eopNode.put("NODE_ID", commonData.getString("NODE_ID"));

        return eopNode;
    }

    private void loadUserInfo(IData param, IData commonData) throws Exception {
        String operCode = getMandaData(param, "OPER_TYPE");
        if(operCode.startsWith("credit")) {
            String userId = getMandaData(param, "USER_ID");
            String productId = getMandaData(param, "PRODUCT_ID");
            String serialNumber = getMandaData(param, "SERIAL_NUMBER");

            commonData.put("USER_ID", userId);
            commonData.put("SERIAL_NUMBER", serialNumber);
            if("7011".equals(productId) || "7012".equals(productId)) {
                IDataset userProductList = UserProductInfoQry.queryUserProductByUserId(userId);
                if(IDataUtil.isEmpty(userProductList)) {
                    CSAppException.apperr(GrpException.CRM_GRP_713, "根据集团用户编码【" + userId + "】未查询到有效集团用户产品信息！");
                }

                String mebProductID = userProductList.first().getString("PRODUCT_ID");
                commonData.put("MEB_PRODUCT_ID", mebProductID);
                commonData.put("MEB_PRODUCT_NAME", userProductList.first().getString("PRODUCT_NAME"));
                OfferCfg meboffercfg = OfferCfg.getInstance(mebProductID, BofConst.ELEMENT_TYPE_CODE_PRODUCT);
                if(meboffercfg != null) {
                    commonData.put("MEB_OFFER_ID", meboffercfg.getOfferId());
                }

                String relationTypeCode = ProductCompInfoQry.getRelaTypeCodeByProductId(productId);
                IDataset grpUsers = RelaUUInfoQry.qryRelaByUserIdBRelaTypeCode(userId, relationTypeCode, null);
                if(IDataUtil.isEmpty(grpUsers)) {
                    CSAppException.apperr(GrpException.CRM_GRP_713, "根据成员用户编码【" + userId + "】未查询到有效集团用户！");
                }
                String grpUserId = grpUsers.first().getString("USER_ID_A");
                String grpSerialNumber = grpUsers.first().getString("SERIAL_NUMBER_A");
                commonData.put("USER_ID", grpUserId);
                commonData.put("SERIAL_NUMBER", grpSerialNumber);
                commonData.put("MEB_USER_ID", userId);
                commonData.put("MEB_SERIAL_NUMBER", serialNumber);
            }
            
            /*OfferCfg offercfg = OfferCfg.getInstance(grpProductId, BofConst.ELEMENT_TYPE_CODE_PRODUCT);
            if(offercfg != null) {
                commonData.put("OFFER_ID", offercfg.getOfferId());
            }*/

            /*IDataset userList = UserInfoQry.getUserInfoByUserIdTag(userId, "0");
            if(IDataUtil.isNotEmpty(userList)) {
                String custId = userList.first().getString("CUST_ID");
                IDataset groupList = GrpInfoQry.qryCustGroupInfoByCustid(custId);
                if(IDataUtil.isNotEmpty(groupList)) {
                    commonData.put("GROUP_ID", groupList.first().getString("GROUP_ID"));
                    commonData.put("CUST_NAME", groupList.first().getString("CUST_NAME"));
                }
            }*/

            //信控工单主题
            commonData.put("TITLE", "信控工单");
            commonData.put("URGENCY_LEVEL", "1");
        }
    }

    private IData loadCommonData(IData param) throws Exception {
        IData commonData = new DataMap();

        String operCode = getMandaData(param, "OPER_TYPE");
        String productId = getMandaData(param, "PRODUCT_ID");
        String userId = getMandaData(param, "USER_ID");
        //处理带成员专线产品
        if("97011".equals(productId) || "97012".equals(productId)) {
            param.put("MEB_PRODUCT_ID", productId);
            IDataset grpProducts = UProductMebInfoQry.queryGrpProductInfosByMebProductId(productId);
            if(IDataUtil.isEmpty(grpProducts)) {
                CSAppException.apperr(GrpException.CRM_GRP_713, "根据成员产品编码【" + productId + "】未取到对应的集团产品编码！");
            }
            productId = grpProducts.first().getString("PRODUCT_ID");
            param.put("PRODUCT_ID", productId);
        }

        commonData.put("PRODUCT_ID", productId);
        commonData.put("BUSI_CODE", productId);
        commonData.put("IN_MODE_CODE", "0");
        commonData.put("GROUP_ID", param.getString("GROUP_ID"));
        commonData.put("TRADE_TYPE_CODE", param.getString("TRADE_TYPE_CODE"));

        if(StringUtils.isNotBlank(param.getString("GROUP_ID"))) {
            IDataset groupList = GrpInfoQry.queryGroupCustInfoByGroupId(param.getString("GROUP_ID"));
            if(IDataUtil.isEmpty(groupList)) {
                CSAppException.apperr(GrpException.CRM_GRP_713, "集团编码" + param.getString("GROUP_ID") + ",没有找到对应的集团");
            }
            commonData.put("GROUP_ID", param.getString("GROUP_ID"));
            commonData.put("CUST_NAME", groupList.first().getString("CUST_NAME"));
        } else {
            IData userInfo = UcaInfoQry.qryUserInfoByUserId(userId);
            if(IDataUtil.isEmpty(userInfo)) {
                CSAppException.apperr(TradeException.CRM_TRADE_180, userId);// 根据用户编码[%s]，查找用户资料不存在！
            }

            // 查询客户信息
            String custId = userInfo.getString("CUST_ID");
            IData custSet = UcaInfoQry.qryGrpInfoByCustId(custId);
            if(IDataUtil.isEmpty(custSet)) {
                CSAppException.apperr(CustException.CRM_CUST_111);// 查找客户资料无数据
            }
            param.put("GROUP_ID", custSet.getString("GROUP_ID"));
            param.put("SERIAL_NUMBER", userInfo.getString("SERIAL_NUMBER"));
            commonData.put("GROUP_ID", custSet.getString("GROUP_ID"));
            commonData.put("CUST_NAME", custSet.getString("CUST_NAME"));
        }

        OfferCfg offercfg = OfferCfg.getInstance(productId, BofConst.ELEMENT_TYPE_CODE_PRODUCT);
        if(offercfg != null) {
            commonData.put("PRODUCT_NAME", offercfg.getOfferName());
            commonData.put("OFFER_ID", offercfg.getOfferId());
        }

        IData inparam = new DataMap();
        inparam.put("BUSI_TYPE", operCode);
        inparam.put("PRODUCT_ID", productId);
        IDataset busiFlowReleList = BusiFlowReleInfoQuery.getBusiCodeByBusitypeAndBusiCode(inparam);
        if(IDataUtil.isEmpty(busiFlowReleList)) {
            CSAppException.apperr(GrpException.CRM_GRP_713, "产品[" + productId + "]操作[" + operCode + "]未配置业务流程关系表!");
        }
        String busiOperType = busiFlowReleList.first().getString("BUSI_OPER_TYPE", "");
        IDataset busiSpecReleList = EweBusiSpecReleInfoQry.qryBusiSpecReleByOperTypeProdId(busiOperType, productId);
        String bpmTempletId = null;
        if(IDataUtil.isNotEmpty(busiSpecReleList)) {
            if(busiSpecReleList.size() == 1) {
                bpmTempletId = busiSpecReleList.first().getString("BPM_TEMPLET_ID");
            } else if(busiSpecReleList.size() > 1 && !"credit".startsWith(operCode)) {
                String inputBusiType = getMandaData(param, "BUSI_TYPE");//商机传递过来业务大类
                for (int i = 0, size = busiSpecReleList.size(); i < size; i++) {
                    bpmTempletId = busiSpecReleList.getData(i).getString("BPM_TEMPLET_ID");
                    if(inputBusiType.equals(busiSpecReleList.getData(i).getString("BPM_TEMPLET_ID"))) {
                        break;
                    }
                }
            }
        } else {
            CSAppException.apperr(GrpException.CRM_GRP_713, "产品[" + productId + "]操作[" + operCode + "]未配置业务流程关系配置!");
        }
        if(StringUtils.isBlank(bpmTempletId)) {
            CSAppException.apperr(GrpException.CRM_GRP_713, "根据传入条件没有匹配到流程信息!");
        }

        IDataset nodeTempletList = QryFlowNodeDescBean.qryNodeTempletByBpmTempIdNodeType(bpmTempletId, "3", "0");
        if(IDataUtil.isEmpty(nodeTempletList)) {
            CSAppException.apperr(GrpException.CRM_GRP_713, "根据流程编码[" + bpmTempletId + "]没有查询到节点信息!");
        }
        commonData.put("BPM_TEMPLET_ID", bpmTempletId);
        commonData.put("NODE_ID", nodeTempletList.first().getString("NODE_ID"));
        commonData.put("BUSIFORM_OPER_TYPE", busiOperType);
        return commonData;
    }

    private void dealOperCode(IData param) throws Exception {
        String operCode = getMandaData(param, "OPER_TYPE");
        String userId = getMandaData(param, "USER_ID");
        if("credit".equals(operCode)) {
            int tradeTypeCode = Integer.parseInt(getMandaData(param, "TRADE_TYPE_CODE"));
            if(tradeTypeCode == 0) {
                CSAppException.apperr(GrpException.CRM_GRP_713, "业务类型[TRADE_TYPE_CODE]未传递！");
            } else if(tradeTypeCode == 7110 || tradeTypeCode == 7220 || tradeTypeCode == 7305) // 7110 高额停机 7220 欠费停机 7305 信用特殊停机
            {
                operCode = creditparse;
            } else if(tradeTypeCode == 7301 || tradeTypeCode == 7303 || tradeTypeCode == 7304 || tradeTypeCode == 7317) // 7301 交费开机 7303 高额开机 7304 信用特殊开机  7317 批量缴费开机
            {
                operCode = creditcontiue;
            } else if(tradeTypeCode == 7240) {
                operCode = creditcanle;
            } /*else if(tradeTypeCode == 7821) {//集团专线欠费停机
                //              operCode = creditlineparse;
              } else if(tradeTypeCode == 7822) {//集团专线欠费停机
                //              operCode = creditlinecontiue;
              }*/ else {
                CSAppException.apperr(GrpException.CRM_GRP_713, "业务类型[" + tradeTypeCode + "]非信控类！");
            }
        }
        /*else if("open".equals(operCode)) {
            operCode = "20";
          } else if("change".equals(operCode)) {
            operCode = "21";
          } else if("cancel".equals(operCode)) {
            operCode = "25";
          }*/

        param.put("OPER_TYPE", operCode);
    }

    private IData dealComonEopAttr(String exec_month, String update_time) throws Exception {
        IData eopAttr = new DataMap();
        eopAttr.put("EXEC_MONTH", exec_month);// 执行月份，取当前月
        eopAttr.put("UPDATE_TIME", update_time);// 更新时间
        eopAttr.put("SEQ", SeqMgr.getAttrSeq());// 序列号
        eopAttr.put("RECORD_NUM", "0"); // 默认为0
        return eopAttr;
    }

    private IData checkIsNeedCredit(IData param) throws Exception {
        IData result = new DataMap();
        result.put("IS_GRP_USER", false);
        String userId = getMandaData(param, "USER_ID");
        IDataset userProductList = UserProductInfoQry.queryUserProductByUserId(userId);
        if(IDataUtil.isNotEmpty(userProductList)) {
            String productId = userProductList.first().getString("PRODUCT_ID");
            IDataset configInfos = EweConfigQry.qryDistinctValueDescByParamName("CREDIT_CONTROL_PARAM", productId, "0");
            if(IDataUtil.isEmpty(configInfos)) {
                result.put("RESULTINFO", "未查询到ESOP信控配置！");
            }
            String paramValue = configInfos.first().getString("PARAMVALUE");
            if("true".equals(paramValue)) {
                result.put("IS_GRP_USER", true);
            } else {
                result.put("RESULTINFO", "ESOP信控配置未开启！");
            }

            /*if("7011".equals(productId) || "7012".equals(productId)) {
                result.put("IS_GRP_USER", false);
                result.put("RESULTINFO", "产品【" + userProductList.first().getString("PRODUCT_NAME") + "】的集团用户无需停机！");
            } else if("7010".equals(productId)) {
                IDataset bizList = AttrBizInfoQry.getBizAttr(productId, "P", BizCtrlType.ChangeUserDis, "IsNeedCredit", null);
                IData bizData = bizList.first();
                String flag = bizData.getString("ATTR_VALUE", "");
                if("false".equals(flag)) {
                    result.put("IS_GRP_USER", false);
                    result.put("RESULTINFO", "产品【" + userProductList.first().getString("PRODUCT_NAME") + "】暂未开启信控，请修改CRM配置开启信控！");
                } else if("true".equals(flag)) {
                    result.put("IS_GRP_USER", true);
                } else {
                    CSAppException.apperr(GrpException.CRM_GRP_713, "产品【" + userProductList.first().getString("PRODUCT_NAME") + "】信控配置错误，请检查！");
                }
            }*/
        }else{
            result.put("RESULTINFO", "未查询到当前用户【"+userId+"】产品信息！");
        }
        return result;
    }
}
