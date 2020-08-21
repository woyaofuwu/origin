package com.asiainfo.veris.crm.order.soa.group.esop.eopintf;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformAttrBean;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformEomsBean;

public class AttrInfoComparaBean extends EopIntfBaseBean
{

    public void comparaAttrInfo(IData param) throws Exception
    {
        String ibsysid = getMandaData(param, "IBSYSID");
        String tradeId = getMandaData(param, "TRADE_ID");
        String recordNum= getMandaData(param, "RECORD_NUM");
        
        IDataset newWorkSheetList = WorkformEomsBean.qryNewWorkSheetByIbsysid(ibsysid,recordNum);
        if(IDataUtil.isEmpty(newWorkSheetList))
        {
            return ;
        }

        IDataset updTradeAttrList = new DatasetList();
        for(int i = 0, size = newWorkSheetList.size(); i < size; i++)
        {
            IData newWorkSheet = newWorkSheetList.getData(i);
            if(!"renewWorkSheet".equals(newWorkSheet.getString("OPER_TYPE")))
            {
                continue;
            }
            String subIbsysid = newWorkSheet.getString("SUB_IBSYSID");
            recordNum = newWorkSheet.getString("RECORD_NUM");
            
            //获取esop参数
            IDataset eopAttrList = WorkformAttrBean.qryAttrBySubIbsysidAndRecordNum(subIbsysid, recordNum);
            if(IDataUtil.isEmpty(eopAttrList))
            {
                continue;
            }
            
            //获取台账表参数
            String eparchyCode = newWorkSheet.getString("EPARCHY_CODE");
            IDataset tradeAttrList = new DatasetList();//TradeAttrInfoQry.getTradeAttrByTradeIdInstType(tradeId, "P", eparchyCode);
            if(IDataUtil.isEmpty(tradeAttrList))
            {
                continue;
            }
            
            for(int j = 0, sizeJ = tradeAttrList.size(); j < sizeJ; j++)
            {
                IData tradeAttr = tradeAttrList.getData(j);
                String attrCode = tradeAttr.getString("ATTR_CODE");
                String attrValue = tradeAttr.getString("ATTR_VALUE");
                for(int k = 0, sizeK = eopAttrList.size(); k < sizeK; k++)
                {
                    IData eopAttr = eopAttrList.getData(k);
                    String eopAttrCode = eopAttr.getString("ATTR_CODE");
                    String eopAttrValue = eopAttr.getString("ATTR_VALUE");
                    if(attrCode.equals(eopAttrCode))
                    {
                        if(StringUtils.isNotEmpty(eopAttrValue)&&!eopAttrValue.equals(attrValue))
                        {
                            tradeAttr.put("ATTR_VALUE", eopAttrValue);
                            tradeAttr.put("ROUTE_EPARCHY_CODE", eparchyCode);
                            updTradeAttrList.add(tradeAttr);
                        }
                        break;
                    }
                }
            }
        }
        if(IDataUtil.isNotEmpty(updTradeAttrList))
        {//更新trade_attr表数据
            for(int i = 0, size = updTradeAttrList.size(); i < size; i++)
            {
                IData updTradeAttr = updTradeAttrList.getData(i);
//                TradeAttrInfoQry.updateAttrValue(updTradeAttr, updTradeAttr.getString("ROUTE_EPARCHY_CODE"));
            }
        }
    }
}
