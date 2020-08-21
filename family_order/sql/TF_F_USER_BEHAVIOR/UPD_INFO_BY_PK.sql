UPDATE tf_f_user_behavior
   SET character_type=:CHARACTER_TYPE,cust_aim=:CUST_AIM,title=:TITLE,rsrv_str1=:RSRV_STR1,rsrv_str2=:RSRV_STR2,rsrv_str3=:RSRV_STR3,rsrv_str4=:RSRV_STR4,rsrv_str5=:RSRV_STR5,rsrv_str6=:RSRV_STR6,rsrv_str7=:RSRV_STR7,rsrv_str8=:RSRV_STR8,rsrv_str9=:RSRV_STR9,rsrv_str10=:RSRV_STR10,rsrv_str11=:RSRV_STR11,rsrv_str12=:RSRV_STR12,rsrv_str13=:RSRV_STR13,rsrv_str14=:RSRV_STR14,rsrv_str15=:RSRV_STR15,remark=:REMARK  
 WHERE user_id=TO_NUMBER(:USER_ID)
   AND in_date=TO_DATE(:IN_DATE, 'YYYY-MM-DD HH24:MI:SS')