SELECT COUNT(1) recordcount
  FROM TF_F_USER_TRANS a
  WHERE  a.user_id=TO_NUMBER(:USER_ID)
   AND a.para_code in (select t.id from td_a_transmode_limit t
                        where t.id=A.PARA_CODE
                        and t.para_code=:PARA_CODE
                        and t.id_type='3' and t.limit_tag='0'
                        and sysdate between t.start_date and t.end_date )
   AND process_tag=:PROCESS_TAG
   AND sysdate< end_date