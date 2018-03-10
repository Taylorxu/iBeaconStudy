# iBeaconStudy
iBeacon的工作原理是基于Bluetooth Low Energy（BLE）低功耗蓝牙传输技术，iBeacon基站不断向四周发送蓝牙信号，当智能设备进入设定区域时，就能够收到信号。只要满足iBeacon技术标准的都可以使用，所以Android也能够支持iBeacon。Google在Android4.3中支持BLE技术

定位一直是非常关键的功能。通过iBeacon基站的部署能够实现室内导航，同时通过蓝牙推送信息，iBeacon在商场零售或者一些公共服务领域如体育馆、博物馆能提供非常棒的体验。尤其是蓝牙不错传输距离、低功耗、以及信号加密使得iBeacon在移动支付领域也非常有前景。总之,iBeacon的潜力似乎是无穷大，也受到了越来越多的关注。

要了解iBeacon是如何工作首先我们要了解BLE。BLE（也称为Bluetooth Smart）最早追溯到Nokia于2006年提出的Wibree，后来融合进了蓝牙标准，成为Bluetooth4.0的一部分。目前我们经常能看到3种蓝牙设备：

Bluetooth：只支持传统模式的蓝牙设备
Bluetooth Smart Ready：支持传统和低功耗两种模式设备
Bluetooth Smart：只支持低功耗蓝牙设备
 estimote-ibeacon-teardown2


BLE与传统的蓝牙相比最大的优势是功耗降低90%，同时传输距离增大（超过100米）、安全和稳定性提高（支持AES加密和CRC验证）。iBeacon同时有一些自己的特点：

无需配对，一般蓝牙设备印象中都需要配对工作。iBeacon无需配对，因为它是采用蓝牙的广播频道传送信号。
程序可以后台唤醒，iBeacon的信息推送需要App支持。但是我们接收iBeacon信号无需打开App，只要保证安装了，同时手机蓝牙打开。
iBeacon是如何工作呢？实际上iBeacon基站通过蓝牙的广播频道不断向外发送位置信息，发送频率越快越耗电。也就是说iBeacon并不推送消息，而只是用于定位，推送消息的功能必须由App来完成。苹果定义了iBeacon 其中32位广播的数据格式。

estimote-ibeacon-teardown3
UUID：厂商识别号
Major：相当于群组号，同一个组里Beacon有相同的Major
Minor：相当于识别群组里单个的Beacon
TX Power：用于测量设备离Beacon的距离
UUID+Major+Minor就构成了一个Beacon的识别号，有点类似于网络中的IP地址。TX Power用于测距，iBeacon目前只定义了大概的3个粗略级别：

非常近（Immediate）: 大概10厘米内
近（Near）:1米内
远（Far）:1米外
这里主要是对其用法做一个介绍：
