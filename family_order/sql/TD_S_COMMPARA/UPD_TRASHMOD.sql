UPDATE td_s_commpara SET para_code1=:PARA_CODE1, UPDATE_STAFF_ID=:UPDATE_STAFF_ID,
       UPDATE_DEPART_ID=:UPDATE_DEPART_ID, 
         UPDATE_TIME=to_date(:UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss')
 WHERE subsys_code='CSM' 
   AND param_attr = '1111' 
   AND param_code = 'TRASHMOD'