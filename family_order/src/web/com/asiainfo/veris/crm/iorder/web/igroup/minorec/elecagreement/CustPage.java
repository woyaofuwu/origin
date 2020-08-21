package com.asiainfo.veris.crm.iorder.web.igroup.minorec.elecagreement;

import java.util.List;

import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.client.BizServiceFactory;
import com.ailk.biz.data.ServiceResponse;
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;

public abstract class CustPage extends BusiPage {

    public abstract void setInfo(IData info);
    
    public abstract void setCond(IData cond);
    
    public abstract void setInfos(IDataset infos);
    
    public abstract void setCount(long count);
    
    public void initCusts(IRequestCycle cycle) throws Exception{
        IData data = this.getData("cond");
        
        String areacode = data.getString("AREA_CODE");
        String deptId = data.getString("DEPART_ID");
        if(StringUtils.isNotBlank(areacode)){
            String areaname = pageutil.getStaticValue("TD_M_AREA", "AREA_CODE", "AREA_NAME", areacode);
            data.put("AREA_NAME", areacode+"|"+areaname);
        }else{
            data.put("AREA_NAME", "HAIN|海南省");
        }
        if(StringUtils.isNotBlank(deptId)){
            String departName = pageutil.getStaticValue("TD_M_DEPART", "DEPART_ID", "DEPART_NAME", deptId);
            data.put("DEPART_NAME", departName);
        }else{
            data.put("DEPART_NAME", "全部部门");
        }
        
        setCond(data);
    }
    
    
    public void queryGroupInfoByParam(IRequestCycle cycle) throws Exception {
        IData data = this.getData("cond");
        IData result = this.call("CC.popup.ICustPopupQuerySV.queryGroupPopupByParams", data,getPagination("navbar1"));
        
        IDataset set = result.getDataset("DATAS");
        
        if(set!=null && set.size()>0){
            setCount(result.getLong("X_RESULTCOUNT",0));
            setInfos(set);
        }
    }
    
    public void queryStaffInfoByParam(IRequestCycle cycle) throws Exception {
        IData data = this.getData("cond");
        IData result = this.call("CC.popup.ICustPopupQuerySV.queryStaffPopupByParams", data,getPagination("navbar1"));
        
        IDataset set = result.getDataset("DATAS");
        
        if(set!=null && set.size()>0){
            setCount(result.getLong("X_RESULTCOUNT",0));
            setInfos(set);
        }
    }
    
    public void queryManagerInfoByParam(IRequestCycle cycle) throws Exception {
        IData data = this.getData("cond");
        IData result = this.call("CC.popup.ICustPopupQuerySV.queryManagerPopupByParams", data,getPagination("navbar1"));
//        IData result = CSViewCall.call(this,"http://10.200.138.6:10004/service", "CC.popup.ICustPopupQuerySV.queryManagerPopupByParams",data, getPagination("navbar1")).first();
        IDataset set = result.getDataset("DATAS");
        
        if(set!=null && set.size()>0){
            setCount(result.getLong("X_RESULTCOUNT",0));
            setInfos(set);
        }
    }
    
    public void initPopGroup(IRequestCycle cycle) throws Exception {
        IData in = getData();
        in.put("cond_EPARCHY_CODE", getVisit().getStaffEparchyCode());  //设置归属地州为当前操作员的归属地州
        in.put("EPARCHY_NAME", StaticUtil.getStaticValue(getVisit(), "TD_M_AREA", "AREA_CODE", "AREA_NAME", getVisit().getStaffEparchyCode()));
        in.put("cond_CITY_CODE", getVisit().getCityCode());  //设置归属地州为当前操作员的归属地州
        setCond(in);
    }
    
    public void queryPopGroupInfoByParam(IRequestCycle cycle) throws Exception {
        IData data = this.getData("cond");
        data.put("AREA_CODE", getVisit().getCityCode());
        data.put("REMOVE_TAG", "0");
        IData result = this.call("CC.popup.ICustPopupQuerySV.queryPopGroupInfoByParam", data,getPagination("navbar1"));
        
        IDataset set = result.getDataset("DATAS");
        
        if(set!=null && set.size()>0){
            setCount(result.getLong("X_RESULTCOUNT",0));
            setInfos(set);
        }
    }
    
    public void queryRivalManagerInfo(IRequestCycle cycle) throws Exception {
        IData data = this.getData("cond");

        IData result = this.call("CC.popup.ICustPopupQuerySV.queryRivalManagerInfo", data,getPagination("navbar1"));
        
        IDataset set = result.getDataset("DATAS");
        
        if(set!=null && set.size()>0){
            setCount(result.getLong("X_RESULTCOUNT",0));
            setInfos(set);
        }
    }
    

    public static List getCitys(String areaCode) throws Exception{
        IData data = new DataMap();
        data.put("AREA_CODE", areaCode);
        
        ServiceResponse response = BizServiceFactory.call("CC.community.ICommunityInfoQuerySV.queryTdMAreas", data);
        IData result = response.getBody();
        
        IDataset set = result.getDataset("DATAS");
        
        data = new DataMap();
        data.put("AREA_CODE", areaCode);
        data.put("AREA_TEXT", areaCode + "|" + StaticUtil.getStaticValue(null,"TD_M_AREA", "AREA_CODE", "AREA_NAME", areaCode));
       
        if(set==null || set.size()==0){
            set = new DatasetList();
            set.add(data);
        }else{
            
            IDataset resultList = new DatasetList();
            
            resultList.add(data);
            for (int i = 0; i < set.size(); i++) {
                resultList.add(set.getData(i));
            }
            
            set = resultList;
        }
        
        return set;
    }
    
    public static List getCitys(String eparchyCode ,String isFull) throws Exception {
        IData data = new DataMap();
        data.put("AREA_CODE", eparchyCode);
        
        ServiceResponse response = BizServiceFactory.call("CC.popup.ICustPopupQuerySV.queryTdMAreasByParent", data);
        IData result = response.getBody();
        
        IDataset set = result.getDataset("DATAS");
        
        return set;
    }
    
}
