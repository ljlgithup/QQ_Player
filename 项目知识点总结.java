QQӰ���ܽ�

1������BaseActivity���淶����ṹ���ṩ�����ķ�����

2�������� MainActivity��
	2.1	ָʾ���Ļ����ļ��㡣
			// ָʾ���ƶ���λ�� = ��ʼλ�� + ƫ��λ��
			// ��ʼλ�� = position * ָʾ�����
			// ƫ��λ�� = ��ָ������Ļ�İٷֱ� * ָʾ�����
			// ƫ��λ��2 = ��ָ������Ļ������ / pager����
	2.2	ViewPagerǶ��Fragment��ʵ����Ƶ����Ƶ����Ĺ��ܡ�
			new FragmentPagerAdapter();
			ViewPager.setAdapter();
	2.3	��������ѡ�б���ʵ�ָ����������ʾ�Ĺ��ܡ�
			OnPageChangeListener ��ѡ�б仯������onPageSelected();
			//��Ƶ��������Ƶ���ɫ��ʾ��
            tv_video.setTextColor(green);
            tv_audio.setTextColor(halfwhite);
            //ѡ�еı��û��ѡ�еĻ�ԭ����
            ViewPropertyAnimator.animate(tv_video).scaleX(1.2f).scaleY(1.2f);
            ViewPropertyAnimator.animate(tv_audio).scaleX(1.0f).scaleY(1.0f);

	3����Ƶ���档
			3.1 �����Ƶ�б�
			3.1.1 ��MediaProvider��ѯ����
			3.1.2 CursorAdapter
			3.1.2.1 newView���������µ�view
			3.1.2.2 bindView�������view
			3.1.2.3 ʹ��cursor������� _id ��
			3.1.3 �첽��ѯ AsyncQueryHandler
			3.1.3.1 startXXX �����������̲߳�ѯ
			3.1.3.2 onXXXComplete �����ص����߳�ִ�в���

			3.2 ��Ƶ����ģ��(VideoView)
			3.2.1 MediaPlayer ��Androidϵͳ��Ψһһ�����ڲ�������Ƶ����
			3.2.2 VideoView ��SurfaceView�����࣬��װ��MediaPlayer��ֻ��������Ƶ�������ʾ

			3.3 ϵͳ����
			3.3.1 ע��㲥�����ߣ����յ����ı仯������
	 		IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
	 		3.3.2 �ڹ㲥���������ȡ����
	 		int level = intent.getIntExtra("level", 0);

			3.4 ��������
			3.4.1 �Զ�SeekBar
			3.4.1.1 ָʾ��ͼƬ
				android:thumb="@drawable/video_progress_thumb"
			3.4.1.2 ָʾ��ֻ��ʾһ��Ľ���취
				android:thumbOffset="0dp"
			3.4.1.3 �޸Ľ�����������ɫ��ͼƬ
				����style="@android:style/Widget.SeekBar"
				���� android:progressDrawable="@drawable/video_seekbar_drawable"
			3.4.2 ��ȡ����
			mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        	int maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
			3.4.3 ��������
        	mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, flag);
        	// flagΪ1����ʾϵͳ��������ʾ��Ϊ0����ʾ

			3.5 ���ƻ�����������������Ļ����
			3.5.1 ��Activity��onTouchEvent�¼�����
			3.5.2 �㷨
				���յ����� = ��ָѹ��ʱ������ + �仯������
				�仯������ = ��ָ������Ļ�İٷֱ� * �������
				��ָ������Ļ�İٷֱ� =  ��ָ������Ļ�ľ��� / ��Ļ�߶�
				��ָ������Ļ�ľ��� = ��ָ��ǰλ�� - ��ָѹ��ʱ��λ��
			3.6 ���Ž��ȿ���

			3.7 ���ؿ������(������˫��������)
			3.7.1 ��ȡView�߶�
				3.7.1.1 getMeasuredHeight��
					�ŵ㣺ֻҪִ����measure����֮��Ϳ��Ի�ȡ���߶ȡ�
					ȱ�㣺��Ƕ��ʹ�ò��ֵ�����£��п��ܻ�ȡ������ȷ��ߡ�
				3.7.1.2 getHeight��
					�ŵ㣺ֻҪ�ܻ�ȡ����߾ͱض���׼ȷ��
					ȱ�㣺ִ����onLayout����֮����ܻ�ȡ���߶ȣ���onCreate�����޷���ȡ���߶ȡ�
					 ll_bottom.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
						@Override
						public void onGlobalLayout() {
				//                logE("VideoPlayerActivity.onGlobalLayout");
						ll_bottom.getViewTreeObserver().removeGlobalOnLayoutListener(this);
						ViewPropertyAnimator.animate(ll_bottom).translationY(ll_bottom.getHeight());
						}
					});
				3.7.2 ���Ƽ���
					3.7.2.1 ������������
						gestureDetector = new GestureDetector(this, new SimpleOnGestureListener());
					3.7.2.2 ����touch�¼�
						public boolean onTouchEvent(MotionEvent event) {
							gestureDetector.onTouchEvent(event);
							//.....
						}
				3.7.2.3 ��дSimpleOnGestureListener�Ļص����� onSingleTapConfirmed

	4����Ƶ����
			4.1  ��������б�
				ʹ��provider.MediaStore.Audio.Media����ѯ���ݿ��ȡ��sdcard�е���Ƶ��������б�
			4.2 ��̨Service���Ÿ���
			4.2.1 Activity����Service��ʹ��bindService��ȡBinder����
			4.2.2 Service����Activity��ʹ�ù㲥�������Ľ���ķ�ʽ

			4.3 ʾ����
			ʹ��֡����ʵ�֣�ʾ��������ѭ���Ķ�����
			4.4 ����˳��
			����ѭ�����б��š�������š�

			4.5 �Զ��岼�ֵ���Ϣ֪ͨ
			4.5.1 Android3.0��ǰʹ��
				//�ڶ�����ʾ��֪ͨ��
				Notification notification = new Notification(R.drawable.icon, "���ڲ��ţ� dida", System.currentTimeMillis());
				//��֪ͨ����ʾ��֪ͨ��
				notification.setLatestEventInfo(this, "�δ�", "٩٩", getContentIntent());
				�÷�����Android6.0�ѱ�ɾ��
			4.5.2 Android3.0 �Ժ�ʹ��
				Notification.Builder builder = new Notification.Builder(this);
			4.5.3 �Զ��岼�ֵ�֪ͨ
				4.5.3.1 notification.contentView = getRemoteView(); // ������
				4.5.3.2 builder.setContent(getRemoteView());
				4.5.3.2 �Զ��岼�ֵ�֪ͨ��Android 3.0��ǰ�ǲ����õ�
			4.5.4 �����Ƴ���֪ͨ
				4.5.4.1 notification.flags = Notification.FLAG_ONGOING_EVENT; // ������
				4.5.4.2 builder.setOngoing(true);

	5���Զ����ʿؼ�
	5.0 �̳�TextView����ʡȥ�Լ�����onMeasure����
	5.1 ���Ƶ��о��е��ı�
		5.1.1 �㷨
			x = viewһ���� - ���ֵ�һ����
			y = viewһ��߶� + ���ֵ�һ��߶�
		5.1.2 ��ȡ��Ļ�Ŀ�ߣ�����дonSizeChange�����л�ȡ��
		5.1.3 ��ȡ�ı��Ŀ��
			5.1.3.1 ʹ��Rect��ȡ���
				Rect bounds = new Rect();
				mPaint.getTextBounds(text, 0, text.length(), bounds);
				�˷�����ȡ�Ŀ����EclipseԤ���ﲻ����������
			5.1.3.2 ʹ��mPaint.measureText(text)��ȡ���
				�˷�������������¶�����������

	5.2 ���ƶ���ˮƽ���е��ı�
		5.2.1 ��ȡ����������
		5.2.2 ��ȡ�����е�Yλ��
		5.2.3 ���л����ı�
			x = ˮƽ����ʹ�õ�x
			y = �����е�Yλ�� + (�����е����� - �����е�����) * �иߡ�

	5.3 ���й������  (���ݵ�ǰ�Ĳ���ʱ�䰴�й������)
		public void Roll(int position, int duration) {
        //�����ǰ���ŵ�ʱ����� ���еĿ�ʼʱ�䣬С�����еĿ�ʼʱ�䣬��ǰ���ŵ�ʱ����о��Ǹ�����
        for (int i = 0; i < list.size(); i++) {
            //��ȡ��ǰ�еĸ��
            LyricBean lyric = list.get(i);
            if (i == list.size() - 1) {
                //���һ��
                endPosition = duration;
            } else {
                //��ȡ��һ�еĸ��
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
	5.4 ƽ���������    (����ÿ���ʵĲ���ʱ�䲻ͬ������������ʾ��ʱ�䲻ͬ)
		ƫ��λ�� = ������ʱ��ٷֱ�  *  �и�
		������ʱ��ٷֱ� = ������ʱ�� / �п��õ�ʱ��
		������ʱ�� = �Ѳ���ʱ�� - ����ʼʱ��
		�п���ʱ�� = ��һ����ʼʱ�� - ��ǰ����ʼʱ��
	5.5 �Ӹ���ļ��������
			ʹ��BufferedReader�����ļ��еĸ�ʳɼ��ϡ�
			BufferedReader buffer = new BufferedReader(new InputStreamReader(new FileInputStream(lyricsFile),"GBK"));
            String line  = buffer.readLine();
            while (line!=null){
                List<LyricBean> lineList = parserLine(line);
                lyricsList.addAll(lineList);

                line = buffer.readLine();
            }
	5.6 ����ļ�����ģ��
		

