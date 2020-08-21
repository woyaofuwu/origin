INSERT INTO tf_b_trade_discnt(
TRADE_ID, ACCEPT_MONTH, USER_ID, USER_ID_A, PACKAGE_ID, PRODUCT_ID, DISCNT_CODE, SPEC_TAG, RELATION_TYPE_CODE, INST_ID, CAMPN_ID, START_DATE, END_DATE, MODIFY_TAG, UPDATE_TIME, UPDATE_STAFF_ID, UPDATE_DEPART_ID, REMARK, RSRV_NUM1, RSRV_NUM2, RSRV_NUM3, RSRV_NUM4, RSRV_NUM5, RSRV_STR1, RSRV_STR2, RSRV_STR3, RSRV_STR4, RSRV_STR5, RSRV_DATE1, RSRV_DATE2, RSRV_DATE3, RSRV_TAG1, RSRV_TAG2, RSRV_TAG3)
SELECT
 TO_NUMBER(:TRADE_ID),
 TO_NUMBER (SUBSTR (:TRADE_ID,5,2)),
 TO_NUMBER(:USER_ID),
 TO_NUMBER (:USER_ID_A),
 (SELECT b.package_id FROM td_b_product_package a,td_b_package_element b 
          WHERE a.package_id=b.package_id AND a.product_id=TO_NUMBER(:PRODUCT_ID) AND b.element_type_code='D'
          AND b.element_id=TO_NUMBER(:DISCNT_CODE) AND SYSDATE BETWEEN a.start_Date AND a.end_date AND SYSDATE BETWEEN b.start_Date AND a.end_date),
 :PRODUCT_ID,
 TO_NUMBER(:DISCNT_CODE),
 :SPEC_TAG,
 :RELATION_TYPE_CODE,
 TO_NUMBER (:INST_ID),
 to_number(:CAMPN_ID),
 nvl(TO_DATE(:START_DATE,'YYYY-MM-DD HH24:MI:SS'),f_csm_getdiscntstartdate(:DISCNT_CODE,:MODIFY_TAG)),
 nvl(TO_DATE(:END_DATE,'YYYY-MM-DD HH24:MI:SS'),f_csm_getdiscntenddate(:DISCNT_CODE,:MODIFY_TAG)),
 :MODIFY_TAG,
 SYSDATE,
 :UPDATE_STAFF_ID,
 :UPDATE_DEPART_ID,
 :REMARK,
 null,
 null,
 null,
 null,
 null,
 null,
 null,
 null,
 null,
 null,
 null,
 null,
 null,
 null,
 null,
 null
FROM dual
WHERE NOT EXISTS(SELECT 1 FROM tf_b_trade_discnt 
                   WHERE trade_id=TO_NUMBER(:TRADE_ID) AND accept_month=TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
                     AND discnt_code=TO_NUMBER(:DISCNT_CODE)
                     AND modify_tag='0')
  AND NOT EXISTS(SELECT 1 FROM tf_f_user_discnt 
                   WHERE partition_id=MOD(:USER_ID,10000) AND user_id=TO_NUMBER(:USER_ID)
                     AND discnt_code=TO_NUMBER(:DISCNT_CODE)
                     AND end_date>SYSDATE)