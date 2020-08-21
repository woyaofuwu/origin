package com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.cfg;

/**
 * 销售品结构属性
 * @author jinnian
 *
 */
public class OfferComChaCfg {

	private String comChaSpecId;
	
	private String fieldName;
	
	private String fieldValue;
	
	public OfferComChaCfg(String comChaSpecId, String fieldName, String fieldValue){
		this.comChaSpecId = comChaSpecId;
		this.fieldName = fieldName;
		this.fieldValue = fieldValue;
	}

	public String getComChaSpecId() {
		return comChaSpecId;
	}

	public String getFieldName() {
		return fieldName;
	}

	public String getFieldValue() {
		return fieldValue;
	}
}
