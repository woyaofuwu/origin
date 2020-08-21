select count(1) recordcount from tf_f_group_contractno a
 WHERE (group_id like '%'||:PARAM0||'%' or :PARAM0 is null)
   AND (contract_no like '%'||:PARAM1||'%' or :PARAM1 is null)