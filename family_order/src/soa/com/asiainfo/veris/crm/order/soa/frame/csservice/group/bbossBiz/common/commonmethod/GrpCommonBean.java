
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commonmethod;

import com.ailk.biz.util.StaticUtil;

import org.apache.log4j.Logger;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.group.common.GroupBaseConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UItemAInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductCompRelaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.trade.UTradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.MFileInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.AttrBizInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.BBossAttrQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PoRatePlanIcbQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductCompInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeBhQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeRelaBBInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.*;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commoncomponent.BbossDisAttrTransBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commoncomponent.GrpDisAttrTransBean;

import java.util.Iterator;

/**
 * @descripiton 该类用于放置集团BBOSS公用方法
 * @author xunyl
 * @date 2013-09-10
 */
public class GrpCommonBean
{
	private final static Logger logger = Logger.getLogger(GrpCommonBean.class);
    /*
     * @descripiton 发送报文前判断是否还有未处理的文件(没有做规则处理是因为管理流程和预受理转正式受理没有引入规则校验)
     * @author xunyl
     * @date 2013-11-21
     */
    public static String checkFileState(String fileId) throws Exception
    {
        if (StringUtils.isBlank(fileId))
        {
            return "";
        }

        IDataset fileInfoList = MFileInfoQry.qryFileInfoListByFileID(fileId);
        if (IDataUtil.isEmpty(fileInfoList))
        {
            CSAppException.apperr(GrpException.CRM_GRP_642);
        }
        IData fileInfo = fileInfoList.getData(0);
        String fileKind = fileInfo.getString("FILE_KIND");
        if ("".equals(fileKind) || !StringUtils.equals("9", fileKind))
        {
            CSAppException.apperr(GrpException.CRM_GRP_643);
        }

        String fileName = fileInfo.getString("FILE_NAME");
        String[] fileNameList = fileName.split("\\.");
        if (fileNameList.length <= 1)
        {
            String err = fileName+"附件无后缀名,格式错误,请重新上传!";
            CSAppException.apperr(GrpException.CRM_GRP_713,err);
        }
        StringBuilder resultFileName = new StringBuilder(fileId);
        resultFileName.append(".");
        resultFileName.append(fileNameList[fileNameList.length - 1]);
        return resultFileName.toString();
    }

    /*
     * @description 校验产品参数附件是否上传
     * @author chenyi
     * @date 2013-12-09
     */
    protected static String checkParamFileUpLoad(String attrCode, String attrValue) throws Exception
    {
        // 1- 检查参数是否为附件类型
        String paramType = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_BBOSS_ATTR", new String[]
                { "ATTR_CODE" }, "EDIT_TYPE", new String[]
                { attrCode });
        if (StringUtils.equals("UPLOAD", paramType) && !"6".equals(CSBizBean.getVisit().getInModeCode()))
        {
            attrValue = checkFileState(attrValue);
        }
        return attrValue;
    }
	 /**
     * 替换报文中的产品全网编码，改为全网编码+"_01"（统付）or 全网编码+"_02" (个付)   daidl
     * @param map
     * @throws Exception
     */
    public static void repalceProSpecCode(IData map) throws Exception {
    	String poNumber = (String) IDataUtil.getDatasetSpecl("RSRV_STR1", map).get(0); // BBOSS商品规格编号
    	IDataset dataset = CommparaInfoQry.getCommparaCode1("CSM", "9044", poNumber,null);//配置商品编码
    	logger.debug("dataset===================="+dataset);
    	if(IDataUtil.isEmpty(dataset)){
    		return;
    	}
        IDataset productNumberSet = IDataUtil.getDatasetSpecl("PRSRV_STR10", map); // 多条产品规格编号
        if(logger.isDebugEnabled()){
        	logger.debug("productNumberSet="+productNumberSet);
        }
        String proSpecCode ="";
        IDataset psubscribeIdSet = IDataUtil.getDatasetSpecl("PSUBSCRIBE_ID", map); // 产品订单号
        IDataset rsrvstr4Set = IDataUtil.getDatasetSpecl("RSRV_STR4", map); // 产品订购关系ID
        IDataset productOperList = IDataUtil.getDataset("RSRV_STR14", map);// 产品操作类型
        //1- 校验报文是否报错
        if (productNumberSet.size() != psubscribeIdSet.size())
        {
            // 其他错误，产品跟产品订单号长度不一致
            CSAppException.apperr(CrmUserException.CRM_USER_902);
        }
        if (rsrvstr4Set.size() != productNumberSet.size())
        {
            // 其他错误，产品跟产品订购关系编号长度不一致
            CSAppException.apperr(CrmUserException.CRM_USER_903);
        }
        IDataset newPrsrvStr10 = new DatasetList();
        //2- 企业彩铃产品特殊处理, 对每个产品分析，如果存在个付和统付则改变全网编码
        for (int i = 0, P = productNumberSet.size(); i < P; i++) {
            //全网产品编码
            proSpecCode = GrpCommonBean.nullToString(productNumberSet.get(i));
            //付费模式特征规格编码 attrCode
            String paraCode1 = dataset.getData(0).getString("PARA_CODE1");
            String oper = GrpCommonBean.nullToString(productOperList.getDataset(i).get(0));
            //3-1 新增和预受理通过报文中属性节点知道付费模式
            if (StringUtils.equals(oper, "1") || StringUtils.equals(oper, "10"))
            {
                IDataset proAttrCodeList = IDataUtil.getDataset("RSRV_STR15", map).getDataset(i);// 产品属性代码ATTR_CODE
                //3-1-1 获取付费模式code
                int j = 0;
                for (int sizeJ = proAttrCodeList.size(); j < sizeJ; j++) {
                    if (StringUtils.equals(paraCode1, proAttrCodeList.get(j).toString())) {
                        IDataset attrActionList = IDataUtil.getDataset("RSRV_STR18", map).getDataset(i);// 产品操作代码 action
                        String action = attrActionList.get(j).toString();
                        //操作编码为1（新增）的时候确定是本次操作的付费编码，为0的时候是删除不做处理
                        if (StringUtils.equals("1",action)) {
                            String feeType = IDataUtil.getDataset("RSRV_STR16", map).getDataset(i).get(j).toString();
                            proSpecCode = proSpecCode + "_" + feeType;
                            break;
                        }
                    }
                }            
            } 
            else {//3-2 变更注销通过查询资料获取付费模式
                //3-2-1 查询集团用户ID
                String proOfferId = rsrvstr4Set.get(i).toString();
                String user_id="";
                IDataset product_info = UserEcrecepProductInfoQry.getUserEcrecepProductByOfferId(proOfferId);
                if(IDataUtil.isNotEmpty(product_info)){
                    user_id = product_info.getData(0).getString("USER_ID");
                }else{
                    user_id = getMerchpUserIdByProdId(proOfferId);
                }
                if (StringUtils.isBlank(user_id)) {
                    CSAppException.apperr(CrmUserException.CRM_USER_783, proOfferId);
                }
                //3-2-2 查询集团用户下对应的付费模式编码 9104010008
                IDataset userAttrByUserId = UserAttrInfoQry.getUserAttrByUserId(user_id, paraCode1);
                if (IDataUtil.isEmpty(userAttrByUserId)) {
                    CSAppException.apperr(CrmUserException.CRM_USER_783, proOfferId);
                }
                String feeType = userAttrByUserId.getData(0).getString("ATTR_VALUE");
                proSpecCode = proSpecCode + "_" + feeType;
            }
            newPrsrvStr10.add(proSpecCode);
        }
        //4- 替换原有的产品编码
        if(newPrsrvStr10.size()==1){
        	 map.put("PRSRV_STR10", proSpecCode);//单个产品放String类型
        }else{
        	 map.put("PRSRV_STR10", newPrsrvStr10);
        }
        if(logger.isDebugEnabled()){
        	logger.debug("map="+map);
        }
    }


    /*
     * @description 处理产品计费号
     * @param1 productId指产品编号
     * @param2 serialNumber指初始化生成的虚拟号
     * @param3 paramList该产品对应的参数集
     * @author xunyl
     * @date 2013-06-06
     * @version_1 某些业务对SERIAL_NUMBER需要做特殊处理，例如网信业务和400业务，需要将集团长服务代码和400号码替换掉前台传递的SERIAL_NUMBER
     */
    public static String dealSerialNumber(String productId, String serialNumber, IDataset paramList) throws Exception
    {

        // 根据产品编号从TD_S_STATIC获取相关配置，结果集为空说明无需处理，非空则需要处理
        IDataset staticValue = StaticUtil.getList(null, "TD_S_STATIC", "DATA_ID", "PDATA_ID", new String[]
                { "TYPE_ID", "DATA_ID" }, new String[]
                { "BBOSS_SERIAL_NUMBER", productId });
        if (staticValue == null || staticValue.size() <= 0)
        {
            return serialNumber;
        }
        String pDataId = staticValue.getData(0).getString("PDATA_ID");// 参数编号
        for (int i = 0; i < paramList.size(); i++)
        {
            if (pDataId.equals(paramList.getData(i).getString("ATTR_CODE")))
            {
                serialNumber = paramList.getData(i).getString("ATTR_VALUE");
                break;
            }
        }
        return serialNumber;
    }

    /**
     * 由于预受理时新增台帐不会有资费表即tf_b_trade表的intf_id不会有资费表 而管理节点和管理报文发送会新增资费，需更新tf_b_trade表的intf_id字段 chenyi 2012-6-17
     *
     * @param tradeId
     * @throws Exception
     */
    public static void dealTradeIntfId(String tradeId) throws Exception
    {

        IDataset tradeInfoSet = TradeInfoQry.getMainTradeByTradeId(tradeId);

        if (IDataUtil.isEmpty(tradeInfoSet))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_825);
        }
        IData map = tradeInfoSet.getData(0);
        String intfId = map.getString("INTF_ID", "");
        if (StringUtils.indexOf(intfId, "TF_B_TRADE_GRP_MERCHP_DISCNT") == -1)
        {
            intfId += "TF_B_TRADE_GRP_MERCHP_DISCNT,";
        }
        if (StringUtils.indexOf(intfId, "TF_B_TRADE_DISCNT") == -1)
        {
            intfId += "TF_B_TRADE_DISCNT,";
        }
        map.clear();
        map.put("TRADE_ID", tradeId);
        map.put("INTF_ID", intfId);
        TradeInfoQry.updateTradeIntfID(map);

    }

    /*
     * @description 删除产品参数台账信息(目前供预受理转正式受理、管理节点用)
     * @author xunyl
     * @date 2013-10-25
     */
    public static void delAttrTradeInfo(String tradeId, String attrCode, String attrValue, String instType, String clearTag, String isNeedPf) throws Exception
    {
        IData inparm = new DataMap();
        inparm.put("TRADE_ID", tradeId);
        inparm.put("ATTR_CODE", attrCode);
        inparm.put("ATTR_VALUE", attrValue);
        inparm.put("INST_TYPE", instType);
        // 是否清除的标记(标记为"M"时需要清除)，管理节点是在服务开通侧下发应答报文时清除，预受理转正式受理是在发送受理保文时清除
        inparm.put("RSRV_STR1", clearTag);
        inparm.put("END_DATE", SysDateMgr.getSysDate("yyyy-MM-dd"));
        inparm.put("MODIFY_TAG", CSBaseConst.TRADE_MODIFY_TAG.DEL.getValue());
        inparm.put("RSRV_STR5", GroupBaseConst.PARMA_STATUS.PARAM_DEL.getValue());// BBOSS侧参数状态
        inparm.put("IS_NEED_PF", isNeedPf);// 1或者是空： 发指令
        TradeAttrInfoQry.updateBbossAttrState(inparm);
    }

    /*
     * @description 产品预受理阶段，根据规范应该过滤集团公司的产品资费信息
     * @author xunyl
     * @date 2013-09-06
     */
    private static void delPreDiscntInfo(IData map) throws Exception
    {
        // 1- 产品信息不存在直接退出
        IDataset productInfoList = map.getDataset("ORDER_INFO");
        if (IDataUtil.isEmpty(productInfoList))
        {
            return;
        }

        // 2- 非预受理操作直接退出
        String productOperType = productInfoList.getData(0).getData("BBOSS_PRODUCT_INFO").getString("PRODUCT_OPER_CODE");
        if (!GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_PREDEAL.getValue().equals(productOperType))
        {
            return;
        }

        // 3- 删过滤产品信息中的集团资费
        for (int i = 0; i < productInfoList.size(); i++)
        {
            IData productInfo = productInfoList.getData(i);
            IDataset elementInfoList = productInfo.getDataset("ELEMENT_INFO");
            if (IDataUtil.isEmpty(elementInfoList))
            {
                return;
            }
            for (int j = 0; j < elementInfoList.size(); j++)
            {
                IData elementInfo = elementInfoList.getData(j);
                String elementType = elementInfo.getString("ELEMENT_TYPE_CODE");
                if ("D".equals(elementType))
                {
                    String elementId = elementInfo.getString("ELEMENT_ID");
                    // 如果省内资费编号与集团公司资费编号存在对应关系，则属于集团侧资费，需过滤
                    String bbossElementId = GrpCommonBean.productToMerch(elementId, 1);
                    if (StringUtils.isNotBlank(bbossElementId))
                    {
                        elementInfoList.remove(elementInfo);
                        j--;
                    }
                }
            }
        }
    }

    /*
     * @description 拼装产品参数台账信息(目前供预受理转正式受理、管理节点用)
     * @author xunyl
     * @date 2013-10-25
     */
    public static IData getAttrTradeInfo(String tradeId, String productUserId, String relaInstId, String attrCode, String attrGroup, String attrName, String attrValue, String isNeedPf, String endDate, String instType) throws Exception
    {
        // 1- 定义参数台账信息
        IData attrTradeInfo = new DataMap();

        // 2- 添加台账编号
        attrTradeInfo.put("TRADE_ID", tradeId);

        // 3- 添加受理日期
        attrTradeInfo.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(tradeId));

        // 4- 添加产品用户编号
        attrTradeInfo.put("USER_ID", productUserId);

        // 5- 添加实例编号
        attrTradeInfo.put("INST_ID", SeqMgr.getInstId());

        // 6- 添加参数与产品的关联关系编号
        attrTradeInfo.put("RELA_INST_ID", relaInstId);

        // 7-添加参数编号
        attrTradeInfo.put("ATTR_CODE", attrCode);

        // 8- 添加参数名称
        attrTradeInfo.put("RSRV_STR3", attrName);

        // 9- 添加参数值(如果是上传附件，需要加入后缀名)
        attrValue = checkParamFileUpLoad(attrCode, attrValue);

        attrTradeInfo.put("ATTR_VALUE", attrValue);

        // 10- 添加参数状态
        attrTradeInfo.put("MODIFY_TAG", CSBaseConst.TRADE_MODIFY_TAG.Add.getValue());

        // 11- 添加参数类型标志
        attrTradeInfo.put("INST_TYPE", instType);

        // 12- 添加属性组编号
        attrTradeInfo.put("RSRV_STR4", attrGroup);

        // 13- 添加参数的开始时间与结束时间
        attrTradeInfo.put("START_DATE", SysDateMgr.getSysDate("yyyy-MM-dd"));
        attrTradeInfo.put("END_DATE", endDate);

        // 14- 添加更新时间、员工部门编号，员工编号
        attrTradeInfo.put("UPDATE_TIME", SysDateMgr.getSysDate("yyyy-MM-dd HH:mm:ss"));
        attrTradeInfo.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        attrTradeInfo.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());

        // 15- 添加是否发服开的标记
        attrTradeInfo.put("IS_NEED_PF", isNeedPf);

        // 16- 添加服开侧的参数状态
        attrTradeInfo.put("RSRV_STR5", GroupBaseConst.PARMA_STATUS.PARAM_ADD.getValue());

        // 17- 返回参数台账信息
        return attrTradeInfo;
    }

    /*
     * @description 拼装资费台账信息(目前供预受理转正式受理、管理节点用)
     * @author xunyl
     * @date 2013-10-25
     */
    public static IData getDiscntTradeInfo(String tradeId, String productUserId, String productId, String packageId, String elementId, String endDate) throws Exception
    {
        // 1- 定义资费台账信息
        IData discntTradeInfo = new DataMap();

        // 2- 添加台账编号
        discntTradeInfo.put("TRADE_ID", tradeId);

        // 3- 添加受理月份
        discntTradeInfo.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(tradeId));

        // 4- 添加参数状态
        discntTradeInfo.put("MODIFY_TAG", CSBaseConst.TRADE_MODIFY_TAG.Add.getValue());

        // 5- 添加用户编号
        discntTradeInfo.put("USER_ID", productUserId);

        // 6- 添加产品编号
        discntTradeInfo.put("PRODUCT_ID", productId);

        // 7- 添加包编号
        discntTradeInfo.put("PACKAGE_ID", packageId);

        // 8- 添加元素编号
        discntTradeInfo.put("DISCNT_CODE", elementId);

        // 9- 添加优惠标记
        discntTradeInfo.put("SPEC_TAG", "2");// 特殊优惠标记：0-正常产品优惠，1-特殊优惠，2-关联优惠

        // 10- 添加关联类型
        String relationTypeCode = GrpCommonBean.getRelationTypeCodeByProdId("", productId, false);
        discntTradeInfo.put("RELATION_TYPE_CODE", relationTypeCode);

        // 11- 添加实例编号
        discntTradeInfo.put("INST_ID", SeqMgr.getInstId());

        // 12- 添加开始时间、结束时间、更新时间
        discntTradeInfo.put("START_DATE", SysDateMgr.getSysDate("yyyy-MM-dd HH:mm:ss"));
        discntTradeInfo.put("END_DATE", endDate);
        discntTradeInfo.put("UPDATE_TIME", SysDateMgr.getSysDate("yyyy-MM-dd HH:mm:ss"));

        // 13- 添加员工编号和员工部门编号
        discntTradeInfo.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        discntTradeInfo.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());

        // 14- 添加用户标识A
        discntTradeInfo.put("USER_ID_A", "-1");// 用户标识A：关联服务中的A用户标识，通常为一集团用户或虚拟用户。对于非关联服务填-1。

        // 15- 返回资费台账信息
        return discntTradeInfo;
    }

    /**
     * 400产品 拼装资费包参数 语音路由下挂号码个数台帐信息 修改目的地号码数量这个属性时候，需要对语音路由下挂号码个数也进行修改，需要拼写出一条为ADD状态的新值台帐和一条为del状态的旧值台帐 chenyi 2014-2-12
     *
     * @param userId
     * @param attrcode
     * @return
     * @throws Exception
     */
    public static IData getDisParam(String userId, String attrcode, String insType, IData newParam) throws Exception
    {

        DataMap disticntParam = new DataMap();

        IDataset useInfoDataset = UserAttrInfoQry.getUserAttrByUserId(userId, attrcode);
        // 新值
        DataMap newValueMap = new DataMap();
        // 旧值
        DataMap oldValueMap = new DataMap();
        if (IDataUtil.isNotEmpty(useInfoDataset))
        {
            IData userInfo = useInfoDataset.getData(0);
            // 2- 添加参数类型
            oldValueMap.put("INST_TYPE", insType);

            // 3- 添加参数状态
            oldValueMap.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());

            // 4- 添加参数编号
            oldValueMap.put("ATTR_CODE", attrcode);

            // 5- 添加参数名称
            oldValueMap.put("RSRV_STR3", userInfo.getString("ATTR_NAME"));// 参数名称

            // 6- 添加参数值
            oldValueMap.put("ATTR_VALUE", userInfo.getString("ATTR_VALUE"));

            // 7- 添加属性组编号
            oldValueMap.put("RSRV_STR4", userInfo.getString("RSRV_STR4"));

            // 8- 添加开始时间
            oldValueMap.put("START_DATE", userInfo.getString("START_DATE"));

            // 9- 添加结束时间
            oldValueMap.put("END_DATE", SysDateMgr.getSysDate());

            // 10- 添加用户编号
            oldValueMap.put("USER_ID", userInfo.getString("USER_ID"));

            // 11- 添加实例编号
            oldValueMap.put("INST_ID", SeqMgr.getInstId());
            // 12- 资费
            oldValueMap.put("RELA_INST_ID", userInfo.getString("RELA_INST_ID"));

            oldValueMap.put("RSRV_STR5", "0");

            oldValueMap.put("ELEMENT_ID", userInfo.getString("ELEMENT_ID"));

            // 新值台帐
            // 2- 添加参数类型
            newValueMap.put("INST_TYPE", insType);

            // 3- 添加参数状态
            newValueMap.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());

            // 4- 添加参数编号
            newValueMap.put("ATTR_CODE", attrcode);

            // 5- 添加参数名称
            newValueMap.put("RSRV_STR3", userInfo.getString("ATTR_NAME"));// 参数名称

            // 6- 添加参数值
            newValueMap.put("ATTR_VALUE", newParam.getString("ATTR_VALUE"));

            // 7- 添加属性组编号
            newValueMap.put("RSRV_STR4", userInfo.getString("RSRV_STR4"));

            // 8- 添加开始时间
            newValueMap.put("START_DATE", SysDateMgr.getSysDate());

            // 9- 添加结束时间
            newValueMap.put("END_DATE", SysDateMgr.getTheLastTime());

            // 10- 添加用户编号
            newValueMap.put("USER_ID", userInfo.getString("USER_ID"));

            // 11- 添加实例编号
            newValueMap.put("INST_ID", SeqMgr.getInstId());
            newValueMap.put("RELA_INST_ID", userInfo.getString("RELA_INST_ID"));

            // 12- 添加BBOSS侧参数状态，服开拼报文用
            newValueMap.put("RSRV_STR5", "1");
            newValueMap.put("ELEMENT_ID", userInfo.getString("ELEMENT_ID"));

        }

        disticntParam.put("NewDisParam", newValueMap);
        disticntParam.put("OldDisParam", oldValueMap);

        return disticntParam;
    }

    /**
     * chenyi 12-10-28 查询ICB参数的名字
     *
     * @param distinctAttr
     * @return
     * @throws Exception
     */
    public static IDataset getDistinctAttrName(IDataset distinctAttr) throws Exception
    {

        IData map = new DataMap();
        if (IDataUtil.isNotEmpty(distinctAttr))
        {

            for (int i = 0, sizeI = distinctAttr.size(); i < sizeI; i++)
            {

                map = distinctAttr.getData(i);
                // 查询是否是ICB参数
                String number = map.getString("ATTR_CODE", "");
                IDataset IcbSet = PoRatePlanIcbQry.getIcbsByParameterNumber(number);
                if (IcbSet != null && IcbSet.size() > 0)
                {
                    map.put("ATTR_NAME", IcbSet.getData(0).getString("PARAMETERNAME", ""));
                }
                else
                {
                    map.put("IS_NEED_PF", "0");
                }
            }

        }
        return distinctAttr;
    }

    /*
     * @descriptin 获取产品一级信息，其中一级信息包含了二、三级信息
     * @author xunyl
     * @date 2013-06-24
     */
    public static void getFirstProductInfo(IData map, IData firstProductInfo) throws Exception
    {
        // 1- 拼装产品基本信息
        IDataset productNumberSet = IDataUtil.getDatasetSpecl("PRSRV_STR10", map); // 多条产品规格编号
        IDataset psubscribeIdSet = IDataUtil.getDatasetSpecl("PSUBSCRIBE_ID", map); // 产品订单号
        IDataset rsrvstr4Set = IDataUtil.getDatasetSpecl("RSRV_STR4", map); // 产品订购关系ID
        IDataset productOperList = IDataUtil.getDataset("RSRV_STR14", map);// 产品操作类型
        if (productNumberSet.size() != psubscribeIdSet.size())
        {
            // 其他错误，产品跟产品订单号长度不一致
            CSAppException.apperr(CrmUserException.CRM_USER_902);
        }
        if (rsrvstr4Set.size() != productNumberSet.size())
        {
            // 其他错误，产品跟产品订购关系编号长度不一致
            CSAppException.apperr(CrmUserException.CRM_USER_903);
        }
        firstProductInfo.put("PRO_NUMBER", productNumberSet);
        firstProductInfo.put("PRO_ORDER_NUMBER", psubscribeIdSet);
        firstProductInfo.put("PRO_ORDER_RELATION_NUMBER", rsrvstr4Set);
        firstProductInfo.put("PRODUCT_OPER", productOperList);
        
        logger.debug("=================productNumberSet================="+productNumberSet);
        logger.debug("=================productOperList================="+productOperList);

        // 2- 拼装产品资费信息(受理操作中，产品级资费操作代码和资费描述在拼台账没有用到，这里放入list中便于以后扩展)
        IDataset productRateList = IDataUtil.getDataset("RSRV_STR8", map);// 资费计划标识
        IDataset actionCv2List = IDataUtil.getDataset("ACTION_CV2", map);// 产品级资费操作代码
        IDataset feeDescList = IDataUtil.getDataset("FEE_DESC", map);// 资费描述
        String poRatePolicyEffRule =map.getString("PO_RATE_POLICY_EFF_RULE") ;// jkdt套餐生效规则  daidl
        String rsrv_str3=map.getString("RSRV_STR3") ;//bboss套餐生效规则daidl
        if (productRateList.size() != actionCv2List.size())
        {
            CSAppException.apperr(CrmUserException.CRM_USER_904);
        }
        firstProductInfo.put("RATE_PLAN_ID", productRateList);
        firstProductInfo.put("RATE_PLAN_ACTION", actionCv2List);
        firstProductInfo.put("RATE_PLAN_DESCRIPTION", feeDescList);
        firstProductInfo.put("PO_RATE_POLICY_EFF_RULE", poRatePolicyEffRule);
        firstProductInfo.put("RSRV_STR3", rsrv_str3);
        logger.debug("=======   poRatePolicyEffRule  ======="+ poRatePolicyEffRule   );

        // 3- 拼装ICB参数信息
        IDataset icbCodeList = IDataUtil.getDataset("RSRV_STR9", map);// 参数编码
        IDataset icbNameList = IDataUtil.getDataset("RSRV_STR10", map);// 参数名
        IDataset icbValueList = IDataUtil.getDataset("RSRV_STR11", map);// 参数值
        firstProductInfo.put("ICB_PARAM_CODE", icbCodeList);
        firstProductInfo.put("ICB_PARAM_NAME", icbNameList);
        firstProductInfo.put("ICB_PARAM_VALUE", icbValueList);

        // 4- 拼装产品属性信息
        IDataset proAttrCodeList = IDataUtil.getDataset("RSRV_STR15", map);// 产品属性代码
        IDataset attrNameList = IDataUtil.getDataset("RSRV_STR17", map);// 产品属性名
        IDataset attrValueList = IDataUtil.getDataset("RSRV_STR16", map);// 产品属性值
        IDataset attrActionList = IDataUtil.getDataset("RSRV_STR18", map);// 产品属性操作代码
        IDataset attrCggroupList = IDataUtil.getDataset("CGROUP", map);// 产品属性组代码
        if (attrNameList.size() != attrValueList.size() || attrNameList.size() != attrActionList.size() || attrNameList.size() != proAttrCodeList.size())
        {
            CSAppException.apperr(CrmUserException.CRM_USER_905);
        }
        firstProductInfo.put("PARAM_CODE", proAttrCodeList);
        firstProductInfo.put("PARAM_NAME", attrNameList);
        firstProductInfo.put("PARAM_VALUE", attrValueList);
        firstProductInfo.put("PARAM_ACTION", attrActionList);
        firstProductInfo.put("PARAM_CGROUP", attrCggroupList);

        // 5- 拼装产品一次性费用信息
        IDataset ProductOrderChargecode = IDataUtil.getDataset("PRODUCT_ORDER_CHARGE_CODE", map);// 一次性费用编码
        IDataset ProductOrderChargeValue = IDataUtil.getDataset("PRODUCT_ORDER_CHARGE_VALUE", map);// 一次性费用属性值
        if (ProductOrderChargecode.size() != ProductOrderChargeValue.size())
        {
            CSAppException.apperr(CrmUserException.CRM_USER_906);
        }
        firstProductInfo.put("PRODUCT_CHARGE_CODE", ProductOrderChargecode);
        firstProductInfo.put("PRODUCT_CHARGE_VALUE", ProductOrderChargeValue);
    }

    /*
     * @description 拼装BBOSS侧资费数据(MERCHP_DISCNT)，服开拼报文用(目前供预受理转正式受理、管理节点用)
     * @author xunyl
     * @date 2013-10-25
     */
    public static IData getMerchpDiscntTradeInfo(String tradeId, String merchSpecCode, String productOrderId, String productOfferId, String productSpecCode, String productDiscntCode, String productUserId, String instId, String endDate,
                                                 String isNeedPf) throws Exception
    {
        // 1- 定义BBOSS侧资费信息
        IData merchpDiscntTradeInfo = new DataMap();

        // 2- 添加台账编号
        merchpDiscntTradeInfo.put("TRADE_ID", tradeId);

        // 3- 添加爱商品规格编号
        merchpDiscntTradeInfo.put("MERCH_SPEC_CODE", merchSpecCode);

        // 4- 添加产品订单编号
        merchpDiscntTradeInfo.put("PRODUCT_ORDER_ID", productOrderId);

        // 5- 添加产品订购关系编号
        merchpDiscntTradeInfo.put("PRODUCT_OFFER_ID", productOfferId);

        // 6- 添加产品规格编号
        merchpDiscntTradeInfo.put("PRODUCT_SPEC_CODE", productSpecCode);

        // 7- 添加产品资费编码
        merchpDiscntTradeInfo.put("PRODUCT_DISCNT_CODE", productDiscntCode);

        // 8- 添加产品用户编号
        merchpDiscntTradeInfo.put("USER_ID", productUserId);

        // 9- 添加受理年月
        merchpDiscntTradeInfo.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(tradeId));

        // 10- 添加资费状态
        merchpDiscntTradeInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());

        // 11- 添加BBOSS侧参数状态，服开拼报文用
        merchpDiscntTradeInfo.put("RSRV_STR1", GroupBaseConst.PARMA_STATUS.PARAM_ADD.getValue());

        // 12- 添加实例标志(用trade_discnt表的实例标志，用于关联ICB参数)
        merchpDiscntTradeInfo.put("INST_ID", SeqMgr.getInstId());

        // 13- 添加开始时间、结束时间、更新时间
        merchpDiscntTradeInfo.put("START_DATE", SysDateMgr.getSysDate("yyyy-MM-dd HH:mm:ss"));
        merchpDiscntTradeInfo.put("END_DATE", endDate);
        merchpDiscntTradeInfo.put("UPDATE_TIME", SysDateMgr.getSysDate("yyyy-MM-dd HH:mm:ss"));

        // 14- 添加员工工号及部门编号
        merchpDiscntTradeInfo.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        merchpDiscntTradeInfo.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());

        // 15- 添加是否发送服开标志(1或者是空： 发指令)
        merchpDiscntTradeInfo.put("IS_NEED_PF", isNeedPf);

        // 16- 添加关联实例标志(对应为TRADE_DISCNT表的INST_ID)
        merchpDiscntTradeInfo.put("RELA_INST_ID", instId);

        return merchpDiscntTradeInfo;
    }

    /*
     * @description 根据产品订购关系编号获取产品用户编号
     * @author xunyl
     * @date 2013-07-03
     */
    public static String getMerchpUserIdByProdId(String productOfferingId) throws Exception
    {
        // 1- 根据产品订购关系编号获取对应的产品用户信息
        IDataset merchpUserInfos = UserGrpMerchpInfoQry.qryMerchpInfoByProductOfferId(productOfferingId);
        if (null == merchpUserInfos || merchpUserInfos.size() == 0)
        {
            CSAppException.apperr(CrmUserException.CRM_USER_914, productOfferingId);
        }

        // 2- 获取产品用户编号
        return merchpUserInfos.getData(0).getString("USER_ID");
    }

    /*
     * @description 根据产品台账编号获取商品台账信息
     * @author xunyl
     * @date 2013-10-18
     */
    public static IData getMerchTradeInfo(String productTradeId) throws Exception
    {
        // 1- 根据产品台账编号查询商产品BB关系
        IDataset tradeRelaBBInfoList = TradeRelaBBInfoQry.qryRelaBBInfoListByTradeIdForGrp(productTradeId);
        if (IDataUtil.isEmpty(tradeRelaBBInfoList))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_825);
        }

        // 2- 获取商品用户编号
        IData tradeRelaBBInfo = tradeRelaBBInfoList.getData(0);
        String merchUserId = tradeRelaBBInfo.getString("USER_ID_A");

        // 3- 根据商品用户编号，获取商品主台账编号
        IDataset merchTradeInfoList = TradeInfoQry.getMainTradeByUserId(merchUserId);
        if (IDataUtil.isEmpty(merchTradeInfoList))
        {
            // 如果不存在商品台账编号则到历史表捞取(专线业务一单多线场合，第一笔受理归档时商品台账就转历史表了)
            IDataset productTradeInfoList = TradeInfoQry.getMainTradeByTradeId(productTradeId);
            if (IDataUtil.isEmpty(productTradeInfoList))
            {
                CSAppException.apperr(CrmUserException.CRM_USER_853);
            }
            IData productTradeInfo = productTradeInfoList.getData(0);
            String tradeTypeCode = productTradeInfo.getString("TRADE_TYPE_CODE");
            merchTradeInfoList = TradeBhQry.queryTradeHis(merchUserId, tradeTypeCode);
            if (IDataUtil.isEmpty(merchTradeInfoList))
            {
                CSAppException.apperr(CrmUserException.CRM_USER_853);
            }
        }

        // 4- 获取商品台账信息
        IData merchTradeInfo = merchTradeInfoList.getData(0);

        // 5- 返回商品台账信息
        return merchTradeInfo;
    }

    /*
     * @description 根据商品订购关系编号获取商品用户编号
     * @author xunyl
     * @date 2013-07-01
     */
    public static String getMerchUserIdByProdOffId(String merchOfferingID) throws Exception
    {
        // 1- 根据商品订购关系编号获取对应的商品用户信息
        IDataset merchUserInfos = UserGrpMerchInfoQry.qryMerchInfoByMerchOfferId(merchOfferingID);
        if (null == merchUserInfos || merchUserInfos.size() == 0)
        {
            CSAppException.apperr(CrmUserException.CRM_USER_914, merchOfferingID);
        }

        // 2- 获取商品用户编号
        return merchUserInfos.getData(0).getString("USER_ID");
    }

    /**
     * chenyi 2014-4-18 过滤属性值，将add状态属性的值全不过滤出来
     *
     * @param paramDataset
     * @return
     */
    public static IDataset getOthererParam(IDataset paramDataset)
    {

        for (int i = paramDataset.size(); i > 0; i--)
        {
            IData param = paramDataset.getData(i - 1);
            String state = param.getString("STATE");
            if ("EXIST".equals(state) || "DEL".equals(state))
            {
                paramDataset.remove(i - 1);
            }
        }

        return paramDataset;
    }

    /*
     * @descripiton 获取产品资费的ICB参数信息
     * @author xunyl
     * @date 2013-06-26
     */
    public static void getProductIcbParamInfo(IData thirdProductInfo, IDataset productIcbParamInfo) throws Exception
    {
        // 1- 获取ICB参数编码集
        IDataset icbCodeList = thirdProductInfo.getDataset("ICB_PARAM_CODE");

        // 2- 获取ICB参数名称集
        IDataset icbNameList = thirdProductInfo.getDataset("ICB_PARAM_NAME");

        // 3- 获取ICB参数值集
        IDataset icbValueList = thirdProductInfo.getDataset("ICB_PARAM_VALUE");

        // 4- 校验ICB参数是否存在
        if (icbCodeList == null || icbValueList == null)
        {
            return;
        }

      
        // 5- 循环ICB参数编码集，封装ICB参数对象
        for (int i = 0; i < icbCodeList.size(); i++)
        {
            IData icbParam = new DataMap();
            icbParam.put("ATTR_CODE", GrpCommonBean.nullToString(icbCodeList.get(i)));
            icbParam.put("ATTR_VALUE", GrpCommonBean.nullToString(icbValueList.get(i)));
            icbParam.put("ATTR_NAME", GrpCommonBean.nullToString(icbNameList.get(i)));
            
            productIcbParamInfo.add(icbParam);
        }
    }

    /**
     * @description 根据用户编号获取对应的产品编号
     * @author xunyl
     * @date 2014-08-19
     */
    public static String getProductIdByUserId(String userId) throws Exception
    {
        // 1- 定义返回的产品编号
        String productId = "";

        // 2- 根据用户编号获取产品编号
        IDataset userProductInfoList = UserProductInfoQry.queryProductByUserId(userId);
        if (IDataUtil.isEmpty(userProductInfoList))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_1);
        }
        IData userProductInfo = userProductInfoList.getData(0);
        productId = userProductInfo.getString("PRODUCT_ID");

        // 3- 返回产品编号
        return productId;
    }

    /*
     * @description 获取产品对应的属性信息
     * @param1 productId指产品编号
     * @param2 secondProductInfo指IBOSS与CRM侧的二级信息 例如产品、资费与资费参数，产品为一级，资费为二级，资费参数为三级
     * @param3 产品对应的产品参数对象
     * @author xunyl
     * @date 2013-06-26
     */
    public static IDataset getProductParamInfo(String productId, IData secondProductInfo, IData productParam) throws Exception
    {
        // 1- 定义产品属性集
        IDataset productParamInfo = new DatasetList();

        // 2- 封装产品属性对象
        // 2-1 添加产品属性
        IDataset proAttrCodeList = secondProductInfo.getDataset("PARAM_CODE");// 产品属性代码
        IDataset attrNameList = secondProductInfo.getDataset("PARAM_NAME");// 产品属性名
        IDataset attrValueList = secondProductInfo.getDataset("PARAM_VALUE");// 产品属性值
        IDataset attrCggroupList = secondProductInfo.getDataset("PARAM_CGROUP");// 产品属性组代码
        IDataset attrActionList = secondProductInfo.getDataset("PARAM_ACTION"); 
        
        for (int i = 0; i < proAttrCodeList.size(); i++)
        {
            IData param = new DataMap();
            param.put("ATTR_CODE", nullToString(proAttrCodeList.get(i)));
            param.put("ATTR_NAME", nullToString(attrNameList.get(i)));
            param.put("ATTR_VALUE", nullToString(attrValueList.get(i)));
            param.put("ATTR_GROUP", nullToString(attrCggroupList.get(i)));
            if (StringUtils.equals("0", nullToString(attrActionList.get(i))))
            {
                // 反向落地时，报文中可能会出现参数值为null或者空串，而这些值没有必要入表
                if (StringUtils.isBlank(param.getString("ATTR_VALUE")))
                {
                    continue;
                }
                param.put("STATE", GroupBaseConst.PRODUCT_ATTR_STATUS_DESC.ATTR_DEL.getValue());
            }
            else if (StringUtils.equals("1", nullToString(attrActionList.get(i))))
            {
                // 反向落地时，报文中可能会出现参数值为null或者空串，而这些值没有必要入表
                if (StringUtils.isBlank(param.getString("ATTR_VALUE")))
                {
                    continue;
                }
                param.put("STATE", GroupBaseConst.PRODUCT_ATTR_STATUS_DESC.ATTR_ADD.getValue());
            }

            productParamInfo.add(param);
        }

        // 2-2 添加一次性费用
        IDataset ProductOrderChargecode = secondProductInfo.getDataset("PRODUCT_CHARGE_CODE");// 一次性费用编码
        IDataset ProductOrderChargeValue = secondProductInfo.getDataset("PRODUCT_CHARGE_VALUE");// 一次性费用属性值
        if (ProductOrderChargecode != null && ProductOrderChargeValue != null)
        {
            for (int j = 0; j < ProductOrderChargecode.size(); j++)
            {
                IData param = new DataMap();
                param.put("ATTR_CODE", "ONCEFEE_" + nullToString(ProductOrderChargecode.get(j)));
                param.put("ATTR_NAME", nullToString(ProductOrderChargecode.get(j)));
                param.put("ATTR_VALUE", nullToString(ProductOrderChargeValue.get(j)));
                param.put("ATTR_GROUP", "");// 默认为""
                param.put("STATE", "ADD");// 默认为新增
                productParamInfo.add(param);
            }
        }

        // 3- 添加属性集至返回对象
        productParam.put("PRODUCT_ID", productId);
        productParam.put("PRODUCT_PARAM", productParamInfo);

        // 4- 返回产品属性集
        return productParamInfo;
    }

    /**
     * chenyi
     * 拼写商品资费参数信息
     * @param merchIcbParamInfo
     * @param elementId
     * @param icbCodeList
     * @param icbNameList
     * @param icbValueList
     * @throws Exception
     */
    public static void getMerchIcbParamInfo(IDataset merchIcbParamInfo,String elementId,IDataset icbCodeList,IDataset icbNameList,IDataset icbValueList)throws Exception
    {

        //1-查询td_b_attr_itema配置参数
        IDataset  localItemaList= UItemAInfoQry.qryOfferChaSpecsByIdAndIdType("D",elementId,"ZZZZ");
        // 4- 校验ICB参数是否存在
        if (IDataUtil.isEmpty(icbCodeList)||IDataUtil.isEmpty(localItemaList))
        {
            return;
        }

        for(int i=0,sizeI=localItemaList.size();i<sizeI;i++){
            IData  localItema=localItemaList.getData(i);
            String localAttrCode=localItema.getString("ATTR_CODE");
            for(int j=0,sizeJ=icbCodeList.size();j<sizeJ;j++){
                String attrCode=GrpCommonBean.nullToString(icbCodeList.get(j));
                if(localAttrCode.equals(attrCode)){
                    IData icbParam = new DataMap();
                    icbParam.put("ATTR_CODE", GrpCommonBean.nullToString(icbCodeList.get(j)));
                    icbParam.put("ATTR_VALUE", GrpCommonBean.nullToString(icbValueList.get(j)));
                    icbParam.put("ATTR_NAME", GrpCommonBean.nullToString(icbNameList.get(j)));
                    merchIcbParamInfo.add(icbParam);
                }
            }
        }


    }
    /*
     * @description 根据商品台帐编号获取产品台账信息
     * @author xunyl
     * @date 2013-10-31
     */
    public static IDataset getProductTradeInfo(String merchTradeId) throws Exception
    {
        // 1- 根据商品台账编号获取相应的产品台账编号
        IDataset merchTradeInfoList = TradeInfoQry.getMainTradeByTradeId(merchTradeId);
        if (IDataUtil.isEmpty(merchTradeInfoList))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_853);
        }
        IData merchTradeInfo = merchTradeInfoList.getData(0);
        String orderId = merchTradeInfo.getString("ORDER_ID");

        // 2- 根据ORDER_ID查找所有的产品台帐信息
        IDataset productTradeInfoList = UTradeInfoQry.qryTradeByOrderId(orderId, Route.CONN_CRM_CG);
        if (IDataUtil.isEmpty(productTradeInfoList))
        {
            return new DatasetList();
        }
        for (int i = 0; i < productTradeInfoList.size(); i++)
        {
            IData productTradeInfo = productTradeInfoList.getData(i);
            String productTradeId = productTradeInfo.getString("TRADE_ID");
            if (merchTradeId.equals(productTradeId))
            {
                productTradeInfoList.remove(i);
                i--;
            }
        }

        // 3- 返回产品台帐信息
        return productTradeInfoList;
    }

    /**
     * @description 根据商品/产品编号获取商品/产品的关系编码
     * @author xunyl
     * @date 2014-08-19
     */
    public static String getRelationTypeCodeByProdId(String merchId, String merchpId, boolean isMerch) throws Exception
    {
        // 1- 定义返回的realtionTypeCode
        String relationTypeCode = "";

        // 2- 商品编号和产品编号都为空则直接返回""
        if (StringUtils.isBlank(merchId) && StringUtils.isBlank(merchpId))
        {
            return relationTypeCode;
        }

        // 3- 返回商品的realtionTypeCode
        if (isMerch)
        {
            if (StringUtils.isNotBlank(merchId))
            {
                relationTypeCode = ProductCompInfoQry.getRelaTypeCodeByProductId(merchId);
            }
            else
            {
                IDataset productCompRelaInfoList = UProductCompRelaInfoQry.queryProductRealByProductB(merchpId);
                if (IDataUtil.isNotEmpty(productCompRelaInfoList))
                {
                    merchId = productCompRelaInfoList.getData(0).getString("PRODUCT_ID_A");
                    relationTypeCode = ProductCompInfoQry.getRelaTypeCodeByProductId(merchId);
                }
            }
            return relationTypeCode;
        }

        // 4- 返回产品的realtionTypeCode
        if (StringUtils.isNotBlank(merchpId))
        {
            relationTypeCode = ProductCompInfoQry.getRelaTypeCodeByProductId(merchpId);
        }
        else
        {
            IDataset productCompRelaInfoList = UProductCompRelaInfoQry.getCompReleInfo(merchId);
            if (IDataUtil.isNotEmpty(productCompRelaInfoList))
            {
                merchpId = productCompRelaInfoList.getData(0).getString("PRODUCT_ID_B");
                relationTypeCode = ProductCompInfoQry.getRelaTypeCodeByProductId(merchpId);
            }
        }
        return relationTypeCode;
    }

    /*
     * @descriptin 获取产品二级信息，其中二级信息包含了三级信息
     * @author xunyl
     * @date 2013-06-25
     */
    public static void getSecondProductInfo(int index, IData firstProductInfo, IData secondProductInfo) throws Exception
    {
        // 1-拼装产品操作类型
        IDataset productOperList = firstProductInfo.getDataset("PRODUCT_OPER");// 产品操作类型
        secondProductInfo.put("PRODUCT_OPER", IDataUtil.modiIDataset(productOperList.get(index), "RSRV_STR14"));

        // 2- 拼装资费信息
        IDataset ratePlanIdList = firstProductInfo.getDataset("RATE_PLAN_ID");// 资费计划标识
        IDataset ratePlanActionList = firstProductInfo.getDataset("RATE_PLAN_ACTION");// 产品级资费操作代码
        IDataset ratePalnDesList = firstProductInfo.getDataset("RATE_PLAN_DESCRIPTION");// 资费描述
        secondProductInfo.put("RATE_PLAN_ID", IDataUtil.modiIDataset(ratePlanIdList.get(index), "RSRV_STR8"));
        secondProductInfo.put("RATE_PLAN_ACTION", IDataUtil.modiIDataset(ratePlanActionList.get(index), "ACTION_CV2"));
        secondProductInfo.put("RATE_PLAN_DESCRIPTION", IDataUtil.modiIDataset(ratePalnDesList.get(index), "FEE_DESC"));

        secondProductInfo.put("PO_RATE_POLICY_EFF_RULE",firstProductInfo.getString("PO_RATE_POLICY_EFF_RULE"));//jkdt套餐生效规则   daidl
        secondProductInfo.put("RSRV_STR3",firstProductInfo.getString("RSRV_STR3"));//bboss套餐生效规则   daidl
   
        logger.debug("=======  PO_RATE_POLICY_EFF_RULE  ======="+firstProductInfo.getString("PO_RATE_POLICY_EFF_RULE"));
        
        // 3- 拼装ICB参数信息
        IDataset icbCodeList = firstProductInfo.getDataset("ICB_PARAM_CODE");// 参数编码
        IDataset icbNameList = firstProductInfo.getDataset("ICB_PARAM_NAME");// 参数名
        IDataset icbValueList = firstProductInfo.getDataset("ICB_PARAM_VALUE");// 参数值
        secondProductInfo.put("ICB_PARAM_CODE", IDataUtil.modiIDataset(icbCodeList.get(index), "RSRV_STR9"));
        secondProductInfo.put("ICB_PARAM_NAME", IDataUtil.modiIDataset(icbNameList.get(index), "RSRV_STR10"));
        secondProductInfo.put("ICB_PARAM_VALUE", IDataUtil.modiIDataset(icbValueList.get(index), "RSRV_STR11"));

        // 4- 拼装产品属性信息
        IDataset proAttrCodeList = firstProductInfo.getDataset("PARAM_CODE");// 产品属性代码
        IDataset attrNameList = firstProductInfo.getDataset("PARAM_NAME");// 产品属性名
        IDataset attrValueList = firstProductInfo.getDataset("PARAM_VALUE");// 产品属性值
        IDataset attrActionList = firstProductInfo.getDataset("PARAM_ACTION");// 产品属性操作代码
        IDataset attrCggroupList = firstProductInfo.getDataset("PARAM_CGROUP");// 产品属性组代码
        secondProductInfo.put("PARAM_CODE", IDataUtil.modiIDataset(proAttrCodeList.get(index), "RSRV_STR15"));
        secondProductInfo.put("PARAM_NAME", IDataUtil.modiIDataset(attrNameList.get(index), "RSRV_STR17"));
        secondProductInfo.put("PARAM_VALUE", IDataUtil.modiIDataset(attrValueList.get(index), "RSRV_STR16"));
        secondProductInfo.put("PARAM_ACTION", IDataUtil.modiIDataset(attrActionList.get(index), "RSRV_STR18"));
        secondProductInfo.put("PARAM_CGROUP", IDataUtil.modiIDataset(attrCggroupList.get(index), "CGROUP"));

        // 5- 拼装产品一次性费用信息
        IDataset ProductOrderChargecode = firstProductInfo.getDataset("PRODUCT_CHARGE_CODE");// 一次性费用编码
        IDataset ProductOrderChargeValue = firstProductInfo.getDataset("PRODUCT_CHARGE_VALUE");// 一次性费用属性值
        if (null == ProductOrderChargecode || ProductOrderChargeValue.size() == 0)
        {
            return;
        }
        secondProductInfo.put("PRODUCT_CHARGE_CODE", IDataUtil.modiIDataset(ProductOrderChargecode.get(index), "PRODUCT_ORDER_CHARGE_CODE"));
        secondProductInfo.put("PRODUCT_CHARGE_VALUE", IDataUtil.modiIDataset(ProductOrderChargeValue.get(index), "PRODUCT_ORDER_CHARGE_VALUE"));
    }

    /*
     * @description 获取产品三级信息 目前仅有产品的ICB参数信息
     * @author xunyl
     * @date 2013-06-26
     */
    public static void getThirdProductInfo(int index, IData secondProductInfo, IData thirdProductInfo) throws Exception
    {
        // 拼装产品ICB参数信息
        IDataset ratePlanIdList = secondProductInfo.getDataset("RATE_PLAN_ID");// 资费计划标识
        IDataset ratePlanActionList = secondProductInfo.getDataset("RATE_PLAN_ACTION");// 产品级资费操作代码
        IDataset icbCodeList = secondProductInfo.getDataset("ICB_PARAM_CODE");//
        IDataset icbNameList = secondProductInfo.getDataset("ICB_PARAM_NAME");// 产品级资费操作代码
        IDataset icbValueList = secondProductInfo.getDataset("ICB_PARAM_VALUE");// 资费描述

        if (ratePlanIdList.size() == 0 || ratePlanActionList.size() == 0)
        {
            return;
        }
        thirdProductInfo.put("ICB_PARAM_CODE", IDataUtil.modiIDataset(icbCodeList.get(index), "RSRV_STR9"));
        thirdProductInfo.put("ICB_PARAM_NAME", IDataUtil.modiIDataset(icbNameList.get(index), "RSRV_STR10"));
        thirdProductInfo.put("ICB_PARAM_VALUE", IDataUtil.modiIDataset(icbValueList.get(index), "RSRV_STR11"));
    }

    /*
     * @desctiption 将产品参数类型由IData向IDataset转化
     * @author xunyl
     * @Date 2013-04-16
     */
    public static IDataset merchPParamsToDataset(IData paramMap) throws Exception
    {
        IDataset productParams = new DatasetList();
        Iterator<String> iterator = paramMap.keySet().iterator();
        while (iterator.hasNext())
        {
            String mapKey = iterator.next();
            IData valueMap = paramMap.getData(mapKey);
            productParams.add(valueMap);
        }
        return productParams;
    }

    /**
     * @description 集团公司商产品规格转省BOSS商产品规格
     * @author xunyl
     * @date 2013-06-21
     */
    public static String merchToProduct(String elementId, int mode, String productId) throws Exception
    {
        // mode 0是商品，1是资费，2是产品
        // 1- 定义查询的元素类型
        String attrObj = "";
        String attrCode = "";
        if (mode == 1 && productId != null)
        {
            return merchToProductByPid(elementId, mode, productId);
        }
        else
        {
            if("010190008".equals(elementId) || "010190009".equals(elementId)
                    || (mode == 1 && "878".equals(elementId)))
            {
                CSAppException.apperr(CrmUserException.CRM_USER_3008, elementId);

            }
            attrObj = "PRO";// 商品、产品ID

            // 根据商品编号查询产品信息
            IDataset datas = AttrBizInfoQry.getBizAttrByAttrValue("1", "B", attrObj, elementId, null);
            // 老商产品模式 商产品集团订单号 不一致
            if (datas != null && datas.size() == 1)
            {
                attrCode = datas.getData(0).getString("ATTR_CODE", "");
            }// 商产品订单号一致
            else if (datas != null && datas.size() > 1)
            {
                for (int i = 0; i < datas.size(); i++)
                {
                    if (mode == 0 && "MERCHID".equals(datas.getData(i).getString("RSRV_STR5")))
                    {
                        attrCode = datas.getData(i).getString("ATTR_CODE", "");// 商品id
                    }
                    else if (mode == 2)
                    {
                        attrCode = datas.getData(i).getString("ATTR_CODE", "");// 产品id
                    }
                }
            }
            else
            {
                CSAppException.apperr(CrmUserException.CRM_USER_900, elementId);
            }
        }

        return attrCode;
    }

    /**
     * @description 针对集团资费对应本地资费多条的情况，特做此修改
     * @author xuxf
     * @date 2014-06-06
     */
    private static String merchToProductByPid(String elementId, int mode, String productId) throws Exception
    {
        String attrCode = "";
        String attrObj = "";

        attrObj = "DIS";// 商品、产品资费，商品套餐

        // 1获取对应本地元素编码
        IDataset datas = getElemetInfo(elementId, mode, productId, attrObj);
        if (IDataUtil.isNotEmpty(datas))
        {
            IData data = datas.getData(0);
            attrCode = data.getString("ATTR_CODE", "");
        }
        else
        {
            CSAppException.apperr(CrmUserException.CRM_USER_900, elementId);
        }

        return attrCode;
    }

    /**
     * @Function:
     * @Description:获取本地元素编码
     * @author:chenyi
     * @date: 下午3:32:50 2014-9-26
     */
    private static IDataset getElemetInfo(String rateId, int mode, String productId, String attrObj) throws Exception
    {
        IDataset ids = new DatasetList();
        IDataset localInfo = AttrBizInfoQry.getBizAttrByAttrValue("1", "B", attrObj, rateId, null);
        if (IDataUtil.isEmpty(localInfo))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_900, rateId);
        }
        for (int i = 0, sizeI = localInfo.size(); i < sizeI; i++)
        {
            IData data = localInfo.getData(i);
            String attr_code = data.getString("ATTR_CODE");
            // 场景1 根据集团资费查找本地packageId对应是集团套餐编码(attr_biz表的attr_value配置为对应商品的集团资费套餐编码package_id)
            //为集团提供 为产品编码，包ID。查询包信息
            IDataset packageInfos = UpcCall.queryOfferGroupRelOfferIdGroupId(BofConst.ELEMENT_TYPE_CODE_PRODUCT, productId,attr_code, "Y");
            if (IDataUtil.isNotEmpty(packageInfos))
            {
                ids.add(data);
                break;
            }
            else
            {
                // 场景2 根据集团资费查找本地元素 element对应是集团资费编码(attr_biz表的attr_value配置为对应产品的集团资费编码element_id)
                //为集团提供 为产品编码，元素编码。查询元素信息
                IDataset elementInfos = UpcCall.qryOfferByOfferIdRelOfferId(productId, BofConst.ELEMENT_TYPE_CODE_PRODUCT, attr_code, BofConst.ELEMENT_TYPE_CODE_DISCNT);
                if (IDataUtil.isNotEmpty(elementInfos))
                {
                    ids.add(data);
                    break;
                }
            }
        }

        return ids;
    }

    /**
     * @description 某些业务的特殊性，需要将拼好的标准结构进行特殊处理
     * @author xunyl
     * @date 2014-08-06
     */
    public static void modifyInparamsBySpecialBiz(IData map, String merchOperType) throws Exception
    {
        //1- 产品预受理阶段(正向)，根据规范过滤集团公司的产品资费信息
        IData merchInfo = map.getData("MERCH_INFO");
        String inModeCode = merchInfo.getString("IN_MODE_CODE");
        if (GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_ADD.getValue().equals(merchOperType) && !StringUtils.equals(inModeCode, "6"))
        {
            delPreDiscntInfo(map);
        }

        // 2- 公众服务云业务需要根据产品参数中的折扣来添加资费折扣
        String bossMerchSpecNum = merchInfo.getString("PRODUCT_ID");
        String bbossMerchSpecNum = productToMerch(bossMerchSpecNum, 0);
        if ("1010402".equals(bbossMerchSpecNum))
        {
            BbossDisAttrTransBean.addDisAttrByProductAttr(map, merchOperType);
        }


        //3- 省内IDC流量计费业务省内资费转产品参数/省内资费属性转产品参数
        if("50005".equals(bbossMerchSpecNum) && !StringUtils.equals(inModeCode, "6")){
            GrpDisAttrTransBean.disDataToAttrData(map, merchOperType);
        }

        //4- 云客服需要根据产品属性参数中的坐席数量添加到资费属性参数中
        if("01114001".equals(bbossMerchSpecNum)){
            GrpDisAttrTransBean.attrDataToDisData(map, merchOperType);
        }
    }

    /**
     * @description 将null转化为空串
     * @author xunyl
     * @date 2013-06-25
     */
    public static String nullToString(Object obj) throws Exception
    {
        if (null == obj)
        {
            return "";
        }
        else
        {
            return obj.toString();
        }
    }

    /**
     * @description 省BOSS产品/资费转BBOSS产品/资费
     * @author xunyl
     * @date 2014-08-06
     */
    public static String productToMerch(String productId, int mode) throws Exception
    {
        String attrObj = "";
        if (mode == 0)
        { // 商品、产品ID
            attrObj = "PRO";
        }
        else if (mode == 1)
        { // 商品、产品资费，商品套餐
            attrObj = "DIS";
        }

        IDataset datas = AttrBizInfoQry.getBizAttr("1", "B", attrObj, productId, null);

        String temp = null;
        if (datas != null && datas.size() > 0)
        {
            temp = datas.getData(0).getString("ATTR_VALUE", "");
        }
        return temp;
    }

    /**
     * 存一个空的附件、审批人、联系人信息 不会有数据，但不会报错
     *
     * @author weixb3
     * @return
     * @throws Exception
     */
    public static IData putEmptyGoodInfo() throws Exception
    {
        IData good_info = new DataMap();
        IDataset att_infos = new DatasetList();
        good_info.put("ATT_INFOS", att_infos);
        IDataset auditor_infos = new DatasetList();
        good_info.put("AUDITOR_INFOS", auditor_infos);
        IDataset contactor_infos = new DatasetList();
        good_info.put("CONTACTOR_INFOS", contactor_infos);

        return good_info;
    }

    /**
     * @Title: isMandatroyAttribute
     * @Description: 判断属性在当前操作下是否必选
     * @param attrCode
     * @param operCode
     * @return 是为true，否为false
     * @throws Exception
     * @return boolean
     * @author chenkh
     * @time 2014年12月26日
     */
    public static boolean isMandatroyAttribute(String attrCode, String operCode) throws Exception
    {
        IDataset attrInfo = BBossAttrQry.qryBBossAttrMandatoryByOperCodeAndAttrCode(attrCode, operCode);
        if (IDataUtil.isEmpty(attrInfo))
        {
            return false;
        }
        return true;
    }

    /**
     * @description 根据用户编号和属性编号获取属性值
     * @author xunyl
     * @date 2015-06-27
     */
    public static String getAttrValueByAttrCode(String grpProductUserId,String attrCode)throws Exception{
        //1- 初始化返回值
        String attrValue = "";

        //2- 根据用户编号和属性编号获取参数用户资料信息
        IDataset userAttrInfoList = UserAttrInfoQry.getUserAttrByUserInstType(grpProductUserId, attrCode);
        if(IDataUtil.isEmpty(userAttrInfoList)){
            return attrValue;
        }

        //3- 获取参数的用户资料值
        IData userAttrInfo = userAttrInfoList.getData(0);
        attrValue = userAttrInfo.getString("ATTR_VALUE");

        //4- 返回属性值
        return attrValue;
    }

    /**
     * @description 落地报文合同附件发往客管保存
     * @author xunyl
     * @date 2015-03-07
     */
    public static void sendAttInfoListToCliMng(IData map,String custId,String productId)throws Exception{
        //1- 初始化入参
        IDataset fileInfoList = new DatasetList();

        //2- 获取合同类型
        IDataset pOAttTypeInfoList = IDataUtil.getDataset("POATT_TYPE", map);
        if(IDataUtil.isEmpty(pOAttTypeInfoList)){
            return;
        }

        //3-获取合同附件相关信息
        IDataset pOAttCodeInfoList = IDataUtil.getDataset("POATT_CODE", map);//合同编码
        IDataset contNameInfoList = IDataUtil.getDataset("CONT_NAME", map);//合同名称
        IDataset pOAttNameInfoList = IDataUtil.getDataset("POATT_NAME", map);//附件名称
        IDataset contEffdateInfoList = IDataUtil.getDataset("CONT_EFFDATE", map);//合同开始日期
        IDataset contExpdateInfoList = IDataUtil.getDataset("CONT_EXPDATE", map);//合同结束日期
        IDataset isAutoRecontInfoList = IDataUtil.getDataset("IS_AUTO_RECONT", map);//是否自动续约
        IDataset recontExpdateInfoList = IDataUtil.getDataset("RECONT_EXPDATE", map);//续约后到期时间
        IDataset contFeeInfoList = IDataUtil.getDataset("CONT_FEE", map);//签约资费
        IDataset perferPlanInfoList = IDataUtil.getDataset("PERFER_PLAN", map);//优惠方案
        IDataset autoRecontCycInfoList = IDataUtil.getDataset("AUTO_RECONTCYC", map);//自动续约周期
        IDataset isRecontInfoList = IDataUtil.getDataset("IS_RECONT", map);//是否为续签合同

        //4- 根据合同类型拼写附件列表
        for(int i=0;i<pOAttTypeInfoList.size();i++){
            IData fileInfo = new DataMap();
            fileInfo.put("POATT_TYPE", IDataUtil.isNotEmpty(pOAttTypeInfoList)?pOAttTypeInfoList.get(i).toString():"");
            fileInfo.put("POATT_CODE", pOAttCodeInfoList.size() > i ?pOAttCodeInfoList.get(i).toString():"");
            fileInfo.put("CONT_NAME", contNameInfoList.size() > i?contNameInfoList.get(i).toString():"");
            fileInfo.put("POATT_NAME", IDataUtil.isNotEmpty(pOAttNameInfoList)?pOAttNameInfoList.get(i).toString():"");
            fileInfo.put("CONT_EFFDATE", contEffdateInfoList.size() > i?contEffdateInfoList.get(i).toString():"");
            fileInfo.put("CONT_EXPDATE", contExpdateInfoList.size() > i?contExpdateInfoList.get(i).toString():"");
            fileInfo.put("IS_AUTO_RECONT", isAutoRecontInfoList.size() > i?isAutoRecontInfoList.get(i).toString():"");
            fileInfo.put("RECONT_EXPDATE", recontExpdateInfoList.size() > i?recontExpdateInfoList.get(i).toString():"");
            fileInfo.put("CONT_FEE", contFeeInfoList.size() > i?contFeeInfoList.get(i).toString():"");
            fileInfo.put("PERFER_PALN", perferPlanInfoList.size() > i?perferPlanInfoList.get(i).toString():"");
            fileInfo.put("AUTO_RECONTCYC", autoRecontCycInfoList.size() > i?autoRecontCycInfoList.get(i).toString():"");
            fileInfo.put("IS_RECONT", isRecontInfoList.size() > i?isRecontInfoList.get(i).toString():"");
            fileInfoList.add(fileInfo);
        }

        //5- 封装接口参数
        IData inparam = new DataMap();
        inparam.put("CUST_ID", custId);
        inparam.put("PRODUCT_ID", productId);
        inparam.put("FILE_INFO_LIST", fileInfoList);

        //6- 调用客管接口
        CSAppCall.callCCHT("ITF_CRM_ContractAdd", inparam, false);
    }


    /**
     * 集团客户受理大厅子流程登记
     * @param userID
     * @param instId
     * @param serial_number
     * @param ecrecep_order_id
     * @param ecrecep_offer_id
     * @param opertype
     * @param svcStatus
     * @param userStatus
     * @throws Exception
     */
    public static IData actEcrecepProcedure(String userID,String instId,String serial_number,String ecrecep_order_id,String ecrecep_offer_id,String opertype,String svcStatus,String  userStatus)throws Exception{
        IData epMap=new DataMap();
        epMap.put("USER_ID", userID);
        epMap.put("INST_ID", instId);
        epMap.put("SERIAL_NUMBER", serial_number);
        epMap.put("ECRECEP_ORDER_ID", ecrecep_order_id);
        epMap.put("ECRECEP_OFFER_ID", ecrecep_offer_id);
        epMap.put("OPERTYPE", opertype);
        epMap.put("SVC_STATUS", svcStatus);
        epMap.put("USER_STATUS", userStatus);
        epMap.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
        epMap.put("START_DATE", SysDateMgr.getSysTime());
        epMap.put("END_DATE", SysDateMgr.getTheLastTime());
        return epMap;
    }

    public static void modifyJKDTInparamsBySpecialBiz(IData map, String merchOperType) throws Exception
    {
        //1- 产品预受理阶段(正向)，根据规范过滤集团公司的产品资费信息
        IData merchInfo = map.getData("MERCH_INFO");
        String inModeCode = merchInfo.getString("IN_MODE_CODE");
        if (GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_ADD.getValue().equals(merchOperType) && !StringUtils.equals(inModeCode, "6"))
        {
            delJKDTPreDiscntInfo(map);
        }

        // 2- 公众服务云业务需要根据产品参数中的折扣来添加资费折扣
        String bossMerchSpecNum = merchInfo.getString("PRODUCT_ID");
        String bbossMerchSpecNum = productJKDTToMerch(bossMerchSpecNum, 0);
        if ("1010402".equals(bbossMerchSpecNum))
        {
            BbossDisAttrTransBean.addDisAttrByProductAttr(map, merchOperType);
        }


        //3- 省内IDC流量计费业务省内资费转产品参数/省内资费属性转产品参数
        if("50005".equals(bbossMerchSpecNum) && !StringUtils.equals(inModeCode, "6")){
            GrpDisAttrTransBean.disDataToAttrData(map, merchOperType);
        }

        //4- 云客服需要根据产品属性参数中的坐席数量添加到资费属性参数中
        if("01114001".equals(bbossMerchSpecNum)){
            GrpDisAttrTransBean.attrDataToDisData(map, merchOperType);
        }
    }

    private static void delJKDTPreDiscntInfo(IData map) throws Exception
    {
        // 1- 产品信息不存在直接退出
        IDataset productInfoList = map.getDataset("ORDER_INFO");
        if (IDataUtil.isEmpty(productInfoList))
        {
            return;
        }

        // 2- 非预受理操作直接退出
        String productOperType = productInfoList.getData(0).getData("BBOSS_PRODUCT_INFO").getString("PRODUCT_OPER_CODE");
        if (!GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_PREDEAL.getValue().equals(productOperType))
        {
            return;
        }

        // 3- 删过滤产品信息中的集团资费
        for (int i = 0; i < productInfoList.size(); i++)
        {
            IData productInfo = productInfoList.getData(i);
            IDataset elementInfoList = productInfo.getDataset("ELEMENT_INFO");
            if (IDataUtil.isEmpty(elementInfoList))
            {
                return;
            }
            for (int j = 0; j < elementInfoList.size(); j++)
            {
                IData elementInfo = elementInfoList.getData(j);
                String elementType = elementInfo.getString("ELEMENT_TYPE_CODE");
                if ("D".equals(elementType))
                {
                    String elementId = elementInfo.getString("ELEMENT_ID");
                    // 如果省内资费编号与集团公司资费编号存在对应关系，则属于集团侧资费，需过滤
                    String bbossElementId = GrpCommonBean.productToMerch(elementId, 1);
                    if (StringUtils.isNotBlank(bbossElementId))
                    {
                        elementInfoList.remove(elementInfo);
                        j--;
                    }
                }
            }
        }
    }

    public static String getJKDTMerchUserIdByProdOffId(String merchOfferingID) throws Exception
    {
        // 1- 根据商品订购关系编号获取对应的商品用户信息
        IDataset merchUserInfos = UserEcrecepOfferfoQry.qryJKDTMerchInfoByMerchOfferId(merchOfferingID);
        if (null == merchUserInfos || merchUserInfos.size() == 0)
        {
            CSAppException.apperr(CrmUserException.CRM_USER_914, merchOfferingID);
        }

        // 2- 获取商品用户编号
        return merchUserInfos.getData(0).getString("USER_ID");
    }

    public static String getJKDTMerchpUserIdByProdId(String productOfferingId) throws Exception
    {
        // 1- 根据产品订购关系编号获取对应的产品用户信息
        IDataset merchpUserInfos = UserEcrecepProductInfoQry.qryEcrEceppInfosByPro(productOfferingId,null);
        if (null == merchpUserInfos || merchpUserInfos.size() == 0)
        {
            CSAppException.apperr(CrmUserException.CRM_USER_914, productOfferingId);
        }

        // 2- 获取产品用户编号
        return merchpUserInfos.getData(0).getString("USER_ID");
    }

    /*
     * @description 根据接口规范的商品操作编号获取服务开通侧配置的商品操作编号(供服务开通用)
     * @author xunyl
     * @date 2013-08-26
     */
    public static String getJKDTServMerchOpType(String merchOpType,String credit_pause_flag) throws Exception
    {
        // 1- 定义返回值
        String servMerchOpType = "";

        // 2-获取对应的商品编号
        if (merchOpType.equals(GroupBaseConst.MERCH_STATUS.MERCH_PASTE.getValue()) ||
                StringUtils.equals(merchOpType, credit_pause_flag))
        {
            servMerchOpType = "04";
        }
        else if (merchOpType.equals(GroupBaseConst.MERCH_STATUS.MERCH_CONTINUE.getValue()))
        {
            servMerchOpType = "05";
        }
        else if (merchOpType.equals(GroupBaseConst.MERCH_STATUS.MERCH_MODIFY_DISCNT.getValue()))
        {
            servMerchOpType = "15";
        }
        else if (merchOpType.equals(GroupBaseConst.MERCH_STATUS.MERCH_MODIFY_GROUP.getValue()))
        {
            servMerchOpType = "17";
        }
        else if (merchOpType.equals(GroupBaseConst.MERCH_STATUS.MERCH_MODIFY_PARAM.getValue()))
        {
            servMerchOpType = "19";
        }
        else if (merchOpType.equals(GroupBaseConst.MERCH_STATUS.MERCH_PREDESTORY.getValue()))
        {
            servMerchOpType = "10";
        }
        else if (merchOpType.equals(GroupBaseConst.MERCH_STATUS.MERCH_CANCLEPREDESTORY.getValue()))
        {
            servMerchOpType = "11";
        }
        else if (merchOpType.equals(GroupBaseConst.MERCH_STATUS.MERCH_MODIFY_MEB.getValue()))
        {
            servMerchOpType = "16";
        }
        else if (merchOpType.equals(GroupBaseConst.MERCH_STATUS.MERCH_MODIFY_PROV.getValue()))
        {
            servMerchOpType = "13";
        }
        else if (merchOpType.equals(GroupBaseConst.MERCH_STATUS.MERCH_PASTE_MEBFLUX.getValue()))
        {
            servMerchOpType = "42";
        }
        else if (merchOpType.equals(GroupBaseConst.MERCH_STATUS.MERCH_CONTINUE_MEBFLUX.getValue()))
        {
            servMerchOpType = "43";
        }

        // 3- 返回服务开通侧商品操作编号
        return servMerchOpType;
    }

    /*
     * @description 根据接口规范的产品操作编号获取服务开通侧配置的产品操作编号(供服务开通用)
     * @author xunyl
     * @date 2013-08-26
     */
    public static String getServJKDTMerchpOpType(String merchpOpType,String credit_pause_flag) throws Exception
    {
        // 1- 定义返回值
        String servMerchpOpType = "";

        // 2-获取对应的产品编号
        if (merchpOpType.equals(GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_ADD.getValue()))
        {
            servMerchpOpType = "06";
        }
        else if (merchpOpType.equals(GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_CANCLE.getValue()))
        {
            servMerchpOpType = "07";
        }
        else if (merchpOpType.equals(GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_PASTE.getValue()) ||
                StringUtils.equals(merchpOpType, credit_pause_flag))
        {
            servMerchpOpType = "04";
        }
        else if (merchpOpType.equals(GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_CONTINUE.getValue()))
        {
            servMerchpOpType = "05";
        }
        else if (merchpOpType.equals(GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_MODIFY_DISCNT.getValue()))
        {
            servMerchpOpType = "15";
        }
        else if (merchpOpType.equals(GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_MODIFY_MEB.getValue()))
        {
            servMerchpOpType = "16";
        }
        else if (merchpOpType.equals(GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_MODIFY_PARAM.getValue()))
        {
            servMerchpOpType = "19";
        }
        else if (merchpOpType.equals(GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_PREDEAL.getValue()))
        {
            servMerchpOpType = "10";
        }
        else if (merchpOpType.equals(GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_PREDESTORY.getValue()))
        {
            servMerchpOpType = "11";
        }
        else if (merchpOpType.equals(GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_CANCLEPREDESTORY.getValue()))
        {
            servMerchpOpType = "12";
        }
        else if (merchpOpType.equals(GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_MODIFY_PROV.getValue()))
        {
            servMerchpOpType = "13";
        }
        else if (merchpOpType.equals(GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_COPREDEAL.getValue()))
        {
            servMerchpOpType = "14";
        }
        else if (merchpOpType.equals(GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_CODEAL.getValue()))
        {
            servMerchpOpType = "25";
        }
        else if (merchpOpType.equals(GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_CONTINUE_MEBFLUX.getValue()))
        {
            servMerchpOpType = "23";
        }
        else if (merchpOpType.equals(GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_PASTE_MEBFLUX.getValue()))
        {
            servMerchpOpType = "22";
        }

        // 3- 返回服务开通侧产品操作编号
        return servMerchpOpType;
    }


    private static String JKDTmerchToProductByPid(String elementId, int mode, String productId) throws Exception
    {
        String attrCode = "";
        String attrObj = "";

        attrObj = "DIS";// 商品、产品资费，商品套餐

        String jkdtLocalDiString=merchJKDTToProduct(elementId, 2, "");//查询集客大厅资费


        // 1获取对应本地元素编码
        IDataset datas = getElemetInfo(elementId, mode, productId, attrObj);
        if (IDataUtil.isNotEmpty(datas))
        {
            IData data = datas.getData(0);
            attrCode = data.getString("ATTR_CODE", "");
        }
        //如果集客大厅资费和bboss资费对应同一个本地id，则以bboss的资费为准，因为查集客大厅资费jkdtLocalDiString时候，bboss资费的attrCode必定为空（带了productid查）
        //查bboss资费的attrCode时候有可能jkdtLocalDiString不为空。
        if(StringUtils.isNotEmpty(jkdtLocalDiString)&&StringUtils.isNotEmpty(attrCode)){
            return attrCode;
        }else if(StringUtils.isNotEmpty(jkdtLocalDiString)&&StringUtils.isEmpty(attrCode)){
            return jkdtLocalDiString;
        }else if(StringUtils.isEmpty(jkdtLocalDiString)&&StringUtils.isNotEmpty(attrCode)){
            return attrCode;
        }


        return attrCode;
    }

    public static String getJKDTProductIdByUserId(String userId) throws Exception
    {
        // 1- 定义返回的产品编号
        String productId = "";

        // 2- 根据用户编号获取产品编号
        IDataset userProductInfoList = UserEcrecepProductInfoQry.qryEcrEceppInfosByUserId(userId);
        if (IDataUtil.isEmpty(userProductInfoList))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_1);
        }
        IData userProductInfo = userProductInfoList.getData(0);
        productId = userProductInfo.getString("PRODUCT_ID");

        // 3- 返回产品编号
        return productId;
    }

    /**
     *集团公司商产品规格转省BOSS商产品规格
     */
    public static String merchJKDTToProduct(String elementId, int mode, String productId) throws Exception {
        //mode 0是商品，1是资费，2是产品
        // 1- 定义查询的元素类型
        String attrCode = "";

        String type = "0";
        //call产商品的接口（0-商品 1-产品 2-资费）
        /* PONUMBER，TYPE，OFFER_ID，OFFER_CODE，OFFER_TYPE */
        if (mode == 0){
            type = "0";
        } else if (mode == 1) {
            type = "2";
        } else if (mode == 2) {
            type = "1";
        }

        IDataset mapping = UpcCall.queryOfferMappingByBossNumber(elementId, type);
        attrCode = mapping.getData(0).getString("OFFER_CODE","");

        if (   (mode == 0 || mode ==2 )   &&  StringUtils.isEmpty(attrCode)  ){
            CSAppException.apperr(CrmUserException.CRM_USER_783, elementId);
        }

        return attrCode;
    }


    public static String productJKDTToMerch(String productId, int mode) throws Exception
    {
        //mode 产商品是0    ;
        //            1是商产品资费，商品套餐
        String attrObj = "";
        if (mode == 0)
        {
            attrObj = "P";
            IDataset mapping = UpcCall.queryOfferMappingByLocalNumber(productId, attrObj);
            String temp = mapping.getData(0).getString("PONUMBER", "");
            if(StringUtils.isEmpty(temp)){
                CSAppException.apperr(CrmUserException.CRM_USER_783, productId);
            }
            return temp;
        } else {
            attrObj = "D";
            IDataset mapping = UpcCall.queryOfferMappingByLocalNumber(productId, attrObj);
            String temp = mapping.getData(0).getString("PONUMBER", "");
            return temp;
        }

    }
    
    public static String getJKDTMerchpUserIdByProudctId(String productOfferingId) throws Exception
    {
        // 1- 根据产品订购关系编号获取对应的产品用户信息
        IDataset merchpUserInfos = UserEcrecepProductInfoQry.getUserEcrecepProductByOfferId(productOfferingId);
        if (null == merchpUserInfos || merchpUserInfos.size() == 0)
        {
            CSAppException.apperr(CrmUserException.CRM_USER_914, productOfferingId);
        }

        // 2- 获取产品用户编号
        return merchpUserInfos.getData(0).getString("USER_ID");
    }
    
    /*
     * @descripiton 获取产品属性折扣率参数或其它信息
     * @author daidl
     * @date 2019-03-28
     */
    public static void getProductOtherParamInfo(IData secondProductInfo, IDataset productIcbParamInfo , String productId) throws Exception
    {
        // 1- 获取产品属性参数编码集
        IDataset codeList = secondProductInfo.getDataset("PARAM_CODE");

        // 2- 获取产品属性参数名称集
        IDataset nameList = secondProductInfo.getDataset("PARAM_NAME");

        // 3- 获取产品属性值集
        IDataset valueList = secondProductInfo.getDataset("PARAM_VALUE");

        // 4- 校验属性参数是否存在
        if (codeList == null || valueList == null)
        {
            return;
        }
        //5- 校验是否有折扣率属性参数配置,并封装参数对象
        for (int i = 0; i < codeList.size(); i++) {
        	String code  = GrpCommonBean.nullToString(codeList.get(i));
        	IDataset dataset = CommparaInfoQry.getCommparaByCodeCode1("CSM", "9080", productId,code);//约定配置的折扣参数信息
            if(IDataUtil.isNotEmpty(dataset)){
            	IData param = new DataMap();
                param.put("ATTR_CODE", dataset.getData(0).getString("PARA_CODE2"));
                param.put("ATTR_VALUE", GrpCommonBean.nullToString(valueList.get(i)));
                param.put("ATTR_NAME", GrpCommonBean.nullToString(nameList.get(i)));
                productIcbParamInfo.add(param);
            }
		} 
    }
    /**
     * 获取产品、资费、属性的特殊处理
     * @author fuzn 2019-06-10
     * @param secondProductInfo
     * @param productIcbParamInfo
     * @param productId
     * @param dElementId
     * @throws Exception
     */
    public static void getProductOtherParamInfo(IData secondProductInfo,IDataset productIcbParamInfo,String productId,String dElementId) throws Exception
    {
    	String productSpecNumber = GrpCommonBean.productJKDTToMerch(productId, 0);
    	
    	// 1- 获取产品属性参数编码集
        IDataset codeList = secondProductInfo.getDataset("PARAM_CODE");

        // 2- 获取产品属性参数名称集
        IDataset nameList = secondProductInfo.getDataset("PARAM_NAME");

        // 3- 获取产品属性值集
        IDataset valueList = secondProductInfo.getDataset("PARAM_VALUE");

        // 4- 校验属性参数是否存在
        if (codeList == null || valueList == null)
        {
            return;
        }
      
        for (int i = 0; i < codeList.size(); i++)
        {
        	String attrcode  = GrpCommonBean.nullToString(codeList.get(i));
        	IDataset dataset = CommparaInfoQry.queryCommparaByCodeAndName(productSpecNumber,dElementId,attrcode,"CSM","9080"); // 约定配置的折扣参数信息
            if(IDataUtil.isNotEmpty(dataset)){
            	IData param = new DataMap();
                // param.put("ATTR_CODE", dataset.getData(0).getString("PARA_CODE2"));
            	param.put("ATTR_CODE", attrcode);
                param.put("ATTR_VALUE", GrpCommonBean.nullToString(valueList.get(i)));
                param.put("ATTR_NAME", GrpCommonBean.nullToString(nameList.get(i)));
                productIcbParamInfo.add(param);
            }
        }
    }
    
}
