SELECT T.ASYN_ID,
       T.BUSIFORM_ID,
       T.ACCEPT_MONTH,
       T.BUSIFORM_NODE_ID,
       T.NODE_ID,
       T.STATE,
       T.VALID_TAG,
       T.REMARK,
       T.EPARCHY_CODE,
       T.ACCEPT_DEPART_ID,
       T.UPDATE_DEPART_ID,
       T.UPDATE_STAFF_ID,
       T.ACCEPT_STAFF_ID,
       TO_CHAR(T.CREATE_DATE, 'YYYY-MM-DD HH24:MI:SS') CREATE_DATE,
       TO_CHAR(T.UPDATE_DATE, 'YYYY-MM-DD HH24:MI:SS') UPDATE_DATE
  from tf_b_ewe_asyn t
 where t.busiform_id = :BUSIFORM_ID
   and t.node_id = :NODE_ID
   and t.state in ('0', 'S')
union
SELECT T.ASYN_ID,
       T.BUSIFORM_ID,
       T.ACCEPT_MONTH,
       T.BUSIFORM_NODE_ID,
       T.NODE_ID,
       T.STATE,
       T.VALID_TAG,
       T.REMARK,
       T.EPARCHY_CODE,
       T.ACCEPT_DEPART_ID,
       T.UPDATE_DEPART_ID,
       T.UPDATE_STAFF_ID,
       T.ACCEPT_STAFF_ID,
       TO_CHAR(T.CREATE_DATE, 'YYYY-MM-DD HH24:MI:SS') CREATE_DATE,
       TO_CHAR(T.UPDATE_DATE, 'YYYY-MM-DD HH24:MI:SS') UPDATE_DATE
  from tf_b_ewe_asyn t
 where t.busiform_node_id = :BUSIFORM_NODE_ID
   and t.state in ('0', 'S')
