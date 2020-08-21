/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.valuecard;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import org.apache.log4j.Logger;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCardException;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.ParamException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UDepartInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UStaffInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.DevicePriceQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ParamInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ValueCardInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.valuecard.order.requestdata.ValueCardInfoReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.valuecard.order.requestdata.ValueCardReqData;

/**
 * @CREATED by gongp@2014-5-13 修改历史 Revision 2014-5-13 下午04:46:12
 */
public class ValueCardMgrBean extends CSBizBean
{

	static Logger logger=Logger.getLogger(ValueCardMgrBean.class);
	 
    public void addSaleStaffInfo(IDataset cardList) throws Exception
    {

        for (int i = 0, size = cardList.size(); i < size; i++)
        {
            IData temp = cardList.getData(i);
            temp.put("SALE_TIME", SysDateMgr.getSysTime());
            temp.put("SALE_STAFF_ID", getVisit().getStaffId());
            if ("0".equals(temp.getString("SALE_TAG")))
            {
                temp.put("SALE_TAG", "未销售");
            }
            else if ("1".equals(temp.getString("SALE_TAG")))
            {
                temp.put("SALE_TAG", "已销售");
            }
            else if ("2".equals(temp.getString("SALE_TAG")))
            {
                temp.put("SALE_TAG", "已赠送");
            }
            else
            {
                temp.put("SALE_TAG", "未知");
            }
        }
    }

    /***************************************************************************
     * 转换各种标识
     * 
     * @param args
     * @return
     */
    public IDataset changeTag(IDataset args) throws Exception
    {

        if (IDataUtil.isNotEmpty(args))
        {
            for (int i = 0, size = args.size(); i < size; i++)
            {
                IData arg = args.getData(i);
                if ("0".equals(arg.getString("SALE_TAG")))
                {
                    arg.put("SALE_TAG", "未销售");
                }
                else if ((arg.getString("SALE_TAG")).equals("1"))
                {
                    arg.put("SALE_TAG", "已销售");
                }
                else if ((arg.getString("SALE_TAG")).equals("2"))
                {
                    arg.put("SALE_TAG", "已赠送");
                }
                else
                {
                    arg.put("SALE_TAG", "未知");
                }

                arg.put("X_STAFF_NAME_IN", UStaffInfoQry.getStaffNameByStaffId(arg.getString("STAFF_ID_IN")));
                arg.put("X_SALE_STAFF_NAME", UStaffInfoQry.getStaffNameByStaffId(arg.getString("SALE_STAFF_ID")));
                arg.put("X_STOCK_NAME", UDepartInfoQry.getDepartNameByDepartId(arg.getString("STOCK_ID")));
                arg.put("X_STATE_NAME", StaticUtil.getStaticValueDataSource(getVisit(),Route.CONN_RES,"RES_STATE_DEF", new String[]
                { "STATE_CODE", "RES_TYPE_ID","TABLE_COL" }, "STATE_NAME", new String[]
                { arg.getString("CARD_STATE_CODE"), "3","RES_STATE" }));
                arg.put("X_FACTORY_NAME", StaticUtil.getStaticValueDataSource(getVisit(),Route.CONN_RES,"RES_SUPPLIER", new String[]
                { "SUPPLIER_NO", "SUPPLIER_TYPE_ID" }, "SUPPLIER_NAME", new String[]
                { arg.getString("FACTORY_CODE"), "3" }));
            }
        }
        return args;
    }

    /**
     * 获取的卡号是否连续
     * 
     * @param data
     * @throws Exception
     */
    public void checkISConnect(IData data, IDataset cardList) throws Exception
    {
        String start = data.getString("START_CARD_NO");
        String end = data.getString("END_CARD_NO");
        int start4 = Integer.parseInt(start.substring(start.length() - 4, start.length()));
        int end4 = Integer.parseInt(end.substring(end.length() - 4, end.length()));
        String card = null;
        String temp = null;
        boolean isExist = false;
        for (int i = start4; i <= end4; i++)
        {
            isExist = false;
            temp = "0000" + i;
            card = start.substring(0, start.length() - 4) + temp.substring(temp.length() - 4, temp.length());
            for (int j = 0; j < cardList.size(); j++)
            {
                if (card.equals(cardList.get(j, "PAYCARD_NO")))
                {
                    isExist = true;
                    break;
                }
            }
            if (!isExist)
            {
                CSAppException.apperr(ParamException.CRM_PARAM_53, card);
            }
        }
    }

    /**
     * 规则校验：只能是同类型、同面值的卡
     * 
     * @param dataset1
     * @throws Exception
     */
    public void checkRule(IDataset dataset) throws Exception
    {

        String kindCode = dataset.getData(0).getString("RES_KIND_ID");
        String ADVISE_PRICE = dataset.getData(0).getString("ADVISE_PRICE");
        String cardKindCode = dataset.getData(0).getString("CARD_KIND_CODE") == null ? "" : dataset.getData(0).getString("CARD_KIND_CODE");
        String activeFlag = dataset.getData(0).getString("ACTIVE_FLAG");
        for (int i = 0; i < dataset.size(); i++)
        {
            IData temp = dataset.getData(i);
            if (!kindCode.equals(temp.getString("RES_KIND_ID")))
            {
                CSAppException.apperr(CrmCardException.CRM_CARD_210, temp.getString("PAYCARD_NO"));
            }
            if("31m".equals(temp.getString("RES_KIND_ID").substring(0,3))){
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "刮刮卡不能在此办理!");
            }
            if (!ADVISE_PRICE.equals(temp.getString("ADVISE_PRICE")))
            {
                CSAppException.apperr(CrmCardException.CRM_CARD_212, temp.getString("PAYCARD_NO"));
            }
            /*if (null != temp.getString("CARD_KIND_CODE"))
            {
                if (!cardKindCode.equals(temp.getString("CARD_KIND_CODE")))
                {
                    CSAppException.apperr(CrmCardException.CRM_CARD_215, temp.getString("PAYCARD_NO"));
                }
            }*/
            if (StringUtils.isBlank(activeFlag) || StringUtils.isBlank(temp.getString("ACTIVE_FLAG")))
            {
                CSAppException.apperr(CrmCardException.CRM_CARD_206);
            }
            if (!activeFlag.equals(temp.getString("ACTIVE_FLAG")))
            {
                CSAppException.apperr(CrmCardException.CRM_CARD_273);
            }

        }
    }

    /**
     * 新增记录
     * 
     * @param param
     * @throws Exception
     */
    public int createCanGiveValueCardInfo(IData param) throws Exception
    {

        String sql = " INSERT INTO tf_m_valuecard_sum_approval (AREA_CODE,STAFF_ID,TOTAL_AMOUNT,RSRV_DATE1,COMMIT_TIME,RSRV_STR2,OPERA_STAFF_ID) " + "VALUES (:AREA_CODE,:STAFF_ID,:TOTAL_AMOUNT*100,to_date(:END_TIME,'yyyy-mm-dd hh24:mi:ss'),SYSDATE,:REMARKS,:OPERA_STAFF_ID) ";

        return Dao.executeUpdate(new StringBuilder(sql), param);
    }

    public IData createChangeTable2(IData data, IDataset oldCardList, IDataset newCardList) throws Exception
    {
        String tradeTypeCode = data.getString("TRADE_TYPE_CODE");
        IData tableInfo = new DataMap();
        tableInfo.put("RES_KIND_CODE", oldCardList.getData(0).getString("RES_KIND_CODE", ""));
        tableInfo.put("startCardNo", data.getString("START_CARD_NO", ""));
        tableInfo.put("endCardNo", data.getString("END_CARD_NO", ""));
        // tableInfo.put("VALUE_CODE", oldCardList.getData(0).getString("VALUE_CODE", ""));
        // 获取单价
        float oldCardPrice = Float.parseFloat(oldCardList.getData(0).getString("SALE_MONEY", "0")) / 100;

        tableInfo.put("devicePrice", String.valueOf(newCardList.getData(0).getString("ADVISE_PRICE", "")));
        tableInfo.put("singlePrice", String.valueOf(oldCardPrice));
        tableInfo.put("totalPrice", String.valueOf("0"));
        tableInfo.put("valueCode", oldCardList.getData(0).getString("VALUE_CODE"));
        tableInfo.put("advise_price", oldCardList.getData(0).getString("ADVISE_PRICE", ""));// 原价
        tableInfo.put("rowCount", oldCardList.size());

        IData activateInfo = new DataMap();

        activateInfo.put("OLD_VALUE_CARD_NO", oldCardList.get(0, "VALUE_CARD_NO"));
        activateInfo.put("OLD_ACTIVE_FLAG", oldCardList.get(0, "ACTIVE_FLAG"));

        activateInfo.put("VALUE_CARD_NO", newCardList.get(0, "VALUE_CARD_NO"));
        activateInfo.put("ACTIVE_FLAG", newCardList.get(0, "ACTIVE_FLAG"));

        tableInfo.put("activateInfo", activateInfo.toString());

        return tableInfo;
    }

    public IData createTable2(IData data, IDataset cardList) throws Exception
    {
        String tradeTypeCode = data.getString("TRADE_TYPE_CODE");
        IData tableInfo = new DataMap();
        tableInfo.put("RES_KIND_CODE", cardList.getData(0).getString("RES_KIND_CODE", ""));
        tableInfo.put("startCardNo", data.getString("START_CARD_NO", ""));
        tableInfo.put("endCardNo", data.getString("END_CARD_NO", ""));
        // tableInfo.put("VALUE_CODE", cardList.getData(0).getString("VALUE_CODE", ""));
        // 获取单价
        String realPrice = cardList.getData(0).getString("RSRV_NUM1");

        float singlePrice = 0;
        float oldCardPrice = 0;

        if ("416".equals(tradeTypeCode))// 430业务反正最后都是0，决定不查
        {
            String devicePrice = getDevicePrice(cardList.getData(0));
            singlePrice = getSinglePrice(devicePrice, data);
        }
        else if ("419".equals(tradeTypeCode))
        {
            singlePrice = Float.parseFloat(cardList.getData(0).getString("SALE_MONEY", ""));
        }
        else
        {
            singlePrice = Float.parseFloat(realPrice) / 100;
        }
        String cardKindCode = cardList.getData(0).getString("RES_TYPE_CODE", "");
        tableInfo.put("singlePrice", singlePrice / 100);// 单价(元)
        tableInfo.put("totalPrice", singlePrice * cardList.size() / 100);// 总价
        tableInfo.put("rowCount", cardList.size());

        if ("418".equals(tradeTypeCode) || "430".equals(tradeTypeCode))
        {
            tableInfo.put("singlePrice", String.valueOf("0"));
            tableInfo.put("totalPrice", String.valueOf("0"));
            /**
             * 20160622
             * 状态标志编码
             */
            tableInfo.put("statusCode", data.getString("statusCode", ""));
        }

        tableInfo.put("valueCode", cardList.getData(0).getString("VALUE_CODE", "")); // 原价格代码
        tableInfo.put("advise_price", cardList.getData(0).getString("ADVISE_PRICE", ""));// 原价
        tableInfo.put("devicePrice", cardList.getData(0).getString("ADVISE_PRICE", ""));// 原价
        tableInfo.put("cardType", cardKindCode.substring(cardKindCode.length() - 1));// 
        tableInfo.put("activeFlag", cardList.getData(0).getString("ACTIVE_FLAG", ""));
        /*
         * IData activateInfo = null; IDataset activateInfos = new DatasetList(); for (int i = 0; i < cardList.size();
         * i++) { activateInfo = new DataMap(); activateInfo.put("VALUE_CARD_NO", cardList.get(i, "VALUE_CARD_NO"));
         * activateInfo.put("ACTIVE_FLAG", cardList.get(i, "ACTIVE_FLAG")); activateInfos.add(activateInfo); }
         * tableInfo.put("activateInfo", activateInfos);
         */
        return tableInfo;
    }

    /**
     * 根据rowid删除记录
     * 
     * @param param
     * @throws Exception
     */
    public int deleteCanGiveValueCardInfo(IData param) throws Exception
    {

        String sql = "DELETE  FROM tf_m_valuecard_sum_approval WHERE ROWID=:ROW_ID ";

        return Dao.executeUpdate(new StringBuilder(sql), param);
    }

    public IData getCardInfoParam(String tradeTypeCode, IData param, int iTag) throws Exception
    {
        IData data = new DataMap();
        data.put("RES_TYPE_CODE", "3");

        data.put("STOCK_ID", getVisit().getDepartId());
        if ("416".equals(tradeTypeCode) || "418".equals(tradeTypeCode) || "419".equals(tradeTypeCode) || "424".equals(tradeTypeCode) || "430".equals(tradeTypeCode))
        {
            data.put("RES_NO_S", param.getString("START_CARD_NO"));
            data.put("RES_NO_E", param.getString("END_CARD_NO"));
        }
        else if ("420".equals(tradeTypeCode))
        {
            if (iTag == 1)
            {
                data.put("RES_NO_S", param.getString("END_CARD_NO"));
                data.put("RES_NO_E", param.getString("END_CARD_NO"));
            }
            else if (iTag == 2)
            {
                data.put("RES_NO_S", param.getString("START_CARD_NO"));
                data.put("RES_NO_E", param.getString("START_CARD_NO"));
            }
        }
        return data;
    }

    /**
     * 获取有价卡换卡卡信息
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IData getChangeValeCardInfo(IData data) throws Exception
    { // 416="有价卡销售" 418="有价卡赠送" 419="有价卡退卡" 420="有价卡换卡"
        String tradeTypeCode = data.getString("TRADE_TYPE_CODE");
        IDataset oldCardSet = new DatasetList();
        IDataset newCardSet = new DatasetList();

        // 原卡信息
        IData paramOld = getCardInfoParam(tradeTypeCode, data, 2);
        oldCardSet = ResCall.queryValueCardReturnInfoIntf(paramOld.getString("RES_NO_S", ""), paramOld.getString("RES_NO_S", ""), paramOld.getString("STOCK_ID", ""), paramOld.getString("RES_TYPE_CODE", ""), "0", null, "420");
        /**
         * REQ201611250011_关于2016年下半年有价卡换卡界面优化需求
         * @author zhuoyingzhi
         * 20161226
         */
         if("420".equals(tradeTypeCode)){
        	 //有价卡换卡
        	 if(IDataUtil.isNotEmpty(oldCardSet)){
        		 /**
        		  * REQ201702170009关于过期有价卡不能做换卡的优化需求
        		  * @author zhuoyingzhi
        		  * @date 20170314
        		  */
        		 //获取有效期(2011-08-31)
            	 String cardEndDate=oldCardSet.getData(0).getString("END_DATE", "");
            	 if(!"".equals(cardEndDate)&&cardEndDate !=null){
            		 if(isEndDate(cardEndDate,"")){
            			 //有效期已经过期
                    	 CSAppException.apperr(CrmCommException.CRM_COMM_103, "过期有价卡不能做换卡操作，只能后台修改有效期实现延期之后，方可充值。");
            		 }
            	 }
        		 /**
        		  * 通过vc平台获取有效期
        		  * 
        		  */
            	 //通过vc平台查询  有价卡信息
            	 IDataset vcCardInfo=ResCall.queryMPCardStatus(paramOld.getString("RES_NO_S", ""));
            	 if(IDataUtil.isNotEmpty(vcCardInfo)){
            		 //获取终止时间(20110831)
                	 String acntStop=vcCardInfo.getData(0).getString("ACNTSTOP", "");
                	 if(!"".equals(acntStop)&&acntStop !=null){
                		 if(isEndDate(acntStop,"VC")){
                			 //有效期已经过期(终止时间)
                        	 CSAppException.apperr(CrmCommException.CRM_COMM_103, "过期有价卡不能做换卡操作，只能后台修改有效期实现延期之后，方可充值。");
                		 }
                	 }
            	 }
            	 /*******************REQ201702170009关于过期有价卡不能做换卡的优化需求end*****************************/
            	 //获取原卡的销售时间
            	 String cardSaleTime=oldCardSet.getData(0).getString("SALE_TIME", "");
            	 if(!"".equals(cardSaleTime)&&cardSaleTime!=null){
            		 if(isThreeMonth(cardSaleTime)){
            			 //销售日期超过三个月
                         boolean isValueCradChangeRight = StaffPrivUtil.isFuncDataPriv(this.getVisit().getStaffId(), "isValueCradChangeRight");
                         if(!isValueCradChangeRight){
                        	 //无权限,提示拦截语句
                        	 CSAppException.apperr(CrmCommException.CRM_COMM_103, "这张卡销售超过三个月不能换卡");
                         }
            		 }
            	 }
        	 }
         }
        /****************************************************************/
         
        String vcpanduan = oldCardSet.getData(0).getString("RES_TYPE_CODE", "00");
        if(vcpanduan.substring(0,2).equals("30"))
        {
	        IDataset VCInfo = ResCall.queryValueCardReturnVcInfo(paramOld.getString("RES_NO_S", ""), paramOld.getString("RES_NO_S", ""), paramOld.getString("STOCK_ID", ""), paramOld.getString("RES_TYPE_CODE", ""));
	        String crdflg = VCInfo.getData(0).getString("CARDFLAG", ""); // CRDFLG 充值卡状态。非负整数，范围：0:有效 1:已使 2:清退 3:生成 4:封闭状态
	                                                                   // 5：过期
	         //CARDFLAG：有价卡状态标识0:有效（激活） 1:无效（已使用）2: 清退 3:生成（新入库） 4:封闭状态（已锁定）
	        if (!"0".equals(crdflg))
	        {
	            StringBuilder sb = new StringBuilder("");
	            sb.append("有价卡[").append(paramOld.getString("RES_NO_S", "")).append("]状态不满足条件，不能办理换卡！").append("[状态：").append(VCInfo.getData(0).getString("CRDFLG_T", crdflg)).append("]"); // TODO
	                                                                                                                                                                                        // 正式环境放开
	            CSAppException.apperr(CrmCommException.CRM_COMM_103, sb.toString());
	         }
        }
        
        //oldCardSet = ResCall.queryValueCardReturnInfoIntf(paramOld.getString("RES_NO_S", ""), paramOld.getString("RES_NO_S", ""), paramOld.getString("STOCK_ID", ""), paramOld.getString("RES_TYPE_CODE", ""), "0", null, "420");

        if (!oldCardSet.getData(0).getString("X_RESULTCODE", "0").equals("0") || Integer.parseInt(oldCardSet.getData(0).getString("X_RECORDNUM", "0")) == 0)
        {
            CSAppException.apperr(CrmCardException.CRM_CARD_221);// 该有价卡的状态不符合换卡条件
        }
        
        if(oldCardSet.getData(0).getString("RES_TYPE_CODE", "0").equals("31e"))
        {
            CSAppException.appError("2015010918", "31e省内ip记账卡不支持换卡");
        }
        // 新卡信息
        IData paramNew = getCardInfoParam(tradeTypeCode, data, 1);
        newCardSet = ResCall.checkResourceForValueCard(paramNew.getString("RES_NO_E", ""), paramNew.getString("RES_NO_E", ""), paramNew.getString("STOCK_ID", ""), paramNew.getString("RES_TYPE_CODE", ""), "420");

        if (newCardSet == null || newCardSet.size() == 0 || "0".equals(newCardSet.get(0, "X_RECORDNUM")))
        {
            CSAppException.apperr(CrmCardException.CRM_CARD_107);
        }
        // 验证必须为同一类型、面值的卡
        if (!oldCardSet.get(0, "RES_KIND_CODE").equals(newCardSet.get(0, "RES_KIND_CODE")))
        {
            CSAppException.apperr(CrmCardException.CRM_CARD_203);
        }
        if("31m".equals(oldCardSet.getData(0).getString("RES_KIND_CODE").substring(0,3))
                ||"31m".equals(newCardSet.getData(0).getString("RES_KIND_CODE").substring(0,3))){
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "刮刮卡不能在此办理!");
        }
        if (!oldCardSet.get(0, "ADVISE_PRICE").equals(newCardSet.get(0, "ADVISE_PRICE")))
        {
            CSAppException.apperr(CrmCardException.CRM_CARD_205);
        }
        /*if (null != oldCardSet.get(0, "CARD_KIND_CODE") && !"".equals(oldCardSet.get(0, "CARD_KIND_CODE")) && null != newCardSet.get(0, "CARD_KIND_CODE") && !"".equals(newCardSet.get(0, "CARD_KIND_CODE")))
        {
            if (!oldCardSet.get(0, "CARD_KIND_CODE").equals(newCardSet.get(0, "CARD_KIND_CODE")))
            {
                CSAppException.apperr(CrmCardException.CRM_CARD_214);// 原卡与新卡的子类型不同！
            }
        }*/

        IData table2 = createChangeTable2(data, oldCardSet, newCardSet);
        // 组织结果集
        IData result = new DataMap();
        IDataset tableSet = new DatasetList();
        tableSet.add(table2);
        result.put("TABLE2", tableSet);
        return result;

    }

    /**
     * @param cardSet
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-7-4
     */
    public String getDevicePrice(IData cardSet) throws Exception
    {
        String valueCode = cardSet.getString("VALUE_CODE");
        
        if(StringUtils.isBlank(valueCode)){
            valueCode = cardSet.getString("RES_KIND_CODE").substring(cardSet.getString("RES_KIND_CODE").length()-1);
        }

        IDataset devicePrice = DevicePriceQry.getDevicePrices(cardSet.getString("RES_TYPE_CODE"), cardSet.getString("RES_KIND_CODE"), "-1", valueCode, "416", "-1", "Z", "0898");

        String price = "0";

        if (IDataUtil.isNotEmpty(devicePrice))
        {
            for (int i = 0; i < devicePrice.size(); i++)
            {
                IData temp = devicePrice.getData(i);
                if ((null != cardSet.getString("CARD_KIND_CODE")) && (!"".equals(cardSet.getString("CARD_KIND_CODE"))) && (cardSet.getString("CARD_KIND_CODE").equals(devicePrice.get(i, "CARD_KIND_CODE"))))
                {
                    price = temp.getString("DEVICE_PRICE");
                    return price;
                }
                else if (null == temp.getString("CARD_KIND_CODE") || "-1".equals(temp.getString("CARD_KIND_CODE")))
                {
                    price = temp.getString("DEVICE_PRICE");
                }
            }
        }
        else
        {
            // common.error("没有获取到有效的有价卡售价参数，该段卡无法销售！");
            CSAppException.apperr(CrmCardException.CRM_CARD_116);
        }

        return price;
    }

    /**
     * 获取赠送总额审批工单及工单余额
     */
    public IDataset getgetWorkOrders() throws Exception
    {
        IDataset returnValue = new DatasetList();

        String cityId = getVisit().getCityCode();

        IDataset dataset = ValueCardInfoQry.getgetWorkOrders(cityId);

        for (Iterator iterator = dataset.iterator(); iterator.hasNext();)
        {

            IData map = (IData) iterator.next();
            String staff_id = map.getString("STAFF_ID");
            String totalStr = map.getString("TOTAL_AMOUNT");
            Double total = Double.parseDouble(totalStr);

            IDataset infos = ValueCardInfoQry.getStaffValueCardAudit(staff_id);

            IData info = infos.getData(0);
            String fee = info.getString("FEE", "");
            if ("".equals(fee))
            {
                map.put("BALANCE", total + "");

                returnValue.add(map);
            }
            else
            {
                Double feeInt = Double.parseDouble(fee);
                if (feeInt < total)
                {

                    map.put("BALANCE", "" + (total - feeInt));

                    returnValue.add(map);
                }
            }
        }

        return returnValue;
    }

    public IData getResInfoForImport(IData param) throws Exception
    {
        String cardList = param.getString("CARD_LIST");
        String[] arr = cardList.split(",");

        IDataset dataset = ParamInfoQry.getTagInfoBySubSys("CS_VIEWCON_VALUECARDCANCEL", "CSM", "0", "0898");

        if (IDataUtil.isNotEmpty(dataset))
        {
            int TAG_NUMBER = dataset.getData(0).getInt("TAG_NUMBER", 300);
            long count = arr.length;
            if (count > TAG_NUMBER)
            {
                // common.error("数量过大将导致超时，请缩小数据段在【"+TAG_NUMBER+"】内！");
                CSAppException.apperr(CrmCardException.CRM_CARD_262, TAG_NUMBER);
            }
        }

        if (arr.length <= 1)
        {
            // common.error("导入查询失败！");
            CSAppException.apperr(CrmCardException.CRM_CARD_272);
        }
        IDataset resDataset = new DatasetList();

        for (int i = 0; i < arr.length; i++)
        {
            IData temp = new DataMap();
            temp.put("VALUE_CARD_NO", arr[i]);
            resDataset.add(temp);
        }

        IDataset dataset1 = ResCall.queryValueCardReturnInfoIntf("1", "1", getVisit().getDepartId(), "3", "1", resDataset, "424");

        if (IDataUtil.isEmpty(dataset1))
        {
            // common.error("548102:没有查询到卡信息！");
            CSAppException.apperr(CrmCardException.CRM_CARD_208);
        }
        float saleMoney = 0;

        for (int i = 0, size = dataset1.size(); i < size; i++)
        {
            IData temp = dataset1.getData(i);
            if ("3".equals(equals(temp.getString("FEE_TAG"))))
            {
                // common.error("548109","输入的卡号段中有销售的有价卡，不允许办理有价卡赠送返销！");
                CSAppException.apperr(CrmCardException.CRM_CARD_271);
            }
            saleMoney += Float.parseFloat(temp.getString("SALE_MONEY"));
        }

        checkRule(dataset1);

        // 组织结果集
        IData result = new DataMap();
        result.put("TABLE1", changeTag(dataset1));
        // result.put("TABLE2", dataset2);
        return result;

    }

    /**
     * 根据前台是否打折单选框获取单价
     * 
     * @param cardPrice
     * @return
     * @throws Exception
     */
    public Float getSinglePrice(String cardPrice, IData data) throws Exception
    {

        String radio = data.getString("baseinfo_radio", "");
        Float salePrice = null; // 销售价格
        if ("a".equals(radio))
        {
            salePrice = Float.parseFloat(cardPrice);
        }
        else
        {
            String discount = data.getString("DISCOUNT", "");
            String strPrice = data.getString("SALEPRICE", "");
            if (!"".equals(discount))
            {
                salePrice = Float.parseFloat(cardPrice) * Float.parseFloat(discount) / 10;
            }
            else if (!"".equals(strPrice))
            {
                salePrice = Float.parseFloat(strPrice) * 100;
            }
            else
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_152);
            }
        }
        return salePrice;
    }

    /**
     * 批量获取有价卡信息，包括有价卡销售，返销的数据
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IData getValeCardListInfo(IData data) throws Exception
    {// 416="有价卡销售" 418="有价卡赠送" 419="有价卡退卡" 420="有价卡换卡"
        String tradeTypeCode = data.getString("TRADE_TYPE_CODE");
        IDataset cardList = new DatasetList();// 资源接口获取的有价卡信息

        if ("418".equals(tradeTypeCode) || "424".equals(tradeTypeCode))
        {

            IDataset dataset = ParamInfoQry.getTagInfoBySubSys("CS_VIEWCON_VALUECARDGIVE", "CSM", "0", "0898");

            if ("424".equals(tradeTypeCode))
            {
                dataset = ParamInfoQry.getTagInfoBySubSys("CS_VIEWCON_VALUECARDCANCEL", "CSM", "0", "0898");
            }

            if (dataset != null && dataset.size() == 1)
            {
                int TAG_NUMBER = dataset.getData(0).getInt("TAG_NUMBER", 300);
                String startCardNo = data.getString("START_CARD_NO", "");
                String endCardNo = data.getString("END_CARD_NO", "");

                BigInteger end = new BigInteger(endCardNo);
                BigInteger start = new BigInteger(startCardNo);
                BigInteger one = new BigInteger("1");
                BigInteger num = end.subtract(start).add(one);
                int count = num.intValue();
                if (count > TAG_NUMBER)
                {
                    // common.error("数量过大将导致超时，请缩小数据段在【"+TAG_NUMBER+"】内！");
                    CSAppException.apperr(CrmCardException.CRM_CARD_262, TAG_NUMBER);
                }

            }

        }

        // 有价卡销售
        if ("416".equals(tradeTypeCode) || "418".equals(tradeTypeCode) || "430".equals(tradeTypeCode))
        {
            IData param = getCardInfoParam(tradeTypeCode, data, 0);
            cardList = ResCall.checkResourceForValueCard(param.getString("RES_NO_S", ""), param.getString("RES_NO_E", ""), param.getString("STOCK_ID", ""), param.getString("RES_TYPE_CODE", ""), tradeTypeCode);
        }
        // 有价卡退卡
        if ("419".equals(tradeTypeCode) || "424".equals(tradeTypeCode))
        {
            IData param = getCardInfoParam(tradeTypeCode, data, 0);
            cardList = ResCall.queryValueCardReturnInfoIntf(param.getString("RES_NO_S", ""), param.getString("RES_NO_E", ""), param.getString("STOCK_ID", ""), param.getString("RES_TYPE_CODE", ""), "0", null, tradeTypeCode);

            if ( !"424".equals(tradeTypeCode) && "1".equals(cardList.getData(0).getString("FEE_TAG")))
            {
                CSAppException.apperr(CrmCardException.CRM_CARD_138);
            }
        }

        if (IDataUtil.isEmpty(cardList) || "0".equals(cardList.get(0, "X_RECORDNUM")))
        {
            CSAppException.apperr(CrmCardException.CRM_CARD_208);
        }
        else
        {
            if ("424".equals(tradeTypeCode))
            {
                if ("3".equals(cardList.getData(0).getString("FEE_TAG")))
                {
                    CSAppException.apperr(CrmCardException.CRM_CARD_270);
                }
            }
        }
        
        /**
         * 20160617
         * 判断手机号码是否为省局:
         * 是,customerNumber 为可填
         * 否,customerNumber 为必填
         */
        String cityId = getVisit().getCityCode().toUpperCase();
        String staffId = getVisit().getStaffId();
        //客户号码
        String  customerNumber=data.getString("customerNumber");
        if("418".equals(tradeTypeCode)){
        	//有价卡赠送
            if (cityId.equals("HNSJ") || staffId.startsWith("HNSJ")){
            	//省局帐号
            }else{
            	//不是省局帐号
            	if("".equals(customerNumber)){
            		CSAppException.apperr(CrmCardException.CRM_CARD_275);
            	}
            }
        }
  
        // 检查有价卡段是否连续
        checkISConnect(data, cardList);
        // 有价卡退卡业务，校验该卡是否使用，调取VC平台
        // checkISUse(data, cardList);
        // 海南有价卡销售校验规则
        this.hainCheckRule(cardList, tradeTypeCode, data.getString("START_CARD_NO"), data.getString("END_CARD_NO"));
        // 验证必须为同一类型、同一面值的卡
        checkRule(cardList);
        // 添加销售员工，时间
        addSaleStaffInfo(cardList);
        

        // 构造table2 数据
        IData table2 = createTable2(data, cardList);

        // 组织结果集
        IData result = new DataMap();
        result.put("TABLE1", cardList);
        IDataset tableSet = new DatasetList();
        tableSet.add(table2);
        result.put("TABLE2", tableSet);
        return result;
    }

    public IData getValueCardInfo(IData data) throws Exception
    {

        String tradeTypeCode = data.getString("TRADE_TYPE_CODE");
        if (tradeTypeCode.equals("420"))
        {
            // 获取有价卡换卡的卡信息
            return getChangeValeCardInfo(data);
        }
        else
        {
            // 获取有价卡卡信息，销售返销。
            return getValeCardListInfo(data);
        }
    }

    public IData getVPMNGiveValueCardUserInfo(IData param) throws Exception
    {

        String sn = param.getString("SERIAL_NUMBER");

        String month = param.getString("MONTH");

        UcaData ucaData = UcaDataFactory.getNormalUca(sn);

        String userId = ucaData.getUserId();

        String user_brand_code = ucaData.getBrandCode();

        if (!"VPMN".equals(user_brand_code))
        {
            // common.error("该用户不是VPMN集团用户");
            CSAppException.apperr(CrmCardException.CRM_CARD_157);
        }

        IDataset dataset = UserOtherInfoQry.getUserOtherInfo(userId, "VCAD", userId.substring(userId.length() - 4, userId.length()), month);

        String vpmnfee = "0.0";
        if (IDataUtil.isNotEmpty(dataset))
        {
            IData temp = dataset.getData(0);
            String rsrvStr1 = temp.getString("RSRV_STR1");
            String rsrvStr2 = temp.getString("RSRV_STR2");

            if (StringUtils.isNotBlank(rsrvStr1) && StringUtils.isNotBlank(rsrvStr2))
            {
                vpmnfee = (Integer.parseInt(rsrvStr1) - Integer.parseInt(rsrvStr2)) + "";
            }
        }

        IData result = new DataMap();

        result.put("MONTH", month);
        result.put("VPMN_FEE", vpmnfee);

        if (ucaData.getCustomer() != null)
        {
            result.putAll(ucaData.getCustomer().toData());
        }

        if (ucaData.getCustPerson() != null)
        {
            result.putAll(ucaData.getCustPerson().toData());
        }

        return result;
    }

    /***************************************************************************
     * 海南特殊校验规则
     * 
     * @param args
     */
    private void hainCheckRule(IDataset args, String tradeTypeCode, String startNo, String endNo) throws Exception
    {
        if (null != args)
        {
            // 20120330
            BigInteger end = new BigInteger(endNo);
            BigInteger start = new BigInteger(startNo);
            BigInteger one = new BigInteger("1");
            BigInteger num = end.subtract(start).add(one);
            BigInteger size = BigInteger.valueOf(args.size());
            int count = num.intValue();
            if (args.size() < count)
            {
                StringBuilder result = new StringBuilder("");
                int hk = 0;
                for (int i = 0; i < count; i++)
                {
                    String tempId = (start.add(BigInteger.valueOf(i))) + "";
                    if (tempId.length() < startNo.length())
                    {
                        int t = startNo.length() - tempId.length();
                        for (int jj = 0; jj < t; jj++)
                        {
                            tempId = "0" + tempId;
                        }
                    }
                    for (int j = 0; j < args.size(); j++)
                    {
                        if ((tempId).equals(args.get(j, "VALUE_CARD_NO")))
                        {
                            break;
                        }
                        else
                        {
                            if (j == args.size() - 1)
                            {
                                if (hk != 0)
                                {
                                    result.append(",");
                                }
                                result.append(tempId);
                                hk = 1;
                            }
                        }
                    }
                }
                // common.error("输入卡段不是全满足操作条件["+result.toString()+"]"+"，请减少卡段范围分批销售！");
            }
            if ("416".equals(tradeTypeCode))
            {
                for (int i = 0; i < args.size(); i++)
                {
                    if ("f".equals(args.get(i, "RES_KIND_CODE")))
                    {
                        // common.error("不能销售话费卡！");
                        CSAppException.apperr(CrmCardException.CRM_CARD_220);
                    }
                }
            }
        }
    }

    /**
     * 修改记录
     * 
     * @param param
     * @throws Exception
     */
    public int updateCanGiveValueCardInfo(IData param) throws Exception
    {
    	
        String sql = " UPDATE tf_m_valuecard_sum_approval SET AREA_CODE=:AREA_CODE ,STAFF_ID=:STAFF_ID,TOTAL_AMOUNT=:TOTAL_AMOUNT*100,COMMIT_TIME=SYSDATE,RSRV_DATE1=to_date(:END_TIME,'yyyy-mm-dd hh24:mi:ss'),RSRV_STR2=:REMARKNEW,OPERA_STAFF_ID=:OPERA_STAFF_ID " + " where rowid=:ROW_ID ";
        return Dao.executeUpdate(new StringBuilder(sql), param);
    }

    /**
     * 修改记录
     * 
     * @param param
     * @throws Exception
     */
    public int updatePrintRemark(IData param) throws Exception
    {

        String sql = " update TF_B_TRADEFEE_DEVICE set rsrv_str2='PRINT' where trade_id = :TRADE_ID";

        return Dao.executeUpdate(new StringBuilder(sql), param);
    }
    /**
     * 20160618
     * 验证批量加入信息(只适合批量加入)
     * @param data
     * @return
     * @throws Exception
     */
    public IData checkValeCardInfo(IData data) throws Exception
    {
    	 // 418="有价卡赠送"
        String tradeTypeCode = data.getString("TRADE_TYPE_CODE");
        IDataset cardList = new DatasetList();// 资源接口获取的有价卡信息

        if ("418".equals(tradeTypeCode))
        {

            IDataset dataset = ParamInfoQry.getTagInfoBySubSys("CS_VIEWCON_VALUECARDGIVE", "CSM", "0", "0898");

            if (dataset != null && dataset.size() == 1)
            {
                int TAG_NUMBER = dataset.getData(0).getInt("TAG_NUMBER", 300);
                String startCardNo = data.getString("START_CARD_NO", "");
                String endCardNo = data.getString("END_CARD_NO", "");

                BigInteger end = new BigInteger(endCardNo);
                BigInteger start = new BigInteger(startCardNo);
                BigInteger one = new BigInteger("1");
                BigInteger num = end.subtract(start).add(one);
                int count = num.intValue();
                if (count > TAG_NUMBER)
                {
                    // common.error("数量过大将导致超时，请缩小数据段在【"+TAG_NUMBER+"】内！");
                    CSAppException.apperr(CrmCardException.CRM_CARD_262, TAG_NUMBER);
                }

            }
            /**
             * 20160617
             * 判断手机号码是否为省局:
             * 是,customerNumber 为可填
             * 否,customerNumber 为必填
             */
  		  //模版中的手机号码
  		  String tmpcustomerNo=data.getString("customerNo");
          String cityId = getVisit().getCityCode().toUpperCase();
          String staffId = getVisit().getStaffId();
	    	//有价卡赠送
	        if (cityId.equals("HNSJ") || staffId.startsWith("HNSJ")){
	        	//省局帐号
	        }else{
	        	//不是省局帐号
	        	if("".equals(tmpcustomerNo)){
	        		//客户号码:为必填项！
	        		CSAppException.apperr(CrmCardException.CRM_CARD_275);
	        	}
	        }

        }

        // 有价卡销售
        if ("418".equals(tradeTypeCode))
        {
            IData param = getCardInfoParam(tradeTypeCode, data, 0);
            cardList = ResCall.checkResourceForValueCard(param.getString("RES_NO_S", ""), param.getString("RES_NO_E", ""), param.getString("STOCK_ID", ""), param.getString("RES_TYPE_CODE", ""), tradeTypeCode);
        }

        if (IDataUtil.isEmpty(cardList) || "0".equals(cardList.get(0, "X_RECORDNUM")))
        {
            CSAppException.apperr(CrmCardException.CRM_CARD_208);
        }
        
  
        // 检查有价卡段是否连续
        checkISConnect(data, cardList);
        // 海南有价卡销售校验规则
        this.hainCheckRule(cardList, tradeTypeCode, data.getString("START_CARD_NO"), data.getString("END_CARD_NO"));
        // 验证必须为同一类型、同一面值的卡
        checkRule(cardList);
        // 添加销售员工，时间
        addSaleStaffInfo(cardList);
        

        // 构造table2 数据
        IData table2 = createTable2(data, cardList);

        // 组织结果集
        IData result = new DataMap();
        result.put("TABLE1", cardList);
        IDataset tableSet = new DatasetList();
        tableSet.add(table2);
        result.put("TABLE2", tableSet);
        return result;
    }
    
    /**
     * 
     * @param cardInfo
     * @param data
     * @param cardReqData
     * @throws Exception
     */
    public void  initValueCardDetailedInfo(BusiTradeData btd,ValueCardInfoReqData cardInfo,IData data,ValueCardReqData cardReqData,
    		  String startCardNo) throws Exception {
    	try {
    		 IDataset  cardList = ResCall.checkResourceForValueCard(cardInfo.getStartCardNo(), cardInfo.getEndCardNo(), this.getVisit().getDepartId(), "3", "418");
    		 if(IDataUtil.isNotEmpty(cardList)){
    			 IData card=new DataMap();
    			 card=cardList.getData(0);
        		 //台帐流水号
    	 		   data.put("TRADE_ID", btd.getTradeId());
    	 		   //卡类型
				   data.put("KIND_NAME", StaticUtil.getStaticValueDataSource(
						getVisit(), "res", "RES_SKU", "RES_SKU_ID",
						"RES_SKU_NAME", card.getString("RES_KIND_CODE")));
    	 		  
    	 		   //卡号
    	 		   data.put("CARD_NUMBER", startCardNo);
    	 		   //面值
    	 		   data.put("DEVICE_PRICE", (Long.valueOf(cardInfo.getDevicePrice())/100));
    	 		   //审批工单号
    	 		   data.put("AUDITORDERNUMBER", cardReqData.getAuditStaffId());
    	          
    	 		   //有效日期
    	           data.put("VALID_DATA", card.getString("END_DATE"));
    	 		   
    	 		   //销售标识
    	 		   data.put("SALE_TAG", "已销售");
    	 		   //归属库存位置
				   data.put("LOCATION", StaticUtil.getStaticValueDataSource(
						getVisit(), "res", "TD_M_DEPART", "DEPART_ID",
						"DEPART_NAME", card.getString("STOCK_ID")));
    	 		  //销售操作工号
    	 		  //data.put("TRADE_STAFF_ID", card.getString("SALE_STAFF_ID"));
				   //修改为当前用户姓名
    	 		  data.put("TRADE_STAFF_ID", this.getVisit().getStaffId());
    	 		 
    	 		   //销售操作日期(原来)
    	 		   data.put("ACCEPT_DATE",card.getString("SALE_TIME"));
    	 		   //销售金额
    	 		   data.put("SALE_PRICE", card.getString("SALE_MONEY"));

    	 		   //补录时间,在有价卡赠送时应该为空
    	 		   data.put("RECORD_TIME", "");
    	 		   //分公司
    	 		   data.put("CITY_CODE",  this.getVisit().getCityCode());
    	 		   //部门编码
    	 		   data.put("DEPART_CODE", this.getVisit().getDepartCode());
    	 		   //部门名称
    	 		   data.put("RSRV_STR6", this.getVisit().getDepartName());
    	 		   
    	 		   //更新时间
    	 		   data.put("UPDATE_TIME", cardInfo.getSaleTime());
    	 		   //
    	 		   data.put("STATE_NAME", "赠送");
    	 		   data.put("STATE_CODE", "0");
    	 		   if("true".equals(cardInfo.getImportFlag())){
    	 			   //批量导入
        	 		   //客户号码
        	 		   data.put("CUST_NUMBER", cardInfo.getCustomerNo());
        	 		   //客户姓名
        	 		   data.put("RSRV_STR2", cardInfo.getCustomerName());
        	 		   //对应的集团名称
        	 		   data.put("RSRV_STR3", cardInfo.getGroupName());
        	 		   //赠送人姓名
        	 		   data.put("RSRV_STR4", cardInfo.getGiveName());
    	 		   }else{
        	 		   //客户号码
        	 		   data.put("CUST_NUMBER", cardReqData.getCustomerNumber());
        	 		   //客户姓名
        	 		   data.put("RSRV_STR2", cardReqData.getCustomerName());
        	 		   //对应的集团名称
        	 		   data.put("RSRV_STR3", cardReqData.getGroupName());
        	 		   //赠送人姓名
        	 		   data.put("RSRV_STR4", cardReqData.getGiveName());
    	 		   }
    	 		   //录入时间(销售日期)
    	 		  data.put("RSRV_STR5", SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND));
    	 		  
    	 		  //业务类型
    	 		 data.put("TRADE_TYPE_CODE", "418");
    	 		 //受理月份
    	 		 data.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(btd.getTradeId()));
    	 		
    		 }

    		
		} catch (Exception e) {
			// TODO: handle exception
			if(logger.isInfoEnabled()) logger.info(e);
			throw e;
		}
    }
    
    /**
     * REQ201611250011_关于2016年下半年有价卡换卡界面优化需求
     * <br/>
     * 判断销售时间是否超过三个月(于天数为准)
     * @param saleTimePar
     * @return
     * @throws Exception
     * @author zhuoyingzhi
     */
    public boolean isThreeMonth(String saleTimeParam)throws Exception{
    	try {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			Calendar calendar=Calendar.getInstance();
			calendar.add(Calendar.MONTH, -3);
			String beforeThreeMonthStr = formatter.format(calendar.getTime());
			//系统日期三个月前日期
			Date threeMonth=formatter.parse(beforeThreeMonthStr);
			//销售时间
			Date saleTime=formatter.parse(saleTimeParam);
			if(saleTime.getTime() < threeMonth.getTime()){
				 //销售时间超过三个月
				 return true;
			}
    		return false;
		} catch (Exception e) {
			if(logger.isInfoEnabled()) logger.info(e);
			throw e;
		}
    }
    /**
     * REQ201702170009关于过期有价卡不能做换卡的优化需求
     * <br/>
     * 判断有效期是否已经过期(以天为判断标准)
     * @author zhuoyingzhi
     * @date 20170314
     * @param cardEndDate
     * @return
     * @throws Exception
     */
    public boolean isEndDate(String cardEndDate,String tag)throws Exception{
    	try {
			SimpleDateFormat formatter = null;
			Date sysdate=null;
			if("VC".equals(tag)){
				//(20110831)
				formatter = new SimpleDateFormat("yyyyMMdd");
				//系统当前日期
				sysdate=formatter.parse(SysDateMgr.getSysDateYYYYMMDD());
			}else{
				//(2011-08-31)
				formatter = new SimpleDateFormat("yyyy-MM-dd");
				sysdate=formatter.parse(SysDateMgr.getSysDate());
			}
			
			//有效期
			Date endDate=formatter.parse(cardEndDate);
			if(endDate.getTime() < sysdate.getTime()){
				 //有效期已经过期
				 return true;
			}
    		return false;
		} catch (Exception e) {
			if(logger.isInfoEnabled()) logger.info(e);
			throw e;
		}
    }
    
}
