SELECT COUNT(1) recordcount
  from dual
  where (select nvl(pspt_end_date,to_date('2050-12-31','yyyy-mm-dd'))
         from tf_f_cust_person where cust_id = to_number(:CUST_ID)) < sysdate