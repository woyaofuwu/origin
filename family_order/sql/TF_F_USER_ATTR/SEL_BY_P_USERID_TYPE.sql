SELECT PARTITION_ID, to_char(USER_ID) USER_ID,INST_TYPE, INST_ID, ATTR_CODE, 
ATTR_VALUE, to_char(START_DATE,'yyyy-mm-dd hh24:mi:ss') START_DATE, 
to_char(END_DATE,'yyyy-mm-dd hh24:mi:ss') END_DATE, 
to_char(UPDATE_TIME,'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, 
UPDATE_STAFF_ID, UPDATE_DEPART_ID, REMARK, to_char(RSRV_NUM1) RSRV_NUM1, 
to_char(RSRV_NUM2) RSRV_NUM2, to_char(RSRV_NUM3) RSRV_NUM3, 
to_char(RSRV_NUM4) RSRV_NUM4, to_char(RSRV_NUM5) RSRV_NUM5, RSRV_STR1, 
RSRV_STR2, RSRV_STR3, RSRV_STR4, RSRV_STR5, 
to_char(RSRV_DATE1,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1, 
to_char(RSRV_DATE2,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2, 
to_char(RSRV_DATE3,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3, RSRV_TAG1, 
RSRV_TAG2, RSRV_TAG3, TO_CHAR(RELA_INST_ID) RELA_INST_ID
 FROM TF_F_USER_ATTR attr 
   WHERE USER_ID=TO_NUMBER(:USER_ID)
      AND partition_id=MOD(TO_NUMBER(:USER_ID),10000)
      AND INST_TYPE=:INST_TYPE
	  AND attr.END_DATE > SYSDATE
      AND exists 
      (
          SELECT 1 FROM TF_F_USER_PRODUCT product 
             WHERE product.USER_ID = attr.USER_ID and 
                   product.INST_ID = attr.RELA_INST_ID
                   and product.partition_id = attr.partition_id
				   AND SYSDATE BETWEEN product.start_date AND product.end_date
      )