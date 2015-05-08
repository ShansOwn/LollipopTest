package com.shansown.android.lollipoptest.rx;

import android.os.Bundle;

import com.shansown.android.lollipoptest.BaseActivity;
import com.shansown.android.lollipoptest.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.shansown.android.lollipoptest.util.LogUtils.LOGD;
import static com.shansown.android.lollipoptest.util.LogUtils.makeLogTag;

public class RxDemoActivity extends BaseActivity {

    private static final String TAG = makeLogTag(RxDemoActivity.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rx);
        getActionBarToolbar().setNavigationOnClickListener(view -> finish());
    }

    @OnClick(R.id.map)
    protected void onMapClicked() {
        LOGD(TAG, "___onMapClicked___");
        startMapTest();
    }

    @OnClick(R.id.flatMap)
    protected void onFlatMapClicked() {
        LOGD(TAG, "___onFlatMapClicked___");
        startFlatMapTest();
    }

    @OnClick(R.id.flatMap_Map)
    protected void onFlatMap_MapClicked() {
        LOGD(TAG, "___onFlatMap_MapClicked___");
        startFlatMap_MapTest();
    }

    private void startMapTest() {
        LOGD(TAG, "startMapTest");
        Observable.from(initData())
                .map(string -> {
                    LOGD(TAG, "on Map: " + Thread.currentThread());
                    doSlowOperation();
                    return string + "-mapped";
                })
                .map(s -> {
                    LOGD(TAG, "on Another Map: " + Thread.currentThread());
                    return s + "-another_mapped";
                })
                .map(s -> {
                    LOGD(TAG, "on More Another Map: " + Thread.currentThread());
                    return s + "-final_mapped";
                })
                .filter(s -> {
                    boolean filter = ((s.hashCode() & 1) == 0);
                    LOGD(TAG, "Filter: " + filter + " - " + s);
                    return filter;
                })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                        LOGD(TAG, "on Map Completed:" + " - " + Thread.currentThread());
                    }

                    @Override
                    public void onError(Throwable e) {
                        LOGD(TAG, "on Map Error" + " - " + Thread.currentThread());
                    }

                    @Override
                    public void onNext(String string) {
                        LOGD(TAG, "on Map Next: " + string + " - " + Thread.currentThread());
                    }
                });
    }

    private void startFlatMapTest() {
        LOGD(TAG, "startFlatMapTest");
        /**
         * For flatMap demonstration we use {@link Observable#just(Object)}
         * instead {@link Observable#from(Iterable)}
         */
        Observable.just(initData())
                .flatMap(Observable::from)
                .flatMap(string -> {
                    LOGD(TAG, "on FlatMap: " + Thread.currentThread());
                    doSlowOperation();
                    return Observable.just(string + "-flatMapped");
                })
                .flatMap(s -> {
                    LOGD(TAG, "on Another FlatMap: " + Thread.currentThread());
                    return Observable.just(s + "-another_flatMapped");
                })
                .flatMap(s -> {
                    LOGD(TAG, "on More Another FlatMap: " + Thread.currentThread());
                    return Observable.just(s + "-final_flatMapped");
                })
                .filter(s -> {
                    boolean filter = ((s.hashCode() & 1) == 0);
                    LOGD(TAG, "Filter: " + filter + " - " + s);
                    return filter;
                })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                        LOGD(TAG, "on FlatMap Completed:" + " - " + Thread.currentThread());
                    }

                    @Override
                    public void onError(Throwable e) {
                        LOGD(TAG, "on FlatMap Error" + " - " + Thread.currentThread());
                    }

                    @Override
                    public void onNext(String string) {
                        LOGD(TAG, "on FlatMap Next: " + string + " - " + Thread.currentThread());
                    }
                });
    }

    private void startFlatMap_MapTest() {
        Observable.just(initData())
                .flatMap(Observable::from)
                .map(string -> {
                    LOGD(TAG, "on FlatMap_Map: " + Thread.currentThread());
                    doSlowOperation();
                    return string + "-flatMapped";
                })
                .map(s -> {
                    LOGD(TAG, "on Another FlatMap_Map: " + Thread.currentThread());
                    return s + "-another_flatMapped";
                })
                .map(s -> {
                    LOGD(TAG, "on More Another FlatMap_Map: " + Thread.currentThread());
                    return s + "-final_flatMapped";
                })
                .filter(s -> {
                    boolean filter = ((s.hashCode() & 1) == 0);
                    LOGD(TAG, "Filter: " + filter + " - " + s);
                    return filter;
                })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((s) -> LOGD(TAG, "on FlatMap_Map Next: " + s + " - " + Thread.currentThread()),
                        (e) -> LOGD(TAG, "on FlatMap_Map Error" + " - " + Thread.currentThread()),
                        () -> LOGD(TAG, "on FlatMap_Map Completed:" + " - " + Thread.currentThread()));
    }

    private List<String> initData() {
        LOGD(TAG, "initData");
        List<String> strings = new ArrayList<>();
        String string = "String";
        for (int i = 0; i < 100; i++) {
            strings.add(string + "-" + i);
        }
        return strings;
    }

    private void doSlowOperation() {
        LOGD(TAG, "doSlowOperation - " + Thread.currentThread());
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}