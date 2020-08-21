SELECT out_group_id paracode,out_group_name paraname
  FROM tf_f_outnetgroup
 WHERE SYSDATE BETWEEN start_date AND end_date