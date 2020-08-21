/**
 * 
 */

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
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.bat.BatTradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.MsisdnInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.AttrBizInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserGrpMerchMebInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserGrpMerchpInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.bat.unit.BatDealBBossUnit;

/**
 * @author chenyi 2014-8-6 TODO
 */
public class BatDealBBossBATMEBCENPAYBean
{

    /**
     * 检查是否为有效成员以及叠加包是否有效ID
     * 
     * @param pd
     * @param sucess
     * @param faile
     * @throws Exception
     */
    public static void checkMemUUAndRate(String groupid, String batchId, String batchOperType, IDataset bats) throws Exception
    {

        for (int i = 0, sizeI = bats.size(); i < sizeI; i++)
        {
            IData bat = bats.getData(i);
            String serialNumber = bat.getString("SERIAL_NUMBER");
            String rateid = bat.getString("DATA1");// 叠加包id
            String productid = bat.getString("DATA2");// 产品product_id
            String productSpecNumber = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_ATTR_BIZ", new String[]
            { "ID", "ID_TYPE", "ATTR_CODE", "ATTR_OBJ" }, "ATTR_VALUE", new String[]
            { "1", "B", productid, "PRO" });// 获取集团产品编码
            // 获取集团用户id
            IDataset usermerchpInfo = UserGrpMerchpInfoQry.qryMerchpInfoByGrpIDProductSpecStatus(groupid, productSpecNumber, null);
            String grpUserId = usermerchpInfo.getData(0).getString("USER_ID");

            // 1- 查询用户信息,如果没有则遍历地州库
            IDataset mofficeInfo = MsisdnInfoQry.getMsdnBySn(serialNumber);

            // 2- 如果用户资料不存在,并且不允许外省成员，直接记录错误
            if (IDataUtil.isEmpty(mofficeInfo))
            {
                CSAppException.apperr(CrmUserException.CRM_USER_112, serialNumber);
            }

            //集团新下发的用户案例中   省boss发起的叠加包订购也可以不为有效成员
         
            // 3-1 新增时 判断订购关系是否 重复
            // 获取号码默认地州
/*            String defaultEparchyCode = RouteInfoQry.getEparchyCodeBySnForCrm(serialNumber);
            String userId = mofficeInfo.getData(0).getString("USER_ID");*/
            // 获取成员的订购信息，merch_meb
//            IDataset datas = UserGrpMerchMebInfoQry.getSEL_BY_USERID_USERIDA(userId, grpUserId, defaultEparchyCode);
//
//            if (IDataUtil.isEmpty(datas))
//            {
//                IData result = new DataMap();
//                result.put("BATCH_ID", batchId);
//                result.put("SERIAL_NUMBER", serialNumber);
//                result.put("DEAL_STATE", BatDealStateUtils.DEAL_STATE_B);
//                result.put("DEAL_RESULT", "导入成功");
//                result.put("DEAL_DESC", "未找到BBOSS成员订购产品信息,不能进行流量叠加包订购操作！");
//                BatDealBBossUnit.updateBatDealByBatchIdSn(result);
//
//                CSAppException.apperr(ProductException.CRM_PRODUCT_506);
//            }

            // 4集团受理模式校验
            String attr_code = "99904".equals(productSpecNumber) ? "999044008" : "999054008";
            IDataset userInfos = UserAttrInfoQry.getUserAttrByUserInstType(grpUserId, attr_code);
            if (IDataUtil.isNotEmpty(userInfos))
            {
                String payType = userInfos.getData(0).getString("ATTR_VALUE");

                // 是否订购定额统付
                if (!"4".equals(payType))
                {
                    CSAppException.apperr(GrpException.CRM_GRP_812);
                }
            }

            // 5 流量包有效性校验
            IDataset discntInfos = AttrBizInfoQry.getBizAttrByAttrValue(productid, "D", "FluxPay", rateid, null);
            if (IDataUtil.isEmpty(discntInfos))
            {
                CSAppException.apperr(GrpException.CRM_GRP_833);
            }
        }

    }

    /**
     * 统付业务启动批量
     * 
     * @param inData
     * @return
     * @throws Exception
     */
    public IDataset startBBossCenPayOpenBatDeals(String batchId, String batchOperType) throws Exception
    {

        // 1- BBOSS批量启动前相关校验
        // 1-1 查询该批量批次是否存在
        IData param = new DataMap();
        param.put("BATCH_ID", batchId);
        param.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(batchId));
        IData batchdata = BatTradeInfoQry.qryTradeBatByPK(param);

        if (IDataUtil.isEmpty(batchdata))
        {
            CSAppException.apperr(BatException.CRM_BAT_35);
        }

        // 1-2 批量条件非空校验
        String batch_task_id = batchdata.getString("BATCH_TASK_ID");
        String condString = BatTradeInfoQry.getTaskCondString(batch_task_id);

        if (StringUtils.isEmpty(condString))
        {
            CSAppException.apperr(BatException.CRM_BAT_11);
        }

        // 2- 批量条件string转换为IDataset类型
        JSONArray array = new JSONArray();
        array.element(condString);
        IDataset batchCondition = new DatasetList(array.toString());

        if (IDataUtil.isEmpty(batchCondition))
        {
            CSAppException.apperr(BatException.CRM_BAT_36);
        }

        // 3- 获取该批次的产品订购关系
        String group_id = batchCondition.getData(0).getString("GROUP_ID", "");

        IData inparams = new DataMap();
        inparams.put("BATCH_ID", batchId);
        IDataset bats = BatTradeInfoQry.queryBBossMemberInfosByBidSn(inparams, Route.getJourDb(Route.CONN_CRM_CG));// 批量明细

        checkMemUUAndRate(group_id, batchId, batchOperType, bats);

        IDataset fileNameList = new DatasetList();

        if (batchOperType.equals("BATMEBCENPAY"))
        {
            // 流量叠加包批量处理
            fileNameList = startCenPayOpenBatDeal(bats, batchId);
        }

        return fileNameList;
    }

    /**
     * @Function:
     * @Description:启动流量叠加包批量
     * @param：
     * @return：
     * @throws：
     * @author:chenyi
     * @date: 下午3:32:50 2013-10-23
     */
    public IDataset startCenPayOpenBatDeal(IDataset bats, String batch_id) throws Exception
    {
        IDataset fileNameList = new DatasetList();

        // 拼装批量执行的SQL
        // StringBuilder sql = new StringBuilder(
        // "insert into TI_B_BBOSS_USER (ORG_DOMAIN, IBSYSID_SUB, IBSYSID, PROVCODE, INFO_TYPE, MSISDN, FEEPLAN, OPERCODE, ACCOUNTNAMEREQ, PAYTYPE, PAYAMOUNT, EFFRULE, NAMEMATCH, CURRFEEPLAN, USERSTATUS, CENTROLPAYSTATUS, ACCOUNTNAME, FAILDESC, ISNEWUSER, NEWUSERFAILDESC, NEWUSERCOUNT, UPDATE_TIME, STATUS, RSRV_STR1, RSRV_STR2, RSRV_STR3, RSRV_STR4, RSRV_STR5)"
        // + "select ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,sysdate,?,?,?,?,?,? from dual");

        // Parameter[] param = new Parameter[bats.size()];
        String ibsysid = SeqMgr.getCenIbSysId();
        for (int i = 0; i < bats.size(); i++)
        {
             
            /** 构造绑定对象，按顺序绑定参数值 */
            IData bat = bats.getData(i);
            IDataset discntInfos = AttrBizInfoQry.getBizAttrByAttrValue(bat.getString("DATA2"), "D", "FluxPay", bat.getString("DATA1"), null);
            String rateId = discntInfos.getData(0).getString("ATTR_CODE");
            IData param = new DataMap();
            param.put("ORG_DOMAIN", "BOSS");
            param.put("IBSYSID_SUB", SeqMgr.getCenIbSysSubId());
            param.put("IBSYSID", ibsysid);
            param.put("UPDATE_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
            // 省代码
            param.put("PROVCODE","");
            // 信息类型
            param.put("INFO_TYPE","");
            param.put("MSISDN", bat.getString("SERIAL_NUMBER"));

            // 套餐要求
            param.put("FEEPLAN", rateId);
            // 成员操作类型
            param.put("OPERCODE", "");
            // 户名调查要求
            param.put("ACCOUNTNAMEREQ", bat.getString("DATA3"));
            // 支付类型
            param.put("PAYTYPE", bat.getString("DATA4"));
            // 支付额度
            param.put("PAYAMOUNT", bat.getString("DATA6"));
            // 账期生效规则
            param.put("EFFRULE", bat.getString("DATA9"));

            // 户名是否匹配
            param.put("NAMEMATCH", "");
            // 当前套餐
           
            param.put("CURRFEEPLAN", rateId);
            Dao.insert("TI_B_BBOSS_USER", param, Route.CONN_CRM_CEN);

        }

        IData data = new DataMap();
        data.put("IBSYSID", ibsysid);
        data.put("BATCH_ID", batch_id);
        data.put("STATUS", "1");
        data.put("UPDATE_TIME", SysDateMgr.getSysDate());
        data.put("REMARK", "");

        String fileName = BatDealBBossUnit.getMemAttachFileName();
        fileNameList.add(fileName);
        data.put("FILE_NAME", fileName);
        data.put("ORG_DOMAIN", "BOSS");

        // FILE_TYPE=7为流量统付叠加包
        data.put("FILE_TYPE", "7");

        Dao.insert("TI_B_BBOSS", data);
        // 更新状态
        BatDealBBossUnit.updateYDZFBatState(batch_id);

        return fileNameList;
    }

}
