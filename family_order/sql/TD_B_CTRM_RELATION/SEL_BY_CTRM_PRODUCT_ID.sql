SELECT CTRM_PRODUCT_ID,
       CTRM_PRODUCT_TYPE,
       CONTRACT_ID,
       PRODUCT_ID,
       PACKAGE_ID,
       ELEMENT_ID,
       ELEMENT_TYPE_CODE,
       CHECK_ELEMENT_TYPE,
       CHECK_ELEMENT_ID,
       START_DATE,
       END_DATE,
       EPARCHY_CODE,
       UPDATE_TIME,
       UPDATE_STAFF_ID,
       UPDATE_DEPART_ID,
       REMARK,
       RSRV_STR1,
       RSRV_STR2,
       RSRV_STR3,
       RSRV_STR4,
       RSRV_STR5,
	   RSRV_TAG1,
	   RSRV_TAG2,
       RSRV_TAG3
  FROM TD_B_CTRM_RELATION
 WHERE ctrm_product_id = :CTRM_PRODUCT_ID
   and (eparchy_code = :EPARCHY_CODE OR eparchy_code = 'ZZZZ')
   and sysdate between start_date and end_date