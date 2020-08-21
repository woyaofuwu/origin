SELECT /*+ first_rows(1)*/
   COUNT(1) recordcount
 from TF_F_USER_SALE_ACTIVE
 where partition_id = mod(to_number(:USER_ID), 10000)
  and user_id = :USER_ID
  and campn_type = :CAMPN_TYPE
  and end_date > sysdate
  and process_tag = '0'
  and rownum < 2