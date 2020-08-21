select *from tf_f_grpacct_epostinfo where 1=1 
and USER_ID=:USER_ID
and POST_TAG=:POST_TAG
and partition_id=MOD(TO_NUMBER(:USER_ID),10000)
