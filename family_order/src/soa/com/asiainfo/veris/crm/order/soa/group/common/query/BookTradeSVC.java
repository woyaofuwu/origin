
package com.asiainfo.veris.crm.order.soa.group.common.query;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UTradeTypeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UBrandInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UDepartInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UStaffInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.trade.UTradeHisInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.trade.UTradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.diversifyacct.DiversifyAcctUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQrySVC;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.trade.out.TradeCancelFee;

public class BookTradeSVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    public IDataset queryErrorInfoTrade(IData param) throws Exception
    {
        BookTradeQueryDAO bean = new BookTradeQueryDAO();
        return bean.queryErrorInfoTrade(param,getRouteId());
    }

    public IDataset queryUserCancelTrade(IData param) throws Exception
    {
        BookTradeQueryDAO bean = new BookTradeQueryDAO();
        param.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        return bean.queryUserCancelTrade(param,getRouteId());
    }

    public void cancelRecvFee(IData param) throws Exception
    {
        TradeCancelFee bean = new TradeCancelFee();
        IData mainTrade = new DataMap();
        mainTrade.put("TRADE_ID", param.get("TRADE_ID"));
        IData pgData = new DataMap();
        pgData.put("CANCEL_TYPE", "1");
        bean.cancelRecvFee(mainTrade, pgData);

    }

    public static IDataset queryTradeInfo(IData param) throws Exception
    {
        BookTradeQueryDAO bean = new BookTradeQueryDAO();
        param.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        return bean.queryTradeInfo(param);
    }

    public static IDataset queryTradeUserInfo(IData param) throws Exception
    {
        BookTradeQueryDAO bean = new BookTradeQueryDAO();
        param.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        return bean.queryTradeUserInfo(param);
    }

    public static IDataset queryGroupNetInfo(IData param) throws Exception
    {
        BookTradeQueryDAO bean = new BookTradeQueryDAO();
        param.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());

        return IDataUtil.idToIds(UTradeInfoQry.qryTradeByTradeId(param.getString("TRADE_ID"), param.getString("CANCEL_TAG")));
    }

    /**
     * 集团专网竣工
     */

    public IDataset updateTradeDiscnt(IData param) throws Exception
    {
        BookTradeQueryDAO bean = new BookTradeQueryDAO();
        param.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        IDataset dataset = new DatasetList();
        IData data = new DataMap();
        int result = bean.updateTradeDiscnt(param);
        data.put("RESULT", result);
        dataset.add(data);
        return dataset;
    }

    public IDataset upateTradeSrv(IData param) throws Exception
    {
        BookTradeQueryDAO bean = new BookTradeQueryDAO();
        param.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        IDataset dataset = new DatasetList();
        IData data = new DataMap();
        int result = bean.upateTradeSrv(param);
        data.put("RESULT", result);
        dataset.add(data);
        return dataset;
    }

    public IDataset updateTrade(IData param) throws Exception
    {
        BookTradeQueryDAO bean = new BookTradeQueryDAO();
        param.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        IDataset dataset = new DatasetList();
        IData data = new DataMap();
        int result = bean.updateTrade(param);
        data.put("RESULT", result);
        dataset.add(data);
        return dataset;
    }

    /**
     * 账后代付
     */
    public IDataset queryGroupAccountInfo(IData param) throws Exception
    {
        BookTradeQueryDAO bean = new BookTradeQueryDAO();
        param.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        IDataset result = bean.queryGroupAccountInfo(param);
        if(IDataUtil.isNotEmpty(result))
        {
        	for(int i=0; i<result.size(); i++)
        	{
        		IData data = result.getData(i);
        		String productId= data.getString("PRODUCT_ID");
        		String productName = "";
        		if(StringUtils.isNotBlank(productId))
        		{
        			productName = UProductInfoQry.getProductNameByProductId(productId);
        		}
        		data.put("PRODUCT_NAME", productName);
        	}
        }
        return result;
    }

    public IDataset getRelationAAByActIdATag(IData param) throws Exception
    {
        BookTradeQueryDAO bean = new BookTradeQueryDAO();
        param.put("TRADE_EPARCHY_CODE", param.getString("EPARCHY_CODE_A"));
        return bean.getRelationAAByActIdATag(param);
    }

    public IDataset getNoteItemList(IData param) throws Exception
    {
        BookTradeQueryDAO bean = new BookTradeQueryDAO();
        param.put("TRADE_EPARCHY_CODE", param.getString("EPARCHY_CODE_A"));
        return bean.getNoteItemList(param);
    }

    public IDataset filterNoteItems(IData param) throws Exception
    {
        BookTradeQueryDAO bean = new BookTradeQueryDAO();
        param.put("TRADE_EPARCHY_CODE", param.getString("EPARCHY_CODE_A"));
        return bean.filterNoteItems(param);
    }

    public IDataset qryMebProductList(IData param) throws Exception
    {
        BookTradeQueryDAO bean = new BookTradeQueryDAO();
        param.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        return bean.qryMebProductList(param, this.getPagination());
    }

    public IDataset qryGrpSpecialPayInfo(IData param) throws Exception
    {
        BookTradeQueryDAO bean = new BookTradeQueryDAO();
        param.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        return bean.qryMebProductList(param, this.getPagination());
    }

    /**
     * 根据手机号码，获取用户受限表信息
     */
    public IDataset getAccountUniteBySN(IData param) throws Exception
    {
        BookTradeQueryDAO bean = new BookTradeQueryDAO();
        param.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        return bean.getAccountUniteBySN(param, this.getPagination());
    }

    /**
     * 根据集团名称，获取受限表信息
     */
    public IDataset getAccountUniteByCustName(IData param) throws Exception
    {
        BookTradeQueryDAO bean = new BookTradeQueryDAO();
        param.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        return bean.getAccountUniteByCustName(param);
    }

    /**
     * 根据ec接入号，获取用户受限表信息
     */
    public IDataset getAccountUniteByBank(IData param) throws Exception
    {
        BookTradeQueryDAO bean = new BookTradeQueryDAO();
        param.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        return bean.getAccountUniteByBank(param);
    }

    /**
     * 打印信息查询
     * 
     * @author liujy
     */
    public IDataset queryPrints(IData param) throws Exception
    {
        BookTradeQueryDAO bean = new BookTradeQueryDAO();
        String userId = "";
        String custId = "";
        IData paraUserInfo = new DataMap();
        paraUserInfo.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        paraUserInfo.put("SERIAL_NUMBER", param.getString("SERIAL_NUMBER"));
        paraUserInfo.put("REMOVE_TAG", "0");
        paraUserInfo.put("NET_TYPE_CODE", "00");
        IDataset userInfos = bean.getUserInfoBySn(paraUserInfo);

        if (null != userInfos && userInfos.size() > 0)
        {
            IData userInfo = (IData) userInfos.get(0);
            userId = userInfo.getString("USER_ID");
            custId = userInfo.getString("CUST_ID");
        }
        if (StringUtils.isBlank(userId) || StringUtils.isBlank(custId))
        {

            CSAppException.apperr(GrpException.CRM_GRP_674);
        }

        IData paraPrints = new DataMap();
        String startDate = param.getString("START_DATE");
        String endDate = param.getString("END_DATE");
        if (StringUtils.isNotBlank(startDate) && StringUtils.isNotBlank(endDate))
        {
            startDate = startDate + SysDateMgr.START_DATE_FOREVER;
            endDate = endDate + SysDateMgr.END_DATE;
        }

        paraPrints.put("SERIAL_NUMBER", param.getString("SERIAL_NUMBER"));
        paraPrints.put("TRADE_STAFF_ID", param.getString("STAFF_ID"));
        paraPrints.put("START_DATE", startDate);
        paraPrints.put("END_DATE", endDate);
        paraPrints.put("CANCEL_TAG", "0");

        IDataset printsInfos = bean.queryPrints(paraPrints, this.getPagination());

        IDataset returnValues = new DatasetList();
        IData printsInfo = new DataMap();
        if (printsInfos.size() > 0)
        {
            for (int i = 0; i < printsInfos.size(); i++)
            {
                printsInfo = (IData) printsInfos.getData(i);
                String checkName = printsInfo.getString("VIP_CLASS");

                if (checkName.equals("E"))
                {
                    checkName = "证件校验";

                }
                else if (checkName.equals("B"))
                {
                    checkName = "用户密码校验";
                }
                else
                {
                    checkName = "证件校验";
                }
                printsInfo.put("VIP_CLASS", checkName);
                returnValues.add(printsInfo);
            }
        }
        return returnValues;
    }

    public IDataset redirectToMsgBox(IData param) throws Exception
    {
        BookTradeQueryDAO bean = new BookTradeQueryDAO();
        IDataset printDataset = new DatasetList();
        IData returnData = new DataMap();
        IData querPrintPara = new DataMap();

        querPrintPara.put("TRADE_ID", param.getString("TRADE_ID"));
        querPrintPara.put("CANCEL_TAG", "0");
        IData infos = UTradeHisInfoQry.qryTradeHisByPk(param.getString("TRADE_ID"), "0", Route.getJourDb(Route.CONN_CRM_CG));

        if (IDataUtil.isEmpty(infos))
        {
            CSAppException.apperr(GrpException.CRM_GRP_828);
        }

        String printInfo = printReceiptForGrp(infos); // 打印信息处理

        returnData.put("NAME", "免填单");
        returnData.put("PRINT_DATA", printInfo);
        returnData.put("TYPE", "3");
        returnData.put("TRADE_ID", param.getString("TRADE_ID"));
        printDataset.add(returnData);

        return printDataset;
    }

    private String printReceiptForGrp(IData idTrade) throws Exception
    {
        IData idPrintData = new DataMap();
        IData idCnote = new DataMap();

        if (idTrade.isEmpty())
        {
            CSAppException.apperr(GrpException.CRM_GRP_830);
        }

        // 得到定单信息
        String tradeId = idTrade.getString("TRADE_ID");
        String accetpDate = idTrade.getString("ACCEPT_DATE");

        IData printout = createNoteInfo(idTrade);
        idPrintData.putAll(printout);
        idPrintData.put("TEMPLET_TYPE", "3");

        if (!idPrintData.containsKey("RECEIPT_INFO1"))
        {
            CSAppException.apperr(GrpException.CRM_GRP_831);
        }

        idCnote.put("TRADE_ID", tradeId);
        idCnote.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(tradeId));

        idCnote.put("NOTE_TYPE", idPrintData.getString("TEMPLET_TYPE"));

        idCnote.put("RECEIPT_INFO1", idPrintData.getString("RECEIPT_INFO1"));
        idCnote.put("RECEIPT_INFO2", idPrintData.getString("RECEIPT_INFO2"));
        idCnote.put("RECEIPT_INFO3", idPrintData.getString("RECEIPT_INFO3"));
        idCnote.put("RECEIPT_INFO4", idPrintData.getString("RECEIPT_INFO4"));
        idCnote.put("RECEIPT_INFO5", idPrintData.getString("RECEIPT_INFO5"));
        idCnote.put("NOTICE_CONTENT", "");
        idCnote.put("REMARK", "集团业务免填单补录");
        idCnote.put("UPDATE_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
        idCnote.put("ACCEPT_DATE", accetpDate);
        idCnote.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        idCnote.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        
        if (!Dao.insert("TF_B_TRADE_CNOTE_INFO", idCnote,Route.getJourDb(Route.CONN_CRM_CG)))
        {
            CSAppException.apperr(GrpException.CRM_GRP_827);
        }

        // 打印数据解析，返回带有位置和模板的打印字符串
        idPrintData.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        idPrintData.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        idPrintData.put("TRADE_EPARCHY_CODE", CSBizBean.getVisit().getStaffEparchyCode());

        // 打印
        // String printData = PrintEngine.parseData(pd, idPrintData);
        String printData = "";
        return printData;

    }

    public IData createNoteInfo(IData tradeInfo) throws Exception
    {
        BookTradeQueryDAO bean = new BookTradeQueryDAO();
        IData outData = new DataMap();

        StringBuilder RECEIPT_INFO1 = new StringBuilder();
        StringBuilder RECEIPT_INFO2 = new StringBuilder();
        StringBuilder RECEIPT_INFO3 = new StringBuilder();

        // 受理员工姓名
        String staffName = UStaffInfoQry.getStaffNameByStaffId(tradeInfo.getString("TRADE_STAFF_ID"));

        // 受理员工部门
        String departName = UDepartInfoQry.getDepartNameByDepartId(tradeInfo.getString("TRADE_DEPART_ID"));

        // 受理类型名称
        IData data = new DataMap();
        data.put("TRADE_TYPE_CODE", tradeInfo.getString("TRADE_TYPE_CODE"));
        data.put("EPARCHY_CODE", CSBizBean.getVisit().getStaffEparchyCode());
        String tradeTypeName = getCodeName(data);

        // 产品名称
        String productName = UProductInfoQry.getProductNameByProductId(tradeInfo.getString("PRODUCT_ID"));

        // 品牌名称
        String brandName = UBrandInfoQry.getBrandNameByBrandCode(tradeInfo.getString("BRAND_CODE"));

        // 业务受理方式
        String idenChk = tradeInfo.getString("PROCESS_TAG_SET").substring(19, 20);

        if (idenChk.equals("E"))
        {
            idenChk = "身份证校验";
        }
        else if (idenChk.equals("B"))
        {
            idenChk = "密码校验";
        }
        else
        {
            idenChk = "身份证校验";
        }
        // 根据CUST_ID_B获取集团客户名称
        String groupCustName = "";
        if (!tradeInfo.getString("CUST_ID_B", "-1").equals("-1"))
        {
            IData custInfos = UcaInfoQry.qryCustomerInfoByCustIdForGrp(tradeInfo.getString("CUST_ID_B"));
            if (custInfos != null && custInfos.size() > 0)
            {
                groupCustName = custInfos.getString("CUST_NAME");
            }
        }

        if (idenChk == null || idenChk.equals(""))
        {
            idenChk = "无";
        }

        String tradeid = tradeInfo.getString("TRADE_ID", "");
        IData param = new DataMap();
        param.put("TRADE_ID", tradeid);
        param.put("MODIFY_TAG", "0");
        String disnames = "";
        IDataset discinfos = bean.qryTradeDiscntInfos(param);
        int max = -1;
        String maxDate = "1900-01-01";
        if (discinfos != null && discinfos.size() > 0)
        {
            for (int i = 0; i < discinfos.size(); i++)
            {
                String discode = discinfos.getData(i).getString("DISCNT_CODE", "");
                if (!discode.equals("493"))
                {

                    if (discinfos.getData(i).getString("END_DATE", "").compareTo(maxDate) > 0)
                    {
                        maxDate = discinfos.getData(i).getString("END_DATE", "");
                        max = i;
                    }

                }
            }
            if (max != -1)
            {
                String discode1 = discinfos.getData(max).getString("DISCNT_CODE", "");
                String disname = UDiscntInfoQry.getDiscntNameByDiscntCode(discode1);

                if (disnames.equals(""))
                    disnames = disname;
                else
                    disnames = disnames + "," + disname;
            }
        }
        param.put("RES_TYPE_CODE", "S");
        String rescodes = "";
        IDataset resinfos = bean.qryTradeResInfosByType(param);
        if (resinfos != null && resinfos.size() > 0)
        {
            for (int i = 0; i < resinfos.size(); i++)
            {
                String rescode = resinfos.getData(i).getString("RES_CODE", "");

                if (rescodes.equals(""))
                    rescodes = rescode;
                else
                    rescodes = rescodes + "," + rescode;
            }
        }
        String tradetype = tradeInfo.getString("TRADE_TYPE_CODE", "");
        RECEIPT_INFO1.append("~~      业务类型：");
        RECEIPT_INFO1.append(tradeTypeName);
        RECEIPT_INFO1.append("      受理方式：");
        RECEIPT_INFO1.append(idenChk);
        // add by zhaoyi@非VPMN成员业务，修改显示字段
        if (!tradetype.equals("3034") && !tradetype.equals("3035") && !tradetype.equals("3037"))
        {
            RECEIPT_INFO1.append("~~      集团产品编码：");
            RECEIPT_INFO1.append(tradeInfo.getString("SERIAL_NUMBER_B"));
            RECEIPT_INFO1.append("~~      集团名称：");
            RECEIPT_INFO1.append(groupCustName);
        }
        else
        {
            RECEIPT_INFO1.append("~~      VPMN编码：");
            RECEIPT_INFO1.append(tradeInfo.getString("SERIAL_NUMBER_B"));
            RECEIPT_INFO1.append("~~      VPMN名称：");
            RECEIPT_INFO1.append(groupCustName);
        }

        if (tradetype.equals("3034") || tradetype.equals("3035"))
        {
            RECEIPT_INFO1.append("~~      短号码：");
            RECEIPT_INFO1.append(rescodes);// 根据情况是否打印短号码

            // 要加服务优惠的变更信息
            RECEIPT_INFO1.append("~~      新增优惠：");
            RECEIPT_INFO1.append(disnames);
            RECEIPT_INFO1.append("~~      温馨提示：");
            RECEIPT_INFO1.append("集团V网的管理权归属集团单位");
        }
        else if (tradetype.equals("3644"))
        {
            RECEIPT_INFO1.append("~~      新增优惠：");
            RECEIPT_INFO1.append(disnames);
        }

        outData.put("RECEIPT_INFO1", RECEIPT_INFO1);
        outData.put("BRAND_NAME", brandName);
        outData.put("STAFF_ID", tradeInfo.getString("TRADE_STAFF_ID"));
        outData.put("TRADE_ID", tradeInfo.getString("TRADE_ID"));
        outData.put("CUST_NAME", tradeInfo.getString("CUST_NAME"));
        outData.put("SERIAL_NUMBER", tradeInfo.getString("SERIAL_NUMBER"));
        outData.put("YEAR", tradeInfo.getString("ACCEPT_DATE").substring(0, 4));
        outData.put("MONTH", tradeInfo.getString("ACCEPT_DATE").substring(5, 7));
        outData.put("DAY", tradeInfo.getString("ACCEPT_DATE").substring(8, 10));

        outData.put("X_RESULTCODE", "0");
        outData.put("X_RESULTINFO", "TradeOK!");
        return outData;
    }

    public static String getCodeName(IData param) throws Exception
    {

        return UTradeTypeInfoQry.getTradeTypeName(param.getString("TRADE_TYPE_CODE"), param.getString("EPARCHY_CODE"));
    }

    public IDataset getAcctInfoByAcctId(IData param) throws Exception
    {

        return IDataUtil.idToIds(UcaInfoQry.qryAcctInfoByAcctId(param.getString("ACCT_ID")));

    }

    public IDataset qryRelationAAByAcctIdAAndB(IData param) throws Exception
    {
        BookTradeQueryDAO bean = new BookTradeQueryDAO();
        return bean.qryRelationAAByAcctIdAAndB(param);
    }

    public IDataset getRelationAAByActIdB(IData param) throws Exception
    {
        BookTradeQueryDAO bean = new BookTradeQueryDAO();
        return bean.getRelationAAByActIdB(param);
    }

    public static IDataset getDefaultPayRelationByUserID(IData inparams) throws Exception
    {
        return IDataUtil.idToIds(UcaInfoQry.qryDefaultPayRelaByUserId(inparams.getString("USER_ID")));
    }

    public static IDataset getLastDefaultPayRelationByUserID(IData inparams) throws Exception
    {
        BookTradeQueryDAO bean = new BookTradeQueryDAO();
        return bean.getLastDefaultPayRelationByUserID(inparams);
    }

    public static IDataset getJudeAcctDayTag(IData inparams) throws Exception
    {
        String result = DiversifyAcctUtil.getJudeAcctDayTag(inparams);
        IDataset dataset = new DatasetList();
        IData data = new DataMap();
        data.put("IFUSERACCTTAG", result);
        dataset.add(data);
        return dataset;
    }

    public static IDataset getUserAcctDay(IData inparams) throws Exception
    {
        IData result = DiversifyAcctUtil.getUserAcctDay(inparams.getString("USER_ID"));
        IDataset dataset = new DatasetList();
        dataset.add(result);
        return dataset;
    }

    public static IDataset checkUserAcctDayWithWarn(IData inparams) throws Exception
    {
        DiversifyAcctUtil.checkUserAcctDayWithWarn(inparams.getData("USERACCTDAY"), inparams.getString("SERIALNUMBER"), "1", false);
        return null;
    }

    public static IDataset qryPayItemCode(IData inparams) throws Exception
    {
        AcctCall acctCall = new AcctCall();
        IData data = acctCall.qryPayItemCode(inparams.getString("DETAIL_ITEMSET"));
        IDataset dataset = new DatasetList();
        dataset.add(data);
        return dataset;
    }

    public static IDataset getTradeTypeLimit(IData inparams) throws Exception
    {
        BookTradeQueryDAO bean = new BookTradeQueryDAO();
        return bean.getTradeTypeLimit(inparams);
    }

    public static IDataset getSpecPayByUserIdA(IData inparams) throws Exception
    {
        BookTradeQueryDAO bean = new BookTradeQueryDAO();
        return bean.getSpecPayByUserIdA(inparams);
    }

    /**
     * 查询优惠
     * 
     * @param inparams
     * @return
     * @throws Exception
     */
    public static IDataset getUserDiscntInfo(IData inparams) throws Exception
    {
        BookTradeQueryDAO bean = new BookTradeQueryDAO();
        return bean.getUserDiscntInfo(inparams);
    }

    public static IDataset getUserDiscntInfoByCode(IData inparams) throws Exception
    {
        return IDataUtil.idToIds(UDiscntInfoQry.getDiscntInfoByPk(inparams.getString("DISCNT_CODE")));
    }

    /**
     * //查询专线优惠信息
     * 
     * @param inparams
     * @return
     * @throws Exception
     */
    public static IDataset getPriceDataByProductId(IData inparams) throws Exception
    {
        BookTradeQueryDAO bean = new BookTradeQueryDAO();
        return bean.getPriceDataByProductId(inparams);
    }

    /***
     * ESOP查询专线信息
     * 
     * @param user
     * @return
     * @throws Exception
     */
    public static IDataset getDatalineInfoByUserId(IData user) throws Exception
    {
        BookTradeQueryDAO bean = new BookTradeQueryDAO();
        return bean.getDatalineInfoByUserId(user);
    }
    
    /***
     * ESOP变更勘查查询专线信息
     * 
     * @param user
     * @return
     * @throws Exception
     */
    public static IDataset queryChangeLineInfosForEsop(IData user) throws Exception
    {
        BookTradeQueryDAO bean = new BookTradeQueryDAO();
        return bean.queryChangeLineInfosForEsop(user);
    }
    
    public static void updateDatalineTradeByTradeId(IData trade) throws Exception{
        IData maiTrade = trade.getData("MAINTRADE");
        IData pfBackData = trade.getData("PFBACKDATA");
        
        updateDatalineTrade(maiTrade,pfBackData);
    }
    
    public static void  updateDatalineTrade(IData mainTrade,IData pfBackData) throws Exception{
        IData valueCode = new DataMap();
        IData userDataline = new DataMap();
        IData userData = new DataMap();
        IData userDataAttr = new DataMap();
        String tradeId = mainTrade.getString("TRADE_ID");
        //------modify by chenzg@20170915--begin--REQ201706010005ESOP1.5计费节点触发改造优化---------------
        /*1、办理、变更:VOIP专线（专网专线）,互联网专线接入（专网专线）,数据专线（专网专线）业务，服开回单将工单的执行时间改为2050
         *2、待ESOP点击协议报备完成后，再通过接口修改工单的执行时间为当前时间
         * */
        String tradeTypeCode = mainTrade.getString("TRADE_TYPE_CODE");
       /* if( "2990".equals(tradeTypeCode) || "2991".equals(tradeTypeCode) ||
        	"3080".equals(tradeTypeCode) || "3081".equals(tradeTypeCode) ||
        	"3010".equals(tradeTypeCode) || "3011".equals(tradeTypeCode) ){
        	//------modify by fufn@20180423--begin--REQ201802260030关于集客业务支撑流程式快速开通功能需求 ---------------
        	IDataset iDataset1=TradeOtherInfoQry.queryDatalineAttr(tradeId,"SHEETTYPE");
        	if(iDataset1.size()>0&&iDataset1.getData(0).getString("ATTR_VALUE", "").equals("34")){
    			System.out.println("updateDatalineTrade20180423:"+iDataset1.getData(0).getString("ATTR_VALUE", ""));
        	}
        	else{
        		BookTradeQueryDAO dao = new BookTradeQueryDAO();
            	dao.updateMainTradeExecTimeTo2050(tradeId);
        	}
        	//------modify by fufn@20180423--end--REQ201802260030关于集客业务支撑流程式快速开通功能需求 ---------------
        }*/
        //------modify by chenzg@20170915--end----REQ201706010005ESOP1.5计费节点触发改造优化---------------
        if(null != mainTrade && mainTrade.size() > 0 && null != pfBackData && pfBackData.size() > 0){
            
            for (int i = 0; i < pfBackData.size(); i++){
                String attr[] = pfBackData.getNames();
                valueCode.put(attr[i], pfBackData.getString(attr[i]));
            }
            
            IData param = new DataMap();
            param.put("SUBSYS_CODE", "CSM");
            param.put("PARAM_ATTR", "7012");
            param.put("PARAM_CODE", "7010");
            param.put("EPARCHY_CODE", "0898");
            IDataset staticData =  getPriceDataByProductId(param);
            
            if(null != staticData && staticData.size() > 0){
                for (int i = 0; i < staticData.size(); i++)
                {
                    IData dataAttr = staticData.getData(i);
                    String eomsCode = dataAttr.getString("PARA_CODE1");
                    String tableCode = dataAttr.getString("PARA_CODE2");
                    String attrCode = dataAttr.getString("PARA_CODE3");
                    
                    if(StringUtils.isNotBlank(valueCode.getString(eomsCode))){
                        userData.put(tableCode, valueCode.getString(eomsCode));
                        userDataAttr.put(attrCode, valueCode.getString(eomsCode));
                    }
                }
            }
            
            //修改资料数据
            IDataset trade = TradeOtherInfoQry.queryDatalineByTradeId(tradeId);
            if(null != trade && trade.size() > 0){
                userDataline = trade.getData(0);
                for (int i = 0; i < userData.size(); i++)
                {
                    String userAttr[] = userData.getNames();
                    userDataline.put(userAttr[i], userData.getString(userAttr[i]));
                }
                //不知道为什么J2EE的时候要把台帐的MODIFY_TAG改掉，导致原本是0新增，被改为了2，未能生成dataline资料。由于影响生成资料，在20161123将其注释掉，
                //userDataline.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                //userDataline.put("UPDATE_TIME",SysDateMgr.getSysDate());
                
                TradeOtherInfoQry.updateDatalineTrade(tradeId,userDataline);
            }
            
            
          //修改OTHER表数据
            IData otherParam = new DataMap();
            IDataset tradeOther  = TradeOtherInfoQry.queryUserOtherByTradeId(tradeId,"N001");
            if(null !=  tradeOther && tradeOther.size() >0 ){
                otherParam = tradeOther.getData(0);
                if(null != userData && userData.size() > 0){
                    String userAttr[] = userData.getNames();
                    for (int i = 0; i < userAttr.length; i++)
                    {
                        if("BAND_WIDTH".equals(userAttr[i])){
                            otherParam.put("RSRV_STR2", userData.getString(userAttr[i]));
                        }else if("CITY_A".equals(userAttr[i])){
                            otherParam.put("RSRV_STR12", userData.getString(userAttr[i]));
                        }else if("CITY_Z".equals(userAttr[i])){
                            otherParam.put("RSRV_STR13", userData.getString(userAttr[i]));
                        }
                    }
                    TradeOtherInfoQry.updateUserOther(tradeId,otherParam);
                }
            }
            
            
            //修改派发信息
            if (null != userDataAttr && userDataAttr.size() > 0)
            {
                String userAttr[] = userDataAttr.getNames();
                for (int i = 0; i < userAttr.length; i++)
                {
                    String attrCode = userAttr[i];
                    IDataset datalineattrs = TradeOtherInfoQry.queryDatalineAttr(tradeId,attrCode);
                    if(null != datalineattrs && datalineattrs.size() > 0){
                        
                        IData datalineattr = datalineattrs.getData(0);
                        datalineattr.put("ATTR_VALUE", userDataAttr.getString(userAttr[i]));
                        
                        TradeOtherInfoQry.updateUserDatalineAttr(tradeId,attrCode,datalineattr);
                    }
                    
                }
            }
        }
    }
    /**
     * 查询该客户下未完工的订单
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset queryOrderInfo(IData param) throws Exception
    {
        BookTradeQueryDAO bean = new BookTradeQueryDAO();
        param.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        return bean.queryOrderInfo(param);
    }
    /**
     * 根据ORDER_ID查询所有TRADE
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset queryMainTradeByOrderId(IData param) throws Exception
    {
        BookTradeQueryDAO bean = new BookTradeQueryDAO();
        param.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        return bean.queryMainTradeByOrderId(param);
    }
    /**
     * 闭环时手动将订单完工
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset updateOrderState(IData param) throws Exception
    {
        BookTradeQueryDAO bean = new BookTradeQueryDAO();
        param.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        IDataset dataset = new DatasetList();
        IData data = new DataMap();
        int result = bean.updateOrderState(param);
        data.put("RESULT", result);
        dataset.add(data);
        return dataset;
    }
    
    
    /**
     * 撤单查询该订单信息
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset queryOrderInfoByOrderId(IData param) throws Exception
    {
        BookTradeQueryDAO bean = new BookTradeQueryDAO();
        param.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        return bean.queryOrderInfoByOrderId(param);
    }
    
    /**
     * 撤单时询该订单的所有TRADE
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset queryTradeInfoByOrderId(IData param) throws Exception
    {
        BookTradeQueryDAO bean = new BookTradeQueryDAO();
        param.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        return bean.queryTradeInfoByOrderId(param);
    }
    
    /**
     * 撤单时删除订单数据
     * @param param
     * @return
     * @throws Exception
     */
    public static boolean deleteOrderInfoByOrderId(IData param) throws Exception
    {
        BookTradeQueryDAO bean = new BookTradeQueryDAO();
        param.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        return bean.deleteOrderInfoByOrderId(param);
    }
    
    /**
     * 撤单时删除所有TRADE数据
     * @param param
     * @return
     * @throws Exception
     */
    public static boolean deleteTradeInfoByOrderId(IData param) throws Exception
    {
        BookTradeQueryDAO bean = new BookTradeQueryDAO();
        param.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        return bean.deleteTradeInfoByOrderId(param);
    }
    
    
    /**
     * 撤单时将订单移历史表
     * @param param
     * @return
     * @throws Exception
     */
    public static int[] insertOrderHInfo(IDataset param) throws Exception
    {
        BookTradeQueryDAO bean = new BookTradeQueryDAO();
        return bean.insertOrderHInfo(param);
    }
    
    /**
     * 撤单时将所有TRADE移历史表
     * @param param
     * @return
     * @throws Exception
     */
    public static int[] insertTradeHInfo(IDataset param) throws Exception
    {
        BookTradeQueryDAO bean = new BookTradeQueryDAO();
        return bean.insertTradeHInfo(param);
    }
    
    public static IDataset queryTradeOtherByTradeId(IData param) throws Exception
    {
        return TradeOtherInfoQry.getTradeOtherByTradeId(param.getString("TRADE_ID"));
    }
    
    public static void updateTradeOtherByTradeId(IData param) throws Exception
    {
        TradeOtherInfoQry.updateUserOther(param.getString("TRADE_ID"),param.getData("USER_OTHER"));
    }
    
    public static IDataset checkGroupDatalineUserInfoByGroupId(IData param) throws Exception
    {
        BookTradeQueryDAO bean = new BookTradeQueryDAO();
        param.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        return bean.checkGroupDatalineUserInfo(param);
    } 
    
    public static IDataset getMaxLineNumberByUserId(IData param)throws Exception
    {
        BookTradeQueryDAO bean = new BookTradeQueryDAO();
        param.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        return bean.getMaxLineNumberByUserId(param);
    } 
    /**
     * 根据trade_id修改工单的执行时间为当前时间,
     * ESOP点击协议报备完成后调用
     * REQ201706010005ESOP1.5计费节点触发改造优化
     * @param param
     * @throws Exception
     * @Author:chenzg
     * @Date:2017-9-15
     */
    public static IDataset updateTradeExecTimeToNow(IData param) throws Exception{
    	System.out.println("updateTradeExecTimeToNow="+param);
        String tradeId = IDataUtil.getMandaData(param, "TRADE_ID");
        BookTradeQueryDAO dao = new BookTradeQueryDAO();
        dao.updateOrderTradeExecTimeToNow(tradeId);
        IDataset dataset = new DatasetList();
        IData iData = new DataMap();
        iData.put("updateTradeExecTimeToNow", "0");
        dataset.add(iData);
        return dataset;
        
    }
	/**
     * 根据专线实例号查询数据，判断资料是否存在且是否欠费、是否为20180601之后生成
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset getDatalineforTapMarkting (IData param) throws Exception{
    	System.out.println("BookTradeSVC-getDatalineforTapMarkting param="+param);
    	IDataset dataset = new DatasetList();
    	IData iData = new DataMap();
        iData.put("updateTradeExecTimeToNow", "0");
        dataset.add(iData);
        
    	String productNo = IDataUtil.getMandaData(param, "PRODUCTNO");
    	String groupId = IDataUtil.getMandaData(param, "GROUPID");
    	String offerCode = IDataUtil.getMandaData(param, "OFFER_CODE");
    	BookTradeQueryDAO bean = new BookTradeQueryDAO();
    	IData datalineInData = new DataMap();
    	datalineInData.put("PRODUCT_NO", productNo);	 
    	IDataset datalineInfos=bean.getDatalineInfoByProductNo(datalineInData);
    	System.out.println("BookTradeSVC-getDatalineforTapMarkting datalineInfos="+datalineInfos);
    	if(null !=  datalineInfos && datalineInfos.size() >0 ){
    		IData datalineInfo=datalineInfos.getData(0);
    		String userId=datalineInfo.getString("USER_ID", "");
    		UcaData uca = UcaDataFactory.getUcaByUserId(userId);
    		if(uca.getCustGroup().getGroupId().equals(groupId)){

        		String startDate=datalineInfo.getString("START_DATE", "");
        		SimpleDateFormat simpleDateFormatStartDate=new SimpleDateFormat("yyyy-MM-dd");
      	 	  	Date start_Date = simpleDateFormatStartDate.parse(startDate);	
      	 	  	SimpleDateFormat simpleDateFormatNowtDate=new SimpleDateFormat("yyyy-MM-dd");
    	 	  	Date now_Date = simpleDateFormatNowtDate.parse("2018-06-01");	
    	 	  	System.out.println("BookTradeSVC-getDatalineforTapMarkting start_Date="+start_Date+" now_Date:"+now_Date);
        		if(start_Date.before(now_Date)){
        			iData.put("getDatalineforTapMarkting", "0002");
            		iData.put("getDatalineforTapMarktingInfo", "该专线实例编码在2018年6月之前生成");
        		}else{
        			float balance = 0.0f;
        			IData oweFeeData = AcctCall.getOweFeeByUserId(userId);
        			System.out.println("BookTradeSVC-getDatalineforTapMarkting oweFeeData="+oweFeeData);
        			if (IDataUtil.isNotEmpty(oweFeeData)) {
        				String ACCT_BALANCE = oweFeeData.getString("ACCT_BALANCE", "");
        				if (StringUtils.isNotBlank(ACCT_BALANCE)) {
        					balance = Float.parseFloat(ACCT_BALANCE) / 100.0f; //acct_balance < 0表示欠费
        				}
        			}
        			if (balance < 0) 
        			{
        				/*stoptypeCode = "2";
        				stoptypeDesc = "欠费";*/
        				iData.put("getDatalineforTapMarkting", "0003");
        				iData.put("getDatalineforTapMarktingInfo", "该产品编码已欠费");
        			} else{
        				IData otherInData = new DataMap();
        				otherInData.put("USER_ID", userId);
        				otherInData.put("PRODUCTNO", productNo);
        				otherInData.put("PRODUCT_ID", offerCode);

        				if("7010".equals(offerCode)){
        					IDataset otherInfos=bean.queryLineInfosForProductNo(otherInData);
            				if(null !=  otherInfos && otherInfos.size() >0 ){
            					IData otherInfo=otherInfos.getData(0);
            					if(otherInfo.getString("RSRV_STR3", "0").equals("0")){
            						iData.put("getDatalineforTapMarkting", "0005");
                		    		iData.put("getDatalineforTapMarktingInfo", "根据PRODUCTNO查询专线月租费用为零");
            					}else{
            						
            						iData.put("PRODUCTNO", "productNo");
            						iData.put("PRODUCTNUMBER", uca.getUser().getSerialNumber());
            						iData.put("MONTHLYFEE_EXCITATION", otherInfo.getString("RSRV_STR3", "0"));
            						iData.put("getDatalineforTapMarkting", "0000");
            	    				iData.put("getDatalineforTapMarktingInfo", "该产品编码可用!");
            	    				
            					}
            				}else{
            		    		iData.put("getDatalineforTapMarkting", "0004");
            		    		iData.put("getDatalineforTapMarktingInfo", "根据PRODUCTNO查询专线计费资料不存在");
            		    	}
        				}else{
        					UserAttrInfoQrySVC userAttrInfoQrySVC=new UserAttrInfoQrySVC();
        					IData lineInfo=userAttrInfoQrySVC.getUserLineInfoByUserId(otherInData);
        					if(lineInfo.getString("59701003", "0").equals("0")){
        						iData.put("getDatalineforTapMarkting", "0005");
            		    		iData.put("getDatalineforTapMarktingInfo", "根据PRODUCTNO查询专线月租费用为零");
        					}else{
        						iData.put("PRODUCTNO", "productNo");
        						iData.put("PRODUCTNUMBER", uca.getUser().getSerialNumber());
        						iData.put("MONTHLYFEE_EXCITATION", lineInfo.getString("59701003", "0"));
        						iData.put("getDatalineforTapMarkting", "0000");
        	    				iData.put("getDatalineforTapMarktingInfo", "该产品编码可用!");
        					}
        				}
        				

        				
        				
        			}
        			
        			
        		}
    		}else{
    			iData.put("getDatalineforTapMarkting", "0006");
	    		iData.put("getDatalineforTapMarktingInfo", "该专线集团编码与输入集团编码不符");
    		}
    		
    	}else{
    		iData.put("getDatalineforTapMarkting", "0001");
    		iData.put("getDatalineforTapMarktingInfo", "根据PRODUCTNO查询专线资料不存在");
    	}
    	
    	dataset.add(iData);
        return dataset;
    }
    /**
     * 根据专线实例号查询数据 专线资料是否存在
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset getDatalineforProduct(IData param) throws Exception
    {
        String productNo = IDataUtil.getMandaData(param, "PRODUCTNO");
        BookTradeQueryDAO bean = new BookTradeQueryDAO();
        IData datalineInData = new DataMap();
        datalineInData.put("PRODUCT_NO", productNo);
        IDataset datalineInfos = bean.getDatalineInfoByProductNo(datalineInData);
        return datalineInfos;
    }
}
