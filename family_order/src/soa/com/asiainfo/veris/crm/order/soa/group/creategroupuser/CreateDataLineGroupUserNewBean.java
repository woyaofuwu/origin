package com.asiainfo.veris.crm.order.soa.group.creategroupuser;

import java.util.Iterator;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.AttrBizInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderBaseBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.GrpInvoker;
import com.asiainfo.veris.crm.order.soa.group.dataline.DatalineUtil;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformAttrBean;
import com.asiainfo.veris.crm.order.soa.group.esp.DatalineEspUtil;

public class CreateDataLineGroupUserNewBean extends GroupOrderBaseBean {

    public void actOrderDataOther(IData map) throws Exception {
        // 创建集团用户信息
        int pfWait = 1;
        String userId = map.getString("USER_ID");
        String serialNumber = map.getString("SERIAL_NUMBER");
        String productId = map.getString("PRODUCT_ID");

        IDataset attrInternet = new DatasetList(map.getString("PRODUCT_PARAM_INFO"));
        //判断是否开环
        pfWait = DatalineUtil.getDatalineOrderPfWait(attrInternet);
        map.put("PF_WAIT", pfWait);

        // 解析专线数据
        IDataset internet = DatalineUtil.parseDataInfo(attrInternet);

        IDataset commonData = DatalineUtil.parseCommonDataInfo(attrInternet);

        IDataset dataline = DatalineUtil.parseDataLineInfo(attrInternet);

        // 生成专线用户
        //        IData interzj = null;
        //        if (null != internet && internet.size() > 0)
        //        {
        //
        //            for (int i = 0; i < internet.size(); i++)
        //            {
        //                IData inter = internet.getData(i);
        //                int size = internetzj.size();
        //                int flag = i + 1;
        //                if (flag > size){}
        //                else{
        //                    interzj = internetzj.getData(i);
        //                }
        //
        //                String lineNumberCode = inter.getString("pam_NOTIN_LINE_NUMBER_CODE");
        //                if(null != dataline && dataline.size() >0){
        //                    for (int j = 0; j < dataline.size(); j++)
        //                    {
        //                        IData datas = dataline.getData(j);
        //                        String numberCode = datas.getString("pam_NOTIN_LINE_NUMBER_CODE");
        //                        
        //                        if (numberCode.equals(lineNumberCode))
        //                        {
        //                            IData maps = new DataMap();
        //                            maps.put("ATTRINTERNET", inter);
        //                            maps.put("ATTRZJ", interzj);
        //                            maps.put("DATALINE", datas);
        //                            maps.put("COMMON_DATA", commonData);
        //                            maps.put("CUST_ID", map.getString("CUST_ID"));
        //                            maps.put("USER_ID", userId);
        //                            maps.put("PRODUCT_ID", productId);
        //                            maps.put("SERIAL_NUMBER", serialNumber);
        //                            maps.put("TRADE_TYPE_CODE", map.getString("TRADE_TYPE_CODE"));
        //                            maps.put("ACCT_ID", map.getString("ACCT_ID"));
        //                            maps.put("PF_WAIT", pfWait);
        //                            
        //                            GrpInvoker.ivkProduct(maps, BizCtrlType.CreateUser, "CreateClass");
        //                        }
        //                    }
        //                    interzj.clear();
        //                }
        //            }
        //        }
        //
        //        // 建立部分依赖关系
        //        List<BizData> bd = DataBusManager.getDataBus().getGrpBizData();
        //        for (int i = 0; i < bd.size(); i++)
        //        {
        //            IDataset other = bd.get(i).getTradeOther();
        //
        //            if (null != other && other.size() > 0)
        //            {
        //
        ////                DatalineUtil.createLimit(mainTradeId, other.getData(0).getString("TRADE_ID"));
        //            	DatalineUtil.createAllLimit(other.getData(0).getString("TRADE_ID"),mainTradeId);
        // 
        //            }
        //        }

        //生成成员SERIAL_NUMBER
        String mebProductId = map.getString("MEB_PRODUCT_ID");//ProductMebInfoQry.getMemberMainProductByProductId(productId);
        IDataset tradeInfos = AttrBizInfoQry.getBizAttr(mebProductId, "P", BizCtrlType.CreateUser, "TradeTypeCode", null);
        if(DataUtils.isEmpty(tradeInfos)) {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据" + mebProductId + "没找到业务类型");
        }
        String tradeTypeCode = tradeInfos.first().getString("ATTR_VALUE", "");
        if(StringUtils.isEmpty(tradeTypeCode)) {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据" + mebProductId + "没找到业务类型");
        }
        IData inparam = new DataMap();

        inparam.put("MAP", map);

        //添加产品参数供成员新增时使用
        /*if(attrInternet != null && attrInternet.size() > 0) {
            for (int i = 0; i < attrInternet.size(); i++) {
                attrInternet.getData(i).put("PRODUCT_ID", mebProductId);
            }
            map.put("PRODUCT_PARAM_INFO", attrInternet.toString());
        }*/

        inparam.put("INTERNET", internet);
        inparam.put("COMMONDATA", commonData);
        inparam.put("DATALINE", dataline);
        inparam.put("PRODUCT_ID", productId);
        inparam.put("TRADE_TYPE_CODE", tradeTypeCode);
        inparam.put("GRP_SERIAL_NUMBER", serialNumber);
        inparam.put("GRP_USER_ID", userId);
        inparam.put("MEB_PRODUCT_ID", mebProductId);
        inparam.put("PF_WAIT", pfWait);
        inparam.put("EOS", map.getDataset("EOSS"));
        if(null != internet) {
            int sizeNum = internet.size();
            if(sizeNum > 0 && sizeNum < 2) {
                //一条专线直接生成成员工单
                createMebTrade(inparam);
            }
        }
    }

    /** 一条专线直接生成成员工单
     * 
     * @param inparam
     * @throws Exception */
    private void createMebTrade(IData inparam) throws Exception {
        IDataset internet = inparam.getDataset("INTERNET");

        IDataset commonData = inparam.getDataset("COMMONDATA");

        IDataset dataline = inparam.getDataset("DATALINE");

        String productId = inparam.getString("PRODUCT_ID");
        String tradeTypeCode = inparam.getString("TRADE_TYPE_CODE");
        String mainTradeId = inparam.getString("TRADE_ID");
        String serialNumber = inparam.getString("GRP_SERIAL_NUMBER");
        String mebProductId = inparam.getString("MEB_PRODUCT_ID");
        String pfWait = inparam.getString("PF_WAIT");
        String grpUserId = inparam.getString("GRP_USER_ID");

        IData map = inparam.getData("MAP");

        IData inter = internet.getData(0);
        String lineNumberCode = inter.getString("pam_NOTIN_LINE_NUMBER_CODE");

        if(null != dataline && dataline.size() > 0) {
            for (int j = 0; j < dataline.size(); j++) {
                IData datas = dataline.getData(j);
                String numberCode = datas.getString("pam_NOTIN_LINE_NUMBER_CODE");

                if(numberCode.equals(lineNumberCode)) {
                    //避免服务号码的重复 add begin
                    IData param = new DataMap();
                    param.put("PRODUCT_ID", productId);
                    param.put(Route.USER_EPARCHY_CODE, CSBizBean.getUserEparchyCode());

                    IDataset grpSnData = new DatasetList();
                    for (int k = 0; k < 10; k++) {
                        grpSnData = CSAppCall.call("CS.GrpGenSnSVC.genGrpSn", param);

                        String serialNumberMeb = grpSnData.first().getString("SERIAL_NUMBER", "");

                        if(StringUtils.isEmpty(serialNumberMeb)) {
                            break;
                        }

                        IData userList = UcaInfoQry.qryUserMainProdInfoBySnForGrp("ZX" + serialNumberMeb);
                        param.clear();
                        param.put("SERIAL_NUMBER", "ZX" + serialNumberMeb);
                        param.put(Route.USER_EPARCHY_CODE, CSBizBean.getUserEparchyCode());
                        param.put("TRADE_TYPE_CODE", tradeTypeCode);
                        IDataset tradeList = CSAppCall.call("CS.TradeInfoQrySVC.getMainTradeBySN", param);

                        if(IDataUtil.isEmpty(userList) && IDataUtil.isEmpty(tradeList)) {
                            break;
                        }
                    }
                    // 避免服务号码的重复 add end

                    String mebSerialNumber = "ZX" + grpSnData.first().getString("SERIAL_NUMBER", "");//专线成员生成规则+ZX

                    //获取专线名
                    String lineName = "";
                    Iterator<String> it = datas.keySet().iterator();
                    while (it.hasNext()) {
                        String lineNamekey = it.next();
                        if("TRADENAME".equals(lineNamekey)) {
                            lineName = datas.getString(lineNamekey, "");
                            break;
                        }
                    }

                    //添加成员所需数据
                    map.put("TRADE_ID", mainTradeId);
                    map.put("GRP_SERIAL_NUMBER", serialNumber);
                    map.put("SERIAL_NUMBER", mebSerialNumber);
                    map.put("PAY_NAME", lineName);
                    map.put("GRP_USER_ID", grpUserId);
                    //成员新增账户
                    map.put("ACCT_IS_ADD", map.getBoolean("ACCT_IS_ADD", false));
                    map.put("ATTRINTERNET", inter);
                    map.put("DATALINE", datas);
                    map.put("COMMON_DATA", commonData);
                    map.put("ELEMENT_INFO", DatalineUtil.getElementInfo(map.getDataset("ELEMENT_INFO"), datas, mebProductId));
                    map.put("EOS", inparam.getDataset("EOS"));

                    //添加资源信息
                    IDataset resList = map.getDataset("RES_INFO", new DatasetList());
                    IData resInfo = new DataMap();
                    resInfo.put("DISABLED", "true");
                    resInfo.put("MODIFY_TAG", "0");
                    resInfo.put("RES_CODE", mebSerialNumber);
                    resInfo.put("RES_TYPE_CODE", "L");
                    resInfo.put("CHECKED", "true");
                    resList.add(resInfo);
                    map.put("RES_INFO", resList);

                    //公共信息存Attr表
                    IDataset paramInfo = map.getDataset("PRODUCT_PARAM_INFO", new DatasetList());
                    IDataset productParam = new DatasetList();
                    if(paramInfo.size() > 0) {
                        IData productParamInfo = paramInfo.first();
                        if(null != productParamInfo && productParamInfo.size() > 0) {
                            productParam = new DatasetList(productParamInfo.getString("PRODUCT_PARAM"));
                        }
                    }
                    IData paramp = new DataMap();
                    paramp.put("PRODUCT_ID", mebProductId);
                    IData paramAttr = new DataMap();
                    //inputParams.getDataset("DLINE_DATA").getData(arg0)
                    String ibsysId = inparam.getDataset("EOS").first().getString("IBSYSID");
                    IData projectNames = WorkformAttrBean.qryAttrByIbsysidRecordCode(ibsysId, "PROJECTNAME", "0");
                    if(IDataUtil.isEmpty(projectNames)) {
                        CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据订单号查询无项目名称信息!");
                    }
                    String projectName = projectNames.getString("ATTR_VALUE");
                    paramAttr.put("NOTIN_DETMANAGER_INFO", "");
                    paramAttr.put("NOTIN_DETMANAGER_PHONE", "");
                    paramAttr.put("NOTIN_DETADDRESS", "");
                    paramAttr.put("NOTIN_PROJECT_NAME", projectName);
                    paramAttr.put("PROJECT_NAME", projectName);
                    paramAttr.putAll(DatalineEspUtil.getCommInfo(ibsysId));
                    productParam.addAll(DatalineEspUtil.saveProductParamInfoFrontData(paramAttr));
                    paramp.put("PRODUCT_PARAM", productParam);
                    paramInfo.add(paramp);
                    map.put("PRODUCT_PARAM_INFO", paramInfo);

                    IData date = DatalineEspUtil.getStartDate(inparam.getDataset("EOS").first()); //获取验收期时间
                    map.put("ELEMENT_INFO", DatalineUtil.getEndDateInfo(map.getDataset("ELEMENT_INFO"), date));
                    GrpInvoker.ivkProduct(map, BizCtrlType.CreateMember, "CreateUserClass");

                }
            }
        }

    }

    /** 数据专线（专网专线）集团产品受理业务类型 */
    @Override
    protected String setOrderTypeCode() throws Exception {
        return "3010";
    }

}
