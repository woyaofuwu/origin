select user_id,cust_id,serial_number,eparchy_code,
user_passwd,f_csb_encrypt((select tag_info from td_s_tag where tag_code='CS_INF_DEFAULTPWD' and rownum=1),user_id) deposit_name  from   tf_f_user
where  user_id=TO_NUMBER(:USER_ID)
  AND  partition_id=MOD(TO_NUMBER(:USER_ID),10000)