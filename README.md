#**海曼MQtt Android SDK集成文档**

- [简介](#Introduction)
		- [平台简介](#Platform_Introduction)
		- [APP SDK简介](#sdk_Introduction)
		- [集成流程](#Integration_Processes)
- [二、轻松集成](#integrated)
    - [集成准备](#ready)
    - [Setp1 下载SDK](#setp1)
    - [Setp2 开发环境配置](#setp2)
    - [Setp3 初始化SDK](#setp3)
    - [Setp4 注册、登录用户账号](#setp4)
    - [Setp5 添加设备](#setp5)
    - [Setp6 连接设备](#setp6)
    - [Setp7 设备控制 & 接收设备数据](#setp7)
    - [Setp8 推送集成](#setp8)
- [三、API文档](#api) 
- [四、术语表](#Glossary)
- [五、更新历史](#history)
## 一、<a name="Introduction">简介</a>

- [平台简介](#Platform_Introduction)
- [APP SDK简介](#sdk_Introduction)
- [集成流程](#Integration_Processes)

### 1.1、<a name="Platform_Introduction">平台简介</a>
- 为传统企业提供物联网云平台软件服务，快速实现产品智能化、进入物联网领域
- 为企业提供技术、服务支撑平台和运营体系
- 为企业提供物联网数据服务的平台

### 1.2、<a name="sdk_Introduction">APP SDK 简介</a>

**为简化开发者的APP开发，云智易提供了APP开发套件和相关的支持服务：**

- 提供APP的SDK和开发文档，兼容iOS、Android平台
- 提供APP端通用业务功能块的实例源代码，极大简化开发人员开发工作
- 为企业用户提供专业的技术支持服务

**APP开发套件包含以下主要功能：**

- 智能配网功能
- 设备绑定接口
- 建立安全的远程连接，以及心跳维持
- 智能链路功能，选择最优的数据通道
- 提供数据接收与发送的接口
- 提供底层网络和设备状态回调接口
- 使开发者简化APP开发流程，快速完成APP开发，开发者仅需关注APP业务功能即可，而相对复杂的协议与错误处理等事项可忽略。

### 1.3、<a name="Integration_Processes" >集成流程</a> 

1. 注册企业开发者账号， 添加测试产品；
2. 下载、配置APP SDK
3. 通过SDK中HTTP RESTful接口申请用户账号；
4. 通过SDK中HTTP RESTful接口进行用户账号认证，获取"userid"、"accesstoken"；
5. 使用 "userid"、"accesstoken"登录云端；
6. 添加测试设备；
7. 连接测试设备；
8. 控制设备，接收设备数据；
 

##二、<a name="integrated">轻松集成</a>


- [集成准备](#ready)
- [Setp1 下载SDK](#setp1)
- [Setp2 开发环境配置](#setp2)
- [Setp3 初始化SDK](#setp3)
- [Setp4 注册、登录用户账号](#setp4)
- [Setp5 添加设备](#setp5)
- [Setp6 连接设备](#setp6)
- [Setp7 设备控制 & 接收设备数据](#setp7)
- [Setp8 推送集成](#setp8)

### 2.1<a name="ready" > 集成准备</a>

为了您更好的使用提供的服务，使用前需要先登录企业管理后台注册企业账号，并添加产品和导入测试设备。

###2.1.1 注册企业账号
1. 向我司提供所需的注册账号。
2. 我司提供相应后台地址。
3. 进入后台配置相关信息 
4. 或通过我司提供后台开发协议进行后台开发功能。
 

###2.1.2 添加产品
1. 登录企业管理后台
2. 点击左侧“添加产品”按钮
3. 输入产品信息进行添加 
  

###2.2  <a name="setp1" >Setp 1 下载SDK工具包</a>
1. SDK下载地址 
2. 解压下载的SDK，目录结构如下图  

- 注：《透传DEMO源码》是一个使用海曼APP SDK的示例测试程序，实现了设备透传控制功能，开发者可以直接使用透传Demo进行测试体验，也可以结合本文档，参考其中Demo代码实现开发自己的APP。 
-
- 注：为了简化Http接口调用，Android透传SDK中提供了HmHttpManage类源码供开发者使用，使用时需要替换HttpManage.COMPANY_ID为注册的企业ID,以及登录成功后替换ACCESSTOKEN和userId。 此文档中涉及到Android Http接口的调用都以HmHttpManage类为例说明。

###2.3 <a name="setp2" >Setp 2 开发环境配置</a>

**Android**

1. 打开Android Studio ，点击菜单 File->New->New Project... 创建一个新的工程；
2. 把解压的 hm-mqtt-sdk-v1_20171221.jar、ltlink2.jar、libAES128Converter.so（具体jar名称可能有区别） 添加到工程 libs目录；
3. 在APP的build.gradle文件中添加sdk、json、http库的引用
```
 dependencies {
  	compile 'se.wetcat.qatja:core:1.0.3' //mqtt 服务
    compile 'com.orhanobut:logger:2.1.1'
    compile 'com.google.code.gson:gson:2.8.2'
    compile 'com.lzy.net:okgo:+'
    compile 'com.lzy.net:okrx:+'
    compile 'com.lzy.net:okserver:+'
    
}
```

**IOS**

1. 配置头文件搜索路径:进入Xcode工程属性页面，进入BuildSettings标签，找到HeaderSearchPaths选项，将libxlink/include目录加入SearchPaths；
2. 配置库引用:进入Xcode工程属性页面，进入BuildSettings标签，找到OtherLinkerFlags选项，加入-lxlinksdklib参数；
3. 配置库文件搜索路径:进入Xcode工程属性页面，进入BuildSettings标签，找到LibrarySearchPaths选项，将libxlink/lib下针对模拟器和实体机加载的库的路径配置到SearhPaths中。

###2.4 <a name="setp3" >Setp 3 初始化SDK</a>

**Android**

1. 在AndroidManifest.xml文件中application 标签下配置添加：

	**Android 代码范例**

	```
		<service
            android:name="com.heiman.mqttsdk.HmService"
            android:exported="false" />

		注意：如果缺少以上配置会造成sdk服务不能正常启动
	```

2. 添加sdk所需要权限

	**Android 代码范例**

	```
		<!-- 联网权限 -->
		<uses-permission android:name="android.permission.INTERNET" />
		<!-- 网络状态 -->
		<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
		<!-- wifi状态 -->
		<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
		<!-- wifi状态改变 -->
		<uses-permission android:name="android.permission.CHANGE_WIFI_MULTICAST_STATE" />
	```

3. 创建自定义Application 实现XlinkNetListener接口，并在AndroidManifest.xml中修改android:name为新建的Application

	**Android 代码范例**

	```
	public class MyApp extends Application implements HmNetListener, HmGatewayListener
	```

	```
	 <application
	        android:name=".MyApp"
	        ...
	 </application>
	```

4. 在自定义Application 下的onCreate()函数调用XlinkAgent.init进行SDK初始化

	**Android 代码范例**

	```
	// 初始化sdk
	 HmAgent.getInstance().init(this);
	//参数会被长期引用，最好使用application 的 context。
	```

5. 注册SDK回调监听器。至少需要注册一个监听器，可注册多个。

* 注册SDK通用监听器是为了能让APP收到来自SDK的响应事件，包含了登录、接收设备数据等回调接口。该监听器是SDK使用中十分重要的一个监听器，与SDK相关的操作都会在这里会回调，如果没有正确注册通用监听器，将无法正常使用SDK。

	**Android 代码范例**

	```
	//设置SDK回调监听器
	HmAgent.getInstance().addHmListener(this);
	//可以在该监听器中直接更新UI
	``` 

####注意：
  调用初始化方法XlinkAgent.init(Context)和添加监听的回调XlinkNetListener方法不能在子线程中进行操作，否则可能会出错



    **IOS**


###2.5 <a name="setp4" >Setp 4 注册、登录用户账号</a>

1. **用户注册**

	可通过手机或者邮箱在平台下注册成为一个用户。通过账号和密码进行认证获得一个有效的调用凭证，即可通过调用凭证使用用户相关的RESTful接口。 

	**注：使用邮箱注册用户后，系统会往注册的邮箱发送一封认证激活邮件，需要查看邮件并点击邮箱中的链接进行账号激活后才能正常使用账号。**

	**Android调用示例**

	**为了简化Http接口调用，Android透传SDK中提供了HmHttpManage类源码供开发者使用，使用时需要替换HttpManage.COMPANY_ID为注册的企业ID,以及登录成功后替换ACCESSTOKEN和userId。 此文档中涉及到Android Http接口的调用都以HmHttpManage类为例说明。**


	```
 HmHttpManage.getInstance().onRegister(uid, passwprd, new Dialogback<Code<Register>>(this) {
            @Override
            public void onSuccess(Response<Code<Register>> response) { 
            	//注册成功回调
            }

            @Override
            public void onError(Response<Code<Register>> response) {
 				//登录失败
            }
        });
	```

	**IOS调用示例**

	**开发者需要手动将HttpRequest里面的宏定义 CorpId 后面的企业ID修改为自己企业的企业ID**

	```
	+(void)registerWithAccount:(NSString *)account withNickname:(NSString *)nickname withVerifyCode:(NSString *)verifyCode withPassword:(NSString *)pwd didLoadData:(MyBlock)block;
	```

2. **用户验证**

	调用Http RESTful接口进行用户验证，需要企业ID，可在企业后台获取。 登陆与认证是用户通过账号（邮箱或者手机号）和密码获取RESTful接口调用凭证的方式，成功认证后会获得一个有效的调用凭证和一个有效的刷新凭证。
    调用凭证有效期为2个小时。在凭证失效前需要调用刷新用户凭证。

	**Android 调用示例**

	```
 HmHttpManage.getInstance().onLogin(email, passwprd, new Dialogback<Code<Login>>(this) {
            @Override
            public void onSuccess(Response<Code<Login>> response) { 
            	//登录成功回调
            }

            @Override
            public void onError(Response<Code<Login>> response) {
 				//登录失败
            }
        });
	```

	**IOS 调用示例**

	```
 	```

3. **登录SDK**

	用户验证成功后，需要使用“userId”和“ACCESSTOKEN”调用SDK登录函数登录云端后才能使用远程设备连接、控制功能。 

	**Android  调用示例**

	```
    //启用服务， 设备发现，设置AccessKey等依赖此服务
      HmAgent.getInstance().start(new HmStart() {
            @Override
            public void onStart(int code) {
                if (code == HmCode.SERVER_START) {
                  
                }
            }
        });
  ```
  ```
    //登录SDK 启用云端远程服务
     HmAgent.getInstance().connect(HmConstant.host, HmConstant.port,
                            "AndroidMqttSDK", "AndroidMqttSDK",
                            "U_" + HmApplication.getLogin().getAccess_token()); 
   ```
   ```             
    // 回调登录xlink状态
    @Override
    public void onLogin(int code) {
        if (code == STATE_NONE) {
			Logger.e("HmApplication:服务器未变化!");
        } else if (code == STATE_CONNECTED) {
            Logger.e("HmApplication:服务器连接成功!");
        } else if (code == STATE_CONNECTION_FAILED) {
            Logger.e("HmApplication:服务器连接失败!");
        }
    }
	```

	**IOS 调用示例**

	```

	```

###2.6 <a name="setp5" >Setp 5 添加设备</a>

SDK封装了配置设备加网功能，可以使用SDK方法配置设备网络并发现内网连接的设备，并加入到SDK中。 也可以通过网络获取订阅设备的必要参数，通过Json转成设备实体并添加到SDK中。 只有把设备对象添加到SDK中并初始化设备，才能进行设备的连接、控制等操作。
  
 

2. 内网配网关设备

	**Android  调用示例具体方法参照Demo中SmarLinkActivity**

	```
 	if (isStart) {
                    mHandler.sendEmptyMessageDelayed(MSG_SMARTLINK_FAIL, UPDATE_FIRMWARE_FAIL_TIME);
                    try {
                        smartlink.setConfigCallback(configCallback);
                        smartlink.startConfig(HmUtils.getSSid(this), getBssId(), etwifipass.getText().toString(), UPDATE_FIRMWARE_FAIL_TIME);
                        isStart = false;
                        btnstart.setText("停止配置");
                        etwifipass.setFocusable(false);
                        etwifipass.clearFocus();
                        etwifipass.setFocusableInTouchMode(false);

                        final View focusView = etwifipass;

                        if (focusView != null) {
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            assert imm != null;
                            boolean b = imm.hideSoftInputFromWindow(focusView.getWindowToken(), 0);
                            focusView.clearFocus();
                        }

                    } catch (Exception e) {
                        mHandler.removeMessages(MSG_SMARTLINK_FAIL);
                        mHandler.sendEmptyMessage(MSG_SMARTLINK_FAIL);
                    }
                } else {
                    smartlink.stopConfig();
                    smartlink.setConfigCallback(null);
                    isStart = true;
                    btnstart.setText("开始配网");
                    etwifipass.setFocusable(true);
                    etwifipass.setFocusableInTouchMode(true);
                    etwifipass.requestFocus();
                }
	
	```
	
	```
	//APP收到注册设备到云端
	 HmHttpManage.getInstance().onRegisterDevice(hmDevice.getPid()+"", hmDevice.getDeviceMac(), "1", "1", hmDevice.getDeviceMac(), new Dialogback<<Code<RegisterDevice>>>(SmarLinkActivity.this) {
                        @Override
                        public void onSuccess(Response<<Code<RegisterDevice>>> response) {
                            //注册成功
                        }
                        @Override
                        public void onError(Response<<Code<RegisterDevice>>> response) {
                            //注册失败
                        }
                    });
	
    使用内网发现功能扫描设备会自动将发现的设备加入到SDK并初始化， 如果通过Json等方式添加设备，还需要调用SDK的 HmHttpManage.getInstance().initDevice(xDevice)进行初始化到SDK中。
	```

	**IOS 调用示例**

	```

	```

###2.7 <a name="setp6" >Setp 6 连接设备</a>

1. 连接设备用于探测设备状态，并与之建立网络通道。外网连接需要先订阅设备（SDK内部会自动订阅，如果连接返回设备订阅关系错误如设备AccessKey不存在情况，设备Accesskey通过服务器生产每一类产品有一个Accesskey，则需要手动调用订阅方法进行订阅）。
SKD会根据网络连接情况自适应内外网络环境，会自动选择速度快的网络进行设备连接。 

	**Android  调用示例** 

 ```
  XlinkAgent.getInstance().subscribeDevice(device.getXDevice(), device.getXDevice().getSubKey(), new SubscribeDeviceListener() {
                @Override
                public void onSubscribeDevice(XDevice xdevice, int code) {
                    if (code == XlinkCode.SUCCEED) {
                        device.setSubscribe(true);
                    }
                }
            });


	如果设备已经订阅，或者设备在订阅列表中存在，那么就不需要进行重复订阅了，那么就可以通过accessKey和subKey进行设备的链接，调用方式如下：

	//根据上一步内网发现的设备，使用AccesKey和subKey进行设备连接 如设备未设置AccessKey，连接前需要先进行AccessKey设置。 HmAgent.getInstance().connectDevice(device.getXDevice(),device.getXDevice().getAccessKey(),device.getXDevice().getSubKey(), connectDeviceListener);
        if (ret < 0) {// 调用设备失败
            //返回小于0 表示扫描失败， 具体错误码参见API文档附录
        }
   ```
    //根据上一步内网发现的设备，使用AccesKey进行设备连接 如设备未设置AccessKey，连接前需要先进行AccessKey设置。
    //需要在同一个局域网并且设备未设置AccssKey才能设置成功
	XlinkAgent.getInstance().setDeviceAccessKey(device, key, new SetDeviceAccessKeyListener() {
         @Override
         public void onSetLocalDeviceAccessKey(XDevice device, int code, int messageId) {
             Log("设置AccessKey:" + code);
             switch (code) {
                 case XlinkCode.SUCCEED:
                 	//设置成功
                     break;
             }
         }
     });
	```
	 /**
     * 连接设备回调。该回调在主程序，可直接更改ui
     */
    private ConnectDeviceListener connectDeviceListener = new ConnectDeviceListener() {

        @Override
        public void onConnectDevice(XDevice xDevice, int result) {

            switch (result) {
                case XlinkCode.DEVICE_STATE_LOCAL_LINK:
                	 // 连接设备成功 设备处于内网
                    break;
                case XlinkCode.DEVICE_STATE_OUTER_LINK:
                	// 连接设备成功 设备处于云端
                	break;
                case XlinkCode.CONNECT_DEVICE_INVALID_KEY:
                	// 设备授权码错误
                    break;
                case XlinkCode.CONNECT_DEVICE_OFFLINE:
                   // 设备不在线
                    break;
                case XlinkCode.CONNECT_DEVICE_TIMEOUT:
                // 连接设备超时了，（设备未应答，或者服务器未应答）
                    break;
                case XlinkCode.CONNECT_DEVICE_SERVER_ERROR:
                    break;
                case XlinkCode.CONNECT_DEVICE_OFFLINE_NO_LOGIN:
                    //连接设备失败，设备未在局域网内，且当前手机只有局域网环境"
                    break;
                default:
                    break;
            }

        }
    ```
	**IOS 调用示例**
	```
	```

###2.8 <a name="setp7" >Setp 7 设备控制&接收设备数据</a>

1. 设备连接成功后，即可对设备进行控制和获取设备的属性等信息。 设备控制可通过透传数据包  。
SDK

	**Android  调用示例**

	```
	//需要连接成功后才能发送数据，SDK和云端将发送的数据透传到设备端
	  HmAgent.getInstance().publish(hmDevice, data, new HmPublishListener() {
                            @Override
                            public void onPulishData(HmDevice hmDevice, int code) {
                                if (code != HmCode.SUCCEED) {
                                    Logger.e("code:" + code);
                                    Log("发送失败：" + code);
                                }
                            }
                        });
	``` 

	**IOS 调用示例**

	```
	```
	
###2.9 <a name="setp8" >Setp 8 推送集成</a>
如需增加SDK推送，目前支持
[极光推送](https://docs.jiguang.cn/jpush/client/Android/android_3m/)

##三、<a name="api">API文档</a>

####Android：

具体参考[SDK接口说明]()
####IOS：

具体参考[SDK接口说明]()

##四、<a name="Glossary">术语表</a>


> | 术语 | 解释 |
> |-------------|-------------|
> |产品|即企业要开发、生产、销售的一款设备，对应到企业管理后台的产品管理|
> |设备|设备是产品的实体，这里特指直接接入云智易平台的智能设备，如网关，插座，灯泡|
> |子设备|间接接入海曼平台的设备，如通过网关接入的温度传感器|
> |企业后台|指海曼提供给企业客户的管理后台|
> |企业账号|指登录企业管理后台的管理员账号|
> |企业ID|注册企业账号后，系统会自动给账号分配一个企业ID，调用Http接口需要使用到|
> |APP用户|指在APP端通过Http接口注册和登录的终端用户账号|
> |数据端点|数据端点指产品的属性，APP可以通过获取和修改设备数据端点的值来进行控制设备.云平台可以根据维护的数据端点来处理数据统计、消息推送服务|
> |设备分享|用户可以将设备分享给其他用户，与其他用户共同拥有设备，共同控制设备，只有设备的管理者才可分享设备给其他用户|
> |订阅|App用户与设备产生一种订阅关系授权，必须已订阅该设备的APP用户才能通过云端链接和控制设备及收到设备的消息推送|
> |设备管理员|设备管理员可以分享设备、取消他人的订阅关系等。 平台默认第一个订阅设备的用户为管理员|
> |AccessKey|产品公钥由服务器生产| 




##五、<a name="history">更新历史</a>

####Android:

2017-12-21：发布初步版本协议以及SDK



####IOS

 # MqttSDKAndroid
