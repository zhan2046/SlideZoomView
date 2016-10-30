<!--lang: java-->
# 这是一个SeeKBar与数组共享滑动效果的View

效果图：

![](https://github.com/ruzhan123/HorizontalNumberView/raw/master/gif/number_view.gif)



简单分析：

1. 初始化一些参数，设置数据，准备在onDraw方法中连续绘制TextView
2. 计算出TextView绘制的坐标点，根据左滑还是右滑设置TextView之间间隙，大小，颜色
3. 将seekbar注入进来，根据对调监听设置当前位置，进行重绘，绘制最新的数组列表

---

实现难点：

1.  计算出绘制数组宽度与高度
2.  TextView绘制坐标与方式
3.  seekbar回调，改变哪一些值可以达到预期的滑动效果
4.  往左滑计算方式，往右滑计算方式
5.  设置不同状态下TextViewde参数

---

我的博客：[详解](https://ruzhan123.github.io/2016/10/31/2016-10-31-01-HorizontalNumberView%E4%B8%8Eseekbar%E5%85%B1%E4%BA%AB%E6%BB%91%E5%8A%A8%E7%9A%84%E8%87%AA%E5%AE%9A%E4%B9%89view/)


##License


```java


		Copyright (C) 2016 ruzhan
		
		Licensed under the Apache License, Version 2.0 (the "License");
		you may not use this file except in compliance with the License.
		You may obtain a copy of the License at
		
		http://www.apache.org/licenses/LICENSE-2.0
		
		Unless required by applicable law or agreed to in writing, software
		distributed under the License is distributed on an "AS IS" BASIS,
		WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
		See the License for the specific language governing permissions and
		limitations under the License.


```