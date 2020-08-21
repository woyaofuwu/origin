
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.widenetmove;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.ailk.bizservice.query.UcaInfoQry;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.SaleActiveException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.ActiveStockInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.SmsSend;
import com.asiainfo.veris.crm.order.soa.person.common.util.WideNetUtil;

public class WidenetMoveSVC extends CSBizService
{

    private static final long serialVersionUID = 1L;

    /**
     * 校验主号码
     * 
     * @author chenzm
     * @param input
     * @throws Exception
     */

    public IData checkSerialNumber(IData input) throws Exception
    {
        WidenetMoveBean widenetMoveBean = BeanManager.createBean(WidenetMoveBean.class);
        IData data = widenetMoveBean.checkSerialNumber(input);
        return data;
    }
    

    public IDataset showProdMode(IData input) throws Exception
    {
        WidenetMoveBean widenetMoveBean = BeanManager.createBean(WidenetMoveBean.class);
        IDataset dataset = widenetMoveBean.showProdMode(input);
        return dataset;
    }
    
    public IDataset getWidenetProductInfo(IData input) throws Exception
    {

    	WidenetMoveBean widenetMoveBean = BeanManager.createBean(WidenetMoveBean.class);
        IDataset dataset = widenetMoveBean.getWidenetProductInfo(input);
        return dataset;
    }

    public IDataset getModelInfo(IData input) throws Exception
    {
        WidenetMoveBean widenetMoveBean = BeanManager.createBean(WidenetMoveBean.class);
        input.put("TAB_NAME", "TF_F_USER_OTHER");
        input.put("SQL_REF", "SEL_USER_OTHER_FTTHMODERM");
        
        IDataset dataset = widenetMoveBean.getModelInfo(input);
        return dataset;
    }

    public IDataset getUserBalance(IData input) throws Exception
    {
        WidenetMoveBean widenetMoveBean = BeanManager.createBean(WidenetMoveBean.class);
        input.put("TAB_NAME", "TF_F_ACCOUNT");
        input.put("SQL_REF", "SEL_ACCTID_DEFAULT_BY_SERIALNUM");
        IDataset dataset = widenetMoveBean.getUserBalance(input);
        return dataset;
    }

    public IDataset getUserProductInfo(IData input) throws Exception
    {
        WidenetMoveBean widenetMoveBean = BeanManager.createBean(WidenetMoveBean.class);
        IDataset dataset = widenetMoveBean.getUserProductInfo(input);
        return dataset;
    }
    
    public IDataset getSaleActiveComm(IData input) throws Exception
    {
    	//add by zhangxing3 for "REQ201807230001线上包年宽带打折套餐的需求"
    	String packageId = input.getString("PACKAGE_ID","");
        //System.out.println("=============choicePackageNode=========PACKAGE_ID:"+packageId);
        if("84015243".equals(packageId) || "84015244".equals(packageId))
        {
        	checkSaleActiveStock(packageId);
        }
        //add by zhangxing3 for "REQ201807230001线上包年宽带打折套餐的需求"
    	
    	WidenetMoveBean widenetMoveBean = BeanManager.createBean(WidenetMoveBean.class);
        IDataset result = widenetMoveBean.getSaleActiveComm(input);
        
        //只有当时查询宽带营销活动时才调用营销包权限过滤接口
        if ("178".equals(input.getString("PARAM_ATTR")))
        {
            result = WideNetUtil.filterWideSaleActiveListByPriv(getVisit().getStaffId(), result);
        }
        
        return result;
    }
    
    /**
     * 校验营销包库存
     * zhangxing3
     */
    public void checkSaleActiveStock(String packageId)throws Exception
    {
    	IDataset result = UpcCall.queryTempChaByOfferTable(BofConst.ELEMENT_TYPE_CODE_PACKAGE, packageId, "TD_B_PACKAGE_EXT");
        //System.out.println("=============checkSaleActiveStock=========result:"+result);

        if (IDataUtil.isEmpty(result))
        {
            CSAppException.apperr(SaleActiveException.CRM_SALEACTIVE_23, packageId);
        }
        IData pkgExtInfo = result.getData(0);
        String condFactor3 = pkgExtInfo.getString("COND_FACTOR3");
        //System.out.println("=============checkSaleActiveStock=========condFactor3:"+condFactor3);

        if (StringUtils.isBlank(condFactor3))
        {
            return;
        }

        if ("ZZZZ".equals(condFactor3))
        {

        }
        else
        {
            result = ActiveStockInfoQry.queryByResKind(condFactor3, CSBizBean.getVisit().getStaffId(), CSBizBean.getVisit().getCityCode(), CSBizBean.getTradeEparchyCode());
            //System.out.println("=============checkSaleActiveStock=========result:"+result);

            if (IDataUtil.isEmpty(result))
            {
                CSAppException.apperr(SaleActiveException.CRM_SALEACTIVE_43);
            }
            IData activeStockInfo = result.getData(0);
            //System.out.println("=============checkSaleActiveStock=========activeStockInfo:"+activeStockInfo);

            int warnningValueU = activeStockInfo.getInt("WARNNING_VALUE_U");
            int warnningValueD = activeStockInfo.getInt("WARNNING_VALUE_D");
            if (warnningValueU >= warnningValueD)
            {
                CSAppException.apperr(SaleActiveException.CRM_SALEACTIVE_44);
            }
        }
    }
    
    public IDataset isUserSaleActive(IData input) throws Exception
    {
        WidenetMoveBean widenetMoveBean = BeanManager.createBean(WidenetMoveBean.class);
        boolean isDeal = widenetMoveBean.isUserSaleActive(input);
        IDataset dataset = new DatasetList();
        IData idata = new DataMap();
        idata.put("IS_DEAL", isDeal);
        dataset.add(idata);
        
        return dataset;
    }
    
    public IDataset getUserInfoBySerial(IData input) throws Exception
    {
        WidenetMoveBean widenetMoveBean = BeanManager.createBean(WidenetMoveBean.class);
        IDataset dataset = widenetMoveBean.getUserInfoBySerial(input);
        return dataset;
    }
    
    public IDataset getStaticInfoOnly(IData input) throws Exception
    {
        WidenetMoveBean widenetMoveBean = BeanManager.createBean(WidenetMoveBean.class);
        IDataset dataset = widenetMoveBean.getStaticInfoOnly(input);
        return dataset;
    }

    public IDataset judgeIsCanMove(IData input) throws Exception
    {
        WidenetMoveBean widenetMoveBean = BeanManager.createBean(WidenetMoveBean.class);
        IDataset dataset = widenetMoveBean.judgeIsCanMove(input);
        return dataset;
    }

    public IDataset getDiscntModelBef(IData input) throws Exception
    {
        WidenetMoveBean widenetMoveBean = BeanManager.createBean(WidenetMoveBean.class);
        IDataset dataset = widenetMoveBean.getDiscntModelBef(input);
        return dataset;
    }

    public IDataset getWideNetMoveInfo(IData input) throws Exception
    {
        WidenetMoveBean widenetMoveBean = BeanManager.createBean(WidenetMoveBean.class);
        IDataset dataset = widenetMoveBean.getWideNetMoveInfo(input);
        return dataset;
    }


    public IDataset queryCheckSaleActiveFee(IData input) throws Exception
    {
        WidenetMoveBean widenetMoveBean = BeanManager.createBean(WidenetMoveBean.class);
        IDataset dataset = new DatasetList();
        IData idata = widenetMoveBean.queryCheckSaleActiveFee(input);
        dataset.add(idata);
        return dataset;
    }

    //add by danglt
    public IDataset getSaleActiveCycle(IData input) throws Exception {
        WidenetMoveBean widenetMoveBean = BeanManager.createBean(WidenetMoveBean.class);
        return widenetMoveBean.getSaleActiveCycle(input);
    }
    //降速接口
    public IData revertFTTHrate(IData input) throws Exception {
    	IData result = new DataMap();
        IDataset widenetMoveList = qryWidenetMoveInfo();
        if(IDataUtil.isNotEmpty(widenetMoveList)){
        	for (int i = 0; i < widenetMoveList.size(); i++) {
        		IData widenetMove = widenetMoveList.getData(i);
        		String userId = widenetMove.getString("USER_ID");
        		String serialNumber = widenetMove.getString("SERIAL_NUMBER");
        		String productId_OLD = widenetMove.getString("PRODUCT_ID");
        		String tradeId = widenetMove.getString("RSRV_STR2");
        		String opdateTime = widenetMove.getString("UPDATE_TIME");
        		
        		String dates = opdateTime.replace("/", "-");
  		        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
  				Date date = sdf.parse(dates);
  				SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
  				String yyyy = formatter.format(date);
  				SimpleDateFormat formatter1 = new SimpleDateFormat("MM");
  				String MM = formatter1.format(date);
        		String yyyyMM_OLD = yyyy+MM;
        		IData wUserInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
	            if (IDataUtil.isNotEmpty(wUserInfo)){
	        		IDataset produclist = UserProductInfoQry.queryMainProductNow(userId);
	        		if(IDataUtil.isNotEmpty(produclist)){
	        			String productId_NEW = produclist.getData(0).getString("PRODUCT_ID");
	        			String YYYYMMNEW = produclist.getData(0).getString("UPDATE_TIME");
	        			String dates_NEW = YYYYMMNEW.replace("/", "-");
	      		        SimpleDateFormat sdf_NEW = new SimpleDateFormat("yyyy-MM-dd");
	      				Date date_NEW = sdf_NEW.parse(dates_NEW);
	      				SimpleDateFormat formatter_NEW = new SimpleDateFormat("yyyy");
	      				String yyyy_NEW = formatter_NEW.format(date_NEW);
	      				SimpleDateFormat formatter1_NEW = new SimpleDateFormat("MM");
	      				String MM_NEW = formatter1_NEW.format(date_NEW);
	            		String yyyyMM_NEW = yyyy_NEW+MM_NEW;
	        			if(productId_OLD.equals(productId_NEW) && yyyyMM_OLD.equals(yyyyMM_NEW)){
	        				
	        				//老产品速率
	            	        String old_rate = WideNetUtil.getWidenetProductRate(productId_OLD);
	            	        changeResMethod(tradeId, userId, serialNumber, old_rate, "102400");
	            	        result.put("X_RESULTCODE", "0");
	            	        widenetMove.put("SERIAL_NUMBER", serialNumber);
	            	        widenetMove.put("RSRV_STR1", "Y");
	            	        Dao.update("TF_F_USER_WIDENET_MOVE", widenetMove, new String[] { "SERIAL_NUMBER" });
	        			}
	        		}
	            }
			}
        }
        return result;
    }
    
    //下发降速短信接口
    public IData revertFTTHratetosms(IData input) throws Exception {
    	IData result = new DataMap();
        IDataset widenetMoveList = qryWidenetMoveInfotosms();
        if(IDataUtil.isNotEmpty(widenetMoveList)){
        	for (int i = 0; i < widenetMoveList.size(); i++) {
        		IData widenetMove = widenetMoveList.getData(i);
        		String userId = widenetMove.getString("USER_ID");
        		String serialNumber = widenetMove.getString("SERIAL_NUMBER");
        		String productId_OLD = widenetMove.getString("PRODUCT_ID");
        		String tradeId = widenetMove.getString("RSRV_STR2");
        		String opdateTime = widenetMove.getString("UPDATE_TIME");
        		String dates = opdateTime.replace("/", "-");
  		        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
  				Date date = sdf.parse(dates);
  				SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
  				String yyyy = formatter.format(date);
  				SimpleDateFormat formatter1 = new SimpleDateFormat("MM");
  				String MM = formatter1.format(date);
        		String yyyyMM_OLD = yyyy+MM;
        		IData wUserInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
	            if (IDataUtil.isNotEmpty(wUserInfo)){
	        		IDataset produclist = UserProductInfoQry.queryMainProductNow(userId);
	        		if(IDataUtil.isNotEmpty(produclist)){
	        			String productId_NEW = produclist.getData(0).getString("PRODUCT_ID");
	        			String YYYYMMNEW = produclist.getData(0).getString("UPDATE_TIME");
	        			String dates_NEW = YYYYMMNEW.replace("/", "-");
	      		        SimpleDateFormat sdf_NEW = new SimpleDateFormat("yyyy-MM-dd");
	      				Date date_NEW = sdf_NEW.parse(dates_NEW);
	      				SimpleDateFormat formatter_NEW = new SimpleDateFormat("yyyy");
	      				String yyyy_NEW = formatter_NEW.format(date_NEW);
	      				SimpleDateFormat formatter1_NEW = new SimpleDateFormat("MM");
	      				String MM_NEW = formatter1_NEW.format(date_NEW);
	            		String yyyyMM_NEW = yyyy_NEW+MM_NEW;
	        			if(productId_OLD.equals(productId_NEW) && yyyyMM_OLD.equals(yyyyMM_NEW)){
    		        		if(serialNumber.startsWith("KD_")){
    		        			serialNumber=serialNumber.substring(3);
    		        		}
              		        IData smsData = new DataMap();
              		        smsData.put("RECV_OBJECT", serialNumber);
              		        smsData.put("RECV_ID", userId);
              		        smsData.put("NOTICE_CONTENT", "尊敬的客户：您参加的FTTH迁移活动100M提速即将到期，如您需要继续使用100M或以上宽带，请致电10086或到厅办理，感谢您的支持。中国移动。");

              		        SmsSend.insSms(smsData);
	        			}
	        		}
	            }
			}
        }
        return result;
    }
    
    public IDataset qryWidenetMoveInfotosms() throws Exception {
    	SQLParser parser = new SQLParser(null);
        parser.addSQL("SELECT * FROM UCR_CRM1.TF_F_USER_WIDENET_MOVE T WHERE to_char(T.UPDATE_TIME,'yyyymm') = to_char(ADD_MONTHS(SYSDATE, -12),'yyyymm') AND T.RSRV_STR1 IS NULL");
        return Dao.qryByParse(parser);  
    }
    
    public IDataset qryWidenetMoveInfo() throws Exception {
    	SQLParser parser = new SQLParser(null);
        parser.addSQL("SELECT * FROM UCR_CRM1.TF_F_USER_WIDENET_MOVE T WHERE to_char(T.UPDATE_TIME,'yyyymm') <= to_char(ADD_MONTHS(SYSDATE, -12),'yyyymm') AND T.RSRV_STR1 IS NULL");
        return Dao.qryByParse(parser);  
    }
    
    public void changeResMethod(String TRADE_ID ,String USER_ID,String SERIAL_NUMBER ,String PRES_RATE,String OLD_PRES_RATE) throws Exception{		
		IData inParam = new DataMap();
		inParam.put("TRADE_ID", TRADE_ID);
		inParam.put("USER_ID", USER_ID);
		inParam.put("SERIAL_NUMBER", SERIAL_NUMBER);
		inParam.put("PRES_RATE", PRES_RATE);
		inParam.put("OLD_PRES_RATE", OLD_PRES_RATE);
		CSAppCall.callNGPf("PF_ORDER_CHANGERES", inParam);
	}
    
	public IDataset getUserProductInfoNew(IData input) throws Exception
    {
        WidenetMoveBean widenetMoveBean = BeanManager.createBean(WidenetMoveBean.class);
        IDataset dataset = widenetMoveBean.getUserProductInfoNew(input);
        return dataset;
    }

    public IDataset checkSPAMDiscntRequirement(IData input) throws Exception
    {
        WidenetMoveBean widenetMoveBean = BeanManager.createBean(WidenetMoveBean.class);
        IDataset dataset = widenetMoveBean.checkSPAMDiscntRequirement(input);
        return dataset;
    }
}
