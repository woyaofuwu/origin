UPDATE tf_f_user_other
   SET end_date=TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS')
      WHERE partition_id=TO_NUMBER(:PARTITION_ID)
        AND user_id=TO_NUMBER(:USER_ID)
          AND rsrv_value_code=:RSRV_VALUE_CODE
            AND rsrv_value=:RSRV_VALUE
              AND start_date=TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS')