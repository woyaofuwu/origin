SELECT acyc_id,bcyc_id,deal_type,step,channel_no,start_id,end_id,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,to_char(total_num) total_num,to_char(success_num) success_num,to_char(error_num) error_num,status,eparchy_code 
  FROM tf_a_drecv_log
 WHERE bcyc_id=to_number(:BCYC_ID)
   AND deal_type=to_char(:DEAL_TYPE)