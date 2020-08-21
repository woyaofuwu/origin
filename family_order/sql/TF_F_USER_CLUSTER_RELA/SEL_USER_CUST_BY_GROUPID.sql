select  a.USER_ID,a.SERIAL_NUMBER,a.PRODUCT_ID,a.BRAND_CODE,a.EPARCHY_CODE,a.CITY_CODE,a.NET_TYPE_CODE,
  a.REMOVE_TAG,a.USER_STATE_CODESET,a.IN_DATE,a.OPEN_DATE,b.CUST_ID,b.CUST_NAME,
  b.CUST_TYPE,b.PSPT_TYPE_CODE,b.PSPT_ID,c.GROUP_ID,c.ROLE_CODE,c.START_DATE,'' SERIAL_NUMBER_BAK,'' stand_address,'' detail_address
 from tf_f_user a,tf_f_customer b,tf_f_user_cluster_rela c
 where a.CUST_ID=b.CUST_ID
 and a.USER_ID=c.USER_ID
 and a.NET_TYPE_CODE='00'
 and a.REMOVE_TAG='0'
 and b.CUST_TYPE='0'
 and b.REMOVE_TAG='0'
 and sysdate between c.START_DATE and c.END_DATE
 and c.GROUP_ID=:GROUP_ID
 union all 
 select  a.USER_ID,a.SERIAL_NUMBER as SERIAL_NUMBER,a.PRODUCT_ID,a.BRAND_CODE,a.EPARCHY_CODE,a.CITY_CODE,a.NET_TYPE_CODE,
  a.REMOVE_TAG,a.USER_STATE_CODESET,a.IN_DATE,a.OPEN_DATE,b.CUST_ID,b.CUST_NAME,
  b.CUST_TYPE,b.PSPT_TYPE_CODE,b.PSPT_ID,c.GROUP_ID,c.ROLE_CODE,c.START_DATE,a.SERIAL_NUMBER SERIAL_NUMBER_BAK,e.stand_address,e.detail_address
 from tf_f_user a,tf_f_customer b,tf_f_user_widenet e,tf_f_user_cluster_rela c
 where a.CUST_ID=b.CUST_ID
 and a.USER_ID=c.USER_ID
 AND a.user_id=e.user_id(+)
 and a.NET_TYPE_CODE='04'
 and a.REMOVE_TAG='0'
 and b.CUST_TYPE='0'
 and b.REMOVE_TAG='0'
 and sysdate between c.START_DATE and c.END_DATE
 and c.GROUP_ID=:GROUP_ID