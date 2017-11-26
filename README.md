DeviceHive Android Example
=========================

[DeviceHive]: http://devicehive.com "DeviceHive framework"
[DataArt]: http://dataart.com "DataArt"

DeviceHive Android Example demostrates the usage of [DeviceHive Java Library](https://github.com/devicehive/devicehive-java) inside Android Application. [DeviceHive Java Library](https://github.com/devicehive/devicehive-java) could be easily [implemented](https://github.com/devicehive/devicehive-java#download) into any Android Project. It supports Android Applications for tablets and phones, Android Wear and Android Things. 

This Android Application is showing how easily you can register a new Device or get the existing and start sending any data to the server. For an example we are sending current Location.

```java
//Creating DeviceHive Instance
  DeviceHive deviceHive = DeviceHive.getInstance().init(String serverUrl,
                    new TokenAuth(String refreshToken));
//Registering new Device or getting the existing
  DHResponse<Device> dhResponse = deviceHive.getDevice(String deviceId);
        if (dhResponse.isSuccessful()) {
            Device device = dhResponse.getData();        
        }
//Sending of the notifications
device.sendNotification(String notificationName, List<Parameter> params);
```



DeviceHive license
------------------

[DeviceHive] is developed by [DataArt] Apps and distributed under Open Source
[Apache 2.0](https://en.wikipedia.org/wiki/Apache_License). This basically means
you can do whatever you want with the software as long as the copyright notice
is included. This also means you don't have to contribute the end product or
modified sources back to Open Source, but if you feel like sharing, you are
highly encouraged to do so!

&copy; Copyright 2017 DataArt Apps &copy; All Rights Reserved

