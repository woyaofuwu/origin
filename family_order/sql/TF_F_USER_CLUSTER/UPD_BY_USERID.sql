UPDATE TF_F_USER_CLUSTER
    SET LEADER_USER_ID   = :USER_ID,
        UPDATE_TIME      =  SYSDATE,
        UPDATE_STAFF_ID  = :UPDATE_STAFF_ID,
        UPDATE_DEPART_ID = :UPDATE_DEPART_ID
  WHERE GROUP_ID = TO_NUMBER(:GROUP_ID)
  AND sysdate between start_date AND end_date