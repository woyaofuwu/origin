select t.rela_id,
       t.task_type,
       t.task_id,
       t.task_name,
       t.priority,
       t.remark,
       t.condition_id,
       T.VALID_TAG,
       T.ACCEPT_DEPART_ID,
       T.UPDATE_DEPART_ID,
       T.UPDATE_STAFF_ID,
       T.ACCEPT_STAFF_ID,
       TO_CHAR(T.CREATE_DATE, 'YYYY-MM-DD HH24:MI:SS') CREATE_DATE,
       TO_CHAR(T.UPDATE_DATE, 'YYYY-MM-DD HH24:MI:SS') UPDATE_DATE
  from TD_B_EWE_NODE_TASK_RELA T
WHERE T.RELA_ID = :RELA_ID