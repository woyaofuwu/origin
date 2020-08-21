SELECT m.SIGN_ID RSRV_STR6,
       m.BANK_ACCT_ID RSRV_STR7,
       m.BANK_ACCT_TYPE RSRV_STR8,
       m.BANK_ID RSRV_STR9,
       m.PAY_TYPE,
       m.RECH_THRESHOLD RSRV_STR10,
       m.RECH_AMOUNT RSRV_STR11,
       u.prepay_tag,
       to_char(m.APPLY_DATE,'yyyy-mm-dd hh24:mi:ss') APPLY_DATE,
       to_char(m.START_DATE,'yyyy-mm-dd hh24:mi:ss') START_DATE,
       to_char(m.END_DATE,'yyyy-mm-dd hh24:mi:ss') END_DATE,
       m.ID_TYPE RSRV_STR14,
       m.ID_VALUE RSRV_STR15,
       m.UPDATE_STAFF_ID,
       m.UPDATE_DEPART_ID,
       m.REMARK,
       m.RSRV_STR1,
       m.RSRV_STR2,
       m.RSRV_STR3,
       m.RSRV_STR4,
       m.RSRV_STR5
  FROM Tf_f_Bank_Mainsign m,
       (select *
          from tf_f_user
         where serial_number = :RSRV_STR13
           and open_date = (select max(open_date)
                              from tf_f_user
                             where serial_number = :RSRV_STR13)) u
 WHERE m.user_type = :RSRV_STR12
   and m.user_value = :RSRV_STR13
   AND m.start_date = to_date(:START_DATE, 'yyyy-mm-dd hh24:mi:ss')