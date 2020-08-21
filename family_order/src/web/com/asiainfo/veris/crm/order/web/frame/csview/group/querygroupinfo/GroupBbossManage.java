
package com.asiainfo.veris.crm.order.web.frame.csview.group.querygroupinfo;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.attrbiz.AttrBizInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.product.productcomprelainfo.ProductCompRelaInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.upc.UpcViewCall;

/**
 * TODO 此处填写 class 信息
 * 
 * @author yourname (mailto:yourname@primeton.com)
 */

public abstract class GroupBbossManage extends CSBasePage
{

    /**
     * ajax传参方法
     * 
     * @param cycle
     * @throws Exception
     */
    public void ajaxSetPospecNumber(IRequestCycle cycle) throws Exception
    {
    	IData pData = this.getData();
    	String pospecnumber = pData.getString("cond_POSPECNUMBER", "");
    	this.setPoProducts(UpcViewCall.queryPoproductByPospecNumber(this, pospecnumber));
    }
    
    /**
     * @Description: 初始化页面方法
     * @author jch
     * @date 2009-8-3
     * @param cycle
     * @throws Exception
     */
    public void initialQry(IRequestCycle cycle) throws Exception
    {
        IData initdata = getData("cond", true);

        IDataset poList = UpcViewCall.queryPoByValid(this);
        setPoInfos(poList);
        setCondition(initdata);
    }

    /**
     * 省BOSS取消受理
     * 
     * @param cycle
     * @throws Exception
     */
    public void cancelSend(IRequestCycle cycle) throws Exception
    {

        IData param = getData();

        CSViewCall.call(this, "CS.GroupBBossManageSVC.cancelSend", param);

        IData ctrlInfo = new DataMap();

        ctrlInfo.put("strHint", "取消受理成功！");
    }

    public abstract IData getCondition();

    /**
     * 作用：根据group_id查询集团基本信息 默认传入为cond_GROUP_ID
     * 
     * @author zhujm 2009-03-06
     * @param cycle
     * @throws Throwable
     */
    public void getGroupBaseInfo(IRequestCycle cycle) throws Throwable
    {

    }

    public abstract IDataset getInfos();

    public abstract IDataset getSubInfos();

    /**
     * @Description: 初始化页面方法
     * @author jch
     * @date 2009-8-3
     * @param cycle
     * @throws Exception
     */
    public void initial(IRequestCycle cycle) throws Exception
    {

        IData initdata = getData("cond", true);
        IData param = getData();
        String ibsysid = param.getString("IBSYSID", "");
        String workId = param.getString("WORK_ID", "");
        IData ctrlInfo = new DataMap();

        String start_date = SysDateMgr.getFirstDayOfThisMonth4WEB();
        String end_date = SysDateMgr.getLastDateThisMonth4WEB();

        // 现代码
        if ("".equals(ibsysid) && "".equals(workId))
        {

            // queryBbossTradeOtherInfo(cycle);
            initdata.put("cond_START_DATE", start_date);
            initdata.put("cond_END_DATE", end_date);
            setCondition(initdata);
            ctrlInfo.put("strHint", "请输入查询条件~~！");
        }
        else
        {
            IData condition = new DataMap();
            condition.put("cond_START_DATE", start_date);
            condition.put("cond_END_DATE", end_date);
            condition.put("cond_IS_ESOP", "true");
            setCondition(condition);

            IData inData = new DataMap();
            inData.put("X_TRANS_CODE", "ITF_EOS_QcsGrpBusi");
            inData.put("X_SUBTRANS_CODE", "GetEosInfo");
            inData.put("NODE_ID", param.getString("NODE_ID"));
            inData.put("IBSYSID", param.getString("IBSYSID", ""));
            inData.put("OPER_CODE", "13");
            inData.putAll(param);
            // IData httpResult = (IData) HttpHelper.callHttpSvc(pd,"ITF_EOS_QcsGrpBusi", inData);
            // IData httpResult =CSViewCall.callHttp(this,"ITF_EOS_QcsGrpBusi", inData);
            IDataset httResultSetDataset = CSViewCall.call(this, "SS.ESOPQcsGrpBusiIntfSvc.getEosInfo", inData);
            if (IDataUtil.isEmpty(httResultSetDataset))
                CSViewException.apperr(GrpException.CRM_GRP_508);
            IData httpResult = httResultSetDataset.getData(0);

            IData esop_data = new DataMap();
            esop_data.put("IBSYSID", param.getString("IBSYSID", ""));
            esop_data.put("NODE_ID", param.getString("NODE_ID", ""));
            esop_data.put("BPM_TEMPLET_ID", param.getString("BPM_TEMPLET_ID", ""));
            esop_data.put("MAIN_TEMPLET_ID", param.getString("MAIN_TEMPLET_ID", ""));
            esop_data.put("WORK_ID", param.getString("WORK_ID", ""));
            setEsop(esop_data);// 管理报文反馈接口回调esop用
            param.putAll(httpResult);

            String product_id_a = param.getString("PRODUCT_ID");

            //String product_id_b = StaticUtil.getStaticValue(getVisit(), "TD_B_PRODUCT_COMP_RELA", "PRODUCT_ID_A", "PRODUCT_ID_B", product_id_a);
            // 根据商品编号获取产品信息
            IDataset ds = ProductCompRelaInfoIntfViewUtil.qryProductCompRelaInfosByProductIdARelationTypeCodeAndForceTag(this, product_id_a, "1", null);
            String product_id_b = ds.getData(0).getString("PRODUCT_ID_B");

            param.put("PRODUCT_ID", product_id_b);
            // 查看预受理台帐信息
            IDataset dataset = CSViewCall.call(this, "CS.TradeInfoQrySVC.queryBbossTradeByEsop", param);
            if (dataset.size() == 0)
            {
                ctrlInfo.put("strHint", "没有符合条件的查询结果~~！");
                this.setCtrlInfo(ctrlInfo);
                return;
            }
            else
            {
                ctrlInfo.put("strHint", "查询成功~~！");
            }
            dataset.getData(0).put("IBSYSID", param.getString("IBSYSID"));

            // 查询集团产品订单信息
            for (int i = 0, sizeI = dataset.size(); i < sizeI; i++)
            {
                IData merchpTradeInfo = dataset.getData(i);
                String user_id_a = merchpTradeInfo.getString("USER_ID_A");
                String group_id = merchpTradeInfo.getString("GROUP_ID");
                String merch_spec_code = merchpTradeInfo.getString("MERCH_SPEC_CODE");
                IData inparm = new DataMap();
                inparm.put("USER_ID", user_id_a);
                inparm.put("GROUP_ID", group_id);
                inparm.put("MERCH_SPEC_CODE", merch_spec_code);

                IDataset merchInfo = CSViewCall.call(this, "CS.TradeGrpMerchInfoQrySVC.qryMerchInfoByGrpUid", inparm);
                if (IDataUtil.isNotEmpty(dataset))
                {
                    merchpTradeInfo.put("MERCH_ORDER_ID", merchInfo.getData(0).getString("MERCH_ORDER_ID"));
                }

            }

            setInfos(dataset);

            /* 拼端到端台帐数据，tf_b_trade_ext表保存所需要数据 开始 */
            IData eosData = new DataMap();
            IDataset eos = new DatasetList();
            eosData.put("IBSYSID", param.getString("IBSYSID"));
            eosData.put("SUB_IBSYSID", param.getString("SUB_IBSYSID"));
            eosData.put("NODE_ID", param.getString("NODE_ID"));
            eosData.put("BPM_TEMPLET_ID", param.getString("BPM_TEMPLET_ID"));
            eosData.put("MAIN_TEMPLET_ID", param.getString("MAIN_TEMPLET_ID"));
            eosData.put("ATTR_CODE", "ESOP");
            eosData.put("ATTR_VALUE", param.getString("IBSYSID"));
            eosData.put("RSRV_STR1", param.getString("NODE_ID"));
            eosData.put("WORK_ID", param.getString("WORK_ID", ""));

            // 查询tradeid
            IData paramId = new DataMap();
            paramId.put("ATTR_VALUE", param.getString("IBSYSID"));
            IDataset datas = CSViewCall.call(this, "CS.TradeInfoQrySVC.getTradeBysubscribeIdForEsop", paramId);
            String tradeId = "";
            if (null != datas && !datas.isEmpty())
            {
                tradeId = datas.getData(0).getString("TRADE_ID", "");
            }
            eosData.put("TRADE_ID", tradeId);
            // end by szw 20130329

            eos.add(eosData);
            IData para = new DataMap();
            para.put("cond_CREATE_STAFF_ID", getVisit().getStaffId());
            para.put("EOS", eos);
            setEos(para);

        }

        setCtrlInfo(ctrlInfo);
        this.setPoInfos(UpcViewCall.queryPoByValid(this));
    }

    /**
     * @Description 管理流程（主办省、配合省）ESOP页面初始化
     * @author jch
     * @throws Exception
     * @param cycle
     */
    public void initialEsop(IRequestCycle cycle) throws Exception
    {

        IData param = getData();
        IData ctrlInfo = new DataMap();
        IDataset manageInfos = CSViewCall.call(this, "CS.TradeOtherInfoQrySVC.queryBbossManageInfoByEsop", param);

        for (int i = 0, size = manageInfos.size(); i < size; i++)
        {
            IData otherInfo = manageInfos.getData(i);
            String flowPoint = otherInfo.getString("RSRV_VALUE_CODE").substring(6);
            IData imparamData = new DataMap();
            imparamData.put("ATTR_CODE", flowPoint);
            IDataOutput data = CSViewCall.callPage(this, "CS.BBossAttrQrySVC.qryBBossAttrByAttrCode", imparamData, getPagination("infonav"));
            if (IDataUtil.isNotEmpty(data.getData()))
            {
                IData attrInfoData = data.getData().getData(0);
                otherInfo.put("ATTR_NAME", attrInfoData.getString("ATTR_NAME"));
                otherInfo.put("DEFAULT_VALUE", attrInfoData.getString("DEFAULT_VALUE"));

                imparamData.clear();
                imparamData.put("TRADE_ID", otherInfo.getString("TRADE_ID"));
                IDataOutput dataInfo = CSViewCall.callPage(this, "CS.TradeUserInfoQrySVC.getTradeUserByTradeIdForGrp", imparamData, getPagination("infonav"));
                IData userInfo = dataInfo.getData().getData(0);
                otherInfo.put("GRP_USER_EPARCHYCODE", userInfo.getString("EPARCHY_CODE"));

            }

        }
        //

        if (manageInfos.size() == 0)
        {
            ctrlInfo.put("strHint", "没有符合条件的查询结果~~！");
        }
        else
        {
            ctrlInfo.put("strHint", "查询成功~~！");
        }
        
        setInfos(manageInfos);
        IData esop_data = new DataMap();
        esop_data.put("IBSYSID", param.getString("IBSYSID", ""));
        esop_data.put("NODE_ID", param.getString("NODE_ID", ""));
        esop_data.put("BPM_TEMPLET_ID", param.getString("BPM_TEMPLET_ID", ""));
        esop_data.put("MAIN_TEMPLET_ID", param.getString("MAIN_TEMPLET_ID", ""));
        esop_data.put("WORK_ID", param.getString("WORK_ID", ""));
        setEsop(esop_data);// 管理报文反馈接口回调esop用

        IData condition = new DataMap();
        condition.put("cond_IS_ESOP", "true");
        setCondition(condition);
        this.setPoInfos(UpcViewCall.queryPoByValid(this));
    }

    /**
     * @author weixb3
     * @Description 查询出TF_B_TRADE中需要有预受理BBOSS的数据
     * @throws Exception
     * @param cycle
     */
    public void queryBbossTrade(IRequestCycle cycle) throws Exception
    {

        IData param = getData();
        String ibsysid = param.getString("IBSYSID", "");
        IData ctrlInfo = new DataMap();

        if (StringUtils.equals("", ibsysid))
        {
            IDataOutput dop = CSViewCall.callPage(this, "CS.TradeGrpMerchInfoQrySVC.queryBbossTrade", param, getPagination("infonav"));

            IDataset dataset = dop.getData();

            if (IDataUtil.isEmpty(dataset))
            {
                ctrlInfo.put("strHint", "没有符合条件的查询结果！");
            }
            else
            {

                String pospecnumber = param.getString("POSPECNUMBER");
                IDataset ids = AttrBizInfoIntfViewUtil.qryAttrBizInfosByIdAndIdTypeAttrObjAttrValue(this, "1", "B", "PRO", pospecnumber);

                if (IDataUtil.isNotEmpty(ids))
                {

                    for (int i = 0, size = dataset.size(); i < size; i++)
                    {
                        dataset.getData(i).put("MPRODUCT_ID", ids.getData(0).getString("ATTR_CODE"));
                        dataset.getData(i).put("MPRODUCT_NAME", UpcViewCall.queryOfferNameByOfferId(this, "P", ids.getData(0).getString("ATTR_CODE")));
                        dataset.getData(i).put("PRODUCT_NAME", UpcViewCall.queryOfferNameByOfferId(this, "P", dataset.getData(i).getString("PRODUCT_ID", "")));
                    }

                    param.put("MPRODUCT_ID", ids.getData(0).getString("ATTR_CODE"));

                }
                ctrlInfo.put("strHint", "查询成功！");
            }
            setCtrlInfo(ctrlInfo);
            setCondition(param);
            setInfos(dataset);
            setInfoCount(dop.getDataCount());
        }
        else
        {
            // esop业务
            IData condition = new DataMap();
            condition.put("cond_IS_ESOP", "true");
            setCondition(condition);

            IData inData = new DataMap();
            inData.put("X_TRANS_CODE", "ITF_EOS_QcsGrpBusi");
            inData.put("X_SUBTRANS_CODE", "GetEosInfo");
            inData.put("NODE_ID", param.getString("NODE_ID"));
            inData.put("IBSYSID", param.getString("IBSYSID", ""));
            inData.put("OPER_CODE", "13");
            inData.putAll(param);
            IDataset httResultSetDataset = CSViewCall.call(this, "SS.ESOPQcsGrpBusiIntfSvc.getEosInfo", inData);
            if (IDataUtil.isEmpty(httResultSetDataset))
                CSViewException.apperr(GrpException.CRM_GRP_508);
            IData httpResult = httResultSetDataset.getData(0);

            IData esop_data = new DataMap();
            esop_data.put("IBSYSID", param.getString("IBSYSID", ""));
            esop_data.put("NODE_ID", param.getString("NODE_ID", ""));
            esop_data.put("BPM_TEMPLET_ID", param.getString("BPM_TEMPLET_ID", ""));
            esop_data.put("MAIN_TEMPLET_ID", param.getString("MAIN_TEMPLET_ID", ""));
            esop_data.put("WORK_ID", param.getString("WORK_ID", ""));
            setEsop(esop_data);// 管理报文反馈接口回调esop用
            param.putAll(httpResult);

            // 查看预受理台帐信息
            IDataset dataset = CSViewCall.call(this, "CS.TradeInfoQrySVC.queryBbossTradeByEsop", param);
            if (dataset.size() == 0)
            {
                ctrlInfo.put("strHint", "没有符合条件的查询结果~~！");
                this.setCtrlInfo(ctrlInfo);
                return;
            }
            else
            {
                ctrlInfo.put("strHint", "查询成功~~！");
            }

            dataset.getData(0).put("IBSYSID", param.getString("IBSYSID"));
            setInfos(dataset);

            /* 拼端到端台帐数据，tf_b_trade_ext表保存所需要数据 开始 */
            IData eosData = new DataMap();
            IDataset eos = new DatasetList();
            eosData.put("IBSYSID", param.getString("IBSYSID"));
            eosData.put("SUB_IBSYSID", param.getString("SUB_IBSYSID"));
            eosData.put("NODE_ID", param.getString("NODE_ID"));
            eosData.put("BPM_TEMPLET_ID", param.getString("BPM_TEMPLET_ID"));
            eosData.put("MAIN_TEMPLET_ID", param.getString("MAIN_TEMPLET_ID"));
            eosData.put("ATTR_CODE", "ESOP");
            eosData.put("ATTR_VALUE", param.getString("IBSYSID"));
            eosData.put("RSRV_STR1", param.getString("NODE_ID"));
            eosData.put("WORK_ID", param.getString("WORK_ID", ""));

            // 查询tradeid

            IData paramId = new DataMap();
            paramId.put("ATTR_VALUE", param.getString("IBSYSID"));
            IDataset datas = CSViewCall.call(this, " CS.TradeInfoQrySVC.getTradeBysubscribeIdForEsop", paramId);
            String tradeId = "";
            if (null != datas && !datas.isEmpty())
            {
                tradeId = datas.getData(0).getString("TRADE_ID", "");
            }
            eosData.put("TRADE_ID", tradeId);
            // end by szw 20130329

            eos.add(eosData);
            IData para = new DataMap();
            para.put("EOS", eos);
            setEos(para);

            // setCondition(param);
            // getTradeData().put("EOS", eos);
            // pd.setTransfer("tradeData", getTradeData().toString());
            /* 拼端到端台帐数据，tf_b_trade_ext表保存所需要数据 结束 */

        }

    }

    /**
     * @author jch
     * @Description 查询出TF_B_TRADE_OTHER中状态为未操作的状态
     * @throws Exception
     * @param cycle
     */

    public void queryBbossTradeOtherInfo(IRequestCycle cycle) throws Exception
    {
        IData param = getData();
        param.toString();
        // cond_OPERATE_FLAG

        IDataOutput dop = CSViewCall.callPage(this, "CS.TradeOtherInfoQrySVC.queryBbossManageDetailInfo", param, getPagination("infonav"));

        IDataset dataset = dop.getData();
        for (int i = 0, size = dataset.size(); i < size; i++)
        {
            IData otherInfo = dataset.getData(i);
            String flowPoint = otherInfo.getString("RSRV_VALUE_CODE").substring(6);
            IData imparamData = new DataMap();
            imparamData.put("ATTR_CODE", flowPoint);
            IDataOutput data = CSViewCall.callPage(this, "CS.BBossAttrQrySVC.qryBBossAttrByAttrCode", imparamData, getPagination("infonav"));
            if (IDataUtil.isNotEmpty(data.getData()))
            {
                IData attrInfoData = data.getData().getData(0);
                otherInfo.put("ATTR_NAME", attrInfoData.getString("ATTR_NAME"));
                otherInfo.put("DEFAULT_VALUE", attrInfoData.getString("DEFAULT_VALUE"));

                imparamData.clear();
                imparamData.put("TRADE_ID", otherInfo.getString("TRADE_ID"));
                IDataOutput dataInfo = CSViewCall.callPage(this, "CS.TradeUserInfoQrySVC.getTradeUserByTradeIdForGrp", imparamData, getPagination("infonav"));
                if (IDataUtil.isNotEmpty(dataInfo.getData()))
                {
                    IData userInfo = dataInfo.getData().getData(0);
                    otherInfo.put("GRP_USER_EPARCHYCODE", userInfo.getString("EPARCHY_CODE"));
                }

            }
            otherInfo.put("PRODUCT_NAME21", UpcViewCall.queryOfferNameByOfferId(this, "P", otherInfo.getString("RSRV_STR21", "")));
            otherInfo.put("PRODUCT_NAME22", UpcViewCall.queryOfferNameByOfferId(this, "P", otherInfo.getString("RSRV_STR22", "")));
        }
        setInfoCount(dop.getDataCount());

        IData ctrlInfo = new DataMap();
        if (dataset.size() == 0)
        {
            ctrlInfo.put("strHint", "没有符合条件的查询结果！");
        }
        else
        {
            ctrlInfo.put("strHint", "查询成功！");
        }
        setCtrlInfo(ctrlInfo);
        setCondition(getData("cond", true));
        setInfos(dataset);

    }

    /**
     * @author jch
     * @Description 查询出TF_B_TRADE_EXT中商品处理失败数据
     * @throws Exception
     * @param cycle
     */

    public void queryPoError(IRequestCycle cycle) throws Exception
    {

        IData param = getData();

        IDataOutput dop = CSViewCall.callPage(this, "CS.BbossQueryBizSVC.queryPoError", param, getPagination("infonav"));
        IDataset dataset = dop.getData();
        IData ctrlInfo = new DataMap();
        if (dataset.size() == 0)
        {
            ctrlInfo.put("strHint", "没有符合条件的查询结果！");
        }
        else
        {
            ctrlInfo.put("strHint", "查询成功！");
        }
        IDataset poList = UpcViewCall.queryPoByValid(this);
        setPoInfos(poList);
        setCtrlInfo(ctrlInfo);
        setCondition(getData("cond", true));
        setInfos(dataset);
        setInfoCount(dop.getDataCount());

    }

    /**
     * 发送受理报文前，对台账进行处理
     * 
     * @param cycle
     * @throws Exception
     */
    public void sendCreateDataBefore(IRequestCycle cycle) throws Exception
    {
        // 1- 获取参数
        IData param = getData();
        param.put(Route.USER_EPARCHY_CODE, getVisit().getStaffEparchyCode());
        param.put("FLAG", "0");

        // 2- 获取发送结果
        IDataset sendResult = CSViewCall.call(this, "CS.CreateBBossUserSVC.sendTradeInfoToBboss", param);
        String resultInfo = (String) sendResult.get(0);

        // 3- 返回结果
        IData ctrlInfo = new DataMap();
        ctrlInfo.put("strHint", resultInfo);
        setCtrlInfo(ctrlInfo);
    }

    public abstract void setCondition(IData condition);

    public abstract void setCtrlInfo(IData ctrlInfo);

    public abstract void setEos(IData eos);

    public abstract void setEsop(IData esop);

    public abstract void setInfoCount(long infoCount);

    public abstract void setInfos(IDataset infos);

    public abstract void setSubInfos(IDataset subInfos);
    public abstract void setPoInfos(IDataset poInfos);
    public abstract void setPoProducts(IDataset poProducts);
}
