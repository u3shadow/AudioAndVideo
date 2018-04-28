package com.u3coding.audioandvideo;

import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.u3coding.playerandrecoder.audiorecoder.RecordAudioActivity;
import com.u3coding.playerandrecoder.audiorecoder.RecordAudioViewModel;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;

@RunWith(AndroidJUnit4.class)
public class RecordAudioActivityTest {
    @Rule
    public ActivityTestRule<RecordAudioActivity> mActivityRule = new ActivityTestRule(RecordAudioActivity.class);
    @Test
    public void test_view_is_show(){
        onView(ViewMatchers.withId(R.id.bt_start)).check(matches(isDisplayed()));
        onView(ViewMatchers.withId(R.id.bt_stop)).check(matches(isDisplayed()));
        onView(ViewMatchers.withId(R.id.tv_record)).check(matches(isDisplayed()));
    }
}
