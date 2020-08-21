
package com.asiainfo.veris.crm.order.web.group.upgprelation;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;

public abstract class UnifyPayRelationQry extends GroupBasePage
{
    public abstract IData getCondition();

    public abstract void setCondition(IData condition);

    public abstract IDataset getInfos();

    public abstract void setInfos(IDataset infos);

    public abstract void setHintInfo(String infos);

    public abstract void setInfoCount(long infoCount);

    public void initial(IRequestCycle cycle) throws Exception
    {
        IData param = getData("cond", true);
        this.setCondition(param);
        setHintInfo("请输入查询条件~~!");
    }

    public void queryInfos(IRequestCycle cycle) throws Exception
    {
        IData inputParam = getData("cond", true);
        inputParam.put("SERIAL_NUMBER_A", inputParam.getString("SERIAL_NUMBER_A"));
        inputParam.put("SERIAL_NUMBER_B", inputParam.getString("SERIAL_NUMBER_B"));
        inputParam.put("GROUP_ID", inputParam.getString("GROUP_ID"));
        
        String eparchyCode = getTradeEparchyCode();
        inputParam.put(Route.ROUTE_EPARCHY_CODE,eparchyCode);
        
        String selType = inputParam.getString("ENET_INFO_QUERY","");
        
        IDataset infos = new DatasetList();
        
        IDataOutput dataOutput = null;
        if("1".equals(selType))//按成员号码查询
        {
            dataOutput = CSViewCall.callPage(this, 
                    "SS.UnifyPayRelationQrySVC.qryGrpUnifyPayInfo",
                    inputParam, getPagination("PageNav"));
            
            if (null != dataOutput && dataOutput.getData().size() > 0)
            {
                setHintInfo("查询成功~~！");
                infos = dataOutput.getData();
            }
            else
            {
                setHintInfo("没有符合条件的查询结果~~！");
            }
            
        } 
        else if("0".equals(selType))//按照集团产品编码查询
        {
            String serialNumberA = inputParam.getString("SERIAL_NUMBER_A");
            IData ucaData = UCAInfoIntfViewUtil.qryGrpUCAInfoByGrpSn(this, serialNumberA,false);
            
            if(IDataUtil.isEmpty(ucaData))
            {
                setHintInfo("未获取到集团产品用户的信息~~！");
                return;
            }
            
            IData grpUserInfo = ucaData.getData("GRP_USER_INFO");
            String productId = grpUserInfo.getString("PRODUCT_ID","");
            
            if(!"7345".equals(productId))
            {
                setHintInfo("该集团产品编码不是集团统一付费产品~~！");
                return;
            }
            
            String userId = grpUserInfo.getString("USER_ID","");
            IData params = new DataMap();
            params.put("USER_ID", userId);
            params.put(Route.ROUTE_EPARCHY_CODE,eparchyCode);
            
            IDataset acctInfosList = CSViewCall.call(this, 
                    "SS.UnifyPayRelationQrySVC.qryGrpUnifyPayAcctInfo",
                    params);
            if(IDataUtil.isEmpty(acctInfosList))
            {
                setHintInfo("未获取到该集团产品编码的默认统一付费账户~~！");
                return;
            }
            
            IData acctInfos = acctInfosList.getData(0);
            String acctId = acctInfosList.getData(0).getString("ACCT_ID");
            params.clear();
            params.put("ACCT_ID", acctId);
            params.put(Route.ROUTE_EPARCHY_CODE,eparchyCode);
            
            dataOutput = CSViewCall.callPage(this, 
                    "SS.UnifyPayRelationQrySVC.qryGrpUnifyPayInfoByAcctId",
                    params, getPagination("PageNav"));
            
            if (null != dataOutput && dataOutput.getData().size() > 0)
            {
                setHintInfo("查询成功~~！");
                infos = dataOutput.getData();
                
                for (int i = 0, len = infos.size(); i < len; i++)
                {
                    infos.getData(i).put("GROUP_ID", acctInfos.getString("GROUP_ID",""));
                    infos.getData(i).put("CUST_NAME", acctInfos.getString("CUST_NAME",""));
                    infos.getData(i).put("CUST_ID", acctInfos.getString("CUST_ID",""));
                    infos.getData(i).put("ACCT_ID", acctInfos.getString("ACCT_ID",""));
                    infos.getData(i).put("PAY_NAME", acctInfos.getString("PAY_NAME",""));
                    infos.getData(i).put("BANK_CODE", acctInfos.getString("BANK_CODE",""));
                    infos.getData(i).put("BANK_ACCT_NO", acctInfos.getString("BANK_ACCT_NO",""));
                    infos.getData(i).put("PAY_MODE_CODE", acctInfos.getString("PAY_MODE_CODE",""));
                }
                
            }
            else
            {
                setHintInfo("没有符合条件的查询结果~~！");
            }
            
        }
        
        setCondition(inputParam);
        setInfos(infos);
        setInfoCount(dataOutput.getDataCount());
    }
}
