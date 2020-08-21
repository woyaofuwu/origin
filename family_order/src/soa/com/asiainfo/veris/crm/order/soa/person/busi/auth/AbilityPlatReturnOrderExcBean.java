package com.asiainfo.veris.crm.order.soa.person.busi.auth;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.BusinessAbilityCall;
import net.sf.json.JSONArray;

import com.ailk.bizcommon.route.Route;
import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.bizservice.base.CSAppCall;
import com.ailk.bizservice.base.CSBizBean;
import com.ailk.bizservice.dao.CrmDAO;
import com.ailk.bizservice.dao.Dao;
import com.ailk.bizservice.query.UcaInfoQry;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.database.dao.DAOManager;
import com.ailk.database.dbconn.DBConnection;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.RouteInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.exception.impl.BofException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.PBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.abilityopenplatform.AbilityOpenPlatQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.utils.ExceptionUtils;
import com.asiainfo.veris.crm.order.soa.person.common.query.queryInfo.QueryListInfo;
import com.asiainfo.veris.crm.order.soa.person.common.util.PersonUtil;

public class AbilityPlatReturnOrderExcBean extends  CSBizBean
{ 
    
    public IDataset tradeRegItem(IData input) throws Exception
    {   
        String serialNumber = input.getString("SERIAL_NUMBER");
        
        IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
        if (IDataUtil.isEmpty(userInfo))
        {
            CSAppException.apperr(BofException.CRM_BOF_002);
        }
        
        IDataset productList = getProductListByOprNum(input.getString("OPR_NUM"));
        
        if (IDataUtil.isEmpty(productList))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据流水号["+input.getString("OPR_NUM")+"]查询产品列表不存在");
        }
        
//        productList = DataHelper.filter(productList, "PRODUCT_TYPE=10100");
//        
//        if (IDataUtil.isEmpty(productList))
//        {
//            CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据流水号["+input.getString("OPR_NUM")+"]查询产品类型不支持");
//        }
//        
        
        IDataset selectedElements = new DatasetList();  
        
        for (Iterator iterator = productList.iterator(); iterator.hasNext();)
        {
            IData item = (IData) iterator.next();
            
            IData elementInput = new DataMap(); 
            elementInput.put("SERIAL_NUMBER", serialNumber);
            elementInput.put("MODIFY_TAG", "1"); 
            
            IData inparam = new DataMap();
            inparam.put("EPARCHY_CODE", userInfo.getString("EPARCHY_CODE"));
            inparam.put("CTRM_PRODUCT_ID", item.getString("PRODUCT_ID"));
            
            IDataset relationInfo = QueryListInfo.queryListInfoForRelation(inparam);
            
            if (IDataUtil.isEmpty(relationInfo))
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据能力平台产品编码["+item.getString("PRODUCT_ID")+"]查询产品映射关系不存在");
            }
            
            for (Iterator iterator2 = relationInfo.iterator(); iterator2.hasNext();)
            {
                IData relationItem = (IData) iterator2.next();
                
                selectedElements.addAll(PersonUtil.buildChangeProdOrder(elementInput, relationItem.getString("ELEMENT_ID"), relationItem.getString("ELEMENT_TYPE_CODE")));
                
            } 
        } 
        IData inParam = new DataMap();
        inParam.put("SELECTED_ELEMENTS", selectedElements);
        inParam.put("SERIAL_NUMBER", serialNumber);
        inParam.put("TRADE_TYPE_CODE", "110");

        IDataset idsRet = CSAppCall.call("SS.ChangeProductRegSVC.tradeReg", inParam); 
        
        return idsRet;
    } 

    private void updateInfo(IData inData) throws Exception
    {

        DBConnection conn = new DBConnection("cen1", true, false);
        try
        {
            inData.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
            inData.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
            
            SQLParser parser = new SQLParser(inData);
            parser.addSQL(" UPDATE TF_B_CTRM_RETURN SET ");
            parser.addSQL(" STATUS      = :STATUS, ");
            parser.addSQL(" TRADE_ID   = :TRADE_ID, ");
            parser.addSQL(" UPDATE_STAFF_ID   = :UPDATE_STAFF_ID, ");
            parser.addSQL(" UPDATE_DEPART_ID   = :UPDATE_DEPART_ID, ");
            parser.addSQL(" IS_SYNC   = :IS_SYNC, ");
            parser.addSQL(" RESULT_INFO   = :RESULT_INFO, ");
            parser.addSQL(" RESULT_CODE   = :RESULT_CODE, ");
            //parser.addSQL(" RESULT_DETAIL   = :RESULT_DETAIL, ");
            parser.addSQL(" REMARK   = :REMARK, "); 
            parser.addSQL(" UPDATE_TIME = SYSDATE ");
            parser.addSQL(" WHERE OPR_NUM = :OPR_NUM ");

            CrmDAO dao = DAOManager.createDAO(CrmDAO.class, Route.CONN_CRM_CEN);
            dao.executeUpdate(conn, parser.getSQL(), parser.getParam());

            conn.commit();
        }
        catch (Exception e)
        {
            conn.rollback();
            CSAppException.apperr(CrmCommException.CRM_COMM_103, e.getMessage());
        }
        finally
        {
            conn.close();
        }
    }

    public IData processOrder(IData input) throws Exception
    {

        StringBuilder selectSQL = new StringBuilder()
                .append(" SELECT ROW_.*,ROWID, ROWNUM ROWNUM_ FROM ")
                .append(" ( select SERVICE_NO SERIAL_NUMBER,OPR_NUM from TF_B_CTRM_RETURN where STATUS = '0' order by ACCEPT_DATE) ROW_  ")
                .append(" WHERE ROWNUM <= 10000 ");

        IData input1 = new DataMap();
        IDataset list = Dao.qryBySql(selectSQL, input1, Route.CONN_CRM_CEN);

        if (IDataUtil.isNotEmpty(list))
        {
            for (int i = 0; i < list.size(); i++)
            {
                IData item = list.getData(i);
                 
                IData resultItem = new DataMap();
                resultItem.put("RESULT_INFO", "处理成功");
                resultItem.put("RESULT_CODE", "0");
                resultItem.put("RESULT_DETAIL", "");
                
                IDataset idsRet = null;
                try
                {
                	idsRet = CSAppCall.call("SS.AbilityPlatReturnOrderExcSVC.tradeRegItem", item);
                } 
                catch (Exception e)
                {
                    resultItem.putAll(ExceptionUtils.getExceptionInfo(e)); 
                }

                IData inData = new DataMap();

                //更新下发表中执行的工单id   
                if (IDataUtil.isNotEmpty(idsRet))
                {
                    inData.put("TRADE_ID", idsRet.first().getString("TRADE_ID"));
                }  

                inData.putAll(resultItem);
                inData.put("STATUS", "0".equals(inData.getString("RESULT_CODE")) ? "RC" : "RR");
                inData.put("OPR_NUM", item.getString("OPR_NUM"));
                
                //需要同步订单状态给一级平台
                inData.put("IS_SYNC","0");

                updateInfo(inData);

            }
        }
        IData returnData = new DataMap();

        String rspCode = "0";
        String rspDesc = "调用成功！";
        returnData.put("RESULT_CODE", rspCode);
        returnData.put("RESULT_INFO", rspDesc);
        returnData.put("RSP_CODE", rspCode);
        returnData.put("RSP_DESC", rspDesc);
        return returnData;
    }
    
 
    public static IDataset getProductListByOprNum(String oprNum) throws Exception
    {
        IData params = new DataMap();
        params.put("OPR_NUM", oprNum);
        SQLParser parser = new SQLParser(params);
        
        parser.addSQL("select *  from  TF_B_CTRM_RETURN_PRODUCT where  OPR_NUM = :OPR_NUM ");  
        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
    }
    

    /**
     *宽带办理资格校验
     * @param Idata
     * @return
     * @throws Exception
     */
    public IData checkOrderBroadband(IData input) throws Exception
    {
        String serviceType = input.getString("SERVICE_TYPE");//01：手机号码 02：宽带号码
        String serviceNo = input.getString("SERVICE_NO");
        String checkType = input.getString("CHECK_TYPE");//1-新装预约；  2-续订；3-提速
        
        try
        {
            if ("1".equals(checkType))
            { 
                checkAddressId(input);
                if ("01".equals(serviceType))
                {
                    checkSerialNumber(input);
                    
                    IData inParam = new DataMap();
                    inParam.put("SERVICE_NO", "KD_"+serviceNo); 
                    checkKdSerialNumber(inParam ,Boolean.TRUE);    
                }
                else if ("02".equals(serviceType))
                { 
                    checkKdSerialNumber(input,Boolean.TRUE);    
                }
                else
                {
                    CSAppException.apperr(CrmCommException.CRM_COMM_103,  "不支持的业务号码类型"); 
                }

            }
            else if ("2".equals(checkType))
            {
                String sn = IDataUtil.chkParam(input, "SERVICE_NO"); 
 
                
                if ("01".equals(serviceType))
                {
                    checkSerialNumber(input);
                    
                    List xmListList = (List) input.get("PRODUCT_LIST");
                    JSONArray json = JSONArray.fromObject(xmListList);
                    DatasetList xmList = DatasetList.fromJSONArray(json);
                    input.remove("PRODUCT_LIST");
                    input.put("PRODUCT_LIST", xmList);
                    
                    IDataset productList = input.getDataset("PRODUCT_LIST");
                    
                    if (IDataUtil.isEmpty(productList))
                    {
                        return buildRetData("2999", "接口参数检查: 产品列表不能为空.");
                    }
                }
                else if ("02".equals(serviceType))
                { 
                    checkKdSerialNumber(input,Boolean.FALSE);    
                }
                else
                {
                    CSAppException.apperr(CrmCommException.CRM_COMM_103,  "不支持的业务号码类型"); 
                }
            }
            else if ("3".equals(checkType))
            {
                String sn = IDataUtil.chkParam(input, "SERVICE_NO");  
                
                if ("01".equals(serviceType))
                {
                    checkSerialNumber(input); 
                }
                else if ("02".equals(serviceType))
                { 
                    checkKdSerialNumber(input,Boolean.FALSE);    
                }
                else
                {
                    CSAppException.apperr(CrmCommException.CRM_COMM_103,  "不支持的业务号码类型"); 
                }
            }
            else 
            { 
                CSAppException.apperr(CrmCommException.CRM_COMM_103,  "不支持的校验类型"); 
                
            }

        }
        catch (Exception e)
        {
            IData exception = ExceptionUtils.getExceptionInfo(e);

            return buildRetData("2999", exception.getString("RESULT_INFO"));
        }

        return buildRetData("0000", "校验成功");
    }

    private void checkKdSerialNumber(IData input,Boolean exists) throws Exception
    {
        String serviceNo = IDataUtil.chkParam(input, "SERVICE_NO");
        
        if(!(StringUtils.isNotEmpty(serviceNo) && serviceNo.indexOf("KD_")>-1 && serviceNo.split("_")[1].length() == 11)) 
        { 
            CSAppException.apperr(CrmCommException.CRM_COMM_103,  "宽带号码"+serviceNo+"格式不正确");
        }
        
        String routeId = getRouteId(serviceNo);

        IData userInfo = UcaInfoQry.qryUserInfoBySn(serviceNo, routeId);

        if (exists)
        {
            if (IDataUtil.isNotEmpty(userInfo))
            { 
                CSAppException.apperr(CrmCommException.CRM_COMM_103,  "通过业务号码" + serviceNo + "查询宽带用户资料已经存在!");
            }  
        }
        else
        {
            if (IDataUtil.isEmpty(userInfo))
            { 
                CSAppException.apperr(CrmCommException.CRM_COMM_103,  "通过业务号码" + serviceNo + "查询宽带用户资料不存在!");
            } 
        }
       
    }

    private void checkSerialNumber(IData input) throws Exception
    {
        String sn = IDataUtil.chkParam(input, "SERVICE_NO"); 

        String routeId = getRouteId(sn);

        IData userInfo = UcaInfoQry.qryUserInfoBySn(sn, routeId); 

        if (IDataUtil.isEmpty(userInfo))
        { 
            CSAppException.apperr(CrmCommException.CRM_COMM_103,  "通过业务号码" + sn + "查询用户资料不存在!");
        }

        if (!"0".endsWith(userInfo.getString("USER_STATE_CODESET")))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103,  "号码" + sn + "用户状态不正常"); 
        }  
    }
    
    private void checkAddressId(IData input) throws Exception
    {
        String addressId = IDataUtil.chkParam(input, "ADDRESS_ID");
        //校验地址标识是否可安装
        
        IData inparams = new DataMap();
        inparams.put("REGION_SP", addressId);
        IDataset dataList = PBossCall.callPBOSS("PB.AddressManageSvc.queryAddressForHTML", inparams );
        IData result = new DataMap();
        if (IDataUtil.isNotEmpty(dataList))
        {
            result = dataList.first();
            if (StringUtils.equals("0", result.getString("PORT_NUM")))
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "该地址可用端口数为0，无法继续安装！");
            }    
        }
        else
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据传入的条件查询不到标准地址信息！");
        }
    }

    private String getRouteId(String sn) throws Exception
    {     
        String routeId = RouteInfoQry.getEparchyCodeBySnForCrm(sn);

        if (StringUtils.isBlank(routeId))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103,  "通过业务号码" + sn + "查询用户资料不存在!"); 
        }
        return routeId;
    }

    private IData buildRetData(String bizCode,String bizDesc) throws Exception
    {
        IData outData = new DataMap();
        outData.put("BIZ_CODE",bizCode);
        if(!"0000".equals(bizCode))
        {
            bizDesc = "校验失败:"+bizDesc;
        }
        outData.put("BIZ_DESC",bizDesc);
        outData.put("RESULTE_TIME", SysDateMgr.decodeTimestamp(SysDateMgr.getSysTime(), SysDateMgr.PATTERN_STAND_SHORT));
        
        return outData; 
    }

    /**
     * 综合退订接口
     * @param Idata
     * @return
     * @throws Exception
     */
    public IData returnOrderInfo(IData input) throws Exception
    {
        IData returnData = new DataMap();
        returnData.put("RESULTE_TIME", SysDateMgr.decodeTimestamp(SysDateMgr.getSysTime(), SysDateMgr.PATTERN_STAND_SHORT));
        returnData.put("BIZ_CODE", "0000");
        returnData.put("BIZ_DESC", "综合退订成功");

        boolean flag = false;
        DBConnection conn = new DBConnection("cen1", true, false);

        try
        {
            input.put("OPR_NUM", SysDateMgr.getSysDate("yyyyMMddhh24mmss") + UUID.randomUUID().toString().toUpperCase().replace("-", "").substring(0, 16));
            input.put("ACCEPT_MONTH", SysDateMgr.getCurMonth());
            input.put("ACCEPT_DATE", SysDateMgr.getSysTime());
            input.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
            input.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
            input.put("UPDATE_TIME", SysDateMgr.getSysTime());
            input.put("STATUS", "0");
            input.put("IS_SYNC", "2");
            checkPramByKeys(input, "RETURN_ID,RETURN_TYPE,EXT_RETURN_ID,ORDER_ID,SUB_ORDER_ID,CHANNEL_ID,BUYER_RETURN_TIME"
                                 + ",EXT_CHANNEL_RETURN_TIME,SERVICE_NO_TYPE,SERVICE_NO,TOTAL_FEE,CONTACT_PHONE");


            /*
            * 集团权益配合改造--综合退订请求 【CIP00063】  add by zhengkai5
            * 退订类型为权益商品时，调用权益接口
            *   1：普通退订；
            *   2：售后维权退订；
            *   3：退定号卡；
            *   4：权益商品退订；
            *   5：权益商品退费；
            * */
            String returnType = input.getString("RETURN_TYPE");
            if("4".equals(returnType) || "5".equals(returnType))
            {
                IData rightsParam = new DataMap();
                rightsParam.put("orderId",input.getString("ORDER_ID"));  // 订单标识
                rightsParam.put("subOrderId",input.getString("SUB_ORDER_ID"));  // 原子订单编码
                //rightsParam.put("orderSource",input.getString(""));
                rightsParam.put("returnId",input.getString("RETURN_ID"));  // 退单编号
               // rightsParam.put("statusDesc",input.getString(""));
                IData rigthsResult = BusinessAbilityCall.callBusinessCenterCommon("HAIN_UNHT_QYgroupReturnOrder",rightsParam);
                if(!"0000".equals(rigthsResult.getString("respCode")))
                {
                    returnData.put("BIZ_CODE", rigthsResult.getString("respCode"));
                    returnData.put("BIZ_DESC", rigthsResult.getString("respDesc"));
                    return returnData;
                }
            }


            List xmListList = (List) input.get("RETURN_GOODS_INFO");
        	JSONArray json = JSONArray.fromObject(xmListList);
        	DatasetList xmList = DatasetList.fromJSONArray(json);
        	input.remove("RETURN_GOODS_INFO");
        	input.put("RETURN_GOODS_INFO", xmList);
            
            IDataset goodsList = input.getDataset("RETURN_GOODS_INFO"); 
            if (IDataUtil.isNotEmpty(goodsList))
            {
                for (int i = 0; i < goodsList.size(); i++)
                {
                    IData goodsItem = goodsList.getData(i);

                    IDataset relaProducts = AbilityOpenPlatQry.queryListInfo("0898", goodsItem.getString("GOODS_ID",""));
                    if(IDataUtil.isNotEmpty(relaProducts))
                    {
                        if("1".equals(relaProducts.getData(0).getString("RSRV_TAG1")))
                        {
                            flag = true;
                        }
                    }

                    copyCommonDatas(input, goodsItem);
                    goodsItem.put("REMARK", "综合退订商品信息");
                  
                    checkPramByKeys(goodsItem, "GOODS_ID,GOODS_TITLE,RETURN_QUANTITY,PRICE");
                    Dao.insert("TF_B_CTRM_RETURN_GOODS", goodsItem, Route.CONN_CRM_CEN);

                    IDataset productList = goodsItem.getDataset("PRODUCT_LIST");

                    if (IDataUtil.isNotEmpty(productList))
                    {
                        for (int j = 0; j < productList.size(); j++)
                        {
                            IData productItem = productList.getData(j);
                          
                            copyCommonDatas(goodsItem, productItem, "GOODS_ID");
                            productItem.put("REMARK", "能力平台调用，子订单信息同步");
                          
                            checkPramByKeys(productItem, "PRODUCT_ID,PRODUCT_TYPE");

//                            if (!"10100".equals(productItem.getString("PRODUCT_TYPE")))
//                            { 
//                                CSAppException.apperr(CrmCommException.CRM_COMM_103,"接口参数检查: 输入参数[PRODUCT_TYPE=" + productItem.getString("PRODUCT_TYPE")
//                                                      + "]不支持该产品类型的退订"); 
//                            }

                            Dao.insert("TF_B_CTRM_RETURN_PRODUCT", productItem, Route.CONN_CRM_CEN);
                        }
                    }
                }
            }
            else
            { 
                CSAppException.apperr(CrmCommException.CRM_COMM_103,"接口参数检查: 输入参数[RETURN_GOODS_INFO]不存在或者参数值为空");
             
            }
            if(flag)
            {
                input.put("IS_SYNC", "0");
            }
            Dao.insert("TF_B_CTRM_RETURN", input, Route.CONN_CRM_CEN);
            conn.commit();
        }
        catch (Exception e)
        {
            conn.rollback();
            IData exception = ExceptionUtils.getExceptionInfo(e); 
            returnData.putAll(buildRetData("2999", exception.getString("RESULT_INFO")));
        }
        finally
        {
            conn.close();
        } 
        return returnData;
    }

    private void checkPramByKeys(IData data, String keyNamesStr) throws Exception
    {
        String keyNames[] = keyNamesStr.split(",");
        for (String strColName : keyNames)
        {
            IDataUtil.chkParam(data, strColName);
        }

    }

    private void transDataByName(IData input, IData dest, String name)
    {
        if (StringUtils.isNotBlank(input.getString(name, "")))
        {
            dest.put(name, input.getString(name));
        }

    }

    private void copyCommonDatas(IData input, IData dest, String... extNames)
    {
        String datas[] = {"OPR_NUM", "ACCEPT_MONTH", "UPDATE_DEPART_ID", "UPDATE_STAFF_ID", "UPDATE_TIME",
                          "ACCEPT_DATE", "RETURN_ID"};
        for (int i = 0; i < datas.length; i++)
        {
            String string = datas[i];
            transDataByName(input, dest, string);
        }

        for (int i = 0; i < extNames.length; i++)
        {
            String string = extNames[i];
            transDataByName(input, dest, string);
        }

        dest.put("ID", UUID.randomUUID().toString().toUpperCase().replace("-", "").substring(0, 30));

    }
}

