SELECT a.PARTITION_ID,
       to_char(a.USER_ID) USER_ID,
       a.SERIAL_NUMBER,
       a.BIZ_CODE,
       a.SP_CODE,
       a.PRODUCT_NO,
       a.BIZ_TYPE_CODE,
       a.ORG_DOMAIN,
       a.OPR_SOURCE,
       a.BIZ_STATE_CODE,
       to_char(a.START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE,
       to_char(a.END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE,
       to_char(a.FIRST_DATE, 'yyyy-mm-dd hh24:mi:ss') FIRST_DATE,
       to_char(a.FIRST_DATE_MON, 'yyyy-mm-dd hh24:mi:ss') FIRST_DATE_MON,
       a.GIFT_SERIAL_NUMBER,
       a.GIFT_USER_ID,
       b.BILL_TYPE,
       TRIM(to_char(b.price/1000,'999990.00')) PRICE,
       to_char(a.SUBSCRIBE_ID) SUBSCRIBE_ID,
       a.RSRV_NUM1,
       a.RSRV_NUM2,
       a.RSRV_NUM3,
       to_char(a.RSRV_NUM4) RSRV_NUM4,
       to_char(a.RSRV_NUM5) RSRV_NUM5,
       a.RSRV_STR1,
       a.RSRV_STR2,
       a.RSRV_STR3,
       c.serv_code RSRV_STR4,
       c.cs_tel RSRV_STR5,
       b.biz_type RSRV_STR6,
       b.biz_name RSRV_STR7,
       b.biz_attr RSRV_STR8,
       c.sp_short_name RSRV_STR9,
       c.sp_name RSRV_STR10,
       to_char(a.RSRV_DATE1, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1,
       to_char(a.RSRV_DATE2, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2,
       to_char(a.RSRV_DATE3, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3,
       to_char(a.RSRV_DATE4, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE4,
       to_char(a.RSRV_DATE5, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE5,
       b.biz_desc REMARK,
       a.UPDATE_STAFF_ID,
       a.UPDATE_DEPART_ID,
       to_char(a.UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME
  FROM TF_FH_USER_PLAT_ORDER a, Td_m_Operation_Sp b, Td_m_Corporation_Sp c
 WHERE a.partition_id = MOD(TO_NUMBER(:USER_ID), 10000)
   AND a.user_id = TO_NUMBER(:USER_ID)
   AND a.sp_code = b.sp_code(+)
   AND a.biz_code = trim(b.biz_code(+))
   AND a.sp_code = c.sp_code
   AND EXISTS (SELECT 1
          FROM td_s_commpara
         WHERE param_attr = :PARAM_ATTR
           AND param_code = a.biz_type_code
           AND sysdate BETWEEN start_date AND end_date)