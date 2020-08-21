
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base;

import java.util.Iterator;
import java.util.List;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.PlatException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.Clone;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.ProvinceUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bizctrl.BizCtrlBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bizctrl.BizCtrlInfo;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.util.DataBusManager;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.fee.FeeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.fee.PayMoneyData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ESOPCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.diversifyacct.DiversifyAcctUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductFeeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.SvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcStateInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.GrpInvoker;

public abstract class GroupTradeBaseBean extends TradeBaseBean
{
    protected GroupBaseReqData reqData = null;

    private void actEosInt() throws Exception
    {

        IDataset eosDataset = reqData.getEos();

        if (IDataUtil.isNotEmpty(eosDataset))
        {
            IData param = new DataMap();

            IData eosData = eosDataset.getData(0);

            String productId = reqData.getUca().getProductId();// ??,看是取什么产品id

            IData MainTrade = bizData.getTrade();
            
            // 流量自由与400充驱动流程
            if ("7342".equals(productId) || "7343".equals(productId) || "7344".equals(productId) || "9983".equals(productId)) {
                param.put("IBSYSID", eosData.getString("IBSYSID"));
                param.put("NODE_ID", eosData.getString("NODE_ID", ""));
                param.put("ORDER_ID", MainTrade.getString("ORDER_ID", ""));
                param.put("USER_ID", MainTrade.getString("USER_ID", ""));
                param.put("SERIAL_NUMBER", MainTrade.getString("SERIAL_NUMBER", ""));
                param.put("TRADE_ID", MainTrade.getString("TRADE_ID", ""));

                // 还需要测试老流程，暂时不管报错，也不返回
              
                  CSAppCall.call("SS.EopIntfSVC.saveEopNodeAndDrive", param);
               

                // return;
            }

            if ("NEWFLAG".equals(eosData.getString("RSRV_STR5"))) {// 政企订单中心改造
                IData params = new DataMap();
                params.put("IBSYSID", eosData.getString("IBSYSID"));
                params.put("USER_ID", MainTrade.getString("USER_ID", ""));
                params.put("TRADE_ID", this.getTradeId());
                params.put("SERIAL_NUMBER", MainTrade.getString("SERIAL_NUMBER", ""));
                params.put("BATCH_ID", MainTrade.getString("BATCH_ID", ""));
                String tradeTypeCode = MainTrade.getString("TRADE_TYPE_CODE");
                if ("3080".equals(tradeTypeCode) || "3010".equals(tradeTypeCode)) {
                    IData productParam = new DataMap();
                    productParam.put("IBSYSID", eosData.getString("IBSYSID"));
                    productParam.put("TRADE_ID", this.getTradeId());
                    productParam.put("RECORD_NUM", "0");
                    CSAppCall.call("SS.EopIntfSVC.updateEopProduct", productParam);
                }
                if ("3086".equals(tradeTypeCode) || "3016".equals(tradeTypeCode) || "3018".equals(tradeTypeCode) || "2990".equals(tradeTypeCode) || "2991".equals(tradeTypeCode)|| "3849".equals(tradeTypeCode)) {
                    IData productParam = new DataMap();
                    productParam.put("IBSYSID", eosData.getString("IBSYSID"));
                    productParam.put("TRADE_ID", MainTrade.getString("TRADE_ID"));
                    productParam.put("RECORD_NUM", eosData.getString("RSRV_STR6"));
                    CSAppCall.call("SS.EopIntfSVC.updEopProductSub", productParam);
                }
                /* if ("3086".equals(tradeTypeCode) || "3016".equals(tradeTypeCode)) { params.put("RECORD_NUM", eosData.getString("RSRV_STR6")); CSAppCall.call("SS.EopIntfSVC.updEopProductSub", params); } */
            }else{
            	 param.put("USER_ID", MainTrade.getString("USER_ID", ""));

                 param.put("IBSYSID", eosData.getString("IBSYSID"));
                 param.put("NODE_ID", eosData.getString("NODE_ID", ""));
                 param.put("TRADE_ID", this.getTradeId());
                 param.put("BPM_TEMPLET_ID", eosData.getString("BPM_TEMPLET_ID", ""));
                 param.put("MAIN_TEMPLET_ID", eosData.getString("MAIN_TEMPLET_ID", ""));
                 param.put("ROLE_ID", CSBizBean.getVisit().getStaffRoles());

                 param.put("CITY_CODE", CSBizBean.getVisit().getCityCode());
                 param.put("DEPART_ID", CSBizBean.getVisit().getDepartId());
                 param.put("DEPART_NAME", CSBizBean.getVisit().getDepartName());
                 param.put("EPARCHY_CODE", BizRoute.getRouteId());
                 param.put("STAFF_ID", CSBizBean.getVisit().getStaffId());
                 param.put("STAFF_NAME", CSBizBean.getVisit().getStaffName());
                 param.put("X_TRANS_CODE", "ITF_EOS_TcsGrpBusi");
                 param.put("X_SUBTRANS_CODE", "SaveAndSend");
                 param.put("OPER_CODE", "01");
                 param.put("DEAL_STATE", "2");
                 if (!"".equals(eosData.getString("SUB_IBSYSID", "")))
                 {
                     param.put("SUB_SUBSCRIBE_ID", eosData.getString("SUB_IBSYSID", ""));// 专线子流程订单号
                 }

                 param.put("ORIG_DOMAIN", "ECRM"); // 发起方应用域代码
                 param.put("HOME_DOMAIN", "ECRM"); // 归属方应用域代码
                 param.put("BIPCODE", "EOS2D011"); // 业务交易代码 这个编码要传进来
                 param.put("ACTIVITYCODE", "T2011011"); // 交易代码 这个编码也要传进来
                 param.put("BUSI_SIGN", ""); // 报文类型，BPM要基于此判断
                 param.put("WORK_TYPE", "00"); // 提交类型
                 param.put("PROCESS_TIME", this.getAcceptTime()); // 处理时间
                 param.put("ACCEPT_DATE", this.getAcceptTime()); // 受理时间
                 param.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode()); // 受理地州
                 param.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId()); // 受理员工
                 param.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId()); // 受理部门
                 param.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
                 param.put("WORK_ID", eosData.getString("WORK_ID", "")); // BPM工作标识,界面提交时传其它不传
                 param.put("X_RESULTINFO", "TradeOk");

                 // add by humh3 for esop 本地专线 20130326 start
                 IData productParam = reqData.cd.getProductParamMap(productId);

                 String MemUserId = reqData.getUca().getUserId(); // 成员用户标识
                 if (IDataUtil.isNotEmpty(productParam))
                 {
                     Iterator<String> iteratorParam = productParam.keySet().iterator();
                     while (iteratorParam.hasNext())
                     {
                         String keyParam = iteratorParam.next();
                         String valueParam = productParam.getString(keyParam, "");
                         if (!"".equals(MemUserId) && keyParam.equals("PRODUCT_NO"))
                         {// 本地专线特有参数
                             // 将业务标识,成员USERID提供给EOS
                             param.put("USER_ID", "");// 清空，要不然会覆盖集团开户的信息
                             param.put("TRADE_ID", "");// 清空，要不然会覆盖集团开户的信息
                             param.put("PRODUCT_NO", valueParam);
                             param.put("MEB_USER_ID", MemUserId);
                             param.put("MEB_TRADE_ID", this.getTradeId());
                         }
                     }
                 }
                 // add by humh3 for esop 本地专线 20130326 end

                 if ("dealPage".equals(eosData.getString("NODE_ID", "")))
                 {// 成员节点
                     param.put("USER_ID", "");// 清空，要不然会覆盖集团开户的信息
                     param.put("TRADE_ID", "");// 清空，要不然会覆盖集团开户的信息
                     param.put("MEB_USER_ID", MainTrade.getString("USER_ID", ""));
                     param.put("MEB_TRADE_ID", this.getTradeId());
                     param.put("USER_ID_A", MainTrade.getString("USER_ID_B", ""));
                 }

                 param.put("X_RESULTCODE", "0");
                 param.put("X_SUBTRANS_CODE", "SaveAndSend");
                 if (!eosData.getString("WORK_ID", "").equals(""))
                 {
                    ESOPCall.callESOP("ITF_EOS_TcsGrpBusi", param);
                 }
             }
        }

           

    }

    private void actTradeAttr() throws Exception
    {
        IDataset dataset = reqData.cd.getElementParam();

        if (IDataUtil.isEmpty(dataset))
        {
            return;
        }

        super.addTradeAttr(dataset);
    }
    
    @Override
    protected void actTradeBase() throws Exception
    {
        // 服务
        actTradeSvc();

        // SP服务
        actTradeSpSvc();

        // 优惠
        actTradeDiscnt();
        
        // 资源
        actTradeRes();

        // 成员定制
        actTradeGrpPackage();

        // 产品个性化参数
        actTradeAttr();

        // 台帐费用子表
        actTradefeeSub();

        // 付款方式子表
        actTradePayMode();

        // ESOP端到端数据
        actTradeEos();

    }

    @Override
    protected void actTradeBefore() throws Exception
    {
        super.actTradeBefore();
        
    }

    private void actTradeDiscnt() throws Exception
    {
        IDataset dataset = reqData.cd.getDiscnt();

        if (IDataUtil.isEmpty(dataset))
        {
            return;
        }

        super.addTradeDiscnt(dataset);
    }
    
    private void actTradeEos() throws Exception
    {
        IDataset dataset = reqData.getEos();

        if (IDataUtil.isNotEmpty(dataset))
        {
            for (int i = 0, size = dataset.size(); i < size; i++)
            {
                IData map = dataset.getData(i);
                map.put("RSRV_STR10", map.getString("RSRV_STR10", "EOS"));
            }
            super.addTradeExt(dataset);
        }
    }

    protected void actTradefeeSub() throws Exception
    {
        IDataset dataset = new DatasetList();

        List<FeeData> feeList = reqData.getFeeList();
        if (feeList == null || feeList.size() == 0)
        {
            return;
        }
        for (FeeData fd : feeList)
        {
            dataset.add(fd.toData());
        }

        if (IDataUtil.isEmpty(dataset))
        {
            return;
        }

        super.addTradefeeSub(dataset);
    }

    private void actTradeGrpPackage() throws Exception
    {
        IDataset dataset = reqData.cd.getGrpPackage();

        if (IDataUtil.isEmpty(dataset))
        {
            return;
        }

        super.addTradeGrpPackage(dataset);
    }

    protected void actTradePayMode() throws Exception
    {
        IDataset dataset = new DatasetList();

        List<PayMoneyData> payMoneyList = reqData.getPayMoneyList();

        if (payMoneyList == null || payMoneyList.size() == 0)
        {
            return;
        }

        for (PayMoneyData fd : payMoneyList)
        {
            dataset.add(fd.toData());
        }

        //集团彩铃的特殊处理 ***add by xushifu 企业一卡通特殊处理
        String tradeTypeCode = getTradeTypeCode();
               
        if(!StringUtils.equals(tradeTypeCode, "2950") && !StringUtils.equals(tradeTypeCode, "3660"))
        {
            // 处理付费方式子表
            super.addTradefeePayMoney(dataset);
        }

        IDataset deferDataset = new DatasetList();
        IDataset checkDataset = new DatasetList();

        for (int i = 0, row = dataset.size(); i < row; i++)
        {
            IData data = dataset.getData(i);

            // 得到付款方式
            String payMoneyCode = data.getString("PAY_MONEY_CODE");

            if (payMoneyCode.equals("1"))
            {
                // 支票
                checkDataset.add(data);
            }
            else if (payMoneyCode.equals("2"))
            {
                // 挂帐
                deferDataset.add(data);
            }
        }

        // 处理支票子表
        if (checkDataset.size() > 0)
        {
            setRegCheck(checkDataset);
        }

        // 处理挂账子表
        if (deferDataset.size() > 0)
        {
            // 把前台显示的费用列表的营业费挂账, 总的挂账费用不保存
            IDataset feeList = new DatasetList();

            List<FeeData> feeList_ = reqData.getFeeList();

            for (FeeData fd : feeList_)
            {
                feeList.add(fd.toData());
            }

//            if (IDataUtil.isNotEmpty(feeList))
//            {
//                IDataset tradefeeList = ProductFeeInfoQry.getFee(getTradeTypeCode(), "4", feeList.getData(0).getString("FEE_TYPE_CODE"));
//
//                if (IDataUtil.isNotEmpty(tradefeeList))
//                {
//                    deferDataset.clear();
//
//                    for (int i = 0, row = feeList.size(); i < row; i++)
//                    {
//                        IData feeData = feeList.getData(i);
//
//                        IData addDeferData = new DataMap();
//
//                        addDeferData.put("ACT_TAG", "1");
//                        addDeferData.put("FEE_MODE", feeData.getString("FEE_MODE")); // 费用类型：0-营业费用项，1-押金，2-预存
//                        addDeferData.put("FEE_TYPE_CODE", "41"); // 营业费用类型 41
//                        addDeferData.put("DEFER_CYCLE_ID", SysDateMgr.getNowCyc()); // 当前账期
//                        addDeferData.put("DEFER_ITEM_CODE", feeData.getString("RSRV_STR1")); // 挂帐帐目:为明细帐目,不指定时挂到默认的帐目
//                        addDeferData.put("MONEY", feeData.getString("FACT_PAY_FEE")); // 金额
//
//                        deferDataset.add(addDeferData);
//                    }
//                }
//            }

            super.addTradefeeDefer(deferDataset);
        }
    }

    protected final void actTradeRes() throws Exception
    {
        IDataset dataset = reqData.cd.getRes();

        if (IDataUtil.isEmpty(dataset))
        {
            return;
        }

        super.addTradeRes(dataset);
    }

    private void actTradeSpSvc() throws Exception
    {
        IDataset spSvcList = reqData.cd.getSpSvc();

        if (IDataUtil.isEmpty(spSvcList))
        {
            return;
        }

        for (int i = 0, row = spSvcList.size(); i < row; i++)
        {
            IData spSvcData = spSvcList.getData(i);

            String serviceId = spSvcData.getString("ELEMENT_ID");

            // 获取平台服务信息
            IData param = new DataMap();
            param.put("SERVICE_ID", serviceId);

            IDataset bizDataList = PlatSvcInfoQry.querySvcBizInfo(param);

            if (IDataUtil.isEmpty(bizDataList))
            {
                CSAppException.apperr(PlatException.CRM_PLAT_1, serviceId);
            }

            // MODIFY_TAG转义
            String modifyTag = spSvcData.getString("MODIFY_TAG", "");
            String servType = spSvcData.getString("SERV_TYPE", "");

            String operCode = "01";

            if (TRADE_MODIFY_TAG.Add.getValue().equals(modifyTag))
            {
                operCode = "0".equals(servType) ? "01" : "06";
            }
            else if (TRADE_MODIFY_TAG.MODI.getValue().equals(modifyTag))
            {
                operCode = "0".equals(servType) ? "02" : "07";
            }
            else if (TRADE_MODIFY_TAG.DEL.getValue().equals(modifyTag))
            {
                operCode = "08";
            }

            IData bizData = bizDataList.getData(0);
            bizData.put("USER_ID", reqData.getUca().getUserId());
            bizData.put("SERIAL_NUMBER", reqData.getUca().getSerialNumber());
            bizData.put("OPER_CODE", operCode);
            bizData.put("GIFT_SERIAL_NUMBER", operCode);

            if (operCode.matches("01|07"))
            {
                spSvcData.put("BIZ_STATE_CODE", "E");
            }
            else if (operCode.matches("04|14"))
            {
                spSvcData.put("BIZ_STATE_CODE", "N");
            }
            else
            {
                spSvcData.put("BIZ_STATE_CODE", "A");
            }

            // 避免新设置的 BIZ_STATE_CODE 的值被覆盖
            bizData.remove("BIZ_STATE_CODE");

            spSvcData.put("PRODUCT_ID", spSvcData.getString("PRODUCT_ID", ""));
            spSvcData.put("PACKAGE_ID", spSvcData.getString("PACKAGE_ID", ""));
            spSvcData.put("OPR_SOURCE", "08"); // 操作来源 08 营业厅

            spSvcData.put("SERVICE_ID", serviceId);

            spSvcData.put("OPER_TIME", getAcceptTime());

            spSvcData.put("IS_NEED_PF", spSvcData.getString("IS_NEED_PF", "1"));

            spSvcData.putAll(bizData);
        }

        super.addTradePlatsvc(spSvcList);
    }

    @Override
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();
    }

    protected final void actTradeSvc() throws Exception
    {
        IDataset dataset = reqData.cd.getSvc();

        if (IDataUtil.isEmpty(dataset))
        {
            return;
        }

        super.addTradeSvc(dataset);
    }

    /**
     * 处理服务状态
     * 
     * @throws Exception
     */
    protected void actTradeSvcState() throws Exception
    {
        IDataset svcList = reqData.cd.getSvc();

        if (IDataUtil.isEmpty(svcList))
        {
            return;
        }

        IDataset svcStateList = new DatasetList();

        for (int i = 0, row = svcList.size(); i < row; i++)
        {
            IData svcData = svcList.getData(i);
            String modifyTag = svcData.getString("MODIFY_TAG", "");

            if (TRADE_MODIFY_TAG.Add.getValue().equals(modifyTag))
            {
                // 处理新增服务的服务状态
                addSvcState(svcData, svcStateList);
            }
            else if (TRADE_MODIFY_TAG.DEL.getValue().equals(modifyTag))
            {
                // 处理删除服务的服务状态
                delSvcState(svcData, svcStateList);
            }
        }

        super.addTradeSvcstate(svcStateList);
    }

    public void addPlatSvcAttrChildTrade(IDataset datas) throws Exception
    {

        for (int row = 0; row < datas.size(); row++)
        {
            IData map = datas.getData(row);
            IData data = new DataMap();

            data.put("REMARK", "平台业务类操作"); // 备注

            data.put("SERVICE_ID", map.getString("SERVICE_ID", ""));
            data.put("INFO_CODE", map.getString("INFO_CODE", ""));
            data.put("INFO_VALUE", map.getString("INFO_VALUE", ""));
            data.put("INFO_NAME", map.getString("INFO_NAME", ""));
            data.put("RSRV_NUM1", map.getString("RSRV_NUM1", ""));
            data.put("RSRV_NUM2", map.getString("RSRV_NUM2", ""));
            data.put("RSRV_STR1", map.getString("RSRV_STR1", ""));
            data.put("RSRV_STR2", map.getString("RSRV_STR2", ""));
            data.put("RSRV_DATE1", map.getString("RSRV_DATE1", ""));
            data.put("RSRV_DATE2", map.getString("RSRV_DATE2", ""));
            data.put("RSRV_DATE3", map.getString("RSRV_DATE3", ""));
            data.putAll(map);

            addTradePlatsvcAttr(data);
        }
    }

    /**
     * 处理新增服务的服务状态
     * 
     * @param svcData
     *            服务信息
     * @param svcStateList
     *            服务状态列表
     * @throws Exception
     */
    public void addSvcState(IData svcData, IDataset svcStateList) throws Exception
    {
        String userId = reqData.getUca().getUser().getUserId();
        String serviceId = svcData.getString("SERVICE_ID");

        // 查询用户服务状态
        IDataset userSvcStateList = UserSvcStateInfoQry.getUserLastStateByUserSvc(userId, serviceId);

        // 注销原有的服务状态
        if (IDataUtil.isNotEmpty(userSvcStateList))
        {
            for (int i = 0, row = userSvcStateList.size(); i < row; i++)
            {
                IData userSvcSateData = userSvcStateList.getData(i);

                userSvcSateData.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                userSvcSateData.put("END_DATE", diversifyBooking ? DiversifyAcctUtil.getLastDayThisAcct(userId) : getAcceptTime());

                svcStateList.add(userSvcSateData);
            }
        }

        // 新增服务状态
        IData addSvcStateData = (IData) Clone.deepClone(svcData);

        // 查询主体服务状态
        String mainTag = SvcInfoQry.queryMainTagByPackageIdAndServiceId(svcData.getString("PRODUCT_ID"), svcData.getString("PACKAGE_ID"), serviceId);
        
        addSvcStateData.put("STATE_CODE", "0"); // 正常
        addSvcStateData.put("MAIN_TAG", mainTag);
        addSvcStateData.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
        addSvcStateData.put("INST_ID", SeqMgr.getInstId());// 实例ID

        if (diversifyBooking)
        {
            addSvcStateData.put("START_DATE", DiversifyAcctUtil.getFirstTimeNextAcct(userId));
        }

        svcStateList.add(addSvcStateData);
    }

    @Override
    protected void callOutIntf() throws Exception
    {
        super.callOutIntf();
        actEosInt();
    }

    protected void chkTradeBefore(IData map) throws Exception
    {
        if (ProvinceUtil.isProvince(ProvinceUtil.HAIN))
        {
            if (!map.getBoolean("NEED_RULE", true))// 告知不需要规矩校验直接放行 false为不校验规则
            {
                return;
            }
        }

        super.chkTradeBefore(map);
        String validateCtrlClass = reqData.getValidateCtrlClass();
        String validateMethod = reqData.getValidateMethod();
        if (StringUtils.isNotEmpty(validateCtrlClass) && StringUtils.isNotEmpty(validateMethod))
        {
            GrpInvoker.invoker(validateCtrlClass, validateMethod, new Object[]
            { map }, new Class[]
            { IData.class });
        }
    }

    /**
     * 处理删除服务的服务状态
     * 
     * @param svcData
     * @param svcStateList
     * @throws Exception
     */
    public void delSvcState(IData svcData, IDataset svcStateList) throws Exception
    {
        String userId = reqData.getUca().getUser().getUserId();
        String serviceId = svcData.getString("SERVICE_ID");

        // 获取用户服务状态信息
        IDataset userSvcStateList = UserSvcStateInfoQry.getUserLastStateByUserSvc(userId, serviceId);

        // 处理服务状态信息
        if (IDataUtil.isNotEmpty(userSvcStateList))
        {
            for (int i = 0, row = userSvcStateList.size(); i < row; i++)
            {
                IData userSvcStateData = userSvcStateList.getData(i);

                // 删除原有服务状态信息
                userSvcStateData.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                userSvcStateData.put("END_DATE", diversifyBooking ? DiversifyAcctUtil.getLastDayThisAcct(userId) : getAcceptTime());

                // 新增无效的服务状态信息
                IData delSvcStateData = (IData) Clone.deepClone(userSvcStateData);

                delSvcStateData.put("STATE_CODE", "1");// 销户
                delSvcStateData.put("START_DATE", diversifyBooking ? DiversifyAcctUtil.getFirstTimeNextAcct(userId) : getAcceptTime());
                delSvcStateData.put("END_DATE", SysDateMgr.getTheLastTime());
                delSvcStateData.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                delSvcStateData.put("INST_ID", SeqMgr.getInstId());

                // 添加服务状态信息
                svcStateList.add(userSvcStateData);
                svcStateList.add(delSvcStateData);
            }
        }
    }

    /**
     * 过滤filterType开头的属性值
     * 
     * @param filterType
     * @param params
     * @return
     * @throws Exception
     */
    protected IDataset filterParamAttr(String filterType, IDataset params) throws Exception
    {
        if (IDataUtil.isEmpty(params))
        {
            return params;
        }

        for (int i = params.size() - 1; i >= 0; i--)
        {
            IData param = params.getData(i);
            if (param.getString("ATTR_CODE").startsWith(filterType))
            {
                params.remove(i);
            }
        }

        return params;
    }

    public String getAttrState(IData param, String state) throws Exception
    {

        if (state.equals("MODI") || state.equals("DEL"))
        {
            IData map = new DataMap();
            map.put("USER_ID", param.getString("USER_ID"));
            map.put("INST_ID", param.getString("INST_ID"));
            map.put("INST_TYPE", param.getString("INST_TYPE"));
            map.put("ATTR_CODE", param.getString("ATTR_CODE"));

            IDataset datas = UserAttrInfoQry.getUserAttrSingleByPK(param.getString("USER_ID"), param.getString("ATTR_CODE"), param.getString("INST_ID"), param.getString("INST_TYPE"));
            if (datas != null && datas.size() > 0)
            {
                return state;
            }
            else
            {
                if (state.equals("MODI"))
                {
                    return "ADD";
                }
                else
                {
                    return "EXIST";
                }
            }
        }
        else
        {
            return state;
        }
    }

    public String getCancelDate(String startDate, String endDate) throws Exception
    {

        String cancelDate = endDate.substring(0, 10);
        String sysDate = getAcceptTime().substring(0, 10);

        // 未生效时注销
        if (getAcceptTime().compareTo(startDate) < 0)
        {
            // 当前时间减一秒
            return SysDateMgr.getLastSecond(getAcceptTime());
        }
        // 当前时间
        if (cancelDate.compareTo(sysDate) == 0)
        {
            return getAcceptTime();
        }
        // 非立即生效
        else
        {
            return cancelDate + SysDateMgr.getEndTime235959();
        }
    }

    public String getEnableDate(String date) throws Exception
    {

        String enableDate = date.substring(0, 10);
        String sysDate = getAcceptTime().substring(0, 10);

        // 当前时间
        if (enableDate.compareTo(sysDate) == 0)
        {
            return getAcceptTime();
        }
        else
        {
            // 非立即生效
            return enableDate + SysDateMgr.getFirstTime00000();
        }
    }

    protected void getProductCtrlInfo(String productId, String ctrlType) throws Exception
    {
        // 产品控制信息
        BizCtrlInfo ctrlInfo = BizCtrlBean.qryProductCtrlInfo(productId, ctrlType);

        reqData.setValidateMethod(ctrlInfo.getAttrStr1Value("Validate"));
        reqData.setProductCtrlInfo(productId, ctrlInfo);
    }

    @Override
    protected BaseReqData getReqData() throws Exception
    {
        return new GroupBaseReqData();
    }

    @Override
    protected void init() throws Exception
    {
        // 初始化产品控制信息
        initProductCtrlInfo();
    }

    protected void initProductCtrlInfo() throws Exception
    {

    }

    @Override
    protected void initReqData() throws Exception
    {
        super.initReqData();

        reqData = (GroupBaseReqData) getBaseReqData();
    }

    @Override
    protected void makInit(IData map) throws Exception
    {
        super.makInit(map);
        reqData.setEos(map.getDataset("EOS"));
        reqData.setBatchDealType(map.getString("BATCH_DEAL_TYPE"));
        reqData.setMemFinish(map.getString("MEM_FINISH"));// MEM_FINISH HuNan只有专线业务才会传true
        // reqData.setEsopTag(IDataUtil.isNotEmpty(map.getDataset("EOS")));
        reqData.setEffectNow(map.getBoolean("EFFECT_NOW", false)); // 不传默认不立即生效 按产品资费配置来
        reqData.setDevStaffId(map.getString("DEVELOP_STAFF_ID"));
        reqData.setBatchOperType(map.getString("BATCH_OPER_TYPE"));

        reqData.setIfBooking(map.getBoolean("IF_BOOKING", false)); // 是否预约

        reqData.setNeedSms(map.getBoolean("IF_SMS", true)); // 是否发短信 默认发

        if (reqData.isEffectNow())
        {
            effectNow = reqData.isEffectNow();
        }

        if (reqData.isIfBooking())
        {
            diversifyBooking = reqData.isIfBooking();
        }

        reqData.setAcceptTime(getAcceptTime());

        map.put("IF_BOOKING", reqData.isIfBooking());// 有些规则需要用到，明确告诉规则是否预约

    }
    
    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);
        
    }
    
    public void modiSvcState(String svcId, IDataset result, String eparchyCode, String newstate) throws Exception
    {

        String USER_ID = reqData.getUca().getUser().getUserId();

        IDataset results = UserSvcStateInfoQry.getUserLastStateByUserSvc(USER_ID, svcId);

        for (int i = 0; i < results.size(); i++)
        {
            IData map = results.getData(i);
            map.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
            map.put("END_DATE", getAcceptTime());

            IData map1 = new DataMap();
            map1.putAll(map);
            map1.put("STATE_CODE", newstate);// 新状态
            map1.put("START_DATE", getAcceptTime());
            map1.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
            map1.put("END_DATE", SysDateMgr.getTheLastTime());
            map1.put("INST_ID", SeqMgr.getInstId());// 实例ID

            result.add(map);
            result.add(map1);
        }
    }

    @Override
    protected void regTrade() throws Exception
    {
        IData data = bizData.getTrade();

        data.put("BRAND_CODE", "-1");// 主台账品牌默认为-1
        data.put("EXEC_TIME", getAcceptTime());
        
        // 营业前台操作，有费用需要支付后才能跑单子 先改成X状态 ，其它暂时先不管
        IDataset tradeFeeSubs = bizData.getTradefeeSub();
        if (IDataUtil.isNotEmpty(tradeFeeSubs) && StringUtils.equals("0", CSBizBean.getVisit().getInModeCode()))
        {
            if((Double.valueOf(tradeFeeSubs.getData(0).getString("FEE","0"))).longValue() >= 0){
                data.put("SUBSCRIBE_STATE", "X");// 未支付
                DataBusManager.getDataBus().setSubscribeStateX("X");// 融合业务需要
            }
        }
    }
    
    protected void regOrder() throws Exception
    {
        IData data = bizData.getOrder();

        // 营业前台操作，有费用需要支付后才能跑单子 先改成X状态 ，其它暂时先不管
        IDataset tradeFeeSubs = bizData.getTradefeeSub();
        if (IDataUtil.isNotEmpty(tradeFeeSubs) && StringUtils.equals("0", CSBizBean.getVisit().getInModeCode()))
        {
            if((Double.valueOf(tradeFeeSubs.getData(0).getString("FEE","0"))).longValue() >= 0){
                data.put("ORDER_STATE", "X");// 未支付
            }
        }
    }

    protected void setBindDiscntUserIdA(IData map) throws Exception
    {

    }

    protected void setRegCheck(IDataset datas) throws Exception
    {

        for (int row = 0; row < datas.size(); row++)
        {
            IData map = (IData) datas.get(row);

            String strCheckMoney = map.getString("CHECK_MONEY", "0");

            if (strCheckMoney == null || "".equals(strCheckMoney))
            {
                strCheckMoney = "0";
            }

            double money = Double.valueOf(strCheckMoney);

            // 如果费用是0就不登记这条
            if (money == 0)
            {
                continue;
            }

            IData data = new DataMap();

            data.put("CHECK_CARD_NO", map.getString("CHECK_CARD_NO", ""));// 支票号码
            data.put("CHECK_CARD_NAME", map.getString("CHECK_CARD_NAME", ""));// 支票名称
            data.put("CHECK_BANK_CODE", map.getString("CHECK_BANK_CODE", ""));// 银行编码

            // 支票金额 luojh
            data.put("CHECK_MONEY", StrUtil.doubleToStr(money));

            // 支票限额
            String limit = String.valueOf(Double.valueOf(map.getString("CHECK_LIMIT", "0.00")));
            data.put("CHECK_LIMIT", limit.substring(0, limit.indexOf('.')));

            data.put("CHECK_TAG", map.getString("CHECK_TAG", ""));// 支票到帐标志：0-未到帐，1-到帐
            data.put("RECEIVE_DATE", map.getString("RECEIVE_DATE", ""));// 支票到帐时间

            data.put("REMARK", map.getString("REMARK", ""));// 备注

            data.put("RSRV_STR1", map.getString("RSRV_STR1", ""));// 预留字段1
            data.put("RSRV_STR2", map.getString("RSRV_STR2", ""));// 预留字段2
            data.put("RSRV_STR3", map.getString("RSRV_STR3", ""));// 预留字段3
            data.put("RSRV_STR4", map.getString("RSRV_STR4", ""));// 预留字段4
            data.put("RSRV_STR5", map.getString("RSRV_STR5", ""));// 预留字段5
            data.put("RSRV_STR6", map.getString("RSRV_STR6", ""));// 预留字段6
            data.put("RSRV_STR7", map.getString("RSRV_STR7", ""));// 预留字段7
            data.put("RSRV_STR8", map.getString("RSRV_STR8", ""));// 预留字段8
            data.put("RSRV_STR9", map.getString("RSRV_STR9", ""));// 预留字段9
            data.put("RSRV_STR10", map.getString("RSRV_STR10", ""));// 预留字段10

            addTradefeeCheck(data);
        }
    }

    @Override
    protected String setTradeId() throws Exception
    {

        // 生成业务流水号
        String id = SeqMgr.getTradeId();

        return id;
    }

}
