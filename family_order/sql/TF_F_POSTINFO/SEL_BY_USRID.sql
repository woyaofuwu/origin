SELECT to_char(id) id,id_type,post_name,post_tag,post_content,post_typeset,post_cyc,post_address,post_code,email,fax_nbr,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date 
  FROM tf_f_postinfo
 WHERE id=TO_NUMBER(:ID)
   AND id_type=:ID_TYPE
   AND sysdate BETWEEN start_date AND end_date