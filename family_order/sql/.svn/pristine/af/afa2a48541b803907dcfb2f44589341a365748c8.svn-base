  select A.TRADE_ID,
          'G' ELEMENT_TYPE_CODE,
          A.GOODS_ID   ELEMENT_ID,
          '' ELEMENT_NAME,
          A.MODIFY_TAG,
          'TF_B_TRADE_SALE_GOODS' TRADE_TAB_NAME,
          A.RSRV_TAG3 DETAIL_STATE_CODE
     from TF_B_TRADE_SALE_GOODS A
    where A.TRADE_ID = :TRADE_ID
      and A.MODIFY_TAG in ('0')