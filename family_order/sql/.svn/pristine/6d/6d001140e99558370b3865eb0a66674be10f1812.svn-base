SELECT TO_CHAR(activity_id) activity_id,bpm_id,bpm_activity,TO_CHAR(trade_id) trade_id,TO_CHAR(user_id) user_id,staff_id,depart_id,activity_state,is_active,TO_CHAR(start_time,'YYYY-MM-DD HH24:MI:SS') start_time,TO_CHAR(end_time,'YYYY-MM-DD HH24:MI:SS') end_time,exec_result,exec_count
  FROM tl_bpm_activity
 WHERE trade_id = TO_NUMBER(:TRADE_ID)
   AND (bpm_activity = :BPM_ACTIVITY OR :BPM_ACTIVITY IS NULL)
   AND (activity_state = :ACTIVITY_STATE OR :ACTIVITY_STATE IS NULL)
   AND (is_active = :IS_ACTIVE OR :IS_ACTIVE IS NULL)