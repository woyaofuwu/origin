Select a.PARA_CODE2,a.param_attr,a.para_code1,t.open_date From tf_f_user t ,td_s_commpara a where months_between( trunc(sysdate, 'MONTH'),trunc(t.open_date, 'MONTH'))+1>=TO_NUMBER(a.para_code1) 
and t.remove_tag='0'  and a.param_code='realNameScore' and sysdate>=a.start_date and   sysdate<=a.end_date and t.acct_tag=0
and t.user_id=TO_NUMBER(:USER_ID)
 AND partition_id = MOD(TO_NUMBER(:USER_ID),10000)