SELECT to_char(group_id) group_id,
  to_char(a.user_id) user_id,
   a.role_code,
   to_char(a.START_DATE,'yyyy-mm-dd hh24:mi:ss') START_DATE,
   to_char(a.END_DATE,'yyyy-mm-dd hh24:mi:ss') END_DATE,
   a.UPDATE_TIME,
   a.UPDATE_STAFF_ID,
   a.UPDATE_DEPART_ID,
   a.remark,
   a.RSRV_NUM1,
   a.RSRV_NUM2,
   a.RSRV_NUM3,
   a.RSRV_NUM4,
   a.RSRV_NUM5,
   a.RSRV_STR1,
   a.RSRV_STR2,
   a.RSRV_STR3,
   a.RSRV_STR4,
   a.RSRV_STR5,
   to_char(a.RSRV_DATE1,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1,
   to_char(a.RSRV_DATE2,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2, 
   to_char(a.RSRV_DATE3,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3,
   a.RSRV_TAG1,
   a.RSRV_TAG2,
   b.serial_number
  FROM TF_F_USER_CLUSTER_RELA a  ,tf_f_user b
 WHERE group_id = TO_NUMBER(:GROUP_ID)
   AND a.user_id=b.user_id
 order by a.role_code asc