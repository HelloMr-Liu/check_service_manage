package org.king2.check_service_manage.entity;
/**
 * 描述:定义安全信息配置
 * @author 刘梓江
 * @Date 2021/5/21 11:38
 */
public class SecurityConfigFile {

    /**
     * 公匙信息
     */
    private String publicKey;

    /**
     * 私钥信息
     */
    private String privateKey;


    public SecurityConfigFile() {
    }

    public SecurityConfigFile(String publicKey, String privateKey) {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public String getPrivateKey() {
        return privateKey;
    }
}

