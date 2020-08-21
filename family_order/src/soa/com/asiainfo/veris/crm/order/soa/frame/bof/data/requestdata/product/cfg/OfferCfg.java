package com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.cfg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ailk.cache.localcache.ConcurrentLRUMap;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.service.session.SessionManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.util.ArrayUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.exception.impl.BofException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;

public class OfferCfg {
    
    private String categoryId;
    
    private String offerId;
    
    private String offerCode;
    
    private String offerName;
    
    private String description;
    
    private String offerType;
    
    private String brandCode;
    
    private String productMode;
    
    private String netTypeCode;
    
    private String validDate;
    
    private String expireDate;
    
    private boolean comp;
    
    private boolean main;
    
    private String offerLineId;
    
    private String orderMode;
    
    private PriceCfg priceCfg;
    
    private List<OfferComChaCfg> offerComChaCfgs;
    
    private List<OfferChaCfg> offerChaCfgs;
    
    private List<OfferCfg> childrenOfferCfgs;
    
    public static ConcurrentLRUMap<String, Map<String, OfferCfg>> manager = new ConcurrentLRUMap<String, Map<String, OfferCfg>>(800);
    
    private OfferCfg(IData data) throws Exception{
        this.categoryId = data.getString("CATEGORY_ID");
        this.offerId = data.getString("OFFER_ID");
        this.offerName = data.getString("OFFER_NAME");
        this.description = data.getString("DESCRIPTION");
        this.offerLineId = data.getString("OFFER_LINE_ID");
        this.orderMode = data.getString("ORDER_MODE");
        this.offerType = data.getString("OFFER_TYPE");
        this.offerCode = data.getString("OFFER_CODE");
        this.validDate = data.getString("VALID_DATE");
        this.expireDate = data.getString("EXPIRE_DATE");
        this.main = "1".equals(data.get("IS_MAIN")) ? true : false;
        if(data.containsKey("IS_MAIN_COM")){
        	this.main = "1".equals(data.get("IS_MAIN_COM")) ? true : false;
        }
        this.comp = "1".equals(data.get("IS_COMP")) ? true : false;
        
        String sessionId = String.valueOf(SessionManager.getInstance().getId());
        Map<String, OfferCfg> cache = manager.get(sessionId);
        if(cache == null){
            cache = new HashMap<String, OfferCfg>();
            manager.put(sessionId, cache);
        }
        cache.put(offerCode+"-"+offerType, this);
    }
    
    public static OfferCfg getInstance(String offerCode,String offerType) throws Exception{
        String sessionId = String.valueOf(SessionManager.getInstance().getId());
        Map<String, OfferCfg> cache = manager.get(sessionId);
        if(cache == null){
            cache = new HashMap<String, OfferCfg>();
            manager.put(sessionId, cache);
        }
        
        OfferCfg cfg = cache.get(offerCode+"-"+offerType);
        if(cfg != null){
            return cfg;
        }
        
        IData offer = UpcCall.queryOfferByOfferId(offerType, offerCode);
        if(IDataUtil.isNotEmpty(offer)){
            offer.put("OFFER_TYPE", offerType);
            offer.put("OFFER_CODE", offerCode);
            cfg = new OfferCfg(offer);
            cache.put(offerCode+"-"+offerType, cfg);
        }
        else{
        	CSAppException.apperr(BofException.CRM_BOF_023, "element_id="+offerCode+",element_type="+offerType);
        }
        
        return cfg;
    }

    public String getOfferId() {
        return offerId;
    }
    
    public String getOfferCode() {
        return offerCode;
    }

    public String getOfferType() {
        return offerType;
    }

    public String getOfferName() {
        return offerName;
    }
    
    public String getBrandCode() throws Exception{
        if(this.brandCode == null){
            this.getOfferComChaCfgs();
        }
        if(this.brandCode == null){
            this.brandCode = "";
        }
        return brandCode;
    }
    
    public String getProductMode() throws Exception{
        if(this.productMode == null){
            this.getOfferComChaCfgs();
        }
        if(this.productMode == null){
            this.productMode = "";
        }
        return this.productMode;
    }
    
    public String getNetTypeCode() throws Exception{
        if(this.netTypeCode == null){
            this.getOfferComChaCfgs();
        }
        if(this.netTypeCode == null){
            this.netTypeCode = "";
        }
        return this.netTypeCode;
    }
    

    public boolean isComp() {
        return comp;
    }

    public boolean isMain() {
        return main;
    }

    public String getOfferLineId() {
        return offerLineId;
    }

    public String getOrderMode() {
        return orderMode;
    }
    
    public String getValidDate() {
        return validDate;
    }
    
    public String getExpireDate() {
        return expireDate;
    }
    
    public PriceCfg getPriceCfg() throws Exception{
        if(this.priceCfg != null){
            return this.priceCfg;
        }
        
        //调用产商品中心服务
        IData priceParam = new DataMap();
        priceParam.put("OFFER_ID", this.offerId);
        IData result = null;//UpcCall.call("UPC.Out.ProdQueryFSV.queryProdSpecByOfferId", priceParam);
        IDataset sp = result.getDataset("OUTDATA");
        if(ArrayUtil.isEmpty(sp)){
            //BizException.bizerr(CisfException.CRM_CISF_032);
        }
        this.priceCfg = new PriceCfg();
        IData data = sp.getData(0);
        this.priceCfg.setPriceId(data.getString("PRICE_ID"));
        this.priceCfg.setPriceName(data.getString("PRICE_NAME"));
        this.priceCfg.setFee(data.getString("FEE"));
        return this.priceCfg;
    }
    
    public List<OfferComChaCfg> getOfferComChaCfgs() throws Exception{
        if(this.offerComChaCfgs != null){
            return this.offerComChaCfgs;
        }
        this.offerComChaCfgs = new ArrayList<OfferComChaCfg>();
        
        IDataset sp = UpcCall.queryOfferComChaByCond(offerType, offerCode);
        
        if(ArrayUtil.isEmpty(sp)){
            return this.offerComChaCfgs;
        }
        
        for(int i = 0,size = sp.size(); i < size ;i++){
            IData data = sp.getData(i);
            OfferComChaCfg comCha = new OfferComChaCfg(data.getString("COM_CHA_SPEC_ID"), data.getString("FIELD_NAME"), data.getString("FIELD_VALUE"));
            if("BRAND_CODE".equals(comCha.getFieldName())){
                this.brandCode = comCha.getFieldValue();
            }
            else if("PRODUCT_MODE".equals(comCha.getFieldName())){
                this.productMode = comCha.getFieldValue();
            }else if("NET_TYPE_CODE".equals(comCha.getFieldName())){
                this.netTypeCode = comCha.getFieldValue();
            }
            this.offerComChaCfgs.add(comCha);
        }
        return this.offerComChaCfgs;
    }
    
    public List<OfferChaCfg> getOfferChaCfgs() throws Exception{
        if(this.offerChaCfgs != null){
            return this.offerChaCfgs;
        }
        
        this.offerChaCfgs = new ArrayList<OfferChaCfg>();
        IData chaParams = new DataMap();
        chaParams.put("OFFER_TYPE", this.offerType);
        chaParams.put("OFFER_CODE", this.offerCode);
        
        IDataset sp = UpcCall.queryOfferChaByCond(offerType, offerCode);
        
        if(ArrayUtil.isEmpty(sp)){
            return this.offerChaCfgs;
        }
        for(int i = 0,size = sp.size(); i < size ;i++){
            IData data = sp.getData(i);
            OfferChaCfg cha = new OfferChaCfg();
            cha.setChaSpecId(data.getString("CHA_SPEC_ID"));
            cha.setDefaultValue(data.getString("DEFAULT_VALUE"));
            cha.setIsNull("1".equals(data.getString("IS_NULL")) ? true : false);
            cha.setMinValue(data.getString("MIN_VALUE"));
            cha.setMaxValue(data.getString("MAX_VALUE"));
            cha.setValueType(data.getString("VALUE_TYPE"));
            this.offerChaCfgs.add(cha);
        }
        return this.offerChaCfgs;
    }
    
    public List<OfferCfg> getChildrenOfferCfgs() throws Exception{
        if(this.childrenOfferCfgs != null){
            return this.childrenOfferCfgs;
        }
        
        if(!this.isComp()){
            return null;
        }
        
        IDataset sp = UpcCall.queryOfferComRelOfferByOfferId(offerType, offerCode);
        
        this.childrenOfferCfgs = new ArrayList<OfferCfg>();
        if(ArrayUtil.isEmpty(sp)){
            return this.childrenOfferCfgs;
        }
        for(int i = 0 ;i < sp.size(); i++){
            IData data = sp.getData(i);
            OfferCfg childOfferCfg = new OfferCfg(data);
            this.childrenOfferCfgs.add(childOfferCfg);
        }
        return this.childrenOfferCfgs;
    }
    
    public OfferCfg findChild(String offerType, String offerCode) throws Exception{
    	List<OfferCfg> children = this.getChildrenOfferCfgs();
    	for(OfferCfg offerCfg : children){
    		if(offerType.equals(offerCfg.getOfferType()) && offerCode.equals(offerCfg.getOfferCode())){
    			return offerCfg;
    		}
    	}
    	return null;
    }
    
    public OfferCfg getChildOfferCfg(String childOfferId) throws Exception{
        if(!this.isComp()){
            return null;
        }
        
        if(this.childrenOfferCfgs == null){
            this.getChildrenOfferCfgs();
        }
        
        for(OfferCfg childOfferCfg : this.childrenOfferCfgs){
            if(childOfferId.equals(childOfferCfg.getOfferId())){
                return childOfferCfg;
            }
        }
        
        return null;
    }
    
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getCategoryId()
    {
        return categoryId;
    }
    
    public IData toData() throws Exception
    {
        IData data = new DataMap();
        data.put("CATEGORY_ID", this.categoryId);
        data.put("OFFER_ID", this.offerId);
        data.put("OFFER_NAME", this.offerName);
        data.put("DESCRIPTION", this.description);
        data.put("OFFER_LINE_ID", this.offerLineId);
        data.put("ORDER_MODE", this.orderMode);
        data.put("OFFER_TYPE", this.offerType);
        data.put("OFFER_CODE", this.offerCode);
        data.put("IS_MAIN", this.main);
        data.put("IS_COMP", this.comp);

        return data;
    }
}
