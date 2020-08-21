select STATE_NAME
     from td_s_servicestate
    where service_id = :SERVICE_ID
      and state_code =
          (select USER_STATE_CODESET from tf_f_user where user_id = :USER_ID)
          AND end_date > SYSDATE