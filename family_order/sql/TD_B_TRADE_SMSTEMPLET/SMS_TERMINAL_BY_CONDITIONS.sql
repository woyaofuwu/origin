--IS_CACHE=Y
select TRADE_TYPE_CODE,
       (select a1.trade_type from TD_S_TRADETYPE a1 where a1.trade_type_code=a.trade_type_code) trade_type, 
       a.BRAND_CODE BRAND_CODE,
       DECODE(BRAND_CODE,'ZZZZ','鍏ㄥ搧鐗?,(select a1.BRAND from td_s_brand a1 where a1.BRAND_CODE=a.BRAND_CODE)) BRAND,
       a.PRODUCT_ID PRODUCT_ID,
       DECODE(PRODUCT_ID,'-1','鍏ㄤ骇鍝?,(select a1.PRODUCT_NAME from TD_B_PRODUCT a1 where a1.PRODUCT_ID=a.PRODUCT_ID)) PRODUCT_NAME,     
       CANCEL_TAG,
       DECODE(CANCEL_TAG,'0','姝ｅ父','2','杩旈攒',CANCEL_TAG) CANCEL_TAG_NAME,
       ITEM_INDEX,
       ORDER_NO,
       OBJ_TYPE_CODE,
       DECODE(OBJ_TYPE_CODE,'0','浜у搧','1','链嶅姟', '2','浼樻儬','Z','骞冲彴',OBJ_TYPE_CODE) OBJ_TYPE_NAME,
       OBJ_CODE,
       IN_MODE_CODE,
       SMS_TYPE,
       BIZ_TYPE_CODE,
       ORG_DOMAIN,
       OPERATING_MODE,
       DECODE(OPERATING_MODE,'0','姊︾綉涓氩姟','1','镊湁涓氩姟',OPERATING_MODE) OPERATING_MODE_NAME,
       NOTICE_CONTENT,
       SEND_DELAY,
       to_char(START_DATE,'yyyy-mm-dd hh24:mi:ss') START_DATE,
       END_DATE,
       EPARCHY_CODE,
       DECODE(EPARCHY_CODE,'ZZZZ','鍏ㄥ湴宸?,(SELECT area_name FROM td_m_area a1 where a1.area_code=a.EPARCHY_CODE)) EPARCHY_NAME,
       REMARK,
       UPDATE_STAFF_ID,
       (select a1.STAFF_NAME from TD_M_STAFF a1 where a1.STAFF_ID=a.UPDATE_STAFF_ID) STAFF_NAME, 
       UPDATE_DEPART_ID,
       (select a1.DEPART_NAME from TD_M_DEPART a1 where a1.DEPART_ID=a.UPDATE_DEPART_ID) DEPART_NAME, 
       to_char(UPDATE_TIME,'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME
  from TD_B_TRADE_SMSTEMPLET A
 WHERE 1 = 1
   AND (A.EPARCHY_CODE = :EPARCHY_CODE or A.EPARCHY_CODE='ZZZZ')
   AND A.TRADE_TYPE_CODE = :TRADE_TYPE_CODE
   AND A.BRAND_CODE = :BRAND_CODE
   AND A.PRODUCT_ID = :PRODUCT_ID
   AND A.CANCEL_TAG = :CANCEL_TAG
   AND A.ITEM_INDEX = :ITEM_INDEX
   AND A.OBJ_TYPE_CODE = :OBJ_TYPE_CODE
   AND A.START_DATE = TO_DATE(:START_DATE,'YYYY-MM-DD HH24:MI:SS')