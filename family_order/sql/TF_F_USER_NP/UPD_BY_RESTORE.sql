UPDATE TF_F_USER_NP
   SET PORT_OUT_NETID  = :PORT_OUT_NETID,
       PORT_IN_NETID   = :PORT_IN_NETID,
       NP_TAG          = :NP_TAG,
       APPLY_DATE      = SYSDATE,
       NP_DESTROY_TIME = NULL,
       PORT_IN_DATE    = TO_DATE(TO_CHAR(SYSDATE, 'YYYY-MM-DD'),
                                 'YYYY-MM-DD'),
       PORT_OUT_DATE   = NULL,
       REMARK          = :REMARK
 WHERE USER_ID = TO_NUMBER(:USER_ID)