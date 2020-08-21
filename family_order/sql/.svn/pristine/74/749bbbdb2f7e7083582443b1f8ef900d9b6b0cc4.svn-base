SELECT b.serial_number id,id_type,post_name,post_tag,post_content,post_typeset,post_cyc,post_address,post_code,email,fax_nbr,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date 
  FROM TF_F_POSTINFO a,tf_f_user b 
 WHERE b.user_id = a.id
   AND b.serial_number >= :PHONE_CODE_A_START
   AND b.serial_number <= :PHONE_CODE_A_END
   AND b.remove_tag = '0'              
   AND SYSDATE BETWEEN a.start_date AND a.end_date
   AND a.id_type = '1'
   AND a.post_tag = '1'