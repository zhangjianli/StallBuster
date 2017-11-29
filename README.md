# StallBuster
As an Android developer, we all know that we must avoid time consuming operations, like file IO, in main thread, which will affect your app's performance.
But sometimes even you have done all the optimization work, still your app takes forever to start a new activity.
This might because something happened in the main thread causing the blockage.

In fact when an app's process is created, it's main thread goes into an infinite loop, MainLooper. MainLooper has a MessageQueue. Any thread can post a Message to this queue. It will be picked by the MainLooper and executed in corresponding Handler, no matter the Message is posted by your app or by Android system. If you know what's in the MessageQueue and how these Message are executed, you know everything about your app's main thread.

So, when you start a new Activity, Android system will post a series of Messages to MainLooper. If your app post other Messages in between these Messages, it will slow down the start activity procedure. This is more likely to happen if you have multiple background threads in your app.  

Well, StallBuster can help you to detect these unexpected Messages. If you have StallBuster integrated in your app, each time you start a new activity. StallBuster will record all the Messages from startActivity is called to new activity is displayed and how much time it takes to handle them. StallBuster will highlight the unexpected Messages and Messages took too much time to handle. You can easily find out why your app is slow.  

# Getting started
1.Add StallBuster to your app
```gradle
dependencies {
    compile 'com.github.zhangjianli:stallbuster:1.1'
}
```
2.Add StallBuster.getInstance().init() in your app's Application
```java
public class DemoApplication extends Application {

    @Override
    public void onCreate() {
        StallBuster.getInstance().init(this);
        super.onCreate();
    }


}
```
3.Build and install your app.

4.Play with your app, open some activities, then checkout open StallBuster to checkout the result
# License

    Copyright (C) 2017 ZhangJianli

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
