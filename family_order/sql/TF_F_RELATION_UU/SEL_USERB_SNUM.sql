SELECT A.serial_number_b SERIAL_NUMBER_B
  FROM TF_F_RELATION_UU A, TF_F_USER B
 WHERE A.USER_ID_A = TO_NUMBER(:USER_ID)
   AND A.USER_ID_B = B.USER_ID
   AND A.RELATION_TYPE_CODE = :RELATION_TYPE_CODE
   AND A.ROLE_CODE_B = :ROLE_CODE_B
   AND B.USER_STATE_CODESET = :USER_STATE_CODESET
   AND B.REMOVE_TAG = '0'
   AND sysdate BETWEEN A.START_DATE AND A.END_DATE