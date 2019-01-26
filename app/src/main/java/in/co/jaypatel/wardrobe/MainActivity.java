package in.co.jaypatel.wardrobe;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private TopPagerAdapter mTopPagerAdapter;
    private BottomPagerAdapter mBottomPagerAdapter;
    private ViewPager mTopContainer, mBottomContainer;
    public static int numberOfTopSlides = 1, numberOfBottomSlides = 1;
    public static HashMap<Integer,Dress> topDressMap = new HashMap<>();
    public static HashMap<Integer,Dress> bottomDressMap = new HashMap<>();
    public static int currentSlideNumber = 0;
    public static Context context;
    FloatingActionButton fabShuffle;
    public static int topCurrentSlide;
    public static int bottomCurrentSlide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        context = this;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 1);
        }

        mTopContainer = findViewById(R.id.topContainer);
        mBottomContainer = findViewById(R.id.bottomContainer);

        mTopPagerAdapter = new TopPagerAdapter(getSupportFragmentManager());
        mTopContainer.setAdapter(mTopPagerAdapter);

        mBottomPagerAdapter = new BottomPagerAdapter(getSupportFragmentManager());
        mBottomContainer.setAdapter(mBottomPagerAdapter);

        fabShuffle = findViewById(R.id.fabShuffle);

        if(savedInstanceState != null) {
            topDressMap = (HashMap<Integer, Dress>) savedInstanceState.getSerializable("topDressMap");
            bottomDressMap = (HashMap<Integer, Dress>) savedInstanceState.getSerializable("bottomDressMap");
            savedInstanceState.remove("topDressMap");
            currentSlideNumber = savedInstanceState.getInt("currentSlideNumber");
            numberOfTopSlides = savedInstanceState.getInt("numberOfTopSlides");
            numberOfBottomSlides = savedInstanceState.getInt("numberOfBottomSlides");

            mTopPagerAdapter.notifyDataSetChanged();
            mTopContainer.setCurrentItem(currentSlideNumber+1);
        }

        fabShuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(topDressMap.size() > 1 && bottomDressMap.size() > 1) {
                    List<String> top = new ArrayList<>();
                    List<String> bottom = new ArrayList<>();

                    for (Map.Entry<Integer, Dress> entry : topDressMap.entrySet()) {
                        Dress dress = entry.getValue();
                        top.add(dress.getCloth());
                    }

                    for (Map.Entry<Integer, Dress> entry : bottomDressMap.entrySet()) {
                        Dress dress = entry.getValue();
                        bottom.add(dress.getCloth());
                    }

                    Random random = new Random();
                    int topCloth = getRandomItem(random, top.size());
                    int bottomCloth = getRandomItem(random, bottom.size());

                    mTopContainer.setCurrentItem(topCloth);
                    mBottomContainer.setCurrentItem(bottomCloth);

                }else if (topDressMap.size() == 1 || bottomDressMap.size() == 1) {
                    Toast.makeText(MainActivity.this, "To get random combination you need to add more images", Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(MainActivity.this, "Please add some images to get random combination!", Toast.LENGTH_LONG).show();
                }
            }
        });

        mTopContainer.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                topCurrentSlide = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mBottomContainer.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                bottomCurrentSlide = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("topDressMap", topDressMap);
        outState.putSerializable("bottomDressMap", bottomDressMap);
        outState.putInt("currentSlideNumber",currentSlideNumber);
        outState.putInt("numberOfTopSlides", numberOfTopSlides);
        outState.putInt("numberOfBottomSlides", numberOfBottomSlides);
        super.onSaveInstanceState(outState);
    }

    public class TopPagerAdapter extends FragmentStatePagerAdapter {
        private Observable mObservers = new FragmentObserver();

        public TopPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getItemPosition(Object object){
            return PagerAdapter.POSITION_NONE;
        }

        @Override
        public Fragment getItem(int position) {
            mObservers.deleteObservers();

            TopClothFragment slideFragment = TopClothFragment.newInstance(position + 1);

            if(slideFragment instanceof Observer)
                mObservers.addObserver(slideFragment);

            return slideFragment;
        }

        @Override
        public int getCount() {
            return numberOfTopSlides;
        }

    }

    public class BottomPagerAdapter extends FragmentStatePagerAdapter {
        private Observable mObservers = new FragmentObserver();

        public BottomPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getItemPosition(Object object){
            return PagerAdapter.POSITION_NONE;
        }

        @Override
        public Fragment getItem(int position) {
            mObservers.deleteObservers();

            BottomClothFragment slideFragment = BottomClothFragment.newInstance(position + 1);

            if(slideFragment instanceof Observer) {
                mObservers.addObserver(slideFragment);
            }
            return slideFragment;
        }

        @Override
        public int getCount() {
            return numberOfBottomSlides;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 1){
            if(!(grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED))
                Toast.makeText(this,"You must have to give the storage permission",Toast.LENGTH_LONG).show();
        }
    }

    public class FragmentObserver extends Observable {
        @Override
        public void notifyObservers() {
            setChanged();
            super.notifyObservers();
        }
    }

    int getRandomItem(Random random, int listSize) {
        int index = random.nextInt(listSize);
        return index;
    }

    public void addNewTopSlide(View view) {
        numberOfTopSlides++;
        mTopPagerAdapter.notifyDataSetChanged();
        mTopContainer.setCurrentItem(numberOfTopSlides -1);
    }

    public void addNewBottomSlide(View view) {
        numberOfBottomSlides++;
        mBottomPagerAdapter.notifyDataSetChanged();
        mBottomContainer.setCurrentItem(numberOfBottomSlides -1);
    }

}
