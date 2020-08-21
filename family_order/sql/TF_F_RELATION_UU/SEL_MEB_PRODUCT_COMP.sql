SELECT a.product_id_b, a.product_id_a, a.product_name, to_char(c.USER_ID_A) USER_ID_A, c.SERIAL_NUMBER_A, to_char(c.USER_ID_B) USER_ID_B, c.SERIAL_NUMBER_B, c.RELATION_TYPE_CODE, c.ROLE_TYPE_CODE, c.ROLE_CODE_A, c.ROLE_CODE_B, c.ORDERNO, c.SHORT_CODE, to_char(c.INST_ID) INST_ID, to_char(c.START_DATE,'yyyy-mm-dd hh24:mi:ss') START_DATE, to_char(c.END_DATE,'yyyy-mm-dd hh24:mi:ss') END_DATE, to_char(c.UPDATE_TIME,'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, c.UPDATE_STAFF_ID, c.UPDATE_DEPART_ID, c.REMARK, c.RSRV_NUM1, c.RSRV_NUM2, c.RSRV_NUM3, to_char(c.RSRV_NUM4) RSRV_NUM4, to_char(c.RSRV_NUM5) RSRV_NUM5, c.RSRV_STR1, c.RSRV_STR2, c.RSRV_STR3, c.RSRV_STR4, c.RSRV_STR5, to_char(c.RSRV_DATE1,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1, to_char(c.RSRV_DATE2,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2, to_char(c.RSRV_DATE3,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3, c.RSRV_TAG1, c.RSRV_TAG2, c.RSRV_TAG3
 FROM TD_B_PRODUCT_COMP_RELA a, TF_F_USER b, TF_F_RELATION_UU c
 WHERE a.product_id_a=:PRODUCT_ID_A
 and b.user_id=c.user_id_a
 and b.cust_id=:CUST_ID
 and b.remove_tag='0'
 and c.user_id_b=:USER_ID_B
 and sysdate between c.start_date and c.end_date