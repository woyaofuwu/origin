UPDATE tf_f_user_foregift
   SET cust_name='',pspt_id='',rsrv_date1=''  
 WHERE user_id=TO_NUMBER(:USER_ID)
   AND foregift_code=:FOREGIFT_CODE
   AND money=0