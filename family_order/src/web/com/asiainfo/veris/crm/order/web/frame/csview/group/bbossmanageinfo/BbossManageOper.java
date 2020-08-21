
package com.asiainfo.veris.crm.order.web.frame.csview.group.bbossmanageinfo;

import java.util.Iterator;

import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.BizEnv;
import com.ailk.cache.memcache.util.SharedCache;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.cache.CacheKey;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.bbossattrgroup.GroupBBossUtilCommon;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.attrbiz.AttrBizInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.seqinfo.SeqMgrInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.util.GroupEsopUtilView;

public abstract class BbossManageOper extends GroupBBossUtilCommon
{

    public final static String BIZ_CHANGE_FLAG = "2";

    public final static String BIZ_DEAL_FLAG = "1";

    // 操作所处的阶段 0表示预受理 1 业务受理 2变更 3注销 modify by zhouwei
    public final static String BIZ_PRE_DEAL_FLAG = "0";

    public final static String BIZ_REMOVE_FLAG = "3";

    public final static String PARA_CODE_STAGE_O = "PARA_CODE14"; // 主办省阶段位标识

    public final static String PARA_CODE_STAGE_S = "PARA_CODE22"; // 配合省阶段位标识

    public static String flowPoint;

    private static String productOperType = "";// 操作阶段 // 10表示预受理 1业务受理 9变更 2注销

    public static String ibsysid = "";

    public static String subibsysid = "";

    public static String subsubscribe_id = "";

    public static String nodeid = "";

    public String userId = "";// esop使用

    public static String workid = "";

    public static String bpmtempleid = "";

    public static String maintempleid = "";

    /**
     * 将BOSS反馈参数从新组合 chenyi
     */

    public IDataset fileManagePlus(IRequestCycle cycle, IDataset managePlus, IData data) throws Exception
    {

        // 取省BBOSS的反馈管理信息

        IData inparams = new DataMap();
        inparams.put("TRADE_ID", data.getString("TRADE_ID"));
        inparams.put("USER_ID", data.getString("BBOSS_USER_ID"));
        inparams.put("RSRV_VALUE_CODE", "BBOSS_MANAGE_" + data.getString("FLOWINFO").substring(6));

        IDataset manage = CSViewCall.call(this, "CS.TradeOtherInfoQrySVC.queryBbossManageInfobyTradeIdUserId", inparams);

        if (manage.isEmpty())
        {
            for (int i = 0; i < managePlus.size(); i++)
            {
                IData plus = managePlus.getData(i);
                // 设置默认时间
                if ("审批时间".equals(plus.getString("ATTR_NAME")))
                {
                    plus.put("ATTR_VALUE", SysDateMgr.getSysTime());
                }
                // 设置默认审批意见
                if ("审批意见".equals(plus.getString("ATTR_NAME")))
                {
                    plus.put("ATTR_VALUE", "同意");
                }
            }
        }
        else
        {
            for (int j = 0; j < manage.size(); j++)
            {
                IData temp = manage.getData(j);
                for (int i = 0; i < managePlus.size(); i++)
                {
                    IData tempPlus = managePlus.getData(i);

                    if (temp.getString("RSRV_STR14", "").length() > 0 && temp.getString("RSRV_STR12").equals(tempPlus.getString("ATTR_CODE", "").substring(4)))
                    {

                        tempPlus.put("ATTR_VALUE", temp.getString("RSRV_STR14", ""));

                    }
                }
            }
        }
        dealBBossAttr(flowPoint.substring(6), managePlus);

        return managePlus;
    }

    public abstract IData getInfo();

    private String getTradeId() throws Exception
    {

        String tradeId = getData().getString("TRADE_ID");

        return tradeId;
    }

    /**
     * 通过trade_id查出产品受理时添加的参数
     * 
     * @author jch
     * @version 创建时间：2009-7-8 下午10:07:22
     */
    public IData getUserAttrByTradeIdInstIdUserId(String code, String userId, String bussinessFlag) throws Exception
    {

        IData params = new DataMap();
        String tradeId = getTradeId();
        params.put("TRADE_ID", tradeId);
        params.put("INST_TYPE", "P");
        params.put("ATTR_CODE", code);
        params.put("USER_ID", userId);

        if (BIZ_REMOVE_FLAG.equals(bussinessFlag))// 注销
        {

            IDataset dataset = CSViewCall.call(this, "CS.TradeAttrInfoQrySVC.getUserProductAttrValuebyTradeIdAndUserId_ENDDATA", params);
            if (dataset != null && dataset.size() != 0)
                return dataset.getData(0);

        }
        else
        {

            IDataset dataset = CSViewCall.call(this, "CS.TradeAttrInfoQrySVC.getUserProductAttrValuebyTradeIdAndUserId", params);
            if (dataset != null && dataset.size() != 0)
                return dataset.getData(0);
        }
        return null;
    }

    /**
     * 查出产品受理时添加的参数
     * 
     * @author shixb
     * @version 创建时间：2009-7-8 下午10:07:22
     */
    public IData getUserAttrByUserIdInstId(String code) throws Exception
    {

        IData params = new DataMap();
        String userId = getData().getString("GRP_USER_ID");
        params.put("USER_ID", userId);
        params.put("INST_TYPE", "P");
        params.put("ATTR_CODE", code);
        IDataset dataset = CSViewCall.call(this, "CS.UserAttrInfoQrySVC.getUserProductAttrValue", params);
        if (dataset != null && dataset.size() != 0)
            return dataset.getData(0);
        return null;
    }

    /**
     * chenyi 13-10-28 查询资料表属性值
     * 
     * @return
     * @throws Exception
     */
    protected IDataset getUserAttrInfo(String code, String user_id) throws Exception
    {

        IData param = new DataMap();
        param.put("ATTR_CODE", code);
        param.put("USER_ID", user_id);
        IDataset result = CSViewCall.call(this, "CS.UserAttrInfoQrySVC.getUserAttrByUserId", param);
        return result;

    }

    public void init(IRequestCycle cycle) throws Exception
    {
        IData data = getData();

        productId = data.getString("PRODUCT_ID");
        String trade_id = data.getString("TRADE_ID");
        String user_id = data.getString("BBOSS_USER_ID");// BBOSS子产品用户ID
        flowPoint = data.getString("FLOWINFO");
        String myFlag = data.getString("MYFLAG", "");
        String orderId = data.getString("ORDER_ID", "");

        subibsysid = data.getString("subibsysid", "");
        subsubscribe_id = data.getString("subsubscribe_id", "");
        ibsysid = data.getString("ibsysid", "");
        workid = data.getString("workid", "");
        nodeid = data.getString("nodeid", "");
        bpmtempleid = data.getString("bpmtempleid", "");
        maintempleid = data.getString("maintempleid", "");
        setIbsysid(ibsysid);

        IData paramData = new DataMap();
        paramData.put("ORDER_ID", orderId);
        IDataset merchInfos = CSViewCall.call(this, "CS.TradeGrpMerchInfoQrySVC.qryMerchInfoByMainTradeOrderId", paramData);
        if (IDataUtil.isNotEmpty(merchInfos))
        {
            String merch_tradeId = merchInfos.getData(0).getString("TRADE_ID");
            setMerchTradeId(merch_tradeId);
        }

        // 操作阶段
        IData param = new DataMap();
        param.put("ATTR_CODE", flowPoint.substring(6));
        IDataset outDataset = CSViewCall.call(this, "CS.BBossAttrQrySVC.qryBBossAttrByAttrCode", param);//
        int operType = 0;
        if (IDataUtil.isNotEmpty(outDataset))
        {
            String operflag = outDataset.getData(0).getString("RSRV_STR2"); // 0 预受理 1受理 // 2 专线业务网络资源变更 3 专线业务资费变更 变更类
            // 4注销
            operType = Integer.parseInt(operflag);
        }

        if (operType == 2 || operType == 3)
        {
            productOperType = "9";
        }
        else if (operType == 0)
        {

            productOperType = "10";
        }
        else if (operType == 1)
        {

            productOperType = "1";

        }
        else if (operType == 4)
        {
            productOperType = "2";
        }

        setFlowPoint(flowPoint);
        setTradeId(trade_id);
        setBbossUserId(user_id);
        setMyFlag(myFlag);
        setOrderId(orderId);

        setGrpUserEparchycode(getData().getString("GRP_USER_EPARCHYCODE", ""));

        // 产品属性

        queryPOProductPlus(cycle);

        queryFlowInfo(cycle);
        queryManageInfo(cycle);
        queryElements(cycle);

    }

    /*
     * @description 查询并添加ESOP录入的属性值
     * @author xunyl
     * @date 2013-09-22
     */
    protected void qryEsopParams(IDataset filterDs) throws Exception
    {
        // 1- 获取ESOP侧录入的参数
        IData inparams = new DataMap();
        inparams.put("OPER_CODE", "11");
        String rowIndex = getData().getString("rowIndex");
        if (rowIndex != null && !rowIndex.equals(""))
        {
            inparams.put("index", Integer.parseInt(rowIndex) - 1);
        }
        IData esopParams = GroupEsopUtilView.getEsopParams(this, inparams, true);

        // 2- CRM侧产品参数赋值（服务与资费需要重新录入）
        if (esopParams != null && !esopParams.isEmpty() && filterDs != null && filterDs.size() > 0)
        {
            String paramCode = "";
            for (int i = 0; i < filterDs.size(); i++)
            {
                IData productAttrInfo = filterDs.getData(i);
                paramCode = productAttrInfo.getString("ATTR_CODE", "");
                String attrGroup = productAttrInfo.getString("GROUPATTR", "");
                Iterator iterator = esopParams.keySet().iterator();
                String tempKey = "";
                while (iterator.hasNext())
                {
                    tempKey = (String) iterator.next();
                    if (!"".equals(paramCode) && tempKey.contains(paramCode))
                    {
                        String esopParamValue = esopParams.getString(tempKey, "");
                        if (null == attrGroup || ("").equals(attrGroup))
                        {// 非属性组直接赋值
                            productAttrInfo.put("ATTR_VALUE", esopParamValue);
                            productAttrInfo.put("PARA_CODE14", "1");// 锁定, （应端到端要求不锁定，因为他们的字段很多没有校验）
                        }
                        else
                        {// 属性组属性，需要拆分工单
                            filterDs.remove(i);
                            String[] paramValueList = esopParamValue.split(";");
                            for (int j = 0; j < paramValueList.length; j++)
                            {
                                IData cgroupParamInfo = new DataMap();
                                cgroupParamInfo.putAll(productAttrInfo);
                                cgroupParamInfo.put("ATTR_VALUE", paramValueList[i]);
                                cgroupParamInfo.put("ATTR_GROUP", j + 1);
                                cgroupParamInfo.put("PARA_CODE14", "1");// 锁定,（应端到端要求不锁定，因为他们的字段很多没有校验）
                                filterDs.add(i + j, cgroupParamInfo);
                            }
                            i = i + (paramValueList.length - 1);
                        }
                        esopParams.remove(paramCode);
                    }
                }
            }
        }
    }

    public void queryElements(IRequestCycle cycle) throws Exception
    {
        IData param = getData();
        String orderId = param.getString("ORDER_ID", "");
        String tradeID = param.getString("TRADE_ID", "");
        IData paramData = new DataMap();
        paramData.put("ORDER_ID", orderId);
        IDataset merchInfos = CSViewCall.call(this, "CS.TradeGrpMerchInfoQrySVC.qryMerchInfoByMainTradeOrderId", paramData);
        IData merchInfo = new DataMap();
        if (IDataUtil.isNotEmpty(merchInfos))
        {
            merchInfo = merchInfos.getData(0);
        }

        String user_id = param.getString("BBOSS_USER_ID");
        IData data = new DataMap();
        data.put("USER_ID", user_id);
        data.put("PRODUCT_ID", productId);
        data.put("GROUP_ID", merchInfo.getString("GROUP_ID"));
        data.put("EPARCHY_CODE", getData().getString("GRP_USER_EPARCHYCODE", ""));
        data.put("TRADE_ID", tradeID);
        data.put(Route.USER_EPARCHY_CODE, getData().getString("GRP_USER_EPARCHYCODE", ""));
        IData productCtrlInfo = AttrBizInfoIntfViewUtil.qryChgUsProductCtrlInfoByProductId(this, productId);

        data.put("TRADE_TYPE_CODE", productCtrlInfo.getData("TradeTypeCode").getString("ATTR_VALUE"));
        setCond(data);
    }

    /**
     * 取能操作的流程信息 jch
     */
    public void queryFlowInfo(IRequestCycle cycle) throws Exception
    {

        IData data = getData();

        String flowInfo = data.getString("FLOWINFO").substring(6);

        IData inparams = new DataMap();
        inparams.put("PRODUCTSPECNUMBER", flowInfo);
        inparams.put("BIZ_TYPE", "10");// 管理节点信息
        IDataset managePlus = CSViewCall.call(this, "CS.BBossAttrQrySVC.qryBBossAttrByPospecBiztype", inparams); // boss反馈的参数

        //类型为文件或者一点支付业务的成员附件、号码确认反馈明细附件等属性需要支持附件下载功能
        setFileIdforParam(managePlus);
        
        setBossManageInfo(fileManagePlus(cycle, managePlus, data));

    }
    
    /**
     * @description 添加附件属性的FILEID(附件总共有三类，一类入成员附件，该属性不带文件后缀，二类为前台导入文件，该文件以fileId+后缀命名，三类
     * 为后台落地文件，该文件以文件名+后缀命名)
     * @author xunyl
     * @date 2016-01-17
     */
    private void setFileIdforParam(IDataset productPlusInfoList)throws Exception{
        for(int i=0;i<productPlusInfoList.size();i++){
            IData productPlusInfo = productPlusInfoList.getData(i);
            String eidtType = productPlusInfo.getString("EDIT_TYPE");
            String attrCode = productPlusInfo.getString("ATTR_CODE");
            if(StringUtils.equals("999033717",attrCode) || StringUtils.equals("999033734",attrCode) || 
                    StringUtils.equals("999033735",attrCode) || StringUtils.equals("UPLOAD",eidtType)){
                String attrValue = productPlusInfo.getString("ATTR_VALUE");
                if(StringUtils.isBlank(attrValue)){
                    continue;
                }
                //一点支付的成员附件由IBOSS处理后带上了后缀.xls,因此这里要带上附件查询
                if(StringUtils.equals("999033717",attrCode) || StringUtils.equals("999033734",attrCode) || 
                        StringUtils.equals("999033735",attrCode)){
                    attrValue = attrValue+".xls";
                }
                //根据文件名称查找对应的fileId
                IData inParam = new DataMap();
                inParam.put("FILE_NAME",attrValue);
                IDataset fileInfoList = CSViewCall.call(this, "CS.MFileInfoQrySVC.qryFileInfoListByFileName", inParam);
                if(IDataUtil.isNotEmpty(fileInfoList)){
                    String fileId = fileInfoList.getData(0).getString("FILE_ID");
                    String realName = fileInfoList.getData(0).getString("FILE_NAME");
                    productPlusInfo.put("FILE_ID", fileId);
                    productPlusInfo.put("REAL_NAME", realName);
                    continue;
                }
                                                
                //前台主动上传的附件，附件名称已然被修改成fileId+后缀的形式（可以肯定后缀前的部分为int型）
                String[] fileNameList = attrValue.split("\\.");
                boolean isNum = fileNameList[0].matches("[0-9]+");
                if(isNum==false){
                    continue;
                }
                inParam.clear();
                inParam.put("FILE_ID", fileNameList[0]);
                fileInfoList = CSViewCall.call(this, "CS.MFileInfoQrySVC.qryFileInfoListByFileID", inParam);
                if(IDataUtil.isNotEmpty(fileInfoList)){
                    String fileId = fileInfoList.getData(0).getString("FILE_ID");
                    String realName = fileInfoList.getData(0).getString("FILE_NAME");
                    productPlusInfo.put("FILE_ID", fileId);
                    productPlusInfo.put("REAL_NAME", realName);
                    continue;
                }
            }           
        }
    }

    /**
     * 取管理流程信息
     */

    public void queryManageInfo(IRequestCycle cycle) throws Exception
    {

        IData data = getData();
        String tradeId = data.getString("TRADE_ID");
        String userId = data.getString("BBOSS_USER_ID");
        // String flowPoint = data.getString("FLOWINFO");
        String flowpointCode = flowPoint.substring(6);

        IData inparams = new DataMap();
        inparams.put("TRADE_ID", tradeId);
        inparams.put("USER_ID", userId);
        inparams.put("RSRV_VALUE_CODE", flowPoint);
        inparams.put("MODIFY_TAG", "0");

        IDataset manage = CSViewCall.call(this, "CS.TradeOtherInfoQrySVC.queryBbossManageInfobyTradeIdUserId", inparams);

        IDataset copyManage = new DatasetList();
        if (manage.size() > 0)
        {

            // 将数据转换存的方式

            for (int i = 1; i <= 20; i++)
            {
                IData temp = new DataMap();

                String num = manage.getData(0).getString("RSRV_NUM" + i);
                if (null != num && !"".equals(num))
                {
                    IData param = new DataMap();
                    param.put("ATTR_CODE", flowpointCode + num);
                    IDataset attrInfo = CSViewCall.call(this, "CS.BBossAttrQrySVC.qryBBossAttrByAttrCode", param);
                    String attName = "";
                    if (IDataUtil.isNotEmpty(attrInfo))
                    {
                        attName = attrInfo.getData(0).getString("ATTR_NAME");
                    }

                    temp.put("ATTR_NAME", attName);
                    temp.put("RSRV_NUM", num);

                    int m = 22 + i;
                    temp.put("RSRV_STR", manage.getData(0).getString("RSRV_STR" + m));

                    copyManage.add(temp);
                }
                else
                    break;

            }
        }
        setManageInfo(copyManage);

    }

    /**
     * @param
     * @desciption 产品的参数信息
     * @author fanti
     * @version 创建时间：2014年8月29日 下午11:09:01
     */
    public void queryPOProductPlus(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        String tradeId = data.getString("TRADE_ID");
        String user_id = data.getString("BBOSS_USER_ID");

        IData productParam = new DataMap();
        productParam.put("PRODUCT_ID", productId); // 产品ID
        productParam.put("PRODUCT_OPER_TYPE", flowPoint.substring(6)); // 产品操作类型
        productParam.put("PRODUCT_USER_ID", user_id); // 产品用户ID
        productParam.put("BBOSS_STAGE", "5"); // 阶段编码（1-缓存，2-集团受理，3-集团变更，4-预受理转正式受理，5-管理节点）
        productParam.put("TRADE_ID", tradeId); // 产品用户TRADE_ID

        IDataset filterDs = getProductPlusInfo(productParam, new DataMap());

        qryEsopParams(filterDs);

        setPOProductPlus(filterDs);
        this.setOldPOProductPlus(filterDs);
    }

    /**
     * 响应确定提交参数，进行逻辑处理
     */
    public void serverBbossFlow(IRequestCycle cycle) throws Exception
    {

        IData inputData = new DataMap();

        IData manage_info = new DataMap(getData().getString("MANAGE_INFO_HIDDEN"));
        String product_id = getData().getString("PRODUCT_NUMBER");
        String user_id = getData().getString("BBOSS_USER_ID");
        String merch_trade_id = getData().getString("MERCH_TRADE_ID");
        String orderId = getData().getString("ORDER_ID");
        String eparchycode = getData().getString("GRP_USER_EPARCHYCODE");
        String tradeId = getData().getString("TRADE_ID");

        inputData.put(Route.USER_EPARCHY_CODE, eparchycode);

        IData parmData = new DataMap();
        parmData.put("PRODUCT_ID", getData().getString("PRODUCT_NUMBER"));
        parmData.put("TRADE_ID", tradeId);

        parmData.put("BBOSSFLOW", manage_info.getString("BbossFlow"));
        parmData.put("USER_ID", user_id);

        String bbossFlow = manage_info.getString("BbossFlow");
        if (bbossFlow == null || "".equals(bbossFlow))
        {
            CSViewException.apperr(CrmCommException.CRM_COMM_484);
        }

        bbossFlow = bbossFlow.substring(manage_info.getString("BbossFlow").indexOf("_") + 1, manage_info.getString("BbossFlow").lastIndexOf("_"));

        // modify by zhouwei 20120405 begin
        // 0 不需要发产品属性报文,2 发属性,3 全发,1 发一部分
        String bbossFlow_2 = "";
        if (bbossFlow.length() == 2)
        {
            bbossFlow_2 = bbossFlow.substring(1, 2); // 配合省填写参数逻辑控制
            bbossFlow = bbossFlow.substring(0, 1); // 主办省填写参数逻辑控制 0：不需要发产品属性报文

        }

        // 处理商品级附件名称

        String pOAttachment = getData().getString("POATTACHMENT");

        if (pOAttachment != null && !"".equals(pOAttachment))
        {
            IData iData = new DataMap();
            iData.put("TRADE_ID", merch_trade_id);
            // 商品信息
            IDataset merchList = CSViewCall.call(this, "CS.TradeGrpMerchInfoQrySVC.qryAllMerchInfoByTradeId", iData);
            // 插TF_B_TRADE_OTHER
            iData.clear();
            iData.put("pOAttachment", pOAttachment);
            iData.put("TRADE_ID", merch_trade_id);
            iData.put("BBOSS_USER_ID", merchList.getData(0).getString("USER_ID"));

            inputData.put("POATTACHMENT_DATA", iData);
        }

        // 拼写产品属性
        IData product_paramsData = manage_info.getData("PRODUCT_PARAM");// 产品属性

        IDataset product_parms = product_paramsData.getDataset(product_id);
        IData inparam = new DataMap();
        inparam.put("FLOWPOINT", getData().getString("FLOWPOINT"));
        inparam.put("TRADE_ID", tradeId);
        inparam.put("BBOSS_USER_ID", getData().getString("BBOSS_USER_ID"));
        inparam.put("PRODUCT_PARMS", product_parms);

        inparam.put(Route.USER_EPARCHY_CODE, eparchycode);

        // 拼写资费
        IData merchPDiscntsData = manage_info.getData("PRODUCTS_ELEMENT"); // 选择的产品资费

        IDataset merchPDiscnts = merchPDiscntsData.getDataset(product_id);
        inparam.put("MERCHPDISCNTS", merchPDiscnts);
        // 查询merchp表
        inparam.put("USER_ID", getData().getString("BBOSS_USER_ID"));
        IDataset merchpList = CSViewCall.call(this, "CS.TradeGrpMerchpInfoQrySVC.qryGrpMerchpByUserIdAndTradeId", inparam);
        if (merchpList != null && merchpList.size() > 0)
        {
            inparam.put("MERCH_SPEC_CODE", merchpList.getData(0).getString("MERCH_SPEC_CODE"));
            inparam.put("PRODUCT_ORDER_ID", merchpList.getData(0).getString("PRODUCT_ORDER_ID"));
            inparam.put("PRODUCT_OFFER_ID", merchpList.getData(0).getString("PRODUCT_OFFER_ID"));
            inparam.put("PRODUCT_SPEC_CODE", merchpList.getData(0).getString("PRODUCT_SPEC_CODE"));
            inparam.put("PRODUCT_NUMBER", product_id);
        }

        // 将属性和资费保存
        inputData.put("ATTR_DINS_INFO", inparam);

        // 处里省BBOSS反馈信息
        // 反馈管理报文时，调用EMOS的接口

        // 是否走esop流程
        boolean sendpf = BizEnv.getEnvBoolean("isesop", false);

        if (sendpf && StringUtils.isNotEmpty(ibsysid))
        {

            IData param = new DataMap();

            param.put("USER_ID", user_id);
            param.put("IBSYSID", ibsysid);
            param.put("NODE_ID", nodeid);
            param.put("TRADE_ID", tradeId);
            param.put("BPM_TEMPLET_ID", bpmtempleid);
            param.put("MAIN_TEMPLET_ID", maintempleid);
            // param.put("ROLE_ID", getVisit().get);
            param.put("CITY_CODE", getVisit().getCityCode());
            param.put("DEPART_ID", getVisit().getDepartId());
            param.put("DEPART_NAME", getVisit().getDepartName());
            param.put("EPARCHY_CODE", getTradeEparchyCode());
            param.put("STAFF_ID", getVisit().getStaffId());
            param.put("STAFF_NAME", getVisit().getStaffName());
            param.put("DEAL_STATE", "2");
            param.put("X_TRANS_CODE", "ITF_EOS_TcsGrpBusi");
            param.put("X_SUBTRANS_CODE", "SaveAndSend");
            param.put("OPER_CODE", "01");

            param.put("ORIG_DOMAIN", "ECRM"); // 发起方应用域代码
            param.put("HOME_DOMAIN", "ECRM"); // 归属方应用域代码
            param.put("BIPCODE", "EOS2D011"); // 业务交易代码 这个编码要传进来
            param.put("ACTIVITYCODE", "T2011011"); // 交易代码 这个编码也要传进来
            param.put("BUSI_SIGN", ""); // 报文类型，BPM要基于此判断
            param.put("WORK_TYPE", "00"); // 提交类型

            param.put("PROCESS_TIME", SysDateMgr.getSysDate()); // 处理时间
            param.put("ACCEPT_DATE", SysDateMgr.getSysDate()); // 受理时间
            param.put("UPDATE_STAFF_ID", getVisit().getStaffId()); // 受理员工
            param.put("UPDATE_DEPART_ID", getVisit().getDepartId()); // 受理部门

            param.put("WORK_ID", workid); // BPM工作标识,界面提交时传其它不传

            param.put("X_RESULTINFO", "TradeOk");
            param.put("X_RESULTCODE", "0");

            inputData.put("ESOP", param);

        }

        // 处里省BBOSS反馈信息
        manage_info.put("FLOWPOINT", getData().getString("FLOWPOINT"));
        manage_info.put("TRADE_ID", tradeId);
        manage_info.put("BBOSS_USER_ID", getData().getString("BBOSS_USER_ID"));
        manage_info.put(Route.USER_EPARCHY_CODE, getData().getString("GRP_USER_EPARCHYCODE"));

        inputData.put("MANAGE_INFO", manage_info);

        // 更新trade表将状态改成非W
        IData inparams = new DataMap();
        inparams.clear();
        inparams.put("ORDER_ID", orderId);
        inparams.put("FLAG", "1");
        inparams.put("MERCH_TRADE_ID", merch_trade_id);

        // 更改主台帐标记为受理状态
        inparams.put("RSRV_STR10", "");
        inparams.put("TRADE_ID", tradeId);

        inputData.put("TRADE_INFO", inparams);

        CSViewCall.call(this, "CS.SynBBossGrpMgrBizSVC.RspBBossManage", inputData);

    }

    public abstract void setBbossUserId(String bbossUserId);

    public abstract void setBossManageInfo(IDataset bossManageInfo);

    public abstract void setCondition(IData condition);

    public abstract void setFlowInfo(IDataset flowInfo); // 流程信息

    public abstract void setFlowPoint(String flowPoint);

    public abstract void setForcetag(String na);

    public abstract void setGrpUserEparchycode(String eparchycode);

    public abstract void setIbsysid(String ibsysid);

    public abstract void setInfo(IData info);

    public abstract void setInfos(IData infos);

    public abstract void setManageInfo(IDataset manageInfo);// 管理信息

    public abstract void setMerchTradeId(String merchtradeid);

    public abstract void setMyFlag(String myFlag);

    // 已经有产品的参数信息
    public abstract void setOldPOProductPlus(IDataset pOProductPlus);

    public abstract void setOrderId(String orderId);

    // 产品的参数信息

    public abstract void setPlusProducts(IDataset plusProducts);

    public abstract void setPoInfos(IDataset poInfos);

    public abstract void setPoList(IDataset poList);

    public abstract void setPOProductPlus(IDataset pOProductPlus);

    public abstract void setProductDisInfos(IDataset productDisInfos);

    public abstract void setProductInfos(IDataset productInfos);

    public abstract void setPurchaseInfos(IDataset purchaseInfos);

    public abstract void setRelawwList(IDataset relawwlist);

    public abstract void setTradeId(String tradeId);

    public abstract void setUseTag(String use_tag);

    /**
     * chenyi 验证互联网专线所在省客户编码 13-12-2
     * 
     * @throws Exception
     */
    public void verifyInterNetGroupInfo(IRequestCycle cycle) throws Exception
    {
        String tradeId = getData().getString("MERCH_TRADE_ID");
        String group_id = getData().getString("GROUP_ID");
        IData inparams = new DataMap();
        inparams.put("TRADE_ID", tradeId);
        IDataset ds = CSViewCall.call(this, "CS.TradeGrpMerchInfoQrySVC.qryAllMerchInfoByTradeId", inparams);
        if (IDataUtil.isNotEmpty(ds))
        {
            String bizMode = ds.getData(0).getString("BIZ_MODE");
            if ("4".equals(bizMode))
            {
                inparams.clear();
                inparams.put("GROUP_ID", group_id);
                IDataset groupInfo = CSViewCall.call(this, "CS.UcaInfoQrySVC.qryGrpInfoByGrpId", inparams);

                // 可能直接写的集团对应的EC编码
                if (IDataUtil.isEmpty(groupInfo))
                {
                    inparams.clear();
                    inparams.put("MP_GROUP_CUST_CODE", group_id);
                    groupInfo = CSViewCall.call(this, "CS.GrpInfoQrySVC.getGroupByMpGroup", inparams);
                }

                if (IDataUtil.isEmpty(groupInfo) || (StringUtils.isBlank(groupInfo.getData(0).getString("MP_GROUP_CUST_CODE"))))
                {
                    CSViewException.apperr(CrmCommException.CRM_COMM_548);
                }
                else
                {
                    inparams.clear();
                    inparams.put("MP_GROUP_CUST_CODE", groupInfo.getData(0).getString("MP_GROUP_CUST_CODE"));
                    setAjax(inparams);
                }
            }
        }

    }
    
    /**
     * @description 获取一点支付业务成员附件等参数导入的批次号
     * @author xunyl
     * @date 2015-10-28
     */
    public void getBatchId(IRequestCycle cycle)throws Exception{
        //1- 序列生成批次号
        IData inparam = new DataMap();
        inparam.put(Route.USER_EPARCHY_CODE, getTradeEparchyCode());
        IData taskIdData = CSViewCall.call(this, "CS.SeqMgrSVC.getBatchId", inparam).getData(0);
        String batTaskId = taskIdData.getString("seq_id");
        
        //2- 返回序列号
        IData idata = new DataMap();
        idata.put("result", batTaskId);
        setAjax(idata);
    }
    
    /**
     * @description 获取一点支付业务成员附件的参数生成的文件名称
     * @param xunyl
     * @date 2015-10-28
     */
    public void getFileName(IRequestCycle cycle)throws Exception{
        //1- 获取批次号
        String batchTaskId= getData().getString("BATCH_TASK_ID");
        
        //2- 根据批次号获取文件名称
        String key = CacheKey.getBossBatchInfoKey(batchTaskId);
        String fileName = SharedCache.get(key).toString();
        
        //3- 返回文件名
        IData idata = new DataMap();
        idata.put("result", fileName);
        setAjax(idata);
    }

}
