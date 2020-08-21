
package com.asiainfo.veris.crm.order.soa.person.busi.score.scoreinterface.order.filter;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ailk.common.BaseException;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.Utility;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.plat.order.filter.cibp.CibpExceptionFilter;
import com.asiainfo.veris.crm.order.soa.person.busi.score.scoreinterface.order.requestdata.ItemInfoData;
import com.asiainfo.veris.crm.order.soa.person.busi.score.scoreinterface.order.requestdata.StackPackageData;

/**
 * 移动自有产品兑换错误输出转换
 * 
 * @author huangsl
 */
public class ScoreExchMobilSelfExceptionFilter implements IFilterException
{

    protected static final Logger log = Logger.getLogger(CibpExceptionFilter.class);

    public IData transferException(Throwable e, IData input) throws Exception
    {
        try
        {
        	String error =  Utility.parseExceptionMessage(e); 
            String[] errorArray = error.split(BaseException.INFO_SPLITE_CHAR);
            input.put("X_RESULTCODE", "-1");
            input.put("X_RSPCODE", "2999");
            input.put("X_RESULTINFO", errorArray[1]);
            input.put("X_RSPDESC", errorArray[1]);
            String itemId = "";
            IDataset temp = new DatasetList(input.getString("STACK_PACKAGE"));
            List<StackPackageData> stackPackageDatas = new ArrayList<StackPackageData>();
            List<ItemInfoData> itemDatas = new ArrayList<ItemInfoData>();
            int tempSize = temp.size();
            for (int i = 0; i < tempSize; i++)
            {
                IDataset item = new DatasetList(temp.getData(i).getString("ITEM_INFO"));
                for (int j = 0; j < item.size(); j++)
                {
                    IData itemTemp = new DataMap();
                    itemTemp.putAll(item.getData(j));
                    itemId = itemTemp.getString("ITEM_ID");
                }
            }
            IData exchangeMobilType = new DataMap();
            IDataset exchangeMobilTypes = CommparaInfoQry.getCommpara("CSM", "4502", itemId, CSBizBean.getUserEparchyCode());
            if (IDataUtil.isNotEmpty(exchangeMobilTypes))
            {
                exchangeMobilType = exchangeMobilTypes.getData(0);
            }else{
            	input.put("BUSI_TYPE", "01");
            	input.put("BUSI_RSLT_CODE", "06");
            	input.put("BUSI_RSLT_DESC", "获取兑换的移动自有产品无数据");
            }
            String mobilType = exchangeMobilType.getString("PARA_CODE1");
            
            if(errorArray[1].contains("兑换类型与受理手机号码不匹配")){
            	input.put("BUSI_TYPE", mobilType);
            	input.put("BUSI_RSLT_CODE", "05");
            	input.put("BUSI_RSLT_DESC", "兑换类型与受理手机号码不匹配");
				return input;
            }
            if(errorArray[1].contains("月末最后一天不受理")){
            	input.put("BUSI_TYPE", "02");
            	input.put("BUSI_RSLT_CODE", "03");
            	input.put("BUSI_RSLT_DESC", "月末最后一天不受理");
				return input;
            }
            if(errorArray[1].contains("该用户已开通飞信会员产品，不可以订购")){
            	input.put("BUSI_TYPE", mobilType);
            	input.put("BUSI_RSLT_CODE", "02");
            	input.put("BUSI_RSLT_DESC", "该用户已开通飞信会员产品，不可以订购");
				return input;
            }
            if(errorArray[1].contains("子订单号在台帐或者历史台帐表中已经存在")){
            	input.put("BUSI_TYPE", mobilType);
    			if("99".equals(mobilType)){
    				input.put("BUSI_TYPE", "02");
    			}
    			input.put("BUSI_RSLT_CODE", "06");
    			input.put("BUSI_RSLT_DESC", "子订单号在台帐或者历史台帐表中已经存在");
    			return input;
            }
            if(errorArray[1].contains("已办理过流量共享用户不能办理季包、半年包")){
            	input.put("BUSI_TYPE", mobilType);
    			input.put("BUSI_RSLT_CODE", "01");
    			input.put("BUSI_RSLT_DESC", "已办理过流量共享用户不能办理季包、半年包");
    			return input;
            }else{
            	input.put("BUSI_TYPE", mobilType);
    			input.put("BUSI_RSLT_CODE", "06");
    			input.put("BUSI_RSLT_DESC", "省内订单处理其他错误");
            }
        }
        catch (Exception ex)
        {
            try
            {
                String[] errorMessage = e.getMessage().split("`");
                input.put("X_RESULTCODE", errorMessage[0]);
                input.put("X_RSPCODE", errorMessage[0]);
                input.put("X_RESULTINFO", errorMessage[1]);
                input.put("X_RSPDESC", errorMessage[1]);
            }
            catch (Exception ex2)
            {
                input.put("X_RESULTCODE", "99");
                input.put("X_RSPCODE", "99");
                input.put("X_RESULTINFO", "其它错误");
                input.put("X_RSPDESC", "其它错误");
            }

        }

        return input;
    }

}
