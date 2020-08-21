package com.asiainfo.veris.crm.iorder.soa.person.busi.tp.tpdatadeal;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.iorder.soa.person.busi.tp.base.ReqBuildService;
import com.asiainfo.veris.crm.order.pub.consts.TpConsts;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.saleactivetrans.SaleActiveTransBean;
import org.apache.axis.utils.StringUtils;

import java.math.BigDecimal;
import java.util.Iterator;

public class SaleActiveDataDealSVC extends ReqBuildService {

    @Override
    public IData initReqData(IData input) throws Exception {
        //1、获取用户可中止的营销活动
        IDataset baseTradeInfo = queryBaseTradeInfo(input);

        //2、校验选择
        String userProductId = "67220428";//input.getString("PRODUCT_ID");
        IData dealData = null;
        if(DataUtils.isNotEmpty(baseTradeInfo) && !StringUtils.isEmpty(userProductId)){
            for (int i = 0; i < baseTradeInfo.size();i++){
                IData data = baseTradeInfo.getData(i);
                String productId = data.getString("PRODUCT_ID");
                if(userProductId.equals(productId)){
                    dealData = data;
                    break;
                }
            }
        }

        IData svcParam = new DataMap();

        if(DataUtils.isNotEmpty(dealData)){
            svcParam = getIntfMsg(input,dealData);
        }
        return svcParam;
    }

    @Override
    public String getTradeTypeCode(IData input) throws Exception {
        return "237";
    }

    public IDataset queryBaseTradeInfo(IData data) throws Exception
    {
        String serialNumber = data.getString(TpConsts.comKey.accessNumber);
        UcaData ucaData = UcaDataFactory.getNormalUca(serialNumber);
        String userId = ucaData.getUserId();
        String eparchyCode = ucaData.getUserEparchyCode();
        SaleActiveTransBean saleActiveTransBean = BeanManager.createBean(SaleActiveTransBean.class);
        IDataset saleActives =  saleActiveTransBean.queryEndSaleActives(userId,eparchyCode);
        //紧急修改用户确认问题。
        if(saleActives != null && saleActives.size() > 0){
            String staff = data.getString("OP_ID");
            /**
             * REQ201607270001 关于终止吉祥号码营销活动包权限的优化需求
             * chenxy3
             * 统一规则，替代//REQ201412260003新增星火计划礼包提前终止专项办理界面
             * 有权限且才能终止配置6889里的规则
             * flag
             * */
            boolean priv = StaffPrivUtil.isFuncDataPriv(staff, "SALEACTIVE_END_PROD");
            IDataset saleActives1=new DatasetList();
            if("SUPERUSR".equalsIgnoreCase(staff)){
                priv=true;
            }
            //1、要终止的列表循环
            for (Iterator it1 = saleActives.iterator(); it1.hasNext();){
                String flag = "1";
                IData id1 = (IData) it1.next();
                if(priv==false){
                    String product = id1.getString("PRODUCT_ID");
                    IDataset commparaValue = new DatasetList();
                    if (product != null && !"".equals(product)) {//经查，最原始的代码使用的6889，但三代后6889已经被占用，改为6888
                        commparaValue = CommparaInfoQry.getCommparaAllColByParser("CSM", "6888", product, "0898");
                    }
                    if (commparaValue != null && commparaValue.size() > 0){
                        for(int k=0;k<commparaValue.size();k++){
                            IData commData=commparaValue.getData(k);
                            String paraCode1=commData.getString("PARA_CODE1","");
                            String packId = id1.getString("PACKAGE_ID");
                            if(("1".equals(paraCode1)||paraCode1.equals(packId))){
                                flag ="0";
                                break;
                            }
                        }
                    }
                }
                id1.put("PRODT_FLAG", flag);
                saleActives1.add(id1);
            }
            return saleActives1;
        }else{
            return saleActives;
        }
    }

    /**
     * 拼装接口数据
     * @param input
     * @param dealData
     * @return
     * @throws Exception
     */
    private IData getIntfMsg(IData input,IData dealData) throws Exception{
        //如果存在退费，封装操作费用
        String returnFee = dealData.getString("RETURNFEE","0");
        BigDecimal returnFeeD = new BigDecimal(returnFee);
        int finalReturnFee = returnFeeD.multiply(new BigDecimal(100)).intValue();

        String ysreturnFee = dealData.getString("YSRETURNFEE", "0");
        BigDecimal ysreturnFeeD = new BigDecimal(ysreturnFee);
        int ysfinalReturnFee = ysreturnFeeD.multiply(new BigDecimal(100)).intValue();

        String trueFeeCost = dealData.getString("TRUE_RETURNFEE_COST", "0");
        BigDecimal trueFeeCostFeeD = new BigDecimal(trueFeeCost);
        int trueReturnFeeCost = trueFeeCostFeeD.multiply(new BigDecimal(100)).intValue();

        String trueFeePrice = dealData.getString("TRUE_RETURNFEE_PRICE", "0");
        BigDecimal trueFeePriceFeeD = new BigDecimal(trueFeePrice);
        int trueReturnfeePrice = trueFeePriceFeeD.multiply(new BigDecimal(100)).intValue();

        IData svcParam = new DataMap();
        svcParam.put("CHECK_MODE", dealData.getString("CHECK_MODE"));
        svcParam.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
        svcParam.put("PRODUCT_ID", dealData.getString("PRODUCT_ID"));
        svcParam.put("PACKAGE_ID", dealData.getString("PACKAGE_ID"));
        svcParam.put("RELATION_TRADE_ID", dealData.getString("RELATION_TRADE_ID"));
        svcParam.put("CAMPN_TYPE", dealData.getString("CAMPN_TYPE"));
        svcParam.put("REMARK", input.getString("REMARK"));
        svcParam.put("RETURNFEE",finalReturnFee);
        svcParam.put("YSRETURNFEE", ysfinalReturnFee);
        svcParam.put("TRUE_RETURNFEE_COST", trueReturnFeeCost);
        svcParam.put("TRUE_RETURNFEE_PRICE", trueReturnfeePrice);
        svcParam.put("SRC_PAGE", "0");
        svcParam.put("INTERFACE", "1");
        svcParam.put(Route.ROUTE_EPARCHY_CODE, input.getString("EPARCHY_CODE"));
        svcParam.put("END_DATE_VALUE", dealData.getString("END_DATE_VALUE"));//QR-20150109-14 营销活动终止时间不对BUG by songlm @20150114
        svcParam.put("PAYMONEYEND", dealData.getString("RSRVSTR6"));//付费方式
        svcParam.put("BACK_TERM", input.getString("BACK_TERM"));
        if(finalReturnFee>0){
            IData tradeFeeSub = new DataMap();
            tradeFeeSub.put("TRADE_TYPE_CODE", "237");
            tradeFeeSub.put("FEE_TYPE_CODE", "602");
            tradeFeeSub.put("FEE", finalReturnFee);
            tradeFeeSub.put("OLDFEE", finalReturnFee);
            tradeFeeSub.put("FEE_MODE", "0");	//营业费用
            tradeFeeSub.put("ELEMENT_ID", "");

            IData tradePayMoney = new DataMap();
            tradePayMoney.put("PAY_MONEY_CODE", "0");
            tradePayMoney.put("MONEY", finalReturnFee);

            IDataset tradeFeeSubs = new DatasetList();
            tradeFeeSubs.add(tradeFeeSub);

            IDataset tradePayMoneys = new DatasetList();
            tradePayMoneys.add(tradePayMoney);

            svcParam.put("X_TRADE_FEESUB", tradeFeeSubs);
            svcParam.put("X_TRADE_PAYMONEY", tradePayMoneys);
        }
        return svcParam;
    }
}
