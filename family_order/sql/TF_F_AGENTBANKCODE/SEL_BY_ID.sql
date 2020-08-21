SELECT depart_id,depart_code,cardcode,cardname,staff_id,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,state,prevalue1,prevalue2 
  FROM tf_f_agentbankcode
 WHERE depart_id=:DEPART_ID