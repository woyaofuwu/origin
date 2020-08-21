package com.asiainfo.veris.crm.order.soa.person.busi.dreamNetBreakOffOrder;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.BatException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCallIntf;

public class DreamNetBreakOffOrderBean extends CSBizBean {

    public static IDataset dreamNetBreakOffOrder(IData input ,Pagination pagination) throws Exception{
    	
  //    return Dao.qryByCode("TL_B_PLATSVC_STOP_MNG", "SELECT_ALL", input,pagination);
    	return Dao.qryByCodeParser("TL_B_PLATSVC_STOP_MNG", "SELECT_ALL", input,pagination);
    }
    
    //批量导入
    public IData batImportDreamNetList(IData param) throws Exception {
 
        IData result = checkImportFile(param);
        IDataset successes = result.getDataset("SUCCESS");
        if (successes.size() > 0) {
        	importDealAdd(successes); // 批量新增
        }
        return result;
    }
    
    private IData checkImportFile(IData params) throws Exception {
        IDataset succds = new DatasetList();
        IDataset faileds = new DatasetList();
       
        IData fileData = params.getData("FILEDATA");
        String status =  fileData.getString("status");
        IDataset[] datasets = (IDataset[]) fileData.get("right");
        for (int i = 0; i < datasets.length; i++) {
            IDataset dataset = datasets[i];
            
            if (IDataUtil.isEmpty(dataset)) {
                CSAppException.apperr(BatException.CRM_BAT_20);
            }
            if (dataset.size() > 1000) {
                CSAppException.apperr(BatException.CRM_BAT_21, "1000");
            }
            for (int j = 0; j < dataset.size(); j++) {
            	String serviceId = "";
            	String serv_mode = "";
                IData data = dataset.getData(j);
                String spCode = data.getString("SP_CODE", "");
                String bisCode = data.getString("BIZ_CODE", "");
                String endDate = data.getString("END_DATE", "");
                IDataset datas = UpcCallIntf.querySpServiceBySpCodeAndBizCodeAndBizStateCode(data.getString("SP_CODE"), data.getString("BIZ_CODE"));
                if(IDataUtil.isNotEmpty(datas)){
                	IData Idata = datas.getData(0);
                	serviceId = Idata.getString("OFFER_CODE");
                	System.out.println("e3e3e3e3"+serviceId+j);
                    IDataset spServiceInfo = UpcCall.qrySpServiceSpInfo(serviceId,"Z");
                    serv_mode = spServiceInfo.getData(0).getString("SERV_MODE", "");
                }else{
                	System.out.println("cdfdfcszs"+j);
                	data.put("REMARK","错误描述：该数据为非梦网数据！");
                	faileds.add(data);
                    dataset.remove(j);
                    j--;
                    continue;
                }
                
                System.out.println("ddddddffffdddssdddddd"+serv_mode);
                if(!"0".equals(serv_mode)){
                	 System.out.println("dddddrrrrfddddddd"+serv_mode);
                	 data.put("REMARK","错误描述：该数据为非梦网数据！");
                     faileds.add(data);
                     dataset.remove(j);
                     j--;
                     continue;
                }
                if (StringUtils.isEmpty(spCode)) {
                    data.put("REMARK","错误描述：spCode不能为空！");
                    faileds.add(data);
                    continue;
                }
                if (StringUtils.isEmpty(bisCode)) {
                    data.put("REMARK","错误描述：bisCode名称不能为空！");
                    faileds.add(data);
                    continue;
                }
           
                if (dataset.size() > 1) {
                    for (int k = j + 1; k < dataset.size(); k++) {
                        if (StringUtils.equals(spCode, dataset.getData(k).getString("SP_CODE")) && StringUtils.equals(bisCode, dataset.getData(k).getString("BIZ_CODE"))) {
                            dataset.getData(k).put("REMARK", data.getString("REMARK") + "||错误描述：文件中存在重复的号码!");
                            faileds.add(dataset.getData(k));
                            dataset.remove(k);
                            k--;
                            continue;
                        }
                    }
                }
                data.put("SP_CODE", spCode);
                data.put("BIZ_CODE", bisCode);
                data.put("END_DATE", endDate);
                data.put("status", status);
                data.put("START_DATE", data.getString("START_DATE", SysDateMgr.getSysTime()));
               
                succds.add(data);
            }
        }

        IData result = new DataMap();
        result.put("SUCCESS", succds);
        result.put("FAILED", faileds);
        return result;
    }
    // 批量新增导入数据处理
    private void importDealAdd(IDataset dataset) throws Exception {
        IDataset addParams = new DatasetList();
        IDataset updateParams = new DatasetList();
        for (int i = 0; i < dataset.size(); i++) {
            IData data = dataset.getData(i);
            String spCode = data.getString("SP_CODE");
            String bizCode = data.getString("BIZ_CODE");
//            String status = data.getString("status");
            // 构建插入数据库的数据
            IData param = new DataMap();
            IDataset datas = UpcCallIntf.querySpServiceBySpCodeAndBizCodeAndBizStateCode(data.getString("SP_CODE"), data.getString("BIZ_CODE"));
            if(datas.size() > 0){
            	IData Idata = datas.getData(0);
                String offerCode = Idata.getString("OFFER_CODE");
                param.put("SP_CODE", data.getString("SP_CODE"));
                param.put("BIZ_CODE", data.getString("BIZ_CODE"));
                //TRUNC(TO_DATE(:START_DATE, 'YYYY-MM-DD'))
                param.put("END_DATE", data.getString("END_DATE"));
                param.put("START_DATE", data.getString("START_DATE"));
                param.put("STATUS",data.getString("status"));
                param.put("OFFER_CODE",offerCode);
                
             // 先判断SP_CODE是不是已经存在，如果已经存在则添加到更新参数
                IDataset ExistDreamNet = DreamNetBreakOffOrderBean.qryUpdateDreamNet(spCode,bizCode);
               
                // 假如SP_CODE已经存在则添加到更新列表，更新主要是把表中的状态更新
                if (IDataUtil.isNotEmpty(ExistDreamNet)) {
                	//这里不需要判断状态，把状态更新到最新就行
                    updateParams.add(param);
                } else { // 假如用户不存在则添加到新增列表
                    
                    addParams.add(param);
                }
            }
            
         }
        if (IDataUtil.isNotEmpty(addParams)) {
            Dao.insert("TL_B_PLATSVC_STOP_MNG", addParams, Route.getCrmDefaultDb());
        }
        //更新数据
        if (IDataUtil.isNotEmpty(updateParams)) {
        	DreamNetBreakOffOrderBean.updateDreamNet(updateParams);
        }
    }
    
    // 根据sp_code biz_code来查询表中是否已经有该条数据了，如果有该数据则将其状态改变
    public static IDataset qryUpdateDreamNet(String spCode ,String bizCode) throws Exception {
        IData inparams = new DataMap();
        inparams.put("SP_CODE", spCode);
        inparams.put("BIZ_CODE", bizCode);
       
        return Dao.qryByCode("TL_B_PLATSVC_STOP_MNG", "SELECT_SP_BIZ_CODE", inparams);
       
    }
    
    // 梦网包月类业务数据更新
    public static int[] updateDreamNet(IDataset input) throws Exception {
        
        return Dao.executeBatchByCodeCode("TL_B_PLATSVC_STOP_MNG", "BATCH_UPDATE_DREAMNNET", input);
    }
    
   

}