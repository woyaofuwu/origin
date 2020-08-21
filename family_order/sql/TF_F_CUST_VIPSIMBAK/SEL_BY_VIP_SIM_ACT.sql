SELECT vip_id,sim_card_no,sim_type_code,imsi,to_char(send_date,'yyyy-mm-dd hh24:mi:ss') send_date,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,act_tag,client_info1,client_info2,client_info3,client_info4,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,update_staff_id,update_depart_id,remark 
  FROM tf_f_cust_vipsimbak
 WHERE vip_id=:VIP_ID
   AND sim_card_no=:SIM_CARD_NO
   AND act_tag=:ACT_TAG