
//存储AES加密密匙
let aesEncryptKey="";

//获取服务器的加密公匙
let remoteKey={};
$.ajax({
    async:false,
    url:"/check_service_manage/security/key",
    type:"post",
    dataType:"json",
    success:(data)=>{
        remoteKey=data;
    }
})

/**
 * AES加密
 * @param content 待加密的内容
 * @param secretKey 密钥
 * @param iv 初始向量
 * @returns {string} 加密结果
 */
function aesEncrypt(content, secretKey, iv) {
    return CryptoJS.AES.encrypt(content, CryptoJS.enc.Utf8.parse(secretKey), {
        iv: CryptoJS.enc.Utf8.parse(iv),
        mode: CryptoJS.mode.CBC,
        padding: CryptoJS.pad.Pkcs7
    }).toString();
}

/**
 * AES解密
 * @param content 待解密的内容
 * @param secretKey 密钥
 * @param iv 初始向量
 * @returns {string} 解密结果
 */
function aesDecrypt(content, secretKey, iv) {
    return CryptoJS.AES.decrypt(content, CryptoJS.enc.Utf8.parse(secretKey), {
        iv: CryptoJS.enc.Utf8.parse(iv),
        mode: CryptoJS.mode.CBC,
        padding: CryptoJS.pad.Pkcs7
    }).toString(CryptoJS.enc.Utf8);
}

/**
 * RSA 公钥加密
 * @param content 待加密数据
 * @param publicKey 公钥
 * @returns {string} 加密结果
 */
function rsaEncrypt(content, publicKey) {
    let encrypt = new JSEncrypt();
    encrypt.setPublicKey(publicKey);
    return encrypt.encryptUnicodeLong(content);
}

/**
 * RSA 私钥解密
 * @param content 待解密数据
 * @param privateKey 私钥
 * @returns {string} 解密结果
 */
function rsaDecrypt(content, privateKey) {
    let encrypt = new JSEncrypt();
    encrypt.setPrivateKey(privateKey);
    return encrypt.decryptUnicodeLong(content);
}


/**
 * 定义Ajax全局请求拦截器
 */
$.ajaxSetup({
    cache: false,  // 不缓存请求信息
    dataType: "json",
    headers: {
        encryptAesEncryptKey:encryptAESKey(16),
    },
    xhrFields: {
        withCredentials: true
    },
    processData: false,
    //发送前方法
    beforeSend: function (jqXHR,settings) {
        //配置全局请求上下文
        settings.url="/check_service_manage"+settings.url;

        if(settings.data&&JSON.stringify(settings.data)!="{}"){

            //对原内容进行AES数据加密
            let encryptStr=JSON.stringify(settings.data);
            if(typeof settings.data =="string"){
                encryptStr=encryptStr.substring(1,encryptStr.length-1);
            }
            let encryptJsonInfo=aesEncrypt(encryptStr,aesEncryptKey,aesEncryptKey);
            settings.data="encryptJsonInfo="+encryptJsonInfo;
        }
    },
    //完成请求后触发。
    complete: function(jqXHR) {

    },

});


/**
 * 随机生成一个加密AES密匙
 * @param length
 * @returns {string}
 */
function encryptAESKey (length) {
    const chars = ['0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E',
        'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z']
    if (!Number.isInteger(length) || length <= 0) { // 合法性校验
        return 'Error'
    }
    let randomString = ''
    for (let i = 0; i < length; i++) {
        randomString += chars[Math.floor(Math.random() * chars.length)]
    }
    aesEncryptKey=randomString;

    //使用RSA对AES加密密匙加密
    let encryptAESKey=rsaEncrypt(aesEncryptKey, remoteKey.publicKey);
    return encryptAESKey;
}

/**
 * 使用AES密匙解密
 * @param encryptData
 * @returns {any}
 */
function decryptData(encryptData){
    let decryptData=aesDecrypt(encryptData,aesEncryptKey,aesEncryptKey);
    return JSON.parse(decryptData);
}

/**
 * 时间格式化方法
 * @param fmt
 * @param date
 * @returns {*}
 */
function dateFormat(fmt, date) {
    let ret;
    const opt = {
        "Y+": date.getFullYear().toString(),        // 年
        "m+": (date.getMonth() + 1).toString(),     // 月
        "d+": date.getDate().toString(),            // 日
        "H+": date.getHours().toString(),           // 时
        "M+": date.getMinutes().toString(),         // 分
        "S+": date.getSeconds().toString()          // 秒
        // 有其他格式化字符需求可以继续添加，必须转化成字符串
    };
    for (let k in opt) {
        ret = new RegExp("(" + k + ")").exec(fmt);
        if (ret) {
            fmt = fmt.replace(ret[1], (ret[1].length == 1) ? (opt[k]) : (opt[k].padStart(ret[1].length, "0")))
        };
    };
    return fmt;
}