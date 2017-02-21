
SlideZoomView
===============

A simple slide zoom view for Android

Screenshots
===============

![](https://github.com/ruzhan123/SlideZoomView/raw/master/gif/slidezoomview.gif)


SlideZoomView control text **position** and **size**, in onDraw **canvas** set **drawText**

[![](https://jitpack.io/v/ruzhan123/SlideZoomView.svg)](https://jitpack.io/#ruzhan123SlideZoomView)

Gradle
------

Add it in your root build.gradle at the end of repositories:


```java

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

Add the dependency:


```java

	dependencies {
	        compile 'com.github.ruzhan123:SlideZoomView:v1.0'
	}
```


Usage
------

```xml

	<zhan.slidezoomview.SlideZoomView
	  android:id="@+id/hnv1"
	  android:layout_width="match_parent"
	  android:layout_height="40dp"
	  android:layout_marginLeft="15dp"
	  android:layout_marginRight="15dp"
	  android:layout_marginTop="30dp"
	  app:number_view_normal_text_color="@color/number_view_normal_text_color"
	  app:number_view_normal_text_size="40"
	  app:number_view_select_text_color="@color/number_view_select_text_color"
	  app:number_view_select_text_size="70"
	  />
	
	<SeekBar
	  android:id="@+id/seek_bar1"
	  android:layout_width="match_parent"
	  android:layout_height="wrap_content"
	  android:layout_marginBottom="15dp"
	  android:layout_marginLeft="15dp"
	  android:layout_marginRight="15dp"
	  android:background="@drawable/seek_bar_bg"
	  android:progressDrawable="@drawable/seek_bar_pro_drawable"
	  />
```

```java

    mHnv1 = (SlideZoomView) findViewById(R.id.hnv1);
    mSeekBar1 = (SeekBar) findViewById(R.id.seek_bar1);
    mHnv1.setNumberStringArray(new String[]{"1","2","3","4","5","6","7","8","9","10"});
    mHnv1.setSeekBar(mSeekBar1);
```

Developed by
-------

 ruzhan - <a href='javascript:'>ruzhan333@gmail.com</a>


License
-------

    Copyright 2017 ruzhan

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
	
