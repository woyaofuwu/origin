
package com.asiainfo.veris.crm.order.web.frame.csview.group.querygroupinfo;

import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CustException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.exception.ParamException;
import com.asiainfo.veris.crm.order.pub.exception.TradeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.commparainfo.CommParaInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.upc.UpcViewCall;

/**
 * 查询跨省工单流转状态(BBOSS)
 * 
 * @author jch
 */
/*
 * 修改历史 $Log: GrpProvCprtDetail.java,v $ 修改历史 Revision 1.33 2013/06/22 09:44:58 weixb3 修改历史 *** empty log message ***
 */
public abstract class GrpProvCprtDetail extends CSBasePage
{

    /**
     * @Description: 将IDataset的数据列转换成行，便于前台展示
     * @author jch
     * @date
     * @param
     * @param
     * @param
     * @return
     * @throws Exception
     */
    public IDataset changIDataset(IDataset dataset, String type) throws Exception
    {

        IDataset resultDatasetFact = new DatasetList();

        if ("ProvCustMag".equals(type))
        {
            for (int i = 0; i < dataset.size(); i++)
            {
                IDataset datasetChild = (DatasetList) dataset.get(i);
                IData temp = new DataMap();
                IDataset resultDataset = new DatasetList();
                for (int j = 0; j < datasetChild.size(); j++)
                {
                    IData provData = datasetChild.getData(j);

                    if ("MAG_NAME".equals(provData.getString("ATTR_CODE")))
                    {
                        temp.put("MagName", provData.getString("ATTR_VALUE"));

                    }
                    else if ("MAG_PHONE".equals(provData.getString("ATTR_CODE")))
                    {
                        temp.put("MagPhone", provData.getString("ATTR_VALUE"));
                    }
                    else if ("MAG_TYPE".equals(provData.getString("ATTR_CODE")))
                    {
                        temp.put("MagType", provData.getString("ATTR_VALUE"));
                    }

                    temp.put("SUB_INFO_TYPE", provData.getString("SUB_INFO_TYPE"));
                    // temp.put("ORDER_NUMBER",
                    // provData.getString("ORDERING_ID"));

                    if ((j + 1) >= datasetChild.size())// 说明是最后一个啦
                    {
                        resultDataset.add(temp);
                        continue;
                    }
                    if (j > 0 && !datasetChild.getData(j).getString("SUB_INFO_TYPE").equals(datasetChild.getData(j + 1).getString("SUB_INFO_TYPE")))
                    {

                        resultDataset.add(temp);
                        temp = new DataMap();

                    }

                }
                if (resultDataset.size() > 0)
                    resultDatasetFact.add(resultDataset);
            }

        }
        else if ("CustInfo".equals(type))
        {

            for (int i = 0; i < dataset.size(); i++)
            {
                IDataset datasetChild = (DatasetList) dataset.get(i);
                IDataset resultDataset = new DatasetList();
                IData temp = new DataMap();
                for (int j = 0; j < datasetChild.size(); j++)
                {
                    IData provData = datasetChild.getData(j);

                    if ("CUST_TYPE".equals(provData.getString("ATTR_CODE")))
                    {
                        temp.put("CustType", provData.getString("ATTR_VALUE"));

                    }
                    else if ("CUST_CODE".equals(provData.getString("ATTR_CODE")))
                    {
                        temp.put("CustCode", provData.getString("ATTR_VALUE"));
                    }
                    else if ("CUST_NAME".equals(provData.getString("ATTR_CODE")))
                    {
                        temp.put("CustName", provData.getString("ATTR_VALUE"));
                    }

                    temp.put("SUB_INFO_TYPE", provData.getString("SUB_INFO_TYPE"));
                    temp.put("ATTR_NAME", provData.getString("ATTR_NAME"));

                    if ((j + 1) >= datasetChild.size())// 说明是最后一个啦
                    {
                        resultDataset.add(temp);
                        continue;
                    }
                    if (j > 0 && !datasetChild.getData(j).getString("SUB_INFO_TYPE").equals(datasetChild.getData(j + 1).getString("SUB_INFO_TYPE")))
                    {

                        resultDataset.add(temp);
                        temp = new DataMap();

                    }

                }
                if (resultDataset.size() > 0)
                    resultDatasetFact.add(resultDataset);

            }

        }
        else if ("DealInfo".equals(type))
        {

            for (int i = 0; i < dataset.size(); i++)
            {
                IDataset datasetChild = (DatasetList) dataset.get(i);
                IDataset resultDataset = new DatasetList();
                IData temp = new DataMap();
                for (int j = 0; j < datasetChild.size(); j++)
                {
                    IData provData = datasetChild.getData(j);

                    if ("DEAL_SIDE".equals(provData.getString("ATTR_CODE")))
                    {
                        temp.put("DealSide", provData.getString("ATTR_VALUE"));

                    }
                    else if ("DEAL_DETAIL".equals(provData.getString("ATTR_CODE")))
                    {
                        temp.put("DealDatail", provData.getString("ATTR_VALUE"));
                    }
                    else if ("Attachment".equals(provData.getString("ATTR_CODE")))
                    {
                        temp.put("Attachment", provData.getString("ATTR_VALUE"));
                    }

                    temp.put("SUB_INFO_TYPE", provData.getString("SUB_INFO_TYPE"));
                    temp.put("ATTR_NAME", provData.getString("ATTR_NAME"));

                    if ((j + 1) >= datasetChild.size())// 说明是最后一个啦
                    {
                        resultDataset.add(temp);
                        continue;
                    }
                    if (j > 0 && !datasetChild.getData(j).getString("SUB_INFO_TYPE").equals(datasetChild.getData(j + 1).getString("SUB_INFO_TYPE")))
                    {

                        resultDataset.add(temp);
                        temp = new DataMap();

                    }

                }
                if (resultDataset.size() > 0)
                    resultDatasetFact.add(resultDataset);
            }

        }
        else if ("ProdInfoModify".equals(type))
        {

            for (int i = 0; i < dataset.size(); i++)
            {
                IDataset datasetChild = (DatasetList) dataset.get(i);
                IDataset resultDataset = new DatasetList();
                IData temp = new DataMap();
                for (int j = 0; j < datasetChild.size(); j++)
                {
                    IData provData = datasetChild.getData(j);

                    if ("MODI_TYPE".equals(provData.getString("ATTR_CODE")))
                    {
                        temp.put("ModiType", provData.getString("ATTR_VALUE"));

                    }
                    else if ("MODI_VALUE".equals(provData.getString("ATTR_CODE")))
                    {
                        temp.put("ModiValue", provData.getString("ATTR_VALUE"));
                    }

                    temp.put("SUB_INFO_TYPE", provData.getString("SUB_INFO_TYPE"));
                    temp.put("ATTR_NAME", provData.getString("ATTR_NAME"));

                    if ((j + 1) >= datasetChild.size())// 说明是最后一个啦
                    {
                        resultDataset.add(temp);
                        continue;
                    }
                    if (j > 0 && !datasetChild.getData(j).getString("SUB_INFO_TYPE").equals(datasetChild.getData(j + 1).getString("SUB_INFO_TYPE")))
                    {

                        resultDataset.add(temp);
                        temp = new DataMap();

                    }

                }
                if (resultDataset.size() > 0)
                    resultDatasetFact.add(resultDataset);
            }

        }
        else if ("RespPerson".equals(type))
        {

            IDataset resultDataset = new DatasetList();
            IData temp = new DataMap();
            for (int i = 0; i < dataset.size(); i++)
            {

                IData provData = dataset.getData(i);

                if ("NAME".equals(provData.getString("ATTR_CODE")))
                {
                    temp.put("Name", provData.getString("ATTR_VALUE"));

                }
                else if ("PHONE".equals(provData.getString("ATTR_CODE")))
                {
                    temp.put("Phone", provData.getString("ATTR_VALUE"));
                }
                else if ("DEPARTMENT".equals(provData.getString("ATTR_CODE")))
                {
                    temp.put("Department", provData.getString("ATTR_VALUE"));
                }

                temp.put("SUB_INFO_TYPE", provData.getString("SUB_INFO_TYPE"));
                temp.put("ATTR_NAME", provData.getString("ATTR_NAME"));

                if ((i + 1) >= dataset.size())// 说明是最后一个啦
                {
                    resultDataset.add(temp);
                    continue;
                }
                if (i > 0 && !dataset.getData(i).getString("SUB_INFO_TYPE").equals(dataset.getData(i + 1).getString("SUB_INFO_TYPE")))
                {

                    resultDataset.add(temp);
                    temp = new DataMap();

                }

            }
            if (resultDataset.size() > 0)
                resultDatasetFact.add(resultDataset);

        }

        return resultDatasetFact;

    }

    public abstract IDataset getAttrInfos();

    public abstract IData getCondition();

    public abstract IDataset getContactorInfos();

    public abstract IDataset getCustInfos();

    public abstract IDataset getCustInfosfk();

    public abstract IDataset getDealInfos();

    public abstract IDataset getDealInfosfk();

    public abstract IDataset getDiscnts();

    public abstract IData getGroupInfo();

    public abstract IDataset getInfos();

    public abstract IData getMerchInfo();

    public abstract IDataset getMerchpInfos();

    public abstract IDataset getPoAttraMent();

    public abstract IDataset getPoAudit();

    public abstract IData getPoTradeInfo();

    public abstract IDataset getProdInfoModifys();

    public abstract IDataset getProdInfoModifysfk();

    public abstract IDataset getProdTrades();

    public abstract IDataset getProdTradesfk();

    public abstract IDataset getProductTradeInfos();

    public abstract IDataset getProvCustMags();

    public abstract IDataset getProvCustMagsfk();

    public abstract IData getRespPersonfk();

    public abstract IDataset getRspPersons();

    public abstract IDataset getSyncStats();

    /**
     * @Description: 将订单信息从原来拆分出来，为了保证以后的esop能共用，这里是初始化订单的详细信息
     * @author jch
     * @date
     * @param
     * @param
     * @param
     * @return
     * @throws Exception
     */
    public void initalOrder(IRequestCycle cycle) throws Exception
    {

        IData urldata = getData();
        String trade_id = urldata.getString("TRADE_ID");
        if (StringUtils.isBlank(trade_id) || StringUtils.isEmpty(trade_id))
            CSViewException.apperr(ParamException.CRM_PARAM_150);
        // 1:读取客户信息

        IDataset groupCustDataset = CSViewCall.call(this, "CS.GrpInfoQrySVC.queryCustGroupInfoByMpCustCode", urldata);

        if (groupCustDataset == null || groupCustDataset.size() == 0)
        {
            CSViewException.apperr(CustException.CRM_CUST_43);
        }

        this.setGroupInfo(groupCustDataset.getData(0));

        // 2:商品信息
        IDataset memchDataset = CSViewCall.call(this, "CS.TradeGrpMerchInfoQrySVC.qryMerchInfoByTradeId", urldata);
        // 由于商品信息是只有1条地，因此不为空的话，取memchDatasetget(0) ,商品信息没有，肯定没产品信息，对于BBOSS产品
        if (memchDataset.isEmpty())
        {
            CSViewException.apperr(TradeException.CRM_TRADE_41, trade_id);
        }

        this.setMerchInfo(memchDataset.getData(0));

        // 套餐 审批信息 * 0-n
        urldata.put("RSRV_VALUE_CODE", "AUDITOR_INFOS");
        IDataset tradeOtherAudit = CSViewCall.call(this, "CS.TradeOtherInfoQrySVC.queryTradeOtherByTradeIdAndRsrvValueCode", urldata);

        this.setPoAudit(tradeOtherAudit);

        // 套餐合同信息 + 1-n
        urldata.put("RSRV_VALUE_CODE", "ATT_INFOS");
        IDataset paAttrMent = CSViewCall.call(this, "CS.TradeOtherInfoQrySVC.queryTradeOtherByTradeIdAndRsrvValueCode", urldata);

        this.setPoAttraMent(paAttrMent);

        // 套餐联系人信息

        urldata.put("RSRV_VALUE_CODE", "CONTACTOR_INFOS");
        IDataset contactorInfo = CSViewCall.call(this, "CS.TradeOtherInfoQrySVC.queryTradeOtherByTradeIdAndRsrvValueCode", urldata);
        this.setContactorInfos(contactorInfo);

        // 3:产品信息 分几次读取 1：产品基础信息TF_B_TRADE_GRP_MERCHP 2:产品属性 3:产品资费
        // tf_b_trade_discnt 4：产品支付省?
        // IDataset
        // prodDataset=(IDataset)call("GrpProvCprtBean.queryTradeProdbyTradeId(
        // urldata, ctx.getPagination());

        // 1：产品基础信息TF_B_TRADE_GRP_MERCHP
        IDataset prodMerchpDataset = CSViewCall.call(this, "CS.TradeGrpMerchInfoQrySVC.queryTradeGrpMerchp", urldata);

        // prodMerchpDataset.set(1, new DataMap());
        // prodMerchpDataset.add( new DataMap());
        IDataset atts = new DatasetList();
        IDataset distints = new DatasetList();

        for (int i = 0; i < prodMerchpDataset.size(); i++)
        {
            IDataset copygrpAttrDataset = new DatasetList();
            IData merchpData = prodMerchpDataset.getData(i);

            // merchpData 为了页面上方便，处理支付省信息

            String payCompany = merchpData.getString("RSRV_STR1");// 支付省信息，多个用分号分割

            if (null != payCompany && !StringUtils.equals("", payCompany))
            {
                StringBuilder str = new StringBuilder();
                String payCompanys[] = StringUtils.split(payCompany, ";");
                for (int j = 0, length = payCompanys.length; j < length; j++)
                {
                    String staticName = StaticUtil.getStaticValue("SYMTHESIS_PROVINCE_CODE", payCompanys[j]);
                    str.append(staticName);
                    if (payCompanys.length != (j + 1))
                        str.append(";");

                }

                merchpData.put("RSRV_STR1", str);
            }

            String productCode = merchpData.getString("PRODUCT_SPEC_CODE");

            // 2:查询出台帐表产品属性

            urldata.put("INST_TYPE", "P");
            urldata.put("USER_ID", merchpData.getString("USER_ID"));

            // 3根据trade_id ,user_id查询出资费信息

            IDataset discnts = CSViewCall.call(this, "CS.TradeDiscntInfoQrySVC.queryDiscntTrade", urldata);
            if (discnts == null)
            {
                discnts = new DatasetList();
            }
            distints.add(discnts);

            IData inparam = new DataMap();
            String productTradeId = merchpData.getString("TRADE_ID");
            String productUserId = merchpData.getString("USER_ID");
            inparam.put("TRADE_ID", productTradeId);
            inparam.put("USER_ID", productUserId);
            inparam.put("INST_TYPE", "P");
            IDataset grpAttrDataset = CSViewCall.call(this, "CS.TradeAttrInfoQrySVC.queryTradeGrpAttrByTradeId", inparam);

            if (grpAttrDataset == null || grpAttrDataset.size() == 0)
                continue;

            // 为了页面上面展示，要改一下，存储结构(RSRV_STR3存放的是参数名称)
            for (int j = 0, size = grpAttrDataset.size(); j < size; j++)
            {
                IData temp = grpAttrDataset.getData(j);

                j = j + 1;
                if (j < grpAttrDataset.size())
                {
                    temp.put("RSRV_STR3_0", j >= grpAttrDataset.size() || grpAttrDataset.size() - j == 1 ? "" : grpAttrDataset.getData(j).getString("RSRV_STR3"));
                    temp.put("ATTR_VALUE_0", j >= grpAttrDataset.size() || grpAttrDataset.size() - j == 1 ? "" : grpAttrDataset.getData(j).getString("ATTR_VALUE"));
                }
                j = j + 1;
                if (j < grpAttrDataset.size())
                {
                    temp.put("RSRV_STR3_1", j >= grpAttrDataset.size() || grpAttrDataset.size() - j == 1 ? "" : grpAttrDataset.getData(j).getString("RSRV_STR3"));
                    temp.put("ATTR_VALUE_1", j >= grpAttrDataset.size() || grpAttrDataset.size() - j == 1 ? "" : grpAttrDataset.getData(j).getString("ATTR_VALUE"));
                }

                copygrpAttrDataset.add(temp);

            }

            atts.add(copygrpAttrDataset);

        }

        this.setDiscnts(distints);

        this.setMerchpInfos(prodMerchpDataset);

        this.setAttrInfos(atts);

        this.setInfos(memchDataset);
    }

    // public abstract void setRespPersonfks(IDataset
    // respPersonfks);//设置反馈当前状态负责人信息
    // public abstract IData getRespPersonfks();

    /**
     * @Description: 初始化工单状态页面查询
     * @author jch
     * @date
     * @param
     * @param
     * @param
     * @return
     * @throws Exception
     */
    public void initial(IRequestCycle cycle) throws Exception
    {

    }

    /**
     * 查TD_S_COMMPARA表中配置的信息
     * 
     * @author shixb
     * @version 创建时间：2009-8-20 下午04:58:48
     */
    public IDataset queryCommparams(String productCode) throws Exception
    {
        return CommParaInfoIntfViewUtil.qryCommParasByParamAttrAndParamCode4EparchyCode(this, "CSM", "3526", productCode, "ZZZZ");

    }

    /**
     * @Description: 根据状态类型，查询出配置表的对应关系
     * @author jch
     * @date
     * @param sync_stae
     *            同步状态
     * @param
     * @param
     * @return
     * @throws Exception
     */
    public IDataset queryCommpInfoByAttrAndParam(String sync_state) throws Exception
    {
        IDataset comparDatas = CommParaInfoIntfViewUtil.qryCommParasByParamAttrAndParamCodeEparchyCode(this, "CSM", "5001", sync_state, "ZZZZ");
        return comparDatas;
    }

    /**
     * @Description: 根据链接传过来的条件查询出结果,根据trade_id,ec_code,sync_seq 读取相应的台账信息
     * @author weixb3
     * @date 2013/10/11
     * @param SYNC_STATE
     *            (1000,1001) 配合省协助业务预受理 SYNC_STATE 1000 配合省协助受理 SYNC_STATE 1001 超时电路反馈 3 modify_tag 0-增加 1-删除 2-修改
     *            TF_B_TRADE_OTHER 审批信息 RSRV1_STR2 IF_ANS 是，和否 0否 1是 是否反馈
     * @return
     * @throws Exception
     */
    public void queryProvCprt(IRequestCycle cycle) throws Exception
    {

        IData urldata = getData(); // 取链接url过来的参数信息

        String trade_id = urldata.getString("TRADE_ID");
        String ec_code = urldata.getString("MP_GROUP_CUST_CODE");
        String sync_seq = urldata.getString("SYNC_SEQUENCE");
        String if_provcprt = urldata.getString("IF_PROVCPRT");
        String if_ans = urldata.getString("IF_ANS");
        String sync_state = urldata.getString("SYNC_STATE");// 同步状态

        IDataset memchDataset = valBaseInfo(urldata, trade_id, ec_code); //
        IData merchInfo = memchDataset.getData(0);
        IDataset pos = UpcViewCall.queryPoByPospecNumber(this, merchInfo.getString("MERCH_SPEC_CODE", ""));
        merchInfo.put("POSPECNAME", pos.getData(0).getString("POSPECNAME", ""));
        this.setMerchInfo(merchInfo);

        // 根据同步序列号
        // 读取下发信息标签
        // 商品工单信息
        IData poTradeInfo = new DataMap();
        String merch_order_id = urldata.getString("MEMCH_ORDER_ID");// 全网商品订单
        poTradeInfo.put("PO_ORDER_ID", merch_order_id);
        this.setPoTradeInfo(poTradeInfo);

        // 读取商品当前信息 ? 0-1
        urldata.put("INFO_TAG", "O");
        IDataset poTradeStateAttrs = CSViewCall.call(this, "CS.TradeOtherInfoQrySVC.queryPoTradeStateAttr", urldata);

        // 1.2.2 修改了 从? 变成 *号

        IData respData = new DataMap();
        IDataset respDataList = new DatasetList();

        if (IDataUtil.isNotEmpty(poTradeStateAttrs))
        {
            IData respPerson = new DataMap();
            for (int poAttr = 0; poAttr < poTradeStateAttrs.size(); poAttr++)
            {
                IData temp = poTradeStateAttrs.getData(poAttr);

                if (StringUtils.equals("RespPerson", temp.getString("INFO_TYPE")))
                {
                    if ("NAME".equals(temp.getString("ATTR_CODE")))
                    {
                        respPerson.put("NAME", temp.getString("ATTR_VALUE"));
                    }
                    else if ("PHONE".equals(temp.getString("ATTR_CODE")))
                    {
                        respPerson.put("PHONE", temp.getString("ATTR_VALUE"));
                    }
                    else if ("DEPARTMENT".equals(temp.getString("ATTR_CODE")))
                    {
                        respPerson.put("DEPARTMENT", temp.getString("ATTR_VALUE"));
                    }
                    if (poAttr > 0 && poAttr % 3 == 2)
                    {
                        IData tempRespPerson = new DataMap();
                        tempRespPerson.putAll(respPerson);
                        respDataList.add(tempRespPerson);
                        respPerson.clear();
                    }

                }
            }
        }
        this.setRspPersons(respDataList);

        // 读取产品工单状态表，定位几个产品

        IDataset prodTradeDataset = CSViewCall.call(this, "CS.TradeProductInfoQrySVC.queryProductTrade", urldata);

        IDataset provCustMag = new DatasetList();
        IDataset custInfo = new DatasetList();
        IDataset dealInfo = new DatasetList();
        IDataset prodInfoModify = new DatasetList();

        for (int i = 0, size = prodTradeDataset.size(); i < size; i++)
        {
            String order_number = prodTradeDataset.getData(i).getString("ORDER_NUMBER");

            IDataset provCustMagChild = new DatasetList();
            IDataset custInfoChild = new DatasetList();
            IDataset dealInfoChild = new DatasetList();
            IDataset prodInfoModifyChild = new DatasetList();

            // 读取产品客户经理信息 * 1-n ,这里一次，将几个类型的信息全部读出来，在应用服务器进行区分，就不分几次读取数据，减少IO
            // 由于查出来的是纵表，这里从新封一次
            urldata.put("INFO_TAG", "P");
            urldata.put("ORDERING_ID", order_number);
            IDataset prodTradeStateAttrs = CSViewCall.call(this, "CS.TradeOtherInfoQrySVC.queryPoTradeStateAttr", urldata);
            for (int j = 0, size2 = prodTradeStateAttrs.size(); j < size2; j++)
            {
                IData temp = prodTradeStateAttrs.getData(j);

                if (StringUtils.equals("ProvCustMag", temp.getString("INFO_TYPE")))// 产品对应的客户经理

                {
                    provCustMagChild.add(temp);
                    continue;
                }
                else if (StringUtils.equals("CustInfo", prodTradeStateAttrs.getData(j).getString("INFO_TYPE"))) // 产品受理涉及的客户信息

                {
                    custInfoChild.add(temp);
                    continue;
                }
                else if (StringUtils.equals("DealInfo", prodTradeStateAttrs.getData(j).getString("INFO_TYPE"))) // 产品落实情况

                {
                    dealInfoChild.add(temp);
                    continue;
                }
                else if (StringUtils.equals("ProdInfoModify", temp.getString("INFO_TYPE"))) // 产品信息修正

                {
                    prodInfoModifyChild.add(temp);
                    continue;
                }

            }
            if (IDataUtil.isNotEmpty(provCustMagChild))
            {
                provCustMag.add(provCustMagChild);
            }
            if (IDataUtil.isNotEmpty(custInfoChild))
            {
                custInfo.add(custInfoChild);
            }
            if (IDataUtil.isNotEmpty(dealInfoChild))
            {
                dealInfo.add(dealInfoChild);
            }
            if (IDataUtil.isNotEmpty(prodInfoModifyChild))
            {
                prodInfoModify.add(prodInfoModifyChild);
            }
        }

        this.setProdInfoModifys(prodInfoModify);
        this.setCustInfos(custInfo);
        this.setDealInfos(dealInfo);
        this.setProvCustMags(provCustMag);
        this.setProdTrades(prodTradeDataset);

        // 配合省协助业务预受理 SYNC_STATE 1000
        // 配合省协助受理 SYNC_STATE 1001 说明需要反馈

        // 1002 配合省分配客户经理信息

        // 1003 配合省落实情况反馈

        // 3- 超时电路反馈
        // 4 超时电路取消申请
        IDataset syncStats = this.queryCommpInfoByAttrAndParam(sync_state);

        IData condition = new DataMap();
        condition.put("PKF_PROD_SIZE", String.valueOf(prodTradeDataset.size()));
        condition.put("MP_EC_CODE", ec_code);
        condition.put("TRADE_ID", trade_id);
        condition.put("SYNC_SEQUENCE", sync_seq);
        condition.put("IF_ANS", if_ans);// 判断是否反馈过，用来判断确定按钮
        condition.put("PRE_SYNC_STATE", sync_state);// 查询的条件
        setCondition(condition);

        if (IDataUtil.isEmpty(syncStats))
        {
            this.setSyncStats(null);
        }
        else
        {
            this.setSyncStats(syncStats);
        }

        if (StringUtils.equals("1", if_ans) && StringUtils.equals("1", if_provcprt))// 如果已反馈且必须是需要反馈,要查，工单反馈信息

        {
            IData data = new DataMap();
            data.put("SYNC_SEQUENCE", sync_seq);
            queryProvCprtFk(data);
        }
        else
        {

            // 根据产品个数构造一个空的

            int productSize = prodTradeDataset.size();

            this.setRespPersonfk(respData);
            IDataset common = new DatasetList();

            for (int i = 0; i < productSize; i++)
            {
                IDataset commonChild = new DatasetList();
                commonChild.add(new DataMap());
                common.add(commonChild);
            }
            this.setProvCustMagsfk(common);
            this.setCustInfosfk(common);
            this.setDealInfosfk(common);
            this.setProdInfoModifysfk(common);
            this.setProdTradesfk(prodTradeDataset);

        }

        this.setInfos(memchDataset);

        setInfo(getData());

    }

    /**
     * @Description: 如果是查看看工单反馈,查询出工单反馈表
     * @author jch
     * @date
     * @param
     * @param
     * @param
     * @return
     * @throws Exception
     */
    public void queryProvCprtFk(IData data) throws Exception
    {

        // String sync_sequence = data.getString("SYNC_SEQUENCE");

        IDataset poTrade = CSViewCall.call(this, "CS.TradeOtherInfoQrySVC.queryPoTradeState", data);

        if (null == poTrade || poTrade.isEmpty())
        {
            CSViewException.apperr(TradeException.CRM_TRADE_91);
        }

        String next_sync_sequence = poTrade.getData(0).getString("RSRV_STR1"); // 前一个节点的序列号

        if (next_sync_sequence == null || StringUtils.equals("", next_sync_sequence))
        {
            CSViewException.apperr(TradeException.CRM_TRADE_91);
        }
        data.put("SYNC_SEQUENCE", next_sync_sequence);

        // 读取商品当前信息 ? 0-1
        data.put("INFO_TAG", "O");
        IDataset poTradeStateAttrs = CSViewCall.call(this, "CS.TradeOtherInfoQrySVC.queryPoTradeStateAttr", data);

        IData respData = new DataMap(); // 反馈信息只考虑一个，以后要考虑多个，这里要改

        if (poTradeStateAttrs == null || poTradeStateAttrs.size() == 0) // 说明没有当前状态负责人
        {

        }
        else
        {
            for (int poAttr = 0, size = poTradeStateAttrs.size(); poAttr < size; poAttr++)
            {
                IData temp = poTradeStateAttrs.getData(poAttr);
                if (temp.getString("ATTR_CODE").equals("NAME"))
                {
                    respData.put("Name", temp.getString("ATTR_VALUE"));
                    continue;
                }
                else if (temp.getString("ATTR_CODE").equals("PHONE"))
                {
                    respData.put("Phone", temp.getString("ATTR_VALUE"));
                    continue;
                }
                else if (temp.getString("ATTR_CODE").equals("DEPARTMENT"))
                {
                    respData.put("Department", temp.getString("ATTR_VALUE"));
                    continue;
                }
            }

        }

        // 查询出产品工单表，表示几笔工单

        IDataset prodTradeDataset = CSViewCall.call(this, "CS.TradeProductInfoQrySVC.queryProductTrade", data);

        IDataset provCustMag = new DatasetList();
        IDataset custInfo = new DatasetList();
        IDataset dealInfo = new DatasetList();
        IDataset prodInfoModify = new DatasetList();
        for (int prodTrade = 0, size = prodTradeDataset.size(); prodTrade < size; prodTrade++)
        {
            String order_number = prodTradeDataset.getData(prodTrade).getString("ORDER_NUMBER");

            IDataset provCustMagChild = new DatasetList();
            IDataset custInfoChild = new DatasetList();
            IDataset dealInfoChild = new DatasetList();
            IDataset prodInfoModifyChild = new DatasetList();

            // 读取产品客户经理信息 * 1-n ,这里一次，将几个类型的信息全部读出来，在应用服务器进行区分，就不分几次读取数据，减少IO
            // 由于查出来的是纵表，这里从新封一次

            data.put("INFO_TAG", "P");
            data.put("ORDERING_ID", order_number);
            IDataset prodTradeStateAttrs = CSViewCall.call(this, "CS.TradeOtherInfoQrySVC.queryPoTradeStateAttr", data);
            for (int attr = 0, sizes = prodTradeStateAttrs.size(); attr < sizes; attr++)
            {

                IData temp = prodTradeStateAttrs.getData(attr);

                if ("ProvCustMag".equals(temp.getString("INFO_TYPE")))// 产品对应的客户经理

                {
                    provCustMagChild.add(temp);
                    continue;
                }
                else if ("CustInfo".equals(prodTradeStateAttrs.getData(attr).getString("INFO_TYPE"))) // 产品受理涉及的客户信息

                {
                    custInfoChild.add(temp);
                    continue;
                }
                else if ("DealInfo".equals(prodTradeStateAttrs.getData(attr).getString("INFO_TYPE"))) // 产品落实情况

                {
                    dealInfoChild.add(temp);
                    continue;
                }
                else if ("ProdInfoModify".equals(temp.getString("INFO_TYPE"))) // 产品信息修正

                {
                    prodInfoModifyChild.add(temp);
                    continue;
                }

            }

            provCustMag.add(provCustMagChild);
            custInfo.add(custInfoChild);
            dealInfo.add(dealInfoChild);
            prodInfoModify.add(prodInfoModifyChild);

        }

        this.setRespPersonfk(respData);
        this.setProvCustMagsfk(this.changIDataset(provCustMag, "ProvCustMag"));
        this.setCustInfosfk(this.changIDataset(custInfo, "CustInfo"));
        this.setDealInfosfk(this.changIDataset(dealInfo, "DealInfo"));
        this.setProdInfoModifysfk(this.changIDataset(prodInfoModify, "ProdInfoModify"));
        this.setProdTradesfk(prodTradeDataset);

    }

    /**
     * 发送和保存数据到CBoss
     * 
     * @param cycle
     * @throws Exception
     */
    public void sendDataToCboss(IRequestCycle cycle) throws Exception
    {
        IData sendIBOSSParamInfo = new DataMap();

        IData urldata = getData(); // 取链接url过来的参数信息
        String url_param = urldata.getString("URL_PARM");
        IData commData = new DataMap();
        IData response_DataMap = new DataMap(url_param);// 转换为map
        IDataset productBase = response_DataMap.getDataset("PRODUCT_BASE");// 产品对应的基本信息
        String merch_order_id = response_DataMap.getString("MERCH_ORDER_ID");
        String sync_sequence = response_DataMap.getString("SYNC_SEQUENCE");

        commData.put("POORDER_NUMBER", merch_order_id);
        IDataset resp_Persion = response_DataMap.getDataset("RESP_PERSION");
        IDataset productProvCust = response_DataMap.getDataset("PRODUCT_PROV_CUST");
        IDataset custInfos = response_DataMap.getDataset("CUST_INFO");
        IDataset dealInfos = response_DataMap.getDataset("DEAL_INFO");
        IDataset prodInfoModifys = response_DataMap.getDataset("PROD_INFO_MODIFY");
        commData.put("RESP_PERSION", resp_Persion);
        commData.put("PRODUCT_PROV_CUST", productProvCust);
        commData.put("CUST_INFO", custInfos);
        commData.put("DEAL_INFO", dealInfos);
        commData.put("PROD_INFO_MODIFY", prodInfoModifys);
        commData.put("PRODUCT_BASE", productBase);
        commData.put("STATE_TYPE", response_DataMap.getString("STATE_TYPE"));
        // 1.22加的
        commData.put("CUSTOMER_NUMBER", response_DataMap.getString("MP_EC_CODE"));
        commData.put("PO_SPEC_NUMBER", response_DataMap.getString("PFK_MERCH_SPEC_CODE"));

        // 状态同步时间

        commData.put("SYN_TIME", SysDateMgr.getSysDate());

        IData data = spellProvCprtAns(commData);
        sendIBOSSParamInfo.put("IBOSS_INFO", data);

        // 将发送数据，保存在工单表中
        IData seqData = new DataMap();
        seqData.put(Route.USER_EPARCHY_CODE, getVisit().getStaffEparchyCode());
        String SeqId = CSViewCall.call(this, "CS.SeqMgrSVC.getSYnTradeIdForGrp", seqData).getData(0).getString("seq_id");// 获取序列

        IData poTradeState = new DataMap();

        // poTradeState.put("SYNC_SEQUENCE", sync_sequence);// 原来需反馈的序列号
        poTradeState.put("IF_ANS", "1");
        poTradeState.put("NEXT_SYNC_SEQUENCE", SeqId);
        CSViewCall.call(this, "CS.GrpProvCprtDAOSVC.updatePoTradeState", poTradeState);

        poTradeState.put("SYNC_SEQUENCE", SeqId); // 同步序列
        poTradeState.put("PO_ORDER_ID", merch_order_id);// 商品订单号

        poTradeState.put("EC_NAME", response_DataMap.getString("PFK_CUST_NAME"));// EC客户名称
        poTradeState.put("EC_SERIAL_NUMBER", response_DataMap.getString("MP_EC_CODE"));
        poTradeState.put("MERCH_SPEC_CODE", response_DataMap.getString("PFK_MERCH_SPEC_CODE"));
        poTradeState.put("SYNC_STATE", response_DataMap.getString("StateType"));
        // poTradeState.put("SYN_TIME", sysdate);
        poTradeState.put("SEND", "BOSS");
        poTradeState.put("RECEIVE", "BBSS");
        poTradeState.put("IF_PROVCPRT", "0");// 是否协助工单
        poTradeState.put("PROVCPRT_TYPE", "");
        poTradeState.put("IF_ANS", "1");// 是否已反馈

        poTradeState.put("TRADE_ID", response_DataMap.getString("TRADE_ID", ""));
        poTradeState.put("REMARK", "省BOSS工单反馈");
        poTradeState.put("RSRV_STR1", sync_sequence);// 保存上一次下发工单的序列号，起到关联作用
        sendIBOSSParamInfo.put("PO_TRADE_STATE_INFO", poTradeState);

        // 更改原工单是否反馈，改为反馈状态

        // 跨省工单状态产品表 TF_B_PRODUCTTRADE
        IDataset productTreadeInfoList = new DatasetList();
        for (int i = 0, size = productBase.size(); i < size; i++)
        {
            IData tempProdBase = productBase.getData(i);
            IData productOrder = new DataMap();
            productOrder.put("SYNC_SEQUENCE", SeqId);
            productOrder.put("ORDER_NUMBER", tempProdBase.getString("PRODUCT_ID"));
            productOrder.put("CIRCUIT_CODE", tempProdBase.getString("CIRCUIT_CODE"));
            productOrder.put("RESULT_CODE", tempProdBase.getString("RESULT"));
            productOrder.put("RESULT_INFO", tempProdBase.getString("REASON"));
            productOrder.put("CONTACT", "");
            productOrder.put("CONTACT_PHONE", "");
            productOrder.put("PLAN_COMPTIME", "");
            productTreadeInfoList.add(productOrder);
        }
        sendIBOSSParamInfo.put("PRODUCT_TRADE_INFO", productTreadeInfoList);

        // 跨省工单状态信息表 TF_B_POTRADE_STATE_ATTR
        // 1;先处理商品 1.22改了，可能有多条记录

        IDataset poTradeAttrPram = new DatasetList();

        if (null != resp_Persion)
        {
            for (int i = 0, size = resp_Persion.size(); i < size; i++)
            {
                IData temp = new DataMap();
                IData tempPersion = resp_Persion.getData(i);

                temp.put("SYNC_SEQUENCE", SeqId);
                temp.put("INFO_TAG", "O"); // O商品 P产品
                temp.put("INFO_TYPE", "RespPerson");
                temp.put("SUB_INFO_TYPE", "0");
                temp.put("ORDERING_ID", "");
                temp.put("ATTR_NAME", "姓名");
                temp.put("ATTR_CODE", "NAME");
                temp.put("ATTR_VALUE", tempPersion.getString("NAME"));
                poTradeAttrPram.add(temp);

                temp = new DataMap();

                temp.put("SYNC_SEQUENCE", SeqId);
                temp.put("INFO_TAG", "O"); // O商品 P产品
                temp.put("INFO_TYPE", "RespPerson");
                temp.put("SUB_INFO_TYPE", "0");
                temp.put("ORDERING_ID", "");
                temp.put("ATTR_NAME", "电话");
                temp.put("ATTR_CODE", "PHONE");
                temp.put("ATTR_VALUE", tempPersion.getString("PHONE"));
                poTradeAttrPram.add(temp);

                temp = new DataMap();

                temp.put("SYNC_SEQUENCE", SeqId);
                temp.put("INFO_TAG", "O"); // O商品 P产品
                temp.put("INFO_TYPE", "RespPerson");
                temp.put("SUB_INFO_TYPE", "0");
                temp.put("ORDERING_ID", "");
                temp.put("ATTR_NAME", "所在单位及部门");
                temp.put("ATTR_CODE", "DEPARTMENT");
                temp.put("ATTR_VALUE", tempPersion.getString("DEPARTMENT"));
                poTradeAttrPram.add(temp);
            }
        }

        // 处理产品客户经理属性

        for (int i = 0, size = productProvCust.size(); i < size; i++)
        {
            IData tempProdProv = productProvCust.getData(i);

            IData magTemp = new DataMap();

            magTemp.put("SYNC_SEQUENCE", SeqId);
            magTemp.put("INFO_TAG", "P"); // O商品 P产品
            magTemp.put("INFO_TYPE", "ProvCustMag");
            magTemp.put("SUB_INFO_TYPE", i);
            magTemp.put("ORDERING_ID", tempProdProv.getString("PRODUCT_ORDER_ID"));
            magTemp.put("ATTR_NAME", "姓名");
            magTemp.put("ATTR_CODE", "MAG_NAME");
            magTemp.put("ATTR_VALUE", tempProdProv.getString("MAG_NAME"));

            poTradeAttrPram.add(magTemp);
            magTemp = new DataMap();
            magTemp.put("SYNC_SEQUENCE", SeqId);
            magTemp.put("INFO_TAG", "P"); // O商品 P产品
            magTemp.put("INFO_TYPE", "ProvCustMag");
            magTemp.put("SUB_INFO_TYPE", i);
            magTemp.put("ORDERING_ID", tempProdProv.getString("PRODUCT_ORDER_ID"));
            magTemp.put("ATTR_NAME", "电话");
            magTemp.put("ATTR_CODE", "MAG_PHONE");
            magTemp.put("ATTR_VALUE", tempProdProv.getString("MAG_PHONE"));
            poTradeAttrPram.add(magTemp);

            magTemp = new DataMap();
            magTemp.put("SYNC_SEQUENCE", SeqId);
            magTemp.put("INFO_TAG", "P"); // O商品 P产品
            magTemp.put("INFO_TYPE", "ProvCustMag");
            magTemp.put("SUB_INFO_TYPE", i);
            magTemp.put("ORDERING_ID", tempProdProv.getString("PRODUCT_ORDER_ID"));
            magTemp.put("ATTR_NAME", "客户经理类型");
            magTemp.put("ATTR_CODE", "MAG_TYPE");
            magTemp.put("ATTR_VALUE", tempProdProv.getString("MAG_TYPE"));
            poTradeAttrPram.add(magTemp);
        }

        // 处理产品受理涉及的客户信息

        for (int i = 0, size = custInfos.size(); i < size; i++)
        {
            IData tempDealInfo = custInfos.getData(i);
            IData magTemp = new DataMap();

            magTemp.put("SYNC_SEQUENCE", SeqId);
            magTemp.put("INFO_TAG", "P"); // O商品 P产品
            magTemp.put("INFO_TYPE", "CustInfo");
            magTemp.put("SUB_INFO_TYPE", i);
            magTemp.put("ORDERING_ID", tempDealInfo.getString("PRODUCT_ORDER_ID"));
            magTemp.put("ATTR_NAME", "客户类型");
            magTemp.put("ATTR_CODE", "CUST_TYPE");
            magTemp.put("ATTR_VALUE", tempDealInfo.getString("CUSTTYPE"));
            poTradeAttrPram.add(magTemp);

            magTemp = new DataMap();
            magTemp.put("SYNC_SEQUENCE", SeqId);
            magTemp.put("INFO_TAG", "P"); // O商品 P产品
            magTemp.put("INFO_TYPE", "CustInfo");
            magTemp.put("SUB_INFO_TYPE", i);
            magTemp.put("ORDERING_ID", tempDealInfo.getString("PRODUCT_ORDER_ID"));
            magTemp.put("ATTR_NAME", "客户编码");
            magTemp.put("ATTR_CODE", "CUST_CODE");
            magTemp.put("ATTR_VALUE", tempDealInfo.getString("CUSTCODE"));
            poTradeAttrPram.add(magTemp);

            magTemp = new DataMap();
            magTemp.put("SYNC_SEQUENCE", SeqId);
            magTemp.put("INFO_TAG", "P"); // O商品 P产品
            magTemp.put("INFO_TYPE", "CustInfo");
            magTemp.put("SUB_INFO_TYPE", i);
            magTemp.put("ORDERING_ID", tempDealInfo.getString("PRODUCT_ORDER_ID"));
            magTemp.put("ATTR_NAME", "客户名称");
            magTemp.put("ATTR_CODE", "CUST_NAME");
            magTemp.put("ATTR_VALUE", tempDealInfo.getString("CUSTNAME"));
            poTradeAttrPram.add(magTemp);
        }

        // 处理产品落实情况

        for (int i = 0, size = dealInfos.size(); i < size; i++)
        {
            IDataset dealInfosChild = (DatasetList) dealInfos.get(i);
            for (int j = 0, sizes = dealInfosChild.size(); j < sizes; j++)
            {
                IData tempDealInfo = dealInfosChild.getData(j);

                IData magTemp = new DataMap();
                magTemp.put("SYNC_SEQUENCE", SeqId);
                magTemp.put("INFO_TAG", "P"); // O商品 P产品
                magTemp.put("INFO_TYPE", "DealInfo");
                magTemp.put("SUB_INFO_TYPE", j);
                magTemp.put("ORDERING_ID", tempDealInfo.getString("PRODUCT_ORDER_ID"));
                magTemp.put("ATTR_NAME", "产品落实侧");
                magTemp.put("ATTR_CODE", "DEAL_SIDE");
                magTemp.put("ATTR_VALUE", tempDealInfo.getString("DEALSIDE"));

                poTradeAttrPram.add(magTemp);
                magTemp = new DataMap();
                magTemp.put("SYNC_SEQUENCE", SeqId);
                magTemp.put("INFO_TAG", "P"); // O商品 P产品
                magTemp.put("INFO_TYPE", "DealInfo");
                magTemp.put("SUB_INFO_TYPE", j);
                magTemp.put("ORDERING_ID", tempDealInfo.getString("PRODUCT_ORDER_ID"));
                magTemp.put("ATTR_NAME", "当前状态处理说明");
                magTemp.put("ATTR_CODE", "DEAL_DETAIL");
                magTemp.put("ATTR_VALUE", tempDealInfo.getString("DEALDATAIL"));

                poTradeAttrPram.add(magTemp);

                magTemp = new DataMap();
                magTemp.put("SYNC_SEQUENCE", SeqId);
                magTemp.put("INFO_TAG", "P"); // O商品 P产品
                magTemp.put("INFO_TYPE", "DealInfo");
                magTemp.put("SUB_INFO_TYPE", j);
                magTemp.put("ORDERING_ID", tempDealInfo.getString("PRODUCT_ORDER_ID"));
                magTemp.put("ATTR_NAME", "当前状态涉及附件");
                magTemp.put("ATTR_CODE", "ATTACH_MENT");
                magTemp.put("ATTR_VALUE", tempDealInfo.getString("ATTACHMENT"));

                poTradeAttrPram.add(magTemp);
            }

        }

        // 处理产品信息修正

        for (int i = 0, size = prodInfoModifys.size(); i < size; i++)
        {
            IData tempProdInfo = prodInfoModifys.getData(i);

            IData magTemp = new DataMap();
            magTemp.put("SYNC_SEQUENCE", SeqId);
            magTemp.put("INFO_TAG", "P"); // O商品 P产品
            magTemp.put("INFO_TYPE", "ProdInfoModify");
            magTemp.put("SUB_INFO_TYPE", i);
            magTemp.put("ORDERING_ID", tempProdInfo.getString("PRODUCT_ORDER_ID"));
            magTemp.put("ATTR_NAME", "修正的产品信息类别");
            magTemp.put("ATTR_CODE", "MODI_TYPE");
            magTemp.put("ATTR_VALUE", tempProdInfo.getString("MODI_TYPE"));
            poTradeAttrPram.add(magTemp);

            magTemp = new DataMap();
            magTemp.put("SYNC_SEQUENCE", SeqId);
            magTemp.put("INFO_TAG", "P"); // O商品 P产品
            magTemp.put("INFO_TYPE", "ProdInfoModify");
            magTemp.put("SUB_INFO_TYPE", i);
            magTemp.put("ORDERING_ID", tempProdInfo.getString("PRODUCT_ORDER_ID"));
            magTemp.put("ATTR_NAME", "修正后的值");
            magTemp.put("ATTR_CODE", "MODI_VALUE");
            magTemp.put("ATTR_VALUE", tempProdInfo.getString("MODI_VALUE"));
            poTradeAttrPram.add(magTemp);
        }

        sendIBOSSParamInfo.put("PO_TRADE_STATE_ATTR_INFO_LIST", poTradeAttrPram);
        // 端到端esop数据
        String eosString = response_DataMap.getString("EOS");
        if (StringUtils.isNotEmpty(eosString))
        {

            IData eosData = new DataMap(eosString);
            String ibsysid = eosData.getString("IBSYSID", "");

            String nodeId = eosData.getString("NODE_ID", "");
            String bpmTempletId = eosData.getString("BPM_TEMPLET_ID", "");
            String mainTempletId = eosData.getString("MAIN_TEMPLET_ID", "");
            String workID = eosData.getString("WORK_ID", "");
            IData param = new DataMap();
            param.put("IBSYSID", ibsysid);
            param.put("NODE_ID", nodeId);
            param.put("TRADE_ID", response_DataMap.getString("TRADE_ID", ""));
            param.put("BPM_TEMPLET_ID", bpmTempletId);
            param.put("MAIN_TEMPLET_ID", mainTempletId);

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
            param.put("PROCESS_TIME", SysDateMgr.getFirstDayOfThisMonth4WEB()); // 处理时间
            param.put("ACCEPT_DATE", SysDateMgr.getFirstDayOfThisMonth4WEB()); // 受理时间
            param.put("TRADE_EPARCHY_CODE", getTradeEparchyCode()); // 受理地州
            param.put("UPDATE_STAFF_ID", getVisit().getStaffId()); // 受理员工
            param.put("UPDATE_DEPART_ID", getVisit().getDepartId()); // 受理部门
            param.put("TRADE_CITY_CODE", getVisit().getCityCode());
            param.put("WORK_ID", workID); // BPM工作标识,界面提交时传其它不传

            param.put("X_RESULTINFO", "TradeOk");
            param.put("X_RESULTCODE", "0");

            sendIBOSSParamInfo.put("EOS", param);

        }

        CSViewCall.call(this, "CS.SynBbossOrderStateSVC.rspDealOrderResult", sendIBOSSParamInfo);

    }

    public abstract void setAttrInfos(IDataset infos);// 设置产品属性值

    public abstract void setCondition(IData condition);

    public abstract void setContactorInfos(IDataset contactorInfos); // 订购的联系人信息

    public abstract void setCustInfos(IDataset custInfos); // 产品受理涉及的客户信息

    public abstract void setCustInfosfk(IDataset custInfosfk); // 设置反馈产品受理涉及的客户信息

    public abstract void setDealInfos(IDataset dealInfos); // 产品落实情况

    public abstract void setDealInfosfk(IDataset dealInfosfk); // 设置反馈产品落实情况

    public abstract void setDiscnts(IDataset discnts);// 台帐资费信息

    public abstract void setGroupInfo(IData groupInfo); // 放集团信息

    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);

    public abstract void setMerchInfo(IData info); // 放商品信息

    public abstract void setMerchpInfos(IDataset infos); // 放产品信息

    public abstract void setPoAttraMent(IDataset poAttraMent); // 合同信息

    public abstract void setPoAudit(IDataset poAudit); // 业务审批信息

    public abstract void setPoTradeInfo(IData poTradeInfo); // 放商品跨省工单信息

    public abstract void setProdInfoModifys(IDataset prodInfoModifys); // 产品信息修正

    public abstract void setProdInfoModifysfk(IDataset prodInfoModifysfk); // 设置反馈产品信息修正

    public abstract void setProdTrades(IDataset prodTrades); // 产品工单台帐

    public abstract void setProdTradesfk(IDataset prodTradesfk); // 设置反馈产品工单台帐

    public abstract void setProductTradeInfos(IDataset productTradeInfos); // 放产品工单信息

    public abstract void setProvCustMags(IDataset provCustMags); // 产品对应的客户经理

    public abstract void setProvCustMagsfk(IDataset provCustMagsfk);// 设置反馈产品对应的客户经理

    public abstract void setRespPersonfk(IData respPersonfk);// 设置反馈当前状态负责人信息

    public abstract void setRspPersons(IDataset rspPersons); // 放当前状态负责人信息 列表

    public abstract void setSyncStats(IDataset syncStats); // 同步状态

    /**
     * @Description: 发送集团BBOSS规范1.2 接口，工单流转状态同步接口 业务编码: BIP4B260 BBOSS向省BOSS进行工单流转状态同步 交易编码：T4011060
     *               省BOSS向BBOSS进行工单流转状态同步 交易编码：T4011060
     * @author weixb3
     * @date 2013/10/10
     * @throws Exception
     */
    private IData spellProvCprtAns(IData commData) throws Exception
    {

        IData inparam = new DataMap();
        inparam.put("TAG_CODE", "PUB_INF_PROVINCE");
        inparam.put("KEY", "TAG_INFO");
        inparam.put("DEFAULT_VALUE", "");
        inparam.put("EPARCHY_CODE", getTradeEparchyCode());
        // 根据与 CBOSS接口规范 定义通用数据接口
        IData map = new DataMap();
        // 头
        map.put("PROVINCE_CODE", CSViewCall.call(this, "CS.TagInfoQrySVC.getSysTagInfo", inparam).getData(0).getString("RESULT"));// 省别编码
        map.put("IN_MODE_CODE", "0");// 接入方式
        map.put("TRADE_EPARCHY_CODE", getTradeEparchyCode());// 交易地州编码
        map.put("TRADE_CITY_CODE", getVisit().getCityCode());// 交易城市代码
        map.put("TRADE_DEPART_ID", getVisit().getDepartId());// 员工部门编码
        map.put("TRADE_STAFF_ID", getVisit().getStaffId());// 员工城市编码
        map.put("TRADE_DEPART_PASSWD", ""); // 渠道接入密码此密码由BOSS制定，接入渠道必填
        map.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());// 路由地州编码
        map.put("ROUTETYPE", "00");// 路由类型 00-省代码，01-手机号
        map.put("ROUTEVALUE", "000");// 路由值
        map.put("KIND_ID", "BIP4B260_T4011060_0_0"); // 交易唯一标识

        map.put("POORDER_NUMBER", commData.getString("POORDER_NUMBER")); // BBOSS,商品订单号
        map.put("STATE_TYPE", commData.getString("STATE_TYPE"));// 同步的状态节点
        // 1.22加的
        map.put("CUSTOMER_NUMBER", commData.getString("CUSTOMER_NUMBER"));
        map.put("PO_SPEC_NUMBER", commData.getString("PO_SPEC_NUMBER"));

        IDataset respMapdaDataset = commData.getDataset("RESP_PERSION");

        if (IDataUtil.isNotEmpty(respMapdaDataset))
        {
            IDataset name = new DatasetList();
            IDataset phone = new DatasetList();
            IDataset department = new DatasetList();

            for (int i = 0, sizeI = respMapdaDataset.size(); i < sizeI; i++)
            {
                IData respMap = respMapdaDataset.getData(i);
                name.add(respMap.getString("NAME"));
                phone.add(respMap.getString("PHONE"));
                department.add(respMap.getString("DEPARTMENT"));
            }

            map.put("NAME", name);
            map.put("PHONE", phone);
            map.put("DEPARTMENT", department);
        }

        IDataset prodBase = commData.getDataset("PRODUCT_BASE");

        if (IDataUtil.isNotEmpty(prodBase))
        {
            IDataset pkgProductOrderId = new DatasetList();
            IDataset pkgCircuitCode = new DatasetList();
            IDataset pkgResultCode = new DatasetList();
            IDataset pfkResultInfo = new DatasetList();

            for (int i = 0, size = prodBase.size(); i < size; i++)
            {
                pkgProductOrderId.add(prodBase.getData(i).getString("PRODUCT_ID"));
                pkgCircuitCode.add(prodBase.getData(i).getString("CIRCUIT_CODE"));
                pkgResultCode.add(prodBase.getData(i).getString("RESULT"));
                pfkResultInfo.add(prodBase.getData(i).getString("REASON"));
            }

            map.put("PRODUCT_ID", pkgProductOrderId);
            map.put("CIRCUIT_CODE", pkgCircuitCode);
            map.put("RESULT", pkgResultCode);
            map.put("REASON", pfkResultInfo);
        }

        // 产品对应的客户经理
        IDataset productProdvCust = commData.getDataset("PRODUCT_PROV_CUST");

        if (IDataUtil.isNotEmpty(productProdvCust))
        {
            IDataset pkgMagType = new DatasetList();
            IDataset pkgMagName = new DatasetList();
            IDataset pkgMagPhone = new DatasetList();

            for (int i = 0, size = productProdvCust.size(); i < size; i++)
            {
                IData temp = productProdvCust.getData(i);

                IDataset pkgMagTypeChild = new DatasetList();
                IDataset pkgMagNameChild = new DatasetList();
                IDataset pkgMagPhoneChild = new DatasetList();

                pkgMagNameChild.add(temp.getString("MAG_NAME"));
                pkgMagPhoneChild.add(temp.getString("MAG_PHONE"));
                pkgMagTypeChild.add(temp.getString("MAG_TYPE"));

                pkgMagType.add(pkgMagTypeChild);
                pkgMagName.add(pkgMagNameChild);
                pkgMagPhone.add(pkgMagPhoneChild);

            }

            map.put("MAG_TYPE", pkgMagType);
            map.put("MAG_NAME", pkgMagName);
            map.put("MAG_PHONE", pkgMagPhone);
        }

        // 产品受理涉及的客户信息
        IDataset custInfos = commData.getDataset("CUST_INFO");

        if (IDataUtil.isNotEmpty(custInfos))
        {
            IDataset pkgCustType = new DatasetList();
            IDataset pkgCustCode = new DatasetList();
            IDataset pkgCustName = new DatasetList();

            for (int i = 0, size = custInfos.size(); i < size; i++)
            {
                IDataset custInfosChild = custInfos.getDataset(i);
                IDataset pkgCustTypeChild = new DatasetList();
                IDataset pkgCustCodeChild = new DatasetList();
                IDataset pkgCustNameChild = new DatasetList();
                for (int j = 0, size2 = custInfosChild.size(); j < size2; j++)
                {
                    pkgCustTypeChild.add(custInfosChild.getData(j).getString("CUST_TYPE"));
                    pkgCustCodeChild.add(custInfosChild.getData(j).getString("CUST_CODE"));
                    pkgCustNameChild.add(custInfosChild.getData(j).getString("CUST_NAME"));
                }
                pkgCustType.add(pkgCustTypeChild);
                pkgCustCode.add(pkgCustCodeChild);
                pkgCustName.add(pkgCustNameChild);

            }

            map.put("CUST_TYPE", pkgCustType);
            map.put("CUST_CODE", pkgCustCode);
            map.put("CUST_NAME", pkgCustName);
        }

        // 反馈产品受理的落实情况
        IDataset dealInfos = commData.getDataset("DEAL_INFO");

        if (IDataUtil.isNotEmpty(dealInfos))
        {
            IDataset pkgDealSide = new DatasetList();
            IDataset pkgDealDetail = new DatasetList();
            IDataset pkgAttachment = new DatasetList();

            for (int i = 0, size = dealInfos.size(); i < size; i++)
            {
                IDataset dealInfosChild = dealInfos.getDataset(i);

                IDataset pkgDealSideChild = new DatasetList();
                IDataset pkgDealDetailChild = new DatasetList();
                IDataset pkgAttachmentChild = new DatasetList();
                for (int j = 0, size2 = dealInfosChild.size(); j < size2; j++)
                {
                    pkgDealSideChild.add(dealInfosChild.getData(j).getString("DEAL_SIDE"));
                    pkgDealDetailChild.add(dealInfosChild.getData(j).getString("DEAL_DETAIL"));
                    pkgAttachmentChild.add(dealInfosChild.getData(j).getString("ATTACHMENT"));
                }

                pkgDealSide.add(pkgDealSideChild);
                pkgDealDetail.add(pkgDealDetailChild);
                pkgAttachment.add(pkgAttachmentChild);
            }

            map.put("DEAL_SIDE", pkgDealSide);
            map.put("DEAL_DETAIL", pkgDealDetail);
            map.put("ATTACHMENT", pkgAttachment);
        }

        // 产品信息修
        IDataset prodInfoModifys = commData.getDataset("PROD_INFO_MODIFY");

        if (IDataUtil.isNotEmpty(prodInfoModifys))
        {
            IDataset pkgModiType = new DatasetList();
            IDataset pkgModiValue = new DatasetList();

            for (int i = 0, size = prodInfoModifys.size(); i < size; i++)
            {
                IData prodInfoModifysChild = prodInfoModifys.getData(i);
                IDataset pkgModiTypeChild = new DatasetList();
                IDataset pkgModiValueChild = new DatasetList();

                pkgModiTypeChild.add(prodInfoModifysChild.getString("MODI_TYPE"));//
                pkgModiValueChild.add(prodInfoModifysChild.getString("MODI_VALUE"));

                pkgModiType.add(pkgModiTypeChild);
                pkgModiValue.add(pkgModiValueChild);

            }

            map.put("MODI_TYPE", pkgModiType);
            map.put("MODI_VALUE", pkgModiValue);

        }

        map.put("SYN_TIME", commData.getString("SYN_TIME"));// 状态同步时间YYYYMMDDhhmiss
        String notes = commData.getString("NOTES");

        if (notes != null && !"".equals(notes))
            map.put("NOTES", notes);

        return map;

    }

    private IDataset valBaseInfo(IData urldata, String trade_id, String ec_code) throws Exception
    {
        if (StringUtils.isBlank(ec_code) || StringUtils.isEmpty(ec_code))
            CSViewException.apperr(ParamException.CRM_PARAM_164);

        // 1:读取客户信息
        IDataset groupCustDataset = CSViewCall.call(this, "CS.GrpInfoQrySVC.queryCustGroupInfoByMpCustCode", urldata);

        if (null == groupCustDataset || groupCustDataset.isEmpty())
        {
            CSViewException.apperr(GrpException.CRM_GRP_201, ec_code);
        }
        this.setGroupInfo(groupCustDataset.getData(0));

        // 2:商品信息
        IDataset memchDataset = CSViewCall.call(this, "CS.TradeGrpMerchInfoQrySVC.qryMerchInfoByTradeId", urldata);
        // 由于商品信息是只有1条地，因此不为空的话，取memchDatasetget(0) ,商品信息没有，肯定没产品信息，对于BBOSS产品
        if (memchDataset.isEmpty())
        {
            CSViewException.apperr(TradeException.CRM_TRADE_19, trade_id);
        }
        return memchDataset;
    }

}
