package com.easy.edittextinrecyclerviewdemo;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.rv);

        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(new DividerItemDecoration(MainActivity.this,DividerItemDecoration.VERTICAL_LIST));
        recyclerView.setItemViewCacheSize(0);
        recyclerView.setAdapter(new RecyclerView.Adapter() {

            //输入法
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            //edittext里的文字内容集合
            SparseArray<String> etTextAry = new SparseArray();
            TextWatcher textWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    etTextAry.put(etFocusPos, s.toString());
                }
            };
            //edittext的焦点位置
            int etFocusPos = -1;

            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View itemLayout = LayoutInflater.from(MainActivity.this).inflate(
                        R.layout.item_edit, parent, false);
                return new ItemHolder(itemLayout);
            }

            @Override
            public void onViewRecycled(RecyclerView.ViewHolder holder) {
                super.onViewRecycled(holder);
                /**
                 * 当前holder被销毁时，把holder的TextChangedListener删除
                 */
                ItemHolder viewHolder = (ItemHolder) holder;
                viewHolder.et.removeTextChangedListener(textWatcher);
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int i) {

                final int position = i;
                final ItemHolder viewHolder = (ItemHolder) holder;

                viewHolder.tv.setText("item "+position);
                viewHolder.et.setText(etTextAry.get(position));
                if (etFocusPos == position) {
                    viewHolder.et.requestFocus();
                    viewHolder.et.setSelection(viewHolder.et.getText().length());
                }
                viewHolder.et.addTextChangedListener(textWatcher);

                viewHolder.et.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (event.getAction() == MotionEvent.ACTION_UP) {
                            etFocusPos = position;
                        }
                        return false;
                    }
                });
            }

            @Override
            public int getItemCount() {
                return 40;
            }

            @Override
            public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
                super.onViewDetachedFromWindow(holder);
                if (etFocusPos == holder.getAdapterPosition()) {
                    inputMethodManager.hideSoftInputFromWindow(((ItemHolder) holder).et.getWindowToken(), 0);
                }
            }

            class ItemHolder extends RecyclerView.ViewHolder {

                private TextView tv;
                private EditText et;

                public ItemHolder(View itemView) {
                    super(itemView);
                    tv = (TextView) itemView.findViewById(R.id.tv);
                    et = (EditText) itemView.findViewById(R.id.et);
                }


            }
        });
    }
}
