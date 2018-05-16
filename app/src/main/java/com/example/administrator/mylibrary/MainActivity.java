package com.example.administrator.mylibrary;

import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.perfect.library.utils.math.MathManager;
import com.perfect.library.utils.system.AppUtils;
import com.perfect.library.utils.system.SystemUtils;
import com.perfect.library.utils.view.EditTextManager;

public class MainActivity extends AppCompatActivity {

    Handler handler = new Handler();
    private FloatingActionButton floating_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final TextView tv=findViewById(R.id.tv);
        EditText et=findViewById(R.id.et);
//        EditTextManager.getManager(et).setInputAllButEmoji();
//        String s= AppUtils.getPackageName(this);
//        et.setText(s);

        et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                    EditTextManager.getManager(tv).hideSoftInput();
            }
        });
        et.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditTextManager.getManager(tv).hideSoftInput();
            }
        });
//        String ss= (String) MathManager.create().setNumbers("0.6","0.4").operation(MathManager.ValueType.STRING).setDecimalNum(3).divide();
//        String ss=MathManager.create().setNumber("-1").setDecimalNum(2);
//        et.setText(ss);
//        final TextView text_view = (TextView) findViewById(R.id.text_view);
//        int width = ScreenUtils.getWindowWidth(this);
//        text_view.setWidth(width - 10);
//
//        final CoordinatorLayout coordinator_layout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);
//
//        RecyclerView recycler_view = (RecyclerView) findViewById(R.id.recycler_view);
//        floating_button = (FloatingActionButton) findViewById(R.id.floating_button);
//
//        MyTextView mytext_view= (MyTextView) findViewById(R.id.mytext_view);
//
//        mytext_view.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(MainActivity.this,"asd",Toast.LENGTH_SHORT).show();
//            }
//        });


//        text_view.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                if (floating_button.isShown()){
//                    floating_button.hide();
//                }else {
//                    floating_button.show();
//                }
//
//                handler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(MainActivity.this, "start", Toast.LENGTH_LONG).show();
//                    }
//                });
//
//                Snackbar snackbar = Snackbar.make(coordinator_layout, "test", Snackbar.LENGTH_LONG).setAction("asd", new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        Toast.makeText(MainActivity.this, "asd", Toast.LENGTH_LONG).show();
//                    }
//                }).setAction("test", new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        handler.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                Toast.makeText(MainActivity.this, "asd", Toast.LENGTH_LONG).show();
//                            }
//                        });
//                    }
//                });
//                snackbar.setCallback(new Snackbar.Callback() {
//                    @Override
//                    public void onDismissed(Snackbar snackbar, int event) {
//                        super.onDismissed(snackbar, event);
//                    }
//
//                    @Override
//                    public void onShown(Snackbar snackbar) {
//                        super.onShown(snackbar);
//                    }
//                });
//                snackbar.show();
//            }
//        });


//        final TextInputEditText mTextInputEditText= (TextInputEditText) findViewById(R.id.mTextInputEditText);
//
//        mTextInputEditText.setError("asd");
//
//        final TextInputLayout mTextInputLayout= (TextInputLayout) findViewById(R.id.mTextInputLayout);
//        final EditText mEditText= (EditText) findViewById(R.id.mEditText);
//
//        mTextInputLayout.setError("test");
//        mTextInputLayout.setErrorEnabled(true);
//
//        text_view.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (mEditText.getText().toString().length()==0){
//
//                    mTextInputEditText.setError("asd");
//                    mTextInputLayout.setErrorEnabled(true);
//
//                }else {
//                    mTextInputEditText.setError("");
//                    mTextInputLayout.setErrorEnabled(false);
//                }
//            }
//        });


//        FragmentManager manager=getSupportFragmentManager();
//        FragmentTransaction transaction=manager.beginTransaction();
//        transaction.replace(R.id.fragment_layout,new ViewPagerFragment());
//        transaction.commit();

//        Bitmap bitmap;
//        ViewPagerFragment fragment=new ViewPagerFragment();
//
//        Log.d("asd","memory:"+memory);
//        Log.d("asd","largememory:"+largememory);
    }
}
