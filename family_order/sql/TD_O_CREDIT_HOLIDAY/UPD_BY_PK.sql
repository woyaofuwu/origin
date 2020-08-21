UPDATE td_o_credit_holiday
   SET start_date=TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS'),end_date=TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS'),day_type=:DAY_TYPE,remark=:REMARK,update_time=sysdate,update_staff_id=:UPDATE_STAFF_ID,update_depart_id=:UPDATE_DEPART_ID  
 WHERE start_date=TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS')