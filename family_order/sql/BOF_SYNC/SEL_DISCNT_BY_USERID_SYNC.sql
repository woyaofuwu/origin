select INST_ID,
       '0' ID_TYPE,
       User_Id,
       Discnt_Code,
       spec_tag,
       decode(User_Id_a, -1, 0, user_id_a),
       '-1' Product_Id,
       '-1' Package_Id,
       Null,
       Null,
       TO_CHAR(START_DATE, 'YYYYMMDDHH24MISS'),
       TO_CHAR(END_DATE, 'YYYYMMDDHH24MISS'),
       TO_CHAR(SYSDATE, 'YYYYMMDDHH24MISS')
  from TF_F_USER_DISCNT b
 WHERE b.USER_ID = :USER_ID
   AND start_date < End_date
   AND SYSDATE < End_date
