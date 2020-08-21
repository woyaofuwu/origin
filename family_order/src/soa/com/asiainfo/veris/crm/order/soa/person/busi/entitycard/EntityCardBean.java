/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.entitycard;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCardException;
import com.asiainfo.veris.crm.order.pub.exception.ParamException;
import com.asiainfo.veris.crm.order.pub.exception.ResException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;

/**
 * @CREATED by gongp@2014-5-14 修改历史 Revision 2014-5-14 下午04:54:24
 */
public class EntityCardBean extends CSBizBean
{

    /**
     * 激活实体卡
     * 
     * @param data
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-7-7
     */
    public boolean activeEntityCard(IData data) throws Exception
    {

        IDataset dataset = this.getEntityCardInfo(data);

        IDataset paramSets = new DatasetList();
        IData param = new DataMap();

        for (int i = 0, size = dataset.size(); i < size; i++)
        {

            IData temp = dataset.getData(i);
            IData tmpData = new DataMap();
            if (!"1".equals(temp.getString("ACTIVE_FLAG")))
            {
                tmpData.put("CARD_NO", temp.getString("VALUE_CARD_NO"));
                tmpData.put("CARD_PRICE", (int) (Double.parseDouble(data.getString("SINGLE_PRICE", "0")) * 100));
                paramSets.add(tmpData);
            }
        }

        param.putAll(paramSets.toData());

        String kindId = "BIP2B277_T2000204_0_0";// 激活实体卡
        param.put("KIND_ID", kindId);
        param.put("X_TRANS_CODE", "");
        param.put("IN_MODE_CODE", "6");
        param.put("HOMEPROV", "898");
        param.put("ROUTETYPE", "00");//路由类型  00-省代码，01-手机号
        param.put("ROUTEVALUE", "000");
        param.put("TRADE_DEPART_PASSWD", "");//渠道接入密码
        IData activeEntityResult = IBossCall.callHttpIBOSS("IBOSS", param).getData(0);

        if (IDataUtil.isEmpty(activeEntityResult) || !"0000".equals(activeEntityResult.getString("X_RSPCODE", "")) || !"0".equals(activeEntityResult.getString("X_RSPTYPE", "")))
        {
            // common.error("实体卡激活失败！");
            CSAppException.apperr(CrmCardException.CRM_CARD_267);
        }

        IDataset datas = ResCall.entityCardActiveSync(data.getString("START_CARD_NO", ""), data.getString("END_CARD_NO", ""), getVisit().getDepartId());

        if (IDataUtil.isEmpty(datas) || datas.getData(0).getInt("X_RECORDNUM", 0) <= 0)
        {
            CSAppException.apperr(ResException.CRM_RES_10);
        }

        return true;
    }

    /**
     * 激活实体卡
     * 
     * @param data
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-7-7
     */
    public IData activeEntityCardForRes(IData data) throws Exception
    {
        IDataset paramSets = new DatasetList();
        IData param = new DataMap();

        String kindId = "BIP2B277_T2000204_0_0";// 激活实体卡
        param.putAll(data.getDataset("CARDLIST").toData());
        param.put("KIND_ID", kindId);
        param.put("X_TRANS_CODE", "");
        param.put("IN_MODE_CODE", "6");
        param.put("HOMEPROV", "898");
        param.put("ROUTETYPE", "00");//路由类型  00-省代码，01-手机号
        param.put("ROUTEVALUE", "000");
        param.put("TRADE_DEPART_PASSWD", "");//渠道接入密码
        IData activeEntityResult = IBossCall.callHttpIBOSS("IBOSS", param).getData(0);

        if (IDataUtil.isEmpty(activeEntityResult) || !"0000".equals(activeEntityResult.getString("X_RSPCODE", "")) || !"0".equals(activeEntityResult.getString("X_RSPTYPE", "")))
        {
            // common.error("实体卡激活失败！");
            CSAppException.apperr(CrmCardException.CRM_CARD_267);
        }

        return activeEntityResult;
    }

    public void addSaleStaffInfo(IDataset cardList) throws Exception
    {

        for (int i = 0, size = cardList.size(); i < size; i++)
        {
            cardList.getData(i).put("SALE_TIME", SysDateMgr.getSysTime());
            cardList.getData(i).put("SALE_STAFF_ID", this.getVisit().getStaffId());
        }
    }

    /*
     * 转换各种标识
     * @param args
     * @return
     */
    private IDataset changeTag(IDataset args) throws Exception
    {

        if (IDataUtil.isNotEmpty(args))
        {
            for (int i = 0, size = args.size(); i < size; i++)
            {
                IData arg = args.getData(i);
                if ((arg.getString("SALE_TAG")).equals("0"))
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
                if (card.equals(cardList.get(j, "VALUE_CARD_NO")))
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

    public IDataset checkNewEntityCard(IData data) throws Exception
    {

        String oldCardNo = data.getString("OLD_ENTITY_CARD_NO");

        IDataset oldCardInfo = ResCall.getUsedEntityCardInfo(oldCardNo, oldCardNo, "", "3");

        if (IDataUtil.isEmpty(oldCardInfo) || ("0".equals(oldCardInfo.getData(0).getString("X_RECORDNUM", ""))))
        {
            CSAppException.apperr(ResException.CRM_RES_90, "旧");
        }
        else
        {
            IData resultData = oldCardInfo.getData(0);
            if (resultData.containsKey("X_RESULTCODE") && resultData.getString("X_RESULTCODE").equals("-1"))
            {
                CSAppException.apperr(ResException.CRM_RES_91, "旧");
            }
        }

        String newCardNo = data.getString("NEW_ENTITY_CARD_NO");

        IDataset entityCardInfos = ResCall.validateEntityCardInfo(newCardNo, newCardNo, getVisit().getDepartId(), "3");

        if (IDataUtil.isEmpty(entityCardInfos) || ("0".equals(entityCardInfos.getData(0).getString("X_RECORDNUM", ""))))
        {
            CSAppException.apperr(ResException.CRM_RES_90, "新");
        }
        else
        {
            IData resultData = entityCardInfos.getData(0);
            if (resultData.containsKey("X_RESULTCODE") && resultData.getString("X_RESULTCODE").equals("-1"))
            {
                CSAppException.apperr(ResException.CRM_RES_91, "新");
            }
        }

        if (!oldCardInfo.getData(0).getString("RES_KIND_CODE").equals(entityCardInfos.getData(0).getString("RES_KIND_CODE")))
        {
            CSAppException.apperr(ResException.CRM_RES_89);
        }

        if (!"1".equals(entityCardInfos.getData(0).getString("ACTIVE_FLAG")))
        {

            IData param = new DataMap(), tmpData = new DataMap();

            IDataset dataset = new DatasetList();

            tmpData.put("CARD_NO", newCardNo);
            tmpData.put("CARD_PRICE", oldCardInfo.getData(0).getString("SALE_MONEY", "0"));
            dataset.add(tmpData);

            param.putAll(dataset.toData());

            String kindId = "BIP2B277_T2000204_0_0";// 激活实体卡
            param.put("KIND_ID", kindId);
            param.put("X_TRANS_CODE", "");
            param.put("IN_MODE_CODE", "6");
            param.put("HOMEPROV", "898");
            param.put("ROUTETYPE", "00");//路由类型  00-省代码，01-手机号
            param.put("ROUTEVALUE", "000");
            param.put("TRADE_DEPART_PASSWD", "");//渠道接入密码
            
            IData activeEntityResult = IBossCall.callHttpIBOSS("IBOSS", param).getData(0);

            if (IDataUtil.isEmpty(activeEntityResult) || !"0000".equals(activeEntityResult.getString("X_RSPCODE", "")) || !"0".equals(activeEntityResult.getString("X_RSPTYPE", "")))
            {
                // common.error("实体卡激活失败！");
                CSAppException.apperr(CrmCardException.CRM_CARD_267);
            }
        }

        return new DatasetList();
    }

    /**
     * 规则校验：只能是同类型、同面值的卡
     * 
     * @param dataset1
     * @throws Exception
     */
    public void checkRule(IDataset dataset) throws Exception
    {

        String kindCode = dataset.getData(0).getString("RES_KIND_CODE");
        String ADVISE_PRICE = dataset.getData(0).getString("ADVISE_PRICE");
        String cardKindCode = dataset.getData(0).getString("CARD_KIND_CODE") == null ? "" : dataset.getData(0).getString("CARD_KIND_CODE");
        String activeFlag = dataset.getData(0).getString("ACTIVE_FLAG");
        for (int i = 0; i < dataset.size(); i++)
        {
            IData temp = dataset.getData(i);
            if (!kindCode.equals(temp.getString("RES_KIND_CODE")))
            {
                CSAppException.apperr(CrmCardException.CRM_CARD_210, temp.getString("VALUE_CARD_NO"));
            }
            if (!ADVISE_PRICE.equals(temp.getString("ADVISE_PRICE")))
            {
                CSAppException.apperr(CrmCardException.CRM_CARD_212, temp.getString("VALUE_CARD_NO"));
            }
            if (null != temp.getString("CARD_KIND_CODE"))
            {
                if (!cardKindCode.equals(temp.getString("CARD_KIND_CODE")))
                {
                    CSAppException.apperr(CrmCardException.CRM_CARD_215, temp.getString("VALUE_CARD_NO"));
                }
            }
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
     * 获取通用参数
     * 
     * @param inparam
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-5-29
     */
    public IData getCommonParam() throws Exception
    {

        IData commonparam = new DataMap();
        commonparam.put("PROVINCE_CODE", getVisit().getProvinceCode());// 省别编码
        commonparam.put("IN_MODE_CODE", getVisit().getInModeCode()); // 接入方式
        commonparam.put("TRADE_EPARCHY_CODE", getVisit().getStaffEparchyCode());// 交易地州编码
        commonparam.put("TRADE_CITY_CODE", getVisit().getCityCode());// 交易城市代码
        commonparam.put("TRADE_DEPART_ID", getVisit().getDepartId());// 员工部门编码
        commonparam.put("TRADE_STAFF_ID", getVisit().getStaffId());// 员工城市编码
        commonparam.put("TRADE_DEPART_PASSWD", "");// 渠道接入密码
        commonparam.put("RES_TYPE_CODE", "3"); // 资源类型[实体卡]
        commonparam.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());// 路由地州编码
        commonparam.put("ROUTETYPE", "00");// 路由类型 00-省代码，01-手机号
        commonparam.put("ROUTEVALUE", "000");

        return commonparam;
    }

    /**
     * @param data
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-6-1
     */
    public IDataset getEntityCardInfo(IData data) throws Exception
    {

        IDataset entityCardInfos = ResCall.validateEntityCardInfo(data.getString("START_CARD_NO", ""), data.getString("END_CARD_NO", ""), getVisit().getDepartId(), "3");

        if (IDataUtil.isEmpty(entityCardInfos) || ("0".equals(entityCardInfos.getData(0).getString("X_RECORDNUM", ""))))
        {
            CSAppException.apperr(ResException.CRM_RES_10);
        }
        else
        {
            IData resultData = entityCardInfos.getData(0);
            if (resultData.containsKey("X_RESULTCODE") && resultData.getString("X_RESULTCODE").equals("-1"))
            {
                CSAppException.apperr(ResException.CRM_RES_9);
            }
        }

        return entityCardInfos;
    }

    /**
     * @param data
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-6-4
     */
    public IData getResInfo(IData data) throws Exception
    {

        IDataset cardList = getEntityCardInfo(data);

        IData entityData = cardList.getData(0);

        // 获取单价
        String realPrice = entityData.getString("ADVISE_PRICE");

        if (StringUtils.isBlank(realPrice))
        {
            CSAppException.apperr(ResException.CRM_RES_88);
        }

        float singlePrice = 0;

        String xCodingStr = data.getString("X_CODING_STR", "");

        IDataset dataset2 = new DatasetList();

        if (StringUtils.isNotBlank(xCodingStr))
        {
            dataset2 = new DatasetList(xCodingStr);
        }

        IData info = new DataMap(); // table2的展示数据

        info.put("startCardNo", data.getString("START_CARD_NO"));
        info.put("endCardNo", data.getString("END_CARD_NO"));
        info.put("simPrice", entityData.getString("RSRV_STR2"));
        info.put("advise_price", entityData.getString("ADVISE_PRICE"));
        info.put("resTypeCode", entityData.getString("RES_TYPE_CODE"));
        info.put("RES_KIND_CODE", entityData.getString("RES_KIND_CODE"));

        String devicePrice = entityData.getString("ADVISE_PRICE");

        singlePrice = getSinglePrice(data, devicePrice);

        info.put("singlePrice", String.valueOf(singlePrice));
        info.put("totalPrice", String.valueOf(singlePrice * cardList.size()));
        info.put("rowCount", String.valueOf(cardList.size()));
        info.put("valueCode", entityData.getString("VALUE_CODE"));

        String activeFlag = "1";// 1 标志该批实体卡已激活 ;0 标志该批实体卡未激活

        for (int i = 0, size = cardList.size(); i < size; i++)
        {
            IData temp = cardList.getData(i);
            if (!"1".equals(temp.getString("ACTIVE_FLAG")))
            {
                activeFlag = "0";
            }
        }

        info.put("activeFlag", activeFlag);
        info.put("parvalue", realPrice);
        info.put("devicePrice", devicePrice);

        dataset2.add(info);

        checkRule(cardList);

        this.checkISConnect(data, cardList);

        addSaleStaffInfo(cardList);

        // 组织结果集
        IData result = new DataMap();
        result.put("TABLE1", changeTag(cardList));
        result.put("TABLE2", dataset2);

        return result;
    }

    /**
     * 根据前台是否打折单选框获取单价
     * 
     * @param pd
     * @param cardPrice
     * @return
     * @throws Exception
     */
    public Float getSinglePrice(IData input, String cardPrice) throws Exception
    {

        String radio = input.getString("baseinfo_radio");
        Float salePrice = null; // 销售价格
        if ("a".equals(radio))
        {
            salePrice = Float.parseFloat(cardPrice) / 100;
        }
        else
        {
            String discount = input.getString("DISCOUNT", "");
            String strPrice = input.getString("SALEPRICE", "");
            if (!"".equals(discount))
            {
                salePrice = Float.parseFloat(cardPrice) * Float.parseFloat(discount) / 1000;
            }
            else if (!"".equals(strPrice))
            {
                salePrice = Float.parseFloat(strPrice);
            }
            else
            {
                // common.error("548106:获取折扣出错！");
            }
        }
        return salePrice;
    }

    /**
     * 实体卡锁定
     * 
     * @param dataNum
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-5-29
     */
    public IDataset lockEntityCard(IData dataNum) throws Exception
    {

        IData param = new DataMap();
        IDataset dataset = new DatasetList();

        param.putAll(getCommonParam());
        param.put("CARD_NO", dataNum.getString("CARD_NO", ""));
        param.put("KIND_ID", "BIP2B276_T2001116_0_0");// 交易唯一标识
        param.put("X_TRANS_CODE", "");
        param.put("IN_MODE_CODE", "6");
        param.put("HOMEPROV", "898");
        param.put("ROUTETYPE", "00");//路由类型  00-省代码，01-手机号
        param.put("ROUTEVALUE", "000");
        param.put("TRADE_DEPART_PASSWD", "");//渠道接入密码
        
        try
        {
            dataset = IBossCall.callHttpIBOSS("IBOSS", param);
        }
        catch (Exception e)
        {
            IData temp = new DataMap();
            temp.put("X_RSPCODE", "-1");
            temp.put("X_RSPDESC", e.getMessage() + "/n" + "系统异常，请联系管理员");
            dataset.add(temp);
        }

        return dataset;
    }

    /**
     * 根据实体卡卡号查询实体卡信息
     * 
     * @param pd
     * @param inpara
     * @return
     * @throws Exception
     */
    public IDataset QueryEntityCard(IData dataNum, Pagination pagination) throws Exception
    {

        IData param = new DataMap();
        IDataset dataset = new DatasetList();

        param.put("KIND_ID", "BIP1A119_T1000119_0_0");// 交易唯一标识
        param.put("X_TRANS_CODE", "IBOSS");
        param.put("IN_MODE_CODE", "6");
        param.put("CARD_NO", dataNum.getString("CARD_NO"));
        param.put("ROUTETYPE", "00");//路由类型  00-省代码，01-手机号
        param.put("ROUTEVALUE", "000");
        param.put("TRADE_DEPART_PASSWD", "");//渠道接入密码
        
        try
        {
            dataset = IBossCall.callHttpIBOSS("IBOSS", param);
        }
        catch (Exception e)
        {
            IData temp = new DataMap();
            temp.put("X_RSPCODE", "-1");
            temp.put("X_RSPDESC", e.getMessage() + "/n" + "系统异常，请联系管理员");
            dataset.add(temp);
        }

        if (IDataUtil.isNotEmpty(dataset))
        {

            IData entityData = dataset.getData(0);

            String entityCardStatus = "";
            String entityCardRoamFlag = "";

            if ("0".equals(entityData.getString("X_RESULTCODE")))
            {
                if ("00".equals(entityData.getString("CARDSTATUS")))
                    entityCardStatus = "未激活";
                if ("01".equals(entityData.getString("CARDSTATUS")))
                    entityCardStatus = "激活";
                if ("02".equals(entityData.getString("CARDSTATUS")))
                    entityCardStatus = "锁定";
                if ("03".equals(entityData.getString("CARDSTATUS")))
                    entityCardStatus = "绑定";
                if ("0".equals(entityData.getString("ROAMFLAG")))
                    entityCardRoamFlag = "可漫游";
                if ("1".equals(entityData.getString("ROAMFLAG")))
                    entityCardRoamFlag = "不可漫游";
                entityData.put("ENTITY_CARDSTATUS", entityCardStatus);
                entityData.put("ENTITY_ROAMFLAG", entityCardRoamFlag);

            }
            else if ("01".equals(entityData.getString("X_RESULTCODE")))
            {
                CSAppException.apperr(CrmCardException.CRM_CARD_231);
            }
            else if ("02".equals(entityData.getString("X_RESULTCODE")))
            {
                CSAppException.apperr(CrmCardException.CRM_CARD_232);
            }
        }
        else
        {
            CSAppException.apperr(CrmCardException.CRM_CARD_231);
        }

        return dataset;
    }

}
