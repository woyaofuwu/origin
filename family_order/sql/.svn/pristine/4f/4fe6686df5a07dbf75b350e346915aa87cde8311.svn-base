SELECT /*+index (a PK_TF_F_USER_INFOCHANGE)*/count(1) recordcount
  FROM Tf_f_User_Infochange a
     WHERE  user_id=:USER_ID
        AND partition_id=mod(:USER_ID,10000)
        AND sysdate between start_Date and end_Date
          AND start_Date<to_date(:START_DATE,'yyyy-mm-dd hh24:mi:ss')