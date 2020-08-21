UPDATE tf_f_cust_vipsimbak
   SET start_date=TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS'),act_tag=DECODE(:ACT_TAG, '0','1','1','0'),update_time=SYSDATE,update_staff_id=:UPDATE_STAFF_ID,update_depart_id=:UPDATE_DEPART_ID,remark=:REMARK  
 WHERE sim_card_no=:SIM_CARD_NO
   AND act_tag=:ACT_TAG