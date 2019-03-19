package cc.rome753.myapplication;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.media.MediaSyncEvent;
import android.media.audiofx.Visualizer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    //指定音频源 这个和MediaRecorder是相同的 MediaRecorder.AudioSource.MIC指的是麦克风
    private static final int mAudioSource = MediaRecorder.AudioSource.MIC;
    //指定采样率 （MediaRecoder 的采样率通常是8000Hz AAC的通常是44100Hz。 设置采样率为44100，目前为常用的采样率，官方文档表示这个值可以兼容所有的设置）
//    private static final int mSampleRateInHz=44100 ;
    private static final int mSampleRateInHz=4096;
    //指定捕获音频的声道数目。在AudioFormat类中指定用于此的常量
    private static final int mChannelConfig= AudioFormat.CHANNEL_CONFIGURATION_MONO; //单声道
    //指定音频量化位数 ,在AudioFormaat类中指定了以下各种可能的常量。通常我们选择ENCODING_PCM_16BIT和ENCODING_PCM_8BIT PCM代表的是脉冲编码调制，它实际上是原始音频样本。
    //因此可以设置每个样本的分辨率为16位或者8位，16位将占用更多的空间和处理能力,表示的音频也更加接近真实。
    private static final int mAudioFormat=AudioFormat.ENCODING_PCM_16BIT;
    //指定缓冲区大小。调用AudioRecord类的getMinBufferSize方法可以获得。
    private int mBufferSizeInBytes= AudioRecord.getMinBufferSize(mSampleRateInHz,mChannelConfig, mAudioFormat);//计算最小缓冲区
    //创建AudioRecord。AudioRecord类实际上不会保存捕获的音频，因此需要手动创建文件并保存下载。
    private AudioRecord mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,mSampleRateInHz,mChannelConfig,
            mAudioFormat, mBufferSizeInBytes);//创建AudioRecorder对象

    private SoundTexture mSoundTexture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSoundTexture = findViewById(R.id.st);



        new Thread() {
            @Override
            public void run() {
                mAudioRecord.startRecording();
                initAudioTrack();
                initVisualizer();
                while(start && mAudioRecord.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING) {
                    byte[] buffer = new byte[mBufferSizeInBytes];
                    int result = mAudioRecord.read(buffer, 0, mBufferSizeInBytes);
                    mAudioTrack.write(buffer, 0, mBufferSizeInBytes);
                }
            }
        }.start();
    }

    boolean start = true;

    @Override
    protected void onDestroy() {
        start = false;
        mVisualizer.setEnabled(false);
        mVisualizer.release();
        mAudioRecord.stop();
        mAudioRecord.release();
        super.onDestroy();
    }

    private AudioTrack mAudioTrack;
    private void initAudioTrack() {
        mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, mSampleRateInHz, mChannelConfig, mAudioFormat, mBufferSizeInBytes, AudioTrack.MODE_STREAM);
        mAudioTrack.play();
    }

    private Visualizer mVisualizer;
    private void initVisualizer() {
        mVisualizer = new Visualizer(mAudioTrack.getAudioSessionId());
        mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
        mVisualizer.setDataCaptureListener(new Visualizer.OnDataCaptureListener() {
            public void onWaveFormDataCapture(Visualizer visualizer, byte[] bytes,
                                              int samplingRate) {
                Log.e("chao", "mVisualizer onWaveFormDataCapture");
//                mVisualizerView.updateVisualizer(bytes);


            }

            public void onFftDataCapture(Visualizer visualizer, byte[] bytes, int samplingRate) {
                Log.e("chao", "mVisualizer onFftDataCapture");
//                mVisualizerView2.updateVisualizer(bytes);
                int max = 0, index = 0;
                int len = bytes.length / 2 + 1;
                for(int i = 2; i < len; i+=2) {
                    int amp = (int) Math.hypot(bytes[i], bytes[i + 1]);
                    if(amp > max) {
                        max = Math.max(max, amp);
                        index = i;
                    }

                }
                mSoundTexture.update(index * 8);

            }
        }, Visualizer.getMaxCaptureRate() / 2, false, true);
        mVisualizer.setEnabled(true);
    }
}
