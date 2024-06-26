package com.emojimixer.activities;

import static androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE;
import static com.emojimixer.functions.UIMethods.shadAnim;
import static com.emojimixer.functions.Utils.getRecyclerCurrentItem;
import static com.emojimixer.functions.Utils.setSnapHelper;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.CookieManager;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.emojimixer.MediaManager;
import com.emojimixer.R;
import com.emojimixer.adapters.EmojisSliderAdapter;
import com.emojimixer.ads.AdsManager;
import com.emojimixer.databinding.ActivityMainBinding;
import com.emojimixer.functions.EmojiMixer;
import com.emojimixer.functions.RequestNetwork;
import com.emojimixer.functions.RequestNetworkController;
import com.emojimixer.functions.Utils;
import com.emojimixer.functions.offsetItemDecoration;
import com.emojimixer.utils.Common;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jakewharton.rxbinding4.view.RxView;
import com.vapp.admoblibrary.ads.AdmobUtils;

import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private ImageView ivChoose1;
    private ImageView ivChoose2;
    private TextView btnMerge;
    private String emote1;
    private String emote2;
    private String finalEmojiURL;
    private RecyclerView emojisSlider1;
    private ArrayList<HashMap<String, Object>> supportedEmojisList = new ArrayList<>();
    private RequestNetwork requestSupportedEmojis;
    private RequestNetwork.RequestListener requestSupportedEmojisListener;
    private SharedPreferences sharedPref;
    private boolean isFineToUseListeners = false;
    private LinearLayoutManager emojisSlider1LayoutManager;
    private LinearLayoutManager emojisSlider2LayoutManager;
    private final SnapHelper emojisSlider1SnapHelper = new LinearSnapHelper();
    private final SnapHelper emojisSlider2SnapHelper = new LinearSnapHelper();
    private int posItem1 = -1;
    private int posItem2 = -1;
    private String unicode1 = "u1f604";
    private String unicode2 = "u1f422";
    public String newDate = "20210218";
    private int type = 1;
    private ActivityMainBinding binding;
    private ArrayList<HashMap<String, Object>> listEmoji;
    private ArrayList<HashMap<String, Object>> listAnimal;
    private ArrayList<HashMap<String, Object>> listFood;
    private ArrayList<HashMap<String, Object>> listOther;
    private EmojisSliderAdapter emojiAdapter;
    ActivityResultLauncher<Intent> mLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                loadAndShowBannerCollap();
            });
    private Animation blinkAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        hideNavigation();
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        loadAndShowBannerCollap();
        initLogic();
        LOGIC_BACKEND();
    }

    private void loadAndShowBannerCollap() {
        if (AdmobUtils.isNetworkConnected(this)) {
            binding.frBanner.setVisibility(View.VISIBLE);
            binding.line.setVisibility(View.VISIBLE);
            AdsManager.showAdBannerCollapsible(this, AdsManager.INSTANCE.getBANNER_COLLAP_MERGE(), binding.frBanner, binding.line);
        } else {
            binding.frBanner.setVisibility(View.GONE);
            binding.line.setVisibility(View.GONE);
        }
    }

    private void loadAnimation(ImageView ivItem) {
        blinkAnimation = AnimationUtils.loadAnimation(this, R.anim.blink_animation);
        ivItem.startAnimation(blinkAnimation);
    }

    public void hideNavigation() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        hideNavigation();
    }

    @SuppressLint("CheckResult")
    public void initLogic() {
        ivChoose1 = binding.ivChoosePlayer1;
        ivChoose2 = binding.ivChoosePlayer2;
        btnMerge = binding.btnMerge;
        emojisSlider1 = binding.emojisSlider1;
        requestSupportedEmojis = new RequestNetwork(this);
        sharedPref = getSharedPreferences("AppData", Activity.MODE_PRIVATE);
        ivChoose1.setOnClickListener(v -> {
            binding.ivChoosePlayer2.clearAnimation();
            loadAnimation(binding.ivChoosePlayer1);
            type = 1;
            emojiAdapter.updateType(type);

        });
        binding.ivBack.setOnClickListener(v -> {
            finish();
        });
        ivChoose2.setOnClickListener(v -> {
            type = 2;
            binding.ivChoosePlayer1.clearAnimation();
            loadAnimation(binding.ivChoosePlayer2);
            emojiAdapter.updateType(type);
        });
        loadAnimation(binding.ivChoosePlayer1);

        RxView.clicks(binding.ivCategories1).throttleFirst(1000, TimeUnit.MILLISECONDS).subscribe(v -> {
            emojiAdapter.updateData(listEmoji);
            binding.ivCategories1.setBackgroundResource(R.drawable.img_choose_active);
            binding.ivCategories2.setBackgroundResource(R.drawable.img_choose_unactive);
            binding.ivCategories3.setBackgroundResource(R.drawable.img_choose_unactive);
            binding.ivCategories4.setBackgroundResource(R.drawable.img_choose_unactive);
        });
        RxView.clicks(binding.ivCategories2).throttleFirst(1000, TimeUnit.MILLISECONDS).subscribe(v -> {
            emojiAdapter.updateData(listFood);
            binding.ivCategories1.setBackgroundResource(R.drawable.img_choose_unactive);
            binding.ivCategories2.setBackgroundResource(R.drawable.img_choose_active);
            binding.ivCategories3.setBackgroundResource(R.drawable.img_choose_unactive);
            binding.ivCategories4.setBackgroundResource(R.drawable.img_choose_unactive);
        });
        RxView.clicks(binding.ivCategories3).throttleFirst(1000, TimeUnit.MILLISECONDS).subscribe(v -> {
            emojiAdapter.updateData(listOther);
            binding.ivCategories1.setBackgroundResource(R.drawable.img_choose_unactive);
            binding.ivCategories2.setBackgroundResource(R.drawable.img_choose_unactive);
            binding.ivCategories3.setBackgroundResource(R.drawable.img_choose_active);
            binding.ivCategories4.setBackgroundResource(R.drawable.img_choose_unactive);

        });
        RxView.clicks(binding.ivCategories4).throttleFirst(1000, TimeUnit.MILLISECONDS).subscribe(v -> {
            emojiAdapter.updateData(listAnimal);
            binding.ivCategories1.setBackgroundResource(R.drawable.img_choose_unactive);
            binding.ivCategories2.setBackgroundResource(R.drawable.img_choose_unactive);
            binding.ivCategories3.setBackgroundResource(R.drawable.img_choose_unactive);
            binding.ivCategories4.setBackgroundResource(R.drawable.img_choose_active);
        });
        binding.dontTouch.setVisibility(View.GONE);
        binding.dontTouch.setOnClickListener(v -> {

        });
        btnMerge.setOnClickListener(v -> {
            binding.dontTouch.setVisibility(View.VISIBLE);
            AdsManager.showAdInter(this, AdsManager.INSTANCE.getINTER_MERGE(), new AdsManager.AdListener() {
                @Override
                public void onAdClosed() {
                    binding.dontTouch.setVisibility(View.GONE);
                    handleReplaceActivityResult();
                }
            }, "1");
        });

        String response = AssetJSONFile("supported_emojis.json", this);
        Log.d("response==", response.toString());
        sharedPref.edit().putString("supportedEmojisList", response).apply();
        addDataToSliders(response);


//        requestSupportedEmojisListener = new RequestNetwork.RequestListener() {
//            @Override
//            public void onResponse(String tag, String response, HashMap<String, Object> responseHeaders) {
//                try {
//                    sharedPref.edit().putString("supportedEmojisList", response).apply();
//                    addDataToSliders(response);
//                } catch (Exception ignored) {
//                }
//            }
//
//            @Override
//            public void onErrorResponse(String tag, String message) {
//
//            }
//        };
    }

    private void handleReplaceActivityResult() {
        Intent intent = new Intent(this, ResultActivity.class)
                .putExtra("unicode1", unicode1)
                .putExtra("unicode2", unicode2)
                .putExtra("date", newDate);
        mLauncher.launch(intent);
    }

    private void LOGIC_BACKEND() {
        emojisSlider1LayoutManager = new GridLayoutManager(this, 5);
        emojisSlider2LayoutManager = new GridLayoutManager(this, 5);
        setSnapHelper(emojisSlider1, emojisSlider1SnapHelper, emojisSlider1LayoutManager);

        emojisSlider1.setLayoutManager(emojisSlider1LayoutManager);


        if (sharedPref.getString("supportedEmojisList", "").isEmpty()) {

        } else {
            addDataToSliders(sharedPref.getString("supportedEmojisList", ""));
        }
    }

    public String AssetJSONFile(String filename, Context context) {
        try {
            AssetManager manager = context.getAssets();
            InputStream file = manager.open(filename);
            byte[] formArray = new byte[file.available()];
            file.read(formArray);
            file.close();
            return new String(formArray);

        } catch (Exception ex) {
            return null;
        }
    }

    private void addDataToSliders(String data) {
        isFineToUseListeners = false;
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            supportedEmojisList = new Gson().fromJson(data, new TypeToken<ArrayList<HashMap<String, Object>>>() {
            }.getType());
            listEmoji = filterByCategory("emoji", supportedEmojisList);
            listAnimal = filterByCategory("animal", supportedEmojisList);
            listFood = filterByCategory("food", supportedEmojisList);
            listOther = filterByCategory("other", supportedEmojisList);

            handler.post(() -> {
                emojiAdapter = new EmojisSliderAdapter(listEmoji, MainActivity.this, type, new EmojisSliderAdapter.ICallBack() {
                    @Override
                    public void clickItem(@NotNull String url, int position, String unicode
                            , String date) {
                        posItem1 = position;
                        newDate = date;
                        unicode1 = unicode;
                        type = 2;

                        emojiAdapter.updateType(type);
                        setImageFromUrl(ivChoose1, url);
                        if (Common.INSTANCE.getSoundBool(MainActivity.this)) {
                            MediaManager.Companion.getGetInstance().playSound(MainActivity.this, "sound/select.mp3");
                        }
                        if (posItem1 != -1 && posItem2 != -1) {
                            binding.ivChoosePlayer1.clearAnimation();
                            binding.ivChoosePlayer2.clearAnimation();
                            binding.btnMerge.setBackgroundResource(R.drawable.img_buton);
                        } else {
                            binding.ivChoosePlayer1.clearAnimation();
                            loadAnimation(binding.ivChoosePlayer2);
                        }

                    }

                    @Override
                    public void clickItem2(@NotNull String url, int position, String unicode) {
                        if (Common.INSTANCE.getSoundBool(MainActivity.this)) {
                            MediaManager.Companion.getGetInstance().playSound(MainActivity.this, "sound/select.mp3");
                        }
                        binding.ivChoosePlayer2.clearAnimation();
                        setImageFromUrl(ivChoose2, url);
                        posItem2 = position;
                        unicode2 = unicode;
                        if (posItem1 != -1 && posItem2 != -1) {
                            binding.btnMerge.setBackgroundResource(R.drawable.img_buton);
                            binding.ivChoosePlayer1.clearAnimation();
                            binding.ivChoosePlayer2.clearAnimation();
                        }
                    }
                });
                emojisSlider1.setAdapter(emojiAdapter);
//                new Handler().postDelayed(() -> {
//                    for (int i = 0; i < 2; i++) {
//                        Random rand = new Random();
//                        int randomNum = rand.nextInt((supportedEmojisList.size()) - 1);
//                        if (i == 0) {
//                            emojisSlider1.smoothScrollToPosition(randomNum);
//                        } else {
//                        }
//                    }
//
//                    shouldShowEmoji(false);
//                    emote1 = Objects.requireNonNull(supportedEmojisList.get(getRecyclerCurrentItem(emojisSlider1, emojisSlider1SnapHelper, emojisSlider1LayoutManager)).get("emojiUnicode")).toString();
////                    emote2 = Objects.requireNonNull(supportedEmojisList.get(getRecyclerCurrentItem(emojisSlider2, emojisSlider2SnapHelper, emojisSlider2LayoutManager)).get("emojiUnicode")).toString();
////                    mixEmojis(emote1, emote2, Objects.requireNonNull(supportedEmojisList.get(getRecyclerCurrentItem(emojisSlider1, emojisSlider1SnapHelper, emojisSlider1LayoutManager)).get("date")).toString());
//                    registerViewPagersListener();
//                    isFineToUseListeners = true;
//                }, 1000);
            });
        });
    }

    public ArrayList<HashMap<String, Object>> filterByCategory(String category, ArrayList<HashMap<String, Object>> supportedEmojisList) {
        ArrayList<HashMap<String, Object>> filteredList = new ArrayList<>();

        for (HashMap<String, Object> emoji : supportedEmojisList) {
            if (emoji.containsKey("categories") && Objects.equals(emoji.get("categories"), category)) {
                filteredList.add(emoji);
            }
        }

        return filteredList;
    }

    private void registerViewPagersListener() {
        emojisSlider1.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (isFineToUseListeners && newState == SCROLL_STATE_IDLE) {
                    emote1 = Objects.requireNonNull(supportedEmojisList.get(getRecyclerCurrentItem(emojisSlider1, emojisSlider1SnapHelper, emojisSlider1LayoutManager)).get("emojiUnicode")).toString();
                    shouldShowEmoji(false);
//                    mixEmojis(emote1, emote2, Objects.requireNonNull(supportedEmojisList.get(getRecyclerCurrentItem(emojisSlider1, emojisSlider1SnapHelper, emojisSlider1LayoutManager)).get("date")).toString());
                }
            }
        });

//        emojisSlider2.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
//                if (isFineToUseListeners && newState == SCROLL_STATE_IDLE) {
//                    emote2 = Objects.requireNonNull(supportedEmojisList.get(getRecyclerCurrentItem(emojisSlider2, emojisSlider2SnapHelper, emojisSlider2LayoutManager)).get("emojiUnicode")).toString();
//                    shouldShowEmoji(false);
////                    mixEmojis(emote1, emote2, Objects.requireNonNull(supportedEmojisList.get(getRecyclerCurrentItem(emojisSlider2, emojisSlider2SnapHelper, emojisSlider2LayoutManager)).get("date")).toString());
//                }
//            }
//        });
    }


//    private void mixEmojis(String emoji1, String emoji2, String date) {
//        shouldEnableSave(false);
//        progressBar.setVisibility(View.VISIBLE);
//
//        EmojiMixer em = new EmojiMixer(emoji1, emoji2, date, this, new EmojiMixer.EmojiListener() {
//            @Override
//            public void onSuccess(String emojiUrl) {
//                finalEmojiURL = emojiUrl;
//                shouldEnableSave(true);
//                setImageFromUrl(mixedEmoji, emojiUrl);
//            }
//
//            @Override
//            public void onFailure(String failureReason) {
//                shouldEnableSave(false);
//                mixedEmoji.setImageResource(R.drawable.sad);
//                shouldShowEmoji(true);
//            }
//        });
//        Thread thread = new Thread(em);
//        thread.start();
//    }


    private void shouldShowEmoji(boolean shouldShow) {
//        isFineToUseListeners = true;
//        if (shouldShow) {
//            shadAnim(mixedEmoji, "scaleY", 1, 300);
//            shadAnim(mixedEmoji, "scaleX", 1, 300);
//            shadAnim(progressBar, "scaleY", 0, 300);
//            shadAnim(progressBar, "scaleX", 0, 300);
//        } else {
//            shadAnim(mixedEmoji, "scaleY", 0, 300);
//            shadAnim(mixedEmoji, "scaleX", 0, 300);
//            shadAnim(progressBar, "scaleY", 1, 300);
//            shadAnim(progressBar, "scaleX", 1, 300);
//        }
    }

    private void shouldEnableSave(boolean shouldShow) {

        if (shouldShow) {
            new Handler().postDelayed(() -> {
                //colorAnimator(saveEmoji, "#2A2B28", "#FF9D05", 250);
                //saveEmoji.setTextColor(Color.parseColor("#422B0D"));
                //saveEmoji.setIconTint(ColorStateList.valueOf(Color.parseColor("#422B0D")));
            }, 1000);
        } else {
            //colorAnimator(saveEmoji, "#FF9D05", "#2A2B28", 250);
            //saveEmoji.setTextColor(Color.parseColor("#A3A3A3"));
            //saveEmoji.setIconTint(ColorStateList.valueOf(Color.parseColor("#A3A3A3")));
        }
    }

    private void setImageFromUrl(ImageView image, String url) {
        Glide.with(this)
                .load(url)
                .fitCenter()
                .transition(DrawableTransitionOptions.withCrossFade())
                .listener(
                        new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                shouldShowEmoji(true);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                shouldShowEmoji(true);
                                return false;
                            }
                        }
                )
                .into(image);
    }


    private void downloadFile(String url) {
        Toast.makeText(this, "Download started, check notifications bar.", Toast.LENGTH_SHORT).show();
        String fileName = "MixedEmoji_" + new SimpleDateFormat("yyyy-MM-dd HH-mm-ss", Locale.US).format(new Date());
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                DownloadManager downloadmanager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                request.setMimeType(getContentResolver().getType(Uri.parse(url)));
                request.addRequestHeader("cookie", CookieManager.getInstance().getCookie(url));
                request.setTitle(fileName);
                request.setDescription(getString(R.string.downloading));
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "/MixedEmojis/" + fileName + URLUtil.guessFileName(url, "", ""));
                downloadmanager.enqueue(request);
            } catch (Exception e) {
                Log.e("Download error", e.toString());
            }
        });
    }
}
