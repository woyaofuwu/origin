select nvl(sum(a.rsrv_str3),0) haved_num from tf_f_user_other a
where a.partition_id=mod(:USER_ID,10000)
and a.user_id=:USER_ID
and a.rsrv_value_code='GET_GGCARD_NUM'
and a.rsrv_Str1=:PRODUCT_ID
and a.rsrv_Str2=:PACKAGE_ID
and a.end_date>sysdate