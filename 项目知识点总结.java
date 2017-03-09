QQ影音总结

1、基类BaseActivity。规范代码结构、提供公共的方法。

2、主界面 MainActivity。
	2.1	指示器的滑动的计算。
			// 指示器移动的位置 = 起始位置 + 偏移位置
			// 起始位置 = position * 指示器宽度
			// 偏移位置 = 手指划过屏幕的百分比 * 指示器宽度
			// 偏移位置2 = 手指划过屏幕的像素 / pager个数
	2.2	ViewPager嵌套Fragment的实现视频、音频界面的功能。
			new FragmentPagerAdapter();
			ViewPager.setAdapter();
	2.3	标题栏，选中标题实现高亮、变大显示的功能。
			OnPageChangeListener 的选中变化监听中onPageSelected();
			//视频高亮，音频半灰色显示。
            tv_video.setTextColor(green);
            tv_audio.setTextColor(halfwhite);
            //选中的变大，没有选中的还原动画
            ViewPropertyAnimator.animate(tv_video).scaleX(1.2f).scaleY(1.2f);
            ViewPropertyAnimator.animate(tv_audio).scaleX(1.0f).scaleY(1.0f);

	3、视频界面。
			3.1 填充视频列表
			3.1.1 从MediaProvider查询数据
			3.1.2 CursorAdapter
			3.1.2.1 newView方法创建新的view
			3.1.2.2 bindView方法填充view
			3.1.2.3 使用cursor必须包含 _id 列
			3.1.3 异步查询 AsyncQueryHandler
			3.1.3.1 startXXX 方法开启子线程查询
			3.1.3.2 onXXXComplete 方法回到主线程执行操作

			3.2 视频播放模块(VideoView)
			3.2.1 MediaPlayer 是Android系统里唯一一个用于播放音视频的类
			3.2.2 VideoView 是SurfaceView的子类，封装了MediaPlayer，只是用于视频画面的显示

			3.3 系统电量
			3.3.1 注册广播接收者，接收电量的变化监听。
	 		IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
	 		3.3.2 在广播接收者里获取电量
	 		int level = intent.getIntExtra("level", 0);

			3.4 音量控制
			3.4.1 自定SeekBar
			3.4.1.1 指示器图片
				android:thumb="@drawable/video_progress_thumb"
			3.4.1.2 指示器只显示一半的解决办法
				android:thumbOffset="0dp"
			3.4.1.3 修改进度条、背景色的图片
				参照style="@android:style/Widget.SeekBar"
				创建 android:progressDrawable="@drawable/video_seekbar_drawable"
			3.4.2 获取音量
			mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        	int maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
			3.4.3 设置音量
        	mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, flag);
        	// flag为1则显示系统的音量提示框，为0则不显示

			3.5 手势滑动，控制音量和屏幕亮度
			3.5.1 在Activity的onTouchEvent事件处理
			3.5.2 算法
				最终的音量 = 手指压下时的音量 + 变化的音量
				变化的音量 = 手指划过屏幕的百分比 * 最大音量
				手指划过屏幕的百分比 =  手指划过屏幕的距离 / 屏幕高度
				手指划过屏幕的距离 = 手指当前位置 - 手指压下时的位置
			3.6 播放进度控制

			3.7 隐藏控制面板(单击、双击、长按)
			3.7.1 获取View高度
				3.7.1.1 getMeasuredHeight，
					优点：只要执行了measure方法之后就可以获取到高度。
					缺点：在嵌套使用布局的情况下，有可能获取不到正确宽高。
				3.7.1.2 getHeight，
					优点：只要能获取到宽高就必定是准确的
					缺点：执行了onLayout方法之后才能获取到高度，在onCreate过程无法获取到高度。
					 ll_bottom.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
						@Override
						public void onGlobalLayout() {
				//                logE("VideoPlayerActivity.onGlobalLayout");
						ll_bottom.getViewTreeObserver().removeGlobalOnLayoutListener(this);
						ViewPropertyAnimator.animate(ll_bottom).translationY(ll_bottom.getHeight());
						}
					});
				3.7.2 手势监听
					3.7.2.1 创建监听对象
						gestureDetector = new GestureDetector(this, new SimpleOnGestureListener());
					3.7.2.2 拦截touch事件
						public boolean onTouchEvent(MotionEvent event) {
							gestureDetector.onTouchEvent(event);
							//.....
						}
				3.7.2.3 复写SimpleOnGestureListener的回调方法 onSingleTapConfirmed

	4、音频部分
			4.1  填充音乐列表
				使用provider.MediaStore.Audio.Media；查询数据库获取，sdcard中的音频数据填充列表。
			4.2 后台Service播放歌曲
			4.2.1 Activity控制Service，使用bindService获取Binder对象
			4.2.2 Service控制Activity，使用广播或其他的解耦的方式

			4.3 示波器
			使用帧动画实现，示泼器不断循环的动画。
			4.4 播放顺序
			单曲循环、列表播放、随机播放。

			4.5 自定义布局的消息通知
			4.5.1 Android3.0以前使用
				//在顶部显示的通知。
				Notification notification = new Notification(R.drawable.icon, "正在播放： dida", System.currentTimeMillis());
				//在通知里显示的通知。
				notification.setLatestEventInfo(this, "滴答", "侃侃", getContentIntent());
				该方法在Android6.0已被删除
			4.5.2 Android3.0 以后使用
				Notification.Builder builder = new Notification.Builder(this);
			4.5.3 自定义布局的通知
				4.5.3.1 notification.contentView = getRemoteView(); // 不建议
				4.5.3.2 builder.setContent(getRemoteView());
				4.5.3.2 自定义布局的通知在Android 3.0以前是不可用的
			4.5.4 不可移除的通知
				4.5.4.1 notification.flags = Notification.FLAG_ONGOING_EVENT; // 不建议
				4.5.4.2 builder.setOngoing(true);

	5、自定义歌词控件
	5.0 继承TextView可以省去自己处理onMeasure方法
	5.1 绘制单行居中的文本
		5.1.1 算法
			x = view一半宽度 - 文字的一半宽度
			y = view一半高度 + 文字的一半高度
		5.1.2 获取屏幕的宽高，在重写onSizeChange方法中获取。
		5.1.3 获取文本的宽高
			5.1.3.1 使用Rect获取宽高
				Rect bounds = new Rect();
				mPaint.getTextBounds(text, 0, text.length(), bounds);
				此方法获取的宽度在Eclipse预览里不能正常工作
			5.1.3.2 使用mPaint.measureText(text)获取宽度
				此方法在任意情况下都能正常工作

	5.2 绘制多行水平居中的文本
		5.2.1 获取高亮行行数
		5.2.2 获取高亮行的Y位置
		5.2.3 按行绘制文本
			x = 水平居中使用的x
			y = 居中行的Y位置 + (绘制行的行数 - 高亮行的行数) * 行高。

	5.3 按行滚动歌词  (根据当前的播放时间按行滚动歌词)
		public void Roll(int position, int duration) {
        //如果当前播放的时间大于 本行的开始时间，小于下行的开始时间，当前播放的时间的行就是高亮行
        for (int i = 0; i < list.size(); i++) {
            //获取当前行的歌词
            LyricBean lyric = list.get(i);
            if (i == list.size() - 1) {
                //最后一行
                endPosition = duration;
            } else {
                //获取下一行的歌词
                LyricBean lyricNext = list.get(i + 1);
                endPosition = lyricNext.getStartPosition();
            }

            if (lyric.getStartPosition() < position && endPosition > position) {
                currentPosition = i;
                break;
            }
        }
        invalidate();
    }
	5.4 平滑滚动歌词    (根据每句歌词的播放时间不同，而高亮行显示的时间不同)
		偏移位置 = 经过的时间百分比  *  行高
		经过的时间百分比 = 经过的时间 / 行可用的时间
		经过的时间 = 已播放时间 - 行起始时间
		行可用时间 = 下一行起始时间 - 当前行起始时间
	5.5 从歌词文件解析歌词
			使用BufferedReader解析文件中的歌词成集合。
			BufferedReader buffer = new BufferedReader(new InputStreamReader(new FileInputStream(lyricsFile),"GBK"));
            String line  = buffer.readLine();
            while (line!=null){
                List<LyricBean> lineList = parserLine(line);
                lyricsList.addAll(lineList);

                line = buffer.readLine();
            }
	5.6 歌词文件加载模块
		

