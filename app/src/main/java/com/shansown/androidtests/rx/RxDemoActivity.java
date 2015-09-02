package com.shansown.androidtests.rx;

import android.os.Bundle;

import com.shansown.androidtests.BaseActivity;
import com.shansown.androidtests.R;
import java.util.ArrayList;
import java.util.List;

import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.shansown.androidtests.util.LogUtils.LOGD;
import static com.shansown.androidtests.util.LogUtils.makeLogTag;

public class RxDemoActivity extends BaseActivity {

  private static final String TAG = makeLogTag(RxDemoActivity.class);

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_rx);
    getActionBarToolbar().setNavigationOnClickListener(view -> finish());
  }

  @OnClick(R.id.map) protected void onMapClicked() {
    LOGD(TAG, "___onMapClicked___");
    startMapTest();
  }

  @OnClick(R.id.flatMap) protected void onFlatMapClicked() {
    LOGD(TAG, "___onFlatMapClicked___");
    startFlatMapTest();
  }

  @OnClick(R.id.flatMapIterable) protected void onFlatMapIterableClicked() {
    LOGD(TAG, "onFlatMapIterableClicked");
    startFlatMapIterableTest();
  }

  @OnClick(R.id.flatMap_Map) protected void onFlatMap_MapClicked() {
    LOGD(TAG, "___onFlatMap_MapClicked___");
    startFlatMap_MapTest();
  }

  @OnClick(R.id.zip) protected void onZipClicked() {
    LOGD(TAG, "___onZipClicked___");
    startZipTest();
  }

  @OnClick(R.id.combineLatest) protected void onCombineLatestClicked() {
    LOGD(TAG, "___onCombineLatestClicked___");
    startCombineLatestTest();
  }

  @OnClick(R.id.merge) protected void onMergeClicked() {
    LOGD(TAG, "___onMergeClicked___");
    startMergeTest();
  }

  @OnClick(R.id.defer) protected void onDeferClicked() {
    LOGD(TAG, "___onDeferClicked___");
    startDeferTest();
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
          @Override public void onCompleted() {
            LOGD(TAG, "on Map Completed:" + " - " + Thread.currentThread());
          }

          @Override public void onError(Throwable e) {
            LOGD(TAG, "on Map Error" + " - " + Thread.currentThread());
          }

          @Override public void onNext(String string) {
            LOGD(TAG, "___on Map Next: " + string + " - " + Thread.currentThread());
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
          @Override public void onCompleted() {
            LOGD(TAG, "on FlatMap Completed:" + " - " + Thread.currentThread());
          }

          @Override public void onError(Throwable e) {
            LOGD(TAG, "on FlatMap Error" + " - " + Thread.currentThread());
          }

          @Override public void onNext(String string) {
            LOGD(TAG, "___on FlatMap Next: " + string + " - " + Thread.currentThread());
          }
        });
  }

  private void startFlatMapIterableTest() {
    LOGD(TAG, "startFlatMapTest");
    Observable.just(initData())
        .flatMapIterable(strings -> strings)
        .flatMap(string -> {
          LOGD(TAG, "on FlatMapIterable: " + Thread.currentThread());
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
          @Override public void onCompleted() {
            LOGD(TAG, "on FlatMapIterable Completed:" + " - " + Thread.currentThread());
          }

          @Override public void onError(Throwable e) {
            LOGD(TAG, "on FlatMapIterable Error" + " - " + Thread.currentThread());
          }

          @Override public void onNext(String string) {
            LOGD(TAG, "___on FlatMapIterable Next: " + string + " - " + Thread.currentThread());
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
        .subscribe(
            (s) -> LOGD(TAG, "___on FlatMap_Map Next: " + s + " - " + Thread.currentThread()),
            (e) -> LOGD(TAG, "on FlatMap_Map Error" + " - " + Thread.currentThread()),
            () -> LOGD(TAG, "on FlatMap_Map Completed:" + " - " + Thread.currentThread()));
  }

  private void startZipTest() {
    Observable.zip(Observable.from(initData()).filter(s -> {
      boolean filter = ((s.hashCode() & 1) == 0);
      LOGD(TAG, "Filter1: " + filter + " - " + s);
      return filter;
    }), Observable.from(initSmallData()).filter(s -> {
      boolean filter = ((s.hashCode() & 1) == 0);
      LOGD(TAG, "Filter2: " + filter + " - " + s);
      return filter;
    }), (s, s2) -> {
      LOGD(TAG, "on Zip: " + Thread.currentThread());
      doSlowOperation();
      return s + " - " + s2;
    })
        .subscribeOn(Schedulers.computation())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe((s) -> LOGD(TAG, "___on Zip Next: " + s + " - " + Thread.currentThread()),
            (e) -> LOGD(TAG, "on Zip Error" + " - " + Thread.currentThread()),
            () -> LOGD(TAG, "on Zip Completed:" + " - " + Thread.currentThread()));
  }

  private void startCombineLatestTest() {
    Observable.combineLatest(Observable.from(initData()).filter(s -> {
      boolean filter = ((s.hashCode() & 1) == 0);
      LOGD(TAG, "Filter1: " + filter + " - " + s);
      return filter;
    }), Observable.from(initSmallData()).filter(s -> {
      boolean filter = ((s.hashCode() & 1) == 0);
      LOGD(TAG, "Filter2: " + filter + " - " + s);
      return filter;
    }), (s, s2) -> {
      LOGD(TAG, "on CombineLatest: " + Thread.currentThread());
      doSlowOperation();
      return s + " - " + s2;
    })
        .subscribeOn(Schedulers.computation())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            (s) -> LOGD(TAG, "___on CombineLatest Next: " + s + " - " + Thread.currentThread()),
            (e) -> LOGD(TAG, "on CombineLatest Error" + " - " + Thread.currentThread()),
            () -> LOGD(TAG, "on CombineLatest Completed:" + " - " + Thread.currentThread()));
  }

  private void startMergeTest() {
    Observable.merge(Observable.from(initData()).filter(s -> {
      boolean filter = ((s.hashCode() & 1) == 0);
      LOGD(TAG, "Filter1: " + filter + " - " + s);
      return filter;
    }), Observable.from(initSmallData()).filter(s -> {
      boolean filter = ((s.hashCode() & 1) == 0);
      LOGD(TAG, "Filter2: " + filter + " - " + s);
      return filter;
    }))
        .subscribeOn(Schedulers.computation())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe((s) -> LOGD(TAG, "___on Merge Next: " + s + " - " + Thread.currentThread()),
            (e) -> LOGD(TAG, "on Merge Error" + " - " + Thread.currentThread()),
            () -> LOGD(TAG, "on Merge Completed:" + " - " + Thread.currentThread()));
  }

  private void startDeferTest() {
    Observable.defer(() -> Observable.just(initSlowData()))
        .flatMap(Observable::from)
        .map(str -> {
          LOGD(TAG, "on Defer_Map: " + str);
          return str;
        })
        .subscribeOn(Schedulers.computation())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe((s) -> LOGD(TAG, "___on Defer Next: " + s + " - " + Thread.currentThread()),
            (e) -> LOGD(TAG, "on Defer Error" + " - " + Thread.currentThread()),
            () -> LOGD(TAG, "on Defer Completed:" + " - " + Thread.currentThread()));
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

  private List<String> initSmallData() {
    LOGD(TAG, "initSmallData");
    List<String> strings = new ArrayList<>();
    String string = "String";
    for (int i = 1000; i > 980; i--) {
      strings.add(string + "-" + i);
    }
    return strings;
  }

  private List<String> initSlowData() {
    LOGD(TAG, "initData");
    doVerySlowOperation();
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

  private void doVerySlowOperation() {
    LOGD(TAG, "doVerySlowOperation - " + Thread.currentThread());
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}