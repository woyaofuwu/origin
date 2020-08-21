package com.asiainfo.veris.crm.order.soa.person.busi.cmonline.broadband;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.HwTerminalCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.SaleTerminalLimitInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.tib.SaleActiveInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.createmergewideuser.MergeWideUserCreateSVC;

/**
 * 中移在线
 * 工具类
*/
public class BroadBandUtil {

	/**
	 * 和目营销活动校验
	 * @author zhengkai5
	 * */
	public static void chkHEMU(IData input) throws Exception
	{
		String heMuResNo = input.getString("HEMU_RES_ID");  //资源号    ?  入参中无此值
		
        String staffId = input.getString("STAFF_ID");
        String serialNumber = input.getString("SERIAL_NUMBER");
        String heMuSalePackageId = input.getString("packageId");  //营销活动编码
        String heMuSaleProuctId = input.getString("activityId");  
        
        IDataset hdhkActives = SaleActiveInfoQry.queryHdfkActivesByResNo(input.getString("RES_NO"));

        if (IDataUtil.isNotEmpty(hdhkActives))
        {
        	CSAppException.appError("-1", "在途状态的终端不允许优惠购机！");
        }

        //和目终端校验
        IDataset terminalDataset = HwTerminalCall.getTerminalInfoByTerminalId(heMuResNo, staffId, serialNumber, "");

        IData terminalData = terminalDataset.getData(0);

        if (!"0".equals(terminalData.getString("X_RESULTCODE")))
        {
        	CSAppException.appError("-1", terminalData.getString("X_RESULTINFO"));
        }

        if (!"1".equals(terminalData.getString("TERMINAL_STATE")))
        {
        	CSAppException.appError("-1", "终端状态不正常，不允许销售！");
        }
        
        IData terminalLimit = SaleTerminalLimitInfoQry.queryByPK(heMuSaleProuctId, heMuSalePackageId, "0", terminalData.getString("DEVICE_MODEL_CODE"), "0898");
        
        if (IDataUtil.isEmpty(terminalLimit))
        {
        	CSAppException.appError("-1", "该终端型号与所选营销活动不匹配！");
        }
//    	IData hemuData = new DataMap();
        /*IDataset saleActiveList = CommparaInfoQry.getCommparaByParaCode("CSM", "178", "HEMU", null, null, heMuSaleProuctId, null);
        
        if(IDataUtil.isNotEmpty(saleActiveList))
        {
        	hemuData.put("PRODUCT_ID",saleActiveList.getData(0).getString("PARA_CODE4"));
        	hemuData.put("PACKAGE_ID", saleActiveList.getData(0).getString("PARA_CODE5"));
	    	//标记是宽带开户营销活动
        	hemuData.put("WIDE_USER_CREATE_SALE_ACTIVE", "1");
	        // 预受理校验，不写台账
        	hemuData.put("PRE_TYPE",  BofConst.PRE_TYPE_CHECK);
        	hemuData.put("TRADE_TYPE_CODE", "240");
	        CSAppCall.call("SS.SaleActiveRegSVC.tradeReg4Intf", hemuData);
        }*/
	}
	
	
	/**
	 * 魔百和营销活动检验
	 * @author zhengkai5
	 * */
	public static void chkTopSetBox(IData input) throws Exception
	{
        String productId = input.getString("packageId",""); //营销活动产品id
        String topSetBoxSaleActiveId = input.getString("activityId","");  //魔百和营销活动ID
        if (StringUtils.isNotBlank(topSetBoxSaleActiveId))
        {
            IDataset topSetBoxCommparaInfos = CommparaInfoQry.getCommparaInfoByCode2("CSM", "178", "3800", topSetBoxSaleActiveId, "0898");
            
            if (IDataUtil.isNotEmpty(topSetBoxCommparaInfos))
            {
                IData topSetBoxCommparaInfo = topSetBoxCommparaInfos.first();
                
                //所依赖的宽带1+营销活动虚拟ID
                String paraCode25 = topSetBoxCommparaInfo.getString("PARA_CODE25");
                
                boolean flag = false;
                
                //不为空，则需要校验
                if (StringUtils.isNotBlank(paraCode25))
                {
                    if (StringUtils.isNotBlank(productId))
                    {
                        String paraCode25Array [] = paraCode25.split("\\|");
                        
                        //判断是否选中了依赖的1+营销活动
                        for(int i = 0 ; i < paraCode25Array.length ; i++ )
                        {
                            if (productId.equals(paraCode25Array[i]))
                            {
                                flag = true;
                                break;
                            }
                        }
                        if (!flag)
                        {
                            CSAppException.appError("-1", "请先选择办理宽带1+营销活动才能办理该魔百和营销活动！");
                        }
                    }
                    else
                    {
                        CSAppException.appError("-1", "请先选择办理宽带1+营销活动才能办理该魔百和营销活动！");
                    }
                }
            }
        }
	}
	
	/**
	 * IMS固话营销活动校验
	 * @author zhengkai5
	 * */
	public static void ChkIMS(IData param)
	{
		
	}
	
	//出参封装
	public static IData result(IData param)
	{
		IData result  = new  DataMap();
		IData object = new DataMap();
		IDataset resultlist = new DatasetList();
		String flag = param.getString("flag");
		if("1".equals(flag))
		{
			resultlist.add(param);
			object.put("respCode","0" );
			object.put("respDesc","success" );
			object.put("result",resultlist);
			
			result.put("rtnCode", "0");
			result.put("rtnMsg", "成功");
			result.put("object", object);
		}else {
			resultlist.add(param);
			object.put("respCode","-1" );
			object.put("respDesc","fail" );
			object.put("result",resultlist);
			
			result.put("rtnCode", "-1");
			result.put("rtnMsg", "失败");
			result.put("object", object);
		}
		return result;
	}
	

	/**
	 * 宽带产品校验
	 * @throws Exception 
	 * */
	public static void chkProdeductInfo(IData input) throws Exception
	{
		
		String saleActiveId = input.getString("SALE_ACTIVE_ID");
	    String wideProductId = input.getString("WIDE_PRODUCT_ID");
	        
	    IDataset saleActiveIDataset = CommparaInfoQry.getEnableCommparaInfoByCode12("CSM", "178", "600", wideProductId, saleActiveId, "0898");
	        
	    if (IDataUtil.isNotEmpty(saleActiveIDataset))
        {
            IData saleActiveData = saleActiveIDataset.first();
              
            input.put("PRODUCT_ID",saleActiveData.getString("PARA_CODE4"));
            input.put("PACKAGE_ID", saleActiveData.getString("PARA_CODE5"));
        }
        else
        {
            CSAppException.appError("-1", "该营销活动配置信息不存在，请联系管理员！");
        }
        
        IData retData = new MergeWideUserCreateSVC().checkSelectedDiscnts(input);
        
        if ("-1".equals(retData.getString("resultCode")))
        {
            CSAppException.appError("20160001", "用户已经选择了包年优惠，不能再办理宽带营销活动，如果要办理请先取消包年优惠！");
        }
        
        if ("-2".equals(retData.getString("resultCode")))
        {
            CSAppException.appError("20160002", "用户已经选择了VIP体验套餐，不能再办理宽带营销活动，如果要办理请先取消VIP体验套餐！");
        }
	}
	
	/**
	 * 服务号码校验
	 * @author zhengkai5
	 * */
	public static void chkSerialNumber(String serialNumber) throws Exception
	{
		IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
        if (IDataUtil.isEmpty(userInfo))
        {   // 没有获取到有效的主号码信息！
        	CSAppException.appError("-1", "没有获取到有效的主号码信息！");
        }
        IData productInfo = UcaInfoQry.qryMainProdInfoByUserId(userInfo.getString("USER_ID"));
        if (IDataUtil.isEmpty(productInfo))
        {   // 没有获取到有效的主产品信息！
        	CSAppException.appError("-1", "取得用户主产品信息出错！");
        }
	}
	
	/**
	 * 参数去重
	 * */
	public static IDataset distinct(IDataset param)
	{
		if(IDataUtil.isEmpty(param))
		{
			return null;
		}
		for(int i = 0;i<param.size() - 1 ;i++)
		{
			for(int j = param.size()-1 ; j>i ; j--)
			{
				if(param.getData(j).getString("PARA_CODE5").equals(param.getData(i).getString("PARA_CODE5")))
				{
					param.remove(j);
				}
			}
		}
		return param;
	}
	
	public static IData requestData (IDataset result)
	{
		IData output = new DataMap();
		IData object = new DataMap();
		if(!"-1".equals(result.first().getString("FLAG")))
		{
			result.first().remove("FLAG");
			object.put("respCode", "0");
			object.put("respDesc", "success");
			object.put("result", result);
			
			output.put("rtnCode", "0");
			output.put("rtnMsg", "成功！");
			output.put("object", object);
		}else {
			result.first().remove("FLAG");
			object.put("respCode", "-1");
			object.put("respDesc", "fail");
			object.put("result", result);
			
			output.put("rtnCode", "-1");
			output.put("rtnMsg", "失败！");
			output.put("object", object);
		}
		return output;
	}
	
	public static IData requestData (IData result)
	{
		IData output = new DataMap();
		IData object = new DataMap();
		if(!"-1".equals(result.getString("FLAG")))
		{
			result.remove("FLAG");
			object.put("respCode", "0");
			object.put("respDesc", "success");
			object.put("result", result);
			
			output.put("rtnCode", "0");
			output.put("rtnMsg", "成功！");
			output.put("object", object);
		}else {
			result.remove("FLAG");
			object.put("respCode", "-1");
			object.put("respDesc", "fail");
			object.put("result", result);
			
			output.put("rtnCode", "-1");
			output.put("rtnMsg", "失败！");
			output.put("object", object);
		}
		return output;
	}
	
}
