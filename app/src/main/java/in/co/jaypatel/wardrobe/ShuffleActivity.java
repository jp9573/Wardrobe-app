package in.co.jaypatel.wardrobe;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;

public class ShuffleActivity extends AppCompatActivity {

    ImageView top, bottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shuffle);

        top = findViewById(R.id.imageButtonTop);
        bottom = findViewById(R.id.imageButtonBottom);

        String topCloth = getIntent().getStringExtra("top");
        String bottomCloth = getIntent().getStringExtra("bottom");

        File file = new File(topCloth);
        Picasso.get().load(file).placeholder(R.drawable.top_cloth).into(top);

        file = new File(bottomCloth);
        Picasso.get().load(file).placeholder(R.drawable.bottom_cloth).into(bottom);
    }
}
