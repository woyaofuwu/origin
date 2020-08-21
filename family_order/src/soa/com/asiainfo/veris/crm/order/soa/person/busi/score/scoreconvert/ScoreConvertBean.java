
package com.asiainfo.veris.crm.order.soa.person.busi.score.scoreconvert;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.CustException;
import com.asiainfo.veris.crm.order.pub.exception.IBossException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UBrandInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustVipInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.UpmsQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserUpmsOrderQry;

public class ScoreConvertBean extends CSBizBean
{
    public static final String tagComplete = "01";// 交易处理完成

    public static final String tagFail = "02";// 交易处理超时

    public static final String tagError = "03";// 交易处理废止

    /**
     * 调用IBOSS接口撤销订单
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IData cancelConvertGiftOrder(IData data) throws Exception
    {
        IData resultMap = new DataMap();
        String resultCode = "";
        String resultMessage = "";
        String orderSubId = data.getString("ORDER_SUB_ID", "");
        String procState = data.getString("PROC_STATE");
        String orderId = data.getString("ORDER_ID");

        // 先判断子订单号是否存在，如不存在，则先调用普通查询接口获取子订单号
        if ("".equals(orderSubId) && tagComplete.equals(procState))
        {
            IData returnData = IBossCall.queryIboss(orderId);

            String returnCode = "";
            String returnMessage = "";
            String state = "";
            if (IDataUtil.isNotEmpty(returnData))
            {
                returnCode = returnData.getString("TRADE_RES_CODE", "");

                if (tagComplete.equals(returnCode))// 普通订单查询返回成功
                {
                    returnMessage = returnData.getString("TRADE_RES_DESC", "");
                    state = ((IDataset) returnData.get("ITEM_INFO")).get(4).toString();
                    state = state.substring(2, state.length() - 2);// 子订单状态
                    orderSubId = ((IDataset) returnData.get("ITEM_INFO")).get(0).toString();// 子订单id
                    orderSubId = orderSubId.substring(2, orderSubId.length() - 2);
                    data.put("ORDER_SEQ", data.getString("ORDER_SEQ"));
                    data.put("PROC_STATE", tagComplete);
                    data.put("ORDER_STATE", state);
                    data.put("ORDER_SUB_ID", orderSubId);
                    updateUpmsOrderState(data);
                }
                else
                {
                    CSAppException.apperr(IBossException.CRM_IBOSS_7, returnMessage);
                }
            }
            else
            {
                CSAppException.apperr(IBossException.CRM_IBOSS_8);
            }
        }

        IData resultData = IBossCall.cancelIboss(orderSubId);

        if (IDataUtil.isNotEmpty(resultData))
        {
            resultCode = resultData.getString("TRADE_RES_CODE", "");
            resultMessage = resultData.getString("TRADE_RES_DESC", "订单撤销失败！");

            if (tagComplete.equals(resultCode))// 订单撤销成功
            {
                data.put("ORDER_SEQ", data.getString("ORDER_SEQ"));
                data.put("PROC_STATE", tagComplete);// 此笔交易处理完成
                data.put("ORDER_STATE", resultData.getString("SUB_ORDER_STATUS"));// 2-订单已撤销
                updateUpmsOrderState(data);
            }
            else
            {
                CSAppException.apperr(IBossException.CRM_IBOSS_9, resultMessage);
            }
        }
        else
        {
            CSAppException.apperr(IBossException.CRM_IBOSS_10);
        }
        resultMap.put("MESSAGE", resultMessage);
        return resultMap;
    }

    /**
     * 获取子业务信息
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IData getCommInfo(IData inData) throws Exception
    {

        IData data = new DataMap();
        IData vipInfo = new DataMap();
        // 查用户积分
        IData param = new DataMap();
        param.put("BRAND_CODE", inData.getString("BRAND_CODE"));
        IDataset scoreInfo = AcctCall.queryUserScore(inData.getString("USER_ID"));
        if (IDataUtil.isEmpty(scoreInfo))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_6);
        }
        String score = scoreInfo.getData(0).getString("SUM_SCORE"); // 用户可兑换积分
        data.put("SCORE", score);
        data.put("ITEM_POINT1", "0");// 设置积分查询范围默认值
        data.put("ITEM_POINT2", "10000");

        String tag = getScoreByBrandCode(inData.getString("BRAND_CODE"));
        data.put("EXCHANGE_TAG", tag);

        vipInfo = queryVipClass(param); // 查客户级别

        data.put("CLASS_ID", vipInfo.getString("PARA_CODE2"));
        data.put("CLASS_NAME", vipInfo.getString("PARA_CODE3"));

        // 初始化省份信息
        IDataset provinceList = null;
        try
        {
            provinceList = UpmsQry.getIBOSSProvince();
        }
        catch (Exception e)
        {
            CSAppException.apperr(CrmUserException.CRM_USER_1074);
        }
        data.put("PROVINCE_LIST", provinceList);

        //获取品牌编码，调用产商品接口,本次改造duhj 2017/03/08
        String brandName=UBrandInfoQry.getBrandNameByBrandCode(inData.getString("BRAND_CODE"));
        data.put("BRAND_NAME", brandName);

        return data;
    }

    /**
     * 获取IBOSS的配送市信息
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset getIBOSSCity(IData data) throws Exception
    {
        IDataset dataset = UpmsQry.getIBOSSCity(data);

        return dataset;
    }

    /**
     * 获取IBOSS的配送区信息
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset getIBOSSDistrict(IData data) throws Exception
    {
        IDataset dataset = UpmsQry.getIBOSSDistrict(data);

        return dataset;
    }

    /**
     * 根据BRAND_CODE判断用户是全球通积分还是动感地带M值
     * 
     * @param brandCode
     * @return
     * @throws Exception
     */
    public String getScoreByBrandCode(String brandCode) throws Exception
    {
        String tag = "";
        if ("G001".equals(brandCode))// 全球通
        {
            tag = "01";
        }
        else if ("G010".equals(brandCode))// 动感地带
        {
            tag = "03";
        }
        else
        // 其他品牌
        {
            // common.error("10002", "该用户产品不能进行积分兑换，用户品牌必须为全球通或动感地带！");
            tag = "02";
        }

        return tag;
    }

    // queryConvertGiftOrder
    public IDataset queryConvertGiftOrder(IData data) throws Exception
    {
        IData resultData = new DataMap();
        String state = "";
        String resultCode = "";
        String resultMessage = "";
        String orderSubId = data.getString("ORDER_SUB_ID", "");
        String procState = data.getString("PROC_STATE", "01");
        String orderId = data.getString("ORDER_ID", "");
        IData param = new DataMap();
        if (tagComplete.equals(procState))// 普通订单查询
        {
            param.put("KIND_ID", "BIP5A045_T5000045_0_0"); // 普通订单查询接口
            param.put("SUB_ORDER_ID", orderSubId);
            param.put("ORDER_ID", data.getString("ORDER_ID", orderId));
        }
        else if (tagFail.equals(procState))// 超时订单查询
        {
            // 由于可能没有在查询条件内输入手机号，必须先通过USER_ID查出SERIAL_NUMBER
            param.put("KIND_ID", "BIP5A059_T5000059_0_0"); // 超市查询接口
            param.put("PROVINCE", "898");
            param.put("ORD_REQ_SEQ", data.getString("ORDER_SEQ", ""));
            param.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
        }
        resultData = IBossCall.connetionIboss(param);
        if (IDataUtil.isNotEmpty(resultData))
        {
            resultCode = resultData.getString("TRADE_RES_CODE", "");
            resultMessage = resultData.getString("TRADE_RES_DESC", "查询订单失败！");

            if (tagComplete.equals(resultCode) && tagComplete.equals(procState))// 普通订单查询返回成功
            {
            	IDataset tradeResInfos = resultData.getDataset("TRADE_RES_INFO");//IDataUtil.getDatasetSpecl("TRADE_RES_INFO",resultData); 
            	IData tradeResInfo = tradeResInfos.getData(0);
            	IDataset packageList = tradeResInfo.getDataset("PACKAGE_LIST");//IDataUtil.getDatasetSpecl("PACKAGE_LIST",tradeResInfo);
            	IData pck = packageList.getData(0);
            	IDataset subOrderList = pck.getDataset("SUB_ORDER_LIST");//IDataUtil.getDatasetSpecl("SUB_ORDER_LIST",pck);
            	IData subOrder = subOrderList.getData(0);
            	String orderSubId1 = subOrder.getString("SUB_ORDER_ID");
            	data.put("ORDER_SUB_ID", orderSubId1);
                data.put("ORDER_SEQ", data.getString("ORDER_SEQ"));
                data.put("PROC_STATE", procState);
                updateUpmsOrderState(data);
            	
                /*state = ((IDataset) resultData.get("ITEM_INFO")).get(4).toString();
                state = state.substring(2, state.length() - 2);// 子订单状态

                if (!state.equals(data.getString("ORDER_STATE")))
                {// 状态发生变化时才更新crm侧订单表状态
                    data.put("ORDER_SEQ", data.getString("ORDER_SEQ"));
                    data.put("PROC_STATE", procState);
                    data.put("ORDER_STATE", state);
                    String orderSubId1 = ((IDataset) resultData.get("ITEM_INFO")).get(0).toString();// 子订单id
                    orderSubId1 = orderSubId1.substring(2, orderSubId1.length() - 2);
                    data.put("ORDER_SUB_ID", orderSubId1);
                    updateUpmsOrderState(data);
                }*/
            }
            else if (tagComplete.equals(resultCode) && tagFail.equals(procState))// 超时订单查询成功
            {
                data.put("ORDER_SEQ", data.getString("ORDER_SEQ"));
                data.put("ORDER_ID", resultData.getString("ORDER_ID"));
                data.put("PROC_STATE", tagComplete);
                updateUpmsOrderState(data);
            }
            else if (!tagComplete.equals(resultCode) && tagFail.equals(procState))// 超时订单查询返回失败，将该条记录废止
            {
                data.put("ORDER_SEQ", data.getString("ORDER_SEQ"));
                data.put("PROC_STATE", tagError);
                updateUpmsOrderState(data);
            }
            else if (!tagComplete.equals(resultCode))
            {
                CSAppException.apperr(IBossException.CRM_IBOSS_46, resultMessage);
            }
        }
        else
        {
            CSAppException.apperr(IBossException.CRM_IBOSS_47);
        }
        return IDataUtil.idToIds(resultData);
    }

    public IDataset queryConvertRecord(IData data, Pagination pagination) throws Exception
    {
        String serialNumber = data.getString("SERIAL_NUMBER");
        if (StringUtils.isNotBlank(serialNumber))
        {
            IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);

            if (IDataUtil.isNotEmpty(userInfo))
            {
                data.put("USER_ID", userInfo.getString("USER_ID"));
            }
            else
            {
                CSAppException.apperr(CrmUserException.CRM_USER_779, data.getString("SERIAL_NUMBER"));
            }
        }

        return UserUpmsOrderQry.queryUserUpmsOrderInfo(data, pagination);
    }

    public IData queryGiftCount(IData data) throws Exception
    {
        IDataset dataset = IBossCall.queryGiftCount(data.getString("ITEM_ID"), data.getString("DELIV_PROVINCE"), data.getString("CITY_ID"), data.getString("DISTRICT"));
        IData result = new DataMap();
        if (IDataUtil.isNotEmpty(dataset))
        {
            result = dataset.getData(0);
        }
        return result;
    }

    public IDataset queryGifts(IData data, Pagination pagination) throws Exception
    {
        IDataset dataset = new DatasetList();
        String itemOrder = data.getString("ITEM_ORDER");
        data.put("STATE", "0");
        data.put("ITEM_STATUS", "1");
        data.remove("BRAND_CODE");
        //data.put("LEVEL_TYPE", "01");// 01-用户级别 02-活动
        //data.put("LEVEL_TYPE_CODE", data.getString("CLASS_ID"));// 301全球通钻卡，302全球通金卡，303全球通银卡，000全球通普通（除钻金银外的其他客户级别），999动感地带。当分级类型为活动时，填活动ID
       // data.put("BRAND_CODE", data.getString("EXCHANGE_TAG"));// 01-全球通 03-动感地带
        data.put("SCORE_STATE", "0");

        if ("DESC".equals(itemOrder))
        {
            dataset = UpmsQry.queryUpmsGiftInfoDesc(data, pagination);
        }
        else if ("ASC".equals(itemOrder))
        {
            dataset = UpmsQry.queryUpmsGiftInfoAsc(data, pagination);
        }

        return dataset;
    }

    /**
     * 获取客户级别
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IData queryVipClass(IData data) throws Exception
    {
        String vipTypeCode = "";
        String vipClassId = "";
        IData param = new DataMap();
        String userId = data.getString("USER_ID");
        String brandCode = data.getString("BRAND_CODE");
        param.put("USER_ID", userId);

        IDataset vipInfo = CustVipInfoQry.qryVipInfoByUserId(userId);

        if (vipInfo.size() > 1)
        {
            CSAppException.apperr(CustException.CRM_CUST_46, userId);
        }
        if (IDataUtil.isNotEmpty(vipInfo))
        {
            IData temp = new DataMap();
            temp = vipInfo.getData(0);
            vipTypeCode = temp.getString("VIP_TYPE_CODE");
            vipClassId = temp.getString("VIP_CLASS_ID");
            IDataset commparaInfo = CommparaInfoQry.getCommParas("CSM", "8765", vipClassId, vipTypeCode, this.getTradeEparchyCode());// 8765各省自行定义
            if (IDataUtil.isNotEmpty(commparaInfo))
            {
                data = commparaInfo.getData(0);
            }
        }

        if (data.isEmpty())
        {
            if ("G001".equals(brandCode))
            {
                data.put("PARA_CODE2", "000");
                data.put("PARA_CODE3", "全球通普通客户");
            }
            else if ("G010".equals(brandCode))
            {
                data.put("PARA_CODE2", "999");
                data.put("PARA_CODE3", "动感地带客户");
            }
            else
            {
                // common.error("无法定位客户级别信息！");
                data.put("PARA_CODE2", "888");
                data.put("PARA_CODE3", "神州行客户");
            }
        }
        return data;
    }

    /**
     * 修改订单表状态
     * 
     * @param pd
     * @param data
     * @throws Exception
     */
    public void updateUpmsOrderState(IData data) throws Exception
    {
        data.put("UPDATE_TIME", SysDateMgr.getSysTime());
        data.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        data.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());

        Dao.save("TF_F_USER_UPMS_ORDER", data);
    }

}
