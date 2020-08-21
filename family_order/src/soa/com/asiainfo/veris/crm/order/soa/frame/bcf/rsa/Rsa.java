package com.asiainfo.veris.crm.order.soa.frame.bcf.rsa;

import org.bouncycastle.util.encoders.Base64;

import javax.crypto.Cipher;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class Rsa {
    public static class Keys {
        private String privateKey;
        private String publicKey;

        public Keys(String privateKey, String publicKey) {
            this.privateKey = privateKey;
            this.publicKey = publicKey;
        }

        public String getPrivateKey() {
            return privateKey;
        }

        public String getPublicKey() {
            return publicKey;
        }
    }

    public static class Generator {
        public static Keys generate() {
            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
            try {
                KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", "BC");
                generator.initialize(2048, new SecureRandom());
                KeyPair pair = generator.generateKeyPair();
                PublicKey publicKey = pair.getPublic();
                PrivateKey privateKey = pair.getPrivate();
                return new Keys(new String(Base64.encode(privateKey.getEncoded())), new String(Base64.encode(publicKey.getEncoded())));
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (NoSuchProviderException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public static class Encoder {
        private PrivateKey mPrivateKey;
        private Cipher cipher;

        public Encoder(String privateKey) {
            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
            PKCS8EncodedKeySpec privatePKCS8 = new PKCS8EncodedKeySpec(Base64.decode(privateKey.getBytes()));
            try {
                KeyFactory keyFactory = KeyFactory.getInstance("RSA", "BC");
                mPrivateKey = keyFactory.generatePrivate(privatePKCS8);
                cipher = Cipher.getInstance("RSA", "BC");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public String encode(String source) {
            try {
                cipher.init(Cipher.ENCRYPT_MODE, mPrivateKey);
                byte[] cipherText = cipher.doFinal(source.getBytes("utf-8"));
                return new String(Base64.encode(cipherText));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public static class Decoder {
        private PublicKey mPublicKey;
        private Cipher cipher;

        public Decoder(String publicKey) {
            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
            X509EncodedKeySpec publicX509 = new X509EncodedKeySpec(Base64.decode(publicKey.getBytes()));
            try {
                KeyFactory keyFactory = KeyFactory.getInstance("RSA", "BC");
                mPublicKey = keyFactory.generatePublic(publicX509);
                cipher = Cipher.getInstance("RSA", "BC");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public String decode(String source) {
            try {
                cipher.init(Cipher.DECRYPT_MODE, mPublicKey);
                byte[] output = cipher.doFinal(Base64.decode(source.getBytes()));
                return new String(output, "utf-8");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

//    public static void main(String[] args) {
//    	/*Rsa r=new Rsa();
//    	Keys key=new Generator().generate();
//    	String pri=key.getPrivateKey();
//    	String pub=key.getPublicKey();*/
//        String s = "您好！";
//    	/*System.out.println(pri);
//    	System.out.println("-----");
//    	System.out.println(pub);*/
//        String pri = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQChHtRZtav9QPFLxPQq2rujxUWpsZ8GPZg1xzEfrz2tWzpAOd43SJywwMMUIETpm8VOGl4pmXf5HJIgf6cskJTILx1+C4HAFDkL8OuQ1iaAEBTSk4NDwLKrkLwGgmlJaCCJVQ6Ejw6dETTRUDGTjmNT2Y96H1pYK62EjZ9wcthdpH3054q5tiyQJY3aYgp3nVq80p+63R7Uw/TRD1pmydyJEbLzBtg6O0eMA98NNlXTMkR+tA2z6vD7MXxOUt0OaGNcaz6eKzbKwhxIrdkJSLpdtOWmKjornSTZAvkrMdLK03XXVcjvfb9n2T/GTB5/bHRP5wzTi/W5QHxMhoyYxrkTAgMBAAECggEAHNNUMHyVOaj9wo2JFYWunl0z2mlBxy8L5Usu2blTcolowYbY39Eo32KNRDOFwLmysgd7ozumwDXBWvkboph3Vd1ADIXof8Hedulya6Y0myLFZusnR97Y2GL7kLqSNaTgdVF3WHXzqlwis/QB+qE12hGJXtLvKekekSF5TffuB8qWZRUTomkFFt+k//9OH6twp+hlwBKLiUAcFkZzYCyNdULR43Wm6Siwhm/lL40FcLv0H9HjcAMEQLaR87aTzW+CHiRVPEFLldBcoydh22GYCql29x+eeVgID+XiIsMufS2gDKr0lqFaq+KFbGtmI8DjpTlrauZVQzPcDi7PLH8aqQKBgQDNxME+gxTJV6o4xw8lGz9TqLVPamSG1V2yg1Cw7+tQwcXuSrQa6ugC+jES/RUZSd+xjcBiQRSV5y2eG8LoiA5X3zXGyQ05TNHTPN33hduwPoaUEvTrYbs5If3nRZ1t++YMwBqvCjLL6kSl5ojj3LGfDLTmB3LrHuNDWcE2jRE0PQKBgQDIc9h3f7y2YzG71SgZabIDxirFT5HGyq3HXyJtftF/W/Sa1wN/OA8s68TgHlE0jPXORK8KcvrxeXrpnD9mQZc19pgl6+ZiHvwswqAFb1nWp3pDrQvrfWNXjZSlwyQY/m9cEkvdJD2VE5LelgMlWDBgflHzADLNN3+gcGej1R5njwKBgBy8HUBdjcmQNHU5VyQXagCEzs0IToGFyk/jhqEu3+2nIbzlMcGQjFXeGnxMW2XsqxBgez09WWKVpgkuV0mhtl8PDLN14CLgV2zoUxb92nACS0jiXNGCFGMmHA7v6cwyIS4mpZNMGUvgqzV/vB4V87gCTkDRSXsMFTCSmCjGCmEBAoGBALJOGedyQLMcWUjzus+gLTEePT12If3qm9oUzdMIU+IuMc7qI7oua5FRx7Z0QVe1a5Enl2x8CqxxmtvimKKlBZSC3aQdyrjNRxOprB4phohiQWehrlCzIILo9ajdhGaXLQeBXuo/KmhJGQPV/MZjQ+UReGPncUkKbQSR+B7LnFgRAoGAE0PR81/SxZ1YS7oWh+IIlE15gLZxxAJsfHQmme1/mFoCsQDQWS8O3kTqoT8WD0D4QdkD5VQsU/Cpvh8fZV8IR9lsROc5s1wp0RmGu5kn1YgELqnhneacqzPX6F6OAlIJUCO6KkSxst7R6baqFWkl4YUnKSFGE0elCeIjgCim1ts=";
//        String pub = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAoR7UWbWr/UDxS8T0Ktq7o8VFqbGfBj2YNccxH689rVs6QDneN0icsMDDFCBE6ZvFThpeKZl3+RySIH+nLJCUyC8dfguBwBQ5C/DrkNYmgBAU0pODQ8Cyq5C8BoJpSWggiVUOhI8OnRE00VAxk45jU9mPeh9aWCuthI2fcHLYXaR99OeKubYskCWN2mIKd51avNKfut0e1MP00Q9aZsnciRGy8wbYOjtHjAPfDTZV0zJEfrQNs+rw+zF8TlLdDmhjXGs+nis2ysIcSK3ZCUi6XbTlpio6K50k2QL5KzHSytN111XI732/Z9k/xkwef2x0T+cM04v1uUB8TIaMmMa5EwIDAQAB";
//    	/*MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQChHtRZtav9QPFLxPQq2rujxUWpsZ8GPZg1xzEfrz2tWzpAOd43SJywwMMUIETpm8VOGl4pmXf5HJIgf6cskJTILx1+C4HAFDkL8OuQ1iaAEBTSk4NDwLKrkLwGgmlJaCCJVQ6Ejw6dETTRUDGTjmNT2Y96H1pYK62EjZ9wcthdpH3054q5tiyQJY3aYgp3nVq80p+63R7Uw/TRD1pmydyJEbLzBtg6O0eMA98NNlXTMkR+tA2z6vD7MXxOUt0OaGNcaz6eKzbKwhxIrdkJSLpdtOWmKjornSTZAvkrMdLK03XXVcjvfb9n2T/GTB5/bHRP5wzTi/W5QHxMhoyYxrkTAgMBAAECggEAHNNUMHyVOaj9wo2JFYWunl0z2mlBxy8L5Usu2blTcolowYbY39Eo32KNRDOFwLmysgd7ozumwDXBWvkboph3Vd1ADIXof8Hedulya6Y0myLFZusnR97Y2GL7kLqSNaTgdVF3WHXzqlwis/QB+qE12hGJXtLvKekekSF5TffuB8qWZRUTomkFFt+k//9OH6twp+hlwBKLiUAcFkZzYCyNdULR43Wm6Siwhm/lL40FcLv0H9HjcAMEQLaR87aTzW+CHiRVPEFLldBcoydh22GYCql29x+eeVgID+XiIsMufS2gDKr0lqFaq+KFbGtmI8DjpTlrauZVQzPcDi7PLH8aqQKBgQDNxME+gxTJV6o4xw8lGz9TqLVPamSG1V2yg1Cw7+tQwcXuSrQa6ugC+jES/RUZSd+xjcBiQRSV5y2eG8LoiA5X3zXGyQ05TNHTPN33hduwPoaUEvTrYbs5If3nRZ1t++YMwBqvCjLL6kSl5ojj3LGfDLTmB3LrHuNDWcE2jRE0PQKBgQDIc9h3f7y2YzG71SgZabIDxirFT5HGyq3HXyJtftF/W/Sa1wN/OA8s68TgHlE0jPXORK8Kcvrx
//    	eXrpnD9mQZc19pgl6+ZiHvwswqAFb1nWp3pDrQvrfWNXjZSlwyQY/m9cEkvdJD2VE5LelgMlWDBgflHzADLNN3+gcGej1R5njwKBgBy8HUBdjcmQNHU5VyQXagCEzs0IToGFyk/jhqEu3+2nIbzlMcGQjFXeGnxMW2XsqxBgez09WWKVpgkuV0mhtl8PDLN14CLgV2zoUxb92nACS0jiXNGCFGMmHA7v6cwyIS4mpZNMGUvgqzV/vB4V87gCTkDRSXsMFTCSmCjGCmEBAoGBALJOGedyQLMcWUjzus+gLTEePT12If3qm9oUzdMIU+IuMc7qI7oua5FRx7Z0QVe1a5Enl2x8CqxxmtvimKKlBZSC3aQdyrjNRxOprB4phohiQWehrlCzIILo9ajdhGaXLQeBXuo/KmhJGQPV/MZjQ+UReGPncUkKbQSR+B7LnFgRAoGAE0PR81/SxZ1YS7oWh+IIlE15gLZxxAJsfHQmme1/mFoCsQDQWS8O3kTqoT8WD0D4QdkD5VQsU/Cpvh8fZV8IR9lsROc5s1wp0RmGu5kn1YgELqnhneacqzPX6F6OAlIJUCO6KkSxst7R6baqFWkl4YUnKSFGE0elCeIjgCim1ts=
//    	-----
//    	MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAoR7UWbWr/UDxS8T0Ktq7o8VFqbGfBj2YNccxH689rVs6QDneN0icsMDDFCBE6ZvFThpeKZl3+RySIH+nLJCUyC8dfguBwBQ5C/DrkNYmgBAU0pODQ8Cyq5C8BoJpSWggiVUOhI8OnRE00VAxk45jU9mPeh9aWCuthI2fcHLYXaR99OeKubYskCWN2mIKd51avNKfut0e1MP00Q9aZsnciRGy8wbYOjtHjAPfDTZV0zJEfrQNs+rw+zF8TlLdDmhjXGs+nis2ysIcSK3ZCUi6XbTlpio6K50k2QL5KzHSytN111XI732/Z9k/xkwef2x0T+cM04v1uUB8TIaMmMa5EwIDAQAB
//*/
//
//        /*String s1=r.Encoder(pri).encode(s);*/
//
//        String source = "您好";
//        System.out.println(pri.length());
//        String ss = new Rsa.Encoder(pri).encode(source);
//        System.out.println(ss);
//        String ssss = new Rsa.Decoder(pub).decode(ss);
//        System.out.println(ssss);
//
//    }
}
