package com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.bat.bean;

import net.sf.json.JSONArray;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.BatException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.exception.ProductException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.group.common.GroupBaseConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.BatDealStateUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.RouteInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.diversifyacct.AcctDayDateUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.diversifyacct.DiversifyAcctUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.bat.BatTradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.MsisdnInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.AttrBizInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.BBossAttrQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserGrpMerchMebInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserGrpMerchpInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserNpAllInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.bat.unit.BatDealBBossUnit;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.GroupDiversifyUtilBean;

public class BatDealBBossMebSubBean
{
    // 是否允许外省成员,true表示允许，false表示不允许
    private static boolean allowExtMeb = false;

    // 是否允许加携入号码,true表示允许，false表示不允许
    private static boolean allowNp = false;
    
    /**
     * @description 处理BBOSS成员批量明细信息
     * @author xunyl
     * @date 2015-12-17
     */
    public static final void dealBBossMebSub(String batchId,String batchOperType)throws Exception{
        //1- 获取成员明细列表
        IData inparam = new DataMap();
        inparam.put("BATCH_ID", batchId);
        inparam.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(batchId));
        IDataset memberDataset = BatTradeInfoQry.queryBBossMemberInfos(inparam, Route.getJourDb(Route.CONN_CRM_CG));
        if (IDataUtil.isEmpty(memberDataset))
        {
            CSAppException.apperr(GrpException.CRM_GRP_335);
        }
        
        //2- 根据批次号获取集团用户编号和产品编号(批量信息不再校验是否为空，前面已经校验)
        inparam.clear();
        inparam.put("BATCH_ID", batchId);
        inparam.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(batchId));
        IData batchdata = BatTradeInfoQry.qryTradeBatByPK(inparam);
        String batchTaskId = batchdata.getString("BATCH_TASK_ID");
        String condString = BatTradeInfoQry.getTaskCondString(batchTaskId);
        if (StringUtils.isEmpty(condString))
        {
            CSAppException.apperr(BatException.CRM_BAT_11);
        }
        JSONArray array = new JSONArray();
        array.element(condString);
        IDataset batchCondition = new DatasetList(array.toString());

        if (IDataUtil.isEmpty(batchCondition))
        {
            CSAppException.apperr(BatException.CRM_BAT_36);
        }
        String productId = batchCondition.getData(0).getString("PRODUCT_ID", "");
        String groupId = batchCondition.getData(0).getString("GROUP_ID", "");
        String grpUserId = batchCondition.getData(0).getString("USER_ID", "");

        //3- 根据产品编号判断成员新增操作中是否允许外省号码及携号转网号码加入
        productJudge(productId);
        
        //4- 异常检测(过滤不正常的成员号码，避免无效号码发往集团BBOSS侧)
        for(int i=0;i<memberDataset.size();i++){
            IData memberInfo = memberDataset.getData(i);  
            String errorStr = memberInfoCheck(memberInfo,batchId,productId,grpUserId,batchOperType);
            if(StringUtils.isNotBlank(errorStr)){
                IData result = new DataMap();
                result.put("DEAL_STATE", BatDealStateUtils.DEAL_STATE_B);
                result.put("DEAL_RESULT", "导入成功");
                result.put("DEAL_DESC", errorStr);
                result.put("BATCH_ID", batchId);
                result.put("SERIAL_NUMBER", memberInfo.getString("SERIAL_NUMBER",""));
                BatDealBBossUnit.updateBatDealByBatchIdSn(result);
                memberDataset.remove(i);
                i--;
            }else{
                IData result = new DataMap();
                result.put("DEAL_STATE", BatDealStateUtils.DEAL_STATE_9);
                result.put("DEAL_RESULT", "导入成功");
                result.put("DEAL_DESC", "成员批量数据提交一级BBOSS成功");
                result.put("BATCH_ID", batchId);
                result.put("SERIAL_NUMBER", memberInfo.getString("SERIAL_NUMBER",""));
                BatDealBBossUnit.updateBatDealByBatchIdSn(result);
            }
        }             
        
        //5- 循环处理成员明细信息
        for (int i = 0; i < memberDataset.size(); i++)
        {
            //3-1 获取批量明细中的手机号码、成员类型、产品订购关系编号、批量类型、批次号、系统批次号
            IData memberInfo = memberDataset.getData(i);
            String serialNumber = memberInfo.getString("SERIAL_NUMBER");
            String memberType = memberInfo.getString("DATA19");
            
            String merchPCode = AttrBizInfoQry.getAttrValueBy1BAttrCodeObj(productId, "PRO");
            //根据PRODUCTSPECNUMBER取POSPECNUMBER
            String merchCode = UpcCall.queryPospecnumberByProductspecnumber(merchPCode);
            IDataset merchpInfoList = UserGrpMerchpInfoQry.qryMerchpInfoByGroupIdMerchScProductScUserId(grpUserId, groupId, merchCode, merchPCode);
            if (IDataUtil.isEmpty(merchpInfoList))
            {
                CSAppException.apperr(GrpException.CRM_GRP_401, groupId, merchPCode);
            }
            IData merchpInfo = merchpInfoList.getData(0);
            String productOfferId = merchpInfo.getString("PRODUCT_OFFER_ID");
            String ibSysId = SeqMgr.getUipSysId();
            
            //3-2 插入BBOSS成员批量主表信息
            insertBBossUdr(serialNumber, memberType, productOfferId, batchOperType, batchId, ibSysId);

            //3-3 插入BBOSS成员批量明细表
            IDataset mebBBossAttrParam = BBossAttrQry.qryBBossAttrByPospecBiztype(merchPCode, "2");
            insertBBossUdrSub(memberInfo, mebBBossAttrParam, ibSysId);
        }
    }
    
    /**
     * @description 明细表异常检测
     * @author xunyl
     * @date 2015-12-22
     */
    private static String memberInfoCheck(IData memberInfo,String batchId,String productId,String grpUserId,String batchOperType)throws Exception{
        //1- 异常定义
        String error = "";
        
        //2- 成员手机号码检测
        String serialNumber = memberInfo.getString("SERIAL_NUMBER");
        if (StringUtils.isEmpty(serialNumber)){
            error = BatException.CRM_BAT_87.getValue();
            return error;
        }
        
        //3- 成员操作类型检测
        String memberType = memberInfo.getString("DATA19");
        if (StringUtils.isEmpty(memberType))
        {
            error = BatException.CRM_BAT_88.getValue();
            return error;
        }
        
        //4- 号码归属检测(非中国移动号码抛出错误)
        IData snInfo = MsisdnInfoQry.getMsisonBySerialnumber(serialNumber, null);
        if (snInfo == null || !"1".equals(snInfo.getString("ASP")))
        {
            error = CrmUserException.CRM_USER_617.getValue();
            if(allowNp){//全网MAS允许加携转号码
                IDataset userNpAll = UserNpAllInfoQry.queryUserNpAll(serialNumber);
                if(IDataUtil.isNotEmpty(userNpAll)){
                    IData userNp = userNpAll.getData(0);
                    // 002标示运营商为移动
                    if ("002".equals(userNp.getString("PORT_IN_NETID").substring(0, 3)))
                    {
                        error = "";
                    }
                }         
            }
            if(StringUtils.isNotBlank(error)){
                return error;
            }
        }
        
        //5- 成员资料检测
        IDataset mofficeInfo = MsisdnInfoQry.getMsdnBySn(serialNumber);
        if (IDataUtil.isEmpty(mofficeInfo) && !(batchOperType.equals("BATADDBBOSSMEMBER") && (allowExtMeb || allowNp)))
        {
            error = CrmUserException.CRM_USER_112.getValue();
            return error;
        }
        String defaultEparchyCode = RouteInfoQry.getEparchyCodeBySnForCrm(serialNumber);
        if (!batchOperType.equals("BATADDBBOSSMEMBER") || (batchOperType.equals("BATADDBBOSSMEMBER") && IDataUtil.isNotEmpty(mofficeInfo)))
        {
            String userId = mofficeInfo.getData(0).getString("USER_ID");
            // 获取成员的订购信息，merch_meb
            IDataset datas = UserGrpMerchMebInfoQry.getSEL_BY_USERID_USERIDA(userId, grpUserId, defaultEparchyCode);
            if (IDataUtil.isEmpty(datas) && !batchOperType.equals("BATADDBBOSSMEMBER"))
            {               
                error = ProductException.CRM_PRODUCT_83.getValue();  
                return error;
            }
            else if ((datas != null && datas.size() > 0) && batchOperType.equals("BATADDBBOSSMEMBER"))
            {
                error = ProductException.CRM_PRODUCT_87.getValue();
                return error;
            }
        }
        
        //6- 分散逻辑处理检测
        error = DiversifyUtilDeal(serialNumber,batchId,productId);   
        if(StringUtils.isNotBlank(error)){
            return error;
        }    
        
        //7- 返回校验结果
        return error;
    }
    
    /**
     * @description BBOSS成员批量分散处理逻辑(寻友良搬迁，原代码位于BatDealBBossMebBean中)
     * @author xunyl
     * @date 2015-12-22
     */
    private static String DiversifyUtilDeal(String serialNumber, String batchId, String productId) throws Exception
    {
        // 1- 查询集团产品编码
        String merchPCode = AttrBizInfoQry.getAttrValueBy1BAttrCodeObj(productId, "PRO");

        // 2- 查询集团商品编码
        //String merchCode = UpcCall.queryPoproductByPospecNumber(merchPCode).getData(0).getString("POSPECNUMBER");
        //根据PRODUCTSPECNUMBER取POSPECNUMBER
        String merchCode = UpcCall.queryPospecnumberByProductspecnumber(merchPCode);

        // 3- 查询本地商品ID
        String merch_id = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_ATTR_BIZ", new java.lang.String[]
        { "ID", "ID_TYPE", "ATTR_OBJ", "ATTR_VALUE" }, "ATTR_CODE", new java.lang.String[]
        { "1", "B", "PRO", merchCode });

        // 4- 获取成员号码信息
        IDataset mebUserInfos = MsisdnInfoQry.getMsdnBySn(serialNumber);

        // 5- 分散逻辑处理
        // 是否允许受理的号码
        String errorStr = "";
        if (IDataUtil.isNotEmpty(mebUserInfos))
        {
            String mebUserId = mebUserInfos.getData(0).getString("USER_ID");
            // 判断商品是否必须要求为自然月账期
            boolean ifMerchNatureDay = GroupDiversifyUtilBean.getNatureProductTag(merch_id);
            // 判断产品是否必须要求为自然月账期
            boolean ifProductNatrueDay = !GroupDiversifyUtilBean.getSpecialImmeProductTag(productId);

            // 成员用户账期判断
            if (ifMerchNatureDay || ifProductNatrueDay)
            {
                IData mebUserAcctDay = AcctDayDateUtil.getUserAcctDayAndFirstDateInfo(mebUserId, CSBizBean.getTradeEparchyCode());
                if (mebUserAcctDay != null && mebUserAcctDay.size() > 0)
                {
                    // 非自然月账期的情况
                    if (!DiversifyAcctUtil.checkUserAcctDay(mebUserAcctDay, "1", false))
                    {
                        String diverDistribute = DiversifyAcctUtil.userAcctDayDistribution(mebUserAcctDay, "1");// 成员账期分布

                        if (diverDistribute.equals(GroupBaseConst.UserDaysDistribute.FALSE_TRUE.getValue()))
                        {
                            errorStr = "当前用户的出账日不是1号,不允许办理业务,请在集团产品成员新增页面受理!";                        
                        }
                        else
                        {
                            errorStr = "当前用户的出账日不是1号,不允许办理业务,须将账期改为自然月才可办理改业务!";
                        }
                    }
                }
            }
        }

        return errorStr;
    }
        
    /**
     * 插入BBOSS成员批量主表信息
     * 
     * @param seiralNumber
     * @param memberType
     * @param productOfferId
     * @param batchOperType
     * @param batchId
     * @param ibSysId
     * @throws Exception
     */
    private static void insertBBossUdr(String seiralNumber, String memberType, String productOfferId, String batchOperType, String batchId, String ibSysId) throws Exception
    {
        IData baseInfo = new DataMap();
        baseInfo.put("SRC", "1");
        baseInfo.put("MODIFY_TAG", "0");
        baseInfo.put("OPER_FLAG", "1");
        baseInfo.put("RSRV_STR10", "2");
        baseInfo.put("IBSYSID", ibSysId);
        baseInfo.put("PKGSEQ", SeqMgr.getTradeId());

        baseInfo.put("BIPCODE", "BIP4B257");
        baseInfo.put("ACTIVITYCODE", "T4101034");
        baseInfo.put("ORDERNUMBER", "");// 订单编码
        baseInfo.put("PRODUCTID", productOfferId);// 订购关系id
        baseInfo.put("ORDERSOURCE", "0"); // 0- BBOSS受理 1-省BOSS上传 2- EC上传

        baseInfo.put("MEMBERNUMBER", seiralNumber);// 成员号码
        if (batchOperType.equals("BATADDBBOSSMEMBER"))
        {
            baseInfo.put("OPER_ACTION", "1");// 操作类型新增
        }
        else if (batchOperType.equals("BATDELBBOSSMEMBER"))
        {
            baseInfo.put("OPER_ACTION", "0");// 操作类型删除
        }
        else if (batchOperType.equals("BATCONBBOSSMEMBER"))
        {
            baseInfo.put("OPER_ACTION", "4");// 操作类型恢复
        }
        else if (batchOperType.equals("BATPASBBOSSMEMBER"))
        {
            baseInfo.put("OPER_ACTION", "3");// 操作类型暂停
        }
        else if (batchOperType.equals("BATMODBBOSSMEMBER"))
        {
            baseInfo.put("OPER_ACTION", "6");// 操作类型变更
        }

        baseInfo.put("MEMBERTYPEID", memberType);// 成员类型
        baseInfo.put("EFFDATE", SysDateMgr.getSysTime());// 期望生效时间
        baseInfo.put("UPDATE_TIME", SysDateMgr.getSysTime());// 更新时间
        baseInfo.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        baseInfo.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
        baseInfo.put("TRADE_EPARCHY_CODE", CSBizBean.getVisit().getStaffEparchyCode());
        baseInfo.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        baseInfo.put("RSRV_STR9", batchId);// 插入bath_id，给一级BBOSS归档调用，更改状态标志
        baseInfo.put("DAY", SysDateMgr.getCurDay());// 插入DAY字段

        // 插入到主表
        Dao.insert("TI_B_BBOSSMEMBERDATA_UDR", baseInfo, Route.CONN_CRM_CEN);
    }

    /**
     * 插入BBOSS成员批量明细表信息
     * 
     * @param sub
     * @param memParam
     * @param ibSysId
     * @throws Exception
     */
    private static void insertBBossUdrSub(IData sub, IDataset memParam, String ibSysId) throws Exception
    {
        // 查数据到明细表 读取批量表的参数数据
        IDataset subData = new DatasetList();
        for (int j = 0, pSize = memParam.size(); j < pSize; j++)
        {

            String attr_code = memParam.getData(j).getString("ATTR_CODE");
            attr_code.substring(attr_code.length() - 4, attr_code.length());
            String attr_name = memParam.getData(j).getString("ATTR_NAME");
            String dataCol = memParam.getData(j).getString("RSRV_STR1");

            String value = sub.getString(dataCol);
            if (StringUtils.isEmpty(value))
            {
                continue;
            }

            IData putData = new DataMap();
            putData.put("IBSYSID", SeqMgr.getSysIbSysId());
            putData.put("ID", ibSysId);
            putData.put("PARAMNAMEEN", attr_code);
            putData.put("PARAMNAMECN", attr_name);
            putData.put("PARAMVALUE", value);
            putData.put("PARAMTYPE", "1"); // 解决批量业务不传成员参数bug
            subData.add(putData);
        }

        // 插明细表信息
        Dao.insert("TI_B_BBOSSMEMBERDATA_UDR_SUB", subData, Route.CONN_CRM_CEN);
    }
    
    /**
     * @description 产品校验信息判断(寻友良搬迁过来，原方法来自BatDealBBossMebBean.java中)
     * @author xunyl
     * @date 2015-12-22
     */
    private static void productJudge(String productId) throws Exception
    {
        // 1- 是否允许外省成员
        IDataset configs = AttrBizInfoQry.getBizAttr("1", "B", "CanExtMeb", productId, null);

        if (null != configs && configs.size() > 0)
        {
            allowExtMeb = true;
        }

        // 2- 是否允许加携入号码
        IDataset configsNp = AttrBizInfoQry.getBizAttr("1", "B", "CanNp", productId, null);

        if (null != configsNp && configsNp.size() > 0)
        {
            allowNp = true;
        }
    }

}
