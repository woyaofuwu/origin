update Tf_f_User_Comp_Rela t set t.end_date = to_date(sysdate,'YYYY-MM-DD HH24:MI:SS')
 where t.partition_id=MOD(:USER_ID,10000)
 and t.cust_id=TO_NUMBER(:USER_ID_ID)
 and to_char(t.start_date,'yyyy-mm-dd hh24:mi:ss')=to_char(:START_DATE,'yyyy-mm-dd hh24:mi:ss')
 and t.comp_product_id=:COMP_PRODUCT_ID
 and t.comp_user_id=:COMP_USER_ID