
package com.asiainfo.veris.crm.order.soa.person.busi.nonbossfee;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.nonbossfee.NonBossFeeLogInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ParamInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.StaticInfoQry;

public class NonBossFeeSVC extends CSBizService
{

    private static final long serialVersionUID = 1L;

    /**
     * 非boss费用补录返销、做废
     * REQ201409250007201409非出账业务收款及发票管理需求
     * 2015-03-10 chenxy3
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset cancelNonBossFeeLog(IData input) throws Exception
    {
        IDataset returnSet = new DatasetList();
        NonBossFeeBean bean = BeanManager.createBean(NonBossFeeBean.class);
        IData data = new DataMap();
        IDataset infos = new DatasetList(input.getString("NON_BOSS_INFO", "[]"));
        String cenType=infos.getData(0).getString("CENCEL_TYPE"); 
        String tiType=infos.getData(0).getString("TI_TYPE");
        if("0".equals(tiType)){
        	data=bean.updateNonbossLogChZf(input);
        }else{
	        if(!"".equals(cenType)&&"ZF".equals(cenType)){
	        	data = bean.cancelInvoice(input);//作废发票        	
	        }else if(!"".equals(cenType)&&"CH".equals(cenType)){
	        	data = bean.rePrintByCH(input); //冲红
	        }else{ 
	        	data = bean.cancelNonBossFee(input);//返销
	        } 
        }
        returnSet.add(data);
        return returnSet;
    }

    /**
     * 补录非boss收费
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset insertNonBossFee(IData input) throws Exception
    {
        IDataset returnSet = new DatasetList();
        NonBossFeeBean bean = BeanManager.createBean(NonBossFeeBean.class);
        IData data = bean.insertNonBossFee(input);
        returnSet.add(data);
        return returnSet;
    }

    /**
     * 打印发票
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset printNonBossFeeTrade(IData input) throws Exception
    {
        NonBossFeeBean bean = BeanManager.createBean(NonBossFeeBean.class);
        return bean.printNonBossFeeTrade(input);
    }

    /**
     * 根据条件查询费用项目
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset queryNonBossFeeItems(IData input) throws Exception
    {
        return StaticInfoQry.getStaticValidValueByTypeId("PAY_ITEM_NAME", "0");
    }

    /**
     * 查询非boss费用补录记录
     * REQ201409250007201409非出账业务收款及发票管理需求
     * 用于“非出账业务查询及发票管理”查询
     * chenxy3 2015-3-4
     */
    public IDataset queryNonBossFeeLog(IData input) throws Exception
    {
        String staffId = getVisit().getStaffId();
        String cityCode = getVisit().getCityCode();
        input.put("STAFF_ID", staffId);
        input.put("TRADE_CITY_CODE", cityCode);    
        /*String logId = input.getString("LOG_ID");
        String payName = input.getString("PAY_NAME");
        String feeName = input.getString("FEE_NAME");
        String nonBossFeePrint = input.getString("NON_BOSS_FEE_PRINT");

        String startDate = input.getString("START_DATE");
        String endDate = input.getString("END_DATE");*/
        //return NonBossFeeLogInfoQry.queryNonBossFeeLog(staffId, cityCode, logId, payName, feeName, nonBossFeePrint, startDate, endDate, getPagination(), CSBizBean.getTradeEparchyCode());
        input.put(input.getString("FUZZY_QUERY"), input.getString("FUZZY_COMMENT"));
        return NonBossFeeLogInfoQry.queryNonBossFeeLog(input, getPagination(), CSBizBean.getTradeEparchyCode());
    }

    /**
     * 查询用户全称
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset queryPayNameRemark(IData input) throws Exception
    {
        String payName = input.getString("PAY_NAME");
        return ParamInfoQry.queryPayName("CSM", "9996", "PAY_NAME_REMARK", payName);
    }

    /**
     * 补打发票
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset rePrintNonBossFee(IData input) throws Exception
    {
        NonBossFeeBean bean = BeanManager.createBean(NonBossFeeBean.class); 
        return bean.rePrintNonBossFee(input);
    }
}
