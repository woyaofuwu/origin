 update tf_f_user_other t
    set t.RSRV_VALUE = :RSRV_VALUE,
        t.RSRV_DATE2 = SYSDATE,
        RSRV_STR17   = :RSRV_STR17,
        RSRV_STR18   = TO_CHAR(SYSDATE,'YYYY-MM-DD hh24:mi:ss'),
        RSRV_STR19   = :RSRV_STR19,
        RSRV_STR20   = :RSRV_STR20,
        RSRV_STR30   = :RSRV_STR30,
        end_date     = SYSDATE
  where t.USER_ID = TO_NUMBER(:USER_ID)
    and t.RSRV_VALUE_CODE = 'RED_PAK'
    and RSRV_VALUE = :RSRV_VALUE_COND
    and sysdate < t.end_date