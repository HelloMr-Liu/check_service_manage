
//私钥
var PRIVATE_KEY = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAItpqEJfXMSZLeen2F1ahugnTtyFWYwV4izvy4Ob5+tq6X1j7NEHJ0LwBy06VF4jYI7Cb44iiXU39pEqfDu0jZjsMPrB6ojS/3e2f6JlUval20zsRAP28mT3aDhrnSbKt50LtQUQ+ZZXRqRwp/i/l772cVW9Xo1jrhiQUf+4ZYh5AgMBAAECgYBpqgMlizG8sHzif4y19M8bVe+npEpiqpNqi7UsYxY8kCO4Lexn4YwYZ+SImxUm5JlimmwCxpp4hZoUHKdceVR32q3XDmJtp9J31K5Bccajv84lpW/Wf0ZzLAwOVGDIp7w/i7gO366EJHgOiYA58zxGGp2HqmChgebz1Kj19F/8gQJBANYcOYtCGdu1gln5pxbHxIrOg/fe+hagld21nhLvt7FG5h1FLI2hicc17E25p1Obl8uCv+kK5ts3FLpHPtXtG6UCQQCmsC9G77M7ZqgZky9hd9SFP/cStkiXpV7bsOZ0ouTu8pgh5QJnkWwUEEqeyywCCCTn31wGVnmz1kThESwD4LFFAkEAkDtrODhLGwpmyR24RUxFRgxgMfXvGpcK3dOerLr3T2CnW0hh/va2zY+xo5pQ8a160AlAJkjFwx6Tw/iCh7G2UQJAF9BmALlwHtVswLKhe0hWaW8ZCmhWeKvcoIBmfFVlZantCDKipE9HZJprPsKCGAw30ZS8nf8uxf2fu5PreOZaDQJBAMAmImJwOT9+949YwF/5BDFHgdc01py5Xnz4jg0EgmGtIkfvAdsGkhwIu7BIRDU14m5wQbVXui02YGbO4xeIS2g="
//公钥
var PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCLaahCX1zEmS3np9hdWoboJ07chVmMFeIs78uDm+fraul9Y+zRBydC8ActOlReI2COwm+OIol1N/aRKnw7tI2Y7DD6weqI0v93tn+iZVL2pdtM7EQD9vJk92g4a50myredC7UFEPmWV0akcKf4v5e+9nFVvV6NY64YkFH/uGWIeQIDAQAB"

/*================定义Ajax全局请求拦截器=============*/
$.ajaxSetup({
    cache: false,  // 不缓存请求信息
    dataType: "json",
    headers: {
        "publicKey":PUBLIC_KEY,
    },
    xhrFields: {
        withCredentials: true
    },
    //发送前方法
    beforeSend: function (jqXHR,settings) {
        //配置全局请求上下文
        settings.url="/check_service_manage"+settings.url;
        if(settings.data&&JSON.stringify(settings.data)!="{}"){

            //获取服务器的加密公匙
            let remoteKey={};
            $.ajax({
                url:"/security/key",
                async:false,
                type:"post",
                dataType:"json",
                success:(data)=>{
                    let encrypt = new JSEncrypt();
                    encrypt.setPrivateKey(PRIVATE_KEY);
                    remoteKey=JSON.parse(encrypt.decryptUnicodeLong(data));
                }
            })

            //进行请求参数信息加密
            let encrypt = new JSEncrypt();
            encrypt.setPublicKey(remoteKey.publicKey);
            let encryptStr=JSON.stringify(settings.data);
            if(typeof settings.data =="string"){
                encryptStr=encryptStr.substring(1,encryptStr.length-1);
            }
            let encrypted = encrypt.encryptUnicodeLong(encryptStr);
            settings.data="encryptJsonInfo="+encrypted;
        }
    },
    processData: false,
    //完成请求后触发。
    complete: function(jqXHR) {

    },

});

