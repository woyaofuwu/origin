/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.familytrade.order.action.finish;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeRelaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import org.apache.log4j.Logger;

/**
 * @CREATED by gongp@2014-8-21 修改历史 Revision 2014-8-21 下午08:44:58
 */
public class FamilyUnionPayOpenAction implements ITradeFinishAction
{
    static Logger logger=Logger.getLogger(FamilyUnionPayOpenAction.class);
    /*
     * (non-Javadoc)
     */
    @Override
    public void executeAction(IData mainTrade) throws Exception
    {
        try {
            // TODO Auto-generated method stub
            String tradeId = mainTrade.getString("TRADE_ID");
            IDataset tradeRelation = TradeRelaInfoQry.queryTradeRelaByTradeIdModTag(tradeId,"56","0");
            System.out.print("wuhao50==========" + tradeRelation);
            if(IDataUtil.isNotEmpty(tradeRelation) && tradeRelation.size() > 0)
            {
                //第一个循环，捞取统付主号
                for(int i = 0 ; i < tradeRelation.size() ; i++)
                {
                    IData idata = tradeRelation.getData(i);
                    String userId = idata.getString("USER_ID_B");
                    String serialNumber = idata.getString("SERIAL_NUMBER_B");
                    if("1".equals(idata.getString("ROLE_CODE_B")))//主卡号码
                    {
                        //主号为开通状态则才对副号进行开机操作
                        IDataset userDatas = UserInfoQry.getUserInfoBySn(serialNumber, "0");//查询user表记录
                        if (IDataUtil.isNotEmpty(userDatas) && userDatas.size() > 0) {
                            IData userData = userDatas.getData(0);
                            System.out.print("wuhao48==========" + userData);
                            String userState = userData.getString("USER_STATE_CODESET");
                            if (!"0".equals(userState)){
                                return;
                            }
                        }else {
                            return;
                        }
                        break;
                    }
                }
                //第一个循环，捞取统付副号
                for(int i = 0 ; i < tradeRelation.size() ; i++)
                {
                    IData idata = tradeRelation.getData(i);
                    if("2".equals(idata.getString("ROLE_CODE_B")))//副卡号码
                    {
                        String userId = idata.getString("USER_ID_B");
                        String serialNumber = idata.getString("SERIAL_NUMBER_B");
                        //有往月欠费的号码不能开机
                        IData oweFee = AcctCall.getOweFeeByUserId(userId);
                        String fee1 = oweFee.size() > 0 ? oweFee.getString("LAST_OWE_FEE") : "0";
                        if (Integer.parseInt(fee1) > 0) {
                            return;
                        }
                        IDataset productDatas = ProductInfoQry.getUserProductByUserIdForGrp(userId);
                        if (IDataUtil.isNotEmpty(productDatas) && productDatas.size() > 0) {
                            IData productData = productDatas.getData(0);
                            System.out.print("wuhao51==========" + productData);
                            IDataset userDatas = UserInfoQry.getUserInfoBySn(serialNumber, "0");//查询user表记录
                            System.out.print("wuhao52==========" + userDatas);
                            if (IDataUtil.isNotEmpty(userDatas) && userDatas.size() > 0) {
                                IData userData = userDatas.getData(0);
                                System.out.print("wuhao53==========" + userData);
                                String userState = userData.getString("USER_STATE_CODESET");
                                if ("5".equals(userState) || "7".equals(userState) || "A".equals(userState) || "B".equals(userState))//欠费状态的号码都局开
                                {
                                    IData param = new DataMap();
                                    if ("PWLW".equals(productData.getString("BRAND_CODE")))//物联网卡号码
                                    {
                                        param.put("TRADE_TYPE_CODE", "7304");//信用特殊开机
                                    } else {
                                        param.put("TRADE_TYPE_CODE", "126");//局方开机
                                    }
                                    param.put("USER_ID", productData.getString("USER_ID"));
                                    param.put("ROUTE_EPARCHY_CODE", "0898");
                                    param.put("SERIAL_NUMBER", serialNumber);
                                    param.put("REMARK", "统付副卡关联开机");
                                    CSAppCall.callOne("SS.CreditTradeRegSVC.tradeReg", param);
                                }
                            }
                        }
                    }
                }
            }
        }catch (Exception e){
            if (e.getMessage() != null) {
                String error = e.getMessage().length() > 2000 ? e.getMessage().substring(0,2000) : e.getMessage();
                System.out.println(error);
                logger.error(error);
            }
        }
    }
}
