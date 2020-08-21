SELECT u.partition_id,to_char(u.user_id) user_id,to_char(u.cust_id) cust_id,
to_char(u.usecust_id) usecust_id,u.eparchy_code,u.city_code,u.CITY_CODE_A,
u.user_passwd,u.USER_DIFF_CODE,u.user_type_code,u.USER_TAG_SET,u.USER_STATE_CODESET,
u.NET_TYPE_CODE,u.serial_number,
u.acct_tag,u.prepay_tag,u.MPUTE_MONTH_FEE,
to_char(u.MPUTE_DATE, 'yyyy-mm-dd hh24:mi:ss') MPUTE_DATE,
to_char(u.first_call_time, 'yyyy-mm-dd hh24:mi:ss') first_call_time,
to_char(u.last_stop_time, 'yyyy-mm-dd hh24:mi:ss') last_stop_time,
to_char(u.CHANGEUSER_DATE, 'yyyy-mm-dd hh24:mi:ss') CHANGEUSER_DATE,IN_NET_MODE,
to_char(u.in_date, 'yyyy-mm-dd hh24:mi:ss') in_date,u.IN_STAFF_ID,u.IN_DEPART_ID,u.OPEN_MODE,
to_char(u.open_date, 'yyyy-mm-dd hh24:mi:ss') open_date,u.OPEN_STAFF_ID,u.OPEN_DEPART_ID,
u.DEVELOP_STAFF_ID,to_char(u.DEVELOP_DATE, 'yyyy-mm-dd hh24:mi:ss') DEVELOP_DATE,u.DEVELOP_DEPART_ID,u.DEVELOP_CITY_CODE,
u.DEVELOP_EPARCHY_CODE,
u.DEVELOP_NO,to_char(u.ASSURE_CUST_ID) ASSURE_CUST_ID,u.ASSURE_TYPE_CODE,
to_char(u.ASSURE_DATE, 'yyyy-mm-dd hh24:mi:ss') ASSURE_DATE,remove_tag,
to_char(u.PRE_DESTROY_TIME, 'yyyy-mm-dd hh24:mi:ss') PRE_DESTROY_TIME,
to_char(u.destroy_time, 'yyyy-mm-dd hh24:mi:ss') destroy_time,
u.REMOVE_EPARCHY_CODE,u.REMOVE_CITY_CODE,u.REMOVE_DEPART_ID,u.REMOVE_REASON_CODE,
to_char(u.update_time, 'yyyy-mm-dd hh24:mi:ss') update_time,u.UPDATE_STAFF_ID,
u.UPDATE_DEPART_ID,u.REMARK,u.RSRV_NUM1,u.RSRV_NUM2,u.RSRV_NUM3,to_char(u.RSRV_NUM4) RSRV_NUM4,
to_char(u.RSRV_NUM5) RSRV_NUM5,u.rsrv_str1,u.rsrv_str2,u.rsrv_str3,u.rsrv_str4,u.rsrv_str5,u.rsrv_str6,
u.rsrv_str7,u.rsrv_str8,u.rsrv_str9,u.rsrv_str10,
to_char(u.RSRV_DATE1, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1,
to_char(u.RSRV_DATE2, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2,
to_char(u.RSRV_DATE3, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3,u.RSRV_TAG1,u.RSRV_TAG2,u.RSRV_TAG3,
p.user_id_a,p.product_id,p.product_mode,p.brand_code
  FROM tf_f_user u,tf_f_user_product p 
 WHERE u.user_id = TO_NUMBER(:USER_ID)
   AND u.partition_id = MOD(TO_NUMBER(:USER_ID), 10000)
   and u.remove_tag = :REMOVE_TAG
   and u.user_id = p.user_id
   and p.partition_id = u.partition_id