SELECT COUNT(1) recordcount
  FROM (SELECT F_CSB_ENCRYPT(a.PARAM_CODE, :USER_ID) M
          FROM TD_S_COMMPARA a
         WHERE a.PARAM_ATTR = 3005) b
 WHERE b.M = (select c.USER_PASSWD from tf_f_user c where c.user_id = :USER_ID)