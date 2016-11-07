package ru.mdsps.views.waveprogressview.test;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.SeekBar;

import ru.mdsps.views.waveprogressview.WaveProgressView;

public class MainActivity extends AppCompatActivity {

    private WaveProgressView mWaveView;
    private RadioGroup mRadioGroup;
    private SeekBar mLtSeekBar, mRtSeekBar, mLbSeekBar, mRbSeekBar;
    private LinearLayout mSeekBarBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findElements();
        mRadioGroup.setOnCheckedChangeListener(mRadioCheck);
        mLtSeekBar.setProgress((int) mWaveView.getTopLeftCornerRadius());
        mLtSeekBar.setOnSeekBarChangeListener(mSeekChange);
        mRtSeekBar.setProgress((int) mWaveView.getTopRightCornerRadius());
        mRtSeekBar.setOnSeekBarChangeListener(mSeekChange);
        mLbSeekBar.setProgress((int) mWaveView.getBottomLeftCornerRadius());
        mLbSeekBar.setOnSeekBarChangeListener(mSeekChange);
        mRbSeekBar.setProgress((int) mWaveView.getBottomRightCornerRadius());
        mRbSeekBar.setOnSeekBarChangeListener(mSeekChange);
    }

    private void findElements(){
        mWaveView = (WaveProgressView) findViewById(R.id.wave_progress);
        mRadioGroup = (RadioGroup) findViewById(R.id.types);
        mSeekBarBox = (LinearLayout) findViewById(R.id.seek_bar_box);
        mLtSeekBar = (SeekBar) findViewById(R.id.seek_bar_lt);
        mLtSeekBar.setMax(60);
        mRtSeekBar = (SeekBar) findViewById(R.id.seek_bar_rt);
        mRtSeekBar.setMax(60);
        mLbSeekBar = (SeekBar) findViewById(R.id.seek_bar_lb);
        mLbSeekBar.setMax(60);
        mRbSeekBar = (SeekBar) findViewById(R.id.seek_bar_rb);
        mRbSeekBar.setMax(60);

    }

    RadioGroup.OnCheckedChangeListener mRadioCheck = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
            switch (checkedId) {
                case R.id.type_rectangle:
                    mWaveView.setViewType(WaveProgressView.VIEW_TYPE_RECTANGLE);
                    mSeekBarBox.setVisibility(View.VISIBLE);
                    break;
                case R.id.type_oval:
                    mWaveView.setViewType(WaveProgressView.VIEW_TYPE_OVAL);
                    mSeekBarBox.setVisibility(View.GONE);
                    break;
                case R.id.type_square:
                    mWaveView.setViewType(WaveProgressView.VIEW_TYPE_SQUARE);
                    mSeekBarBox.setVisibility(View.VISIBLE);
                    break;
                case R.id.type_circle:
                    mWaveView.setViewType(WaveProgressView.VIEW_TYPE_CIRCLE);
                    mSeekBarBox.setVisibility(View.GONE);
                    break;
            }
        }
    };

    SeekBar.OnSeekBarChangeListener mSeekChange = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            switch(seekBar.getId()){
                case R.id.seek_bar_lt:
                    mWaveView.setTopLeftCornerRadius(i);
                    break;
                case R.id.seek_bar_rt:
                    mWaveView.setTopRightCornerRadius(i);
                    break;
                case R.id.seek_bar_lb:
                    mWaveView.setBottomLeftCornerRadius(i);
                    break;
                case R.id.seek_bar_rb:
                    mWaveView.setBottomRightCornerRadius(i);
                    break;
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };
}
