package com.easy.edittextinrecyclerviewdemo;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LinearLayoutManager llm;
    private RecyclerView.Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView) findViewById(R.id.rv);
        recyclerView.setLayoutManager(llm=new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(new DividerItemDecoration(MainActivity.this,DividerItemDecoration.VERTICAL_LIST));
        recyclerView.setAdapter(adapter=new RecyclerView.Adapter() {
            //输入法
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            //edittext里的文字内容集合
            SparseArray<String> etTextAry = new SparseArray();
            //edittext的焦点位置
            int etFocusPos = -1;
            TextWatcher textWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    //每次修改文字后，保存在数据集合中
                    Log.e("tag","index="+etFocusPos+",save="+s.toString());
                    etTextAry.put(etFocusPos, s.toString());
                }
            };

            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View itemLayout = LayoutInflater.from(MainActivity.this).inflate(
                        R.layout.item_edit, parent, false);
                return new ItemHolder(itemLayout);
            }

            @Override
            public synchronized void onBindViewHolder(RecyclerView.ViewHolder holder, int i) {
                Log.e("tag","绑定Holder,index="+i);
                final int position = i;
                ItemHolder viewHolder = (ItemHolder) holder;
                viewHolder.tv.setText("item "+position);
                viewHolder.et.setText(etTextAry.get(position));
                viewHolder.et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View view, boolean b) {
                        if (b){
                            etFocusPos = position;
                            Log.e("tag","etFocusPos焦点选中-"+etFocusPos);
                        }
                    }
                });
            }

            @Override
            public int getItemCount() {
                return 50;
            }

            @Override
            public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
                super.onViewDetachedFromWindow(holder);
                Log.e("tag","隐藏item="+holder.getAdapterPosition());
                ItemHolder viewHolder = (ItemHolder) holder;
                viewHolder.et.removeTextChangedListener(textWatcher);
                viewHolder.et.clearFocus();
                if (etFocusPos == holder.getAdapterPosition()) {
                    inputMethodManager.hideSoftInputFromWindow(((ItemHolder) holder).et.getWindowToken(), 0);
                }
            }

            @Override
            public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
                super.onViewAttachedToWindow(holder);
                Log.e("tag","显示item="+holder.getAdapterPosition());
                ItemHolder viewHolder = (ItemHolder) holder;
                viewHolder.et.addTextChangedListener(textWatcher);
                if (etFocusPos == holder.getAdapterPosition()) {
                    viewHolder.et.requestFocus();
                    viewHolder.et.setSelection(viewHolder.et.getText().length());
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
