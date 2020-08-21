
package com.asiainfo.veris.crm.order.soa.group.destroygroupuser;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.destroygroupuser.DestroyGroupUser;
import com.asiainfo.veris.crm.order.soa.group.dataline.DatalineUtil;

public class DestroyNetinGroupUser extends DestroyGroupUser
{
    public DestroyNetinGroupUser()
    {
    }

    /**
     * 其它台帐处理
     */
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();
//        actTradeDatalineAttr();
//        actTradeDataline();
          checkDataline();
    }
    
    private void checkDataline() throws Exception
    {
        IDataset resultDataset = new DatasetList();
        String user_id = reqData.getUca().getUser().getUserId();
        String serailNumber = reqData.getUca().getUser().getSerialNumber();
        
        	IData inputParam = new DataMap();
        	inputParam.put("USER_ID",user_id);     	
        	resultDataset = CSAppCall.call("CS.RelaUUInfoQrySVC.qryUUInfoAllCrmByUserIdA", inputParam);
        	if(null != resultDataset && resultDataset.size() > 0){
        		CSAppException.apperr(CrmCommException.CRM_COMM_888,"集团产品编码【"+serailNumber+"】下存在成员产品，无法注销！");
        	}
        
  
    }

    private void actTradeDatalineAttr() throws Exception
    {
        IDataset resultDataset = new DatasetList();
        IDataset dataset = new DatasetList();
        IData dataLine = null;
        IDataset eos = reqData.getEos();
        if (null != eos && eos.size() > 0)
        {
            IData eosList = eos.getData(0);
            IData inputParam = new DataMap();
            inputParam.put("NODE_ID", eosList.getString("NODE_ID", ""));
            inputParam.put("IBSYSID", eosList.getString("IBSYSID", ""));
            inputParam.put("SUB_IBSYSID", eosList.getString("SUB_IBSYSID", ""));
            inputParam.put("PRODUCT_ID", eosList.getString("PRODUCT_ID"));
            inputParam.put("OPER_CODE", "14");
            
            dataset.add(inputParam);
            resultDataset = CSAppCall.call("SS.ESOPQcsGrpBusiIntfSvc.getEosInfo", inputParam);
        }else{
            CSAppException.apperr(GrpException.CRM_GRP_838);
        }

        if (null != resultDataset && resultDataset.size() > 0){
            dataLine = DatalineUtil.mergeData(resultDataset);
            if (null != dataLine && dataLine.size() > 0){
                IDataset commonData = dataLine.getDataset("COMMON_DATA");
                if (null != commonData && commonData.size() > 0){
				
					IDataset lineSet = dataLine.getDataset("DLINE_DATA");
					if (null != lineSet && lineSet.size() > 0){
						String productNoListStr = "";
						String tradeIdListStr = "";
						for(int i = 0; i < lineSet.size();i++){
							String tradeIdTemp = lineSet.getData(i).getString("TRADEID","");
							String productNOTemp = lineSet.getData(i).getString("PRODUCTNO","");
							if(i == 0){
								productNoListStr = productNOTemp;
								tradeIdListStr = tradeIdTemp;
							}else{
								productNoListStr = productNoListStr +","+productNOTemp;
								tradeIdListStr = tradeIdListStr+","+productNOTemp;
							}
						}
						
						for(int m=commonData.size()-1;m >=0;m--){
							String attrCodeTemp = commonData.getData(m).getString("ATTR_CODE","");
							if(attrCodeTemp.equals("TRADEID") || attrCodeTemp.equals("PRODUCTNO") ){
								commonData.remove(m);
							}
						}
						
						IData tradeIdData = new DataMap();
						tradeIdData.put("ATTR_CODE","TRADEID");
						tradeIdData.put("ATTR_VALUE",tradeIdListStr);
						
						IData productNoData = new DataMap();
						productNoData.put("ATTR_CODE","PRODUCTNO");
						productNoData.put("ATTR_VALUE",productNoListStr);
						
						commonData.add(tradeIdData);
						commonData.add(productNoData);
						
					}
				
                    IData userData = new DataMap();
                    userData.put("USER_ID", reqData.getUca().getUserId());
                    userData.put("START_DATE", getAcceptTime());
                    userData.put("SHEET_TYPE", "6");
                    userData.put("PRODUCT_NO", "-1");

                    dataset = DatalineUtil.addTradeUserDataLineAttr(commonData, null, userData);
                }
            }
        }

        super.addTradeDataLineAttr(dataset);
    }

    private void actTradeDataline() throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("USER_ID", reqData.getUca().getUser().getUserId());
        inparam.put("SHEET_TYPE", "6");
        IDataset datalineList = TradeOtherInfoQry.queryUserDataLineByUserIdAndProductNo(inparam);

        if (null != datalineList && datalineList.size() > 0)
        {
            for (int i = 0; i < datalineList.size(); i++)
            {
                IData userLine = datalineList.getData(i);
                userLine.put("UPDATE_TIME", getAcceptTime());
                userLine.put("END_DATE", getAcceptTime());
                userLine.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
            }
        }

        super.addTradeDataLine(datalineList);
    }
}
