
package com.asiainfo.veris.crm.order.web.frame.csview.group.simpleBusi.destroygroupmember;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.group.common.GroupBaseConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.product.productinfo.ProductInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;

public abstract class SimpleDestroyGroupMember extends GroupBasePage
{

    public abstract IData getInfo();

    /**
     * 作用：页面的初始化
     * 
     * @author luoy 2009-07-29
     * @param cycle
     * @throws Throwable
     */
    public void initial(IRequestCycle cycle) throws Throwable
    {

        /* 端到端判断，页面初始化开始 */
        IData param = getData();
        String ibsysid = param.getString("IBSYSID", "");
        IData condition = new DataMap();

        condition.put("RELA_CODE", "0");
        String productTreeLimitType = param.getString("PRODUCTTREE_LIMIT_TYPE", "1"); // 0为限制limit_products不展示。limit_type=1为只显示limit_products中的产品
        String productTreeLimitProducts = param.getString("PRODUCTTREE_LIMIT_PRODUCTS");

        if (StringUtils.isNotBlank(productTreeLimitProducts))
        {
            condition.put("LIMIT_TYPE", productTreeLimitType);
            condition.put("LIMIT_PRODUCTS", productTreeLimitProducts);

            if (productTreeLimitType.equals("1"))
            {
                String[] productLimitS = productTreeLimitProducts.split(",");
                int limitLen = productLimitS.length;
                String relaCode = "";
                for (int i = 0; i < limitLen; i++)
                {
                    String productId = productLimitS[i];
                    String brandCode = ProductInfoIntfViewUtil.qryBrandCodeStrByProductId(this, productId);
                    if (GroupBaseConst.BB_BRAND_CODE.toString().indexOf(brandCode) >= 0)
                    {
                        relaCode = (relaCode.equals("") || relaCode.equals(GroupBaseConst.RELA_TYPE.BB.getValue())) ? GroupBaseConst.RELA_TYPE.BB.getValue() : GroupBaseConst.RELA_TYPE.ALL.getValue();
                    }
                    else
                    {
                        relaCode = (relaCode.equals("") || relaCode.equals(GroupBaseConst.RELA_TYPE.UU.getValue())) ? GroupBaseConst.RELA_TYPE.UU.getValue() : GroupBaseConst.RELA_TYPE.ALL.getValue();
                    }
                }

                if (StringUtils.isNotBlank(relaCode))
                {
                    condition.put("RELA_CODE", relaCode);
                }
            }

        }

        if (!"".equals(ibsysid))
        {
            IData inData = new DataMap();
            inData.putAll(getData());
            inData.put("NODE_ID", param.getString("NODE_ID", ""));
            inData.put("IBSYSID", param.getString("IBSYSID", ""));
            inData.put("SUB_IBSYSID", param.getString("SUB_IBSYSID", ""));
            inData.put("OPER_CODE", "15");
            IDataset httResultSetDataset = CSViewCall.call(this, "SS.ESOPQcsGrpBusiIntfSvc.getEosInfo", inData);
            if (IDataUtil.isEmpty(httResultSetDataset))
                CSViewException.apperr(GrpException.CRM_GRP_508);

            /* 拼端到端台帐数据，tf_b_trade_ext表保存所需要数据 开始 */
            IData eosData = new DataMap();
            IDataset eos = new DatasetList();
            eosData.put("IBSYSID", param.getString("IBSYSID"));
            eosData.put("SUB_IBSYSID", param.getString("SUB_IBSYSID"));
            eosData.put("NODE_ID", param.getString("NODE_ID"));
            eosData.put("WORK_ID", param.getString("WORK_ID"));
            eosData.put("BPM_TEMPLET_ID", param.getString("BPM_TEMPLET_ID"));
            eosData.put("MAIN_TEMPLET_ID", param.getString("MAIN_TEMPLET_ID"));
            eosData.put("ATTR_CODE", "ESOP");
            eosData.put("ATTR_VALUE", param.getString("IBSYSID"));
            eosData.put("RSRV_STR1", param.getString("NODE_ID"));
            eosData.put("RSRV_STR2", "01");
            eos.add(eosData);

            condition.put("EOS", eos);
            condition.put("ESOP_TAG", "ESOP");
            condition.put("MEM_FINISH", "true");
            condition.put("IBSYSID", ibsysid);

            /* 拼端到端台帐数据，tf_b_trade_ext表保存所需要数据 结束 */
        }

        IData iparam = new DataMap();
        iparam.put("SUBSYS_CODE", "CSM");
        iparam.put("PARAM_ATTR", "7354");
        iparam.put("PARAM_CODE", productId);
        iparam.put("EPARCHY_CODE", Route.getCrmDefaultDb());
        IDataset resultSet = CSViewCall.call(this, "CS.ParamInfoQrySVC.getCommparaByParamattr", iparam);
        if (IDataUtil.isNotEmpty(resultSet)){
        	condition.put("MEB_FILE_SHOW","true");
        }
        
        setCondition(condition);
    }

    /**
     * 校验V网成员本月是否新增了两次
     * @param cycle
     * @throws Throwable
     */
    public void checkBreTipsHelp(IRequestCycle cycle) throws Throwable
    {
    	IData databus = getData();
    	IData resultData = new DataMap();
    	resultData.put("DEL_VPMN_TWO", "FALSE");
    	if(IDataUtil.isNotEmpty(databus)){
    		String mebSn = databus.getString("MebSerialNumber","");
    		String mebUserId = databus.getString("MebUserId","");
    		if(StringUtils.isNotBlank(mebSn) && StringUtils.isNotBlank(mebUserId)){
    			
    			//String startDate =  DiversifyAcctUtil.getFirstTimeThisAcct(mebUserId);
    	        //String endDate = DiversifyAcctUtil.getLastTimeThisAcctday(mebUserId, null);
    	        IData param = new DataMap();
    	        param.put("TRADE_TYPE_CODE", "3034");
    	        param.put("USER_ID", mebUserId);
    	        //param.put("SERIAL_NUMBER", mebSn);
    	        //param.put("START_DATE", startDate);
    	        //param.put("END_DATE", endDate);
    	        
    	        //集团VPMN成员新增
    	        //IDataset ds3034infos = TradeHistoryInfoQry.getInfosByUserIdTradeTypeCode("3034", mebUserId, startDate, endDate); 
    	        IDataset ds3034infos = CSViewCall.call(this, "CS.TradeHistoryInfoQrySVC.getInfosByUserIdTradeTypeCodeFor3037", param);
    	        if (IDataUtil.isNotEmpty(ds3034infos) && ds3034infos.size() > 1)
    	        {
    	             //err = "客户本月办理新增集团次数已达到两次，如取消则当月不能再办理新增集团，是否继续？，点击确定继续办理，点击取消停止办理。 ";
    	             //BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_CHIOCE, "30370000001", err.toString());
    	             resultData.put("DEL_VPMN_TWO", "TRUE");
    	         }
    		}
    	}
    	this.setAjax(resultData);
    }
    
    public abstract void setCondition(IData condition);

    public abstract void setGroupInfo(IData groupUserInfo);

    public abstract void setHidden(String hidden);

    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);

    public abstract void setMebUseInfo(IData info);

    public abstract void setProductCtrlInfo(IData productCtrlInfo);

    public abstract void setProductInfo(IData productInfo);// 产品信息

    public abstract void setUserInfo(IData userInfo);// 用户信息

}
