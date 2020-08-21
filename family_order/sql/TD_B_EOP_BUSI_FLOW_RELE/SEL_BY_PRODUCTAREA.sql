select *
  from (select A.BUSI_CODE, A.RSRV_STR1, A.BUSI_NAME, A.PRIORITY
          from TD_B_EOP_BUSI_FLOW_RELE A, TD_B_EOP_PROD_SPEC B
         where A.BUSI_SPEC_ID = B.BUSI_SPEC_ID
           AND A.BUSI_TYPE = :BUSI_TYPE
           AND B.PROD_SPEC_ID = :PROD_SPEC_ID
           and A.AREA_ID in (:AREA_ID, 'ZZZZ')
           and A.IN_MODE_CODE in (:IN_MODE_CODE, 'Z')
           and sysdate between A.BEGIN_TIME and A.END_TIME
         group by A.BUSI_CODE, A.RSRV_STR1, A.BUSI_NAME, A.PRIORITY) t
 order by t.priority