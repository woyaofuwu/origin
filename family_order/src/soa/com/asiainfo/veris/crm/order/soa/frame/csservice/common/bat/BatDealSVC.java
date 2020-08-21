
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.bat;

import net.sf.json.JSONArray;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.BatException;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.util.DataSetUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.bat.bean.BatDealBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.bat.BatDealInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.bat.BatInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.bat.BatTaskInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.bat.BatchTypeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustomerInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.DiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PkgInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductTypeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.PayRelaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserTelephoeInfoQry;

public class BatDealSVC extends CSBizService
{

    private static final long serialVersionUID = -3724416125656198579L;

    /**
     * 根据返销状态等查询详细
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset batchDetialQuery(IData data) throws Exception
    {
        return BatDealInfoQry.batchDetialAllQuery(data, this.getPagination());
    }
    /**
     * 根据返销状态等查询详细,添加对实名制的校验
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset batchDetialQry(IData data) throws Exception
    {
    	return BatDealInfoQry.batchDetialAllQry(data, this.getPagination());
    }

    /**
     * 根据返销状态等查询VPMN详细
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset batchDetialQueryVPMN(IData data) throws Exception
    {
        return BatDealInfoQry.batchDetialQueryAllVPMN(data, this.getPagination(), Route.getJourDb(Route.CONN_CRM_CG));
    }

    /**
     * 删除批次信息
     */
    public IDataset batTaskNowCancelRunForGrp(IData input) throws Exception
    {
        IDataUtil.chkParam(input, "BATCH_IDS");

        BatDealBean bean = (BatDealBean) BeanManager.createBean(BatDealBean.class);

        String batchIds = input.getString("BATCH_IDS");

        String[] batchIdSet = batchIds.split(",");

        IData data = new DataMap();

        for (int i = 0, size = batchIdSet.length; i < size; i++)
        {
            String batchId = batchIdSet[i];

            data.clear();

            data.put("BATCH_ID", batchId);

            bean.batTaskNowDelete(data);
        }

        return new DatasetList();
    }

    // 批量任务立即删除
    public boolean batTaskNowDelete(IData input) throws Exception
    {
        BatDealBean bean = (BatDealBean) BeanManager.createBean(BatDealBean.class);
        return bean.batTaskNowDelete(input);
    }

    // 批量任务立即启动
    public boolean batTaskNowRun(IData input) throws Exception
    {
        BatDealBean bean = (BatDealBean) BeanManager.createBean(BatDealBean.class);
        return bean.batTaskNowRun(input);
    }

    /**
     * 批量任务立即启动
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public boolean batTaskNowRunForGrp(IData input) throws Exception
    {
        return BatDealBean.batTaskNowRunForGrp(input);
    }

    // 批量任务预约启动
    public boolean batTaskOnTimeRun(IData input) throws Exception
    {
        BatDealBean bean = (BatDealBean) BeanManager.createBean(BatDealBean.class);
        return bean.batTaskOnTimeRun(input);
    }

    // 根据服务号码信息返销
    public IDataset cancelByBatchid(IData input) throws Exception
    {
        BatDealBean bean = (BatDealBean) BeanManager.createBean(BatDealBean.class);
        int cancelResult = bean.cancelByBatchid(input, getPagination());
        IData result = new DataMap();
        result.put("CANCELRESULT", cancelResult);
        return IDataUtil.idToIds(result);
    }
    
    // 根据批次号返销,增加对实名制激活的校验
    public IDataset cancelByBatid(IData input) throws Exception
    {
    	BatDealBean bean = (BatDealBean) BeanManager.createBean(BatDealBean.class);
    	int cancelResult = bean.cancelByBatid(input, getPagination());
    	IData result = new DataMap();
    	result.put("CANCELRESULT", cancelResult);
    	return IDataUtil.idToIds(result);
    }

    // 根据服务号码信息返销
    public IDataset cancelBySerialNum(IData input) throws Exception
    {
        BatDealBean bean = (BatDealBean) BeanManager.createBean(BatDealBean.class);
        int cancelResult = bean.cancelBySerialNum(input, getPagination());
        IData result = new DataMap();
        result.put("CANCELRESULT", cancelResult);
        return IDataUtil.idToIds(result);
    }

    public IDataset checkForAppendTrunk(IData mainUserData) throws Exception
    {
        String mainSn = mainUserData.getString("SERIAL_NUMBER");
        mainUserData.put("TRADE_ID", SeqMgr.getTradeId());
        mainUserData.put("ORDER_ID", SeqMgr.getOrderId());

        IData userData = UcaInfoQry.qryUserInfoBySn(mainSn);

        if (IDataUtil.isEmpty(userData))
        {
            CSAppException.apperr(BatException.CRM_BAT_85);
        }

        String userId = userData.getString("USER_ID");
        String custId = userData.getString("CUST_ID");

        mainUserData.put("MAIN_USER_ID", userData.getString("USER_ID"));
        mainUserData.put("MAIN_CUST_ID", userData.getString("CUST_ID"));
        mainUserData.put("MAIN_SERIAL_NUMBER", userData.getString("SERIAL_NUMBER"));

        IDataset payRelationSet = PayRelaInfoQry.queryDefaultPayRelaByUserId(userId);
        if (payRelationSet.size() == 0)
        {
            CSAppException.apperr(BatException.CRM_BAT_81);
        }
        mainUserData.put("MAIN_ACCT_ID", payRelationSet.getData(0).getString("ACCT_ID"));

        IDataset productDatas = UserProductInfoQry.queryMainProductNow(userId);
        if (productDatas.isEmpty())
        {
            CSAppException.apperr(BatException.CRM_BAT_85);
        }
        IData productDataAll = productDatas.getData(0);
        IData productData = new DataMap();
        productData.put("PRODUCT_ID", productDataAll.getString("PRODUCT_ID"));
        productData.put("PRODUCT_MODE", "00");
        productData.put("BRAND_CODE", productDataAll.getString("BRAND_CODE"));

        IDataset userMainSvcSet = UserSvcInfoQry.qryUserSvcByUserId(userId);
        if (userMainSvcSet.size() == 0)
        {
            CSAppException.apperr(BatException.CRM_BAT_82);
        }
        IData userMainSvc = null;
        for (int i = 0; i < userMainSvcSet.size(); i++)
        {
            if (userMainSvcSet.getData(i).getString("MAIN_TAG").equals("1"))
            {
                userMainSvc = userMainSvcSet.getData(i);
                break;
            }
        }
        if (userMainSvc == null)
        {
            CSAppException.apperr(BatException.CRM_BAT_82);
        }

        IData svcData = new DataMap();
        svcData.put("ELEMENT_ID", userMainSvc.getString("SERVICE_ID"));
        svcData.put("PACKAGE_ID", userMainSvc.getString("PACKAGE_ID"));
        svcData.put("PRODUCT_ID", userMainSvc.getString("PRODUCT_ID"));
        svcData.put("START_DATE", userMainSvc.getString("START_DATE"));
        svcData.put("END_DATE", userMainSvc.getString("END_DATE"));
        svcData.put("ENABLE_TAG", "0");
        svcData.put("DEFAULT_TAG", "1");
        svcData.put("MAIN_TAG", "1");

        IData custPerData = UcaInfoQry.qryPerInfoByCustId(custId);
        if (custPerData.size() == 0)
        {
            CSAppException.apperr(BatException.CRM_BAT_83);
        }

        IData custData = new DataMap();
        custData.put("PSPT_TYPE_CODE", custPerData.getString("PSPT_TYPE_CODE"));
        custData.put("PSPT_ID", custPerData.getString("PSPT_ID"));
        custData.put("CUST_NAME", custPerData.getString("CUST_NAME"));
        custData.put("POST_ADDRESS", custPerData.getString("POST_ADDRESS"));
        custData.put("PHONE", custPerData.getString("PHONE"));
        custData.put("CONTACT", custPerData.getString("CONTACT"));
        custData.put("CONTACT_PHONE", custPerData.getString("CONTACT_PHONE"));

        IDataset userTeleSet = UserTelephoeInfoQry.getUserTelephoneByUserId(userId);
        if (userTeleSet.size() == 0)
        {
            CSAppException.apperr(BatException.CRM_BAT_84);
        }
        IData userTelData = userTeleSet.getData(0);
        custData.put("STAND_ADDRESS", userTelData.getString("STAND_ADDRESS"));
        custData.put("DETAIL_ADDRESS", userTelData.getString("DETAIL_ADDRESS"));
        custData.put("SIGN_PATH", userTelData.getString("SIGN_PATH"));
        custData.put("STAND_ADDRESS_CODE", userTelData.getString("STAND_ADDRESS_CODE"));
        custData.put("AREA_TYPE", userData.getString("RSRV_TAG1"));
        custData.put("CLEAR_MODE_TYPE", userData.getString("RSRV_TAG2"));

        IData submitData = new DataMap();
        submitData.put("MAIN_PRODUCT", productData);
        submitData.putAll(custData);
        submitData.putAll(mainUserData);
        submitData.put("TF_B_TRADE_PRODUCT", productData);
        submitData.put("TF_B_TRADE_SVC", svcData);
        IDataset returnDatas = new DatasetList();
        returnDatas.add(submitData);
        return returnDatas;
    }

    /**
     * 创建批次依赖
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IData createBatRelaiton(IData input) throws Exception
    {
        IData result = new DataMap();
        String batchId = input.getString("BATCH_ID");
        String relaBatchId = input.getString("RELA_BATCH_ID");
        String relationTypeCode = input.getString("RELATION_TYPE_CODE", "0");
        boolean flag = BatDealBean.createBatRealtion(batchId, relaBatchId, relationTypeCode);
        result.put("RESULT", String.valueOf(flag));
        return result;
    }

    /**
     * 创建批量任务task
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset createBatTask(IData data) throws Exception
    {
        String batTaskId = BatDealBean.createBatTask(data);

        IDataset dataset = new DatasetList();

        IData retData = new DataMap();

        retData.put("BATCH_TASK_ID", batTaskId);

        dataset.add(retData);

        return dataset;
    }

    public IDataset getAdvanceFees(IData input) throws Exception
    {
        BatDealBean bean = (BatDealBean) BeanManager.createBean(BatDealBean.class);
        return bean.getAdvanceFees(input);
    }

    /************************************* person end ****************************************************/

    //
    public IDataset getCommparaInfoEx(IData input) throws Exception
    {
        BatDealBean bean = (BatDealBean) BeanManager.createBean(BatDealBean.class);
        return bean.getCommparaInfoEx(input);
    }

    // 查询有效的优惠ID
    public IDataset getDiscntCode(IData input) throws Exception
    {
        BatDealBean bean = (BatDealBean) BeanManager.createBean(BatDealBean.class);
        return bean.getDiscntCode(input);
    }

    //
    public IDataset getElementInfo(IData input) throws Exception
    {
        BatDealBean bean = (BatDealBean) BeanManager.createBean(BatDealBean.class);
        return bean.getElementInfo(input);
    }

    public IDataset getIds(IData input) throws Exception
    {
        String userId = SeqMgr.getUserId();
        String custId = SeqMgr.getCustId();
        String acctId = SeqMgr.getAcctId();
        IData ids = new DataMap();
        ids.put("MAIN_USER_ID", userId);
        ids.put("MAIN_CUST_ID", custId);
        ids.put("MAIN_ACCT_ID", acctId);
        IDataset idss = new DatasetList();
        idss.add(ids);
        return idss;
    }

    //
    public IDataset getJoinCause(IData input) throws Exception
    {
        BatDealBean bean = (BatDealBean) BeanManager.createBean(BatDealBean.class);
        return bean.getJoinCause(input);
    }

    /**
     * 获取指定日期日、月导入数量
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset getNowDayCount(IData idata) throws Exception
    {
        IDataset returnDataSet = new DatasetList();

        String batch_oper_type = idata.getString("BATCH_OPER_TYPE");

        IData returnData = BatDealBean.getNowDayCount(batch_oper_type, null);

        returnDataSet.add(returnData);

        return returnDataSet;
    }

    public IDataset getOperTypeBySpAndBiz(IData input) throws Exception
    {
        BatDealBean bean = (BatDealBean) BeanManager.createBean(BatDealBean.class);
        return bean.getOperTypeBySpAndBiz(input);
    }

    //
    public IDataset getPackageInfo(IData input) throws Exception
    {
        BatDealBean bean = (BatDealBean) BeanManager.createBean(BatDealBean.class);
        return bean.getPackageInfo(input);
    }

    /************************************* common end ****************************************************/

    /************************************* group start ****************************************************/

    public IDataset getProductsType(IData data) throws Exception
    {
        String parentTypeCode = data.getString("PARENT_PTYPE_CODE");
        IDataset set = ProductTypeInfoQry.getProductsType(parentTypeCode, null);
        return set;
    }

    /**
     * 导入批量产品变更明细
     * 
     * @param idata
     * @return
     * @throws Exception
     */
    public IDataset importData(IData idata) throws Exception
    {
        BatDealBean bean = (BatDealBean) BeanManager.createBean(BatDealBean.class);

        IDataset dataset = idata.getDataset("DATA_SET");

        IData inParam = idata.getData("IN_PARAM");

        bean.importData(dataset, inParam);

        return new DatasetList();

    }
    
    // 本省企业服务代码信息导入后台提交服务
    public IDataset importSpInfoCSData(IData input) throws Exception
    {
        BatDealBean bean = (BatDealBean) BeanManager.createBean(BatDealBean.class);
        return bean.importSpInfoCSData(input);
    }

    // OCS导入号码处理后台提交服务
    public void importOcsData(IData input) throws Exception
    {
        BatDealBean bean = (BatDealBean) BeanManager.createBean(BatDealBean.class);
        bean.importOcsData(input);
    }

    // OCS导入不需要监控号码处理（白名单）
    public void importOcsNoNeedMonitorData(IData input) throws Exception
    {
        BatDealBean bean = (BatDealBean) BeanManager.createBean(BatDealBean.class);
        bean.importOcsNoNeedMonitorData(input);
    }

    // 从序列中取BATCH_ID
    public IDataset initBatchId(IData input) throws Exception
    {
        BatDealBean bean = (BatDealBean) BeanManager.createBean(BatDealBean.class);
        setUserEparchyCode(CSBizBean.getTradeEparchyCode());
        return bean.initBatchId(input);
    }

    /**
     * 根据主键查询批次信息
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset qryTradeBatByPK(IData data) throws Exception
    {
        IData batData = BatInfoQry.qryTradeBatByPK(data);

        IDataset dataset = new DatasetList();

        dataset.add(batData);

        return dataset;
    }

    public IDataset queryAllBanks(IData cond) throws Exception
    {
        BatDealBean bean = (BatDealBean) BeanManager.createBean(BatDealBean.class);
        return bean.queryAllBanks(cond, getPagination());
    }

    public IDataset queryAttrsByElement(IData cond) throws Exception
    {
        BatDealBean bean = (BatDealBean) BeanManager.createBean(BatDealBean.class);
        cond.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        return bean.queryAttrsByElement(cond, getPagination());
    }

    public IDataset queryBanks(IData cond) throws Exception
    {
        BatDealBean bean = (BatDealBean) BeanManager.createBean(BatDealBean.class);
        cond.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        return bean.queryBanks(cond, getPagination());
    }

    // 批量任务信息查询
    public IDataset queryBatchInfo(IData input) throws Exception
    {
        BatDealBean bean = (BatDealBean) BeanManager.createBean(BatDealBean.class);
        return bean.queryBatchInfo(input, getPagination());
    }

    //
    public IDataset queryBatchInfoByTaskId(IData input) throws Exception
    {
        BatDealBean bean = (BatDealBean) BeanManager.createBean(BatDealBean.class);
        return bean.queryBatchInfoByTaskId(input, getPagination());
    }

    /************************************* group end ****************************************************/

    //
    public IDataset queryBatchTaskList(IData input) throws Exception
    {
        BatDealBean bean = (BatDealBean) BeanManager.createBean(BatDealBean.class);
        return bean.queryBatchTaskList(input, getPagination());
    }

    // 根据可返销的业务类型
    public IDataset queryBatchType(IData input) throws Exception
    {
        BatDealBean bean = (BatDealBean) BeanManager.createBean(BatDealBean.class);
        return bean.queryBatchType(input, getPagination());
    }
    
    public IDataset queryTaskId(IData input) throws Exception
    {
        BatDealBean bean = (BatDealBean) BeanManager.createBean(BatDealBean.class);
        return bean.queryTaskId(input);
    }
    public void insertOrder(IData input) throws Exception
    {
        BatDealBean bean = (BatDealBean) BeanManager.createBean(BatDealBean.class);
         bean.insertOrder(input);
    }
    public IDataset queryGroupName(IData input) throws Exception
    {
        BatDealBean bean = (BatDealBean) BeanManager.createBean(BatDealBean.class);
        return bean.queryGroupName(input);
    }
    public IDataset queryGroupSn(IData input) throws Exception
    {
        BatDealBean bean = (BatDealBean) BeanManager.createBean(BatDealBean.class);
        return bean.queryGroupSn(input);
    }
    public IDataset queryTypeCode(IData input) throws Exception
    {
        BatDealBean bean = (BatDealBean) BeanManager.createBean(BatDealBean.class);
        return bean.queryTypeCode(input);
    }
    public IDataset queryTypeCodeByType(IData input) throws Exception
    {
        BatDealBean bean = (BatDealBean) BeanManager.createBean(BatDealBean.class);
        return bean.queryTypeCodeByType(input);
    }
    
    

    // 查询批量业务类型
    public IDataset queryBatchTypeByCode(IData input) throws Exception
    {
        BatDealBean bean = (BatDealBean) BeanManager.createBean(BatDealBean.class);
        return bean.queryBatchTypeByCode(input);
    }

    /**
     * 查询批量业务参数信息
     * 
     * @param idata
     * @return
     * @throws Exception
     */
    public IDataset queryBatchTypeParamsEx(IData idata) throws Exception
    {
        IData data = BatchTypeInfoQry.queryBatchTypeParamsEx(idata.getString("BATCH_OPER_CODE", ""), CSBizBean.getTradeEparchyCode());

        IDataset dataSet = new DatasetList();

        dataSet.add(data);

        return dataSet;
    }

    /**
     * 查询批量类型 TRADE_ATTR： 1表示个人业务，2表示集团业务
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset queryBatchTypes(IData input) throws Exception
    {
        return BatchTypeInfoQry.queryBatchTypes(input);
    }

    //
    public IDataset queryBatDeal(IData input) throws Exception
    {
        BatDealBean bean = (BatDealBean) BeanManager.createBean(BatDealBean.class);
        return bean.queryBatDeal(input, this.getPagination());
    }

    //
    public IDataset queryBatDealByBatchId(IData input) throws Exception
    {
        BatDealBean bean = (BatDealBean) BeanManager.createBean(BatDealBean.class);
        return bean.queryBatDealByBatchId(input, this.getPagination());
    }

    /************************************* person start ****************************************************/
    // 批量任务结果信息查询
    public IDataset queryBatDealBySN(IData input) throws Exception
    {
        BatDealBean bean = (BatDealBean) BeanManager.createBean(BatDealBean.class);
        return bean.queryBatDealBySN(input, getPagination());
    }

    /**
     * 查询批量任务信息以及该批量日导入数目
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset queryBatTask(IData data) throws Exception
    {
        IDataset dataset = new DatasetList();

        IData retData = BatDealBean.queryBatTask(data);

        dataset.add(retData);

        return dataset;
    }

    // 查询批量待审核任务
    public IDataset queryBatTaskAudit(IData input) throws Exception
    {
        BatDealBean bean = (BatDealBean) BeanManager.createBean(BatDealBean.class);
        return bean.queryNeedApproveBatTrades(input, getPagination());
    }

    // 查询批量信息批次信息
    public IDataset queryBatTaskBatchInfo(IData input) throws Exception
    {
        BatDealBean bean = (BatDealBean) BeanManager.createBean(BatDealBean.class);
        return bean.queryBatTaskBatchInfo(input);
    }

    // 根据批量任务编号查询批量任务信息
    public IDataset queryBatTaskByPK(IData input) throws Exception
    {
        BatDealBean bean = (BatDealBean) BeanManager.createBean(BatDealBean.class);
        return bean.queryBatTaskByPK(input.getString("BATCH_TASK_ID"));
    }

    // 查询批量待删除任务
    public IDataset queryBatTaskDelete(IData input) throws Exception
    {
        BatDealBean bean = (BatDealBean) BeanManager.createBean(BatDealBean.class);
        return bean.queryNeedDeleteBatTradeC(input, getPagination());
    }

    // 查询批量待启动任务
    public IDataset queryBatTaskStart(IData input) throws Exception
    {
        BatDealBean bean = (BatDealBean) BeanManager.createBean(BatDealBean.class);
        return bean.queryBatTaskStart(input, getPagination());
    }

    /**
     * 查询批量任务列表
     * 
     * @param data
     * @param pagination
     * @return
     * @throws Exception
     */
    public IDataset queryBatTrades(IData data) throws Exception
    {
        data.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        data.put("STAFF_ID", data.getString("CREATE_STAFF_ID"));

        String operType = data.getString("operType");

        if ("2".equals(operType))
        {
            data.put("REMOVE_TAG", "1");
            data.put("ACTIVE_FLAG", "0");
        }
        else
        {
            data.put("REMOVE_TAG", "0");
            data.put("ACTIVE_FLAG", operType);
        }

        return BatInfoQry.qryBatNeedToStart(data, this.getPagination());
    }

    // 根据批量类型编码查询批量信息
    public IDataset queryBatTypeByPK(IData idata) throws Exception
    {
        IDataset dataSet = BatchTypeInfoQry.qryBatchTypeByOperType(idata.getString("BATCH_OPER_TYPE"));

        return dataSet;
    }

    // 根据批次信息查询待返销订单
    public IDataset queryCancelBatByBatchInfo(IData input) throws Exception
    {
        BatDealBean bean = (BatDealBean) BeanManager.createBean(BatDealBean.class);
        return bean.queryCancelBatByBatchInfo(input, getPagination());
    }
    
    // 根据批次信息查询待返销订单,增加对实名制激活的校验
    public IDataset queryCancelBatByBatchInfos(IData input) throws Exception
    {
    	BatDealBean bean = (BatDealBean) BeanManager.createBean(BatDealBean.class);
    	return bean.queryCancelBatByBatchInfos(input, getPagination());
    }

    // 根据服务号码信息查询待返销订单
    public IDataset queryCancelBatBySerialInfo(IData input) throws Exception
    {
        BatDealBean bean = (BatDealBean) BeanManager.createBean(BatDealBean.class);
        return bean.queryCancelBatBySerialInfo(input, getPagination());
    }
    
    // 根据服务号码信息查询待返销订单，添加对实名制激活的校验
    public IDataset queryCancelBatBySerialInfos(IData input) throws Exception
    {
    	BatDealBean bean = (BatDealBean) BeanManager.createBean(BatDealBean.class);
    	return bean.queryCancelBatBySerialInfos(input, getPagination());
    }

    // 根据查询返销标识
    public IDataset queryCancelTag(IData input) throws Exception
    {
        BatDealBean bean = (BatDealBean) BeanManager.createBean(BatDealBean.class);
        return bean.queryCancelTag(input, getPagination());
    }

    //
    public IDataset queryCommpara(IData input) throws Exception
    {
        BatDealBean bean = (BatDealBean) BeanManager.createBean(BatDealBean.class);
        return bean.queryCommpara(input);
    }

    // 批量优惠变更 优惠查询
    public IDataset queryDiscntInfo(IData input) throws Exception
    {
        BatDealBean bean = (BatDealBean) BeanManager.createBean(BatDealBean.class);
        return bean.queryDiscntInfo(input, getPagination());
    }
    
    // 批量优惠变更 优惠查询
    public IDataset queryDiscntSpecInfo(IData input) throws Exception
    {
    	BatDealBean bean = (BatDealBean) BeanManager.createBean(BatDealBean.class);
    	return bean.queryDiscntSpecInfo(input, getPagination());
    }

    public IDataset queryDiscnts(IData input) throws Exception
    {
        BatDealBean bean = (BatDealBean) BeanManager.createBean(BatDealBean.class);
        return bean.queryDiscnts(input, getPagination());
    }

    public IDataset queryDiscntsByDtype(IData data) throws Exception
    {
        String discntType = data.getString("DISCNT_TYPE_CODE");
        IDataset set = DiscntInfoQry.queryDiscntsByDtype(discntType);
        return set;
    }

    public IDataset queryFaildInfo(IData input) throws Exception
    {
        BatDealBean bean = (BatDealBean) BeanManager.createBean(BatDealBean.class);
        return bean.queryFaildInfo(input);
    }

    //
    public IDataset queryG3NetCardType(IData input) throws Exception
    {
        BatDealBean bean = (BatDealBean) BeanManager.createBean(BatDealBean.class);
        return bean.queryG3NetCardType(input);
    }

    //
    public IDataset queryGiftSalePackageByPRoductId(IData input) throws Exception
    {
        String product_id = input.getString("PRODUCT_ID");
        return PkgInfoQry.queryGiftSalePackageByPRoductId(product_id, getVisit().getStaffId());
    }

    // OCS导入处理结果查询
    public IDataset queryOcsDealInfo(IData input) throws Exception
    {
        BatDealBean bean = (BatDealBean) BeanManager.createBean(BatDealBean.class);
        return bean.queryOcsDealInfo(input, getPagination());
    }

    //
    public IDataset queryPackages(IData input) throws Exception
    {
        return CommparaInfoQry.getOnlyByAttr("CSM", "954", CSBizBean.getTradeEparchyCode());
    }
    
    public IDataset queryPackagesend(IData input) throws Exception
    {
        return CommparaInfoQry.getOnlyByAttr("CSM", "955", CSBizBean.getTradeEparchyCode());
    }

    //
    public IDataset queryPhoneByProduct(IData input) throws Exception
    {
        BatDealBean bean = (BatDealBean) BeanManager.createBean(BatDealBean.class);
        return bean.queryPhoneByProduct(input);
    }

    // 平台业务代码查询
    public IDataset queryPlatInfo(IData input) throws Exception
    {
        BatDealBean bean = (BatDealBean) BeanManager.createBean(BatDealBean.class);
        return bean.queryPlatInfo(input, getPagination());
    }

    // 根据号码查询批量任务信息
    public IDataset queryPopuTaskInfoBySn(IData input) throws Exception
    {
        BatDealBean bean = (BatDealBean) BeanManager.createBean(BatDealBean.class);
        return bean.queryPopuTaskInfoBySn(input, getPagination());
    }

    /************************************* common start ****************************************************/
    // 根据TAKSID查询批量任务信息
    public IDataset queryPopuTaskInfoByTaskId(IData input) throws Exception
    {
        BatDealBean bean = (BatDealBean) BeanManager.createBean(BatDealBean.class);
        return bean.queryPopuTaskInfoByTaskId(input, getPagination());
    }

    //
    public IDataset queryProductListNoLimit(IData input) throws Exception
    {
        BatDealBean bean = (BatDealBean) BeanManager.createBean(BatDealBean.class);
        return bean.queryProductListNoLimit(input);
    }

    //
    public IDataset queryResId(IData input) throws Exception
    {
        BatDealBean bean = (BatDealBean) BeanManager.createBean(BatDealBean.class);
        return bean.queryResId(input);
    }

    // 批量服务变更 - 服务编码、名称查询
    public IDataset querySerivceInfo(IData input) throws Exception
    {
        BatDealBean bean = (BatDealBean) BeanManager.createBean(BatDealBean.class);
        return bean.querySerivceInfo(input, getPagination());
    }
    
    // 批量服务变更 - 服务编码、名称查询
    public IDataset querySerivceSpecInfo(IData input) throws Exception
    {
        BatDealBean bean = (BatDealBean) BeanManager.createBean(BatDealBean.class);
        return bean.querySerivceSpecInfo(input, getPagination());
    }

    // 平台服务查询
    public IDataset queryServiceInfoForPlat(IData input) throws Exception
    {
        BatDealBean bean = (BatDealBean) BeanManager.createBean(BatDealBean.class);
        return bean.queryServiceInfoForPlat(input, getPagination());
    }

    public IDataset queryServices(IData input) throws Exception
    {
        BatDealBean bean = (BatDealBean) BeanManager.createBean(BatDealBean.class);
        return bean.queryServices(input, getPagination());
    }

    public IDataset queryUsersByBank(IData cond) throws Exception
    {
        BatDealBean bean = (BatDealBean) BeanManager.createBean(BatDealBean.class);
        return bean.queryUsersByBank2(cond, getPagination());
    }

    //
    public boolean setOcsBusi(IData input) throws Exception
    {
        BatDealBean bean = (BatDealBean) BeanManager.createBean(BatDealBean.class);
        return bean.setOcsBusi(input);
    }

    /**
     * 提供给单页面批量调用,批量参数放到IN_PARAM中，批量明细放到BAT_DEAL_LIST中
     * 
     * @param iData
     * @return
     * @throws Exception
     */
    public IDataset singlePagecreateBat(IData iData) throws Exception
    {
        IDataset resultList = new DatasetList();
        if (IDataUtil.isEmpty(iData))
        {
            return resultList;
        }

        IData inParam = iData.getData("IN_PARAM", new DataMap());

        IDataset batDealList = iData.getDataset("BAT_DEAL_LIST");

        String batch_id = BatDealBean.createBat(inParam, batDealList);

        IData resultData = new DataMap();

        resultData.put("BATCH_ID", batch_id);

        resultList.add(resultData);

        return resultList;
    }

    // 批量任务审核
    public boolean subAudit(IData input) throws Exception
    {
        BatDealBean bean = (BatDealBean) BeanManager.createBean(BatDealBean.class);
        return bean.subAudit(input);
    }

    // 批量任务创建
    public IDataset submitBatTask(IData input) throws Exception
    {
        BatDealBean bean = (BatDealBean) BeanManager.createBean(BatDealBean.class);
        setUserEparchyCode(CSBizBean.getTradeEparchyCode());
        return bean.submitBatTask(input);
    }

    public IDataset taskDetialQuery(IData cond) throws Exception
    {
        BatDealBean bean = (BatDealBean) BeanManager.createBean(BatDealBean.class);
        cond.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        return bean.qryTaskDetial(cond, getPagination());
    }

    /**
     * 错单重跑
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset updateBatDealStartToRun(IData data) throws Exception
    {
        BatDealBean.updateBatDealStartToRun(data);

        return new DatasetList();
    }

    /**
     * 查询批量任务处理结果，流量自由充产品，网厅使用
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset queryBatDealResult(IData data) throws Exception
    {
        if("".equals(data.getString("BATCH_ID","")))
        {
            CSAppException.apperr(BatException.CRM_BAT_66);
        }
        return BatDealInfoQry.queryBatDealResult(data);
    }
    
    /**
     * 查询批量任务处理结果(根据手机号码)，流量自由充产品，网厅使用
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset qryBatDealResultForGfffMem(IData data) throws Exception
    {
        if("".equals(data.getString("SERIAL_NUMBER","")))
        {
            CSAppException.apperr(BatException.CRM_BAT_97);
        }
        return BatDealInfoQry.qryBatDealResultForGfffMem(data,this.getPagination());
    }
    
    
    /**
     * 查询批量任务处理结果(根据集团产品编码、批量流水号、查询号码)
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset queryFluxBatDealResult(IData data) throws Exception
    {
    	String grp_serial_number =  data.getString("GRP_SERIAL_NUMBER");
    	String batch_id =  data.getString("BATCH_ID","");
    	String serial_number =  data.getString("SERIAL_NUMBER","");
    	if (StringUtils.isBlank(grp_serial_number))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "查询集团产品编码不可为空！");
        }
    	
    	if("".equals(serial_number) && "".equals(batch_id))
    	{
    		CSAppException.apperr(CrmCommException.CRM_COMM_103, "批量流水号、查询号码不可都为空！");
    	}
    	
    	IDataset ds = BatDealInfoQry.queryFluxBatDealResult(data,this.getPagination());
        if(IDataUtil.isEmpty(ds))
        {
         // 返回参数设置
            IData result = new DataMap();
            result.put("X_RESULTCODE", "0"); // 正常状态
            result.put("X_RESULTINFO", "查询批量任务处理结果无数据！"); // 正常状态描述
            return IDataUtil.idToIds(result);
        }else
        {
            return ds; 
        }
    }
    
    /**
     * REQ201801150022_新增IMS号码开户人像比对功能
     * <br/>
     * 获取电子工单内容
     * @param data
     * @return
     * @throws Exception
     */
    public IData qryBatTaskByBatchTaskId(IData data) throws Exception
    {
		String batchTaskId = data.getString("BATCH_TASK_ID", "");
		
		
		
		IDataset tradeBat=BatInfoQry.qryBatByBatchTaskId(batchTaskId);
		//批次号
		String batchId="";
		if(IDataUtil.isNotEmpty(tradeBat)){
			batchId=tradeBat.getData(0).getString("BATCH_ID","");
		}
		
		//获取提交的参数
		IData batchTaskInfo = BatTaskInfoQry.qryBatTaskByBatchTaskId(batchTaskId);
        if (IDataUtil.isEmpty(batchTaskInfo))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "该批量任务不存在");
        }
		JSONArray array_rx = new JSONArray();
        StringBuffer sb = new StringBuffer();
        sb.append(batchTaskInfo.getString("CODING_STR1", "")).append(batchTaskInfo.getString("CODING_STR2", "")).
        	append(batchTaskInfo.getString("CODING_STR3", "")).append(batchTaskInfo.getString("CODING_STR4", "")).
        		append(batchTaskInfo.getString("CODING_STR5", ""));
        array_rx.element(sb.toString());
//		array_rx.element(batchTaskInfo.getString("CODING_STR1", ""));
		DatasetList ds_rx = new DatasetList(array_rx.toString());
		
		IData result=new DataMap();
		result.put("BATCH_OPER_CODE",batchTaskInfo.getString("BATCH_OPER_CODE", ""));
		//IMS批量开户
		if(("BATOPENGROUPMEM").equals(batchTaskInfo.getString("BATCH_OPER_CODE", ""))){
			result.put("PIC_ID", "");
			result.put("ID_CARD", "");
			result.put("CUST_NAME", "");
			//批次号
			result.put("BATCH_ID", batchId);
			//手机号码
			result.put("SERIAL_NUMBER", "");
			result.put("PRODUCT_NAME", "IMS 语音");
			if (DataSetUtils.isNotBlank(ds_rx)) {
				    //客户摄像标识
				    String custInfoPicId=ds_rx.getData(0).getString("custInfo_PIC_ID","");
					//经办人摄像标识
					String agentPicId=ds_rx.getData(0).getString("custInfo_AGENT_PIC_ID","");
	            	//0-已采集，1-未采集
	            	if(!"".equals(custInfoPicId)&&custInfoPicId != null){
	            		//客户已经摄像
	            		result.put("PIC_ID", "0");
	            	}else{
	                	if(!"".equals(agentPicId)&&agentPicId != null){
	                		//经办人已经摄像
	                		result.put("PIC_ID", "0");
	                	}else{
	                		//未摄像
	                		result.put("PIC_ID", "1");
	                	}
	            	}
	               IData memCustInfo=ds_rx.getData(0).getData("MEM_CUST_INFO");
	               if(IDataUtil.isNotEmpty(memCustInfo)){
	            	    //证件号码
	            	    String psptId=memCustInfo.getString("PSPT_ID","");
	            	    result.put("ID_CARD", psptId);
	            	    
	            	    //客户名称
	            	    String custName=memCustInfo.getString("CUST_NAME","");
	            	    result.put("CUST_NAME", custName);
	            	    
	            	    //手机号码
	            	    String SERIAL_NUMBER=memCustInfo.getString("CONTACT_PHONE","");
	            	    result.put("SERIAL_NUMBER", SERIAL_NUMBER);
	               }
			}
		}
		
        return result;
    }
    
	/**
	 * 判断批量是否已经打印
	 * @param data
	 * @return
	 * @throws Exception
	 * @author  zhuoyingzhi
	 * @date 20180724
	 */    
    public IData checkBatTask(IData input) throws Exception
    {
        BatDealBean bean = (BatDealBean) BeanManager.createBean(BatDealBean.class);
        return bean.checkBatTask(input);
    }    
    
    /*
     * REQ201807240010++新增批量开户界面人像比对、受理单打印优化需求 by mqx 20190117
     * 记录信息到tf_b_trade_cnote_info表
     */
    public void insertIntoTradeCnoteInfoBat(IData input) throws Exception
    {
        BatDealBean bean = (BatDealBean) BeanManager.createBean(BatDealBean.class);
        bean.insertIntoTradeCnoteInfoBat(input);
    }   
}
