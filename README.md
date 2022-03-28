![alt text](https://www.clover.com/static/media/clover-logo.4204a79c.svg)

# Examples for the Clover Remote Pay Android SDK

## Version

Current version: 4.3.5

## Overview

These example apps utilize the Remote Pay Android SDK, which allows your Android-based Point-of-Sale (POS) system to communicate with a [Clover® payment device](https://www.clover.com/pos-hardware/) and process payments. Learn more about [Clover Integrations](https://www.clover.com/integrations).

The Android project includes an example POS, along with other simpler examples. To work with the project effectively, you will need:
- [Gradle](https://gradle.org) (suggested version 6.2.2).
- An [Android SDK](http://developer.android.com/sdk/index.html) (17+).
- An IDE, such as [Android Studio](http://developer.android.com/tools/studio/index.html).
- To experience transactions end-to-end from the merchant and customer perspectives, we also recommend ordering a [Clover DevKit](http://cloverdevkit.com/collections/devkits/products/clover-mini-dev-kit).

## Getting connected with USB Pay Display
1. Download the USB Pay Display app from the Clover App Market onto your Clover DevKit. 
2. Open the USB Pay Display app.
3. Run the Clover Connector Android Example POS app on your Android POS device or emulator.
4. The Example POS screen and device connection status should appear. If the connection was successful, the device status should be "connected." If the device remains disconnected, verify that:
	1) You are connecting the correct cable to the correct connection point on the Clover device. On the Clover Mini, this is the USB port with the Clover logo. You will need to use a USB A-to-USB B cable (see [ADB over USB](https://docs.clover.com/clover-platform/docs/setting-up-your-devkit#section--adb-over-usb-) for more information).
	2) Your Android device supports “host” mode, which is also referred to as OTG mode. This functionality is required to communicate with the Clover Mini, which will operate in “accessory” mode.
	
## Getting connected
1. Download the Secure Network Pay Display app from the Clover App Market onto your Clover DevKit.
2. Configure Secure Network Pay Display - https://docs.clover.com/clover-platform/docs/configuring-secure-network-pay-display#section-device-server-certificates
3. Open MainActivity.java and uncomment the following line: 
    ```cloverConnector = new CloverConnector(getNetworkConfiguration("ip-address", 12345))```
4) Enter the ip address that Secure Network Pay Display shows in the start-up screen.

## Additional resources

* [Release Notes](https://github.com/clover/remote-pay-android/releases)
* [Secure Network Pay Display](https://docs.clover.com/clover-platform/docs/pay-display-apps#section--secure-network-pay-display-)
* [Tutorial for the Android SDK](https://docs.clover.com/clover-platform/docs/android)
* [API Documentation](https://clover.github.io/remote-pay-android/4.3.5/docs/)
* [Clover Developer Community](https://community.clover.com/index.html)
