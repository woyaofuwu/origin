
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commoncomponent;

import com.ailk.biz.BizEnv;
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.ProductException;
import com.asiainfo.veris.crm.order.pub.exception.UUException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.Clone;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.AttrBizInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaBBInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeGrpMerchpInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserGrpInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserGrpMerchpInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commonmethod.GrpCommonBean;

/**
 * 2014-7-14
 * 
 * @author chenyi 流量统付业务处理
 */
public class BbossPayBizInfoDealbean
{
    /**
     * chenyi 2014-7-15 成员变更拼写mebcenpay集合
     * 
     * @param paramDataList
     * @param userid
     * @return
     * @throws Exception
     */
    public static IDataset chgMebCenPayData(IDataset paramDataList, String userid,String productOfferId) throws Exception
    {
        // 1定义参数集
        IDataset mebCenPayDataset = new DatasetList();
        if (IDataUtil.isNotEmpty(paramDataList))
        {

            // 2过滤参数，获取add状态参数
            paramDataList = GrpCommonBean.getOthererParam(paramDataList);

            // 3获取资料表 MebCenPay数据
            IDataset mebCenPayInfoDataset = UserGrpInfoQry.queryMebCenPayInfo(userid,productOfferId);

            if (IDataUtil.isEmpty(mebCenPayInfoDataset))
            {
                return null;
            }
            IData oldDataMap = mebCenPayInfoDataset.getData(0);
            // 4 将变更的参数暂放到tempData
            // 4-1获取封顶流量和成员套餐包变更信息
            IData tempData = new DataMap();// 暂存有变化的参数
            String tp_id = "";// 成员套餐包
            String nf_limit = "";// 成员封顶流量

            // 反向受理 attr_code与规范相同 正向受理的attr_code是product_id与规范attr_code组合
            for (int i = 0, sizeI = paramDataList.size(); i < sizeI; i++)
            {
                IData paramData = paramDataList.getData(i);
                // 成员套餐包
                if (paramData.getString("ATTR_CODE").endsWith("1100"))
                {
                    tp_id = paramData.getString("ATTR_VALUE");
                    tempData.put("ELEMENT_ID", tp_id);
                }
                else if (paramData.getString("ATTR_CODE").endsWith("1101"))
                {
                    nf_limit = paramData.getString("ATTR_VALUE");
                    tempData.put("LIMIT_FEE", nf_limit);
                }

            }

            // 4-2获取成员生效规则变更值 生成生效时间
            String start_date = oldDataMap.getString("START_DATE");
            for (int i = 0, sizeI = paramDataList.size(); i < sizeI; i++)
            {
                IData paramData = paramDataList.getData(i);
                if (paramData.getString("ATTR_CODE").endsWith("1103"))
                {
                    String effRule = paramData.getString("ATTR_VALUE");
                    if ("1".equals(effRule))
                    {
                        start_date = SysDateMgr.getSysTime();
                        tempData.put("START_DATE", start_date);
                    }
                    else if ("2".equals(effRule))
                    {
                        start_date = SysDateMgr.getFirstDayOfNextMonth() + SysDateMgr.getFirstTime00000();
                        tempData.put("START_DATE", start_date);
                    }
                }
            }

            String end_date = oldDataMap.getString("END_DATE");
            String attr_value = "00";
            IDataset attrInfos = UserAttrInfoQry.getUserAttrByUserId(userid,"1104"); //获取新增时赠送有效期的值
            if(IDataUtil.isNotEmpty(attrInfos))
            {
                attr_value = attrInfos.getData(0).getString("ATTR_VALUE");
            }
            // 4-3获取赠送有效期,根据生效规则生成end_date
            for (int i = 0, sizeI = paramDataList.size(); i < sizeI; i++)
            {
                IData paramData = paramDataList.getData(i);
                if (paramData.getString("ATTR_CODE").endsWith("1104"))
                {
                    String effMonth = paramData.getString("ATTR_VALUE");// 成员有效账期，月为单位，00代表无限期，变更仅能从00变成有限期
                    if (StringUtils.equals(attr_value, effMonth))  //有限期没有变化，END_DATE为新增时的失效时间
                    {
                        tempData.put("END_DATE", end_date);
                        continue;
                    }
                    else if(!StringUtils.equals("00", attr_value)) //已有限期不能变更
                    {
                        CSAppException.apperr(ProductException.CRM_PRODUCT_508);
                    }
                    int month = Integer.parseInt(effMonth);
                    tempData.put("END_DATE", SysDateMgr.getAddMonthsLastDay(month, start_date));
                }
            }

            // 5如果有变更参数则
            if (IDataUtil.isNotEmpty(tempData))
            {

                // 5.1深度克隆相关数据
                IData newDataMap = (IData) Clone.deepClone(oldDataMap);
                // 5.2老数据是将end_date改为当前时间，modify_tag改为2的原有记录
                oldDataMap.put("MODIFY_TAG", "2");
                oldDataMap.put("END_DATE", SysDateMgr.getSysTime());

                // 5.3新变更数据
                newDataMap.put("ELEMENT_ID", tempData.getString("ELEMENT_ID", oldDataMap.getString("ELEMENT_ID")));
                newDataMap.put("LIMIT_FEE", tempData.getString("LIMIT_FEE", oldDataMap.getString("LIMIT_FEE")));
                newDataMap.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                newDataMap.put("START_DATE",tempData.getString("START_DATE", oldDataMap.getString("START_DATE")));
                newDataMap.put("END_DATE",tempData.getString("END_DATE", oldDataMap.getString("END_DATE")));
                newDataMap.put("INST_ID", SeqMgr.getInstId());
                // 5.4将新组合数据添加集合
                mebCenPayDataset.add(oldDataMap);
                mebCenPayDataset.add(newDataMap);

            }

        }

        return mebCenPayDataset;
    }

    /**
     * chenyi 2014-4-18 流量统付业务 如同步账务属性字段有变动，则需要新增一条，删除一条表数据给账务 如果是变更的操作，涉及TF_F_USER_CENPAY/TF_F_CENPAY 这两个表时，我需要传两条数据给计费
     * 一条是将end_date改为当前时间，modify_tag改为2的原有记录 update操作，截止当前记录 一条是modify_tag为0的变更后记录 新增变更后记录
     * 
     * @throws Exception
     */
    public static IDataset getDataGrpCenpay(IDataset productParamInfos, String userid) throws Exception
    {

        IDataset grpCenPayDataset = new DatasetList();
        // 1-获取产品参数信息
        if (IDataUtil.isEmpty(productParamInfos))
        {
            return null;
        }
        // 因为变更时子类每次只处理一个产品，因此产品参数的List中一般只会有一条记录
        IData productParamInfo = productParamInfos.getData(0);

        // 2获取前台传递过来的所有参数值
        IDataset params = productParamInfo.getDataset("PRODUCT_PARAM");
        if (IDataUtil.isEmpty(params))
        {
            return null;
        }
        // 3过滤参数，获取add状态参数
        params = GrpCommonBean.getOthererParam(params);

        IData tempData = new DataMap();// 暂存有变化的参数
        // 4 循环参数集,将新值放到对应节点
        for (int i = 0, sizeI = params.size(); i < sizeI; i++)
        {
            IData param = params.getData(i);
            if ("999044014".equals(param.getString("ATTR_CODE")) || "999054016".equals(param.getString("ATTR_CODE")) ||
                    "999084014".equals(param.getString("ATTR_CODE")) || "999094016".equals(param.getString("ATTR_CODE")))
            {
                String cust_name = param.getString("ATTR_VALUE");// 集团客户编码 通用统付的时候表示客户简称 定向统付的时候表示客户产品简称
                tempData.put("CUST_NAME", cust_name);
            }
            else if ("999044008".equals(param.getString("ATTR_CODE")) || "999054008".equals(param.getString("ATTR_CODE")) ||
                    "999084008".equals(param.getString("ATTR_CODE")) || "999094008".equals(param.getString("ATTR_CODE")))
            {
                String oper_type = param.getString("ATTR_VALUE");// 业务模式
                tempData.put("OPER_TYPE", oper_type);
            }
            else if ("999044030".equals(param.getString("ATTR_CODE")) || "999084030".equals(param.getString("ATTR_CODE")))
            {
                String SERVICE_ID = "999044030".equals(param.getString("ATTR_CODE")) ? param.getString("ATTR_VALUE") : "0";// 业务计费代码
                // 定向流量必填;
                // 通用流量填:0
                tempData.put("SERVICE_ID", SERVICE_ID);
            }
            else if ("999044009".equals(param.getString("ATTR_CODE")))
            {
                String nf_limit = "999044009".equals(param.getString("ATTR_CODE")) ? param.getString("ATTR_VALUE") : "";// 单用户封顶流量
                // 需确认
                int limit_fee = limitParsnt(nf_limit);
                tempData.put("LIMIT_FEE", limit_fee);
            }
            else if ("999044025".equals("ATTR_CODE") || "999084025".equals("ATTR_CODE"))
            {
                tempData.put("END_DATE", param.getString("ATTR_VALUE"));
            }

            else if ("999044024".equals("ATTR_CODE") || "999084024".equals("ATTR_CODE"))
            {
                tempData.put("START_DATE", param.getString("ATTR_VALUE"));
            }

        }
        // 5如果对应参数有变化则对other表同步账务数据进行操作
        if (IDataUtil.isNotEmpty(tempData))
        {
            // 1获取资料表 tf_f_user_GRP_CENPAY数据
            IDataset userGrpCenPayDataset = UserGrpInfoQry.queryGrpCenPayInfo(userid);
            if (IDataUtil.isEmpty(userGrpCenPayDataset))
            {
                return null;
            }
            IData oldDataMap = userGrpCenPayDataset.getData(0);

            // 2深度克隆相关数据
            IData newDataMap = (IData) Clone.deepClone(oldDataMap);

            // 一条是将end_date改为当前时间，modify_tag改为2的原有记录
            oldDataMap.put("MODIFY_TAG", "2");
            // 修改老值生效时间 场景 第一次订购套餐时候生效时间为下个月，但在第一次套餐还没生效前就修改了套餐，并把生效时间修改了
            if (StringUtils.isNotEmpty(tempData.getString("START_DATE")))
            {
                oldDataMap.put("START_DATE", tempData.getString("START_DATE"));
            }
            oldDataMap.put("END_DATE", SysDateMgr.getLastDateThisMonth());
          

            // 将变化的新值放入对应字段，如果没变化，直接将原来值插入
            newDataMap.put("CUST_NAME", tempData.getString("CUST_NAME", oldDataMap.getString("CUST_NAME")));
            newDataMap.put("OPER_TYPE", tempData.getString("OPER_TYPE", oldDataMap.getString("OPER_TYPE")));
            newDataMap.put("SERVICE_ID", tempData.getString("SERVICE_ID", oldDataMap.getString("SERVICE_ID")));
            newDataMap.put("LIMIT_FEE", tempData.getString("LIMIT_FEE", oldDataMap.getString("LIMIT_FEE")));
            if (StringUtils.isNotEmpty(tempData.getString("END_DATE")))
            {
                newDataMap.put("END_DATE", tempData.getString("END_DATE"));
            }

            // 新增值
            newDataMap.put("MODIFY_TAG", "0");
            newDataMap.put("START_DATE", tempData.getString("START_DATE", SysDateMgr.getFirstDayOfNextMonth()));
            newDataMap.put("INST_ID", SeqMgr.getInstId());

            grpCenPayDataset.add(newDataMap);
            grpCenPayDataset.add(oldDataMap);

        }
        return grpCenPayDataset;
    }

    /**
	 * @author chenmw
	 * @date 2016-11-22
	 * @description 国际流量统付，产品变更，登记grp_cenpay表
     * @param productParamInfos
     * @param userid
     * @return
     * @throws Exception
     */
    public static IDataset getDataGrpCenpayForInternational(IDataset productParamInfos, String userid) throws Exception{

        IDataset grpCenPayDataset = new DatasetList();
        // 1-获取产品参数信息
        if (IDataUtil.isEmpty(productParamInfos)){
        	return new DatasetList();
        }
        // 因为变更时子类每次只处理一个产品，因此产品参数的List中一般只会有一条记录
        IData productParamInfo = productParamInfos.getData(0);

        // 2-获取前台传递过来的所有参数值
        IDataset params = productParamInfo.getDataset("PRODUCT_PARAM");
        if (IDataUtil.isEmpty(params)){
            return new DatasetList();
        }
        // 3-过滤参数，获取add状态参数
        params = GrpCommonBean.getOthererParam(params);

        // 4-循环参数集,将新值放到对应节点
        // 暂存有变化的参数
        IData tempData = new DataMap();
        for (int i = 0, sizeI = params.size(); i < sizeI; i++){
            IData param = params.getData(i);
            String attrCode = param.getString("ATTR_CODE");
        	String attrValue = param.getString("ATTR_VALUE");
        	if("999104016".equals(attrCode)){
        		tempData.put("CUST_NAME",attrValue);
        	}
        }
       
        // 5-如果对应参数有变化则对other表同步账务数据进行操作
        if (IDataUtil.isNotEmpty(tempData)){
            // 1-获取资料表 tf_f_user_GRP_CENPAY数据
            IDataset userGrpCenPayDataset = UserGrpInfoQry.queryGrpCenPayInfo(userid);
            if (IDataUtil.isEmpty(userGrpCenPayDataset)){
            	return new DatasetList();
            }
            IData oldDataMap = userGrpCenPayDataset.getData(0);

            // 2-深度克隆相关数据
            IData newDataMap = (IData) Clone.deepClone(oldDataMap);

            // 一条是将end_date改为当前时间，modify_tag改为2的原有记录
            oldDataMap.put("MODIFY_TAG", "2");
            // 修改老值生效时间 场景 第一次订购套餐时候生效时间为下个月，但在第一次套餐还没生效前就修改了套餐，并把生效时间修改了
            if (StringUtils.isNotEmpty(tempData.getString("START_DATE"))){
                oldDataMap.put("START_DATE", tempData.getString("START_DATE"));
            }
            oldDataMap.put("END_DATE", SysDateMgr.getLastDateThisMonth());
          
            // 将变化的新值放入对应字段，如果没变化，直接将原来值插入
            newDataMap.put("CUST_NAME", tempData.getString("CUST_NAME", oldDataMap.getString("CUST_NAME")));
            newDataMap.put("OPER_TYPE", tempData.getString("OPER_TYPE", oldDataMap.getString("OPER_TYPE")));
            newDataMap.put("SERVICE_ID", tempData.getString("SERVICE_ID", oldDataMap.getString("SERVICE_ID")));
            newDataMap.put("LIMIT_FEE", tempData.getString("LIMIT_FEE", oldDataMap.getString("LIMIT_FEE")));
            if (StringUtils.isNotEmpty(tempData.getString("END_DATE"))){
                newDataMap.put("END_DATE", tempData.getString("END_DATE"));
            }

            // 新增值
            newDataMap.put("MODIFY_TAG", "0");
            newDataMap.put("START_DATE", tempData.getString("START_DATE", SysDateMgr.getFirstDayOfNextMonth()));
            newDataMap.put("INST_ID", SeqMgr.getInstId());

            grpCenPayDataset.add(newDataMap);
            grpCenPayDataset.add(oldDataMap);
        }
        return grpCenPayDataset;
    }

    /**
     * @Function:
     * @Description:根据生效规则和有效期 设定 TF_B_TRADE_RELATION， TF_B_TRADE_MEB_CENPAY，TF_B_TRADE_DISCNT，TF_B_TRADE_PAYRELATION
     *                         生效时间
     * @author:chenyi
     * @throws Exception
     * @date: 下午3:32:50 2014-9-30
     */
    public static void dealPayBizRelaEffect(IData iData, IDataset paramDataList) throws Exception
    {

        if (IDataUtil.isEmpty(paramDataList))
        {
            return;
        }

        String startDate = "";
        // 反向受理 attr_code与规范相同 正向受理的attr_code是product_id与规范attr_code组合
        // 1 获取生效规则，生成start-date
        for (int i = 0, sizeI = paramDataList.size(); i < sizeI; i++)
        {
            IData paramData = paramDataList.getData(i);
            if (paramData.getString("ATTR_CODE").endsWith("1103"))
            {
                String effRule = paramData.getString("ATTR_VALUE");// 成员账期生效规则 1-立即生效 2-下账期生效
                if ("1".equals(effRule))
                {
                    startDate = SysDateMgr.getSysTime();
                    iData.put("START_DATE", startDate);
                }
                else if ("2".equals(effRule))
                {
                    startDate = SysDateMgr.getFirstDayOfNextMonth()+ SysDateMgr.getFirstTime00000();;
                    iData.put("START_DATE", startDate);
                }

            }
        }
        // 2 根据赠送有效期和start_date生成end-date
        for (int i = 0, sizeI = paramDataList.size(); i < sizeI; i++)
        {
            IData paramData = paramDataList.getData(i);

            if (paramData.getString("ATTR_CODE").endsWith("1104"))
            {
                String effMonth = paramData.getString("ATTR_VALUE");// 成员有效账期，月为单位，00代表无限期，变更仅能从00变成有限期，已有限期不能变更
                if ("00".equals(effMonth))
                {
                    iData.put("END_DATE", SysDateMgr.END_DATE_FOREVER);
                }
                else
                {
                    int month = Integer.parseInt(effMonth);

                    iData.put("END_DATE", SysDateMgr.getAddMonthsLastDay(month, startDate));
                }

            }
        }
    }

    /**
     * @Function:
     * @Description:如果赠送有效期和变更生效规则 发生变化，则start-date和end-date需要变化
     * @param：
     * @return：
     * @throws：
     * @version:
     * @author:chenyi
     * @date: 下午3:32:50 2013-10-23
     */
    public static void chgEffect(IData changeData, IData tempData)
    {
        if (StringUtils.isEmpty(changeData.getString("MODIFY_TAG")))
        {
            changeData.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
        }
        else if (TRADE_MODIFY_TAG.DEL.getValue().equals(changeData.getString("MODIFY_TAG")))
        {
            return;
        }

        changeData.put("START_DATE", tempData.getString("START_DATE"));
        changeData.put("END_DATE", tempData.getString("END_DATE"));
    }

    /**
     * @Function:
     * @Description:个人套餐包生效时间修改 
     *                          由于套餐包修改都是下账期生效，如果赠送有效期和生效规则关联生成的start——date<套餐包默认生效时间，则套餐包生效时间为默认生效时间，否则为关联生成的start——date
     * @param：
     * @return：
     * @throws：
     * @version:
     * @author:chenyi
     * @date: 下午3:32:50 2013-10-23
     */
    public static void chgDisEffect(IData changeData, IData tempData)
    {
        if (TRADE_MODIFY_TAG.Add.getValue().equals(changeData.getString("MODIFY_TAG")))
        {
            changeData.put("END_DATE", tempData.getString("END_DATE"));
        }

    }

    /**
     * @Function:
     * @Description:
     * @param：获取变更的生效规则和赠送有效期
     * @return：
     * @throws：
     * @version:
     * @author:chenyi
     * @date: 下午3:32:50 2013-10-23
     */
    public static IData chgPayBizRelaEffect(IDataset paramDataList, String useridA, String useridB) throws Exception
    {
        // 1定义参数集
        IData tempData = new DataMap();

        if (IDataUtil.isEmpty(paramDataList))
        {
            return tempData;
        }
        // 2过滤参数，获取add状态参数
        paramDataList = GrpCommonBean.getOthererParam(paramDataList);

        // 反向受理 attr_code与规范相同 正向受理的attr_code是product_id与规范attr_code组合

        // 3获取生效时间
        // 2-1获取user-attr赠送有效期 和生效规则
        IDataset relaBB = RelaBBInfoQry.getBBByUserIdAB(useridA,useridB,null,null);
        if(IDataUtil.isEmpty(relaBB)){
        	CSAppException.apperr(UUException.CRM_UU_103, useridA,useridB);
         }
        String start_date = relaBB.getData(0).getString("START_DATE");// 默认START_DATE与成员新增生效时间一致
        String end_date = relaBB.getData(0).getString("END_DATE");// 默认END_DATE与成员新增生效时间一致
        tempData.put("START_DATE", start_date);
        tempData.put("END_DATE", end_date);
        for (int i = 0, sizeI = paramDataList.size(); i < sizeI; i++)
        {
            IData paramData = paramDataList.getData(i);
            if (paramData.getString("ATTR_CODE").endsWith("1103"))
            {
                String effRule = paramData.getString("ATTR_VALUE");
                if ("1".equals(effRule))
                {
                    start_date = SysDateMgr.getSysTime();;
                }
                else if ("2".equals(effRule))
                {
                    start_date = SysDateMgr.getFirstDayOfNextMonth()+ SysDateMgr.getFirstTime00000();
                }
                tempData.put("START_DATE", start_date);
            }
        }
        // 4 根据赠送有效期和start_date生成end_date
        for (int i = 0, sizeI = paramDataList.size(); i < sizeI; i++)
        {
            IData paramData = paramDataList.getData(i);

            if (paramData.getString("ATTR_CODE").endsWith("1104"))
            {
                String effMonth = paramData.getString("ATTR_VALUE");// 成员有效账期，月为单位，00代表无限期，变更仅能从00变成有限期，已有限期不能变更
                if ("00".equals(effMonth))
                {
                    CSAppException.apperr(ProductException.CRM_PRODUCT_508);
                }
                int month = Integer.parseInt(effMonth);
                end_date = SysDateMgr.getAddMonthsLastDay(month, start_date);
                tempData.put("END_DATE", end_date);
            }

        }

        return tempData;
    }

    /**
     * 2014-7-15 集团成员新增 拼写mebcenpay表数据
     * 
     * @param paramDataList
     * @param userid
     * @param product_id
     * @param ecId
     * @param serial_number
     * @return
     * @throws Exception
     */
    public static IData getDataMebCenPay(IDataset paramDataList, String userid, String useridA, String product_id, String ecId, String serial_number) throws Exception
    {
        IData cenPayData = new DataMap();

        String product_spec_code = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_ATTR_BIZ", new String[]
        { "ID", "ID_TYPE", "ATTR_CODE", "ATTR_OBJ" }, "ATTR_VALUE", new String[]
        { "1", "B", product_id, "PRO" });// 获取集团产品编码

        cenPayData.put("INST_ID", SeqMgr.getInstId());
        cenPayData.put("USER_ID", userid);
        cenPayData.put("MP_GROUP_CUST_CODE", ecId);
        String payType = getPayTypeByProductSpecCode(product_spec_code);
        // ""代表定向流量统付
        cenPayData.put("PAY_TYPE", payType);

        String operType = merchIdToProId(product_spec_code, useridA);// 获取当前的产品集团订购时候的业务模式
        cenPayData.put("OPER_TYPE", operType);// 业务模式 OPER_TYPE 定向流量统付 attr_code为999044008 ； 通用流量统付
        // attr_code为999054008

        IData grpMerchPInfo = getGrpMerchpInfo(useridA);
        cenPayData.put("PRODUCT_OFFER_ID", grpMerchPInfo.getString("PRODUCT_OFFER_ID", ""));
        cenPayData.put("SERIAL_NUMBER", serial_number);// 成员号码SERIAL_NUMBER
        cenPayData.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
        cenPayData.put("RSRV_VALUE_CODE", "BBOSSMEMLLTF");// 同步账务标记
        cenPayData.put("UPDATE_TIME", SysDateMgr.getSysTime());
        cenPayData.put("ACCEPT_MONTH", SysDateMgr.getCurMonth());

        if (IDataUtil.isNotEmpty(paramDataList))
        {
            // 反向受理 attr_code与规范相同 正向受理的attr_code是product_id与规范attr_code组合
            for (int i = 0, sizeI = paramDataList.size(); i < sizeI; i++)
            {
                IData paramData = paramDataList.getData(i);
                // 成员套餐包
                if (paramData.getString("ATTR_CODE").endsWith("1100"))
                {
                    String tp_id = "4".equals(operType) ? paramData.getString("ATTR_VALUE") : "";
                    cenPayData.put("ELEMENT_ID", tp_id);

                }
                else if (paramData.getString("ATTR_CODE").endsWith("1101"))
                {
                    String nf_limit = "5".equals(operType) ? paramData.getString("ATTR_VALUE") : "";
                    cenPayData.put("LIMIT_FEE", nf_limit);

                }
            }
        }

        // 生效时间处理
        dealPayBizRelaEffect(cenPayData, paramDataList);

        return cenPayData;
    }

    /**
     * @description 根据集团产品编号获取统付类型
     * @author xunyl
     * @date 2015-07-15
     */
    public static String getPayTypeByProductSpecCode(String productSpecCode)throws Exception{
        //1- 定义返回值
        String payType = "";
        
        //2- 根据产品编码获取对应的统付类型
        if(StringUtils.equals("99904", productSpecCode)){//定向流量统付
            payType = "0";
        }else if(StringUtils.equals("99905", productSpecCode)){//通用流量统付
            payType = "1";
        }else if(StringUtils.equals("99908", productSpecCode)){//闲时定向流量统付
            payType = "2";
        }else if(StringUtils.equals("99909", productSpecCode)){//闲时通用流量统付
            payType = "3";
       }else if(StringUtils.equals("99910", productSpecCode)){//国际流量统付
    		payType = "2";
       }
        //3- 返回统付类型
        return payType;
    }
    
    /**
     * chenyi 2014-7-28 订购加油包时拼装meb_dis数据同步服开
     * 
     * @param merchPDsts
     * @param productID
     * @param merchMeb
     * @param member_order_number
     * @return
     * @throws Exception
     */
    public static IDataset getDataMerchMbDis(IDataset merchPDsts, String productID, IData merchMeb, String member_order_number) throws Exception
    {
        if (IDataUtil.isEmpty(merchPDsts))
        {
            return null;
        }
        // 2- 定义数据
        IDataset productDstInfo = new DatasetList();

        // 判断当前是否是流量统付，而且是否定额模式，如果是，则需要生成付费关系
        // 2- 处理BBOSS侧资费表信息
        for (int i = 0; i < merchPDsts.size(); i++)
        {
            IData merchPDst = merchPDsts.getData(i);
            // 2-1 将资费编码转化为BBOSS侧的资费编号，转化后的资费编号不存在或者为空皆属于无效资费，不进行处理
            String discntCode = merchPDst.getString("DISCNT_CODE", "");// 本地ELEMENT_ID
            IDataset discntInfos = AttrBizInfoQry.getBizAttrByAttrValue(productID, "D", "FluxPay", discntCode, null);

            if (IDataUtil.isEmpty(discntInfos))
            {
                continue;
            }
            String productDiscntCode = discntInfos.getData(0).getString("ATTR_CODE");

            merchPDst.put("USER_ID", merchMeb.getString("USER_ID"));
            merchPDst.put("SERIAL_NUMBER", merchMeb.getString("SERIAL_NUMBER"));
            merchPDst.put("PRODUCT_OFFER_ID", merchMeb.getString("PRODUCT_OFFER_ID"));
            String product_spec_code = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_ATTR_BIZ", new String[]
            { "ID", "ID_TYPE", "ATTR_CODE", "ATTR_OBJ" }, "ATTR_VALUE", new String[]
            { "1", "B", productID, "PRO" });// 获取集团产品编码

            merchPDst.put("PRODUCT_SPEC_CODE", product_spec_code);
            merchPDst.put("PRODUCT_DISCNT_CODE", productDiscntCode);// 流量叠加包
            merchPDst.put("MEMBERORDERNUMBER", member_order_number);// 成员订购关系 需要在回单的时候更新到此表
            merchPDst.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
            merchPDst.put("START_DATE", merchPDst.getString("START_DATE", "").equals("") ? SysDateMgr.getSysTime() : merchPDst.getString("START_DATE"));
            merchPDst.put("END_DATE", SysDateMgr.getLastDateThisMonth());
            merchPDst.put("RSRV_TAG1", "1");// 订单来源
            merchPDst.put("INST_ID", SeqMgr.getInstId());
            productDstInfo.add(merchPDst);
        }
        return productDstInfo;

    }

    /**
     * chenyi 结束grpcenpay资料
     * 
     * @param userid
     * @return
     * @throws Exception
     */
    public static IDataset getDELgrpcenpay(String userid) throws Exception
    {
        IDataset grpCenPayInfoDataset = UserGrpInfoQry.queryGrpCenPayInfo(userid);

        if (IDataUtil.isEmpty(grpCenPayInfoDataset))
        {
            return null;
        }

        for (int i = 0, sizeI = grpCenPayInfoDataset.size(); i < sizeI; i++)
        {
            IData grpCenpay = grpCenPayInfoDataset.getData(i);

            grpCenpay.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
            grpCenpay.put("END_DATE", SysDateMgr.getSysTime());
        }

        return grpCenPayInfoDataset;

    }

    /**
     * chenyi 2014-7-28 成员注销结束此数据
     * 
     * @param userid
     * @return
     * @throws Exception
     */
    public static IDataset getDELmebcenpay(String userid,String productOfferId) throws Exception
    {

        IDataset mebCenPayInfoDataset = UserGrpInfoQry.queryMebCenPayInfo(userid,productOfferId);
        if (IDataUtil.isEmpty(mebCenPayInfoDataset))
        {
            return null;
        }

        for (int i = 0, sizeI = mebCenPayInfoDataset.size(); i < sizeI; i++)
        {
            IData mebCenpay = mebCenPayInfoDataset.getData(i);

            mebCenpay.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
            mebCenpay.put("END_DATE", SysDateMgr.getSysTime());
        }

        return mebCenPayInfoDataset;
    }

    /**
     * chenyi 2014-7-26 订购流量叠加包时需要插入一条新纪录到此表，且结束日期为当前月最后一天
     * 
     * @param discntInfoDataset
     * @param userId
     * @return
     * @throws Exception
     */
    public static IDataset getFluxMebCenpayData(IDataset discntInfoDataset, String userId, String product_id,String productOfferId) throws Exception
    {
        // 获取受理时候资料信息
        IDataset mebCenPayInfoDataset = UserGrpInfoQry.queryMebCenPayInfo(userId,productOfferId);

        // 拼装mebcenpay数据
        IDataset mebcenpayDataset = new DatasetList();

        if (IDataUtil.isNotEmpty(mebCenPayInfoDataset))
        {
            for (int i = 0, sizeI = discntInfoDataset.size(); i < sizeI; i++)
            {
                IData mebcenpay =(IData )Clone.deepClone(mebCenPayInfoDataset.getData(0));

                IData discntInfo = discntInfoDataset.getData(i);
                String discntCode = discntInfo.getString("DISCNT_CODE", "");// 本地ELEMENT_ID
                String element_id = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_ATTR_BIZ", new String[]
                { "ID", "ID_TYPE", "ATTR_VALUE", "ATTR_OBJ" }, "ATTR_CODE", new String[]
                { product_id, "D", discntCode, "FluxPay" });
                
                //如果受理模式为5，则LIMIT_FEE需要放对应流量值
                if ("5".equals(mebcenpay.getString("OPER_TYPE")))
                {
                    String limitInfo = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_ATTR_BIZ", new String[]
                    { "ID", "ID_TYPE", "ATTR_VALUE", "ATTR_OBJ" }, "RSRV_STR5", new String[]
                    { product_id, "D", discntCode, "FluxPay" });
                    String limint = limitInfo.substring(limitInfo.indexOf("|")+1, limitInfo.length());
                    mebcenpay.put("LIMIT_FEE", limint);
                }

                mebcenpay.put("START_DATE", SysDateMgr.getSysTime());
                mebcenpay.put("END_DATE", SysDateMgr.getLastDateThisMonth());
                mebcenpay.put("UPDATE_TIME", SysDateMgr.getSysTime());
                mebcenpay.put("ACCEPT_MONTH", SysDateMgr.getCurMonth());
                mebcenpay.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                mebcenpay.put("ELEMENT_ID", element_id);
                mebcenpay.put("INST_ID", SeqMgr.getInstId());
                mebcenpayDataset.add(mebcenpay);
            }
        }

        return mebcenpayDataset;
    }

    /*
     * chenyi 2014-7-15 根据用户ID查询TF_F_USER_GRP_MERCHP表得到产品对应哪个的订单号和订购关系ID
     */
    private static IData getGrpMerchpInfo(String userid) throws Exception
    {
        IDataset merchPDatas = UserGrpMerchpInfoQry.qryMerchpInfoByUserIdMerchSpecProductSpecStatus(userid, null, null, null, null);
        if (merchPDatas == null || merchPDatas.size() == 0)
        {
            CSAppException.apperr(ProductException.CRM_PRODUCT_106);
        }
        return merchPDatas.getData(0);
    }

    /**
     * chenyi 2014-4-08 定向流量统付业务和通用流量统付业务 由于业务特殊性，需要同步一些数据到账务
     */
    public static IData insertGrpCenpay(IData productParamInfos, String product_spec_code, String merch_spec_code, String productOfferingId, String groupId, String product_id, String ec_serial_number, String biz_mode, String userid) throws Exception
    {
        IData dataMap = new DataMap();
        dataMap.put("USER_ID", userid);
        dataMap.put("INST_ID", SeqMgr.getInstId());
        dataMap.put("ACCEPT_MONTH", SysDateMgr.getCurMonth());
        dataMap.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
        dataMap.put("MP_GROUP_CUST_CODE", ec_serial_number);// 放置与账务约定标识 ec_serial_number
        dataMap.put("GROUP_ID", groupId);

        dataMap.put("BIZ_MODE", biz_mode);

        String province_code = BizEnv.getEnvString("crm.grpcorp.provincecode");// 获取当前省份编码
        dataMap.put("PROVINCE_CODE", province_code);

        // String merch_spec_code = map.getString("RSRV_STR2", "");
        dataMap.put("MERCH_SPEC_CODE", merch_spec_code);
        dataMap.put("PRODUCT_SPEC_CODE", product_spec_code);
        dataMap.put("PRODUCT_ID", product_id);
        dataMap.put("PRODUCT_OFFER_ID", productOfferingId);

        String pay_type = getPayTypeByProductSpecCode(product_spec_code);
        dataMap.put("PAY_TYPE", pay_type);
        String oper_type = productParamInfos.getString(product_spec_code+"4008");// 业务模式
        dataMap.put("OPER_TYPE", oper_type);
        if ("99904".equals(product_spec_code))
        {
            String cust_name = productParamInfos.getString("999044014");// 集团客户编码 通用统付是时候表示客户简称 定向统付的时候表示客户产品简称
            dataMap.put("CUST_NAME", cust_name);          
            if (StringUtils.equals("2", oper_type))
            {
                String nf_limit = productParamInfos.getString("999044009");// 业务模式为2时，传入LIMIT_FEE值并判断
                int limit_fee = limitParsnt(nf_limit);
                dataMap.put("LIMIT_FEE", limit_fee);
            }

            dataMap.put("SERVICE_ID", productParamInfos.getString("999044030", "123456"));// 预受理阶段没有此值，pre代表预受理阶段入表，归档时候会更新此值
        }
        if ("99905".equals(product_spec_code))
        {
            String cust_name = productParamInfos.getString("999054016");// 集团客户编码 通用统付是时候表示客户简称 定向统付的时候表示客户产品简称
            dataMap.put("CUST_NAME", cust_name);          
            dataMap.put("SERVICE_ID", "0"); // 通用业务为0；
        }
        if ("99908".equals(product_spec_code))
        {
            String cust_name = productParamInfos.getString("999084014");// 集团客户编码 通用统付是时候表示客户简称 定向统付的时候表示客户产品简称
            dataMap.put("CUST_NAME", cust_name);                  
            dataMap.put("SERVICE_ID", productParamInfos.getString("999084030", "123456"));// 预受理阶段没有此值，pre代表预受理阶段入表，归档时候会更新此值
        }
        if ("99909".equals(product_spec_code))
        {
            String cust_name = productParamInfos.getString("999094016");// 集团客户编码 通用统付是时候表示客户简称 定向统付的时候表示客户产品简称
            dataMap.put("CUST_NAME", cust_name);          
            dataMap.put("SERVICE_ID", "0"); // 通用业务为0；
        }
        dataMap.put("START_DATE", SysDateMgr.getSysTime());
        dataMap.put("END_DATE", SysDateMgr.getTheLastTime());
        dataMap.put("UPDATE_TIME", SysDateMgr.getSysTime());
        return dataMap;

    }

    /**
	 * @author chenmw
	 * @date 2016-11-22
	 * @description 国际流量统付，登记grp_cenpay表
     * @param productParamInfos
     * @param product_spec_code
     * @param merch_spec_code
     * @param productOfferingId
     * @param groupId
     * @param product_id
     * @param ec_serial_number
     * @param biz_mode
     * @param userid
     * @return
     * @throws Exception
     */
    public static IData insertGrpCenpayForInternational(IData productParamInfos, String product_spec_code, String merch_spec_code, String productOfferingId, String groupId, String product_id, String ec_serial_number, String biz_mode, String userid) throws Exception{
        IData dataMap = new DataMap();
        dataMap.put("USER_ID", userid);
        dataMap.put("INST_ID", SeqMgr.getInstId());
        dataMap.put("ACCEPT_MONTH", SysDateMgr.getCurMonth());
        dataMap.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
        dataMap.put("MP_GROUP_CUST_CODE", ec_serial_number);// 放置与账务约定标识 ec_serial_number
        dataMap.put("GROUP_ID", groupId);

        dataMap.put("BIZ_MODE", biz_mode);

        String province_code = BizEnv.getEnvString("crm.grpcorp.provincecode");// 获取当前省份编码
        dataMap.put("PROVINCE_CODE", province_code);

        dataMap.put("MERCH_SPEC_CODE", merch_spec_code);
        dataMap.put("PRODUCT_SPEC_CODE", product_spec_code);
        dataMap.put("PRODUCT_ID", product_id);
        dataMap.put("PRODUCT_OFFER_ID", productOfferingId);
        
        //国际流量统付业务
        dataMap.put("PAY_TYPE", "2");
        //指定用户全量统付 业务模式
        dataMap.put("OPER_TYPE", "3");
        
        String cust_name = productParamInfos.getString("999104016");
        dataMap.put("CUST_NAME",cust_name);
        //业务计费代码
        dataMap.put("SERVICE_ID", "0");
        
        dataMap.put("START_DATE", SysDateMgr.getSysTime());
        dataMap.put("END_DATE", SysDateMgr.getTheLastTime());
        dataMap.put("UPDATE_TIME", SysDateMgr.getSysTime());
        return dataMap;
    }

    
    /**
     * chenyi 2014-7-18 判断是否为流量叠加包订购
     * 
     * @param discntInfo
     * @param productid
     * @return
     * @throws Exception
     */
    public static boolean isBbossPaybiz(IDataset discntInfo, String productid) throws Exception
    {
        boolean isPayBiz = false;// 是否为订购流量叠加包标记
        boolean isFlux = isFluxTFBusiness(productid);
        String productSpecCode = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_ATTR_BIZ", new String[]
                { "ID", "ID_TYPE", "ATTR_CODE", "ATTR_OBJ" }, "ATTR_VALUE", new String[]
                { "1", "B", productid, "PRO" });// 获取集团产品编码
        if ((!(isFlux || StringUtils.equals("9101101", productSpecCode)) || IDataUtil.isEmpty(discntInfo)))
        {
            return isPayBiz;
        }

        // 循环资费，是不是有流量叠加包
        for (int i = 0; i < discntInfo.size(); i++)
        {
            IData merchPDst = discntInfo.getData(i);

            // 将资费编码转化为BBOSS侧的资费编号，转化后的资费编号不存在或者为空皆属于无效资费，不进行处理
            String discntCode = merchPDst.getString("DISCNT_CODE", "");// 本地ELEMENT_ID
            IDataset discntInfos = AttrBizInfoQry.getBizAttrByAttrValue(productid, "D", "FluxPay", discntCode, null);
            String modifytag = merchPDst.getString("MODIFY_TAG");// 本地ELEMENT_ID
            if (IDataUtil.isNotEmpty(discntInfos) && TRADE_MODIFY_TAG.Add.getValue().equals(modifytag))
            {
                isPayBiz = true;
                break;
            }
            else
            {

                continue;

            }

        }

        return isPayBiz;

    }

    /**
     * chenyi 2014-7-15 判断当前业务是否流量统付业务
     * 
     * @param productID
     * @return
     * @throws Exception
     */
    public static boolean isFluxTFBusiness(String productID) throws Exception
    {
        boolean isFlux = false;
        String product_spec_code = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_ATTR_BIZ", new String[]
        { "ID", "ID_TYPE", "ATTR_CODE", "ATTR_OBJ" }, "ATTR_VALUE", new String[]
        { "1", "B", productID, "PRO" });// 获取集团产品编码
        // 如果为流量统付产品，则需要同步字段给账务
        if ("99904".equals(product_spec_code) || "99905".equals(product_spec_code) 
        		||"99908".equals(product_spec_code) || "99909".equals(product_spec_code) 
                || "99910".equals(product_spec_code))
        {
            isFlux = true;
        }
        return isFlux;
    }

    public static String getFluxPrepayTag(String productId, IData productParam) throws Exception
    {
        String retValue = "";
        String product_spec_code = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_ATTR_BIZ", new String[]
                { "ID", "ID_TYPE", "ATTR_CODE", "ATTR_OBJ" }, "ATTR_VALUE", new String[]
                { "1", "B", productId, "PRO" });// 获取集团产品编码
        // 如果为流量统付产品，则需要同步字段给账务
        if ("99904".equals(product_spec_code) || "99905".equals(product_spec_code)
        		 ||"99908".equals(product_spec_code) || "99909".equals(product_spec_code) )
        {
            retValue = productParam.getString(product_spec_code + "4101");
        }

        return retValue;
    }
    /**
     * LIMIT_FEE 值转换 chenyi 2014-7-26
     * 
     * @param nf_limit
     * @return
     */
    private static int limitParsnt(String nf_limit)
    {

        String str1 = nf_limit.substring(nf_limit.length() - 2, nf_limit.length());
        int limit;

        if (StringUtils.equalsIgnoreCase("MB", str1))
        {
            limit = Integer.parseInt(nf_limit.substring(0, nf_limit.length() - 2)); // 把字符串转换为int
        }
        else
        {
            //limit = Integer.parseInt(nf_limit.substring(0, nf_limit.length() - 2)); // 把字符串转换为int
            //limit = limit * 1024;
        	limit = Integer.parseInt(nf_limit);
        }
        return limit;
    };

    /**
     * chenyi 2014-4-23 判断此产品是是通用流量统付还是定额流量统付，并查询出当前的业务模式
     * 
     * @throws Exception
     */
    public static String merchIdToProId(String product_spec_code, String userid) throws Exception
    {
        // 获取当前产品集团受理时的业务模式
        String operMode =product_spec_code +"4008";
        String operType = "";
        IDataset userInfos = UserAttrInfoQry.getUserAttrByUserInstType(userid, operMode);
        if (IDataUtil.isNotEmpty(userInfos))
        {
            operType = userInfos.getData(0).getString("ATTR_VALUE");
        }

        return operType;
    }

    /**
     * @Function:
     * @Description:归档时候更新GrpCenpay表字段
     * @param：
     * @return：
     * @throws：
     * @version:
     * @author:chenyi
     * @throws Exception
     * @date: 下午3:32:50 2014-9-5
     */
    public static void UpdateGrpCenpay(IData map, IDataset productParamInfoList, String trade_id, String productOfferingId) throws Exception
    {
    	String bizMode = map.getString("SI_BIZ_MODE", "");
        String  finishTime = map.getString("FINISH_TIME", "");

        String serviceId = "";
        String startDate = "";
        String endDate = "";
        for (int i = 0, sizeI = productParamInfoList.size(); i < sizeI; i++)
        {
            IData param = productParamInfoList.getData(i);
            if ("999044030".equals(param.getString("ATTR_CODE")) || "999084030".equals(param.getString("ATTR_CODE")))
            {
                serviceId = param.getString("ATTR_VALUE");
            }
            if ("999044024".equals(param.getString("ATTR_CODE")) || "999084024".equals(param.getString("ATTR_CODE")))
            {
                startDate = param.getString("ATTR_VALUE");
            }
            if ("999044025".equals(param.getString("ATTR_CODE")) || "999084025".equals(param.getString("ATTR_CODE")))
            {
                endDate = param.getString("ATTR_VALUE");
            }
        }
        // 时间中较晚时间为统付生效时间
        if (!"".equals(startDate))
        {

            long a = Long.valueOf(finishTime);
            long b = Long.valueOf(startDate);

            if (a >= b)
            {
                startDate = finishTime;
            }
        }
        else
        {
            startDate = finishTime;// 如果start_DATE为空则取归档时间
        }
        
        TradeGrpMerchpInfoQry.updateGrpCenpayByTradeId(trade_id, bizMode, serviceId, startDate, endDate, productOfferingId);
    }

}
