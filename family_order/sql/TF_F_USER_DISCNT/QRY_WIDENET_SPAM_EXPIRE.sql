SELECT U.SERIAL_NUMBER,T.*
  FROM UCR_CRM1.TF_F_USER_DISCNT T, UCR_CRM1.TF_F_USER U
 WHERE T.DISCNT_CODE = '80176874'
   AND T.USER_ID = U.USER_ID
   AND U.REMOVE_TAG = '0'
   AND T.END_DATE = TO_DATE('2020/6/30 23:59:59', 'YYYY/MM/DD HH24:MI:SS')
   AND (T.RSRV_TAG1 != 'Y' or T.RSRV_TAG1 is null)