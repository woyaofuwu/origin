select to_char(CUST_ID) CUST_ID,to_char(USER_ID) USER_ID,CONTRACT_ID,to_char(PRODUCT_ID) PRODUCT_ID,
       STATE,to_char(START_DATE,'yyyy-mm-dd hh24:mi:ss') START_DATE,to_char(END_DATE,'yyyy-mm-dd hh24:mi:ss') END_DATE,
       to_char(UPDATE_TIME,'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME,UPDATE_STAFF_ID	,UPDATE_DEPART_ID	,REMARK	,
       to_char(RSRV_NUM1)	RSRV_NUM1,to_char(RSRV_NUM2) RSRV_NUM2,
       to_char(RSRV_NUM3)	RSRV_NUM3,RSRV_STR1,RSRV_STR2,RSRV_STR3,RSRV_STR4,RSRV_STR5,
       RSRV_STR6,to_char(RSRV_DATE1,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1,to_char(RSRV_DATE2,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2,RSRV_TAG1,RSRV_TAG2 
       from TF_F_CUST_CONTRACT_PRODUCT
       where CONTRACT_ID=:CONTRACT_ID
	     AND CUST_ID=:CUST_ID