
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commoncomponent;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.group.common.GroupBaseConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.ProvinceUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeEcrecepProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeGrpMerchpInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commonmethod.GrpCommonBean;

/**
 * @description 该类用于落地报文，集团业务、成员业务和管理节点报文落地时如果落地报文的数据与本地台帐信息不一致，需要更新本地台帐
 * @author xunyl
 * @date 2013-09-27
 */

public class UpdateAttrInfoBean extends CSBizService
{

    private static final long serialVersionUID = 1L;

    /*
     * @description 新增产品参数台帐信息
     * @author xunyl
     * @date 2013-09-16
     */
    protected static void addAttrTradeInfo(IData productParamInfo, String tradeId) throws Exception
    {
        // 1- 定义产品参数的台帐对象
        IData attrTradeInfo = new DataMap();

        // 2- 添加产品参数台帐编号
        attrTradeInfo.put("TRADE_ID", tradeId);

        // 3- 添加产品参数实例编号
        attrTradeInfo.put("INST_ID", SeqMgr.getInstId());

        // 4- 添加产品参数实例类型
        attrTradeInfo.put("INST_TYPE", "P");

        // 5- 添加产品实例编号
        IDataset productTradeInfoList = TradeGrpMerchpInfoQry.qryMerchpInfoByTradeId(tradeId);

        IData productTradeInfo = productTradeInfoList.getData(0);
        String productInstId = productTradeInfo.getString("INST_ID");
        attrTradeInfo.put("RELA_INST_ID", productInstId);

        // 6- 添加产品参数编号
        String attrCode = productParamInfo.getString("ATTR_CODE");
        attrTradeInfo.put("ATTR_CODE", attrCode);

        // 7- 添加产品参数名称
        String attrName = productParamInfo.getString("ATTR_NAME");
        attrTradeInfo.put("RSRV_STR3", attrName);

        // 8- 添加产品参数属性值
        String attrValue = productParamInfo.getString("ATTR_VALUE");
        attrTradeInfo.put("ATTR_VALUE", attrValue);

        // 9- 添加产品参数属性组编号
        String attrGroup = productParamInfo.getString("ATTR_GROUP");
        attrTradeInfo.put("RSRV_STR4", attrGroup);

        // 10- 添加产品参数的状态类型
        attrTradeInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());

        // 11- 添加产品用户编号
        String productUserId = productTradeInfo.getString("USER_ID");
        attrTradeInfo.put("USER_ID", productUserId);

        // 12- BBOSS侧参数状态，服开拼报文用
        attrTradeInfo.put("RSRV_STR5", GroupBaseConst.PARMA_STATUS.PARAM_ADD.getValue());

        // 13- 添加开始时间
        attrTradeInfo.put("START_DATE", SysDateMgr.getSysDate());

        // 14- 添加结束时间
        attrTradeInfo.put("END_DATE", SysDateMgr.getEndCycle20501231());

        // 15 接受月份
        attrTradeInfo.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(tradeId));

        // 16- 产品参数入表
        Dao.insert("TF_B_TRADE_ATTR", attrTradeInfo,Route.getJourDb());
    }

    /*
     * @description 处理状态对应为新增的参数
     * @author xunyl
     * @date 2013-11-09
     */
    protected static void dealAddAttrInfo(IDataset productParamInfoList, IDataset attrTradeInfoList, IDataset attrInstInfoList, IData productParamInfo, String tradeId) throws Exception
    {
        IData attrTradeInfo = new DataMap();
        if (IDataUtil.isNotEmpty(attrTradeInfoList))
        {
            attrTradeInfo = getAttrTradeInfo(attrTradeInfoList, productParamInfo, TRADE_MODIFY_TAG.Add.getValue());
        }

        if (IDataUtil.isNotEmpty(attrTradeInfo))
        {
            String attrValue = productParamInfo.getString("ATTR_VALUE");
            String tradeAttrValue = attrTradeInfo.getString("ATTR_VALUE");
            // 台帐表能找到，而且值不一样，修改台帐表参数值
            if (!StringUtils.equals(attrValue, tradeAttrValue))
            {
                updateAttrTradeInfo(attrTradeInfo, attrValue);
            }
            // 台帐表能找到，值一样，则不做任何处理
            return;
        }
        else
        {
            IData attrUserInfo = new DataMap();
            if (IDataUtil.isNotEmpty(attrInstInfoList))
            {
                attrUserInfo = getAttrUserInfo(attrInstInfoList, productParamInfo);
            }

            // 台帐表不能找到，资料表亦不能找到，并且参数值不为null或者空串(集团公司下发归档报文会有这种情况)直接新增台帐
            if (IDataUtil.isEmpty(attrUserInfo))
            {
                if (StringUtils.isNotBlank(productParamInfo.getString("ATTR_VALUE")))
                {
                    addAttrTradeInfo(productParamInfo, tradeId);
                }
                return;
            }

            // 台帐表不能找到，资料表能找到，归档的参数中存在该参数被删除记录，直接新增台帐
            String userAttrValue = attrUserInfo.getString("ATTR_VALUE");
            IData qryDelAttrResult = getDelAttrInfo(productParamInfoList, productParamInfo, userAttrValue);
            boolean isExistDelAttr = qryDelAttrResult.getBoolean("isExistDelAttr");
            if (isExistDelAttr)
            {
                addAttrTradeInfo(productParamInfo, tradeId);
                return;
            }

            // 台帐表不能找到，资料表能找到，归档的参数中没有该参数被删除记录，资料表的值与新增的参数值一样，则不做任何处理
            boolean isEqual = qryDelAttrResult.getBoolean("isEqual");
            if (isEqual)
            {
                return;
            }

            // 台帐表不能找到，资料表能找到，归档的参数中没有该参数被删除记录，资料表的值与新增的参数值不一样，则抛出异常
            if (!isEqual)
            {
                String attrCode = productParamInfo.getString("ATTR_CODE");
                CSAppException.apperr(CrmUserException.CRM_USER_1011, attrCode);
            }
        }
    }

    /*
     * @description 更新本地产品参数的台账信息
     * @author xunyl
     * @date 2013-09-14
     */
    public static void dealAttrTradeInfo(IDataset productParamInfoList, String tradeId) throws Exception
    {
        // 1- 产品参数为空，直接退出处理
        if (IDataUtil.isEmpty(productParamInfoList))
        {
            return;
        }

        // 2- 根据台账编号获取对应的所有参数台账信息
        IDataset attrTradeInfoList = TradeAttrInfoQry.getTradeAttrByTradeId(tradeId);

        // 3- 根据台账编号获取对应的所有参数用户信息
        String productUserId = getUserIdByTradeId(tradeId);
        IDataset attrInstInfoList = UserAttrInfoQry.getUserAttrByUserId(productUserId);
        
        // 4- 一些业务需要根据配合省来过滤产品参数，这样该配合省就只会有本省的产品参数(如：一点支付业务)
        productParamInfoList = dealProductParamByProviceCode(productParamInfoList, tradeId);

        // 5- 循环单个属性进行处理
        for (int i = 0; i < productParamInfoList.size(); i++)
        {
            IData productParamInfo = productParamInfoList.getData(i);
            String paramState = productParamInfo.getString("STATE");
            if (StringUtils.equals(GroupBaseConst.PRODUCT_ATTR_STATUS_DESC.ATTR_ADD.getValue(), paramState))
            {
                dealAddAttrInfo(productParamInfoList, attrTradeInfoList, attrInstInfoList, productParamInfo, tradeId);
            }
            else if (StringUtils.equals(GroupBaseConst.PRODUCT_ATTR_STATUS_DESC.ATTR_DEL.getValue(), paramState))
            {
                dealDelAttrInfo(attrTradeInfoList, attrInstInfoList, productParamInfo, tradeId);
            }
        }
    }
    

    /*
     * @description 处理状态对应为删除的参数
     * @author xunyl
     * @date 2013-11-09
     */
    protected static void dealDelAttrInfo(IDataset attrTradeInfoList, IDataset attrInstInfoList, IData productParamInfo, String tradeId) throws Exception
    {
        IData attrTradeInfo = new DataMap();
        if (IDataUtil.isNotEmpty(attrTradeInfoList))
        {
            attrTradeInfo = getAttrTradeInfo(attrTradeInfoList, productParamInfo, TRADE_MODIFY_TAG.DEL.getValue());
        }
        // 台账表能找到，不做处理
        if (IDataUtil.isNotEmpty(attrTradeInfo))
        {
            return;
        }
        else
        {
            IData attrUserInfo = new DataMap();
            if (IDataUtil.isNotEmpty(attrInstInfoList))
            {
                attrUserInfo = getAttrUserInfo(attrInstInfoList, productParamInfo);
            }
            // 台帐表不能找到，资料表不能找到，不做处理
            if (IDataUtil.isEmpty(attrUserInfo))
            {
                return;
            }

            // 台帐表不能找到，资料表能找到，新增被删除参数的台账信息
            delAttrTradeInfo(attrUserInfo, tradeId);
        }
    }

    /*
     * @description 删除产品参数台帐信息
     * @author xunyl
     * @date 2013-09-16
     */
    protected static void delAttrTradeInfo(IData attrUserInfo, String tradeId) throws Exception
    {
        // 1- 添加产品参数台帐编号
        attrUserInfo.put("TRADE_ID", tradeId);

        // 2- 添加BBOSS侧参数状态，服开拼报文用
        attrUserInfo.put("RSRV_STR5", GroupBaseConst.PARMA_STATUS.PARAM_DEL.getValue());

        // 3- 添加受理时间
        String acceptMonth = StrUtil.getAcceptMonthById(tradeId);
        attrUserInfo.put("ACCEPT_MONTH", acceptMonth);

        // 4- 添加部门编号
        attrUserInfo.put("UPDATE_DEPART_ID", getVisit().getDepartId());

        // 5- 添加员工编号
        attrUserInfo.put("UPDATE_STAFF_ID", getVisit().getStaffId());

        // 6- 添加更新时间
        attrUserInfo.put("UPDATE_TIME", SysDateMgr.getSysDate());

        // 7- 添加结束时间
        attrUserInfo.put("END_DATE", SysDateMgr.getSysDate());

        // 8-添加
        attrUserInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());

        // 9- 产品参数入表
        Dao.insert("TF_B_TRADE_ATTR", attrUserInfo,Route.getJourDb());
    }

    /*
     * @description 获取属性对应的台账信息
     * @author xunyl
     * @date 2013-11-09
     */
    protected static IData getAttrTradeInfo(IDataset attrTradeInfoList, IData productParamInfo, String modifyTag) throws Exception
    {
        String attrGroup = GrpCommonBean.nullToString(productParamInfo.getString("ATTR_GROUP"));
        String attrCode = productParamInfo.getString("ATTR_CODE");

        for (int i = 0; i < attrTradeInfoList.size(); i++)
        {
            IData attrTradeInfo = attrTradeInfoList.getData(i);
            String tradeAttrCode = attrTradeInfo.getString("ATTR_CODE");
            String tradeAttrGroup = GrpCommonBean.nullToString(attrTradeInfo.getString("RSRV_STR4"));// 属性组标识
            String tradeModifyTag = attrTradeInfo.getString("MODIFY_TAG");
            if ((StringUtils.isBlank(attrGroup) && StringUtils.isBlank(tradeAttrGroup)) || ((StringUtils.isNotBlank(attrGroup) && StringUtils.isNotBlank(tradeAttrGroup)) && StringUtils.equals(attrCode, tradeAttrCode)))
            {
                if (StringUtils.equals(attrCode, tradeAttrCode) && StringUtils.equals(modifyTag, tradeModifyTag) && StringUtils.equals(attrGroup, tradeAttrGroup))
                {
                    return attrTradeInfo;
                }
            }
        }

        return new DataMap();
    }

    /*
     * @获取属性对应的资料信息
     * @author xunyl
     * @date 2013-11-09
     */
    protected static IData getAttrUserInfo(IDataset attrInstInfoList, IData productParamInfo) throws Exception
    {
        String attrGroup = GrpCommonBean.nullToString(productParamInfo.getString("ATTR_GROUP"));
        String attrCode = productParamInfo.getString("ATTR_CODE");

        for (int i = 0; i < attrInstInfoList.size(); i++)
        {
            IData attrUserInfo = attrInstInfoList.getData(i);
            String tradeAttrCode = attrUserInfo.getString("ATTR_CODE");
            String tradeAttrGroup = GrpCommonBean.nullToString(attrUserInfo.getString("RSRV_STR4"));// 属性组标识
            //关于新增IDC流量计费模式的需求_优化属性组问题
            //if ((StringUtils.isBlank(attrGroup) && StringUtils.isBlank(tradeAttrGroup)) || (!StringUtils.isBlank(attrGroup) && !StringUtils.isBlank(tradeAttrGroup)))
            if ((StringUtils.isBlank(attrGroup) && StringUtils.isBlank(tradeAttrGroup)) || ((StringUtils.isNotBlank(attrGroup) && StringUtils.isNotBlank(tradeAttrGroup)) && StringUtils.equals(attrCode, tradeAttrCode)))
            {
                if (StringUtils.equals(attrCode, tradeAttrCode) && StringUtils.equals(attrGroup, tradeAttrGroup))
                {
                    return attrUserInfo;
                }
            }
        }

        return new DataMap();
    }

    /*
     * @description 查询归档参数中该新增的参数是否存在有对应的被删除的记录
     * @author xunyl
     * @date 2013-09-15
     */
    protected static IData getDelAttrInfo(IDataset productParamInfoList, IData addParamInfo, String userAttrValue) throws Exception
    {
        // 1- 定义返回数据
        IData qryResult = new DataMap();

        // 2- 获取参数编号
        String addAttrCode = addParamInfo.getString("ATTR_CODE");

        // 3- 获取参数值
        String addAttrValue = addParamInfo.getString("ATTR_VALUE");

        // 4- 获取参数的属性组编号
        String addAttrGroup = addParamInfo.getString("ATTR_GROUP");

        // 5- 查询归档参数中是否有该参数被删除的记录
        for (int i = 0, sizei = productParamInfoList.size(); i < sizei; i++)
        {
            IData productParamInfo = productParamInfoList.getData(i);
            String state = productParamInfo.getString("STATE");
            String attrCode = productParamInfo.getString("ATTR_CODE");
            String attrGroup = productParamInfo.getString("ATTR_GROUP");
            // 三种场景需要结束本次循环:(1)参数编号不相等，非同一属性 (2)产品参数编号非删除状态 (3)新增的参数属于属性组，但本次循环的参数不属于属性组或者两者属性组编号不等
            if (!StringUtils.equals(GroupBaseConst.PRODUCT_ATTR_STATUS_DESC.ATTR_DEL.getValue(), state) || !StringUtils.equals(addAttrCode, attrCode) || (!StringUtils.isEmpty(addAttrGroup) && !addAttrGroup.equals(attrGroup)))
            {
                continue;
            }
            qryResult.put("isExistDelAttr", true);
            return qryResult;
        }

        // 6- 返回结果
        if (addAttrValue.equals(userAttrValue))
        {
            qryResult.put("isExistDelAttr", false);
            qryResult.put("isEqual", true);
        }
        else
        {
            qryResult.put("isExistDelAttr", false);
            qryResult.put("isEqual", false);
        }
        return qryResult;
    }

    /*
     * @description 根据台账编号获取对应的用户编号
     * @author xunyl
     * @date 2013-11-09
     */
    protected static String getUserIdByTradeId(String tradeId) throws Exception
    {
        IDataset MainTradeInfoList = TradeInfoQry.getMainTradeByTradeIdForGrp(tradeId);
        if (IDataUtil.isEmpty(MainTradeInfoList))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_853);
        }
        IData mainTradeInfo = MainTradeInfoList.getData(0);
        String userId = mainTradeInfo.getString("USER_ID");
        return userId;
    }

    /*
     * @description 更新产品参数台帐信息
     * @author xunyl
     * @date 2013-09-14
     */
    protected static void updateAttrTradeInfo(IData attrTradeInfo, String attrValue) throws Exception
    {
        // 1- 获取台帐编号
        String tradeId = attrTradeInfo.getString("TRADE_ID");

        // 2- 获取用户编号
        String userId = attrTradeInfo.getString("USER_ID");

        // 3- 获取参数类型
        String instType = attrTradeInfo.getString("INST_TYPE");

        // 4- 获取实例编号
        String instId = attrTradeInfo.getString("INST_ID");

        // 5- 获取参数编号
        String attrCode = attrTradeInfo.getString("ATTR_CODE");

        // 6- 获取参数状态
        String modefyTag = attrTradeInfo.getString("MODIFY_TAG");

        // 7- 获取属性组编号
        String attrGroup = attrTradeInfo.getString("RSRV_STR4");
        if (null == attrGroup || StringUtils.isEmpty(attrGroup))
        {
            attrGroup = null;
        }

        // 7- 更新产品参数值
        IData inparams = new DataMap();
        inparams.put("TRADE_ID", tradeId);
        inparams.put("USER_ID", userId);
        inparams.put("INST_TYPE", instType);
        inparams.put("INST_ID", instId);
        inparams.put("ATTR_CODE", attrCode);
        inparams.put("MODIFY_TAG", modefyTag);
        inparams.put("RSRV_STR4", attrGroup);
        inparams.put("ATTR_VALUE", attrValue);

        Dao.executeUpdateByCodeCode("TF_B_TRADE_ATTR", "UPD_ATTRVALUE_BY_PK", inparams, Route.getJourDb(Route.CONN_CRM_CG));
    }
    
    /** 
    * @Title: dealProductParamByProviceCode
    * @Description: 根据属性组去除非本配合省的产品数据
    * @param productParam
    * @param tradeId
    * @return 处理后的产品数据
    * @throws Exception    
    * @return IDataset
    * @author chenkh
    * @time 2014年12月25日
    */ 
    protected static IDataset dealProductParamByProviceCode(IDataset productParam, String tradeId) throws Exception
    {
        String provinceCode = ProvinceUtil.getProvinceCodeGrpCorp();
        // 1- 如果出现产品数据ATTR_CODE为空的极端情况，暂时直接返回，不做处理
        String attrCode = GrpCommonBean.nullToString(productParam.getData(0).getString("ATTR_CODE"));
        if (StringUtils.isEmpty(attrCode))
        {
            return productParam;
        }

        // 2- 判断是否为成员账单一点支付的情况并进行处理
        if (attrCode.length()>=5&&"99903".equals(attrCode.substring(0, 5)))
        {
            String attrGroup = IsCooperationProvice(productParam, provinceCode);
            // 2.1- 判断是否为配合省的情况，不是直接返回
            if (StringUtils.isEmpty(attrGroup))
            {
                return productParam;
            }
            // 2.2- 配合省需要循环处理数据，过滤掉其他的配合省产品参数
            productParam = dealProductData(productParam, attrGroup);
        }

        return productParam;
    }

    /**
     * @Title: dealProductData
     * @Description: 去除属性组中非本配合省数据
     * @param productParam
     * @param provinceCode
     * @return 返回处理后的结果
     * @throws Exception
     * @return IDataset
     * @author chenkh
     * @time 2015年3月16日
     */
    protected static IDataset dealProductData(IDataset productParam, String attrGroup) throws Exception
    {
        IDataset dealParam = new DatasetList();
        for (int i = 0, sizeI = productParam.size(); i < sizeI; i++)
        {
            IData tempData = productParam.getData(i);
            // 1- 取得属性组编号即为配合省编码
            String grpAttr = GrpCommonBean.nullToString(tempData.getString("ATTR_GROUP"));
            // 2- 如果属性组不为空且不为配合省编码，则直接舍去该属性。
            if (StringUtils.isNotEmpty(grpAttr) && !grpAttr.equals(attrGroup))
            {
                continue;
            }
            dealParam.add(tempData);
        }
        return dealParam;
    }

    /**
     * @Title: IsCooperationProvice
     * @Description: 判断是否为配合省,并返回ATTR_GROUP
     * @param data
     * @return 是返回配合省编号，否返回1+provinceCode。
     * @throws Exception
     * @return String
     * @author chenkh
     * @time 2014年12月25日
     */
    protected static String IsCooperationProvice(IDataset data, String provinceCode) throws Exception
    {
        for (int i = 0, sizeI = data.size(); i < sizeI; i++)
        {
            // 一点支付的判断，如果别的业务有需求，可以在下面添加扩展
            if ("999033720".equals(data.getData(i).getString("ATTR_CODE")) && provinceCode.equals(data.getData(i).getString("ATTR_VALUE")))
                return data.getData(i).getString("ATTR_GROUP");
        }
        return "1"+provinceCode;
    }
    
    public static void dealJKDTAttrTradeInfo(IDataset productParamInfoList, String tradeId) throws Exception
    {
        // 1- 产品参数为空，直接退出处理
        if (IDataUtil.isEmpty(productParamInfoList))
        {
            return;
        }

        // 2- 根据台账编号获取对应的所有参数台账信息
        IDataset attrTradeInfoList = TradeAttrInfoQry.getTradeAttrByTradeId(tradeId);

        // 3- 根据台账编号获取对应的所有参数用户信息
        String productUserId = getUserIdByTradeId(tradeId);
        IDataset attrInstInfoList = UserAttrInfoQry.getUserAttrByUserId(productUserId);
        
        // 4- 一些业务需要根据配合省来过滤产品参数，这样该配合省就只会有本省的产品参数(如：一点支付业务)
        productParamInfoList = dealProductParamByProviceCode(productParamInfoList, tradeId);

        // 5- 循环单个属性进行处理
        for (int i = 0; i < productParamInfoList.size(); i++)
        {
            IData productParamInfo = productParamInfoList.getData(i);
            String paramState = productParamInfo.getString("STATE");
            if (StringUtils.equals(GroupBaseConst.PRODUCT_ATTR_STATUS_DESC.ATTR_ADD.getValue(), paramState))
            {
            	dealJKDTAddAttrInfo(productParamInfoList, attrTradeInfoList, attrInstInfoList, productParamInfo, tradeId);
            }
            else if (StringUtils.equals(GroupBaseConst.PRODUCT_ATTR_STATUS_DESC.ATTR_DEL.getValue(), paramState))
            {
                dealDelAttrInfo(attrTradeInfoList, attrInstInfoList, productParamInfo, tradeId);
            }
        }
    }
    
    //集客大厅
    protected static void dealJKDTAddAttrInfo(IDataset productParamInfoList, IDataset attrTradeInfoList, IDataset attrInstInfoList, IData productParamInfo, String tradeId) throws Exception
    {
        IData attrTradeInfo = new DataMap();
        if (IDataUtil.isNotEmpty(attrTradeInfoList))
        {
            attrTradeInfo = getAttrTradeInfo(attrTradeInfoList, productParamInfo, TRADE_MODIFY_TAG.Add.getValue());
        }

        if (IDataUtil.isNotEmpty(attrTradeInfo))
        {
            String attrValue = productParamInfo.getString("ATTR_VALUE");
            String tradeAttrValue = attrTradeInfo.getString("ATTR_VALUE");
            // 台帐表能找到，而且值不一样，修改台帐表参数值
            if (!StringUtils.equals(attrValue, tradeAttrValue))
            {
                updateAttrTradeInfo(attrTradeInfo, attrValue);
            }
            // 台帐表能找到，值一样，则不做任何处理
            return;
        }
        else
        {
            IData attrUserInfo = new DataMap();
            if (IDataUtil.isNotEmpty(attrInstInfoList))
            {
                attrUserInfo = getAttrUserInfo(attrInstInfoList, productParamInfo);
            }

            // 台帐表不能找到，资料表亦不能找到，并且参数值不为null或者空串(集团公司下发归档报文会有这种情况)直接新增台帐
            if (IDataUtil.isEmpty(attrUserInfo))
            {
                if (StringUtils.isNotBlank(productParamInfo.getString("ATTR_VALUE")))
                {
                	addJKDTAttrTradeInfo(productParamInfo, tradeId);
                }
                return;
            }

            // 台帐表不能找到，资料表能找到，归档的参数中存在该参数被删除记录，直接新增台帐
            String userAttrValue = attrUserInfo.getString("ATTR_VALUE");
            IData qryDelAttrResult = getDelAttrInfo(productParamInfoList, productParamInfo, userAttrValue);
            boolean isExistDelAttr = qryDelAttrResult.getBoolean("isExistDelAttr");
            if (isExistDelAttr)
            {
            	addJKDTAttrTradeInfo(productParamInfo, tradeId);
                return;
            }

            // 台帐表不能找到，资料表能找到，归档的参数中没有该参数被删除记录，资料表的值与新增的参数值一样，则不做任何处理
            boolean isEqual = qryDelAttrResult.getBoolean("isEqual");
            if (isEqual)
            {
                return;
            }

            // 台帐表不能找到，资料表能找到，归档的参数中没有该参数被删除记录，资料表的值与新增的参数值不一样，则抛出异常
            if (!isEqual)
            {
                String attrCode = productParamInfo.getString("ATTR_CODE");
                CSAppException.apperr(CrmUserException.CRM_USER_1011, attrCode);
            }
        }
    }
    
    //集客大厅
    protected static void addJKDTAttrTradeInfo(IData productParamInfo, String tradeId) throws Exception
    {
        // 1- 定义产品参数的台帐对象
        IData attrTradeInfo = new DataMap();

        // 2- 添加产品参数台帐编号
        attrTradeInfo.put("TRADE_ID", tradeId);

        // 3- 添加产品参数实例编号
        attrTradeInfo.put("INST_ID", SeqMgr.getInstId());

        // 4- 添加产品参数实例类型
        attrTradeInfo.put("INST_TYPE", "P");

        // 5- 添加产品实例编号
        IDataset productTradeInfoList = TradeEcrecepProductInfoQry.qryJKDTMerchpInfoByTradeId(tradeId);

        IData productTradeInfo = productTradeInfoList.getData(0);
        String productInstId = productTradeInfo.getString("INST_ID");
        attrTradeInfo.put("RELA_INST_ID", productInstId);

        // 6- 添加产品参数编号
        String attrCode = productParamInfo.getString("ATTR_CODE");
        attrTradeInfo.put("ATTR_CODE", attrCode);

        // 7- 添加产品参数名称
        String attrName = productParamInfo.getString("ATTR_NAME");
        attrTradeInfo.put("RSRV_STR3", attrName);

        // 8- 添加产品参数属性值
        String attrValue = productParamInfo.getString("ATTR_VALUE");
        attrTradeInfo.put("ATTR_VALUE", attrValue);

        // 9- 添加产品参数属性组编号
        String attrGroup = productParamInfo.getString("ATTR_GROUP");
        attrTradeInfo.put("RSRV_STR4", attrGroup);

        // 10- 添加产品参数的状态类型
        attrTradeInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());

        // 11- 添加产品用户编号
        String productUserId = productTradeInfo.getString("USER_ID");
        attrTradeInfo.put("USER_ID", productUserId);

        // 12- BBOSS侧参数状态，服开拼报文用
        attrTradeInfo.put("RSRV_STR5", GroupBaseConst.PARMA_STATUS.PARAM_ADD.getValue());

        // 13- 添加开始时间
        attrTradeInfo.put("START_DATE", SysDateMgr.getSysDate());

        // 14- 添加结束时间
        attrTradeInfo.put("END_DATE", SysDateMgr.getEndCycle20501231());

        // 15 接受月份
        attrTradeInfo.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(tradeId));

        // 16- 产品参数入表
        Dao.insert("TF_B_TRADE_ATTR", attrTradeInfo,Route.getJourDb());
    }
}
