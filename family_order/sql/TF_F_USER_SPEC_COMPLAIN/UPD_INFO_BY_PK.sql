UPDATE tf_f_user_spec_complain
   SET type=:TYPE,long_content=:LONG_CONTENT,deal_desc=:DEAL_DESC,rsrv_str1=:RSRV_STR1,rsrv_str2=:RSRV_STR2,rsrv_str3=:RSRV_STR3,rsrv_str4=:RSRV_STR4,rsrv_str5=:RSRV_STR5,rsrv_str6=:RSRV_STR6,rsrv_str7=:RSRV_STR7,rsrv_str8=:RSRV_STR8,rsrv_str9=:RSRV_STR9,rsrv_str10=:RSRV_STR10,remark=:REMARK  
 WHERE user_id=TO_NUMBER(:USER_ID)
   AND in_date=TO_DATE(:IN_DATE, 'YYYY-MM-DD HH24:MI:SS')