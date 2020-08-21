
package com.asiainfo.veris.crm.order.soa.group.changeuserelement;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.Clone;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.group.common.base.BizData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.util.DataBusManager;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeDataLineAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderBaseBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.GrpInvoker;
import com.asiainfo.veris.crm.order.soa.group.dataline.DatalineUtil;

public class ChangeNetinUserElementBean extends GroupOrderBaseBean
{

    public void actOrderDataOther(IData map) throws Exception
    {
        String mainTradeId = "";
        IDataset attrInternet = new DatasetList(map.getString("PRODUCT_PARAM_INFO"));

        // 修改主用户
        GrpInvoker.ivkProduct(map, BizCtrlType.ChangeUserDis, "CreateUserClass");

        // 获取主用户TRADE
        List<BizData> gbd = DataBusManager.getDataBus().getGrpBizData();

        if (null != gbd && gbd.size() > 0)
        {
            IDataset user = gbd.get(0).getTradeUser();
            mainTradeId = user.getData(0).getString("TRADE_ID");
        }

        // 解析专线数据
        IDataset internet = DatalineUtil.parseDataInfo(attrInternet);

        IDataset commonData = DatalineUtil.parseCommonDataInfo(attrInternet);

        IDataset dataline = DatalineUtil.parseDataLineInfo(attrInternet);

        //校验同一个专线实例是否存在未完工的工单
        if (IDataUtil.isNotEmpty(dataline))
        {
            for (int j = 0; j < dataline.size(); j++)
            {
            	IData lineSet = dataline.getData(j);
            	if(IDataUtil.isNotEmpty(lineSet))
            	{
            		String numberCode = lineSet.getString("PRODUCTNO","");
            		if(StringUtils.isNotBlank(numberCode))
            		{
            			IDataset lineData = TradeDataLineAttrInfoQry.qryDatalineInstanceByProductNo(numberCode);
            			if(IDataUtil.isNotEmpty(lineData))
            			{
            				 CSAppException.apperr(GrpException.CRM_GRP_713, "该专线实例" + numberCode + "存在未完工的工单!");
            			}
            			
            		}
            	}
            }
        }
        
        //合同变更时,dataline是空的
        if(IDataUtil.isEmpty(dataline))
        {
        	if(IDataUtil.isNotEmpty(internet))
        	{
        		for (int j = 0; j < internet.size(); j++)
                {
                	IData lineSet = internet.getData(j);
                	if(IDataUtil.isNotEmpty(lineSet))
                	{
                		String numberCode = lineSet.getString("pam_NOTIN_PRODUCT_NUMBER","");
                		if(StringUtils.isNotBlank(numberCode))
                		{
                			IDataset lineData = TradeDataLineAttrInfoQry.qryDatalineInstanceByProductNo(numberCode);
                			if(IDataUtil.isNotEmpty(lineData))
                			{
                				 CSAppException.apperr(GrpException.CRM_GRP_713, "该专线实例" + numberCode + "存在未完工的工单!!");
                			}
                			
                		}
                	}
                }
        	}
        }
        
        // 生成专线用户
        if (null != internet && internet.size() > 0)
        {
            for (int i = 0; i < internet.size(); i++)
            {
                IData maps = new DataMap();
                IData inter = internet.getData(i);
                String lineNumberCode = inter.getString("pam_NOTIN_PRODUCT_NUMBER");

                if (null != dataline && dataline.size() > 0)
                {
                    for (int j = 0; j < dataline.size(); j++)
                    {
                        IData datas = dataline.getData(j);
                        String numberCode = datas.getString("PRODUCTNO");
                        if (numberCode.equals(lineNumberCode))
                        {
                            maps = (IData) Clone.deepClone(map);
                            maps.put("ATTRINTERNET", inter);
                            maps.put("DATALINE", datas);
                            maps.put("COMMON_DATA", commonData);

                            GrpInvoker.ivkProduct(maps, BizCtrlType.ChangeUserDis, "CreateClass");
                        }
                    }
                }
                else if (null == dataline || dataline.size() < 1)
                {
                    maps = (IData) Clone.deepClone(map);
                    maps.put("ATTRINTERNET", inter);
                    GrpInvoker.ivkProduct(maps, BizCtrlType.ChangeUserDis, "CreateClass");
                }

            }
        }

        // 建立部分依赖关系
        List<BizData> bd = DataBusManager.getDataBus().getGrpBizData();
        for (int i = 0; i < bd.size(); i++)
        {
            IDataset other = bd.get(i).getTradeOther();

            if (null != other && other.size() > 0)
            {

                DatalineUtil.createLimit(mainTradeId, other.getData(0).getString("TRADE_ID"));

            }
        }

    }

    @Override
    protected String setOrderTypeCode() throws Exception
    {
        return "3081";
    }

}
