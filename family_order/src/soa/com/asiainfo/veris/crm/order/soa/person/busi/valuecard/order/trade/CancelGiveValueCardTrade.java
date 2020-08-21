/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.valuecard.order.trade;

import java.math.BigInteger;
import java.util.List;

import org.apache.log4j.Logger;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DeviceTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.FeeTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.person.busi.valuecard.ValueCardMgrBean;
import com.asiainfo.veris.crm.order.soa.person.busi.valuecard.order.requestdata.CancelGiveValueCardReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.valuecard.order.requestdata.ValueCardInfoReqData;

/**
 * @CREATED by gongp@2014-6-9 修改历史 Revision 2014-6-9 上午09:02:01
 */
public class CancelGiveValueCardTrade extends BaseTrade implements ITrade
{

	 static Logger logger=Logger.getLogger(CancelGiveValueCardTrade.class);
	
    @Override
    public void createBusiTradeData(BusiTradeData bd) throws Exception
    {
        // TODO Auto-generated method stub
        CancelGiveValueCardReqData reqData = (CancelGiveValueCardReqData) bd.getRD();

        String dealMethod = reqData.getDealMethod();

        MainTradeData mtd = bd.getMainTradeData();

        if ("".equals(reqData.getUca().getSerialNumber()))
        {
            mtd.setUserId(bd.getTradeId());
            mtd.setCustName(reqData.getCustName());
        }

        mtd.setRsrvStr1("5");
        mtd.setRsrvStr3(" ");
        mtd.setRsrvStr4(" ");
        mtd.setRsrvStr6(dealMethod);

        mtd.setRsrvStr8(reqData.getVestStaffId());
        mtd.setRsrvStr9(StaticUtil.getStaticValue(getVisit(), "TD_M_STAFF", "STAFF_ID", "DEPART_ID", reqData.getVestStaffId()));
        mtd.setRsrvStr10(reqData.getVestCityCode());
        mtd.setRemark(reqData.getRemark());

        if ("2".equals(dealMethod))
        {// 文件导入

            mtd.setRsrvStr7("RECORD_ID");

            ValueCardMgrBean bean = BeanManager.createBean(ValueCardMgrBean.class);
            IData param = new DataMap();
            param.put("CARD_LIST", reqData.getImportCardNumList());
            IData datas = bean.getResInfoForImport(param);

            IDataset dataset = datas.getDataset("TABLE1");

            this.dealTradeFeeDeviceByTmp(bd, dataset);

        }
        else
        {// 连号

            List<ValueCardInfoReqData> cardList = reqData.getCardlist();

            /*
             * Float shouldPayFee = new Float(0); int totalCount = 0; if(cardList!= null && cardList.size()>0){ for (int
             * i = 0; i < cardList.size(); i++) { ValueCardInfoReqData valueData = cardList.get(i); shouldPayFee +=
             * Float.valueOf(valueData.getTotalPrice()) * 100; totalCount += valueData.getRowCount(); } }
             * this.dealTradeFeeSub(bd, shouldPayFee); float balance = 0; float totalBalance = 0;
             * if(Float.parseFloat(bd.getOperFee()) < shouldPayFee) { totalBalance = shouldPayFee -
             * Float.parseFloat(bd.getOperFee()); balance = totalBalance; } float[] theBalance = new
             * float[cardList.size()]; //分摊到各号段的减免差价，单位：（分） if(totalBalance != 0) { IData balanceData = null; for(int i
             * = 0; i < cardList.size(); i++) { balanceData = (IData) cardList.get(i); int tempSalePrice = (int)
             * (totalBalance * (Float.parseFloat(balanceData.getString("totalPrice")) * 100 / shouldPayFee) / 100); if(i
             * == cardList.size() - 1) { theBalance[i] = balance; } else { theBalance[i] = tempSalePrice * 100; }
             * balance = balance - tempSalePrice * 100; } }
             */
            IData tagMap = new DataMap();
            for (int i = 0, size = cardList.size(); i < size; i++)
            {

                ValueCardInfoReqData valueData = cardList.get(i);

                this.dealTradeFeeDevice(bd, valueData, tagMap);
            }

        }
    }

    public void dealTradeFeeDevice(BusiTradeData bd, ValueCardInfoReqData valueData, IData tagMap) throws Exception
    {
        CancelGiveValueCardReqData reqData = (CancelGiveValueCardReqData) bd.getRD();

        DeviceTradeData deviceTd = new DeviceTradeData();

        deviceTd.setDeviceTypeCode(valueData.getResKindCode());// cardType
        deviceTd.setFeeTypeCode("20");
        deviceTd.setDeviceNoS(valueData.getStartCardNo());
        deviceTd.setDeviceNoE(valueData.getEndCardNo());
        deviceTd.setDeviceNum(valueData.getRowCount() + "");
        deviceTd.setSalePrice(String.valueOf((int) (Double.parseDouble(valueData.getTotalPrice()))));
        deviceTd.setDevicePrice(String.valueOf((int) (Double.parseDouble(valueData.getDevicePrice()))));

        String resTypeCode ="";
        if(!StringUtils.isBlank(valueData.getResKindCode()) && valueData.getResKindCode().length()>=3){
            resTypeCode = valueData.getResKindCode().substring(0, 3);
        }
        deviceTd.setRsrvStr1(resTypeCode);

        bd.add(reqData.getUca().getSerialNumber(), deviceTd);

        IDataset resSet = ResCall.updateValueCardReturnInfoIntf(valueData.getStartCardNo(), valueData.getEndCardNo(), "3", CSBizBean.getVisit().getDepartId(), "0", null, reqData.getVestStaffId(), reqData.getVestCityCode(), reqData.getVestDepartId(),
                "424");
        if (resSet.getData(0).getInt("X_RESULTCODE") != 0)
        {
            // THROW_C(216201, "调用资源更新接口出错！");
            CSAppException.apperr(CrmUserException.CRM_USER_867);
        }
        
        /**
         * 
         */
        String valueCardNo=valueData.getStartCardNo();
        updateValueCardDetailedInfoByCardNumber(valueCardNo);
        
    }

    public void dealTradeFeeDeviceByTmp(BusiTradeData bd, IDataset dataset) throws Exception
    {

        CancelGiveValueCardReqData reqData = (CancelGiveValueCardReqData) bd.getRD();

        if (IDataUtil.isNotEmpty(dataset))
        {

            DataHelper.sort(dataset, "VALUE_CARD_NO", IDataset.TYPE_STRING, IDataset.ORDER_ASCEND);
            IDataset resDataset = new DatasetList();

            for (int i = 0, size = dataset.size(); i < size; i++)
            {

                IData data = dataset.getData(i);
                IData temp = new DataMap();
                temp.put("VALUE_CARD_NO", data.getString("VALUE_CARD_NO"));
                temp.put("RES_KIND_CODE", data.getString("RES_KIND_CODE"));
                resDataset.add(temp);

                DeviceTradeData deviceTd = new DeviceTradeData();

                deviceTd.setDeviceTypeCode(data.getString("RES_KIND_CODE"));// cardType
                deviceTd.setFeeTypeCode("20");// 22——有价卡购卡费
                deviceTd.setDeviceNoS(data.getString("VALUE_CARD_NO"));

                deviceTd.setSalePrice("0");
                deviceTd.setDevicePrice(data.getString("VALUE_PRICE", "0"));

                String resTypeCode ="";
                String resKindcode =data.getString("RES_KIND_CODE");
                if(!StringUtils.isBlank(resKindcode) && resKindcode.length()>=3){
                    resTypeCode = resKindcode.substring(0, 3);
                }
                
                deviceTd.setRsrvStr1(resTypeCode);

                int k = i;
                for (int j = 0; j < dataset.size() - k; j++)
                {
                    BigInteger tmpInteger1 = new BigInteger(dataset.getData(i).getString("VALUE_CARD_NO", "%"));
                    BigInteger tmpInteger2 = new BigInteger("0");
                    if (i + 1 == dataset.size())
                        tmpInteger2 = new BigInteger(dataset.getData(i).getString("VALUE_CARD_NO", "%"));
                    else
                        tmpInteger2 = new BigInteger(dataset.getData(i + 1).getString("VALUE_CARD_NO", "%"));
                    if (tmpInteger2 == tmpInteger1.add(new BigInteger("1")))
                    {
                        tmpInteger1 = tmpInteger2;
                        i++;
                    }
                    else
                    {
                        deviceTd.setDeviceNoE(tmpInteger1 + "");
                        deviceTd.setDeviceNum("" + (j + 1));
                        break;
                    }
                }

                bd.add(reqData.getUca().getSerialNumber(), deviceTd);
                
                /**
                 * 当返销时修改tl_b_valuecard_detailed 
                 * 通过卡号修改为
                 * t.state_name='返销' t.state_code='1'
                 */
                String valueCardNo=data.getString("VALUE_CARD_NO");
                updateValueCardDetailedInfoByCardNumber(valueCardNo);
            }

            IDataset resSet = ResCall.updateValueCardReturnInfoIntf("1", "1", "3", CSBizBean.getVisit().getDepartId(), "1", resDataset, reqData.getVestStaffId(), reqData.getVestCityCode(), reqData.getVestDepartId(), "424");
            if (resSet.getData(0).getInt("X_RESULTCODE") != 0)
            {
                // THROW_C(216201, "调用资源更新接口出错！");
                CSAppException.apperr(CrmUserException.CRM_USER_867);
            }
        }
    }

    public void dealTradeFeeSub(BusiTradeData bd, float shouldPayFee) throws Exception
    {
        CancelGiveValueCardReqData reqData = (CancelGiveValueCardReqData) bd.getRD();

        List feeTradeDatas = bd.getTradeDatas(TradeTableEnum.TRADE_FEESUB);

        if (feeTradeDatas != null && feeTradeDatas.size() > 0)
        {
            for (int i = 0; i < feeTradeDatas.size(); i++)
            {
                FeeTradeData feeTradeData = (FeeTradeData) feeTradeDatas.get(i);
                if (shouldPayFee == 0)
                {
                    feeTradeData.setOldfee(String.valueOf(0));
                }
                else
                {
                    feeTradeData.setOldfee("-" + String.valueOf(shouldPayFee));
                }
            }
        }
    }
    
    /**
     * 当返销时修改tl_b_valuecard_detailed 
     * 通过卡号修改为
     * t.state_name='返销' t.state_code='1'
     * @param valueCardNo
     * @throws Exception
     */
    public void updateValueCardDetailedInfoByCardNumber(String valueCardNo) throws Exception{
    	try {
            if(!"".equals(valueCardNo)||valueCardNo != null){
            	StringBuilder sql = new StringBuilder();
            	sql.append(" update tl_b_valuecard_detailed t set t.state_name='返销',t.state_code='1' ");
            	sql.append(" ,t.rsrv_str1='"+SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND)+"'");
            	sql.append(" where t.card_number='"+valueCardNo+"'");
            	sql.append(" and t.state_code='0' ");
            	IData  param=new DataMap();
            	Dao.executeUpdate(sql, param);
            }
		} catch (Exception e) {
			// TODO: handle exception
			if(logger.isInfoEnabled()) logger.info(e);
			throw e;
		}
    	
    }

}
