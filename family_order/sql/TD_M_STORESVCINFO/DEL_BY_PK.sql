DELETE FROM td_m_storesvcinfo
 WHERE store_id=to_char(:STORE_ID)
   AND store_svc_id=to_char(:STORE_SVC_ID)