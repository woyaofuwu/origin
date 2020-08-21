package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.other.rsaEncryptDecrypt.util;


import java.io.Serializable;


public class Result<T> implements Serializable{
	
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 8447240244835655077L;

	/**前台提示信息*/
    private String message;

    /**结果值*/
    private T value;

    /**操作结果码*/
    private Integer code;
    
    /**具体业务异常码*/
    private String bizCode;

    public enum CodeEnum {
        /**0:正常返回,-1服务异常,1业务异常*/
        SUCCEE(0), ERROR(-1), BizExcep(1);
        public Integer code;

        /** {@linkplain #code} */
        public Integer getCode() {
            return code;
        }

        /** {@linkplain #code} */
        public void setCode(Integer code) {
            this.code = code;
        }

        private CodeEnum(Integer code) {
            this.code = code;
        }

    }
    
    /**判断是否成功,有可能是正常业务异常*/
    public boolean isSucceed() {
        return this.code == CodeEnum.SUCCEE.code;
    }

    /** {@linkplain #message} */
    public String getMessage() {
        return message;
    }

    /** {@linkplain #message} */
    public Result<T> setMessage(String message) {
        this.message = message;
        return this;
    }
    
    public Result<T> setSuccessMessage() {
    	this.message = "操作成功";
    	return this;
    }

    /** {@linkplain #value} */
    public T getValue() {
        return value;
    }

    /** {@linkplain #value} */
    public Result<T> setValue(T value) {
        this.value = value;
        return this;
    }

    /** {@linkplain #code} */
    public Integer getCode() {
        return code;
    }

    /** {@linkplain #code} */
    public Result<T> setCode(CodeEnum code) {
        this.code = code.getCode();
        return this;
    }

    /** {@linkplain #bizCode} */
	public String getBizCode() {
		return bizCode;
	}

	/** {@linkplain #bizCode} */
	public Result<T> setBizCode(String bizCode) {
		this.bizCode = bizCode;
		return this;
	}
	
	/**设置异常信息并设置异常码*/
    public Result<T> setFaildMessage(String message) {
    	this.message = message;
    	this.code = CodeEnum.BizExcep.code;
    	return this;
    }

}
